package mist.mystralix.domain.reminder;

public class Reminder {

    private String reminderUUID;

    private String userDiscordID;

    private int reminderID;

    private String message;

    private long targetTimestamp;

    private boolean notificationSent;

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

    public Reminder(
            String reminderUUID,
            String userDiscordID,
            String message,
            long targetTimestamp,
            boolean notificationSent
    ) {
        this(reminderUUID, userDiscordID, 0, message, targetTimestamp, notificationSent);
    }

    public String getReminderUUID() {
        return reminderUUID;
    }

    public String getUserDiscordID() {
        return userDiscordID;
    }

    public int getReminderID() {
        return reminderID;
    }

    public String getMessage() {
        return message;
    }

    public long getTargetTimestamp() {
        return targetTimestamp;
    }

    public boolean isNotificationSent() {
        return notificationSent;
    }

    public void setReminderUUID(String reminderUUID) {
        this.reminderUUID = reminderUUID;
    }

    public void setUserDiscordID(String userDiscordID) {
        this.userDiscordID = userDiscordID;
    }

    public void setReminderID(int reminderID) {
        this.reminderID = reminderID;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTargetTimestamp(long targetTimestamp) {
        this.targetTimestamp = targetTimestamp;
    }

    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }
}