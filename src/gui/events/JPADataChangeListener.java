package gui.events;

import gui.interfaces.GenericClosure;
import op.OPDE;
import op.threads.DisplayManager;
import org.apache.commons.collections.Closure;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;

/**
 * Created by tloehr on 09.06.15.
 */
public class JPADataChangeListener<T> implements DataChangeListener<T> {

    private final GenericClosure<T> afterGlow;

    public JPADataChangeListener(GenericClosure<T> afterGlow) {
        this.afterGlow = afterGlow;
    }

    @Override
    public void dataChanged(DataChangeEvent<T> evt) {
        T myEntity = null;

        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            myEntity = em.merge(evt.getData());
            em.lock(myEntity, LockModeType.OPTIMISTIC);
            em.getTransaction().commit();
        } catch (OptimisticLockException ole) {
            em.getTransaction().rollback();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.warn(Logger.getLogger(evt.getSource().getClass()), ole);
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage(ole));
        } catch (Exception e) {
            OPDE.fatal(Logger.getLogger(evt.getSource().getClass()), e);
        } finally {
            em.close();
            afterGlow.execute(myEntity);
        }
    }
}
