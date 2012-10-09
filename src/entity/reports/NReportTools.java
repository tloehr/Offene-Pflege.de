/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package entity.reports;

import entity.EntityTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.nursingprocess.DFNTools;
import op.OPDE;
import op.tools.Pair;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.awt.color.ColorSpace;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author tloehr
 */
public class NReportTools {

    public static NReport getFirstReport(Resident resident) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Pflegeberichte.findAllByBewohner");
        query.setParameter("bewohner", resident);
        query.setFirstResult(0);
        query.setMaxResults(1);
        NReport p = (NReport) query.getSingleResult();
        em.close();
        return p;
    }


    public static boolean isChangeable(NReport nReport) {
//        OPDE.debug(nReport.getPbid());
        return !nReport.isObsolete() && nReport.getUsersAcknowledged().isEmpty() && (OPDE.isAdmin() || nReport.getUser().equals(OPDE.getLogin().getUser()));
    }

    /**
     * retrieves the PITs of the first and the last entry in the ResValue table.
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


    public static int getNumReports(Resident resident) {
        int num = 0;

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createQuery("SELECT COUNT(nr) FROM NReport nr WHERE nr.resident = :resident ");
        queryMin.setParameter("resident", resident);

        try {
            num = (Integer) queryMin.getSingleResult();
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

    public static String getReportsAsHTML(List<NReport> berichte, boolean specialsOnly, boolean withlongheader, String subtitle, String highlight) {
        String html = "";
        boolean ihavesomethingtoshow = false;

        if (!berichte.isEmpty()) {
            html += "<h2 id=\"fonth2\" >" + OPDE.lang.getString("nursingrecords.reports") + (withlongheader ? " " + OPDE.lang.getString("misc.msg.for") + " " + ResidentTools.getLabelText(berichte.get(0).getResident()) : "") + "</h2>\n";
            html += SYSTools.catchNull(subtitle).isEmpty() ? "" : "<h3 id=\"fonth3\" >" + subtitle + "</h3>\n";
            html += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>"
                    + "<th>Info</th><th>Text</th>\n</tr>";
            for (NReport bericht : berichte) {
                if (!specialsOnly || bericht.isSpecial()) {
                    ihavesomethingtoshow = true;
                    html += "<tr>";
                    html += "<td valign=\"top\">" + getDatumUndUser(bericht, false, false) + "</td>";
                    html += "<td valign=\"top\">" + getAsHTML(bericht, highlight) + "</td>";
                    html += "</tr>\n";
                }
            }
            html += "</table>\n";
        }

        if (berichte.isEmpty() || !ihavesomethingtoshow) {
            html = "";
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

    public static String getDatumUndUser(NReport bericht, boolean showIDs, boolean showMinutes) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        result = sdf.format(bericht.getPit()) + "; " + bericht.getUser().getFullname();
        if (showMinutes && !bericht.isDeleted() && !bericht.isReplaced()) {
            result += "<br/>" + OPDE.lang.getString("misc.msg.Effort") + ": " + bericht.getMinutes() + " " + OPDE.lang.getString("misc.msg.Minute(s)");
        }
        if (showIDs) {
            result += "<br/><i>(" + bericht.getPbid() + ")</i>";
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

        result += getDatumUndUser(nReport, false, true);

        result += SYSTools.catchNull(getTagsAsHTML(nReport), " [", "]") + " ";

        DateFormat df = DateFormat.getDateTimeInstance();

        if (nReport.isDeleted()) {
            result += "<br/><i>" + OPDE.lang.getString("misc.msg.thisentryhasbeendeleted") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(nReport.getEditDate()) + " <br/>" + OPDE.lang.getString("misc.msg.Bywhom") + " " + nReport.getEditedBy().getFullname() + "</i><br/>";
        }
        if (nReport.isReplacement() && !nReport.isReplaced()) {
            result += "<br/><i>" + OPDE.lang.getString("misc.msg.thisEntryIsAReplacement") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(nReport.getReplacementFor().getEditDate()) + " <br/>" + "<br/>" + OPDE.lang.getString("misc.msg.originalentry") + ": " + nReport.getReplacementFor().getPbid() + "</i><br/>";
        }
        if (nReport.isReplaced()) {
            result += "<br/><i>" + OPDE.lang.getString("misc.msg.thisentryhasbeenedited") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(nReport.getEditDate()) + " <br/>" + OPDE.lang.getString("misc.msg.Bywhom") + " " + nReport.getEditedBy().getFullname();
            result += "<br/>" + OPDE.lang.getString("misc.msg.replaceentry") + ": " + nReport.getReplacedBy().getPbid() + "</i><br/>";
        }
//        if (!nReport.getAttachedFilesConnections().isEmpty()) {
//            result += "<font color=\"green\">&#9679;</font>";
//        }
//        if (!nReport.getAttachedProcessConnections().isEmpty()) {
//            result += "<font color=\"red\">&#9679;</font>";
//        }

        String tmp = SYSTools.replace(nReport.getText(), "\n", "<br/>", false);
        if (!SYSTools.catchNull(highlight).isEmpty()) {
            tmp = SYSTools.replace(tmp, highlight, "<font style=\"BACKGROUND-COLOR: yellow\">" + highlight + "</font>", true);
        }

        result += "<p>[" + nReport.getPbid() + "] " + tmp + "<p/>";

        result += "<div/>";
        return result;
    }

    public static String getInfoAsHTML(NReport nReport) {
        String result = "<div id=\"fonttext\">";

        DateFormat df = DateFormat.getDateTimeInstance();
        if (nReport.isDeleted()) {
            result += "<br/><i>" + OPDE.lang.getString("misc.msg.thisentryhasbeendeleted") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(nReport.getEditDate()) + " <br/>" + OPDE.lang.getString("misc.msg.Bywhom") + " " + nReport.getEditedBy().getFullname() + "</i><br/>";
        }
        if (nReport.isReplacement() && !nReport.isReplaced()) {
            result += "<br/><i>" + OPDE.lang.getString("misc.msg.thisEntryIsAReplacement") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(nReport.getReplacementFor().getEditDate()) + " <br/>" + "<br/>" + OPDE.lang.getString("misc.msg.originalentry") + ": " + nReport.getReplacementFor().getPbid() + "</i><br/>";
        }
        if (nReport.isReplaced()) {
            result += "<br/><i>" + OPDE.lang.getString("misc.msg.thisentryhasbeenedited") + " <br/>" + OPDE.lang.getString("misc.msg.atchrono") + " " + df.format(nReport.getEditDate()) + " <br/>" + OPDE.lang.getString("misc.msg.Bywhom") + " " + nReport.getEditedBy().getFullname();
            result += "<br/>" + OPDE.lang.getString("misc.msg.replaceentry") + ": " + nReport.getReplacedBy().getPbid() + "</i><br/>";
        }
        return result + "</div>";
    }


    public static String getBewohnerName(NReport bericht) {
        String result = "";
        result = bericht.getResident().getNachname() + ", " + bericht.getResident().getVorname();
        return "<font " + getHTMLColor(bericht) + SYSConst.html_arial14 + ">" + result + "</font>";
    }

    /**
     * @param em
     * @param headertiefe
     * @param bvwochen
     * @return
     */
    public static String getBVBerichte(EntityManager em, int headertiefe, int bvwochen) {
        StringBuilder html = new StringBuilder(1000);
//        String jpql = "" +
//                " SELECT  b, pb " +
//                " FROM Bewohner b " +
//                " LEFT JOIN b.pflegberichte pb " +
//                " LEFT JOIN pb.tags pbt " +
//                " WHERE pb.pit >= :datum AND pbt.kurzbezeichnung = 'BV' AND b.station IS NOT NULL AND b.adminonly <> 2 " +
//                " ORDER BY b.bWKennung, pb.pit ";


//        String sql = " SELECT b.*, a.PBID " +
//                " FROM Bewohner b " +
//                " LEFT OUTER JOIN ( " +
//                "    SELECT pb.* FROM NReport pb " +
//                "    LEFT OUTER JOIN PB2TAGS pbt ON pbt.PBID = pb.PBID " +
//                "    LEFT OUTER JOIN PBericht_TAGS pbtags ON pbt.PBTAGID = pbtags.PBTAGID " +
//                "    WHERE pb.PIT > ? AND pbtags.Kurzbezeichnung = 'BV'" +
//                " ) a ON a.BWKennung = b.BWKennung " +
//                " WHERE b.StatID IS NOT NULL AND b.adminonly <> 2 " +
//                " ORDER BY b.BWKennung, a.pit ";
        Query query = em.createNamedQuery("Pflegeberichte.findBVAktivitaet");
        query.setParameter(1, SYSCalendar.addField(new Date(SYSCalendar.startOfDay()), bvwochen * -1, GregorianCalendar.WEEK_OF_MONTH));

//        Query query2 = em.createQuery("SELECT b FROM Bewohner b LEFT JOIN b.pflegberichte pb WHERE pb IS NULL ");
//        query.setParameter("datum", SYSCalendar.addField(new Date(SYSCalendar.startOfDay()), bvwochen * -1, GregorianCalendar.WEEK_OF_MONTH));


        List<Object[]> list = query.getResultList();
        DateFormat df = DateFormat.getDateInstance();
        html.append("<h" + headertiefe + ">");
        html.append("Berichte der BV-Tätigkeiten");
        html.append("</h" + headertiefe + ">");
        html.append("<table border=\"1\"><tr>" +
                "<th>BewohnerIn</th><th>Datum</th><th>Text</th><th>UKennung</th><th>BV</th></tr>");

        for (Object[] paar : list) {
            Resident bewohner = (Resident) paar[0];
            BigInteger pbid = (BigInteger) paar[1];

            // Bei Bedarf den Pflegebericht "einsammeln"
            NReport bericht = pbid == null ? null : em.find(NReport.class, pbid.longValue());

            html.append("<tr>");

            html.append("<td>" + ResidentTools.getBWLabel1(bewohner) + "</td>");
            if (bericht == null) {
                html.append("<td align=\"center\">--</td>");
                html.append("<td><b>Keine BV Aktivitäten gefunden.</b></td>");
                html.append("<td align=\"center\">--</td>");
            } else {
                html.append("<td>" + df.format(bericht.getPit()) + "</td>");
                html.append("<td>" + bericht.getText() + "</td>");
                html.append("<td>" + bericht.getUser().getUID() + "</td>");
            }
            if (bewohner.getBv1() == null) {
                html.append("<td><b>kein BV zugeordnet</b></td>");
            } else {
                html.append("<td>" + bewohner.getBv1().getUID() + "</td>");
            }
            html.append("</tr>");
        }
        html.append("</table>");

//        if (rs.first()) {

//        }


        return html.toString();
    }


    /**
     * Durchsucht die NReport nach einem oder mehreren Suchbegriffen
     */
    public static String getBerichteASHTML(EntityManager em, String suche, NReportTAGS tag, int headertiefe, int monate) {
        StringBuilder html = new StringBuilder(1000);
        String where = "";
        String htmlbeschreibung = "";
        String jpql = "";
        String order = " ORDER BY p.bewohner.bWKennung, p.pit ";

        if (suche.trim().isEmpty() && tag == null) {
            html.append("<h" + headertiefe + ">");
            html.append("Berichtsuche nicht möglich.");
            html.append("</h" + headertiefe + ">");
        } else {
            jpql = "SELECT p FROM NReport p JOIN p.tags t WHERE p.pit >= :date AND t = :tag";

            if (!suche.trim().isEmpty()) {
                where = " AND p.text like :search ";
                htmlbeschreibung += "Suchbegriff: '" + suche + "'<br/>";
            }

            if (tag != null) {
                // Suchausdruck vorbereiten.
                where += " AND t = :tag ";
                htmlbeschreibung += "Markierung: '" + tag.getBezeichnung() + "'<br/>";
            }

//            String sql = "" +
//                    " SELECT b.nachname, b.vorname, b.geschlecht, b.bwkennung, tb.Text, tb.PIT, tb.UKennung " +
//                    " FROM Tagesberichte tb " +
//                    " INNER JOIN Bewohner b ON tb.BWKennung = b.BWKennung " +
//                    " WHERE Date(tb.PIT) >= DATE_ADD(now(), INTERVAL ? MONTH) " +
//                    " AND " + where +
//                    " b.AdminOnly <> 2 " +
//                    " ORDER BY b.BWKennung, tb.PIT ";

            try {
                Query query = em.createQuery(jpql + where + order);
                if (!suche.trim().isEmpty()) {
                    query.setParameter("search", "%" + suche + "%");
                }
                if (tag != null) {
                    query.setParameter("tag", tag);
                }

                query.setParameter("date", SYSCalendar.addField(new Date(), monate * -1, GregorianCalendar.MONTH));
                List<NReport> list = query.getResultList();
                html.append("<h" + headertiefe + ">");
                html.append("Suchergebnisse in den Berichten der letzten " + monate + " Monate");
                html.append("</h" + headertiefe + ">");
                html.append(htmlbeschreibung);

                if (!list.isEmpty()) {
                    DateFormat df = DateFormat.getDateTimeInstance();
                    html.append("<table border=\"1\"><tr>" +
                            "<th>BewohnerIn</th><th>Datum/Uhrzeit</th><th>Text</th><th>UKennung</th></tr>");
                    for (NReport bericht : list) {
                        html.append("<tr>");

                        html.append("<td>" + ResidentTools.getBWLabel1(bericht.getResident()) + "</td>");
                        html.append("<td>" + df.format(bericht.getPit()) + "</td>");
                        html.append("<td>" + bericht.getText() + "</td>");
                        html.append("<td>" + bericht.getUser().getUID() + "</td>");
                        html.append("</tr>");
                    }
                    html.append("</table>");
                } else {
                    html.append("<br/>keine Treffer gefunden...");
                }
            } catch (Exception e) {
                OPDE.fatal(e);
            }
        }

        return html.toString();
    }

    public static String getSozialZeiten(EntityManager em, int headertiefe, Date monat) {
        StringBuilder html = new StringBuilder(1000);
        SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy");

        try {

            Date von = new Date(SYSCalendar.bom(monat).getTime());
            Date bis = new Date(SYSCalendar.eom(monat).getTime());
//            int daysinmonth = SYSCalendar.eom(SYSCalendar.toGC(monat));

            Query query = em.createNamedQuery("Pflegeberichte.findSozialZeiten");
            query.setParameter(1, von);
            query.setParameter(2, bis);
            query.setParameter(3, von);
            query.setParameter(4, bis);

            List list = query.getResultList();
            BigDecimal daysinmonth = new BigDecimal(SYSCalendar.eom(SYSCalendar.toGC(monat)));

            html.append("<h" + headertiefe + ">");
            html.append("Zeiten des Sozialen Dienstes je BewohnerIn");
            html.append("</h" + headertiefe + ">");

            headertiefe++;

            html.append("<h" + headertiefe + ">");
            html.append("Zeitraum: " + df.format(monat));
            html.append("</h" + headertiefe + ">");

            html.append("<table border=\"1\"><tr>" +
                    "<th>BewohnerIn</th><th>Dauer (Minuten)</th><th>Dauer (Stunden)</th><th>Stundenschnitt pro Tag</th><th>PEA (Minuten)</th><th>PEA (Stunden)</th><th>Stundenschnitt pro Tag</th></tr>");
            for (Object object : list) {
                Resident bewohner = (Resident) ((Object[]) object)[0];
                BigDecimal sdauer = (BigDecimal) ((Object[]) object)[1];
                BigDecimal peadauer = (BigDecimal) ((Object[]) object)[2];


                html.append("<tr>");

                html.append("<td>" + ResidentTools.getBWLabel1(bewohner) + "</td>");
                html.append("<td>" + sdauer + "</td>");
                html.append("<td>" + sdauer.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP) + "</td>");
                html.append("<td>" + sdauer.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP).divide(daysinmonth, 2, BigDecimal.ROUND_HALF_UP) + "</td>");
                html.append("<td>" + peadauer + "</td>");
                html.append("<td>" + peadauer.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP) + "</td>");
                html.append("<td>" + peadauer.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP).divide(daysinmonth, 2, BigDecimal.ROUND_HALF_UP) + "</td>");
                html.append("</tr>");
            }

            html.append("</table>");

            html.append("<p><b>PEA:</b> Personen mit erheblich eingeschränkter Alltagskompetenz (gemäß §87b SGB XI)." +
                    " Der hier errechnete Wert ist der <b>Anteil</b> für die PEA Leistungen, die in den allgemeinen Sozialzeiten" +
                    " mit enthalten sind.</p>");

        } catch (Exception e) {
            OPDE.fatal(e);
        }

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
                    " ORDER BY nr.pit ASC ";

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
                    " ORDER BY nr.pit ASC";

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

    public static ArrayList<NReport> getNReports4Tags(Resident resident, DateMidnight day, NReportTAGS tag) {
        DateTime from = day.toDateTime();
        DateTime to = day.plusDays(1).toDateTime().minusSeconds(1);
        EntityManager em = OPDE.createEM();
        ArrayList<NReport> list = null;

        try {

            String jpql = " SELECT nr " +
                    " FROM NReport nr " +
                    " JOIN nr.tags t " +
                    " WHERE nr.resident = :resident " +
                    " AND nr.pit >= :from AND nr.pit <= :to AND t = :tag " +
                    " ORDER BY nr.pit ASC ";

            Query query = em.createQuery(jpql);

            query.setParameter("resident", resident);
            query.setParameter("tag", tag);
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
