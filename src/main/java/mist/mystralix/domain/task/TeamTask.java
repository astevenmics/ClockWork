package mist.mystralix.domain.task;

import mist.mystralix.infrastructure.exception.TeamTaskOperationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamTask extends Task {

    private final String teamUUID;
    private final int teamID;
    private final List<String> assignedUsers;

    public TeamTask(
            String uuid,
            String userDiscordID,
            int id,
            TaskDAO taskDAO,
            String teamUUID,
            int teamID,
            List<String> assignedUsers
    ) {
        super(
                uuid,
                userDiscordID,
                id,
                taskDAO
        );
        this.teamUUID = teamUUID;
        this.teamID = teamID;
        this.assignedUsers = new ArrayList<>(assignedUsers);
    }

    public TeamTask(
            String uuid,
            String userDiscordID,
            TaskDAO taskDAO,
            String teamUUID,
            int teamID,
            ArrayList<String> assignedUsers
    ) {
        super(
                uuid,
                userDiscordID,
                taskDAO
        );
        this.teamUUID = teamUUID;
        this.teamID = teamID;
        this.assignedUsers = assignedUsers;
    }

    public List<String> getAssignedUsers() {
        return Collections.unmodifiableList(assignedUsers);
    }

    public String getTeamUUID() {
        return teamUUID;
    }

    public int getTeamID() {
        return teamID;
    }

    public void addAssignedUser(String userID) {
        if (assignedUsers.contains(userID)) {
            throw new TeamTaskOperationException("User already assigned to this team task.");
        }
        assignedUsers.add(userID);
    }

    public void removeAssignedUser(String userID) {
        if (assignedUsers.contains(userID)) {
            throw new TeamTaskOperationException("User is not assigned to this team task.");
        }
        assignedUsers.remove(userID);
    }

}