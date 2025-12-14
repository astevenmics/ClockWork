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
        // Validate list contents
        if (list.isEmpty() || !(list.getFirst() instanceof Team)) {
            return null;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Teams");
        embed.setColor(Color.WHITE);

        // TODO: Pagination support for large task lists
        for (Object obj : list) {
            if (!(obj instanceof Team team)) continue;

            embed.addField(
                    "#" + team.getId() + " | " + team.getTeamName(),
                            "Status: ",
                    true
            );
        }

        embed.setFooter(
                user.getEffectiveName() + " | Task List",
                user.getEffectiveAvatarUrl()
        );

        return embed.build();
    }

    public MessageEmbed createInvitationEmbed(
            User user,
            User userToAdd,
            Team team
    ) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Team #" + team.getId());
        embed.setColor(Color.WHITE);
        embed.setDescription(
                "User: " + userToAdd.getAsMention()
                + ", has successfully been invited into the team.\n"
                + "User can accept or deny the request."
        );
        embed.setFooter(
                user.getEffectiveName() + " | Teams ",
                user.getEffectiveAvatarUrl()
        );

        return embed.build();
    }

    public MessageEmbed createInvitationToUserEmbed(
            User user,
            User userToAdd,
            Team team
    ) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Team Invitation | Team #" + team.getId());
        embed.setColor(Color.WHITE);
        embed.setDescription(
                "You have been invited to be part of the **"
                + team.getTeamName() + "** team by " + user.getAsMention()
                + "!\nYou can either accept or reject the request.\n"
                + "**/team accept " + team.getId() + "** in the server to accept."
                + "**/team reject " + team.getId() + "** in the server to reject."
        );
        embed.setFooter(
                userToAdd.getEffectiveName() + " | Team Invitation",
                userToAdd.getEffectiveAvatarUrl()
        );

        return embed.build();
    }

    @Override
    public MessageEmbed createErrorEmbed(User user, String message) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Error | Team");
        embed.setColor(Color.RED);
        embed.setDescription(message);

        embed.setFooter(
                user.getEffectiveName() + " | Team Error",
                user.getEffectiveAvatarUrl()
        );

        return embed.build();
    }

    @Override
    public MessageEmbed createMissingParametersEmbed(User user, String message) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Team Interaction Incomplete");
        embed.setColor(Color.ORANGE);
        embed.setDescription(message);

        embed.setFooter(
                user.getEffectiveName() + " | Team Lacking Information",
                user.getEffectiveAvatarUrl()
        );

        return embed.build();
    }
}