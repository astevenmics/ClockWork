package mist.mystralix.application.team;

import mist.mystralix.infrastructure.repository.task.TaskRepository;
import mist.mystralix.infrastructure.repository.team.TeamRepository;
import mist.mystralix.infrastructure.repository.teamtask.TeamTaskRepository;

public class TeamTaskService {

    private final TeamTaskRepository TEAM_TASK_REPOSITORY;
    private final TeamRepository TEAM_REPOSITORY;
    private final TaskRepository TASK_REPOSITORY;

    public TeamTaskService(
            TeamTaskRepository teamTaskRepository,
            TeamRepository teamRepository,
            TaskRepository taskRepository
    ) {
        this.TEAM_TASK_REPOSITORY = teamTaskRepository;
        this.TEAM_REPOSITORY = teamRepository;
        this.TASK_REPOSITORY = taskRepository;
    }

}
