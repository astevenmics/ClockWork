package mist.mystralix.Objects;

import mist.mystralix.Enums.TaskStatus;

public class Task {

    public String title;
    public String description;
    public TaskStatus taskStatus;

    public Task(
            String title,
            String description,
            TaskStatus taskStatus
    ) {
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
    }

}