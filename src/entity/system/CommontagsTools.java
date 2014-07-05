package entity.system;

import entity.info.Resident;
import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Created by tloehr on 30.05.14.
 */
public class CommontagsTools {


    public static final int TYPE_SYS_USER = 0;
    public static final int TYPE_SYS_HANDOVER = 1;
    public static final int TYPE_SYS_EMERGENCY = 2;
    public static final int TYPE_SYS_UBV = 3;
    public static final int TYPE_SYS_BV = 4;
    public static final int TYPE_SYS_WOUNDS = 5;
    public static final int TYPE_SYS_FALLS = 6;
    public static final int TYPE_SYS_PAIN = 7;
    public static final int TYPE_SYS_SOCIAL = 8;
    public static final int TYPE_SYS_SOCIAL2 = 10;
    public static final int TYPE_SYS_COMPLAINT = 9;

    public static ArrayList<Commontags> getAll() {
        EntityManager em = OPDE.createEM();
        ArrayList<Commontags> list = null;

        try {

            String jpql = " SELECT c " +
                    " FROM Commontags c " +
                    " ORDER BY c.text ASC ";

            Query query = em.createQuery(jpql);
            list = new ArrayList<Commontags>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }


    public static ArrayList<Commontags> getAllUsedInQMSPlans(boolean inactiveOnes2) {
        EntityManager em = OPDE.createEM();
        ArrayList<Commontags> list = null;

        try {

            String jpql = " SELECT DISTINCT c " +
                    " FROM Commontags c " +
                    " JOIN c.qmsplans qms " +
                    " ORDER BY c.text ASC ";

            Query query = em.createQuery(jpql);
            list = new ArrayList<Commontags>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    public static ArrayList<Commontags> getAllUsedInTrainings() {
        EntityManager em = OPDE.createEM();
        ArrayList<Commontags> list = null;

        try {

            String jpql = " SELECT DISTINCT c " +
                    " FROM Commontags c " +
                    " JOIN c.trainings t " +
                    " ORDER BY c.text ASC ";

            Query query = em.createQuery(jpql);
            list = new ArrayList<Commontags>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    public static ArrayList<Commontags> getAllUsedInNReports(Resident resident) {
        EntityManager em = OPDE.createEM();
        ArrayList<Commontags> list = null;

        try {

//             String mysql = " SELECT DISTINCT ctagid FROM nreports2tags ";
//             Query query = em.createNativeQuery(mysql);
//
//             for ()


            String jpql = " SELECT DISTINCT c " +
                    " FROM Commontags c " +
                    " JOIN c.nReports nr " +
                    " WHERE nr.resident = :resident " +
                    " ORDER BY c.text ASC ";


            Query query = em.createQuery(jpql);
            query.setParameter("resident", resident);
            list = new ArrayList<Commontags>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

}
