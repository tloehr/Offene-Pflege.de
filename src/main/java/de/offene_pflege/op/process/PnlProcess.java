/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PnlProcess.java
 *
 * Created on 03.06.2011, 16:38:35
 */
package de.offene_pflege.op.process;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import de.offene_pflege.backend.entity.EntityTools;
import de.offene_pflege.backend.services.SYSFilesService;
import de.offene_pflege.backend.entity.done.Resident;
import de.offene_pflege.backend.services.ResidentTools;
import de.offene_pflege.backend.entity.process.*;
import de.offene_pflege.backend.entity.system.OPUsers;
import de.offene_pflege.backend.entity.system.UsersTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.gui.interfaces.DefaultCPTitle;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.system.InternalClassACL;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.*;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.util.List;
import java.util.*;

/**
 * @author tloehr
 */
public class PnlProcess extends NursingRecordsPanel {



    private Resident resident;
    private boolean initPhase = false;
    private JToggleButton tbClosed, tbSystem;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    private HashMap<Integer, CollapsiblePane> mapCP;

    private List<QProcess> processList;
    private int MAX_TEXT_LENGTH = 65;

    public PnlProcess(Resident resident, JScrollPane jspSearch) {
        super("nursingrecords.qprocesses");
        initPhase = true;
        this.jspSearch = jspSearch;
        this.resident = resident;
        initComponents();
        initPanel();
        switchResident(resident);
        initPhase = false;
    }

    public PnlProcess(JScrollPane jspSearch) {
        super("nursingrecords.qprocesses");
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
        mapCP = new HashMap<Integer, CollapsiblePane>();
//        mapProcess2CP = new HashMap<QProcess, CollapsiblePane>();
//        qProcess2ElementMap = new HashMap<QProcess, ArrayList<QProcessElement>>();
//        qProcessMap = new HashMap<QProcess, CollapsiblePane>();
//        elementMap = new HashMap<QProcessElement, CollapsiblePane>();
        prepareSearchArea();
    }

    @Override
    public void reload() {
        if (resident != null) {
            switchResident(resident);
        } else {
            processList = QProcessTools.getProcesses4(OPDE.getLogin().getUser());
            reloadDisplay();
        }

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


        mapCP.clear();

        for (QProcess qProcess : processList) {
            createCP4(qProcess);
        }
        buildPanel();

    }


    private CollapsiblePane createCP4(final QProcess qProcess) {

        if (!mapCP.containsKey(qProcess.hashCode())) {
            mapCP.put(qProcess.hashCode(), new CollapsiblePane());
            try {
                mapCP.get(qProcess.hashCode()).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        final CollapsiblePane cp = mapCP.get(qProcess.hashCode());

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

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            try {
                cp.setCollapsed(!cp.isCollapsed());
            } catch (PropertyVetoException pve) {
                // BAH!
            }
        });

//        GUITools.addExpandCollapseButtons(cp, cptitle.getRight());

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
            btnPrint.setToolTipText(SYSTools.xx("nursingrecords.qprocesses.btnrevision.tooltip"));
            btnPrint.addActionListener(actionEvent -> {
                String html = QProcessTools.getAsHTML(qProcess);
                html += QProcessTools.getElementsAsHTML(qProcess, tbSystem.isSelected());
                SYSFilesService.print(html, true);
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
        btnMenu.addActionListener(e -> {
            JidePopup popup = new JidePopup();
            popup.setMovable(false);
            popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
            popup.setOwner(btnMenu);
            popup.removeExcludedComponent(btnMenu);
            JPanel pnl = getMenu(qProcess);
            popup.getContentPane().add(pnl);
            popup.setDefaultFocusComponent(pnl);

            GUITools.showPopup(popup, SwingConstants.WEST);
        });
//        btnMenu.setPanelEnabled(!qProcess.isClosed());
        cptitle.getRight().add(btnMenu);

        cp.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cp.setContentPane(createContentPanel4(qProcess));
                cp.setOpaque(false);
            }
        });
        cp.setBackground(QProcessTools.getBG1(qProcess));

        if (!cp.isCollapsed()) {
            cp.setContentPane(createContentPanel4(qProcess));
            cp.setOpaque(false);
        }

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


        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {

            /***
             *      _     _            _       _     _ ____  ____                       _
             *     | |__ | |_ _ __    / \   __| | __| |  _ \|  _ \ ___ _ __   ___  _ __| |_
             *     | '_ \| __| '_ \  / _ \ / _` |/ _` | |_) | |_) / _ \ '_ \ / _ \| '__| __|
             *     | |_) | |_| | | |/ ___ \ (_| | (_| |  __/|  _ <  __/ |_) | (_) | |  | |_
             *     |_.__/ \__|_| |_/_/   \_\__,_|\__,_|_|   |_| \_\___| .__/ \___/|_|   \__|
             *                                                        |_|
             */
            final JButton btnAddPReport = GUITools.createHyperlinkButton("nursingrecords.qprocesses.btnaddpreport.tooltip", SYSConst.icon22add, null);
            btnAddPReport.setBackground(QProcessTools.getBG2(qProcess));
            btnAddPReport.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnAddPReport.addActionListener(actionEvent -> {
                final JidePopup popup = new JidePopup();
                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.PAGE_AXIS));
                final JButton btnSave = new JButton(SYSConst.icon22apply);
                final JTextArea editor = new JTextArea("", 10, 40);
                btnSave.addActionListener(evt -> {

                    if (editor.getText().trim().isEmpty()) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.emptyentry")));
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

                        processList.remove(qProcess);
                        processList.add(myProcess);
                        Collections.sort(processList);

                        createCP4(myProcess);

                        buildPanel();
                    } catch (OptimisticLockException ole) { OPDE.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
                        }
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    } catch (RollbackException ole) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                GUITools.showPopup(popup, SwingUtilities.EAST);
            });
            btnAddPReport.setEnabled(!qProcess.isClosed());
            elementPanel.add(btnAddPReport);
        }

        for (final QProcessElement element : qProcess.getElements()) {
            if (tbSystem.isSelected() || !(element instanceof PReport) || !((PReport) element).isSystem()) {
                final CollapsiblePane cpElement = createCP4(element, qProcess);
                if (element instanceof PReport && ((PReport) element).isSystem()) {
                    cpElement.setIcon(SYSConst.icon16exec);
                }
                if (element instanceof PReport && ((PReport) element).isPDCA()) {
                    cpElement.setBackground(QProcessTools.getBG2(qProcess).darker());
                } else {
                    cpElement.setBackground(QProcessTools.getBG2(qProcess));
                }
//                elementMap.put(element, cpElement);
                elementPanel.add(cpElement);
            }
        }
        return elementPanel;
    }


    private CollapsiblePane createCP4(final QProcessElement element, final QProcess qProcess) {
        String elementTitle = "[" + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(element.getPITInMillis())) + "] " + SYSTools.left(element.getTitle(), MAX_TEXT_LENGTH);
        elementTitle += " [" + element.getUser().getUID() + "]";

        if (!mapCP.containsKey(element.hashCode())) {
            mapCP.put(element.hashCode(), new CollapsiblePane(elementTitle));
            //            cpMap.get(key).setStyle(CollapsiblePane.PLAIN_STYLE);
            try {
                mapCP.get(element.hashCode()).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        final CollapsiblePane cpElement = mapCP.get(element.hashCode());
        cpElement.setTitle(elementTitle);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.LINE_AXIS));

        /***
         *      _   _       _     _       _
         *     | | | |_ __ | |   (_)_ __ | | __
         *     | | | | '_ \| |   | | '_ \| |/ /
         *     | |_| | | | | |___| | | | |   <
         *      \___/|_| |_|_____|_|_| |_|_|\_\
         *
         */
        final JButton btnUnlink = new JButton(SYSConst.icon16unlink);
        btnUnlink.setPressedIcon(SYSConst.icon16unlinkPressed);
        btnUnlink.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnUnlink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnUnlink.setContentAreaFilled(false);
        btnUnlink.setOpaque(false);
        btnUnlink.setBorder(null);
        btnUnlink.setToolTipText(SYSTools.xx("nursingrecords.qprocesses.btnunlink.tooltip"));
        btnUnlink.addActionListener(actionEvent -> {
            currentEditor = new DlgYesNo(SYSTools.xx("nursingrecords.qprocesses.question.unlink") + "<p>" + element.getContentAsHTML() + "</p>", SYSConst.icon48delete, answer -> {
                if (answer.equals(JOptionPane.YES_OPTION)) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        if (!qProcess.isCommon()) {
                            em.lock(em.merge(qProcess.getResident()), LockModeType.OPTIMISTIC);
                        }

                        QProcessElement myElement = em.merge(element);
                        QProcess myProcess = em.merge(qProcess);

                        em.lock(myProcess, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                        if (element instanceof PReport) {
                            myProcess.getPReports().remove(myElement);
                            em.remove(myElement);
                        } else {
                            QProcessTools.removeElementFromProcess(em, myElement, myProcess);

                        }
                        em.getTransaction().commit();

                        processList.remove(qProcess);
                        em.refresh(myProcess);
                        processList.add(myProcess);
                        Collections.sort(processList);

                        createCP4(myProcess);

                        buildPanel();

                    } catch (OptimisticLockException ole) { OPDE.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
                        }
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    } catch (RollbackException ole) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                        currentEditor = null;
                    }

                }
            });
            currentEditor.setVisible(true);
        });
        btnUnlink.setEnabled(!qProcess.isClosed() && !((element instanceof PReport) && (((PReport) element).isSystem() || ((PReport) element).isPDCA())));
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
     * always regenerated by the PrinterForm Editor.
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
        super.cleanup();
        cpProcess.removeAll();
//        SYSTools.clear(mapProcess2CP);
//        SYSTools.clear(qProcess2ElementMap);
//        SYSTools.clear(qProcessMap);
//        SYSTools.clear(elementMap);
        SYSTools.clear(processList);
    }

    @Override
    public void switchResident(Resident res) {
        this.resident = EntityTools.find(Resident.class, res.getId());
        GUITools.setResidentDisplay(resident);
        processList = QProcessTools.getProcesses4(resident);
        reloadDisplay();
    }

    private List<Component> addKey() {
        List<Component> list = new ArrayList<Component>();
        list.add(new JSeparator());
        list.add(new JLabel(SYSTools.xx("misc.msg.key")));
        list.add(new JLabel(SYSTools.xx("nursingrecords.qprocesses.keydescription1"), SYSConst.icon22ledGreenOn, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.qprocesses.keydescription2"), SYSConst.icon22ledYellowOn, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.qprocesses.keydescription3"), SYSConst.icon22ledRedOn, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.qprocesses.keydescription4"), SYSConst.icon22stopSign, SwingConstants.LEADING));

//        if (qProcess.isRevisionPastDue()) {
//                   cptitle.getTitleButton().setIcon(SYSConst.icon22ledRedOn);
//               } else if (qProcess.isRevisionDue()) {
//                   cptitle.getTitleButton().setIcon(SYSConst.icon22ledYellowOn);
//               } else if (qProcess.isClosed()) {
//                   cptitle.getTitleButton().setIcon(SYSConst.icon22stopSign);
//               } else {
//                   cptitle.getTitleButton().setIcon(SYSConst.icon22ledGreenOn);
//               }

        return list;
    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(SYSTools.xx(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

        GUITools.addAllComponents(mypanel, addCommands());
        GUITools.addAllComponents(mypanel, addFilters());
        GUITools.addAllComponents(mypanel, addKey());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }

    private List<Component> addFilters() {
        List<Component> list = new ArrayList<Component>();

        if (resident == null) {
            final JComboBox cmbUser = new JComboBox();
            final JComboBox cmbPCat = new JComboBox();
            DefaultComboBoxModel dcbm = SYSTools.list2cmb(ResidentTools.getAllActive());
            dcbm.insertElementAt(null, 0);
            final JComboBox cmbBW = new JComboBox(dcbm);
            cmbBW.setRenderer(ResidentTools.getRenderer());
            cmbBW.setFont(SYSConst.ARIAL14);
            cmbBW.setSelectedIndex(0);
            cmbBW.addItemListener(itemEvent -> {
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
            });
            list.add(cmbBW);


            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, internalClassID)) {
                DefaultComboBoxModel dcbm1 = SYSTools.list2cmb(UsersTools.getUsers(false));
                cmbUser.setModel(dcbm1);
                dcbm1.insertElementAt(null, 0);
                cmbUser.setRenderer(UsersTools.getRenderer());
                cmbUser.setFont(SYSConst.ARIAL14);
                cmbUser.setSelectedIndex(0);
                cmbUser.addItemListener(itemEvent -> {
                    if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
                    initPhase = true;
                    cmbBW.setSelectedIndex(0);
                    if (cmbPCat.getModel().getSize() > 0) {
                        cmbPCat.setSelectedIndex(0);
                    }
                    initPhase = false;
                    processList = QProcessTools.getProcesses4((OPUsers) itemEvent.getItem());
                    reloadDisplay();
                });
                list.add(cmbUser);
                DefaultComboBoxModel dcbm2 = SYSTools.list2cmb(PCatTools.getPCats());
                dcbm2.insertElementAt(null, 0);
                cmbPCat.setModel(dcbm2);
                cmbPCat.setRenderer(PCatTools.getRenderer());
                cmbPCat.setFont(SYSConst.ARIAL14);
                cmbPCat.setSelectedIndex(0);
                cmbPCat.addItemListener(itemEvent -> {
                    if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
                    initPhase = true;
                    cmbBW.setSelectedIndex(0);
                    if (cmbUser.getModel().getSize() > 0) {
                        cmbUser.setSelectedIndex(0);
                    }
                    initPhase = false;
                    processList = QProcessTools.getProcesses4((PCat) itemEvent.getItem());
                    reloadDisplay();
                });
                list.add(cmbPCat);

                final JideButton btnAll = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.qprocesses.btnallactive"), SYSConst.icon22link, e -> {
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
                });
                list.add(btnAll);

                final JideButton btnRunningOut = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.qprocesses.btnrunningout"), SYSConst.icon22clock, e -> {
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
                });
                list.add(btnRunningOut);
            }

            final JideButton btnMyProcesses = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.qprocesses.btnmyprocesses"), SYSConst.icon22myself, e -> {
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
        tbClosed = GUITools.getNiceToggleButton(SYSTools.xx("misc.filters.showclosed"));
        tbClosed.addItemListener(itemEvent -> {
            if (initPhase) return;
            reloadDisplay();
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
        tbSystem = GUITools.getNiceToggleButton(SYSTools.xx("nursingrecords.qprocesses.tbsystem.text"));
        tbSystem.addItemListener(itemEvent -> {
            if (initPhase) return;
            reloadDisplay();
        });
        tbSystem.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbSystem);

        return list;
    }


//    private void buildPanel() {
//        buildPanel(true);
//    }

    private void buildPanel() {
        cpProcess.removeAll();
        cpProcess.setLayout(new JideBoxLayout(cpProcess, JideBoxLayout.Y_AXIS));
        Collections.sort(processList);
        boolean empty = true;
        for (QProcess process : processList) {
            if (tbClosed.isSelected() || !process.isClosed()) {
                empty = false;
                CollapsiblePane cp = mapCP.get(process.hashCode());
                cpProcess.add(cp);
//                try {
//                    cp.setCollapsed(collapseAll);
//                } catch (PropertyVetoException e) {
//
//                }
            }
        }
        if (empty) {
            CollapsiblePane emptyPane = new CollapsiblePane(SYSTools.xx("misc.msg.nodata"));
            emptyPane.setCollapsible(false);
            cpProcess.add(emptyPane);
        }
        cpProcess.addExpansion();
    }

    private List<Component> addCommands() {

        List<Component> list = new ArrayList<Component>();

        /***
         *      _     _            _       _     _
         *     | |__ | |_ _ __    / \   __| | __| |
         *     | '_ \| __| '_ \  / _ \ / _` |/ _` |
         *     | |_) | |_| | | |/ ___ \ (_| | (_| |
         *     |____/ \__|_| |_/_/   \_\__,_|\__,_|
         *
         */
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            final JideButton btnAdd = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.qprocesses.btnadd"), SYSConst.icon22add, null);
            btnAdd.addActionListener(actionEvent -> {
                currentEditor = new DlgProcess(new QProcess(resident), o -> {
                    if (resident != null && !ResidentTools.isActive(resident)) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                        currentEditor = null;
                        return;
                    }
                    if (o != null) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            QProcess qProcess = em.merge((QProcess) o);
                            em.getTransaction().commit();
                            processList.add(qProcess);
                            Collections.sort(processList);
                            createCP4(qProcess);
                            buildPanel();
                        } catch (Exception e) {
                            em.getTransaction().rollback();
                        } finally {
                            em.close();
                        }
                    }
                    currentEditor = null;
                });
                currentEditor.setVisible(true);
            });
            list.add(btnAdd);
            btnAdd.setEnabled(resident == null || ResidentTools.isActive(resident));
        }

        return list;
    }

    private JPanel getMenu(final QProcess qProcess) {
        final JPanel pnlMenu = new JPanel(new VerticalLayout());

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
                final JButton btnClose = GUITools.createHyperlinkButton("nursingrecords.qprocesses.btnclose.tooltip", SYSConst.icon22stop, null);
                btnClose.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnClose.addActionListener(actionEvent -> {
                    currentEditor = new DlgYesNo(SYSTools.xx("nursingrecords.qprocesses.question.close") + "<p>" + qProcess.getTitle() + "</p>", SYSConst.icon48playerStop, answer -> {
                        if (answer.equals(JOptionPane.YES_OPTION)) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();

                                QProcess myProcess = em.merge(qProcess);
                                if (!myProcess.isCommon()) {
                                    em.lock(em.merge(myProcess.getResident()), LockModeType.OPTIMISTIC);
                                }
                                em.lock(myProcess, LockModeType.OPTIMISTIC);

                                PReport pReport = em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_CLOSE), PReportTools.PREPORT_TYPE_CLOSE, myProcess));

                                myProcess.setTo(new Date());
                                myProcess.getPReports().add(pReport);
                                em.getTransaction().commit();
                                processList.remove(qProcess);
                                em.refresh(myProcess);
                                processList.add(myProcess);
                                mapCP.remove(qProcess.hashCode());
                                createCP4(myProcess);
                                buildPanel();
                            } catch (OptimisticLockException ole) { OPDE.warn(ole);
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                                currentEditor = null;
                            }
                        }
                    });
                    currentEditor.setVisible(true);
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
                final JButton btnReopen = GUITools.createHyperlinkButton("nursingrecords.qprocesses.btnreopen.tooltip", SYSConst.icon22playerPlay, null);
                btnReopen.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnReopen.addActionListener(actionEvent -> {
                    currentEditor = new DlgYesNo(SYSTools.xx("nursingrecords.qprocesses.question.reopen") + "<p>" + qProcess.getTitle() + "</p>", SYSConst.icon48play, answer -> {
                        if (answer.equals(JOptionPane.YES_OPTION)) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();

                                QProcess myProcess = em.merge(qProcess);
                                if (!myProcess.isCommon()) {
                                    em.lock(em.merge(myProcess.getResident()), LockModeType.OPTIMISTIC);
                                }
                                em.lock(myProcess, LockModeType.OPTIMISTIC);
                                PReport pReport = em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_REOPEN), PReportTools.PREPORT_TYPE_REOPEN, qProcess));
                                myProcess.setTo(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
                                myProcess.getPReports().add(pReport);
                                em.getTransaction().commit();
                                processList.remove(qProcess);
                                myProcess.setOwner(em.merge(OPDE.getLogin().getUser()));
                                em.refresh(myProcess);
                                processList.add(myProcess);
                                mapCP.remove(qProcess.hashCode());
                                createCP4(myProcess);
                                buildPanel();
                            } catch (OptimisticLockException ole) { OPDE.warn(ole);
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                                currentEditor = null;
                            }
                        }
                    });
                    currentEditor.setVisible(true);
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
            final JButton btnDelete = GUITools.createHyperlinkButton("nursingrecords.qprocesses.btndelete.tooltip", SYSConst.icon22delete, null);
            btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnDelete.addActionListener(actionEvent -> {
                currentEditor = new DlgYesNo(SYSTools.xx("nursingrecords.qprocesses.question.delete") + "<p>" + qProcess.getTitle() + "</p>", SYSConst.icon48delete, answer -> {
                    if (answer.equals(JOptionPane.YES_OPTION)) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            QProcess myProcess = em.merge(qProcess);
                            if (!myProcess.isCommon()) {
                                em.lock(em.merge(myProcess.getResident()), LockModeType.OPTIMISTIC);
                            }
                            em.lock(myProcess, LockModeType.OPTIMISTIC);

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

                            em.remove(myProcess);

                            em.getTransaction().commit();
                            processList.remove(qProcess);
                            mapCP.remove(qProcess.hashCode());
                            buildPanel();
                        } catch (OptimisticLockException ole) { OPDE.warn(ole);
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                            currentEditor = null;
                        }
                    }
                });
                currentEditor.setVisible(true);
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
            final JButton btnRevision = GUITools.createHyperlinkButton("nursingrecords.qprocesses.btnrevision.tooltip", SYSConst.icon22calendar, null);
            btnRevision.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnRevision.addActionListener(actionEvent -> {

                final JidePopup popup = new JidePopup();
                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                final JButton btnSave = new JButton(SYSConst.icon16apply);
                final JDateChooser editor = new JDateChooser(new LocalDate().plusWeeks(2).toDate());
                editor.setFont(SYSConst.ARIAL14);
                editor.setMinSelectableDate(new LocalDate().plusDays(1).toDate());
                btnSave.addActionListener(evt -> {

                    if (editor.getDate() == null) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.wrongentry")));
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
                        PReport pReport = new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_WV) + ": " + DateFormat.getDateInstance().format(editor.getDate()), PReportTools.PREPORT_TYPE_WV, myProcess);
                        myProcess.setRevision(editor.getDate());
                        myProcess.getPReports().add(pReport);
                        em.getTransaction().commit();

                        processList.remove(qProcess);
                        em.refresh(myProcess);
                        processList.add(myProcess);

                        createCP4(myProcess);

                        buildPanel();
                    } catch (OptimisticLockException ole) { OPDE.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
                        }
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    } catch (RollbackException ole) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                });

                JPanel pnl = new JPanel(new BorderLayout(10, 10));

                pnl.add(editor, BorderLayout.CENTER);
                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
                buttonPanel.add(btnSave);
                pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
                pnl.add(buttonPanel, BorderLayout.SOUTH);
                popup.setTransient(true);

                popup.setOwner(btnRevision);
                popup.getContentPane().add(pnl);

                popup.setDefaultFocusComponent(editor);
                GUITools.showPopup(popup, SwingUtilities.WEST);

            });
            btnRevision.setEnabled(qProcess.isYours() && !qProcess.isClosed());
            pnlMenu.add(btnRevision);


            if (qProcess.isCommon()) {
                /***
                 *      ____  ____   ____    _
                 *     |  _ \|  _ \ / ___|  / \
                 *     | |_) | | | | |     / _ \
                 *     |  __/| |_| | |___ / ___ \
                 *     |_|   |____/ \____/_/   \_\
                 *
                 */
                final JPopupMenu pdcaMenu = new JPopupMenu(SYSTools.xx("nursingrecords.qprocesses.set.pdca"));

                JMenuItem pdca_plan = new JMenuItem(SYSTools.xx("nursingrecords.qprocesses.set.pdca.plan"));
                pdca_plan.addActionListener(actionEvent -> {
                    currentEditor = new DlgYesNo(SYSTools.xx("misc.msg.are.you.sure"), null, answer -> {
                        if (answer.equals(JOptionPane.YES_OPTION)) {
                            setPDCA(qProcess, PReportTools.PREPORT_TYPE_SET_PDCA_PLAN, QProcessTools.PDCA_PLAN);
                        }
                        currentEditor = null;
                    });
                    currentEditor.setVisible(true);
                });
                pdca_plan.setEnabled(!qProcess.isPDCA() || qProcess.getPDCA() == QProcessTools.PDCA_ACT);
                pdcaMenu.add(pdca_plan);

                JMenuItem pdca_do = new JMenuItem(SYSTools.xx("nursingrecords.qprocesses.set.pdca.do"));
                pdca_do.addActionListener(actionEvent -> {
                    currentEditor = new DlgYesNo(SYSTools.xx("misc.msg.are.you.sure"), null, answer -> {
                        if (answer.equals(JOptionPane.YES_OPTION)) {
                            setPDCA(qProcess, PReportTools.PREPORT_TYPE_SET_PDCA_DO, QProcessTools.PDCA_DO);
                        }
                        currentEditor = null;
                    });
                    currentEditor.setVisible(true);
                });
                pdca_do.setEnabled(qProcess.isPDCA() && qProcess.getPDCA() == QProcessTools.PDCA_PLAN);
                pdcaMenu.add(pdca_do);

                JMenuItem pdca_check = new JMenuItem(SYSTools.xx("nursingrecords.qprocesses.set.pdca.check"));
                pdca_check.addActionListener(actionEvent -> {
                    currentEditor = new DlgYesNo(SYSTools.xx("misc.msg.are.you.sure"), null, answer -> {
                        if (answer.equals(JOptionPane.YES_OPTION)) {
                            setPDCA(qProcess, PReportTools.PREPORT_TYPE_SET_PDCA_CHECK, QProcessTools.PDCA_CHECK);
                        }
                        currentEditor = null;
                    });
                    currentEditor.setVisible(true);
                });
                pdca_check.setEnabled(qProcess.isPDCA() && qProcess.getPDCA() == QProcessTools.PDCA_DO);
                pdcaMenu.add(pdca_check);

                JMenuItem pdca_act = new JMenuItem(SYSTools.xx("nursingrecords.qprocesses.set.pdca.act"));
                pdca_act.addActionListener(actionEvent -> {
                    currentEditor = new DlgYesNo(SYSTools.xx("misc.msg.are.you.sure"), null, answer -> {
                        if (answer.equals(JOptionPane.YES_OPTION)) {
                            setPDCA(qProcess, PReportTools.PREPORT_TYPE_SET_PDCA_ACT, QProcessTools.PDCA_ACT);
                        }
                        currentEditor = null;
                    });
                    currentEditor.setVisible(true);
                });
                pdca_act.setEnabled(qProcess.isPDCA() && qProcess.getPDCA() == QProcessTools.PDCA_CHECK);
                pdcaMenu.add(pdca_act);

                pdcaMenu.add(new JSeparator());
                JMenuItem pdca_clear = new JMenuItem(SYSTools.xx("nursingrecords.qprocesses.clear.pdca"));
                pdca_clear.addActionListener(actionEvent -> {
                    currentEditor = new DlgYesNo(SYSTools.xx("misc.msg.are.you.sure"), null, answer -> {
                        if (answer.equals(JOptionPane.YES_OPTION)) {
                            setPDCA(qProcess, null, null);
                        }
                        currentEditor = null;
                    });
                    currentEditor.setVisible(true);
                });
                pdca_clear.setEnabled(qProcess.isPDCA());
                pdcaMenu.add(pdca_clear);

                final JideButton btnPDCA = GUITools.createHyperlinkButton("nursingrecords.qprocesses.set.pdca", SYSConst.icon22pdca, null);

                btnPDCA.addActionListener(e -> pdcaMenu.show(btnPDCA, 0, 0));

                pnlMenu.add(btnPDCA);
            }


            if (qProcess.isYours()) {
                /***
                 *      _     _         _   _                 _  ___
                 *     | |__ | |_ _ __ | | | | __ _ _ __   __| |/ _ \__   _____ _ __
                 *     | '_ \| __| '_ \| |_| |/ _` | '_ \ / _` | | | \ \ / / _ \ '__|
                 *     | |_) | |_| | | |  _  | (_| | | | | (_| | |_| |\ V /  __/ |
                 *     |_.__/ \__|_| |_|_| |_|\__,_|_| |_|\__,_|\___/  \_/ \___|_|
                 *
                 */
                final JButton btnHandOver = GUITools.createHyperlinkButton("nursingrecords.qprocesses.btnhandover.tooltip", SYSConst.icon22give, null);
                btnHandOver.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnHandOver.addActionListener(actionEvent -> {

                    final JidePopup popup = new JidePopup();
                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.PAGE_AXIS));
                    final JButton btnSave = new JButton(SYSConst.icon22apply);
                    final JList editor = new JList(SYSTools.list2dlm(UsersTools.getUsers(false)));
                    editor.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    editor.setCellRenderer(UsersTools.getRenderer());
                    btnSave.addActionListener(evt -> {

                        if (editor.getSelectedValue() == null) {
                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.emptyentry")));
                            return;
                        }

                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            QProcess myProcess = em.merge(qProcess);
                            OPUsers handOverTo = em.merge((OPUsers) editor.getSelectedValue());
                            if (!myProcess.isCommon()) {
                                em.lock(em.merge(myProcess.getResident()), LockModeType.OPTIMISTIC);
                            }
                            em.lock(myProcess, LockModeType.OPTIMISTIC);

                            PReport pReport = em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_SET_OWNERSHIP) + ": " + handOverTo.getFullname(), PReportTools.PREPORT_TYPE_SET_OWNERSHIP, myProcess));
                            myProcess.setOwner(handOverTo);
                            myProcess.getPReports().add(pReport);
                            em.getTransaction().commit();

                            processList.remove(qProcess);
                            em.refresh(myProcess);
                            processList.add(myProcess);

                            Collections.sort(processList);

                            createCP4(myProcess);
                            buildPanel();
                        } catch (OptimisticLockException ole) { OPDE.warn(ole);
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                final JButton btbTakeOver = GUITools.createHyperlinkButton("nursingrecords.qprocesses.btntakeover.tooltip", SYSConst.icon22take, null);
                btbTakeOver.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btbTakeOver.addActionListener(actionEvent -> {
                    currentEditor = new DlgYesNo(SYSTools.xx("nursingrecords.qprocesses.question.takeover") + "<p>" + qProcess.getTitle() + "</p>", SYSConst.icon48play, answer -> {
                        if (answer.equals(JOptionPane.YES_OPTION)) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();

                                QProcess myProcess = em.merge(qProcess);
                                if (!myProcess.isCommon()) {
                                    em.lock(em.merge(myProcess.getResident()), LockModeType.OPTIMISTIC);
                                }
                                em.lock(myProcess, LockModeType.OPTIMISTIC);
                                PReport pReport = em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_TAKE_OWNERSHIP) + ": " + OPDE.getLogin().getUser().getFullname(), PReportTools.PREPORT_TYPE_TAKE_OWNERSHIP, myProcess));
                                myProcess.getPReports().add(pReport);
                                myProcess.setOwner(em.merge(OPDE.getLogin().getUser()));
                                em.getTransaction().commit();
                                processList.remove(qProcess);

                                em.refresh(myProcess);
                                processList.add(myProcess);
                                Collections.sort(processList);

                                createCP4(myProcess);
                                buildPanel();
                            } catch (OptimisticLockException ole) { OPDE.warn(ole);
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                                currentEditor = null;
                            }
                        }
                    });
                    currentEditor.setVisible(true);

                });
                btbTakeOver.setEnabled(!qProcess.isClosed());
                pnlMenu.add(btbTakeOver);
            }

        }

        return pnlMenu;
    }


    private void setPDCA(QProcess qProcess, Short preportType, Integer pdca) {
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            QProcess myProcess = em.merge(qProcess);

            em.lock(myProcess, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            myProcess.setPDCA(pdca);

            if (pdca == null) {
                Query query = em.createQuery("DELETE FROM PReport p WHERE p.qProcess = :qProcess AND p.art IN (:art1, :art2, :art3, :art4)");
                query.setParameter("qProcess", qProcess);
                query.setParameter("art1", PReportTools.PREPORT_TYPE_SET_PDCA_PLAN);
                query.setParameter("art2", PReportTools.PREPORT_TYPE_SET_PDCA_DO);
                query.setParameter("art3", PReportTools.PREPORT_TYPE_SET_PDCA_CHECK);
                query.setParameter("art4", PReportTools.PREPORT_TYPE_SET_PDCA_ACT);
                query.executeUpdate();
            } else {
                PReport pReport = em.merge(new PReport(null, preportType, myProcess));
                myProcess.getPReports().add(pReport);
            }

            em.getTransaction().commit();

            processList.remove(qProcess);
            em.refresh(myProcess);
            processList.add(myProcess);

            createCP4(myProcess);

            buildPanel();
        } catch (OptimisticLockException ole) { OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                OPDE.getMainframe().emptyFrame();
                OPDE.getMainframe().afterLogin();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
        } catch (RollbackException ole) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspProcess;
    private CollapsiblePanes cpProcess;
    // End of variables declaration//GEN-END:variables
}
