package entity;

import gui.GUITools;
import gui.interfaces.NotRemovableUnlessEmpty;
import op.OPDE;
import op.threads.DisplayManager;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 16.06.11
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */
public class EntityTools {
    public static boolean persist(Object entity) {
        boolean success = false;

        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            success = true;
        } catch (Exception e) {
            OPDE.fatal(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return success;
    }

    public static <T> T find(Class<T> entity, Object id) {
        T foundEntity = null;
        EntityManager em = OPDE.createEM();

        try {
            foundEntity = em.find(entity, id);
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return foundEntity;
    }

    public static <T> T merge(T entity) {
        T mergedEntity = null;
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            mergedEntity = em.merge(entity);
            em.lock(mergedEntity, LockModeType.OPTIMISTIC);
            em.getTransaction().commit();
        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                OPDE.getMainframe().emptyFrame();
                OPDE.getMainframe().afterLogin();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return mergedEntity;
    }

//    public static <T> T store(T entity) {
//        boolean success = false;
//        EntityManager em = OPDE.createEM();
//        try {
//            em.getTransaction().begin();
//            if (em.contains(entity)) {
//                em.merge(entity);
//            } else {
//                em.persist(entity);
//            }
//            em.getTransaction().commit();
//            em.refresh(entity);
//            success = true;
//        } catch (Exception e) {
//            OPDE.fatal(e);
//            em.getTransaction().rollback();
//        }
//        return success;
//    }

    public static boolean delete(Object entity) {
        boolean success = false;
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            em.remove(em.merge(entity));
            em.getTransaction().commit();
            success = true;
        } catch (Exception e) {
            OPDE.fatal(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return success;
    }

    public static <T> T refresh(T entity) {

        EntityManager em = OPDE.createEM();
        try {
            em.refresh(em.merge(entity));
//            success = true;
        } catch (Exception e) {
            Logger.getLogger(EntityTools.class).error(e);
        } finally {
            em.close();
        }
        return entity;
    }

    /**
     * Erzeugt einen String, der die PrimärSchlüssel durch komma getrennt enthält.
     * Ist ein Workaround für Queries mit dem Schlüsselwort IN.
     *
     * @param entities
     * @return
     */
    public static String getIDList(List entities) {
        EntityManager em = OPDE.createEM();
        String list = "";
        Iterator it = entities.iterator();
        while (it.hasNext()) {
            list += em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(it.next());
            list += it.hasNext() ? "," : "";
        }
        em.close();
        return list;
    }

    public static String getMySQLsearchPattern(String pattern) {
        pattern = pattern.trim().replaceAll("%", "");
        pattern = "%" + pattern + "%";
        return pattern;
    }


    /**
     * checks if an entity may be deleted due to the constraints of the @NotRemovableUnlessEmpty annotation
     * @param entity
     * @return
     */
    public static String mayBeDeleted(Object entity) {
        HashSet<String> mayBeDeleted = new HashSet<>();

        Field[] fields = entity.getClass().getDeclaredFields();
        for (final Field field : fields) {
            if (field.isAnnotationPresent(NotRemovableUnlessEmpty.class)) {
                NotRemovableUnlessEmpty annotation = field.getAnnotation(NotRemovableUnlessEmpty.class);
                try {
                    if (PropertyUtils.getProperty(entity, field.getName()) instanceof Collection) {
                        if (!((Collection) PropertyUtils.getProperty(entity, field.getName())).isEmpty()){
                            mayBeDeleted.add(annotation.message());
                        }
                    }
                } catch (Exception e) {
                    OPDE.fatal(Logger.getLogger(entity.getClass()), e);
                }
            }
        }

        return GUITools.createStringListFrom(mayBeDeleted);
    }
}
