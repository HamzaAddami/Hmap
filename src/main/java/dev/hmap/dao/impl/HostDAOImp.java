package dev.hmap.dao.impl;

import dev.hmap.dao.base.HostDAO;
import dev.hmap.model.Host;

import java.util.List;

public class HostDAOImp implements HostDAO<Host, Integer> {

    @Override
    public Host save(Host host) {
        return null;
    }

    @Override
    public List<Host> findAll() {
        return List.of();
    }

    @Override
    public void delete(Integer id) {

    }
}
