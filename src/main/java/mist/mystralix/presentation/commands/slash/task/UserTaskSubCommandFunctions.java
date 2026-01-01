package mist.mystralix.presentation.commands.slash.task;

import mist.mystralix.application.helper.TaskHelper;
import mist.mystralix.application.pagination.PaginationData;
import mist.mystralix.application.pagination.PaginationService;
import mist.mystralix.application.pagination.UserTaskPaginationData;
import mist.mystralix.application.task.UserTaskService;
import mist.mystralix.application.validator.InputValidation;
import mist.mystralix.application.validator.TaskValidator;
import mist.mystralix.domain.enums.TaskStatus;
import mist.mystralix.domain.task.UserTask;
import mist.mystralix.presentation.commands.slash.ISlashCommandCRUD;
import mist.mystralix.presentation.embeds.UserTaskEmbed;
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

public class UserTaskSubCommandFunctions implements ISlashCommandCRUD {

    private final UserTaskService USER_TASK_SERVICE;

    private final PaginationService PAGINATION_SERVICE;

    private final UserTaskEmbed USER_TASK_EMBED = new UserTaskEmbed();

    public UserTaskSubCommandFunctions(UserTaskService taskService, PaginationService paginationService) {
        this.USER_TASK_SERVICE = taskService;
        this.PAGINATION_SERVICE = paginationService;
    }

    @Override
    public MessageEmbed create(SlashCommandInteraction event) {
        User user = event.getUser();
        OptionMapping title = event.getOption("title");
        OptionMapping description = event.getOption("description");

        if (title == null || description == null) {
            return USER_TASK_EMBED.createMissingParametersEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        String uuid = UUID.randomUUID().toString();

        USER_TASK_SERVICE.addTask(user, uuid, title.getAsString(), description.getAsString(), TaskStatus.INPROGRESS.getIntValue());

        UserTask createdTask = USER_TASK_SERVICE.getByUUID(uuid);

        return USER_TASK_EMBED.createMessageEmbed(
                user,
                "New UserTask",
                createdTask
        );
    }

    @Override
    public MessageEmbed delete(SlashCommandInteraction event) {
        return InputValidation.validate(
                event,
                USER_TASK_SERVICE,
                USER_TASK_EMBED,
                "task_id",
                "task",
                userTask -> {
                    User user = event.getUser();
                    MessageEmbed messageEmbed = TaskValidator.validateTaskAccess(event.getUser(), userTask, USER_TASK_EMBED);
                    if (messageEmbed != null) {
                        return messageEmbed;
                    }
                    USER_TASK_SERVICE.delete(userTask);
                    return USER_TASK_EMBED.createMessageEmbed(user, "Deleted Task", userTask);
                }
        );
    }

    @Override
    public MessageEmbed update(SlashCommandInteraction event) {
        return InputValidation.validate(
                event,
                USER_TASK_SERVICE,
                USER_TASK_EMBED,
                "id",
                "task",
                userTask -> {
                    MessageEmbed messageEmbed = TaskValidator.validateTaskAccess(event.getUser(), userTask, USER_TASK_EMBED);
                    if (messageEmbed != null) {
                        return messageEmbed;
                    }
                    TaskHelper.updateTask(event, userTask);
                    USER_TASK_SERVICE.update(userTask);
                    return USER_TASK_EMBED.createMessageEmbed(event.getUser(), "Updated Task", userTask);
                });
    }

    @Override
    public MessageEmbed read(SlashCommandInteraction event) {
        return InputValidation.validate(
                event,
                USER_TASK_SERVICE,
                USER_TASK_EMBED,
                "task_id",
                "task",
                userTask -> {
                    MessageEmbed messageEmbed = TaskValidator.validateTaskAccess(event.getUser(), userTask, USER_TASK_EMBED);
                    if (messageEmbed != null) {
                        return messageEmbed;
                    }
                    return USER_TASK_EMBED.createMessageEmbed(event.getUser(), "Viewing", userTask);
                });
    }

    public MessageEmbed cancelTask(SlashCommandInteraction event) {
        return InputValidation.validate(
                event,
                USER_TASK_SERVICE,
                USER_TASK_EMBED,
                "task_id",
                "task",
                userTask -> {
                    User user = event.getUser();

                    MessageEmbed messageEmbed = TaskValidator.validateTaskAccess(event.getUser(), userTask, USER_TASK_EMBED);
                    if (messageEmbed != null) {
                        return messageEmbed;
                    }

                    userTask.setStatus(TaskStatus.CANCELLED.getIntValue());
                    USER_TASK_SERVICE.update(userTask);

                    return USER_TASK_EMBED.createMessageEmbed(user, "Cancelled Task", userTask);
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
        ArrayList<UserTask> tasks = USER_TASK_SERVICE.getUserTasks(user);

        if (tasks.isEmpty()) {
            return USER_TASK_EMBED.createErrorEmbed(user, TaskMessages.NO_CURRENT_TASKS);
        }

        // Filter if not ALL
        if (selected != TaskStatus.ALL) {
            tasks = tasks.stream()
                    .filter(t -> TaskStatus.getTaskStatus(t.getStatus()).equals(selected))
                    .collect(Collectors.toCollection(ArrayList::new));

            if (tasks.isEmpty()) {
                return USER_TASK_EMBED.createErrorEmbed(
                        user,
                        String.format(TaskMessages.NO_TASKS_FOUND_WITH_STATUS, selected.getStringValue())
                );
            }
        }

        int tasksPerPage = 12;
        int totalPages = (int) Math.ceil((double) tasks.size() / tasksPerPage);
        int currentPage = 1;

        PaginationData paginationData = new UserTaskPaginationData(currentPage, totalPages, tasks);
        PAGINATION_SERVICE.addPaginationData(UserTask.class.getName() + ":" + user.getId(), paginationData);

        MessageEmbed messageEmbed = USER_TASK_EMBED.createPaginatedEmbed(user, new ArrayList<>(tasks), currentPage, tasksPerPage);
        Button previousButton = Button.primary("prev_page:" + UserTask.class.getName(), "Previous");
        Button nextButton = Button.primary("next_page:" + UserTask.class.getName(), "Next");

        previousButton = previousButton.asDisabled();
        if (currentPage == totalPages) {
            nextButton = nextButton.asDisabled();
        }

        event.getHook().editOriginalEmbeds(messageEmbed).setComponents(ActionRow.of(previousButton, nextButton)).queue();

        return messageEmbed;

    }
}