package mist.mystralix.Listeners.CommandListener.CommandObjects.Task;

import mist.mystralix.Listeners.CommandListener.CommandObjects.IMessageEmbedBuilder;
import mist.mystralix.Objects.Task.Task;
import mist.mystralix.Objects.Task.TaskDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.ArrayList;

/**
 * Responsible for building embed messages related to task objects.
 *
 * <p>This class serves as the presentation layer for any Task-related output
 * in Discord. It produces embeds for:
 * <ul>
 *     <li>Single task views</li>
 *     <li>Task lists</li>
 *     <li>Error messages</li>
 *     <li>Missing parameter notifications</li>
 * </ul>
 *
 * Implements {@link IMessageEmbedBuilder} for standardized output behavior.
 */
public class TaskEmbed implements IMessageEmbedBuilder {

    /**
     * Builds an embed representing a single task.
     *
     * @param user  the user who triggered the interaction
     * @param title the title of the embed
     * @param object a generic object expected to be a {@link Task}
     * @return a formatted {@link MessageEmbed}, or {@code null} if the object is not a Task
     */
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
        embed.setTitle(title + " | Task #" + task.getTaskID());
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

    /**
     * Builds an embed containing a list of tasks.
     *
     * <p>If the list is empty or does not contain Task objects,
     * this method returns {@code null}.</p>
     *
     * @param user the user requesting the list
     * @param list the collection of task objects
     * @return an embed showing all tasks, or {@code null} on invalid input
     */
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
                    "#" + task.getTaskID() + " | " + taskTitle,
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

    /**
     * Builds an embed representing a task-related error.
     *
     * @param user the user who triggered the error message
     * @param message the content of the error
     * @return a red-highlighted error embed
     */
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

    /**
     * Builds an embed notifying the user that needed parameters were missing
     * (e.g., missing task ID, title, or description).
     *
     * @param user the user receiving the notice
     * @param message the error message describing missing parameters
     * @return an orange-highlighted embed indicating incomplete user input
     */
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
