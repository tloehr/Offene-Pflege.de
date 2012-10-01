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
public class HomesTools {

    /**
     * Erstellt eine Textdarstellung der betreffenden Einrichtung. Kann man für Ausdrucke und so brauchen.
     *
     * @param einrichtung
     * @return
     */
    public static String getAsText(Homes einrichtung) {
        return einrichtung.getBezeichnung() + ", " + einrichtung.getStrasse() + ", " + einrichtung.getPlz() + " " + einrichtung.getOrt() + ", Tel.: " + einrichtung.getTel() + ", Fax.: " + einrichtung.getFax();
    }

    /**
     * Setzt eine ComboBox mit der Liste der Homes. Wenn möglich wird direkt die eigene Einrichtung (abhängig von der Standard-Station) eingestellt.
     *
     * @param cmb
     */
    public static void setComboBox(JComboBox cmb) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Einrichtungen.findAll");
        cmb.setModel(new DefaultComboBoxModel(new Vector<Homes>(query.getResultList())));

        long statid = OPDE.getLocalProps().containsKey("station") ? Long.parseLong(OPDE.getLocalProps().getProperty("station")) : 1l;

        Query query2 = em.createNamedQuery("Stationen.findByStatID");
        query2.setParameter("statID", statid);
        Station station = (Station) query2.getSingleResult();
        em.close();
        cmb.setSelectedItem(station.getEinrichtung());
    }
}
