package de.offene_pflege.gui.events;

import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.DisplayMessage;
import lombok.extern.log4j.Log4j2;


import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Created by tloehr on 09.06.15.
 */
@Log4j2
public class JPADataChangeListener<T> implements DataChangeListener<T> {

    private final DataChangeListener<T> postProcessing;

    public JPADataChangeListener(DataChangeListener<T> postProcessing) {
        this.postProcessing = postProcessing;
    }

    @Override
    public void dataChanged(DataChangeEvent<T> evt) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLIntegrityConstraintViolationException {
        T myEntity;

        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            myEntity = em.merge(evt.getData());
            em.lock(myEntity, LockModeType.OPTIMISTIC);
            em.getTransaction().commit();

            // Passes this on to the postProcessing
            evt.setData(myEntity);
            postProcessing.dataChanged(evt);
        } catch (OptimisticLockException ole) {
            em.getTransaction().rollback();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.warn(ole);
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage(ole));
        } catch (Exception e) {

            for (Throwable t = e.getCause(); t != null; t = t.getCause()) {
                if (t instanceof SQLIntegrityConstraintViolationException) {
                    log.warn( t);
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("error.sql.integrity"));
                    throw (new SQLIntegrityConstraintViolationException(t));
                }
            }

            OPDE.fatal(e);
        } finally {
            em.close();

        }
    }
}
