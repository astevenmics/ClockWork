package mist.mystralix.application.validator;

import mist.mystralix.application.team.TeamService;
import mist.mystralix.application.team.TeamTaskService;
import mist.mystralix.domain.records.TeamTaskValidationResult;
import mist.mystralix.domain.task.TeamTask;
import mist.mystralix.domain.team.Team;
import mist.mystralix.presentation.embeds.TeamTaskEmbed;
import mist.mystralix.utils.Constants;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.Objects;

public class TeamTaskValidator {

    public static boolean isUserNotPartOfTeam(Team team, String userId) {
        return !team.getModerators().contains(userId) && !team.getMembers().contains(userId);
    }

    public static MessageEmbed validateInputAndTeamAndPermissionAndTeamTask(
            SlashCommandInteraction event,
            TeamService teamService,
            TeamTaskService teamTaskService,
            TeamTaskEmbed teamTaskEmbed
    ) {
        User user = event.getUser();

        OptionMapping teamOption = event.getOption("team");
        OptionMapping taskOption = event.getOption("task");

        if (teamOption == null || taskOption == null) {
            return teamTaskEmbed.createErrorEmbed(user, Constants.MISSING_PARAMETERS.getValue(String.class));
        }

        int teamId = teamOption.getAsInt();
        int taskId = taskOption.getAsInt();

        MessageEmbed messageEmbed = validateTeamAndPermission(user, teamService, teamTaskEmbed, teamId);
        if (messageEmbed != null) {
            return messageEmbed;
        }
        Team team = teamService.findByID(teamId);
        TeamTask teamTask = teamTaskService.getById(taskId);
        if (teamTask == null) {
            return teamTaskEmbed.createErrorEmbed(user,
                    String.format(
                            Constants.OBJECT_NOT_FOUND.getValue(String.class),
                            "Team Task"
                    ));
        }

        if (!team.getTasksUUID().contains(teamTask.getUUID())) {
            return teamTaskEmbed.createErrorEmbed(user,
                    String.format(
                            Constants.TEAM_TASK_NOT_PART_OF_TEAM.getValue(String.class),
                            taskId,
                            team.getTeamName()
                    ));
        }
        return null;
    }

    public static MessageEmbed validateTeamAndAccess(
            User user,
            TeamService teamService,
            TeamTaskEmbed teamTaskEmbed,
            int teamId) {
        Team team = teamService.findByID(teamId);
        if (team == null) {
            return teamTaskEmbed.createErrorEmbed(user,
                    String.format(
                            Constants.OBJECT_NOT_FOUND.getValue(String.class),
                            "team"
                    ));
        }

        String userId = user.getId();
        if (!team.getTeamLeader().equals(userId) && !team.getModerators().contains(userId) && !team.getMembers().contains(userId)) {
            return teamTaskEmbed.createErrorEmbed(user,
                    String.format(
                            Constants.USER_NOT_PART_OF_THE_TEAM.getValue(String.class),
                            team.getTeamName()
                    ));
        }

        return null;
    }

    public static MessageEmbed validateTeamAndPermission(
            User user,
            TeamService teamService,
            TeamTaskEmbed teamTaskEmbed,
            int teamId
    ) {
        MessageEmbed messageEmbed = validateTeamAndAccess(user, teamService, teamTaskEmbed, teamId);
        if (messageEmbed != null) {
            return messageEmbed;
        }
        Team team = teamService.findByID(teamId);
        String userId = user.getId();
        if (!team.getTeamLeader().equals(userId) && !team.getModerators().contains(userId)) {
            return teamTaskEmbed.createErrorEmbed(user, Constants.TEAM_MODERATOR_OR_HIGHER_REQUIRED.getValue(String.class));
        }

        return null;
    }

    public static TeamTaskValidationResult getTeamTask(
            SlashCommandInteraction event,
            TeamService teamService,
            TeamTaskService teamTaskService
    ) {
        int teamId = Objects.requireNonNull(event.getOption("team")).getAsInt();
        int taskId = Objects.requireNonNull(event.getOption("task")).getAsInt();
        Team team = teamService.findByID(teamId);
        TeamTask teamTask = teamTaskService.getById(taskId);

        return new TeamTaskValidationResult(null, team, teamTask);
    }

    public static TeamTaskValidationResult validateAndGetTeamTask(
            SlashCommandInteraction event,
            TeamService teamService,
            TeamTaskService teamTaskService,
            TeamTaskEmbed teamTaskEmbed
    ) {
        MessageEmbed messageEmbed = validateInputAndTeamAndPermissionAndTeamTask(
                event,
                teamService,
                teamTaskService,
                teamTaskEmbed
        );
        if (messageEmbed != null) {
            return new TeamTaskValidationResult(messageEmbed, null, null);
        }

        return getTeamTask(event, teamService, teamTaskService);
    }

}
