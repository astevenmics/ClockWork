package mist.mystralix.presentation.commands.slash;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

/**
 * Defines a unified CRUD contract for Discord slash command handlers.
 *
 * <p>This interface standardizes how command groups (e.g., /task, /reminder)
 * implement Create, Read, Update, Delete, and List operations.</p>
 *
 * <p>Each method receives a {@link SlashCommandInteraction} event and must
 * return a {@link MessageEmbed} that will be sent as the command response.</p>
 *
 * <p>Using this interface ensures:</p>
 * <ul>
 *     <li>Consistent structure across all slash command modules</li>
 *     <li>Cleaner grouping of related subcommands</li>
 *     <li>Improved maintainability by enforcing a shared API</li>
 * </ul>
 */
public interface ISlashCommandCRUD {

    /**
     * Handles the "create" operation.
     *
     * <p>Used when a new entity should be created (e.g., a task or reminder).</p>
     *
     * @param event The slash command invocation context.
     * @return A {@link MessageEmbed} describing the created object or an error.
     */
    MessageEmbed create(SlashCommandInteraction event);

    /**
     * Handles retrieval of a single entity.
     *
     * <p>Typically used for subcommands like <code>/task view</code> or
     * <code>/reminder view</code>. The event usually requires an ID option.</p>
     *
     * @param event The slash command invocation context.
     * @return A {@link MessageEmbed} displaying the entity or an error embed.
     */
    MessageEmbed read(SlashCommandInteraction event);

    /**
     * Handles modification of an existing entity.
     *
     * <p>Used for subcommands like <code>/task update</code> or
     * <code>/reminder update</code>.</p>
     *
     * @param event The slash command invocation context.
     * @return A {@link MessageEmbed} describing the updated entity or an error.
     */
    MessageEmbed update(SlashCommandInteraction event);

    /**
     * Handles deletion of a single entity.
     *
     * <p>Used for subcommands like <code>/task delete</code> or
     * <code>/reminder delete</code>.</p>
     *
     * @param event The slash command invocation context.
     * @return A {@link MessageEmbed} confirming the deletion or an error embed.
     */
    MessageEmbed delete(SlashCommandInteraction event);

    /**
     * Retrieves all entities belonging to the user.
     *
     * <p>Used for commands like <code>/task list</code> or
     * <code>/reminder list</code>.</p>
     *
     * @param event The slash command invocation context.
     * @return A {@link MessageEmbed} listing all entities, or an empty-state embed.
     */
    MessageEmbed readAll(SlashCommandInteraction event);

}
