/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.services;


import de.offene_pflege.entity.building.Floors;
import de.offene_pflege.entity.building.Homes;
import de.offene_pflege.entity.building.Station;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Vector;

/**
 * @author tloehr
 */
public class HomesService {

    /**
     * Erstellt eine Textdarstellung der betreffenden Einrichtung. Kann man für Ausdrucke und so brauchen.
     *
     * @param einrichtung
     * @return
     */
    public static String getAsText(Homes einrichtung) {
        return einrichtung.getName() + ", " + einrichtung.getStreet() + ", " + einrichtung.getZip() + " " + einrichtung.getCity() + ", Tel.: " + einrichtung.getTel() + ", Fax.: " + einrichtung.getFax();
    }

    public static String getAsTextForTX(Homes einrichtung) {
        return einrichtung.getName() + "\n" + einrichtung.getStreet() + "\n" + einrichtung.getZip() + " " + einrichtung.getCity() + "\nTel.: " + einrichtung.getTel() + "\nFax.: " + einrichtung.getFax();
    }


    /**
     * Setzt eine ComboBox mit der Liste der Homes. Wenn möglich wird direkt die eigene Einrichtung (abhängig von der
     * Standard-Station) eingestellt.
     *
     * @param cmb
     */
    public static void setComboBox(JComboBox cmb) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT e FROM Homes e WHERE e.active = true ORDER BY e.id");
        cmb.setModel(new DefaultComboBoxModel(new Vector<Homes>(query.getResultList())));

        long statid = OPDE.getLocalProps().containsKey("station") ? Long.parseLong(OPDE.getLocalProps().getProperty("station")) : 1l;

        Query query2 = em.createQuery("SELECT s FROM Station s WHERE s.id = :statID");
        query2.setParameter("statID", statid);
        Station station = (Station) query2.getSingleResult();
        em.close();
        cmb.setSelectedItem(station.getHome());
    }
//
//    public static Homes getByPK(String eid) {
//        EntityManager em = OPDE.createEM();
//        Query query = em.createQuery("SELECT e FROM Homes e WHERE e.id = :id");
//        query.setParameter("id", eid);
//        Homes home = (Homes) query.getSingleResult();
//        em.close();
//        return home;
//    }
//

    public static Homes get() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT e FROM Homes e WHERE e.active = TRUE ORDER BY e.id");
        query.setMaxResults(1);
        Homes homes = (Homes) query.getSingleResult();

        em.close();

        return homes;
    }

    public static Homes createHome() {
        Homes home = new Homes();
        home.setId(UUID.randomUUID().toString());
        home.setName(SYSTools.xx("opde.settings.home.btnAddHome"));
        home.setStreet(SYSTools.xx("misc.msg.street"));
        home.setZip("12345");
        home.setCity(SYSTools.xx("misc.msg.city"));
        home.setTel(SYSTools.xx("misc.msg.phone"));
        home.setFax(SYSTools.xx("misc.msg.fax"));
        home.setActive(true);
        home.setColor("ffffff");
        home.setMaxcap(10);

        Station newStation = StationService.createStation(SYSTools.xx("opde.settings.home.btnAddStation"), home);
        Floors newFloor = FloorService.create(home, SYSTools.xx("opde.settings.home.btnAddFloor"));

        home.setStation(new ArrayList<>());
        home.getStation().add(newStation);
        home.setFloors(new ArrayList<>());
        home.getFloors().add(newFloor);
        return home;
    }
}
