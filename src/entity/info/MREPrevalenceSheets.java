package entity.info;

import entity.files.SYSFilesTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.SYSTools;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ExecutionException;

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
    public static final int RUNNING_NO = 2; // running number
    public static final int PRESENT_YESTERDAY = 3; // resinfotype "ABWE1", TYPE_ABSENCE, presence with a interval overlapping the PIT of the day in question
    public static final int YEAR_OF_BIRTH = 4; // resident "dob"
    public static final int MALE = 5; // resident "Geschlecht == 1"
    public static final int FEMALE = 6; // resident "Geschlecht == 2"
    public static final int URINE_CATHETER = 7; // resinfotype "INKOAID2", ResInfoTypeTools.TYPE_INCOAID, trans.aid=true
    public static final int VESSEL_CATHETER = 8;
    public static final int DECUBITUS = 9; // resinfotype "wound[1..5]", ResInfoTypeTools.TYPE_WOUND1..5
    public static final int TRACHEOSTOMA = 10; // resinfotype "respi", TYPE_RESPIRATION
    public static final int OTHER_WOUNDS = 11; // resinfotype "wound[1..5]", ResInfoTypeTools.TYPE_WOUND1..5
    public static final int PEG = 12; // resinfotype "ARTNUTRIT", TYPE_ARTIFICIAL_NUTRTITION, tubetype=peg
    public static final int MRSA = 13; // resinfotype "INFECT1", ResInfoTypeTools.TYPE_INFECTION, mrsa=true
    public static final int SURGERY_LAST_30_DAYS = 14; // resinfotype "SURGERY1", presence with a PIT within the last 30 days
    public static final int HOSPITAL_STAY_LAST_30_DAYS = 15; // resinfotype "ABWE1", presence with a interval overlapping a PIT within the last 30 days. type=HOSPITAL
    public static final int DESORIENTED_TIME_LOCATION = 16; // resinfotype "ORIENT1", time != yes1 || location != yes3
    public static final int BEDRIDDEN_WHEELCHAIR = 17; // resinfotype "MOBILITY",bedridden=true || wheel.aid=true
    public static final int URINARY_INCONTINENCE = 18; // resinfotype "HINKO" OR "HINKON",TYPE_INCO_PROFILE_DAY = 113 OR TYPE_INCO_PROFILE_NIGHT = 114, inkoprofil != kontinenz
    public static final int FAECAL_INCONTINENCE = 19; // resinfotype "FINCO1",TYPE_INCO_FAECAL = 115,incolevel > 0
    public static final int DIABETES_INSULINE = 20; // resinfotype "DIABETES1",TYPE_DIABETES = 98,application != none
    public static final int CARELEVEL0 = 21; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS0" || "0" || "Pflegestufe0"
    public static final int CARELEVEL1 = 22; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS1" || "1" || "Pflegestufe1"
    public static final int CARELEVEL2 = 23; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS2" || "2" || "Pflegestufe2"
    public static final int CARELEVEL3 = 24; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS3" || "3" || "Pflegestufe3"
    public static final int CARELEVEL3p = 25; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS3+" || "3+" || "Pflegestufe3+" || "PS3p" || "3p" || "Pflegestufe3p" || "PS3plus" || "3plus" || "Pflegestufe3plus"
    public static final int PNEUMOCOCCAL_VACCINE = 26; // resinfotype "VACCINE1",TYPE_VACCINE = 144,vaccinetype == 9
    public static final int RUNNING_ANTIBIOTICS = 27; // active prescription with assigned commontag of type  == TYPE_SYS_ANTIBIOTICS = 14. Create subsheet out of attached resinfo "ANTIBIO1".


    private final int[] NEEDED_TYPES = new int[]{ResInfoTypeTools.TYPE_INCOAID, ResInfoTypeTools.TYPE_INCO_FAECAL, ResInfoTypeTools.TYPE_INCO_PROFILE_DAY, ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT,
            ResInfoTypeTools.TYPE_WOUND1, ResInfoTypeTools.TYPE_WOUND2, ResInfoTypeTools.TYPE_WOUND3, ResInfoTypeTools.TYPE_WOUND4, ResInfoTypeTools.TYPE_WOUND5, ResInfoTypeTools.TYPE_RESPIRATION,
            ResInfoTypeTools.TYPE_ORIENTATION, ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, ResInfoTypeTools.TYPE_INFECTION, ResInfoTypeTools.TYPE_SURGERY, ResInfoTypeTools.TYPE_VACCINE, ResInfoTypeTools.TYPE_ABSENCE};

    private final ArrayList<Resident> listResidents;
    private int progress, max, runningNumber;
    private HashMap<Integer, ResInfo> mapID2Info;
    private final HashMap<ResInfo, Properties> mapInfo2Properties;
    private final HashMap<Integer, ResInfoType> mapResInfoType;

    public MREPrevalenceSheets(final LocalDate targetDate) {


        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));


        listResidents = ResidentTools.getAllActive(targetDate.minusDays(1), targetDate);
        Collections.sort(listResidents, new Comparator<Resident>() {
            @Override
            public int compare(Resident o1, Resident o2) {
                int sort = o1.getStation().compareTo(o2.getStation());
                if (sort == 0) sort = o1.getRID().compareTo(o2.getRID());
                return sort;
            }
        });
        max = listResidents.size();
        progress = 0;
        runningNumber = 0;


        mapID2Info = new HashMap<Integer, ResInfo>();
        mapInfo2Properties = new HashMap<ResInfo, Properties>();
        mapResInfoType = new HashMap<>();

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {

                // get all residents who were at least living here yesterday, even they may have been away on those two days
                for (Resident resident : listResidents) {

                    mapID2Info.clear();
                    mapInfo2Properties.clear();

                    for (int neededType : NEEDED_TYPES) {
                        for (ResInfo info : ResInfoTools.getAll(resident, getByType(neededType), targetDate.minusDays(1), targetDate)) {
                            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));

                            if (info.getResInfoType().getType() != ResInfoTypeTools.TYPE_DIAGNOSIS) {
                                mapID2Info.put(info.getResInfoType().getType(), info);
                                mapInfo2Properties.put(info, load(info.getProperties()));
                            }

                        }
                    }


//                    SYSFilesTools.handleFile(, Desktop.Action.OPEN);

                }

                return null;
            }

            @Override
            protected void done() {

                try {
                    SYSFilesTools.print(get().toString(), true);
                } catch (ExecutionException ee) {
                    OPDE.fatal(ee);
                } catch (InterruptedException ie) {
                    // nop
                }

                mapResInfoType.clear();
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


    private Workbook prepareWorkbook() throws IOException {
        Workbook wb = new XSSFWorkbook();

        org.apache.poi.ss.usermodel.Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 18);
        titleFont.setFontName("Arial");
        CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setFont(titleFont);

        org.apache.poi.ss.usermodel.Font boldFont = wb.createFont();
        boldFont.setFontHeightInPoints((short) 12);
        boldFont.setFontName("Arial");
        boldFont.setBold(true);
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

        for (int i = 0; i < 30; i++) {
            sheet1.getRow(ROW_SHEET1_TITLE + 6).createCell(i).setCellValue(SYSTools.xx("prevalence.sheet1.col" + String.format("%02d", i + 1) + ".title"));
            sheet1.getRow(ROW_SHEET1_TITLE + 6).getCell(i).setCellStyle(rotatedStyle);

        }


        for (int i = 0; i < 30; i++) {
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

    private void addStatLine(Resident resident, boolean anon) {
        String room = resident.getRoom() != null ? resident.getRoom().toString() : "--";
        String rid = anon ? resident.getRID() : ResidentTools.getLabelText(resident);
        String resident_away_yesterday = mapID2Info
    }


    private void createRows(Sheet sheet, int num) {
        int offset = sheet.getLastRowNum();
        for (int row = 1; row <= num; row++) {
            sheet.createRow(offset + row);
        }
    }

    private ResInfoType getResInfoTypeByType(int type){
          if (!mapResInfoType.containsKey(type)){
              mapResInfoType.put(type, ResInfoTypeTools.getByType(type));
          }
        return mapResInfoType.get(type);
    }
}
