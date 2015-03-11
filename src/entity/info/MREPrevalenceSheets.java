package entity.info;

import entity.prescription.Prescription;
import entity.prescription.PrescriptionTools;
import entity.system.Commontags;
import entity.system.CommontagsTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.SYSTools;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tloehr on 05.03.15.
 */
public class MREPrevalenceSheets {
    public static final int ROW_SHEET1_TITLE = 1;
    public static final int COL_SHEET1_TITLE = 0;
    public static final int ROW_SHEET2_TITLE = 1;
    public static final int COL_SHEET2_TITLE = 0;

    public static final int ROOM_NO = 0;
    public static final int RESIDENT_NAME_OR_RESID = 1; // resident name or just the id (when anonymous is selected)
    public static final int RESIDENT_STATION = 2; // resident name or just the id (when anonymous is selected)
    public static final int RUNNING_NO = 3; // running number
    public static final int PRESENT_DAY_BEFORE = 4; // resinfotype "ABWE1", TYPE_ABSENCE, presence with a interval overlapping the PIT of the day in question
    public static final int YEAR_OF_BIRTH = 5; // resident "dob"
    public static final int MALE = 6; // resident "Geschlecht == 1"
    public static final int FEMALE = 7; // resident "Geschlecht == 2"
    public static final int URINE_CATHETER = 8; // resinfotype "INKOAID2", ResInfoTypeTools.TYPE_INCOAID, trans.aid=true
    public static final int VESSEL_CATHETER = 9; // "VACTH1", TYPE_VESSEL_CATHETER = 146, vessel.catheter=true
    public static final int BEDSORE = 10; // decubitus, resinfotype "wound[1..5]", ResInfoTypeTools.TYPE_WOUND1..5
    public static final int TRACHEOSTOMA = 11; // resinfotype "respi", TYPE_RESPIRATION
    public static final int OTHER_WOUNDS = 12; // resinfotype "wound[1..5]", ResInfoTypeTools.TYPE_WOUND1..5
    public static final int PEG = 13; // resinfotype "ARTNUTRIT", TYPE_ARTIFICIAL_NUTRTITION, tubetype=peg
    public static final int MRSA = 14; // resinfotype "INFECT1", ResInfoTypeTools.TYPE_INFECTION, mrsa=true
    public static final int SURGERY_LAST_30_DAYS = 15; // resinfotype "SURGERY1",  TYPE_SURGERY, presence with a PIT within the last 30 days
    public static final int HOSPITAL_STAY_LAST_3_MONTHS = 16; // resinfotype "ABWE1", presence with a interval overlapping a PIT within the last 30 days. type=HOSPITAL
    public static final int DESORIENTED_TIME_LOCATION = 17; // resinfotype "ORIENT1", TYPE_ORIENTATION, time != yes1 || location != yes3
    public static final int BEDRIDDEN_WHEELCHAIR = 18; // resinfotype "MOBILITY",bedridden=true || wheel.aid=true
    public static final int URINARY_INCONTINENCE = 19; // resinfotype "HINKO" OR "HINKON",TYPE_INCO_PROFILE_DAY = 113 OR TYPE_INCO_PROFILE_NIGHT = 114, inkoprofil != kontinenz
    public static final int FAECAL_INCONTINENCE = 20; // resinfotype "FINCO1",TYPE_INCO_FAECAL = 115,incolevel > 0
    public static final int DIABETES_INSULINE = 21; // resinfotype "DIABETES1",TYPE_DIABETES = 98,application != none
    public static final int CARELEVEL0 = 22; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS0" || "0" || "Pflegestufe0"
    public static final int CARELEVEL1 = 23; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS1" || "1" || "Pflegestufe1"
    public static final int CARELEVEL2 = 24; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS2" || "2" || "Pflegestufe2"
    public static final int CARELEVEL3 = 25; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS3" || "3" || "Pflegestufe3"
    public static final int CARELEVEL3p = 26; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS3+" || "3+" || "Pflegestufe3+" || "PS3p" || "3p" || "Pflegestufe3p" || "PS3plus" || "3plus" || "Pflegestufe3plus"
    public static final int PNEUMOCOCCAL_VACCINE = 27; // resinfotype "VACCINE1",TYPE_VACCINE = 144,vaccinetype == 9
    public static final int RUNNING_ANTIBIOTICS = 28; // active prescription with assigned commontag of type  == TYPE_SYS_ANTIBIOTICS = 14. Create subsheet out of attached resinfo "ANTIBIO1".

    public static final int MAXCOL_SHEET1 = 29;
    public static final int SHEET1_START_OF_LIST = ROW_SHEET1_TITLE + 6;

    private final int[] NEEDED_TYPES = new int[]{ResInfoTypeTools.TYPE_INCOAID, ResInfoTypeTools.TYPE_INCO_FAECAL, ResInfoTypeTools.TYPE_INCO_PROFILE_DAY, ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT,
            ResInfoTypeTools.TYPE_WOUND1, ResInfoTypeTools.TYPE_WOUND2, ResInfoTypeTools.TYPE_WOUND3, ResInfoTypeTools.TYPE_WOUND4, ResInfoTypeTools.TYPE_WOUND5, ResInfoTypeTools.TYPE_RESPIRATION,
            ResInfoTypeTools.TYPE_ORIENTATION, ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, ResInfoTypeTools.TYPE_INFECTION, ResInfoTypeTools.TYPE_VACCINE};

    private final ArrayList<Resident> listResidents;
    private final LocalDate targetDate;
    private final boolean anonymous;
    private int progress, max, runningNumber;
    private HashMap<Integer, ResInfo> mapID2Info;
    private final HashMap<ResInfo, Properties> mapInfo2Properties;
    private final HashMap<Integer, ResInfoType> mapResInfoType;
    private final Commontags antibiotics;
    private Font titleFont, boldFont;

    public MREPrevalenceSheets(final LocalDate targetDate, boolean anonymous) {
        this.targetDate = targetDate;
        this.anonymous = anonymous;
        progress = 1;


        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));


        antibiotics = CommontagsTools.getType(CommontagsTools.TYPE_SYS_ANTIBIOTICS);


        listResidents = ResidentTools.getAllActive(targetDate.minusDays(1), targetDate);
        Collections.sort(listResidents, new Comparator<Resident>() {
            @Override
            public int compare(Resident o1, Resident o2) {
                int sort = o1.getStation().compareTo(o2.getStation());
                if (sort == 0) sort = o1.getRID().compareTo(o2.getRID());
                return sort;
            }
        });
        max = listResidents.size() * MAXCOL_SHEET1 + 3; // 2 more for preparation and wrapup
        runningNumber = 0;

        progress++;
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));

        mapID2Info = new HashMap<Integer, ResInfo>();
        mapInfo2Properties = new HashMap<ResInfo, Properties>();
        mapResInfoType = new HashMap<>();

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {

                // prepare a vanilla workbook to fill
                Workbook workbook = prepareWorkbook();


                // get all residents who were at least living here yesterday, even they may have been away on those two days
                for (Resident resident : listResidents) {

                    progress++;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));

                    // load the data for this resident
                    mapID2Info.clear();
                    mapInfo2Properties.clear();

                    for (int neededType : NEEDED_TYPES) {
                        for (ResInfo info : ResInfoTools.getAll(resident, getResInfoTypeByType(neededType), targetDate.minusDays(1), targetDate)) {

                            if (info.getResInfoType().getType() != ResInfoTypeTools.TYPE_DIAGNOSIS) {
                                mapID2Info.put(info.getResInfoType().getType(), info);
                                mapInfo2Properties.put(info, load(info.getProperties()));
                            }

                        }
                    }

                    runningNumber++;
                    fillLine(workbook.getSheetAt(0), resident);

//                    SYSFilesTools.handleFile(, Desktop.Action.OPEN);

                }

                return workbook;
            }

            @Override
            protected void done() {
                try {

                    progress++;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));

                    FileOutputStream fileOut = new FileOutputStream("/local/workbook.xlsx");
                    Workbook workbook = (Workbook) get();
                    workbook.write(fileOut);
                    fileOut.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                mapResInfoType.clear();
                mapID2Info.clear();
                mapInfo2Properties.clear();

                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);
            }
        };

        worker.execute();


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

    private String getCellContent(int type, String key, String value) {
        return (mapID2Info.containsKey(getResInfoTypeByType(type)) &&
                mapInfo2Properties.containsKey(key) &&
                mapInfo2Properties.get(mapID2Info.get(getResInfoTypeByType(type))).getProperty(key).equalsIgnoreCase(value) ? "X" : "");
    }

    private void fillLine(Sheet sheet, Resident resident) {

        String[] content = new String[30];
        content[ROOM_NO] = SYSTools.catchNull(resident.getRoom(), "--");
        content[RESIDENT_NAME_OR_RESID] = anonymous ? resident.getRID() : ResidentTools.getLabelText(resident);
        content[RESIDENT_STATION] = SYSTools.catchNull(resident.getStation(), "--");
        content[RUNNING_NO] = Integer.toString(runningNumber);

        // absent yesterday ?
        ArrayList<ResInfo> listAbsence = ResInfoTools.getAll(resident, getResInfoTypeByType(ResInfoTypeTools.TYPE_ABSENCE), targetDate.minusDays(1), targetDate);
        content[PRESENT_DAY_BEFORE] = listAbsence.isEmpty() ? "X" : "";
        listAbsence.clear();

        content[YEAR_OF_BIRTH] = Integer.toString(new LocalDate(resident.getDOB()).getYear());
        content[MALE] = resident.getGender() == ResidentTools.MALE ? "X" : "";
        content[FEMALE] = resident.getGender() == ResidentTools.FEMALE ? "X" : "";
        content[URINE_CATHETER] = getCellContent(ResInfoTypeTools.TYPE_INCOAID, "trans.aid", "true");
        content[VESSEL_CATHETER] = getCellContent(ResInfoTypeTools.TYPE_VESSEL_CATHETER, "vessel.catheter", "true");

        boolean bedsore = false;
        boolean wounds = false;
        for (int type : ResInfoTypeTools.TYPE_ALL_WOUNDS) {
            bedsore |= getCellContent(type, "bedsore", "true").equals("X");
            wounds |= mapID2Info.containsKey(getResInfoTypeByType(type));
        }
        content[BEDSORE] = bedsore ? "X" : "";
        content[OTHER_WOUNDS] = wounds ? "X" : "";
        content[TRACHEOSTOMA] = getCellContent(ResInfoTypeTools.TYPE_RESPIRATION, "stoma", "true");
        content[PEG] = getCellContent(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "tubetype", "peg");
        content[MRSA] = getCellContent(ResInfoTypeTools.TYPE_INFECTION, "mrsa", "true");

        ArrayList<ResInfo> listSurgery = ResInfoTools.getAll(resident, getResInfoTypeByType(ResInfoTypeTools.TYPE_SURGERY), targetDate.minusDays(30), targetDate);
        content[SURGERY_LAST_30_DAYS] = listSurgery.isEmpty() ? "" : "X";
        listSurgery.clear();

        ArrayList<ResInfo> listHospital = ResInfoTools.getAll(resident, getResInfoTypeByType(ResInfoTypeTools.TYPE_ABSENCE), targetDate.minusMonths(3), targetDate);
        boolean hospital = false;
        for (ResInfo resInfo : listHospital) {
            Properties p = load(resInfo.getProperties());
            hospital |= p.containsKey("type") && p.getProperty("type").equalsIgnoreCase(ResInfoTypeTools.TYPE_ABSENCE_HOSPITAL);
            p.clear();
        }
        content[HOSPITAL_STAY_LAST_3_MONTHS] = hospital ? "X" : "";
        listHospital.clear();

        boolean desoriented = getCellContent(ResInfoTypeTools.TYPE_ORIENTATION, "time", "yes1").equalsIgnoreCase("X") || getCellContent(ResInfoTypeTools.TYPE_ORIENTATION, "location", "yes3").equalsIgnoreCase("X");
        content[DESORIENTED_TIME_LOCATION] = desoriented ? "X" : "";

        boolean immobile = getCellContent(ResInfoTypeTools.TYPE_MOBILITY, "bedridden", "true").equalsIgnoreCase("X") || getCellContent(ResInfoTypeTools.TYPE_MOBILITY, "wheel.aid", "true").equalsIgnoreCase("X");
        content[BEDRIDDEN_WHEELCHAIR] = immobile ? "X" : "";

        boolean urine = !(getCellContent(ResInfoTypeTools.TYPE_INCO_PROFILE_DAY, "inkoprofil", "kontinenz").equalsIgnoreCase("X") && getCellContent(ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT, "inkoprofil", "kontinenz").equalsIgnoreCase("X"));
        content[URINARY_INCONTINENCE] = urine ? "X" : "";

        boolean faecal = !getCellContent(ResInfoTypeTools.TYPE_INCO_FAECAL, "incolevel", "0").equalsIgnoreCase("X");
        content[FAECAL_INCONTINENCE] = faecal ? "X" : "";

        boolean insuline = !getCellContent(ResInfoTypeTools.TYPE_DIABETES, "application", "none").equalsIgnoreCase("X");
        content[DIABETES_INSULINE] = insuline ? "X" : "";


        boolean ps0, ps1, ps2, ps3, ps3p = false;

        if (!getCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "assigned").equalsIgnoreCase("X")) {
            content[CARELEVEL0] = "X";
        } else {
            String text = mapInfo2Properties.get(mapID2Info.get(getResInfoTypeByType(ResInfoTypeTools.TYPE_NURSING_INSURANCE))).getProperty("result").replaceAll("\\s", "");

            Pattern p0 = Pattern.compile("(PS0|0|Pflegestufe0)", Pattern.CASE_INSENSITIVE);
            Matcher m0 = p0.matcher(text);

            Pattern p1 = Pattern.compile("(PS1|1|Pflegestufe1)", Pattern.CASE_INSENSITIVE);
            Matcher m1 = p1.matcher(text);

            Pattern p2 = Pattern.compile("(PS2|2|Pflegestufe2)", Pattern.CASE_INSENSITIVE);
            Matcher m2 = p2.matcher(text);

            Pattern p3 = Pattern.compile("(PS3|3|Pflegestufe3)", Pattern.CASE_INSENSITIVE);
            Matcher m3 = p3.matcher(text);

            Pattern p3p = Pattern.compile("(PS3p|3p|Pflegestufe3p)", Pattern.CASE_INSENSITIVE);
            Matcher m3p = p3p.matcher(text);

            content[CARELEVEL0] = m0.matches() ? "X" : "";
            content[CARELEVEL1] = m1.matches() ? "X" : "";
            content[CARELEVEL2] = m2.matches() ? "X" : "";
            content[CARELEVEL3] = m3.matches() ? "X" : "";
            content[CARELEVEL3p] = m3p.matches() ? "X" : "";
        }

        content[PNEUMOCOCCAL_VACCINE] = getCellContent(ResInfoTypeTools.TYPE_VACCINE, "vaccinetype", "9");

        ArrayList<Prescription> listPrescripitons = PrescriptionTools.getPrescriptions4Tags(resident, antibiotics);
        boolean antibiotics = false;
        for (Prescription prescription : listPrescripitons) {
            antibiotics |= prescription.isActiveOn(targetDate);
        }
        content[RUNNING_ANTIBIOTICS] = antibiotics ? "X" : "";


        createRows(sheet, 1);
        for (int i = 0; i < MAXCOL_SHEET1; i++) {

            progress++;
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));

            sheet.getRow(SHEET1_START_OF_LIST + runningNumber).createCell(i).setCellValue(SYSTools.catchNull(content[i]));
        }
    }


    private Workbook prepareWorkbook() throws IOException {
        Workbook wb = new XSSFWorkbook();

        titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 18);
        titleFont.setFontName("Arial");
        CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setFont(titleFont);

        boldFont = wb.createFont();
        boldFont.setFontHeightInPoints((short) 12);
        boldFont.setFontName("Arial");
        boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        CellStyle boldStyle = wb.createCellStyle();
        boldStyle.setFont(boldFont);

        CellStyle rotatedStyle = wb.createCellStyle();
        rotatedStyle.setFont(boldFont);
        rotatedStyle.setRotation((short) 90);

        // sheet1
        Sheet sheet1 = wb.createSheet(WorkbookUtil.createSafeSheetName(SYSTools.xx("prevalence.sheet1.tab.title")));
        sheet1.getPrintSetup().setLandscape(true);
        sheet1.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);

        createRows(sheet1, ROW_SHEET1_TITLE + 7);

        sheet1.getRow(ROW_SHEET1_TITLE).createCell(COL_SHEET1_TITLE).setCellValue(SYSTools.xx("prevalence.sheet1.title"));
        sheet1.getRow(ROW_SHEET1_TITLE).getCell(COL_SHEET1_TITLE).setCellStyle(titleStyle);

        sheet1.getRow(ROW_SHEET1_TITLE + 2).createCell(COL_SHEET1_TITLE).setCellValue(SYSTools.xx("home.name"));
        sheet1.getRow(ROW_SHEET1_TITLE + 3).createCell(COL_SHEET1_TITLE).setCellValue(SYSTools.xx("num.of.beds"));
        sheet1.getRow(ROW_SHEET1_TITLE + 4).createCell(COL_SHEET1_TITLE).setCellValue(SYSTools.xx("beds.in.use"));

        sheet1.getRow(ROW_SHEET1_TITLE + 2).createCell(COL_SHEET1_TITLE + 20).setCellValue(SYSTools.xx("day.of.elicitation"));
        sheet1.getRow(ROW_SHEET1_TITLE + 2).createCell(COL_SHEET1_TITLE + 21).setCellValue(targetDate.toDate());

        for (int i = 0; i < MAXCOL_SHEET1; i++) {
            sheet1.getRow(SHEET1_START_OF_LIST).createCell(i).setCellValue(SYSTools.xx("prevalence.sheet1.col" + String.format("%02d", i + 1) + ".title"));
            sheet1.getRow(SHEET1_START_OF_LIST).getCell(i).setCellStyle(rotatedStyle);

        }


        for (int i = 0; i < MAXCOL_SHEET1; i++) {
            sheet1.autoSizeColumn(i);
        }


//        // sheet2
//
//        Sheet sheet2 = wb.createSheet(WorkbookUtil.createSafeSheetName(SYSTools.xx("prevalence.sheet2.tab.title")));
//        sheet2.getPrintSetup().setLandscape(false);
//        sheet2.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
//
//        createRows(sheet2, ROW_SHEET2_TITLE + 7);
//
//        sheet1.getRow(ROW_SHEET1_TITLE).createCell(COL_SHEET1_TITLE).setCellValue(SYSTools.xx("prevalence.sheet1.title"));
//        sheet1.getRow(ROW_SHEET1_TITLE).getCell(COL_SHEET1_TITLE).setCellStyle(titleStyle);
//
//        sheet1.getRow(ROW_SHEET1_TITLE + 2).createCell(COL_SHEET1_TITLE).setCellValue(SYSTools.xx("home.name"));
//        sheet1.getRow(ROW_SHEET1_TITLE + 3).createCell(COL_SHEET1_TITLE).setCellValue(SYSTools.xx("num.of.beds"));
//        sheet1.getRow(ROW_SHEET1_TITLE + 4).createCell(COL_SHEET1_TITLE).setCellValue(SYSTools.xx("beds.in.use"));
//
//        for (int i = 0; i < 30; i++) {
//            sheet1.getRow(ROW_SHEET1_TITLE + 6).createCell(i).setCellValue(SYSTools.xx("prevalence.sheet1.col" + String.format("%02d", i + 1) + ".title"));
//            sheet1.getRow(ROW_SHEET1_TITLE + 6).getCell(i).setCellStyle(rotatedStyle);
//
//        }
//
//        //        Logger.getAnonymousLogger().log(Level.INFO, Double.toString(cell.getNumericCellValue()));
//        //        cell.setCellType(Cell.CELL_TYPE_STRING);
//        //        cell.setCellValue("a test");

        // Write the output to a file
//        FileOutputStream fileOut = new FileOutputStream("/local/workbook.xlsx");
//        wb.write(fileOut);
//        fileOut.close();


        return wb;
    }
//
//    private void addStatLine(Resident resident, boolean anon) {
//        String room = resident.getRoom() != null ? resident.getRoom().toString() : "--";
//        String rid = anon ? resident.getRID() : ResidentTools.getLabelText(resident);
//        String resident_away_yesterday = mapID2Info
//    }


    private void createRows(Sheet sheet, int num) {
        int offset = sheet.getLastRowNum();
        for (int row = 1; row <= num; row++) {
            sheet.createRow(offset + row);
        }
    }

    private ResInfoType getResInfoTypeByType(int type) {
        if (!mapResInfoType.containsKey(type)) {
            mapResInfoType.put(type, ResInfoTypeTools.getByType(type));
        }
        return mapResInfoType.get(type);
    }
}
