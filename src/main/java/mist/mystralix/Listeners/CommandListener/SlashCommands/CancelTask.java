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

public class CancelTask implements SlashCommand {

    /* Command Name */
    /* /canceltask */
    @Override
    public String getName() {
        return "canceltask";
    }

    /* Command Description */
    @Override
    public String getDescription() {
        return "Updates specified task's status as cancelled.";
    }

    /* Command Options */
    @Override
    public OptionData[] getOptions() {
        return new OptionData[]{
                new OptionData(
                        OptionType.INTEGER,
                        "task_id",
                        "ID number of the task to cancel.",
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

        TaskHandler taskHandler = new TaskHandler();

        Task taskToCancel = taskHandler.getUserTask(user, taskID);
        if(taskToCancel == null) {
            event.reply("No task found.").queue();
            return;
        }

        TaskDAO taskDAO = taskToCancel.taskDAO;
        taskDAO.taskStatus = TaskStatus.CANCELLED;

        taskHandler.cancelUserTask(user, taskID, taskToCancel);

        String title = "Cancelled Task";

        MessageEmbed embed = CustomEmbed.createTaskEmbed(
                user,
                title,
                taskToCancel
                );

        event.replyEmbeds(embed).queue();
    }



}