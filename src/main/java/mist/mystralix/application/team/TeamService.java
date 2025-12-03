package mist.mystralix.application.team;

import mist.mystralix.domain.team.Team;
import mist.mystralix.utils.IdentifiableFetcher;

public class TeamService implements IdentifiableFetcher<Team> {
    @Override
    public Team fetchByUserIDAndObjectID(String userDiscordId, int objectID) {
        return null;
    }
}