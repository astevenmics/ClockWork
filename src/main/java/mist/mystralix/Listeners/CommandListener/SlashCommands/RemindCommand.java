package mist.mystralix.Listeners.CommandListener.SlashCommands;

import mist.mystralix.Enums.TaskStatus;
import mist.mystralix.Listeners.CommandListener.CommandObjects.TaskSubCommandFunctions;
import mist.mystralix.Listeners.CommandListener.SlashCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class RemindCommand implements SlashCommand {

    /*
        Command Name
            - remind
            - Usage: /remind [(me/create)/delete/list/update/view] <options>
    */
    @Override
    public String getName() {
        return "remind";
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

            };
    }

    /* Command Event/Output */
    @Override
    public void execute(SlashCommandInteraction event) {


    }


}