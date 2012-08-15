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
package op.care.bhp;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.planung.DFNTools;
import entity.prescription.BHP;
import entity.prescription.BHPTools;
import entity.prescription.DosageFormTools;
import entity.prescription.PrescriptionsTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class PnlBHP extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.bhp";
    private Resident resident;
    JPopupMenu menu;
    private boolean initPhase;
    private boolean abwesend;

    private HashMap<BHP, CollapsiblePane> bhpCollapsiblePaneHashMap;
    private HashMap<Byte, ArrayList<BHP>> shiftMAPBHP;
    private HashMap<Byte, CollapsiblePane> shiftMAPpane;
    private int MAX_TEXT_LENGTH = 110;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JDateChooser jdcDatum;
    private JComboBox cmbSchicht;

    private JideButton addButton;

    public PnlBHP(Resident resident, JScrollPane jspSearch) {
        initComponents();
        this.jspSearch = jspSearch;
        initPanel();
        switchResident(resident);
    }

    @Override
    public void switchResident(Resident bewohner) {

        this.resident = bewohner;
        OPDE.getDisplayManager().setMainMessage(ResidentTools.getBWLabelText(bewohner));

        initPhase = true;
        jdcDatum.setMinSelectableDate(DFNTools.getMinDatum(bewohner));
        jdcDatum.setDate(new Date());
        initPhase = false;

        reloadDisplay();
    }

    private void initPanel() {
        bhpCollapsiblePaneHashMap = new HashMap<BHP, CollapsiblePane>();
        shiftMAPpane = new HashMap<Byte, CollapsiblePane>();
        shiftMAPBHP = new HashMap<Byte, ArrayList<BHP>>();
        prepareSearchArea();
    }


    @Override
    public void cleanup() {
        jdcDatum.cleanup();
        SYSTools.unregisterListeners(this);
    }

    @Override
    public void reload() {
        reloadDisplay();
    }

    private void jspBHPComponentResized(ComponentEvent e) {
        // TODO add your code here
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
//            cpBHP.removeAll();
//
//            SwingWorker worker = new SwingWorker() {
//
//                @Override
//                protected Object doInBackground() throws Exception {
//
//                    int progress = 0;
//                    if (involvedNPs != null) {
//                        involvedNPs.clear();
//                    }
//                    dfnCollapsiblePaneHashMap.clear();
//                    nursingProcessArrayListHashMap.clear();
//                    involvedNPs = DFNTools.getInvolvedNPs((byte) (cmbSchicht.getSelectedIndex() - 1), resident, jdcDatum.getDate());
//
//                    cpDFN.setLayout(new JideBoxLayout(cpDFN, JideBoxLayout.Y_AXIS));
//                    for (NursingProcess np : involvedNPs) {
//                        progress++;
//                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, involvedNPs.size()));
//                        cpDFN.add(createCollapsiblePanesFor(np));
//                    }
//
//                    // this adds unassigned DFNs to the panel
//                    CollapsiblePane unassignedPane = createCollapsiblePanesFor((NursingProcess) null);
//                    if (unassignedPane != null) {
//                        cpDFN.add(unassignedPane);
//                    }
//
//                    return null;
//                }
//
//                @Override
//                protected void done() {
//                    cpDFN.addExpansion();
//                    cpDFN.repaint();
//                    initPhase = false;
//                    OPDE.getDisplayManager().setProgressBarMessage(null);
//                    OPDE.getMainframe().setBlocked(false);
//                }
//            };
//            worker.execute();

        } else {

            cpBHP.removeAll();
            bhpCollapsiblePaneHashMap.clear();
            if (shiftMAPBHP != null) {
                for (Byte key : shiftMAPBHP.keySet()) {
                    shiftMAPBHP.get(key).clear();
                }
                shiftMAPBHP.clear();
            }
            if (shiftMAPpane != null) {
                for (Byte key : shiftMAPpane.keySet()) {
                    shiftMAPpane.get(key).removeAll();
                }
                shiftMAPpane.clear();
            }

            cpBHP.setLayout(new JideBoxLayout(cpBHP, JideBoxLayout.Y_AXIS));

//            ArrayList<BHP> allBHPsForToday = ;
            for (BHP bhp : BHPTools.getBHPs(resident, jdcDatum.getDate())) {
                if (!shiftMAPBHP.containsKey(bhp.getShift())) {
                    shiftMAPBHP.put(bhp.getShift(), new ArrayList<BHP>());
                }
                shiftMAPBHP.get(bhp.getShift()).add(bhp);


            }

//            // this adds unassigned DFNs to the panel
//            CollapsiblePane unassignedPane = createCollapsiblePanesFor((NursingProcess) null);
//            if (unassignedPane != null) {
//                cpDFN.add(unassignedPane);
//            }

            for (Byte shift : new Byte[]{BHPTools.SHIFT_VERY_EARLY, BHPTools.SHIFT_EARLY, BHPTools.SHIFT_LATE, BHPTools.SHIFT_VERY_LATE}) {
                shiftMAPpane.put(shift, createCPFor(shift));
                cpBHP.add(shiftMAPpane.get(shift));
                try {
                    shiftMAPpane.get(shift).setCollapsed(shift != SYSCalendar.whatShiftIs(new Date()));
                } catch (PropertyVetoException e) {
                    OPDE.debug(e);
                }
            }
            cpBHP.addExpansion();

        }
        initPhase = false;
    }


    private CollapsiblePane createCPFor(final Byte shift) {
        /***
         *  ____                _        ____ ____   __            ____  _   _ ___ _____ _____
         * / ___|_ __ ___  __ _| |_ ___ / ___|  _ \ / _| ___  _ __/ ___|| | | |_ _|  ___|_   _|
         * | |   | '__/ _ \/ _` | __/ _ \ |   | |_) | |_ / _ \| '__\___ \| |_| || || |_    | |
         * | |___| | |  __/ (_| | ||  __/ |___|  __/|  _| (_) | |   ___) |  _  || ||  _|   | |
         * \____|_|  \___|\__,_|\__\___|\____|_|   |_|  \___/|_|  |____/|_| |_|___|_|     |_|
         */
        String title = "";

        if (shift == BHPTools.SHIFT_ON_DEMAND) {
            title = OPDE.lang.getString(internalClassID + ".ondemand");
        } else {
            title = GUITools.getLocalizedMessages(BHPTools.SHIFT_TEXT)[shift];
        }

        final CollapsiblePane prPane = new CollapsiblePane(title);

        prPane.setSlidingDirection(SwingConstants.SOUTH);
        prPane.setBackground(SYSCalendar.getBGSHIFT(shift));
        prPane.setForeground(SYSCalendar.getFGSHIFT(shift));
        prPane.setOpaque(false);

        JPanel prPanel = new JPanel();
        prPanel.setLayout(new VerticalLayout());

        if (shiftMAPBHP.containsKey(shift)) {
            for (BHP bhp : shiftMAPBHP.get(shift)) {
                prPanel.setBackground(bhp.getBG());
                bhpCollapsiblePaneHashMap.put(bhp, createCPFor(bhp));
                prPanel.add(bhpCollapsiblePaneHashMap.get(bhp));
            }
            prPane.setCollapsible(true);
        } else {
            prPane.setCollapsible(false);
        }

        prPane.setContentPane(prPanel);

//        prPane.setEmphasized(shift == SYSCalendar.whatShiftIs(new Date()));

        return prPane;
    }

    private CollapsiblePane createCPFor(final BHP bhp) {
        final CollapsiblePane bhpPane = new CollapsiblePane();
        String fg = SYSConst.html_grey50;
//        if (dfn.getNursingProcess() != null) {
//            fg = "#" + dfn.getNursingProcess().getKategorie().getFgcontent();
//        }

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
        JideButton btnBHP = GUITools.createHyperlinkButton("<html><font size=+1>" +
                SYSTools.left(PrescriptionsTools.getPrescriptionAsShortText(bhp.getPrescriptionSchedule().getPrescription()), MAX_TEXT_LENGTH) + "</font>" +
                BHPTools.getScheduleText(bhp, " <b>[", "]</b>") +
                (bhp.hasMed() ? " <font size=+1>" + SYSTools.getAsHTML(bhp.getDosis()) +
                        " " + DosageFormTools.getUsageText(bhp.getPrescription().getDarreichung().getDosageForm()) + "</font>" : "") +
                (bhp.getUser() != null ? ", <u>" + bhp.getUser().getUKennung() + "</u>" : "") +
                "</html>", BHPTools.getIcon(bhp), null);

        OPDE.debug(btnBHP.getText());

//        title.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
        btnBHP.setAlignmentX(Component.LEFT_ALIGNMENT);


        btnBHP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    bhpPane.setCollapsed(!bhpPane.isCollapsed());
                } catch (PropertyVetoException e) {
                    OPDE.error(e);
                }
            }
        });

        titlePanelleft.add(btnBHP);


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
            btnApply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
//                    if (dfn.getStatus() == DFNTools.STATE_DONE) {
//                        return;
//                    }
//
//                    if (DFNTools.isChangeable(dfn)) {
//                        EntityManager em = OPDE.createEM();
//                        try {
//                            em.getTransaction().begin();
//
//
//                            em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC);
//                            DFN myDFN = em.merge(dfn);
//                            em.lock(myDFN, LockModeType.OPTIMISTIC);
//
//                            myDFN.setStatus(DFNTools.STATE_DONE);
//                            myDFN.setUser(em.merge(OPDE.getLogin().getUser()));
//                            myDFN.setIst(new Date());
//                            myDFN.setiZeit(SYSCalendar.whatTimeIDIs(new Date()));
//                            myDFN.setMdate(new Date());
//                            dfnCollapsiblePaneHashMap.put(myDFN, createCollapsiblePanesFor(myDFN));
//                            int position = nursingProcessArrayListHashMap.get(myDFN.getNursingProcess()).indexOf(dfn);
//                            nursingProcessArrayListHashMap.get(myDFN.getNursingProcess()).remove(dfn);
//                            nursingProcessArrayListHashMap.get(myDFN.getNursingProcess()).add(position, myDFN);
//                            Collections.sort(nursingProcessArrayListHashMap.get(myDFN.getNursingProcess()));
//                            em.getTransaction().commit();
//                            refreshDisplay();
//                        } catch (OptimisticLockException ole) {
//                            if (em.getTransaction().isActive()) {
//                                em.getTransaction().rollback();
//                            }
//                            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                OPDE.getMainframe().emptyFrame();
//                                OPDE.getMainframe().afterLogin();
//                            }
//                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                        } catch (Exception e) {
//                            if (em.getTransaction().isActive()) {
//                                em.getTransaction().rollback();
//                            }
//                            OPDE.fatal(e);
//                        } finally {
//                            em.close();
//                        }
//
//                    } else {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
//                    }
                }
            });
//            btnApply.setEnabled(!dfn.isOnDemand());
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
//                    if (dfn.getStatus() == DFNTools.STATE_REFUSED) {
//                        return;
//                    }
//
//                    if (DFNTools.isChangeable(dfn)) {
//                        EntityManager em = OPDE.createEM();
//                        try {
//                            em.getTransaction().begin();
//
//                            em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC);
//                            DFN myDFN = em.merge(dfn);
//                            em.lock(myDFN, LockModeType.OPTIMISTIC);
//
//                            myDFN.setStatus(DFNTools.STATE_REFUSED);
//                            myDFN.setUser(em.merge(OPDE.getLogin().getUser()));
//                            myDFN.setIst(new Date());
//                            myDFN.setiZeit(SYSCalendar.whatTimeIDIs(new Date()));
//                            myDFN.setMdate(new Date());
//                            dfnCollapsiblePaneHashMap.put(myDFN, createCollapsiblePanesFor(myDFN));
//                            int position = nursingProcessArrayListHashMap.get(myDFN.getNursingProcess()).indexOf(dfn);
//                            nursingProcessArrayListHashMap.get(myDFN.getNursingProcess()).remove(dfn);
//                            nursingProcessArrayListHashMap.get(myDFN.getNursingProcess()).add(position, myDFN);
//                            Collections.sort(nursingProcessArrayListHashMap.get(myDFN.getNursingProcess()));
//
//                            em.getTransaction().commit();
//                            refreshDisplay();
//                        } catch (OptimisticLockException ole) {
//                            if (em.getTransaction().isActive()) {
//                                em.getTransaction().rollback();
//                            }
//                            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                OPDE.getMainframe().emptyFrame();
//                                OPDE.getMainframe().afterLogin();
//                            }
//                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                        } catch (Exception e) {
//                            if (em.getTransaction().isActive()) {
//                                em.getTransaction().rollback();
//                            }
//                            OPDE.fatal(e);
//                        } finally {
//                            em.close();
//                        }
//
//                    } else {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
//                    }
                }
            });
//            btnCancel.setEnabled(!dfn.isOnDemand());
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
//                    if (dfn.getStatus() == DFNTools.STATE_OPEN) {
//                        return;
//                    }
//
//                    if (DFNTools.isChangeable(dfn)) {
//                        EntityManager em = OPDE.createEM();
//                        try {
//                            em.getTransaction().begin();
//
//                            em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC);
//                            DFN myDFN = em.merge(dfn);
//                            em.lock(myDFN, LockModeType.OPTIMISTIC);
//
//                            // on demand DFNs are deleted if they not wanted anymore
//                            if (myDFN.isOnDemand()) {
//                                em.remove(myDFN);
//                                dfnCollapsiblePaneHashMap.remove(myDFN);
//                                unassignedDFNArrayList.remove(myDFN);
//                            } else {
//                                // the normal DFNs (those assigned to a NursingProcess) are reset to the OPEN state.
//                                myDFN.setStatus(DFNTools.STATE_OPEN);
//                                myDFN.setUser(null);
//                                myDFN.setIst(null);
//                                myDFN.setiZeit(null);
//                                myDFN.setMdate(new Date());
//                                dfnCollapsiblePaneHashMap.put(myDFN, createCollapsiblePanesFor(myDFN));
//                                int position = nursingProcessArrayListHashMap.get(myDFN.getNursingProcess()).indexOf(dfn);
//                                nursingProcessArrayListHashMap.get(myDFN.getNursingProcess()).remove(dfn);
//                                nursingProcessArrayListHashMap.get(myDFN.getNursingProcess()).add(position, myDFN);
//                                Collections.sort(nursingProcessArrayListHashMap.get(myDFN.getNursingProcess()));
//                            }
//
//                            em.getTransaction().commit();
//                            refreshDisplay();
//                        } catch (OptimisticLockException ole) {
//                            if (em.getTransaction().isActive()) {
//                                em.getTransaction().rollback();
//                            }
//                            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                OPDE.getMainframe().emptyFrame();
//                                OPDE.getMainframe().afterLogin();
//                            }
//                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                        } catch (Exception e) {
//                            if (em.getTransaction().isActive()) {
//                                em.getTransaction().rollback();
//                            }
//                            OPDE.fatal(e);
//                        } finally {
//                            em.close();
//                        }
//
//                    } else {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
//                    }
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

        bhpPane.setTitleLabelComponent(titlePanel);
        bhpPane.setSlidingDirection(SwingConstants.SOUTH);
//        OPDE.debug("Dimension: " + dfnPane.getPreferredSize());

        /***
         *       ___ ___  _  _ _____ ___ _  _ _____
         *      / __/ _ \| \| |_   _| __| \| |_   _|
         *     | (_| (_) | .` | | | | _|| .` | | |
         *      \___\___/|_|\_| |_| |___|_|\_| |_|
         *
         */

        JTextPane contentPane = new JTextPane();
        contentPane.setEditable(false);
        contentPane.setContentType("text/html");
        contentPane.setText(SYSTools.toHTML(PrescriptionsTools.getPrescriptionAsHTML(bhp.getPrescription(), false, false)));
        bhpPane.setContentPane(contentPane);
        bhpPane.setBackground(bhp.getBG());
        bhpPane.setForeground(bhp.getFG());

        try {
            bhpPane.setCollapsed(true);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

        bhpPane.setHorizontalAlignment(SwingConstants.LEADING);
        bhpPane.setOpaque(false);
        return bhpPane;
    }


    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspBHP = new JScrollPane();
        cpBHP = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspBHP ========
        {
            jspBHP.setBorder(null);
            jspBHP.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    jspBHPComponentResized(e);
                }
            });

            //======== cpBHP ========
            {
                cpBHP.setLayout(new BoxLayout(cpBHP, BoxLayout.X_AXIS));
            }
            jspBHP.setViewportView(cpBHP);
        }
        add(jspBHP);
    }// </editor-fold>//GEN-END:initComponents

//    private void tblBHPMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBHPMousePressed
//        if (!OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {
//            return;
//        } // Hier dürfen nur Examen dran.
//        final TMBHP tm = (TMBHP) tblBHP.getModel();
//        if (tm.getRowCount() == 0) {
//            return;
//        }
//        Point p = evt.getPoint();
//        final int col = tblBHP.columnAtPoint(p);
//        final int row = tblBHP.rowAtPoint(p);
//
//        Point p2 = evt.getPoint();
//        // Convert a coordinate relative to a component's bounds to screen coordinates
//        SwingUtilities.convertPointToScreen(p2, tblBHP);
//
//        final Point screenposition = p2;
//
//        ListSelectionModel lsm = tblBHP.getSelectionModel();
//        lsm.setSelectionInterval(row, row);
//
//        BHP bhp = tm.getBHP(row);
////        Verordnung verordnung = bhp.getPrescription();
//
//        boolean changeable =
//                // Diese Kontrolle stellt sicher, dass ein User nur seine eigenen Einträge und das auch nur
//                // eine halbe Stunde lang bearbeiten kann.
//                // Ausserdem kann man nur dann etwas geben, wenn es
//                //      a) eine Massnahmen ohne Medikation ist
//                //      ODER
//                //      (
//                //          b) ein angebrochener Bestand vorhanden ist
//                //          UND
//                //          c)  das häkchen NICHT gesetzt ist oder wenn es gesetzt ist, kann man es
//                //              nur dann wieder wegnehmen, wenn es derselbe Benutzer FRüH GENUG tut.
//                //              Und auch nur dann, wenn nicht mehrere Packungen beim Ausbuchen betroffen waren.
//                //          )
//                //      )
//                !abwesend &&
//                        !bhp.getPrescription().isAbgesetzt()
//                        // Offener Status geht immer
//                        && (
//                        bhp.getStatus() == BHPTools.STATE_OPEN
//                                // Nicht mehr offen ?
//                                // Dann nur wenn derselbe Benutzer dass wieder rückgängig machen will
//                                ||
//                                (bhp.getUser().equals(OPDE.getLogin().getUser())
//                                        // und es noch früh genug ist (30 Minuten)
//                                        && SYSCalendar.earlyEnough(bhp.getMDate().getTime(), BHP_MAX_MINUTES_TO_WITHDRAW)
//                                        // und kein abgesetzter Bestand beteiligt ist. Das verhindert einfach, dass bei
//                                        // eine Rückgabe eines Vorrates verhindert wird, wenn bei Abhaken eine Packung leer wurde und direkt eine neue
//                                        // angebrochen wurde.
//                                        && !bhp.hasAbgesetzteBestand()
//                                )
//                );
//
//        OPDE.debug(changeable ? "BHP changeable" : "BHP NOT changeable");
//
//        if (changeable) {
//            if (!evt.isPopupTrigger() && col == TMBHP.COL_STATUS) { // Drückt auch wirklich mit der LINKEN Maustaste auf die mittlere Spalte.
//
//                byte status = bhp.getStatus();
//                MedInventory inventory = bhp.getPrescription().hasMedi() ? TradeFormTools.getVorratZurDarreichung(resident, bhp.getPrescription().getDarreichung()) : null;
//
//                boolean deleted = false; // für das richtige fireTableRows....
//                if (!bhp.getPrescription().hasMedi() || status != BHPTools.STATE_OPEN || MedStockTools.getStockInUse(inventory) != null) {
//                    status++;
//                    if (status > 1) {
//                        status = BHPTools.STATE_OPEN;
//                    }
//                    EntityManager em = OPDE.createEM();
//
//                    try {
//                        em.getTransaction().begin();
//
//                        bhp = em.merge(bhp);
//
//                        em.lock(bhp.getPrescription(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//                        em.lock(bhp, LockModeType.OPTIMISTIC);
//
//                        bhp.setStatus(status);
//                        bhp.setMDate(new Date());
//                        if (status == BHPTools.STATE_OPEN) {
//                            bhp.setUser(null);
//                            bhp.setIst(null);
//                            bhp.setiZeit(null);
//                            bhp.setBemerkung(null);
//                        } else {
//                            bhp.setUser(em.merge(OPDE.getLogin().getUser()));
//                            bhp.setIst(new Date());
//                            bhp.setiZeit(SYSCalendar.ermittleZeit());
//                        }
//
//                        if (bhp.getPrescription().hasMedi()) {
//                            if (status == BHPTools.STATE_DONE) {
//                                MedInventoryTools.entnahmeVorrat(em, em.merge(inventory), bhp.getDosis(), true, bhp);
//                            } else {
//                                for (MedStockTransaction buchung : bhp.getStockTransaction()) {
//                                    em.remove(buchung);
//                                }
//                                bhp.getStockTransaction().clear();
//                            }
//                        }
//                        // Wenn man eine Massnahme aus der Bedarfsmedikation
//                        // rückgängig macht, wird sie gelöscht.
//                        if (bhp.getPrescription().isBedarf() && status == BHPTools.STATE_OPEN) {
//                            em.remove(bhp);
//                            deleted = true;
//                        }
//                        em.getTransaction().commit();
//                    } catch (OptimisticLockException ole) {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Wurde zwischenzeitlich von jemand anderem geändert.", DisplayMessage.IMMEDIATELY, OPDE.getErrorMessageTime()));
//                        em.getTransaction().rollback();
//                        deleted = true; // damit alles neu geladen wird.
//                    } catch (Exception ex) {
//                        if (em.getTransaction().isActive()) {
//                            em.getTransaction().rollback();
//                        }
//                        OPDE.fatal(ex);
//                    } finally {
//                        em.close();
//                    }
//                    reloadTable();
////                    if (deleted) {
////                        reloadTable();
////                    } else {
////                        tm.setBHP(row, bhp);
////                        tm.fireTableRowsUpdated(row, row);
////                    }
//                }
//            }
//        }
//
//        if (!changeable && !evt.isPopupTrigger()) { // sagen warum das nicht ging
//
//            String msg = "";
//
//            if (abwesend) {
//                msg += "BewohnerIn ist abwesend. ";
//            } else if (bhp.getPrescription().isAbgesetzt()) {
//                msg += "Die Verordnung ist abgesetzt. ";
//            }
//
//            if (bhp.getStatus() != BHPTools.STATE_OPEN) {
//                if (!bhp.getUser().equals(OPDE.getLogin().getUser())) {
//                    msg += "Nur derselbe Benutzer kann das Häkchen wieder wegnehmen. ";
//                } else if (!SYSCalendar.earlyEnough(bhp.getMDate().getTime(), BHP_MAX_MINUTES_TO_WITHDRAW)) {
//                    msg += "Zu spät, sie können das Häkchen nur " + BHP_MAX_MINUTES_TO_WITHDRAW + " Minuten lang wieder zurück nehmen. ";
//                } else if (bhp.hasAbgesetzteBestand()) {
//                    msg += "Beim Abhaken wurde eine Packung abgeschlossen und eine neue angebrochen. ";
//                }
//            }
//
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(msg, DisplayMessage.WARNING));
//
//        }
//
//        // Nun noch Menüeinträge
//        if (evt.isPopupTrigger()) {
//            SYSTools.unregisterListeners(menu);
//            menu = new JPopupMenu();
//            final BHP myBHP = bhp;
//            if (bhp.getPrescription().hasMedi()) {
//                JMenuItem itemPopupXDiscard = new JMenuItem("Verweigert (Medikament wird trotzdem ausgebucht.)");
//                itemPopupXDiscard.addActionListener(new java.awt.event.ActionListener() {
//                    public void actionPerformed(java.awt.event.ActionEvent evt) {
//
//                        EntityManager em = OPDE.createEM();
//                        try {
//                            em.getTransaction().begin();
//                            BHP innerbhp = em.merge(myBHP);
//                            em.lock(innerbhp.getPrescription(), LockModeType.OPTIMISTIC);
//                            em.lock(innerbhp, LockModeType.OPTIMISTIC);
//
//                            innerbhp.setStatus(BHPTools.STATE_REFUSED_DISCARDED);
//                            innerbhp.setUser(em.merge(OPDE.getLogin().getUser()));
//                            innerbhp.setIst(new Date());
//                            innerbhp.setiZeit(SYSCalendar.ermittleZeit());
//                            innerbhp.setMDate(new Date());
//
//                            MedInventory inventory = TradeFormTools.getVorratZurDarreichung(em, innerbhp.getPrescriptionSchedule().getPrescription().getBewohner(), innerbhp.getPrescriptionSchedule().getPrescription().getDarreichung());
//                            MedInventoryTools.entnahmeVorrat(em, inventory, innerbhp.getDosis(), innerbhp);
//
//                            em.getTransaction().commit();
//                            tm.setBHP(row, innerbhp);
//                            tm.fireTableRowsUpdated(row, row);
//                        } catch (OptimisticLockException ole) {
//                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Wurde zwischenzeitlich von jemand anderem geändert.", DisplayMessage.IMMEDIATELY, 2));
//                            em.getTransaction().rollback();
//                            reloadTable();
//                        } catch (Exception e) {
//                            em.getTransaction().rollback();
//                            OPDE.fatal(e);
//                        } finally {
//                            em.close();
//                        }
//                    }
//                });
//                menu.add(itemPopupXDiscard);
//                itemPopupXDiscard.setEnabled(myBHP.getStatus() == BHPTools.STATE_OPEN);
//
////                menu.add(new JSeparator());
//            }
//            //-----------------------------------------
//            String str = "Verweigert";
//            if (bhp.getPrescription().hasMedi()) {
//                str = "Verweigert (Medikament wird nicht ausgebucht.)";
//            }
//
//            JMenuItem itemPopupXPreserve = new JMenuItem(str);
//            itemPopupXPreserve.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    EntityManager em = OPDE.createEM();
//                    try {
//                        em.getTransaction().begin();
//                        BHP innerBHP = em.merge(myBHP);
//                        em.lock(innerBHP, LockModeType.OPTIMISTIC);
//                        em.lock(innerBHP.getPrescription(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//
//                        innerBHP.setStatus(BHPTools.STATE_REFUSED);
//                        innerBHP.setUser(em.merge(OPDE.getLogin().getUser()));
//                        innerBHP.setIst(new Date());
//                        innerBHP.setiZeit(SYSCalendar.ermittleZeit());
//                        innerBHP.setMDate(new Date());
//                        em.getTransaction().commit();
//                        tm.setBHP(row, innerBHP);
//                        tm.fireTableRowsUpdated(row, row);
//                    } catch (OptimisticLockException ole) {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Wurde zwischenzeitlich von jemand anderem geändert.", DisplayMessage.IMMEDIATELY, 2));
//                        em.getTransaction().rollback();
//                        reloadTable();
//                    } catch (Exception e) {
//                        em.getTransaction().rollback();
//                        OPDE.fatal(e);
//                    } finally {
//                        em.close();
//                    }
//                }
//            });
//            menu.add(itemPopupXPreserve);
//            itemPopupXPreserve.setEnabled(changeable && myBHP.getStatus() == BHPTools.STATE_OPEN);
//
//            if (bhp.getPrescription().hasMedi()) {
//
//
//                final MedStock bestand = MedStockTools.getStockInUse(TradeFormTools.getVorratZurDarreichung(resident, bhp.getPrescription().getDarreichung()));
//
//                if (bestand != null) {
//
//                    menu.add(new JSeparator());
//
//                    JMenuItem itemPopupBuchungen = new JMenuItem("Buchungen von Bestands Nr. " + bestand.getBestID() + " anzeigen");
//                    itemPopupBuchungen.addActionListener(new java.awt.event.ActionListener() {
//
//                        public void actionPerformed(java.awt.event.ActionEvent evt) {
//
//                            final JidePopup popup = new JidePopup();
//
//                            PnlBuchungen dlg = new PnlBuchungen(bestand, null);
//
//                            popup.setMovable(false);
//                            popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//                            popup.getContentPane().add(dlg);
//                            popup.setOwner(tblBHP);
//                            popup.removeExcludedComponent(tblBHP);
//                            popup.setDefaultFocusComponent(dlg);
//                            popup.showPopup(screenposition.x, screenposition.y);
//                        }
//                    });
//                    menu.add(itemPopupBuchungen);
//                    itemPopupBuchungen.setEnabled(true);
//                }
//            }
//
//            if (OPDE.isAdmin()) {
//                menu.add(new JSeparator());
//                final BHP mybhp = bhp;
//                JMenuItem itemPopupINFO = new JMenuItem("Infos anzeigen");
//                itemPopupINFO.addActionListener(new java.awt.event.ActionListener() {
//                    public void actionPerformed(java.awt.event.ActionEvent evt) {
//                        String message = "BHPID: " + mybhp.getBHPid();
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(message, 10));
//                    }
//                });
//                menu.add(itemPopupINFO);
//            }
//
//            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
//        }
//    }//GEN-LAST:event_tblBHPMousePressed
//
//
//    private void jspBHPComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspBHPComponentResized
//        JScrollPane jsp = (JScrollPane) evt.getComponent();
//        Dimension dim = jsp.getSize();
//        // Größe der Text Spalten im DFN ändern.
//        // Summe der fixen Spalten  = 175 + ein bisschen
//        int textWidth = dim.width - (50 + 80 + 35 + 80 + 25);
//        TableColumnModel tcm1 = tblBHP.getColumnModel();
//        if (tcm1.getColumnCount() < 6) {
//            return;
//        }
//
//        //tcm1.getColumn(TMBHP.COL_massid).setPreferredWidth(50);
//        tcm1.getColumn(TMBHP.COL_BEZEICHNUNG).setPreferredWidth(textWidth / 2);
//        tcm1.getColumn(TMBHP.COL_DOSIS).setPreferredWidth(50);
//        tcm1.getColumn(TMBHP.COL_ZEIT).setPreferredWidth(80);
//        tcm1.getColumn(TMBHP.COL_STATUS).setPreferredWidth(35);
//        tcm1.getColumn(TMBHP.COL_UKENNUNG).setPreferredWidth(80);
//        tcm1.getColumn(TMBHP.COL_BEMPLAN).setPreferredWidth(textWidth / 2);
////        tcm1.getColumn(TMBHP.COL_BEMBHP).setPreferredWidth(35);
//
//        //tcm1.getColumn(0).setHeaderValue("ID");
//        tcm1.getColumn(TMBHP.COL_BEZEICHNUNG).setHeaderValue("Bezeichnung");
//        tcm1.getColumn(TMBHP.COL_DOSIS).setHeaderValue("Dosis");
//        tcm1.getColumn(TMBHP.COL_ZEIT).setHeaderValue("Zeit");
//        tcm1.getColumn(TMBHP.COL_STATUS).setHeaderValue("Status");
//        tcm1.getColumn(TMBHP.COL_UKENNUNG).setHeaderValue("PflegerIn");
//        tcm1.getColumn(TMBHP.COL_BEMPLAN).setHeaderValue("Hinweis");
////        tcm1.getColumn(TMBHP.COL_BEMBHP).setHeaderValue("!");
//    }//GEN-LAST:event_jspBHPComponentResized
//
//    private void reloadTable() {
//
//        tblBHP.setModel(new TMBHP(resident, jdcDatum.getDate(), cmbSchicht.getSelectedIndex() - 1));
//        tblBHP.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
//        jspBHP.dispatchEvent(new ComponentEvent(jspBHP, ComponentEvent.COMPONENT_RESIZED));
//        tblBHP.getColumnModel().getColumn(TMBHP.COL_BEZEICHNUNG).setCellRenderer(new RNDBHP());
//        tblBHP.getColumnModel().getColumn(TMBHP.COL_DOSIS).setCellRenderer(new RNDBHP());
//        tblBHP.getColumnModel().getColumn(TMBHP.COL_ZEIT).setCellRenderer(new RNDBHP());
//        tblBHP.getColumnModel().getColumn(TMBHP.COL_STATUS).setCellRenderer(new RNDBHP());
//        tblBHP.getColumnModel().getColumn(TMBHP.COL_UKENNUNG).setCellRenderer(new RNDBHP());
//        tblBHP.getColumnModel().getColumn(TMBHP.COL_BEMPLAN).setCellRenderer(new RNDBHP());
////        tblBHP.getColumnModel().getColumn(TMBHP.COL_BEMBHP).setCellRenderer(new RNDBHP());
//
//    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);


        searchPanes.add(addCommands());
        searchPanes.add(addFilter());

        searchPanes.addExpansion();

    }

    private CollapsiblePane addFilter() {

        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setLayout(new VerticalLayout());

        CollapsiblePane panelFilter = new CollapsiblePane("Auswahl");
        panelFilter.setStyle(CollapsiblePane.PLAIN_STYLE);
        panelFilter.setCollapsible(false);

        cmbSchicht = new JComboBox(new DefaultComboBoxModel(GUITools.getLocalizedMessages(new String[]{"misc.msg.everything", internalClassID + ".shift.veryearly", internalClassID + ".shift.early", internalClassID + ".shift.late", internalClassID + ".shift.verylate"})));
        cmbSchicht.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbSchicht.setSelectedIndex(SYSCalendar.ermittleSchicht() + 1);
        cmbSchicht.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (!initPhase) {
                    reloadDisplay();
                }
            }
        });
        labelPanel.add(cmbSchicht);

        jdcDatum = new JDateChooser(new Date());
        jdcDatum.setFont(new Font("Arial", Font.PLAIN, 14));
        jdcDatum.setMinSelectableDate(BHPTools.getMinDatum(resident));

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
        labelPanel.add(jdcDatum);

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

        labelPanel.add(buttonPanel);

        panelFilter.setContentPane(labelPanel);

        return panelFilter;
    }


    private CollapsiblePane addCommands() {
        final JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane("BHP");
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            addButton = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnadd"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    if (PrescriptionsTools.hasBedarf(resident)) {

                        final JidePopup popup = new JidePopup();

                        DlgBedarf dlg = new DlgBedarf(resident, new Closure() {
                            @Override
                            public void execute(Object o) {
                                popup.hidePopup();
                                if (o != null) {
                                    reloadDisplay();
                                }
                            }
                        });

                        popup.setMovable(false);
                        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                        popup.getContentPane().add(dlg);
                        popup.setOwner(addButton);
                        popup.removeExcludedComponent(addButton);
                        popup.setDefaultFocusComponent(dlg);
                        Point p = new Point(addButton.getX(), addButton.getY());
                        SwingUtilities.convertPointToScreen(p, addButton);
                        popup.showPopup(p.x, p.y - (int) dlg.getPreferredSize().getHeight()); // - (int) addButton.getPreferredSize().getHeight()
                    } else {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Keine Bedarfsverordnungen vorhanden"));
                    }
                }
            });
            mypanel.add(addButton);
        }

        searchPane.setContentPane(mypanel);
        return searchPane;
    }


    private void addButtonActionPerformed(ActionEvent actionEvent) {

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspBHP;
    private CollapsiblePanes cpBHP;
    // End of variables declaration//GEN-END:variables
}
