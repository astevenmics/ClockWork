package mist.mystralix.application.helper;

import mist.mystralix.presentation.embeds.IMessageEmbedBuilder;
import mist.mystralix.utils.messages.CommonMessages;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class InputHelper {

    public static MessageEmbed checkInputNull(
            User user,
            IMessageEmbedBuilder embedBuilder,
            String... options
    ) {
        for (String option : options) {
            if (option == null) {
                return embedBuilder.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
            }
        }

        return null;
    }

}
