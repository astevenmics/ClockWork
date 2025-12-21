package mist.mystralix.infrastructure.repository.teamtask;

import mist.mystralix.domain.task.TeamTask;
import mist.mystralix.infrastructure.repository.base.BaseRepository;

import java.util.ArrayList;

public interface TeamTaskRepository extends BaseRepository<TeamTask> {

    ArrayList<TeamTask> findAllByTeamId(int teamId);

}