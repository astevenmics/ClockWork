package mist.mystralix.Listeners.CommandListener.CommandObjects;

import mist.mystralix.Objects.Reminder.Reminder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;

public class ReminderEmbed implements IMessageEmbedBuilder {

    @Override
    public <T> MessageEmbed createMessageEmbed(User user, String title, T object) {
        if(!(object instanceof Reminder reminder)) { return null; }
        String reminderMessage = reminder.message;
        int reminderID = reminder.reminderID;

        long reminderTargetTimestamp = reminder.targetTimestamp;
        Instant instant = Instant.ofEpochMilli(reminderTargetTimestamp);
        String discordReminderTargetTimestamp = TimeFormat.DATE_TIME_LONG.format(instant);


        long currentTime = System.currentTimeMillis();
        instant = Instant.ofEpochMilli(currentTime);
        String discordCurrentTimestamp = TimeFormat.DATE_TIME_LONG.format(instant);


        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title + " | Reminder #" + reminderID);
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setDescription(
                "Message: " + reminderMessage + "\n"
                        + "Current Time: " + discordCurrentTimestamp + "\n"
                        + "Due on: " + discordReminderTargetTimestamp
        );
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Reminder",
                user.getEffectiveAvatarUrl()
        );

        return embedBuilder.build();
    }

    @Override
    public MessageEmbed createListEmbed(User user, ArrayList<?> userReminders) {
        if(userReminders.isEmpty() || !(userReminders.getFirst() instanceof Reminder)) { return null; }
        // TODO: Add pagination
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Tasks");
        embedBuilder.setColor(Color.ORANGE);

        for(Object objectReminder : userReminders) {
            if(!(objectReminder instanceof Reminder reminder)) { continue; }
            String reminderMessage = reminder.message;
            int reminderID = reminder.reminderID;
            Instant instant = Instant.ofEpochMilli(reminder.targetTimestamp);
            String discordTimestamp = TimeFormat.DATE_TIME_LONG.format(instant);

            embedBuilder.addField(
                    "#" + reminderID + " | " + reminderMessage,
                    "Due on: " + discordTimestamp,
                    false);
        }
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Reminders",
                user.getEffectiveAvatarUrl()
        );
        return embedBuilder.build();
    }

    @Override
    public MessageEmbed createErrorEmbed(User user, String message) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Error | Reminder");
        embedBuilder.setColor(Color.RED);
        embedBuilder.setDescription(message);
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Reminder Error",
                user.getEffectiveAvatarUrl()
        );

        return embedBuilder.build();
    }

    @Override
    public MessageEmbed createMissingParametersEmbed(User user, String message) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Task Interaction Incomplete");
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setDescription(message);
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Task Lacking Information",
                user.getEffectiveAvatarUrl()
        );

        return embedBuilder.build();
    }
}