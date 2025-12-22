package mist.mystralix.utils;

import java.util.List;

public enum Constants {

    TABLES(List.of(
            "CREATE TABLE IF NOT EXISTS tasks (" +
                    "uuid VARCHAR(50) PRIMARY KEY, " +
                    "user_discord_id VARCHAR(50) NOT NULL, " +
                    "id BIGINT NOT NULL AUTO_INCREMENT, " +
                    "task_dao JSON NOT NULL DEFAULT (JSON_ARRAY()), " +
                    "UNIQUE KEY(id)" +
                    ");",
            "CREATE TABLE IF NOT EXISTS reminders (" +
                    "uuid VARCHAR(50) PRIMARY KEY, " +
                    "user_discord_id VARCHAR(50) NOT NULL, " +
                    "id BIGINT NOT NULL AUTO_INCREMENT, " +
                    "message VARCHAR(8192) NOT NULL, " +
                    "target_timestamp BIGINT NOT NULL, " +
                    "is_notification_sent BOOLEAN NOT NULL DEFAULT FALSE, " +
                    "UNIQUE KEY(id)" +
                    ");",
            "CREATE TABLE IF NOT EXISTS teams (" +
                    "uuid VARCHAR(50) PRIMARY KEY, " +
                    "id BIGINT NOT NULL AUTO_INCREMENT, " +
                    "team_name VARCHAR(50) NOT NULL, " +
                    "team_leader VARCHAR(50) NOT NULL, " +
                    "moderators JSON NOT NULL DEFAULT (JSON_ARRAY()), " +
                    "members JSON NOT NULL DEFAULT (JSON_ARRAY()), " +
                    "tasks_uuid JSON NOT NULL DEFAULT (JSON_ARRAY()), " +
                    "team_invitations JSON NOT NULL DEFAULT (JSON_ARRAY()), " +
                    "UNIQUE KEY(id)" +
                    ");",
            "CREATE TABLE IF NOT EXISTS team_task (" +
                    "uuid VARCHAR(50) PRIMARY KEY, " +
                    "user_discord_id VARCHAR(50) NOT NULL, " +
                    "id BIGINT NOT NULL AUTO_INCREMENT, " +
                    "task_dao JSON NOT NULL DEFAULT (JSON_ARRAY()), " +
                    "team_uuid VARCHAR(50) NOT NULL, " +
                    "team_id VARCHAR(50) NOT NULL, " +
                    "assigned_users JSON NOT NULL DEFAULT (JSON_ARRAY()), " +
                    "UNIQUE KEY(id)" +
                    ");"
    )),
    MISSING_PARAMETERS("Please provide all the necessary parameters."),
    OBJECT_NOT_FOUND("The %s does not exist."),
    REMINDER_INVALID_TIME_INPUT("Invalid reminder time provided.\nExample: 1d, 1d20h, 20h15m..."),
    REMINDER_MINIMUM_TIME_INPUT("Time target/duration must be at least 1 minute."),
    NO_OBJECT_FOUND("No object found of %s with ID: %s"),
    USER_ALREADY_INVITED("The user, %s, has already been invited."),
    USER_ALREADY_PART_OF_THE_TEAM("You are already part of the **%s** team"),
    USER_MENTIONED_CANNOT_BE_USER("You cannot mention yourself in this command. Please choose a different user."),
    USER_MENTIONED_CANNOT_BE_BOT("You cannot mention a bot in this command. Please choose a user."),
    USER_MENTIONED_ALREADY_PART_OF_THE_TEAM("The user, %s, is already part of the %s team"),
    USER_MENTIONED_ALREADY_HAS_THE_POSITION_IN_THE_TEAM("The user, %s, is already a %s in the %s team"),
    USER_MENTIONED_ALREADY_ASSIGNED_FOR_THE_TEAM_TASK("The user, %s, is assigned for this team task."),
    USER_MENTIONED_IS_NOT_ASSIGNED_FOR_THE_TEAM_TASK("The user, %s, is not assigned for this team task."),
    USER_MENTIONED_NOT_PART_OF_THE_TEAM("The mentioned user is not a part of the **%s** team."),
    USER_NOT_PART_OF_THE_TEAM("You are not a part of the **%s** team."),
    TEAM_NO_MODERATORS("No moderators"),
    TEAM_NO_MEMBERS("No members"),
    TEAM_LEADER_CANNOT_LEAVE("You cannot leave the team while you're still the leader.\nYou must appoint someone else as the leader before departing."),
    TEAM_NO_PENDING_INVITATION("You do not have any pending invitations for this team"),
    TEAM_CO_MODERATORS_ERROR("You are not able to remove co-moderators from the team."),
    TEAM_MODERATOR_OR_HIGHER_REQUIRED("You must be a team moderator or higher to execute this command."),
    TEAM_LEADER_REQUIRED("You must be the team leader to execute this command."),
    TEAM_TASK_NOT_PART_OF_TEAM("Team Task #%d is not part of the %s team.");

    private final Object VALUE;

    Constants(Object value) {
        this.VALUE = value;
    }

    public <T> T getValue(Class<T> type) {
        return type.cast(VALUE);
    }

}