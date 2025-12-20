package mist.mystralix.application.task;

import mist.mystralix.domain.task.Task;
import mist.mystralix.domain.task.TaskDAO;
import mist.mystralix.infrastructure.repository.task.TaskRepository;
import mist.mystralix.utils.IdentifiableFetcher;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class TaskService implements IdentifiableFetcher<Task> {

    private final TaskRepository TASK_REPOSITORY;

    public TaskService(TaskRepository taskRepository) {
        this.TASK_REPOSITORY = taskRepository;
    }

    public void addTask(TaskDAO task, User user, String uuid) {

        TASK_REPOSITORY.create(
                new Task(
                        uuid,
                        user.getId(),
                        task)
        );
    }

    public ArrayList<Task> getUserTasks(User user) {
        return TASK_REPOSITORY.readAll(user.getId());
    }

    public Task getUserTask(String userDiscordID, String uuid) {
        return TASK_REPOSITORY.findByDiscordIDAndUUID(userDiscordID, uuid);
    }

    public Task getUserTask(String userDiscordID, int id) {
        return TASK_REPOSITORY.findByDiscordIDAndID(userDiscordID, id);
    }

    public void updateUserTask(Task task) {
        TASK_REPOSITORY.update(task);
    }

    public void deleteUserTask(Task task) {
        TASK_REPOSITORY.delete(task);
    }

    @Override
    public Task fetchByUserIDAndObjectID(String userDiscordId, int id) {
        return getUserTask(userDiscordId, id);
    }

    @Override
    public Task fetchByUserIDAndObjectUUID(String userDiscordId, String objectUUID) {
        return getUserTask(userDiscordId, objectUUID);
    }
}