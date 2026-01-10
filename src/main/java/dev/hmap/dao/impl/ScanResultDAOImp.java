package dev.hmap.dao.impl;

import dev.hmap.dao.base.ScanResultDAO;
import dev.hmap.model.Host;
import dev.hmap.model.ScanResult;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ScanResultDAOImp extends AbstractDAO<ScanResult, Long> implements ScanResultDAO {

    public ScanResultDAOImp(EntityManager em){
        super(em, ScanResult.class);
    }

    @Override
    public List<ScanResult> findLastScansByHost(Host host, int limit) {
        return em.createQuery(
                        "SELECT DISTINCT s FROM ScanResult s " +
                                "LEFT JOIN FETCH s.scannedPorts " +
                                "WHERE s.host.id = :hostId " +
                                "ORDER BY s.startTime DESC", ScanResult.class)
                .setParameter("hostId", host.getId())
                .setMaxResults(limit)
                .getResultList();
    }
}
