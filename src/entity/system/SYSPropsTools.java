/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.system;

import entity.Users;
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

    public static void storeProp(String key, String value, Users user) {
        String namedQuery = "SYSProps.findByKeyAndUser";
        EntityManager em = OPDE.createEM();
        if (user == null) {
            namedQuery = "SYSProps.findByKey";
        }

        Query query = em.createNamedQuery(namedQuery);
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
        } catch (Exception e) {
            OPDE.fatal(e);
        }


        em.getTransaction().begin();
        try {
            if (em.contains(prop)) {
                em.merge(prop);
            } else {
                em.persist(prop);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            OPDE.fatal(e);
            em.getTransaction().rollback();
        }

        OPDE.setProp(key, value);
    }

    public static void storeProp(String key, String value) {
        storeProp(key, value, null);
    }


    public static void storeBoolean(String key, boolean value) {
        storeProp(key, value ? "true" : "false", null);
    }

    public static boolean isBoolean(String key) {
        boolean bool = false;
        if (OPDE.getProps().containsKey(key)) {
            bool = OPDE.getProps().getProperty(key).equalsIgnoreCase("true");
        }
        return bool;
    }


    /**
     * Lädt Properties aus der Tabelle OCProps ein.
     * Passend zu einer IP bzw. IP='*', wenn die Properties für alle gedacht sind.
     *
     * @param ip       String mit der IP-Adresse oder '*'
     * @param only4me, true, dann werden nur die Properties geladen, die zu der aktuellen Userkennung passen. false, alle.
     * @return Ergebnis in einem Properties Objekt.
     */
    public static Properties loadProps(Users user) {
        EntityManager em = OPDE.createEM();
        String namedQuery = "SYSProps.findByUser";

        if (user == null) {
            namedQuery = "SYSProps.findAllWOUsers";
        }

        Query query = em.createNamedQuery(namedQuery);

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

        return p;
    }

    public static void storeState(String name, JCheckBox cb) {
        storeProp(name, Boolean.toString(cb.isSelected()), OPDE.getLogin().getUser());
    }

    public static void restoreState(String name, JCheckBox cb) {
        if (OPDE.getProps().containsKey(name)) {
            cb.setSelected(OPDE.getProps().getProperty(name).equalsIgnoreCase("true"));
        } else {
            cb.setSelected(false);
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
