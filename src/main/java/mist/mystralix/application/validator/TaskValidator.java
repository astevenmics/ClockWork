package mist.mystralix.application.validator;

import mist.mystralix.domain.task.ITask;
import mist.mystralix.presentation.embeds.IMessageEmbedBuilder;
import mist.mystralix.utils.messages.TaskMessages;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class TaskValidator {

    public static MessageEmbed validateTaskAccess(User user, ITask task, IMessageEmbedBuilder taskEmbed) {
        if (!task.getUserDiscordID().equals(user.getId())) {
            return taskEmbed.createErrorEmbed(user,
                    String.format(TaskMessages.TASK_NOT_CREATED_BY_USER, task.getId())
            );
        }
        return null;
    }

}
