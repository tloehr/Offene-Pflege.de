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
package de.offene_pflege.op.care.nursingprocess;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import com.toedter.calendar.JDateChooser;
import de.offene_pflege.backend.entity.info.ResInfoCategory;
import de.offene_pflege.backend.services.ResInfoCategoryTools;
import de.offene_pflege.backend.entity.nursingprocess.Intervention;
import de.offene_pflege.backend.entity.nursingprocess.InterventionSchedule;
import de.offene_pflege.backend.entity.nursingprocess.InterventionScheduleTools;
import de.offene_pflege.backend.entity.nursingprocess.NursingProcess;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.*;
import de.offene_pflege.tablerenderer.RNDHTML;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author root
 */
public class DlgNursingProcess extends MyJDialog {
    public static final String internalClassID = "nursingrecords.nursingprocess.dlgplanung";
    private Closure actionBlock;
    private NursingProcess nursingProcess, resultNursingProcess;
    private JPopupMenu menu;
    //    private ArrayList<InterventionSchedule> listInterventionSchedule2Remove = new ArrayList();
    private PnlCommonTags pnlCommonTags;
    protected JDialog currentEditor;

    /**
     * Creates new form DlgNursingProcess
     */
    public DlgNursingProcess(NursingProcess nursingProcess, Closure actionBlock) {
        super(false);
        this.nursingProcess = nursingProcess;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
        pack();
//        setVisible(true);
    }

    private void initDialog() {
        cmbKategorie.setModel(new DefaultComboBoxModel(ResInfoCategoryTools.getAll4NP().toArray()));

        lblTopic.setText(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.lblTopic"));
        lblCat.setText(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.lblCat"));
        lblSituation.setText(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.lblSituation"));
        lblGoal.setText(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.lblGoal"));
//        lblFlag.setText(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.lblFlag"));
        lblFirstRevision.setText(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.lblFirstRevision"));

        btnPopoutSituation.setToolTipText(SYSTools.xx("misc.msg.presshere.for.larger.window"));
        btnPopoutGoal.setToolTipText(SYSTools.xx("misc.msg.presshere.for.larger.window"));

        txtStichwort.setText(nursingProcess.getTopic());
        txtSituation.setText(nursingProcess.getSituation());
        txtZiele.setText(nursingProcess.getGoal());
        jdcKontrolle.setDate(nursingProcess.getNextEval());
        jdcKontrolle.setMinSelectableDate(new Date());
        cmbKategorie.setSelectedItem(nursingProcess.getCategory());
        reloadInterventions();

        pnlCommonTags = new PnlCommonTags(nursingProcess.getCommontags(), true, 3);
        jPanel5.add(new JScrollPane(pnlCommonTags), CC.xyw(1, 9, 3, CC.DEFAULT, CC.FILL));

        String mode = "new";
        if (nursingProcess.getID() != 0) {
            mode = "edit";
        } else if (nursingProcess.getID() == 0 && nursingProcess.getNPSeries() > -1) {
            mode = "change";
        } else if (nursingProcess.getID() == 0 && nursingProcess.getNPSeries() == -2) {
            mode = "template";
        }
        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung." + mode), OPDE.START_OF_MODULE_TIME));
    }

    @Override
    public void dispose() {
        jdcKontrolle.cleanup();
        //  https://github.com/tloehr/Offene-Pflege.de/issues/62
        // closes an open modal dialog, if necessary.
        // when the timeout occurs
        if (currentEditor != null && currentEditor.isShowing()) {
            currentEditor.dispose();
        }
        super.dispose();

        actionBlock.execute(resultNursingProcess);
    }

    private void reloadInterventions() {
        tblPlanung.setModel(new TMPlan(nursingProcess));
        tblPlanung.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblPlanung.getColumnModel().getColumn(TMPlan.COL_TXT).setCellRenderer(new RNDHTML());
        tblPlanung.getColumnModel().getColumn(TMPlan.COL_TXT).setHeaderValue(SYSTools.xx("nursingrecords.nursingprocess.interventions"));
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
        PnlSelectIntervention pnlSelectIntervention = new PnlSelectIntervention(o -> {
            popup.hidePopup();
            if (o != null) {
                for (Intervention intervention : (List<Intervention>) o) {
                    nursingProcess.getInterventionSchedule().add(new InterventionSchedule(nursingProcess, intervention));
                }
                reloadInterventions();
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

    private void btnPopoutSituationActionPerformed(ActionEvent e) {
        currentEditor = new DlgYesNo(SYSConst.icon48edit, o -> {
            if (o != null) {
                txtSituation.setText(o.toString());
            }
            currentEditor = null;
        }, "nursingrecords.nursingprocess.dlgplanung.lblSituation", txtSituation.getText(), null);
        currentEditor.setVisible(true);
    }

    private void btnPopoutGoalActionPerformed(ActionEvent e) {
        currentEditor = new DlgYesNo(SYSConst.icon48edit, o -> {
            if (o != null) {
                txtZiele.setText(o.toString());
            }
            currentEditor = null;
        }, "nursingrecords.nursingprocess.dlgplanung.lblGoal", txtZiele.getText(), null);
        currentEditor.setVisible(true);
    }

    /**
     * Reasons why you couldn't save it
     *
     * @return
     */
    private boolean saveOK() {

        if (txtStichwort.getText().trim().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.stichwortxx"), DisplayMessage.WARNING));
            return false;
        }

        if (jdcKontrolle.getDate() == null) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.datumxx"), DisplayMessage.WARNING));
            return false;
        }

        if (cmbKategorie.getSelectedItem() == null) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.kategoriexx"), DisplayMessage.WARNING));
            return false;
        }

        if (nursingProcess.getInterventionSchedule().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.schedulexx"), DisplayMessage.WARNING));
            return false;
        }

        if (txtSituation.getText().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.situationxx"), DisplayMessage.WARNING));
            return false;
        }

        if (txtZiele.getText().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.goalxx"), DisplayMessage.WARNING));
            return false;
        }

        return true;

    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel5 = new JPanel();
        lblTopic = new JLabel();
        txtStichwort = new JTextField();
        lblCat = new JLabel();
        cmbKategorie = new JComboBox<>();
        panel4 = new JPanel();
        lblSituation = new JLabel();
        btnPopoutSituation = new JButton();
        jScrollPane3 = new JScrollPane();
        txtSituation = new JTextArea();
        panel5 = new JPanel();
        lblGoal = new JLabel();
        btnPopoutGoal = new JButton();
        jScrollPane1 = new JScrollPane();
        txtZiele = new JTextArea();
        lblFirstRevision = new JLabel();
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
                "fill:14dlu, $lgap, fill:default:grow, $rgap, pref, $lgap, 14dlu"));

        //======== jPanel5 ========
        {
            jPanel5.setLayout(new FormLayout(
                    "default, $lcgap, default:grow",
                    "fill:default, $rgap, default, 2*($lgap, fill:default:grow), $lgap, 70dlu, $lgap, pref"));

            //---- lblTopic ----
            lblTopic.setFont(new Font("Arial", Font.PLAIN, 14));
            lblTopic.setText("Stichwort");
            jPanel5.add(lblTopic, CC.xy(1, 1, CC.DEFAULT, CC.TOP));

            //---- txtStichwort ----
            txtStichwort.setFont(new Font("Arial", Font.BOLD, 20));
            txtStichwort.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtStichwortFocusGained(e);
                }
            });
            jPanel5.add(txtStichwort, CC.xy(3, 1));

            //---- lblCat ----
            lblCat.setFont(new Font("Arial", Font.PLAIN, 14));
            lblCat.setText("Kategorie");
            jPanel5.add(lblCat, CC.xy(1, 3));

            //---- cmbKategorie ----
            cmbKategorie.setModel(new DefaultComboBoxModel<>(new String[]{
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
            }));
            cmbKategorie.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel5.add(cmbKategorie, CC.xy(3, 3));

            //======== panel4 ========
            {
                panel4.setLayout(new BorderLayout());

                //---- lblSituation ----
                lblSituation.setFont(new Font("Arial", Font.PLAIN, 14));
                lblSituation.setText("Situation");
                panel4.add(lblSituation, BorderLayout.CENTER);

                //---- btnPopoutSituation ----
                btnPopoutSituation.setText(null);
                btnPopoutSituation.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/popup.png")));
                btnPopoutSituation.setBorderPainted(false);
                btnPopoutSituation.setContentAreaFilled(false);
                btnPopoutSituation.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/pressed.png")));
                btnPopoutSituation.setBorder(null);
                btnPopoutSituation.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnPopoutSituation.addActionListener(e -> btnPopoutSituationActionPerformed(e));
                panel4.add(btnPopoutSituation, BorderLayout.EAST);
            }
            jPanel5.add(panel4, CC.xy(1, 5, CC.DEFAULT, CC.TOP));

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

            //======== panel5 ========
            {
                panel5.setLayout(new BorderLayout());

                //---- lblGoal ----
                lblGoal.setFont(new Font("Arial", Font.PLAIN, 14));
                lblGoal.setText("Ziele");
                panel5.add(lblGoal, BorderLayout.CENTER);

                //---- btnPopoutGoal ----
                btnPopoutGoal.setText(null);
                btnPopoutGoal.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/popup.png")));
                btnPopoutGoal.setBorderPainted(false);
                btnPopoutGoal.setContentAreaFilled(false);
                btnPopoutGoal.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/pressed.png")));
                btnPopoutGoal.setBorder(null);
                btnPopoutGoal.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnPopoutGoal.addActionListener(e -> btnPopoutGoalActionPerformed(e));
                panel5.add(btnPopoutGoal, BorderLayout.EAST);
            }
            jPanel5.add(panel5, CC.xy(1, 7, CC.DEFAULT, CC.TOP));

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

            //---- lblFirstRevision ----
            lblFirstRevision.setFont(new Font("Arial", Font.PLAIN, 14));
            lblFirstRevision.setText("Erste Kontrolle am");
            jPanel5.add(lblFirstRevision, CC.xy(1, 11));

            //---- jdcKontrolle ----
            jdcKontrolle.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel5.add(jdcKontrolle, CC.xy(3, 11));
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
                        new Object[][]{
                                {null, null, null, null},
                                {null, null, null, null},
                                {null, null, null, null},
                                {null, null, null, null},
                        },
                        new String[]{
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
                btnAddIntervention.addActionListener(e -> btnAddInterventionActionPerformed(e));
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
            btnCancel.addActionListener(e -> btnCancelActionPerformed(e));
            panel1.add(btnCancel);

            //---- btnSave ----
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.setText(null);
            btnSave.addActionListener(e -> btnSaveActionPerformed(e));
            panel1.add(btnSave);
        }
        contentPane.add(panel1, CC.xy(5, 5, CC.RIGHT, CC.DEFAULT));
        setSize(1145, 695);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents


    private void btnCancelActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        resultNursingProcess = null;
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void jspPlanungComponentResized(ComponentEvent evt) {//GEN-FIRST:event_jspPlanungComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        if (tblPlanung.getRowCount() <= 0) {
            return;
        }
        Dimension dim = jsp.getSize();
        int textWidth = dim.width - 25;
        TableColumnModel tcm1 = tblPlanung.getColumnModel();
        tcm1.getColumn(0).setPreferredWidth(textWidth);
        tcm1.getColumn(0).setHeaderValue(SYSTools.xx("nursingrecords.nursingprocess.interventions"));
    }//GEN-LAST:event_jspPlanungComponentResized

    private void btnSaveActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (saveOK()) {
            save();
            resultNursingProcess = nursingProcess;
            dispose();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void tblPlanungMousePressed(MouseEvent evt) {//GEN-FIRST:event_tblPlanungMousePressed
        if (!SwingUtilities.isRightMouseButton(evt)) {
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
        JMenuItem itemPopupDelete = new JMenuItem(SYSTools.xx("misc.commands.delete"), SYSConst.icon22delete);
        itemPopupDelete.addActionListener(evt12 -> {
            for (int row13 : tblPlanung.getSelectedRows()) {
//                listInterventionSchedule2Remove.add(((TMPlan) tblPlanung.getModel()).getInterventionSchedule(row13));
                nursingProcess.getInterventionSchedule().remove(((TMPlan) tblPlanung.getModel()).getInterventionSchedule(row13));
            }
            ((TMPlan) tblPlanung.getModel()).fireTableDataChanged();
        });
        menu.add(itemPopupDelete);

        /***
         *      _ _                 ____                        ____       _              _       _
         *     (_) |_ ___ _ __ ___ |  _ \ ___  _ __  _   _ _ __/ ___|  ___| |__   ___  __| |_   _| | ___
         *     | | __/ _ \ '_ ` _ \| |_) / _ \| '_ \| | | | '_ \___ \ / __| '_ \ / _ \/ _` | | | | |/ _ \
         *     | | ||  __/ | | | | |  __/ (_) | |_) | |_| | |_) |__) | (__| | | |  __/ (_| | |_| | |  __/
         *     |_|\__\___|_| |_| |_|_|   \___/| .__/ \__,_| .__/____/ \___|_| |_|\___|\__,_|\__,_|_|\___|
         *                                    |_|         |_|
         */
        final JMenuItem itemPopupSchedule = new JMenuItem(SYSTools.xx("misc.commands.editsheduling"), SYSConst.icon22clock);
        itemPopupSchedule.addActionListener(evt1 -> {
            final JidePopup popup = new JidePopup();

            /**
             * This routine uses the <b>first</b> element of the selection as the template for editing
             * the schedule. After the edit it clones this "template", removes the original
             * InterventionSchedules (copying the apropriate Intervention of every single
             * Schedule first) and finally creates new schedules and adds them to
             * the NursingProcess.
             *
             * The user can select more than one schedule (for deleting), but this makes no sense
             * for the editing function. Therefore we only use the first selection and ignore the rest.
             */
            int myRow = tblPlanung.getSelectedRows()[0];
            InterventionSchedule firstSelection = ((TMPlan) tblPlanung.getModel()).getInterventionSchedule(myRow).clone();

            JPanel dlg = new PnlSchedule(firstSelection, o -> {
                if (o != null) {
                    InterventionSchedule template = (InterventionSchedule) o; //contains the template to be copied over to the others
                    ArrayList<InterventionSchedule> selected = new ArrayList<>();
                    for (int row1 : tblPlanung.getSelectedRows()) {
                        selected.add(((TMPlan) tblPlanung.getModel()).getInterventionSchedule(row1));
                    }

                    InterventionScheduleTools.copySchedule(template, selected, nursingProcess);

                    popup.hidePopup();
                    Collections.sort(nursingProcess.getInterventionSchedule());
                    ((TMPlan) tblPlanung.getModel()).fireTableDataChanged();
                }
            });

            popup.setMovable(false);
            popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
            popup.getContentPane().add(dlg);
            popup.setOwner(jspPlanung);
            popup.removeExcludedComponent(jspPlanung);
            popup.setDefaultFocusComponent(dlg);

            GUITools.showPopup(popup, SwingConstants.SOUTH_WEST);
        });
        menu.add(itemPopupSchedule);


        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }//GEN-LAST:event_tblPlanungMousePressed


    private void save() {
        nursingProcess.setTopic(txtStichwort.getText().trim());
        nursingProcess.setSituation(txtSituation.getText().trim());
        nursingProcess.setGoal(txtZiele.getText().trim());
        nursingProcess.setNextEval(jdcKontrolle.getDate());
        nursingProcess.setCategory((ResInfoCategory) cmbKategorie.getSelectedItem());
        nursingProcess.getCommontags().clear();
        nursingProcess.getCommontags().addAll(pnlCommonTags.getListSelectedTags());
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel5;
    private JLabel lblTopic;
    private JTextField txtStichwort;
    private JLabel lblCat;
    private JComboBox<String> cmbKategorie;
    private JPanel panel4;
    private JLabel lblSituation;
    private JButton btnPopoutSituation;
    private JScrollPane jScrollPane3;
    private JTextArea txtSituation;
    private JPanel panel5;
    private JLabel lblGoal;
    private JButton btnPopoutGoal;
    private JScrollPane jScrollPane1;
    private JTextArea txtZiele;
    private JLabel lblFirstRevision;
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
