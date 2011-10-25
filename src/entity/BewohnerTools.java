/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import op.OPDE;
import op.tools.SYSCalendar;

import javax.persistence.Query;
import javax.swing.*;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author tloehr
 */
public class BewohnerTools {
    public static Bewohner findByBWKennung(String bwkennung) {
        Query query = OPDE.getEM().createNamedQuery("Bewohner.findByBWKennung");
        query.setParameter("bWKennung", bwkennung);
        return (Bewohner) query.getSingleResult();
    }

    public static JLabel getBWLabel(Bewohner bewohner) {
        JLabel lblBW = new JLabel();
        lblBW.setFont(new java.awt.Font("Dialog", 1, 18));
        lblBW.setHorizontalAlignment(SwingConstants.LEADING);
        lblBW.setForeground(new java.awt.Color(255, 51, 0));
        lblBW.setText(getBWLabelText(bewohner));
        return lblBW;
    }

    public static String getBWLabelText(Bewohner bewohner) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT);
        String result = bewohner.getNachname() + ", " + bewohner.getVorname() + " (*" + df.format(bewohner.getGebDatum()) + ", ";
        BWInfo hauf = BWInfoTools.getLastBWInfo(bewohner, BWInfoTypTools.findByBWINFTYP("hauf"));

        if (BWInfoTools.isVerstorben(hauf)) {
            // In dem Fall, wird das Alter bis zum Sterbedatum gerechnet.
            result += SYSCalendar.calculateAge(SYSCalendar.toGC(bewohner.getGebDatum()), SYSCalendar.toGC(hauf.getBis())) + " Jahre) [" + bewohner.getBWKennung() + "]";
            result += "  verstorben: " + df.format(hauf.getBis()) + ", ";
        } else {
            if (BWInfoTools.isAusgezogen(hauf)) {
                result += "  ausgezogen: " + df.format(hauf.getBis()) + ", ";
            }
            result += SYSCalendar.calculateAge(SYSCalendar.toGC(bewohner.getGebDatum()), SYSCalendar.toGC(new Date())) + " Jahre) [" + bewohner.getBWKennung() + "]";
        }
        return result;
    }

}
