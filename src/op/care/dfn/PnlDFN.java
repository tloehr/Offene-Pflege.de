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
package op.care.dfn;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import entity.Bewohner;
import entity.BewohnerTools;
import entity.info.BWInfoTools;
import entity.planung.DFN;
import entity.planung.DFNTools;
import entity.planung.NursingProcess;
import entity.planung.NursingProcessTools;
import entity.verordnungen.BHPTools;
import op.OPDE;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateTime;
import org.joda.time.Period;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author root
 */
public class PnlDFN extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.dfn";

    String bwkennung;
    Bewohner bewohner;
    JPopupMenu menu;

    private boolean initPhase;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JDateChooser jdcDatum;
    private JComboBox cmbSchicht;
    private int DFN_MAX_MINUTES_TO_WITHDRAW;
    private JideButton addButton;
    private ArrayList<NursingProcess> involvedNPs;
    private HashMap<DFN, CollapsiblePane> dfnCollapsiblePaneHashMap;
    private HashMap<NursingProcess, ArrayList<DFN>> nursingProcessArrayListHashMap;
    private ArrayList<DFN> unassignedDFNArrayList;
    private CollapsiblePane unassignedPane;
    private boolean residentAbsent;

    public PnlDFN(Bewohner bewohner, JScrollPane jspSearch) {
        initComponents();
        this.jspSearch = jspSearch;
        initPanel();
        change2Bewohner(bewohner);
    }


    private void initPanel() {
        DFN_MAX_MINUTES_TO_WITHDRAW = Integer.parseInt(OPDE.getProps().getProperty("dfn_max_minutes_to_withdraw"));
//        planungCollapsiblePaneMap = new HashMap<NursingProcess, CollapsiblePane>();
//        categoryCPMap = new HashMap<BWInfoKat, CollapsiblePane>();
//        planungen = new HashMap<BWInfoKat, java.util.List<NursingProcess>>();
        nursingProcessArrayListHashMap = new HashMap<NursingProcess, ArrayList<DFN>>();
        dfnCollapsiblePaneHashMap = new HashMap<DFN, CollapsiblePane>();
        prepareSearchArea();
    }

    @Override
    public void cleanup() {
        jdcDatum.cleanup();
        involvedNPs.clear();
        cpDFN.removeAll();
        dfnCollapsiblePaneHashMap.clear();
        nursingProcessArrayListHashMap.clear();
        SYSTools.unregisterListeners(this);
    }

    @Override
    public void reload() {
        reloadDisplay();
    }


    @Override
    public void change2Bewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
        OPDE.getDisplayManager().setMainMessage(BewohnerTools.getBWLabelText(bewohner));
        reloadDisplay();


        residentAbsent = bewohner.isActive() && BWInfoTools.absentSince(bewohner) != null;


//        btnLock.setEnabled(readOnly);
//
//        ignoreJDCEvent = true;
//        jdcDatum.setDate(SYSCalendar.nowDBDate());
//        btnForward.setEnabled(false); // In die Zukunft kann man nicht gucken.
//
//        ArrayList hauf = DBRetrieve.getHauf(bwkennung);
//        Date[] d = (Date[]) hauf.get(0);
//        jdcDatum.setMinSelectableDate(d[0]);
//
//
//        ignoreJDCEvent = false;
//        cmbSchicht.setSelectedIndex(SYSCalendar.ermittleSchicht() + 1);

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
//                    try {
//                        int progress = 0;
//
//                        if (kategorien.isEmpty()) {
//                            // Elmininate empty categories
//                            for (final BWInfoKat kat : BWInfoKatTools.getKategorien()) {
//                                if (!NursingProcessTools.findByKategorieAndBewohner(bewohner, kat).isEmpty()) {
//                                    kategorien.add(kat);
//                                }
//                            }
//                        }
//
//                        cpPlan.setLayout(new JideBoxLayout(cpPlan, JideBoxLayout.Y_AXIS));
//                        for (BWInfoKat kat : kategorien) {
//                            progress++;
//                            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, kategorien.size()));
//                            cpPlan.add(createCollapsiblePanesFor(kat));
//                        }
//
//
//                    } catch (Exception e) {
//                        OPDE.fatal(e);
//                    }
//                    return null;
//                }
//
//                @Override
//                protected void done() {
//                    cpPlan.addExpansion();
//                    initPhase = false;
//                    OPDE.getDisplayManager().setProgressBarMessage(null);
//                    OPDE.getMainframe().setBlocked(false);
//                }
//            };
//            worker.execute();

        } else {

            cpDFN.removeAll();
            involvedNPs.clear();
            dfnCollapsiblePaneHashMap.clear();
            nursingProcessArrayListHashMap.clear();
            involvedNPs = DFNTools.getInvolvedNPs((byte) (cmbSchicht.getSelectedIndex() - 1), bewohner, jdcDatum.getDate());

            cpDFN.setLayout(new JideBoxLayout(cpDFN, JideBoxLayout.Y_AXIS));
            for (NursingProcess np : involvedNPs) {
                cpDFN.add(createCollapsiblePanesFor(np));
            }

            // this adds unassigned DFNs to the panel
            CollapsiblePane unassignedPane = createCollapsiblePanesFor((NursingProcess) null);
            if (unassignedPane != null) {
                cpDFN.add(unassignedPane);
            }

            cpDFN.addExpansion();
        }
        initPhase = false;
    }


    private void refreshDisplay() {
        cpDFN.removeAll();

        for (NursingProcess np : involvedNPs) {
            cpDFN.add(refreshCollapsiblePanesFor(np));
        }

        // this adds unassigned DFNs to the panel
        unassignedPane = createCollapsiblePanesFor((NursingProcess) null);
        if (unassignedPane != null) {
            cpDFN.add(unassignedPane);
        }

        cpDFN.addExpansion();
    }

    private CollapsiblePane refreshCollapsiblePanesFor(final NursingProcess np) {
        String title = "";
        Color fore, back, backcontent;

        if (np == null) {
            title = OPDE.lang.getString(internalClassID + ".ondemand");
            back = Color.LIGHT_GRAY;
            fore = Color.BLACK;
            backcontent = Color.LIGHT_GRAY;
        } else {
            title = np.getStichwort() + " (" + np.getKategorie().getBezeichnung() + ")";
            back = np.getKategorie().getBackgroundHeader();
            fore = np.getKategorie().getForegroundHeader();
            backcontent = np.getKategorie().getBackgroundContent();
        }

        final CollapsiblePane npPane = new CollapsiblePane(title);

        npPane.setSlidingDirection(SwingConstants.SOUTH);
        npPane.setBackground(back);
        npPane.setForeground(fore);
        npPane.setOpaque(false);

        JPanel npPanel = new JPanel();
        npPanel.setLayout(new VerticalLayout());
        npPanel.setBackground(backcontent);

        if (np == null) {
            if (unassignedDFNArrayList.isEmpty()) {
                return null;
            }
        }

        for (DFN dfn : (np == null ? unassignedDFNArrayList : nursingProcessArrayListHashMap.get(np))) {

            npPanel.add(dfnCollapsiblePaneHashMap.get(dfn));
        }

        npPane.setContentPane(npPanel);
        npPane.setCollapsible(false);
        try {
            npPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }
        return npPane;
    }

    private CollapsiblePane createCollapsiblePanesFor(final NursingProcess np) {
        String title = "";
        Color fore, back, backcontent;

        if (np == null) {
            title = OPDE.lang.getString(internalClassID + ".ondemand");
            back = Color.LIGHT_GRAY;
            fore = Color.BLACK;
            backcontent = Color.LIGHT_GRAY;
        } else {
            title = np.getStichwort() + " (" + np.getKategorie().getBezeichnung() + ")";
            back = np.getKategorie().getBackgroundHeader();
            fore = np.getKategorie().getForegroundHeader();
            backcontent = np.getKategorie().getBackgroundContent();
        }

        final CollapsiblePane npPane = new CollapsiblePane(title);

//        katpane.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent mouseEvent) {
//                try {
//                    if (katpane.isCollapsed()) {
//                        katpane.setCollapsed(false);
//                    } else {
//                        // collapse all children
//                        for (NursingProcess planung : planungen.get(kat)) {
//                            planungCollapsiblePaneMap.get(planung).setCollapsed(true);
//                        }
//                        katpane.setCollapsed(true);
//                    }
//                } catch (PropertyVetoException e) {
//                    OPDE.error(e);
//                }
//            }
//        });
        npPane.setSlidingDirection(SwingConstants.SOUTH);
        npPane.setBackground(back);
        npPane.setForeground(fore);
        npPane.setOpaque(false);

        JPanel npPanel = new JPanel();
        npPanel.setLayout(new VerticalLayout());
        npPanel.setBackground(backcontent);

        if (np == null) {
            unassignedDFNArrayList = DFNTools.getDFNs((byte) (cmbSchicht.getSelectedIndex() - 1), bewohner, jdcDatum.getDate());
            if (unassignedDFNArrayList.isEmpty()) {
                return null;
            }
        } else {
            nursingProcessArrayListHashMap.put(np, DFNTools.getDFNs((byte) (cmbSchicht.getSelectedIndex() - 1), np, jdcDatum.getDate()));
        }

        for (DFN dfn : (np == null ? unassignedDFNArrayList : nursingProcessArrayListHashMap.get(np))) {

//            final JideButton btnDFN = GUITools.createHyperlinkButton(dfn.getIntervention().getBezeichnung(), SYSConst.icon22redo, null);
//            btnDFN.setForeground(np.getKategorie().getForegroundContent());
//            btnDFN.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//
//                }
//            });
//            npPanel.add(btnDFN);

            dfnCollapsiblePaneHashMap.put(dfn, createCollapsiblePanesFor(dfn));
            npPanel.add(dfnCollapsiblePaneHashMap.get(dfn));
        }


        npPane.setContentPane(npPanel);
        npPane.setCollapsible(false);
        try {
            npPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }
        return npPane;
    }

    private CollapsiblePane createCollapsiblePanesFor(final DFN dfn) {
        final CollapsiblePane dfnPane = new CollapsiblePane();
        String fg = SYSConst.html_grey50;
        if (dfn.getNursingProcess() != null) {
            fg = "#" + dfn.getNursingProcess().getKategorie().getFgcontent();
        }

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
        JideButton btnDFN = GUITools.createHyperlinkButton("<html><font color=\"" + fg + "\">" + dfn.getIntervention().getBezeichnung() + DFNTools.getScheduleText(dfn, " <b>[", "]</b>") + "</html>", DFNTools.getIcon(dfn), null);
//        title.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
        btnDFN.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (!dfn.isOnDemand()) {
            btnDFN.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    try {
                        dfnPane.setCollapsed(!dfnPane.isCollapsed());
                    } catch (PropertyVetoException e) {
                        OPDE.error(e);
                    }
                }
            });
        }
        titlePanelleft.add(btnDFN);


        JPanel titlePanelright = new JPanel();
        titlePanelright.setLayout(new BoxLayout(titlePanelright, BoxLayout.LINE_AXIS));


        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {

            /***
             *      _     _            _                _
             *     | |__ | |_ _ __    / \   _ __  _ __ | |_   _
             *     | '_ \| __| '_ \  / _ \ | '_ \| '_ \| | | | |
             *     | |_) | |_| | | |/ ___ \| |_) | |_) | | |_| |
             *     |_.__/ \__|_| |_/_/   \_\ .__/| .__/|_|\__, |
             *                             |_|   |_|      |___/
             */
            JButton btnApply = new JButton(SYSConst.icon22apply);
            btnApply.setPressedIcon(SYSConst.icon22applyPressed);
            btnApply.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnApply.setContentAreaFilled(false);
            btnApply.setBorder(null);
//            btnApply.setToolTipText(OPDE.lang.getString(internalClassID + ".btnadd.tooltip"));
            btnApply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (dfn.getStatus() == DFNTools.STATUS_ERLEDIGT) {
                        return;
                    }
                    boolean changeable =
                            !residentAbsent && bewohner.isActive() &&
                                    dfn.getNursingProcess().getBis().after(new Date()) && // prescription is active
                                    (dfn.getUser() == null ||
                                            (dfn.getUser().equals(OPDE.getLogin().getUser()) &&
                                                    new Period(new DateTime(dfn.getMdate()), new DateTime()).getMinutes() < DFN_MAX_MINUTES_TO_WITHDRAW));
                    if (changeable) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();


                            em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC);
                            DFN myDFN = em.merge(dfn);
                            em.lock(myDFN, LockModeType.OPTIMISTIC);

//                            if (myDFN.isOnDemand()) {
//                                em.remove(myDFN);
//                                dfnCollapsiblePaneHashMap.remove(myDFN);
//                                unassignedDFNArrayList.remove(myDFN);
//                            } else {
                            myDFN.setStatus(DFNTools.STATUS_ERLEDIGT);
                            myDFN.setUser(em.merge(OPDE.getLogin().getUser()));
                            myDFN.setIst(new Date());
                            myDFN.setiZeit(SYSCalendar.whatTimeIDis(new Date()));
                            dfnCollapsiblePaneHashMap.put(myDFN, createCollapsiblePanesFor(myDFN));
//                                dfnCollapsiblePaneHashMap.remove(dfn);
                            int position = nursingProcessArrayListHashMap.get(myDFN.getNursingProcess()).indexOf(dfn);
                            nursingProcessArrayListHashMap.get(myDFN.getNursingProcess()).remove(dfn);
                            nursingProcessArrayListHashMap.get(myDFN.getNursingProcess()).add(position, myDFN);
//                            }
                            em.getTransaction().commit();
                            refreshDisplay();
                        } catch (OptimisticLockException ole) {
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            if (ole.getMessage().indexOf("Class> entity.Bewohner") > -1) {
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

                    } else {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
                    }
                }
            });
            btnApply.setEnabled(!dfn.isOnDemand());
            titlePanelright.add(btnApply);


            /***
             *      _     _          ____                     _
             *     | |__ | |_ _ __  / ___|__ _ _ __   ___ ___| |
             *     | '_ \| __| '_ \| |   / _` | '_ \ / __/ _ \ |
             *     | |_) | |_| | | | |__| (_| | | | | (_|  __/ |
             *     |_.__/ \__|_| |_|\____\__,_|_| |_|\___\___|_|
             *
             */
            final JButton btnCancel = new JButton(SYSConst.icon22cancel);
            btnCancel.setPressedIcon(SYSConst.icon22cancelPressed);
            btnCancel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnCancel.setContentAreaFilled(false);
            btnCancel.setBorder(null);
//            btnCancel.setToolTipText(OPDE.lang.getString(internalClassID + ".btneval.tooltip"));
            btnCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                }
            });
            btnCancel.setEnabled(!dfn.isOnDemand());
            titlePanelright.add(btnCancel);

            /***
             *      _     _         _____                 _
             *     | |__ | |_ _ __ | ____|_ __ ___  _ __ | |_ _   _
             *     | '_ \| __| '_ \|  _| | '_ ` _ \| '_ \| __| | | |
             *     | |_) | |_| | | | |___| | | | | | |_) | |_| |_| |
             *     |_.__/ \__|_| |_|_____|_| |_| |_| .__/ \__|\__, |
             *                                     |_|        |___/
             */
            final JButton btnEmpty = new JButton(SYSConst.icon22empty);
            btnEmpty.setPressedIcon(SYSConst.icon22emptyPressed);
            btnEmpty.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnEmpty.setContentAreaFilled(false);
            btnEmpty.setBorder(null);
//            btnCancel.setToolTipText(OPDE.lang.getString(internalClassID + ".btneval.tooltip"));
            btnEmpty.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                }
            });
            titlePanelright.add(btnEmpty);
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

        dfnPane.setTitleLabelComponent(titlePanel);
        dfnPane.setSlidingDirection(SwingConstants.SOUTH);

        /***
         *       ___ ___  _  _ _____ ___ _  _ _____
         *      / __/ _ \| \| |_   _| __| \| |_   _|
         *     | (_| (_) | .` | | | | _|| .` | | |
         *      \___\___/|_|\_| |_| |___|_|\_| |_|
         *
         */
        if (dfn.getNursingProcess() != null) {
            JTextPane contentPane = new JTextPane();
            contentPane.setContentType("text/html");
            contentPane.setText(SYSTools.toHTML(NursingProcessTools.getAsHTML(dfn.getNursingProcess(), false)));
            dfnPane.setContentPane(contentPane);
            dfnPane.setBackground(dfn.getNursingProcess().getKategorie().getBackgroundContent());
            dfnPane.setForeground(dfn.getNursingProcess().getKategorie().getForegroundContent());
        }
        try {
            dfnPane.setCollapsed(true);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }
        dfnPane.setCollapsible(dfn.getNursingProcess() != null);
        dfnPane.setHorizontalAlignment(SwingConstants.LEADING);
        dfnPane.setOpaque(false);
        return dfnPane;
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

        String[] strs = GUITools.getLocalizedMessages(new String[]{"misc.msg.everything", internalClassID + ".shift.veryearly", internalClassID + ".shift.early", internalClassID + ".shift.late", internalClassID + ".shift.verylate"});

        cmbSchicht = new JComboBox(new DefaultComboBoxModel(strs));
        cmbSchicht.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbSchicht.setSelectedIndex((int) SYSCalendar.whatShiftis(new Date()) + 1);
        cmbSchicht.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (!initPhase) {
                    reloadDisplay();
                }
            }
        });
        list.add(cmbSchicht);

        jdcDatum = new JDateChooser(new Date());
        jdcDatum.setFont(new Font("Arial", Font.PLAIN, 14));
        jdcDatum.setMinSelectableDate(BHPTools.getMinDatum(bewohner));

        jdcDatum.setBackground(Color.WHITE);
        jdcDatum.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (initPhase) {
                    return;
                }
                if (evt.getPropertyName().equals("date")) {
                    reloadDisplay();
                }
            }
        });
        list.add(jdcDatum);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new HorizontalLayout(5));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JButton homeButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_start.png")));
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcDatum.setDate(jdcDatum.getMinSelectableDate());
            }
        });
        homeButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_start_pressed.png")));
        homeButton.setBorder(null);
        homeButton.setBorderPainted(false);
        homeButton.setOpaque(false);
        homeButton.setContentAreaFilled(false);
        homeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton backButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_back.png")));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcDatum.setDate(SYSCalendar.addDate(jdcDatum.getDate(), -1));
            }
        });
        backButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_back_pressed.png")));
        backButton.setBorder(null);
        backButton.setBorderPainted(false);
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        JButton fwdButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_play.png")));
        fwdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcDatum.setDate(SYSCalendar.addDate(jdcDatum.getDate(), 1));
            }
        });
        fwdButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_play_pressed.png")));
        fwdButton.setBorder(null);
        fwdButton.setBorderPainted(false);
        fwdButton.setOpaque(false);
        fwdButton.setContentAreaFilled(false);
        fwdButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton endButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_end.png")));
        endButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcDatum.setDate(new Date());
            }
        });
        endButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_end_pressed.png")));
        endButton.setBorder(null);
        endButton.setBorderPainted(false);
        endButton.setOpaque(false);
        endButton.setContentAreaFilled(false);
        endButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        buttonPanel.add(homeButton);
        buttonPanel.add(backButton);
        buttonPanel.add(fwdButton);
        buttonPanel.add(endButton);

        list.add(buttonPanel);

        return list;
    }


    private java.util.List<Component> addCommands() {

        java.util.List<Component> list = new ArrayList<Component>();

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            addButton = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnadd"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), null);
//            addButton.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//
//                    if (VerordnungTools.hasBedarf(bewohner)) {
//
//                        final JidePopup popup = new JidePopup();
//
//                        DlgBedarf dlg = new DlgBedarf(bewohner, new Closure() {
//                            @Override
//                            public void execute(Object o) {
//                                popup.hidePopup();
//                                if (o != null) {
//                                    reloadTable();
//                                }
//                            }
//                        });
//
//                        popup.setMovable(false);
//                        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//                        popup.getContentPane().add(dlg);
//                        popup.setOwner(addButton);
//                        popup.removeExcludedComponent(addButton);
//                        popup.setDefaultFocusComponent(dlg);
//                        Point p = new Point(addButton.getX(), addButton.getY());
//                        SwingUtilities.convertPointToScreen(p, addButton);
//                        popup.showPopup(p.x, p.y - (int) dlg.getPreferredSize().getHeight()); // - (int) addButton.getPreferredSize().getHeight()
//                    } else {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Keine Bedarfsverordnungen vorhanden"));
//                    }
//                }
//            });
            list.add(addButton);
        }

        return list;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspDFN = new JScrollPane();
        cpDFN = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspDFN ========
        {
            jspDFN.setBorder(new BevelBorder(BevelBorder.RAISED));

            //======== cpDFN ========
            {
                cpDFN.setLayout(new BoxLayout(cpDFN, BoxLayout.X_AXIS));
            }
            jspDFN.setViewportView(cpDFN);
        }
        add(jspDFN);
    }// </editor-fold>//GEN-END:initComponents

//    private void btnNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNowActionPerformed
//        jdcDatum.setDate(SYSCalendar.today_date());
//        btnForward.setEnabled(SYSCalendar.sameDay(jdcDatum.getDate(), SYSCalendar.nowDBDate()) < 0);
//    }//GEN-LAST:event_btnNowActionPerformed
//
//    private void btnForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardActionPerformed
//        jdcDatum.setDate(SYSCalendar.addDate(jdcDatum.getDate(), 1));
//        btnForward.setEnabled(SYSCalendar.sameDay(jdcDatum.getDate(), SYSCalendar.nowDBDate()) < 0);
//    }//GEN-LAST:event_btnForwardActionPerformed
//
//    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
//        jdcDatum.setDate(SYSCalendar.addDate(jdcDatum.getDate(), -1));
//        btnForward.setEnabled(SYSCalendar.sameDay(jdcDatum.getDate(), SYSCalendar.nowDBDate()) < 0);
//    }//GEN-LAST:event_btnBackActionPerformed
//
//    private void btnTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopActionPerformed
//        jdcDatum.setDate(jdcDatum.getMinSelectableDate());
//        btnForward.setEnabled(SYSCalendar.sameDay(jdcDatum.getDate(), SYSCalendar.nowDBDate()) < 0);
//    }//GEN-LAST:event_btnTopActionPerformed
//
//    private void jdcDatumPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcDatumPropertyChange
//        if (!evt.getPropertyName().equals("date") || ignoreJDCEvent) {
//            return;
//        }
//        ignoreJDCEvent = true;
//        SYSCalendar.checkJDC((JDateChooser) evt.getSource());
//        ignoreJDCEvent = false;
//        reloadTable();
//    }//GEN-LAST:event_jdcDatumPropertyChange

    private void tblDFNMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDFNMousePressed
//        if (readOnly) {
//            return;
//        }
//        final TMDFN tm = (TMDFN) tblDFN.getModel();
//        if (tm.getRowCount() == 0) {
//            return;
//        }
//        Point p = evt.getPoint();
//        final int col = tblDFN.columnAtPoint(p);
//        final int row = tblDFN.rowAtPoint(p);
//        ListSelectionModel lsm = tblDFN.getSelectionModel();
//        lsm.setSelectionInterval(row, row);
//        final long dfnid = ((Long) tm.getValueAt(row, TMDFN.COL_DFNID)).longValue();
//        //final long termid = ((Long) tm.getValueAt(row, TMDFN.COL_TERMID)).longValue();
//        int status = ((Integer) tm.getValueAt(row, TMDFN.COL_STATUS)).intValue();
//        String ukennung = ((String) tm.getValueAt(row, TMDFN.COL_UKENNUNG)).toString();
//        long abdatum = ((Long) tm.getValueAt(row, TMDFN.COL_BIS)).longValue();
//        //final long soll = ((Long) tm.getValueAt(row, TMDFN.COL_SOLL)).longValue();
//        //final int szeit = ((Integer) tm.getValueAt(row, TMDFN.COL_SZEIT)).intValue();
//        //final long relid = ((Long) tm.getValueAt(row, TMDFN.COL_RELID)).longValue();
//        long mdate = ((Long) tm.getValueAt(row, TMDFN.COL_MDATE)).longValue();
//
//        boolean changeable =
//                // Diese Kontrolle stellt sicher, dass ein User nur seine eigenen Einträge und das auch nur
//                // eine halbe Stunde lang bearbeiten kann.
//                !abwesend &&
//                        SYSCalendar.isInFuture(abdatum) &&
//                        (ukennung.equals("") ||
//                                (ukennung.equalsIgnoreCase(OPDE.getLogin().getUser().getUKennung()) &&
//                                        SYSCalendar.earlyEnough(mdate, 30)));
//        OPDE.debug(changeable ? "changeable" : "NOT changeable");
//        if (changeable) {
//            // Drückt der Anwender auch wirklich mit der LINKEN Maustaste auf die mittlere Spalte.
//            if (!evt.isPopupTrigger() && col == TMDFN.COL_STATUS) {
//                boolean fullReloadNecessary = false;
//                status++;
//                if (status > 1) {
//                    status = 0;
//                }
//                HashMap hm = new HashMap();
//                hm.put("Status", status);
//                if (status == 0) {
//                    hm.put("UKennung", null);
//                    hm.put("Ist", null);
//                    hm.put("IZeit", null);
//                    hm.put("Dauer", 0);
//                    DBHandling.updateRecord("DFN", hm, "DFNID", dfnid);
//
//                } else {
//                    hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//                    hm.put("Ist", "!NOW!");
//                    hm.put("IZeit", SYSCalendar.ermittleZeit());
//                    DBHandling.updateRecord("DFN", hm, "DFNID", dfnid);
//                }
//
//                hm.clear();
//                tm.setUpdate(row, status);
//                if (fullReloadNecessary) {
//                    reloadTable();
//                }
//            }
//
//        }
//        // Nun noch Menüeinträge
//        if (evt.isPopupTrigger()) {
//            SYSTools.unregisterListeners(menu);
//            menu = new JPopupMenu();
//
//            JMenuItem itemPopupRefuse = new JMenuItem("Verweigert / nicht durchgeführt");
//            itemPopupRefuse.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    HashMap hm = new HashMap();
//                    hm.put("Status", TMDFN.STATUS_VERWEIGERT);
//                    hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//                    hm.put("Ist", "!NOW!");
//                    hm.put("IZeit", SYSCalendar.ermittleZeit());
//                    DBHandling.updateRecord("DFN", hm, "DFNID", dfnid);
//                    hm.clear();
//                    tm.setUpdate(row, TMDFN.STATUS_VERWEIGERT);
//                    //tm.reload(row, col);
//                }
//            });
//            menu.add(itemPopupRefuse);
//            ocs.setEnabled(this, "itemPopupRefuse", itemPopupRefuse, changeable && status == TMDFN.STATUS_OFFEN);
//
//            if (changeable) {
//                menu.add(new JSeparator());
//                int[] mins = new int[]{1, 2, 3, 4, 5, 10, 15, 20, 30, 45, 60, 120, 240, 360};
//                HashMap text = new HashMap();
//                text.put(60, "1 Stunde");
//                text.put(120, "2 Stunden");
//                text.put(240, "3 Stunden");
//                text.put(360, "4 Stunden");
//                for (int i = 0; i < mins.length; i++) {
//                    String einheit = mins[i] + " Minuten";
//                    if (text.containsKey(mins[i])) {
//                        einheit = mins[i] + " " + text.get(mins[i]).toString();
//                    }
//                    JMenuItem item = new JMenuItem(einheit);
//                    final int minutes = mins[i];
//                    item.addActionListener(new java.awt.event.ActionListener() {
//
//                        public void actionPerformed(java.awt.event.ActionEvent evt) {
//                            HashMap hm = new HashMap();
//                            hm.put("Dauer", minutes);
//                            DBHandling.updateRecord("DFN", hm, "DFNID", dfnid);
//                            hm.clear();
//                            tm.reload(row, TMDFN.COL_BEZEICHNUNG);
//                        }
//                    });
//                    menu.add(item);
//                    item.setEnabled(status == TMDFN.STATUS_ERLEDIGT);
//                }
//                text.clear();
//            }
//
//            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
//        }
//    }//GEN-LAST:event_tblDFNMousePressed
//
//    private void jspDFNComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspDFNComponentResized
//        JScrollPane jsp = (JScrollPane) evt.getComponent();
//        Dimension dim = jsp.getSize();
//        // Größe der Text Spalten im DFN ändern.
//        // Summe der fixen Spalten  = 175 + ein bisschen
//        int textWidth = dim.width - (50 + 80 + 55 + 80 + 25);
//        TableColumnModel tcm1 = tblDFN.getColumnModel();
//        if (tcm1.getColumnCount() < 4) {
//            return;
//        }
//
//        //tcm1.getColumn(TMDFN.COL_MassID).setPreferredWidth(50);
//        tcm1.getColumn(TMDFN.COL_BEZEICHNUNG).setPreferredWidth(textWidth / 2);
//        tcm1.getColumn(TMDFN.COL_ZEIT).setPreferredWidth(80);
//        tcm1.getColumn(TMDFN.COL_STATUS).setPreferredWidth(55);
//        tcm1.getColumn(TMDFN.COL_UKENNUNG).setPreferredWidth(80);
//        tcm1.getColumn(TMDFN.COL_BEMPLAN).setPreferredWidth(textWidth / 2);
//
//        //tcm1.getColumn(0).setHeaderValue("ID");
//        tcm1.getColumn(TMDFN.COL_BEZEICHNUNG).setHeaderValue("Bezeichnung");
//        tcm1.getColumn(TMDFN.COL_ZEIT).setHeaderValue("Zeit");
//        tcm1.getColumn(TMDFN.COL_STATUS).setHeaderValue("Status");
//        tcm1.getColumn(TMDFN.COL_UKENNUNG).setHeaderValue("PflegerIn");
//        tcm1.getColumn(TMDFN.COL_BEMPLAN).setHeaderValue("Hinweis");
    }//GEN-LAST:event_jspDFNComponentResized

//    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
//        Object[] sel = DlgMassSelect.showDialog(parent, true);
//        if (sel.length > 0) {
//            HashMap hmnew = new HashMap();
//            hmnew.put("Status", TMDFN.STATUS_ERLEDIGT);
//            hmnew.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//            hmnew.put("BWKennung", bwkennung);
//            hmnew.put("TermID", 0);
//            hmnew.put("Soll", "!NOW!");
//            hmnew.put("StDatum", "!NOW!");
//            hmnew.put("SZeit", SYSCalendar.ermittleZeit());
//            hmnew.put("Ist", "!NOW!");
//            hmnew.put("IZeit", SYSCalendar.ermittleZeit());
//            for (int i = 0; i < sel.length; i++) {
//                // Zuerst neuen DFN einfügen.
//                ListElement elmass = (ListElement) sel[i];
//                long massID = elmass.getPk();
//                hmnew.put("MassID", massID);
//                double dauer = ((BigDecimal) DBRetrieve.getSingleValue("Massnahmen", "Dauer", "MassID", massID)).doubleValue();
//                hmnew.put("Dauer", dauer);
//                DBHandling.insertRecord("DFN", hmnew);
//            }
//            reloadTable();
//        }
//    }//GEN-LAST:event_btnNewActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspDFN;
    private CollapsiblePanes cpDFN;
    // End of variables declaration//GEN-END:variables
}
