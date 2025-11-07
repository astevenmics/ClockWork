package mist.mystralix.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Task {

    // TODO: Auto incremenet once transitioned to Database
    public int id;
    public String title;
    public String description;

    @JsonCreator
    public Task(
            @JsonProperty("id") int id,
            @JsonProperty("title") String title,
            @JsonProperty("description") String description
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

}