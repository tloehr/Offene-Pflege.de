package de.offene_pflege.backend.services;


import de.offene_pflege.backend.entity.done.ResInfo;
import de.offene_pflege.backend.entity.done.ResInfoCategory;
import de.offene_pflege.backend.entity.done.ResInfoType;
import de.offene_pflege.backend.entity.system.Commontags;
import de.offene_pflege.backend.entity.system.CommontagsTools;
import de.offene_pflege.op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: tloehr Date: 24.10.11 Time: 16:33 To change this template use File | Settings | File
 * Templates.
 */
public class ResInfoTypeTools {

    public static final int MODE_INTERVAL_BYSECOND = 0;
    public static final int MODE_INTERVAL_BYDAY = 1;
    public static final int MODE_INTERVAL_NOCONSTRAINTS = 2;
    public static final int MODE_INTERVAL_SINGLE_INCIDENTS = 3; // Das sind Ereignisse, bei denen von == bis gilt. Weitere Einschränkungen werden nicht gemacht.
    public static final String[] INTERVALS = new String[]{"PERIOD_BY_SECONDS","PERIOD_BY_DATE","PERIOD_WITHOUT_CONSTRAINTS","POINT_IN_TIME"};

//    public static final int STATUS_INACTIVE_NORMAL = -1;
//    public static final int STATUS_NORMAL = 0;
//    public static final int STATUS_SYSTEM = 10;
//    public static final int STATUS_INACTIVE_SYSTEM = -10;

    // types 31-48 werden hier nicht verwendet.

    public static final int TYPE_ABSENCE = 10;
    public static final int TYPE_ROOM = 12;
    public static final int TYPE_ALLERGY = 97;
    public static final int TYPE_INFECTION = 99;
    public static final int TYPE_DIABETES = 98;
    public static final int TYPE_DIAGNOSIS = 50;
    public static final int TYPE_FALL = 30; // Sturzprotokoll
    public static final int TYPE_FALLRISK = 157;
//    public static final int TYPE_OLD = -1;

    public static final int TYPE_WARNING = 96;
    public static final int TYPE_WOUNDS = 20;

    public static final int TYPE_STAY = 100;
    public static final int TYPE_COMMS = 101;
    public static final int TYPE_CONFIDANTS = 102;
    public static final int TYPE_LEGALCUSTODIANS = 103;
    public static final int TYPE_PERSONALS = 104;
    public static final int TYPE_NURSING_INSURANCE = 105;
    public static final int TYPE_HEALTH_INSURANCE = 106;

    public static final int TYPE_CARE = 108;
    public static final int TYPE_SKIN = 109;
    public static final int TYPE_MOBILITY = 110;
    public static final int TYPE_EXCRETIONS = 111;
    public static final int TYPE_SCALE_BRADEN = 116;
    public static final int TYPE_WOUND1 = 117;
    public static final int TYPE_WOUND2 = 118;
    public static final int TYPE_WOUND3 = 119;
    public static final int TYPE_WOUND4 = 120;
    public static final int TYPE_WOUND5 = 121;
    public static final int TYPE_WOUND6 = 147;
    public static final int TYPE_WOUND7 = 148;
    public static final int TYPE_WOUND8 = 149;
    public static final int TYPE_WOUND9 = 150;
    public static final int TYPE_WOUND10 = 151;
    public static final int[] TYPE_ALL_WOUNDS = new int[]{TYPE_WOUND1, TYPE_WOUND2, TYPE_WOUND3, TYPE_WOUND4, TYPE_WOUND5, TYPE_WOUND6, TYPE_WOUND7, TYPE_WOUND8, TYPE_WOUND9, TYPE_WOUND10};
    public static final int TYPE_WOUNDHISTORY1 = 122;
    public static final int TYPE_WOUNDHISTORY2 = 123;
    public static final int TYPE_WOUNDHISTORY3 = 124;
    public static final int TYPE_WOUNDHISTORY4 = 125;
    public static final int TYPE_WOUNDHISTORY5 = 126;
    public static final int TYPE_WOUNDHISTORY6 = 152;
    public static final int TYPE_WOUNDHISTORY7 = 153;
    public static final int TYPE_WOUNDHISTORY8 = 154;
    public static final int TYPE_WOUNDHISTORY9 = 155;
    public static final int TYPE_WOUNDHISTORY10 = 156;
    public static final int TYPE_SLEEP = 127;
    public static final int TYPE_FOOD = 128;
    public static final int TYPE_ARTIFICIAL_NUTRTITION = 129;
    public static final int TYPE_PACEMAKER = 130;
    public static final int TYPE_CONSCIOUS = 131;
    public static final int TYPE_ORIENTATION = 132;
    public static final int TYPE_RESPIRATION = 133;
    public static final int TYPE_MEDS = 134;
    public static final int TYPE_MYCOSIS = 135;
    public static final int TYPE_PSYCH = 136;
    public static final int TYPE_PAIN = 137;
    public static final int TYPE_FIXIERUNGS_BESCHLUSS = 138;
    public static final int TYPE_LIVINGWILL = 139;
    public static final int TYPE_AMPUTATION = 140;
    public static final int TYPE_SPECIALIST = 141;
    public static final int TYPE_HOSPITAL_STAY = 142;
    public static final int TYPE_ANTIBIOTICS = 143;
    public static final int TYPE_VACCINE = 144;
    public static final int TYPE_SURGERY = 145;
    public static final int TYPE_VESSEL_CATHETER = 146;

    public static final int TYPE_APOPLEX = 158; // Apoplex


    ///////////////////////////////////////////////
    // DEPRECATED
    public static final int TYPE_INCOAID = 112;
    public static final int TYPE_INCO_PROFILE_DAY = 113;
    public static final int TYPE_INCO_PROFILE_NIGHT = 114;
    public static final int TYPE_INCO_FAECAL = 115;
    // Ersetzt durch
    public static final int TYPE_INCO = 159;
    ///////////////////////////////////////////////
    // DEPRECATED
    public static final int TYPE_MOUTHCARE = 107;
    ///////////////////////////////////////////////

    public static final int TYPE_ALLTAG = 160; // Alltagsleben. EQUIV 135
    public static final int TYPE_SOZIALES = 161; // Soziales


    public static final int TYPE_FRAKTUR = 162;
    public static final int TYPE_HERZINFARKT = 163;
    public static final int TYPE_KOERPERGEWICHTDOKU = 164;
    public static final int TYPE_FALL_AUSWIRKUNG = 165;
    public static final int TYPE_FIXIERUNGPROTOKOLL = 166;


    public static final int TYPE_BESD = 167;
    public static final int TYPE_INTEGRATIONS_GESPRAECH = 168;

    // find new max type via sql: SELECT 'next to use',MAX(equiv)+1, MAX(TYPE)+1 FROM resinfotype;

    public static final String TYPE_ABSENCE_HOSPITAL = "HOSPITAL";
    public static final String TYPE_ABSENCE_HOLLIDAY = "HOLLIDAY";
    public static final String TYPE_ABSENCE_OTHER = "OTHER";

    public static final String KZP_KEY = "kzp"; // Kurzzeitpflege. TRUE, oder FALSE (bzw. der Key fehlt)
    public static final String STAY_KEY = "stay";
    public static final String STAY_VALUE_PRESENT = "";
    public static final String STAY_VALUE_DEAD = "dead";
    public static final String STAY_VALUE_LEFT = "left";
    public static final String STAY_VALUE_NOW_PERMANENT = "war_kzp_jetzt_dauerhaft"; // KZP Aufenthalt, der durch einen dauerhaften Aufenthalt ersetzt wurde.

    public static ResInfoType getByID(String id) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfoType b WHERE b.bwinftyp = :bwinftyp");
        query.setParameter("bwinftyp", id);
        List<ResInfoType> resInfoTypes = query.getResultList();
        em.close();
        return resInfoTypes.isEmpty() ? null : resInfoTypes.get(0);
    }

    public static ResInfoType getByType(int type) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfoType b WHERE b.type = :type AND b.deprecated = false");
        query.setParameter("type", type);
        List<ResInfoType> resInfoTypes = query.getResultList();
        em.close();
        return resInfoTypes.isEmpty() ? null : resInfoTypes.get(0);
    }

    public static List<ResInfoType> getByCat(ResInfoCategory category) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfoType b WHERE b.resInfoCat = :cat  ORDER BY b.bWInfoKurz, b.bwinftyp");  // AND b.type >= 0
        query.setParameter("cat", category);
        List<ResInfoType> resInfoTypen = query.getResultList();
        em.close();
        return resInfoTypen;
    }

    public static List<ResInfoType> getAllActive() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfoType b WHERE b.deprecated = false ORDER BY b.bWInfoKurz ");
//        query.setParameter("type", TYPE_DIAGNOSIS);
        List<ResInfoType> resInfoTypen = query.getResultList();
        em.close();
        return resInfoTypen;
    }

    public static List<ResInfoType> getAll() {
            EntityManager em = OPDE.createEM();
            Query query = em.createQuery("SELECT b FROM ResInfoType b ORDER BY b.bWInfoKurz ");
    //        query.setParameter("type", TYPE_DIAGNOSIS);
            List<ResInfoType> resInfoTypen = query.getResultList();
            em.close();
            return resInfoTypen;
        }

    public static boolean containsOnlyClosedInfos(ArrayList<ResInfo> listInfos) {
        boolean containsOnlyClosedInfos = true;
        for (ResInfo info : listInfos) {
            containsOnlyClosedInfos = info.isClosed();
            if (!containsOnlyClosedInfos) {
                break;
            }
        }
        return containsOnlyClosedInfos;
    }

    public static ResInfoType getResInfoType4Annotation(Commontags tag) {
        if (tag.getType() == CommontagsTools.TYPE_SYS_ANTIBIOTICS) {
            return getByType(TYPE_ANTIBIOTICS);
        }
        return null;
    }


    public static boolean is4Annotations(ResInfoType resInfoType) {
        if (resInfoType.getType() == TYPE_ANTIBIOTICS) {
            return true;
        }
        return false;
    }

    public static boolean containsOneActiveObsoleteInfo(ArrayList<ResInfo> listInfos) {
        boolean containsOneActiveObsoleteInfo = false;
        for (ResInfo info : listInfos) {
            containsOneActiveObsoleteInfo = info.getResInfoType().isDeprecated() && !info.isClosed();
            if (containsOneActiveObsoleteInfo) {
                break;
            }
        }
        return containsOneActiveObsoleteInfo;
    }


}
