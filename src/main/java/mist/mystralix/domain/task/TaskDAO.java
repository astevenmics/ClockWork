package mist.mystralix.domain.task;

import mist.mystralix.domain.enums.TaskStatus;

public class TaskDAO {

    private String title;

    private String description;

    private TaskStatus taskStatus;

    public TaskDAO(
            String title,
            String description,
            TaskStatus taskStatus
    ) {
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
}