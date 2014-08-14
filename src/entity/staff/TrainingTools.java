package entity.staff;

import entity.system.Commontags;
import entity.system.CommontagsTools;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.tools.Pair;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tloehr on 17.05.14.
 */
public class TrainingTools {

    public static final byte STATE_INTERNAL = 0;
    public static final byte STATE_WORK_PLACE_RELATED = 1;
    public static final byte STATE_EXTERNAL = 2;

    public static final String[] STATES = new String[]{"opde.training.state.internal", "opde.training.state.workplace", "opde.training.state.external"};

    /**
     * retrieves the PITs of the first and the last entry in the training table.
     *
     * @return
     */
    public static Pair<LocalDate, LocalDate> getMinMax() {
        Pair<LocalDate, LocalDate> result = null;

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createQuery("SELECT t FROM Training t ORDER BY t.starting ASC ");
        queryMin.setMaxResults(1);

        Query queryMax = em.createQuery("SELECT t FROM Training t ORDER BY t.starting DESC ");
        queryMax.setMaxResults(1);

        try {
            ArrayList<Training> min = new ArrayList<Training>(queryMin.getResultList());
            ArrayList<Training> max = new ArrayList<Training>(queryMax.getResultList());
            if (min.isEmpty()) {
                result = null;
            } else {
                result = new Pair<LocalDate, LocalDate>(new LocalDate(min.get(0).getStarting()), new LocalDate(max.get(0).getStarting()));
            }

        } catch (Exception e) {
            OPDE.fatal(e);
        }

        em.close();
        return result;
    }

    public static Pair<LocalDate, LocalDate> getMinMax(Commontags commontag) {


        if (commontag == null) {
            return getMinMax();
        }

        Pair<LocalDate, LocalDate> result = null;

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createQuery("SELECT t FROM Training t WHERE :commontag MEMBER OF t.commontags ORDER BY t.starting ASC ");
        queryMin.setParameter("commontag", commontag);
        queryMin.setMaxResults(1);

        Query queryMax = em.createQuery("SELECT t FROM Training t WHERE :commontag MEMBER OF t.commontags ORDER BY t.starting DESC ");
        queryMax.setParameter("commontag", commontag);
        queryMax.setMaxResults(1);

        try {
            ArrayList<Training> min = new ArrayList<Training>(queryMin.getResultList());
            ArrayList<Training> max = new ArrayList<Training>(queryMax.getResultList());
            if (min.isEmpty()) {
                result = null;
            } else {
                result = new Pair<LocalDate, LocalDate>(new LocalDate(min.get(0).getStarting()), new LocalDate(max.get(0).getStarting()));
            }

        } catch (Exception e) {
            OPDE.fatal(e);
        }

        em.close();
        return result;
    }


    private static ArrayList<Training> getAll() {
        ArrayList<Training> list = new ArrayList<>();

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createQuery("SELECT t FROM Training t ORDER BY t.starting DESC ");

        list.addAll(queryMin.getResultList());

        em.close();

        return list;

    }

    private static ArrayList<Training> getTrainings4(int iYear) {
        if (iYear <= 0) return getAll();

        ArrayList<Training> list = new ArrayList<>();

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createQuery("SELECT t FROM Training t WHERE t.starting >= :from AND t.starting <= :to ORDER BY t.starting DESC ");
        queryMin.setParameter("from", SYSCalendar.boy(iYear).toDate());
        queryMin.setParameter("to", SYSCalendar.eoy(iYear).toDate());

        list.addAll(queryMin.getResultList());

        em.close();

        return list;

    }

    public static ArrayList<Training> getTrainings4(int iYear, Commontags commontag) {
        if (commontag == null) return getTrainings4(iYear);


        ArrayList<Training> list = new ArrayList<>();

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createQuery("SELECT t FROM Training t WHERE :commontag MEMBER OF t.commontags AND t.starting >= :from AND t.starting <= :to ORDER BY t.starting DESC ");
        queryMin.setParameter("commontag", commontag);
        queryMin.setParameter("from", SYSCalendar.boy(iYear).toDate());
        queryMin.setParameter("to", SYSCalendar.eoy(iYear).toDate());

        list.addAll(queryMin.getResultList());

        em.close();

        return list;

    }

    public static String getAsHTML(List<Training> trainingList) {

        String html = SYSConst.html_h1("opde.training");

        for (Training training : trainingList) {

            boolean withTime = !new LocalDate(training.getStarting()).toDateTimeAtStartOfDay().toDate().equals(training.getStarting());
            DateTime starting = new DateTime(training.getStarting());

            html += SYSConst.html_h2(
                    training.getTitle() + ", [" +
                            (withTime ? starting.toString("EEE, dd.MM.yy  HH:mm") : starting.toString("EEE, dd.MM.yy")) +
                            "]"
            );

            html += (training.getCommontags().isEmpty() ? "" : " " + CommontagsTools.getAsHTML(training.getCommontags(), SYSConst.html_16x16_tagPurple) + "<br/>");

            html += SYSTools.xx(STATES[training.getState()]) + "<br/>";

            if (!SYSTools.catchNull(training.getDocent()).isEmpty()) {
                html += SYSTools.xx("opde.training.docent") + ": " + training.getDocent() + "<br/>";
            }

            if (!SYSTools.catchNull(training.getText()).isEmpty()) {
                html += SYSConst.html_paragraph(SYSTools.xx("misc.msg.details") + ": " + training.getText());
            }

            ArrayList<Training2Users> listDone = getDone(training);
            ArrayList<Training2Users> listRefused = getRefused(training);
            ArrayList<Training2Users> listOpen = getOpen(training);

            if (!listOpen.isEmpty()) {
                html += SYSConst.html_h3("opde.t2u.state.open");
            }
            html += Training2UsersTools.getAsHTML(listOpen);

            if (!listRefused.isEmpty()) {
                html += SYSConst.html_h3("opde.t2u.state.refused");
            }
            html += Training2UsersTools.getAsHTML(listRefused);

            if (!listDone.isEmpty()) {
                html += SYSConst.html_h3("opde.t2u.state.done");
            }
            html += Training2UsersTools.getAsHTML(listDone);

        }


        return SYSConst.html_div(html);
    }

    public static String getTraining2Attendees(int year) {

        String html = SYSConst.html_h1(SYSTools.xx("opde.controlling.staff.training") + " " + year);

        ArrayList<Users> listUsers = UsersTools.getUsers(false);
        ArrayList<Training> listTraining = getTrainings4(year);


        for (Users user : listUsers) {
//            boolean nothing = true;

            html += SYSConst.html_h2(user.getFullname());


            String list = "";
//            ArrayList<Training> listTrainingsForThisUser = new ArrayList<>();

            for (Training training : listTraining) {

                Training2Users match = Training2UsersTools.get4User(training.getAttendees(), user);

                if (match != null) {
                    boolean withTime = !new LocalDate(training.getStarting()).toDateTimeAtStartOfDay().toDate().equals(training.getStarting());
                    DateTime starting = new DateTime(training.getStarting());
                    list += SYSConst.html_li(Training2UsersTools.getHTMLIcon(match) + training.getTitle() + ", [" +
                            (withTime ? starting.toString("EEE, dd.MM.yy  HH:mm") : starting.toString("EEE, dd.MM.yy")) +
                            "]");
                }
            }

            if (list.isEmpty()) {
                html += SYSTools.xx("opde.controlling.staff.nothing.yet");
            } else {
                html += SYSConst.html_ul(list);
            }


        }


        return SYSConst.html_div(html);
    }


    public static ArrayList<Training2Users> getDone(Training training) {
        ArrayList<Training2Users> listResult = new ArrayList<>();

        for (Training2Users training2Users : training.getAttendees()) {
            if (training2Users.getState() == Training2UsersTools.STATE_DONE) {
                listResult.add(training2Users);
            }
        }

        Collections.sort(listResult);
        return listResult;
    }

    public static ArrayList<Training2Users> getRefused(Training training) {
        ArrayList<Training2Users> listResult = new ArrayList<>();

        for (Training2Users training2Users : training.getAttendees()) {
            if (training2Users.getState() == Training2UsersTools.STATE_REFUSED) {
                listResult.add(training2Users);
            }
        }

        Collections.sort(listResult);
        return listResult;
    }

    public static ArrayList<Training2Users> getOpen(Training training) {
        ArrayList<Training2Users> listResult = new ArrayList<>();

        for (Training2Users training2Users : training.getAttendees()) {
            if (training2Users.getState() == Training2UsersTools.STATE_OPEN) {
                listResult.add(training2Users);
            }
        }

        Collections.sort(listResult);
        return listResult;
    }

}
