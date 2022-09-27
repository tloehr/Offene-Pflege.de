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
package de.offene_pflege.op.care.supervisor;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import de.offene_pflege.entity.building.Homes;
import de.offene_pflege.services.HomesService;
import de.offene_pflege.entity.files.SYSFilesTools;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.entity.reports.*;
import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.gui.interfaces.DefaultCPTitle;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.system.InternalClassACL;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.NursingRecordsPanel;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import lombok.extern.log4j.Log4j2;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.MutableInterval;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

@Log4j2
public class PnlHandover extends NursingRecordsPanel {


    private JXSearchField txtSearch;
    private JComboBox yearCombo;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    private Map<String, CollapsiblePane> cpMap;
    private Map<String, ArrayList<Handovers>> cacheHO;
    private Map<String, ArrayList<NReport>> cacheNR;
    private HashMap<LocalDate, String> hollidays;
    private JToggleButton tbResidentFirst;
    private Comparator myComparator;

    Format monthFormatter = new SimpleDateFormat("MMMM yyyy");
    Format dayFormat = new SimpleDateFormat("EEEE, dd.MM.yyyy");

    int checkWeeksbackForNewReports = 4;


    /**
     * Creates new form PnlHandover
     */
    public PnlHandover(JScrollPane jspSearch) {
        super("nursingrecords.handover");
        this.jspSearch = jspSearch;
        initComponents();
        initPanel();

        reloadDisplay();
    }

    @Override
    public void reload() {
        reloadDisplay();
    }

    private List<Component> addKey() {
        List<Component> list = new ArrayList<Component>();
        list.add(new JSeparator());
        list.add(new JLabel(SYSTools.xx("misc.msg.key")));
        list.add(new JLabel(SYSTools.xx("nursingrecords.handover.keydescription1"), SYSConst.icon22ledRedOn, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.handover.keydescription2"), SYSConst.icon22ledGreenOn, SwingConstants.LEADING));
        return list;
    }

    private void initPanel() {

        myComparator = (Comparator<NReport>) (o1, o2) -> {
            if (!tbResidentFirst.isSelected()) {
                return o1.getPit().compareTo(o2.getPit());
            } else {
                int comp = o1.getResident().getId().compareTo(o2.getResident().getId());
                if (comp == 0) {
                    comp = o1.getPit().compareTo(o2.getPit());
                }
                return comp;
            }
        };

        cpMap = Collections.synchronizedMap(new HashMap<String, CollapsiblePane>());
        cacheHO = Collections.synchronizedMap(new HashMap<String, ArrayList<Handovers>>());
        cacheNR = Collections.synchronizedMap(new HashMap<String, ArrayList<NReport>>());
        OPDE.getDisplayManager().setMainMessage(SYSTools.xx(internalClassID));
        prepareSearchArea();
    }

    @Override
    public void switchResident(Resident resident) {
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the PrinterForm Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspHandover = new JScrollPane();
        cpsHandover = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspHandover ========
        {

            //======== cpsHandover ========
            {
                cpsHandover.setLayout(new BoxLayout(cpsHandover, BoxLayout.X_AXIS));
            }
            jspHandover.setViewportView(cpsHandover);
        }
        add(jspHandover);
    }// </editor-fold>//GEN-END:initComponents

    private void prepareSearchArea() {

        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(2));
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(SYSTools.xx(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            log.error(e);
        }

        GUITools.addAllComponents(mypanel, addCommands());
        GUITools.addAllComponents(mypanel, addFilters());
        GUITools.addAllComponents(mypanel, addKey());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }

    @Override
    public void cleanup() {
        super.cleanup();
        cpsHandover.removeAll();

        synchronized (cpMap) {
            SYSTools.clear(cpMap);
        }

        synchronized (cacheHO) {
            SYSTools.clear(cacheHO);
        }
        synchronized (cacheNR) {
            SYSTools.clear(cacheNR);
        }
        SYSTools.clear(hollidays);

    }

    @Override
    public String getInternalClassID() {
        return internalClassID;
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

        synchronized (cpMap) {
            SYSTools.clear(cpMap);
        }
        synchronized (cacheHO) {
            SYSTools.clear(cacheHO);
        }
        synchronized (cacheNR) {
            SYSTools.clear(cacheNR);
        }


        MutableInterval minmax = NReportTools.getMinMax();
        if (minmax != null) {
            hollidays = SYSCalendar.getHolidays(minmax.getStart().getYear(), minmax.getEnd().getYear());
            LocalDate start = SYSCalendar.bom(minmax.getStart()).toLocalDate();
            LocalDate end = new LocalDate();
            for (int year = end.getYear(); year >= start.getYear(); year--) {
                createCP4Year(year, start, end);
            }
        }

        expandDay(new LocalDate());
        buildPanel();
    }


    private void expandDay(LocalDate day) {
        final String keyYear = Integer.toString(day.getYear()) + ".year";
        if (cpMap.containsKey(keyYear) && cpMap.get(keyYear).isCollapsed()) {
            try {
                cpMap.get(keyYear).setCollapsed(false);
            } catch (PropertyVetoException e) {
                // bah!
            }
        }
        final String keyMonth = monthFormatter.format(day.toDate()) + ".month";
        if (cpMap.containsKey(keyMonth) && cpMap.get(keyMonth).isCollapsed()) {
            try {
                cpMap.get(keyMonth).setCollapsed(false);
            } catch (PropertyVetoException e) {
                // bah!
            }
        }
        final String keyDay = DateFormat.getDateInstance().format(day.toDate());
        if (cpMap.containsKey(keyDay) && cpMap.get(keyDay).isCollapsed()) {
            try {
                cpMap.get(keyDay).setCollapsed(false);
            } catch (PropertyVetoException e) {
                // bah!
            }
        }
    }

    private CollapsiblePane createCP4Year(final int year, LocalDate min, LocalDate max) {
        /***
         *                          _        ____ ____     __             __   _______    _    ____
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \   / _| ___  _ __  \ \ / / ____|  / \  |  _ \
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | | |_ / _ \| '__|  \ V /|  _|   / _ \ | |_) |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/  |  _| (_) | |      | | | |___ / ___ \|  _ <
         *      \___|_|  \___|\__,_|\__\___|\____|_|     |_|  \___/|_|      |_| |_____/_/   \_\_| \_\
         *
         */

        final LocalDate start = new LocalDate(year, 1, 1).isBefore(min.dayOfMonth().withMinimumValue()) ? min.dayOfMonth().withMinimumValue() : new LocalDate(year, 1, 1);
        final LocalDate end = new LocalDate(year, 12, 31).isAfter(max.dayOfMonth().withMaximumValue()) ? max.dayOfMonth().withMaximumValue() : new LocalDate(year, 12, 31);

        final String keyYear = Integer.toString(year) + ".year";
        synchronized (cpMap) {
            if (!cpMap.containsKey(keyYear)) {
                cpMap.put(keyYear, new CollapsiblePane());
                try {
                    cpMap.get(keyYear).setCollapsed(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        }

        final CollapsiblePane cpYear = cpMap.get(keyYear);


        String title = "<html><font size=+1>" +
                "<b>" + Integer.toString(year) + "</b>" +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            try {
                cpYear.setCollapsed(!cpYear.isCollapsed());
            } catch (PropertyVetoException pve) {
                // BAH!
            }
        });


        cpYear.setTitleLabelComponent(cptitle.getMain());
        cpYear.setSlidingDirection(SwingConstants.SOUTH);
        cpYear.setBackground(SYSConst.orange1[SYSConst.medium3]);
        cpYear.setOpaque(true);

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
                for (LocalDate month = end; month.compareTo(start) >= 0; month = month.minusMonths(1)) {
                    pnlContent.add(createCP4Month(month));
                }

                cpYear.setContentPane(pnlContent);

            }
        });

        if (!cpYear.isCollapsed()) {
            JPanel pnlContent = new JPanel(new VerticalLayout());

            for (LocalDate month = end; month.compareTo(start) >= 0; month = month.minusMonths(1)) {
                pnlContent.add(createCP4Month(month));
            }

            cpYear.setContentPane(pnlContent);
            cpYear.setOpaque(false);
        }

        cpYear.setHorizontalAlignment(SwingConstants.LEADING);
        cpYear.setOpaque(false);

        return cpYear;
    }

    private CollapsiblePane createCP4Month(final LocalDate month) {
        /***
         *                          _        ____ ____     __                      __  __  ___  _   _ _____ _   _
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \   / _| ___  _ __    __ _  |  \/  |/ _ \| \ | |_   _| | | |
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | | |_ / _ \| '__|  / _` | | |\/| | | | |  \| | | | | |_| |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/  |  _| (_) | |    | (_| | | |  | | |_| | |\  | | | |  _  |
         *      \___|_|  \___|\__,_|\__\___|\____|_|     |_|  \___/|_|     \__,_| |_|  |_|\___/|_| \_| |_| |_| |_|
         *
         */
        final String key = monthFormatter.format(month.toDate()) + ".month";
        synchronized (cpMap) {
            if (!cpMap.containsKey(key)) {
                cpMap.put(key, new CollapsiblePane());
                try {
                    cpMap.get(key).setCollapsed(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        final CollapsiblePane cpMonth = cpMap.get(key);

        String title = "<html><font size=+1><b>" +
                monthFormatter.format(month.toDate()) +
                "</b>" +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            try {
                cpMonth.setCollapsed(!cpMonth.isCollapsed());
            } catch (PropertyVetoException pve) {
                // BAH!
            }
        });

        cpMonth.setTitleLabelComponent(cptitle.getMain());
        cpMonth.setSlidingDirection(SwingConstants.SOUTH);
        cpMonth.setBackground(SYSConst.orange1[SYSConst.medium2]);
        cpMonth.setOpaque(true);
        cpMonth.setHorizontalAlignment(SwingConstants.LEADING);

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

    private JPanel createContentPanel4Month(LocalDate month) {
        /***
         *                      _             _      __              __  __  ___  _   _ _____ _   _
         *       ___ ___  _ __ | |_ ___ _ __ | |_   / _| ___  _ __  |  \/  |/ _ \| \ | |_   _| | | |
         *      / __/ _ \| '_ \| __/ _ \ '_ \| __| | |_ / _ \| '__| | |\/| | | | |  \| | | | | |_| |
         *     | (_| (_) | | | | ||  __/ | | | |_  |  _| (_) | |    | |  | | |_| | |\  | | | |  _  |
         *      \___\___/|_| |_|\__\___|_| |_|\__| |_|  \___/|_|    |_|  |_|\___/|_| \_| |_| |_| |_|
         *
         */
        JPanel pnlMonth = new JPanel(new VerticalLayout());

        pnlMonth.setOpaque(false);

        LocalDate now = new LocalDate();

        boolean sameMonth = now.dayOfMonth().withMaximumValue().equals(month.dayOfMonth().withMaximumValue());

        final LocalDate start = sameMonth ? now : month.dayOfMonth().withMaximumValue();
        final LocalDate end = month.dayOfMonth().withMinimumValue();

        for (LocalDate day = start; end.compareTo(day) <= 0; day = day.minusDays(1)) {
            pnlMonth.add(createCP4Day(day));
        }

        return pnlMonth;
    }

    private CollapsiblePane createCP4Day(final LocalDate day) {
        final String key = DateFormat.getDateInstance().format(day.toDate());
        synchronized (cpMap) {
            if (!cpMap.containsKey(key)) {
                cpMap.put(key, new CollapsiblePane());
                try {
                    cpMap.get(key).setCollapsed(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }
            }
        }
        final CollapsiblePane cpDay = cpMap.get(key);
        if (hollidays == null) {
            hollidays = SYSCalendar.getHolidays(day.getYear(), day.getYear());
        }
        String titleDay = "<html><font size=+1>" +
                dayFormat.format(day.toDate()) +
                SYSTools.catchNull(hollidays.get(day), " (", ")") +
                "</font></html>";
        final DefaultCPTitle titleCPDay = new DefaultCPTitle(titleDay, e -> {
            try {
                cpDay.setCollapsed(!cpDay.isCollapsed());
            } catch (PropertyVetoException pve) {
                // BAH!
            }
        });

        final JButton btnAcknowledge = new JButton(SYSConst.icon163ledGreenOn);
        btnAcknowledge.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnAcknowledge.setToolTipText(SYSTools.xx("nursingrecords.handover.tooltips.btnAcknowledge"));
        btnAcknowledge.addActionListener(actionEvent -> {
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();

                synchronized (cacheHO) {
                    ArrayList<Handovers> listHO = new ArrayList<Handovers>(cacheHO.get(key));
                    for (final Handovers ho : listHO) {
                        if (!Handover2UserTools.containsUser(em, ho, OPDE.getLogin().getUser())) {
                            Handovers myHO = em.merge(ho);
                            Handover2User connObj = em.merge(new Handover2User(myHO, em.merge(OPDE.getLogin().getUser())));
                            myHO.getUsersAcknowledged().add(connObj);
                        }
                    }
                }

                synchronized (cacheNR) {
                    ArrayList<NReport> listNR = new ArrayList<NReport>(cacheNR.get(key));
                    for (final NReport nreport : listNR) {
                        if (!NR2UserTools.containsUser(em, nreport, OPDE.getLogin().getUser())) {
                            NReport myNR = em.merge(nreport);
                            NR2User connObj = em.merge(new NR2User(myNR, em.merge(OPDE.getLogin().getUser())));
                            myNR.getUsersAcknowledged().add(connObj);
                        }
                    }
                }

                em.getTransaction().commit();
                createCP4Day(day);
                buildPanel();
            } catch (OptimisticLockException ole) {
                log.warn(ole);
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
        titleCPDay.getRight().add(btnAcknowledge);

        cpDay.setTitleLabelComponent(titleCPDay.getMain());
        cpDay.setSlidingDirection(SwingConstants.SOUTH);

        if (hollidays.containsKey(day)) {
            cpDay.setBackground(SYSConst.red1[SYSConst.medium1]);
        } else if (day.getDayOfWeek() == DateTimeConstants.SATURDAY || day.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            cpDay.setBackground(SYSConst.red1[SYSConst.light3]);
        } else {
            cpDay.setBackground(SYSConst.orange1[SYSConst.light3]);
        }
        cpDay.setOpaque(true);

        cpDay.setHorizontalAlignment(SwingConstants.LEADING);
        cpDay.setStyle(CollapsiblePane.PLAIN_STYLE);
        cpDay.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                createContentPanel4Day(day, cpDay);
                btnAcknowledge.setEnabled(true);
            }

            @Override
            public void paneCollapsed(CollapsiblePaneEvent collapsiblePaneEvent) {
                btnAcknowledge.setEnabled(false);
            }
        });

        btnAcknowledge.setEnabled(!cpDay.isCollapsed());
        if (!cpDay.isCollapsed()) {
            createContentPanel4Day(day, cpDay);
        }

        return cpDay;
    }


    private void createContentPanel4Day(final LocalDate day, final CollapsiblePane cpDay) {

        final JPanel dayPanel = new JPanel(new VerticalLayout());


        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));
        OPDE.getMainframe().setBlocked(true);

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {

//                final JPanel dayPanel = new JPanel(new VerticalLayout());
                dayPanel.setOpaque(false);

                ArrayList<Handovers> listHO = HandoversTools.getBy(day, HomesService.get());
                ArrayList<NReport> listNR = NReportTools.getNReports4Handover(day, HomesService.get());

                Collections.sort(listNR, myComparator);

                int max = listHO.size() + listNR.size();
                int i = 0; // for zebra pattern and progress
                for (final Handovers handover : listHO) {
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), i, max));

                    String title = "<html><table border=\"0\">" +
                            "<tr valign=\"top\">" +
                            "<td width=\"100\" align=\"left\">" + DateFormat.getTimeInstance(DateFormat.SHORT).format(handover.getPit()) +
                            " " + SYSTools.xx("misc.msg.Time.short") +
                            "</td>" +
                            "<td width=\"100\" align=\"center\">--</td>" +
                            "<td width=\"400\" align=\"left\">" +
                            handover.getText() +
                            "</td>" +

                            "<td width=\"100\" align=\"left\">" + handover.getUser().getFullname() + "</td>" +
                            "</tr>" +
                            "</table>" +
                            "</html>";

                    final DefaultCPTitle pnlSingle = new DefaultCPTitle(SYSTools.toHTMLForScreen(title), evt -> {
                        EntityManager em = OPDE.createEM();
                        if (Handover2UserTools.containsUser(em, handover, OPDE.getLogin().getUser())) {
                            em.close();
                            return;
                        }
                        try {
                            em.getTransaction().begin();
                            Handovers myHO = em.merge(handover);
                            Handover2User connObj = em.merge(new Handover2User(myHO, em.merge(OPDE.getLogin().getUser())));
                            myHO.getUsersAcknowledged().add(connObj);
                            em.getTransaction().commit();

                            createCP4Day(day);
                            buildPanel();

                        } catch (OptimisticLockException ole) {
                            log.warn(ole);
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

                    final JButton btnInfo = new JButton(SYSConst.icon22info);
                    btnInfo.setPressedIcon(SYSConst.icon22infoPressed);
                    btnInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    btnInfo.setAlignmentY(Component.TOP_ALIGNMENT);
                    btnInfo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btnInfo.setContentAreaFilled(false);
                    btnInfo.setBorder(null);
                    btnInfo.addActionListener(e -> {
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));
                        OPDE.getMainframe().setBlocked(true);

                        SwingWorker worker1 = new SwingWorker() {

                            @Override
                            protected Object doInBackground() throws Exception {
                                SYSFilesTools.print(Handover2UserTools.getAsHTML(handover), false);
                                return null;
                            }

                            @Override
                            protected void done() {
                                try {
                                    get();
                                } catch (Exception ex1) {
                                    OPDE.fatal(ex1);
                                }
                                OPDE.getDisplayManager().setProgressBarMessage(null);
                                OPDE.getMainframe().setBlocked(false);
                            }

                        };
                        worker1.execute();

                    });
                    pnlSingle.getRight().add(btnInfo);

                    EntityManager em = OPDE.createEM();
                    pnlSingle.getButton().setIcon(Handover2UserTools.containsUser(em, handover, OPDE.getLogin().getUser()) ? SYSConst.icon22ledGreenOn : SYSConst.icon22ledRedOn);
                    em.close();

                    pnlSingle.getButton().setVerticalTextPosition(SwingConstants.TOP);

                    JPanel zebra = new JPanel();
                    zebra.setLayout(new BoxLayout(zebra, BoxLayout.LINE_AXIS));
                    zebra.setOpaque(true);
                    if (i % 2 == 0) {
                        zebra.setBackground(SYSConst.orange1[SYSConst.light2]);
                    } else {
                        zebra.setBackground(Color.WHITE);
                    }
                    zebra.add(pnlSingle.getMain());
                    i++;
                    dayPanel.add(zebra);
                }
                for (final NReport nreport : listNR) {
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), i, max));


                    String title = "<html><table border=\"0\">" +
                            "<tr valign=\"top\">" +
                            "<td width=\"100\" align=\"left\">" + DateFormat.getTimeInstance(DateFormat.SHORT).format(nreport.getPit()) +
                            " " + SYSTools.xx("misc.msg.Time.short") +
                            "</td>" +
                            "<td width=\"100\" align=\"left\">" + ResidentTools.getTextCompact(nreport.getResident()) + "</td>" +
                            "<td width=\"400\" align=\"left\">" +
                            nreport.getText() +
                            "</td>" +

                            "<td width=\"100\" align=\"left\">" + nreport.getNewBy().getFullname() + "</td>" +
                            "</tr>" +
                            "</table>" +
                            "</html>";


                    final DefaultCPTitle pnlSingle = new DefaultCPTitle(SYSTools.toHTMLForScreen(title), evt -> {
                        EntityManager em = OPDE.createEM();
                        if (NR2UserTools.containsUser(em, nreport, OPDE.getLogin().getUser())) {
                            em.close();
                            return;
                        }

                        try {
                            em.getTransaction().begin();
                            NReport myNR = em.merge(nreport);
                            NR2User connObj = em.merge(new NR2User(myNR, em.merge(OPDE.getLogin().getUser())));
                            myNR.getUsersAcknowledged().add(connObj);
                            em.getTransaction().commit();
                            createCP4Day(day);
                            buildPanel();
                        } catch (OptimisticLockException ole) {
                            log.warn(ole);
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

                    final JButton btnInfo = new JButton(SYSConst.icon22info);
                    btnInfo.setPressedIcon(SYSConst.icon22infoPressed);
                    btnInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    btnInfo.setAlignmentY(Component.TOP_ALIGNMENT);
                    btnInfo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btnInfo.setContentAreaFilled(false);
                    btnInfo.setBorder(null);
                    btnInfo.addActionListener(e -> {

                        SYSFilesTools.print(NR2UserTools.getAsHTML(nreport), false);

//                            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));
//                            OPDE.getMainframe().setBlocked(true);
//
//                            SwingWorker worker = new SwingWorker() {
//
//                                @Override
//                                protected Object doInBackground() throws Exception {
//
//                                    return null;
//                                }
//
//                                @Override
//                                protected void done() {
//                                    OPDE.getDisplayManager().setProgressBarMessage(null);
//                                    OPDE.getMainframe().setBlocked(false);
//                                }
//
//                            };
//                            worker.execute();

                    });
                    pnlSingle.getRight().add(btnInfo);

                    EntityManager em = OPDE.createEM();
                    pnlSingle.getButton().setIcon(NR2UserTools.containsUser(em, nreport, OPDE.getLogin().getUser()) ? SYSConst.icon22ledGreenOn : SYSConst.icon22ledRedOn);
                    em.close();

                    pnlSingle.getButton().setVerticalTextPosition(SwingConstants.TOP);


                    JPanel zebra = new JPanel();
                    zebra.setLayout(new BoxLayout(zebra, BoxLayout.LINE_AXIS));
                    zebra.setOpaque(true);
                    if (i % 2 == 0) {
                        zebra.setBackground(SYSConst.orange1[SYSConst.light2]);
                    } else {
                        zebra.setBackground(Color.WHITE);
                    }
                    zebra.add(pnlSingle.getMain());
                    i++;

                    dayPanel.add(zebra);
                }
                final String key = DateFormat.getDateInstance().format(day.toDate());
                synchronized (cacheHO) {
                    cacheHO.put(key, listHO);
                }
                synchronized (cacheNR) {
                    cacheNR.put(key, listNR);
                }
                return null;

            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception ex2) {
                    OPDE.fatal(ex2);
                }
                cpDay.setContentPane(dayPanel);
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);
            }
        };
        worker.execute();

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspHandover;
    private CollapsiblePanes cpsHandover;
    // End of variables declaration//GEN-END:variables

    private void buildPanel() {
        cpsHandover.removeAll();
        cpsHandover.setLayout(new JideBoxLayout(cpsHandover, JideBoxLayout.Y_AXIS));

        MutableInterval minmax = NReportTools.getMinMax();
        if (minmax != null) {
            LocalDate start = SYSCalendar.bom(minmax.getStart()).toLocalDate();
            LocalDate end = new LocalDate();

            for (int year = end.getYear(); year >= start.getYear(); year--) {
                final String keyYear = Integer.toString(year) + ".year";
                cpsHandover.add(cpMap.get(keyYear));
            }
        }

        cpsHandover.addExpansion();
    }


    private List<Component> addCommands() {
        List<Component> list = new ArrayList<Component>();

        /***
         *      _   _
         *     | \ | | _____      __
         *     |  \| |/ _ \ \ /\ / /
         *     | |\  |  __/\ V  V /
         *     |_| \_|\___| \_/\_/
         *
         */
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.INSERT, internalClassID)) {
            JideButton addButton = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.handover.tooltips.btnadd"), SYSConst.icon22add, actionEvent -> {
                currentEditor = new DlgHOReport(new Handovers(HomesService.get()), report -> {
                    if (report != null) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            final Handovers myHO = em.merge((Handovers) report);
                            myHO.getUsersAcknowledged().add(em.merge(new Handover2User(myHO, OPDE.getLogin().getUser())));
                            em.getTransaction().commit();

                            LocalDate day = new LocalDate(myHO.getPit());

                            final String key = DateFormat.getDateInstance().format(myHO.getPit());

                            createCP4Day(day);
                            expandDay(day);

                            buildPanel();
                            GUITools.scroll2show(jspHandover, cpMap.get(key), cpsHandover, o -> {
//                                            GUITools.flashBackground(linemapHO.get(myHO), Color.YELLOW, 2);
                            });
                        } catch (OptimisticLockException ole) {
                            log.warn(ole);
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
                    currentEditor = null;
                });
                currentEditor.setVisible(true);
            });
            list.add(addButton);
        }


        //https://github.com/tloehr/Offene-Pflege.de/issues/43
//        final JideButton btnFindOpenHandovers = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.handover.tooltips.btnFindOpenHandovers"), SYSConst.icon22RedFlag, null);
//        btnFindOpenHandovers.addActionListener(actionEvent -> {
//
////                OPDE.getMainframe().setBlocked(true);
////                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));
//            final long time = System.currentTimeMillis();
//
//            SwingWorker worker = new SwingWorker() {
//                Date max = null;
//
//                @Override
//                protected Object doInBackground() throws Exception {
//
//                    LocalDate start = new LocalDate().minusWeeks(checkWeeksbackForNewReports);
//                    LocalDate end = new LocalDate();
//                    int maxdays = checkWeeksbackForNewReports * 7;
//                    int running = 0;
//
//                    for (LocalDate day = start; day.compareTo(end) <= 0; day = day.plusDays(1)) {
//                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), running, maxdays));
//                        running++;
//
//                        if (NR2UserTools.hasOpenReports(day, OPDE.getLogin().getUser(), HomesService.get())) {
//                            expandDay(day);
//                        }
//                    }
//
//                    return null;
//                }
//
//                @Override
//                protected void done() {
//
//                    OPDE.getDisplayManager().setProgressBarMessage(null);
//                    OPDE.getMainframe().setBlocked(false);
//
//                }
//            };
//            worker.execute();
//        });
//        list.add(btnFindOpenHandovers);


        final JideButton btnControllingToday = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.handover.tooltips.btnControllingToday"), SYSConst.icon22magnify1, null);
        btnControllingToday.addActionListener(actionEvent -> {
            btnControllingToday.setEnabled(false);
            HandoversTools.printSupervision(new LocalDate(), HomesService.get(), o -> btnControllingToday.setEnabled(true));
        });
        list.add(btnControllingToday);

        final JideButton btnControllingYesterday = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.handover.tooltips.btnControllingYesterday"), SYSConst.icon22magnify1, null);
        btnControllingYesterday.addActionListener(actionEvent -> {
            btnControllingYesterday.setEnabled(false);
            HandoversTools.printSupervision(new LocalDate().minusDays(1), HomesService.get(), o -> btnControllingYesterday.setEnabled(true));
        });
        list.add(btnControllingYesterday);

        return list;
    }


    private List<Component> addFilters() {
        List<Component> list = new ArrayList<Component>();

        MutableInterval minmax = NReportTools.getMinMax();
        if (minmax != null) {
            final DefaultComboBoxModel yearModel = new DefaultComboBoxModel();
            for (int year = new LocalDate().getYear(); year >= minmax.getStart().getYear(); year--) {
                yearModel.addElement(year);
            }

            JPanel innerPanel = new JPanel();
            innerPanel.setOpaque(false);
            innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
            txtSearch = new JXSearchField(SYSTools.xx("misc.msg.searchphrase"));
            txtSearch.setInstantSearchDelay(100000);
            txtSearch.setFont(SYSConst.ARIAL14);
            txtSearch.addActionListener(e -> {
                if (SYSTools.catchNull(txtSearch.getText()).trim().length() > 3) {
                    SYSFilesTools.print(NReportTools.getReportsAndHandoversAsHTML(NReportTools.getNReports4Handover(HomesService.get(), txtSearch.getText().trim(), Integer.parseInt(yearModel.getSelectedItem().toString())), txtSearch.getText().trim(), Integer.parseInt(yearModel.getSelectedItem().toString())), false);
                }
            });
            innerPanel.add(txtSearch);
            JButton btnSearchGeneralReports = GUITools.createHyperlinkButton("nursingrecords.handover.searchHandovers", null, null);
            btnSearchGeneralReports.addActionListener(e -> {
                List listHandovers = HandoversTools.getBy(Integer.parseInt(yearModel.getSelectedItem().toString()), HomesService.get());
                SYSFilesTools.print(NReportTools.getReportsAndHandoversAsHTML(listHandovers, "", Integer.parseInt(yearModel.getSelectedItem().toString())), false);
            });
            innerPanel.add(btnSearchGeneralReports);
            yearCombo = new JXComboBox(yearModel);
            yearCombo.addItemListener(e -> txtSearch.postActionEvent());

            JPanel myPanel = new JPanel();
            myPanel.setOpaque(false);
            myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.LINE_AXIS));
            myPanel.add(innerPanel);
            myPanel.add(yearCombo);
            list.add(myPanel);
        }

//        cmbHomes = new JComboBox();
//        cmbHomes.setFont(SYSConst.ARIAL14);
//        HomesService.setComboBox(cmbHomes);
//        cmbHomes.addItemListener(itemEvent -> {
//            if (itemEvent.getStateChange() != ItemEvent.SELECTED) return;
//            reloadDisplay();
//        });
//        list.add(cmbHomes);

        tbResidentFirst = GUITools.getNiceToggleButton("nursingrecords.handover.residentFirst");
        SYSPropsTools.restoreState("nursingrecords.handover.tbResidentFirst", tbResidentFirst);
        tbResidentFirst.addItemListener(e -> {
            SYSPropsTools.storeState("nursingrecords.handover.tbResidentFirst", tbResidentFirst);
            reload();
        });
        tbResidentFirst.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbResidentFirst);

        return list;
    }
}
