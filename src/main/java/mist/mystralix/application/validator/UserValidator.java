package mist.mystralix.application.validator;

import mist.mystralix.presentation.embeds.IMessageEmbedBuilder;
import mist.mystralix.utils.Constants;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class UserValidator {

    // Checks if the user mentioned is themselves
    // Checks if the user mentioned is a bot
    public static MessageEmbed validatorUser(
            User user,
            User userMentioned,
            IMessageEmbedBuilder embedBuilder
    ) {
        if (user.equals(userMentioned)) {
            return embedBuilder.createErrorEmbed(user, Constants.USER_MENTIONED_CANNOT_BE_USER.getValue(String.class));
        } else if (userMentioned.isBot()) {
            return embedBuilder.createErrorEmbed(user, Constants.USER_MENTIONED_CANNOT_BE_BOT.getValue(String.class));
        }
        return null;
    }
}
