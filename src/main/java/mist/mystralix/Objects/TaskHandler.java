package mist.mystralix.Objects;

import mist.mystralix.Enums.TaskStatus;
import mist.mystralix.Exception.FileException;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class TaskHandler {

    // ToDo: Apply Database to this class
//    private final JSONHandler JSON_HANDLER;
//
//    // TODO: Centralize getUserTasks
//    public TaskHandler() {
//        this.JSON_HANDLER = new JSONHandler();
//    }
//
//    public HashMap<Integer, Task> getUserTasks(File file) throws IOException {
//        return JSON_HANDLER.getFileContentsHashMap(file, Integer.class, Task.class);
//    }
//
//    public void setUserTasks(
//            File file,
//            Task newTask,
//            User user,
//            UserCounterManager userCounterManager
//    ) throws FileException, IOException {
//        HashMap<Integer, Task> userTasks = getUserTasks(file);
//
//        userCounterManager.setUserCounter(user.getId());
//
//        userTasks.put(newTask);
//        JSON_HANDLER.setUserTasksInFile(file, userTasks);
//    }
//
//    public void cancelUserTask(
//            File file,
//            Task taskToCancel
//    ) throws IOException {
//        HashMap<Integer, Task> userTasks = getUserTasks(file);
//
//        taskToCancel.taskStatus = TaskStatus.CANCELLED;
//
//        userTasks.put(taskToCancel.id, taskToCancel);
//        JSON_HANDLER.setUserTasksInFile(file, userTasks);
//    }

}