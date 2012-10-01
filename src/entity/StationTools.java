/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import op.OPDE;

/**
 * @author tloehr
 */
public class StationTools {

//    /**
//     * Setzt eine ComboBox mit der Liste der Station. Wenn möglich wird direkt die gewünschte Standard Station eingestellt.
//     *
//     * @param cmb
//     */
//    public static void setCombaoBox(JComboBox cmb) {
//        EntityManager em = OPDE.createEM();
//        Query query = em.createNamedQuery("Station.findAllSorted");
//        cmb.setModel(new DefaultComboBoxModel(new Vector<Station>(query.getResultList())));
//
//
//        Query query2 = em.createNamedQuery("Station.findByStatID");
//        query2.setParameter("statID", statid);
//        Station station = (Station) query2.getSingleResult();
//        cmb.setSelectedItem(station);
//        em.close();
//    }



    public static Station getSpecialStation(){
        long statid = OPDE.getLocalProps().containsKey("station") ? Long.parseLong(OPDE.getLocalProps().getProperty("station")) : 1l;
        return EntityTools.find(Station.class, statid);
    }



}
