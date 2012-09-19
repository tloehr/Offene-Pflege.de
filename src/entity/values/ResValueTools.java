package entity.values;


import entity.info.Resident;
import entity.info.ResidentTools;
import entity.nursingprocess.DFNTools;
import entity.process.QProcess;
import entity.process.SYSVAL2PROCESS;
import op.OPDE;
import op.care.values.PnlValues;
import op.tools.Pair;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 28.10.11
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
public class ResValueTools {
    public static final int UNKNOWN = 0;
    public static final int RR = 1;
    public static final int PULSE = 2;
    public static final int TEMP = 3;
    public static final int GLUCOSE = 4;
    public static final int WEIGHT = 5;
    public static final int HEIGHT = 6;
    public static final int BREATHING = 7;
    public static final int QUICK = 8;
    public static final int STOOL = 9;
    public static final int VOMIT = 10;
    public static final int LIQUIDBALANCE = 11;
    public static final String[] VALUES = new String[]{"UNKNOWN", "RR", "PULSE", "TEMP", "GLUCOSE", "WEIGHT", "HEIGHT", "BREATHING", "QUICK", "STOOL", "VOMIT", "LIQUIDBALANCE"};

    public static final String[] UNITS = new String[]{"", "mmHg", "s/m", "°C", "mg/dl", "kg", "m", "A/m", "%", "", "", "ml"};
    public static final String RRSYS = "systolisch";
    public static final String RRDIA = "diatolisch";


    public static HashMap<Integer, String> getByID() {
        HashMap<Integer, String> table = new HashMap<Integer, String>();
        for (int i = 0; i <= 11; i++) {
            table.put(i, OPDE.lang.getString("misc.msg." + VALUES[i]));
        }
        return table;
    }

    public static HashMap<Integer, String> getUnitsByID() {
        HashMap<Integer, String> table = new HashMap<Integer, String>();
        for (int i = 0; i <= 11; i++) {
            table.put(i, OPDE.lang.getString("misc.msg.unit." + VALUES[i]));
        }
        return table;
    }

    public static HashMap<String, Integer> getByStringID() {
        HashMap<String, Integer> table = new HashMap<String, Integer>();
        for (int i = 0; i <= 11; i++) {
            table.put(OPDE.lang.getString("misc.msg." + VALUES[i]), i);
        }
        return table;
    }

    /**
     * Rendert eine HTML Darstellung des Datums und des Benutzers eines bestimmten Bewohner Wertes
     *
     * @param bwert
     * @param showids
     * @param colorize
     * @return
     */
    public static String getPITasHTML(ResValue bwert, boolean showids, boolean colorize) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        String color = "";
        if (colorize) {
            if (bwert.isReplaced() || bwert.isDeleted()) {
                color = SYSConst.html_lightslategrey;
            } else {
                color = OPDE.getProps().getProperty(DFNTools.SHIFT_KEY_TEXT[SYSCalendar.whatShiftIs(bwert.getPit())] + "_FGBHP");
            }
        }
        String result = sdf.format(bwert.getPit()) + "; " + bwert.getUser().getFullname();
        if (showids) {
            result += "<br/><i>(" + bwert.getID() + ")</i>";
        }
        return SYSTools.htmlUmlautConversion(colorize ? "<font " + color + " " + SYSConst.html_arial14 + ">" + result + "</font>" : result);
    }

    /**
     * Ermittelt den ersten bisher eingetragenen Wert für einen Bewohner.
     *
     * @param bewohner
     * @return
     */
    public static ResValue getFirst(Resident bewohner) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResValue b WHERE b.resident = :bewohner ORDER BY b.pit ");
        query.setParameter("bewohner", bewohner);
        query.setMaxResults(1);
        ResValue p = (ResValue) query.getSingleResult();
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
    public static ResValue getLast(Resident bewohner, int type) {

        ResValue result;

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" " +
                " SELECT b FROM ResValue b WHERE b.resident = :bewohner AND b.vtype = :type " +
                " ORDER BY b.pit DESC ");
        query.setMaxResults(1);
        query.setParameter("bewohner", bewohner);
        query.setParameter("type", type);

        try {
            result = (ResValue) query.getSingleResult();
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
     * @param
     * @param colorize
     * @return
     */
//    public static String getAsHTML(ResValue bwert, boolean colorize) {
//        String result = "";
//        String color = "";
//        if (colorize) {
//            if (bwert.isReplaced() || bwert.isDeleted()) {
//                color = SYSConst.html_lightslategrey;
//            } else {
//                color = OPDE.getProps().getProperty(DFNTools.SHIFT_KEY_TEXT[SYSCalendar.whatShiftIs(bwert.getPit())] + "_FGBHP");
//            }
//        }
////        if (!bwert.getAttachedFiles().isEmpty()) {
////            result += "<font color=\"green\">&#9679;</font>";
////        }
////        if (!bwert.getAttachedProcessConnections().isEmpty()) {
////            result += "<font color=\"red\">&#9679;</font>";
////        }
//        DateFormat df = DateFormat.getDateTimeInstance();
//        if (bwert.isDeleted()) {
//            result += "<i>" + OPDE.lang.getString("misc.msg.thisentryhasbeendeleted") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(bwert.getMdate()) + OPDE.lang.getString("misc.msg.Bywhom") + " " + bwert.getEditedBy().getFullname() + "</i><br/>";
//        }
//        if (bwert.isReplacement() && !bwert.isReplaced()) {
//            result += "<i>" + OPDE.lang.getString("misc.msg.thisentryhasbeenedited") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(bwert.getCreateDate()) + "<br/>" + OPDE.lang.getString("misc.msg.originalentry") + ": " + bwert.getReplacementFor().getBwid() + "</i><br/>";
//        }
//        if (bwert.isReplaced()) {
//            result += "<i>" + OPDE.lang.getString("misc.msg.thisentryhasbeenedited") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(bwert.getCreateDate()) + OPDE.lang.getString("misc.msg.Bywhom") + " " + bwert.getEditedBy().getFullname();
//            result += "<br/>" + OPDE.lang.getString("misc.msg.replaceentry") + ": " + bwert.getReplacedBy().getBwid() + "</i><br/>";
//        }
//
//        if (bwert.getType() == RR) {
//            result += "<b>" + bwert.getWert() + "/" + bwert.getValue2() + " " + UNITS[RR] + " " + VALUES[PULSE] + ": " + bwert.getValue3() + " " + UNITS[PULSE] + "</b>";
//        } else if (bwert.getType() == STOOL || bwert.getType() == VOMIT) {
//            result += "<i>" + OPDE.lang.getString("misc.msg.novalue") + "</i>";
//        } else {
//            result += "<b>" + bwert.getWert() + " " + UNITS[bwert.getType()] + "</b>";
//        }
//
//        result += " (" + VALUES[bwert.getType()] + ")";
//
////        if (bwert.getText() != null && !bwert.getText().isEmpty()) {
////            result += "<br/><b>Bemerkung:</b> " + bwert.getText();
////        }
//        return SYSTools.htmlUmlautConversion(colorize ? "<font " + color + " " + SYSConst.html_arial14 + ">" + result + "</font>" : result);
//    }
//
//
//    public static String getTitle(ResValue param) {
//        String result = "";
//        if (param.getType() == RR) {
//            result += "<b>" + param.getWert() + "/" + param.getValue2() + " " + UNITS[RR] + " " + VALUES[PULSE] + ": " + param.getValue3() + " " + UNITS[PULSE] + "</b>";
//        } else if (param.getType() == STOOL || param.getType() == VOMIT) {
//            result += "<i>" + OPDE.lang.getString("misc.msg.novalue") + "</i>";
//        } else {
//            result += "<b>" + param.getWert() + " " + UNITS[param.getType()] + "</b>";
//        }
//        return result;
//    }
    public static String getTExtAsHTML(ResValue wert, boolean colorize) {
        String result = "";
        if (!SYSTools.catchNull(wert.getText()).isEmpty()) {
            String color = "";
            if (colorize) {
                if (wert.isReplaced() || wert.isDeleted()) {
                    color = SYSConst.html_lightslategrey;
                } else {
                    color = OPDE.getProps().getProperty(DFNTools.SHIFT_KEY_TEXT[SYSCalendar.whatShiftIs(wert.getPit())] + "_FGBHP");
                }
            }
            result = "<font " + color + " " + SYSConst.html_arial14 + ">" + "<b>" + OPDE.lang.getString("misc.msg.comment") + ":</b> " + wert.getText() + "</font>";
        }
        return SYSTools.htmlUmlautConversion(result);
    }


    public static String getAsHTML(List<ResValue> bwerte) {

        if (bwerte.isEmpty()) {
            return "<i>" + OPDE.lang.getString("misc.msg.emptyselection") + "</i>";
        }

        String html = "";

        html += "<h1 id=\"fonth1\">" + OPDE.lang.getString(PnlValues.internalClassID) + " " + OPDE.lang.getString("misc.msg.for") + " " + ResidentTools.getLabelText(bwerte.get(0).getResident()) + "</h1>";

        html += "<table  id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>" +
                "<th style=\"width:20%\">" + OPDE.lang.getString(PnlValues.internalClassID + ".tabheader1") +
                "</th><th style=\"width:40%\">" + OPDE.lang.getString(PnlValues.internalClassID + ".tabheader2") + "</th>" +
                "</th><th style=\"width:40%\">" + OPDE.lang.getString(PnlValues.internalClassID + ".tabheader3") + "</th></tr>\n";

        for (ResValue wert : bwerte) {
            html += "<tr>";
            html += "<td>" + getPITasHTML(wert, false, false) + "</td>";
//            html += "<td>" + getAsHTML(wert, false) + "</td>";
            html += "<td>" + getTExtAsHTML(wert, false) + "</td>";
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
    public static ResValue deleteWert(ResValue wert) {
        ResValue mywert = null;
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();

            mywert = em.merge(wert);
            mywert.setDeletedBy(em.merge(OPDE.getLogin().getUser()));

//            // Datei Zuordnungen entfernen
//            Iterator<SYSNR2FILE> files = bericht.getAttachedFiles().iterator();
//            while (files.hasNext()) {
//                SYSNR2FILE oldAssignment = files.next();
//                em.remove(oldAssignment);
//            }
//            bericht.getAttachedFiles().clear();

            // Vorgangszuordnungen entfernen
            Iterator<SYSVAL2PROCESS> vorgaenge = mywert.getAttachedQProcesses().iterator();
            while (vorgaenge.hasNext()) {
                SYSVAL2PROCESS oldAssignment = vorgaenge.next();
                em.remove(oldAssignment);
            }
            mywert.getAttachedQProcesses().clear();
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
    public static ResValue changeWert(ResValue oldOne, ResValue newOne) {
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
            for (SYSVAL2PROCESS oldAssignment : oldOne.getAttachedQProcesses()) {
                QProcess vorgang = oldAssignment.getVorgang();
                em.lock(vorgang, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                SYSVAL2PROCESS newAssignment = em.merge(new SYSVAL2PROCESS(vorgang, newOne));
                newOne.getAttachedQProcesses().add(newAssignment);
                em.remove(oldAssignment);
            }
            oldOne.getAttachedQProcesses().clear();
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
        Query query = em.createQuery("SELECT SUM(b.val1) FROM ResValue b WHERE b.val1 > 0 AND b.replacedBy IS NULL AND b.resident = :bewohner AND b.vtype = :type AND b.pit >= :pit ");
        query.setParameter("bewohner", bewohner);
        query.setParameter("type", ResValueTools.LIQUIDBALANCE);
        query.setParameter("pit", new DateTime().minusWeeks(1).toDateMidnight().toDate());

        BigDecimal sumwert = (BigDecimal) query.getSingleResult();
        result = sumwert != null && sumwert.abs().compareTo(BigDecimal.ZERO) > 0;

        return result;
    }

    public static boolean hatAusfuhren(Resident bewohner) {
        boolean result = false;

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT SUM(b.val1) FROM ResValue b WHERE b.val1 < 0 AND b.replacedBy IS NULL AND b.resident = :bewohner AND b.vtype = :type AND b.pit >= :pit ");
        query.setParameter("bewohner", bewohner);
        query.setParameter("type", ResValueTools.LIQUIDBALANCE);
        query.setParameter("pit", new DateTime().minusWeeks(1).toDateMidnight().toDate());

        BigDecimal sumwert = (BigDecimal) query.getSingleResult();
        result = sumwert != null && sumwert.abs().compareTo(BigDecimal.ZERO) > 0;

        return result;
    }


    /**
     * retrieves the first and the last entry in the allowance table.
     *
     * @param resident
     * @return
     */
    public static Pair<ResValue, ResValue> getMinMax(Resident resident, ResValueType type) {
        Pair<ResValue, ResValue> result = null;

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createQuery("SELECT rv FROM ResValue rv WHERE rv.resident = :resident ORDER BY rv.pit ASC ");
        queryMin.setParameter("resident", resident);
        queryMin.setMaxResults(1);

        Query queryMax = em.createQuery("SELECT rv FROM ResValue rv WHERE rv.resident = :resident ORDER BY rv.pit DESC ");
        queryMax.setParameter("resident", resident);
        queryMax.setMaxResults(1);

        try {
            ArrayList<ResValue> min = new ArrayList<ResValue>(queryMin.getResultList());
            ArrayList<ResValue> max = new ArrayList<ResValue>(queryMax.getResultList());
            result = new Pair<ResValue, ResValue>(min.get(0), max.get(0));
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return result;
    }

}
