package mist.mystralix.infrastructure.repository.team;

import com.google.gson.Gson;
import mist.mystralix.application.helper.TeamHelper;
import mist.mystralix.config.DBManager;
import mist.mystralix.domain.team.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBTeamRepository implements TeamRepository {

    @Override
    public void create(Team baseObject) {
        String sqlStatement =
                """
                        INSERT INTO teams
                        (uuid, team_name, team_leader, moderators, members, task_uuids)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """;

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
                ) {

            Gson gson = new Gson();
            String teamName = baseObject.getTeamName();
            String teamLeader = baseObject.getTeamLeader();
            String moderators = gson.toJson(baseObject.getModerators());
            String members = gson.toJson(baseObject.getMembers());
            String taskUUIDs = gson.toJson(baseObject.getTasksUUID());

            preparedStatement.setString(1, baseObject.getUUID());
            preparedStatement.setString(2, teamName);
            preparedStatement.setString(3, teamLeader);
            preparedStatement.setString(4, moderators);
            preparedStatement.setString(5, members);
            preparedStatement.setString(6, taskUUIDs);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error creating team UUID: " + baseObject.getUUID());
            throw new RuntimeException("DB Error", e);
        }
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
            if (resultSet.next()) {
                team = TeamHelper.getTeam(resultSet);
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
            if (resultSet.next()) {
                team = TeamHelper.getTeam(resultSet);
            }

        } catch (SQLException e) {
            System.out.println("Error finding a team with an ID: " + id);
            throw new RuntimeException("DB Error", e);
        }
        return team;
    }

    @Override
    public void update(Team baseObject) {

        String sqlStatement =
                """
                        UPDATE teams
                        SET team_leader = ?, team_name = ?, moderators = ?, members = ?, task_uuids = ?, team_invitations = ?
                        WHERE uuid = ?
                        """;

        String uuid = baseObject.getUUID();

        try(
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
                ) {
            Gson gson = new Gson();
            String moderators = gson.toJson(baseObject.getModerators());
            String members = gson.toJson(baseObject.getMembers());
            String taskUUIDs = gson.toJson(baseObject.getTasksUUID());
            String teamInvitations = gson.toJson(baseObject.getTeamInvitations());
            preparedStatement.setString(1, baseObject.getTeamLeader());
            preparedStatement.setString(2, baseObject.getTeamName());
            preparedStatement.setString(3, moderators);
            preparedStatement.setString(4, members);
            preparedStatement.setString(5, taskUUIDs);
            preparedStatement.setString(6, teamInvitations);
            preparedStatement.setString(7, uuid);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("No team updated. UUID not found: " + uuid);
            }

        } catch (SQLException e) {
            System.out.println("Error updating team with an UUID: " + uuid);
            throw new RuntimeException("DB Error", e);
        }

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

        String sqlStatement =
                """
                        SELECT * FROM teams WHERE team_leader = ?
                        OR JSON_CONTAINS(moderators, JSON_ARRAY(?))
                        OR JSON_CONTAINS(members, JSON_ARRAY(?)) ORDER BY id ASC
                        """;

        try(
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
                ) {

            preparedStatement.setString(1, userDiscordID);
            preparedStatement.setString(2, userDiscordID);
            preparedStatement.setString(3, userDiscordID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                teams.add(TeamHelper.getTeam(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Error reading all existing teams with userDiscordID: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
        return teams;
    }
}