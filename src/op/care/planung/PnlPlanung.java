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

import op.OCSec;
import op.OPDE;
import op.care.CleanablePanel;
import op.care.FrmPflege;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSPrint;
import op.tools.SYSTools;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author tloehr
 */
public class PnlPlanung extends CleanablePanel {

    private JPopupMenu menu;
    //private int mode;
    private FrmPflege parent;
    private String bwkennung;
    private ListSelectionListener lsl;
    private OCSec ocs;
    private ActionListener fileActionListener;

    /**
     * Creates new form PnlPlanung
     */
    public PnlPlanung(FrmPflege parent, String bwkennung) {
        this.parent = parent;
        this.bwkennung = bwkennung;
        ocs = OPDE.getOCSec();
        initComponents();
        initPanel();
    }

    private void initPanel() {
        //SYSTools.setBWLabel(lblBW, bwkennung);
        if (parent.bwlabel == null) {
            SYSTools.setBWLabel(lblBW, this.bwkennung);
            parent.bwlabel = lblBW;
        } else {
            lblBW.setText(parent.bwlabel.getText());
            lblBW.setToolTipText(parent.bwlabel.getToolTipText());
        }

        fileActionListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        };
        reloadTable();
    }

    public void cleanup() {
        SYSTools.unregisterListeners(this);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnVorlage = new javax.swing.JButton();
        btnCopy = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        lblBW = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cbPast = new javax.swing.JCheckBox();
        cbDetails = new javax.swing.JCheckBox();
        jspPlanung = new javax.swing.JScrollPane();
        tblPlanung = new javax.swing.JTable();

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/filenew.png"))); // NOI18N
        btnNew.setText("Neu");
        btnNew.setFocusable(false);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnVorlage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/new_sheet.png"))); // NOI18N
        btnVorlage.setText("Neu aus Vorlage");
        btnVorlage.setFocusable(false);
        btnVorlage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVorlageActionPerformed(evt);
            }
        });
        jToolBar1.add(btnVorlage);

        btnCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/user_active.png"))); // NOI18N
        btnCopy.setText("Übernahme von BW");
        btnCopy.setFocusable(false);
        btnCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCopy);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/printer.png"))); // NOI18N
        btnPrint.setText("Drucken");
        btnPrint.setFocusable(false);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/lock.png"))); // NOI18N
        btnLogout.setText("Abmelden");
        btnLogout.setFocusable(false);
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutbtnLogoutHandler(evt);
            }
        });
        jToolBar1.add(btnLogout);

        lblBW.setFont(new java.awt.Font("Dialog", 1, 18));
        lblBW.setForeground(new java.awt.Color(255, 51, 0));
        lblBW.setText("jLabel3");

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        cbPast.setText("Alte Planungen anzeigen");
        cbPast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPastActionPerformed(evt);
            }
        });

        cbDetails.setText("Detailanzeige");
        cbDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDetailsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cbPast)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDetails)
                                .addContainerGap(503, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cbPast)
                                        .addComponent(cbDetails))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jspPlanung.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jspPlanungComponentResized(evt);
            }
        });

        tblPlanung.setModel(new javax.swing.table.DefaultTableModel(
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
        tblPlanung.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblPlanungMousePressed(evt);
            }
        });
        jspPlanung.setViewportView(tblPlanung);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 860, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(lblBW, javax.swing.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jspPlanung, javax.swing.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblBW, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jspPlanung, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        new DlgPlanung(parent, bwkennung);
        reloadTable();
    }//GEN-LAST:event_btnNewActionPerformed

    private void cbPastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPastActionPerformed
        reloadTable();
    }//GEN-LAST:event_cbPastActionPerformed

    private void tblPlanungMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPlanungMousePressed
        if (!evt.isPopupTrigger()) {
            return;
        }
        Point p = evt.getPoint();
        ListSelectionModel lsm = tblPlanung.getSelectionModel();
        //int col = tblPlanung.columnAtPoint(p);
        int row = tblPlanung.rowAtPoint(p);
        lsm.setSelectionInterval(row, row);
        final long planid = ((Long) tblPlanung.getModel().getValueAt(row, TMPlanungen.COL_PLANID)).longValue();
        boolean abgesetzt = ((Boolean) tblPlanung.getModel().getValueAt(row, TMPlanungen.COL_ABGESETZT)).booleanValue();
        final String stichwort = tblPlanung.getModel().getValueAt(row, TMPlanungen.COL_STICHWORT).toString();
        long numAffectedDFNs = DBHandling.numAffectedDFNs(planid);
        boolean sameUser = tblPlanung.getModel().getValueAt(row, TMPlanungen.COL_ANUKENNUNG).toString().equalsIgnoreCase(OPDE.getLogin().getUser().getUKennung());
        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        /**
         * BEARBEITEN
         * Eine Planung kann GEändert werden (Korrektur)
         * - Wenn es KEINE abgehakten, zugehörigen DFNs gibt.
         * - Wenn sie nicht bereits abgesetzt wurde.
         * - Wenn sie von mir ist.
         *
         */
        boolean bearbeitenMöglich = OPDE.isAdmin() || (!abgesetzt && sameUser && numAffectedDFNs == 0);

        // if (evt.isPopupTrigger()) {

        //final HashMap entry = (HashMap) bwinfo.getAttribute().get(tblPlanung.getSelectedRow());
        SYSTools.unregisterListeners(menu);
        menu = new JPopupMenu();

        // BEARBEITEN
        JMenuItem itemPopupEdit = new JMenuItem("Bearbeiten");
        itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new DlgPlanung(parent, bwkennung, planid, DlgPlanung.EDIT_MODE);
                reloadTable();
            }
        });
        menu.add(itemPopupEdit);

        JMenuItem itemPopupChange = new JMenuItem("Verändern");
        itemPopupChange.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    new DlgPlanung(parent, bwkennung, planid, DlgPlanung.CHANGE_MODE);
                    Thread.sleep(1000);// Sonst, falsche Darstellung in Tabelle
                    reloadTable();
                } catch (InterruptedException ex) {
                    Logger.getLogger(PnlPlanung.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        menu.add(itemPopupChange);

        JMenuItem itemPopupQuit = new JMenuItem("Absetzen");
        itemPopupQuit.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    new DlgAbsetzen(parent, planid, stichwort);
                    Thread.sleep(1000); // Sonst, falsche Darstellung in Tabelle
                    reloadTable();
                } catch (InterruptedException ex) {
                    Logger.getLogger(PnlPlanung.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        menu.add(itemPopupQuit);

        JMenuItem itemPopupDelete = new JMenuItem("Löschen");
        itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (JOptionPane.showConfirmDialog(parent, "Möchten Sie diese Planung und die zugehörigen DFNs wirklich löschen ?",
                        stichwort + " löschen ?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    DBHandling.deletePlanung(planid);
                    reloadTable();
                }
            }
        });
        menu.add(itemPopupDelete);

        JMenuItem itemPopupControl = new JMenuItem("Überprüfung eintragen");
        itemPopupControl.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new DlgPKontrolle(parent, planid);
                reloadTable();
            }
        });
        menu.add(itemPopupControl);

        menu.add(new JSeparator());
        JMenuItem itemPopupInfo = new JMenuItem("Infos anzeigen");
        itemPopupInfo.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JOptionPane.showMessageDialog(parent, "PlanID: " + planid + "\n", "Software-Infos", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        menu.add(itemPopupInfo);
        ocs.setEnabled(this, "itemPopupInfo", itemPopupInfo, true);


        /**
         * Löschen
         * Eine Planung kann gelöscht werden
         * - Wenn es KEINE abgehakten, zugehörigen DFNs gibt. Sonst nicht.
         * - man Eigentümer, mindestens PDL oder ADMIN ist
         *
         */
        boolean löschenMöglich = OPDE.isAdmin() || (!abgesetzt && sameUser && numAffectedDFNs == 0);

        /**
         * Verändern
         * Eine Planung kann VERändert werden
         * - Wenn es abgehakte, zugehörige DFNs gibt. Sonst nicht.
         * - ab Examen aufwärts.
         *
         */
        boolean verändernMöglich = !abgesetzt && numAffectedDFNs > 0;
        /**
         * Absetzen
         * Eine Planung kann abgesetzt werden.
         * - immer
         * - ab Examen aufwärts.
         *
         */
        boolean absetzenMöglich = !abgesetzt && numAffectedDFNs > 0;


        ocs.setEnabled(this, "itemPopupEdit", itemPopupEdit, bearbeitenMöglich);
        ocs.setEnabled(this, "itemPopupChange", itemPopupChange, verändernMöglich);
        ocs.setEnabled(this, "itemPopupDelete", itemPopupDelete, löschenMöglich);
        ocs.setEnabled(this, "itemPopupQuit", itemPopupQuit, absetzenMöglich);
        ocs.setEnabled(this, "itemPopupControl", itemPopupQuit, absetzenMöglich);

        if (singleRowSelected) {
            menu.add(new JSeparator());
            menu.add(op.share.vorgang.DBHandling.getVorgangContextMenu(parent, "Planung", planid, bwkennung, fileActionListener));
        }


        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
//        } else if (bearbeitenMöglich && evt.getClickCount() == 2) { // Bearbeiten, wenn möglich
//            new DlgPlanung(parent, bwkennung, planid, DlgPlanung.EDIT_MODE);
//            reloadTable();
//        }
    }//GEN-LAST:event_tblPlanungMousePressed

    private void jspPlanungComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspPlanungComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();
        // Größe der Text Spalten im DFN ändern.
        // Summe der fixen Spalten  = 175 + ein bisschen
        int textWidth = dim.width - (150 + 65 + 65 + 25);
        TableColumnModel tcm1 = tblPlanung.getColumnModel();
        if (tcm1.getColumnCount() < 4) {
            return;
        }

        tcm1.getColumn(TMPlanungen.COL_KATEGORIE).setPreferredWidth(150);
        tcm1.getColumn(TMPlanungen.COL_BEMERKUNG).setPreferredWidth(textWidth);
        tcm1.getColumn(TMPlanungen.COL_AN).setPreferredWidth(65);
        tcm1.getColumn(TMPlanungen.COL_AB).setPreferredWidth(65);
        tcm1.getColumn(TMPlanungen.COL_KATEGORIE).setHeaderValue("Kategorie");
        tcm1.getColumn(TMPlanungen.COL_BEMERKUNG).setHeaderValue("Bemerkung");
        tcm1.getColumn(TMPlanungen.COL_AN).setHeaderValue("Angesetzt");
        tcm1.getColumn(TMPlanungen.COL_AB).setHeaderValue("Abgesetzt");
    }//GEN-LAST:event_jspPlanungComponentResized

    private void btnVorlageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVorlageActionPerformed
        long template = DlgVorlage.showDialog(parent);
        if (template > 0) {
            new DlgPlanung(parent, bwkennung, template, DlgPlanung.TEMPLATE_MODE);
            reloadTable();
        }
    }//GEN-LAST:event_btnVorlageActionPerformed

    private void cbDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDetailsActionPerformed
        reloadTable();
    }//GEN-LAST:event_cbDetailsActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        try {
            // Create temp file.
            File temp = File.createTempFile("planung", ".html");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            if (cbPast.isSelected()) {
                out.write(DBHandling.getPlanungenAsHTML(bwkennung, SYSConst.DATE_VON_ANFANG_AN, SYSConst.DATE_BIS_AUF_WEITERES));
            } else {
                Date now = SYSCalendar.nowDBDate();
                Date von = new Date(SYSCalendar.erkenneDatum("03.01.2011").getTimeInMillis());

                out.write(DBHandling.getPlanungenAsHTML(bwkennung, von, von));
            }
            out.close();
            SYSPrint.handleFile(parent, temp.getAbsolutePath(), Desktop.Action.OPEN);
        } catch (IOException e) {
        }
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyActionPerformed
        new DlgCopy(parent, bwkennung);
        reloadTable();
    }//GEN-LAST:event_btnCopyActionPerformed

    private void btnLogoutbtnLogoutHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutbtnLogoutHandler
        OPDE.ocmain.lockOC();
    }//GEN-LAST:event_btnLogoutbtnLogoutHandler

    private void reloadTable() {

        tblPlanung.setModel(new TMPlanungen(bwkennung, cbPast.isSelected(), cbDetails.isSelected()));

        tblPlanung.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
//        lsm.addListSelectionListener(lsl);
        jspPlanung.dispatchEvent(new ComponentEvent(jspPlanung, ComponentEvent.COMPONENT_RESIZED));

        tblPlanung.getColumnModel().getColumn(0).setCellRenderer(new RNDPlanungen());
        tblPlanung.getColumnModel().getColumn(1).setCellRenderer(new RNDPlanungen());
        tblPlanung.getColumnModel().getColumn(2).setCellRenderer(new RNDPlanungen());
        tblPlanung.getColumnModel().getColumn(3).setCellRenderer(new RNDPlanungen());


    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCopy;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnVorlage;
    private javax.swing.JCheckBox cbDetails;
    private javax.swing.JCheckBox cbPast;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JScrollPane jspPlanung;
    private javax.swing.JLabel lblBW;
    private javax.swing.JTable tblPlanung;
    // End of variables declaration//GEN-END:variables
}
