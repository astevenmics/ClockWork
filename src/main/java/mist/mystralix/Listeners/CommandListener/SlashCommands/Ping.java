package mist.mystralix.Listeners.CommandListener.SlashCommands;

import mist.mystralix.Listeners.CommandListener.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class Ping implements SlashCommand {

    /* Command Name */
    /* /ping */
    @Override
    public String getName() {
        return "ping";
    }

    /* Command Description */
    @Override
    public String getDescription() {
        return "A test command";
    }

    /* Command Options */
    @Override
    public OptionData[] getOptions() {
        return new OptionData[0];
    }

    /* Command Event/Output */
    @Override
    public void execute(SlashCommandInteraction event) {
        event.reply("Pong!").queue();
    }
}