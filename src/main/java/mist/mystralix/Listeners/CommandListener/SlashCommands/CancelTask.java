package mist.mystralix.Listeners.CommandListener.SlashCommands;

import mist.mystralix.Enums.TaskStatus;
import mist.mystralix.ExternalFileHandler.FileHandler;
import mist.mystralix.Listeners.CommandListener.SlashCommand;
import mist.mystralix.Objects.Task;
import mist.mystralix.Objects.TaskHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.File;
import java.util.HashMap;

public class CancelTask implements SlashCommand {

    /* Command Name */
    /* /canceltask */
    @Override
    public String getName() {
        return "canceltask";
    }

    /* Command Description */
    @Override
    public String getDescription() {
        return "Updates specified task's status as cancelled.";
    }

    /* Command Options */
    @Override
    public OptionData[] getOptions() {
        return new OptionData[]{
                new OptionData(
                        OptionType.INTEGER,
                        "task_id",
                        "ID number of the task to cancel.",
                        true
                )
        };
    }

    /* Command Event/Output */
    @Override
    public void execute(SlashCommandInteraction event) {

        User user = event.getUser();

        OptionMapping option = event.getOption("task_id");
        if (option == null) { return; }
        int taskID = option.getAsInt();

        Task taskToCancel = null;

        try {
            FileHandler fileHandler = new FileHandler();
            TaskHandler taskHandler = new TaskHandler();

            File userTaskFile = fileHandler.getUserTaskFile(user);
            HashMap<Integer, Task> userTasks = taskHandler.getUserTasks(userTaskFile);

            taskToCancel = userTasks.get(taskID);
            taskHandler.cancelUserTask(userTaskFile, taskToCancel);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if(taskToCancel == null) { return; }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Task #" + taskID);
        embedBuilder.setColor(taskToCancel.taskStatus.getColorValue());
        embedBuilder.setDescription(
                "Title: " + taskToCancel.title + "\n"
                        + "Description: " + taskToCancel.description + "\n"
                        + "Status: " + taskToCancel.taskStatus.getIcon() + " "
                        + taskToCancel.taskStatus.getStringValue()
        );
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Task Viewer",
                user.getEffectiveAvatarUrl()
        );

        event.replyEmbeds(embedBuilder.build()).queue();

    }

}