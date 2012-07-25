package entity;

import op.OPDE;

import javax.persistence.EntityManager;
import java.util.Iterator;
import java.util.List;

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
            em.getTransaction().commit();
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

//    public static boolean refresh(Object entity) {
//        boolean success = false;
//        EntityManager em = OPDE.createEM();
//        try {
//            em.getTransaction().begin();
//            em.refresh(entity);
//            em.getTransaction().commit();
//            success = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            em.getTransaction().rollback();
//        } finally {
//            em.close();
//        }
//        return success;
//    }

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
        pattern = pattern.replaceAll("%", "");
        pattern = "%" + pattern + "%";
        return pattern;
    }
}
