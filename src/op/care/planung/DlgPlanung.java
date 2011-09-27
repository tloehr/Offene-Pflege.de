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
package op.care.planung;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import op.OPDE;
import op.care.DFNImport;
import op.tools.ListElement;
import op.share.bwinfo.BWInfo;
import op.tools.DBRetrieve;
import op.tools.DlgException;
import op.tools.SYSCalendar;
import op.tools.SYSTools;

/**
 *
 * @author  root
 */
public class DlgPlanung extends javax.swing.JDialog {

    public static final int NEW_MODE = 1; // Neu
    public static final int EDIT_MODE = 2; // Korrigieren
    public static final int CHANGE_MODE = 3; // Ändern
    public static final int TEMPLATE_MODE = 4; // Verwendung von Vorlagen
    private String bwkennung;
    private PropertyChangeListener myPropertyChangeListener;
    private int editMode;
    private long planid;
    private long plankenn;
    private java.awt.Frame parent;
    private boolean doDeleteTmp; // Sollen die TMPs nach dem Dispose gelöscht werden ?
    private JPopupMenu menu;
    private String oldSituation = ""; // Situation, wie sie am Anfang der Bearbeitung war. Wird nur bei CHANGE gebraucht.
    private String oldZiele = ""; // Ziele, wie sie am Anfang der Bearbeitung waren. Wird nur bei CHANGE gebraucht.

    /** Creates new form DlgPlanung */
    public DlgPlanung(java.awt.Frame parent, String bwkennung, long planid, int mode) {
        super(parent, true);
        this.parent = parent;
        this.bwkennung = bwkennung;
        this.planid = planid;
        this.editMode = mode;
        initComponents();
        initDialog();
    }

    public DlgPlanung(java.awt.Frame parent, String bwkennung) {
        super(parent, true);
        this.parent = parent;
        this.bwkennung = bwkennung;
        this.planid = 0;
        this.plankenn = 0;
        this.editMode = NEW_MODE;
        initComponents();
        initDialog();
    }

    private void initDialog() {
        doDeleteTmp = true;
        this.setTitle(SYSTools.getWindowTitle("Bearbeitung einer Pflegeplanung"));
        cmbKategorie.setModel(op.share.bwinfo.DBHandling.ladeKategorien(BWInfo.ART_PFLEGE, false, false));
        SYSTools.setBWLabel(lblBW, bwkennung);

        myPropertyChangeListener = new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                saveOK();
            }
        };

        jdcKontrolle.addPropertyChangeListener(myPropertyChangeListener);

        // Welchen Zustand soll der Dialog annehmen
        // Hier werden die Formularfelder entsprechend vorbesetzt.
        txtSuche.setText("");
        tblBib.setModel(new DefaultTableModel());
        switch (editMode) {
            case NEW_MODE: {
                planid = 0;
                plankenn = 0;
                lblTitle.setText("Pflegeplanung (Neueingabe)");
                txtSituation.setText("");
                txtStichwort.setText("");
                // Nächste Kontrolle einfach mal in 60 Tagen. Frühestens ab morgen.
                jdcKontrolle.setDate(SYSCalendar.addDate(SYSCalendar.nowDBDate(), 60));
                jdcKontrolle.setMinSelectableDate(SYSCalendar.addDate(SYSCalendar.nowDBDate(), 1));
                // Keine Kategorie auswählen.
                cmbKategorie.setSelectedIndex(-1);
                // Tabelle leeren
                tblPlanung.setModel(new DefaultTableModel());
                break;
            }
            case EDIT_MODE: {
                lblTitle.setText("Pflegeplanung (Bearbeiten, Korrektur)");
                HashMap planung = DBRetrieve.getSingleRecord("Planung", "PlanID", planid);
                DBHandling.copy2tmp(planid);
                plankenn = ((BigInteger) planung.get("PlanKennung")).longValue();
                txtSituation.setText(SYSTools.catchNull(planung.get("Situation").toString()));
                txtZiele.setText(SYSTools.catchNull(planung.get("Ziel").toString()));
                txtStichwort.setText(planung.get("Stichwort").toString());
                Date nkontrolle = new Date(((java.sql.Date) planung.get("NKontrolle")).getTime());
                jdcKontrolle.setDate(nkontrolle);
                jdcKontrolle.setMinSelectableDate(SYSCalendar.addDate(SYSCalendar.nowDBDate(), 1));
                long bwikid = ((BigInteger) planung.get("BWIKID")).longValue();
                SYSTools.selectInComboBox(cmbKategorie, bwikid);
                reloadMeinePlanung();
                break;
            }
            case TEMPLATE_MODE: {
                lblTitle.setText("Pflegeplanung (Vorlage)");
                HashMap planung = DBRetrieve.getSingleRecord("Planung", "PlanID", planid);
                DBHandling.copy2tmp(planid);
                planid = 0;
                plankenn = 0;
                txtSituation.setText(SYSTools.catchNull(planung.get("Situation").toString()));
                txtZiele.setText(SYSTools.catchNull(planung.get("Ziel").toString()));
                txtStichwort.setText(planung.get("Stichwort").toString());
                // Nächste Kontrolle einfach mal in 60 Tagen. Frühestens ab morgen.
                jdcKontrolle.setDate(SYSCalendar.addDate(SYSCalendar.nowDBDate(), 60));
                jdcKontrolle.setMinSelectableDate(SYSCalendar.addDate(SYSCalendar.nowDBDate(), 1));
                long bwikid = ((BigInteger) planung.get("BWIKID")).longValue();
                SYSTools.selectInComboBox(cmbKategorie, bwikid);
                reloadMeinePlanung();
                break;
            }
            case CHANGE_MODE: {
                lblTitle.setText("Pflegeplanung (Veränderung der Situation)");
                DBHandling.copy2tmp(planid);
                HashMap planung = DBRetrieve.getSingleRecord("Planung", "PlanID", planid);
                plankenn = ((BigInteger) planung.get("PlanKennung")).longValue();
                oldSituation = SYSTools.catchNull(planung.get("Situation").toString());
                oldZiele = SYSTools.catchNull(planung.get("Ziel").toString());
                txtSituation.setText(SYSTools.catchNull(planung.get("Situation").toString()));
                txtZiele.setText(SYSTools.catchNull(planung.get("Ziel").toString()));
                txtStichwort.setText(planung.get("Stichwort").toString());
                jdcKontrolle.setDate(SYSCalendar.addDate(SYSCalendar.nowDBDate(), 60));
                jdcKontrolle.setMinSelectableDate(SYSCalendar.addDate(SYSCalendar.nowDBDate(), 1));
                long bwikid = ((BigInteger) planung.get("BWIKID")).longValue();
                // Kategorie kann man nicht mehr ändern                
                SYSTools.selectInComboBox(cmbKategorie, bwikid);
                cmbKategorie.setEnabled(false);
                reloadMeinePlanung();
                break;
            }
            default: {
            }
        } // SWITCH
        SYSTools.centerOnParent(parent, this);
        saveOK();
        setVisible(true);
    }

    @Override
    public void dispose() {
        if (doDeleteTmp) {
            DBHandling.dropTmp();
        }
        jdcKontrolle.removePropertyChangeListener(myPropertyChangeListener);
        jdcKontrolle.cleanup();
        SYSTools.unregisterListeners(jdcKontrolle);
        SYSTools.unregisterListeners(this);
        super.dispose();
    }

    private void reloadMeinePlanung() {
        ArrayList plan = DBHandling.loadPlanung(bwkennung, planid, OPDE.getLogin().getLoginID(), editMode != CHANGE_MODE);

        tblPlanung.setModel(new TMPlanung(plan));
        tblPlanung.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jspPlanung.dispatchEvent(new ComponentEvent(jspPlanung, ComponentEvent.COMPONENT_RESIZED));
        tblPlanung.getColumnModel().getColumn(0).setCellRenderer(new RNDPlanung());
//        tblPlanung.getColumnModel().getColumn(1).setCellRenderer(new RNDPlanung());
    }

    private void reloadBibliothek() {
        ArrayList bib = DBHandling.loadBibliothek(txtSuche.getText(), bwkennung);

        tblBib.setModel(new TMPlanung(bib));
        tblBib.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jspBib.dispatchEvent(new ComponentEvent(jspBib, ComponentEvent.COMPONENT_RESIZED));
        //tblBib.getColumnModel().getColumn(0).setCellRenderer(new RNDPlanung());
        tblBib.getColumnModel().getColumn(0).setCellRenderer(new RNDPlanung());

    //btnAdd.setEnabled(!txtSuche.equals("") && tblBib.getModel().getRowCount() == 0);

    }

    private void saveOK() {
        // Gründe, warum man nicht speichern kann.
        boolean stichwortXX = txtStichwort.getText().trim().equals("");
        boolean datumXX = jdcKontrolle.getDate() == null;
        boolean kategorieXX = cmbKategorie.getSelectedIndex() < 0;
//        boolean situationXX1 = (editMode == CHANGE_MODE && txtSituation.getText().equals(""));
//        boolean situationXX2 = (editMode == CHANGE_MODE && txtSituation.getText().equalsIgnoreCase(oldSituation));
//        boolean zieleXX1 = (editMode == CHANGE_MODE && txtZiele.getText().equals(""));
//        boolean zieleXX2 = (editMode == CHANGE_MODE && txtZiele.getText().equalsIgnoreCase(oldZiele));
        btnSave.setEnabled(!(stichwortXX || datumXX || kategorieXX)); //|| situationXX1 || situationXX2 || zieleXX1 || zieleXX2));

        if (!btnSave.isEnabled()) {
            String ursache = "<html><body>Sie können auf dem / den folgenden Grund/Gründen nicht speichern:<ul>";
            ursache += (stichwortXX ? "<li>Sie <b>müssen</b> ein Stichwort angeben.</li>" : "");
            ursache += (datumXX ? "<li>Sie haben ein falsches datum für die nächste Kontrolle angegeben.</li>" : "");
            ursache += (kategorieXX ? "<li>Sie haben keine Kategorie für die Planung ausgewählt.</li>" : "");
//            ursache += (situationXX1 ? "<li>Sie haben keinen Text zur Situationsbeschreibung eingegeben. Bei einer Planungsänderung ist das Pflicht.</li>" : "");
//            ursache += (situationXX2 ? "<li>Sie haben den Text zur Situationsbeschreibung nicht verändert. Bei einer Planungsänderung ist das Pflicht.</li>" : "");
//            ursache += (zieleXX1 ? "<li>Sie haben keinen Text zur Zielbeschreibung eingegeben. Bei einer Planungsänderung ist das Pflicht.</li>" : "");
//            ursache += (zieleXX2 ? "<li>Sie haben den Text zur Zielbeschreibung nicht verändert. Bei einer Planungsänderung ist das Pflicht.</li>" : "");
            ursache += "</ul></body></html>";
            btnSave.setToolTipText(ursache);
        } else {
            btnSave.setToolTipText(null);
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblBW = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jspPlanung = new javax.swing.JScrollPane();
        tblPlanung = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtStichwort = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cmbKategorie = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jdcKontrolle = new com.toedter.calendar.JDateChooser();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtSituation = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtZiele = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        txtSuche = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jspBib = new javax.swing.JScrollPane();
        tblBib = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/cancel.png"))); // NOI18N
        btnCancel.setText("Abbrechen");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/apply.png"))); // NOI18N
        btnSave.setText("Speichern");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lblTitle.setFont(new java.awt.Font("Dialog", 1, 24));
        lblTitle.setText("Pflegeplanung");

        lblBW.setFont(new java.awt.Font("Dialog", 1, 18));
        lblBW.setForeground(new java.awt.Color(255, 51, 0));
        lblBW.setText("Nachname, Vorname (*GebDatum, 00 Jahre) [??1]");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 629, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBW))
                .addContainerGap(272, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBW)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setDividerLocation(700);

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jSplitPane2.setDividerLocation(350);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jspPlanung.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jspPlanungComponentResized(evt);
            }
        });

        tblPlanung.setModel(new javax.swing.table.DefaultTableModel(
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
        tblPlanung.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblPlanungMousePressed(evt);
            }
        });
        jspPlanung.setViewportView(tblPlanung);

        jSplitPane2.setRightComponent(jspPlanung);

        jLabel2.setBackground(new java.awt.Color(255, 204, 204));
        jLabel2.setFont(new java.awt.Font("Dialog", 1, 18));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Meine Pflegeplanung");
        jLabel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel2.setOpaque(true);

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel4.setText("Stichwort:");

        txtStichwort.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtStichwortCaretUpdate(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel5.setText("Kategorie:");

        cmbKategorie.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbKategorie.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbKategorieItemStateChanged(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel7.setText("Kontrolle:");

        txtSituation.setColumns(20);
        txtSituation.setLineWrap(true);
        txtSituation.setRows(5);
        txtSituation.setWrapStyleWord(true);
        txtSituation.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtSituationCaretUpdate(evt);
            }
        });
        jScrollPane3.setViewportView(txtSituation);

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel3.setText("Situation:");

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel8.setText("Ziele:");

        txtZiele.setColumns(20);
        txtZiele.setLineWrap(true);
        txtZiele.setRows(5);
        txtZiele.setWrapStyleWord(true);
        txtZiele.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtZieleCaretUpdate(evt);
            }
        });
        jScrollPane1.setViewportView(txtZiele);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(cmbKategorie, 0, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jdcKontrolle, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtStichwort, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE))))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(14, 14, 14)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(txtStichwort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7)
                                .addComponent(cmbKategorie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5))
                            .addComponent(jdcKontrolle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel4))
                .addGap(6, 6, 6)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                    .addComponent(jLabel8))
                .addContainerGap())
        );

        jSplitPane2.setTopComponent(jPanel5);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel2);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtSuche.setToolTipText("Geben Sie % ein, wenn Sie alles angezeigt bekommen möchten.");
        txtSuche.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtSucheCaretUpdate(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(153, 153, 255));
        jLabel6.setFont(new java.awt.Font("Dialog", 1, 18));
        jLabel6.setForeground(new java.awt.Color(255, 255, 102));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Bibliothek");
        jLabel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel6.setOpaque(true);

        jspBib.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jspBibComponentResized(evt);
            }
        });

        tblBib.setModel(new javax.swing.table.DefaultTableModel(
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
        tblBib.setDragEnabled(true);
        tblBib.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBibMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblBibMousePressed(evt);
            }
        });
        jspBib.setViewportView(tblBib);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel1.setText("Suche:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jspBib, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSuche, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtSuche, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jspBib, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 919, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCancel)
                    .addComponent(btnSave))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void txtSucheCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtSucheCaretUpdate
        reloadBibliothek();
    }//GEN-LAST:event_txtSucheCaretUpdate

    private void jspBibComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspBibComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        if (tblBib.getRowCount() <= 0) {
            return;
        }
        Dimension dim = jsp.getSize();
        int textWidth = dim.width - 25;
        TableColumnModel tcm1 = tblBib.getColumnModel();
        //tcm1.getColumn(0).setPreferredWidth(85);
        tcm1.getColumn(0).setPreferredWidth(textWidth);
        //tcm1.getColumn(0).setHeaderValue("Kennung");
        tcm1.getColumn(0).setHeaderValue("Text");
    }//GEN-LAST:event_jspBibComponentResized

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void jspPlanungComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspPlanungComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        if (tblPlanung.getRowCount() <= 0) {
            return;
        }
        Dimension dim = jsp.getSize();
        int textWidth = dim.width - 25;
        TableColumnModel tcm1 = tblPlanung.getColumnModel();
//        tcm1.getColumn(0).setPreferredWidth(85);
        tcm1.getColumn(0).setPreferredWidth(textWidth);
//        tcm1.getColumn(0).setHeaderValue("Kennung");
        tcm1.getColumn(0).setHeaderValue("Text");
    }//GEN-LAST:event_jspPlanungComponentResized

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
        doDeleteTmp = false;
        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void cmbKategorieItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbKategorieItemStateChanged
        saveOK();
    }//GEN-LAST:event_cmbKategorieItemStateChanged

    private void txtStichwortCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtStichwortCaretUpdate
        saveOK();
    }//GEN-LAST:event_txtStichwortCaretUpdate

    private void tblPlanungMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPlanungMousePressed
        if (!evt.isPopupTrigger()) {
            return;
        }
        Point p = evt.getPoint();
        ListSelectionModel lsm = tblPlanung.getSelectionModel();
        int row = tblPlanung.rowAtPoint(p);
        if (lsm.isSelectionEmpty() || (lsm.getMinSelectionIndex() == lsm.getMaxSelectionIndex())) {
            lsm.setSelectionInterval(row, row);
        }
        SYSTools.unregisterListeners(menu);
        menu = new JPopupMenu();

        // -------------------------------------------------
        JMenuItem itemPopupDelete = new JMenuItem("Entfernen");
        itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (JOptionPane.showConfirmDialog(parent, "Sollen die markierten Einträge wirklich entfernt werden ?",
                        "Einträge entfernen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    removeFromPlan();
                    reloadMeinePlanung();
                    reloadBibliothek();
                }
            }
        });
        menu.add(itemPopupDelete);
        itemPopupDelete.setEnabled(!kontrollenMarkiert());

        // -------------------------------------------------
        JMenuItem itemPopupTermin = new JMenuItem("Termine der Massnahmen bearbeiten");
        itemPopupTermin.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMassnahmen();
                reloadMeinePlanung();
            }
        });
        menu.add(itemPopupTermin);
        itemPopupTermin.setEnabled(nurMassnahmenMarkiert());

        // -------------------------------------------------

        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }//GEN-LAST:event_tblPlanungMousePressed

    private void tblBibMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBibMousePressed
        if (!evt.isPopupTrigger()) {
            return;
        }
        Point p = evt.getPoint();
        ListSelectionModel lsm = tblBib.getSelectionModel();
        int col = tblBib.columnAtPoint(p);
        int row = tblBib.rowAtPoint(p);
        if (lsm.isSelectionEmpty() || (lsm.getMinSelectionIndex() == lsm.getMaxSelectionIndex())) {
            lsm.setSelectionInterval(row, row);
        }
        SYSTools.unregisterListeners(menu);
        menu = new JPopupMenu();

        // -------------------------------------------------
        JMenuItem itemPopupAdd = new JMenuItem("Zur Planung hinzufügen");
        itemPopupAdd.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bib2plan();
                reloadMeinePlanung();
                reloadBibliothek();
            }
        });
        menu.add(itemPopupAdd);

        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }//GEN-LAST:event_tblBibMousePressed

    private void tblBibMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBibMouseClicked
        if (evt.getClickCount() == 2) {
            bib2plan();
            reloadMeinePlanung();
            reloadBibliothek();
        }
    }//GEN-LAST:event_tblBibMouseClicked

    private void txtSituationCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtSituationCaretUpdate
        saveOK();
    }//GEN-LAST:event_txtSituationCaretUpdate

    private void txtZieleCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtZieleCaretUpdate
        saveOK();
    }//GEN-LAST:event_txtZieleCaretUpdate

    private void save() {
        switch (editMode) {
            case NEW_MODE: {
                saveNEW();
                break;
            }
            case EDIT_MODE: {
                saveEDIT();
                break;
            }
            case CHANGE_MODE: {
                saveCHANGE();
                break;
            }
            case TEMPLATE_MODE: {
                saveTEMPLATE();
                break;
            }
            default: {
                // NOP
            }
        }
    }

    private void saveEDIT() {
        HashMap hm = new HashMap();
        hm.put("Stichwort", txtStichwort.getText());
        hm.put("Situation", txtSituation.getText());
        hm.put("Ziel", txtZiele.getText());
        ListElement lel = (ListElement) cmbKategorie.getSelectedItem();
        hm.put("BWIKID", lel.getPk());
        hm.put("NKontrolle", jdcKontrolle.getDate());

        Connection db = OPDE.getDb().db;
        try {
            // Hier beginnt eine Transaktion
            db.setAutoCommit(false);
            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            db.commit();

            if (!op.tools.DBHandling.updateRecord("Planung", hm, "PlanID", planid)) {
                throw new SQLException("Fehler bei Insert into Planung");
            }
            hm.clear();
            DBHandling.cleanDFN(planid);
            DBHandling.tmp2real(planid);
            DFNImport.importDFN(planid);

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

    private void saveTEMPLATE() {
        HashMap hm = new HashMap();
        hm.put("BWKennung", bwkennung);
        hm.put("Stichwort", txtStichwort.getText());
        hm.put("Situation", txtSituation.getText());
        hm.put("Ziel", txtZiele.getText());
        ListElement lel = (ListElement) cmbKategorie.getSelectedItem();
        hm.put("BWIKID", lel.getPk());
        hm.put("Von", "!NOW!");
        hm.put("Bis", "!BAW!");
        hm.put("AnUKennung", OPDE.getLogin().getUser().getUKennung());
        hm.put("AbUKennung", null);
        hm.put("PlanKennung", OPDE.getDb().getUID("__plankenn"));
        hm.put("NKontrolle", jdcKontrolle.getDate());

        Connection db = OPDE.getDb().db;
        try {
            // Hier beginnt eine Transaktion
            db.setAutoCommit(false);
            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            db.commit();

            planid = op.tools.DBHandling.insertRecord("Planung", hm);
            if (planid < 0) {
                throw new SQLException("Fehler bei Insert into Planung");
            }
            hm.clear();
            DBHandling.tmp2real(planid);
            DFNImport.importDFN(planid);

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

    private void saveCHANGE() {
        // Daten für die NEUE Planung
        HashMap hm = new HashMap();
        hm.put("BWKennung", bwkennung);
        hm.put("Stichwort", txtStichwort.getText());
        hm.put("Situation", txtSituation.getText());
        hm.put("Ziel", txtZiele.getText());
        ListElement lel = (ListElement) cmbKategorie.getSelectedItem();
        hm.put("BWIKID", lel.getPk());
        hm.put("Von", "!NOW+1!");
        hm.put("Bis", "!BAW!");
        hm.put("AnUKennung", OPDE.getLogin().getUser().getUKennung());
        hm.put("AbUKennung", null);
        hm.put("PlanKennung", plankenn);
        hm.put("NKontrolle", jdcKontrolle.getDate());

        Connection db = OPDE.getDb().db;
        try {
            // Hier beginnt eine Transaktion
            db.setAutoCommit(false);
            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            db.commit();

            DBHandling.absetzen(planid, txtSituation.getText());

            planid = op.tools.DBHandling.insertRecord("Planung", hm);
            if (planid < 0) {
                throw new SQLException("Fehler bei Insert into Planung");
            }
            hm.clear();
            DBHandling.tmp2real(planid);
            DFNImport.importDFN(planid, SYSCalendar.nowDB(), 0);

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

    private void saveNEW() {
        HashMap hm = new HashMap();
        hm.put("BWKennung", bwkennung);
        hm.put("Stichwort", txtStichwort.getText());
        hm.put("Situation", txtSituation.getText());
        hm.put("Ziel", txtZiele.getText());
        ListElement lel = (ListElement) cmbKategorie.getSelectedItem();
        hm.put("BWIKID", lel.getPk());
        hm.put("Von", "!NOW!");
        hm.put("Bis", "!BAW!");
        hm.put("AnUKennung", OPDE.getLogin().getUser().getUKennung());
        hm.put("AbUKennung", null);
        hm.put("PlanKennung", OPDE.getDb().getUID("__plankenn"));
        hm.put("NKontrolle", jdcKontrolle.getDate());

        Connection db = OPDE.getDb().db;
        try {
            // Hier beginnt eine Transaktion
            db.setAutoCommit(false);
            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            db.commit();
            planid = op.tools.DBHandling.insertRecord("Planung", hm);
            if (planid < 0) {
                throw new SQLException("Fehler bei Insert into Planung");
            }
            hm.clear();
            DBHandling.tmp2real(planid);
            DFNImport.importDFN(planid);

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

    private void editMassnahmen() {
        int[] sel = tblPlanung.getSelectedRows();
        //ArrayList relids = new ArrayList();
        ArrayList termids = new ArrayList();

        if (sel.length > 0) {
            for (int r = 0; r < sel.length; r++) {
                int row = sel[r];
                //long relid = ((Long) tblPlanung.getModel().getValueAt(row, TMPlanung.COL_RELID)).longValue();
                long termid = ((Long) tblPlanung.getModel().getValueAt(row, TMPlanung.COL_PKID)).longValue();
                //relids.add(relid);
                termids.add(termid);
            }

            DlgTermin dlg = new DlgTermin(this, termids, OPDE.getLogin().getLoginID(), false);
            dlg.showDialog();

        }
    }

    private boolean nurMassnahmenMarkiert() {
        int[] sel = tblPlanung.getSelectedRows();
        boolean result;
        if (sel.length > 0) {
            result = true;
            for (int r = 0; r < sel.length; r++) {
                int row = sel[r];
                int art = ((Integer) tblPlanung.getModel().getValueAt(row, TMPlanung.COL_ART)).intValue();
                if (art != DBHandling.ART_MASSNAHME) {
                    result = false;
                    r = sel.length;
                }
            }
        } else {
            result = false;
        }
        return result;
    }

    private boolean kontrollenMarkiert() {
        int[] sel = tblPlanung.getSelectedRows();
        boolean result;
        if (sel.length > 0) {
            result = false;
            for (int r = 0; r < sel.length; r++) {
                int row = sel[r];
                int art = ((Integer) tblPlanung.getModel().getValueAt(row, TMPlanung.COL_ART)).intValue();
                if (art == DBHandling.ART_KONTROLLEN) {
                    result = true;
                    r = sel.length;
                }
            }
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Kopiert die markierten Zeilen der Bibliothek in den Plan.
     * 
     */
    private void bib2plan() {
        if (!tblBib.getSelectionModel().isSelectionEmpty()) {
            Connection db = OPDE.getDb().db;
            try {
                // Hier beginnt eine Transaktion
                db.setAutoCommit(false);
                db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                db.commit();
                int[] sel = tblBib.getSelectedRows();
                //HashMap hm = new HashMap();
                for (int r = 0; r < sel.length; r++) {
                    int row = sel[r];
                    //int art = ((Integer) tblBib.getModel().getValueAt(row, TMPlanung.COL_ART)).intValue();
                    //String t = tblBib.getModel().getValueAt(row, TMPlanung.COL_PKID).toString();
                    long pkid = ((Long) tblBib.getModel().getValueAt(row, TMPlanung.COL_PKID)).longValue();
                    DBHandling.addMassnahme2Planung(pkid, planid);
                }
                db.commit();
                db.setAutoCommit(true);
            } catch (SQLException ex) {
                try {
                    db.rollback();
                    new DlgException(ex);
                } catch (SQLException ex1) {
                    System.exit(1);
                }
            }
        }
    }

    void removeFromPlan() {
        if (!tblPlanung.getSelectionModel().isSelectionEmpty()) {
            Connection db = OPDE.getDb().db;
            try {
                // Hier beginnt eine Transaktion
                db.setAutoCommit(false);
                db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                db.commit();
                int[] sel = tblPlanung.getSelectedRows();
                HashMap hm = new HashMap();
                for (int r = 0; r < sel.length; r++) {
                    int row = sel[r];
                    long termid = ((Long) tblPlanung.getModel().getValueAt(row, TMPlanung.COL_PKID)).longValue();

                    if (op.tools.DBHandling.deleteRecords("MassTermin", "TermID", termid) < 0) {
                        throw new SQLException("FEHLER bei MassTermin");
                    }

                    hm.clear();
                }
                db.commit();
                db.setAutoCommit(true);
            } catch (SQLException ex) {
                try {
                    db.rollback();
                    new DlgException(ex);
                } catch (SQLException ex1) {
                    System.exit(1);
                }
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cmbKategorie;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private com.toedter.calendar.JDateChooser jdcKontrolle;
    private javax.swing.JScrollPane jspBib;
    private javax.swing.JScrollPane jspPlanung;
    private javax.swing.JLabel lblBW;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblBib;
    private javax.swing.JTable tblPlanung;
    private javax.swing.JTextArea txtSituation;
    private javax.swing.JTextField txtStichwort;
    private javax.swing.JTextField txtSuche;
    private javax.swing.JTextArea txtZiele;
    // End of variables declaration//GEN-END:variables
}
