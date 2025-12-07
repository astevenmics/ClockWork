package mist.mystralix.infrastructure.repository.base;

import java.util.List;

public interface BaseRepository<T> {

    void create(T baseObject);

    T findByUUID(String uuid);

    T findByDiscordIDAndUUID(String userDiscordID, String uuid);

    T findByDiscordIDAndID(String userDiscordID, int id);

    void update(T baseObject);

    void delete(T baseObject);

    List<T> readAll(String userDiscordID);

}