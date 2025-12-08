package mist.mystralix.presentation.embeds;

import mist.mystralix.domain.team.Team;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.ArrayList;

public class TeamEmbed implements IMessageEmbedBuilder {
    @Override
    public <T> MessageEmbed createMessageEmbed(User user, String title, T object) {
        if (!(object instanceof Team team)) return null;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title + " | Team #" + team.getId());
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setDescription(
                "Name: " + team.getTeamName() + "\n"
        );
        embedBuilder.setFooter(
                user.getEffectiveName() + " | Team",
                user.getEffectiveAvatarUrl()
        );

        return embedBuilder.build();
    }

    @Override
    public MessageEmbed createListEmbed(User user, ArrayList<?> list) {
        return null;
    }

    @Override
    public MessageEmbed createErrorEmbed(User user, String message) {
        return null;
    }

    @Override
    public MessageEmbed createMissingParametersEmbed(User user, String message) {
        return null;
    }
}