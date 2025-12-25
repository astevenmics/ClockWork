package mist.mystralix.presentation.listeners;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class MessageReceivedHandler extends ListenerAdapter {

    private static final Set<String> CENSORED_WORDS = new HashSet<>();
    private static boolean loaded = false;

    private void loadCensoredWords() {
        if (loaded) return;

        Path baseDir = Paths.get(System.getProperty("user.dir"));
        Path filePath = baseDir.resolve("censored_words.txt");

        if (!Files.exists(filePath)) {
            System.err.println("MessageFilter Error: File not found at " + filePath);
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    CENSORED_WORDS.add(line.toLowerCase().trim());
                }
            }
            loaded = true;
        } catch (IOException e) {
            System.err.println("MessageFilter Error: " + e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        loadCensoredWords();
        if (CENSORED_WORDS.isEmpty()) return;

        String message = event.getMessage()
                .getContentRaw()
                .toLowerCase();

        for (String word : message.split("\\s+")) {
            word = word.replaceAll("[^a-z0-9]", "");
            if (CENSORED_WORDS.contains(word)) {
                event.getMessage().delete().queue();
                return;
            }
        }

        if (event.isFromType(ChannelType.PRIVATE)) {
            System.out.printf(
                    "[PM] %s: %s%n",
                    event.getAuthor().getName(),
                    event.getMessage().getContentDisplay()
            );
        } else if (event.getMember() != null) {
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
