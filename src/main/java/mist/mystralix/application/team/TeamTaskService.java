package mist.mystralix.application.team;

import mist.mystralix.domain.task.TaskDAO;
import mist.mystralix.domain.task.TeamTask;
import mist.mystralix.infrastructure.repository.teamtask.TeamTaskRepository;
import mist.mystralix.utils.IdentifiableFetcher;

import java.util.ArrayList;

public class TeamTaskService implements IdentifiableFetcher<TeamTask> {

    private final TeamTaskRepository TEAM_TASK_REPOSITORY;

    public TeamTaskService(
            TeamTaskRepository teamTaskRepository
    ) {
        this.TEAM_TASK_REPOSITORY = teamTaskRepository;
    }

    @Override
    public void update(TeamTask teamTask) {
        TEAM_TASK_REPOSITORY.update(teamTask);
    }

    @Override
    public void delete(TeamTask teamTask) {
        TEAM_TASK_REPOSITORY.delete(teamTask);
    }

    @Override
    public TeamTask getById(
            int id
    ) {
        return TEAM_TASK_REPOSITORY.findByID(id);
    }

    @Override
    public TeamTask getByUUID(
            String uuid
    ) {
        return TEAM_TASK_REPOSITORY.findByUUID(uuid);
    }


    public void create(
            String uuid,
            String userDiscordID,
            String teamUUID,
            int teamID,
            TaskDAO taskDAO
    ) {
        TEAM_TASK_REPOSITORY.create(
                new TeamTask(
                        uuid,
                        userDiscordID,
                        taskDAO,
                        teamUUID,
                        teamID,
                        new ArrayList<>()
                )
        );
    }

    public ArrayList<TeamTask> findAllByTeamID(
            int teamID
    ) {
        return TEAM_TASK_REPOSITORY.findAllByTeamId(teamID);
    }

}
