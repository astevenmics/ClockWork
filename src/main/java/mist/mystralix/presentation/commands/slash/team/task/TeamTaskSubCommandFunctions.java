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
        TeamTask teamTask = TEAM_TASK_SERVICE.create(
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

        return TEAM_TASK_EMBED.createMessageEmbed(
                user,
                "New Team Task",
                teamTask
        );
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