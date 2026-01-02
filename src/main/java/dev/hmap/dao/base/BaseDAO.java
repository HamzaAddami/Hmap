package dev.hmap.dao.base;

import java.util.List;
import java.util.Optional;

public interface BaseDAO<T, ID>{
    T save(T t);
    T update(T t);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(T t);
}
