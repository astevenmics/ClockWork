package mist.mystralix.application.helper;

import mist.mystralix.domain.enums.TaskStatus;
import mist.mystralix.domain.task.ITask;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Optional;

public class TaskHelper {

    public static void updateTask(SlashCommandInteraction event, ITask task) {
        String newTitle = event.getOption("title", () -> null, OptionMapping::getAsString);
        String newDesc = event.getOption("description", () -> null, OptionMapping::getAsString);
        Integer newStatus = event.getOption("status", () -> null, OptionMapping::getAsInt);

        Optional.ofNullable(newTitle).ifPresent(task::setTitle);
        Optional.ofNullable(newDesc).ifPresent(task::setDescription);
        Optional.ofNullable(newStatus).ifPresent(task::setStatus);
    }

    public static OptionData getTaskTypeOptions() {
        return new OptionData(
                OptionType.INTEGER,
                "status",
                "New status for the task.",
                false
        )
                .addChoice(
                        TaskStatus.COMPLETED.getIcon() + " " + TaskStatus.COMPLETED.getStringValue(),
                        TaskStatus.COMPLETED.getIntValue())
                .addChoice(
                        TaskStatus.INPROGRESS.getIcon() + " " + TaskStatus.INPROGRESS.getStringValue(),
                        TaskStatus.INPROGRESS.getIntValue())
                .addChoice(
                        TaskStatus.ARCHIVED.getIcon() + " " + TaskStatus.ARCHIVED.getStringValue(),
                        TaskStatus.ARCHIVED.getIntValue())
                .addChoice(
                        TaskStatus.CANCELLED.getIcon() + " " + TaskStatus.CANCELLED.getStringValue(),
                        TaskStatus.CANCELLED.getIntValue());
    }

}
