package mist.mystralix.presentation.embeds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.ArrayList;

public class TeamTaskEmbed implements IMessageEmbedBuilder {

    @Override
    public <T> MessageEmbed createMessageEmbed(User user, String title, T object) {
        // TODO Update embed
        return new EmbedBuilder()
                .setTitle("Team Task")
                .setDescription(title)
                .setColor(Color.GREEN)
                .setFooter(user.getEffectiveName(), user.getEffectiveAvatarUrl())
                .build();
    }

    @Override
    public MessageEmbed createListEmbed(User user, ArrayList<?> list) {
        return null;
    }

    @Override
    public MessageEmbed createErrorEmbed(User user, String message) {
        return new EmbedBuilder()
                .setTitle("Error Team Task")
                .setDescription(message)
                .setColor(Color.RED)
                .setFooter(user.getEffectiveName(), user.getEffectiveAvatarUrl())
                .build();
    }

    @Override
    public MessageEmbed createMissingParametersEmbed(User user, String message) {
        return null;
    }
}