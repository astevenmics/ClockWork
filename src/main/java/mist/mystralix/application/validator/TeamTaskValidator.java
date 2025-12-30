package mist.mystralix.application.validator;

import mist.mystralix.domain.task.TeamTask;
import mist.mystralix.domain.team.Team;
import mist.mystralix.presentation.embeds.IMessageEmbedBuilder;
import mist.mystralix.utils.messages.CommonMessages;
import mist.mystralix.utils.messages.TeamMessages;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class TeamTaskValidator {

    public static MessageEmbed validateTeamTask(
            User user,
            Team team,
            TeamTask teamTask,
            IMessageEmbedBuilder embedBuilder
    ) {
        if (teamTask == null) {
            return embedBuilder.createErrorEmbed(user,
                    String.format(CommonMessages.OBJECT_NOT_FOUND, "Team Task")
            );
        }

        if (!team.getTasksUUID().contains(teamTask.getUUID())) {
            return embedBuilder.createErrorEmbed(user,
                    String.format(TeamMessages.TEAM_TASK_NOT_PART_OF_TEAM, teamTask.getId(), team.getTeamName())
            );
        }
        return null;
    }

}
