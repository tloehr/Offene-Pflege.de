/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.system;

import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author tloehr
 */
public class SYSPropsTools {

    public static final String KEY_PHYSICAL_PRINTER = "printer.physical.name";
    public static final String KEY_LOGICAL_PRINTER = "printer.logical.name";
    public static final String KEY_MEDSTOCK_LABEL = "printer.medstock.label.name";

    public static void storeProp(EntityManager em, String key, String value, Users user) throws Exception {
        String jpql = "SELECT s FROM SYSProps s WHERE s.key = :key AND s.user = :user";

        if (user == null) {
            jpql = "SELECT s FROM SYSProps s WHERE s.key = :key";
        }

        Query query = em.createQuery(jpql);
        query.setParameter("key", key);

        if (user != null) {
            query.setParameter("user", user);
        }

        SYSProps prop = null;

        try {
            prop = (SYSProps) query.getSingleResult();
            prop.setValue(value);

        } catch (NoResultException nre) {
            prop = new SYSProps(key, value, user);
        }

        prop = em.merge(prop);
        OPDE.setProp(key, value);
    }

    public static void storeProp(String key, String value, Users user) {
        if (OPDE.getProps().containsKey(key) && OPDE.getProps().getProperty(key).equals(value)){
            return;
        }

        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            storeProp(em, key, value, user);
            em.getTransaction().commit();
        } catch (Exception e) {
            OPDE.fatal(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

    }

    public static void storeProp(String key, String value) {
        storeProp(key, value, null);
    }

    public static void storeProp(EntityManager em, String key, String value) throws Exception {
        storeProp(em, key, value, null);
    }


    public static void storeBoolean(String key, boolean value, Users user) {
        storeProp(key, value ? "true" : "false", user);
    }

    public static boolean isBooleanTrue(String key) {
        return isBooleanTrue(key, false);
    }

    public static boolean isBooleanTrue(String key, boolean defaultBoolean) {
        boolean bool = defaultBoolean;
        if (OPDE.getProps().containsKey(key)) {
            bool = OPDE.getProps().getProperty(key).equalsIgnoreCase("true");
        }
        return bool;
    }

    public static int getInteger(String key) {
        int i = 0;
        if (OPDE.getProps().containsKey(key)) {
            i = Integer.parseInt(OPDE.getProps().getProperty(key));
        }
        return i;
    }


    /**
     * Lädt Properties aus der Tabelle OCProps ein.
     * Passend zu einer IP bzw. IP='*', wenn die Properties für alle gedacht sind.
     */
    public static Properties loadProps(Users user) {
        EntityManager em = OPDE.createEM();
        String jpql = "SELECT s FROM SYSProps s WHERE s.user = :user";

        if (user == null) {
            jpql = "SELECT s FROM SYSProps s WHERE s.user IS NULL";
        }

        Query query = em.createQuery(jpql);

        if (user != null) {
            query.setParameter("user", user);
        }

        List<SYSProps> props = (List<SYSProps>) query.getResultList();
        Properties p = new Properties();
        Iterator<SYSProps> it = props.iterator();
        while (!props.isEmpty() && it.hasNext()) {
            SYSProps prop = it.next();
            p.put(prop.getKey(), prop.getValue());
        }

        em.close();

        return p;
    }

    public static void storeState(String name, JCheckBox cb) {
        storeProp(name, Boolean.toString(cb.isSelected()), OPDE.getLogin().getUser());
    }

    public static void storeState(String name, JToggleButton btn) {
        storeProp(name, Boolean.toString(btn.isSelected()), OPDE.getLogin().getUser());
    }

    public static void restoreState(String name, JCheckBox cb) {
        if (OPDE.getProps().containsKey(name)) {
            cb.setSelected(OPDE.getProps().getProperty(name).equalsIgnoreCase("true"));
        } else {
            cb.setSelected(false);
        }
    }

    public static void restoreState(String name, JToggleButton btn) {
        if (OPDE.getProps().containsKey(name)) {
            btn.setSelected(OPDE.getProps().getProperty(name).equalsIgnoreCase("true"));
        } else {
            btn.setSelected(false);
        }
    }

    public static void storeState(String name, JComboBox cmb) {
        storeProp(name, Integer.toString(cmb.getSelectedIndex()), OPDE.getLogin().getUser());
    }

    public static void restoreState(String name, JComboBox cmb) {
        if (OPDE.getProps().containsKey(name)) {
            int index = Integer.parseInt(OPDE.getProps().getProperty(name));
            cmb.setSelectedIndex(index);
        } else {
            cmb.setSelectedIndex(0);
        }
    }

}
