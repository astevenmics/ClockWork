package mist.mystralix.presentation.embeds;

import mist.mystralix.application.pagination.PaginationEmbedCreator;
import mist.mystralix.domain.reminder.Reminder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;

public class ReminderEmbed implements IMessageEmbedBuilder, PaginationEmbedCreator {

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
                "Make sure to keep your DMs open to receive reminders!",
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

    @Override
    public MessageEmbed createPaginatedEmbed(User user, ArrayList<Object> data, int currentPage, int itemsPerPage) {
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(currentPage * itemsPerPage, data.size());
        int totalPages = (int) Math.ceil((double) data.size() / itemsPerPage);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("List of Reminders | " + user.getEffectiveName());

        for (int i = startIndex; i < endIndex; i++) {
            if (!(data.get(i) instanceof Reminder reminder)) continue;
            Instant targetInstant = Instant.ofEpochMilli(reminder.getTargetTimestamp());
            String discordReminderTargetTimestamp = TimeFormat.DATE_TIME_LONG.format(targetInstant);

            embedBuilder.addField("Reminder #" + reminder.getId(),
                    String.format(
                            """
                                    Message: %s
                                    Due On: %s
                                    """,
                            reminder.getMessage(),
                            discordReminderTargetTimestamp
                    ),
                    false);
        }

        embedBuilder.setFooter("Reminder Count: " + data.size() + " | Page " + currentPage + "/" + totalPages, user.getEffectiveAvatarUrl());

        return embedBuilder.build();
    }

    public MessageEmbed createReminderEmbed(User user, Reminder reminder) {

        Instant targetInstant = Instant.ofEpochMilli(reminder.getTargetTimestamp());
        String discordReminderTargetTimestamp = TimeFormat.DATE_TIME_LONG.format(targetInstant);

        Instant createdInstant = Instant.ofEpochMilli(reminder.getCreatedTimestamp());
        String discordCreatedTimestamp = TimeFormat.DATE_TIME_LONG.format(createdInstant);

        return new EmbedBuilder()
                .setTitle("Reminder Alert | Reminder #" + reminder.getId())
                .setColor(Color.GREEN)
                .setDescription(
                        String.format(
                                """
                                        Message: %s
                                        
                                        Created On: %s
                                        Due on: %s
                                        """,
                                reminder.getMessage(),
                                discordCreatedTimestamp,
                                discordReminderTargetTimestamp
                        ))
                .setFooter("Reminder for " + user.getEffectiveName(), user.getEffectiveAvatarUrl())
                .build();
    }
}