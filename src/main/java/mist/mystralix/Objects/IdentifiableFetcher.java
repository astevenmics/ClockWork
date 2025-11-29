package mist.mystralix.Objects;

public interface IdentifiableFetcher<T> {

    T fetchByUserIDAndObjectID(String userDiscordId, int objectID);

}
