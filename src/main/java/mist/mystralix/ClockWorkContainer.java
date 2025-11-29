package mist.mystralix;

import mist.mystralix.Database.Reminder.DBReminderRepository;
import mist.mystralix.Database.Task.DBTaskRepository;
import mist.mystralix.Database.Reminder.ReminderRepository;
import mist.mystralix.Database.Task.TaskRepository;
import mist.mystralix.Objects.Reminder.ReminderService;
import mist.mystralix.Objects.Task.TaskService;

/**
 * Central dependency injection container for the ClockWork application.
 * <p>
 * This class is responsible for constructing and wiring all core services
 * and their underlying repository implementations. It ensures that every
 * service receives the correct database-backed repository and that there is
 * a single, shared instance of each service throughout the application.
 * </p>
 *
 * <p>
 * The container is intentionally minimal and explicit, avoiding external
 * frameworks to maintain simplicity, transparency, and full control over
 * service creation.
 * </p>
 */
public class ClockWorkContainer {

    /**
     * Service responsible for all task-related operations,
     * such as creation, retrieval, updating, and deletion.
     */
    private final TaskService taskService;

    /**
     * Service responsible for scheduling, storing, retrieving,
     * and managing user reminders.
     */
    private final ReminderService reminderService;

    /**
     * Constructs a new {@code ClockWorkContainer} and initializes all
     * repositories and domain services.
     * <p>
     * This includes:
     * <ul>
     *     <li>Creating database-backed {@link TaskRepository} and {@link ReminderRepository} instances</li>
     *     <li>Instantiating {@link TaskService} and {@link ReminderService} with their respective repositories</li>
     *     <li>Ensuring these services are available as singletons within the container</li>
     * </ul>
     * </p>
     */
    public ClockWorkContainer() {
        TaskRepository taskRepository = new DBTaskRepository();
        ReminderRepository reminderRepository = new DBReminderRepository();

        this.taskService = new TaskService(taskRepository);
        this.reminderService = new ReminderService(reminderRepository);
    }

    /**
     * Returns the singleton {@link TaskService} instance managed by this container.
     *
     * @return the application's task service
     */
    public TaskService getTaskService() {
        return taskService;
    }

    /**
     * Returns the singleton {@link ReminderService} instance managed by this container.
     *
     * @return the application's reminder service
     */
    public ReminderService getReminderService() {
        return reminderService;
    }
}
