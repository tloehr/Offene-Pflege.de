package entity.info;

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
    public static final int TYPE_BIOHAZARD = 99;
    public static final int TYPE_DIABETES = 98;
    public static final int TYPE_DIAGNOSIS = 50;
    public static final int TYPE_FALL = 30; // Sturz
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



//    public static final String TYPE_HOSPITAL_STAY = "KH";

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

    /**
     * if you hand over an obsolete infotype to this method it returns an
     * active version for it, using the equiv attribute. If the type is not
     * obsolete, you will get it back.
     * If there is no active version you will get NULL instead.
     * Infotypes without replacements have either none active one withing their
     * equiv domain or equiv is 0.
     *
     * @param resInfoType
     * @return
     */
    public static ResInfoType getActiveVersion4(ResInfoType resInfoType) {
        if (!resInfoType.isObsolete()) {
            return resInfoType;
        }
        if (resInfoType.getEquiv().intValue() == 0) {
            return null;
        }
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfoType b WHERE b.equiv = :equiv AND b.type >= 0 ");
        query.setParameter("equiv", resInfoType.getEquiv());
        List<ResInfoType> resInfoTypes = query.getResultList();
        em.close();
        if (resInfoTypes.isEmpty()) {
            return null;
        }
        return resInfoTypes.get(0); // there should never be more than one active resinfotype.
    }

}
