package mist.mystralix.Listeners.CommandListener.CommandObjects;

import mist.mystralix.Objects.Task;
import mist.mystralix.Objects.TaskHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.function.Function;

public class TaskFunctions {

    public static MessageEmbed handleTask(
            SlashCommandInteraction event,
            Function<Task, MessageEmbed> action
    ) {
        User user = event.getUser();

        OptionMapping option = event.getOption("task_id");
        if (option == null) {
            return TaskEmbed.createTaskErrorEmbed(user, "No task ID provided");
        }
        int taskID = option.getAsInt();

        TaskHandler taskHandler = new TaskHandler();

        Task task = taskHandler.getUserTask(user, taskID);
        if(task == null) {
            return TaskEmbed.createLackingInformationEmbed(
                    user,
                    "No task found"
            );
        }

        return action.apply(task);
    }

}