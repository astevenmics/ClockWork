package mist.mystralix.domain.task;

import mist.mystralix.infrastructure.exception.TeamTaskOperationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TeamTask extends UserTask implements ITask {

    private final String teamUUID;
    private final Integer teamID;
    private final List<String> assignedUsers;

    private TeamTask(Builder builder) {
        super(
                new UserTask.Builder(builder.uuid)
                        .userDiscordID(builder.userDiscordID)
                        .id(builder.id)
                        .title(builder.title)
                        .description(builder.description)
                        .status(builder.status)
        );
        this.teamUUID = builder.teamUUID;
        this.teamID = builder.teamID;
        this.assignedUsers = new ArrayList<>(builder.assignedUsers);
    }

    public List<String> getAssignedUsers() {
        return Collections.unmodifiableList(assignedUsers);
    }

    public String getTeamUUID() {
        return teamUUID;
    }

    public Integer getTeamID() {
        return teamID;
    }

    public void addAssignedUser(String userID) {
        if (assignedUsers.contains(userID)) {
            throw new TeamTaskOperationException("User already assigned to this team task.");
        }
        assignedUsers.add(userID);
    }

    public void removeAssignedUser(String userID) {
        if (!assignedUsers.contains(userID)) {
            throw new TeamTaskOperationException("User is not assigned to this team task.");
        }
        assignedUsers.remove(userID);
    }

    public static class Builder {

        private final String uuid;
        private String userDiscordID;
        private Integer id;
        private String title;
        private String description;
        private Integer status;

        private String teamUUID;
        private Integer teamID;
        private List<String> assignedUsers = new ArrayList<>();

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

        public Builder teamUUID(String uuid) {
            Objects.requireNonNull(uuid, "Team UUID cannot be null");
            this.teamUUID = uuid;
            return this;
        }

        public Builder teamID(Integer teamID) {
            Objects.requireNonNull(teamID, "Team ID cannot be null");
            this.teamID = teamID;
            return this;
        }

        public Builder assignedUsers(List<String> assignedUsers) {
            Objects.requireNonNull(assignedUsers, "List of assigned users cannot be null");
            this.assignedUsers = new ArrayList<>(assignedUsers);
            return this;
        }

        public TeamTask build() {
            if (userDiscordID == null || teamUUID == null || teamID == null || title == null || description == null || status == null) {
                throw new TeamTaskOperationException("Not all required fields have been set");
            }
            return new TeamTask(this);
        }
    }

}