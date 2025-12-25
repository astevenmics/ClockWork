package mist.mystralix.presentation.commands.slash.team.task;

import mist.mystralix.application.helper.TaskHelper;
import mist.mystralix.application.helper.TeamTaskHelper;
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
import mist.mystralix.domain.task.TaskDAO;
import mist.mystralix.domain.task.TeamTask;
import mist.mystralix.domain.team.Team;
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

        int teamId = teamOption.getAsInt();
        String title = titleOption.getAsString();
        String description = descriptionOption.getAsString();

        MessageEmbed messageEmbed = TeamValidator.validateTeamAndPermission(user, TEAM_SERVICE, TEAM_TASK_EMBED, teamId);
        if (messageEmbed != null) {
            return messageEmbed;
        }

        Team team = TEAM_SERVICE.getById(teamId);
        String uuid = UUID.randomUUID().toString();

        TEAM_TASK_SERVICE.create(
                uuid,
                userId,
                team.getUUID(),
                teamId,
                new TaskDAO(
                        title,
                        description,
                        TaskStatus.INPROGRESS
                )
        );

        team.getTasksUUID().add(uuid);
        TEAM_SERVICE.update(team);

        return TEAM_TASK_EMBED.createTeamTaskCreatedEmbed(
                user,
                TEAM_TASK_SERVICE.getByUUID(uuid),
                event
        );
    }

    @Override
    public MessageEmbed read(SlashCommandInteraction event) {

        MessageEmbed messageEmbed = TeamTaskValidator.validateInputAndTeamAndPermissionAndTeamTask(
                event,
                TEAM_SERVICE,
                TEAM_TASK_SERVICE,
                TEAM_TASK_EMBED
        );
        if (messageEmbed != null) {
            return messageEmbed;
        }

        User user = event.getUser();
        // Will never be null due to the checker in messageEmbed
        int taskId = Objects.requireNonNull(event.getOption("task")).getAsInt();

        return TEAM_TASK_EMBED.createTeamViewEmbed(
                user,
                TEAM_TASK_SERVICE.getById(taskId),
                event
        );
    }

    @Override
    public MessageEmbed update(SlashCommandInteraction event) {

        MessageEmbed messageEmbed = TeamTaskValidator.validateInputAndTeamAndPermissionAndTeamTask(
                event,
                TEAM_SERVICE,
                TEAM_TASK_SERVICE,
                TEAM_TASK_EMBED
        );
        if (messageEmbed != null) {
            return messageEmbed;
        }

        User user = event.getUser();
        // teamId and taskId are not null due to the checker in messageEmbed
        int taskId = Objects.requireNonNull(event.getOption("task")).getAsInt();
        TeamTask teamTask = TEAM_TASK_SERVICE.getById(taskId);

        TaskHelper.updateTaskDAO(event, teamTask.getTaskDAO());
        TEAM_TASK_SERVICE.update(teamTask);

        return TEAM_TASK_EMBED.createMessageEmbed(user, "Updated Task", teamTask);
    }

    @Override
    public MessageEmbed delete(SlashCommandInteraction event) {

        TeamTaskValidationResult result = TeamTaskValidator.validateAndGetTeamTask(
                event,
                TEAM_SERVICE,
                TEAM_TASK_SERVICE,
                TEAM_TASK_EMBED
        );
        if (result.error() != null) {
            return result.error();
        }

        User user = event.getUser();
        ArrayList<String> teamTasksUUID = result.team().getTasksUUID();
        teamTasksUUID.remove(result.teamTask().getUUID());
        TEAM_SERVICE.update(result.team());
        TEAM_TASK_SERVICE.delete(result.teamTask());

        return TEAM_TASK_EMBED.createMessageEmbed(
                user,
                "Team Task Deleted",
                result.teamTask()
        );
    }

    @Override
    public MessageEmbed readAll(SlashCommandInteraction event) {
        User user = event.getUser();
        OptionMapping teamOption = event.getOption("team");

        if (teamOption == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        int teamId = teamOption.getAsInt();

        MessageEmbed messageEmbed = TeamValidator.validateTeamAndAccess(user, TEAM_SERVICE, TEAM_TASK_EMBED, teamId);
        if (messageEmbed != null) {
            return messageEmbed;
        }

        ArrayList<TeamTask> teamTasks = TEAM_TASK_SERVICE.findAllByTeamID(teamId);

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

    public MessageEmbed handleAssignment(SlashCommandInteraction event) {
        TeamTaskValidationResult result = TeamTaskValidator.validateAndGetTeamTask(
                event,
                TEAM_SERVICE,
                TEAM_TASK_SERVICE,
                TEAM_TASK_EMBED
        );
        if (result.error() != null) {
            return result.error();
        }

        User user = event.getUser();
        // teamId and taskId are not null due to the checker in messageEmbed
        OptionMapping userOption = event.getOption("user");

        if (event.getSubcommandName() == null || userOption == null) {
            return TEAM_TASK_EMBED.createErrorEmbed(user, CommonMessages.MISSING_PARAMETERS);
        }

        User userToHandle = userOption.getAsUser();
        String userToHandleId = userToHandle.getId();
        boolean isAssign = event.getSubcommandName().equals("assign");

        MessageEmbed userErrorEmbed = UserValidator.validateUserBot(user, userToHandle, TEAM_TASK_EMBED);
        if (userErrorEmbed != null) {
            return userErrorEmbed;
        }

        if (TeamTaskValidator.isUserMentionedNotPartOfTeam(result.team(), userToHandleId)) {
            return TEAM_TASK_EMBED.createErrorEmbed(user,
                    String.format(
                            TeamMessages.USER_NOT_PART_OF_TEAM,
                            result.team().getTeamName()
                    ));
        }

        if (isAssign && result.teamTask().getAssignedUsers().contains(userToHandleId)) {
            return TEAM_TASK_EMBED.createErrorEmbed(user,
                    String.format(
                            TeamMessages.USER_ALREADY_ASSIGNED_TASK,
                            userToHandle.getAsMention()
                    ));
        } else if (!isAssign && !result.teamTask().getAssignedUsers().contains(userToHandleId)) {
            return TEAM_TASK_EMBED.createErrorEmbed(user,
                    String.format(
                            TeamMessages.USER_NOT_ASSIGNED_TASK,
                            userToHandle.getAsMention()
                    ));
        }

        TeamTaskHelper.updateAssignedUsers(result.teamTask(), userToHandleId, isAssign);
        TEAM_TASK_SERVICE.update(result.teamTask());

        return TEAM_TASK_EMBED.createTeamAssignedUsersUpdateEmbed(
                user,
                isAssign,
                userToHandle,
                result.teamTask()
        );

    }
}