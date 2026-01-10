package dev.hmap.service.scanner;

import dev.hmap.config.ScanRepository;
import dev.hmap.dao.base.ScanResultDAO;
import dev.hmap.dao.impl.ScanResultDAOImp;
import dev.hmap.model.Host;
import dev.hmap.model.ScanResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ScanResultService {

    @FunctionalInterface
    public interface TransactionalAction<T> {
        T execute(ScanResultDAO dao, EntityManager em);
    }

    public <T> T runTransaction(TransactionalAction<T> action) {
        try(EntityManager em = ScanRepository.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try{
                tx.begin();
                ScanResultDAO dao = new ScanResultDAOImp(em);
                T result = action.execute(dao, em);
                tx.commit();
                return result;
            }catch (Exception e){
                if(tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    public ScanResult registerScanResult(ScanResult sr) {
        return runTransaction((dao, em) -> {
            return dao.save(sr);
        });
    }

    public ScanResult updateScanResult(ScanResult sr) {
        return runTransaction((dao, em) -> {
            return dao.update(sr);
        });
    }

    public void deleteScanResult(ScanResult sr) {
         runTransaction((dao, em) -> {
            dao.delete(sr);
            return null;
        });
    }

    public List<ScanResult> getLastScansByHost(Host host, int limit) {
        return runTransaction((dao, em) -> {
            return dao.findLastScansByHost(host, limit);
        });
    }
 }
