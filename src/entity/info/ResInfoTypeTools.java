package entity.info;

import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
    public static final int TYPE_STAY = 100;
    public static final int TYPE_WARNING = 96;
    public static final int TYPE_WOUNDS = 20;


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
        Query query = em.createQuery("SELECT b FROM ResInfoType b WHERE b.resInfoCat = :cat  ORDER BY b.bWInfoKurz, b.bwinftyp");  // AND b.type >= 0
        query.setParameter("cat", category);
        List<ResInfoType> resInfoTypen = query.getResultList();
        em.close();
        return resInfoTypen;
    }

    public static List<ResInfoType> getAllActive() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfoType b WHERE b.type >= 0 ");
        List<ResInfoType> resInfoTypen = query.getResultList();
        em.close();
        return resInfoTypen;
    }

}
