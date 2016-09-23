package entity.qms;

import entity.info.ResInfo;
import entity.info.ResInfoTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.nursingprocess.NursingProcess;
import entity.nursingprocess.NursingProcessTools;
import entity.prescription.Prescription;
import entity.prescription.PrescriptionTools;
import entity.reports.NReport;
import entity.reports.NReportTools;
import entity.system.Commontags;
import entity.system.CommontagsTools;
import entity.values.ResValue;
import entity.values.ResValueTools;
import op.tools.Pair;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by tloehr on 05.09.14.
 */
public class ControllingTools {

    /**
     * gathers all the necessary data and puts them into one data structure organised as a HashMap<Resident, DB>.
     * where DB is simply a helper class for gathering a couple of lists. nothing fancy.
     *
     * @param from
     * @param to
     * @return
     */
    public static HashMap<Resident, DB> getPainDossierData(LocalDate from, LocalDate to, Closure progress) throws Exception {

        Commontags painTag = CommontagsTools.getType(CommontagsTools.TYPE_SYS_PAIN);
        Commontags painMgrTag = CommontagsTools.getType(CommontagsTools.TYPE_SYS_PAINMGR);

        if (painTag == null) return null;
        if (painMgrTag == null) return null;

        HashMap<Resident, DB> mapResidents = new HashMap<>();

        int p = -1;

        p += 5;
        progress.execute(new Pair<>(p, 100));
        HashSet<NReport> painReports = new HashSet<>(NReportTools.getNReports4Tags(painTag, from, to));

        p += 5;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        painReports.addAll(NReportTools.getNReports4Tags(painMgrTag, from, to));
        p += 5;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        for (NReport nReport : painReports) {
            if (!mapResidents.containsKey(nReport.getResident())) {
                mapResidents.put(nReport.getResident(), new DB());
            }
            mapResidents.get(nReport.getResident()).getNreports().add(nReport);
        }

        ArrayList<ResValue> painValues = ResValueTools.getPainvalues(from, to);
        p += 5;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        for (ResValue val : painValues) {
            if (!mapResidents.containsKey(val.getResident())) {
                mapResidents.put(val.getResident(), new DB());
            }
            mapResidents.get(val.getResident()).getResValues().add(val);
        }

        HashSet<NursingProcess> painNP = new HashSet(NursingProcessTools.getAll(CommontagsTools.TYPE_SYS_PAIN, from, to));
        p += 5;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        painNP.addAll(NursingProcessTools.getAll(CommontagsTools.TYPE_SYS_PAINMGR, from, to));
        p += 5;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        for (NursingProcess np : painNP) {
            if (!mapResidents.containsKey(np.getResident())) {
                mapResidents.put(np.getResident(), new DB());
            }
            mapResidents.get(np.getResident()).getNursingProcesses().add(np);
        }

        HashSet<ResInfo> painInfo = new HashSet(ResInfoTools.getAll(CommontagsTools.TYPE_SYS_PAIN, from, to));
        p += 5;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        painInfo.addAll(ResInfoTools.getAll(CommontagsTools.TYPE_SYS_PAINMGR, from, to));
        p += 5;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        for (ResInfo info : painInfo) {
            if (!mapResidents.containsKey(info.getResident())) {
                mapResidents.put(info.getResident(), new DB());
            }
            mapResidents.get(info.getResident()).getResInfos().add(info);
        }

        HashSet<Prescription> painPresc = new HashSet(PrescriptionTools.getAll(CommontagsTools.TYPE_SYS_PAIN, from, to));
        p += 5;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        painPresc.addAll(PrescriptionTools.getAll(CommontagsTools.TYPE_SYS_PAINMGR, from, to));
        p += 5;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        for (Prescription prescription : painPresc) {
            if (!mapResidents.containsKey(prescription.getResident())) {
                mapResidents.put(prescription.getResident(), new DB());
            }
            mapResidents.get(prescription.getResident()).getPrescriptions().add(prescription);
        }

        progress.execute(new Pair<Integer, Integer>(50, 100));
        return mapResidents;

    }


    public static String getPainDossierAsHTML(LocalDate from, LocalDate to, Closure progress) throws Exception {

        HashMap<Resident, DB> mapResidents = getPainDossierData(from, to, progress);

        DateFormat df = DateFormat.getDateInstance();

        String html = "";

        BigDecimal step = new BigDecimal(50).divide(new BigDecimal(mapResidents.keySet().size()), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal p = new BigDecimal(50);


        ArrayList<Resident> listResident = new ArrayList<>(mapResidents.keySet());
        Collections.sort(listResident);

        for (Resident resident : listResident) {
            p = p.add(step);

            progress.execute(new Pair<Integer, Integer>(p.intValue(), 100));

            String resTXT = SYSConst.html_h1(SYSTools.xx("opde.controlling.orga.paindossier") + ": " + ResidentTools.getLabelText(resident));
            resTXT += SYSConst.html_bold(SYSTools.xx("controlling.misc.controlPeriod") + " " + df.format(from.toDate()) + " &rarr; " + df.format(to.toDate()));


            String nreports = SYSConst.html_h2("nursingrecords.reports");

            if (mapResidents.get(resident).getNreports().isEmpty()) {
                nreports += SYSTools.xx("misc.msg.currentlynoentry");
            } else {
                ArrayList<NReport> listReports = new ArrayList<>(mapResidents.get(resident).getNreports());
                Collections.sort(listReports);
                nreports += NReportTools.getNReportsAsHTML(listReports, false, false, null, null, false);
                listReports.clear();
            }
            resTXT += SYSConst.html_rectangle_around(nreports);

            String values = SYSConst.html_h2("misc.msg.pain.intensity");
            if (mapResidents.get(resident).getResValues().isEmpty()) {
                values += SYSTools.xx("misc.msg.currentlynoentry");
            } else {
                ArrayList<ResValue> listValues = new ArrayList<>(mapResidents.get(resident).getResValues());
                Collections.sort(listValues);
                values += ResValueTools.getAsHTML(listValues);
                listValues.clear();
            }
            resTXT += values;


            String info = SYSConst.html_h2("nursingrecords.info");
            if (mapResidents.get(resident).getResInfos().isEmpty()) {
                info += SYSTools.xx("misc.msg.currentlynoentry");
            } else {
                ArrayList<ResInfo> listInfos = new ArrayList<>(mapResidents.get(resident).getResInfos());
                Collections.sort(listInfos);
                info += ResInfoTools.getResInfosAsHTML(listInfos, true, null);
                listInfos.clear();
            }
            resTXT += SYSConst.html_rectangle_around(info);

            String prescription = SYSConst.html_h2("nursingrecords.prescription");
            if (mapResidents.get(resident).getPrescriptions().isEmpty()) {
                prescription += SYSTools.xx("misc.msg.currentlynoentry");
            } else {
                ArrayList<Prescription> listPrescriptions = new ArrayList<>(mapResidents.get(resident).getPrescriptions());
                Collections.sort(listPrescriptions);
                prescription += PrescriptionTools.getPrescriptionsAsHTML4PainList(listPrescriptions, from, to);
                listPrescriptions.clear();
            }
            resTXT += prescription;

            String nursingprocess = SYSConst.html_h2("nursingrecords.nursingprocess");
            if (mapResidents.get(resident).getNursingProcesses().isEmpty()) {
                nursingprocess += SYSTools.xx("misc.msg.currentlynoentry");
            } else {
                ArrayList<NursingProcess> listNP = new ArrayList<>(mapResidents.get(resident).getNursingProcesses());
                Collections.sort(listNP);
                for (NursingProcess np : listNP) {
                    nursingprocess += NursingProcessTools.getAsHTML(np, false, true, false, false);
                }
                listNP.clear();
            }
            resTXT += SYSConst.html_rectangle_around(nursingprocess);

            resTXT += "<hr/>";

            html += resTXT;
        }
        mapResidents.clear();
        listResident.clear();

        return html;
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
        HashSet<ResValue> resValues;

        private DB() {
            nreports = new HashSet<>();
            prescriptions = new HashSet<>();
            resInfos = new HashSet<>();
            nursingProcesses = new HashSet<>();
            resValues = new HashSet<>();
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

        public HashSet<ResValue> getResValues() {
            return resValues;
        }
    }

}
