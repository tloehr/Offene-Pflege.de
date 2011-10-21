package entity;

import op.OPDE;

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

        try {
            OPDE.getEM().getTransaction().begin();
            OPDE.getEM().persist(entity);
            OPDE.getEM().getTransaction().commit();
            success = true;
        } catch (Exception e) {
            OPDE.getEM().getTransaction().rollback();
        }
        return success;
    }

    public static boolean merge(Object entity) {
        boolean success = false;

        try {
            OPDE.getEM().getTransaction().begin();
            OPDE.getEM().merge(entity);
            OPDE.getEM().getTransaction().commit();
            success = true;
        } catch (Exception e) {
            OPDE.getEM().getTransaction().rollback();
        }
        return success;
    }

    public static boolean store(Object entity) {
        boolean success = false;

        try {
            OPDE.getEM().getTransaction().begin();
            if (OPDE.getEM().contains(entity)) {
                OPDE.getEM().merge(entity);
            } else {
                OPDE.getEM().persist(entity);
            }
            OPDE.getEM().getTransaction().commit();
            success = true;
        } catch (Exception e) {
            OPDE.getEM().getTransaction().rollback();
        }
        return success;
    }

    public static boolean delete(Object entity) {
        boolean success = false;

        try {
            OPDE.getEM().getTransaction().begin();
            OPDE.getEM().remove(entity);
            OPDE.getEM().getTransaction().commit();
            success = true;
        } catch (Exception e) {
            OPDE.getEM().getTransaction().rollback();
        }
        return success;
    }
}
