package mist.mystralix.presentation.listeners;

import mist.mystralix.presentation.commands.slash.SlashCommand;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class StringSelectInteractionHandler extends ListenerAdapter {

    private final HashMap<String, SlashCommand> commands;

    public StringSelectInteractionHandler(HashMap<String, SlashCommand> commands) {
        this.commands = commands;
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String commandName = event.getComponentId().split(":")[0];
        SlashCommand command = commands.get(commandName);

        if (command == null) {
            event.reply("Unknown command!").setEphemeral(true).queue();
            return;
        }

        command.stringSelectInteraction(event);
    }

}
