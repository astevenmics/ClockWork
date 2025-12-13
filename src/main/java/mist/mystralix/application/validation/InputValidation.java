package mist.mystralix.application.validation;

import mist.mystralix.presentation.embeds.IMessageEmbedBuilder;
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
            return embedBuilder.createMissingParametersEmbed(user, "No " + idOptionName + " provided");
        }

        int objectID = option.getAsInt();

        T task = service.fetchByUserIDAndObjectID(userDiscordID, objectID);

        if (task == null) {
            return embedBuilder.createErrorEmbed(
                    user,
                    "No object found of " + idOptionName + " with ID " + objectID
            );
        }

        return action.apply(task);
    }

}