package mist.mystralix.domain.team;

import mist.mystralix.infrastructure.exception.TeamOperationException;
import mist.mystralix.utils.messages.TeamMessages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Team {

    // Immutable
    private final String uuid;

    // ID value set by DB | Immutable
    private final Integer id;

    private String teamName;

    private String teamLeader;

    // Immutable List
    private final List<String> moderators;

    // Immutable List
    private final List<String> members;

    // Immutable List
    private final List<String> taskUUIDs;

    // Immutable List
    private final List<String> teamInvitations;

    private Team(Builder builder) {
        this.id = builder.id;
        this.uuid = Objects.requireNonNull(builder.uuid);
        this.teamName = Objects.requireNonNull(builder.teamName);
        this.teamLeader = Objects.requireNonNull(builder.teamLeader);

        this.moderators = new ArrayList<>(builder.moderators);
        this.members = new ArrayList<>(builder.members);
        this.taskUUIDs = new ArrayList<>(builder.taskUUIDs);
        this.teamInvitations = new ArrayList<>(builder.teamInvitations);
    }

    // Getters //

    public String getUUID() {
        return uuid;
    }

    public Integer getId() {
        return id;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTeamLeader() {
        return teamLeader;
    }

    public List<String> getModerators() {
        return Collections.unmodifiableList(this.moderators);
    }

    public List<String> getMembers() {
        return Collections.unmodifiableList(this.members);
    }

    public List<String> getTasksUUID() {
        return Collections.unmodifiableList(this.taskUUIDs);
    }

    public List<String> getTeamInvitations() {
        return Collections.unmodifiableList(this.teamInvitations);
    }

    // Setters //

    public void setTeamName(String teamName) {
        Objects.requireNonNull(teamName, "Null Input. Please provide a proper input");
        if (this.teamName.equals(teamName)) {
            throw new TeamOperationException("This team already has the same team name");
        }
        this.teamName = teamName;
    }

    // Throws NullPointerException and TeamOperationException
    // Domain rule: only members can be promoted to moderator
    public void addModerator(String moderator) {
        Objects.requireNonNull(moderator, "Null Input. Please provide a proper input");
        if (this.teamLeader.equals(moderator)) {
            throw new TeamOperationException("You cannot add the team leader as a moderator");
        }
        if (this.moderators.contains(moderator)) {
            String role = "moderator";
            throw new TeamOperationException(String.format(
                    TeamMessages.USER_ALREADY_HAS_ROLE_IN_TEAM,
                    role,
                    teamName
            ));
        }
        if (!this.members.contains(moderator)) {
            throw new TeamOperationException(String.format(
                    TeamMessages.USER_NOT_PART_OF_TEAM,
                    teamName
            ));
        }
        this.moderators.add(moderator);
    }

    // Throws NullPointerException and TeamOperationException
    public void addMember(String member) {
        Objects.requireNonNull(member, "Null Input. Please provide a proper input");
        if (this.teamLeader.equals(member)) {
            throw new TeamOperationException("You cannot add the team leader as a member");
        }
        if (this.moderators.contains(member)) {
            String role = "moderator";
            throw new TeamOperationException(String.format(
                    TeamMessages.USER_ALREADY_HAS_ROLE_IN_TEAM,
                    role,
                    teamName
            ));
        }
        if (this.members.contains(member)) {
            String role = "member";
            throw new TeamOperationException(String.format(
                    TeamMessages.USER_ALREADY_HAS_ROLE_IN_TEAM,
                    role,
                    teamName
            ));
        }
        this.members.add(member);
    }

    // Throws NullPointerException and TeamOperationException
    // Only checks if the taskUUIDs already contains the task
    // Does not check if the task exist
    public void addTask(String task) {
        Objects.requireNonNull(task, "Null Input. Please provide a proper input");
        if (this.taskUUIDs.contains(task)) {
            throw new TeamOperationException("Team Task already exists in this team");
        }
        this.taskUUIDs.add(task);
    }

    // Domain rule: User must not be in the team already to be invited
    public void addTeamInvitation(String userId) {
        Objects.requireNonNull(userId, "Null Input. Please provide a proper input");
        if (this.teamLeader.equals(userId) || this.moderators.contains(userId) || this.members.contains(userId)) {
            throw new TeamOperationException(String.format(
                    TeamMessages.USER_ALREADY_PART_OF_TEAM,
                    teamName
            ));
        }
        if (this.teamInvitations.contains(userId)) {
            throw new TeamOperationException(TeamMessages.USER_ALREADY_INVITED);
        }
        this.teamInvitations.add(userId);
    }

    public void removeModerator(String moderator) {
        Objects.requireNonNull(moderator, "Null Input. Please provide a proper input");
        if (this.teamLeader.equals(moderator)) {
            throw new TeamOperationException("You cannot remove the team leader. Team leader is not a moderator.");
        }
        if (!this.moderators.contains(moderator)) {
            throw new TeamOperationException(String.format(
                    TeamMessages.USER_NOT_A_MODERATOR,
                    teamName
            ));
        }
        this.moderators.remove(moderator);
    }

    public void removeMember(String member) {
        Objects.requireNonNull(member, "Null Input. Please provide a proper input");
        if (this.teamLeader.equals(member)) {
            throw new TeamOperationException("You cannot remove the team leader as a member");
        }
        if (!this.members.contains(member)) {
            throw new TeamOperationException(String.format(
                    TeamMessages.USER_NOT_PART_OF_TEAM,
                    teamName
            ));
        }
        this.members.remove(member);
    }

    public void removeTask(String taskUUID) {
        Objects.requireNonNull(taskUUID, "Null Input. Please provide a proper input");
        if (!this.taskUUIDs.contains(taskUUID)) {
            throw new TeamOperationException("Team Task does not exist in this team");
        }
        this.taskUUIDs.remove(taskUUID);
    }

    public void removeTeamInvitation(String userId) {
        Objects.requireNonNull(userId, "Null Input. Please provide a proper input");
        if (!this.teamInvitations.contains(userId)) {
            throw new TeamOperationException(TeamMessages.NO_PENDING_TEAM_INVITATION);
        }
        this.teamInvitations.remove(userId);
    }

    /*
     * Transfers leadership from the current leader to a new user.
     * Domain rules:
     * - New leader must be a member or moderator
     * - Current leader becomes a moderator
     */
    public void transferLeadership(String newLeaderId) {
        Objects.requireNonNull(newLeaderId, "Null Input. Please provide a proper input");

        if (this.teamLeader.equals(newLeaderId)) {
            throw new TeamOperationException("User is already the team leader");
        }

        boolean wasModerator = moderators.contains(newLeaderId);
        boolean wasMember = members.contains(newLeaderId);

        if (!wasModerator && !wasMember) {
            throw new TeamOperationException(String.format(
                    TeamMessages.USER_NOT_PART_OF_TEAM,
                    teamName
            ));
        }

        String oldLeader = this.teamLeader;

        // Remove new leader from previous role
        if (wasModerator) {
            this.moderators.remove(newLeaderId);
        } else {
            this.members.remove(newLeaderId);
        }

        // Promote new leader
        this.teamLeader = newLeaderId;

        // Demote old leader to moderator
        if (!this.moderators.contains(oldLeader)) {
            this.moderators.add(oldLeader);
        }
    }

    public static class Builder {

        private final String uuid;

        // ID value set by DB
        private Integer id;

        private String teamName;

        private String teamLeader;

        private List<String> moderators = new ArrayList<>();

        private List<String> members = new ArrayList<>();

        private List<String> taskUUIDs = new ArrayList<>();

        private List<String> teamInvitations = new ArrayList<>();

        public Builder(String uuid) {
            this.uuid = uuid;
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder teamName(String teamName) {
            this.teamName = teamName;
            return this;
        }

        public Builder teamLeader(String teamLeader) {
            this.teamLeader = teamLeader;
            return this;
        }

        public Builder moderators(List<String> moderators) {
            this.moderators = new ArrayList<>(moderators);
            return this;
        }

        public Builder members(List<String> members) {
            this.members = new ArrayList<>(members);
            return this;
        }

        public Builder taskUUIDs(List<String> taskUUIDs) {
            this.taskUUIDs = new ArrayList<>(taskUUIDs);
            return this;
        }

        public Builder teamInvitations(List<String> teamInvitations) {
            this.teamInvitations = new ArrayList<>(teamInvitations);
            return this;
        }

        public Team build() {
            if (teamName == null || teamLeader == null) {
                throw new TeamOperationException("Team name or leader not set");
            }
            return new Team(this);
        }

    }

}