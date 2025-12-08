package mist.mystralix.domain.team;

import java.util.ArrayList;

public class Team {

    private String uuid;
    private int id;
    private String teamName;
    private ArrayList<String> moderators;
    private ArrayList<String> members;
    private ArrayList<String> tasksUUID;

    public Team(
            String uuid,
            int id,
            String teamName,
            ArrayList<String> moderators,
            ArrayList<String> members,
            ArrayList<String> tasksUUID
    ) {
        this.uuid = uuid;
        this.id = id;
        this.teamName = teamName;
        this.moderators = moderators;
        this.members = members;
        this.tasksUUID = tasksUUID;
    }

    public Team(
            String uuid,
            String teamName,
            ArrayList<String> moderators,
            ArrayList<String> members,
            ArrayList<String> tasksUUID
    ) {
        this.uuid = uuid;
        this.teamName = teamName;
        this.moderators = moderators;
        this.members = members;
        this.tasksUUID = tasksUUID;
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
}