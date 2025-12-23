package mist.mystralix.application.validator;

import mist.mystralix.application.team.TeamService;
import mist.mystralix.application.validationresult.TeamValidationResult;
import mist.mystralix.domain.team.Team;
import mist.mystralix.presentation.embeds.IMessageEmbedBuilder;
import mist.mystralix.utils.messages.CommonMessages;
import mist.mystralix.utils.messages.TeamMessages;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.Objects;

public class TeamValidator {

    // Checks if the team exists
    // Checks if the user is part of the team
    public static MessageEmbed validateTeamAndAccess(
            User user,
            TeamService teamService,
            IMessageEmbedBuilder teamEmbed,
            int teamId
    ) {
        Team team = teamService.findByID(teamId);
        if (team == null) {
            return teamEmbed.createErrorEmbed(user,
                    String.format(
                            CommonMessages.OBJECT_NOT_FOUND,
                            "team"
                    ));
        }

        String userId = user.getId();
        if (!team.getTeamLeader().equals(userId) && !team.getModerators().contains(userId) && !team.getMembers().contains(userId)) {
            return teamEmbed.createErrorEmbed(user,
                    String.format(
                            TeamMessages.NOT_PART_OF_TEAM,
                            team.getTeamName()
                    ));
        }

        return null;
    }

    // Through validateTeamAndAccess | Checks if the team exists
    // Through validateTeamAndAccess | Checks if the user is part of the team
    // Checks if user is either a Team Leader or a Moderator
    public static MessageEmbed validateTeamAndPermission(
            User user,
            TeamService teamService,
            IMessageEmbedBuilder teamEmbed,
            int teamId
    ) {
        MessageEmbed messageEmbed = validateTeamAndAccess(user, teamService, teamEmbed, teamId);
        if (messageEmbed != null) {
            return messageEmbed;
        }
        Team team = teamService.findByID(teamId);
        String userId = user.getId();
        if (!team.getTeamLeader().equals(userId) && !team.getModerators().contains(userId)) {
            return teamEmbed.createErrorEmbed(user, TeamMessages.MODERATOR_REQUIRED);
        }

        return null;
    }

    // Through validateTeamAndAccess | Checks if the team exists
    // Through validateTeamAndAccess | Checks if the user is part of the team
    // Checks if user is a Team Leader
    public static MessageEmbed validateTeamAndLeadership(
            User user,
            TeamService teamService,
            IMessageEmbedBuilder teamEmbed,
            int teamId
    ) {
        MessageEmbed messageEmbed = validateTeamAndAccess(user, teamService, teamEmbed, teamId);
        if (messageEmbed != null) {
            return messageEmbed;
        }
        Team team = teamService.findByID(teamId);
        String userId = user.getId();
        if (!team.getTeamLeader().equals(userId)) {
            return teamEmbed.createErrorEmbed(user, TeamMessages.LEADER_REQUIRED);
        }

        return null;
    }

    public static TeamValidationResult validateTeamAndGetTeam(
            SlashCommandInteraction event,
            TeamService teamService,
            IMessageEmbedBuilder teamEmbed,
            String... requiredOptions
    ) {
        User user = event.getUser();
        for (String option : requiredOptions) {
            if (event.getOption(option) == null) {
                return new TeamValidationResult(teamEmbed.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS), null);
            }
        }

        OptionMapping teamOption = event.getOption("id");
        MessageEmbed errorEmbed = TeamValidator.validateTeamAndLeadership(user, teamService, teamEmbed, Objects.requireNonNull(teamOption).getAsInt());
        if (errorEmbed != null) {
            return new TeamValidationResult(errorEmbed, null);
        }

        Team team = teamService.findByID(teamOption.getAsInt());
        return new TeamValidationResult(null, team);
    }

}
