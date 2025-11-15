package mist.mystralix;

import mist.mystralix.Database.DBHandler;
import mist.mystralix.Listeners.CommandListener.Commands.CommandManager;
import mist.mystralix.Listeners.MessageListeners.MessageFilter;
import mist.mystralix.Listeners.MessageListeners.MessageLogger;
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

        CommandManager manager = new CommandManager();
        jda.addEventListener(manager);
        jda.addEventListener(new MessageLogger());
        jda.addEventListener(new MessageFilter());

        Guild guild = jda.getGuildById(System.getenv("GUILD_ID"));
        if (guild != null) guild.updateCommands().addCommands(manager.getCommandData()).queue();

        DBHandler dbHandler = new DBHandler();
        dbHandler.initializeDatabaseTable();
    }
}