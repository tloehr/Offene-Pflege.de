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
package op.controlling;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import entity.qms.Qmsplan;
import entity.qms.Qmssched;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.*;
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
import java.util.HashSet;

/**
 * @author root
 */
public class DlgQMSPlan extends MyJDialog {
    public static final String internalClassID = "opde.controlling.qms.dlgqmsplan";
    private Qmsplan qmsplan;
    private Closure actionBlock;
    private JPopupMenu menu;
    private PnlCommonTags pnlCommonTags;

    /**
     * Creates new form DlgNursingProcess
     */
    public DlgQMSPlan(Qmsplan qmsplan, Closure actionBlock) {
        super(false);
        this.qmsplan = qmsplan;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
        setVisible(true);
    }

    private void initDialog() {

//        tblScheds.setModel(new DefaultTableModel());

        pnlCommonTags = new PnlCommonTags(qmsplan.getCommontags(), true);
        pnlLeft.add(pnlCommonTags, CC.xy(3, 5));

        lblTitle.setText(SYSTools.xx("misc.msg.title"));
        lblDescription.setText(SYSTools.xx("misc.msg.description"));
        lblTags.setText(SYSTools.xx("misc.msg.tags"));

        txtTitle.setText(qmsplan.getTitle());
        txtDescription.setText(qmsplan.getDescription());

//        reloadMeasures();
    }

    @Override
    public void dispose() {
        super.dispose();

        if (qmsplan == null) {
            actionBlock.execute(null);
        } else {
            actionBlock.execute(qmsplan);
        }
    }

//    private void reloadMeasures() {
//        tblScheds.setModel(new TMQMScheds(qmsplan));
//        tblScheds.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//        tblScheds.getColumnModel().getColumn(TMQMScheds.COL_TXT).setCellRenderer(new RNDHTML());
//        tblScheds.getColumnModel().getColumn(TMQMScheds.COL_TXT).setHeaderValue(SYSTools.xx("misc.msg.measures"));
//    }


//    private void btnAddQMSActionPerformed(ActionEvent e) {
//        final JidePopup popup = new JidePopup();
//        PnlQMSSchedule pnlQMSSchedule = new PnlQMSSchedule(new Qmssched(qmsplan), new Closure() {
//            @Override
//            public void execute(Object o) {
//                popup.hidePopup();
//                if (o != null) {
//                    qmsplan.getQmsschedules().add((Qmssched) o);
//                    reloadMeasures();
//                }
//            }
//        });
//
//        popup.setMovable(false);
//        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//
//        popup.setOwner(btnAddQMS);
//        popup.removeExcludedComponent(btnAddQMS);
//        popup.getContentPane().add(pnlQMSSchedule);
//        popup.setDefaultFocusComponent(pnlQMSSchedule);
//        GUITools.showPopup(popup, SwingConstants.NORTH_WEST);
//    }

    private void txtTitleFocusGained(FocusEvent e) {
        ((JTextComponent) e.getSource()).selectAll();
    }

    private void txtDescriptionFocusGained(FocusEvent e) {
        ((JTextComponent) e.getSource()).selectAll();
    }

    /**
     * Reasons why you couldn't save it
     *
     * @return
     */
    private boolean saveOK() {

        if (SYSTools.tidy(txtTitle.getText()).isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("opde.controlling.qms.dlgqmsplan.titlexx"), DisplayMessage.WARNING));
            return false;
        }

//
//        if (cmbKategorie.getSelectedItem() == null) {
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.kategoriexx"), DisplayMessage.WARNING));
//            return false;
//        }
//
//        if (nursingProcess.getInterventionSchedule().isEmpty()) {
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.schedulexx"), DisplayMessage.WARNING));
//            return false;
//        }
//
//        if (txtDescription.getText().isEmpty()) {
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("opde.controlling.qms.dlgqmsplan.descriptionxx"), DisplayMessage.WARNING));
//            return false;
//        }
//
//        if (txtZiele.getText().isEmpty()) {
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.goalxx"), DisplayMessage.WARNING));
//            return false;
//        }

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
        pnlLeft = new JPanel();
        lblTitle = new JLabel();
        txtTitle = new JTextField();
        jScrollPane3 = new JScrollPane();
        txtDescription = new JTextArea();
        lblDescription = new JLabel();
        lblTags = new JLabel();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnSave = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "14dlu, $lcgap, pref:grow, $ugap, pref",
            "fill:14dlu, $lgap, fill:default:grow, $rgap, pref, $lgap, 14dlu"));

        //======== pnlLeft ========
        {
            pnlLeft.setLayout(new FormLayout(
                "default, $lcgap, default:grow",
                "fill:default, $rgap, fill:default:grow, $lgap, pref"));

            //---- lblTitle ----
            lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
            lblTitle.setText("Stichwort");
            pnlLeft.add(lblTitle, CC.xy(1, 1, CC.DEFAULT, CC.TOP));

            //---- txtTitle ----
            txtTitle.setFont(new Font("Arial", Font.BOLD, 20));
            txtTitle.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtTitleFocusGained(e);
                }
            });
            pnlLeft.add(txtTitle, CC.xy(3, 1));

            //======== jScrollPane3 ========
            {

                //---- txtDescription ----
                txtDescription.setColumns(20);
                txtDescription.setLineWrap(true);
                txtDescription.setRows(5);
                txtDescription.setWrapStyleWord(true);
                txtDescription.setFont(new Font("Arial", Font.PLAIN, 14));
                txtDescription.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtDescriptionFocusGained(e);
                    }
                });
                jScrollPane3.setViewportView(txtDescription);
            }
            pnlLeft.add(jScrollPane3, CC.xy(3, 3));

            //---- lblDescription ----
            lblDescription.setFont(new Font("Arial", Font.PLAIN, 14));
            lblDescription.setText("Situation");
            pnlLeft.add(lblDescription, CC.xy(1, 3, CC.DEFAULT, CC.TOP));

            //---- lblTags ----
            lblTags.setFont(new Font("Arial", Font.PLAIN, 14));
            lblTags.setText("Markierung");
            pnlLeft.add(lblTags, CC.xy(1, 5));
        }
        contentPane.add(pnlLeft, CC.xy(3, 3, CC.DEFAULT, CC.FILL));

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
        contentPane.add(panel1, CC.xy(3, 5, CC.RIGHT, CC.DEFAULT));
        setSize(710, 495);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents


    private void btnCancelActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        qmsplan = null;
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

//    private void jspPlanungComponentResized(ComponentEvent evt) {//GEN-FIRST:event_jspPlanungComponentResized
//        JScrollPane jsp = (JScrollPane) evt.getComponent();
//        if (tblScheds.getRowCount() <= 0) {
//            return;
//        }
//        Dimension dim = jsp.getSize();
//        int textWidth = dim.width - 25;
//        TableColumnModel tcm1 = tblScheds.getColumnModel();
//        tcm1.getColumn(0).setPreferredWidth(textWidth);
//        tcm1.getColumn(0).setHeaderValue(SYSTools.xx("misc.msg.measures"));
//    }//GEN-LAST:event_jspPlanungComponentResized

    private void btnSaveActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (saveOK()) {
            save();
            dispose();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

//    private void tblPlanungMousePressed(MouseEvent evt) {//GEN-FIRST:event_tblPlanungMousePressed
//        if (!SwingUtilities.isRightMouseButton(evt)) {
//            return;
//        }
//
//        Point p = evt.getPoint();
//        ListSelectionModel lsm = tblScheds.getSelectionModel();
//        final int startRow = tblScheds.rowAtPoint(p);
//        if (lsm.isSelectionEmpty() || (lsm.getMinSelectionIndex() == lsm.getMaxSelectionIndex())) {
//            lsm.setSelectionInterval(startRow, startRow);
//        }
//
//        menu = new JPopupMenu();
//
//        /***
//         *      _ _                 ____                         ____       _      _
//         *     (_) |_ ___ _ __ ___ |  _ \ ___  _ __  _   _ _ __ |  _ \  ___| | ___| |_ ___
//         *     | | __/ _ \ '_ ` _ \| |_) / _ \| '_ \| | | | '_ \| | | |/ _ \ |/ _ \ __/ _ \
//         *     | | ||  __/ | | | | |  __/ (_) | |_) | |_| | |_) | |_| |  __/ |  __/ ||  __/
//         *     |_|\__\___|_| |_| |_|_|   \___/| .__/ \__,_| .__/|____/ \___|_|\___|\__\___|
//         *                                    |_|         |_|
//         */
//        JMenuItem itemPopupDelete = new JMenuItem(SYSTools.xx("misc.commands.delete"), SYSConst.icon22delete);
//        itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//
//
//                for (int r : tblScheds.getSelectedRows()) {
//
////                    listInterventionSchedule2Remove.add(((TMPlan) tblPlanung.getModel()).getInterventionSchedule(row));
//
//                    qmsplan.getQmsschedules().remove(((TMQMScheds) tblScheds.getModel()).getSchedule(r));
//                }
//                ((TMQMScheds) tblScheds.getModel()).fireTableDataChanged();
//            }
//        });
//        menu.add(itemPopupDelete);
//
//        /***
//         *      _ _                 ____                        ____       _              _       _
//         *     (_) |_ ___ _ __ ___ |  _ \ ___  _ __  _   _ _ __/ ___|  ___| |__   ___  __| |_   _| | ___
//         *     | | __/ _ \ '_ ` _ \| |_) / _ \| '_ \| | | | '_ \___ \ / __| '_ \ / _ \/ _` | | | | |/ _ \
//         *     | | ||  __/ | | | | |  __/ (_) | |_) | |_| | |_) |__) | (__| | | |  __/ (_| | |_| | |  __/
//         *     |_|\__\___|_| |_| |_|_|   \___/| .__/ \__,_| .__/____/ \___|_| |_|\___|\__,_|\__,_|_|\___|
//         *                                    |_|         |_|
//         */
//        final JMenuItem itemPopupEditSchedule = new JMenuItem(SYSTools.xx("misc.commands.editsheduling"), SYSConst.icon22clock);
//
//        itemPopupEditSchedule.addActionListener(new java.awt.event.ActionListener() {
//
//
//            final Qmssched selectedQmssched = ((TMQMScheds) tblScheds.getModel()).getSchedule(startRow);
//
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                final JidePopup popup = new JidePopup();
//                PnlQMSSchedule pnlQMSSchedule = new PnlQMSSchedule(selectedQmssched, new Closure() {
//                    @Override
//                    public void execute(Object o) {
//                        popup.hidePopup();
//                        if (o != null) {
//                            qmsplan.getQmsschedules().remove(selectedQmssched);
//                            qmsplan.getQmsschedules().add((Qmssched) o);
//                            ((TMQMScheds) tblScheds.getModel()).fireTableDataChanged();
//                        }
//                    }
//                });
//
//                popup.setMovable(false);
//                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//
//                popup.setOwner(btnAddQMS);
//                popup.removeExcludedComponent(btnAddQMS);
//                popup.getContentPane().add(pnlQMSSchedule);
//                popup.setDefaultFocusComponent(pnlQMSSchedule);
//                GUITools.showPopup(popup, SwingConstants.NORTH_WEST);
//            }
//        });
//        menu.add(itemPopupEditSchedule);
//        itemPopupEditSchedule.setEnabled(true);
//
//
//        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
//    }//GEN-LAST:event_tblPlanungMousePressed


    private void save() {
        qmsplan.setTitle(SYSTools.tidy(txtTitle.getText()));
        qmsplan.setDescription(SYSTools.tidy(txtDescription.getText()));
        qmsplan.getCommontags().clear();
        qmsplan.getCommontags().addAll(pnlCommonTags.getListSelectedTags());




    }

    //GEN-BEGIN:variables
    private JPanel pnlLeft;
    private JLabel lblTitle;
    private JTextField txtTitle;
    private JScrollPane jScrollPane3;
    private JTextArea txtDescription;
    private JLabel lblDescription;
    private JLabel lblTags;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnSave;
    //GEN-END:variables
}
