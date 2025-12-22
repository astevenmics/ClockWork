package mist.mystralix.application.validationresult;

import mist.mystralix.domain.team.Team;
import net.dv8tion.jda.api.entities.MessageEmbed;

public record TeamValidationResult(MessageEmbed error, Team team) {
}
