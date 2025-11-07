package mist.mystralix.Listeners.CommandListener;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public interface SlashCommand {

    String getName(); // Command Name
    String getDescription(); // Command Description
    OptionData[] getOptions(); // Command Options
    void execute(SlashCommandInteraction event); // Command Event/Output

}