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
package op.care.supervisor;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.Homes;
import entity.HomesTools;
import entity.files.SYSFilesTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.reports.*;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class PnlHandover extends NursingRecordsPanel {
    public static final String internalClassID = "nursingrecords.handover";

    private JXSearchField txtSearch;
    private JComboBox yearCombo;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    private Map<String, CollapsiblePane> cpMap;
    //    private Map<String, JPanel> contentmap;
    private Map<String, ArrayList<Handovers>> cacheHO;
    private Map<String, ArrayList<NReport>> cacheNR;
    //    private Map<NReport, JPanel> linemapNR;
//    private Map<Handovers, JPanel> linemapHO;
    private HashMap<DateMidnight, String> hollidays;
    private JComboBox cmbHomes;
    private JToggleButton tbResidentFirst;
    private Comparator myComparator;

    Format monthFormatter = new SimpleDateFormat("MMMM yyyy");
    Format dayFormat = new SimpleDateFormat("EEEE, dd.MM.yyyy");
    boolean massAcknowledgeRunning = false;

    /**
     * Creates new form PnlHandover
     */
    public PnlHandover(JScrollPane jspSearch) {
        this.jspSearch = jspSearch;
        initComponents();
        initPanel();

//        jdcDatum.setDate(SYSCalendar.today_date());

        reloadDisplay();
    }

    @Override
    public void reload() {
        reloadDisplay();
    }

    private java.util.List<Component> addKey() {
        java.util.List<Component> list = new ArrayList<Component>();
        list.add(new JSeparator());
        list.add(new JLabel(OPDE.lang.getString("misc.msg.key")));
        list.add(new JLabel(OPDE.lang.getString(internalClassID + ".keydescription1"), SYSConst.icon22ledRedOn, SwingConstants.LEADING));
        list.add(new JLabel(OPDE.lang.getString(internalClassID + ".keydescription2"), SYSConst.icon22ledGreenOn, SwingConstants.LEADING));
        return list;
    }

    private void initPanel() {

        myComparator = new Comparator<NReport>() {
            @Override
            public int compare(NReport o1, NReport o2) {
                if (!tbResidentFirst.isSelected()) {
                    return o1.getPit().compareTo(o2.getPit());
                } else {
                    int comp = o1.getResident().getRID().compareTo(o2.getResident().getRID());
                    if (comp == 0) {
                        comp = o1.getPit().compareTo(o2.getPit());
                    }
                    return comp;
                }
            }
        };

//        contentmap = Collections.synchronizedMap(new HashMap<String, JPanel>());
        cpMap = Collections.synchronizedMap(new HashMap<String, CollapsiblePane>());
//        linemapNR = Collections.synchronizedMap(new HashMap<NReport, JPanel>());
//        linemapHO = Collections.synchronizedMap(new HashMap<Handovers, JPanel>());
        cacheHO = Collections.synchronizedMap(new HashMap<String, ArrayList<Handovers>>());
        cacheNR = Collections.synchronizedMap(new HashMap<String, ArrayList<NReport>>());
        OPDE.getDisplayManager().setMainMessage(OPDE.lang.getString(internalClassID));
        prepareSearchArea();
    }

    @Override
    public void switchResident(Resident resident) {
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
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
        GUITools.addAllComponents(mypanel, addKey());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }

    @Override
    public void cleanup() {
        cpsHandover.removeAll();

//        synchronized (contentmap) {
//            SYSTools.clear(contentmap);
//        }
        synchronized (cpMap) {
            SYSTools.clear(cpMap);
        }
//        synchronized (linemapHO) {
//            SYSTools.clear(linemapHO);
//        }
//        synchronized (linemapNR) {
//            SYSTools.clear(linemapNR);
//        }
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


//        synchronized (contentmap) {
//            SYSTools.clear(contentmap);
//        }
        synchronized (cpMap) {
            SYSTools.clear(cpMap);
        }
//        synchronized (linemapHO) {
//            SYSTools.clear(linemapHO);
//        }
//        synchronized (linemapNR) {
//            SYSTools.clear(linemapNR);
//        }
        synchronized (cacheHO) {
            SYSTools.clear(cacheHO);
        }
        synchronized (cacheNR) {
            SYSTools.clear(cacheNR);
        }


        Pair<DateTime, DateTime> minmax = NReportTools.getMinMax();
        if (minmax != null) {

            hollidays = SYSCalendar.getHolidays(minmax.getFirst().getYear(), minmax.getSecond().getYear());
            DateMidnight start = minmax.getFirst().toDateMidnight().dayOfMonth().withMinimumValue();
            DateMidnight end = new DateMidnight();
            for (int year = end.getYear(); year >= start.getYear(); year--) {
                createCP4Year(year, start, end);
            }
        }

        expandDay(new DateMidnight());

        buildPanel();

    }


    private void expandDay(DateMidnight day) {
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
                for (DateMidnight month = end; month.compareTo(start) >= 0; month = month.minusMonths(1)) {
                    pnlContent.add(createCP4Month(month));
                }

                cpYear.setContentPane(pnlContent);

            }
        });

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


    private JPanel createContentPanel4Month(DateMidnight month) {
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

        DateMidnight now = new DateMidnight();

        boolean sameMonth = now.dayOfMonth().withMaximumValue().equals(month.dayOfMonth().withMaximumValue());

        final DateMidnight start = sameMonth ? now : month.dayOfMonth().withMaximumValue();
        final DateMidnight end = month.dayOfMonth().withMinimumValue();

        for (DateMidnight day = start; end.compareTo(day) <= 0; day = day.minusDays(1)) {
            pnlMonth.add(createCP4Day(day));
        }

        return pnlMonth;
    }

    private CollapsiblePane createCP4Day(final DateMidnight day) {
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

        final JButton btnAcknowledge = new JButton(SYSConst.icon163ledGreenOn);
        btnAcknowledge.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnAcknowledge.setToolTipText(OPDE.lang.getString(internalClassID + ".tooltips.btnAcknowledge"));
        btnAcknowledge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
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
                } catch (OptimisticLockException ole) { OPDE.warn(ole);
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


    private void createContentPanel4Day(final DateMidnight day, final CollapsiblePane cpDay) {

        final JPanel dayPanel = new JPanel(new VerticalLayout());


        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));
        OPDE.getMainframe().setBlocked(true);

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {

//                final JPanel dayPanel = new JPanel(new VerticalLayout());
                dayPanel.setOpaque(false);

                ArrayList<Handovers> listHO = HandoversTools.getBy(day, (Homes) cmbHomes.getSelectedItem());
                ArrayList<NReport> listNR = NReportTools.getNReports4Handover(day, (Homes) cmbHomes.getSelectedItem());

                Collections.sort(listNR, myComparator);

                int max = listHO.size() + listNR.size();
                int i = 0; // for zebra pattern and progress
                for (final Handovers handover : listHO) {
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), i, max));

                    String title = "<html><table border=\"0\">" +
                            "<tr valign=\"top\">" +
                            "<td width=\"100\" align=\"left\">" + DateFormat.getTimeInstance(DateFormat.SHORT).format(handover.getPit()) +
                            " " + OPDE.lang.getString("misc.msg.Time.short") +
                            "</td>" +
                            "<td width=\"100\" align=\"center\">--</td>" +
                            "<td width=\"400\" align=\"left\">" +
                            handover.getText() +
                            "</td>" +

                            "<td width=\"100\" align=\"left\">" + handover.getUser().getFullname() + "</td>" +
                            "</tr>" +
                            "</table>" +
                            "</html>";

                    final DefaultCPTitle pnlSingle = new DefaultCPTitle(SYSTools.toHTMLForScreen(title), new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
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

                            } catch (OptimisticLockException ole) { OPDE.warn(ole);
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

                    final JButton btnInfo = new JButton(SYSConst.icon22info);
                    btnInfo.setPressedIcon(SYSConst.icon22infoPressed);
                    btnInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    btnInfo.setAlignmentY(Component.TOP_ALIGNMENT);
                    btnInfo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btnInfo.setContentAreaFilled(false);
                    btnInfo.setBorder(null);
                    btnInfo.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));
                            OPDE.getMainframe().setBlocked(true);

                            SwingWorker worker = new SwingWorker() {

                                @Override
                                protected Object doInBackground() throws Exception {
                                    SYSFilesTools.print(Handover2UserTools.getAsHTML(handover), false);
                                    return null;
                                }

                                @Override
                                protected void done() {
                                    OPDE.getDisplayManager().setProgressBarMessage(null);
                                    OPDE.getMainframe().setBlocked(false);
                                }

                            };
                            worker.execute();

                        }
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
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), i, max));


                    String title = "<html><table border=\"0\">" +
                            "<tr valign=\"top\">" +
                            "<td width=\"100\" align=\"left\">" + DateFormat.getTimeInstance(DateFormat.SHORT).format(nreport.getPit()) +
                            " " + OPDE.lang.getString("misc.msg.Time.short") +
                            "<br/>" + nreport.getMinutes() + " " + OPDE.lang.getString("misc.msg.Minute(s)") +
                            "</td>" +
                            "<td width=\"100\" align=\"left\">" + ResidentTools.getTextCompact(nreport.getResident()) + "</td>" +
                            "<td width=\"400\" align=\"left\">" +
                            nreport.getText() +
                            "</td>" +

                            "<td width=\"100\" align=\"left\">" + nreport.getUser().getFullname() + "</td>" +
                            "</tr>" +
                            "</table>" +
                            "</html>";


                    final DefaultCPTitle pnlSingle = new DefaultCPTitle(SYSTools.toHTMLForScreen(title), new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
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
                            } catch (OptimisticLockException ole) { OPDE.warn(ole);
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

                    final JButton btnInfo = new JButton(SYSConst.icon22info);
                    btnInfo.setPressedIcon(SYSConst.icon22infoPressed);
                    btnInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    btnInfo.setAlignmentY(Component.TOP_ALIGNMENT);
                    btnInfo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btnInfo.setContentAreaFilled(false);
                    btnInfo.setBorder(null);
                    btnInfo.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));
                            OPDE.getMainframe().setBlocked(true);

                            SwingWorker worker = new SwingWorker() {

                                @Override
                                protected Object doInBackground() throws Exception {
                                    SYSFilesTools.print(NR2UserTools.getAsHTML(nreport), false);
                                    return null;
                                }

                                @Override
                                protected void done() {
                                    OPDE.getDisplayManager().setProgressBarMessage(null);
                                    OPDE.getMainframe().setBlocked(false);
                                }

                            };
                            worker.execute();

                        }
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

        Pair<DateTime, DateTime> minmax = NReportTools.getMinMax();
        if (minmax != null) {
            DateMidnight start = minmax.getFirst().toDateMidnight().dayOfMonth().withMinimumValue();
            DateMidnight end = new DateMidnight();

            for (int year = end.getYear(); year >= start.getYear(); year--) {
                final String keyYear = Integer.toString(year) + ".year";
                cpsHandover.add(cpMap.get(keyYear));
            }
        }

        cpsHandover.addExpansion();
    }


    private java.util.List<Component> addCommands() {
        java.util.List<Component> list = new ArrayList<Component>();

        /***
         *      _   _
         *     | \ | | _____      __
         *     |  \| |/ _ \ \ /\ / /
         *     | |\  |  __/\ V  V /
         *     |_| \_|\___| \_/\_/
         *
         */
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.INSERT, internalClassID)) {
            JideButton addButton = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".tooltips.btnadd"), SYSConst.icon22add, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgHOReport(new Handovers((Homes) cmbHomes.getSelectedItem()), new Closure() {
                        @Override
                        public void execute(Object report) {
                            if (report != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    final Handovers myHO = em.merge((Handovers) report);
                                    myHO.getUsersAcknowledged().add(em.merge(new Handover2User(myHO, OPDE.getLogin().getUser())));
                                    em.getTransaction().commit();

                                    DateMidnight day = new DateMidnight(myHO.getPit());

                                    final String key = DateFormat.getDateInstance().format(myHO.getPit());

                                    createCP4Day(day);
                                    expandDay(day);

                                    buildPanel();
                                    GUITools.scroll2show(jspHandover, cpMap.get(key), cpsHandover, new Closure() {
                                        @Override
                                        public void execute(Object o) {
//                                            GUITools.flashBackground(linemapHO.get(myHO), Color.YELLOW, 2);
                                        }
                                    });
                                } catch (OptimisticLockException ole) { OPDE.warn(ole);
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
            list.add(addButton);
        }

        final JideButton btnControllingToday = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".tooltips.btnControllingToday"), SYSConst.icon22magnify1, null);
        btnControllingToday.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                btnControllingToday.setEnabled(false);
                HandoversTools.printSupervision(new DateMidnight(), (Homes) cmbHomes.getSelectedItem(), new Closure() {
                    @Override
                    public void execute(Object o) {
                        btnControllingToday.setEnabled(true);
                    }
                });
            }
        });
        list.add(btnControllingToday);

        final JideButton btnControllingYesterday = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".tooltips.btnControllingYesterday"), SYSConst.icon22magnify1, null);
        btnControllingYesterday.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                btnControllingYesterday.setEnabled(false);
                HandoversTools.printSupervision(new DateMidnight().minusDays(1), (Homes) cmbHomes.getSelectedItem(), new Closure() {
                    @Override
                    public void execute(Object o) {
                        btnControllingYesterday.setEnabled(true);
                    }
                });
            }
        });
        list.add(btnControllingYesterday);

        return list;
    }


    private List<Component> addFilters() {
        List<Component> list = new ArrayList<Component>();

        Pair<DateTime, DateTime> minmax = NReportTools.getMinMax();
        if (minmax != null) {
            final DefaultComboBoxModel yearModel = new DefaultComboBoxModel();
            for (int year = new DateMidnight().getYear(); year >= minmax.getFirst().getYear(); year--) {
                yearModel.addElement(year);
            }

            JPanel innerPanel = new JPanel();
            innerPanel.setOpaque(false);
            innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
            txtSearch = new JXSearchField(OPDE.lang.getString("misc.msg.searchphrase"));
            txtSearch.setInstantSearchDelay(100000);
            txtSearch.setFont(SYSConst.ARIAL14);
            txtSearch.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (SYSTools.catchNull(txtSearch.getText()).trim().length() > 3) {
                        SYSFilesTools.print(NReportTools.getReportsAndHandoversAsHTML(NReportTools.getNReports4Handover((Homes) cmbHomes.getSelectedItem(), txtSearch.getText().trim(), Integer.parseInt(yearModel.getSelectedItem().toString())), txtSearch.getText().trim(), Integer.parseInt(yearModel.getSelectedItem().toString())), false);
                    }
                }
            });
            innerPanel.add(txtSearch);
            JButton btnSearchGeneralReports = GUITools.createHyperlinkButton(internalClassID + ".searchHandovers", null, null);
            btnSearchGeneralReports.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List listHandovers = HandoversTools.getBy(Integer.parseInt(yearModel.getSelectedItem().toString()), (Homes) cmbHomes.getSelectedItem());
                    SYSFilesTools.print(NReportTools.getReportsAndHandoversAsHTML(listHandovers, "", Integer.parseInt(yearModel.getSelectedItem().toString())), false);
                }
            });
            innerPanel.add(btnSearchGeneralReports);
            yearCombo = new JXComboBox(yearModel);
            yearCombo.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    txtSearch.postActionEvent();
                }
            });

            JPanel myPanel = new JPanel();
            myPanel.setOpaque(false);
            myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.LINE_AXIS));
            myPanel.add(innerPanel);
            myPanel.add(yearCombo);
            list.add(myPanel);
        }

        cmbHomes = new JComboBox();
        cmbHomes.setFont(SYSConst.ARIAL14);
        HomesTools.setComboBox(cmbHomes);
        cmbHomes.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() != ItemEvent.SELECTED) return;
                reloadDisplay();
            }
        });
        list.add(cmbHomes);

        tbResidentFirst = GUITools.getNiceToggleButton(internalClassID + ".residentFirst");
        SYSPropsTools.restoreState(internalClassID + ".tbResidentFirst", tbResidentFirst);
        tbResidentFirst.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SYSPropsTools.storeState(internalClassID + ".tbResidentFirst", tbResidentFirst);
                reload();
            }
        });
        tbResidentFirst.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbResidentFirst);

        return list;
    }
}
