package dev.hmap.service.scanner;

import dev.hmap.config.ScanRepository;
import dev.hmap.dao.base.PortDAO;
import dev.hmap.dao.impl.PortDAOImp;
import dev.hmap.model.Port;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;


public class  PortService {

    @FunctionalInterface
    public interface TransactionalAction<T> {
        T execute(PortDAO dao, EntityManager em);
    }

    public <T> T runTransaction(TransactionalAction<T> action){
        try(EntityManager em = ScanRepository.getEntityManager()){
            EntityTransaction tx = em.getTransaction();
            try{
                tx.begin();
                PortDAO dao = new PortDAOImp(em);
                T result = action.execute(dao, em);
                tx.commit();
                return result;
            }catch (Exception e){
                if(tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }


    public Port registerPort(Port p) {
        return runTransaction((dao, em) -> {
            return dao.save(p);
        });
    }

    public Port updatePort(Port p) {
        return runTransaction((dao, em) -> {
            return dao.update(p);
        });
    }

    public void deletePort(Port p) {
        runTransaction((dao, em) -> {
            dao.delete(p);
           return null;
        });
    }
}
