package mist.mystralix.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBSchemaInitializer {

    public void initializeDatabaseTable() {

        String tasksTable =
                "CREATE TABLE IF NOT EXISTS tasks " +
                        "(uuid VARCHAR(50) PRIMARY KEY," +
                        "userID VARCHAR(50) NOT NULL, " +
                        "taskID BIGINT NOT NULL AUTO_INCREMENT," +
                        "task VARCHAR(8192) NOT NULL," +
                        "UNIQUE KEY(taskID)" +
                        ");";
        try (Connection connection = DBManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(tasksTable);
        } catch (SQLException e) {
            System.out.println("Error initializing task table into the database");
            throw new RuntimeException("DB Error", e);
        }
    }

}