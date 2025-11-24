package mist.mystralix.Listeners.CommandListener.SlashCommands;

import mist.mystralix.Enums.TaskStatus;
import mist.mystralix.Listeners.CommandListener.CommandObjects.TaskSubCommandFunctions;
import mist.mystralix.Listeners.CommandListener.SlashCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class TaskCommand implements SlashCommand {

    /*
        Command Name
            - task
            - Usage: /task [add/cancel/delete/list/update/view] <options>
    */
    @Override
    public String getName() {
        return "task";
    }

    /*
        Command Description
            - Handles all task-related features
            - Handles SubCommands: add/cancel/delete/list/update/view
    */
    @Override
    public String getDescription() {
        return "All task-related features, such as adding, deleting, updating, etc.";
    }

    @Override
    public SubcommandData[] getSubcommands() {
        return new SubcommandData[] {
            new SubcommandData(
                    "add",
                    "Creating tasks/adding tasks to your task list."
            )
                .addOptions(
                    new OptionData(
                            OptionType.STRING,
                            "title",
                            "A brief title for the task.",
                            true
                    ).setRequiredLength(1, 32),
                    new OptionData(
                            OptionType.STRING,
                            "description",
                            "A brief description for the task.",
                            true
                    ).setRequiredLength(1, 256)
                ),
            new SubcommandData(
                    "cancel",
                    "Updates specified task's status as cancelled."
            )
                .addOptions(
                    new OptionData(
                            OptionType.INTEGER,
                            "task_id",
                            "ID number of the task to cancel.",
                            true
                    )
                ),
            new SubcommandData(
                    "delete",
                    "Deletes a specific task selected using its task ID."
            )
                .addOptions(
                    new OptionData(
                            OptionType.INTEGER,
                            "task_id",
                            "ID number of the task to view.",
                            true
                    )
                ),
            new SubcommandData(
                    "list",
                    "Lists all the tasks regardless of status: completed, in progress, archived, or cancelled."
            )
                .addOptions(
                    new OptionData(
                            OptionType.INTEGER,
                            "type",
                            "List all tasks of a certain type.",
                            false
                    )
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
            new SubcommandData(
                    "update",
                    "Updates information: title, description, or task status, of an existing task."
            )
                .addOptions(
                    new OptionData(
                            OptionType.INTEGER,
                            "id",
                            "Task ID of the task to update.",
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
                            "List all tasks of a certain type.",
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
            new SubcommandData(
                    "view",
                    "View a specific task selected using its task ID."
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

    /* Command Event/Output */
    @Override
    public void execute(SlashCommandInteraction event) {

        String subCommand = event.getSubcommandName();
        if(subCommand == null) {
            event.reply("Invalid subcommand.").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        TaskSubCommandFunctions task = new TaskSubCommandFunctions();
        MessageEmbed messageEmbed;

        messageEmbed = switch (subCommand) {
            case "add"      -> task.create(event);
            case "cancel"   -> task.cancelTask(event);
            case "delete"   -> task.delete(event);
            case "list"     -> task.listTasks(event);
            case "update"   -> task.update(event);
            case "view"     -> task.read(event);
            default         -> null;
        };

        if(messageEmbed == null) {
            event.getHook().editOriginal("❌ An error occurred (embed was null).").queue();
            return;
        }
        event.getHook().editOriginalEmbeds(messageEmbed).queue();

    }


}