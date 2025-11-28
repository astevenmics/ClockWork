package mist.mystralix.Objects.Reminder;

import mist.mystralix.Database.DBReminderHandler;
import mist.mystralix.Listeners.CommandListener.CommandObjects.ReminderEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class ReminderHandler {

    private final DBReminderHandler DB_REMINDER_HANDLER;

    public ReminderHandler() {
        this.DB_REMINDER_HANDLER = new DBReminderHandler();
    }

    public void createReminder(
            String reminderUUID,
            String userDiscordID,
            String reminderMessage,
            long targetTimestamp
    ) {
        DB_REMINDER_HANDLER
            .create(
                new Reminder(
                reminderUUID,
                userDiscordID,
                reminderMessage,
                targetTimestamp
                )
            );
    }

    public Reminder getUserReminder(
            String userDiscordID,
            int reminderID
    ) {
        return DB_REMINDER_HANDLER.read(userDiscordID, reminderID);
    }

    public Reminder getUserReminder(
            String userDiscordID,
            String reminderUUID
    ) {
        return DB_REMINDER_HANDLER.read(userDiscordID, reminderUUID);
    }

    public ArrayList<Reminder> getAllUserReminders(
            String userDiscordID
    ) {
        return DB_REMINDER_HANDLER.readAll(userDiscordID);
    }

    public void updateUserReminder(
            Reminder updatedReminder
    ) {
        DB_REMINDER_HANDLER.update(updatedReminder);
    }

    public void delete(
            Reminder reminder
    ) {
        String reminderUUID = reminder.reminderUUID;
        String userDiscordID = reminder.userDiscordID;
        DB_REMINDER_HANDLER.delete(
                reminderUUID,
                userDiscordID
        );
    }

    public void sendReminder(User user, Reminder reminder) {
        ReminderEmbed reminderEmbed = new ReminderEmbed();
        if (user == null) {
            System.out.println("Error: User not found! | " + reminder.userDiscordID);
            return;
        }

        user.openPrivateChannel().queue(
                channel -> {
                    MessageEmbed messageEmbed = reminderEmbed.createMessageEmbed(
                            user,
                            "Reminder Alert!",
                            reminder
                    );
                    channel.sendMessageEmbeds(messageEmbed).queue();
        });
    }


}