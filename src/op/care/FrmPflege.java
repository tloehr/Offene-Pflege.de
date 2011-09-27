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
package op.care;

import entity.Bewohner;
import entity.Stationen;
import entity.StationenTools;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javax.persistence.Query;
import javax.swing.JComponent;
import javax.swing.JLabel;
import op.OPDE;
import op.care.berichte.PnlBerichte;
import op.care.bhp.PnlBHP;
import op.care.schichtleitung.PnlSchichtleitung;
import op.care.verordnung.PnlVerordnung;
import op.care.dfn.PnlDFN;
import op.care.planung.PnlPlanung;
import op.care.sysfiles.PnlFiles;
import op.care.uebergabe.PnlUebergabe;
import op.care.vital.PnlVitalwerte;
import op.threads.HeapStat;
import op.tools.DBRetrieve;
import op.tools.InternalClassACL;
import op.tools.SYSTools;

/**
 *
 * @author  __USER__
 */
public class FrmPflege extends javax.swing.JFrame {

    public static final String internalClassID = "nursingrecords.main";
    public static final int MAINTAB_UEBERSICHT = 0;
    public static final int UE_UEBERGABE = 0;
    public static final int UE_SCHICHTLEITUNG = 1;
    public static final int MAINTAB_BW = 1;
    public static final int TAB_UEBERSICHT = 0;
    public static final int TAB_PB = 1;
    public static final int TAB_DFN = 2;
    public static final int TAB_BHP = 3;
    public static final int TAB_VITAL = 4;
    public static final int TAB_VERORDNUNG = 5;
    public static final int TAB_INFO = 6;
    public static final int TAB_PPLANUNG = 7;
    public static final int TAB_VORGANG = 8;
    public static final int TAB_FILES = 9;
    public static final int SOZ_BERICHTE = 0;
    public static final int SOZ_NY = 1;
    private String currentBW = "";
    private Bewohner bewohner = null;
    public String currentTBDatum;
    public String currentTBUhrzeit;
    public String currentTBText;
    public long currentAnamID;
    public HashMap hmC;
    public HashMap fragen;
    public HashMap dfnplanung;
    private HandleBWSelections lslBW;
    private boolean initPhase;
    private HeapStat hs;
    public JLabel bwlabel;

    /**
     * Creates new form FrmPflege
     */
    public FrmPflege() {
        initPhase = true;
        initComponents();
        bwlabel = null;
        setTitle(SYSTools.getWindowTitle("Pflegedokumentation"));
        this.setVisible(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        lblServer.setText(OPDE.url);
        lblUser.setText("Benutzer: " + DBRetrieve.getUsername(OPDE.getLogin().getUser().getUKennung()));

        StationenTools.setComboBox(cmbStation);

        applySecurity();

        hs = new HeapStat(pbHeap, lblHeap);
        hs.start();
        reloadTable();
        initPhase = false;
        jtpMain.setSelectedIndex(MAINTAB_BW);
        jtpPflegeakte.setSelectedIndex(TAB_UEBERSICHT);

        // Zugriffe
        jtpPflegeakte.setEnabledAt(TAB_PB, OPDE.getInternalClasses().userHasAccessLevelForThisClass(PnlBerichte.internalClassID, InternalClassACL.EXECUTE));
        jtpPflegeakte.setEnabledAt(TAB_FILES, OPDE.getInternalClasses().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.EXECUTE));

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgDFN = new javax.swing.ButtonGroup();
        bgKorrekturen = new javax.swing.ButtonGroup();
        pnlStatus = new javax.swing.JPanel();
        lblServer = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        pbHeap = new javax.swing.JProgressBar();
        lblHeap = new javax.swing.JLabel();
        jtpMain = new javax.swing.JTabbedPane();
        jtpUebersicht = new javax.swing.JTabbedPane();
        pnlUebergabe = new javax.swing.JPanel();
        pnlSchichtleitung = new javax.swing.JPanel();
        jsplBW = new javax.swing.JSplitPane();
        jtpPflegeakte = new javax.swing.JTabbedPane();
        pnlUeber = new javax.swing.JPanel();
        pnlTB = new javax.swing.JPanel();
        pnlDFN = new javax.swing.JPanel();
        pnlBHP = new javax.swing.JPanel();
        pnlVitalDummy = new javax.swing.JPanel();
        pnlVer = new javax.swing.JPanel();
        pnlInfo = new javax.swing.JPanel();
        pnlPPlanung = new javax.swing.JPanel();
        pnlVorgang = new javax.swing.JPanel();
        pnlFiles = new javax.swing.JPanel();
        pnlBW = new javax.swing.JPanel();
        cmbStation = new javax.swing.JComboBox();
        jspBW = new javax.swing.JScrollPane();
        tblBW = new javax.swing.JTable();
        cbArchiv = new javax.swing.JCheckBox();
        btnRefresh = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        btnVerlegung = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("OpenCare Prototype C");

        pnlStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblServer.setText("jLabel6");

        lblUser.setText("jLabel6");

        lblHeap.setText("jLabel1");

        org.jdesktop.layout.GroupLayout pnlStatusLayout = new org.jdesktop.layout.GroupLayout(pnlStatus);
        pnlStatus.setLayout(pnlStatusLayout);
        pnlStatusLayout.setHorizontalGroup(
            pnlStatusLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlStatusLayout.createSequentialGroup()
                .add(lblServer)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pbHeap, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblHeap)
                .add(18, 18, 18)
                .add(lblUser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlStatusLayout.setVerticalGroup(
            pnlStatusLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlStatusLayout.createSequentialGroup()
                .add(pnlStatusLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblServer)
                    .add(lblHeap)
                    .add(lblUser))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pbHeap, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        jtpMain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jtpMainStateChanged(evt);
            }
        });

        jtpUebersicht.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jtpUebersicht.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jtpUebersichtStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlUebergabeLayout = new org.jdesktop.layout.GroupLayout(pnlUebergabe);
        pnlUebergabe.setLayout(pnlUebergabeLayout);
        pnlUebergabeLayout.setHorizontalGroup(
            pnlUebergabeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 809, Short.MAX_VALUE)
        );
        pnlUebergabeLayout.setVerticalGroup(
            pnlUebergabeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 469, Short.MAX_VALUE)
        );

        jtpUebersicht.addTab("Übergabe", pnlUebergabe);

        org.jdesktop.layout.GroupLayout pnlSchichtleitungLayout = new org.jdesktop.layout.GroupLayout(pnlSchichtleitung);
        pnlSchichtleitung.setLayout(pnlSchichtleitungLayout);
        pnlSchichtleitungLayout.setHorizontalGroup(
            pnlSchichtleitungLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 809, Short.MAX_VALUE)
        );
        pnlSchichtleitungLayout.setVerticalGroup(
            pnlSchichtleitungLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 469, Short.MAX_VALUE)
        );

        jtpUebersicht.addTab("Schichtleitung", pnlSchichtleitung);

        jtpMain.addTab("Übersicht", jtpUebersicht);

        jsplBW.setDividerLocation(250);

        jtpPflegeakte.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jtpPflegeakte.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jtpPflegeakteStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlUeberLayout = new org.jdesktop.layout.GroupLayout(pnlUeber);
        pnlUeber.setLayout(pnlUeberLayout);
        pnlUeberLayout.setHorizontalGroup(
            pnlUeberLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 548, Short.MAX_VALUE)
        );
        pnlUeberLayout.setVerticalGroup(
            pnlUeberLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 465, Short.MAX_VALUE)
        );

        jtpPflegeakte.addTab("Übersicht", pnlUeber);

        org.jdesktop.layout.GroupLayout pnlTBLayout = new org.jdesktop.layout.GroupLayout(pnlTB);
        pnlTB.setLayout(pnlTBLayout);
        pnlTBLayout.setHorizontalGroup(
            pnlTBLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 548, Short.MAX_VALUE)
        );
        pnlTBLayout.setVerticalGroup(
            pnlTBLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 465, Short.MAX_VALUE)
        );

        jtpPflegeakte.addTab("Pflegeberichte", pnlTB);
        pnlTB.getAccessibleContext().setAccessibleParent(jtpPflegeakte);

        org.jdesktop.layout.GroupLayout pnlDFNLayout = new org.jdesktop.layout.GroupLayout(pnlDFN);
        pnlDFN.setLayout(pnlDFNLayout);
        pnlDFNLayout.setHorizontalGroup(
            pnlDFNLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 548, Short.MAX_VALUE)
        );
        pnlDFNLayout.setVerticalGroup(
            pnlDFNLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 465, Short.MAX_VALUE)
        );

        jtpPflegeakte.addTab("DFN", pnlDFN);

        org.jdesktop.layout.GroupLayout pnlBHPLayout = new org.jdesktop.layout.GroupLayout(pnlBHP);
        pnlBHP.setLayout(pnlBHPLayout);
        pnlBHPLayout.setHorizontalGroup(
            pnlBHPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 548, Short.MAX_VALUE)
        );
        pnlBHPLayout.setVerticalGroup(
            pnlBHPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 465, Short.MAX_VALUE)
        );

        jtpPflegeakte.addTab("BHP", pnlBHP);

        org.jdesktop.layout.GroupLayout pnlVitalDummyLayout = new org.jdesktop.layout.GroupLayout(pnlVitalDummy);
        pnlVitalDummy.setLayout(pnlVitalDummyLayout);
        pnlVitalDummyLayout.setHorizontalGroup(
            pnlVitalDummyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 548, Short.MAX_VALUE)
        );
        pnlVitalDummyLayout.setVerticalGroup(
            pnlVitalDummyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 465, Short.MAX_VALUE)
        );

        jtpPflegeakte.addTab("Werte", pnlVitalDummy);

        org.jdesktop.layout.GroupLayout pnlVerLayout = new org.jdesktop.layout.GroupLayout(pnlVer);
        pnlVer.setLayout(pnlVerLayout);
        pnlVerLayout.setHorizontalGroup(
            pnlVerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 548, Short.MAX_VALUE)
        );
        pnlVerLayout.setVerticalGroup(
            pnlVerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 465, Short.MAX_VALUE)
        );

        jtpPflegeakte.addTab("Verordnungen", pnlVer);

        org.jdesktop.layout.GroupLayout pnlInfoLayout = new org.jdesktop.layout.GroupLayout(pnlInfo);
        pnlInfo.setLayout(pnlInfoLayout);
        pnlInfoLayout.setHorizontalGroup(
            pnlInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 548, Short.MAX_VALUE)
        );
        pnlInfoLayout.setVerticalGroup(
            pnlInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 465, Short.MAX_VALUE)
        );

        jtpPflegeakte.addTab("Informationen", pnlInfo);

        org.jdesktop.layout.GroupLayout pnlPPlanungLayout = new org.jdesktop.layout.GroupLayout(pnlPPlanung);
        pnlPPlanung.setLayout(pnlPPlanungLayout);
        pnlPPlanungLayout.setHorizontalGroup(
            pnlPPlanungLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 548, Short.MAX_VALUE)
        );
        pnlPPlanungLayout.setVerticalGroup(
            pnlPPlanungLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 465, Short.MAX_VALUE)
        );

        jtpPflegeakte.addTab("Planung", pnlPPlanung);

        org.jdesktop.layout.GroupLayout pnlVorgangLayout = new org.jdesktop.layout.GroupLayout(pnlVorgang);
        pnlVorgang.setLayout(pnlVorgangLayout);
        pnlVorgangLayout.setHorizontalGroup(
            pnlVorgangLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 548, Short.MAX_VALUE)
        );
        pnlVorgangLayout.setVerticalGroup(
            pnlVorgangLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 465, Short.MAX_VALUE)
        );

        jtpPflegeakte.addTab("Vorgänge", pnlVorgang);

        org.jdesktop.layout.GroupLayout pnlFilesLayout = new org.jdesktop.layout.GroupLayout(pnlFiles);
        pnlFiles.setLayout(pnlFilesLayout);
        pnlFilesLayout.setHorizontalGroup(
            pnlFilesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 548, Short.MAX_VALUE)
        );
        pnlFilesLayout.setVerticalGroup(
            pnlFilesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 465, Short.MAX_VALUE)
        );

        jtpPflegeakte.addTab("Dokumente", pnlFiles);

        jsplBW.setRightComponent(jtpPflegeakte);

        cmbStation.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alle", "Station 1", "Station 2", "Herchen" }));
        cmbStation.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbStationItemStateChanged(evt);
            }
        });

        jspBW.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jspBWComponentResized(evt);
            }
        });

        tblBW.setModel(new javax.swing.table.DefaultTableModel(
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
        jspBW.setViewportView(tblBW);

        cbArchiv.setText("Archiv");
        cbArchiv.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbArchiv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbArchivActionPerformed(evt);
            }
        });

        btnRefresh.setBackground(java.awt.Color.white);
        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/reload.png"))); // NOI18N
        btnRefresh.setBorder(null);
        btnRefresh.setBorderPainted(false);
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnVerlegung.setForeground(new java.awt.Color(255, 51, 0));
        btnVerlegung.setText("Verlegungsbericht");
        btnVerlegung.setToolTipText("");
        btnVerlegung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerlegungActionPerformed(evt);
            }
        });
        jToolBar1.add(btnVerlegung);

        org.jdesktop.layout.GroupLayout pnlBWLayout = new org.jdesktop.layout.GroupLayout(pnlBW);
        pnlBW.setLayout(pnlBWLayout);
        pnlBWLayout.setHorizontalGroup(
            pnlBWLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlBWLayout.createSequentialGroup()
                .addContainerGap()
                .add(cbArchiv)
                .addContainerGap(167, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlBWLayout.createSequentialGroup()
                .addContainerGap()
                .add(cmbStation, 0, 202, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnRefresh)
                .addContainerGap())
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jspBW, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
        );
        pnlBWLayout.setVerticalGroup(
            pnlBWLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlBWLayout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jspBW, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlBWLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(btnRefresh)
                    .add(cmbStation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(8, 8, 8)
                .add(cbArchiv))
        );

        jsplBW.setLeftComponent(pnlBW);

        jtpMain.addTab("Pflegeakte", jsplBW);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jtpMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 851, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jtpMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jtpMain.getAccessibleContext().setAccessibleName("Übergabe");

        setSize(new java.awt.Dimension(851, 623));
    }// </editor-fold>//GEN-END:initComponents

    private void jtpMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpMainStateChanged
        reloadDisplay();
}//GEN-LAST:event_jtpMainStateChanged

    private void btnVerlegungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerlegungActionPerformed
        print(op.care.DBHandling.getUeberleitung(bewohner, true, true, true, false, false, true, true, true, true, false));
}//GEN-LAST:event_btnVerlegungActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        reloadTable();
}//GEN-LAST:event_btnRefreshActionPerformed

    private void cbArchivActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbArchivActionPerformed
        if (!initPhase) {
            reloadTable();
        }
}//GEN-LAST:event_cbArchivActionPerformed

    private void jspBWComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspBWComponentResized
        formatBWTable();
}//GEN-LAST:event_jspBWComponentResized

    private void cmbStationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbStationItemStateChanged
        if (!initPhase) {
            initPhase = true;
            cbArchiv.setSelected(false);
            initPhase = false;
            reloadTable();
        }
}//GEN-LAST:event_cmbStationItemStateChanged

    private void jtpPflegeakteStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpPflegeakteStateChanged
        reloadDisplay();
}//GEN-LAST:event_jtpPflegeakteStateChanged

    private void jtpUebersichtStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpUebersichtStateChanged
        reloadDisplay();
}//GEN-LAST:event_jtpUebersichtStateChanged

    public void applySecurity() {
        if (OPDE.isAdmin()) {
            cbArchiv.setEnabled(true);
            cmbStation.setEnabled(true);
            // Admins dürfen alles sehen.
            //DefaultComboBoxModel d = (DefaultComboBoxModel) cmbStation.getModel();
            //d.addElement("Alle");
            //cmbStation.setModel(d);
        } else {
            cbArchiv.setEnabled(false);
            cmbStation.setEnabled(true);
        }
    }

    private void print(String html) {
        try {
            // Create temp file.
            File temp = File.createTempFile("ueberleitung", ".html");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(SYSTools.htmlUmlautConversion(html));

            out.close();
            //DlgFilesAssign.handleFile(this, temp.getAbsolutePath(), Desktop.Action.OPEN);
        } catch (IOException e) {
        }
    }

    public void dispose() {
        hs.interrupt();
        cleanup();
        super.dispose();
    }

    public Bewohner getBewohner() {
        return bewohner;
    }

    private void formatBWTable() {
        JViewport jv = (JViewport) tblBW.getParent();
        JScrollPane jsp = (JScrollPane) jv.getParent();
        Dimension dim = jsp.getSize();
        // Größe der Massnahmen Spalten ändern.
        int width = dim.width;
        TableColumnModel tcm1 = tblBW.getColumnModel();

//        // Zu Beginn der Applikation steht noch ein standardmodell drin.
//        // das hat nur 4 Spalten. solange braucht sich dieser handler nicht
//        // damit zu befassen.
//        if (tcm1.getColumnCount() != ) {
//            return;
//        }

        tcm1.getColumn(0).setPreferredWidth(width);
        //tcm1.getColumn(1).setPreferredWidth(70);

        tcm1.getColumn(0).setHeaderValue("Name");
        //tcm1.getColumn(1).setHeaderValue("Info");

        tcm1.getColumn(0).setCellRenderer(new RNDBW());
        //tcm1.getColumn(1).setCellRenderer(new RNDBW());
    }

    public void cleanup() {
        // Aufräumen
        for (int i = 0; i < jtpPflegeakte.getTabCount(); i++) {
            if (jtpPflegeakte.getComponentAt(i) != null && jtpPflegeakte.getComponentAt(i) instanceof CleanablePanel) {
                CleanablePanel cp = (CleanablePanel) jtpPflegeakte.getComponentAt(i);
                cp.cleanup();
                SYSTools.unregisterListeners((JComponent) jtpPflegeakte.getComponentAt(i));
                jtpPflegeakte.setComponentAt(i, null);
            }
        }
        if (jtpMain.getComponentAt(MAINTAB_UEBERSICHT) != null && jtpMain.getComponentAt(MAINTAB_UEBERSICHT) instanceof CleanablePanel) {
            CleanablePanel cp = (CleanablePanel) jtpMain.getComponentAt(MAINTAB_UEBERSICHT);
            cp.cleanup();
            jtpMain.setComponentAt(MAINTAB_UEBERSICHT, null);
        }
    }

    private void reloadDisplay() {
        if (initPhase) {
            return;
        }
        cleanup();
        switch (jtpMain.getSelectedIndex()) {
            case MAINTAB_UEBERSICHT: {
                switch (jtpUebersicht.getSelectedIndex()) {
                    case UE_UEBERGABE: {
                        jtpUebersicht.setComponentAt(UE_UEBERGABE, new PnlUebergabe(this));
                        jtpUebersicht.setTitleAt(UE_UEBERGABE, "Übergabe");
                        break;
                    }
                    case UE_SCHICHTLEITUNG: {
                        jtpUebersicht.setComponentAt(UE_SCHICHTLEITUNG, new PnlSchichtleitung(this));
                        jtpUebersicht.setTitleAt(UE_SCHICHTLEITUNG, "Schichtleitung");
                        break;
                    }
                    default: {
                    }
                }

                break;
            }
            case MAINTAB_BW: {
                if (bewohner != null) {
                    switch (jtpPflegeakte.getSelectedIndex()) {
                        case TAB_UEBERSICHT: {
                            jtpPflegeakte.setComponentAt(TAB_UEBERSICHT, new PnlBWUebersicht(this));
                            jtpPflegeakte.setTitleAt(TAB_UEBERSICHT, "Übersicht");
                            break;
                        }
                        case TAB_PB: {
                            jtpPflegeakte.setComponentAt(TAB_PB, new PnlBerichte(this));
                            jtpPflegeakte.setTitleAt(TAB_PB, "Pflegeberichte");
                            break;
                        }
                        case TAB_DFN: {
                            jtpPflegeakte.setComponentAt(TAB_DFN, new PnlDFN(this, currentBW));
                            jtpPflegeakte.setTitleAt(TAB_DFN, "DFN");
                            break;
                        }
                        case TAB_VITAL: {
                            jtpPflegeakte.setComponentAt(TAB_VITAL, new PnlVitalwerte(currentBW, this, jtpPflegeakte, tblBW));
                            jtpPflegeakte.setTitleAt(TAB_VITAL, "Werte");
                            break;
                        }
                        case TAB_INFO: {
                            jtpPflegeakte.setComponentAt(TAB_INFO, new op.care.bwinfo.PnlInfo(this, currentBW));
                            jtpPflegeakte.setTitleAt(TAB_INFO, "Informationen");
                            break;
                        }
                        case TAB_BHP: {
                            jtpPflegeakte.setComponentAt(TAB_BHP, new PnlBHP(this, currentBW));
                            jtpPflegeakte.setTitleAt(TAB_BHP, "BHP");
                            break;
                        }
                        case TAB_PPLANUNG: {
                            jtpPflegeakte.setComponentAt(TAB_PPLANUNG, new PnlPlanung(this, currentBW));
                            jtpPflegeakte.setTitleAt(TAB_PPLANUNG, "Planung");
                            break;
                        }
                        case TAB_VERORDNUNG: {
                            jtpPflegeakte.setComponentAt(TAB_VERORDNUNG, new PnlVerordnung(this, currentBW));
                            jtpPflegeakte.setTitleAt(TAB_VERORDNUNG, "Verordnungen");
                            break;
                        }
                        case TAB_VORGANG: {
                            jtpPflegeakte.setComponentAt(TAB_VORGANG, new PnlBWVorgang(this));
                            jtpPflegeakte.setTitleAt(TAB_VORGANG, "Vorgänge");
                            break;
                        }
                        case TAB_FILES: {
                            jtpPflegeakte.setComponentAt(TAB_FILES, new PnlFiles(this));
                            jtpPflegeakte.setTitleAt(TAB_FILES, "Dokumente");
                            break;
                        }
                        default: {
                        }
                    }
                    break;
                }
            }

            default: {
            }
        }
    }

    private void reloadTable() {
        // Bewohner-Liste
        ListSelectionModel lsmbw = tblBW.getSelectionModel();
        if (lslBW != null) {
            lsmbw.removeListSelectionListener(lslBW);
        }
        lslBW = new HandleBWSelections();

        if (cbArchiv.isSelected()) { // Archivmodus gewünscht.
            tblBW.setModel(new TMBW());
            initPhase = true;
            //cmbStation.setSelectedItem("Alle");
            cmbStation.setEnabled(false);
            initPhase = false;
        } else {
            tblBW.setModel(new TMBW( (Stationen) cmbStation.getSelectedItem()));
        }

        tblBW.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lsmbw.addListSelectionListener(lslBW);
        if (tblBW.getModel().getRowCount() > 0) {
            tblBW.setRowSelectionInterval(0, 0);
        }
        TableColumnModel tcm1 = tblBW.getColumnModel();
//        tcm1.getColumn(0).setHeaderValue("Name");
//        tcm1.getColumn(1).setHeaderValue("Info");
        jspBW.dispatchEvent(new ComponentEvent(jspBW, ComponentEvent.COMPONENT_RESIZED));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgDFN;
    private javax.swing.ButtonGroup bgKorrekturen;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnVerlegung;
    private javax.swing.JCheckBox cbArchiv;
    private javax.swing.JComboBox cmbStation;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JScrollPane jspBW;
    private javax.swing.JSplitPane jsplBW;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JTabbedPane jtpPflegeakte;
    private javax.swing.JTabbedPane jtpUebersicht;
    private javax.swing.JLabel lblHeap;
    private javax.swing.JLabel lblServer;
    private javax.swing.JLabel lblUser;
    private javax.swing.JProgressBar pbHeap;
    private javax.swing.JPanel pnlBHP;
    private javax.swing.JPanel pnlBW;
    private javax.swing.JPanel pnlDFN;
    private javax.swing.JPanel pnlFiles;
    private javax.swing.JPanel pnlInfo;
    private javax.swing.JPanel pnlPPlanung;
    private javax.swing.JPanel pnlSchichtleitung;
    private javax.swing.JPanel pnlStatus;
    private javax.swing.JPanel pnlTB;
    private javax.swing.JPanel pnlUeber;
    private javax.swing.JPanel pnlUebergabe;
    private javax.swing.JPanel pnlVer;
    private javax.swing.JPanel pnlVitalDummy;
    private javax.swing.JPanel pnlVorgang;
    private javax.swing.JTable tblBW;
    // End of variables declaration//GEN-END:variables

    class HandleBWSelections
            implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent lse) {
            // Erst reagieren wenn der Auswahl-Vorgang abgeschlossen ist.
            if (!lse.getValueIsAdjusting()) {
                DefaultListSelectionModel lsm = (DefaultListSelectionModel) lse.getSource();
                TableModel tm = getTblBW().getModel();
                currentBW = ((String) tm.getValueAt(lsm.getLeadSelectionIndex(), TMBW.COL_BWKENNUNG)).trim();
                Query query = OPDE.getEM().createNamedQuery("Bewohner.findByBWKennung");
                query.setParameter("bWKennung", currentBW);
                bewohner = (Bewohner) query.getSingleResult();
                // Bewohnernamen auf das Fenster schreiben.
                //String bwname = ((String) tm.getValueAt(lsm.getLeadSelectionIndex(), 0)).trim() + ", " + ((String) tm.getValueAt(lsm.getLeadSelectionIndex(), 1)).trim();
                bwlabel = null;
                reloadDisplay();

            } // if (!lse.getValueIsAdjusting()){

        } // public void valueChanged(ListSelectionEvent lse) {
    } // class HandleBWSelections

    public String getCurrentBW() {
        return currentBW;
    }

    public javax.swing.JTabbedPane getjtpPflegeakte() {
        return jtpPflegeakte;
    }

    public javax.swing.JTable getTblBW() {
        return tblBW;
    }
}
