package mist.mystralix.infrastructure.repository.base;

import java.util.ArrayList;

public interface BaseRepository<T> {

    void create(T baseObject);

    T findByID(int id);

    T findByUUID(String uuid);

    void update(T baseObject);

    void delete(T baseObject);

    default ArrayList<T> readAll(String userDiscordID) {
        return new ArrayList<>();
    }

    ;

}