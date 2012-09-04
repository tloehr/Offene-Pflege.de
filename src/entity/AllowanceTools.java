/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import entity.info.Resident;
import entity.info.ResidentTools;
import op.OPDE;
import op.tools.HTMLTools;
import op.tools.Pair;
import op.tools.SYSCalendar;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author tloehr
 */
public class AllowanceTools {

    // TGID, BelegDatum, Belegtext, Betrag, _creator, _editor, _cdate, _edate, _cancel
    public static String getEinzelnAsHTML(List<Allowance> listTG, BigDecimal vortrag, Resident bewohner) {

        SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy");
        DecimalFormat currency = new DecimalFormat("######.00");
        BigDecimal saldo = vortrag;

        int BARBEGTRAG_PAGEBREAK_AFTER_ELEMENT_NO = Integer.parseInt(OPDE.getProps().getProperty("barbetrag_pagebreak_after_element_no"));

        int elementNumber = 1;
        boolean pagebreak = false;

        String header = "Barbetragsübersicht für " + ResidentTools.getLabelText(bewohner);

        String html = "<html>\n"
                + "<head>\n"
                + "<title>" + header + "</title>\n"
                + OPDE.getCSS()
//                + "<style type=\"text/css\" media=\"all\">\n"
//                + "body { padding:10px; }\n"
//                + "#fontsmall { font-size:10px; font-weight:bold; font-family:Arial,sans-serif;}\n"
//                + "#fonth1 { font-size:24px; font-family:Arial,sans-serif;}\n"
//                + "#fonth2 { font-size:16px; font-weight:bold; font-family:Arial,sans-serif;}\n"
//                + "#fonttext { font-size:12px; font-family:Arial,sans-serif;}\n"
//                + "#fonttextgrau { font-size:12px; background-color:#CCCCCC; font-family:Arial,sans-serif;}\n"
//                + "</style>\n"
                + HTMLTools.JSCRIPT_PRINT
                + "</head>\n"
                + "<body>\n";


        html += "<h1 align=\"center\" id=\"fonth1\">" + header + "</h1>";

        int monat = -1;

        for (Allowance tg : listTG) {

            GregorianCalendar belegDatum = SYSCalendar.toGC(tg.getBelegDatum());
            boolean monatsWechsel = monat != belegDatum.get(GregorianCalendar.MONTH);


            if (pagebreak || monatsWechsel) {
                // Falls zufällig ein weiterer Header (der 3 Elemente hoch ist) einen Pagebreak auslösen WÜRDE
                // müssen wir hier schonmal vorsorglich den Seitenumbruch machen.
                // 2 Zeilen rechne ich nochdrauf, damit die Tabelle mindestens 2 Zeilen hat, bevor der Seitenumbruch kommt.
                // Das kann dann passieren, wenn dieser if Konstrukt aufgrund eines Monats-Wechsels durchlaufen wird.
                pagebreak = (elementNumber + 3 + 2) > BARBEGTRAG_PAGEBREAK_AFTER_ELEMENT_NO;

                // Außer beim ersten mal und beim Pagebreak, muss dabei die vorherige Tabelle abgeschlossen werden.


                if (monat != -1) { // beim ersten mal nicht
                    html += "<tr>";
                    html += "<td width=\"90\"  align=\"center\" >&nbsp;</td>";
                    html += "<td width=\"400\">" + (monatsWechsel ? "Saldo zum Monatsende" : "Zwischensumme") + "</td>";
                    html += "<td>&nbsp;</td>";
                    html += "<td width=\"70\" align=\"right\"" + (monatsWechsel ? " id=\"fonth2\">" : ">") + "&euro; " + currency.format(saldo) + "</td>";
                    html += "</tr>\n";
                    html += "</table>\n";
                }

                monat = belegDatum.get(GregorianCalendar.MONTH);

                html += "<h2 id=\"fonth2\" " + (pagebreak ? "style=\"page-break-before:always\">" : ">") + ((pagebreak && !monatsWechsel) ? "<i>(fortgesetzt)</i> " : "") + df.format(new Date(belegDatum.getTimeInMillis())) + "</h2>\n";
                html += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\">\n<tr>"
                        + "<th>Belegdatum</th><th>Belegtext</th><th>Betrag</th><th>Saldo</th></tr>\n";

                // Vortragszeile
                html += "<tr id=\"fonttextgrau\">";
                html += "<td width=\"90\"  align=\"center\" >&nbsp;</td>";
                html += "<td width=\"400\">" + (pagebreak && !monatsWechsel ? "Übertrag von vorheriger Seite" : "Übertrag aus Vormonat") + "</td>";
                html += "<td>&nbsp;</td>";
                html += "<td width=\"70\" align=\"right\">&euro; " + currency.format(saldo) + "</td>";
                html += "</tr>\n";

                elementNumber += 3;

                if (pagebreak) {
                    elementNumber = 1;
                    pagebreak = false;
                }
            }

            saldo = saldo.add(tg.getBetrag());

            html += "<tr>";
            html += "<td width=\"90\"  align=\"center\" >" + DateFormat.getDateInstance().format(new Date(belegDatum.getTimeInMillis())) + "</td>";
            html += "<td width=\"400\">" + tg.getBelegtext() + "</td>";
            html += "<td width=\"70\" align=\"right\">&euro; " + currency.format(tg.getBetrag()) + "</td>";
            html += "<td width=\"70\" align=\"right\">&euro; " + currency.format(saldo) + "</td>";
            html += "</tr>\n";
            elementNumber += 1;

            pagebreak = elementNumber > BARBEGTRAG_PAGEBREAK_AFTER_ELEMENT_NO;
        }

        html += "<tr>";
        html += "<td width=\"90\"  align=\"center\" >&nbsp;</td>";
        html += "<td width=\"400\">Saldo zum Monatsende</td>";
        html += "<td>&nbsp;</td>";
        html += "<td width=\"70\" align=\"right\" id=\"fonth2\">&euro; " + currency.format(saldo) + "</td>";
        html += "</tr>\n";

        html += "</table>\n"
                + "</body>";


        return html;
    }


    public static BigDecimal getSUM(Resident resident, DateTime to) {
        OPDE.debug("getSUM to: "+DateFormat.getDateInstance().format(to.toDate()));
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT SUM(al.betrag) FROM Allowance al WHERE al.bewohner = :resident AND al.belegDatum <= :to ");
        query.setParameter("resident", resident);
//        query.setParameter("from", from.toDate());
        query.setParameter("to", to.toDate());
        BigDecimal sum = BigDecimal.ZERO;
        try {
            sum = (BigDecimal) query.getSingleResult();
        } catch (NoResultException nre) {
            sum = BigDecimal.ZERO;
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        if (sum == null) {
            sum = BigDecimal.ZERO;
        }
        OPDE.debug("getSUM sum: "+ sum.toPlainString());
        return sum;
    }

    public static BigDecimal getSUM(Resident resident) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT SUM(al.betrag) FROM Allowance al WHERE al.bewohner = :resident ");
        query.setParameter("resident", resident);
        BigDecimal sum = BigDecimal.ZERO;
        try {
            sum = (BigDecimal) query.getSingleResult();
        } catch (NoResultException nre) {
            sum = BigDecimal.ZERO;
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        if (sum == null) {
            sum = BigDecimal.ZERO;
        }
        return sum;
    }

    public static ArrayList<Allowance> getAll(Resident resident) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT al FROM Allowance al WHERE al.bewohner = :resident ORDER BY al.belegDatum ");
        query.setParameter("resident", resident);
        ArrayList<Allowance> result = null;
        try {
            result = new ArrayList<Allowance>(query.getResultList());
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        if (result == null) {
            result = new ArrayList<Allowance>();
        }
        return result;
    }

    public static ArrayList<Allowance> getMonth(Resident resident, Date month) {
        DateTime from = new DateTime(month).dayOfMonth().withMinimumValue();
        DateTime to = new DateTime(month).dayOfMonth().withMaximumValue();

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT al FROM Allowance al WHERE al.bewohner = :resident AND al.belegDatum >= :from AND al.belegDatum <= :to ORDER BY al.belegDatum DESC, al.id DESC");
        query.setParameter("resident", resident);
        query.setParameter("from", from.toDate());
        query.setParameter("to", to.toDate());

        ArrayList<Allowance> result = null;

        try {
            result = new ArrayList<Allowance>(query.getResultList());
        } catch (Exception e) {
            OPDE.fatal(e);
        }

        return result;
    }

    /**
     * retrieves the first and the last entry in the allowance table.
     * @param resident
     * @return
     */
    public static Pair<Allowance, Allowance> getMinMax(Resident resident) {
        Pair<Allowance, Allowance> result = null;

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createQuery("SELECT al FROM Allowance al WHERE al.bewohner = :resident ORDER BY al.belegDatum ASC ");
        queryMin.setParameter("resident", resident);
        queryMin.setMaxResults(1);

        Query queryMax = em.createQuery("SELECT al FROM Allowance al WHERE al.bewohner = :resident ORDER BY al.belegDatum DESC ");
        queryMax.setParameter("resident", resident);
        queryMax.setMaxResults(1);

        try {
            ArrayList<Allowance> min = new ArrayList<Allowance>(queryMin.getResultList());
            ArrayList<Allowance> max = new ArrayList<Allowance>(queryMax.getResultList());
            result = new Pair<Allowance, Allowance>(min.get(0), max.get(0));
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return result;
    }
}
