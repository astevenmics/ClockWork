package mist.mystralix.presentation.commands.slash.team.task;

import mist.mystralix.application.helper.InputHelper;
import mist.mystralix.application.helper.TaskHelper;
import mist.mystralix.application.pagination.PaginationData;
import mist.mystralix.application.pagination.PaginationService;
import mist.mystralix.application.pagination.TeamTaskPaginationData;
import mist.mystralix.application.team.TeamService;
import mist.mystralix.application.team.TeamTaskService;
import mist.mystralix.application.validationresult.TeamTaskValidationResult;
import mist.mystralix.application.validator.TeamTaskValidator;
import mist.mystralix.application.validator.TeamValidator;
import mist.mystralix.application.validator.UserValidator;
import mist.mystralix.domain.enums.TaskStatus;
import mist.mystralix.domain.task.TeamTask;
import mist.mystralix.domain.team.Team;
import mist.mystralix.infrastructure.exception.TeamTaskOperationException;
import mist.mystralix.presentation.commands.slash.ISlashCommandCRUD;
import mist.mystralix.presentation.embeds.TeamTaskEmbed;
import mist.mystralix.utils.messages.CommonMessages;
import mist.mystralix.utils.messages.TeamMessages;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class TeamTaskSubCommandFunctions implements ISlashCommandCRUD {

    private final TeamTaskService TEAM_TASK_SERVICE;
    private final TeamService TEAM_SERVICE;
    private final PaginationService PAGINATION_SERVICE;
    private final TeamTaskEmbed TEAM_TASK_EMBED = new TeamTaskEmbed();

    public TeamTaskSubCommandFunctions(
            TeamTaskService teamTaskService,
            TeamService teamService,
            PaginationService paginationService
    ) {
        this.TEAM_TASK_SERVICE = teamTaskService;
        this.TEAM_SERVICE = teamService;
        this.PAGINATION_SERVICE = paginationService;
    }

    /*
    Only Moderator and Above can use this
    Checks if the team exists
    Checks if the user is part of the team
    Checks if the user is a moderator or a leader
    */
    @Override
    public MessageEmbed create(SlashCommandInteraction event) {

        User user = event.getUser();
        String userId = user.getId();

        OptionMapping teamOption = event.getOption("team");
        OptionMapping titleOption = event.getOption("title");
        OptionMapping descriptionOption = event.getOption("description");
        if (teamOption == null || titleOption == null || descriptionOption == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        String title = titleOption.getAsString();
        String description = descriptionOption.getAsString();

        Team team = TEAM_SERVICE.getById(teamOption.getAsInt());
        MessageEmbed messageEmbed = TeamValidator.validateTeamAndModeratorOrLeader(user, team, TEAM_TASK_EMBED);
        if (messageEmbed != null) {
            return messageEmbed;
        }

        String uuid = UUID.randomUUID().toString();

        TEAM_TASK_SERVICE.create(
                uuid,
                userId,
                title,
                description,
                TaskStatus.INPROGRESS.getIntValue(),
                team.getUUID(),
                team.getId()
        );

        try {
            team.addTask(uuid);
        } catch (TeamTaskOperationException e) {
            return TEAM_TASK_EMBED.createErrorEmbed(user, e.getLocalizedMessage());
        }
        TEAM_SERVICE.update(team);

        return TEAM_TASK_EMBED.createTeamTaskCreatedEmbed(
                user,
                TEAM_TASK_SERVICE.getByUUID(uuid),
                event
        );
    }

    /*
    Member and Above can use this
    Checks if the team exists
    Checks if the user is part of the team
    Checks if the team task exists
    Checks if the team task is part of the team
    */
    @Override
    public MessageEmbed read(SlashCommandInteraction event) {
        User user = event.getUser();
        TeamTaskValidationResult result = validateTeamAndTask(event, user, false);
        if (result.error() != null) {
            return result.error();
        }

        TeamTask teamTask = result.teamTask();
        return TEAM_TASK_EMBED.createTeamViewEmbed(user, teamTask, event);
    }

    /* Only Moderator and Above can use this
    Checks if the team exists
    Checks if the user is part of the team
    Checks if the user is a moderator or a leader
    Checks if the team task exists
    Checks if the team task is part of the team
    */
    @Override
    public MessageEmbed update(SlashCommandInteraction event) {
        User user = event.getUser();
        TeamTaskValidationResult result = validateTeamAndTask(event, user, true);
        if (result.error() != null) {
            return result.error();
        }

        TeamTask teamTask = result.teamTask();
        TaskHelper.updateTask(event, teamTask);
        TEAM_TASK_SERVICE.update(teamTask);

        return TEAM_TASK_EMBED.createMessageEmbed(user, "Updated Team Task", teamTask);
    }

    /*
    Only Moderator and Above can use this
    Checks if the team exists
    Checks if the user is part of the team
    Checks if the user is a moderator or a leader
    Checks if the team task exists
    Checks if the team task is part of the team
    */
    @Override
    public MessageEmbed delete(SlashCommandInteraction event) {

        User user = event.getUser();
        TeamTaskValidationResult result = validateTeamAndTask(event, user, true);
        if (result.error() != null) {
            return result.error();
        }

        Team team = result.team();
        TeamTask teamTask = result.teamTask();

        try {
            team.removeTask(teamTask.getUUID());
        } catch (TeamTaskOperationException e) {
            return TEAM_TASK_EMBED.createErrorEmbed(user, e.getLocalizedMessage());
        }
        TEAM_SERVICE.update(team);
        TEAM_TASK_SERVICE.delete(teamTask);

        return TEAM_TASK_EMBED.createMessageEmbed(
                user,
                "Team Task Deleted",
                teamTask
        );
    }

    /*
    Member Above can use this
    Checks if the team exists
    Checks if the user is part of the team
    Checks if the Team has any TeamTasks
    */
    @Override
    public MessageEmbed readAll(SlashCommandInteraction event) {
        User user = event.getUser();
        OptionMapping teamOption = event.getOption("team");
        if (teamOption == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        Team team = TEAM_SERVICE.getById(teamOption.getAsInt());
        MessageEmbed messageEmbed = TeamValidator.validateTeamAndAccess(user, team, TEAM_TASK_EMBED);
        if (messageEmbed != null) {
            return messageEmbed;
        }

        ArrayList<TeamTask> teamTasks = TEAM_TASK_SERVICE.findAllByTeamID(team.getId());

        if (teamTasks.isEmpty()) {
            return TEAM_TASK_EMBED.createErrorEmbed(user, TeamMessages.NO_CURRENT_TEAM_TASK);
        }

        int teamTasksPerPage = 3;
        int totalPages = (int) Math.ceil((double) teamTasks.size() / teamTasksPerPage);
        int currentPage = 1;

        PaginationData paginationData = new TeamTaskPaginationData(currentPage, totalPages, teamTasks);
        PAGINATION_SERVICE.addPaginationData(TeamTask.class.getName() + ":" + user.getId(), paginationData);

        messageEmbed = TEAM_TASK_EMBED.createPaginatedEmbed(user, new ArrayList<>(teamTasks), currentPage, teamTasksPerPage);
        Button previousButton = Button.primary("prev_page:" + TeamTask.class.getName(), "Previous");
        Button nextButton = Button.primary("next_page:" + TeamTask.class.getName(), "Next");

        previousButton = previousButton.asDisabled();
        if (currentPage == totalPages) {
            nextButton = nextButton.asDisabled();
        }
        event.getHook().editOriginalEmbeds(messageEmbed).setComponents(ActionRow.of(previousButton, nextButton)).queue();

        return messageEmbed;
    }

    /*
    Moderator and above can use this
    Checks if the team exists
    Checks if the user is part of the team
    Checks if the user is a moderator or a leader
    Checks if the team task exists
    Checks if the team task is part of the team
    Checks if the mentioned user is a bot
    Checks if the mentioned user is part of the team
    Checks if the mentioned user is already assigned to the task (for assign)
    Checks if the mentioned user is not assigned to the task (for unassign)
    */
    public MessageEmbed handleAssignment(SlashCommandInteraction event) {

        User user = event.getUser();
        TeamTaskValidationResult result = validateTeamAndTask(event, user, true);
        if (result.error() != null) {
            return result.error();
        }

        Team team = result.team();
        TeamTask teamTask = result.teamTask();

        OptionMapping userOption = event.getOption("user");
        if (event.getSubcommandName() == null || userOption == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        User userToHandle = userOption.getAsUser();
        String userToHandleId = userToHandle.getId();
        boolean isAssign = event.getSubcommandName().trim().equalsIgnoreCase("assign");

        MessageEmbed messageEmbed = UserValidator.validateUserBot(user, userToHandle, TEAM_TASK_EMBED);
        if (messageEmbed != null) {
            return messageEmbed;
        }

        if (TeamValidator.isUserNotPartOfTeam(team, userToHandleId)) {
            return TEAM_TASK_EMBED.createErrorEmbed(user,
                    String.format(
                            TeamMessages.USER_NOT_PART_OF_TEAM,
                            team.getTeamName()
                    ));
        }

        try {
            if (isAssign) {
                teamTask.addAssignedUser(userToHandleId);
            } else {
                teamTask.removeAssignedUser(userToHandleId);
            }
        } catch (TeamTaskOperationException e) {
            return TEAM_TASK_EMBED.createErrorEmbed(user, e.getMessage());
        }
        TEAM_TASK_SERVICE.update(teamTask);

        return TEAM_TASK_EMBED.createTeamAssignedUsersUpdateEmbed(
                user,
                isAssign,
                userToHandle,
                teamTask
        );

    }

    private TeamTaskValidationResult validateTeamAndTask(SlashCommandInteraction event, User user, boolean requireModerator) {
        MessageEmbed messageEmbed = InputHelper.checkInputNull(user, TEAM_TASK_EMBED, "team", "task");
        if (messageEmbed != null) {
            return new TeamTaskValidationResult(messageEmbed, null, null);
        }

        Team team = TEAM_SERVICE.getById(Objects.requireNonNull(event.getOption("team")).getAsInt());
        messageEmbed = TeamValidator.validateTeamAndAccess(user, team, TEAM_TASK_EMBED);
        if (messageEmbed != null) {
            return new TeamTaskValidationResult(messageEmbed, null, null);
        }

        if (requireModerator) {
            messageEmbed = TeamValidator.validateTeamAndModeratorOrLeader(user, team, TEAM_TASK_EMBED);
            if (messageEmbed != null) {
                return new TeamTaskValidationResult(messageEmbed, null, null);
            }
        }

        TeamTask teamTask = TEAM_TASK_SERVICE.getById(Objects.requireNonNull(event.getOption("task")).getAsInt());
        messageEmbed = TeamTaskValidator.validateTeamTask(user, team, teamTask, TEAM_TASK_EMBED);
        if (messageEmbed != null) {
            return new TeamTaskValidationResult(messageEmbed, null, null);
        }

        return new TeamTaskValidationResult(null, team, teamTask);
    }

}