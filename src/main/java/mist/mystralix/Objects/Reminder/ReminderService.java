package mist.mystralix.Objects.Reminder;

import mist.mystralix.Database.Reminder.ReminderRepository;
import mist.mystralix.Listeners.CommandListener.CommandObjects.Reminder.ReminderEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.List;

public class ReminderService {

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
        REMINDER_REPOSITORY
            .create(
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
        return REMINDER_REPOSITORY.read(userDiscordID, reminderID);
    }

    public Reminder getUserReminder(
            String userDiscordID,
            String reminderUUID
    ) {
        return REMINDER_REPOSITORY.read(userDiscordID, reminderUUID);
    }

    public List<Reminder> getAllUserReminders(
            String userDiscordID
    ) {
        return REMINDER_REPOSITORY.readAll(userDiscordID);
    }

    public void updateUserReminder(
            Reminder updatedReminder
    ) {
        REMINDER_REPOSITORY.update(updatedReminder);
    }

    public void delete(
            Reminder reminder
    ) {
        String reminderUUID = reminder.reminderUUID;
        String userDiscordID = reminder.userDiscordID;
        REMINDER_REPOSITORY.delete(
                reminderUUID,
                userDiscordID
        );
    }

    public HashSet<Reminder> getAllActiveReminders() {
        return REMINDER_REPOSITORY.getAllActiveReminders();
    }

    public void sendReminder(User user, Reminder reminder) {
        ReminderEmbed reminderEmbed = new ReminderEmbed();
        if (user == null) {
            System.out.println("Error: User not found! | " + reminder.userDiscordID);
            return;
        }

        MessageEmbed embed = reminderEmbed.createMessageEmbed(
                user,
                "Reminder Alert!",
                reminder
        );

        user.openPrivateChannel().queue(
                channel -> channel.sendMessageEmbeds(embed).queue(
                        _ -> System.out.println("✅ Reminder sent to " + user.getEffectiveName()),
                        fail -> System.out.println("❌ Failed to send to " + user.getEffectiveName() + " (DM blocked): " + fail.getMessage())
                ),
                _ -> System.out.println("❌ Cannot open DM with " + user.getEffectiveName() + " — DMs off or user blocked the bot.")
        );
    }

    public void reminderSentUpdate(Reminder reminder) {
        REMINDER_REPOSITORY.updateIsNotificationSent(reminder);
    }


}