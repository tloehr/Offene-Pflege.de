package entity.roster;

import op.OPDE;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 16.10.13
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
public class WorkinglogTools {

    public static ArrayList<Workinglog> getAll(LocalDate from, LocalDate to) {
        EntityManager em = OPDE.createEM();
        ArrayList<Workinglog> list = null;

        try {
            String jpql = " SELECT wl " +
                    " FROM Workinglog wl" +
                    " WHERE wl.pit >= :from AND wl.pit <= :to " +
                    " ORDER BY wl.pit ASC ";

            Query query = em.createQuery(jpql);
            query.setParameter("from", from.toDate());
            query.setParameter("to", to.toDate());

            list = new ArrayList<Workinglog>(query.getResultList());
        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    public static ArrayList<Workinglog> getAll(LocalDate month) {
        //        DateTime from = month.dayOfMonth().withMinimumValue().toDateTimeAtStartOfDay();
        //        DateTime to = month.dayOfMonth().withMaximumValue().toDateTimeAtCurrentTime().hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute().withMaximumValue();

        return getAll(month.dayOfMonth().withMinimumValue(), month.dayOfMonth().withMaximumValue());

    }
}
