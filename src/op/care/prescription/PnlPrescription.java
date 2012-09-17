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
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.files.SYSFilesTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.prescription.BHPTools;
import entity.prescription.Prescription;
import entity.prescription.PrescriptionSchedule;
import entity.prescription.PrescriptionTools;
import entity.system.SYSPropsTools;
import entity.system.UniqueTools;
import op.OPDE;
import op.care.med.vorrat.DlgNewStocks;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * @author tloehr
 */
public class PnlPrescription extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.prescription";

    private Resident resident;

    private ArrayList<Prescription> lstPrescriptions;
    private HashMap<String, CollapsiblePane> cpMap;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JToggleButton tbClosed;

    private Color[] demandColors, regularColors, archiveColors;


    /**
     * Dieser Actionlistener wird gebraucht, damit die einzelnen Menüpunkte des Kontextmenüs, nachdem sie
     * aufgerufen wurden, einen reloadTable() auslösen können.
     */
//    private ActionListener standardActionListener;

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
        prepareSearchArea();
        cpMap = new HashMap<String, CollapsiblePane>();
        lstPrescriptions = new ArrayList<Prescription>();
        demandColors = SYSConst.green2;
        regularColors = SYSConst.blue1;
        archiveColors = SYSConst.greyscale;
    }

    @Override
    public void switchResident(Resident resident) {
        this.resident = resident;
        lstPrescriptions = PrescriptionTools.getAll(resident);
        Collections.sort(lstPrescriptions);
        OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(resident));
        reloadDisplay();
    }

    @Override
    public void reload() {
        lstPrescriptions = PrescriptionTools.getAll(resident);
        Collections.sort(lstPrescriptions);
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
        cpsPrescription.removeAll();
        cpMap.clear();


        if (withworker) {

//            OPDE.getMainframe().setBlocked(true);
//            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));
//
//            SwingWorker worker = new SwingWorker() {
//
//                @Override
//                protected Object doInBackground() throws Exception {
//                    int progress = 0;
//                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, lstInventories.size()));
//
//                    for (MedInventory inventory : lstInventories) {
//                        progress++;
//                        createCP4(inventory);
//                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, lstInventories.size()));
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

            for (Prescription prescription : lstPrescriptions) {
                createCP4(prescription);
            }

            buildPanel();
        }

    }


    private CollapsiblePane createCP4(final Prescription prescription) {
        /***
         *                          _        ____ ____  _  _    _____                      _                 __
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |  / /_ _|_ ____   _____ _ __ | |_ ___  _ __ _   \ \
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_| | | || '_ \ \ / / _ \ '_ \| __/ _ \| '__| | | | |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _| | | || | | \ V /  __/ | | | || (_) | |  | |_| | |
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_| | ||___|_| |_|\_/ \___|_| |_|\__\___/|_|   \__, | |
         *                                                     \_\                                       |___/_/
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
                "<tr>" +

                "<td width=\"450\" align=\"left\">" +
//                (prescription.isDiscontinued() ? "<s>" : "") +
                PrescriptionTools.getShortDescription(prescription) + "</td>" +
//                (prescription.isDiscontinued() ? "</s>" : "") +
                "<td width=\"450\" align=\"left\">" + PrescriptionTools.getDose(prescription) + "</td>" +

                "</tr>" +
                "</table>" +


                "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpPres.setCollapsed(!cpPres.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });
        cpPres.setTitleLabelComponent(cptitle.getMain());
        cpPres.setSlidingDirection(SwingConstants.SOUTH);

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {

            /***
             *      ____  _                     _   _
             *     |  _ \(_)___  ___ ___  _ __ | |_(_)_ __  _   _  ___
             *     | | | | / __|/ __/ _ \| '_ \| __| | '_ \| | | |/ _ \
             *     | |_| | \__ \ (_| (_) | | | | |_| | | | | |_| |  __/
             *     |____/|_|___/\___\___/|_| |_|\__|_|_| |_|\__,_|\___|
             *
             */
            final JButton btnDiscontinue = new JButton(SYSConst.icon22playerStop);
            btnDiscontinue.setPressedIcon(SYSConst.icon22playerStopPressed);
            btnDiscontinue.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnDiscontinue.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnDiscontinue.setContentAreaFilled(false);
            btnDiscontinue.setBorder(null);
            btnDiscontinue.setToolTipText(OPDE.lang.getString(internalClassID + ".btnDiscontinue.tooltip"));
            btnDiscontinue.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

//                    if (BHPTools.hasBeenUsedAlready(prescription)) {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".cantedit.hasBeenUsedAlready"));
//                        return;
//                    }

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
                                    BHPTools.cleanup(em, myPrescription);
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
            btnDiscontinue.setEnabled(!prescription.isDiscontinued());
            cptitle.getRight().add(btnDiscontinue);


            /***
             *      _____    _ _ _
             *     | ____|__| (_) |_
             *     |  _| / _` | | __|
             *     | |__| (_| | | |_
             *     |_____\__,_|_|\__/
             *
             */
            final JButton btnEdit = new JButton(SYSConst.icon22edit1);
            btnEdit.setPressedIcon(SYSConst.icon22edit1Pressed);
            btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnEdit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnEdit.setContentAreaFilled(false);
            btnEdit.setBorder(null);
            btnEdit.setToolTipText(OPDE.lang.getString(internalClassID + ".btnEdit.tooltip"));
            btnEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    if (BHPTools.hasBeenUsedAlready(prescription)) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".cantedit.hasBeenUsedAlready"));
                        return;
                    }

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
            btnEdit.setEnabled(!prescription.isDiscontinued());
            cptitle.getRight().add(btnEdit);


        }


        cpPres.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                JTextPane txt = new JTextPane();
                txt.setContentType("text/html");
                txt.setEditable(false);
                txt.setText(SYSTools.toHTML(PrescriptionTools.getPrescriptionAsHTML(prescription, false, false, true)));
                cpPres.setContentPane(txt);
            }
        });

        if (!cpPres.isCollapsed()) {
            JTextPane txt = new JTextPane();
            txt.setContentType("text/html");
            txt.setEditable(false);
            txt.setText(SYSTools.toHTML(PrescriptionTools.getPrescriptionAsHTML(prescription, false, false, true)));
            cpPres.setContentPane(txt);
        }

        cpPres.setHorizontalAlignment(SwingConstants.LEADING);
        cpPres.setOpaque(false);
        cpPres.setBackground(getColor(prescription, SYSConst.medium4));

        return cpPres;
    }

    private Color getColor(Prescription prescription, int level) {
        if (prescription.isDiscontinued()) {
            return archiveColors[level];
        } else if (prescription.isOnDemand()) {
            return demandColors[level];
        } else {
            return regularColors[level];
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
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

//    private void printVerordnungen(int[] sel) {
//        try {
//            File temp = File.createTempFile("prescription", ".html");
//            temp.deleteOnExit();
//            List<Prescription> listVerordnung = ((TMVerordnung) tblVerordnung.getModel()).getVordnungenAt(sel);
//            String html = SYSTools.htmlUmlautConversion(PrescriptionTools.getPrescriptionAsHTML(listVerordnung, true, true, false));
//            SYSFilesTools.print(html, true);
//        } catch (IOException e) {
////            new DlgException(e);
//        }
//
//    }

    private void printStellplan() {

        try {
            File temp = File.createTempFile("stellplan", ".html");
            temp.deleteOnExit();
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            String html = SYSTools.htmlUmlautConversion(PrescriptionTools.getDailyPlanAsHTML(resident.getStation().getEinrichtung()));
            out.write(html);
            out.close();
            SYSFilesTools.handleFile(temp, Desktop.Action.OPEN);
        } catch (IOException e) {
//            new DlgException(e);
        }

    }

//    private void tblVerordnungMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVerordnungMousePressed
//        Point p = evt.getPoint();
//
//        final ListSelectionModel lsm = tblVerordnung.getSelectionModel();
//        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();
//
//        if (lsm.getMinSelectionIndex() == lsm.getMaxSelectionIndex()) {
//
//            int row = tblVerordnung.rowAtPoint(p);
//            lsm.setSelectionInterval(row, row);
//        }
//
//        final List<Prescription> selection = ((TMVerordnung) tblVerordnung.getModel()).getVordnungenAt(tblVerordnung.getSelectedRows());
//
//        // Kontext Menü
//        if (singleRowSelected && evt.isPopupTrigger()) {
//            boolean readOnly = false;
//            final Prescription selectedVerordnung = selection.get(0);
//
//            long num = BHPTools.getNumBHPs(selectedVerordnung);
//            boolean editAllowed = !readOnly && num == 0;
//            boolean changeAllowed = !readOnly && !selectedVerordnung.isOnDemand() && !selectedVerordnung.isDiscontinued() && num > 0;
//            boolean absetzenAllowed = !readOnly && !selectedVerordnung.isDiscontinued();
//            boolean deleteAllowed = !readOnly && num == 0;
//
//            SYSTools.unregisterListeners(menu);
//            menu = new JPopupMenu();
//
//            JMenuItem itemPopupEdit = new JMenuItem("Korrigieren");
//            itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
////                    long numVerKennung = VerordnungTools.getNumVerodnungenMitGleicherKennung(prescription);
////                    int status = numVerKennung == 1 ? DlgRegular.EDIT_MODE : DlgRegular.EDIT_OF_CHANGE_MODE;
//
//                    new DlgRegular(selectedVerordnung, DlgRegular.ALLOW_ALL_EDIT, new Closure() {
//                        @Override
//                        public void execute(Object o) {
//                            if (o != null) {
//                                Pair<Prescription, List<PrescriptionSchedule>> result = (Pair<Prescription, List<PrescriptionSchedule>>) o;
//                                EntityManager em = OPDE.createEM();
//                                Prescription verordnung = em.merge(result.getFirst());
//
//                                try {
//                                    em.getTransaction().begin();
//                                    em.lock(verordnung, LockModeType.OPTIMISTIC);
//
//                                    // Änderung an bestehenden Planungen
//                                    for (PrescriptionSchedule planung : verordnung.getPrescriptionSchedule()) {
//                                        planung = em.merge(planung);
//                                        em.lock(planung, LockModeType.OPTIMISTIC);
//                                    }
//
//                                    // Planungen die zukünftig wegfallen.
//                                    for (PrescriptionSchedule planung : result.getSecond()) {
//                                        planung = em.merge(planung);
//                                        em.lock(planung, LockModeType.OPTIMISTIC);
//                                        em.remove(planung);
//                                    }
//
//                                    // Bei einer Korrektur werden alle bisherigen Einträge aus der BHP zuerst wieder entfernt.
//                                    Query queryDELBHP = em.createQuery("DELETE FROM BHP bhp WHERE bhp.prescription = :verordnung");
//                                    queryDELBHP.setParameter("verordnung", verordnung);
//                                    queryDELBHP.executeUpdate();
//
//                                    if (!verordnung.isOnDemand()) {
//                                        BHPTools.generate(em, verordnung.getPrescriptionSchedule(), new DateMidnight(), true);
//                                    }
//
//                                    em.getTransaction().commit();
//                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Korrigiert: " + PrescriptionTools.toPrettyString(verordnung), 2));
//                                } catch (javax.persistence.OptimisticLockException ole) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
//                                    }
//                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                } catch (Exception e) {
//                                    em.getTransaction().rollback();
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//                            }
//                            reloadTable();
//                        }
//                    });
//                }
//            });
//
//            menu.add(itemPopupEdit);
//            itemPopupEdit.setEnabled(editAllowed && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
//            //ocs.setEnabled(this, "itemPopupEditText", itemPopupEditText, !readOnly && status > 0 && changeable);
//            // -------------------------------------------------
//            JMenuItem itemPopupChange = new JMenuItem("Verändern");
//            itemPopupChange.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//
//                    new DlgRegular((Prescription) selectedVerordnung.clone(), DlgRegular.NO_CHANGE_MED_AND_SIT, new Closure() {
//                        @Override
//                        public void execute(Object o) {
//                            if (o != null) {
//                                EntityManager em = OPDE.createEM();
//
//                                Pair<Prescription, List<PrescriptionSchedule>> result = (Pair<Prescription, List<PrescriptionSchedule>>) o;
//
//                                try {
//                                    em.getTransaction().begin();
//
//                                    Prescription newVerordnung = em.merge(result.getFirst());
//                                    Prescription oldVerordnung = em.merge(selectedVerordnung);
//
//                                    em.lock(oldVerordnung, LockModeType.OPTIMISTIC);
//
//                                    // Bei einer Veränderung, wird erst die alte Verordnung durch den ANsetzenden Doc ABgesetzt.
//                                    oldVerordnung.setTo(new Date());
//                                    oldVerordnung.setUserOFF(em.merge(OPDE.getLogin().getUser()));
//                                    oldVerordnung.setDocOFF(newVerordnung.getDocON() == null ? null : em.merge(newVerordnung.getDocON()));
//                                    oldVerordnung.setHospitalOFF(newVerordnung.getHospitalON() == null ? null : em.merge(newVerordnung.getHospitalON()));
//
//                                    // Dann wird die neue Verordnung angesetzt.
//                                    // die neue Verordnung beginnt eine Sekunde, nachdem die vorherige Abgesetzt wurde.
//                                    newVerordnung.setFrom(SYSCalendar.addField(oldVerordnung.getTo(), 1, GregorianCalendar.SECOND));
//
//                                    // Dann werden die nicht mehr benötigten BHPs der alten Verordnung entfernt.
//                                    BHPTools.cleanup(em, oldVerordnung);
//
//                                    // Die neuen BHPs werden erzeugt.
//                                    if (!newVerordnung.isOnDemand()) {
//                                        // ab der aktuellen Uhrzeit
//                                        BHPTools.generate(em, newVerordnung.getPrescriptionSchedule(), new DateMidnight(), false);
//                                    }
//
//                                    em.getTransaction().commit();
//                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Geändert: " + PrescriptionTools.toPrettyString(oldVerordnung), 2));
//                                } catch (OptimisticLockException ole) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
//                                    }
//                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                } catch (Exception e) {
//                                    em.getTransaction().rollback();
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//                            }
//                            reloadTable();
//                        }
//                    });
//                }
//            });
//            menu.add(itemPopupChange);
//            itemPopupChange.setEnabled(changeAllowed && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
//            // -------------------------------------------------
//            JMenuItem itemPopupQuit = new JMenuItem("Absetzen");
//            itemPopupQuit.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//
//                    new DlgDiscontinue(selectedVerordnung, new Closure() {
//                        @Override
//                        public void execute(Object o) {
//                            if (o != null) {
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    Prescription verordnung = (Prescription) em.merge(o);
//                                    em.lock(verordnung, LockModeType.OPTIMISTIC);
//                                    verordnung.setTo(new Date());
//                                    verordnung.setUserOFF(em.merge(OPDE.getLogin().getUser()));
//                                    BHPTools.cleanup(em, verordnung);
//                                    em.getTransaction().commit();
//                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Abgesetzt: " + PrescriptionTools.toPrettyString(verordnung), 2));
////                                    em.getEntityManagerFactory().getCache().evict(Verordnung.class);
//                                } catch (OptimisticLockException ole) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
//                                    }
//                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                } catch (Exception e) {
//                                    em.getTransaction().rollback();
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//                            }
//                        }
//                    });
//                    reloadTable();
//                }
//            });
//            menu.add(itemPopupQuit);
//            itemPopupQuit.setEnabled(absetzenAllowed && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
//            // -------------------------------------------------
//            JMenuItem itemPopupDelete = new JMenuItem("Löschen", new ImageIcon(getClass().getResource("/artwork/22x22/bw/trashcan_empty.png")));
//            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    new DlgYesNo("Soll die Verordnung wirklich gelöscht werden.", new ImageIcon(getClass().getResource("/artwork/48x48/bw/trashcan_empty.png")), new Closure() {
//                        @Override
//                        public void execute(Object answer) {
//                            if (answer.equals(JOptionPane.YES_OPTION)) {
//                                Prescription myverordnung = null;
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    myverordnung = em.merge(selectedVerordnung);
//                                    em.getTransaction().begin();
//                                    em.lock(myverordnung, LockModeType.OPTIMISTIC);
//                                    em.remove(myverordnung);
//
//                                    Query delQuery = em.createQuery("DELETE FROM BHP b WHERE b.prescription = :verordnung");
//                                    delQuery.setParameter("verordnung", selectedVerordnung);
//                                    delQuery.executeUpdate();
//
//                                    em.getTransaction().commit();
//                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Gelöscht: " + PrescriptionTools.toPrettyString(selectedVerordnung), 2));
//                                } catch (OptimisticLockException ole) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
//                                    }
//                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                } catch (Exception e) {
//                                    em.getTransaction().rollback();
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//                                reloadTable();
//                            }
//                        }
//                    });
//
//
//                }
//            });
//            menu.add(itemPopupDelete);
//
//            itemPopupDelete.setEnabled(deleteAllowed && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE));
//
//            if (selectedVerordnung.hasMed()) {
//                menu.add(new JSeparator());
//
//                final MedStock bestandImAnbruch = MedStockTools.getStockInUse(TradeFormTools.getInventory4TradeForm(resident, selectedVerordnung.getTradeForm()));
//                boolean bestandAbschliessenAllowed = !readOnly && !selectedVerordnung.isDiscontinued() && bestandImAnbruch != null && !bestandImAnbruch.hashNext2Open();
//                boolean bestandAnbrechenAllowed = !readOnly && !selectedVerordnung.isDiscontinued() && bestandImAnbruch == null;
//
//                JMenuItem itemPopupCloseBestand = new JMenuItem("Bestand abschließen");
//                itemPopupCloseBestand.addActionListener(new java.awt.event.ActionListener() {
//
//                    public void actionPerformed(java.awt.event.ActionEvent evt) {
//                        new DlgCloseStock(bestandImAnbruch, new Closure() {
//                            @Override
//                            public void execute(Object o) {
//                                if (o != null) {
//                                    reloadTable();
//                                }
//                            }
//                        });
//                    }
//                });
//                menu.add(itemPopupCloseBestand);
//                itemPopupCloseBestand.setEnabled(bestandAbschliessenAllowed && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
//
//                JMenuItem itemPopupOpenBestand = new JMenuItem("Bestand anbrechen");
//                itemPopupOpenBestand.addActionListener(new java.awt.event.ActionListener() {
//
//                    public void actionPerformed(java.awt.event.ActionEvent evt) {
//                        new DlgOpenStock(selectedVerordnung.getTradeForm(), selectedVerordnung.getResident(), new Closure() {
//                            @Override
//                            public void execute(Object o) {
//                                if (o != null) {
//                                    reloadTable();
//                                }
//                            }
//                        });
//                    }
//                });
//                menu.add(itemPopupOpenBestand);
//                itemPopupOpenBestand.setEnabled(bestandAnbrechenAllowed && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
//            }
//            menu.add(new JSeparator());
//
//            JMenuItem itemPopupPrint = new JMenuItem("Markierte Verordnungen drucken");
//            itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    int[] sel = tblVerordnung.getSelectedRows();
//                    printVerordnungen(sel);
//                }
//            });
//            menu.add(itemPopupPrint);
//
////            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !prescription.isDiscontinued() && singleRowSelected) {
////                menu.add(new JSeparator());
////                menu.add(SYSFilesTools.getSYSFilesContextMenu(parent, prescription, standardActionListener));
////            }
////
////            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !prescription.isDiscontinued() && singleRowSelected) {
////                menu.add(new JSeparator());
////                menu.add(QProcessTools.getVorgangContextMenu(parent, prescription, resident, standardActionListener));
////            }
//
//
//            menu.add(new JSeparator());
//            JMenuItem itemPopupInfo = new JMenuItem("Infos anzeigen");
//            itemPopupInfo.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    final MedStock bestandImAnbruch = MedStockTools.getStockInUse(TradeFormTools.getInventory4TradeForm(resident, selectedVerordnung.getTradeForm()));
//
//                    String message = "VerID: " + selectedVerordnung.getVerid();
//                    if (bestandImAnbruch != null) {
//                        BigDecimal apv = MedStockTools.getAPV4(bestandImAnbruch.getInventory());
//                        BigDecimal apvBest = bestandImAnbruch.getAPV();
//                        message += "  VorID: " + bestandImAnbruch.getInventory().getID() + "  DafID: " + bestandImAnbruch.getTradeForm().getID() + "  APV per BW: " + apv + "  APV (Bestand): " + apvBest;
//                    }
//
//                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(message, 10));
//                }
//            });
//            itemPopupInfo.setEnabled(true);
//            menu.add(itemPopupInfo);
//
//
//            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
//        }
//    }//GEN-LAST:event_tblVerordnungMousePressed

//    private void jspVerordnungComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspVerordnungComponentResized
//
//        JScrollPane jsp = (JScrollPane) evt.getComponent();
//        Dimension dim = jsp.getSize();
//
//        TableColumnModel tcm1 = tblVerordnung.getColumnModel();
//
//        if (tcm1.getColumnCount() > 0) {
//            tcm1.getColumn(TMVerordnung.COL_MSSN).setPreferredWidth(dim.width / 3);  // 1/5 tel der Gesamtbreite
//            tcm1.getColumn(TMVerordnung.COL_Dosis).setPreferredWidth(dim.width / 3);  // 3/5 tel der Gesamtbreite
//            tcm1.getColumn(TMVerordnung.COL_Hinweis).setPreferredWidth(dim.width / 3);  // 1/5 tel der Gesamtbreite
//            tcm1.getColumn(0).setHeaderValue("Medikament / Massnahme");
//            tcm1.getColumn(1).setHeaderValue("Dosierung / Häufigkeit");
//            tcm1.getColumn(2).setHeaderValue("Hinweise");
//        }
//    }//GEN-LAST:event_jspVerordnungComponentResized

    public void cleanup() {
        SYSTools.unregisterListeners(this);
//        SYSRunningClassesTools.endModule(myRunningClass);
    }

//    private void loadTable() {
//        tblVerordnung.setModel(new TMVerordnung(resident, tbClosed.isSelected(), true));
//        tblVerordnung.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//
//        jspVerordnung.dispatchEvent(new ComponentEvent(jspVerordnung, ComponentEvent.COMPONENT_RESIZED));
//        tblVerordnung.getColumnModel().getColumn(0).setCellRenderer(new RNDHTML());
//        tblVerordnung.getColumnModel().getColumn(1).setCellRenderer(new RNDHTML());
//        tblVerordnung.getColumnModel().getColumn(2).setCellRenderer(new RNDHTML());
//    }

//    private void reloadTable() {
//        OPDE.getMainframe().setBlocked(true);
//        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));
//        TableModel oldmodel = tblVerordnung.getModel();
//        tblVerordnung.setModel(new DefaultTableModel());
//        if (oldmodel != null && oldmodel instanceof TMVerordnung) {
//            ((TMVerordnung) oldmodel).cleanup();
//        }
//
//        SwingWorker worker = new SwingWorker() {
//            TMVerordnung model;
//
//            @Override
//            protected Object doInBackground() throws Exception {
//                model = new TMVerordnung(resident, tbClosed.isSelected(), true);
//                return null;
//            }
//
//            @Override
//            protected void done() {
//                OPDE.getDisplayManager().setProgressBarMessage(null);
//                OPDE.getMainframe().setBlocked(false);
//
//                tblVerordnung.setModel(model);
//                tblVerordnung.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//
//                jspVerordnung.dispatchEvent(new ComponentEvent(jspVerordnung, ComponentEvent.COMPONENT_RESIZED));
//                tblVerordnung.getColumnModel().getColumn(0).setCellRenderer(new RNDHTML());
//                tblVerordnung.getColumnModel().getColumn(1).setCellRenderer(new RNDHTML());
//                tblVerordnung.getColumnModel().getColumn(2).setCellRenderer(new RNDHTML());
//            }
//        };
//        worker.execute();
//    }

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

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }

    private java.util.List<Component> addFilters() {
        java.util.List<Component> list = new ArrayList<Component>();

        tbClosed = GUITools.getNiceToggleButton(internalClassID + ".showclosed");
        SYSPropsTools.restoreState(internalClassID + ":tbClosed", tbClosed);
        tbClosed.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SYSPropsTools.storeState(internalClassID + ":tbClosed", tbClosed);
                buildPanel();
            }
        });

        list.add(tbClosed);

        return list;
    }

    private java.util.List<Component> addCommands() {
        java.util.List<Component> list = new ArrayList<Component>();

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            JideButton addRegular = GUITools.createHyperlinkButton(internalClassID + ".btnNewRegular", SYSConst.icon22add, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgRegular(new Prescription(resident), DlgRegular.MODE_NEW, new Closure() {
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
            list.add(addRegular);
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            JideButton addNewOnDemand = GUITools.createHyperlinkButton(internalClassID + ".btnNewOnDemand", SYSConst.icon22add, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
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

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {
            JideButton buchenButton = GUITools.createHyperlinkButton(internalClassID + ".newstocks", new ImageIcon(getClass().getResource("/artwork/22x22/shetaddrow.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgNewStocks(resident);
                }
            });
            list.add(buchenButton);
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
            JideButton printButton = GUITools.createHyperlinkButton(internalClassID + ".printdailyplan", SYSConst.icon22print, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    printStellplan();
                }
            });
            list.add(printButton);
        }

        return list;
    }

    private void buildPanel() {
        cpsPrescription.removeAll();
        cpsPrescription.setLayout(new JideBoxLayout(cpsPrescription, JideBoxLayout.Y_AXIS));

        for (Prescription prescription : lstPrescriptions) {
            if (tbClosed.isSelected() || !prescription.isDiscontinued()) {
                cpsPrescription.add(cpMap.get(prescription.getID() + ".xprescription"));
//            cpMap.get(prescription.getID() + ".xprescription").getContentPane().revalidate();
//            cpsPrescription.revalidate();
            }
        }


        cpsPrescription.addExpansion();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspPrescription;
    private CollapsiblePanes cpsPrescription;
    // End of variables declaration//GEN-END:variables


}
