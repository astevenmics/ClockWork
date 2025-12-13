package mist.mystralix.domain.task;

public class Task {

    private final String taskUUID;

    private final String userDiscordID;

    private int taskID;

    private final TaskDAO taskDAO;

    public Task(
            String taskUUID,
            String userDiscordID,
            int taskID,
            TaskDAO taskDAO
    ) {
        this.taskUUID = taskUUID;
        this.userDiscordID = userDiscordID;
        this.taskID = taskID;
        this.taskDAO = taskDAO;
    }

    public Task(
            String taskUUID,
            String userDiscordID,
            TaskDAO taskDAO
    ) {
        this.taskUUID = taskUUID;
        this.userDiscordID = userDiscordID;
        this.taskDAO = taskDAO;
    }

    public String getTaskUUID() {
        return taskUUID;
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