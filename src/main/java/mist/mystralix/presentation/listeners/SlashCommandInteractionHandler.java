package mist.mystralix.presentation.listeners;

import mist.mystralix.presentation.commands.slash.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class SlashCommandInteractionHandler extends ListenerAdapter {

    private final HashMap<String, SlashCommand> commands;

    public SlashCommandInteractionHandler(HashMap<String, SlashCommand> commands) {
        this.commands = commands;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild()) {
            event.reply("This command can only be used in servers.")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        String commandName = event.getName();
        SlashCommand command = commands.get(commandName);

        if (command == null) {
            event.reply("Unknown command!").setEphemeral(true).queue();
            return;
        }

        command.execute(event);
    }
}