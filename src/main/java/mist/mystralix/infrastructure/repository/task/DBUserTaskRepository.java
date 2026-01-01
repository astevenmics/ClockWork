package mist.mystralix.infrastructure.repository.task;

import mist.mystralix.config.DBManager;
import mist.mystralix.domain.task.UserTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBUserTaskRepository implements UserTaskRepository {

    @Override
    public void create(UserTask userTask) {

        String sqlStatement =
                """
                        INSERT INTO user_tasks
                        (uuid, user_discord_id, title, description, status)
                        VALUES (?, ?, ?, ?, ?);
                        """;

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {

            preparedStatement.setString(1, userTask.getUUID());
            preparedStatement.setString(2, userTask.getUserDiscordID());
            preparedStatement.setString(3, userTask.getTitle());
            preparedStatement.setString(4, userTask.getDescription());
            preparedStatement.setInt(5, userTask.getStatus());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding userTask to database.");
            throw new RuntimeException("DB Error", e);
        }
    }

    @Override
    public UserTask findByID(int id) {
        UserTask userTask = null;

        String sqlStatement = "SELECT * FROM user_tasks WHERE id = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String userDiscordID = resultSet.getString("user_discord_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                Integer status = resultSet.getInt("status");

                userTask = new UserTask.Builder(uuid)
                        .userDiscordID(userDiscordID)
                        .id(id)
                        .title(title)
                        .description(description)
                        .status(status)
                        .build();
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving userTask with taskID: " + id);
            throw new RuntimeException("DB Error", e);
        }

        return userTask;
    }

    @Override
    public UserTask findByUUID(String uuid) {
        UserTask userTask = null;

        String sqlStatement = "SELECT * FROM user_tasks WHERE uuid = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, uuid);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String userDiscordID = resultSet.getString("user_discord_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                Integer status = resultSet.getInt("status");

                userTask = new UserTask.Builder(uuid)
                        .userDiscordID(userDiscordID)
                        .id(id)
                        .title(title)
                        .description(description)
                        .status(status)
                        .build();
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving userTask with ID: " + uuid);
            throw new RuntimeException("DB Error", e);
        }

        return userTask;
    }

    @Override
    public ArrayList<UserTask> readAll(String userDiscordID) {
        ArrayList<UserTask> userTasks = new ArrayList<>();

        String sqlStatement = "SELECT * FROM user_tasks WHERE user_discord_id = ? ORDER BY id ASC;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, userDiscordID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                Integer status = resultSet.getInt("status");

                UserTask userTask = new UserTask.Builder(uuid)
                        .userDiscordID(userDiscordID)
                        .id(id)
                        .title(title)
                        .description(description)
                        .status(status)
                        .build();

                userTasks.add(userTask);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving all tasks for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }

        return userTasks;
    }

    @Override
    public void update(UserTask userTask) {

        String userDiscordID = userTask.getUserDiscordID();
        int id = userTask.getId();

        String sqlStatement =
                """
                        UPDATE user_tasks
                        SET title = ?, description = ?, status = ?
                        WHERE user_discord_id = ? AND id = ?;
                        """;

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, userTask.getTitle());
            preparedStatement.setString(2, userTask.getDescription());
            preparedStatement.setInt(3, userTask.getStatus());
            preparedStatement.setString(4, userDiscordID);
            preparedStatement.setInt(5, id);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("UserTask not found for update: user=" + userDiscordID + ", taskID=" + id);
            }

        } catch (SQLException e) {
            System.out.println("Error updating userTask ID: " + id + " for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
    }

    @Override
    public void delete(UserTask userTask) {
        String userDiscordID = userTask.getUserDiscordID();
        String sqlStatement = "DELETE FROM user_tasks WHERE uuid = ? AND user_discord_id = ?;";

        String uuid = userTask.getUUID();

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, userDiscordID);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted == 0) {
                System.out.println("No userTask deleted. UUID not found: " + uuid);
            }

        } catch (SQLException e) {
            System.out.println("Error deleting userTask UUID: " + uuid + " for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
    }
}