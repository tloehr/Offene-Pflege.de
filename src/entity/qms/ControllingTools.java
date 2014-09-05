package entity.qms;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import entity.Station;
import entity.files.SYSFilesTools;
import entity.info.ResInfo;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.nursingprocess.NursingProcess;
import entity.prescription.*;
import entity.reports.NReport;
import entity.reports.NReportTools;
import entity.system.Commontags;
import entity.system.CommontagsTools;
import op.OPDE;
import op.system.PDF;
import op.threads.DisplayMessage;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by tloehr on 05.09.14.
 */
public class ControllingTools {

    public static HashMap<Resident, DB> getPainDossierData(LocalDate from) {

        Commontags painTag = CommontagsTools.getType(CommontagsTools.TYPE_SYS_PAIN);
        Commontags painMgrTag = CommontagsTools.getType(CommontagsTools.TYPE_SYS_PAINMGR);

        if (painTag == null) return null;
        if (painMgrTag == null) return null;

        HashMap<Resident, DB> mapResidents = new HashMap<>();

        ArrayList<NReport> painReports = NReportTools.getNReports4Tags(painTag, from, new LocalDate());
        painReports.addAll(NReportTools.getNReports4Tags(painMgrTag, from, new LocalDate()));

        for (NReport nReport : painReports) {
            if (!mapResidents.containsKey(nReport.getResident())) {
                mapResidents.put(nReport.getResident(), new DB());
            }

            mapResidents.get(nReport.getResident()).getNreports().add(nReport);

        }

        return mapResidents;

    }


    public static String getPainDossierAsHTML(LocalDate from){
        HashMap<Resident, DB> mapResidents = getPainDossierData(from);

        for (Resident resident : mapResidents.keySet()){




        }

    }

//    private static void printPainDossierAsPDF(final Station station, final List data) throws Exception {
//
//            OPDE.getMainframe().setBlocked(true);
//            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));
//
//            final Font whiteFont = PDF.bold();
//            whiteFont.setColor(BaseColor.WHITE);
//            final EntityManager em = OPDE.createEM();
//
//            SwingWorker worker = new SwingWorker() {
//
//                @Override
//                protected Object doInBackground() throws Exception {
//                    String header = SYSTools.xx("nursingrecords.prescription.dailyplan.header1") + " " + DateFormat.getDateInstance().format(new Date()) + " (" + station.getName() + ")";
//                    final PDF pdf = new PDF(null, header, 10);
//                    pdf.getDocument().add(new Header(OPDE.getAppInfo().getSignature(), header));
//
//
//                    Paragraph h1 = new Paragraph(new Phrase(header, PDF.plain(PDF.sizeH1())));
//                    h1.setAlignment(Element.ALIGN_CENTER);
//                    pdf.getDocument().add(h1);
//    //                pdf.getDocument().add(Chunk.NEWLINE);
//
//                    Paragraph p = new Paragraph(SYSTools.xx("nursingrecords.prescription.dailyplan.warning"));
//                    p.setAlignment(Element.ALIGN_CENTER);
//                    pdf.getDocument().add(p);
//                    pdf.getDocument().add(Chunk.NEWLINE);
//
//                    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
//                    int progress = -1;
//                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, data.size()));
//                    String resID = "";
//                    PdfPTable table = null;
//
//                    for (Object obj : data) {
//                        progress++;
//
//                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, data.size()));
//
//                        Object[] objects = (Object[]) obj;
//                        Prescription prescription = em.find(Prescription.class, ((BigInteger) objects[0]).longValue());
//                        PrescriptionSchedule schedule = em.find(PrescriptionSchedule.class, ((BigInteger) objects[1]).longValue());
//                        BigInteger bestid = (BigInteger) objects[2];
//                        BigInteger formid = (BigInteger) objects[4];
//
//                        // Alle Formen, die nicht abzählbar sind, werden grau hinterlegt. Also Tropfen, Spritzen etc.
//                        boolean gray = false;
//                        if (formid != null) {
//                            DosageForm form = em.find(DosageForm.class, formid.longValue());
//                            gray = form.getDailyPlan() > 0;
//                        }
//
//                        /***
//                         *      _                    _
//                         *     | |__   ___  __ _  __| |
//                         *     | '_ \ / _ \/ _` |/ _` |
//                         *     | | | |  __/ (_| | (_| |
//                         *     |_| |_|\___|\__,_|\__,_|
//                         *
//                         */
//                        // If the resident changes in the list. We need to restart a new table.
//                        boolean residentChanges = !resID.equalsIgnoreCase(prescription.getResident().getRID());
//                        if (residentChanges) {
//                            // the table has to be closed every time the resident changes. But not the first time... obviously
//                            if (table != null) {
//                                pdf.getDocument().add(table);
//                                pdf.getDocument().add(Chunk.NEWLINE);
//                            }
//
//                            table = new PdfPTable(new float[]{6, 1, 1, 1, 1, 1, 1, 6});
//                            table.setTotalWidth(Utilities.millimetersToPoints(180));
//                            table.setLockedWidth(true);
//                            PdfPCell cell = new PdfPCell(new Phrase(ResidentTools.getLabelText(prescription.getResident()), whiteFont));
//                            cell.setBackgroundColor(BaseColor.BLACK);
//                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                            cell.setVerticalAlignment(Element.ALIGN_TOP);
//                            cell.setColspan(8);
//                            table.addCell(cell);
//
//                            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
//    //                        table.getDefaultCell().setBackgroundColor(null);
//
//                            table.addCell(PDF.cell("nursingrecords.prescription.dailyplan.table.col1", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
//                            table.addCell(PDF.cell("misc.msg.earlyinthemorning.short", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
//                            table.addCell(PDF.cell("misc.msg.morning.short", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
//                            table.addCell(PDF.cell("misc.msg.noon.short", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
//                            table.addCell(PDF.cell("misc.msg.afternoon.short", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
//                            table.addCell(PDF.cell("misc.msg.evening.short", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
//                            table.addCell(PDF.cell("misc.msg.lateatnight.short", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
//                            table.addCell(PDF.cell("misc.msg.comment", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
//
//                            table.setHeaderRows(2);
//
//                            resID = prescription.getResident().getRID();
//                        }
//
//                        /***
//                         *                      _             _
//                         *       ___ ___  _ __ | |_ ___ _ __ | |_
//                         *      / __/ _ \| '_ \| __/ _ \ '_ \| __|
//                         *     | (_| (_) | | | | ||  __/ | | | |_
//                         *      \___\___/|_| |_|\__\___|_| |_|\__|
//                         *
//                         */
//                        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_LEFT);
//                        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_TOP);
//
//                        if (gray) {
//                            table.getDefaultCell().setBackgroundColor(BaseColor.LIGHT_GRAY);
//                        }
//
//                        Phrase col1 = getShortDescriptionAsPhrase(prescription);
//                        if (bestid != null) {
//                            MedStock stock = em.find(MedStock.class, bestid.longValue());
//                            col1.add(Chunk.NEWLINE);
//                            col1.add(PDF.chunk(SYSTools.xx("nursingrecords.prescription.dailyplan.stockInUse") + " " + SYSTools.xx("misc.msg.number") + " " + stock.getID(), PDF.italic()));
//
//                            String warning = "";
//                            warning += (stock.expiresIn(7) ? "!!" : "");
//                            warning += (stock.expiresIn(0) ? "!!!!" : "");
//                            // variable expiry ?
//                            if (stock.getTradeForm().getDaysToExpireAfterOpened() != null) {
//                                col1.add(Chunk.NEWLINE);
//                                col1.add(PDF.chunk(warning + " " + SYSTools.xx("misc.msg.expiresAfterOpened") + ": " + df.format(new DateTime(stock.getOpened()).plusDays(stock.getTradeForm().getDaysToExpireAfterOpened()).toDate())));
//                            }
//                            if (stock.getExpires() != null) {
//                                DateFormat sdf = df;
//                                // if expiry is at the end of a month then it has a different format
//                                if (new LocalDate(stock.getExpires()).equals(new LocalDate(stock.getExpires()).dayOfMonth().withMaximumValue())) {
//                                    sdf = new SimpleDateFormat("MM/yy");
//                                }
//                                col1.add(Chunk.NEWLINE);
//                                col1.add(PDF.chunk(SYSTools.xx("misc.msg.expires") + ": " + sdf.format(stock.getExpires())));
//                            }
//                        }
//                        table.addCell(col1);
//
//                        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
//                        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
//
//                        if (schedule.usesTime()) {
//                            PdfPCell cellTime = new PdfPCell();
//                            cellTime.setHorizontalAlignment(Element.ALIGN_CENTER);
//                            cellTime.setVerticalAlignment(Element.ALIGN_MIDDLE);
//                            cellTime.setColspan(6);
//
//                            Chunk timeChunk = PDF.chunk(DateFormat.getTimeInstance(DateFormat.SHORT).format(schedule.getUhrzeit()) + " " + SYSTools.xx("misc.msg.Time.short"), PDF.bold());
//                            timeChunk.setUnderline(0.4f, -1f);
//
//                            Phrase contentTime = new Phrase();
//                            contentTime.setFont(PDF.plain());
//
//                            // this is only as a workaround until i figure out to align cells with a colspan.
//                            Chunk tab1 = new Chunk(new VerticalPositionMark(), 40, false);
//                            contentTime.add(tab1);
//                            contentTime.add(timeChunk);
//                            contentTime.add(" ");
//
//                            contentTime.add(PDF.getAsPhrase(schedule.getUhrzeitDosis()));
//                            contentTime.add(schedule.getPrescription().hasMed() ? " " + SYSConst.UNITS[schedule.getPrescription().getTradeForm().getDosageForm().getUsageUnit()] : "x");
//                            contentTime.add(Chunk.NEWLINE);
//                            contentTime.add(" ");
//                            cellTime.addElement(contentTime);
//
//                            table.addCell(cellTime);
//
//                        } else {
//                            table.addCell(PDF.getAsPhrase(schedule.getNachtMo()));
//                            table.addCell(PDF.getAsPhrase(schedule.getMorgens()));
//                            table.addCell(PDF.getAsPhrase(schedule.getMittags()));
//                            table.addCell(PDF.getAsPhrase(schedule.getNachmittags()));
//                            table.addCell(PDF.getAsPhrase(schedule.getAbends()));
//                            table.addCell(PDF.getAsPhrase(schedule.getNachtAb()));
//                        }
//
//                        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_JUSTIFIED);
//                        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_TOP);
//
//                        table.addCell(PrescriptionScheduleTools.getRemarkAsPhrase(schedule));
//
//                        table.getDefaultCell().setBackgroundColor(null);
//
//                    }
//                    pdf.getDocument().add(table);
//                    pdf.getDocument().close();
//                    return pdf;
//                }
//
//                @Override
//                protected void done() {
//                    OPDE.getDisplayManager().setProgressBarMessage(null);
//                    OPDE.getMainframe().setBlocked(false);
//
//                    try {
//                        SYSFilesTools.handleFile(((PDF) get()).getOutputFile(), Desktop.Action.OPEN);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                    }
//                    em.close();
//                }
//            };
//            worker.execute();
//
//
//        }


    private static class DB {
        HashSet<NReport> nreports;
        HashSet<Prescription> prescriptions;
        HashSet<ResInfo> resInfos;
        HashSet<NursingProcess> nursingProcesses;

        private DB() {
            nreports = new HashSet<>();
            prescriptions = new HashSet<>();
            resInfos = new HashSet<>();
            nursingProcesses = new HashSet<>();
        }

        public HashSet<NReport> getNreports() {
            return nreports;
        }

        public HashSet<Prescription> getPrescriptions() {
            return prescriptions;
        }

        public HashSet<ResInfo> getResInfos() {
            return resInfos;
        }

        public HashSet<NursingProcess> getNursingProcesses() {
            return nursingProcesses;
        }
    }

}
