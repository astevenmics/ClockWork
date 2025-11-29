package mist.mystralix.Database.Reminder;

import mist.mystralix.Objects.Reminder.Reminder;

import java.util.HashSet;
import java.util.List;

/**
 * Repository interface defining all database operations related to reminders.
 *
 * <p>This abstraction allows the application to remain independent of the
 * underlying persistence layer (MySQL, SQLite, in-memory, etc.). Implementations
 * such as {@code DBReminderRepository} are responsible for handling actual
 * storage logic.</p>
 *
 * <p>The methods follow standard CRUD conventions and include additional
 * operations used by the reminder scheduler (active reminder scanning).</p>
 */
public interface ReminderRepository {

    /**
     * Persists a new reminder into the data store.
     *
     * @param reminder the reminder to store
     */
    void create(Reminder reminder);

    /**
     * Retrieves a reminder using a user ID and numeric reminder ID.
     *
     * @param userDiscordID the Discord ID of the reminder owner
     * @param reminderID    the numeric reminder ID
     * @return the matching reminder, or {@code null} if none is found
     */
    Reminder read(String userDiscordID, int reminderID);

    /**
     * Retrieves a reminder using a user ID and a UUID string.
     *
     * @param userDiscordID the Discord ID of the reminder owner
     * @param reminderUUID  the unique UUID identifier of the reminder
     * @return the matching reminder, or {@code null} if none is found
     */
    Reminder read(String userDiscordID, String reminderUUID);

    /**
     * Updates an existing reminder.
     *
     * <p>Implementations should identify the record using the reminder's
     * internal UUID or ID values.</p>
     *
     * @param reminder the reminder containing updated data
     */
    void update(Reminder reminder);

    /**
     * Deletes a reminder using its UUID and the owner's user ID.
     *
     * @param reminderUUID   the unique UUID identifier of the reminder
     * @param userDiscordID  the owner of the reminder
     */
    void delete(String reminderUUID, String userDiscordID);

    /**
     * Retrieves all reminders belonging to a specific user.
     *
     * @param userDiscordID the Discord ID of the reminder owner
     * @return a list of reminders; never {@code null}
     */
    List<Reminder> readAll(String userDiscordID);

    /**
     * Retrieves all reminders in the system that are still active
     * (i.e., not yet sent by the scheduler).
     *
     * <p>Used exclusively by {@code ReminderScheduler} to determine which
     * reminders need to be fired.</p>
     *
     * @return a set of active reminders
     */
    HashSet<Reminder> getAllActiveReminders();

    /**
     * Updates the notification flag for a reminder, typically after it has been sent.
     *
     * @param reminder the reminder whose notification flag should be updated
     */
    void updateIsNotificationSent(Reminder reminder);

}
