package mist.mystralix.Listeners.CommandListener.CommandObjects.Reminder;

import mist.mystralix.Listeners.CommandListener.CommandObjects.IMessageEmbedBuilder;
import mist.mystralix.Objects.Reminder.Reminder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;

/**
 * Builds all Discord embeds related to reminders.
 *
 * <p>This class formats reminder data into readable Discord embed messages
 * for various scenarios including:</p>
 *
 * <ul>
 *     <li>A single reminder summary</li>
 *     <li>A list of reminders</li>
 *     <li>Error states</li>
 *     <li>Missing options or invalid input</li>
 * </ul>
 *
 * <p>The class implements {@link IMessageEmbedBuilder} to maintain consistency
 * with other embed creators in the system (e.g., tasks).</p>
 */
public class ReminderEmbed implements IMessageEmbedBuilder {

    /**
     * Creates a formatted embed displaying a single reminder.
     *
     * <p>The embed includes:</p>
     * <ul>
     *     <li>The reminder message</li>
     *     <li>The current timestamp (Discord formatted)</li>
     *     <li>The target reminder time (Discord formatted)</li>
     *     <li>User footer information</li>
     * </ul>
     *
     * @param user   the user requesting or owning the reminder
     * @param title  a title prefix describing what the embed represents
     * @param object the Reminder object to display (must be a {@link Reminder})
     * @return a populated {@link MessageEmbed}, or {@code null} if object is not a Reminder
     */
    @Override
    public <T> MessageEmbed createMessageEmbed(User user, String title, T object) {
        if (!(object instanceof Reminder reminder)) return null;

        String reminderMessage = reminder.getMessage();
        int reminderID = reminder.getReminderID();

        // Format target time
        Instant targetInstant = Instant.ofEpochMilli(reminder.getTargetTimestamp());
        String discordReminderTargetTimestamp = TimeFormat.DATE_TIME_LONG.format(targetInstant);

        // Format current time
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

    /**
     * Creates a list-style embed displaying multiple reminders.
     *
     * <p>Each reminder is rendered as a field containing:</p>
     * <ul>
     *     <li>Reminder ID and message</li>
     *     <li>Formatted due date</li>
     * </ul>
     *
     * <p>Note: Pagination support is marked as a future enhancement.</p>
     *
     * @param user          the user whose reminders are being listed
     * @param userReminders a list of reminder objects to render
     * @return a {@link MessageEmbed}, or {@code null} if the list is empty or invalid
     */
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
            int reminderID = reminder.getReminderID();

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

    /**
     * Creates an error embed for incorrect or failed reminder actions.
     *
     * @param user    the user receiving the error
     * @param message the error message to display
     * @return a red-colored error embed
     */
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

    /**
     * Creates an embed for missing or incomplete user input.
     *
     * <p>Used when required fields are missing from slash command options.</p>
     *
     * @param user    the user who invoked the command
     * @param message message describing what input is missing
     * @return an orange-colored warning embed
     */
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
}
