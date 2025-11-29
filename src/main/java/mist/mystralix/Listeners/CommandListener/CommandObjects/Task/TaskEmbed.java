package mist.mystralix.Listeners.CommandListener.CommandObjects.Task;

import mist.mystralix.Listeners.CommandListener.CommandObjects.IMessageEmbedBuilder;
import mist.mystralix.Objects.Task.Task;
import mist.mystralix.Objects.Task.TaskDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.ArrayList;

/*
    CustomEmbed | Utility Class
        - Responsible for creating custom embeds per specific uses
*/
public class TaskEmbed implements IMessageEmbedBuilder {

    @Override
    public <T> MessageEmbed createMessageEmbed(User user, String title, T object) {
        if(!(object instanceof Task task)) { return null; }
        TaskDAO taskDAO = task.taskDAO;
        String taskDAOTitle = taskDAO.title;
        String taskDAODescription = taskDAO.description;
        String taskDAOStatus = taskDAO.taskStatus.getIcon() + " " + taskDAO.taskStatus.getStringValue();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title + " | Task #" + task.taskID);
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

    @Override
    public MessageEmbed createListEmbed(User user, ArrayList<?> list) {
        if (list.isEmpty() || !(list.getFirst() instanceof Task)) { return null; }
        // TODO: Add pagination
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Tasks");
        embedBuilder.setColor(Color.WHITE);
        for(Object objectTask : list) {
            if(!(objectTask instanceof Task task)) { continue; }
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

    @Override
    public MessageEmbed createErrorEmbed(User user, String message) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Error | Task");
        embedBuilder.setColor(Color.RED);
        embedBuilder.setDescription(message);
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Task Error",
                user.getEffectiveAvatarUrl()
        );

        return embedBuilder.build();
    }

    @Override
    public MessageEmbed createMissingParametersEmbed(User user, String message) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Task Interaction Incomplete");
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setDescription(message);
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Task Lacking Information",
                user.getEffectiveAvatarUrl()
        );

        return embedBuilder.build();
    }
}