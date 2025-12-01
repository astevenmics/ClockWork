package mist.mystralix.utils;

public interface IdentifiableFetcher<T> {

    T fetchByUserIDAndObjectID(String userDiscordId, int objectID);

}
