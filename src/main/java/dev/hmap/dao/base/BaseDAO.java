package dev.hmap.dao.base;

import java.util.List;

public interface BaseDAO<T, I>{
    T save(T t);
    List<T> findAll();
    void delete(I id);
}
