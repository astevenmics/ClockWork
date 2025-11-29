package mist.mystralix.Listeners.CommandListener.SlashCommands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/*
    SlashCommand Interface
        - Responsible in making sure that all commands that uses the SlashCommand feature have all the required options
        - getName()        | Responsible for getting the name of the command
            * Example: getName() { "task"; }
        - getDescription() | Responsible for holding the information of what the command is.
                           | Description is used for displaying a brief description of what the command is/does
        - getSubcommands() | Responsible for storing the subcommand data (if present) that will later be added in the command
                           | Array contains SubcommandData values, with all of its information/options
        - execute(SlashCommandInteraction event)
                           | Function containing the actions done as the command is executed
*/
public sealed interface SlashCommand permits ReminderCommand, TaskCommand {

    /*
        Command Name
    */
    String getName();

    /*
        Command Name
    */
    String getDescription();

    /*
        Subcommand Data
    */
    SubcommandData[] getSubcommands();

    /*
        Command Event/Output
    */
    void execute(SlashCommandInteraction event);

}