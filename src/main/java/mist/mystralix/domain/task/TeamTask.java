package mist.mystralix.domain.task;

import java.util.ArrayList;

public class TeamTask extends Task {

    private String teamUUID;
    private int teamID;
    private ArrayList<String> assignedUsers;

    public TeamTask(
            String uuid,
            String userDiscordID,
            int id,
            TaskDAO taskDAO,
            String teamUUID,
            int teamID,
            ArrayList<String> assignedUsers
    ) {
        super(
                uuid,
                userDiscordID,
                id,
                taskDAO
        );
        this.teamUUID = teamUUID;
        this.teamID = teamID;
        this.assignedUsers = assignedUsers;
    }

    public TeamTask(
            String uuid,
            String userDiscordID,
            TaskDAO taskDAO,
            String teamUUID,
            ArrayList<String> assignedUsers
    ) {
        super(
                uuid,
                userDiscordID,
                taskDAO
        );
        this.teamUUID = teamUUID;
        this.assignedUsers = assignedUsers;
    }

    public ArrayList<String> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(ArrayList<String> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public String getTeamUUID() {
        return teamUUID;
    }

    public void setTeamUUID(String teamUUID) {
        this.teamUUID = teamUUID;
    }

    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }
}