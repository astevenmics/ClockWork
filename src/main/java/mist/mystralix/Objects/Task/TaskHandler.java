package mist.mystralix.Objects.Task;

import mist.mystralix.Database.DBTaskHandler;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class TaskHandler {

    private final DBTaskHandler DB_TASK_HANDLER;

    public TaskHandler() {
        this.DB_TASK_HANDLER = new DBTaskHandler();
    }

    public void addTask(TaskDAO task, User user, String taskUUIDAsString) {
        String userDiscordID = user.getId();
        DB_TASK_HANDLER.addTask(task, userDiscordID, taskUUIDAsString);
    }

    public ArrayList<Task> getUserTasks(User user) {
        ArrayList<Task> userTasks = new ArrayList<>();
        String userDiscordID = user.getId();
        return DB_TASK_HANDLER.getAllUserTasks(userTasks, userDiscordID);
    }

    public Task getUserTask(String taskUUIDAsString) {
        return  DB_TASK_HANDLER.getTask(taskUUIDAsString);
    }

    public Task getUserTask(User user, int taskID) {
        String userDiscordID = user.getId();
        return DB_TASK_HANDLER.getTask(userDiscordID, taskID);
    }

    public void updateUserTask(User user, int taskID, Task task) {
        String userDiscordID = user.getId();
        DB_TASK_HANDLER.updateUserTask(userDiscordID, taskID, task);
    }

    public void deleteUserTask(User user, Task task) {
        String userDiscordID = user.getId();
        DB_TASK_HANDLER.deleteUserTask(userDiscordID, task);
    }

}