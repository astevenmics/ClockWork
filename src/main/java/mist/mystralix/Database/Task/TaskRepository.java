package mist.mystralix.Database.Task;

import mist.mystralix.Objects.Task.Task;
import mist.mystralix.Objects.Task.TaskDAO;

import java.util.List;

/**
 * Repository abstraction for performing CRUD operations on Task data.
 *
 * <p>This interface defines the contract between the application layer
 * ({@code TaskService}) and the persistence layer (e.g., MySQL, SQLite, JSON files).
 * By programming against this interface rather than a concrete implementation,
 * the application follows the Dependency Inversion Principle (DIP), enabling
 * testability, modularity, and the ability to swap database technologies without
 * modifying service or command logic.</p>
 */
public interface TaskRepository {

    /**
     * Inserts a new task into the underlying storage.
     *
     * @param task     the basic task structure containing title, description, and status
     * @param userId   the Discord ID of the user who owns the task
     * @param uuid     the unique UUID associated with the task
     */
    void addTask(TaskDAO task, String userId, String uuid);

    /**
     * Retrieves all tasks belonging to the specified user, typically ordered by taskID.
     *
     * @param userId the Discord ID of the task owner
     * @return a list of {@link Task} objects; never null (empty list if none found)
     */
    List<Task> getAllUserTasks(String userId);

    /**
     * Retrieves a specific task using its globally unique UUID.
     *
     * @param uuid the unique identifier of the task
     * @return the corresponding {@link Task}, or {@code null} if not found
     */
    Task getTask(String uuid);

    /**
     * Retrieves a task belonging to a specific user using its numeric taskID.
     * <p>This is the ID commonly used for UI and user commands (e.g., "/task update id:3").</p>
     *
     * @param userId the ID of the task owner
     * @param taskId the user-friendly numeric ID of the task
     * @return the matching {@link Task}, or {@code null} if not found
     */
    Task getTask(String userId, int taskId);

    /**
     * Updates the stored content of a user's task.
     *
     * @param userId the ID of the user who owns the task
     * @param taskId the numeric ID of the task to update
     * @param task   the updated task data (usually containing modified TaskDAO information)
     */
    void updateUserTask(String userId, int taskId, Task task);

    /**
     * Deletes a task belonging to a user.
     *
     * @param userId the ID of the user who owns the task
     * @param task   the task instance containing the UUID to remove
     */
    void deleteUserTask(String userId, Task task);
}
