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
package op.care.nursingprocess;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import com.toedter.calendar.JDateChooser;
import entity.info.ResInfoCategory;
import entity.info.ResInfoCategoryTools;
import entity.nursingprocess.Intervention;
import entity.nursingprocess.InterventionSchedule;
import entity.nursingprocess.NursingProcess;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.GUITools;
import op.tools.MyJDialog;
import op.tools.Pair;
import op.tools.SYSConst;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import tablerenderer.RNDHTML;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * @author root
 */
public class DlgNursingProcess extends MyJDialog {
    public static final String internalClassID = "nursingrecords.nursingprocess.dlgplanung";
    private Closure actionBlock;
    private NursingProcess planung;
    private JPopupMenu menu;
    private ArrayList<InterventionSchedule> listInterventionSchedule2Remove = new ArrayList();

    /**
     * Creates new form DlgNursingProcess
     */
    public DlgNursingProcess(NursingProcess planung, Closure actionBlock) {
        super();
        this.planung = planung;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
        pack();
        setVisible(true);
    }

    private void initDialog() {
        cmbKategorie.setModel(new DefaultComboBoxModel(ResInfoCategoryTools.getCategoriesForNursingProcess().toArray()));

        txtStichwort.setText(planung.getStichwort());
        txtSituation.setText(planung.getSituation());
        txtZiele.setText(planung.getZiel());
        jdcKontrolle.setDate(planung.getNKontrolle());
        jdcKontrolle.setMinSelectableDate(new Date());
        cmbKategorie.setSelectedItem(planung.getKategorie());
        reloadInterventions();

        String mode = "new";
        if (planung.getPlanID() != null) {
            mode = "edit";
        } else if (planung.getPlanID() == null && planung.getPlanKennung() > -1) {
            mode = "change";
        } else if (planung.getPlanID() == null && planung.getPlanKennung() == -2) {
            mode = "template";
        }
        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + "." + mode), OPDE.START_OF_MODULE_TIME));
    }

    @Override
    public void dispose() {
        jdcKontrolle.cleanup();
        super.dispose();

        if (planung == null) {
            actionBlock.execute(null);
        } else {
            actionBlock.execute(new Pair<NursingProcess, ArrayList<InterventionSchedule>>(planung, listInterventionSchedule2Remove));
        }
    }

    private void reloadInterventions() {
        tblPlanung.setModel(new TMPlanung(planung));
        tblPlanung.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblPlanung.getColumnModel().getColumn(TMPlanung.COL_TXT).setCellRenderer(new RNDHTML());
        tblPlanung.getColumnModel().getColumn(TMPlanung.COL_TXT).setHeaderValue(OPDE.lang.getString(PnlNursingProcess.internalClassID + ".interventions"));
    }

    private void btnAddInterventionActionPerformed(ActionEvent e) {
        /***
         *      _     _            _       _     _
         *     | |__ | |_ _ __    / \   __| | __| |
         *     | '_ \| __| '_ \  / _ \ / _` |/ _` |
         *     | |_) | |_| | | |/ ___ \ (_| | (_| |
         *     |_.__/ \__|_| |_/_/   \_\__,_|\__,_|
         *
         */
        final JidePopup popup = new JidePopup();
        PnlSelectIntervention pnlSelectIntervention = new PnlSelectIntervention(new Closure() {
            @Override
            public void execute(Object o) {
                popup.hidePopup();
                if (o != null) {
                    for (Object obj : (Object[]) o) {
                        Intervention intervention = (Intervention) obj;
                        planung.getInterventionSchedule().add(new InterventionSchedule(planung, intervention));
                    }
                    reloadInterventions();
                }
            }
        });

        popup.setMovable(false);
        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

        popup.setOwner(btnAddIntervention);
        popup.removeExcludedComponent(btnAddIntervention);
        popup.getContentPane().add(pnlSelectIntervention);
        popup.setDefaultFocusComponent(pnlSelectIntervention);
        GUITools.showPopup(popup, SwingConstants.NORTH_WEST);
    }

    private void txtStichwortFocusGained(FocusEvent e) {
        ((JTextComponent) e.getSource()).selectAll();
    }

    private void txtSituationFocusGained(FocusEvent e) {
        ((JTextComponent) e.getSource()).selectAll();
    }

    private void txtZieleFocusGained(FocusEvent e) {
        ((JTextComponent) e.getSource()).selectAll();
    }

    /**
     * Reasons why you couldn't save it
     *
     * @return
     */
    private boolean saveOK() {

        if (txtStichwort.getText().trim().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".stichwortxx"), DisplayMessage.WARNING));
            return false;
        }

        if (jdcKontrolle.getDate() == null) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".datumxx"), DisplayMessage.WARNING));
            return false;
        }

        if (cmbKategorie.getSelectedItem() == null) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".kategoriexx"), DisplayMessage.WARNING));
            return false;
        }

        if (planung.getInterventionSchedule().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".schedulexx"), DisplayMessage.WARNING));
            return false;
        }

        if (txtSituation.getText().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".situationxx"), DisplayMessage.WARNING));
            return false;
        }

        if (txtZiele.getText().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".goalxx"), DisplayMessage.WARNING));
            return false;
        }

        return true;

    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel5 = new JPanel();
        jLabel4 = new JLabel();
        txtStichwort = new JTextField();
        jLabel5 = new JLabel();
        cmbKategorie = new JComboBox();
        jScrollPane3 = new JScrollPane();
        txtSituation = new JTextArea();
        jLabel3 = new JLabel();
        jLabel8 = new JLabel();
        jScrollPane1 = new JScrollPane();
        txtZiele = new JTextArea();
        jLabel7 = new JLabel();
        jdcKontrolle = new JDateChooser();
        panel2 = new JPanel();
        jspPlanung = new JScrollPane();
        tblPlanung = new JTable();
        panel3 = new JPanel();
        btnAddIntervention = new JButton();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnSave = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "14dlu, $lcgap, 280dlu:grow, $ugap, pref, $lcgap, 14dlu",
            "fill:14dlu, $lgap, default, $rgap, pref, $lgap, 14dlu"));

        //======== jPanel5 ========
        {
            jPanel5.setLayout(new FormLayout(
                "default, $lcgap, default:grow",
                "fill:default, $rgap, default, 2*($lgap, fill:default:grow), $lgap, pref"));

            //---- jLabel4 ----
            jLabel4.setFont(new Font("Arial", Font.PLAIN, 14));
            jLabel4.setText("Stichwort:");
            jPanel5.add(jLabel4, CC.xy(1, 1, CC.DEFAULT, CC.TOP));

            //---- txtStichwort ----
            txtStichwort.setFont(new Font("Arial", Font.BOLD, 20));
            txtStichwort.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtStichwortFocusGained(e);
                }
            });
            jPanel5.add(txtStichwort, CC.xy(3, 1));

            //---- jLabel5 ----
            jLabel5.setFont(new Font("Arial", Font.PLAIN, 14));
            jLabel5.setText("Kategorie:");
            jPanel5.add(jLabel5, CC.xy(1, 3));

            //---- cmbKategorie ----
            cmbKategorie.setModel(new DefaultComboBoxModel(new String[] {
                "Item 1",
                "Item 2",
                "Item 3",
                "Item 4"
            }));
            cmbKategorie.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel5.add(cmbKategorie, CC.xy(3, 3));

            //======== jScrollPane3 ========
            {

                //---- txtSituation ----
                txtSituation.setColumns(20);
                txtSituation.setLineWrap(true);
                txtSituation.setRows(5);
                txtSituation.setWrapStyleWord(true);
                txtSituation.setFont(new Font("Arial", Font.PLAIN, 14));
                txtSituation.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtSituationFocusGained(e);
                    }
                });
                jScrollPane3.setViewportView(txtSituation);
            }
            jPanel5.add(jScrollPane3, CC.xy(3, 5));

            //---- jLabel3 ----
            jLabel3.setFont(new Font("Arial", Font.PLAIN, 14));
            jLabel3.setText("Situation:");
            jPanel5.add(jLabel3, CC.xy(1, 5, CC.DEFAULT, CC.TOP));

            //---- jLabel8 ----
            jLabel8.setFont(new Font("Arial", Font.PLAIN, 14));
            jLabel8.setText("Ziele:");
            jPanel5.add(jLabel8, CC.xy(1, 7, CC.DEFAULT, CC.TOP));

            //======== jScrollPane1 ========
            {

                //---- txtZiele ----
                txtZiele.setColumns(20);
                txtZiele.setLineWrap(true);
                txtZiele.setRows(5);
                txtZiele.setWrapStyleWord(true);
                txtZiele.setFont(new Font("Arial", Font.PLAIN, 14));
                txtZiele.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtZieleFocusGained(e);
                    }
                });
                jScrollPane1.setViewportView(txtZiele);
            }
            jPanel5.add(jScrollPane1, CC.xy(3, 7));

            //---- jLabel7 ----
            jLabel7.setFont(new Font("Arial", Font.PLAIN, 14));
            jLabel7.setText("Erste Kontrolle am:");
            jPanel5.add(jLabel7, CC.xy(1, 9));

            //---- jdcKontrolle ----
            jdcKontrolle.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel5.add(jdcKontrolle, CC.xy(3, 9));
        }
        contentPane.add(jPanel5, CC.xy(3, 3, CC.DEFAULT, CC.FILL));

        //======== panel2 ========
        {
            panel2.setLayout(new FormLayout(
                "default:grow",
                "default, $lgap, default"));

            //======== jspPlanung ========
            {
                jspPlanung.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        jspPlanungComponentResized(e);
                    }
                });

                //---- tblPlanung ----
                tblPlanung.setModel(new DefaultTableModel(
                    new Object[][] {
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                    },
                    new String[] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                    }
                ));
                tblPlanung.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                tblPlanung.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        tblPlanungMousePressed(e);
                    }
                });
                jspPlanung.setViewportView(tblPlanung);
            }
            panel2.add(jspPlanung, CC.xy(1, 1));

            //======== panel3 ========
            {
                panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

                //---- btnAddIntervention ----
                btnAddIntervention.setText(null);
                btnAddIntervention.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                btnAddIntervention.setContentAreaFilled(false);
                btnAddIntervention.setBorderPainted(false);
                btnAddIntervention.setBorder(null);
                btnAddIntervention.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png")));
                btnAddIntervention.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnAddInterventionActionPerformed(e);
                    }
                });
                panel3.add(btnAddIntervention);
            }
            panel2.add(panel3, CC.xy(1, 3));
        }
        contentPane.add(panel2, CC.xy(5, 3));

        //======== panel1 ========
        {
            panel1.setLayout(new HorizontalLayout(5));

            //---- btnCancel ----
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnCancel.setText(null);
            btnCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnCancelActionPerformed(e);
                }
            });
            panel1.add(btnCancel);

            //---- btnSave ----
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.setText(null);
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });
            panel1.add(btnSave);
        }
        contentPane.add(panel1, CC.xy(5, 5, CC.RIGHT, CC.DEFAULT));
        setSize(1145, 740);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents


    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        planung = null;
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void jspPlanungComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspPlanungComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        if (tblPlanung.getRowCount() <= 0) {
            return;
        }
        Dimension dim = jsp.getSize();
        int textWidth = dim.width - 25;
        TableColumnModel tcm1 = tblPlanung.getColumnModel();
        tcm1.getColumn(0).setPreferredWidth(textWidth);
        tcm1.getColumn(0).setHeaderValue(OPDE.lang.getString(PnlNursingProcess.internalClassID + ".interventions"));
    }//GEN-LAST:event_jspPlanungComponentResized

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (saveOK()) {
            save();
            dispose();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void tblPlanungMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPlanungMousePressed
        if (!evt.isPopupTrigger()) {
            return;
        }

        Point p = evt.getPoint();
        ListSelectionModel lsm = tblPlanung.getSelectionModel();
        int row = tblPlanung.rowAtPoint(p);
        if (lsm.isSelectionEmpty() || (lsm.getMinSelectionIndex() == lsm.getMaxSelectionIndex())) {
            lsm.setSelectionInterval(row, row);
        }

        menu = new JPopupMenu();

        /***
         *      _ _                 ____                         ____       _      _
         *     (_) |_ ___ _ __ ___ |  _ \ ___  _ __  _   _ _ __ |  _ \  ___| | ___| |_ ___
         *     | | __/ _ \ '_ ` _ \| |_) / _ \| '_ \| | | | '_ \| | | |/ _ \ |/ _ \ __/ _ \
         *     | | ||  __/ | | | | |  __/ (_) | |_) | |_| | |_) | |_| |  __/ |  __/ ||  __/
         *     |_|\__\___|_| |_| |_|_|   \___/| .__/ \__,_| .__/|____/ \___|_|\___|\__\___|
         *                                    |_|         |_|
         */
        JMenuItem itemPopupDelete = new JMenuItem(OPDE.lang.getString("misc.commands.delete"), SYSConst.icon22delete);
        itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                for (int row : tblPlanung.getSelectedRows()) {
                    listInterventionSchedule2Remove.add(((TMPlanung) tblPlanung.getModel()).getInterventionSchedule(row));
                    planung.getInterventionSchedule().remove(((TMPlanung) tblPlanung.getModel()).getInterventionSchedule(row));
                }
                ((TMPlanung) tblPlanung.getModel()).fireTableDataChanged();
            }
        });
        menu.add(itemPopupDelete);
//        itemPopupDelete.setEnabled(!kontrollenMarkiert());

        /***
         *      _ _                 ____                        ____       _              _       _
         *     (_) |_ ___ _ __ ___ |  _ \ ___  _ __  _   _ _ __/ ___|  ___| |__   ___  __| |_   _| | ___
         *     | | __/ _ \ '_ ` _ \| |_) / _ \| '_ \| | | | '_ \___ \ / __| '_ \ / _ \/ _` | | | | |/ _ \
         *     | | ||  __/ | | | | |  __/ (_) | |_) | |_| | |_) |__) | (__| | | |  __/ (_| | |_| | |  __/
         *     |_|\__\___|_| |_| |_|_|   \___/| .__/ \__,_| .__/____/ \___|_| |_|\___|\__,_|\__,_|_|\___|
         *                                    |_|         |_|
         */
        final JMenuItem itemPopupSchedule = new JMenuItem(OPDE.lang.getString("misc.commands.editsheduling"), SYSConst.icon22clock);
        itemPopupSchedule.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                final JidePopup popup = new JidePopup();

                /**
                 * This routine uses the <b>first</b> element of the selection as the template for editing
                 * the schedule. After the edit it clones this "template", removes the original
                 * InterventionSchedules (copying the apropriate Intervention of every single
                 * Schedule first) and finally creates new schedules and adds them to
                 * the CareProcess in question.
                 */
                int row = tblPlanung.getSelectedRows()[0];
                InterventionSchedule firstInterventionScheduleWillBeTemplate = ((TMPlanung) tblPlanung.getModel()).getInterventionSchedule(row);
                JPanel dlg = new PnlSchedule(firstInterventionScheduleWillBeTemplate, new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o != null) {
                            InterventionSchedule template = (InterventionSchedule) o;
                            ArrayList<InterventionSchedule> listInterventionSchedule2Add = new ArrayList();
                            for (int row : tblPlanung.getSelectedRows()) {
                                InterventionSchedule oldTermin = ((TMPlanung) tblPlanung.getModel()).getInterventionSchedule(row);
                                InterventionSchedule newTermin = template.clone();
                                newTermin.setIntervention(oldTermin.getIntervention());
                                listInterventionSchedule2Remove.add(oldTermin);
                                listInterventionSchedule2Add.add(newTermin);
                            }
                            planung.getInterventionSchedule().removeAll(listInterventionSchedule2Remove);
                            planung.getInterventionSchedule().addAll(listInterventionSchedule2Add);
                            popup.hidePopup();
                            Collections.sort(planung.getInterventionSchedule());
                            ((TMPlanung) tblPlanung.getModel()).fireTableDataChanged();
                        }
                    }
                });

                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                popup.getContentPane().add(dlg);
                popup.setOwner(jspPlanung);
                popup.removeExcludedComponent(jspPlanung);
                popup.setDefaultFocusComponent(dlg);

                GUITools.showPopup(popup, SwingConstants.WEST);
            }
        });
        menu.add(itemPopupSchedule);


        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }//GEN-LAST:event_tblPlanungMousePressed


    //    private void saveTEMPLATE() {
//        HashMap hm = new HashMap();
//        hm.put("BWKennung", bewohner.getRID());
//        hm.put("Stichwort", txtStichwort.getText());
//        hm.put("Situation", txtSituation.getText());
//        hm.put("Ziel", txtZiele.getText());
//        ListElement lel = (ListElement) cmbKategorie.getSelectedItem();
//        hm.put("BWIKID", lel.getPk());
//        hm.put("Von", "!NOW!");
//        hm.put("Bis", "!BAW!");
//        hm.put("AnUKennung", OPDE.getLogin().getUser().getUID());
//        hm.put("AbUKennung", null);
//        hm.put("PlanKennung", OPDE.getDb().getUID("__plankenn"));
//        hm.put("NKontrolle", jdcKontrolle.getDate());
//
//        Connection db = OPDE.getDb().db;
//        try {
//            // Hier beginnt eine Transaktion
//            db.setAutoCommit(false);
//            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
//            db.commit();
//
//            planid = op.tools.DBHandling.insertRecord("Planung", hm);
//            if (planid < 0) {
//                throw new SQLException("Fehler bei Insert into Planung");
//            }
//            hm.clear();
//            DBHandling.tmp2real(planid);
//            DFNImport.importDFN(planid);
//
//            db.commit();
//            db.setAutoCommit(true);
//
//        } catch (SQLException ex) {
//            try {
//                db.rollback();
//            } catch (SQLException ex1) {
//                new DlgException(ex1);
//                ex1.printStackTrace();
//                System.exit(1);
//            }
//            new DlgException(ex);
//        }
//    }
//
//    private void saveCHANGE() {
//        // Daten für die NEUE Planung
//        HashMap hm = new HashMap();
//        hm.put("BWKennung", bewohner.getRID());
//        hm.put("Stichwort", txtStichwort.getText());
//        hm.put("Situation", txtSituation.getText());
//        hm.put("Ziel", txtZiele.getText());
//        ListElement lel = (ListElement) cmbKategorie.getSelectedItem();
//        hm.put("BWIKID", lel.getPk());
//        hm.put("Von", "!NOW+1!");
//        hm.put("Bis", "!BAW!");
//        hm.put("AnUKennung", OPDE.getLogin().getUser().getUID());
//        hm.put("AbUKennung", null);
//        hm.put("PlanKennung", plankenn);
//        hm.put("NKontrolle", jdcKontrolle.getDate());
//
//        Connection db = OPDE.getDb().db;
//        try {
//            // Hier beginnt eine Transaktion
//            db.setAutoCommit(false);
//            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
//            db.commit();
//
//            DBHandling.absetzen(planid, txtSituation.getText());
//
//            planid = op.tools.DBHandling.insertRecord("Planung", hm);
//            if (planid < 0) {
//                throw new SQLException("Fehler bei Insert into Planung");
//            }
//            hm.clear();
//            DBHandling.tmp2real(planid);
//            DFNImport.importDFN(planid, SYSCalendar.nowDB(), 0);
//
//            db.commit();
//            db.setAutoCommit(true);
//
//        } catch (SQLException ex) {
//            try {
//                db.rollback();
//            } catch (SQLException ex1) {
//                new DlgException(ex1);
//                ex1.printStackTrace();
//                System.exit(1);
//            }
//            new DlgException(ex);
//        }
//    }
//
    private void save() {
        planung.setStichwort(txtStichwort.getText().trim());
        planung.setSituation(txtSituation.getText().trim());
        planung.setZiel(txtZiele.getText().trim());
        planung.setNKontrolle(jdcKontrolle.getDate());
        planung.setKategorie((ResInfoCategory) cmbKategorie.getSelectedItem());

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel5;
    private JLabel jLabel4;
    private JTextField txtStichwort;
    private JLabel jLabel5;
    private JComboBox cmbKategorie;
    private JScrollPane jScrollPane3;
    private JTextArea txtSituation;
    private JLabel jLabel3;
    private JLabel jLabel8;
    private JScrollPane jScrollPane1;
    private JTextArea txtZiele;
    private JLabel jLabel7;
    private JDateChooser jdcKontrolle;
    private JPanel panel2;
    private JScrollPane jspPlanung;
    private JTable tblPlanung;
    private JPanel panel3;
    private JButton btnAddIntervention;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnSave;
    // End of variables declaration//GEN-END:variables
}
