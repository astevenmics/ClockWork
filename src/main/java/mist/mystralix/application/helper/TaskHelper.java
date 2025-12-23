package mist.mystralix.application.helper;

import mist.mystralix.domain.enums.TaskStatus;
import mist.mystralix.domain.task.TaskDAO;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Optional;

public class TaskHelper {

    public static void updateTaskDAO(SlashCommandInteraction event, TaskDAO taskDAO) {
        String newTitle = event.getOption("title", () -> null, OptionMapping::getAsString);
        String newDesc = event.getOption("description", () -> null, OptionMapping::getAsString);
        int statusInt = event.getOption("type", () -> 0, OptionMapping::getAsInt);

        TaskStatus newStatus = TaskStatus.getTaskStatus(statusInt);

        Optional.ofNullable(newTitle).ifPresent(taskDAO::setTitle);
        Optional.ofNullable(newDesc).ifPresent(taskDAO::setDescription);
        Optional.ofNullable(newStatus).ifPresent(taskDAO::setTaskStatus);
    }

    public static OptionData getTaskTypeOptions() {
        return new OptionData(
                OptionType.INTEGER,
                "type",
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
