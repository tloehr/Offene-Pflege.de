package entity.roster;

import entity.system.Users;
import op.OPDE;
import org.joda.time.DateMidnight;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 24.08.13
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
public class WorkAccountTools {

    public static final int HOLIDAY_MANUAL = 0;
    public static final int HOLIDAY_CALCULATED = 1;
    public static final int HOLIDAY_AUTO = 2;
    public static final int SICK_MANUAL = 3;
    public static final int SICK_CALCULATED = 4;
    public static final int HOURS_MANUAL = 5;
    public static final int HOURS_CALCULATED = 6;
    public static final int HOURS_AUTO = 7;

    public static final int[] HOLIDAYS = new int[]{HOLIDAY_AUTO, HOLIDAY_CALCULATED, HOLIDAY_MANUAL};
    public static final int[] HOURS = new int[]{HOURS_AUTO, HOURS_CALCULATED, HOURS_MANUAL};
    public static final int[] SICK = new int[]{SICK_CALCULATED, SICK_MANUAL};

    public static BigDecimal getSum(DateMidnight day, Users owner, int[] types) {
        EntityManager em = OPDE.createEM();
        BigDecimal sum = null;


        try {
            String jpql = " SELECT SUM(wa.value) " +
                    " FROM Workaccount wa " +
                    " WHERE wa.owner = :owner AND wa.date <= :day AND wa.type IN ( ";
            for (int type : types) {
                jpql += type + ",";
            }

            jpql = jpql.substring(0, jpql.length() - 1) + ")";

            Query query = em.createQuery(jpql);
            query.setParameter("day", day.toDate());
            query.setParameter("owner", owner);

            sum = (BigDecimal) query.getSingleResult();
        } catch (NonUniqueResultException nue) {
            // thats ok
        } catch (NoResultException nre) {
            // thats ok
        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }

        return sum == null ? BigDecimal.ZERO : sum;
    }


}
