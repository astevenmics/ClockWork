package mist.mystralix.Listeners.CommandListener.SlashCommands;

import mist.mystralix.Database.DBHandler;
import mist.mystralix.Enums.TaskStatus;
import mist.mystralix.Listeners.CommandListener.SlashCommand;
import mist.mystralix.Objects.Task;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;

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

        Task newTask = new Task(
                taskTitle,
                taskDescription,
                TaskStatus.INPROGRESS
        );

        DBHandler dbHandler = new DBHandler();
        dbHandler.addTask(newTask, taskUser.getId());

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