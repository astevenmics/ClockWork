package mist.mystralix.config;

import mist.mystralix.utils.Constants;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBSchemaInitializer {

    public void initializeDatabaseTable() {
    List<?> constantsList = Constants.TABLES.getValue(List.class);
    List<String> tableQueries = constantsList.stream()
            .map(String::valueOf)
            .toList();

        try (
                Connection connection = DBManager.getConnection();
                Statement statement = connection.createStatement()
        ) {
            // Create tables if missing
            for (String query : tableQueries) {
                statement.execute(query);
            }

        } catch (SQLException e) {
            System.out.println("Error initializing database schema.");
            throw new RuntimeException("DB Error", e);
        }
    }

}