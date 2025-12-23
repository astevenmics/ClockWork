package mist.mystralix.application.team;

import mist.mystralix.domain.team.Team;
import mist.mystralix.infrastructure.repository.team.TeamRepository;
import mist.mystralix.utils.IdentifiableFetcher;

import java.util.ArrayList;
import java.util.UUID;

public class TeamService implements IdentifiableFetcher<Team> {

    private final TeamRepository TEAM_REPOSITORY;

    public TeamService(
            TeamRepository teamRepository
    ) {
        this.TEAM_REPOSITORY = teamRepository;
    }

    public Team create(
            String teamName,
            String teamLeaderId
    ) {

        String uuid = UUID.randomUUID().toString();

        Team newTeam = new Team(
                uuid,
                teamName,
                teamLeaderId,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        TEAM_REPOSITORY.create(newTeam);

        return TEAM_REPOSITORY.findByUUID(uuid);
    }

    public void update(Team team) {
        TEAM_REPOSITORY.update(team);
    }

    public void delete(Team team) {
        TEAM_REPOSITORY.delete(team);
    }

    public ArrayList<Team> getUserTeams(String userDiscordID) {
        return TEAM_REPOSITORY.readAll(userDiscordID);
    }

    public Team findByID(int id) {
        return TEAM_REPOSITORY.findByID(id);
    }

    @Override
    public Team fetchByUserIDAndObjectID(String userDiscordId, int objectID) {
        return TEAM_REPOSITORY.findByDiscordIDAndID(userDiscordId, objectID);
    }

    @Override
    public Team fetchByUserIDAndObjectUUID(String userDiscordId, String objectUUID) {
        return TEAM_REPOSITORY.findByDiscordIDAndUUID(userDiscordId, objectUUID);
    }
}