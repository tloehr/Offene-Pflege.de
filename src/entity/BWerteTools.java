package entity;


import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 28.10.11
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
public class BWerteTools {
    public static final int UNKNOWN = 0;
    public static final int RR = 1;
    public static final int PULS = 2;
    public static final int TEMP = 3;
    public static final int BZ = 4;
    public static final int GEWICHT = 5;
    public static final int GROESSE = 6;
    public static final int ATEM = 7;
    public static final int QUICK = 8;
    public static final int STUHLGANG = 9;
    public static final int ERBRECHEN = 10;
    public static final int BILANZ = 11;
    public static final String[] WERTE = new String[]{"unbekannt", "Blutdruck", "Puls", "Temperatur", "Blutzucker", "Gewicht", "Größe", "Atemfrequenz", "Quickwert", "Stuhlgang", "Erbrochen", "Ein-/Ausfuhrbilanz"};
    public static final String[] EINHEIT = new String[]{"unbekannt", "mmHg", "s/m", "°C", "mg/dl", "kg", "m", "A/m", "%", "", "", "ml"};
    public static final String RRSYS = "systolisch";
    public static final String RRDIA = "diatolisch";


    /**
     * Rendert eine HTML Darstellung des Datums und des Benutzers eines bestimmten Bewohner Wertes
     *
     * @param bwert
     * @param showids
     * @param colorize
     * @return
     */
    public static String getPITasHTML(BWerte bwert, boolean showids, boolean colorize) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        String color = "";
        if (colorize) {
            if (bwert.isReplaced() || bwert.isDeleted()) {
                color = SYSConst.html_lightslategrey;
            } else {
                color = SYSCalendar.getHTMLColor4Schicht(SYSCalendar.ermittleSchicht(bwert.getPit()));
            }
        }
        String result = sdf.format(bwert.getPit()) + "; " + bwert.getUser().getNameUndVorname();
        if (showids) {
            result += "<br/><i>(" + bwert.getBwid() + ")</i>";
        }
        return colorize ? "<font " + color + " " + SYSConst.html_arial14 + ">" + result + "</font>" : result;
    }

    /**
     * Ermittelt den ersten bisher eingetragenen Wert für einen Bewohner.
     *
     * @param bewohner
     * @return
     */
    public static BWerte getFirstWert(Bewohner bewohner) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM BWerte b WHERE b.bewohner = :bewohner ORDER BY b.pit ");
        query.setParameter("bewohner", bewohner);
        query.setMaxResults(1);
        BWerte p = (BWerte) query.getSingleResult();
        em.close();
        return p;
    }

    /**
     * Ermittelt den jeweils zuletzt eingetragenen Wert.
     *
     * @param bewohner um den es geht
     * @param type     des gesuchten Wertes
     * @return der Wert. <code>null</code>, wenn es keinen gibt.
     */
    public static BWerte getLetztenBWert(Bewohner bewohner, int type) {

        BWerte result;

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" " +
                " SELECT b FROM BWerte b WHERE b.bewohner = :bewohner AND b.type = :type " +
                " ORDER BY b.pit DESC ");
        query.setMaxResults(1);
        query.setParameter("bewohner", bewohner);
        query.setParameter("type", type);

        try {
            result = (BWerte) query.getSingleResult();
        } catch (Exception e) {
            result = null;
        } finally {
            em.close();
        }

        return result;
    }

    /**
     * Rendert eine HTML Darstellung eines bestimmten Bewohner Wertes
     *
     * @param bwert
     * @param colorize
     * @return
     */
    public static String getBWertAsHTML(BWerte bwert, boolean colorize) {
        String result = "";
        String color = "";
        if (colorize) {
            if (bwert.isReplaced() || bwert.isDeleted()) {
                color = SYSConst.html_lightslategrey;
            } else {
                color = SYSCalendar.getHTMLColor4Schicht(SYSCalendar.ermittleSchicht(bwert.getPit()));
            }
        }
//        if (!bwert.getAttachedFiles().isEmpty()) {
//            result += "<font color=\"green\">&#9679;</font>";
//        }
//        if (!bwert.getAttachedVorgaenge().isEmpty()) {
//            result += "<font color=\"red\">&#9679;</font>";
//        }
        DateFormat df = DateFormat.getDateTimeInstance();
        if (bwert.isDeleted()) {
            result += "<i><b>Gelöschter Eintrag.</b><br/>Gelöscht am/um: " + df.format(bwert.getMdate()) + " von " + bwert.getEditedBy().getNameUndVorname() + "</i><br/>";
        }
        if (bwert.isReplacement()) {
            result += "<i>Dies ist ein Eintrag, der <b>nachbearbeitet</b> wurde.<br/>Am/um: " + df.format(bwert.getCdate()) + "<br/>Der Originaleintrag hatte die Nummer: " + bwert.getReplacementFor().getBwid() + "</i><br/>";
        }
        if (bwert.isReplaced()) {
            result += "<i>Dies ist ein Eintrag, der durch eine <b>Nachbearbeitung</b> ungültig wurde. Bitte ignorieren.<br/>Änderung wurde am/um: " + df.format(bwert.getCdate()) + " von " + bwert.getEditedBy().getNameUndVorname() + " vorgenommen.";
            result += "<br/>Der Korrektureintrag hat die Nummer: " + bwert.getReplacedBy().getBwid() + "</i><br/>";
        }

        if (bwert.getType() == RR) {
            result += "<b>" + bwert.getWert() + "/" + bwert.getWert2() + " " + EINHEIT[RR] + " " + WERTE[PULS] + ": " + bwert.getWert3() + " " + EINHEIT[PULS] + "</b>";
        } else if (bwert.getType() == STUHLGANG || bwert.getType() == ERBRECHEN) {
            result += "<i>Kein Wert. Siehe Bemerkung</i>";
        } else {
            result += "<b>" + bwert.getWert() + " " + EINHEIT[bwert.getType()] + "</b>";
        }

        result += " (" + WERTE[bwert.getType()] + ")";

        if (bwert.getBemerkung() != null && !bwert.getBemerkung().isEmpty()) {
            result += "<br/><b>Bemerkung:</b> " + bwert.getBemerkung();
        }
        return colorize ? "<font " + color + " " + SYSConst.html_arial14 + ">" + result + "</font>" : result;
    }


    public static String getBWerteAsHTML(List<BWerte> bwerte) {

        if (bwerte.isEmpty()) {
            return "<i>keine Werte in der Auswahl vorhanden</i>";
        }

        String html = "";

        html += "<h1>Bewohner-Werte für " + BewohnerTools.getBWLabelText(bwerte.get(0).getBewohner()) + "</h1>";

        html += "<table border=\"1\" cellspacing=\"0\"><tr>" +
                "<th style=\"width:30%\">Info</th><th style=\"width:70%\">Wert</th></tr>";

        for (BWerte wert : bwerte) {
            html += "<tr>";
            html += "<td>" + getPITasHTML(wert, false, false) + "</td>";
            html += "<td>" + getBWertAsHTML(wert, false) + "</td>";
            html += "</tr>";
            html += "</table>";
        }

        html = "<html><head>" +
                "<title>" + SYSTools.getWindowTitle("") + "</title>" +
                OPDE.getCSS() +
                "<script type=\"text/javascript\">" +
                "window.onload = function() {" +
                "window.print();" +
                "}</script></head><body>" + html + "</body></html>";
        return html;
    }


}
