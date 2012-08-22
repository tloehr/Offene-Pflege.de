/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PnlProcess.java
 *
 * Created on 03.06.2011, 16:38:35
 */
package op.process;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.info.Resident;
import entity.process.QProcess;
import entity.process.QProcessElement;
import entity.process.QProcessTools;
import op.OPDE;
import op.events.TaskPaneContentChangedListener;
import op.tools.GUITools;
import op.tools.InternalClassACL;
import op.tools.NursingRecordsPanel;
import op.tools.SYSTools;
import org.jdesktop.swingx.VerticalLayout;
import org.pushingpixels.trident.Timeline;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author tloehr
 */
public class PnlProcess extends NursingRecordsPanel {

    public static final String internalClassID = "opde.tickets";

    private Resident resident;
    private boolean initPhase = false;
    private JToggleButton tbClosed;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    private HashMap<QProcess, CollapsiblePane> qProcessCollapsiblePaneHashMap;
    private HashMap<QProcess, ArrayList<QProcessElement>> qProcess2ElementMap;
    private HashMap<QProcess, CollapsiblePane> qProcessMap;
    private HashMap<QProcessElement, CollapsiblePane> elementMap;
    private List<QProcess> processList;

    //    private int positionToAddPanels;
//    protected HashMap<JComponent, ArrayList<Short>> authorizationMap;
    private TaskPaneContentChangedListener taskPaneContentChangedListener;
    private Timeline textmessageTL;

    public PnlProcess(Resident resident, JScrollPane jspSearch) {
        initPhase = true;
        this.jspSearch = jspSearch;
        initComponents();
        initPanel();
        switchResident(resident);
        initPhase = false;
    }

    private void initPanel() {
        qProcessCollapsiblePaneHashMap = new HashMap<QProcess, CollapsiblePane>();
        qProcess2ElementMap = new HashMap<QProcess, ArrayList<QProcessElement>>();
        qProcessMap = new HashMap<QProcess, CollapsiblePane>();
        elementMap = new HashMap<QProcessElement, CollapsiblePane>();
        prepareSearchArea();
    }

    @Override
    public void reload() {
        reloadDisplay();
    }


//    protected CollapsiblePane addVorgaengeFuerBW(Resident bewohner) {
//
//        JPanel labelPanel = new JPanel();
//        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
//
//        EntityManager em = OPDE.createEM();
//        Query query = em.createNamedQuery("Vorgaenge.findActiveByBewohner");
//        query.setParameter("bewohner", bewohner);
//        List<QProcess> listProceses = query.getResultList();
//        Iterator<QProcess> it = listProceses.iterator();
//        em.close();
//
//        CollapsiblePane bwpanel = new CollapsiblePane(bewohner.getNachname() + ", " + bewohner.getVorname());
//        try {
//            bwpanel.setCollapsed(false);
//        } catch (PropertyVetoException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//
//        if (!listProceses.isEmpty()) {
//            while (it.hasNext()) {
//                final QProcess innervorgang = it.next();
//                JideButton buttonBW = GUITools.createHyperlinkButton(innervorgang.getTitel(), null, new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent actionEvent) {
//                        loadTable(innervorgang);
//                        loadDetails(innervorgang);
//                    }
//                });
//                labelPanel.add(buttonBW);
//            }
//        }
//
//        bwpanel.setContentPane(labelPanel);
//
//        return bwpanel;
//    }


    private void reloadDisplay() {
        /***
         *               _                 _ ____  _           _
         *      _ __ ___| | ___   __ _  __| |  _ \(_)___ _ __ | | __ _ _   _
         *     | '__/ _ \ |/ _ \ / _` |/ _` | | | | / __| '_ \| |/ _` | | | |
         *     | | |  __/ | (_) | (_| | (_| | |_| | \__ \ |_) | | (_| | |_| |
         *     |_|  \___|_|\___/ \__,_|\__,_|____/|_|___/ .__/|_|\__,_|\__, |
         *                                              |_|            |___/
         */
        initPhase = true;
//        dfnCollapsiblePaneHashMap.clear();
//        if (shiftMAPDFN != null) {
//            for (Byte key : shiftMAPDFN.keySet()) {
//                shiftMAPDFN.get(key).clear();
//            }
//        }
//        if (shiftMAPpane != null) {
//            for (Byte key : shiftMAPpane.keySet()) {
//                shiftMAPpane.get(key).removeAll();
//            }
//            shiftMAPpane.clear();
//        }
        final boolean withworker = false;
        if (withworker) {

//            OPDE.getMainframe().setBlocked(true);
//            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));
//
//            cpDFN.removeAll();
//
//            SwingWorker worker = new SwingWorker() {
//
//                @Override
//                protected Object doInBackground() throws Exception {
//
//                    int progress = 0;
//                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, 100));
//
//                    for (DFN dfn : DFNTools.getDFNs(resident, jdcDatum.getDate())) {
//                        shiftMAPDFN.get(dfn.getShift()).add(dfn);
//                    }
//
//                    for (Byte shift : new Byte[]{DFNTools.SHIFT_ON_DEMAND, DFNTools.SHIFT_VERY_EARLY, DFNTools.SHIFT_EARLY, DFNTools.SHIFT_LATE, DFNTools.SHIFT_VERY_LATE}) {
//                        shiftMAPpane.put(shift, createCP4(shift));
//                        try {
//                            shiftMAPpane.get(shift).setCollapsed(shift == DFNTools.SHIFT_ON_DEMAND || shift != SYSCalendar.whatShiftIs(new Date()));
//                        } catch (PropertyVetoException e) {
//                            OPDE.debug(e);
//                        }
//                        progress += 20;
//                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, 100));
//                    }
//
//                    return null;
//                }
//
//                @Override
//                protected void done() {
//                    buildPanel(true);
//                    initPhase = false;
//                    OPDE.getDisplayManager().setProgressBarMessage(null);
//                    OPDE.getMainframe().setBlocked(false);
//                }
//            };
//            worker.execute();

        } else {

            processList = (resident == null ? QProcessTools.getProcesses4(OPDE.getLogin().getUser()) : QProcessTools.getProcesses4(resident));
            Collections.sort(processList);

            for (QProcess qProcess : processList) {
                qProcessMap.put(qProcess, createCP4(qProcess));
            }

            buildPanel();
        }
        initPhase = false;
    }


    private CollapsiblePane createCP4(final QProcessElement element) {
        String title = element.getTitle();
        final CollapsiblePane cp = new CollapsiblePane();

        /***
         *      _   _ _____    _    ____  _____ ____
         *     | | | | ____|  / \  |  _ \| ____|  _ \
         *     | |_| |  _|   / _ \ | | | |  _| | |_) |
         *     |  _  | |___ / ___ \| |_| | |___|  _ <
         *     |_| |_|_____/_/   \_\____/|_____|_| \_\
         *
         */

        JPanel titlePanelleft = new JPanel();
        titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));


        /***
         *      _     _       _    _           _   _                _   _                _
         *     | |   (_)_ __ | | _| |__  _   _| |_| |_ ___  _ __   | | | | ___  __ _  __| | ___ _ __
         *     | |   | | '_ \| |/ / '_ \| | | | __| __/ _ \| '_ \  | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
         *     | |___| | | | |   <| |_) | |_| | |_| || (_) | | | | |  _  |  __/ (_| | (_| |  __/ |
         *     |_____|_|_| |_|_|\_\_.__/ \__,_|\__|\__\___/|_| |_| |_| |_|\___|\__,_|\__,_|\___|_|
         *
         */
        JideButton btnReport = GUITools.createHyperlinkButton(SYSTools.toHTMLForScreen(title), null, null);
        btnReport.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    cp.setCollapsed(!cp.isCollapsed());
                } catch (PropertyVetoException e) {
                    OPDE.error(e);
                }
            }
        });

        titlePanelleft.add(btnReport);

        JPanel titlePanelright = new JPanel();
        titlePanelright.setLayout(new BoxLayout(titlePanelright, BoxLayout.LINE_AXIS));


//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {


//            /***
//             *      _     _         _____    _ _ _
//             *     | |__ | |_ _ __ | ____|__| (_) |_
//             *     | '_ \| __| '_ \|  _| / _` | | __|
//             *     | |_) | |_| | | | |__| (_| | | |_
//             *     |_.__/ \__|_| |_|_____\__,_|_|\__|
//             *
//             */
//            final JButton btnEdit = new JButton(SYSConst.icon22edit1);
//            btnEdit.setPressedIcon(SYSConst.icon22edit1Pressed);
//            btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            btnEdit.setContentAreaFilled(false);
//            btnEdit.setBorder(null);
//            btnEdit.setToolTipText(OPDE.lang.getString(internalClassID + ".btnedit.tooltip"));
//            btnEdit.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    if (!NReportTools.isChangeable(report)) {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
//                        return;
//                    }
//                    new DlgReport((NReport) report.clone(), new Closure() {
//                        @Override
//                        public void execute(Object result) {
//                            if (result != null) {
//
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                    NReport newReport = em.merge((NReport) result);
//                                    NReport oldReport = em.merge((NReport) report);
//
//                                    em.lock(oldReport, LockModeType.OPTIMISTIC);
//                                    newReport.setReplacementFor(oldReport);
//
//                                    for (SYSPB2FILE oldAssignment : oldReport.getAttachedFiles()) {
//                                        em.remove(oldAssignment);
//                                    }
//                                    oldReport.getAttachedFiles().clear();
//                                    for (SYSNR2PROCESS oldAssignment : oldReport.getAttachedVorgaenge()) {
//                                        em.remove(oldAssignment);
//                                    }
//                                    oldReport.getAttachedVorgaenge().clear();
//
//                                    oldReport.setEditedBy(em.merge(OPDE.getLogin().getUser()));
//                                    oldReport.setEditpit(new Date());
//                                    oldReport.setReplacedBy(newReport);
//
//                                    em.getTransaction().commit();
//                                    DateMidnight dm = new DateMidnight(newReport.getPit());
//                                    if (!dayMap.containsKey(dm)) {
//                                        dayMap.put(dm, new ArrayList<NReport>());
//                                    }
//                                    dayMap.get(dm).remove(report);
//                                    dayMap.get(dm).add(newReport);
//                                    dayMap.get(dm).add(oldReport);
//                                    Collections.sort(dayMap.get(dm));
//                                    reportMap.remove(report);
//                                    reportMap.put(newReport, createCP4(newReport));
//                                    reportMap.put(oldReport, createCP4(oldReport));
//                                    buildPanel();
//                                } catch (OptimisticLockException ole) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
//                                    }
//                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                } catch (Exception e) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//                            }
//                        }
//                    });
//                }
//            });
//            btnEdit.setEnabled(!report.isObsolete());
//            titlePanelright.add(btnEdit);
//
//            /***
//             *      _     _         ____       _      _
//             *     | |__ | |_ _ __ |  _ \  ___| | ___| |_ ___
//             *     | '_ \| __| '_ \| | | |/ _ \ |/ _ \ __/ _ \
//             *     | |_) | |_| | | | |_| |  __/ |  __/ ||  __/
//             *     |_.__/ \__|_| |_|____/ \___|_|\___|\__\___|
//             *
//             */
//            final JButton btnDelete = new JButton(SYSConst.icon22delete);
//            btnDelete.setPressedIcon(SYSConst.icon22deletePressed);
//            btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            btnDelete.setContentAreaFilled(false);
//            btnDelete.setBorder(null);
//            btnDelete.setToolTipText(OPDE.lang.getString(internalClassID + ".btndelete.tooltip"));
//            btnDelete.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    new DlgYesNo(OPDE.lang.getString("misc.questions.delete"), SYSConst.icon48delete, new Closure() {
//                        @Override
//                        public void execute(Object answer) {
//                            if (answer.equals(JOptionPane.YES_OPTION)) {
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                    NReport delReport = em.merge(report);
//                                    em.lock(delReport, LockModeType.OPTIMISTIC);
//                                    delReport.setDeletedBy(em.merge(OPDE.getLogin().getUser()));
//                                    for (SYSPB2FILE oldAssignment : delReport.getAttachedFiles()) {
//                                        em.remove(oldAssignment);
//                                    }
//                                    delReport.getAttachedFiles().clear();
//                                    for (SYSNR2PROCESS oldAssignment : delReport.getAttachedVorgaenge()) {
//                                        em.remove(oldAssignment);
//                                    }
//                                    delReport.getAttachedVorgaenge().clear();
//                                    em.getTransaction().commit();
//
//                                    DateMidnight dm = new DateMidnight(delReport.getPit());
//                                    if (!dayMap.containsKey(dm)) {
//                                        dayMap.put(dm, new ArrayList<NReport>());
//                                    }
//                                    dayMap.get(dm).remove(report);
//                                    dayMap.get(dm).add(delReport);
//                                    Collections.sort(dayMap.get(dm));
//                                    reportMap.remove(report);
//                                    reportMap.put(delReport, createCP4(delReport));
//                                    buildPanel();
//                                } catch (OptimisticLockException ole) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
//                                    }
//                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                } catch (Exception e) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//
//                            }
//                        }
//                    });
//                }
//            });
//            btnDelete.setEnabled(!report.isObsolete());
//            titlePanelright.add(btnDelete);
//
//            /***
//             *      _     _       _____  _    ____
//             *     | |__ | |_ _ _|_   _|/ \  / ___|___
//             *     | '_ \| __| '_ \| | / _ \| |  _/ __|
//             *     | |_) | |_| | | | |/ ___ \ |_| \__ \
//             *     |_.__/ \__|_| |_|_/_/   \_\____|___/
//             *
//             */
//            final JButton btnTAGs = new JButton(SYSConst.icon22todo);
//            btnTAGs.setPressedIcon(SYSConst.icon22todoPressed);
//            btnTAGs.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            btnTAGs.setContentAreaFilled(false);
//            btnTAGs.setBorder(null);
//            btnTAGs.setToolTipText(OPDE.lang.getString(internalClassID + ".btntags.tooltip"));
//            btnTAGs.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    if (popup != null && popup.isPopupVisible()) {
//                        popup.hidePopup();
//                        return;
//                    }
//                    if (!NReportTools.isChangeable(report)) {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
//                        return;
//                    }
//                    ItemListener il = new ItemListener() {
//                        @Override
//                        public void itemStateChanged(ItemEvent itemEvent) {
//                            JCheckBox cb = (JCheckBox) itemEvent.getSource();
//                            NReportTAGS tag = (NReportTAGS) cb.getClientProperty("UserObject");
//
//                            EntityManager em = OPDE.createEM();
//                            try {
//                                em.getTransaction().begin();
//                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                NReport newReport = em.merge(report);
//                                em.lock(newReport, LockModeType.OPTIMISTIC);
//
//                                if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
//                                    newReport.getTags().remove(tag);
//                                } else {
//                                    newReport.getTags().add(tag);
//                                }
//
//                                em.getTransaction().commit();
//                                DateMidnight dm = new DateMidnight(newReport.getPit());
//                                if (!dayMap.containsKey(dm)) {
//                                    dayMap.put(dm, new ArrayList<NReport>());
//                                }
//                                dayMap.get(dm).remove(report);
//                                dayMap.get(dm).add(newReport);
//                                Collections.sort(dayMap.get(dm));
//                                reportMap.remove(report);
//                                reportMap.put(newReport, createCP4(newReport));
//                                buildPanel();
//                            } catch (OptimisticLockException ole) {
//                                if (em.getTransaction().isActive()) {
//                                    em.getTransaction().rollback();
//                                }
//                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                    OPDE.getMainframe().emptyFrame();
//                                    OPDE.getMainframe().afterLogin();
//                                }
//                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                            } catch (Exception e) {
//                                if (em.getTransaction().isActive()) {
//                                    em.getTransaction().rollback();
//                                }
//                                OPDE.fatal(e);
//                            } finally {
//                                em.close();
//                            }
//
//
//                        }
//                    };
//
//                    popup = new JidePopup();
//                    JPanel pnl = NReportTAGSTools.createCheckBoxPanelForTags(il, report.getTags(), new GridLayout(8, 4));
//                    popup.setMovable(false);
//                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//                    popup.setOwner(btnTAGs);
//                    popup.removeExcludedComponent(btnTAGs);
//                    popup.getContentPane().add(pnl);
//                    popup.setDefaultFocusComponent(pnl);
//
//                    GUITools.showPopup(popup, SwingConstants.WEST);
//
//                }
//            });
//            btnTAGs.setEnabled(!report.isObsolete());
//            titlePanelright.add(btnTAGs);
//
//            /***
//             *      _     _         __  __ _             _
//             *     | |__ | |_ _ __ |  \/  (_)_ __  _   _| |_ ___  ___
//             *     | '_ \| __| '_ \| |\/| | | '_ \| | | | __/ _ \/ __|
//             *     | |_) | |_| | | | |  | | | | | | |_| | ||  __/\__ \
//             *     |_.__/ \__|_| |_|_|  |_|_|_| |_|\__,_|\__\___||___/
//             *
//             */
//            final JButton btnMinutes = new JButton(SYSConst.icon22clock);
//            btnMinutes.setPressedIcon(SYSConst.icon22clockPressed);
//            btnMinutes.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            btnMinutes.setContentAreaFilled(false);
//            btnMinutes.setBorder(null);
//            btnMinutes.setToolTipText(OPDE.lang.getString(internalClassID + ".btnminutes.tooltip"));
//            btnMinutes.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    if (!NReportTools.isChangeable(report)) {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
//                        return;
//                    }
//                    final JPopupMenu menu = SYSCalendar.getMinutesMenu(new int[]{1, 2, 3, 4, 5, 10, 15, 20, 30, 45, 60, 120, 240, 360}, new Closure() {
//                        @Override
//                        public void execute(Object o) {
//                            EntityManager em = OPDE.createEM();
//                            try {
//                                em.getTransaction().begin();
//
//                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                NReport myReport = em.merge(report);
//                                em.lock(myReport, LockModeType.OPTIMISTIC);
//
//                                myReport.setMinutes((Integer) o);
//                                myReport.setEditpit(new Date());
//
//                                em.getTransaction().commit();
//
//                                DateMidnight dm = new DateMidnight(myReport.getPit());
//                                if (!dayMap.containsKey(dm)) {
//                                    dayMap.put(dm, new ArrayList<NReport>());
//                                }
//                                dayMap.get(dm).add(myReport);
//                                Collections.sort(dayMap.get(dm));
//                                reportMap.put(myReport, createCP4(myReport));
//                                buildPanel();
//
//                            } catch (OptimisticLockException ole) {
//                                if (em.getTransaction().isActive()) {
//                                    em.getTransaction().rollback();
//                                }
//                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                    OPDE.getMainframe().emptyFrame();
//                                    OPDE.getMainframe().afterLogin();
//                                }
//                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                            } catch (Exception e) {
//                                if (em.getTransaction().isActive()) {
//                                    em.getTransaction().rollback();
//                                }
//                                OPDE.fatal(e);
//                            } finally {
//                                em.close();
//                            }
//                        }
//                    });
//
//                    menu.show(btnMinutes, 0, btnMinutes.getHeight());
//                }
//            });
//            btnMinutes.setEnabled(!report.isObsolete());
//            titlePanelright.add(btnMinutes);
//        }
//
//        /***
//         *      _     _         _____ _ _
//         *     | |__ | |_ _ __ |  ___(_) | ___  ___
//         *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
//         *     | |_) | |_| | | |  _| | | |  __/\__ \
//         *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
//         *
//         */
//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.INSERT)) {
//            final JButton btnFiles = new JButton(SYSConst.icon22attach);
//            btnFiles.setPressedIcon(SYSConst.icon22attachPressed);
//            btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            btnFiles.setContentAreaFilled(false);
//            btnFiles.setBorder(null);
//            btnFiles.setToolTipText(OPDE.lang.getString(internalClassID + ".btnfiles.tooltip"));
//            btnFiles.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    Closure closure = null;
//                    if (!report.isObsolete()) {
//                        closure = new Closure() {
//                            @Override
//                            public void execute(Object o) {
//                                EntityManager em = OPDE.createEM();
//                                NReport myReport = em.merge(report);
//                                em.refresh(myReport);
//                                DateMidnight dm = new DateMidnight(myReport.getPit());
//                                if (!dayMap.containsKey(dm)) {
//                                    dayMap.put(dm, new ArrayList<NReport>());
//                                }
//                                dayMap.get(dm).remove(report);
//                                dayMap.get(dm).add(myReport);
//                                Collections.sort(dayMap.get(dm));
//                                reportMap.remove(report);
//                                reportMap.put(myReport, createCP4(myReport));
//                                buildPanel();
//                                em.close();
//                            }
//                        };
//                    }
//                    new DlgFiles(report, closure);
//                }
//            });
//
//            if (report.getAttachedFiles().size() > 0) {
//                JLabel lblNum = new JLabel(Integer.toString(report.getAttachedFiles().size()), SYSConst.icon16redStar, SwingConstants.CENTER);
//                lblNum.setFont(SYSConst.ARIAL10BOLD);
//                lblNum.setForeground(Color.YELLOW);
//                lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
//                DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnFiles, lblNum, DefaultOverlayable.SOUTH_EAST);
//                overlayableBtn.setOpaque(false);
//                titlePanelright.add(overlayableBtn);
//            } else {
//                titlePanelright.add(btnFiles);
//            }
//
//        }
//
//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.INSERT)) {
//            /***
//             *      _     _         ____
//             *     | |__ | |_ _ __ |  _ \ _ __ ___   ___ ___  ___ ___
//             *     | '_ \| __| '_ \| |_) | '__/ _ \ / __/ _ \/ __/ __|
//             *     | |_) | |_| | | |  __/| | | (_) | (_|  __/\__ \__ \
//             *     |_.__/ \__|_| |_|_|   |_|  \___/ \___\___||___/___/
//             *
//             */
//            final JButton btnProcess = new JButton(SYSConst.icon22link);
//            btnProcess.setPressedIcon(SYSConst.icon22linkPressed);
//            btnProcess.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            btnProcess.setContentAreaFilled(false);
//            btnProcess.setBorder(null);
//            btnProcess.setToolTipText(OPDE.lang.getString(internalClassID + ".btnprocess.tooltip"));
//            btnProcess.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//
//                }
//            });
//            btnProcess.setEnabled(!report.isObsolete());
//            titlePanelright.add(btnProcess);
//        }

        titlePanelleft.setOpaque(false);
        titlePanelright.setOpaque(false);
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);

        titlePanel.setLayout(new GridBagLayout());
        ((GridBagLayout) titlePanel.getLayout()).columnWidths = new int[]{0, 80};
        ((GridBagLayout) titlePanel.getLayout()).columnWeights = new double[]{1.0, 1.0};

        titlePanel.add(titlePanelleft, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 5), 0, 0));

        titlePanel.add(titlePanelright, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));

        cp.setTitleLabelComponent(titlePanel);
        cp.setSlidingDirection(SwingConstants.SOUTH);

        try {
            cp.setCollapsed(true);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }


        /***
         *       ___ ___  _  _ _____ ___ _  _ _____
         *      / __/ _ \| \| |_   _| __| \| |_   _|
         *     | (_| (_) | .` | | | | _|| .` | | |
         *      \___\___/|_|\_| |_| |___|_|\_| |_|
         *
         */

        cp.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {

//
                JTextPane contentPane = new JTextPane();
                contentPane.setContentType("text/html");
                contentPane.setEditable(false);
                contentPane.setText(SYSTools.toHTMLForScreen(element.getContentAsHTML()));
                cp.setContentPane(contentPane);

            }
        });
//        cp.setBackground(SYSCalendar.getBG(SYSCalendar.whatShiftIs(report.getPit())));
        cp.setHorizontalAlignment(SwingConstants.LEADING);
        cp.setOpaque(false);

        return cp;
    }




    private CollapsiblePane createCP4(final QProcess qProcess) {
        String title = qProcess.getTitle();
        final CollapsiblePane cp = new CollapsiblePane();

        /***
         *      _   _ _____    _    ____  _____ ____
         *     | | | | ____|  / \  |  _ \| ____|  _ \
         *     | |_| |  _|   / _ \ | | | |  _| | |_) |
         *     |  _  | |___ / ___ \| |_| | |___|  _ <
         *     |_| |_|_____/_/   \_\____/|_____|_| \_\
         *
         */

        JPanel titlePanelleft = new JPanel();
        titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));


        /***
         *      _     _       _    _           _   _                _   _                _
         *     | |   (_)_ __ | | _| |__  _   _| |_| |_ ___  _ __   | | | | ___  __ _  __| | ___ _ __
         *     | |   | | '_ \| |/ / '_ \| | | | __| __/ _ \| '_ \  | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
         *     | |___| | | | |   <| |_) | |_| | |_| || (_) | | | | |  _  |  __/ (_| | (_| |  __/ |
         *     |_____|_|_| |_|_|\_\_.__/ \__,_|\__|\__\___/|_| |_| |_| |_|\___|\__,_|\__,_|\___|_|
         *
         */
        JideButton btnReport = GUITools.createHyperlinkButton(SYSTools.toHTMLForScreen(title), null, null);
        btnReport.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    cp.setCollapsed(!cp.isCollapsed());
                } catch (PropertyVetoException e) {
                    OPDE.error(e);
                }
            }
        });
        btnReport.setForeground(qProcess.isClosed() ? Color.gray : Color.black);

        titlePanelleft.add(btnReport);

        JPanel titlePanelright = new JPanel();
        titlePanelright.setLayout(new BoxLayout(titlePanelright, BoxLayout.LINE_AXIS));


//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {


//            /***
//             *      _     _         _____    _ _ _
//             *     | |__ | |_ _ __ | ____|__| (_) |_
//             *     | '_ \| __| '_ \|  _| / _` | | __|
//             *     | |_) | |_| | | | |__| (_| | | |_
//             *     |_.__/ \__|_| |_|_____\__,_|_|\__|
//             *
//             */
//            final JButton btnEdit = new JButton(SYSConst.icon22edit1);
//            btnEdit.setPressedIcon(SYSConst.icon22edit1Pressed);
//            btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            btnEdit.setContentAreaFilled(false);
//            btnEdit.setBorder(null);
//            btnEdit.setToolTipText(OPDE.lang.getString(internalClassID + ".btnedit.tooltip"));
//            btnEdit.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    if (!NReportTools.isChangeable(report)) {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
//                        return;
//                    }
//                    new DlgReport((NReport) report.clone(), new Closure() {
//                        @Override
//                        public void execute(Object result) {
//                            if (result != null) {
//
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                    NReport newReport = em.merge((NReport) result);
//                                    NReport oldReport = em.merge((NReport) report);
//
//                                    em.lock(oldReport, LockModeType.OPTIMISTIC);
//                                    newReport.setReplacementFor(oldReport);
//
//                                    for (SYSPB2FILE oldAssignment : oldReport.getAttachedFiles()) {
//                                        em.remove(oldAssignment);
//                                    }
//                                    oldReport.getAttachedFiles().clear();
//                                    for (SYSNR2PROCESS oldAssignment : oldReport.getAttachedVorgaenge()) {
//                                        em.remove(oldAssignment);
//                                    }
//                                    oldReport.getAttachedVorgaenge().clear();
//
//                                    oldReport.setEditedBy(em.merge(OPDE.getLogin().getUser()));
//                                    oldReport.setEditpit(new Date());
//                                    oldReport.setReplacedBy(newReport);
//
//                                    em.getTransaction().commit();
//                                    DateMidnight dm = new DateMidnight(newReport.getPit());
//                                    if (!dayMap.containsKey(dm)) {
//                                        dayMap.put(dm, new ArrayList<NReport>());
//                                    }
//                                    dayMap.get(dm).remove(report);
//                                    dayMap.get(dm).add(newReport);
//                                    dayMap.get(dm).add(oldReport);
//                                    Collections.sort(dayMap.get(dm));
//                                    reportMap.remove(report);
//                                    reportMap.put(newReport, createCP4(newReport));
//                                    reportMap.put(oldReport, createCP4(oldReport));
//                                    buildPanel();
//                                } catch (OptimisticLockException ole) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
//                                    }
//                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                } catch (Exception e) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//                            }
//                        }
//                    });
//                }
//            });
//            btnEdit.setEnabled(!report.isObsolete());
//            titlePanelright.add(btnEdit);
//
//            /***
//             *      _     _         ____       _      _
//             *     | |__ | |_ _ __ |  _ \  ___| | ___| |_ ___
//             *     | '_ \| __| '_ \| | | |/ _ \ |/ _ \ __/ _ \
//             *     | |_) | |_| | | | |_| |  __/ |  __/ ||  __/
//             *     |_.__/ \__|_| |_|____/ \___|_|\___|\__\___|
//             *
//             */
//            final JButton btnDelete = new JButton(SYSConst.icon22delete);
//            btnDelete.setPressedIcon(SYSConst.icon22deletePressed);
//            btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            btnDelete.setContentAreaFilled(false);
//            btnDelete.setBorder(null);
//            btnDelete.setToolTipText(OPDE.lang.getString(internalClassID + ".btndelete.tooltip"));
//            btnDelete.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    new DlgYesNo(OPDE.lang.getString("misc.questions.delete"), SYSConst.icon48delete, new Closure() {
//                        @Override
//                        public void execute(Object answer) {
//                            if (answer.equals(JOptionPane.YES_OPTION)) {
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                    NReport delReport = em.merge(report);
//                                    em.lock(delReport, LockModeType.OPTIMISTIC);
//                                    delReport.setDeletedBy(em.merge(OPDE.getLogin().getUser()));
//                                    for (SYSPB2FILE oldAssignment : delReport.getAttachedFiles()) {
//                                        em.remove(oldAssignment);
//                                    }
//                                    delReport.getAttachedFiles().clear();
//                                    for (SYSNR2PROCESS oldAssignment : delReport.getAttachedVorgaenge()) {
//                                        em.remove(oldAssignment);
//                                    }
//                                    delReport.getAttachedVorgaenge().clear();
//                                    em.getTransaction().commit();
//
//                                    DateMidnight dm = new DateMidnight(delReport.getPit());
//                                    if (!dayMap.containsKey(dm)) {
//                                        dayMap.put(dm, new ArrayList<NReport>());
//                                    }
//                                    dayMap.get(dm).remove(report);
//                                    dayMap.get(dm).add(delReport);
//                                    Collections.sort(dayMap.get(dm));
//                                    reportMap.remove(report);
//                                    reportMap.put(delReport, createCP4(delReport));
//                                    buildPanel();
//                                } catch (OptimisticLockException ole) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
//                                    }
//                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                } catch (Exception e) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//
//                            }
//                        }
//                    });
//                }
//            });
//            btnDelete.setEnabled(!report.isObsolete());
//            titlePanelright.add(btnDelete);
//
//            /***
//             *      _     _       _____  _    ____
//             *     | |__ | |_ _ _|_   _|/ \  / ___|___
//             *     | '_ \| __| '_ \| | / _ \| |  _/ __|
//             *     | |_) | |_| | | | |/ ___ \ |_| \__ \
//             *     |_.__/ \__|_| |_|_/_/   \_\____|___/
//             *
//             */
//            final JButton btnTAGs = new JButton(SYSConst.icon22todo);
//            btnTAGs.setPressedIcon(SYSConst.icon22todoPressed);
//            btnTAGs.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            btnTAGs.setContentAreaFilled(false);
//            btnTAGs.setBorder(null);
//            btnTAGs.setToolTipText(OPDE.lang.getString(internalClassID + ".btntags.tooltip"));
//            btnTAGs.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    if (popup != null && popup.isPopupVisible()) {
//                        popup.hidePopup();
//                        return;
//                    }
//                    if (!NReportTools.isChangeable(report)) {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
//                        return;
//                    }
//                    ItemListener il = new ItemListener() {
//                        @Override
//                        public void itemStateChanged(ItemEvent itemEvent) {
//                            JCheckBox cb = (JCheckBox) itemEvent.getSource();
//                            NReportTAGS tag = (NReportTAGS) cb.getClientProperty("UserObject");
//
//                            EntityManager em = OPDE.createEM();
//                            try {
//                                em.getTransaction().begin();
//                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                NReport newReport = em.merge(report);
//                                em.lock(newReport, LockModeType.OPTIMISTIC);
//
//                                if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
//                                    newReport.getTags().remove(tag);
//                                } else {
//                                    newReport.getTags().add(tag);
//                                }
//
//                                em.getTransaction().commit();
//                                DateMidnight dm = new DateMidnight(newReport.getPit());
//                                if (!dayMap.containsKey(dm)) {
//                                    dayMap.put(dm, new ArrayList<NReport>());
//                                }
//                                dayMap.get(dm).remove(report);
//                                dayMap.get(dm).add(newReport);
//                                Collections.sort(dayMap.get(dm));
//                                reportMap.remove(report);
//                                reportMap.put(newReport, createCP4(newReport));
//                                buildPanel();
//                            } catch (OptimisticLockException ole) {
//                                if (em.getTransaction().isActive()) {
//                                    em.getTransaction().rollback();
//                                }
//                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                    OPDE.getMainframe().emptyFrame();
//                                    OPDE.getMainframe().afterLogin();
//                                }
//                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                            } catch (Exception e) {
//                                if (em.getTransaction().isActive()) {
//                                    em.getTransaction().rollback();
//                                }
//                                OPDE.fatal(e);
//                            } finally {
//                                em.close();
//                            }
//
//
//                        }
//                    };
//
//                    popup = new JidePopup();
//                    JPanel pnl = NReportTAGSTools.createCheckBoxPanelForTags(il, report.getTags(), new GridLayout(8, 4));
//                    popup.setMovable(false);
//                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//                    popup.setOwner(btnTAGs);
//                    popup.removeExcludedComponent(btnTAGs);
//                    popup.getContentPane().add(pnl);
//                    popup.setDefaultFocusComponent(pnl);
//
//                    GUITools.showPopup(popup, SwingConstants.WEST);
//
//                }
//            });
//            btnTAGs.setEnabled(!report.isObsolete());
//            titlePanelright.add(btnTAGs);
//
//            /***
//             *      _     _         __  __ _             _
//             *     | |__ | |_ _ __ |  \/  (_)_ __  _   _| |_ ___  ___
//             *     | '_ \| __| '_ \| |\/| | | '_ \| | | | __/ _ \/ __|
//             *     | |_) | |_| | | | |  | | | | | | |_| | ||  __/\__ \
//             *     |_.__/ \__|_| |_|_|  |_|_|_| |_|\__,_|\__\___||___/
//             *
//             */
//            final JButton btnMinutes = new JButton(SYSConst.icon22clock);
//            btnMinutes.setPressedIcon(SYSConst.icon22clockPressed);
//            btnMinutes.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            btnMinutes.setContentAreaFilled(false);
//            btnMinutes.setBorder(null);
//            btnMinutes.setToolTipText(OPDE.lang.getString(internalClassID + ".btnminutes.tooltip"));
//            btnMinutes.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    if (!NReportTools.isChangeable(report)) {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
//                        return;
//                    }
//                    final JPopupMenu menu = SYSCalendar.getMinutesMenu(new int[]{1, 2, 3, 4, 5, 10, 15, 20, 30, 45, 60, 120, 240, 360}, new Closure() {
//                        @Override
//                        public void execute(Object o) {
//                            EntityManager em = OPDE.createEM();
//                            try {
//                                em.getTransaction().begin();
//
//                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                NReport myReport = em.merge(report);
//                                em.lock(myReport, LockModeType.OPTIMISTIC);
//
//                                myReport.setMinutes((Integer) o);
//                                myReport.setEditpit(new Date());
//
//                                em.getTransaction().commit();
//
//                                DateMidnight dm = new DateMidnight(myReport.getPit());
//                                if (!dayMap.containsKey(dm)) {
//                                    dayMap.put(dm, new ArrayList<NReport>());
//                                }
//                                dayMap.get(dm).add(myReport);
//                                Collections.sort(dayMap.get(dm));
//                                reportMap.put(myReport, createCP4(myReport));
//                                buildPanel();
//
//                            } catch (OptimisticLockException ole) {
//                                if (em.getTransaction().isActive()) {
//                                    em.getTransaction().rollback();
//                                }
//                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                    OPDE.getMainframe().emptyFrame();
//                                    OPDE.getMainframe().afterLogin();
//                                }
//                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                            } catch (Exception e) {
//                                if (em.getTransaction().isActive()) {
//                                    em.getTransaction().rollback();
//                                }
//                                OPDE.fatal(e);
//                            } finally {
//                                em.close();
//                            }
//                        }
//                    });
//
//                    menu.show(btnMinutes, 0, btnMinutes.getHeight());
//                }
//            });
//            btnMinutes.setEnabled(!report.isObsolete());
//            titlePanelright.add(btnMinutes);
//        }
//
//        /***
//         *      _     _         _____ _ _
//         *     | |__ | |_ _ __ |  ___(_) | ___  ___
//         *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
//         *     | |_) | |_| | | |  _| | | |  __/\__ \
//         *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
//         *
//         */
//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.INSERT)) {
//            final JButton btnFiles = new JButton(SYSConst.icon22attach);
//            btnFiles.setPressedIcon(SYSConst.icon22attachPressed);
//            btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            btnFiles.setContentAreaFilled(false);
//            btnFiles.setBorder(null);
//            btnFiles.setToolTipText(OPDE.lang.getString(internalClassID + ".btnfiles.tooltip"));
//            btnFiles.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    Closure closure = null;
//                    if (!report.isObsolete()) {
//                        closure = new Closure() {
//                            @Override
//                            public void execute(Object o) {
//                                EntityManager em = OPDE.createEM();
//                                NReport myReport = em.merge(report);
//                                em.refresh(myReport);
//                                DateMidnight dm = new DateMidnight(myReport.getPit());
//                                if (!dayMap.containsKey(dm)) {
//                                    dayMap.put(dm, new ArrayList<NReport>());
//                                }
//                                dayMap.get(dm).remove(report);
//                                dayMap.get(dm).add(myReport);
//                                Collections.sort(dayMap.get(dm));
//                                reportMap.remove(report);
//                                reportMap.put(myReport, createCP4(myReport));
//                                buildPanel();
//                                em.close();
//                            }
//                        };
//                    }
//                    new DlgFiles(report, closure);
//                }
//            });
//
//            if (report.getAttachedFiles().size() > 0) {
//                JLabel lblNum = new JLabel(Integer.toString(report.getAttachedFiles().size()), SYSConst.icon16redStar, SwingConstants.CENTER);
//                lblNum.setFont(SYSConst.ARIAL10BOLD);
//                lblNum.setForeground(Color.YELLOW);
//                lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
//                DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnFiles, lblNum, DefaultOverlayable.SOUTH_EAST);
//                overlayableBtn.setOpaque(false);
//                titlePanelright.add(overlayableBtn);
//            } else {
//                titlePanelright.add(btnFiles);
//            }
//
//        }
//
//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.INSERT)) {
//            /***
//             *      _     _         ____
//             *     | |__ | |_ _ __ |  _ \ _ __ ___   ___ ___  ___ ___
//             *     | '_ \| __| '_ \| |_) | '__/ _ \ / __/ _ \/ __/ __|
//             *     | |_) | |_| | | |  __/| | | (_) | (_|  __/\__ \__ \
//             *     |_.__/ \__|_| |_|_|   |_|  \___/ \___\___||___/___/
//             *
//             */
//            final JButton btnProcess = new JButton(SYSConst.icon22link);
//            btnProcess.setPressedIcon(SYSConst.icon22linkPressed);
//            btnProcess.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            btnProcess.setContentAreaFilled(false);
//            btnProcess.setBorder(null);
//            btnProcess.setToolTipText(OPDE.lang.getString(internalClassID + ".btnprocess.tooltip"));
//            btnProcess.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//
//                }
//            });
//            btnProcess.setEnabled(!report.isObsolete());
//            titlePanelright.add(btnProcess);
//        }

        titlePanelleft.setOpaque(false);
        titlePanelright.setOpaque(false);
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);

        titlePanel.setLayout(new GridBagLayout());
        ((GridBagLayout) titlePanel.getLayout()).columnWidths = new int[]{0, 80};
        ((GridBagLayout) titlePanel.getLayout()).columnWeights = new double[]{1.0, 1.0};

        titlePanel.add(titlePanelleft, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 5), 0, 0));

        titlePanel.add(titlePanelright, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));

        cp.setTitleLabelComponent(titlePanel);
        cp.setSlidingDirection(SwingConstants.SOUTH);

        try {
            cp.setCollapsed(true);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }


        /***
         *       ___ ___  _  _ _____ ___ _  _ _____
         *      / __/ _ \| \| |_   _| __| \| |_   _|
         *     | (_| (_) | .` | | | | _|| .` | | |
         *      \___\___/|_|\_| |_| |___|_|\_| |_|
         *
         */

        cp.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {

                JTextPane contentPane = new JTextPane();
                contentPane.setContentType("text/html");
                contentPane.setEditable(false);
                contentPane.setText(SYSTools.toHTMLForScreen(QProcessTools.getAsHTML(qProcess)));

                JPanel elementPanel = new JPanel();
                elementPanel.setLayout(new VerticalLayout());
                elementPanel.add(contentPane);

                for (QProcessElement element : qProcess.getElements()){
                    elementPanel.add(new CollapsiblePane(element.getTitle()));
                }

                cp.setContentPane(elementPanel);
                cp.setOpaque(false);

            }
        });
//        cp.setBackground(SYSCalendar.getBG(SYSCalendar.whatShiftIs(report.getPit())));
        cp.setHorizontalAlignment(SwingConstants.LEADING);
        cp.setOpaque(false);

        return cp;
    }






//    protected void addVorgaengeFuerBW() {
//        //((Container) taskContainer).add(new JLabel("Bewohner"));
//        EntityManager em = OPDE.createEM();
//        List<Bewohner> bewohner = em.createNamedQuery("Bewohner.findAllActiveSorted").getResultList();
//
//        JXTaskPane allbwpanel = new JXTaskPane("nach BewohnerInnen");
//        allbwpanel.setCollapsed(true);
//
//        for (Bewohner bw : bewohner) {
//
//            Query query = em.createNamedQuery("QProcess.findActiveByBewohner");
//            query.setParameter("bewohner", bw);
//            List<QProcess> listVorgaenge = query.getResultList();
//            Iterator<QProcess> it = listVorgaenge.iterator();
//
//            if (!listVorgaenge.isEmpty()) {
//                JXTaskPane bwpanel = new JXTaskPane(bw.getNachname() + ", " + bw.getVorname());
//                bwpanel.setCollapsed(true);
//
//                while (it.hasNext()) {
//                    final QProcess innervorgang = it.next();
//                    bwpanel.add(new AbstractAction() {
//                        {
//                            putValue(Action.NAME, innervorgang.getTitel());
//                        }
//
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//                            loadTable(innervorgang);
//                            loadDetails(innervorgang);
//                        }
//                    });
//                }
//
//                allbwpanel.add(bwpanel);
//            }
//
//        }
//
//        panelSearch.add(allbwpanel);
//
//        em.close();
//    }


//    protected Object[] getTableButtons() {
//        return new Object[]{new JButton(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/edit_remove.png"))),
//                new TableButtonBehaviour() {
//                    @Override
//                    public void actionPerformed(TableButtonActionEvent e) {
//                        ((TMElement) tblElements.getModel()).removeRow(e.getTable().getSelectedRow());
//                    }
//
//                    @Override
//                    public boolean isEnabled(JTable table, int row, int col) {
//                        QProcessElement element = ((TMElement) tblElements.getModel()).getElement(row);
//                        boolean systemBericht = (element instanceof PReport) && ((PReport) element).isSystem();
//                        return !systemBericht;
//                    }
//                }};
//    }

//    protected void getVorgaengeFuerMA() {
//        pnlVorgaengeByMA.removeAll();
//        pnlVorgaengeByMA.add(cmbMA);
//        pnlVorgaengeByMA.add(new JSeparator());
//        Users selectedUser = (Users) cmbMA.getSelectedItem();
//        if (selectedUser != null) {
//            EntityManager em = OPDE.createEM();
//            Query query = em.createNamedQuery("Vorgaenge.findActiveByBesitzer");
//            query.setParameter("besitzer", selectedUser);
//            List<QProcess> listProceses = query.getResultList();
//            Iterator<QProcess> it = listProceses.iterator();
//
//            while (it.hasNext()) {
//                final QProcess innervorgang = it.next();
//                pnlVorgaengeByMA.add(new AbstractAction() {
//                    {
//                        putValue(Action.NAME, innervorgang.getTitel());
//                        //putValue(Action.SHORT_DESCRIPTION, null);
//                    }
//
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        loadTable(innervorgang);
//                        if (btnDetails.isSelected()) {
//                            loadDetails(innervorgang);
//                        }
//                    }
//                });
//            }
//            em.close();
//        }
//    }
//
//    protected void addVorgaengeFuerMA() {
//        EntityManager em = OPDE.createEM();
//        List<Users> listeUser = em.createNamedQuery("Users.findByStatusSorted").setParameter("status", 1).getResultList();
//
//        JXTaskPane allmapanel = new JXTaskPane("nach MitarbeiterInnen");
//        allmapanel.setCollapsed(true);
//
//        for (Users user : listeUser) {
//
//            Query query = em.createNamedQuery("QProcess.findActiveByBesitzer");
//            query.setParameter("besitzer", user);
//            List<QProcess> listVorgaenge = query.getResultList();
//            Iterator<QProcess> it = listVorgaenge.iterator();
//
//            if (!listVorgaenge.isEmpty()) {
//                JXTaskPane mapanel = new JXTaskPane(user.getNachname() + ", " + user.getVorname());
//                mapanel.setCollapsed(true);
//
//                while (it.hasNext()) {
//                    final QProcess innervorgang = it.next();
//                    OPDE.debug(innervorgang);
//                    mapanel.add(new AbstractAction() {
//                        {
//                            String titel = innervorgang.getTitel();
//                            if (innervorgang.getResident() != null) {
//                                titel += " [" + innervorgang.getResident().getBWKennung() + "]";
//                            }
//                            putValue(Action.NAME, titel);
//                        }
//
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//                            loadTable(innervorgang);
//                            loadDetails(innervorgang);
//                        }
//                    });
//                }
//                allmapanel.add(mapanel);
//            }
//
//        }
//
//        panelSearch.add(allmapanel);
//        em.close();
//    }
//
//    protected void addMeineVorgaenge() {
//        pnlMyVorgaenge = new JXTaskPane("Meine alten Vorgänge");
//        pnlMyVorgaenge.setSpecial(true);
//        pnlMyVorgaenge.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/identity.png")));
//        //pnlVorgaengeRunningOut.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/redled.png")));
//        pnlMyVorgaenge.setCollapsed(false);
//        loadMeineVorgaenge();
//
//        pnlMyVorgaenge.addPropertyChangeListener("collapsed", new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                if (!(Boolean) evt.getNewValue()) {
//                    loadMeineVorgaenge();
//                } else {
//                    pnlMyVorgaenge.removeAll();
//                }
//            }
//        });
//        panelSearch.add(pnlMyVorgaenge);
//    }
//
//
//    protected void loadAllVorgaenge() {
//
//        if (pnlAlleVorgaenge.isEnabled()) {
//            EntityManager em = OPDE.createEM();
//            Query query = em.createNamedQuery("QProcess.findAllActiveSorted");
//            ArrayList<QProcess> alleAktiven = new ArrayList(query.getResultList());
//
//            Iterator<QProcess> it = alleAktiven.iterator();
//
//            while (it.hasNext()) {
//                final QProcess innervorgang = it.next();
//                pnlAlleVorgaenge.add(new AbstractAction() {
//                    {
//                        putValue(Action.NAME, innervorgang.getTitel());
//                        putValue(Action.SHORT_DESCRIPTION, (innervorgang.getResident() == null ? "allgemeiner Vorgang" : innervorgang.getResident().getNachname()));
//                    }
//
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        loadTable(innervorgang);
//                        if (btnDetails.isSelected()) {
//                            loadDetails(innervorgang);
//                        }
//                    }
//                });
//
//            }
//            em.close();
//        }
//    }
//
//
//    protected void loadVorgaengeRunningOut() {
//
//        if (pnlVorgaengeRunningOut.isEnabled()) {
//            EntityManager em = OPDE.createEM();
//            Query query = em.createNamedQuery("QProcess.findActiveRunningOut");
//            query.setParameter("wv", SYSCalendar.addDate(new Date(), 4)); // 4 Tage von heute aus gerechnet.
//            ArrayList<QProcess> vorgaenge = new ArrayList(query.getResultList());
//
//            Iterator<QProcess> it = vorgaenge.iterator();
//
//            while (it.hasNext()) {
//                final QProcess innervorgang = it.next();
//                pnlVorgaengeRunningOut.add(new AbstractAction() {
//                    {
//                        putValue(Action.NAME, innervorgang.getTitel());
//                        putValue(Action.SHORT_DESCRIPTION, (innervorgang.getResident() == null ? "allgemeiner Vorgang" : innervorgang.getResident().getNachname()));
//                    }
//
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        loadTable(innervorgang);
//                        if (btnDetails.isSelected()) {
//                            loadDetails(innervorgang);
//                        }
//                    }
//                });
//
//            }
//            em.close();
//        }
//    }

//    protected void loadMeineVorgaenge() {
//        EntityManager em = OPDE.createEM();
//        Query query = em.createNamedQuery("Vorgaenge.findActiveByBesitzer");
//        query.setParameter("besitzer", OPDE.getLogin().getUser());
//        ArrayList<QProcess> byBesitzer = new ArrayList(query.getResultList());
//
//        Iterator<QProcess> it = byBesitzer.iterator();
//
//        while (it.hasNext()) {
//
//            final QProcess innervorgang = it.next();
//
//            pnlMyVorgaenge.add(new AbstractAction() {
//
//                {
//                    String titel = innervorgang.getTitel();
//                    if (innervorgang.getBewohner() != null) {
//                        titel += " [" + innervorgang.getBewohner().getBWKennung() + "]";
//                    }
//                    putValue(Action.NAME, titel);
//                    putValue(Action.SHORT_DESCRIPTION, (innervorgang.getBewohner() == null ? "allgemeiner Vorgang" : innervorgang.getBewohner().getNachname()));
//                }
//
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    loadTable(innervorgang);
//                    if (btnDetails.isSelected()) {
//                        loadDetails(innervorgang);
//                    }
//                }
//            });
//
//        }
//
//        pnlMyVorgaenge.setTitle("Meine Vorgänge (" + byBesitzer.size() + ")");
//        em.close();
//    }
//
//    protected void loadMeineInaktivenVorgaenge() {
//        EntityManager em = OPDE.createEM();
//        Query query = em.createNamedQuery("Vorgaenge.findInactiveByBesitzer");
//        query.setParameter("besitzer", OPDE.getLogin().getUser());
//
//        ArrayList<QProcess> proceses = new ArrayList(query.getResultList());
//
//        Iterator<QProcess> it = proceses.iterator();
//
//        while (it.hasNext()) {
//            final QProcess innervorgang = it.next();
//            pnlMeineAltenVorgaenge.add(new AbstractAction() {
//                {
//                    putValue(Action.NAME, innervorgang.getTitel());
//                    putValue(Action.SHORT_DESCRIPTION, (innervorgang.getBewohner() == null ? "allgemeiner Vorgang" : innervorgang.getBewohner().getNachname()));
//                }
//
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    loadTable(innervorgang);
//                    if (btnDetails.isSelected()) {
//                        loadDetails(innervorgang);
//                    }
//                }
//            });
//
//        }
//
//        pnlMeineAltenVorgaenge.setTitle("Meine alten Vorgänge (" + proceses.size() + ")");
//        em.close();
//    }
//
//
//    protected void loadDetails(QProcess process) {
//        lblVorgang.setText(process.getTitel() + " [" + (process.getBewohner() == null ? "allgemein" : process.getBewohner().getBWKennung()) + "]");
//
//        // Wenn nötig, laufende Operation abbrechen und obere Knopfreihe anzeigen.
//        if (laufendeOperation != LAUFENDE_OPERATION_NICHTS) {
//            laufendeOperation = LAUFENDE_OPERATION_NICHTS;
//            lblMessage.setText(null);
//            SYSTools.showSide(splitButtonsCenter, SYSTools.LEFT_UPPER_SIDE, speedSlow);
//        }
//
//        if (btnDetails.isSelected()) {
//            ignoreEvents = true;
//            txtTitel.setText(process.getTitel());
//            lblBW.setText(process.getBewohner() == null ? "Allgemeiner Vorgang" : ResidentTools.getLabelText(process.getBewohner()));
//            lblStart.setText(DateFormat.getDateInstance().format(process.getVon()));
//            jdcWV.setDate(process.getWv());
//            lblEnde.setText(process.getBis().equals(SYSConst.DATE_BIS_AUF_WEITERES) ? "noch nicht abgeschlossen" : DateFormat.getDateInstance().format(process.getBis()));
//            lblCreator.setText(process.getErsteller().getNameUndVorname());
//            lblOwner.setText(process.getBesitzer().getNameUndVorname());
//            cmbKat.setSelectedItem(process.getKategorie());
//            lblPDCA.setText(QProcessTools.PDCA[process.getPdca()]);
//            listOwner.setSelectedValue(process.getBesitzer(), true);
//
//            // ACLs
//            txtTitel.setEditable(OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER));
//            ignoreEvents = false;
//        }
//
//    }
//
//    private void btnAssignItemStateChanged(ItemEvent e) {
//        double percent = btnAssign.isSelected() ? 0.65d : 1.0d;
//        SYSTools.showSide(splitDetailsOwner, percent, speedSlow);
//    }
//
//    private void btnDetailsItemStateChanged(ItemEvent e) {
//        splitTDPercent = btnDetails.isSelected() ? 0.4d : 1.0d;
//        SYSTools.showSide(splitTableDetails, splitTDPercent, speedSlow);
//        loadDetails(aktuellerVorgang);
//        btnEndReactivate.setEnabled(!btnDetails.isSelected() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.CANCEL));
//    }
//
//    private void btnApplyActionPerformed(ActionEvent e) {
//
//        switch (laufendeOperation) {
//            case LAUFENDE_OPERATION_BERICHT_EINGABE: {
//                PReport vbericht = new PReport(pnlEditor.getHTML(), PReportTools.VBERICHT_ART_USER, aktuellerVorgang);
//                EntityTools.persist(vbericht);
//                //((TMElement) tblElements.getModel()).addVBericht(vbericht);
//                loadTable(aktuellerVorgang);
//                splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow);
//                break;
//            }
//            case LAUFENDE_OPERATION_VORGANG_BEARBEITEN: {
//                EntityManager em = OPDE.createEM();
//                try {
//                    em.getTransaction().begin();
//                    if (pdcaChanged) {
//                        PReport vbericht = new PReport("PDCA Stufe erhöht auf: " + QProcessTools.PDCA[aktuellerVorgang.getPdca()], PReportTools.VBERICHT_ART_PDCA, aktuellerVorgang);
//                        vbericht.setPdca(aktuellerVorgang.getPdca());
//                        em.persist(vbericht);
//                    }
//                    aktuellerVorgang = em.merge(aktuellerVorgang);
//                    em.getTransaction().commit();
//                } catch (Exception exc) {
//                    em.getTransaction().rollback();
//                    OPDE.fatal(exc);
//                } finally {
//                    em.close();
//                }
//
//                pdcaChanged = false;
//                btnPDCAPlus.setEnabled(true);
//
//                break;
//            }
//            default: {
//
//            }
//        }
//
//        lblMessage.setText(null);
//        textmessageTL.cancel();
//        SYSTools.showSide(splitButtonsCenter, SYSTools.LEFT_UPPER_SIDE, speedFast);
//        laufendeOperation = LAUFENDE_OPERATION_NICHTS;
//
//    }
//
//    private void btnCancelActionPerformed(ActionEvent e) {
////        if (savePressedOnce) {
////            btnApply.setText(null);
////            alternatingFlash.stop();
////            alternatingFlash = null;
////        }
////
////        loadDetails(aktuellerVorgang);
////        setDetailsChanged(false);
////        savePressedOnce = false;
//        switch (laufendeOperation) {
//            case LAUFENDE_OPERATION_BERICHT_EINGABE: {
//                splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow);
//                break;
//            }
//            case LAUFENDE_OPERATION_VORGANG_BEARBEITEN: {
//                btnPDCAPlus.setEnabled(true);
//                EntityManager em = OPDE.createEM();
//                em.refresh(aktuellerVorgang);
//                em.close();
//                loadDetails(aktuellerVorgang);
//                break;
//            }
//            default: {
//
//            }
//        }
//        lblMessage.setText(null);
//        textmessageTL.cancel();
//        SYSTools.showSide(splitButtonsCenter, SYSTools.LEFT_UPPER_SIDE, speedFast);
//        laufendeOperation = LAUFENDE_OPERATION_NICHTS;
//    }
//
//    private void listOwnerValueChanged(ListSelectionEvent e) {
//        if (ignoreEvents) return;
//        setCenterButtons2Edit("Änderungen speichern ?");
//        laufendeOperation = LAUFENDE_OPERATION_VORGANG_BEARBEITEN;
//        aktuellerVorgang.setBesitzer((Users) listOwner.getSelectedValue());
//        lblEnde.setText(aktuellerVorgang.getBesitzer().getNameUndVorname());
//        btnAssign.setSelected(false);
////        split2Percent = 100;
////        new SplitAnimator(splitPane2, split2Percent).execute();
//
//    }
//
//
//    private void cmbKatItemStateChanged(ItemEvent e) {
//        if (ignoreEvents) return;
//        setCenterButtons2Edit("Änderungen speichern ?");
//        laufendeOperation = LAUFENDE_OPERATION_VORGANG_BEARBEITEN;
//        aktuellerVorgang.setKategorie((PCat) cmbKat.getSelectedItem());
//
//    }
//
//    private void jdcWVPropertyChange(PropertyChangeEvent e) {
//        if (ignoreEvents) return;
//        if (e.getPropertyName().equals("date")) {
//            setCenterButtons2Edit("Änderungen speichern ?");
//            laufendeOperation = LAUFENDE_OPERATION_VORGANG_BEARBEITEN;
//            aktuellerVorgang.setWv(jdcWV.getDate());
//        }
//    }
//
//    private void txtTitelCaretUpdate(CaretEvent e) {
//        if (ignoreEvents) return;
//        setCenterButtons2Edit("Änderungen speichern ?");
//        laufendeOperation = LAUFENDE_OPERATION_VORGANG_BEARBEITEN;
//        aktuellerVorgang.setTitel(txtTitel.getText());
//        lblVorgang.setText(aktuellerVorgang.getTitel());
//    }
//
//    private void btnPDCAPlusActionPerformed(ActionEvent e) {
//        setCenterButtons2Edit("Änderungen speichern ?");
//        laufendeOperation = LAUFENDE_OPERATION_VORGANG_BEARBEITEN;
//        aktuellerVorgang.setPdca(QProcessTools.incPDCA(aktuellerVorgang.getPdca()));
//        lblPDCA.setText(QProcessTools.PDCA[aktuellerVorgang.getPdca()]);
//        btnPDCAPlus.setEnabled(false);
//        pdcaChanged = true;
//    }

//    /**
//     * Wenn möglich enabled diese Methode die entsprechende Komponente.
//     * Hängt ab von der Gruppenmitgliedschaft des Users.
//     *
//     * @param comp
//     */
//    protected void enable(JComponent comp) {
//        boolean answer = false;
//        if (authorizationMap.containsKey(comp)) {
//            ArrayList<Short> list = authorizationMap.get(comp);
//            for (Iterator<Short> itAcl = list.iterator(); !answer && itAcl.hasNext(); ) {
//                short acl = itAcl.next();
//                // ist user Mitglied in einer der zugelassenen ACL Gruppen ?
//                answer = OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, acl);
//            }
//        } else {
//            // Im Zweifel zulassen.
//            answer = true;
//        }
//
//        comp.setEnabled(answer);
//    }

//    private void btnEndReactivateActionPerformed(ActionEvent e) {
//        if (aktuellerVorgang.isAbgeschlossen()) {
//            QProcessTools.reopenVorgang(aktuellerVorgang);
//            //new TextFlash(lblMessage, "Vorgang wieder geöffnet", true, false, 600).execute();
//            btnEndReactivate.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/shutdown.png")));
//            btnEndReactivate.setToolTipText("Vorgang abschließen");
//
//        } else {
//            QProcessTools.endVorgang(aktuellerVorgang);
//            //new TextFlash(lblMessage, "Vorgang abgeschlossen", true, false, 600).execute();
//            btnEndReactivate.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/reload.png")));
//            btnEndReactivate.setToolTipText("Vorgang wieder aktivieren");
//
//        }
//
//        if (!pnlMyVorgaenge.isCollapsed()) {
//            pnlMyVorgaenge.removeAll();
//            loadMeineVorgaenge();
//        }
//
//        if (!pnlMeineAltenVorgaenge.isCollapsed()) {
//            pnlMeineAltenVorgaenge.removeAll();
//            loadMeineInaktivenVorgaenge();
//        }
//
//
//    }
//
//
//    private void btnTakeOverActionPerformed(ActionEvent e) {
//        aktuellerVorgang.setBesitzer(OPDE.getLogin().getUser());
//        lblEnde.setText(aktuellerVorgang.getBesitzer().getNameUndVorname());
//
//    }

//    private void btnDeleteActionPerformed(ActionEvent e) {
//        if (savePressedOnce) { // Wurde bereits einmal gedrückt. Also ist das hier die Bestätigung.
//            alternatingFlash.stop();
//            btnDelete.setText(null);
//            alternatingFlash = null;
//            btnCancel1.setVisible(false);
//            QProcessTools.deleteVorgang(aktuellerVorgang);
//            btnDetails.setSelected(false);
//            loadTable(null);
//        } else {
//            btnCancel1.setVisible(true);
//            btnDelete.setText("WIRKLICH ?");
//            alternatingFlash = new ComponentAlternatingFlash(btnDelete, btnCancel1, new ImageIcon(getClass().getResource("/artwork/22x22/help3.png")));
//            alternatingFlash.execute();
//        }
//
//        setLowerMiddleButtons(savePressedOnce);
//        btnDelElement.setEnabled(!savePressedOnce);
//
//        savePressedOnce = !savePressedOnce;
//    }

//    protected void loadTable(QProcess process) {
//        aktuellerVorgang = process;
//
//        if (process == null) {
//            tblElements.setModel(new DefaultTableModel());
//        } else {
//
//            lblVorgang.setText(process.getTitel() + " [" + (process.getBewohner() == null ? "allgemein" : process.getBewohner().getBWKennung()) + "]");
//
//            List<QProcessElement> elementQs = new ArrayList<QProcessElement>(QProcessTools.findElementeByVorgang(process, btnSystemInfo.isSelected()));
//
//            tblElements.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
//
//            tblElements.setModel(new TMElement(elementQs));
//            tblElements.getColumnModel().getColumn(TMElement.COL_PIT).setCellRenderer(new RNDHTML());
//            tblElements.getColumnModel().getColumn(TMElement.COL_PDCA).setCellRenderer(new RNDHTML());
//            tblElements.getColumnModel().getColumn(TMElement.COL_CONTENT).setCellRenderer(new RNDHTML());
//            tblElements.getColumnModel().getColumn(TMElement.COL_PIT).setHeaderValue("Datum / MA");
//            tblElements.getColumnModel().getColumn(TMElement.COL_PDCA).setHeaderValue("PDCA");
//            tblElements.getColumnModel().getColumn(TMElement.COL_CONTENT).setHeaderValue("Inhalt");
//
////            if (btnDelElement.isSelected()) {
////                // ButtonsRenderer und ButtonsEditor sind dafür da, damit man in den Tabellen Spalten Knöpfe einfügen kann
////                // Es gibt immer einen Cancel Button und einen Menge von Funktionsknöpfe. Was diese Funktionsknöpfe
////                // machen sollen, steht in den Actionlistenern, die man mit übergibt.
////                tblElements.addColumn(new TableColumn(TMElement.COL_OPERATIONS, 0,
////                        //
////                        new ButtonsRenderer(new JButton(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/cancel.png"))),
////                                getTableButtons()),
////                        //
////                        new ButtonsEditor(tblElements, new JButton(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/cancel.png"))),
////                                getTableButtons()
////                        )
////                )
////                );
////                tblElements.getColumnModel().getColumn(TMElement.COL_OPERATIONS).setHeaderValue("--");
////                new TableColumnSizeAnimator(jspElements, tblElements.getColumnModel().getColumn(TMElement.COL_OPERATIONS), 150).execute();
////                //OPDE.debug(tblElements.getColumnCount());
////            }
//
//            jspElements.dispatchEvent(new ComponentEvent(jspElements, ComponentEvent.COMPONENT_RESIZED));
//
//            if (aktuellerVorgang.isAbgeschlossen()) {
//                btnEndReactivate.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/reload.png")));
//                btnEndReactivate.setToolTipText("Vorgang wieder aktivieren");
//            } else {
//                btnEndReactivate.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/shutdown.png")));
//                btnEndReactivate.setToolTipText("Vorgang abschließen");
//            }
//
//
//        }
//
//        btnEndReactivate.setEnabled(process != null && !btnDetails.isSelected() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.CANCEL));
//        //btnDelete.setEnabled(process != null && !btnDetails.isSelected() && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE));
//
//        btnAddBericht.setEnabled(process != null && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
//        btnSystemInfo.setEnabled(process != null && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER));
//        //btnDelElement.setEnabled(process != null && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER));
//        btnDetails.setEnabled(process != null && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
//        btnPrint.setEnabled(process != null && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT));
//
//        OPDE.debug(tblElements.getColumnCount());
//
//    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspProcess = new JScrollPane();
        cpProcess = new CollapsiblePanes();

        //======== this ========
        setBorder(new LineBorder(Color.black, 1, true));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspProcess ========
        {

            //======== cpProcess ========
            {
                cpProcess.setLayout(new BoxLayout(cpProcess, BoxLayout.X_AXIS));
            }
            jspProcess.setViewportView(cpProcess);
        }
        add(jspProcess);
    }// </editor-fold>//GEN-END:initComponents


    @Override
    public void cleanup() {

    }

    @Override
    public void switchResident(Resident resident) {
        this.resident = resident;
        reloadDisplay();
    }


    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane();
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

        GUITools.addAllComponents(mypanel, addCommands());
        GUITools.addAllComponents(mypanel, addFilters());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }

    private java.util.List<Component> addFilters() {
        java.util.List<Component> list = new ArrayList<Component>();
        /***
         *      _   _      ____ _                    _
         *     | |_| |__  / ___| | ___  ___  ___  __| |
         *     | __| '_ \| |   | |/ _ \/ __|/ _ \/ _` |
         *     | |_| |_) | |___| | (_) \__ \  __/ (_| |
         *      \__|_.__/ \____|_|\___/|___/\___|\__,_|
         *
         */
        tbClosed = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.showclosed"));
        tbClosed.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase) return;
//                SYSPropsTools.storeState(internalClassID + ":tbShowReplaced", tbShowReplaced);
                buildPanel();
            }
        });
//        SYSPropsTools.restoreState(internalClassID + ":tbShowReplaced", tbShowReplaced);
        tbClosed.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbClosed);

        return list;
    }


    private void buildPanel() {
        cpProcess.removeAll();
        cpProcess.setLayout(new JideBoxLayout(cpProcess, JideBoxLayout.Y_AXIS));
        for (QProcess process : processList) {
            if (tbClosed.isSelected() || !process.isClosed()) {
                CollapsiblePane cp = qProcessMap.get(process);

//                JPanel elementPanel = new JPanel();
//                elementPanel.setLayout(new VerticalLayout());
//                elementPanel.add(new CollapsiblePane("test"));
//                elementPanel.add(new CollapsiblePane("test2"));
////                for (final QProcessElement element : qProcess2ElementMap.get(process)) {
////                    elementPanel.add(new CollapsiblePane("test"));
////                    elementPanel.add(new CollapsiblePane("test2"));
////                }
//                cp.setContentPane(elementPanel);
//                cp.setOpaque(false);
                cpProcess.add(cp);
            }
        }

        cpProcess.addExpansion();
    }

    private java.util.List<Component> addCommands() {

        java.util.List<Component> list = new ArrayList<Component>();

        /***
         *      _     _            _       _     _
         *     | |__ | |_ _ __    / \   __| | __| |
         *     | '_ \| __| '_ \  / _ \ / _` |/ _` |
         *     | |_) | |_| | | |/ ___ \ (_| | (_| |
         *     |_.__/ \__|_| |_/_/   \_\__,_|\__,_|
         *
         */
        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            final JideButton btnAdd = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnadd"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), null);
            btnAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                }
            });
            list.add(btnAdd);

        }

        return list;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspProcess;
    private CollapsiblePanes cpProcess;
    // End of variables declaration//GEN-END:variables
}
