package mist.mystralix.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Responsible for initializing and validating the database schema required
 * for the application to function.
 *
 * <p>This class ensures that the following tables exist:</p>
 * <ul>
 *     <li><strong>tasks</strong> – stores user task entries</li>
 *     <li><strong>reminders</strong> – stores scheduled user reminders</li>
 * </ul>
 *
 * <p>If the tables do not exist, they are created automatically using
 * {@code CREATE TABLE IF NOT EXISTS} statements. This allows the bot to start
 * without requiring manual database setup.</p>
 *
 * <p>This class should typically be called once at application startup.</p>
 */
public class DBSchemaInitializer {

    /**
     * Initializes the required database tables if they do not already exist.
     *
     * <p>This method creates both the {@code tasks} and {@code reminders} tables with
     * strict column definitions and auto-incrementing numeric IDs for user-friendly
     * reference.</p>
     *
     * <h3>Table: tasks</h3>
     * <pre>
     * taskUUID          VARCHAR(50) PRIMARY KEY
     * userDiscordID     VARCHAR(50) NOT NULL
     * taskID            BIGINT AUTO_INCREMENT UNIQUE
     * taskDAO           VARCHAR(8192) NOT NULL
     * </pre>
     *
     * <h3>Table: reminders</h3>
     * <pre>
     * reminderUUID       VARCHAR(50) PRIMARY KEY
     * userDiscordID      VARCHAR(50) NOT NULL
     * reminderID         BIGINT AUTO_INCREMENT UNIQUE
     * message            VARCHAR(8192) NOT NULL
     * targetTimestamp    BIGINT NOT NULL
     * isNotificationSent BOOLEAN NOT NULL (default FALSE)
     * </pre>
     *
     * <p>Errors during table creation are wrapped into a {@link RuntimeException}
     * to prevent the bot from starting in an invalid state.</p>
     */
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