package mist.mystralix.Listeners.CommandListener.SlashCommands;

import mist.mystralix.Enums.TaskStatus;
import mist.mystralix.Exception.FileException;
import mist.mystralix.ExternalFileHandler.FileHandler;
import mist.mystralix.Listeners.CommandListener.SlashCommand;
import mist.mystralix.Objects.Task;
import mist.mystralix.Objects.TaskHandler;
import mist.mystralix.Objects.UserCounter;
import mist.mystralix.Manager.UserCounterManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.io.File;
import java.io.IOException;

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
        TaskHandler taskHandler = new TaskHandler();
        FileHandler fileHandler = new FileHandler();

        try {
            UserCounterManager userCounterManager = new UserCounterManager(fileHandler);
            UserCounter userCounter = userCounterManager.getUserCounter(taskUser.getId());

            Task newTask = new Task(
                    userCounter.counter,
                    taskTitle,
                    taskDescription,
                    TaskStatus.INPROGRESS
            );

            File file = fileHandler.getUserTaskFile(taskUser);
            taskHandler.setUserTasks(
                    file,
                    newTask,
                    taskUser,
                    userCounterManager
            );
        } catch (IOException | FileException e) {
            System.out.println(e.getMessage());
            // TODO: Update error (potentially creating a class for visually appealing error reports
            event.getHook().editOriginal("Error! Please try again later.").queue();
            return;
        }

        // TODO: Update visually
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Task Added");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.addField(
                "Title",
                taskTitle,
                true
        );
        embedBuilder.addField(
                "Description",
                taskDescription,
                false
        );
        embedBuilder.setFooter(
                taskUser.getEffectiveName() + " | This task has been added to your task list.",
                taskUser.getEffectiveAvatarUrl()
        );

        event.replyEmbeds(embedBuilder.build()).queue();
    }

}