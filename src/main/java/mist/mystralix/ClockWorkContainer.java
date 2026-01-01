package mist.mystralix;

import mist.mystralix.application.pagination.PaginationService;
import mist.mystralix.application.reminder.ReminderService;
import mist.mystralix.application.task.UserTaskService;
import mist.mystralix.application.team.TeamService;
import mist.mystralix.application.team.TeamTaskService;
import mist.mystralix.infrastructure.repository.reminder.DBReminderRepository;
import mist.mystralix.infrastructure.repository.reminder.ReminderRepository;
import mist.mystralix.infrastructure.repository.task.DBUserTaskRepository;
import mist.mystralix.infrastructure.repository.task.UserTaskRepository;
import mist.mystralix.infrastructure.repository.team.DBTeamRepository;
import mist.mystralix.infrastructure.repository.team.TeamRepository;
import mist.mystralix.infrastructure.repository.teamtask.DBTeamTaskRepository;
import mist.mystralix.infrastructure.repository.teamtask.TeamTaskRepository;

public class ClockWorkContainer {

    private final UserTaskService userTaskService;

    private final ReminderService reminderService;

    private final TeamService teamService;

    private final TeamTaskService teamTaskService;

    private final PaginationService paginationService;

    public ClockWorkContainer() {
        UserTaskRepository userTaskRepository = new DBUserTaskRepository();
        ReminderRepository reminderRepository = new DBReminderRepository();
        TeamRepository teamRepository = new DBTeamRepository();
        TeamTaskRepository teamTaskRepository = new DBTeamTaskRepository();

        this.userTaskService = new UserTaskService(userTaskRepository);
        this.reminderService = new ReminderService(reminderRepository);
        this.teamService = new TeamService(teamRepository);
        this.teamTaskService = new TeamTaskService(teamTaskRepository);
        this.paginationService = new PaginationService();
    }

    public UserTaskService getUserTaskService() {
        return userTaskService;
    }

    public ReminderService getReminderService() {
        return reminderService;
    }

    public TeamService getTeamService() {
        return teamService;
    }

    public TeamTaskService getTeamTaskService() {
        return teamTaskService;
    }

    public PaginationService getPaginationService() {
        return paginationService;
    }

}