package mist.mystralix.presentation.listeners;

import mist.mystralix.infrastructure.exception.FileException;
import mist.mystralix.infrastructure.file.FileHandler;
import mist.mystralix.infrastructure.file.JSONHandler;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class MessageReceivedHandler extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        String eventMessage = event.getMessage().getContentDisplay();
        String censorWordsFileName = "\\censored_words.json";

        if (event.getAuthor().isBot() || event.getMember() == null) {
            return;
        }

        File file;
        FileHandler fileHandler = new FileHandler();
        JSONHandler jsonHandler = new JSONHandler();
        HashSet<String> censoredWords;

        try {
            file = fileHandler.getFile(censorWordsFileName, true);

            censoredWords = jsonHandler.getFileContentsHashSet(file, String.class);

        } catch (IOException | FileException e) {
            System.out.println("MessageFilter Error: " + e.getMessage());
            return;
        }

        if (censoredWords.contains(eventMessage)) {
            event.getMessage().delete().queue();
        }

        if (event.getAuthor().isBot() || event.getMember() == null) {
            return;
        }

        if (event.isFromType(ChannelType.PRIVATE)) {
            System.out.printf(
                    "[PM] %s: %s%n",
                    event.getAuthor().getName(),
                    event.getMessage().getContentDisplay()
            );
        } else {
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