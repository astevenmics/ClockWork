package mist.mystralix.Listeners.CommandListener.Commands;

import mist.mystralix.Listeners.CommandListener.SlashCommand;
import mist.mystralix.Listeners.CommandListener.SlashCommands.TaskCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.HashMap;
import java.util.List;

public class CommandManager extends ListenerAdapter {
    private final HashMap<String, SlashCommand> commands = new HashMap<>();

    public CommandManager() {
        registerCommand(new TaskCommand());
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