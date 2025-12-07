package mist.mystralix.presentation.commands.slash.team;

import mist.mystralix.application.team.TeamService;
import mist.mystralix.presentation.commands.slash.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class TeamCommand implements SlashCommand {

    /** Service providing all team CRUD operations. */
    private final TeamService TEAM_SERVICE;

    public TeamCommand(TeamService teamService) {
        this.TEAM_SERVICE = teamService;
    }

    @Override
    public String getName() {
        return "team";
    }

    @Override
    public String getDescription() {
        return "Ability to group and organize teams with tasks";
    }

    @Override
    public SubcommandData[] getSubcommands() {
        return new SubcommandData[]{ };
    }

    @Override
    public void execute(SlashCommandInteraction event) {

    }
}