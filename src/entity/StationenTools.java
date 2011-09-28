/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import op.OPDE;

import javax.persistence.Query;
import javax.swing.*;
import java.util.Vector;

/**
 * @author tloehr
 */
public class StationenTools {

    /**
     * Setzt eine ComboBox mit der Liste der Stationen. Wenn möglich wird direkt die gewünschte Standard Station eingestellt.
     *
     * @param cmb
     */
    public static void setComboBox(JComboBox cmb) {
        Query query = OPDE.getEM().createNamedQuery("Stationen.findAllSorted");
        cmb.setModel(new DefaultComboBoxModel(new Vector<Stationen>(query.getResultList())));

        //TODO: Kandidat für SYSProps
        long statid = OPDE.getLocalProps().containsKey("station") ? Long.parseLong(OPDE.getLocalProps().getProperty("station")) : 1l;

        Query query2 = OPDE.getEM().createNamedQuery("Stationen.findByStatID");
        query2.setParameter("statID", statid);
        Stationen station = (Stationen) query2.getSingleResult();
        cmb.setSelectedItem(station);
    }

}
