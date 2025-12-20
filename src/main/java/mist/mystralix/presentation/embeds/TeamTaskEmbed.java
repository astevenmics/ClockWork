package mist.mystralix.presentation.embeds;

import mist.mystralix.application.loops.Loops;
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

public class TeamTaskEmbed implements IMessageEmbedBuilder {

    @Override
    public <T> MessageEmbed createMessageEmbed(User user, String title, T object) {
        return null;
    }

    @Override
    public MessageEmbed createListEmbed(User user, ArrayList<?> list) {
        return null;
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

        ArrayList<String> assignedUsers = teamTask.getAssignedUsers();
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

        TaskStatus teamTaskStatus = teamTask.getTaskDAO().getTaskStatus();

        return new EmbedBuilder()
                .setTitle("✅ New Team Task Created")
                .setDescription("A new team task has been successfully created.")
                .setColor(Color.GREEN)
                .addField("Task Title", teamTask.getTaskDAO().getTitle(), false)
                .addField("Task Description", teamTask.getTaskDAO().getDescription(), false)
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
}