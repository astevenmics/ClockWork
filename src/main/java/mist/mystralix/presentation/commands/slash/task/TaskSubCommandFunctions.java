package mist.mystralix.presentation.commands.slash.task;

import mist.mystralix.application.helper.TaskHelper;
import mist.mystralix.application.pagination.PaginationData;
import mist.mystralix.application.pagination.PaginationService;
import mist.mystralix.application.pagination.TaskPaginationData;
import mist.mystralix.application.task.TaskService;
import mist.mystralix.application.validator.InputValidation;
import mist.mystralix.application.validator.TaskValidator;
import mist.mystralix.domain.enums.TaskStatus;
import mist.mystralix.domain.task.Task;
import mist.mystralix.domain.task.TaskDAO;
import mist.mystralix.presentation.commands.slash.ISlashCommandCRUD;
import mist.mystralix.presentation.embeds.TaskEmbed;
import mist.mystralix.utils.messages.CommonMessages;
import mist.mystralix.utils.messages.TaskMessages;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskSubCommandFunctions implements ISlashCommandCRUD {

    private final TaskService TASK_SERVICE;

    private final PaginationService PAGINATION_SERVICE;

    private final TaskEmbed TASK_EMBED = new TaskEmbed();

    public TaskSubCommandFunctions(TaskService taskService, PaginationService paginationService) {
        this.TASK_SERVICE = taskService;
        this.PAGINATION_SERVICE = paginationService;
    }

    @Override
    public MessageEmbed create(SlashCommandInteraction event) {
        User user = event.getUser();
        OptionMapping title = event.getOption("title");
        OptionMapping description = event.getOption("description");

        if (title == null || description == null) {
            return TASK_EMBED.createMissingParametersEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        TaskDAO taskDAO = new TaskDAO(
                title.getAsString(),
                description.getAsString(),
                TaskStatus.INPROGRESS
        );

        String uuid = UUID.randomUUID().toString();

        TASK_SERVICE.addTask(taskDAO, user, uuid);

        Task createdTask = TASK_SERVICE.getByUUID(uuid);

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
                "task",
                task -> {
                    User user = event.getUser();
                    MessageEmbed messageEmbed = TaskValidator.validateTaskAccess(event.getUser(), task, TASK_EMBED);
                    if (messageEmbed != null) {
                        return messageEmbed;
                    }
                    TASK_SERVICE.delete(task);
                    return TASK_EMBED.createMessageEmbed(user, "Deleted Task", task);
                }
        );
    }

    @Override
    public MessageEmbed update(SlashCommandInteraction event) {
        return InputValidation.validate(
                event,
                TASK_SERVICE,
                TASK_EMBED,
                "id",
                "task",
                task -> {
                    MessageEmbed messageEmbed = TaskValidator.validateTaskAccess(event.getUser(), task, TASK_EMBED);
                    if (messageEmbed != null) {
                        return messageEmbed;
                    }
                    TaskHelper.updateTaskDAO(event, task.getTaskDAO());
                    TASK_SERVICE.update(task);
                    return TASK_EMBED.createMessageEmbed(event.getUser(), "Updated Task", task);
                });
    }

    @Override
    public MessageEmbed read(SlashCommandInteraction event) {
        return InputValidation.validate(
                event,
                TASK_SERVICE,
                TASK_EMBED,
                "task_id",
                "task",
                task -> {
                    MessageEmbed messageEmbed = TaskValidator.validateTaskAccess(event.getUser(), task, TASK_EMBED);
                    if (messageEmbed != null) {
                        return messageEmbed;
                    }
                    return TASK_EMBED.createMessageEmbed(event.getUser(), "Viewing", task);
                });
    }

    public MessageEmbed cancelTask(SlashCommandInteraction event) {
        return InputValidation.validate(
                event,
                TASK_SERVICE,
                TASK_EMBED,
                "task_id",
                "task",
                task -> {
                    User user = event.getUser();

                    MessageEmbed messageEmbed = TaskValidator.validateTaskAccess(event.getUser(), task, TASK_EMBED);
                    if (messageEmbed != null) {
                        return messageEmbed;
                    }

                    task.getTaskDAO().setTaskStatus(TaskStatus.CANCELLED);
                    TASK_SERVICE.update(task);

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

        if (tasks.isEmpty()) {
            return TASK_EMBED.createErrorEmbed(user, TaskMessages.NO_CURRENT_TASKS);
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
                        String.format(TaskMessages.NO_TASKS_FOUND_WITH_STATUS, selected.getStringValue())
                );
            }
        }

        int tasksPerPage = 12;
        int totalPages = (int) Math.ceil((double) tasks.size() / tasksPerPage);
        int currentPage = 1;

        PaginationData paginationData = new TaskPaginationData(currentPage, totalPages, tasks);
        PAGINATION_SERVICE.addPaginationData(Task.class.getName() + ":" + user.getId(), paginationData);

        MessageEmbed messageEmbed = TASK_EMBED.createPaginatedEmbed(user, new ArrayList<>(tasks), currentPage, tasksPerPage);
        Button previousButton = Button.primary("prev_page:" + Task.class.getName(), "Previous");
        Button nextButton = Button.primary("next_page:" + Task.class.getName(), "Next");

        previousButton = previousButton.asDisabled();
        if (currentPage == totalPages) {
            nextButton = nextButton.asDisabled();
        }

        event.getHook().editOriginalEmbeds(messageEmbed).setComponents(ActionRow.of(previousButton, nextButton)).queue();

        return messageEmbed;

    }
}