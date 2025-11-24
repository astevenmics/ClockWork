package mist.mystralix.Listeners.CommandListener.CommandObjects;

import mist.mystralix.Objects.Reminder.Reminder;
import mist.mystralix.Objects.Task.Task;
import mist.mystralix.Objects.Task.TaskDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ReminderEmbed {

    /*
        - Creates a complete embed specifically for task-related actions
            * Adding tasks
            * Cancelling tasks
            * Viewing a specific task
        - Three parameters
            * User user
                - Expected to be the user that executed the command
                - Used in getting the user's effective name
                - Used in getting user's avatar URL
            * String embedTitle
                - Contains the custom title for each usage
                - Allows the task to be different for each usage
            * Task task
                - Used in getting all the information posted in the embed
    */
    @NotNull
    public static MessageEmbed createReminderEmbed(
            User user,
            String embedTitle,
            Reminder reminder,
            long currentTime
    ) {

        String reminderMessage = reminder.message;
        long reminderTargetTimestamp = reminder.targetTimestamp;
        int reminderID = reminder.reminderID;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(embedTitle + " | Reminder #" + reminderID);
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setDescription(
                "Message: " + reminderMessage + "\n"
                        + "Current Time: " + currentTime + "\n"
                        + "Timestamp: " + reminderTargetTimestamp
        );
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Reminder",
                user.getEffectiveAvatarUrl()
        );

        return embedBuilder.build();
    }

}