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

package op.care.med.vorrat;

import op.OPDE;
import op.tools.DBHandling;
import op.tools.DBRetrieve;
import op.tools.GuiChecks;
import op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

/**
 *
 */
public class DlgEditBuchung extends javax.swing.JDialog {
    private double menge;
    private long bestid;
    private Component parent;
    private double bestandsumme;
    private double packgroesse;

    public DlgEditBuchung(java.awt.Frame parent, long bestid) {
        super(parent, true);
        this.parent = parent;
        this.bestid = bestid;
        initDialog();
    }

    public DlgEditBuchung(JDialog parent, long bestid) {
        super(parent, true);
        this.parent = parent;
        this.bestid = bestid;
        initDialog();
    }

    private void initDialog() {
        setTitle(SYSTools.getWindowTitle("Einzelbuchung"));
//        bestandsumme = op.care.med.DBHandling.getBestandSumme(bestid);
        BigInteger mpid = (BigInteger) DBRetrieve.getSingleValue("MPBestand", "MPID", "BestID", bestid);

        if (mpid != null && mpid.longValue() > 0) {
            packgroesse = ((BigDecimal) DBRetrieve.getSingleValue("MPackung", "Inhalt", "MPID", mpid.longValue())).doubleValue();
        } else {
            packgroesse = Double.MAX_VALUE;
        }

        initComponents();
        SYSTools.centerOnParent(parent, this);
        txtMenge.setText("0.00");
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
        jScrollPane1 = new javax.swing.JScrollPane();
        txtText = new javax.swing.JTextArea();
        txtMenge = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnBuchung = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblEinheit = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel1.setText("Einzelbuchung");

        txtText.setColumns(20);
        txtText.setRows(5);
        jScrollPane1.setViewportView(txtText);

        txtMenge.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMenge.setText("jTextField1");
        txtMenge.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtMengeCaretUpdate(evt);
            }
        });

        jLabel2.setText("Menge:");

        btnBuchung.setText("Buchen");
        btnBuchung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuchungActionPerformed(evt);
            }
        });

        btnCancel.setText("Abbrechen");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        lblEinheit.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblEinheit.setText("jLabel4");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnBuchung)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnCancel))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtMenge, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblEinheit, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(lblEinheit)
                                        .addComponent(txtMenge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnCancel)
                                        .addComponent(btnBuchung))
                                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnBuchungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuchungActionPerformed
        save();
        dispose();
    }//GEN-LAST:event_btnBuchungActionPerformed

    private void save() {
        HashMap hm2 = new HashMap();
        hm2.put("Menge", menge);
        hm2.put("BestID", bestid);
        hm2.put("UKennung", OPDE.getLogin().getUser().getUKennung());
        hm2.put("Text", txtText.getText());
        hm2.put("PIT", "!NOW!");
        DBHandling.insertRecord("MPBuchung", hm2);
    }

    private void txtMengeCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtMengeCaretUpdate
        menge = GuiChecks.checkDouble(evt, false);
        if (menge < 0d) {
            btnBuchung.setEnabled(menge * -1 <= bestandsumme);
        } else if (menge > 0d) {
            btnBuchung.setEnabled(menge <= packgroesse - bestandsumme);
        } else {
            btnBuchung.setEnabled(false);
        }
    }//GEN-LAST:event_txtMengeCaretUpdate


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuchung;
    private javax.swing.JButton btnCancel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblEinheit;
    private javax.swing.JTextField txtMenge;
    private javax.swing.JTextArea txtText;
    // End of variables declaration//GEN-END:variables

}
