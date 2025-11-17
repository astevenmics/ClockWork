package mist.mystralix.Objects;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

/*
    CustomEmbed | Utility Class
        - Responsible for creating custom embeds per specific uses
*/
public class CustomEmbed {

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
    public static MessageEmbed createTaskEmbed(
            User user,
            String embedTitle,
            Task task
    ) {

        TaskDAO taskDAO = task.taskDAO;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(embedTitle + " | Task #" + task.taskID);
        embedBuilder.setColor(taskDAO.taskStatus.getColorValue());
        embedBuilder.setDescription(
                "Title: " + taskDAO.title + "\n"
                        + "Description: " + taskDAO.description + "\n"
                        + "Status: " + taskDAO.taskStatus.getIcon() + " "
                        + taskDAO.taskStatus.getStringValue()
        );
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Task Viewer",
                user.getEffectiveAvatarUrl()
        );

        return embedBuilder.build();
    }
}