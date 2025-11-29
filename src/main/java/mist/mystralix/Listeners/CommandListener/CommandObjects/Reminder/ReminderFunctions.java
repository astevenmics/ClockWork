package mist.mystralix.Listeners.CommandListener.CommandObjects.Reminder;

import mist.mystralix.Objects.Reminder.Reminder;
import mist.mystralix.Objects.Reminder.ReminderService;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.function.Function;

/**
 * Utility class containing shared logic for reminder-related CRUD operations.
 *
 * <p>This class provides centralized validation and retrieval logic used by
 * multiple subcommands within {@link ReminderSubCommandFunctions}. It ensures
 * consistent behavior for:</p>
 * <ul>
 *     <li>Missing parameters</li>
 *     <li>ID extraction</li>
 *     <li>Database lookup</li>
 *     <li>Error handling</li>
 * </ul>
 *
 * <p>By delegating these common concerns to this utility, command handlers remain concise
 * and focus solely on their specific business logic (create, update, delete, etc.).</p>
 */
public class ReminderFunctions {

    /**
     * Shared CRUD validation pipeline for reminder-related subcommands.
     *
     * <p>This method performs the following steps:</p>
     * <ol>
     *     <li>Extracts the calling user</li>
     *     <li>Validates existence of the {@code reminder_id} option</li>
     *     <li>Retrieves the reminder via {@link ReminderService}</li>
     *     <li>Returns error embeds for missing or invalid reminders</li>
     *     <li>Supplies the retrieved reminder to the provided handler action</li>
     * </ol>
     *
     * <p>This prevents duplicate logic in delete, update, read, and view operations.</p>
     *
     * @param event the slash command interaction
     * @param reminderService the service layer used for database queries
     * @param reminderEmbed embed builder for formatted error or success messages
     * @param action functional callback applied once the reminder is successfully retrieved
     * @return a populated {@link MessageEmbed} for Discord output
     */
    public static MessageEmbed handleReminder(
            SlashCommandInteraction event,
            ReminderService reminderService,
            ReminderEmbed reminderEmbed,
            Function<Reminder, MessageEmbed> action
    ) {
        User user = event.getUser();
        String userDiscordID = user.getId();

        // Validate required ID option
        OptionMapping idOption = event.getOption("reminder_id");
        if (idOption == null) {
            return reminderEmbed.createMissingParametersEmbed(
                    user, "No reminder ID was provided."
            );
        }

        int reminderID = idOption.getAsInt();

        // Fetch reminder from DB
        Reminder reminder = reminderService.getUserReminder(userDiscordID, reminderID);
        if (reminder == null) {
            return reminderEmbed.createErrorEmbed(
                    user, "Reminder not found or invalid ID."
            );
        }

        // Execute provided handler using validated reminder
        return action.apply(reminder);
    }
}
