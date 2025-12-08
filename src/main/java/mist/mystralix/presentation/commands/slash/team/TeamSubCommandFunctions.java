package mist.mystralix.presentation.commands.slash.team;

import mist.mystralix.application.team.TeamService;
import mist.mystralix.domain.team.Team;
import mist.mystralix.presentation.commands.slash.ISlashCommandCRUD;
import mist.mystralix.presentation.embeds.TeamEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.ArrayList;

public class TeamSubCommandFunctions implements ISlashCommandCRUD {

    private final TeamService TEAM_SERVICE;
    private final TeamEmbed TEAM_EMBED = new TeamEmbed();

    public TeamSubCommandFunctions(TeamService teamService) {
        this.TEAM_SERVICE = teamService;
    }

    @Override
    public MessageEmbed create(SlashCommandInteraction event) {
        OptionMapping optionName = event.getOption("team_name");
        if (optionName == null) {
            // TODO: Update return embed for errors
            return null;
        }

        User user = event.getUser();

        String name = optionName.getAsString();
        ArrayList<String> moderators = new ArrayList<>();
        moderators.add(user.getId());

        Team team = TEAM_SERVICE.create(name, moderators);

        return TEAM_EMBED.createMessageEmbed(user, "New Team", team);
    }

    @Override
    public MessageEmbed read(SlashCommandInteraction event) {
        return null;
    }

    @Override
    public MessageEmbed update(SlashCommandInteraction event) {
        return null;
    }

    @Override
    public MessageEmbed delete(SlashCommandInteraction event) {
        return null;
    }

    @Override
    public MessageEmbed readAll(SlashCommandInteraction event) {
        return null;
    }
}