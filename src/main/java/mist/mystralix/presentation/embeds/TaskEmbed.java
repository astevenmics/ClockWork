package mist.mystralix.presentation.embeds;

import mist.mystralix.domain.task.Task;
import mist.mystralix.domain.task.TaskDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.ArrayList;

public class TaskEmbed implements IMessageEmbedBuilder {

    @Override
    public <T> MessageEmbed createMessageEmbed(User user, String title, T object) {

        // Ensure provided object is a Task instance
        if (!(object instanceof Task task)) {
            return null;
        }

        TaskDAO taskDAO = task.getTaskDAO();
        String taskTitle = taskDAO.getTitle();
        String taskDesc = taskDAO.getDescription();
        String taskStatus = taskDAO.getTaskStatus().getIcon() + " " + taskDAO.getTaskStatus().getStringValue();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title + " | Task #" + task.getId());
        embed.setColor(taskDAO.getTaskStatus().getColorValue());
        embed.setDescription(
                "Title: " + taskTitle + "\n" +
                        "Description: " + taskDesc + "\n" +
                        "Status: " + taskStatus
        );

        embed.setFooter(
                user.getEffectiveName() + " | Task",
                user.getEffectiveAvatarUrl()
        );

        return embed.build();
    }

    @Override
    public MessageEmbed createListEmbed(User user, ArrayList<?> list) {

        // Validate list contents
        if (list.isEmpty() || !(list.getFirst() instanceof Task)) {
            return null;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Tasks");
        embed.setColor(Color.WHITE);

        // TODO: Pagination support for large task lists
        for (Object obj : list) {
            if (!(obj instanceof Task task)) continue;

            TaskDAO dao = task.getTaskDAO();
            String taskTitle = dao.getTitle();
            String taskDesc = dao.getDescription();
            String taskStatus = dao.getTaskStatus().getIcon() + " " + dao.getTaskStatus().getStringValue();

            embed.addField(
                    "#" + task.getId() + " | " + taskTitle,
                    "Description: " + taskDesc + "\n" +
                            "Status: " + taskStatus,
                    true
            );
        }

        embed.setFooter(
                user.getEffectiveName() + " | Task List",
                user.getEffectiveAvatarUrl()
        );

        return embed.build();
    }

    @Override
    public MessageEmbed createErrorEmbed(User user, String message) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Error | Task");
        embed.setColor(Color.RED);
        embed.setDescription(message);

        embed.setFooter(
                user.getEffectiveName() + " | Task Error",
                user.getEffectiveAvatarUrl()
        );

        return embed.build();
    }

    @Override
    public MessageEmbed createMissingParametersEmbed(User user, String message) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Task Interaction Incomplete");
        embed.setColor(Color.ORANGE);
        embed.setDescription(message);

        embed.setFooter(
                user.getEffectiveName() + " | Task Lacking Information",
                user.getEffectiveAvatarUrl()
        );

        return embed.build();
    }
}