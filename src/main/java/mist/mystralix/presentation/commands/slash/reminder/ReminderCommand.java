package mist.mystralix.presentation.commands.slash.reminder;

import mist.mystralix.application.reminder.ReminderService;
import mist.mystralix.presentation.commands.slash.SlashCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * Slash command entrypoint for all reminder-related features.
 *
 * <p>This command acts as the command router for the `/reminder` namespace,
 * delegating subcommand execution to {@link ReminderSubCommandFunctions}.
 *
 * <p>All logic is delegated (composition over inheritance), making the class
 * a thin command dispatcher and keeping the command clean and maintainable.
 *
 * <p>Example usage:
 * <ul>
 *     <li>/reminder create message:"Drink water" time:30m</li>
 *     <li>/reminder delete reminder_id:3</li>
 *     <li>/reminder list</li>
 *     <li>/reminder update reminder_id:1 time:2h</li>
 *     <li>/reminder view reminder_id:7</li>
 * </ul>
 */
public final class ReminderCommand implements SlashCommand {

    /** Handles the actual business logic for each reminder subcommand. */
    private final ReminderSubCommandFunctions REMINDER_SUB_COMMAND_FUNCTIONS;

    /**
     * Constructs the ReminderCommand and injects dependencies.
     *
     * @param reminderService the service handling reminder operations
     */
    public ReminderCommand(ReminderService reminderService) {
        this.REMINDER_SUB_COMMAND_FUNCTIONS = new ReminderSubCommandFunctions(reminderService);
    }

    /** @return the slash command name: {@code "reminder"} */
    @Override
    public String getName() {
        return "reminder";
    }

    /** @return the description displayed in Discord’s command menu */
    @Override
    public String getDescription() {
        return "All reminder-related features, such as adding, deleting, updating, etc.";
    }

    /**
     * Defines all subcommands and their parameters for Discord registration.
     *
     * @return an array containing all subcommands under `/reminder`
     */
    @Override
    public SubcommandData[] getSubcommands() {
        return new SubcommandData[] {

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
                )
        };
    }

    /**
     * Executes the appropriate reminder subcommand based on user input.
     *
     * <p>This method:
     * <ol>
     *     <li>Retrieves the subcommand name</li>
     *     <li>Delegates to {@link ReminderSubCommandFunctions}</li>
     *     <li>Sends the resulting embed to Discord</li>
     * </ol>
     *
     * @param event the slash command interaction from Discord
     */
    @Override
    public void execute(SlashCommandInteraction event) {

        String subCommand = event.getSubcommandName();

        // Invalid or missing subcommand — prevent null pointer issues
        if (subCommand == null) {
            event.reply("Invalid subcommand.").setEphemeral(true).queue();
            return;
        }

        // Let Discord know we are working (prevents timeout)
        event.deferReply().queue();

        // Delegate the action to the appropriate handler
        MessageEmbed messageEmbed = switch (subCommand) {
            case "create" -> REMINDER_SUB_COMMAND_FUNCTIONS.create(event);
            case "delete" -> REMINDER_SUB_COMMAND_FUNCTIONS.delete(event);
            case "update" -> REMINDER_SUB_COMMAND_FUNCTIONS.update(event);
            case "view"   -> REMINDER_SUB_COMMAND_FUNCTIONS.read(event);
            case "list"   -> REMINDER_SUB_COMMAND_FUNCTIONS.readAll(event);
            default -> null;
        };

        // Handle unexpected null result
        if (messageEmbed == null) {
            event.getHook().editOriginal("❌ An error occurred (embed was null).").queue();
            return;
        }

        // Send the actual response back to Discord
        event.getHook().editOriginalEmbeds(messageEmbed).queue();
    }
}
