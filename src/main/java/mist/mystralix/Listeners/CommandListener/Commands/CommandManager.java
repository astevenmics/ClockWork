package mist.mystralix.Listeners.CommandListener.Commands;

import mist.mystralix.ClockWorkContainer;
import mist.mystralix.Listeners.CommandListener.SlashCommands.SlashCommand;
import mist.mystralix.Listeners.CommandListener.SlashCommands.ReminderCommand;
import mist.mystralix.Listeners.CommandListener.SlashCommands.TaskCommand;
import mist.mystralix.Objects.Reminder.ReminderService;
import mist.mystralix.Objects.Task.TaskService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.HashMap;
import java.util.List;

/**
 * Central command registry and dispatcher for all slash commands.
 *
 * <p>The {@code CommandManager} is responsible for:</p>
 * <ul>
 *     <li>registering all {@link SlashCommand} instances</li>
 *     <li>mapping command names to their handlers</li>
 *     <li>executing the correct command when a slash command interaction occurs</li>
 *     <li>providing command metadata for Discord registration</li>
 * </ul>
 *
 * <p>Each command is injected with its required services via the {@link ClockWorkContainer}
 * dependency container.</p>
 */
public class CommandManager extends ListenerAdapter {

    /** Holds all registered slash commands mapped by their root command name. */
    private final HashMap<String, SlashCommand> commands = new HashMap<>();

    /**
     * Initializes the command manager and registers all available slash commands.
     *
     * <p>Command dependencies are retrieved from the central {@link ClockWorkContainer}
     * to ensure proper dependency injection.</p>
     *
     * @param clockWorkContainer DI container that provides TaskService and ReminderService
     */
    public CommandManager(ClockWorkContainer clockWorkContainer) {
        TaskService taskService = clockWorkContainer.getTaskService();
        ReminderService reminderService = clockWorkContainer.getReminderService();

        registerCommand(new TaskCommand(taskService));
        registerCommand(new ReminderCommand(reminderService));
    }

    /**
     * Registers a new slash command by adding it to the internal command map.
     *
     * @param command the command implementation to register
     */
    private void registerCommand(SlashCommand command) {
        commands.put(command.getName(), command);
    }

    /**
     * Event listener handler invoked when a slash command is executed.
     *
     * <p>This method looks up the corresponding {@link SlashCommand} by name,
     * validates its existence, and delegates execution to the command's
     * {@link SlashCommand#execute(SlashCommandInteraction)} method.</p>
     *
     * @param event the slash command event emitted by JDA
     */
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

    /**
     * Builds a list of {@link SlashCommandData} required for Discord slash command registration.
     *
     * <p>Discord needs these objects during startup when calling
     * {@code jda.updateCommands()} or {@code guild.updateCommands()}.</p>
     *
     * <p>This method constructs:</p>
     * <ul>
     *     <li>the command name</li>
     *     <li>the command description</li>
     *     <li>all subcommands declared for that command</li>
     * </ul>
     *
     * @return a list of command metadata objects to register with Discord
     */
    public List<SlashCommandData> getCommandData() {
        return commands.values().stream()
                .map(command ->
                        Commands
                                .slash(
                                        command.getName(),
                                        command.getDescription()
                                )
                                .addSubcommands(command.getSubcommands())
                )
                .toList();
    }
}
