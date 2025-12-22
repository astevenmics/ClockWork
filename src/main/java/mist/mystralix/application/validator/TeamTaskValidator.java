package mist.mystralix.application.validator;

import mist.mystralix.application.team.TeamService;
import mist.mystralix.application.team.TeamTaskService;
import mist.mystralix.application.validationresult.TeamTaskValidationResult;
import mist.mystralix.domain.task.TeamTask;
import mist.mystralix.domain.team.Team;
import mist.mystralix.presentation.embeds.IMessageEmbedBuilder;
import mist.mystralix.utils.Constants;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.Objects;

public class TeamTaskValidator {

    // Checks if the mentioned user is already part of the team
    public static boolean isUserMentionedNotPartOfTeam(Team team, String userId) {
        return !team.getTeamLeader().equals(userId) && !team.getModerators().contains(userId) && !team.getMembers().contains(userId);
    }

    // Through validateTeamAndPermission | Checks if the team exists
    // Through validateTeamAndPermission | Checks if the user is part of the team
    // Through validateTeamAndPermission | Checks if the user is either a Team Leader or a Moderator
    // Checks if TeamTask exists
    // Checks if the TeamTask is part of the team
    public static MessageEmbed validateInputAndTeamAndPermissionAndTeamTask(
            SlashCommandInteraction event,
            TeamService teamService,
            TeamTaskService teamTaskService,
            IMessageEmbedBuilder embedBuilder
    ) {
        User user = event.getUser();

        OptionMapping teamOption = event.getOption("team");
        OptionMapping taskOption = event.getOption("task");

        if (teamOption == null || taskOption == null) {
            return embedBuilder.createErrorEmbed(user, Constants.MISSING_PARAMETERS.getValue(String.class));
        }

        int teamId = teamOption.getAsInt();
        int taskId = taskOption.getAsInt();

        MessageEmbed messageEmbed = TeamValidator.validateTeamAndPermission(user, teamService, embedBuilder, teamId);
        if (messageEmbed != null) {
            return messageEmbed;
        }
        Team team = teamService.findByID(teamId);
        TeamTask teamTask = teamTaskService.getById(taskId);
        if (teamTask == null) {
            return embedBuilder.createErrorEmbed(user,
                    String.format(
                            Constants.OBJECT_NOT_FOUND.getValue(String.class),
                            "Team Task"
                    ));
        }

        if (!team.getTasksUUID().contains(teamTask.getUUID())) {
            return embedBuilder.createErrorEmbed(user,
                    String.format(
                            Constants.TEAM_TASK_NOT_PART_OF_TEAM.getValue(String.class),
                            taskId,
                            team.getTeamName()
                    ));
        }
        return null;
    }

    // Through validateTeamAndPermission | Checks if the team exists
    // Through validateTeamAndPermission | Checks if the user is part of the team
    // Through validateTeamAndPermission | Checks if the user is either a Team Leader or a Moderator
    // Through validateInputAndTeamAndPermissionAndTeamTask | Checks if TeamTask exists
    // Through validateInputAndTeamAndPermissionAndTeamTask | Checks if the TeamTask is part of the team
    // Simply returns a team and teamTask object
    public static TeamTaskValidationResult validateAndGetTeamTask(
            SlashCommandInteraction event,
            TeamService teamService,
            TeamTaskService teamTaskService,
            IMessageEmbedBuilder embedBuilder
    ) {
        MessageEmbed messageEmbed = validateInputAndTeamAndPermissionAndTeamTask(
                event,
                teamService,
                teamTaskService,
                embedBuilder
        );
        if (messageEmbed != null) {
            return new TeamTaskValidationResult(messageEmbed, null, null);
        }

        int teamId = Objects.requireNonNull(event.getOption("team")).getAsInt();
        int taskId = Objects.requireNonNull(event.getOption("task")).getAsInt();
        Team team = teamService.findByID(teamId);
        TeamTask teamTask = teamTaskService.getById(taskId);

        return new TeamTaskValidationResult(null, team, teamTask);
    }

}
