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
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.nursingprocess.*;
import op.OPDE;
import op.care.planung.massnahmen.PnlSelectIntervention;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author root
 */
public class PnlDFN extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.dfn";

    Resident resident;

    private boolean initPhase;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JDateChooser jdcDatum;

    private HashMap<DFN, CollapsiblePane> dfnCollapsiblePaneHashMap;
    private HashMap<Byte, ArrayList<DFN>> shiftMAPDFN;
    private HashMap<Byte, CollapsiblePane> shiftMAPpane;
    private int MAX_TEXT_LENGTH = 65;

    public PnlDFN(Resident resident, JScrollPane jspSearch) {
        initComponents();
        this.jspSearch = jspSearch;
        initPanel();
        switchResident(resident);
    }

    private void initPanel() {
        shiftMAPpane = new HashMap<Byte, CollapsiblePane>();
        shiftMAPDFN = new HashMap<Byte, ArrayList<DFN>>();
        for (Byte shift : new Byte[]{DFNTools.SHIFT_ON_DEMAND, DFNTools.SHIFT_VERY_EARLY, DFNTools.SHIFT_EARLY, DFNTools.SHIFT_LATE, DFNTools.SHIFT_VERY_LATE}) {
            shiftMAPDFN.put(shift, new ArrayList<DFN>());
        }
        dfnCollapsiblePaneHashMap = new HashMap<DFN, CollapsiblePane>();
        prepareSearchArea();
    }

    @Override
    public void cleanup() {
        jdcDatum.cleanup();
//        involvedNPs.clear();
        cpDFN.removeAll();
        dfnCollapsiblePaneHashMap.clear();
        SYSTools.unregisterListeners(this);
    }

    @Override
    public void reload() {
        reloadDisplay();
    }


    @Override
    public void switchResident(Resident bewohner) {
        this.resident = bewohner;
        OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(bewohner));

        initPhase = true;
        jdcDatum.setMinSelectableDate(DFNTools.getMinDatum(bewohner));
        jdcDatum.setDate(new Date());
        initPhase = false;

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
        dfnCollapsiblePaneHashMap.clear();
        if (shiftMAPDFN != null) {
            for (Byte key : shiftMAPDFN.keySet()) {
                shiftMAPDFN.get(key).clear();
            }
        }
        if (shiftMAPpane != null) {
            for (Byte key : shiftMAPpane.keySet()) {
                shiftMAPpane.get(key).removeAll();
            }
            shiftMAPpane.clear();
        }
        final boolean withworker = true;
        if (withworker) {

            OPDE.getMainframe().setBlocked(true);
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

            cpDFN.removeAll();

            SwingWorker worker = new SwingWorker() {

                @Override
                protected Object doInBackground() throws Exception {

                    int progress = 0;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, 100));

                    for (DFN dfn : DFNTools.getDFNs(resident, jdcDatum.getDate())) {
                        shiftMAPDFN.get(dfn.getShift()).add(dfn);
                    }

                    for (Byte shift : new Byte[]{DFNTools.SHIFT_ON_DEMAND, DFNTools.SHIFT_VERY_EARLY, DFNTools.SHIFT_EARLY, DFNTools.SHIFT_LATE, DFNTools.SHIFT_VERY_LATE}) {
                        shiftMAPpane.put(shift, createCP4(shift));
                        try {
                            shiftMAPpane.get(shift).setCollapsed(shift == DFNTools.SHIFT_ON_DEMAND || shift != SYSCalendar.whatShiftIs(new Date()));
                        } catch (PropertyVetoException e) {
                            OPDE.debug(e);
                        }
                        progress += 20;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, 100));
                    }

                    return null;
                }

                @Override
                protected void done() {
                    buildPanel(true);
                    initPhase = false;
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();

        } else {

            for (DFN dfn : DFNTools.getDFNs(resident, jdcDatum.getDate())) {
                shiftMAPDFN.get(dfn.getShift()).add(dfn);
            }

            for (Byte shift : new Byte[]{DFNTools.SHIFT_ON_DEMAND, DFNTools.SHIFT_VERY_EARLY, DFNTools.SHIFT_EARLY, DFNTools.SHIFT_LATE, DFNTools.SHIFT_VERY_LATE}) {
                shiftMAPpane.put(shift, createCP4(shift));
                try {
                    shiftMAPpane.get(shift).setCollapsed(shift == DFNTools.SHIFT_ON_DEMAND || shift != SYSCalendar.whatShiftIs(new Date()));
                } catch (PropertyVetoException e) {
                    OPDE.debug(e);
                }
            }

            buildPanel(true);
        }
        initPhase = false;
    }

    private void buildPanel(boolean resetCollapseState) {
        cpDFN.removeAll();
        cpDFN.setLayout(new JideBoxLayout(cpDFN, JideBoxLayout.Y_AXIS));
        for (Byte shift : new Byte[]{DFNTools.SHIFT_ON_DEMAND, DFNTools.SHIFT_VERY_EARLY, DFNTools.SHIFT_EARLY, DFNTools.SHIFT_LATE, DFNTools.SHIFT_VERY_LATE}) {
            shiftMAPpane.get(shift).setCollapsible(!shiftMAPDFN.get(shift).isEmpty());
            cpDFN.add(shiftMAPpane.get(shift));
            if (resetCollapseState) {
                try {
                    shiftMAPpane.get(shift).setCollapsed(shift != SYSCalendar.whatShiftIs(new Date()));
                } catch (PropertyVetoException e) {
                    OPDE.debug(e);
                }
            }
        }
        cpDFN.addExpansion();
    }

    private CollapsiblePane createCP4Shift(Byte shift) {
        String title = GUITools.getLocalizedMessages(DFNTools.SHIFT_TEXT)[shift];
        final CollapsiblePane mainPane = new CollapsiblePane(title);
        mainPane.setSlidingDirection(SwingConstants.SOUTH);
        mainPane.setBackground(SYSCalendar.getBGSHIFT(shift));
        mainPane.setForeground(SYSCalendar.getFGSHIFT(shift));
        mainPane.setOpaque(false);

        if (!shiftMAPDFN.get(shift).isEmpty()) {
            NursingProcess currentNP = null;
            CollapsiblePane npPane = null;
            JPanel npPanel = null;
            JPanel shiftOuterPanel = new JPanel();
            shiftOuterPanel.setLayout(new VerticalLayout());
            for (DFN dfn : shiftMAPDFN.get(shift)) {
//                OPDE.debug(bhp.getPrescription().getVerid());
//                OPDE.debug(currentPrescription != null ? currentPrescription.getVerid() : "null");
                if (currentNP == null || dfn.getNursingProcess().getPlanID().longValue() != currentNP.getPlanID().longValue()) {
                    if (currentNP != null) {
                        npPane.setContentPane(npPanel);
                        shiftOuterPanel.add(npPane);
                    }
                    currentNP = dfn.getNursingProcess();
                    npPanel = new JPanel();
                    npPanel.setLayout(new VerticalLayout());
//                    npPanel.setBackground(dfn.getBG());
                    npPane = new CollapsiblePane(SYSTools.toHTMLForScreen("<html>" + (currentNP.isAbgesetzt() ? "<s>" : "") + currentNP.getStichwort() + (currentNP.isAbgesetzt() ? "</s>" : "") + " (" + currentNP.getKategorie().getBezeichnung() + ")" + "</html>"));
                    npPane.setCollapsible(false);
                    npPane.setBackground(SYSCalendar.getBGSHIFT(shift).darker()); // a little darker
                    npPane.setForeground(SYSCalendar.getFGSHIFT(shift));
                    npPane.setOpaque(false);
                }

                npPane.setContentPane(npPanel);
                shiftOuterPanel.add(npPane);

                dfnCollapsiblePaneHashMap.put(dfn, createCP4(dfn));
                npPanel.add(dfnCollapsiblePaneHashMap.get(dfn));
            }
            mainPane.setContentPane(shiftOuterPanel);
            mainPane.setCollapsible(true);
        } else {
            mainPane.setContentPane(new JPanel());
            mainPane.setCollapsible(false);
        }

        return mainPane;
    }

    private CollapsiblePane createCP4(Byte shift) {
        if (shift == DFNTools.SHIFT_ON_DEMAND) {
            return createCP4OnDemand();
        } else {
            return createCP4Shift(shift);
        }
    }

    private CollapsiblePane createCP4OnDemand() {
        /***
         *                          _        ____ ____  _  _
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _|
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_|
         *
         */
        String title = OPDE.lang.getString(internalClassID + ".ondemand");

        final CollapsiblePane npPane = new CollapsiblePane(title);
        npPane.setSlidingDirection(SwingConstants.SOUTH);
        npPane.setBackground(SYSCalendar.getBGSHIFT(DFNTools.SHIFT_ON_DEMAND));
        npPane.setForeground(SYSCalendar.getFGSHIFT(DFNTools.SHIFT_ON_DEMAND));
        npPane.setOpaque(false);

        JPanel npPanel = new JPanel();
        npPanel.setLayout(new VerticalLayout());

        if (shiftMAPDFN.containsKey(DFNTools.SHIFT_ON_DEMAND)) {
            for (DFN dfn : shiftMAPDFN.get(DFNTools.SHIFT_ON_DEMAND)) {
                npPanel.setBackground(dfn.getBG());
                dfnCollapsiblePaneHashMap.put(dfn, createCP4(dfn));
                npPanel.add(dfnCollapsiblePaneHashMap.get(dfn));
            }
            npPane.setContentPane(npPanel);
            npPane.setCollapsible(true);
        } else {
            npPane.setContentPane(npPanel);
            npPane.setCollapsible(false);
        }

        return npPane;
    }

    private CollapsiblePane createCP4(final DFN dfn) {
        final CollapsiblePane dfnPane = new CollapsiblePane();
//        String fg = SYSConst.html_grey50;
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
        JideButton btnDFN = GUITools.createHyperlinkButton("<html><font size=+1>" +
                SYSTools.left(dfn.getIntervention().getBezeichnung(), MAX_TEXT_LENGTH) +
                DFNTools.getScheduleText(dfn, " [", "]") +
                ", " + dfn.getMinutes() + " " + OPDE.lang.getString("misc.msg.Minute(s)") + (dfn.getUser() != null ? ", <I>" + dfn.getUser().getUKennung() + "</I>" : "") +
                "</font></html>", DFNTools.getIcon(dfn), null);

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
            btnApply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (dfn.getStatus() == DFNTools.STATE_DONE) {
                        return;
                    }

                    if (DFNTools.isChangeable(dfn)) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();

                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                            DFN myDFN = em.merge(dfn);
                            em.lock(myDFN, LockModeType.OPTIMISTIC);

                            myDFN.setStatus(DFNTools.STATE_DONE);
                            myDFN.setUser(em.merge(OPDE.getLogin().getUser()));
                            myDFN.setIst(new Date());
                            myDFN.setiZeit(SYSCalendar.whatTimeIDIs(new Date()));
                            myDFN.setMdate(new Date());


                            dfnCollapsiblePaneHashMap.put(myDFN, createCP4(myDFN));
                            int position = shiftMAPDFN.get(myDFN.getShift()).indexOf(myDFN);
                            shiftMAPDFN.get(myDFN.getShift()).remove(position);
                            shiftMAPDFN.get(myDFN.getShift()).add(position, myDFN);

                            em.getTransaction().commit();

                            shiftMAPpane.put(myDFN.getShift(), createCP4(myDFN.getShift()));
                            buildPanel(false);

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
                    if (dfn.getStatus() == DFNTools.STATE_REFUSED) {
                        return;
                    }

                    if (DFNTools.isChangeable(dfn)) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();

                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                            DFN myDFN = em.merge(dfn);
                            em.lock(myDFN, LockModeType.OPTIMISTIC);

                            myDFN.setStatus(DFNTools.STATE_REFUSED);
                            myDFN.setUser(em.merge(OPDE.getLogin().getUser()));
                            myDFN.setIst(new Date());
                            myDFN.setiZeit(SYSCalendar.whatTimeIDIs(new Date()));
                            myDFN.setMdate(new Date());

                            dfnCollapsiblePaneHashMap.put(myDFN, createCP4(myDFN));
                            int position = shiftMAPDFN.get(myDFN.getShift()).indexOf(myDFN);
                            shiftMAPDFN.get(myDFN.getShift()).remove(position);
                            shiftMAPDFN.get(myDFN.getShift()).add(position, myDFN);


                            em.getTransaction().commit();
                            shiftMAPpane.put(myDFN.getShift(), createCP4(myDFN.getShift()));
                            buildPanel(false);
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

                    } else {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
                    }
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
                    if (dfn.getStatus() == DFNTools.STATE_OPEN) {
                        return;
                    }

                    if (DFNTools.isChangeable(dfn)) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();

                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                            DFN myDFN = em.merge(dfn);
                            em.lock(myDFN, LockModeType.OPTIMISTIC);

                            // on demand DFNs are deleted if they not wanted anymore
                            if (myDFN.isOnDemand()) {
                                em.remove(myDFN);
                                dfnCollapsiblePaneHashMap.remove(myDFN);
                                shiftMAPDFN.get(myDFN.getShift()).remove(myDFN);
                            } else {
                                // the normal DFNs (those assigned to a NursingProcess) are reset to the OPEN state.
                                myDFN.setStatus(DFNTools.STATE_OPEN);
                                myDFN.setUser(null);
                                myDFN.setIst(null);
                                myDFN.setiZeit(null);
                                myDFN.setMdate(new Date());
                                dfnCollapsiblePaneHashMap.put(myDFN, createCP4(myDFN));
                                int position = shiftMAPDFN.get(myDFN.getShift()).indexOf(myDFN);
                                shiftMAPDFN.get(myDFN.getShift()).remove(position);
                                shiftMAPDFN.get(myDFN.getShift()).add(position, myDFN);

                            }

                            em.getTransaction().commit();
                            shiftMAPpane.put(myDFN.getShift(), createCP4(myDFN.getShift()));
                            buildPanel(false);
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

                    } else {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".notchangeable")));
                    }
                }
            });
            titlePanelright.add(btnEmpty);


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
//            btnCancel.setToolTipText(OPDE.lang.getString(internalClassID + ".btneval.tooltip"));
            btnMinutes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (!DFNTools.isChangeable(dfn)) {
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
                                DFN myDFN = em.merge(dfn);
                                em.lock(myDFN, LockModeType.OPTIMISTIC);

                                myDFN.setMinutes(new BigDecimal((Integer) o));
                                myDFN.setUser(em.merge(OPDE.getLogin().getUser()));
                                myDFN.setMdate(new Date());

                                dfnCollapsiblePaneHashMap.put(myDFN, createCP4(myDFN));
                                int position = shiftMAPDFN.get(myDFN.getShift()).indexOf(myDFN);
                                shiftMAPDFN.get(myDFN.getShift()).remove(position);
                                shiftMAPDFN.get(myDFN.getShift()).add(position, myDFN);

                                em.getTransaction().commit();
                                shiftMAPpane.put(myDFN.getShift(), createCP4(myDFN.getShift()));
                                buildPanel(false);
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

                    menu.show(btnMinutes, 0,btnMinutes.getHeight());
                }
            });
            btnMinutes.setEnabled(dfn.getStatus() != DFNTools.STATE_OPEN);
            titlePanelright.add(btnMinutes);


            /***
             *      _     _         ___        __
             *     | |__ | |_ _ __ |_ _|_ __  / _| ___
             *     | '_ \| __| '_ \ | || '_ \| |_ / _ \
             *     | |_) | |_| | | || || | | |  _| (_) |
             *     |_.__/ \__|_| |_|___|_| |_|_|  \___/
             *
             */
            final JButton btnInfo = new JButton(SYSConst.icon22info);
            final JidePopup popupInfo = new JidePopup();
            btnInfo.setPressedIcon(SYSConst.icon22infoPressed);
            btnInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnInfo.setContentAreaFilled(false);
            btnInfo.setBorder(null);
            final JTextPane txt = new JTextPane();
            txt.setContentType("text/html");
            txt.setEditable(false);

            popupInfo.setMovable(false);
            popupInfo.getContentPane().setLayout(new BoxLayout(popupInfo.getContentPane(), BoxLayout.LINE_AXIS));
            popupInfo.getContentPane().add(new JScrollPane(txt));
            popupInfo.removeExcludedComponent(txt);
            popupInfo.setDefaultFocusComponent(txt);

//            btnCancel.setToolTipText(OPDE.lang.getString(internalClassID + ".btneval.tooltip"));
            btnInfo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    popupInfo.setOwner(btnInfo);
                    txt.setText(SYSTools.toHTMLForScreen(dfn.getInterventionSchedule().getBemerkung()));
                    GUITools.showPopup(popupInfo, SwingConstants.CENTER);
                }
            });

            btnInfo.setEnabled(!dfn.isOnDemand() && !SYSTools.catchNull(dfn.getInterventionSchedule().getBemerkung()).isEmpty());
            titlePanelright.add(btnInfo);
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
//        OPDE.debug("Dimension: " + dfnPane.getPreferredSize());

        /***
         *       ___ ___  _  _ _____ ___ _  _ _____
         *      / __/ _ \| \| |_   _| __| \| |_   _|
         *     | (_| (_) | .` | | | | _|| .` | | |
         *      \___\___/|_|\_| |_| |___|_|\_| |_|
         *
         */

        dfnPane.setBackground(dfn.getBG());
        dfnPane.setForeground(dfn.getFG());
        try {
            dfnPane.setCollapsed(true);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }
        dfnPane.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                JTextPane contentPane = new JTextPane();
                contentPane.setContentType("text/html");
                contentPane.setEditable(false);
                contentPane.setText(SYSTools.toHTML(NursingProcessTools.getAsHTML(dfn.getNursingProcess(), false)));
                dfnPane.setContentPane(contentPane);
            }
        });
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

//        String[] strs = GUITools.getLocalizedMessages(new String[]{"misc.msg.everything", internalClassID + ".shift.veryearly", internalClassID + ".shift.early", internalClassID + ".shift.late", internalClassID + ".shift.verylate"});

//        cmbSchicht = new JComboBox(new DefaultComboBoxModel(strs));
//        cmbSchicht.setFont(new Font("Arial", Font.PLAIN, 14));
//        cmbSchicht.setSelectedIndex((int) SYSCalendar.whatShiftIs(new Date()) + 1);
//        cmbSchicht.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent itemEvent) {
//                if (!initPhase) {
//                    reloadDisplay();
//                }
//            }
//        });
//        list.add(cmbSchicht);

        jdcDatum = new JDateChooser(new Date());
        jdcDatum.setFont(new Font("Arial", Font.PLAIN, 14));

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
                    final JidePopup popup = new JidePopup();
                    popup.setMovable(false);
                    PnlSelectIntervention pnl = new PnlSelectIntervention(new Closure() {
                        @Override
                        public void execute(Object o) {
                            popup.hidePopup();
                            if (o != null) {
                                Object[] objects = (Object[]) o;
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);

                                    for (Object obj : objects) {
                                        Intervention intervention = em.merge((Intervention) obj);
                                        DFN dfn = em.merge(new DFN(resident, intervention));
                                        dfnCollapsiblePaneHashMap.put(dfn, createCP4(dfn));
                                        shiftMAPDFN.get(dfn.getShift()).add(dfn);
                                    }

                                    em.getTransaction().commit();
                                    shiftMAPpane.put(DFNTools.SHIFT_ON_DEMAND, createCP4(DFNTools.SHIFT_ON_DEMAND));
                                    buildPanel(false);
                                    try {
                                        shiftMAPpane.get(DFNTools.SHIFT_ON_DEMAND).setCollapsed(false);
                                    } catch (PropertyVetoException e) {
                                        OPDE.debug(e);
                                    }
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
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                    popup.getContentPane().add(pnl);
                    popup.setOwner(btnAdd);
                    popup.removeExcludedComponent(pnl);
                    popup.setDefaultFocusComponent(pnl);
                    GUITools.showPopup(popup, SwingConstants.EAST);
                }
            });
            list.add(btnAdd);

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspDFN;
    private CollapsiblePanes cpDFN;
    // End of variables declaration//GEN-END:variables
}
