package mist.mystralix.Database;

import mist.mystralix.Objects.Reminder.Reminder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

public class DBReminderHandler {

    public void create(Reminder reminder) {

        String reminderUUID = reminder.reminderUUID;
        String userDiscordID = reminder.userDiscordID;
        String reminderMessage = reminder.message;
        long timestamp = reminder.targetTimestamp;

        String sqlStatement = "INSERT INTO reminders " +
                "(reminderUUID, userDiscordID, message, targetTimestamp) " +
                "VALUES (?, ?, ?, ?);";

        try(
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, reminderUUID);
            preparedStatement.setString(2, userDiscordID);
            preparedStatement.setString(3, reminderMessage);
            preparedStatement.setLong(4, timestamp);

            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("Error adding reminder to database.");
            throw new RuntimeException("DB Error", e);
        }

    }

    public Reminder read(String userDiscordID, int reminderID) {

        String sqlStatement = "SELECT * FROM reminders WHERE userDiscordID = ? AND reminderID = ?;";

        try(
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, userDiscordID);
            preparedStatement.setInt(2, reminderID);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next() ) {
                String reminderUUID = resultSet.getString("reminderUUID");
                String reminderMessage = resultSet.getString("message");
                long timestamp = resultSet.getLong("targetTimestamp");

                return new Reminder(
                        reminderUUID,
                        userDiscordID,
                        reminderID,
                        reminderMessage,
                        timestamp
                );

            } else {
                return null;
            }

        }
        catch (SQLException e) {
            System.out.println("Error getting reminder to database.");
            throw new RuntimeException("DB Error", e);
        }

    }

    public Reminder read(String userDiscordID, String reminderUUID) {

        String sqlStatement = "SELECT * FROM reminders WHERE userDiscordID = ? AND reminderUUID = ?;";

        try(
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, userDiscordID);
            preparedStatement.setString(2, reminderUUID);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next() ) {
                String reminderMessage = resultSet.getString("message");
                long timestamp = resultSet.getLong("targetTimestamp");
                int reminderID = resultSet.getInt("reminderID");

                return new Reminder(
                        reminderUUID,
                        userDiscordID,
                        reminderID,
                        reminderMessage,
                        timestamp
                );

            } else {
                return null;
            }

        }
        catch (SQLException e) {
            System.out.println("Error getting reminder to database.");
            throw new RuntimeException("DB Error", e);
        }

    }

    public void update(Reminder reminder) {

        int reminderID = reminder.reminderID;
        String message = reminder.message;
        long targetTimestamp = reminder.targetTimestamp;
        String userDiscordID = reminder.userDiscordID;
        String reminderUUID = reminder.reminderUUID;

        String sqlStatement =
                "UPDATE reminders " +
                "SET message = ?, targetTimestamp = ? " +
                "WHERE reminderUUID = ? AND userDiscordID = ?;";

        try(
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, message);
            preparedStatement.setLong(2, targetTimestamp);
            preparedStatement.setString(3, reminderUUID);
            preparedStatement.setString(4, userDiscordID);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("Reminder not found/updated");
            }

        }
        catch (SQLException e) {
            System.out.println("Error updating reminder ID: " + reminderID + "for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }

    }

    public void delete(String reminderUUID, String userDiscordID) {

        String sqlStatement =
                "DELETE FROM reminders WHERE reminderUUID = ? AND userDiscordID = ?;";

        try(
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, reminderUUID);
            preparedStatement.setString(2, userDiscordID);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("Reminder not found/deleted");
            }

        }
        catch (SQLException e) {
            System.out.println("Error deleting reminder UUID: " + reminderUUID + "for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }

    }

    public ArrayList<Reminder> readAll(String userDiscordID) {

        ArrayList<Reminder> reminders = new ArrayList<>();
        String sqlStatement = "SELECT * FROM reminders WHERE userDiscordID = ? ORDER BY reminderID ASC;";

        try(
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, userDiscordID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                String reminderUUID = resultSet.getString("reminderUUID");
                String reminderMessage = resultSet.getString("message");
                int reminderID = resultSet.getInt("reminderID");
                long targetTimestamp = resultSet.getLong("targetTimestamp");

                reminders.add(
                    new Reminder(
                        reminderUUID,
                        userDiscordID,
                        reminderID,
                        reminderMessage,
                        targetTimestamp
                    )
                );
            }
            return reminders;
        }
        catch (SQLException e) {
            System.out.println("Error reading all reminders for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }

    }

    public HashSet<Reminder> getAllReminders() {
        HashSet<Reminder> reminders = new HashSet<>();
        String sqlStatement = "SELECT * FROM reminders;";
        try(
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
                ) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                String reminderUUID = resultSet.getString("reminderUUID");
                String userDiscordID = resultSet.getString("userDiscordID");
                int reminderID = resultSet.getInt("reminderID");
                String reminderMessage = resultSet.getString("message");
                long targetTimestamp = resultSet.getLong("targetTimestamp");
                reminders.add(
                        new Reminder(
                               reminderUUID,
                               userDiscordID,
                               reminderID,
                               reminderMessage,
                               targetTimestamp
                        )
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error reading all reminders", e);
        }
        return reminders;
    }


}