/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author tloehr
 */
public class StationenTools {

//    /**
//     * Setzt eine ComboBox mit der Liste der Stationen. Wenn möglich wird direkt die gewünschte Standard Station eingestellt.
//     *
//     * @param cmb
//     */
//    public static void setCombaoBox(JComboBox cmb) {
//        EntityManager em = OPDE.createEM();
//        Query query = em.createNamedQuery("Stationen.findAllSorted");
//        cmb.setModel(new DefaultComboBoxModel(new Vector<Stationen>(query.getResultList())));
//
//
//        Query query2 = em.createNamedQuery("Stationen.findByStatID");
//        query2.setParameter("statID", statid);
//        Stationen station = (Stationen) query2.getSingleResult();
//        cmb.setSelectedItem(station);
//        em.close();
//    }



    public static Stationen getSpecialStation(){
        long statid = OPDE.getLocalProps().containsKey("station") ? Long.parseLong(OPDE.getLocalProps().getProperty("station")) : 1l;
        return EntityTools.find(Stationen.class, statid);
    }



}
