package mist.mystralix.Listeners.CommandListener.CommandObjects;

import mist.mystralix.Objects.Reminder.Reminder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;

public class ReminderEmbed {

    @NotNull
    public static MessageEmbed createReminderEmbed(
            User user,
            String embedTitle,
            Reminder reminder
    ) {

        String reminderMessage = reminder.message;
        int reminderID = reminder.reminderID;

        long reminderTargetTimestamp = reminder.targetTimestamp;
        Instant instant = Instant.ofEpochMilli(reminderTargetTimestamp);
        String discordReminderTargetTimestamp = TimeFormat.DATE_TIME_LONG.format(instant);


        long currentTime = System.currentTimeMillis();
        instant = Instant.ofEpochMilli(currentTime);
        String discordCurrentTimestamp = TimeFormat.DATE_TIME_LONG.format(instant);


        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(embedTitle + " | Reminder #" + reminderID);
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

    @NotNull
    public static MessageEmbed createReminderListEmbed(
            User user,
            ArrayList<Reminder> userReminders
    ) {

        // TODO: Add pagination
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Tasks");
        embedBuilder.setColor(Color.ORANGE);
        for(Reminder reminder : userReminders) {
            int reminderID = reminder.reminderID;
            String reminderMessage = reminder.message;
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

    @NotNull
    public static MessageEmbed createReminderErrorEmbed(
            User user,
            String errorMessage
    ) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Error | Reminder");
        embedBuilder.setColor(Color.RED);
        embedBuilder.setDescription(errorMessage);
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Reminder Error",
                user.getEffectiveAvatarUrl()
        );

        return embedBuilder.build();
    }

    @NotNull
    public static MessageEmbed createLackingInformationEmbed(
            User user,
            String lackingInformationMessage
    ) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Task Interaction Incomplete");
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setDescription(lackingInformationMessage);
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Task Lacking Information",
                user.getEffectiveAvatarUrl()
        );

        return embedBuilder.build();
    }

}