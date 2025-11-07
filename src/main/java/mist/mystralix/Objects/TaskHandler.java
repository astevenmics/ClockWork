package mist.mystralix.Objects;

import mist.mystralix.Exception.FileException;
import mist.mystralix.ExternalFileHandler.FileHandler;
import mist.mystralix.ExternalFileHandler.JSONHandler;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class TaskHandler {

    public HashSet<Task> getUserTasks(File file) throws IOException {
        JSONHandler jsonHandler = new JSONHandler();
        return jsonHandler.getFileContentsHashSet(file, Task.class);
    }

    public void setUserTasks(File file, Task newTask, User user) throws FileException, IOException {
        JSONHandler jsonHandler = new JSONHandler();
        HashSet<Task> userTasks = getUserTasks(file);

        setUserCounter(user.getId());

        userTasks.add(newTask);
        jsonHandler.setUserTasksInFile(file, userTasks);
    }

    // TODO: Cache the counter and clean up
    public UserCounter getUserCounter(String userDiscordId) throws FileException, IOException {
        String counterFileName = "\\task_counter_per_user.json";
        FileHandler fileHandler = new FileHandler();
        File counterFile = fileHandler.getFile(counterFileName, true);
        JSONHandler jsonHandler = new JSONHandler();
        HashMap<String, UserCounter> usersCounter = jsonHandler.getFileContentsHashMap(counterFile, String.class, UserCounter.class);
        UserCounter userCounter = usersCounter.get(userDiscordId);
        if (userCounter == null) {
            setUserCounter(userDiscordId);
            usersCounter = jsonHandler.getFileContentsHashMap(counterFile, String.class, UserCounter.class);
            userCounter = usersCounter.get(userDiscordId);
        }
        return userCounter;
    }

    public HashMap<String, UserCounter> getUserCounterList() throws FileException, IOException {
        String counterFileName = "\\task_counter_per_user.json";
        FileHandler fileHandler = new FileHandler();
        File counterFile = fileHandler.getFile(counterFileName, true);
        JSONHandler jsonHandler = new JSONHandler();
        return jsonHandler.getFileContentsHashMap(counterFile, String.class, UserCounter.class);
    }

    public void setUserCounter(String userDiscordId) throws FileException, IOException {
        JSONHandler jsonHandler = new JSONHandler();
        HashMap<String, UserCounter> allUserCounters = getUserCounterList();
        UserCounter userCounter = allUserCounters.get(userDiscordId);

        if(userCounter == null) {
            userCounter = new UserCounter(userDiscordId, 0);
        }
        userCounter.counter += 1;
        allUserCounters.put(userDiscordId, userCounter);

        String counterFileName = "\\task_counter_per_user.json";
        FileHandler fileHandler = new FileHandler();
        File counterFile = fileHandler.getFile(counterFileName, true);
        jsonHandler.setUserCounterInFile(counterFile, allUserCounters);
    }

}