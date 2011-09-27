/*
 * OffenePflege
 * Copyright (C) 2008 Torsten L�hr
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
 * Auf deutsch (freie �bersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie k�nnen es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation ver�ffentlicht, weitergeben und/oder modifizieren, gem�� Version 2 der Lizenz.
 *
 * Die Ver�ffentlichung dieses Programms erfolgt in der Hoffnung, da� es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT F�R EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 * 
 */
package op.care.bhp;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSConst;
import op.tools.SYSTools;
import tablerenderer.RNDHTML;

/**
 *
 * @author  root
 */
public class DlgBedarf extends javax.swing.JDialog {

    private ListSelectionListener lsl;
    private String bwkennung;

    /** Creates new form DlgBedarf */
    public DlgBedarf(java.awt.Frame parent, String bwkennung) {
        super(parent, true);
        this.bwkennung = bwkennung;
        initComponents();
        SYSTools.setBWLabel(lblBW, bwkennung);
        loadTable();
        SYSTools.centerOnParent(parent, this);
        setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Erzeugter Quelltext ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitle = new javax.swing.JLabel();
        lblBW = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jspBedarf = new javax.swing.JScrollPane();
        tblBedarf = new javax.swing.JTable();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        lblTitle.setFont(new java.awt.Font("Dialog", 1, 24));
        lblTitle.setText("Bedarfsmedikation");

        lblBW.setFont(new java.awt.Font("Dialog", 1, 18));
        lblBW.setForeground(new java.awt.Color(255, 51, 0));
        lblBW.setText("Nachname, Vorname (*GebDatum, 00 Jahre) [??1]");

        jspBedarf.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jspBedarfComponentResized(evt);
            }
        });

        tblBedarf.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jspBedarf.setViewportView(tblBedarf);

        btnOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
        btnOK.setText("\u00dcbernehmen");
        btnOK.setEnabled(false);
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
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
                    .addComponent(jspBedarf, javax.swing.GroupLayout.DEFAULT_SIZE, 903, Short.MAX_VALUE)
                    .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 903, Short.MAX_VALUE)
                    .addComponent(lblBW, javax.swing.GroupLayout.DEFAULT_SIZE, 903, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 903, Short.MAX_VALUE)
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
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBW)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jspBedarf, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOK))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jspBedarfComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspBedarfComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();
        int textWidth = dim.width - (100);
        TableColumnModel tcm1 = tblBedarf.getColumnModel();
        if (tcm1.getColumnCount() < 4) {
            return;
        }
        tcm1.getColumn(TMBedarf.COL_SIT).setPreferredWidth(textWidth / 4);
        tcm1.getColumn(TMBedarf.COL_MSSN).setPreferredWidth(textWidth / 4);
        tcm1.getColumn(TMBedarf.COL_Dosis).setPreferredWidth(textWidth / 4);
        tcm1.getColumn(TMBedarf.COL_Hinweis).setPreferredWidth(textWidth / 4);

        tcm1.getColumn(0).setHeaderValue("Situation");
        tcm1.getColumn(1).setHeaderValue("Massnahme");
        tcm1.getColumn(2).setHeaderValue("Dosis / H�ufig.");
        tcm1.getColumn(3).setHeaderValue("Hinweis");

    }//GEN-LAST:event_jspBedarfComponentResized

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        save();
        dispose();
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed
    
    /**
     * Eine Bedarfsmedikation oder eine Bedarfsmassnahme wird in die BHP
     * gesetzt und direkt abgehakt. Ist DAFID > 0 wird auch das Medikament
     * ausgebucht.
     *
     */
    private void save(){
        HashMap hm = new HashMap();
        int row = tblBedarf.getSelectedRow();
        TMBedarf tm = (TMBedarf) tblBedarf.getModel();
        double dosis = ((Double) tm.getValueAt(row, TMBedarf.COL_MaxEDosis)).doubleValue();
        //boolean kalkulieren = ((Boolean) tm.getValueAt(row, TMBedarf.COL_KALKULIEREN)).booleanValue();
        hm.put("BHPPID",tm.getValueAt(row, TMBedarf.COL_BHPPID));
        hm.put("UKennung",OPDE.getLogin().getUser().getUKennung());
        hm.put("Soll","!NOW!");
        hm.put("Ist","!NOW!");
        hm.put("SZeit",SYSConst.UZ);
        hm.put("Dosis", dosis);
        hm.put("Status",TMBHP.STATUS_ERLEDIGT);
        hm.put("_mdate","!NOW!");
        hm.put("Dauer",0);
        
        Connection db = OPDE.getDb().db;
        try {
            // Hier beginnt eine Transaktion
            db.setAutoCommit(false);
            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            db.commit();
            
            long bhpid = op.tools.DBHandling.insertRecord("BHP",hm);
            long dafid = ((Long) tm.getValueAt(row, TMBedarf.COL_DAFID)).longValue();
            hm.clear();            
            
            op.care.med.DBHandling.entnahmeVorrat(dafid, bwkennung, dosis, true, bhpid);
                        
            db.commit();
            db.setAutoCommit(true);
        } catch (SQLException ex) {
            try {
                db.rollback();
            } catch (SQLException ex1) {
                new DlgException(ex1);
                ex1.printStackTrace();
                System.exit(1);
            }
            new DlgException(ex);
        }
    }
    
    public void dispose() {
        SYSTools.unregisterListeners(this);
        super.dispose();
    }
    
    
    private void loadTable(){
        ListSelectionModel lsm = tblBedarf.getSelectionModel();
        if (lsl!= null) lsm.removeListSelectionListener(lsl);
        lsl = new HandleSelections();
        
        tblBedarf.setModel(new TMBedarf(bwkennung));
        tblBedarf.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lsm.addListSelectionListener(lsl);
        
        jspBedarf.dispatchEvent(new ComponentEvent(jspBedarf, ComponentEvent.COMPONENT_RESIZED));
        tblBedarf.getColumnModel().getColumn(0).setCellRenderer(new RNDHTML());
        tblBedarf.getColumnModel().getColumn(1).setCellRenderer(new RNDHTML());
        tblBedarf.getColumnModel().getColumn(2).setCellRenderer(new RNDHTML());
        tblBedarf.getColumnModel().getColumn(3).setCellRenderer(new RNDHTML());
//        tblBedarf.getColumnModel().getColumn(4).setCellRenderer(new RNDHTML());
    }
    
    // Variablendeklaration - nicht modifizieren//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JScrollPane jspBedarf;
    private javax.swing.JLabel lblBW;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblBedarf;
    // Ende der Variablendeklaration//GEN-END:variables
    
    class HandleSelections implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent lse) {
            // Erst reagieren wenn der Auswahl-Vorgang abgeschlossen ist.
            TableModel tm = tblBedarf.getModel();
            if (tm.getRowCount() <= 0) {
                return;
            }
            if (!lse.getValueIsAdjusting()){
                DefaultListSelectionModel lsm = (DefaultListSelectionModel) lse.getSource();
                if (lsm.isSelectionEmpty()){
                    btnOK.setEnabled(false);
                } else {
                    boolean maxErreicht = ((Boolean) tm.getValueAt(lsm.getLeadSelectionIndex(), TMBedarf.COL_MAXERREICHT)).booleanValue();
//                    boolean reichtVorrat = ((Boolean) tm.getValueAt(lsm.getLeadSelectionIndex(), TMBedarf.COL_REICHTVORRAT)).booleanValue();
//                    boolean kalkulieren = ((Boolean) tm.getValueAt(lsm.getLeadSelectionIndex(), TMBedarf.COL_KALKULIEREN)).booleanValue();
                    btnOK.setEnabled(!maxErreicht);
                }
            }
        }
    }
}
