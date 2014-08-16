package entity.qms;

import entity.system.Users;
import io.lamma.Date;
import io.lamma.Lamma4j;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by tloehr on 28.05.14.
 */
public class QmsTools {
    public static final short STATE_OPEN = 0;
    public static final short STATE_DONE = 1;
    public static final short STATE_REFUSED = 2;


    /**
     * this method generates QMS objects. It uses the lamma.io date sequence generator and checks which occurence is next.
     * This one will be the new one to generate. Every QMS object knows its
     * position in the recurrence list.
     *
     * @param qmssched      the schedule which contains the recurrence pattern
     * @param numOccurences how many occurences should be generated.
     */
    public static void generate(Qmssched qmssched, int numOccurences) {
//        ArrayList<Qms> listQms = new ArrayList<>();

        Collections.sort(qmssched.getQmsList());
        int maxSequence = qmssched.getQmsList().isEmpty() ? -1 : qmssched.getQmsList().get(qmssched.getQmsList().size() - 1).getSequence();

        ArrayList<Date> listSequence = new ArrayList<>();
        if (qmssched.isYearly()){
            listSequence.addAll(Lamma4j.sequence(SYSCalendar.toLammaDate(qmssched.getStartingOn()), SYSCalendar.toLammaDate(new LocalDate().plusYears(20).toDate()), QmsschedTools.getRecurrence(qmssched)));
        } else {
            listSequence.addAll(Lamma4j.sequence(SYSCalendar.toLammaDate(qmssched.getStartingOn()), SYSCalendar.toLammaDate(new LocalDate().plusYears(Math.max(qmssched.getYearly(), 2)).toDate()), QmsschedTools.getRecurrence(qmssched)));
        }
         new ArrayList<>();

        if (listSequence.size() >= maxSequence) {
            for (int element = maxSequence + 1; element < maxSequence + numOccurences + 1; element++) {
                qmssched.getQmsList().add(new Qms(SYSCalendar.toLocalDate(listSequence.get(element)).toDate(), qmssched, element));
            }
        }
    }

    /**
     * retrieves a list of all due and overdue QMSs.
     * @param user the list is created only for those users which are supposed to be notified
     *
     * @return
     */
    public static ArrayList<Qms> getDueList(Users user) {
        EntityManager em = OPDE.createEM();
        ArrayList<Qms> result = new ArrayList<>();

        try {


            String jpql = " SELECT qms " +
                    " FROM Qms qms " +
                    " WHERE qms.qmssched.state = :schedstate AND qms.qmsplan.state = :planstate AND qms.state = :qmsstate " +
                    " AND :user MEMBER OF qms.qmsplan.notification " +
                    " ORDER BY qms.target DESC ";

            Query query = em.createQuery(jpql);

            query.setParameter("schedstate", QmsschedTools.STATE_ACTIVE);
            query.setParameter("planstate", QmsplanTools.STATE_ACTIVE);
            query.setParameter("qmsstate", QmsTools.STATE_OPEN);
            query.setParameter("user", user);

            ArrayList<Qms> listPre = new ArrayList<Qms>(query.getResultList());

            for (Qms qms : listPre) {
                if (qms.isDue()) {
                    result.add(qms);
                }
            }

            listPre.clear();

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return result;
    }

    public static Icon getIcon(Qms qms) {
        if (qms.getState() == STATE_DONE) {
            return SYSConst.icon22apply;
        }

        if (qms.isPastDue()) {
            return SYSConst.icon22ledRedOn;
        }

        if (qms.isDue()) {
            return SYSConst.icon22ledYellowOn;
        }

        if (qms.isOpen()) {
            return null;
        }

        if (qms.getState() == STATE_REFUSED) {
            return SYSConst.icon22cancel;
        }

        return null;
    }


    public static String toHTML(Qms qms) {

        String result = SYSConst.html_bold(DateFormat.getDateInstance().format(qms.getTarget()));

        if (qms.isDue()) {
            result += " // (" + SYSTools.xx(qms.isPastDue() ? "misc.msg.pastdue" : "misc.msg.due") + ")";
        }

        if (!qms.isOpen()) {
            result += " // " + DateFormat.getDateInstance().format(qms.getActual()) + "; " + qms.getUser().getUID();
        }


        return result;
    }

}
