package de.offene_pflege.entity.system;

import de.offene_pflege.entity.info.ResInfo;
import de.offene_pflege.entity.info.ResInfoTypeTools;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by tloehr on 30.05.14.
 */
public class CommontagsTools {

    // type settings in the "commontags" table. every type which is not 0 is considered a system type, which can not be edited or deleted.
    // those tags are used in queries for controlling purposes. so the system relies on their presence.
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
    public static final int TYPE_SYS_PAINMGR = 11;
    public static final int TYPE_SYS_METHADONE = 12; // methadon maintenance (Substitutionspräparate)
    public static final int TYPE_SYS_NARCOTICS = 13; // general narcotics (enhanced legal action necessary)
    public static final int TYPE_SYS_ANTIBIOTICS = 14; // antibiotics (relevant for prevalence analysis)
    // TODO: really ?
//    public static final int TYPE_SYS_DRAIN_INDICATION = 14; // methadon maintenance (Substitutionspräparate)


    public static boolean isAnnotationNecessary(Commontags tag) {
        return tag.getType() == TYPE_SYS_ANTIBIOTICS;
    }


    public static Commontags getTagForAnnotation(ResInfo annotation) {
        if (annotation.getResInfoType().getType() == ResInfoTypeTools.TYPE_ANTIBIOTICS) {
            return getType(TYPE_SYS_ANTIBIOTICS);
        }

        return null;
    }

    public static ArrayList<Commontags> getAll() {
        EntityManager em = OPDE.createEM();
        ArrayList<Commontags> list = null;

        try {

            String jpql = " SELECT c " +
                    " FROM Commontags c " +
                    " ORDER BY c.color, c.type, c.text ASC ";

            Query query = em.createQuery(jpql);
            list = new ArrayList<Commontags>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    public static Commontags getType(int type) {
        EntityManager em = OPDE.createEM();
        ArrayList<Commontags> list = null;

        try {

            String jpql = " SELECT c " +
                    " FROM Commontags c " +
                    " WHERE c.type = :type ";

            Query query = em.createQuery(jpql);
            query.setParameter("type", type);

            list = new ArrayList<Commontags>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list.isEmpty() ? null : list.get(0);
    }

    public static ArrayList<Commontags> getAllUsedInNReports(Resident resident) {
        EntityManager em = OPDE.createEM();
        ArrayList<Commontags> list = null;

        try {

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

    public static ArrayList<Commontags> getAllUsedInPrescription(Resident resident) {
        EntityManager em = OPDE.createEM();
        ArrayList<Commontags> list = null;

        try {

            String jpql = " SELECT DISTINCT c " +
                    " FROM Commontags c " +
                    " JOIN c.prescriptions p " +
                    " WHERE p.resident = :resident " +
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

    public static ArrayList<Commontags> getAllUsedInNursingProcess(Resident resident) {
        EntityManager em = OPDE.createEM();
        ArrayList<Commontags> list = null;

        try {

            String jpql = " SELECT DISTINCT c " +
                    " FROM Commontags c " +
                    " JOIN c.nursingProcesses p " +
                    " WHERE p.resident = :resident " +
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

    public static ArrayList<Commontags> getAllUsedInResInfos(Resident resident) {
        EntityManager em = OPDE.createEM();
        ArrayList<Commontags> list = null;

        try {

            String jpql = " SELECT DISTINCT c " +
                    " FROM Commontags c " +
                    " JOIN c.resinfos p " +
                    " WHERE p.resident = :resident " +
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

    public static String getAsHTML(Collection<Commontags> commontags, String icon) {
        return getAsHTML(commontags, icon, 0);
    }

    public static String getAsHTML(Collection<Commontags> commontags, String icon, int maxline) {
        String result = "";
        int i = 0;
        for (Commontags ctag : commontags) {
            i++;
            result += icon + "&nbsp;" + "<font color=\"#" + ctag.getColor() + "\">" + ctag.getText() + "</font> ";
            if (maxline > 0 && i % maxline == 0) {
                result += "<br/>";
            }
        }
        return result;
    }

//    public static Chunk getAsChunk(Collection<Commontags> commontags) {
//            Chunk chunk = new Chunk();
//            int i = 0;
//            for (Commontags ctag : commontags) {
//                i++;
//                Paragrap
//                chunk.append("");
//                result += SYSCons + "&nbsp;" + "<font color=\"#" + ctag.getColor() + "\">" + ctag.getText() + "</font> ";
//                if (maxline > 0 && i % maxline == 0){
//                    result += "<br/>";
//                }
//            }
//            return result;
//        }

}
