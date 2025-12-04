package mist.mystralix.domain.team;

import mist.mystralix.domain.task.Task;

import java.util.ArrayList;

public class Team {

    private String uuid;
    private int id;
    private ArrayList<String> moderators;
    private ArrayList<String> members;
    private Task teamTask;

    public Team(
            String uuid,
            int id,
            ArrayList<String> moderators,
            ArrayList<String> members,
            Task teamTask
    ) {
        this.uuid = uuid;
        this.id = id;
        this.moderators = moderators;
        this.members = members;
        this.teamTask = teamTask;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Task getTeamTask() {
        return teamTask;
    }

    public void setTeamTask(Task teamTask) {
        this.teamTask = teamTask;
    }
}