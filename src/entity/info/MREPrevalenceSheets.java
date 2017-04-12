package entity.info;

import entity.EntityTools;
import entity.building.Homes;
import entity.building.HomesTools;
import entity.building.Rooms;
import entity.building.RoomsTools;
import entity.prescription.Prescription;
import entity.prescription.PrescriptionTools;
import entity.system.Commontags;
import entity.system.CommontagsTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.Pair;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

/**
 * @see <a href="https://github.com/tloehr/Offene-Pflege.de/issues/11">GitHub #11</a>
 * <p>
 * Created by tloehr on 05.03.15.
 */
public class MREPrevalenceSheets {
    public static final int ROW_SHEET1_TITLE = 1;
    public static final int COL_SHEET1_TITLE = 0;
    public static final int ROW_SHEET2_TITLE = 1;
    public static final int COL_SHEET2_TITLE = 0;

    public static final int ROOM_NO = 0;
    public static final int RESIDENT_NAME_OR_RESID = 1; // resident name or just the id (when anonymous is selected)
    //    public static final int RESIDENT_STATION = 2; // resident name or just the id (when anonymous is selected)
    public static final int RUNNING_NO = 2; // running number
    public static final int PRESENT_DAY_BEFORE = 3; // resinfotype "ABWE1", TYPE_ABSENCE, presence with a interval overlapping the PIT of the day in question
    public static final int YEAR_OF_BIRTH = 4; // resident "dob"
    public static final int MALE = 5; // resident "Geschlecht == 1"
    public static final int FEMALE = 6; // resident "Geschlecht == 2"
    public static final int URINE_CATHETER = 7; // resinfotype "INKOAID2", ResInfoTypeTools.TYPE_INCOAID, trans.aid=true
    public static final int VESSEL_CATHETER = 8; // "VCATH1", TYPE_VESSEL_CATHETER = 146, vessel.catheter=true
    public static final int BEDSORE = 9; // decubitus, resinfotype "wound[1..5]", ResInfoTypeTools.TYPE_WOUND1..5
    public static final int TRACHEOSTOMA = 10; // resinfotype "respi", TYPE_RESPIRATION
    public static final int OTHER_WOUNDS = 11; // resinfotype "wound[1..5]", ResInfoTypeTools.TYPE_WOUND1..5
    public static final int PEG = 12; // resinfotype "ARTNUTRIT", TYPE_ARTIFICIAL_NUTRTITION, tubetype=peg
    public static final int MRSA = 13; // resinfotype "INFECT1", ResInfoTypeTools.TYPE_INFECTION, mrsa=true
    public static final int SURGERY_LAST_30_DAYS = 14; // resinfotype "SURGERY1",  TYPE_SURGERY, presence with a PIT within the last 30 days
    public static final int HOSPITAL_STAY_LAST_3_MONTHS = 15; // resinfotype "ABWE1", presence with a interval overlapping a PIT within the last 30 days. type=HOSPITAL
    public static final int DESORIENTED_TIME_LOCATION = 16; // resinfotype "ORIENT1", TYPE_ORIENTATION, time != yes1 || location != yes3
    public static final int BEDRIDDEN_WHEELCHAIR = 17; // resinfotype "MOBILITY",bedridden=true || wheel.aid=true
    public static final int URINARY_INCONTINENCE = 18; // resinfotype "HINKO" OR "HINKON",TYPE_INCO_PROFILE_DAY = 113 OR TYPE_INCO_PROFILE_NIGHT = 114, inkoprofil != kontinenz
    public static final int FAECAL_INCONTINENCE = 19; // resinfotype "FINCO1",TYPE_INCO_FAECAL = 115,incolevel > 0
    public static final int DIABETES_INSULINE = 20; // resinfotype "DIABETES1",TYPE_DIABETES = 98,application != none
    public static final int CARELEVEL0 = 21; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS0" || "0" || "Pflegestufe0"
    public static final int CARELEVEL1 = 22; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS1" || "1" || "Pflegestufe1"
    public static final int CARELEVEL2 = 23; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS2" || "2" || "Pflegestufe2"
    public static final int CARELEVEL3 = 24; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS3" || "3" || "Pflegestufe3"
    public static final int CARELEVEL4 = 25; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS3+" || "3+" || "Pflegestufe3+" || "PS3p" || "3p" || "Pflegestufe3p" || "PS3plus" || "3plus" || "Pflegestufe3plus"
    public static final int CARELEVEL5 = 26; // resinfotype "NINSURANCE",TYPE_NURSING_INSURANCE = 105,grade == assigned & result.replaceAll("\\s","") == "PS3+" || "3+" || "Pflegestufe3+" || "PS3p" || "3p" || "Pflegestufe3p" || "PS3plus" || "3plus" || "Pflegestufe3plus"
    public static final int PNEUMOCOCCAL_VACCINE = 27; // resinfotype "VACCIN1",TYPE_VACCINE = 144,vaccinetype == 9
    public static final int RUNNING_ANTIBIOTICS = 28; // active prescription with assigned commontag of type  == TYPE_SYS_ANTIBIOTICS = 14. Create subsheet out of attached resinfo "ANTIBIO1".
    public static final int BEDS_IN_USE = 29; // active prescription with assigned commontag of type  == TYPE_SYS_ANTIBIOTICS = 14. Create subsheet out of attached resinfo "ANTIBIO1".
    public static final int BEDS_TOTAL = 30; // active prescription with assigned commontag of type  == TYPE_SYS_ANTIBIOTICS = 14. Create subsheet out of attached resinfo "ANTIBIO1".

    public static final int MAXCOL_SHEET1 = 31; //https://github.com/tloehr/Offene-Pflege.de/issues/71
    public static final int SHEET1_START_OF_LIST = ROW_SHEET1_TITLE + 6;

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

    private final int[] NEEDED_TYPES = new int[]{ResInfoTypeTools.TYPE_INCOAID, ResInfoTypeTools.TYPE_INCO_FAECAL, ResInfoTypeTools.TYPE_INCO_PROFILE_DAY, ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT,
            ResInfoTypeTools.TYPE_WOUND1, ResInfoTypeTools.TYPE_WOUND2, ResInfoTypeTools.TYPE_WOUND3, ResInfoTypeTools.TYPE_WOUND4, ResInfoTypeTools.TYPE_WOUND5, ResInfoTypeTools.TYPE_RESPIRATION,
            ResInfoTypeTools.TYPE_ORIENTATION, ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, ResInfoTypeTools.TYPE_INFECTION, ResInfoTypeTools.TYPE_VACCINE, ResInfoTypeTools.TYPE_DIABETES,
            ResInfoTypeTools.TYPE_NURSING_INSURANCE, ResInfoTypeTools.TYPE_MOBILITY, ResInfoTypeTools.TYPE_VESSEL_CATHETER, ResInfoTypeTools.TYPE_ROOM};

    private final ArrayList<Resident> listResidents;
    private final LocalDate targetDate;
    private final boolean anonymous;
    private final Closure progressClosure;
    private final HashMap<Resident, Rooms> mapRooms;
    private int progress, max, runningNumber, sheet2_col_index;
    private HashMap<Integer, ResInfo> mapID2Info;
    private final HashMap<ResInfo, Properties> mapInfo2Properties;
    private final HashMap<Integer, ResInfoType> mapResInfoType;
    private final HashMap<Homes, int[]> mapBedsTotal;
    private final HashMap<Homes, int[]> mapBedsInUse;
    private final Commontags antibiotics;
    private Font titleFont, boldFont;
    private CellStyle titleStyle, dateStyle, grayStyle, blueGrayStyle;
    private Sheet sheet1, sheet2;
    private Workbook wb;

    private final Logger logger = Logger.getLogger(getClass());

    public MREPrevalenceSheets(final LocalDate targetDate, boolean anonymous, Closure progressClosure) {
        this.targetDate = targetDate;
        this.anonymous = anonymous;
        this.progressClosure = progressClosure;
        mapID2Info = new HashMap<>();
        mapInfo2Properties = new HashMap<>();
        mapResInfoType = new HashMap<>();
        mapRooms = new HashMap<>();


        // todo: the selection of residents and their away times need to be studied more.
        // All residents, who were living in the resthome at 8 in the morning on the targetDate
        LocalTime morning8 = new LocalTime(8, 0); // eight o'clock
        listResidents = ResidentTools.getAllActive(targetDate.toDateTime(morning8), SYSCalendar.eod(targetDate));
        antibiotics = CommontagsTools.getType(CommontagsTools.TYPE_SYS_ANTIBIOTICS);
        mapBedsTotal = new HashMap<>();
        mapBedsInUse = new HashMap<>();

        for (Homes home : HomesTools.getAll()) {
            int maxLevel = RoomsTools.getMaxLevel(home);
            mapBedsTotal.put(home, new int[maxLevel + 1]);
            mapBedsInUse.put(home, new int[maxLevel + 1]);

            for (short level = 0; level <= maxLevel; level++) {
                mapBedsTotal.get(home)[level] = RoomsTools.getBedsTotal(home, level);
                mapBedsInUse.get(home)[level] = 0;
            }
        }
    }


    public File createSheet() throws Exception {
        progress = 1;
        sheet2_col_index = COL_SHEET2_TITLE + 2;

        for (Resident resident : listResidents) {
            for (ResInfo resInfo : ResInfoTools.getAll(resident, getResInfoTypeByType(ResInfoTypeTools.TYPE_ROOM), SYSCalendar.midOfDay(targetDate), SYSCalendar.midOfDay(targetDate))) {
                Properties p1 = SYSTools.load(resInfo.getProperties());
                long rid1 = Long.parseLong(SYSTools.catchNull(p1.getProperty("room.id"), "-1"));
                Rooms room1 = EntityTools.find(Rooms.class, rid1);
                mapRooms.put(resInfo.getResident(), room1);
                mapBedsInUse.get(room1.getFloor().getHome())[room1.getFloor().getLevel()]++;
            }
        }

        // this sorts the resident list according to their assigned rooms. if not possible according to their assigned stations.
        // and if still not possible according to their RIDs (which is always working).
        Collections.sort(listResidents, (o1, o2) -> {
            int i1 = mapRooms.containsKey(o1) ? 1 : 0;
            int i2 = mapRooms.containsKey(o2) ? 1 : 0;
            int sort = i1 - i2; // little trick

            if (sort == 0) {
                if (i1 == 1) {// both residents have rooms assigned
                    sort = mapRooms.get(o1).getFloor().getHome().getEid().compareTo(mapRooms.get(o2).getFloor().getHome().getEid());
                    if (sort == 0)
                        sort = Integer.compare(mapRooms.get(o1).getFloor().getLevel(), mapRooms.get(o2).getFloor().getLevel());
                }
            }
            if (sort == 0) sort = o1.toString().compareTo(o2.toString());
            return sort;
        });

        max = listResidents.size() * MAXCOL_SHEET1 + 3; // 2 more for preparation and wrapup
        runningNumber = 0;

        progress++;
        progressClosure.execute(new Pair<Integer, Integer>(progress, max));


        // prepare a vanilla workbook to fill
        prepareWorkbook();

        // get all residents who were at least living here yesterday, even they may have been away on those two days
        boolean lastForThisLevel = false;

        for (Resident resident : listResidents) {


            progress++;
            progressClosure.execute(new Pair<Integer, Integer>(progress, max));

            // load the data for this resident
            mapID2Info.clear();
            mapInfo2Properties.clear();

            for (int neededType : NEEDED_TYPES) {
                for (ResInfo info : ResInfoTools.getAll(resident, getResInfoTypeByType(neededType), targetDate.minusDays(1), targetDate)) {
                    mapID2Info.put(info.getResInfoType().getType(), info);
                    mapInfo2Properties.put(info, SYSTools.load(info.getProperties()));
                }
            }

            // the whole sheet is sorted by the levels of the floors.
            if (runningNumber != 0) {
                Resident next = runningNumber + 1 >= listResidents.size() ? null : listResidents.get(runningNumber + 1);
                if (next == null) {
                    lastForThisLevel = true;
                } else if (mapRooms.containsKey(resident) && !mapRooms.containsKey(next)) {
                    lastForThisLevel = true;
                } else if (!mapRooms.containsKey(resident) && mapRooms.containsKey(next)) {
                    lastForThisLevel = true;
                } else if (mapRooms.containsKey(resident) && mapRooms.containsKey(next)) {
                    lastForThisLevel = !mapRooms.get(resident).getFloor().getHome().equals(mapRooms.get(next).getFloor().getHome()) ||
                            !mapRooms.get(resident).getFloor().getLevel().equals(mapRooms.get(next).getFloor().getLevel());
                } else {
                    lastForThisLevel = false;
                }
            }


            runningNumber++;
            ArrayList<Prescription> listAntibiotics = fillLineSheet1(resident, lastForThisLevel);
            lastForThisLevel = false;

            if (!listAntibiotics.isEmpty()) {
                if (sheet2 == null) {
                    prepareSheet2();
                }

                for (Prescription prescription : listAntibiotics) {
                    fillColSheet2(prescription);
                    sheet2_col_index++;
                }
            }

        }

        for (int col = 0; col < MAXCOL_SHEET1; col++) {
            sheet1.autoSizeColumn(col);
        }

        if (sheet2 != null) {
            for (int i = 0; i < COL_SHEET2_TITLE + runningNumber; i++) {
                sheet2.autoSizeColumn(i);
            }
        }


        progress++;


        progressClosure.execute(new Pair<Integer, Integer>(progress, max));

        File temp = File.createTempFile("opde-mre", ".xlsx");
        FileOutputStream fileOut = new FileOutputStream(temp);
        wb.write(fileOut);
        fileOut.close();

        mapResInfoType.clear();
        mapID2Info.clear();
        mapInfo2Properties.clear();

        return temp;
    }


    private String getValue(int type, String key) {
        return mapID2Info.containsKey(type) &&
                mapInfo2Properties.containsKey(mapID2Info.get(type)) &&
                mapInfo2Properties.get(mapID2Info.get(type)).containsKey(key) ? mapInfo2Properties.get(mapID2Info.get(type)).getProperty(key) : "";
    }

    private boolean isCellContent(int type, String key, String value) {
        return getCellContent(type, key, value).equals("X");
    }

    private String getCellContent(int type, String key, String value) {
        return getValue(type, key).equalsIgnoreCase(value) ? "X" : "";
    }

    private String getCellContent(Properties properties, String key, String value) {
        return properties.containsKey(key) && properties.getProperty(key).equalsIgnoreCase(value) ? "X" : "";
    }

    private void fillColSheet2(Prescription prescription) throws Exception {
        ResInfo resInfo = ResInfoTools.getAnnotation4Prescription(prescription, antibiotics);
        Properties properties = resInfo != null ? SYSTools.load(resInfo.getProperties()) : new Properties();

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

    private ArrayList<Prescription> fillLineSheet1(Resident resident, boolean lastForThisLevel) throws Exception {
        String[] content = new String[MAXCOL_SHEET1];

        content[ROOM_NO] = getValue(ResInfoTypeTools.TYPE_ROOM, "room.text").isEmpty() ? "--" : getValue(ResInfoTypeTools.TYPE_ROOM, "room.text");
        content[RESIDENT_NAME_OR_RESID] = anonymous ? resident.getRID() : ResidentTools.getLabelText(resident);
        content[RUNNING_NO] = Integer.toString(runningNumber);

        // absent yesterday ?
        ArrayList<ResInfo> listAbsence = ResInfoTools.getAll(resident, getResInfoTypeByType(ResInfoTypeTools.TYPE_ABSENCE), targetDate.minusDays(1), targetDate.minusDays(1));
        ArrayList<ResInfo> listStay = ResInfoTools.getAll(resident, getResInfoTypeByType(ResInfoTypeTools.TYPE_STAY), targetDate.minusDays(1), targetDate.minusDays(1));
        content[PRESENT_DAY_BEFORE] = listAbsence.isEmpty() && !listStay.isEmpty() ? "X" : "";
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
            Properties p = SYSTools.load(resInfo.getProperties());
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

        boolean urine = (mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_PROFILE_DAY) && !getCellContent(ResInfoTypeTools.TYPE_INCO_PROFILE_DAY, "inkoprofil", "kontinenz").equalsIgnoreCase("X")) || (mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT) && !getCellContent(ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT, "inkoprofil", "kontinenz").equalsIgnoreCase("X"));
        content[URINARY_INCONTINENCE] = urine ? "X" : "";

        boolean faecal = mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_FAECAL) && !getCellContent(ResInfoTypeTools.TYPE_INCO_FAECAL, "incolevel", "0").equalsIgnoreCase("X");
        content[FAECAL_INCONTINENCE] = faecal ? "X" : "";

        boolean insuline = mapID2Info.containsKey(ResInfoTypeTools.TYPE_DIABETES) && !getCellContent(ResInfoTypeTools.TYPE_DIABETES, "application", "none").equalsIgnoreCase("X");
        content[DIABETES_INSULINE] = insuline ? "X" : "";

        // alle die nicht mindestens "pg1" oder höher haben gelten als "pg0". Auch diejenigen, die nie beantragt haben oder abgelehnt wurden.
        boolean pg1andabove = isCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg1") ||
                isCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg2") ||
                isCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg3") ||
                isCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg4") ||
                isCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg5");


        if (!pg1andabove) {
            content[CARELEVEL0] = "X";
        } else {
            content[CARELEVEL1] = getCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg1");
            content[CARELEVEL2] = getCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg2");
            content[CARELEVEL3] = getCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg3");
            content[CARELEVEL4] = getCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg4");
            content[CARELEVEL5] = getCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg5");

        }

        content[PNEUMOCOCCAL_VACCINE] = getCellContent(ResInfoTypeTools.TYPE_VACCINE, "vaccinetype", "9");

        ArrayList<Prescription> listPrescripitons = PrescriptionTools.getPrescriptions4Tags(resident, antibiotics);
        ArrayList<Prescription> listAntibiotics = new ArrayList<>();
        for (Prescription prescription : listPrescripitons) {
            if (prescription.isActiveOn(targetDate) && prescription.hasMed()) {
                listAntibiotics.add(prescription);
            }
        }
        content[RUNNING_ANTIBIOTICS] = !listAntibiotics.isEmpty() ? "X" : "";

        createRows(sheet1, 1);
        for (int col = 0; col < MAXCOL_SHEET1 - 2; col++) {
            progress++;
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));

            sheet1.getRow(SHEET1_START_OF_LIST + runningNumber).createCell(col).setCellValue(SYSTools.catchNull(content[col]));
        }

        progress += 2; // for the additional 2 columns;

        if (lastForThisLevel && mapRooms.containsKey(resident)) {
            sheet1.getRow(SHEET1_START_OF_LIST + runningNumber).createCell(MAXCOL_SHEET1 - 2).setCellValue(mapBedsInUse.get(mapRooms.get(resident).getFloor().getHome())[mapRooms.get(resident).getFloor().getLevel()]);
            sheet1.getRow(SHEET1_START_OF_LIST + runningNumber).createCell(MAXCOL_SHEET1 - 1).setCellValue(mapBedsTotal.get(mapRooms.get(resident).getFloor().getHome())[mapRooms.get(resident).getFloor().getLevel()]);

            for (int col = 0; col < MAXCOL_SHEET1; col++) {
                sheet1.getRow(SHEET1_START_OF_LIST + runningNumber).getCell(col).setCellStyle(blueGrayStyle);
            }

        }

        return listAntibiotics;
    }


    private void prepareSheet2() throws Exception {
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
        sheet2.getRow(SHEET2_RUNNING_NO).getCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_RUNNING_NO).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block1.row1") + " " + SYSTools.xx("prevalence.sheet1.title"));
        sheet2.getRow(SHEET2_RUNNING_NO).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_MED).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block1.row2"));
        sheet2.getRow(SHEET2_MED).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_MED).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_STRENGTH).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block1.row3"));
        sheet2.getRow(SHEET2_STRENGTH).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_STRENGTH).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_DOSE).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block1.row4"));
        sheet2.getRow(SHEET2_DOSE).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_DOSE).getCell(1).setCellStyle(grayStyle);

        sheet2.getRow(SHEET2_APPLICATION_LOCAL).createCell(0).setCellValue(SYSTools.xx("prevalence.sheet2.block2"));
        sheet2.getRow(SHEET2_APPLICATION_LOCAL).getCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_APPLICATION_LOCAL).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block2.row1"));
        sheet2.getRow(SHEET2_APPLICATION_LOCAL).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_APPLICATION_SYSTEM).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_APPLICATION_SYSTEM).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block2.row2"));
        sheet2.getRow(SHEET2_APPLICATION_SYSTEM).getCell(1).setCellStyle(grayStyle);

        sheet2.getRow(SHEET2_TREATMENT_PROPHYLACTIC).createCell(0).setCellValue(SYSTools.xx("prevalence.sheet2.block3"));
        sheet2.getRow(SHEET2_TREATMENT_PROPHYLACTIC).getCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_TREATMENT_PROPHYLACTIC).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block3.row1"));
        sheet2.getRow(SHEET2_TREATMENT_PROPHYLACTIC).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_TREATMENT_THERAPEUTIC).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_TREATMENT_THERAPEUTIC).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block3.row2"));
        sheet2.getRow(SHEET2_TREATMENT_THERAPEUTIC).getCell(1).setCellStyle(grayStyle);

        sheet2.getRow(SHEET2_BECAUSE_OF_URINAL).createCell(0).setCellValue(SYSTools.xx("prevalence.sheet2.block4"));
        sheet2.getRow(SHEET2_BECAUSE_OF_URINAL).getCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_URINAL).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row1"));
        sheet2.getRow(SHEET2_BECAUSE_OF_URINAL).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_WOUND).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_WOUND).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row2"));
        sheet2.getRow(SHEET2_BECAUSE_OF_WOUND).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_RESP).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_RESP).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row3"));
        sheet2.getRow(SHEET2_BECAUSE_OF_RESP).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_DIGESTIVE).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_DIGESTIVE).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row4"));
        sheet2.getRow(SHEET2_BECAUSE_OF_DIGESTIVE).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_EYES).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_EYES).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row5"));
        sheet2.getRow(SHEET2_BECAUSE_OF_EYES).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_EARS_NOSE_MOUTH).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_EARS_NOSE_MOUTH).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row6"));
        sheet2.getRow(SHEET2_BECAUSE_OF_EARS_NOSE_MOUTH).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_SYSTEMIC).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_SYSTEMIC).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row7"));
        sheet2.getRow(SHEET2_BECAUSE_OF_SYSTEMIC).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_FEVER).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_FEVER).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row8"));
        sheet2.getRow(SHEET2_BECAUSE_OF_FEVER).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_OTHER).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BECAUSE_OF_OTHER).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block4.row9"));
        sheet2.getRow(SHEET2_BECAUSE_OF_OTHER).getCell(1).setCellStyle(grayStyle);

        sheet2.getRow(SHEET2_STARTED_HOME).createCell(0).setCellValue(SYSTools.xx("prevalence.sheet2.block5"));
        sheet2.getRow(SHEET2_STARTED_HOME).getCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_STARTED_HOME).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block5.row1"));
        sheet2.getRow(SHEET2_STARTED_HOME).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_STARTED_HOSPITAL).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_STARTED_HOSPITAL).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block5.row2"));
        sheet2.getRow(SHEET2_STARTED_HOSPITAL).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_STARTED_ELSEWHERE).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_STARTED_ELSEWHERE).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block5.row3"));
        sheet2.getRow(SHEET2_STARTED_ELSEWHERE).getCell(1).setCellStyle(grayStyle);

        sheet2.getRow(SHEET2_BY_GP).createCell(0).setCellValue(SYSTools.xx("prevalence.sheet2.block6"));
        sheet2.getRow(SHEET2_BY_GP).getCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BY_GP).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block6.row1"));
        sheet2.getRow(SHEET2_BY_GP).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BY_SPECIALIST).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BY_SPECIALIST).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block6.row2"));
        sheet2.getRow(SHEET2_BY_SPECIALIST).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BY_EMERGENCY).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_BY_EMERGENCY).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block6.row3"));
        sheet2.getRow(SHEET2_BY_EMERGENCY).getCell(1).setCellStyle(grayStyle);

        sheet2.getRow(SHEET2_ADDTIONAL_URINETEST).createCell(0).setCellValue(SYSTools.xx("prevalence.sheet2.block7"));
        sheet2.getRow(SHEET2_ADDTIONAL_URINETEST).getCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_ADDTIONAL_URINETEST).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block7.row1"));
        sheet2.getRow(SHEET2_ADDTIONAL_URINETEST).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_ADDTIONAL_MICROBIOLOGY).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_ADDTIONAL_MICROBIOLOGY).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block7.row2"));
        sheet2.getRow(SHEET2_ADDTIONAL_MICROBIOLOGY).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_ADDTIONAL_ISOLATED).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_ADDTIONAL_ISOLATED).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block7.row3"));
        sheet2.getRow(SHEET2_ADDTIONAL_ISOLATED).getCell(1).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_ADDTIONAL_RESISTANT).createCell(0).setCellStyle(grayStyle);
        sheet2.getRow(SHEET2_ADDTIONAL_RESISTANT).createCell(1).setCellValue(SYSTools.xx("prevalence.sheet2.block7.row4"));
        sheet2.getRow(SHEET2_ADDTIONAL_RESISTANT).getCell(1).setCellStyle(grayStyle);
    }


    private void prepareWorkbook() throws Exception {
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
        rotatedStyle.setAlignment(CellStyle.ALIGN_CENTER);
        rotatedStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        rotatedStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font f0 = wb.createFont();
        f0.setColor(IndexedColors.WHITE.getIndex());
        rotatedStyle.setFont(f0);

        grayStyle = wb.createCellStyle();
        grayStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        grayStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font f = wb.createFont();
        f.setColor(IndexedColors.WHITE.getIndex());
        grayStyle.setFont(f);

        blueGrayStyle = wb.createCellStyle();
        blueGrayStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        blueGrayStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font f1 = wb.createFont();
        f1.setColor(IndexedColors.WHITE.getIndex());
        blueGrayStyle.setFont(f1);

        // sheet1
        sheet1 = wb.createSheet(WorkbookUtil.createSafeSheetName(SYSTools.xx("prevalence.sheet1.tab.title")));
        sheet1.getPrintSetup().setLandscape(true);
        sheet1.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);

        createRows(sheet1, ROW_SHEET1_TITLE + 7);

        sheet1.getRow(ROW_SHEET1_TITLE).createCell(COL_SHEET1_TITLE).setCellValue(SYSTools.xx("prevalence.sheet1.title"));
        sheet1.getRow(ROW_SHEET1_TITLE).getCell(COL_SHEET1_TITLE).setCellStyle(titleStyle);

        sheet1.getRow(ROW_SHEET1_TITLE + 3).createCell(COL_SHEET1_TITLE).setCellValue(SYSTools.xx("day.of.elicitation"));
        sheet1.getRow(ROW_SHEET1_TITLE + 4).createCell(COL_SHEET1_TITLE).setCellValue(targetDate.toString("dd.MM.yyyy"));

        for (int i = 0; i < MAXCOL_SHEET1; i++) {
            sheet1.getRow(SHEET1_START_OF_LIST).createCell(i).setCellValue(SYSTools.xx("prevalence.sheet1.col" + String.format("%02d", i + 1) + ".title"));
            sheet1.getRow(SHEET1_START_OF_LIST).getCell(i).setCellStyle(rotatedStyle);
        }
    }

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
