package mist.mystralix.infrastructure.repository.base;

import java.util.ArrayList;

public interface BaseRepository<T> {

    void create(T baseObject);

    T findByUUID(String uuid);

    T findByDiscordIDAndUUID(String userDiscordID, String uuid);

    T findByDiscordIDAndID(String userDiscordID, int id);

    void update(T baseObject);

    void delete(T baseObject);

    ArrayList<T> readAll(String userDiscordID);

}