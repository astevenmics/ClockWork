package mist.mystralix.Listeners.CommandListener.SlashCommands;

import mist.mystralix.Listeners.CommandListener.SlashCommand;
import mist.mystralix.Objects.CustomEmbed;
import mist.mystralix.Objects.Task;
import mist.mystralix.Objects.TaskHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class DeleteTask implements SlashCommand {

    /*
        Command Name
            - deletetask
            - Usage: /deletetask [taskID]
    */
    @Override
    public String getName() {
        return "deletetask";
    }

    /* Command Description */
    @Override
    public String getDescription() {
        return "Deletes a specific task selected using its task ID";
    }

    /*
        Command Options
            - Requires a taskID
            - taskID        | A unique identifier for a task.
                            | TaskIDs are accessible upon making the task and in viewing all the tasks
    */
    @Override
    public OptionData[] getOptions() {
        return new OptionData[]{
                new OptionData(
                        OptionType.INTEGER,
                        "task_id",
                        "ID number of the task to view.",
                        true
                )
        };
    }

    /* Command Event/Output */
    @Override
    public void execute(SlashCommandInteraction event) {

        User user = event.getUser();

        OptionMapping option = event.getOption("task_id");
        if (option == null) { return; }
        int taskID = option.getAsInt();

        TaskHandler  taskHandler = new TaskHandler();

        Task taskToDelete = taskHandler.getUserTask(user, taskID);

        if(taskToDelete == null) {
            event.reply("No task found.").queue();
            return;
        }

        taskHandler.deleteUserTask(user, taskToDelete);

        String title = "Deleted Task";

        MessageEmbed embed = CustomEmbed.createTaskEmbed(
                user,
                title,
                taskToDelete
        );

        event.replyEmbeds(embed).queue();

    }

}