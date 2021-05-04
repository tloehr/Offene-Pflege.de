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
package de.offene_pflege.op.care.dfn;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.files.SYSFilesTools;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.entity.nursingprocess.DFN;
import de.offene_pflege.entity.nursingprocess.DFNTools;
import de.offene_pflege.entity.nursingprocess.Intervention;
import de.offene_pflege.entity.nursingprocess.NursingProcessTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.gui.interfaces.DefaultCPTitle;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.care.nursingprocess.PnlSelectIntervention;
import de.offene_pflege.op.system.InternalClassACL;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.NursingRecordsPanel;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.util.List;
import java.util.*;

/**
 * @author root
 */
public class PnlDFN extends NursingRecordsPanel {


    Resident resident;

    private boolean initPhase;

    private final JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JDateChooser jdcDate;

    private Map<DFN, CollapsiblePane> mapDFN2Pane;
    private Map<Byte, ArrayList<DFN>> mapShift2DFN;
    private Map<Byte, CollapsiblePane> mapShift2Pane;
    private final int MAX_TEXT_LENGTH = 65;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspDFN;
    private CollapsiblePanes cpDFN;

    public PnlDFN(Resident resident, JScrollPane jspSearch) {
        super("nursingrecords.dfn");
        initComponents();
        this.jspSearch = jspSearch;
        initPanel();
        switchResident(resident);
    }

    @Override
    public String getInternalClassID() {
        return internalClassID;
    }

    private void initPanel() {
        mapShift2Pane = Collections.synchronizedMap(new HashMap<Byte, CollapsiblePane>());
        mapShift2DFN = Collections.synchronizedMap(new HashMap<Byte, ArrayList<DFN>>());
        for (Byte shift : new Byte[]{SYSCalendar.SHIFT_ON_DEMAND, SYSCalendar.SHIFT_VERY_EARLY, SYSCalendar.SHIFT_EARLY, SYSCalendar.SHIFT_LATE, SYSCalendar.SHIFT_VERY_LATE}) {
            mapShift2DFN.put(shift, new ArrayList<DFN>());
        }
        mapDFN2Pane = Collections.synchronizedMap(new HashMap<DFN, CollapsiblePane>());
        prepareSearchArea();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        jdcDate.cleanup();
        cpDFN.removeAll();
        synchronized (mapDFN2Pane) {
            SYSTools.clear(mapDFN2Pane);
        }
        synchronized (mapShift2DFN) {
            SYSTools.clear(mapShift2DFN);
        }
        synchronized (mapShift2Pane) {
            SYSTools.clear(mapShift2Pane);
        }
        SYSTools.unregisterListeners(this);
    }

    @Override
    public void reload() {
        GUITools.setResidentDisplay(resident);
        reloadDisplay();
    }

    @Override
    public void switchResident(Resident res) {
        this.resident = EntityTools.find(Resident.class, res.getId());
        GUITools.setResidentDisplay(resident);

        initPhase = true;
        jdcDate.setMinSelectableDate(DFNTools.getMinDatum(resident));
        jdcDate.setMaxSelectableDate(new Date());
        jdcDate.setDate(new Date());
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
        synchronized (mapDFN2Pane) {
            SYSTools.clear(mapDFN2Pane);
        }

        synchronized (mapShift2DFN) {
            for (Byte key : mapShift2DFN.keySet()) {
                mapShift2DFN.get(key).clear();
            }
        }

        synchronized (mapShift2Pane) {
            for (Byte key : mapShift2Pane.keySet()) {
                mapShift2Pane.get(key).removeAll();
            }
            mapShift2Pane.clear();
        }

        final boolean withworker = true;
        if (withworker) {

            OPDE.getMainframe().setBlocked(true);
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

            cpDFN.removeAll();

            SwingWorker worker = new SwingWorker() {

                @Override
                protected Object doInBackground() throws Exception {

                    int progress = 0;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, 100));

                    ArrayList<DFN> listDFNs = DFNTools.getDFNs(resident, jdcDate.getDate());

                    synchronized (mapShift2DFN) {
                        for (DFN dfn : listDFNs) {
                            mapShift2DFN.get(dfn.getShift()).add(dfn);
                        }
                    }


                    // now build the CollapsiblePanes
                    for (Byte shift : new Byte[]{SYSCalendar.SHIFT_ON_DEMAND, SYSCalendar.SHIFT_VERY_EARLY, SYSCalendar.SHIFT_EARLY, SYSCalendar.SHIFT_LATE, SYSCalendar.SHIFT_VERY_LATE}) {
                        CollapsiblePane cp = createCP4(shift);
                        synchronized (mapShift2Pane) {
                            mapShift2Pane.put(shift, cp);
                            try {
                                mapShift2Pane.get(shift).setCollapsed(shift == SYSCalendar.SHIFT_ON_DEMAND || shift != SYSCalendar.whatShiftIs(new Date()));
                            } catch (PropertyVetoException e) {
                                OPDE.debug(e);
                            }
                            progress += 20;
                            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, 100));
                        }
                    }
                    return null;
                }

                @Override
                protected void done() {
                    buildPanel(true);
                    initPhase = false;
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(DateFormat.getDateInstance().format(jdcDate.getDate())));
                }
            };
            worker.execute();

        } else {
//           buildPanel(true);
        }
        initPhase = false;
    }

    private void buildPanel(boolean resetCollapseState) {

        cpDFN.removeAll();
        cpDFN.setLayout(new JideBoxLayout(cpDFN, JideBoxLayout.Y_AXIS));

        for (Byte shift : new Byte[]{SYSCalendar.SHIFT_ON_DEMAND, SYSCalendar.SHIFT_VERY_EARLY, SYSCalendar.SHIFT_EARLY, SYSCalendar.SHIFT_LATE, SYSCalendar.SHIFT_VERY_LATE}) {

            boolean isEmpty = true;
            synchronized (mapShift2DFN) {

                // todo: to track down the NPEs.
                if (mapShift2DFN.get(shift) == null) {
                    reload();
                    break;
                }

                isEmpty = mapShift2DFN.get(shift).isEmpty();
            }
            synchronized (mapShift2Pane) {
                mapShift2Pane.get(shift).setCollapsible(!isEmpty);
                cpDFN.add(mapShift2Pane.get(shift));
                if (resetCollapseState) {
                    try {
                        mapShift2Pane.get(shift).setCollapsed(shift != SYSCalendar.SHIFT_ON_DEMAND && shift != SYSCalendar.whatShiftIs(new Date()));
                    } catch (PropertyVetoException e) {
                        OPDE.debug(e);
                    }
                }
            }
        }

        cpDFN.addExpansion();
    }

    private CollapsiblePane createCP4Shift(Byte shift) {
        String title = "<html><font size=+1><b>" + GUITools.getLocalizedMessages(SYSCalendar.SHIFT_TEXT)[shift] + "</b></font></html>";
        final CollapsiblePane mainPane = new CollapsiblePane(title);
        mainPane.setSlidingDirection(SwingConstants.SOUTH);
        mainPane.setBackground(SYSCalendar.getBGSHIFT(shift));
        mainPane.setForeground(SYSCalendar.getFGSHIFT(shift));
        mainPane.setOpaque(false);

        JPanel npPanel = new JPanel();
        npPanel.setLayout(new VerticalLayout());

        boolean containsKey = false;
        synchronized (mapShift2DFN) {
            containsKey = mapShift2DFN.containsKey(shift);
        }

        if (containsKey) {
            if (shift == SYSCalendar.SHIFT_EARLY) {

                final CollapsiblePane morning = new CollapsiblePane("<html><font size=+1>" + SYSTools.xx("misc.msg.morning.long") + "</font></html>", null);
                morning.setSlidingDirection(SwingConstants.SOUTH);
                morning.setBackground(SYSCalendar.getBGSHIFT(shift).darker());
                morning.setForeground(Color.white);
                morning.setOpaque(false);
                JPanel pnlMorning = new JPanel();
                pnlMorning.setLayout(new VerticalLayout());
                morning.setContentPane(pnlMorning);

                final CollapsiblePane noon = new CollapsiblePane("<html><font size=+1>" + SYSTools.xx("misc.msg.noon.long") + "</font></html>", null);
                noon.setSlidingDirection(SwingConstants.SOUTH);
                noon.setBackground(SYSCalendar.getBGSHIFT(shift).darker());
                noon.setForeground(Color.white);
                noon.setOpaque(false);
                JPanel pnlNoon = new JPanel();
                pnlNoon.setLayout(new VerticalLayout());
                noon.setContentPane(pnlNoon);

                final CollapsiblePane clock = new CollapsiblePane("<html><font size=+1>" + SYSTools.xx("misc.msg.Time.long") + "</font></html>", null);
                clock.setSlidingDirection(SwingConstants.SOUTH);
                clock.setBackground(SYSCalendar.getBGSHIFT(shift).darker());
                clock.setForeground(Color.white);
                clock.setOpaque(false);
                JPanel pnlClock = new JPanel();
                pnlClock.setLayout(new VerticalLayout());
                clock.setContentPane(pnlClock);

                ArrayList<DFN> listDFN = null;
                synchronized (mapShift2DFN) {
                    listDFN = mapShift2DFN.get(shift);
                }

                for (DFN dfn : listDFN) {
                    npPanel.setBackground(SYSCalendar.getBGItem(dfn.getShift()));
                    CollapsiblePane cp1 = createCP4(dfn);
                    synchronized (mapDFN2Pane) {
                        mapDFN2Pane.put(dfn, cp1);
                        if (dfn.getSollZeit() == SYSCalendar.BYTE_MORNING) {
                            pnlMorning.add(mapDFN2Pane.get(dfn));
                        } else if (dfn.getSollZeit() == SYSCalendar.BYTE_NOON) {
                            pnlNoon.add(mapDFN2Pane.get(dfn));
                        } else {
                            pnlClock.add(mapDFN2Pane.get(dfn));
                        }
                    }
                }

                if (pnlClock.getComponentCount() > 0) {
                    npPanel.add(clock);
                }
                if (pnlMorning.getComponentCount() > 0) {
                    npPanel.add(morning);
                }
                if (pnlNoon.getComponentCount() > 0) {
                    npPanel.add(noon);
                }

            } else if (shift == SYSCalendar.SHIFT_LATE) {
                final CollapsiblePane afternoon = new CollapsiblePane("<html><font size=+1>" + SYSTools.xx("misc.msg.afternoon.long") + "</font></html>", null);
                afternoon.setSlidingDirection(SwingConstants.SOUTH);
                afternoon.setBackground(SYSCalendar.getBGSHIFT(shift).darker());
                afternoon.setForeground(Color.white);
                afternoon.setOpaque(false);
                JPanel pnlAfternoon = new JPanel();
                pnlAfternoon.setLayout(new VerticalLayout());
                afternoon.setContentPane(pnlAfternoon);

                final CollapsiblePane evening = new CollapsiblePane("<html><font size=+1>" + SYSTools.xx("misc.msg.evening.long") + "</font></html>", null);
                evening.setSlidingDirection(SwingConstants.SOUTH);
                evening.setBackground(SYSCalendar.getBGSHIFT(shift).darker());
                evening.setForeground(Color.white);
                evening.setOpaque(false);
                JPanel pnlEvening = new JPanel();
                pnlEvening.setLayout(new VerticalLayout());
                evening.setContentPane(pnlEvening);

                final CollapsiblePane clock = new CollapsiblePane("<html><font size=+1>" + SYSTools.xx("misc.msg.Time.long") + "</font></html>", null);
                clock.setSlidingDirection(SwingConstants.SOUTH);
                clock.setBackground(SYSCalendar.getBGSHIFT(shift).darker());
                clock.setForeground(Color.white);
                clock.setOpaque(false);
                JPanel pnlClock = new JPanel();
                pnlClock.setLayout(new VerticalLayout());
                clock.setContentPane(pnlClock);

                ArrayList<DFN> listDFN = null;
                synchronized (mapShift2DFN) {
                    listDFN = mapShift2DFN.get(shift);
                }

                for (DFN dfn : listDFN) {
                    npPanel.setBackground(SYSCalendar.getBGItem(dfn.getShift()));
                    CollapsiblePane cp1 = createCP4(dfn);
                    synchronized (mapDFN2Pane) {
                        mapDFN2Pane.put(dfn, cp1);
                        if (dfn.getSollZeit() == SYSCalendar.BYTE_AFTERNOON) {
                            pnlAfternoon.add(mapDFN2Pane.get(dfn));
                        } else if (dfn.getSollZeit() == SYSCalendar.BYTE_EVENING) {
                            pnlEvening.add(mapDFN2Pane.get(dfn));
                        } else {
                            pnlClock.add(mapDFN2Pane.get(dfn));
                        }
                    }
                }

                if (pnlClock.getComponentCount() > 0) {
                    npPanel.add(clock);
                }
                if (pnlAfternoon.getComponentCount() > 0) {
                    npPanel.add(afternoon);
                }
                if (pnlEvening.getComponentCount() > 0) {
                    npPanel.add(evening);
                }


            } else {
                ArrayList<DFN> listDFN = null;
                synchronized (mapShift2DFN) {
                    listDFN = mapShift2DFN.get(shift);
                }
                for (DFN dfn : listDFN) {
                    npPanel.setBackground(SYSCalendar.getBGItem(dfn.getShift()));
                    CollapsiblePane cp1 = createCP4(dfn);
                    synchronized (mapDFN2Pane) {
                        mapDFN2Pane.put(dfn, cp1);
                        npPanel.add(mapDFN2Pane.get(dfn));
                    }
                }
            }
            mainPane.setContentPane(npPanel);
            mainPane.setCollapsible(true);
        } else {
            mainPane.setContentPane(npPanel);
            mainPane.setCollapsible(false);
        }


        return mainPane;
    }

    private CollapsiblePane createCP4(Byte shift) {
        if (shift == SYSCalendar.SHIFT_ON_DEMAND) {
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
        String title = "<html><font size=+1><b>" + SYSTools.xx("msg.shift.ondemand") + "</b></font></html>";

        final CollapsiblePane npPane = new CollapsiblePane(title);
        npPane.setSlidingDirection(SwingConstants.SOUTH);
        npPane.setBackground(SYSCalendar.getBGSHIFT(SYSCalendar.SHIFT_ON_DEMAND));
        npPane.setForeground(SYSCalendar.getFGSHIFT(SYSCalendar.SHIFT_ON_DEMAND));
        npPane.setOpaque(false);

        JPanel npPanel = new JPanel();
        npPanel.setLayout(new VerticalLayout());

        ArrayList<DFN> list = null;
        synchronized (mapShift2DFN) {
            list = mapShift2DFN.containsKey(SYSCalendar.SHIFT_ON_DEMAND) ? mapShift2DFN.get(SYSCalendar.SHIFT_ON_DEMAND) : null;
        }

        if (list != null && !list.isEmpty()) {
            for (DFN dfn : list) {
                npPanel.setBackground(SYSCalendar.getBGItem(dfn.getShift()));
                CollapsiblePane cp = createCP4(dfn);
                synchronized (mapDFN2Pane) {
                    mapDFN2Pane.put(dfn, cp);
                    npPanel.add(mapDFN2Pane.get(dfn));
                }
            }
            npPane.setContentPane(npPanel);
            npPane.setCollapsible(true);
        } else {
            npPane.setContentPane(npPanel);
            npPane.setCollapsible(false);
        }

        return npPane;
    }

    private List<Component> addKey() {
        List<Component> list = new ArrayList<Component>();
        list.add(new JSeparator());
        list.add(new JLabel(SYSTools.xx("misc.msg.key")));
        list.add(new JLabel(SYSTools.xx("nursingrecords.dfn.keydescription1"), SYSConst.icon22ledBlueOn, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.dfn.keydescription2"), SYSConst.icon22ledOrangeOn, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.dfn.keydescription3"), SYSConst.icon22ledPurpleOn, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.dfn.keydescription4"), SYSConst.icon22stopSign, SwingConstants.LEADING));
        return list;
    }

    private CollapsiblePane createCP4(final DFN dfn) {
        final CollapsiblePane dfnPane = new CollapsiblePane();

        ActionListener applyActionListener = actionEvent -> {
            if (dfn.getState() == DFNTools.STATE_DONE) {
                return;
            }
            if (!dfn.isOnDemand() && dfn.getNursingProcess().isClosed()) {
                return;
            }

            if (DFNTools.isChangeable(dfn)) {
                EntityManager em = OPDE.createEM();
                try {
                    em.getTransaction().begin();

                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                    DFN myDFN = em.merge(dfn);
                    em.lock(myDFN, LockModeType.OPTIMISTIC);
                    if (!myDFN.isOnDemand()) {
                        em.lock(myDFN.getNursingProcess(), LockModeType.OPTIMISTIC);
                    }

                    myDFN.setState(DFNTools.STATE_DONE);
                    myDFN.setUser(em.merge(OPDE.getLogin().getUser()));
                    myDFN.setIst(new Date());
                    myDFN.setiZeit(SYSCalendar.whatTimeIDIs(new Date()));
                    myDFN.setMdate(new Date());

                    em.getTransaction().commit();

                    CollapsiblePane cp1 = createCP4(myDFN);

                    synchronized (mapDFN2Pane) {
                        mapDFN2Pane.put(myDFN, cp1);
                    }
                    synchronized (mapShift2DFN) {
                        int position = mapShift2DFN.get(myDFN.getShift()).indexOf(myDFN);
                        mapShift2DFN.get(myDFN.getShift()).remove(position);
                        mapShift2DFN.get(myDFN.getShift()).add(position, myDFN);
                    }

                    CollapsiblePane cp2 = createCP4(myDFN.getShift());
                    synchronized (mapShift2Pane) {
                        mapShift2Pane.put(myDFN.getShift(), cp2);
                    }

                    buildPanel(false);

                } catch (OptimisticLockException ole) {
                    OPDE.warn(ole);
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

            } else {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.dfn.notchangeable")));
            }
        };

        String title = "<html><font size=+1>" +
//                (dfn.isFloating() ? (dfn.isActive() ? "(!) " : "(OK) ") : "") +
                SYSTools.left(dfn.getIntervention().getBezeichnung(), MAX_TEXT_LENGTH) +
                DFNTools.getScheduleText(dfn, " [", "]") +
                (dfn.getUser() != null ? ", <i>" + SYSTools.anonymizeUser(dfn.getUser()) + "</i>" : "") +
                "</font></html>";

        // minuten brauchen wir nicht mehr
        //+ dfn.getMinutes() + " " + SYSTools.xx("misc.msg.Minute(s)")

        DefaultCPTitle cptitle = new DefaultCPTitle(title, OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID) ? applyActionListener : null);
        dfnPane.setCollapseOnTitleClick(false);
//        cptitle.getTitleButton().setIcon(DFNTools.getIcon(dfn));
        JLabel icon1 = new JLabel(DFNTools.getIcon(dfn));
        icon1.setOpaque(false);
        JLabel icon2 = new JLabel(DFNTools.getFloatingIcon(dfn));
        icon2.setOpaque(false);
        cptitle.getAdditionalIconPanel().add(icon1);
        cptitle.getAdditionalIconPanel().add(icon2);

        if (dfn.isFloating()) {
            cptitle.getButton().setToolTipText(SYSTools.xx("nursingrecords.dfn.enforced.tooltip") + ": " + DateFormat.getDateInstance().format(dfn.getStDatum()));
        }

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID) && (dfn.isOnDemand() || !dfn.getNursingProcess().isClosed())) {
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
            btnApply.addActionListener(applyActionListener);
            btnApply.setEnabled(!dfn.isOnDemand() && dfn.isOpen());
            cptitle.getRight().add(btnApply);
//            JPanel spacer = new JPanel();
//            spacer.setOpaque(false);
//            cptitle.getRight().add(spacer);
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
//            btnCancel.setToolTipText(SYSTools.xx("nursingrecords.dfn.btneval.tooltip"));
            btnCancel.addActionListener(actionEvent -> {
                if (dfn.getState() == DFNTools.STATE_REFUSED) {
                    return;
                }

                if (DFNTools.isChangeable(dfn)) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();

                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                        DFN myDFN = em.merge(dfn);
                        em.lock(myDFN, LockModeType.OPTIMISTIC);
                        if (!myDFN.isOnDemand()) {
                            em.lock(myDFN.getNursingProcess(), LockModeType.OPTIMISTIC);
                        }

                        myDFN.setState(DFNTools.STATE_REFUSED);
                        myDFN.setUser(em.merge(OPDE.getLogin().getUser()));
                        myDFN.setIst(new Date());
                        myDFN.setiZeit(SYSCalendar.whatTimeIDIs(new Date()));
                        myDFN.setMdate(new Date());

                        em.getTransaction().commit();

                        CollapsiblePane cp1 = createCP4(myDFN);
                        synchronized (mapDFN2Pane) {
                            mapDFN2Pane.put(myDFN, cp1);
                        }
                        synchronized (mapShift2DFN) {
                            int position = mapShift2DFN.get(myDFN.getShift()).indexOf(myDFN);
                            mapShift2DFN.get(myDFN.getShift()).remove(position);
                            mapShift2DFN.get(myDFN.getShift()).add(position, myDFN);
                        }
                        CollapsiblePane cp2 = createCP4(myDFN.getShift());
                        synchronized (mapShift2Pane) {
                            mapShift2Pane.put(myDFN.getShift(), cp2);
                        }
                        buildPanel(false);
                    } catch (OptimisticLockException ole) {
                        OPDE.warn(ole);
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

                } else {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.dfn.notchangeable")));
                }
            });
            btnCancel.setEnabled(!dfn.isOnDemand() && dfn.isOpen());
            cptitle.getRight().add(btnCancel);

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
//            btnCancel.setToolTipText(SYSTools.xx("nursingrecords.dfn.btneval.tooltip"));
            btnEmpty.addActionListener(actionEvent -> {
                if (dfn.getState() == DFNTools.STATE_OPEN) {
                    return;
                }

                if (DFNTools.isChangeable(dfn)) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();

                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                        DFN myDFN = em.merge(dfn);
                        em.lock(myDFN, LockModeType.OPTIMISTIC);
                        if (!myDFN.isOnDemand()) {
                            em.lock(myDFN.getNursingProcess(), LockModeType.OPTIMISTIC);
                        }

                        // on demand DFNs are deleted if they not wanted anymore
                        if (myDFN.isOnDemand()) {
                            em.remove(myDFN);
                            synchronized (mapDFN2Pane) {
                                mapDFN2Pane.remove(myDFN);
                            }
                            synchronized (mapShift2DFN) {
                                mapShift2DFN.get(myDFN.getShift()).remove(myDFN);
                            }
                        } else {
                            // the normal DFNs (those assigned to a NursingProcess) are reset to the OPEN state.
                            myDFN.setState(DFNTools.STATE_OPEN);
                            myDFN.setUser(null);
                            myDFN.setIst(null);
                            myDFN.setiZeit(null);
                            myDFN.setMdate(new Date());
                            CollapsiblePane cp1 = createCP4(myDFN);
                            synchronized (mapDFN2Pane) {
                                mapDFN2Pane.put(myDFN, cp1);
                            }
                            synchronized (mapShift2DFN) {
                                int position = mapShift2DFN.get(myDFN.getShift()).indexOf(myDFN);
                                mapShift2DFN.get(myDFN.getShift()).remove(position);
                                mapShift2DFN.get(myDFN.getShift()).add(position, myDFN);
                            }
                        }
                        em.getTransaction().commit();

                        CollapsiblePane cp2 = createCP4(myDFN.getShift());
                        synchronized (mapShift2Pane) {
                            mapShift2Pane.put(myDFN.getShift(), cp2);
                        }
                        buildPanel(false);
                    } catch (OptimisticLockException ole) {
                        OPDE.warn(ole);
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

                } else {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.dfn.notchangeable")));
                }
            });
            btnEmpty.setEnabled(!dfn.isOpen());
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

        btnInfo.addActionListener(actionEvent -> {
            popupInfo.setOwner(btnInfo);
            txt.setText(SYSTools.toHTML(SYSConst.html_div(dfn.getInterventionSchedule().getBemerkung())));
            GUITools.showPopup(popupInfo, SwingConstants.WEST);
        });

        btnInfo.setEnabled(!dfn.isOnDemand() && !SYSTools.catchNull(dfn.getInterventionSchedule().getBemerkung()).isEmpty());
        cptitle.getRight().add(btnInfo);

        dfnPane.setTitleLabelComponent(cptitle.getMain());

        dfnPane.setBackground(SYSCalendar.getBGItem(dfn.getShift()));
        dfnPane.setForeground(SYSCalendar.getFGItem(dfn.getShift()));
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
                contentPane.setText(SYSTools.toHTML(NursingProcessTools.getAsHTML(dfn.getNursingProcess(), false, true, false, false)));
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

        jdcDate = new JDateChooser(new Date());
        jdcDate.setFont(new Font("Arial", Font.PLAIN, 14));

        jdcDate.setBackground(Color.WHITE);

        list.add(jdcDate);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new HorizontalLayout(5));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        final JButton homeButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_start.png")));
        homeButton.addActionListener(actionEvent -> jdcDate.setDate(jdcDate.getMinSelectableDate()));
        homeButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_start_pressed.png")));
        homeButton.setBorder(null);
        homeButton.setBorderPainted(false);
        homeButton.setOpaque(false);
        homeButton.setContentAreaFilled(false);
        homeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final JButton backButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_back.png")));
        backButton.addActionListener(actionEvent -> {
            DateMidnight current = new DateMidnight(jdcDate.getDate());
            DateMidnight min = new DateMidnight(jdcDate.getMinSelectableDate());
            if (current.equals(min)) {
                return;
            }
            jdcDate.setDate(SYSCalendar.addDate(jdcDate.getDate(), -1));
        });
        backButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_back_pressed.png")));
        backButton.setBorder(null);
        backButton.setBorderPainted(false);
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        final JButton fwdButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_play.png")));
        fwdButton.addActionListener(actionEvent -> {
            DateMidnight current = new DateMidnight(jdcDate.getDate());
            if (current.equals(new DateMidnight())) {
                return;
            }
            jdcDate.setDate(SYSCalendar.addDate(jdcDate.getDate(), 1));
        });
        fwdButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_play_pressed.png")));
        fwdButton.setBorder(null);
        fwdButton.setBorderPainted(false);
        fwdButton.setOpaque(false);
        fwdButton.setContentAreaFilled(false);
        fwdButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final JButton endButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_end.png")));
        endButton.addActionListener(actionEvent -> jdcDate.setDate(new Date()));
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

        jdcDate.addPropertyChangeListener(evt -> {
            if (initPhase) {
                return;
            }
            if (evt.getPropertyName().equals("date")) {
                reloadDisplay();
            }
        });


        return list;
    }

    private List<Component> addCommands() {

        List<Component> list = new ArrayList<Component>();

        /***
         *      _     _            _       _     _
         *     | |__ | |_ _ __    / \   __| | __| |
         *     | '_ \| __| '_ \  / _ \ / _` |/ _` |
         *     | |_) | |_| | | |/ ___ \ (_| | (_| |
         *     |_.__/ \__|_| |_/_/   \_\__,_|\__,_|
         *
         */
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {

            final JideButton btnAdd = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.dfn.btnadd"), SYSConst.icon22add, null);
            btnAdd.addActionListener(actionEvent -> {
                if (!ResidentTools.isActive(resident)) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                    return;
                }

                final JidePopup popup = new JidePopup();
                popup.setMovable(false);
                PnlSelectIntervention pnl = new PnlSelectIntervention(o -> {
                    popup.hidePopup();
                    if (o != null) {

                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);

                            for (Intervention inv : (List<Intervention>) o) {
                                Intervention intervention = em.merge(inv);
                                DFN dfn = em.merge(new DFN(resident, intervention));

                                DateTime newDateTime = new LocalDate(jdcDate.getDate()).toDateTime(new LocalTime());
                                dfn.setSoll(newDateTime.toDate());
                                dfn.setIst(newDateTime.toDate());

                                CollapsiblePane cp1 = createCP4(dfn);
                                synchronized (mapDFN2Pane) {
                                    mapDFN2Pane.put(dfn, cp1);
                                }
                                synchronized (mapShift2DFN) {
                                    mapShift2DFN.get(dfn.getShift()).add(dfn);
                                }
                            }

                            em.getTransaction().commit();

                            CollapsiblePane cp2 = createCP4(SYSCalendar.SHIFT_ON_DEMAND);
                            synchronized (mapShift2Pane) {
                                mapShift2Pane.put(SYSCalendar.SHIFT_ON_DEMAND, cp2);
                            }
                            buildPanel(false);
                            try {
                                synchronized (mapShift2Pane) {
                                    mapShift2Pane.get(SYSCalendar.SHIFT_ON_DEMAND).setCollapsed(false);
                                }
                            } catch (PropertyVetoException e) {
                                OPDE.debug(e);
                            }

                        } catch (OptimisticLockException ole) {
                            OPDE.warn(ole);
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
                });
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                popup.getContentPane().add(pnl);
                popup.setOwner(btnAdd);
                popup.removeExcludedComponent(pnl);
                popup.setDefaultFocusComponent(pnl);
                GUITools.showPopup(popup, SwingConstants.NORTH);
            });
            list.add(btnAdd);

        }

        final JideButton printPrescription = GUITools.createHyperlinkButton("nursingrecords.dfn.print", SYSConst.icon22print2, actionEvent -> {

            String html = "";

            synchronized (mapShift2DFN) {
                html += "<h1 id=\"fonth1\" >" + ResidentTools.getFullName(resident) + "</h1>";
                html += SYSConst.html_h2(SYSTools.xx("nursingrecords.bhp") + ": " + SYSConst.html_bold(DateFormat.getDateInstance().format(jdcDate.getDate())));

                for (Byte shift : new Byte[]{SYSCalendar.SHIFT_ON_DEMAND, SYSCalendar.SHIFT_VERY_EARLY, SYSCalendar.SHIFT_EARLY, SYSCalendar.SHIFT_LATE, SYSCalendar.SHIFT_VERY_LATE}) {
                    html += DFNTools.getDFNsAsHTMLtable(mapShift2DFN.get(shift));
                }
            }

            SYSFilesTools.print(html, true);
        });
        list.add(printPrescription);
        return list;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the PrinterForm Editor.
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
    // End of variables declaration//GEN-END:variables
}
