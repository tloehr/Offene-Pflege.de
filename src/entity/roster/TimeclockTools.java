package entity.roster;

import entity.system.Users;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Created by tloehr on 11.01.14.
 */
public class TimeclockTools {
    public static final int STATE_UNCHECKED = 0;
    public static final int STATE_ACCEPTED = 1;
    public static final int STATE_CORRECTED = 2;
    public static final int STATE_REJECTED = 3;


    public static ArrayList<Timeclock> getAllWithinLast(int weeks, Users owner) {
        EntityManager em = OPDE.createEM();
        ArrayList<Timeclock> list = null;
        DateTime from = SYSCalendar.bow(new DateTime().minusWeeks(weeks).toLocalDate()).toDateTimeAtStartOfDay();

        try {
            String jpql = " SELECT tc " +
                    " FROM Timeclock tc" +
                    " WHERE tc.owner = :owner AND tc.begin >= :from " +
                    " ORDER BY tc.begin DESC ";

            Query query = em.createQuery(jpql);
            query.setParameter("owner", owner);
            query.setParameter("from", from.toDate());

            list = new ArrayList<Timeclock>(query.getResultList());
        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    public static ArrayList<Timeclock> getAllStartingOn(LocalDate startDate, Users owner) {
        EntityManager em = OPDE.createEM();
        ArrayList<Timeclock> list = null;
        DateTime from = startDate.toDateTimeAtStartOfDay();
        DateTime to = SYSCalendar.eod(startDate);

        try {
            String jpql = " SELECT tc " +
                    " FROM Timeclock tc" +
                    " WHERE tc.owner = :owner AND tc.begin >= :from AND tc.begin <= :to " +
                    " ORDER BY tc.begin DESC ";

            Query query = em.createQuery(jpql);
            query.setParameter("owner", owner);
            query.setParameter("from", from.toDate());
            query.setParameter("to", to.toDate());

            list = new ArrayList<Timeclock>(query.getResultList());
        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    public static Timeclock getActive(Users owner) {
        EntityManager em = OPDE.createEM();
        Timeclock active = null;

        try {
            String jpql = " SELECT tc " +
                    " FROM Timeclock tc" +
                    " WHERE tc.owner = :owner AND tc.end = :tfn " +
                    " ORDER BY tc.begin DESC ";

            Query query = em.createQuery(jpql);
            query.setParameter("owner", owner);
            query.setParameter("tfn", SYSConst.DATE_UNTIL_FURTHER_NOTICE);


            ArrayList<Timeclock> list = new ArrayList<Timeclock>(query.getResultList());
            active = list.isEmpty() ? null : list.get(0);

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return active;
    }


}
