package mist.mystralix.presentation.embeds;

import mist.mystralix.domain.reminder.Reminder;
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
        if (!(object instanceof Reminder reminder)) return null;

        String reminderMessage = reminder.getMessage();
        int reminderID = reminder.getId();

        Instant targetInstant = Instant.ofEpochMilli(reminder.getTargetTimestamp());
        String discordReminderTargetTimestamp = TimeFormat.DATE_TIME_LONG.format(targetInstant);

        Instant nowInstant = Instant.ofEpochMilli(System.currentTimeMillis());
        String discordCurrentTimestamp = TimeFormat.DATE_TIME_LONG.format(nowInstant);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title + " | Reminder #" + reminderID);
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setDescription(
                "Message: " + reminderMessage + "\n" +
                        "Current Time: " + discordCurrentTimestamp + "\n" +
                        "Due on: " + discordReminderTargetTimestamp
        );
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Reminder",
                user.getEffectiveAvatarUrl()
        );

        return embedBuilder.build();
    }

    @Override
    public MessageEmbed createListEmbed(User user, ArrayList<?> userReminders) {
        if (userReminders.isEmpty() || !(userReminders.getFirst() instanceof Reminder)) {
            return null;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Reminders");
        embedBuilder.setColor(Color.ORANGE);

        for (Object objectReminder : userReminders) {
            if (!(objectReminder instanceof Reminder reminder)) continue;

            String reminderMessage = reminder.getMessage();
            int reminderID = reminder.getId();

            Instant instant = Instant.ofEpochMilli(reminder.getTargetTimestamp());
            String discordTimestamp = TimeFormat.DATE_TIME_LONG.format(instant);

            embedBuilder.addField(
                    "#" + reminderID + " | " + reminderMessage,
                    "Due on: " + discordTimestamp,
                    false
            );
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
        embedBuilder.setTitle("Reminder Interaction Incomplete");
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setDescription(message);
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Missing Reminder Info",
                user.getEffectiveAvatarUrl()
        );

        return embedBuilder.build();
    }

    public MessageEmbed createReminderEmbed(User user, Reminder reminder) {
        return new EmbedBuilder()
                .setTitle("Reminder Alert | Reminder #" + reminder.getId())
                .setColor(Color.GREEN)
                .setDescription("Message: " + reminder.getMessage())
                .setFooter("Reminder for " + user.getEffectiveName(), user.getEffectiveAvatarUrl())
                .build();
    }
}