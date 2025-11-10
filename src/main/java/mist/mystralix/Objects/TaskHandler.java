package mist.mystralix.Objects;

import mist.mystralix.Exception.FileException;
import mist.mystralix.ExternalFileHandler.JSONHandler;
import mist.mystralix.Manager.UserCounterManager;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class TaskHandler {

    private final JSONHandler JSON_HANDLER;

    // TODO: Centralize getUserTasks
    public TaskHandler() {
        this.JSON_HANDLER = new JSONHandler();
    }

    public HashMap<Integer, Task> getUserTasks(File file) throws IOException {
        return JSON_HANDLER.getFileContentsHashMap(file, Integer.class, Task.class);
    }

    public void setUserTasks(
            File file,
            Task newTask,
            User user,
            UserCounterManager userCounterManager
    ) throws FileException, IOException {
        HashMap<Integer, Task> userTasks = getUserTasks(file);

        userCounterManager.setUserCounter(user.getId());

        userTasks.put(newTask.id, newTask);
        JSON_HANDLER.setUserTasksInFile(file, userTasks);
    }

}