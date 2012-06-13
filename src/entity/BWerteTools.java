package entity;


import op.OPDE;
import op.care.vital.TMWerte;
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
    public static final String[] WERTE = new String[]{"?? unbekannt ??", "Blutdruck", "Puls", "Temperatur", "Blutzucker", "Gewicht", "Größe", "Atemfrequenz", "Quickwert", "Stuhlgang", "Erbrochen", "Ein-/Ausfuhrbilanz"};
    public static final String[] EINHEIT = new String[]{"?? unbekannt ??", "mmHg", "s/m", "°C", "mg/dl", "kg", "m", "A/m", "%", "", "", "ml"};

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
            if (bwert.isReplaced()) {
                color = SYSConst.html_lightslategrey;
            } else {
                color = SYSCalendar.getHTMLColor4Schicht(SYSCalendar.ermittleSchicht(bwert.getPit()));
            }
        }
        String result = sdf.format(bwert.getPit()) + "; " + bwert.getUser().getNameUndVorname();
        if (showids) {
            result += "<br/><i>(" + bwert.getBwid() + ")</i>";
        }
        return colorize ? "<font " + color + ">" + result + "</font>" : result;
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
            if (bwert.isReplaced()) {
                color = SYSConst.html_lightslategrey;
            } else {
                color = SYSCalendar.getHTMLColor4Schicht(SYSCalendar.ermittleSchicht(bwert.getPit()));
            }
        }
        if (!bwert.getAttachedFiles().isEmpty()) {
            result += "<font color=\"green\">&#9679;</font>";
        }
        if (!bwert.getAttachedVorgaenge().isEmpty()) {
            result += "<font color=\"red\">&#9679;</font>";
        }
        DateFormat df = DateFormat.getDateTimeInstance();
        if (bwert.isDeleted()) {
            result += "<i>Gelöschter Eintrag. Gelöscht am/um: " + df.format(bwert.getMdate()) + " von " + bwert.getEditedBy().getNameUndVorname() + "</i><br/>";
        }
        if (bwert.isReplacement()) {
            result += "<i>Dies ist ein Eintrag, der nachbearbeitet wurde.<br/>Am/um: " + df.format(bwert.getCdate()) + "<br/>Der Originaleintrag hatte die Nummer: " + bwert.getReplacementFor().getBwid() + "</i><br/>";
        }
        if (bwert.isReplaced()) {
            result += "<i>Dies ist ein Eintrag, der durch eine Nachbearbeitung ungültig wurde. Bitte ignorieren.<br/>Änderung wurde am/um: " + df.format(bwert.getCdate()) + " von " + bwert.getEditedBy().getNameUndVorname() + " vorgenommen.";
            result += "<br/>Der Korrektureintrag hat die Nummer: " + bwert.getReplacedBy().getBwid() + "</i><br/>";
        }
        result += "<b>" + bwert.getWert() + " " + EINHEIT[bwert.getType()] + "</b> (" + WERTE[bwert.getType()] + ")";

        if (bwert.getBemerkung() != null && !bwert.getBemerkung().isEmpty()) {
            result += "<br/><b>Bemerkung:</b> " + bwert.getBemerkung();
        }


//                if (fianzahl > 0) {
//                    //URL url = this.getClass().getResource("/artwork/16x16/attach.png");
//                    //System.out.println(url.getPath());
//                    result += "<font color=\"green\">&#9679;</font>";
//                }
//                if (vrganzahl > 0) {
//                    //URL url = this.getClass().getResource("/artwork/16x16/mail-tagged.png");
//                    //System.out.println(url.getPath());
//                    result += "<font color=\"red\">&#9679;</font>";
//                }
        return colorize ? "<font " + color + ">" + result + "</font>" : result;
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
                "<script type=\"text/javascript\">" +
                "window.onload = function() {" +
                "window.print();" +
                "}</script></head><body>" + html + "</body></html>";
        return html + "</font>";
    }


//
//    public static String getEinheit(String xml) {
//        String result;
//        if (xml.indexOf("<RRSYS/>") >= 0) {
//            result = "mmHg";
//        } else if (xml.indexOf("<RRDIA/>") >= 0) {
//            result = "mmHg";
//        } else if (xml.indexOf("<TEMP/>") >= 0) {
//            result = "°C";
//        } else if (xml.indexOf("<PULS/>") >= 0) {
//            result = "s/m";
//        } else if (xml.indexOf("<BZ/>") >= 0) {
//            result = "mg/dl";
//        } else if (xml.indexOf("<GEWICHT/>") >= 0) {
//            result = "kg";
//        } else if (xml.indexOf("<GROESSE/>") >= 0) {
//            result = "m";
//        } else if (xml.indexOf("<ATEM/>") >= 0) {
//            result = "A/m";
//        } else if (xml.indexOf("<braden") >= 0) {
//            result = "Bradenskala";
//        } else if (xml.indexOf("<QUICK/>") >= 0) {
//            result = "%";
//        } else if (xml.indexOf("<STUHLGANG/>") >= 0) {
//            result = "";
//        } else if (xml.indexOf("<ERBRECHEN/>") >= 0) {
//            result = "";
//        } else if (xml.indexOf("<BILANZ/>") >= 0) {
//            result = "ml";
//        } else {
//            result = "?? unbekannt ??";
//        }
//        return result;
//    }
//
//    public static int getMode(String xml) {
//        int result;
//        if (xml.indexOf("<RRSYS/>") >= 0) {
//            result = DlgVital.MODE_RRSYS;
//        } else if (xml.indexOf("<RRDIA/>") >= 0) {
//            result = DlgVital.MODE_RRDIA;
//        } else if (xml.indexOf("<TEMP/>") >= 0) {
//            result = DlgVital.MODE_TEMP;
//        } else if (xml.indexOf("<RR/>") >= 0) {
//            result = DlgVital.MODE_RR;
//        } else if (xml.indexOf("<PULS/>") >= 0) {
//            result = DlgVital.MODE_PULS;
//        } else if (xml.indexOf("<BZ/>") >= 0) {
//            result = DlgVital.MODE_BZ;
//        } else if (xml.indexOf("<GEWICHT/>") >= 0) {
//            result = DlgVital.MODE_GEWICHT;
//        } else if (xml.indexOf("<GROESSE/>") >= 0) {
//            result = DlgVital.MODE_GROESSE;
//        } else if (xml.indexOf("<ATEM/>") >= 0) {
//            result = DlgVital.MODE_ATEM;
//        } else if (xml.indexOf("<QUICK/>") >= 0) {
//            result = DlgVital.MODE_QUICK;
//        } else if (xml.indexOf("<STUHLGANG/>") >= 0) {
//            result = DlgVital.MODE_STUHLGANG;
//        } else if (xml.indexOf("<ERBRECHEN/>") >= 0) {
//            result = DlgVital.MODE_ERBRECHEN;
//        } else if (xml.indexOf("<BILANZ/>") >= 0) {
//            result = DlgVital.MODE_BILANZ;
//        } else {
//            result = DlgVital.MODE_UNKNOWN;
//        }
//        return result;
//    }
//
//    public static String getXML(int mode) {
//
//        String result;
//
//        switch (mode) {
//            case DlgVital.MODE_RRSYS: {
//                result = "<RRSYS/>";
//                break;
//            }
//            case DlgVital.MODE_RRDIA: {
//                result = "<RRDIA/>";
//                break;
//            }
//            case DlgVital.MODE_TEMP: {
//                result = "<TEMP/>";
//                break;
//            }
//            case DlgVital.MODE_RR: {
//                result = "<RR/>";
//                break;
//            }
//            case DlgVital.MODE_PULS: {
//                result = "<PULS/>";
//                break;
//            }
//            case DlgVital.MODE_BZ: {
//                result = "<BZ/>";
//                break;
//            }
//            case DlgVital.MODE_GEWICHT: {
//                result = "<GEWICHT/>";
//                break;
//            }
//            case DlgVital.MODE_GROESSE: {
//                result = "<GROESSE/>";
//                break;
//            }
//            case DlgVital.MODE_ATEM: {
//                result = "<ATEM/>";
//                break;
//            }
//            case DlgVital.MODE_QUICK: {
//                result = "<QUICK/>";
//                break;
//            }
//            case DlgVital.MODE_ERBRECHEN: {
//                result = "<ERBRECHEN/>";
//                break;
//            }
//            case DlgVital.MODE_STUHLGANG: {
//                result = "<STUHLGANG/>";
//                break;
//            }
//            case DlgVital.MODE_BILANZ: {
//                result = "<BILANZ/>";
//                break;
//            }
//            default: {
//                result = "<UNKNOWN/>";
//                break;
//            }
//        }
//        return result;
//    }
}
