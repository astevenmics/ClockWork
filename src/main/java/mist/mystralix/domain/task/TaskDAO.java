package mist.mystralix.domain.task;

import mist.mystralix.domain.enums.TaskStatus;

/**
 * Task Data Access Object (DAO).
 *
 * <p>This class is used to transfer raw task data between the database layer
 * and the application. Contains basic task information such as title,
 * description, and status.</p>
 */
public class TaskDAO {

    /** The title summarizing the task's purpose. */
    private String title;

    /** The description detailing what the task entails. */
    private String description;

    /** The task’s status (e.g., COMPLETED, INPROGRESS). */
    private TaskStatus taskStatus;

    /**
     * Constructs a new {@code TaskDAO} object.
     *
     * @param title       the task title
     * @param description the task description
     * @param taskStatus  the current status of the task
     */
    public TaskDAO(
            String title,
            String description,
            TaskStatus taskStatus
    ) {
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
    }

    /** @return the task title */
    public String getTitle() {
        return title;
    }

    /** @param title sets the task title */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return the task description */
    public String getDescription() {
        return description;
    }

    /** @param description sets the task description */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return the task status */
    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    /** @param taskStatus sets the task status */
    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
}
