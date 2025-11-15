package mist.mystralix.Database;

import com.google.gson.Gson;
import mist.mystralix.Objects.Task;

import java.sql.*;
import java.util.ArrayList;

public class DBHandler {

    private final DBManager DB_MANAGER;
    private static Connection CONNECTION;

    public DBHandler() {
        this.DB_MANAGER = new DBManager();
        CONNECTION = DB_MANAGER.getConnection();
    }

    // TODO | AddTask, UpdateTask[title, description, status], GetTasks, and GetTask
    // Cleanup

    public void addTask(Task task, String userID) {
        if (CONNECTION == null) {
            CONNECTION = DB_MANAGER.getConnection();
        }

        String sqlStatement = "INSERT INTO tasks (userID, taskID, task) values (?, ?, ?);";

        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(sqlStatement)) {
            Gson gson = new Gson();
            String json = gson.toJson(task);
            int newUserGlobalCounter = getUserTaskCounter(userID) + 1;

            preparedStatement.setString(1, userID);
            preparedStatement.setInt(2, newUserGlobalCounter);
            preparedStatement.setString(3, json);
            preparedStatement.executeUpdate();

            updateUserTaskCounterDatabase(userID, newUserGlobalCounter);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error adding task to database.");
        }
    }

    public Task getTask(String userID, int taskID) {
        if (CONNECTION == null) {
            CONNECTION = DB_MANAGER.getConnection();
        }
        Task task = null;

        String sqlStatement = "SELECT * FROM tasks WHERE userID = ? AND taskID = ?;";
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(sqlStatement)) {
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
            System.out.println(e.getMessage());
            System.out.println("Error getting user: " + userID + ", task: " + taskID);
        }
        return task;
    }

    public ArrayList<Task> getAllUserTasks(String userID) {
        if (CONNECTION == null) {
            CONNECTION = DB_MANAGER.getConnection();
        }
        ArrayList<Task> task = new  ArrayList<>();

        String sqlStatement = "SELECT * FROM tasks WHERE userID = ?";
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(sqlStatement)) {
            // For now

            preparedStatement.setString(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();

            Gson gson = new Gson();
            while (resultSet.next()) {
                String taskString = resultSet.getString("task");
                task.add(gson.fromJson(taskString, Task.class));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error getting user: " + userID);
        }
        return task;
    }

    public void cancelUserTask(String userID, int taskID, Task task) {
        if (CONNECTION == null) {
            CONNECTION = DB_MANAGER.getConnection();
        }

        Gson gson = new Gson();
        String taskString = gson.toJson(task);

        String sqlStatement = "UPDATE tasks SET task = ? WHERE userID = ? AND taskID = ?;";
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, taskString);
            preparedStatement.setString(2, userID);
            preparedStatement.setInt(3, taskID);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated <= 0) {
                System.out.println("Task not found");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error cancelling user: " + userID);
        }
    }

    public int getUserTaskCounter(String userID) {
        if (CONNECTION == null) {
            CONNECTION = DB_MANAGER.getConnection();
        }

        int taskCounter = 0;

        String sqlStatement = "SELECT * FROM taskCounters WHERE userID = ?;";
        try(PreparedStatement preparedStatement = CONNECTION.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, userID);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                taskCounter = resultSet.getInt("taskCounter");
            } else {
                addToTaskCounterDatabase(userID);
                return 0;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error getting user: " + userID + " task counter.");
        }
        return taskCounter;
    }

    private void addToTaskCounterDatabase(String userID) {
        if (CONNECTION == null) {
            CONNECTION = DB_MANAGER.getConnection();
        }
        String sqlQuery = "INSERT OR IGNORE INTO taskCounters (userID, taskCounter) VALUES (?, ?);";
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, userID);
            preparedStatement.setInt(2, 0);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error adding user counter for: " + userID + " to database.");
        }
    }

    private void updateUserTaskCounterDatabase(String userID, int newTaskCounter) {
        if (CONNECTION == null) {
            CONNECTION = DB_MANAGER.getConnection();
        }
        String sqlQuery = "UPDATE taskCounters SET taskCounter = ? WHERE userID = ?;";
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(sqlQuery)) {
            preparedStatement.setInt(1, newTaskCounter);
            preparedStatement.setString(2, userID);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("No user found with userID: " + userID);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Error adding user counter for: " + userID + " to database.");
        }
    }

    public void initializeDatabaseTable() {
        if (CONNECTION == null) {
            CONNECTION = DB_MANAGER.getConnection();
        }

        String tasksTable =
                "CREATE TABLE IF NOT EXISTS tasks " +
                        "(globalTaskCounter INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "userID TEXT NOT NULL, " +
                        "taskID INTEGER NOT NULL," +
                        "task TEXT NOT NULL" +
                        ");";
        String counterTable =
                "CREATE TABLE IF NOT EXISTS taskCounters " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "userID TEXT NOT NULL, " +
                        "taskCounter INTEGER NOT NULL" +
                        ");";
        try (Statement statement = CONNECTION.createStatement()) {
            statement.executeUpdate(tasksTable);
            statement.executeUpdate(counterTable);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error executing query");
        }
    }

}