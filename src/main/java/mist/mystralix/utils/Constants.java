package mist.mystralix.utils;

import java.util.List;

public enum Constants {

    TABLES(List.of(
            "CREATE TABLE IF NOT EXISTS tasks (" +
                    "uuid VARCHAR(50) PRIMARY KEY, " +
                    "userDiscordID VARCHAR(50) NOT NULL, " +
                    "taskID BIGINT NOT NULL AUTO_INCREMENT, " +
                    "taskDAO JSON NOT NULL DEFAULT (JSON_ARRAY()), " +
                    "UNIQUE KEY(taskID)" +
                    ");",
            "CREATE TABLE IF NOT EXISTS reminders (" +
                    "uuid VARCHAR(50) PRIMARY KEY, " +
                    "userDiscordID VARCHAR(50) NOT NULL, " +
                    "reminderID BIGINT NOT NULL AUTO_INCREMENT, " +
                    "message VARCHAR(8192) NOT NULL, " +
                    "targetTimestamp BIGINT NOT NULL, " +
                    "isNotificationSent BOOLEAN NOT NULL DEFAULT FALSE, " +
                    "UNIQUE KEY(reminderID)" +
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
                    ");"
    )),
    MISSING_PARAMETERS("Please provide all the necessary parameters."),
    OBJECT_NOT_FOUND("The %s does not exist."),
    REMINDER_INVALID_TIME_INPUT("Invalid reminder time provided.\nExample: 1d, 1d20h, 20h15m..."),
    REMINDER_MINIMUM_TIME_INPUT("Time target/duration must be at least 1 minute."),
    NO_OBJECT_FOUND("No object found of %s with ID: %s"),
    USER_ALREADY_INVITED("The user, %s, has already been invited."),
    USER_ALREADY_PART_OF_THE_TEAM("The user, %s, is already part of the %s team"),
    USER_NOT_PART_OF_THE_TEAM("The mentioned user is not a part of the team."),
    TEAM_CO_MODERATORS_ERROR("You are not able to remove co-moderators from the team."),
    TEAM_MODERATOR_OR_HIGHER_REQUIRED("You must be a team moderator or higher to execute this command."),
    TEAM_LEADER_REQUIRED("You must be the team leader to execute this command.");

    private final Object VALUE;

    Constants(Object value) {
        this.VALUE = value;
    }

    public <T> T getValue(Class<T> type) {
        return type.cast(VALUE);
    }

}