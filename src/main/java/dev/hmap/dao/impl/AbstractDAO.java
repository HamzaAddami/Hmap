package dev.hmap.dao.impl;


import dev.hmap.dao.base.BaseDAO;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;


public abstract class AbstractDAO<T, ID> implements BaseDAO<T, ID> {

    protected final EntityManager em;
    private final Class<T> entityClass;

    public AbstractDAO(EntityManager em, Class<T> entityClass){
        this.em = em;
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity){
        em.persist(entity);
        return entity;
    }

    @Override
    public T update(T entity) {
        return em.merge(entity);
    }

    @Override
    public void delete(T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(em.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        return em.createQuery("FROM " + entityClass.getSimpleName(), entityClass).getResultList();
    }

}
