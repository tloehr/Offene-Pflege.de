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
package op.care.bhp;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.jidesoft.utils.ColorUtils;
import com.toedter.calendar.JDateChooser;
import entity.info.Resident;
import entity.nursingprocess.DFNTools;
import entity.prescription.*;
import op.OPDE;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class PnlBHP extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.bhp";
    private Resident resident;
    private boolean initPhase;

    private HashMap<BHP, CollapsiblePane> mapBHP2Pane;
    private HashMap<Byte, ArrayList<BHP>> mapShift2BHP;
    private HashMap<Byte, CollapsiblePane> mapShift2Pane;
    private HashMap<Prescription, MedStock> mapPrescription2Stock;
    private int MAX_TEXT_LENGTH = 65;

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
    public String getInternalClassID() {
        return internalClassID;
    }

    @Override
    public void switchResident(Resident bewohner) {
        this.resident = bewohner;
        GUITools.setBWDisplay(resident);

        initPhase = true;
        jdcDatum.setMinSelectableDate(DFNTools.getMinDatum(bewohner));
        jdcDatum.setDate(new Date());
        jdcDatum.setMaxSelectableDate(new Date());
        initPhase = false;

        reloadDisplay();
    }

    private void initPanel() {
        mapBHP2Pane = new HashMap<BHP, CollapsiblePane>();
        mapShift2Pane = new HashMap<Byte, CollapsiblePane>();
        mapShift2BHP = new HashMap<Byte, ArrayList<BHP>>();
        mapPrescription2Stock = new HashMap<Prescription, MedStock>();
        prepareSearchArea();
    }


    @Override
    public void cleanup() {
        jdcDatum.cleanup();
        mapShift2BHP.clear();
        mapShift2Pane.clear();
        mapPrescription2Stock.clear();
        mapBHP2Pane.clear();
        SYSTools.unregisterListeners(this);
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

        final boolean withworker = true;
        if (withworker) {

            OPDE.getMainframe().setBlocked(true);
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

            mapPrescription2Stock.clear();
            mapBHP2Pane.clear();
            if (mapShift2BHP != null) {
                for (Byte key : mapShift2BHP.keySet()) {
                    mapShift2BHP.get(key).clear();
                }
                mapShift2BHP.clear();
            }
            if (mapShift2Pane != null) {
                for (Byte key : mapShift2Pane.keySet()) {
                    mapShift2Pane.get(key).removeAll();
                }
                mapShift2Pane.clear();
            }

            SwingWorker worker = new SwingWorker() {

                @Override
                protected Object doInBackground() throws Exception {

                    int progress = 0;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));
                    for (BHP bhp : BHPTools.getBHPs(resident, jdcDatum.getDate())) {
                        if (!mapShift2BHP.containsKey(bhp.getShift())) {
                            mapShift2BHP.put(bhp.getShift(), new ArrayList<BHP>());
                        }
                        mapShift2BHP.get(bhp.getShift()).add(bhp);
                    }

                    if (!mapShift2BHP.containsKey(BHPTools.SHIFT_ON_DEMAND)) {
                        mapShift2BHP.put(BHPTools.SHIFT_ON_DEMAND, new ArrayList<BHP>());
                    }
                    mapShift2BHP.get(BHPTools.SHIFT_ON_DEMAND).addAll(BHPTools.getBHPsOnDemand(resident, jdcDatum.getDate()));

                    for (Byte shift : new Byte[]{BHPTools.SHIFT_ON_DEMAND, BHPTools.SHIFT_VERY_EARLY, BHPTools.SHIFT_EARLY, BHPTools.SHIFT_LATE, BHPTools.SHIFT_VERY_LATE}) {
                        mapShift2Pane.put(shift, createCP4(shift));
                        try {
                            mapShift2Pane.get(shift).setCollapsed(shift == BHPTools.SHIFT_ON_DEMAND || shift != SYSCalendar.whatShiftIs(new Date()));
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

            mapBHP2Pane.clear();
            if (mapShift2BHP != null) {
                for (Byte key : mapShift2BHP.keySet()) {
                    mapShift2BHP.get(key).clear();
                }
                mapShift2BHP.clear();
            }
            if (mapShift2Pane != null) {
                for (Byte key : mapShift2Pane.keySet()) {
                    mapShift2Pane.get(key).removeAll();
                }
                mapShift2Pane.clear();
            }

            for (BHP bhp : BHPTools.getBHPs(resident, jdcDatum.getDate())) {
                if (!mapShift2BHP.containsKey(bhp.getShift())) {
                    mapShift2BHP.put(bhp.getShift(), new ArrayList<BHP>());
                }
                mapShift2BHP.get(bhp.getShift()).add(bhp);
            }

            if (!mapShift2BHP.containsKey(BHPTools.SHIFT_ON_DEMAND)) {
                mapShift2BHP.put(BHPTools.SHIFT_ON_DEMAND, new ArrayList<BHP>());
            }
            mapShift2BHP.get(BHPTools.SHIFT_ON_DEMAND).addAll(BHPTools.getBHPsOnDemand(resident, jdcDatum.getDate()));

            for (Byte shift : new Byte[]{BHPTools.SHIFT_ON_DEMAND, BHPTools.SHIFT_VERY_EARLY, BHPTools.SHIFT_EARLY, BHPTools.SHIFT_LATE, BHPTools.SHIFT_VERY_LATE}) {
                mapShift2Pane.put(shift, createCP4(shift));
                try {
                    mapShift2Pane.get(shift).setCollapsed(shift == BHPTools.SHIFT_ON_DEMAND || shift != SYSCalendar.whatShiftIs(new Date()));
                } catch (PropertyVetoException e) {
                    OPDE.debug(e);
                }
            }

            buildPanel(true);

        }
        initPhase = false;
    }

    private void buildPanel(boolean resetCollapseState) {
        cpBHP.removeAll();
        cpBHP.setLayout(new JideBoxLayout(cpBHP, JideBoxLayout.Y_AXIS));
        cpBHP.add(mapShift2Pane.get(BHPTools.SHIFT_ON_DEMAND));
        for (Byte shift : new Byte[]{BHPTools.SHIFT_VERY_EARLY, BHPTools.SHIFT_EARLY, BHPTools.SHIFT_LATE, BHPTools.SHIFT_VERY_LATE}) {
            cpBHP.add(mapShift2Pane.get(shift));
            if (resetCollapseState) {
                try {
                    mapShift2Pane.get(shift).setCollapsed(shift != SYSCalendar.whatShiftIs(new Date()));
                } catch (PropertyVetoException e) {
                    OPDE.debug(e);
                }
            }
        }
        cpBHP.addExpansion();
    }

    private CollapsiblePane createCP4(Byte shift) {
        if (shift == BHPTools.SHIFT_ON_DEMAND) {
            return createCP4OnDemand();
        } else {
            return createCP4Shift(shift);
        }
    }

    private CollapsiblePane createCP4OnDemand() {
        /***
         *                          _        ____ ____  _  _    ___        ____                                 _
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |  / _ \ _ __ |  _ \  ___ _ __ ___   __ _ _ __   __| |
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_| | | | '_ \| | | |/ _ \ '_ ` _ \ / _` | '_ \ / _` |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _| |_| | | | | |_| |  __/ | | | | | (_| | | | | (_| |
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_|  \___/|_| |_|____/ \___|_| |_| |_|\__,_|_| |_|\__,_|
         *
         */
        String title = "<html><font size=+1><b>" + OPDE.lang.getString(internalClassID + ".ondemand") + "</b></font></html>";

        final CollapsiblePane mainPane = new CollapsiblePane(title);
        mainPane.setSlidingDirection(SwingConstants.SOUTH);
        mainPane.setBackground(SYSCalendar.getBGSHIFT(BHPTools.SHIFT_ON_DEMAND));
        mainPane.setForeground(SYSCalendar.getFGSHIFT(BHPTools.SHIFT_ON_DEMAND));
        mainPane.setOpaque(false);


        if (!mapShift2BHP.get(BHPTools.SHIFT_ON_DEMAND).isEmpty()) {
            Prescription currentPrescription = null;
            CollapsiblePane sitPane = null;
            JPanel sitPanel = null;
            JPanel sitOuterPanel = new JPanel();
            sitOuterPanel.setLayout(new VerticalLayout());
            for (BHP bhp : mapShift2BHP.get(BHPTools.SHIFT_ON_DEMAND)) {
//                OPDE.debug(bhp.getPrescription().getVerid());
//                OPDE.debug(currentPrescription != null ? currentPrescription.getVerid() : "null");
                if (currentPrescription == null || bhp.getPrescription().getID() != currentPrescription.getID()) {
                    if (currentPrescription != null) {
                        sitPane.setContentPane(sitPanel);
//                        prPane.add(sitPane);
                        sitOuterPanel.add(sitPane);
                    }
                    currentPrescription = bhp.getPrescription();
                    sitPanel = new JPanel();
                    sitPanel.setLayout(new VerticalLayout());
                    sitPanel.setBackground(bhp.getBG());
                    sitPane = new CollapsiblePane(SYSTools.toHTMLForScreen("<b>" + currentPrescription.getSituation().getText()) + "</b>");
                    sitPane.setSlidingDirection(SwingConstants.SOUTH);
                    sitPane.setBackground(ColorUtils.getDerivedColor(SYSCalendar.getBGSHIFT(BHPTools.SHIFT_ON_DEMAND), 0.4f)); // a little darker
                    sitPane.setForeground(Color.BLACK);//SYSCalendar.getFGSHIFT(BHPTools.SHIFT_ON_DEMAND));
                    sitPane.setOpaque(false);
                }

                sitPane.setContentPane(sitPanel);
                sitOuterPanel.add(sitPane);

                mapBHP2Pane.put(bhp, createCP4(bhp));
                sitPanel.add(mapBHP2Pane.get(bhp));
            }
            mainPane.setContentPane(sitOuterPanel);
            mainPane.setCollapsible(true);
        } else {
            mainPane.setContentPane(new JPanel());
            mainPane.setCollapsible(false);
        }

        return mainPane;
    }

    private CollapsiblePane createCP4Shift(final Byte shift) {
        /***
         *                          _        ____ ____  _  _
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _|
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_|
         *
         */
        String title = "<html><font size=+1><b>" + GUITools.getLocalizedMessages(DFNTools.SHIFT_TEXT)[shift] + "</b></font></html>";

        final CollapsiblePane prPane = new CollapsiblePane(title);
        prPane.setSlidingDirection(SwingConstants.SOUTH);
        prPane.setBackground(SYSCalendar.getBGSHIFT(shift));
        prPane.setForeground(SYSCalendar.getFGSHIFT(shift));
        prPane.setOpaque(false);

        JPanel prPanel = new JPanel();
        prPanel.setLayout(new VerticalLayout());

        if (mapShift2BHP.containsKey(shift)) {
            for (BHP bhp : mapShift2BHP.get(shift)) {
                prPanel.setBackground(bhp.getBG());
                mapBHP2Pane.put(bhp, createCP4(bhp));
                prPanel.add(mapBHP2Pane.get(bhp));
            }
            prPane.setContentPane(prPanel);
            prPane.setCollapsible(true);
        } else {
            prPane.setContentPane(prPanel);
            prPane.setCollapsible(false);
        }

        return prPane;
    }

    private CollapsiblePane createCP4(final BHP bhp) {
        final CollapsiblePane bhpPane = new CollapsiblePane();

        JPanel titlePanelleft = new JPanel();
        titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));

//        JideButton btnBHP = GUITools.createHyperlinkButton(, BHPTools.getIcon(bhp), null);

        // it is expensive to search for stocks. hence the cache map


        MedStock stock = mapPrescription2Stock.get(bhp.getPrescription());
        if (bhp.hasMed() && stock == null) {
            stock = MedStockTools.getStockInUse(TradeFormTools.getInventory4TradeForm(resident, bhp.getTradeForm()));
            mapPrescription2Stock.put(bhp.getPrescription(), stock);
        }


        String title = "<html><font size=+1>" +
                SYSTools.left(PrescriptionTools.getPrescriptionAsShortText(bhp.getPrescriptionSchedule().getPrescription()), MAX_TEXT_LENGTH) +
                (bhp.hasMed() ? ", <b>" + SYSTools.getAsHTML(bhp.getDose()) +
                        " " + DosageFormTools.getUsageText(bhp.getPrescription().getTradeForm().getDosageForm()) + "</b>" : "") +
                BHPTools.getScheduleText(bhp, ", ", "") +
                (bhp.getUser() != null ? ", <i>" + bhp.getUser().getUID() + "</i>" : "") +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    bhpPane.setCollapsed(!bhpPane.isCollapsed());
                } catch (PropertyVetoException e) {
                    OPDE.error(e);
                }
            }
        });


        JLabel icon1 = new JLabel(BHPTools.getIcon(bhp));
        icon1.setOpaque(false);
        JLabel icon2 = new JLabel(BHPTools.getWarningIcon(bhp, stock));
        icon2.setOpaque(false);


        cptitle.getAdditionalIconPanel().add(icon1);
        cptitle.getAdditionalIconPanel().add(icon2);

        if (bhp.getPrescription().isClosed()) {
            JLabel icon3 = new JLabel(SYSConst.icon22stopSign);
            icon3.setOpaque(false);
            cptitle.getAdditionalIconPanel().add(icon3);
        }


        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            if (!bhp.getPrescription().isClosed()) {


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
                btnApply.setToolTipText(OPDE.lang.getString(internalClassID + ".btnApply.tooltip"));
                btnApply.setBorder(null);
                btnApply.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if (bhp.getStatus() != BHPTools.STATE_OPEN) {
                            return;
                        }

                        if (BHPTools.isChangeable(bhp)) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();

                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                BHP myBHP = em.merge(bhp);
                                em.lock(myBHP, LockModeType.OPTIMISTIC);
                                em.lock(myBHP.getPrescriptionSchedule(), LockModeType.OPTIMISTIC);
                                em.lock(myBHP.getPrescription(), LockModeType.OPTIMISTIC);

                                myBHP.setStatus(BHPTools.STATE_DONE);
                                myBHP.setUser(em.merge(OPDE.getLogin().getUser()));
                                myBHP.setIst(new Date());
                                myBHP.setiZeit(SYSCalendar.whatTimeIDIs(new Date()));
                                myBHP.setMDate(new Date());

                                // TODO: Calc Medi here
                                if (myBHP.hasMed()) {
                                    MedInventory inventory = TradeFormTools.getInventory4TradeForm(resident, myBHP.getTradeForm());
                                    MedInventoryTools.withdraw(em, em.merge(inventory), myBHP.getDose(), myBHP);
                                }

                                mapBHP2Pane.put(myBHP, createCP4(myBHP));
                                int position = mapShift2BHP.get(myBHP.getShift()).indexOf(myBHP);
                                mapShift2BHP.get(myBHP.getShift()).remove(position);
                                mapShift2BHP.get(myBHP.getShift()).add(position, myBHP);
                                if (myBHP.isOnDemand()) {
                                    Collections.sort(mapShift2BHP.get(myBHP.getShift()), BHPTools.getOnDemandComparator());
                                } else {
                                    Collections.sort(mapShift2BHP.get(myBHP.getShift()));
                                }
                                em.getTransaction().commit();

                                mapShift2Pane.put(bhp.getShift(), createCP4(bhp.getShift()));
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
                // TODO: Calc Medi here
                btnApply.setEnabled(bhp.isOpen() && (!bhp.hasMed() || mapPrescription2Stock.containsKey(bhp.getPrescription())));
                cptitle.getRight().add(btnApply);


                /***
                 *      _     _         ____       __
                 *     | |__ | |_ _ __ |  _ \ ___ / _|_   _ ___  ___
                 *     | '_ \| __| '_ \| |_) / _ \ |_| | | / __|/ _ \
                 *     | |_) | |_| | | |  _ <  __/  _| |_| \__ \  __/
                 *     |_.__/ \__|_| |_|_| \_\___|_|  \__,_|___/\___|
                 *
                 */
                final JButton btnRefuse = new JButton(SYSConst.icon22cancel);
                btnRefuse.setPressedIcon(SYSConst.icon22cancelPressed);
                btnRefuse.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnRefuse.setContentAreaFilled(false);
                btnRefuse.setBorder(null);
                btnRefuse.setToolTipText(SYSTools.toHTMLForScreen(OPDE.lang.getString(internalClassID + ".btnRefuse.tooltip")));
                btnRefuse.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if (bhp.getStatus() != BHPTools.STATE_OPEN) {
                            return;
                        }

                        if (BHPTools.isChangeable(bhp)) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();

                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                BHP myBHP = em.merge(bhp);
                                em.lock(myBHP, LockModeType.OPTIMISTIC);
                                em.lock(myBHP.getPrescriptionSchedule(), LockModeType.OPTIMISTIC);
                                em.lock(myBHP.getPrescription(), LockModeType.OPTIMISTIC);

                                myBHP.setStatus(BHPTools.STATE_REFUSED);
                                myBHP.setUser(em.merge(OPDE.getLogin().getUser()));
                                myBHP.setIst(new Date());
                                myBHP.setiZeit(SYSCalendar.whatTimeIDIs(new Date()));
                                myBHP.setMDate(new Date());

                                mapBHP2Pane.put(myBHP, createCP4(myBHP));
                                int position = mapShift2BHP.get(myBHP.getShift()).indexOf(myBHP);
                                mapShift2BHP.get(bhp.getShift()).remove(myBHP);
                                mapShift2BHP.get(bhp.getShift()).add(position, myBHP);
                                if (myBHP.isOnDemand()) {
                                    Collections.sort(mapShift2BHP.get(myBHP.getShift()), BHPTools.getOnDemandComparator());
                                } else {
                                    Collections.sort(mapShift2BHP.get(myBHP.getShift()));
                                }

                                em.getTransaction().commit();
                                mapShift2Pane.put(bhp.getShift(), createCP4(bhp.getShift()));
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
                btnRefuse.setEnabled(!bhp.isOnDemand() && bhp.isOpen());
                cptitle.getRight().add(btnRefuse);

                /***
                 *      _     _         ____       __                ____  _                       _
                 *     | |__ | |_ _ __ |  _ \ ___ / _|_   _ ___  ___|  _ \(_)___  ___ __ _ _ __ __| |
                 *     | '_ \| __| '_ \| |_) / _ \ |_| | | / __|/ _ \ | | | / __|/ __/ _` | '__/ _` |
                 *     | |_) | |_| | | |  _ <  __/  _| |_| \__ \  __/ |_| | \__ \ (_| (_| | | | (_| |
                 *     |_.__/ \__|_| |_|_| \_\___|_|  \__,_|___/\___|____/|_|___/\___\__,_|_|  \__,_|
                 *
                 */
                final JButton btnRefuseDiscard = new JButton(SYSConst.icon22deleteall);
                btnRefuseDiscard.setPressedIcon(SYSConst.icon22deleteallPressed);
                btnRefuseDiscard.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnRefuseDiscard.setContentAreaFilled(false);
                btnRefuseDiscard.setBorder(null);
                btnRefuseDiscard.setToolTipText(SYSTools.toHTMLForScreen(OPDE.lang.getString(internalClassID + ".btnRefuseDiscard.tooltip")));
                btnRefuseDiscard.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if (bhp.getStatus() != BHPTools.STATE_OPEN) {
                            return;
                        }

                        if (BHPTools.isChangeable(bhp)) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();

                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                BHP myBHP = em.merge(bhp);
                                em.lock(myBHP, LockModeType.OPTIMISTIC);
                                em.lock(myBHP.getPrescriptionSchedule(), LockModeType.OPTIMISTIC);
                                em.lock(myBHP.getPrescription(), LockModeType.OPTIMISTIC);

                                myBHP.setStatus(BHPTools.STATE_REFUSED_DISCARDED);
                                myBHP.setUser(em.merge(OPDE.getLogin().getUser()));
                                myBHP.setIst(new Date());
                                myBHP.setiZeit(SYSCalendar.whatTimeIDIs(new Date()));
                                myBHP.setMDate(new Date());
                                // TODO: Calc Medi here
                                if (myBHP.hasMed()) {
                                    MedInventory inventory = TradeFormTools.getInventory4TradeForm(resident, myBHP.getTradeForm());
                                    if (inventory != null) {
                                        MedInventoryTools.withdraw(em, em.merge(inventory), myBHP.getDose(), myBHP);
                                    } else {
                                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".NoInventory"));
                                    }
                                }

                                mapBHP2Pane.put(myBHP, createCP4(myBHP));
                                int position = mapShift2BHP.get(myBHP.getShift()).indexOf(myBHP);
                                mapShift2BHP.get(bhp.getShift()).remove(myBHP);
                                mapShift2BHP.get(bhp.getShift()).add(position, myBHP);
                                if (myBHP.isOnDemand()) {
                                    Collections.sort(mapShift2BHP.get(myBHP.getShift()), BHPTools.getOnDemandComparator());
                                } else {
                                    Collections.sort(mapShift2BHP.get(myBHP.getShift()));
                                }

                                em.getTransaction().commit();
                                mapShift2Pane.put(bhp.getShift(), createCP4(bhp.getShift()));
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
                // TODO: Calc Medi here
                btnRefuseDiscard.setEnabled(!bhp.isOnDemand() && bhp.hasMed() && bhp.isOpen());
                cptitle.getRight().add(btnRefuseDiscard);


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
                btnEmpty.setToolTipText(OPDE.lang.getString(internalClassID + ".btnEmpty.tooltip"));
                btnEmpty.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if (bhp.getStatus() == BHPTools.STATE_OPEN) {
                            return;
                        }

                        if (BHPTools.isChangeable(bhp)) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();

                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                BHP myBHP = em.merge(bhp);
                                em.lock(myBHP, LockModeType.OPTIMISTIC);
                                em.lock(myBHP.getPrescriptionSchedule(), LockModeType.OPTIMISTIC);
                                em.lock(myBHP.getPrescription(), LockModeType.OPTIMISTIC);

                                // the normal BHPs (those assigned to a NursingProcess) are reset to the OPEN state.
                                // TXs are deleted
                                myBHP.setStatus(BHPTools.STATE_OPEN);
                                myBHP.setUser(null);
                                myBHP.setIst(null);
                                myBHP.setiZeit(null);
                                myBHP.setMDate(new Date());

                                // TODO: Calc Medi here
                                if (myBHP.getPrescription().hasMed()) {
                                    for (MedStockTransaction tx : myBHP.getStockTransaction()) {
                                        em.remove(tx);
                                    }
                                    myBHP.getStockTransaction().clear();
                                }

                                mapBHP2Pane.put(myBHP, createCP4(myBHP));
                                int position = mapShift2BHP.get(myBHP.getShift()).indexOf(myBHP);
                                mapShift2BHP.get(bhp.getShift()).remove(myBHP);
                                mapShift2BHP.get(bhp.getShift()).add(position, myBHP);
                                if (myBHP.isOnDemand()) {
                                    Collections.sort(mapShift2BHP.get(myBHP.getShift()), BHPTools.getOnDemandComparator());
                                } else {
                                    Collections.sort(mapShift2BHP.get(myBHP.getShift()));
                                }

                                em.getTransaction().commit();
                                mapShift2Pane.put(bhp.getShift(), createCP4(bhp.getShift()));
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
                btnEmpty.setEnabled(!bhp.isOpen());
                cptitle.getRight().add(btnEmpty);
            }


            /***
             *      _     _         ___        __
             *     | |__ | |_ _ __ |_ _|_ __  / _| ___
             *     | '_ \| __| '_ \ | || '_ \| |_ / _ \
             *     | |_) | |_| | | || || | | |  _| (_) |
             *     |_.__/ \__|_| |_|___|_| |_|_|  \___/
             *
             */
            final JButton btnInfo = new JButton(SYSConst.icon22info);

            btnInfo.setPressedIcon(SYSConst.icon22infoPressed);
            btnInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnInfo.setContentAreaFilled(false);
            btnInfo.setBorder(null);
            btnInfo.setToolTipText(OPDE.lang.getString(internalClassID + ".btnInfo.tooltip"));
            final JTextPane txt = new JTextPane();
            txt.setContentType("text/html");
            txt.setEditable(false);
            final JidePopup popupInfo = new JidePopup();
            popupInfo.setMovable(false);
            popupInfo.setContentPane(new JScrollPane(txt));
            popupInfo.removeExcludedComponent(txt);
            popupInfo.setDefaultFocusComponent(txt);


            btnInfo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    popupInfo.setOwner(btnInfo);
                    txt.setText(SYSTools.toHTML(SYSConst.html_div(bhp.getPrescription().getText())));
                    GUITools.showPopup(popupInfo, SwingConstants.SOUTH_WEST);
                }
            });

            btnInfo.setEnabled(!SYSTools.catchNull(bhp.getPrescription().getText()).isEmpty());
            cptitle.getRight().add(btnInfo);

        }

        bhpPane.setTitleLabelComponent(cptitle.getMain());
        bhpPane.setSlidingDirection(SwingConstants.SOUTH);

        final JTextPane contentPane = new JTextPane();
        contentPane.setEditable(false);
        contentPane.setContentType("text/html");
        bhpPane.setContentPane(contentPane);
        bhpPane.setBackground(bhp.getBG());
        bhpPane.setForeground(bhp.getFG());

        try {
            bhpPane.setCollapsed(true);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

        bhpPane.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                contentPane.setText(SYSTools.toHTML(PrescriptionTools.getPrescriptionAsHTML(bhp.getPrescription(), false, false, true, false)));
            }
        });

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

            //======== cpBHP ========
            {
                cpBHP.setLayout(new BoxLayout(cpBHP, BoxLayout.X_AXIS));
            }
            jspBHP.setViewportView(cpBHP);
        }
        add(jspBHP);
    }// </editor-fold>//GEN-END:initComponents


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

//        searchPanes.add(addCommands());
        GUITools.addAllComponents(searchPanes, addFilter());

        GUITools.addAllComponents(searchPanes, addKey());

        searchPanes.addExpansion();

    }

    private java.util.List<Component> addKey() {
        java.util.List<Component> list = new ArrayList<Component>();
        list.add(new JSeparator());
        list.add(new JLabel(OPDE.lang.getString("misc.msg.key")));
        list.add(new JLabel(OPDE.lang.getString(internalClassID + ".keydescription1"), SYSConst.icon22ledYellowOn, SwingConstants.LEADING));
        list.add(new JLabel(OPDE.lang.getString(internalClassID + ".keydescription2"), SYSConst.icon22ledRedOn, SwingConstants.LEADING));

        return list;
    }

    private java.util.List<Component> addFilter() {
        java.util.List<Component> list = new ArrayList<Component>();


//        cmbSchicht = new JComboBox(new DefaultComboBoxModel(GUITools.getLocalizedMessages(new String[]{"misc.msg.everything", internalClassID + ".shift.veryearly", internalClassID + ".shift.early", internalClassID + ".shift.late", internalClassID + ".shift.verylate"})));
//        cmbSchicht.setFont(new Font("Arial", Font.PLAIN, 14));
//        cmbSchicht.setSelectedIndex(SYSCalendar.ermittleSchicht() + 1);
//        cmbSchicht.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent itemEvent) {
//                if (!initPhase) {
//                    reloadDisplay();
//                }
//            }
//        });
//        labelPanel.add(cmbSchicht);

        jdcDatum = new JDateChooser(new Date());
        jdcDatum.setFont(new Font("Arial", Font.PLAIN, 18));
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

//        panelFilter.setContentPane(labelPanel);

        return list;
    }

//


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspBHP;
    private CollapsiblePanes cpBHP;
    // End of variables declaration//GEN-END:variables
}
