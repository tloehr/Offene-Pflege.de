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
import entity.files.SYSFilesTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.process.*;
import entity.system.Users;
import entity.system.UsersTools;
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
        this.resident = resident;
        initComponents();
        initPanel();
        switchResident(resident);
        initPhase = false;
    }

    public PnlProcess(JScrollPane jspSearch) {
        initPhase = true;
        this.jspSearch = jspSearch;
        this.resident = null;
        initComponents();
        initPanel();
        OPDE.getDisplayManager().setMainMessage(OPDE.getAppInfo().getInternalClasses().get(internalClassID).getShortDescription());
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

        qProcessCollapsiblePaneHashMap.clear();
        qProcess2ElementMap.clear();
        qProcessMap.clear();
        elementMap.clear();

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

//        String title = "<html><font size=+1>" +
//                SYSTools.left(qProcess.getTitle(), MAX_TEXT_LENGTH) +
//                " <b>" +
//                (qProcess.isCommon() ?
//                        "" :
//                        ResidentTools.getTextCompact(qProcess.getResident())) +
//                "</b>, " +
//                "[" +
//                DateFormat.getDateInstance(DateFormat.SHORT).format(qProcess.getFrom()) + "&rarr;" +
//                (qProcess.isClosed() ? DateFormat.getDateInstance(DateFormat.SHORT).format(qProcess.getTo()) : "|") +
//                "]" +
//                "</font></html>";

        String title = "<html><table border=\"0\">" +
                "<tr valign=\"top\">" +
                "<td width=\"100\" align=\"left\">" + qProcess.getPITAsHTML() + "</td>" +
                "<td width=\"100\" align=\"left\">" + " <b>" +
                (qProcess.isCommon() ?
                        "" :
                        ResidentTools.getTextCompact(qProcess.getResident())) +
                "</b>, "
                + "</td>" +
                "<td width=\"400\" align=\"left\">" +
                (qProcess.isClosed() ? "<s>" : "") +
                qProcess.getTitle() +
                (qProcess.isClosed() ? "</s>" : "") +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cp.setCollapsed(!cp.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

        GUITools.addExpandCollapseButtons(cp, cptitle.getRight());

        cp.setTitleLabelComponent(cptitle.getMain());
        cp.setSlidingDirection(SwingConstants.SOUTH);
        cp.setOpaque(true);
        cp.setHorizontalAlignment(SwingConstants.LEADING);

        if (qProcess.isRevisionPastDue()) {
            cptitle.getButton().setIcon(SYSConst.icon22ledRedOn);
        } else if (qProcess.isRevisionDue()) {
            cptitle.getButton().setIcon(SYSConst.icon22ledYellowOn);
        } else if (qProcess.isClosed()) {
            cptitle.getButton().setIcon(SYSConst.icon22stopSign);
        } else {
            cptitle.getButton().setIcon(SYSConst.icon22ledGreenOn);
        }


        /***
         *      _     _         ____       _       _
         *     | |__ | |_ _ __ |  _ \ _ __(_)_ __ | |_
         *     | '_ \| __| '_ \| |_) | '__| | '_ \| __|
         *     | |_) | |_| | | |  __/| |  | | | | | |_
         *     |_.__/ \__|_| |_|_|   |_|  |_|_| |_|\__|
         *
         */
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
            final JButton btnPrint = new JButton(SYSConst.icon22print2);
            btnPrint.setPressedIcon(SYSConst.icon22print2Pressed);
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
            cptitle.getRight().add(btnPrint);
        }

        /***
         *      __  __
         *     |  \/  | ___ _ __  _   _
         *     | |\/| |/ _ \ '_ \| | | |
         *     | |  | |  __/ | | | |_| |
         *     |_|  |_|\___|_| |_|\__,_|
         *
         */
        final JButton btnMenu = new JButton(SYSConst.icon22menu);
        btnMenu.setPressedIcon(SYSConst.icon22Pressed);
        btnMenu.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMenu.setContentAreaFilled(false);
        btnMenu.setBorder(null);
        btnMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JidePopup popup = new JidePopup();
                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                popup.setOwner(btnMenu);
                popup.removeExcludedComponent(btnMenu);
                JPanel pnl = getMenu(qProcess);
                popup.getContentPane().add(pnl);
                popup.setDefaultFocusComponent(pnl);

                GUITools.showPopup(popup, SwingConstants.WEST);
            }
        });
        btnMenu.setEnabled(!qProcess.isClosed());
        cptitle.getRight().add(btnMenu);

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

    @Override
    public String getInternalClassID() {
        return internalClassID;
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
        cpProcess.removeAll();
        qProcessCollapsiblePaneHashMap.clear();
        qProcess2ElementMap.clear();
        qProcessMap.clear();
        elementMap.clear();
        processList.clear();
    }

    @Override
    public void switchResident(Resident resident) {
        this.resident = resident;
        GUITools.setResidentDisplay(resident);
        processList = QProcessTools.getProcesses4(resident);
        reloadDisplay();
    }


    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
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

        if (resident == null) {
            final JComboBox cmbUser = new JComboBox();
            final JComboBox cmbPCat = new JComboBox();
            DefaultComboBoxModel dcbm = SYSTools.list2cmb(ResidentTools.getAllActive());
            dcbm.insertElementAt(null, 0);
            final JComboBox cmbBW = new JComboBox(dcbm);
            cmbBW.setRenderer(ResidentTools.getRenderer());
            cmbBW.setFont(SYSConst.ARIAL14);
            cmbBW.setSelectedIndex(0);
            cmbBW.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent itemEvent) {
                    if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
                    initPhase = true;
                    if (cmbUser.getModel().getSize() > 0) {
                        cmbUser.setSelectedIndex(0);
                    }
                    if (cmbPCat.getModel().getSize() > 0) {
                        cmbPCat.setSelectedIndex(0);
                    }
                    processList = QProcessTools.getProcesses4((Resident) itemEvent.getItem());
                    initPhase = false;
                    reloadDisplay();
                }
            });
            list.add(cmbBW);


            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, internalClassID)) {
                DefaultComboBoxModel dcbm1 = SYSTools.list2cmb(UsersTools.getUsers(false));
                cmbUser.setModel(dcbm1);
                dcbm1.insertElementAt(null, 0);
                cmbUser.setRenderer(UsersTools.getRenderer());
                cmbUser.setFont(SYSConst.ARIAL14);
                cmbUser.setSelectedIndex(0);
                cmbUser.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent itemEvent) {
                        if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
                        initPhase = true;
                        cmbBW.setSelectedIndex(0);
                        if (cmbPCat.getModel().getSize() > 0) {
                            cmbPCat.setSelectedIndex(0);
                        }
                        initPhase = false;
                        processList = QProcessTools.getProcesses4((Users) itemEvent.getItem());
                        reloadDisplay();
                    }
                });
                list.add(cmbUser);
                DefaultComboBoxModel dcbm2 = SYSTools.list2cmb(PCatTools.getPCats());
                dcbm2.insertElementAt(null, 0);
                cmbPCat.setModel(dcbm2);
                cmbPCat.setRenderer(PCatTools.getRenderer());
                cmbPCat.setFont(SYSConst.ARIAL14);
                cmbPCat.setSelectedIndex(0);
                cmbPCat.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent itemEvent) {
                        if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
                        initPhase = true;
                        cmbBW.setSelectedIndex(0);
                        if (cmbUser.getModel().getSize() > 0) {
                            cmbUser.setSelectedIndex(0);
                        }
                        initPhase = false;
                        processList = QProcessTools.getProcesses4((PCat) itemEvent.getItem());
                        reloadDisplay();
                    }
                });
                list.add(cmbPCat);

                final JideButton btnAll = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnallactive"), SYSConst.icon22link, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        processList = QProcessTools.getAllActive();
                        initPhase = true;
                        cmbBW.setSelectedIndex(0);
                        if (cmbUser.getModel().getSize() > 0) {
                            cmbUser.setSelectedIndex(0);
                        }
                        if (cmbPCat.getModel().getSize() > 0) {
                            cmbPCat.setSelectedIndex(0);
                        }
                        initPhase = false;
                        reloadDisplay();
                    }
                });
                list.add(btnAll);

                final JideButton btnRunningOut = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnrunningout"), SYSConst.icon22clock, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        processList = QProcessTools.getProcessesRunningOutIn(5);
                        initPhase = true;
                        cmbBW.setSelectedIndex(0);
                        if (cmbUser.getModel().getSize() > 0) {
                            cmbUser.setSelectedIndex(0);
                        }
                        if (cmbPCat.getModel().getSize() > 0) {
                            cmbPCat.setSelectedIndex(0);
                        }
                        initPhase = false;
                        reloadDisplay();
                    }
                });
                list.add(btnRunningOut);
            }

            final JideButton btnMyProcesses = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnmyprocesses"), SYSConst.icon22myself, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    initPhase = true;
                    cmbBW.setSelectedIndex(0);
                    if (cmbUser.getModel().getSize() > 0) {
                        cmbUser.setSelectedIndex(0);
                    }
                    if (cmbPCat.getModel().getSize() > 0) {
                        cmbPCat.setSelectedIndex(0);
                    }
                    initPhase = false;
                    processList = QProcessTools.getProcesses4(OPDE.getLogin().getUser());
                    reloadDisplay();
                }
            });
            list.add(btnMyProcesses);
        }

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

    private void buildPanel(final boolean collapseAll) {
        cpProcess.removeAll();
        cpProcess.setLayout(new JideBoxLayout(cpProcess, JideBoxLayout.Y_AXIS));
        Collections.sort(processList);
        boolean empty = true;
        for (QProcess process : processList) {
            if (tbClosed.isSelected() || !process.isClosed()) {
                empty = false;
                CollapsiblePane cp = qProcessMap.get(process);
                cpProcess.add(cp);
                try {
                    cp.setCollapsed(collapseAll);
                } catch (PropertyVetoException e) {

                }
            }
        }
        if (empty) {
            CollapsiblePane emptyPane = new CollapsiblePane(OPDE.lang.getString("misc.msg.nodata"));
            emptyPane.setCollapsible(false);
            cpProcess.add(emptyPane);
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
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
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

    private JPanel getMenu(final QProcess qProcess) {
        final JPanel pnlMenu = new JPanel(new VerticalLayout());

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {

            /***
             *      _     _            _       _     _ ____  ____                       _
             *     | |__ | |_ _ __    / \   __| | __| |  _ \|  _ \ ___ _ __   ___  _ __| |_
             *     | '_ \| __| '_ \  / _ \ / _` |/ _` | |_) | |_) / _ \ '_ \ / _ \| '__| __|
             *     | |_) | |_| | | |/ ___ \ (_| | (_| |  __/|  _ <  __/ |_) | (_) | |  | |_
             *     |_.__/ \__|_| |_/_/   \_\__,_|\__,_|_|   |_| \_\___| .__/ \___/|_|   \__|
             *                                                        |_|
             */
            final JButton btnAddPReport = GUITools.createHyperlinkButton(internalClassID + ".btnaddpreport.tooltip", SYSConst.icon22add, null);
            btnAddPReport.setAlignmentX(Component.RIGHT_ALIGNMENT);
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
            pnlMenu.add(btnAddPReport);

        }


        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            if (!qProcess.isClosed()) {
                /***
                 *      _     _          ____ _
                 *     | |__ | |_ _ __  / ___| | ___  ___  ___
                 *     | '_ \| __| '_ \| |   | |/ _ \/ __|/ _ \
                 *     | |_) | |_| | | | |___| | (_) \__ \  __/
                 *     |_.__/ \__|_| |_|\____|_|\___/|___/\___|
                 *
                 */
                final JButton btnClose = GUITools.createHyperlinkButton(internalClassID + ".btnclose.tooltip", SYSConst.icon22stop, null);
                btnClose.setAlignmentX(Component.RIGHT_ALIGNMENT);
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

                                        PReport pReport = em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_CLOSE), PReportTools.PREPORT_TYPE_CLOSE, myProcess));

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
                pnlMenu.add(btnClose);
            } else {
                /***
                 *      _     _         ____
                 *     | |__ | |_ _ __ |  _ \ ___  ___  _ __   ___ _ __
                 *     | '_ \| __| '_ \| |_) / _ \/ _ \| '_ \ / _ \ '_ \
                 *     | |_) | |_| | | |  _ <  __/ (_) | |_) |  __/ | | |
                 *     |_.__/ \__|_| |_|_| \_\___|\___/| .__/ \___|_| |_|
                 *                                     |_|
                 */
                final JButton btnReopen = GUITools.createHyperlinkButton(internalClassID + ".btnreopen.tooltip", SYSConst.icon22playerPlay, null);
                btnReopen.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnReopen.addActionListener(new ActionListener() {
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
                                        myProcess.setTo(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
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
                btnReopen.setEnabled(OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, internalClassID));
                pnlMenu.add(btnReopen);
            }

        }
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.DELETE, internalClassID)) {
            /***
             *      _     _         ____       _      _
             *     | |__ | |_ _ __ |  _ \  ___| | ___| |_ ___
             *     | '_ \| __| '_ \| | | |/ _ \ |/ _ \ __/ _ \
             *     | |_) | |_| | | | |_| |  __/ |  __/ ||  __/
             *     |_.__/ \__|_| |_|____/ \___|_|\___|\__\___|
             *
             */
            final JButton btnDelete = GUITools.createHyperlinkButton(internalClassID + ".btndelete.tooltip", SYSConst.icon22delete, null);
            btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
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
                                    for (SYSNP2PROCESS att : myProcess.getAttachedNursingProcessesConnections()) {
                                        em.remove(att);
                                    }
                                    for (SYSINF2PROCESS att : myProcess.getAttachedResInfoConnections()) {
                                        em.remove(att);
                                    }
                                    for (SYSPRE2PROCESS att : myProcess.getAttachedPrescriptionConnections()) {
                                        em.remove(att);
                                    }
                                    for (SYSVAL2PROCESS att : myProcess.getAttachedResValueConnections()) {
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
            pnlMenu.add(btnDelete);
        }
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            /***
             *      _     _         ____            _     _
             *     | |__ | |_ _ __ |  _ \ _____   _(_)___(_) ___  _ __
             *     | '_ \| __| '_ \| |_) / _ \ \ / / / __| |/ _ \| '_ \
             *     | |_) | |_| | | |  _ <  __/\ V /| \__ \ | (_) | | | |
             *     |_.__/ \__|_| |_|_| \_\___| \_/ |_|___/_|\___/|_| |_|
             *
             */
            final JButton btnRevision = GUITools.createHyperlinkButton(internalClassID + ".btnrevision.tooltip", SYSConst.icon22calendar, null);
            btnRevision.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnRevision.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    final JidePopup popup = new JidePopup();
                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                    final JButton btnSave = new JButton(SYSConst.icon16apply);
                    final JDateChooser editor = new JDateChooser(new DateMidnight().plusWeeks(2).toDate());
                    editor.setFont(SYSConst.ARIAL14);
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
                                processList.remove(qProcess);
                                processList.add(myProcess);
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
            pnlMenu.add(btnRevision);


            if (qProcess.isYours()) {
                /***
                 *      _     _         _   _                 _  ___
                 *     | |__ | |_ _ __ | | | | __ _ _ __   __| |/ _ \__   _____ _ __
                 *     | '_ \| __| '_ \| |_| |/ _` | '_ \ / _` | | | \ \ / / _ \ '__|
                 *     | |_) | |_| | | |  _  | (_| | | | | (_| | |_| |\ V /  __/ |
                 *     |_.__/ \__|_| |_|_| |_|\__,_|_| |_|\__,_|\___/  \_/ \___|_|
                 *
                 */
                final JButton btnHandOver = GUITools.createHyperlinkButton(internalClassID + ".btnhandover.tooltip", SYSConst.icon22give, null);
                btnHandOver.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnHandOver.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {

                        final JidePopup popup = new JidePopup();
                        popup.setMovable(false);
                        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.PAGE_AXIS));
                        final JButton btnSave = new JButton(SYSConst.icon22apply);
                        final JList editor = new JList(SYSTools.list2dlm(UsersTools.getUsers(false)));
                        editor.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        editor.setCellRenderer(UsersTools.getRenderer());
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
                pnlMenu.add(btnHandOver);
            } else {
                /***
                 *      _     _       _____     _         ___
                 *     | |__ | |_ _ _|_   _|_ _| | _____ / _ \__   _____ _ __
                 *     | '_ \| __| '_ \| |/ _` | |/ / _ \ | | \ \ / / _ \ '__|
                 *     | |_) | |_| | | | | (_| |   <  __/ |_| |\ V /  __/ |
                 *     |_.__/ \__|_| |_|_|\__,_|_|\_\___|\___/  \_/ \___|_|
                 *
                 */
                final JButton btbTakeOver = GUITools.createHyperlinkButton(internalClassID + ".btntakeover.tooltip", SYSConst.icon22take, null);
                btbTakeOver.setAlignmentX(Component.RIGHT_ALIGNMENT);
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
                pnlMenu.add(btbTakeOver);
            }

        }

        return pnlMenu;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspProcess;
    private CollapsiblePanes cpProcess;
    // End of variables declaration//GEN-END:variables
}
