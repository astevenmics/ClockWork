package mist.mystralix.domain.reminder;

public class Reminder {

    private String uuid;

    private String userDiscordID;

    private int id;

    private String message;

    private long createdTimestamp;

    private long targetTimestamp;

    private boolean notificationSent;

    public Reminder(
            String uuid,
            String userDiscordID,
            int id,
            String message,
            long createdTimestamp,
            long targetTimestamp,
            boolean notificationSent
    ) {
        this.uuid = uuid;
        this.userDiscordID = userDiscordID;
        this.id = id;
        this.message = message;
        this.createdTimestamp = createdTimestamp;
        this.targetTimestamp = targetTimestamp;
        this.notificationSent = notificationSent;
    }

    public Reminder(
            String uuid,
            String userDiscordID,
            String message,
            long createdTimestamp,
            long targetTimestamp,
            boolean notificationSent
    ) {
        this(uuid, userDiscordID, 0, message, createdTimestamp, targetTimestamp, notificationSent);
    }

    public String getUUID() {
        return uuid;
    }

    public String getUserDiscordID() {
        return userDiscordID;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public long getTargetTimestamp() {
        return targetTimestamp;
    }

    public boolean isNotificationSent() {
        return notificationSent;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public void setUserDiscordID(String userDiscordID) {
        this.userDiscordID = userDiscordID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public void setTargetTimestamp(long targetTimestamp) {
        this.targetTimestamp = targetTimestamp;
    }

    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }
}