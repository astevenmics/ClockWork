package mist.mystralix.Listeners.MessageListeners;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Simple message logger used for debugging or administrative monitoring.
 *
 * <p>This listener prints all received messages (excluding bot messages)
 * to the console. It supports both private messages (DMs) and guild messages.</p>
 *
 * <p><strong>Note:</strong> This class should not be used for production-level
 * logging without sanitization or proper log rotation.</p>
 */
public class MessageLogger extends ListenerAdapter {

    /**
     * Triggered whenever a message is received in any channel the bot can see.
     *
     * <p>This method logs messages to the console while applying the following rules:</p>
     * <ul>
     *     <li>Ignores messages from bots to prevent log spam or recursion.</li>
     *     <li>Ignores messages where the member is null (e.g., system messages).</li>
     *     <li>Formats output differently for:
     *         <ul>
     *             <li>Private Messages (DMs)</li>
     *             <li>Guild channels (including channel/guild names)</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param event the JDA message event containing message and user metadata
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        // Prevent logging messages from bots or system events
        // event.getMember() == null occurs for DMs, webhooks, and system messages
        if (event.getAuthor().isBot() || event.getMember() == null) {
            return;
        }

        // DM (Private Message)
        if (event.isFromType(ChannelType.PRIVATE)) {
            System.out.printf(
                    "[PM] %s: %s%n",
                    event.getAuthor().getName(),
                    event.getMessage().getContentDisplay()
            );
        }

        // Guild text channel
        else {
            System.out.printf(
                    "[%s][%s] %s: %s%n",
                    event.getGuild().getName(),
                    event.getChannel().getName(),
                    event.getMember().getEffectiveName(),
                    event.getMessage().getContentDisplay()
            );
        }
    }
}
