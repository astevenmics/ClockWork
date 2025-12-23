package mist.mystralix.utils;

public interface IdentifiableFetcher<T> {

    void update(T t);

    void delete(T t);

    T getById(int id);

    T getByUUID(String uuid);

}