/*
 * OffenePflege
 * Copyright (C) 2006-2012 Torsten Löhr
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
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import entity.files.SYSNR2FILE;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.process.SYSNR2PROCESS;
import entity.reports.NReport;
import entity.reports.NReportTAGSTools;
import entity.reports.NReportTools;
import op.OPDE;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

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

    //    private HashMap<DateMidnight, ArrayList<NReport>> dayMap;
    private HashMap<NReport, CollapsiblePane> reportMap;
    private HashMap<String, CollapsiblePane> cpMap;
    private HashMap<String, JPanel> contentmap;
    private HashMap<String, ArrayList<NReport>> valuecache;
    private HashMap<NReport, JPanel> linemap;

    private Resident resident;
    private JPopupMenu menu;
    private boolean initPhase;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private NReport firstReport;

    Format monthFormatter = new SimpleDateFormat("MMMM yyyy");
    Format weekFormater = new SimpleDateFormat("w yyyy");
    Format dayFormat = new SimpleDateFormat("EEEE, dd.MM.yyyy");


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
        contentmap = new HashMap<String, JPanel>();
//        dayMap = new HashMap<DateMidnight, ArrayList<NReport>>();
        cpMap = new HashMap<String, CollapsiblePane>();
        valuecache = new HashMap<String, ArrayList<NReport>>();
        reportMap = new HashMap<NReport, CollapsiblePane>();
        linemap = new HashMap<NReport, JPanel>();
        prepareSearchArea();
    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);
        searchPanes.add(addCommands());
//        searchPanes.add(addFilters());
        searchPanes.addExpansion();
    }

    @Override
    public void reload() {
//        if (tbFilesOnly.isSelected()) {
//            reloadDisplay(NReportTools.getReportsWithFilesOnly(resident));
//        } else {
//            reloadDisplay(NReportTools.getNReports(resident, new DateMidnight()));
//        }

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
        cpsReports = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspReports ========
        {

            //======== cpsReports ========
            {
                cpsReports.setLayout(new BoxLayout(cpsReports, BoxLayout.X_AXIS));
            }
            jspReports.setViewportView(cpsReports);
        }
        add(jspReports);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void cleanup() {
        contentmap.clear();
        cpMap.clear();
        cpsReports.removeAll();
//        dayMap.clear();
        linemap.clear();
        valuecache.clear();

    }

    @Override
    public void switchResident(Resident bewohner) {
        this.resident = bewohner;
        OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(bewohner));
//        txtSearch.setText(null);
//        firstReport = NReportTools.getFirstReport(resident);
//        jdcVon.setMaxSelectableDate(new Date());
//        jdcVon.setMinSelectableDate(firstReport.getPit());
        reloadDisplay();
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
//                    reloadDisplay(NReportTools.getReports(resident, txtSearch.getText()));
                }
            }
        });

        labelPanel.add(txtSearch);

//        jdcVon = new JDateChooser(new Date());
//        jdcVon.setBackground(Color.WHITE);
//        jdcVon.setFont(SYSConst.ARIAL14);
//        jdcVon.addPropertyChangeListener(new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                if (initPhase) {
//                    return;
//                }
//                if (!evt.getPropertyName().equals("date")) {
//                    return;
//                }
//                reloadDisplay(NReportTools.getReports(resident, jdcVon.getDate(), WEEKS_BACK));
//            }
//        });
//        labelPanel.add(jdcVon);
//
//        JPanel buttonPanel = new JPanel();
//        buttonPanel.setBackground(Color.WHITE);
//        buttonPanel.setLayout(new HorizontalLayout(5));
//        buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

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

//        tbShowReplaced = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.showreplaced"));
//        tbShowReplaced.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent itemEvent) {
//                if (initPhase) return;
////                SYSPropsTools.storeState(internalClassID + ":tbShowReplaced", tbShowReplaced);
//                buildPanel();
//            }
//        });
//        labelPanel.add(tbShowReplaced);
////        SYSPropsTools.restoreState(internalClassID + ":tbShowReplaced", tbShowReplaced);
//        tbShowReplaced.setHorizontalAlignment(SwingConstants.LEFT);


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
//                                    if (!dayMap.containsKey(dm)) {
//                                        dayMap.put(dm, new ArrayList<NReport>());
//                                    }
//                                    dayMap.get(dm).add(myReport);
//                                    Collections.sort(dayMap.get(dm));
//                                    reportMap.put(myReport, createCP4Month(myReport));
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

    private void reloadDisplay() {
        /***
         *               _                 _ ____  _           _
         *      _ __ ___| | ___   __ _  __| |  _ \(_)___ _ __ | | __ _ _   _
         *     | '__/ _ \ |/ _ \ / _` |/ _` | | | | / __| '_ \| |/ _` | | | |
         *     | | |  __/ | (_) | (_| | (_| | |_| | \__ \ |_) | | (_| | |_| |
         *     |_|  \___|_|\___/ \__,_|\__,_|____/|_|___/ .__/|_|\__,_|\__, |
         *                                              |_|            |___/
         */

        contentmap.clear();
        cpMap.clear();
        linemap.clear();
        valuecache.clear();

        final boolean withworker = false;
        if (withworker) {
            initPhase = true;

            OPDE.getMainframe().setBlocked(true);
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

            SwingWorker worker = new SwingWorker() {

                @Override
                protected Object doInBackground() throws Exception {
                    int progress = 0;
//                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));
//
//                    for (NReport report : reportList) {
//                        DateMidnight dateMidnight = new DateMidnight(report.getPit());
//                        if (!dayMap.containsKey(dateMidnight)) {
//                            dayMap.put(dateMidnight, new ArrayList<NReport>());
//                        }
//                        dayMap.get(dateMidnight).add(report);
////                        reportMap.put(report, createCP4Month(report));
//                        progress++;
//                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, reportList.size()));
//                    }
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
            Pair<DateTime, DateTime> minmax = NReportTools.getMinMax(resident);
            if (minmax != null) {
                DateMidnight start = minmax.getFirst().toDateMidnight().dayOfMonth().withMinimumValue();
                DateMidnight end = resident.isActive() ? new DateMidnight() : minmax.getSecond().toDateMidnight().dayOfMonth().withMinimumValue();
                for (int year = end.getYear(); year >= start.getYear(); year--) {
                    createCP4Year(year, start, end);
                }
            }

            // TODO: EXPAND THE LAST 2 WEEKS
//            for (NReport report : reportList) {
//                DateMidnight dateMidnight = new DateMidnight(report.getPit());
//                if (!dayMap.containsKey(dateMidnight)) {
//                    dayMap.put(dateMidnight, new ArrayList<NReport>());
//                }
//                dayMap.get(dateMidnight).add(report);
//                reportMap.put(report, createCP4Month(report));
//            }

            buildPanel();
            initPhase = false;
        }

    }

    private CollapsiblePane createCP4Year(final int year, DateMidnight min, DateMidnight max) {
        /***
         *                          _        ____ ____     __             __   _______    _    ____
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \   / _| ___  _ __  \ \ / / ____|  / \  |  _ \
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | | |_ / _ \| '__|  \ V /|  _|   / _ \ | |_) |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/  |  _| (_) | |      | | | |___ / ___ \|  _ <
         *      \___|_|  \___|\__,_|\__\___|\____|_|     |_|  \___/|_|      |_| |_____/_/   \_\_| \_\
         *
         */

        final DateMidnight start = new DateMidnight(year, 1, 1).isBefore(min.dayOfMonth().withMinimumValue()) ? min.dayOfMonth().withMinimumValue() : new DateMidnight(year, 1, 1);
        final DateMidnight end = new DateMidnight(year, 12, 31).isAfter(max.dayOfMonth().withMaximumValue()) ? max.dayOfMonth().withMaximumValue() : new DateMidnight(year, 12, 31);

        final String keyYear = Integer.toString(year) + ".year";
        if (!cpMap.containsKey(keyYear)) {
            cpMap.put(keyYear, new CollapsiblePane());
            try {
                cpMap.get(keyYear).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

        final CollapsiblePane cpYear = cpMap.get(keyYear);


        String title = "<html><font size=+1>" +
                "<b>" + Integer.toString(year) + "</b>" +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpYear.setCollapsed(!cpYear.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });


        final JButton btnExpandAll = new JButton(SYSConst.icon22expand);
        btnExpandAll.setPressedIcon(SYSConst.icon22addPressed);
        btnExpandAll.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnExpandAll.setContentAreaFilled(false);
        btnExpandAll.setBorder(null);
        btnExpandAll.setToolTipText(OPDE.lang.getString("misc.msg.expandall"));
        btnExpandAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    GUITools.setCollapsed(cpYear, false);
                } catch (PropertyVetoException e) {
                    // bah!
                }
            }


        });
        cptitle.getRight().add(btnExpandAll);

        final JButton btnCollapseAll = new JButton(SYSConst.icon22collapse);
        btnCollapseAll.setPressedIcon(SYSConst.icon22addPressed);
        btnCollapseAll.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnCollapseAll.setContentAreaFilled(false);
        btnCollapseAll.setBorder(null);
        btnCollapseAll.setToolTipText(OPDE.lang.getString("misc.msg.collapseall"));
        btnCollapseAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    GUITools.setCollapsed(cpYear, true);
                } catch (PropertyVetoException e) {
                    // bah!
                }
            }


        });
        cptitle.getRight().add(btnCollapseAll);

        cpYear.setTitleLabelComponent(cptitle.getMain());
        cpYear.setSlidingDirection(SwingConstants.SOUTH);


        /***
         *           _ _      _            _
         *       ___| (_) ___| | _____  __| |   ___  _ __    _   _  ___  __ _ _ __
         *      / __| | |/ __| |/ / _ \/ _` |  / _ \| '_ \  | | | |/ _ \/ _` | '__|
         *     | (__| | | (__|   <  __/ (_| | | (_) | | | | | |_| |  __/ (_| | |
         *      \___|_|_|\___|_|\_\___|\__,_|  \___/|_| |_|  \__, |\___|\__,_|_|
         *                                                   |___/
         */
        cpYear.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                JPanel pnlContent = new JPanel(new VerticalLayout());

                // somebody clicked on the year
                for (DateMidnight month = end; month.compareTo(start) >= 0; month = month.minusMonths(1)) {
                    pnlContent.add(createCP4Month(month));
                }

                cpYear.setContentPane(pnlContent);
                cpYear.setOpaque(false);
            }
        });
//        cpYear.setBackground(getColor(vtype, SYSConst.light4));

        if (!cpYear.isCollapsed()) {
            JPanel pnlContent = new JPanel(new VerticalLayout());

            for (DateMidnight month = end; month.compareTo(start) >= 0; month = month.minusMonths(1)) {
                pnlContent.add(createCP4Month(month));
            }

            cpYear.setContentPane(pnlContent);
            cpYear.setOpaque(false);
        }

        cpYear.setHorizontalAlignment(SwingConstants.LEADING);
        cpYear.setOpaque(false);

        return cpYear;
    }


    private CollapsiblePane createCP4Month(final DateMidnight month) {
        /***
         *                          _        ____ ____     __                      __  __  ___  _   _ _____ _   _
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \   / _| ___  _ __    __ _  |  \/  |/ _ \| \ | |_   _| | | |
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | | |_ / _ \| '__|  / _` | | |\/| | | | |  \| | | | | |_| |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/  |  _| (_) | |    | (_| | | |  | | |_| | |\  | | | |  _  |
         *      \___|_|  \___|\__,_|\__\___|\____|_|     |_|  \___/|_|     \__,_| |_|  |_|\___/|_| \_| |_| |_| |_|
         *
         */
        final String key = monthFormatter.format(month.toDate()) + ".month";
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        final CollapsiblePane cpMonth = cpMap.get(key);

        String title = "<html><b>" +
                monthFormatter.format(month.toDate()) +
                "</b>" +
                "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpMonth.setCollapsed(!cpMonth.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

//        /***
//         *      ____       _       _   __  __             _   _
//         *     |  _ \ _ __(_)_ __ | |_|  \/  | ___  _ __ | |_| |__
//         *     | |_) | '__| | '_ \| __| |\/| |/ _ \| '_ \| __| '_ \
//         *     |  __/| |  | | | | | |_| |  | | (_) | | | | |_| | | |
//         *     |_|   |_|  |_|_| |_|\__|_|  |_|\___/|_| |_|\__|_| |_|
//         *
//         */
//        final JButton btnPrintMonth = new JButton(SYSConst.icon22print2);
//        btnPrintMonth.setPressedIcon(SYSConst.icon22print2Pressed);
//        btnPrintMonth.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        btnPrintMonth.setContentAreaFilled(false);
//        btnPrintMonth.setBorder(null);
//        btnPrintMonth.setToolTipText(OPDE.lang.getString("misc.tooltips.btnprintmonth"));
//        btnPrintMonth.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//
//                if (!valuecache.containsKey(key)) {
//                    createContentPanel4Month(vtype, month);
//                }
//                SYSFilesTools.print(ResValueTools.getAsHTML(valuecache.get(key)), true);
//            }
//        });
//        cptitle.getRight().add(btnPrintMonth);
//
        cpMonth.setTitleLabelComponent(cptitle.getMain());
        cpMonth.setSlidingDirection(SwingConstants.SOUTH);
        cpMonth.setOpaque(false);
        cpMonth.setHorizontalAlignment(SwingConstants.LEADING);
//        cpMonth.setBackground(getColor(vtype, SYSConst.light3));

        /***
         *           _ _      _            _                                       _   _
         *       ___| (_) ___| | _____  __| |   ___  _ __    _ __ ___   ___  _ __ | |_| |__
         *      / __| | |/ __| |/ / _ \/ _` |  / _ \| '_ \  | '_ ` _ \ / _ \| '_ \| __| '_ \
         *     | (__| | | (__|   <  __/ (_| | | (_) | | | | | | | | | | (_) | | | | |_| | | |
         *      \___|_|_|\___|_|\_\___|\__,_|  \___/|_| |_| |_| |_| |_|\___/|_| |_|\__|_| |_|
         *
         */
        cpMonth.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpMonth.setContentPane(createContentPanel4Month(month));
            }
        });

        if (!cpMonth.isCollapsed()) {
            cpMonth.setContentPane(createContentPanel4Month(month));
        }


        return cpMonth;
    }


    private JPanel createContentPanel4Month(DateMidnight month) {
        /***
         *                      _             _      __              __  __  ___  _   _ _____ _   _
         *       ___ ___  _ __ | |_ ___ _ __ | |_   / _| ___  _ __  |  \/  |/ _ \| \ | |_   _| | | |
         *      / __/ _ \| '_ \| __/ _ \ '_ \| __| | |_ / _ \| '__| | |\/| | | | |  \| | | | | |_| |
         *     | (_| (_) | | | | ||  __/ | | | |_  |  _| (_) | |    | |  | | |_| | |\  | | | |  _  |
         *      \___\___/|_| |_|\__\___|_| |_|\__| |_|  \___/|_|    |_|  |_|\___/|_| \_| |_| |_| |_|
         *
         */


//        if (!contentmap.containsKey(key)) {

        JPanel pnlMonth = new JPanel(new VerticalLayout());

        pnlMonth.setOpaque(false);

        DateMidnight now = new DateMidnight();

        boolean sameMonth = now.dayOfMonth().withMaximumValue().equals(month.dayOfMonth().withMaximumValue());

        final DateMidnight start = sameMonth ? now : month.dayOfMonth().withMaximumValue();
        final DateMidnight end = month.dayOfMonth().withMinimumValue();

        for (DateMidnight week = start; end.compareTo(week) <= 0; week = week.minusWeeks(1)) {
            pnlMonth.add(createCP4Week(week));
        }

        return pnlMonth;
    }

    private CollapsiblePane createCP4Week(final DateMidnight week) {
        /***
         *                          _        ____ ____     __                      __  __  ___  _   _ _____ _   _
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \   / _| ___  _ __    __ _  |  \/  |/ _ \| \ | |_   _| | | |
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | | |_ / _ \| '__|  / _` | | |\/| | | | |  \| | | | | |_| |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/  |  _| (_) | |    | (_| | | |  | | |_| | |\  | | | |  _  |
         *      \___|_|  \___|\__,_|\__\___|\____|_|     |_|  \___/|_|     \__,_| |_|  |_|\___/|_| \_| |_| |_| |_|
         *
         */
        final String key = weekFormater.format(week.toDate()) + ".week";
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        final CollapsiblePane cpWeek = cpMap.get(key);

        String title = "<html><b>" +
                DateFormat.getDateInstance(DateFormat.SHORT).format(week.dayOfWeek().withMaximumValue().toDate()) + " - " +
                DateFormat.getDateInstance(DateFormat.SHORT).format(week.dayOfWeek().withMinimumValue().toDate()) +
                " (" +
                OPDE.lang.getString("misc.msg.weekinyear") +
                week.getWeekOfWeekyear() +
                ")" +
                "</b>" +
                "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpWeek.setCollapsed(!cpWeek.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

        GUITools.addExpandCollapseButtons(cpWeek, cptitle.getRight());
//        /***
//         *      ____       _       _   __  __             _   _
//         *     |  _ \ _ __(_)_ __ | |_|  \/  | ___  _ __ | |_| |__
//         *     | |_) | '__| | '_ \| __| |\/| |/ _ \| '_ \| __| '_ \
//         *     |  __/| |  | | | | | |_| |  | | (_) | | | | |_| | | |
//         *     |_|   |_|  |_|_| |_|\__|_|  |_|\___/|_| |_|\__|_| |_|
//         *
//         */
//        final JButton btnPrintMonth = new JButton(SYSConst.icon22print2);
//        btnPrintMonth.setPressedIcon(SYSConst.icon22print2Pressed);
//        btnPrintMonth.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        btnPrintMonth.setContentAreaFilled(false);
//        btnPrintMonth.setBorder(null);
//        btnPrintMonth.setToolTipText(OPDE.lang.getString("misc.tooltips.btnprintmonth"));
//        btnPrintMonth.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//
//                if (!valuecache.containsKey(key)) {
//                    createContentPanel4Month(vtype, month);
//                }
//                SYSFilesTools.print(ResValueTools.getAsHTML(valuecache.get(key)), true);
//            }
//        });
//        cptitle.getRight().add(btnPrintMonth);
//
        cpWeek.setTitleLabelComponent(cptitle.getMain());
        cpWeek.setSlidingDirection(SwingConstants.SOUTH);
        cpWeek.setOpaque(false);
        cpWeek.setHorizontalAlignment(SwingConstants.LEADING);
//        cpMonth.setBackground(getColor(vtype, SYSConst.light3));

        /***
         *           _ _      _            _                                       _   _
         *       ___| (_) ___| | _____  __| |   ___  _ __    _ __ ___   ___  _ __ | |_| |__
         *      / __| | |/ __| |/ / _ \/ _` |  / _ \| '_ \  | '_ ` _ \ / _ \| '_ \| __| '_ \
         *     | (__| | | (__|   <  __/ (_| | | (_) | | | | | | | | | | (_) | | | | |_| | | |
         *      \___|_|_|\___|_|\_\___|\__,_|  \___/|_| |_| |_| |_| |_|\___/|_| |_|\__|_| |_|
         *
         */
        cpWeek.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpWeek.setContentPane(createWeekContentPanel4(week));
            }
        });

        if (!cpWeek.isCollapsed()) {
            cpWeek.setContentPane(createWeekContentPanel4(week));
        }


        return cpWeek;
    }

    private JPanel createWeekContentPanel4(DateMidnight week) {
        JPanel pnlWeek = new JPanel(new VerticalLayout());

        pnlWeek.setOpaque(false);

        DateMidnight now = new DateMidnight();

        boolean sameWeek = now.dayOfWeek().withMaximumValue().equals(week.dayOfWeek().withMaximumValue());

        final DateMidnight start = sameWeek ? now : week.dayOfWeek().withMaximumValue();
        final DateMidnight end = week.dayOfWeek().withMinimumValue();

        for (DateMidnight day = start; end.compareTo(day) <= 0; day = day.minusDays(1)) {
            pnlWeek.add(createCP4Day(day));
        }

        return pnlWeek;
    }


    private CollapsiblePane createCP4Day(final DateMidnight day) {
        final String key = DateFormat.getDateInstance().format(day.toDate());
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
//            cpMap.get(key).setStyle(CollapsiblePane.PLAIN_STYLE);
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        final CollapsiblePane cpDay = cpMap.get(key);
        String titleDay = "<html><font size=+1>" +
                dayFormat.format(day.toDate()) +
                "</font></html>";
        final DefaultCPTitle titleCPDay = new DefaultCPTitle(titleDay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpDay.setCollapsed(!cpDay.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

        cpDay.setTitleLabelComponent(titleCPDay.getMain());
        cpDay.setSlidingDirection(SwingConstants.SOUTH);
        cpDay.setOpaque(false);
        cpDay.setHorizontalAlignment(SwingConstants.LEADING);
        cpDay.setStyle(CollapsiblePane.PLAIN_STYLE);
        cpDay.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpDay.setContentPane(createDayContent4(day));
            }
        });

        if (!cpDay.isCollapsed()) {
            cpDay.setContentPane(createDayContent4(day));
        }

        return cpDay;
    }

    private JPanel createDayContent4(DateMidnight day) {
        final String key = DateFormat.getDateInstance().format(day.toDate());
        if (contentmap.containsKey(key)) {
            return contentmap.get(key);
        }
        final JPanel dayPanel = new JPanel(new VerticalLayout());
        dayPanel.setOpaque(false);
        if (!valuecache.containsKey(key)) {
            valuecache.put(key, NReportTools.getNReports(resident, day));
        }

        int i = 0; // for zebra pattern
        for (final NReport nreport : valuecache.get(key)) {
            String title = "<html><table border=\"0\">" +
                    "<tr>" +
                    "<td width=\"100\" align=\"left\">" + DateFormat.getTimeInstance(DateFormat.SHORT).format(nreport.getPit()) + "</td>" +
                    "<td width=\"100\" align=\"left\">" + SYSTools.catchNull(NReportTools.getTagsAsHTML(nreport), " [", "]") + "</td>" +
                    "<td width=\"500\" align=\"left\">" + nreport.getText() + "</td>" +
                    "<td width=\"200\" align=\"left\">" + nreport.getUser().getFullname() + "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</html>";

            final DefaultCPTitle pnlSingle = new DefaultCPTitle(SYSTools.toHTMLForScreen(title), null);
            if (nreport.isObsolete()) {
                pnlSingle.getButton().setIcon(SYSConst.icon22eraser);
                pnlSingle.getButton().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        GUITools.showPopup(GUITools.getHTMLPopup(pnlSingle.getButton(), NReportTools.getInfoAsHTML(nreport)), SwingConstants.NORTH);
                    }
                });
            }
            if (nreport.isReplacement()) {
                pnlSingle.getButton().setIcon(SYSConst.icon22edited);
                pnlSingle.getButton().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        GUITools.showPopup(GUITools.getHTMLPopup(pnlSingle.getButton(), NReportTools.getInfoAsHTML(nreport)), SwingConstants.NORTH);
                    }
                });
            }
            /***
             *      _____    _ _ _
             *     | ____|__| (_) |_
             *     |  _| / _` | | __|
             *     | |__| (_| | | |_
             *     |_____\__,_|_|\__|
             *
             */
            final JButton btnEdit = new JButton(SYSConst.icon22edit3);
            btnEdit.setPressedIcon(SYSConst.icon22edit3Pressed);
            btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnEdit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnEdit.setContentAreaFilled(false);
            btnEdit.setBorder(null);
            btnEdit.setToolTipText(OPDE.lang.getString(internalClassID + ".btnEdit.tooltip"));
            btnEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgReport(nreport.clone(), new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {

                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    final NReport newReport = em.merge((NReport) o);
                                    NReport oldReport = em.merge(nreport);
                                    em.lock(oldReport, LockModeType.OPTIMISTIC);
                                    newReport.setReplacementFor(oldReport);

                                    for (SYSNR2FILE oldAssignment : oldReport.getAttachedFilesConnections()) {
                                        em.remove(oldAssignment);
                                    }
                                    oldReport.getAttachedFilesConnections().clear();
                                    for (SYSNR2PROCESS oldAssignment : oldReport.getAttachedProcessConnections()) {
                                        em.remove(oldAssignment);
                                    }
                                    oldReport.getAttachedProcessConnections().clear();

                                    oldReport.setEditedBy(em.merge(OPDE.getLogin().getUser()));
                                    oldReport.setEditDate(new Date());
                                    oldReport.setReplacedBy(newReport);

                                    em.getTransaction().commit();

                                    DateTime dt = new DateTime(newReport.getPit());

                                    final String keyYear = Integer.toString(dt.getYear()) + ".year";
                                    final String keyMonth = monthFormatter.format(dt.toDate()) + ".month";

                                    valuecache.get(keyMonth).remove(nreport);
                                    valuecache.get(keyMonth).add(oldReport);
                                    valuecache.get(keyMonth).add(newReport);
                                    Collections.sort(valuecache.get(keyMonth));

                                    contentmap.remove(keyMonth);
                                    createCP4Month(dt.toDateMidnight());

                                    try {
                                        cpMap.get(keyYear).setCollapsed(false);
                                        cpMap.get(keyMonth).setCollapsed(false);
                                    } catch (PropertyVetoException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    }

                                    buildPanel();

                                    GUITools.scroll2show(jspReports, cpMap.get(keyMonth), cpsReports, new Closure() {
                                        @Override
                                        public void execute(Object o) {
                                            GUITools.flashBackground(linemap.get(newReport), Color.YELLOW, 2);
                                        }
                                    });
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
            btnEdit.setEnabled(!nreport.isObsolete());
            pnlSingle.getRight().add(btnEdit);

            /***
             *      ____       _      _
             *     |  _ \  ___| | ___| |_ ___
             *     | | | |/ _ \ |/ _ \ __/ _ \
             *     | |_| |  __/ |  __/ ||  __/
             *     |____/ \___|_|\___|\__\___|
             *
             */
            final JButton btnDelete = new JButton(SYSConst.icon22delete);
            btnDelete.setPressedIcon(SYSConst.icon22delete);
            btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnDelete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnDelete.setContentAreaFilled(false);
            btnDelete.setBorder(null);
            btnDelete.setToolTipText(OPDE.lang.getString(internalClassID + ".btnDelete.tooltip"));
            btnDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgYesNo(OPDE.lang.getString("misc.questions.delete1") + "<br/><i>" + DateFormat.getDateTimeInstance().format(nreport.getPit()) + "</i><br/>" + OPDE.lang.getString("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                        @Override
                        public void execute(Object answer) {
                            if (answer.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    NReport delReport = em.merge(nreport);
                                    em.lock(delReport, LockModeType.OPTIMISTIC);
                                    delReport.setDeletedBy(em.merge(OPDE.getLogin().getUser()));
                                    for (SYSNR2FILE oldAssignment : delReport.getAttachedFilesConnections()) {
                                        em.remove(oldAssignment);
                                    }
                                    delReport.getAttachedFilesConnections().clear();
                                    for (SYSNR2PROCESS oldAssignment : delReport.getAttachedProcessConnections()) {
                                        em.remove(oldAssignment);
                                    }
                                    delReport.getAttachedProcessConnections().clear();
                                    em.getTransaction().commit();

                                    DateTime dt = new DateTime(delReport.getPit());
                                    final String keyYear = Integer.toString(dt.getYear()) + ".year";
                                    final String keyMonth = monthFormatter.format(dt.toDate()) + ".month";

                                    valuecache.get(keyMonth).remove(delReport);
                                    valuecache.get(keyMonth).add(delReport);
                                    Collections.sort(valuecache.get(keyMonth));

                                    contentmap.remove(keyMonth);
                                    createCP4Month(dt.toDateMidnight());

                                    try {
//                                            cpMap.get(keyType).setCollapsed(false);
                                        cpMap.get(keyYear).setCollapsed(false);
                                        cpMap.get(keyMonth).setCollapsed(false);
                                    } catch (PropertyVetoException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
                        }
                    });
                }
            });
            btnDelete.setEnabled(!nreport.isObsolete());
            pnlSingle.getRight().add(btnDelete);

            JPanel zebra = new JPanel();
            zebra.setLayout(new BoxLayout(zebra, BoxLayout.LINE_AXIS));
            zebra.setOpaque(true);
            if (i % 2 == 0){
                zebra.setBackground(SYSConst.blue1[SYSConst.light2]);
            } else {
                zebra.setBackground(Color.WHITE);
            }
            zebra.add(pnlSingle.getMain());
            i++;

            dayPanel.add(zebra);
            linemap.put(nreport, pnlSingle.getMain());
        }
        contentmap.put(key, dayPanel);
        return dayPanel;
    }

//    private CollapsiblePane createCP4Month(final NReport nreport) {
//        String title = "[" + DateFormat.getTimeInstance(DateFormat.SHORT).format(nreport.getPit()) + "] " + SYSTools.left(nreport.getText(), MAX_TEXT_LENGTH) + SYSTools.catchNull(NReportTools.getTagsAsHTML(nreport), " [", "]");
//        title = (nreport.isObsolete() ? "<s>" : "") + title + (nreport.isObsolete() ? "</s>" : "");
//        final CollapsiblePane cp = new CollapsiblePane();
//
//        /***
//         *      _   _ _____    _    ____  _____ ____
//         *     | | | | ____|  / \  |  _ \| ____|  _ \
//         *     | |_| |  _|   / _ \ | | | |  _| | |_) |
//         *     |  _  | |___ / ___ \| |_| | |___|  _ <
//         *     |_| |_|_____/_/   \_\____/|_____|_| \_\
//         *
//         */
//
//        JPanel titlePanelleft = new JPanel();
//        titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));
//
//        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    cpPres.setCollapsed(!cpPres.isCollapsed());
//                } catch (PropertyVetoException pve) {
//                    // BAH!
//                }
//            }
//        });
//
//
//        /***
//         *      _     _       _    _           _   _                _   _                _
//         *     | |   (_)_ __ | | _| |__  _   _| |_| |_ ___  _ __   | | | | ___  __ _  __| | ___ _ __
//         *     | |   | | '_ \| |/ / '_ \| | | | __| __/ _ \| '_ \  | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
//         *     | |___| | | | |   <| |_) | |_| | |_| || (_) | | | | |  _  |  __/ (_| | (_| |  __/ |
//         *     |_____|_|_| |_|_|\_\_.__/ \__,_|\__|\__\___/|_| |_| |_| |_|\___|\__,_|\__,_|\___|_|
//         *
//         */
//        JideButton btnReport = GUITools.createHyperlinkButton(SYSTools.toHTMLForScreen(title), null, null);
//        btnReport.setAlignmentX(Component.LEFT_ALIGNMENT);
//        btnReport.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                try {
//                    cp.setCollapsed(!cp.isCollapsed());
//                } catch (PropertyVetoException e) {
//                    OPDE.error(e);
//                }
//            }
//        });
//        btnReport.setForeground(nreport.isObsolete() ? Color.gray : Color.black);
//
//        titlePanelleft.add(btnReport);
//
//        JPanel titlePanelright = new JPanel();
//        titlePanelright.setLayout(new BoxLayout(titlePanelright, BoxLayout.LINE_AXIS));
//
//
////        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {
////
////
////            /***
////             *      _     _         _____    _ _ _
////             *     | |__ | |_ _ __ | ____|__| (_) |_
////             *     | '_ \| __| '_ \|  _| / _` | | __|
////             *     | |_) | |_| | | | |__| (_| | | |_
////             *     |_.__/ \__|_| |_|_____\__,_|_|\__|
////             *
////             */
////            final JButton btnEdit = new JButton(SYSConst.icon22edit3);
////            btnEdit.setPressedIcon(SYSConst.icon22edit3Pressed);
////            btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
////            btnEdit.setContentAreaFilled(false);
////            btnEdit.setBorder(null);
////            btnEdit.setToolTipText(OPDE.lang.getString(internalClassID + ".btnedit.tooltip"));
////            btnEdit.addActionListener(new ActionListener() {
////                @Override
////                public void actionPerformed(ActionEvent actionEvent) {
////                    if (!NReportTools.isChangeable(report)) {
////                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
////                        return;
////                    }
////                    new DlgReport(report.clone(), new Closure() {
////                        @Override
////                        public void execute(Object result) {
////                            if (result != null) {
////
////                                EntityManager em = OPDE.createEM();
////                                try {
////                                    em.getTransaction().begin();
////                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
////                                    NReport newReport = em.merge((NReport) result);
////                                    NReport oldReport = em.merge((NReport) report);
////
////                                    em.lock(oldReport, LockModeType.OPTIMISTIC);
////                                    newReport.setReplacementFor(oldReport);
////
////                                    for (SYSNR2FILE oldAssignment : oldReport.getAttachedFilesConnections()) {
////                                        em.remove(oldAssignment);
////                                    }
////                                    oldReport.getAttachedFilesConnections().clear();
////                                    for (SYSNR2PROCESS oldAssignment : oldReport.getAttachedProcessConnections()) {
////                                        em.remove(oldAssignment);
////                                    }
////                                    oldReport.getAttachedProcessConnections().clear();
////
////                                    oldReport.setEditedBy(em.merge(OPDE.getLogin().getUser()));
////                                    oldReport.setEditpit(new Date());
////                                    oldReport.setReplacedBy(newReport);
////
////                                    em.getTransaction().commit();
////                                    DateMidnight dm = new DateMidnight(newReport.getPit());
////                                    if (!dayMap.containsKey(dm)) {
////                                        dayMap.put(dm, new ArrayList<NReport>());
////                                    }
////                                    dayMap.get(dm).remove(report);
////                                    dayMap.get(dm).add(newReport);
////                                    dayMap.get(dm).add(oldReport);
////                                    Collections.sort(dayMap.get(dm));
////                                    reportMap.remove(report);
////                                    reportMap.put(newReport, createCP4Month(newReport));
////                                    reportMap.put(oldReport, createCP4Month(oldReport));
////                                    buildPanel();
////                                } catch (OptimisticLockException ole) {
////                                    if (em.getTransaction().isActive()) {
////                                        em.getTransaction().rollback();
////                                    }
////                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
////                                        OPDE.getMainframe().emptyFrame();
////                                        OPDE.getMainframe().afterLogin();
////                                    }
////                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
////                                } catch (Exception e) {
////                                    if (em.getTransaction().isActive()) {
////                                        em.getTransaction().rollback();
////                                    }
////                                    OPDE.fatal(e);
////                                } finally {
////                                    em.close();
////                                }
////                            }
////                        }
////                    });
////                }
////            });
////            btnEdit.setEnabled(!report.isObsolete());
////            titlePanelright.add(btnEdit);
////
////            /***
////             *      _     _         ____       _      _
////             *     | |__ | |_ _ __ |  _ \  ___| | ___| |_ ___
////             *     | '_ \| __| '_ \| | | |/ _ \ |/ _ \ __/ _ \
////             *     | |_) | |_| | | | |_| |  __/ |  __/ ||  __/
////             *     |_.__/ \__|_| |_|____/ \___|_|\___|\__\___|
////             *
////             */
////            final JButton btnDelete = new JButton(SYSConst.icon22delete);
////            btnDelete.setPressedIcon(SYSConst.icon22deletePressed);
////            btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
////            btnDelete.setContentAreaFilled(false);
////            btnDelete.setBorder(null);
////            btnDelete.setToolTipText(OPDE.lang.getString(internalClassID + ".btndelete.tooltip"));
////            btnDelete.addActionListener(new ActionListener() {
////                @Override
////                public void actionPerformed(ActionEvent actionEvent) {
////                    new DlgYesNo(OPDE.lang.getString("misc.questions.delete"), SYSConst.icon48delete, new Closure() {
////                        @Override
////                        public void execute(Object answer) {
////                            if (answer.equals(JOptionPane.YES_OPTION)) {
////                                EntityManager em = OPDE.createEM();
////                                try {
////                                    em.getTransaction().begin();
////                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
////                                    NReport delReport = em.merge(report);
////                                    em.lock(delReport, LockModeType.OPTIMISTIC);
////                                    delReport.setDeletedBy(em.merge(OPDE.getLogin().getUser()));
////                                    for (SYSNR2FILE oldAssignment : delReport.getAttachedFilesConnections()) {
////                                        em.remove(oldAssignment);
////                                    }
////                                    delReport.getAttachedFilesConnections().clear();
////                                    for (SYSNR2PROCESS oldAssignment : delReport.getAttachedProcessConnections()) {
////                                        em.remove(oldAssignment);
////                                    }
////                                    delReport.getAttachedProcessConnections().clear();
////                                    em.getTransaction().commit();
////
////                                    DateMidnight dm = new DateMidnight(delReport.getPit());
////                                    if (!dayMap.containsKey(dm)) {
////                                        dayMap.put(dm, new ArrayList<NReport>());
////                                    }
////                                    dayMap.get(dm).remove(report);
////                                    dayMap.get(dm).add(delReport);
////                                    Collections.sort(dayMap.get(dm));
////                                    reportMap.remove(report);
////                                    reportMap.put(delReport, createCP4Month(delReport));
////                                    buildPanel();
////                                } catch (OptimisticLockException ole) {
////                                    if (em.getTransaction().isActive()) {
////                                        em.getTransaction().rollback();
////                                    }
////                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
////                                        OPDE.getMainframe().emptyFrame();
////                                        OPDE.getMainframe().afterLogin();
////                                    }
////                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
////                                } catch (Exception e) {
////                                    if (em.getTransaction().isActive()) {
////                                        em.getTransaction().rollback();
////                                    }
////                                    OPDE.fatal(e);
////                                } finally {
////                                    em.close();
////                                }
////
////                            }
////                        }
////                    });
////                }
////            });
////            btnDelete.setEnabled(!report.isObsolete());
////            titlePanelright.add(btnDelete);
////
////            /***
////             *      _     _       _____  _    ____
////             *     | |__ | |_ _ _|_   _|/ \  / ___|___
////             *     | '_ \| __| '_ \| | / _ \| |  _/ __|
////             *     | |_) | |_| | | | |/ ___ \ |_| \__ \
////             *     |_.__/ \__|_| |_|_/_/   \_\____|___/
////             *
////             */
////            final JButton btnTAGs = new JButton(SYSConst.icon22todo);
////            btnTAGs.setPressedIcon(SYSConst.icon22todoPressed);
////            btnTAGs.setAlignmentX(Component.RIGHT_ALIGNMENT);
////            btnTAGs.setContentAreaFilled(false);
////            btnTAGs.setBorder(null);
////            btnTAGs.setToolTipText(OPDE.lang.getString(internalClassID + ".btntags.tooltip"));
////            btnTAGs.addActionListener(new ActionListener() {
////                @Override
////                public void actionPerformed(ActionEvent actionEvent) {
////                    if (popup != null && popup.isPopupVisible()) {
////                        popup.hidePopup();
////                        return;
////                    }
////                    if (!NReportTools.isChangeable(report)) {
////                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
////                        return;
////                    }
////                    ItemListener il = new ItemListener() {
////                        @Override
////                        public void itemStateChanged(ItemEvent itemEvent) {
////                            JCheckBox cb = (JCheckBox) itemEvent.getSource();
////                            NReportTAGS tag = (NReportTAGS) cb.getClientProperty("UserObject");
////
////                            EntityManager em = OPDE.createEM();
////                            try {
////                                em.getTransaction().begin();
////                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
////                                NReport newReport = em.merge(report);
////                                em.lock(newReport, LockModeType.OPTIMISTIC);
////
////                                if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
////                                    newReport.getTags().remove(tag);
////                                } else {
////                                    newReport.getTags().add(tag);
////                                }
////
////                                em.getTransaction().commit();
////                                DateMidnight dm = new DateMidnight(newReport.getPit());
////                                if (!dayMap.containsKey(dm)) {
////                                    dayMap.put(dm, new ArrayList<NReport>());
////                                }
////                                dayMap.get(dm).remove(report);
////                                dayMap.get(dm).add(newReport);
////                                Collections.sort(dayMap.get(dm));
////                                reportMap.remove(report);
////                                reportMap.put(newReport, createCP4Month(newReport));
////                                buildPanel();
////                            } catch (OptimisticLockException ole) {
////                                if (em.getTransaction().isActive()) {
////                                    em.getTransaction().rollback();
////                                }
////                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
////                                    OPDE.getMainframe().emptyFrame();
////                                    OPDE.getMainframe().afterLogin();
////                                }
////                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
////                            } catch (Exception e) {
////                                if (em.getTransaction().isActive()) {
////                                    em.getTransaction().rollback();
////                                }
////                                OPDE.fatal(e);
////                            } finally {
////                                em.close();
////                            }
////
////
////                        }
////                    };
////
////                    popup = new JidePopup();
////                    JPanel pnl = NReportTAGSTools.createCheckBoxPanelForTags(il, report.getTags(), new GridLayout(8, 4));
////                    popup.setMovable(false);
////                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
////                    popup.setOwner(btnTAGs);
////                    popup.removeExcludedComponent(btnTAGs);
////                    popup.getContentPane().add(pnl);
////                    popup.setDefaultFocusComponent(pnl);
////
////                    GUITools.showPopup(popup, SwingConstants.WEST);
////
////                }
////            });
////            btnTAGs.setEnabled(!report.isObsolete());
////            titlePanelright.add(btnTAGs);
////
////            /***
////             *      _     _         __  __ _             _
////             *     | |__ | |_ _ __ |  \/  (_)_ __  _   _| |_ ___  ___
////             *     | '_ \| __| '_ \| |\/| | | '_ \| | | | __/ _ \/ __|
////             *     | |_) | |_| | | | |  | | | | | | |_| | ||  __/\__ \
////             *     |_.__/ \__|_| |_|_|  |_|_|_| |_|\__,_|\__\___||___/
////             *
////             */
////            final JButton btnMinutes = new JButton(SYSConst.icon22clock);
////            btnMinutes.setPressedIcon(SYSConst.icon22clockPressed);
////            btnMinutes.setAlignmentX(Component.RIGHT_ALIGNMENT);
////            btnMinutes.setContentAreaFilled(false);
////            btnMinutes.setBorder(null);
////            btnMinutes.setToolTipText(OPDE.lang.getString(internalClassID + ".btnminutes.tooltip"));
////            btnMinutes.addActionListener(new ActionListener() {
////                @Override
////                public void actionPerformed(ActionEvent actionEvent) {
////                    if (!NReportTools.isChangeable(report)) {
////                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
////                        return;
////                    }
////                    final JPopupMenu menu = SYSCalendar.getMinutesMenu(new int[]{1, 2, 3, 4, 5, 10, 15, 20, 30, 45, 60, 120, 240, 360}, new Closure() {
////                        @Override
////                        public void execute(Object o) {
////                            EntityManager em = OPDE.createEM();
////                            try {
////                                em.getTransaction().begin();
////
////                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
////                                NReport myReport = em.merge(report);
////                                em.lock(myReport, LockModeType.OPTIMISTIC);
////
////                                myReport.setMinutes((Integer) o);
////                                myReport.setEditpit(new Date());
////
////                                em.getTransaction().commit();
////
////                                DateMidnight dm = new DateMidnight(myReport.getPit());
////                                if (!dayMap.containsKey(dm)) {
////                                    dayMap.put(dm, new ArrayList<NReport>());
////                                }
////                                dayMap.get(dm).add(myReport);
////                                Collections.sort(dayMap.get(dm));
////                                reportMap.put(myReport, createCP4Month(myReport));
////                                buildPanel();
////
////                            } catch (OptimisticLockException ole) {
////                                if (em.getTransaction().isActive()) {
////                                    em.getTransaction().rollback();
////                                }
////                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
////                                    OPDE.getMainframe().emptyFrame();
////                                    OPDE.getMainframe().afterLogin();
////                                }
////                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
////                            } catch (Exception e) {
////                                if (em.getTransaction().isActive()) {
////                                    em.getTransaction().rollback();
////                                }
////                                OPDE.fatal(e);
////                            } finally {
////                                em.close();
////                            }
////                        }
////                    });
////
////                    menu.show(btnMinutes, 0, btnMinutes.getHeight());
////                }
////            });
////            btnMinutes.setEnabled(!report.isObsolete());
////            titlePanelright.add(btnMinutes);
////        }
////
////        /***
////         *      _     _         _____ _ _
////         *     | |__ | |_ _ __ |  ___(_) | ___  ___
////         *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
////         *     | |_) | |_| | | |  _| | | |  __/\__ \
////         *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
////         *
////         */
////        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.INSERT)) {
////            final JButton btnFiles = new JButton(SYSConst.icon22attach);
////            btnFiles.setPressedIcon(SYSConst.icon22attachPressed);
////            btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
////            btnFiles.setContentAreaFilled(false);
////            btnFiles.setBorder(null);
////            btnFiles.setToolTipText(OPDE.lang.getString("misc.btnfiles.tooltip"));
////            btnFiles.addActionListener(new ActionListener() {
////                @Override
////                public void actionPerformed(ActionEvent actionEvent) {
////                    Closure closure = null;
////                    if (!report.isObsolete()) {
////                        closure = new Closure() {
////                            @Override
////                            public void execute(Object o) {
////                                EntityManager em = OPDE.createEM();
////                                NReport myReport = em.merge(report);
////                                em.refresh(myReport);
////                                DateMidnight dm = new DateMidnight(myReport.getPit());
////                                if (!dayMap.containsKey(dm)) {
////                                    dayMap.put(dm, new ArrayList<NReport>());
////                                }
////                                dayMap.get(dm).remove(report);
////                                dayMap.get(dm).add(myReport);
////                                Collections.sort(dayMap.get(dm));
////                                reportMap.remove(report);
////                                reportMap.put(myReport, createCP4Month(myReport));
////                                buildPanel();
////                                em.close();
////                            }
////                        };
////                    }
////                    new DlgFiles(report, closure);
////                }
////            });
////
////            btnFiles.setEnabled(!report.isObsolete());
////            if (report.getAttachedFilesConnections().size() > 0) {
////                JLabel lblNum = new JLabel(Integer.toString(report.getAttachedFilesConnections().size()), SYSConst.icon16redStar, SwingConstants.CENTER);
////                lblNum.setFont(SYSConst.ARIAL10BOLD);
////                lblNum.setForeground(Color.YELLOW);
////                lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
////                DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnFiles, lblNum, DefaultOverlayable.SOUTH_EAST);
////                overlayableBtn.setOpaque(false);
////                titlePanelright.add(overlayableBtn);
////            } else {
////                titlePanelright.add(btnFiles);
////            }
////
////        }
////
////
////        /***
////         *      _     _         ____
////         *     | |__ | |_ _ __ |  _ \ _ __ ___   ___ ___  ___ ___
////         *     | '_ \| __| '_ \| |_) | '__/ _ \ / __/ _ \/ __/ __|
////         *     | |_) | |_| | | |  __/| | | (_) | (_|  __/\__ \__ \
////         *     |_.__/ \__|_| |_|_|   |_|  \___/ \___\___||___/___/
////         *
////         */
////        final JButton btnProcess = new JButton(SYSConst.icon22link);
////        btnProcess.setPressedIcon(SYSConst.icon22linkPressed);
////        btnProcess.setAlignmentX(Component.RIGHT_ALIGNMENT);
////        btnProcess.setContentAreaFilled(false);
////        btnProcess.setBorder(null);
////        btnProcess.setToolTipText(OPDE.lang.getString("misc.btnprocess.tooltip"));
////        btnProcess.addActionListener(new ActionListener() {
////            @Override
////            public void actionPerformed(ActionEvent actionEvent) {
////                Closure closure = null;
////                if (!report.isObsolete()) {
////                    closure = new Closure() {
////                        @Override
////                        public void execute(Object o) {
////                            if (o == null) {
////                                return;
////                            }
////                            Pair<ArrayList<QProcess>, ArrayList<QProcess>> result = (Pair<ArrayList<QProcess>, ArrayList<QProcess>>) o;
////
////                            ArrayList<QProcess> assigned = result.getFirst();
////                            ArrayList<QProcess> unassigned = result.getSecond();
////
////                            EntityManager em = OPDE.createEM();
////
////                            try {
////                                em.getTransaction().begin();
////
////                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
////                                NReport myReport = em.merge(report);
////                                em.lock(myReport, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
////
////
////                                for (SYSNR2PROCESS linkObject : myReport.getAttachedProcessConnections()) {
////                                    if (unassigned.contains(linkObject.getQProcess())) {
////                                        em.remove(em.merge(linkObject));
////                                    }
////                                }
////
////                                for (QProcess qProcess : assigned) {
////                                    List<QProcessElement> listElements = qProcess.getElements();
////                                    if (!listElements.contains(myReport)) {
////                                        QProcess myQProcess = em.merge(qProcess);
////                                        SYSNR2PROCESS myLinkObject = em.merge(new SYSNR2PROCESS(myQProcess, myReport));
////                                        qProcess.getAttachedNReportConnections().add(myLinkObject);
////                                        myReport.getAttachedProcessConnections().add(myLinkObject);
////                                    }
////                                }
////
////                                em.getTransaction().commit();
////
////                                DateMidnight dm = new DateMidnight(myReport.getPit());
////                                if (!dayMap.containsKey(dm)) {
////                                    dayMap.put(dm, new ArrayList<NReport>());
////                                }
////                                dayMap.get(dm).remove(report);
////                                dayMap.get(dm).add(myReport);
////                                Collections.sort(dayMap.get(dm));
////                                reportMap.remove(report);
////                                reportMap.put(myReport, createCP4Month(myReport));
////                                buildPanel();
////                            } catch (OptimisticLockException ole) {
////                                if (em.getTransaction().isActive()) {
////                                    em.getTransaction().rollback();
////                                }
////                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
////                                    OPDE.getMainframe().emptyFrame();
////                                    OPDE.getMainframe().afterLogin();
////                                }
////                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
////                            } catch (Exception e) {
////                                if (em.getTransaction().isActive()) {
////                                    em.getTransaction().rollback();
////                                }
////                                OPDE.fatal(e);
////                            } finally {
////                                em.close();
////                            }
////
////                        }
////                    };
////                }
////                new DlgProcessAssign(report, closure);
////            }
////        });
////        btnProcess.setEnabled(!report.isObsolete());
////
////        if (!report.getAttachedProcessConnections().isEmpty()) {
////            JLabel lblNum = new JLabel(Integer.toString(report.getAttachedProcessConnections().size()), SYSConst.icon16redStar, SwingConstants.CENTER);
////            lblNum.setFont(SYSConst.ARIAL10BOLD);
////            lblNum.setForeground(Color.YELLOW);
////            lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
////            DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnProcess, lblNum, DefaultOverlayable.SOUTH_EAST);
////            overlayableBtn.setOpaque(false);
////            titlePanelright.add(overlayableBtn);
////        } else {
////            titlePanelright.add(btnProcess);
////        }
//
//
//        titlePanelleft.setOpaque(false);
//        titlePanelright.setOpaque(false);
//        JPanel titlePanel = new JPanel();
//        titlePanel.setOpaque(false);
//
//        titlePanel.setLayout(new GridBagLayout());
//        ((GridBagLayout) titlePanel.getLayout()).columnWidths = new int[]{0, 80};
//        ((GridBagLayout) titlePanel.getLayout()).columnWeights = new double[]{1.0, 1.0};
//
//        titlePanel.add(titlePanelleft, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
//                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
//                new Insets(0, 0, 0, 5), 0, 0));
//
//        titlePanel.add(titlePanelright, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
//                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
//                new Insets(0, 0, 0, 0), 0, 0));
//
//        cp.setTitleLabelComponent(titlePanel);
//        cp.setSlidingDirection(SwingConstants.SOUTH);
//
//        try {
//            cp.setCollapsed(true);
//        } catch (PropertyVetoException e) {
//            OPDE.error(e);
//        }
//
//
//        /***
//         *       ___ ___  _  _ _____ ___ _  _ _____
//         *      / __/ _ \| \| |_   _| __| \| |_   _|
//         *     | (_| (_) | .` | | | | _|| .` | | |
//         *      \___\___/|_|\_| |_| |___|_|\_| |_|
//         *
//         */
//
//        cp.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
//            @Override
//            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
//                JTextPane contentPane = new JTextPane();
//                contentPane.setContentType("text/html");
//                contentPane.setEditable(false);
//                contentPane.setText(SYSTools.toHTMLForScreen(NReportTools.getAsHTML(nreport)));
//                cp.setContentPane(contentPane);
//            }
//        });
//        cp.setBackground(SYSCalendar.getBG(SYSCalendar.whatShiftIs(nreport.getPit())));
//        cp.setHorizontalAlignment(SwingConstants.LEADING);
//        cp.setOpaque(false);
//
//        return cp;
//    }


    private void buildPanel() {
        cpsReports.removeAll();
        cpsReports.setLayout(new JideBoxLayout(cpsReports, JideBoxLayout.Y_AXIS));

        Pair<DateTime, DateTime> minmax = NReportTools.getMinMax(resident);
        if (minmax != null) {
            DateMidnight start = minmax.getFirst().toDateMidnight().dayOfMonth().withMinimumValue();
            DateMidnight end = resident.isActive() ? new DateMidnight() : minmax.getSecond().toDateMidnight().dayOfMonth().withMinimumValue();
            for (int year = end.getYear(); year >= start.getYear(); year--) {
                final String keyYear = Integer.toString(year) + ".year";
                cpsReports.add(cpMap.get(keyYear));
            }
        }

        cpsReports.addExpansion();
    }

//    private void buildPanel() {
//        OPDE.debug(cpsReports.getComponentCount());
//        cpsReports.removeAll();
//
//        JButton older = new JButton(OPDE.lang.getString("misc.msg.olderEntries"), SYSConst.icon22down);
//        older.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                if (reportMap.containsKey(firstReport)) {
//                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.noOlderEntries")));
//                    return;
//                }
//                DateMidnight dm = new DateMidnight(jdcVon.getDate());
//                jdcVon.setDate(dm.minusWeeks(WEEKS_BACK).minusDays(1).toDate());
//                javax.swing.SwingUtilities.invokeLater(new Runnable() {
//                    public void run() {
//                        jspReports.getVerticalScrollBar().setValue(0);
//                    }
//                });
//            }
//        });
//        JButton newer = new JButton(OPDE.lang.getString("misc.msg.newerEntries"), SYSConst.icon22up);
//        newer.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                if (new DateMidnight(jdcVon.getDate()).plusWeeks(WEEKS_BACK).isAfterNow()) {
//                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.noNewerEntries")));
//                    return;
//                }
//                DateMidnight dm = new DateMidnight(jdcVon.getDate());
//                jdcVon.setDate(dm.plusWeeks(WEEKS_BACK).plusDays(1).toDate());
//                javax.swing.SwingUtilities.invokeLater(new Runnable() {
//                    public void run() {
//                        jspReports.getVerticalScrollBar().setValue(jspReports.getVerticalScrollBar().getMaximum());
//                    }
//                });
//            }
//        });
//
//        cpsReports.setLayout(new JideBoxLayout(cpsReports, JideBoxLayout.Y_AXIS));
//
//        cpsReports.add(newer);
//        NReportTAGS tag = cmbAuswahl.getSelectedIndex() == 0 ? null : (NReportTAGS) cmbAuswahl.getSelectedItem();
//
//        boolean empty = true;
//
//        if (!dayMap.isEmpty()) {
//
//            ArrayList<DateMidnight> dateList = new ArrayList(dayMap.keySet());
//            Collections.sort(dateList, new Comparator<DateMidnight>() {
//                @Override
//                public int compare(DateMidnight o1, DateMidnight o2) {
//                    return o1.compareTo(o2) * -1;
//                }
//            });
//
//            int year = dateList.get(0).getYear();
//            int currentYear = year;
//            HashMap hollidays = SYSCalendar.getFeiertage(year);
//
//            for (final DateMidnight date : dateList) {
//
//                if (date.getYear() != currentYear) {
//                    currentYear = date.getYear();
//                    hollidays = SYSCalendar.getFeiertage(currentYear);
//                }
//
//
//                JPanel dayPanel = new JPanel();
//                dayPanel.setLayout(new VerticalLayout());
//
//                for (NReport report : dayMap.get(date)) {
//
//                    NReport report2add = report;
//
//                    if (tag != null && !report.getTags().contains(tag)) {
//                        report2add = null;
//                    }
//
//                    if (report.isObsolete() && !tbShowReplaced.isSelected()) {
//                        report2add = null;
//                    }
//
//                    if (report2add != null) {
//                        dayPanel.add(reportMap.get(report2add));
//                    }
//
//                }
//
//                if (dayPanel.getComponentCount() > 0) {
//                    // create header panel for that day
//                    SimpleDateFormat df = new SimpleDateFormat("EEEE, dd.MM.yyyy");
//
//                    String holliday = SYSTools.catchNull(hollidays.get(DateTimeFormat.forPattern("yyyy-MM-dd").print(date)));
//                    String title = df.format(date.toDate()) + (holliday.isEmpty() ? "" : " " + holliday);
//
//                    final CollapsiblePane dayPane = new CollapsiblePane(title);
//                    dayPane.setSlidingDirection(SwingConstants.SOUTH);
//                    dayPane.setFont(SYSConst.ARIAL20);
//
//
//                    final JButton btnPrint = new JButton(SYSConst.icon22print);
//                    btnPrint.setPressedIcon(SYSConst.icon22printPressed);
//
//                    btnPrint.setContentAreaFilled(false);
//                    btnPrint.setBorder(null);
//                    btnPrint.setToolTipText(OPDE.lang.getString(internalClassID + ".btnprint.tooltip"));
//                    btnPrint.addActionListener(new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//                            SYSFilesTools.print(SYSTools.toHTML(NReportTools.getBerichteAsHTML(dayMap.get(date), false, true)), true);
//                        }
//                    });
//
//                    dayPane.setTitleComponent(btnPrint);
//
//                    if (!holliday.isEmpty()) {
//                        dayPane.setBackground(SYSConst.colorHolliday);
//                    } else if (date.getDayOfWeek() == DateTimeConstants.SATURDAY || date.getDayOfWeek() == DateTimeConstants.SUNDAY) {
//                        dayPane.setBackground(SYSConst.colorWeekend);
//                    } else {
//                        dayPane.setBackground(SYSConst.colorWeekday);
//                    }
//                    dayPane.setContentPane(dayPanel);
//                    dayPane.setCollapsible(false);
//                    dayPane.setOpaque(false);
//
//                    cpsReports.add(dayPane);
//                    empty = false;
//                }
//            }
//        }
//
//        if (empty) {
//            CollapsiblePane emptyCP = new CollapsiblePane(OPDE.lang.getString(internalClassID + ".noreports"));
//            emptyCP.setCollapsible(false);
//            try {
//                emptyCP.setCollapsed(false);
//            } catch (PropertyVetoException e) {
//                OPDE.error(e);
//            }
//            cpsReports.add(emptyCP);
//        }
//
//        cpsReports.add(older);
//        cpsReports.addExpansion();
//
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspReports;
    private CollapsiblePanes cpsReports;
    // End of variables declaration//GEN-END:variables
}
