package dev.hmap.dao.impl;

import dev.hmap.dao.base.PortDAO;
import dev.hmap.model.Port;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PortDAOImp extends AbstractDAO<Port, Long> implements PortDAO {


    public PortDAOImp(EntityManager em) {
        super(em, Port.class);
    }

    @Override
    public List<Port> findOpenPortsByHost(Long hostId) {
        return em.createQuery("SELECT p FROM Port p WHERE p.host.id = :hostId AND (p.state = 'OPEN' OR p.state = 'OPEN_OR_FILTERED') ", Port.class)
                .setParameter("hostId", hostId)
                .getResultList();
    }
}
