package mist.mystralix;

import mist.mystralix.application.reminder.ReminderService;
import mist.mystralix.application.task.TaskService;
import mist.mystralix.application.team.TeamService;
import mist.mystralix.application.team.TeamTaskService;
import mist.mystralix.infrastructure.repository.reminder.DBReminderRepository;
import mist.mystralix.infrastructure.repository.reminder.ReminderRepository;
import mist.mystralix.infrastructure.repository.task.DBTaskRepository;
import mist.mystralix.infrastructure.repository.task.TaskRepository;
import mist.mystralix.infrastructure.repository.team.DBTeamRepository;
import mist.mystralix.infrastructure.repository.team.TeamRepository;
import mist.mystralix.infrastructure.repository.teamtask.DBTeamTaskRepository;
import mist.mystralix.infrastructure.repository.teamtask.TeamTaskRepository;

public class ClockWorkContainer {

    private final TaskService taskService;

    private final ReminderService reminderService;

    private final TeamService teamService;

    private final TeamTaskService teamTaskService;

    public ClockWorkContainer() {
        TaskRepository taskRepository = new DBTaskRepository();
        ReminderRepository reminderRepository = new DBReminderRepository();
        TeamRepository teamRepository = new DBTeamRepository();
        TeamTaskRepository teamTaskRepository = new DBTeamTaskRepository();

        this.taskService = new TaskService(taskRepository);
        this.reminderService = new ReminderService(reminderRepository);
        this.teamService = new TeamService(teamRepository);
        this.teamTaskService = new TeamTaskService(teamTaskRepository);

    }

    public TaskService getTaskService() {
        return taskService;
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

}