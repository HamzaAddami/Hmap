package dev.hmap.dao.impl;

import dev.hmap.dao.base.HostDAO;
import dev.hmap.model.Host;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class HostDAOImp extends AbstractDAO<Host, Long> implements HostDAO {

    public HostDAOImp(EntityManager em) {
        super(em, Host.class);
    }

    @Override
    public Optional<Host> findByIp(String ip) {
        return em.createQuery("SELECT h FROM Host h WHERE h.ipString = :ip", Host.class)
                .setParameter("ip", ip)
                .getResultStream()
                .findFirst();
    }

}
