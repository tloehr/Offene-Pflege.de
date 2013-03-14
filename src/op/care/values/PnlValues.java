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
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.files.SYSFilesTools;
import entity.files.SYSVAL2FILE;
import entity.info.Resident;
import entity.process.*;
import entity.values.ResValue;
import entity.values.ResValueTools;
import entity.values.ResValueTypes;
import entity.values.ResValueTypesTools;
import op.OPDE;
import op.care.sysfiles.DlgFiles;
import op.process.DlgProcessAssign;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.*;

/**
 * @author tloehr
 */
public class PnlValues extends NursingRecordsPanel {
    public static final String internalClassID = "nursingrecords.vitalparameters";

    private Resident resident;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    private ArrayList<ResValueTypes> lstValueTypes;
    private HashMap<String, CollapsiblePane> cpMap;
    private HashMap<ResValue, JPanel> linemap;
    private HashMap<String, ArrayList<ResValue>> mapType2Values;
    private HashMap<DateMidnight, Pair<BigDecimal, BigDecimal>> balances;

    private Color[] color1, color2;

    public PnlValues(Resident resident, JScrollPane jspSearch) {
        this.resident = resident;
        this.jspSearch = jspSearch;

        initComponents();
        initPanel();
        prepareSearchArea();
        switchResident(resident);
    }

    private void initPanel() {
        cpMap = new HashMap<String, CollapsiblePane>();
        linemap = new HashMap<ResValue, JPanel>();
        mapType2Values = new HashMap<String, ArrayList<ResValue>>();
        balances = new HashMap<DateMidnight, Pair<BigDecimal, BigDecimal>>();

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
        mypanel.setLayout(new VerticalLayout(3));
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
        GUITools.addAllComponents(mypanel, addKey());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }

    @Override
    public String getInternalClassID() {
        return internalClassID;
    }

    private java.util.List<Component> addKey() {
        java.util.List<Component> list = new ArrayList<Component>();
        list.add(new JSeparator());
        list.add(new JLabel(OPDE.lang.getString("misc.msg.key")));
        list.add(new JLabel(OPDE.lang.getString(internalClassID + ".keydescription1"), SYSConst.icon22ledGreenOn, SwingConstants.LEADING));
        list.add(new JLabel(OPDE.lang.getString(internalClassID + ".keydescription2"), SYSConst.icon22ledGreenOff, SwingConstants.LEADING));
        return list;
    }

    private java.util.List<Component> addCommands() {
        java.util.List<Component> list = new ArrayList<Component>();

        JideButton addButton = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnControlling.tooltip"), SYSConst.icon22magnify1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!resident.isActive()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                    return;
                }
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
     * always regenerated by the PrinterForm Editor.
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
        GUITools.setResidentDisplay(resident);
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
        cpsValues.removeAll();
        linemap.clear();
        mapType2Values.clear();
        cpMap.clear();
        balances.clear();

        for (ResValueTypes vtype : lstValueTypes) {
            createCP4(vtype);
        }

        buildPanel();

    }

    private CollapsiblePane createCP4(final ResValueTypes vtype) {
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
                SYSConst.html_bold(vtype.getText()) +
                "</font></html>";

        final DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
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

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
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
                                    final String key = myValue.getType().getValType() == ResValueTypesTools.LIQUIDBALANCE ? vtype.getID() + ".xtypes." + dt.toDateMidnight().getMillis() + ".day" : vtype.getID() + ".xtypes." + Integer.toString(dt.getYear()) + ".year";

                                    try {
                                        cpMap.get(keyType).setCollapsed(false);
                                        cpMap.get(key).setCollapsed(false);
                                    } catch (PropertyVetoException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    }

                                    if (!mapType2Values.get(key).contains(myValue)) {
                                        mapType2Values.get(key).add(myValue);
                                        Collections.sort(mapType2Values.get(key));
                                    }

                                    cptitle.getButton().setIcon(SYSConst.icon22ledGreenOn);

                                    createCP4(vtype, dt.getYear());

                                    buildPanel();

                                    GUITools.scroll2show(jspValues, linemap.get(myValue), cpsValues, new Closure() {
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
            btnAdd.setEnabled(resident.isActive());
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

        if (!ResValueTools.getYearsWithValues(resident, vtype).isEmpty()) {
            cptitle.getButton().setIcon(SYSConst.icon22ledGreenOn);
        } else {
            cptitle.getButton().setIcon(SYSConst.icon22ledGreenOff);
        }

        cpType.setHorizontalAlignment(SwingConstants.LEADING);
        cpType.setOpaque(false);
        cpType.setBackground(getColor(vtype, SYSConst.medium1));

        return cpType;
    }

    private JPanel createContentPanel4(final ResValueTypes vtype) {
        ArrayList<Integer> years = ResValueTools.getYearsWithValues(resident, vtype);
        JPanel pnlContent = new JPanel(new VerticalLayout());
        if (!years.isEmpty()) {
            for (int year : years) {
                pnlContent.add(createCP4(vtype, year));
            }
        }
        return pnlContent;
    }

    private CollapsiblePane createCP4(final ResValueTypes vtype, final int year) {
        final String key = vtype.getID() + ".xtypes." + Integer.toString(year) + ".year";
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        }
        final CollapsiblePane cpYear = cpMap.get(key);

        String title = "<html><font size=+1>" +
                Integer.toString(year) +
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


        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
            final JButton btnPrintYear = new JButton(SYSConst.icon22print2);
            btnPrintYear.setPressedIcon(SYSConst.icon22print2Pressed);
            btnPrintYear.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnPrintYear.setContentAreaFilled(false);
            btnPrintYear.setBorder(null);
            btnPrintYear.setToolTipText(OPDE.lang.getString("misc.tooltips.btnprintyear"));
            btnPrintYear.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (!mapType2Values.containsKey(key)) {
                        mapType2Values.put(key, ResValueTools.getResValues(resident, vtype, year));
                    }
                    SYSFilesTools.print(SYSTools.toHTML(ResValueTools.getAsHTML(mapType2Values.get(key), vtype)), true);
                }
            });
            cptitle.getRight().add(btnPrintYear);
        }

        cpYear.setTitleLabelComponent(cptitle.getMain());
        cpYear.setSlidingDirection(SwingConstants.SOUTH);

        cpYear.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                if (vtype.getValType() == ResValueTypesTools.LIQUIDBALANCE) {
                    cpYear.setContentPane(createContentPanelByDay4(vtype, year));
                } else {
                    cpYear.setContentPane(createContentPanel4(vtype, year));
                }

                cpYear.setOpaque(false);
            }
        });
        cpYear.setBackground(getColor(vtype, SYSConst.light4));

        if (!cpYear.isCollapsed()) {
            if (vtype.getValType() == ResValueTypesTools.LIQUIDBALANCE) {
                cpYear.setContentPane(createContentPanelByDay4(vtype, year));
            } else {
                cpYear.setContentPane(createContentPanel4(vtype, year));
            }
            cpYear.setOpaque(false);
        }

        cpYear.setHorizontalAlignment(SwingConstants.LEADING);
        cpYear.setOpaque(false);

        return cpYear;
    }

    private JPanel createContentPanel4(final ResValueTypes vtype, final int year) {
        final String key = vtype.getID() + ".xtypes." + Integer.toString(year) + ".year";
        if (!mapType2Values.containsKey(key)) {
            mapType2Values.put(key, ResValueTools.getResValues(resident, vtype, year));
        }
        if (mapType2Values.get(key).isEmpty()) {
            JLabel lbl = new JLabel(OPDE.lang.getString("misc.msg.novalue"));
            JPanel pnl = new JPanel();
            pnl.add(lbl);
            return pnl;
        }


        JPanel pnlYear = new JPanel(new VerticalLayout());

        pnlYear.setBackground(getColor(vtype, SYSConst.light3));
        pnlYear.setOpaque(false);

        for (final ResValue resValue : mapType2Values.get(key)) {
            String title = "<html><table border=\"0\">" +
                    "<tr>" +
                    "<td width=\"200\" align=\"left\">" + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(resValue.getPit()) + " [" + resValue.getID() + "]</td>" +
                    "<td width=\"340\" align=\"left\">" + ResValueTools.getValueAsHTML(resValue) + "</td>" +
                    "<td width=\"200\" align=\"left\">" + resValue.getUser().getFullname() + "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</html>";

            final DefaultCPTitle pnlTitle = new DefaultCPTitle(title, null);

            if (resValue.isObsolete()) {
                pnlTitle.getAdditionalIconPanel().add(new JLabel(SYSConst.icon22eraser));
            }
            if (resValue.isReplacement()) {
                pnlTitle.getAdditionalIconPanel().add(new JLabel(SYSConst.icon22edited));
            }
            if (!resValue.getText().trim().isEmpty()) {
                pnlTitle.getAdditionalIconPanel().add(new JLabel(SYSConst.icon22info));
            }
            if (pnlTitle.getAdditionalIconPanel().getComponentCount() > 0) {
                pnlTitle.getButton().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        GUITools.showPopup(GUITools.getHTMLPopup(pnlTitle.getButton(), ResValueTools.getInfoAsHTML(resValue)), SwingConstants.NORTH);
                    }
                });
            }


            if (!resValue.getAttachedFilesConnections().isEmpty()) {
                /***
                 *      _     _         _____ _ _
                 *     | |__ | |_ _ __ |  ___(_) | ___  ___
                 *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
                 *     | |_) | |_| | | |  _| | | |  __/\__ \
                 *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
                 *
                 */
                final JButton btnFiles = new JButton(Integer.toString(resValue.getAttachedFilesConnections().size()), SYSConst.icon22greenStar);
                btnFiles.setToolTipText(OPDE.lang.getString("misc.btnfiles.tooltip"));
                btnFiles.setForeground(Color.BLUE);
                btnFiles.setHorizontalTextPosition(SwingUtilities.CENTER);
                btnFiles.setFont(SYSConst.ARIAL18BOLD);
                btnFiles.setPressedIcon(SYSConst.icon22Pressed);
                btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnFiles.setAlignmentY(Component.TOP_ALIGNMENT);
                btnFiles.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnFiles.setContentAreaFilled(false);
                btnFiles.setBorder(null);

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

                                mapType2Values.get(key).remove(resValue);
                                mapType2Values.get(key).add(myValue);
                                Collections.sort(mapType2Values.get(key));

                                createCP4(vtype, year);
                                buildPanel();
                            }
                        });
                    }
                });
                btnFiles.setEnabled(OPDE.isFTPworking());
                pnlTitle.getRight().add(btnFiles);
            }


            if (!resValue.getAttachedProcessConnections().isEmpty()) {
                /***
                 *      _     _         ____
                 *     | |__ | |_ _ __ |  _ \ _ __ ___   ___ ___  ___ ___
                 *     | '_ \| __| '_ \| |_) | '__/ _ \ / __/ _ \/ __/ __|
                 *     | |_) | |_| | | |  __/| | | (_) | (_|  __/\__ \__ \
                 *     |_.__/ \__|_| |_|_|   |_|  \___/ \___\___||___/___/
                 *
                 */
                final JButton btnProcess = new JButton(Integer.toString(resValue.getAttachedProcessConnections().size()), SYSConst.icon22redStar);
                btnProcess.setToolTipText(OPDE.lang.getString("misc.btnprocess.tooltip"));
                btnProcess.setForeground(Color.YELLOW);
                btnProcess.setHorizontalTextPosition(SwingUtilities.CENTER);
                btnProcess.setFont(SYSConst.ARIAL18BOLD);
                btnProcess.setPressedIcon(SYSConst.icon22Pressed);
                btnProcess.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnProcess.setAlignmentY(Component.TOP_ALIGNMENT);
                btnProcess.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnProcess.setContentAreaFilled(false);
                btnProcess.setBorder(null);
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

                                    ArrayList<SYSVAL2PROCESS> attached = new ArrayList<SYSVAL2PROCESS>(resValue.getAttachedProcessConnections());
                                    for (SYSVAL2PROCESS linkObject : attached) {
                                        if (unassigned.contains(linkObject.getQProcess())) {
                                            linkObject.getQProcess().getAttachedNReportConnections().remove(linkObject);
                                            linkObject.getResValue().getAttachedProcessConnections().remove(linkObject);
                                            em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + myValue.getTitle() + " ID: " + myValue.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, linkObject.getQProcess()));
                                            em.remove(linkObject);
                                        }
                                    }
                                    attached.clear();

                                    for (QProcess qProcess : assigned) {
                                        java.util.List<QProcessElement> listElements = qProcess.getElements();
                                        if (!listElements.contains(myValue)) {
                                            QProcess myQProcess = em.merge(qProcess);
                                            SYSVAL2PROCESS myLinkObject = em.merge(new SYSVAL2PROCESS(myQProcess, myValue));
                                            em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_ASSIGN_ELEMENT) + ": " + myValue.getTitle() + " ID: " + myValue.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, myQProcess));
                                            qProcess.getAttachedResValueConnections().add(myLinkObject);
                                            myValue.getAttachedProcessConnections().add(myLinkObject);
                                        }
                                    }

                                    em.getTransaction().commit();

                                    mapType2Values.get(key).remove(resValue);
                                    mapType2Values.get(key).add(myValue);
                                    Collections.sort(mapType2Values.get(key));

                                    createCP4(vtype, year);


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
                                } catch (RollbackException ole) {
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
                btnProcess.setEnabled(OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID));
                pnlTitle.getRight().add(btnProcess);
            }
            /***
             *      __  __
             *     |  \/  | ___ _ __  _   _
             *     | |\/| |/ _ \ '_ \| | | |
             *     | |  | |  __/ | | | |_| |
             *     |_|  |_|\___|_| |_|\__,_|
             *
             */
            final JButton btnMenu = new JButton(SYSConst.icon22menu);
            btnMenu.setPressedIcon(SYSConst.icon22Pressed);
            btnMenu.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnMenu.setAlignmentY(Component.TOP_ALIGNMENT);
            btnMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnMenu.setContentAreaFilled(false);
            btnMenu.setBorder(null);
            btnMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JidePopup popup = new JidePopup();
                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                    popup.setOwner(btnMenu);
                    popup.removeExcludedComponent(btnMenu);
                    JPanel pnl = getMenu(resValue);
                    popup.getContentPane().add(pnl);
                    popup.setDefaultFocusComponent(pnl);

                    GUITools.showPopup(popup, SwingConstants.WEST);
                }
            });
            btnMenu.setEnabled(!resValue.isObsolete());
            pnlTitle.getRight().add(btnMenu);

            pnlYear.add(pnlTitle.getMain());
            linemap.put(resValue, pnlTitle.getMain());
        }
        return pnlYear;
    }

    private JPanel createContentPanelByDay4(final ResValueTypes vtype, final int year) {

        ArrayList<Date> listDays = ResValueTools.getDaysWithValues(resident, vtype, year);
        JPanel pnlYear = new JPanel(new VerticalLayout());

        DateTime from = new DateMidnight(year, 1, 1).dayOfYear().withMinimumValue().toDateTime();
        DateTime to = new DateMidnight(year, 1, 1).toDateTime().dayOfYear().withMaximumValue().secondOfDay().withMaximumValue();

        balances.putAll(ResValueTools.getLiquidBalancePerDay(resident, from.toDateMidnight(), to.toDateMidnight()));

        for (final Date d : listDays) {

            final DateMidnight day = new DateMidnight(d);

            final String key = vtype.getID() + ".xtypes." + d.getTime() + ".day";
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
                    DateFormat.getDateInstance().format(d) + " " + OPDE.lang.getString("misc.msg.ingestion") + ": " + balances.get(day).getFirst() + " " + vtype.getUnit1() + "  " + OPDE.lang.getString("misc.msg.egestion") + ": " + balances.get(day).getSecond() + " " + vtype.getUnit1() +
                    "</font></html>";

            final DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
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

            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
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

                        new DlgValue(new ResValue(resident, vtype, SYSCalendar.addTime2Date(d, new Date())), false, new Closure() {
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
                                        final String key = myValue.getType().getValType() == ResValueTypesTools.LIQUIDBALANCE ? vtype.getID() + ".xtypes." + dt.toDateMidnight().getMillis() + ".day" : vtype.getID() + ".xtypes." + Integer.toString(dt.getYear()) + ".year";

                                        try {
                                            cpMap.get(keyType).setCollapsed(false);
                                            cpMap.get(key).setCollapsed(false);
                                        } catch (PropertyVetoException e) {
                                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                        }

                                        if (!mapType2Values.get(key).contains(myValue)) {
                                            mapType2Values.get(key).add(myValue);
                                            Collections.sort(mapType2Values.get(key));
                                        }

                                        cptitle.getButton().setIcon(SYSConst.icon22ledGreenOn);

                                        createCP4(vtype, dt.getYear());

                                        buildPanel();

                                        GUITools.scroll2show(jspValues, linemap.get(myValue), cpsValues, new Closure() {
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
                btnAdd.setEnabled(resident.isActive());
            }

            cpType.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
                @Override
                public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                    cpType.setContentPane(createContentPanel4(vtype, day));
                }
            });

            if (!cpType.isCollapsed()) {
                cpType.setContentPane(createContentPanel4(vtype, day));
            }
            cpType.setHorizontalAlignment(SwingConstants.LEADING);
            cpType.setOpaque(false);
            cpType.setBackground(getColor(vtype, SYSConst.light3));

            pnlYear.add(cpType);
        }

        return pnlYear;
    }


    private JPanel createContentPanel4(final ResValueTypes vtype, final DateMidnight day) {
        final String key = vtype.getID() + ".xtypes." + day.getMillis() + ".day";
        if (!mapType2Values.containsKey(key)) {
            mapType2Values.put(key, ResValueTools.getResValues(resident, vtype, day));
        }
        if (mapType2Values.get(key).isEmpty()) {
            JLabel lbl = new JLabel(OPDE.lang.getString("misc.msg.novalue"));
            JPanel pnl = new JPanel();
            pnl.add(lbl);
            return pnl;
        }

        JPanel pnlDay = new JPanel(new VerticalLayout());

        pnlDay.setBackground(getColor(vtype, SYSConst.light3));
        pnlDay.setOpaque(false);


        for (final ResValue resValue : mapType2Values.get(key)) {
            String title = "<html><table border=\"0\">" +
                    "<tr>" +
                    "<td width=\"200\" align=\"left\">" + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(resValue.getPit()) + " [" + resValue.getID() + "]</td>" +
                    "<td width=\"340\" align=\"left\">" + ResValueTools.getValueAsHTML(resValue) + "</td>" +
                    "<td width=\"200\" align=\"left\">" + resValue.getUser().getFullname() + "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</html>";

            final DefaultCPTitle pnlTitle = new DefaultCPTitle(title, null);

            if (resValue.isObsolete()) {
                pnlTitle.getAdditionalIconPanel().add(new JLabel(SYSConst.icon22eraser));
            }
            if (resValue.isReplacement()) {
                pnlTitle.getAdditionalIconPanel().add(new JLabel(SYSConst.icon22edited));
            }
            if (!resValue.getText().trim().isEmpty()) {
                pnlTitle.getAdditionalIconPanel().add(new JLabel(SYSConst.icon22info));
            }
            if (pnlTitle.getAdditionalIconPanel().getComponentCount() > 0) {
                pnlTitle.getButton().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        GUITools.showPopup(GUITools.getHTMLPopup(pnlTitle.getButton(), ResValueTools.getInfoAsHTML(resValue)), SwingConstants.NORTH);
                    }
                });
            }


            if (!resValue.getAttachedFilesConnections().isEmpty()) {
                /***
                 *      _     _         _____ _ _
                 *     | |__ | |_ _ __ |  ___(_) | ___  ___
                 *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
                 *     | |_) | |_| | | |  _| | | |  __/\__ \
                 *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
                 *
                 */
                final JButton btnFiles = new JButton(Integer.toString(resValue.getAttachedFilesConnections().size()), SYSConst.icon22greenStar);
                btnFiles.setToolTipText(OPDE.lang.getString("misc.btnfiles.tooltip"));
                btnFiles.setForeground(Color.BLUE);
                btnFiles.setHorizontalTextPosition(SwingUtilities.CENTER);
                btnFiles.setFont(SYSConst.ARIAL18BOLD);
                btnFiles.setPressedIcon(SYSConst.icon22Pressed);
                btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnFiles.setAlignmentY(Component.TOP_ALIGNMENT);
                btnFiles.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnFiles.setContentAreaFilled(false);
                btnFiles.setBorder(null);

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

                                mapType2Values.get(key).remove(resValue);
                                mapType2Values.get(key).add(myValue);
                                Collections.sort(mapType2Values.get(key));

                                createCP4(vtype, day.getYear());
                                buildPanel();
                            }
                        });
                    }
                });
                btnFiles.setEnabled(OPDE.isFTPworking());
                pnlTitle.getRight().add(btnFiles);
            }


            if (!resValue.getAttachedProcessConnections().isEmpty()) {
                /***
                 *      _     _         ____
                 *     | |__ | |_ _ __ |  _ \ _ __ ___   ___ ___  ___ ___
                 *     | '_ \| __| '_ \| |_) | '__/ _ \ / __/ _ \/ __/ __|
                 *     | |_) | |_| | | |  __/| | | (_) | (_|  __/\__ \__ \
                 *     |_.__/ \__|_| |_|_|   |_|  \___/ \___\___||___/___/
                 *
                 */
                final JButton btnProcess = new JButton(Integer.toString(resValue.getAttachedProcessConnections().size()), SYSConst.icon22redStar);
                btnProcess.setToolTipText(OPDE.lang.getString("misc.btnprocess.tooltip"));
                btnProcess.setForeground(Color.YELLOW);
                btnProcess.setHorizontalTextPosition(SwingUtilities.CENTER);
                btnProcess.setFont(SYSConst.ARIAL18BOLD);
                btnProcess.setPressedIcon(SYSConst.icon22Pressed);
                btnProcess.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnProcess.setAlignmentY(Component.TOP_ALIGNMENT);
                btnProcess.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnProcess.setContentAreaFilled(false);
                btnProcess.setBorder(null);
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

                                    ArrayList<SYSVAL2PROCESS> attached = new ArrayList<SYSVAL2PROCESS>(resValue.getAttachedProcessConnections());
                                    for (SYSVAL2PROCESS linkObject : attached) {
                                        if (unassigned.contains(linkObject.getQProcess())) {
                                            linkObject.getQProcess().getAttachedNReportConnections().remove(linkObject);
                                            linkObject.getResValue().getAttachedProcessConnections().remove(linkObject);
                                            em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + myValue.getTitle() + " ID: " + myValue.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, linkObject.getQProcess()));
                                            em.remove(linkObject);
                                        }
                                    }
                                    attached.clear();

                                    for (QProcess qProcess : assigned) {
                                        java.util.List<QProcessElement> listElements = qProcess.getElements();
                                        if (!listElements.contains(myValue)) {
                                            QProcess myQProcess = em.merge(qProcess);
                                            SYSVAL2PROCESS myLinkObject = em.merge(new SYSVAL2PROCESS(myQProcess, myValue));
                                            em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_ASSIGN_ELEMENT) + ": " + myValue.getTitle() + " ID: " + myValue.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, myQProcess));
                                            qProcess.getAttachedResValueConnections().add(myLinkObject);
                                            myValue.getAttachedProcessConnections().add(myLinkObject);
                                        }
                                    }

                                    em.getTransaction().commit();

                                    mapType2Values.get(key).remove(resValue);
                                    mapType2Values.get(key).add(myValue);
                                    Collections.sort(mapType2Values.get(key));

                                    createCP4(vtype, day.getYear());


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
                                } catch (RollbackException ole) {
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
                btnProcess.setEnabled(OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID));
                pnlTitle.getRight().add(btnProcess);
            }
            /***
             *      __  __
             *     |  \/  | ___ _ __  _   _
             *     | |\/| |/ _ \ '_ \| | | |
             *     | |  | |  __/ | | | |_| |
             *     |_|  |_|\___|_| |_|\__,_|
             *
             */
            final JButton btnMenu = new JButton(SYSConst.icon22menu);
            btnMenu.setPressedIcon(SYSConst.icon22Pressed);
            btnMenu.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnMenu.setAlignmentY(Component.TOP_ALIGNMENT);
            btnMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnMenu.setContentAreaFilled(false);
            btnMenu.setBorder(null);
            btnMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JidePopup popup = new JidePopup();
                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                    popup.setOwner(btnMenu);
                    popup.removeExcludedComponent(btnMenu);
                    JPanel pnl = getMenu(resValue);
                    popup.getContentPane().add(pnl);
                    popup.setDefaultFocusComponent(pnl);

                    GUITools.showPopup(popup, SwingConstants.WEST);
                }
            });
            btnMenu.setEnabled(!resValue.isObsolete());
            pnlTitle.getRight().add(btnMenu);

            pnlDay.add(pnlTitle.getMain());
            linemap.put(resValue, pnlTitle.getMain());
        }

        return pnlDay;
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
        SYSTools.clear(cpMap);
        SYSTools.clear(linemap);
        SYSTools.clear(lstValueTypes);
    }

    private void buildPanel() {
        cpsValues.removeAll();
        cpsValues.setLayout(new JideBoxLayout(cpsValues, JideBoxLayout.Y_AXIS));

        for (ResValueTypes vtype : lstValueTypes) {
            cpsValues.add(cpMap.get(vtype.getID() + ".xtypes"));
        }

        cpsValues.addExpansion();
    }


    private JPanel getMenu(final ResValue resValue) {

        final ResValueTypes vtype = resValue.getType();

        JPanel pnlMenu = new JPanel(new VerticalLayout());

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            /***
             *      _____    _ _ _
             *     | ____|__| (_) |_
             *     |  _| / _` | | __|
             *     | |__| (_| | | |_
             *     |_____\__,_|_|\__|
             *
             */
            final JButton btnEdit = GUITools.createHyperlinkButton(internalClassID + ".btnEdit.tooltip", SYSConst.icon22edit3, null);
            btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
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
                                    final String key = newValue.getType().getValType() == ResValueTypesTools.LIQUIDBALANCE ? vtype.getID() + ".xtypes." + dt.toDateMidnight().getMillis() + ".day" : vtype.getID() + ".xtypes." + Integer.toString(dt.getYear()) + ".year";


                                    mapType2Values.get(key).remove(resValue);
                                    mapType2Values.get(key).add(oldValue);
                                    mapType2Values.get(key).add(newValue);
                                    Collections.sort(mapType2Values.get(key));

                                    createCP4(vtype, dt.getYear());

                                    try {
                                        cpMap.get(keyType).setCollapsed(false);
                                        cpMap.get(key).setCollapsed(false);
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
            btnEdit.setEnabled(!resValue.isObsolete());
            pnlMenu.add(btnEdit);

            /***
             *      ____       _      _
             *     |  _ \  ___| | ___| |_ ___
             *     | | | |/ _ \ |/ _ \ __/ _ \
             *     | |_| |  __/ |  __/ ||  __/
             *     |____/ \___|_|\___|\__\___|
             *
             */
            final JButton btnDelete = GUITools.createHyperlinkButton(internalClassID + ".btnDelete.tooltip", SYSConst.icon22delete, null);
            btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
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

                                    final String key = myValue.getType().getValType() == ResValueTypesTools.LIQUIDBALANCE ? vtype.getID() + ".xtypes." + dt.toDateMidnight().getMillis() + ".day" : vtype.getID() + ".xtypes." + Integer.toString(dt.getYear()) + ".year";

                                    mapType2Values.get(key).remove(resValue);
                                    mapType2Values.get(key).add(myValue);
                                    Collections.sort(mapType2Values.get(key));


                                    createCP4(vtype, dt.getYear());

                                    try {
                                        cpMap.get(keyType).setCollapsed(false);
                                        cpMap.get(key).setCollapsed(false);
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
            btnDelete.setEnabled(!resValue.isObsolete());
            pnlMenu.add(btnDelete);


            pnlMenu.add(new JSeparator());
            /***
             *      _     _         _____ _ _
             *     | |__ | |_ _ __ |  ___(_) | ___  ___
             *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
             *     | |_) | |_| | | |  _| | | |  __/\__ \
             *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
             *
             */

            final JButton btnFiles = GUITools.createHyperlinkButton("misc.btnfiles.tooltip", SYSConst.icon22attach, null);
            btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
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
                            final String key = vtype.getID() + ".xtypes." + Integer.toString(dt.getYear()) + ".year";

                            mapType2Values.get(key).remove(resValue);
                            mapType2Values.get(key).add(myValue);
                            Collections.sort(mapType2Values.get(key));
                            buildPanel();
                        }
                    });
                }
            });
            btnFiles.setEnabled(!resValue.isObsolete() && OPDE.isFTPworking());
            pnlMenu.add(btnFiles);

            /***
             *      _     _         ____
             *     | |__ | |_ _ __ |  _ \ _ __ ___   ___ ___  ___ ___
             *     | '_ \| __| '_ \| |_) | '__/ _ \ / __/ _ \/ __/ __|
             *     | |_) | |_| | | |  __/| | | (_) | (_|  __/\__ \__ \
             *     |_.__/ \__|_| |_|_|   |_|  \___/ \___\___||___/___/
             *
             */
            final JButton btnProcess = GUITools.createHyperlinkButton("misc.btnprocess.tooltip", SYSConst.icon22link, null);
            btnProcess.setAlignmentX(Component.RIGHT_ALIGNMENT);
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

                                ArrayList<SYSVAL2PROCESS> attached = new ArrayList<SYSVAL2PROCESS>(resValue.getAttachedProcessConnections());
                                for (SYSVAL2PROCESS linkObject : attached) {
                                    if (unassigned.contains(linkObject.getQProcess())) {
                                        linkObject.getQProcess().getAttachedNReportConnections().remove(linkObject);
                                        linkObject.getResValue().getAttachedProcessConnections().remove(linkObject);
                                        em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + myValue.getTitle() + " ID: " + myValue.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, linkObject.getQProcess()));
                                        em.remove(linkObject);
                                    }
                                }
                                attached.clear();

                                for (QProcess qProcess : assigned) {
                                    java.util.List<QProcessElement> listElements = qProcess.getElements();
                                    if (!listElements.contains(myValue)) {
                                        QProcess myQProcess = em.merge(qProcess);
                                        SYSVAL2PROCESS myLinkObject = em.merge(new SYSVAL2PROCESS(myQProcess, myValue));
                                        em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_ASSIGN_ELEMENT) + ": " + myValue.getTitle() + " ID: " + myValue.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, myQProcess));
                                        qProcess.getAttachedResValueConnections().add(myLinkObject);
                                        myValue.getAttachedProcessConnections().add(myLinkObject);
                                    }
                                }

                                em.getTransaction().commit();

                                DateTime dt = new DateTime(myValue.getPit());
                                final String key = vtype.getID() + ".xtypes." + Integer.toString(dt.getYear()) + ".year";

                                mapType2Values.get(key).remove(resValue);
                                mapType2Values.get(key).add(myValue);
                                Collections.sort(mapType2Values.get(key));

                                createCP4(vtype, dt.getYear());

                                buildPanel();
                                //GUITools.flashBackground(contentmap.get(keyMonth), Color.YELLOW, 2);

                            } catch (OptimisticLockException ole) {
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
                                }
                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                            } catch (RollbackException ole) {
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
            btnProcess.setEnabled(!resValue.isObsolete());
            pnlMenu.add(btnProcess);
        }
        return pnlMenu;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspValues;
    private CollapsiblePanes cpsValues;
    // End of variables declaration//GEN-END:variables
}
