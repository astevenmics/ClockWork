package mist.mystralix.Listeners.CommandListener.CommandObjects;

import mist.mystralix.Database.DBTaskRepository;
import mist.mystralix.Objects.Task.Task;
import mist.mystralix.Objects.Task.TaskService;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.function.Function;

public class TaskFunctions {

    public static MessageEmbed handleTask(
            SlashCommandInteraction event,
            TaskService taskService,
            TaskEmbed taskEmbed,
            Function<Task, MessageEmbed> action
    ) {
        User user = event.getUser();

        OptionMapping option = event.getOption("task_id");
        if (option == null) {
            return taskEmbed.createMissingParametersEmbed(user, "No task ID provided");
        }
        int taskID = option.getAsInt();

        Task task = taskService.getUserTask(user, taskID);
        if(task == null) {
            return taskEmbed.createErrorEmbed(
                    user,
                    "No task found"
            );
        }

        return action.apply(task);
    }

}