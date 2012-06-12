/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.verordnungen;

import entity.*;
import op.OPDE;
import op.tools.HTMLTools;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author tloehr
 */
public class VerordnungTools {

    /**
     * Diese Methode ermittelt eine Liste aller Verordnungen gemäß der unten genannten Einschränkungen.
     *
     * @param bewohner dessen Verordnungen wir suchen
     * @return Eine Liste aus Objekt Arrays, die folgenden Aufbau hat:
     *         <center><code><b>{Verordnung, Vorrat, Saldo des Vorrats,
     *         Bestand (im Anbruch), Saldo des Bestandes, Bezeichnung des Medikamentes, Bezeichnung der Massnahme}</b></code></center>
     *         Es gibt Verordnungen, die keine Medikamente besitzen, bei denen steht dann <code>null</code> an den entsprechenden
     *         Stellen.
     */
    public static List<Object[]> getVerordnungenUndVorraeteUndBestaende(Bewohner bewohner, boolean archiv) {
        List<Object[]> listResult = new ArrayList<Object[]>();

        EntityManager em = OPDE.createEM();
        Query queryVerordnung = em.createQuery("SELECT v FROM Verordnung v WHERE " + (archiv ? "" : " v.abDatum >= :now AND ") + " v.bewohner = :bewohner ");
        if (!archiv) {
            queryVerordnung.setParameter("now", new Date());
        }
        queryVerordnung.setParameter("bewohner", bewohner);
        List<Verordnung> listeVerordnung = queryVerordnung.getResultList();

        for (Verordnung verordnung : listeVerordnung) {

            MedVorrat vorrat = verordnung.getDarreichung() == null ? null : DarreichungTools.getVorratZurDarreichung(bewohner, verordnung.getDarreichung());
            MedBestand aktiverBestand = MedBestandTools.getBestandImAnbruch(vorrat);
            listResult.add(new Object[]{verordnung, vorrat, aktiverBestand});
        }

        em.close();

        return listResult;
    }


    /**
     * Diese Methode erzeugt einen Stellplan für den aktuellen Tag im HTML Format.
     * Eine Besonderheit bei der Implementierung muss ich hier erläutern.
     * Aufgrund der ungleichen HTML Standards (insbesonders der Druckdarstellung im CSS2.0 und später auch CSS2.1)
     * muss ich hier einen Trick anwenden, damit das auf verschiedenen Browsern halbwegs gleich aussieht.
     * <p/>
     * Daher addiere ich jedes größere Element auf einer Seite (also Header, Tabellen Zeilen) mit dem Wert 1.
     * Nach einer bestimmten Anzahl von Elementen erzwinge ich einen Pagebreak.
     * <p/>
     * Nach einem Pagebreak wird der Name des aktuellen Bewohner nocheinmal wiederholt.
     * <p/>
     * Ein Mac OS Safari druckt mit diesen Werten sehr gut.
     * Beim Firefox sollten die Ränder wie folgt eingestellt werden:
     * <ul>
     * <li>print.print_margin_bottom = 0.3</li>
     * <li>print.print_margin_left = 0.1</li>
     * <li>print.print_margin_right = 0.1</li>
     * <li>print.print_margin_top = 0.3</li>
     * <li>print.print_unwriteable_margin_bottom = 57</li>
     * <li>print.print_unwriteable_margin_left = 25</li>
     * <li>print.print_unwriteable_margin_right = 25</li>
     * <li>print.print_unwriteable_margin_top = 25</li>
     * <li>Drucken des Hintergrundes einschalten</li>
     * <ul>
     *
     * @param einrichtungen Die Einrichtung, für die der Stellplan erstellt werden soll. Sortiert nach den Stationen.
     */
    public static String getStellplanAsHTML(Einrichtungen einrichtungen) {
        EntityManager em = OPDE.createEM();
        String html = "";

        try {
            Query query = em.createNativeQuery("" +
                    " SELECT v.VerID, st.StatID, bhp.BHPPID, best.BestID, vor.VorID, F.FormID, M.MedPID, M.Bezeichnung, Ms.Bezeichnung " +
                    " FROM BHPVerordnung v " +
                    " INNER JOIN Bewohner bw ON v.BWKennung = bw.BWKennung  " +
                    " INNER JOIN Massnahmen Ms ON Ms.MassID = v.MassID " +
                    " INNER JOIN Stationen st ON bw.StatID = st.StatID  " +
                    " LEFT OUTER JOIN MPDarreichung D ON v.DafID = D.DafID " +
                    " LEFT OUTER JOIN BHPPlanung bhp ON bhp.VerID = v.VerID " +
                    " LEFT OUTER JOIN MProdukte M ON M.MedPID = D.MedPID " +
                    " LEFT OUTER JOIN MPFormen F ON D.FormID = F.FormID " +
                    " LEFT OUTER JOIN ( " +
                    "      SELECT DISTINCT M.VorID, M.BWKennung, B.DafID FROM MPVorrat M  " +
                    "      INNER JOIN MPBestand B ON M.VorID = B.VorID " +
                    "      WHERE M.Bis = '9999-12-31 23:59:59' " +
                    " ) vorr ON vorr.DafID = v.DafID AND vorr.BWKennung = v.BWKennung" +
                    " LEFT OUTER JOIN MPVorrat vor ON vor.VorID = vorr.VorID" +
                    " LEFT OUTER JOIN MPBestand best ON best.VorID = vor.VorID" +
                    " WHERE v.AnDatum < now() AND v.AbDatum > now() AND v.SitID IS NULL AND (v.DafID IS NOT NULL OR v.Stellplan IS TRUE) " +
                    " AND st.EKennung = ? AND ((best.Aus = '9999-12-31 23:59:59' AND best.Anbruch < '9999-12-31 23:59:59') OR (v.DafID IS NULL)) " +
                    " ORDER BY st.statid, CONCAT(bw.nachname,bw.vorname), bw.BWKennung, v.DafID IS NOT NULL, F.Stellplan, CONCAT( M.Bezeichnung, Ms.Bezeichnung)");
            query.setParameter(1, einrichtungen.getEKennung());
            html = getStellplan(query.getResultList());

        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return html;
    }

    /**
     * Erzeugt eine Liste mit EntityBeans und Salden. Diese Liste enthält die zur Zeit verordnete Bedarfsverordnungen.
     * Die Liste enthält ein Objekt Array mit dem folgenden Aufbau:
     * <ol>
     * <li></li>
     * </ol>
     *
     * @param bewohner
     * @return Liste mit allen Bedarfsverordnungen. <code>null</code>, wenn nichts da war oder bei Fehler.
     */
    public static List getBedarfsliste(Bewohner bewohner) {
        String sql = " SELECT v.VerID, s.SitID, p.BHPPID, vor.Saldo, bisher.tagesdosis, d.DafID, bestand.APV, bestand.Summe, bestand.BestID " +
                " FROM BHPVerordnung v " +
                " INNER JOIN Situationen s ON v.SitID = s.SitID " +
                " INNER JOIN BHPPlanung p ON v.VerID = p.VerID" +
                " LEFT OUTER JOIN MPDarreichung d ON v.DafID = d.DafID " +
                // Dieser Konstrukt bestimmt die Vorräte für einen Bewohner
                // Dabei wird berücksichtigt, dass ein Vorrat unterschiedliche Hersteller umfassen
                // kann. Dies wird durch den mehrfach join erreicht. Dadurch stehen die verschiedenen
                // DafIDs der unterschiedlichen Produkte im selben Vorrat jeweils in verschiedenen Zeilen.
                // Durch den LEFT OUTER JOIN pickt sich die Datenbank die richtigen Paare heraus.
                " LEFT OUTER JOIN " +
                "      ( " +
                "        SELECT DISTINCT a.VorID, b.DafID, a.saldo FROM ( " +
                "           SELECT best.VorID, best.DafID, sum(buch.Menge) saldo FROM MPBestand best " +
                "           INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
                "           INNER JOIN MPVorrat vor1 ON best.VorID = vor1.VorID" +
                "           WHERE vor1.BWKennung=? AND vor1.Bis = '9999-12-31 23:59:59'" +
                "           GROUP BY VorID" +
                "           ) a  " +
                "        INNER JOIN (" +
                "           SELECT best.VorID, best.DafID FROM MPBestand best " +
                "           ) b ON a.VorID = b.VorID " +
                "      ) vor ON vor.DafID = v.DafID " +
                // Hier wird berechnet, wieviel von der Tagesdosis der Bewohner heute schon bekommen hat.
                " LEFT OUTER JOIN" +
                "      (" +
                "        SELECT b3.VerID, sum(b1.dosis) tagesdosis " +
                "        FROM BHP b1" +
                "        INNER JOIN BHPPlanung b2 ON b1.BHPPID = b2.BHPPID" +
                "        INNER JOIN BHPVerordnung b3 ON b3.VerID = b2.VerID" +
                "        WHERE b3.BWKennung=? AND b3.AbDatum = '9999-12-31 23:59:59'" +
                "        AND DATE(b1.Ist) = Date(now()) AND b1.Status = " + BHPTools.STATUS_ERLEDIGT +
                "        GROUP BY b3.VerID" +
                "      ) bisher ON bisher.VerID = v.VerID" +
                // Hier kommen jetzt die Bestände im Anbruch dabei. Die Namen der Medikamente könnten ja vom
                // ursprünglich verordneten abweichen.
                " LEFT OUTER JOIN( " +
                "        SELECT best1.NextBest, best1.VorID, best1.BestID, best1.DafID, best1.APV, SUM(buch1.Menge) summe " +
                "        FROM MPBestand best1 " +
                "        INNER JOIN MPBuchung buch1 ON buch1.BestID = best1.BestID " +
                "        WHERE best1.Aus = '9999-12-31 23:59:59' AND best1.Anbruch < now() " +
                "        GROUP BY best1.BestID" +
                "      ) bestand ON bestand.VorID = vor.VorID " +
                " LEFT OUTER JOIN MPDarreichung D1 ON bestand.DafID = D1.DafID " +
                " LEFT OUTER JOIN MProdukte M1 ON M1.MedPID = D1.MedPID " +
                " WHERE v.BWKennung = ? AND v.AbDatum = '9999-12-31 23:59:59' " +
                " ORDER BY s.Text";
        EntityManager em = OPDE.createEM();

        Query query = em.createNativeQuery(sql);
        query.setParameter(1, bewohner.getBWKennung());
        query.setParameter(2, bewohner.getBWKennung());
        query.setParameter(3, bewohner.getBWKennung());
        List<Object[]> listeRohfassung = query.getResultList();
        ArrayList<Object[]> listeBedarf = null;

        if (!listeRohfassung.isEmpty()) {
            listeBedarf = new ArrayList<Object[]>(listeRohfassung.size());

            for (Object[] rohdaten : listeRohfassung) {
                Verordnung verordnung = em.find(Verordnung.class, ((BigInteger) rohdaten[0]).longValue());
                Situationen situation = em.find(Situationen.class, ((BigInteger) rohdaten[1]).longValue());
                VerordnungPlanung planung = em.find(VerordnungPlanung.class, ((BigInteger) rohdaten[2]).longValue());
                BigDecimal vorratSaldo = (BigDecimal) rohdaten[3];
                BigDecimal tagesdosisBisher = (BigDecimal) rohdaten[4];
                Darreichung darreichung = rohdaten[5] == null ? null : em.find(Darreichung.class, ((BigInteger) rohdaten[5]).longValue());
                BigDecimal apv = (BigDecimal) rohdaten[6];
                BigDecimal bestandSumme = (BigDecimal) rohdaten[7];
                MedBestand bestand = rohdaten[8] == null ? null : em.find(MedBestand.class, ((BigInteger) rohdaten[8]).longValue());

                listeBedarf.add(new Object[]{verordnung, situation, planung, vorratSaldo, tagesdosisBisher, darreichung, apv, bestandSumme, bestand});
            }
        }

        em.close();
        return listeBedarf;
    }


    public static boolean hasBedarf(Bewohner bewohner) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT COUNT(v) FROM Verordnung v WHERE v.bewohner = :bewohner AND v.abDatum >= :now AND v.situation IS NOT NULL ");
        query.setParameter("bewohner", bewohner);
        query.setParameter("now", new Date());
        return ((Long) query.getSingleResult()).longValue() > 0;
    }

    private static String getStellplan(List data) {

        int STELLPLAN_PAGEBREAK_AFTER_ELEMENT_NO = Integer.parseInt(OPDE.getProps().getProperty("stellplan_pagebreak_after_element_no"));

        int elementNumber = 1;
        boolean pagebreak = false;

        String header = "Stellplan für den " + DateFormat.getDateInstance().format(new Date());

        String html = "<html>"
                + "<head>"
                + "<title>" + header + "</title>"
                + OPDE.getCSS()
                + HTMLTools.JSCRIPT_PRINT
                + "</head>"
                + "<body>";

        String bwkennung = "";
        long statid = 0;

        Iterator it = data.iterator();

        EntityManager em = OPDE.createEM();

        while (it.hasNext()) {

            Object[] objects = (Object[]) it.next();

            Verordnung verordnung = em.find(Verordnung.class, ((BigInteger) objects[0]).longValue());
            Stationen station = em.find(Stationen.class, ((BigInteger) objects[1]).longValue());
            VerordnungPlanung planung = em.find(VerordnungPlanung.class, ((BigInteger) objects[2]).longValue());

            BigInteger bestid = (BigInteger) objects[3];
            //Vorrat wäre objects[4]
            BigInteger formid = (BigInteger) objects[5];

            OPDE.debug(verordnung);


            boolean stationsWechsel = statid != station.getStatID();

            // Wenn der Plan für eine ganze Einrichtung gedruckt wird, dann beginnt eine
            // neue Station immer auf einer neuen Seite.
            if (stationsWechsel) {
                elementNumber = 1;
                // Beim ersten Mal nur ein H1 Header. Sonst mit Seitenwechsel.
                if (statid == 0) {
                    html += "<h1 align=\"center\" id=\"fonth1\">";
                } else {
                    html += "</table>";
                    html += "<h1 align=\"center\" id=\"fonth1\" style=\"page-break-before:always\">";
                }
                html += header + " (" + station.getBezeichnung() + ")" + "</h1>";
                html += "<div align=\"center\" id=\"fontsmall\">Stellpläne <u>nur einen Tag</u> lang benutzen! Danach <u>müssen sie vernichtet</u> werden.</div>";
                statid = station.getStatID();
            }


            // Alle Formen, die nicht abzählbar sind, werden grau hinterlegt. Also Tropfen, Spritzen etc.
            boolean grau = false;
            if (formid != null) {
                MedFormen form = em.find(MedFormen.class, formid.longValue());
                grau = form.getStellplan() > 0;
            }

            // Wenn der Bewohnername sich in der Liste ändert, muss
            // einmal die Überschrift drüber gesetzt werden.
            boolean bewohnerWechsel = !bwkennung.equalsIgnoreCase(verordnung.getBewohner().getBWKennung());

            if (pagebreak || stationsWechsel || bewohnerWechsel) {
                // Falls zufällig ein weiterer Header (der 2 Elemente hoch ist) einen Pagebreak auslösen WÜRDE
                // müssen wir hier schonmal vorsorglich den Seitenumbruch machen.
                // 2 Zeilen rechne ich nochdrauf, damit die Tabelle mindestens 2 Zeilen hat, bevor der Seitenumbruch kommt.
                // Das kann dann passieren, wenn dieser if Konstrukt aufgrund eines BW Wechsels durchlaufen wird.
                pagebreak = (elementNumber + 2 + 2) > STELLPLAN_PAGEBREAK_AFTER_ELEMENT_NO;

                // Außer beim ersten mal und beim Pagebreak, muss dabei die vorherige Tabelle abgeschlossen werden.
                if (pagebreak || !bwkennung.equals("")) {
                    html += "</table>";
                }

                bwkennung = verordnung.getBewohner().getBWKennung();

                html += "<h2 id=\"fonth2\" " +
                        (pagebreak ? "style=\"page-break-before:always\">" : ">") +
                        ((pagebreak && !bewohnerWechsel) ? "<i>(fortgesetzt)</i> " : "")
                        + BewohnerTools.getBWLabelText(verordnung.getBewohner())
                        + "</h2>";
                html += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>"
                        + "<th>Präparat / Massnahme</th><th>FM</th><th>MO</th><th>MI</th><th>NM</th><th>AB</th><th>NA</th><th>Bemerkungen</th></tr>";
                elementNumber += 2;

                if (pagebreak) {
                    elementNumber = 1;
                    pagebreak = false;
                }
            }


            html += "<tr " + (grau ? "id=\"fonttextgrau\">" : ">");
            html += "<td width=\"300\" >" + (verordnung.hasMedi() ? "<b>" + DarreichungTools.toPrettyString(verordnung.getDarreichung()) + "</b>" : verordnung.getMassnahme().getBezeichnung());
            html += (bestid != null ? "<br/><i>Bestand im Anbruch Nr.: " + bestid + "</i>" : "") + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getNachtMo()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getMorgens()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getMittags()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getNachmittags()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getAbends()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getNachtAb()) + "</td>";
            html += "<td width=\"300\" >" + VerordnungPlanungTools.getHinweis(planung, false) + "</td>";
            html += "</tr>";
            elementNumber += 1;

            pagebreak = elementNumber > STELLPLAN_PAGEBREAK_AFTER_ELEMENT_NO;
        }

        em.close();

        html += "</table>"
                + "</body>";


        return html;
    }

    public static String getMassnahme(Verordnung verordnung) {
        String result = SYSConst.html_fontface;

        if (verordnung.isAbgesetzt()) {
            result += "<s>"; // Abgesetzte
        }
        if (!verordnung.hasMedi()) {
            result += verordnung.getMassnahme().getBezeichnung();
        } else {
            // Prüfen, was wirklich im Anbruch gegeben wird. (Wenn das Medikament über die Zeit gegen Generica getauscht wurde.)

            MedVorrat vorrat = DarreichungTools.getVorratZurDarreichung(verordnung.getBewohner(), verordnung.getDarreichung());
            MedBestand aktuellerAnbruch = MedBestandTools.getBestandImAnbruch(vorrat);

            if (aktuellerAnbruch != null) {
                if (!aktuellerAnbruch.getDarreichung().equals(verordnung.getDarreichung())) { // Nur bei Abweichung.
                    result += "<b>" + aktuellerAnbruch.getDarreichung().getMedProdukt().getBezeichnung().replaceAll("-", "- ") +
                            (aktuellerAnbruch.getDarreichung().getZusatz().isEmpty() ? "" : " " + aktuellerAnbruch.getDarreichung().getZusatz()) + "</b>" +
                            (aktuellerAnbruch.getDarreichung().getMedForm().getZubereitung().isEmpty() ? "" : " " + aktuellerAnbruch.getDarreichung().getMedForm().getZubereitung()) + " " +
                            (aktuellerAnbruch.getDarreichung().getMedForm().getAnwText().isEmpty() ? SYSConst.EINHEIT[aktuellerAnbruch.getDarreichung().getMedForm().getAnwEinheit()] : aktuellerAnbruch.getDarreichung().getMedForm().getAnwText());
                    result += " <i>(ursprünglich verordnet: " + verordnung.getDarreichung().getMedProdukt().getBezeichnung().replaceAll("-", "- ");
                    result += (aktuellerAnbruch.getDarreichung().getZusatz().isEmpty() ? "" : " " + aktuellerAnbruch.getDarreichung().getZusatz()) + ")</i>";
                } else {
                    result += "<b>" + verordnung.getDarreichung().getMedProdukt().getBezeichnung().replaceAll("-", "- ")
                            + (aktuellerAnbruch.getDarreichung().getZusatz().isEmpty() ? "" : " " + aktuellerAnbruch.getDarreichung().getZusatz()) + "</b>" +
                            (aktuellerAnbruch.getDarreichung().getMedForm().getZubereitung().isEmpty() ? "" : " " + aktuellerAnbruch.getDarreichung().getMedForm().getZubereitung()) + " " +
                            (verordnung.getDarreichung().getMedForm().getAnwText().isEmpty() ? SYSConst.EINHEIT[verordnung.getDarreichung().getMedForm().getAnwEinheit()] : verordnung.getDarreichung().getMedForm().getAnwText());
                }
            } else {
                result += "<b>" + verordnung.getDarreichung().getMedProdukt().getBezeichnung().replaceAll("-", "- ")
                        + (verordnung.getDarreichung().getZusatz().isEmpty() ? "" : " " + verordnung.getDarreichung().getZusatz()) + "</b>" +
                        (verordnung.getDarreichung().getMedForm().getZubereitung().isEmpty() ? "" : " " + verordnung.getDarreichung().getMedForm().getZubereitung()) + " " +
                        (verordnung.getDarreichung().getMedForm().getAnwText().isEmpty() ? SYSConst.EINHEIT[verordnung.getDarreichung().getMedForm().getAnwEinheit()] : verordnung.getDarreichung().getMedForm().getAnwText());
            }


        }
        if (verordnung.isAbgesetzt()) {
            result += "</s>"; // Abgesetzte
        }

        return result + "</font>";
    }

    public static String getHinweis(Verordnung verordnung) {
        String result = SYSConst.html_fontface;

        if (verordnung.isBedarf()) {
            result += "<b><u>Nur bei Bedarf:</u> <font color=\"blue\">" + verordnung.getSituation().getText() + "</font></b>";
        }
        if (!verordnung.getBemerkung().isEmpty()) {
            result += result.isEmpty() ? "" : "<br/>";
            result += "<b><u>Bemerkung:</u> </b>" + verordnung.getBemerkung();
        }
        return result+"</font>";
    }

    public static String getAN(Verordnung verordnung) {
        String result = "<font face=\"" + OPDE.arial14.getFamily() + "\">";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        String datum = sdf.format(verordnung.getAnDatum());


        result += "<font color=\"green\">" + datum + "; ";
        if (verordnung.getAnKH() != null) {
            result += verordnung.getAnKH().getName();
        }
        if (verordnung.getAnArzt() != null) {
            if (verordnung.getAnKH() != null) {
                result += " <i>bestätigt durch:</i> ";
            }
            result += verordnung.getAnArzt().getAnrede() + " " + SYSTools.anonymizeName(verordnung.getAnArzt().getName(), SYSTools.INDEX_NACHNAME);
        }
        result += "; " + verordnung.getAngesetztDurch().getNameUndVorname() + "</font>";


        return result + "</font>";
    }

    public static String getAB(Verordnung verordnung) {
        String result = "<font face=\"" + OPDE.arial14.getFamily() + "\">";

        if (verordnung.isBegrenzt()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
            String datum = sdf.format(verordnung.getAbDatum());


            result += "<font color=\"" + (verordnung.isAbgesetzt() ? "red" : "lime") + "\">" + datum + "; ";

            result += verordnung.getAbKH() != null ? verordnung.getAbKH().getName() : "";

            if (verordnung.getAbArzt() != null) {
                if (verordnung.getAbKH() != null) {
                    result += " <i>bestätigt durch:</i> ";
                }
                result += verordnung.getAbArzt().getAnrede() + " " + SYSTools.anonymizeName(verordnung.getAbArzt().getName(), SYSTools.INDEX_NACHNAME);
            }
            result += "; " + verordnung.getAbgesetztDurch().getNameUndVorname() + "</font>";

        }
        return result + "</font>";
    }

    public static String getDosis(Verordnung verordnung) {
        return getDosis(verordnung, false, null, null);
    }

    public static String getDosis(Verordnung verordnung, boolean mitBestandsAnzeige, MedVorrat vorrat, MedBestand bestandImAnbruch) {
//        long timestart = System.currentTimeMillis();
        String result = "<font face=\"" + OPDE.arial14.getFamily() + "\">";
        if (verordnung.getPlanungen().size() > 1) {
            Collections.sort(verordnung.getPlanungen());
        }
        Iterator<VerordnungPlanung> planungen = verordnung.getPlanungen().iterator();

        if (planungen.hasNext()) {
            VerordnungPlanung vorherigePlanung = null;
            VerordnungPlanung planung = null;
            while (planungen.hasNext()) {
                planung = planungen.next();
                result += VerordnungPlanungTools.getDosisAsHTML(planung, vorherigePlanung, false);
                vorherigePlanung = planung;
            }
            if (VerordnungPlanungTools.getTerminStatus(planung) != VerordnungPlanungTools.MAXDOSIS) {
                // Wenn die letzte Planung eine Tabelle benötigte (das tut sie dann, wenn
                // es keine Bedarfsverordnung war), dann müssen wir die Tabelle hier noch
                // schließen.
                result += "</table>";
            }
        } else {
            result += "<i>Noch keine Dosierung / Anwendungsinformationen verfügbar</i><br/>";
        }

        if (mitBestandsAnzeige && verordnung.hasMedi()) {
            if (verordnung.isBisPackEnde()) {
                result += "nur bis Packungs Ende<br/>";
            }
            if (!verordnung.isAbgesetzt()) {
                if (bestandImAnbruch != null) {
                    EntityManager em = OPDE.createEM();

                    BigDecimal vorratSumme = null;
                    BigDecimal bestandSumme = null;
                    try {
                        vorratSumme = MedVorratTools.getVorratSumme(em, bestandImAnbruch.getVorrat());
                        bestandSumme = MedBestandTools.getBestandSumme(em, bestandImAnbruch);
                    } catch (Exception e) {
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }


                    if (vorratSumme != null && vorratSumme.compareTo(BigDecimal.ZERO) > 0) {
                        result += "<b><u>Vorrat:</u> <font color=\"green\">" + SYSTools.roundScale2(vorratSumme) + " " +
                                SYSConst.EINHEIT[bestandImAnbruch.getDarreichung().getMedForm().getPackEinheit()] +
                                "</font></b>";
                        if (!bestandImAnbruch.getDarreichung().getMedForm().anwUndPackEinheitenGleich()) {

                            BigDecimal anwmenge = vorratSumme.multiply(bestandImAnbruch.getApv());


                            //double anwmenge = SYSTools.roundScale2(rs.getDouble("saldo") * rs.getDouble("APV"));
                            result += " <i>entspricht " + SYSTools.roundScale2(anwmenge) + " " +//SYSConst.EINHEIT[rs.getInt("f.AnwEinheit")]+"</i>";
                                    MedFormenTools.getAnwText(bestandImAnbruch.getDarreichung().getMedForm());
                            result += " (bei einem APV von " + SYSTools.roundScale2(bestandImAnbruch.getApv()) + " zu 1)";
                            result += "</i>";
                        }

                        result += "<br/>Bestand im Anbruch Nr.: <b><font color=\"green\">" + bestandImAnbruch.getBestID() + "</font></b>";

                        if (vorratSumme.compareTo(bestandSumme) != 0) {
                            result += "<br/>Restmenge im Anbruch: <b><font color=\"green\">" + bestandSumme.setScale(2, BigDecimal.ROUND_UP) + " " +
                                    SYSConst.EINHEIT[bestandImAnbruch.getDarreichung().getMedForm().getPackEinheit()] + "</font></b>";
                            if (!bestandImAnbruch.getDarreichung().getMedForm().anwUndPackEinheitenGleich()) {
                                //double anwmenge = SYSTools.roundScale2(rs.getDouble("bestsumme") * rs.getDouble("APV"));
                                BigDecimal anwmenge = bestandSumme.multiply(bestandImAnbruch.getApv());

                                result += " <i>entspricht " + anwmenge.setScale(2, BigDecimal.ROUND_UP) + " " +//SYSConst.EINHEIT[rs.getInt("f.AnwEinheit")]+"</i>";
                                        MedFormenTools.getAnwText(bestandImAnbruch.getDarreichung().getMedForm()) + "</i>";
                            }
                        }

                    } else {
                        result += "<b><font color=\"red\">Der Vorrat an diesem Medikament ist <u>leer</u>.</font></b>";
                    }
                } else {
                    if (vorrat == null) {
                        result += "<b><font color=\"red\">Es gibt bisher keinen Vorrat für dieses Medikament.</font></b>";
                    } else {
                        if (MedVorratTools.getNaechsteNochUngeoeffnete(vorrat) != null) {
                            result += "<br/><b><font color=\"red\">Kein Bestand im Anbruch. Vergabe nicht möglich.</font></b>";
                        } else {
                            result += "<br/><b><font color=\"red\">Keine Bestände mehr im Vorrat vorhanden. Vergabe nicht möglich.</font></b>";
                        }

                    }
                }
            }

        }


//        long timeend = System.currentTimeMillis();

//        OPDE.debug("time end: " + (timeend - timestart) + " millis");

        return result + "</font>";
    }

    /**
     * Dieser Query ordnet Verordnungen den Vorräten zu. Dazu ist ein kleiner Trick nötig. Denn über die Zeit können verschiedene Vorräte mit verschiedenen
     * Darreichungen für dieselbe Verordnung verwendet werden. Der Trick ist der Join über zwei Spalten in der Zeile mit "MPBestand"
     */
    public static List<Verordnung> getVerordnungenByVorrat(MedVorrat vorrat) {
        EntityManager em = OPDE.createEM();

        List<BigInteger> list = null;
        List<Verordnung> result = null;
        Query query = em.createNativeQuery(" SELECT DISTINCT ver.VerID FROM BHPVerordnung ver " +
                " INNER JOIN MPVorrat v ON v.BWKennung = ver.BWKennung " + // Verbindung über Bewohner
                " INNER JOIN MPBestand b ON ver.DafID = b.DafID AND v.VorID = b.VorID " + // Verbindung über Bestand zur Darreichung UND dem Vorrat
                " WHERE b.VorID=? AND ver.AbDatum > now() ");
        query.setParameter(1, vorrat.getVorID());
        list = query.getResultList();

        if (!list.isEmpty()) {
            result = new ArrayList<Verordnung>(list.size());
            for (BigInteger verid : list) {
                result.add(em.find(Verordnung.class, verid.longValue()));
            }
        }
        em.close();

        return result;
    }

    /**
     * Gibt eine HTML Darstellung der Verordungen zurück, die in dem übergebenen TableModel enthalten sind.
     */
    public static String getVerordnungenAsHTML(List<Verordnung> list) {
        String result = "";

        if (!list.isEmpty()) {

            Verordnung verordnung = list.get(0);
//                if (SYSTools.catchNull(bwkennung).equals("")) {
//                    result += "<h2>Ärztliche Verordnungen</h2>";
//                } else {
            result += "<h1  id=\"fonth1\" >Ärztliche Verordnungen für " + BewohnerTools.getBWLabelText(verordnung.getBewohner()) + "</h1>";
            if (verordnung.getBewohner().getStation() != null) {
                result += EinrichtungenTools.getAsText(verordnung.getBewohner().getStation().getEinrichtung());
            }

            result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>" +
                    "<th >Medikament/Massnahme</th><th >Dosierung / Hinweise</th><th >Angesetzt</th></tr>";

            Iterator<Verordnung> itVerordnung = list.iterator();
            while (itVerordnung.hasNext()) {
                verordnung = itVerordnung.next();

                result += "<tr>";
                result += "<td valign=\"top\">" + getMassnahme(verordnung) + "</td>";
                result += "<td valign=\"top\">" + getDosis(verordnung) + "<br/>";
                result += getHinweis(verordnung) + "</td>";
                result += "<td valign=\"top\">" + getAN(verordnung) + "</td>";
                //result += "<td>" + SYSTools.unHTML2(tmv.getValueAt(v, TMVerordnung.COL_AB).toString()) + "</td>";
                result += "</tr>";
            }

            result += "</table>";
        } else {
            result += "<h2  id=\"fonth2\" >Ärztliche Verordnungen</h2><i>zur Zeit gibt es keine Verordnungen</i>";
        }
        return result;
    }

    /**
     * Ermittelt die Anzahl der Verordnungen, die zu dieser Verordnung gemäß der VerordnungKennung gehören.
     * Verordnung, die über die Zeit mehrfach geändert werden, hängen über die VerordnungsKennung aneinander.
     *
     * @param verordnung
     * @return Anzahl der Verordnungen, die zu dieser gehören.e
     */
    public static int getNumVerodnungenMitGleicherKennung(Verordnung verordnung) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Verordnung.findByVerKennung");
        query.setParameter("verKennung", verordnung.getVerKennung());
        int num = query.getResultList().size();
        em.close();
        return num;
    }

    public static String toPrettyString(Verordnung verordnung) {
        String myPretty = "";

        if (verordnung.hasMedi()) {
            myPretty = DarreichungTools.toPrettyString(verordnung.getDarreichung());
        } else {
            myPretty = verordnung.getMassnahme().getBezeichnung();
        }

        myPretty += verordnung.isBedarf() ? " (Nur bei Bedarf: " + verordnung.getSituation().getText() + ")" : "";

        return myPretty;
    }

}
