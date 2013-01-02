/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package entity.reports;

import entity.EntityTools;
import entity.Homes;
import entity.info.Resident;
import entity.info.ResidentTools;
import op.OPDE;
import op.controlling.PnlControlling;
import op.tools.Pair;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author tloehr
 */
public class NReportTools {

    public static NReport getFirstReport(Resident resident) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT p FROM NReport p "
                + " WHERE p.resident = :bewohner "
                + " ORDER BY p.pit");
        query.setParameter("bewohner", resident);
        query.setFirstResult(0);
        query.setMaxResults(1);
        NReport p = (NReport) query.getSingleResult();
        em.close();
        return p;
    }


    public static boolean isChangeable(NReport nReport) {
//        OPDE.debug(nReport.getPbid());
        return !nReport.isObsolete() && nReport.getResident().isActive() && nReport.getUsersAcknowledged().isEmpty() && (OPDE.isAdmin() || nReport.getUser().equals(OPDE.getLogin().getUser()));
    }

    /**
     * retrieves the PITs of the first and the last entries in the NReports and Handovers table.
     * The values are combined, so that the maximum span is calculated.
     *
     * @return
     */
    public static Pair<DateTime, DateTime> getMinMax() {
        long begin = System.currentTimeMillis();
        Pair<DateTime, DateTime> result = null;

        EntityManager em = OPDE.createEM();
        Query queryMin1 = em.createQuery("SELECT nr FROM NReport nr ORDER BY nr.pit ASC ");
        queryMin1.setMaxResults(1);

        Query queryMax1 = em.createQuery("SELECT nr FROM NReport nr ORDER BY nr.pit DESC ");
        queryMax1.setMaxResults(1);

        Query queryMin2 = em.createQuery("SELECT ho FROM Handovers ho ORDER BY ho.pit ASC ");
        queryMin2.setMaxResults(1);

        Query queryMax2 = em.createQuery("SELECT ho FROM Handovers ho ORDER BY ho.pit DESC ");
        queryMax2.setMaxResults(1);

        try {
            ArrayList<NReport> min1 = new ArrayList<NReport>(queryMin1.getResultList());
            ArrayList<NReport> max1 = new ArrayList<NReport>(queryMax1.getResultList());
            ArrayList<Handovers> min2 = new ArrayList<Handovers>(queryMin2.getResultList());
            ArrayList<Handovers> max2 = new ArrayList<Handovers>(queryMax2.getResultList());

            if (min1.isEmpty() && min2.isEmpty()) {
                result = null;
            } else {
                if (min1 == null) {
                    result = new Pair<DateTime, DateTime>(new DateTime(min2.get(0).getPit()), new DateTime(max2.get(0).getPit()));
                } else if (min2 == null) {
                    result = new Pair<DateTime, DateTime>(new DateTime(min1.get(0).getPit()), new DateTime(max1.get(0).getPit()));
                } else {
                    DateTime min3 = new DateTime(Math.min(min1.get(0).getPit().getTime(), min2.get(0).getPit().getTime()));
                    DateTime max3 = new DateTime(Math.max(max1.get(0).getPit().getTime(), max2.get(0).getPit().getTime()));
                    result = new Pair<DateTime, DateTime>(min3, max3);
                }
            }
        } catch (Exception e) {
            OPDE.fatal(e);
        }

        em.close();
        SYSTools.showTimeDifference(begin);
        return result;
    }

    /**
     * retrieves the PITs of the first and the last entry in the NReports table.
     *
     * @param resident
     * @return
     */
    public static Pair<DateTime, DateTime> getMinMax(Resident resident) {
        long begin = System.currentTimeMillis();
        Pair<DateTime, DateTime> result = null;

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createQuery("SELECT nr FROM NReport nr WHERE nr.resident = :resident ORDER BY nr.pit ASC ");
        queryMin.setParameter("resident", resident);
        queryMin.setMaxResults(1);

        Query queryMax = em.createQuery("SELECT nr FROM NReport nr WHERE nr.resident = :resident ORDER BY nr.pit DESC ");
        queryMax.setParameter("resident", resident);
        queryMax.setMaxResults(1);

        try {
            ArrayList<NReport> min = new ArrayList<NReport>(queryMin.getResultList());
            ArrayList<NReport> max = new ArrayList<NReport>(queryMax.getResultList());
            if (min.isEmpty()) {
                result = null;
            } else {
                result = new Pair<DateTime, DateTime>(new DateTime(min.get(0).getPit()), new DateTime(max.get(0).getPit()));
            }

        } catch (Exception e) {
            OPDE.fatal(e);
        }

        em.close();
        SYSTools.showTimeDifference(begin);
        return result;
    }


    public static long getNum(Resident resident, DateMidnight day) {
        long num = 0;

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createQuery("SELECT COUNT(nr) FROM NReport nr WHERE nr.resident = :resident AND nr.pit >= :start AND nr.pit <= :end");
        queryMin.setParameter("resident", resident);
        queryMin.setParameter("start", day.toDate());
        queryMin.setParameter("end", day.plusDays(1).toDateTime().minusSeconds(1).toDate());

        try {
            num = (Long) queryMin.getSingleResult();
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        em.close();
        return num;
    }

    /**
     * Führt die notwendigen Änderungen an den Entities durch, wenn ein Bericht geändert wurde. Dazu gehört auch die Dateien
     * und Vorgänge umzubiegen. Der alte Bericht verliert seine Dateien und Vorgänge. Es werden auch die
     * notwendigen Querverweise zwischen dem alten und dem neuen Bericht erstellt.
     *
     * @param oldReport der Bericht, der durch den <code>newReport</code> ersetzt werden soll.
     * @param newReport siehe oben
     * @return Erfolg oder nicht
     */
//    public static void editReport(EntityManager em, NReport oldReport, NReport newReport) throws Exception {
//
//
//    }

    /**
     * liefert eine Kopie eines Berichtes, die noch nicht persistiert wurde. * Somit ist PBID = 0
     * Gilt nicht für die Mappings (Dateien oder Vorgänge). Die werden erst bei editReport() geändert.
     *
     * @param source
     * @return
     */
//    public static NReport copyBericht(NReport source) {
//        NReport target = new NReport(source.getResident());
//        target.setMinutes(source.getMinutes());
//        target.setEditedBy(source.getEditedBy());
//        target.setEditpit(source.getEditDate());
//        target.setPit(source.getPit());
//        target.setReplacedBy(source.getReplacedBy());
//        target.setReplacementFor(source.getReplacementFor());
//        target.setText(source.getText());
//        target.setUser(source.getUser());
//
//        Iterator<NReportTAGS> tags = source.getTags().iterator();
//        while (tags.hasNext()) {
//            NReportTAGS tag = tags.next();
//            target.getTags().add(tag);
//        }
//
//        return target;
//    }

//    public static boolean saveBericht(NReport newBericht) {
//        boolean success = false;
//        EntityManager em = OPDE.createEM();
//        em.getTransaction().begin();
//        try {
//            em.persist(newBericht);
//            em.getTransaction().commit();
//            success = true;
//        } catch (Exception e) {
//            OPDE.getLogger().error(e.getMessage(), e);
//            em.getTransaction().rollback();
//        } finally {
//            em.close();
//        }
//        return success;
//    }

    /**
     * Berichtdarstellung für die Vorgänge.
     *
     * @param nReport
     * @param withResident
     * @return
     */
    public static String getNReportAsHTML(NReport nReport, boolean withResident) {
        String html = "";
        String text = SYSTools.replace(nReport.getText(), "\n", "<br/>", false);

        if (withResident) {
            html += "<b>Pflegebericht für " + ResidentTools.getLabelText(nReport.getResident()) + "</b>";
        } else {
            html += "<b>Pflegebericht</b>";
        }
        html += "<p>" + text + "</p>";
        return html;
    }

    public static String getPITAsHTML(NReport nReport) {
        DateFormat df = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        String html = "";
        html += df.format(nReport.getPit()) + "; " + nReport.getUser().getFullname();
        return html;
    }

    public static String getReportsAsHTML(List<NReport> nReports, boolean specialsOnly, boolean withlongheader, String subtitle, String highlight) {
        String html = "";
        boolean ihavesomethingtoshow = false;

        if (!nReports.isEmpty()) {
            html += "<h2 id=\"fonth2\" >" + OPDE.lang.getString("nursingrecords.reports") + (withlongheader ? " " + OPDE.lang.getString("misc.msg.for") + " " + ResidentTools.getLabelText(nReports.get(0).getResident()) : "") + "</h2>\n";
            html += SYSTools.catchNull(subtitle).isEmpty() ? "" : "<h3 id=\"fonth3\" >" + subtitle + "</h3>\n";
            html += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>"
                    + "<th>Info</th><th>Text</th>\n</tr>";
            for (NReport nReport : nReports) {
                if (!specialsOnly || nReport.isSpecial()) {
                    ihavesomethingtoshow = true;
                    html += "<tr>";
                    html += "<td valign=\"top\">" + getDateAndUser(nReport, true, false);
                    html += nReport.isReplaced() ? SYSConst.html_22x22_Eraser : "";
                    html += nReport.isReplacement() ? SYSConst.html_22x22_Edited : "";
                    html += "</td>";
                    html += "<td valign=\"top\">" + getAsHTML(nReport, highlight) + "</td>";
                    html += "</tr>\n";
                }
            }
            html += "</table>\n";
        }

        if (nReports.isEmpty() || !ihavesomethingtoshow) {
            html = ""; //SYSConst.html_italic("misc.msg.noentryyet");
        }
        return html;
    }

    private static String getHTMLColor(NReport nReport) {
        String color = "";
        if (nReport.isReplaced() || nReport.isDeleted()) {
            color = SYSConst.html_lightslategrey;
        } else {
            color = "color=\"black\"";// OPDE.getProps().getProperty(DFNTools.SHIFT_KEY_TEXT[SYSCalendar.whatShiftIs(nReport.getPit())] + "_FGBHP");
        }
        return color;
    }

    public static String getTagsAsHTML(NReport bericht) {
        String result = "";
        Iterator<NReportTAGS> itTags = bericht.getTags().iterator();
        while (itTags.hasNext()) {
            NReportTAGS tag = itTags.next();
            result += (tag.isBesonders() ? "<b>" : "");
            result += tag.getKurzbezeichnung();
            result += (tag.isBesonders() ? "</b>" : "");
            result += (itTags.hasNext() ? " " : "");
        }
        result += "";
        return result;
    }

    public static String getDateAndUser(NReport nReport, boolean showIDs, boolean showMinutes) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        result = sdf.format(nReport.getPit()) + "; " + nReport.getUser().getFullname();
        if (showMinutes && !nReport.isDeleted() && !nReport.isReplaced()) {
            result += "<br/>" + OPDE.lang.getString("misc.msg.Effort") + ": " + nReport.getMinutes() + " " + OPDE.lang.getString("misc.msg.Minute(s)");
        }
        result += SYSTools.catchNull(getTagsAsHTML(nReport), "<br/>[", "]") + " ";
        if (showIDs) {
            result += "<br/><i>[" + nReport.getPbid() + "]</i>";
        }
        return result;
    }

    /**
     * gibt eine HTML Darstellung des Berichtes zurück.
     *
     * @return
     */
    public static String getAsHTML(NReport nReport, String highlight) {
        String result = "<div id=\"fonttext\">";

//        result += getDateAndUser(nReport, false, true);

//        result += SYSTools.catchNull(getTagsAsHTML(nReport), " [", "]") + " ";

        DateFormat df = DateFormat.getDateTimeInstance();

        if (nReport.isDeleted()) {
            result += "<br/>" + OPDE.lang.getString("misc.msg.thisentryhasbeendeleted") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(nReport.getEditDate()) + " <br/>" + OPDE.lang.getString("misc.msg.Bywhom") + " " + nReport.getEditedBy().getFullname() + "<br/>";
        }
        if (nReport.isReplacement() && !nReport.isReplaced()) {
            result += "<br/>" + OPDE.lang.getString("misc.msg.thisEntryIsAReplacement") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(nReport.getReplacementFor().getEditDate()) + " <br/>" + "<br/>" + OPDE.lang.getString("misc.msg.originalentry") + ": " + nReport.getReplacementFor().getPbid() + "<br/>";
        }
        if (nReport.isReplaced()) {
            result += "<br/>" + OPDE.lang.getString("misc.msg.thisentryhasbeenedited") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(nReport.getEditDate()) + " <br/>" + OPDE.lang.getString("misc.msg.Bywhom") + " " + nReport.getEditedBy().getFullname();
            result += "<br/>" + OPDE.lang.getString("misc.msg.replaceentry") + ": " + nReport.getReplacedBy().getPbid() + "<br/>";
        }
//        if (!nReport.getAttachedFilesConnections().isEmpty()) {
//            result += "<font color=\"green\">&#9679;</font>";
//        }
//        if (!nReport.getAttachedQProcessConnections().isEmpty()) {
//            result += "<font color=\"red\">&#9679;</font>";
//        }

        String tmp = SYSTools.replace(nReport.getText(), "\n", "<br/>", false);
        if (!SYSTools.catchNull(highlight).isEmpty()) {
            tmp = SYSTools.replace(tmp, highlight, "<font style=\"BACKGROUND-COLOR: yellow\">" + highlight + "</font>", true);
        }

        result += "<p>" + tmp + "<p/>";

        result += "<div/>";
        return result;
    }

    public static String getInfoAsHTML(NReport nReport) {
        String result = "<div id=\"fonttext\">";

        result += "[" + nReport.getPbid() + "]<br/>";

        DateFormat df = DateFormat.getDateTimeInstance();
        if (nReport.isDeleted()) {
            result += "<br/>" + OPDE.lang.getString("misc.msg.thisentryhasbeendeleted") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(nReport.getEditDate()) + " <br/>" + OPDE.lang.getString("misc.msg.Bywhom") + " " + nReport.getEditedBy().getFullname() + "<br/>";
        } else if (nReport.isReplacement() && !nReport.isReplaced()) {
            result += "<br/>" + OPDE.lang.getString("misc.msg.thisEntryIsAReplacement") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(nReport.getReplacementFor().getEditDate()) + " <br/>" + "<br/>" + OPDE.lang.getString("misc.msg.originalentry") + ": " + nReport.getReplacementFor().getPbid() + "<br/>";
        } else if (nReport.isReplaced()) {
            result += "<br/>" + OPDE.lang.getString("misc.msg.thisentryhasbeenedited") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(nReport.getEditDate()) + " <br/>" + OPDE.lang.getString("misc.msg.Bywhom") + " " + nReport.getEditedBy().getFullname();
            result += "<br/>" + OPDE.lang.getString("misc.msg.replaceentry") + ": " + nReport.getReplacedBy().getPbid() + "<br/>";
        } else {
            result += "<br/>" + OPDE.lang.getString("misc.msg.created") + " " + OPDE.lang.getString("misc.msg.atchrono") + ": " + df.format(nReport.getEditDate()) + " <br/>" + OPDE.lang.getString("misc.msg.Bywhom") + ": " + nReport.getUser().getFullname();
        }


        return result + "</div>";
    }


    public static String getBVActivites(DateMidnight from, Closure progress) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
        ArrayList<Resident> listResidents = ResidentTools.getAllActive();
        StringBuilder html = new StringBuilder(1000);

        int p = -1;
        progress.execute(new Pair<Integer, Integer>(p, listResidents.size()));

        html.append(SYSConst.html_h1_open + OPDE.lang.getString(PnlControlling.internalClassID + ".orga.bvactivities") + SYSConst.html_h1_close);

        p = 0;
        for (Resident resident : listResidents) {
            p++;
            progress.execute(new Pair<Integer, Integer>(p, listResidents.size()));

            ArrayList<NReport> listReports = getBVActivities(resident, from);

            html.append(SYSConst.html_h2_open + ResidentTools.getTextCompact(resident) + SYSConst.html_h2_close);

            if (resident.getPN1() == null) {
                html.append(SYSConst.html_div(SYSConst.html_bold(OPDE.lang.getString(PnlControlling.internalClassID + ".orga.bvactivities.nobv"))));
            } else {
                html.append(SYSConst.html_div(SYSConst.html_bold(OPDE.lang.getString("misc.msg.primaryNurse")) + ": " + resident.getPN1().getFullname()));
            }

            if (listReports.isEmpty()) {
                html.append(SYSConst.html_div(SYSConst.html_bold(OPDE.lang.getString("misc.msg.nodata"))));
            } else {
                html.append("<table id=\"fonttext\" border=\"1\">" +
                        SYSConst.html_table_tr(
                                SYSConst.html_table_th(OPDE.lang.getString("misc.msg.Date")) +
                                        SYSConst.html_table_th(OPDE.lang.getString("misc.msg.Text")) +
                                        SYSConst.html_table_th(OPDE.lang.getString("misc.msg.user"))
                        ));

                for (NReport nReport : listReports) {
                    html.append(SYSConst.html_table_tr(
                            SYSConst.html_table_td(df.format(nReport.getPit()), null) +
                                    SYSConst.html_table_td(SYSConst.html_paragraph(nReport.getText()), null) +
                                    SYSConst.html_table_td(nReport.getUser().getFullname(), null)
                    ));
                }
                html.append("</table>");
            }
        }
        return html.toString();
    }

    public static ArrayList<NReport> getBVActivities(Resident resident, DateMidnight from) {

        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;

        try {

            String jpql = " SELECT nr " +
                    " FROM NReport nr " +
                    " JOIN nr.tags tg " +
                    " WHERE " +
                    " nr.resident = :resident " +
                    " AND nr.pit >= :from " +
                    " AND tg.system = :bv " +
                    " AND nr.replacedBy IS NULL " +
//                    " AND nr.resident.station IS NOT NULL " +
//                    " AND nr.resident.adminonly <> 2 " +
                    " ORDER BY nr.pit ";

            Query query = em.createQuery(jpql);
            query.setParameter("resident", resident);
            query.setParameter("from", from.toDate());
            query.setParameter("bv", NReportTAGSTools.TYPE_SYS_BV);

            list = new ArrayList<NReport>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }


        return list;
    }

    public static String getTimes4SocialReports(DateMidnight month, Closure progress) {
        StringBuilder html = new StringBuilder(1000);
        Format monthFormmatter = new SimpleDateFormat("MMMM yyyy");
        DateTime from = month.dayOfMonth().withMinimumValue().toDateTime();
        DateTime to = month.dayOfMonth().withMaximumValue().plusDays(1).toDateTime().minusSeconds(1);

        int p = -1;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        EntityManager em = OPDE.createEM();

        String jpql1 = " " +
                " SELECT DISTINCT n FROM NReport n " +
                " JOIN n.tags t " +
                " WHERE n.pit >= :from AND n.pit <= :to AND n.replacedBy IS NULL AND n.resident.adminonly <> 2 AND t.system = :tagsystem ORDER BY n.resident.rid ";
        Query query1 = em.createQuery(jpql1);
        query1.setParameter("tagsystem", NReportTAGSTools.TYPE_SYS_SOCIAL);
        query1.setParameter("from", from.toDate());
        query1.setParameter("to", to.toDate());
        ArrayList<NReport> listNR = new ArrayList<NReport>(query1.getResultList());

        em.close();

        NReportTAGS pea = NReportTAGSTools.getByShortDescription("PEA");
        NReportTAGS social = NReportTAGSTools.getByShortDescription("Soz");

        HashMap<Resident, Pair<Integer, Integer>> statmap = new HashMap<Resident, Pair<Integer, Integer>>();
        p = 0;
        for (NReport nr : listNR) {
            p++;
            progress.execute(new Pair<Integer, Integer>(p, listNR.size()));

            if (!statmap.containsKey(nr.getResident())) {
                statmap.put(nr.getResident(), new Pair<Integer, Integer>(0, 0));
            }
            Pair<Integer, Integer> pair = statmap.get(nr.getResident());
            int socialtime = pair.getFirst();
            int peatime = pair.getSecond();

            if (nr.getTags().contains(pea)) {
                peatime += nr.getMinutes();
            }
            if (nr.getTags().contains(social)) {
                socialtime += nr.getMinutes();
            }

            statmap.put(nr.getResident(), new Pair<Integer, Integer>(socialtime, peatime));
        }

        html.append(SYSConst.html_h1(OPDE.lang.getString(PnlControlling.internalClassID + ".nursing.social")));
        html.append(SYSConst.html_h2(monthFormmatter.format(month.toDate())));


        StringBuilder table = new StringBuilder(1000);
        table.append(SYSConst.html_table_tr(
                SYSConst.html_table_th("misc.msg.resident") +
                        SYSConst.html_table_th(OPDE.lang.getString("misc.msg.Effort") + " (" + OPDE.lang.getString("misc.msg.Minutes") + ")") +
                        SYSConst.html_table_th(OPDE.lang.getString("misc.msg.Effort") + " (" + OPDE.lang.getString("misc.msg.Hours") + ")") +
                        SYSConst.html_table_th(OPDE.lang.getString(PnlControlling.internalClassID + ".nursing.social.averageHoursPerDay")) +
                        SYSConst.html_table_th("PEA " + OPDE.lang.getString("misc.msg.Effort") + " (" + OPDE.lang.getString("misc.msg.Minutes") + ")") +
                        SYSConst.html_table_th("PEA " + OPDE.lang.getString("misc.msg.Effort") + " (" + OPDE.lang.getString("misc.msg.Hours") + ")") +
                        SYSConst.html_table_th("PEA " + OPDE.lang.getString(PnlControlling.internalClassID + ".nursing.social.averageHoursPerDay"))
        ));

        BigDecimal daysinmonth = new BigDecimal(month.dayOfMonth().withMaximumValue().getDayOfMonth());

        ArrayList<Resident> listResident = new ArrayList<Resident>(statmap.keySet());
        Collections.sort(listResident);

        for (Resident resident : listResident) {

            Pair<Integer, Integer> pair = statmap.get(resident);
            BigDecimal socialtime = new BigDecimal(pair.getFirst());
            BigDecimal peatime = new BigDecimal(pair.getSecond());

            boolean highlight = socialtime.equals(BigDecimal.ZERO) || peatime.equals(BigDecimal.ZERO);

            table.append(SYSConst.html_table_tr(
                    SYSConst.html_table_td(ResidentTools.getTextCompact(resident)) +
                            SYSConst.html_table_td(socialtime.toString(), socialtime.equals(BigDecimal.ZERO)) +
                            SYSConst.html_table_td(socialtime.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP).toString(), socialtime.equals(BigDecimal.ZERO)) +
                            SYSConst.html_table_td(socialtime.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP).divide(daysinmonth, 2, BigDecimal.ROUND_HALF_UP).toString(), socialtime.equals(BigDecimal.ZERO)) +
                            SYSConst.html_table_td(peatime.toString(), peatime.equals(BigDecimal.ZERO)) +
                            SYSConst.html_table_td(peatime.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP).toString(), peatime.equals(BigDecimal.ZERO)) +
                            SYSConst.html_table_td(peatime.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP).divide(daysinmonth, 2, BigDecimal.ROUND_HALF_UP).toString(), peatime.equals(BigDecimal.ZERO))
                    , highlight));
        }

        html.append(SYSConst.html_table(table.toString(), "1"));
        html.append(SYSConst.html_paragraph(PnlControlling.internalClassID + ".nursing.social.peaexplain"));

        return html.toString();
    }

    public static ArrayList<NReport> getNReports4Month(Resident resident, DateMidnight month) {
        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;
        DateTime from = month.dayOfMonth().withMinimumValue().toDateTime();
        DateTime to = month.toDateTime().dayOfMonth().withMaximumValue().secondOfDay().withMaximumValue();
        OPDE.debug(to);
        try {

            String jpql = " SELECT nr " +
                    " FROM NReport nr " +
                    " WHERE nr.resident = :resident " +
                    " AND nr.pit >= :from AND nr.pit <= :to " +
                    " ORDER BY nr.pit ASC ";

            Query query = em.createQuery(jpql);

            query.setParameter("resident", resident);
            query.setParameter("from", from.toDate());
            query.setParameter("to", to.toDate());

            list = new ArrayList<NReport>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    public static ArrayList<NReport> getNReports4Week(Resident resident, DateMidnight week) {
        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;
        DateTime from = week.dayOfWeek().withMinimumValue().toDateTime();
        DateTime to = week.toDateTime().dayOfWeek().withMaximumValue().secondOfDay().withMaximumValue();
        OPDE.debug(to);
        try {

            String jpql = " SELECT nr " +
                    " FROM NReport nr " +
                    " WHERE nr.resident = :resident " +
                    " AND nr.pit >= :from AND nr.pit <= :to " +
                    " ORDER BY nr.pit ASC ";

            Query query = em.createQuery(jpql);

            query.setParameter("resident", resident);
            query.setParameter("from", from.toDate());
            query.setParameter("to", to.toDate());

            list = new ArrayList<NReport>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    public static ArrayList<NReport> getNReports4Day(Resident resident, DateMidnight day) {
        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;
        DateTime from = day.toDateTime();
        DateTime to = day.plusDays(1).toDateTime().minusSeconds(1);

        try {

            String jpql = " SELECT nr " +
                    " FROM NReport nr " +
                    " WHERE nr.resident = :resident " +
                    " AND nr.pit >= :from AND nr.pit <= :to " +
                    " ORDER BY nr.pit DESC ";

            Query query = em.createQuery(jpql);

            query.setParameter("resident", resident);
            query.setParameter("from", from.toDate());
            query.setParameter("to", to.toDate());

//            long a = System.currentTimeMillis();

            list = new ArrayList<NReport>(query.getResultList());

//            long b = System.currentTimeMillis();

//            OPDE.debug((b - a) + " ms");

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    /**
     * @param resident
     * @param search
     * @return
     */
    public static ArrayList<NReport> getNReports4Search(Resident resident, String search) {
        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;

        try {

            String jpql = " SELECT nr " +
                    " FROM NReport nr " +
                    " WHERE nr.resident = :resident " +
                    " AND nr.text like :search " +
                    " ORDER BY nr.pit DESC";

            Query query = em.createQuery(jpql);

            query.setParameter("resident", resident);
            query.setParameter("search", EntityTools.getMySQLsearchPattern(search));

            list = new ArrayList<NReport>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    /**
     * retrieves all NReports for a certain day which have been assigned with the Tags Nr. 1 (Handover) and Nr. 2 (Emergency)
     *
     * @param day
     * @return
     */
    public static ArrayList<NReport> getNReports4Handover(DateMidnight day, Homes home) {
        DateTime from = day.toDateTime();
        DateTime to = day.plusDays(1).toDateTime().minusSeconds(1);
        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;

        try {

            String jpql = " SELECT nr " +
                    " FROM NReport nr " +
                    " JOIN nr.tags t " +
                    " WHERE " +
                    " nr.pit >= :from AND nr.pit <= :to AND (t.system = :handover OR t.nrtagid = :emergency) " +
                    " AND nr.resident.station.home = :home " +
                    " AND nr.replacedBy IS NULL AND nr.editedBy IS NULL " +
                    " ORDER BY nr.pit ASC ";

            Query query = em.createQuery(jpql);

            query.setParameter("from", from.toDate());
            query.setParameter("to", to.toDate());
            query.setParameter("home", home);
            query.setParameter("handover", NReportTAGSTools.TYPE_SYS_HANDOVER);
            query.setParameter("emergency", NReportTAGSTools.TYPE_SYS_EMERGENCY);

            list = new ArrayList<NReport>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    public static ArrayList<NReport> getNReports4Tags(Resident resident, NReportTAGS tag) {
//        DateTime from = day.toDateTime();
//        DateTime to = day.plusDays(1).toDateTime().minusSeconds(1);
        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;

        try {

            String jpql = " SELECT nr " +
                    " FROM NReport nr " +
                    " JOIN nr.tags t " +
                    " WHERE nr.resident = :resident " +
                    " AND t = :tag " +
//                    " AND nr.pit >= :from AND nr.pit <= :to  " +
                    " ORDER BY nr.pit ASC ";

            Query query = em.createQuery(jpql);

            query.setParameter("resident", resident);
            query.setParameter("tag", tag);
//            query.setParameter("from", from.toDate());
//            query.setParameter("to", to.toDate());

            list = new ArrayList<NReport>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    /**
     * @param resident
     * @return
     */
    public static ArrayList<NReport> getReportsWithFilesOnly(Resident resident) {
        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;

        try {

            String jpql = " SELECT nr " +
                    " FROM NReport nr " +
                    " JOIN nr.attachedFilesConnections nraf " +
                    " WHERE nr.resident = :resident " +
                    " ORDER BY nr.pit ";

            Query query = em.createQuery(jpql);
            query.setParameter("resident", resident);

            list = new ArrayList<NReport>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

}
