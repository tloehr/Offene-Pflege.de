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
import entity.info.ResInfo;
import entity.info.ResInfoTools;
import entity.info.Resident;
import entity.prescription.*;
import entity.process.*;
import entity.system.Commontags;
import entity.system.CommontagsTools;
import entity.system.UniqueTools;
import gui.GUITools;
import gui.interfaces.DefaultCPTitle;
import op.OPDE;
import op.care.med.inventory.DlgCloseStock;
import op.care.med.inventory.DlgNewStocks;
import op.care.med.inventory.DlgOpenStock;
import op.care.med.inventory.PnlExpiry;
import op.care.sysfiles.DlgFiles;
import op.process.DlgProcessAssign;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

/**
 * @author tloehr
 */
public class PnlPrescription extends NursingRecordsPanel {


    private Resident resident;

    private ArrayList<Prescription> lstPrescriptions, lstVisiblePrescriptions; // <= the latter is only for the zebra pattern
    private HashMap<String, CollapsiblePane> cpMap;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JToggleButton tbClosed;

    private Color[] color1, color2;

    private List<Commontags> listUsedCommontags;

    /**
     * Creates new form PnlPrescription
     */
    public PnlPrescription(Resident resident, JScrollPane jspSearch) {
        super("nursingrecords.prescription");
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
        listUsedCommontags = new ArrayList<Commontags>();
        prepareSearchArea();
    }

    @Override
    public void switchResident(Resident res) {
        this.resident = EntityTools.find(Resident.class, res.getRID());
        listUsedCommontags.clear();
        listUsedCommontags.addAll(CommontagsTools.getAllUsedInPrescription(resident));
        GUITools.setResidentDisplay(resident);
        prepareSearchArea();
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
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

            SwingWorker worker = new SwingWorker() {

                @Override
                protected Object doInBackground() throws Exception {
                    int progress = -1;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, lstPrescriptions.size()));

                    if (tbClosed.isSelected()) {
                        lstPrescriptions = PrescriptionTools.getAll(resident);
                    } else {
                        lstPrescriptions = PrescriptionTools.getAllActive(resident);
                    }
                    Collections.sort(lstPrescriptions);

                    for (Prescription prescription : lstPrescriptions) {
                        progress++;
                        createCP4(prescription);
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, lstPrescriptions.size()));
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
                PrescriptionTools.getDoseAsHTML(prescription) +
                PrescriptionTools.getInventoryInformationAsHTML(prescription) +
                "</td>" +
                "<td width=\"200\" align=\"left\">" +
                PrescriptionTools.getOriginalPrescription(prescription) +
                PrescriptionTools.getRemark(prescription) +
                "</td>";

        if (!prescription.getCommontags().isEmpty()) {
            title += "<tr>" +
                    "    <td colspan=\"3\">" + CommontagsTools.getAsHTML(prescription.getCommontags(), SYSConst.html_16x16_tagPurple_internal) + "</td>" +
                    "  </tr>";
        }

        if (PrescriptionTools.isAnnotationNecessary(prescription)) {
            title += "<tr>" +
                    "    <td colspan=\"3\">" + PrescriptionTools.getAnnontationsAsHTML(prescription) + "</td>" +
                    "  </tr>";
        }


        title += "</table>" + "</html>";

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
            btnFiles.setToolTipText(SYSTools.xx("misc.btnfiles.tooltip"));
            btnFiles.setForeground(Color.BLUE);
            btnFiles.setHorizontalTextPosition(SwingUtilities.CENTER);
            btnFiles.setFont(SYSConst.ARIAL18BOLD);
            btnFiles.setPressedIcon(SYSConst.icon22Pressed);
            btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnFiles.setAlignmentY(Component.TOP_ALIGNMENT);
            btnFiles.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnFiles.setContentAreaFilled(false);
            btnFiles.setBorder(null);

            btnFiles.addActionListener(actionEvent -> {
                // checked for acls
                Closure fileHandleClosure = OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID) ? null : o -> {
                    EntityManager em = OPDE.createEM();
                    final Prescription myPrescription = em.find(Prescription.class, prescription.getID());
                    em.close();
                    lstPrescriptions.remove(prescription);
                    lstPrescriptions.add(myPrescription);
                    Collections.sort(lstPrescriptions);
                    final CollapsiblePane myCP = createCP4(myPrescription);
                    currentEditor = null;
                    buildPanel();
                    GUITools.flashBackground(myCP, Color.YELLOW, 2);
                };
                currentEditor = new DlgFiles(prescription, fileHandleClosure);
                currentEditor.setVisible(true);
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
            btnProcess.setToolTipText(SYSTools.xx("misc.btnprocess.tooltip"));
            btnProcess.setForeground(Color.YELLOW);
            btnProcess.setHorizontalTextPosition(SwingUtilities.CENTER);
            btnProcess.setFont(SYSConst.ARIAL18BOLD);
            btnProcess.setPressedIcon(SYSConst.icon22Pressed);
            btnProcess.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnProcess.setAlignmentY(Component.TOP_ALIGNMENT);
            btnProcess.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnProcess.setContentAreaFilled(false);
            btnProcess.setBorder(null);
            btnProcess.addActionListener(actionEvent -> {
                currentEditor = new DlgProcessAssign(prescription, o -> {
                    if (o == null) {
                        currentEditor = null;
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
                                em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + myPrescription.getTitle() + " ID: " + myPrescription.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, linkObject.getQProcess()));
                                em.remove(linkObject);
                            }
                        }
                        attached.clear();


                        for (QProcess qProcess : assigned) {
                            List<QProcessElement> listElements = qProcess.getElements();
                            if (!listElements.contains(myPrescription)) {
                                QProcess myQProcess = em.merge(qProcess);
                                SYSPRE2PROCESS myLinkObject = em.merge(new SYSPRE2PROCESS(myQProcess, myPrescription));
                                em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_ASSIGN_ELEMENT) + ": " + myPrescription.getTitle() + " ID: " + myPrescription.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, myQProcess));
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
                        OPDE.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
                        }
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    } catch (RollbackException ole) {
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
                        currentEditor = null;
                    }

                });
                currentEditor.setVisible(true);
            });
            // checked for acls
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
        btnMenu.addActionListener(e -> {
            JidePopup popup = new JidePopup();
            popup.setMovable(false);
            popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
            popup.setOwner(btnMenu);
            popup.removeExcludedComponent(btnMenu);
            JPanel pnl = getMenu(prescription);
            popup.getContentPane().add(pnl);
            popup.setDefaultFocusComponent(pnl);

            GUITools.showPopup(popup, SwingConstants.WEST);
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
        } else {
            icon = null;
            if (mypres.shouldBeCalculated()) {
                MedInventory inventory = TradeFormTools.getInventory4TradeForm(mypres.getResident(), mypres.getTradeForm());
                MedStock stockInUse = MedStockTools.getStockInUse(inventory);
                if (stockInUse == null) {
                    icon = SYSConst.icon22ledRedOn;
                } else if (stockInUse.isExpired()) {
                    icon = SYSConst.icon22ledOrangeOn;
                } else if (stockInUse.expiresIn(MedStockTools.DAYS_TO_EXPIRE_SOON)) {
                    icon = SYSConst.icon22ledOrangeOff;
                } else if (!stockInUse.getTradeForm().getDosageForm().isDontCALC() && MedStockTools.getSum(stockInUse).compareTo(BigDecimal.ZERO) <= 0) {
                    icon = SYSConst.icon22ledYellowOn;
                } else {
                    icon = null;
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
        super.cleanup();
        SYSTools.clear(cpMap);
        cpsPrescription.removeAll();
        SYSTools.clear(lstPrescriptions);
        SYSTools.clear(lstVisiblePrescriptions);

        SYSTools.clear(listUsedCommontags);

    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(3));
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

    private java.util.List<Component> addKey() {
        java.util.List<Component> list = new ArrayList<Component>();
        list.add(new JSeparator());
        list.add(new JLabel(SYSTools.xx("misc.msg.key")));
        list.add(new JLabel(SYSTools.xx("nursingrecords.prescription.keydescription1"), SYSConst.icon22stopSign, SwingConstants.LEADING));
//        if (resident.isCalcMediUPR1()) {
        list.add(new JLabel(SYSTools.xx("nursingrecords.prescription.keydescription2"), SYSConst.icon22ledYellowOn, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.prescription.keydescription3"), SYSConst.icon22ledRedOn, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.prescription.keydescription5"), SYSConst.icon22ledOrangeOff, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.prescription.keydescription4"), SYSConst.icon22ledOrangeOn, SwingConstants.LEADING));
//        }
        return list;
    }

    private java.util.List<Component> addFilters() {
        java.util.List<Component> list = new ArrayList<Component>();

        tbClosed = GUITools.getNiceToggleButton("nursingrecords.prescription.showclosed");
        tbClosed.addItemListener(e -> reloadDisplay());
        tbClosed.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbClosed);

        if (!listUsedCommontags.isEmpty()) {

            JPanel pnlTags = new JPanel();
            pnlTags.setLayout(new BoxLayout(pnlTags, BoxLayout.Y_AXIS));
            pnlTags.setOpaque(false);

            for (final Commontags commontag : listUsedCommontags) {
                final JButton btnTag = GUITools.createHyperlinkButton(commontag.getText(), SYSConst.icon16tagPurple, e -> SYSFilesTools.print(PrescriptionTools.getPrescriptionsAsHTML(PrescriptionTools.getPrescriptions4Tags(resident, commontag), true, true, false, tbClosed.isSelected(), true), true));
                btnTag.setForeground(GUITools.getColor(commontag.getColor()));
                pnlTags.add(btnTag);
            }
            list.add(pnlTags);
        }

        return list;
    }

    private java.util.List<Component> addCommands() {
        java.util.List<Component> list = new ArrayList<Component>();

        // checked for acls
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            JideButton addRegular = GUITools.createHyperlinkButton("nursingrecords.prescription.btnNewRegular", SYSConst.icon22add, actionEvent -> {
                if (!resident.isActive()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                    return;
                }
                currentEditor = new DlgRegular(new Prescription(resident), DlgRegular.MODE_NEW, o -> {
                    if (o != null) {
                        Pair<Prescription, List<PrescriptionSchedule>> returnPackage = (Pair<Prescription, List<PrescriptionSchedule>>) o;
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                            Prescription myPrescription = em.merge(returnPackage.getFirst());
                            myPrescription.setRelation(UniqueTools.getNewUID(em, "__verkenn").getUid());
                            BHPTools.generate(em, myPrescription.getPrescriptionSchedule(), new LocalDate(), true);
                            em.getTransaction().commit();

                            lstPrescriptions.add(myPrescription);
                            Collections.sort(lstPrescriptions);
                            final CollapsiblePane myCP = createCP4(myPrescription);
                            buildPanel();
                            SwingUtilities.invokeLater(() -> GUITools.scroll2show(jspPrescription, myCP.getLocation().y - 100, o12 -> GUITools.flashBackground(myCP, Color.YELLOW, 2)));
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
                        buildPanel();
                    }
                    currentEditor = null;
                });
                currentEditor.setVisible(true);
            });
            list.add(addRegular);
        }

        // checked for acls
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            JideButton addNewOnDemand = GUITools.createHyperlinkButton("nursingrecords.prescription.btnNewOnDemand", SYSConst.icon22add, actionEvent -> {
                if (!resident.isActive()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                    return;
                }
                currentEditor = new DlgOnDemand(new Prescription(resident), o -> {
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

                            SwingUtilities.invokeLater(() -> GUITools.scroll2show(jspPrescription, myCP.getLocation().y - 100, o1 -> GUITools.flashBackground(myCP, Color.YELLOW, 2)));

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
                        buildPanel();
                    }
                    currentEditor = null;
                });
                currentEditor.setVisible(true);
            });

            list.add(addNewOnDemand);
        }

        // checked for acls
        if (resident.isCalcMediUPR1() && OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            JideButton buchenButton = GUITools.createHyperlinkButton("nursingrecords.prescription.newstocks", new ImageIcon(getClass().getResource("/artwork/22x22/shetaddrow.png")), actionEvent -> {
                if (!resident.isActive()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                    return;
                }
                currentEditor = new DlgNewStocks(resident);
                currentEditor.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        currentEditor = null;
                    }
                });
                currentEditor.setVisible(true);
                reload();
            });
            list.add(buchenButton);
        }

        // checked for acls
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
            JideButton printPrescription = GUITools.createHyperlinkButton("nursingrecords.prescription.print", SYSConst.icon22print2, actionEvent -> SYSFilesTools.print(PrescriptionTools.getPrescriptionsAsHTML(lstPrescriptions, true, true, false, tbClosed.isSelected(), true), true));
            list.add(printPrescription);

//            JideButton printDaily = GUITools.createHyperlinkButton("nursingrecords.prescription.printdailyplan", SYSConst.icon22print2, new ActionListener() {
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
            OPDE.debug(prescription.getID() + ".xprescription");


            cpMap.get(prescription.getID() + ".xprescription").setBackground(getColor(SYSConst.medium1, i % 2 == 1));
            cpsPrescription.add(cpMap.get(prescription.getID() + ".xprescription"));
            i++;
//            }
        }
        cpsPrescription.addExpansion();
    }


    private JPanel getMenu(final Prescription prescription) {

        JPanel pnlMenu = new JPanel(new VerticalLayout());
        long numBHPs = BHPTools.getConfirmedBHPs(prescription);
        final MedInventory inventory = prescription.shouldBeCalculated() ? TradeFormTools.getInventory4TradeForm(prescription.getResident(), prescription.getTradeForm()) : null;
        final MedStock stockInUse = MedStockTools.getStockInUse(inventory);

        // checked for acls
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            /***
             *       ____ _
             *      / ___| |__   __ _ _ __   __ _  ___
             *     | |   | '_ \ / _` | '_ \ / _` |/ _ \
             *     | |___| | | | (_| | | | | (_| |  __/
             *      \____|_| |_|\__,_|_| |_|\__, |\___|
             *                              |___/
             */
            final JButton btnChange = GUITools.createHyperlinkButton("nursingrecords.prescription.btnChange.tooltip", SYSConst.icon22playerPlay, null);
            btnChange.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnChange.addActionListener(actionEvent -> {

                currentEditor = new DlgRegular(prescription.clone(), DlgRegular.MODE_CHANGE, o -> {
                    if (o != null) {

                        Pair<Prescription, List<PrescriptionSchedule>> returnPackage = (Pair<Prescription, List<PrescriptionSchedule>>) o;

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
                            BHPTools.generate(em, newPrescription.getPrescriptionSchedule(), new LocalDate(), true);
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
                    currentEditor = null;
                });
                currentEditor.setVisible(true);
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
            final JButton btnStop = GUITools.createHyperlinkButton("nursingrecords.prescription.btnStop.tooltip", SYSConst.icon22playerStop, null);
            btnStop.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnStop.addActionListener(actionEvent -> {
                currentEditor = new DlgDiscontinue(prescription, o -> {
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

                            buildPanel();


                            SwingUtilities.invokeLater(() -> GUITools.scroll2show(jspPrescription, myCP.getLocation().y - 100, o14 -> GUITools.flashBackground(myCP, Color.YELLOW, 2)));

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
                            em.getTransaction().rollback();
                            OPDE.fatal(e);
                        } finally {
                            em.close();
                        }
                    }
                    currentEditor = null;
                });
                currentEditor.setVisible(true);
            });
            btnStop.setEnabled(!prescription.isClosed()); //  && numBHPs != 0
            pnlMenu.add(btnStop);


            /***
             *      _____    _ _ _
             *     | ____|__| (_) |_
             *     |  _| / _` | | __|
             *     | |__| (_| | | |_
             *     |_____\__,_|_|\__/
             *
             */
            final JButton btnEdit = GUITools.createHyperlinkButton("nursingrecords.prescription.btnEdit.tooltip", SYSConst.icon22edit3, null);
            btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnEdit.addActionListener(actionEvent -> {
                if (prescription.isOnDemand()) {
                    currentEditor = new DlgOnDemand(prescription, o -> {
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

                                synchronized (listUsedCommontags) {
                                    boolean reloadSearch = false;
                                    for (Commontags ctag : myPrescription.getCommontags()) {
                                        if (!listUsedCommontags.contains(ctag)) {
                                            listUsedCommontags.add(ctag);
                                            reloadSearch = true;
                                        }
                                    }
                                    if (reloadSearch) {
                                        prepareSearchArea();
                                    }
                                }
                                SwingUtilities.invokeLater(() -> GUITools.scroll2show(jspPrescription, myCP.getLocation().y - 100, o13 -> GUITools.flashBackground(myCP, Color.YELLOW, 2)));

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
//                                    buildPanel();
                        }
                        currentEditor = null;
                    });
                } else {
                    currentEditor = new DlgRegular(prescription, DlgRegular.MODE_EDIT, o -> {
                        if (o != null) {

                            Pair<Prescription, List<PrescriptionSchedule>> returnPackage = (Pair<Prescription, List<PrescriptionSchedule>>) o;

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

                                BHPTools.generate(em, myPrescription.getPrescriptionSchedule(), new LocalDate(), true);

                                em.getTransaction().commit();

                                lstPrescriptions.remove(prescription);
                                lstPrescriptions.add(myPrescription);
                                Collections.sort(lstPrescriptions);
                                final CollapsiblePane myCP = createCP4(myPrescription);
                                buildPanel();

                                synchronized (listUsedCommontags) {
                                    boolean reloadSearch = false;
                                    for (Commontags ctag : myPrescription.getCommontags()) {
                                        if (!listUsedCommontags.contains(ctag)) {
                                            listUsedCommontags.add(ctag);
                                            reloadSearch = true;
                                        }
                                    }
                                    if (reloadSearch) {
                                        prepareSearchArea();
                                    }
                                }

                                SwingUtilities.invokeLater(() -> GUITools.scroll2show(jspPrescription, myCP.getLocation().y - 100, o12 -> GUITools.flashBackground(myCP, Color.YELLOW, 2)));

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
//                                    buildPanel();
                        }
                        currentEditor = null;
                    });
                }
                currentEditor.setVisible(true);
            });
            btnEdit.setEnabled(prescription.isMine() && !prescription.isClosed() && numBHPs == 0);
            pnlMenu.add(btnEdit);


            /***
             *      _     _       _____  _    ____
             *     | |__ | |_ _ _|_   _|/ \  / ___|___
             *     | '_ \| __| '_ \| | / _ \| |  _/ __|
             *     | |_) | |_| | | | |/ ___ \ |_| \__ \
             *     |_.__/ \__|_| |_|_/_/   \_\____|___/
             *
             */
            final JButton btnTAGs = GUITools.createHyperlinkButton("misc.msg.editTags", SYSConst.icon22tagPurple, null);
            btnTAGs.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnTAGs.addActionListener(actionEvent -> {
                final JidePopup popup = new JidePopup();

                final JPanel pnl = new JPanel(new BorderLayout(5, 5));
                final PnlCommonTags pnlCommonTags = new PnlCommonTags(prescription.getCommontags(), true, 3);
                pnl.add(new JScrollPane(pnlCommonTags), BorderLayout.CENTER);
                JButton btnApply = new JButton(SYSConst.icon22apply);
                pnl.add(btnApply, BorderLayout.SOUTH);
                btnApply.addActionListener(ae -> {
                    EntityManager em = OPDE.createEM();
                    try {

                        em.getTransaction().begin();
                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                        Prescription myPrescription = em.merge(prescription);
                        em.lock(myPrescription, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                        // merging is important, hence no addAll() for this one
                        ArrayList<Commontags> listTags2Add = new ArrayList<Commontags>();
                        for (Commontags tag2add : pnlCommonTags.getListSelectedTags()) {
                            listTags2Add.add(em.merge(tag2add));
                        }

                        // Annotations need to be added, tooo
                        // these are the remaining tags, that need to be disconnected
                        myPrescription.getCommontags().addAll(listTags2Add);
                        ArrayList<Commontags> listTags2Remove = new ArrayList<Commontags>(myPrescription.getCommontags());
                        listTags2Remove.removeAll(listTags2Add);

                        myPrescription.getCommontags().removeAll(listTags2Remove);

                        ArrayList<ResInfo> annotations2remove = new ArrayList<ResInfo>();
                        for (Commontags commontag : listTags2Remove) {
                            for (ResInfo annotation : myPrescription.getAnnotations()) {
                                if (CommontagsTools.getTagForAnnotation(annotation).equals(commontag)) {
                                    annotations2remove.add(annotation);
                                    em.remove(annotation);
                                }
                            }
                        }
                        myPrescription.getAnnotations().removeAll(annotations2remove);

                        em.getTransaction().commit();

                        lstPrescriptions.remove(prescription);
                        lstPrescriptions.add(myPrescription);
                        Collections.sort(lstPrescriptions);
                        final CollapsiblePane myCP = createCP4(myPrescription);
                        buildPanel();

                        synchronized (listUsedCommontags) {
                            boolean reloadSearch = false;
                            for (Commontags ctag : myPrescription.getCommontags()) {
                                if (!listUsedCommontags.contains(ctag)) {
                                    listUsedCommontags.add(ctag);
                                    reloadSearch = true;
                                }
                            }
                            if (reloadSearch) {
                                prepareSearchArea();
                            }
                        }

                        SwingUtilities.invokeLater(() -> GUITools.scroll2show(jspPrescription, myCP.getLocation().y - 100, o -> GUITools.flashBackground(myCP, Color.YELLOW, 2)));

                    } catch (OptimisticLockException ole) {
                        OPDE.warn(ole);
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
                        } else {
                            reloadDisplay();
                        }
                    } catch (Exception e) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }
                });

                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                popup.setOwner(btnTAGs);
                popup.removeExcludedComponent(btnTAGs);
                pnl.setPreferredSize(new Dimension(350, 150));
                popup.getContentPane().add(pnl);
                popup.setDefaultFocusComponent(pnl);

                GUITools.showPopup(popup, SwingConstants.WEST);

            });
            btnTAGs.setEnabled(prescription.isMine() && !prescription.isClosed());
            pnlMenu.add(btnTAGs);


            /***
             *                              _        _
             *       __ _ _ __  _ __   ___ | |_ __ _| |_ ___
             *      / _` | '_ \| '_ \ / _ \| __/ _` | __/ _ \
             *     | (_| | | | | | | | (_) | || (_| | ||  __/
             *      \__,_|_| |_|_| |_|\___/ \__\__,_|\__\___|
             *
             */

            final JButton btnAnnotation = GUITools.createHyperlinkButton("nursingrecords.prescription.edit.annotations", SYSConst.icon22annotate, null);
            btnAnnotation.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnAnnotation.addActionListener(actionEvent -> {
                currentEditor = new DlgAnnotations(prescription, o -> {
                    if (o != null) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();

                            ResInfo annotation = em.merge((ResInfo) o);

                            annotation.setHtml(ResInfoTools.getContentAsHTML(annotation));

                            Prescription myPrescription = em.merge(prescription);
                            em.lock(myPrescription, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                            myPrescription.getAnnotations().remove(annotation); // just in case, it was an EDIT rather than an ADD
                            myPrescription.getAnnotations().add(annotation);

                            em.lock(annotation, LockModeType.OPTIMISTIC);
                            em.getTransaction().commit();

                            lstPrescriptions.remove(prescription);
                            lstPrescriptions.add(myPrescription);

                            Collections.sort(lstPrescriptions);
                            final CollapsiblePane myCP = createCP4(myPrescription);
                            buildPanel();

                            SwingUtilities.invokeLater(() -> GUITools.scroll2show(jspPrescription, myCP.getLocation().y - 100, o1 -> GUITools.flashBackground(myCP, Color.YELLOW, 2)));

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
            btnAnnotation.setEnabled(prescription.isMine() && !prescription.isClosed() && prescription.hasMed() && PrescriptionTools.isAnnotationNecessary(prescription));
            pnlMenu.add(btnAnnotation);
        }

        // checked for acls
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.DELETE, internalClassID)) {
            /***
             *      ____       _      _
             *     |  _ \  ___| | ___| |_ ___
             *     | | | |/ _ \ |/ _ \ __/ _ \
             *     | |_| |  __/ |  __/ ||  __/
             *     |____/ \___|_|\___|\__\___|
             *
             */
            final JButton btnDelete = GUITools.createHyperlinkButton("nursingrecords.prescription.btnDelete.tooltip", SYSConst.icon22delete, null);
            btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnDelete.addActionListener(actionEvent -> {

                currentEditor = new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/>" + PrescriptionTools.toPrettyString(prescription) + "</br>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, answer -> {
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

                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.Deleted") + ": " + PrescriptionTools.toPrettyString(myverordnung)));
                            lstPrescriptions.remove(prescription);
                            buildPanel();
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
                            em.getTransaction().rollback();
                            OPDE.fatal(e);
                        } finally {
                            em.close();
                            currentEditor = null;
                        }
                    }
                });
                currentEditor.setVisible(true);
            });
            btnDelete.setEnabled(prescription.isMine() && numBHPs == 0 && !prescription.isClosed());
            pnlMenu.add(btnDelete);
        }
        // checked for acls
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            pnlMenu.add(new JSeparator());


            /***
             *      ____       _   _____            _            ____        _
             *     / ___|  ___| |_| ____|_  ___ __ (_)_ __ _   _|  _ \  __ _| |_ ___
             *     \___ \ / _ \ __|  _| \ \/ / '_ \| | '__| | | | | | |/ _` | __/ _ \
             *      ___) |  __/ |_| |___ >  <| |_) | | |  | |_| | |_| | (_| | ||  __/
             *     |____/ \___|\__|_____/_/\_\ .__/|_|_|   \__, |____/ \__,_|\__\___|
             *                               |_|           |___/
             */
            final JButton btnExpiry = GUITools.createHyperlinkButton("nursingrecords.inventory.tooltip.btnSetExpiry", SYSConst.icon22gotoEnd, null);
            btnExpiry.addActionListener(actionEvent -> {
                final JidePopup popup = new JidePopup();
                popup.setMovable(false);

                PnlExpiry pnlExpiry = new PnlExpiry(stockInUse.getExpires(), SYSTools.xx("nursingrecords.inventory.pnlExpiry.title") + ": " + stockInUse.getID(), o -> {
                    popup.hidePopup();

                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        MedStock myStock = em.merge(stockInUse);
                        em.lock(em.merge(myStock.getInventory().getResident()), LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myStock.getInventory()), LockModeType.OPTIMISTIC);
                        myStock.setExpires((Date) o);
                        em.getTransaction().commit();
                        createCP4(prescription);
                        buildPanel();
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

                });
                popup.setOwner(btnExpiry);
                popup.setContentPane(pnlExpiry);
                popup.removeExcludedComponent(pnlExpiry);
                popup.setDefaultFocusComponent(pnlExpiry);
                GUITools.showPopup(popup, SwingConstants.WEST);
            });
            btnExpiry.setEnabled(inventory != null && stockInUse != null);
            pnlMenu.add(btnExpiry);

            /***
             *       ____ _                ____  _             _
             *      / ___| | ___  ___  ___/ ___|| |_ ___   ___| | __
             *     | |   | |/ _ \/ __|/ _ \___ \| __/ _ \ / __| |/ /
             *     | |___| | (_) \__ \  __/___) | || (_) | (__|   <
             *      \____|_|\___/|___/\___|____/ \__\___/ \___|_|\_\
             *
             */
            final JButton btnCloseStock = GUITools.createHyperlinkButton("nursingrecords.inventory.stock.btnout.tooltip", SYSConst.icon22ledRedOn, null);
            btnCloseStock.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnCloseStock.addActionListener(actionEvent -> {
                currentEditor = new DlgCloseStock(stockInUse, o -> {
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
                    currentEditor = null;
                });
                currentEditor.setVisible(true);
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
            final JButton btnOpenStock = GUITools.createHyperlinkButton("nursingrecords.inventory.stock.btnopen.tooltip", SYSConst.icon22ledGreenOn, null);
            btnOpenStock.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnOpenStock.addActionListener(actionEvent -> {
                currentEditor = new DlgOpenStock(prescription.getTradeForm(), resident, o -> {
                    if (o != null) {
                        final CollapsiblePane myCP = createCP4(prescription);
                        GUITools.flashBackground(myCP, Color.YELLOW, 2);
                    }
                    currentEditor = null;
                });
                currentEditor.setVisible(true);
            });
            btnOpenStock.setEnabled(inventory != null && stockInUse == null && !prescription.isClosed());
            pnlMenu.add(btnOpenStock);

            /***
             *      ____  _     _      _____  __  __           _
             *     / ___|(_) __| | ___| ____|/ _|/ _| ___  ___| |_ ___
             *     \___ \| |/ _` |/ _ \  _| | |_| |_ / _ \/ __| __/ __|
             *      ___) | | (_| |  __/ |___|  _|  _|  __/ (__| |_\__ \
             *     |____/|_|\__,_|\___|_____|_| |_|  \___|\___|\__|___/
             *
             */
            final JButton btnEditSideEffects = GUITools.createHyperlinkButton("nursingrecords.prescription.edit.sideeffects", SYSConst.icon22sideeffects, null);
            btnEditSideEffects.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnEditSideEffects.addActionListener(actionEvent -> {
                currentEditor = new DlgYesNo(SYSConst.icon48sideeffects, o -> {
                    if (o != null) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            MedProducts myProduct = em.merge(prescription.getTradeForm().getMedProduct());
                            myProduct.setSideEffects(o.toString().trim());
                            for (TradeForm tf : myProduct.getTradeforms()) {
                                em.lock(em.merge(tf), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                for (MedPackage mp : tf.getPackages()) {
                                    em.lock(em.merge(mp), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                }
                            }
                            em.lock(myProduct, LockModeType.OPTIMISTIC);
                            em.getTransaction().commit();
                            reload();
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
                }, "nursingrecords.prescription.edit.sideeffects", prescription.getTradeForm().getMedProduct().getSideEffects(), null);
                currentEditor.setVisible(true);
            });
            // checked for acls
            btnEditSideEffects.setEnabled(prescription.hasMed() && OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, "opde.medication"));
            pnlMenu.add(btnEditSideEffects);


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
            btnFiles.addActionListener(actionEvent -> {
                Closure closure = null;
                if (!prescription.isClosed()) {
                    closure = o -> {
                        EntityManager em = OPDE.createEM();
                        final Prescription myPrescription = em.find(Prescription.class, prescription.getID());
                        em.close();
                        lstPrescriptions.remove(prescription);
                        lstPrescriptions.add(myPrescription);
                        Collections.sort(lstPrescriptions);
                        final CollapsiblePane myCP = createCP4(myPrescription);
                        buildPanel();
                        GUITools.flashBackground(myCP, Color.YELLOW, 2);
                    };
                }
                btnFiles.setEnabled(OPDE.isFTPworking());
                currentEditor = new DlgFiles(prescription, closure);
                currentEditor.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        currentEditor = null;
                    }
                });
                currentEditor.setVisible(true);
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
            btnProcess.addActionListener(actionEvent -> {
                currentEditor = new DlgProcessAssign(prescription, o -> {
                    if (o == null) {
                        currentEditor = null;
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
                                em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + myPrescription.getTitle() + " ID: " + myPrescription.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, linkObject.getQProcess()));
                                em.remove(linkObject);
                            }
                        }
                        attached.clear();

                        for (QProcess qProcess : assigned) {
                            List<QProcessElement> listElements = qProcess.getElements();
                            if (!listElements.contains(myPrescription)) {
                                QProcess myQProcess = em.merge(qProcess);
                                SYSPRE2PROCESS myLinkObject = em.merge(new SYSPRE2PROCESS(myQProcess, myPrescription));
                                em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_ASSIGN_ELEMENT) + ": " + myPrescription.getTitle() + " ID: " + myPrescription.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, myQProcess));
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
                        OPDE.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
                        }
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    } catch (RollbackException ole) {
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
                        currentEditor = null;
                    }
                });
                currentEditor.setVisible(true);
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
