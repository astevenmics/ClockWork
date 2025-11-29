package mist.mystralix;

import mist.mystralix.Database.DBTaskRepository;
import mist.mystralix.Database.TaskRepository;
import mist.mystralix.Objects.Task.TaskService;

public class ClockWorkContainer {

    private final TaskService taskService;

    public ClockWorkContainer() {
        TaskRepository taskRepository = new DBTaskRepository();
        this.taskService = new TaskService(taskRepository);
    }

    public TaskService getTaskService() {
        return taskService;
    }

}
