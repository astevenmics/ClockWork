package mist.mystralix.domain.task;

import java.util.Objects;

public class UserTask implements ITask {

    private final String uuid;

    private final String userDiscordID;

    private final Integer id;

    private String title;
    private String description;
    private Integer status;

    UserTask(Builder builder) {
        this.uuid = builder.uuid;
        this.userDiscordID = builder.userDiscordID;
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.status = builder.status;
    }

    public String getUUID() {
        return uuid;
    }

    public String getUserDiscordID() {
        return userDiscordID;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public static class Builder {

        private final String uuid;
        private String userDiscordID;
        private Integer id;

        private String title;
        private String description;
        private Integer status;

        public Builder(String uuid) {
            Objects.requireNonNull(uuid, "UUID cannot be null");
            this.uuid = uuid;
        }

        public Builder userDiscordID(String userDiscordID) {
            Objects.requireNonNull(userDiscordID, "User ID cannot be null");
            this.userDiscordID = userDiscordID;
            return this;
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            Objects.requireNonNull(title, "Title cannot be null");
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            Objects.requireNonNull(description, "Description cannot be null");
            this.description = description;
            return this;
        }

        public Builder status(Integer status) {
            Objects.requireNonNull(status, "Status cannot be null");
            this.status = status;
            return this;
        }

        public UserTask build() {
            if (userDiscordID == null || title == null || description == null || status == null) {
                throw new IllegalStateException("Not all required fields have been set");
            }
            return new UserTask(this);
        }

    }

}