/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.reports;

import entity.Homes;
import entity.nursingprocess.DFNTools;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @author tloehr
 */
public class HandoversTools {

    public static String getDatumUndUser(Handovers bericht, boolean showIDs) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        result = sdf.format(bericht.getPit()) + "; " + bericht.getUser().getFullname();
        if (showIDs) {
            result += "<br/><i>(" + bericht.getUebid() + ")</i>";
        }
        return "<font " + getHTMLColor(bericht) + ">" + result + "</font>";
    }

    private static String getHTMLColor(Handovers bericht) {
        return OPDE.getProps().getProperty(DFNTools.SHIFT_KEY_TEXT[SYSCalendar.whatShiftIs(bericht.getPit())] + "_FGBHP");
    }

    /**
     * retrieves all NReports for a certain day which have been assigned with the Tags Nr. 1 (Handover) and Nr. 2 (Emergency)
     * @param day
     * @return
     */
    public static ArrayList<Handovers> getBy(DateMidnight day, Homes home) {
        DateTime from = day.toDateTime();
        DateTime to = day.plusDays(1).toDateTime().minusSeconds(1);
        EntityManager em = OPDE.createEM();
        ArrayList<Handovers> list = null;

        try {

            String jpql = " SELECT ho " +
                    " FROM Handovers ho " +
                    " WHERE " +
                    " ho.pit >= :from AND ho.pit <= :to " +
                    " AND ho.home = :home " +
                    " ORDER BY ho.pit ASC ";

            Query query = em.createQuery(jpql);

            query.setParameter("from", from.toDate());
            query.setParameter("to", to.toDate());
            query.setParameter("home", home);

            list = new ArrayList<Handovers>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    /**
     * gibt eine HTML Darstellung des Berichtes zurück.
     *
     * @return
     */
    public static String getAsHTML(Handovers bericht) {
        String result = "";

        String fonthead = "<font " + getHTMLColor(bericht) + ">";

        DateFormat df = DateFormat.getDateTimeInstance();
        //result += (flags.equals("") ? "" : "<b>" + flags + "</b><br/>");

        result += SYSTools.replace(bericht.getText(), "\n", "<br/>", false);
        result = fonthead + result + "</font>";
        return result;
    }

    /**
     * gibt eine HTML Darstellung des Einrichtungsnamen zurück.
     *
     * @return
     */
    public static String getEinrichtungAsHTML(Handovers bericht) {
        String result = "";

        String fonthead = "<font " + getHTMLColor(bericht) + ">";
        result += bericht.getHome().getBezeichnung();
        result = fonthead + result + "</font>";
        return result;
    }
}
