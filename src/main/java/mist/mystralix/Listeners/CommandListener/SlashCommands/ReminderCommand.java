package mist.mystralix.Listeners.CommandListener.SlashCommands;

import mist.mystralix.Listeners.CommandListener.CommandObjects.ReminderSubCommandFunctions;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public final class ReminderCommand implements SlashCommand {

    /*
        Command Name
            - remind
            - Usage: /remind [(me/create)/delete/list/update/view] <options>
    */
    @Override
    public String getName() {
        return "reminder";
    }

    /*
        Command Description
            - Handles all reminder-related features
            - Handles SubCommands: (me/create)/delete/list/update/view
    */
    @Override
    public String getDescription() {
        return "All reminder-related features, such as adding, deleting, updating, etc.";
    }

    @Override
    public SubcommandData[] getSubcommands() {
        return new SubcommandData[] {
            new SubcommandData(
                    "create",
                    "Reminds all reminders from the server"
            )
                .addOptions(
                    new OptionData(
                            OptionType.STRING,
                            "message",
                            "Reminder Message",
                            true
                    ),
                    new OptionData(
                            OptionType.STRING,
                            "time",
                            "[1d/1h/30m] | Example: 1h30m",
                            true
                    )
                ),
            new SubcommandData(
                    "delete",
                    "Deletes all reminders from the server"
            )
                .addOptions(
                    new OptionData(
                            OptionType.INTEGER,
                            "reminder_id",
                            "Reminder ID",
                            true
                    )
                ),
            new SubcommandData(
                    "list",
                    "Lists all reminders from the server"
            ),
            new SubcommandData(
                    "update",
                    "Updates all reminders from the server"
            )
                .addOptions(
                    new OptionData(
                        OptionType.INTEGER,
                        "reminder_id",
                        "Reminder ID",
                        true
                    ),
                    new OptionData(
                            OptionType.STRING,
                            "message",
                            "Reminder Message",
                            false
                    ),
                    new OptionData(
                            OptionType.STRING,
                            "time",
                            "[1d/1h/30m] | Example: 1h30m",
                            false
                    )
                ),
            new SubcommandData(
                    "view",
                    "Views all reminders from the server"
            )
                .addOptions(
                    new OptionData(
                            OptionType.INTEGER,
                            "reminder_id",
                            "Reminder ID",
                            true
                    )
                )
        };
    }

    /* Command Event/Output */
    @Override
    public void execute(SlashCommandInteraction event) {

        String subCommand = event.getSubcommandName();
        if (subCommand == null) {
            event.reply("Invalid subcommand.").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        ReminderSubCommandFunctions reminderSubCommandFunctions = new ReminderSubCommandFunctions();
        MessageEmbed messageEmbed;

        messageEmbed = switch (subCommand) {
            case "create" -> reminderSubCommandFunctions.create(event);
            case "delete" -> reminderSubCommandFunctions.delete(event);
            case "update" -> reminderSubCommandFunctions.update(event);
            case "view" -> reminderSubCommandFunctions.read(event);
            case "list" -> reminderSubCommandFunctions.readAll(event);
            default -> null;
        };

        if(messageEmbed == null) {
            event.getHook().editOriginal("❌ An error occurred (embed was null).").queue();
            return;
        }
        event.getHook().editOriginalEmbeds(messageEmbed).queue();

    }


}