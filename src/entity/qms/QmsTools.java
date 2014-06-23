package entity.qms;

import entity.prescription.BHP;
import entity.reports.NReport;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by tloehr on 28.05.14.
 */
public class QmsTools {
    public static final short STATE_OPEN = 0;
    public static final short STATE_DONE = 1;
    public static final short STATE_REFUSED = 2;

    public static ArrayList<Qms> get(Qmssched qmssched, LocalDate year) {
        EntityManager em = OPDE.createEM();
        ArrayList<Qms> list = null;

        try {

            String jpql = " SELECT qms " +
                    " FROM Qms qms " +
                    " WHERE qms.qmssched = :qmssched " +
                    " AND qms.target >= :from AND qms.target <= :to " +
                    " ORDER BY qms.target DESC ";

            Query query = em.createQuery(jpql);

            query.setParameter("qmssched", qmssched);
            query.setParameter("from", SYSCalendar.boy(year).toDateTimeAtStartOfDay().toDate());
            query.setParameter("to", SYSCalendar.eod(SYSCalendar.eoy(year)).toDate());

            list = new ArrayList<Qms>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    public static Icon getIcon(Qms qms) {
            if (qms.getState() == STATE_DONE) {
                return SYSConst.icon22apply;
            }
            if (qms.getState() == STATE_OPEN) {
                return null;
            }
            if (qms.getState() == STATE_REFUSED) {
                return SYSConst.icon22cancel;
            }

            return null;
        }

}
