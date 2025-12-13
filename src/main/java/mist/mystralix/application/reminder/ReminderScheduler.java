package mist.mystralix.application.reminder;

import mist.mystralix.domain.reminder.Reminder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderScheduler {

    private final ScheduledExecutorService SCHEDULER =
            Executors.newScheduledThreadPool(1);

    private final ReminderService REMINDER_SERVICE;

    public ReminderScheduler(ReminderService reminderService) {
        this.REMINDER_SERVICE = reminderService;
    }

    // TODO: update to cancel when updated and re-schedule
    public void scheduleReminders(JDA jda) {
        HashSet<Reminder> allReminders = REMINDER_SERVICE.getAllActiveReminders();

        for (Reminder reminder : allReminders) {

            long reminderTargetTimestamp = reminder.getTargetTimestamp();
            long remainingTime = reminderTargetTimestamp - System.currentTimeMillis();
            String userDiscordID = reminder.getUserDiscordID();

            // If the reminder time has already passed, send it immediately
            if (remainingTime <= 0) {
                jda.retrieveUserById(userDiscordID).queue(user -> {
                    REMINDER_SERVICE.sendReminder(user, reminder);
                    REMINDER_SERVICE.reminderSentUpdate(reminder);
                }, error -> System.out.println(error.getMessage()));

            } else {
                // Otherwise schedule the reminder to execute in the future
                SCHEDULER.schedule(() ->
                                jda.retrieveUserById(userDiscordID).queue(user -> {
                                    REMINDER_SERVICE.sendReminder(user, reminder);
                                    REMINDER_SERVICE.reminderSentUpdate(reminder);
                                }, error -> System.out.println(error.getMessage())),
                        remainingTime,
                        TimeUnit.MILLISECONDS
                );
            }
        }
    }

    // TODO: update to cancel when updated and re-schedule
    public void scheduleReminder(User user, Reminder reminder) {

        long reminderTargetTimestamp = reminder.getTargetTimestamp();
        long remainingTime = reminderTargetTimestamp - System.currentTimeMillis();

        SCHEDULER.schedule(() -> {
                    REMINDER_SERVICE.sendReminder(user, reminder);
                    REMINDER_SERVICE.reminderSentUpdate(reminder);
                },
                remainingTime,
                TimeUnit.MILLISECONDS
        );
    }
}