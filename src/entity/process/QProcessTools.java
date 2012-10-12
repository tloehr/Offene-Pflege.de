/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.process;

import entity.info.ResInfo;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.nursingprocess.NursingProcess;
import entity.prescription.Prescription;
import entity.reports.NReport;
import entity.system.Users;
import entity.values.ResValue;
import op.OPDE;
import op.process.PnlProcess;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.awt.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author tloehr
 */
public class QProcessTools {

    public static Color getBG1(QProcess qProcess) {
        Color common = SYSTools.getColor("CEF0FF");
        Color resident = SYSTools.getColor("DFB0FF");
        Color closed = SYSTools.getColor("C0C0C0");
        if (qProcess.isClosed()) {
            return closed;
        }

        if (qProcess.isCommon()) {
            return common;
        }
        return resident;
    }

    public static Color getBG2(QProcess qProcess) {
        Color common = SYSTools.getColor("D9F3FF");
        Color resident = SYSTools.getColor("F9EEFF");
        Color closed = SYSTools.getColor("E8E8E8");
        if (qProcess.isClosed()) {
            return closed;
        }

        if (qProcess.isCommon()) {
            return common;
        }
        return resident;
    }

    public static void removeElementFromProcess(EntityManager em, QProcessElement element, QProcess qProcess) {
        String elementBezeichnung = "";
        Query query = null;
        if (element instanceof NReport) {
            query = em.createQuery("SELECT s FROM SYSNR2PROCESS s WHERE s.nreport = :element AND s.qProcess = :process AND s.qProcess.to = '9999-12-31 23:59:59'");
            elementBezeichnung = "Pflegebericht";
        } else if (element instanceof ResValue) {
            query = em.createQuery("SELECT s FROM SYSVAL2PROCESS s WHERE s.resValue = :element AND s.vorgang = :process AND s.vorgang.to = '9999-12-31 23:59:59'");
            elementBezeichnung = "Bewohner Wert";
        } else if (element instanceof Prescription) {
            query = em.createQuery("SELECT s FROM SYSPRE2PROCESS s WHERE s.prescription = :element AND s.qProcess = :process AND s.qProcess.to = '9999-12-31 23:59:59'");
            elementBezeichnung = "Ärztliche Verordnung";
        } else if (element instanceof ResInfo) {
            query = em.createQuery("SELECT s FROM SYSINF2PROCESS s WHERE s.bwinfo = :element AND s.vorgang = :process AND s.vorgang.to = '9999-12-31 23:59:59'");
            elementBezeichnung = "Bewohner Information";
        } else if (element instanceof NursingProcess) {
            query = em.createQuery("SELECT s FROM SYSNP2PROCESS s WHERE s.nursingProcess = :element AND s.vorgang = :process AND s.vorgang.to = '9999-12-31 23:59:59'");
            elementBezeichnung = "Pflegeplanung";
        } else {

        }

        query.setParameter("element", element);
        query.setParameter("process", qProcess);

        QProcessElement connectionObject = (QProcessElement) query.getSingleResult();
        em.remove(connectionObject);
        qProcess.removeElement(element);

        qProcess.getPReports().add(em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + elementBezeichnung + " ID: " + element.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, qProcess)));

    }

    public static String getAsHTML(QProcess qProcess) {
        String html = "";
        html += "<h2  id=\"fonth2\" >" + qProcess.getTitle() + "</h2>";

        html += "<div id=\"fonttext\" >";

        if (qProcess.getResident() != null) {
            html += "<br/>" + OPDE.lang.getString(PnlProcess.internalClassID + ".belongsto") + ": <b>" + ResidentTools.getLabelText(qProcess.getResident()) + "</b><br/>";
        } else {
            html += "<br/>" + OPDE.lang.getString(PnlProcess.internalClassID + ".commonprocess") + "<br/>";
        }
        html += "<b>" + OPDE.lang.getString("misc.msg.from") + ":</b> " + DateFormat.getDateInstance().format(qProcess.getFrom());
        if (qProcess.isClosed()) {
            html += "&nbsp;&nbsp;<b>" + OPDE.lang.getString("misc.msg.to") + ":</b> " + DateFormat.getDateInstance().format(qProcess.getTo());
        }

        DateMidnight revision = new DateMidnight(qProcess.getRevision());
        if (revision.isAfterNow()) {
            int daysBetween = Days.daysBetween(new DateTime(), revision).getDays();

            if (daysBetween > 7) {
                html += "<font " + SYSConst.html_darkgreen + ">";
            } else if (daysBetween == 0) {
                html += "<font " + SYSConst.html_gold7 + ">";
            } else {
                html += "<font " + SYSConst.html_darkorange + ">";
            }
        } else {
            html += "<font " + SYSConst.html_darkred + ">";
        }
        html += "&nbsp;&nbsp;<b>" + OPDE.lang.getString(PnlProcess.internalClassID + ".revision") + ":</b> ";
        html += DateFormat.getDateInstance().format(qProcess.getRevision()) + "</font>";
        html += "<br/><b>" + OPDE.lang.getString(PnlProcess.internalClassID + ".createdby") + ":</b> " + qProcess.getCreator().getFullname();
        html += "&nbsp;&nbsp;<b>" + OPDE.lang.getString(PnlProcess.internalClassID + ".ownedby") + ":</b> " + qProcess.getOwner().getFullname();

        html += "</div>";
        return html;
    }

    public static String getElementsAsHTML(QProcess qProcess, boolean includeSystemReports) {
        String html = "";
        DateFormat df = DateFormat.getDateTimeInstance();
        html += "<h2  id=\"fonth2\" >" + OPDE.lang.getString(PnlProcess.internalClassID + ".elementlist") + "</h2>";
        html += "<table  id=\"fonttext\" border=\"1\"><tr>" +
                "<th>" + OPDE.lang.getString("misc.msg.Date") + "</th><th>" + OPDE.lang.getString("misc.msg.content") + "</th></tr>";

        for (QProcessElement element : qProcess.getElements()) {
            if (includeSystemReports || !(element instanceof PReport) || !((PReport) element).isSystem()) {
                html += "<tr >";

                html += "<td valign=\"top\">" + df.format(new Date(element.getPITInMillis())) + "</td>";
                html += "<td valign=\"top\">" + element.getContentAsHTML() + "</td>";
                html += "</tr>";
            }
        }

        html += "</table>";
        return html;
    }


    public static List<QProcess> getProcessesRunningOutIn(int days) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT qp FROM QProcess qp WHERE qp.to = :tfn AND qp.revision <= :revisiondate ");
        query.setParameter("tfn", SYSConst.DATE_BIS_AUF_WEITERES);
        query.setParameter("revisiondate", new DateMidnight().plusDays(days + 1).toDateTime().minusSeconds(1).toDate());
        ArrayList<QProcess> list = new ArrayList<QProcess>(query.getResultList());
        em.close();
        return list;
    }

    public static List<QProcess> getAllActive() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT qp FROM QProcess qp WHERE qp.to = :tfn ");
        query.setParameter("tfn", SYSConst.DATE_BIS_AUF_WEITERES);
        ArrayList<QProcess> list = new ArrayList<QProcess>(query.getResultList());
        em.close();
        return list;
    }

    public static List<QProcess> getProcesses4(PCat pcat) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT qp FROM QProcess qp WHERE qp.pcat = :pcat");
        query.setParameter("pcat", pcat);
        ArrayList<QProcess> list = new ArrayList<QProcess>(query.getResultList());
        em.close();
        return list;
    }

    public static List<QProcess> getProcesses4(Resident resident) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT qp FROM QProcess qp WHERE qp.resident = :resident");
        query.setParameter("resident", resident);
        ArrayList<QProcess> list = new ArrayList<QProcess>(query.getResultList());
        em.close();
        return list;
    }

    public static List<QProcess> getActiveProcesses4(Resident resident) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT qp FROM QProcess qp WHERE qp.to = :tfn AND qp.resident = :resident");
        query.setParameter("resident", resident);
        query.setParameter("tfn", SYSConst.DATE_BIS_AUF_WEITERES);
        ArrayList<QProcess> list = new ArrayList<QProcess>(query.getResultList());
        em.close();
        return list;
    }

    public static List<QProcess> getProcesses4(Users owner) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT qp FROM QProcess qp WHERE qp.owner = :owner");
        query.setParameter("owner", owner);
        ArrayList<QProcess> list = new ArrayList<QProcess>(query.getResultList());
        em.close();
        return list;
    }

    public static void closeAll(EntityManager em, Resident resident, Date enddate) throws Exception {
        Query query = em.createQuery("SELECT qp FROM QProcess qp WHERE qp.resident = :resident AND qp.to >= :now");
        query.setParameter("resident", resident);
        query.setParameter("now", enddate);
        List<QProcess> qProcesses = query.getResultList();

        for (QProcess qp : qProcesses) {
            QProcess myProcess = em.merge(qp);
            em.lock(myProcess, LockModeType.OPTIMISTIC);
            PReport pReport = em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_CLOSE), PReportTools.PREPORT_TYPE_CLOSE, myProcess));
            myProcess.setTo(enddate);
            myProcess.getPReports().add(pReport);
        }
    }


}
