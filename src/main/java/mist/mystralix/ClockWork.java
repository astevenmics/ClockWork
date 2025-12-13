package mist.mystralix;

import mist.mystralix.config.DBManager;
import mist.mystralix.config.DBSchemaInitializer;
import mist.mystralix.presentation.commands.manager.CommandManager;
import mist.mystralix.presentation.listeners.MessageFilter;
import mist.mystralix.presentation.listeners.MessageLogger;
import mist.mystralix.application.reminder.ReminderScheduler;
import mist.mystralix.application.reminder.ReminderService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.EnumSet;

public final class ClockWork {

    public static void main(String[] args) throws InterruptedException {

        final String token = System.getenv("DISCORD_TOKEN");
        final String guildId = System.getenv("GUILD_ID");

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
        ).build();

        jda.awaitReady();
        System.out.println("ClockWork bot is online!");

        CommandManager commandManager = new CommandManager(container);

        jda.addEventListener(commandManager);
        jda.addEventListener(new MessageLogger());
        jda.addEventListener(new MessageFilter());

        if (guildId != null) {
            Guild guild = jda.getGuildById(guildId);
            if (guild != null) {
                guild.updateCommands()
                        .addCommands(commandManager.getCommandData())
                        .queue();
            }
        }

        ReminderService reminderService = container.getReminderService();
        ReminderScheduler reminderScheduler = new ReminderScheduler(reminderService);

        reminderScheduler.scheduleReminders(jda);

        System.out.println("Reminder scheduler activated.");
    }
}