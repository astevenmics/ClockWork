package mist.mystralix.presentation.embeds;

import mist.mystralix.application.loops.Loops;
import mist.mystralix.application.pagination.PaginationEmbedCreator;
import mist.mystralix.domain.enums.TaskStatus;
import mist.mystralix.domain.task.TeamTask;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TeamTaskEmbed implements IMessageEmbedBuilder, PaginationEmbedCreator {

    @Override
    public <T> MessageEmbed createMessageEmbed(User user, String title, T object) {

        if (!(object instanceof TeamTask teamTask)) {
            return null;
        }

        TaskStatus status = TaskStatus.getTaskStatus(teamTask.getStatus());

        return new EmbedBuilder()
                .setTitle(title)
                .setDescription(
                        String.format(
                                """
                                        ID: **%d**
                                        Title: **%s**
                                        Description: **%s**
                                        Status: %s **%s**
                                        """,
                                teamTask.getId(),
                                teamTask.getTitle(),
                                teamTask.getDescription(),
                                status.getIcon(),
                                status.getStringValue()

                        )
                )
                .setColor(Color.WHITE)
                .setFooter(user.getEffectiveName(), user.getEffectiveAvatarUrl())
                .build();
    }

    @Override
    public MessageEmbed createErrorEmbed(User user, String message) {
        return new EmbedBuilder()
                .setTitle("Error Team Task")
                .setDescription(message)
                .setColor(Color.RED)
                .setFooter(user.getEffectiveName(), user.getEffectiveAvatarUrl())
                .build();
    }

    @Override
    public MessageEmbed createMissingParametersEmbed(User user, String message) {
        return null;
    }

    public MessageEmbed createTeamTaskCreatedEmbed(
            User user,
            TeamTask teamTask,
            SlashCommandInteraction event
    ) {

        List<String> assignedUsers = teamTask.getAssignedUsers();
        Guild guild = event.getGuild();
        if(guild == null) {
            return createErrorEmbed(
                    user,
                    "An error has occurred. Please try again."
            );
        }

        StringBuilder assignedUsersMessage = Loops.createTeamUsersStringBuilder(
                guild,
                assignedUsers,
                "No users assigned."
        );

        TaskStatus teamTaskStatus = TaskStatus.getTaskStatus(teamTask.getStatus());

        return new EmbedBuilder()
                .setTitle("✅ New Team Task Created")
                .setDescription("A new team task has been successfully created.")
                .setColor(Color.GREEN)
                .addField("Task Title", teamTask.getTitle(), false)
                .addField("Task Description", teamTask.getDescription(), false)
                .addField("Task ID", String.valueOf(teamTask.getId()), true)
                .addField("Team ID", String.valueOf(teamTask.getTeamID()), true)
                .addField("Status", teamTaskStatus.getIcon() + " " + teamTaskStatus.getStringValue(), true)
                .addField("Assigned Users", assignedUsersMessage.toString(), false)
                .setFooter(
                        "Created by " + user.getEffectiveName(),
                        user.getEffectiveAvatarUrl()
                )
                .setTimestamp(Instant.now())
                .build();
    }

    public MessageEmbed createTeamAssignedUsersUpdateEmbed(
            User user,
            boolean isAssign,
            User userToHandle,
            TeamTask teamTask
    ) {
        String title = isAssign ?
                "Team Task #" + teamTask.getId() + " | User Assigned" :
                "Team Task #" + teamTask.getId() + " | User Unassigned";
        Color color = isAssign ?
                Color.GREEN : Color.RED;
        String fieldMessage = isAssign ?
                "User Assigned: " : "User Unassigned: ";
        String footerMessage = isAssign ?
                "Assigned by " : "Unassigned by ";

        TaskStatus taskStatus = TaskStatus.getTaskStatus(teamTask.getStatus());

        return new EmbedBuilder()
                .setTitle(title)
                .setColor(color)
                .setDescription(
                        String.format(
                                """
                                        **Title**: **%s**
                                        **Description**: **%s**
                                        **Status**: %s **%s**
                                        **Assigned Users**: **%d**
                                        """,
                                teamTask.getTitle(),
                                teamTask.getDescription(),
                                taskStatus.getIcon(),
                                taskStatus.getStringValue(),
                                teamTask.getAssignedUsers().size()
                        )
                )
                .addField(fieldMessage, userToHandle.getAsMention(), false)
                .setFooter(footerMessage + user.getEffectiveName(), user.getEffectiveAvatarUrl())
                .build();
    }

    public MessageEmbed createTeamViewEmbed(
            User user,
            TeamTask teamTask,
            SlashCommandInteraction event
    ) {
        Guild serverGuild = event.getGuild();
        if (serverGuild == null) {
            return createErrorEmbed(
                    user,
                    "An error has occurred. Please try again."
            );
        }

        TaskStatus taskStatus = TaskStatus.getTaskStatus(teamTask.getStatus());

        StringBuilder teamMembers = Loops.createTeamUsersStringBuilder(
                serverGuild,
                teamTask.getAssignedUsers(),
                "No assigned users."
        );

        return new EmbedBuilder()
                .setTitle("Team Task #" + teamTask.getId())
                .setColor(Color.BLUE)
                .addField("Title:", teamTask.getTitle(), true)
                .addField("Status:", taskStatus.getIcon() + " " + taskStatus.getStringValue(), true)
                .addField("Description:", teamTask.getDescription(), false)
                .addField("Assigned Users:", teamMembers.toString(), false)
                .setFooter(user.getEffectiveName() + " | Team Viewing", user.getEffectiveAvatarUrl())
                .build();
    }

    @Override
    public MessageEmbed createPaginatedEmbed(User user, ArrayList<Object> data, int currentPage, int itemsPerPage) {
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(currentPage * itemsPerPage, data.size());
        int totalPages = (int) Math.ceil((double) data.size() / itemsPerPage);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("List of Tasks | Team");

        for (int i = startIndex; i < endIndex; i++) {
            if (!(data.get(i) instanceof TeamTask teamTask)) continue;

            TaskStatus status = TaskStatus.getTaskStatus(teamTask.getStatus());

            embedBuilder.addField("Team Task #" + teamTask.getId(),
                    String.format(
                            """
                                    Title: **%s**
                                    Users: %d users assigned
                                    Status: %s **%s**
                                    """,
                            teamTask.getTitle(),
                            teamTask.getAssignedUsers().size(),
                            status.getIcon(),
                            status.getStringValue()
                    ),
                    true);
        }

        embedBuilder.setFooter("Team Count: " + data.size() + " | Page " + currentPage + "/" + totalPages, user.getEffectiveAvatarUrl());

        return embedBuilder.build();
    }
}