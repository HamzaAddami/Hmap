package dev.hmap.dao.impl;

import dev.hmap.dao.base.ScanResultDAO;
import dev.hmap.model.ScanResult;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ScanResultDAOImp extends AbstractDAO<ScanResult, Long> implements ScanResultDAO {

    public ScanResultDAOImp(EntityManager em){
        super(em, ScanResult.class);
    }

    @Override
    public List<ScanResult> findLastScansByHost(Long hostId, int limit) {
        return em.createQuery("SELECT s FROM ScanResult s WHERE s.host.id = :hostId ORDER BY s.startTime DESC", ScanResult.class)
                .setParameter("hostId", hostId)
                .setMaxResults(limit)
                .getResultList();
    }
}
