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
import java.util.*;
import java.util.stream.Collectors;

public class ListTasks implements SlashCommand {

    /* Command Name */
    /* /listtasks */
    @Override
    public String getName() {
        return "listtasks";
    }

    /* Command Description */
    @Override
    public String getDescription() {
        return "Lists all the tasks regardless of status: completed, in progress, archived, or cancelled.";
    }

    /* Command Options */
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
        FileHandler fileHandler = new FileHandler();

        // TODO: Use EmbedBuilder
        try {
            File userFile = fileHandler.getUserTaskFile(user);
            HashMap<Integer, Task> userTasks = taskHandler.getUserTasks(userFile);
            if(userTasks.isEmpty()) {
                event.reply("You currently do not have any tasks! Use the AddTask command to start.").queue();
                return;
            }

            ArrayList<Task> userTasksList = userTasks
                    .values()
                    .stream()
                    .filter(task -> task
                            .taskStatus
                            .equals(selectedTaskStatus))
                    .sorted(Comparator.comparingInt(taskItem -> taskItem.id))
                    .collect(Collectors.toCollection(ArrayList::new));

            if(userTasksList.isEmpty() && selectedTaskStatus != TaskStatus.ALL) {
                event.reply("No tasks found that have the status of **" + selectedTaskStatus.getStringValue() + "**").queue();
                return;
            }

            ArrayList<Task> tasks =
                    selectedTaskStatus == TaskStatus.ALL ?
                            new ArrayList<>(userTasks.values()) : userTasksList;

            // TODO: Add pagination
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Tasks");
            for(Task task : tasks) {
                embedBuilder.addField(
                        task.id + " - " + task.title,
                        "Description: " + task.description +
                                "\nStatus: " + task.taskStatus.getIcon() +
                                " " + task.taskStatus.getStringValue(),
                        true);
            }
            embedBuilder.setFooter(
                    user.getEffectiveName() + " | Task Lists",
                    user.getEffectiveAvatarUrl()
            );

            event.replyEmbeds(embedBuilder.build()).queue();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


    }

}