package mist.mystralix.Objects.Task;

import mist.mystralix.Database.TaskRepository;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public void addTask(TaskDAO task, User user, String uuid) {
        repository.addTask(task, user.getId(), uuid);
    }

    public List<Task> getUserTasks(User user) {
        return repository.getAllUserTasks(user.getId());
    }

    public Task getUserTask(String uuid) {
        return repository.getTask(uuid);
    }

    public Task getUserTask(User user, int taskId) {
        return repository.getTask(user.getId(), taskId);
    }

    public void updateUserTask(User user, int taskId, Task task) {
        repository.updateUserTask(user.getId(), taskId, task);
    }

    public void deleteUserTask(User user, Task task) {
        repository.deleteUserTask(user.getId(), task);
    }

}
