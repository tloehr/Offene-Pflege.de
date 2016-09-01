/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package entity.reports;

import entity.EntityTools;
import entity.building.Homes;
import entity.files.SYSNR2FILE;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.process.QProcessElement;
import entity.process.SYSNR2PROCESS;
import entity.system.Commontags;
import entity.system.CommontagsTools;
import entity.system.Users;
import op.OPDE;
import op.tools.Pair;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.MutableInterval;

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

    public static boolean ALLOW_EDIT_OF_OTHER_PEOPLES_REPORTS = false;

    public static boolean isChangeable(NReport nReport) {
        return (ALLOW_EDIT_OF_OTHER_PEOPLES_REPORTS || nReport.isMine()) && !nReport.isObsolete() && nReport.getResident().isActive() && nReport.getUsersAcknowledged().isEmpty();
    }

    /**
     * retrieves the PITs of the first and the last entries in the NReports and Handovers table.
     * The values are combined, so that the maximum span is calculated.
     *
     * @return
     */
    public static Pair<DateTime, DateTime> getMinMax() {
        Pair<DateTime, DateTime> result = null;
        long min, max;

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


            if (min1.isEmpty() && min2.isEmpty()) { // that means, that there is now report at all
                result = null;
            } else {
                long mi1 = min1.isEmpty() ? System.currentTimeMillis() : min1.get(0).getPit().getTime();
                long mi2 = min2.isEmpty() ? System.currentTimeMillis() : min2.get(0).getPit().getTime();
                min = Math.min(mi1, mi2);

                long ma1 = max1.isEmpty() ? System.currentTimeMillis() : max1.get(0).getPit().getTime();
                long ma2 = max2.isEmpty() ? System.currentTimeMillis() : max2.get(0).getPit().getTime();
                max = Math.max(ma1, ma2);

                result = new Pair<DateTime, DateTime>(new DateTime(min), new DateTime(max));

            }
        } catch (Exception e) {
            OPDE.fatal(e);
        }

        em.close();
        return result;
    }

    /**
     * retrieves the PITs of the first and the last entry in the NReports table.
     *
     * @param resident
     * @return
     */
    public static MutableInterval getMinMax(Resident resident) {
        long time = System.currentTimeMillis();
        MutableInterval result = null;

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
                result = new MutableInterval(new DateTime(min.get(0).getPit()), new DateTime(max.get(0).getPit()));
            }

        } catch (Exception e) {
            OPDE.fatal(e);
        }


        em.close();
        OPDE.debug((System.currentTimeMillis() - time) + " ms_xx");
        return result;
    }


    public static long getNum(Resident resident, LocalDate day) {
        long num = 0;

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createQuery("SELECT COUNT(nr) FROM NReport nr WHERE nr.resident = :resident AND nr.pit >= :start AND nr.pit <= :end");
        queryMin.setParameter("resident", resident);
        queryMin.setParameter("start", day.toDateTimeAtStartOfDay().toDate());
        queryMin.setParameter("end", SYSCalendar.eod(day).toDate());

        try {
            num = (Long) queryMin.getSingleResult();
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        em.close();
        return num;
    }

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
        html += df.format(nReport.getPit()) + "; " + nReport.getNewBy().getFullname();
        return html;
    }

    public static String getNReportsAsHTML(List<NReport> nReports, boolean withlongheader, String subtitle, String highlight) {
        return getNReportsAsHTML(nReports, true, withlongheader, subtitle, highlight, true);
    }

    public static String getNReportsAsHTML(List<NReport> nReports, boolean withHeader, boolean withlongheader, String subtitle, String highlight, boolean withObsoletes) {
        String result = "";

        if (!nReports.isEmpty()) {

            String html = "";

            if (withHeader) {
                html += "<h1 id=\"fonth1\" >" + SYSTools.xx("nursingrecords.reports") + (withlongheader ? " " + SYSTools.xx("misc.msg.for") + " " + ResidentTools.getLabelText(nReports.get(0).getResident()) : "") + "</h1>\n";
            }
            html += SYSTools.catchNull(subtitle).isEmpty() ? "" : "<h2 id=\"fonth2\" >" + subtitle + "</h2>\n";


            LocalDate prevDate = null;
            for (NReport nreport : nReports) {

                if (withObsoletes || !nreport.isObsolete()) {

                    LocalDate currentDate = new LocalDate(nreport.getPit());

                    if (prevDate == null || !prevDate.equals(currentDate)) {
                        prevDate = currentDate;
                        html += SYSTools.catchNull(subtitle).isEmpty() ? "<h2 id=\"fonth2\" >" + currentDate.toString("EEEE, dd.MM.yyyy") + "</h2>\n" : "<h3 id=\"fonth3\" >" + currentDate.toString("EEEE, dd.MM.yyyy") + "</h3>\n";
                    }


                    html += SYSConst.html_bold(

                            (nreport.isObsolete() ? SYSConst.html_16x16_Eraser : "") +
                                    (nreport.isReplacement() ? SYSConst.html_16x16_Edited : "") +
                                    DateFormat.getTimeInstance(DateFormat.SHORT).format(nreport.getPit()) +
                                    " " + SYSTools.xx("misc.msg.Time.short") +
                                    ", " + nreport.getMinutes() + " " + SYSTools.xx("misc.msg.Minute(s)") +
                                    ", " + nreport.getNewBy().getFullname() +
                                    (nreport.getCommontags().isEmpty() ? "" : " " + CommontagsTools.getAsHTML(nreport.getCommontags(), SYSConst.html_16x16_tagPurple))

                    );

                    html += "<br/>";
                    html += getAsHTML(nreport, highlight);


//                    result = SYSConst.html_paragraph(html);


                }
            }

            result += html;

        } else {
            result = SYSConst.html_italic("misc.msg.noentryyet");
        }


        return result;
    }


//    public static ArrayList<Element> getNReportsAsPDF(List<NReport> nReports, boolean withObsoletes) throws DocumentException, IOException {
//        String result = "";
//
//        ArrayList<Element> listElements = new ArrayList<>();
//
//        String header = SYSTools.xx("nursingrecords.reports") + " " + SYSTools.xx("misc.msg.for") + " " + ResidentTools.getLabelText(nReports.get(0).getResident());
//
//        Paragraph h1 = new Paragraph(new Phrase(header, PDF.plain(PDF.sizeH1())));
//        h1.setAlignment(Element.ALIGN_CENTER);
//        listElements.add(h1);
//
//
//        Paragraph p = new Paragraph(SYSTools.xx("nursingrecords.prescription.dailyplan.warning"));
//        p.setAlignment(Element.ALIGN_CENTER);
//        listElements.add(p);
//        listElements.add(Chunk.NEWLINE);
//
//        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
//
//        if (!nReports.isEmpty()) {
//
//
//            LocalDate prevDate = null;
//            for (NReport nreport : nReports) {
//
//                if (withObsoletes || !nreport.isObsolete()) {
//
//                    LocalDate currentDate = new LocalDate(nreport.getPit());
//
//                    if (prevDate == null || !prevDate.equals(currentDate)) {
//                        prevDate = currentDate;
//
//                        Paragraph h2 = new Paragraph(new Phrase(currentDate.toString("EEEE, dd.MM.yyyy"), PDF.plain(PDF.sizeH2())));
//                        h2.setAlignment(Element.ALIGN_CENTER);
//                        listElements.add(h2);
//                    }
//
//                    listElements.add(SYSConst.getPDF_16x16_tagPurple());
//
//                    html += SYSConst.html_bold(
//
//                            (nreport.isObsolete() ? SYSConst.html_16x16_Eraser : "") +
//                                    (nreport.isReplacement() ? SYSConst.html_16x16_Edited : "") +
//                                    DateFormat.getTimeInstance(DateFormat.SHORT).format(nreport.getPit()) +
//                                    " " + SYSTools.xx("misc.msg.Time.short") +
//                                    ", " + nreport.getMinutes() + " " + SYSTools.xx("misc.msg.Minute(s)") +
//                                    ", " + nreport.getUser().getFullname() +
//                                    (nreport.getCommontags().isEmpty() ? "" : " " + CommontagsTools.getAsHTML(nreport.getCommontags(), SYSConst.html_16x16_tagPurple))
//
//                    );
//
//                    html += "<br/>";
//                    html += getAsHTML(nreport, highlight);
//
//
//                    //                    result = SYSConst.html_paragraph(html);
//
//
//                }
//            }
//
//            result += html;
//
//        } else {
//            result = SYSConst.html_italic("misc.msg.noentryyet");
//        }
//
//
//        return listElements;
//    }

    public static String getReportsAndHandoversAsHTML(List<QProcessElement> reports, String highlight, int year) {
        String html = "";
//        boolean ihavesomethingtoshow = false;

        if (!reports.isEmpty()) {
            html += SYSConst.html_h2(SYSTools.xx("nursingrecords.handover.searchresults"));
            html += SYSConst.html_h3(SYSTools.xx("misc.msg.period") + ": " + year);

            String table = "";
            table += SYSConst.html_table_tr(
                    SYSConst.html_table_th("misc.msg.DateAndUser") +
                            SYSConst.html_table_th("misc.msg.resident") +
                            SYSConst.html_table_th("misc.msg.Text")
            );

//            html += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr><th>Info</th><th>Text</th>\n</tr>";
            for (QProcessElement report : reports) {
                String dateAndUser = (report instanceof NReport ? NReportTools.getDateAndUser((NReport) report, false, false) : HandoversTools.getDateAndUser((Handovers) report, false));
                String resident = (report instanceof NReport ? ResidentTools.getFullName(report.getResident()) : "--");


                String text;
                if (report instanceof NReport) {
                    text = NReportTools.getAsHTML((NReport) report, highlight);
                } else {
                    if (!SYSTools.catchNull(highlight).isEmpty()) {
                        text = SYSTools.replace(((Handovers) report).getText(), highlight, "<font style=\"BACKGROUND-COLOR: yellow\">" + highlight + "</font>", true);
                    } else {
                        text = ((Handovers) report).getText();
                    }
                }

                table += SYSConst.html_table_tr(
                        SYSConst.html_table_td(dateAndUser) +
                                SYSConst.html_table_td(resident) +
                                SYSConst.html_table_td(text)
                );


            }
            html += SYSConst.html_table(table, "1");
        }

//        if (nReports.isEmpty() || !ihavesomethingtoshow) {
//            html = ""; //SYSConst.html_italic("misc.msg.noentryyet");
//        }
        return html;
    }


    public static String getDateAndUser(NReport nReport, boolean showIDs, boolean showMinutes) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        result = sdf.format(nReport.getPit()) + "; " + nReport.getNewBy().getFullname();
        if (showMinutes && !nReport.isDeleted() && !nReport.isReplaced()) {
            result += "<br/>" + SYSTools.xx("misc.msg.Effort") + ": " + nReport.getMinutes() + " " + SYSTools.xx("misc.msg.Minute(s)");
        }
//        result += SYSTools.catchNull(getTagsAsHTML(nReport), "<br/>[", "]") + " ";
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
            result += "<br/>" + SYSTools.xx("misc.msg.thisentryhasbeendeleted") + " <br/>" + SYSTools.xx("misc.msg.atchrono") + " " + df.format(nReport.getDelPIT()) + " <br/>" + SYSTools.xx("misc.msg.Bywhom") + " " + nReport.getDeletedBy().getFullname() + "<br/>";
        }
        if (nReport.isReplacement() && !nReport.isReplaced()) {
            result += "<br/>" + SYSTools.xx("misc.msg.thisEntryIsAReplacement") + " <br/>" + SYSTools.xx("misc.msg.atchrono") + " " + df.format(nReport.getReplacementFor().getEditedPIT()) + " <br/>" + "<br/>" + SYSTools.xx("misc.msg.originalentry") + ": " + nReport.getReplacementFor().getPbid() + "<br/>";
        }
        if (nReport.isReplaced()) {
            result += "<br/>" + SYSTools.xx("misc.msg.thisentryhasbeenedited") + " <br/>" + SYSTools.xx("misc.msg.atchrono") + " " + df.format(nReport.getEditedPIT()) + " <br/>" + SYSTools.xx("misc.msg.Bywhom") + " " + nReport.getEditedBy().getFullname();
            result += "<br/>" + SYSTools.xx("misc.msg.replaceentry") + ": " + nReport.getReplacedBy().getPbid() + "<br/>";
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

        result += "<p>" + tmp + "</p>";

        result += "<div/>";
        return result;
    }

    public static String getInfoAsHTML(NReport nReport) {
        String result = "<div id=\"fonttext\">";

        result += "[" + nReport.getPbid() + "]<br/>";

        DateFormat df = DateFormat.getDateTimeInstance();
        if (nReport.isDeleted()) {
            result += "<br/>" + SYSTools.xx("misc.msg.thisentryhasbeendeleted") + " <br/>" + SYSTools.xx("misc.msg.atchrono") + " " + df.format(nReport.getDelPIT()) + " <br/>" + SYSTools.xx("misc.msg.Bywhom") + " " + nReport.getDeletedBy().getFullname() + "<br/>";
        } else if (nReport.isReplacement() && !nReport.isReplaced()) {
            result += "<br/>" + SYSTools.xx("misc.msg.thisEntryIsAReplacement") + " <br/>" + SYSTools.xx("misc.msg.atchrono") + " " + df.format(nReport.getReplacementFor().getNewPIT()) + " <br/>" + "<br/>" + SYSTools.xx("misc.msg.originalentry") + ": " + nReport.getReplacementFor().getPbid() + "<br/>";
        } else if (nReport.isReplaced()) {
            result += "<br/>" + SYSTools.xx("misc.msg.thisentryhasbeenedited") + " <br/>" + SYSTools.xx("misc.msg.atchrono") + " " + df.format(nReport.getEditedPIT()) + " <br/>" + SYSTools.xx("misc.msg.Bywhom") + " " + nReport.getEditedBy().getFullname();
            result += "<br/>" + SYSTools.xx("misc.msg.replaceentry") + ": " + nReport.getReplacedBy().getPbid() + "<br/>";
        } else {
            result += "<br/>" + SYSTools.xx("misc.msg.created") + " " + SYSTools.xx("misc.msg.atchrono") + ": " + df.format(nReport.getNewPIT()) + " <br/>" + SYSTools.xx("misc.msg.Bywhom") + ": " + nReport.getNewBy().getFullname();
        }


        return result + "</div>";
    }


    public static String getBVActivites(LocalDate from, Closure progress) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
        ArrayList<Resident> listResidents = ResidentTools.getAllActive();
        StringBuilder html = new StringBuilder(1000);

        int p = -1;
        progress.execute(new Pair<Integer, Integer>(p, listResidents.size()));

        html.append(SYSConst.html_h1_open + SYSTools.xx("opde.controlling.orga.bvactivities") + SYSConst.html_h1_close);

        p = 0;
        for (Resident resident : listResidents) {
            p++;
            progress.execute(new Pair<Integer, Integer>(p, listResidents.size()));

            ArrayList<NReport> listReports = getBVActivities(resident, from);

            html.append(SYSConst.html_h2_open + ResidentTools.getTextCompact(resident) + SYSConst.html_h2_close);

            if (resident.getPN1() == null) {
                html.append(SYSConst.html_div(SYSConst.html_bold(SYSTools.xx("opde.controlling.orga.bvactivities.nobv"))));
            } else {
                html.append(SYSConst.html_div(SYSConst.html_bold(SYSTools.xx("misc.msg.primaryNurse")) + ": " + resident.getPN1().getFullname()));
            }

            if (listReports.isEmpty()) {
                html.append(SYSConst.html_div(SYSConst.html_bold(SYSTools.xx("misc.msg.nodata"))));
            } else {
                html.append("<table id=\"fonttext\" border=\"1\">" +
                        SYSConst.html_table_tr(
                                SYSConst.html_table_th(SYSTools.xx("misc.msg.Date")) +
                                        SYSConst.html_table_th(SYSTools.xx("misc.msg.Text")) +
                                        SYSConst.html_table_th(SYSTools.xx("misc.msg.user"))
                        ));

                for (NReport nReport : listReports) {
                    html.append(SYSConst.html_table_tr(
                            SYSConst.html_table_td(df.format(nReport.getPit()), null) +
                                    SYSConst.html_table_td(SYSConst.html_paragraph(nReport.getText()), null) +
                                    SYSConst.html_table_td(nReport.getNewBy().getFullname(), null)
                    ));
                }
                html.append("</table>");
            }
        }
        return html.toString();
    }


    public static String getComplaints(LocalDate from, Closure progress) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);


        StringBuilder html = new StringBuilder(1000);

        int p = -1;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        html.append(SYSConst.html_h2("opde.controlling.orga.complaints.nreports"));

        try {

            EntityManager em = OPDE.createEM();

            String jpql2 = " " +
                    " SELECT n FROM NReport n " +
                    " JOIN n.commontags ct " +
                    " WHERE n.pit > :from " +
                    " AND n.resident.adminonly <> 2 " +
                    " AND n.replacedBy IS NULL " +
                    " AND ct.type = :type " +
                    " ORDER BY n.resident.rid, n.pit DESC ";
            Query query2 = em.createQuery(jpql2);
            query2.setParameter("type", CommontagsTools.TYPE_SYS_COMPLAINT);
            query2.setParameter("from", from.toDateTimeAtStartOfDay().toDate());
            ArrayList<NReport> listReports = new ArrayList<NReport>(query2.getResultList());

            em.close();

            p = 0;

            if (listReports.isEmpty()) {
                html.append(SYSConst.html_div(SYSConst.html_bold(SYSTools.xx("misc.msg.nodata"))));
            } else {
                String table = "";
                table += SYSConst.html_table_tr(
                        SYSConst.html_table_th(SYSTools.xx("misc.msg.Date")) +
                                SYSConst.html_table_th(SYSTools.xx("misc.msg.resident")) +
                                SYSConst.html_table_th(SYSTools.xx("misc.msg.Text")) +
                                SYSConst.html_table_th(SYSTools.xx("misc.msg.user"))
                );

                for (NReport nReport : listReports) {
                    p++;
                    progress.execute(new Pair<Integer, Integer>(p, listReports.size()));

                    table += SYSConst.html_table_tr(
                            SYSConst.html_table_td(df.format(nReport.getPit()), null) +
                                    SYSConst.html_table_td(ResidentTools.getTextCompact(nReport.getResident()), null) +
                                    SYSConst.html_table_td(SYSConst.html_paragraph(nReport.getText()), null) +
                                    SYSConst.html_table_td(nReport.getNewBy().getFullname(), null)
                    );
                }
                html.append(SYSConst.html_table(table, "1"));
            }

        } catch (Exception exc) {
            OPDE.fatal(exc);
        }

        return html.toString();
    }

    public static ArrayList<NReport> getBVActivities(Resident resident, LocalDate from) {

        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;

        try {

            String jpql = " SELECT DISTINCT nr " +
                    " FROM NReport nr " +
                    " JOIN nr.commontags ct " +
                    " WHERE nr.resident = :resident " +
                    " AND nr.pit >= :from " +
                    " AND ct.type = :type " +
                    " AND nr.replacedBy IS NULL " +
                    " ORDER BY nr.pit ";

            Query query = em.createQuery(jpql);
            query.setParameter("resident", resident);
            query.setParameter("from", from.toDateTimeAtStartOfDay().toDate());
            query.setParameter("type", CommontagsTools.TYPE_SYS_BV);

            list = new ArrayList<NReport>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }


        return list;
    }

    public static String getTimes4SocialReports(LocalDate month, Closure progress) {
        StringBuilder html = new StringBuilder(1000);
        Format monthFormmatter = new SimpleDateFormat("MMMM yyyy");

        DateTime from = SYSCalendar.bom(month).toDateTimeAtStartOfDay();
        DateTime to = SYSCalendar.eod(SYSCalendar.eom(month));

        int p = -1;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        EntityManager em = OPDE.createEM();

        String jpql1 = " " +
                " SELECT DISTINCT n FROM NReport n " +
                " JOIN n.commontags ct " +
                " WHERE n.pit >= :from AND n.pit <= :to AND n.replacedBy IS NULL AND n.resident.adminonly <> 2 AND (ct.type = :type1 OR ct.type = :type2) ORDER BY n.resident.rid ";
        Query query1 = em.createQuery(jpql1);
        query1.setParameter("type1", CommontagsTools.TYPE_SYS_SOCIAL);
        query1.setParameter("type2", CommontagsTools.TYPE_SYS_SOCIAL2);
        query1.setParameter("from", from.toDate());
        query1.setParameter("to", to.toDate());
        ArrayList<NReport> listNR = new ArrayList<NReport>(query1.getResultList());

        em.close();

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


            for (Commontags ctag : nr.getCommontags()) {
                if (ctag.getType() == CommontagsTools.TYPE_SYS_SOCIAL) {
                    socialtime += nr.getMinutes();
                }
                if (ctag.getType() == CommontagsTools.TYPE_SYS_SOCIAL2) {
                    peatime += nr.getMinutes();
                }
            }

            statmap.put(nr.getResident(), new Pair<Integer, Integer>(socialtime, peatime));
        }

        html.append(SYSConst.html_h1(SYSTools.xx("opde.controlling.nursing.social")));
        html.append(SYSConst.html_h2(monthFormmatter.format(month.toDate())));


        StringBuilder table = new StringBuilder(1000);
        table.append(SYSConst.html_table_tr(
                SYSConst.html_table_th("misc.msg.resident") +
                        SYSConst.html_table_th(SYSTools.xx("misc.msg.Effort") + " (" + SYSTools.xx("misc.msg.Minutes") + ")") +
                        SYSConst.html_table_th(SYSTools.xx("misc.msg.Effort") + " (" + SYSTools.xx("misc.msg.Hours") + ")") +
                        SYSConst.html_table_th(SYSTools.xx("opde.controlling.nursing.social.averageHoursPerDay")) +
                        SYSConst.html_table_th("PEA " + SYSTools.xx("misc.msg.Effort") + " (" + SYSTools.xx("misc.msg.Minutes") + ")") +
                        SYSConst.html_table_th("PEA " + SYSTools.xx("misc.msg.Effort") + " (" + SYSTools.xx("misc.msg.Hours") + ")") +
                        SYSConst.html_table_th("PEA " + SYSTools.xx("opde.controlling.nursing.social.averageHoursPerDay"))
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
                            SYSConst.html_table_td(SYSTools.formatBigDecimal(socialtime), socialtime.equals(BigDecimal.ZERO)) +
                            SYSConst.html_table_td(SYSTools.formatBigDecimal(socialtime.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP)), socialtime.equals(BigDecimal.ZERO)) +
                            SYSConst.html_table_td(SYSTools.formatBigDecimal(socialtime.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP).divide(daysinmonth, 2, BigDecimal.ROUND_HALF_UP)), socialtime.equals(BigDecimal.ZERO)) +
                            SYSConst.html_table_td(SYSTools.formatBigDecimal(peatime), peatime.equals(BigDecimal.ZERO)) +
                            SYSConst.html_table_td(SYSTools.formatBigDecimal(peatime.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP)), peatime.equals(BigDecimal.ZERO)) +
                            SYSConst.html_table_td(SYSTools.formatBigDecimal(peatime.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP).divide(daysinmonth, 2, BigDecimal.ROUND_HALF_UP)), peatime.equals(BigDecimal.ZERO))
                    , highlight));
        }

        html.append(SYSConst.html_table(table.toString(), "1"));
        html.append(SYSConst.html_paragraph("opde.controlling.nursing.social.peaexplain"));

        return html.toString();
    }

    public static ArrayList<NReport> getNReports(Resident resident, LocalDate ldfrom, LocalDate ldto) {
        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;
        DateTime from = ldfrom.toDateTimeAtStartOfDay();
        DateTime to = SYSCalendar.eod(ldto);

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

    public static ArrayList<NReport> getNReports4Month(Resident resident, LocalDate month) {
        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;
        DateTime from = SYSCalendar.bom(month).toDateTimeAtStartOfDay();
        DateTime to = SYSCalendar.eod(SYSCalendar.eom(month));

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

    public static ArrayList<NReport> getNReports4Week(Resident resident, LocalDate week) {
        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;
        DateTime from = SYSCalendar.bow(week).toDateTimeAtStartOfDay();
        DateTime to = SYSCalendar.eod(SYSCalendar.eow(week));

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

    public static ArrayList<NReport> getNReports4Day(Resident resident, LocalDate day) {
        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;

//        OPDE.debug(day.toString());

        try {

            String jpql = " SELECT nr " +
                    " FROM NReport nr " +
                    " WHERE nr.resident = :resident " +
                    " AND nr.pit >= :from AND nr.pit <= :to " +
                    " ORDER BY nr.pit DESC ";

            Query query = em.createQuery(jpql);

            query.setParameter("resident", resident);
            query.setParameter("from", day.toDateTimeAtStartOfDay().toDate());
            query.setParameter("to", SYSCalendar.eod(day).toDate());

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
                    " ORDER BY nr.pit DESC ";

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

    public static ArrayList getNReports4Handover(Homes home, String searchphrase, int year) {
        EntityManager em = OPDE.createEM();
        ArrayList list = new ArrayList();
        LocalDate from = new LocalDate(year, 1, 1);
        LocalDate to = new LocalDate(year, 12, 31);

        try {

            String jpql = " SELECT DISTINCT nr " +
                    " FROM NReport nr " +
                    " JOIN nr.commontags ct " +
                    " WHERE nr.pit >= :from AND nr.pit <= :to AND (ct.type = :handover OR ct.type = :emergency) AND nr.text LIKE :search" +
                    " AND nr.resident.station.home = :home " +
                    " AND nr.replacedBy IS NULL AND nr.editedBy IS NULL ";

            Query query = em.createQuery(jpql);

            query.setParameter("from", from.toDateTimeAtStartOfDay().toDate());
            query.setParameter("to", SYSCalendar.eod(to).toDate());
            query.setParameter("home", home);
            query.setParameter("handover", CommontagsTools.TYPE_SYS_HANDOVER);
            query.setParameter("emergency", CommontagsTools.TYPE_SYS_EMERGENCY);
            query.setParameter("search", EntityTools.getMySQLsearchPattern(searchphrase));

            list.addAll(query.getResultList());

            String jpql2 = " SELECT ho " +
                    " FROM Handovers ho " +
                    " WHERE ho.pit >= :from AND ho.pit <= :to AND ho.text LIKE :search " +
                    " AND ho.home = :home ";

            Query query2 = em.createQuery(jpql2);

            query2.setParameter("from", from.toDate());
            query2.setParameter("to", to.toDate());
            query2.setParameter("home", home);
            query2.setParameter("search", EntityTools.getMySQLsearchPattern(searchphrase));

            list.addAll(query2.getResultList());


            Collections.sort(list, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    return new Long(((QProcessElement) o1).getPITInMillis()).compareTo(new Long(((QProcessElement) o2).getPITInMillis())) * -1;
                }
            });


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
    public static ArrayList<NReport> getNReports4Handover(LocalDate day, Homes home) {
        return getNReports4Handover(day, day, home);
    }

    public static ArrayList<NReport> getNReports4Handover(LocalDate from, LocalDate to, Homes home) {

        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;

        try {

            String jpql = " SELECT DISTINCT nr " +
                    " FROM NReport nr " +
                    " JOIN nr.commontags ct " +
                    " WHERE nr.pit >= :from AND nr.pit <= :to AND (ct.type = :handover OR ct.type = :emergency) " +
                    " AND nr.resident.station.home = :home " +
                    " AND nr.replacedBy IS NULL AND nr.editedBy IS NULL ";

            Query query = em.createQuery(jpql);

            query.setParameter("from", from.toDateTimeAtStartOfDay().toDate());
            query.setParameter("to", SYSCalendar.eod(to).toDate());
            query.setParameter("home", home);
            query.setParameter("handover", CommontagsTools.TYPE_SYS_HANDOVER);
            query.setParameter("emergency", CommontagsTools.TYPE_SYS_EMERGENCY);

            list = new ArrayList<NReport>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    public static ArrayList<NReport> getNReports4Tags(Resident resident, Commontags tag) {


        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;

        try {

            String jpql = " SELECT nr " +
                    " FROM NReport nr " +
                    " JOIN nr.commontags t " +
                    " WHERE nr.resident = :resident " +
                    " AND t = :tag " +
//                    " AND nr.pit >= :from AND nr.pit <= :to  " +
                    " ORDER BY nr.pit DESC ";

            Query query = em.createQuery(jpql);

            query.setParameter("resident", resident);
            query.setParameter("tag", tag);

            list = new ArrayList<NReport>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    /**
     * sets all necessary changes to <i>DELETE</i> a report. Which is in fact never really deleted.
     * @param report
     * @param deletedBy
     * @return
     */
    public static NReport delete(NReport report, Users deletedBy) {
        report.setDeletedBy(deletedBy);
        report.setDelPIT(new Date());
        report.getAttachedFilesConnections().clear();
        report.getAttachedQProcessConnections().clear();
        return report;
    }

    /**
        * sets all necessary changes to <i>DELETE</i> a report. Which is in fact never really deleted.
        * @param report
        * @param deletedBy
        * @return
        */
       public static NReport replace(NReport report, Users deletedBy) {
           report.setDeletedBy(deletedBy);
           report.setDelPIT(new Date());
           report.getAttachedFilesConnections().clear();
           report.getAttachedQProcessConnections().clear();
           return report;
       }

    public static ArrayList<NReport> getNReports4Tags(Commontags tag, LocalDate start, LocalDate end) {

        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;

        try {

            String jpql = " SELECT nr " +
                    " FROM NReport nr " +
                    " JOIN nr.commontags t " +
                    " WHERE nr.resident.adminonly <> 2 " +
                    " AND t = :tag " +
                    " AND nr.pit >= :from AND nr.pit <= :to  " +
                    " ORDER BY nr.pit DESC ";

            Query query = em.createQuery(jpql);

            query.setParameter("tag", tag);
            query.setParameter("from", start.toDateTimeAtStartOfDay().toDate());
            query.setParameter("to", SYSCalendar.eod(end).toDate());

            list = new ArrayList<NReport>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

}
