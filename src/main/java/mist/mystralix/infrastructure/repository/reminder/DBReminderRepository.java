package mist.mystralix.infrastructure.repository.reminder;

import mist.mystralix.config.DBManager;
import mist.mystralix.domain.reminder.Reminder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * MySQL-backed implementation of {@link ReminderRepository}.
 *
 * <p>This class performs CRUD operations for reminders, converting result rows
 * into {@link Reminder} domain objects and handling SQL statements and
 * connection management.</p>
 *
 * <p>All DB connections are retrieved from {@link DBManager}, ensuring
 * connection pooling through HikariCP.</p>
 */
public class DBReminderRepository implements ReminderRepository {

    /**
     * Inserts a new reminder into the database.
     *
     * @param reminder the reminder object containing all relevant fields
     */
    @Override
    public void create(Reminder reminder) {

        // Extract reminder fields
        String reminderUUID = reminder.getReminderUUID();
        String userDiscordID = reminder.getUserDiscordID();
        String reminderMessage = reminder.getMessage();
        long timestamp = reminder.getTargetTimestamp();
        boolean isNotificationSent = reminder.isNotificationSent();

        String sqlStatement =
                "INSERT INTO reminders " +
                        "(reminderUUID, userDiscordID, message, targetTimestamp, isNotificationSent) " +
                        "VALUES (?, ?, ?, ?, ?);";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, reminderUUID);
            preparedStatement.setString(2, userDiscordID);
            preparedStatement.setString(3, reminderMessage);
            preparedStatement.setLong(4, timestamp);
            preparedStatement.setBoolean(5, isNotificationSent);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding reminder to database.");
            throw new RuntimeException("DB Error", e);
        }
    }

    /**
     * Reads a reminder using UUID instead of numeric ID.
     *
     * <p>Useful for retrieving reminders when the UUID is used as the primary identifier.</p>
     *
     * @param userDiscordID the reminder owner
     * @param reminderUUID  the unique reminder UUID
     * @return a Reminder object or null if not found
     */
    @Override
    public Reminder findByUUID(String userDiscordID, String reminderUUID) {

        String sqlStatement = "SELECT * FROM reminders WHERE userDiscordID = ? AND reminderUUID = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {

            preparedStatement.setString(1, userDiscordID);
            preparedStatement.setString(2, reminderUUID);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String reminderMessage = resultSet.getString("message");
                long timestamp = resultSet.getLong("targetTimestamp");
                int reminderID = resultSet.getInt("reminderID");
                boolean isNotificationSent = resultSet.getBoolean("isNotificationSent");

                return new Reminder(
                        reminderUUID,
                        userDiscordID,
                        reminderID,
                        reminderMessage,
                        timestamp,
                        isNotificationSent
                );
            }
            return null;

        } catch (SQLException e) {
            System.out.println("Error reading reminder from database.");
            throw new RuntimeException("DB Error", e);
        }
    }

    /**
     * Reads a reminder based on user ID and numeric reminder ID.
     *
     * @param userDiscordID the Discord ID of the owner
     * @param reminderID    numeric reminder ID
     * @return a Reminder object or null if not found
     */
    @Override
    public Reminder findByID(String userDiscordID, int reminderID) {

        String sqlStatement = "SELECT * FROM reminders WHERE userDiscordID = ? AND reminderID = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, userDiscordID);
            preparedStatement.setInt(2, reminderID);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String reminderUUID = resultSet.getString("reminderUUID");
                String reminderMessage = resultSet.getString("message");
                long timestamp = resultSet.getLong("targetTimestamp");
                boolean isNotificationSent = resultSet.getBoolean("isNotificationSent");

                return new Reminder(
                        reminderUUID,
                        userDiscordID,
                        reminderID,
                        reminderMessage,
                        timestamp,
                        isNotificationSent
                );
            }
            return null;

        } catch (SQLException e) {
            System.out.println("Error reading reminder from database.");
            throw new RuntimeException("DB Error", e);
        }
    }

    /**
     * Updates a reminder's message or timestamp.
     *
     * @param reminder the updated reminder object
     */
    @Override
    public void update(Reminder reminder) {

        int reminderID = reminder.getReminderID();
        String message = reminder.getMessage();
        long targetTimestamp = reminder.getTargetTimestamp();
        String userDiscordID = reminder.getUserDiscordID();
        String reminderUUID = reminder.getReminderUUID();

        String sqlStatement =
                "UPDATE reminders " +
                        "SET message = ?, targetTimestamp = ? " +
                        "WHERE reminderUUID = ? AND userDiscordID = ?;";

        try (
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

        } catch (SQLException e) {
            System.out.println("Error updating reminder ID: " + reminderID + " for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
    }

    /**
     * Deletes a reminder using UUID and user ID.
     *
     * @param reminder Reminder object to be deleted
     */
    @Override
    public void delete(Reminder reminder) {
        String userDiscordID = reminder.getUserDiscordID();
        String reminderUUID = reminder.getReminderUUID();

        String sqlStatement = "DELETE FROM reminders WHERE reminderUUID = ? AND userDiscordID = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setString(1, reminderUUID);
            preparedStatement.setString(2, userDiscordID);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated == 0) {
                System.out.println("Reminder not found/deleted");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting reminder UUID: " + reminderUUID + " for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
    }

    /**
     * Reads all reminders belonging to a specific user.
     *
     * @param userDiscordID owner ID
     * @return a list of reminder objects
     */
    @Override
    public ArrayList<Reminder> readAll(String userDiscordID) {

        ArrayList<Reminder> reminders = new ArrayList<>();
        String sqlStatement = "SELECT * FROM reminders WHERE userDiscordID = ? ORDER BY reminderID ASC;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {

            preparedStatement.setString(1, userDiscordID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String reminderUUID = resultSet.getString("reminderUUID");
                String reminderMessage = resultSet.getString("message");
                int reminderID = resultSet.getInt("reminderID");
                long targetTimestamp = resultSet.getLong("targetTimestamp");
                boolean isNotificationSent = resultSet.getBoolean("isNotificationSent");

                reminders.add(
                        new Reminder(
                            reminderUUID,
                            userDiscordID,
                            reminderID,
                            reminderMessage,
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

    /**
     * Retrieves ALL active reminders (not yet notified).
     *
     * <p>Used by the reminder scheduler on bot startup.</p>
     */
    @Override
    public HashSet<Reminder> getAllActiveReminders() {

        HashSet<Reminder> reminders = new HashSet<>();
        String sqlStatement = "SELECT * FROM reminders WHERE isNotificationSent = 0;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String reminderUUID = resultSet.getString("reminderUUID");
                String userDiscordID = resultSet.getString("userDiscordID");
                int reminderID = resultSet.getInt("reminderID");
                String reminderMessage = resultSet.getString("message");
                long targetTimestamp = resultSet.getLong("targetTimestamp");
                boolean isNotificationSent = resultSet.getBoolean("isNotificationSent");

                reminders.add(
                    new Reminder(
                        reminderUUID,
                        userDiscordID,
                        reminderID,
                        reminderMessage,
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

    /**
     * Marks a reminder as "notification sent".
     *
     * @param reminder the reminder instance to update
     */
    @Override
    public void updateIsNotificationSent(Reminder reminder) {

        int reminderID = reminder.getReminderID();
        String userDiscordID = reminder.getUserDiscordID();
        String reminderUUID = reminder.getReminderUUID();
        boolean isNotificationSent = true;

        String sqlStatement = "UPDATE reminders SET isNotificationSent = ? " +
                        "WHERE reminderUUID = ? AND userDiscordID = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setBoolean(1, isNotificationSent);
            preparedStatement.setString(2, reminderUUID);
            preparedStatement.setString(3, userDiscordID);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated == 0) {
                System.out.println("Reminder not found/updated");
            }

        } catch (SQLException e) {
            System.out.println("Error updating reminder ID: " + reminderID + " for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
    }
}
