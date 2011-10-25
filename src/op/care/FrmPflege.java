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

import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import entity.Bewohner;
import entity.BewohnerTools;
import entity.Stationen;
import entity.StationenTools;
import op.OPDE;
import op.care.berichte.PnlBerichte;
import op.care.bhp.PnlBHP;
import op.care.dfn.PnlDFN;
import op.care.planung.PnlPlanung;
import op.care.schichtleitung.PnlSchichtleitung;
import op.care.sysfiles.PnlFiles;
import op.care.uebergabe.PnlUebergabe;
import op.care.verordnung.PnlVerordnung;
import op.care.vital.PnlVitalwerte;
import op.threads.HeapStat;
import op.tools.DBRetrieve;
import op.tools.InternalClassACL;
import op.tools.SYSTools;
import op.vorgang.PnlVorgang;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author __USER__
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

        lblServer.setText(OPDE.getUrl());
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

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlStatus = new JPanel();
        lblServer = new JLabel();
        lblUser = new JLabel();
        pbHeap = new JProgressBar();
        lblHeap = new JLabel();
        jtpMain = new JTabbedPane();
        jtpUebersicht = new JTabbedPane();
        pnlUebergabe = new JPanel();
        pnlSchichtleitung = new JPanel();
        jsplBW = new JSplitPane();
        jtpPflegeakte = new JTabbedPane();
        pnlUeber = new JPanel();
        pnlTB = new JPanel();
        pnlDFN = new JPanel();
        pnlBHP = new JPanel();
        pnlVitalDummy = new JPanel();
        pnlVer = new JPanel();
        pnlInfo = new JPanel();
        pnlPPlanung = new JPanel();
        pnlVorgang = new JPanel();
        pnlFiles = new JPanel();
        pnlBW = new JPanel();
        cmbStation = new JComboBox();
        jspBW = new JScrollPane();
        tblBW = new JTable();
        cbArchiv = new JCheckBox();
        btnRefresh = new JButton();
        jToolBar1 = new JToolBar();
        btnVerlegung = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("OpenCare Prototype C");
        Container contentPane = getContentPane();

        //======== pnlStatus ========
        {
            pnlStatus.setBorder(new EtchedBorder());

            //---- lblServer ----
            lblServer.setText("jLabel6");

            //---- lblUser ----
            lblUser.setText("jLabel6");

            //---- lblHeap ----
            lblHeap.setText("jLabel1");

            GroupLayout pnlStatusLayout = new GroupLayout(pnlStatus);
            pnlStatus.setLayout(pnlStatusLayout);
            pnlStatusLayout.setHorizontalGroup(
                pnlStatusLayout.createParallelGroup()
                    .addGroup(pnlStatusLayout.createSequentialGroup()
                        .addComponent(lblServer)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pbHeap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblHeap)
                        .addGap(18, 18, 18)
                        .addComponent(lblUser, GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
                        .addContainerGap())
            );
            pnlStatusLayout.setVerticalGroup(
                pnlStatusLayout.createParallelGroup()
                    .addGroup(pnlStatusLayout.createSequentialGroup()
                        .addGroup(pnlStatusLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(lblServer)
                            .addComponent(lblHeap)
                            .addComponent(lblUser))
                        .addContainerGap())
                    .addComponent(pbHeap, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
            );
        }

        //======== jtpMain ========
        {
            jtpMain.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    jtpMainStateChanged(e);
                }
            });

            //======== jtpUebersicht ========
            {
                jtpUebersicht.setTabPlacement(SwingConstants.BOTTOM);
                jtpUebersicht.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        jtpUebersichtStateChanged(e);
                    }
                });

                //======== pnlUebergabe ========
                {

                    GroupLayout pnlUebergabeLayout = new GroupLayout(pnlUebergabe);
                    pnlUebergabe.setLayout(pnlUebergabeLayout);
                    pnlUebergabeLayout.setHorizontalGroup(
                        pnlUebergabeLayout.createParallelGroup()
                            .addGap(0, 809, Short.MAX_VALUE)
                    );
                    pnlUebergabeLayout.setVerticalGroup(
                        pnlUebergabeLayout.createParallelGroup()
                            .addGap(0, 469, Short.MAX_VALUE)
                    );
                }
                jtpUebersicht.addTab("\u00dcbergabe", pnlUebergabe);


                //======== pnlSchichtleitung ========
                {

                    GroupLayout pnlSchichtleitungLayout = new GroupLayout(pnlSchichtleitung);
                    pnlSchichtleitung.setLayout(pnlSchichtleitungLayout);
                    pnlSchichtleitungLayout.setHorizontalGroup(
                        pnlSchichtleitungLayout.createParallelGroup()
                            .addGap(0, 809, Short.MAX_VALUE)
                    );
                    pnlSchichtleitungLayout.setVerticalGroup(
                        pnlSchichtleitungLayout.createParallelGroup()
                            .addGap(0, 469, Short.MAX_VALUE)
                    );
                }
                jtpUebersicht.addTab("Schichtleitung", pnlSchichtleitung);

            }
            jtpMain.addTab("\u00dcbersicht", jtpUebersicht);


            //======== jsplBW ========
            {
                jsplBW.setDividerLocation(250);

                //======== jtpPflegeakte ========
                {
                    jtpPflegeakte.setTabPlacement(SwingConstants.BOTTOM);
                    jtpPflegeakte.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            jtpPflegeakteStateChanged(e);
                        }
                    });

                    //======== pnlUeber ========
                    {

                        GroupLayout pnlUeberLayout = new GroupLayout(pnlUeber);
                        pnlUeber.setLayout(pnlUeberLayout);
                        pnlUeberLayout.setHorizontalGroup(
                            pnlUeberLayout.createParallelGroup()
                                .addGap(0, 548, Short.MAX_VALUE)
                        );
                        pnlUeberLayout.setVerticalGroup(
                            pnlUeberLayout.createParallelGroup()
                                .addGap(0, 465, Short.MAX_VALUE)
                        );
                    }
                    jtpPflegeakte.addTab("\u00dcbersicht", pnlUeber);


                    //======== pnlTB ========
                    {

                        GroupLayout pnlTBLayout = new GroupLayout(pnlTB);
                        pnlTB.setLayout(pnlTBLayout);
                        pnlTBLayout.setHorizontalGroup(
                            pnlTBLayout.createParallelGroup()
                                .addGap(0, 548, Short.MAX_VALUE)
                        );
                        pnlTBLayout.setVerticalGroup(
                            pnlTBLayout.createParallelGroup()
                                .addGap(0, 465, Short.MAX_VALUE)
                        );
                    }
                    jtpPflegeakte.addTab("Pflegeberichte", pnlTB);


                    //======== pnlDFN ========
                    {

                        GroupLayout pnlDFNLayout = new GroupLayout(pnlDFN);
                        pnlDFN.setLayout(pnlDFNLayout);
                        pnlDFNLayout.setHorizontalGroup(
                            pnlDFNLayout.createParallelGroup()
                                .addGap(0, 548, Short.MAX_VALUE)
                        );
                        pnlDFNLayout.setVerticalGroup(
                            pnlDFNLayout.createParallelGroup()
                                .addGap(0, 465, Short.MAX_VALUE)
                        );
                    }
                    jtpPflegeakte.addTab("DFN", pnlDFN);


                    //======== pnlBHP ========
                    {

                        GroupLayout pnlBHPLayout = new GroupLayout(pnlBHP);
                        pnlBHP.setLayout(pnlBHPLayout);
                        pnlBHPLayout.setHorizontalGroup(
                            pnlBHPLayout.createParallelGroup()
                                .addGap(0, 548, Short.MAX_VALUE)
                        );
                        pnlBHPLayout.setVerticalGroup(
                            pnlBHPLayout.createParallelGroup()
                                .addGap(0, 465, Short.MAX_VALUE)
                        );
                    }
                    jtpPflegeakte.addTab("BHP", pnlBHP);


                    //======== pnlVitalDummy ========
                    {

                        GroupLayout pnlVitalDummyLayout = new GroupLayout(pnlVitalDummy);
                        pnlVitalDummy.setLayout(pnlVitalDummyLayout);
                        pnlVitalDummyLayout.setHorizontalGroup(
                            pnlVitalDummyLayout.createParallelGroup()
                                .addGap(0, 548, Short.MAX_VALUE)
                        );
                        pnlVitalDummyLayout.setVerticalGroup(
                            pnlVitalDummyLayout.createParallelGroup()
                                .addGap(0, 465, Short.MAX_VALUE)
                        );
                    }
                    jtpPflegeakte.addTab("Werte", pnlVitalDummy);


                    //======== pnlVer ========
                    {

                        GroupLayout pnlVerLayout = new GroupLayout(pnlVer);
                        pnlVer.setLayout(pnlVerLayout);
                        pnlVerLayout.setHorizontalGroup(
                            pnlVerLayout.createParallelGroup()
                                .addGap(0, 548, Short.MAX_VALUE)
                        );
                        pnlVerLayout.setVerticalGroup(
                            pnlVerLayout.createParallelGroup()
                                .addGap(0, 465, Short.MAX_VALUE)
                        );
                    }
                    jtpPflegeakte.addTab("Verordnungen", pnlVer);


                    //======== pnlInfo ========
                    {

                        GroupLayout pnlInfoLayout = new GroupLayout(pnlInfo);
                        pnlInfo.setLayout(pnlInfoLayout);
                        pnlInfoLayout.setHorizontalGroup(
                            pnlInfoLayout.createParallelGroup()
                                .addGap(0, 548, Short.MAX_VALUE)
                        );
                        pnlInfoLayout.setVerticalGroup(
                            pnlInfoLayout.createParallelGroup()
                                .addGap(0, 465, Short.MAX_VALUE)
                        );
                    }
                    jtpPflegeakte.addTab("Informationen", pnlInfo);


                    //======== pnlPPlanung ========
                    {

                        GroupLayout pnlPPlanungLayout = new GroupLayout(pnlPPlanung);
                        pnlPPlanung.setLayout(pnlPPlanungLayout);
                        pnlPPlanungLayout.setHorizontalGroup(
                            pnlPPlanungLayout.createParallelGroup()
                                .addGap(0, 548, Short.MAX_VALUE)
                        );
                        pnlPPlanungLayout.setVerticalGroup(
                            pnlPPlanungLayout.createParallelGroup()
                                .addGap(0, 465, Short.MAX_VALUE)
                        );
                    }
                    jtpPflegeakte.addTab("Planung", pnlPPlanung);


                    //======== pnlVorgang ========
                    {

                        GroupLayout pnlVorgangLayout = new GroupLayout(pnlVorgang);
                        pnlVorgang.setLayout(pnlVorgangLayout);
                        pnlVorgangLayout.setHorizontalGroup(
                            pnlVorgangLayout.createParallelGroup()
                                .addGap(0, 548, Short.MAX_VALUE)
                        );
                        pnlVorgangLayout.setVerticalGroup(
                            pnlVorgangLayout.createParallelGroup()
                                .addGap(0, 465, Short.MAX_VALUE)
                        );
                    }
                    jtpPflegeakte.addTab("Vorg\u00e4nge", pnlVorgang);


                    //======== pnlFiles ========
                    {

                        GroupLayout pnlFilesLayout = new GroupLayout(pnlFiles);
                        pnlFiles.setLayout(pnlFilesLayout);
                        pnlFilesLayout.setHorizontalGroup(
                            pnlFilesLayout.createParallelGroup()
                                .addGap(0, 548, Short.MAX_VALUE)
                        );
                        pnlFilesLayout.setVerticalGroup(
                            pnlFilesLayout.createParallelGroup()
                                .addGap(0, 465, Short.MAX_VALUE)
                        );
                    }
                    jtpPflegeakte.addTab("Dokumente", pnlFiles);

                }
                jsplBW.setRightComponent(jtpPflegeakte);

                //======== pnlBW ========
                {

                    //---- cmbStation ----
                    cmbStation.setModel(new DefaultComboBoxModel(new String[] {
                        "Alle",
                        "Station 1",
                        "Station 2",
                        "Herchen"
                    }));
                    cmbStation.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbStationItemStateChanged(e);
                        }
                    });

                    //======== jspBW ========
                    {
                        jspBW.addComponentListener(new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                jspBWComponentResized(e);
                            }
                        });

                        //---- tblBW ----
                        tblBW.setModel(new DefaultTableModel(
                            new Object[][] {
                                {null, null, null, null},
                                {null, null, null, null},
                                {null, null, null, null},
                                {null, null, null, null},
                            },
                            new String[] {
                                "Title 1", "Title 2", "Title 3", "Title 4"
                            }
                        ) {
                            Class<?>[] columnTypes = new Class<?>[] {
                                Object.class, Object.class, Object.class, Object.class
                            };
                            @Override
                            public Class<?> getColumnClass(int columnIndex) {
                                return columnTypes[columnIndex];
                            }
                        });
                        jspBW.setViewportView(tblBW);
                    }

                    //---- cbArchiv ----
                    cbArchiv.setText("Archiv");
                    cbArchiv.setBorder(BorderFactory.createEmptyBorder());
                    cbArchiv.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbArchivActionPerformed(e);
                        }
                    });

                    //---- btnRefresh ----
                    btnRefresh.setBackground(Color.white);
                    btnRefresh.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/reload.png")));
                    btnRefresh.setBorder(null);
                    btnRefresh.setBorderPainted(false);
                    btnRefresh.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnRefreshActionPerformed(e);
                        }
                    });

                    //======== jToolBar1 ========
                    {
                        jToolBar1.setFloatable(false);
                        jToolBar1.setRollover(true);

                        //---- btnVerlegung ----
                        btnVerlegung.setForeground(new Color(255, 51, 0));
                        btnVerlegung.setText("Verlegungsbericht");
                        btnVerlegung.setToolTipText("");
                        btnVerlegung.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnVerlegungActionPerformed(e);
                            }
                        });
                        jToolBar1.add(btnVerlegung);
                    }

                    GroupLayout pnlBWLayout = new GroupLayout(pnlBW);
                    pnlBW.setLayout(pnlBWLayout);
                    pnlBWLayout.setHorizontalGroup(
                        pnlBWLayout.createParallelGroup()
                            .addGroup(pnlBWLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(cbArchiv)
                                .addContainerGap(167, Short.MAX_VALUE))
                            .addGroup(GroupLayout.Alignment.TRAILING, pnlBWLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(cmbStation, 0, 202, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRefresh)
                                .addContainerGap())
                            .addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                            .addComponent(jspBW, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                    );
                    pnlBWLayout.setVerticalGroup(
                        pnlBWLayout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, pnlBWLayout.createSequentialGroup()
                                .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jspBW, GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlBWLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnRefresh)
                                    .addComponent(cmbStation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(8, 8, 8)
                                .addComponent(cbArchiv))
                    );
                }
                jsplBW.setLeftComponent(pnlBW);
            }
            jtpMain.addTab("Pflegeakte", jsplBW);

        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(pnlStatus, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jtpMain, GroupLayout.DEFAULT_SIZE, 851, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addComponent(jtpMain, GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(pnlStatus, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        setSize(851, 623);
        setLocationRelativeTo(getOwner());
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
                            final PnlVorgang pnlVorgang = new PnlVorgang(this, bewohner);
                            CleanablePanel cp = new CleanablePanel() {
                                @Override
                                public void cleanup() {
                                    pnlVorgang.cleanup();
                                }
                            };
                            cp.setLayout(new VerticalLayout(10));
                            cp.add(BewohnerTools.getBWLabel(bewohner));
                            cp.add(pnlVorgang);

                            jtpPflegeakte.setComponentAt(TAB_VORGANG, cp);
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
            tblBW.setModel(new TMBW((Stationen) cmbStation.getSelectedItem()));
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
    private JPanel pnlStatus;
    private JLabel lblServer;
    private JLabel lblUser;
    private JProgressBar pbHeap;
    private JLabel lblHeap;
    private JTabbedPane jtpMain;
    private JTabbedPane jtpUebersicht;
    private JPanel pnlUebergabe;
    private JPanel pnlSchichtleitung;
    private JSplitPane jsplBW;
    private JTabbedPane jtpPflegeakte;
    private JPanel pnlUeber;
    private JPanel pnlTB;
    private JPanel pnlDFN;
    private JPanel pnlBHP;
    private JPanel pnlVitalDummy;
    private JPanel pnlVer;
    private JPanel pnlInfo;
    private JPanel pnlPPlanung;
    private JPanel pnlVorgang;
    private JPanel pnlFiles;
    private JPanel pnlBW;
    private JComboBox cmbStation;
    private JScrollPane jspBW;
    private JTable tblBW;
    private JCheckBox cbArchiv;
    private JButton btnRefresh;
    private JToolBar jToolBar1;
    private JButton btnVerlegung;
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
