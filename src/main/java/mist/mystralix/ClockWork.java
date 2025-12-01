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

/**
 * Entry point for the ClockWork Discord bot application.
 *
 * <p>This class performs all necessary bootstrapping steps such as:
 * <ul>
 *     <li>Initializing the database and connection pool</li>
 *     <li>Starting the JDA Discord client</li>
 *     <li>Registering all event listeners and slash commands</li>
 *     <li>Building the dependency injection container</li>
 *     <li>Initializing database schema</li>
 *     <li>Starting the reminder scheduling system</li>
 * </ul>
 *
 * The application intentionally avoids external frameworks to preserve full
 * control and transparency over the boot sequence.
 * </p>
 */
public final class ClockWork {

    public static void main(String[] args) throws InterruptedException {

        // ============================
        // 1. Load and validate env vars
        // ============================
        final String token = System.getenv("DISCORD_TOKEN");
        final String guildId = System.getenv("GUILD_ID");

        if (token == null) {
            throw new IllegalStateException("DISCORD_TOKEN environment variable is missing.");
        }

        // ============================
        // 2. Initialize database layer
        // ============================
        DBManager.buildConnectionPool();

        DBSchemaInitializer schema = new DBSchemaInitializer();
        schema.initializeDatabaseTable();

        // ============================
        // 3. Build dependency container
        // ============================
        ClockWorkContainer container = new ClockWorkContainer();

        // ============================
        // 4. Build JDA client
        // ============================
        JDA jda = JDABuilder.createLight(
                token,
                EnumSet.of(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS
                )
        ).build();

        // Block until connected to Discord WebSocket
        jda.awaitReady();
        System.out.println("ClockWork bot is online!");

        // ============================
        // 5. Register event listeners
        // ============================
        CommandManager commandManager = new CommandManager(container);

        jda.addEventListener(commandManager);  // Slash command handler
        jda.addEventListener(new MessageLogger()); // Logs messages
        jda.addEventListener(new MessageFilter()); // Filters prohibited content

        // ============================
        // 6. Register slash commands to guild
        // ============================
        if (guildId != null) {
            Guild guild = jda.getGuildById(guildId);
            if (guild != null) {
                guild.updateCommands()
                        .addCommands(commandManager.getCommandData()) // Register commands from manager
                        .queue();
            }
        }

        // ============================
        // 7. Start reminder scheduler
        // ============================
        ReminderService reminderService = container.getReminderService();
        ReminderScheduler reminderScheduler = new ReminderScheduler(reminderService);

        // Start scheduling existing reminders and listening for triggers
        reminderScheduler.scheduleReminders(jda);

        System.out.println("Reminder scheduler activated.");
    }
}
