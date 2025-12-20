package mist.mystralix.application.reminder;

import mist.mystralix.domain.reminder.Reminder;
import mist.mystralix.infrastructure.repository.reminder.ReminderRepository;
import mist.mystralix.presentation.embeds.ReminderEmbed;
import mist.mystralix.utils.IdentifiableFetcher;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.HashSet;

public class ReminderService implements IdentifiableFetcher<Reminder> {

    private final ReminderRepository REMINDER_REPOSITORY;

    public ReminderService(ReminderRepository reminderRepository) {
        this.REMINDER_REPOSITORY = reminderRepository;
    }

    public void createReminder(
            String reminderUUID,
            String userDiscordID,
            String reminderMessage,
            long targetTimestamp,
            boolean isNotificationSent
    ) {
        REMINDER_REPOSITORY.create(
                new Reminder(
                        reminderUUID,
                        userDiscordID,
                        reminderMessage,
                        targetTimestamp,
                        isNotificationSent
                )
        );
    }

    public Reminder getUserReminder(
            String userDiscordID,
            int reminderID
    ) {
        return REMINDER_REPOSITORY.findByDiscordIDAndID(userDiscordID, reminderID);
    }

    public Reminder getUserReminder(
            String userDiscordID,
            String reminderUUID
    ) {
        return REMINDER_REPOSITORY.findByDiscordIDAndUUID(userDiscordID, reminderUUID);
    }

    public ArrayList<Reminder> getAllUserReminders(String userDiscordID) {
        return REMINDER_REPOSITORY.readAll(userDiscordID);
    }

    public void updateUserReminder(Reminder updatedReminder) {
        REMINDER_REPOSITORY.update(updatedReminder);
    }

    public void delete(Reminder reminder) {
        REMINDER_REPOSITORY.delete(reminder);
    }

    public HashSet<Reminder> getAllActiveReminders() {
        return REMINDER_REPOSITORY.getAllActiveReminders();
    }

    public void sendReminder(User user, Reminder reminder) {

        ReminderEmbed reminderEmbed = new ReminderEmbed();

        // Safety check to avoid null pointer issues
        if (user == null) {
            System.out.println("Error: User not found! | " + reminder.getUserDiscordID());
            return;
        }

        // Build the embed payload
        MessageEmbed embed = reminderEmbed.createMessageEmbed(
                user,
                "Reminder Alert!",
                reminder
        );

        // Open DM channel and send the embed asynchronously
        user.openPrivateChannel().queue(
                channel -> channel.sendMessageEmbeds(embed).queue(
                        _ -> System.out.println("✅ Reminder sent to " + user.getEffectiveName()),
                        fail -> System.out.println("❌ Failed to send to " + user.getEffectiveName() +
                                " (DM blocked): " + fail.getMessage())
                ),
                _ -> System.out.println(
                        "❌ Cannot open DM with " + user.getEffectiveName() +
                                " — DMs are off or the user blocked the bot."
                )
        );
    }

    public void reminderSentUpdate(Reminder reminder) {
        REMINDER_REPOSITORY.updateIsNotificationSent(reminder);
    }


    @Override
    public Reminder fetchByUserIDAndObjectID(String userDiscordId, int id) {
        return getUserReminder(userDiscordId, id);
    }

    @Override
    public Reminder fetchByUserIDAndObjectUUID(String userDiscordId, String objectUUID) {
        return getUserReminder(userDiscordId, objectUUID);
    }

}