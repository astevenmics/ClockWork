package mist.mystralix.presentation.commands.slash;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public interface SlashCommand {

    String getName();

    String getDescription();

    SubcommandData[] getSubcommands();

    default SubcommandGroupData[] getSubcommandGroupData() {
        return new SubcommandGroupData[0];
    }

    void execute(SlashCommandInteraction event);
}