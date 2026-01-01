package mist.mystralix.infrastructure.repository.teamtask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mist.mystralix.config.DBManager;
import mist.mystralix.domain.task.TeamTask;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBTeamTaskRepository implements TeamTaskRepository {

    private final Type LIST_TYPE = new TypeToken<ArrayList<String>>(){}.getType();

    @Override
    public void create(TeamTask teamTask) {
        String sql = """
                INSERT INTO team_tasks
                (uuid, user_discord_id, title, description, status, team_uuid, team_id, assigned_users)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
            ) {

            Gson gson = new Gson();
            String assignedUsersAsJSON = gson.toJson(teamTask.getAssignedUsers());

            preparedStatement.setString(1, teamTask.getUUID());
            preparedStatement.setString(2, teamTask.getUserDiscordID());
            preparedStatement.setString(3, teamTask.getTitle());
            preparedStatement.setString(4, teamTask.getDescription());
            preparedStatement.setInt(5, teamTask.getStatus());
            preparedStatement.setString(6, teamTask.getTeamUUID());
            preparedStatement.setInt(7, teamTask.getTeamID());
            preparedStatement.setString(8, assignedUsersAsJSON);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error creating team task with UUID: " + teamTask.getUUID());
            throw new RuntimeException("DB Error", e);
        }

    }

    @Override
    public TeamTask findByID(int id) {
        String sql = "SELECT * FROM team_tasks WHERE id = ?;";

        TeamTask teamTask = null;

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String userDiscordID = resultSet.getString("user_discord_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                Integer status = resultSet.getInt("status");
                String teamUUID = resultSet.getString("team_uuid");
                Integer teamID = resultSet.getInt("team_id");
                String assignedUsersAsJSON = resultSet.getString("assigned_users");

                Gson gson = new Gson();
                ArrayList<String> assignedUsers = gson.fromJson(assignedUsersAsJSON, LIST_TYPE);

                teamTask = new TeamTask.Builder(uuid)
                        .userDiscordID(userDiscordID)
                        .id(id)
                        .title(title)
                        .description(description)
                        .status(status)
                        .teamUUID(teamUUID)
                        .teamID(teamID)
                        .assignedUsers(assignedUsers)
                        .build();
            }

        } catch (SQLException e) {
            System.out.println("Error find team task with ID: " + id);
            throw new RuntimeException("DB Error", e);
        }
        return teamTask;
    }

    @Override
    public TeamTask findByUUID(String uuid) {
        String sql = "SELECT * FROM team_tasks WHERE uuid = ?;";

        TeamTask teamTask = null;

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
                ) {
            preparedStatement.setString(1, uuid);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String userDiscordID = resultSet.getString("user_discord_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                Integer status = resultSet.getInt("status");
                String teamUUID = resultSet.getString("team_uuid");
                Integer teamID = resultSet.getInt("team_id");
                String assignedUsersAsJSON = resultSet.getString("assigned_users");

                Gson gson = new Gson();
                ArrayList<String> assignedUsers = gson.fromJson(assignedUsersAsJSON, LIST_TYPE);

                teamTask = new TeamTask.Builder(uuid)
                        .userDiscordID(userDiscordID)
                        .id(id)
                        .title(title)
                        .description(description)
                        .status(status)
                        .teamUUID(teamUUID)
                        .teamID(teamID)
                        .assignedUsers(assignedUsers)
                        .build();
            }

        } catch (SQLException e) {
            System.out.println("Error find team task with UUID: " + uuid);
            throw new RuntimeException("DB Error", e);
        }
        return teamTask;
    }

    @Override
    public void update(TeamTask teamTask) {

        String sql =
                """
                        UPDATE team_tasks
                        SET title = ?, description = ?, status = ?, assigned_users = ?
                        WHERE uuid = ?;
                        """;

        String teamTaskUUID = teamTask.getUUID();

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            Gson gson = new Gson();
            String assignedUsersAsJSON = gson.toJson(teamTask.getAssignedUsers());

            preparedStatement.setString(1, teamTask.getTitle());
            preparedStatement.setString(2, teamTask.getDescription());
            preparedStatement.setInt(3, teamTask.getStatus());
            preparedStatement.setString(4, assignedUsersAsJSON);
            preparedStatement.setString(5, teamTaskUUID);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("No team task updated. UUID not found: " + teamTaskUUID);
            }

        } catch (SQLException e) {
            System.out.println("Error updating team task with UUID: " + teamTaskUUID);
            throw new RuntimeException("DB Error", e);
        }
    }

    @Override
    public void delete(TeamTask baseObject) {
        String sql = "DELETE FROM team_tasks WHERE uuid = ?;";
        String teamTaskUUID = baseObject.getUUID();

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, teamTaskUUID);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted == 0) {
                System.out.println("No team task deleted. UUID not found: " + teamTaskUUID);
            }
        } catch (SQLException e) {
            System.out.println("Error deleting team task with UUID: " + teamTaskUUID);
            throw new RuntimeException("DB Error", e);
        }

    }

    @Override
    public ArrayList<TeamTask> findAllByTeamId(int teamId) {

        String sql = "SELECT * FROM team_tasks WHERE team_id = ? ORDER BY id ASC;";
        ArrayList<TeamTask> teamTasks = new ArrayList<>();

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, teamId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                Integer id = resultSet.getInt("id");
                String userDiscordID = resultSet.getString("user_discord_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                Integer status = resultSet.getInt("status");
                String teamUUID = resultSet.getString("team_uuid");
                String assignedUsersAsJSON = resultSet.getString("assigned_users");

                Gson gson = new Gson();
                ArrayList<String> assignedUsers = gson.fromJson(assignedUsersAsJSON, LIST_TYPE);

                teamTasks.add(
                        new TeamTask.Builder(uuid)
                                .userDiscordID(userDiscordID)
                                .id(id)
                                .title(title)
                                .description(description)
                                .status(status)
                                .teamUUID(teamUUID)
                                .teamID(teamId)
                                .assignedUsers(assignedUsers)
                                .build()
                );
            }

        } catch (SQLException e) {
            System.out.println("Error finding team tasks with team id: " + teamId);
            throw new RuntimeException("DB Error", e);
        }

        return teamTasks;
    }
}