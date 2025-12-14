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
                    "moderators JSON NOT NULL DEFAULT (JSON_ARRAY()), " +
                    "members JSON NOT NULL DEFAULT (JSON_ARRAY()), " +
                    "tasks_uuid JSON NOT NULL DEFAULT (JSON_ARRAY()), " +
                    "team_invitations JSON NOT NULL DEFAULT (JSON_ARRAY()), " +
                    "UNIQUE KEY(id)" +
                    ");"
    )),
    TEST("");

    private final Object VALUE;

    Constants(Object value) {
        this.VALUE = value;
    }

    public <T> T getValue(Class<T> type) {
        return type.cast(VALUE);
    }

}