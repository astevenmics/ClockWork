package mist.mystralix.Listeners.MessageListeners;

import mist.mystralix.Exception.FileException;
import mist.mystralix.ExternalFileHandler.FileHandler;
import mist.mystralix.ExternalFileHandler.JSONHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class MessageFilter extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String eventMessage = event.getMessage().getContentDisplay();
        String censorWordsFileName = "\\censored_words.json";

        if((event.getAuthor().isBot()) || (event.getMember() == null)) { return; }

        File file;
        FileHandler fileHandler = new FileHandler();
        HashSet<String> censoredWords;
        JSONHandler jsonHandler = new JSONHandler();
        try {
            file = fileHandler.getFile(censorWordsFileName, true);
            censoredWords = jsonHandler.getFileContentsHashSet(file, String.class);
        } catch (IOException | FileException e) {
            System.out.println(e.getMessage());
            return;
        }

        if(censoredWords.contains(eventMessage)) {
            event.getMessage().delete().queue();
        }
    }
}