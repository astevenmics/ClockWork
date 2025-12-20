package mist.mystralix.presentation.commands.manager;

import mist.mystralix.ClockWorkContainer;
import mist.mystralix.application.reminder.ReminderService;
import mist.mystralix.application.task.TaskService;
import mist.mystralix.application.team.TeamService;
import mist.mystralix.application.team.TeamTaskService;
import mist.mystralix.presentation.commands.slash.SlashCommand;
import mist.mystralix.presentation.commands.slash.reminder.ReminderCommand;
import mist.mystralix.presentation.commands.slash.task.TaskCommand;
import mist.mystralix.presentation.commands.slash.team.TeamCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.HashMap;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    private final HashMap<String, SlashCommand> commands = new HashMap<>();

    public CommandManager(ClockWorkContainer clockWorkContainer) {
        TeamTaskService teamTaskService = clockWorkContainer.getTeamTaskService();
        TaskService taskService = clockWorkContainer.getTaskService();
        ReminderService reminderService = clockWorkContainer.getReminderService();
        TeamService teamService = clockWorkContainer.getTeamService();

        registerCommand(new TaskCommand(taskService));
        registerCommand(new ReminderCommand(reminderService));
        registerCommand(new TeamCommand(
                teamTaskService,
                teamService,
                taskService
        ));
    }

    private void registerCommand(SlashCommand command) {
        commands.put(command.getName(), command);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        SlashCommand command = commands.get(commandName);

        if (command == null) {
            event.reply("Unknown command!").setEphemeral(true).queue();
            return;
        }

        command.execute(event);
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