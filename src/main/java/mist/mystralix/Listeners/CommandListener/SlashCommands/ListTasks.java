package mist.mystralix.Listeners.CommandListener.SlashCommands;

import mist.mystralix.Database.DBHandler;
import mist.mystralix.Enums.TaskStatus;
import mist.mystralix.Listeners.CommandListener.SlashCommand;
import mist.mystralix.Objects.Task;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
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

        DBHandler dbHandler = new DBHandler();
        ArrayList<Task> allTasks = dbHandler.getAllUserTasks(user.getId());

        // TODO: Use EmbedBuilder
        if(allTasks.isEmpty()) {
            event.reply("You currently do not have any tasks! Use the AddTask command to start.").queue();
            return;
        }

            ArrayList<Task> userTasksList = allTasks.stream()
                    .filter(task -> task
                            .taskStatus
                            .equals(selectedTaskStatus))
                    .collect(Collectors.toCollection(ArrayList::new));

            if(userTasksList.isEmpty() && selectedTaskStatus != TaskStatus.ALL) {
                event.reply("No tasks found that have the status of **" + selectedTaskStatus.getStringValue() + "**").queue();
                return;
            }

            ArrayList<Task> tasks = selectedTaskStatus == TaskStatus.ALL ? allTasks : userTasksList;

            // TODO: Add pagination
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Tasks");
            embedBuilder.setColor(selectedTaskStatus.getColorValue());
            for(Task task : tasks) {
                embedBuilder.addField(
                        task.title,
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

    }
}