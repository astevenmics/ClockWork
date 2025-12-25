package mist.mystralix.presentation.commands.slash;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.Collections;
import java.util.List;

public interface SlashCommand {

    String getName();

    String getDescription();

    default List<SubcommandData> getSubcommands() {
        return Collections.emptyList();
    }

    default List<SubcommandGroupData> getSubcommandGroupData() {
        return Collections.emptyList();
    }

    void execute(SlashCommandInteraction event);

}