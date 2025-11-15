package mist.mystralix.Objects;

public class Task {

    public String taskUUID;
    public String userDiscordID;
    public int taskID;
    public TaskDAO taskDAO;

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

}