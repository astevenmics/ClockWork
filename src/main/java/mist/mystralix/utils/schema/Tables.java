package mist.mystralix.utils.schema;

import java.util.List;

public final class Tables {

    public static final String TASKS = """
            CREATE TABLE IF NOT EXISTS tasks (
                uuid VARCHAR(50) PRIMARY KEY,
                user_discord_id VARCHAR(50) NOT NULL,
                id BIGINT NOT NULL AUTO_INCREMENT,
                task_dao JSON NOT NULL DEFAULT (JSON_ARRAY()),
                UNIQUE KEY(id)
            );
            """;

    public static final String REMINDERS = """
            CREATE TABLE IF NOT EXISTS reminders (
                uuid VARCHAR(50) PRIMARY KEY,
                user_discord_id VARCHAR(50) NOT NULL,
                id BIGINT NOT NULL AUTO_INCREMENT,
                message VARCHAR(8192) NOT NULL,
                created_timestamp BIGINT NOT NULL,
                target_timestamp BIGINT NOT NULL,
                is_notification_sent BOOLEAN NOT NULL DEFAULT FALSE,
                UNIQUE KEY(id)
            );
            """;

    public static final String TEAMS = """
            CREATE TABLE IF NOT EXISTS teams (
                uuid VARCHAR(50) PRIMARY KEY,
                id BIGINT NOT NULL AUTO_INCREMENT,
                team_name VARCHAR(50) NOT NULL,
                team_leader VARCHAR(50) NOT NULL,
                moderators JSON NOT NULL DEFAULT (JSON_ARRAY()),
                members JSON NOT NULL DEFAULT (JSON_ARRAY()),
                tasks_uuid JSON NOT NULL DEFAULT (JSON_ARRAY()),
                team_invitations JSON NOT NULL DEFAULT (JSON_ARRAY()),
                UNIQUE KEY(id)
            );
            """;

    public static final String TEAM_TASK = """
            CREATE TABLE IF NOT EXISTS team_task (
                uuid VARCHAR(50) PRIMARY KEY,
                user_discord_id VARCHAR(50) NOT NULL,
                id BIGINT NOT NULL AUTO_INCREMENT,
                task_dao JSON NOT NULL DEFAULT (JSON_ARRAY()),
                team_uuid VARCHAR(50) NOT NULL,
                team_id VARCHAR(50) NOT NULL,
                assigned_users JSON NOT NULL DEFAULT (JSON_ARRAY()),
                UNIQUE KEY(id)
            );
            """;

    private Tables() {
    }

    public static List<String> getTables() {
        return List.of(TASKS, REMINDERS, TEAMS, TEAM_TASK);
    }

}

