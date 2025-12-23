package mist.mystralix.application.validator;

import mist.mystralix.presentation.embeds.IMessageEmbedBuilder;
import mist.mystralix.utils.IdentifiableFetcher;
import mist.mystralix.utils.messages.CommonMessages;
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
            String objectName,
            Function<T, MessageEmbed> action
    ) {
        User user = event.getUser();

        OptionMapping option = event.getOption(idOptionName);

        if (option == null) {
            return embedBuilder.createMissingParametersEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        int objectID = option.getAsInt();

        T object = service.getById(objectID);

        if (object == null) {
            return embedBuilder.createErrorEmbed(user,
                    String.format(
                            CommonMessages.OBJECT_NOT_FOUND,
                            objectName
                    )
            );
        }

        return action.apply(object);
    }

}