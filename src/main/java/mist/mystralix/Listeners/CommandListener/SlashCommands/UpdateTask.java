package mist.mystralix.Listeners.CommandListener.SlashCommands;

import mist.mystralix.Enums.TaskStatus;
import mist.mystralix.Listeners.CommandListener.SlashCommand;
import mist.mystralix.Objects.CustomEmbed;
import mist.mystralix.Objects.Task;
import mist.mystralix.Objects.TaskDAO;
import mist.mystralix.Objects.TaskHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Optional;

public class UpdateTask implements SlashCommand {

    /*
        Command Name
            - updatetask
            - Usage: /updatetask [taskID] <Title | Description | TaskStatus>
            - String title
            - String description
            - TaskStatus taskStatus
    */
    @Override
    public String getName() {
        return "updatetask";
    }

    /* Command Description */
    @Override
    public String getDescription() {
        return "Updates information: title, description, or task status, of an existing task. ";
    }

    /*
        Command Options
            - Provides the option to get either all the tasks or all the tasks of a specific TaskStatus
            - taskID        | A unique identifier for a task.
                            | TaskIDs are accessible upon making the task and in viewing all the tasks
            - Title         | A brief title for the task
            - Description   | Description of what the task is going to be, have, and such.
            - TaskStatus    | An indicator of the task's progress.
    */
    @Override
    public OptionData[] getOptions() {
        return new OptionData[]{
                new OptionData(
                        OptionType.INTEGER,
                        "id",
                        "Task ID of the task to update.",
                        true
                ),
                new OptionData(
                        OptionType.STRING,
                        "title",
                        "New title for the task.",
                        false
                ).setRequiredLength(1, 32),
                new OptionData(
                        OptionType.STRING,
                        "description",
                        "New description for the task.",
                        false
                ).setRequiredLength(1, 256),
                new OptionData(
                        OptionType.INTEGER,
                        "type",
                        "List all tasks of a certain type.",
                        false
                )
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
            event.reply("No task found.").queue();
            return;
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
        MessageEmbed embed = CustomEmbed.createTaskEmbed(
                user,
                title,
                taskToUpdate
        );

        event.replyEmbeds(embed).queue();
    }

}