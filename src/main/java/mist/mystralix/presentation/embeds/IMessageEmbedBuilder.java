package mist.mystralix.presentation.embeds;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public interface IMessageEmbedBuilder {

    <T> MessageEmbed createMessageEmbed(User user, String title, T object);

    MessageEmbed createListEmbed(User user, ArrayList<?> list);

    MessageEmbed createErrorEmbed(User user, String message);

    MessageEmbed createMissingParametersEmbed(User user, String message);

}