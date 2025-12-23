package mist.mystralix.application.reminder;

import mist.mystralix.domain.reminder.Reminder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.*;

public class ReminderScheduler {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    private static final Map<String, ScheduledFuture<?>> SCHEDULED_REMINDERS = new ConcurrentHashMap<>();

    private final ReminderService REMINDER_SERVICE;

    private static volatile ReminderScheduler instance;

    public ReminderScheduler(ReminderService reminderService) {
        this.REMINDER_SERVICE = reminderService;
    }

    public static ReminderScheduler getInstance(ReminderService reminderService) {
        if (instance == null) {
            synchronized (ReminderScheduler.class) {
                if (instance == null) {
                    instance = new ReminderScheduler(reminderService);
                }
            }
        }
        return instance;
    }

    public static ReminderScheduler getInstance() {
        return instance;
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
                ScheduledFuture<?> scheduledReminder = SCHEDULER.schedule(() ->
                                jda.retrieveUserById(userDiscordID).queue(user -> {
                                    REMINDER_SERVICE.sendReminder(user, reminder);
                                    REMINDER_SERVICE.reminderSentUpdate(reminder);
                                    SCHEDULED_REMINDERS.remove(reminder.getUUID());
                                }, error -> System.out.println(error.getMessage())),
                        remainingTime,
                        TimeUnit.MILLISECONDS
                );
                SCHEDULED_REMINDERS.put(reminder.getUUID(), scheduledReminder);
            }
        }
    }

    public void scheduleReminder(User user, Reminder reminder) {

        long reminderTargetTimestamp = reminder.getTargetTimestamp();
        long remainingTime = reminderTargetTimestamp - System.currentTimeMillis();

        ScheduledFuture<?> reminderScheduled = SCHEDULER.schedule(() -> {
                    REMINDER_SERVICE.sendReminder(user, reminder);
                    REMINDER_SERVICE.reminderSentUpdate(reminder);
                    SCHEDULED_REMINDERS.remove(reminder.getUUID());
                },
                remainingTime,
                TimeUnit.MILLISECONDS
        );

        // SCHEDULED_REMINDERS.put returns a value if the key already existed, returns null if not
        ScheduledFuture<?> scheduledReminder = SCHEDULED_REMINDERS.put(reminder.getUUID(), reminderScheduled);
        if (scheduledReminder != null) {
            scheduledReminder.cancel(true);
        }
    }

    public void cancelReminder(String reminderUUID) {
        ScheduledFuture<?> scheduledReminder = SCHEDULED_REMINDERS.get(reminderUUID);
        if (scheduledReminder != null) {
            scheduledReminder.cancel(true);
            SCHEDULED_REMINDERS.remove(reminderUUID);
        }
    }

    public void shutdown() {
        SCHEDULER.shutdown();
        try {
            boolean terminationSuccess = SCHEDULER.awaitTermination(1, TimeUnit.MINUTES);
            if (terminationSuccess) {
                System.out.println("ReminderScheduler has been shut down!");
            } else {
                System.out.println("ReminderScheduler shutdown timed out.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("ReminderScheduler shutdown was interrupted.");
        }
    }

}