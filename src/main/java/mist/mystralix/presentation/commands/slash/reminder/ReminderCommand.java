package mist.mystralix.presentation.commands.slash.reminder;

import mist.mystralix.application.pagination.PaginationService;
import mist.mystralix.application.reminder.ReminderService;
import mist.mystralix.presentation.commands.slash.SlashCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

public final class ReminderCommand implements SlashCommand {

    private final ReminderSubCommandFunctions REMINDER_SUB_COMMAND_FUNCTIONS;

    public ReminderCommand(ReminderService reminderService, PaginationService paginationService) {
        this.REMINDER_SUB_COMMAND_FUNCTIONS = new ReminderSubCommandFunctions(reminderService, paginationService);
    }

    @Override
    public String getName() {
        return "reminder";
    }

    @Override
    public String getDescription() {
        return "All reminder-related features, such as adding, deleting, updating, etc.";
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return List.of(
                // /reminder create
                new SubcommandData(
                        "create",
                        "Create a new reminder with a message and time duration."
                ).addOptions(
                        new OptionData(
                                OptionType.STRING,
                                "message",
                                "Reminder message content.",
                                true
                        ),
                        new OptionData(
                                OptionType.STRING,
                                "time",
                                "[1d/1h/30m] | Example: 1h30m",
                                true
                        )
                ),

                // /reminder delete
                new SubcommandData(
                        "delete",
                        "Deletes a reminder by its reminder ID."
                ).addOptions(
                        new OptionData(
                                OptionType.INTEGER,
                                "reminder_id",
                                "The ID of the reminder to delete.",
                                true
                        )
                ),

                // /reminder list
                new SubcommandData(
                        "list",
                        "Lists all of your active reminders."
                ),

                // /reminder update
                new SubcommandData(
                        "update",
                        "Updates an existing reminder's message or time."
                ).addOptions(
                        new OptionData(
                                OptionType.INTEGER,
                                "reminder_id",
                                "The ID of the reminder to update.",
                                true
                        ),
                        new OptionData(
                                OptionType.STRING,
                                "message",
                                "Updated reminder message.",
                                false
                        ),
                        new OptionData(
                                OptionType.STRING,
                                "time",
                                "Updated time duration. Example: 1h30m",
                                false
                        )
                ),

                // /reminder view
                new SubcommandData(
                        "view",
                        "View a single reminder by its ID."
                ).addOptions(
                        new OptionData(
                                OptionType.INTEGER,
                                "reminder_id",
                                "The ID of the reminder to view.",
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
            case "create" -> REMINDER_SUB_COMMAND_FUNCTIONS.create(event);
            case "delete" -> REMINDER_SUB_COMMAND_FUNCTIONS.delete(event);
            case "update" -> REMINDER_SUB_COMMAND_FUNCTIONS.update(event);
            case "view"   -> REMINDER_SUB_COMMAND_FUNCTIONS.read(event);
            case "list"   -> REMINDER_SUB_COMMAND_FUNCTIONS.readAll(event);
            default -> null;
        };

        if (messageEmbed == null) {
            event.getHook().editOriginal("❌ An error occurred (embed was null).").queue();
            return;
        }

        event.deferReply().queue();
        event.getHook().editOriginalEmbeds(messageEmbed).queue();
    }
}