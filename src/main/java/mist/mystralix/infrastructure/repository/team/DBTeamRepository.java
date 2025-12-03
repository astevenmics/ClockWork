package mist.mystralix.infrastructure.repository.team;

import mist.mystralix.domain.team.Team;

import java.util.List;

public class DBTeamRepository implements TeamRepository {
    @Override
    public void create(Team baseObject) {

    }

    @Override
    public Team findByUUID(String userDiscordID, String uuid) {
        return null;
    }

    @Override
    public Team findByID(String userDiscordID, int id) {
        return null;
    }

    @Override
    public void update(Team baseObject) {

    }

    @Override
    public void delete(Team baseObject) {

    }

    @Override
    public List<Team> readAll(String userDiscordID) {
        return List.of();
    }
}