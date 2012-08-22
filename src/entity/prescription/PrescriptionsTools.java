/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.prescription;

import entity.Einrichtungen;
import entity.Stationen;
import entity.info.Resident;
import entity.info.ResidentTools;
import op.OPDE;
import op.care.verordnung.PnlVerordnung;
import op.tools.HTMLTools;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.*;

/**
 * @author tloehr
 */
public class PrescriptionsTools {

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
    public static List getBedarfsliste(Resident bewohner) {
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
                "        AND DATE(b1.Ist) = Date(now()) AND b1.Status = " + BHPTools.STATE_DONE +
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
                Prescriptions verordnung = em.find(Prescriptions.class, ((BigInteger) rohdaten[0]).longValue());
                Situationen situation = em.find(Situationen.class, ((BigInteger) rohdaten[1]).longValue());
                PrescriptionSchedule planung = em.find(PrescriptionSchedule.class, ((BigInteger) rohdaten[2]).longValue());
                BigDecimal vorratSaldo = (BigDecimal) rohdaten[3];
                BigDecimal tagesdosisBisher = (BigDecimal) rohdaten[4];
                TradeForm darreichung = rohdaten[5] == null ? null : em.find(TradeForm.class, ((BigInteger) rohdaten[5]).longValue());
                BigDecimal apv = (BigDecimal) rohdaten[6];
                BigDecimal bestandSumme = (BigDecimal) rohdaten[7];
                MedStock bestand = rohdaten[8] == null ? null : em.find(MedStock.class, ((BigInteger) rohdaten[8]).longValue());

                listeBedarf.add(new Object[]{verordnung, situation, planung, vorratSaldo, tagesdosisBisher, darreichung, apv, bestandSumme, bestand});
            }
        }

        em.close();
        return listeBedarf;
    }


    public static boolean hasBedarf(Resident resident) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT COUNT(v) FROM Prescriptions v WHERE v.resident = :resident AND v.abDatum >= :now AND v.situation IS NOT NULL ");
        query.setParameter("resident", resident);
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

            Prescriptions verordnung = em.find(Prescriptions.class, ((BigInteger) objects[0]).longValue());
            Stationen station = em.find(Stationen.class, ((BigInteger) objects[1]).longValue());
            PrescriptionSchedule planung = em.find(PrescriptionSchedule.class, ((BigInteger) objects[2]).longValue());

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
                DosageForm form = em.find(DosageForm.class, formid.longValue());
                grau = form.getStellplan() > 0;
            }

            // Wenn der Bewohnername sich in der Liste ändert, muss
            // einmal die Überschrift drüber gesetzt werden.
            boolean bewohnerWechsel = !bwkennung.equalsIgnoreCase(verordnung.getResident().getBWKennung());

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

                bwkennung = verordnung.getResident().getBWKennung();

                html += "<h2 id=\"fonth2\" " +
                        (pagebreak ? "style=\"page-break-before:always\">" : ">") +
                        ((pagebreak && !bewohnerWechsel) ? "<i>(fortgesetzt)</i> " : "")
                        + ResidentTools.getLabelText(verordnung.getResident())
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
            html += "<td width=\"300\" >" + (verordnung.hasMed() ? "<b>" + TradeFormTools.toPrettyString(verordnung.getTradeForm()) + "</b>" : verordnung.getMassnahme().getBezeichnung());
            html += (bestid != null ? "<br/><i>Bestand im Anbruch Nr.: " + bestid + "</i>" : "") + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getNachtMo()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getMorgens()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getMittags()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getNachmittags()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getAbends()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getNachtAb()) + "</td>";
            html += "<td width=\"300\" >" + PrescriptionScheduleTools.getHinweis(planung, false) + "</td>";
            html += "</tr>";
            elementNumber += 1;

            pagebreak = elementNumber > STELLPLAN_PAGEBREAK_AFTER_ELEMENT_NO;
        }

        em.close();

        html += "</table>"
                + "</body>";


        return html;
    }

    public static String getPrescriptionAsText(Prescriptions verordnung) {
        String result = "<div id=\"fonttext\">";// = SYSConst.html_fontface;

        if (verordnung.isDiscontinued()) {
            result += "<s>"; // Abgesetzte
        }
        if (!verordnung.hasMed()) {
            result += verordnung.getMassnahme().getBezeichnung();
        } else {
            // Prüfen, was wirklich im Anbruch gegeben wird. (Wenn das Medikament über die Zeit gegen Generica getauscht wurde.)

            MedInventory inventory = TradeFormTools.getInventory4TradeForm(verordnung.getResident(), verordnung.getTradeForm());
            MedStock aktuellerAnbruch = MedStockTools.getStockInUse(inventory);

            if (aktuellerAnbruch != null) {
                if (!aktuellerAnbruch.getTradeForm().equals(verordnung.getTradeForm())) { // Nur bei Abweichung.
                    result += "<b>" + aktuellerAnbruch.getTradeForm().getMedProdukt().getBezeichnung() +
                            (aktuellerAnbruch.getTradeForm().getZusatz().isEmpty() ? "" : " " + aktuellerAnbruch.getTradeForm().getZusatz()) + "</b>" +
                            (aktuellerAnbruch.getTradeForm().getDosageForm().getZubereitung().isEmpty() ? "" : " " + aktuellerAnbruch.getTradeForm().getDosageForm().getZubereitung()) + " " +
                            (aktuellerAnbruch.getTradeForm().getDosageForm().getAnwText().isEmpty() ? SYSConst.EINHEIT[aktuellerAnbruch.getTradeForm().getDosageForm().getAnwEinheit()] : aktuellerAnbruch.getTradeForm().getDosageForm().getAnwText());
                    result += " <i>(" + OPDE.lang.getString(PnlVerordnung.internalClassID + ".originalprescription") + ": " + verordnung.getTradeForm().getMedProdukt().getBezeichnung();
                    result += (aktuellerAnbruch.getTradeForm().getZusatz().isEmpty() ? "" : " " + aktuellerAnbruch.getTradeForm().getZusatz()) + ")</i>";
                } else {
                    result += "<b>" + verordnung.getTradeForm().getMedProdukt().getBezeichnung()
                            + (aktuellerAnbruch.getTradeForm().getZusatz().isEmpty() ? "" : " " + aktuellerAnbruch.getTradeForm().getZusatz()) + "</b>" +
                            (aktuellerAnbruch.getTradeForm().getDosageForm().getZubereitung().isEmpty() ? "" : " " + aktuellerAnbruch.getTradeForm().getDosageForm().getZubereitung()) + " " +
                            (verordnung.getTradeForm().getDosageForm().getAnwText().isEmpty() ? SYSConst.EINHEIT[verordnung.getTradeForm().getDosageForm().getAnwEinheit()] : verordnung.getTradeForm().getDosageForm().getAnwText());
                }
            } else {
                result += "<b>" + verordnung.getTradeForm().getMedProdukt().getBezeichnung()
                        + (verordnung.getTradeForm().getZusatz().isEmpty() ? "" : " " + verordnung.getTradeForm().getZusatz()) + "</b>" +
                        (verordnung.getTradeForm().getDosageForm().getZubereitung().isEmpty() ? "" : " " + verordnung.getTradeForm().getDosageForm().getZubereitung()) + " " +
                        (verordnung.getTradeForm().getDosageForm().getAnwText().isEmpty() ? SYSConst.EINHEIT[verordnung.getTradeForm().getDosageForm().getAnwEinheit()] : verordnung.getTradeForm().getDosageForm().getAnwText());
            }


        }
        if (verordnung.isDiscontinued()) {
            result += "</s>"; // Abgesetzte
        }

        return result + "</div>";
    }

    public static String getPrescriptionAsShortText(Prescriptions verordnung) {
        String result = "";

        if (verordnung.isDiscontinued()) {
            result += "<s>"; // Abgesetzte
        }
        if (!verordnung.hasMed()) {
            result += verordnung.getMassnahme().getBezeichnung();
        } else {


            result += verordnung.getTradeForm().getMedProdukt().getBezeichnung()
                    + (verordnung.getTradeForm().getZusatz().isEmpty() ? "" : " " + verordnung.getTradeForm().getZusatz());


        }
        if (verordnung.isDiscontinued()) {
            result += "</s>"; // Abgesetzte
        }

        return result;
    }

    public static String getHinweis(Prescriptions verordnung) {
        String result = "<div id=\"fonttext\">";

        if (verordnung.isOnDemand()) {
            result += "<b><u>Nur bei Bedarf:</u> <font color=\"blue\">" + verordnung.getSituation().getText() + "</font></b>";
        }
        if (!verordnung.getBemerkung().isEmpty()) {
            result += result.isEmpty() ? "" : "<br/>";
            result += "<b><u>Bemerkung:</u> </b>" + verordnung.getBemerkung();
        }
        return result + "</div>";
    }

    public static String getAN(Prescriptions verordnung) {
        String result = "<div id=\"fonttext\">";
        String datum = DateFormat.getDateInstance().format(verordnung.getAnDatum());

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

        return result + "</div>";
    }

    public static String getAB(Prescriptions verordnung) {
        String result = "<div id=\"fonttext\">";

        if (verordnung.isBegrenzt()) {
            String datum = DateFormat.getDateInstance().format(verordnung.getAbDatum());

            result += "<font color=\"" + (verordnung.isDiscontinued() ? "red" : "lime") + "\">" + datum + "; ";

            result += verordnung.getAbKH() != null ? verordnung.getAbKH().getName() : "";

            if (verordnung.getAbArzt() != null) {
                if (verordnung.getAbKH() != null) {
                    result += " <i>bestätigt durch:</i> ";
                }
                result += verordnung.getAbArzt().getAnrede() + " " + SYSTools.anonymizeName(verordnung.getAbArzt().getName(), SYSTools.INDEX_NACHNAME);
            }
            result += "; " + verordnung.getAbgesetztDurch().getNameUndVorname() + "</font>";

        }
        return result + "</div>";
    }

    public static String getDosis(Prescriptions prescription) {
        return getDosis(prescription, false);
    }

    public static String getDosis(Prescriptions prescription, boolean mitBestandsAnzeige) {
//        long timestart = System.currentTimeMillis();
        String result = "";
        if (prescription.getPrescriptionSchedule().size() > 1) {
            Collections.sort(prescription.getPrescriptionSchedule());
        }
        Iterator<PrescriptionSchedule> planungen = prescription.getPrescriptionSchedule().iterator();


        if (planungen.hasNext()) {
            PrescriptionSchedule vorherigePlanung = null;
            PrescriptionSchedule planung = null;
            while (planungen.hasNext()) {
                planung = planungen.next();
                result += PrescriptionScheduleTools.getDosisAsHTML(planung, vorherigePlanung, false);
                vorherigePlanung = planung;
            }
            if (PrescriptionScheduleTools.getTerminStatus(planung) != PrescriptionScheduleTools.MAXDOSIS) {
                // Wenn die letzte Planung eine Tabelle benötigte (das tut sie dann, wenn
                // es keine Bedarfsverordnung war), dann müssen wir die Tabelle hier noch
                // schließen.
                result += "</table>";
            }
        } else {
            result += "<i>Noch keine Dosierung / Anwendungsinformationen verfügbar</i><br/>";
        }

        if (mitBestandsAnzeige && prescription.hasMed()) {
            MedInventory inventory = TradeFormTools.getInventory4TradeForm(prescription.getResident(), prescription.getTradeForm());
            MedStock stockInUse = MedStockTools.getStockInUse(inventory);

            if (prescription.isTillEndOfPackage()) {
                result += "nur bis Packungs Ende<br/>";
            }
            if (!prescription.isDiscontinued()) {
                if (stockInUse != null) {
                    EntityManager em = OPDE.createEM();

                    BigDecimal invSum = null;
                    BigDecimal stockSum = null;
                    try {
                        invSum = MedInventoryTools.getInventorySum(em, inventory);
                        stockSum = MedStockTools.getBestandSumme(em, stockInUse);
                    } catch (Exception e) {
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }


                    if (invSum != null && invSum.compareTo(BigDecimal.ZERO) > 0) {
                        result += "<b><u>Vorrat:</u> <font color=\"green\">" + invSum.setScale(2, BigDecimal.ROUND_UP) + " " +
                                SYSConst.EINHEIT[stockInUse.getTradeForm().getDosageForm().getPackEinheit()] +
                                "</font></b>";
                        if (!stockInUse.getTradeForm().getDosageForm().anwUndPackEinheitenGleich()) {

                            BigDecimal anwmenge = invSum.multiply(stockInUse.getApv());

                            result += " <i>entspricht " + anwmenge.setScale(2, BigDecimal.ROUND_UP) + " " +
                                    DosageFormTools.getUsageText(stockInUse.getTradeForm().getDosageForm());
                            result += " (bei einem APV von " + stockInUse.getApv().setScale(2, BigDecimal.ROUND_UP) + " zu 1)";
                            result += "</i>";
                        }

                        result += "<br/>Bestand im Anbruch Nr.: <b><font color=\"green\">" + stockInUse.getBestID() + "</font></b>";

                        if (invSum.compareTo(stockSum) != 0) {
                            result += "<br/>Restmenge im Anbruch: <b><font color=\"green\">" + stockSum.setScale(2, BigDecimal.ROUND_UP) + " " +
                                    SYSConst.EINHEIT[stockInUse.getTradeForm().getDosageForm().getPackEinheit()] + "</font></b>";
                            if (!stockInUse.getTradeForm().getDosageForm().anwUndPackEinheitenGleich()) {
                                BigDecimal usage = stockSum.multiply(stockInUse.getApv());

                                result += " <i>entspricht " + usage.setScale(2, BigDecimal.ROUND_UP) + " " +
                                        DosageFormTools.getUsageText(stockInUse.getTradeForm().getDosageForm()) + "</i>";
                            }
                        }

                    } else {
                        result += "<b><font color=\"red\">Der Vorrat an diesem Medikament ist <u>leer</u>.</font></b>";
                    }
                } else {
                    if (inventory == null) {
                        result += "<b><font color=\"red\">Es gibt bisher keinen Vorrat für dieses Medikament.</font></b>";
                    } else {
                        if (MedInventoryTools.getNextToOpen(inventory) != null) {
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

        return result;
    }

    /**
     * Dieser Query ordnet Verordnungen den Vorräten zu. Dazu ist ein kleiner Trick nötig. Denn über die Zeit können verschiedene Vorräte mit verschiedenen
     * Darreichungen für dieselbe Verordnung verwendet werden. Der Trick ist der Join über zwei Spalten in der Zeile mit "MPBestand"
     */
    public static List<Prescriptions> getPrescriptionsByInventory(MedInventory inventory) {
        EntityManager em = OPDE.createEM();

        List<BigInteger> list = null;
        List<Prescriptions> result = null;
        Query query = em.createNativeQuery(" SELECT DISTINCT ver.VerID FROM BHPVerordnung ver " +
                " INNER JOIN MPVorrat v ON v.BWKennung = ver.BWKennung " + // Verbindung über Bewohner
                " INNER JOIN MPBestand b ON ver.DafID = b.DafID AND v.VorID = b.VorID " + // Verbindung über Bestand zur Darreichung UND dem Vorrat
                " WHERE b.VorID=? AND ver.AbDatum > now() ");
        query.setParameter(1, inventory.getVorID());
        list = query.getResultList();

        if (!list.isEmpty()) {
            result = new ArrayList<Prescriptions>(list.size());
            for (BigInteger verid : list) {
                result.add(em.find(Prescriptions.class, verid.longValue()));
            }
        }
        em.close();

        return result;
    }


    /**
     * Dieser Query ordnet Verordnungen den Vorräten zu. Dazu ist ein kleiner Trick nötig. Denn über die Zeit können verschiedene Vorräte mit verschiedenen
     * Darreichungen für dieselbe Verordnung verwendet werden. Der Trick ist der Join über zwei Spalten in der Zeile mit "MPBestand"
     */
    public static List<Prescriptions> getOnDemandPrescriptions(Resident resident, Date date) {
        EntityManager em = OPDE.createEM();

//        List<Prescriptions> list = null;
        Query query = em.createQuery("SELECT p FROM Prescriptions p WHERE p.resident = :resident AND p.situation IS NOT NULL AND p.anDatum <= :from AND p.abDatum >= :to ORDER BY p.situation.text, p.verid");
        query.setParameter("resident", resident);
        query.setParameter("from", new DateTime(date).toDateMidnight().toDate());
        query.setParameter("to", new DateTime(date).toDateMidnight().plusDays(1).toDateTime().minusSeconds(1).toDate());

        List<Prescriptions> list = query.getResultList();
//        Collections.sort(list);

        em.close();

        return list;
    }

    public static String getPrescriptionAsHTML(Prescriptions prescription, boolean withheader, boolean withlongheader, boolean withmed) {
        ArrayList<Prescriptions> single = new ArrayList<Prescriptions>();
        single.add(prescription);
        return getPrescriptionAsHTML(single, withheader, withlongheader, withmed);
    }

    /**
     * Gibt eine HTML Darstellung der Verordungen zurück, die in dem übergebenen TableModel enthalten sind.
     */
    public static String getPrescriptionAsHTML(List<Prescriptions> list, boolean withheader, boolean withlongheader, boolean withmed) {
        String result = "";

        if (!list.isEmpty()) {
            Prescriptions verordnung = list.get(0);
            result += withheader ? "<h2 id=\"fonth2\" >" + OPDE.lang.getString("nursingrecords.prescription") + (withlongheader ? " für " + ResidentTools.getLabelText(verordnung.getResident()) : "") + "</h2>" : "";

//            if (verordnung.getResident().getStation() != null) {
//                result += EinrichtungenTools.getAsText(verordnung.getResident().getStation().getEinrichtung());
//            }

            result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>" +
                    "<th >Medikament/Massnahme</th><th >Dosierung / Hinweise</th><th >Angesetzt</th></tr>";

            Iterator<Prescriptions> itVerordnung = list.iterator();
            while (itVerordnung.hasNext()) {
                verordnung = itVerordnung.next();

                result += "<tr>";
                result += "<td valign=\"top\">" + getPrescriptionAsText(verordnung) + "</td>";
                result += "<td valign=\"top\">" + getDosis(verordnung, withmed) + "<br/>";
                result += getHinweis(verordnung) + "</td>";
                result += "<td valign=\"top\">" + getAN(verordnung) + "</td>";
                //result += "<td>" + SYSTools.unHTML2(tmv.getValueAt(v, TMVerordnung.COL_AB).toString()) + "</td>";
                result += "</tr>";
            }

            result += "</table>";
        } else {
            result += "<h2  id=\"fonth2\" >" + OPDE.lang.getString("nursingrecords.prescription") + "</h2><i>" + OPDE.lang.getString("misc.msg.currentlynoentry") + "</i>";
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
    public static int getNumVerodnungenMitGleicherKennung(Prescriptions verordnung) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Verordnung.findByVerKennung");
        query.setParameter("verKennung", verordnung.getVerKennung());
        int num = query.getResultList().size();
        em.close();
        return num;
    }

    public static String toPrettyString(Prescriptions verordnung) {
        String myPretty = "";

        if (verordnung.hasMed()) {
            myPretty = TradeFormTools.toPrettyString(verordnung.getTradeForm());
        } else {
            myPretty = verordnung.getMassnahme().getBezeichnung();
        }

        myPretty += verordnung.isOnDemand() ? " (Nur bei Bedarf: " + verordnung.getSituation().getText() + ")" : "";

        return myPretty;
    }

    public static void absetzen(EntityManager em, Prescriptions verordnung) throws Exception {
        verordnung = em.merge(verordnung);
        em.lock(verordnung, LockModeType.OPTIMISTIC);
        verordnung.setAbDatum(new Date());
        verordnung.setAbgesetztDurch(em.merge(OPDE.getLogin().getUser()));
        BHPTools.cleanup(em, verordnung);
    }

    public static void alleAbsetzen(EntityManager em, Resident resident) throws Exception {
        Query query = em.createQuery("SELECT b FROM Prescriptions b WHERE b.resident = :resident AND b.abDatum >= :now");
        query.setParameter("resident", resident);
        query.setParameter("now", new Date());
        List<Prescriptions> verordnungen = query.getResultList();

        for (Prescriptions verordnung : verordnungen) {
            absetzen(em, verordnung);
        }
    }

}
