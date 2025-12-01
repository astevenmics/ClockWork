package mist.mystralix.presentation.listeners;

import mist.mystralix.infrastructure.exception.FileException;
import mist.mystralix.infrastructure.file.FileHandler;
import mist.mystralix.infrastructure.file.JSONHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

/**
 * MessageFilter
 *
 * <p>This listener blocks messages that match any word/phrase inside
 * a JSON file named "censored_words.json".</p>
 *
 * <p>The JSON file is expected to contain a list of strings, which are
 * loaded into a {@link HashSet} to allow fast membership checks.</p>
 *
 * <p>Example JSON structure:</p>
 * <pre>
 * [
 *   "badword1",
 *   "badword2",
 *   "example phrase"
 * ]
 * </pre>
 *
 * <p>If the user's entire message exactly matches any censored entry,
 * the message will be deleted.</p>
 */
public class MessageFilter extends ListenerAdapter {

    /**
     * Called whenever a message is sent in any channel the bot can see.
     *
     * <p>This method performs the following steps:</p>
     * <ul>
     *     <li>Ignore messages from bots (prevents infinite loops)</li>
     *     <li>Load censored words from a JSON file</li>
     *     <li>Check if the message is contained in that set</li>
     *     <li>Delete the message if it matches</li>
     * </ul>
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        String eventMessage = event.getMessage().getContentDisplay();
        String censorWordsFileName = "\\censored_words.json";

        // Skip bot messages and messages not from guild members
        // event.getMember() == null means DM, system, or webhook
        if (event.getAuthor().isBot() || event.getMember() == null) {
            return;
        }

        File file;
        FileHandler fileHandler = new FileHandler();
        JSONHandler jsonHandler = new JSONHandler();
        HashSet<String> censoredWords;

        try {
            // Load or create the JSON file that stores censored words.
            // The FileHandler ensures the file exists if allowCreation is true.
            file = fileHandler.getFile(censorWordsFileName, true);

            // Convert the JSON array into a HashSet for O(1) matching
            censoredWords = jsonHandler.getFileContentsHashSet(file, String.class);

        } catch (IOException | FileException e) {
            // If file I/O fails, do not punish the user—just log and skip filtering.
            System.out.println("MessageFilter Error: " + e.getMessage());
            return;
        }

        // Check if the message is exactly equal to one of the blocked entries.
        // This is an exact match, not case-insensitive and not partial matching.
        if (censoredWords.contains(eventMessage)) {
            event.getMessage().delete().queue();
        }
    }
}
