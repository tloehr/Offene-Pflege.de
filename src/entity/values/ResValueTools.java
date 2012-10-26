package entity.values;


import entity.info.Resident;
import entity.info.ResidentTools;
import entity.nursingprocess.DFNTools;
import op.OPDE;
import op.care.values.PnlValues;
import op.controlling.PnlControlling;
import op.tools.Pair;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.Format;
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
                " SELECT b FROM ResValue b WHERE b.resident = :bewohner AND b.replacedBy IS NULL AND b.vtype.valType = :type " +
                " ORDER BY b.pit DESC ");
        query.setMaxResults(1);
        query.setParameter("bewohner", resident);
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
        Query queryMin = em.createQuery("SELECT rv FROM ResValue rv WHERE rv.resident = :resident AND rv.replacedBy IS NULL AND rv.vtype = :vtype ORDER BY rv.pit ASC ");
        queryMin.setParameter("resident", resident);
        queryMin.setParameter("vtype", vtype);
        queryMin.setMaxResults(1);

        Query queryMax = em.createQuery("SELECT rv FROM ResValue rv WHERE rv.resident = :resident AND rv.replacedBy IS NULL AND rv.vtype = :vtype ORDER BY rv.pit DESC ");
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
                " AND rv.replacedBy IS NULL " +
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

    public static String getLiquidBalance(DateMidnight month, Closure progress) {
        //TODO: getAllActive(DateMidnight month)
        ArrayList<Resident> listResidents = ResidentTools.getAllActive();
        Format monthFormmatter = new SimpleDateFormat("MMMM yyyy");
        DateTime from = month.dayOfMonth().withMinimumValue().toDateTime();
        DateTime to = month.dayOfMonth().withMaximumValue().plusDays(1).toDateTime().minusSeconds(1);

        int p = -1;
        boolean isCancelled = false;

        progress.execute(new Pair<Integer, Integer>(p, listResidents.size()));

//        BigDecimal sum = BigDecimal.ZERO;

        StringBuilder html = new StringBuilder(1000);
//        String sqlVormonat = "" +
//                " SELECT avg(ein.EINFUHR) Einfuhr, ifnull(avg(aus.AUSFUHR), 0) Ausfuhr, ifnull(sum(ein.EINFUHR)+sum(aus.AUSFUHR), 0) BILANZ FROM " +
//                " (" +
//                "   SELECT Pit, Date(PIT), bw.BWKennung, SUM(Wert) AUSFUHR FROM BWerte bw" +
//                "   WHERE ReplacedBy = 0 AND Wert < 0 AND bw.BWKennung = ? AND XML='<BILANZ/>' AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?)" +
//                "   GROUP BY bw.BWKennung, Date(PIT) " +
//                " ) aus " +
//                " RIGHT OUTER JOIN " +
//                " (" +
//                "   SELECT Pit, Date(PIT), bw.BWKennung, SUM(Wert) EINFUHR FROM BWerte bw " +
//                "   WHERE ReplacedBy = 0 AND Wert > 0 AND bw.BWKennung = ? AND XML='<BILANZ/>' AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?) " +
//                "   GROUP BY bw.BWKennung, Date(PIT) " +
//                " ) ein " +
//                " ON aus.BWKennung = ein.BWKennung AND Date(aus.PIT) = Date(ein.PIT) " +
//                " INNER JOIN Bewohner b ON ein.BWKennung = b.BWKennung " +
//                " GROUP BY b.BWKennung ";
//        String sql = "" +
//                " SELECT ein.BWKennung, DATE_FORMAT(ein.PIT, '%d.%c.%Y') Datum, ein.Einfuhr, ifnull(aus.AUSFUHR, 0) Ausfuhr, ifnull((ein.EINFUHR+aus.AUSFUHR), 0) BILANZ FROM " +
//                " ( " +
//                "   SELECT PIT, bw.BWKennung, SUM(Wert) AUSFUHR FROM BWerte bw " +
//                "   INNER JOIN Bewohner b ON b.BWKennung = bw.BWKennung " +
//                "   WHERE ReplacedBy = 0 AND Wert < 0 AND AdminOnly <> 2 AND XML='<BILANZ/>' AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?) " +
//                "   GROUP BY bw.BWKennung, Date(PIT) " +
//                " ) aus " +
//                " RIGHT OUTER JOIN " +
//                " (" +
//                "   SELECT PIT, bw.BWKennung, SUM(Wert) EINFUHR FROM BWerte bw " +
//                "   INNER JOIN Bewohner b ON b.BWKennung = bw.BWKennung " +
//                "   WHERE ReplacedBy = 0 AND Wert > 0 AND AdminOnly <> 2 AND XML='<BILANZ/>' AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?) " +
//                "   GROUP BY bw.BWKennung, Date(PIT) " +
//                " ) ein " +
//                " ON aus.BWKennung = ein.BWKennung AND Date(aus.PIT) = Date(ein.PIT)" +
//                " INNER JOIN Bewohner b ON ein.BWKennung = b.BWKennung " +
//                " ORDER BY ein.BWKennung, Date(ein.PIT) ";


//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            PreparedStatement stmtVormonat = OPDE.getDb().db.prepareStatement(sqlVormonat);
//            stmt.setDate(1, new java.sql.Date(SYSCalendar.bom(monat).getTime()));
//            stmt.setDate(2, new java.sql.Date(SYSCalendar.eom(monat).getTime()));
//            stmt.setDate(3, new java.sql.Date(SYSCalendar.bom(monat).getTime()));
//            stmt.setDate(4, new java.sql.Date(SYSCalendar.eom(monat).getTime()));
//
//            // BWKennung wird in der Schleife gesetzt.
//            stmtVormonat.setDate(2, new java.sql.Date(SYSCalendar.bom(vormonat).getTime()));
//            stmtVormonat.setDate(3, new java.sql.Date(SYSCalendar.eom(vormonat).getTime()));
//            stmtVormonat.setDate(5, new java.sql.Date(SYSCalendar.bom(vormonat).getTime()));
//            stmtVormonat.setDate(6, new java.sql.Date(SYSCalendar.eom(vormonat).getTime()));

//            ResultSet rs = stmt.executeQuery();


        html.append(SYSConst.html_h1(OPDE.lang.getString(PnlControlling.internalClassID + ".nutrition.liquidbalance")));
        html.append(SYSConst.html_h2(monthFormmatter.format(month.toDate())));

        p = 0;
        for (Resident resident : listResidents) {
            p++;
            progress.execute(new Pair<Integer, Integer>(p, listResidents.size()));
            Properties controlling = resident.getControlling();
            if (controlling.containsKey(ResidentTools.KEY_BALANCE) && controlling.getProperty(ResidentTools.KEY_BALANCE).equals("on")) {
                BigDecimal targetIn = SYSTools.parseBigDecimal(controlling.getProperty(ResidentTools.KEY_TARGETIN));

                html.append(SYSConst.html_h3(ResidentTools.getBWLabelTextKompakt(resident)));
                html.append(SYSConst.html_div(SYSConst.html_bold(OPDE.lang.getString("misc.msg.targetDrink")) + ": " + targetIn.setScale(2, RoundingMode.HALF_UP).toString() + " ml"));

                HashMap<DateMidnight, Pair<BigDecimal, BigDecimal>> balanceMap = getLiquidBalancePerDay(resident, from.toDateMidnight(), to.toDateMidnight());

//                BigDecimal balance = BigDecimal.ZERO;

//                if (rs.getRow() > 1) {
//                    avgEin = avgEin / rows;
//                    avgAus = avgAus / rows;
//
//                    avgEin = Math.round(avgEin * 100d) / 100d;
//                    avgAus = Math.round(avgAus * 100d) / 100d;
//                    //String.format(prev, arg1)
//
//
//                    html.append("<tr><td></td><td>" + df.format(monat) + "</td><td>&Oslash; " + avgEin + "</td><td>&Oslash; " + avgAus + "</td><td>&Sigma; " + sumBilanz + "</td></tr>");
//
//                    // Vormonat berechnen
//                    stmtVormonat.setString(1, prev);
//                    stmtVormonat.setString(4, prev);
//                    ResultSet rsVormonat = stmtVormonat.executeQuery();
//
//                    if (rsVormonat.first()) {
//                        double avgEinVor = SYSTools.roundScale2(rsVormonat.getDouble("Einfuhr"));
//                        double avgAusVor = SYSTools.roundScale2(rsVormonat.getDouble("Ausfuhr"));
//                        double sumBilanzVor = SYSTools.roundScale2(rsVormonat.getDouble("Bilanz"));
//                        html.append("<tr><td></td><td>" + df.format(vormonat) + "</td><td>&Oslash; " + avgEinVor + "</td><td>&Oslash; " + avgAusVor + "</td><td>&Sigma; " + sumBilanzVor + "</td></tr>");
//                    }
//
//                    rsVormonat.close();
//
//                    html.append("</table>");
//                    avgEin = 0;
//                    avgAus = 0;
//                    sumBilanz = 0;
//                    rows = 0;
//                }

                ArrayList<DateMidnight> listDays = new ArrayList<DateMidnight>(balanceMap.keySet());
                Collections.sort(listDays);

                StringBuilder table = new StringBuilder(1000);
                table.append(SYSConst.html_table_tr(
                        SYSConst.html_table_th(OPDE.lang.getString("misc.msg.Date")) +
                                SYSConst.html_table_th(OPDE.lang.getString("misc.msg.ingestion")) +
                                SYSConst.html_table_th(OPDE.lang.getString("misc.msg.egestion")) +
                                SYSConst.html_table_th(OPDE.lang.getString("misc.msg.balance"))
                ));
                for (DateMidnight day : listDays) {
                    BigDecimal linesum = balanceMap.get(day).getFirst().add(balanceMap.get(day).getSecond());
                    table.append(SYSConst.html_table_tr(
                            SYSConst.html_table_td(DateFormat.getDateInstance().format(day.toDate())) +
                                    SYSConst.html_table_td(balanceMap.get(day).getFirst().setScale(2, RoundingMode.HALF_UP).toString()) +
                                    SYSConst.html_table_td(balanceMap.get(day).getSecond().setScale(2, RoundingMode.HALF_UP).toString()) +
                                    SYSConst.html_table_td(linesum.setScale(2, RoundingMode.HALF_UP).toString())
                    ));

                }
//                BigDecimal avgIn = BigDecimal.ZERO;
//                BigDecimal avgOut = BigDecimal.ZERO;


                html.append(SYSConst.html_table(table.toString(), "1"));
            }

//
//            for (int i = 1; i <= count; i++) {
//                html.append("<td>");
//                html.append(rs.getString(i));
//                html.append("</td>");
//            }
//            if (zieltrink > 0) {
//                html.append("<td>" + (rs.getDouble("Einfuhr") - zieltrink) + "</td>");
//            }
//            avgEin += rs.getDouble("Einfuhr");
//            avgAus += rs.getDouble("Ausfuhr");
//            sumBilanz += rs.getDouble("Bilanz");
//
//            html.append("</tr>");
//            isCancelled = (Boolean) o[2];
        }
//        avgEin = avgEin / rows;
//        avgAus = avgAus / rows;
//        avgEin = Math.round(avgEin * 100d) / 100d;
//        avgAus = Math.round(avgAus * 100d) / 100d;
//        html.append("<tr><td></td><td></td><td>&Oslash; " + avgEin + "</td><td>&Oslash; " + avgAus + "</td><td>&Sigma; " + sumBilanz + "</td></tr>");
//        // Vormonat berechnen
//        stmtVormonat.setString(1, bwkennung);
//        stmtVormonat.setString(4, bwkennung);
//        ResultSet rsVormonat = stmtVormonat.executeQuery();
//
//        if (rsVormonat.first()) {
//            double avgEinVor = SYSTools.roundScale2(rsVormonat.getDouble("Einfuhr"));
//            double avgAusVor = SYSTools.roundScale2(rsVormonat.getDouble("Ausfuhr"));
//            double sumBilanzVor = SYSTools.roundScale2(rsVormonat.getDouble("Bilanz"));
//            html.append("<tr><td></td><td>" + df.format(vormonat) + "</td><td>&Oslash; " + avgEinVor + "</td><td>&Oslash; " + avgAusVor + "</td><td>&Sigma; " + sumBilanzVor + "</td></tr>");
//        }
//
//        rsVormonat.close();
//        html.append("</table>");
//
//
//        isCancelled = (Boolean) o[2];
//        String s = "";
//        if (!isCancelled) {
//            s = html.toString();
//        }
        return html.toString();
    }

    public static HashMap<DateMidnight, Pair<BigDecimal, BigDecimal>> getLiquidBalancePerDay(Resident resident, DateMidnight from, DateMidnight to) {
        // First BD is for the influx, second for the outflow
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("" +
                " SELECT rv FROM ResValue rv " +
                " WHERE rv.resident = :resident " +
                " AND rv.replacedBy IS NULL " +
                " AND rv.vtype.valType = :valType" +
                " AND rv.pit >= :from" +
                " AND rv.pit <= :to" +
                " ORDER BY rv.pit DESC ");
        query.setParameter("resident", resident);
        query.setParameter("valType", LIQUIDBALANCE);
        query.setParameter("from", from.toDate());
        query.setParameter("to", to.plusDays(1).toDateTime().minusSeconds(1).toDate());
        ArrayList<ResValue> list = null;
        list = new ArrayList<ResValue>(query.getResultList());
        em.close();

        // init with dates. so that there are now "empty" days
        HashMap<DateMidnight, Pair<BigDecimal, BigDecimal>> hm = new HashMap<DateMidnight, Pair<BigDecimal, BigDecimal>>();
        for (DateMidnight day = from; day.compareTo(to) <= 0; day = day.plusDays(1)) {
            hm.put(day, new Pair<BigDecimal, BigDecimal>(BigDecimal.ZERO, BigDecimal.ZERO));
        }

        for (ResValue val : list) {
            Pair<BigDecimal, BigDecimal> pair = hm.get(new DateMidnight(val.getPit()));
            BigDecimal ingestion = pair.getFirst();
            BigDecimal egestion = pair.getSecond();
            if (val.getVal1().compareTo(BigDecimal.ZERO) < 0) {
                egestion = egestion.add(val.getVal1().abs());
            } else {
                ingestion = ingestion.add(val.getVal1());
            }
            hm.put(new DateMidnight(val.getPit()), new Pair<BigDecimal, BigDecimal>(ingestion, egestion));
        }

        return hm;
    }

    public static BigDecimal getAvgIn(Resident resident, DateMidnight month) {
        DateTime from = month.dayOfMonth().withMinimumValue().toDateTime();
        DateTime to = month.dayOfMonth().withMaximumValue().plusDays(1).toDateTime().minusSeconds(1);

        // First BD is for the influx, second for the outflow
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("" +
                " SELECT AVG(rv.val1) FROM ResValue rv " +
                " WHERE rv.resident = :resident " +
                " AND rv.replacedBy IS NULL " +
                " AND rv.vtype.valType = :valType" +
                " AND rv.val1 >= 0 " +
                " AND rv.pit >= :from" +
                " AND rv.pit <= :to" +
                " ORDER BY rv.pit DESC ");
        query.setParameter("resident", resident);
        query.setParameter("valType", LIQUIDBALANCE);
        query.setParameter("from", from.toDate());
        query.setParameter("from", to.plusDays(1).toDateTime().minusSeconds(1).toDate());
        BigDecimal avg = (BigDecimal) query.getSingleResult();
        em.close();

        return avg == null ? BigDecimal.ZERO : avg;
    }

    public static HashMap<DateMidnight, BigDecimal> getLiquidIn(Resident resident, DateMidnight from) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("" +
                " SELECT rv FROM ResValue rv " +
                " WHERE rv.resident = :resident " +
                " AND rv.replacedBy IS NULL " +
                " AND rv.vtype.valType = :valType " +
                " AND rv.val1 > 0 " +
                " AND rv.pit >= :from" +
                " ORDER BY rv.pit DESC ");
        query.setParameter("resident", resident);
        query.setParameter("valType", LIQUIDBALANCE);
        query.setParameter("from", from.toDate());
        ArrayList<ResValue> list = new ArrayList<ResValue>(query.getResultList());
        em.close();

        HashMap<DateMidnight, BigDecimal> hm = new HashMap<DateMidnight, BigDecimal>();
        for (DateMidnight day = from; day.compareTo(new DateMidnight()) <= 0; day = day.plusDays(1)) {
            hm.put(day, BigDecimal.ZERO);
        }

        for (ResValue val : list) {
            BigDecimal bd = hm.get(new DateMidnight(val.getPit()));
            hm.put(new DateMidnight(val.getPit()), bd.add(val.getVal1()));
        }

        return hm;
    }

    public static HashMap<DateMidnight, BigDecimal> getLiquidOut(Resident resident, DateMidnight from) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("" +
                " SELECT rv FROM ResValue rv " +
                " WHERE rv.resident = :resident " +
                " AND rv.replacedBy IS NULL " +
                " AND rv.vtype.valType = :valType " +
                " AND rv.val1 < 0 " +
                " AND rv.pit >= :from" +
                " ORDER BY rv.pit DESC ");
        query.setParameter("resident", resident);
        query.setParameter("valType", LIQUIDBALANCE);
        query.setParameter("from", from.toDate());
        ArrayList<ResValue> list = new ArrayList<ResValue>(query.getResultList());
        em.close();

        HashMap<DateMidnight, BigDecimal> hm = new HashMap<DateMidnight, BigDecimal>();
        for (DateMidnight day = from; day.compareTo(new DateMidnight()) <= 0; day = day.plusDays(1)) {
            hm.put(day, BigDecimal.ZERO);
        }

        for (ResValue val : list) {
            BigDecimal bd = hm.get(new DateMidnight(val.getPit()));
            hm.put(new DateMidnight(val.getPit()), bd.add(val.getVal1()));
        }

        return hm;
    }


    /**
     * returns a list of triples of {Resident, ResValue, int} for residents which havent had a stool in the last int days.
     * The ResValue denotes the last stool, if there was any. NULL otherwise.
     *
     * @return
     */
    public static ArrayList<Object[]> getNoStool() {

        ArrayList<Resident> listResident = ResidentTools.getAllActive();
        ArrayList<Object[]> result = new ArrayList<Object[]>();

        DateTime now = new DateTime();

        for (Resident resident : listResident) {
            Properties controlling = resident.getControlling();
            if (controlling.containsKey(ResidentTools.KEY_STOOLDAYS) && !controlling.getProperty(ResidentTools.KEY_STOOLDAYS).equals("off")) {
                int days = Integer.parseInt(controlling.getProperty(ResidentTools.KEY_STOOLDAYS));
                ResValue lastStool = getLast(resident, STOOL);

                if (lastStool == null || lastStool.getPit().before(now.minusDays(days).toDate())) {
                    result.add(new Object[]{resident, lastStool, days});
                }
            }
        }

        listResident.clear();
        return result;
    }

    public static ArrayList<Object[]> getHighLowIn() {

        ArrayList<Resident> listResident = ResidentTools.getAllActive();
        ArrayList<Object[]> result = new ArrayList<Object[]>();


        DateMidnight now = new DateMidnight();

        for (Resident resident : listResident) {
            ArrayList<Pair<DateMidnight, BigDecimal>> violatingValues = new ArrayList<Pair<DateMidnight, BigDecimal>>();
            Properties controlling = resident.getControlling();


//            HashMap<DateMidnight, BigDecimal> balance = null;
            if ((controlling.containsKey(ResidentTools.KEY_LOWIN) && !controlling.getProperty(ResidentTools.KEY_LOWIN).equals("off")) ||
                    (controlling.containsKey(ResidentTools.KEY_HIGHIN) && !controlling.getProperty(ResidentTools.KEY_HIGHIN).equals("off"))) {
                int days = Integer.parseInt(controlling.getProperty(ResidentTools.KEY_DAYSDRINK));
                HashMap<DateMidnight, BigDecimal> in = getLiquidIn(resident, now.minusDays(days));
                if (!in.isEmpty()) {
                    for (DateMidnight day = now.minusDays(days); day.compareTo(new DateMidnight()) <= 0; day = day.plusDays(1)) {
                        if (in.get(day).compareTo(new BigDecimal(controlling.getProperty(ResidentTools.KEY_LOWIN))) < 0) {
                            violatingValues.add(new Pair<DateMidnight, BigDecimal>(day, in.get(day)));
                        }
                        if (in.get(day).compareTo(new BigDecimal(controlling.getProperty(ResidentTools.KEY_HIGHIN))) > 0) {
                            violatingValues.add(new Pair<DateMidnight, BigDecimal>(day, in.get(day)));
                        }
                    }
                }
            }

            if (!violatingValues.isEmpty()) {
                result.add(new Object[]{resident, violatingValues});
            }
        }

        listResident.clear();


        //Date last = op.care.values.DBHandling.lastWert(rs.getString("b.BWKennung"), DlgVital.MODE_STUHLGANG);
//                    ResInfo bwi = new ResInfo(rs.getLong("bi.BWInfoID"));
//                    HashMap antwort = (HashMap) ((HashMap) bwi.getAttribute().get(0)).get("antwort");
//                    boolean minkontrolle = antwort.get("c.einfuhr").toString().equalsIgnoreCase("true");
//                    boolean maxkontrolle = antwort.get("c.ueber").toString().equalsIgnoreCase("true");
//                    int tage = Integer.parseInt(antwort.get("c.einftage").toString());
//                    int minmenge = Integer.parseInt(antwort.get("c.einfmenge").toString());
//                    int maxmenge = Integer.parseInt(antwort.get("c.uebermenge").toString());
//                    if (!minkontrolle) {
//                        minmenge = -1000000; // Klein genug um im SQL Ausdruck ignoriert zu werden.
//                    }
//                    if (!maxkontrolle) {
//                        maxmenge = 1000000; // Groß genug um im SQL Ausdruck ignoriert zu werden.
//                    }
//
//                    String s = " SELECT * FROM (" +
//                            "       SELECT PIT, SUM(Wert) EINFUHR FROM ResValue " +
//                            "       WHERE ReplacedBy = 0 AND Wert > 0 AND BWKennung=? AND XML='<LIQUIDBALANCE/>' " +
//                            "       AND DATE(PIT) >= ADDDATE(DATE(now()), INTERVAL ? DAY) " +
//                            "       Group By DATE(PIT) " +
//                            "       ORDER BY PIT desc " +
//                            " ) a" +
//                            " WHERE a.EINFUHR < ? OR a.Einfuhr > ? ";
//                    PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(s);
//                    stmt1.setString(1, rs.getString("b.BWKennung"));
//                    stmt1.setInt(2, tage * -1);
//                    stmt1.setInt(3, minmenge);
//                    stmt1.setInt(4, maxmenge);
//
//                    ResultSet rs1 = stmt1.executeQuery();
//                    if (rs1.first()) {
//                        rs1.beforeFirst();
//                        while (rs1.next()) {
//
//                            if (html.length() == 0) {
//                                html.append("<h" + headertiefe + ">");
//                                html.append("Bewohner mit zu geringer / zu hoher Einfuhr");
//                                html.append("</h" + headertiefe + ">");
//                                html.append("<table border=\"1\"><tr>" +
//                                        "<th>BewohnerIn</th><th>Datum</th><th>Einfuhr (ml)</th><th>Bemerkung</th></tr>");
//                            }
//                            html.append("<tr>");
//                            String name = SYSTools.anonymizeBW(rs.getString("Nachname"), rs.getString("Vorname"), rs.getString("BWKennung"), rs.getInt("geschlecht"));
//                            html.append("<td>" + name + "</td>");
//                            html.append("<td>" + df.format(rs1.getDate("PIT")) + "</td>");
//                            html.append("<td>" + rs1.getString("Einfuhr") + "</td>");
//                            html.append("<td>" + (rs1.getDouble("Einfuhr") < minmenge ? "Einfuhr zu niedrig" : "Einfuhr zu hoch") + "</td>");
//                            html.append("</tr>");
//                        }
//                    }

        return result;

    }

}
