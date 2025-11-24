package mist.mystralix.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBSchemaInitializer {

    /*
        initializeDatabaseTable
        - Initializes the table in the database by ensuring that the tasks table exists
        - Table Name: task
        - Parameters:
            * uuid | VARCHAR 50 | 50-character limit
                - Contains the unique UUID identifier for the row
            * userDiscordID | VARCHAR 50 | 50-character limit
                - Contains the user's discord ID
            * taskID | BIGINT and AUTO_INCREMENT
                - Contains the unique identifier for tasks callable by users
            * task | VARCHAR 8192 | 8192-character limit
                - Contains the TEXT/String version of the JSON object Task
    */
    public void initializeDatabaseTable() {
        /*
            - SQL Query for MySQL to check/create the tasks table
            - Uses uuid parameter as the unique identifier for the row
            - Uses userDiscordID parameter as the container for the user's discord ID
            - Uses taskID parameter as the container for the task counter callable by users
            - Uses task parameter as a container for the task object
        */
        String tasksTableQuery =
                "CREATE TABLE IF NOT EXISTS tasks " +
                        "(taskUUID VARCHAR(50) PRIMARY KEY," +
                        "userDiscordID VARCHAR(50) NOT NULL, " +
                        "taskID BIGINT NOT NULL AUTO_INCREMENT," +
                        "taskDAO VARCHAR(8192) NOT NULL," +
                        "UNIQUE KEY(taskID)" +
                        ");";
        String remindersTableQuery =
                "CREATE TABLE IF NOT EXISTS reminders " +
                        "(reminderUUID VARCHAR(50) PRIMARY KEY," +
                        "userDiscordID VARCHAR(50) NOT NULL, " +
                        "reminderID BIGINT NOT NULL AUTO_INCREMENT," +
                        "message VARCHAR(8192) NOT NULL," +
                        "targetTimestamp BIGINT NOT NULL," +
                        "UNIQUE KEY(reminderID)" +
                        ");";
        try (
                Connection connection = DBManager.getConnection();
                Statement statement = connection.createStatement()
        ) {
            /*
                Executes the statement pushing it into the database
            */
            statement.executeUpdate(tasksTableQuery);
            statement.executeUpdate(remindersTableQuery);
        } catch (SQLException e) {
            System.out.println("Error initializing task table into the database");
            throw new RuntimeException("DB Error", e);
        }
    }

}