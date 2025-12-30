package mist.mystralix.application.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mist.mystralix.domain.team.Team;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TeamHelper {

    private static final Type LIST_TYPE = new TypeToken<List<String>>() {
    }.getType();
    private static final Gson GSON = new Gson();

    public static Team getTeam(ResultSet resultSet) throws SQLException {
        return new Team.Builder(resultSet.getString("uuid"))
                .id(resultSet.getInt("id"))
                .teamName(resultSet.getString("team_name"))
                .teamLeader(resultSet.getString("team_leader"))
                .moderators(GSON.fromJson(resultSet.getString("moderators"), LIST_TYPE))
                .members(GSON.fromJson(resultSet.getString("members"), LIST_TYPE))
                .taskUUIDs(GSON.fromJson(resultSet.getString("task_uuids"), LIST_TYPE))
                .teamInvitations(GSON.fromJson(resultSet.getString("team_invitations"), LIST_TYPE))
                .build();
    }

}
