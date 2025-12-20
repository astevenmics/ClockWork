package mist.mystralix.infrastructure.repository.teamtask;

import mist.mystralix.domain.task.TeamTask;

import java.util.ArrayList;

public class DBTeamTaskRepository implements TeamTaskRepository {


    @Override
    public void create(TeamTask baseObject) {

    }

    @Override
    public TeamTask findByID(int uuid) {
        return null;
    }

    @Override
    public TeamTask findByUUID(String uuid) {
        return null;
    }

    @Override
    public TeamTask findByDiscordIDAndUUID(String userDiscordID, String uuid) {
        return null;
    }

    @Override
    public TeamTask findByDiscordIDAndID(String userDiscordID, int id) {
        return null;
    }

    @Override
    public void update(TeamTask baseObject) {

    }

    @Override
    public void delete(TeamTask baseObject) {

    }

    @Override
    public ArrayList<TeamTask> readAll(String userDiscordID) {
        return null;
    }
}