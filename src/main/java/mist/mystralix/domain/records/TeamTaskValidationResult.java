package mist.mystralix.domain.records;

import mist.mystralix.domain.task.TeamTask;
import mist.mystralix.domain.team.Team;
import net.dv8tion.jda.api.entities.MessageEmbed;

public record TeamTaskValidationResult(MessageEmbed error, Team team, TeamTask teamTask) {
}
