package mist.mystralix.presentation.commands.manager;

import mist.mystralix.presentation.commands.slash.SlashCommand;
import mist.mystralix.presentation.listeners.MessageReceivedHandler;
import mist.mystralix.presentation.listeners.ShutdownHandler;
import mist.mystralix.presentation.listeners.SlashCommandInteractionHandler;
import mist.mystralix.presentation.listeners.StringSelectInteractionHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class ListenerManager extends ListenerAdapter {

    public ListenerManager(JDA jda, HashMap<String, SlashCommand> commands) {
        jda.addEventListener(new MessageReceivedHandler());
        jda.addEventListener(new ShutdownHandler());
        jda.addEventListener(new SlashCommandInteractionHandler(commands));
        jda.addEventListener(new StringSelectInteractionHandler(commands));
    }
}