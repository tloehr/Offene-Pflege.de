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
import javax.persistence.EntityManager;
import javax.swing.border.*;
import javax.swing.event.*;

import entity.Bewohner;
import entity.BewohnerTools;
import entity.verordnungen.MedBestand;
import entity.verordnungen.MedBuchungenTools;
import entity.verordnungen.MedVorrat;
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

    private MedVorrat vorrat;
    private MedBestand bestand;
    private Bewohner bewohner;
    private Component parent;
    private Component thisDialog;
    private ListSelectionListener lslV;
    private ListSelectionListener lslB;
    private JPopupMenu menuV;
    private JPopupMenu menuB;
    private JPopupMenu menuBuch;
    private boolean ignoreEvent;
    private OCSec ocs;
    private long vorid;
    BigInteger myvorid;
    private long bestid;
    private String bwkennung;

    /**
     * Creates new form DlgVorrat
     */
    public DlgVorrat(JDialog parent, Bewohner bewohner) {
        super(parent, true);
        this.parent = parent;
        this.bewohner = bewohner;
        initDialog();
    }

    public DlgVorrat(JFrame parent, Bewohner bewohner) {
        super(parent, true);
        this.parent = parent;
        this.bewohner = bewohner;
        initDialog();
    }

    public DlgVorrat(JDialog parent) {
        super(parent, true);
        this.parent = parent;
        this.bewohner = null;
        initDialog();
    }

    public DlgVorrat(JFrame parent) {
        super(parent, true);
        this.parent = parent;
        this.bewohner = null;
        initDialog();
    }

    private void initDialog() {
        ocs = OPDE.getOCSec();
        setTitle(SYSTools.getWindowTitle("Medikamentenvorrat"));
        thisDialog = this;
        ignoreEvent = false;
        initComponents();
        txtSuche.setEnabled(bewohner == null);
        cmbBW.setEnabled(bewohner == null);
        if (bewohner == null) {
            txtSuche.requestFocus();
            lblBW.setText("");
        } else {
            BewohnerTools.setBWLabel(lblBW, bewohner);
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
        lblFrage = new JLabel();
        lblBW = new JLabel();
        jPanel2 = new JPanel();
        jspVorrat = new JScrollPane();
        tblVorrat = new JTable();
        cbClosedVorrat = new JCheckBox();
        jPanel3 = new JPanel();
        jspBestand = new JScrollPane();
        tblBestand = new JTable();
        cbClosedBestand = new JCheckBox();
        btnClose = new JButton();
        pnl123 = new JPanel();
        jspBuchung = new JScrollPane();
        tblBuchung = new JTable();
        jPanel1 = new JPanel();
        txtSuche = new JTextField();
        cmbBW = new JComboBox();
        jSeparator1 = new JSeparator();
        jToolBar1 = new JToolBar();
        btnBestandsliste = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();

        //---- lblFrage ----
        lblFrage.setFont(new Font("Dialog", Font.BOLD, 24));
        lblFrage.setText("Medikamenten Vorrat");

        //---- lblBW ----
        lblBW.setFont(new Font("Dialog", Font.BOLD, 18));
        lblBW.setForeground(new Color(255, 51, 0));
        lblBW.setText("Nachname, Vorname (*GebDatum, 00 Jahre) [??1]");

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("Vorr\u00e4te"));

            //======== jspVorrat ========
            {
                jspVorrat.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        jspVorratMousePressed(e);
                    }
                });

                //---- tblVorrat ----
                tblVorrat.setModel(new DefaultTableModel(
                    new Object[][] {
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                    },
                    new String[] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                    }
                ));
                tblVorrat.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        tblVorratMousePressed(e);
                    }
                });
                jspVorrat.setViewportView(tblVorrat);
            }

            //---- cbClosedVorrat ----
            cbClosedVorrat.setText("Abgeschlossene anzeigen");
            cbClosedVorrat.setBorder(BorderFactory.createEmptyBorder());
            cbClosedVorrat.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cbClosedVorratItemStateChanged(e);
                }
            });

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup()
                            .addComponent(jspVorrat, GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                            .addComponent(cbClosedVorrat))
                        .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cbClosedVorrat)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jspVorrat, GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }

        //======== jPanel3 ========
        {
            jPanel3.setBorder(new TitledBorder("Best\u00e4nde"));

            //======== jspBestand ========
            {
                jspBestand.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        jspBestandMousePressed(e);
                    }
                });

                //---- tblBestand ----
                tblBestand.setModel(new DefaultTableModel(
                    new Object[][] {
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                    },
                    new String[] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                    }
                ));
                tblBestand.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        tblBestandMousePressed(e);
                    }
                });
                jspBestand.setViewportView(tblBestand);
            }

            //---- cbClosedBestand ----
            cbClosedBestand.setText("Abgeschlossene anzeigen");
            cbClosedBestand.setBorder(BorderFactory.createEmptyBorder());
            cbClosedBestand.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cbClosedBestandItemStateChanged(e);
                }
            });

            GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup()
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup()
                            .addComponent(jspBestand, GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                            .addComponent(cbClosedBestand))
                        .addContainerGap())
            );
            jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup()
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(cbClosedBestand)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jspBestand, GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }

        //---- btnClose ----
        btnClose.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
        btnClose.setText("Schlie\u00dfen");
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCloseActionPerformed(e);
            }
        });

        //======== pnl123 ========
        {
            pnl123.setBorder(new TitledBorder("Buchungen"));

            //======== jspBuchung ========
            {
                jspBuchung.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        jspBuchungMousePressed(e);
                    }
                });

                //---- tblBuchung ----
                tblBuchung.setModel(new DefaultTableModel(
                    new Object[][] {
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                    },
                    new String[] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                    }
                ));
                tblBuchung.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        tblBuchungMousePressed(e);
                    }
                });
                jspBuchung.setViewportView(tblBuchung);
            }

            GroupLayout pnl123Layout = new GroupLayout(pnl123);
            pnl123.setLayout(pnl123Layout);
            pnl123Layout.setHorizontalGroup(
                pnl123Layout.createParallelGroup()
                    .addGroup(pnl123Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jspBuchung, GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                        .addContainerGap())
            );
            pnl123Layout.setVerticalGroup(
                pnl123Layout.createParallelGroup()
                    .addGroup(pnl123Layout.createSequentialGroup()
                        .addComponent(jspBuchung, GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Suche"));

            //---- txtSuche ----
            txtSuche.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    txtSucheCaretUpdate(e);
                }
            });
            txtSuche.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtSucheActionPerformed(e);
                }
            });
            txtSuche.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtSucheFocusGained(e);
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

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(cmbBW, GroupLayout.Alignment.LEADING, 0, 771, Short.MAX_VALUE)
                            .addComponent(txtSuche, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 771, Short.MAX_VALUE))
                        .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtSuche, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbBW, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(22, Short.MAX_VALUE))
            );
        }

        //======== jToolBar1 ========
        {
            jToolBar1.setFloatable(false);

            //---- btnBestandsliste ----
            btnBestandsliste.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/fileprint.png")));
            btnBestandsliste.setText("Bestandsliste");
            btnBestandsliste.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnBestandslisteActionPerformed(e);
                }
            });
            jToolBar1.add(btnBestandsliste);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(jSeparator1, GroupLayout.DEFAULT_SIZE, 821, Short.MAX_VALUE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(pnl123, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnClose, GroupLayout.Alignment.TRAILING))
                    .addContainerGap())
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblFrage, GroupLayout.DEFAULT_SIZE, 821, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblBW, GroupLayout.DEFAULT_SIZE, 821, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
                .addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, 843, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lblFrage)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lblBW)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(pnl123, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnClose)
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
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
                EntityManager em = OPDE.createEM();
                MedBestand bestand = em.find(MedBestand.class, mybestid);
                em.close();

                if (bestand != null) {
                    reloadVorratTable();
                    DefaultComboBoxModel dcbm = null;
                    cmbBW.setModel(new DefaultComboBoxModel(new Bewohner[]{bestand.getVorrat().getBewohner()}));
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
                    //SYSPrint.printLabel(mybestid);
                }
            });
            menuV.add(itemPopupPrint);
            // ----------------
            JMenuItem itemPopupClose = new JMenuItem("Bestand abschließen");
            itemPopupClose.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (JOptionPane.showConfirmDialog(thisDialog, "Sind sie sicher ?", "Bestand abschließen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        //op.care.med.DBHandling.closeBestand(mybestid, "", false, MedBuchungenTools.STATUS_KORREKTUR_MANUELL);
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
            //itemPopupAnbruch.setEnabled(!op.care.med.DBHandling.hasAnbruch(vorid)); // Nur an anbrechen lassen, wenn noch keine im Anbruch ist.
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
                            //op.care.med.DBHandling.deleteVorrat(vorid);
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
                        //op.care.med.DBHandling.closeVorrat(vorid);
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

                //tblVorrat.setModel(new BeanTableModel());

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
    private JLabel lblFrage;
    private JLabel lblBW;
    private JPanel jPanel2;
    private JScrollPane jspVorrat;
    private JTable tblVorrat;
    private JCheckBox cbClosedVorrat;
    private JPanel jPanel3;
    private JScrollPane jspBestand;
    private JTable tblBestand;
    private JCheckBox cbClosedBestand;
    private JButton btnClose;
    private JPanel pnl123;
    private JScrollPane jspBuchung;
    private JTable tblBuchung;
    private JPanel jPanel1;
    private JTextField txtSuche;
    private JComboBox cmbBW;
    private JSeparator jSeparator1;
    private JToolBar jToolBar1;
    private JButton btnBestandsliste;
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
