package mist.mystralix.Objects.Reminder;

/**
 * Represents a scheduled reminder created by a Discord user.
 *
 * <p>This model contains all metadata associated with a reminder:
 * <ul>
 *     <li>UUID identifier stored in the database</li>
 *     <li>Discord user ID of the creator</li>
 *     <li>User-friendly numerical reminder ID</li>
 *     <li>Reminder message text</li>
 *     <li>Execution timestamp in milliseconds</li>
 *     <li>Flag that indicates whether notification was delivered</li>
 * </ul>
 *
 * <p>This class is mutable (unlike immutable DTOs), because reminders often have
 * fields updated after creation (e.g., incremented reminderID or status changes).</p>
 */
public class Reminder {

    /** Unique UUID for persistent identification of the reminder. */
    private String reminderUUID;

    /** Discord user ID of the reminder owner. */
    private String userDiscordID;

    /** User-friendly numeric reminder ID (0 if not assigned yet). */
    private int reminderID;

    /** Reminder message content. */
    private String message;

    /** Timestamp (ms) when this reminder should trigger. */
    private long targetTimestamp;

    /** Whether the notification was already sent. */
    private boolean notificationSent;

    /**
     * Full constructor used when all reminder properties are known.
     *
     * @param reminderUUID      unique UUID for this reminder
     * @param userDiscordID     Discord user ID of the owner
     * @param reminderID        numeric reminder ID (0 if not yet assigned)
     * @param message           reminder message text
     * @param targetTimestamp   trigger time in milliseconds
     * @param notificationSent  whether delivery already occurred
     */
    public Reminder(
            String reminderUUID,
            String userDiscordID,
            int reminderID,
            String message,
            long targetTimestamp,
            boolean notificationSent
    ) {
        this.reminderUUID = reminderUUID;
        this.userDiscordID = userDiscordID;
        this.reminderID = reminderID;
        this.message = message;
        this.targetTimestamp = targetTimestamp;
        this.notificationSent = notificationSent;
    }

    /**
     * Constructor used when reminderID is not known yet (common for new reminders).
     *
     * @param reminderUUID      UUID for this reminder
     * @param userDiscordID     owner’s Discord ID
     * @param message           reminder message text
     * @param targetTimestamp   trigger time in milliseconds
     * @param notificationSent  whether the reminder has already fired
     */
    public Reminder(
            String reminderUUID,
            String userDiscordID,
            String message,
            long targetTimestamp,
            boolean notificationSent
    ) {
        this(reminderUUID, userDiscordID, 0, message, targetTimestamp, notificationSent);
    }

    // -------------------------------------------------------------------------
    // Getter Methods
    // -------------------------------------------------------------------------

    /** @return the reminder's UUID */
    public String getReminderUUID() {
        return reminderUUID;
    }

    /** @return the owner's Discord ID */
    public String getUserDiscordID() {
        return userDiscordID;
    }

    /** @return the numeric reminder ID */
    public int getReminderID() {
        return reminderID;
    }

    /** @return the reminder message text */
    public String getMessage() {
        return message;
    }

    /** @return the time (ms) when this reminder should fire */
    public long getTargetTimestamp() {
        return targetTimestamp;
    }

    /** @return whether the reminder notification was already sent */
    public boolean isNotificationSent() {
        return notificationSent;
    }

    // -------------------------------------------------------------------------
    // Setter Methods
    // -------------------------------------------------------------------------

    /**
     * Sets the UUID for this reminder.
     *
     * <p>Not commonly used but allows reassigning UUIDs when cloning data.</p>
     */
    public void setReminderUUID(String reminderUUID) {
        this.reminderUUID = reminderUUID;
    }

    /** Sets the Discord owner ID. */
    public void setUserDiscordID(String userDiscordID) {
        this.userDiscordID = userDiscordID;
    }

    /** Sets the user-friendly numeric reminder ID. */
    public void setReminderID(int reminderID) {
        this.reminderID = reminderID;
    }

    /** Sets the reminder message text. */
    public void setMessage(String message) {
        this.message = message;
    }

    /** Updates the scheduled trigger timestamp. */
    public void setTargetTimestamp(long targetTimestamp) {
        this.targetTimestamp = targetTimestamp;
    }

    /** Marks whether the notification has been sent. */
    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }
}
