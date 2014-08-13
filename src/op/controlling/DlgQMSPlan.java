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

import javax.swing.event.*;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.swing.JideLabel;
import entity.qms.Qmsplan;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.MyJDialog;
import op.tools.PnlCommonTags;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * @author root
 */
public class DlgQMSPlan extends MyJDialog {
    public static final String internalClassID = "opde.controlling.qms.dlgqmsplan";
    private Qmsplan qmsplan;
    private Closure actionBlock;
    private JPopupMenu menu;
    private PnlCommonTags pnlCommonTags;
    private ArrayList<Users> notifyList;


    /**
     * Creates new form DlgNursingProcess
     */
    public DlgQMSPlan(Qmsplan qmsplan, Closure actionBlock) {
        super(false);
        this.qmsplan = qmsplan;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
        pack();
        setVisible(true);
    }

    private void initDialog() {

        pnlCommonTags = new PnlCommonTags(qmsplan.getCommontags(), true, 4);
        pnlLeft.add(new JScrollPane(pnlCommonTags), CC.xywh(3, 7, 5, 1, CC.DEFAULT, CC.FILL));


        lblTitle.setText(SYSTools.xx("misc.msg.title"));
        lblDescription.setText(SYSTools.xx("misc.msg.description"));
        lblTags.setText(SYSTools.xx("misc.msg.commontags"));
        lblNotify.setText(SYSTools.xx("misc.msg.notification.list"));

        txtTitle.setText(qmsplan.getTitle());
        txtDescription.setText(qmsplan.getDescription());

        cmbNotify.setModel(new DefaultComboBoxModel(UsersTools.getUsers(false).toArray()));
        cmbNotify.setRenderer(UsersTools.getRenderer());
        lstNotify.setCellRenderer(UsersTools.getRenderer());
        cmbNotify.setSelectedItem(null);

        notifyList = new ArrayList<>(qmsplan.getNotification());
        lstNotify.setModel(SYSTools.list2dlm(notifyList));

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

    private void txtTitleFocusGained(FocusEvent e) {
        ((JTextComponent) e.getSource()).selectAll();
    }

    private void txtDescriptionFocusGained(FocusEvent e) {
        ((JTextComponent) e.getSource()).selectAll();
    }

    private void cmbNotifyItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (!notifyList.contains(e.getItem())) {
                notifyList.add((Users) e.getItem());
                lstNotify.setModel(SYSTools.list2dlm(notifyList));
            }
        }
    }

    private void lstNotifyValueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        notifyList.remove(lstNotify.getSelectedValue());
        lstNotify.setModel(SYSTools.list2dlm(notifyList));
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
        lblNotify = new JideLabel();
        cmbNotify = new JComboBox();
        jScrollPane3 = new JScrollPane();
        txtDescription = new JTextArea();
        lblDescription = new JideLabel();
        scrollPane1 = new JScrollPane();
        lstNotify = new JList();
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
                "pref, $lcgap, default:grow, $lcgap, pref, $lcgap, default:grow",
                "default, $lgap, fill:default, $rgap, fill:default:grow, $lgap, 40dlu, $rgap, default"));

            //---- lblTitle ----
            lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
            lblTitle.setText("Stichwort");
            lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
            pnlLeft.add(lblTitle, CC.xy(3, 1));

            //---- txtTitle ----
            txtTitle.setFont(new Font("Arial", Font.PLAIN, 20));
            txtTitle.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtTitleFocusGained(e);
                }
            });
            pnlLeft.add(txtTitle, CC.xy(3, 3));

            //---- lblNotify ----
            lblNotify.setText("text");
            lblNotify.setOrientation(1);
            lblNotify.setFont(new Font("Arial", Font.PLAIN, 18));
            lblNotify.setHorizontalAlignment(SwingConstants.CENTER);
            lblNotify.setClockwise(false);
            pnlLeft.add(lblNotify, CC.xywh(5, 3, 1, 3));

            //---- cmbNotify ----
            cmbNotify.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cmbNotifyItemStateChanged(e);
                }
            });
            pnlLeft.add(cmbNotify, CC.xy(7, 3));

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
            pnlLeft.add(jScrollPane3, CC.xy(3, 5));

            //---- lblDescription ----
            lblDescription.setFont(new Font("Arial", Font.PLAIN, 18));
            lblDescription.setText("Situation");
            lblDescription.setOrientation(1);
            lblDescription.setClockwise(false);
            pnlLeft.add(lblDescription, CC.xy(1, 5, CC.DEFAULT, CC.CENTER));

            //======== scrollPane1 ========
            {

                //---- lstNotify ----
                lstNotify.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                lstNotify.addListSelectionListener(new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        lstNotifyValueChanged(e);
                    }
                });
                scrollPane1.setViewportView(lstNotify);
            }
            pnlLeft.add(scrollPane1, CC.xy(7, 5));

            //---- lblTags ----
            lblTags.setFont(new Font("Arial", Font.PLAIN, 18));
            lblTags.setText("Markierung");
            lblTags.setHorizontalAlignment(SwingConstants.CENTER);
            pnlLeft.add(lblTags, CC.xywh(3, 9, 5, 1));
        }
        contentPane.add(pnlLeft, CC.xy(3, 3));

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


    private void btnSaveActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (saveOK()) {
            save();
            dispose();
        }
    }//GEN-LAST:event_btnSaveActionPerformed


    private void save() {
        qmsplan.setTitle(SYSTools.tidy(txtTitle.getText()));
        qmsplan.setDescription(SYSTools.tidy(txtDescription.getText()));
        qmsplan.getCommontags().clear();
        qmsplan.getCommontags().addAll(pnlCommonTags.getListSelectedTags());
        qmsplan.getNotification().clear();
        qmsplan.getNotification().addAll(notifyList);

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel pnlLeft;
    private JLabel lblTitle;
    private JTextField txtTitle;
    private JideLabel lblNotify;
    private JComboBox cmbNotify;
    private JScrollPane jScrollPane3;
    private JTextArea txtDescription;
    private JideLabel lblDescription;
    private JScrollPane scrollPane1;
    private JList lstNotify;
    private JLabel lblTags;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnSave;
    // End of variables declaration//GEN-END:variables
}
