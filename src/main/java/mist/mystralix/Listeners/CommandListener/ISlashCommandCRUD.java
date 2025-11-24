package mist.mystralix.Listeners.CommandListener;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

/*
    TODO: Update this comment
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
public interface ISlashCommandCRUD {

    /*
        Comment Here
    */
    MessageEmbed create(SlashCommandInteraction event);

    /*
        Comment Here
    */
    MessageEmbed read(SlashCommandInteraction event);

    /*
        Comment Here
    */
    MessageEmbed update(SlashCommandInteraction event);

    /*
        Comment Here
    */
    MessageEmbed delete(SlashCommandInteraction event);

}