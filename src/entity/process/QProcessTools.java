/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.process;

import entity.BWerte;
import entity.Users;
import entity.info.BWInfo;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.nursingprocess.NursingProcess;
import entity.prescription.Prescriptions;
import entity.reports.NReport;
import op.OPDE;
import op.process.PnlProcess;
import op.tools.DlgException;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

/**
 * @author tloehr
 */
public class QProcessTools {


    /**
     * Sucht alle Elemente für einen bestimmten Vorgang raus und gibt diesen als Liste von Elementen zurück.
     * Bei dieser Liste muss man beachten, dass sie aus zwei Arten von Elementen bestehen kann.
     * <ul>
     * <li>Entweder die Liste enthält einen (oder mehrere) Vorgangsberichte. Diese sind direkt per 1:n Relation mit den Vorgängen verbunden.</li>
     * <li>Oder es sind kleine Objekt Arrays (2-wertig), bei dem das erste Elemen (Index 0) das entsprechende QProcessElement (von welcher Art auch immer) enthält und das
     * zweite Element ist ein short, der den jeweiligen PDCA Zyklus enthält. Das liegt daran, dass die Zurordnungen der verschiedenen Dokumentationselemente über eine
     * attributierte m:n Relation erfolgt. Irgendwoher muss diese Information ja kommen.</li>
     * Daher muss in allen Dingen dieser Aufbau berücksichtigt werden. Das sieht schon bei dem elementsComparator.
     * </ul>
     *
     * @param vorgang
     * @return
     */
    public static List findElementeByVorgang(QProcess vorgang, boolean mitSystem) {
        EntityManager em = OPDE.createEM();
        Comparator<Object> elementsComparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {

                long l1;
                if (o1 instanceof Object[]) {
                    l1 = ((QProcessElement) ((Object[]) o1)[0]).getPITInMillis();
                } else {
                    l1 = ((QProcessElement) o1).getPITInMillis();
                }


                long l2;
                if (o2 instanceof Object[]) {
                    l2 = ((QProcessElement) ((Object[]) o2)[0]).getPITInMillis();
                } else {
                    l2 = ((QProcessElement) o2).getPITInMillis();
                }

                return new Long(l1).compareTo(l2);
            }
        };

        List elements = new ArrayList();
        Query query;

        query = em.createNamedQuery(mitSystem ? "PReport.findByVorgang" : "PReport.findByVorgangOhneSystem");
        query.setParameter("vorgang", vorgang);
        elements.addAll(query.getResultList());

        query = em.createNamedQuery("Pflegeberichte.findByVorgang");
        query.setParameter("vorgang", vorgang);
        elements.addAll(query.getResultList());

        query = em.createNamedQuery("BWerte.findByVorgang");
        query.setParameter("process", vorgang);
        elements.addAll(query.getResultList());

        query = em.createNamedQuery("Verordnung.findByVorgang");
        query.setParameter("vorgang", vorgang);
        elements.addAll(query.getResultList());

        query = em.createNamedQuery("BWInfo.findByVorgang");
        query.setParameter("vorgang", vorgang);
        elements.addAll(query.getResultList());

        query = em.createNamedQuery("Planung.findByVorgang");
        query.setParameter("vorgang", vorgang);
        elements.addAll(query.getResultList());

        Collections.sort(elements, elementsComparator);

        em.close();
        return elements;
    }


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

//    public static void endVorgang(QProcess vorgang) {
//        EntityManager em = OPDE.createEM();
//        try {
//            em.getTransaction().begin();
//            PReport systemBericht = new PReport("Vorgang abgeschlossen", PReportTools.PREPORT_TYPE_CLOSE, vorgang);
//            em.persist(systemBericht);
//            vorgang.setTo(new Date());
//            em.merge(vorgang);
//            em.getTransaction().commit();
//        } catch (Exception e) {
//            em.getTransaction().rollback();
//            OPDE.fatal(e);
//        } finally {
//            em.close();
//        }
//    }

    public static void reopenVorgang(QProcess vorgang) {
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            PReport systemBericht = new PReport("Vorgang wieder geöffnet", PReportTools.PREPORT_TYPE_REOPEN, vorgang);
            em.persist(systemBericht);
            vorgang.setTo(SYSConst.DATE_BIS_AUF_WEITERES);
            em.merge(vorgang);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            new DlgException(ex);
        } finally {
            em.close();
        }
    }

//    public static QProcess createVorgang(String title, PCat vkat, Resident bw) {
//        QProcess vorgang = new QProcess(title, bw, vkat);
//        PReport vbericht = new PReport("Neuen Vorgang erstellt.", PReportTools.PREPORT_TYPE_CREATE, vorgang);
//        EntityManager em = OPDE.createEM();
//        try {
//            em.getTransaction().begin();
//            em.persist(vorgang);
//            em.persist(vbericht);
//            em.getTransaction().commit();
//        } catch (Exception e) {
//            em.getTransaction().rollback();
//        } finally {
//            em.close();
//        }
//        return vorgang;
//    }

    /**
     * Hängt ein QProcessElement an einen Vorgang an und erstellt einen entsprechenden SystemBericht dazu.
     *
     * @param elementQ
     * @param vorgang
     */
    public static void add(QProcessElement elementQ, QProcess vorgang) {

        //TODO: Das hier muss völlig überarbeitet werden. Die Position muss anders ermittelt werden.

//
//        EntityManager em = OPDE.createEM();
//        try{
//        Object connectionObject = null;
//        String elementBezeichnung = "";
//        if (elementQ instanceof NReport) {
//            connectionObject = new SYSNR2PROCESS(process, (NReport) elementQ);
//            elementBezeichnung = "Pflegebericht";
//        } else if (elementQ instanceof BWerte) {
//            connectionObject = new SYSVAL2PROCESS(process, (BWerte) elementQ);
//            elementBezeichnung = "Bewohner Wert";
//        } else if (elementQ instanceof Verordnung) {
//            connectionObject = new SYSPRE2PROCESS(process, (Verordnung) elementQ);
//            elementBezeichnung = "Ärztliche Verordnung";
//        } else if (elementQ instanceof BWInfo) {
//            connectionObject = new SYSINF2PROCESS(process, (BWInfo) elementQ);
//            elementBezeichnung = "Bewohner Information";
//        } else if (elementQ instanceof Planung) {
//            connectionObject = new SYSNP2PROCESS(process, (Planung) elementQ);
//            elementBezeichnung = "Pflegeplanung";
//        } else {
//
//        }
//
//        em.persist(connectionObject);
//
//        // Jetzt fehlt nur noch eins: der PDCA Zyklus muss ermittelt werden. Dieser ergibt sich daraus, WO das Element einsortiert wurde. Daher fragen wir jetzt eine Gesamtübersicht ab.
//        // Der PDCA des neuen Elements ist der des vorherigen Elements. Ist das neue Element das erste in der Liste, ist der PDCA zwingend PLAN.
//
//        List alleElememente = findElementeByVorgang(process, true);
//        int index = alleElememente.indexOf(elementQ);
//        short pdca = PDCA_PLAN;
//        if (index > 0) {
//            Object o = alleElememente.get(index - 1);
//            if (o instanceof Object[]) {
//                pdca = ((Short) ((Object[]) o)[1]);
//            } else {
//                pdca = ((PReport) o).getPdca();
//            }
//        }
//
//        // Connection Objekt korregieren
//        if (elementQ instanceof NReport) {
//            ((SYSNR2PROCESS) connectionObject).setPdca(pdca);
//        } else if (elementQ instanceof BWerte) {
//            ((SYSVAL2PROCESS) connectionObject).setPdca(pdca);
//        } else if (elementQ instanceof Verordnung) {
//            ((SYSPRE2PROCESS) connectionObject).setPdca(pdca);
//        } else if (elementQ instanceof BWInfo) {
//            ((SYSINF2PROCESS) connectionObject).setPdca(pdca);
//        } else if (elementQ instanceof Planung) {
//            ((SYSNP2PROCESS) connectionObject).setPdca(pdca);
//        } else {
//
//        }
//        EntityTools.merge(connectionObject);
//
//        // Nun noch den Systembericht erstellen.
//        PReport vbericht = new PReport("Neue Zuordnung wurde vorgenommen für: " + elementBezeichnung + " ID: " + elementQ.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, process);
//        vbericht.setPdca(pdca);
//        EntityTools.persist(vbericht);
//
//        // Das ursprüngliche Element bekommt die Änderungen nicht mit. Und der JPA Cache auch nicht.
//        // Daher muss das Objekt hier manuell neu gelesen werden.
//        EntityTools.refresh(elementQ);
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

        qProcess.getPReports().add(em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT)+": " + elementBezeichnung + " ID: " + element.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, qProcess)));

    }

    public static void setWVVorgang(QProcess vorgang, Date wv) {

        EntityManager em = OPDE.createEM();

        try {
            em.getTransaction().begin();

            PReport systemBericht = new PReport("Wiedervorlage gesetzt auf: " + DateFormat.getDateInstance().format(wv), PReportTools.PREPORT_TYPE_WV, vorgang);
            em.persist(systemBericht);

            vorgang.setRevision(wv);
            em.merge(vorgang);

            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            OPDE.fatal(ex);
        } finally {
            em.close();
        }
    }

    public static String getAsHTML(QProcess qProcess) {
        String html = "";
        html += "<h2>" + qProcess.getTitle() + "</h2>";

        if (qProcess.getResident() != null) {
            html += "<br/>Vorgang gehört zu BewohnerIn: <b>" + ResidentTools.getLabelText(qProcess.getResident()) + "</b><br/>";
        } else {
            html += "<br/>Allgemeiner Vorgang<br/>";
        }
        html += "<b>Von:</b> " + DateFormat.getDateInstance().format(qProcess.getFrom());
        if (qProcess.isClosed()) {
            html += "&nbsp;&nbsp;<b>Bis:</b> " + DateFormat.getDateInstance().format(qProcess.getTo());
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
        html += "&nbsp;&nbsp;<b>Wiedervorlage:</b> ";
        html += DateFormat.getDateInstance().format(qProcess.getRevision()) + "</font>";
        html += "<br/><b>Erstellt von:</b> " + qProcess.getCreator().getNameUndVorname();
        html += "&nbsp;&nbsp;<b>Aktueller Besitzer:</b> " + qProcess.getOwner().getNameUndVorname();

        return html;
    }

    public static List<QProcess> getProcesses4(Resident resident) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT qp FROM QProcess qp WHERE qp.resident = :resident");
        query.setParameter("resident", resident);
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
//                        OPDE.debug("Vorgang '" + vorgang.getTitle() + "' für Bewohner '" + bw.getBWKennung() + "' angelegt. Element mit ID " + finalElementQ.getID() + " zugeordnet.");
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
