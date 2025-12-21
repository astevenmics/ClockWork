package mist.mystralix.presentation.commands.slash.reminder;

import mist.mystralix.application.reminder.ReminderScheduler;
import mist.mystralix.application.reminder.ReminderService;
import mist.mystralix.application.validator.InputValidation;
import mist.mystralix.domain.reminder.Reminder;
import mist.mystralix.presentation.commands.slash.ISlashCommandCRUD;
import mist.mystralix.presentation.embeds.ReminderEmbed;
import mist.mystralix.utils.Constants;
import mist.mystralix.utils.TimeHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class ReminderSubCommandFunctions implements ISlashCommandCRUD {

    private final ReminderService REMINDER_SERVICE;

    private final ReminderEmbed REMINDER_EMBED = new ReminderEmbed();

    public ReminderSubCommandFunctions(ReminderService reminderService) {
        this.REMINDER_SERVICE = reminderService;
    }

    @Override
    public MessageEmbed create(SlashCommandInteraction event) {

        User user = event.getUser();
        OptionMapping message = event.getOption("message");
        OptionMapping targetTime = event.getOption("time");

        if (message == null || targetTime == null) {
            return REMINDER_EMBED.createMissingParametersEmbed(
                    user,
                    Constants.MISSING_PARAMETERS.getValue(String.class)
            );
        }

        String reminderMessage = message.getAsString();
        String reminderTargetTimestampAsString = targetTime.getAsString();

        long reminderAsLong = TimeHandler.parseDuration(reminderTargetTimestampAsString);
        if (reminderAsLong <= 0) {
            return REMINDER_EMBED.createErrorEmbed(
                    user,
                    Constants.REMINDER_INVALID_TIME_INPUT.getValue(String.class)
            );
        } else if (reminderAsLong < 60_000L) {
            return REMINDER_EMBED.createErrorEmbed(
                    user,
                    Constants.REMINDER_MINIMUM_TIME_INPUT.getValue(String.class)
            );
        }

        long targetTimestamp = System.currentTimeMillis() + reminderAsLong;

        String userDiscordID = user.getId();
        String reminderUUID = UUID.randomUUID().toString();
        boolean isNotificationSent = false;

        REMINDER_SERVICE.createReminder(
                reminderUUID,
                userDiscordID,
                reminderMessage,
                targetTimestamp,
                isNotificationSent
        );

        Reminder newlyCreatedReminder = REMINDER_SERVICE.getUserReminder(
                userDiscordID,
                reminderUUID
        );

        new ReminderScheduler(REMINDER_SERVICE)
                .scheduleReminder(user, newlyCreatedReminder);

        return REMINDER_EMBED.createMessageEmbed(
                user,
                "New Reminder",
                newlyCreatedReminder
        );
    }

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

    @Override
    public MessageEmbed update(SlashCommandInteraction event) {
        return InputValidation.validate(
                event, 
                REMINDER_SERVICE, 
                REMINDER_EMBED, 
                "reminder_id", 
                reminder -> {
                    User user = event.getUser();
        
                    Optional.ofNullable(
                            event.getOption("message", () -> null, OptionMapping::getAsString)
                    ).ifPresent(reminder::setMessage);
        
                    String timeString = event.getOption("time", () -> null, OptionMapping::getAsString);
        
                    if (timeString != null) {
                        long duration = TimeHandler.parseDuration(timeString);
        
                        if (duration <= 0) {
                            return REMINDER_EMBED.createErrorEmbed(
                                    user,
                                    Constants.REMINDER_INVALID_TIME_INPUT.getValue(String.class)
                            );
                        }
        
                        reminder.setTargetTimestamp(System.currentTimeMillis() + duration);
                    }
        
                    REMINDER_SERVICE.updateUserReminder(reminder);
        
                    return REMINDER_EMBED.createMessageEmbed(user, "Updated", reminder);
                }
        );
    }

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