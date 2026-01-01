package mist.mystralix.presentation.embeds;

import mist.mystralix.application.pagination.PaginationEmbedCreator;
import mist.mystralix.domain.enums.TaskStatus;
import mist.mystralix.domain.task.UserTask;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.ArrayList;

public class UserTaskEmbed implements IMessageEmbedBuilder, PaginationEmbedCreator {

    @Override
    public <T> MessageEmbed createMessageEmbed(User user, String title, T object) {

        // Ensure provided object is a Task instance
        if (!(object instanceof UserTask userTask)) {
            return null;
        }

        TaskStatus status = TaskStatus.getTaskStatus(userTask.getStatus());
        String taskTitle = userTask.getTitle();
        String taskDesc = userTask.getDescription();
        String taskStatus = status.getIcon() + " " + status.getStringValue();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title + " | Task #" + userTask.getId());
        embed.setColor(status.getColorValue());
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

    @Override
    public MessageEmbed createPaginatedEmbed(User user, ArrayList<Object> data, int currentPage, int itemsPerPage) {
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(currentPage * itemsPerPage, data.size());
        int totalPages = (int) Math.ceil((double) data.size() / itemsPerPage);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("List of Tasks | " + user.getEffectiveName());

        for (int i = startIndex; i < endIndex; i++) {
            if (!(data.get(i) instanceof UserTask userTask)) continue;
            TaskStatus status = TaskStatus.getTaskStatus(userTask.getStatus());
            embedBuilder.addField("Task #" + userTask.getId(),
                    String.format(
                            """
                                    Title: **%s**
                                    Status: %s **%s**
                                    """,
                            userTask.getTitle(),
                            status.getIcon(),
                            status.getStringValue()
                    ),
                    true);
        }

        embedBuilder.setFooter("Task Count: " + data.size() + " | Page " + currentPage + "/" + totalPages, user.getEffectiveAvatarUrl());

        return embedBuilder.build();
    }
}