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
package op.care.vital;

import com.toedter.calendar.JDateChooser;
import entity.SYSFiles;
import entity.SYSFilesTools;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javax.persistence.Query;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import op.OCSec;
import op.OPDE;
import op.care.CleanablePanel;
import op.care.FrmPflege;
import op.care.berichte.RNDBerichte;
import op.tools.SYSCalendar;
import op.tools.SYSPrint;
import op.tools.SYSTools;

/**
 *
 * @author  tloehr
 */
public class PnlVitalwerte extends CleanablePanel {

    public static final int RR = 0;
    public static final int BZ = 1;
    public static final int TEMP = 2;
    public static final int GROESSE = 3;
    public static final int GEWICHT = 4;
    public static final int ATEM = 5;
    public static final int BRADEN = 6;
    public static final int BILANZ = 7;
    public static final int PULS = 8;
    public static final int QUICK = 9;
    public static final int STUHLGANG = 10;
    public static final int ERBRECHEN = 11;
    public String currentBW;
    public JTabbedPane jtpPflege;
    public JTable tblBW;
    public boolean editMode = false;
    public boolean newMode = false;
    private boolean[] filter = {false, false, false, false, false, false, false, false, false, false, false, false};
    private FrmPflege parent;
    private FocusAdapter fa;
    private boolean initPhase;
    private JPopupMenu menu;
    private OCSec ocs;
    private ActionListener fileActionListener;

    /** Creates new form pnlVitalwerte */
    public PnlVitalwerte(String currentBW, FrmPflege parent, JTabbedPane jtpPflege, JTable tblBW) {
        initPhase = true;
        this.currentBW = currentBW;
        this.jtpPflege = jtpPflege;
        this.tblBW = tblBW;
        this.parent = parent;
        ocs = OPDE.getOCSec();
        initComponents();
        if (parent.bwlabel == null) {
            SYSTools.setBWLabel(lblBW, currentBW);
            parent.bwlabel = lblBW;
        } else {
            lblBW.setText(parent.bwlabel.getText());
            lblBW.setToolTipText(parent.bwlabel.getToolTipText());
        }

        jdcVon.setDate(SYSCalendar.addField(SYSCalendar.today_date(), -2, GregorianCalendar.WEEK_OF_MONTH));
        jdcBis.setDate(SYSCalendar.today_date());
//        jdcDatum.setMaxSelectableDate(SYSCalendar.today_date());
//
//        fa = new FocusAdapter() {
//
//            public void focusLost(java.awt.event.FocusEvent evt) {
//                jdcDatumFocusLost(evt);
//            }
//        };
//
//        jdcDatum.getDateEditor().getUiComponent().addFocusListener(fa);
        fileActionListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        };
        reloadTable();
        initPhase = false;
    }

//    private void jdcDatumFocusLost(java.awt.event.FocusEvent evt) {
//        if (jdcDatum.getDate() == null) {
//            jdcDatum.setDate(SYSCalendar.addField(SYSCalendar.today_date(), -2, GregorianCalendar.WEEK_OF_MONTH));
//        }
//        jdcDatum.firePropertyChange("date", 0, 0);
//    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jspTblVW = new javax.swing.JScrollPane();
        tblVital = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        btnPrint = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        lblBW = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cbRR = new javax.swing.JCheckBox();
        cbPuls = new javax.swing.JCheckBox();
        cbBZ = new javax.swing.JCheckBox();
        cbTemp = new javax.swing.JCheckBox();
        cbGewicht = new javax.swing.JCheckBox();
        cbAtem = new javax.swing.JCheckBox();
        cbGroesse = new javax.swing.JCheckBox();
        cbBilanz = new javax.swing.JCheckBox();
        cbStuhlgang = new javax.swing.JCheckBox();
        cbQuick = new javax.swing.JCheckBox();
        cbIDS = new javax.swing.JCheckBox();
        cbShowEdits = new javax.swing.JCheckBox();
        cbErbrochen = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jdcVon = new com.toedter.calendar.JDateChooser();
        btnHA = new javax.swing.JButton();
        btnToday1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jdcBis = new com.toedter.calendar.JDateChooser();
        btnToday2 = new javax.swing.JButton();

        jspTblVW.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jspTblVWMousePressed(evt);
            }
        });
        jspTblVW.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jspTblVWComponentResized(evt);
            }
        });

        tblVital.setModel(new javax.swing.table.DefaultTableModel(
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
        tblVital.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblVitalMousePressed(evt);
            }
        });
        jspTblVW.setViewportView(tblVital);

        jToolBar1.setFloatable(false);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/fileprint.png"))); // NOI18N
        btnPrint.setText("Drucken");
        btnPrint.setEnabled(false);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/lock.png"))); // NOI18N
        btnLogout.setText("Abmelden");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        jToolBar1.add(btnLogout);

        lblBW.setFont(new java.awt.Font("Dialog", 1, 18));
        lblBW.setForeground(new java.awt.Color(255, 51, 0));
        lblBW.setText("jLabel3");

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        cbRR.setText("Blutdruck / Puls");
        cbRR.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbRR.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbRR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRRActionPerformed(evt);
            }
        });

        cbPuls.setText("Puls");
        cbPuls.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbPuls.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbPuls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPulsActionPerformed(evt);
            }
        });

        cbBZ.setText("Blutzucker");
        cbBZ.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbBZ.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbBZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBZActionPerformed(evt);
            }
        });

        cbTemp.setText("Temperatur");
        cbTemp.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbTemp.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbTemp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTempActionPerformed(evt);
            }
        });

        cbGewicht.setText("Gewicht");
        cbGewicht.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbGewicht.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbGewicht.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbGewichtActionPerformed(evt);
            }
        });

        cbAtem.setText("Atemfrequenz");
        cbAtem.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbAtem.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbAtem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbAtemActionPerformed(evt);
            }
        });

        cbGroesse.setText("Größe");
        cbGroesse.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbGroesse.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbGroesse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbGroesseActionPerformed(evt);
            }
        });

        cbBilanz.setText("Ein-/Ausfuhr");
        cbBilanz.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbBilanz.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbBilanz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBilanzActionPerformed(evt);
            }
        });

        cbStuhlgang.setText("Stuhlgang");
        cbStuhlgang.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbStuhlgang.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbStuhlgang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbStuhlgangActionPerformed(evt);
            }
        });

        cbQuick.setText("Quick");
        cbQuick.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbQuick.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbQuick.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbQuickActionPerformed(evt);
            }
        });

        cbIDS.setText("Werte-Nr. anzeigen");
        cbIDS.setToolTipText("<html>Jeder Bericht hat immer eine eindeutige Nummer.<br/>Diese Nummern werden im Alltag nicht benötigt.<br/>Sollten Sie diese Nummern dennoch sehen wollen<br/>dann schalten Sie diese hier ein.</html>");
        cbIDS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbIDSActionPerformed(evt);
            }
        });

        cbShowEdits.setText("Änderungen anzeigen");
        cbShowEdits.setToolTipText("<html>Damit Änderungen und Löschungen trotzdem nachvollziehbar bleiben<br/>werden sie nur ausgeblendet. Mit diesem Schalter werden diese Änderungen wieder angezeigt.</html>");
        cbShowEdits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbShowEditsActionPerformed(evt);
            }
        });

        cbErbrochen.setText("Erbrochen");
        cbErbrochen.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cbErbrochen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbErbrochenActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbRR)
                    .add(cbBZ)
                    .add(cbPuls))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbAtem)
                    .add(cbGewicht)
                    .add(cbTemp))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbBilanz)
                            .add(cbGroesse))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbErbrochen)
                            .add(cbQuick)))
                    .add(cbStuhlgang))
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbIDS)
                    .add(cbShowEdits))
                .add(39, 39, 39))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbRR)
                    .add(cbGroesse)
                    .add(cbTemp)
                    .add(cbQuick)
                    .add(cbIDS))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbGewicht)
                            .add(cbBilanz)
                            .add(cbPuls)
                            .add(cbErbrochen))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbAtem)
                            .add(cbBZ)
                            .add(cbStuhlgang)))
                    .add(cbShowEdits))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setText("Werte anzeigen vom:");

        jdcVon.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcVonPropertyChange(evt);
            }
        });

        btnHA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/2leftarrow.png"))); // NOI18N
        btnHA.setToolTipText("Erster Eintrag");
        btnHA.setBorder(null);
        btnHA.setBorderPainted(false);
        btnHA.setContentAreaFilled(false);
        btnHA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHAActionPerformed(evt);
            }
        });

        btnToday1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/history.png"))); // NOI18N
        btnToday1.setToolTipText("Heute");
        btnToday1.setBorder(null);
        btnToday1.setBorderPainted(false);
        btnToday1.setContentAreaFilled(false);
        btnToday1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToday1ActionPerformed(evt);
            }
        });

        jLabel1.setText("bis einschließlich:");

        jdcBis.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcBisPropertyChange(evt);
            }
        });

        btnToday2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/history.png"))); // NOI18N
        btnToday2.setToolTipText("Heute");
        btnToday2.setBorder(null);
        btnToday2.setBorderPainted(false);
        btnToday2.setContentAreaFilled(false);
        btnToday2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToday2ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .add(2, 2, 2)
                .add(jdcVon, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 146, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnHA)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnToday1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jdcBis, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnToday2)
                .addContainerGap(67, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jdcBis, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jdcVon, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnToday2)
                    .add(jLabel2)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, btnHA, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, btnToday1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(new java.awt.Component[] {btnHA, btnToday1, btnToday2, jLabel1, jLabel2, jdcBis, jdcVon}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(lblBW, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jspTblVW, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblBW)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jspTblVW, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    private void cbStuhlgangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbStuhlgangActionPerformed
        filter[STUHLGANG] = ((JCheckBox) evt.getSource()).isSelected();
        if (!initPhase) {
            reloadTable();
        }
    }//GEN-LAST:event_cbStuhlgangActionPerformed

    private void cbQuickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbQuickActionPerformed
        filter[QUICK] = ((JCheckBox) evt.getSource()).isSelected();
        if (!initPhase) {
            reloadTable();
        }
    }//GEN-LAST:event_cbQuickActionPerformed

    private void cbPulsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPulsActionPerformed
        filter[PULS] = ((JCheckBox) evt.getSource()).isSelected();
        if (!initPhase) {
            reloadTable();
        }
    }//GEN-LAST:event_cbPulsActionPerformed

    private void cbBilanzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBilanzActionPerformed
        filter[BILANZ] = ((JCheckBox) evt.getSource()).isSelected();
        if (!initPhase) {
            reloadTable();
        }
    }//GEN-LAST:event_cbBilanzActionPerformed

    private void cbAtemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAtemActionPerformed
        filter[ATEM] = ((JCheckBox) evt.getSource()).isSelected();
        if (!initPhase) {
            reloadTable();
        }
    }//GEN-LAST:event_cbAtemActionPerformed

    private void cbGewichtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbGewichtActionPerformed
        if (initPhase) {
            return;
        }
        boolean selected = ((JCheckBox) evt.getSource()).isSelected();
        filter[GEWICHT] = selected;
        if (selected) {
            jdcVon.setDate(SYSCalendar.addField(SYSCalendar.today_date(), -6, GregorianCalendar.MONTH));
            jdcBis.setDate(SYSCalendar.today_date());
        } else {
            jdcVon.setDate(SYSCalendar.addField(SYSCalendar.today_date(), -2, GregorianCalendar.WEEK_OF_MONTH));
            jdcBis.setDate(SYSCalendar.today_date());
        }
        // reloadTable() implizit über ChangeEvent des jdcDatum
    }//GEN-LAST:event_cbGewichtActionPerformed

    private void cbGroesseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbGroesseActionPerformed
        filter[GROESSE] = ((JCheckBox) evt.getSource()).isSelected();
        if (!initPhase) {
            reloadTable();
        }
    }//GEN-LAST:event_cbGroesseActionPerformed

    private void cbTempActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTempActionPerformed
        filter[TEMP] = ((JCheckBox) evt.getSource()).isSelected();
        if (!initPhase) {
            reloadTable();
        }
    }//GEN-LAST:event_cbTempActionPerformed

    private void cbBZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBZActionPerformed
        filter[BZ] = ((JCheckBox) evt.getSource()).isSelected();
        if (!initPhase) {
            reloadTable();
        }
    }//GEN-LAST:event_cbBZActionPerformed

    private void cbRRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRRActionPerformed
        filter[RR] = ((JCheckBox) evt.getSource()).isSelected();
        if (!initPhase) {
            reloadTable();
        }
    }//GEN-LAST:event_cbRRActionPerformed

    private void printWerte(int[] sel) {
        try {
            // Create temp file.
            File temp = File.createTempFile("bwerte", ".html");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));

            TMWerte tm = (TMWerte) tblVital.getModel();
            out.write(op.care.vital.DBHandling.getWerteAsHTML(tm, currentBW, sel));

            out.close();
            SYSPrint.handleFile(parent, temp.getAbsolutePath(), Desktop.Action.OPEN);
        } catch (IOException e) {
        }

    }

    public void cleanup() {
        ListSelectionModel lsmtb2 = tblVital.getSelectionModel();
        SYSTools.unregisterListeners(this);
        jdcVon.cleanup();
        jdcBis.cleanup();
    }

    private void jspTblVWComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspTblVWComponentResized
        JViewport jv = (JViewport) tblVital.getParent();
        JScrollPane jsp = (JScrollPane) jv.getParent();
        Dimension dim = jsp.getSize();
        // Größe der Massnahmen Spalten ändern.
        int width = dim.width - 200; // größe - der fixen spalten
        TableColumnModel tcm1 = tblVital.getColumnModel();

        // Zu Beginn der Applikation steht noch ein standardmodell drin.
        // das hat nur 4 Spalten. solange braucht sich dieser handler nicht
        // damit zu befassen.
        if (tcm1.getColumnCount() < 2) {
            return;
        }

        tcm1.getColumn(0).setPreferredWidth(200);
        tcm1.getColumn(1).setPreferredWidth(width);

        tcm1.getColumn(0).setHeaderValue("Datum / PflegerIn");
        tcm1.getColumn(1).setHeaderValue("Wert");
    }//GEN-LAST:event_jspTblVWComponentResized

    private void reloadTable() {
        ListSelectionModel lsm = tblVital.getSelectionModel();
        TMWerte oldModel = null;

        tblVital.setModel(new TMWerte(jdcVon.getDate(), jdcBis.getDate(), this.filter, this.currentBW, cbShowEdits.isSelected(), cbIDS.isSelected()));
        if (oldModel != null) {
            oldModel.cleanup();
        }
        tblVital.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        //tblTB.setDefaultRenderer(String.class, new TBTableRenderer());

        if (tblVital.getModel().getRowCount() > 0) {
            btnPrint.setEnabled(true);
        } else {
            btnPrint.setEnabled(false);
        }

        jspTblVW.dispatchEvent(new ComponentEvent(jspTblVW, ComponentEvent.COMPONENT_RESIZED));

        // Hier kann die Klasse RNDBerichte verwendet werden. Sie ist einfach in
        // HTML Renderer ohne Zebra Muster. Genau was wir hier wollen.
        tblVital.getColumnModel().getColumn(0).setCellRenderer(new RNDBerichte());
        tblVital.getColumnModel().getColumn(1).setCellRenderer(new RNDBerichte());

    }

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        OPDE.ocmain.lockOC();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void tblVitalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVitalMousePressed

        // #0000043
        Point p = evt.getPoint();
        ListSelectionModel lsm = tblVital.getSelectionModel();

        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        int row = tblVital.rowAtPoint(p);
        if (singleRowSelected) {
            lsm.setSelectionInterval(row, row);
        }

        SYSTools.unregisterListeners(menu);
        menu = new JPopupMenu();

        String[] menus = new String[]{"Atemfrequenz", "Blutdruck / Puls", "Blutzucker", "Ein-/Ausfuhr", "Erbrechen", "Gewicht", "Größe", "Puls", "Quick", "Stuhlgang", "Temperatur"};
        final int[] modes = new int[]{DlgVital.MODE_ATEM, DlgVital.MODE_RR, DlgVital.MODE_BZ, DlgVital.MODE_BILANZ, DlgVital.MODE_ERBRECHEN, DlgVital.MODE_GEWICHT, DlgVital.MODE_GROESSE, DlgVital.MODE_PULS, DlgVital.MODE_QUICK, DlgVital.MODE_STUHLGANG, DlgVital.MODE_TEMP};

        if (evt.isPopupTrigger()) {
            JMenu menuNew = new JMenu("Neu");

            for (int m = 0; m < menus.length; m++) {
                final int mm = m;
                // Neu
                JMenuItem itemPopupNew = new JMenuItem(menus[m]);
                itemPopupNew.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        new DlgVital(parent, new Object[]{modes[mm], currentBW});
                        reloadTable();
                    }
                });
                menuNew.add(itemPopupNew);
            }
            menu.add(menuNew);
        }

        TableModel tm = tblVital.getModel();
        if (tm.getRowCount() > 0 && row > -1) {
            final Object[] o = (Object[]) tm.getValueAt(lsm.getLeadSelectionIndex(), TMWerte.TBL_OBJECT);
            long replacedby = (Long) o[TMWerte.COL_REPLACEDBY];
            final long bwid = (Long) o[TMWerte.COL_BWID];

            boolean alreadyEdited = replacedby != 0;
            //boolean sameUser = (ukennung.compareTo(OPDE.getLogin().getUser().getUKennung()) == 0);

            boolean bearbeitenMöglich = !alreadyEdited && singleRowSelected;

            if (evt.isPopupTrigger()) {

                // KORRIGIEREN
                JMenuItem itemPopupEdit = new JMenuItem("Korrigieren");
                itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        new DlgVital(parent, o);
                        reloadTable();
                    }
                });
                menu.add(itemPopupEdit);

                JMenuItem itemPopupDelete = new JMenuItem("Löschen");
                itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        if (JOptionPane.showConfirmDialog(parent, "Möchten Sie diesen Eintrag wirklich löschen ?",
                                "Wert löschen ?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            HashMap hm = new HashMap();
                            hm.put("ReplacementFor", bwid);
                            hm.put("ReplacedBy", bwid);
                            hm.put("EditBy", OPDE.getLogin().getUser().getUKennung());
                            op.tools.DBHandling.updateRecord("BWerte", hm, "BWID", bwid);
                            reloadTable();
                        }
                    }
                });
                menu.add(itemPopupDelete);

                JMenuItem itemPopupPrint = new JMenuItem("Markierte Werte drucken");
                itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        int[] sel = tblVital.getSelectedRows();
                        printWerte(sel);
                    }
                });
                menu.add(itemPopupPrint);

                ocs.setEnabled(this, "itemPopupEdit", itemPopupEdit, bearbeitenMöglich);
                ocs.setEnabled(this, "itemPopupDelete", itemPopupDelete, bearbeitenMöglich);

                if (!alreadyEdited && singleRowSelected) {
                    menu.add(new JSeparator());
                    // #0000003
                    menu.add(op.share.vorgang.DBHandling.getVorgangContextMenu(parent, "BWerte", bwid, currentBW, fileActionListener));

                    Query query = OPDE.getEM().createNamedQuery("BWerte.findByBwid");
                    query.setParameter("bwid", bwid);
                    entity.BWerte bwert = (entity.BWerte) query.getSingleResult();
                    menu.add(SYSFilesTools.getSYSFilesContextMenu(parent, bwert, fileActionListener));

                    // #0000035
                    //menu.add(SYSFiles.getOPFilesContextMenu(parent, "BWerte", bwid, currentBW, tblVital, true, true, SYSFiles.CODE_BERICHTE, fileActionListener));
                }
            }
        }
        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }//GEN-LAST:event_tblVitalMousePressed

    private void cbIDSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbIDSActionPerformed
        if (this.initPhase) {
            return;
        }
        SYSTools.storeState(this.getClass().getName() + ":cbTBIDS", cbIDS);
        reloadTable();
}//GEN-LAST:event_cbIDSActionPerformed

    private void cbShowEditsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbShowEditsActionPerformed
        if (this.initPhase) {
            return;
        }
        SYSTools.storeState(this.getClass().getName() + ":cbShowEdits", cbShowEdits);
        reloadTable();
}//GEN-LAST:event_cbShowEditsActionPerformed

    private void jdcVonPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcVonPropertyChange
        if (this.initPhase) {
            return;
        }
        if (!evt.getPropertyName().equals("date")) {
            return;
        }
        SYSCalendar.checkJDC((JDateChooser) evt.getSource());
        if (jdcBis.getDate().before(jdcVon.getDate())) {
            jdcVon.setDate(jdcBis.getDate());
        }
        reloadTable();
}//GEN-LAST:event_jdcVonPropertyChange

    private void btnHAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHAActionPerformed
        jdcVon.setDate(DBHandling.firstWert(currentBW));
}//GEN-LAST:event_btnHAActionPerformed

    private void btnToday1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToday1ActionPerformed
        jdcVon.setDate(SYSCalendar.addDate(SYSCalendar.today_date(), -14));
        reloadTable();
}//GEN-LAST:event_btnToday1ActionPerformed

    private void jdcBisPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcBisPropertyChange
        if (this.initPhase) {
            return;
        }
        if (!evt.getPropertyName().equals("date")) {
            return;
        }
        SYSCalendar.checkJDC((JDateChooser) evt.getSource());
        if (jdcBis.getDate().before(jdcVon.getDate())) {
            jdcVon.setDate(jdcBis.getDate());
        }
        reloadTable();
}//GEN-LAST:event_jdcBisPropertyChange

    private void btnToday2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToday2ActionPerformed
        jdcBis.setDate(SYSCalendar.today_date());
        reloadTable();
}//GEN-LAST:event_btnToday2ActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        printWerte(null);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void jspTblVWMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jspTblVWMousePressed
        tblVitalMousePressed(evt);
    }//GEN-LAST:event_jspTblVWMousePressed

    private void cbErbrochenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbErbrochenActionPerformed
        filter[ERBRECHEN] = ((JCheckBox) evt.getSource()).isSelected();
        if (!initPhase) {
            reloadTable();
        }
    }//GEN-LAST:event_cbErbrochenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHA;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnToday1;
    private javax.swing.JButton btnToday2;
    private javax.swing.JCheckBox cbAtem;
    private javax.swing.JCheckBox cbBZ;
    private javax.swing.JCheckBox cbBilanz;
    private javax.swing.JCheckBox cbErbrochen;
    private javax.swing.JCheckBox cbGewicht;
    private javax.swing.JCheckBox cbGroesse;
    private javax.swing.JCheckBox cbIDS;
    private javax.swing.JCheckBox cbPuls;
    private javax.swing.JCheckBox cbQuick;
    private javax.swing.JCheckBox cbRR;
    private javax.swing.JCheckBox cbShowEdits;
    private javax.swing.JCheckBox cbStuhlgang;
    private javax.swing.JCheckBox cbTemp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToolBar jToolBar1;
    private com.toedter.calendar.JDateChooser jdcBis;
    private com.toedter.calendar.JDateChooser jdcVon;
    private javax.swing.JScrollPane jspTblVW;
    private javax.swing.JLabel lblBW;
    private javax.swing.JTable tblVital;
    // End of variables declaration//GEN-END:variables
}
