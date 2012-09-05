/*
 * OffenePflege
 * Copyright (C) 2008 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License V2 as published by the Free Software Foundation
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------ 
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 * 
 */
package op.care.reports;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import entity.files.SYSFilesTools;
import entity.files.SYSNR2FILE;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.process.QProcess;
import entity.process.QProcessElement;
import entity.process.SYSNR2PROCESS;
import entity.reports.NReport;
import entity.reports.NReportTAGS;
import entity.reports.NReportTAGSTools;
import entity.reports.NReportTools;
import op.OPDE;
import op.care.sysfiles.DlgFiles;
import op.care.sysfiles.PnlFiles;
import op.process.DlgProcessAssign;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * @author root
 */
public class PnlReport extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.reports";
    private int MAX_TEXT_LENGTH = 80;

    private final int WEEKS_BACK = 4;
    private JDateChooser jdcVon;
    private JXSearchField txtSearch;
    private JToggleButton tbShowReplaced, tbFilesOnly;
    private JComboBox cmbAuswahl;

    private HashMap<DateMidnight, ArrayList<NReport>> dayMap;
    private HashMap<NReport, CollapsiblePane> reportMap;

    private Resident resident;
    private JPopupMenu menu;
    private boolean initPhase;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private NReport firstReport;

    private JidePopup popup;


    /**
     * Creates new form PnlReport
     */
    public PnlReport(Resident resident, JScrollPane jspSearch) {
        this.initPhase = true;
        initComponents();
        this.jspSearch = jspSearch;

        prepareSearchArea();

        initPanel();
        this.initPhase = false;

        switchResident(resident);


    }

    private void initPanel() {

        dayMap = new HashMap<DateMidnight, ArrayList<NReport>>();
//        dayCPMap = new HashMap<DateMidnight, CollapsiblePane>();
        reportMap = new HashMap<NReport, CollapsiblePane>();
        prepareSearchArea();
    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);
        searchPanes.add(addCommands());
        searchPanes.add(addFilters());
        searchPanes.addExpansion();
    }

    @Override
    public void reload() {
        if (tbFilesOnly.isSelected()) {
            reloadDisplay(NReportTools.getReportsWithFilesOnly(resident));
        } else {
            reloadDisplay(NReportTools.getReports(resident, jdcVon.getDate(), WEEKS_BACK));
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspReports = new JScrollPane();
        cpReports = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspReports ========
        {

            //======== cpReports ========
            {
                cpReports.setLayout(new BoxLayout(cpReports, BoxLayout.X_AXIS));
            }
            jspReports.setViewportView(cpReports);
        }
        add(jspReports);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void cleanup() {

        jdcVon.cleanup();

    }

    @Override
    public void switchResident(Resident bewohner) {
        this.resident = bewohner;
        OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(bewohner));
        txtSearch.setText(null);
        firstReport = NReportTools.getFirstReport(resident);
        jdcVon.setMaxSelectableDate(new Date());
        jdcVon.setMinSelectableDate(firstReport.getPit());
        reload();
    }

//
//
//            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !alreadyEdited && singleRowSelected) {
////                menu.add(new JSeparator());
//                menu.add(QProcessTools.getVorgangContextMenu(new JFrame(), bericht, resident, standardActionListener));
//            }
//
//            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
//        }
//    }//GEN-LAST:event_tblTBMousePressed


    private CollapsiblePane addFilters() {
        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setLayout(new VerticalLayout(5));

        txtSearch = new JXSearchField(OPDE.lang.getString("misc.msg.searchphrase"));
        txtSearch.setFont(SYSConst.ARIAL14);
        txtSearch.setInstantSearchDelay(750);
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (SYSTools.catchNull(txtSearch.getText()).trim().length() > 3) {
                    reloadDisplay(NReportTools.getReports(resident, txtSearch.getText()));
                }
            }
        });

        labelPanel.add(txtSearch);

        jdcVon = new JDateChooser(new Date());
        jdcVon.setBackground(Color.WHITE);
        jdcVon.setFont(SYSConst.ARIAL14);
        jdcVon.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (initPhase) {
                    return;
                }
                if (!evt.getPropertyName().equals("date")) {
                    return;
                }
                reloadDisplay(NReportTools.getReports(resident, jdcVon.getDate(), WEEKS_BACK));
            }
        });
        labelPanel.add(jdcVon);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new HorizontalLayout(5));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        EntityManager em = OPDE.createEM();
        MouseAdapter ma = GUITools.getHyperlinkStyleMouseAdapter();
        Query query = em.createNamedQuery("PBerichtTAGS.findAllActive");
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel(query.getResultList().toArray());
        em.close();

        dcbm.insertElementAt(OPDE.lang.getString("misc.commands.noselection"), 0);
        cmbAuswahl = new JComboBox(dcbm);
        cmbAuswahl.setFont(SYSConst.ARIAL14);
        cmbAuswahl.setRenderer(NReportTAGSTools.getPBerichtTAGSRenderer());
        cmbAuswahl.setSelectedIndex(0);
        cmbAuswahl.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
//                SYSPropsTools.storeState(internalClassID + ":cmbAuswahl", cmbAuswahl);
                buildPanel();
            }
        });
        labelPanel.add(cmbAuswahl);
//        SYSPropsTools.restoreState(internalClassID + ":cmbAuswahl", cmbAuswahl);

        tbFilesOnly = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.filesonly"));
        tbFilesOnly.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase) return;
//                SYSPropsTools.storeState(internalClassID + ":tbFilesOnly", tbFilesOnly);
                reload();
            }
        });
        labelPanel.add(tbFilesOnly);
//        SYSPropsTools.restoreState(internalClassID + ":tbFilesOnly", tbFilesOnly);
        tbFilesOnly.setHorizontalAlignment(SwingConstants.LEFT);

        tbShowReplaced = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.showreplaced"));
        tbShowReplaced.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase) return;
//                SYSPropsTools.storeState(internalClassID + ":tbShowReplaced", tbShowReplaced);
                buildPanel();
            }
        });
        labelPanel.add(tbShowReplaced);
//        SYSPropsTools.restoreState(internalClassID + ":tbShowReplaced", tbShowReplaced);
        tbShowReplaced.setHorizontalAlignment(SwingConstants.LEFT);


        JideButton resetButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.resetFilter"), SYSConst.icon22undo, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                initPhase = true;
                jdcVon.setDate(new Date());
                cmbAuswahl.setSelectedIndex(0);
                tbFilesOnly.setSelected(false);
                tbShowReplaced.setSelected(false);
                txtSearch.setText(null);
                initPhase = false;
                reload();
            }
        });
        labelPanel.add(resetButton);


        CollapsiblePane panelFilter = new CollapsiblePane(OPDE.lang.getString("misc.msg.Filter"));
        panelFilter.setStyle(CollapsiblePane.PLAIN_STYLE);
        panelFilter.setCollapsible(false);
        panelFilter.setContentPane(labelPanel);

        return panelFilter;
    }

    private CollapsiblePane addCommands() {

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(OPDE.lang.getString(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        /***
         *      _   _
         *     | \ | | _____      __
         *     |  \| |/ _ \ \ /\ / /
         *     | |\  |  __/\ V  V /
         *     |_| \_|\___| \_/\_/
         *
         */
        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            JideButton addButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.new"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgReport(new NReport(resident), new Closure() {
                        @Override
                        public void execute(Object report) {
                            if (report != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    NReport myReport = (NReport) em.merge(report);
                                    em.getTransaction().commit();
                                    DateMidnight dm = new DateMidnight(myReport.getPit());
                                    if (!dayMap.containsKey(dm)) {
                                        dayMap.put(dm, new ArrayList<NReport>());
                                    }
                                    dayMap.get(dm).add(myReport);
                                    Collections.sort(dayMap.get(dm));
                                    reportMap.put(myReport, createCP4(myReport));
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
            mypanel.add(addButton);

//            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
//                JideButton btnPrint = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.print"), SYSConst.icon22print, new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent actionEvent) {
//
//
//                        SYSFilesTools.print(SYSTools.toHTML(NReportTools.getBerichteAsHTML(, false, true)), true);
//                    }
//                });
//                mypanel.add(btnPrint);
//

//
//    private void printBericht(int[] sel) {
//        TMPflegeberichte tm = (TMPflegeberichte) tblTB.getModel();
//        SYSFilesTools.print(SYSTools.toHTML(NReportTools.getBerichteAsHTML(SYSTools.getSelectionAsList(tm.getNReport(), sel), false, true)), true);
//    }

        }

//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
//            JideButton printButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.print"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")), new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    TMPflegeberichte tm = (TMPflegeberichte) tblTB.getModel();
//                    SYSFilesTools.print(SYSTools.toHTML(NReportTools.getBerichteAsHTML(tm.getNReport(), false, true)), true);
//                }
//            });
//            mypanel.add(printButton);
//        }
//

        searchPane.setContentPane(mypanel);
        searchPanes.add(searchPane);


        searchPane.setContentPane(mypanel);
        return searchPane;
    }

    private void reloadDisplay(final ArrayList<NReport> reportList) {
        /***
         *               _                 _ ____  _           _
         *      _ __ ___| | ___   __ _  __| |  _ \(_)___ _ __ | | __ _ _   _
         *     | '__/ _ \ |/ _ \ / _` |/ _` | | | | / __| '_ \| |/ _` | | | |
         *     | | |  __/ | (_) | (_| | (_| | |_| | \__ \ |_) | | (_| | |_| |
         *     |_|  \___|_|\___/ \__,_|\__,_|____/|_|___/ .__/|_|\__,_|\__, |
         *                                              |_|            |___/
         */

        dayMap.clear();
        reportMap.clear();


        final boolean withworker = true;
        if (withworker) {
            initPhase = true;

            OPDE.getMainframe().setBlocked(true);
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

            SwingWorker worker = new SwingWorker() {

                @Override
                protected Object doInBackground() throws Exception {
                    int progress = 0;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

                    for (NReport report : reportList) {
                        DateMidnight dateMidnight = new DateMidnight(report.getPit());
                        if (!dayMap.containsKey(dateMidnight)) {
                            dayMap.put(dateMidnight, new ArrayList<NReport>());
                        }
                        dayMap.get(dateMidnight).add(report);
                        reportMap.put(report, createCP4(report));
                        progress++;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, reportList.size()));
                    }
                    return null;
                }

                @Override
                protected void done() {
                    buildPanel();
                    initPhase = false;
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();

        } else {
            initPhase = true;
            // insert the reports into the appropriate sublists and create the CPs
            for (NReport report : reportList) {
                DateMidnight dateMidnight = new DateMidnight(report.getPit());
                if (!dayMap.containsKey(dateMidnight)) {
                    dayMap.put(dateMidnight, new ArrayList<NReport>());
                }
                dayMap.get(dateMidnight).add(report);
                reportMap.put(report, createCP4(report));
            }

            buildPanel();
            initPhase = false;
        }

    }


    private CollapsiblePane createCP4(final NReport report) {
        String title = "[" + DateFormat.getTimeInstance(DateFormat.SHORT).format(report.getPit()) + "] " + SYSTools.left(report.getText(), MAX_TEXT_LENGTH) + SYSTools.catchNull(NReportTools.getTagsAsHTML(report), " [", "]");
        title = (report.isObsolete() ? "<s>" : "") + title + (report.isObsolete() ? "</s>" : "");
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
        btnReport.setForeground(report.isObsolete() ? Color.gray : Color.black);

        titlePanelleft.add(btnReport);

        JPanel titlePanelright = new JPanel();
        titlePanelright.setLayout(new BoxLayout(titlePanelright, BoxLayout.LINE_AXIS));


        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {


            /***
             *      _     _         _____    _ _ _
             *     | |__ | |_ _ __ | ____|__| (_) |_
             *     | '_ \| __| '_ \|  _| / _` | | __|
             *     | |_) | |_| | | | |__| (_| | | |_
             *     |_.__/ \__|_| |_|_____\__,_|_|\__|
             *
             */
            final JButton btnEdit = new JButton(SYSConst.icon22edit1);
            btnEdit.setPressedIcon(SYSConst.icon22edit1Pressed);
            btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnEdit.setContentAreaFilled(false);
            btnEdit.setBorder(null);
            btnEdit.setToolTipText(OPDE.lang.getString(internalClassID + ".btnedit.tooltip"));
            btnEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (!NReportTools.isChangeable(report)) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
                        return;
                    }
                    new DlgReport((NReport) report.clone(), new Closure() {
                        @Override
                        public void execute(Object result) {
                            if (result != null) {

                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    NReport newReport = em.merge((NReport) result);
                                    NReport oldReport = em.merge((NReport) report);

                                    em.lock(oldReport, LockModeType.OPTIMISTIC);
                                    newReport.setReplacementFor(oldReport);

                                    for (SYSNR2FILE oldAssignment : oldReport.getAttachedFiles()) {
                                        em.remove(oldAssignment);
                                    }
                                    oldReport.getAttachedFiles().clear();
                                    for (SYSNR2PROCESS oldAssignment : oldReport.getAttachedProcessConnections()) {
                                        em.remove(oldAssignment);
                                    }
                                    oldReport.getAttachedProcessConnections().clear();

                                    oldReport.setEditedBy(em.merge(OPDE.getLogin().getUser()));
                                    oldReport.setEditpit(new Date());
                                    oldReport.setReplacedBy(newReport);

                                    em.getTransaction().commit();
                                    DateMidnight dm = new DateMidnight(newReport.getPit());
                                    if (!dayMap.containsKey(dm)) {
                                        dayMap.put(dm, new ArrayList<NReport>());
                                    }
                                    dayMap.get(dm).remove(report);
                                    dayMap.get(dm).add(newReport);
                                    dayMap.get(dm).add(oldReport);
                                    Collections.sort(dayMap.get(dm));
                                    reportMap.remove(report);
                                    reportMap.put(newReport, createCP4(newReport));
                                    reportMap.put(oldReport, createCP4(oldReport));
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
            btnEdit.setEnabled(!report.isObsolete());
            titlePanelright.add(btnEdit);

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
                    new DlgYesNo(OPDE.lang.getString("misc.questions.delete"), SYSConst.icon48delete, new Closure() {
                        @Override
                        public void execute(Object answer) {
                            if (answer.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    NReport delReport = em.merge(report);
                                    em.lock(delReport, LockModeType.OPTIMISTIC);
                                    delReport.setDeletedBy(em.merge(OPDE.getLogin().getUser()));
                                    for (SYSNR2FILE oldAssignment : delReport.getAttachedFiles()) {
                                        em.remove(oldAssignment);
                                    }
                                    delReport.getAttachedFiles().clear();
                                    for (SYSNR2PROCESS oldAssignment : delReport.getAttachedProcessConnections()) {
                                        em.remove(oldAssignment);
                                    }
                                    delReport.getAttachedProcessConnections().clear();
                                    em.getTransaction().commit();

                                    DateMidnight dm = new DateMidnight(delReport.getPit());
                                    if (!dayMap.containsKey(dm)) {
                                        dayMap.put(dm, new ArrayList<NReport>());
                                    }
                                    dayMap.get(dm).remove(report);
                                    dayMap.get(dm).add(delReport);
                                    Collections.sort(dayMap.get(dm));
                                    reportMap.remove(report);
                                    reportMap.put(delReport, createCP4(delReport));
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
            btnDelete.setEnabled(!report.isObsolete());
            titlePanelright.add(btnDelete);

            /***
             *      _     _       _____  _    ____
             *     | |__ | |_ _ _|_   _|/ \  / ___|___
             *     | '_ \| __| '_ \| | / _ \| |  _/ __|
             *     | |_) | |_| | | | |/ ___ \ |_| \__ \
             *     |_.__/ \__|_| |_|_/_/   \_\____|___/
             *
             */
            final JButton btnTAGs = new JButton(SYSConst.icon22todo);
            btnTAGs.setPressedIcon(SYSConst.icon22todoPressed);
            btnTAGs.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnTAGs.setContentAreaFilled(false);
            btnTAGs.setBorder(null);
            btnTAGs.setToolTipText(OPDE.lang.getString(internalClassID + ".btntags.tooltip"));
            btnTAGs.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (popup != null && popup.isPopupVisible()) {
                        popup.hidePopup();
                        return;
                    }
                    if (!NReportTools.isChangeable(report)) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
                        return;
                    }
                    ItemListener il = new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent itemEvent) {
                            JCheckBox cb = (JCheckBox) itemEvent.getSource();
                            NReportTAGS tag = (NReportTAGS) cb.getClientProperty("UserObject");

                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                NReport newReport = em.merge(report);
                                em.lock(newReport, LockModeType.OPTIMISTIC);

                                if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
                                    newReport.getTags().remove(tag);
                                } else {
                                    newReport.getTags().add(tag);
                                }

                                em.getTransaction().commit();
                                DateMidnight dm = new DateMidnight(newReport.getPit());
                                if (!dayMap.containsKey(dm)) {
                                    dayMap.put(dm, new ArrayList<NReport>());
                                }
                                dayMap.get(dm).remove(report);
                                dayMap.get(dm).add(newReport);
                                Collections.sort(dayMap.get(dm));
                                reportMap.remove(report);
                                reportMap.put(newReport, createCP4(newReport));
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
                    };

                    popup = new JidePopup();
                    JPanel pnl = NReportTAGSTools.createCheckBoxPanelForTags(il, report.getTags(), new GridLayout(8, 4));
                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                    popup.setOwner(btnTAGs);
                    popup.removeExcludedComponent(btnTAGs);
                    popup.getContentPane().add(pnl);
                    popup.setDefaultFocusComponent(pnl);

                    GUITools.showPopup(popup, SwingConstants.WEST);

                }
            });
            btnTAGs.setEnabled(!report.isObsolete());
            titlePanelright.add(btnTAGs);

            /***
             *      _     _         __  __ _             _
             *     | |__ | |_ _ __ |  \/  (_)_ __  _   _| |_ ___  ___
             *     | '_ \| __| '_ \| |\/| | | '_ \| | | | __/ _ \/ __|
             *     | |_) | |_| | | | |  | | | | | | |_| | ||  __/\__ \
             *     |_.__/ \__|_| |_|_|  |_|_|_| |_|\__,_|\__\___||___/
             *
             */
            final JButton btnMinutes = new JButton(SYSConst.icon22clock);
            btnMinutes.setPressedIcon(SYSConst.icon22clockPressed);
            btnMinutes.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnMinutes.setContentAreaFilled(false);
            btnMinutes.setBorder(null);
            btnMinutes.setToolTipText(OPDE.lang.getString(internalClassID + ".btnminutes.tooltip"));
            btnMinutes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (!NReportTools.isChangeable(report)) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
                        return;
                    }
                    final JPopupMenu menu = SYSCalendar.getMinutesMenu(new int[]{1, 2, 3, 4, 5, 10, 15, 20, 30, 45, 60, 120, 240, 360}, new Closure() {
                        @Override
                        public void execute(Object o) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();

                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                NReport myReport = em.merge(report);
                                em.lock(myReport, LockModeType.OPTIMISTIC);

                                myReport.setMinutes((Integer) o);
                                myReport.setEditpit(new Date());

                                em.getTransaction().commit();

                                DateMidnight dm = new DateMidnight(myReport.getPit());
                                if (!dayMap.containsKey(dm)) {
                                    dayMap.put(dm, new ArrayList<NReport>());
                                }
                                dayMap.get(dm).add(myReport);
                                Collections.sort(dayMap.get(dm));
                                reportMap.put(myReport, createCP4(myReport));
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

                    menu.show(btnMinutes, 0, btnMinutes.getHeight());
                }
            });
            btnMinutes.setEnabled(!report.isObsolete());
            titlePanelright.add(btnMinutes);
        }

        /***
         *      _     _         _____ _ _
         *     | |__ | |_ _ __ |  ___(_) | ___  ___
         *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
         *     | |_) | |_| | | |  _| | | |  __/\__ \
         *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
         *
         */
        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.INSERT)) {
            final JButton btnFiles = new JButton(SYSConst.icon22attach);
            btnFiles.setPressedIcon(SYSConst.icon22attachPressed);
            btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnFiles.setContentAreaFilled(false);
            btnFiles.setBorder(null);
            btnFiles.setToolTipText(OPDE.lang.getString(internalClassID + ".btnfiles.tooltip"));
            btnFiles.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    Closure closure = null;
                    if (!report.isObsolete()) {
                        closure = new Closure() {
                            @Override
                            public void execute(Object o) {
                                EntityManager em = OPDE.createEM();
                                NReport myReport = em.merge(report);
                                em.refresh(myReport);
                                DateMidnight dm = new DateMidnight(myReport.getPit());
                                if (!dayMap.containsKey(dm)) {
                                    dayMap.put(dm, new ArrayList<NReport>());
                                }
                                dayMap.get(dm).remove(report);
                                dayMap.get(dm).add(myReport);
                                Collections.sort(dayMap.get(dm));
                                reportMap.remove(report);
                                reportMap.put(myReport, createCP4(myReport));
                                buildPanel();
                                em.close();
                            }
                        };
                    }
                    new DlgFiles(report, closure);
                }
            });

            btnFiles.setEnabled(!report.isObsolete());
            if (report.getAttachedFiles().size() > 0) {
                JLabel lblNum = new JLabel(Integer.toString(report.getAttachedFiles().size()), SYSConst.icon16redStar, SwingConstants.CENTER);
                lblNum.setFont(SYSConst.ARIAL10BOLD);
                lblNum.setForeground(Color.YELLOW);
                lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
                DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnFiles, lblNum, DefaultOverlayable.SOUTH_EAST);
                overlayableBtn.setOpaque(false);
                titlePanelright.add(overlayableBtn);
            } else {
                titlePanelright.add(btnFiles);
            }

        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.INSERT)) {
            /***
             *      _     _         ____
             *     | |__ | |_ _ __ |  _ \ _ __ ___   ___ ___  ___ ___
             *     | '_ \| __| '_ \| |_) | '__/ _ \ / __/ _ \/ __/ __|
             *     | |_) | |_| | | |  __/| | | (_) | (_|  __/\__ \__ \
             *     |_.__/ \__|_| |_|_|   |_|  \___/ \___\___||___/___/
             *
             */
            final JButton btnProcess = new JButton(SYSConst.icon22link);
            btnProcess.setPressedIcon(SYSConst.icon22linkPressed);
            btnProcess.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnProcess.setContentAreaFilled(false);
            btnProcess.setBorder(null);
            btnProcess.setToolTipText(OPDE.lang.getString(internalClassID + ".btnprocess.tooltip"));
            btnProcess.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    Closure closure = null;
                    if (!report.isObsolete()) {
                        closure = new Closure() {
                            @Override
                            public void execute(Object o) {
                                if (o == null) {
                                    return;
                                }
                                Pair<ArrayList<QProcess>, ArrayList<QProcess>> result = (Pair<ArrayList<QProcess>, ArrayList<QProcess>>) o;

                                ArrayList<QProcess> assigned = result.getFirst();
                                ArrayList<QProcess> unassigned = result.getSecond();

                                EntityManager em = OPDE.createEM();

                                try {
                                    em.getTransaction().begin();

                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    NReport myReport = em.merge(report);
                                    em.lock(myReport, LockModeType.OPTIMISTIC_FORCE_INCREMENT);


                                    for (SYSNR2PROCESS linkObject : myReport.getAttachedProcessConnections()) {
                                        if (unassigned.contains(linkObject.getQProcess())) {
                                            em.remove(em.merge(linkObject));
                                        }
                                    }

                                    for (QProcess qProcess : assigned) {
                                        List<QProcessElement> listElements = qProcess.getElements();
                                        if (!listElements.contains(myReport)) {
                                            QProcess myQProcess = em.merge(qProcess);
                                            SYSNR2PROCESS myLinkObject = em.merge(new SYSNR2PROCESS(myQProcess, myReport));
                                            qProcess.getAttachedNReportConnections().add(myLinkObject);
                                            myReport.getAttachedProcessConnections().add(myLinkObject);
                                        }
                                    }

                                    em.getTransaction().commit();

                                    DateMidnight dm = new DateMidnight(myReport.getPit());
                                    if (!dayMap.containsKey(dm)) {
                                        dayMap.put(dm, new ArrayList<NReport>());
                                    }
                                    dayMap.get(dm).remove(report);
                                    dayMap.get(dm).add(myReport);
                                    Collections.sort(dayMap.get(dm));
                                    reportMap.remove(report);
                                    reportMap.put(myReport, createCP4(myReport));
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
                        };
                    }
                    new DlgProcessAssign(report, closure);
                }
            });
            btnProcess.setEnabled(!report.isObsolete());

            if (!report.getAttachedProcessConnections().isEmpty()) {
                JLabel lblNum = new JLabel(Integer.toString(report.getAttachedProcessConnections().size()), SYSConst.icon16redStar, SwingConstants.CENTER);
                lblNum.setFont(SYSConst.ARIAL10BOLD);
                lblNum.setForeground(Color.YELLOW);
                lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
                DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnProcess, lblNum, DefaultOverlayable.SOUTH_EAST);
                overlayableBtn.setOpaque(false);
                titlePanelright.add(overlayableBtn);
            } else {
                titlePanelright.add(btnProcess);
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

        cp.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                JTextPane contentPane = new JTextPane();
                contentPane.setContentType("text/html");
                contentPane.setEditable(false);
                contentPane.setText(SYSTools.toHTMLForScreen(NReportTools.getAsHTML(report)));
                cp.setContentPane(contentPane);
            }
        });
        cp.setBackground(SYSCalendar.getBG(SYSCalendar.whatShiftIs(report.getPit())));
        cp.setHorizontalAlignment(SwingConstants.LEADING);
        cp.setOpaque(false);

        return cp;
    }

    private void buildPanel() {
        OPDE.debug(cpReports.getComponentCount());
        cpReports.removeAll();

        JButton older = new JButton(OPDE.lang.getString("misc.msg.olderEntries"), SYSConst.icon22down);
        older.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (reportMap.containsKey(firstReport)) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.noOlderEntries")));
                    return;
                }
                DateMidnight dm = new DateMidnight(jdcVon.getDate());
                jdcVon.setDate(dm.minusWeeks(WEEKS_BACK).minusDays(1).toDate());
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jspReports.getVerticalScrollBar().setValue(0);
                    }
                });
            }
        });
        JButton newer = new JButton(OPDE.lang.getString("misc.msg.newerEntries"), SYSConst.icon22up);
        newer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (new DateMidnight(jdcVon.getDate()).plusWeeks(WEEKS_BACK).isAfterNow()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.noNewerEntries")));
                    return;
                }
                DateMidnight dm = new DateMidnight(jdcVon.getDate());
                jdcVon.setDate(dm.plusWeeks(WEEKS_BACK).plusDays(1).toDate());
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jspReports.getVerticalScrollBar().setValue(jspReports.getVerticalScrollBar().getMaximum());
                    }
                });
            }
        });

        cpReports.setLayout(new JideBoxLayout(cpReports, JideBoxLayout.Y_AXIS));

        cpReports.add(newer);
        NReportTAGS tag = cmbAuswahl.getSelectedIndex() == 0 ? null : (NReportTAGS) cmbAuswahl.getSelectedItem();

        boolean empty = true;

        if (!dayMap.isEmpty()) {

            ArrayList<DateMidnight> dateList = new ArrayList(dayMap.keySet());
            Collections.sort(dateList, new Comparator<DateMidnight>() {
                @Override
                public int compare(DateMidnight o1, DateMidnight o2) {
                    return o1.compareTo(o2) * -1;
                }
            });

            int year = dateList.get(0).getYear();
            int currentYear = year;
            HashMap hollidays = SYSCalendar.getFeiertage(year);

            for (final DateMidnight date : dateList) {

                if (date.getYear() != currentYear) {
                    currentYear = date.getYear();
                    hollidays = SYSCalendar.getFeiertage(currentYear);
                }


                JPanel dayPanel = new JPanel();
                dayPanel.setLayout(new VerticalLayout());

                for (NReport report : dayMap.get(date)) {

                    NReport report2add = report;

                    if (tag != null && !report.getTags().contains(tag)) {
                        report2add = null;
                    }

                    if (report.isObsolete() && !tbShowReplaced.isSelected()) {
                        report2add = null;
                    }

                    if (report2add != null) {
                        dayPanel.add(reportMap.get(report2add));
                    }

                }

                if (dayPanel.getComponentCount() > 0) {
                    // create header panel for that day
                    SimpleDateFormat df = new SimpleDateFormat("EEEE, dd.MM.yyyy");

                    String holliday = SYSTools.catchNull(hollidays.get(DateTimeFormat.forPattern("yyyy-MM-dd").print(date)));
                    String title = df.format(date.toDate()) + (holliday.isEmpty() ? "" : " " + holliday);

                    final CollapsiblePane dayPane = new CollapsiblePane(title);
                    dayPane.setSlidingDirection(SwingConstants.SOUTH);
                    dayPane.setFont(SYSConst.ARIAL20);


                    final JButton btnPrint = new JButton(SYSConst.icon22print);
                    btnPrint.setPressedIcon(SYSConst.icon22printPressed);

                    btnPrint.setContentAreaFilled(false);
                    btnPrint.setBorder(null);
                    btnPrint.setToolTipText(OPDE.lang.getString(internalClassID + ".btnprint.tooltip"));
                    btnPrint.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SYSFilesTools.print(SYSTools.toHTML(NReportTools.getBerichteAsHTML(dayMap.get(date), false, true)), true);
                        }
                    });

                    dayPane.setTitleComponent(btnPrint);

                    if (!holliday.isEmpty()) {
                        dayPane.setBackground(SYSConst.colorHolliday);
                    } else if (date.getDayOfWeek() == DateTimeConstants.SATURDAY || date.getDayOfWeek() == DateTimeConstants.SUNDAY) {
                        dayPane.setBackground(SYSConst.colorWeekend);
                    } else {
                        dayPane.setBackground(SYSConst.colorWeekday);
                    }
                    dayPane.setContentPane(dayPanel);
                    dayPane.setCollapsible(false);
                    dayPane.setOpaque(false);

                    cpReports.add(dayPane);
                    empty = false;
                }
            }
        }

        if (empty) {
            CollapsiblePane emptyCP = new CollapsiblePane(OPDE.lang.getString(internalClassID + ".noreports"));
            emptyCP.setCollapsible(false);
            try {
                emptyCP.setCollapsed(false);
            } catch (PropertyVetoException e) {
                OPDE.error(e);
            }
            cpReports.add(emptyCP);
        }

        cpReports.add(older);
        cpReports.addExpansion();

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspReports;
    private CollapsiblePanes cpReports;
    // End of variables declaration//GEN-END:variables
}
