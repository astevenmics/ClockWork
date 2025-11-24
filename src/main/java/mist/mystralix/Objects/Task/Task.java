package mist.mystralix.Objects.Task;

/*
    Task | Global Object Class
        - Responsible for creating objects to store in the database, a global object
        - Contains all the information for a task
*/
public class Task {

    /*
        String taskUUID         | A random UUID value stored as a string
                                | Used as a unique identifier for the Task object in the database
    */
    public String taskUUID;

    /*
        String userDiscordID    | User's/Task Owner's discord ID stored as a String
                                | Uses String to ease up storing information, does not require using BIGINT
    */
    public String userDiscordID;

    /*
        int taskID              | A unique identifier for each task
                                | A user-friendly identifier
                                | Often used for identifying or determining the task the user wants to update
    */
    public int taskID;

    /*
        TaskDAO taskDAO         | A TaskDAO object containing the basic information of a task
                                | Basic Information: title, description, and taskStatus
    */
    public TaskDAO taskDAO;

    /*
        - Public constructor to easily create a Task object by including all class attributes into its parameters
        - Mostly used in changing a JSON String from database into a Task class object
        - Sets the class attributes to its respective parameter values
        - Four parameters
            * String taskUUID
                - A random UUID value stored as a string
                - Used as a unique identifier for the Task object in the database
            * String userDiscordID
                - User's/Task Owner's discord ID stored as a String
                - Uses String to ease up storing information, does not require using BIGINT
            * int taskID
                - A unique identifier for each task
                - A user-friendly identifier
                - Often used for identifying or determining the task the user wants to update
            * TaskDAO taskDAO
                - A TaskDAO object containing the basic information of a task
                - Basic Information: title, description, and taskStatus
    */
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