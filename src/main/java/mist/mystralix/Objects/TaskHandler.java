package mist.mystralix.Objects;

import mist.mystralix.Database.DBTaskHandler;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class TaskHandler {

    private final DBTaskHandler DB_TASK_HANDLER;

    public TaskHandler() {
        this.DB_TASK_HANDLER = new DBTaskHandler();
    }

    public void addTask(Task task, User user) {
        String userDiscordID = user.getId();
        DB_TASK_HANDLER.addTask(task, userDiscordID);
    }

    public ArrayList<Task> getUserTasks(User user) {
        ArrayList<Task> userTasks = new ArrayList<>();
        String userDiscordID = user.getId();
        return DB_TASK_HANDLER.getAllUserTasks(userTasks, userDiscordID);
    }

    public Task getUserTask(User user, int taskID) {
        String userDiscordID = user.getId();
        return DB_TASK_HANDLER.getTask(userDiscordID, taskID);
    }

    public void cancelUserTask(User user, int taskID, Task task) {
        String userDiscordID = user.getId();
        DB_TASK_HANDLER.cancelUserTask(userDiscordID, taskID, task);
    }

}