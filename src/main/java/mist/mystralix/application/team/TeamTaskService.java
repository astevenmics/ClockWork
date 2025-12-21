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

    public TeamTask getById(
            int id
    ) {
        return TEAM_TASK_REPOSITORY.findByID(id);
    }

    public TeamTask getByUUID(
            String uuid
    ) {
        return TEAM_TASK_REPOSITORY.findByUUID(uuid);
    }

    public void delete(
            TeamTask teamTask
    ) {
        TEAM_TASK_REPOSITORY.delete(teamTask);
    }

    public void updateTeamTask(TeamTask teamTask) {
        TEAM_TASK_REPOSITORY.update(teamTask);
    }

    public ArrayList<TeamTask> findAllByTeamID(
            int teamID
    ) {
        return TEAM_TASK_REPOSITORY.findAllByTeamId(teamID);
    }

    @Override
    public TeamTask fetchByUserIDAndObjectID(String userDiscordId, int objectID) {
        return null;
    }

    @Override
    public TeamTask fetchByUserIDAndObjectUUID(String userDiscordId, String objectUUID) {
        return null;
    }
}
