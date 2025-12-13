package mist.mystralix.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

// TODO: Use Constants
public class DBSchemaInitializer {

    public void initializeDatabaseTable() {

        // TODO: update table names
        // TODO: Turn taskDAO into JSON
        String tasksTableQuery =
                "CREATE TABLE IF NOT EXISTS tasks (" +
                        "taskUUID VARCHAR(50) PRIMARY KEY, " +
                        "userDiscordID VARCHAR(50) NOT NULL, " +
                        "taskID BIGINT NOT NULL AUTO_INCREMENT, " +
                        "taskDAO VARCHAR(8192) NOT NULL, " +
                        "UNIQUE KEY(taskID)" +
                        ");";

        // TODO: update table names
        String remindersTableQuery =
                "CREATE TABLE IF NOT EXISTS reminders (" +
                        "reminderUUID VARCHAR(50) PRIMARY KEY, " +
                        "userDiscordID VARCHAR(50) NOT NULL, " +
                        "reminderID BIGINT NOT NULL AUTO_INCREMENT, " +
                        "message VARCHAR(8192) NOT NULL, " +
                        "targetTimestamp BIGINT NOT NULL, " +
                        "isNotificationSent BOOLEAN NOT NULL DEFAULT FALSE, " +
                        "UNIQUE KEY(reminderID)" +
                        ");";

        String teamsTableQuery =
                "CREATE TABLE IF NOT EXISTS teams (" +
                        "uuid VARCHAR(50) PRIMARY KEY, " +
                        "id BIGINT NOT NULL AUTO_INCREMENT, " +
                        "team_name VARCHAR(50) NOT NULL, " +
                        "moderators JSON NOT NULL, " +
                        "members JSON NOT NULL, " +
                        "tasks_uuid JSON NOT NULL, " +
                        "UNIQUE KEY(id)" +
                        ");";

        try (
                Connection connection = DBManager.getConnection();
                Statement statement = connection.createStatement()
        ) {
            // Create tables if missing
            statement.executeUpdate(tasksTableQuery);
            statement.executeUpdate(remindersTableQuery);
            statement.executeUpdate(teamsTableQuery);

        } catch (SQLException e) {
            System.out.println("Error initializing database schema.");
            throw new RuntimeException("DB Error", e);
        }
    }

}