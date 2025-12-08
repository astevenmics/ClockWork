package mist.mystralix.presentation.commands.slash;

import mist.mystralix.presentation.commands.slash.reminder.ReminderCommand;
import mist.mystralix.presentation.commands.slash.task.TaskCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

/**
 * Represents a top-level Discord slash command definition.
 *
 * <p>This interface defines the minimal contract required for any slash command
 * in the system (e.g., {@code /task}, {@code /reminder}).</p>
 *
 * <p>A SlashCommand implementation is responsible for:</p>
 * <ul>
 *     <li>providing its command name</li>
 *     <li>defining a user-facing description</li>
 *     <li>registering one or more subcommands</li>
 *     <li>executing behavior when a command is invoked</li>
 * </ul>
 *
 * <p>This interface is marked as {@code sealed} to limit valid implementations
 * to known, controlled classes such as {@link TaskCommand} and {@link ReminderCommand}.</p>
 */
public interface SlashCommand {

    /**
     * Returns the primary name of the slash command.
     *
     * <p>Example: {@code "task"} → becomes the root command {@code /task}.</p>
     *
     * @return the command's name in lowercase
     */
    String getName();

    /**
     * Returns a short human-readable description of the command.
     *
     * <p>This text is shown in Discord's UI when users browse slash commands.</p>
     *
     * @return a brief description of the command's purpose
     */
    String getDescription();

    /**
     * Provides all subcommands associated with this command.
     *
     * <p>Each {@link SubcommandData} defines:</p>
     * <ul>
     *     <li>the subcommand name</li>
     *     <li>a description</li>
     *     <li>the input options the command accepts</li>
     * </ul>
     *
     * <p>Example: a {@code /task} command may expose:
     * <pre>
     * add, delete, update, view, list
     * </pre></p>
     *
     * @return an array of {@link SubcommandData} describing available subcommands
     */
    SubcommandData[] getSubcommands();

    default SubcommandGroupData[] getSubcommandGroupData() {
        return new SubcommandGroupData[0];
    }

    /**
     * Executes the command logic when Discord triggers the slash command.
     *
     * <p>This method is invoked after JDA identifies which root command and
     * subcommand were used. Implementations typically:</p>
     *
     * <ul>
     *     <li>read user options</li>
     *     <li>delegate to a subcommand handler</li>
     *     <li>return an initial reply or an embed</li>
     * </ul>
     *
     * @param event the Discord interaction event context
     */
    void execute(SlashCommandInteraction event);
}