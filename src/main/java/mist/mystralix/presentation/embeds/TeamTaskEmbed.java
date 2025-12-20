package mist.mystralix.presentation.embeds;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class TeamTaskEmbed implements IMessageEmbedBuilder {

    @Override
    public <T> MessageEmbed createMessageEmbed(User user, String title, T object) {
        return null;
    }

    @Override
    public MessageEmbed createListEmbed(User user, ArrayList<?> list) {
        return null;
    }

    @Override
    public MessageEmbed createErrorEmbed(User user, String message) {
        return null;
    }

    @Override
    public MessageEmbed createMissingParametersEmbed(User user, String message) {
        return null;
    }
}