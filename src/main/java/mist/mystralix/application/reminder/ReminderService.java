package mist.mystralix.application.reminder;

import mist.mystralix.infrastructure.repository.reminder.ReminderRepository;
import mist.mystralix.presentation.embeds.ReminderEmbed;
import mist.mystralix.utils.IdentifiableFetcher;
import mist.mystralix.domain.reminder.Reminder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.List;

/**
 * Service layer responsible for all business logic related to Reminder objects.
 *
 * <p>This class sits between the command layer and the repository layer, providing:
 * <ul>
 *     <li>Object construction and validation</li>
 *     <li>Database read/write operations through {@link ReminderRepository}</li>
 *     <li>DM sending utilities for reminder alerts</li>
 *     <li>Convenience helpers used by the scheduler</li>
 * </ul>
 *
 * <p>Following a clean architecture pattern, this class contains no SQL or
 * persistence details—those are delegated entirely to the injected repository.</p>
 */
public class ReminderService implements IdentifiableFetcher<Reminder> {

    /** Repository responsible for database operations for reminders. */
    private final ReminderRepository REMINDER_REPOSITORY;

    /**
     * Constructs the ReminderService with an injected repository instance.
     *
     * @param reminderRepository the repository used to persist and retrieve reminders
     */
    public ReminderService(ReminderRepository reminderRepository) {
        this.REMINDER_REPOSITORY = reminderRepository;
    }

    /**
     * Creates a new reminder and stores it in the database.
     *
     * @param reminderUUID        UUID string uniquely identifying the reminder
     * @param userDiscordID       the Discord ID of the user creating the reminder
     * @param reminderMessage     the message content of the reminder
     * @param targetTimestamp     the timestamp when the reminder should trigger (ms)
     * @param isNotificationSent  whether the notification has already been sent
     */
    public void createReminder(
            String reminderUUID,
            String userDiscordID,
            String reminderMessage,
            long targetTimestamp,
            boolean isNotificationSent
    ) {
        REMINDER_REPOSITORY.create(
                new Reminder(
                        reminderUUID,
                        userDiscordID,
                        reminderMessage,
                        targetTimestamp,
                        isNotificationSent
                )
        );
    }

    /**
     * Retrieves a reminder using a user ID and numeric reminder ID.
     *
     * @param userDiscordID the Discord user who owns the reminder
     * @param reminderID     numeric reminder ID assigned by the database
     * @return the matching reminder, or {@code null} if none exists
     */
    public Reminder getUserReminder(
            String userDiscordID,
            int reminderID
    ) {
        return REMINDER_REPOSITORY.findByID(userDiscordID, reminderID);
    }

    /**
     * Retrieves a reminder using a user ID and reminder UUID.
     *
     * @param userDiscordID the owner of the reminder
     * @param reminderUUID  UUID string uniquely identifying the reminder
     * @return the matching reminder, or {@code null} if none exists
     */
    public Reminder getUserReminder(
            String userDiscordID,
            String reminderUUID
    ) {
        return REMINDER_REPOSITORY.findByUUID(userDiscordID, reminderUUID);
    }

    /**
     * Retrieves all reminders owned by a given Discord user.
     *
     * @param userDiscordID the owner's Discord ID
     * @return a list of reminders; never {@code null}
     */
    public List<Reminder> getAllUserReminders(String userDiscordID) {
        return REMINDER_REPOSITORY.readAll(userDiscordID);
    }

    /**
     * Updates an existing reminder with new data.
     *
     * @param updatedReminder the reminder object containing updated fields
     */
    public void updateUserReminder(Reminder updatedReminder) {
        REMINDER_REPOSITORY.update(updatedReminder);
    }

    /**
     * Deletes a reminder using its internal UUID and owner ID.
     *
     * @param reminder the reminder to delete
     */
    public void delete(Reminder reminder) {
        REMINDER_REPOSITORY.delete(reminder);
    }

    /**
     * Returns all reminders system-wide that:
     * <ul>
     *     <li>are not yet sent</li>
     *     <li>have a scheduled timestamp</li>
     * </ul>
     *
     * <p>Used exclusively by the {@code ReminderScheduler} to determine which
     * reminders need to be executed.</p>
     *
     * @return a set of active reminders
     */
    public HashSet<Reminder> getAllActiveReminders() {
        return REMINDER_REPOSITORY.getAllActiveReminders();
    }

    /**
     * Sends a reminder notification to a user through Discord DMs.
     *
     * <p>This method creates a reminder embed, opens a private user channel, and sends
     * the message asynchronously using JDA's queue system. The method prints
     * debug logs for success or failure cases (DMs blocked, bot blocked, etc.).</p>
     *
     * @param user      the Discord user receiving the notification
     * @param reminder  the reminder associated with this notification
     */
    public void sendReminder(User user, Reminder reminder) {

        ReminderEmbed reminderEmbed = new ReminderEmbed();

        // Safety check to avoid null pointer issues
        if (user == null) {
            System.out.println("Error: User not found! | " + reminder.getUserDiscordID());
            return;
        }

        // Build the embed payload
        MessageEmbed embed = reminderEmbed.createMessageEmbed(
                user,
                "Reminder Alert!",
                reminder
        );

        // Open DM channel and send the embed asynchronously
        user.openPrivateChannel().queue(
                channel -> channel.sendMessageEmbeds(embed).queue(
                        _ -> System.out.println("✅ Reminder sent to " + user.getEffectiveName()),
                        fail -> System.out.println("❌ Failed to send to " + user.getEffectiveName() +
                                " (DM blocked): " + fail.getMessage())
                ),
                _ -> System.out.println(
                        "❌ Cannot open DM with " + user.getEffectiveName() +
                                " — DMs are off or the user blocked the bot."
                )
        );
    }

    /**
     * Marks a reminder as "notification sent" in the database.
     *
     * <p>Used by the scheduler to avoid duplicate message delivery.</p>
     *
     * @param reminder the reminder to update
     */
    public void reminderSentUpdate(Reminder reminder) {
        REMINDER_REPOSITORY.updateIsNotificationSent(reminder);
    }


    @Override
    public Reminder fetchByUserIDAndObjectID(String userDiscordId, int taskId) {
        return getUserReminder(userDiscordId, taskId);
    }

}
