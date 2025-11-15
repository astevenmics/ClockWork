package mist.mystralix.Database;

import com.google.gson.Gson;
import mist.mystralix.Objects.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBHandler {

    private final DBManager DB_MANAGER;
    private static Connection CONNECTION;

    public DBHandler() {
        this.DB_MANAGER = new DBManager();
        CONNECTION = DB_MANAGER.getConnection();
    }

    // TODO | AddTask, UpdateTask[title, description, status], GetTasks, and GetTask

    public void addTask(Task task, String userID) {
        if (CONNECTION == null) {
            CONNECTION = DB_MANAGER.getConnection();
        }

        String sqlStatement = "INSERT OR IGNORE INTO tasks (userID, task) values (?, ?);";

        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(sqlStatement)) {
            Gson gson = new Gson();
            String json = gson.toJson(task);

            preparedStatement.setString(1, userID);
            preparedStatement.setString(2, json);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error adding task to database.");
        }
    }

    public void updateQuery() {
        if (CONNECTION == null) {
            CONNECTION = DB_MANAGER.getConnection();
        }

        try (Statement statement = CONNECTION.createStatement()) {
            // For now
            String query =
                    "CREATE TABLE IF NOT EXISTS tasks " +
                            "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "userID TEXT NOT NULL, " +
                            "task TEXT NOT NULL" +
                            ");";
            statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error executing query");
        }
    }

    public Task getTask(int id) {
        if (CONNECTION == null) {
            CONNECTION = DB_MANAGER.getConnection();
        }
        Task task = null;

        String sqlStatement = "SELECT * FROM tasks WHERE id = ?;";
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(sqlStatement)) {
            // For now

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String taskString = resultSet.getString("task");

                Gson gson = new Gson();
                task = gson.fromJson(taskString, Task.class);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error executing query");
        }
        return task;
    }

    public void initializeDatabaseTable() {
        if (CONNECTION == null) {
            CONNECTION = DB_MANAGER.getConnection();
        }

        // Dropping old table
        String query =
                "CREATE TABLE IF NOT EXISTS tasks " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "userID TEXT NOT NULL, " +
                        "task TEXT NOT NULL" +
                        ");";
        try (Statement statement = CONNECTION.createStatement()) {
            statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error executing query");
        }
    }

}