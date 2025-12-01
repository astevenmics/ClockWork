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

        // Retrieve the required idOptionName slash command option
        OptionMapping option = event.getOption(idOptionName);

        // Missing task ID → return a consistent error message
        if (option == null) {
            return embedBuilder.createMissingParametersEmbed(user, "No " + idOptionName + " provided");
        }

        int objectID = option.getAsInt();

        // Fetch the task associated with the user discord ID and given ID
        T task = service.fetchByUserIDAndObjectID(userDiscordID, objectID);

        // If no task exists with that ID → return an error embed
        if (task == null) {
            return embedBuilder.createErrorEmbed(
                    user,
                    "No object found of " + idOptionName + " with ID " + objectID
            );
        }

        // Execute user-provided action on the retrieved task
        return action.apply(task);
    }

}
