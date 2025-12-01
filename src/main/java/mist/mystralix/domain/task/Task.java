package mist.mystralix.domain.task;

/**
 * Represents the complete domain model for a task within the application.
 *
 * <p>This class stores all identifying metadata required by both the
 * domain layer and the persistence layer:</p>
 *
 * <ul>
 *   <li><b>taskUUID</b> – globally unique identifier used internally and in the database</li>
 *   <li><b>userDiscordID</b> – owner of the task</li>
 *   <li><b>taskID</b> – user-friendly numeric index used in slash commands</li>
 *   <li><b>taskDAO</b> – mutable task content (title, description, status)</li>
 * </ul>
 *
 * <p>This object is immutable except for the {@link TaskDAO}, which is expected
 * to be modified when task content changes (e.g., updates to title, description, or status).</p>
 */
public class Task {

    /**
     * The unique, persistent UUID associated with this task.
     * <p>
     * Unlike {@code taskID}, this value never changes and is used internally
     * as the absolute identifier in the database.
     * </p>
     */
    private final String taskUUID;

    /**
     * The Discord user ID of the task owner.
     * <p>
     * Stored as a String for compatibility with Discord's snowflake ID format.
     * </p>
     */
    private final String userDiscordID;

    /**
     * A human-friendly numeric identifier assigned to the task.
     *
     * <p>This is user-facing and used primarily in slash commands (e.g., <code>/task view 3</code>).
     * Multiple tasks can exist per user, and their taskIDs typically follow
     * an incremental sequence.</p>
     */
    private int taskID;

    /**
     * Contains the mutable portion of the task:
     * title, description, and status.
     *
     * <p>Updates to a task are applied by modifying this object and persisting
     * the change through the service/repository layer.</p>
     */
    private final TaskDAO taskDAO;

    /**
     * Constructs a new {@code Task} domain object.
     *
     * @param taskUUID       the unique global identifier for this task
     * @param userDiscordID  the Discord ID of the task owner
     * @param taskID         the user-visible numeric task identifier
     * @param taskDAO        the task's mutable content payload
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

    public Task(
            String taskUUID,
            String userDiscordID,
            TaskDAO taskDAO
    ) {
        this.taskUUID = taskUUID;
        this.userDiscordID = userDiscordID;
        this.taskDAO = taskDAO;
    }

    /** @return the unique UUID of the task */
    public String getTaskUUID() {
        return taskUUID;
    }

    /** @return the Discord ID of the user who owns this task */
    public String getUserDiscordID() {
        return userDiscordID;
    }

    /** @return the numeric, user-facing task identifier */
    public int getTaskID() {
        return taskID;
    }

    /**
     * Returns the task's content container (title, description, status).
     *
     * @return the mutable TaskDAO instance associated with this task
     */
    public TaskDAO getTaskDAO() {
        return taskDAO;
    }
}
