package mist.mystralix.presentation.commands.slash.reminder;

import mist.mystralix.presentation.embeds.IMessageEmbedBuilder;
import mist.mystralix.application.validation.InputValidation;
import mist.mystralix.presentation.commands.slash.ISlashCommandCRUD;
import mist.mystralix.utils.IdentifiableFetcher;
import mist.mystralix.domain.reminder.Reminder;
import mist.mystralix.application.reminder.ReminderScheduler;
import mist.mystralix.application.reminder.ReminderService;
import mist.mystralix.utils.TimeHandler;
import mist.mystralix.presentation.embeds.ReminderEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Implements all business logic for the reminder-related subcommands.
 *
 * <p>This class processes the functional behavior for:</p>
 * <ul>
 *     <li>/reminder create</li>
 *     <li>/reminder delete</li>
 *     <li>/reminder update</li>
 *     <li>/reminder view</li>
 *     <li>/reminder list</li>
 * </ul>
 *
 * <p>Each subcommand interacts with {@link ReminderService} for persistence and
 * reminder state management. Output formatting is delegated to {@link ReminderEmbed}.
 * Shared validation and lookup steps are centralized in {@link InputValidation#validate(SlashCommandInteraction, IdentifiableFetcher, IMessageEmbedBuilder, String, Function)}.</p>
 */
public class ReminderSubCommandFunctions implements ISlashCommandCRUD {

    /** Service layer used for database operations and reminder dispatching. */
    private final ReminderService REMINDER_SERVICE;

    /** Shared embed builder utility used for consistent visual responses. */
    private final ReminderEmbed REMINDER_EMBED = new ReminderEmbed();

    /**
     * Constructs a handler for all reminder subcommands.
     *
     * @param reminderService the dependency providing CRUD and scheduling operations
     */
    public ReminderSubCommandFunctions(ReminderService reminderService) {
        this.REMINDER_SERVICE = reminderService;
    }

    /**
     * Handles /reminder create.
     *
     * <p>This method performs:</p>
     * <ul>
     *     <li>Input validation for message and time</li>
     *     <li>Duration parsing using {@link TimeHandler#parseDuration(String)}</li>
     *     <li>Reminder creation and persistence</li>
     *     <li>Scheduling the reminder using {@link ReminderScheduler}</li>
     *     <li>Returning a formatted embed summarizing the created reminder</li>
     * </ul>
     *
     * <p>If the time format is invalid or duration is too short, an error embed is returned.</p>
     */
    @Override
    public MessageEmbed create(SlashCommandInteraction event) {

        User user = event.getUser();
        OptionMapping message = event.getOption("message");
        OptionMapping targetTime = event.getOption("time");

        // Required field validation
        if (message == null || targetTime == null) {
            return REMINDER_EMBED.createMissingParametersEmbed(
                    user, "Neither message nor time were provided"
            );
        }

        String reminderMessage = message.getAsString();
        String reminderTargetTimestampAsString = targetTime.getAsString();

        // Parse duration string into milliseconds
        long reminderAsLong = TimeHandler.parseDuration(reminderTargetTimestampAsString);
        if (reminderAsLong <= 0) {
            return REMINDER_EMBED.createErrorEmbed(
                    user,
                    "Invalid reminder time provided.\nExample: 1d, 1d20h, 20h15m..."
            );
        } else if (reminderAsLong < 60_000L) {
            return REMINDER_EMBED.createErrorEmbed(
                    user,
                    "Time target/duration must be at least 1 minute."
            );
        }

        // Compute final timestamp
        long targetTimestamp = System.currentTimeMillis() + reminderAsLong;

        // Create reminder metadata
        String userDiscordID = user.getId();
        String reminderUUID = UUID.randomUUID().toString();
        boolean isNotificationSent = false;

        // Persist reminder
        REMINDER_SERVICE.createReminder(
                reminderUUID,
                userDiscordID,
                reminderMessage,
                targetTimestamp,
                isNotificationSent
        );

        // Retrieve stored domain object
        Reminder newlyCreatedReminder = REMINDER_SERVICE.getUserReminder(
                userDiscordID,
                reminderUUID
        );

        // Schedule reminder execution
        new ReminderScheduler(REMINDER_SERVICE)
                .scheduleReminder(user, newlyCreatedReminder);

        return REMINDER_EMBED.createMessageEmbed(
                user,
                "New Reminder",
                newlyCreatedReminder
        );
    }

    /**
     * Handles /reminder delete.
     *
     * <p>The shared CRUD logic is delegated to
     * {@link InputValidation#validate(SlashCommandInteraction, IdentifiableFetcher, IMessageEmbedBuilder, String, Function)}.</p>
     *
     * <p>If the reminder exists, it is removed from persistent storage and an embed confirming the deletion is returned.</p>
     */
    @Override
    public MessageEmbed delete(SlashCommandInteraction event) {
        return InputValidation.validate(
                event, 
                REMINDER_SERVICE, 
                REMINDER_EMBED,
                "reminder_id",
                reminder -> {
                    REMINDER_SERVICE.delete(reminder);
                    return REMINDER_EMBED.createMessageEmbed(event.getUser(), "Deleted", reminder);
                }
        );
    }

    /**
     * Handles /reminder update.
     *
     * <p>The shared lookup and validation logic is handled by {@link InputValidation#validate(SlashCommandInteraction, IdentifiableFetcher, IMessageEmbedBuilder, String, Function)}.</p>
     *
     * <p>Supports updating:</p>
     * <ul>
     *     <li>Reminder message</li>
     *     <li>Reminder timestamp (via duration string)</li>
     * </ul>
     *
     * <p>Invalid duration inputs will return an error embed.</p>
     */
    @Override
    public MessageEmbed update(SlashCommandInteraction event) {
        return InputValidation.validate(
                event, 
                REMINDER_SERVICE, 
                REMINDER_EMBED, 
                "reminder_id", 
                reminder -> {
                    User user = event.getUser();
        
                    // Optional message update
                    Optional.ofNullable(
                            event.getOption("message", () -> null, OptionMapping::getAsString)
                    ).ifPresent(reminder::setMessage);
        
                    // Optional time update
                    String timeString = event.getOption("time", () -> null, OptionMapping::getAsString);
        
                    if (timeString != null) {
                        long duration = TimeHandler.parseDuration(timeString);
        
                        if (duration <= 0) {
                            return REMINDER_EMBED.createErrorEmbed(
                                    user, "Invalid time format. Example: 1d, 1h20m"
                            );
                        }
        
                        reminder.setTargetTimestamp(System.currentTimeMillis() + duration);
                    }
        
                    REMINDER_SERVICE.updateUserReminder(reminder);
        
                    return REMINDER_EMBED.createMessageEmbed(user, "Updated", reminder);
                }
        );
    }

    /**
     * Handles /reminder view.
     *
     * <p>Retrieves a single reminder and displays its details formatted as an embed.</p>
     */
    @Override
    public MessageEmbed read(SlashCommandInteraction event) {
        return InputValidation.validate(
                event, 
                REMINDER_SERVICE, 
                REMINDER_EMBED,
                "reminder_id",
                reminder ->
                    REMINDER_EMBED.createMessageEmbed(
                        event.getUser(),
                        "View",
                        reminder
                )
        );
    }

    /**
     * Handles /reminder list.
     *
     * <p>Retrieves all reminders associated with the requesting user and displays them in an aggregated list embed.</p>
     */
    @Override
    public MessageEmbed readAll(SlashCommandInteraction event) {

        User user = event.getUser();
        String userDiscordID = user.getId();

        ArrayList<Reminder> userReminders = REMINDER_SERVICE.getAllUserReminders(userDiscordID);

        if (userReminders.isEmpty()) {
            return REMINDER_EMBED.createErrorEmbed(
                    user,
                    "You currently do not have any reminders! Use the /remind command to start."
            );
        }

        return REMINDER_EMBED.createListEmbed(user, userReminders);
    }
}