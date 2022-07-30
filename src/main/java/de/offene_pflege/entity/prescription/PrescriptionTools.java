/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.entity.prescription;

import com.itextpdf.text.Font;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import de.offene_pflege.entity.building.Station;
import de.offene_pflege.entity.files.SYSFilesTools;
import de.offene_pflege.entity.info.ResInfo;
import de.offene_pflege.entity.info.ResInfoTools;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.entity.system.Commontags;
import de.offene_pflege.entity.system.CommontagsTools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.system.PDF;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.*;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.text.html.HTML;
import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author tloehr
 */
@Log4j2
public class PrescriptionTools {

    /**
     * Diese Methode erzeugt einen Stellplan für den aktuellen Tag im HTML Format. Eine Besonderheit bei der
     * Implementierung muss ich hier erläutern. Aufgrund der ungleichen HTML Standards (insbesonders der
     * Druckdarstellung im CSS2.0 und später auch CSS2.1) muss ich hier einen Trick anwenden, damit das auf
     * verschiedenen Browsern halbwegs gleich aussieht.
     * <p>
     * Daher addiere ich jedes größere Element auf einer Seite (also Header, Tabellen Zeilen) mit dem Wert 1. Nach einer
     * bestimmten Anzahl von Elementen erzwinge ich einen Pagebreak.
     * <p>
     * Nach einem Pagebreak wird der Name des aktuellen Bewohner nocheinmal wiederholt.
     * <p>
     * Ein Mac OS Safari druckt mit diesen Werten sehr gut. Beim Firefox (about:settings) sollten die Ränder wie folgt
     * eingestellt werden:
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
                    " SELECT v.VerID, bhp.BHPPID, best.BestID, vor.VorID, F.FormID, M.MedPID, M.Text, Ms.Bezeichnung " +
                    " FROM prescription v " +
                    " INNER JOIN resident bw ON v.BWKennung = bw.id  " +
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
                    " ORDER BY CONCAT(bw.nachname,bw.vorname), bw.id, v.DafID IS NOT NULL, bhp.Uhrzeit, F.Stellplan, CONCAT( M.Text, Ms.Bezeichnung)");
            query.setParameter(1, station.getId());

            printDailyPlanAsPDF(station, query.getResultList());


            em.close();
        } catch (Exception e) {
            OPDE.fatal(e);
        }
    }


    private static void printDailyPlanAsPDF(final Station station, final List data) throws Exception {

        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

        final Font whiteFont = PDF.bold();
        whiteFont.setColor(BaseColor.WHITE);
        final EntityManager em = OPDE.createEM();

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                String header = SYSTools.xx("nursingrecords.prescription.dailyplan.header1") + " " + DateFormat.getDateInstance().format(new Date()) + " (" + station.getName() + ")";
                final PDF pdf = new PDF(null, header, 10);
                pdf.getDocument().add(new Header(OPDE.getAppInfo().getSignature(), header));

                Paragraph h1 = new Paragraph(new Phrase(header, PDF.plain(PDF.sizeH1())));
                h1.setAlignment(Element.ALIGN_CENTER);
                pdf.getDocument().add(h1);
//                pdf.getDocument().add(Chunk.NEWLINE);

                Paragraph p = new Paragraph(SYSTools.xx("nursingrecords.prescription.dailyplan.warning"));
                p.setAlignment(Element.ALIGN_CENTER);
                pdf.getDocument().add(p);
                pdf.getDocument().add(Chunk.NEWLINE);

                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                int progress = -1;
                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, data.size()));
                String resID = "";
                PdfPTable table = null;

                for (Object obj : data) {
                    progress++;

                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, data.size()));

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

                    /***
                     *      _                    _
                     *     | |__   ___  __ _  __| |
                     *     | '_ \ / _ \/ _` |/ _` |
                     *     | | | |  __/ (_| | (_| |
                     *     |_| |_|\___|\__,_|\__,_|
                     *
                     */
                    // If the resident changes in the list. We need to restart a new table.
                    boolean residentChanges = !resID.equalsIgnoreCase(prescription.getResident().getId());
                    if (residentChanges) {
                        // the table has to be closed every time the resident changes. But not the first time... obviously
                        if (table != null) {
                            pdf.getDocument().add(table);
                            pdf.getDocument().add(Chunk.NEWLINE);
                        }

                        table = new PdfPTable(new float[]{6, 1, 1, 1, 1, 1, 1, 6});
                        table.setTotalWidth(Utilities.millimetersToPoints(180));
                        table.setLockedWidth(true);
                        PdfPCell cell = new PdfPCell(new Phrase(ResidentTools.getLabelText(prescription.getResident()), whiteFont));
                        cell.setBackgroundColor(BaseColor.BLACK);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_TOP);
                        cell.setColspan(8);
                        table.addCell(cell);

                        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
//                        table.getDefaultCell().setBackgroundColor(null);

                        table.addCell(PDF.cell("nursingrecords.prescription.dailyplan.table.col1", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
                        table.addCell(PDF.cell("misc.msg.earlyinthemorning.short", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
                        table.addCell(PDF.cell("misc.msg.morning.short", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
                        table.addCell(PDF.cell("misc.msg.noon.short", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
                        table.addCell(PDF.cell("misc.msg.afternoon.short", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
                        table.addCell(PDF.cell("misc.msg.evening.short", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
                        table.addCell(PDF.cell("misc.msg.lateatnight.short", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
                        table.addCell(PDF.cell("misc.msg.comment", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));

                        table.setHeaderRows(2);

                        resID = prescription.getResident().getId();
                    }

                    /***
                     *                      _             _
                     *       ___ ___  _ __ | |_ ___ _ __ | |_
                     *      / __/ _ \| '_ \| __/ _ \ '_ \| __|
                     *     | (_| (_) | | | | ||  __/ | | | |_
                     *      \___\___/|_| |_|\__\___|_| |_|\__|
                     *
                     */
                    table.getDefaultCell().setVerticalAlignment(Element.ALIGN_LEFT);
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_TOP);

                    if (gray) table.getDefaultCell().setBackgroundColor(BaseColor.LIGHT_GRAY);

                    Phrase col1 = getShortDescriptionAsPhrase(prescription);
                    if (bestid != null) {
                        MedStock stock = em.find(MedStock.class, bestid.longValue());
                        col1.add(Chunk.NEWLINE);
                        col1.add(PDF.chunk(SYSTools.xx("nursingrecords.prescription.dailyplan.stockInUse") + " " + SYSTools.xx("misc.msg.number") + " " + stock.getID(), PDF.italic()));

                        String warning = "";
                        warning += (stock.expiresIn(7) ? "!!" : "");
                        warning += (stock.expiresIn(0) ? "!!!!" : "");
                        // variable expiry ?
                        if (stock.getTradeForm().getDaysToExpireAfterOpened() != null) {
                            col1.add(Chunk.NEWLINE);
                            col1.add(PDF.chunk(warning + " " + SYSTools.xx("misc.msg.expiresAfterOpened") + ": " + df.format(new DateTime(stock.getOpened()).plusDays(stock.getTradeForm().getDaysToExpireAfterOpened()).toDate())));
                        }
                        if (stock.getExpires() != null) {
                            DateFormat sdf = df;
                            // if expiry is at the end of a month then it has a different format
                            if (new LocalDate(stock.getExpires()).equals(new LocalDate(stock.getExpires()).dayOfMonth().withMaximumValue())) {
                                sdf = new SimpleDateFormat("MM/yy");
                            }
                            col1.add(Chunk.NEWLINE);
                            col1.add(PDF.chunk(SYSTools.xx("misc.msg.expires") + ": " + sdf.format(stock.getExpires())));
                        }
                    }
                    table.addCell(col1);

                    table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

                    if (schedule.usesTime()) {
                        PdfPCell cellTime = new PdfPCell();
                        if (gray) cellTime.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cellTime.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cellTime.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cellTime.setColspan(6);

                        Chunk timeChunk = PDF.chunk(DateFormat.getTimeInstance(DateFormat.SHORT).format(schedule.getUhrzeit()) + " " + SYSTools.xx("misc.msg.Time.short"), PDF.bold());
                        timeChunk.setUnderline(0.4f, -1f);

                        Phrase contentTime = new Phrase();
                        contentTime.setFont(PDF.plain());

                        // this is only as a workaround until i figure out to align cells with a colspan.
                        Chunk tab1 = new Chunk(new VerticalPositionMark(), 40, false);
                        contentTime.add(tab1);
                        contentTime.add(timeChunk);
                        contentTime.add(" ");

                        contentTime.add(PDF.getAsPhrase(schedule.getUhrzeitDosis()));
                        contentTime.add(schedule.getPrescription().hasMed() ? " " + SYSConst.UNITS[schedule.getPrescription().getTradeForm().getDosageForm().getUsageUnit()] : "x");
                        contentTime.add(Chunk.NEWLINE);
                        contentTime.add(" ");
                        cellTime.addElement(contentTime);

                        table.addCell(cellTime);

                    } else {
                        table.addCell(PDF.getAsPhrase(schedule.getNachtMo()));
                        table.addCell(PDF.getAsPhrase(schedule.getMorgens()));
                        table.addCell(PDF.getAsPhrase(schedule.getMittags()));
                        table.addCell(PDF.getAsPhrase(schedule.getNachmittags()));
                        table.addCell(PDF.getAsPhrase(schedule.getAbends()));
                        table.addCell(PDF.getAsPhrase(schedule.getNachtAb()));
                    }

                    table.getDefaultCell().setVerticalAlignment(Element.ALIGN_JUSTIFIED);
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_TOP);

                    table.addCell(PrescriptionScheduleTools.getRemarkAsPhrase(schedule));

                    table.getDefaultCell().setBackgroundColor(null);

                }
                pdf.getDocument().add(table);
                pdf.getDocument().close();
                return pdf;
            }

            @Override
            protected void done() {
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);

                try {
                    SYSFilesTools.handleFile(((PDF) get()).getOutputFile(), Desktop.Action.OPEN);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (ExecutionException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                em.close();
            }
        };
        worker.execute();


    }

//    private static String formatDose(BigDecimal dose){
//        return dose.equals(BigDecimal.ZERO) ? "" : dose.toString();
//    }


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
        return result;
    }

    /**
     * setzt das medikament aus dieser Verordnung auf die Bestellliste oder löscht sie davon, falls es schon drauf war.
     *
     * @param prescription
     */
    public static void toggle_order_status_for(Prescription prescription) {
        if (!prescription.hasMed()) return;
        // if (MedOrderTools.contains(prescription)) MedOrderTools.remove(prescription)
        // else MedOrderTools.add(prescription);
    }


    public static String getShortDescriptionAsCompactText(Prescription prescription) {
        String result = "";

        if (!prescription.hasMed()) {
            result += prescription.getIntervention().getBezeichnung();
        } else {
            MedInventory inventory = TradeFormTools.getInventory4TradeForm(prescription.getResident(), prescription.getTradeForm());
            MedStock stockInUse = prescription.isClosed() ? null : MedStockTools.getStockInUse(inventory);

            if (stockInUse != null) {
                result += stockInUse.getTradeForm().getMedProduct().getText()
                        + (stockInUse.getTradeForm().getSubtext().isEmpty() ? "" : " " + stockInUse.getTradeForm().getSubtext()) +
                        SYSTools.left(
                                (stockInUse.getTradeForm().getDosageForm().getPreparation().isEmpty() ? "" : " " + stockInUse.getTradeForm().getDosageForm().getPreparation()) + " " +
                                        (prescription.getTradeForm().getDosageForm().getUsageText().isEmpty() ? SYSConst.UNITS[prescription.getTradeForm().getDosageForm().getUsageUnit()] : prescription.getTradeForm().getDosageForm().getUsageText()), 4, ".");

            } else {
                result += prescription.getTradeForm().getMedProduct().getText()
                        + (prescription.getTradeForm().getSubtext().isEmpty() ? "" : " " + prescription.getTradeForm().getSubtext()) +
                        SYSTools.left(
                                (prescription.getTradeForm().getDosageForm().getPreparation().isEmpty() ? "" : " " + prescription.getTradeForm().getDosageForm().getPreparation()) + " " +
                                        (prescription.getTradeForm().getDosageForm().getUsageText().isEmpty() ? SYSConst.UNITS[prescription.getTradeForm().getDosageForm().getUsageUnit()] : prescription.getTradeForm().getDosageForm().getUsageText()), 4, ".");
            }
        }
        return result;
    }

    public static Phrase getShortDescriptionAsPhrase(Prescription prescription) {
        Phrase phrase = new Phrase();

        if (!prescription.hasMed()) {
            phrase.add(PDF.chunk(prescription.getIntervention().getBezeichnung()));
        } else {
            MedInventory inventory = TradeFormTools.getInventory4TradeForm(prescription.getResident(), prescription.getTradeForm());
            MedStock stockInUse = prescription.isClosed() ? null : MedStockTools.getStockInUse(inventory);

            if (stockInUse != null) {
                phrase.add(PDF.chunk(stockInUse.getTradeForm().getMedProduct().getText() + (stockInUse.getTradeForm().getSubtext().isEmpty() ? "" : " " + stockInUse.getTradeForm().getSubtext()), PDF.bold()));
                phrase.add(" ");
                phrase.add(PDF.chunk((stockInUse.getTradeForm().getDosageForm().getPreparation().isEmpty() ? "" : " " + stockInUse.getTradeForm().getDosageForm().getPreparation()) + " " +
                        (prescription.getTradeForm().getDosageForm().getUsageText().isEmpty() ? SYSConst.UNITS[prescription.getTradeForm().getDosageForm().getUsageUnit()] : prescription.getTradeForm().getDosageForm().getUsageText())));
            } else {
                phrase.add(PDF.chunk(prescription.getTradeForm().getMedProduct().getText() + (prescription.getTradeForm().getSubtext().isEmpty() ? "" : " " + prescription.getTradeForm().getSubtext()), PDF.bold()));
                phrase.add(" ");
                phrase.add(PDF.chunk((prescription.getTradeForm().getDosageForm().getPreparation().isEmpty() ? "" : " " + prescription.getTradeForm().getDosageForm().getPreparation()) + " " +
                        (prescription.getTradeForm().getDosageForm().getUsageText().isEmpty() ? SYSConst.UNITS[prescription.getTradeForm().getDosageForm().getUsageUnit()] : prescription.getTradeForm().getDosageForm().getUsageText())));
            }
        }

        //result += "</font>";

        return phrase;
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

    public static Phrase getOriginalPrescriptionAsPhrase(Prescription presription) {
        Phrase phrase = new Phrase();

        if (presription.shouldBeCalculated()) {

            MedInventory inventory = TradeFormTools.getInventory4TradeForm(presription.getResident(), presription.getTradeForm());
            MedStock stockInUse = MedStockTools.getStockInUse(inventory);

            if (stockInUse != null) {
                // If the current prescription defers from the original one (different provider of the medication as in the beginning)
                if (!stockInUse.getTradeForm().equals(presription.getTradeForm())) {
                    phrase.add(PDF.chunk(TradeFormTools.toPrettyHTMLalternative(presription.getTradeForm()), PDF.plain(PDF.sizeDefault() - 2)));
                    phrase.add(Chunk.NEWLINE);
                }
            }
        }


        return phrase;
    }


    public static String getLongDescription(Prescription presription) {
        String result = "";//"<div id=\"fonttext\">";// = SYSConst.html_fontface;

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
//                    result += " <i>(" + SYSTools.xx(PnlPrescription.internalClassID + ".originalprescription") + ": " + presription.getTradeForm().getMedProduct().getName();
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
        return result;
    }

    public static String getOrderInformation(Prescription prescription) {
        if (!prescription.hasMed()) return "";
        Optional<MedOrder> optionalMedOrder = MedOrderTools.find(prescription);
        if (optionalMedOrder.isEmpty()) return "";
        return HTMLTools.p(SYSConst.html_color(Color.green, "nursingrecords.prescription.ordered"));
    }

    public static String getRemark(Prescription prescription) {
        String result = "<div id=\"fonttext\">";

        if (prescription.hasMed() && prescription.getTradeForm().getMedProduct().hasSideEffects()) {
            result += "<b><u>" + SYSTools.xx("misc.msg.sideeffects") + ":</u> <font color=\"orange\">" + prescription.getTradeForm().getMedProduct().getSideEffects() + "</font></b>";
        }
        if (prescription.isOnDemand()) {
            result += result.isEmpty() ? "" : "<br/>";
            result += "<b><u>" + SYSTools.xx("misc.msg.ondemand") + ":</u> <font color=\"blue\">" + prescription.getSituation().getText() + "</font></b>";
        }
        if (!prescription.getText().isEmpty()) {
            result += result.isEmpty() ? "" : "<br/>";
            result += "<b><u>" + SYSTools.xx("misc.msg.comment") + ":</u> </b>" + prescription.getText();
        }
        if (prescription.isOnDailyPlan()) {
            result += "<br/>" + SYSConst.html_italic(SYSTools.xx("nursingrecords.prescription.addedToDailyPlan"));
        }
        if (prescription.isWeightControlled()) {
            result += "<br/>" + SYSConst.html_bold(SYSTools.xx("opde.medication.tradeform.weightControlled"));
        }

        return result + "</div>";
    }

    public static String getAnnontationsAsHTML(Prescription prescription) {

        String result = "<div id=\"fonttext\">";

        if (isAnnotationNecessary(prescription)) {
            result += "<br/>" + SYSConst.html_bold("nursingrecords.prescription.edit.annotations") + "<br/>";


            if (prescription.getAnnotations().isEmpty()) {
                result += SYSTools.xx("misc.msg.noentryyet") + "<br/>";
            }

            for (ResInfo annotation : prescription.getAnnotations()) {
                Commontags tag = CommontagsTools.getTagForAnnotation(annotation);
                result += SYSConst.html_16x16_Annotate_internal + "&nbsp;" + tag.getText() + "<br/>";
                result += annotation == null ? SYSTools.xx("misc.msg.noentryyet") : ResInfoTools.getContentAsHTML(annotation);

            }
        }

        return result + "</div>";
    }


    public static boolean isAnnotationNecessary(Prescription prescription) {
        for (Commontags tag : prescription.getCommontags()) {
            if (CommontagsTools.isAnnotationNecessary(tag)) return true;
        }
        return false;
    }


    public static String getDoseAsHTML(Prescription prescription) {
        return getDoseAsHTML(prescription, false);
    }

    public static String getDoseAsCompactText(Prescription prescription) {
        String result = "";

        ArrayList<PrescriptionSchedule> listSchedules = new ArrayList<>(prescription.getPrescriptionSchedule());
        Collections.sort(listSchedules);

        if (listSchedules.isEmpty()) {
            result += SYSTools.xx("nursingrecords.prescription.noDosageYet");
        } else {
            for (PrescriptionSchedule schedule : listSchedules) {
                result += PrescriptionScheduleTools.getDoseAsCompactText(schedule) + "; ";
            }
            result = result.substring(0, result.length() - 2);
        }


        return result;
    }

    public static String getDoseAsHTML(Prescription prescription, boolean showInventory) {
        String result = "";
        ArrayList<PrescriptionSchedule> listSchedules = new ArrayList<>(prescription.getPrescriptionSchedule());
//        if (listSchedules.size() > 1)
        Collections.sort(listSchedules);

        Iterator<PrescriptionSchedule> planungen = listSchedules.iterator(); //prescription.getPrescriptionSchedule().iterator();

        if (planungen.hasNext()) {
            PrescriptionSchedule previousSchedule = null;
            PrescriptionSchedule schedule = null;
            while (planungen.hasNext()) {
                schedule = planungen.next();
                result += PrescriptionScheduleTools.getDoseAsHTML(schedule, previousSchedule, false);
                previousSchedule = schedule;
            }
            if (PrescriptionScheduleTools.getTerminStatus(schedule) != PrescriptionScheduleTools.MAXDOSE) {
                // Wenn die letzte Planung eine Tabelle benötigte (das tut sie dann, wenn
                // es keine Bedarfsverordnung war), dann müssen wir die Tabelle hier noch
                // schließen.
                result += "</table>";
            }
        } else {
            result += "<i>" + SYSTools.xx("nursingrecords.prescription.noDosageYet") + "</i><br/>";
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
                result += "<b>" + SYSTools.xx("misc.msg.onlyUntilEndOfPackage") + "</b><br/>";
            }
            if (!prescription.isClosed()) {
                if (stockInUse != null) {
                    EntityManager em = OPDE.createEM();

                    BigDecimal invSum = null;
                    BigDecimal stockSum = null;
                    try {
                        invSum = MedInventoryTools.getSum(inventory);
                        stockSum = MedStockTools.getSum(stockInUse);
                    } catch (Exception e) {
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }

                    if (invSum != null && invSum.compareTo(BigDecimal.ZERO) > 0) {
                        result += "<b><u>" + SYSTools.xx("misc.msg.inventory") + ":</u> <font color=\"green\">" + invSum.setScale(2, BigDecimal.ROUND_UP) + " " +
                                SYSConst.UNITS[stockInUse.getTradeForm().getDosageForm().getPackUnit()] +
                                "</font></b>";
                        if (stockInUse.getTradeForm().getDosageForm().isUPRn()) {
                            BigDecimal anwmenge = invSum.multiply(stockInUse.getUPR());

                            result += " " + SYSTools.xx("misc.msg.equalTo") + " " + anwmenge.setScale(2, BigDecimal.ROUND_UP) + " " +
                                    DosageFormTools.getUsageText(stockInUse.getTradeForm().getDosageForm());
                            result += " (" + SYSTools.xx("misc.msg.upr") + ": " + stockInUse.getUPR().setScale(2, BigDecimal.ROUND_UP) + " " + SYSTools.xx("misc.msg.to1");
                            if (stockInUse.getUPRDummyMode() == MedStockTools.REPLACE_WITH_EFFECTIVE_UPR_WHEN_CLOSING) {
                                result += ", " + SYSTools.xx("misc.msg.preliminary");
                            }
                            result += ")";
                        }

                        result += "<br/>" + SYSTools.xx("misc.msg.stockInUse") + ": <b><font color=\"green\">" + stockInUse.getID() + "</font></b>";

                        if (invSum.compareTo(stockSum) != 0) {
                            result += "<br/>" + SYSTools.xx("misc.msg.leftInStock") + ": <b><font color=\"green\">" + stockSum.setScale(2, BigDecimal.ROUND_UP) + " " +
                                    SYSConst.UNITS[stockInUse.getTradeForm().getDosageForm().getPackUnit()] + "</font></b>";
                            if (stockInUse.getTradeForm().getDosageForm().isUPRn()) {
                                BigDecimal usage = stockSum.multiply(stockInUse.getUPR());

                                result += " (" + SYSTools.xx("misc.msg.equalTo") + " " + usage.setScale(2, BigDecimal.ROUND_UP) + " " +
                                        DosageFormTools.getUsageText(stockInUse.getTradeForm().getDosageForm()) + ")";
                            }
                        }

                        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                        // variable expiry ?
                        if (stockInUse.getTradeForm().getDaysToExpireAfterOpened() != null) {
                            result += "<br/><font color=\"" + MedStockTools.getHTMLColor(stockInUse) + "\">" + SYSTools.xx("misc.msg.expiresAfterOpened") + ": " + df.format(new DateTime(stockInUse.getOpened()).plusDays(stockInUse.getTradeForm().getDaysToExpireAfterOpened()).toDate()) + "</font>";
                        }

                        // fixed expiry ?
                        if (stockInUse.getExpires() != null) {
                            // only abbreviate on the first of a month
                            String expiryString;
                            if (new LocalDate(stockInUse.getExpires()).getDayOfMonth() == 1) {
                                expiryString = new SimpleDateFormat("MM/yy").format(stockInUse.getExpires());
                            } else {
                                expiryString = df.format(stockInUse.getExpires());
                            }

                            result += "<br/><font color=\"" + MedStockTools.getHTMLColor(stockInUse) + "\">" + SYSTools.xx("misc.msg.expires") + ": " + expiryString + "</font>";
                        }


                    } else {
                        result += "<b><font color=\"red\">" + SYSTools.xx("misc.msg.emptyInventory") + "</font></b>";
                    }
                } else {
                    if (inventory == null) {
                        result += "<b><font color=\"red\">" + SYSTools.xx("misc.msg.noInventoryYet") + "</font></b>";
                    } else {
                        if (MedInventoryTools.getNextToOpen(inventory) != null) {
                            result += "<br/><b><font color=\"red\">" + SYSTools.xx("misc.msg.noOpenStock") + "</font></b>";
                        } else {
                            result += "<br/><b><font color=\"red\">" + SYSTools.xx("misc.msg.noOpenStock") + "</font></b>";
                        }

                    }
                }
            }

        }
        return result;
    }

    /**
     * Dieser Query ordnet Verordnungen den Vorräten zu. Dazu ist ein kleiner Trick nötig. Denn über die Zeit können
     * verschiedene Vorräte mit verschiedenen Darreichungen für dieselbe Verordnung verwendet werden. Der Trick ist der
     * Join über zwei Spalten in der Zeile mit "MPBestand"
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

    public static ArrayList<Prescription> getAll(int type, LocalDate from, LocalDate to) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("" +
                " SELECT DISTINCT p FROM Prescription p " +
                " JOIN p.commontags ct " +
                " WHERE ((p.from <= :from AND p.to >= :from) OR " +
                " (p.from <= :to AND p.to >= :to) OR " +
                " (p.from > :from AND p.to < :to)) " +
                " AND ct.type = :type ");
        query.setParameter("from", from.toDateTimeAtStartOfDay().toDate());
        query.setParameter("to", SYSCalendar.eod(to).toDate());
        query.setParameter("type", type);
        ArrayList<Prescription> prescriptions = new ArrayList<Prescription>(query.getResultList());
        em.close();
        return prescriptions;
    }

    public static ArrayList<Prescription> getAll(Resident resident, LocalDate from, LocalDate to) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("" +
                " SELECT DISTINCT p FROM Prescription p " +
                " WHERE ((p.from <= :from AND p.to >= :from) OR " +
                " (p.from <= :to AND p.to >= :to) OR " +
                " (p.from > :from AND p.to < :to)) " +
                " AND p.resident = :resident " +
                " ORDER BY p.from");
        query.setParameter("from", from.toDateTimeAtStartOfDay().toDate());
        query.setParameter("to", SYSCalendar.eod(to).toDate());
        query.setParameter("resident", resident);
        ArrayList<Prescription> prescriptions = new ArrayList<Prescription>(query.getResultList());
        em.close();
        return prescriptions;
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


    /**
     * Diese Methode wird nach der Anmeldung benutzt um festzustellen ob wir leere Vorräte haben.
     * https://github.com/tloehr/Offene-Pflege.de/issues/102
     *
     * @return
     */
    public static ArrayList<Prescription> getAllActiveWithEmptyInventories() {
        ArrayList<Prescription> result = new ArrayList<>();

        try {

            EntityManager em = OPDE.createEM();
            Query query = em.createQuery(" SELECT p FROM Prescription p WHERE p.to >= :now AND p.tradeform IS NOT NULL");
            query.setParameter("now", new Date());
            ArrayList<Prescription> listPrescriptions = new ArrayList<Prescription>(query.getResultList());

            for (Prescription p : listPrescriptions) {
                try {
                    MedInventory inventory = TradeFormTools.getInventory4TradeForm(em, p.getResident(), p.getTradeForm());

                    MedStock stockInUse = MedStockTools.getStockInUse(em, inventory);
                    if (stockInUse == null) { //leeren Vorrat gefunden
                        result.add(p);
                    }

                } catch (javax.persistence.NoResultException nre) {
                    result.add(p);
                }
            }

            em.close();
        } catch (Exception e) {
            OPDE.fatal(e);
        }

        for (Prescription p : result) {
            log.debug(toPrettyString(p));
        }

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

//    public static ArrayList<Prescription> getAllActiveWithWeightControll() {
//        EntityManager em = OPDE.createEM();
//
//        ArrayList<Prescription> result = null;
//        Query query = em.createQuery(" SELECT p FROM Prescription p WHERE p.weightControl = :weightControl AND p.to >= :now");
//        query.setParameter("weightControl", true);
//        query.setParameter("now", new Date());
//
//        result = new ArrayList(query.getResultList());
//
//        em.close();
//        return result;
//    }

    public static ArrayList<Prescription> getAllActiveRegularMedsOnly(Resident resident) {
        EntityManager em = OPDE.createEM();

        ArrayList<Prescription> result = null;
        Query query = em.createQuery(" SELECT p FROM Prescription p WHERE p.resident = :resident AND p.situation IS NULL AND p.tradeform IS NOT NULL AND p.to >= :now ORDER BY p.tradeform.medProduct.text");
        query.setParameter("resident", resident);
        query.setParameter("now", new Date());
        result = new ArrayList<Prescription>(query.getResultList());

        em.close();
        return result;
    }

    public static ArrayList<Prescription> getAllActiveOnDemandMedsOnly(Resident resident) {
        EntityManager em = OPDE.createEM();

        ArrayList<Prescription> result = null;
        Query query = em.createQuery(" SELECT p FROM Prescription p WHERE p.resident = :resident AND p.situation IS NOT NULL AND p.tradeform IS NOT NULL AND p.to >= :now ORDER BY p.tradeform.medProduct.text");
        query.setParameter("resident", resident);
        query.setParameter("now", new Date());
        result = new ArrayList<Prescription>(query.getResultList());

        em.close();
        return result;
    }

    public static ArrayList<Prescription> getAllActiveByFlag(Resident resident, int flag) {
        EntityManager em = OPDE.createEM();

        ArrayList<Prescription> result = null;
        Query query = em.createQuery(" SELECT p FROM Prescription p WHERE p.resident = :resident AND p.to >= :now AND p.intervention.flag = :flag");
        query.setParameter("resident", resident);
        query.setParameter("now", new Date());
        query.setParameter("flag", flag);
        result = new ArrayList<Prescription>(query.getResultList());

        em.close();
        return result;
    }

    /**
     * Dieser Query ordnet Verordnungen den Vorräten zu. Dazu ist ein kleiner Trick nötig. Denn über die Zeit können
     * verschiedene Vorräte mit verschiedenen Darreichungen für dieselbe Verordnung verwendet werden. Der Trick ist der
     * Join über zwei Spalten in der Zeile mit "MPBestand"
     */
    public static List<Prescription> getOnDemandPrescriptions(Resident resident, Date date) {
        LocalDate d1 = new LocalDate(date);
        return getAll(resident, d1, d1).stream().filter(prescription -> prescription.getSituation() != null)
                .sorted(Comparator.comparing(p -> p.getSituation().getText()))
                .collect(Collectors.toList());
//
//
//        EntityManager em = OPDE.createEM();
//
//        Query query = em.createQuery("SELECT p FROM Prescription p WHERE p.resident = :resident AND p.situation IS NOT NULL AND p.from <= :from AND p.to >= :to ORDER BY p.situation.text, p.id");
//        query.setParameter("resident", resident);
//        query.setParameter("from", new LocalDate(date).toDateTimeAtStartOfDay().toDate());
//        query.setParameter("to", SYSCalendar.eod(new LocalDate(date)).toDate());
//
//        List<Prescription> list = query.getResultList();
//
//        em.close();
//        return list;
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
            result += withheader ? "<h2 id=\"fonth2\" >" + SYSTools.xx("nursingrecords.prescription") + (withlongheader ? ": " + ResidentTools.getLabelText(prescription.getResident()) : "") + "</h2>" : "";

            result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>" +
                    "<th>" + SYSTools.xx("misc.msg.medication") + "/" + SYSTools.xx("misc.msg.measures") + "</th><th>" + SYSTools.xx("misc.msg.dosage") + "</th><th>" + SYSTools.xx("misc.msg.valid.from") + "</th></tr>";

            for (Prescription myprescription : list) {

                if (withDiscontinued || !myprescription.isClosed()) {

                    result += "<tr>";
                    result += "<td valign=\"top\">" + (withIcon && myprescription.isClosed() ? SYSConst.html_22x22_StopSign : "") + getLongDescription(myprescription);
                    result += (myprescription.getCommontags().isEmpty() ? "" : "<br/>" + CommontagsTools.getAsHTML(myprescription.getCommontags(), withIcon ? SYSConst.html_16x16_tagPurple : ""));
                    result += "</td>";
                    result += "<td valign=\"top\">" + getDoseAsHTML(myprescription, withmed) + "<br/>";
                    result += getRemark(myprescription);

                    result += "</td>";
                    result += "<td valign=\"top\">" + myprescription.getPITAsHTML();

                    result += "</td>";
                    result += "</tr>";
                }
            }

            result += "</table>";
        } else {
            result += "<h2  id=\"fonth2\" >" + SYSTools.xx("nursingrecords.prescription") + "</h2><i>" + SYSTools.xx("misc.msg.currentlynoentry") + "</i>";
        }
        return result;
    }

    public static String getPITAsHTML(Prescription prescription) {
//        String result = "";
        DateFormat df = DateFormat.getDateInstance();

        String td = SYSConst.html_bold(df.format(prescription.getFrom())) + "; " +
                prescription.getUserON().getFullname() +
                (prescription.getDocON() != null ? "; " + GPTools.getFullName(prescription.getDocON()) : "") +
                (prescription.getHospitalON() != null ? "; " + HospitalTools.getFullName(prescription.getHospitalON()) : "");

        td += "&nbsp;&rarr;&nbsp;";

        if (prescription.isClosed()) {
            td += SYSConst.html_bold(df.format(prescription.getTo())) + "; " +
                    prescription.getUserOFF().getFullname() +
                    (prescription.getDocOFF() != null ? "; " + GPTools.getFullName(prescription.getDocOFF()) : "") +
                    (prescription.getHospitalOFF() != null ? "; " + HospitalTools.getFullName(prescription.getHospitalOFF()) : "");

        }

        td += "<br/>[" + prescription.getID() + "]";

        return td;
    }

    public static String getPrescriptionsAsHTML4PainList(List<Prescription> list, LocalDate from, LocalDate to) {
        String result = "";

        if (!list.isEmpty()) {
            for (Prescription myprescription : list) {
                String paragraph = "";

                paragraph += (myprescription.getCommontags().isEmpty() ? "" : " " + CommontagsTools.getAsHTML(myprescription.getCommontags(), SYSConst.html_16x16_tagPurple));
                paragraph += "<br/>" + getDoseAsHTML(myprescription, false);
                paragraph += "<br/>" + getRemark(myprescription);
                paragraph += "<br/>" + getPITAsHTML(myprescription);


                result += "<font size=+1>" + getLongDescription(myprescription) + "</font>";
                result += SYSConst.html_paragraph(paragraph);

                if (myprescription.isOnDemand()) {
                    ArrayList<BHP> listBHP = BHPTools.getBHPs(myprescription, from, to);

                    if (!listBHP.isEmpty()) {
                        result += SYSConst.html_h3("BHPs");
                        result += SYSConst.html_paragraph(BHPTools.getBHPsAsHTMLtable(listBHP, false));
                    }
                }
            }
        } else {
            result += SYSConst.html_h2("nursingrecords.prescription") + SYSConst.html_italic("misc.msg.currentlynoentry");
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

        myPretty += verordnung.isOnDemand() ? " (" + SYSTools.xx("misc.msg.ondemand") + ": " + verordnung.getSituation().getText() + ")" : "";

        return myPretty;
    }

    public static String toPrettyHTML(Prescription verordnung) {
        String myPretty = "";

        if (verordnung.hasMed()) {
            myPretty = "[" + verordnung.getID() + "] " + SYSConst.html_bold(TradeFormTools.toPrettyString(verordnung.getTradeForm()));
        } else {
            myPretty = SYSConst.html_bold(verordnung.getIntervention().getBezeichnung());
        }

        myPretty += verordnung.isOnDemand() ? " (" + SYSTools.xx("misc.msg.ondemand") + ": " + SYSConst.html_color(Color.blue, verordnung.getSituation().getText()) + ")" : "";

        return myPretty;
    }


    public static void closeAll(EntityManager em, Resident resident, Date enddate) throws Exception {
        Query query = em.createQuery("SELECT b FROM Prescription b WHERE b.resident = :resident AND b.to >= :now");
        query.setParameter("resident", resident);
        query.setParameter("now", enddate);
        List<Prescription> verordnungen = query.getResultList();

        for (Prescription verordnung : verordnungen) {
            Prescription myp = em.merge(verordnung);
            em.lock(myp, LockModeType.OPTIMISTIC);
            // just in case, somebody added an info AFTER the resident moved out / died. We fix that start date here
            myp.setFrom(SYSCalendar.min(myp.getFrom(), enddate));
            myp.setTo(enddate);
            myp.setUserOFF(em.merge(OPDE.getLogin().getUser()));
//            BHPTools.cleanup(em, myp);
        }
    }

    public static ArrayList<Prescription> getPrescriptions4Tags(Resident resident, Commontags tag) {
        EntityManager em = OPDE.createEM();
        ArrayList<Prescription> list = null;

        try {

            String jpql = " SELECT p " +
                    " FROM Prescription p" +
                    " JOIN p.commontags t " +
                    " WHERE p.resident = :resident " +
                    " AND t = :tag " +
                    " ORDER BY p.from DESC ";

            Query query = em.createQuery(jpql);

            query.setParameter("resident", resident);
            query.setParameter("tag", tag);

            list = new ArrayList<Prescription>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }


}
