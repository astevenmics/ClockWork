package mist.mystralix.Listeners.CommandListener.SlashCommands;

import mist.mystralix.Enums.TaskStatus;
import mist.mystralix.Listeners.CommandListener.SlashCommand;
import mist.mystralix.Objects.Task;
import mist.mystralix.Objects.TaskDAO;
import mist.mystralix.Objects.TaskHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ListTasks implements SlashCommand {

    /*
        Command Name
            - listtasks
            - Usage: /listtasks (Task Status: COMPLETED, INPROGRESS, ARCHIVED, CANCELLED)
    */
    @Override
    public String getName() {
        return "listtasks";
    }

    /* Command Description */
    @Override
    public String getDescription() {
        return "Lists all the tasks regardless of status: completed, in progress, archived, or cancelled.";
    }

    /*
        Command Options
            - Provides the option to get either all the tasks or all the tasks of a specific TaskStatus
            - type          | Determines the type of tasks the user wants to view
                            | Returns all tasks if not specified
                            | Options are: COMPLETED, INPROGRESS, ARCHIVED, and CANCELLED
    */
    @Override
    public OptionData[] getOptions() {
        return new OptionData[]{
                new OptionData(
                        OptionType.INTEGER,
                        "type",
                        "List all tasks of a certain type.",
                        false
                )
                        .addChoice(
                                TaskStatus.ALL.getIcon() + " " + TaskStatus.ALL.getStringValue(),
                                TaskStatus.ALL.getIntValue())
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
                        TaskStatus.CANCELLED.getIntValue())
        };
    }

    /* Command Event/Output */
    @Override
    public void execute(SlashCommandInteraction event) {

        User user = event.getUser();

        int option = event.getOption("type",
                () -> 1,
                OptionMapping::getAsInt
        );

        TaskStatus selectedTaskStatus = TaskStatus.getTaskStatus(option);

        TaskHandler taskHandler = new TaskHandler();
        ArrayList<Task> allTasks = taskHandler.getUserTasks(user);

        // TODO: Use EmbedBuilder
        if(allTasks.isEmpty()) {
            event.reply("You currently do not have any tasks! Use the AddTask command to start.").queue();
            return;
        }

            ArrayList<Task> userTasksList = allTasks.stream()
                    .filter(task -> task
                            .taskDAO
                            .taskStatus
                            .equals(selectedTaskStatus))
                    .collect(Collectors.toCollection(ArrayList::new));

            if(userTasksList.isEmpty() && selectedTaskStatus != TaskStatus.ALL) {
                event.reply("No tasks found that have the status of **" + selectedTaskStatus.getStringValue() + "**").queue();
                return;
            }

        EmbedBuilder embedBuilder = getEmbedBuilder(selectedTaskStatus, allTasks, userTasksList);
        embedBuilder.setFooter(
                    user.getEffectiveName() + " | Task Lists",
                    user.getEffectiveAvatarUrl()
            );

        event.replyEmbeds(embedBuilder.build()).queue();

    }

    @NotNull
    private static EmbedBuilder getEmbedBuilder(TaskStatus selectedTaskStatus, ArrayList<Task> allTasks, ArrayList<Task> userTasksList) {
        ArrayList<Task> tasks = selectedTaskStatus == TaskStatus.ALL ? allTasks : userTasksList;

        // TODO: Add pagination
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Tasks");
        embedBuilder.setColor(selectedTaskStatus.getColorValue());
        for(Task task : tasks) {
            TaskDAO taskDAO = task.taskDAO;
            embedBuilder.addField(
                    "#" + task.taskID + " | " + taskDAO.title,
                    "Description: " + taskDAO.description +
                            "\nStatus: " + taskDAO.taskStatus.getIcon() +
                            " " + taskDAO.taskStatus.getStringValue(),
                    true);
        }
        return embedBuilder;
    }
}