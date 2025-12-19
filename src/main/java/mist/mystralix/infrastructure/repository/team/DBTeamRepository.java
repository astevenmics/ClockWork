package mist.mystralix.infrastructure.repository.team;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mist.mystralix.config.DBManager;
import mist.mystralix.domain.team.Team;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBTeamRepository implements TeamRepository {

    private final Type LIST_TYPE = new TypeToken<ArrayList<String>>(){}.getType();

    @Override
    public void create(Team baseObject) {
        String sqlStatement =
                "INSERT INTO teams " +
                "(uuid, team_name, team_leader, moderators, members, tasks_uuid) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
                ) {

            Gson gson = new Gson();
            String teamName = baseObject.getTeamName();
            String teamLeader = baseObject.getTeamLeader();
            String moderators = gson.toJson(baseObject.getModerators());
            String members = gson.toJson(baseObject.getMembers());
            String tasksUUID = gson.toJson(baseObject.getTasksUUID());

            preparedStatement.setString(1, baseObject.getUUID());
            preparedStatement.setString(2, teamName);
            preparedStatement.setString(3, teamLeader);
            preparedStatement.setString(4, moderators);
            preparedStatement.setString(5, members);
            preparedStatement.setString(6, tasksUUID);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error creating team UUID: " + baseObject.getUUID());
            throw new RuntimeException("DB Error", e);
        }
    }

    @Override
    public Team findByDiscordIDAndUUID(String userDiscordID, String uuid) {

        String sqlStatement = "SELECT * FROM teams " +
                "WHERE uuid = ? " +
                "AND (" +
                "JSON CONTAINS(moderators, JSON_ARRAY(?)) " +
                "OR JSON CONTAINS(members, JSON_ARRAY(?))" +
                ");";

        Team team = null;

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
                ) {

            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, userDiscordID);
            preparedStatement.setString(3, userDiscordID);

            ResultSet resultSet = preparedStatement.executeQuery();
            Gson gson = new Gson();
            if (resultSet.next()) {
                int id = resultSet.getInt("uuid");
                String name = resultSet.getString("team_name");
                String teamLeader = resultSet.getString("team_leader");
                ArrayList<String> moderators = gson.fromJson(resultSet.getString("moderators"), LIST_TYPE);
                ArrayList<String> members = gson.fromJson(resultSet.getString("members"), LIST_TYPE);
                ArrayList<String> tasksUUID = gson.fromJson(resultSet.getString("tasks_uuid"), LIST_TYPE);
                ArrayList<String> teamInvitations = gson.fromJson(resultSet.getString("team_invitations"), LIST_TYPE);

                team = new Team(
                        uuid,
                        id,
                        name,
                        teamLeader,
                        moderators,
                        members,
                        tasksUUID,
                        teamInvitations
                );
            }
        } catch (SQLException e) {
            System.out.println("Error finding a team with an UUID: " + uuid);
            throw new RuntimeException("DB Error", e);
        }

        return team;
    }

    @Override
    public Team findByDiscordIDAndID(String userDiscordID, int id) {

        String sqlStatement = "SELECT * FROM teams WHERE id = ? AND (" +
                "JSON_CONTAINS(moderators, JSON_ARRAY(?)) " +
                "OR JSON_CONTAINS(members, JSON_ARRAY(?))" +
                ");";

        Team team = null;
        try(
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
                ) {

            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, userDiscordID);
            preparedStatement.setString(3, userDiscordID);

            ResultSet resultSet = preparedStatement.executeQuery();
            Gson gson = new Gson();

            if (resultSet.next()) {
                String uuid = resultSet.getString("id");
                String name = resultSet.getString("team_name");
                String teamLeader = resultSet.getString("team_leader");
                ArrayList<String> moderators = gson.fromJson(resultSet.getString("moderators"), LIST_TYPE);
                ArrayList<String> members = gson.fromJson(resultSet.getString("members"), LIST_TYPE);
                ArrayList<String> tasksUUID = gson.fromJson(resultSet.getString("tasks_uuid"), LIST_TYPE);
                ArrayList<String> teamInvitations = gson.fromJson(resultSet.getString("team_invitations"), LIST_TYPE);

                team = new Team(
                        uuid,
                        id,
                        name,
                        teamLeader,
                        moderators,
                        members,
                        tasksUUID,
                        teamInvitations
                );
            }

        } catch (SQLException e) {
            System.out.println("Error finding a team with an ID: " + id);
            throw new RuntimeException("DB Error", e);
        }
        return team;
    }

    @Override
    public Team findByUUID(String uuid) {

        String sqlStatement = "SELECT * FROM teams WHERE uuid = ?";

        Team team = null;
        try(
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {

            preparedStatement.setString(1, uuid);

            ResultSet resultSet = preparedStatement.executeQuery();
            Gson gson = new Gson();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("team_name");
                String teamLeader = resultSet.getString("team_leader");
                ArrayList<String> moderators = gson.fromJson(resultSet.getString("moderators"), LIST_TYPE);
                ArrayList<String> members = gson.fromJson(resultSet.getString("members"), LIST_TYPE);
                ArrayList<String> tasksUUID = gson.fromJson(resultSet.getString("tasks_uuid"), LIST_TYPE);
                ArrayList<String> teamInvitations = gson.fromJson(resultSet.getString("team_invitations"), LIST_TYPE);

                team = new Team(
                        uuid,
                        id,
                        name,
                        teamLeader,
                        moderators,
                        members,
                        tasksUUID,
                        teamInvitations
                );
            }

        } catch (SQLException e) {
            System.out.println("Error finding a team with an UUID: " + uuid);
            throw new RuntimeException("DB Error", e);
        }
        return team;
    }

    @Override
    public Team findByID(int id) {

        String sqlStatement = "SELECT * FROM teams WHERE id = ?";

        Team team = null;
        try(
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            Gson gson = new Gson();

            if (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String name = resultSet.getString("team_name");
                String teamLeader = resultSet.getString("team_leader");
                ArrayList<String> moderators = gson.fromJson(resultSet.getString("moderators"), LIST_TYPE);
                ArrayList<String> members = gson.fromJson(resultSet.getString("members"), LIST_TYPE);
                ArrayList<String> tasksUUID = gson.fromJson(resultSet.getString("tasks_uuid"), LIST_TYPE);
                ArrayList<String> teamInvitations = gson.fromJson(resultSet.getString("team_invitations"), LIST_TYPE);

                team = new Team(
                        uuid,
                        id,
                        name,
                        teamLeader,
                        moderators,
                        members,
                        tasksUUID,
                        teamInvitations
                );
            }

        } catch (SQLException e) {
            System.out.println("Error finding a team with an ID: " + id);
            throw new RuntimeException("DB Error", e);
        }
        return team;
    }

    @Override
    public void update(Team baseObject) {

        String sqlStatement = "UPDATE teams " +
                "SET team_name = ?, moderators = ?, members = ?, tasks_uuid = ?, team_invitations = ? " +
                "WHERE uuid = ?";

        String uuid = baseObject.getUUID();

        try(
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
                ) {
            Gson gson = new Gson();
            String moderators = gson.toJson(baseObject.getModerators());
            String members = gson.toJson(baseObject.getMembers());
            String tasksUUID = gson.toJson(baseObject.getTasksUUID());
            String teamInvitations = gson.toJson(baseObject.getTeamInvitations());
            preparedStatement.setString(1, baseObject.getTeamName());
            preparedStatement.setString(2, moderators);
            preparedStatement.setString(3, members);
            preparedStatement.setString(4, tasksUUID);
            preparedStatement.setString(5, teamInvitations);
            preparedStatement.setString(6, uuid);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("No team updated. UUID not found: " + uuid);
            }

        } catch (SQLException e) {
            System.out.println("Error updating team with an UUID: " + uuid);
            throw new RuntimeException("DB Error", e);
        }

    }

    public void updateTeamLeader(Team baseObject) {
        // do something
    }

    @Override
    public void delete(Team baseObject) {
        String sqlStatement = "DELETE FROM teams WHERE uuid = ?";

        String teamUUID = baseObject.getUUID();

        try(
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
                ) {

            preparedStatement.setString(1, teamUUID);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted == 0) {
                System.out.println("No team deleted. UUID not found: " + teamUUID);
            }

        } catch (SQLException e) {
            System.out.println("Error deleting team UUID: " + teamUUID + " for team: " + teamUUID);
            throw new RuntimeException("DB Error", e);
        }
    }

    @Override
    public ArrayList<Team> readAll(String userDiscordID) {

        ArrayList<Team> teams = new ArrayList<>();

        String sqlStatement = "SELECT * FROM teams WHERE team_leader = ?" +
                "OR JSON_CONTAINS(moderators, JSON_ARRAY(?)) " +
                "OR JSON_CONTAINS(members, JSON_ARRAY(?)) ORDER BY id ASC";

        try(
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
                ) {

            preparedStatement.setString(1, userDiscordID);
            preparedStatement.setString(2, userDiscordID);
            preparedStatement.setString(3, userDiscordID);

            ResultSet resultSet = preparedStatement.executeQuery();

            Gson gson = new Gson();

            while(resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                int id = resultSet.getInt("id");
                String name = resultSet.getString("team_name");
                String teamLeader = resultSet.getString("team_leader");

                ArrayList<String> moderators = gson.fromJson(
                        resultSet.getString("moderators"),
                        LIST_TYPE
                );
                ArrayList<String> members = gson.fromJson(
                        resultSet.getString("members"),
                        LIST_TYPE
                );
                ArrayList<String> tasksUUID = gson.fromJson(
                        resultSet.getString("tasks_uuid"),
                        LIST_TYPE
                );

                ArrayList<String> teamInvitations = gson.fromJson(
                        resultSet.getString("team_invitations"),
                        LIST_TYPE
                );

                teams.add(new Team(
                        uuid,
                        id,
                        name,
                        teamLeader,
                        moderators,
                        members,
                        tasksUUID,
                        teamInvitations
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error reading all existing teams with userDiscordID: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
        return teams;
    }
}