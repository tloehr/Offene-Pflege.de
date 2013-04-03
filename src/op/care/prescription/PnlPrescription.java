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
package op.care.prescription;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.EntityTools;
import entity.files.SYSFilesTools;
import entity.info.Resident;
import entity.prescription.*;
import entity.process.*;
import entity.system.UniqueTools;
import op.OPDE;
import op.care.med.inventory.DlgCloseStock;
import op.care.med.inventory.DlgNewStocks;
import op.care.med.inventory.DlgOpenStock;
import op.care.med.inventory.PnlInventory;
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

/**
 * @author tloehr
 */
public class PnlPrescription extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.prescription";

    private Resident resident;

    private ArrayList<Prescription> lstPrescriptions, lstVisiblePrescriptions; // <= the latter is only for the zebra pattern
    private HashMap<String, CollapsiblePane> cpMap;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JToggleButton tbClosed;

    private Color[] color1, color2;

    /**
     * Creates new form PnlPrescription
     */
    public PnlPrescription(Resident resident, JScrollPane jspSearch) {
        this.jspSearch = jspSearch;
        this.resident = resident;
        initComponents();
        initPanel();
        switchResident(resident);
    }

    private void initPanel() {
        color1 = SYSConst.greyscale;
        color2 = SYSConst.blue1;
        cpMap = new HashMap<String, CollapsiblePane>();
        lstPrescriptions = new ArrayList<Prescription>();
        lstVisiblePrescriptions = new ArrayList<Prescription>();
        prepareSearchArea();
    }

    @Override
    public void switchResident(Resident res) {
        this.resident = EntityTools.find(Resident.class, res.getRID());
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
        final boolean withworker = true;
        cpsPrescription.removeAll();
        lstVisiblePrescriptions.clear();
        cpMap.clear();
        lstPrescriptions.clear();

        if (withworker) {

            OPDE.getMainframe().setBlocked(true);
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

            SwingWorker worker = new SwingWorker() {

                @Override
                protected Object doInBackground() throws Exception {
                    int progress = -1;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, lstPrescriptions.size()));

                    if (tbClosed.isSelected()) {
                        lstPrescriptions = PrescriptionTools.getAll(resident);
                    } else {
                        lstPrescriptions = PrescriptionTools.getAllActive(resident);
                    }
                    Collections.sort(lstPrescriptions);

                    for (Prescription prescription : lstPrescriptions) {
                        progress++;
                        createCP4(prescription);
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, lstPrescriptions.size()));
                    }

                    return null;
                }

                @Override
                protected void done() {
                    buildPanel();
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();

        } else {
            if (tbClosed.isSelected()) {
                lstPrescriptions = PrescriptionTools.getAll(resident);
            } else {
                lstPrescriptions = PrescriptionTools.getAllActive(resident);
            }
            Collections.sort(lstPrescriptions);
            for (Prescription prescription : lstPrescriptions) {
                createCP4(prescription);
            }

            buildPanel();
        }

    }

    @Override
    public String getInternalClassID() {
        return internalClassID;
    }


    private CollapsiblePane createCP4(final Prescription prescription) {
        /***
         *                          _        ____ ____  _  _    ______                          _       _   _           __
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |  / /  _ \ _ __ ___  ___  ___ _ __(_)_ __ | |_(_) ___  _ __\ \
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_| || |_) | '__/ _ \/ __|/ __| '__| | '_ \| __| |/ _ \| '_ \| |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _| ||  __/| | |  __/\__ \ (__| |  | | |_) | |_| | (_) | | | | |
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_| | ||_|   |_|  \___||___/\___|_|  |_| .__/ \__|_|\___/|_| |_| |
         *                                                     \_\                               |_|                    /_/
         */
        final String key = prescription.getID() + ".xprescription";
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        final CollapsiblePane cpPres = cpMap.get(key);


        String title = "<html><table border=\"0\">" +
                "<tr valign=\"top\">" +
                "<td width=\"280\" align=\"left\">" + prescription.getPITAsHTML() + "</td>" +
                "<td width=\"380\" align=\"left\">" +
                "<font size=+1>" + PrescriptionTools.getShortDescription(prescription) + "</font>" +
                PrescriptionTools.getDose(prescription) +
                PrescriptionTools.getInventoryInformationAsHTML(prescription) +
                "</td>" +
                "<td width=\"200\" align=\"left\">" +
                PrescriptionTools.getOriginalPrescription(prescription) +
                PrescriptionTools.getRemark(prescription) +
                "</td>" +
                "</table>" +
                "</html>";

//        String title2 = "";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, null);
        cpPres.setCollapsible(false);
        cptitle.getButton().setIcon(getIcon(prescription));

        cpPres.setTitleLabelComponent(cptitle.getMain());
        cpPres.setSlidingDirection(SwingConstants.SOUTH);


        if (!prescription.getAttachedFilesConnections().isEmpty()) {
            /***
             *      _     _         _____ _ _
             *     | |__ | |_ _ __ |  ___(_) | ___  ___
             *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
             *     | |_) | |_| | | |  _| | | |  __/\__ \
             *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
             *
             */
            final JButton btnFiles = new JButton(Integer.toString(prescription.getAttachedFilesConnections().size()), SYSConst.icon22greenStar);
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
                    Closure fileHandleClosure = OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID) ? null : new Closure() {
                        @Override
                        public void execute(Object o) {
                            EntityManager em = OPDE.createEM();
                            Prescription myPrescription = em.merge(prescription);
                            em.refresh(myPrescription);
                            em.close();
                            lstPrescriptions.remove(prescription);
                            lstPrescriptions.add(myPrescription);
                            Collections.sort(lstPrescriptions);
                            final CollapsiblePane myCP = createCP4(myPrescription);
                            buildPanel();
                            GUITools.flashBackground(myCP, Color.YELLOW, 2);
                        }
                    };
                    new DlgFiles(prescription, fileHandleClosure);
                }
            });
            btnFiles.setEnabled(OPDE.isFTPworking());
            cptitle.getRight().add(btnFiles);
        }


        if (!prescription.getAttachedProcessConnections().isEmpty()) {
            /***
             *      _     _         ____
             *     | |__ | |_ _ __ |  _ \ _ __ ___   ___ ___  ___ ___
             *     | '_ \| __| '_ \| |_) | '__/ _ \ / __/ _ \/ __/ __|
             *     | |_) | |_| | | |  __/| | | (_) | (_|  __/\__ \__ \
             *     |_.__/ \__|_| |_|_|   |_|  \___/ \___\___||___/___/
             *
             */
            final JButton btnProcess = new JButton(Integer.toString(prescription.getAttachedProcessConnections().size()), SYSConst.icon22redStar);
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
                    new DlgProcessAssign(prescription, new Closure() {
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
                                Prescription myPrescription = em.merge(prescription);
                                em.lock(myPrescription, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                                ArrayList<SYSPRE2PROCESS> attached = new ArrayList<SYSPRE2PROCESS>(prescription.getAttachedProcessConnections());
                                for (SYSPRE2PROCESS linkObject : attached) {
                                    if (unassigned.contains(linkObject.getQProcess())) {
                                        linkObject.getQProcess().getAttachedNReportConnections().remove(linkObject);
                                        linkObject.getPrescription().getAttachedProcessConnections().remove(linkObject);
                                        em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + myPrescription.getTitle() + " ID: " + myPrescription.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, linkObject.getQProcess()));
                                        em.remove(linkObject);
                                    }
                                }
                                attached.clear();


                                for (QProcess qProcess : assigned) {
                                    List<QProcessElement> listElements = qProcess.getElements();
                                    if (!listElements.contains(myPrescription)) {
                                        QProcess myQProcess = em.merge(qProcess);
                                        SYSPRE2PROCESS myLinkObject = em.merge(new SYSPRE2PROCESS(myQProcess, myPrescription));
                                        em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_ASSIGN_ELEMENT) + ": " + myPrescription.getTitle() + " ID: " + myPrescription.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, myQProcess));
                                        qProcess.getAttachedPrescriptionConnections().add(myLinkObject);
                                        myPrescription.getAttachedProcessConnections().add(myLinkObject);
                                    }
                                }

                                em.getTransaction().commit();

                                lstPrescriptions.remove(prescription);
                                lstPrescriptions.add(myPrescription);
                                Collections.sort(lstPrescriptions);
                                final CollapsiblePane myCP = createCP4(myPrescription);
                                buildPanel();
                                GUITools.flashBackground(myCP, Color.YELLOW, 2);

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
            cptitle.getRight().add(btnProcess);
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
                JPanel pnl = getMenu(prescription);
                popup.getContentPane().add(pnl);
                popup.setDefaultFocusComponent(pnl);

                GUITools.showPopup(popup, SwingConstants.WEST);
            }
        });
        cptitle.getRight().add(btnMenu);

        cpPres.setHorizontalAlignment(SwingConstants.LEADING);
        cpPres.setOpaque(false);


        return cpPres;
    }

    private Icon getIcon(Prescription mypres) {
        Icon icon;

        if (mypres.isClosed()) {
            icon = SYSConst.icon22stopSign;
        } else if (mypres.isOnDemand()) {
            icon = null;
            if (mypres.shouldBeCalculated()) {
                MedInventory inventory = TradeFormTools.getInventory4TradeForm(mypres.getResident(), mypres.getTradeForm());
                MedStock stockInUse = MedStockTools.getStockInUse(inventory);
                if (stockInUse == null) {
                    icon = SYSConst.icon22ledRedOn;
                } else {
                    if (MedStockTools.getSum(stockInUse).compareTo(BigDecimal.ZERO) <= 0) {
                        icon = SYSConst.icon22ledYellowOn;
                    } else {
                        icon = null;
                    }
                }
            }
        } else {
            icon = null;
            if (mypres.shouldBeCalculated()) {
                MedInventory inventory = TradeFormTools.getInventory4TradeForm(mypres.getResident(), mypres.getTradeForm());
                MedStock stockInUse = MedStockTools.getStockInUse(inventory);
                if (stockInUse == null) {
                    icon = SYSConst.icon22ledRedOn;
                } else {
                    if (MedStockTools.getSum(stockInUse).compareTo(BigDecimal.ZERO) <= 0) {
                        icon = SYSConst.icon22ledYellowOn;
                    } else {
                        icon = null;
                    }
                }
            }
        }
        return icon;
    }


    private Color getColor(int level, boolean odd) {
        if (odd) {
            return color1[level];
        } else {
            return color2[level];
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspPrescription = new JScrollPane();
        cpsPrescription = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspPrescription ========
        {
            jspPrescription.setToolTipText("");

            //======== cpsPrescription ========
            {
                cpsPrescription.setLayout(new BoxLayout(cpsPrescription, BoxLayout.X_AXIS));
            }
            jspPrescription.setViewportView(cpsPrescription);
        }
        add(jspPrescription);
    }// </editor-fold>//GEN-END:initComponents

    public void cleanup() {
        SYSTools.clear(cpMap);
        cpsPrescription.removeAll();
        SYSTools.clear(lstPrescriptions);
        SYSTools.clear(lstVisiblePrescriptions);
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
        GUITools.addAllComponents(mypanel, addFilters());
        GUITools.addAllComponents(mypanel, addKey());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }

    private java.util.List<Component> addKey() {
        java.util.List<Component> list = new ArrayList<Component>();
        list.add(new JSeparator());
        list.add(new JLabel(OPDE.lang.getString("misc.msg.key")));
        list.add(new JLabel(OPDE.lang.getString(internalClassID + ".keydescription1"), SYSConst.icon22stopSign, SwingConstants.LEADING));
        list.add(new JLabel(OPDE.lang.getString(internalClassID + ".keydescription2"), SYSConst.icon22ledYellowOn, SwingConstants.LEADING));
        list.add(new JLabel(OPDE.lang.getString(internalClassID + ".keydescription3"), SYSConst.icon22ledRedOn, SwingConstants.LEADING));

        return list;
    }

    private java.util.List<Component> addFilters() {
        java.util.List<Component> list = new ArrayList<Component>();

        tbClosed = GUITools.getNiceToggleButton(internalClassID + ".showclosed");
        tbClosed.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                reloadDisplay();
            }
        });
        tbClosed.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbClosed);

        return list;
    }

    private java.util.List<Component> addCommands() {
        java.util.List<Component> list = new ArrayList<Component>();

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            JideButton addRegular = GUITools.createHyperlinkButton(internalClassID + ".btnNewRegular", SYSConst.icon22add, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (!resident.isActive()) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                        return;
                    }
                    new DlgRegular(new Prescription(resident), DlgRegular.MODE_NEW, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                Pair<Prescription, java.util.List<PrescriptionSchedule>> returnPackage = (Pair<Prescription, List<PrescriptionSchedule>>) o;
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    Prescription myPrescription = em.merge(returnPackage.getFirst());
                                    myPrescription.setRelation(UniqueTools.getNewUID(em, "__verkenn").getUid());
                                    BHPTools.generate(em, myPrescription.getPrescriptionSchedule(), new DateMidnight(), true);
                                    em.getTransaction().commit();

                                    lstPrescriptions.add(myPrescription);
                                    Collections.sort(lstPrescriptions);
                                    final CollapsiblePane myCP = createCP4(myPrescription);
                                    buildPanel();
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            GUITools.scroll2show(jspPrescription, myCP.getLocation().y - 100, new Closure() {
                                                @Override
                                                public void execute(Object o) {
                                                    GUITools.flashBackground(myCP, Color.YELLOW, 2);
                                                }
                                            });
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
                                buildPanel();
                            }
                        }
                    });
                }
            });
            list.add(addRegular);
        }

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            JideButton addNewOnDemand = GUITools.createHyperlinkButton(internalClassID + ".btnNewOnDemand", SYSConst.icon22add, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (!resident.isActive()) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                        return;
                    }
                    new DlgOnDemand(new Prescription(resident), new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {

                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    Prescription myPrescription = em.merge((Prescription) o);
                                    myPrescription.setRelation(UniqueTools.getNewUID(em, "__verkenn").getUid());
                                    em.getTransaction().commit();

                                    lstPrescriptions.add(myPrescription);
                                    Collections.sort(lstPrescriptions);
                                    final CollapsiblePane myCP = createCP4(myPrescription);
                                    buildPanel();

                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            GUITools.scroll2show(jspPrescription, myCP.getLocation().y - 100, new Closure() {
                                                @Override
                                                public void execute(Object o) {
                                                    GUITools.flashBackground(myCP, Color.YELLOW, 2);
                                                }
                                            });
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
                                buildPanel();
                            }
                        }
                    });
                }
            });
            list.add(addNewOnDemand);
        }

        if (resident.isCalcMediUPR1() && OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            JideButton buchenButton = GUITools.createHyperlinkButton(internalClassID + ".newstocks", new ImageIcon(getClass().getResource("/artwork/22x22/shetaddrow.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (!resident.isActive()) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                        return;
                    }
                    new DlgNewStocks(resident);
                    reload();
                }
            });
            list.add(buchenButton);
        }

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
            JideButton printPrescription = GUITools.createHyperlinkButton(internalClassID + ".print", SYSConst.icon22print2, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    SYSFilesTools.print(PrescriptionTools.getPrescriptionsAsHTML(lstPrescriptions, true, true, false, tbClosed.isSelected(), true), true);
                }
            });
            list.add(printPrescription);

//            JideButton printDaily = GUITools.createHyperlinkButton(internalClassID + ".printdailyplan", SYSConst.icon22print2, new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    SYSFilesTools.print(PrescriptionTools.printDailyPlan(resident.getStation().getHome()), true);
//                }
//            });
//            list.add(printDaily);
        }

        return list;
    }

    private void buildPanel() {
        cpsPrescription.removeAll();
        cpsPrescription.setLayout(new JideBoxLayout(cpsPrescription, JideBoxLayout.Y_AXIS));

        int i = 0;
        // for the zebra coloring
        for (Prescription prescription : lstPrescriptions) {
//            if (tbClosed.isSelected() || !prescription.isClosed()) {
            cpMap.get(prescription.getID() + ".xprescription").setBackground(getColor(SYSConst.medium1, i % 2 == 1));
            cpsPrescription.add(cpMap.get(prescription.getID() + ".xprescription"));
            i++;
//            }
        }
        cpsPrescription.addExpansion();
    }


    private JPanel getMenu(final Prescription prescription) {

        JPanel pnlMenu = new JPanel(new VerticalLayout());
        long numBHPs = BHPTools.getNumBHPs(prescription);
        final MedInventory inventory = prescription.shouldBeCalculated() ? TradeFormTools.getInventory4TradeForm(prescription.getResident(), prescription.getTradeForm()) : null;
        final MedStock stockInUse = MedStockTools.getStockInUse(inventory);

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            /***
             *       ____ _
             *      / ___| |__   __ _ _ __   __ _  ___
             *     | |   | '_ \ / _` | '_ \ / _` |/ _ \
             *     | |___| | | | (_| | | | | (_| |  __/
             *      \____|_| |_|\__,_|_| |_|\__, |\___|
             *                              |___/
             */
            final JButton btnChange = GUITools.createHyperlinkButton(internalClassID + ".btnChange.tooltip", SYSConst.icon22playerPlay, null);
            btnChange.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnChange.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgRegular(prescription.clone(), DlgRegular.MODE_CHANGE, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {

                                Pair<Prescription, java.util.List<PrescriptionSchedule>> returnPackage = (Pair<Prescription, List<PrescriptionSchedule>>) o;

                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);

                                    // Fetch the new prescription from the PAIR
                                    Prescription newPrescription = em.merge(returnPackage.getFirst());
                                    Prescription oldPrescription = em.merge(prescription);
                                    em.lock(oldPrescription, LockModeType.OPTIMISTIC);

                                    // First close the old prescription
                                    DateTime now = new DateTime();
                                    oldPrescription.setTo(now.toDate());
                                    oldPrescription.setUserOFF(em.merge(OPDE.getLogin().getUser()));
                                    oldPrescription.setDocOFF(newPrescription.getDocON() == null ? null : em.merge(newPrescription.getDocON()));
                                    oldPrescription.setHospitalOFF(newPrescription.getHospitalON() == null ? null : em.merge(newPrescription.getHospitalON()));

                                    // the new prescription starts 1 second after the old one closes
                                    newPrescription.setFrom(now.plusSeconds(1).toDate());

                                    // create new BHPs according to the prescription
                                    BHPTools.generate(em, newPrescription.getPrescriptionSchedule(), new DateMidnight(), true);
                                    em.getTransaction().commit();

                                    lstPrescriptions.remove(prescription);
                                    lstPrescriptions.add(oldPrescription);
                                    lstPrescriptions.add(newPrescription);
                                    Collections.sort(lstPrescriptions);

                                    // Refresh Display
                                    createCP4(oldPrescription);
                                    final CollapsiblePane myNewCP = createCP4(newPrescription);
                                    buildPanel();
                                    GUITools.flashBackground(myNewCP, Color.YELLOW, 2);
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
                                buildPanel();
                            }
                        }
                    });
                }
            });
            btnChange.setEnabled(!prescription.isClosed() && !prescription.isOnDemand() && numBHPs != 0);
            pnlMenu.add(btnChange);

            /***
             *      ____  _
             *     / ___|| |_ ___  _ __
             *     \___ \| __/ _ \| '_ \
             *      ___) | || (_) | |_) |
             *     |____/ \__\___/| .__/
             *                    |_|
             */
            final JButton btnStop = GUITools.createHyperlinkButton(internalClassID + ".btnStop.tooltip", SYSConst.icon22playerStop, null);
            btnStop.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnStop.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgDiscontinue(prescription, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    Prescription myPrescription = (Prescription) em.merge(o);
                                    em.lock(myPrescription.getResident(), LockModeType.OPTIMISTIC);
                                    em.lock(myPrescription, LockModeType.OPTIMISTIC);
                                    myPrescription.setTo(new Date());
                                    em.getTransaction().commit();

                                    lstPrescriptions.remove(prescription);
                                    lstPrescriptions.add(myPrescription);
                                    Collections.sort(lstPrescriptions);
                                    final CollapsiblePane myCP = createCP4(myPrescription);

                                    if (!tbClosed.isSelected()) {
                                        tbClosed.setSelected(true);
                                    } else {
                                        buildPanel();
                                    }

                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            GUITools.scroll2show(jspPrescription, myCP.getLocation().y - 100, new Closure() {
                                                @Override
                                                public void execute(Object o) {
                                                    GUITools.flashBackground(myCP, Color.YELLOW, 2);
                                                }
                                            });
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
                                    em.getTransaction().rollback();
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                            }
                        }
                    });
                }
            });
            btnStop.setEnabled(!prescription.isClosed() && numBHPs != 0);
            pnlMenu.add(btnStop);


            /***
             *      _____    _ _ _
             *     | ____|__| (_) |_
             *     |  _| / _` | | __|
             *     | |__| (_| | | |_
             *     |_____\__,_|_|\__/
             *
             */
            final JButton btnEdit = GUITools.createHyperlinkButton(internalClassID + ".btnEdit.tooltip", SYSConst.icon22edit3, null);
            btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (prescription.isOnDemand()) {
                        new DlgOnDemand(prescription, new Closure() {
                            @Override
                            public void execute(Object o) {
                                if (o != null) {

                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();
                                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                        Prescription myPrescription = em.merge((Prescription) o);
                                        em.lock(myPrescription, LockModeType.OPTIMISTIC);

                                        Query queryDELBHP = em.createQuery("DELETE FROM BHP bhp WHERE bhp.prescription = :prescription");
                                        queryDELBHP.setParameter("prescription", myPrescription);
                                        queryDELBHP.executeUpdate();

                                        em.getTransaction().commit();

                                        lstPrescriptions.remove(prescription);
                                        lstPrescriptions.add(myPrescription);
                                        Collections.sort(lstPrescriptions);
                                        final CollapsiblePane myCP = createCP4(myPrescription);
                                        buildPanel();

                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                GUITools.scroll2show(jspPrescription, myCP.getLocation().y - 100, new Closure() {
                                                    @Override
                                                    public void execute(Object o) {
                                                        GUITools.flashBackground(myCP, Color.YELLOW, 2);
                                                    }
                                                });
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
                                    buildPanel();
                                }
                            }
                        });
                    } else {
                        new DlgRegular(prescription, DlgRegular.MODE_EDIT, new Closure() {
                            @Override
                            public void execute(Object o) {
                                if (o != null) {

                                    Pair<Prescription, java.util.List<PrescriptionSchedule>> returnPackage = (Pair<Prescription, List<PrescriptionSchedule>>) o;

                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();
                                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                        Prescription myPrescription = em.merge(returnPackage.getFirst());
                                        em.lock(myPrescription, LockModeType.OPTIMISTIC);

                                        // delete whats not in the new prescription anymore
                                        for (PrescriptionSchedule schedule : returnPackage.getSecond()) {
                                            em.remove(em.merge(schedule));
                                        }

                                        Query queryDELBHP = em.createQuery("DELETE FROM BHP bhp WHERE bhp.prescription = :prescription");
                                        queryDELBHP.setParameter("prescription", myPrescription);
                                        queryDELBHP.executeUpdate();

                                        BHPTools.generate(em, myPrescription.getPrescriptionSchedule(), new DateMidnight(), true);

                                        em.getTransaction().commit();

                                        lstPrescriptions.remove(prescription);
                                        lstPrescriptions.add(myPrescription);
                                        Collections.sort(lstPrescriptions);
                                        final CollapsiblePane myCP = createCP4(myPrescription);
                                        buildPanel();

                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                GUITools.scroll2show(jspPrescription, myCP.getLocation().y - 100, new Closure() {
                                                    @Override
                                                    public void execute(Object o) {
                                                        GUITools.flashBackground(myCP, Color.YELLOW, 2);
                                                    }
                                                });
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
                                    buildPanel();
                                }
                            }
                        });
                    }
                }

            });
            btnEdit.setEnabled(!prescription.isClosed() && numBHPs == 0);
            pnlMenu.add(btnEdit);
        }
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.DELETE, internalClassID)) {
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

                    new DlgYesNo(OPDE.lang.getString("misc.questions.delete1") + "<br/>" + PrescriptionTools.toPrettyString(prescription) + "</br>" + OPDE.lang.getString("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                        @Override
                        public void execute(Object answer) {
                            if (answer.equals(JOptionPane.YES_OPTION)) {

                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    Prescription myverordnung = em.merge(prescription);
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    em.lock(myverordnung, LockModeType.OPTIMISTIC);
                                    em.remove(myverordnung);

                                    Query delQuery = em.createQuery("DELETE FROM BHP b WHERE b.prescription = :prescription");
                                    delQuery.setParameter("prescription", myverordnung);
                                    delQuery.executeUpdate();
                                    em.getTransaction().commit();

                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.Deleted") + ": " + PrescriptionTools.toPrettyString(myverordnung)));
                                    lstPrescriptions.remove(prescription);
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
                                    em.getTransaction().rollback();
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                            }
                        }
                    });
                }

            });
            btnDelete.setEnabled(numBHPs == 0 && !prescription.isClosed());
            pnlMenu.add(btnDelete);
        }
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            pnlMenu.add(new JSeparator());

            /***
             *       ____ _                ____  _             _
             *      / ___| | ___  ___  ___/ ___|| |_ ___   ___| | __
             *     | |   | |/ _ \/ __|/ _ \___ \| __/ _ \ / __| |/ /
             *     | |___| | (_) \__ \  __/___) | || (_) | (__|   <
             *      \____|_|\___/|___/\___|____/ \__\___/ \___|_|\_\
             *
             */
            final JButton btnCloseStock = GUITools.createHyperlinkButton(PnlInventory.internalClassID + ".stock.btnout.tooltip", SYSConst.icon22ledRedOn, null);
            btnCloseStock.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnCloseStock.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgCloseStock(stockInUse, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                // The prescription itself is not changed, but the stock in question.
                                // this information is requested by a single DB request every time
                                // the CP is created for that particular prescription.
                                // A new call to the createCP4 method will reuse the old
                                // CollapsiblePane and set a new TextContent to it.
                                // Now with the MedStock information.

                                // If this current stock was valid until the end of package
                                // it needs to be reread here.
                                if (prescription.isUntilEndOfPackage()) {
                                    EntityManager em = OPDE.createEM();
                                    Prescription myPrescription = em.merge(prescription);
                                    em.refresh(myPrescription);
                                    lstPrescriptions.remove(prescription);
                                    lstPrescriptions.add(myPrescription);
                                    Collections.sort(lstPrescriptions);
                                    final CollapsiblePane myCP = createCP4(myPrescription);
                                } else {
                                    final CollapsiblePane myCP = createCP4(prescription);
                                    GUITools.flashBackground(myCP, Color.YELLOW, 2);
                                }
                                buildPanel();
                            }
                        }
                    });
                }
            });
            btnCloseStock.setEnabled(inventory != null && stockInUse != null && !stockInUse.isToBeClosedSoon());
            pnlMenu.add(btnCloseStock);

            /***
             *       ___                   ____  _             _
             *      / _ \ _ __   ___ _ __ / ___|| |_ ___   ___| | __
             *     | | | | '_ \ / _ \ '_ \\___ \| __/ _ \ / __| |/ /
             *     | |_| | |_) |  __/ | | |___) | || (_) | (__|   <
             *      \___/| .__/ \___|_| |_|____/ \__\___/ \___|_|\_\
             *           |_|
             */
            final JButton btnOpenStock = GUITools.createHyperlinkButton(PnlInventory.internalClassID + ".stock.btnopen.tooltip", SYSConst.icon22ledGreenOn, null);
            btnOpenStock.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnOpenStock.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgOpenStock(prescription.getTradeForm(), resident, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                final CollapsiblePane myCP = createCP4(prescription);
                                GUITools.flashBackground(myCP, Color.YELLOW, 2);
                            }
                        }
                    });
                }
            });
            btnOpenStock.setEnabled(inventory != null && stockInUse == null && !prescription.isClosed());
            pnlMenu.add(btnOpenStock);

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
                    Closure closure = null;
                    if (!prescription.isClosed()) {
                        closure = new Closure() {
                            @Override
                            public void execute(Object o) {
                                EntityManager em = OPDE.createEM();
                                Prescription myPrescription = em.merge(prescription);
                                em.refresh(myPrescription);
                                em.close();
                                lstPrescriptions.remove(prescription);
                                lstPrescriptions.add(myPrescription);
                                Collections.sort(lstPrescriptions);
                                final CollapsiblePane myCP = createCP4(myPrescription);
                                buildPanel();
                                GUITools.flashBackground(myCP, Color.YELLOW, 2);
                            }
                        };
                    }
                    btnFiles.setEnabled(OPDE.isFTPworking());
                    new DlgFiles(prescription, closure);
                }
            });

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
                    new DlgProcessAssign(prescription, new Closure() {
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
                                Prescription myPrescription = em.merge(prescription);
                                em.lock(myPrescription, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                                ArrayList<SYSPRE2PROCESS> attached = new ArrayList<SYSPRE2PROCESS>(prescription.getAttachedProcessConnections());
                                for (SYSPRE2PROCESS linkObject : attached) {
                                    if (unassigned.contains(linkObject.getQProcess())) {
                                        linkObject.getQProcess().getAttachedNReportConnections().remove(linkObject);
                                        linkObject.getPrescription().getAttachedProcessConnections().remove(linkObject);
                                        em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + myPrescription.getTitle() + " ID: " + myPrescription.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, linkObject.getQProcess()));
                                        em.remove(linkObject);
                                    }
                                }
                                attached.clear();

                                for (QProcess qProcess : assigned) {
                                    List<QProcessElement> listElements = qProcess.getElements();
                                    if (!listElements.contains(myPrescription)) {
                                        QProcess myQProcess = em.merge(qProcess);
                                        SYSPRE2PROCESS myLinkObject = em.merge(new SYSPRE2PROCESS(myQProcess, myPrescription));
                                        em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_ASSIGN_ELEMENT) + ": " + myPrescription.getTitle() + " ID: " + myPrescription.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, myQProcess));
                                        qProcess.getAttachedPrescriptionConnections().add(myLinkObject);
                                        myPrescription.getAttachedProcessConnections().add(myLinkObject);
                                    }
                                }

                                em.getTransaction().commit();

                                lstPrescriptions.remove(prescription);
                                lstPrescriptions.add(myPrescription);
                                Collections.sort(lstPrescriptions);
                                final CollapsiblePane myCP = createCP4(myPrescription);
                                buildPanel();
                                GUITools.flashBackground(myCP, Color.YELLOW, 2);

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
            btnProcess.setEnabled(!prescription.isClosed());

//            if (!prescription.getAttachedProcessConnections().isEmpty()) {
//                JLabel lblNum = new JLabel(Integer.toString(prescription.getAttachedProcessConnections().size()), SYSConst.icon16redStar, SwingConstants.CENTER);
//                lblNum.setFont(SYSConst.ARIAL10BOLD);
//                lblNum.setForeground(Color.YELLOW);
//                lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
//                DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnProcess, lblNum, DefaultOverlayable.SOUTH_EAST);
//                overlayableBtn.setOpaque(false);
//                pnlMenu.add(overlayableBtn);
//            } else {
            pnlMenu.add(btnProcess);
//            }
        }
        return pnlMenu;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspPrescription;
    private CollapsiblePanes cpsPrescription;
    // End of variables declaration//GEN-END:variables


}
