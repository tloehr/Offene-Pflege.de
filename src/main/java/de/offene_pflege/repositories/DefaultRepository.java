package de.offene_pflege.repositories;

import de.offene_pflege.op.OPDE;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Log4j2
public class DefaultRepository<T> {
    private Class<T> entityClass;

    public Optional<T> find(Class<T> entity, Object id) {
        T foundEntity = null;
        EntityManager em = OPDE.createEM();

        try {
            foundEntity = em.find(entity, id);
        } catch (Exception e) {
            log.fatal(e);
        } finally {
            em.close();
        }
        return foundEntity == null ? Optional.empty() : Optional.of(foundEntity);
    }

    public List<T> findAll(Class<T> entity, Object id) {
        EntityManager em = OPDE.createEM();
        return em.createQuery("Select t from " + entityClass.getSimpleName() + " t").getResultList();
    }

}
