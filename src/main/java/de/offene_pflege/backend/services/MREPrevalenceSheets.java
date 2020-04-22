package de.offene_pflege.backend.services;


import de.offene_pflege.backend.entity.EntityTools;
import de.offene_pflege.backend.entity.done.Floors;
import de.offene_pflege.backend.entity.done.Homes;
import de.offene_pflege.backend.entity.done.Rooms;
import de.offene_pflege.backend.entity.done.ResInfo;
import de.offene_pflege.backend.entity.done.ResInfoType;
import de.offene_pflege.backend.entity.done.Resident;
import de.offene_pflege.backend.entity.prescription.Prescription;
import de.offene_pflege.backend.entity.system.Commontags;
import de.offene_pflege.backend.entity.system.CommontagsTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.HasLogger;
import de.offene_pflege.op.tools.Pair;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.*;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * Erzeugt eine Exceltabelle zur Prävalenzmessung im Rahmen des Hygienesiegels des mre-netz regio rhein-ahr.
 */
public class MREPrevalenceSheets implements HasLogger {

    public static final int ROW_SHEET0_FIRST_LINE_FOR_HEADER = 0; // Deckblatt

    public static final int ROW_SHEET1_TITLE = 2; // Prävalenzdaten
    //    public static final int ROW_SHEET2_TITLE = 1; // Antibiotika
    public static final int COL_SHEET2_TITLE = 0;
//    private static final int SHEET1_START_OF_LIST = ROW_SHEET1_TITLE + 1;

    private final int COL_SHEET0_TITLES = 0;

    public static final int SHEET0_ROW_TARGETDATE = ROW_SHEET0_FIRST_LINE_FOR_HEADER + 2;
    public static final int SHEET0_ROW_FACILITY_NAME = ROW_SHEET0_FIRST_LINE_FOR_HEADER + 4;
    public static final int SHEET0_ROW_FACILITY_SHORTNAME = ROW_SHEET0_FIRST_LINE_FOR_HEADER + 5;
    public static final int SHEET0_ROW_FACILITY_STREET = ROW_SHEET0_FIRST_LINE_FOR_HEADER + 6;
    public static final int SHEET0_ROW_FACILITY_ZIPCODE = ROW_SHEET0_FIRST_LINE_FOR_HEADER + 7;
    public static final int SHEET0_ROW_FACILITY_CITY = ROW_SHEET0_FIRST_LINE_FOR_HEADER + 8;
    public static final int SHEET0_ROW_BEDS_TOTAL = ROW_SHEET0_FIRST_LINE_FOR_HEADER + 9;
    public static final int SHEET0_ROW_BEDS_IN_USE = ROW_SHEET0_FIRST_LINE_FOR_HEADER + 10;
    public static final int SHEET0_ROW_NUM_ROOMS = ROW_SHEET0_FIRST_LINE_FOR_HEADER + 11;
    public static final int SHEET0_ROW_NUM_SINGLE_ROOMS = ROW_SHEET0_FIRST_LINE_FOR_HEADER + 12;
    public static final int SHEET0_ROW_START_OF_DEPARTMENTS = ROW_SHEET0_FIRST_LINE_FOR_HEADER + 13;


    // Spalten-Nummern für das Sheet1
    public static final int FLOOR_INDEX = 0;
    public static final int RESIDENT_NAME_OR_RESID = 1; // resident name or just the id (when anonymous is selected)
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
    public static final int SURGERY_LAST_30_DAYS = 13; // resinfotype "SURGERY1",  TYPE_SURGERY, presence with a PIT within the last 30 days
    public static final int HOSPITAL_STAY_LAST_3_MONTHS = 14; // resinfotype "ABWE1", presence with a interval overlapping a PIT within the last 30 days. type=HOSPITAL
    public static final int DESORIENTED_TIME_LOCATION = 15; // resinfotype "ORIENT1", TYPE_ORIENTATION, time != yes1 || location != yes3
    public static final int BEDRIDDEN_WHEELCHAIR = 16; // resinfotype "MOBILITY",bedridden=true || wheel.aid=true
    public static final int URINARY_INCONTINENCE = 17; // resinfotype "HINKO" OR "HINKON",TYPE_INCO_PROFILE_DAY = 113 OR TYPE_INCO_PROFILE_NIGHT = 114, inkoprofil != kontinenz
    public static final int FAECAL_INCONTINENCE = 18; // resinfotype "FINCO1",TYPE_INCO_FAECAL = 115,incolevel > 0
    public static final int DIABETES_INSULINE = 19; // resinfotype "DIABETES1",TYPE_DIABETES = 98,application != none
    public static final int CARELEVEL0 = 20;
    public static final int CARELEVEL1 = 21;
    public static final int CARELEVEL2 = 22;
    public static final int CARELEVEL3 = 23;
    public static final int CARELEVEL4 = 24;
    public static final int CARELEVEL5 = 25;
    public static final int RUNNING_ANTIBIOTICS = 26; // active prescription with assigned commontag of type  == TYPE_SYS_ANTIBIOTICS = 14. Create subsheet out of attached resinfo "ANTIBIO1".
    public static final int MRSA = 27; // resinfotype "INFECT1", ResInfoTypeTools.TYPE_INFECTION, mrsa=true
    public static final int VRE = 28; // resinfotype "INFECT1", ResInfoTypeTools.TYPE_INFECTION, mrsa=true
    public static final int _3MRGN = 29; // resinfotype "INFECT1", ResInfoTypeTools.TYPE_INFECTION, mrsa=true
    public static final int _4MRGN = 30; // resinfotype "INFECT1", ResInfoTypeTools.TYPE_INFECTION, mrsa=true


    // Spalten-Nummern für das Sheet2
    public static final int SHEET2_RUNNING_NO = 0;
    public static final int SHEET2_MED = 1;
    public static final int SHEET2_STRENGTH = 2;
    public static final int SHEET2_DOSE = 3;
    public static final int SHEET2_APPLICATION_LOCAL = 4;
    public static final int SHEET2_APPLICATION_SYSTEM = 5;
    public static final int SHEET2_TREATMENT_PROPHYLACTIC = 6;
    public static final int SHEET2_TREATMENT_THERAPEUTIC = 7;
    public static final int SHEET2_BECAUSE_OF_URINAL = 8;
    public static final int SHEET2_BECAUSE_OF_WOUND = 9;
    public static final int SHEET2_BECAUSE_OF_RESP = 10;
    public static final int SHEET2_BECAUSE_OF_DIGESTIVE = 11;
    public static final int SHEET2_BECAUSE_OF_EYES = 12;
    public static final int SHEET2_BECAUSE_OF_EARS_NOSE_MOUTH = 13;
    public static final int SHEET2_BECAUSE_OF_SYSTEMIC = 14;
    public static final int SHEET2_BECAUSE_OF_FEVER = 15;
    public static final int SHEET2_BECAUSE_OF_OTHER = 16;
    public static final int SHEET2_STARTED_HOME = 17;
    public static final int SHEET2_STARTED_HOSPITAL = 18;
    public static final int SHEET2_STARTED_ELSEWHERE = 19;
    public static final int SHEET2_BY_GP = 20;
    public static final int SHEET2_BY_SPECIALIST = 21;
    public static final int SHEET2_BY_EMERGENCY = 22;
    public static final int SHEET2_ADDTIONAL_URINETEST = 23;
    public static final int SHEET2_ADDTIONAL_MICROBIOLOGY = 24;
    public static final int SHEET2_ADDTIONAL_ISOLATED = 25;
    public static final int SHEET2_ADDTIONAL_RESISTANT = 26;


    // https://github.com/tloehr/Offene-Pflege.de/issues/96
    public static final int SHEET0_START_OF_LIST = ROW_SHEET0_FIRST_LINE_FOR_HEADER + 13;

    //https://github.com/tloehr/Offene-Pflege.de/issues/71
    public static final int MAXCOL_SHEET0 = 5;
    public static final int MAXCOL_SHEET1 = 31;
    public static final int MAXCOL_SHEET2 = 27;

    private final int[] NEEDED_TYPES = new int[]{ResInfoTypeTools.TYPE_INCOAID, ResInfoTypeTools.TYPE_INCO_FAECAL, ResInfoTypeTools.TYPE_INCO_PROFILE_DAY, ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT,
            ResInfoTypeTools.TYPE_WOUND1, ResInfoTypeTools.TYPE_WOUND2, ResInfoTypeTools.TYPE_WOUND3, ResInfoTypeTools.TYPE_WOUND4, ResInfoTypeTools.TYPE_WOUND5, ResInfoTypeTools.TYPE_RESPIRATION,
            ResInfoTypeTools.TYPE_ORIENTATION, ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, ResInfoTypeTools.TYPE_INFECTION, ResInfoTypeTools.TYPE_VACCINE, ResInfoTypeTools.TYPE_DIABETES,
            ResInfoTypeTools.TYPE_NURSING_INSURANCE, ResInfoTypeTools.TYPE_MOBILITY, ResInfoTypeTools.TYPE_VESSEL_CATHETER, ResInfoTypeTools.TYPE_ROOM};

    private final ArrayList<Resident> listResidents;
    private final LocalDate targetDate;
    private final Homes home;
    private final boolean anonymous;
    private final Closure progressClosure;
    private final HashMap<Resident, Rooms> mapRooms;
    private int progress, max, runningNumber;
    private HashMap<Integer, ResInfo> mapTypeID2Info;
    private final HashMap<ResInfo, Properties> mapInfo2Properties;
    private final HashMap<Integer, ResInfoType> mapResInfoType;
    private final HashMap<Floors, Integer> stationIndex;
    private final int[] bedsTotalPerLevel, bedsInUserPerLevel;
    private int roomsTotal, singleRooms, minDOB = 5000, maxDOB = 0;
    private final Commontags antibiotics;

    private XSSFCellStyle titleStyle, dateStyle, gray1Style, blue2Style, blue1Style, orange1Style, rotatedStyle, strikeStyle, blue2StyleStrike;
    private Sheet sheet0, sheet1, sheet2;
    private XSSFWorkbook wb;


    public MREPrevalenceSheets(final LocalDate targetDate, Homes home, boolean anonymous, Closure progressClosure) {
        this.targetDate = targetDate;
        this.home = home;
        this.anonymous = anonymous;
        this.progressClosure = progressClosure;
        mapTypeID2Info = new HashMap<>();
        mapInfo2Properties = new HashMap<>();
        mapResInfoType = new HashMap<>();
        mapRooms = new HashMap<>();

        antibiotics = CommontagsTools.getType(CommontagsTools.TYPE_SYS_ANTIBIOTICS);

        // Belegung wird ermittelt. Alle BW die um 08 Uhr morgens anwesend waren.
//        LocalTime morning8 = new LocalTime(8, 0); // eight o'clock
        int maxLevel = RoomsService.getMaxLevel(home);
        bedsTotalPerLevel = new int[maxLevel + 1];
        bedsInUserPerLevel = new int[maxLevel + 1];
        for (short level = 0; level <= maxLevel; level++) {
            bedsTotalPerLevel[level] = RoomsService.countBeds(home, level);
            bedsInUserPerLevel[level] = 0;
        }

        roomsTotal = 0;
        singleRooms = 0;
        for (Rooms room : RoomsService.getRooms(home)) {
            roomsTotal++;
            if (room.getSingle()) singleRooms++;
        }

        // Gemäß der Definition aus den Erläuterungen des MRE-Netzwerkes

        listResidents = ResidentTools.getAll(targetDate.toDateTimeAtStartOfDay(), SYSCalendar.eod(targetDate));
        ArrayList<Resident> removeResidents = new ArrayList<>();
        for (Resident resident : listResidents) {
            for (ResInfo resInfo : ResInfoService.getAll(resident, getResInfoTypeByType(ResInfoTypeTools.TYPE_ROOM), targetDate.toDateTimeAtStartOfDay().toDate(), SYSCalendar.eod(targetDate).toDate())) {
                Properties p1 = ResInfoService.getContent(resInfo);
                long rid1 = Long.parseLong(SYSTools.catchNull(p1.getProperty("room.id"), "-1"));
                Rooms room1 = EntityTools.find(Rooms.class, rid1);
                if (room1.getFloor().getHome().equals(home)) { // nur die aus der gewünschten Einrichtung
                    mapRooms.put(resInfo.getResident(), room1);
                    bedsInUserPerLevel[room1.getFloor().getLevel()]++;
                } else {
                    removeResidents.add(resident);
                }
            }
        }
        // Hab nichts eleganteres gefunden, da die Zugehörigkeit zur einer Einrichtung an den Räumen hängt und das
        // ist bei der Datenbank Abfrage zu kompliziert. Wenn überhaupt möglich.
        listResidents.removeAll(removeResidents);
        removeResidents.clear();

        stationIndex = new HashMap<>();
    }


    public File createSheet() throws Exception {
        progress = 1;


        // this sorts the resident list according to their assigned rooms. if not possible according to their assigned stations.
        // and if still not possible according to their RIDs (which is always working).
        Collections.sort(listResidents, (o1, o2) -> {
            int i1 = mapRooms.containsKey(o1) ? 1 : 0;
            int i2 = mapRooms.containsKey(o2) ? 1 : 0;
            int sort = i1 - i2; // little trick

            if (sort == 0) {
                if (i1 == 1) {// both residents have rooms assigned
                    sort = mapRooms.get(o1).getFloor().getHome().getId().compareTo(mapRooms.get(o2).getFloor().getHome().getId());
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


        // leere Excel Tabelle vorbereiten.
        prepareWorkbook();

        for (Resident resident : listResidents) {


            progress++;
            progressClosure.execute(new Pair<Integer, Integer>(progress, max));

            // getProperties the data for this resident
            mapTypeID2Info.clear();
            mapInfo2Properties.clear();

            for (int neededType : NEEDED_TYPES) {
                for (ResInfo info : ResInfoService.getAll(resident, getResInfoTypeByType(neededType), targetDate.minusDays(1), targetDate)) {
                    mapTypeID2Info.put(info.getResInfoType().getType(), info);
                    mapInfo2Properties.put(info, ResInfoService.getContent(info));
                }
            }


            runningNumber++;
            ArrayList<Prescription> listAntibiotics = fillALineInSheet1(resident);

            if (!listAntibiotics.isEmpty()) {
                if (sheet2 == null) {
                    prepareSheet2();
                }

                int sheet2_runningRow = 1;
                for (Prescription prescription : listAntibiotics) {
                    fillALineInSheet2(prescription, sheet2_runningRow);
                    sheet2_runningRow++;
                }
            }

        }

        createFormulasInSheet1();


        for (int col = 0; col < MAXCOL_SHEET0; col++) {
            sheet0.autoSizeColumn(col);
        }

        for (int col = 0; col < MAXCOL_SHEET1; col++) {
            sheet1.autoSizeColumn(col);
        }

        if (sheet2 != null) {
            for (int col = 0; col < MAXCOL_SHEET2; col++) {
                sheet2.autoSizeColumn(col);
            }
        }


        progress++;


        progressClosure.execute(new Pair<Integer, Integer>(progress, max));

        File temp = File.createTempFile("opde-mre", ".xlsx");
        FileOutputStream fileOut = new FileOutputStream(temp);
        wb.write(fileOut);
        fileOut.close();

        mapResInfoType.clear();
        mapTypeID2Info.clear();
        mapInfo2Properties.clear();

        return temp;
    }

    private void createFormulasInSheet1() {

        int row2 = ROW_SHEET1_TITLE - 1;
        int row3 = ROW_SHEET1_TITLE;

        for (int i = 3; i < MAXCOL_SHEET1; i++) {

            String coord1 = sheet1.getRow(row3 + 1).getCell(i).getAddress().formatAsString();
            String coord2 = sheet1.getRow(sheet1.getLastRowNum() - 2).getCell(i).getAddress().formatAsString();
            if (i == 4) {
                String strFormula1 = String.format("MIN(%S:%S)", coord1, coord2);
                String strFormula2 = String.format("MAX(%S:%S)", coord1, coord2);
                sheet1.getRow(row2).getCell(i).setCellFormula(strFormula1);
                sheet1.getRow(row2).getCell(i).setCellType(CellType.FORMULA);
                sheet1.getRow(row3).getCell(i).setCellFormula(strFormula2);
                sheet1.getRow(row3).getCell(i).setCellType(CellType.FORMULA);
            } else {
                String strFormula = String.format("SUM(%S:%S)", coord1, coord2);
                sheet1.getRow(row2).getCell(i).setCellFormula(strFormula);
                sheet1.getRow(row2).getCell(i).setCellType(CellType.FORMULA);
            }
        }
    }


    private String getValue(int type, String key) {
        return mapTypeID2Info.containsKey(type) &&
                mapInfo2Properties.containsKey(mapTypeID2Info.get(type)) &&
                mapInfo2Properties.get(mapTypeID2Info.get(type)).containsKey(key) ? mapInfo2Properties.get(mapTypeID2Info.get(type)).getProperty(key) : "0";
    }

    private boolean isCellContent(int type, String key, String value) {
        return getCellContent(type, key, value).equals("1");
    }

    private String getCellContent(int type, String key, String value) {
        return getValue(type, key).equalsIgnoreCase(value) ? "1" : "0";
    }

    private String getCellContent(Properties properties, String key, String value) {
        return properties.containsKey(key) && properties.getProperty(key).equalsIgnoreCase(value) ? "1" : "0";
    }

    private void fillALineInSheet2(Prescription prescription, int runningRow) {
        ResInfo resInfo = ResInfoService.getAnnotation4Prescription(prescription, antibiotics);
        Properties properties = resInfo != null ? ResInfoService.getContent(resInfo) : new Properties();

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
        content[SHEET2_BECAUSE_OF_OTHER] = SYSTools.catchNull(properties.getProperty("inf.other"), "0");

        content[SHEET2_STARTED_HOME] = getCellContent(properties, "therapy.start", "here");
        content[SHEET2_STARTED_HOSPITAL] = getCellContent(properties, "therapy.start", "hospital");
        content[SHEET2_STARTED_ELSEWHERE] = getCellContent(properties, "therapy.start", "other");

        content[SHEET2_BY_GP] = getCellContent(properties, "prescription.by", "gp");
        content[SHEET2_BY_SPECIALIST] = getCellContent(properties, "prescription.by", "specialist");
        content[SHEET2_BY_EMERGENCY] = getCellContent(properties, "prescription.by", "emergency");

        content[SHEET2_ADDTIONAL_URINETEST] = getCellContent(properties, "diag.urinetest", "true");
        content[SHEET2_ADDTIONAL_MICROBIOLOGY] = getCellContent(properties, "diag.microbiology", "true");
        content[SHEET2_ADDTIONAL_ISOLATED] = SYSTools.catchNull(properties.getProperty("diag.result"), "0");
        content[SHEET2_ADDTIONAL_RESISTANT] = SYSTools.catchNull(properties.getProperty("diag.resistent"), "0");

        createRows(sheet2, 1);
        for (int col = 0; col < MAXCOL_SHEET2; col++) {
            // damit Zahlen auch als Zahlen in den Zellen erscheinen und nicht als Zahlen mit Anführungszeichen
            Object d = parseCellContent(SYSTools.catchNull(content[col]));

            if (d instanceof Double) {
                sheet2.getRow(runningRow).createCell(col).setCellValue((Double) d);
            } else {
                sheet2.getRow(runningRow).createCell(col).setCellValue((String) d);
            }

            // alle lieben Zebras
            if (runningRow % 2 == 1)
                sheet2.getRow(runningRow).getCell(col).setCellStyle(blue2Style);
        }
    }

    /**
     * Füllt eine Zeile in Sheet1 und erstellt gleichzeit eine Liste von Antibiotika Verordnungen, die dann zurück gegeben wird.
     *
     * @param resident
     * @return
     */
    private ArrayList<Prescription> fillALineInSheet1(Resident resident) {
        String[] content = new String[MAXCOL_SHEET1];

        getLogger().debug(resident);
        content[FLOOR_INDEX] = stationIndex.get(mapRooms.get(resident).getFloor()).toString();
        content[RESIDENT_NAME_OR_RESID] = anonymous ? SYSTools.anonymizeRID(resident.getId()) : ResidentTools.getLabelText(resident);
        content[RUNNING_NO] = Integer.toString(runningNumber);

        // War er gestern abwesend ?
        ArrayList<ResInfo> listAbsence = ResInfoService.getAll(resident, getResInfoTypeByType(ResInfoTypeTools.TYPE_ABSENCE), targetDate.minusDays(1), targetDate.minusDays(1));
        ArrayList<ResInfo> listStay = ResInfoService.getAll(resident, getResInfoTypeByType(ResInfoTypeTools.TYPE_STAY), targetDate.minusDays(1), targetDate.minusDays(1));
        boolean presentOnDayBefore = listAbsence.isEmpty() && !listStay.isEmpty();
        content[PRESENT_DAY_BEFORE] = presentOnDayBefore ? "1" : "0";
        listAbsence.clear();

        int dob = new LocalDate(resident.getDob()).getYear();
        content[YEAR_OF_BIRTH] = Integer.toString(dob);

        minDOB = Math.min(minDOB, dob);
        maxDOB = Math.max(maxDOB, dob);

        content[MALE] = resident.getGender() == ResidentTools.MALE ? "1" : "0";
        content[FEMALE] = resident.getGender() == ResidentTools.FEMALE ? "1" : "0";
        content[URINE_CATHETER] = getCellContent(ResInfoTypeTools.TYPE_INCOAID, "trans.aid", "true");
        content[VESSEL_CATHETER] = getCellContent(ResInfoTypeTools.TYPE_VESSEL_CATHETER, "vessel.catheter", "true");

        boolean bedsore = false;
        boolean wounds = false;
        for (int type : ResInfoTypeTools.TYPE_ALL_WOUNDS) {
            bedsore |= getCellContent(type, "bedsore", "true").equals("1");
            wounds |= mapTypeID2Info.containsKey(type);
        }
        content[BEDSORE] = bedsore ? "1" : "0";
        content[OTHER_WOUNDS] = wounds ? "1" : "0";
        content[TRACHEOSTOMA] = getCellContent(ResInfoTypeTools.TYPE_RESPIRATION, "stoma", "true");
        content[PEG] = getCellContent(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "tubetype", "peg");
        content[MRSA] = getCellContent(ResInfoTypeTools.TYPE_INFECTION, "mrsa", "true");
        content[VRE] = getCellContent(ResInfoTypeTools.TYPE_INFECTION, "vre", "true");
        content[_3MRGN] = getCellContent(ResInfoTypeTools.TYPE_INFECTION, "3mrgn", "true");
        content[_4MRGN] = getCellContent(ResInfoTypeTools.TYPE_INFECTION, "4mrgn", "true");

        ArrayList<ResInfo> listSurgery = ResInfoService.getAll(resident, getResInfoTypeByType(ResInfoTypeTools.TYPE_SURGERY), targetDate.minusDays(30), targetDate);
        content[SURGERY_LAST_30_DAYS] = listSurgery.isEmpty() ? "0" : "1";
        listSurgery.clear();

        ArrayList<ResInfo> listHospital = ResInfoService.getAll(resident, getResInfoTypeByType(ResInfoTypeTools.TYPE_ABSENCE), targetDate.minusMonths(3), targetDate);
        boolean hospital = false;
        for (ResInfo resInfo : listHospital) {
            Properties p = ResInfoService.getContent(resInfo);
            hospital |= p.containsKey("type") && p.getProperty("type").equalsIgnoreCase(ResInfoTypeTools.TYPE_ABSENCE_HOSPITAL);
            p.clear();
        }
        content[HOSPITAL_STAY_LAST_3_MONTHS] = hospital ? "1" : "0";
        listHospital.clear();

        boolean desoriented = mapTypeID2Info.containsKey(ResInfoTypeTools.TYPE_ORIENTATION) && (!getCellContent(ResInfoTypeTools.TYPE_ORIENTATION, "time", "yes1").equalsIgnoreCase("1") || !getCellContent(ResInfoTypeTools.TYPE_ORIENTATION, "location", "yes3").equalsIgnoreCase("1"));
        content[DESORIENTED_TIME_LOCATION] = desoriented ? "1" : "0";

        boolean immobile = getCellContent(ResInfoTypeTools.TYPE_MOBILITY, "bedridden", "true").equalsIgnoreCase("1") || getCellContent(ResInfoTypeTools.TYPE_MOBILITY, "wheel.aid", "true").equalsIgnoreCase("1");
        content[BEDRIDDEN_WHEELCHAIR] = immobile ? "1" : "0";

        boolean urine = (mapTypeID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_PROFILE_DAY) && !getCellContent(ResInfoTypeTools.TYPE_INCO_PROFILE_DAY, "inkoprofil", "kontinenz").equalsIgnoreCase("1")) || (mapTypeID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT) && !getCellContent(ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT, "inkoprofil", "kontinenz").equalsIgnoreCase("1"));
        content[URINARY_INCONTINENCE] = urine ? "1" : "0";

        boolean faecal = mapTypeID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_FAECAL) && !getCellContent(ResInfoTypeTools.TYPE_INCO_FAECAL, "incolevel", "0").equalsIgnoreCase("1");
        content[FAECAL_INCONTINENCE] = faecal ? "1" : "0";

        boolean insuline = mapTypeID2Info.containsKey(ResInfoTypeTools.TYPE_DIABETES) && !getCellContent(ResInfoTypeTools.TYPE_DIABETES, "application", "none").equalsIgnoreCase("1");
        content[DIABETES_INSULINE] = insuline ? "1" : "0";

        // alle die nicht mindestens "pg1" oder höher haben gelten als "pg0". Auch diejenigen, die nie beantragt haben oder abgelehnt wurden.
        boolean pg1andabove = isCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg1") ||
                isCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg2") ||
                isCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg3") ||
                isCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg4") ||
                isCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg5");

        content[CARELEVEL0] = pg1andabove ? "0" : "1";
        content[CARELEVEL1] = getCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg1");
        content[CARELEVEL2] = getCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg2");
        content[CARELEVEL3] = getCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg3");
        content[CARELEVEL4] = getCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg4");
        content[CARELEVEL5] = getCellContent(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade", "pg5");

        ArrayList<Prescription> listPrescripitons = PrescriptionTools.getPrescriptions4Tags(resident, antibiotics);
        // Gemäß Definition vom MRE Netzwerk
        // "Für die Erhebung der Antibiotika muss der Bewohner an dem Tag und am Vortag anwesend sein und ein Antibiotikum an dem Tag erhalten haben."
        ArrayList<Prescription> listAntibiotics = new ArrayList<>();
        for (Prescription prescription : listPrescripitons) {
            if (prescription.isActiveOn(targetDate) && presentOnDayBefore && prescription.hasMed()) {
                listAntibiotics.add(prescription);
            }
        }
        content[RUNNING_ANTIBIOTICS] = !listAntibiotics.isEmpty() ? "1" : "0";

        createRows(sheet1, 1);
        for (int col = 0; col < MAXCOL_SHEET1; col++) {
            progress++;
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));

            // damit Zahlen auch als Zahlen in den Zellen erscheinen und nicht als Zahlen mit Anführungszeichen
            Object d = parseCellContent(SYSTools.catchNull(content[col]));

            if (d instanceof Double) {
                sheet1.getRow(ROW_SHEET1_TITLE + runningNumber).createCell(col).setCellValue((Double) d);
            } else {
                sheet1.getRow(ROW_SHEET1_TITLE + runningNumber).createCell(col).setCellValue((String) d);
            }


            if (presentOnDayBefore) {
                // alle lieben Zebras
                if (runningNumber % 2 == 1)
                    sheet1.getRow(ROW_SHEET1_TITLE + runningNumber).getCell(col).setCellStyle(blue2Style);
            } else {
                // alle lieben Zebras
                if (runningNumber % 2 == 1)
                    sheet1.getRow(ROW_SHEET1_TITLE + runningNumber).getCell(col).setCellStyle(blue2StyleStrike);
                else
                    sheet1.getRow(ROW_SHEET1_TITLE + runningNumber).getCell(col).setCellStyle(strikeStyle);
            }

        }

        return listAntibiotics;
    }


    private Object parseCellContent(String input) {
        Object val;
        try {
            val = Double.parseDouble(input);
        } catch (Exception e) {
            val = input;
        }
        return val;
    }

    private void prepareSheet2() {
        sheet2 = wb.createSheet(WorkbookUtil.createSafeSheetName(SYSTools.xx("prevalence.sheet2.tab.title")));
        sheet2.getPrintSetup().setLandscape(true);
        sheet2.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);

        createRows(sheet2, 1);
        for (int i = 0; i < MAXCOL_SHEET2; i++) {
            sheet2.getRow(0).createCell(i).setCellValue(SYSTools.xx("prevalence.sheet2.col" + String.format("%02d", i + 1) + ".title"));
            sheet2.getRow(0).getCell(i).setCellStyle(rotatedStyle);
        }

    }


    private void prepareWorkbook() {
        wb = new XSSFWorkbook();

        XSSFColor blue1 = new XSSFColor(GUITools.getColor("BFDAEF"));
        XSSFColor blue2 = new XSSFColor(GUITools.getColor("E4DFEB"));
        XSSFColor orange1 = new XSSFColor(GUITools.getColor("FFD3B7"));
        XSSFColor gray1 = new XSSFColor(GUITools.getColor("F2F2F2"));

        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 18);
        titleStyle = wb.createCellStyle();
        titleStyle.setFont(titleFont);

        Font boldFont = wb.createFont();
        boldFont.setFontHeightInPoints((short) 12);
        boldFont.setBold(true);
        CellStyle boldStyle = wb.createCellStyle();
        boldStyle.setFont(boldFont);

        strikeStyle = wb.createCellStyle();
        Font strikeThroughFont = wb.createFont();
        strikeThroughFont.setStrikeout(true);
        strikeStyle.setFont(strikeThroughFont);


        short df = wb.createDataFormat().getFormat("dd.MM.yyyy");
        dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat(df);
        dateStyle.setFont(boldFont);

        rotatedStyle = wb.createCellStyle();
        rotatedStyle.setFont(boldFont);
        rotatedStyle.setRotation((short) 90);
        rotatedStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        rotatedStyle.setAlignment(HorizontalAlignment.CENTER);
        rotatedStyle.setFillForegroundColor(gray1);
        rotatedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        gray1Style = wb.createCellStyle();
        gray1Style.setFillForegroundColor(gray1);
        gray1Style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        blue1Style = wb.createCellStyle();
        blue1Style.setFillForegroundColor(blue1);
        blue1Style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        blue2Style = wb.createCellStyle();
        blue2Style.setFillForegroundColor(blue2);
        blue2Style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        blue2Style = wb.createCellStyle();
        blue2Style.setFillForegroundColor(blue2);
        blue2Style.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        blue2StyleStrike = wb.createCellStyle();
        blue2StyleStrike.setFillForegroundColor(blue2);
        blue2StyleStrike.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        blue2StyleStrike.setFont(strikeThroughFont);

        orange1Style = wb.createCellStyle();
        orange1Style.setFillForegroundColor(orange1);
        orange1Style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        prepareAndFillSheet0();
        prepareSheet1();

    }

    private void prepareSheet1() {


        // sheet1
        sheet1 = wb.createSheet(WorkbookUtil.createSafeSheetName(SYSTools.xx("prevalence.sheet1.tab.title")));
        sheet1.getPrintSetup().setLandscape(true);
        sheet1.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);

        createRows(sheet1, 4); // Überschriften und die beiden Summenzeilen.

        int row1 = ROW_SHEET1_TITLE - 2;
        int row2 = ROW_SHEET1_TITLE - 1;
        int row3 = ROW_SHEET1_TITLE;

        for (int i = 0; i < MAXCOL_SHEET1; i++) {
            sheet1.getRow(row1).createCell(i).setCellValue(SYSTools.xx("prevalence.sheet1.col" + String.format("%02d", i + 1) + ".title"));
            sheet1.getRow(row1).getCell(i).setCellStyle(rotatedStyle);
            sheet1.getRow(row2).createCell(i);
            sheet1.getRow(row2).getCell(i).setCellStyle(blue1Style);
            sheet1.getRow(row3).createCell(i);
            sheet1.getRow(row3).getCell(i).setCellStyle(blue1Style);
        }
    }

    private void prepareAndFillSheet0() {
        // sheet0
        sheet0 = wb.createSheet(WorkbookUtil.createSafeSheetName(SYSTools.xx("prevalence.sheet0.tab.title")));
        sheet0.getPrintSetup().setLandscape(false);
        sheet0.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);

        // Header bis einschl. "Anzahl Einzelzimmer"
        // und je 4 Zeilen für jede Plfegestation, die aktiv ist
        createRows(sheet0, 100);

        sheet0.getRow(ROW_SHEET0_FIRST_LINE_FOR_HEADER).createCell(COL_SHEET0_TITLES).setCellValue(SYSTools.xx("prevalence.sheet0.title"));
        sheet0.getRow(ROW_SHEET0_FIRST_LINE_FOR_HEADER).getCell(COL_SHEET0_TITLES).setCellStyle(titleStyle);

        sheet0.getRow(SHEET0_ROW_TARGETDATE).createCell(COL_SHEET0_TITLES).setCellValue(SYSTools.xx("day.of.elicitation"));
        sheet0.getRow(SHEET0_ROW_TARGETDATE).createCell(COL_SHEET0_TITLES + 1).setCellValue(targetDate.toString("dd.MM.yyyy"));
        sheet0.getRow(SHEET0_ROW_TARGETDATE).getCell(COL_SHEET0_TITLES).setCellStyle(blue1Style);
        sheet0.getRow(SHEET0_ROW_TARGETDATE).getCell(COL_SHEET0_TITLES + 1).setCellStyle(blue1Style);

        sheet0.getRow(SHEET0_ROW_FACILITY_NAME).createCell(COL_SHEET0_TITLES).setCellValue(SYSTools.xx("Name des Pflegeeinrichtung"));
        sheet0.getRow(SHEET0_ROW_FACILITY_NAME).createCell(COL_SHEET0_TITLES + 1).setCellValue(home.getName());
        sheet0.getRow(SHEET0_ROW_FACILITY_NAME).getCell(COL_SHEET0_TITLES).setCellStyle(blue1Style);
        sheet0.getRow(SHEET0_ROW_FACILITY_NAME).getCell(COL_SHEET0_TITLES + 1).setCellStyle(blue1Style);
        sheet0.getRow(SHEET0_ROW_FACILITY_SHORTNAME).createCell(COL_SHEET0_TITLES).setCellValue(SYSTools.xx("Kurzbezeichnung"));
        sheet0.getRow(SHEET0_ROW_FACILITY_SHORTNAME).createCell(COL_SHEET0_TITLES + 1).setCellValue(home.getId()); // todo: das geht so nicht. Ist eine UUID keine shortname
        sheet0.getRow(SHEET0_ROW_FACILITY_SHORTNAME).getCell(COL_SHEET0_TITLES).setCellStyle(blue1Style);
        sheet0.getRow(SHEET0_ROW_FACILITY_SHORTNAME).getCell(COL_SHEET0_TITLES + 1).setCellStyle(blue1Style);
        sheet0.getRow(SHEET0_ROW_FACILITY_STREET).createCell(COL_SHEET0_TITLES).setCellValue(SYSTools.xx("Straße, Hausnr."));
        sheet0.getRow(SHEET0_ROW_FACILITY_STREET).createCell(COL_SHEET0_TITLES + 1).setCellValue(home.getStreet());
        sheet0.getRow(SHEET0_ROW_FACILITY_STREET).getCell(COL_SHEET0_TITLES).setCellStyle(blue1Style);
        sheet0.getRow(SHEET0_ROW_FACILITY_STREET).getCell(COL_SHEET0_TITLES + 1).setCellStyle(blue1Style);
        sheet0.getRow(SHEET0_ROW_FACILITY_ZIPCODE).createCell(COL_SHEET0_TITLES).setCellValue(SYSTools.xx("Postleitzahl"));
        sheet0.getRow(SHEET0_ROW_FACILITY_ZIPCODE).createCell(COL_SHEET0_TITLES + 1).setCellValue(home.getZip());
        sheet0.getRow(SHEET0_ROW_FACILITY_ZIPCODE).getCell(COL_SHEET0_TITLES).setCellStyle(blue1Style);
        sheet0.getRow(SHEET0_ROW_FACILITY_ZIPCODE).getCell(COL_SHEET0_TITLES + 1).setCellStyle(blue1Style);
        sheet0.getRow(SHEET0_ROW_FACILITY_CITY).createCell(COL_SHEET0_TITLES).setCellValue(SYSTools.xx("Ort"));
        sheet0.getRow(SHEET0_ROW_FACILITY_CITY).createCell(COL_SHEET0_TITLES + 1).setCellValue(home.getCity());
        sheet0.getRow(SHEET0_ROW_FACILITY_CITY).getCell(COL_SHEET0_TITLES).setCellStyle(blue1Style);
        sheet0.getRow(SHEET0_ROW_FACILITY_CITY).getCell(COL_SHEET0_TITLES + 1).setCellStyle(blue1Style);

        sheet0.getRow(SHEET0_ROW_BEDS_TOTAL).createCell(COL_SHEET0_TITLES).setCellValue(SYSTools.xx("Gesamtzahl Betten Pflegeeinrichtung"));
        sheet0.getRow(SHEET0_ROW_BEDS_IN_USE).createCell(COL_SHEET0_TITLES).setCellValue(SYSTools.xx("Belegte Betten am Tag der Erhebung"));
        sheet0.getRow(SHEET0_ROW_NUM_ROOMS).createCell(COL_SHEET0_TITLES).setCellValue(SYSTools.xx("Anzahl Patientenzimmer"));
        sheet0.getRow(SHEET0_ROW_NUM_SINGLE_ROOMS).createCell(COL_SHEET0_TITLES).setCellValue(SYSTools.xx("Anzahl Einzelzimmer"));

        sheet0.getRow(SHEET0_ROW_NUM_ROOMS).createCell(COL_SHEET0_TITLES + 1).setCellValue(roomsTotal);
        sheet0.getRow(SHEET0_ROW_NUM_SINGLE_ROOMS).createCell(COL_SHEET0_TITLES + 1).setCellValue(singleRooms);


        sheet0.getRow(SHEET0_ROW_TARGETDATE).createCell(COL_SHEET0_TITLES + 4).setCellValue(SYSTools.xx("prevalence.sheet0.opderef.line1"));
        sheet0.getRow(SHEET0_ROW_TARGETDATE + 1).createCell(COL_SHEET0_TITLES + 4).setCellValue(SYSTools.xx("prevalence.sheet0.opderef.line2"));
        sheet0.getRow(SHEET0_ROW_TARGETDATE + 2).createCell(COL_SHEET0_TITLES + 4).setCellValue(SYSTools.xx("prevalence.sheet0.opderef.line3"));
        sheet0.getRow(SHEET0_ROW_TARGETDATE + 3).createCell(COL_SHEET0_TITLES + 4).setCellValue(SYSTools.xx("prevalence.sheet0.opderef.line4"));

        XSSFCell linkcell = (XSSFCell) sheet0.getRow(SHEET0_ROW_TARGETDATE + 3).getCell(COL_SHEET0_TITLES + 4);
        CreationHelper createHelper = wb.getCreationHelper();

        XSSFHyperlink link = (XSSFHyperlink) createHelper.createHyperlink(HyperlinkType.URL);
        link.setAddress(SYSTools.xx("prevalence.sheet0.opderef.line4"));
        linkcell.setHyperlink(link);

        List<Floors> floors = home.getFloors();
        Collections.sort(floors);

        int bedsTotal = 0;
        int bedsInUse = 0;
        int index = 0;
        int runningStationIndex = 1; // einfach eine laufende Nummer, die das "Deckblatt" mit den "Prävalenzdaten" verbindet.
        for (Floors floor : floors) {
            sheet0.getRow(SHEET0_START_OF_LIST + index).createCell(COL_SHEET0_TITLES).setCellValue(home.getId() + " , " + floor.getName());
            sheet0.getRow(SHEET0_START_OF_LIST + index).getCell(COL_SHEET0_TITLES).setCellStyle(orange1Style);
            sheet0.getRow(SHEET0_START_OF_LIST + index).createCell(COL_SHEET0_TITLES + 1).setCellValue(runningStationIndex);
            sheet0.getRow(SHEET0_START_OF_LIST + index).getCell(COL_SHEET0_TITLES + 1).setCellStyle(orange1Style);

            sheet0.getRow(SHEET0_START_OF_LIST + index + 1).createCell(COL_SHEET0_TITLES).setCellValue(SYSTools.xx("Anzahl Betten"));
            sheet0.getRow(SHEET0_START_OF_LIST + index + 1).getCell(COL_SHEET0_TITLES).setCellStyle(orange1Style);
            sheet0.getRow(SHEET0_START_OF_LIST + index + 1).createCell(COL_SHEET0_TITLES + 1).setCellValue(bedsTotalPerLevel[floor.getLevel()]);
            sheet0.getRow(SHEET0_START_OF_LIST + index + 1).getCell(COL_SHEET0_TITLES + 1).setCellStyle(orange1Style);

            sheet0.getRow(SHEET0_START_OF_LIST + index + 2).createCell(COL_SHEET0_TITLES).setCellValue(SYSTools.xx("Belegte Betten am Tag der Erhebung"));
            sheet0.getRow(SHEET0_START_OF_LIST + index + 2).getCell(COL_SHEET0_TITLES).setCellStyle(orange1Style);
            sheet0.getRow(SHEET0_START_OF_LIST + index + 2).createCell(COL_SHEET0_TITLES + 1).setCellValue(bedsInUserPerLevel[floor.getLevel()]);
            sheet0.getRow(SHEET0_START_OF_LIST + index + 2).getCell(COL_SHEET0_TITLES + 1).setCellStyle(orange1Style);
            index += 4;
            stationIndex.put(floor, runningStationIndex); // für die Zuordnung auf Sheet1, Spalte 0
            runningStationIndex++;
            bedsTotal += bedsTotalPerLevel[floor.getLevel()];
            bedsInUse += bedsInUserPerLevel[floor.getLevel()];
        }

        sheet0.getRow(SHEET0_ROW_BEDS_TOTAL).createCell(COL_SHEET0_TITLES + 1).setCellValue(bedsTotal);
        sheet0.getRow(SHEET0_ROW_BEDS_IN_USE).createCell(COL_SHEET0_TITLES + 1).setCellValue(bedsInUse);

    }

    private void createRows(Sheet sheet, int num) {

        int offset = sheet.getLastRowNum();

        for (int row = 0; row <= num; row++) {
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
