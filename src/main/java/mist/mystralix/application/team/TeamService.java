package mist.mystralix.application.team;

import mist.mystralix.domain.task.Task;
import mist.mystralix.domain.team.Team;
import mist.mystralix.infrastructure.repository.task.TaskRepository;
import mist.mystralix.infrastructure.repository.team.TeamRepository;
import mist.mystralix.utils.IdentifiableFetcher;

import java.util.ArrayList;
import java.util.UUID;

public class TeamService implements IdentifiableFetcher<Team> {

    private final TeamRepository TEAM_REPOSITORY;
    private final TaskRepository TASK_REPOSITORY;

    public TeamService(
            TeamRepository teamRepository,
            TaskRepository taskRepository
    ) {
        this.TEAM_REPOSITORY = teamRepository;
        this.TASK_REPOSITORY = taskRepository;
    }

    public Team create(
            String teamName,
            ArrayList<String> moderators
    ) {

        String uuid = UUID.randomUUID().toString();

        Team newTeam = new Team(
                uuid,
                teamName,
                moderators,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        TEAM_REPOSITORY.create(newTeam);

        return TEAM_REPOSITORY.findByUUID(uuid);
    }

    public void update(Team team) {
        TEAM_REPOSITORY.update(team);
    }

    public ArrayList<Team> getUserTeams(String userDiscordID) {
        return TEAM_REPOSITORY.readAll(userDiscordID);
    }

    public ArrayList<Task> getTeamTasks(String userDiscordID, int objectID) {
        Team team = fetchByUserIDAndObjectID(userDiscordID, objectID);
        ArrayList<String> tasksUUID = team.getTasksUUID();
        ArrayList<Task> tasks = new ArrayList<>();

        for (String taskUUID : tasksUUID) {
            tasks.add(TASK_REPOSITORY.findByUUID(taskUUID));
        }

        return tasks;
    }

    public Team findByID(int id) {
        return TEAM_REPOSITORY.findByID(id);
    }

    @Override
    public Team fetchByUserIDAndObjectID(String userDiscordId, int objectID) {
        return TEAM_REPOSITORY.findByDiscordIDAndID(userDiscordId, objectID);
    }

    @Override
    public Team fetchByUserIDAndObjectUUID(String userDiscordId, String objectUUID) {
        return TEAM_REPOSITORY.findByDiscordIDAndUUID(userDiscordId, objectUUID);
    }
}