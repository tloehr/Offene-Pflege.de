package entity.roster;

import entity.system.Users;
import op.OPDE;
import op.tools.SYSCalendar;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 17.08.13
 * Time: 13:38
 * To change this template use File | Settings | File Templates.
 */
public class RPlanTools {


    public static ArrayList<Rplan> getAll(Rosters roster) {
        EntityManager em = OPDE.createEM();
        ArrayList<Rplan> list = null;
//        DateTime from = month.dayOfMonth().withMinimumValue().toDateTimeAtStartOfDay();
//        DateTime to = month.dayOfMonth().withMaximumValue().toDateTimeAtCurrentTime().hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute().withMaximumValue();

        try {
            String jpql = " SELECT rp " +
                    " FROM Rplan rp" +
                    " WHERE rp.roster = :roster " +
                    " ORDER BY rp.owner.uid, rp.start ASC ";

            Query query = em.createQuery(jpql);
            query.setParameter("roster", roster);
//            query.setParameter("to", to.toDate());

            list = new ArrayList<Rplan>(query.getResultList());
        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }


    public static ArrayList<Rplan> getAll(LocalDate day, Rosters roster) {
        EntityManager em = OPDE.createEM();
        ArrayList<Rplan> list = null;
        DateTime from = day.toDateTimeAtStartOfDay();
        DateTime to = day.toDateTimeAtCurrentTime().hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute().withMaximumValue();

        try {
            String jpql = " SELECT rp " +
                    " FROM Rplan rp" +
                    " WHERE rp.roster = :roster AND rp.start >= :from AND rp.start <= :to " +
                    " ORDER BY rp.start ASC ";

            Query query = em.createQuery(jpql);
            query.setParameter("roster", roster);
            query.setParameter("from", from.toDate());
            query.setParameter("to", to.toDate());

            list = new ArrayList<Rplan>(query.getResultList());
        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }


    public static ArrayList<Rplan> getAll(LocalDate day, Users owner) {
        EntityManager em = OPDE.createEM();
        ArrayList<Rplan> list = null;
        DateTime from = day.toDateTimeAtStartOfDay();
        DateTime to = day.toDateTimeAtCurrentTime().hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute().withMaximumValue();

        try {
            String jpql = " SELECT rp " +
                    " FROM Rplan rp" +
                    " WHERE rp.owner = :owner AND rp.start >= :from AND rp.start <= :to " +
                    " ORDER BY rp.start ASC ";

            Query query = em.createQuery(jpql);
            query.setParameter("owner", owner);
            query.setParameter("from", from.toDate());
            query.setParameter("to", to.toDate());

            list = new ArrayList<Rplan>(query.getResultList());
        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    public static ArrayList<Rplan> getAllInWeek(LocalDate week, Users owner) {
        EntityManager em = OPDE.createEM();
        ArrayList<Rplan> list = null;
        DateTime from = week.dayOfWeek().withMinimumValue().toDateTimeAtStartOfDay();
        DateTime to = SYSCalendar.eod(week.dayOfWeek().withMaximumValue());

        try {
            String jpql = " SELECT rp " +
                    " FROM Rplan rp" +
                    " WHERE rp.owner = :owner AND rp.start >= :from AND rp.start <= :to " +
                    " ORDER BY rp.start ASC ";

            Query query = em.createQuery(jpql);
            query.setParameter("owner", owner);
            query.setParameter("from", from.toDate());
            query.setParameter("to", to.toDate());

            list = new ArrayList<Rplan>(query.getResultList());
        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }


}
