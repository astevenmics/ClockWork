package mist.mystralix.Objects.Task;

import mist.mystralix.Database.Task.TaskRepository;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class TaskService {

    private final TaskRepository TASK_REPOSITORY;

    public TaskService(TaskRepository taskRepository) {
        this.TASK_REPOSITORY = taskRepository;
    }

    public void addTask(TaskDAO task, User user, String uuid) {
        TASK_REPOSITORY.addTask(task, user.getId(), uuid);
    }

    public List<Task> getUserTasks(User user) {
        return TASK_REPOSITORY.getAllUserTasks(user.getId());
    }

    public Task getUserTask(String uuid) {
        return TASK_REPOSITORY.getTask(uuid);
    }

    public Task getUserTask(User user, int taskId) {
        return TASK_REPOSITORY.getTask(user.getId(), taskId);
    }

    public void updateUserTask(User user, int taskId, Task task) {
        TASK_REPOSITORY.updateUserTask(user.getId(), taskId, task);
    }

    public void deleteUserTask(User user, Task task) {
        TASK_REPOSITORY.deleteUserTask(user.getId(), task);
    }

}
