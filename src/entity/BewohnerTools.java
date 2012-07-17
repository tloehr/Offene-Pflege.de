/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import entity.info.BWInfo;
import entity.info.BWInfoTools;
import entity.info.BWInfoTypTools;
import entity.verordnungen.VerordnungTools;
import op.OPDE;
import op.tools.DlgListSelector;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
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

//    public static boolean isWeiblich(Bewohner bewohner) {
//        return bewohner.getGeschlecht() == GESCHLECHT_WEIBLICH;
//    }

    public static String getBWLabelText(Bewohner bewohner) {
        boolean verstorben = BWInfoTools.isVerstorben(bewohner);
        boolean ausgezogen = BWInfoTools.isVerstorben(bewohner);
        BWInfo hauf = BWInfoTools.getLastBWInfo(bewohner, BWInfoTypTools.findByBWINFTYP("hauf"));

        DateFormat df = DateFormat.getDateInstance();
        String result = bewohner.getNachname() + ", " + bewohner.getVorname() + " (*" + df.format(bewohner.getGebDatum()) + ", ";

        DateMidnight birthdate = new DateTime(bewohner.getGebDatum()).toDateMidnight();
        DateTime refdate = verstorben ? new DateTime(hauf.getBis()) : new DateTime();
        Years age = Years.yearsBetween(birthdate, refdate);

        result += age.getYears() + " " + OPDE.lang.getString("misc.msg.Years") + " [" + bewohner.getBWKennung() + "]";

        if (verstorben || ausgezogen) {
            result += "  " + (verstorben ? OPDE.lang.getString("misc.msg.late") : OPDE.lang.getString("misc.msg.movedout")) + ": " + df.format(hauf.getBis()) + ", ";
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


    /**
     * This method must be called if a resident finally leaves the home. It will then seize all running processes and
     * end all open periods of any kind. Plans, Medication etc.
     *
     * @param em       as it is quite a complex operation, it runs within a surrounding EM to trigger rollbacks if necessary
     * @param bewohner the resident in question
     */
    public static void endOfStay(EntityManager em, Bewohner bewohner, Date enddate) throws Exception {
        em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        // TODO: Die ganzen Operationen bei Sterben und Ausziehen müssen gemacht werden, wenn der REST fertig ist.
        VerordnungTools.alleAbsetzen(em, bewohner);
        // Alle Planungen absetzen
        BWInfoTools.alleAbsetzen(em, bewohner);
        // Alle Bestände schließen
        // Alle nicht abgehakten BHPs und DFNs löschen
        // Alle Vorgänge schließen
    }


}
