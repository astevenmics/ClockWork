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

public class ViewTask implements SlashCommand {

    /* Command Name */
    /* /viewtask */
    @Override
    public String getName() {
        return "viewtask";
    }

    /* Command Description */
    @Override
    public String getDescription() {
        return "View a specific task selected using its task ID";
    }

    /* Command Options */
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

        Task taskToView = taskHandler.getUserTask(user, taskID);

        if(taskToView == null) {
            event.reply("No task found.").queue();
            return;
        }

        String title = "Viewing";

        MessageEmbed embed = CustomEmbed.createTaskEmbed(
                user,
                title,
                taskToView
        );

        event.replyEmbeds(embed).queue();

    }

}