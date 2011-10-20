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
import op.tools.ListElement;
import op.tools.SYSTools;

import javax.swing.*;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author tloehr
 */
public class DlgProdukt extends javax.swing.JDialog {
    private AtomicLong mpid;

    /**
     * Creates new form DlgProdukt
     */
    public DlgProdukt(JFrame parent, AtomicLong mpid, String template) {
        super(parent, true);
        this.mpid = mpid;
        initComponents();
        SYSTools.centerOnParent(parent, this);
        txtBezeichnung.setText(template);
        ResultSet rs1 = op.tools.DBRetrieve.getResultSet("MPHersteller", new String[]{"MPHID", "Firma", "Ort"}, new String[]{"Firma", "Ort"});
        cmbHersteller.setModel(SYSTools.rs2cmb(rs1, true));
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Erzeugter Quelltext ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        txtBezeichnung = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        btnCancel = new javax.swing.JButton();
        btnOK = new javax.swing.JButton();
        cmbHersteller = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        btnEditHersteller = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel1.setText("Neues Medizinprodukt");

        jLabel2.setText("Produktname:");

        txtBezeichnung.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtBezeichnungCaretUpdate(evt);
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

        cmbHersteller.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Eintrag 1", "Eintrag 2", "Eintrag 3", "Eintrag 4"}));
        cmbHersteller.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbHerstellerItemStateChanged(evt);
            }
        });

        jLabel3.setText("Hersteller:");

        btnEditHersteller.setBackground(java.awt.Color.white);
        btnEditHersteller.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/edit.png")));
        btnEditHersteller.setToolTipText("Neuen Hersteller eingeben.");
        btnEditHersteller.setBorderPainted(false);
        btnEditHersteller.setOpaque(false);
        btnEditHersteller.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditHerstellerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                                        .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                                        .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(btnOK)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnCancel))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jLabel3))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addComponent(cmbHersteller, 0, 307, Short.MAX_VALUE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnEditHersteller, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                                        .addComponent(txtBezeichnung, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE))))
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
                                        .addComponent(txtBezeichnung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel3)
                                                .addComponent(cmbHersteller, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(btnEditHersteller))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnCancel)
                                        .addComponent(btnOK))
                                .addContainerGap())
        );
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - 470) / 2, (screenSize.height - 192) / 2, 470, 192);
    }// </editor-fold>//GEN-END:initComponents

    private void cmbHerstellerItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbHerstellerItemStateChanged
        btnOK.setEnabled(!txtBezeichnung.getText().equals("") && cmbHersteller.getSelectedIndex() > 0);
    }//GEN-LAST:event_cmbHerstellerItemStateChanged

    private void btnEditHerstellerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditHerstellerActionPerformed
        new DlgMedHersteller(this);
        ResultSet rs1 = op.tools.DBRetrieve.getResultSet("MPHersteller", new String[]{"MPHID", "Firma", "Ort"}, new String[]{"Firma", "Ort"});
        cmbHersteller.setModel(SYSTools.rs2cmb(rs1, true));
    }//GEN-LAST:event_btnEditHerstellerActionPerformed

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        HashMap alreadyExist = op.tools.DBRetrieve.getSingleRecord("MProdukte", "Bezeichnung", txtBezeichnung.getText());
        if (alreadyExist == null) {
            long mphid = ((ListElement) cmbHersteller.getSelectedItem()).getPk();
            HashMap hm = new HashMap();
            hm.put("Bezeichnung", txtBezeichnung.getText());
            hm.put("MPHID", mphid);
            hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
            long pk = DBHandling.insertRecord("MProdukte", hm);
            this.mpid.set(pk);
            hm.clear();
            dispose();
        } else {
            alreadyExist.clear();
        }
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtBezeichnungCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBezeichnungCaretUpdate
        btnOK.setEnabled(!txtBezeichnung.getText().equals("") && cmbHersteller.getSelectedIndex() > 0);
    }//GEN-LAST:event_txtBezeichnungCaretUpdate


    // Variablendeklaration - nicht modifizieren//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnEditHersteller;
    private javax.swing.JButton btnOK;
    private javax.swing.JComboBox cmbHersteller;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField txtBezeichnung;
    // Ende der Variablendeklaration//GEN-END:variables

}