package mist.mystralix.domain.team;

import java.util.ArrayList;

public class Team {

    private String uuid;
    private int id;
    private String teamName;
    private String teamLeader;
    private ArrayList<String> moderators;
    private ArrayList<String> members;
    private ArrayList<String> tasksUUID;
    private ArrayList<String> teamInvitations;

    public Team(
            String uuid,
            int id,
            String teamName,
            String teamLeader,
            ArrayList<String> moderators,
            ArrayList<String> members,
            ArrayList<String> tasksUUID,
            ArrayList<String> teamInvitations
    ) {
        this.uuid = uuid;
        this.id = id;
        this.teamName = teamName;
        this.teamLeader = teamLeader;
        this.moderators = moderators;
        this.members = members;
        this.tasksUUID = tasksUUID;
        this.teamInvitations = teamInvitations;
    }

    public Team(
            String uuid,
            String teamName,
            String teamLeader,
            ArrayList<String> moderators,
            ArrayList<String> members,
            ArrayList<String> tasksUUID,
            ArrayList<String> teamInvitations
    ) {
        this.uuid = uuid;
        this.teamName = teamName;
        this.teamLeader = teamLeader;
        this.moderators = moderators;
        this.members = members;
        this.tasksUUID = tasksUUID;
        this.teamInvitations = teamInvitations;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(String teamLeader) {
        this.teamLeader = teamLeader;
    }

    public ArrayList<String> getModerators() {
        return moderators;
    }

    public void setModerators(ArrayList<String> moderators) {
        this.moderators = moderators;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public ArrayList<String> getTasksUUID() {
        return tasksUUID;
    }

    public void setTaskUUID(ArrayList<String> tasksUUID) {
        this.tasksUUID = tasksUUID;
    }

    public ArrayList<String> getTeamInvitations() {
        return teamInvitations;
    }

    public void setTeamInvitations(ArrayList<String> teamInvitations) {
        this.teamInvitations = teamInvitations;
    }
}