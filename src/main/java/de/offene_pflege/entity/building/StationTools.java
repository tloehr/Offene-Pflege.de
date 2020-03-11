/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.entity.building;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

/**
 * @author tloehr
 */
public class StationTools {

    /**
     * Erstellt ein Combobox Modell aus allen aktiven Stationen.
     * Stationen aus Häusern, die nicht mehr aktiv sind, werden weggelassen.
     * @param withNullElement
     * @return
     */
    public static DefaultComboBoxModel getAll4Combobox(boolean withNullElement) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT s FROM Station s WHERE s.home.active = true ORDER BY s.name");
        ArrayList<Station> listStat = new ArrayList<Station>(query.getResultList());
        if (withNullElement) listStat.add(0, null);
        DefaultComboBoxModel result = new DefaultComboBoxModel(listStat.toArray());
        em.close();
        return result;
    }


    /**
     * returns the station where the current host is located.
     *
     * @return
     */
    public static Station getStationForThisHost() {
        long statid = OPDE.getLocalProps().containsKey(SYSPropsTools.KEY_STATION) ? Long.parseLong(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_STATION)) : 1l;
        return EntityTools.find(Station.class, statid);
    }

    public static Station createStation(String name, Homes home){
        Station station = new Station();
        station.setId(0l);
        station.setName(SYSTools.tidy(name));
        station.setHome(home);
        return station;
    }


    public static DefaultMutableTreeNode getCompleteStructure() {

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(SYSTools.xx("misc.commands.noselection"));

        EntityManager em = OPDE.createEM();
        Query queryHomes = em.createQuery("SELECT h FROM Homes h ORDER BY h.name ");
        ArrayList<Homes> listHomes = new ArrayList<Homes>(queryHomes.getResultList());
        em.close();

        for (Homes home : listHomes) {
            DefaultMutableTreeNode homeNode = new DefaultMutableTreeNode(home);

            if (home.getStation().size() > 1) {
                for (Station station : home.getStation()) {
                    homeNode.add(new DefaultMutableTreeNode(station));
                }
            }

            root.add(homeNode);
        }

        return root;
    }


}
