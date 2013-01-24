/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.util.ArrayList;

/**
 * @author tloehr
 */
public class StationTools {

    public static DefaultComboBoxModel getAll4Combobox(boolean withNullElement) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT s FROM Station s ORDER BY s.name ");
        ArrayList<Station> listStat = new ArrayList<Station>(query.getResultList());
        if (withNullElement) listStat.add(0, null);
        DefaultComboBoxModel result = new DefaultComboBoxModel(listStat.toArray());
        em.close();
        return result;
    }


    /**
     * returns the station where the current host is located.
     * @return
     */
    public static Station getStationForThisHost() {
        long statid = OPDE.getLocalProps().containsKey("station") ? Long.parseLong(OPDE.getLocalProps().getProperty("station")) : 1l;
        return EntityTools.find(Station.class, statid);
    }


}
