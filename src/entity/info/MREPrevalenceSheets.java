package entity.info;

import entity.prescription.Prescription;
import entity.prescription.PrescriptionTools;
import entity.system.Commontags;
import entity.system.CommontagsTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.SYSTools;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.ss.usermodel.*;
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

    public static final int SHEET2_RUNNING_NO = ROW_SHEET2_TITLE + 3;
    public static final int SHEET2_MED = ROW_SHEET2_TITLE + 4;
    public static final int SHEET2_STRENGTH = ROW_SHEET2_TITLE + 5;
    public static final int SHEET2_DOSE = ROW_SHEET2_TITLE + 6;

    public static final int SHEET2_APPLICATION_LOCAL = ROW_SHEET2_TITLE + 8;
    public static final int SHEET2_APPLICATION_SYSTEM = ROW_SHEET2_TITLE + 9;

    public static final int SHEET2_TREATMENT_PROPHYLACTIC = ROW_SHEET2_TITLE + 11;
    public static final int SHEET2_TREATMENT_THERAPEUTIC = ROW_SHEET2_TITLE + 12;

    public static final int SHEET2_BECAUSE_OF_URINAL = ROW_SHEET2_TITLE + 14;
    public static final int SHEET2_BECAUSE_OF_WOUND = ROW_SHEET2_TITLE + 15;
    public static final int SHEET2_BECAUSE_OF_RESP = ROW_SHEET2_TITLE + 16;
    public static final int SHEET2_BECAUSE_OF_DIGESTIVE = ROW_SHEET2_TITLE + 17;
    public static final int SHEET2_BECAUSE_OF_EYES = ROW_SHEET2_TITLE + 18;
    public static final int SHEET2_BECAUSE_OF_EARS_NOSE_MOUTH = ROW_SHEET2_TITLE + 19;
    public static final int SHEET2_BECAUSE_OF_SYSTEMIC = ROW_SHEET2_TITLE + 20;
    public static final int SHEET2_BECAUSE_OF_FEVER = ROW_SHEET2_TITLE + 21;
    public static final int SHEET2_BECAUSE_OF_OTHER = ROW_SHEET2_TITLE + 22;

    public static final int SHEET2_STARTED_HOME = ROW_SHEET2_TITLE + 24;
    public static final int SHEET2_STARTED_HOSPITAL = ROW_SHEET2_TITLE + 25;
    public static final int SHEET2_STARTED_ELSEWHERE = ROW_SHEET2_TITLE + 26;

    public static final int SHEET2_BY_GP = ROW_SHEET2_TITLE + 28;
    public static final int SHEET2_BY_SPECIALIST = ROW_SHEET2_TITLE + 29;
    public static final int SHEET2_BY_EMERGENCY = ROW_SHEET2_TITLE + 30;

    public static final int SHEET2_ADDTIONAL_URINETEST = ROW_SHEET2_TITLE + 32;
    public static final int SHEET2_ADDTIONAL_MICROBIOLOGY = ROW_SHEET2_TITLE + 33;
    public static final int SHEET2_ADDTIONAL_ISOLATED = ROW_SHEET2_TITLE + 34;
    public static final int SHEET2_ADDTIONAL_RESISTANT = ROW_SHEET2_TITLE + 35;

    public static final int[] SHEET2_INDEX = new int[]{SHEET2_RUNNING_NO, SHEET2_MED, SHEET2_STRENGTH, SHEET2_DOSE, SHEET2_APPLICATION_LOCAL, SHEET2_APPLICATION_SYSTEM, SHEET2_TREATMENT_PROPHYLACTIC,
            SHEET2_TREATMENT_THERAPEUTIC, SHEET2_BECAUSE_OF_URINAL, SHEET2_BECAUSE_OF_WOUND, SHEET2_BECAUSE_OF_RESP, SHEET2_BECAUSE_OF_DIGESTIVE,
            SHEET2_BECAUSE_OF_EYES, SHEET2_BECAUSE_OF_EARS_NOSE_MOUTH, SHEET2_BECAUSE_OF_SYSTEMIC, SHEET2_BECAUSE_OF_OTHER, SHEET2_STARTED_HOME, SHEET2_STARTED_HOSPITAL, SHEET2_STARTED_ELSEWHERE,
            SHEET2_BY_GP, SHEET2_BY_SPECIALIST, SHEET2_BY_EMERGENCY, SHEET2_ADDTIONAL_URINETEST, SHEET2_ADDTIONAL_MICROBIOLOGY, SHEET2_ADDTIONAL_ISOLATED, SHEET2_ADDTIONAL_RESISTANT};

    // hier gehts weiter;

    public static final int MAXCOL_SHEET1 = 29;
    public static final int SHEET1_START_OF_LIST = ROW_SHEET1_TITLE + 6;

    private final int[] NEEDED_TYPES = new int[]{ResInfoTypeTools.TYPE_INCOAID, ResInfoTypeTools.TYPE_INCO_FAECAL, ResInfoTypeTools.TYPE_INCO_PROFILE_DAY, ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT,
            ResInfoTypeTools.TYPE_WOUND1, ResInfoTypeTools.TYPE_WOUND2, ResInfoTypeTools.TYPE_WOUND3, ResInfoTypeTools.TYPE_WOUND4, ResInfoTypeTools.TYPE_WOUND5, ResInfoTypeTools.TYPE_RESPIRATION,
            ResInfoTypeTools.TYPE_ORIENTATION, ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, ResInfoTypeTools.TYPE_INFECTION, ResInfoTypeTools.TYPE_VACCINE, ResInfoTypeTools.TYPE_DIABETES,
            ResInfoTypeTools.TYPE_NURSING_INSURANCE, ResInfoTypeTools.TYPE_MOBILITY, ResInfoTypeTools.TYPE_VESSEL_CATHETER};

    private final ArrayList<Resident> listResidents;
    private final LocalDate targetDate;
    private final boolean anonymous;
    private int progress, max, runningNumber, sheet2_col_index;
    private HashMap<Integer, ResInfo> mapID2Info;
    private final HashMap<ResInfo, Properties> mapInfo2Properties;
    private final HashMap<Integer, ResInfoType> mapResInfoType;
    private final Commontags antibiotics;
    private Font titleFont, boldFont;
    private CellStyle titleStyle, dateStyle, bgStyle;
    private Sheet sheet1, sheet2;
    private Workbook wb;

    public MREPrevalenceSheets(final LocalDate targetDate, boolean anonymous) {
        this.targetDate = targetDate;
        this.anonymous = anonymous;
        progress = 1;
        sheet2_col_index = COL_SHEET2_TITLE + 2;


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
                prepareWorkbook();


                // get all residents who were at least living here yesterday, even they may have been away on those two days
                for (Resident resident : listResidents) {

                    progress++;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));

                    // load the data for this resident
                    mapID2Info.clear();
                    mapInfo2Properties.clear();

                    for (int neededType : NEEDED_TYPES) {
                        for (ResInfo info : ResInfoTools.getAll(resident, getResInfoTypeByType(neededType), targetDate.minusDays(1), targetDate)) {
                            mapID2Info.put(info.getResInfoType().getType(), info);
                            mapInfo2Properties.put(info, load(info.getProperties()));
                        }
                    }

                    runningNumber++;
                    ArrayList<Prescription> listAntibiotics = fillLineSheet1(resident);

                    if (!listAntibiotics.isEmpty()) {
                        if (sheet2 == null) {
                            prepareSheet2();
                        }

                        for (Prescription prescription : listAntibiotics) {
                            fillColSheet2(prescription);
                            sheet2_col_index++;
                        }
                    }


//                    SYSFilesTools.handleFile(, Desktop.Action.OPEN);

                }

                for (int i = 0; i < MAXCOL_SHEET1; i++) {
                    sheet1.autoSizeColumn(i);
                }

                if (sheet2 != null) {
                    for (int i = 0; i < COL_SHEET2_TITLE + runningNumber; i++) {
                        sheet2.autoSizeColumn(i);
                    }
                }

                return null;
            }

            @Override
            protected void done() {
                try {

                    progress++;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));

                    FileOutputStream fileOut = new FileOutputStream("/local/workbook.xlsx");

                    get(); // to make sure the exceptions are passed on

                    wb.write(fileOut);

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

    private String getValue(int type, String key) {
        return mapID2Info.containsKey(type) &&
                mapInfo2Properties.containsKey(mapID2Info.get(type)) &&
                mapInfo2Properties.get(mapID2Info.get(type)).containsKey(key) ? mapInfo2Properties.get(mapID2Info.get(type)).getProperty(key) : "";
    }

    private String getCellContent(int type, String key, String value) {
        return getValue(type, key).equalsIgnoreCase(value) ? "X" : "";
    }

    private String getCellContent(Properties properties, String key, String value) {
        return properties.containsKey(key) && properties.getProperty(key).equalsIgnoreCase(value) ? "X" : "";
    }


    private void fillColSheet2(Prescription prescription) {
        ResInfo resInfo = ResInfoTools.getAnnotation4Prescription(prescription, antibiotics);
        Properties properties = resInfo != null ? load(resInfo.getProperties()) : new Properties();

        String[] content = new String[SHEET2_ADDTIONAL_RESISTANT + 1]; // this is always the last. hence the size of the array

        content[SHEET2_RUNNING_NO] = Integer.toString(runningNumber);
        content[SHEET2_MED] = prescription.getTradeForm().getMedProduct().getText();
        content[SHEET2_STRENGTH] = prescription.getTradeForm().getSubtext();
        content[SHEET2_DOSE] = PrescriptionTools.getDoseAsCompactText(prescription);

        content[SHEET2_APPLICATION_LOCAL] = getCellContent(properties, "application", "local");
        content[SHEET2_APPLICATION_SYSTEM] = getCellContent(properties, "application", "systemic");

        content[SHEET2_TREATMENT_PROPHYLACTIC] = getCellContent(properties, "treatment", "prophylactic");
        content[SHEET2_TREATMENT_THERAPEUTIC] = getCellContent(properties, "treatment", "therapeutic");

        content[SHEET2_BECAUSE_OF_URINAL] = getCellContent(properties, "inf.urethra", "true");
        content[SHEET2_BECAUSE_OF_WOUND] = getCellContent(properties, "inf.skin.wound", "true");
        content[SHEET2_BECAUSE_OF_RESP] = getCellContent(properties, "inf.respiratoric", "true");
        content[SHEET2_BECAUSE_OF_DIGESTIVE] = getCellContent(properties, "inf.digestive", "true");
        content[SHEET2_BECAUSE_OF_EYES] = getCellContent(properties, "inf.eyes", "true");
        content[SHEET2_BECAUSE_OF_EARS_NOSE_MOUTH] = getCellContent(properties, "inf.ear.nose.mouth", "true");
        content[SHEET2_BECAUSE_OF_SYSTEMIC] = getCellContent(properties, "inf.systemic", "true");
        content[SHEET2_BECAUSE_OF_FEVER] = getCellContent(properties, "inf.fever", "true");
        content[SHEET2_BECAUSE_OF_OTHER] = SYSTools.catchNull(properties.getProperty("inf.other"), "--");

        content[SHEET2_STARTED_HOME] = getCellContent(properties, "therapy.start", "here");
        content[SHEET2_STARTED_HOSPITAL] = getCellContent(properties, "therapy.start", "hospital");
        content[SHEET2_STARTED_ELSEWHERE] = getCellContent(properties, "therapy.start", "other");

        content[SHEET2_BY_GP] = getCellContent(properties, "prescription.by", "gp");
        content[SHEET2_BY_SPECIALIST] = getCellContent(properties, "prescription.by", "specialist");
        content[SHEET2_BY_EMERGENCY] = getCellContent(properties, "prescription.by", "emergency");

        content[SHEET2_ADDTIONAL_URINETEST] = getCellContent(properties, "diag.urinetest", "true");
        content[SHEET2_ADDTIONAL_MICROBIOLOGY] = getCellContent(properties, "diag.microbiology", "true");
        content[SHEET2_ADDTIONAL_ISOLATED] = SYSTools.catchNull(properties.getProperty("diag.result"), "--");
        content[SHEET2_ADDTIONAL_RESISTANT] = SYSTools.catchNull(properties.getProperty("diag.resistent"), "--");


        for (int row : SHEET2_INDEX) {
            sheet2.getRow(row).createCell(sheet2_col_index).setCellValue(SYSTools.catchNull(content[row]));
        }
    }

    private ArrayList<Prescription> fillLineSheet1(Resident resident) {

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
            wounds |= mapID2Info.containsKey(type);
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

        //desoriented = !getCellContent(ResInfoTypeTools.TYPE_ORIENTATION, "time", "yes1").equalsIgnoreCase("X") && !getCellContent(ResInfoTypeTools.TYPE_ORIENTATION, "location", "yes3").equalsIgnoreCase("X");
        boolean desoriented = mapID2Info.containsKey(ResInfoTypeTools.TYPE_ORIENTATION) && (!getCellContent(ResInfoTypeTools.TYPE_ORIENTATION, "time", "yes1").equalsIgnoreCase("X") || !getCellContent(ResInfoTypeTools.TYPE_ORIENTATION, "location", "yes3").equalsIgnoreCase("X"));
        content[DESORIENTED_TIME_LOCATION] = desoriented ? "X" : "";

        boolean immobile = getCellContent(ResInfoTypeTools.TYPE_MOBILITY, "bedridden", "true").equalsIgnoreCase("X") || getCellContent(ResInfoTypeTools.TYPE_MOBILITY, "wheel.aid", "true").equalsIgnoreCase("X");
        content[BEDRIDDEN_WHEELCHAIR] = immobile ? "X" : "";

//        if (resident.getName().equalsIgnoreCase("Vom Endt")) {
//            OPDE.debug(resident.getName());
//            OPDE.debug(mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_PROFILE_DAY));
//            OPDE.debug(!getCellContent(ResInfoTypeTools.TYPE_INCO_PROFILE_DAY, "inkoprofil", "kontinenz").equalsIgnoreCase("X"));
//            OPDE.debug(mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT));
//            OPDE.debug(getCellContent(ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT, "inkoprofil", "kontinenz").equalsIgnoreCase("X"));
//        }
        boolean urine = (mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_PROFILE_DAY) && !getCellContent(ResInfoTypeTools.TYPE_INCO_PROFILE_DAY, "inkoprofil", "kontinenz").equalsIgnoreCase("X")) || (mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT) && !getCellContent(ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT, "inkoprofil", "kontinenz").equalsIgnoreCase("X"));
        content[URINARY_INCONTINENCE] = urine ? "X" : "";

        boolean faecal = mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_FAECAL) && !getCellContent(ResInfoTypeTools.TYPE_INCO_FAECAL, "incolevel", "0").equalsIgnoreCase("X");
        content[FAECAL_INCONTINENCE] = faecal ? "X" : "";

        boolean insuline = mapID2Info.containsKey(ResInfoTypeTools.TYPE_DIABETES) && !getCellContent(ResInfoTypeTools.TYPE_DIABETES, "application", "none").equalsIgnoreCase("X");
        content[DIABETES_INSULINE] = insuline ? "X" : "";

        if (!getCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "assigned").equalsIgnoreCase("X")) {
            content[CARELEVEL0] = "X";
        } else {
            String text = getValue(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "result").replaceAll("\\s", "");

            Pattern p0 = Pattern.compile("(PS0|0|Pflegestufe0)", Pattern.CASE_INSENSITIVE);
            Matcher m0 = p0.matcher(text);

            Pattern p1 = Pattern.compile("(PS1|1|Pflegestufe1)", Pattern.CASE_INSENSITIVE);
            Matcher m1 = p1.matcher(text);

            Pattern p2 = Pattern.compile("(PS2|2|Pflegestufe2)", Pattern.CASE_INSENSITIVE);
            Matcher m2 = p2.matcher(text);

            Pattern p3 = Pattern.compile("(PS3|3|Pflegestufe3)", Pattern.CASE_INSENSITIVE);
            Matcher m3 = p3.matcher(text);

            Pattern p3p = Pattern.compile("(PS3p|3p|Pflegestufe3p|PS3\\+|3\\+|Pflegestufe3\\+)", Pattern.CASE_INSENSITIVE);
            Matcher m3p = p3p.matcher(text);

            content[CARELEVEL0] = m0.matches() ? "X" : "";
            content[CARELEVEL1] = m1.matches() ? "X" : "";
            content[CARELEVEL2] = m2.matches() ? "X" : "";
            content[CARELEVEL3] = m3.matches() ? "X" : "";
            content[CARELEVEL3p] = m3p.matches() ? "X" : "";
        }

        content[PNEUMOCOCCAL_VACCINE] = getCellContent(ResInfoTypeTools.TYPE_VACCINE, "vaccinetype", "9");

        ArrayList<Prescription> listPrescripitons = PrescriptionTools.getPrescriptions4Tags(resident, antibiotics);
        ArrayList<Prescription> listAntibiotics = new ArrayList<>();
        for (Prescription prescription : listPrescripitons) {
            if (prescription.isActiveOn(targetDate)) {
                listAntibiotics.add(prescription);
            }
        }
        content[RUNNING_ANTIBIOTICS] = !listAntibiotics.isEmpty() ? "X" : "";


        createRows(sheet1, 1);
        for (int i = 0; i < MAXCOL_SHEET1; i++) {

            progress++;
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));

            sheet1.getRow(SHEET1_START_OF_LIST + runningNumber).createCell(i).setCellValue(SYSTools.catchNull(content[i]));
            sheet1.getRow(SHEET1_START_OF_LIST + runningNumber).getRowStyle().setVerticalAlignment(CellStyle.ALIGN_CENTER);


        }
        return listAntibiotics;
    }


    private void prepareSheet2() {

        sheet2 = wb.createSheet(WorkbookUtil.createSafeSheetName(SYSTools.xx("prevalence.sheet2.tab.title")));
        sheet2.getPrintSetup().setLandscape(true);
        sheet2.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);

        createRows(sheet2, ROW_SHEET2_TITLE + 35);

        sheet2.getRow(ROW_SHEET2_TITLE).createCell(COL_SHEET2_TITLE).setCellValue(SYSTools.xx("prevalence.sheet2.title"));
        sheet2.getRow(ROW_SHEET2_TITLE).getCell(COL_SHEET2_TITLE).setCellStyle(titleStyle);

        sheet2.getRow(ROW_SHEET2_TITLE + 1).createCell(COL_SHEET2_TITLE).setCellValue(SYSTools.xx("day.of.elicitation"));
        sheet2.getRow(ROW_SHEET2_TITLE + 1).createCell(COL_SHEET2_TITLE + 1).setCellValue(targetDate.toDate());
        sheet2.getRow(ROW_SHEET2_TITLE + 1).getCell(COL_SHEET2_TITLE + 1).setCellStyle(dateStyle);

        sheet2.getRow(SHEET2_RUNNING_NO).createCell(0).setCellValue(SYSTools.xx("prevalence.sheet2.block1"));
        sheet2.getRow(SHEET2_RUNNING_NO).getCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_RUNNING_NO).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block1.row1") + " " + SYSTools.xx("prevalence.sheet1.title"));
        sheet2.getRow(SHEET2_RUNNING_NO).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_MED).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block1.row2"));
        sheet2.getRow(SHEET2_MED).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_MED).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_STRENGTH).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block1.row3"));
        sheet2.getRow(SHEET2_STRENGTH).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_STRENGTH).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_DOSE).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block1.row4"));
        sheet2.getRow(SHEET2_DOSE).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_DOSE).getCell(1).setCellStyle(bgStyle);

        sheet2.getRow(SHEET2_APPLICATION_LOCAL).createCell(0).setCellValue(SYSTools.xx("prevalence.sheet2.block2"));
        sheet2.getRow(SHEET2_APPLICATION_LOCAL).getCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_APPLICATION_LOCAL).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block2.row1"));
        sheet2.getRow(SHEET2_APPLICATION_LOCAL).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_APPLICATION_SYSTEM).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_APPLICATION_SYSTEM).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block2.row2"));
        sheet2.getRow(SHEET2_APPLICATION_SYSTEM).getCell(1).setCellStyle(bgStyle);

        sheet2.getRow(SHEET2_TREATMENT_PROPHYLACTIC).createCell(0).setCellValue(SYSTools.xx("prevalence.sheet2.block3"));
        sheet2.getRow(SHEET2_TREATMENT_PROPHYLACTIC).getCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_TREATMENT_PROPHYLACTIC).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block3.row1"));
        sheet2.getRow(SHEET2_TREATMENT_PROPHYLACTIC).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_TREATMENT_THERAPEUTIC).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_TREATMENT_THERAPEUTIC).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block3.row2"));
        sheet2.getRow(SHEET2_TREATMENT_THERAPEUTIC).getCell(1).setCellStyle(bgStyle);

        sheet2.getRow(SHEET2_BECAUSE_OF_URINAL).createCell(0).setCellValue(SYSTools.xx("prevalence.sheet2.block4"));
        sheet2.getRow(SHEET2_BECAUSE_OF_URINAL).getCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_URINAL).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row1"));
        sheet2.getRow(SHEET2_BECAUSE_OF_URINAL).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_WOUND).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_WOUND).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row2"));
        sheet2.getRow(SHEET2_BECAUSE_OF_WOUND).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_RESP).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_RESP).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row3"));
        sheet2.getRow(SHEET2_BECAUSE_OF_RESP).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_DIGESTIVE).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_DIGESTIVE).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row4"));
        sheet2.getRow(SHEET2_BECAUSE_OF_DIGESTIVE).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_EYES).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_EYES).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row5"));
        sheet2.getRow(SHEET2_BECAUSE_OF_EYES).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_EARS_NOSE_MOUTH).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_EARS_NOSE_MOUTH).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row6"));
        sheet2.getRow(SHEET2_BECAUSE_OF_EARS_NOSE_MOUTH).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_SYSTEMIC).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_SYSTEMIC).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row7"));
        sheet2.getRow(SHEET2_BECAUSE_OF_SYSTEMIC).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_FEVER).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_FEVER).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row8"));
        sheet2.getRow(SHEET2_BECAUSE_OF_FEVER).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_OTHER).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_OTHER).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row9"));
        sheet2.getRow(SHEET2_BECAUSE_OF_OTHER).getCell(1).setCellStyle(bgStyle);

        sheet2.getRow(SHEET2_STARTED_HOME).createCell(0).setCellValue(SYSTools.xx("prevalence.sheet2.block5"));
        sheet2.getRow(SHEET2_STARTED_HOME).getCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_STARTED_HOME).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block5.row1"));
        sheet2.getRow(SHEET2_STARTED_HOME).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_STARTED_HOSPITAL).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_STARTED_HOSPITAL).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block5.row2"));
        sheet2.getRow(SHEET2_STARTED_HOSPITAL).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_STARTED_ELSEWHERE).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_STARTED_ELSEWHERE).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block5.row3"));
        sheet2.getRow(SHEET2_STARTED_ELSEWHERE).getCell(1).setCellStyle(bgStyle);

        sheet2.getRow(SHEET2_BY_GP).createCell(0).setCellValue(SYSTools.xx("prevalence.sheet2.block6"));
        sheet2.getRow(SHEET2_BY_GP).getCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BY_GP).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block6.row1"));
        sheet2.getRow(SHEET2_BY_GP).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BY_SPECIALIST).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BY_SPECIALIST).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block6.row2"));
        sheet2.getRow(SHEET2_BY_SPECIALIST).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BY_EMERGENCY).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_BY_EMERGENCY).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block6.row3"));
        sheet2.getRow(SHEET2_BY_EMERGENCY).getCell(1).setCellStyle(bgStyle);

        sheet2.getRow(SHEET2_ADDTIONAL_URINETEST).createCell(0).setCellValue(SYSTools.xx("prevalence.sheet2.block7"));
        sheet2.getRow(SHEET2_ADDTIONAL_URINETEST).getCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_ADDTIONAL_URINETEST).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block7.row1"));
        sheet2.getRow(SHEET2_ADDTIONAL_URINETEST).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_ADDTIONAL_MICROBIOLOGY).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_ADDTIONAL_MICROBIOLOGY).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block7.row2"));
        sheet2.getRow(SHEET2_ADDTIONAL_MICROBIOLOGY).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_ADDTIONAL_ISOLATED).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_ADDTIONAL_ISOLATED).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block7.row3"));
        sheet2.getRow(SHEET2_ADDTIONAL_ISOLATED).getCell(1).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_ADDTIONAL_RESISTANT).createCell(0).setCellStyle(bgStyle);
        sheet2.getRow(SHEET2_ADDTIONAL_RESISTANT).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block7.row4"));
        sheet2.getRow(SHEET2_ADDTIONAL_RESISTANT).getCell(1).setCellStyle(bgStyle);
    }


    private void prepareWorkbook() throws IOException {
        wb = new XSSFWorkbook();

        titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 18);
//        titleFont.setFontName("Arial");
        titleStyle = wb.createCellStyle();
        titleStyle.setFont(titleFont);

        boldFont = wb.createFont();
        boldFont.setFontHeightInPoints((short) 12);
//        boldFont.setFontName("Arial");
        boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        CellStyle boldStyle = wb.createCellStyle();
        boldStyle.setFont(boldFont);

        short df = wb.createDataFormat().getFormat("dd.MM.yyyy");
        dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat(df);
        dateStyle.setFont(boldFont);

        CellStyle rotatedStyle = wb.createCellStyle();
        rotatedStyle.setFont(boldFont);
        rotatedStyle.setRotation((short) 90);
        rotatedStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        bgStyle = wb.createCellStyle();
        bgStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        bgStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font f = wb.createFont();
        f.setColor(IndexedColors.WHITE.getIndex());
        bgStyle.setFont(f);

        // sheet1
        sheet1 = wb.createSheet(WorkbookUtil.createSafeSheetName(SYSTools.xx("prevalence.sheet1.tab.title")));
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
        sheet1.getRow(ROW_SHEET1_TITLE + 2).createCell(COL_SHEET1_TITLE + 21).setCellStyle(dateStyle);

        for (int i = 0; i < MAXCOL_SHEET1; i++) {
            sheet1.getRow(SHEET1_START_OF_LIST).createCell(i).setCellValue(SYSTools.xx("prevalence.sheet1.col" + String.format("%02d", i + 1) + ".title"));
            sheet1.getRow(SHEET1_START_OF_LIST).getCell(i).setCellStyle(rotatedStyle);

        }

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
