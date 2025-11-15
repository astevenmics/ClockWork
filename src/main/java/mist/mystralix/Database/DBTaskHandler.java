package mist.mystralix.Database;

import com.google.gson.Gson;
import mist.mystralix.Objects.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

public class DBTaskHandler {

    // Cleanup
    public void addTask(Task task, String userID) {

        String sqlStatement = "INSERT INTO tasks (uuid, userID, task) values (?, ?,  ?);";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            Gson gson = new Gson();
            String json = gson.toJson(task);

            preparedStatement.setString(1, UUID.randomUUID().toString());
            preparedStatement.setString(2, userID);
            preparedStatement.setString(3, json);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error adding task to database.");
            throw new RuntimeException("DB Error", e);
        }
    }

    public Task getTask(String userID, int taskID) {
        Task task;

        String sqlStatement = "SELECT * FROM tasks WHERE userID = ? AND taskID = ?;";
        try (Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            // For now

            preparedStatement.setString(1, userID);
            preparedStatement.setInt(2, taskID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String taskString = resultSet.getString("task");

                Gson gson = new Gson();
                task = gson.fromJson(taskString, Task.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error getting user: " + userID + ", task: " + taskID);
            throw new RuntimeException("DB Error", e);
        }
        return task;
    }

    public ArrayList<Task> getAllUserTasks(ArrayList<Task> userTasks, String userID) {
        String sqlStatement = "SELECT * FROM tasks WHERE userID = ? ORDER BY taskID ASC";
        try (Connection connection = DBManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            // For now

            preparedStatement.setString(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();

            Gson gson = new Gson();
            while (resultSet.next()) {
                String taskString = resultSet.getString("task");
                userTasks.add(gson.fromJson(taskString, Task.class));
            }
        } catch (Exception e) {
            System.out.println("Error getting user: " + userID);
            throw new RuntimeException("DB Error", e);
        }
        return userTasks;
    }

    public void cancelUserTask(String userID, int taskID, Task task) {
        Gson gson = new Gson();
        String taskString = gson.toJson(task);

        String sqlStatement = "UPDATE tasks SET task = ? WHERE userID = ? AND taskID = ?;";
        try (Connection connection = DBManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, taskString);
            preparedStatement.setString(2, userID);
            preparedStatement.setInt(3, taskID);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated <= 0) {
                System.out.println("Task not found");
            }
        } catch (Exception e) {
            System.out.println("Error cancelling user: " + userID);
            throw new RuntimeException("DB Error", e);
        }
    }

}