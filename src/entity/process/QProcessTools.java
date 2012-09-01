/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.process;

import entity.BWerte;
import entity.system.Users;
import entity.info.BWInfo;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.nursingprocess.NursingProcess;
import entity.prescription.Prescriptions;
import entity.reports.NReport;
import op.OPDE;
import op.process.PnlProcess;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.persistence.EntityManager;
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
            query = em.createNamedQuery("SYSNR2PROCESS.findByElementAndVorgang");
            elementBezeichnung = "Pflegebericht";
        } else if (element instanceof BWerte) {
            query = em.createNamedQuery("SYSVAL2PROCESS.findByElementAndVorgang");
            elementBezeichnung = "Bewohner Wert";
        } else if (element instanceof Prescriptions) {
            query = em.createNamedQuery("SYSPRE2PROCESS.findByElementAndVorgang");
            elementBezeichnung = "Ärztliche Verordnung";
        } else if (element instanceof BWInfo) {
            query = em.createNamedQuery("SYSINF2PROCESS.findByElementAndVorgang");
            elementBezeichnung = "Bewohner Information";
        } else if (element instanceof NursingProcess) {
            query = em.createNamedQuery("SYSNP2PROCESS.findByElementAndVorgang");
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

//    private static JMenu getNeuMenu(QProcessElement elementQ, Resident bewohner) {
//        JMenu neu = new JMenu("Neu erstellen");
//        final JTextField txt = new JTextField("");
//        final Resident bw = bewohner;
//        final QProcessElement finalElementQ = elementQ;
//        neu.add(txt);
//        EntityManager em = OPDE.createEM();
//        Query query = em.createNamedQuery("VKat.findAllSorted");
//
//        Iterator<PCat> it = query.getResultList().iterator();
//        while (it.hasNext()) {
//            final PCat kat = it.next();
//            JMenuItem mi = new JMenuItem(kat.getText());
//            mi.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    if (!txt.getText().trim().isEmpty()) {
//                        QProcess vorgang = createVorgang(txt.getText(), kat, bw);
//                        add(finalElementQ, vorgang);
//                        OPDE.debug("Vorgang '" + vorgang.getTitle() + "' für Bewohner '" + bw.getBWKennung() + "' angelegt. Element mit ID " + finalElementQ.getGID() + " zugeordnet.");
//                    }
//                }
//            });
//            neu.add(mi);
//        }
//
//        em.close();
//
//        return neu;
//    }

    /**
     * Erstellt ein JMenu, dass zu einem bestimmten QProcessElement <code>elementQ</code> und für einen bestimmten Bewohner <code>bewohner</code> alle Vorgänge enthält,
     * zu dem dieses Element <b>noch nicht</b> zuegordnet ist.
     *
     * @param elementQ
     * @param bewohner
     * @return
     */
//    private static JMenu getVorgaenge2Assign(QProcessElement elementQ, Resident bewohner, ActionListener callback) {
//        JMenu result = new JMenu("Zuordnen zu");
//        EntityManager em = OPDE.createEM();
//        final ActionListener cb = callback;
//
//        final QProcessElement finalElementQ = elementQ;
//
//        // 1. Alle Vorgänge für den betreffenden BW suchen und in die Liste packen.
//        List<QProcess> proceses = new ArrayList();
//        Query query;
//        query = em.createNamedQuery("Vorgaenge.findActiveByBewohner");
//        query.setParameter("bewohner", bewohner);
//        proceses.addAll(query.getResultList());
//
//
//        // 2. Alle die Vorgänge entfernen, zu denen das betreffenden Object bereits zugeordnet wurde.
//        Query complement = null;
//        if (elementQ instanceof NReport) {
//            complement = em.createNamedQuery("SYSPB2VORGANG.findActiveAssignedVorgaengeByElement");
//        } else if (elementQ instanceof BWerte) {
//            complement = em.createNamedQuery("SYSBWerte2VORGANG.findActiveAssignedVorgaengeByElement");
//        } else if (elementQ instanceof Prescriptions) {
//            complement = em.createNamedQuery("SYSVER2VORGANG.findActiveAssignedVorgaengeByElement");
//        } else if (elementQ instanceof BWInfo) {
//            complement = em.createNamedQuery("SYSBWI2VORGANG.findActiveAssignedVorgaengeByElement");
//        } else if (elementQ instanceof NursingProcess) {
//            complement = em.createNamedQuery("SYSPLAN2VORGANG.findActiveAssignedVorgaengeByElement");
//        } else {
//            complement = null;
//        }
//        complement.setParameter("element", elementQ);
//        proceses.removeAll(complement.getResultList());
//
//        // 3. Nun alle Vorgänge als JMenuItems anhängen.
//        Iterator<QProcess> it = proceses.iterator();
//        while (it.hasNext()) {
//            final QProcess vorgang = it.next();
//            JMenuItem mi = new JMenuItem(vorgang.getTitle());
//            // Bei Aufruf eines Menüs, wird dass Element an den Vorgang angehangen.
//            mi.addActionListener(new java.awt.event.ActionListener() {
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    add(finalElementQ, vorgang);
//                    cb.actionPerformed(new ActionEvent(this, 0, "VorgangAssign"));
//                }
//            });
//            result.add(mi);
//        }
//        em.close();
//        return result;
//    }
//
//    private static JMenu getVorgaenge2Remove(QProcessElement elementQ, ActionListener callback) {
//
//        JMenu result = new JMenu("Entfernen von");
//
//        final ActionListener cb = callback;
//
//        final QProcessElement finalElementQ = elementQ;
//        EntityManager em = OPDE.createEM();
//
//        // 1. Alle aktiven Vorgänge suchen, die diesem Element zugeordnet sind.
//        List<QProcess> proceses = new ArrayList();
//        Query query = null;
//        if (elementQ instanceof NReport) {
//            query = em.createNamedQuery("SYSPB2VORGANG.findActiveAssignedVorgaengeByElement");
//        } else if (elementQ instanceof BWerte) {
//            query = em.createNamedQuery("SYSBWerte2VORGANG.findActiveAssignedVorgaengeByElement");
//        } else if (elementQ instanceof Prescriptions) {
//            query = em.createNamedQuery("SYSVER2VORGANG.findActiveAssignedVorgaengeByElement");
//        } else if (elementQ instanceof BWInfo) {
//            query = em.createNamedQuery("SYSBWI2VORGANG.findActiveAssignedVorgaengeByElement");
//        } else if (elementQ instanceof NursingProcess) {
//            query = em.createNamedQuery("SYSPLAN2VORGANG.findActiveAssignedVorgaengeByElement");
//        } else {
//            query = null;
//        }
//        query.setParameter("element", elementQ);
//        proceses.addAll(query.getResultList());
//
//        // 2. Nun diese Vorgänge als JMenuItems anhängen.
//        Iterator<QProcess> it = proceses.iterator();
//        while (it.hasNext()) {
//            final QProcess vorgang = it.next();
//            JMenuItem mi = new JMenuItem(vorgang.getTitle());
//            // Bei Aufruf eines Menüs, wird dass Element vom Vorgang entfernt
//            mi.addActionListener(new java.awt.event.ActionListener() {
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    remove(finalElementQ, vorgang);
//                    cb.actionPerformed(new ActionEvent(this, 0, "VorgangRemove"));
//                }
//            });
//            result.add(mi);
//        }
//
//        em.close();
//        return result;
//    }

//
//    public static JMenu getVorgangContextMenu(Frame parent, QProcessElement elementQ, Resident bewohner, ActionListener callback) {
//        JMenu menu = new JMenu("<html>Vorgänge <font color=\"red\">&#9679;</font></html>");
//
//        // Neuer Vorgang Menü
//        menu.add(getNeuMenu(elementQ, bewohner));
//
//        // Untermenü mit vorhandenen Vorgängen einblenden.
//        // Aber nur, wenn die nicht leer sind.
//        JMenu addMenu = getVorgaenge2Assign(elementQ, bewohner, callback);
//        if (addMenu.getMenuComponentCount() > 0) {
//            menu.add(addMenu);
//        }
//        JMenu delMenu = getVorgaenge2Remove(elementQ, callback);
//        if (delMenu.getMenuComponentCount() > 0) {
//            menu.add(delMenu);
//        }
//
//        return menu;
//    }


}
