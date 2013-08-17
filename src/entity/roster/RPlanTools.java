package entity.roster;

import entity.info.Resident;
import entity.reports.NReport;
import op.OPDE;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

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


    public static ArrayList<RPlan> get4Month(DateMidnight month) {
           EntityManager em = OPDE.createEM();
           ArrayList<RPlan> list = null;
           DateTime from = month.toDateTime().dayOfMonth().withMinimumValue().minuteOfDay().withMinimumValue().secondOfDay().withMinimumValue();
           DateTime to = month.toDateTime().dayOfMonth().withMaximumValue().hourOfDay().withMaximumValue().secondOfDay().withMaximumValue();

           try {
               String jpql = " SELECT rp " +
                       " FROM RPlan rp" +
                       " WHERE rp.start >= :from AND rp.start <= :to " +
                       " ORDER BY rp.owner.uid, rp.start ASC ";

               Query query = em.createQuery(jpql);
               query.setParameter("from", from.toDate());
               query.setParameter("to", to.toDate());

               list = new ArrayList<RPlan>(query.getResultList());
           } catch (Exception se) {
               OPDE.fatal(se);
           } finally {
               em.close();
           }
           return list;
       }




}
