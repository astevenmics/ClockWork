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
        return "Create, manage, and view personal reminders with custom messages and durations.";
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return List.of(
                // /reminder create
                new SubcommandData(
                        "create",
                        "Create a new reminder with a message and a scheduled time."
                ).addOptions(
                        new OptionData(
                                OptionType.STRING,
                                "message",
                                "Message content.",
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
                        "Delete one of your reminders using its reminder ID."
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
                        "View all reminders, with optional filtering by status."
                ).addOptions(
                        new OptionData(OptionType.STRING, "status", "Filter reminders by status.", false)
                                .addChoice("Active", "active")
                                .addChoice("Expired", "expired")
                )
                ,

                // /reminder update
                new SubcommandData(
                        "edit",
                        "Edit an existing reminder’s message or scheduled time."
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
                        "View detailed information about a specific reminder by ID."
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
            case "edit" -> REMINDER_SUB_COMMAND_FUNCTIONS.update(event);
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