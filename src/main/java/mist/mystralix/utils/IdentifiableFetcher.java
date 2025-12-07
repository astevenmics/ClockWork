package mist.mystralix.utils;

public interface IdentifiableFetcher<T> {

    T fetchByUserIDAndObjectID(String userDiscordId, int objectID);
    T fetchByUserIDAndObjectUUID(String userDiscordId, String objectUUID);

}