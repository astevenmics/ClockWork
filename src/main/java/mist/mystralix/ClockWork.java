package mist.mystralix;

import mist.mystralix.application.reminder.ReminderScheduler;
import mist.mystralix.config.DBManager;
import mist.mystralix.config.DBSchemaInitializer;
import mist.mystralix.presentation.commands.manager.CommandManager;
import mist.mystralix.presentation.commands.manager.ListenerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.EnumSet;

public final class ClockWork {

    public static void main(String[] args) throws InterruptedException {

        final String token = System.getenv("DISCORD_TOKEN");
        if (token == null) {
            throw new IllegalStateException("DISCORD_TOKEN environment variable is missing.");
        }

        DBManager.buildConnectionPool();
        DBSchemaInitializer schema = new DBSchemaInitializer();
        schema.initializeDatabaseTable();

        ClockWorkContainer container = new ClockWorkContainer();

        JDA jda = JDABuilder.createLight(
                token,
                EnumSet.of(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS
                )
        )
                .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        jda.awaitReady();
        System.out.println("ClockWork bot is online!");

        CommandManager commandManager = new CommandManager(container);
        jda.updateCommands().addCommands(commandManager.getCommandData()).queue();
        ReminderScheduler reminderScheduler = ReminderScheduler.getInstance(container.getReminderService());
        reminderScheduler.scheduleReminders(jda);
        System.out.println("Reminder scheduler activated.");

        new ListenerManager(jda, commandManager.getCommands());

    }
}