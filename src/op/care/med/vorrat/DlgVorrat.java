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

import op.OCSec;
import op.OPDE;
import op.tools.*;
import tablerenderer.RNDStandard;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * In OPDE.de gibt es eine Bestandsverwaltung für Medikamente. Bestände werden mit Hilfe von 3 Tabellen
 * in der Datenbank verwaltet.
 * <ul>
 * <li><B>MPVorrat</B> Ein Vorrat ist wie eine Schachtel oder Schublade zu sehen, in denen
 * einzelne Päckchen enthalten sind. Jetzt kann es natürlich passieren, dass verschiedene
 * Präparete, die aber pharmazeutisch gleichwertig sind in derselben Schachtel enthalten
 * sind. Wenn z.B. 3 verschiedene Medikamente mit demselben Wirkstoff, derselben Darreichungsform
 * und in derselben Stärke vorhanden sind, dann sollten sie auch in demselben Vorrat zusammengefasst
 * werden. Vorräte gehören immer einem bestimmten Bewohner.</li>
 * <li><B>MPBestand</B> Ein Bestand entspricht i.d.R. einer Verpackung. Also eine Schachtel eines
 * Medikamentes wäre für sich genommen ein Bestand. Aber auch z.B. wenn ein BW von zu Hause einen
 * angebrochenen Blister mitbringt, dann wird dies als eigener Bestand angesehen. Bestände gehören
 * immer zu einem bestimmten Vorrat. Das Eingangs-, Ausgangs und Anbruchdatum wird vermerkt. Es meistens
 * einen Verweis auf eine MPID aus der Tabelle MPackung. Bei eigenen Gebinden kann dieses Feld auch
 * <CODE>null</CODE> sein.</li>
 * <li><B>MPBuchung</B> Eine Buchung ist ein Ein- bzw. Ausgang von einer Menge von Einzeldosen zu oder von
 * einem bestimmten Bestand. Also wenn eine Packung eingebucht wird, dann wird ein Bestand erstellt und
 * eine Eingangsbuchung in Höhe der Ursprünglichen Packungsgrößen (z.B. 100 Stück). Bei Vergabe von
 * Medikamenten an einen Bewohner (über Abhaken in der BHP) werden die jeweiligen Mengen
 * ausgebucht. In diesem Fall steht in der Spalte BHPID der Verweis zur entsprechenden Zeile in der
 * Tabelle BHP.</li>
 * </ul>
 *
 * @author tloehr
 */
public class DlgVorrat extends javax.swing.JDialog {

    private String bwkennung;
    private Component parent;
    private Component thisDialog;
    private long vorid;
    private long bestid;
    private ListSelectionListener lslV;
    private ListSelectionListener lslB;
    private JPopupMenu menuV;
    private JPopupMenu menuB;
    private JPopupMenu menuBuch;
    private boolean ignoreEvent;
    private OCSec ocs;

    /**
     * Creates new form DlgVorrat
     */
    public DlgVorrat(JDialog parent, String bwkennung) {
        super(parent, true);
        this.parent = parent;
        this.bwkennung = bwkennung;
        initDialog();
    }

    public DlgVorrat(JFrame parent, String bwkennung) {
        super(parent, true);
        this.parent = parent;
        this.bwkennung = bwkennung;
        initDialog();
    }

    public DlgVorrat(JDialog parent) {
        super(parent, true);
        this.parent = parent;
        this.bwkennung = "";
        initDialog();
    }

    public DlgVorrat(JFrame parent) {
        super(parent, true);
        this.parent = parent;
        this.bwkennung = "";
        initDialog();
    }

    private void initDialog() {
        ocs = OPDE.getOCSec();
        setTitle(SYSTools.getWindowTitle("Medikamentenvorrat"));
        thisDialog = this;
        ignoreEvent = false;
        initComponents();
        txtSuche.setEnabled(bwkennung.equals(""));
        cmbBW.setEnabled(bwkennung.equals(""));
        if (bwkennung.equals("")) {
            txtSuche.requestFocus();
            lblBW.setText("");
        } else {
            SYSTools.setBWLabel(lblBW, bwkennung);
        }
        reloadVorratTable();
        SYSTools.centerOnParent(parent, this);
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblFrage = new javax.swing.JLabel();
        lblBW = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jspVorrat = new javax.swing.JScrollPane();
        tblVorrat = new javax.swing.JTable();
        cbClosedVorrat = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jspBestand = new javax.swing.JScrollPane();
        tblBestand = new javax.swing.JTable();
        cbClosedBestand = new javax.swing.JCheckBox();
        btnClose = new javax.swing.JButton();
        pnl123 = new javax.swing.JPanel();
        jspBuchung = new javax.swing.JScrollPane();
        tblBuchung = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtSuche = new javax.swing.JTextField();
        cmbBW = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        jToolBar1 = new javax.swing.JToolBar();
        btnBestandsliste = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblFrage.setFont(new java.awt.Font("Dialog", 1, 24));
        lblFrage.setText("Medikamenten Vorrat");

        lblBW.setFont(new java.awt.Font("Dialog", 1, 18));
        lblBW.setForeground(new java.awt.Color(255, 51, 0));
        lblBW.setText("Nachname, Vorname (*GebDatum, 00 Jahre) [??1]");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Vorräte"));

        jspVorrat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jspVorratMousePressed(evt);
            }
        });

        tblVorrat.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null}
                },
                new String[]{
                        "Title 1", "Title 2", "Title 3", "Title 4"
                }
        ));
        tblVorrat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblVorratMousePressed(evt);
            }
        });
        jspVorrat.setViewportView(tblVorrat);

        cbClosedVorrat.setText("Abgeschlossene anzeigen");
        cbClosedVorrat.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbClosedVorrat.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbClosedVorrat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbClosedVorratItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jspVorrat, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                                        .addComponent(cbClosedVorrat))
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cbClosedVorrat)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jspVorrat, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Bestände"));

        jspBestand.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jspBestandMousePressed(evt);
            }
        });

        tblBestand.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null}
                },
                new String[]{
                        "Title 1", "Title 2", "Title 3", "Title 4"
                }
        ));
        tblBestand.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblBestandMousePressed(evt);
            }
        });
        jspBestand.setViewportView(tblBestand);

        cbClosedBestand.setText("Abgeschlossene anzeigen");
        cbClosedBestand.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbClosedBestand.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbClosedBestand.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbClosedBestandItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jspBestand, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                                        .addComponent(cbClosedBestand))
                                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(cbClosedBestand)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jspBestand, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                                .addContainerGap())
        );

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/cancel.png"))); // NOI18N
        btnClose.setText("Schließen");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        pnl123.setBorder(javax.swing.BorderFactory.createTitledBorder("Buchungen"));

        jspBuchung.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jspBuchungMousePressed(evt);
            }
        });

        tblBuchung.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null}
                },
                new String[]{
                        "Title 1", "Title 2", "Title 3", "Title 4"
                }
        ));
        tblBuchung.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblBuchungMousePressed(evt);
            }
        });
        jspBuchung.setViewportView(tblBuchung);

        javax.swing.GroupLayout pnl123Layout = new javax.swing.GroupLayout(pnl123);
        pnl123.setLayout(pnl123Layout);
        pnl123Layout.setHorizontalGroup(
                pnl123Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnl123Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jspBuchung, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                                .addContainerGap())
        );
        pnl123Layout.setVerticalGroup(
                pnl123Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnl123Layout.createSequentialGroup()
                                .addComponent(jspBuchung, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Suche"));

        txtSuche.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtSucheCaretUpdate(evt);
            }
        });
        txtSuche.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSucheActionPerformed(evt);
            }
        });
        txtSuche.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSucheFocusGained(evt);
            }
        });

        cmbBW.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbBWItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(cmbBW, javax.swing.GroupLayout.Alignment.LEADING, 0, 717, Short.MAX_VALUE)
                                        .addComponent(txtSuche, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 717, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtSuche, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbBW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(14, Short.MAX_VALUE))
        );

        jToolBar1.setFloatable(false);

        btnBestandsliste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/fileprint.png"))); // NOI18N
        btnBestandsliste.setText("Bestandsliste");
        btnBestandsliste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBestandslisteActionPerformed(evt);
            }
        });
        jToolBar1.add(btnBestandsliste);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(pnl123, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(btnClose, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblFrage, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblBW, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblFrage)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblBW)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(pnl123, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnClose)
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbClosedBestandItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbClosedBestandItemStateChanged
        reloadBestandTable();
    }//GEN-LAST:event_cbClosedBestandItemStateChanged

    private void cbClosedVorratItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbClosedVorratItemStateChanged
        reloadVorratTable();
    }//GEN-LAST:event_cbClosedVorratItemStateChanged

    private void btnBestandslisteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBestandslisteActionPerformed
        printBestand();
    }//GEN-LAST:event_btnBestandslisteActionPerformed

    private void printBestand() {
//        if (!bwkennung.equals("") ||
//                JOptionPane.showConfirmDialog(this, "Es wurde kein Bewohner ausgewählt.\nMöchten Sie wirklich eine Gesamtliste ausdrucken ?", "Frage", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//
//            HashMap params = new HashMap();
//            JRDSMedBestand jrds = new JRDSMedBestand(bwkennung);
//
//            SYSPrint.printReport(preview, jrds, params, "medbestand", dialog);
//            if (!preview && !dialog) {
//                JOptionPane.showMessageDialog(this, "Der Druckvorgang ist abgeschlossen.", "Drucker", JOptionPane.INFORMATION_MESSAGE);
//            }
//        }
    }

    private void cmbBWItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbBWItemStateChanged
        ListElement e = (ListElement) cmbBW.getSelectedItem();
        bwkennung = e.getData();
        reloadVorratTable();
    }//GEN-LAST:event_cmbBWItemStateChanged

    private void txtSucheCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtSucheCaretUpdate
//        if (ignoreEvent || !txtSuche.isEnabled()) {
//            return;
//        }
//        if (txtSuche.getText().equals("")) {
//            cmbBW.setModel(new DefaultComboBoxModel());
//            bwkennung = "";
//            reloadVorratTable();
//        } else if (txtSuche.getText().matches("\\d*")) { // Nur Zahlen.. Das ist eine BestID
//            bwkennung = "";
//            reloadVorratTable();
//        } else {
//            DefaultComboBoxModel dcbm = null;
//            if (txtSuche.getText().length() == 3) { // Könnte eine Suche nach der Kennung sein
//                ResultSet rs = op.tools.DBRetrieve.getResultSet("Bewohner", new String[]{"BWKennung", "Nachname", "Vorname", "GebDatum"}, "BWKennung",
//                        txtSuche.getText(), "=");
//                dcbm = SYSTools.rs2cmb(rs);
//            }
//            if (dcbm == null || dcbm.getSize() == 0) {
//                ResultSet rs = op.tools.DBRetrieve.getResultSet("Bewohner", new String[]{"BWKennung", "Nachname", "Vorname", "GebDatum"}, "Nachname",
//                        "%" + txtSuche.getText() + "%", "like");
//                dcbm = SYSTools.rs2cmb(rs, new String[]{"", "", "", "*"});
//            }
//            if (dcbm != null && dcbm.getSize() > 0) {
//                cmbBW.setModel(dcbm);
//                cmbBW.setSelectedIndex(0);
//                cmbBWItemStateChanged(null);
//            } else {
//                cmbBW.setModel(new DefaultComboBoxModel());
//                bwkennung = "";
//            }
//
//        }
    }//GEN-LAST:event_txtSucheCaretUpdate

    private void txtSucheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSucheActionPerformed
        if (!txtSuche.getText().equals("")) {
            if (txtSuche.getText().matches("\\d*")) { // Nur Zahlen.. Das ist eine BestID
                long mybestid = Long.parseLong(txtSuche.getText());
                BigInteger myvorid = (BigInteger) op.tools.DBHandling.getSingleValue("MPBestand", "VorID", "BestID", mybestid);
                if (myvorid != null && myvorid.longValue() > 0) {
                    bwkennung = (String) DBHandling.getSingleValue("MPVorrat", "BWKennung", "VorID", myvorid.longValue());
                    reloadVorratTable();
                    DefaultComboBoxModel dcbm = null;
                    ResultSet rs = op.tools.DBRetrieve.getResultSet("Bewohner", new String[]{"BWKennung", "Nachname", "Vorname", "GebDatum"}, "BWKennung",
                            bwkennung, "=");
                    dcbm = SYSTools.rs2cmb(rs);
                    cmbBW.setModel(dcbm);
                    int i = 0;
                    boolean found = false;
                    TMResultSet tm = (TMResultSet) tblVorrat.getModel();
                    while (!found && i < tm.getRowCount()) {
                        long thisVorID = ((BigInteger) tm.getPK(i)).longValue();//Long.parseLong(tm.getValueAt(0, i).toString());
                        if (thisVorID == myvorid.longValue()) {
                            tblVorrat.getSelectionModel().setSelectionInterval(i, i);
                            found = true;
                        }
                        i++;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Der eingegebene Bestand existiert nicht.", "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                DefaultComboBoxModel dcbm = null;
                if (txtSuche.getText().length() == 3) { // Könnte eine Suche nach der Kennung sein
                    ResultSet rs = op.tools.DBRetrieve.getResultSet("Bewohner", new String[]{"BWKennung", "Nachname", "Vorname", "GebDatum"}, "BWKennung",
                            txtSuche.getText(), "=");
                    dcbm = SYSTools.rs2cmb(rs);
                }
                if (dcbm == null || dcbm.getSize() == 0) {
                    ResultSet rs = op.tools.DBRetrieve.getResultSet("Bewohner", new String[]{"BWKennung", "Nachname", "Vorname", "GebDatum"}, "Nachname",
                            "%" + txtSuche.getText() + "%", "like");
                    dcbm = SYSTools.rs2cmb(rs, new String[]{"", "", "", "*"});
                }
                if (dcbm != null && dcbm.getSize() > 0) {
                    cmbBW.setModel(dcbm);
                    cmbBW.setSelectedIndex(0);
                    cmbBWItemStateChanged(null);
                } else {
                    cmbBW.setModel(new DefaultComboBoxModel());
                    bwkennung = "";
                }
            }
        } else {
            cmbBW.setModel(new DefaultComboBoxModel());
            bwkennung = "";
            reloadVorratTable();
        }
    }//GEN-LAST:event_txtSucheActionPerformed

    private void txtSucheFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSucheFocusGained
        SYSTools.markAllTxt(txtSuche);
    }//GEN-LAST:event_txtSucheFocusGained

    private void jspBuchungMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jspBuchungMousePressed
        tblBuchungMousePressed(evt);
    }//GEN-LAST:event_jspBuchungMousePressed

    private void tblBuchungMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBuchungMousePressed
        if (!evt.isPopupTrigger() || bestid == 0) {
            return;
        }
        Point p = evt.getPoint();
        SYSTools.unregisterListeners(menuBuch);
        menuBuch = new JPopupMenu();

        JMenuItem itemPopupNew = new JMenuItem("Neu");
        itemPopupNew.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBuchung();
            }
        });
        menuBuch.add(itemPopupNew);

        if (!(evt.getSource() instanceof JScrollPane)) {
            final TMResultSet tm = (TMResultSet) tblBuchung.getModel();
            if (tm.getRowCount() > 0) {
                //final int col = tblBuchung.columnAtPoint(p);
                final int row = tblBuchung.rowAtPoint(p);
                ListSelectionModel lsm = tblBuchung.getSelectionModel();
                lsm.setSelectionInterval(row, row);
                final long buchid = ((BigInteger) ((TMResultSet) tblBuchung.getModel()).getPK(row)).longValue();
                // Menüeinträge

                JMenuItem itemPopupDelete = new JMenuItem("Löschen");
                itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        deleteBuchung(buchid);
                        reloadBuchungTable();
                        TMResultSet tm = (TMResultSet) tblVorrat.getModel();
                        tm.reload(tblVorrat.getSelectedRow(), tblVorrat.getSelectedColumn());
                    }
                });
                menuBuch.add(itemPopupDelete);
            }
        }

        JMenuItem itemPopupReset = new JMenuItem("Alle Buchungen zurücksetzen.");
        itemPopupReset.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBuchung();
            }
        });
        menuBuch.add(itemPopupReset);
        menuBuch.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }//GEN-LAST:event_tblBuchungMousePressed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void jspBestandMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jspBestandMousePressed
        if (!evt.isPopupTrigger() || vorid == 0) {
            return;
        }
        Point p = evt.getPoint();
        SYSTools.unregisterListeners(menuV);
        menuV = new JPopupMenu();

        JMenuItem itemPopupNew = new JMenuItem("Neu");
        itemPopupNew.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBestand();
            }
        });
        menuV.add(itemPopupNew);
        menuV.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }//GEN-LAST:event_jspBestandMousePressed

    private void tblBestandMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBestandMousePressed
        if (!evt.isPopupTrigger() || vorid == 0) {
            return;
        }
        final TMResultSet tm = (TMResultSet) tblBestand.getModel();
        if (tm.getRowCount() > 0) {
            Point p = evt.getPoint();
            //final int col = tblBestand.columnAtPoint(p);
            final int row = tblBestand.rowAtPoint(p);
            ListSelectionModel lsm = tblBestand.getSelectionModel();
            lsm.setSelectionInterval(row, row);
            final long mybestid = ((BigInteger) ((TMResultSet) tblBestand.getModel()).getPK(row)).longValue();
            // Menüeinträge
            SYSTools.unregisterListeners(menuV);
            menuV = new JPopupMenu();

            JMenuItem itemPopupNew = new JMenuItem("Neu");
            itemPopupNew.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    newBestand();
                }
            });
            menuV.add(itemPopupNew);

//            JMenuItem itemPopupEdit = new JMenuItem("Bearbeiten");
//            itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                }
//            });
//            menuV.add(itemPopupEdit);
//
            JMenuItem itemPopupDelete = new JMenuItem("Löschen");
            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    deleteBestand(mybestid);
                }
            });
            menuV.add(itemPopupDelete);
            // ----------------
            JMenuItem itemPopupPrint = new JMenuItem("Beleg drucken");
            itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    SYSPrint.printLabel(mybestid);
                }
            });
            menuV.add(itemPopupPrint);
            // ----------------
            JMenuItem itemPopupClose = new JMenuItem("Bestand abschließen");
            itemPopupClose.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (JOptionPane.showConfirmDialog(thisDialog, "Sind sie sicher ?", "Bestand abschließen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        op.care.med.DBHandling.closeBestand(mybestid, "", false, op.care.med.DBHandling.STATUS_KORREKTUR_MANUELL);
                        reloadVorratTable();
                    }
                }
            });
            itemPopupClose.setEnabled(op.care.med.DBHandling.isAnbruch(mybestid));
            menuV.add(itemPopupClose);

            // ---------------
            JMenuItem itemPopupEinbuchen = new JMenuItem("Bestand wieder aktivieren");
            itemPopupEinbuchen.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    HashMap hm = new HashMap();
                    hm.put("Aus", "!BAW!");
                    DBHandling.updateRecord("MPBestand", hm, "BestID", mybestid);
                    hm.clear();
                    reloadBestandTable();
                }
            });
            itemPopupEinbuchen.setEnabled(op.care.med.DBHandling.isAusgebucht(mybestid));
            menuV.add(itemPopupEinbuchen);

            // ---------------
            JMenuItem itemPopupAnbruch = new JMenuItem("Bestand anbrechen");
            itemPopupAnbruch.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    HashMap hm = new HashMap();
                    hm.put("Anbruch", "!NOW!");
                    DBHandling.updateRecord("MPBestand", hm, "BestID", mybestid);
                    hm.clear();
                    reloadBestandTable();
                }
            });
            itemPopupAnbruch.setEnabled(!op.care.med.DBHandling.hasAnbruch(vorid)); // Nur an anbrechen lassen, wenn noch keine im Anbruch ist.
            menuV.add(itemPopupAnbruch);

            // ---------------
            JMenuItem itemPopupVerschließen = new JMenuItem("Bestand wieder verschließen");
            itemPopupVerschließen.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    HashMap hm = new HashMap();
                    hm.put("Anbruch", "!BAW!");
                    hm.put("Aus", "!BAW!");
                    DBHandling.updateRecord("MPBestand", hm, "BestID", mybestid);
                    hm.clear();
                    reloadBestandTable();
                }
            });
            itemPopupVerschließen.setEnabled(op.care.med.DBHandling.isAnbruch(mybestid));
            menuV.add(itemPopupVerschließen);

            //ocs.setEnabled(classname, "itemPopupEditVer", itemPopupEditVer, true);
            menuV.show(evt.getComponent(), (int) p.getX(), (int) p.getY());

            //ocs.setEnabled(classname, "itemPopupEditVer", itemPopupEditVer, true);
            //menuV.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }//GEN-LAST:event_tblBestandMousePressed

    private void tblVorratMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVorratMousePressed
        if (!evt.isPopupTrigger()) {
            return;
        }
        final TMResultSet tm = (TMResultSet) tblVorrat.getModel();
        if (tm.getRowCount() > 0) {
            Point p = evt.getPoint();
            final int col = tblVorrat.columnAtPoint(p);
            final int row = tblVorrat.rowAtPoint(p);
            ListSelectionModel lsm = tblVorrat.getSelectionModel();
            lsm.setSelectionInterval(row, row);

            // Menüeinträge
            SYSTools.unregisterListeners(menuV);
            menuV = new JPopupMenu();

            JMenuItem itemPopupNew = new JMenuItem("Neu");
            itemPopupNew.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    newVorrat();
                }
            });
            menuV.add(itemPopupNew);

            JMenuItem itemPopupDelete = new JMenuItem("Vorrat löschen");
            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (JOptionPane.showConfirmDialog(parent, "Sind sie sicher ?", "Vorrat löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        if (JOptionPane.showConfirmDialog(parent, "Wirklich ?", "Vorrat löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            op.care.med.DBHandling.deleteVorrat(vorid);
                        }
                    }
                    reloadVorratTable();
                }
            });
            menuV.add(itemPopupDelete);

            JMenuItem itemPopupClose = new JMenuItem("Vorrat abschließen und ausbuchen");
            itemPopupClose.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (JOptionPane.showConfirmDialog(parent, "Sind sie sicher ?", "Vorrat abschließen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        op.care.med.DBHandling.closeVorrat(vorid);
                    }
                    reloadVorratTable();
                }
            });
            menuV.add(itemPopupClose);

            menuV.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }//GEN-LAST:event_tblVorratMousePressed

    private void jspVorratMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jspVorratMousePressed
        if (!evt.isPopupTrigger()) {
            return;
        }
        Point p = evt.getPoint();
        SYSTools.unregisterListeners(menuV);
        menuV = new JPopupMenu();

        JMenuItem itemPopupNew = new JMenuItem("Neu");
        itemPopupNew.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newVorrat();
            }
        });
        menuV.add(itemPopupNew);

        menuV.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }//GEN-LAST:event_jspVorratMousePressed

    /**
     * Diese Methode legt einen neuen Vorrat in der Tabelle MPVorrat an. Ein
     * Vorrat braucht nur eine allgemeine Bezeichnung zu haben.
     */
    private void newVorrat() {
        String neuerVorrat = JOptionPane.showInputDialog(this, "Bitte geben Sie die Bezeichnung für den neuen Vorrat ein.");
        if (neuerVorrat != null && !neuerVorrat.equals("")) {
            HashMap hm = new HashMap();
            hm.put("Text", neuerVorrat);
            hm.put("BWKennung", bwkennung);
            hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
            hm.put("Von", "!NOW!");
            hm.put("Bis", "!BAW!");
            DBHandling.insertRecord("MPVorrat", hm);
            reloadVorratTable();
        }
    }

    /**
     * Löscht einen bestimmten Bestand und die zugehörigen Buchungen.
     */
    private void deleteBestand(long bestid) {
        if (JOptionPane.showConfirmDialog(this, "Möchten Sie den Bestand wirklich löschen ?") == JOptionPane.YES_OPTION) {
            DBHandling.deleteRecords("MPBestand", "BestID", bestid);
            DBHandling.deleteRecords("MPBuchung", "BestID", bestid);
            reloadBestandTable();
        }
    }

    /**
     * Löscht einen bestimmten Bestand und die zugehörigen Buchungen.
     */
//    private void closeBestand(long bestid){
//        if (JOptionPane.showConfirmDialog(this, "Möchten Sie den Bestand wirklich abschließen ?") == JOptionPane.YES_OPTION){
//            
//            HashMap hm = new HashMap();
//            hm.put("BestID", bestid);
//            hm.put("BHPID", 0);
//            hm.put("Menge", op.care.med.DBHandling.getBestandSumme(bestid) * -1);
//            hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//            hm.put("PIT", "!NOW!");
//            op.tools.DBHandling.insertRecord("MPBuchung",hm);
//            
//            HashMap hm2 = new HashMap();
//            hm2.put("Aus","!NOW!");
//            op.tools.DBHandling.updateRecord("MPBestand",hm2,"BestID",bestid);
//            hm2.clear();
//            
//            reloadBestandTable();
//        }
//    }

    /**
     * Löscht eine Buchung.
     */
    private void deleteBuchung(long buchid) {
        if (JOptionPane.showConfirmDialog(this, "Möchten Sie die Buchung wirklich löschen ?") == JOptionPane.YES_OPTION) {
            DBHandling.deleteRecords("MPBuchung", "BuchID", buchid);
            reloadBuchungTable();
        }
    }

    /**
     * Öffnet den Dialog DlgEditBestand.
     */
    private void newBestand() {
        new DlgEditBestand(this, vorid);
        reloadBestandTable();
    }

    /**
     * Öffnet den Dialog DlgEditBuchung
     */
    private void newBuchung() {
        new DlgEditBuchung(this, bestid);
        reloadBuchungTable();
        TMResultSet tm = (TMResultSet) tblVorrat.getModel();
        tm.reload(tblVorrat.getSelectedRow(), tblVorrat.getSelectedColumn());

    }

    private void resetBuchung() {
        if (JOptionPane.showConfirmDialog(this, "Sind Sie sicher ?") == JOptionPane.YES_OPTION) {
            op.care.med.DBHandling.resetBestand(bestid);
            reloadVorratTable();
        }
    }

    private void reloadVorratTable() {
        ListSelectionModel lsm = tblVorrat.getSelectionModel();
        if (lslV != null) {
            lsm.removeListSelectionListener(lslV);
        }

        if (!bwkennung.equals("")) {
            String sql = "SELECT DISTINCT v.VorID, v.Text 'Name des Vorrats', ifnull(b.saldo, 0.00) Bestandsmenge" +
                    " FROM MPVorrat v " +
                    " LEFT OUTER JOIN (" +
                    "   SELECT best.VorID, sum(buch.Menge) saldo FROM MPBestand best " +
                    "   INNER JOIN MPVorrat v ON v.VorID = best.VorID " +
                    "   INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
                    "   WHERE v.BWKennung=? " + // Diese Zeile ist eigentlich nicht nötig. Beschleunigt aber ungemein.
                    "   GROUP BY best.VorID" +
                    " ) b ON b.VorID = v.VorID" +
                    " WHERE v.BWKennung=? " +
                    (cbClosedVorrat.isSelected() ? "" : " AND v.Bis = '9999-12-31 23:59:59' ") +
                    " ORDER BY v.Text ";

            try {
                PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
                stmt.setString(1, bwkennung);
                stmt.setString(2, bwkennung);
                ResultSet rs = stmt.executeQuery();

                lslV = new HandleVorratSelections();

                tblVorrat.setModel(new TMResultSet(rs));

                tblVorrat.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
                lsm.addListSelectionListener(lslV);

                //jspDosis.dispatchEvent(new ComponentEvent(jspDosis, ComponentEvent.COMPONENT_RESIZED));
                for (int i = 0; i < tblVorrat.getModel().getColumnCount(); i++) {
                    tblVorrat.getColumnModel().getColumn(i).setCellRenderer(new RNDStandard());
                }
            } catch (SQLException ex) {
                new DlgException(ex);
            }
        } else {
            tblVorrat.setModel(new DefaultTableModel());
        }
        ListSelectionModel lsm1 = tblBestand.getSelectionModel();
        if (lslB != null) {
            lsm1.removeListSelectionListener(lslB);
        }
        tblBestand.setModel(new DefaultTableModel());
        tblBuchung.setModel(new DefaultTableModel());
    }

    private void reloadBestandTable() {
        if (vorid == 0) {
            tblBestand.setModel(new DefaultTableModel());
            return;
        }
        String sql = " SELECT best.BestID, best.BestID, CONCAT(mprd.Bezeichnung,if(daf.Zusatz IS NULL, '', CONCAT(', ', daf.Zusatz)), ', ', " +
                " F.Zubereitung) Produkt, Date(Ein) Eingang, Date(Anbruch) Anbruch, Date(Aus) Aus, " +
                " ifnull(best.Text, '') " +
                " TextBestand, mp.PZN, mp.Inhalt, sum.saldo Rest, APV, NextBest, " +
                " CASE mp.Groesse WHEN 0 THEN 'N1' WHEN 1 THEN 'N2' " +
                " WHEN 2 THEN 'N3' WHEN 3 THEN 'AP' WHEN 4 THEN 'OP' ELSE " +
                " '' END Groesse FROM MPBestand best " +
                "	INNER JOIN MPDarreichung daf ON daf.DafID = best.DafID " +
                "   INNER JOIN MProdukte mprd ON mprd.MedPID = daf.MedPID " +
                "	INNER JOIN MPFormen F ON daf.FormID = F.FormID " +
                "	LEFT OUTER JOIN MPackung mp ON mp.MPID = best.MPID " +
                "       LEFT OUTER JOIN " +
                "           ( " +
                "               SELECT best.BestID, ifnull(sum(buch.Menge),0) saldo FROM MPBestand best " +
                "               INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
                "               WHERE best.VorID=? " + // Diese Zeile ist eigentlich nicht nötig. Beschleunigt aber ungemein.
                "               GROUP BY best.BestID " +
                "           ) sum ON sum.BestID = best.BestID " +
                " WHERE best.VorID = ? " +
                (cbClosedBestand.isSelected() ? "" : " AND best.Aus = '9999-12-31 23:59:59' ") +
                " ORDER BY Anbruch ";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, vorid);
            stmt.setLong(2, vorid);
            ResultSet rs = stmt.executeQuery();

            ListSelectionModel lsm = tblBestand.getSelectionModel();
            if (lslB != null) {
                lsm.removeListSelectionListener(lslB);
            }
            lslB = new HandleBestandSelections();

            tblBestand.setModel(new TMResultSet(rs));
            tblBestand.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            lsm.addListSelectionListener(lslB);
            //jspDosis.dispatchEvent(new ComponentEvent(jspDosis, ComponentEvent.COMPONENT_RESIZED));
            for (int i = 0; i < tblBestand.getModel().getColumnCount(); i++) {
                tblBestand.getColumnModel().getColumn(i).setCellRenderer(new RNDStandard());
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        tblBuchung.setModel(new DefaultTableModel());
        bestid = 0;
    }

    private void reloadBuchungTable() {
        if (bestid == 0) {
            tblBuchung.setModel(new DefaultTableModel());
            return;
        }
        String sql = "SELECT BuchID, Date(PIT) Datum, IFNULL(Text, '') Text, Menge, UKennung FROM MPBuchung " +
                " WHERE BestID = ? " +
                " ORDER BY PIT ";
        // Hier gehts weiter

        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, bestid);
            ResultSet rs = stmt.executeQuery();

            tblBuchung.setModel(new TMResultSet(rs));
            tblBuchung.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            //jspDosis.dispatchEvent(new ComponentEvent(jspDosis, ComponentEvent.COMPONENT_RESIZED));
            for (int i = 0; i < tblBuchung.getModel().getColumnCount(); i++) {
                tblBuchung.getColumnModel().getColumn(i).setCellRenderer(new RNDStandard());
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBestandsliste;
    private javax.swing.JButton btnClose;
    private javax.swing.JCheckBox cbClosedBestand;
    private javax.swing.JCheckBox cbClosedVorrat;
    private javax.swing.JComboBox cmbBW;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JScrollPane jspBestand;
    private javax.swing.JScrollPane jspBuchung;
    private javax.swing.JScrollPane jspVorrat;
    private javax.swing.JLabel lblBW;
    private javax.swing.JLabel lblFrage;
    private javax.swing.JPanel pnl123;
    private javax.swing.JTable tblBestand;
    private javax.swing.JTable tblBuchung;
    private javax.swing.JTable tblVorrat;
    private javax.swing.JTextField txtSuche;
    // End of variables declaration//GEN-END:variables

    class HandleVorratSelections implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent lse) {
            // Erst reagieren wenn der Auswahl-Vorgang abgeschlossen ist.
            TMResultSet tm = (TMResultSet) tblVorrat.getModel();
            if (tm.getRowCount() <= 0) {
                return;
            }

            if (!lse.getValueIsAdjusting()) {
                DefaultListSelectionModel lsm = (DefaultListSelectionModel) lse.getSource();
                if (lsm.isSelectionEmpty()) {
                    vorid = 0;
                } else {
                    vorid = ((BigInteger) tm.getPK(lsm.getLeadSelectionIndex())).longValue();
                }
                reloadBestandTable();
            }
        }
    }

    class HandleBestandSelections implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent lse) {
            // Erst reagieren wenn der Auswahl-Vorgang abgeschlossen ist.
            TMResultSet tm = (TMResultSet) tblBestand.getModel();
            if (tm.getRowCount() <= 0) {
                return;
            }

            if (!lse.getValueIsAdjusting()) {
                DefaultListSelectionModel lsm = (DefaultListSelectionModel) lse.getSource();
                if (lsm.isSelectionEmpty()) {
                    bestid = 0;
                } else {
                    bestid = ((BigInteger) tm.getPK(lsm.getLeadSelectionIndex())).longValue();
                    reloadBuchungTable();
                }
            }
        }
    }
}
