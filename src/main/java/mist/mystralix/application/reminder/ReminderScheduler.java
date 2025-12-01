package mist.mystralix.application.reminder;

import mist.mystralix.domain.reminder.Reminder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Schedules reminder notifications based on their target timestamps.
 *
 * <p>This class uses a {@link ScheduledExecutorService} to delay the execution
 * of reminder alerts until the correct moment. When a reminder is due, the
 * scheduler retrieves the corresponding Discord user and triggers the
 * {@link ReminderService} to send a DM.</p>
 *
 * <p>The scheduler supports:
 * <ul>
 *     <li>Boot-time loading of all pending reminders</li>
 *     <li>Scheduling a single newly-created reminder</li>
 *     <li>Immediate execution of overdue reminders</li>
 * </ul>
 * </p>
 */
public class ReminderScheduler {

    /** Dedicated thread pool for scheduling reminder execution. */
    private final ScheduledExecutorService SCHEDULER =
            Executors.newScheduledThreadPool(1);

    /** The service responsible for business logic and DB operations. */
    private final ReminderService REMINDER_SERVICE;

    /**
     * Constructs a ReminderScheduler with the required service dependency.
     *
     * @param reminderService service used for sending reminders and updating DB state
     */
    public ReminderScheduler(ReminderService reminderService) {
        this.REMINDER_SERVICE = reminderService;
    }

    /**
     * Schedules all active reminders stored in the database.
     *
     * <p>This method is typically called once at bot startup. It:
     * <ol>
     *     <li>Loads all reminders that are not yet marked as "sent"</li>
     *     <li>Calculates the time until they should fire</li>
     *     <li>Either executes the reminder immediately (if overdue) or schedules it</li>
     * </ol>
     *
     * @param jda the active JDA instance used for retrieving users and sending DMs
     */
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

    /**
     * Schedules a single reminder (typically used after a reminder is newly created).
     *
     * @param user     the user who should receive the reminder
     * @param reminder the reminder data to schedule
     */
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
