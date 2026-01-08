package dev.hmap.service.scanner.impl;

import dev.hmap.config.ScanRepository;
import dev.hmap.config.ThreadPoolManager;
import dev.hmap.dao.base.HostDAO;
import dev.hmap.dao.impl.HostDAOImp;
import dev.hmap.model.Host;
import dev.hmap.service.scanner.base.HostService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

public class HostServiceImpl implements HostService {

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

    @Override
    public Host registerHost(Host host) {
        return runTransaction((dao, em) -> {
            return dao.save(host);
        });
    }

    @Override
    public Host updateHost(Host host) {
        return null;
    }

    @Override
    public Host findById(Long id) {
        return null;
    }

    @Override
    public List<Host> findAllHosts() {
        return List.of();
    }

    @Override
    public void delete(Host host) {

    }

    @Override
    public Optional<Host> findById(String ip) {
        return Optional.empty();
    }
}
