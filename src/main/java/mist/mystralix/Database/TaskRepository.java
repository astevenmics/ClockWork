package mist.mystralix.Database;

import mist.mystralix.Objects.Task.Task;
import mist.mystralix.Objects.Task.TaskDAO;

import java.util.List;

public interface TaskRepository {

    void addTask(TaskDAO task, String userId, String uuid);

    List<Task> getAllUserTasks(String userId);

    Task getTask(String uuid);

    Task getTask(String userId, int taskId);

    void updateUserTask(String userId, int taskId, Task task);

    void deleteUserTask(String userId, Task task);

}
