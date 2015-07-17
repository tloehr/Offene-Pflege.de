package gui.events;

import op.OPDE;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Created by tloehr on 09.06.15.
 */
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
            OPDE.warn(Logger.getLogger(evt.getSource().getClass()), ole);
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage(ole));
        } catch (Exception e) {

            for (Throwable t = e.getCause(); t != null; t = t.getCause()) {
                if (t instanceof SQLIntegrityConstraintViolationException) {
                    OPDE.warn(Logger.getLogger(evt.getSource().getClass()), t);
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("error.sql.integrity"));
                    throw (new SQLIntegrityConstraintViolationException(t));
                }
            }

            OPDE.fatal(Logger.getLogger(evt.getSource().getClass()), e);
        } finally {
            em.close();

        }
    }
}
