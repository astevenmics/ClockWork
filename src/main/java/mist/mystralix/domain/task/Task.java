package mist.mystralix.domain.task;

public class Task {

    private final String uuid;

    private final String userDiscordID;

    private int taskID;

    private final TaskDAO taskDAO;

    public Task(
            String uuid,
            String userDiscordID,
            int taskID,
            TaskDAO taskDAO
    ) {
        this.uuid = uuid;
        this.userDiscordID = userDiscordID;
        this.taskID = taskID;
        this.taskDAO = taskDAO;
    }

    public Task(
            String uuid,
            String userDiscordID,
            TaskDAO taskDAO
    ) {
        this.uuid = uuid;
        this.userDiscordID = userDiscordID;
        this.taskDAO = taskDAO;
    }

    public String getUUID() {
        return uuid;
    }

    public String getUserDiscordID() {
        return userDiscordID;
    }

    public int getTaskID() {
        return taskID;
    }

    public TaskDAO getTaskDAO() {
        return taskDAO;
    }

}