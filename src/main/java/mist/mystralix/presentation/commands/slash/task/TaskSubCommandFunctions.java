package mist.mystralix.presentation.commands.slash.task;

import mist.mystralix.application.task.TaskService;
import mist.mystralix.application.validation.InputValidation;
import mist.mystralix.domain.enums.TaskStatus;
import mist.mystralix.domain.task.Task;
import mist.mystralix.domain.task.TaskDAO;
import mist.mystralix.presentation.commands.slash.ISlashCommandCRUD;
import mist.mystralix.presentation.embeds.TaskEmbed;
import mist.mystralix.utils.Constants;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskSubCommandFunctions implements ISlashCommandCRUD {

    private final TaskService TASK_SERVICE;

    private final TaskEmbed TASK_EMBED = new TaskEmbed();

    public TaskSubCommandFunctions(TaskService taskService) {
        this.TASK_SERVICE = taskService;
    }

    @Override
    public MessageEmbed create(SlashCommandInteraction event) {
        User user = event.getUser();
        OptionMapping title = event.getOption("title");
        OptionMapping description = event.getOption("description");

        if (title == null || description == null) {
            return TASK_EMBED.createMissingParametersEmbed(
                    user,
                    Constants.MISSING_PARAMETERS.getValue(String.class)
            );
        }

        TaskDAO taskDAO = new TaskDAO(
                title.getAsString(),
                description.getAsString(),
                TaskStatus.INPROGRESS
        );

        String uuid = UUID.randomUUID().toString();

        TASK_SERVICE.addTask(taskDAO, user, uuid);

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

    @Override
    public MessageEmbed update(SlashCommandInteraction event) {
        User user = event.getUser();

        int taskID = event.getOption("id", () -> 0, OptionMapping::getAsInt);

        Task task = TASK_SERVICE.getUserTask(user.getId(), taskID);
        if (task == null) {
            return TASK_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.OBJECT_NOT_FOUND.getValue(String.class),
                            "task"
                    )
            );
        }

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

        Optional.ofNullable(newTitle).ifPresent(dao::setTitle);
        Optional.ofNullable(newDesc).ifPresent(dao::setDescription);
        Optional.ofNullable(newStatus).ifPresent(dao::setTaskStatus);

        TASK_SERVICE.updateUserTask(task);

        return TASK_EMBED.createMessageEmbed(user, "Updated Task", task);
    }

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