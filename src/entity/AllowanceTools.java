/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import entity.info.Resident;
import entity.info.ResidentTools;
import op.OPDE;
import op.allowance.PnlAllowance;
import op.tools.Pair;
import op.tools.SYSCalendar;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author tloehr
 */
public class AllowanceTools {

    public static String getAsHTML(List<Allowance> lstCash, BigDecimal carry, Resident resident) {

        Collections.sort(lstCash, new Comparator<Allowance>() {
            @Override
            public int compare(Allowance o1, Allowance o2) {
                int result = o1.getDate().compareTo(o2.getDate());
                if (result == 0) {
                    result = o1.getId().compareTo(o2.getId());
                }
                return result;
            }
        });

        DecimalFormat cf = new DecimalFormat("######.00");
        Format monthFormatter = new SimpleDateFormat("MMMM yyyy");

        BigDecimal saldo = carry;

        int BARBEGTRAG_PAGEBREAK_AFTER_ELEMENT_NO = Integer.parseInt(OPDE.getProps().getProperty("barbetrag_pagebreak_after_element_no"));

        int elementNumber = 1;
        boolean pagebreak = false;

        String header = OPDE.lang.getString(PnlAllowance.internalClassID + ".printheader") + " " + ResidentTools.getLabelText(resident);

        String html = "";

        html += "<h1 align=\"center\" id=\"fonth1\">" + header + "</h1>";

        int monat = -1;

        for (Allowance tg : lstCash) {

            GregorianCalendar belegDatum = SYSCalendar.toGC(tg.getDate());
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
                    html += "<td width=\"340\">" + (monatsWechsel ? "Saldo zum Monatsende" : "Zwischensumme") + "</td>";
                    html += "<td>&nbsp;</td>";
                    html += "<td width=\"100\" align=\"right\"" + (monatsWechsel ? " id=\"fonth2\">" : ">") + "&euro; " + cf.format(saldo) + "</td>";
                    html += "</tr>\n";
                    html += "</table>\n";
                }

                monat = belegDatum.get(GregorianCalendar.MONTH);

                html += "<h2 id=\"fonth2\" " + (pagebreak ? "style=\"page-break-before:always\">" : ">") + ((pagebreak && !monatsWechsel) ? "<i>(fortgesetzt)</i> " : "") + monthFormatter.format(new Date(belegDatum.getTimeInMillis())) + "</h2>\n";
                html += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\">\n<tr>"
                        + "<th>Belegdatum</th><th>Belegtext</th><th>Betrag</th><th>Saldo</th></tr>\n";

                // Vortragszeile
                html += "<tr id=\"fonttextgrau\">";
                html += "<td width=\"90\"  align=\"center\" >&nbsp;</td>";
                html += "<td width=\"340\">" + (pagebreak && !monatsWechsel ? "Übertrag von vorheriger Seite" : "Übertrag aus Vormonat") + "</td>";
                html += "<td>&nbsp;</td>";
                html += "<td width=\"100\" align=\"right\">&euro; " + cf.format(saldo) + "</td>";
                html += "</tr>\n";

                elementNumber += 3;

                if (pagebreak) {
                    elementNumber = 1;
                    pagebreak = false;
                }
            }

            saldo = saldo.add(tg.getAmount());

            html += "<tr>";
            html += "<td width=\"90\"  align=\"center\" >" + DateFormat.getDateInstance().format(new Date(belegDatum.getTimeInMillis())) + "</td>";
            html += "<td width=\"340\">" + tg.getText() + "</td>";
            html += "<td width=\"100\" align=\"right\">&euro; " + cf.format(tg.getAmount()) + "</td>";
            html += "<td width=\"100\" align=\"right\">&euro; " + cf.format(saldo) + "</td>";
            html += "</tr>\n";
            elementNumber += 1;

            pagebreak = elementNumber > BARBEGTRAG_PAGEBREAK_AFTER_ELEMENT_NO;
        }

        html += "<tr>";
        html += "<td width=\"90\"  align=\"center\" >&nbsp;</td>";
        html += "<td width=\"340\">Saldo zum Monatsende</td>";
        html += "<td>&nbsp;</td>";
        html += "<td width=\"100\" align=\"right\" id=\"fonth2\">&euro; " + cf.format(saldo) + "</td>";
        html += "</tr>\n";

        html += "</table>\n";

        return html;
    }


    public static BigDecimal getSUM(Resident resident, DateTime to) {
//        OPDE.debug("getSUM to: " + DateFormat.getDateInstance().format(to.toDate()));
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT SUM(al.amount) FROM Allowance al WHERE al.resident = :resident AND al.date <= :to ");
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
//        OPDE.debug("getSUM sum: " + sum.toPlainString());
        return sum;
    }

    public static BigDecimal getSUM(Resident resident) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT SUM(al.amount) FROM Allowance al WHERE al.resident = :resident ");
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
        Query query = em.createQuery("SELECT al FROM Allowance al WHERE al.resident = :resident ORDER BY al.date DESC, al.id DESC ");
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

    public static ArrayList<Allowance> getYear(Resident resident, Date year) {
        DateTime from = new DateTime(year).dayOfYear().withMinimumValue();
        DateTime to = new DateTime(year).dayOfYear().withMaximumValue();

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT al FROM Allowance al WHERE al.resident = :resident AND al.date >= :from AND al.date <= :to ORDER BY al.date DESC, al.id DESC");
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

    public static ArrayList<Allowance> getMonth(Resident resident, Date month) {
        DateTime from = new DateTime(month).dayOfMonth().withMinimumValue();
        DateTime to = new DateTime(month).dayOfMonth().withMaximumValue();

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT al FROM Allowance al WHERE al.resident = :resident AND al.date >= :from AND al.date <= :to ORDER BY al.date DESC, al.id DESC");
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
     *
     * @param resident
     * @return
     */
    public static Pair<Allowance, Allowance> getMinMax(Resident resident) {
        Pair<Allowance, Allowance> result = null;

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createQuery("SELECT al FROM Allowance al WHERE al.resident = :resident ORDER BY al.date ASC ");
        queryMin.setParameter("resident", resident);
        queryMin.setMaxResults(1);

        Query queryMax = em.createQuery("SELECT al FROM Allowance al WHERE al.resident = :resident ORDER BY al.date DESC ");
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


    public static String getOverallSumAsHTML(int monthsback) {
        DecimalFormat cf = new DecimalFormat("######.00");
        Format monthFormatter = new SimpleDateFormat("MMMM yyyy");
        String html = "<h1  align=\"center\" id=\"fonth1\">"+OPDE.lang.getString(PnlAllowance.internalClassID+".overallsum")+"</h1>";

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT SUM(al.amount) FROM Allowance al WHERE al.date <= :end");

        DateMidnight from = new DateMidnight().dayOfMonth().withMinimumValue().minusMonths(monthsback);


        html += "<table>";

        for (DateMidnight end = new DateMidnight().dayOfMonth().withMaximumValue(); end.isAfter(from); end = end.minusMonths(1)){
            query.setParameter("end", end.toDate());
            BigDecimal bd = (BigDecimal) query.getSingleResult();

            String fonttext = end.getMonthOfYear() % 2 == 0 ? "fonttext" : "fonttextgray";


            html += "<tr><td id=\""+fonttext+"\" width=\"300\" align=\"right\">"+monthFormatter.format(end.toDate())+"</td><td  id=\""+fonttext+"\"  width=\"100\" align=\"right\">"+cf.format(bd)+" &euro;</td></tr>";
        }

        html += "</table>";
        em.close();
        return html;
    }
}
