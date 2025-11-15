package mist.mystralix.Listeners.CommandListener.SlashCommands;

import mist.mystralix.Enums.TaskStatus;
import mist.mystralix.Listeners.CommandListener.SlashCommand;
import mist.mystralix.Objects.CustomEmbed;
import mist.mystralix.Objects.Task;
import mist.mystralix.Objects.TaskDAO;
import mist.mystralix.Objects.TaskHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.UUID;

public class AddTask implements SlashCommand {

    /* Command Name */
    /* /addtask */
    @Override
    public String getName() {
        return "addtask";
    }

    /* Command Description */
    @Override
    public String getDescription() {
        return "Creating tasks/adding tasks to your task list.";
    }

    /* Command Options */
    @Override
    public OptionData[] getOptions() {
        return new OptionData[]{
                new OptionData(
                        OptionType.STRING,
                        "title",
                        "A brief title for the task.",
                        true
                ).setRequiredLength(1, 32),
                new OptionData(
                        OptionType.STRING,
                        "description",
                        "A brief description for the task.",
                        true
                ).setRequiredLength(1, 256),
        };
    }

    /* Command Event/Output */
    @Override
    public void execute(SlashCommandInteraction event) {

        OptionMapping title = event.getOption("title");
        OptionMapping description = event.getOption("description");

        if (title == null || description == null) { return; }

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

        MessageEmbed embed = CustomEmbed.createTaskEmbed(
                taskUser,
                embedTitle,
                newlyCreatedTask
        );

        event.replyEmbeds(embed).queue();
    }

}