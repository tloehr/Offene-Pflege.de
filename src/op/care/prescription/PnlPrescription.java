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
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.files.SYSFilesTools;
import entity.info.Resident;
import entity.prescription.*;
import entity.process.QProcess;
import entity.process.QProcessElement;
import entity.process.SYSPRE2PROCESS;
import entity.system.SYSPropsTools;
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

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
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

    private Color[] demandColors, regularColors;

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
        demandColors = SYSConst.green2;
        regularColors = SYSConst.blue1;
        cpMap = new HashMap<String, CollapsiblePane>();
        lstPrescriptions = new ArrayList<Prescription>();
        lstVisiblePrescriptions = new ArrayList<Prescription>();
        prepareSearchArea();
    }

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

                    lstPrescriptions = PrescriptionTools.getAll(resident);
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

            for (Prescription prescription : lstPrescriptions) {
                createCP4(prescription);
            }

            buildPanel();
        }

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
                "<tr>" +
                "<td width=\"450\" align=\"left\">" +
                PrescriptionTools.getShortDescription(prescription) + "</td>" +
                "<td width=\"300\" align=\"left\">" + PrescriptionTools.getDose(prescription) + "</td>" +
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


        /***
         *      __  __
         *     |  \/  | ___ _ __  _   _
         *     | |\/| |/ _ \ '_ \| | | |
         *     | |  | |  __/ | | | |_| |
         *     |_|  |_|\___|_| |_|\__,_|
         *
         */
        final JButton btnMenu = new JButton(SYSConst.icon32menu);
        btnMenu.setPressedIcon(SYSConst.icon32Pressed);
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
        btnMenu.setEnabled(!prescription.isDiscontinued());
        cptitle.getRight().add(btnMenu);

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


        return cpPres;
    }

    private Color getColor(Prescription prescription, int level, boolean odd) {
        if (odd) {
            level = Math.max(0, level - 2);
        }

//        if (prescription.isDiscontinued()) {
//            level = Math.max(0, level - 3);
//        }

        if (prescription.isOnDemand()) {
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
        cpMap.clear();
        cpsPrescription.removeAll();
        lstPrescriptions.clear();
        lstVisiblePrescriptions.clear();
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
        tbClosed.setHorizontalAlignment(SwingConstants.LEFT);
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
                                Pair<Prescription, java.util.List<PrescriptionSchedule>> returnPackage = (Pair<Prescription, List<PrescriptionSchedule>>) o;
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    Prescription myPrescription = em.merge(returnPackage.getFirst());
                                    myPrescription.setRelation(UniqueTools.getNewUID(em, "__verkenn").getUid());
                                    BHPTools.generate(em, myPrescription.getPrescriptionSchedule(), new DateMidnight(), false);
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
            addRegular.setBackground(regularColors[SYSConst.medium4]);
            addRegular.setOpaque(true);
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
            addNewOnDemand.setBackground(demandColors[SYSConst.medium4]);
            addNewOnDemand.setOpaque(true);
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
            JideButton printPrescription = GUITools.createHyperlinkButton(internalClassID + ".print", SYSConst.icon22print2, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    SYSFilesTools.print(PrescriptionTools.getPrescriptionsAsHTML(lstPrescriptions, true, true, false, tbClosed.isSelected()), true);
                }
            });
            list.add(printPrescription);

            JideButton printDaily = GUITools.createHyperlinkButton(internalClassID + ".printdailyplan", SYSConst.icon22print2, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    SYSFilesTools.print(PrescriptionTools.getDailyPlanAsHTML(resident.getStation().getEinrichtung()), true);
                }
            });
            list.add(printDaily);
        }

        return list;
    }

    private void buildPanel() {
        cpsPrescription.removeAll();
        cpsPrescription.setLayout(new JideBoxLayout(cpsPrescription, JideBoxLayout.Y_AXIS));

        int i = 0;
        // for the zebra coloring
        for (Prescription prescription : lstPrescriptions) {
            if (tbClosed.isSelected() || !prescription.isDiscontinued()) {
                cpMap.get(prescription.getID() + ".xprescription").setBackground(getColor(prescription, SYSConst.medium1, i % 2 == 1));
                cpsPrescription.add(cpMap.get(prescription.getID() + ".xprescription"));
                i++;
//                cpPres.setBackground(getColor(prescription, SYSConst.medium1));
            }
        }
//
//        for (Prescription prescription : lstVisiblePrescriptions) {
//
//        }

        cpsPrescription.addExpansion();
    }


    private JPanel getMenu(final Prescription prescription) {

        JPanel pnlMenu = new JPanel(new VerticalLayout());


        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {

            final boolean prescriptionHasBeenUsedAlready = BHPTools.hasBeenUsedAlready(prescription);
            final MedInventory inventory = TradeFormTools.getInventory4TradeForm(prescription.getResident(), prescription.getTradeForm());
            final MedStock stockInUse = MedStockTools.getStockInUse(inventory);

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

                    if (!prescriptionHasBeenUsedAlready) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".cantchange.hasNeverBeenUsed"));
                        return;
                    }

                    new DlgRegular(prescription.clone(), DlgRegular.MODE_CHANGE, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {

                                Pair<Prescription, java.util.List<PrescriptionSchedule>> returnPackage = (Pair<Prescription, List<PrescriptionSchedule>>) o;

                                EntityManager em = OPDE.createEM();
                                try {


                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    Prescription newPrescription = em.merge(returnPackage.getFirst());
                                    Prescription oldPrescription = em.merge(prescription);

                                    em.lock(oldPrescription, LockModeType.OPTIMISTIC);

                                    // Bei einer Veränderung, wird erst die alte Verordnung durch den ANsetzenden Doc ABgesetzt.
                                    DateTime now = new DateTime();
                                    oldPrescription.setTo(now.toDate());
                                    oldPrescription.setUserOFF(em.merge(OPDE.getLogin().getUser()));
                                    oldPrescription.setDocOFF(newPrescription.getDocON() == null ? null : em.merge(newPrescription.getDocON()));
                                    oldPrescription.setHospitalOFF(newPrescription.getHospitalON() == null ? null : em.merge(newPrescription.getHospitalON()));

                                    newPrescription.setFrom(now.plusSeconds(1).toDate());

                                    BHPTools.cleanup(em, oldPrescription);
                                    BHPTools.generate(em, newPrescription.getPrescriptionSchedule(), new DateMidnight(), false);

                                    em.getTransaction().commit();

                                    lstPrescriptions.remove(prescription);
                                    lstPrescriptions.add(oldPrescription);
                                    lstPrescriptions.add(newPrescription);
                                    Collections.sort(lstPrescriptions);
                                    final CollapsiblePane myCP = createCP4(newPrescription);
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
            btnChange.setEnabled(!prescription.isDiscontinued() && !prescription.isOnDemand());
            pnlMenu.add(btnChange);

            /***
             *      ____  _                     _   _
             *     |  _ \(_)___  ___ ___  _ __ | |_(_)_ __  _   _  ___
             *     | | | | / __|/ __/ _ \| '_ \| __| | '_ \| | | |/ _ \
             *     | |_| | \__ \ (_| (_) | | | | |_| | | | | |_| |  __/
             *     |____/|_|___/\___\___/|_| |_|\__|_|_| |_|\__,_|\___|
             *
             */
            final JButton btnDiscontinue = GUITools.createHyperlinkButton(internalClassID + ".btnDiscontinue.tooltip", SYSConst.icon22playerStop, null);
            btnDiscontinue.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnDiscontinue.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    if (!prescriptionHasBeenUsedAlready) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".cantdiscontinue.hasNeverBeenUsed"));
                        return;
                    }

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
                                    if (!myPrescription.isOnDemand()) {
                                        BHPTools.cleanup(em, myPrescription);
                                    }
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
//            btnDiscontinue.setEnabled(!prescription.isDiscontinued());
            pnlMenu.add(btnDiscontinue);


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

                    if (prescriptionHasBeenUsedAlready) {
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

                                        Query queryDELBHP = em.createQuery("DELETE FROM BHP bhp WHERE bhp.prescription = :prescription");
                                        queryDELBHP.setParameter("prescription", myPrescription);
                                        queryDELBHP.executeUpdate();

                                        if (!myPrescription.isOnDemand()) {
                                            BHPTools.generate(em, myPrescription.getPrescriptionSchedule(), new DateMidnight(), true);
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
            btnDelete.setEnabled(!prescription.isDiscontinued() && (!prescriptionHasBeenUsedAlready || OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER)));
            pnlMenu.add(btnDelete);

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

                        }
                    });
                }
            });
            btnCloseStock.setEnabled(inventory != null && stockInUse != null);
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

                        }
                    });
                }
            });
            btnOpenStock.setEnabled(inventory != null && stockInUse == null);
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
                    new DlgFiles(prescription, new Closure() {
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
                    });
                }
            });

            btnFiles.setEnabled(!prescription.isDiscontinued());
            if (prescription.getAttachedFilesConnections().size() > 0) {
                JLabel lblNum = new JLabel(Integer.toString(prescription.getAttachedFilesConnections().size()), SYSConst.icon16greenStar, SwingConstants.CENTER);
                lblNum.setFont(SYSConst.ARIAL10BOLD);
                lblNum.setForeground(Color.BLUE);
                lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
                DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnFiles, lblNum, DefaultOverlayable.SOUTH_EAST);
                overlayableBtn.setOpaque(false);
                pnlMenu.add(overlayableBtn);
            } else {
                pnlMenu.add(btnFiles);
            }

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

                                for (SYSPRE2PROCESS linkObject : myPrescription.getAttachedProcessConnections()) {
                                    if (unassigned.contains(linkObject.getQProcess())) {
                                        em.remove(em.merge(linkObject));
                                    }
                                }

                                for (QProcess qProcess : assigned) {
                                    List<QProcessElement> listElements = qProcess.getElements();
                                    if (!listElements.contains(myPrescription)) {
                                        QProcess myQProcess = em.merge(qProcess);
                                        SYSPRE2PROCESS myLinkObject = em.merge(new SYSPRE2PROCESS(myQProcess, myPrescription));
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
            btnProcess.setEnabled(!prescription.isDiscontinued());

            if (!prescription.getAttachedProcessConnections().isEmpty()) {
                JLabel lblNum = new JLabel(Integer.toString(prescription.getAttachedProcessConnections().size()), SYSConst.icon16redStar, SwingConstants.CENTER);
                lblNum.setFont(SYSConst.ARIAL10BOLD);
                lblNum.setForeground(Color.YELLOW);
                lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
                DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnProcess, lblNum, DefaultOverlayable.SOUTH_EAST);
                overlayableBtn.setOpaque(false);
                pnlMenu.add(overlayableBtn);
            } else {
                pnlMenu.add(btnProcess);
            }
        }
        return pnlMenu;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspPrescription;
    private CollapsiblePanes cpsPrescription;
    // End of variables declaration//GEN-END:variables


}
