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

import entity.EntityTools;
import entity.verordnungen.*;
import op.tools.SYSTools;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

/**
 *
 */
public class DlgEditBuchung extends javax.swing.JDialog {
    private BigDecimal menge;
    private Component parent;
    private BigDecimal bestandsumme;
    private BigDecimal packgroesse;
    private MedBestand bestand;


    public DlgEditBuchung(java.awt.Frame parent, MedBestand bestand) {
        super(parent, true);
        this.parent = parent;
        this.bestand = bestand;
        initDialog();
    }

    public DlgEditBuchung(JDialog parent, MedBestand bestand) {
        super(parent, true);
        this.parent = parent;
        this.bestand = bestand;
        initDialog();
    }

    private void initDialog() {
        setTitle(SYSTools.getWindowTitle("Einzelbuchung"));
        bestandsumme = MedBestandTools.getBestandSumme(bestand);

        lblEinheit.setText(DarreichungTools.getPackungsEinheit(bestand.getDarreichung()));

        if (bestand.hasPackung()) {
            packgroesse = bestand.getPackung().getInhalt();
        } else {
            packgroesse = BigDecimal.valueOf(Double.MAX_VALUE);
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
        jLabel1 = new JLabel();
        jSeparator1 = new JSeparator();
        jScrollPane1 = new JScrollPane();
        txtText = new JTextArea();
        txtMenge = new JTextField();
        jLabel2 = new JLabel();
        btnBuchung = new JButton();
        btnCancel = new JButton();
        lblEinheit = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();

        //---- jLabel1 ----
        jLabel1.setFont(new Font("Dialog", Font.BOLD, 16));
        jLabel1.setText("Einzelbuchung");

        //======== jScrollPane1 ========
        {

            //---- txtText ----
            txtText.setColumns(20);
            txtText.setRows(5);
            jScrollPane1.setViewportView(txtText);
        }

        //---- txtMenge ----
        txtMenge.setHorizontalAlignment(SwingConstants.RIGHT);
        txtMenge.setText("jTextField1");
        txtMenge.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                txtMengeCaretUpdate(e);
            }
        });

        //---- jLabel2 ----
        jLabel2.setText("Menge:");

        //---- btnBuchung ----
        btnBuchung.setText("Buchen");
        btnBuchung.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnBuchungActionPerformed(e);
            }
        });

        //---- btnCancel ----
        btnCancel.setText("Abbrechen");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCancelActionPerformed(e);
            }
        });

        //---- lblEinheit ----
        lblEinheit.setHorizontalAlignment(SwingConstants.TRAILING);
        lblEinheit.setText("jLabel4");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(jScrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                                .addComponent(jLabel1, GroupLayout.Alignment.LEADING)
                                .addComponent(jSeparator1, GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                        .addComponent(btnBuchung)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnCancel))
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtMenge, GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblEinheit, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(lblEinheit)
                                .addComponent(txtMenge, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnCancel)
                                .addComponent(btnBuchung))
                        .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnBuchungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuchungActionPerformed
        save();
        dispose();
    }//GEN-LAST:event_btnBuchungActionPerformed

    private void save() {
        MedBuchungen buchung = new MedBuchungen(bestand, menge, null, MedBuchungenTools.STATUS_KORREKTUR_MANUELL);
        buchung.setText(txtText.getText());
        EntityTools.persist(buchung);
    }

    private void txtMengeCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtMengeCaretUpdate
        menge = SYSTools.checkBigDecimal(evt, !SYSTools.MUST_BE_POSITIVE);
        if (menge.compareTo(BigDecimal.ZERO) < 0) {
            btnBuchung.setEnabled(menge.negate().compareTo(bestandsumme) <= 0);
        } else if (menge.compareTo(BigDecimal.ZERO) > 0) {
            btnBuchung.setEnabled(menge.compareTo(packgroesse.subtract(bestandsumme)) <= 0);
        } else {
            btnBuchung.setEnabled(false);
        }
    }//GEN-LAST:event_txtMengeCaretUpdate


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel jLabel1;
    private JSeparator jSeparator1;
    private JScrollPane jScrollPane1;
    private JTextArea txtText;
    private JTextField txtMenge;
    private JLabel jLabel2;
    private JButton btnBuchung;
    private JButton btnCancel;
    private JLabel lblEinheit;
    // End of variables declaration//GEN-END:variables

}
