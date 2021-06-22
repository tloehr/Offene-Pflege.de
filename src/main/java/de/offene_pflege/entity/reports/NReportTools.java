/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package de.offene_pflege.entity.reports;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.building.Homes;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.entity.process.QProcessElement;
import de.offene_pflege.entity.system.Commontags;
import de.offene_pflege.entity.system.CommontagsTools;
import de.offene_pflege.entity.system.OPUsers;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.*;
import org.apache.commons.collections.Closure;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.MutableInterval;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author tloehr
 */
public class NReportTools {

    public static int IGNORED_AMOUNT_SECONDS_TILL_THE_CLOCK_TURNS_UP = 120;
    private static Logger logger = Logger.getLogger(NReport.class);

    //https://github.com/tloehr/Offene-Pflege.de/issues/66
    public static boolean isChangeable(NReport nReport) {
        return (nReport.isMine() && !nReport.isObsolete() && ResidentTools.isActive(nReport.getResident()) && nReport.getUsersAcknowledged().isEmpty());
    }

    /**
     * retrieves the PITs of the first and the last entries in the NReports and Handovers table.
     * The values are combined, so that the maximum span is calculated.
     *
     * https://github.com/tloehr/Offene-Pflege.de/issues/83
     * https://github.com/tloehr/Offene-Pflege.de/issues/88
     *
     * @return
     */
    public static MutableInterval getMinMax() {
        MutableInterval result = null;


        EntityManager em = OPDE.createEM();
        Query queryMin1 = em.createQuery("SELECT nr FROM NReport nr ORDER BY nr.pit ASC");
        queryMin1.setMaxResults(1);
        Query queryMax1 = em.createQuery("SELECT nr FROM NReport nr ORDER BY nr.pit DESC");
        queryMax1.setMaxResults(1);
        Query queryMin2 = em.createQuery("SELECT nr FROM Handovers nr ORDER BY nr.pit ASC");
        queryMin2.setMaxResults(1);
        Query queryMax2 = em.createQuery("SELECT nr FROM Handovers nr ORDER BY nr.pit DESC");
        queryMax2.setMaxResults(1);

        DateTime dmin1 = null, dmax1 = null, dmin2 = null, dmax2 = null;
        
        try {
            NReport min1 = (NReport) queryMin1.getSingleResult();
            NReport max1 = (NReport) queryMax1.getSingleResult();
            dmin1 = min1 == null ? new DateTime() : new DateTime(min1.getPit());
            dmax1 = max1 == null ? new DateTime() : new DateTime(max1.getPit());
        } catch (NoResultException nre) {
            dmin1 = null;
            dmax1 = null;
        } catch (Exception e) {
            OPDE.fatal(e);
        }

        try {
            Handovers min2 = (Handovers) queryMin2.getSingleResult();
            Handovers max2 = (Handovers) queryMax2.getSingleResult();
            dmin2 = min2 == null ? new DateTime() : new DateTime(min2.getPit());
            dmax2 = max2 == null ? new DateTime() : new DateTime(max2.getPit());
        } catch (NoResultException nre) {
            dmin2 = null;
            dmax2 = null;
        } catch (Exception e) {
            OPDE.fatal(e);
        }

        DateTime min = SYSCalendar.min(dmin1, dmin2);
        DateTime max = SYSCalendar.max(dmax1, dmax2);

        if (min != null && max != null){
            result = new MutableInterval(min, max);
        } else {
            result = null;
        }

        em.close();
        return result;
    }




    public static MutableInterval getNativeMinMax2(Resident resident) {
        long time = System.currentTimeMillis();
        MutableInterval result = null;

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createNativeQuery(" SELECT MIN(PIT) FROM nreports WHERE BWKennung = ?");
        queryMin.setParameter(1, resident.getId());

        Query queryMax = em.createNativeQuery(" SELECT MAX(PIT) FROM nreports WHERE BWKennung = ?");
        queryMax.setParameter(1, resident.getId());

        try {
            // umgeschrieben weg. 1.15.2 scheinbar hat sich eine library geändert und wirft nun LocalDateTime statt Date aus
            Optional<LocalDateTime> optMin = Optional.of((LocalDateTime) queryMin.getSingleResult());
            Optional<LocalDateTime> optMax = Optional.of((LocalDateTime) queryMax.getSingleResult());

            result = null;
            if (optMin.isPresent()){
                org.joda.time.LocalDateTime min = JavaTimeConverter.toJodaLocalDateTime(optMin.get());
                org.joda.time.LocalDateTime max = JavaTimeConverter.toJodaLocalDateTime(optMax.get());
                result = new MutableInterval(min.toDateTime(), max.toDateTime());
            }

        } catch (Exception e) {
            OPDE.fatal(e);
        }
        
        em.close();
        logger.debug((System.currentTimeMillis() - time) + " ms for native minmax2");
        return result;
    }


    /**
     * retrieves the PITs of the first and the last entry in the NReports table.
     *
     * @param resident
     * @return
     */
    @Deprecated
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
            NReport min = (NReport) queryMin.getSingleResult();
            NReport max = (NReport) queryMax.getSingleResult();
            if (min == null) {
                result = null;
            } else {
                result = new MutableInterval(new DateTime(min.getPit()), new DateTime(max.getPit()));
            }

        } catch (Exception e) {
            OPDE.fatal(e);
        }


        em.close();
        logger.debug((System.currentTimeMillis() - time) + " ms for minmax");
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
        logger.debug(nReport.getPbid());
//        result += getDateAndUser(nReport, false, true);

//        result += SYSTools.catchNull(getTagsAsHTML(nReport), " [", "]") + " ";

        DateFormat df = DateFormat.getDateTimeInstance();

        if (nReport.isDeleted()) {
            result += "<br/>" + SYSTools.xx("misc.msg.thisentryhasbeendeleted") + " <br/>" + SYSTools.xx("misc.msg.atchrono") + " " + df.format(nReport.getDelPIT()) + " <br/>" + SYSTools.xx("misc.msg.Bywhom") + " " + nReport.getDeletedBy().getFullname() + "<br/>";
        }
        if (nReport.isReplacement() && !nReport.isReplaced()) {
            result += "<br/>" + SYSTools.xx("misc.msg.thisEntryIsAReplacement") + " <br/>" + SYSTools.xx("misc.msg.atchrono") + " " + df.format(nReport.getReplacementFor().getNewPIT()) + " <br/>" + "<br/>" + SYSTools.xx("misc.msg.originalentry") + ": " + nReport.getReplacementFor().getPbid() + "<br/>";
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

        result += "</div>";
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

            if (resident.getPn1() == null) {
                html.append(SYSConst.html_div(SYSConst.html_bold(SYSTools.xx("opde.controlling.orga.bvactivities.nobv"))));
            } else {
                html.append(SYSConst.html_div(SYSConst.html_bold(SYSTools.xx("misc.msg.bv")) + ": " + resident.getPn1().getFullname()));
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
                    " ORDER BY n.resident.id, n.pit DESC ";
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


            Collections.sort(list, (o1, o2) -> new Long(((QProcessElement) o1).getPITInMillis()).compareTo(new Long(((QProcessElement) o2).getPITInMillis())) * -1);


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
     *
     * @param report
     * @param deletedBy
     * @return
     */
    public static NReport delete(NReport report, OPUsers deletedBy) {
        report.setDeletedBy(deletedBy);
        report.setDelPIT(new Date());
        report.getAttachedFilesConnections().clear();
        report.getAttachedQProcessConnections().clear();
        return report;
    }

    /**
     * sets all necessary changes to <i>DELETE</i> a report. Which is in fact never really deleted.
     *
     * @param report
     * @param deletedBy
     * @return
     */
    public static NReport replace(NReport report, OPUsers deletedBy) {
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

            long begin = System.currentTimeMillis();


//            String jpql = " SELECT nr " +
//                    " FROM NReport nr " +
//                    " JOIN nr.commontags t " +
//                    " WHERE nr.pit >= :from AND nr.pit <= :to  " +
//                    " ORDER BY nr.pit DESC ";
//                    " WHERE nr.resident.adminonly <> 2 " +
//                    " AND t = :tag " +


            // native sql. the generated one is awfully slow
            String nativeSQL = "SELECT nr.* FROM nreports nr " +
                    " INNER JOIN nreports2tags tg ON nr.`PBID` = tg.`PBID` " +
                    " WHERE nr.`PIT` >= ? AND nr.`PIT` <= ? " +
                    " AND tg.ctagid = ?;";

            Query query = em.createNativeQuery(nativeSQL, NReport.class);
            query.setParameter(1, start.toDateTimeAtStartOfDay().toDate());
            query.setParameter(2, SYSCalendar.eod(end).toDate());
            query.setParameter(3, tag.getId());

            list = new ArrayList<>(query.getResultList());
            SYSTools.showTimeDifference(begin);

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

}
