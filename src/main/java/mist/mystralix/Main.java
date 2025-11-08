package mist.mystralix;

import mist.mystralix.Listeners.CommandListener.Commands.CommandManager;
import mist.mystralix.Listeners.MessageListeners.MessageFilter;
import mist.mystralix.Listeners.MessageListeners.MessageLogger;
import mist.mystralix.Manager.Initializer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.EnumSet;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        JDA jda = JDABuilder.createLight(
                System.getenv("DISCORD_TOKEN"),
                        EnumSet.of(
                                GatewayIntent.GUILD_MESSAGES,
                                GatewayIntent.DIRECT_MESSAGES,
                                GatewayIntent.MESSAGE_CONTENT,
                                GatewayIntent.GUILD_MEMBERS
                        )
                )
                .build();

        jda.awaitReady();
        System.out.println("Bot is ready!");


        Initializer initializer = new Initializer();
        initializer.initializeEssentialFolders();
        initializer.initializeEssentialFiles();

        CommandManager manager = new CommandManager();
        jda.addEventListener(manager);
        jda.addEventListener(new MessageLogger());
        jda.addEventListener(new MessageFilter());

        Guild guild = jda.getGuildById("1145616832590516246");
        if (guild != null) guild.updateCommands().addCommands(manager.getCommandData()).queue();

    }
}