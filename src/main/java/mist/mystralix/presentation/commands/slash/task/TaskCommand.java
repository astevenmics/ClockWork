package mist.mystralix.presentation.commands.slash.task;

import mist.mystralix.application.helper.TaskHelper;
import mist.mystralix.application.pagination.PaginationService;
import mist.mystralix.application.task.UserTaskService;
import mist.mystralix.domain.enums.TaskStatus;
import mist.mystralix.presentation.commands.slash.SlashCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

public final class TaskCommand implements SlashCommand {

    private final UserTaskSubCommandFunctions USER_TASK_SUB_COMMAND_HANDLER;

    public TaskCommand(UserTaskService taskService, PaginationService paginationService) {
        this.USER_TASK_SUB_COMMAND_HANDLER = new UserTaskSubCommandFunctions(taskService, paginationService);
    }

    @Override
    public String getName() {
        return "task";
    }

    @Override
    public String getDescription() {
        return "Create, manage, and track tasks with status updates and detailed views.";
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return List.of(
                new SubcommandData("create", "Create a new task with a title and description.")
                        .addOptions(
                        new OptionData(OptionType.STRING, "title", "A brief title for the task.", true)
                                .setRequiredLength(1, 32),
                        new OptionData(OptionType.STRING, "description", "A detailed description for the task.", true)
                                .setRequiredLength(1, 256)
                        ),
                new SubcommandData("delete", "Permanently delete a task using its task ID.")
                        .addOptions(new OptionData(
                        OptionType.INTEGER,
                        "task_id",
                        "ID number of the task to delete.",
                        true
                )),
                new SubcommandData("edit", "Edit a task’s title, description, or status.")
                        .addOptions(
                                new OptionData(
                                        OptionType.INTEGER,
                                        "id",
                                        "ID of the task to update.",
                                        true
                                ),
                                new OptionData(
                                        OptionType.STRING,
                                        "title",
                                        "New title for the task.",
                                        false
                                ).setRequiredLength(1, 32),
                                new OptionData(
                                        OptionType.STRING,
                                        "description",
                                        "New description for the task.",
                                        false
                                ).setRequiredLength(1, 256),
                                TaskHelper.getTaskTypeOptions()
                        ),
                // /task list
                new SubcommandData("list", "View all tasks, with optional filtering by status.")
                        .addOptions(
                                new OptionData(OptionType.INTEGER, "type", "Filter tasks by status.", false)
                                        .addChoice(
                                                TaskStatus.ALL.getIcon() + " " + TaskStatus.ALL.getStringValue(),
                                                TaskStatus.ALL.getIntValue())
                                        .addChoice(
                                                TaskStatus.COMPLETED.getIcon() + " " + TaskStatus.COMPLETED.getStringValue(),
                                                TaskStatus.COMPLETED.getIntValue())
                                        .addChoice(
                                                TaskStatus.INPROGRESS.getIcon() + " " + TaskStatus.INPROGRESS.getStringValue(),
                                                TaskStatus.INPROGRESS.getIntValue())
                                        .addChoice(
                                                TaskStatus.ARCHIVED.getIcon() + " " + TaskStatus.ARCHIVED.getStringValue(),
                                                TaskStatus.ARCHIVED.getIntValue())
                                        .addChoice(
                                                TaskStatus.CANCELLED.getIcon() + " " + TaskStatus.CANCELLED.getStringValue(),
                                                TaskStatus.CANCELLED.getIntValue())
                ),
                // /task cancel
                new SubcommandData("cancel", "Cancel an active task without deleting it.")
                        .addOptions(new OptionData(
                                OptionType.INTEGER,
                                "task_id",
                                "ID number of the task to cancel.",
                                true
                        )),


                // /task view
                new SubcommandData(
                        "view",
                        "View detailed information about a specific task by ID."
                )
                    .addOptions(
                            new OptionData(
                                OptionType.INTEGER,
                                "task_id",
                                "ID number of the task to view.",
                                true
                    )
                    ));
    }

    @Override
    public void execute(SlashCommandInteraction event) {

        String subCommand = event.getSubcommandName();
        if (subCommand == null) {
            event.reply("Invalid subcommand.").setEphemeral(true).queue();
            return;
        }

        MessageEmbed messageEmbed = switch (subCommand) {
            case "create" -> USER_TASK_SUB_COMMAND_HANDLER.create(event);
            case "cancel" -> USER_TASK_SUB_COMMAND_HANDLER.cancelTask(event);
            case "delete" -> USER_TASK_SUB_COMMAND_HANDLER.delete(event);
            case "list" -> USER_TASK_SUB_COMMAND_HANDLER.readAll(event);
            case "edit" -> USER_TASK_SUB_COMMAND_HANDLER.update(event);
            case "view" -> USER_TASK_SUB_COMMAND_HANDLER.read(event);
            default       -> null;
        };

        if (messageEmbed == null) {
            event.getHook()
                    .editOriginal("❌ An unexpected error occurred (embed was null).")
                    .queue();
            return;
        }

        event.deferReply().queue();
        event.getHook().editOriginalEmbeds(messageEmbed).queue();
    }
}