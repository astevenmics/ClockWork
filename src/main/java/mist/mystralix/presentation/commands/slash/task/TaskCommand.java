package mist.mystralix.presentation.commands.slash.task;

import mist.mystralix.domain.enums.TaskStatus;
import mist.mystralix.application.task.TaskService;
import mist.mystralix.presentation.commands.slash.SlashCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * Slash command handler for all /task-related operations.
 *
 * <p>This class defines the command structure (subcommands, options, descriptions)
 * and delegates execution to {@link TaskSubCommandFunctions}, which contains the
 * business logic for each action.</p>
 *
 * <p>Registered as: <b>/task</b></p>
 * <p>Available subcommands:</p>
 * <ul>
 *     <li>/task add</li>
 *     <li>/task delete</li>
 *     <li>/task update</li>
 *     <li>/task cancel</li>
 *     <li>/task list</li>
 *     <li>/task view</li>
 * </ul>
 *
 * <p>This class is marked <code>final</code> because it is not designed for inheritance.
 * Command behavior should remain consistent and centralized.</p>
 */
public final class TaskCommand implements SlashCommand {

    /** Service providing all task CRUD operations. */
    private final TaskService TASK_SERVICE;

    /**
     * Constructs a new TaskCommand with dependency-injected TaskService.
     *
     * @param taskService the domain service responsible for all task logic
     */
    public TaskCommand(TaskService taskService) {
        this.TASK_SERVICE = taskService;
    }

    /**
     * @return the Slash Command name (root command): {@code "task"}.
     */
    @Override
    public String getName() {
        return "task";
    }

    /**
     * @return a human-readable description used by Discord when displaying the command.
     */
    @Override
    public String getDescription() {
        return "All task-related features: adding, deleting, updating, and more.";
    }

    /**
     * Defines the full subcommand structure for /task.
     *
     * <p>Each {@link SubcommandData} describes:</p>
     * <ul>
     *   <li>Subcommand name (e.g., "add")</li>
     *   <li>Description</li>
     *   <li>Arguments / Options</li>
     *   <li>Choices (for task status enums)</li>
     * </ul>
     *
     * @return an array of definition objects that Discord uses during command registration.
     */
    @Override
    public SubcommandData[] getSubcommands() {
        return new SubcommandData[] {

                // /task add
                new SubcommandData("add", "Create a new task.")
                        .addOptions(
                        new OptionData(OptionType.STRING, "title", "A brief title for the task.", true)
                                .setRequiredLength(1, 32),
                        new OptionData(OptionType.STRING, "description", "A detailed description for the task.", true)
                                .setRequiredLength(1, 256)
                ),

                // /task cancel
                new SubcommandData("cancel", "Cancel a task by its task ID.")
                        .addOptions(new OptionData(
                        OptionType.INTEGER,
                        "task_id",
                        "ID number of the task to cancel.",
                        true
                )),

                // /task delete
                new SubcommandData("delete", "Delete a task by its task ID.")
                        .addOptions(new OptionData(
                        OptionType.INTEGER,
                        "task_id",
                        "ID number of the task to delete.",
                        true
                )),

                // /task list
                new SubcommandData("list", "List all tasks, optionally filtered by status.")
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

                // /task update
                new SubcommandData("update", "Update a task’s title, description, or status.")
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
                        new OptionData(
                                OptionType.INTEGER,
                                "type",
                                "New status for the task.",
                                false
                        )
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

                // /task view
                new SubcommandData(
                        "view",
                        "View a specific task by its task ID."
                )
                    .addOptions(
                            new OptionData(
                                OptionType.INTEGER,
                                "task_id",
                                "ID number of the task to view.",
                                true
                    )
                )
        };
    }

    /**
     * Executes the correct subcommand logic based on the name retrieved from the event.
     *
     * <p>Execution flow:</p>
     * <ol>
     *     <li>Validate that a subcommand name exists</li>
     *     <li>Defer the reply (avoids "interaction failed")</li>
     *     <li>Create a {@link TaskSubCommandFunctions} handler</li>
     *     <li>Dispatch to the correct method using a switch expression</li>
     *     <li>Handle any null embed (unexpected condition)</li>
     * </ol>
     *
     * @param event the incoming SlashCommandInteraction from Discord
     */
    @Override
    public void execute(SlashCommandInteraction event) {

        String subCommand = event.getSubcommandName();
        if (subCommand == null) {
            event.reply("Invalid subcommand.").setEphemeral(true).queue();
            return;
        }

        // Prevent timeout by acknowledging interaction
        event.deferReply().queue();

        // Handler containing implementations of each subcommand
        TaskSubCommandFunctions subCommandHandler = new TaskSubCommandFunctions(TASK_SERVICE);

        MessageEmbed messageEmbed = switch (subCommand) {
            case "add"    -> subCommandHandler.create(event);
            case "cancel" -> subCommandHandler.cancelTask(event);
            case "delete" -> subCommandHandler.delete(event);
            case "list"   -> subCommandHandler.readAll(event);
            case "update" -> subCommandHandler.update(event);
            case "view"   -> subCommandHandler.read(event);
            default       -> null;
        };

        if (messageEmbed == null) {
            event.getHook()
                    .editOriginal("❌ An unexpected error occurred (embed was null).")
                    .queue();
            return;
        }

        // Send the rendered embed
        event.getHook().editOriginalEmbeds(messageEmbed).queue();
    }
}
