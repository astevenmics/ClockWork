package mist.mystralix.presentation.commands.slash.team.task;

import mist.mystralix.application.team.TeamService;
import mist.mystralix.application.team.TeamTaskService;
import mist.mystralix.domain.enums.TaskStatus;
import mist.mystralix.domain.task.TaskDAO;
import mist.mystralix.domain.task.TeamTask;
import mist.mystralix.domain.team.Team;
import mist.mystralix.presentation.commands.slash.ISlashCommandCRUD;
import mist.mystralix.presentation.embeds.TeamTaskEmbed;
import mist.mystralix.utils.Constants;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class TeamTaskSubCommandFunctions implements ISlashCommandCRUD {

    private final TeamTaskService TEAM_TASK_SERVICE;
    private final TeamService TEAM_SERVICE;
    private final TeamTaskEmbed TEAM_TASK_EMBED = new TeamTaskEmbed();

    public TeamTaskSubCommandFunctions(
            TeamTaskService teamTaskService,
            TeamService teamService
    ) {
        this.TEAM_TASK_SERVICE = teamTaskService;
        this.TEAM_SERVICE = teamService;
    }


    @Override
    public MessageEmbed create(SlashCommandInteraction event) {

        User user = event.getUser();
        String userId = user.getId();

        OptionMapping teamOption = event.getOption("team");
        OptionMapping titleOption = event.getOption("title");
        OptionMapping descriptionOption = event.getOption("description");
        if (teamOption == null || titleOption == null || descriptionOption == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(user,
                    Constants.MISSING_PARAMETERS.getValue(String.class)
            );
        }

        int teamId = teamOption.getAsInt();
        String title = titleOption.getAsString();
        String description = descriptionOption.getAsString();

        Team team = TEAM_SERVICE.findByID(teamId);
        if (team == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(
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

        if(!teamLeaderId.equals(userId) && !moderators.contains(userId) && !members.contains(userId)) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.USER_NOT_PART_OF_THE_TEAM.getValue(String.class),
                            team.getTeamName()
                    )
            );
        }

        if(!teamLeaderId.equals(user.getId()) && !team.getModerators().contains(user.getId())) {
            // You are not allowed/permitted to add users in this team
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    Constants.TEAM_MODERATOR_OR_HIGHER_REQUIRED.getValue(String.class)
            );
        }

        String uuid = UUID.randomUUID().toString();
        TEAM_TASK_SERVICE.create(
                uuid,
                userId,
                team.getUUID(),
                teamId,
                new TaskDAO(
                        title,
                        description,
                        TaskStatus.INPROGRESS
                )
        );

        ArrayList<String> teamTasksUUID = team.getTasksUUID();
        teamTasksUUID.add(uuid);
        TEAM_SERVICE.update(team);

        return TEAM_TASK_EMBED.createTeamTaskCreatedEmbed(
                user,
                TEAM_TASK_SERVICE.getByUUID(uuid),
                event
        );
    }

    @Override
    public MessageEmbed read(SlashCommandInteraction event) {

        User user = event.getUser();
        String userId = user.getId();

        OptionMapping teamOption = event.getOption("team");
        OptionMapping taskOption = event.getOption("task");

        if (teamOption == null || taskOption == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    Constants.MISSING_PARAMETERS.getValue(String.class)
            );
        }

        Team team = TEAM_SERVICE.findByID(teamOption.getAsInt());
        if (team == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    String.format(Constants.OBJECT_NOT_FOUND.getValue(String.class),
                            "team"
                    )
            );
        }

        if (
                !team.getTeamLeader().equals(userId) &&
                        !team.getModerators().contains(userId) &&
                        !team.getMembers().contains(userId)
        ) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.USER_NOT_PART_OF_THE_TEAM.getValue(String.class),
                            team.getTeamName()
                    )
            );
        }

        TeamTask teamTask = TEAM_TASK_SERVICE.getById(taskOption.getAsInt());
        if (teamTask == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    String.format(Constants.OBJECT_NOT_FOUND.getValue(String.class),
                            "teamTask"
                    )
            );
        }

        if (!team.getTasksUUID().contains(teamTask.getUUID())) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.TEAM_TASK_NOT_PART_OF_TEAM.getValue(String.class),
                            teamTask.getId(),
                            team.getTeamName()
                    )
            );
        }

        return TEAM_TASK_EMBED.createMessageEmbed(
                user,
                "Team Information",
                teamTask
        );
    }

    @Override
    public MessageEmbed update(SlashCommandInteraction event) {
        User user = event.getUser();
        String userId = user.getId();

        OptionMapping teamIdOption = event.getOption("team");
        OptionMapping taskIdOption = event.getOption("task");
        OptionMapping titleOption = event.getOption("title");
        OptionMapping descriptionOption = event.getOption("description");
        OptionMapping statusOption = event.getOption("type");

        if (teamIdOption == null || taskIdOption == null || titleOption == null && descriptionOption == null && statusOption == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    Constants.MISSING_PARAMETERS.getValue(String.class)
            );
        }

        Team team = TEAM_SERVICE.findByID(teamIdOption.getAsInt());
        if (team == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.OBJECT_NOT_FOUND.getValue(String.class),
                            "Team"
                    )
            );
        }

        String teamLeaderId = team.getTeamLeader();
        ArrayList<String> moderators = team.getModerators();
        ArrayList<String> members = team.getMembers();
        ArrayList<String> teamTasksUUID = team.getTasksUUID();

        if (!teamLeaderId.equals(userId) && !moderators.contains(userId) && !members.contains(userId)) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.USER_NOT_PART_OF_THE_TEAM.getValue(String.class),
                            team.getTeamName()
                    )
            );
        }

        if (!teamLeaderId.equals(user.getId()) && !moderators.contains(user.getId())) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    Constants.TEAM_MODERATOR_OR_HIGHER_REQUIRED.getValue(String.class)
            );
        }

        TeamTask teamTask = TEAM_TASK_SERVICE.getById(taskIdOption.getAsInt());
        if (teamTask == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.OBJECT_NOT_FOUND.getValue(String.class),
                            "Team Task"
                    )
            );
        }

        if (!teamTasksUUID.contains(teamTask.getUUID())) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.TEAM_TASK_NOT_PART_OF_TEAM.getValue(String.class),
                            teamTask.getId(),
                            team.getTeamName()
                    )
            );
        }

        String newTitle = event.getOption("title",
                () -> null,
                OptionMapping::getAsString
        );
        String newDesc = event.getOption("description",
                () -> null,
                OptionMapping::getAsString
        );
        int statusInt = event.getOption("type",
                () -> 0,
                OptionMapping::getAsInt
        );

        TaskStatus newStatus = TaskStatus.getTaskStatus(statusInt);
        TaskDAO dao = teamTask.getTaskDAO();

        Optional.ofNullable(newTitle).ifPresent(dao::setTitle);
        Optional.ofNullable(newDesc).ifPresent(dao::setDescription);
        Optional.ofNullable(newStatus).ifPresent(dao::setTaskStatus);

        TEAM_TASK_SERVICE.updateTeamTask(teamTask);

        return TEAM_TASK_EMBED.createMessageEmbed(user, "Updated Task", teamTask);
    }

    @Override
    public MessageEmbed delete(SlashCommandInteraction event) {

        User user = event.getUser();
        String userId = user.getId();
        OptionMapping teamOption = event.getOption("team");
        OptionMapping taskOption = event.getOption("task");

        if (teamOption == null || taskOption == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    Constants.MISSING_PARAMETERS.getValue(String.class)
            );
        }

        int teamId = teamOption.getAsInt();
        int teamTaskId = taskOption.getAsInt();

        Team team = TEAM_SERVICE.findByID(teamId);
        if (team == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(
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

        if (!teamLeaderId.equals(userId) && !moderators.contains(userId) && !members.contains(userId)) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.USER_NOT_PART_OF_THE_TEAM.getValue(String.class),
                            team.getTeamName()
                    )
            );
        }

        if (!teamLeaderId.equals(user.getId()) && !team.getModerators().contains(user.getId())) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    Constants.TEAM_MODERATOR_OR_HIGHER_REQUIRED.getValue(String.class)
            );
        }

        TeamTask teamTask = TEAM_TASK_SERVICE.getById(teamTaskId);
        if (teamTask == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.OBJECT_NOT_FOUND.getValue(String.class),
                            "Team Task"
                    )
            );
        }

        if (!team.getTasksUUID().contains(teamTask.getUUID())) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.TEAM_TASK_NOT_PART_OF_TEAM.getValue(String.class),
                            teamTaskId,
                            team.getTeamName()
                    )
            );
        }

        ArrayList<String> teamTasksUUID = team.getTasksUUID();
        teamTasksUUID.remove(teamTask.getUUID());
        TEAM_SERVICE.update(team);
        TEAM_TASK_SERVICE.delete(teamTask);

        return TEAM_TASK_EMBED.createMessageEmbed(
                user,
                "Team Task Deleted",
                teamTask
        );
    }

    @Override
    public MessageEmbed readAll(SlashCommandInteraction event) {

        User user = event.getUser();
        String userId = user.getId();

        OptionMapping teamOption = event.getOption("team");

        if (teamOption == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    Constants.MISSING_PARAMETERS.getValue(String.class)
            );
        }

        int teamID = teamOption.getAsInt();
        Team team = TEAM_SERVICE.findByID(teamID);
        if (team == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    String.format(Constants.OBJECT_NOT_FOUND.getValue(String.class),
                            "team"
                    )
            );
        }

        if (
                !team.getTeamLeader().equals(userId) &&
                        !team.getModerators().contains(userId) &&
                        !team.getMembers().contains(userId)
        ) {
            return TEAM_TASK_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            Constants.USER_NOT_PART_OF_THE_TEAM.getValue(String.class),
                            team.getTeamName()
                    )
            );
        }

        return TEAM_TASK_EMBED.createListEmbed(
                user,
                TEAM_TASK_SERVICE.findAllByTeamID(teamID)
        );
    }
}