package mist.mystralix.Listeners.CommandListener.CommandObjects;

import mist.mystralix.Listeners.CommandListener.ISlashCommandCRUD;
import mist.mystralix.Objects.Reminder.Reminder;
import mist.mystralix.Objects.Reminder.ReminderHandler;
import mist.mystralix.Objects.TimeHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.ArrayList;
import java.util.UUID;

public class ReminderSubCommandFunctions implements ISlashCommandCRUD {

    @Override
    public MessageEmbed create(SlashCommandInteraction event) {
        User user = event.getUser();
        OptionMapping message = event.getOption("message");
        OptionMapping targetTime = event.getOption("time");

        if (message == null || targetTime == null) {
            return ReminderEmbed.createReminderErrorEmbed(user, "Neither message nor time were provided");
        }

        String reminderMessage = message.getAsString();
        String reminderTargetTimestampAsString = targetTime.getAsString();

        long reminderAsLong = TimeHandler.parseDuration(reminderTargetTimestampAsString);
        if(reminderAsLong <= 0){
            return ReminderEmbed.createReminderErrorEmbed(user, "Invalid reminder time provided.\nExample: 1d, 1d20h, 20h15m...");
        } else if (reminderAsLong < 60_000L){
            return ReminderEmbed.createReminderErrorEmbed(user, "Time target/duration needs to be, at least, over a minute");
        }

        long currentTime = System.currentTimeMillis();
        long targetTimeStamp = currentTime + reminderAsLong;

        String userDiscordID = user.getId();
        String reminderUUID = UUID.randomUUID().toString();


        ReminderHandler reminderHandler = new ReminderHandler();
        reminderHandler.createReminder(
                reminderUUID,
                userDiscordID,
                reminderMessage,
                targetTimeStamp
        );

        Reminder newlyCreatedReminder = reminderHandler.getUserReminder(userDiscordID, reminderUUID);
        return ReminderEmbed.createReminderEmbed(
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
            return ReminderEmbed.createReminderErrorEmbed(user, "No ID was provided");
        }

        int reminderID = Integer.parseInt(reminder_id.getAsString());

        ReminderHandler reminderHandler = new ReminderHandler();

        Reminder reminderToDelete = reminderHandler.getUserReminder(userDiscordID, reminderID);
        if(reminderToDelete == null){
            return ReminderEmbed.createReminderErrorEmbed(user, "Invalid reminder ID/Reminder not existing");
        }

        reminderHandler.delete(reminderToDelete);

        System.out.println("Deleted reminder with ID: " + reminderID);

        return ReminderEmbed.createReminderEmbed(
                user,
                "Deleted",
                reminderToDelete
        );
    }

    @Override
    public MessageEmbed update(SlashCommandInteraction event) {
        User user = event.getUser();
        String userDiscordID = user.getId();

        OptionMapping id = event.getOption("reminder_id");
        OptionMapping message = event.getOption("message");
        OptionMapping targetTime = event.getOption("time");

        if (id == null || message == null || targetTime == null) {
            return ReminderEmbed.createReminderErrorEmbed(user, "Neither id, message, nor time were provided");
        }

        int reminderID = id.getAsInt();
        String reminderMessage = message.getAsString();
        String reminderTargetTimestampAsString = targetTime.getAsString();
        long reminderAsLong = TimeHandler.parseDuration(reminderTargetTimestampAsString);
        if(reminderAsLong == -1){
            return ReminderEmbed.createReminderErrorEmbed(user, "Invalid reminder time provided.\nExample: 1d, 1d20h, 20h15m...");
        }

        long currentTime = System.currentTimeMillis();
        long targetTimeStamp = currentTime + reminderAsLong;

        ReminderHandler reminderHandler = new ReminderHandler();
        Reminder reminderToUpdate = reminderHandler.getUserReminder(userDiscordID, reminderID);
        if(reminderToUpdate == null){
            return ReminderEmbed.createReminderErrorEmbed(user, "Invalid reminder ID/Reminder not existing");
        }
        reminderToUpdate.message = reminderMessage;
        reminderToUpdate.targetTimestamp = targetTimeStamp;

        reminderHandler.updateUserReminder(reminderToUpdate);

        return ReminderEmbed.createReminderEmbed(
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
            return ReminderEmbed.createReminderErrorEmbed(user, "No ID was provided");
        }

        int reminderID = id.getAsInt();
        ReminderHandler reminderHandler = new ReminderHandler();

        Reminder reminderToView = reminderHandler.getUserReminder(userDiscordID, reminderID);
        if(reminderToView == null){
            return ReminderEmbed.createReminderErrorEmbed(user, "Invalid reminder ID/Reminder not existing");
        }
        // put into embed
        return ReminderEmbed.createReminderEmbed(
                user,
                "View",
                reminderToView
                );
    }

    public MessageEmbed readAll(SlashCommandInteraction event) {
        User user = event.getUser();
        String userDiscordID = user.getId();

        ReminderHandler reminderHandler = new ReminderHandler();

        ArrayList<Reminder> userReminders = reminderHandler.getAllUserReminders(userDiscordID);
        return ReminderEmbed.createReminderListEmbed(
                user,
                userReminders
        );
    }

}