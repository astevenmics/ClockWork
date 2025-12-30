package mist.mystralix.presentation.commands.slash.team;

import mist.mystralix.application.pagination.PaginationData;
import mist.mystralix.application.pagination.PaginationService;
import mist.mystralix.application.pagination.TeamPaginationData;
import mist.mystralix.application.team.TeamService;
import mist.mystralix.application.validator.TeamValidator;
import mist.mystralix.application.validator.UserValidator;
import mist.mystralix.domain.team.Team;
import mist.mystralix.infrastructure.exception.TeamOperationException;
import mist.mystralix.presentation.commands.slash.ISlashCommandCRUD;
import mist.mystralix.presentation.embeds.TeamEmbed;
import mist.mystralix.utils.messages.CommonMessages;
import mist.mystralix.utils.messages.TeamMessages;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamSubCommandFunctions implements ISlashCommandCRUD {

    private final TeamService TEAM_SERVICE;
    private final PaginationService PAGINATION_SERVICE;
    private final TeamEmbed TEAM_EMBED = new TeamEmbed();

    public TeamSubCommandFunctions(TeamService teamService, PaginationService paginationService) {
        this.TEAM_SERVICE = teamService;
        this.PAGINATION_SERVICE = paginationService;
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

        try {
            TEAM_SERVICE.create(uuid, name, user.getId());
        } catch (TeamOperationException exception) {
            return TEAM_EMBED.createErrorEmbed(user, exception.getMessage());
        }

        return TEAM_EMBED.createMessageEmbed(user, "New Team", TEAM_SERVICE.getByUUID(uuid));
    }

    /*
    Only Leader can use this
    Checks if the team exists
    Checks if the user is part of the team
    Checks if the user is a leader
    Checks if the mentioned user is a bot
    Checks if the mentioned user is part of the team
    */
    public MessageEmbed handlePosition(SlashCommandInteraction event) {
        User user = event.getUser();
        OptionMapping teamOption = event.getOption("id");
        OptionMapping userToHandleOption = event.getOption("user");
        OptionMapping positionOption = event.getOption("position");

        if (teamOption == null || userToHandleOption == null || positionOption == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        Team team = TEAM_SERVICE.getById(teamOption.getAsInt());
        MessageEmbed messageEmbed = TeamValidator.validateTeamAndLeader(user, team, TEAM_EMBED);
        if (messageEmbed != null) return messageEmbed;

        User userToHandle = userToHandleOption.getAsUser();
        messageEmbed = UserValidator.validateUserBot(user, userToHandle, TEAM_EMBED);
        if (messageEmbed != null) {
            return messageEmbed;
        }
        if (TeamValidator.isUserNotPartOfTeam(team, userToHandle.getId())) {
            return TEAM_EMBED.createErrorEmbed(user,
                    String.format(
                            TeamMessages.USER_NOT_PART_OF_TEAM,
                            team.getTeamName()
                    ));
        }

        String position = positionOption.getAsString().trim().toLowerCase();
        try {
            switch (position) {
                case "leader" -> {
                    team.transferLeadership(userToHandle.getId());
                    TEAM_SERVICE.update(team);
                    return TEAM_EMBED.createTeamTransferredEmbed(user, userToHandle, team);
                }
                case "moderator" -> {
                    team.addModerator(userToHandle.getId());
                    team.removeMember(userToHandle.getId());
                }
                case "member" -> {
                    if (team.getModerators().contains(userToHandle.getId())) {
                        team.removeModerator(userToHandle.getId());
                    }
                    team.addMember(userToHandle.getId());
                }
            }
        } catch (TeamOperationException e) {
            return TEAM_EMBED.createErrorEmbed(user, e.getMessage());
        }

        TEAM_SERVICE.update(team);
        return TEAM_EMBED.createPositionUpdateEmbed(user, userToHandle, team, position.equals("moderator"));
    }

    /*
    Only Leader can use this
    Checks if the team exists
    Checks if the user is part of the team
    Checks if the user is a leader
    */
    public MessageEmbed updateName(SlashCommandInteraction event) {
        User user = event.getUser();
        OptionMapping teamOption = event.getOption("id");
        OptionMapping nameOption = event.getOption("name");
        if (teamOption == null || nameOption == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        Team team = TEAM_SERVICE.getById(teamOption.getAsInt());
        MessageEmbed messageEmbed = TeamValidator.validateTeamAndLeader(user, team, TEAM_EMBED);
        if (messageEmbed != null) return messageEmbed;

        String currentName = team.getTeamName();
        String newName = nameOption.getAsString();
        try {
            team.setTeamName(newName);
        } catch (TeamOperationException e) {
            return TEAM_EMBED.createErrorEmbed(user, e.getMessage());
        }
        TEAM_SERVICE.update(team);

        return TEAM_EMBED.createTeamNameUpdateEmbed(user, team, currentName);
    }

    /*
    Only Leader can use this
    Checks if the team exists
    Checks if the user is part of the team
    Checks if the user is a leader
    */
    @Override
    public MessageEmbed delete(SlashCommandInteraction event) {

        User user = event.getUser();
        OptionMapping teamOption = event.getOption("id");
        if (teamOption == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        Team team = TEAM_SERVICE.getById(teamOption.getAsInt());
        MessageEmbed messageEmbed = TeamValidator.validateTeamAndLeader(user, team, TEAM_EMBED);
        if (messageEmbed != null) return messageEmbed;

        TEAM_SERVICE.delete(team);
        return TEAM_EMBED.createMessageEmbed(
                user,
                String.format(TeamMessages.TEAM_DELETED, team.getTeamName()),
                team
        );
    }

    /*
    Only Moderator and Above can use this
    Checks if the team exists
    Checks if the user is part of the team
    Checks if the user is a moderator or a leader
    Checks if the mentioned user is a bot
    Checks if the mentioned user is already part of the team (cancels it)
    */
    public MessageEmbed add(SlashCommandInteraction event) {

        User user = event.getUser();
        MessageEmbed messageEmbed, invitationEmbed;

        OptionMapping userOption = event.getOption("user");
        OptionMapping idOption = event.getOption("id");
        if (userOption == null || idOption == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        Team team = TEAM_SERVICE.getById(idOption.getAsInt());
        messageEmbed = TeamValidator.validateTeamAndModeratorOrLeader(user, team, TEAM_EMBED);
        if (messageEmbed != null) return messageEmbed;

        User userToAdd = userOption.getAsUser();
        messageEmbed = UserValidator.validateUserBot(userToAdd, userToAdd, TEAM_EMBED);
        if (messageEmbed != null) return messageEmbed;
        if (TeamValidator.isUserPartOfTeam(team, userToAdd.getId())) {
            return TEAM_EMBED.createErrorEmbed(user,
                    String.format(
                            TeamMessages.USER_ALREADY_PART_OF_TEAM,
                            team.getTeamName()
                    ));
        }

        // Add user to pending team invitation list
        try {
            team.addTeamInvitation(userToAdd.getId());
        } catch (TeamOperationException exception) {
            return TEAM_EMBED.createErrorEmbed(user, exception.getMessage());
        }

        invitationEmbed = TEAM_EMBED.createInvitationToUserEmbed(user, userToAdd, team);
        messageEmbed = TEAM_EMBED.createInvitationEmbed(user, userToAdd, team);
        TEAM_SERVICE.update(team);

        userToAdd.openPrivateChannel().queue(
                channel -> channel.sendMessageEmbeds(invitationEmbed).queue(
                        success -> {
                        },
                        fail -> {
                            // DM failed
                            event.getChannel().sendMessageEmbeds(
                                    TEAM_EMBED.createErrorEmbed(user,
                                            "Failed to send DM to " + userToAdd.getName() + " (DM blocked)")
                            ).queue();
                        }),
                fail -> {
                    // Could not open DM
                    event.getChannel().sendMessageEmbeds(
                            TEAM_EMBED.createErrorEmbed(user,
                                    "Cannot open DM with " + userToAdd.getName() +
                                            " — DMs are off or blocked the bot")
                    ).queue();
                });

        return messageEmbed;
    }

    /*
    Only Moderator and Above can use this
    Checks if the team exists
    Checks if the user is part of the team
    Checks if the user is a moderator or a leader
    Checks if the mentioned user is a bot
    Checks if the mentioned user is part of the team (if not returns)
    Checks if the mentioned user is the team leader (cannot remove team leader)
    Checks if the user is a moderator and mentioned user is a moderator (cannot remove co-moderators)
    */
    public MessageEmbed remove(SlashCommandInteraction event) {

        User user = event.getUser();
        String userId = user.getId();

        OptionMapping idOption = event.getOption("id");
        OptionMapping userOption = event.getOption("user");
        if (idOption == null || userOption == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        Team team = TEAM_SERVICE.getById(idOption.getAsInt());
        MessageEmbed messageEmbed = TeamValidator.validateTeamAndModeratorOrLeader(user, team, TEAM_EMBED);
        if (messageEmbed != null) return messageEmbed;

        User userToRemove = userOption.getAsUser();
        String userToRemoveId = userToRemove.getId();
        messageEmbed = UserValidator.validateUserBot(user, userToRemove, TEAM_EMBED);
        if (messageEmbed != null) return messageEmbed;

        if (TeamValidator.isUserNotPartOfTeam(team, userToRemoveId)) {
            return TEAM_EMBED.createErrorEmbed(user,
                    String.format(
                            TeamMessages.USER_NOT_PART_OF_TEAM,
                            team.getTeamName()
                    ));
        }

        String teamLeaderID = team.getTeamLeader();
        List<String> moderators = team.getModerators();
        List<String> members = team.getMembers();

        // Team leader cannot be removed
        if (teamLeaderID.equals(userToRemoveId)) {
            return TEAM_EMBED.createErrorEmbed(user, TeamMessages.CANNOT_REMOVE_TEAM_LEADER);
        }

        try {
            if (teamLeaderID.equals(userId)) {
                // Leader can remove moderators and members
                if (moderators.contains(userToRemoveId)) team.removeModerator(userToRemoveId);
                if (members.contains(userToRemoveId)) team.removeMember(userToRemoveId);
            } else if (moderators.contains(userId)) {
                // Moderators can remove only members
                if (moderators.contains(userToRemoveId)) {
                    return TEAM_EMBED.createErrorEmbed(user, TeamMessages.CANNOT_REMOVE_FELLOW_MODERATOR);
                }
                team.removeMember(userToRemoveId);
            } else {
                return TEAM_EMBED.createErrorEmbed(user, TeamMessages.MODERATOR_REQUIRED);
            }
        } catch (TeamOperationException e) {
            return TEAM_EMBED.createErrorEmbed(user, e.getMessage());
        }

        TEAM_SERVICE.update(team);

        return TEAM_EMBED.createRemovedMemberEmbed(user, userToRemove, team);
    }

    /*
    Only Members and above can use this
    Checks if the team exists
    Checks if the user is part of the team
    Checks if the user is a leader (leaders cannot leave, they must transfer team leadership first)
    */
    public MessageEmbed leave(SlashCommandInteraction event) {
        User user = event.getUser();
        String userId = user.getId();

        OptionMapping idOption = event.getOption("id");
        if(idOption == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        Team team = TEAM_SERVICE.getById(idOption.getAsInt());
        MessageEmbed messageEmbed = TeamValidator.validateTeamAndAccess(user, team, TEAM_EMBED);
        if (messageEmbed != null) return messageEmbed;

        if (team.getTeamLeader().equals(userId)) {
            return TEAM_EMBED.createErrorEmbed(user, TeamMessages.LEADER_CANNOT_LEAVE);
        }

        try {
            if (team.getModerators().contains(userId)) {
                team.removeModerator(userId);
            } else {
                team.removeMember(userId);
            }
        } catch (TeamOperationException e) {
            return TEAM_EMBED.createErrorEmbed(user, e.getMessage());
        }
        TEAM_SERVICE.update(team);

        return TEAM_EMBED.createLeftTeamEmbed(
                user,
                team
        );
    }


    /*
    Only Members and Above can use this
    Checks if the team exists
    Checks if the user is part of the team
    */
    @Override
    public MessageEmbed read(SlashCommandInteraction event) {

        User user = event.getUser();

        OptionMapping idOption = event.getOption("id");
        if(idOption == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        Team team = TEAM_SERVICE.getById(idOption.getAsInt());
        MessageEmbed messageEmbed = TeamValidator.validateTeamAndAccess(user, team, TEAM_EMBED);
        if (messageEmbed != null) return messageEmbed;

        return TEAM_EMBED.createTeamInfoEmbed(
                user,
                TEAM_SERVICE.getById(idOption.getAsInt()),
                event
        );
    }

    /*
    Non-Members can use this
    Checks if the team exists
    Checks if the user is already part of the team
    Checks if the user has an existing invitation from the team
    */
    public MessageEmbed handleInvitation(SlashCommandInteraction event) {
        User user = event.getUser();
        String userId = user.getId();

        OptionMapping idOption = event.getOption("id");
        OptionMapping decisionOption = event.getOption("decision");
        if (idOption == null || decisionOption == null) {
            return TEAM_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        Team team = TEAM_SERVICE.getById(idOption.getAsInt());
        MessageEmbed messageEmbed = TeamValidator.validateTeam(user, team, TEAM_EMBED);
        if (messageEmbed != null) return messageEmbed;

        if (TeamValidator.isUserPartOfTeam(team, userId)) {
            return TEAM_EMBED.createErrorEmbed(user,
                    String.format(
                            TeamMessages.ALREADY_PART_OF_TEAM,
                            team.getTeamName()
                    )
            );
        }

        if (!team.getTeamInvitations().contains(userId)) {
            return TEAM_EMBED.createErrorEmbed(user, TeamMessages.NO_PENDING_TEAM_INVITATION);
        }

        // Decision is either "accept" or "reject"
        String decision = decisionOption.getAsString().trim().toLowerCase();
        boolean isInvitationAccepted = decision.equals("accept");

        try {
            team.removeTeamInvitation(userId);
            if (isInvitationAccepted) team.addMember(userId);
        } catch (TeamOperationException e) {
            return TEAM_EMBED.createErrorEmbed(user, e.getMessage());
        }

        TEAM_SERVICE.update(team);

        return isInvitationAccepted ?
                TEAM_EMBED.invitationAcceptedEmbed(user, team) :
                TEAM_EMBED.invitationRejectedEmbed(user, team);
    }

    // Anyone can use this can use this
    // Checks if user is in any teams
    @Override
    public MessageEmbed readAll(SlashCommandInteraction event) {
        User user = event.getUser();

        ArrayList<Team> userTeams = TEAM_SERVICE.getUserTeams(user.getId());
        if (userTeams.isEmpty()) {
            return TEAM_EMBED.createErrorEmbed(user, TeamMessages.NOT_IN_A_TEAM);
        }

        int teamsPerPage = 12;
        int totalPages = (int) Math.ceil((double) userTeams.size() / teamsPerPage);
        int currentPage = 1;

        PaginationData paginationData = new TeamPaginationData(currentPage, totalPages, userTeams);
        PAGINATION_SERVICE.addPaginationData(Team.class.getName() + ":" + user.getId(), paginationData);

        MessageEmbed messageEmbed = TEAM_EMBED.createPaginatedEmbed(user, new ArrayList<>(userTeams), currentPage, teamsPerPage);
        Button previousButton = Button.primary("prev_page:" + Team.class.getName(), "Previous");
        Button nextButton = Button.primary("next_page:" + Team.class.getName(), "Next");

        previousButton = previousButton.asDisabled();
        if (currentPage == totalPages) {
            nextButton = nextButton.asDisabled();
        }
        event.getHook().editOriginalEmbeds(messageEmbed).setComponents(ActionRow.of(previousButton, nextButton)).queue();

        return messageEmbed;
    }

    @Override
    public MessageEmbed update(SlashCommandInteraction event) {
        return null;
    }
}