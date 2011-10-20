/*
 * OffenePflege
 * Copyright (C) 2008 Torsten Löhr
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

package op.care.med;

import op.OPDE;
import op.tools.DBHandling;
import op.tools.*;

import javax.swing.*;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class DlgStoff extends javax.swing.JDialog {
    private long dafid;
    private boolean ignoreCaret;

    /**
     * Creates new form DlgStoff
     */
    public DlgStoff(JFrame parent, String title, long dafid) {
        super(parent, true);
        ignoreCaret = true;
        this.dafid = dafid;
        initComponents();
        txtStaerke.setValue(new Float(0.00d));
        cmbEinheit.setModel(new DefaultComboBoxModel(SYSConst.STAERKE));
        cmbStofftyp.setModel(new DefaultComboBoxModel(SYSConst.STOFFTYP));
        cmbStoffe.setModel(new DefaultComboBoxModel());
        txtStoff.setText("");
        ignoreCaret = false;
        SYSTools.centerOnParent(parent, this);
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        txtStoff = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        btnCancel = new javax.swing.JButton();
        btnOK = new javax.swing.JButton();
        cmbStoffe = new javax.swing.JComboBox();
        btnAdd = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        txtStaerke = new javax.swing.JFormattedTextField(new DecimalFormat("####.##"));
        cmbEinheit = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        cmbStofftyp = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel1.setText("Zusammensetzung");

        jLabel2.setText("Stoffname:");

        txtStoff.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtStoffCaretUpdate(evt);
            }
        });
        txtStoff.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStoffFocusGained(evt);
            }
        });

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/cancel.png")));
        btnCancel.setText("Abbrechen");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/apply.png")));
        btnOK.setText("OK");
        btnOK.setEnabled(false);
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        cmbStoffe.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        cmbStoffe.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbStoffeItemStateChanged(evt);
            }
        });

        btnAdd.setBackground(java.awt.Color.white);
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/edit_add.png")));
        btnAdd.setBorder(null);
        btnAdd.setBorderPainted(false);
        btnAdd.setEnabled(false);
        btnAdd.setOpaque(false);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        jLabel3.setText("St\u00e4rke:");

        txtStaerke.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtStaerke.setText("0.0");

        cmbEinheit.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        jLabel4.setText("Stofftyp:");

        cmbStofftyp.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                                        .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                                        .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jLabel4))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(cmbStoffe, 0, 324, Short.MAX_VALUE)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(txtStoff, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnAdd)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                .addComponent(cmbStofftyp, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                        .addComponent(txtStaerke, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(cmbEinheit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(btnOK)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnCancel)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(txtStoff, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnAdd))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbStoffe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(txtStaerke, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cmbEinheit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cmbStofftyp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnCancel)
                                        .addComponent(btnOK))
                                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[]{cmbEinheit, txtStaerke});

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - 437) / 2, (screenSize.height - 251) / 2, 437, 251);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        if (JOptionPane.showConfirmDialog(this, "'" + txtStoff.getText() + "' \n\nSoll dieser Wirkstoff neu hinzugefügt werden ?", "Neue Wirkstoff", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;
        HashMap hm = new HashMap();
        hm.put("Bezeichnung", txtStoff.getText());
        hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
        DBHandling.insertRecord("MPStoffe", hm);
        txtStoffCaretUpdate(null);
    }//GEN-LAST:event_btnAddActionPerformed

    private void cmbStoffeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbStoffeItemStateChanged
        ListElement le = (ListElement) cmbStoffe.getSelectedItem();
        ignoreCaret = true; // Damit wir keine Rekursion durch die Listener bekommen
        txtStoff.setText(le.getData());
        btnOK.setEnabled(true);
        ignoreCaret = false;
    }//GEN-LAST:event_cmbStoffeItemStateChanged

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtStoffFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStoffFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtStoffFocusGained

    private void txtStoffCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtStoffCaretUpdate
        if (ignoreCaret || txtStoff.getText().equals("")) return;
        // Sobald man rumtippt, kann man erstmal nicht mehr speichern.
        btnOK.setEnabled(false);
        HashMap where = new HashMap();
        where.put("Bezeichnung", new Object[]{"%" + txtStoff.getText() + "%", "like"});
        ResultSet rs = DBRetrieve.getResultSet("MPStoffe", new String[]{"StoffID", "Bezeichnung"}, where, new String[]{"Bezeichnung"});
        cmbStoffe.setModel(SYSTools.rs2cmb(rs));
        // Wenn nur einer in der Liste steht kann man Speichern.
        btnOK.setEnabled(cmbStoffe.getModel().getSize() == 1);
        // Wenn die Liste leer ist, kann man einen neuen hinzufügen.
        btnAdd.setEnabled(cmbStoffe.getModel().getSize() == 0);
    }//GEN-LAST:event_txtStoffCaretUpdate

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        HashMap hm = new HashMap();
        ListElement le = (ListElement) cmbStoffe.getSelectedItem();
        Number staerke = (Number) txtStaerke.getValue();
        hm.put("StoffID", le.getPk());
        hm.put("DafID", dafid);
        hm.put("Staerke", staerke.doubleValue());
        hm.put("Dimension", cmbEinheit.getSelectedIndex());
        hm.put("Stofftyp", cmbStofftyp.getSelectedIndex());
        hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
        if (DBHandling.insertRecord("MPZusammensetzung", hm) > -1) {
            hm.clear();
            hm.put("Signatur", "");
            DBHandling.updateRecord("MPDarreichung", hm, "DafID", dafid);
        }
        dispose();
    }//GEN-LAST:event_btnOKActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JComboBox cmbEinheit;
    private javax.swing.JComboBox cmbStoffe;
    private javax.swing.JComboBox cmbStofftyp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JFormattedTextField txtStaerke;
    private javax.swing.JTextField txtStoff;
    // End of variables declaration//GEN-END:variables

}