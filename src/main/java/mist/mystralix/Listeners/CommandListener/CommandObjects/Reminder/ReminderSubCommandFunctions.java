package mist.mystralix.Listeners.CommandListener.CommandObjects.Reminder;

import mist.mystralix.Listeners.CommandListener.ISlashCommandCRUD;
import mist.mystralix.Objects.Reminder.Reminder;
import mist.mystralix.Objects.Reminder.ReminderScheduler;
import mist.mystralix.Objects.Reminder.ReminderService;
import mist.mystralix.Objects.TimeHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class ReminderSubCommandFunctions implements ISlashCommandCRUD {

    private final ReminderService REMINDER_SERVICE;

    public ReminderSubCommandFunctions(ReminderService reminderService) {
        this.REMINDER_SERVICE = reminderService;
    }

    private final ReminderEmbed REMINDER_EMBED = new ReminderEmbed();

    @Override
    public MessageEmbed create(SlashCommandInteraction event) {
        
        User user = event.getUser();
        OptionMapping message = event.getOption("message");
        OptionMapping targetTime = event.getOption("time");

        if (message == null || targetTime == null) {
            return REMINDER_EMBED.createMissingParametersEmbed(
                    user,
                    "Neither message nor time were provided"
            );
        }

        String reminderMessage = message.getAsString();
        String reminderTargetTimestampAsString = targetTime.getAsString();

        long reminderAsLong = TimeHandler.parseDuration(reminderTargetTimestampAsString);
        if(reminderAsLong <= 0){
            return REMINDER_EMBED.createErrorEmbed(
                    user,
                    "Invalid reminder time provided.\nExample: 1d, 1d20h, 20h15m..."
            );
        } else if (reminderAsLong < 60_000L){
            return REMINDER_EMBED.createErrorEmbed(
                    user,
                    "Time target/duration needs to be, at least, over a minute"
            );
        }

        long currentTime = System.currentTimeMillis();
        long targetTimeStamp = currentTime + reminderAsLong;

        String userDiscordID = user.getId();
        String reminderUUID = UUID.randomUUID().toString();
        boolean isNotificationSent = false;

        REMINDER_SERVICE.createReminder(
                reminderUUID,
                userDiscordID,
                reminderMessage,
                targetTimeStamp,
                isNotificationSent
        );

        Reminder newlyCreatedReminder = REMINDER_SERVICE.getUserReminder(
                userDiscordID,
                reminderUUID
        );

        ReminderScheduler reminderScheduler = new ReminderScheduler(REMINDER_SERVICE);
        reminderScheduler.scheduleReminder(user, newlyCreatedReminder);
        return REMINDER_EMBED.createMessageEmbed(
                user,
                "New Reminder",
                newlyCreatedReminder
        );
    }

    @Override
    public MessageEmbed delete(SlashCommandInteraction event) {
        
        User user = event.getUser();
        String userDiscordID = user.getId();
        OptionMapping reminder_id = event.getOption("reminder_id");

        if (reminder_id == null) {
            return REMINDER_EMBED.createMissingParametersEmbed(
                    user,
                    "No ID was provided"
            );
        }

        int reminderID = Integer.parseInt(reminder_id.getAsString());

        Reminder reminderToDelete = REMINDER_SERVICE.getUserReminder(
                userDiscordID,
                reminderID
        );
        if(reminderToDelete == null){
            return REMINDER_EMBED.createErrorEmbed(
                    user,
                    "Invalid reminder ID/Reminder not existing"
            );
        }

        REMINDER_SERVICE.delete(reminderToDelete);

        System.out.println("Deleted reminder with ID: " + reminderID);

        return REMINDER_EMBED.createMessageEmbed(
                user,
                "Deleted",
                reminderToDelete
        );
    }

    @Override
    public MessageEmbed update(SlashCommandInteraction event) {
        
        User user = event.getUser();
        String userDiscordID = user.getId();

        int reminderID = event.getOption(
                "reminder_id",
                () -> 0,
                OptionMapping::getAsInt
        );
        String reminderMessage = event.getOption(
                "message",
                () -> null,
                OptionMapping::getAsString
        );
        String reminderTargetTimestampAsString = event.getOption(
                "time",
                () -> null,
                OptionMapping::getAsString
        );

        if (reminderID == 0) {
            return REMINDER_EMBED.createMissingParametersEmbed(
                    user,
                    "Neither id, message, nor time were provided"
            );
        }

        Reminder reminderToUpdate = REMINDER_SERVICE.getUserReminder(userDiscordID, reminderID);

        if(reminderToUpdate == null){
            return REMINDER_EMBED.createErrorEmbed(
                    user,
                    "Invalid reminder ID/Reminder not existing"
            );
        }

        Optional.ofNullable(reminderMessage).ifPresent(m -> reminderToUpdate.message = m);

        if(reminderTargetTimestampAsString != null){
            long reminderAsLong = TimeHandler.parseDuration(reminderTargetTimestampAsString);
            if(reminderAsLong == -1) {
                return REMINDER_EMBED.createErrorEmbed(
                        user,
                        "Invalid reminder time provided.\nExample: 1d, 1d20h, 20h15m..."
                );
            }

            long currentTime = System.currentTimeMillis();
            reminderToUpdate.targetTimestamp = currentTime + reminderAsLong;
        }

        REMINDER_SERVICE.updateUserReminder(reminderToUpdate);

        return REMINDER_EMBED.createMessageEmbed(
                user,
                "Updated",
                reminderToUpdate
        );
    }

    @Override
    public MessageEmbed read(SlashCommandInteraction event) {
        
        User user = event.getUser();
        String userDiscordID = user.getId();

        OptionMapping id = event.getOption("reminder_id");

        if (id == null) {
            return REMINDER_EMBED.createMissingParametersEmbed(
                    user,
                    "No ID was provided"
            );
        }

        int reminderID = id.getAsInt();

        Reminder reminderToView = REMINDER_SERVICE.getUserReminder(userDiscordID, reminderID);
        if(reminderToView == null){
            return REMINDER_EMBED.createErrorEmbed(
                    user,
                    "Invalid reminder ID/Reminder not existing"
            );
        }
        // put into embed
        return REMINDER_EMBED.createMessageEmbed(
                user,
                "View",
                reminderToView
                );
    }

    @Override
    public MessageEmbed readAll(SlashCommandInteraction event) {
        
        User user = event.getUser();
        String userDiscordID = user.getId();

        ArrayList<Reminder> userReminders = (ArrayList<Reminder>) REMINDER_SERVICE.getAllUserReminders(userDiscordID);
        return REMINDER_EMBED.createListEmbed(
                user,
                userReminders
        );
    }

}