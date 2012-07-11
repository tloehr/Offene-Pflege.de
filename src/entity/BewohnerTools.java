/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import entity.info.BWInfo;
import entity.info.BWInfoTools;
import entity.info.BWInfoTypTools;
import op.OPDE;
import op.tools.DlgListSelector;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author tloehr
 */
public class BewohnerTools {

    public static final int GESCHLECHT_MAENNLICH = 1;
    public static final int GESCHLECHT_WEIBLICH = 2;
    public static final String GESCHLECHT[] = {"", OPDE.lang.getString("misc.msg.male"), OPDE.lang.getString("misc.msg.female")};
    public static final String ANREDE[] = {"", OPDE.lang.getString("misc.msg.termofaddress.mr"), OPDE.lang.getString("misc.msg.termofaddress.mrs")};

    public static Bewohner findByBWKennung(String bwkennung) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Bewohner.findByBWKennung");
        query.setParameter("bWKennung", bwkennung);
        Bewohner bewohner = (Bewohner) query.getSingleResult();
        em.close();
        return bewohner;
    }

    public static JLabel getBWLabel(Bewohner bewohner) {
        JLabel lblBW = new JLabel();
        setBWLabel(lblBW, bewohner);
        return lblBW;
    }

    public static String getBWLabel1(Bewohner bewohner) {
        return bewohner.getNachname() + ", " + bewohner.getVorname();
    }

    public static String getBWLabel2(Bewohner bewohner) {
        return "(*" + DateFormat.getDateInstance().format(bewohner.getGebDatum()) + ") [" + bewohner.getBWKennung() + "]";
    }

    public static String getBWLabelTextKompakt(Bewohner bewohner) {
        return bewohner.getNachname() + ", " + bewohner.getVorname() + " [" + bewohner.getBWKennung() + "]";
    }


    public static void setBWLabel(JLabel lblBW, Bewohner bewohner) {
        lblBW.setFont(new java.awt.Font("Dialog", 1, 18));
        lblBW.setHorizontalAlignment(SwingConstants.LEADING);
        lblBW.setForeground(new java.awt.Color(255, 51, 0));
        lblBW.setText(getBWLabelText(bewohner));
    }

    public static String getFullName(Bewohner bewohner) {
        return ANREDE[bewohner.getGeschlecht()] + " " + bewohner.getVorname() + " " + bewohner.getNachname();
    }

    public static boolean isWeiblich(Bewohner bewohner) {
        return bewohner.getGeschlecht() == GESCHLECHT_WEIBLICH;
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

    /**
     * @return die BWKennung des gewünschten Bewohners oder "" wenn die Suche nicht erfolgreich war.
     */
    public static void findeBW(String muster, Closure applyClosure) {
        Bewohner bewohner = EntityTools.find(Bewohner.class, muster);

        if (bewohner == null) { // das Muster war kein gültiger Primary Key, dann suchen wir eben nach Namen.
            muster += "%"; // MySQL Wildcard
            EntityManager em = OPDE.createEM();

            Query query = em.createNamedQuery("Bewohner.findByNachname");
            query.setParameter("nachname", muster);
            List<Bewohner> listBW = query.getResultList();

            DefaultListModel dlm = SYSTools.list2dlm(listBW);

            if (dlm.getSize() > 1) {
                new DlgListSelector("Bitte wählen Sie eine(n) Bewohner(in) aus.", "Ihre Suche ergab mehrere Möglichkeiten. Welche(n) Bewohner(in) meinten Sie ?", dlm, applyClosure).setVisible(true);
            } else if (dlm.getSize() == 1) {
                bewohner = listBW.get(0);
                applyClosure.execute(bewohner);
            } else {
                applyClosure.execute(null);
            }
        } else {
            applyClosure.execute(bewohner);
        }
    }


}
