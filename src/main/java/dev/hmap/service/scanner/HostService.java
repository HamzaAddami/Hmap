package dev.hmap.service.scanner;

import dev.hmap.config.ScanRepository;
import dev.hmap.dao.base.HostDAO;
import dev.hmap.dao.impl.HostDAOImp;
import dev.hmap.model.Host;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

public class HostService {

    @FunctionalInterface
    private interface TransactionalAction<T>{
        T execute(HostDAO dao, EntityManager em);
    }

    private <T> T runTransaction(TransactionalAction<T> action){
        try(EntityManager em = ScanRepository.getEntityManager()){
            EntityTransaction tx = em.getTransaction();
            try{
                tx.begin();
                HostDAO dao = new HostDAOImp(em);
                T result = action.execute(dao, em);
                tx.commit();
                return result;
            }catch (Exception e){
                if(tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }


    public Host registerHost(Host host) {
        return runTransaction((dao, em) -> {
            return dao.save(host);
        });
    }

    public Host updateHost(Host host) {
        return null;
    }

    public Host findById(Long id) {
        return null;
    }

    public List<Host> findAllHosts() {
        return List.of();
    }

    public void delete(Host host) {

    }

    public Optional<Host> findById(String ip) {
        return Optional.empty();
    }
}
