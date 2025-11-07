package mist.mystralix.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCounter {

    public String userDiscordId;
    public int counter;

    @JsonCreator
    public UserCounter(
            @JsonProperty("userDiscordId") String userDiscordId,
            @JsonProperty("counter") int counter) {
        this.userDiscordId = userDiscordId;
        this.counter = counter;
    }

}