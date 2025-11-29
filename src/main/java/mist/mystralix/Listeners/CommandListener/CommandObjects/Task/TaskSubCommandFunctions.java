package mist.mystralix.Listeners.CommandListener.CommandObjects.Task;

import mist.mystralix.Database.Task.DBTaskRepository;
import mist.mystralix.Enums.TaskStatus;
import mist.mystralix.Listeners.CommandListener.ISlashCommandCRUD;
import mist.mystralix.Objects.Task.Task;
import mist.mystralix.Objects.Task.TaskDAO;
import mist.mystralix.Objects.Task.TaskService;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskSubCommandFunctions implements ISlashCommandCRUD {

    private final TaskService TASK_SERVICE;

    public TaskSubCommandFunctions(TaskService taskService) {
        this.TASK_SERVICE = taskService;
    }

    private final TaskEmbed TASK_EMBED = new TaskEmbed();

    @Override
    public MessageEmbed create(SlashCommandInteraction event) {
        User taskUser = event.getUser();
        OptionMapping title = event.getOption("title");
        OptionMapping description = event.getOption("description");

        if (title == null || description == null) {
            return TASK_EMBED.createMissingParametersEmbed(taskUser, "Neither title nor description were provided");
        }

        String taskTitle = title.getAsString();
        String taskDescription = description.getAsString();



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

        TASK_SERVICE.addTask(newTask, taskUser, taskUUIDAsString);

        String embedTitle = "New Task";
        Task newlyCreatedTask = TASK_SERVICE.getUserTask(taskUUIDAsString);

        return TASK_EMBED.createMessageEmbed(
                taskUser,
                embedTitle,
                newlyCreatedTask
        );

    }

    @Override
    public MessageEmbed delete(SlashCommandInteraction event) {
        return TaskFunctions.handleTask(event, TASK_SERVICE, TASK_EMBED, task -> {
            User user = event.getUser();

            TASK_SERVICE.deleteUserTask(user, task);

            return TASK_EMBED.createMessageEmbed(user, "Deleted Task", task);
        });
    }

    @Override
    public MessageEmbed update(SlashCommandInteraction event) {
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
        Task taskToUpdate = TASK_SERVICE.getUserTask(user, taskID);

        /*
            - Checks early on if the task the user wants to update is on their lists of tasks
            - If taskToUpdate is null, ends the event with a reply to the user
        */
        if(taskToUpdate == null) {
            return TASK_EMBED.createErrorEmbed(user, "No task found");
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
        TASK_SERVICE.updateUserTask(user, taskID, taskToUpdate);

        String title = "Updated Task";
        return TASK_EMBED.createMessageEmbed(
                user,
                title,
                taskToUpdate
        );

    }

    @Override
    public MessageEmbed read(SlashCommandInteraction event) {
        return TaskFunctions.handleTask(event, TASK_SERVICE, TASK_EMBED, task ->
            TASK_EMBED.createMessageEmbed(
                event.getUser(),
                "Viewing",
                task
            )
        );
    }


    public MessageEmbed cancelTask(SlashCommandInteraction event) {
        return TaskFunctions.handleTask(event, TASK_SERVICE, TASK_EMBED, task -> {
            User user = event.getUser();
            TaskService taskService = new TaskService(new DBTaskRepository());

            task.taskDAO.taskStatus = TaskStatus.CANCELLED;
            taskService.updateUserTask(user, task.taskID, task);

            return TASK_EMBED.createMessageEmbed(user, "Cancelled Task", task);
        });
    }

    @Override
    public MessageEmbed readAll(SlashCommandInteraction event) {
        User user = event.getUser();

        int option = event.getOption("type",
                () -> 1,
                OptionMapping::getAsInt
        );

        TaskStatus selectedTaskStatus = TaskStatus.getTaskStatus(option);

        TaskService taskService = new TaskService(new DBTaskRepository());
        ArrayList<Task> tasks = (ArrayList<Task>) taskService.getUserTasks(user);

        if(tasks.isEmpty()) {
            return TASK_EMBED.createErrorEmbed(
                    user,
                    "You currently do not have any tasks! Use the /task add command to start."
            );
        }

        if(selectedTaskStatus != TaskStatus.ALL) {
            tasks = tasks.stream()
                    .filter(task -> task
                            .taskDAO
                            .taskStatus
                            .equals(selectedTaskStatus))
                    .collect(Collectors.toCollection(ArrayList::new));

            if(tasks.isEmpty()) {
                return TASK_EMBED.createErrorEmbed(
                        user,
                        "No tasks found that have the status of " + selectedTaskStatus.getStringValue()
                );
            }
        }

        return TASK_EMBED.createListEmbed(
                user,
                tasks
        );

    }

}