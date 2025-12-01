package mist.mystralix.presentation.embeds;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

/**
 * Defines a standardized contract for building Discord embeds used across
 * various command modules (e.g. Task, Reminder).
 *
 * <p>This interface ensures that all embed builders provide a consistent
 * structure for:</p>
 *
 * <ul>
 *     <li>Rendering a single domain object into a formatted embed</li>
 *     <li>Rendering lists of domain objects (e.g. all tasks or reminders)</li>
 *     <li>Rendering error messages</li>
 *     <li>Rendering missing-parameter validation messages</li>
 * </ul>
 *
 * <p>Each concrete implementation (such as {@code TaskEmbed} or
 * {@code ReminderEmbed}) contains logic for styling and formatting the
 * corresponding object type.</p>
 */
public interface IMessageEmbedBuilder {

    /**
     * Builds an embed representing a single domain object.
     *
     * <p>Implementations are expected to validate the type of {@code object},
     * cast it appropriately, and render a formatted embed containing key fields
     * and metadata.</p>
     *
     * @param user   the user invoking the command (used for footers, context, etc.)
     * @param title  the title of the embed
     * @param object the domain object to render (e.g. {@code Task}, {@code Reminder})
     * @param <T>    generic type used for object casting
     * @return a formatted {@link MessageEmbed}, or {@code null} if the type is unsupported
     */
    <T> MessageEmbed createMessageEmbed(User user, String title, T object);

    /**
     * Builds an embed containing multiple objects, typically for list or
     * pagination-based responses.
     *
     * <p>Implementations should assume the list contains a consistent object
     * type and extract relevant fields for each entry.</p>
     *
     * @param user the user invoking the command
     * @param list an {@link ArrayList} of domain objects
     * @return a formatted list embed, or {@code null} if the list is empty or invalid
     */
    MessageEmbed createListEmbed(User user, ArrayList<?> list);

    /**
     * Builds an embed representing an error condition (e.g. invalid input,
     * failed database lookup, or command misuse).
     *
     * @param user    the user invoking the command
     * @param message the error message to display
     * @return an error embed with a standardized style
     */
    MessageEmbed createErrorEmbed(User user, String message);

    /**
     * Builds an embed indicating that required command parameters were missing.
     *
     * <p>This method is commonly triggered when a user omits required slash
     * command options such as {@code task_id}, {@code message}, etc.</p>
     *
     * @param user    the user invoking the command
     * @param message the message explaining the missing parameters
     * @return an embed styled specifically for missing-parameter scenarios
     */
    MessageEmbed createMissingParametersEmbed(User user, String message);

}
