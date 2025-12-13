package mist.mystralix;

import mist.mystralix.application.team.TeamService;
import mist.mystralix.infrastructure.repository.reminder.DBReminderRepository;
import mist.mystralix.infrastructure.repository.task.DBTaskRepository;
import mist.mystralix.infrastructure.repository.reminder.ReminderRepository;
import mist.mystralix.infrastructure.repository.task.TaskRepository;
import mist.mystralix.application.reminder.ReminderService;
import mist.mystralix.application.task.TaskService;
import mist.mystralix.infrastructure.repository.team.DBTeamRepository;

public class ClockWorkContainer {

    private final TaskService taskService;

    private final ReminderService reminderService;

    private final TeamService teamService;

    public ClockWorkContainer() {
        TaskRepository taskRepository = new DBTaskRepository();
        ReminderRepository reminderRepository = new DBReminderRepository();
        DBTeamRepository teamRepository = new DBTeamRepository();

        this.taskService = new TaskService(taskRepository);
        this.reminderService = new ReminderService(reminderRepository);
        this.teamService = new TeamService(teamRepository, taskRepository);
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

}