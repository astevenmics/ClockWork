package mist.mystralix.presentation.commands.slash.task;

import mist.mystralix.domain.enums.TaskStatus;
import mist.mystralix.presentation.embeds.IMessageEmbedBuilder;
import mist.mystralix.application.validation.InputValidation;
import mist.mystralix.presentation.commands.slash.ISlashCommandCRUD;
import mist.mystralix.utils.IdentifiableFetcher;
import mist.mystralix.domain.task.Task;
import mist.mystralix.domain.task.TaskDAO;
import mist.mystralix.application.task.TaskService;
import mist.mystralix.presentation.embeds.TaskEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handles all /task subcommands (add, delete, update, view, list).
 *
 * <p>This class converts raw Discord SlashCommand inputs into structured domain
 * operations via {@link TaskService}, and outputs results through {@link TaskEmbed}.
 * It acts as the glue between Discord-level interactions and business logic.</p>
 *
 * <p>Each method in this class follows the same structure:</p>
 * <ol>
 *     <li>Validate input</li>
 *     <li>Retrieve task (if needed)</li>
 *     <li>Apply modifications</li>
 *     <li>Persist changes through TaskService</li>
 *     <li>Return a user-facing embed response</li>
 * </ol>
 */
public class TaskSubCommandFunctions implements ISlashCommandCRUD {

    /** Domain service containing the core CRUD logic for tasks. */
    private final TaskService TASK_SERVICE;

    /** Utility responsible for rendering embeds for task operations. */
    private final TaskEmbed TASK_EMBED = new TaskEmbed();

    public TaskSubCommandFunctions(TaskService taskService) {
        this.TASK_SERVICE = taskService;
    }

    /**
     * Creates a new task for the user.
     *
     * <p>Steps:</p>
     * <ul>
     *     <li>Validate title and description</li>
     *     <li>Create a TaskDAO (title, description, status)</li>
     *     <li>Generate a UUID</li>
     *     <li>Persist via TaskService</li>
     *     <li>Retrieve and display the newly created Task model</li>
     * </ul>
     *
     * @param event slash command from Discord
     * @return embed describing the newly created task
     */
    @Override
    public MessageEmbed create(SlashCommandInteraction event) {
        User user = event.getUser();
        OptionMapping title = event.getOption("title");
        OptionMapping description = event.getOption("description");

        // Missing required fields
        if (title == null || description == null) {
            return TASK_EMBED.createMissingParametersEmbed(
                    user,
                    "Neither title nor description were provided"
            );
        }

        TaskDAO taskDAO = new TaskDAO(
                title.getAsString(),
                description.getAsString(),
                TaskStatus.INPROGRESS
        );

        // Generate unique identifier for this task
        String uuid = UUID.randomUUID().toString();

        TASK_SERVICE.addTask(taskDAO, user, uuid);

        // Retrieve full Task model (DAO + metadata)
        Task createdTask = TASK_SERVICE.getUserTask(
                user.getId(),
                uuid
        );

        return TASK_EMBED.createMessageEmbed(
                user,
                "New Task",
                createdTask
        );
    }

    /**
     * Deletes a task using shared validation logic in {@link InputValidation#validate(SlashCommandInteraction, IdentifiableFetcher, IMessageEmbedBuilder, String, Function)}.
     *
     * @param event slash command
     * @return embed indicating deletion
     */
    @Override
    public MessageEmbed delete(SlashCommandInteraction event) {
        return InputValidation.validate(
                event,
                TASK_SERVICE,
                TASK_EMBED,
                "task_id",
                task -> {
                    User user = event.getUser();
                    TASK_SERVICE.deleteUserTask(task);
                    return TASK_EMBED.createMessageEmbed(user, "Deleted Task", task);
                }
        );
    }

    /**
     * Updates a task’s title, description, or status.
     *
     * <p>This method supports partial updates. Only fields provided by the user
     * via slash command options will be changed. Missing values are ignored.</p>
     *
     * @param event slash command
     * @return embed showing updated task
     */
    @Override
    public MessageEmbed update(SlashCommandInteraction event) {
        User user = event.getUser();

        // Required task ID
        int taskID = event.getOption("id", () -> 0, OptionMapping::getAsInt);

        // Lookup task
        Task task = TASK_SERVICE.getUserTask(user.getId(), taskID);
        if (task == null) {
            return TASK_EMBED.createErrorEmbed(user, "No task found");
        }

        // Optional updates
        String newTitle = event.getOption("title",
                () -> null,
                OptionMapping::getAsString
        );
        String newDesc  = event.getOption("description",
                () -> null,
                OptionMapping::getAsString
        );
        int statusInt   = event.getOption("type",
                () -> 0,
                OptionMapping::getAsInt
        );

        TaskStatus newStatus = TaskStatus.getTaskStatus(statusInt);
        TaskDAO dao = task.getTaskDAO();

        // Apply updates only if present
        Optional.ofNullable(newTitle).ifPresent(dao::setTitle);
        Optional.ofNullable(newDesc).ifPresent(dao::setDescription);
        Optional.ofNullable(newStatus).ifPresent(dao::setTaskStatus);

        TASK_SERVICE.updateUserTask(task);

        return TASK_EMBED.createMessageEmbed(user, "Updated Task", task);
    }

    /**
     * Views a single task.
     */
    @Override
    public MessageEmbed read(SlashCommandInteraction event) {
        return InputValidation.validate(
                event,
                TASK_SERVICE,
                TASK_EMBED,
                "task_id",
                task ->
                    TASK_EMBED.createMessageEmbed(
                            event.getUser(),
                            "Viewing",
                            task
                    )
        );
    }

    /**
     * Cancels a task (sets status → CANCELLED).
     *
     * <p><b>Note:</b> This currently creates a new TaskService/Repository instance,
     * which is not ideal in Dependency Injection. Should use injected service instead.</p>
     *
     * @param event slash command
     * @return embed confirming cancellation
     */
    public MessageEmbed cancelTask(SlashCommandInteraction event) {
        return InputValidation.validate(
                event,
                TASK_SERVICE,
                TASK_EMBED,
                "task_id",
                task -> {
                    User user = event.getUser();

                    // Apply cancellation
                    task.getTaskDAO().setTaskStatus(TaskStatus.CANCELLED);
                    TASK_SERVICE.updateUserTask(task);

                    return TASK_EMBED.createMessageEmbed(user, "Cancelled Task", task);
                }
        );
    }

    /**
     * Displays all user tasks, optionally filtered by status.
     *
     * @param event slash command
     * @return embed listing tasks
     */
    @Override
    public MessageEmbed readAll(SlashCommandInteraction event) {
        User user = event.getUser();

        int option = event.getOption("type",
                () -> 1,
                OptionMapping::getAsInt
        );
        TaskStatus selected = TaskStatus.getTaskStatus(option);

        ArrayList<Task> tasks = TASK_SERVICE.getUserTasks(user);

        // No tasks at all
        if (tasks.isEmpty()) {
            return TASK_EMBED.createErrorEmbed(
                    user,
                    "You currently do not have any tasks! Use the /task add command to start."
            );
        }

        // Filter if not ALL
        if (selected != TaskStatus.ALL) {
            tasks = tasks.stream()
                    .filter(t -> t
                            .getTaskDAO()
                            .getTaskStatus()
                            .equals(selected))
                    .collect(Collectors.toCollection(ArrayList::new));

            if (tasks.isEmpty()) {
                return TASK_EMBED.createErrorEmbed(
                        user,
                        "No tasks found with status " + selected.getStringValue()
                );
            }
        }

        return TASK_EMBED.createListEmbed(
                user,
                tasks
        );
    }
}