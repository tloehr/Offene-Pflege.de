package de.offene_pflege.entity.nursingprocess;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.info.ResInfoCategory;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA. User: tloehr Date: 19.07.12 Time: 15:50 To change this template use File | Settings | File
 * Templates.
 */
public class NursingProcessTools {

    public static final String UNIQUEID = "__plankenn";
    public static final int MAXNumOfEvals = 4;

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

    public static List<NursingProcess> getTemplates(String topic, boolean includeInactives, boolean searchResidents) {
        EntityManager em = OPDE.createEM();
        // Suche nach Topics
        Query query = em.createQuery("SELECT p FROM NursingProcess p WHERE p.topic like :topic " + (includeInactives ? "" : " AND p.to > :now ") + " ORDER BY p.topic, p.resident.id, p.from");
        query.setParameter("topic", EntityTools.getMySQLsearchPattern(topic));
        if (!includeInactives) {
            query.setParameter("now", new Date());
        }
        HashSet<NursingProcess> planungen = new HashSet<>(query.getResultList());

        if (searchResidents) {
            // Suche nach BW Namen
            Query query1 = em.createQuery("SELECT p FROM NursingProcess p WHERE p.resident.name like :resname " + (includeInactives ? "" : " AND p.to > :now ") + " ORDER BY p.topic, p.resident.id, p.from");
            query1.setParameter("resname", EntityTools.getMySQLsearchPattern(topic));
            if (!includeInactives) {
                query1.setParameter("now", new Date());
            }
            planungen.addAll(query1.getResultList());
        }
        em.close();

        // Nun noch sortieren
        return planungen.stream()
                .sorted(Comparator.comparing(NursingProcess::getTitle)
                        .thenComparing(NursingProcess::getResident)
                        .thenComparing(NursingProcess::getFrom))
                .collect(Collectors.toList());
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
                html += InterventionScheduleTools.getTerminAsHTML(interventionSchedule);
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
            // just in case, somebody added an info AFTER the resident moved out / died. We fix that start date here
            mynp.setFrom(SYSCalendar.min(mynp.getFrom(), enddate));
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
