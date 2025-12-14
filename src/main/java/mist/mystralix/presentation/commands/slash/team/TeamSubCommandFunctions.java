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

    public MessageEmbed add(SlashCommandInteraction event) {

        User user = event.getUser();

        MessageEmbed messageEmbed, invitationEmbed;
        OptionMapping userOption = event.getOption("user");
        OptionMapping idOption = event.getOption("id");
        if (userOption == null || idOption == null) {
            // TODO: Update return embed for errors
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    "Please provide a team ID and mention a user"
            );
        }

        User userToAdd = userOption.getAsUser();
        int id = idOption.getAsInt();

        Team team = TEAM_SERVICE.findByID(id);
        if(team == null) {
            // Team does not exist
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    "Team does not exist"
            );
        }

        ArrayList<String> moderators = team.getModerators();
        ArrayList<String> members = team.getMembers();
        ArrayList<String> teamInvitations = team.getTeamInvitations();

        if(!team.getModerators().contains(user.getId())) {
            // You are not allowed/permitted to add users in this team
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    "You need to be either the owner/moderator of a team to invite a user"
            );
        }

        if(moderators.contains(userToAdd.getId()) || members.contains(userToAdd.getId())) {
            // User already in the team
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    "The user " + userToAdd.getAsMention() + " is already part of the team."
            );
        }

        if(teamInvitations.contains(userToAdd.getId())) {
            // User already invited
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    "The user " + userToAdd.getAsMention() + " has already been invited."
            );
        }

        invitationEmbed = TEAM_EMBED.createInvitationToUserEmbed(user, userToAdd, team);
        messageEmbed = TEAM_EMBED.createInvitationEmbed(user, userToAdd, team);

        userToAdd.openPrivateChannel().queue(channel -> channel.sendMessageEmbeds(invitationEmbed).queue());

        // Pending invitation
        teamInvitations.add(userToAdd.getId());
        team.setTeamInvitations(teamInvitations);

        TEAM_SERVICE.update(team);

        return messageEmbed;
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
        User user = event.getUser();

        ArrayList<Team> userTeams = TEAM_SERVICE.getUserTeams(user.getId());

        return TEAM_EMBED.createListEmbed(user, userTeams);
    }
}