package mist.mystralix.Objects;

import mist.mystralix.Exception.FileException;
import mist.mystralix.ExternalFileHandler.JSONHandler;
import mist.mystralix.Manager.UserCounterManager;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class TaskHandler {

    public HashSet<Task> getUserTasks(File file) throws IOException {
        JSONHandler jsonHandler = new JSONHandler();
        return jsonHandler.getFileContentsHashSet(file, Task.class);
    }

    public void setUserTasks(
            File file,
            Task newTask,
            User user,
            UserCounterManager userCounterManager
    ) throws FileException, IOException {
        JSONHandler jsonHandler = new JSONHandler();
        HashSet<Task> userTasks = getUserTasks(file);

        userCounterManager.setUserCounter(user.getId());

        userTasks.add(newTask);
        jsonHandler.setUserTasksInFile(file, userTasks);
    }

}