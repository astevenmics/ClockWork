package mist.mystralix.Listeners.CommandListener.CommandObjects;

import mist.mystralix.Enums.TaskStatus;
import mist.mystralix.Objects.CustomEmbed;
import mist.mystralix.Objects.Task;
import mist.mystralix.Objects.TaskDAO;
import mist.mystralix.Objects.TaskHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskFunctions {

    public MessageEmbed addTask(SlashCommandInteraction event) {
        OptionMapping title = event.getOption("title");
        OptionMapping description = event.getOption("description");

        if (title == null || description == null) { return null; }

        String taskTitle = title.getAsString();
        String taskDescription = description.getAsString();

        User taskUser = event.getUser();

        TaskDAO newTask = new TaskDAO(
                taskTitle,
                taskDescription,
                TaskStatus.INPROGRESS
        );

        /*
            Creates a new UUID object/value for the first placeholder as a unique identifier
        */
        UUID taskUUID = UUID.randomUUID();
        /*
            Converts taskUUID into a string to store in the uuid String placeholder in the table
        */
        String taskUUIDAsString = taskUUID.toString();

        TaskHandler taskHandler = new TaskHandler();
        taskHandler.addTask(newTask, taskUser, taskUUIDAsString);

        String embedTitle = "New Task";
        Task newlyCreatedTask = taskHandler.getUserTask(taskUUIDAsString);

        return CustomEmbed.createTaskEmbed(
                taskUser,
                embedTitle,
                newlyCreatedTask
        );

    }

    public MessageEmbed cancelTask(SlashCommandInteraction event) {
        User user = event.getUser();

        OptionMapping option = event.getOption("task_id");
        if (option == null) { return null; }
        int taskID = option.getAsInt();

        TaskHandler taskHandler = new TaskHandler();

        Task taskToCancel = taskHandler.getUserTask(user, taskID);
        if(taskToCancel == null) {
            return createErrorEmbed(user, "No task found");
        }

        TaskDAO taskDAO = taskToCancel.taskDAO;
        taskDAO.taskStatus = TaskStatus.CANCELLED;

        taskHandler.updateUserTask(user, taskID, taskToCancel);

        String title = "Cancelled Task";

        return CustomEmbed.createTaskEmbed(
                user,
                title,
                taskToCancel
        );

    }

    public MessageEmbed deleteTask(SlashCommandInteraction event) {
        User user = event.getUser();

        OptionMapping option = event.getOption("task_id");
        if (option == null) { return null; }
        int taskID = option.getAsInt();

        TaskHandler  taskHandler = new TaskHandler();

        Task taskToDelete = taskHandler.getUserTask(user, taskID);

        if(taskToDelete == null) {
            return createErrorEmbed(user, "No task found");
        }

        taskHandler.deleteUserTask(user, taskToDelete);

        String title = "Deleted Task";

        return CustomEmbed.createTaskEmbed(
                user,
                title,
                taskToDelete
        );

    }

    public MessageEmbed listTasks(SlashCommandInteraction event) {
        User user = event.getUser();

        int option = event.getOption("type",
                () -> 1,
                OptionMapping::getAsInt
        );

        TaskStatus selectedTaskStatus = TaskStatus.getTaskStatus(option);

        TaskHandler taskHandler = new TaskHandler();
        ArrayList<Task> allTasks = taskHandler.getUserTasks(user);

        // TODO: Update embed formatting
        if(allTasks.isEmpty()) {
            return createErrorEmbed(user, "You currently do not have any tasks! Use the AddTask command to start.");
        }

            ArrayList<Task> userTasksList = allTasks.stream()
                    .filter(task -> task
                            .taskDAO
                            .taskStatus
                            .equals(selectedTaskStatus))
                    .collect(Collectors.toCollection(ArrayList::new));

            if(userTasksList.isEmpty() && selectedTaskStatus != TaskStatus.ALL) {
                return createErrorEmbed(user, "No tasks found that have the status of " + selectedTaskStatus.getStringValue());
            }

        EmbedBuilder embedBuilder = getEmbedBuilder(selectedTaskStatus, allTasks, userTasksList);
        embedBuilder.setFooter(
                    user.getEffectiveName() + " | Task Lists",
                    user.getEffectiveAvatarUrl()
            );

        return embedBuilder.build();

    }

    public MessageEmbed updateTask(SlashCommandInteraction event) {
        TaskHandler taskHandler = new TaskHandler();
        User user = event.getUser();

        /*
            - Gets the taskID number the user wants to update
            - It is going to be at least a number, in any case, as it was set to be a required option for the command
            - In case it passes through as null, it will be set as 0
        */
        int taskID = event.getOption("id",
                () -> 0,
                OptionMapping::getAsInt
        );

        /*
            - Gets the Task the user specifies (taskID) by checking the user's list of tasks
            - If existing, returns a Task object
            - If not, returns null
        */
        Task taskToUpdate = taskHandler.getUserTask(user, taskID);

        /*
            - Checks early on if the task the user wants to update is on their lists of tasks
            - If taskToUpdate is null, ends the event with a reply to the user
        */
        if(taskToUpdate == null) {
            return createErrorEmbed(user, "No task found");
        }

        /*
            - Gets the title option containing the new title the user wants the task to have
            - In case it passes through as null, it will be set as null
        */
        String newTitle = event.getOption("title",
                () -> null,
                OptionMapping::getAsString
        );

        /*
            - Gets the description option containing the new description the user wants the task to have
            - In case it passes through as null, it will be set as null
        */
        String newDescription = event.getOption("description",
                () -> null,
                OptionMapping::getAsString
        );

        /*
            - Gets the taskStatusType number the user wants the task to be set as
            - It is going to be at least a number, in any case, as it was set to be a required option for the command
            - In case it passes through as null, it will be set as 0
            - 0 is not a TaskStatus, therefore will return null
        */
        int taskStatusOption = event.getOption("type",
                () -> 0,
                OptionMapping::getAsInt
        );

        /*
            - Gets the TaskStatus the user requests
            - If taskStatusOption is 0, it will return null
            - If the TaskStatus is null, no update on the task's TaskStatus will happen
        */
        TaskStatus newTaskStatus = TaskStatus.getTaskStatus(taskStatusOption);

        /*
            - Sets the TaskDAO object in the Task object as a TaskDAO object for readability
        */
        TaskDAO taskToUpdateDAO = taskToUpdate.taskDAO;

        /*
            - Checks if the newTitle, newDescription, or newTaskStatus is present or null
            - If present, replaces the existing content of its respective field
            - If null, does not replace the existing content inside the TaskDAO
        */
        Optional.ofNullable(newTitle).ifPresent(t -> taskToUpdateDAO.title = t);
        Optional.ofNullable(newDescription).ifPresent(d -> taskToUpdateDAO.description = d);
        Optional.ofNullable(newTaskStatus).ifPresent(s -> taskToUpdateDAO.taskStatus = s);

        /*
            - Pushes the updates into the database
            - Uses taskID despite it already being in the taskToUpdate Task object for readability
        */
        taskHandler.updateUserTask(user, taskID, taskToUpdate);

        String title = "Updated Task";
        return CustomEmbed.createTaskEmbed(
                user,
                title,
                taskToUpdate
        );

    }

    public MessageEmbed viewTask(SlashCommandInteraction event) {

        User user = event.getUser();

        OptionMapping option = event.getOption("task_id");
        if (option == null) { return null; }
        int taskID = option.getAsInt();

        TaskHandler  taskHandler = new TaskHandler();

        Task taskToView = taskHandler.getUserTask(user, taskID);

        if(taskToView == null) {
            return createErrorEmbed(user, "No task found");
        }

        String title = "Viewing";

        return CustomEmbed.createTaskEmbed(
                user,
                title,
                taskToView
        );

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

    @NotNull
    public static MessageEmbed createErrorEmbed(
            User user,
            String errorMessage
    ) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Error | Task");
        embedBuilder.setColor(Color.RED);
        embedBuilder.setDescription(errorMessage);
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Task",
                user.getEffectiveAvatarUrl()
        );

        return embedBuilder.build();
    }
}