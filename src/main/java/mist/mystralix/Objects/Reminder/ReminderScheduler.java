package mist.mystralix.Objects.Reminder;

import mist.mystralix.Database.DBReminderHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderScheduler {

    private final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
    private final DBReminderHandler DB_REMINDER_HANDLER = new DBReminderHandler();

    public void scheduleReminders(JDA jda) {
        HashSet<Reminder> allReminders = DB_REMINDER_HANDLER.getAllReminders();
        ReminderHandler reminderHandler = new ReminderHandler();

        for (Reminder reminder : allReminders) {
            long reminderTargetTimestamp = reminder.targetTimestamp;
            long remainingTime = reminderTargetTimestamp - System.currentTimeMillis();

            String userDiscordID = reminder.userDiscordID;

            if (remainingTime <= 0) {
                jda.retrieveUserById(userDiscordID).queue(user ->
                                reminderHandler.sendReminder(
                                        user,
                                        reminder
                                ),
                        error -> System.out.println(error.getMessage()));
            } else {
                SCHEDULER.schedule(() ->
                    jda.retrieveUserById(userDiscordID).queue(user ->
                            reminderHandler.sendReminder(user, reminder),
                            error -> System.out.println(error.getMessage())
                    ),
                    remainingTime,
                    TimeUnit.MILLISECONDS);
            }
        }
    }

    public void scheduleReminder(User user, Reminder reminder) {
        ReminderHandler reminderHandler = new ReminderHandler();

        long reminderTargetTimestamp = reminder.targetTimestamp;
        long remainingTime = reminderTargetTimestamp - System.currentTimeMillis();
                SCHEDULER.schedule(() ->
                        reminderHandler.sendReminder(user, reminder),
                        remainingTime,
                        TimeUnit.MILLISECONDS
                );
    }



}