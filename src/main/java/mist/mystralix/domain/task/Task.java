package mist.mystralix.domain.task;

public class Task {

    private final String uuid;

    private final String userDiscordID;

    private int id;

    private final TaskDAO taskDAO;

    public Task(
            String uuid,
            String userDiscordID,
            int id,
            TaskDAO taskDAO
    ) {
        this.uuid = uuid;
        this.userDiscordID = userDiscordID;
        this.id = id;
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

    public int getId() {
        return id;
    }

    public TaskDAO getTaskDAO() {
        return taskDAO;
    }

}