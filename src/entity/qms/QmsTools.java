package entity.qms;

import io.lamma.Date;
import io.lamma.Lamma4j;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by tloehr on 28.05.14.
 */
public class QmsTools {
    public static final short STATE_OPEN = 0;
    public static final short STATE_DONE = 1;
    public static final short STATE_REFUSED = 2;


    public static void generate(Qmssched qmssched, int howMany) {
//        ArrayList<Qms> listQms = new ArrayList<>();

        Collections.sort(qmssched.getQmsList());
        int maxSequence = qmssched.getQmsList().isEmpty() ? -1 : qmssched.getQmsList().get(qmssched.getQmsList().size() - 1).getSequence();

        ArrayList<Date> listSequence = new ArrayList<>(Lamma4j.sequence(SYSCalendar.toLammaDate(qmssched.getQmsplan().getFrom()), SYSCalendar.toLammaDate(SYSCalendar.min(new LocalDate(qmssched.getQmsplan().getTo()), new LocalDate().plusYears(1)).toDate()), QmsschedTools.getRecurrence(qmssched)));

        if (listSequence.size() >= maxSequence) {
            for (int element = maxSequence+1; element < maxSequence + howMany + 1; element++) {
                qmssched.getQmsList().add(new Qms(SYSCalendar.toLocalDate(listSequence.get(element)).toDate(), qmssched, element));
            }
        }
    }


//    public static ArrayList<Qms> get(Qmssched qmssched, LocalDate year) {
//        EntityManager em = OPDE.createEM();
//        ArrayList<Qms> list = null;
//
//        try {
//
//            String jpql = " SELECT qms " +
//                    " FROM Qms qms " +
//                    " WHERE qms.qmssched = :qmssched " +
//                    " AND qms.target >= :from AND qms.target <= :to " +
//                    " ORDER BY qms.target DESC ";
//
//            Query query = em.createQuery(jpql);
//
//            query.setParameter("qmssched", qmssched);
//            query.setParameter("from", SYSCalendar.boy(year).toDateTimeAtStartOfDay().toDate());
//            query.setParameter("to", SYSCalendar.eod(SYSCalendar.eoy(year)).toDate());
//
//            list = new ArrayList<Qms>(query.getResultList());
//
//        } catch (Exception se) {
//            OPDE.fatal(se);
//        } finally {
//            em.close();
//        }
//        return list;
//    }

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
