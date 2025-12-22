package mist.mystralix.presentation.embeds;

import mist.mystralix.application.loops.Loops;
import mist.mystralix.domain.team.Team;
import mist.mystralix.utils.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

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
            return new EmbedBuilder()
                    .setTitle("No Teams Found")
                    .setDescription("You are not currently in any teams!")
                    .setFooter(
                            "Type /team to view all available commands!",
                            user.getEffectiveAvatarUrl()
                    )
                    .build();
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Teams");
        embed.setColor(Color.WHITE);

        // TODO: Pagination support for large task lists
        for (Object obj : list) {
            if (!(obj instanceof Team team)) continue;

            int teamUserCount =
                    team.getModerators().size() +           // Team Members
                            team.getMembers().size() +      // Team Moderators
                            1;                              // Team Leader

            embed.addField(
                    "Team #" + team.getId(),
                    String.format(
                            """
                            Name: **%s**
                            User Count: **%d**
                            """,
                            team.getTeamName(),
                            teamUserCount
                    ), true
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

    public MessageEmbed createRemovedMemberEmbed(
            User user,
            User userRemoved,
            Team team
    ) {

        int teamUserCount =
                team.getModerators().size() +   // Team Members
                team.getMembers().size() +      // Team Moderators
                1;                              // Team Leader

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Team #" + team.getId());
        embed.setColor(Color.RED);
        embed.setDescription(
                "User: " + userRemoved.getAsMention()
                        + ", has been removed from the **"
                        + team.getTeamName() + "** team by "
                        + user.getAsMention()
        );
        embed.setFooter(
                "Team User Count: " + teamUserCount + " | Teams",
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
                + "**/team invitation " + team.getId() + " Accept** in the server to accept.\n"
                + "**/team invitation " + team.getId() + " Reject** in the server to reject."
        );
        embed.setFooter(
                userToAdd.getEffectiveName() + " | Team Invitation",
                userToAdd.getEffectiveAvatarUrl()
        );

        return embed.build();
    }

    public MessageEmbed invitationAcceptedEmbed(
            User user,
            Team team
    ) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Team Invitation Accepted | Team #" + team.getId());
        embed.setColor(Color.WHITE);
        embed.setDescription(
                "You have accepted the team invitation from **" + team.getTeamName() + "**"
                        + "!\n\nTo view more information about the team:\n"
                        + "**/team view " + team.getId() + "**"
        );
        embed.setFooter(
                user.getEffectiveName() + " | Team Invitation Accepted",
                user.getEffectiveAvatarUrl()
        );

        return embed.build();
    }

    public MessageEmbed invitationRejectedEmbed(
            User user,
            Team team
    ) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Team Invitation Rejected | Team #" + team.getId());
        embed.setColor(Color.ORANGE);
        embed.setDescription("You have rejected the team invitation from **" + team.getTeamName() + "**");
        embed.setFooter(
                user.getEffectiveName() + " | Team Invitation Rejected",
                user.getEffectiveAvatarUrl()
        );

        return embed.build();
    }

    public MessageEmbed createLeftTeamEmbed(
            User user,
            Team team
    ) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Team Leave  | Team #" + team.getId());
        embed.setColor(Color.RED);
        embed.setDescription("You have successfully left the **" + team.getTeamName() + "** team.");
        embed.setFooter(
                user.getEffectiveName() + " | User Left Team",
                user.getEffectiveAvatarUrl()
        );

        return embed.build();
    }

    public MessageEmbed createTeamInfoEmbed(
            User user,
            Team team,
            SlashCommandInteraction event
    ) {

        Guild serverGuild = event.getGuild();
        if(serverGuild == null) {
            return createErrorEmbed(
                    user,
                    "An error has occurred. Please try again."
            );
        }

        StringBuilder teamModerators = Loops.createTeamUsersStringBuilder(
                serverGuild,
                team.getModerators(),
                Constants.TEAM_NO_MODERATORS.getValue(String.class)
        );
        StringBuilder teamMembers = Loops.createTeamUsersStringBuilder(
                serverGuild,
                team.getMembers(),
                Constants.TEAM_NO_MEMBERS.getValue(String.class)
        );

        Member teamLeader = serverGuild.getMember(User.fromId(team.getTeamLeader()));
        String teamLeaderMention = teamLeader != null ? teamLeader.getAsMention() : team.getTeamLeader();

        int teamUserCount =
                team.getModerators().size() +   // Team Members
                team.getMembers().size() +      // Team Moderators
                1;                              // Team Leader

        return new EmbedBuilder()
                .setTitle("Team Info | Team #" + team.getId())
                .setColor(Color.GREEN)
                .setDescription(
                        String.format(
                            """
                            **Name**: %s
                            **Leader**: %s
                            **Moderators**: %s
                            **Members**: %s
                            **User Count**: %d
                            """,
                            team.getTeamName(),
                            teamLeaderMention,
                            teamModerators,
                            teamMembers,
                            teamUserCount
                        )
                )
                .setFooter(
                        user.getEffectiveName() + " | Team Info",
                        user.getEffectiveAvatarUrl()
                )
                .build();
    }

    @Override
    public MessageEmbed createErrorEmbed(User user, String message) {
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Error | Team")
                .setDescription(message)
                .setFooter(user.getEffectiveName() + " | Team Error", user.getEffectiveAvatarUrl())
                .build();
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

    public MessageEmbed createPositionUpdateEmbed(
            User user,
            User userUpdated,
            Team team,
            boolean isPromote
    ) {
        String roleChange = isPromote ? "promoted" : "demoted";
        String previousRole = isPromote ? "Member" : "Moderator";
        String newRole = isPromote ? "Moderator" : "Member";
        return new EmbedBuilder()
                .setColor(isPromote ? Color.GREEN : Color.RED)
                .setTitle("Position Update | Team #" + team.getId())
                .setDescription(
                        String.format(
                                """
                                        %s has been %s in **%s** team by %s
                                        *Previous Role*: **%s** | *New Role*: **%s**
                                        """,
                                userUpdated.getAsMention(),
                                roleChange,
                                team.getTeamName(),
                                user.getAsMention(),
                                previousRole,
                                newRole
                        )
                )
                .build();
    }

    public MessageEmbed createTeamNameUpdateEmbed(
            User user,
            Team team,
            String previousName
    ) {
        return new EmbedBuilder()
                .setColor(Color.PINK)
                .setTitle("Team Name Update | Team #" + team.getId())
                .setDescription(
                        String.format(
                                """
                                        You have successfully changed the team name.
                                        *Previous Name*: **%s**
                                        *New Name*: **%s**
                                        """,
                                previousName,
                                team.getTeamName()
                        )
                )
                .setFooter("Team Name changed by: " + user.getEffectiveName(), user.getEffectiveAvatarUrl())
                .build();
    }

    public MessageEmbed createTeamTransferredEmbed(
            User user,
            User userMentioned,
            Team team
    ) {
        return new EmbedBuilder()
                .setColor(Color.PINK)
                .setTitle("Team Transfer | Team #" + team.getId())
                .setDescription(
                        String.format(
                                """
                                        You have successfully transferred **%s** team to %s.
                                        Your current role in the team now is **Moderator**
                                        """,
                                team.getTeamName(),
                                userMentioned.getAsMention()
                        ))
                .setFooter("Team Transfer changed by: " + user.getEffectiveName(), user.getEffectiveAvatarUrl())
                .build();
    }

}