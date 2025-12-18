package mist.mystralix.presentation.commands.slash.team;

import mist.mystralix.application.team.TeamService;
import mist.mystralix.domain.team.Team;
import mist.mystralix.presentation.commands.slash.ISlashCommandCRUD;
import mist.mystralix.presentation.embeds.TeamEmbed;
import mist.mystralix.utils.Constants;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.ArrayList;

public class TeamSubCommandFunctions implements ISlashCommandCRUD {

    // TODO: Add check if user mentioned is the same as the user
    // TODO: Add check if the user mentioned is a bot

    private final TeamService TEAM_SERVICE;
    private final TeamEmbed TEAM_EMBED = new TeamEmbed();

    public TeamSubCommandFunctions(TeamService teamService) {
        this.TEAM_SERVICE = teamService;
    }

    @Override
    public MessageEmbed create(SlashCommandInteraction event) {
        User user = event.getUser();
        OptionMapping optionName = event.getOption("team_name");
        if (optionName == null) {
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    Constants.MISSING_PARAMETERS.getValue(String.class)
            );
        }


        String name = optionName.getAsString();
        Team team = TEAM_SERVICE.create(name, user.getId());

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
                    Constants.MISSING_PARAMETERS.getValue(String.class)
            );
        }

        User userToAdd = userOption.getAsUser();
        String userToAddMention = userToAdd.getAsMention();
        int id = idOption.getAsInt();

        Team team = TEAM_SERVICE.findByID(id);
        if(team == null) {
            // Team does not exist
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.OBJECT_NOT_FOUND.getValue(String.class),
                            "team"
                    )
            );
        }

        String teamLeaderId = team.getTeamLeader();
        ArrayList<String> moderators = team.getModerators();
        ArrayList<String> members = team.getMembers();
        ArrayList<String> teamInvitations = team.getTeamInvitations();

        if(!teamLeaderId.equals(user.getId()) && !team.getModerators().contains(user.getId())) {
            // You are not allowed/permitted to add users in this team
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    Constants.TEAM_MODERATOR_OR_HIGHER_REQUIRED.getValue(String.class)
            );
        }

        if(moderators.contains(userToAdd.getId()) || members.contains(userToAdd.getId())) {
            // User already in the team
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.USER_ALREADY_PART_OF_THE_TEAM.getValue(String.class),
                            userToAddMention,
                            team.getTeamName()
                    )
            );
        }

        if(teamInvitations.contains(userToAdd.getId())) {
            // User already invited
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.USER_ALREADY_INVITED.getValue(String.class),
                            userToAddMention
                    )
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

    public MessageEmbed remove(SlashCommandInteraction event) {

        User user = event.getUser();
        String userId = user.getId();

        OptionMapping idOption = event.getOption("id");
        OptionMapping userOption = event.getOption("user");
        if (idOption == null || userOption == null) {
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    Constants.MISSING_PARAMETERS.getValue(String.class)
            );
        }

        int teamId = idOption.getAsInt();
        Team team = TEAM_SERVICE.findByID(teamId);
        if(team == null) {
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.OBJECT_NOT_FOUND.getValue(String.class),
                            "team"
                    )
            );
        }

        User userToRemove = userOption.getAsUser();
        String userToRemoveId = userToRemove.getId();

        String teamLeaderID = team.getTeamLeader();
        ArrayList<String> moderators = team.getModerators();
        ArrayList<String> members = team.getMembers();

        if(teamLeaderID.equals(userToRemoveId) || moderators.contains(userToRemoveId)) {
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    Constants.TEAM_CO_MODERATORS_ERROR.getValue(String.class)
            );
        }

        if(!moderators.contains(userToRemove.getId()) && !members.contains(userToRemove.getId())) {
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    Constants.USER_NOT_PART_OF_THE_TEAM.getValue(String.class)
            );
        }

        if(teamLeaderID.equals(userId)) {
            moderators.remove(userToRemoveId);
            members.remove(userToRemoveId);
            TEAM_SERVICE.update(team);
        } else if(moderators.contains(userId)) {
            members.remove(userToRemoveId);
        } else {
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    Constants.TEAM_MODERATOR_OR_HIGHER_REQUIRED.getValue(String.class)
            );
        }

        TEAM_SERVICE.update(team);

        return TEAM_EMBED.createRemovedMemberEmbed(user, userToRemove, team);
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

        User user = event.getUser();

        OptionMapping optionName = event.getOption("id");
        if (optionName == null) {
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    Constants.MISSING_PARAMETERS.getValue(String.class)
            );
        }
        int teamId = optionName.getAsInt();

        Team team = TEAM_SERVICE.findByID(teamId);
        if(team == null) {
            // Team does not exist
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.OBJECT_NOT_FOUND.getValue(String.class),
                            "team"
                    )
            );
        }
        String teamLeaderId = team.getTeamLeader();

        // Only team leader can delete the team
        if(!teamLeaderId.equals(user.getId())) {
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    Constants.TEAM_LEADER_REQUIRED.getValue(String.class)
            );
        }

        TEAM_SERVICE.delete(team);
        return TEAM_EMBED.createMessageEmbed(user, "Team has been deleted", team);
    }

    @Override
    public MessageEmbed readAll(SlashCommandInteraction event) {
        User user = event.getUser();

        ArrayList<Team> userTeams = TEAM_SERVICE.getUserTeams(user.getId());

        return TEAM_EMBED.createListEmbed(user, userTeams);
    }
}