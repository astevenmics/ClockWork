package mist.mystralix.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import mist.mystralix.Enums.TaskStatus;

public class Task {

    // TODO: Auto increment once transitioned to Database
    public int id;
    public String title;
    public String description;
    public TaskStatus taskStatus;

    @JsonCreator
    public Task(
            @JsonProperty("id") int id,
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("taskStatus") TaskStatus taskStatus
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
    }

}