package mist.mystralix.presentation.commands.slash.team;

import mist.mystralix.application.team.TeamService;
import mist.mystralix.application.validationresult.TeamValidationResult;
import mist.mystralix.application.validator.TeamValidator;
import mist.mystralix.application.validator.UserValidator;
import mist.mystralix.domain.team.Team;
import mist.mystralix.presentation.commands.slash.ISlashCommandCRUD;
import mist.mystralix.presentation.embeds.TeamEmbed;
import mist.mystralix.utils.messages.CommonMessages;
import mist.mystralix.utils.messages.TeamMessages;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class TeamSubCommandFunctions implements ISlashCommandCRUD {

    private final TeamService TEAM_SERVICE;
    private final TeamEmbed TEAM_EMBED = new TeamEmbed();

    public TeamSubCommandFunctions(TeamService teamService) {
        this.TEAM_SERVICE = teamService;
    }

    @Override
    public MessageEmbed create(SlashCommandInteraction event) {
        User user = event.getUser();
        OptionMapping optionName = event.getOption("team_name");
        if (optionName == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        String name = optionName.getAsString();
        String uuid = UUID.randomUUID().toString();
        TEAM_SERVICE.create(uuid, name, user.getId());

        return TEAM_EMBED.createMessageEmbed(user, "New Team", TEAM_SERVICE.getByUUID(uuid));
    }

    public MessageEmbed add(SlashCommandInteraction event) {

        User user = event.getUser();

        MessageEmbed messageEmbed, invitationEmbed;
        OptionMapping userOption = event.getOption("user");
        OptionMapping idOption = event.getOption("id");
        if (userOption == null || idOption == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        User userToAdd = userOption.getAsUser();
        String userToAddMention = userToAdd.getAsMention();
        int id = idOption.getAsInt();

        MessageEmbed errorEmbed = TeamValidator.validateTeamAndPermission(
                user,
                TEAM_SERVICE,
                TEAM_EMBED,
                id
        );

        if (errorEmbed != null) {
            return errorEmbed;
        }

        MessageEmbed userErrorEmbed = UserValidator.validatorUser(user, userToAdd, TEAM_EMBED);
        if (userErrorEmbed != null) {
            return userErrorEmbed;
        }

        Team team = TEAM_SERVICE.getById(id);

        if (team.getModerators().contains(userToAdd.getId()) || team.getMembers().contains(userToAdd.getId())) {
            // User already in the team
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            TeamMessages.USER_ALREADY_PART_OF_TEAM,
                            userToAddMention,
                            team.getTeamName()
                    )
            );
        }

        ArrayList<String> teamInvitations = team.getTeamInvitations();
        if(teamInvitations.contains(userToAdd.getId())) {
            // User already invited
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            TeamMessages.USER_ALREADY_INVITED,
                            userToAddMention
                    )
            );
        }

        invitationEmbed = TEAM_EMBED.createInvitationToUserEmbed(user, userToAdd, team);
        messageEmbed = TEAM_EMBED.createInvitationEmbed(user, userToAdd, team);

        userToAdd.openPrivateChannel().queue(channel -> channel.sendMessageEmbeds(invitationEmbed).queue());

        // Pending invitation
        teamInvitations.add(userToAdd.getId());
        team.setTeamInvitations(teamInvitations);

        TEAM_SERVICE.update(team);

        return messageEmbed;
    }

    public MessageEmbed remove(SlashCommandInteraction event) {

        User user = event.getUser();
        String userId = user.getId();

        OptionMapping idOption = event.getOption("id");
        OptionMapping userOption = event.getOption("user");
        if (idOption == null || userOption == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        int teamId = idOption.getAsInt();
        MessageEmbed errorEmbed = TeamValidator.validateTeamAndPermission(user, TEAM_SERVICE, TEAM_EMBED, teamId);
        if (errorEmbed != null) return errorEmbed;

        Team team = TEAM_SERVICE.getById(teamId);
        User userToRemove = userOption.getAsUser();
        String userToRemoveId = userToRemove.getId();

        MessageEmbed userErrorEmbed = UserValidator.validatorUser(user, userToRemove, TEAM_EMBED);
        if (userErrorEmbed != null) {
            return userErrorEmbed;
        }

        String teamLeaderID = team.getTeamLeader();
        ArrayList<String> moderators = team.getModerators();
        ArrayList<String> members = team.getMembers();

        if(teamLeaderID.equals(userToRemoveId) || moderators.contains(userToRemoveId)) {
            return TEAM_EMBED.createErrorEmbed(user, TeamMessages.CANNOT_REMOVE_FELLOW_MODERATOR);
        }

        if(!moderators.contains(userToRemove.getId()) && !members.contains(userToRemove.getId())) {
            return TEAM_EMBED.createErrorEmbed(user,
                    String.format(
                            TeamMessages.USER_NOT_PART_OF_TEAM,
                            team.getTeamName()
                    )
            );
        }

        if(teamLeaderID.equals(userId)) {
            moderators.remove(userToRemoveId);
            members.remove(userToRemoveId);
            TEAM_SERVICE.update(team);
        } else if(moderators.contains(userId)) {
            members.remove(userToRemoveId);
        } else {
            return TEAM_EMBED.createErrorEmbed(user, TeamMessages.MODERATOR_REQUIRED);
        }

        TEAM_SERVICE.update(team);

        return TEAM_EMBED.createRemovedMemberEmbed(user, userToRemove, team);
    }

    public MessageEmbed handleInvitation(SlashCommandInteraction event) {
        User user = event.getUser();
        String userId = user.getId();

        OptionMapping idOption = event.getOption("id");
        OptionMapping decisionOption = event.getOption("decision");
        if(idOption == null || decisionOption == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        Team team = TEAM_SERVICE.getById(idOption.getAsInt());
        if (team == null) {
            return TEAM_EMBED.createErrorEmbed(user,
                    String.format(
                            CommonMessages.OBJECT_NOT_FOUND,
                            "team"
                    ));
        }

        if (team.getTeamLeader().equals(userId) || team.getModerators().contains(userId) || team.getMembers().contains(userId)) {
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            TeamMessages.ALREADY_PART_OF_TEAM,
                            team.getTeamName()
                    )
            );
        }

        ArrayList<String> teamInvitations = team.getTeamInvitations();
        if(!teamInvitations.contains(userId)) {
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    TeamMessages.NO_PENDING_TEAM_INVITATION
            );
        }

        // Decision is either "accept" or "reject"
        String decision = decisionOption.getAsString();
        boolean isInvitationAccepted = decision.equals("accept");
        if(isInvitationAccepted) {
            team.getMembers().add(userId);
        }
        teamInvitations.remove(userId);
        TEAM_SERVICE.update(team);


        return isInvitationAccepted ?
                TEAM_EMBED.invitationAcceptedEmbed(user, team) :
                TEAM_EMBED.invitationRejectedEmbed(user, team);
    }


    public MessageEmbed leave(SlashCommandInteraction event) {
        User user = event.getUser();
        String userId = user.getId();

        OptionMapping idOption = event.getOption("id");
        if(idOption == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        MessageEmbed errorEmbed = TeamValidator.validateTeamAndAccess(user, TEAM_SERVICE, TEAM_EMBED, idOption.getAsInt());
        if (errorEmbed != null) return errorEmbed;
        Team team = TEAM_SERVICE.getById(idOption.getAsInt());

        if (team.getTeamLeader().equals(userId)) {
            return TEAM_EMBED.createErrorEmbed(user, TeamMessages.LEADER_CANNOT_LEAVE);
        }

        team.getModerators().remove(userId);
        team.getMembers().remove(userId);
        TEAM_SERVICE.update(team);

        return TEAM_EMBED.createLeftTeamEmbed(
                user,
                team
        );
    }

    @Override
    public MessageEmbed read(SlashCommandInteraction event) {

        User user = event.getUser();

        OptionMapping idOption = event.getOption("id");
        if(idOption == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        MessageEmbed errorEmbed = TeamValidator.validateTeamAndAccess(user, TEAM_SERVICE, TEAM_EMBED, idOption.getAsInt());
        if (errorEmbed != null) return errorEmbed;

        return TEAM_EMBED.createTeamInfoEmbed(
                user,
                TEAM_SERVICE.getById(idOption.getAsInt()),
                event
        );
    }

    @Override
    public MessageEmbed update(SlashCommandInteraction event) {
        return null;
    }

    @Override
    public MessageEmbed delete(SlashCommandInteraction event) {

        User user = event.getUser();

        OptionMapping optionName = event.getOption("id");
        if (optionName == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }
        int teamId = optionName.getAsInt();

        MessageEmbed errorEmbed = TeamValidator.validateTeamAndLeadership(user, TEAM_SERVICE, TEAM_EMBED, teamId);
        if (errorEmbed != null) return errorEmbed;
        Team team = TEAM_SERVICE.getById(teamId);

        TEAM_SERVICE.delete(team);
        return TEAM_EMBED.createMessageEmbed(user, team.getTeamName() + " team has been deleted", team);
    }

    @Override
    public MessageEmbed readAll(SlashCommandInteraction event) {
        User user = event.getUser();

        ArrayList<Team> userTeams = TEAM_SERVICE.getUserTeams(user.getId());

        return TEAM_EMBED.createListEmbed(user, userTeams);
    }

    public MessageEmbed handlePosition(SlashCommandInteraction event) {
        User user = event.getUser();
        OptionMapping teamOption = event.getOption("id");
        OptionMapping userToHandleOption = event.getOption("user");
        OptionMapping positionOption = event.getOption("position");

        if (teamOption == null || userToHandleOption == null || positionOption == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        MessageEmbed errorEmbed = TeamValidator.validateTeamAndLeadership(user, TEAM_SERVICE, TEAM_EMBED, teamOption.getAsInt());
        if (errorEmbed != null) return errorEmbed;
        Team team = TEAM_SERVICE.getById(teamOption.getAsInt());
        ArrayList<String> moderators = team.getModerators();
        ArrayList<String> members = team.getMembers();

        User userToHandle = userToHandleOption.getAsUser();

        MessageEmbed userErrorEmbed = UserValidator.validatorUser(user, userToHandle, TEAM_EMBED);
        if (userErrorEmbed != null) {
            return userErrorEmbed;
        }

        if (!moderators.contains(userToHandle.getId()) && !members.contains(userToHandle.getId())) {
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            TeamMessages.NOT_PART_OF_TEAM,
                            team.getTeamName()
                    )
            );
        }

        String position = positionOption.getAsString();

        if ((position.equals("moderator") && moderators.contains(userToHandle.getId())) ||
                (position.equals("member") && members.contains(userToHandle.getId()))) {
            return TEAM_EMBED.createErrorEmbed(
                    user,
                    String.format(
                            TeamMessages.USER_ALREADY_HAS_ROLE_IN_TEAM,
                            userToHandle.getAsMention(),
                            position,
                            team.getTeamName()
                    )
            );
        }

        if (position.equals("moderator")) {
            moderators.add(userToHandle.getId());
            members.remove(userToHandle.getId());
        } else if (position.equals("member")) {
            moderators.remove(userToHandle.getId());
            members.add(userToHandle.getId());
        }

        TEAM_SERVICE.update(team);

        return TEAM_EMBED.createPositionUpdateEmbed(user, userToHandle, team, position.equals("moderator"));
    }

    public MessageEmbed updateName(SlashCommandInteraction event) {
        User user = event.getUser();
        TeamValidationResult result = TeamValidator.validateTeamAndGetTeam(event, TEAM_SERVICE, TEAM_EMBED, "id", "name");
        if (result.error() != null) return result.error();

        Team team = result.team();
        String currentName = team.getTeamName();
        String newName = Objects.requireNonNull(event.getOption("name")).getAsString();
        team.setTeamName(newName);
        TEAM_SERVICE.update(team);

        return TEAM_EMBED.createTeamNameUpdateEmbed(user, team, currentName);
    }

    public MessageEmbed transferTeam(SlashCommandInteraction event) {
        User user = event.getUser();
        TeamValidationResult result = TeamValidator.validateTeamAndGetTeam(event, TEAM_SERVICE, TEAM_EMBED, "id", "user");
        if (result.error() != null) return result.error();

        Team team = result.team();
        User userMentioned = Objects.requireNonNull(event.getOption("user")).getAsUser();
        ArrayList<String> moderators = team.getModerators();
        ArrayList<String> members = team.getMembers();

        MessageEmbed userErrorEmbed = UserValidator.validatorUser(user, userMentioned, TEAM_EMBED);
        if (userErrorEmbed != null) {
            return userErrorEmbed;
        }

        if (!moderators.contains(userMentioned.getId()) && !members.contains(userMentioned.getId())) {
            return TEAM_EMBED.createErrorEmbed(user,
                    String.format(
                            TeamMessages.USER_NOT_PART_OF_TEAM,
                            team.getTeamName()
                    ));
        }

        team.setTeamLeader(userMentioned.getId());
        moderators.remove(userMentioned.getId());
        members.remove(userMentioned.getId());
        moderators.add(user.getId());
        TEAM_SERVICE.update(team);

        return TEAM_EMBED.createTeamTransferredEmbed(user, userMentioned, team);
    }
}