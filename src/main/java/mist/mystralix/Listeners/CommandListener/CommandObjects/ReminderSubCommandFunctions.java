package mist.mystralix.Listeners.CommandListener.CommandObjects;

import mist.mystralix.Listeners.CommandListener.ISlashCommandCRUD;
import mist.mystralix.Objects.Reminder.Reminder;
import mist.mystralix.Objects.Reminder.ReminderHandler;
import mist.mystralix.Objects.TimeHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.UUID;

public class ReminderSubCommandFunctions implements ISlashCommandCRUD {

    @Override
    public MessageEmbed create(SlashCommandInteraction event) {
        User user = event.getUser();
        OptionMapping message = event.getOption("message");
        OptionMapping targetTime = event.getOption("time");

        if (message == null || targetTime == null) {
            return null;
//            return TaskEmbed.createTaskErrorEmbed(taskUser, "Neither title nor description were provided");
        }

        String reminderMessage = message.getAsString();
        String reminderTargetTimestampAsString = targetTime.getAsString();

        long reminderAsLong = TimeHandler.parseDuration(reminderTargetTimestampAsString);
        if(reminderAsLong == -1){
            System.out.println("Invalid reminder time");
            return null;
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
                newlyCreatedReminder,
                currentTime
        );
    }

    @Override
    public MessageEmbed delete(SlashCommandInteraction event) {
        ReminderHandler reminderHandler = new ReminderHandler();
        return null;
    }

    @Override
    public MessageEmbed update(SlashCommandInteraction event) {
        ReminderHandler reminderHandler = new ReminderHandler();
        return null;
    }

    @Override
    public MessageEmbed read(SlashCommandInteraction event) {
        ReminderHandler reminderHandler = new ReminderHandler();
        return null;
    }

    public MessageEmbed readAll(SlashCommandInteraction event) {
        ReminderHandler reminderHandler = new ReminderHandler();
        return null;
    }

}