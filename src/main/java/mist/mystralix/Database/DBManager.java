package mist.mystralix.Database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBManager {

    protected static Connection connection;

    protected Connection getConnection() {

        try {
            File getFile = new File("db/database.db");
            if (!getFile.exists()) {
                boolean created = getFile.createNewFile();
                if(!created) {
                    System.out.println("Database File not created");
                }
            }
            String url = "jdbc:sqlite:" + getFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error connecting to database");
        }
        return connection;
    }

}