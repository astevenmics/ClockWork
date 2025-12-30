package mist.mystralix.application.validator;

import mist.mystralix.presentation.embeds.IMessageEmbedBuilder;
import mist.mystralix.utils.messages.UserMessages;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class UserValidator {

    // Checks if the user mentioned is a bot
    public static MessageEmbed validateUserBot(
            User user,
            User userMentioned,
            IMessageEmbedBuilder embedBuilder
    ) {
        if (userMentioned.isBot()) {
            return embedBuilder.createErrorEmbed(user, UserMessages.CANNOT_MENTION_BOT);
        }
        return null;
    }
}
