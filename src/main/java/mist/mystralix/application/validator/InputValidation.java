package mist.mystralix.application.validator;

import mist.mystralix.presentation.embeds.IMessageEmbedBuilder;
import mist.mystralix.utils.Constants;
import mist.mystralix.utils.IdentifiableFetcher;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.function.Function;

public class InputValidation {

    public static <T> MessageEmbed validate(
            SlashCommandInteraction event,
            IdentifiableFetcher<T> service,
            IMessageEmbedBuilder embedBuilder,
            String idOptionName,
            Function<T, MessageEmbed> action
    ) {
        User user = event.getUser();
        String userDiscordID = user.getId();

        OptionMapping option = event.getOption(idOptionName);

        if (option == null) {
            return embedBuilder.createMissingParametersEmbed(
                    user,
                    Constants.MISSING_PARAMETERS.getValue(String.class)
            );
        }

        int objectID = option.getAsInt();

        T object = service.fetchByUserIDAndObjectID(userDiscordID, objectID);

        if (object == null) {
            return embedBuilder.createErrorEmbed(
                    user,
                    String.format(
                            Constants.NO_OBJECT_FOUND.getValue(String.class),
                            idOptionName,
                            objectID
                    )
            );
        }

        return action.apply(object);
    }

}