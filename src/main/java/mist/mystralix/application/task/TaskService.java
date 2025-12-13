package mist.mystralix.application.task;

import mist.mystralix.domain.task.Task;
import mist.mystralix.domain.task.TaskDAO;
import mist.mystralix.infrastructure.repository.task.TaskRepository;
import mist.mystralix.utils.IdentifiableFetcher;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

/**
 * Application-layer service responsible for executing task-related use cases.
 *
 * <p>This service acts as the middle layer between the Discord command handlers
 * and the underlying {@link TaskRepository}. It coordinates the workflow for:
 * <ul>
 *     <li>Creating tasks</li>
 *     <li>Retrieving a user's task list</li>
 *     <li>Finding individual tasks</li>
 *     <li>Updating existing tasks</li>
 *     <li>Deleting tasks</li>
 * </ul>
 *
 * <p>All persistence logic is delegated to the {@link TaskRepository}
 * implementation that is injected into this service.
 * This ensures the service remains testable and independent of the database.</p>
 */
public class TaskService implements IdentifiableFetcher<Task> {

    /**
     * Repository implementation that handles all task persistence operations.
     * Injected through the constructor to comply with DIP and enable testing.
     */
    private final TaskRepository TASK_REPOSITORY;

    /**
     * Creates a new {@code TaskService} using the provided repository.
     *
     * @param taskRepository the repository responsible for storing and retrieving tasks
     */
    public TaskService(TaskRepository taskRepository) {
        this.TASK_REPOSITORY = taskRepository;
    }

    /**
     * Adds a new task for the specified user.
     *
     * @param task  the task model containing the task data
     * @param user  the Discord user who owns the task
     * @param uuid  a unique identifier representing the task
     */
    public void addTask(TaskDAO task, User user, String uuid) {

        TASK_REPOSITORY.create(
                new Task(
                        uuid,
                        user.getId(),
                        task)
        );
    }

    /**
     * Retrieves all tasks associated with the given user.
     *
     * @param user the Discord user whose tasks should be listed
     * @return a list of {@link Task} objects belonging to the user
     */
    public ArrayList<Task> getUserTasks(User user) {
        return TASK_REPOSITORY.readAll(user.getId());
    }

    /**
     * Retrieves a task by its unique UUID.
     *
     * @param uuid the unique identifier of the task
     * @return the corresponding {@link Task}, or {@code null} if not found
     */
    public Task getUserTask(String userDiscordID, String uuid) {
        return TASK_REPOSITORY.findByDiscordIDAndUUID(userDiscordID, uuid);
    }

    /**
     * Retrieves a task belonging to a specific user by its numeric ID.
     *
     * @param userDiscordID    the discord ID of the owner of the task
     * @param taskId  the numeric task ID
     * @return the corresponding {@link Task}, or {@code null} if not found
     */
    public Task getUserTask(String userDiscordID, int taskId) {
        return TASK_REPOSITORY.findByDiscordIDAndID(userDiscordID, taskId);
    }

    /**
     * Updates an existing user task.
     *
     * @param task    the modified task object containing updated fields
     */
    public void updateUserTask(Task task) {
        TASK_REPOSITORY.update(task);
    }

    /**
     * Deletes a user-owned task.
     *
     * @param task the task object to delete
     */
    public void deleteUserTask(Task task) {
        TASK_REPOSITORY.delete(task);
    }

    @Override
    public Task fetchByUserIDAndObjectID(String userDiscordId, int taskId) {
        return getUserTask(userDiscordId, taskId);
    }

    @Override
    public Task fetchByUserIDAndObjectUUID(String userDiscordId, String objectUUID) {
        return getUserTask(userDiscordId, objectUUID);
    }
}