/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import com.mysql.jdbc.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import op.OPDE;

/**
 *
 * @author tloehr
 */
public class SYSPropsTools {

    public static void storeProp(String key, String value, Users user) {
        String namedQuery = "SYSProps.findByKeyAndUser";

        if (user == null) {
            namedQuery = "SYSProps.findByKey";
        }

        Query query = OPDE.getEM().createNamedQuery(namedQuery);
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


        OPDE.getEM().getTransaction().begin();
        try {
            if (OPDE.getEM().contains(prop)) {
                OPDE.getEM().merge(prop);
            } else {
                OPDE.getEM().persist(prop);
            }
            OPDE.getEM().getTransaction().commit();
        } catch (Exception e) {
            OPDE.fatal(e);
            OPDE.getEM().getTransaction().rollback();
        }

        OPDE.setProp(key, value);
    }

    public static void storeProp(String key, String value) {
        storeProp(key, value, null);
    }
    

    /**
     * Lädt Properties aus der Tabelle OCProps ein.
     * Passend zu einer IP bzw. IP='*', wenn die Properties für alle gedacht sind.
     * @param ip String mit der IP-Adresse oder '*'
     * @param only4me, true, dann werden nur die Properties geladen, die zu der aktuellen Userkennung passen. false, alle.
     * @return Ergebnis in einem Properties Objekt.
     *
     */
    public static Properties loadProps(Users user) {

        String namedQuery = "SYSProps.findByUser";

        if (user == null) {
            namedQuery = "SYSProps.findAllWOUsers";
        }
        
        Query query = OPDE.getEM().createNamedQuery(namedQuery);
        
        if (user != null) {
            query.setParameter("user", user);
        }

        List<SYSProps> props = (List<SYSProps>) query.getResultList();
        Properties p = new Properties();
        Iterator<SYSProps> it = props.iterator();
        while (!props.isEmpty() && it.hasNext()){
            SYSProps prop = it.next();
            p.put(prop.getKey(), prop.getValue());
        }
        
        return p;
    }
}
