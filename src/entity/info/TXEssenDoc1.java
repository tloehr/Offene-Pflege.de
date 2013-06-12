package entity.info;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import entity.files.SYSFilesTools;
import entity.nursingprocess.*;
import entity.prescription.*;
import entity.values.ResValueTools;
import op.OPDE;
import op.care.info.PnlBodyScheme;
import op.system.PDF;
import op.tools.Pair;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.lang.ArrayUtils;

import javax.persistence.EntityManager;
import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 03.06.13
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class TXEssenDoc1 {

    public static final String SOURCEFILENAME = "ueberleitungsbogen210809.pdf";
    private final HashMap<ResInfo, Properties> mapInfo2Properties;
    private Resident resident;
    private HashMap<String, String> content;
    //    private ArrayList<ResInfo> listAllInfos;
    private ArrayList<ResInfo> listICD;
    private HashMap<Integer, ResInfo> mapID2Info;

    private final Font pdf_font_small = new Font(Font.FontFamily.HELVETICA, 6);
    private final Font pdf_font_small_bold = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD);
    private final Font pdf_font_normal_bold = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
    private PdfContentByte over = null;
    private PdfWriter writer = null;
    PdfStamper stamper = null;


    public static final String[] PARTS = new String[]{"head.left.side", "shoulder.left.side", "upper.back.left.side", "ellbow.side.left", "hand.left.side", "hip.left.side", "bottom.left.side", "upper.leg.left.side",
            "lower.leg.left.side", "calf.left.side", "heel.left.side", "face", "shoulder.front.right", "shoulder.front.left", "upper.belly", "crook.arm.right",
            "crook.arm.left", "lower.belly", "groin", "upper.leg.right.front", "upper.leg.left.front", "knee.right", "knee.left", "shin.right.front", "shin.left.front",
            "foot.right.front", "foot.left.front", "back.of.the.head", "shoulder.back.left", "shoulder.back.right", "back.mid", "ellbow.left",
            "ellbow.right", "back.low", "bottom.back", "upper.leftleg.back", "upper.rightleg.back", "knee.hollowleft", "knee.hollowright", "calf.leftback",
            "calf.rightback", "foot.leftback", "foot.rightback", "head.right.side", "shoulder.right.side", "back.upper.left.side", "ellbow.rightside",
            "hand.right.side", "hip.right.side", "bottom.right.side", "upper.leg.right.side", "lower.leg.right.side", "calf.right.side", "heel.right.side"};


    public TXEssenDoc1(Resident resident) {
        this.resident = resident;
        content = new HashMap<String, String>();
//        listAllInfos.addAll(ResInfoTools.getAll(resident));
        listICD = new ArrayList<ResInfo>();
        mapID2Info = new HashMap<Integer, ResInfo>();
        mapInfo2Properties = new HashMap<ResInfo, Properties>();

        for (ResInfo info : ResInfoTools.getAllActive(resident)) {
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            File stamperFile = new File(OPDE.getOPWD() + File.separator + OPDE.SUBDIR_CACHE + File.separator + "TX_" + resident.getRID() + "_" + sdf.format(new Date()) + ".pdf");
            stamper = new PdfStamper(new PdfReader(OPDE.getOPWD() + File.separator + OPDE.SUBDIR_TEMPLATES + File.separator + SOURCEFILENAME), new FileOutputStream(stamperFile));

            createContent4Section1();
            createContent4Section2();
            createContent4Section3();
            createContent4Section4();
            createContent4Section5();
            createContent4Section6();
            createContent4Section7();
            createContent4Section8();
            createContent4Section9();
            createContent4Section10();
            createContent4Section11();
            createContent4Section12();
            createContent4Section13();
            createContent4Section14();
            createContent4Section15();
            createContent4Section16();
            createContent4Section17();
            createContent4Section18();
            createContent4Section19();

            createContent4SectionICD();
            createContent4Meds();

            fillDataIntoPDF();
            fillWoundsIntoPDF();

            stamper.setFormFlattening(true);

            stamper.close();

            mapInfo2Properties.clear();
            listICD.clear();
            content.clear();

            SYSFilesTools.handleFile(stamperFile, Desktop.Action.OPEN);
        } catch (Exception e) {
            OPDE.fatal(e);
        }

    }

    private void fillDataIntoPDF() throws Exception {
        AcroFields form = stamper.getAcroFields();
        for (String key : content.keySet()) {
            if (!ArrayUtils.contains(PnlBodyScheme.PARTS, key)) { // this is a special case. The bodyparts and the pdfkeys have the same name.
                form.setField(key, content.get(key));


            }
        }

    }

    private void fillWoundsIntoPDF() {

    }


    /**
     * fills the usual stuff like resident name, insurances, dob and the rest on all three pages.
     * filling means, putting pairs into the content HashMap.
     * <p/>
     * Contains also "1 Soziale Aspekte"
     */
    private void createContent4Section1() {
        content.put(RESIDENT_GENDER, resident.getGender() == ResidentTools.MALE ? "1" : "0");
        content.put(RESIDENT_FIRSTNAME, resident.getFirstname());
        content.put(PAGE2_RESIDENT_FIRSTNAME, resident.getFirstname());
        content.put(RESIDENT_NAME, resident.getName());
        content.put(PAGE2_RESIDENT_NAME, resident.getName());
        content.put(PAGE3_RESIDENT_FULLNAME, ResidentTools.getFullName(resident));
        content.put(RESIDENT_DOB, DateFormat.getDateInstance().format(resident.getDOB()));
        content.put(PAGE2_RESIDENT_DOB, DateFormat.getDateInstance().format(resident.getDOB()));
        content.put(PAGE3_RESIDENT_DOB, DateFormat.getDateInstance().format(resident.getDOB()));
        if (resident.isActive()) {
            content.put(RESIDENT_STREET, resident.getStation().getHome().getStreet());
            content.put(RESIDENT_CITY, resident.getStation().getHome().getCity());
            content.put(RESIDENT_ZIP, resident.getStation().getHome().getZIP());
            content.put(RESIDENT_PHONE, resident.getStation().getHome().getTel());
            content.put(PAGE2_PHONE, resident.getStation().getHome().getTel());
        }
        content.put(TX_DATE, DateFormat.getDateInstance().format(new Date()));
        content.put(PAGE2_DATE, DateFormat.getDateInstance().format(new Date()));
        content.put(PAGE3_TX_DATE, DateFormat.getDateInstance().format(new Date()));
        content.put(PAGE3_TX2_DATE, DateFormat.getDateInstance().format(new Date()));
        content.put(TX_TIME, DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()));

        content.put(RESIDENT_HINSURANCE, getValue(ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "hiname"));
        content.put(PAGE3_RESIDENT_HINSURANCE, getValue(ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "hiname"));
        content.put(RESIDENT_HINSURANCEID, getValue(ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "personno"));
        content.put(RESIDENT_HINSURANCENO, getValue(ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "insuranceno"));

        content.put(COMMS_MOTHERTONGUE, getValue(ResInfoTypeTools.TYPE_COMMS, "mothertongue"));
        content.put(SOCIAL_RELIGION, getValue(ResInfoTypeTools.TYPE_PERSONALS, "konfession"));

        content.put(LEGAL_SINGLE, setCheckbox(getValue(ResInfoTypeTools.TYPE_PERSONALS, "single")));
        content.put(LEGAL_MINOR, setCheckbox(ResidentTools.isMinor(resident)));

        content.put(LC_GENERAL, setCheckbox(mapID2Info.containsKey(ResInfoTypeTools.TYPE_LEGALCUSTODIANS)));
        content.put(LC_FINANCE, setCheckbox(getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "finance")));
        content.put(LC_HEALTH, setCheckbox(getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "health")));
        content.put(LC_CUSTODY, setCheckbox(getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "confinement")));
        content.put(LC_NAME, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "name"));
        content.put(LC_FIRSTNAME, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "firstname"));
        content.put(LC_PHONE, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "tel"));
        content.put(LC_STREET, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "street"));
        content.put(LC_ZIP, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "zip"));
        content.put(LC_CITY, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "city"));

        content.put(CONFIDANT_NAME, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1name"));
        content.put(CONFIDANT_FIRSTNAME, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1firstname"));
        content.put(CONFIDANT_STREET, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1street"));
        content.put(CONFIDANT_ZIP, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1zip"));
        content.put(CONFIDANT_CITY, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1city"));
        content.put(CONFIDANT_PHONE, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1tel"));
        content.put(SOCIAL_CONFIDANT_CARE, setCheckbox(getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1ready2nurse")));

        content.put(SOCIAL_CURRENT_RESTHOME, "1");

        content.put(DATE_REQUESTED_INSURANCE_GRADE, getValue(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "requestdate"));
        content.put(ASSIGNED_INSURANCE_GRADE, getValue(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "result"));

        String grade = getValue(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade");
        grade = grade.equalsIgnoreCase("refused") ? "none" : grade; // there is no such thing as refused request in this document.
        content.put(ASSIGNED_INSURANCE_GRADE, setRadiobutton(grade, new String[]{"none", "assigned", "requested"}));
        content.put(DATE_REQUESTED_INSURANCE_GRADE_PAGE3, setCheckbox(grade.equalsIgnoreCase("requested")));
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
        content.put(PERSONAL_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_CARE, "personal.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(PERSONAL_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "personal.care.bed")));
        content.put(PERSONAL_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "personal.care.shower")));
        content.put(PERSONAL_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "personal.care.basin")));
        content.put(MOUTH_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "mouth.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(MOUTH_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "mouth.care.bed")));
        content.put(MOUTH_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "mouth.care.shower")));
        content.put(MOUTH_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "mouth.care.basin")));
        content.put(DENTURE_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "denture.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(DENTURE_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "denture.care.bed")));
        content.put(DENTURE_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "denture.care.shower")));
        content.put(DENTURE_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "denture.care.basin")));
        content.put(COMBING_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_CARE, "combing.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(COMBING_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "combing.care.bed")));
        content.put(COMBING_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "combing.care.shower")));
        content.put(COMBING_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "combing.care.basin")));
        content.put(SHAVE_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_CARE, "shave.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(SHAVE_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "shave.care.bed")));
        content.put(SHAVE_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "shave.care.shower")));
        content.put(SHAVE_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "shave.care.basin")));
        content.put(DRESSING_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_CARE, "dressing.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(DRESSING_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "dressing.care.bed")));
        content.put(DRESSING_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "dressing.care.shower")));
        content.put(DRESSING_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "dressing.care.basin")));

        content.put(SKIN_DRY, setCheckbox(getValue(ResInfoTypeTools.TYPE_SKIN, "skin.dry")));
        content.put(SKIN_GREASY, setCheckbox(getValue(ResInfoTypeTools.TYPE_SKIN, "skin.greasy")));
        content.put(SKIN_ITCH, setCheckbox(getValue(ResInfoTypeTools.TYPE_SKIN, "skin.itch")));
        content.put(SKIN_NORMAL, setCheckbox(getValue(ResInfoTypeTools.TYPE_SKIN, "skin.normal")));

        content.put(PREFERRED_CAREPRODUCTS, getValue(ResInfoTypeTools.TYPE_CARE, "preferred.careproducts"));


    }


    /**
     * mobilty
     */
    private void createContent4Section4() {
        content.put(MOBILITY_GET_UP, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "stand"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(MOBILITY_AID_GETUP, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "stand.aid")));
        content.put(MOBILITY_WALKING, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "walk"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(MOBILITY_AID_WALKING, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "walk.aid")));
        content.put(MOBILITY_TRANSFER, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "transfer"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(MOBILITY_AID_TRANSFER, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "transfer.aid")));
        content.put(MOBILITY_TOILET, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "toilet"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(MOBILITY_AID_TOILET, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "toilet.aid")));
        content.put(MOBILITY_SITTING, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "sitting"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(MOBILITY_AID_SITTING, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "sitting.aid")));
        content.put(MOBILITY_BED, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "bedmovement"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(MOBILITY_AID_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "bedmovement.aid")));

        content.put(MOBILITY_AID_CRUTCH, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "crutch.aid")));
        content.put(MOBILITY_AID_CANE, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "cane.aid")));
        content.put(MOBILITY_AID_WHEELCHAIR, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "wheel.aid")));
        content.put(MOBILITY_AID_COMMODE, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "commode.aid"))); // <- from other section
        content.put(MOBILITY_AID_WALKER, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "walker.aid")));
        content.put(MOBILITY_AID_COMMENT, getValue(ResInfoTypeTools.TYPE_MOBILITY, "other.aid"));
        content.put(MOBILITY_BEDRIDDEN, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "bedridden")));

        String mobilityMeasures = "";
        long prev = -1;
        for (InterventionSchedule is : InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_MOBILITY)) {
            if (is.getIntervention().getMassID() != prev) {
                prev = is.getIntervention().getMassID();
                if (!mobilityMeasures.isEmpty()) {
                    mobilityMeasures += "; ";
                }
                mobilityMeasures += is.getIntervention().getBezeichnung() + ": ";
            }
            mobilityMeasures += InterventionScheduleTools.getTerminAsCompactText(is) + ", ";
        }
        content.put(MOBILITY_BEDPOSITION, mobilityMeasures.isEmpty() ? "--" : mobilityMeasures.substring(0, mobilityMeasures.length() - 2)); // the last ", " has to be cut off again
    }

    /**
     * excretiions
     */
    private void createContent4Section5() {
        boolean weightControl = !PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_WEIGHT_MONITORING).isEmpty() ||
                !InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_WEIGHT_MONITORING).isEmpty();
        content.put(EXCRETIONS_CONTROL_WEIGHT, setCheckbox(weightControl));
        content.put(MONITORING_WEIGHT, setCheckbox(weightControl));

        Properties controlling = resident.getControlling();
        content.put(EXCRETIONS_LIQUID_BALANCE, setCheckbox(SYSTools.catchNull(controlling.getProperty("liquidbalance")).equalsIgnoreCase("on")));

        content.put(EXCRETIONS_AID_BEDPAN, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "bedpan.aid")));
        content.put(EXCRETIONS_AID_COMMODE, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "commode.aid")));
        content.put(EXCRETIONS_AID_URINAL, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "urinal.aid")));

        boolean noAid = getValue(ResInfoTypeTools.TYPE_INCOAID, "bedpan.aid").equalsIgnoreCase("false") &&
                getValue(ResInfoTypeTools.TYPE_INCOAID, "commode.aid").equalsIgnoreCase("false") &&
                getValue(ResInfoTypeTools.TYPE_INCOAID, "urinal.aid").equalsIgnoreCase("false");
        content.put(EXCRETIONS_AID_NO, setCheckbox(noAid));

        content.put(EXCRETIONS_DIARRHOEA_TENDENCY, setCheckbox(getValue(ResInfoTypeTools.TYPE_EXCRETIONS, "diarrhoe")));
        content.put(EXCRETIONS_OBSTIPATION_TENDENCY, setCheckbox(getValue(ResInfoTypeTools.TYPE_EXCRETIONS, "obstipation")));
        content.put(EXCRETIONS_DIGITAL, setCheckbox(getValue(ResInfoTypeTools.TYPE_EXCRETIONS, "digital")));

        boolean inco_urine = (mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_PROFILE_DAY) && !getValue(ResInfoTypeTools.TYPE_INCO_PROFILE_DAY, "inkoprofil").equalsIgnoreCase("kontinenz")) ||
                (mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT) && !getValue(ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT, "inkoprofil").equalsIgnoreCase("kontinenz"));
        boolean inco_faecal = (mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_FAECAL) && !getValue(ResInfoTypeTools.TYPE_INCO_FAECAL, "incolevel").equalsIgnoreCase("0"));
        content.put(EXCRETIONS_INCO_URINE, setCheckbox(inco_urine));
        content.put(EXCRETIONS_INCO_FAECAL, setCheckbox(inco_faecal));

        content.put(EXCRETIONS_INCOAID_NEEDSHELP, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "needshelp")));
        content.put(EXCRETIONS_INCOAID_SELF, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "needshelp").equalsIgnoreCase("false")));
        content.put(EXCRETIONS_TRANS_AID, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "trans.aid")));
        content.put(EXCRETIONS_SUP_AID, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "sup.aid")));


        boolean diapers = getValue(ResInfoTypeTools.TYPE_INCOAID, "windel").equalsIgnoreCase("true");
        boolean pads1 = getValue(ResInfoTypeTools.TYPE_INCOAID, "vorlagen1").equalsIgnoreCase("true") ||
                getValue(ResInfoTypeTools.TYPE_INCOAID, "vorlagen2").equalsIgnoreCase("true") ||
                getValue(ResInfoTypeTools.TYPE_INCOAID, "vorlagen3").equalsIgnoreCase("true");
        boolean pads2 = getValue(ResInfoTypeTools.TYPE_INCOAID, "dbinden").equalsIgnoreCase("true");
        boolean undersheet = getValue(ResInfoTypeTools.TYPE_INCOAID, "krunterlagen").equalsIgnoreCase("true");

        String incoaidtext = (diapers ? OPDE.lang.getString("misc.msg.diaper") + ", " : "") +
                (pads1 ? OPDE.lang.getString("misc.msg.incopad") + ", " : "") +
                (pads2 ? OPDE.lang.getString("misc.msg.sanitarypads") + ", " : "") +
                (undersheet ? OPDE.lang.getString("misc.msg.undersheet") + ", " : "");

        content.put(EXCRETIONS_ONEWAY_AID, setCheckbox(diapers || pads1 || pads2 || undersheet));
        content.put(EXCRETIONS_CURRENT_USED_AID, incoaidtext.isEmpty() ? "--" : incoaidtext.substring(0, incoaidtext.length() - 2));

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
                content.put(EXCRETIONS_LASTCHANGE, DateFormat.getDateInstance().format(lastChange));
            } else {
                content.put(EXCRETIONS_LASTCHANGE, "--");
            }
        }
        presCatheterChange.clear();
    }

    private void createContent4Section6() {
        content.put(PROPH_CONTRACTURE, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_CONTRACTURE).isEmpty()));
        content.put(PROPH_BEDSORE, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_BEDSORE).isEmpty()));
        content.put(PROPH_SOOR, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_SOOR).isEmpty()));
        content.put(PROPH_THROMBOSIS, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_THROMBOSIS).isEmpty()));
        content.put(PROPH_PNEUMONIA, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_PNEUMONIA).isEmpty()));
        content.put(PROPH_INTERTRIGO, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_INTERTRIGO).isEmpty()));
        content.put(PROPH_FALL, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_FALL).isEmpty()));
        content.put(PROPH_OBSTIPATION, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_OBSTIPATION).isEmpty()));
    }

    /**
     * bedsore
     */
    private void createContent4Section7() {
        String braden = "Braden: " + getValue(ResInfoTypeTools.TYPE_SCALE_BRADEN, "scalesum") + " (" + getValue(ResInfoTypeTools.TYPE_SCALE_BRADEN, "risk") + ")";
        content.put(RISKSCALE_TYPE_BEDSORE, braden);
        int rating;
        try {
            rating = Integer.parseInt(getValue(ResInfoTypeTools.TYPE_SCALE_BRADEN, "rating"));
        } catch (NumberFormatException nfe) {
            rating = 0;
        }
        content.put(SCALE_RISK_BEDSORE, setCheckbox(rating > 0));

        boolean bedsore = false;
        for (int type : ResInfoTypeTools.TYPE_ALL_WOUNDS) {
            bedsore |= setCheckbox(type).equals("1");
        }
        content.put(BEDSORE, setCheckbox(bedsore));
    }

    /**
     * sleep
     */
    private void createContent4Section8() {
        content.put(SLEEP_NORMAL, setCheckbox(getValue(ResInfoTypeTools.TYPE_SLEEP, "normal")));

        boolean insomnia = getValue(ResInfoTypeTools.TYPE_SLEEP, "einschlaf").equalsIgnoreCase("true") || getValue(ResInfoTypeTools.TYPE_SLEEP, "durchschlaf").equalsIgnoreCase("true");
        content.put(SLEEP_INSOMNIA, setCheckbox(insomnia));

        boolean restless = getValue(ResInfoTypeTools.TYPE_SLEEP, "unruhe").equalsIgnoreCase("true") || getValue(ResInfoTypeTools.TYPE_SLEEP, "daynight").equalsIgnoreCase("true");
        content.put(SLEEP_RESTLESS, setCheckbox(restless));

        content.put(SLEEP_POS_LEFT, setCheckbox(getValue(ResInfoTypeTools.TYPE_SLEEP, "left")));
        content.put(SLEEP_POS_FRONT, setCheckbox(getValue(ResInfoTypeTools.TYPE_SLEEP, "front")));
        content.put(SLEEP_POS_BACK, setCheckbox(getValue(ResInfoTypeTools.TYPE_SLEEP, "back")));
        content.put(SLEEP_POS_RIGHT, setCheckbox(getValue(ResInfoTypeTools.TYPE_SLEEP, "right")));

        content.put(SLEEP_COMMENTS, setCheckbox(getValue(ResInfoTypeTools.TYPE_SLEEP, "schlafhilfen")));

    }

    /**
     * food
     */
    private void createContent4Section9() {
        content.put(FOOD_ASSISTANCE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_FOOD, "transfer"), new String[]{"none", "needsmotivation", "needshelp", "completehelp"}));
        content.put(FOOD_DYSPHAGIA, setCheckbox(getValue(ResInfoTypeTools.TYPE_FOOD, "dysphagia")));
        content.put(FOOD_BITESIZE, setCheckbox(getValue(ResInfoTypeTools.TYPE_FOOD, "bitesize")));

        Date lastMeal = SYSConst.DATE_THE_VERY_BEGINNING;

        BHP bhp = BHPTools.getLastBHP(resident, InterventionTools.FLAG_FOOD_CONSUMPTION);
        DFN dfn = DFNTools.getLastDFN(resident, InterventionTools.FLAG_FOOD_CONSUMPTION);

        lastMeal = new Date(Math.max(lastMeal.getTime(), (bhp == null ? 0 : bhp.getIst().getTime())));
        lastMeal = new Date(Math.max(lastMeal.getTime(), (dfn == null ? 0 : dfn.getIst().getTime())));

        if (!lastMeal.equals(SYSConst.DATE_THE_VERY_BEGINNING)) {
            content.put(FOOD_LAST_MEAL, DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(lastMeal));
        } else {
            content.put(FOOD_LAST_MEAL, "--");
        }


        BigDecimal foodml = BigDecimal.ZERO;
        ArrayList<Prescription> listPresGavageFood = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GAVAGE_FOOD_500ML);
        for (Prescription p : listPresGavageFood) {
            for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                foodml = foodml.add(ps.getOverAllDoseSum());
            }
        }
        listPresGavageFood.clear();

        BigDecimal liquidml = BigDecimal.ZERO;
        ArrayList<Prescription> listPresGavageLiquid = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GAVAGE_LIQUID_500ML);
        for (Prescription p : listPresGavageLiquid) {
            for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                liquidml = liquidml.add(ps.getOverAllDoseSum());
            }
        }
        listPresGavageLiquid.clear();

        content.put(FOOD_ARTIFICIAL_FEEDING, setCheckbox(foodml.compareTo(BigDecimal.ZERO) > 0));
        content.put(FOOD_DAILY_ML, setBD(foodml, "ml"));
        content.put(FOOD_TEE_DAILY_ML, setBD(liquidml, "ml"));


        String tubetype = "--";
        if (!getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "bitesize").equals("--")) {
            String langKey = "misc.msg." + getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "tubetype");
            tubetype = OPDE.lang.getString(langKey);
        }

        content.put(FOOD_TUBETYPE, tubetype);
        content.put(FOOD_TUBESINCE, getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "tubesince"));
        content.put(FOOD_PUMP, setCheckbox(getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "pump")));
        content.put(FOOD_SYRINGE, setCheckbox(getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "syringe")));
        content.put(FOOD_GRAVITY, setCheckbox(getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "gravity")));

        content.put(FOOD_DAILY_KCAL, getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "calories"));
        content.put(FOOD_ORALNUTRITION, setCheckbox(getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "oralnutrition")));
        content.put(FOOD_BREADUNTIS, getValue(ResInfoTypeTools.TYPE_FOOD, "breadunit"));
        content.put(FOOD_LIQUIDS_DAILY_ML, getValue(ResInfoTypeTools.TYPE_FOOD, "zieltrinkmenge"));
        content.put(FOOD_BMI, setBD(ResValueTools.getBMI(resident), ""));


        content.put(FOOD_PARENTERAL, setCheckbox(getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "parenteral")));
        content.put(FOOD_DRINKSALONE, setCheckbox(getValue(ResInfoTypeTools.TYPE_FOOD, "drinksalone")));
        content.put(FOOD_ABROSIA, setCheckbox(getValue(ResInfoTypeTools.TYPE_ARTIFICIAL_NUTRTITION, "abrosia")));
        content.put(FOOD_DRINKINGMOTIVATION, setCheckbox(getValue(ResInfoTypeTools.TYPE_FOOD, "motivationdrinking")));

    }

    /**
     * special aspects
     */
    private void createContent4Section10() {
        content.put(SPECIAL_MRE, setRadiobutton(getValue(ResInfoTypeTools.TYPE_INFECTION, "multiresistant"), new String[]{"no", "yes", "notchecked"}));
        content.put(SPECIAL_YESNO_ALLERGY, setCheckbox(mapID2Info.containsKey(ResInfoTypeTools.TYPE_ALLERGY)));
        content.put(SPECIAL_ALLERGIEPASS, setCheckbox(getValue(ResInfoTypeTools.TYPE_ALLERGY, "allergiepass")));
        content.put(SPECIAL_COMMENT_ALLERGY, getValue(ResInfoTypeTools.TYPE_ALLERGY, "beschreibung"));

        content.put(SPECIAL_MYCOSIS, setCheckbox(getValue(ResInfoTypeTools.TYPE_INFECTION, "mycosis")));
        content.put(SPECIAL_WOUNDS, setCheckbox(hasWounds()));
        content.put(SPECIAL_WOUNDPAIN, setCheckbox(hasWoundPain()));
        content.put(SPECIAL_PACER, setCheckbox(getValue(ResInfoTypeTools.TYPE_PACEMAKER, "pacemaker")));
        content.put(SPECIAL_LASTCONTROL_PACER, setCheckbox(getValue(ResInfoTypeTools.TYPE_PACEMAKER, "lastcheck")));
    }

    /**
     * consciousness
     */
    private void createContent4Section11() {
        content.put(CONSCIOUSNESS_AWAKE, setCheckbox(getValue(ResInfoTypeTools.TYPE_CONSCIUOS, "awake")));
        content.put(CONSCIOUSNESS_SOPOR, setCheckbox(getValue(ResInfoTypeTools.TYPE_CONSCIUOS, "sopor")));
        content.put(CONSCIOUSNESS_COMA, setCheckbox(getValue(ResInfoTypeTools.TYPE_CONSCIUOS, "coma")));
        content.put(CONSCIOUSNESS_SOMNOLENT, setCheckbox(getValue(ResInfoTypeTools.TYPE_CONSCIUOS, "somnolent")));

        content.put(COMMS_SPEECH_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_COMMS, "ability1"), new String[]{"oE1", "mE1", "zE1"}));
        content.put(COMMS_UNDERSTANDING_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_COMMS, "ability2"), new String[]{"oE2", "mE2", "zE2"}));
        content.put(COMMS_HEARING_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_COMMS, "ability3"), new String[]{"oE3", "mE3", "zE3"}));
        content.put(COMMS_SEEING_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_COMMS, "ability4"), new String[]{"oE4", "mE4", "zE4"}));
        content.put(COMMS_WRITING_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_COMMS, "ability5"), new String[]{"oE5", "mE5", "zE5"}));

        content.put(ORIENTATION_TIME_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_ORIENTATION, "time"), new String[]{"no1", "yes1", "intermittent1"}));
        content.put(ORIENTATION_PERSONAL_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_ORIENTATION, "personal"), new String[]{"no2", "yes2", "intermittent2"}));
        content.put(ORIENTATION_LOCATION_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_ORIENTATION, "location"), new String[]{"no3", "yes3", "intermittent3"}));
        content.put(ORIENTATION_SITUATION_ABILITY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_ORIENTATION, "situation"), new String[]{"no4", "yes4", "intermittent4"}));
        content.put(ORIENTATION_RUNNAWAY_TENDENCY, setRadiobutton(getValue(ResInfoTypeTools.TYPE_ORIENTATION, "runaway"), new String[]{"no5", "yes5", "intermittent5"}));
    }


    /**
     * respiration
     */
    private void createContent4Section12() {
        content.put(RESPIRATION_NORMAL, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "awake")));
        content.put(RESPIRATION_CARDCONGEST, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "cardcongest")));
        content.put(RESPIRATION_PAIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "pain")));
        content.put(RESPIRATION_COUGH, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "cough")));
        content.put(RESPIRATION_MUCOUS, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "mucous")));
        content.put(RESPIRATION_SPUTUM, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "sputum")));
        content.put(RESPIRATION_SMOKING, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "smoking")));
        content.put(RESPIRATION_ASTHMA, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "asthma")));
        content.put(RESPIRATION_COMMENT, mapID2Info.containsKey(ResInfoTypeTools.TYPE_RESPIRATION) ? SYSTools.catchNull(mapID2Info.get(ResInfoTypeTools.TYPE_RESPIRATION).getText()) : "");

        content.put(RESPIRATION_STOMA, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "stoma")));
        content.put(RESPIRATION_SILVERTUBE, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "silver")));
        content.put(RESPIRATION_SILICONTUBE, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "silicon")));
        content.put(RESPIRATION_ASPIRATE, setCheckbox(getValue(ResInfoTypeTools.TYPE_RESPIRATION, "aspirate")));

        content.put(RESPIRATION_TUBESIZE, getValue(ResInfoTypeTools.TYPE_RESPIRATION, "tubesize"));
        content.put(RESPIRATION_TUBETYPE, getValue(ResInfoTypeTools.TYPE_RESPIRATION, "tubetype"));

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

        content.put(MONITORING_BP, setCheckbox(bp));
        content.put(MONITORING_PORT, setCheckbox(port));
        content.put(MONITORING_RESPIRATION, setCheckbox(respiration));
        content.put(MONITORING_PULSE, setCheckbox(pulse));
        content.put(MONITORING_TEMP, setCheckbox(temp));
        content.put(MONITORING_WEIGHT, setCheckbox(weight));
        content.put(MONITORING_PAIN, setCheckbox(pain));

        Properties controlling = resident.getControlling();
        content.put(MONITORING_INTAKE, setCheckbox(SYSTools.catchNull(controlling.getProperty("liquidbalance")).equalsIgnoreCase("on")));
        content.put(MONITORING_EXCRETION, setCheckbox(SYSTools.catchNull(controlling.getProperty("liquidbalance")).equalsIgnoreCase("on")));

    }

    /**
     * special monitoring
     */
    private void createContent4Section14() {
        boolean logo = !InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_THERAPY_LOGOPEDICS).isEmpty();
        boolean physio = !InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_THERAPY_PHYSIO).isEmpty();
        boolean ergo = !InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_THERAPY_ERGO).isEmpty();

        content.put(THERAPY_ERGO, setCheckbox(ergo));
        content.put(THERAPY_LOGO, setCheckbox(logo));
        content.put(THERAPY_PHYSIO, setCheckbox(physio));
    }


    private void createContent4Section15() {
        // nothing yet
    }


    /**
     * meds / diabetes
     */
    private void createContent4Section16() {
        content.put(MEDS_INSULIN_APPLICATION, setRadiobutton(getValue(ResInfoTypeTools.TYPE_DIABETES, "application"), new String[]{"pen", "syringe", "pump", "none"}));
        content.put(MEDS_INJECTION_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MEDS, "injection"), new String[]{"none", "lvl1", "lvl3"}));
        content.put(MEDS_SELF, setCheckbox(getValue(ResInfoTypeTools.TYPE_MEDS, "self")));
        content.put(MEDS_DAILY_RATION, setCheckbox(getValue(ResInfoTypeTools.TYPE_MEDS, "dailyration")));
        content.put(MEDS_CONTROL, setCheckbox(getValue(ResInfoTypeTools.TYPE_MEDS, "control")));
        content.put(MEDS_MARCUMARPASS, setCheckbox(getValue(ResInfoTypeTools.TYPE_MEDS, "marcumarpass")));

        BHP lastMed = BHPTools.getLastBHP(resident, InterventionTools.FLAG_MEDS_APPLICATION);
        if (lastMed != null) {
            content.put(MEDS_LAST_APPLICATION, DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(lastMed.getIst()));
        } else {
            content.put(MEDS_LAST_APPLICATION, "--");
        }


        ArrayList<Prescription> listGlucose = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_GLUCOSE_MONITORING);
        if (!listGlucose.isEmpty()) {
            int daily = 0, weekly = 0;
            BigDecimal morning = BigDecimal.ZERO, noon = BigDecimal.ZERO, evening = BigDecimal.ZERO;
            for (Prescription p : listGlucose) {
                for (PrescriptionSchedule ps : p.getPrescriptionSchedule()) {
                    if (ps.isTaeglich()) {
                        daily++;
                        morning = morning.add(ps.getMorningSum());
                        noon = noon.add(ps.getNoonSum());
                        evening = evening.add(ps.getEveningSum());
                    }
                    if (ps.isWoechentlich()) {
                        weekly++;
                        morning = morning.add(ps.getMorningSum());
                        noon = noon.add(ps.getNoonSum());
                        evening = evening.add(ps.getEveningSum());
                    }
                }
            }
            content.put(MEDS_MORNING_GLUCOSE, setCheckbox(morning.compareTo(BigDecimal.ZERO) > 0));
            content.put(MEDS_NOON_GLUCOSE, setCheckbox(noon.compareTo(BigDecimal.ZERO) > 0));
            content.put(MEDS_EVENING_GLUCOSE, setCheckbox(evening.compareTo(BigDecimal.ZERO) > 0));
            content.put(MEDS_DAILY_GLUCOSECHECK, (daily > 0 ? Integer.toString(daily) : "--"));
            content.put(MEDS_WEEKLY_GLUCOSECHECK, (weekly > 0 ? Integer.toString(weekly) : "--"));
        }

    }

    private void createContent4Section17() {
        // nothing yet
    }

    private void createContent4Section18() {
        content.put(COMMENTS_GENERAL, getValue(ResInfoTypeTools.TYPE_WARNING, "beschreibung"));
    }

    private void createContent4Section19() {
        ArrayList<String> bodyParts = new ArrayList<String>(Arrays.asList(PnlBodyScheme.PARTS));
        String[] pdfbody = new String[]{BODY1_DESCRIPTION, BODY2_DESCRIPTION, BODY3_DESCRIPTION, BODY4_DESCRIPTION, BODY5_DESCRIPTION, BODY6_DESCRIPTION};
        int lineno = -1;
        AcroFields form = stamper.getAcroFields();
        PdfContentByte directcontent = stamper.getOverContent(2);

        for (int type : ArrayUtils.add(ResInfoTypeTools.TYPE_ALL_WOUNDS, ResInfoTypeTools.TYPE_MYCOSIS)) {
            if (mapID2Info.containsKey(type)) {

                String descriptionKey = (type == ResInfoTypeTools.TYPE_MYCOSIS ? "misc.msg.mycosis" : "misc.msg.wound.documentation");

                ResInfo currentWound = mapID2Info.get(type);
                lineno++;

                content.put(pdfbody[lineno], OPDE.lang.getString(descriptionKey) + " " + DateFormat.getDateInstance().format(currentWound.getFrom()) + ": " + ResInfoTools.getContentAsPlainText(currentWound));

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
                        int listpos = Arrays.asList(PnlBodyScheme.PARTS).indexOf(bodykey);
                        OPDE.debug(listpos);
                        // does this property denote a body part AND is it clicked ?
                        if (bodyParts.contains(bodykey) && mapInfo2Properties.get(currentWound).getProperty(key).equalsIgnoreCase("true")) {
                            // set the pointer to the middle right part of the frame
                            directcontent.moveTo(pos1.position.getRight(), pos1.position.getTop() - (pos1.position.getHeight() / 2f));
                            // find the position of the checkbox representing the bodypart.
                            AcroFields.FieldPosition pos2 = form.getFieldPositions(PDFPARTS[listpos]).get(0);
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
                        int listpos = Arrays.asList(PnlBodyScheme.PARTS).indexOf(bodykey);

                        // does this property denote a body part AND is it clicked ?
                        if (bodyParts.contains(bodykey) && mapInfo2Properties.get(currentWound).getProperty(key).equalsIgnoreCase("true")) {
                            AcroFields.FieldPosition pos2 = form.getFieldPositions(PDFPARTS[listpos]).get(0);
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

        String sICD = "";

        for (ResInfo icd : listICD) {
            EntityManager em = OPDE.createEM();

            Doc gp = null;
            try {
                long gpid = Long.parseLong(mapInfo2Properties.get(icd).getProperty("arztid"));
                gp = em.find(Doc.class, gpid);
            } catch (NumberFormatException e) {
                // bah!
            }

            Hospital hp = null;
            try {
                long hpid = Long.parseLong(mapInfo2Properties.get(icd).getProperty("khid"));
                hp = em.find(Hospital.class, hpid);
            } catch (NumberFormatException e) {
                // bah!
            }

            em.close();

            mapInfo2Properties.get(icd).getProperty("");
            sICD += mapInfo2Properties.get(icd).getProperty("icd");
            sICD += ": " + mapInfo2Properties.get(icd).getProperty("text");
            sICD += mapInfo2Properties.get(icd).getProperty("koerperseite").equalsIgnoreCase("nicht festgelegt") ? "" : " " + mapInfo2Properties.get(icd).getProperty("koerperseite") + ", ";
            sICD += mapInfo2Properties.get(icd).getProperty("diagnosesicherheit").equalsIgnoreCase("nicht festgelegt") ? "" : " " + mapInfo2Properties.get(icd).getProperty("diagnosesicherheit");

            sICD += "; ";
        }

        if (!sICD.isEmpty()) {
            content.put(DIAG_ICD10, sICD.substring(0, sICD.length() - 2));
        }
    }

    /**
     * creates the medications list. Max length of this list 15 entries. Which is medical overkill anyways.
     */
    private void createContent4Meds() {
        ArrayList<Pair<String, String>> listFieldsOnPage3 = new ArrayList<Pair<String, String>>();
        listFieldsOnPage3.add(new Pair(MEDS1, DOSAGE1));
        listFieldsOnPage3.add(new Pair(MEDS2, DOSAGE2));
        listFieldsOnPage3.add(new Pair(MEDS3, DOSAGE3));
        listFieldsOnPage3.add(new Pair(MEDS4, DOSAGE4));
        listFieldsOnPage3.add(new Pair(MEDS5, DOSAGE5));
        listFieldsOnPage3.add(new Pair(MEDS6, DOSAGE6));
        listFieldsOnPage3.add(new Pair(MEDS7, DOSAGE7));
        listFieldsOnPage3.add(new Pair(MEDS8, DOSAGE8));
        listFieldsOnPage3.add(new Pair(MEDS9, DOSAGE9));
        listFieldsOnPage3.add(new Pair(MEDS10, DOSAGE10));
        listFieldsOnPage3.add(new Pair(MEDS11, DOSAGE11));
        listFieldsOnPage3.add(new Pair(MEDS12, DOSAGE12));
        listFieldsOnPage3.add(new Pair(MEDS13, DOSAGE13));
        listFieldsOnPage3.add(new Pair(MEDS14, DOSAGE14));
        listFieldsOnPage3.add(new Pair(MEDS15, DOSAGE15));

        ArrayList<Prescription> listRegularMeds = PrescriptionTools.getAllActiveRegularMedsOnly(resident);

        int line = 0;

//        for (int i = 0; i < 15; i++){
//        }

        for (Prescription pres : listRegularMeds) {
            content.put(listFieldsOnPage3.get(line).getFirst(), PrescriptionTools.getShortDescriptionAsCompactText(pres));
            content.put(listFieldsOnPage3.get(line).getSecond(), PrescriptionTools.getDoseAsCompactText(pres));
            line++;
        }


        boolean needMoreRoom = line >= 15;

        content.put(DOCS_ADDITIONALMEDSLIST, setCheckbox(needMoreRoom));

        listFieldsOnPage3.clear();

    }

    private void getAdditionMeds() {

        try {
            Document document = new Document(PageSize.A4, Utilities.millimetersToPoints(10), Utilities.millimetersToPoints(10), Utilities.millimetersToPoints(20), Utilities.millimetersToPoints(20));
            ByteArrayOutputStream streamDoc = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, streamDoc);
            document.open();

            Paragraph h1 = new Paragraph(new Phrase("HEADER!!", PDF.plain(PDF.sizeH1())));
            h1.setAlignment(Element.ALIGN_CENTER);
            document.add(h1);

            Paragraph p = new Paragraph(SYSTools.xx("nursingrecords.prescription.dailyplan.warning"));
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);
            document.add(Chunk.NEWLINE);

            document.close();


            try {
                PdfReader docReader = new PdfReader(streamDoc.toByteArray());
                stamper.insertPage(4, PageSize.A4);
                // Add the stationary to the new page
                stamper.getUnderContent(4).addTemplate(, 0, 0);
                // Add as much content of the column as possible
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        } catch (DocumentException d) {

        }

    }

    private String getValue(int type, String propsKey) {
        if (!mapID2Info.containsKey(type)) {
            return "--";
        }
        return SYSTools.catchNull(mapInfo2Properties.get(mapID2Info.get(type)).getProperty(propsKey), "--");
    }

    private String setCheckbox(boolean in) {
        return in ? "1" : "0";
    }

    private String setBD(BigDecimal bd, String suffix) {

        return (bd != null && bd.compareTo(BigDecimal.ZERO) > 0) ? bd.setScale(2, RoundingMode.HALF_UP).toString() + " " + suffix : "--";
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
        return Integer.toString(Arrays.asList(list).indexOf(sIn));
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


    //       _                  _____                      _                    _          _
    //      / \   ___ _ __ ___ |  ___|__  _ __ _ __ ___   | | _____ _   _ ___  | |__   ___| | _____      __
    //     / _ \ / __| '__/ _ \| |_ / _ \| '__| '_ ` _ \  | |/ / _ \ | | / __| | '_ \ / _ \ |/ _ \ \ /\ / /
    //    / ___ \ (__| | | (_) |  _| (_) | |  | | | | | | |   <  __/ |_| \__ \ | |_) |  __/ | (_) \ V  V /
    //   /_/   \_\___|_|  \___/|_|  \___/|_|  |_| |_| |_| |_|\_\___|\__, |___/ |_.__/ \___|_|\___/ \_/\_/
    //                                                              |___/
    public static final String ASSIGNED_INSURANCE_GRADE = "Formular1[0].#subform[0].#area[2].#area[3].bewilligtePflegestufe[0]";
    public static final String BACK_LOW = "Formular1[0].#subform[1].Kontrollkästchen[15]";
    public static final String BACK_MID = "Formular1[0].#subform[1].Kontrollkästchen[14]";
    public static final String BACK_OF_THE_HEAD = "Formular1[0].#subform[1].GrafikKopf_hinten[0]";
    public static final String BACK_UPPER_LEFT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[3]";
    public static final String BEDSORE = "Formular1[0].#subform[0].Gruppe-Dekubitus[0]";
    public static final String BODY1_DESCRIPTION = "Formular1[0].#subform[1].TextField1[1]";
    public static final String BODY2_DESCRIPTION = "Formular1[0].#subform[1].TextField1[0]";
    public static final String BODY3_DESCRIPTION = "Formular1[0].#subform[1].TextField1[5]";
    public static final String BODY4_DESCRIPTION = "Formular1[0].#subform[1].TextField1[4]";
    public static final String BODY5_DESCRIPTION = "Formular1[0].#subform[1].TextField1[3]";
    public static final String BODY6_DESCRIPTION = "Formular1[0].#subform[1].TextField1[2]";
    public static final String BOTTOM_BACK = "Formular1[0].#subform[1].Kontrollkästchen[11]";
    public static final String BOTTOM_LEFT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[26]";
    public static final String BOTTOM_RIGHT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[5]";
    public static final String CALF_LEFTBACK = "Formular1[0].#subform[1].Kontrollkästchen[34]";
    public static final String CALF_LEFTSIDE = "Formular1[0].#subform[1].Kontrollkästchen[28]";
    public static final String CALF_RIGHT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[7]";
    public static final String CALF_RIGHTBACK = "Formular1[0].#subform[1].Kontrollkästchen[32]";
    public static final String COMBING_CARE_BASIN = "Formular1[0].#subform[0].Waschbecken[0]";
    public static final String COMBING_CARE_BED = "Formular1[0].#subform[0].Bett[3]";
    public static final String COMBING_CARE_LEVEL = "Formular1[0].#subform[0].Kämmen[3]";
    public static final String COMBING_CARE_SHOWER = "Formular1[0].#subform[0].Bad_Dusche[3]";
    public static final String COMMENTS_GENERAL = "Formular1[0].#subform[1].BisherigeVersorgungFreitext[0]";
    public static final String COMMS_HEARING_ABILITY = "Formular1[0].#subform[1].Gehör[0]";
    public static final String COMMS_MOTHERTONGUE = "Formular1[0].#subform[0].Muttersprache[0]";
    public static final String COMMS_SEEING_ABILITY = "Formular1[0].#subform[1].Sehen[0]";
    public static final String COMMS_SPEECH_ABILITY = "Formular1[0].#subform[1].Sprache[0]";
    public static final String COMMS_UNDERSTANDING_ABILITY = "Formular1[0].#subform[1].Sprachverständnis[0]";
    public static final String COMMS_WRITING_ABILITY = "Formular1[0].#subform[1].Schrift[0]";
    public static final String CONFIDANT_CITY = "Formular1[0].#subform[0].#area[2].OrtZugehörige[1]";
    public static final String CONFIDANT_FIRSTNAME = "Formular1[0].#subform[0].#area[2].VornameZugehörige[1]";
    public static final String CONFIDANT_NAME = "Formular1[0].#subform[0].#area[2].NameZugehörige[1]";
    public static final String CONFIDANT_PHONE = "Formular1[0].#subform[0].#area[2].TelefonZugehörige[1]";
    public static final String CONFIDANT_STREET = "Formular1[0].#subform[0].#area[2].StraßeZugehörige[1]";
    public static final String CONFIDANT_ZIP = "Formular1[0].#subform[0].#area[2].PLZZugehörige[1]";
    public static final String CONSCIOUSNESS_AWAKE = "Formular1[0].#subform[1].BewußtseinslageWach[0]";
    public static final String CONSCIOUSNESS_COMA = "Formular1[0].#subform[1].BewußtseinslageKomatös[0]";
    public static final String CONSCIOUSNESS_SOMNOLENT = "Formular1[0].#subform[1].BewußtseinslageBlanko[0]";
    public static final String CONSCIOUSNESS_SOPOR = "Formular1[0].#subform[1].BewußtseinslageSomnolent[0]";
    public static final String CROOK_ARM_LEFT = "Formular1[0].#subform[1].Kontrollkästchen[37]";
    public static final String CROOK_ARM_RIGHT = "Formular1[0].#subform[1].Kontrollkästchen[38]";
    public static final String DATE_REQUESTED_INSURANCE_GRADE_PAGE3 = "Formular1[0].Patientenüberleitung-Seite3[0].Kontrollkästchen2[0]";
    public static final String DATE_REQUESTED_INSURANCE_GRADE = "Formular1[0].#subform[0].#area[2].DatumsUhrzeitfeld4[0]";
    public static final String DENTURE_CARE_BASIN = "Formular1[0].#subform[0].Waschbecken[1]";
    public static final String DENTURE_CARE_BED = "Formular1[0].#subform[0].Bett[1]";
    public static final String DENTURE_CARE_LEVEL = "Formular1[0].#subform[0].Zahnprothese[0]";
    public static final String DENTURE_CARE_SHOWER = "Formular1[0].#subform[0].Bad_Dusche[1]";
    public static final String DIAG_ICD10 = "Formular1[0].Patientenüberleitung-Seite3[0].AufnahmegrundDiagnose[0]";
    public static final String DOCS_ADDITIONALMEDSLIST = "Formular1[0].Patientenüberleitung-Seite3[0].Medi-Plan[0]";
    public static final String DOCS_GP_REPORT = "Formular1[0].#subform[1].Arztbrief[0]";
    public static final String DOCS_IMAGES = "Formular1[0].#subform[1].Bilder[0]";
    public static final String DOCS_LAB = "Formular1[0].#subform[1].Labor[0]";
    public static final String DOCS_MEDS_LIST = "Formular1[0].#subform[1].Medi-Plan[0]";
    public static final String DOCS_MISC = "Formular1[0].#subform[1].Sonstiges[0]";
    public static final String DOCS_PREVIOUS = "Formular1[0].#subform[1].Vorberichte[0]";
    public static final String DOCS_WITHPATIENT = "Formular1[0].#subform[1].Kontrollkästchen1[0]";
    public static final String DRESSING_CARE_BASIN = "Formular1[0].#subform[0].Waschbecken[4]";
    public static final String DRESSING_CARE_BED = "Formular1[0].#subform[0].Bett[4]";
    public static final String DRESSING_CARE_LEVEL = "Formular1[0].#subform[0].Auskleiden[0]";
    public static final String DRESSING_CARE_SHOWER = "Formular1[0].#subform[0].Bad_Dusche[4]";
    public static final String ELLBOW_LEFT = "Formular1[0].#subform[1].Kontrollkästchen[19]";
    public static final String ELLBOW_RIGHT = "Formular1[0].#subform[1].Kontrollkästchen[20]";
    public static final String ELLBOW_RIGHTSIDE = "Formular1[0].#subform[1].Kontrollkästchen[10]";
    public static final String ELLBOW_SIDE_LEFT = "Formular1[0].#subform[1].Kontrollkästchen[31]";
    public static final String EXCRETIONS_AID_BEDPAN = "Formular1[0].#subform[0].Steckbecken[0]";
    public static final String EXCRETIONS_AID_COMMODE = "Formular1[0].#subform[0].#area[5].Kontrollkästchen4[0]";
    public static final String EXCRETIONS_AID_NO = "Formular1[0].#subform[0].Hilfsmittel_nein[0]";
    public static final String EXCRETIONS_AID_URINAL = "Formular1[0].#subform[0].Urinflasche[0]";
    public static final String EXCRETIONS_AP_AID = "Formular1[0].#subform[0].Anuspraeter[0]";
    public static final String EXCRETIONS_COMMENT = "Formular1[0].#subform[0].Besonderheiten[1]";
    public static final String EXCRETIONS_CONTROL_WEIGHT = "Formular1[0].#subform[0].#area[5].Gewichtskontrolle[0]";
    public static final String EXCRETIONS_CURRENT_USED_AID = "Formular1[0].#subform[0].bisherversorgtmit[0]";
    public static final String EXCRETIONS_DIARRHOEA_TENDENCY = "Formular1[0].#subform[0].Stuhlgang_Durchfall[0]";
    public static final String EXCRETIONS_DIGITAL = "Formular1[0].#subform[0].Stuhlgang_Ausräumung[0]";
    public static final String EXCRETIONS_INCO_FAECAL = "Formular1[0].#subform[0].Stuhlinkontinenz[0]";
    public static final String EXCRETIONS_INCO_URINE = "Formular1[0].#subform[0].Harninkontinenz[0]";
    public static final String EXCRETIONS_INCOAID_NEEDSHELP = "Formular1[0].#subform[0].Versorgung_mitHilfe[0]";
    public static final String EXCRETIONS_INCOAID_SELF = "Formular1[0].#subform[0].Versorgung_selbständig[0]";
    public static final String EXCRETIONS_LASTCHANGE = "Formular1[0].#subform[0].DatumsUhrzeitfeld2[0]";
    public static final String EXCRETIONS_LIQUID_BALANCE = "Formular1[0].#subform[0].#area[5].Flüssigkeit[0]";
    public static final String EXCRETIONS_NORMAL = "Formular1[0].#subform[0].Stuhlgang_normal[0]";
    public static final String EXCRETIONS_OBSTIPATION_TENDENCY = "Formular1[0].#subform[0].Stuhlgang_Verstopfung[0]";
    public static final String EXCRETIONS_ONEWAY_AID = "Formular1[0].#subform[0].Einmalinkontinenzartikel[0]";
    public static final String EXCRETIONS_SUP_AID = "Formular1[0].#subform[0].suprapub\\.Harnblasenkatheter[0]";
    public static final String EXCRETIONS_TRANS_AID = "Formular1[0].#subform[0].transur\\.Blasenkatheter[0]";
    public static final String EXCRETIONS_TUBESIZE_CH = "Formular1[0].#subform[0].CH[0]";
    public static final String FACE = "Formular1[0].#subform[1].Kontrollkästchen[40]";
    public static final String FOOD_ABROSIA = "Formular1[0].#subform[1].Nahrungskarenz[0]";
    public static final String FOOD_ARTIFICIAL_FEEDING = "Formular1[0].#subform[1].SonderkostJaNein[0]";
    public static final String FOOD_ASSISTANCE_LEVEL = "Formular1[0].#subform[1].HilfebeiNahrung[0]";
    public static final String FOOD_BITESIZE = "Formular1[0].#subform[1].ErnährungMundgrechteZubereitung[0]";
    public static final String FOOD_BMI = "Formular1[0].#subform[1].Bodymaßindex[0]";
    public static final String FOOD_BREADUNTIS = "Formular1[0].#subform[1].täglicheBE[0]";
    public static final String FOOD_DAILY_KCAL = "Formular1[0].#subform[1].#area[12].Kalorienzufuhr_kcal[0]";
    public static final String FOOD_DAILY_ML = "Formular1[0].#subform[1].#area[12].Sondenkost_ml[0]";
    public static final String FOOD_DRINKINGMOTIVATION = "Formular1[0].#subform[1].AnhaltenzumTrinken[0]";
    public static final String FOOD_DRINKSALONE = "Formular1[0].#subform[1].Trinkverhalten_selbständig[0]";
    public static final String FOOD_DYSPHAGIA = "Formular1[0].#subform[1].Ernährung_Schluckstörung[0]";
    public static final String FOOD_GRAVITY = "Formular1[0].#subform[1].Schwerkraft[0]";
    public static final String FOOD_LAST_MEAL = "Formular1[0].#subform[1].ErnährungLetzteMahlzeit[0]";
    public static final String FOOD_LIQUIDS_DAILY_ML = "Formular1[0].#subform[1].TäglicheTrinkmenge[0]";
    public static final String FOOD_ORALNUTRITION = "Formular1[0].#subform[1].OraleErnährung[0]";
    public static final String FOOD_PARENTERAL = "Formular1[0].#subform[1].ParenteraleErnährung[0]";
    public static final String FOOD_PUMP = "Formular1[0].#subform[1].Ernährungspumpe[0]";
    public static final String FOOD_SYRINGE = "Formular1[0].#subform[1].Spritze[0]";
    public static final String FOOD_TEE_DAILY_ML = "Formular1[0].#subform[1].#area[12].Tee_ml[0]";
    public static final String FOOD_TUBESINCE = "Formular1[0].#subform[1].Sondegelegtam[0]";
    public static final String FOOD_TUBETYPE = "Formular1[0].#subform[1].Sondentyp[0]";
    public static final String FOOT_LEFT_FRONT = "Formular1[0].#subform[1].Kontrollkästchen[52]";
    public static final String FOOT_LEFTBACK = "Formular1[0].#subform[1].Kontrollkästchen[35]";
    public static final String FOOT_RIGHT_FRONT = "Formular1[0].#subform[1].Kontrollkästchen[51]";
    public static final String FOOT_RIGHTBACK = "Formular1[0].#subform[1].Kontrollkästchen[36]";
    public static final String GROIN = "Formular1[0].#subform[1].Kontrollkästchen[39]";
    public static final String HAND_LEFT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[30]";
    public static final String HAND_RIGHT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[9]";
    public static final String HEAD_LEFT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[22]";
    public static final String HEAD_RIGHT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[1]";
    public static final String HEEL_LEFT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[29]";
    public static final String HEEL_RIGHT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[8]";
    public static final String HIP_LEFT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[25]";
    public static final String HIP_RIGHT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[4]";
    public static final String INSURANCE_GRADE_NO = "Formular1[0].#subform[0].#area[2].#area[3].Einstufung_nein[0]";
    public static final String INSURANCE_GRADE_REQUESTED = "Formular1[0].#subform[0].#area[2].#area[3].Einstufung_beantragt[0]";
    public static final String INSURANCE_GRADE_YES = "Formular1[0].#subform[0].#area[2].#area[3].Einstufung_ja[0]";
    public static final String KNEE_HOLLOWLEFT = "Formular1[0].#subform[1].Kontrollkästchen[33]";
    public static final String KNEE_HOLLOWRIGHT = "Formular1[0].#subform[1].Kontrollkästchen[18]";
    public static final String KNEE_LEFT = "Formular1[0].#subform[1].Kontrollkästchen[47]";
    public static final String KNEE_RIGHT = "Formular1[0].#subform[1].Kontrollkästchen[49]";
    public static final String LC_CITY = "Formular1[0].#subform[0].#area[2].OrtZugehörige[0]";
    public static final String LC_CUSTODY = "Formular1[0].#subform[0].#area[2].Aufenthaltsbestimmung[0]";
    public static final String LC_FINANCE = "Formular1[0].#subform[0].#area[2].Vermögensverwaltung[0]";
    public static final String LC_FIRSTNAME = "Formular1[0].#subform[0].#area[2].VornameZugehörige[0]";
    public static final String LC_GENERAL = "Formular1[0].#subform[0].#area[2].gesetzl\\.Betreuer[0]";
    public static final String LC_HEALTH = "Formular1[0].#subform[0].#area[2].Gesundheitsfürsorge[0]";
    public static final String LC_NAME = "Formular1[0].#subform[0].#area[2].NameZugehörige[0]";
    public static final String LC_PHONE = "Formular1[0].#subform[0].#area[2].TelefonZugehörige[0]";
    public static final String LC_STREET = "Formular1[0].#subform[0].#area[2].StraßeZugehörige[0]";
    public static final String LC_ZIP = "Formular1[0].#subform[0].#area[2].PLZZugehörige[0]";
    public static final String LEGAL_MINOR = "Formular1[0].#subform[0].#area[2].minderjährig[0]";
    public static final String LEGAL_SINGLE = "Formular1[0].#subform[0].#area[2].alleinstehend[0]";
    public static final String LOWER_BELLY = "Formular1[0].#subform[1].Kontrollkästchen[44]";
    public static final String LOWER_LEG_LEFT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[27]";
    public static final String LOWER_LEG_RIGHT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[6]";
    public static final String MEDS_CONTROL = "Formular1[0].#subform[1].ÜberwachungEinnahme[0]";
    public static final String MEDS_DAILY_GLUCOSECHECK = "Formular1[0].#subform[1].BZKontrolleXtäglich[0]";
    public static final String MEDS_DAILY_RATION = "Formular1[0].#subform[1].BereitstellenTagesration[0]";
    public static final String MEDS_EVENING_GLUCOSE = "Formular1[0].#subform[1].BZKontrolleAbends[0]";
    public static final String MEDS_INJECTION_LEVEL = "Formular1[0].#subform[1].Injektion[0]";
    public static final String MEDS_INSULIN_APPLICATION = "Formular1[0].#subform[1].InsulinVerabreichung[0]";
    public static final String MEDS_LAST_APPLICATION = "Formular1[0].#subform[1].letzteMedikation[0]";
    public static final String MEDS_MARCUMARPASS = "Formular1[0].#subform[1].Wundschmerz[1]";
    public static final String MEDS_MORNING_GLUCOSE = "Formular1[0].#subform[1].BZKontrolleMorgens[0]";
    public static final String MEDS_NOON_GLUCOSE = "Formular1[0].#subform[1].BZKontrolleMittags[0]";
    public static final String MEDS_SELF = "Formular1[0].#subform[1].Einnahme_selbständig[0]";
    public static final String MEDS_WEEKLY_GLUCOSECHECK = "Formular1[0].#subform[1].BZKontrollexwöchentlich[0]";
    public static final String MOBILITY_AID_BED = "Formular1[0].#subform[0].#area[7].Hilfsmittel[5]";
    public static final String MOBILITY_AID_CANE = "Formular1[0].#subform[0].#area[7].HilfsmittelGehstock[0]";
    public static final String MOBILITY_AID_COMMENT = "Formular1[0].#subform[0].#area[7].Textfeld22[0]";
    public static final String MOBILITY_AID_COMMODE = "Formular1[0].#subform[0].#area[7].HilfsmittelToilettenstuhl[0]";
    public static final String MOBILITY_AID_CRUTCH = "Formular1[0].#subform[0].#area[7].HilfsmittelUnterarmgehstütze[0]";
    public static final String MOBILITY_AID_SITTING = "Formular1[0].#subform[0].Hilfsmittel[4]";
    public static final String MOBILITY_AID_GETUP = "Formular1[0].#subform[0].Hilfsmittel[0]";
    public static final String MOBILITY_AID_TOILET = "Formular1[0].#subform[0].Hilfsmittel[3]";
    public static final String MOBILITY_AID_TRANSFER = "Formular1[0].#subform[0].Hilfsmittel[2]";
    public static final String MOBILITY_AID_WALKER = "Formular1[0].#subform[0].#area[7].HilfsmittelRollator[0]";
    public static final String MOBILITY_AID_WALKING = "Formular1[0].#subform[0].Hilfsmittel[1]";
    public static final String MOBILITY_AID_WHEELCHAIR = "Formular1[0].#subform[0].#area[7].HilfsmittelRollstuhl[0]";
    public static final String MOBILITY_BED = "Formular1[0].#subform[0].#area[7].BeweglichkeitimBett[0]";
    public static final String MOBILITY_BEDPOSITION = "Formular1[0].#subform[0].#area[7].Lagerungsart[0]";
    public static final String MOBILITY_BEDRIDDEN = "Formular1[0].#subform[0].#area[7].Bettlägerig[0]";
    public static final String MOBILITY_COMMENT = "Formular1[0].#subform[0].#area[7].MobilitätBemerkung[0]";
    public static final String MOBILITY_GET_UP = "Formular1[0].#subform[0].Aufstehen[0]";
    public static final String MOBILITY_SITTING = "Formular1[0].#subform[0].SitzenimStuhl[0]";
    public static final String MOBILITY_TOILET = "Formular1[0].#subform[0].Toilettengang[0]";
    public static final String MOBILITY_TRANSFER = "Formular1[0].#subform[0].Transfer[0]";
    public static final String MOBILITY_WALKING = "Formular1[0].#subform[0].Gehen[0]";
    public static final String MONITORING_BP = "Formular1[0].#subform[1].ÜberwachungBlutdruck[0]";
    public static final String MONITORING_EXCRETION = "Formular1[0].#subform[1].Überwachung_Ausfuhr[0]";
    public static final String MONITORING_INTAKE = "Formular1[0].#subform[1].ÜberwachungTemperatur[0]";
    public static final String MONITORING_PAIN = "Formular1[0].#subform[1].ÜberwachungSchnerz[0]";
    public static final String MONITORING_PORT = "Formular1[0].#subform[1].ÜberwachungPuls[0]";
    public static final String MONITORING_PULSE = "Formular1[0].#subform[1].ÜberwachungPsyche[0]";
    public static final String MONITORING_RESPIRATION = "Formular1[0].#subform[1].ÜberwachungAtmung[0]";
    public static final String MONITORING_TEMP = "Formular1[0].#subform[1].ÜberwachungSturz[0]";
    public static final String MONITORING_WEIGHT = "Formular1[0].#subform[1].Überwachung_Gewicht[0]";
    public static final String MOUTH_CARE_BASIN = "Formular1[0].#subform[0].Waschbecken[0]";
    public static final String MOUTH_CARE_BED = "Formular1[0].#subform[0].Bett[0]";
    public static final String MOUTH_CARE_LEVEL = "Formular1[0].#subform[0].Mundpflege[0]";
    public static final String MOUTH_CARE_SHOWER = "Formular1[0].#subform[0].Bad_Dusche[0]";
    public static final String ORIENTATION_LOCATION_ABILITY = "Formular1[0].#subform[1].örtlich[0]";
    public static final String ORIENTATION_PERSONAL_ABILITY = "Formular1[0].#subform[1].Persönlich[0]";
    public static final String ORIENTATION_RUNNAWAY_TENDENCY = "Formular1[0].#subform[1].Weglauftendenz[0]";
    public static final String ORIENTATION_SITUATION_ABILITY = "Formular1[0].#subform[1].Situativ[0]";
    public static final String ORIENTATION_TIME_ABILITY = "Formular1[0].#subform[1].Zeitlich[0]";
    public static final String PAGE2_DATE = "Formular1[0].#subform[1].Datum[0]";
    public static final String PAGE2_PHONE = "Formular1[0].#subform[1].Telefonnummer[0]";
    public static final String PAGE2_RESIDENT_DOB = "Formular1[0].#subform[1].Pat-Geburtsdatum[1]";
    public static final String PAGE2_RESIDENT_FIRSTNAME = "Formular1[0].#subform[1].Pat-Vorname[1]";
    public static final String PAGE2_RESIDENT_NAME = "Formular1[0].#subform[1].Pat-Name[1]";
    public static final String PAGE2_USERNAME = "Formular1[0].#subform[1].Name_Unterschrift[0]";
    public static final String PAGE3_RESIDENT_GP = "Formular1[0].Patientenüberleitung-Seite3[0].AdressederweiterbehandelndenPraxis[0]";
    public static final String PAGE3_DOCS_WITHPATIENT = "Formular1[0].Patientenüberleitung-Seite3[0].Kontrollkästchen1[0]";
    public static final String PAGE3_RESIDENT_DOB = "Formular1[0].Patientenüberleitung-Seite3[0].Pat-Geburtsdatum[0]";
    public static final String PAGE3_RESIDENT_FULLNAME = "Formular1[0].Patientenüberleitung-Seite3[0].Pat-Name[0]";
    public static final String PAGE3_RESIDENT_HINSURANCE = "Formular1[0].Patientenüberleitung-Seite3[0].Krankenkasse[0]";
    public static final String PAGE3_TX_DATE = "Formular1[0].Patientenüberleitung-Seite3[0].Datum[0]";
    public static final String PAGE3_TX2_DATE = "Formular1[0].Patientenüberleitung-Seite3[0].Textfeld27[3]";
    public static final String PERSONAL_CARE_BASIN = "Formular1[0].#subform[0].PersonalCareBasin[0]";
    public static final String PERSONAL_CARE_BED = "Formular1[0].#subform[0].PersonalCareBed[0]";
    public static final String PERSONAL_CARE_COMMENT = "Formular1[0].#subform[0].SonstigesGrundpflege[0]";
    public static final String PERSONAL_CARE_LEVEL = "Formular1[0].#subform[0].Körperpflege[0]";
    public static final String PERSONAL_CARE_SHOWER = "Formular1[0].#subform[0].PersonalCareShower[0]";
    public static final String PREFERRED_CAREPRODUCTS = "Formular1[0].#subform[0].Pflegemittel[0]";
    public static final String PROPH_BEDSORE = "Formular1[0].#subform[0].#area[6].Dekubitus[0]";
    public static final String PROPH_CONTRACTURE = "Formular1[0].#subform[0].#area[6].Kontraktur[0]";
    public static final String PROPH_FALL = "Formular1[0].#subform[0].#area[6].Sturz[0]";
    public static final String PROPH_INTERTRIGO = "Formular1[0].#subform[0].Intertrigo[0]";
    public static final String PROPH_OBSTIPATION = "Formular1[0].#subform[0].#area[6].Obstipation[0]";
    public static final String PROPH_PNEUMONIA = "Formular1[0].#subform[0].Pneunomie[0]";
    public static final String PROPH_SOOR = "Formular1[0].#subform[0].#area[6].Soor_Protitis[0]";
    public static final String PROPH_THROMBOSIS = "Formular1[0].#subform[0].Thrombose[0]";
    public static final String RESIDENT_CARDSTATE_HINSURANCE = "Formular1[0].Patientenüberleitung-Seite3[0].Status[0]";
    public static final String RESIDENT_CITY = "Formular1[0].#subform[0].#area[0].#area[1].Pat-Ort[0]";
    public static final String RESIDENT_DOB = "Formular1[0].#subform[0].#area[0].#area[1].Pat-Geburtsdatum[0]";
    public static final String RESIDENT_FIRSTNAME = "Formular1[0].#subform[0].#area[0].#area[1].Pat-Vorname[0]";
    public static final String RESIDENT_GENDER = "Formular1[0].#subform[0].#area[0].#area[1].Geschlechtsoptionsfelder[0]";
    public static final String RESIDENT_HINSURANCE = "Formular1[0].#subform[0].#area[0].#area[1].Krankenkasse[0]";
    public static final String RESIDENT_HINSURANCEID = "Formular1[0].Patientenüberleitung-Seite3[0].Versicherten-Nr\\.[0]";
    public static final String RESIDENT_HINSURANCENO = "Formular1[0].Patientenüberleitung-Seite3[0].Kassen-Nr\\.[0]";
    public static final String RESIDENT_NAME = "Formular1[0].#subform[0].#area[0].#area[1].Pat-Name[0]";
    public static final String RESIDENT_PHONE = "Formular1[0].#subform[0].#area[0].#area[1].Pat-Telefonnr[0]";
    public static final String RESIDENT_STREET = "Formular1[0].#subform[0].#area[0].#area[1].Pat-Straße[0]";
    public static final String RESIDENT_ZIP = "Formular1[0].#subform[0].#area[0].#area[1].Pat-PLZ[0]";
    public static final String RESPIRATION_ASPIRATE = "Formular1[0].#subform[1].Absaugen[0]";
    public static final String RESPIRATION_ASTHMA = "Formular1[0].#subform[1].Asthma[0]";
    public static final String RESPIRATION_CARDCONGEST = "Formular1[0].#subform[1].kardialerStau[0]";
    public static final String RESPIRATION_COMMENT = "Formular1[0].#subform[1].Atmung_Eingabe[0]";
    public static final String RESPIRATION_COUGH = "Formular1[0].#subform[1].Husten[0]";
    public static final String RESPIRATION_MUCOUS = "Formular1[0].#subform[1].Verschleimung[0]";
    public static final String RESPIRATION_NORMAL = "Formular1[0].#subform[1].unauffällig[0]";
    public static final String RESPIRATION_OTHER = "Formular1[0].#subform[1].Atmung_Sonstiges[0]";
    public static final String RESPIRATION_PAIN = "Formular1[0].#subform[1].Schmerzen[0]";
    public static final String RESPIRATION_SMOKING = "Formular1[0].#subform[1].Rauchen[0]";
    public static final String RESPIRATION_SILICONTUBE = "Formular1[0].#subform[1].Silikonkanüle[0]";
    public static final String RESPIRATION_SILVERTUBE = "Formular1[0].#subform[1].Silberkanüle[0]";
    public static final String RESPIRATION_SPUTUM = "Formular1[0].#subform[1].Auswurf[0]";
    public static final String RESPIRATION_STOMA = "Formular1[0].#subform[1].Tracheostoma[0]";
    public static final String RESPIRATION_TUBESIZE = "Formular1[0].#subform[1].Kanülengröße[0]";
    public static final String RESPIRATION_TUBETYPE = "Formular1[0].#subform[1].Kanülenart[0]";
    public static final String RISKSCALE_TYPE_BEDSORE = "Formular1[0].#subform[0].Risiko[0]";
    public static final String SCALE_RISK_BEDSORE = "Formular1[0].#subform[0].BradenSkala[0]";
    public static final String SHAVE_CARE_BASIN = "Formular1[0].#subform[0].Waschbecken[2]";
    public static final String SHAVE_CARE_BED = "Formular1[0].#subform[0].Bett[2]";
    public static final String SHAVE_CARE_LEVEL = "Formular1[0].#subform[0].Rasieren[0]";
    public static final String SHAVE_CARE_SHOWER = "Formular1[0].#subform[0].Bad_Dusche[2]";
    public static final String SHIN_LEFT_FRONT = "Formular1[0].#subform[1].Kontrollkästchen[48]";
    public static final String SHIN_RIGHT_FRONT = "Formular1[0].#subform[1].Kontrollkästchen[50]";
    public static final String SHOULDER_BACK_LEFT = "Formular1[0].#subform[1].Kontrollkästchen[12]";
    public static final String SHOULDER_BACK_RIGHT = "Formular1[0].#subform[1].Kontrollkästchen[13]";
    public static final String SHOULDER_FRONT_LEFT = "Formular1[0].#subform[1].Kontrollkästchen[42]";
    public static final String SHOULDER_FRONT_RIGHT = "Formular1[0].#subform[1].Kontrollkästchen[41]";
    public static final String SHOULDER_LEFT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[23]";
    public static final String SHOULDER_RIGHT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[2]";
    public static final String SKIN_DRY = "Formular1[0].#subform[0].Haut_trocken[0]";
    public static final String SKIN_GREASY = "Formular1[0].#subform[0].Haut_fettig[0]";
    public static final String SKIN_ITCH = "Formular1[0].#subform[0].Haut_Juckreiz[0]";
    public static final String SKIN_NORMAL = "Formular1[0].#subform[0].Haut_intakt[0]";
    public static final String SLEEP_COMMENTS = "Formular1[0].#subform[0].Besonderheiten[0]";
    public static final String SLEEP_INSOMNIA = "Formular1[0].#subform[0].Schlafstörungen[0]";
    public static final String SLEEP_NORMAL = "Formular1[0].#subform[0].SchlafUngestört[0]";
    public static final String SLEEP_POS_BACK = "Formular1[0].#subform[0].SchlaflageRücken[0]";
    public static final String SLEEP_POS_FRONT = "Formular1[0].#subform[0].SchlaflageBauch[0]";
    public static final String SLEEP_POS_LEFT = "Formular1[0].#subform[0].SchlaflageLinks[0]";
    public static final String SLEEP_POS_RIGHT = "Formular1[0].#subform[0].SchlaflageRechts[0]";
    public static final String SLEEP_RESTLESS = "Formular1[0].#subform[0].SchlafNächlticheUnruhe[0]";
    public static final String SOCIAL_CONFIDANT_CARE = "Formular1[0].#subform[0].#area[2].#area[3].Pflegebereitschaft[0]";
    public static final String SOCIAL_CURRENT_RESTHOME = "Formular1[0].#subform[0].#area[2].Pflegeheim[0]";
    public static final String SOCIAL_CURRENT_SITUATION_CONFIDANT = "Formular1[0].#subform[0].#area[2].Bezugsperson[0]";
    public static final String SOCIAL_CURRENT_SITUATION_NURSING_SERVICE = "Formular1[0].#subform[0].#area[2].amb\\.Pflegedienst[0]";
    public static final String SOCIAL_CURRENT_SITUATION_SELF = "Formular1[0].#subform[0].#area[2].selbständig[0]";
    public static final String SOCIAL_RELIGION = "Formular1[0].#subform[0].Religion[0]";
    public static final String SPECIAL_ALLERGIEPASS = "Formular1[0].#subform[1].#area[9].Allergiepass_vorhanden[0]";
    public static final String SPECIAL_COMMENT_ALLERGY = "Formular1[0].#subform[1].Art[0]";
    public static final String SPECIAL_LASTCONTROL_PACER = "Formular1[0].#subform[1].#area[9].letzteKontrolleam[0]";
    public static final String SPECIAL_MRE = "Formular1[0].#subform[1].#area[9].MRSA[0]";
    public static final String SPECIAL_MYCOSIS = "Formular1[0].#subform[1].Pilzinfektion[0]";
    public static final String SPECIAL_PACER = "Formular1[0].#subform[1].#area[9].Herzschrittmacher[0]";
    public static final String SPECIAL_PALLIATIVE = "Formular1[0].#subform[1].#area[9].Palliativpflege[0]";
    public static final String SPECIAL_WOUNDPAIN = "Formular1[0].#subform[1].#area[9].Wundschmerz[0]";
    public static final String SPECIAL_WOUNDS = "Formular1[0].#subform[1].Wunden[0]";
    public static final String SPECIAL_YESNO_ALLERGY = "Formular1[0].#subform[1].#area[9].Allergien[0]";
    public static final String THERAPY_ERGO = "Formular1[0].#subform[1].TherapieErgotherapie[0]";
    public static final String THERAPY_LOGO = "Formular1[0].#subform[1].TherapieLogopädie[0]";
    public static final String THERAPY_PHYSIO = "Formular1[0].#subform[1].TherapieKrankengymnastik[0]";
    public static final String TX_DATE = "Formular1[0].#subform[0].DatumÜberleitung[0]";
    public static final String TX_FINAL = "Formular1[0].#subform[0].Pflegschaftsauswahlfelder[0]";
    public static final String TX_LOGO = "Formular1[0].#subform[0].Bildfeld1[0]";
    public static final String TX_RECIPIENT = "Formular1[0].#subform[0].#area[8].Überleitungsort[0]";
    public static final String TX_RECIPIENT_OTHER = "Formular1[0].#subform[0].SonstigeÜberleitungsorte[0]";
    public static final String TX_TIME = "Formular1[0].#subform[0].DatumsUhrzeitfeldÜberleitung[0]";
    public static final String UPPER_BACK_LEFT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[24]";
    public static final String UPPER_BELLY = "Formular1[0].#subform[1].Kontrollkästchen[43]";
    public static final String UPPER_LEFTLEG_BACK = "Formular1[0].#subform[1].Kontrollkästchen[17]";
    public static final String UPPER_LEG_LEFT_FRONT = "Formular1[0].#subform[1].Kontrollkästchen[45]";
    public static final String UPPER_LEG_LEFT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[21]";
    public static final String UPPER_LEG_RIGHT_FRONT = "Formular1[0].#subform[1].Kontrollkästchen[46]";
    public static final String UPPER_LEG_RIGHT_SIDE = "Formular1[0].#subform[1].Kontrollkästchen[0]";
    public static final String UPPER_RIGHTLEG_BACK = "Formular1[0].#subform[1].Kontrollkästchen[16]";
    public static final String VALUABLES_COURTORDER = "Formular1[0].#subform[0].#area[2].Versichertenkarte[0]";
    public static final String VALUABLES_CREDITCARD = "Formular1[0].#subform[0].#area[2].Kreditkarte[0]";
    public static final String VALUABLES_HEALTHCARD = "Formular1[0].#subform[0].#area[2].Andenken[0]";
    public static final String VALUABLES_KEYS = "Formular1[0].#subform[0].#area[2].Hausschlüssel[0]";
    public static final String VALUABLES_LIVINGWILL = "Formular1[0].#subform[0].#area[2].Patientenverfügung[0]";
    public static final String VALUABLES_ORGANDONOR = "Formular1[0].#subform[0].#area[2].Organspendeausweis[0]";
    public static final String VALUABLES_OTHER = "Formular1[0].#subform[0].#area[2].SonstigeWertsachen[0]";
    public static final String VALUABLES_WALLET = "Formular1[0].#subform[0].#area[2].Geldbörse[0]";
    public static final String VALUABLES_WATCH = "Formular1[0].#subform[0].#area[2].Uhr[0]";
    public static final String MEDS1 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Medication[0]";
    public static final String DOSAGE1 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Dosage[14]";
    public static final String MEDS2 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Medication[14]";
    public static final String DOSAGE2 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Dosage[0]";
    public static final String MEDS3 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Medication[13]";
    public static final String DOSAGE3 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Dosage[1]";
    public static final String MEDS4 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Medication[1]";
    public static final String DOSAGE4 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Dosage[13]";
    public static final String MEDS5 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Medication[2]";
    public static final String DOSAGE5 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Dosage[12]";
    public static final String MEDS6 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Medication[12]";
    public static final String DOSAGE6 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Dosage[2]";
    public static final String MEDS7 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Medication[11]";
    public static final String DOSAGE7 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Dosage[3]";
    public static final String MEDS8 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Medication[3]";
    public static final String DOSAGE8 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Dosage[11]";
    public static final String MEDS9 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Medication[4]";
    public static final String DOSAGE9 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Dosage[10]";
    public static final String MEDS10 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Medication[10]";
    public static final String DOSAGE10 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Dosage[4]";
    public static final String MEDS11 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Medication[9]";
    public static final String DOSAGE11 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Dosage[5]";
    public static final String MEDS12 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Medication[5]";
    public static final String DOSAGE12 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Dosage[9]";
    public static final String MEDS13 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Medication[6]";
    public static final String DOSAGE13 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Dosage[8]";
    public static final String MEDS14 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Medication[7]";
    public static final String DOSAGE14 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Dosage[7]";
    public static final String MEDS15 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Medication[8]";
    public static final String DOSAGE15 = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Dosage[6]";
    public static final String MEDS_WARNINGTEXT = "Formular1[0].Patientenüberleitung-Seite3[0].#area[0].Text1";


    public static final String[] PDFPARTS = new String[]{HEAD_LEFT_SIDE, SHOULDER_LEFT_SIDE, UPPER_BACK_LEFT_SIDE, ELLBOW_SIDE_LEFT, HAND_LEFT_SIDE, HIP_LEFT_SIDE, BOTTOM_LEFT_SIDE, UPPER_LEG_LEFT_SIDE,
            LOWER_LEG_LEFT_SIDE, CALF_LEFTSIDE, HEEL_LEFT_SIDE, FACE, SHOULDER_FRONT_RIGHT, SHOULDER_FRONT_LEFT, UPPER_BELLY, CROOK_ARM_RIGHT,
            CROOK_ARM_LEFT, LOWER_BELLY, GROIN, UPPER_LEG_RIGHT_FRONT, UPPER_LEG_LEFT_FRONT, KNEE_RIGHT, KNEE_LEFT, SHIN_RIGHT_FRONT, SHIN_LEFT_FRONT,
            FOOT_RIGHT_FRONT, FOOT_LEFT_FRONT, BACK_OF_THE_HEAD, SHOULDER_BACK_LEFT, SHOULDER_BACK_RIGHT, BACK_MID, ELLBOW_LEFT,
            ELLBOW_RIGHT, BACK_LOW, BOTTOM_BACK, UPPER_LEFTLEG_BACK, UPPER_RIGHTLEG_BACK, KNEE_HOLLOWLEFT, KNEE_HOLLOWRIGHT, CALF_LEFTBACK,
            CALF_RIGHTBACK, FOOT_LEFTBACK, FOOT_RIGHTBACK, HEAD_RIGHT_SIDE, SHOULDER_RIGHT_SIDE, BACK_UPPER_LEFT_SIDE, ELLBOW_RIGHTSIDE,
            HAND_RIGHT_SIDE, HIP_RIGHT_SIDE, BOTTOM_RIGHT_SIDE, UPPER_LEG_RIGHT_SIDE, LOWER_LEG_RIGHT_SIDE, CALF_RIGHT_SIDE, HEEL_RIGHT_SIDE};


}
