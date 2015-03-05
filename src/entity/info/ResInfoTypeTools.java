package entity.info;

import entity.system.Commontags;
import entity.system.CommontagsTools;
import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 24.10.11
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class ResInfoTypeTools {

    public static final int MODE_INTERVAL_BYSECOND = 0;
    public static final int MODE_INTERVAL_BYDAY = 1;
    public static final int MODE_INTERVAL_NOCONSTRAINTS = 2;
    public static final int MODE_INTERVAL_SINGLE_INCIDENTS = 3; // Das sind Ereignisse, bei denen von == bis gilt. Weitere Einschränkungen werden nicht gemacht.

    public static final int STATUS_INACTIVE_NORMAL = -1;
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_SYSTEM = 10;
    public static final int STATUS_INACTIVE_SYSTEM = -10;

    public static final int TYPE_ABSENCE = 10;
    public static final int TYPE_ALLERGY = 97;
    public static final int TYPE_INFECTION = 99;
    public static final int TYPE_DIABETES = 98;
    public static final int TYPE_DIAGNOSIS = 50;
    public static final int TYPE_FALL = 30; // Sturzprotokoll
    public static final int TYPE_OLD = -1;

    public static final int TYPE_WARNING = 96;
    public static final int TYPE_WOUNDS = 20;


    public static final int TYPE_STAY = 100;
    public static final int TYPE_COMMS = 101;
    public static final int TYPE_CONFIDANTS = 102;
    public static final int TYPE_LEGALCUSTODIANS = 103;
    public static final int TYPE_PERSONALS = 104;
    public static final int TYPE_NURSING_INSURANCE = 105;
    public static final int TYPE_HEALTH_INSURANCE = 106;
    public static final int TYPE_MOUTHCARE = 107;
    public static final int TYPE_CARE = 108;
    public static final int TYPE_SKIN = 109;
    public static final int TYPE_MOBILITY = 110;
    public static final int TYPE_EXCRETIONS = 111;
    public static final int TYPE_INCOAID = 112;
    public static final int TYPE_INCO_PROFILE_DAY = 113;
    public static final int TYPE_INCO_PROFILE_NIGHT = 114;
    public static final int TYPE_INCO_FAECAL = 115;
    public static final int TYPE_SCALE_BRADEN = 116;
    public static final int TYPE_WOUND1 = 117;
    public static final int TYPE_WOUND2 = 118;
    public static final int TYPE_WOUND3 = 119;
    public static final int TYPE_WOUND4 = 120;
    public static final int TYPE_WOUND5 = 121;
    public static final int[] TYPE_ALL_WOUNDS = new int[]{TYPE_WOUND1, TYPE_WOUND2, TYPE_WOUND3, TYPE_WOUND4, TYPE_WOUND5};
    public static final int TYPE_WOUNDHISTORY1 = 122;
    public static final int TYPE_WOUNDHISTORY2 = 123;
    public static final int TYPE_WOUNDHISTORY3 = 124;
    public static final int TYPE_WOUNDHISTORY4 = 125;
    public static final int TYPE_WOUNDHISTORY5 = 126;
    public static final int TYPE_SLEEP = 127;
    public static final int TYPE_FOOD = 128;
    public static final int TYPE_ARTIFICIAL_NUTRTITION = 129;
    public static final int TYPE_PACEMAKER = 130;
    public static final int TYPE_CONSCIUOS = 131;
    public static final int TYPE_ORIENTATION = 132;
    public static final int TYPE_RESPIRATION = 133;
    public static final int TYPE_MEDS = 134;
    public static final int TYPE_MYCOSIS = 135;
    public static final int TYPE_PSYCH = 136;
    public static final int TYPE_PAIN = 137;
    public static final int TYPE_FIXATION= 138;
    public static final int TYPE_LIVINGWILL= 139;
    public static final int TYPE_AMPUTATION = 140;
    public static final int TYPE_SPECIALIST = 141;
    public static final int TYPE_HOSPITAL_STAY = 142;
    public static final int TYPE_ANTIBIOTICS = 143;
    public static final int TYPE_VACCINE = 144;

    public static final String TYPE_ABSENCE_HOSPITAL = "HOSPITAL";
    public static final String TYPE_ABSENCE_HOLLIDAY = "HOLLIDAY";
    public static final String TYPE_ABSENCE_OTHER = "OTHER";

    public static final String STAY_KEY = "stay";
    public static final String STAY_VALUE_PRESENT = "";
    public static final String STAY_VALUE_DEAD = "dead";
    public static final String STAY_VALUE_LEFT = "left";

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
        Query query = em.createQuery("SELECT b FROM ResInfoType b WHERE b.type = :type");
        query.setParameter("type", type);
        List<ResInfoType> resInfoTypes = query.getResultList();
        em.close();
        return resInfoTypes.isEmpty() ? null : resInfoTypes.get(0);
    }

    public static List<ResInfoType> getByCat(ResInfoCategory category) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfoType b WHERE b.resInfoCat = :cat ORDER BY b.bWInfoKurz, b.bwinftyp");  // AND b.type >= 0
        query.setParameter("cat", category);
        List<ResInfoType> resInfoTypen = query.getResultList();
        em.close();
        return resInfoTypen;
    }

    public static List<ResInfoType> getAllActive() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfoType b WHERE b.type >= 0 ORDER BY b.bWInfoKurz ");
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

    public static ResInfoType getResInfoType4Annotation(Commontags tag){
        if (tag.getType() == CommontagsTools.TYPE_SYS_ANTIBIOTICS){
            return getByType(TYPE_ANTIBIOTICS);
        }
        return null;
    }


    public static boolean is4Annotations(ResInfoType resInfoType){
            if (resInfoType.getType() == TYPE_ANTIBIOTICS){
                return true;
            }
            return false;
        }




    public static boolean containsOneActiveObsoleteInfo(ArrayList<ResInfo> listInfos) {
        boolean containsOneActiveObsoleteInfo = false;
        for (ResInfo info : listInfos) {
            containsOneActiveObsoleteInfo = info.getResInfoType().isObsolete() && !info.isClosed();
            if (containsOneActiveObsoleteInfo) {
                break;
            }
        }
        return containsOneActiveObsoleteInfo;
    }

//    /**
//     * if you hand over an obsolete infotype to this method it returns an
//     * active version for it, using the equiv attribute. If the type is not
//     * obsolete, you will get it back.
//     * If there is no active version you will get NULL instead.
//     * Infotypes without replacements have either none active one withing their
//     * equiv domain or equiv is 0.
//     *
//     * @param resInfoType
//     * @return
//     */
//    public static ResInfoType getActiveVersion4(ResInfoType resInfoType) {
//        if (!resInfoType.isObsolete()) {
//            return resInfoType;
//        }
//        if (resInfoType.getEquiv().intValue() == 0) {
//            return null;
//        }
//        EntityManager em = OPDE.createEM();
//        Query query = em.createQuery("SELECT b FROM ResInfoType b WHERE b.equiv = :equiv AND b.type >= 0 ");
//        query.setParameter("equiv", resInfoType.getEquiv());
//        List<ResInfoType> resInfoTypes = query.getResultList();
//        em.close();
//        if (resInfoTypes.isEmpty()) {
//            return null;
//        }
//        return resInfoTypes.get(0); // there should never be more than one active resinfotype.
//    }

}
