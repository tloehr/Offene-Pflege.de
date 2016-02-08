package entity.info;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import entity.building.HomesTools;
import entity.files.SYSFilesTools;
import entity.nursingprocess.*;
import entity.prescription.*;
import entity.values.ResValue;
import entity.values.ResValueTools;
import entity.values.ResValueTypesTools;
import op.OPDE;
import op.care.info.PnlBodyScheme;
import op.system.AppInfo;
import op.system.PDF;
import op.threads.DisplayMessage;
import op.tools.Pair;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 03.06.13
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class TXEssenDoc {

    public static final String SOURCEDOC1 = "ueberleitungsbogen_121029.pdf";
    public static final String SOURCEMRE = "anlage_mre_130207.pdf";
    public static final String SOURCEPSYCH = "anlage_psych_080418.pdf";

    private HashMap<Integer, ResInfo> mapID2Info;
    private final HashMap<ResInfo, Properties> mapInfo2Properties;
    private Resident resident;
    private HashMap<String, String> content;
    private ArrayList<ResInfo> listICD;


    private final Font pdf_font_small = new Font(Font.FontFamily.HELVETICA, 8);
//    private final Font pdf_font_small_bold = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
//    private final Font pdf_font_normal_bold = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
//    private PdfContentByte over = null;
//    private PdfWriter writer = null;
//
    ByteArrayOutputStream medListStream = null, icdListStream = null;
    boolean mre, psych = false;int progress, max;


    String generalComment = "";

    public TXEssenDoc(Resident res) {
        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

        //    #docs  #mre  #psych  + allActive.size()
        max = 21 + 1 + 1;
        progress = 0;


        this.resident = res;
        content = new HashMap<String, String>();
        listICD = new ArrayList<ResInfo>();
        mapID2Info = new HashMap<Integer, ResInfo>();
        mapInfo2Properties = new HashMap<ResInfo, Properties>();


        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {

                for (ResInfo info : ResInfoTools.getAllActive(resident)) {

                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));

                    if (!info.isSingleIncident() && !info.isNoConstraints()) {
                        mapID2Info.put(info.getResInfoType().getType(), info);
                        mapInfo2Properties.put(info, load(info.getProperties()));
                    }
                    if (info.getResInfoType().getType() == ResInfoTypeTools.TYPE_DIAGNOSIS) {
                        listICD.add(info);
                        mapInfo2Properties.put(info, load(info.getProperties()));
                    }
                }

                try {

                    psych = mapID2Info.containsKey(ResInfoTypeTools.TYPE_PSYCH);

                    File file1 = createDoc1();
                    content.clear();
                    listICD.clear();

                    progress++;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
                    File filemre = null;
                    if (mre) {
                        filemre = createDocMRE();
                        content.clear();
                    }

                    progress++;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
                    File filepsych = null;
                    if (psych) {
                        filepsych = createDocPSYCH();
                        content.clear();
                    }

                    mapInfo2Properties.clear();


                    SYSFilesTools.handleFile(concatPDFFiles(file1, filemre, filepsych), Desktop.Action.OPEN);
                } catch (Exception e) {
                    OPDE.fatal(e);
                }


                return null;
            }

            @Override
            protected void done() {
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);
            }
        };

        worker.execute();


    }

    private File createDocPSYCH() throws Exception {
        File outfilePSYCH = File.createTempFile("TXE", ".pdf"); //new File(OPDE.getOPWD() + File.separator + OPDE.SUBDIR_CACHE + File.separator + "TXTXEAF.PSYCH_" + resident.getRID() + "_" + sdf.format(new Date()) + ".pdf");
        outfilePSYCH.deleteOnExit();
        PdfStamper stamper = new PdfStamper(new PdfReader(AppInfo.getTemplate(SOURCEPSYCH).getAbsolutePath()), new FileOutputStream(outfilePSYCH));
        createContent4PSYCH();

        AcroFields form = stamper.getAcroFields();
        for (String key : content.keySet()) {
            form.setField(key, content.get(key));
        }
        stamper.setFormFlattening(true);
        stamper.close();
        return outfilePSYCH;
    }

    private File createDocMRE() throws Exception {
        File outfileMRE = File.createTempFile("TXE", ".pdf"); //new File(OPDE.getOPWD() + File.separator + OPDE.SUBDIR_CACHE + File.separator + "TXTXEAF.MRE_" + resident.getRID() + "_" + sdf.format(new Date()) + ".pdf");
        outfileMRE.deleteOnExit();
        PdfStamper stamper = new PdfStamper(new PdfReader(AppInfo.getTemplate(SOURCEMRE).getAbsolutePath()), new FileOutputStream(outfileMRE));
        createContent4MRE();

        AcroFields form = stamper.getAcroFields();
        for (String key : content.keySet()) {
            form.setField(key, content.get(key));
        }
        stamper.setFormFlattening(true);
        stamper.close();
        return outfileMRE;
    }

    private File createDoc1() throws Exception {
        File outfile1 = File.createTempFile("TXE", ".pdf");//new File(OPDE.getOPWD() + File.separator + OPDE.SUBDIR_CACHE + File.separator + "TX1_" + resident.getRID() + "_" + sdf.format(new Date()) + ".pdf");
        outfile1.deleteOnExit();
        PdfStamper stamper = new PdfStamper(new PdfReader(AppInfo.getTemplate(SOURCEDOC1).getAbsolutePath()), new FileOutputStream(outfile1));

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section1();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section2();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section3();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section4();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section5();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section6();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section7();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section8();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section9();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section10();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section11();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section12();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section13();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section14();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section15();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section16();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section17();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section18();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Section19(stamper);

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4SectionICD();

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
        createContent4Meds();

        String docs = mre ? "MRE, " : "";
        docs += psych ? "PSYCH, " : "";
        docs = docs.isEmpty() ? "" : docs.substring(0, docs.length() - 2);

        content.put(TXEAF.DOCS_MISC, setCheckbox(mre || psych));
        content.put(TXEAF.PAGE3_DOCS_MISC, setCheckbox(mre || psych));

        content.put(TXEAF.DOCS_MISC_TEXT, docs);
        content.put(TXEAF.PAGE3_DOCS_MISC_TEXT, docs);


        AcroFields form = stamper.getAcroFields();
        for (String key : content.keySet()) {
            if (!ArrayUtils.contains(PnlBodyScheme.PARTS, key)) { // this is a special case. The bodyparts and the pdfkeys have the same name.
                OPDE.debug(key);
                form.setField(key, content.get(key));
            }
        }

//        TextField tf = new TextField(stamper.getWriter(), new Rectangle(Utilities.millimetersToPoints(1.3696f), Utilities.millimetersToPoints(2.4f), Utilities.millimetersToPoints(1.3696f + 9.25f), Utilities.millimetersToPoints(5.8f)), "asd");
//        tf.setText("Eine alte Dame geht fische essen.");
//        tf.setFont(pdf_font_normal_bold.getBaseFont());
//        tf.set
//        tf.setFontSize(0f);
//        stamper.addAnnotation(tf.getTextField(), 2);
//        tf.setVisibility(BaseField.VISIBLE);
//        tf.setBackgroundColor(BaseColor.RED);

        stamper.setFormFlattening(true);

        stamper.close();
        return outfile1;
    }

    /**
     * concats all the parts and puts a unified pagenumbering on every page
     *
     * @throws Exception
     */
    private File concatPDFFiles(File file1, File filemre, File filepsych) throws Exception {
        File outfileMain = File.createTempFile("TXE", ".pdf");
        outfileMain.deleteOnExit();
//        file1.deleteOnExit();

        Document document = new Document(PageSize.A4, Utilities.millimetersToPoints(0), Utilities.millimetersToPoints(0), Utilities.millimetersToPoints(0), Utilities.millimetersToPoints(0));
        PdfCopy copy = new PdfCopy(document, new FileOutputStream(outfileMain));
        document.open();
        int maxpages = 0;
        int runningPage = 0;
        PdfReader reader1 = new PdfReader(new FileInputStream(file1));
        maxpages += reader1.getNumberOfPages();


        PdfReader readerAdditionalMeds = medListStream == null ? null : new PdfReader(new ByteArrayInputStream(medListStream.toByteArray()));
        maxpages += readerAdditionalMeds == null ? 0 : readerAdditionalMeds.getNumberOfPages();

        PdfReader readerICD = icdListStream == null ? null : new PdfReader(new ByteArrayInputStream(icdListStream.toByteArray()));
        maxpages += readerICD == null ? 0 : readerICD.getNumberOfPages();

        PdfReader readerMRE = filemre == null ? null : new PdfReader(new FileInputStream(filemre));
        maxpages += readerMRE == null ? 0 : readerMRE.getNumberOfPages();

        PdfReader readerPSYCH = filepsych == null ? null : new PdfReader(new FileInputStream(filepsych));
        maxpages += readerPSYCH == null ? 0 : readerPSYCH.getNumberOfPages();

        PdfImportedPage page;
        PdfCopy.PageStamp stamp;
        for (int p = 1; p <= reader1.getNumberOfPages(); p++) {
            runningPage++;
            page = copy.getImportedPage(reader1, p);
            stamp = copy.createPageStamp(page);

            String sidenote = SYSTools.xx("pdf.pagefooter", runningPage, maxpages)
                    + " // " + ResidentTools.getLabelText(resident)
                    + " // " + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(new Date())
                    + " // " + SYSTools.xx("misc.msg.createdby") + ": " + (OPDE.getLogin() != null ? OPDE.getLogin().getUser().getFullname() : "")
                    + " // " + OPDE.getAppInfo().getProgname() + ", v" + OPDE.getAppInfo().getVersion();

            ColumnText.showTextAligned(stamp.getUnderContent(), Element.ALIGN_LEFT, new Phrase(sidenote, pdf_font_small), Utilities.millimetersToPoints(207), Utilities.millimetersToPoints(260), 270);
            stamp.alterContents();
            copy.addPage(page);
        }

        if (readerAdditionalMeds != null) {
            for (int p = 1; p <= readerAdditionalMeds.getNumberOfPages(); p++) {
                runningPage++;
                page = copy.getImportedPage(readerAdditionalMeds, p);
                stamp = copy.createPageStamp(page);
                String sidenote = SYSTools.xx("pdf.pagefooter", runningPage, maxpages)
                        + " // " + ResidentTools.getLabelText(resident)
                        + " // " + SYSTools.xx("misc.msg.createdby") + ": " + (OPDE.getLogin() != null ? OPDE.getLogin().getUser().getFullname() : "")
                        + " // " + OPDE.getAppInfo().getProgname() + ", v" + OPDE.getAppInfo().getVersion();
                ColumnText.showTextAligned(stamp.getUnderContent(), Element.ALIGN_LEFT, new Phrase(sidenote, pdf_font_small), Utilities.millimetersToPoints(207), Utilities.millimetersToPoints(260), 270);
                stamp.alterContents();
                copy.addPage(page);
            }
        }

        if (readerICD != null) {
            for (int p = 1; p <= readerICD.getNumberOfPages(); p++) {
                runningPage++;
                page = copy.getImportedPage(readerICD, p);
                stamp = copy.createPageStamp(page);
                String sidenote = SYSTools.xx("pdf.pagefooter", runningPage, maxpages)
                        + " // " + ResidentTools.getLabelText(resident)
                        + " // " + SYSTools.xx("misc.msg.createdby") + ": " + (OPDE.getLogin() != null ? OPDE.getLogin().getUser().getFullname() : "")
                        + " // " + OPDE.getAppInfo().getProgname() + ", v" + OPDE.getAppInfo().getVersion();
                ColumnText.showTextAligned(stamp.getUnderContent(), Element.ALIGN_LEFT, new Phrase(sidenote, pdf_font_small), Utilities.millimetersToPoints(207), Utilities.millimetersToPoints(260), 270);
                stamp.alterContents();
                copy.addPage(page);
            }
        }

        if (readerMRE != null) {
//            filemre.deleteOnExit();
            for (int p = 1; p <= readerMRE.getNumberOfPages(); p++) {
                runningPage++;
                page = copy.getImportedPage(readerMRE, p);
                stamp = copy.createPageStamp(page);
                String sidenote = SYSTools.xx("pdf.pagefooter", runningPage, maxpages)
                        + " // " + ResidentTools.getLabelText(resident)
                        + " // " + SYSTools.xx("misc.msg.createdby") + ": " + (OPDE.getLogin() != null ? OPDE.getLogin().getUser().getFullname() : "")
                        + " // " + OPDE.getAppInfo().getProgname() + ", v" + OPDE.getAppInfo().getVersion();
                ColumnText.showTextAligned(stamp.getUnderContent(), Element.ALIGN_LEFT, new Phrase(sidenote, pdf_font_small), Utilities.millimetersToPoints(207), Utilities.millimetersToPoints(260), 270);
                stamp.alterContents();
                copy.addPage(page);
            }
        }

        if (readerPSYCH != null) {
//            filepsych.deleteOnExit();
            for (int p = 1; p <= readerPSYCH.getNumberOfPages(); p++) {
                runningPage++;
                page = copy.getImportedPage(readerPSYCH, p);
                stamp = copy.createPageStamp(page);
                String sidenote = SYSTools.xx("pdf.pagefooter", runningPage, maxpages)
                        + " // " + ResidentTools.getLabelText(resident)
                        + " // " + SYSTools.xx("misc.msg.createdby") + ": " + (OPDE.getLogin() != null ? OPDE.getLogin().getUser().getFullname() : "")
                        + " // " + OPDE.getAppInfo().getProgname() + ", v" + OPDE.getAppInfo().getVersion();
                ColumnText.showTextAligned(stamp.getUnderContent(), Element.ALIGN_LEFT, new Phrase(sidenote, pdf_font_small), Utilities.millimetersToPoints(207), Utilities.millimetersToPoints(260), 270);
                stamp.alterContents();
                copy.addPage(page);
            }
        }

        document.close();
        return outfileMain;
    }


    /**
     * fills the usual stuff like resident name, insurances, dob and the rest on all three pages.
     * filling means, putting pairs into the content HashMap.
     * <p/>
     * Contains also "1 Soziale Aspekte"
     */
    private void createContent4Section1() {
        content.put(TXEAF.RESIDENT_GENDER, resident.getGender() == ResidentTools.MALE ? "2" : "1");
        content.put(TXEAF.RESIDENT_FIRSTNAME, resident.getFirstname());
        content.put(TXEAF.PAGE2_RESIDENT_FIRSTNAME, resident.getFirstname());
        content.put(TXEAF.RESIDENT_NAME, resident.getName());
        content.put(TXEAF.PAGE2_RESIDENT_NAME, resident.getName());
        content.put(TXEAF.PAGE3_RESIDENT_FULLNAME, ResidentTools.getFullName(resident));
        content.put(TXEAF.RESIDENT_DOB, DateFormat.getDateInstance().format(resident.getDOB()));
        content.put(TXEAF.PAGE2_RESIDENT_DOB, DateFormat.getDateInstance().format(resident.getDOB()));
        content.put(TXEAF.PAGE3_RESIDENT_DOB, DateFormat.getDateInstance().format(resident.getDOB()));

        content.put(TXEAF.DETAILED_REPORT_FOLLOWS, setCheckbox(false));

        content.put(TXEAF.PAGE2_USERNAME, (OPDE.getLogin() != null ? OPDE.getLogin().getUser().getFullname() : ""));
        content.put(TXEAF.PAGE3_USERNAME, (OPDE.getLogin() != null ? OPDE.getLogin().getUser().getFullname() : ""));

        content.put(TXEAF.PAGE3_RESIDENT_GP, GPTools.getCompleteAddress(resident.getGP()));

        if (resident.isActive()) {
            content.put(TXEAF.RESIDENT_STREET, resident.getStation().getHome().getStreet());
            content.put(TXEAF.RESIDENT_CITY, resident.getStation().getHome().getCity());
            content.put(TXEAF.RESIDENT_ZIP, resident.getStation().getHome().getZIP());
            content.put(TXEAF.RESIDENT_PHONE, resident.getStation().getHome().getTel());
            content.put(TXEAF.PAGE2_PHONE, resident.getStation().getHome().getTel());
            content.put(TXEAF.PAGE1_LOGO_TEXTFIELD, HomesTools.getAsTextForTX(resident.getStation().getHome()));
            content.put(TXEAF.PAGE3_LOGO_TEXTFIELD, HomesTools.getAsText(resident.getStation().getHome()));
        }

        content.put(TXEAF.TX_DATE, DateFormat.getDateInstance().format(new Date()));
        content.put(TXEAF.PAGE2_DATE, DateFormat.getDateInstance().format(new Date()));
        content.put(TXEAF.PAGE3_TX_DATE, DateFormat.getDateInstance().format(new Date()));
        content.put(TXEAF.PAGE3_TX2_DATE, DateFormat.getDateInstance().format(new Date()));
        content.put(TXEAF.PAGE3_TX3_DATE, DateFormat.getDateInstance().format(new Date()));
        content.put(TXEAF.TX_TIME, DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()));

        content.put(TXEAF.RESIDENT_HINSURANCE, getValue(ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "hiname"));
        content.put(TXEAF.PAGE3_RESIDENT_HINSURANCE, getValue(ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "hiname"));
        content.put(TXEAF.RESIDENT_HINSURANCEID, getValue(ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "personno"));
        content.put(TXEAF.RESIDENT_HINSURANCENO, getValue(ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "insuranceno"));

        content.put(TXEAF.COMMS_MOTHERTONGUE, getValue(ResInfoTypeTools.TYPE_COMMS, "mothertongue"));
        content.put(TXEAF.SOCIAL_RELIGION, getValue(ResInfoTypeTools.TYPE_PERSONALS, "konfession"));

        content.put(TXEAF.LEGAL_SINGLE, setCheckbox(getValue(ResInfoTypeTools.TYPE_PERSONALS, "single")));
        content.put(TXEAF.LEGAL_MINOR, setCheckbox(ResidentTools.isMinor(resident)));

        content.put(TXEAF.LC_GENERAL, setCheckbox(mapID2Info.containsKey(ResInfoTypeTools.TYPE_LEGALCUSTODIANS)));
        content.put(TXEAF.LC_FINANCE, setCheckbox(getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "finance")));
        content.put(TXEAF.LC_HEALTH, setCheckbox(getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "health")));
        content.put(TXEAF.LC_CUSTODY, setCheckbox(getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "confinement")));
        content.put(TXEAF.LC_NAME, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "name"));
        content.put(TXEAF.LC_FIRSTNAME, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "firstname"));
        content.put(TXEAF.LC_PHONE, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "tel"));
        content.put(TXEAF.LC_STREET, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "street"));
        content.put(TXEAF.LC_ZIP, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "zip"));
        content.put(TXEAF.LC_CITY, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "city"));

        content.put(TXEAF.CONFIDANT_NAME, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1name"));
        content.put(TXEAF.CONFIDANT_FIRSTNAME, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1firstname"));
        content.put(TXEAF.CONFIDANT_STREET, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1street"));
        content.put(TXEAF.CONFIDANT_ZIP, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1zip"));
        content.put(TXEAF.CONFIDANT_CITY, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1city"));
        content.put(TXEAF.CONFIDANT_PHONE, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1tel"));
        content.put(TXEAF.SOCIAL_CONFIDANT_CARE, setYesNoRadiobutton(getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1ready2nurse")));

        content.put(TXEAF.SOCIAL_CURRENT_RESTHOME, "1");

        content.put(TXEAF.DATE_REQUESTED_INSURANCE_GRADE, getValue(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "requestdate"));
        content.put(TXEAF.ASSIGNED_INSURANCE_GRADE, getValue(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "result"));

        String grade = getValue(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade");
        grade = grade.equalsIgnoreCase("refused") ? "none" : grade; // there is no such thing as refused request in this document.
        content.put(TXEAF.ASSIGNED_INSURANCE_GRADE, getValue(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "result"));
        content.put(TXEAF.INSURANCE_GRADE_NO, setCheckbox(grade.equalsIgnoreCase("none")));
        content.put(TXEAF.INSURANCE_GRADE_YES, setCheckbox(grade.equalsIgnoreCase("assigned")));
        content.put(TXEAF.INSURANCE_GRADE_REQUESTED, setCheckbox(grade.equalsIgnoreCase("requested")));
        content.put(TXEAF.DATE_REQUESTED_INSURANCE_GRADE_PAGE3, setCheckbox(grade.equalsIgnoreCase("requested")));
    }

    /**
     * valuables
     */
    private void createContent4Section2() {
        // nothing yet
    }

    /**
     * personal care
     */
    private void createContent4Section3() {
        content.put(TXEAF.PERSONAL_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_CARE, "personal.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(TXEAF.PERSONAL_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "personal.care.bed")));
        content.put(TXEAF.PERSONAL_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "personal.care.shower")));
        content.put(TXEAF.PERSONAL_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "personal.care.basin")));
        content.put(TXEAF.MOUTH_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "mouth.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(TXEAF.MOUTH_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "mouth.care.bed")));
        content.put(TXEAF.MOUTH_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "mouth.care.shower")));
        content.put(TXEAF.MOUTH_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "mouth.care.basin")));
        content.put(TXEAF.DENTURE_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "denture.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(TXEAF.DENTURE_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "denture.care.bed")));
        content.put(TXEAF.DENTURE_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "denture.care.shower")));
        content.put(TXEAF.DENTURE_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "denture.care.basin")));
        content.put(TXEAF.COMBING_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_CARE, "combing.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(TXEAF.COMBING_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "combing.care.bed")));
        content.put(TXEAF.COMBING_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "combing.care.shower")));
        content.put(TXEAF.COMBING_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "combing.care.basin")));
        content.put(TXEAF.SHAVE_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_CARE, "shave.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(TXEAF.SHAVE_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "shave.care.bed")));
        content.put(TXEAF.SHAVE_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "shave.care.shower")));
        content.put(TXEAF.SHAVE_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "shave.care.basin")));
        content.put(TXEAF.DRESSING_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_CARE, "dressing.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(TXEAF.DRESSING_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "dressing.care.bed")));
        content.put(TXEAF.DRESSING_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "dressing.care.shower")));
        content.put(TXEAF.DRESSING_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "dressing.care.basin")));

        content.put(TXEAF.SKIN_DRY, setCheckbox(getValue(ResInfoTypeTools.TYPE_SKIN, "skin.dry")));
        content.put(TXEAF.SKIN_GREASY, setCheckbox(getValue(ResInfoTypeTools.TYPE_SKIN, "skin.greasy")));
        content.put(TXEAF.SKIN_ITCH, setCheckbox(getValue(ResInfoTypeTools.TYPE_SKIN, "skin.itch")));
        content.put(TXEAF.SKIN_NORMAL, setCheckbox(getValue(ResInfoTypeTools.TYPE_SKIN, "skin.normal")));

        content.put(TXEAF.PREFERRED_CAREPRODUCTS, getValue(ResInfoTypeTools.TYPE_CARE, "preferred.careproducts"));
        content.put(TXEAF.PERSONAL_CARE_COMMENT, getComment(ResInfoTypeTools.TYPE_CARE));

    }


    /**
     * mobilty
     */
    private void createContent4Section4() {
        content.put(TXEAF.MOBILITY_GET_UP, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "stand"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(TXEAF.MOBILITY_AID_GETUP, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "stand.aid")));
        content.put(TXEAF.MOBILITY_WALKING, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "walk"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(TXEAF.MOBILITY_AID_WALKING, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "walk.aid")));
        content.put(TXEAF.MOBILITY_TRANSFER, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "transfer"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(TXEAF.MOBILITY_AID_TRANSFER, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "transfer.aid")));
        content.put(TXEAF.MOBILITY_TOILET, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "toilet"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(TXEAF.MOBILITY_AID_TOILET, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "toilet.aid")));
        content.put(TXEAF.MOBILITY_SITTING, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "sitting"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(TXEAF.MOBILITY_AID_SITTING, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "sitting.aid")));
        content.put(TXEAF.MOBILITY_BED, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "bedmovement"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(TXEAF.MOBILITY_AID_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "bedmovement.aid")));

        content.put(TXEAF.MOBILITY_AID_CRUTCH, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "crutch.aid")));
        content.put(TXEAF.MOBILITY_AID_CANE, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "cane.aid")));
        content.put(TXEAF.MOBILITY_AID_WHEELCHAIR, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "wheel.aid")));
        content.put(TXEAF.MOBILITY_AID_COMMODE, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "commode.aid"))); // <- from other section
        content.put(TXEAF.MOBILITY_AID_WALKER, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "walker.aid")));
        content.put(TXEAF.MOBILITY_AID_COMMENT, getValue(ResInfoTypeTools.TYPE_MOBILITY, "other.aid"));

        content.put(TXEAF.MOBILITY_BEDRIDDEN, setYesNoRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "bedridden")));

        content.put(TXEAF.MOBILITY_COMMENT, getComment(ResInfoTypeTools.TYPE_MOBILITY));

        String mobilityMeasures = "";
        long prev = -1;

        ArrayList<InterventionSchedule> listSchedule = InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_MOBILITY);
        listSchedule.addAll(InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_BEDSORE));

        for (InterventionSchedule is : listSchedule) {
            if (is.getIntervention().getMassID() != prev) {
                prev = is.getIntervention().getMassID();
                if (!mobilityMeasures.isEmpty()) {
                    mobilityMeasures = mobilityMeasures.substring(0, mobilityMeasures.length() - 2) + "; ";
                }
                mobilityMeasures += is.getIntervention().getBezeichnung() + ": ";
            }
            mobilityMeasures += InterventionScheduleTools.getTerminAsCompactText(is) + ", ";
        }

        listSchedule.clear();

        content.put(TXEAF.MOBILITY_BEDPOSITION, mobilityMeasures.isEmpty() ? "--" : mobilityMeasures.substring(0, mobilityMeasures.length() - 2)); // the last ", " has to be cut off again
    }

    /**
     * excretiions
     */
    private void createContent4Section5() {

        boolean weightControl = !PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_WEIGHT_MONITORING).isEmpty() ||
                !InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_WEIGHT_MONITORING).isEmpty();
        content.put(TXEAF.EXCRETIONS_CONTROL_WEIGHT, setCheckbox(weightControl));
        content.put(TXEAF.MONITORING_WEIGHT, setCheckbox(weightControl));

        Properties controlling = resident.getControlling();
        content.put(TXEAF.EXCRETIONS_LIQUID_BALANCE, setYesNoRadiobutton(SYSTools.catchNull(controlling.getProperty(ResidentTools.KEY_BALANCE)).equalsIgnoreCase("on")));

        content.put(TXEAF.EXCRETIONS_AID_BEDPAN, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "bedpan.aid")));
        content.put(TXEAF.EXCRETIONS_AID_COMMODE, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "commode.aid")));
        content.put(TXEAF.EXCRETIONS_AID_URINAL, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "urinal.aid")));


        boolean noAid = getValue(ResInfoTypeTools.TYPE_INCOAID, "bedpan.aid").equalsIgnoreCase("false") &&
                getValue(ResInfoTypeTools.TYPE_INCOAID, "commode.aid").equalsIgnoreCase("false") &&
                getValue(ResInfoTypeTools.TYPE_INCOAID, "urinal.aid").equalsIgnoreCase("false");
        content.put(TXEAF.EXCRETIONS_AID_NO, setCheckbox(noAid));

        boolean normal = getValue(ResInfoTypeTools.TYPE_EXCRETIONS, "diarrhoe").equalsIgnoreCase("false") &&
                getValue(ResInfoTypeTools.TYPE_EXCRETIONS, "digital").equalsIgnoreCase("false") &&
                getValue(ResInfoTypeTools.TYPE_EXCRETIONS, "obstipation").equalsIgnoreCase("false");
        content.put(TXEAF.EXCRETIONS_DIARRHOEA_TENDENCY, setCheckbox(getValue(ResInfoTypeTools.TYPE_EXCRETIONS, "diarrhoe")));
        content.put(TXEAF.EXCRETIONS_OBSTIPATION_TENDENCY, setCheckbox(getValue(ResInfoTypeTools.TYPE_EXCRETIONS, "obstipation")));
        content.put(TXEAF.EXCRETIONS_DIGITAL, setCheckbox(getValue(ResInfoTypeTools.TYPE_EXCRETIONS, "digital")));
        content.put(TXEAF.EXCRETIONS_AP_AID, setCheckbox(getValue(ResInfoTypeTools.TYPE_EXCRETIONS, "ap.aid")));
        content.put(TXEAF.EXCRETIONS_NORMAL, setCheckbox(normal));
        content.put(TXEAF.EXCRETIONS_COMMENT, getComment(ResInfoTypeTools.TYPE_EXCRETIONS));

        content.put(TXEAF.EXCRETIONS_TUBESIZE_CH, getValue(ResInfoTypeTools.TYPE_INCOAID, "tubesize"));

        boolean inco_urine = (mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_PROFILE_DAY) && !getValue(ResInfoTypeTools.TYPE_INCO_PROFILE_DAY, "inkoprofil").equalsIgnoreCase("kontinenz")) ||
                (mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT) && !getValue(ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT, "inkoprofil").equalsIgnoreCase("kontinenz"));
        boolean inco_faecal = (mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_FAECAL) && !getValue(ResInfoTypeTools.TYPE_INCO_FAECAL, "incolevel").equalsIgnoreCase("0"));
        content.put(TXEAF.EXCRETIONS_INCO_URINE, setYesNoRadiobutton(inco_urine));
        content.put(TXEAF.EXCRETIONS_INCO_FAECAL, setYesNoRadiobutton(inco_faecal));

        content.put(TXEAF.EXCRETIONS_INCOAID_NEEDSHELP, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "needshelp")));
        content.put(TXEAF.EXCRETIONS_INCOAID_SELF, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "needshelp").equalsIgnoreCase("false")));
        content.put(TXEAF.EXCRETIONS_TRANS_AID, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "trans.aid")));
        content.put(TXEAF.EXCRETIONS_SUP_AID, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "sup.aid")));

        boolean diapers = getValue(ResInfoTypeTools.TYPE_INCOAID, "windel").equalsIgnoreCase("true");
        boolean pads1 = getValue(ResInfoTypeTools.TYPE_INCOAID, "vorlagen1").equalsIgnoreCase("true") ||
                getValue(ResInfoTypeTools.TYPE_INCOAID, "vorlagen2").equalsIgnoreCase("true") ||
                getValue(ResInfoTypeTools.TYPE_INCOAID, "vorlagen3").equalsIgnoreCase("true");
        boolean pads2 = getValue(ResInfoTypeTools.TYPE_INCOAID, "dbinden").equalsIgnoreCase("true");
        boolean undersheet = getValue(ResInfoTypeTools.TYPE_INCOAID, "krunterlagen").equalsIgnoreCase("true");

        String incoaidtext = (diapers ? SYSTools.xx("misc.msg.diaper") + ", " : "") +
                (pads1 ? SYSTools.xx("misc.msg.incopad") + ", " : "") +
                (pads2 ? SYSTools.xx("misc.msg.sanitarypads") + ", " : "") +
                (undersheet ? SYSTools.xx("misc.msg.undersheet") + ", " : "");

        content.put(TXEAF.EXCRETIONS_ONEWAY_AID, setCheckbox(diapers || pads1 || pads2 || undersheet));
        content.put(TXEAF.EXCRETIONS_CURRENT_USED_AID, incoaidtext.isEmpty() ? "--" : incoaidtext.substring(0, incoaidtext.length() - 2));

        ArrayList<Prescription> presCatheterChange = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_CATHETER_CHANGE);
        if (!presCatheterChange.isEmpty()) {
            Date lastChange = SYSConst.DATE_THE_VERY_BEGINNING;
            for (Prescription prescription : presCatheterChange) { // usually there shouldn't be more than 1, but you never know
                BHP bhp = BHPTools.getLastBHP(prescription);
                if (bhp != null) {
                    lastChange = new Date(Math.max(lastChange.getTime(), BHPTools.getLastBHP(prescription).getIst().getTime()));
                }
            }
            if (!lastChange.equals(SYSConst.DATE_THE_VERY_BEGINNING)) {
                content.put(TXEAF.EXCRETIONS_LASTCHANGE, DateFormat.getDateInstance().format(lastChange));
            } else {
                content.put(TXEAF.EXCRETIONS_LASTCHANGE, "--");
            }
        }
        presCatheterChange.clear();
    }

    private void createContent4Section6() {
        content.put(TXEAF.PROPH_CONTRACTURE, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_CONTRACTURE).isEmpty()));
        content.put(TXEAF.PROPH_BEDSORE, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_BEDSORE).isEmpty()));
        content.put(TXEAF.PROPH_SOOR, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_SOOR).isEmpty()));
        content.put(TXEAF.PROPH_THROMBOSIS, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_THROMBOSIS).isEmpty()));
        content.put(TXEAF.PROPH_PNEUMONIA, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_PNEUMONIA).isEmpty()));
        content.put(TXEAF.PROPH_INTERTRIGO, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_INTERTRIGO).isEmpty()));
        content.put(TXEAF.PROPH_FALL, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_FALL).isEmpty()));
        content.put(TXEAF.PROPH_OBSTIPATION, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_OBSTIPATION).isEmpty()));
    }

    /**
     * bedsore
     */
    private void createContent4Section7() {
        String braden = "Braden: " + getValue(ResInfoTypeTools.TYPE_SCALE_BRADEN, "scalesum") + " (" + getValue(ResInfoTypeTools.TYPE_SCALE_BRADEN, "risk") + ")";
        content.put(TXEAF.RISKSCALE_TYPE_BEDSORE, braden);
        int rating;
        try {
            rating = Integer.parseInt(getValue(ResInfoTypeTools.TYPE_SCALE_BRADEN, "rating"));
        } catch (NumberFormatException nfe) {
            rating = 0;
        }
        content.put(TXEAF.SCALE_RISK_BEDSORE, setYesNoRadiobutton(rating > 0));

        boolean bedsore = false;
        for (int type : ResInfoTypeTools.TYPE_ALL_WOUNDS) {
            bedsore |= getValue(type, "bedsore").equalsIgnoreCase("true");
        }
        content.put(TXEAF.BEDSORE, setYesNoRadiobutton(bedsore));
    }

    /**
     * sleep
     */
    private void createContent4Section8() {
        content.put(TXEAF.SLEEP_NORMAL, setCheckbox(getValue(ResInfoTypeTools.TYPE_SLEEP, "normal")));

        boolean insomnia = getValue(ResInfoTypeTools.TYPE_SLEEP, "einschlaf").equalsIgnoreCase("true") || getValue(ResInfoTypeTools.TYPE_SLEEP, "durchschlaf").equalsIgnoreCase("true");
        content.put(TXEAF.SLEEP_INSOMNIA, setCheckbox(insomnia));

        boolean restless = getValue(ResInfoTypeTools.TYPE_SLEEP, "unruhe").equalsIgnoreCase("true") || getValue(ResInfoTypeTools.TYPE_SLEEP, "daynight").equalsIgnoreCase("true");
        content.put(TXEAF.SLEEP_RESTLESS, setCheckbox(restless));

        content.put(TXEAF.SLEEP_POS_LEFT, setCheckbox(getValue(ResInfoTypeTools.TYPE_SLEEP, "left")));
        content.put(TXEAF.SLEEP_POS_FRONT, setCheckbox(getValue(ResInfoTypeTools.TYPE_SLEEP, "front")));
        content.put(TXEAF.SLEEP_POS_BACK, setCheckbox(getValue(ResInfoTypeTools.TYPE_SLEEP, "back")));
        content.put(TXEAF.SLEEP_POS_RIGHT, setCheckbox(getValue(ResInfoTypeTools.TYPE_SLEEP, "right")));

        content.put(TXEAF.SLEEP_COMMENTS, getValue(ResInfoTypeTools.TYPE_SLEEP, "schlafhilfen"));

    }

    /**
     * food
     */
    private void createContent4Section9() {
        content.put(TXEAF.FOOD_ASSISTANCE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_FOOD, "assistancelevelfood"), new String[]{"none", "needsmotivation", "needshelp", "completehelp"}));
        content.put(TXEAF.FOOD_DYSPHAGIA, setCheckbox(getValue(ResInfoTypeTools.TYPE_FOOD, "dysphagia")));
        content.put(TXEAF.FOOD_BITESIZE, setCheckbox(getValue(ResInfoTypeTools.TYPE_FOOD, "bitesize")));

        long lastMeal = 0;

        BHP bhp = BHPTools.getLastBHP(resident, InterventionTools.FLAG_FOOD_CONSUMPTION);
        DFN dfn = DFNTools.getLastDFN(resident, InterventionTools.FLAG_FOOD_CONSUMPTION);

        lastMeal = Math.max((bhp == null ? 0 : bhp.getIst().getTime()), (dfn == null ? 0 : dfn.getIst().getTime()));

        if (lastMeal != 0) {
            content.put(TXEAF.FOOD_LAST_MEAL, DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(lastMeal));
        } else {
            content.put(TXEAF.FOOD_LAST_MEAL, "--");
        }

        BigDecimal bd200 = new BigDecimal(200);
        BigDecimal bd100 = new BigDecimal(100);
        BigDecimal bd250 = new BigDecimal(250);
        BigDecimal bd500 = new BigDecimal(500);
        BigDecimal bd750 = new BigDecimal(750);
        BigDecimal bd1000 = new BigDecimal(1000);
        BigDecimal foodml = BigDecimal.ZERO;
        ArrayList<Prescription> listPresGavageFood = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GAVAGE_FOOD_250ML);
        for (Prescription p : listPresGavageFood) {
            for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                foodml = foodml.add(ps.getOverAllDoseSum().multiply(bd250));
            }
        }
        listPresGavageFood = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GAVAGE_FOOD_500ML);
        for (Prescription p : listPresGavageFood) {
            for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                foodml = foodml.add(ps.getOverAllDoseSum().multiply(bd500));
            }
        }
        listPresGavageFood = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GAVAGE_FOOD_750ML);
        for (Prescription p : listPresGavageFood) {
            for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                foodml = foodml.add(ps.getOverAllDoseSum().multiply(bd750));
            }
        }
        listPresGavageFood = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GAVAGE_FOOD_1000ML);
        for (Prescription p : listPresGavageFood) {
            for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                foodml = foodml.add(ps.getOverAllDoseSum().multiply(bd1000));
            }
        }
        listPresGavageFood = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GAVAGE_FOOD_100ML);
        for (Prescription p : listPresGavageFood) {
            for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                foodml = foodml.add(ps.getOverAllDoseSum().multiply(bd100));
            }
        }
        listPresGavageFood = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GAVAGE_FOOD_200ML);
        for (Prescription p : listPresGavageFood) {
            for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                foodml = foodml.add(ps.getOverAllDoseSum().multiply(bd200));
            }
        }
        listPresGavageFood.clear();

        BigDecimal liquidml = BigDecimal.ZERO;
        ArrayList<Prescription> listPresGavageLiquid = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GAVAGE_LIQUID_250ML);
        for (Prescription p : listPresGavageLiquid) {
            for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                liquidml = liquidml.add(ps.getOverAllDoseSum().multiply(bd250));
            }
        }
        listPresGavageLiquid = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GAVAGE_LIQUID_500ML);
        for (Prescription p : listPresGavageLiquid) {
            for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                liquidml = liquidml.add(ps.getOverAllDoseSum().multiply(bd500));
            }
        }
        listPresGavageLiquid = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GAVAGE_LIQUID_750ML);
        for (Prescription p : listPresGavageLiquid) {
            for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                liquidml = liquidml.add(ps.getOverAllDoseSum().multiply(bd750));
            }
        }
        listPresGavageLiquid = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GAVAGE_LIQUID_1000ML);
        for (Prescription p : listPresGavageLiquid) {
            for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                liquidml = liquidml.add(ps.getOverAllDoseSum().multiply(bd1000));
            }
        }
        listPresGavageLiquid = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GAVAGE_LIQUID_100ML);
        for (Prescription p : listPresGavageLiquid) {
            for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                liquidml = liquidml.add(ps.getOverAllDoseSum().multiply(bd100));
            }
        }
        listPresGavageLiquid = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GAVAGE_LIQUID_200ML);
        for (Prescription p : listPresGavageLiquid) {
            for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                liquidml = liquidml.add(ps.getOverAllDoseSum().multiply(bd200));
            }
        }
        listPresGavageLiquid.clear();

        content.put(TXEAF.FOOD_ARTIFICIAL_FEEDING, setYesNoRadiobutton(foodml.compareTo(BigDecimal.ZERO) > 0));
        content.put(TXEAF.FOOD_DAILY_ML, setBD(foodml));
        content.put(TXEAF.FOOD_TEE_DAILY_ML, setBD(liquidml));

        String tubetype = "--";
        if (!getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "bitesize").equals("--")) {
            //todo: this is horrible
            String langKey = "misc.msg." + getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "tubetype");
            tubetype = SYSTools.xx(langKey);
        }

        content.put(TXEAF.FOOD_TUBETYPE, tubetype);
        content.put(TXEAF.FOOD_TUBESINCE, getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "tubesince"));
        content.put(TXEAF.FOOD_PUMP, setCheckbox(getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "pump")));
        content.put(TXEAF.FOOD_SYRINGE, setCheckbox(getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "syringe")));
        content.put(TXEAF.FOOD_GRAVITY, setCheckbox(getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "gravity")));

        content.put(TXEAF.FOOD_DAILY_KCAL, getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "calories"));
        content.put(TXEAF.FOOD_ORALNUTRITION, setYesNoRadiobutton(getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "oralnutrition")));
        content.put(TXEAF.FOOD_BREADUNTIS, getValue(ResInfoTypeTools.TYPE_FOOD, "breadunit"));
        content.put(TXEAF.FOOD_LIQUIDS_DAILY_ML, getValue(ResInfoTypeTools.TYPE_FOOD, "zieltrinkmenge"));


        // BMI
        ResValue weight = ResValueTools.getLast(resident, ResValueTypesTools.WEIGHT);
        ResValue height = ResValueTools.getLast(resident, ResValueTypesTools.HEIGHT);


//        String bmi = "--";
        BigDecimal bmi = null;
        String remark = "";
        if (weight != null && height != null) {
            ResInfo amputation = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.TYPE_AMPUTATION);
            BigDecimal adjustmentPercentage = ResInfoTools.getWeightAdjustmentPercentage(amputation);
            BigDecimal theoreticalweight = weight.getVal1();
            if (adjustmentPercentage.equals(BigDecimal.ZERO)) {
                bmi = ResValueTools.getBMI(theoreticalweight, height.getVal1());
            } else {
                theoreticalweight = weight.getVal1().multiply(BigDecimal.ONE.add(adjustmentPercentage.multiply(new BigDecimal(0.01))));
                bmi = ResValueTools.getBMI(theoreticalweight, height.getVal1());

                generalComment += "*) Das Körpergewicht bei der Berechnung des BMI musste aufgrund von Amputationen angepasst werden:\n";
                generalComment += "Mess-Gewicht: " + weight.getVal1().setScale(2, RoundingMode.HALF_UP) + " " + weight.getType().getUnit1() + " (" + DateFormat.getDateInstance().format(weight.getPit()) + "), ";
                generalComment += "Prozentuale Anpassung: " + adjustmentPercentage.setScale(2, RoundingMode.HALF_UP) + "%, ";
                generalComment += "Theoretisches Gewicht: " + theoreticalweight.setScale(2, RoundingMode.HALF_UP) + " " + weight.getType().getUnit1() + "\n";
                generalComment += "Amputation: " + ResInfoTools.getAmputationAsCompactText(amputation);

                remark = "*)";
            }


        }
        content.put(TXEAF.FOOD_BMI, setBD(bmi) + remark);


        content.put(TXEAF.FOOD_PARENTERAL, setCheckbox(getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "parenteral")));
        content.put(TXEAF.FOOD_DRINKSALONE, setCheckbox(getValue(ResInfoTypeTools.TYPE_FOOD, "drinksalone")));
        content.put(TXEAF.FOOD_ABROSIA, setCheckbox(getValue(ResInfoTypeTools.TYPE_FOOD, "abrosia")));
        content.put(TXEAF.FOOD_DRINKINGMOTIVATION, setCheckbox(getValue(ResInfoTypeTools.TYPE_FOOD, "motivationdrinking")));

    }

    /**
     * special aspects
     */
    private void createContent4Section10() {

        boolean confirmed = getValue(ResInfoTypeTools.TYPE_INFECTION, "confirmed").equalsIgnoreCase("true");
        boolean notchecked = getValue(ResInfoTypeTools.TYPE_INFECTION, "notchecked").equalsIgnoreCase("true");

        String rb = "1";
        if (confirmed) {
            rb = "2";
        }
        if (notchecked) {
            rb = "3";
        }

        content.put(TXEAF.SPECIAL_MRE, rb);

        mre = getValue(ResInfoTypeTools.TYPE_INFECTION, "mre").equalsIgnoreCase("true") ||
                getValue(ResInfoTypeTools.TYPE_INFECTION, "vre").equalsIgnoreCase("true") ||
                getValue(ResInfoTypeTools.TYPE_INFECTION, "esbl").equalsIgnoreCase("true") ||
                getValue(ResInfoTypeTools.TYPE_INFECTION, "mrsa").equalsIgnoreCase("true");

        content.put(TXEAF.NO_INFECTIONS_CERTIFIED, setCheckbox(!mapID2Info.containsKey(ResInfoTypeTools.TYPE_INFECTION)));

        content.put(TXEAF.SPECIAL_YESNO_ALLERGY, setYesNoRadiobutton(mapID2Info.containsKey(ResInfoTypeTools.TYPE_ALLERGY)));
        content.put(TXEAF.SPECIAL_ALLERGIEPASS, setCheckbox(getValue(ResInfoTypeTools.TYPE_ALLERGY, "allergiepass")));
        content.put(TXEAF.SPECIAL_COMMENT_ALLERGY, getValue(ResInfoTypeTools.TYPE_ALLERGY, "beschreibung"));

        content.put(TXEAF.SPECIAL_MYCOSIS, setYesNoRadiobutton(mapID2Info.containsKey(ResInfoTypeTools.TYPE_MYCOSIS)));

        // palliative is not set

        content.put(TXEAF.SPECIAL_WOUNDS, setYesNoRadiobutton(hasWounds()));
        content.put(TXEAF.SPECIAL_WOUNDPAIN, setYesNoRadiobutton(hasWoundPain()));
        content.put(TXEAF.SPECIAL_PACER, setYesNoRadiobutton(getValue(ResInfoTypeTools.TYPE_PACEMAKER, "pacemaker")));
        content.put(TXEAF.SPECIAL_LASTCONTROL_PACER, getValue(ResInfoTypeTools.TYPE_PACEMAKER, "lastcheck"));
    }

    /**
     * consciousness
     */
    private void createContent4Section11() {
        content.put(TXEAF.CONSCIOUSNESS_AWAKE, setCheckbox(getValue(ResInfoTypeTools.TYPE_CONSCIUOS, "awake")));
        content.put(TXEAF.CONSCIOUSNESS_SOPOR, setCheckbox(getValue(ResInfoTypeTools.TYPE_CONSCIUOS, "sopor")));
        content.put(TXEAF.CONSCIOUSNESS_COMA, setCheckbox(getValue(ResInfoTypeTools.TYPE_CONSCIUOS, "coma")));
        content.put(TXEAF.CONSCIOUSNESS_SOMNOLENT, setCheckbox(getValue(ResInfoTypeTools.TYPE_CONSCIUOS, "somnolent")));

        content.put(TXEAF.COMMS_SPEECH_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_COMMS, "ability1"), new String[]{"oE1", "mE1", "zE1"}));
        content.put(TXEAF.COMMS_UNDERSTANDING_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_COMMS, "ability2"), new String[]{"oE2", "mE2", "zE2"}));
        content.put(TXEAF.COMMS_HEARING_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_COMMS, "ability3"), new String[]{"oE3", "mE3", "zE3"}));
        content.put(TXEAF.COMMS_SEEING_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_COMMS, "ability4"), new String[]{"oE4", "mE4", "zE4"}));
        content.put(TXEAF.COMMS_WRITING_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_COMMS, "ability5"), new String[]{"oE5", "mE5", "zE5"}));

        content.put(TXEAF.ORIENTATION_TIME_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_ORIENTATION, "time"), new String[]{"yes1", "no1", "intermittent1"}));
        content.put(TXEAF.ORIENTATION_PERSONAL_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_ORIENTATION, "personal"), new String[]{"yes2", "no2", "intermittent2"}));
        content.put(TXEAF.ORIENTATION_LOCATION_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_ORIENTATION, "location"), new String[]{"yes3", "no3", "intermittent3"}));
        content.put(TXEAF.ORIENTATION_SITUATION_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_ORIENTATION, "situation"), new String[]{"yes4", "no4", "intermittent4"}));
        content.put(TXEAF.ORIENTATION_RUNNAWAY_TENDENCY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_ORIENTATION, "runaway"), new String[]{"yes5", "no5", "intermittent5"}));
    }


    /**
     * respiration
     */
    private void createContent4Section12() {
        content.put(TXEAF.RESPIRATION_NORMAL, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "awake")));
        content.put(TXEAF.RESPIRATION_CARDCONGEST, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "cardcongest")));
        content.put(TXEAF.RESPIRATION_PAIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "pain")));
        content.put(TXEAF.RESPIRATION_COUGH, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "cough")));
        content.put(TXEAF.RESPIRATION_MUCOUS, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "mucous")));
        content.put(TXEAF.RESPIRATION_SPUTUM, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "sputum")));
        content.put(TXEAF.RESPIRATION_SMOKING, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "smoking")));
        content.put(TXEAF.RESPIRATION_ASTHMA, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "asthma")));

        content.put(TXEAF.RESPIRATION_OTHER, setCheckbox(!getValue(ResInfoTypeTools.TYPE_RESPIRATION, "other").equals("--")));
        content.put(TXEAF.RESPIRATION_COMMENT, getValue(ResInfoTypeTools.TYPE_RESPIRATION, "other"));

        content.put(TXEAF.RESPIRATION_STOMA, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "stoma")));
        content.put(TXEAF.RESPIRATION_SILVERTUBE, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "silver")));
        content.put(TXEAF.RESPIRATION_SILICONTUBE, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "silicon")));
        content.put(TXEAF.RESPIRATION_ASPIRATE, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "aspirate")));

        content.put(TXEAF.RESPIRATION_TUBESIZE, getValue(ResInfoTypeTools.TYPE_RESPIRATION, "tubesize"));
        content.put(TXEAF.RESPIRATION_TUBETYPE, getValue(ResInfoTypeTools.TYPE_RESPIRATION, "tubetype"));

    }

    /**
     * special monitoring
     */
    private void createContent4Section13() {
        boolean bp = !PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_BP_MONITORING).isEmpty();
        boolean port = !PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PORT_MONITORING).isEmpty();
        boolean respiration = !PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_BREATH_MONITORING).isEmpty();
        boolean pulse = !PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PULSE_MONITORING).isEmpty();
        boolean temp = !PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_TEMP_MONITORING).isEmpty();
        boolean weight = !PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_WEIGHT_MONITORING).isEmpty();
        boolean pain = !InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PAIN_MONITORING).isEmpty();

        content.put(TXEAF.MONITORING_BP, setCheckbox(bp));
        content.put(TXEAF.MONITORING_PORT, setCheckbox(port));
        content.put(TXEAF.MONITORING_RESPIRATION, setCheckbox(respiration));
        content.put(TXEAF.MONITORING_PULSE, setCheckbox(pulse));
        content.put(TXEAF.MONITORING_TEMP, setCheckbox(temp));
        content.put(TXEAF.MONITORING_WEIGHT, setCheckbox(weight));
        content.put(TXEAF.MONITORING_PAIN, setCheckbox(pain));

        Properties controlling = resident.getControlling();
        content.put(TXEAF.MONITORING_INTAKE, setCheckbox(SYSTools.catchNull(controlling.getProperty("liquidbalance")).equalsIgnoreCase("on")));
        content.put(TXEAF.MONITORING_EXCRETION, setCheckbox(SYSTools.catchNull(controlling.getProperty("liquidbalance")).equalsIgnoreCase("on")));

    }

    /**
     * special monitoring
     */
    private void createContent4Section14() {
        boolean logo = !InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_THERAPY_LOGOPEDICS).isEmpty();
        boolean physio = !InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_THERAPY_PHYSIO).isEmpty();
        boolean ergo = !InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_THERAPY_ERGO).isEmpty();

        content.put(TXEAF.THERAPY_ERGO, setCheckbox(ergo));
        content.put(TXEAF.THERAPY_LOGO, setCheckbox(logo));
        content.put(TXEAF.THERAPY_PHYSIO, setCheckbox(physio));
    }


    private void createContent4Section15() {
        // nothing yet
    }


    /**
     * meds / diabetes
     */
    private void createContent4Section16() {
        content.put(TXEAF.MEDS_INSULIN_APPLICATION, setRadiobutton(getValue(ResInfoTypeTools.TYPE_DIABETES, "application"), new String[]{"pen", "syringe", "pump", "none"}));
        content.put(TXEAF.MEDS_INJECTION_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MEDS, "injection"), new String[]{"none", "lvl1", "lvl3"}));
        content.put(TXEAF.MEDS_SELF, setCheckbox(getValue(ResInfoTypeTools.TYPE_MEDS, "self")));
        content.put(TXEAF.MEDS_DAILY_RATION, setCheckbox(getValue(ResInfoTypeTools.TYPE_MEDS, "dailyration")));
        content.put(TXEAF.MEDS_CONTROL, setCheckbox(getValue(ResInfoTypeTools.TYPE_MEDS, "control")));
        content.put(TXEAF.MEDS_MARCUMARPASS, setYesNoRadiobutton(getValue(ResInfoTypeTools.TYPE_MEDS, "marcumarpass")));

        BHP lastMed = BHPTools.getLastBHP(resident, InterventionTools.FLAG_MEDS_APPLICATION);
        if (lastMed != null) {
            content.put(TXEAF.MEDS_LAST_APPLICATION, DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(lastMed.getIst()));
        } else {
            content.put(TXEAF.MEDS_LAST_APPLICATION, "--");
        }


        ArrayList<Prescription> listGlucose = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GLUCOSE_MONITORING);
        if (!listGlucose.isEmpty()) {
            int daily = 0, weekly = 0;
            BigDecimal morning = BigDecimal.ZERO, noon = BigDecimal.ZERO, evening = BigDecimal.ZERO;
            for (Prescription p : listGlucose) {
                for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                    if (ps.getTaeglich() == 1) {
                        daily++;
                        morning = morning.add(ps.getMorningSum());
                        noon = noon.add(ps.getNoonSum());
                        evening = evening.add(ps.getEveningSum());
                    } else if (ps.getTaeglich() > 1) {
                        weekly += 7 / ps.getTaeglich();
                        morning = morning.add(ps.getMorningSum());
                        noon = noon.add(ps.getNoonSum());
                        evening = evening.add(ps.getEveningSum());
                    }
                }
            }
            content.put(TXEAF.MEDS_MORNING_GLUCOSE, setCheckbox(morning.compareTo(BigDecimal.ZERO) > 0));
            content.put(TXEAF.MEDS_NOON_GLUCOSE, setCheckbox(noon.compareTo(BigDecimal.ZERO) > 0));
            content.put(TXEAF.MEDS_EVENING_GLUCOSE, setCheckbox(evening.compareTo(BigDecimal.ZERO) > 0));
            content.put(TXEAF.MEDS_DAILY_GLUCOSECHECK, (daily > 0 ? Integer.toString(daily) : "--"));
            content.put(TXEAF.MEDS_WEEKLY_GLUCOSECHECK, (weekly > 0 ? Integer.toString(weekly) : "--"));
        }

    }

    private void createContent4Section17() {
        // nothing yet
    }

    private void createContent4Section18() {
        content.put(TXEAF.COMMENTS_GENERAL, generalComment + (mapID2Info.containsKey(ResInfoTypeTools.TYPE_WARNING) ? getValue(ResInfoTypeTools.TYPE_WARNING, "beschreibung") + SYSTools.catchNull(mapID2Info.get(ResInfoTypeTools.TYPE_WARNING).getText(), "\n", "") : ""));
    }

    private void createContent4Section19(PdfStamper stamper) throws Exception {
        ArrayList<String> bodyParts = new ArrayList<String>(Arrays.asList(PnlBodyScheme.PARTS));
        String[] pdfbody = new String[]{TXEAF.BODY1_DESCRIPTION, TXEAF.BODY2_DESCRIPTION, TXEAF.BODY3_DESCRIPTION, TXEAF.BODY4_DESCRIPTION, TXEAF.BODY5_DESCRIPTION, TXEAF.BODY6_DESCRIPTION};
        int lineno = -1;
        AcroFields form = stamper.getAcroFields();
        PdfContentByte directcontent = stamper.getOverContent(2);

        for (int type : ArrayUtils.add(ResInfoTypeTools.TYPE_ALL_WOUNDS, ResInfoTypeTools.TYPE_MYCOSIS)) {
            if (mapID2Info.containsKey(type)) {

                String descriptionKey = (type == ResInfoTypeTools.TYPE_MYCOSIS ? "misc.msg.mycosis" : "misc.msg.wound.documentation");

                ResInfo currentWound = mapID2Info.get(type);
                lineno++;

                content.put(pdfbody[lineno], SYSTools.xx(descriptionKey) + " " + DateFormat.getDateInstance().format(currentWound.getFrom()) + ": " + ResInfoTools.getContentAsPlainText(currentWound, true));

                AcroFields.FieldPosition pos1 = form.getFieldPositions(pdfbody[lineno]).get(0);
                directcontent.saveState();

                directcontent.rectangle(pos1.position.getLeft(), pos1.position.getBottom(), pos1.position.getWidth(), pos1.position.getHeight());

                /***
                 *          _                      _   _            _ _
                 *       __| |_ __ __ ___      __ | |_| |__   ___  | (_)_ __   ___  ___
                 *      / _` | '__/ _` \ \ /\ / / | __| '_ \ / _ \ | | | '_ \ / _ \/ __|
                 *     | (_| | | | (_| |\ V  V /  | |_| | | |  __/ | | | | | |  __/\__ \
                 *      \__,_|_|  \__,_| \_/\_/    \__|_| |_|\___| |_|_|_| |_|\___||___/
                 *
                 */
                for (String key : mapInfo2Properties.get(currentWound).stringPropertyNames()) {
                    if (key.startsWith("bs1.")) {
                        String bodykey = key.substring(4);
                        OPDE.debug(bodykey);
                        int listpos = bodyParts.indexOf(bodykey);
                        OPDE.debug(listpos);
                        // does this property denote a body part AND is it clicked ?
                        if (bodyParts.contains(bodykey) && mapInfo2Properties.get(currentWound).getProperty(key).equalsIgnoreCase("true")) {
                            // set the pointer to the middle right part of the frame
                            directcontent.moveTo(pos1.position.getRight(), pos1.position.getTop() - (pos1.position.getHeight() / 2f));
                            // find the position of the checkbox representing the bodypart.
                            AcroFields.FieldPosition pos2 = form.getFieldPositions(TXEAF.PDFPARTS[listpos]).get(0);
                            // draw a line from the right side of the frame into the middle of the checkbox.
                            directcontent.lineTo(pos2.position.getLeft() + (pos2.position.getWidth() / 2), pos2.position.getBottom() + (pos2.position.getHeight() / 2));
                        }
                    }
                }

                directcontent.setLineWidth(1f);
                directcontent.setColorStroke(BaseColor.GRAY);
                directcontent.stroke();
                directcontent.restoreState();

                /***
                 *          _                      _   _            _ _ _   _   _             _          _
                 *       __| |_ __ __ ___      __ | |_| |__   ___  | (_) |_| |_| | ___    ___(_)_ __ ___| | ___  ___
                 *      / _` | '__/ _` \ \ /\ / / | __| '_ \ / _ \ | | | __| __| |/ _ \  / __| | '__/ __| |/ _ \/ __|
                 *     | (_| | | | (_| |\ V  V /  | |_| | | |  __/ | | | |_| |_| |  __/ | (__| | | | (__| |  __/\__ \
                 *      \__,_|_|  \__,_| \_/\_/    \__|_| |_|\___| |_|_|\__|\__|_|\___|  \___|_|_|  \___|_|\___||___/
                 *
                 */
                directcontent.saveState();
                for (String key : mapInfo2Properties.get(currentWound).stringPropertyNames()) {
                    if (key.startsWith("bs1.")) {
                        String bodykey = key.substring(4);
                        int listpos = bodyParts.indexOf(bodykey);

                        // does this property denote a body part AND is it clicked ?
                        if (bodyParts.contains(bodykey) && mapInfo2Properties.get(currentWound).getProperty(key).equalsIgnoreCase("true")) {
                            AcroFields.FieldPosition pos2 = form.getFieldPositions(TXEAF.PDFPARTS[listpos]).get(0);
                            directcontent.circle(pos2.position.getLeft() + (pos2.position.getWidth() / 2), pos2.position.getBottom() + (pos2.position.getHeight() / 2), 2f);
                        }
                    }
                }
                directcontent.setColorFill(BaseColor.GRAY);
                directcontent.fill();
                directcontent.restoreState();
            }
        }

    }

    private void createContent4SectionICD() {
        getAdditionICDs();
        content.put(TXEAF.DIAG_ICD10, listICD.isEmpty() ? SYSTools.xx("nursingrecords.info.tx.no.diags") : SYSTools.xx("nursingrecords.info.tx.diags.to.follow"));
    }

    /**
     * creates the medications list. Max length of this list 15 entries. Which is medical overkill anyways.
     */
    private void createContent4Meds() {
        ArrayList<Pair<String, String>> listFieldsOnPage3 = new ArrayList<Pair<String, String>>();
        listFieldsOnPage3.add(new Pair(TXEAF.MEDS1, TXEAF.DOSAGE1));
        listFieldsOnPage3.add(new Pair(TXEAF.MEDS2, TXEAF.DOSAGE2));
        listFieldsOnPage3.add(new Pair(TXEAF.MEDS3, TXEAF.DOSAGE3));
        listFieldsOnPage3.add(new Pair(TXEAF.MEDS4, TXEAF.DOSAGE4));
        listFieldsOnPage3.add(new Pair(TXEAF.MEDS5, TXEAF.DOSAGE5));
        listFieldsOnPage3.add(new Pair(TXEAF.MEDS6, TXEAF.DOSAGE6));
        listFieldsOnPage3.add(new Pair(TXEAF.MEDS7, TXEAF.DOSAGE7));
        listFieldsOnPage3.add(new Pair(TXEAF.MEDS8, TXEAF.DOSAGE8));

        ArrayList<Prescription> listRegularMeds = PrescriptionTools.getAllActiveRegularMedsOnly(resident);

//        int line = 0;

        int MAXLINESONPDF = 8;

        for (int line = 0; line < Math.min(listRegularMeds.size(), MAXLINESONPDF); line++) {
            Prescription pres = listRegularMeds.get(line);
            content.put(listFieldsOnPage3.get(line).getFirst(), PrescriptionTools.getShortDescriptionAsCompactText(pres));
            content.put(listFieldsOnPage3.get(line).getSecond(), PrescriptionTools.getDoseAsCompactText(pres));
        }

        if (listRegularMeds.size() > MAXLINESONPDF) {
            content.put(TXEAF.MEDS_WARNINGTEXT, listRegularMeds.size() - MAXLINESONPDF + " " + SYSTools.xx("nursingrecords.info.tx.more.meds.to.follow"));
            getAdditionMeds(listRegularMeds, MAXLINESONPDF);
        }

        content.put(TXEAF.DOCS_MEDS_LIST, setCheckbox(medListStream != null));
        content.put(TXEAF.PAGE3_DOCS_MEDS_LIST, setCheckbox(medListStream != null));

        listFieldsOnPage3.clear();

    }

    private void getAdditionMeds(ArrayList<Prescription> listRegularMeds, int startAt) {
        if (listRegularMeds.size() - 1 < startAt) return;


        try {
            Document document = new Document(PageSize.A4, Utilities.millimetersToPoints(10), Utilities.millimetersToPoints(10), Utilities.millimetersToPoints(20), Utilities.millimetersToPoints(20));
            medListStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, medListStream);
            document.open();

            Paragraph h1 = new Paragraph(new Phrase(SYSTools.xx("misc.msg.additional.medslist"), PDF.plain(PDF.sizeH1())));
            h1.setAlignment(Element.ALIGN_CENTER);
            document.add(h1);

            Paragraph p = new Paragraph(new Phrase(ResidentTools.getLabelText(resident)));
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);
            document.add(Chunk.NEWLINE);


            PdfPTable table = new PdfPTable(new float[]{1, 1});
            table.setTotalWidth(Utilities.millimetersToPoints(150));
            table.setLockedWidth(true);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(PDF.cell("misc.msg.drug", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
            table.addCell(PDF.cell("misc.msg.dosage", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
            table.setHeaderRows(1);

            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            for (int pr = startAt; pr < listRegularMeds.size(); pr++) {

                Prescription prescription = listRegularMeds.get(pr);

                table.addCell(new Phrase(PrescriptionTools.getShortDescriptionAsCompactText(prescription)));
                table.addCell(new Phrase(PrescriptionTools.getDoseAsCompactText(prescription)));

            }
            document.add(table);
            document.close();

        } catch (DocumentException d) {

        }

    }


    private void getAdditionICDs() {
        if (listICD.isEmpty()) return;

        try {
            Document document = new Document(PageSize.A4, Utilities.millimetersToPoints(10), Utilities.millimetersToPoints(10), Utilities.millimetersToPoints(20), Utilities.millimetersToPoints(20));
            icdListStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, icdListStream);
            document.open();

            Paragraph h1 = new Paragraph(new Phrase(SYSTools.xx("nursingrecords.info.dlg.diags"), PDF.plain(PDF.sizeH1())));
            h1.setAlignment(Element.ALIGN_CENTER);
            document.add(h1);

            Paragraph p = new Paragraph(new Phrase(ResidentTools.getLabelText(resident)));
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(new float[]{1, 3});
            table.setTotalWidth(Utilities.millimetersToPoints(150));
            table.setLockedWidth(true);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(PDF.cell("misc.msg.diag.icd10", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
            table.addCell(PDF.cell("misc.msg.Text", PDF.bold(), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
            table.setHeaderRows(1);

            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

            for (ResInfo icd : listICD) {
                String sICD = "";
                sICD += mapInfo2Properties.get(icd).getProperty("text");
                sICD += " (" + (mapInfo2Properties.get(icd).getProperty("koerperseite").equalsIgnoreCase("nicht festgelegt") ? "" : mapInfo2Properties.get(icd).getProperty("koerperseite") + ", ");
                sICD += mapInfo2Properties.get(icd).getProperty("diagnosesicherheit") + ")";

//                if (listICD.indexOf(icd) < listICD.size()) {
//                    sICD += "; ";
//                }

                table.addCell(new Phrase(mapInfo2Properties.get(icd).getProperty("icd")));
                table.addCell(new Phrase(sICD));

            }
            document.add(table);
            document.close();

        } catch (DocumentException d) {
            OPDE.error(d);
        }

    }

    private String getValue(int type, String propsKey) {
        if (!mapID2Info.containsKey(type)) {
            return "--";
        }
        return SYSTools.catchNull(mapInfo2Properties.get(mapID2Info.get(type)).getProperty(propsKey), "--");
    }

    private boolean isEmpty(int type, String propsKey) {
        if (!mapID2Info.containsKey(type)) {
            return true;
        }
        return SYSTools.catchNull(mapInfo2Properties.get(mapID2Info.get(type)).getProperty(propsKey)).isEmpty();
    }

    private String setCheckbox(boolean in) {
        return in ? "1" : "0";
    }

    private String setBD(BigDecimal bd) {
        return (bd != null && bd.compareTo(BigDecimal.ZERO) > 0) ? SYSTools.formatBigDecimal(bd.setScale(2, RoundingMode.HALF_UP)) : "--";
    }

    private String setCheckbox(Object in) {
        return SYSTools.catchNull(in).equalsIgnoreCase("true") ? "1" : "0";
    }

    private boolean hasWounds() {
        boolean wounds = false;
        for (int type : ResInfoTypeTools.TYPE_ALL_WOUNDS) {
            wounds |= mapID2Info.containsKey(type);
        }
        return wounds;
    }

    private boolean hasWoundPain() {
        boolean pain = false;
        for (int type : ResInfoTypeTools.TYPE_ALL_WOUNDS) {
            pain |= getValue(type, "pain").equalsIgnoreCase("true");
        }
        return pain;
    }

    private String setRadiobutton(Object in, String[] list) {
        String sIn = SYSTools.catchNull(in);
        int pos = Arrays.asList(list).indexOf(sIn);

        return Integer.toString(pos < 0 ? pos : pos + 1);
    }

    private String setYesNoRadiobutton(Object in) {
        return SYSTools.catchNull(in).equalsIgnoreCase("true") ? "2" : "1";
    }

    private String setYesNoRadiobutton(boolean in) {
        return in ? "2" : "1";
    }


    private Properties load(String text) {
        Properties props = new Properties();
        try {
            StringReader reader = new StringReader(text);
            props.load(reader);
            reader.close();
        } catch (IOException ex) {
            OPDE.fatal(ex);
        }
        return props;
    }


    private void createContent4MRE() {
        content.put(TXEAF.MRE_RESIDENT_GENDER_MALE, setCheckbox(resident.getGender() == ResidentTools.MALE));
        content.put(TXEAF.MRE_RESIDENT_GENDER_FEMALE, setCheckbox(resident.getGender() == ResidentTools.FEMALE));
        content.put(TXEAF.MRE_RESIDENT_FIRSTNAME, resident.getFirstname());
        content.put(TXEAF.MRE_RESIDENT_NAME, resident.getName());
        content.put(TXEAF.MRE_RESIDENT_DOB, DateFormat.getDateInstance().format(resident.getDOB()));
        content.put(TXEAF.MRE_RESIDENT_HINSURANCE, getValue(ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "hiname"));
        if (resident.isActive()) {
            content.put(TXEAF.MRE_LOGO_TEXTFIELD, HomesTools.getAsTextForTX(resident.getStation().getHome()));
            content.put(TXEAF.MRE_RESIDENT_PHONE, resident.getStation().getHome().getTel());
            content.put(TXEAF.MRE_RESIDENT_STREET, resident.getStation().getHome().getStreet());
            content.put(TXEAF.MRE_RESIDENT_CITY, resident.getStation().getHome().getCity());
            content.put(TXEAF.MRE_RESIDENT_ZIP, resident.getStation().getHome().getZIP());
            content.put(TXEAF.MRE_PHONE, resident.getStation().getHome().getTel());
        }

        content.put(TXEAF.MRE_MRSA1, setCheckbox(getValue(ResInfoTypeTools.TYPE_INFECTION, "mrsa")));
        content.put(TXEAF.MRE_MRSA2, setCheckbox(getValue(ResInfoTypeTools.TYPE_INFECTION, "mrsa")));
        content.put(TXEAF.MRE_VRE, setCheckbox(getValue(ResInfoTypeTools.TYPE_INFECTION, "vre")));
        content.put(TXEAF.MRE_ESBL, setCheckbox(getValue(ResInfoTypeTools.TYPE_INFECTION, "esbl")));
        content.put(TXEAF.MRE_MRE_TEXTFIELD, getValue(ResInfoTypeTools.TYPE_INFECTION, "other"));
        content.put(TXEAF.MRE_LAB_CONFIRMED, setYesNoRadiobutton(getValue(ResInfoTypeTools.TYPE_INFECTION, "lab").equalsIgnoreCase("confirmed")));
        content.put(TXEAF.MRE_LAB_WAITING, setCheckbox(getValue(ResInfoTypeTools.TYPE_INFECTION, "lab").equalsIgnoreCase("waiting")));

        content.put(TXEAF.MRE_LOCAL_NOSE, setCheckbox(getValue(ResInfoTypeTools.TYPE_INFECTION, "nose")));
        content.put(TXEAF.MRE_LOCAL_PHARYNX, setCheckbox(getValue(ResInfoTypeTools.TYPE_INFECTION, "pharynx")));
        content.put(TXEAF.MRE_LOCAL_URINE, setCheckbox(getValue(ResInfoTypeTools.TYPE_INFECTION, "urine")));
        content.put(TXEAF.MRE_LOCAL_RESPIRATION, setCheckbox(getValue(ResInfoTypeTools.TYPE_INFECTION, "respiration")));
        content.put(TXEAF.MRE_LOCAL_WOUND, setCheckbox(!getValue(ResInfoTypeTools.TYPE_INFECTION, "woundtext").equals("--")));
        content.put(TXEAF.MRE_LOCAL_OTHER, setCheckbox(!getValue(ResInfoTypeTools.TYPE_INFECTION, "otherplace").equals("--")));
        content.put(TXEAF.MRE_WOUND_TEXTFIELD, getValue(ResInfoTypeTools.TYPE_INFECTION, "woundtext"));
        content.put(TXEAF.MRE_OTHER_TEXTFIELD, getValue(ResInfoTypeTools.TYPE_INFECTION, "otherplace"));

        content.put(TXEAF.MRE_TX_DATE, DateFormat.getDateInstance().format(new Date()));
        content.put(TXEAF.MRE_TX_USERNAME, (OPDE.getLogin() != null ? OPDE.getLogin().getUser().getFullname() : ""));

        ArrayList<String> bodyParts = new ArrayList<String>(Arrays.asList(PnlBodyScheme.PARTS));
        for (String key : mapInfo2Properties.get(mapID2Info.get(ResInfoTypeTools.TYPE_INFECTION)).stringPropertyNames()) {
            if (key.startsWith("bs1.")) {
                String bodykey = key.substring(4);
                OPDE.debug(bodykey);
                int listpos = bodyParts.indexOf(bodykey);
                OPDE.debug(listpos);
                // does this property denote a body part AND is it clicked ?
                content.put(TXEAF.PDFPARTSMRE[listpos], setCheckbox(bodyParts.contains(bodykey) && mapInfo2Properties.get(mapID2Info.get(ResInfoTypeTools.TYPE_INFECTION)).getProperty(key).equalsIgnoreCase("true")));
            }
        }

        content.put(TXEAF.MRE_LAB_DATE, getValue(ResInfoTypeTools.TYPE_INFECTION, "date"));

        content.put(TXEAF.MRE_CLEANING_FROM, getValue(ResInfoTypeTools.TYPE_INFECTION, "cleaningfrom"));
        content.put(TXEAF.MRE_CLEANING_TO, getValue(ResInfoTypeTools.TYPE_INFECTION, "cleaningto"));
        content.put(TXEAF.MRE_CLEANING_WITH, getValue(ResInfoTypeTools.TYPE_INFECTION, "cleaningwith"));
        content.put(TXEAF.MRE_THERAPY_LOCAL_TEXT, getValue(ResInfoTypeTools.TYPE_INFECTION, "therapylocal"));
        content.put(TXEAF.MRE_THERAPY_SYSTEM_TEXT, getValue(ResInfoTypeTools.TYPE_INFECTION, "therapysystem"));
        content.put(TXEAF.MRE_THERAPY_START_FROM_TEXT, getValue(ResInfoTypeTools.TYPE_INFECTION, "therapyfrom"));
        content.put(TXEAF.MRE_THERAPY_START_TO_TEXT, getValue(ResInfoTypeTools.TYPE_INFECTION, "therapyto"));

        content.put(TXEAF.MRE_THERAPY_LOCAL, setCheckbox(!isEmpty(ResInfoTypeTools.TYPE_INFECTION, "therapylocal")));
        content.put(TXEAF.MRE_THERAPY_SYSTEM, setCheckbox(!isEmpty(ResInfoTypeTools.TYPE_INFECTION, "therapysystem")));
        content.put(TXEAF.MRE_THERAPY_START, setCheckbox(!isEmpty(ResInfoTypeTools.TYPE_INFECTION, "therapyfrom")));


        content.put(TXEAF.MRE_THERAPY_MED1, getValue(ResInfoTypeTools.TYPE_INFECTION, "med1"));
        content.put(TXEAF.MRE_THERAPY_MED2, getValue(ResInfoTypeTools.TYPE_INFECTION, "med2"));
        content.put(TXEAF.MRE_THERAPY_MED3, getValue(ResInfoTypeTools.TYPE_INFECTION, "med3"));
        content.put(TXEAF.MRE_THERAPY_MED4, getValue(ResInfoTypeTools.TYPE_INFECTION, "med4"));

        content.put(TXEAF.MRE_THERAPY_DOSE1, getValue(ResInfoTypeTools.TYPE_INFECTION, "dose1"));
        content.put(TXEAF.MRE_THERAPY_DOSE2, getValue(ResInfoTypeTools.TYPE_INFECTION, "dose2"));
        content.put(TXEAF.MRE_THERAPY_DOSE3, getValue(ResInfoTypeTools.TYPE_INFECTION, "dose3"));
        content.put(TXEAF.MRE_THERAPY_DOSE4, getValue(ResInfoTypeTools.TYPE_INFECTION, "dose4"));

        content.put(TXEAF.MRE_THERAPY_FROM1, getValue(ResInfoTypeTools.TYPE_INFECTION, "from1"));
        content.put(TXEAF.MRE_THERAPY_FROM2, getValue(ResInfoTypeTools.TYPE_INFECTION, "from2"));
        content.put(TXEAF.MRE_THERAPY_FROM3, getValue(ResInfoTypeTools.TYPE_INFECTION, "from3"));
        content.put(TXEAF.MRE_THERAPY_FROM4, getValue(ResInfoTypeTools.TYPE_INFECTION, "from4"));

        content.put(TXEAF.MRE_THERAPY_TO1, getValue(ResInfoTypeTools.TYPE_INFECTION, "to1"));
        content.put(TXEAF.MRE_THERAPY_TO2, getValue(ResInfoTypeTools.TYPE_INFECTION, "to2"));
        content.put(TXEAF.MRE_THERAPY_TO3, getValue(ResInfoTypeTools.TYPE_INFECTION, "to3"));
        content.put(TXEAF.MRE_THERAPY_TO4, getValue(ResInfoTypeTools.TYPE_INFECTION, "to4"));

        content.put(TXEAF.MRE_COMMENTS, SYSTools.catchNull(mapID2Info.get(ResInfoTypeTools.TYPE_INFECTION).getText(), "--"));
    }

    private void createContent4PSYCH() {
        content.put(TXEAF.PSYCH_RESIDENT_GENDER, resident.getGender() == ResidentTools.MALE ? "2" : "1");
        content.put(TXEAF.PSYCH_RESIDENT_FIRSTNAME, resident.getFirstname());
        content.put(TXEAF.PSYCH_RESIDENT_NAME, resident.getName());
        content.put(TXEAF.PSYCH_RESIDENT_DOB, DateFormat.getDateInstance().format(resident.getDOB()));
        content.put(TXEAF.PSYCH_RESIDENT_HINSURANCE, getValue(ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "hiname"));
        if (resident.isActive()) {
            content.put(TXEAF.PSYCH_LOGO_TEXTFIELD, HomesTools.getAsTextForTX(resident.getStation().getHome()));
            content.put(TXEAF.PSYCH_RESIDENT_PHONE, resident.getStation().getHome().getTel());
            content.put(TXEAF.PSYCH_RESIDENT_STREET, resident.getStation().getHome().getStreet());
            content.put(TXEAF.PSYCH_RESIDENT_CITY, resident.getStation().getHome().getCity());
            content.put(TXEAF.PSYCH_RESIDENT_ZIP, resident.getStation().getHome().getZIP());
            content.put(TXEAF.PSYCH_PHONE, resident.getStation().getHome().getTel());
        }

        content.put(TXEAF.PSYCH_SPECIALIST, getValue(ResInfoTypeTools.TYPE_PSYCH, "gp1.text"));
        content.put(TXEAF.PSYCH_RESIDENT_ACCOMODATION, setRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "accomodation"), new String[]{"apartment", "assistedliving", "homeless"}));
        content.put(TXEAF.PSYCH_SOCIAL_CONTACTS, setRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "socialcontacts"), new String[]{"supportive", "problem", "missing"}));

        content.put(TXEAF.PSYCH_RESIDENT_JOB, setYesNoRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "job")));
        content.put(TXEAF.PSYCH_RESIDENT_VOLUNTEER, setYesNoRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "volunteer")));

        content.put(TXEAF.PSYCH_AGGRESSIVE, setRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "aggressive"), new String[]{"no1", "yes1", "intermittent1"}));
        content.put(TXEAF.PSYCH_SELFDESTRUCTIVE, setRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "selfdestructive"), new String[]{"no2", "yes2", "intermittent2"}));
        content.put(TXEAF.PSYCH_MANICDEPRESSIVE, setRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "manicdepressive"), new String[]{"no3", "yes3", "intermittent3"}));
        content.put(TXEAF.PSYCH_DELUSION, setRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "delusion"), new String[]{"no4", "yes4", "intermittent4"}));
        content.put(TXEAF.PSYCH_HALLUCINATION, setRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "hallucination"), new String[]{"no5", "yes5", "intermittent5"}));
        content.put(TXEAF.PSYCH_FEAR, setRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "fear"), new String[]{"no12", "yes12", "intermittent12"}));
        content.put(TXEAF.PSYCH_FEARTEXT, getValue(ResInfoTypeTools.TYPE_PSYCH, "feartext"));
        content.put(TXEAF.PSYCH_PASSIVE, setRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "passive"), new String[]{"no6", "yes6", "intermittent6"}));
        content.put(TXEAF.PSYCH_RESTLESS, setRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "restless"), new String[]{"no7", "yes7", "intermittent7"}));
        content.put(TXEAF.PSYCH_REGRESSIVE, setRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "regressive"), new String[]{"no8", "yes8", "intermittent8"}));
        content.put(TXEAF.PSYCH_FAECAL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "faecal"), new String[]{"no9", "yes9", "intermittent9"}));
        content.put(TXEAF.PSYCH_APRAXIA, setRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "apraxia"), new String[]{"no10", "yes10", "intermittent10"}));
        content.put(TXEAF.PSYCH_AGNOSIA, setRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "agnosia"), new String[]{"no11", "yes11", "intermittent11"}));

        content.put(TXEAF.PSYCH_CONSUMING, setYesNoRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "consuming")));
        content.put(TXEAF.PSYCH_TREATMENTSYMPTOMS, setRadiobutton(getValue(ResInfoTypeTools.TYPE_PSYCH, "treatmentsymptoms"), new String[]{"no13", "yes13", "intermittent13"}));

        content.put(TXEAF.PSYCH_ALCOHOL, setCheckbox(getValue(ResInfoTypeTools.TYPE_PSYCH, "alcohol")));
        content.put(TXEAF.PSYCH_DRUGS, setCheckbox(getValue(ResInfoTypeTools.TYPE_PSYCH, "drugs")));
        content.put(TXEAF.PSYCH_MEDS, setCheckbox(getValue(ResInfoTypeTools.TYPE_PSYCH, "meds")));
        content.put(TXEAF.PSYCH_NICOTIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_PSYCH, "nicotin")));
        content.put(TXEAF.PSYCH_GAMBLING, setCheckbox(getValue(ResInfoTypeTools.TYPE_PSYCH, "gambling")));

        content.put(TXEAF.PSYCH_ADDICTION_OTHER, getValue(ResInfoTypeTools.TYPE_PSYCH, "otheraddiction"));

        content.put(TXEAF.PSYCH_SUBSTITUTION, getValue(ResInfoTypeTools.TYPE_PSYCH, "substitution"));
        content.put(TXEAF.PSYCH_SUBSTLOCATION, getValue(ResInfoTypeTools.TYPE_PSYCH, "substlocation"));
        content.put(TXEAF.PSYCH_SUBSTCONTACT, getValue(ResInfoTypeTools.TYPE_PSYCH, "substcontact"));


        content.put(TXEAF.PSYCH_COMMENTS, getComment(ResInfoTypeTools.TYPE_PSYCH));
        content.put(TXEAF.PSYCH_TX_DATE, DateFormat.getDateInstance().format(new Date()));
        content.put(TXEAF.PSYCH_TX_USERNAME, (OPDE.getLogin() != null ? OPDE.getLogin().getUser().getFullname() : ""));


    }

    String getComment(int type) {
        if (!mapID2Info.containsKey(type)) {
            return "--";
        }
        return SYSTools.catchNull(mapID2Info.get(type).getText(), "--");
    }

}
