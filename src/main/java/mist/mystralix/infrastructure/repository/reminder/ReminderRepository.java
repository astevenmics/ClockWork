package mist.mystralix.infrastructure.repository.reminder;

import mist.mystralix.infrastructure.repository.base.BaseRepository;
import mist.mystralix.domain.reminder.Reminder;

import java.util.HashSet;

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
public interface ReminderRepository extends BaseRepository<Reminder> {

    /**
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
