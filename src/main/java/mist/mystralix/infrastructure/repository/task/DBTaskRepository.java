package mist.mystralix.infrastructure.repository.task;

import com.google.gson.Gson;
import mist.mystralix.config.DBManager;
import mist.mystralix.domain.task.Task;
import mist.mystralix.domain.task.TaskDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBTaskRepository implements TaskRepository {

    @Override
    public void create(Task task) {
        String uuidAsString = task.getUUID();
        String userDiscordID = task.getUserDiscordID();
        TaskDAO taskDAO = task.getTaskDAO();

        String sqlStatement =
                "INSERT INTO tasks (uuid, userDiscordID, taskDAO) VALUES (?, ?, ?);";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            Gson gson = new Gson();
            String taskDAOJson = gson.toJson(taskDAO); // Convert DAO to JSON for DB storage

            preparedStatement.setString(1, uuidAsString);
            preparedStatement.setString(2, userDiscordID);
            preparedStatement.setString(3, taskDAOJson);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding task to database.");
            throw new RuntimeException("DB Error", e);
        }
    }

    @Override
    public Task findByDiscordIDAndUUID(String userDiscordID, String uuidAsString) {
        Task task = null;

        String sqlStatement = "SELECT * FROM tasks WHERE userDiscordID = ? AND uuid = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, userDiscordID);
            preparedStatement.setString(2, uuidAsString);

            ResultSet resultSet = preparedStatement.executeQuery();
            Gson gson = new Gson();

            if (resultSet.next()) {
                int taskID = resultSet.getInt("taskID");
                String taskDAOJson = resultSet.getString("taskDAO");

                TaskDAO taskDAO = gson.fromJson(taskDAOJson, TaskDAO.class);

                task = new Task(
                        uuidAsString,
                        userDiscordID,
                        taskID,
                        taskDAO
                );
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving task with UUID: " + uuidAsString);
            throw new RuntimeException("DB Error", e);
        }

        return task;
    }

    @Override
    public Task findByDiscordIDAndID(String userDiscordID, int taskID) {
        Task task = null;

        String sqlStatement = "SELECT * FROM tasks WHERE userDiscordID = ? AND taskID = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, userDiscordID);
            preparedStatement.setInt(2, taskID);

            ResultSet resultSet = preparedStatement.executeQuery();
            Gson gson = new Gson();

            if (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String taskDAOJson = resultSet.getString("taskDAO");

                TaskDAO taskDAO = gson.fromJson(taskDAOJson, TaskDAO.class);

                task = new Task(
                        uuid,
                        userDiscordID,
                        taskID,
                        taskDAO
                );
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving user: " + userDiscordID + ", taskID: " + taskID);
            throw new RuntimeException("DB Error", e);
        }

        return task;
    }

    @Override
    public Task findByUUID(String uuid) {
        Task task = null;

        String sqlStatement = "SELECT * FROM tasks WHERE uuid = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, uuid);

            ResultSet resultSet = preparedStatement.executeQuery();
            Gson gson = new Gson();

            if (resultSet.next()) {
                int taskID = resultSet.getInt("taskID");
                String taskDAOJson = resultSet.getString("taskDAO");
                String userDiscordID = resultSet.getString("userDiscordID");

                TaskDAO taskDAO = gson.fromJson(taskDAOJson, TaskDAO.class);

                task = new Task(
                        uuid,
                        userDiscordID,
                        taskID,
                        taskDAO
                );
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving task with ID: " + uuid);
            throw new RuntimeException("DB Error", e);
        }

        return task;
    }

    @Override
    public ArrayList<Task> readAll(String userDiscordID) {
        ArrayList<Task> userTasks = new ArrayList<>();

        String sqlStatement =
                "SELECT * FROM tasks WHERE userDiscordID = ? ORDER BY taskID ASC;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, userDiscordID);

            ResultSet resultSet = preparedStatement.executeQuery();
            Gson gson = new Gson();

            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                int taskID = resultSet.getInt("taskID");
                String taskDAOJson = resultSet.getString("taskDAO");

                TaskDAO taskDAO = gson.fromJson(taskDAOJson, TaskDAO.class);

                Task task = new Task(
                        uuid,
                        userDiscordID,
                        taskID,
                        taskDAO
                );

                userTasks.add(task);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving all tasks for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }

        return userTasks;
    }

    @Override
    public void update(Task task) {
        String userDiscordID = task.getUserDiscordID();
        int taskID = task.getTaskID();
        String sqlStatement =
                "UPDATE tasks SET taskDAO = ? WHERE userDiscordID = ? AND taskID = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            Gson gson = new Gson();
            String taskDAOJson = gson.toJson(task.getTaskDAO()); // Convert DAO back to JSON

            preparedStatement.setString(1, taskDAOJson);
            preparedStatement.setString(2, userDiscordID);
            preparedStatement.setInt(3, taskID);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("Task not found for update: user=" + userDiscordID + ", taskID=" + taskID);
            }

        } catch (SQLException e) {
            System.out.println("Error updating task ID: " + taskID + " for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
    }

    @Override
    public void delete(Task task) {
        String userDiscordID = task.getUserDiscordID();
        String sqlStatement =
                "DELETE FROM tasks WHERE uuid = ? AND userDiscordID = ?;";

        String uuid = task.getUUID();

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, userDiscordID);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted == 0) {
                System.out.println("No task deleted. UUID not found: " + uuid);
            }

        } catch (SQLException e) {
            System.out.println("Error deleting task UUID: " + uuid + " for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
    }
}