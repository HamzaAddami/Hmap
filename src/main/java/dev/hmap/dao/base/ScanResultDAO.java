package dev.hmap.dao.base;

import java.util.List;

public interface ScanResultDAO<T> {
    T save(T t);
    List<T> findAll();
    void delete(long id);
}
