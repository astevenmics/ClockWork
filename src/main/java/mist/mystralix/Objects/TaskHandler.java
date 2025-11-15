package mist.mystralix.Objects;

import mist.mystralix.Database.DBTaskHandler;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class TaskHandler {

    // ToDo: Apply Database to this class
    private final DBTaskHandler DB_TASK_HANDLER;

    public TaskHandler() {
        this.DB_TASK_HANDLER = new DBTaskHandler();
    }

    public void addTask(Task task, User user) {
        String userID = user.getId();
        DB_TASK_HANDLER.addTask(task, userID);
    }

    public ArrayList<Task> getUserTasks(User user) {
        ArrayList<Task> userTasks = new ArrayList<>();
        String userID = user.getId();
        return DB_TASK_HANDLER.getAllUserTasks(userTasks, userID);
    }

    public Task getUserTask(User user, int taskID) {
        String userID = user.getId();
        return DB_TASK_HANDLER.getTask(userID, taskID);
    }

    public void cancelUserTask(User user, int taskID, Task task) {
        String userID = user.getId();
        DB_TASK_HANDLER.cancelUserTask(userID, taskID, task);
    }

}