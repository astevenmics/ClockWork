package mist.mystralix.application.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mist.mystralix.domain.team.Team;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TeamHelper {

    private static final Type LIST_TYPE = new TypeToken<ArrayList<String>>() {
    }.getType();
    private static final Gson GSON = new Gson();

    public static Team getTeam(ResultSet resultSet) throws SQLException {
        return new Team(
                resultSet.getString("uuid"),
                resultSet.getInt("id"),
                resultSet.getString("team_name"),
                resultSet.getString("team_leader"),
                GSON.fromJson(resultSet.getString("moderators"), LIST_TYPE),
                GSON.fromJson(resultSet.getString("members"), LIST_TYPE),
                GSON.fromJson(resultSet.getString("tasks_uuid"), LIST_TYPE),
                GSON.fromJson(resultSet.getString("team_invitations"), LIST_TYPE)
        );
    }

}
