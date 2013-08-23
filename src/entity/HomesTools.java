/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * @author tloehr
 */
public class HomesTools {

    public static ListCellRenderer getRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
                String text;
                if (o == null) {
                    text = OPDE.lang.getString("misc.commands.>>noselection<<");
                } else if (o instanceof Homes) {
                    Homes home = (Homes) o;
                    text = home.getShortname();
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, b, b1);
            }
        };
    }

    /**
     * Erstellt eine Textdarstellung der betreffenden Einrichtung. Kann man für Ausdrucke und so brauchen.
     *
     * @param einrichtung
     * @return
     */
    public static String getAsText(Homes einrichtung) {
        return einrichtung.getName() + ", " + einrichtung.getStreet() + ", " + einrichtung.getZIP() + " " + einrichtung.getCity() + ", Tel.: " + einrichtung.getTel() + ", Fax.: " + einrichtung.getFax();
    }

    public static String getAsTextForTX(Homes einrichtung) {
        return einrichtung.getName() + "\n" + einrichtung.getStreet() + "\n" + einrichtung.getZIP() + " " + einrichtung.getCity() + "\nTel.: " + einrichtung.getTel() + "\nFax.: " + einrichtung.getFax();
    }

    /**
     * Setzt eine ComboBox mit der Liste der Homes. Wenn möglich wird direkt die eigene Einrichtung (abhängig von der Standard-Station) eingestellt.
     *
     * @param cmb
     */
    public static void setComboBox(JComboBox cmb) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT e FROM Homes e ORDER BY e.shortname");
        cmb.setModel(new DefaultComboBoxModel(new Vector<Homes>(query.getResultList())));

        long statid = OPDE.getLocalProps().containsKey("station") ? Long.parseLong(OPDE.getLocalProps().getProperty("station")) : 1l;

        Query query2 = em.createQuery("SELECT s FROM Station s WHERE s.statID = :statID");
        query2.setParameter("statID", statid);
        Station station = (Station) query2.getSingleResult();
        em.close();
        cmb.setSelectedItem(station.getHome());
    }

    public static List<Homes> getAll() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT e FROM Homes e ORDER BY e.shortname");
        List<Homes> listHomes = query.getResultList();
        em.close();

        return listHomes;
    }
}
