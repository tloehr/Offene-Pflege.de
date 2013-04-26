/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.prescription;

import entity.Station;
import entity.files.SYSFilesTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.care.prescription.PnlPrescription;
import op.threads.DisplayMessage;
import op.tools.HTMLTools;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.swing.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author tloehr
 */
public class PrescriptionTools {

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
     * Beim Firefox (about:settings) sollten die Ränder wie folgt eingestellt werden:
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
     * @param station Die Station, für die der Stellplan erstellt werden soll. Sortiert nach den Station.
     */
    public static void printDailyPlan(Station station) {
        EntityManager em = OPDE.createEM();
        try {
            Query query = em.createNativeQuery("" +
                    " SELECT v.VerID, bhp.BHPPID, best.BestID, vor.VorID, F.FormID, M.MedPID, M.Bezeichnung, Ms.Bezeichnung " +
                    " FROM prescription v " +
                    " INNER JOIN resident bw ON v.BWKennung = bw.BWKennung  " +
                    " INNER JOIN intervention Ms ON Ms.MassID = v.MassID " +
                    " INNER JOIN pschedule bhp ON bhp.VerID = v.VerID " +
                    " LEFT OUTER JOIN tradeform D ON v.DafID = D.DafID " +
                    " LEFT OUTER JOIN medproducts M ON M.MedPID = D.MedPID " +
                    " LEFT OUTER JOIN dosageform F ON D.FormID = F.FormID " +
                    " LEFT OUTER JOIN ( " +
                    "      SELECT DISTINCT M.VorID, M.BWKennung, B.DafID FROM medinventory M  " +
                    "      INNER JOIN medstock B ON M.VorID = B.VorID " +
                    "      WHERE M.Bis = '9999-12-31 23:59:59' " +
                    " ) vorr ON vorr.DafID = v.DafID AND vorr.BWKennung = v.BWKennung " +
                    " LEFT OUTER JOIN medinventory vor ON vor.VorID = vorr.VorID " +
                    " LEFT OUTER JOIN ( " +
                    "      SELECT stock.BestID, stock.VorID FROM medstock stock " +
                    "      WHERE stock.Aus = '9999-12-31 23:59:59' AND stock.Anbruch < '9999-12-31 23:59:59' " +
                    ") best ON best.VorID = vor.VorID " +
                    " WHERE bw.adminonly <> 2 " +
                    " AND v.AnDatum < now() AND v.AbDatum > now() AND Date(bhp.LDatum) <= Date(now()) AND v.SitID IS NULL AND (v.DafID IS NOT NULL OR v.Stellplan IS TRUE) " +
                    " AND bw.StatID = ? " +
                    " ORDER BY CONCAT(bw.nachname,bw.vorname), bw.BWKennung, v.DafID IS NOT NULL, bhp.Uhrzeit, F.Stellplan, CONCAT( M.Bezeichnung, Ms.Bezeichnung)");
            query.setParameter(1, station.getStatID());
            printDailyPlan(station, query.getResultList());
            em.close();
        } catch (Exception e) {
            OPDE.fatal(e);
        }
    }

    private static void printDailyPlan(final Station station, final List data) {

        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

        final EntityManager em = OPDE.createEM();

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                int progress = -1;
                int DAILYPLAN_PAGEBREAK_AFTER_ELEMENT_NO = Integer.parseInt(OPDE.getProps().getProperty(SYSPropsTools.KEY_DAILYPLAN_PAGEBREAK));
                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, data.size()));
                String resID = "";
                int elementNumber = 1;
                boolean pagebreak = false;
                String header = OPDE.lang.getString("nursingrecords.prescription.dailyplan.header1") + " " + DateFormat.getDateInstance().format(new Date());
                String html = SYSConst.html_h1(header + " (" + station.getName() + ")");
                html += "<div align=\"center\" id=\"fonttext\" >" + OPDE.lang.getString("nursingrecords.prescription.dailyplan.warning") + "</div>\n";

                for (Object obj : data) {
                    progress++;

                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, data.size()));

                    Object[] objects = (Object[]) obj;
                    Prescription prescription = em.find(Prescription.class, ((BigInteger) objects[0]).longValue());
                    PrescriptionSchedule schedule = em.find(PrescriptionSchedule.class, ((BigInteger) objects[1]).longValue());
                    BigInteger bestid = (BigInteger) objects[2];
                    BigInteger formid = (BigInteger) objects[4];

                    // Alle Formen, die nicht abzählbar sind, werden grau hinterlegt. Also Tropfen, Spritzen etc.
                    boolean gray = false;
                    if (formid != null) {
                        DosageForm form = em.find(DosageForm.class, formid.longValue());
                        gray = form.getDailyPlan() > 0;
                    }

                    // Wenn der Bewohnername sich in der Liste ändert, muss
                    // einmal die Überschrift drüber gesetzt werden.
                    boolean bewohnerWechsel = !resID.equalsIgnoreCase(prescription.getResident().getRID());

                    if (pagebreak || bewohnerWechsel) {
                        // Falls zufällig ein weiterer Header (der 2 Elemente hoch ist) einen Pagebreak auslösen WÜRDE
                        // müssen wir hier schonmal vorsorglich den Seitenumbruch machen.
                        // 2 Zeilen rechne ich nochdrauf, damit die Tabelle mindestens 2 Zeilen hat, bevor der Seitenumbruch kommt.
                        // Das kann dann passieren, wenn dieser if Konstrukt aufgrund eines BW Wechsels durchlaufen wird.
                        pagebreak = (elementNumber + 2 + 2) > DAILYPLAN_PAGEBREAK_AFTER_ELEMENT_NO;

                        // Außer beim ersten mal und beim Pagebreak, muss dabei die vorherige Tabelle abgeschlossen werden.
                        if (pagebreak || !resID.equals("")) {
                            html += "</table>";
                        }

                        resID = prescription.getResident().getRID();

                        html += "<h2 id=\"fonth2\" " +
                                (pagebreak ? "style=\"page-break-before:always\">" : ">") +
                                ((pagebreak && !bewohnerWechsel) ? "<i>(fortgesetzt)</i> " : "")
                                + ResidentTools.getLabelText(prescription.getResident())
                                + "</h2>\n";
                        html += "<table id=\"font14\" border=\"1\" cellspacing=\"0\">";
                        html += SYSConst.html_table_tr(
                                SYSConst.html_table_th("nursingrecords.prescription.dailyplan.table.col1")
                                        + SYSConst.html_table_th("misc.msg.earlyinthemorning.short")
                                        + SYSConst.html_table_th("misc.msg.morning.short")
                                        + SYSConst.html_table_th("misc.msg.noon.short")
                                        + SYSConst.html_table_th("misc.msg.afternoon.short")
                                        + SYSConst.html_table_th("misc.msg.evening.short")
                                        + SYSConst.html_table_th("misc.msg.lateatnight.short")
                                        + SYSConst.html_table_th("misc.msg.comment")
                        );
                        elementNumber += 2;

                        if (pagebreak) {
                            elementNumber = 1;
                            pagebreak = false;
                        }
                    }

                    html += "<tr style=\"page-break-before:avoid\" " + (gray ? "id=\"fonttextgray14\">" : ">\n");
                    html += "<td width=\"300\" valign=\"top\">" + getShortDescription(prescription);   // (verordnung.hasMed() ? "<b>" + TradeFormTools.toPrettyString(verordnung.getTradeForm()) + "</b>" : verordnung.getIntervention().getName())
                    html += (bestid != null ? "<br/><i>" + OPDE.lang.getString("nursingrecords.prescription.dailyplan.stockInUse") + " " + OPDE.lang.getString("misc.msg.number") + " " + bestid + "</i>" : "") + "</td>\n";

                    if (schedule.usesTime()) {
                        html += "<td colspan=\"6\" align=\"center\">";

                        html += "<b><u>" + DateFormat.getTimeInstance(DateFormat.SHORT).format(schedule.getUhrzeit()) + " " + OPDE.lang.getString("misc.msg.Time.short") + "</u></b> ";
                        html += HTMLTools.printDouble(schedule.getUhrzeitDosis());
                        html += schedule.getPrescription().hasMed() ? " " + SYSConst.UNITS[schedule.getPrescription().getTradeForm().getDosageForm().getUsageUnit()] : "x";

                        html += "</td>\n";
                    } else {

                        html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(schedule.getNachtMo()) + "</td>\n";
                        html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(schedule.getMorgens()) + "</td>\n";
                        html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(schedule.getMittags()) + "</td>\n";
                        html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(schedule.getNachmittags()) + "</td>\n";
                        html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(schedule.getAbends()) + "</td>\n";
                        html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(schedule.getNachtAb()) + "</td>\n";
                    }
                    html += "<td width=\"300\" >" + PrescriptionScheduleTools.getRemark(schedule);
                    if (prescription.getTo().before(SYSConst.DATE_UNTIL_FURTHER_NOTICE)) {
                        html += "<br/><b>" + OPDE.lang.getString(PnlPrescription.internalClassID + ".endsAtChrono") + ": " + DateFormat.getDateInstance().format(prescription.getTo()) + "</b>";
                    }
                    html += "</td>\n";
                    html += "</tr>\n\n";
                    elementNumber += 1;

                    pagebreak = elementNumber > DAILYPLAN_PAGEBREAK_AFTER_ELEMENT_NO;
                }
                return html + "</table>";
            }

            @Override
            protected void done() {
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);

                try {
                    SYSFilesTools.print(get().toString(), true);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (ExecutionException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                em.close();
            }
        };
        worker.execute();

//        return result;
    }

    public static String getShortDescription(Prescription prescription) {
        String result = "";

        if (!prescription.hasMed()) {
            result += prescription.getIntervention().getBezeichnung();
        } else {
            MedInventory inventory = TradeFormTools.getInventory4TradeForm(prescription.getResident(), prescription.getTradeForm());
            MedStock stockInUse = prescription.isClosed() ? null : MedStockTools.getStockInUse(inventory);

            if (stockInUse != null) {
                result += "<b>" + stockInUse.getTradeForm().getMedProduct().getText()
                        + (stockInUse.getTradeForm().getSubtext().isEmpty() ? "" : " " + stockInUse.getTradeForm().getSubtext()) + "</b>" +
                        (stockInUse.getTradeForm().getDosageForm().getPreparation().isEmpty() ? "" : " " + stockInUse.getTradeForm().getDosageForm().getPreparation()) + " " +
                        (prescription.getTradeForm().getDosageForm().getUsageText().isEmpty() ? SYSConst.UNITS[prescription.getTradeForm().getDosageForm().getUsageUnit()] : prescription.getTradeForm().getDosageForm().getUsageText());

            } else {
                result += "<b>" + prescription.getTradeForm().getMedProduct().getText()
                        + (prescription.getTradeForm().getSubtext().isEmpty() ? "" : " " + prescription.getTradeForm().getSubtext()) + "</b>" +
                        (prescription.getTradeForm().getDosageForm().getPreparation().isEmpty() ? "" : " " + prescription.getTradeForm().getDosageForm().getPreparation()) + " " +
                        (prescription.getTradeForm().getDosageForm().getUsageText().isEmpty() ? SYSConst.UNITS[prescription.getTradeForm().getDosageForm().getUsageUnit()] : prescription.getTradeForm().getDosageForm().getUsageText());
            }
        }

        //result += "</font>";

        return result;
    }

    public static String getOriginalPrescription(Prescription presription) {
        String result = "";

        if (presription.shouldBeCalculated()) {

            MedInventory inventory = TradeFormTools.getInventory4TradeForm(presription.getResident(), presription.getTradeForm());
            MedStock stockInUse = MedStockTools.getStockInUse(inventory);

            if (stockInUse != null) {
                // If the current prescription defers from the original one (different provider of the medication as in the beginning)
                if (!stockInUse.getTradeForm().equals(presription.getTradeForm())) {
                    result = TradeFormTools.toPrettyHTMLalternative(presription.getTradeForm());
                }
            }
        }
        return (result.isEmpty() ? "" : result + "<br/>");
    }

    public static String getLongDescription(Prescription presription) {
        String result = "<div id=\"fonttext\">";// = SYSConst.html_fontface;

        if (!presription.hasMed()) {
            result += presription.getIntervention().getBezeichnung();
        } else {

            MedInventory inventory = TradeFormTools.getInventory4TradeForm(presription.getResident(), presription.getTradeForm());
            MedStock stockInUse = MedStockTools.getStockInUse(inventory);

            if (stockInUse != null) {
                // If the current prescription defers from the original one (different provider of the medication as in the beginning)
                if (!stockInUse.getTradeForm().equals(presription.getTradeForm())) {

                    result = TradeFormTools.toPrettyHTML(stockInUse.getTradeForm()) + TradeFormTools.toPrettyHTMLalternative(presription.getTradeForm());

//                    result += "<b>" + stockInUse.getTradeForm().getMedProduct().getName() +
//                            (stockInUse.getTradeForm().getSubtext().isEmpty() ? "" : " " + stockInUse.getTradeForm().getSubtext()) + "</b>" +
//                            (stockInUse.getTradeForm().getDosageForm().getPreparation().isEmpty() ? "" : " " + stockInUse.getTradeForm().getDosageForm().getPreparation()) + " " +
//                            (stockInUse.getTradeForm().getDosageForm().getUsageText().isEmpty() ? SYSConst.UNITS[stockInUse.getTradeForm().getDosageForm().getUsageUnit()] : stockInUse.getTradeForm().getDosageForm().getUsageText());
//                    result += " <i>(" + OPDE.lang.getString(PnlPrescription.internalClassID + ".originalprescription") + ": " + presription.getTradeForm().getMedProduct().getName();
//                    result += (stockInUse.getTradeForm().getSubtext().isEmpty() ? "" : " " + stockInUse.getTradeForm().getSubtext()) + ")</i>";
                } else {

                    result = TradeFormTools.toPrettyHTML(presription.getTradeForm());

                    // No, the resident still gets the orginal stuff
//                    result += "<b>" + presription.getTradeForm().getMedProduct().getName()
//                            + (stockInUse.getTradeForm().getSubtext().isEmpty() ? "" : " " + stockInUse.getTradeForm().getSubtext()) + "</b>" +
//                            (stockInUse.getTradeForm().getDosageForm().getPreparation().isEmpty() ? "" : " " + stockInUse.getTradeForm().getDosageForm().getPreparation()) + " " +
//                            (presription.getTradeForm().getDosageForm().getUsageText().isEmpty() ? SYSConst.UNITS[presription.getTradeForm().getDosageForm().getUsageUnit()] : presription.getTradeForm().getDosageForm().getUsageText());
                }
            } else {
                result = TradeFormTools.toPrettyHTML(presription.getTradeForm());
            }


        }
        return result + "</div>";
    }

    public static String getPrescriptionAsShortText(Prescription verordnung) {
        String result = "";

//        if (verordnung.isClosed()) {
//            result += "<s>"; // Abgesetzte
//        }
        if (!verordnung.hasMed()) {
            result += verordnung.getIntervention().getBezeichnung();
        } else {


            result += verordnung.getTradeForm().getMedProduct().getText()
                    + (verordnung.getTradeForm().getSubtext().isEmpty() ? "" : " " + verordnung.getTradeForm().getSubtext());


        }
//        if (verordnung.isClosed()) {
//            result += "</s>"; // Abgesetzte
//        }

        return result;
    }

    public static String getRemark(Prescription verordnung) {
        String result = "<div id=\"fonttext\">";

        if (verordnung.isOnDemand()) {
            result += "<b><u>" + OPDE.lang.getString("misc.msg.ondemand") + ":</u> <font color=\"blue\">" + verordnung.getSituation().getText() + "</font></b>";
        }
        if (!verordnung.getText().isEmpty()) {
            result += result.isEmpty() ? "" : "<br/>";
            result += "<b><u>" + OPDE.lang.getString("misc.msg.comment") + ":</u> </b>" + verordnung.getText();
        }
        return result + "</div>";
    }

    public static String getON(Prescription verordnung) {
        String result = "<div id=\"fonttext\">";
        String datum = DateFormat.getDateInstance().format(verordnung.getFrom());

        result += "<font color=\"green\">" + datum + "; ";
        if (verordnung.getHospitalON() != null) {
            result += verordnung.getHospitalON().getName();
        }
        if (verordnung.getDocON() != null) {
            if (verordnung.getHospitalON() != null) {
                result += " <i>" + OPDE.lang.getString("misc.msg.confirmedby") + ":</i> ";
            }
            result += verordnung.getDocON().getAnrede() + " " + SYSTools.anonymizeName(verordnung.getDocON().getName(), SYSTools.INDEX_LASTNAME);
        }
        result += "; " + verordnung.getUserON().getFullname() + "</font>";

        return result + "</div>";
    }

    public static String getOFF(Prescription verordnung) {
        String result = "<div id=\"fonttext\">";

        if (verordnung.isLimited()) {
            String datum = DateFormat.getDateInstance().format(verordnung.getTo());

            result += "<font color=\"" + (verordnung.isClosed() ? "red" : "lime") + "\">" + datum + "; ";

            result += verordnung.getHospitalOFF() != null ? verordnung.getHospitalOFF().getName() : "";

            if (verordnung.getDocOFF() != null) {
                if (verordnung.getHospitalOFF() != null) {
                    result += " <i>" + OPDE.lang.getString("misc.msg.confirmedby") + ":</i> ";
                }
                result += verordnung.getDocOFF().getAnrede() + " " + SYSTools.anonymizeName(verordnung.getDocOFF().getName(), SYSTools.INDEX_LASTNAME);
            }
            result += "; " + verordnung.getUserOFF().getFullname() + "</font>";

        }
        return result + "</div>";
    }

    public static String getDose(Prescription prescription) {
        return getDose(prescription, false);
    }

    public static String getDose(Prescription prescription, boolean showInventory) {
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
                result += PrescriptionScheduleTools.getDoseAsHTML(planung, vorherigePlanung, false);
                vorherigePlanung = planung;
            }
            if (PrescriptionScheduleTools.getTerminStatus(planung) != PrescriptionScheduleTools.MAXDOSIS) {
                // Wenn die letzte Planung eine Tabelle benötigte (das tut sie dann, wenn
                // es keine Bedarfsverordnung war), dann müssen wir die Tabelle hier noch
                // schließen.
                result += "</table>";
            }
        } else {
            result += "<i>" + OPDE.lang.getString(PnlPrescription.internalClassID + ".noDosageYet") + "</i><br/>";
        }

        result += showInventory ? getInventoryInformationAsHTML(prescription) : "";

        return result;
    }


    public static String getInventoryInformationAsHTML(final Prescription prescription) {
        String result = "";
        if (!prescription.isClosed() && prescription.shouldBeCalculated()) {
            MedInventory inventory = TradeFormTools.getInventory4TradeForm(prescription.getResident(), prescription.getTradeForm());
            MedStock stockInUse = MedStockTools.getStockInUse(inventory);

            if (prescription.isUntilEndOfPackage()) {
                result += "<b>" + OPDE.lang.getString("misc.msg.onlyUntilEndOfPackage") + "</b><br/>";
            }
            if (!prescription.isClosed()) {
                if (stockInUse != null) {
                    EntityManager em = OPDE.createEM();

                    BigDecimal invSum = null;
                    BigDecimal stockSum = null;
                    try {
                        invSum = MedInventoryTools.getSum(em, inventory);
                        stockSum = MedStockTools.getSum(em, stockInUse);
                    } catch (Exception e) {
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }

                    if (invSum != null && invSum.compareTo(BigDecimal.ZERO) > 0) {
                        result += "<b><u>" + OPDE.lang.getString("misc.msg.inventory") + ":</u> <font color=\"green\">" + invSum.setScale(2, BigDecimal.ROUND_UP) + " " +
                                SYSConst.UNITS[stockInUse.getTradeForm().getDosageForm().getPackUnit()] +
                                "</font></b>";
                        if (stockInUse.getTradeForm().getDosageForm().isUPRn()) {
                            BigDecimal anwmenge = invSum.multiply(stockInUse.getUPR());

                            result += " " + OPDE.lang.getString("misc.msg.equalTo") + " " + anwmenge.setScale(2, BigDecimal.ROUND_UP) + " " +
                                    DosageFormTools.getUsageText(stockInUse.getTradeForm().getDosageForm());
                            result += " (" + OPDE.lang.getString("misc.msg.upr") + ": " + stockInUse.getUPR().setScale(2, BigDecimal.ROUND_UP) + " " + OPDE.lang.getString("misc.msg.to1");
                            if (stockInUse.getUPRDummyMode() == MedStockTools.REPLACE_WITH_EFFECTIVE_UPR_WHEN_CLOSING) {
                                result += ", " + OPDE.lang.getString("misc.msg.preliminary");
                            }
                            result += ")";
                        }

                        result += "<br/>" + OPDE.lang.getString("misc.msg.stockInUse") + ": <b><font color=\"green\">" + stockInUse.getID() + "</font></b>";

                        if (invSum.compareTo(stockSum) != 0) {
                            result += "<br/>" + OPDE.lang.getString("misc.msg.leftInStock") + ": <b><font color=\"green\">" + stockSum.setScale(2, BigDecimal.ROUND_UP) + " " +
                                    SYSConst.UNITS[stockInUse.getTradeForm().getDosageForm().getPackUnit()] + "</font></b>";
                            if (stockInUse.getTradeForm().getDosageForm().isUPRn()) {
                                BigDecimal usage = stockSum.multiply(stockInUse.getUPR());

                                result += " (" + OPDE.lang.getString("misc.msg.equalTo") + " " + usage.setScale(2, BigDecimal.ROUND_UP) + " " +
                                        DosageFormTools.getUsageText(stockInUse.getTradeForm().getDosageForm()) + ")";
                            }
                        }

                        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                        // variable expiry ?
                        if (stockInUse.getTradeForm().getDaysToExpireAfterOpened() != null) {
                            String color = stockInUse.isExpired() ? "red" : "black";
                            result += "<br/><font color=\"" + color + "\">" + OPDE.lang.getString("misc.msg.expiresAfterOpened") + ": " + df.format(new DateTime(stockInUse.getOpened()).plusDays(stockInUse.getTradeForm().getDaysToExpireAfterOpened()).toDate()) + "</font>";
                        }

                        // fixed expiry ?
                        if (stockInUse.getExpires() != null) {
                            String color = stockInUse.isExpired() ? "red" : "black";
                            result += "<br/><font color=\"" + color + "\">" + OPDE.lang.getString("misc.msg.expires") + ": " + new SimpleDateFormat("MM/yy").format(stockInUse.getExpires()) + "</font>";
                        }


                    } else {
                        result += "<b><font color=\"red\">" + OPDE.lang.getString("misc.msg.emptyInventory") + "</font></b>";
                    }
                } else {
                    if (inventory == null) {
                        result += "<b><font color=\"red\">" + OPDE.lang.getString("misc.msg.noInventoryYet") + "</font></b>";
                    } else {
                        if (MedInventoryTools.getNextToOpen(inventory) != null) {
                            result += "<br/><b><font color=\"red\">" + OPDE.lang.getString("misc.msg.noOpenStock") + "</font></b>";
                        } else {
                            result += "<br/><b><font color=\"red\">" + OPDE.lang.getString("misc.msg.noOpenStock") + "</font></b>";
                        }

                    }
                }
            }

        }
        return result;
    }

    /**
     * Dieser Query ordnet Verordnungen den Vorräten zu. Dazu ist ein kleiner Trick nötig. Denn über die Zeit können verschiedene Vorräte mit verschiedenen
     * Darreichungen für dieselbe Verordnung verwendet werden. Der Trick ist der Join über zwei Spalten in der Zeile mit "MPBestand"
     */
    public static List<Prescription> getPrescriptionsByInventory(MedInventory inventory) {
        long begin = System.currentTimeMillis();
        EntityManager em = OPDE.createEM();

        List<BigInteger> list;
        List<Prescription> result = new ArrayList<Prescription>();

        Query query = em.createNativeQuery(" SELECT DISTINCT ver.VerID FROM prescription ver " +
                " INNER JOIN medinventory v ON v.BWKennung = ver.BWKennung " + // Verbindung über Bewohner
                " INNER JOIN medstock b ON ver.DafID = b.DafID AND v.VorID = b.VorID " + // Verbindung über Bestand zur Darreichung UND dem Vorrat
                " WHERE b.VorID=? AND ver.AbDatum > now() ");
        query.setParameter(1, inventory.getID());
        list = query.getResultList();

        for (BigInteger verid : list) {
            result.add(em.find(Prescription.class, verid.longValue()));
        }

        em.close();
        SYSTools.showTimeDifference(begin);
        return result;
    }

    public static ArrayList<Prescription> getAll(Resident resident) {
        EntityManager em = OPDE.createEM();

        ArrayList<Prescription> result = null;
        Query query = em.createQuery(" SELECT p FROM Prescription p WHERE p.resident = :resident ");
        query.setParameter("resident", resident);
        result = new ArrayList<Prescription>(query.getResultList());

        em.close();
        return result;
    }

    public static ArrayList<Prescription> getAllActive(Resident resident) {
        EntityManager em = OPDE.createEM();

        ArrayList<Prescription> result = null;
        Query query = em.createQuery(" SELECT p FROM Prescription p WHERE p.resident = :resident AND p.to >= :now");
        query.setParameter("resident", resident);
        query.setParameter("now", new Date());
//        query.setParameter("tfn", SYSConst.DATE_UNTIL_FURTHER_NOTICE);
        result = new ArrayList<Prescription>(query.getResultList());

        em.close();
        return result;
    }


    /**
     * Dieser Query ordnet Verordnungen den Vorräten zu. Dazu ist ein kleiner Trick nötig. Denn über die Zeit können verschiedene Vorräte mit verschiedenen
     * Darreichungen für dieselbe Verordnung verwendet werden. Der Trick ist der Join über zwei Spalten in der Zeile mit "MPBestand"
     */
    public static List<Prescription> getOnDemandPrescriptions(Resident resident, Date date) {
        EntityManager em = OPDE.createEM();

        Query query = em.createQuery("SELECT p FROM Prescription p WHERE p.resident = :resident AND p.situation IS NOT NULL AND p.from <= :from AND p.to >= :to ORDER BY p.situation.text, p.id");
        query.setParameter("resident", resident);
        query.setParameter("from", new DateTime(date).toDateMidnight().toDate());
        query.setParameter("to", new DateTime(date).toDateMidnight().plusDays(1).toDateTime().minusSeconds(1).toDate());

        List<Prescription> list = query.getResultList();

        em.close();
        return list;
    }

    public static String getPrescriptionAsHTML(Prescription prescription, boolean withheader, boolean withlongheader, boolean withmed, boolean withIcon) {
        ArrayList<Prescription> single = new ArrayList<Prescription>();
        single.add(prescription);
        return getPrescriptionsAsHTML(single, withheader, withlongheader, withmed, true, withIcon);
    }

    public static String getPrescriptionsAsHTML(List<Prescription> list, boolean withheader, boolean withlongheader, boolean withmed, boolean withDiscontinued, boolean withIcon) {
        String result = "";

        if (!list.isEmpty()) {
            Prescription prescription = list.get(0);
            result += withheader ? "<h2 id=\"fonth2\" >" + OPDE.lang.getString("nursingrecords.prescription") + (withlongheader ? " für " + ResidentTools.getLabelText(prescription.getResident()) : "") + "</h2>" : "";

            result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>" +
                    "<th>Medikament/Massnahme</th><th>Dosierung / Hinweise</th><th>Angesetzt</th></tr>";

            for (Prescription myprescription : list) {

                if (withDiscontinued || !myprescription.isClosed()) {

                    result += "<tr>";
                    result += "<td valign=\"top\">" + (withIcon && myprescription.isClosed() ? SYSConst.html_22x22_StopSign : "") + getLongDescription(myprescription) + "</td>";
                    result += "<td valign=\"top\">" + getDose(myprescription, withmed) + "<br/>";
                    result += getRemark(myprescription) + "</td>";
                    result += "<td valign=\"top\">" + myprescription.getPITAsHTML();

                    result += "</td>";
                    result += "</tr>";
                }
            }

            result += "</table>";
        } else {
            result += "<h2  id=\"fonth2\" >" + OPDE.lang.getString("nursingrecords.prescription") + "</h2><i>" + OPDE.lang.getString("misc.msg.currentlynoentry") + "</i>";
        }
        return result;
    }

//    /**
//     * Ermittelt die Anzahl der Verordnungen, die zu dieser Verordnung gemäß der VerordnungKennung gehören.
//     * Verordnung, die über die Zeit mehrfach geändert werden, hängen über die VerordnungsKennung aneinander.
//     *
//     * @param verordnung
//     * @return Anzahl der Verordnungen, die zu dieser gehören.e
//     */
//    public static int getNumVerodnungenMitGleicherKennung(Prescription verordnung) {
//        EntityManager em = OPDE.createEM();
//        Query query = em.createQuery("SELECT b FROM Prescription b WHERE b.prescRelation = :verKennung");
//        query.setParameter("verKennung", verordnung.getRelation());
//        int num = query.getResultList().size();
//        em.close();
//        return num;
//    }

    public static String toPrettyString(Prescription verordnung) {
        String myPretty = "";

        if (verordnung.hasMed()) {
            myPretty = TradeFormTools.toPrettyString(verordnung.getTradeForm());
        } else {
            myPretty = verordnung.getIntervention().getBezeichnung();
        }

        myPretty += verordnung.isOnDemand() ? " (" + OPDE.lang.getString("misc.msg.ondemand") + ": " + verordnung.getSituation().getText() + ")" : "";

        return myPretty;
    }

//    public static void absetzen(EntityManager em, Prescription verordnung) throws Exception {
//        verordnung = em.merge(verordnung);
//        em.lock(verordnung, LockModeType.OPTIMISTIC);
//        verordnung.setTo(new Date());
//        verordnung.setUserOFF(em.merge(OPDE.getLogin().getUser()));
//        BHPTools.cleanup(em, verordnung);
//    }

    public static void closeAll(EntityManager em, Resident resident, Date enddate) throws Exception {
        Query query = em.createQuery("SELECT b FROM Prescription b WHERE b.resident = :resident AND b.to >= :now");
        query.setParameter("resident", resident);
        query.setParameter("now", enddate);
        List<Prescription> verordnungen = query.getResultList();

        for (Prescription verordnung : verordnungen) {
            Prescription myp = em.merge(verordnung);
            em.lock(myp, LockModeType.OPTIMISTIC);
            myp.setTo(enddate);
            myp.setUserOFF(em.merge(OPDE.getLogin().getUser()));
//            BHPTools.cleanup(em, myp);
        }
    }

}
