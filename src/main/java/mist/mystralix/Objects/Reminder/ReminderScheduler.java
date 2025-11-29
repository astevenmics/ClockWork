package mist.mystralix.Objects.Reminder;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderScheduler {

    private final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
    private final ReminderService REMINDER_SERVICE;

    public ReminderScheduler(ReminderService reminderService) {
        this.REMINDER_SERVICE = reminderService;
    }

    public void scheduleReminders(JDA jda) {
        HashSet<Reminder> allReminders = REMINDER_SERVICE.getAllActiveReminders();

        for (Reminder reminder : allReminders) {
            long reminderTargetTimestamp = reminder.targetTimestamp;
            long remainingTime = reminderTargetTimestamp - System.currentTimeMillis();

            String userDiscordID = reminder.userDiscordID;

            if (remainingTime <= 0) {
                jda.retrieveUserById(userDiscordID).queue(user -> {
                    REMINDER_SERVICE.sendReminder(user, reminder);
                    REMINDER_SERVICE.reminderSentUpdate(reminder);
                        }, error -> System.out.println(error.getMessage())
                );
            } else {
                SCHEDULER.schedule(() ->
                    jda.retrieveUserById(userDiscordID).queue(user -> {
                        REMINDER_SERVICE.sendReminder(user, reminder);
                        REMINDER_SERVICE.reminderSentUpdate(reminder);
                            }, error -> System.out.println(error.getMessage())
                    ),
                    remainingTime,
                    TimeUnit.MILLISECONDS);
            }
        }
    }

    public void scheduleReminder(User user, Reminder reminder) {

        long reminderTargetTimestamp = reminder.targetTimestamp;
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