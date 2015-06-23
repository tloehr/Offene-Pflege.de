package gui.events;

import op.OPDE;
import op.threads.DisplayManager;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by tloehr on 09.06.15.
 */
public class JPADataChangeListener<T> implements DataChangeListener<T> {

    //    private final GenericClosure<T> afterGlow;
    private final DataChangeListener<T> postProcessing;

//    public JPADataChangeListener(GenericClosure<T> afterGlow) {
//        this.afterGlow = afterGlow;
//        postProcessing = null;
//    }


    public JPADataChangeListener(DataChangeListener<T> postProcessing) {
        this.postProcessing = postProcessing;
//        this.afterGlow = null;
    }

    @Override
    public void dataChanged(DataChangeEvent<T> evt) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        T myEntity = null;

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
            OPDE.fatal(Logger.getLogger(evt.getSource().getClass()), e);
        } finally {
            em.close();

        }
    }
}
