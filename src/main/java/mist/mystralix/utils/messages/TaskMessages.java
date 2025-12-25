package mist.mystralix.utils.messages;

public class TaskMessages {

    public static final String TASK_NOT_CREATED_BY_USER =
            "You do not have access to Task #%d, as it was created by a different user.";

    public static final String NO_CURRENT_TASKS =
            "You currently do not have any tasks! Use the /task add command to start.";

    public static final String NO_TASKS_FOUND_WITH_STATUS =
            "No tasks found with status %s";

    private TaskMessages() {
    }

}
