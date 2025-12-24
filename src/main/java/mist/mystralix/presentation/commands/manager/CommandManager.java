package mist.mystralix.presentation.commands.manager;

import mist.mystralix.ClockWorkContainer;
import mist.mystralix.application.pagination.PaginationService;
import mist.mystralix.application.reminder.ReminderService;
import mist.mystralix.application.task.TaskService;
import mist.mystralix.application.team.TeamService;
import mist.mystralix.application.team.TeamTaskService;
import mist.mystralix.presentation.commands.slash.SlashCommand;
import mist.mystralix.presentation.commands.slash.general.help.HelpCommand;
import mist.mystralix.presentation.commands.slash.reminder.ReminderCommand;
import mist.mystralix.presentation.commands.slash.task.TaskCommand;
import mist.mystralix.presentation.commands.slash.team.TeamCommand;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.HashMap;
import java.util.List;

public class CommandManager {

    private final HashMap<String, SlashCommand> commands = new HashMap<>();

    public CommandManager(ClockWorkContainer clockWorkContainer) {
        TeamTaskService teamTaskService = clockWorkContainer.getTeamTaskService();
        TaskService taskService = clockWorkContainer.getTaskService();
        ReminderService reminderService = clockWorkContainer.getReminderService();
        TeamService teamService = clockWorkContainer.getTeamService();
        PaginationService paginationService = clockWorkContainer.getPaginationService();

        registerCommand(new ReminderCommand(reminderService, paginationService));
        registerCommand(new TaskCommand(taskService, paginationService));
        registerCommand(new TeamCommand(teamTaskService, teamService, paginationService));
        registerCommand(new HelpCommand(
                teamTaskService,
                taskService,
                reminderService,
                teamService,
                paginationService
        ));
    }

    private void registerCommand(SlashCommand command) {
        commands.put(command.getName(), command);
    }

    public HashMap<String, SlashCommand> getCommands() {
        return commands;
    }

    public List<SlashCommandData> getCommandData() {
        return commands.values().stream()
                .map(command -> {
                        SlashCommandData data = Commands
                                .slash(
                                        command.getName(),
                                        command.getDescription()
                                );

                        data.addSubcommands(command.getSubcommands());
                        data.addSubcommandGroups(command.getSubcommandGroupData());
                        return data;
                    })
                .toList();
    }
}