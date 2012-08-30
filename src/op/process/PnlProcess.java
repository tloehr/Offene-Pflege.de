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
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import entity.system.Users;
import entity.system.UsersTools;
import entity.files.SYSFilesTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.process.*;
import op.OPDE;
import op.events.TaskPaneContentChangedListener;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateMidnight;
import org.pushingpixels.trident.Timeline;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

/**
 * @author tloehr
 */
public class PnlProcess extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.qprocesses";

    private Resident resident;
    private boolean initPhase = false;
    private JToggleButton tbClosed, tbSystem;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    private HashMap<QProcess, CollapsiblePane> qProcessCollapsiblePaneHashMap;
    private HashMap<QProcess, ArrayList<QProcessElement>> qProcess2ElementMap;
    private HashMap<QProcess, CollapsiblePane> qProcessMap;
    private HashMap<QProcessElement, CollapsiblePane> elementMap;
    private List<QProcess> processList;
    private int MAX_TEXT_LENGTH = 65;

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

    public PnlProcess(JScrollPane jspSearch) {
        initPhase = true;
        this.jspSearch = jspSearch;
        initComponents();
        initPanel();
        processList = QProcessTools.getProcesses4(OPDE.getLogin().getUser());
        reloadDisplay();
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
            for (QProcess qProcess : processList) {
                qProcessMap.put(qProcess, createCP4(qProcess));
            }

            buildPanel();
        }
        initPhase = false;
    }


    private CollapsiblePane createCP4(final QProcess qProcess) {
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
        JideButton btnReport = GUITools.createHyperlinkButton("<html><font size=+1>" +
                SYSTools.left(qProcess.getTitle(), MAX_TEXT_LENGTH) +
                " <b>" +
                (qProcess.isCommon() ?
                        "" :
                        ResidentTools.getBWLabelTextKompakt(qProcess.getResident())) +
                "</b>, " +
                "[" +
                DateFormat.getDateInstance(DateFormat.SHORT).format(qProcess.getFrom()) + "&rarr;" +
                (qProcess.isClosed() ? DateFormat.getDateInstance(DateFormat.SHORT).format(qProcess.getTo()) : "|") +
                "]" +
                "</font></html>", qProcess.isRevisionDue() ? SYSConst.icon22infored : null, null);


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


        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {


            /***
             *      _     _            _       _     _ ____  ____                       _
             *     | |__ | |_ _ __    / \   __| | __| |  _ \|  _ \ ___ _ __   ___  _ __| |_
             *     | '_ \| __| '_ \  / _ \ / _` |/ _` | |_) | |_) / _ \ '_ \ / _ \| '__| __|
             *     | |_) | |_| | | |/ ___ \ (_| | (_| |  __/|  _ <  __/ |_) | (_) | |  | |_
             *     |_.__/ \__|_| |_/_/   \_\__,_|\__,_|_|   |_| \_\___| .__/ \___/|_|   \__|
             *                                                        |_|
             */
            final JButton btnAddPReport = new JButton(SYSConst.icon22add);
            btnAddPReport.setPressedIcon(SYSConst.icon22addPressed);
            btnAddPReport.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnAddPReport.setContentAreaFilled(false);
            btnAddPReport.setBorder(null);
            btnAddPReport.setToolTipText(OPDE.lang.getString(internalClassID + ".btnaddpreport.tooltip"));
            btnAddPReport.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    final JidePopup popup = new JidePopup();
                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.PAGE_AXIS));
                    final JButton btnSave = new JButton(SYSConst.icon22apply);
                    final JTextArea editor = new JTextArea("", 10, 40);
                    btnSave.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {

                            if (editor.getText().trim().isEmpty()) {
                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.emptyentry")));
                                return;
                            }

                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                QProcess myProcess = em.merge(qProcess);
                                if (!myProcess.isCommon()) {
                                    em.lock(em.merge(myProcess.getResident()), LockModeType.OPTIMISTIC);
                                }
                                em.lock(myProcess, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                PReport pReport = em.merge(new PReport(editor.getText().trim(), PReportTools.PREPORT_TYPE_USER, myProcess));
                                myProcess.getPReports().add(pReport);

                                em.getTransaction().commit();

                                qProcessMap.remove(qProcess);
                                qProcessMap.put(myProcess, createCP4(myProcess));
                                try {
                                    qProcessMap.get(myProcess).setCollapsed(false);
                                } catch (PropertyVetoException e) {

                                }
                                buildPanel();
                            } catch (OptimisticLockException ole) {
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
                                }
                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                            } catch (Exception e) {
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                OPDE.fatal(e);
                            } finally {
                                em.close();
                            }
                        }
                    });

                    editor.setLineWrap(true);
                    editor.setWrapStyleWord(true);
                    editor.setEditable(true);
                    JScrollPane jspEditor = new JScrollPane(editor);
                    JPanel pnl = new JPanel(new BorderLayout(10, 10));

                    pnl.add(jspEditor, BorderLayout.CENTER);
                    JPanel buttonPanel = new JPanel();
                    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
                    buttonPanel.add(btnSave);
                    pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
                    pnl.add(buttonPanel, BorderLayout.SOUTH);

                    popup.setOwner(btnAddPReport);
                    popup.getContentPane().add(pnl);

                    popup.setDefaultFocusComponent(editor);
                    GUITools.showPopup(popup, SwingUtilities.WEST);
                }
            });
            btnAddPReport.setEnabled(!qProcess.isClosed());
            titlePanelright.add(btnAddPReport);

        }


        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {
            if (!qProcess.isClosed()) {
                /***
                 *      _     _          ____ _
                 *     | |__ | |_ _ __  / ___| | ___  ___  ___
                 *     | '_ \| __| '_ \| |   | |/ _ \/ __|/ _ \
                 *     | |_) | |_| | | | |___| | (_) \__ \  __/
                 *     |_.__/ \__|_| |_|\____|_|\___/|___/\___|
                 *
                 */
                final JButton btnClose = new JButton(SYSConst.icon22stop);
                btnClose.setPressedIcon(SYSConst.icon22stopPressed);
                btnClose.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnClose.setContentAreaFilled(false);
                btnClose.setBorder(null);
                btnClose.setToolTipText(OPDE.lang.getString(internalClassID + ".btnclose.tooltip"));
                btnClose.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        new DlgYesNo(OPDE.lang.getString(internalClassID + ".question.close") + "<p>" + qProcess.getTitle() + "</p>", SYSConst.icon48stop, new Closure() {
                            @Override
                            public void execute(Object answer) {
                                if (answer.equals(JOptionPane.YES_OPTION)) {
                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();

                                        QProcess myProcess = em.merge(qProcess);
                                        if (!myProcess.isCommon()) {
                                            em.lock(em.merge(myProcess.getResident()), LockModeType.OPTIMISTIC);
                                        }
                                        em.lock(myProcess, LockModeType.OPTIMISTIC);

                                        PReport pReport = em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_CLOSE), PReportTools.PREPORT_TYPE_CLOSE, qProcess));

                                        myProcess.setTo(new Date());
                                        myProcess.getPReports().add(pReport);
                                        em.getTransaction().commit();
                                        processList.remove(qProcess);
                                        processList.add(myProcess);
//                                        Collections.sort(processList);
                                        qProcessMap.remove(qProcess);
                                        qProcessMap.put(myProcess, createCP4(myProcess));
                                        buildPanel();
                                    } catch (OptimisticLockException ole) {
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                            OPDE.getMainframe().emptyFrame();
                                            OPDE.getMainframe().afterLogin();
                                        }
                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                    } catch (Exception e) {
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        OPDE.fatal(e);
                                    } finally {
                                        em.close();
                                    }
                                }
                            }
                        });
                    }
                });
                btnClose.setEnabled(qProcess.isYours());
                titlePanelright.add(btnClose);
            } else {
                /***
                 *      _     _         ____
                 *     | |__ | |_ _ __ |  _ \ ___  ___  _ __   ___ _ __
                 *     | '_ \| __| '_ \| |_) / _ \/ _ \| '_ \ / _ \ '_ \
                 *     | |_) | |_| | | |  _ <  __/ (_) | |_) |  __/ | | |
                 *     |_.__/ \__|_| |_|_| \_\___|\___/| .__/ \___|_| |_|
                 *                                     |_|
                 */
                final JButton btnClose = new JButton(SYSConst.icon22play);
                btnClose.setPressedIcon(SYSConst.icon22playPressed);
                btnClose.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnClose.setContentAreaFilled(false);
                btnClose.setBorder(null);
                btnClose.setToolTipText(OPDE.lang.getString(internalClassID + ".btnclose.tooltip"));
                btnClose.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        new DlgYesNo(OPDE.lang.getString(internalClassID + ".question.reopen") + "<p>" + qProcess.getTitle() + "</p>", SYSConst.icon48play, new Closure() {
                            @Override
                            public void execute(Object answer) {
                                if (answer.equals(JOptionPane.YES_OPTION)) {
                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();

                                        QProcess myProcess = em.merge(qProcess);
                                        if (!myProcess.isCommon()) {
                                            em.lock(em.merge(myProcess.getResident()), LockModeType.OPTIMISTIC);
                                        }
                                        em.lock(myProcess, LockModeType.OPTIMISTIC);
                                        PReport pReport = em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_REOPEN), PReportTools.PREPORT_TYPE_REOPEN, qProcess));
                                        myProcess.setTo(SYSConst.DATE_BIS_AUF_WEITERES);
                                        myProcess.getPReports().add(pReport);
                                        em.getTransaction().commit();
                                        processList.remove(qProcess);
                                        myProcess.setOwner(em.merge(OPDE.getLogin().getUser()));
                                        processList.add(myProcess);
//                                        Collections.sort(processList);
                                        qProcessMap.remove(qProcess);
                                        qProcessMap.put(myProcess, createCP4(myProcess));
                                        buildPanel();
                                    } catch (OptimisticLockException ole) {
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                            OPDE.getMainframe().emptyFrame();
                                            OPDE.getMainframe().afterLogin();
                                        }
                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                    } catch (Exception e) {
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        OPDE.fatal(e);
                                    } finally {
                                        em.close();
                                    }
                                }
                            }
                        });
                    }
                });
                btnClose.setEnabled(OPDE.isAdmin());
                titlePanelright.add(btnClose);
            }

            /***
             *      _     _         ____       _      _
             *     | |__ | |_ _ __ |  _ \  ___| | ___| |_ ___
             *     | '_ \| __| '_ \| | | |/ _ \ |/ _ \ __/ _ \
             *     | |_) | |_| | | | |_| |  __/ |  __/ ||  __/
             *     |_.__/ \__|_| |_|____/ \___|_|\___|\__\___|
             *
             */
            final JButton btnDelete = new JButton(SYSConst.icon22delete);
            btnDelete.setPressedIcon(SYSConst.icon22deletePressed);
            btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnDelete.setContentAreaFilled(false);
            btnDelete.setBorder(null);
            btnDelete.setToolTipText(OPDE.lang.getString(internalClassID + ".btndelete.tooltip"));
            btnDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgYesNo(OPDE.lang.getString(internalClassID + ".question.delete") + "<p>" + qProcess.getTitle() + "</p>", SYSConst.icon48delete, new Closure() {
                        @Override
                        public void execute(Object answer) {
                            if (answer.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    QProcess myProcess = em.merge(qProcess);
                                    if (!myProcess.isCommon()) {
                                        em.lock(em.merge(myProcess.getResident()), LockModeType.OPTIMISTIC);
                                    }
                                    em.lock(myProcess, LockModeType.OPTIMISTIC);

                                    em.remove(myProcess);

                                    for (PReport report : myProcess.getPReports()) {
                                        em.remove(report);
                                    }
                                    for (SYSNR2PROCESS att : myProcess.getAttachedNReportConnections()) {
                                        em.remove(att);
                                    }
                                    for (SYSNP2PROCESS att : myProcess.getAttachedNursingProcesses()) {
                                        em.remove(att);
                                    }
                                    for (SYSINF2PROCESS att : myProcess.getAttachedInfos()) {
                                        em.remove(att);
                                    }
                                    for (SYSPRE2PROCESS att : myProcess.getAttachedPrescriptions()) {
                                        em.remove(att);
                                    }
                                    for (SYSVAL2PROCESS att : myProcess.getAttachedResidentValues()) {
                                        em.remove(att);
                                    }

                                    em.getTransaction().commit();
                                    processList.remove(qProcess);
                                    qProcessMap.remove(qProcess);
                                    buildPanel();
                                } catch (OptimisticLockException ole) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                        OPDE.getMainframe().emptyFrame();
                                        OPDE.getMainframe().afterLogin();
                                    }
                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                            }
                        }
                    });
                }
            });
            btnDelete.setEnabled(OPDE.isAdmin());
            titlePanelright.add(btnDelete);


            /***
             *      _     _         ____       _       _
             *     | |__ | |_ _ __ |  _ \ _ __(_)_ __ | |_
             *     | '_ \| __| '_ \| |_) | '__| | '_ \| __|
             *     | |_) | |_| | | |  __/| |  | | | | | |_
             *     |_.__/ \__|_| |_|_|   |_|  |_|_| |_|\__|
             *
             */
            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
                final JButton btnPrint = new JButton(SYSConst.icon22print);
                btnPrint.setPressedIcon(SYSConst.icon22printPressed);
                btnPrint.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnPrint.setContentAreaFilled(false);
                btnPrint.setBorder(null);
                btnPrint.setToolTipText(OPDE.lang.getString(internalClassID + ".btnrevision.tooltip"));
                btnPrint.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        String html = QProcessTools.getAsHTML(qProcess);
                        html += QProcessTools.getElementsAsHTML(qProcess, tbSystem.isSelected());
                        SYSFilesTools.print(html, true);
                    }
                });
                titlePanelright.add(btnPrint);
            }

            /***
             *      _     _         ____            _     _
             *     | |__ | |_ _ __ |  _ \ _____   _(_)___(_) ___  _ __
             *     | '_ \| __| '_ \| |_) / _ \ \ / / / __| |/ _ \| '_ \
             *     | |_) | |_| | | |  _ <  __/\ V /| \__ \ | (_) | | | |
             *     |_.__/ \__|_| |_|_| \_\___| \_/ |_|___/_|\___/|_| |_|
             *
             */
            final JButton btnRevision = new JButton(SYSConst.icon22calendar);
            btnRevision.setPressedIcon(SYSConst.icon22calendarPressed);
            btnRevision.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnRevision.setContentAreaFilled(false);
            btnRevision.setBorder(null);
            btnRevision.setToolTipText(OPDE.lang.getString(internalClassID + ".btnrevision.tooltip"));
            btnRevision.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    final JidePopup popup = new JidePopup();
                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                    final JButton btnSave = new JButton(SYSConst.icon22apply);
                    final JDateChooser editor = new JDateChooser(new DateMidnight().plusWeeks(2).toDate());
                    editor.setFont(SYSConst.ARIAL20);
                    editor.setMinSelectableDate(new DateMidnight().plusDays(1).toDate());
                    btnSave.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {

                            if (editor.getDate() == null) {
                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wrongentry")));
                                return;
                            }

                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                QProcess myProcess = em.merge(qProcess);
                                if (!myProcess.isCommon()) {
                                    em.lock(em.merge(myProcess.getResident()), LockModeType.OPTIMISTIC);
                                }
                                em.lock(myProcess, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                PReport pReport = new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_WV) + ": " + DateFormat.getDateInstance().format(editor.getDate()), PReportTools.PREPORT_TYPE_WV, myProcess);
                                myProcess.setRevision(editor.getDate());
                                myProcess.getPReports().add(pReport);
                                em.getTransaction().commit();
                                qProcessMap.remove(qProcess);
                                qProcessMap.put(myProcess, createCP4(myProcess));
                                try {
                                    qProcessMap.get(myProcess).setCollapsed(false);
                                } catch (PropertyVetoException e) {

                                }
                                buildPanel();
                            } catch (OptimisticLockException ole) {
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
                                }
                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                            } catch (Exception e) {
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                OPDE.fatal(e);
                            } finally {
                                em.close();
                            }
                        }
                    });

                    JPanel pnl = new JPanel(new BorderLayout(10, 10));

                    pnl.add(editor, BorderLayout.CENTER);
                    JPanel buttonPanel = new JPanel();
                    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
                    buttonPanel.add(btnSave);
                    pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
                    pnl.add(buttonPanel, BorderLayout.SOUTH);

                    popup.setOwner(btnRevision);
                    popup.getContentPane().add(pnl);

                    popup.setDefaultFocusComponent(editor);
                    GUITools.showPopup(popup, SwingUtilities.WEST);

                }
            });
            btnRevision.setEnabled(qProcess.isYours() && !qProcess.isClosed());
            titlePanelright.add(btnRevision);


            if (qProcess.isYours()) {
                /***
                 *      _     _         _   _                 _  ___
                 *     | |__ | |_ _ __ | | | | __ _ _ __   __| |/ _ \__   _____ _ __
                 *     | '_ \| __| '_ \| |_| |/ _` | '_ \ / _` | | | \ \ / / _ \ '__|
                 *     | |_) | |_| | | |  _  | (_| | | | | (_| | |_| |\ V /  __/ |
                 *     |_.__/ \__|_| |_|_| |_|\__,_|_| |_|\__,_|\___/  \_/ \___|_|
                 *
                 */
                final JButton btnHandOver = new JButton(SYSConst.icon22give);
                btnHandOver.setPressedIcon(SYSConst.icon22givePressed);
                btnHandOver.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnHandOver.setContentAreaFilled(false);
                btnHandOver.setBorder(null);
                btnHandOver.setToolTipText(OPDE.lang.getString(internalClassID + ".btnhandover.tooltip"));
                btnHandOver.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {

                        final JidePopup popup = new JidePopup();
                        popup.setMovable(false);
                        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.PAGE_AXIS));
                        final JButton btnSave = new JButton(SYSConst.icon22apply);
                        final JList editor = new JList(SYSTools.list2dlm(UsersTools.getUsers(false)));
                        editor.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        editor.setCellRenderer(UsersTools.getUserRenderer());
                        btnSave.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {

                                if (editor.getSelectedValue() == null) {
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.emptyentry")));
                                    return;
                                }

                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    QProcess myProcess = em.merge(qProcess);
                                    Users handOverTo = em.merge((Users) editor.getSelectedValue());
                                    if (!myProcess.isCommon()) {
                                        em.lock(em.merge(myProcess.getResident()), LockModeType.OPTIMISTIC);
                                    }
                                    em.lock(myProcess, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    PReport pReport = em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_SET_OWNERSHIP) + ": " + handOverTo.getFullname(), PReportTools.PREPORT_TYPE_SET_OWNERSHIP, myProcess));
                                    myProcess.getPReports().add(pReport);
                                    myProcess.setOwner(em.merge(handOverTo));
                                    em.getTransaction().commit();

                                    processList.remove(qProcess);
                                    processList.add(myProcess);
//                                    Collections.sort(processList);
                                    qProcessMap.remove(qProcess);
                                    qProcessMap.put(myProcess, createCP4(myProcess));
                                    buildPanel();
                                } catch (OptimisticLockException ole) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                        OPDE.getMainframe().emptyFrame();
                                        OPDE.getMainframe().afterLogin();
                                    }
                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                            }
                        });

                        JScrollPane jspEditor = new JScrollPane(editor);
                        JPanel pnl = new JPanel(new BorderLayout(10, 10));

                        pnl.add(jspEditor, BorderLayout.CENTER);
                        JPanel buttonPanel = new JPanel();
                        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
                        buttonPanel.add(btnSave);
                        pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
                        pnl.add(buttonPanel, BorderLayout.SOUTH);

                        popup.setOwner(btnHandOver);
                        popup.getContentPane().add(pnl);

                        popup.setDefaultFocusComponent(editor);
                        GUITools.showPopup(popup, SwingUtilities.WEST);
                    }
                });
                btnHandOver.setEnabled(!qProcess.isClosed());
                titlePanelright.add(btnHandOver);
            } else {
                /***
                 *      _     _       _____     _         ___
                 *     | |__ | |_ _ _|_   _|_ _| | _____ / _ \__   _____ _ __
                 *     | '_ \| __| '_ \| |/ _` | |/ / _ \ | | \ \ / / _ \ '__|
                 *     | |_) | |_| | | | | (_| |   <  __/ |_| |\ V /  __/ |
                 *     |_.__/ \__|_| |_|_|\__,_|_|\_\___|\___/  \_/ \___|_|
                 *
                 */
                final JButton btbTakeOver = new JButton(SYSConst.icon22take);
                btbTakeOver.setPressedIcon(SYSConst.icon22takePressed);
                btbTakeOver.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btbTakeOver.setContentAreaFilled(false);
                btbTakeOver.setBorder(null);
                btbTakeOver.setToolTipText(OPDE.lang.getString(internalClassID + ".btntakeover.tooltip"));
                btbTakeOver.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        new DlgYesNo(OPDE.lang.getString(internalClassID + ".question.takeover") + "<p>" + qProcess.getTitle() + "</p>", SYSConst.icon48play, new Closure() {
                            @Override
                            public void execute(Object answer) {
                                if (answer.equals(JOptionPane.YES_OPTION)) {
                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();

                                        QProcess myProcess = em.merge(qProcess);
                                        if (!myProcess.isCommon()) {
                                            em.lock(em.merge(myProcess.getResident()), LockModeType.OPTIMISTIC);
                                        }
                                        em.lock(myProcess, LockModeType.OPTIMISTIC);
                                        PReport pReport = em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_SET_OWNERSHIP) + ": " + OPDE.getLogin().getUser().getFullname(), PReportTools.PREPORT_TYPE_SET_OWNERSHIP, qProcess));
                                        myProcess.setOwner(em.merge(OPDE.getLogin().getUser()));
                                        myProcess.getPReports().add(pReport);
                                        myProcess.setOwner(em.merge(OPDE.getLogin().getUser()));
                                        em.getTransaction().commit();
                                        processList.remove(qProcess);
                                        processList.add(myProcess);
//                                        Collections.sort(processList);
                                        qProcessMap.remove(qProcess);
                                        qProcessMap.put(myProcess, createCP4(myProcess));
                                        buildPanel();
                                    } catch (OptimisticLockException ole) {
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                            OPDE.getMainframe().emptyFrame();
                                            OPDE.getMainframe().afterLogin();
                                        }
                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                    } catch (Exception e) {
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        OPDE.fatal(e);
                                    } finally {
                                        em.close();
                                    }
                                }
                            }
                        });

                    }
                });
                btbTakeOver.setEnabled(!qProcess.isClosed());
                titlePanelright.add(btbTakeOver);
            }

        }

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

        cp.addCollapsiblePaneListener(new

                CollapsiblePaneAdapter() {
                    @Override
                    public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                        cp.setContentPane(createContentPanel4(qProcess));
                        cp.setOpaque(false);
                    }
                }

        );
        cp.setBackground(QProcessTools.getBG1(qProcess));


        cp.setHorizontalAlignment(SwingConstants.LEADING);
        cp.setOpaque(false);

        return cp;
    }


    private JPanel createContentPanel4(final QProcess qProcess) {
        JTextPane contentPane = new JTextPane();
        contentPane.setContentType("text/html");
        contentPane.setEditable(false);
        contentPane.setText(SYSTools.toHTML(QProcessTools.getAsHTML(qProcess)));

        JPanel elementPanel = new JPanel();
        elementPanel.setLayout(new VerticalLayout());
        elementPanel.add(contentPane);

        for (final QProcessElement element : qProcess.getElements()) {
            if (tbSystem.isSelected() || !(element instanceof PReport) || !((PReport) element).isSystem()) {
                final CollapsiblePane cpElement = createCP4(element, qProcess);
                if (element instanceof PReport && ((PReport) element).isSystem()) {
                    cpElement.setIcon(SYSConst.icon16exec);
                }
                cpElement.setBackground(QProcessTools.getBG2(qProcess));
                elementMap.put(element, cpElement);
                elementPanel.add(elementMap.get(element));
            }
        }
        return elementPanel;
    }


    private CollapsiblePane createCP4(final QProcessElement element, final QProcess qProcess) {
        String elementTitle = "[" + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(element.getPITInMillis())) + "] " + SYSTools.left(element.getTitle(), MAX_TEXT_LENGTH);
        elementTitle += " [" + element.getUser().getUID() + "]";
        final CollapsiblePane cpElement = new CollapsiblePane(elementTitle);
        try {
            cpElement.setCollapsed(true);
        } catch (PropertyVetoException e) {
            OPDE.debug(e);
        }

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.LINE_AXIS));


        final JButton btnUnlink = new JButton(SYSConst.icon16unlink);
        btnUnlink.setPressedIcon(SYSConst.icon16unlinkPressed);
        btnUnlink.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnUnlink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnUnlink.setContentAreaFilled(false);
        btnUnlink.setBorder(null);
        btnUnlink.setToolTipText(OPDE.lang.getString(internalClassID + ".btnunlink.tooltip"));
        btnUnlink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new DlgYesNo(OPDE.lang.getString(internalClassID + ".question.unlink") + "<p>" + element.getContentAsHTML() + "</p>", SYSConst.icon48delete, new Closure() {
                    @Override
                    public void execute(Object answer) {
                        if (answer.equals(JOptionPane.YES_OPTION)) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                if (!qProcess.isCommon()) {
                                    em.lock(em.merge(qProcess.getResident()), LockModeType.OPTIMISTIC);
                                }
                                if (element instanceof PReport) {
                                    PReport pReport = (PReport) em.merge(element);
                                    em.remove(pReport);
                                    qProcess.getPReports().remove(pReport);
                                } else {
                                    QProcessTools.removeElementFromProcess(em, element, qProcess);
                                }
                                em.getTransaction().commit();
                            } catch (OptimisticLockException ole) {
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
                                }
                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                            } catch (Exception e) {
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                OPDE.fatal(e);
                            } finally {
                                em.close();
                            }
                            qProcessMap.put(qProcess, createCP4(qProcess));

                            try {
                                qProcessMap.get(qProcess).setCollapsed(false);
                            } catch (PropertyVetoException e) {

                            }
                            buildPanel();
                        }
                    }
                });
            }
        });
        btnUnlink.setEnabled(!qProcess.isClosed() && !((element instanceof PReport) && ((PReport) element).isSystem()));
        btnPanel.add(btnUnlink);

        cpElement.setTitleComponent(btnPanel);
        cpElement.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {

                JTextPane elementText = new JTextPane();
                elementText.setContentType("text/html");
                elementText.setEditable(false);
                elementText.setText(SYSTools.toHTML("<div id=\"fonttext\">" + element.getContentAsHTML()) + "</div>");

                cpElement.setContentPane(elementText);
                cpElement.setOpaque(false);

            }
        });
        return cpElement;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents
    () {
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
        qProcessCollapsiblePaneHashMap.clear();
        qProcess2ElementMap.clear();
        qProcessMap.clear();
        processList.clear();
    }

    @Override
    public void switchResident(Resident resident) {
        this.resident = resident;
        processList = QProcessTools.getProcesses4(resident);
        reloadDisplay();
    }


    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(3));
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(OPDE.lang.getString(internalClassID));
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

        JComboBox cmbBW = new JComboBox(SYSTools.list2cmb(ResidentTools.getAllActive()));
        cmbBW.setFont(SYSConst.ARIAL14);
        cmbBW.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
                processList = QProcessTools.getProcesses4((Resident) itemEvent.getItem());
                reloadDisplay();
            }
        });
        list.add(cmbBW);

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER)) {
            final JComboBox cmbUser = new JComboBox(SYSTools.list2cmb(UsersTools.getUsers(false)));
            cmbUser.setRenderer(UsersTools.getUserRenderer());
            cmbUser.setFont(SYSConst.ARIAL14);
            cmbUser.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent itemEvent) {
                    if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
                    processList = QProcessTools.getProcesses4((Users) itemEvent.getItem());
                    reloadDisplay();
                }
            });
            list.add(cmbUser);

            JComboBox cmbPCat = new JComboBox(SYSTools.list2cmb(PCatTools.getPCats()));
            cmbPCat.setFont(SYSConst.ARIAL14);
            cmbPCat.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent itemEvent) {
                    if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
                    processList = QProcessTools.getProcesses4((PCat) itemEvent.getItem());
                    reloadDisplay();
                }
            });
            list.add(cmbPCat);

            final JideButton btnAll = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnallactive"), SYSConst.icon22link, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    processList = QProcessTools.getAllActive();
                    reloadDisplay();
                }
            });
            list.add(btnAll);

            final JideButton btnRunningOut = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnrunningout"), SYSConst.icon22clock, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    processList = QProcessTools.getProcessesRunningOutIn(5);
                    reloadDisplay();
                }
            });
            list.add(btnRunningOut);
        }

        final JideButton btnMyProcesses = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnmyprocesses"), SYSConst.icon22myself, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processList = QProcessTools.getProcesses4(OPDE.getLogin().getUser());
                reloadDisplay();
            }
        });
        list.add(btnMyProcesses);

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
                buildPanel();
            }
        });
        tbClosed.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbClosed);


        /***
         *      _   _    ____            _
         *     | |_| |__/ ___| _   _ ___| |_ ___ _ __ ___
         *     | __| '_ \___ \| | | / __| __/ _ \ '_ ` _ \
         *     | |_| |_) |__) | |_| \__ \ ||  __/ | | | | |
         *      \__|_.__/____/ \__, |___/\__\___|_| |_| |_|
         *                     |___/
         */
        tbSystem = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".tbsystem.text"));
        tbSystem.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase) return;
                buildPanel();
            }
        });
        tbSystem.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbSystem);

        return list;
    }


    private void buildPanel() {
        buildPanel(true);
    }

    private void buildPanel(boolean collapseAll) {
        cpProcess.removeAll();
        cpProcess.setLayout(new JideBoxLayout(cpProcess, JideBoxLayout.Y_AXIS));
        Collections.sort(processList);
        for (QProcess process : processList) {
            if (tbClosed.isSelected() || !process.isClosed()) {
                CollapsiblePane cp = qProcessMap.get(process);
                cpProcess.add(cp);
                try {
                    cp.setCollapsed(collapseAll);
                } catch (PropertyVetoException e) {

                }
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
         *     |____/ \__|_| |_/_/   \_\__,_|\__,_|
         *
         */
        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            final JideButton btnAdd = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnadd"), SYSConst.icon22add, null);
            btnAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgProcess(new QProcess(resident), new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    QProcess qProcess = em.merge((QProcess) o);
                                    em.getTransaction().commit();
                                    processList.add(qProcess);
                                    qProcessMap.put(qProcess, createCP4(qProcess));
                                    buildPanel();
                                } catch (Exception e) {
                                    em.getTransaction().rollback();
                                } finally {
                                    em.close();
                                }
                            }
                        }
                    });
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
