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

    @Override
    public void update(Task task) {
        TASK_REPOSITORY.update(task);
    }

    @Override
    public void delete(Task task) {
        TASK_REPOSITORY.delete(task);
    }

    @Override
    public Task getById(int id) {
        return TASK_REPOSITORY.findByID(id);
    }

    @Override
    public Task getByUUID(String uuid) {
        return TASK_REPOSITORY.findByUUID(uuid);
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

}