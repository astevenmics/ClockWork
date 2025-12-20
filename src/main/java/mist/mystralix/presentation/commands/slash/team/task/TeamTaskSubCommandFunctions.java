package mist.mystralix.presentation.commands.slash.team.task;

import mist.mystralix.application.task.TaskService;
import mist.mystralix.application.team.TeamService;
import mist.mystralix.application.team.TeamTaskService;
import mist.mystralix.presentation.commands.slash.ISlashCommandCRUD;
import mist.mystralix.presentation.embeds.TeamTaskEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class TeamTaskSubCommandFunctions implements ISlashCommandCRUD {

    private final TeamTaskService TEAM_TASK_SERVICE;
    private final TeamService TEAM_SERVICE;
    private final TaskService TASK_SERVICE;
    private final TeamTaskEmbed TEAM_TASK_EMBED = new TeamTaskEmbed();

    public TeamTaskSubCommandFunctions(
            TeamTaskService teamTaskService,
            TeamService teamService,
            TaskService taskService
    ) {
        this.TEAM_TASK_SERVICE = teamTaskService;
        this.TEAM_SERVICE = teamService;
        this.TASK_SERVICE = taskService;
    }


    @Override
    public MessageEmbed create(SlashCommandInteraction event) {
        return null;
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