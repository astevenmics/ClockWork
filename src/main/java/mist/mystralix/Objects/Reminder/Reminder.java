package mist.mystralix.Objects.Reminder;

// TODO: Add Comments
public class Reminder {

    public String reminderUUID;
    public String userDiscordID;
    public int reminderID;
    public String message;
    public long targetTimestamp;
    public boolean isNotificationSent;

    public Reminder(
            String reminderUUID,
            String userDiscordID,
            int reminderID,
            String message,
            long targetTimestamp,
            boolean isNotificationSent
    ) {
        this.reminderUUID = reminderUUID;
        this.userDiscordID = userDiscordID;
        this.reminderID = reminderID;
        this.message = message;
        this.targetTimestamp = targetTimestamp;
        this.isNotificationSent = isNotificationSent;
    }

    public Reminder(
            String reminderUUID,
            String userDiscordID,
            String message,
            long targetTimestamp,
            boolean isNotificationSent
    ) {
        this.reminderUUID = reminderUUID;
        this.userDiscordID = userDiscordID;
        this.message = message;
        this.targetTimestamp = targetTimestamp;
        this.isNotificationSent = isNotificationSent;
    }

}