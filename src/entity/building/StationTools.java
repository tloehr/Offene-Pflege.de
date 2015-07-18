/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.building;

import entity.EntityTools;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
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
     *
     * @return
     */
    public static Station getStationForThisHost() {
        long statid = OPDE.getLocalProps().containsKey(SYSPropsTools.KEY_STATION) ? Long.parseLong(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_STATION)) : 1l;
        return EntityTools.find(Station.class, statid);
    }


    public static DefaultMutableTreeNode getCompleteStructure() {

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(SYSTools.xx("misc.commands.noselection"));

        EntityManager em = OPDE.createEM();
        Query queryHomes = em.createQuery("SELECT h FROM Homes h ORDER BY h.name ");
        ArrayList<Homes> listHomes = new ArrayList<Homes>(queryHomes.getResultList());
        em.close();

        for (Homes home : listHomes) {
            DefaultMutableTreeNode homeNode = new DefaultMutableTreeNode(home);

            if (home.getStations().size() > 1) {
                for (Station station : home.getStations()) {
                    homeNode.add(new DefaultMutableTreeNode(station));
                }
            }

            root.add(homeNode);
        }

        return root;
    }


}
