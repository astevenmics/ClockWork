package mist.mystralix.infrastructure.repository.reminder;

import mist.mystralix.config.DBManager;
import mist.mystralix.domain.reminder.Reminder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

public class DBReminderRepository implements ReminderRepository {

    @Override
    public void create(Reminder reminder) {

        // Extract reminder fields
        String uuid = reminder.getUUID();
        String userDiscordID = reminder.getUserDiscordID();
        String reminderMessage = reminder.getMessage();
        long createdTimestamp = reminder.getCreatedTimestamp();
        long targetTimestamp = reminder.getTargetTimestamp();
        boolean isNotificationSent = reminder.isNotificationSent();

        String sqlStatement =
                "INSERT INTO reminders " +
                        "(uuid, user_discord_id, message, created_timestamp, target_timestamp, is_notification_sent) " +
                        "VALUES (?, ?, ?, ?, ?, ?);";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, userDiscordID);
            preparedStatement.setString(3, reminderMessage);
            preparedStatement.setLong(4, createdTimestamp);
            preparedStatement.setLong(5, targetTimestamp);
            preparedStatement.setBoolean(6, isNotificationSent);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding reminder to database.");
            throw new RuntimeException("DB Error", e);
        }
    }

    @Override
    public Reminder findByDiscordIDAndUUID(String userDiscordID, String uuid) {

        String sqlStatement = "SELECT * FROM reminders WHERE user_discord_id = ? AND uuid = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {

            preparedStatement.setString(1, userDiscordID);
            preparedStatement.setString(2, uuid);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String reminderMessage = resultSet.getString("message");
                long createdTimestamp = resultSet.getLong("created_timestamp");
                long targetTimestamp = resultSet.getLong("target_timestamp");
                int id = resultSet.getInt("id");
                boolean isNotificationSent = resultSet.getBoolean("is_notification_sent");

                return new Reminder(
                        uuid,
                        userDiscordID,
                        id,
                        reminderMessage,
                        createdTimestamp,
                        targetTimestamp,
                        isNotificationSent
                );
            }
            return null;

        } catch (SQLException e) {
            System.out.println("Error reading reminder from database.");
            throw new RuntimeException("DB Error", e);
        }
    }

    @Override
    public Reminder findByDiscordIDAndID(String userDiscordID, int id) {

        String sqlStatement = "SELECT * FROM reminders WHERE user_discord_id = ? AND id = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, userDiscordID);
            preparedStatement.setInt(2, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String reminderMessage = resultSet.getString("message");
                long createdTimestamp = resultSet.getLong("created_timestamp");
                long targetTimestamp = resultSet.getLong("target_timestamp");
                boolean isNotificationSent = resultSet.getBoolean("is_notification_sent");

                return new Reminder(
                        uuid,
                        userDiscordID,
                        id,
                        reminderMessage,
                        createdTimestamp,
                        targetTimestamp,
                        isNotificationSent
                );
            }
            return null;

        } catch (SQLException e) {
            System.out.println("Error reading reminder from database.");
            throw new RuntimeException("DB Error", e);
        }
    }

    @Override
    public Reminder findByID(int uuid) {
        return null;
    }

    @Override
    public Reminder findByUUID(String uuid) {
        return null;
    }

    @Override
    public void update(Reminder reminder) {

        int id = reminder.getId();
        String message = reminder.getMessage();
        long targetTimestamp = reminder.getTargetTimestamp();
        String userDiscordID = reminder.getUserDiscordID();
        String uuid = reminder.getUUID();

        String sqlStatement =
                "UPDATE reminders " +
                        "SET message = ?, target_timestamp = ? " +
                        "WHERE uuid = ? AND user_discord_id = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, message);
            preparedStatement.setLong(2, targetTimestamp);
            preparedStatement.setString(3, uuid);
            preparedStatement.setString(4, userDiscordID);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated == 0) {
                System.out.println("Reminder not found/updated");
            }

        } catch (SQLException e) {
            System.out.println("Error updating reminder ID: " + id + " for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
    }

    @Override
    public void delete(Reminder reminder) {
        String userDiscordID = reminder.getUserDiscordID();
        String uuid = reminder.getUUID();

        String sqlStatement = "DELETE FROM reminders WHERE uuid = ? AND user_discord_id = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, userDiscordID);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated == 0) {
                System.out.println("Reminder not found/deleted");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting reminder UUID: " + uuid + " for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
    }

    @Override
    public ArrayList<Reminder> readAll(String userDiscordID) {

        ArrayList<Reminder> reminders = new ArrayList<>();
        String sqlStatement = "SELECT * FROM reminders WHERE user_discord_id = ? ORDER BY id ASC;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {

            preparedStatement.setString(1, userDiscordID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String reminderMessage = resultSet.getString("message");
                int id = resultSet.getInt("id");
                long createdTimestamp = resultSet.getLong("created_timestamp");
                long targetTimestamp = resultSet.getLong("target_timestamp");
                boolean isNotificationSent = resultSet.getBoolean("is_notification_sent");

                reminders.add(
                        new Reminder(
                            uuid,
                            userDiscordID,
                                id,
                            reminderMessage,
                                createdTimestamp,
                            targetTimestamp,
                            isNotificationSent
                    )
                );
            }

            return reminders;

        } catch (SQLException e) {
            System.out.println("Error reading all reminders for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
    }

    @Override
    public HashSet<Reminder> getAllActiveReminders() {

        HashSet<Reminder> reminders = new HashSet<>();
        String sqlStatement = "SELECT * FROM reminders WHERE is_notification_sent = 0;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String userDiscordID = resultSet.getString("user_discord_id");
                int id = resultSet.getInt("id");
                String reminderMessage = resultSet.getString("message");
                long createdTimestamp = resultSet.getLong("created_timestamp");
                long targetTimestamp = resultSet.getLong("target_timestamp");
                boolean isNotificationSent = resultSet.getBoolean("is_notification_sent");

                reminders.add(
                    new Reminder(
                        uuid,
                        userDiscordID,
                            id,
                        reminderMessage,
                            createdTimestamp,
                        targetTimestamp,
                        isNotificationSent
                    )
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error reading all reminders", e);
        }

        return reminders;
    }

    @Override
    public void updateIsNotificationSent(Reminder reminder) {

        int id = reminder.getId();
        String userDiscordID = reminder.getUserDiscordID();
        String uuid = reminder.getUUID();
        boolean isNotificationSent = true;

        String sqlStatement = "UPDATE reminders SET is_notification_sent = ? " +
                        "WHERE uuid = ? AND user_discord_id = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setBoolean(1, isNotificationSent);
            preparedStatement.setString(2, uuid);
            preparedStatement.setString(3, userDiscordID);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated == 0) {
                System.out.println("Reminder not found/updated");
            }

        } catch (SQLException e) {
            System.out.println("Error updating reminder ID: " + id + " for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
    }
}