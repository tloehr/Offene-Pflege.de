/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.util.Vector;

/**
 * @author tloehr
 */
public class StationTools {

    public static DefaultComboBoxModel getAll4Combobox() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT s FROM Station s ORDER BY s.bezeichnung ");
        DefaultComboBoxModel result = new DefaultComboBoxModel(new Vector<Station>(query.getResultList()));
        em.close();
        return result;
    }


    public static Station getSpecialStation() {
        long statid = OPDE.getLocalProps().containsKey("station") ? Long.parseLong(OPDE.getLocalProps().getProperty("station")) : 1l;
        return EntityTools.find(Station.class, statid);
    }


}
