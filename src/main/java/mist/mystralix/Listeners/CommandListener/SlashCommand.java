package mist.mystralix.Listeners.CommandListener;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/*
    SlashCommand Interface
        - Responsible in making sure that all commands that uses the SlashCommand feature have all the required options
        - getName()        | Responsible for getting the name of the command
            * Example: getName() { "task"; }
        - getDescription() | Responsible for holding the information of what the command is.
                           | Description is used for displaying a brief description of what the command is/does
       - getOptions()      | Responsible for storing the options (if present) that will later be added in the command
                           | Array contains OptionData values, whether an option is required, id, and such.
       - execute(SlashCommandInteraction event)
                           | Function containing the actions done as the command is executed
*/
public interface SlashCommand {

    /*
        Command Name
    */
    String getName();

    /*
        Command Name
    */
    String getDescription();

    /*
        Command Options
    */
    OptionData[] getOptions();

    /*
        Command Event/Output
    */
    void execute(SlashCommandInteraction event);

}