package mist.mystralix.Listeners.CommandListener.CommandObjects;

import mist.mystralix.Enums.TaskStatus;
import mist.mystralix.Objects.Task;
import mist.mystralix.Objects.TaskDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

/*
    CustomEmbed | Utility Class
        - Responsible for creating custom embeds per specific uses
*/
public class TaskEmbed {

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
    public static MessageEmbed createTaskEmbed(
            User user,
            String embedTitle,
            Task task
    ) {

        TaskDAO taskDAO = task.taskDAO;
        String taskDAOTitle = taskDAO.title;
        String taskDAODescription = taskDAO.title;
        String taskDAOStatus = taskDAO.taskStatus.getIcon() + " " + taskDAO.taskStatus.getStringValue();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(embedTitle + " | Task #" + task.taskID);
        embedBuilder.setColor(taskDAO.taskStatus.getColorValue());
        embedBuilder.setDescription(
                "Title: " + taskDAOTitle + "\n"
                        + "Description: " + taskDAODescription + "\n"
                        + "Status: " + taskDAOStatus
        );
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Task",
                user.getEffectiveAvatarUrl()
        );

        return embedBuilder.build();
    }

    @NotNull
    public static MessageEmbed createTaskListEmbed(
            User user,
            TaskStatus selectedTaskStatus,
            ArrayList<Task> allTasks,
            ArrayList<Task> userTasksList
    ) {
        ArrayList<Task> tasks = selectedTaskStatus == TaskStatus.ALL ? allTasks : userTasksList;

        // TODO: Add pagination
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Tasks");
        embedBuilder.setColor(selectedTaskStatus.getColorValue());
        for(Task task : tasks) {
            TaskDAO taskDAO = task.taskDAO;
            String taskDAOTitle = taskDAO.title;
            String taskDAODescription = taskDAO.title;
            String taskDAOStatus = taskDAO.taskStatus.getIcon() + " " + taskDAO.taskStatus.getStringValue();

            embedBuilder.addField(
                    "#" + task.taskID + " | " + taskDAOTitle,
                    "Description: " + taskDAODescription +
                            "\nStatus: " + taskDAOStatus,
                    true);
        }
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Task Lists",
                user.getEffectiveAvatarUrl()
        );
        return embedBuilder.build();
    }

    @NotNull
    public static MessageEmbed createTaskErrorEmbed(
            User user,
            String errorMessage
    ) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Error | Task");
        embedBuilder.setColor(Color.RED);
        embedBuilder.setDescription(errorMessage);
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Task Error",
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