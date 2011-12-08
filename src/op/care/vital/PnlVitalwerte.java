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
import entity.*;
import entity.files.SYSFilesTools;
import entity.vorgang.VorgaengeTools;
import op.OCSec;
import op.OPDE;
import op.care.CleanablePanel;
import op.care.FrmPflege;
import op.tools.InternalClassACL;
import op.tools.SYSCalendar;
import op.tools.SYSPrint;
import op.tools.SYSTools;
import tablerenderer.RNDHTML;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * @author tloehr
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
    private Bewohner bewohner;
    public boolean editMode = false;
    public boolean newMode = false;
    private boolean[] filter = {false, false, false, false, false, false, false, false, false, false, false, false};
    private FrmPflege parent;
    private FocusAdapter fa;
    private boolean initPhase;
    private JPopupMenu menu;
    private OCSec ocs;
    private ActionListener standardActionListener;
    public static final String internalClassID = "nursingrecords.vitalparameters";

    /**
     * Creates new form pnlVitalwerte
     */
    public PnlVitalwerte(FrmPflege parent, Bewohner bewohner) {
        initPhase = true;

        this.parent = parent;
        ocs = OPDE.getOCSec();
        initComponents();


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
        standardActionListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        };
        change2Bewohner(bewohner);
        initPhase = false;
    }

//    private void jdcDatumFocusLost(java.awt.event.FocusEvent evt) {
//        if (jdcDatum.getDate() == null) {
//            jdcDatum.setDate(SYSCalendar.addField(SYSCalendar.today_date(), -2, GregorianCalendar.WEEK_OF_MONTH));
//        }
//        jdcDatum.firePropertyChange("date", 0, 0);
//    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspTblVW = new JScrollPane();
        tblVital = new JTable();
        jToolBar1 = new JToolBar();
        btnPrint = new JButton();
        btnLogout = new JButton();
        lblBW = new JLabel();
        jPanel1 = new JPanel();
        cbRR = new JCheckBox();
        cbPuls = new JCheckBox();
        cbBZ = new JCheckBox();
        cbTemp = new JCheckBox();
        cbGewicht = new JCheckBox();
        cbAtem = new JCheckBox();
        cbGroesse = new JCheckBox();
        cbBilanz = new JCheckBox();
        cbStuhlgang = new JCheckBox();
        cbQuick = new JCheckBox();
        cbIDS = new JCheckBox();
        cbShowEdits = new JCheckBox();
        cbErbrochen = new JCheckBox();
        jPanel2 = new JPanel();
        jLabel2 = new JLabel();
        jdcVon = new JDateChooser();
        btnHA = new JButton();
        btnToday1 = new JButton();
        jLabel1 = new JLabel();
        jdcBis = new JDateChooser();
        btnToday2 = new JButton();

        //======== this ========

        //======== jspTblVW ========
        {
            jspTblVW.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    jspTblVWMousePressed(e);
                }
            });
            jspTblVW.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    jspTblVWComponentResized(e);
                }
            });

            //---- tblVital ----
            tblVital.setModel(new DefaultTableModel(
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
            tblVital.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    tblVitalMousePressed(e);
                }
            });
            jspTblVW.setViewportView(tblVital);
        }

        //======== jToolBar1 ========
        {
            jToolBar1.setFloatable(false);

            //---- btnPrint ----
            btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/fileprint.png")));
            btnPrint.setText("Drucken");
            btnPrint.setEnabled(false);
            btnPrint.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnPrintActionPerformed(e);
                }
            });
            jToolBar1.add(btnPrint);

            //---- btnLogout ----
            btnLogout.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/lock.png")));
            btnLogout.setText("Abmelden");
            btnLogout.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnLogoutActionPerformed(e);
                }
            });
            jToolBar1.add(btnLogout);
        }

        //---- lblBW ----
        lblBW.setFont(new Font("Dialog", Font.BOLD, 18));
        lblBW.setForeground(new Color(255, 51, 0));
        lblBW.setText("jLabel3");

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

            //---- cbRR ----
            cbRR.setText("Blutdruck / Puls");
            cbRR.setBorder(BorderFactory.createEmptyBorder());
            cbRR.setMargin(new Insets(0, 0, 0, 0));
            cbRR.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbRRActionPerformed(e);
                }
            });

            //---- cbPuls ----
            cbPuls.setText("Puls");
            cbPuls.setBorder(BorderFactory.createEmptyBorder());
            cbPuls.setMargin(new Insets(0, 0, 0, 0));
            cbPuls.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbPulsActionPerformed(e);
                }
            });

            //---- cbBZ ----
            cbBZ.setText("Blutzucker");
            cbBZ.setBorder(BorderFactory.createEmptyBorder());
            cbBZ.setMargin(new Insets(0, 0, 0, 0));
            cbBZ.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbBZActionPerformed(e);
                }
            });

            //---- cbTemp ----
            cbTemp.setText("Temperatur");
            cbTemp.setBorder(BorderFactory.createEmptyBorder());
            cbTemp.setMargin(new Insets(0, 0, 0, 0));
            cbTemp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbTempActionPerformed(e);
                }
            });

            //---- cbGewicht ----
            cbGewicht.setText("Gewicht");
            cbGewicht.setBorder(BorderFactory.createEmptyBorder());
            cbGewicht.setMargin(new Insets(0, 0, 0, 0));
            cbGewicht.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbGewichtActionPerformed(e);
                }
            });

            //---- cbAtem ----
            cbAtem.setText("Atemfrequenz");
            cbAtem.setBorder(BorderFactory.createEmptyBorder());
            cbAtem.setMargin(new Insets(0, 0, 0, 0));
            cbAtem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbAtemActionPerformed(e);
                }
            });

            //---- cbGroesse ----
            cbGroesse.setText("Gr\u00f6\u00dfe");
            cbGroesse.setBorder(BorderFactory.createEmptyBorder());
            cbGroesse.setMargin(new Insets(0, 0, 0, 0));
            cbGroesse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbGroesseActionPerformed(e);
                }
            });

            //---- cbBilanz ----
            cbBilanz.setText("Ein-/Ausfuhr");
            cbBilanz.setBorder(BorderFactory.createEmptyBorder());
            cbBilanz.setMargin(new Insets(0, 0, 0, 0));
            cbBilanz.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbBilanzActionPerformed(e);
                }
            });

            //---- cbStuhlgang ----
            cbStuhlgang.setText("Stuhlgang");
            cbStuhlgang.setBorder(BorderFactory.createEmptyBorder());
            cbStuhlgang.setMargin(new Insets(0, 0, 0, 0));
            cbStuhlgang.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbStuhlgangActionPerformed(e);
                }
            });

            //---- cbQuick ----
            cbQuick.setText("Quick");
            cbQuick.setBorder(BorderFactory.createEmptyBorder());
            cbQuick.setMargin(new Insets(0, 0, 0, 0));
            cbQuick.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbQuickActionPerformed(e);
                }
            });

            //---- cbIDS ----
            cbIDS.setText("Werte-Nr. anzeigen");
            cbIDS.setToolTipText("<html>Jeder Bericht hat immer eine eindeutige Nummer.<br/>Diese Nummern werden im Alltag nicht ben\u00f6tigt.<br/>Sollten Sie diese Nummern dennoch sehen wollen<br/>dann schalten Sie diese hier ein.</html>");
            cbIDS.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbIDSActionPerformed(e);
                }
            });

            //---- cbShowEdits ----
            cbShowEdits.setText("\u00c4nderungen anzeigen");
            cbShowEdits.setToolTipText("<html>Damit \u00c4nderungen und L\u00f6schungen trotzdem nachvollziehbar bleiben<br/>werden sie nur ausgeblendet. Mit diesem Schalter werden diese \u00c4nderungen wieder angezeigt.</html>");
            cbShowEdits.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbShowEditsActionPerformed(e);
                }
            });

            //---- cbErbrochen ----
            cbErbrochen.setText("Erbrochen");
            cbErbrochen.setBorder(new EmptyBorder(1, 1, 1, 1));
            cbErbrochen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbErbrochenActionPerformed(e);
                }
            });

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup()
                            .addComponent(cbRR)
                            .addComponent(cbBZ)
                            .addComponent(cbPuls))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup()
                            .addComponent(cbAtem)
                            .addComponent(cbGewicht)
                            .addComponent(cbTemp))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup()
                                    .addComponent(cbBilanz)
                                    .addComponent(cbGroesse))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup()
                                    .addComponent(cbErbrochen)
                                    .addComponent(cbQuick)))
                            .addComponent(cbStuhlgang))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup()
                            .addComponent(cbIDS)
                            .addComponent(cbShowEdits))
                        .addGap(39, 39, 39))
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(cbRR)
                            .addComponent(cbGroesse)
                            .addComponent(cbTemp)
                            .addComponent(cbQuick)
                            .addComponent(cbIDS))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(cbGewicht)
                                    .addComponent(cbBilanz)
                                    .addComponent(cbPuls)
                                    .addComponent(cbErbrochen))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(cbAtem)
                                    .addComponent(cbBZ)
                                    .addComponent(cbStuhlgang)))
                            .addComponent(cbShowEdits))
                        .addContainerGap(19, Short.MAX_VALUE))
            );
        }

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

            //---- jLabel2 ----
            jLabel2.setText("Werte anzeigen vom:");

            //---- jdcVon ----
            jdcVon.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                    jdcVonPropertyChange(e);
                }
            });

            //---- btnHA ----
            btnHA.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/2leftarrow.png")));
            btnHA.setToolTipText("Erster Eintrag");
            btnHA.setBorder(null);
            btnHA.setBorderPainted(false);
            btnHA.setContentAreaFilled(false);
            btnHA.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnHAActionPerformed(e);
                }
            });

            //---- btnToday1 ----
            btnToday1.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/history.png")));
            btnToday1.setToolTipText("Heute");
            btnToday1.setBorder(null);
            btnToday1.setBorderPainted(false);
            btnToday1.setContentAreaFilled(false);
            btnToday1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnToday1ActionPerformed(e);
                }
            });

            //---- jLabel1 ----
            jLabel1.setText("bis einschlie\u00dflich:");

            //---- jdcBis ----
            jdcBis.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                    jdcBisPropertyChange(e);
                }
            });

            //---- btnToday2 ----
            btnToday2.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/history.png")));
            btnToday2.setToolTipText("Heute");
            btnToday2.setBorder(null);
            btnToday2.setBorderPainted(false);
            btnToday2.setContentAreaFilled(false);
            btnToday2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnToday2ActionPerformed(e);
                }
            });

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addGap(2, 2, 2)
                        .addComponent(jdcVon, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHA)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnToday1, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jdcBis, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnToday2)
                        .addContainerGap(179, Short.MAX_VALUE))
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup()
                            .addComponent(jdcBis, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jdcVon, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnToday2)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnHA, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnToday1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)))
                        .addContainerGap(20, Short.MAX_VALUE))
            );
            jPanel2Layout.linkSize(SwingConstants.VERTICAL, new Component[] {btnHA, btnToday1, btnToday2, jLabel1, jLabel2, jdcBis, jdcVon});
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblBW, GroupLayout.DEFAULT_SIZE, 810, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 810, Short.MAX_VALUE)
                        .addComponent(jPanel2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jspTblVW, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 810, Short.MAX_VALUE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lblBW)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jspTblVW, GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                    .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void change2Bewohner(Bewohner bewohner) {
        this.currentBW = bewohner.getBWKennung();
        this.bewohner = bewohner;
        BewohnerTools.setBWLabel(lblBW, bewohner);
        reloadTable();
    }

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
        tblVital.getColumnModel().getColumn(0).setCellRenderer(new RNDHTML());
        tblVital.getColumnModel().getColumn(1).setCellRenderer(new RNDHTML());

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

//                if (!alreadyEdited && singleRowSelected) {
//                    menu.add(new JSeparator());
//                    // #0000003
//                    menu.add(op.share.vorgang.DBHandling.getVorgangContextMenu(parent, "BWerte", bwid, currentBW, fileActionListener));
//
//                    Query query = OPDE.getEM().createNamedQuery("BWerte.findByBwid");
//                    query.setParameter("bwid", bwid);
//                    entity.BWerte bwert = (entity.BWerte) query.getSingleResult();
//                    menu.add(SYSFilesTools.getSYSFilesContextMenu(parent, bwert, fileActionListener));
//
//                    // #0000035
//                    //menu.add(SYSFiles.getOPFilesContextMenu(parent, "BWerte", bwid, currentBW, tblVital, true, true, SYSFiles.CODE_BERICHTE, fileActionListener));
//                }

                BWerte aktuellerWert = BWerteTools.findByID(bwid);

                if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !alreadyEdited && singleRowSelected) {
                    menu.add(new JSeparator());
                    menu.add(SYSFilesTools.getSYSFilesContextMenu(parent, aktuellerWert, standardActionListener));
                }

                if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !alreadyEdited && singleRowSelected) {
                    menu.add(new JSeparator());
                    menu.add(VorgaengeTools.getVorgangContextMenu(parent, aktuellerWert, bewohner, standardActionListener));
                }

            }
        }
        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }//GEN-LAST:event_tblVitalMousePressed

    private void cbIDSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbIDSActionPerformed
        if (this.initPhase) {
            return;
        }
        SYSPropsTools.storeState(this.getClass().getName() + ":cbTBIDS", cbIDS);
        reloadTable();
    }//GEN-LAST:event_cbIDSActionPerformed

    private void cbShowEditsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbShowEditsActionPerformed
        if (this.initPhase) {
            return;
        }
        SYSPropsTools.storeState(this.getClass().getName() + ":cbShowEdits", cbShowEdits);
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
    private JScrollPane jspTblVW;
    private JTable tblVital;
    private JToolBar jToolBar1;
    private JButton btnPrint;
    private JButton btnLogout;
    private JLabel lblBW;
    private JPanel jPanel1;
    private JCheckBox cbRR;
    private JCheckBox cbPuls;
    private JCheckBox cbBZ;
    private JCheckBox cbTemp;
    private JCheckBox cbGewicht;
    private JCheckBox cbAtem;
    private JCheckBox cbGroesse;
    private JCheckBox cbBilanz;
    private JCheckBox cbStuhlgang;
    private JCheckBox cbQuick;
    private JCheckBox cbIDS;
    private JCheckBox cbShowEdits;
    private JCheckBox cbErbrochen;
    private JPanel jPanel2;
    private JLabel jLabel2;
    private JDateChooser jdcVon;
    private JButton btnHA;
    private JButton btnToday1;
    private JLabel jLabel1;
    private JDateChooser jdcBis;
    private JButton btnToday2;
    // End of variables declaration//GEN-END:variables
}
