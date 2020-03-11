package de.offene_pflege.repositories;

import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.HasLogger;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class DefaultRepository<T> implements HasLogger {
    private Class<T> entityClass;

    public Optional<T> find(Class<T> entity, Object id) {
        T foundEntity = null;
        EntityManager em = OPDE.createEM();

        try {
            foundEntity = em.find(entity, id);
        } catch (Exception e) {
            getLogger().fatal(e);
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
