package mist.mystralix.config;

import mist.mystralix.utils.schema.Tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBSchemaInitializer {

    public void initializeDatabaseTable() {

        try (
                Connection connection = DBManager.getConnection();
                Statement statement = connection.createStatement()
        ) {
            // Create tables if missing
            for (String query : Tables.getTables()) {
                statement.execute(query);
            }

        } catch (SQLException e) {
            System.out.println("Error initializing database schema.");
            throw new RuntimeException("DB Error", e);
        }
    }

}