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
package op.share.tools;

import op.OPDE;
import op.tools.DBHandling;
import op.tools.SYSTools;
import se.datadosen.component.RiverLayout;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class DlgRecordEdit extends javax.swing.JDialog {

    private static final int MODE_EDIT = 0; // Ein bestehender Wert wird korrigiert (UPDATE)
    private static final int MODE_NEW = 1; // Ein bestehender Wert verändert sich, der alte bleibt erhalten. (UPDATE, INSERT)
    private ResultSet rs;
    private int dlgMode; // Enthält die Angabe, in welchem Zustand sich der Dialog befindet.
    private String titel;
    private ResultSetMetaData rsmd;
    private ArrayList fieldList;
    private String table;

    /**
     * Lässt die Bearbeitung eines beliebigen Datenbank Records zu. Zur Zeit kann dieses Fenster nur mit CHAR und VARCHAR umgehen. Alle anderen werden zwar angezeigt, aber die Bearbeitung ist nicht möglich.
     * Wichtig ist, dass in der ersten Spalte <b>immer der Primärschlüssel steht<b>. Bei Änderungen wird diese Information nämlich herangezogen um den zu überschreibenden Datensatz zu identifizieren.
     *
     * @param parent Parent Frame
     * @param rs     Ein Resultset. Die Struktur des Fenster wird aus den Meta Informationen des RS übernommen. Im Mode EDIT wird die Vorbelegung der Textfelder aus dem Record übernommen auf dem Zeiger steht.
     * @param mode   EDIT oder CHANGE
     */
    public DlgRecordEdit(java.awt.Frame parent, ResultSet rs, int row, String titel) {
        super(parent, true);
        this.rs = rs;
        if (row == -1) {
            dlgMode = MODE_NEW;
        } else {
            dlgMode = MODE_EDIT;
        }
        try {
            rsmd = rs.getMetaData();
            if (dlgMode == MODE_EDIT) {
                rs.absolute(row + 1);
            }
            table = rsmd.getTableName(1);
        } catch (Exception e) {
            OPDE.getLogger().debug(e);
        }
        this.titel = titel;
        initComponents();
        initDialog();
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        pnlMain = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/apply.png"))); // NOI18N
        btnSave.setText("Speichern");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/cancel.png"))); // NOI18N
        btnCancel.setText("Abbrechen");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(btnSave)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnCancel))
                                        .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnSave, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(btnCancel, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initDialog() {
        JPanel inner = new JPanel(new RiverLayout());
        pnlMain.setViewportView(inner);
        fieldList = new ArrayList();
        try {
            int numcols = rsmd.getColumnCount();
            for (int col = 2; col <= numcols; col++) { // Fängt erst bei zwei an. In der ersten Spalte der PK.
                inner.add("p left", new JLabel(rsmd.getColumnLabel(col)));
                JTextField tf = new JTextField();
                if (dlgMode == MODE_EDIT) {
                    tf.setText(rs.getObject(col).toString());
                } else {
                    tf.setText("");
                }
                tf.setEditable(rsmd.getColumnType(col) == java.sql.Types.CHAR || rsmd.getColumnType(col) == java.sql.Types.VARCHAR);
                inner.add("tab hfill", tf);
                fieldList.add(tf);
            }
        } catch (SQLException ex) {
            OPDE.getLogger().debug(ex);
        }
        SYSTools.centerOnParent(this);
        setTitle(SYSTools.getWindowTitle(titel));
    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        try {
            HashMap hm = new HashMap();
            int numcols = rsmd.getColumnCount();
            for (int col = 2; col <= numcols; col++) {// Fängt erst bei zwei an. In der ersten Spalte der PK.
                hm.put(rsmd.getColumnName(col), ((JTextField) fieldList.get(col - 2)).getText()); // der Index von fieldList beginnt natürlich bei 0
            }
            if (dlgMode == MODE_EDIT) {
                DBHandling.updateRecord(table, hm, rsmd.getColumnName(1), rs.getObject(1));
            } else {
                DBHandling.insertRecord(table, hm);
            }
        } catch (SQLException ex) {
            OPDE.getLogger().debug(ex);
        }
        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JScrollPane pnlMain;
    // End of variables declaration//GEN-END:variables
}
