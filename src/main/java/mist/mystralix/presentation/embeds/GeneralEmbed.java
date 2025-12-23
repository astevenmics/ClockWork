package mist.mystralix.presentation.embeds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class GeneralEmbed {

    public static MessageEmbed createErrorEmbed(String errorMessage) {
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Error")
                .setDescription(errorMessage)
                .build();
    }

}
