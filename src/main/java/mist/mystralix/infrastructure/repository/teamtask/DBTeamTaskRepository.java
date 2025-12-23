package mist.mystralix.infrastructure.repository.teamtask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mist.mystralix.config.DBManager;
import mist.mystralix.domain.task.TaskDAO;
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
    public void create(TeamTask baseObject) {
        String sql = """
                INSERT INTO team_task
                (uuid, user_discord_id, task_dao, team_uuid, team_id, assigned_users)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
            ) {

            Gson gson = new Gson();
            String taskDAOAsJSON = gson.toJson(baseObject.getTaskDAO());
            String assignedUsersAsJSON = gson.toJson(baseObject.getAssignedUsers());

            preparedStatement.setString(1, baseObject.getUUID());
            preparedStatement.setString(2, baseObject.getUserDiscordID());
            preparedStatement.setString(3, taskDAOAsJSON);
            preparedStatement.setString(4, baseObject.getTeamUUID());
            preparedStatement.setInt(5, baseObject.getTeamID());
            preparedStatement.setString(6, assignedUsersAsJSON);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error creating team task with UUID: " + baseObject.getUUID());
            throw new RuntimeException("DB Error", e);
        }

    }

    @Override
    public TeamTask findByID(int id) {
        String sql =
                """
                SELECT * FROM team_task WHERE id = ?;
                """;

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
                String taskDAOAsJSON = resultSet.getString("task_dao");
                String teamUUID = resultSet.getString("team_uuid");
                int teamID = resultSet.getInt("team_id");
                String assignedUsersAsJSON = resultSet.getString("assigned_users");

                Gson gson = new Gson();
                TaskDAO taskDAO = gson.fromJson(taskDAOAsJSON, TaskDAO.class);
                ArrayList<String> assignedUsers = gson.fromJson(assignedUsersAsJSON, LIST_TYPE);

                teamTask = new TeamTask(
                        uuid,
                        userDiscordID,
                        id,
                        taskDAO,
                        teamUUID,
                        teamID,
                        assignedUsers
                );
            }

        } catch (SQLException e) {
            System.out.println("Error find team task with ID: " + id);
            throw new RuntimeException("DB Error", e);
        }
        return teamTask;
    }

    @Override
    public TeamTask findByUUID(String uuid) {
        String sql =
                """
                SELECT * FROM team_task WHERE uuid = ?;
                """;

        TeamTask teamTask = null;

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
                ) {
            preparedStatement.setString(1, uuid);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String userDiscordID = resultSet.getString("user_discord_id");
                int id = resultSet.getInt("id");
                String taskDAOAsJSON = resultSet.getString("task_dao");
                String teamUUID = resultSet.getString("team_uuid");
                int teamID = resultSet.getInt("team_id");
                String assignedUsersAsJSON = resultSet.getString("assigned_users");

                Gson gson = new Gson();
                TaskDAO taskDAO = gson.fromJson(taskDAOAsJSON, TaskDAO.class);
                ArrayList<String> assignedUsers = gson.fromJson(assignedUsersAsJSON, LIST_TYPE);

                teamTask = new TeamTask(
                        uuid,
                        userDiscordID,
                        id,
                        taskDAO,
                        teamUUID,
                        teamID,
                        assignedUsers
                );
            }

        } catch (SQLException e) {
            System.out.println("Error find team task with UUID: " + uuid);
            throw new RuntimeException("DB Error", e);
        }
        return teamTask;
    }

    @Override
    public void update(TeamTask baseObject) {

        String sql =
                """
                        UPDATE team_task
                        SET task_dao = ?, assigned_users = ?
                        WHERE uuid = ?;
                        """;

        String teamTaskUUID = baseObject.getUUID();

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            Gson gson = new Gson();
            String taskDAOAsJSON = gson.toJson(baseObject.getTaskDAO());
            String assignedUsersAsJSON = gson.toJson(baseObject.getAssignedUsers());

            preparedStatement.setString(1, taskDAOAsJSON);
            preparedStatement.setString(2, assignedUsersAsJSON);
            preparedStatement.setString(3, teamTaskUUID);

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
        String sql =
                """
                        DELETE FROM team_task WHERE uuid = ?;
                        """;
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
    public ArrayList<TeamTask> readAll(String userDiscordID) {
        return null;
    }

    @Override
    public ArrayList<TeamTask> findAllByTeamId(int teamId) {

        String sql =
                """
                        SELECT * FROM team_task WHERE team_id = ? ORDER BY id ASC;
                        """;
        ArrayList<TeamTask> teamTasks = new ArrayList<>();

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, teamId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String userDiscordID = resultSet.getString("user_discord_id");
                int id = resultSet.getInt("id");
                String taskDAOAsJSON = resultSet.getString("task_dao");
                String teamUUID = resultSet.getString("team_uuid");
                String assignedUsersAsJSON = resultSet.getString("assigned_users");

                Gson gson = new Gson();
                TaskDAO taskDAO = gson.fromJson(taskDAOAsJSON, TaskDAO.class);
                ArrayList<String> assignedUsers = gson.fromJson(assignedUsersAsJSON, LIST_TYPE);

                teamTasks.add(
                        new TeamTask(
                                uuid,
                                userDiscordID,
                                id,
                                taskDAO,
                                teamUUID,
                                teamId,
                                assignedUsers
                        )
                );
            }

        } catch (SQLException e) {
            System.out.println("Error finding team tasks with team id: " + teamId);
            throw new RuntimeException("DB Error", e);
        }

        return teamTasks;
    }
}