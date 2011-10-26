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
package op.care.verordnung;

import entity.*;
import op.OCSec;
import op.OPDE;
import op.care.CleanablePanel;
import op.care.FrmPflege;
import op.care.bhp.PnlBHP;
import op.care.med.vorrat.*;
import op.tools.DlgException;
import op.tools.SYSConst;
import op.tools.SYSPrint;
import op.tools.SYSTools;

import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author tloehr
 */
public class PnlVerordnung extends CleanablePanel {

    public static final String internalClassID = "nursingrecords.prescription";
    private String bwkennung;
    private Bewohner bewohner;
    private FrmPflege parent;
    private ListSelectionListener lsl;
    private long currentVerID = 0;
    private long anarztid = 0;
    private long abarztid = 0;
    private long ankhid = 0;
    private long abkhid = 0;
    private long vorid = 0;
    private long bestellid = 0;
    private int currCol;
    private boolean readOnly;
    private OCSec ocs;
    private JPopupMenu menu;
    private boolean editAllowed;
    private boolean changeAllowed;
    private boolean absetzenAllowed;
    private boolean deleteAllowed;
    private boolean attachAllowed;
    private boolean infosAllowed;
    private boolean documentsAllowed;
    private ActionListener fileActionListener;
    private SYSRunningClasses runningClass, blockingClass;

    /**
     * Creates new form PnlVerordnung
     */
    public PnlVerordnung(FrmPflege parent, Bewohner bewohner) {
        this.parent = parent;
        ocs = OPDE.getOCSec();
        this.bewohner = bewohner;
        this.bwkennung = bewohner.getBWKennung();
        initComponents();
        initPanel();
    }

    private void initPanel() {
        SYSRunningClasses[] result = SYSRunningClassesTools.moduleStarted(internalClassID, bwkennung, SYSRunningClasses.STATUS_RW);
        runningClass = result[0];

        if (!runningClass.isRW()) {
            blockingClass = result[1];
            btnLock.setEnabled(true);
            btnLock.setToolTipText("<html><body><h3>Dieser Datensatz ist belegt durch:</h3>"
                    + blockingClass.getLogin().getUser().getNameUndVorname()
                    + "</body></html>");
        } else {
            btnLock.setEnabled(false);
            btnLock.setToolTipText(null);
        }

        BewohnerTools.setBWLabel(lblBW, bewohner);

        // SYSTools.setBWLabel(lblBW, bwkennung);

        ocs.setEnabled(this, "btnNew", btnNew, !readOnly);
        ocs.setEnabled(this, "btnBuchen", btnBuchen, false);
        ocs.setEnabled(this, "btnVorrat", btnVorrat, false);
        ocs.setEnabled(this, "btnPrint", btnPrint, false);
        ocs.setEnabled(this, "btnBestellungen", btnBestellungen, false);

        editAllowed = false;
        changeAllowed = false;
        deleteAllowed = false;
        absetzenAllowed = false;
        attachAllowed = false;
        documentsAllowed = false;
        fileActionListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        };
        loadTable();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jspVerordnung = new javax.swing.JScrollPane();
        tblVerordnung = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        cbOhneMedi = new javax.swing.JCheckBox();
        cbBedarf = new javax.swing.JCheckBox();
        cbMedi = new javax.swing.JCheckBox();
        cbAbgesetzt = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cbRegel = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        lblBW = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnBuchen = new javax.swing.JButton();
        btnVorrat = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnStellplan = new javax.swing.JButton();
        btnBestellungen = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        btnLock = new javax.swing.JButton();

        jspVerordnung.setToolTipText("");
        jspVerordnung.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jspVerordnungComponentResized(evt);
            }
        });

        tblVerordnung.setModel(new javax.swing.table.DefaultTableModel(
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
        tblVerordnung.setToolTipText("");
        tblVerordnung.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblVerordnungMousePressed(evt);
            }
        });
        jspVerordnung.setViewportView(tblVerordnung);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        cbOhneMedi.setSelected(true);
        cbOhneMedi.setText("ohne Medikamente");
        cbOhneMedi.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbOhneMedi.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbOhneMedi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbOhneMediActionPerformed(evt);
            }
        });

        cbBedarf.setSelected(true);
        cbBedarf.setText("bei Bedarf");
        cbBedarf.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbBedarf.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbBedarf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBedarfActionPerformed(evt);
            }
        });

        cbMedi.setSelected(true);
        cbMedi.setText("mit Medikamenten");
        cbMedi.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbMedi.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbMedi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbMediActionPerformed(evt);
            }
        });

        cbAbgesetzt.setText("Abgesetzte");
        cbAbgesetzt.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbAbgesetzt.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbAbgesetzt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbAbgesetztActionPerformed(evt);
            }
        });

        jLabel1.setText("fm - nachts, früh morgens | mo - morgens | mi - mittags");

        jLabel2.setText("nm - nachmittags | ab - abends | sa - nachts, spät abends");

        cbRegel.setSelected(true);
        cbRegel.setText("regelmäßig");
        cbRegel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbRegel.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbRegel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRegelActionPerformed(evt);
            }
        });

        jLabel12.setText("<html>Hinweis: &frac14; = 0,25 | <sup>1</sup>/<sub>3</sub> = 0,33 | &frac12; = 0,5 | &frac34; = 0,75</html>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(cbAbgesetzt)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cbMedi)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cbOhneMedi)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cbBedarf)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cbRegel))
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap(343, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cbAbgesetzt)
                                        .addComponent(cbMedi)
                                        .addComponent(cbOhneMedi)
                                        .addComponent(cbBedarf)
                                        .addComponent(cbRegel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblBW.setFont(new java.awt.Font("Dialog", 1, 18));
        lblBW.setForeground(new java.awt.Color(255, 51, 0));
        lblBW.setText("Nachname, Vorname (*GebDatum, 00 Jahre) [??1]");

        jToolBar1.setFloatable(false);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/filenew.png"))); // NOI18N
        btnNew.setText("Neu");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnBuchen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/shetaddrow.png"))); // NOI18N
        btnBuchen.setText("Buchen");
        btnBuchen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuchenActionPerformed(evt);
            }
        });
        jToolBar1.add(btnBuchen);

        btnVorrat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/sheetremocolums.png"))); // NOI18N
        btnVorrat.setText("Vorrat");
        btnVorrat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVorratActionPerformed(evt);
            }
        });
        jToolBar1.add(btnVorrat);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/fileprint.png"))); // NOI18N
        btnPrint.setText("Drucken");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnStellplan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/fileprint.png"))); // NOI18N
        btnStellplan.setText("Stellplan");
        btnStellplan.setToolTipText("<html>Druckt den Plan zum Tabletten stellen für den <b>aktuellen</b> Tag und für <b>alle</b> BewohnerInnen.</html>");
        btnStellplan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStellplanActionPerformed(evt);
            }
        });
        jToolBar1.add(btnStellplan);

        btnBestellungen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/filefind.png"))); // NOI18N
        btnBestellungen.setText("Bestellungen");
        btnBestellungen.setToolTipText("Bestellungen für alle BewohnerInnen anzeigen.");
        btnBestellungen.setEnabled(false);
        btnBestellungen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBestellungenActionPerformed(evt);
            }
        });
        jToolBar1.add(btnBestellungen);

        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/lock.png"))); // NOI18N
        btnLogout.setText("Abmelden");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutbtnLogoutHandler(evt);
            }
        });
        jToolBar1.add(btnLogout);

        btnLock.setBackground(new java.awt.Color(255, 255, 255));
        btnLock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/encrypted.png"))); // NOI18N
        btnLock.setBorder(null);
        btnLock.setBorderPainted(false);
        btnLock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLockActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 967, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblBW, javax.swing.GroupLayout.DEFAULT_SIZE, 909, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnLock)
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jspVerordnung, javax.swing.GroupLayout.DEFAULT_SIZE, 927, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnLock)
                                        .addComponent(lblBW))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jspVerordnung, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnStellplanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStellplanActionPerformed
        printStellplan();
    }//GEN-LAST:event_btnStellplanActionPerformed

    private void btnBestellungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBestellungenActionPerformed
        new DlgBestellListe(parent);
        loadTable();
    }//GEN-LAST:event_btnBestellungenActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        printVerordnungen(null);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void printVerordnungen(int[] sel) {
        try {
            // Create temp file.
            File temp = File.createTempFile("verordnungen", ".html");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));

            TMVerordnung tm = new TMVerordnung(bwkennung, cbAbgesetzt.isSelected(), cbMedi.isSelected(),
                    cbOhneMedi.isSelected(), cbBedarf.isSelected(), cbRegel.isSelected(), false);
            //TMVerordnung tm = (TMVerordnung) tblVerordnung.getModel();

            String html = SYSTools.htmlUmlautConversion(op.care.verordnung.DBHandling.getVerordnungenAsHTML(tm, bewohner, sel));

            out.write(SYSTools.addHTMLTitle(html, SYSTools.getBWLabel(bwkennung), true));

            out.close();
            SYSPrint.handleFile(parent, temp.getAbsolutePath(), Desktop.Action.OPEN);
        } catch (IOException e) {
            new DlgException(e);
        }

    }

    private void printStellplan() {

        try {
            // Create temp file.
            File temp = File.createTempFile("stellplan", ".html");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            String html = SYSTools.htmlUmlautConversion(VerordnungTools.getStellplanAsHTML(bewohner.getStation().getEinrichtung()));

            out.write(html);

            out.close();
            SYSPrint.handleFile(parent, temp.getAbsolutePath(), Desktop.Action.OPEN);
        } catch (IOException e) {
            new DlgException(e);
        }

    }

    private void btnLogoutbtnLogoutHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutbtnLogoutHandler
        OPDE.ocmain.lockOC();
    }//GEN-LAST:event_btnLogoutbtnLogoutHandler

    private void tblVerordnungMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVerordnungMousePressed
        if (!evt.isPopupTrigger()) {
            return;
        }
        Point p = evt.getPoint();
        final ListSelectionModel lsm = tblVerordnung.getSelectionModel();
        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();
        int row = tblVerordnung.rowAtPoint(p);
        if (lsm.getMinSelectionIndex() == lsm.getMaxSelectionIndex()) {
            lsm.setSelectionInterval(row, row);
        }
        final long bestid = (Long) tblVerordnung.getModel().getValueAt(row, TMVerordnung.COL_BESTID);
        final long dafid = (Long) tblVerordnung.getModel().getValueAt(row, TMVerordnung.COL_DAFID);
        final long nextbest = (Long) tblVerordnung.getModel().getValueAt(row, TMVerordnung.COL_NEXTBEST);
        SYSTools.unregisterListeners(menu);
        menu = new JPopupMenu();

        JMenuItem itemPopupEdit = new JMenuItem("Korrigieren");
        itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new DlgVerordnung(parent, bwkennung, currentVerID, DlgVerordnung.EDIT_MODE);
                reloadTable();
            }
        });

        menu.add(itemPopupEdit);
        ocs.setEnabled(this, "itemPopupEdit", itemPopupEdit, editAllowed);
        //ocs.setEnabled(this, "itemPopupEditText", itemPopupEditText, !readOnly && status > 0 && changeable);
        // -------------------------------------------------
        JMenuItem itemPopupChange = new JMenuItem("Verändern");
        itemPopupChange.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new DlgVerordnung(parent, bwkennung, currentVerID, DlgVerordnung.CHANGE_MODE);
                loadTable();
            }
        });
        menu.add(itemPopupChange);
        ocs.setEnabled(this, "itemPopupChange", itemPopupChange, changeAllowed);
        // -------------------------------------------------
        JMenuItem itemPopupQuit = new JMenuItem("Absetzen");
        itemPopupQuit.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new DlgAbsetzen(parent, tblVerordnung.getModel().getValueAt(tblVerordnung.getSelectedRow(), TMVerordnung.COL_MSSN).toString(), currentVerID);
                reloadTable();
            }
        });
        menu.add(itemPopupQuit);
        ocs.setEnabled(this, "itemPopupQuit", itemPopupQuit, absetzenAllowed);
        // -------------------------------------------------
        JMenuItem itemPopupDelete = new JMenuItem("Löschen");
        itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (JOptionPane.showConfirmDialog(parent, "Soll die Verordnung wirklich gelöscht werden.",
                        "Verordnung löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    op.care.verordnung.DBHandling.deleteVerordnung(currentVerID);
                    loadTable();
                }
            }
        });
        menu.add(itemPopupDelete);
        ocs.setEnabled(this, "itemPopupDelete", itemPopupDelete, deleteAllowed);
        // -------------------------------------------------
//        JMenuItem itemPopupBest = new JMenuItem("Nachbestätigung durch Arzt");
//        itemPopupBest.addActionListener(new java.awt.event.ActionListener() {
//
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                new DlgBestaetigung(parent, tblVerordnung.getModel().getValueAt(tblVerordnung.getSelectedRow(), TMVerordnung.COL_MSSN).toString(), currentVerID);
//                loadTable();
//            }
//        });
//        menu.add(itemPopupBest);
        //ocs.setEnabled(this, "itemPopupBest", itemPopupBest, bestaetigungAllowed);
        // -------------------------------------------------
//        JMenuItem itemPopupOrder = new JMenuItem("Nachbestellen");
//        itemPopupOrder.addActionListener(new java.awt.event.ActionListener() {
//
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                HashMap hm = new HashMap();
//                hm.put("VorID", vorid);
//                hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//                hm.put("Text", "");
//                hm.put("Datum", "!NOW!");
//                hm.put("ArztID", anarztid);
//                hm.put("Abschluss", "!BAW!");
//                DBHandling.insertRecord("MPBestellung", hm);
//                hm.clear();
//                loadTable();
//            }
//        });
//        menu.add(itemPopupOrder);
//        ocs.setEnabled(this, "itemPopupOrder", itemPopupOrder, orderAllowed);
        // -------------------------------------------------
//        JMenuItem itemPopupOrderDetail = new JMenuItem("Nachbestellen mit Detaileingabe");
//        itemPopupOrderDetail.addActionListener(new java.awt.event.ActionListener() {
//
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                new DlgBestellung(parent, anarztid, vorid);
//                loadTable();
//            }
//        });
//        menu.add(itemPopupOrderDetail);
//        ocs.setEnabled(parent, "itemPopupOrder", itemPopupOrderDetail, orderAllowed);
//        // -------------------------------------------------
//        JMenuItem itemPopupDelOrder = new JMenuItem("Bestellung löschen");
//        itemPopupDelOrder.addActionListener(new java.awt.event.ActionListener() {
//
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                DBHandling.deleteRecords("MPBestellung", "BestellID", bestellid);
//                loadTable();
//            }
//        });
//        menu.add(itemPopupDelOrder);
//        ocs.setEnabled(parent, "itemPopupDelOrder", itemPopupDelOrder, delOrderAllowed);

        // -------------------------------------------------
        if (dafid > 0) {
            menu.add(new JSeparator());

            JMenuItem itemPopupCloseBestand = new JMenuItem("Bestand abschließen");
            itemPopupCloseBestand.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try {
                        new DlgBestandAbschliessen(parent, bestid);
                        Thread.sleep(1000);
                        reloadTable();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PnlBHP.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            menu.add(itemPopupCloseBestand);
            ocs.setEnabled(this, "itemPopupCloseBestand", itemPopupCloseBestand, !readOnly && nextbest == 0 && bestid > 0);

            JMenuItem itemPopupOpenBestand = new JMenuItem("Bestand anbrechen");
            itemPopupOpenBestand.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try {
                        new DlgBestandAnbrechen(parent, dafid, bwkennung);
                        Thread.sleep(1000);
                        reloadTable();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PnlBHP.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            menu.add(itemPopupOpenBestand);
            ocs.setEnabled(this, "itemPopupOpenBestand", itemPopupOpenBestand, !readOnly && bestid == 0);
        }
        menu.add(new JSeparator());

        JMenuItem itemPopupPrint = new JMenuItem("Markierte Verordnungen drucken");
        itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int[] sel = tblVerordnung.getSelectedRows();
                printVerordnungen(sel);
            }
        });
        menu.add(itemPopupPrint);

        if (singleRowSelected) {
            menu.add(new JSeparator());

            menu.add(op.share.vorgang.DBHandling.getVorgangContextMenu(parent, "BHPVerordnung", currentVerID, bwkennung, fileActionListener));
            Query query = OPDE.getEM().createNamedQuery("Verordnung.findByVerID");
            query.setParameter("verID", currentVerID);
            entity.Verordnung verordnung = (entity.Verordnung) query.getSingleResult();
            menu.add(SYSFilesTools.getSYSFilesContextMenu(parent, verordnung, fileActionListener));
        }

        menu.add(new JSeparator());
        JMenuItem itemPopupInfo = new JMenuItem("Infos anzeigen");
        itemPopupInfo.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                long bestid = op.care.med.DBHandling.getBestandImAnbruch(vorid);
                long dafid = 0;
                double apv = 0d;
                double apvBest = 0d;
                if (bestid > 0) {
                    dafid = ((BigInteger) op.tools.DBRetrieve.getSingleValue("MPBestand", "DafID", "BestID", bestid)).longValue();
                    apv = op.care.med.DBHandling.getAPV(dafid, bwkennung);
                    apvBest = ((BigDecimal) op.tools.DBRetrieve.getSingleValue("MPBestand", "APV", "BestID", bestid)).doubleValue();
                }
                JOptionPane.showMessageDialog(parent, "VerID: " + currentVerID + "\nVorID: " + vorid + "\nDafID: " + dafid + "\nAPV: " + apv + "\nAPV (Bestand): " + apvBest, "Software-Infos", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        ocs.setEnabled(this, "itemPopupInfo", itemPopupInfo, infosAllowed);
        menu.add(itemPopupInfo);


        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }//GEN-LAST:event_tblVerordnungMousePressed

    private void btnVorratActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVorratActionPerformed
        new DlgVorrat(this.parent, bwkennung);
        loadTable();
    }//GEN-LAST:event_btnVorratActionPerformed

    private void btnLockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLockActionPerformed
        initPanel();
    }//GEN-LAST:event_btnLockActionPerformed

    private void cbRegelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRegelActionPerformed
        if (!cbBedarf.isSelected() && !cbRegel.isSelected()) {
            cbBedarf.doClick();
        } else {
            loadTable();
        }
    }//GEN-LAST:event_cbRegelActionPerformed

    private void cbBedarfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBedarfActionPerformed
        if (!cbBedarf.isSelected() && !cbRegel.isSelected()) {
            cbRegel.doClick();
        } else {
            loadTable();
        }
    }//GEN-LAST:event_cbBedarfActionPerformed

    private void cbOhneMediActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbOhneMediActionPerformed
        if (!cbOhneMedi.isSelected() && !cbMedi.isSelected()) {
            cbMedi.doClick();
        } else {
            loadTable();
        }
    }//GEN-LAST:event_cbOhneMediActionPerformed

    private void cbMediActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMediActionPerformed
        if (!cbOhneMedi.isSelected() && !cbMedi.isSelected()) {
            cbOhneMedi.doClick();
        } else {
            loadTable();
        }
    }//GEN-LAST:event_cbMediActionPerformed

    private void cbAbgesetztActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAbgesetztActionPerformed
        loadTable();
    }//GEN-LAST:event_cbAbgesetztActionPerformed

    private void btnBuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuchenActionPerformed
        new DlgBestand(parent, bwkennung);
        loadTable();
    }//GEN-LAST:event_btnBuchenActionPerformed

    private void jspVerordnungComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspVerordnungComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();
        // Größe der Text Spalten im DFN ändern.
        // Summe der fixen Spalten  = 175 + ein bisschen
        int textWidth = dim.width - (150 + 150 + 85 + 85 + 25);
        TableColumnModel tcm1 = tblVerordnung.getColumnModel();
        if (tcm1.getColumnCount() < 4) {
            return;
        }

        tcm1.getColumn(TMVerordnung.COL_MSSN).setPreferredWidth(150);
        tcm1.getColumn(TMVerordnung.COL_Dosis).setPreferredWidth(textWidth);
        tcm1.getColumn(TMVerordnung.COL_Hinweis).setPreferredWidth(150);
        tcm1.getColumn(TMVerordnung.COL_AN).setPreferredWidth(85);
        tcm1.getColumn(TMVerordnung.COL_AB).setPreferredWidth(85);
        tcm1.getColumn(0).setHeaderValue("Medikament / Massnahme");
        tcm1.getColumn(1).setHeaderValue("Dosierung / Häufigkeit");
        tcm1.getColumn(2).setHeaderValue("Hinweise");
        tcm1.getColumn(3).setHeaderValue("Angesetzt");
        tcm1.getColumn(4).setHeaderValue("Abgesetzt");
    }//GEN-LAST:event_jspVerordnungComponentResized

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        new DlgVerordnung(parent, bwkennung);
        loadTable();
    }//GEN-LAST:event_btnNewActionPerformed

    public void cleanup() {
        ListSelectionModel lsmtb = tblVerordnung.getSelectionModel();
        if (lsl != null) {
            lsmtb.removeListSelectionListener(lsl);
        }
        SYSTools.unregisterListeners(this);
        SYSRunningClassesTools.moduleEnded(runningClass);
    }

    private void loadTable() {
        ListSelectionModel lsm = tblVerordnung.getSelectionModel();
        if (lsl != null) {
            lsm.removeListSelectionListener(lsl);
        }
        lsl = new HandleSelections();

        tblVerordnung.setModel(new TMVerordnung(bwkennung, cbAbgesetzt.isSelected(), cbMedi.isSelected(),
                cbOhneMedi.isSelected(), cbBedarf.isSelected(), cbRegel.isSelected(), true));
        tblVerordnung.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lsm.addListSelectionListener(lsl);

        ocs.setEnabled(this, "btnBuchen", btnBuchen, !readOnly);// && tblVerordnung.getModel().getRowCount() > 0);
        ocs.setEnabled(this, "btnVorrat", btnVorrat, !readOnly);// && tblVerordnung.getModel().getRowCount() > 0);
        ocs.setEnabled(this, "btnPrint", btnPrint, !readOnly && tblVerordnung.getModel().getRowCount() > 0);
        ocs.setEnabled(this, "btnBestellungen", btnBestellungen, false); //!readOnly && tblVerordnung.getModel().getRowCount() > 0);

        jspVerordnung.dispatchEvent(new ComponentEvent(jspVerordnung, ComponentEvent.COMPONENT_RESIZED));
        tblVerordnung.getColumnModel().getColumn(0).setCellRenderer(new RNDVerordnung());
        tblVerordnung.getColumnModel().getColumn(1).setCellRenderer(new RNDVerordnung());
        tblVerordnung.getColumnModel().getColumn(2).setCellRenderer(new RNDVerordnung());
        tblVerordnung.getColumnModel().getColumn(3).setCellRenderer(new RNDVerordnung());
        tblVerordnung.getColumnModel().getColumn(4).setCellRenderer(new RNDVerordnung());
    }

    private void reloadTable() {
        TMVerordnung tm = (TMVerordnung) tblVerordnung.getModel();
        tm.reload(currCol, 0);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBestellungen;
    private javax.swing.JButton btnBuchen;
    private javax.swing.JButton btnLock;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnStellplan;
    private javax.swing.JButton btnVorrat;
    private javax.swing.JCheckBox cbAbgesetzt;
    private javax.swing.JCheckBox cbBedarf;
    private javax.swing.JCheckBox cbMedi;
    private javax.swing.JCheckBox cbOhneMedi;
    private javax.swing.JCheckBox cbRegel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JScrollPane jspVerordnung;
    private javax.swing.JLabel lblBW;
    private javax.swing.JTable tblVerordnung;
    // End of variables declaration//GEN-END:variables

    class HandleSelections implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent lse) {
            // Erst reagieren wenn der Auswahl-Vorgang abgeschlossen ist.
            TableModel tm = tblVerordnung.getModel();
            if (tm.getRowCount() <= 0) {
                return;
            }
            if (!lse.getValueIsAdjusting()) {
                DefaultListSelectionModel lsm = (DefaultListSelectionModel) lse.getSource();
                boolean singleSelection = lsm.getMinSelectionIndex() == lsm.getMaxSelectionIndex();
                currCol = lsm.getLeadSelectionIndex();
                if (lsm.isSelectionEmpty() || !singleSelection) {
                    currentVerID = 0;
                    editAllowed = false;
                    changeAllowed = false;
                    deleteAllowed = false;
                    absetzenAllowed = false;
                    attachAllowed = !singleSelection;
                    documentsAllowed = false;
                    infosAllowed = false;
                } else {
                    currentVerID = ((Long) tm.getValueAt(lsm.getLeadSelectionIndex(), TMVerordnung.COL_VERID)).longValue();
                    anarztid = ((Long) tm.getValueAt(lsm.getLeadSelectionIndex(), TMVerordnung.COL_ANARZTID)).longValue();
                    abarztid = ((Long) tm.getValueAt(lsm.getLeadSelectionIndex(), TMVerordnung.COL_ABARZTID)).longValue();
                    ankhid = ((Long) tm.getValueAt(lsm.getLeadSelectionIndex(), TMVerordnung.COL_ANKHID)).longValue();
                    abkhid = ((Long) tm.getValueAt(lsm.getLeadSelectionIndex(), TMVerordnung.COL_ABKHID)).longValue();
                    vorid = ((Long) tm.getValueAt(lsm.getLeadSelectionIndex(), TMVerordnung.COL_VORID)).longValue();
                    //bestellid = ((Long) tm.getValueAt(lsm.getLeadSelectionIndex(), TMVerordnung.COL_BESTELLID)).longValue();
                    boolean abgesetzt = ((Boolean) tm.getValueAt(lsm.getLeadSelectionIndex(), TMVerordnung.COL_ABGESETZT)).booleanValue();
                    // Korrektur nur erlauben, wenn es noch keine abgehakten BHPs dazu gibt.
                    long num = op.care.verordnung.DBRetrieve.numAffectedBHPs(currentVerID);
                    Timestamp abdatum = (Timestamp) tm.getValueAt(lsm.getLeadSelectionIndex(), TMVerordnung.COL_ABDATUM);
                    long sitid = ((Long) tm.getValueAt(lsm.getLeadSelectionIndex(), TMVerordnung.COL_SITID)).longValue();

//                    bestaetigungAllowed = !readOnly && ((abgesetzt && abkhid != 0 && abarztid == 0)
//                            || (!abgesetzt && ankhid != 0 && anarztid == 0));
                    editAllowed = !readOnly && num == 0;
                    changeAllowed = !readOnly && sitid == 0 && abdatum.equals(SYSConst.TS_BIS_AUF_WEITERES) && num > 0;
                    deleteAllowed = !readOnly && num == 0;
                    absetzenAllowed = !readOnly && !abgesetzt;
                    attachAllowed = !readOnly;
                    //orderAllowed = !readOnly && !abgesetzt && vorid > 0 && anarztid > 0 && bestellid == 0;
                    //delOrderAllowed = !readOnly && !abgesetzt && vorid > 0 && bestellid > 0;
                    documentsAllowed = !readOnly;
                    infosAllowed = true;

//
//                    ocs.setEnabled(this, "btnEdit", btnEdit, );
//                    ocs.setEnabled(this, "btnChange", btnChange, );
//                    ocs.setEnabled(this, "btnAbsetzen", btnAbsetzen, !readOnly && num  > 0);
//                    ocs.setEnabled(this, "btnDelete", btnDelete, !readOnly && num > 0);
//                    ocs.setEnabled(this, "btnAttach", btnAttach, !readOnly);
                }
            }
        }
    }
}
