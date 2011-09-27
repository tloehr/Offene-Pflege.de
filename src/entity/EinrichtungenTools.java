/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.Vector;
import javax.persistence.Query;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import op.OPDE;

/**
 *
 * @author tloehr
 */
public class EinrichtungenTools {

    /**
     * Erstellt eine Textdarstellung der betreffenden Einrichtung. Kann man für Ausdrucke und so brauchen.
     * @param einrichtung
     * @return
     */
    public static String getAsText(Einrichtungen einrichtung) {
        return einrichtung.getBezeichnung() + ", " + einrichtung.getStrasse() + ", " + einrichtung.getPlz() + " " + einrichtung.getOrt() + ", Tel.: " + einrichtung.getTel() + ", Fax.: " + einrichtung.getFax();
    }

    /**
     * Setzt eine ComboBox mit der Liste der Einrichtungen. Wenn möglich wird direkt die eigene Einrichtung (abhängig von der Standard-Station) eingestellt.
     * @param cmb
     */
    public static void setComboBox(JComboBox cmb) {

        Query query = OPDE.getEM().createNamedQuery("Einrichtungen.findAll");
        cmb.setModel(new DefaultComboBoxModel(new Vector<Einrichtungen>(query.getResultList())));

        long statid = OPDE.getLocalProps().containsKey("station") ? Long.parseLong(OPDE.getLocalProps().getProperty("station")) : 1l;

        Query query2 = OPDE.getEM().createNamedQuery("Stationen.findByStatID");
        query2.setParameter("statID", statid);
        Stationen station = (Stationen) query2.getSingleResult();
        cmb.setSelectedItem(station.getEinrichtung());
    }
}
