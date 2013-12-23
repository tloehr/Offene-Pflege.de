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
import java.util.ArrayList;
import java.util.Collections;
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
        long statid = OPDE.getLocalProps().containsKey("station") ? Long.parseLong(OPDE.getLocalProps().getProperty("station")) : 1l;
        Vector<Homes> homes = new Vector<Homes>(OPDE.getHomes().values());
        cmb.setModel(new DefaultComboBoxModel(homes));

        Station station = null;
        for (int h = 0; h < homes.size(); h++) {
            for (int s = 0; s < homes.get(h).getStations().size(); s++) {
                if (statid == homes.get(h).getStations().get(s).getStatID().longValue()) {
                    station = homes.get(h).getStations().get(s);
                    break;
                }
            }
            if (station != null) break;
        }
        if (station != null)
            cmb.setSelectedItem(station.getHome());
    }

    public static List<Homes> getAll() {
        List<Homes> listHomes = new ArrayList<Homes>(OPDE.getHomes().values());
        Collections.sort(listHomes);
        return listHomes;
    }



    public static void initHomes() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT e FROM Homes e ORDER BY e.shortname");
        List<Homes> listHomes = query.getResultList();
        for (Homes home : listHomes) {
            OPDE.getHomes().put(home.getEID(), home);
        }
        em.close();
    }
}
