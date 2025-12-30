package mist.mystralix.application.team;

import mist.mystralix.domain.team.Team;
import mist.mystralix.infrastructure.repository.team.TeamRepository;
import mist.mystralix.utils.IdentifiableFetcher;

import java.util.ArrayList;

public class TeamService implements IdentifiableFetcher<Team> {

    private final TeamRepository TEAM_REPOSITORY;

    public TeamService(TeamRepository teamRepository) {
        this.TEAM_REPOSITORY = teamRepository;
    }

    @Override
    public void update(Team team) {
        TEAM_REPOSITORY.update(team);
    }

    @Override
    public void delete(Team team) {
        TEAM_REPOSITORY.delete(team);
    }

    @Override
    public Team getById(int id) {
        return TEAM_REPOSITORY.findByID(id);
    }

    @Override
    public Team getByUUID(String uuid) {
        return TEAM_REPOSITORY.findByUUID(uuid);
    }

    public void create(String uuid, String name, String leaderId) {
        TEAM_REPOSITORY.create(
                new Team.Builder(uuid)
                        .teamLeader(leaderId)
                        .teamName(name)
                        .moderators(new ArrayList<>())
                        .members(new ArrayList<>())
                        .taskUUIDs(new ArrayList<>())
                        .teamInvitations(new ArrayList<>())
                        .build()
        );
    }



    public ArrayList<Team> getUserTeams(String userDiscordID) {
        return TEAM_REPOSITORY.readAll(userDiscordID);
    }
}