package mist.mystralix.application.validator;

import mist.mystralix.domain.reminder.Reminder;
import mist.mystralix.presentation.embeds.ReminderEmbed;
import mist.mystralix.utils.messages.ReminderMessages;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class ReminderValidator {

    public static MessageEmbed validateReminderAccess(User user, Reminder reminder, ReminderEmbed reminderEmbed) {
        if (!reminder.getUserDiscordID().equals(user.getId())) {
            return reminderEmbed.createErrorEmbed(
                    user,
                    String.format(ReminderMessages.REMINDER_NOT_CREATED_BY_USER, reminder.getId())
            );
        }
        return null;
    }

}
