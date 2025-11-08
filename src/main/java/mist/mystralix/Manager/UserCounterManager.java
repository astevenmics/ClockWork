package mist.mystralix.Manager;

import mist.mystralix.Exception.FileException;
import mist.mystralix.ExternalFileHandler.FileHandler;
import mist.mystralix.ExternalFileHandler.JSONHandler;
import mist.mystralix.Objects.UserCounter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class UserCounterManager {

    public final String COUNTER_FILE_NAME = "\\task_counter_per_user.json";
    private final JSONHandler JSON_HANDLER;
    private final File COUNTER_FILE;
    private final HashMap<String, UserCounter> ALL_USER_COUNTERS;

    /*
    Description: UserCounterManager constructor initiates all attributes upon instantiation
    Process:
        1. Loads up/Instantiates a new JSONHandler object
        2. Gets the UserCounter file using the FileHandler's getFile function
        2.1 Set throwFileExistsException as true to receive an error if the file does not exist
        3. ALL_USER_COUNTERS initializes all the UserCounters inside the file
        3.1 This way, the file does not get called and retrieved every single time a function inside the class is called
    */
    public UserCounterManager(FileHandler fileHandler) throws FileException, IOException {
        this.JSON_HANDLER = new JSONHandler();
        this.COUNTER_FILE = fileHandler.getFile(COUNTER_FILE_NAME, true);
        this.ALL_USER_COUNTERS = getAllUserCounters();
    }

    /*
    Description: getAllUserCounters gathers all the information inside the COUNTER_FILE
    Process:
        1. Calls JSON_HANDLER's getFileContentsHashMap to get all UserCounter objects into a HashMap
        2. Returns all collected information as a HashMap
    */
    private HashMap<String, UserCounter> getAllUserCounters() throws IOException {
        return JSON_HANDLER.getFileContentsHashMap(COUNTER_FILE, String.class, UserCounter.class);
    }

    /*
        Description: getUserCounter gets the UserCounter object of a user using their Discord ID
        Process:
            1. userCounter gets the UserCounter Object of the user using the userDiscordId
            2. Checks if the userCounter does not exist
            2.1 If it does not exist, it calls the setUserCounter function to create a new UserCounter
            2.2 Once created, it pushes it into the ALL_USER_COUNTERS attribute
            3. Returns a UserCounter object by getting the UserCounter object using the user's Discord ID
    */
    public UserCounter getUserCounter(String userDiscordId) throws IOException {
        UserCounter userCounter = ALL_USER_COUNTERS.get(userDiscordId);
        if (userCounter == null) {
            setUserCounter(userDiscordId);
        }
        return ALL_USER_COUNTERS.get(userDiscordId);
    }

    /*
        Description: setUserCounter either updates or creates a counter for a user
        Process:
            1. userCounter gets the UserCounter of the user using the userDiscordId
            2. Checks if the userCounter does not exist
            2.1 If it does not exist, it creates a new UserCounter
            3. Increases userCounter's counter by 1
            4. Puts the new/updated userCounter into the ALL_USER_COUNTERS attribute
            5. Pushes the updated ALL_USER_COUNTERS HashMap into the COUNTER_FILE using JSON_HANDLER
            6. ALL_USER_COUNTERS ensures that the file does not get called and read every single function call
            7. The setUserCounterInFile function ensures that the file is in sync with the ALL_USER_COUNTERS HashMap
    */
    public void setUserCounter(String userDiscordId) throws IOException {
        UserCounter userCounter = ALL_USER_COUNTERS.get(userDiscordId);

        if (userCounter == null) {
            userCounter = new UserCounter(userDiscordId, 0);
        }
        userCounter.counter += 1;
        ALL_USER_COUNTERS.put(userDiscordId, userCounter);

        JSON_HANDLER.setUserCounterInFile(COUNTER_FILE, ALL_USER_COUNTERS);
    }

}