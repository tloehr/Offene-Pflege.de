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
package op.care.values;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.files.SYSFilesTools;
import entity.files.SYSVAL2FILE;
import entity.info.Resident;
import entity.process.QProcess;
import entity.process.QProcessElement;
import entity.process.SYSVAL2PROCESS;
import entity.values.ResValue;
import entity.values.ResValueTools;
import entity.values.ResValueTypes;
import op.OPDE;
import op.care.sysfiles.DlgFiles;
import op.process.DlgProcessAssign;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author tloehr
 */
public class PnlValues extends NursingRecordsPanel {

    private Resident resident;

    private boolean initPhase;
    private JPopupMenu menu;
    public static final String internalClassID = "nursingrecords.vitalparameters";

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JToggleButton tbShowReplaced;

    private ArrayList<ResValueTypes> lstValueTypes;
    private HashMap<String, CollapsiblePane> cpMap;
    private HashMap<String, JPanel> contentmap;
    private HashMap<ResValue, JPanel> linemap;
    private HashMap<String, ArrayList<ResValue>> valuecache;

    private Color[] color1, color2;
    Format monthFormatter = new SimpleDateFormat("MMMM yyyy");


    /**
     * Creates new form pnlVitalwerte
     */
    public PnlValues(Resident resident, JScrollPane jspSearch) {
        initPhase = true;
        this.resident = resident;
        this.jspSearch = jspSearch;

        initComponents();
        initPanel();
        prepareSearchArea();
        switchResident(resident);
        initPhase = false;
    }

    private void initPanel() {
        cpMap = new HashMap<String, CollapsiblePane>();
        contentmap = new HashMap<String, JPanel>();
        linemap = new HashMap<ResValue, JPanel>();
        valuecache = new HashMap<String, ArrayList<ResValue>>();

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT t FROM ResValueTypes t ORDER BY t.text");
        lstValueTypes = new ArrayList<ResValueTypes>(query.getResultList());
        em.close();

        color1 = SYSConst.blue1;
        color2 = SYSConst.greyscale;

    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(0));
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
//        GUITools.addAllComponents(mypanel, addFilters());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }

    @Override
    public String getInternalClassID() {
        return internalClassID;
    }

    private java.util.List<Component> addCommands() {
        java.util.List<Component> list = new ArrayList<Component>();

        JideButton addButton = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnControlling.tooltip"), SYSConst.icon22magnify1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new DlgValueControl(resident, new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o != null) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                Resident myResident = em.merge(resident);
                                em.lock(myResident, LockModeType.OPTIMISTIC);
                                myResident.setControlling((Properties) o);
                                em.getTransaction().commit();

                                resident = myResident;
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
        list.add(addButton);


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
        jspValues = new JScrollPane();
        cpsValues = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspValues ========
        {

            //======== cpsValues ========
            {
                cpsValues.setLayout(new BoxLayout(cpsValues, BoxLayout.X_AXIS));
            }
            jspValues.setViewportView(cpsValues);
        }
        add(jspValues);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void switchResident(Resident resident) {
        this.resident = resident;
        GUITools.setBWDisplay(resident);
        reloadDisplay();
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


        final boolean withworker = false;
        cpsValues.removeAll();
        cpsValues.removeAll();
        contentmap.clear();
        linemap.clear();
        valuecache.clear();


        if (withworker) {

//            OPDE.getMainframe().setBlocked(true);
//            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));
//
//            SwingWorker worker = new SwingWorker() {
//
//                @Override
//                protected Object doInBackground() throws Exception {
//                    int progress = -1;
//                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, lstPrescriptions.size()));
//
//                    lstPrescriptions = PrescriptionTools.getAll4ResInfo(resident);
//                    Collections.sort(lstPrescriptions);
//
//                    for (Prescription prescription : lstPrescriptions) {
//                        progress++;
//                        createCP4(prescription);
//                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, lstPrescriptions.size()));
//                    }
//
//                    return null;
//                }
//
//                @Override
//                protected void done() {
//                    buildPanel();
//                    OPDE.getDisplayManager().setProgressBarMessage(null);
//                    OPDE.getMainframe().setBlocked(false);
//                }
//            };
//            worker.execute();

        } else {
//
            for (ResValueTypes vtype : lstValueTypes) {
                createCP4(vtype);
            }

            buildPanel();
        }

    }


    private CollapsiblePane createCP4(final ResValueTypes vtype) {
        /***
         *                          _        ____ ____  _  _    ____     __    _           _____               __
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |  / /\ \   / /_ _| |_   _  __|_   _|   _ _ __   __\ \
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_| |  \ \ / / _` | | | | |/ _ \| || | | | '_ \ / _ \ |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _| |   \ V / (_| | | |_| |  __/| || |_| | |_) |  __/ |
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_| | |    \_/ \__,_|_|\__,_|\___||_| \__, | .__/ \___| |
         *                                                     \_\                              |___/|_|       /_/
         */
        final String key = vtype.getID() + ".xtypes";
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        final CollapsiblePane cpType = cpMap.get(key);

        String title = "<html><font size=+1>" +
                vtype.getText() +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpType.setCollapsed(!cpType.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });
        cpType.setTitleLabelComponent(cptitle.getMain());
        cpType.setSlidingDirection(SwingConstants.SOUTH);

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {
            /***
             *         _       _     _
             *        / \   __| | __| |
             *       / _ \ / _` |/ _` |
             *      / ___ \ (_| | (_| |
             *     /_/   \_\__,_|\__,_|
             *
             */
            final JButton btnAdd = new JButton(SYSConst.icon22add);
            btnAdd.setPressedIcon(SYSConst.icon22addPressed);
            btnAdd.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnAdd.setContentAreaFilled(false);
            btnAdd.setBorder(null);
            btnAdd.setToolTipText(OPDE.lang.getString(internalClassID + ".btnAdd.tooltip") + " (" + vtype.getText() + ")");
            btnAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgValue(new ResValue(resident, vtype), false, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {

                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    final ResValue myValue = em.merge((ResValue) o);
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    em.getTransaction().commit();

                                    DateTime dt = new DateTime(myValue.getPit());

                                    final String keyType = vtype.getID() + ".xtypes";
                                    final String keyYear = vtype.getID() + ".xtypes." + Integer.toString(dt.getYear()) + ".year";
                                    final String keyMonth = vtype.getID() + ".xtypes." + monthFormatter.format(dt.toDate()) + ".month";

                                    if (valuecache.containsKey(keyMonth)) {
                                        valuecache.get(keyMonth).add(myValue);
                                        Collections.sort(valuecache.get(keyMonth));
                                    }
                                    contentmap.remove(keyMonth);
                                    createCP4(vtype, dt.toDateMidnight());

                                    try {
                                        cpMap.get(keyType).setCollapsed(false);
                                        cpMap.get(keyYear).setCollapsed(false);
                                        cpMap.get(keyMonth).setCollapsed(false);
                                    } catch (PropertyVetoException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    }

                                    buildPanel();

                                    GUITools.scroll2show(jspValues, cpMap.get(keyMonth), cpsValues, new Closure() {
                                        @Override
                                        public void execute(Object o) {
                                            GUITools.flashBackground(linemap.get(myValue), Color.YELLOW, 2);
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
            cptitle.getRight().add(btnAdd);
        }
        cpType.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpType.setContentPane(createContentPanel4(vtype));
            }
        });

        if (!cpType.isCollapsed()) {
            cpType.setContentPane(createContentPanel4(vtype));
        }

        cpType.setHorizontalAlignment(SwingConstants.LEADING);
        cpType.setOpaque(false);
        cpType.setBackground(getColor(vtype, SYSConst.medium1));

        return cpType;
    }


    private JPanel createContentPanel4(final ResValueTypes vtype) {
        JPanel pnlContent = new JPanel(new VerticalLayout());
        Pair<DateTime, DateTime> minmax = ResValueTools.getMinMax(resident, vtype);

        if (minmax != null) {
            DateMidnight start = minmax.getFirst().toDateMidnight().dayOfMonth().withMinimumValue();
            DateMidnight end = resident.isActive() ? new DateMidnight() : minmax.getSecond().toDateMidnight().dayOfMonth().withMinimumValue();
            for (int year = end.getYear(); year >= start.getYear(); year--) {
                pnlContent.add(createCP4(vtype, year, start, end));
            }
        }
        return pnlContent;
    }


    private CollapsiblePane createCP4(final ResValueTypes vtype, final int year, DateMidnight min, DateMidnight max) {
        /***
         *                          _        ____ ____  _  _    ______           _     _            _       _       _ __
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |  / /  _ \ ___  ___(_) __| | ___ _ __ | |_    (_)_ __ | |\ \
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_| || |_) / _ \/ __| |/ _` |/ _ \ '_ \| __|   | | '_ \| __| |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _| ||  _ <  __/\__ \ | (_| |  __/ | | | |_ _  | | | | | |_| |
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_| | ||_| \_\___||___/_|\__,_|\___|_| |_|\__( ) |_|_| |_|\__| |
         *                                                     \_\                                     |/             /_/
         */

        final DateMidnight start = new DateMidnight(year, 1, 1).isBefore(min.dayOfMonth().withMinimumValue()) ? min.dayOfMonth().withMinimumValue() : new DateMidnight(year, 1, 1);
        final DateMidnight end = new DateMidnight(year, 12, 31).isAfter(max.dayOfMonth().withMaximumValue()) ? max.dayOfMonth().withMaximumValue() : new DateMidnight(year, 12, 31);

        final String key = vtype.getID() + ".xtypes." + Integer.toString(year) + ".year";
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

        final CollapsiblePane cpYear = cpMap.get(key);


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

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
            /***
             *      ____       _       _ __   __
             *     |  _ \ _ __(_)_ __ | |\ \ / /__  __ _ _ __
             *     | |_) | '__| | '_ \| __\ V / _ \/ _` | '__|
             *     |  __/| |  | | | | | |_ | |  __/ (_| | |
             *     |_|   |_|  |_|_| |_|\__||_|\___|\__,_|_|
             *
             */
            final JButton btnPrintYear = new JButton(SYSConst.icon22print2);
            btnPrintYear.setPressedIcon(SYSConst.icon22print2Pressed);
            btnPrintYear.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnPrintYear.setContentAreaFilled(false);
            btnPrintYear.setBorder(null);
            btnPrintYear.setToolTipText(OPDE.lang.getString("misc.tooltips.btnprintyear"));
            btnPrintYear.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    ArrayList<ResValue> listYear = new ArrayList<ResValue>();
                    for (DateMidnight month = end; month.compareTo(start) >= 0; month = month.minusMonths(1)) {
                        createCP4(vtype, month);
                        final String keymonth = vtype.getID() + ".xtypes." + monthFormatter.format(month.toDate()) + ".month";
                        if (!valuecache.containsKey(keymonth)) {
                            createContentPanel4(vtype, month);
                        }
                        listYear.addAll(valuecache.get(keymonth));
                    }

                    SYSFilesTools.print(ResValueTools.getAsHTML(listYear), true);
                }
            });
            cptitle.getRight().add(btnPrintYear);
        }

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
                    pnlContent.add(createCP4(vtype, month));
                }

                cpYear.setContentPane(pnlContent);
                cpYear.setOpaque(false);
            }
        });
        cpYear.setBackground(getColor(vtype, SYSConst.light4));

        if (!cpYear.isCollapsed()) {
            JPanel pnlContent = new JPanel(new VerticalLayout());

            for (DateMidnight month = end; month.compareTo(start) >= 0; month = month.minusMonths(1)) {
                pnlContent.add(createCP4(vtype, month));
            }

            cpYear.setContentPane(pnlContent);
            cpYear.setOpaque(false);
        }

        cpYear.setHorizontalAlignment(SwingConstants.LEADING);
        cpYear.setOpaque(false);

        return cpYear;
    }


    private CollapsiblePane createCP4(final ResValueTypes vtype, final DateMidnight month) {
        /***
         *                          _        ____ ____  _  _    ______           _     _            _       ____        _      _____ _              __
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |  / /  _ \ ___  ___(_) __| | ___ _ __ | |_    |  _ \  __ _| |_ __|_   _(_)_ __ ___   __\ \
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_| || |_) / _ \/ __| |/ _` |/ _ \ '_ \| __|   | | | |/ _` | __/ _ \| | | | '_ ` _ \ / _ \ |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _| ||  _ <  __/\__ \ | (_| |  __/ | | | |_ _  | |_| | (_| | ||  __/| | | | | | | | |  __/ |
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_| | ||_| \_\___||___/_|\__,_|\___|_| |_|\__( ) |____/ \__,_|\__\___||_| |_|_| |_| |_|\___| |
         *                                                     \_\                                     |/                                           /_/
         */
        final String key = vtype.getID() + ".xtypes." + monthFormatter.format(month.toDate()) + ".month";
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
                "&nbsp;<i>(" + vtype.getText() + ")</i>" +
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

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
            /***
             *      ____       _       _   __  __             _   _
             *     |  _ \ _ __(_)_ __ | |_|  \/  | ___  _ __ | |_| |__
             *     | |_) | '__| | '_ \| __| |\/| |/ _ \| '_ \| __| '_ \
             *     |  __/| |  | | | | | |_| |  | | (_) | | | | |_| | | |
             *     |_|   |_|  |_|_| |_|\__|_|  |_|\___/|_| |_|\__|_| |_|
             *
             */
            final JButton btnPrintMonth = new JButton(SYSConst.icon22print2);
            btnPrintMonth.setPressedIcon(SYSConst.icon22print2Pressed);
            btnPrintMonth.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnPrintMonth.setContentAreaFilled(false);
            btnPrintMonth.setBorder(null);
            btnPrintMonth.setToolTipText(OPDE.lang.getString("misc.tooltips.btnprintmonth"));
            btnPrintMonth.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    if (!valuecache.containsKey(key)) {
                        createContentPanel4(vtype, month);
                    }
                    SYSFilesTools.print(ResValueTools.getAsHTML(valuecache.get(key)), true);
                }
            });
            cptitle.getRight().add(btnPrintMonth);
        }

        cpMonth.setTitleLabelComponent(cptitle.getMain());
        cpMonth.setSlidingDirection(SwingConstants.SOUTH);
        cpMonth.setOpaque(false);
        cpMonth.setBackground(getColor(vtype, SYSConst.light3));

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
                cpMonth.setContentPane(createContentPanel4(vtype, month));
            }
        });

        if (!cpMonth.isCollapsed()) {
            cpMonth.setContentPane(createContentPanel4(vtype, month));
        }

        cpMonth.setHorizontalAlignment(SwingConstants.LEADING);
        cpMonth.setOpaque(false);

        return cpMonth;
    }

    private JPanel createContentPanel4(final ResValueTypes vtype, DateMidnight month) {
        final String key = vtype.getID() + ".xtypes." + monthFormatter.format(month.toDate()) + ".month";

        if (!contentmap.containsKey(key)) {

            JPanel pnlMonth = new JPanel(new VerticalLayout());

            pnlMonth.setBackground(getColor(vtype, SYSConst.light3));
            pnlMonth.setOpaque(false);

            valuecache.put(key, ResValueTools.getResValues(resident, vtype, month.toDateTime()));

            for (final ResValue resValue : valuecache.get(key)) {
                String title = "<html><table border=\"0\">" +
                        "<tr>" +
                        "<td width=\"200\" align=\"left\">" + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(resValue.getPit()) + " [" + resValue.getID() + "]</td>" +
//                        "<td width=\"130\" align=\"left\">" + resValue.getType().getText() + "</td>" +
                        "<td width=\"340\" align=\"left\">" + ResValueTools.getValueAsHTML(resValue) + "</td>" +
                        "<td width=\"200\" align=\"left\">" + resValue.getUser().getFullname() + "</td>" +
                        "</tr>" +
                        "</table>" +
                        "</html>";
                final DefaultCPTitle pnlTitle = new DefaultCPTitle(title, null);
                if (resValue.isDeleted() || resValue.isReplaced()) {
                    pnlTitle.getButton().setIcon(SYSConst.icon22eraser);
                    pnlTitle.getButton().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            GUITools.showPopup(GUITools.getHTMLPopup(pnlTitle.getButton(), ResValueTools.getInfoAsHTML(resValue)), SwingConstants.NORTH);
                        }
                    });
                }
                if (resValue.isReplacement()) {
                    pnlTitle.getButton().setIcon(SYSConst.icon22edited);
                    pnlTitle.getButton().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            GUITools.showPopup(GUITools.getHTMLPopup(pnlTitle.getButton(), ResValueTools.getInfoAsHTML(resValue)), SwingConstants.NORTH);
                        }
                    });
                }

                if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {
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
                            new DlgValue(resValue.clone(), true, new Closure() {
                                @Override
                                public void execute(Object o) {
                                    if (o != null) {

                                        EntityManager em = OPDE.createEM();
                                        try {

                                            em.getTransaction().begin();
                                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                            final ResValue newValue = em.merge((ResValue) o);
                                            ResValue oldValue = em.merge(resValue);

                                            em.lock(oldValue, LockModeType.OPTIMISTIC);
                                            newValue.setReplacementFor(oldValue);

                                            for (SYSVAL2FILE oldAssignment : oldValue.getAttachedFilesConnections()) {
                                                em.remove(oldAssignment);
                                            }
                                            oldValue.getAttachedFilesConnections().clear();
                                            for (SYSVAL2PROCESS oldAssignment : oldValue.getAttachedProcessConnections()) {
                                                em.remove(oldAssignment);
                                            }
                                            oldValue.getAttachedProcessConnections().clear();

                                            oldValue.setEditedBy(em.merge(OPDE.getLogin().getUser()));
                                            oldValue.setEditDate(new Date());
                                            oldValue.setReplacedBy(newValue);

                                            em.getTransaction().commit();

                                            DateTime dt = new DateTime(newValue.getPit());
                                            final String keyType = vtype.getID() + ".xtypes";
                                            final String keyYear = vtype.getID() + ".xtypes." + Integer.toString(dt.getYear()) + ".year";
                                            final String keyMonth = vtype.getID() + ".xtypes." + monthFormatter.format(dt.toDate()) + ".month";

                                            valuecache.get(keyMonth).remove(resValue);
                                            valuecache.get(keyMonth).add(oldValue);
                                            valuecache.get(keyMonth).add(newValue);
                                            Collections.sort(valuecache.get(keyMonth));

                                            contentmap.remove(keyMonth);
                                            createCP4(vtype, dt.toDateMidnight());

                                            try {
                                                cpMap.get(keyType).setCollapsed(false);
                                                cpMap.get(keyYear).setCollapsed(false);
                                                cpMap.get(keyMonth).setCollapsed(false);
                                            } catch (PropertyVetoException e) {
                                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                            }

                                            buildPanel();

                                            GUITools.scroll2show(jspValues, cpMap.get(keyMonth), cpsValues, new Closure() {
                                                @Override
                                                public void execute(Object o) {
                                                    GUITools.flashBackground(linemap.get(newValue), Color.YELLOW, 2);
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
                    btnEdit.setEnabled(!resValue.isArchived());
                    pnlTitle.getRight().add(btnEdit);

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
                            new DlgYesNo(OPDE.lang.getString("misc.questions.delete1") + "<br/><i>" + DateFormat.getDateTimeInstance().format(resValue.getPit()) + "</i><br/>" + OPDE.lang.getString("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                                @Override
                                public void execute(Object o) {
                                    if (o.equals(JOptionPane.YES_OPTION)) {

                                        EntityManager em = OPDE.createEM();
                                        try {

                                            em.getTransaction().begin();

                                            ResValue myValue = em.merge(resValue);
                                            myValue.setDeletedBy(em.merge(OPDE.getLogin().getUser()));

                                            for (SYSVAL2FILE file : myValue.getAttachedFilesConnections()) {
                                                em.remove(file);
                                            }
                                            myValue.getAttachedFilesConnections().clear();

                                            // Vorgangszuordnungen entfernen
                                            for (SYSVAL2PROCESS connObj : myValue.getAttachedProcessConnections()) {
                                                em.remove(connObj);
                                            }
                                            myValue.getAttachedProcessConnections().clear();
                                            myValue.getAttachedProcesses().clear();
                                            em.getTransaction().commit();

                                            DateTime dt = new DateTime(myValue.getPit());
                                            final String keyType = vtype.getID() + ".xtypes";
                                            final String keyYear = vtype.getID() + ".xtypes." + Integer.toString(dt.getYear()) + ".year";
                                            final String keyMonth = vtype.getID() + ".xtypes." + monthFormatter.format(dt.toDate()) + ".month";

                                            valuecache.get(keyMonth).remove(resValue);
                                            valuecache.get(keyMonth).add(myValue);
                                            Collections.sort(valuecache.get(keyMonth));

                                            contentmap.remove(keyMonth);
                                            createCP4(vtype, dt.toDateMidnight());

                                            try {
                                                cpMap.get(keyType).setCollapsed(false);
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
                    btnDelete.setEnabled(!resValue.isArchived());
                    pnlTitle.getRight().add(btnDelete);


                    /***
                     *      _     _         _____ _ _
                     *     | |__ | |_ _ __ |  ___(_) | ___  ___
                     *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
                     *     | |_) | |_| | | |  _| | | |  __/\__ \
                     *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
                     *
                     */

                    final JButton btnFiles = new JButton(SYSConst.icon22attach);
                    btnFiles.setPressedIcon(SYSConst.icon22attachPressed);
                    btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    btnFiles.setContentAreaFilled(false);
                    btnFiles.setBorder(null);
                    btnFiles.setToolTipText(OPDE.lang.getString("misc.btnfiles.tooltip"));
                    btnFiles.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            new DlgFiles(resValue, new Closure() {
                                @Override
                                public void execute(Object o) {
                                    EntityManager em = OPDE.createEM();
                                    ResValue myValue = em.merge(resValue);
                                    em.refresh(myValue);
                                    em.close();

                                    DateTime dt = new DateTime(myValue.getPit());
                                    final String keyMonth = vtype.getID() + ".xtypes." + monthFormatter.format(dt.toDate()) + ".month";

                                    valuecache.get(keyMonth).remove(resValue);
                                    valuecache.get(keyMonth).add(myValue);
                                    Collections.sort(valuecache.get(keyMonth));

                                    contentmap.remove(keyMonth);

                                    createCP4(vtype, dt.toDateMidnight());
                                    buildPanel();
                                    GUITools.flashBackground(contentmap.get(keyMonth), Color.YELLOW, 2);
                                }
                            });
                        }
                    });
                    btnFiles.setEnabled(!resValue.isArchived());

                    if (!resValue.getAttachedFilesConnections().isEmpty()) {
                        JLabel lblNum = new JLabel(Integer.toString(resValue.getAttachedFilesConnections().size()), SYSConst.icon16greenStar, SwingConstants.CENTER);
                        lblNum.setFont(SYSConst.ARIAL10BOLD);
                        lblNum.setForeground(Color.BLUE);
                        lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
                        DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnFiles, lblNum, DefaultOverlayable.CENTER);
                        overlayableBtn.setOpaque(false);
                        pnlTitle.getRight().add(overlayableBtn);
                    } else {
                        pnlTitle.getRight().add(btnFiles);
                    }

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
                    btnProcess.setToolTipText(OPDE.lang.getString("misc.btnprocess.tooltip"));
                    btnProcess.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            new DlgProcessAssign(resValue, new Closure() {
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
                                        ResValue myValue = em.merge(resValue);
                                        em.lock(myValue, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                                        for (SYSVAL2PROCESS linkObject : resValue.getAttachedProcessConnections()) {
                                            if (unassigned.contains(linkObject.getQProcess())) {
                                                em.remove(em.merge(linkObject));
                                            }
                                        }

                                        for (QProcess qProcess : assigned) {
                                            java.util.List<QProcessElement> listElements = qProcess.getElements();
                                            if (!listElements.contains(myValue)) {
                                                QProcess myQProcess = em.merge(qProcess);
                                                SYSVAL2PROCESS myLinkObject = em.merge(new SYSVAL2PROCESS(myQProcess, myValue));
                                                qProcess.getAttachedResValueConnections().add(myLinkObject);
                                                myValue.getAttachedProcessConnections().add(myLinkObject);
                                            }
                                        }

                                        em.getTransaction().commit();

                                        DateTime dt = new DateTime(myValue.getPit());
                                        final String keyMonth = vtype.getID() + ".xtypes." + monthFormatter.format(dt.toDate()) + ".month";

                                        valuecache.get(keyMonth).remove(resValue);
                                        valuecache.get(keyMonth).add(myValue);
                                        Collections.sort(valuecache.get(keyMonth));

                                        contentmap.remove(keyMonth);

                                        createCP4(vtype, dt.toDateMidnight());
                                        buildPanel();
                                        GUITools.flashBackground(contentmap.get(keyMonth), Color.YELLOW, 2);

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
                        }
                    });
                    btnProcess.setEnabled(!resValue.isArchived());

                    if (!resValue.getAttachedProcessConnections().isEmpty()) {
                        JLabel lblNum = new JLabel(Integer.toString(resValue.getAttachedProcessConnections().size()), SYSConst.icon16redStar, SwingConstants.CENTER);
                        lblNum.setFont(SYSConst.ARIAL10BOLD);
                        lblNum.setForeground(Color.YELLOW);
                        lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
                        DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnProcess, lblNum, DefaultOverlayable.CENTER);
                        overlayableBtn.setOpaque(false);
                        pnlTitle.getRight().add(overlayableBtn);
                    } else {
                        pnlTitle.getRight().add(btnProcess);
                    }
                }

                pnlMonth.add(pnlTitle.getMain());
                linemap.put(resValue, pnlTitle.getMain());
//
//                rowsum = rowsum.subtract(allowance.getAmount());
//            }
            }
            contentmap.put(key, pnlMonth);
        }
        return contentmap.get(key);
    }


    private Color getColor(ResValueTypes vtype, int level) {
        if (lstValueTypes.indexOf(vtype) % 2 == 0) {
            return color1[level];
        } else {
            return color2[level];
        }
    }

    @Override
    public void cleanup() {
        cpsValues.removeAll();
        cpMap.clear();
        contentmap.clear();
        linemap.clear();
        valuecache.clear();
        lstValueTypes.clear();
    }

//    private void prepareSearchArea() {
//        searchPanes = new CollapsiblePanes();
//        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
//        jspSearch.setViewportView(searchPanes);
//
//        JPanel mypanel = new JPanel();
//        mypanel.setLayout(new VerticalLayout(3));
//        mypanel.setBackground(Color.WHITE);
//
//        CollapsiblePane searchPane = new CollapsiblePane(OPDE.lang.getString(internalClassID));
//        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
//        searchPane.setCollapsible(false);
//
//        try {
//            searchPane.setCollapsed(false);
//        } catch (PropertyVetoException e) {
//            OPDE.error(e);
//        }
//
//        GUITools.addAllComponents(mypanel, addCommands());
//        GUITools.addAllComponents(mypanel, addFilters());
//
//        searchPane.setContentPane(mypanel);
//
//        searchPanes.add(searchPane);
//        searchPanes.addExpansion();
//    }
//
//
//    private java.util.List<Component> addFilters() {
//        java.util.List<Component> list = new ArrayList<Component>();
//
//        tbShowReplaced = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.showreplaced"));
//        SYSPropsTools.restoreState(internalClassID + ":tbShowReplaced", tbShowReplaced);
//        tbShowReplaced.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent itemEvent) {
//                if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
//                SYSPropsTools.storeState(internalClassID + ":tbShowReplaced", tbShowReplaced);
//                reloadTable();
//            }
//        });
//        tbShowReplaced.setHorizontalAlignment(SwingConstants.LEFT);
//        list.add(tbShowReplaced);
//
//        return list;
//    }
//
//    private java.util.List<Component> addCommands() {
//        java.util.List<Component> list = new ArrayList<Component>();
//
//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {
//            JideButton addButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.new"), SYSConst.icon22add, new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//
//                    new DlgValue(new ResValue(resident, OPDE.getLogin().getUser()), new Closure() {
//                        @Override
//                        public void execute(Object o) {
//                            if (o != null) {
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    em.merge(o);
//                                    em.getTransaction().commit();
//                                } catch (Exception e) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//                                reloadTable();
//                            }
//                        }
//                    });
//                }
//            });
//            list.add(addButton);
//        }
//
//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
//            JideButton printButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.print"), SYSConst.icon22print2, new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    printWerte(null);
//                }
//            });
//            list.add(printButton);
//        }
//
//        return list;
//    }


    private void buildPanel() {
        cpsValues.removeAll();
        cpsValues.setLayout(new JideBoxLayout(cpsValues, JideBoxLayout.Y_AXIS));

        for (ResValueTypes vtype : lstValueTypes) {
            cpsValues.add(cpMap.get(vtype.getID() + ".xtypes"));
        }

        cpsValues.addExpansion();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspValues;
    private CollapsiblePanes cpsValues;
    // End of variables declaration//GEN-END:variables
}
