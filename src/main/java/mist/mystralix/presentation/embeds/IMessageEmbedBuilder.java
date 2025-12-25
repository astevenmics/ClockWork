package mist.mystralix.presentation.embeds;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public interface IMessageEmbedBuilder {

    <T> MessageEmbed createMessageEmbed(User user, String title, T object);

    MessageEmbed createErrorEmbed(User user, String message);

    MessageEmbed createMissingParametersEmbed(User user, String message);

}