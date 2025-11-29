package mist.mystralix.Listeners.CommandListener.CommandObjects.Task;

import mist.mystralix.Objects.Task.Task;
import mist.mystralix.Objects.Task.TaskService;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.function.Function;

/**
 * Utility class providing reusable logic for handling task-based slash command actions.
 *
 * <p>This class centralizes the common flow required for most task operations:
 * <ol>
 *     <li>Validate required input (task_id)</li>
 *     <li>Retrieve the task from the {@link TaskService}</li>
 *     <li>Return an appropriate error embed when conditions fail</li>
 *     <li>Execute the provided action on the retrieved task</li>
 * </ol>
 *
 * <p>By using this helper method, command-specific implementations remain clean
 * and focused only on their unique behavior.</p>
 */
public class TaskFunctions {

    /**
     * Validates command input, retrieves the task, and executes a provided action.
     *
     * @param event        the slash command interaction from Discord
     * @param taskService  service used to fetch tasks from storage
     * @param taskEmbed    embed builder used to output task-related messages
     * @param action       a lambda or method reference that receives a valid Task
     *                     and returns a {@link MessageEmbed} for output
     *
     * @return a {@link MessageEmbed} with the result of the action,
     *         or an error/missing-parameter embed when validation fails
     */
    public static MessageEmbed handleTask(
            SlashCommandInteraction event,
            TaskService taskService,
            TaskEmbed taskEmbed,
            Function<Task, MessageEmbed> action
    ) {
        User user = event.getUser();

        // Retrieve the required "task_id" slash command option
        OptionMapping option = event.getOption("task_id");

        // Missing task ID → return a consistent error message
        if (option == null) {
            return taskEmbed.createMissingParametersEmbed(user, "No task ID provided");
        }

        int taskID = option.getAsInt();

        // Fetch the task associated with the user and given ID
        Task task = taskService.getUserTask(user, taskID);

        // If no task exists with that ID → return an error embed
        if (task == null) {
            return taskEmbed.createErrorEmbed(
                    user,
                    "No task found"
            );
        }

        // Execute user-provided action on the retrieved task
        return action.apply(task);
    }
}
