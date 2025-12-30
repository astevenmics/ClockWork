package mist.mystralix.application.validator;

import mist.mystralix.domain.team.Team;
import mist.mystralix.presentation.embeds.IMessageEmbedBuilder;
import mist.mystralix.utils.messages.CommonMessages;
import mist.mystralix.utils.messages.TeamMessages;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class TeamValidator {

    public static boolean isUserPartOfTeam(Team team, String userId) {
        return team.getTeamLeader().equals(userId)
                || team.getModerators().contains(userId)
                || team.getMembers().contains(userId);
    }

    public static boolean isUserNotPartOfTeam(Team team, String userId) {
        return !isUserPartOfTeam(team, userId);
    }

    public static MessageEmbed validateTeam(
            User user,
            Team team,
            IMessageEmbedBuilder teamEmbed
    ) {
        if (team == null) {
            return teamEmbed.createErrorEmbed(user,
                    String.format(CommonMessages.OBJECT_NOT_FOUND, "team")
            );
        }

        return null;
    }

    public static MessageEmbed validateAccess(
            User user,
            Team team,
            IMessageEmbedBuilder teamEmbed
    ) {
        if (isUserNotPartOfTeam(team, user.getId())) {
            return teamEmbed.createErrorEmbed(user,
                    String.format(TeamMessages.NOT_PART_OF_TEAM, team.getTeamName())
            );
        }
        return null;
    }

    public static boolean isModerator(Team team, String userId) {
        return team.getModerators().contains(userId);
    }

    public static boolean isLeader(Team team, String userId) {
        return team.getTeamLeader().equals(userId);
    }


    // Slash Command Helper //

    public static MessageEmbed validateTeamAndAccess(
            User user,
            Team team,
            IMessageEmbedBuilder teamEmbed
    ) {
        MessageEmbed messageEmbed = validateTeam(user, team, teamEmbed);
        if (messageEmbed != null) {
            return messageEmbed;
        }
        return validateAccess(user, team, teamEmbed);
    }

    public static MessageEmbed validateTeamAndModeratorOrLeader(
            User user,
            Team team,
            IMessageEmbedBuilder teamEmbed
    ) {
        MessageEmbed messageEmbed = validateTeamAndAccess(user, team, teamEmbed);
        if (messageEmbed != null) {
            return messageEmbed;
        }
        if (!isLeader(team, user.getId()) && !isModerator(team, user.getId())) {
            return teamEmbed.createErrorEmbed(user, TeamMessages.MODERATOR_REQUIRED);
        }
        return null;
    }

    public static MessageEmbed validateTeamAndLeader(
            User user,
            Team team,
            IMessageEmbedBuilder teamEmbed
    ) {
        MessageEmbed messageEmbed = validateTeamAndAccess(user, team, teamEmbed);
        if (messageEmbed != null) {
            return messageEmbed;
        }
        return isLeader(team, user.getId()) ? null : teamEmbed.createErrorEmbed(user, TeamMessages.LEADER_REQUIRED);
    }

}
