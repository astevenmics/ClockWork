package mist.mystralix.Objects;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class CustomEmbed {

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