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

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import op.OPDE;
import op.care.med.DBHandling;
import op.care.med.DlgMediAssistent;
import op.tools.ListElement;
import op.tools.Bool;
import op.tools.DlgException;
import op.tools.GuiChecks;
import op.tools.SYSPrint;
import op.tools.SYSTools;

/**
 *
 * @author  tloehr
 */
public class DlgBestand extends javax.swing.JDialog {
    private boolean ignoreEvent;
    private Component parent;
    private String bwkennung;
    private String template;
    private long dafid = -1;
    private boolean medEingegeben = false;
    private boolean mengeEingegeben = false;
    private boolean bwEingegeben = false;
    private double menge;
    private double inhalt;
    private boolean flashVorrat = false;
    private Thread thread = null;
    
    public DlgBestand(JFrame parent){
        this(parent, null, null);
    }
    
    public DlgBestand(JDialog parent){
        this(parent, null, null);
    }
    
    public DlgBestand(JFrame parent, String bwkennung){
        this(parent, bwkennung, null);
    }
    
    public DlgBestand(JDialog parent, String bwkennung){
        this(parent, bwkennung, null);
    }
    
    public DlgBestand(JFrame parent, String bwkennung, String template){
        super(parent, true);
        this.parent = parent;
        this.bwkennung = bwkennung;
        this.template = template;
        initDialog();
    }
    
    public DlgBestand(JFrame parent, String bwkennung, long dafid){
        super(parent, true);
        this.parent = parent;
        this.bwkennung = bwkennung;
        this.template = null;
        this.dafid = dafid;
        initDialog();
    }
    
    public DlgBestand(JDialog parent, String bwkennung, String template){
        super(parent, true);
        this.parent = parent;
        this.bwkennung = bwkennung;
        this.template = template;
        initDialog();
    }
    
    public DlgBestand(JDialog parent, String bwkennung, long dafid){
        super(parent, true);
        this.parent = parent;
        this.bwkennung = bwkennung;
        this.template = null;
        this.dafid = dafid;
        initDialog();
    }
    
    private void initDialog(){
        ignoreEvent = true;
        initComponents();
        if (this.bwkennung != null && !this.bwkennung.equals("")){
            txtBWSuche.setText(bwkennung);
            ignoreEvent = false;
            txtBWSucheCaretUpdate(null);
            txtBWSuche.setEnabled(false);
            bwEingegeben = true;
        }
        if (this.template != null && !this.template.equals("")){
            txtMedSuche.setText(this.template);
        }
        if (this.dafid >= 0){ // Die DafID wird direkt vorgegeben.
            cmbMProdukt.setModel(op.care.med.DBHandling.getMedis(dafid));
            cmbMProduktItemStateChanged(null);
            txtMedSuche.setEnabled(false);
        }
        String name = this.getClass().getName()+"::cbDruck";
        if (OPDE.getProps().containsKey(name)){
            cbDruck.setSelected(OPDE.getProps().getProperty(name).equalsIgnoreCase("true"));
        } else {
            cbDruck.setSelected(false);
        }
        
        ignoreEvent = false;
        if (!txtMedSuche.getText().equals("")){ txtMedSucheCaretUpdate(null); }
        
        SYSTools.centerOnParent(parent, this);
        setTitle(SYSTools.getWindowTitle("Medikamente buchen"));
        setVisible(true);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Erzeugter Quelltext ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblFrage = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        txtMedSuche = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cmbMProdukt = new javax.swing.JComboBox();
        btnMed = new javax.swing.JButton();
        lblVorrat = new javax.swing.JLabel();
        cmbVorrat = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        txtBWSuche = new javax.swing.JTextField();
        cmbBW = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        txtMenge = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cmbPackung = new javax.swing.JComboBox();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        btnClose = new javax.swing.JButton();
        btnApply = new javax.swing.JButton();
        txtBemerkung = new javax.swing.JTextField();
        cbDruck = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        lblFrage.setFont(new java.awt.Font("Dialog", 1, 24));
        lblFrage.setText("Med.-Best\u00e4nde buchen");

        txtMedSuche.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtMedSucheCaretUpdate(evt);
            }
        });
        txtMedSuche.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMedSucheFocusGained(evt);
            }
        });

        jLabel1.setText("PZN oder Suchbegriff:");

        jLabel3.setText("Produkt:");

        cmbMProdukt.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbMProduktItemStateChanged(evt);
            }
        });

        btnMed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/edit.png")));
        btnMed.setToolTipText("Medikamente bearbeiten");
        btnMed.setBorder(null);
        btnMed.setOpaque(false);
        btnMed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMedActionPerformed(evt);
            }
        });

        lblVorrat.setText("vorhandene Vorr\u00e4te:");

        cmbVorrat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cmbVorratMouseEntered(evt);
            }
        });

        jLabel4.setText("Zuordnung zu Bewohner:");

        txtBWSuche.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtBWSucheCaretUpdate(evt);
            }
        });

        cmbBW.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbBWItemStateChanged(evt);
            }
        });

        jLabel5.setText("Buchungsmenge:");

        txtMenge.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtMengeCaretUpdate(evt);
            }
        });
        txtMenge.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMengeFocusGained(evt);
            }
        });

        jLabel6.setText("Packung:");

        cmbPackung.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbPackungItemStateChanged(evt);
            }
        });

        jLabel7.setText("Bemerkung:");

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
        btnClose.setText("Schlie\u00dfen");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnApply.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
        btnApply.setText("Buchen");
        btnApply.setEnabled(false);
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        txtBemerkung.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtBemerkungCaretUpdate(evt);
            }
        });

        cbDruck.setText("Belegdruck");
        cbDruck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbDruck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbDruck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbDruckItemStateChanged(evt);
            }
        });

        jLabel12.setText("<html>Hinweis: &frac14; = 0,25 | <sup>1</sup>/<sub>3</sub> = 0,33 | &frac12; = 0,5 | &frac34; = 0,75</html>");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                            .addComponent(lblFrage, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtMedSuche, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnMed))
                                    .addComponent(cmbMProdukt, 0, 293, Short.MAX_VALUE)
                                    .addComponent(cmbPackung, 0, 293, Short.MAX_VALUE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMenge, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblVorrat)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbVorrat, 0, 293, Short.MAX_VALUE)
                                    .addComponent(txtBemerkung, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBWSuche, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbBW, 0, 202, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(263, Short.MAX_VALUE)
                        .addComponent(btnApply)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClose))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                        .addComponent(cbDruck)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFrage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btnMed)
                    .addComponent(txtMedSuche, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cmbMProdukt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cmbPackung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtMenge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVorrat, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbVorrat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBemerkung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtBWSuche, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbBW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cbDruck)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClose)
                    .addComponent(btnApply))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnMed, txtMedSuche});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmbVorrat, txtMenge});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmbBW, txtBWSuche, txtBemerkung});

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void txtMengeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMengeFocusGained
        SYSTools.markAllTxt((JTextField) evt.getSource());
    }//GEN-LAST:event_txtMengeFocusGained
    
    private void cbDruckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbDruckItemStateChanged
        if (ignoreEvent) { return; }
        SYSTools.storeState(this.getClass().getName()+"::cbDruck", cbDruck);
    }//GEN-LAST:event_cbDruckItemStateChanged
    
    private void cmbVorratMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmbVorratMouseEntered
        if (thread != null && flashVorrat) {
            thread.interrupt();
        }
    }//GEN-LAST:event_cmbVorratMouseEntered
    
    private void txtBemerkungCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBemerkungCaretUpdate
        setApply();
    }//GEN-LAST:event_txtBemerkungCaretUpdate
    
    private void txtMedSucheFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMedSucheFocusGained
        SYSTools.markAllTxt(txtMedSuche);
    }//GEN-LAST:event_txtMedSucheFocusGained
    
    private void cmbBWItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbBWItemStateChanged
        ListElement e = (ListElement) cmbBW.getSelectedItem();
        bwkennung = e.getData();
        if (medEingegeben){ // Vorrat erneut ermitteln
            ListElement le = (ListElement) cmbMProdukt.getSelectedItem();
            initCmbVorrat(le.getPk());
        }
    }//GEN-LAST:event_cmbBWItemStateChanged
    
    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        if (thread != null && flashVorrat) { thread.interrupt(); }
        save();
        if (template != null && !template.equals("")){
            dispose();
        } else {
            txtMenge.setText("0");
            txtBemerkung.setText("");
            txtMedSuche.setText("");
            txtMedSuche.requestFocus();
            btnApply.setEnabled(false);
        }
    }//GEN-LAST:event_btnApplyActionPerformed
    
    private void save(){
        Connection db = OPDE.getDb().db;
        try {
            // Hier beginnt eine Transaktion
            db.setAutoCommit(false);
            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            db.commit();
            
            
            long vorid;
            ListElement le1 = (ListElement) cmbPackung.getSelectedItem();
            ListElement le2 = (ListElement) cmbVorrat.getSelectedItem();
            ListElement le3 = (ListElement) cmbMProdukt.getSelectedItem();
            //ListElement le4 = (ListElement) cmbBW.getSelectedItem();
            if (le2.getPk() == -1){ // neuen Vorrat anlegen
                vorid = op.care.med.DBHandling.createVorrat(le3.toString(), le3.getPk(), bwkennung);
            } else {
                vorid = le2.getPk();
            }
            
            long bestid = op.care.med.DBHandling.einbuchenVorrat(vorid, le1.getPk(), le3.getPk(), txtBemerkung.getText(), menge);
            
            if (bestid <= 0){
                throw new SQLException("Buchung fehlgeschlagen");
            }
            
            if (menge < inhalt || cmbPackung.getSelectedIndex() == 0){ // oder Sonderpackung
                DBHandling.anbrechen(vorid);
            } else {
                if (!DBHandling.hasAnbruch(vorid) &&
                        JOptionPane.showConfirmDialog(this, "Dieser Vorrat enthält bisher nur verschlossene Packungen.\n" +
                        "Soll die neue Packung direkt als angebrochen markiert werden ?", "Packungs-Anbruch",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION ) {
                    DBHandling.anbrechenNaechste(vorid);
                }                
            }
            
            if (cbDruck.isSelected()){
                //SYSPrint.printEpson(op.care.med.DBHandling.getBestandText4Print(bestid));
                SYSPrint.printLabel(bestid);
            }
            
//            // Prüfen, ob BHPs nachimportiert werden müssen.
//            ResultSet verRS = op.care.med.DBHandling.getVerordnungen2Vorrat(vorid);
//            if (verRS.first()){
//                long verid = verRS.getLong(1);
//                // Gibt es heute für diese VerID noch keine BHPs, dann diesen VerID nachimportieren.
//                if (!op.care.bhp.DBHandling.isBHPToday(verid)){
//                    BHPImport.importBHP(verid);
//                }
//            }
            
            
            
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
    
    private void btnMedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMedActionPerformed
        new DlgMediAssistent(this);
        txtMedSucheCaretUpdate(null);
    }//GEN-LAST:event_btnMedActionPerformed
    
    public void dispose() {
        if (thread != null && flashVorrat) { thread.interrupt(); }
        SYSTools.unregisterListeners(this);
        super.dispose();
    }
    
    private void txtMengeCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtMengeCaretUpdate
        menge = GuiChecks.checkDouble(evt, true);
        mengeEingegeben =  menge != 0d;
        setApply();
    }//GEN-LAST:event_txtMengeCaretUpdate
    
    private void txtBWSucheCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBWSucheCaretUpdate
        if (ignoreEvent) { return; }
        if (txtBWSuche.getText().equals("")) {
            cmbBW.setModel(new DefaultComboBoxModel());
            bwEingegeben = false;
        } else {
            DefaultComboBoxModel dcbm = null;
            if (txtBWSuche.getText().length() == 3){ // Könnte eine Suche nach der Kennung sein
                ResultSet rs = op.tools.DBRetrieve.getResultSet("Bewohner",new String[]{"BWKennung","Nachname","Vorname","GebDatum"},"BWKennung",
                        txtBWSuche.getText(),"=");
                dcbm = SYSTools.rs2cmb(rs);
            }
            if (dcbm == null || dcbm.getSize() == 0) {
                ResultSet rs = op.tools.DBRetrieve.getResultSet("Bewohner",new String[]{"BWKennung","Nachname","Vorname","GebDatum"},"Nachname",
                        "%"+txtBWSuche.getText()+"%","like");
                dcbm = SYSTools.rs2cmb(rs, new String[]{"","","","*"});
            }
            if (dcbm != null && dcbm.getSize() > 0){
                cmbBW.setModel(dcbm);
                cmbBW.setSelectedIndex(0);
                bwEingegeben = true;
                cmbBWItemStateChanged(null);
            } else {
                cmbBW.setModel(new DefaultComboBoxModel());
                bwkennung = null;
                bwEingegeben = false;
            }
            
        }
        setApply();
    }//GEN-LAST:event_txtBWSucheCaretUpdate
    
    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed
    
    private void cmbPackungItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbPackungItemStateChanged
        if (ignoreEvent) { return; }
        if (cmbPackung.getSelectedIndex() > 0) {
            ListElement le = (ListElement) cmbPackung.getSelectedItem();
            HashMap hm = op.tools.DBRetrieve.getSingleRecord("MPackung","MPID",le.getPk());
            txtMenge.setText(hm.get("Inhalt").toString());
            inhalt = ((BigDecimal) hm.get("Inhalt")).doubleValue();
            hm.clear();
        } else {
            txtMenge.setText("1.0");
        }
        setApply();
    }//GEN-LAST:event_cmbPackungItemStateChanged
    
    private void flash(){
        flashVorrat = true;
        thread = new Thread() {
            public void run() {
                try {
                    OPDE.getLogger().debug("thread");
                    while (flashVorrat) {
                        if (lblVorrat.getForeground() != Color.RED){
                            lblVorrat.setForeground(Color.RED);
                        } else {
                            lblVorrat.setForeground(Color.WHITE);
                        }
                        Thread.sleep(500);
                    }
                    lblVorrat.setForeground(Color.BLACK);
                    flashVorrat = false;
                } catch (InterruptedException e) {
                    lblVorrat.setForeground(Color.BLACK);
                    flashVorrat = false;
                }
            }
        };
        thread.start();
    }
    
    private void initCmbVorrat(long dafid){
        Bool foundMatch = new Bool(false);
        if (dafid == 0){
            cmbVorrat.setModel(new DefaultComboBoxModel());
        } else {
            cmbVorrat.setModel(SYSTools.rs2cmb(op.care.med.DBHandling.getVorrat2DAF(bwkennung, dafid, foundMatch)));
        }
        if (!foundMatch.isTrue()){
            DefaultComboBoxModel dcbm = (DefaultComboBoxModel) cmbVorrat.getModel();
            dcbm.insertElementAt(new ListElement("<AUTOMATISCH>",-1l),0);
            cmbVorrat.setSelectedIndex(0);
            if (dcbm.getSize() > 1){
                cmbVorrat.setToolTipText("<html>Keinen <b>exakt</b> passender Vorrat gefunden. Wählen Sie selbst einen passenden aus <br/>oder verwenden Sie <b>automatisch</b>.<html>");
                cmbVorrat.showPopup();
                flash();
            } else {
                cmbVorrat.setToolTipText("<html><b>automatisch</b> erstellt direkt einen neuen Vorrat. Da brauchen Sie nichts mehr zu ändern.</html>");
            }
            cmbVorrat.setEnabled(dcbm.getSize() > 1);
        } else {
            ListElement e = (ListElement) cmbVorrat.getSelectedItem();
            cmbVorrat.setToolTipText("Bestand: "+op.care.med.DBHandling.getVorratSumme(e.getPk())+" "+op.care.med.DBHandling.getPackEinheit(dafid));
            cmbVorrat.setEnabled(false);
        }
    }
    
    private void cmbMProduktItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbMProduktItemStateChanged
        if (ignoreEvent) { return; }
        medEingegeben = cmbMProdukt.getModel().getSize() > 0;
        if (cmbMProdukt.getModel().getSize() > 0) {
            ListElement le = (ListElement) cmbMProdukt.getSelectedItem();
            cmbPackung.setModel(op.care.med.DBHandling.getPackungen(le.getPk(), true));
            if (cmbPackung.getModel().getSize() > 0){
                cmbPackung.setSelectedIndex(0);
                cmbPackungItemStateChanged(null);
            }
            initCmbVorrat(le.getPk());
        } else {
            cmbPackung.setModel(new DefaultComboBoxModel());
        }
        medEingegeben = cmbMProdukt.getModel().getSize() > 0;
        setApply();
    }//GEN-LAST:event_cmbMProduktItemStateChanged
    
    private void setApply(){
        boolean txtEntry = true;
        if(cmbPackung.getSelectedIndex() < 0) {
            txtEntry = !txtBemerkung.getText().equals("");
        }
        btnApply.setEnabled(medEingegeben && mengeEingegeben && bwEingegeben && txtEntry);
    }
    
    private void txtMedSucheCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtMedSucheCaretUpdate
        if (ignoreEvent) { return; }
        if (txtMedSuche.getText().equals("")) {
            cmbMProdukt.setModel(new DefaultComboBoxModel());
            cmbPackung.setModel(new DefaultComboBoxModel());
            medEingegeben = false;
        } else {
            if (txtMedSuche.getText().matches("^ß?\\d{7}")) { // Hier sucht man nach einer PZN. Im Barcode ist das führende 'ß' enthalten.
                String pzn = txtMedSuche.getText();
                pzn = (pzn.startsWith("ß") ? pzn.substring(1) : pzn);
                //ignoreEvent = true; txtMedSuche.setText(pzn); ignoreEvent = false;
                HashMap pznsuche = op.tools.DBRetrieve.getSingleRecord("MPackung","PZN",pzn);
                if (pznsuche != null){
                    long dafid = ((BigInteger) pznsuche.get("DafID")).longValue();
                    long mpid = ((BigInteger) pznsuche.get("MPID")).longValue();
                    cmbMProdukt.setModel(op.care.med.DBHandling.getMedis(dafid));
                    cmbMProduktItemStateChanged(null);
                    SYSTools.selectInComboBox(cmbPackung, mpid);
                }
            } else { // Falls die Suche NICHT nur aus Zahlen besteht, dann nach Namen suchen.
                cmbMProdukt.setModel(op.care.med.DBHandling.getMedis(txtMedSuche.getText()));
                cmbMProduktItemStateChanged(null);
            }
            medEingegeben = cmbMProdukt.getModel().getSize() > 0;
        }
        setApply();
    }//GEN-LAST:event_txtMedSucheCaretUpdate
    
    
    // Variablendeklaration - nicht modifizieren//GEN-BEGIN:variables
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnMed;
    private javax.swing.JCheckBox cbDruck;
    private javax.swing.JComboBox cmbBW;
    private javax.swing.JComboBox cmbMProdukt;
    private javax.swing.JComboBox cmbPackung;
    private javax.swing.JComboBox cmbVorrat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblFrage;
    private javax.swing.JLabel lblVorrat;
    private javax.swing.JTextField txtBWSuche;
    private javax.swing.JTextField txtBemerkung;
    private javax.swing.JTextField txtMedSuche;
    private javax.swing.JTextField txtMenge;
    // Ende der Variablendeklaration//GEN-END:variables
    
}
