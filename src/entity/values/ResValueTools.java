package entity.values;


import entity.info.Resident;
import entity.info.ResidentTools;
import entity.nursingprocess.DFNTools;
import op.OPDE;
import op.care.values.PnlValues;
import op.tools.Pair;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 28.10.11
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
public class ResValueTools {

    public static final short RR = 1;
    public static final short PULSE = 2;
    public static final short TEMP = 3;
    public static final short GLUCOSE = 4;
    public static final short WEIGHT = 5;
    public static final short HEIGHT = 6;
    public static final short BREATHING = 7;
    public static final short QUICK = 8;
    public static final short STOOL = 9;
    public static final short VOMIT = 10;
    public static final short LIQUIDBALANCE = 11;

//    public static final String[] VALUES = new String[]{"UNKNOWN", "RR", "PULSE", "TEMP", "GLUCOSE", "WEIGHT", "HEIGHT", "BREATHING", "QUICK", "STOOL", "VOMIT", "LIQUIDBALANCE"};

//    public static final String[] UNITS = new String[]{"", "mmHg", "s/m", "°C", "mg/dl", "kg", "m", "A/m", "%", "", "", "ml"};
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
        return (colorize ? "<font " + color + " " + SYSConst.html_arial14 + ">" + result + "</font>" : result);
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
     * @param resident um den es geht
     * @param type     des gesuchten Wertes
     * @return der Wert. <code>null</code>, wenn es keinen gibt.
     */
    public static ResValue getLast(Resident resident, short type) {

        ResValue result;

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" " +
                " SELECT b FROM ResValue b WHERE b.resident = :bewohner AND b.vtype.valType = :type " +
                " ORDER BY b.pit DESC ");
        query.setMaxResults(1);
        query.setParameter("bewohner", resident);
        query.setParameter("type", type);

        try {
            result = (ResValue) query.getSingleResult();
        } catch (Exception e) {
//            OPDE.fatal(e);
            result = null;
        } finally {
            em.close();
        }

        return result;
    }

    public static String getInfoAsHTML(ResValue resValue) {
        String result = "<div id=\"fonttext\">";

        DateFormat df = DateFormat.getDateTimeInstance();
        if (resValue.isDeleted()) {
            result += OPDE.lang.getString("misc.msg.thisentryhasbeendeleted") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(resValue.getEditDate()) + " <br/>" + OPDE.lang.getString("misc.msg.Bywhom") + " " + resValue.getEditedBy().getFullname() + "<br/>";
        }
        if (resValue.isReplacement() && !resValue.isReplaced()) {
            result += OPDE.lang.getString("misc.msg.thisEntryIsAReplacement") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(resValue.getCreateDate()) + "<br/>" + OPDE.lang.getString("misc.msg.originalentry") + ": " + resValue.getReplacementFor().getID() + "<br/>";
        }
        if (resValue.isReplaced()) {
            result += OPDE.lang.getString("misc.msg.thisentryhasbeenedited") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(resValue.getCreateDate()) + "<br/>" + OPDE.lang.getString("misc.msg.Bywhom") + " " + resValue.getEditedBy().getFullname();
            result += "<br/>" + OPDE.lang.getString("misc.msg.replaceentry") + ": " + resValue.getReplacedBy().getID() + "</i><br/>";
        }

        return result + "</div>";
    }

    //
//
//    public static String getTitle(ResValue param) {
//        String result = "";
//        if (param.getType() == RR) {
//            result += "<b>" + param.getValue1() + "/" + param.getValue2() + " " + UNITS[RR] + " " + VALUES[PULSE] + ": " + param.getValue3() + " " + UNITS[PULSE] + "</b>";
//        } else if (param.getType() == STOOL || param.getType() == VOMIT) {
//            result += "<i>" + OPDE.lang.getString("misc.msg.novalue") + "</i>";
//        } else {
//            result += "<b>" + param.getValue1() + " " + UNITS[param.getType()] + "</b>";
//        }
//        return result;
//    }
    public static String getTextAsHTML(ResValue wert, boolean colorize) {
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
        return result;
    }


    public static String getAsHTML(List<ResValue> resValues) {

        if (resValues.isEmpty()) {
            return "<i>" + OPDE.lang.getString("misc.msg.emptyselection") + "</i>";
        }

        String html = "";

        html += "<h1 id=\"fonth1\">" + OPDE.lang.getString(PnlValues.internalClassID) + " " + OPDE.lang.getString("misc.msg.for") + " " + ResidentTools.getLabelText(resValues.get(0).getResident()) + "</h1>";

        html += "<table  id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>" +
                "<th style=\"width:20%\">" + OPDE.lang.getString(PnlValues.internalClassID + ".tabheader1") +
                "</th><th style=\"width:40%\">" + OPDE.lang.getString(PnlValues.internalClassID + ".tabheader2") + "</th>" +
                "</th><th style=\"width:40%\">" + OPDE.lang.getString(PnlValues.internalClassID + ".tabheader3") + "</th></tr>\n";

        for (ResValue resValue : resValues) {
            html += "<tr>";
            html += "<td>" + getPITasHTML(resValue, false, false) + "</td>";
            html += "<td>" + getAsHTML(resValue) + "</td>";
            html += "<td>" + getTextAsHTML(resValue, false) + "</td>";
            html += "</tr>\n";
        }

        html += "</table>\n";

        return html;
    }

    public static String getAsHTML(ResValue rv) {
        String result = "";

        result += getInfoAsHTML(rv);

        result += getValueAsHTML(rv);

//             result += " (" + VALUES[rv.getType()] + ")";

        return "<div = \"fonttext\">" + result + "</div>";
    }

    public static boolean hatEinfuhren(Resident bewohner) {
        boolean result = false;

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT SUM(b.val1) FROM ResValue b WHERE b.val1 > 0 AND b.replacedBy IS NULL AND b.resident = :bewohner AND b.vtype.valType = :type AND b.pit >= :pit ");
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
        Query query = em.createQuery("SELECT SUM(b.val1) FROM ResValue b WHERE b.val1 < 0 AND b.replacedBy IS NULL AND b.resident = :bewohner AND b.vtype.valType = :type AND b.pit >= :pit ");
        query.setParameter("bewohner", bewohner);
        query.setParameter("type", ResValueTools.LIQUIDBALANCE);
        query.setParameter("pit", new DateTime().minusWeeks(1).toDateMidnight().toDate());

        BigDecimal sumwert = (BigDecimal) query.getSingleResult();
        result = sumwert != null && sumwert.abs().compareTo(BigDecimal.ZERO) > 0;

        return result;
    }

    /**
     * retrieves the PITs of the first and the last entry in the ResValue table.
     *
     * @param resident
     * @return
     */
    public static Pair<DateTime, DateTime> getMinMax(Resident resident, ResValueTypes vtype) {
        Pair<DateTime, DateTime> result = null;

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createQuery("SELECT rv FROM ResValue rv WHERE rv.resident = :resident AND rv.vtype = :vtype ORDER BY rv.pit ASC ");
        queryMin.setParameter("resident", resident);
        queryMin.setParameter("vtype", vtype);
        queryMin.setMaxResults(1);

        Query queryMax = em.createQuery("SELECT rv FROM ResValue rv WHERE rv.resident = :resident AND rv.vtype = :vtype ORDER BY rv.pit DESC ");
        queryMax.setParameter("resident", resident);
        queryMax.setParameter("vtype", vtype);
        queryMax.setMaxResults(1);

        try {
            ArrayList<ResValue> min = new ArrayList<ResValue>(queryMin.getResultList());
            ArrayList<ResValue> max = new ArrayList<ResValue>(queryMax.getResultList());
            if (min.isEmpty()) {
                result = null;
            } else {
                result = new Pair<DateTime, DateTime>(new DateTime(min.get(0).getPit()), new DateTime(max.get(0).getPit()));
            }

        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return result;
    }

    public static ArrayList<ResValue> getResValues(Resident resident, ResValueTypes vtype, DateTime month) {
        DateTime from = month.dayOfMonth().withMinimumValue();
        DateTime to = month.dayOfMonth().withMaximumValue();

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("" +
                " SELECT rv FROM ResValue rv " +
                " WHERE rv.resident = :resident " +
                " AND rv.vtype = :vtype" +
                " AND rv.pit >= :from" +
                " AND rv.pit <= :to" +
                " ORDER BY rv.pit DESC ");
        query.setParameter("resident", resident);
        query.setParameter("vtype", vtype);
        query.setParameter("from", from.toDate());
        query.setParameter("to", to.toDate());
        ArrayList<ResValue> list = new ArrayList<ResValue>(query.getResultList());
        em.close();

        return list;
    }


    public static String getValueAsHTML(ResValue rv) {
        String result = (rv.isDeleted() || rv.isReplaced() ? "<s>" : "");
        if (rv.getType().getValType() == RR) {
            result += "<b>" + rv.getVal1() + "/" + rv.getVal2() + " " + rv.getType().getUnit1() + " " + rv.getType().getLabel3() + ": " + rv.getVal3() + " " + rv.getType().getUnit3() + "</b>";
        } else if (rv.getType().getValType() == STOOL || rv.getType().getValType() == VOMIT) {
            result += "<i>" + SYSTools.catchNull(rv.getText(), "--") + "</i>";
        } else {
            result += "<b>" + rv.getVal1() + " " + rv.getType().getUnit1() + "</b>";
        }
        result += (rv.isDeleted() || rv.isReplaced() ? "</s>" : "");
        return result;
    }




        public static String getNoStool(int headertiefe) {
        StringBuilder html = new StringBuilder(1000);

        // Für wen soll die Ausfuhr überwacht werden ?

            tbStool.setSelected(props.containsKey(KEY_STOOLDAYS) && !props.getProperty(KEY_STOOLDAYS).equals("off"));
        tbBalance.setSelected(props.containsKey(KEY_BALANCE) && !props.getProperty(KEY_BALANCE).equals("off"));
        tbLowIn.setSelected(props.containsKey(KEY_LOWIN) && !props.getProperty(KEY_LOWIN).equals("off"));
        tbHighIn.setSelected(props.containsKey(KEY_HIGHIN) && !props.getProperty(KEY_HIGHIN).equals("off"));
        boolean drinkon = tbBalance.isSelected() || tbLowIn.isSelected() || tbHighIn.isSelected();
        txtDaysDrink.setEnabled(drinkon);
        txtStoolDays.setText(tbStool.isSelected() ? props.getProperty(KEY_STOOLDAYS) : "");
        txtStoolDays.setEnabled(tbStool.isSelected());
        txtLowIn.setText(tbLowIn.isSelected() ? props.getProperty(KEY_LOWIN) : "");
        txtLowIn.setEnabled(tbLowIn.isSelected());
        txtHighIn.setText(tbHighIn.isSelected() ? props.getProperty(KEY_HIGHIN) : "");
        txtHighIn.setEnabled(tbHighIn.isSelected());
        txtDaysDrink.setText(drinkon ? props.getProperty(KEY_DAYSDRINK) : "");
        txtDaysDrink.setEnabled(drinkon);


        String sql = "" +
                " SELECT bi.BWInfoID, b.nachname, b.vorname, b.Geschlecht, b.BWKennung FROM ResInfo bi " +
                " INNER JOIN Bewohner b ON bi.BWKennung = b.BWKennung " +
                " INNER JOIN ResInfo ba ON ba.BWKennung = bi.BWKennung " +
                " WHERE b.AdminOnly <> 2 AND bi.BWINFTYP='CONTROL' AND ba.BWINFTYP='HAUF' " +
                " AND bi.XML LIKE '%<c.stuhl value=\"true\"/>%' " +
                " and bi.von < now() and bi.bis > now() " +
                " AND ba.von <= NOW() AND ba.bis >= NOW()" +
                " ORDER BY nachname, vorname ";

        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            DateFormat df = DateFormat.getDateInstance();

            if (rs.first()) {

                rs.beforeFirst();
                while (rs.next()) {
                    Date last = new Date();//op.care.values.DBHandling.lastWert(rs.getString("b.BWKennung"), 1); //DlgVital.MODE_STUHLGANG);
//                    ResInfo bwi = new ResInfo(rs.getLong("bi.BWInfoID"));
//                    HashMap antwort = (HashMap) ((HashMap) bwi.getAttribute().get(0)).get("antwort");
//                    int tage = Integer.parseInt(antwort.get("c.stuhltage").toString());

//                    Date grenze = SYSCalendar.addDate(new Date(), tage * -1);

//                    if (last.before(grenze)) {
//
//                        if (html.length() == 0) {
//                            html.append("<h" + headertiefe + ">");
//                            html.append("Bewohner ohne Stuhlgang");
//                            html.append("</h" + headertiefe + ">");
//                            html.append("<table border=\"1\"><tr>" +
//                                    "<th>BewohnerIn</th><th>Letzter Stuhlgang</th><th>Tage bis Alarm</th></tr>");
//                        }
//
//                        html.append("<tr>");
//                        String name = SYSTools.anonymizeBW(rs.getString("Nachname"), rs.getString("Vorname"), rs.getString("BWKennung"), rs.getInt("geschlecht"));
//                        html.append("<td>" + name + "</td>");
//                        html.append("<td>" + df.format(last) + "</td>");
//                        html.append("<td>" + tage + "</td>");
//                        html.append("</tr>");
//                    }

                }

                html.append("</table>");
            }
        } catch (SQLException sQLException) {
            // new DlgException(sQLException);
        }
        return html.toString();
    }
//
//    public static String getAlarmEinfuhr(int headertiefe) {
//        StringBuilder html = new StringBuilder(1000);
//
//        // Für wen soll die Ausfuhr überwacht werden ?
//
//        String sql = "" +
//                " SELECT bi.BWInfoID, b.nachname, b.vorname, b.Geschlecht, b.BWKennung FROM ResInfo bi " +
//                " INNER JOIN Bewohner b ON bi.BWKennung = b.BWKennung " +
//                " INNER JOIN ResInfo ba ON ba.BWKennung = bi.BWKennung " +
//                " WHERE b.AdminOnly <> 2 AND bi.BWINFTYP='CONTROL' AND ba.BWINFTYP='HAUF' " +
//                " AND (bi.XML LIKE '%<c.einfuhr value=\"true\"/>%' OR  bi.XML LIKE '%<c.ueber value=\"true\"/>%') " +
//                " and bi.von < now() and bi.bis > now() " +
//                " AND ba.von <= NOW() AND ba.bis >= NOW()" +
//                " ORDER BY nachname, vorname ";
//
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            ResultSet rs = stmt.executeQuery();
//
//            DateFormat df = DateFormat.getDateInstance();
//
//            if (rs.first()) {
//
//                rs.beforeFirst();
//                while (rs.next()) {
//
//                    //Date last = op.care.values.DBHandling.lastWert(rs.getString("b.BWKennung"), DlgVital.MODE_STUHLGANG);
////                    ResInfo bwi = new ResInfo(rs.getLong("bi.BWInfoID"));
////                    HashMap antwort = (HashMap) ((HashMap) bwi.getAttribute().get(0)).get("antwort");
////                    boolean minkontrolle = antwort.get("c.einfuhr").toString().equalsIgnoreCase("true");
////                    boolean maxkontrolle = antwort.get("c.ueber").toString().equalsIgnoreCase("true");
////                    int tage = Integer.parseInt(antwort.get("c.einftage").toString());
////                    int minmenge = Integer.parseInt(antwort.get("c.einfmenge").toString());
////                    int maxmenge = Integer.parseInt(antwort.get("c.uebermenge").toString());
////                    if (!minkontrolle) {
////                        minmenge = -1000000; // Klein genug um im SQL Ausdruck ignoriert zu werden.
////                    }
////                    if (!maxkontrolle) {
////                        maxmenge = 1000000; // Groß genug um im SQL Ausdruck ignoriert zu werden.
////                    }
////
////                    String s = " SELECT * FROM (" +
////                            "       SELECT PIT, SUM(Wert) EINFUHR FROM ResValue " +
////                            "       WHERE ReplacedBy = 0 AND Wert > 0 AND BWKennung=? AND XML='<LIQUIDBALANCE/>' " +
////                            "       AND DATE(PIT) >= ADDDATE(DATE(now()), INTERVAL ? DAY) " +
////                            "       Group By DATE(PIT) " +
////                            "       ORDER BY PIT desc " +
////                            " ) a" +
////                            " WHERE a.EINFUHR < ? OR a.Einfuhr > ? ";
////                    PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(s);
////                    stmt1.setString(1, rs.getString("b.BWKennung"));
////                    stmt1.setInt(2, tage * -1);
////                    stmt1.setInt(3, minmenge);
////                    stmt1.setInt(4, maxmenge);
////
////                    ResultSet rs1 = stmt1.executeQuery();
////                    if (rs1.first()) {
////                        rs1.beforeFirst();
////                        while (rs1.next()) {
////
////                            if (html.length() == 0) {
////                                html.append("<h" + headertiefe + ">");
////                                html.append("Bewohner mit zu geringer / zu hoher Einfuhr");
////                                html.append("</h" + headertiefe + ">");
////                                html.append("<table border=\"1\"><tr>" +
////                                        "<th>BewohnerIn</th><th>Datum</th><th>Einfuhr (ml)</th><th>Bemerkung</th></tr>");
////                            }
////                            html.append("<tr>");
////                            String name = SYSTools.anonymizeBW(rs.getString("Nachname"), rs.getString("Vorname"), rs.getString("BWKennung"), rs.getInt("geschlecht"));
////                            html.append("<td>" + name + "</td>");
////                            html.append("<td>" + df.format(rs1.getDate("PIT")) + "</td>");
////                            html.append("<td>" + rs1.getString("Einfuhr") + "</td>");
////                            html.append("<td>" + (rs1.getDouble("Einfuhr") < minmenge ? "Einfuhr zu niedrig" : "Einfuhr zu hoch") + "</td>");
////                            html.append("</tr>");
////                        }
////                    }
//                }
//                if (html.length() > 0) {
//                    html.append("</table>");
//                }
//            }
//        } catch (SQLException sQLException) {
//            // new DlgException(sQLException);
//        }
//        return html.toString();
//    }

}
