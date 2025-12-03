package mist.mystralix.presentation.commands.slash.team;

import mist.mystralix.presentation.commands.slash.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class TeamCommand implements SlashCommand {
    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public SubcommandData[] getSubcommands() {
        return new SubcommandData[0];
    }

    @Override
    public void execute(SlashCommandInteraction event) {

    }
}