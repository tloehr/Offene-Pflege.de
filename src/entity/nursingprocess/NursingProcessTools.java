package entity.nursingprocess;

import entity.EntityTools;
import entity.info.ResInfoCategory;
import entity.info.Resident;
import entity.info.ResidentTools;
import op.OPDE;
import op.care.nursingprocess.PnlNursingProcess;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 19.07.12
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class NursingProcessTools {
    public static final String UNIQUEID = "__plankenn";

    public static ArrayList<NursingProcess> getAll(Resident resident, ResInfoCategory cat) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT p FROM NursingProcess p WHERE p.resident = :resident AND p.category = :cat ORDER BY p.topic, p.from");
        query.setParameter("cat", cat);
        query.setParameter("resident", resident);
        ArrayList<NursingProcess> planungen = new ArrayList<NursingProcess>(query.getResultList());
        em.close();
        return planungen;
    }

    public static List<NursingProcess> getTemplates(String topic, boolean includeInactives) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT p FROM NursingProcess p WHERE p.topic like :topic " + (includeInactives ? "" : " AND p.to > :now ") + " ORDER BY p.topic, p.resident.rid, p.from");
        query.setParameter("topic", EntityTools.getMySQLsearchPattern(topic));
        if (!includeInactives) {
            query.setParameter("now", new Date());
        }
        List<NursingProcess> planungen = query.getResultList();
        em.close();
        return planungen;
    }

    public static ArrayList<NursingProcess> getAll(Resident resident) {
        long begin = System.currentTimeMillis();
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT p FROM NursingProcess p WHERE p.resident = :resident ");
        query.setParameter("resident", resident);
        ArrayList<NursingProcess> nursingProcesses = new ArrayList<NursingProcess>(query.getResultList());
        em.close();
        SYSTools.showTimeDifference(begin);
        return nursingProcesses;
    }

    public static ArrayList<NursingProcess> getAllActive(Resident resident) {
        long begin = System.currentTimeMillis();
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT p FROM NursingProcess p WHERE p.resident = :resident AND p.to = :ufn ORDER BY p.topic ");
        query.setParameter("ufn", SYSConst.DATE_UNTIL_FURTHER_NOTICE);
        query.setParameter("resident", resident);
        ArrayList<NursingProcess> nursingProcesses = new ArrayList<NursingProcess>(query.getResultList());
        em.close();
        SYSTools.showTimeDifference(begin);
        return nursingProcesses;
    }

    /**
     * Gibt einen String zurück, der eine HTML Darstellung einer Pflegeplanung enthält.
     *
     * @param np
     * @return
     */
    public static String getAsHTML(NursingProcess np, boolean withHeader, boolean withDetails, boolean withIcon) {
        String html = SYSConst.html_h2((withHeader ? OPDE.lang.getString(PnlNursingProcess.internalClassID) + " " + OPDE.lang.getString("misc.msg.for") + " (" + ResidentTools.getTextCompact(np.getResident()) + ")" : "") + "&nbsp;&raquo;" + np.getTopic() + "&laquo");

        html += withIcon && np.isClosed() ? SYSConst.html_22x22_StopSign : "";

        html += "<div id=\"fonttext\">";

        html += withHeader ? "<b>" + OPDE.lang.getString("misc.msg.category") + ":</b> " + np.getCategory().getText() + "<br/>" : "";

        DateFormat df = DateFormat.getDateInstance();
        if (!np.isClosed()) {
            html += "<b>" + OPDE.lang.getString(PnlNursingProcess.internalClassID + ".pnleval.nextevaldate") + ":</b> " + df.format(np.getNextEval()) + "<br/>";
        }

        if (withDetails) {
            html += SYSConst.html_bold("misc.msg.createdby") + ": " + np.getUserON().getFullname() + " ";
            html += SYSConst.html_bold("misc.msg.atchrono") + ": " + df.format(np.getFrom());
            if (np.isClosed()) {
                html += "<br/>";
                html += SYSConst.html_bold("misc.msg.closedBy") + ": " + np.getUserOFF().getFullname() + " ";
                html += SYSConst.html_bold("misc.msg.atchrono") + ": " + df.format(np.getTo());
            }
        }

        html += SYSConst.html_h3("misc.msg.Situation") +
                SYSTools.replace(np.getSituation(), "\n", "<br/>", false);

        html += SYSConst.html_h3("misc.msg.Goal[s]") +
                SYSTools.replace(np.getGoal(), "\n", "<br/>", false);

        html += SYSConst.html_h3(PnlNursingProcess.internalClassID + ".interventions");

        if (np.getInterventionSchedule().isEmpty()) {
            html += "<ul><li><b>" + OPDE.lang.getString("misc.msg.MissingInterventions") + " !!!</b></li></ul>";
        } else {
            html += "<ul>";
            for (InterventionSchedule interventionSchedule : np.getInterventionSchedule()) {
                html += "<li>";
                html += "<div id=\"fonttext\"><b>" + interventionSchedule.getIntervention().getBezeichnung() + "</b> (" + interventionSchedule.getDauer().toPlainString() + " " + OPDE.lang.getString("misc.msg.Minutes") + ")</div>";
                html += InterventionScheduleTools.getTerminAsHTML(interventionSchedule);
                html += "</li>";
            }
            html += "</ul>";
        }


        if (!np.getEvaluations().isEmpty()) {
            html += SYSConst.html_h3(OPDE.lang.getString("misc.msg.DateOfEvals"));
            html += "<ul>";
            for (NPControl npControl : np.getEvaluations()) {
                html += "<li><div id=\"fonttext\">" + NPControlTools.getAsHTML(npControl) + "</div></li>";
            }
            html += "</ul>";
        }

        html += "</div>";
        return html;
    }

    public static void closeAll(EntityManager em, Resident resident, Date enddate) throws Exception {
        Query query = em.createQuery("SELECT np FROM NursingProcess np WHERE np.resident = :resident AND np.to >= :now ");
        query.setParameter("resident", resident);
        query.setParameter("now", enddate);
        List<NursingProcess> nursingProcesses = query.getResultList();

        for (NursingProcess np : nursingProcesses) {
            NursingProcess mynp = em.merge(np);
            em.lock(mynp, LockModeType.OPTIMISTIC);
            mynp.setTo(enddate);
            mynp.setUserOFF(em.merge(OPDE.getLogin().getUser()));
        }
    }

//    public static void close(EntityManager em, NursingProcess np, String closingText) {
//
//
//
//    }
}
