package mist.mystralix.Database;

import java.util.List;

public interface BaseRepository<T> {

    void create(T baseObject);

    T findByUUID(String userDiscordID, String uuid);

    T findByID(String userDiscordID, int id);

    void update(T baseObject);

    void delete(T baseObject);

    List<T> readAll(String userDiscordID);

}
