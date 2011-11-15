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

import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

import entity.SYSPropsTools;
import op.OPDE;
import op.care.med.DBHandling;
import op.care.med.DlgMediAssistent;
import op.tools.*;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author tloehr
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

    public DlgBestand(JFrame parent) {
        this(parent, null, null);
    }

    public DlgBestand(JDialog parent) {
        this(parent, null, null);
    }

    public DlgBestand(JFrame parent, String bwkennung) {
        this(parent, bwkennung, null);
    }

    public DlgBestand(JDialog parent, String bwkennung) {
        this(parent, bwkennung, null);
    }

    public DlgBestand(JFrame parent, String bwkennung, String template) {
        super(parent, true);
        this.parent = parent;
        this.bwkennung = bwkennung;
        this.template = template;
        initDialog();
    }

    public DlgBestand(JFrame parent, String bwkennung, long dafid) {
        super(parent, true);
        this.parent = parent;
        this.bwkennung = bwkennung;
        this.template = null;
        this.dafid = dafid;
        initDialog();
    }

    public DlgBestand(JDialog parent, String bwkennung, String template) {
        super(parent, true);
        this.parent = parent;
        this.bwkennung = bwkennung;
        this.template = template;
        initDialog();
    }

    public DlgBestand(JDialog parent, String bwkennung, long dafid) {
        super(parent, true);
        this.parent = parent;
        this.bwkennung = bwkennung;
        this.template = null;
        this.dafid = dafid;
        initDialog();
    }

    private void initDialog() {
        ignoreEvent = true;
        initComponents();
        if (this.bwkennung != null && !this.bwkennung.equals("")) {
            txtBWSuche.setText(bwkennung);
            ignoreEvent = false;
            txtBWSucheCaretUpdate(null);
            txtBWSuche.setEnabled(false);
            bwEingegeben = true;
        }
        if (this.template != null && !this.template.equals("")) {
            txtMedSuche.setText(this.template);
        }
        if (this.dafid >= 0) { // Die DafID wird direkt vorgegeben.
            cmbMProdukt.setModel(op.care.med.DBHandling.getMedis(dafid));
            cmbMProduktItemStateChanged(null);
            txtMedSuche.setEnabled(false);
        }
        String name = this.getClass().getName() + "::cbDruck";
        if (OPDE.getProps().containsKey(name)) {
            cbDruck.setSelected(OPDE.getProps().getProperty(name).equalsIgnoreCase("true"));
        } else {
            cbDruck.setSelected(false);
        }

        ignoreEvent = false;
        if (!txtMedSuche.getText().equals("")) {
            txtMedSucheCaretUpdate(null);
        }

        SYSTools.centerOnParent(parent, this);
        setTitle(SYSTools.getWindowTitle("Medikamente buchen"));
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
        lblFrage = new JLabel();
        jSeparator1 = new JSeparator();
        txtMedSuche = new JTextField();
        jLabel1 = new JLabel();
        jLabel3 = new JLabel();
        cmbMProdukt = new JComboBox();
        btnMed = new JButton();
        lblVorrat = new JLabel();
        cmbVorrat = new JComboBox();
        jLabel4 = new JLabel();
        txtBWSuche = new JTextField();
        cmbBW = new JComboBox();
        jLabel5 = new JLabel();
        txtMenge = new JTextField();
        jLabel6 = new JLabel();
        cmbPackung = new JComboBox();
        jSeparator2 = new JSeparator();
        jLabel7 = new JLabel();
        btnClose = new JButton();
        btnApply = new JButton();
        txtBemerkung = new JTextField();
        cbDruck = new JCheckBox();
        jLabel12 = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();

        //---- lblFrage ----
        lblFrage.setFont(new Font("Dialog", Font.BOLD, 24));
        lblFrage.setText("Med.-Best\u00e4nde buchen");

        //---- txtMedSuche ----
        txtMedSuche.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                txtMedSucheCaretUpdate(e);
            }
        });
        txtMedSuche.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtMedSucheFocusGained(e);
            }
        });

        //---- jLabel1 ----
        jLabel1.setText("PZN oder Suchbegriff:");

        //---- jLabel3 ----
        jLabel3.setText("Produkt:");

        //---- cmbMProdukt ----
        cmbMProdukt.setModel(new DefaultComboBoxModel(new String[] {

        }));
        cmbMProdukt.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbMProduktItemStateChanged(e);
            }
        });

        //---- btnMed ----
        btnMed.setBackground(Color.white);
        btnMed.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit.png")));
        btnMed.setToolTipText("Medikamente bearbeiten");
        btnMed.setBorder(null);
        btnMed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnMedActionPerformed(e);
            }
        });

        //---- lblVorrat ----
        lblVorrat.setText("vorhandene Vorr\u00e4te:");

        //---- cmbVorrat ----
        cmbVorrat.setModel(new DefaultComboBoxModel(new String[] {

        }));
        cmbVorrat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cmbVorratMouseEntered(e);
            }
        });

        //---- jLabel4 ----
        jLabel4.setText("Zuordnung zu Bewohner:");

        //---- txtBWSuche ----
        txtBWSuche.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                txtBWSucheCaretUpdate(e);
            }
        });

        //---- cmbBW ----
        cmbBW.setModel(new DefaultComboBoxModel(new String[] {

        }));
        cmbBW.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbBWItemStateChanged(e);
            }
        });

        //---- jLabel5 ----
        jLabel5.setText("Buchungsmenge:");

        //---- txtMenge ----
        txtMenge.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                txtMengeCaretUpdate(e);
            }
        });
        txtMenge.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtMengeFocusGained(e);
            }
        });

        //---- jLabel6 ----
        jLabel6.setText("Packung:");

        //---- cmbPackung ----
        cmbPackung.setModel(new DefaultComboBoxModel(new String[] {

        }));
        cmbPackung.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbPackungItemStateChanged(e);
            }
        });

        //---- jLabel7 ----
        jLabel7.setText("Bemerkung:");

        //---- btnClose ----
        btnClose.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
        btnClose.setText("Schlie\u00dfen");
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCloseActionPerformed(e);
            }
        });

        //---- btnApply ----
        btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
        btnApply.setText("Buchen");
        btnApply.setEnabled(false);
        btnApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnApplyActionPerformed(e);
            }
        });

        //---- txtBemerkung ----
        txtBemerkung.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                txtBemerkungCaretUpdate(e);
            }
        });

        //---- cbDruck ----
        cbDruck.setText("Belegdruck");
        cbDruck.setBorder(BorderFactory.createEmptyBorder());
        cbDruck.setMargin(new Insets(0, 0, 0, 0));
        cbDruck.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cbDruckItemStateChanged(e);
            }
        });

        //---- jLabel12 ----
        jLabel12.setText("<html>Hinweis: &frac14; = 0,25 | <sup>1</sup>/<sub>3</sub> = 0,33 | &frac12; = 0,5 | &frac34; = 0,75</html>");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(jSeparator1, GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                                .addComponent(lblFrage, GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(42, 42, 42)
                                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel6))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                            .addComponent(txtMedSuche, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnMed))
                                        .addComponent(cmbMProdukt, 0, 440, Short.MAX_VALUE)
                                        .addComponent(cmbPackung, 0, 440, Short.MAX_VALUE)))))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addGap(31, 31, 31)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(51, 51, 51)
                                    .addComponent(jLabel5)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtMenge, GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE))
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(32, 32, 32)
                                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblVorrat)
                                        .addComponent(jLabel7))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(cmbVorrat, 0, 436, Short.MAX_VALUE)
                                        .addComponent(txtBemerkung, GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)))
                                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtBWSuche, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cmbBW, 0, 353, Short.MAX_VALUE))))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addContainerGap(420, Short.MAX_VALUE)
                            .addComponent(btnApply)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnClose))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jSeparator2, GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel12, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 218, Short.MAX_VALUE)
                            .addComponent(cbDruck)))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblFrage)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(btnMed)
                        .addComponent(txtMedSuche, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(cmbMProdukt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(cmbPackung, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(txtMenge, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblVorrat, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbVorrat, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBemerkung, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtBWSuche, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbBW, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(cbDruck)
                        .addComponent(jLabel12, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jSeparator2, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(btnClose)
                        .addComponent(btnApply))
                    .addContainerGap())
        );
        contentPaneLayout.linkSize(SwingConstants.VERTICAL, new Component[] {btnMed, txtMedSuche});
        contentPaneLayout.linkSize(SwingConstants.VERTICAL, new Component[] {cmbVorrat, txtMenge});
        contentPaneLayout.linkSize(SwingConstants.VERTICAL, new Component[] {cmbBW, txtBWSuche, txtBemerkung});
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void txtMengeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMengeFocusGained
        SYSTools.markAllTxt((JTextField) evt.getSource());
    }//GEN-LAST:event_txtMengeFocusGained

    private void cbDruckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbDruckItemStateChanged
        if (ignoreEvent) {
            return;
        }
        SYSPropsTools.storeState(this.getClass().getName() + "::cbDruck", cbDruck);
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
        if (medEingegeben) { // Vorrat erneut ermitteln
            ListElement le = (ListElement) cmbMProdukt.getSelectedItem();
            initCmbVorrat(le.getPk());
        }
    }//GEN-LAST:event_cmbBWItemStateChanged

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        if (thread != null && flashVorrat) {
            thread.interrupt();
        }
        save();
        if (template != null && !template.equals("")) {
            dispose();
        } else {
            txtMenge.setText("0");
            txtBemerkung.setText("");
            txtMedSuche.setText("");
            txtMedSuche.requestFocus();
            btnApply.setEnabled(false);
        }
    }//GEN-LAST:event_btnApplyActionPerformed

    private void save() {
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
            if (le2.getPk() == -1) { // neuen Vorrat anlegen
                vorid = op.care.med.DBHandling.createVorrat(le3.toString(), le3.getPk(), bwkennung);
            } else {
                vorid = le2.getPk();
            }

            long bestid = op.care.med.DBHandling.einbuchenVorrat(vorid, le1.getPk(), le3.getPk(), txtBemerkung.getText(), menge);

            if (bestid <= 0) {
                throw new SQLException("Buchung fehlgeschlagen");
            }

            if (menge < inhalt || cmbPackung.getSelectedIndex() == 0) { // oder Sonderpackung
                DBHandling.anbrechen(vorid);
            } else {
                if (!DBHandling.hasAnbruch(vorid) &&
                        JOptionPane.showConfirmDialog(this, "Dieser Vorrat enthält bisher nur verschlossene Packungen.\n" +
                                "Soll die neue Packung direkt als angebrochen markiert werden ?", "Packungs-Anbruch",
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    DBHandling.anbrechenNaechste(vorid);
                }
            }

            if (cbDruck.isSelected()) {
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
        if (thread != null && flashVorrat) {
            thread.interrupt();
        }
        SYSTools.unregisterListeners(this);
        super.dispose();
    }

    private void txtMengeCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtMengeCaretUpdate
        menge = GuiChecks.checkDouble(evt, true);
        mengeEingegeben = menge != 0d;
        setApply();
    }//GEN-LAST:event_txtMengeCaretUpdate

    private void txtBWSucheCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBWSucheCaretUpdate
        if (ignoreEvent) {
            return;
        }
        if (txtBWSuche.getText().equals("")) {
            cmbBW.setModel(new DefaultComboBoxModel());
            bwEingegeben = false;
        } else {
            DefaultComboBoxModel dcbm = null;
            if (txtBWSuche.getText().length() == 3) { // Könnte eine Suche nach der Kennung sein
                ResultSet rs = op.tools.DBRetrieve.getResultSet("Bewohner", new String[]{"BWKennung", "Nachname", "Vorname", "GebDatum"}, "BWKennung",
                        txtBWSuche.getText(), "=");
                dcbm = SYSTools.rs2cmb(rs);
            }
            if (dcbm == null || dcbm.getSize() == 0) {
                ResultSet rs = op.tools.DBRetrieve.getResultSet("Bewohner", new String[]{"BWKennung", "Nachname", "Vorname", "GebDatum"}, "Nachname",
                        "%" + txtBWSuche.getText() + "%", "like");
                dcbm = SYSTools.rs2cmb(rs, new String[]{"", "", "", "*"});
            }
            if (dcbm != null && dcbm.getSize() > 0) {
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
        if (ignoreEvent) {
            return;
        }
        if (cmbPackung.getSelectedIndex() > 0) {
            ListElement le = (ListElement) cmbPackung.getSelectedItem();
            HashMap hm = op.tools.DBRetrieve.getSingleRecord("MPackung", "MPID", le.getPk());
            txtMenge.setText(hm.get("Inhalt").toString());
            inhalt = ((BigDecimal) hm.get("Inhalt")).doubleValue();
            hm.clear();
        } else {
            txtMenge.setText("1.0");
        }
        setApply();
    }//GEN-LAST:event_cmbPackungItemStateChanged

    private void flash() {
        flashVorrat = true;
        thread = new Thread() {
            public void run() {
                try {
                    OPDE.getLogger().debug("thread");
                    while (flashVorrat) {
                        if (lblVorrat.getForeground() != Color.RED) {
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

    private void initCmbVorrat(long dafid) {
        Bool foundMatch = new Bool(false);
        if (dafid == 0) {
            cmbVorrat.setModel(new DefaultComboBoxModel());
        } else {
            cmbVorrat.setModel(SYSTools.rs2cmb(op.care.med.DBHandling.getVorrat2DAF(bwkennung, dafid, foundMatch)));
        }
        if (!foundMatch.isTrue()) {
            DefaultComboBoxModel dcbm = (DefaultComboBoxModel) cmbVorrat.getModel();
            dcbm.insertElementAt(new ListElement("<AUTOMATISCH>", -1l), 0);
            cmbVorrat.setSelectedIndex(0);
            if (dcbm.getSize() > 1) {
                cmbVorrat.setToolTipText("<html>Keinen <b>exakt</b> passender Vorrat gefunden. Wählen Sie selbst einen passenden aus <br/>oder verwenden Sie <b>automatisch</b>.<html>");
                cmbVorrat.showPopup();
                flash();
            } else {
                cmbVorrat.setToolTipText("<html><b>automatisch</b> erstellt direkt einen neuen Vorrat. Da brauchen Sie nichts mehr zu ändern.</html>");
            }
            cmbVorrat.setEnabled(dcbm.getSize() > 1);
        } else {
            ListElement e = (ListElement) cmbVorrat.getSelectedItem();
            cmbVorrat.setToolTipText("Bestand: " + op.care.med.DBHandling.getVorratSumme(e.getPk()) + " " + op.care.med.DBHandling.getPackEinheit(dafid));
            cmbVorrat.setEnabled(false);
        }
    }

    private void cmbMProduktItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbMProduktItemStateChanged
        if (ignoreEvent) {
            return;
        }
        medEingegeben = cmbMProdukt.getModel().getSize() > 0;
        if (cmbMProdukt.getModel().getSize() > 0) {
            ListElement le = (ListElement) cmbMProdukt.getSelectedItem();
            cmbPackung.setModel(op.care.med.DBHandling.getPackungen(le.getPk(), true));
            if (cmbPackung.getModel().getSize() > 0) {
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

    private void setApply() {
        boolean txtEntry = true;
        if (cmbPackung.getSelectedIndex() < 0) {
            txtEntry = !txtBemerkung.getText().equals("");
        }
        btnApply.setEnabled(medEingegeben && mengeEingegeben && bwEingegeben && txtEntry);
    }

    private void txtMedSucheCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtMedSucheCaretUpdate
        if (ignoreEvent) {
            return;
        }
        if (txtMedSuche.getText().equals("")) {
            cmbMProdukt.setModel(new DefaultComboBoxModel());
            cmbPackung.setModel(new DefaultComboBoxModel());
            medEingegeben = false;
        } else {
            if (txtMedSuche.getText().matches("^ß?\\d{7}")) { // Hier sucht man nach einer PZN. Im Barcode ist das führende 'ß' enthalten.
                String pzn = txtMedSuche.getText();
                pzn = (pzn.startsWith("ß") ? pzn.substring(1) : pzn);
                //ignoreEvent = true; txtMedSuche.setText(pzn); ignoreEvent = false;
                HashMap pznsuche = op.tools.DBRetrieve.getSingleRecord("MPackung", "PZN", pzn);
                if (pznsuche != null) {
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
    private JLabel lblFrage;
    private JSeparator jSeparator1;
    private JTextField txtMedSuche;
    private JLabel jLabel1;
    private JLabel jLabel3;
    private JComboBox cmbMProdukt;
    private JButton btnMed;
    private JLabel lblVorrat;
    private JComboBox cmbVorrat;
    private JLabel jLabel4;
    private JTextField txtBWSuche;
    private JComboBox cmbBW;
    private JLabel jLabel5;
    private JTextField txtMenge;
    private JLabel jLabel6;
    private JComboBox cmbPackung;
    private JSeparator jSeparator2;
    private JLabel jLabel7;
    private JButton btnClose;
    private JButton btnApply;
    private JTextField txtBemerkung;
    private JCheckBox cbDruck;
    private JLabel jLabel12;
    // Ende der Variablendeklaration//GEN-END:variables

}
