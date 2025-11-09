package mist.mystralix.Listeners.CommandListener.SlashCommands;

import mist.mystralix.Enums.TaskStatus;
import mist.mystralix.ExternalFileHandler.FileHandler;
import mist.mystralix.Listeners.CommandListener.SlashCommand;
import mist.mystralix.Objects.Task;
import mist.mystralix.Objects.TaskHandler;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
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
                        .addChoice(TaskStatus.ALL.getValue(), TaskStatus.ALL.getIntValue())
                        .addChoice(TaskStatus.COMPLETED.getValue(), TaskStatus.COMPLETED.getIntValue())
                        .addChoice(TaskStatus.INPROGRESS.getValue(), TaskStatus.INPROGRESS.getIntValue())
                        .addChoice(TaskStatus.ARCHIVED.getValue(), TaskStatus.ARCHIVED.getIntValue())
                        .addChoice(TaskStatus.CANCELLED.getValue(), TaskStatus.CANCELLED.getIntValue())
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
            HashSet<Task> userTasks = taskHandler.getUserTasks(userFile);
            if(userTasks.isEmpty()) {
                event.reply("You currently do not have any tasks! Use the AddTask command to start.").queue();
                return;
            }
            HashSet<Task> tasks = userTasks.stream()
                    .filter(task -> task.taskStatus.equals(selectedTaskStatus))
                    .collect(Collectors.toCollection(HashSet::new));
            if(tasks.isEmpty()){
                tasks = userTasks;
            }
            event.deferReply().queue();
            tasks.forEach(
                    x -> {
                        String message = x.id + ". " + x.title + "\n" + x.description + "\n" + x.taskStatus.getValue();
                        event.getHook().sendMessage(message).queue();
                    }
            );
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


    }

}