package mist.mystralix.domain.reminder;

import mist.mystralix.infrastructure.exception.ReminderOperationException;

import java.util.Objects;

public class Reminder {

    private final String uuid;

    private final String userDiscordID;

    // ID value set by DB
    private final Integer id;

    private String message;

    private final long createdTimestamp;

    private long targetTimestamp;

    private boolean notificationSent;

    private Reminder(Builder builder) {
        this.uuid = builder.uuid;
        this.userDiscordID = builder.userDiscordID;
        this.id = builder.id;
        this.message = builder.message;
        this.createdTimestamp = builder.createdTimestamp;
        this.targetTimestamp = builder.targetTimestamp;
        this.notificationSent = builder.notificationSent;
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

    public void setMessage(String message) {
        if (message == null || message.isEmpty()) {
            throw new ReminderOperationException("Message cannot be null or empty");
        }
        this.message = message;
    }

    public void setTargetTimestamp(long targetTimestamp) {
        if (targetTimestamp < this.createdTimestamp) {
            throw new ReminderOperationException("Target timestamp cannot be a past timestamp");
        }
        this.targetTimestamp = targetTimestamp;
    }

    public void toggleNotification() {
        this.notificationSent = !notificationSent;
    }

    public static class Builder {
        private final String uuid;
        private String userDiscordID;
        private Integer id;
        private String message;
        private long createdTimestamp;
        private long targetTimestamp;
        private boolean notificationSent;

        public Builder(String uuid) {
            Objects.requireNonNull(uuid, "Reminder | UUID cannot be null");
            this.uuid = uuid;
        }

        public Builder userDiscordID(String userDiscordID) {
            Objects.requireNonNull(userDiscordID, "Reminder | User ID cannot be null");
            this.userDiscordID = userDiscordID;
            return this;
        }

        public Builder id(Integer id) {
            Objects.requireNonNull(id, "Reminder | ID cannot be null");
            this.id = id;
            return this;
        }

        public Builder message(String message) {
            Objects.requireNonNull(message, "Reminder | Message cannot be null");
            this.message = message;
            return this;
        }

        public Builder createdTimestamp(long createdTimestamp) {
            this.createdTimestamp = createdTimestamp;
            return this;
        }

        public Builder targetTimestamp(long targetTimestamp) {
            this.targetTimestamp = targetTimestamp;
            return this;
        }

        public Builder notificationSent(boolean notificationSent) {
            this.notificationSent = notificationSent;
            return this;
        }

        public Reminder build() {
            return new Reminder(this);
        }

    }
}