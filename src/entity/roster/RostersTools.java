package entity.roster;

import entity.info.Resident;
import entity.reports.NReport;
import op.OPDE;
import op.tools.Pair;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 16.08.13
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class RostersTools {


    public static Pair<DateMidnight, DateMidnight> getMinMax() {
        Pair<DateMidnight, DateMidnight> result = null;

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createQuery("SELECT r FROM Rosters r ORDER BY r.month ASC ");
        queryMin.setMaxResults(1);

        Query queryMax = em.createQuery("SELECT r FROM Rosters r ORDER BY r.month DESC ");
        queryMax.setMaxResults(1);

        try {
            ArrayList<NReport> min = new ArrayList<NReport>(queryMin.getResultList());
            ArrayList<NReport> max = new ArrayList<NReport>(queryMax.getResultList());
            if (min.isEmpty()) {
                result = null;
            } else {
                result = new Pair<DateMidnight, DateMidnight>(new DateMidnight(min.get(0).getPit()), new DateMidnight(max.get(0).getPit()));
            }

        } catch (Exception e) {
            OPDE.fatal(e);
        }

        em.close();
        return result;
    }
}
