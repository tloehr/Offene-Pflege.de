package entity;


import entity.info.Resident;
import entity.info.ResidentTools;
import entity.nursingprocess.DFNTools;
import entity.process.*;
import op.OPDE;
import op.care.vital.PnlVitalwerte;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
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
    public static final String[] WERTE = new String[]{"", "Blutdruck", "Puls", "Temperatur", "Blutzucker", "Gewicht", "Größe", "Atemfrequenz", "Quickwert", "Stuhlgang", "Erbrochen", "Ein-/Ausfuhrbilanz"};
    public static final String[] EINHEIT = new String[]{"", "mmHg", "s/m", "°C", "mg/dl", "kg", "m", "A/m", "%", "", "", "ml"};
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
                color = OPDE.getProps().getProperty(DFNTools.SHIFT_KEY_TEXT[SYSCalendar.whatShiftIs(bwert.getPit())] + "_FGBHP");
            }
        }
        String result = sdf.format(bwert.getPit()) + "; " + bwert.getUser().getNameUndVorname();
        if (showids) {
            result += "<br/><i>(" + bwert.getBwid() + ")</i>";
        }
        return SYSTools.htmlUmlautConversion(colorize ? "<font " + color + " " + SYSConst.html_arial14 + ">" + result + "</font>" : result);
    }

    /**
     * Ermittelt den ersten bisher eingetragenen Wert für einen Bewohner.
     *
     * @param bewohner
     * @return
     */
    public static BWerte getFirstWert(Resident bewohner) {
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
    public static BWerte getLetztenBWert(Resident bewohner, int type) {

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
                color = OPDE.getProps().getProperty(DFNTools.SHIFT_KEY_TEXT[SYSCalendar.whatShiftIs(bwert.getPit())] + "_FGBHP");
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
            result += "<i>" + OPDE.lang.getString("misc.msg.thisentryhasbeendeleted") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(bwert.getMdate()) + OPDE.lang.getString("misc.msg.Bywhom") + " " + bwert.getEditedBy().getNameUndVorname() + "</i><br/>";
        }
        if (bwert.isReplacement() && !bwert.isReplaced()) {
            result += "<i>" + OPDE.lang.getString("misc.msg.thisentryhasbeenedited") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(bwert.getCdate()) + "<br/>" + OPDE.lang.getString("misc.msg.originalentry") + ": " + bwert.getReplacementFor().getBwid() + "</i><br/>";
        }
        if (bwert.isReplaced()) {
            result += "<i>" + OPDE.lang.getString("misc.msg.thisentryhasbeenedited") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(bwert.getCdate()) + OPDE.lang.getString("misc.msg.Bywhom") + " " + bwert.getEditedBy().getNameUndVorname();
            result += "<br/>" + OPDE.lang.getString("misc.msg.replaceentry") + ": " + bwert.getReplacedBy().getBwid() + "</i><br/>";
        }

        if (bwert.getType() == RR) {
            result += "<b>" + bwert.getWert() + "/" + bwert.getWert2() + " " + EINHEIT[RR] + " " + WERTE[PULS] + ": " + bwert.getWert3() + " " + EINHEIT[PULS] + "</b>";
        } else if (bwert.getType() == STUHLGANG || bwert.getType() == ERBRECHEN) {
            result += "<i>" + OPDE.lang.getString("misc.msg.novalue") + "</i>";
        } else {
            result += "<b>" + bwert.getWert() + " " + EINHEIT[bwert.getType()] + "</b>";
        }

        result += " (" + WERTE[bwert.getType()] + ")";

//        if (bwert.getBemerkung() != null && !bwert.getBemerkung().isEmpty()) {
//            result += "<br/><b>Bemerkung:</b> " + bwert.getBemerkung();
//        }
        return SYSTools.htmlUmlautConversion(colorize ? "<font " + color + " " + SYSConst.html_arial14 + ">" + result + "</font>" : result);
    }


    public static String getTitle(BWerte param){
        String result ="";
        if (param.getType() == RR) {
            result += "<b>" + param.getWert() + "/" + param.getWert2() + " " + EINHEIT[RR] + " " + WERTE[PULS] + ": " + param.getWert3() + " " + EINHEIT[PULS] + "</b>";
        } else if (param.getType() == STUHLGANG || param.getType() == ERBRECHEN) {
            result += "<i>" + OPDE.lang.getString("misc.msg.novalue") + "</i>";
        } else {
            result += "<b>" + param.getWert() + " " + EINHEIT[param.getType()] + "</b>";
        }
        return result;
    }

    public static String getBemerkungAsHTML(BWerte wert, boolean colorize) {
        String result = "";
        if (!SYSTools.catchNull(wert.getBemerkung()).isEmpty()) {
            String color = "";
            if (colorize) {
                if (wert.isReplaced() || wert.isDeleted()) {
                    color = SYSConst.html_lightslategrey;
                } else {
                    color = OPDE.getProps().getProperty(DFNTools.SHIFT_KEY_TEXT[SYSCalendar.whatShiftIs(wert.getPit())] + "_FGBHP");
                }
            }
            result = "<font " + color + " " + SYSConst.html_arial14 + ">" + "<b>" + OPDE.lang.getString("misc.msg.comment") + ":</b> " + wert.getBemerkung() + "</font>";
        }
        return SYSTools.htmlUmlautConversion(result);
    }


    public static String getBWerteAsHTML(List<BWerte> bwerte) {

        if (bwerte.isEmpty()) {
            return "<i>" + OPDE.lang.getString("misc.msg.emptyselection") + "</i>";
        }

        String html = "";

        html += "<h1 id=\"fonth1\">" + OPDE.lang.getString(PnlVitalwerte.internalClassID) + " " + OPDE.lang.getString("misc.msg.for") + " " + ResidentTools.getLabelText(bwerte.get(0).getBewohner()) + "</h1>";

        html += "<table  id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>" +
                "<th style=\"width:20%\">" + OPDE.lang.getString(PnlVitalwerte.internalClassID + ".tabheader1") +
                "</th><th style=\"width:40%\">" + OPDE.lang.getString(PnlVitalwerte.internalClassID + ".tabheader2") + "</th>" +
                "</th><th style=\"width:40%\">" + OPDE.lang.getString(PnlVitalwerte.internalClassID + ".tabheader3") + "</th></tr>\n";

        for (BWerte wert : bwerte) {
            html += "<tr>";
            html += "<td>" + getPITasHTML(wert, false, false) + "</td>";
            html += "<td>" + getBWertAsHTML(wert, false) + "</td>";
            html += "<td>" + getBemerkungAsHTML(wert, false) + "</td>";
            html += "</tr>\n";
        }

        html += "</table>\n";

//        html = "<html><head>" +
//                "<title>" + SYSTools.getWindowTitle("") + "</title>" +
//                OPDE.getCSS() +
//                "<script type=\"text/javascript\">" +
//                "window.onload = function() {" +
//                "window.print();" +
//                "}</script></head><body>\n" + html + "\n" + SYSConst.html_report_footer + "</body></html>";

        return SYSTools.htmlUmlautConversion(html);
    }


    /**
     * setzt einen Bewohnerwert auf "gelöscht". Das heisst, er ist dann inaktiv. Es werden auch Datei und Vorgangs
     * Zuordnungen entfernt.
     *
     * @param wert
     * @return den geänderten und somit gelöschten Wert. Null bei Fehler.
     */
    public static BWerte deleteWert(BWerte wert) {
        BWerte mywert = null;
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();

            mywert = em.merge(wert);
            mywert.setDeletedBy(em.merge(OPDE.getLogin().getUser()));

//            // Datei Zuordnungen entfernen
//            Iterator<SYSPB2FILE> files = bericht.getAttachedFiles().iterator();
//            while (files.hasNext()) {
//                SYSPB2FILE oldAssignment = files.next();
//                em.remove(oldAssignment);
//            }
//            bericht.getAttachedFiles().clear();

            // Vorgangszuordnungen entfernen
            Iterator<SYSVAL2PROCESS> vorgaenge = mywert.getAttachedVorgaenge().iterator();
            while (vorgaenge.hasNext()) {
                SYSVAL2PROCESS oldAssignment = vorgaenge.next();
                em.remove(oldAssignment);
            }
            mywert.getAttachedVorgaenge().clear();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return mywert;
    }

    /**
     * Führt die notwendigen Änderungen an den Entities durch, wenn ein Wert geändert wurde. Dazu gehört auch die
     * Vorgänge umzubiegen. Der alte Wert verliert seine Vorgänge. Es werden auch die
     * notwendigen Querverweise zwischen dem alten und dem neuen Wert erstellt.
     *
     * @param oldOne der Wert, der durch den <code>newOne</code> ersetzt werden soll.
     * @param newOne siehe oben
     * @return den neuen Wert.
     */
    public static BWerte changeWert(BWerte oldOne, BWerte newOne) {
        EntityManager em = OPDE.createEM();
        em.getTransaction().begin();
        try {

            oldOne = em.merge(oldOne);
            newOne = em.merge(newOne);

            em.lock(oldOne, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            newOne.setReplacementFor(oldOne);
            newOne.setUser(em.merge(OPDE.getLogin().getUser()));

            oldOne.setEditedBy(em.merge(OPDE.getLogin().getUser()));
            oldOne.setCdate(new Date());
            oldOne.setReplacedBy(newOne);

            // Vorgänge umbiegen
            for (SYSVAL2PROCESS oldAssignment : oldOne.getAttachedVorgaenge()) {
                QProcess vorgang = oldAssignment.getVorgang();
                em.lock(vorgang, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                SYSVAL2PROCESS newAssignment = em.merge(new SYSVAL2PROCESS(vorgang, newOne));
                newOne.getAttachedVorgaenge().add(newAssignment);
                em.remove(oldAssignment);
            }
            oldOne.getAttachedVorgaenge().clear();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return newOne;
    }

    public static boolean hatEinfuhren(Resident bewohner) {
            boolean result = false;

            EntityManager em = OPDE.createEM();
            Query query = em.createQuery("SELECT SUM(b.wert) FROM BWerte b WHERE b.wert > 0 AND b.replacedBy IS NULL AND b.bewohner = :bewohner AND b.type = :type AND b.pit >= :pit ");
            query.setParameter("bewohner", bewohner);
            query.setParameter("type", BWerteTools.BILANZ);
            query.setParameter("pit", new DateTime().minusWeeks(1).toDateMidnight().toDate());

            BigDecimal sumwert = (BigDecimal) query.getSingleResult();
            result = sumwert != null && sumwert.abs().compareTo(BigDecimal.ZERO) > 0;

            return result;
        }

        public static boolean hatAusfuhren(Resident bewohner) {
            boolean result = false;

            EntityManager em = OPDE.createEM();
            Query query = em.createQuery("SELECT SUM(b.wert) FROM BWerte b WHERE b.wert < 0 AND b.replacedBy IS NULL AND b.bewohner = :bewohner AND b.type = :type AND b.pit >= :pit ");
            query.setParameter("bewohner", bewohner);
            query.setParameter("type", BWerteTools.BILANZ);
            query.setParameter("pit", new DateTime().minusWeeks(1).toDateMidnight().toDate());

            BigDecimal sumwert = (BigDecimal) query.getSingleResult();
            result = sumwert != null && sumwert.abs().compareTo(BigDecimal.ZERO) > 0;

            return result;
        }

}
