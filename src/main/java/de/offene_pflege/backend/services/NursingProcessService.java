package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.EntityTools;
import de.offene_pflege.backend.entity.done.Resident;
import de.offene_pflege.backend.entity.info.ResInfoCategory;
import de.offene_pflege.backend.entity.nursingprocess.*;
import de.offene_pflege.backend.entity.system.Commontags;
import de.offene_pflege.backend.entity.system.OPUsers;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.JavaTimeConverter;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: tloehr Date: 19.07.12 Time: 15:50 To change this template use File | Settings | File
 * Templates.
 */
public class NursingProcessService {

    public static final String UNIQUEID = "__plankenn";
    public static final int MAXNumOfEvals = 4;

    public static NursingProcess create(Resident resident, OPUsers opUsers) {
        NursingProcess nursingProcess = new NursingProcess();
        nursingProcess.setResident(resident);
        nursingProcess.setUserON(opUsers);
        nursingProcess.setInterventionSchedules(new ArrayList<>());
        nursingProcess.setAttachedFilesConnections(new ArrayList<>());
        nursingProcess.setAttachedQProcessConnections(new ArrayList<>());
        nursingProcess.setNpControls(new ArrayList<>());
        nursingProcess.setFrom(new Date());
        nursingProcess.setTo(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
        nursingProcess.setNextEval(JavaTimeConverter.toDate(java.time.LocalDateTime.now().plusWeeks(4)));
        nursingProcess.setCommontags(new ArrayList<>());
        nursingProcess.setNpseries(-1l);
        return nursingProcess;
    }

    public NursingProcess create(NursingProcess source) {
        NursingProcess nursingProcess = new NursingProcess();
        nursingProcess.setTopic(source.getTopic());
        nursingProcess.setSituation(source.getSituation());
        nursingProcess.setGoal(source.getGoal());
        nursingProcess.setFrom(source.getFrom());
        nursingProcess.setTo(source.getTo());
        nursingProcess.setNpseries(source.getNpseries());
        nursingProcess.setNextEval(source.getNextEval());
        nursingProcess.setUserON(source.getUserON());
        nursingProcess.setUserOFF(source.getUserOFF());
        nursingProcess.setResident(source.getResident());
        nursingProcess.setCategory(source.getCategory());
        nursingProcess.setInterventionSchedules(new ArrayList<>());
        nursingProcess.setAttachedFilesConnections(new ArrayList<>());
        nursingProcess.setAttachedQProcessConnections(new ArrayList<>());
        nursingProcess.setCommontags(new ArrayList<>());
        return nursingProcess;

        source.getInterventionSchedules().forEach(is -> {
            InterventionSchedule myIS = is.clone();
            myIS.setNursingProcess(nursingProcess);
            nursingProcess.getInterventionSchedules().add(myIS);
        });

        for (Commontags ctag : commontags) {
            myNewNP.getCommontags().add(ctag);
        }
        return myNewNP;
    }



    public static String getPitAsHTML(NursingProcess nursingProcess) {
        String result = "";
        DateFormat df = DateFormat.getDateInstance();

        if (isClosed(this)) {

            result += "<table id=\"fonttext\" border=\"0\" cellspacing=\"0\">";
            result += "<tr>";
            result += "<td valign=\"top\">" + df.format(nursingProcess.getFrom()) + "</td>";
            result += "<td valign=\"top\">&raquo;</td>";
            result += "<td valign=\"top\">" + df.format(nursingProcess.getTo()) + "</td>";
            result += "</tr>\n";
            result += "<tr>";
            result += "<td valign=\"top\">" + OPUsersService.getFullname(nursingProcess.getUserON()) + "</td>";
            result += "<td valign=\"top\">&raquo;</td>";
            result += "<td valign=\"top\">" + OPUsersService.getFullname(nursingProcess.getUserOFF()) + "</td>";
            result += "</tr>\n";
            result += "</table>\n";

        } else {
            result += df.format(nursingProcess.getFrom()) + "&nbsp;&raquo;&raquo;" +
                    "<br/>" +
                    OPUsersService.getFullname(nursingProcess.getUserON());
        }
        result += "<br/>[" + nursingProcess.getId() + "]";

        return result;
    }


    public static boolean isClosed(NursingProcess nursingProcess) {
        return nursingProcess.getFrom().before(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
    }

    public static ArrayList<NursingProcess> getAll(Resident resident, ResInfoCategory cat) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT p FROM NursingProcess p WHERE p.resident = :resident AND p.category = :cat ORDER BY p.topic, p.from");
        query.setParameter("cat", cat);
        query.setParameter("resident", resident);
        ArrayList<NursingProcess> planungen = new ArrayList<NursingProcess>(query.getResultList());
        em.close();
        return planungen;
    }

    public static ArrayList<NursingProcess> getAll(Resident resident, ResInfoCategory cat, LocalDate from, LocalDate to) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT p FROM NursingProcess p WHERE p.resident = :resident AND p.category = :cat " +
                " AND ((p.from <= :from AND p.to >= :from) OR " +
                " (p.from <= :to AND p.to >= :to) OR " +
                " (p.from > :from AND p.to < :to)) " +
                " ORDER BY p.topic, p.from");
        query.setParameter("cat", cat);
        query.setParameter("resident", resident);
        query.setParameter("from", from.toDateTimeAtStartOfDay().toDate());
        query.setParameter("to", SYSCalendar.eod(to).toDate());
        ArrayList<NursingProcess> planungen = new ArrayList<NursingProcess>(query.getResultList());
        em.close();
        return planungen;
    }

    public static ArrayList<NursingProcess> getAll(int type, LocalDate from, LocalDate to) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("" +
                " SELECT DISTINCT p FROM NursingProcess p " +
                " JOIN p.commontags ct " +
                " WHERE ((p.from <= :from AND p.to >= :from) OR " +
                " (p.from <= :to AND p.to >= :to) OR " +
                " (p.from > :from AND p.to < :to)) " +
                " AND ct.type = :type ");
        query.setParameter("from", from.toDateTimeAtStartOfDay().toDate());
        query.setParameter("to", SYSCalendar.eod(to).toDate());
        query.setParameter("type", type);
        ArrayList<NursingProcess> planungen = new ArrayList<NursingProcess>(query.getResultList());
        em.close();
        return planungen;
    }

    public static List<NursingProcess> getTemplates(String topic, boolean includeInactives) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT p FROM NursingProcess p WHERE p.topic like :topic " + (includeInactives ? "" : " AND p.to > :now ") + " ORDER BY p.topic, p.resident.id, p.from");
        query.setParameter("topic", EntityTools.getMySQLsearchPattern(topic));
        if (!includeInactives) {
            query.setParameter("now", new Date());
        }
        List<NursingProcess> planungen = query.getResultList();
        em.close();
        return planungen;
    }

    public static ArrayList<NursingProcess> getAll(Resident resident) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT p FROM NursingProcess p WHERE p.resident = :resident ");
        query.setParameter("resident", resident);
        ArrayList<NursingProcess> nursingProcesses = new ArrayList<NursingProcess>(query.getResultList());
        em.close();
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
    public static String getAsHTML(NursingProcess np, boolean withHeader, boolean withDetails, boolean withIcon, boolean showAllEvals) {
        String html = "";// : "";

        if (withHeader) {
            html += SYSConst.html_h2(SYSTools.xx("nursingrecords.nursingprocess") + " " + SYSTools.xx("misc.msg.for") + " (" + ResidentTools.getTextCompact(np.getResident()) + ")");
            html += "<br/>&raquo;" + np.getTopic() + "&laquo";
        }

        html += withIcon && np.isClosed() ? SYSConst.html_22x22_StopSign : "";

        html += "<div id=\"fonttext\">";

        html += withHeader ? "<b>" + SYSTools.xx("misc.msg.category") + ":</b> " + np.getCategory().getText() + "<br/>" : "";

        DateFormat df = DateFormat.getDateInstance();
        if (!np.isClosed()) {
            html += "<b>" + SYSTools.xx("nursingrecords.nursingprocess.pnleval.nextevaldate") + ":</b> " + df.format(np.getNextEval()) + "<br/>";
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

        html += SYSConst.html_h3("nursingrecords.nursingprocess.interventions");

        if (np.getInterventionSchedule().isEmpty()) {
            html += "<ul><li><b>" + SYSTools.xx("misc.msg.MissingInterventions") + " !!!</b></li></ul>";
        } else {
            html += "<ul>";
            for (InterventionSchedule interventionSchedule : np.getInterventionSchedule()) {
                html += "<li>";
                html += "<div id=\"fonttext\"><b>" + interventionSchedule.getIntervention().getBezeichnung() + "</b></div>";
                html += InterventionScheduleService.getTerminAsHTML(interventionSchedule);
                html += "</li>";
            }
            html += "</ul>";
        }

        if (!np.getEvaluations().isEmpty()) {
            html += SYSConst.html_h3(SYSTools.xx("misc.msg.DateOfEvals"));
            html += "<ul>";
            int numEvals = 0;
            Collections.sort(np.getEvaluations());
            for (NPControl npControl : np.getEvaluations()) {
                numEvals++;
                html += "<li><div id=\"fonttext\">" + NPControlTools.getAsHTML(npControl) + "</div></li>";
                if (!showAllEvals && np.getEvaluations().size() > MAXNumOfEvals && numEvals >= MAXNumOfEvals) {
                    html += "<li>" + SYSConst.html_italic((np.getEvaluations().size() - numEvals) + " " + SYSTools.xx("misc.msg.moreToShow")) + " </li>";
                    break;
                }
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
