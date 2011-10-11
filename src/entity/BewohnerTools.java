/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import op.OPDE;
import op.tools.SYSTools;

import javax.persistence.Query;
import java.util.List;

/**
 *
 * @author tloehr
 */
public class BewohnerTools {
    public static Bewohner findByBWKennung(String bwkennung) {
        Query query = OPDE.getEM().createNamedQuery("Bewohner.findByBWKennung");
        query.setParameter("bWKennung", bwkennung);
        return (Bewohner) query.getSingleResult();
    }

    public static String getBWLabel(Bewohner bewohner) {
        return SYSTools.getBWLabel(bewohner.getBWKennung());
    }

}
