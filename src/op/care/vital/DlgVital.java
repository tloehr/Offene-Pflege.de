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

import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSCalendar;
import op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class DlgVital extends javax.swing.JDialog {

    public static final int MODE_UNKNOWN = 0;
    public static final int MODE_RR = 1;
    public static final int MODE_TEMP = 2;
    public static final int MODE_GROESSE = 3;
    public static final int MODE_GEWICHT = 4;
    public static final int MODE_BZ = 5;
    public static final int MODE_ATEM = 6;
    public static final int MODE_BILANZ = 7;
    public static final int MODE_PULS = 8;
    public static final int MODE_RRSYS = 9;
    public static final int MODE_RRDIA = 10;
    public static final int MODE_QUICK = 11;
    public static final int MODE_STUHLGANG = 12;
    public static final int MODE_ERBRECHEN = 13;
    public double min1, min2, min3, max1, max2, max3, norm1, norm2, norm3;
    int activeControls;
    Object[] object = null;
    int mode = 0;
    boolean edit = false;
    String currentBW;
//
//    public DlgVital(java.awt.Frame parent, int mode) {
//        this(parent, mode, false, "", "", "", null, null, "");
//    }

    /**
     * Creates new form dlgVital
     */
    public DlgVital(java.awt.Frame parent, Object[] obj) {
        super(parent, true);
        initComponents();
        lblStatus.setText(" ");
        this.object = obj;

        if (this.object.length == 2) {
            mode = (Integer) object[0];
            currentBW = object[1].toString();
            edit = false;
        } else {
            mode = DBHandling.getBWertMode(object[TMWerte.COL_XML].toString());
            currentBW = object[TMWerte.COL_BWKENNUNG].toString();
            edit = true;
        }

        if (!edit) {
            txtDatum.setText(SYSCalendar.printGCGermanStyle(new GregorianCalendar()));
            txtUhrzeit.setText(SYSCalendar.toGermanTime(new GregorianCalendar()));
            txtBemerkung.setText("");
        } else {
            txtDatum.setText(SYSCalendar.printGCGermanStyle(SYSCalendar.toGC(((Timestamp) object[TMWerte.COL_PIT]).getTime())));
            txtUhrzeit.setText(SYSCalendar.toGermanTime(SYSCalendar.toGC(((Timestamp) object[TMWerte.COL_PIT]).getTime())));
            txtBemerkung.setText(object[TMWerte.COL_BEMERKUNG].toString());
        }

        switch (mode) {
            case MODE_RR: {
                setActiveControls(3);

                if (!edit) {
                    txtWert1.setText("120");
                    txtWert2.setText("80");
                    txtWert3.setText("90");
                } else {
                    txtWert1.setText(object[TMWerte.COL_V1].toString());
                    txtWert2.setText(object[TMWerte.COL_V2].toString());
                    txtWert3.setText(object[TMWerte.COL_V3].toString());
                }

                // Grenzwerte
                min1 = 20;
                max1 = 250;
                norm1 = 120;
                min2 = 10;
                max2 = 160;
                norm2 = 80;
                min3 = 0;
                max3 = 250;
                norm3 = 90;

                lblVital.setText("Blutdruck und Puls");
                lblWert1.setText("RR systolisch:");
                lblWert2.setText("RR diastolisch:");
                lblWert3.setText("Puls:");
                lblEinheit1.setText("mm Hg");
                lblEinheit2.setText("mm Hg");
                lblEinheit3.setText("s/min");

                txtWert1.requestFocus();
                this.pack();
                break;
            }
            case MODE_PULS: {
                setActiveControls(1);

                if (!edit) {
                    txtWert2.setText("90");
                } else {
                    txtWert2.setText(object[TMWerte.COL_V1].toString());
                }

                // Grenzwerte
                min2 = 0;
                max2 = 250;
                norm2 = 90;

                lblVital.setText("Puls");
                lblWert2.setText("Puls:");
                lblEinheit2.setText("s/min");

                txtWert2.requestFocus();
                this.pack();
                break;
            }
            case MODE_ATEM: {
                setActiveControls(1);

                if (!edit) {
                    txtWert2.setText("15");
                } else {
                    txtWert2.setText(object[TMWerte.COL_V1].toString());
                }

                // Grenzwerte
                min2 = 1;
                max2 = 50;
                norm2 = 15;

                lblVital.setText("Atemfrequenz");
                lblWert2.setText("Frequenz:");
                lblEinheit2.setText("Atemzüge / min");

                txtWert2.requestFocus();
                this.pack();
                break;
            }
            case MODE_GEWICHT: {
                setActiveControls(1);

                if (!edit) {
                    txtWert2.setText("70.0");
                } else {
                    txtWert2.setText(object[TMWerte.COL_V1].toString());
                }

                // Grenzwerte
                min2 = 10;
                max2 = 250;
                norm2 = 70.0d;

                lblVital.setText("Körpergewicht");
                lblWert2.setText("Gewicht:");
                lblEinheit2.setText("kg");

                txtWert2.requestFocus();
                this.pack();
                break;
            }
            case MODE_GROESSE: {
                setActiveControls(1);

                if (!edit) {
                    txtWert2.setText("1.70");
                } else {
                    txtWert2.setText(object[TMWerte.COL_V1].toString());
                }

                // Grenzwerte
                min2 = 0.5d;
                max2 = 2.5d;
                norm2 = 1.7d;

                lblVital.setText("Körpergröße");
                lblWert2.setText("Größe:");
                lblEinheit2.setText("m");

                txtWert2.requestFocus();
                this.pack();
                break;
            }
            case MODE_BZ: {
                setActiveControls(1);

                if (!edit) {
                    txtWert2.setText("100");
                } else {
                    txtWert2.setText(object[TMWerte.COL_V1].toString());
                }

                // Grenzwerte
                min2 = 10d;
                max2 = 800d;
                norm2 = 100d;

                lblVital.setText("Blutzucker");
                lblWert2.setText("Konzentration:");
                lblEinheit2.setText("mg/dl");

                txtWert2.requestFocus();
                this.pack();
                break;
            }
            case MODE_QUICK: {
                setActiveControls(1);

                if (!edit) {
                    txtWert2.setText("50");
                } else {
                    txtWert2.setText(object[TMWerte.COL_V1].toString());
                }

                // Grenzwerte
                min2 = 0d;
                max2 = 100d;
                norm2 = 50d;

                lblVital.setText("Quickwert");
                lblWert2.setText("Blutgerinnung:");
                lblEinheit2.setText("%");

                txtWert2.requestFocus();
                this.pack();
                break;
            }
            case MODE_TEMP: {
                setActiveControls(1);

                if (!edit) {
                    txtWert2.setText("37.0");
                } else {
                    txtWert2.setText(object[TMWerte.COL_V1].toString());
                }

                // Grenzwerte
                min2 = 20.0d;
                max2 = 43.0d;
                norm2 = 37.0d;

                lblVital.setText("Körpertemperatur");
                lblWert2.setText("Temperatur:");
                lblEinheit2.setText("°C");

                txtWert2.requestFocus();
                this.pack();
                break;
            }
            case MODE_BILANZ: {
                setActiveControls(1);

                if (!edit) {
                    txtWert2.setText("200");
                } else {
                    txtWert2.setText(object[TMWerte.COL_V1].toString());
                }

                // Grenzwerte
                min2 = -3000.0d;
                max2 = 3000.0d;
                norm2 = 200.0d;

                lblVital.setText("Flüssigkeitszufuhr");
                lblWert2.setText("Menge:");
                lblEinheit2.setText("ml");

                txtWert2.requestFocus();
                this.pack();
                break;
            }
            case MODE_RRSYS: {
                setActiveControls(1);

                if (!edit) {
                    txtWert2.setText("120");
                } else {
                    txtWert2.setText(object[TMWerte.COL_V1].toString());
                }

                // Grenzwerte
                min2 = 20;
                max2 = 250;
                norm2 = 120;

                lblVital.setText("Blutdruck systolisch");
                lblWert2.setText("RR systolisch:");
                lblEinheit2.setText("mm Hg");

                txtWert2.requestFocus();
                this.pack();
                break;
            }
            case MODE_RRDIA: {
                setActiveControls(1);

                if (!edit) {
                    txtWert2.setText("80");
                } else {
                    txtWert2.setText(object[TMWerte.COL_V1].toString());
                }

                // Grenzwerte
                min2 = 10;
                max2 = 160;
                norm2 = 80;

                lblVital.setText("Blutdruck diastolisch");
                lblWert2.setText("RR diastolisch:");
                lblEinheit2.setText("mm Hg");

                txtWert2.requestFocus();
                this.pack();
                break;
            }
            case MODE_STUHLGANG: {
                setActiveControls(0);
                lblVital.setText("Stuhlgang");
                txtBemerkung.requestFocus();
                this.pack();
                break;
            }
            case MODE_ERBRECHEN: {
                setActiveControls(0);
                lblVital.setText("Erbrochen");
                txtBemerkung.requestFocus();
                this.pack();
                break;
            }
            default: {
            }
        }

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

        lblVital = new javax.swing.JLabel();
        lblWert1 = new javax.swing.JLabel();
        lblWert2 = new javax.swing.JLabel();
        lblWert3 = new javax.swing.JLabel();
        txtWert1 = new javax.swing.JTextField();
        txtWert2 = new javax.swing.JTextField();
        txtWert3 = new javax.swing.JTextField();
        lblEinheit1 = new javax.swing.JLabel();
        lblEinheit2 = new javax.swing.JLabel();
        lblEinheit3 = new javax.swing.JLabel();
        btnDiscard = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtDatum = new javax.swing.JTextField();
        txtUhrzeit = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setModal(true);

        lblVital.setFont(new java.awt.Font("Dialog", 1, 18));
        lblVital.setText("jLabel1");

        lblWert1.setText("jLabel1");

        lblWert2.setText("jLabel1");

        lblWert3.setText("jLabel1");

        txtWert1.setText("jTextField1");
        txtWert1.setFocusCycleRoot(true);
        txtWert1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtWert1ActionPerformed(evt);
            }
        });
        txtWert1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtWert1FocusGained(evt);
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                txtWert1FocusLost(evt);
            }
        });

        txtWert2.setText("jTextField1");
        txtWert2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtWert2ActionPerformed(evt);
            }
        });
        txtWert2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtWert2FocusGained(evt);
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                txtWert2FocusLost(evt);
            }
        });

        txtWert3.setText("jTextField1");
        txtWert3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtWert3ActionPerformed(evt);
            }
        });
        txtWert3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtWert3FocusGained(evt);
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                txtWert3FocusLost(evt);
            }
        });

        lblEinheit1.setText("jLabel1");

        lblEinheit2.setText("jLabel1");

        lblEinheit3.setText("jLabel1");

        btnDiscard.setText("Verwerfen");
        btnDiscard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiscardActionPerformed(evt);
            }
        });

        btnSave.setText("Speichern");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        txtBemerkung.setColumns(20);
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setRows(5);
        txtBemerkung.setToolTipText("Tragen Sie hier Bemerkungen ein.");
        txtBemerkung.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtBemerkung);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblStatus.setForeground(new java.awt.Color(255, 0, 0));
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblStatus.setText("jLabel3");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(lblStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(lblStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Datum:");

        txtDatum.setText("jTextField2");
        txtDatum.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDatumFocusGained(evt);
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDatumFocusLost(evt);
            }
        });

        txtUhrzeit.setText("jTextField1");
        txtUhrzeit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUhrzeitFocusGained(evt);
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUhrzeitFocusLost(evt);
            }
        });

        jLabel1.setText("Uhrzeit:");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(txtDatum, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(txtUhrzeit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(273, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(txtUhrzeit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jLabel1)
                                        .add(txtDatum, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jLabel2))
                                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                                .add(btnSave)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(btnDiscard))
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                                        .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                                                .add(lblWert3)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(txtWert3))
                                                        .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                                                .add(lblWert2)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(txtWert2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                                                        .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                                                .add(lblWert1)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(txtWert1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                                        .add(lblEinheit1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                                                        .add(lblEinheit2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                                                        .add(lblEinheit3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
                                        .add(lblVital, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(lblVital)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(btnDiscard)
                                                        .add(btnSave)))
                                        .add(layout.createSequentialGroup()
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(lblWert1)
                                                        .add(txtWert1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(lblEinheit1))
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(lblWert2)
                                                        .add(txtWert2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(lblEinheit2))
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(lblWert3)
                                                        .add(txtWert3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(lblEinheit3))))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - 576) / 2, (screenSize.height - 323) / 2, 576, 323);
    }// </editor-fold>//GEN-END:initComponents

    private void txtWert3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWert3FocusLost
        txtWertFocusLost(evt, min3, max3, norm3);
    }//GEN-LAST:event_txtWert3FocusLost

    private void txtWert2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWert2FocusLost
        txtWertFocusLost(evt, min2, max2, norm2);
    }//GEN-LAST:event_txtWert2FocusLost

    private void txtWert1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWert1FocusLost
        txtWertFocusLost(evt, min1, max1, norm1);
    }//GEN-LAST:event_txtWert1FocusLost

    private void txtWertFocusLost(java.awt.event.FocusEvent evt, double min, double max, double norm) {
        JTextField jtf = (JTextField) evt.getSource();
        jtf.setText(jtf.getText().replace(",", "."));
        try {
            double v = Double.parseDouble(jtf.getText());
            if (v < min || max < v) {
                notify("Der Wert liegt außerhalb des erlaubten Bereiches. Min: " + Double.toString(min) + " Max: " + Double.toString(max));
                jtf.setText(Double.toString(norm));
                jtf.requestFocus();
            }
        } catch (NumberFormatException ex) {
            jtf.setText(Double.toString(norm));
            notify("Der Wert ist keine gültige Zahl.");
            jtf.requestFocus();
        }
    }

    private void notify(String text) {
        Toolkit.getDefaultToolkit().beep();
        text = SYSCalendar.toGermanTime(SYSCalendar.heute()) + ": " + text;
        lblStatus.setText(text);
    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed

        HashMap hm = new HashMap();

        Date date = new Date(SYSCalendar.erkenneDatum(txtDatum.getText()).getTimeInMillis());
        Time time = new Time(SYSCalendar.erkenneUhrzeit(txtUhrzeit.getText()).getTimeInMillis());
        hm.put("PIT", SYSCalendar.addTime2Date(date, time));

        hm.put("BWKennung", currentBW);
        hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
        hm.put("Bemerkung", txtBemerkung.getText());
        hm.put("_cdate", "!NOW!");
        hm.put("_mdate", "!NOW!");

        double[] werte = null;
        String[] xmls = null;
        long[] bwids = null;


        if (this.activeControls == 1) {
            werte = new double[]{Double.parseDouble(txtWert2.getText())};
            xmls = new String[]{DBHandling.getBWertXML(mode)};
            if (edit) {
                bwids = new long[]{(Long) object[TMWerte.COL_BWID]};
            } else {
                bwids = new long[]{0l};
            }
        }

        if (this.activeControls == 0) { // Für den Stuhlgang oder das Erbrechen
            werte = new double[]{0d};
            xmls = new String[]{DBHandling.getBWertXML(mode)};
            if (edit) {
                bwids = new long[]{(Long) object[TMWerte.COL_BWID]};
            } else {
                bwids = new long[]{0l};
            }
            // Sonst wird nichts gespeichert.
            this.activeControls = 1;
        }

        if (this.activeControls == 3) {
            werte = new double[]{Double.parseDouble(txtWert1.getText()), Double.parseDouble(txtWert2.getText()), Double.parseDouble(txtWert3.getText())};
            xmls = new String[]{DBHandling.getBWertXML(MODE_RRSYS), DBHandling.getBWertXML(MODE_RRDIA), DBHandling.getBWertXML(MODE_PULS)};
            if (edit) {
                bwids = new long[]{(Long) object[TMWerte.COL_PK1], (Long) object[TMWerte.COL_PK2], (Long) object[TMWerte.COL_PK3]};
            } else {
                bwids = new long[]{0l, 0l, 0l};
            }
        }

        Connection db = OPDE.getDb().db;
        try {

            db.setAutoCommit(false);
            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            db.commit();

            long rel = 0l;

            if (activeControls > 1) {
                rel = OPDE.getDb().getUID("__bwerte");
            }

            for (int sort = 0; sort < activeControls; sort++) {
                hm.put("Wert", werte[sort]);
                hm.put("XML", xmls[sort]);
                hm.put("Sortierung", sort);
                hm.put("Beziehung", rel);
                hm.put("ReplacementFor", bwids[sort]);

                long newbwid = op.tools.DBHandling.insertRecord("BWerte", hm);

                if (edit) {
                    if (newbwid > 0) { // weitere Verarbeitung nur dann, wenn Insert funktioniert hat.
                        // alten Eintrag ändern und verstecken
                        HashMap oldrecord = new HashMap();
                        oldrecord.put("_mdate", "!NOW!");
                        oldrecord.put("EditBy", OPDE.getLogin().getUser().getUKennung());
                        oldrecord.put("ReplacedBy", newbwid);
                        if (!op.tools.DBHandling.updateRecord("BWerte", oldrecord, "BWID", bwids[sort])) {
                            throw new SQLException("update");
                        }
                    } else {
                        throw new SQLException("insert");
                    }

                    // Vorgänge umbiegen
                    String sql1 = "UPDATE VorgangAssign SET ForeignKey = ? WHERE ForeignKey = ? AND TableName = 'BWerte'";
                    PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(sql1);
                    stmt1.setLong(1, newbwid);
                    stmt1.setLong(2, bwids[sort]);
                    stmt1.executeUpdate();

                    // Dateien umbiegen
//                    String sql2 = "UPDATE OCFilesAssign SET ForeignKey = ? WHERE ForeignKey = ? AND TableName = 'BWerte'";
//                    PreparedStatement stmt2 = OPDE.getDb().db.prepareStatement(sql2);
//                    stmt2.setLong(1, newbwid);
//                    stmt2.setLong(2, bwids[sort]);
//                    stmt2.executeUpdate();

                }
            }

            db.commit();
            db.setAutoCommit(true);

        } catch (SQLException ex) {

            try {
                db.rollback();
                new DlgException(ex);
            } catch (SQLException ex1) {
                new DlgException(ex1);
                ex1.printStackTrace();
                System.exit(1);
            }

        }

        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnDiscardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiscardActionPerformed
        dispose();
    }//GEN-LAST:event_btnDiscardActionPerformed

    private void txtWert3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtWert3ActionPerformed
        txtBemerkung.requestFocus();
    }//GEN-LAST:event_txtWert3ActionPerformed

    private void txtWert2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtWert2ActionPerformed
        if (this.activeControls == 2) {
            txtBemerkung.requestFocus();
        }

        if (this.activeControls == 3) {
            txtWert3.requestFocus();
        }
//if (this.activeControls == 1) btnSave.doClick();
    }//GEN-LAST:event_txtWert2ActionPerformed

    private void txtWert1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtWert1ActionPerformed
        txtWert2.requestFocus();
    }//GEN-LAST:event_txtWert1ActionPerformed

    private void txtUhrzeitFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUhrzeitFocusLost
        JTextField txt = (JTextField) evt.getComponent();
        String t = txt.getText();
        try {
            GregorianCalendar gc = SYSCalendar.erkenneUhrzeit(t);
            txt.setText(SYSCalendar.toGermanTime(gc));
        } catch (NumberFormatException nfe) {
            txt.setText(SYSCalendar.toGermanTime(new GregorianCalendar()));
            txt.requestFocusInWindow();
        }
    }//GEN-LAST:event_txtUhrzeitFocusLost

    private void txtDatumFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDatumFocusLost
        JTextField txt = (JTextField) evt.getComponent();
        String t = txt.getText();
        try {
            GregorianCalendar gc = SYSCalendar.erkenneDatum(t);
            txt.setText(SYSCalendar.printGCGermanStyle(gc));
        } catch (NumberFormatException nfe) {
            txt.setText(SYSCalendar.printGCGermanStyle(new GregorianCalendar()));
            txt.requestFocusInWindow();
        }
    }//GEN-LAST:event_txtDatumFocusLost

    private void txtUhrzeitFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUhrzeitFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtUhrzeitFocusGained

    private void txtDatumFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDatumFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtDatumFocusGained

    private void txtWert3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWert3FocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtWert3FocusGained

    private void txtWert2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWert2FocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtWert2FocusGained

    private void txtWert1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWert1FocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtWert1FocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDiscard;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEinheit1;
    private javax.swing.JLabel lblEinheit2;
    private javax.swing.JLabel lblEinheit3;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblVital;
    private javax.swing.JLabel lblWert1;
    private javax.swing.JLabel lblWert2;
    private javax.swing.JLabel lblWert3;
    private javax.swing.JTextArea txtBemerkung;
    private javax.swing.JTextField txtDatum;
    private javax.swing.JTextField txtUhrzeit;
    private javax.swing.JTextField txtWert1;
    private javax.swing.JTextField txtWert2;
    private javax.swing.JTextField txtWert3;
    // End of variables declaration//GEN-END:variables

    private void setActiveControls(int num) {
        this.activeControls = num;
        lblWert1.setVisible(false);
        lblWert2.setVisible(false);
        lblWert3.setVisible(false);
        txtWert1.setVisible(false);
        txtWert2.setVisible(false);
        txtWert3.setVisible(false);
        lblEinheit1.setVisible(false);
        lblEinheit2.setVisible(false);
        lblEinheit3.setVisible(false);

        if (num < 1 || num > 3) {
            return;
        }

        if (num >= 1) {
            lblWert2.setVisible(true);
            txtWert2.setVisible(true);
            lblEinheit2.setVisible(true);
        }

        if (num >= 2) {
            lblWert1.setVisible(true);
            txtWert1.setVisible(true);
            lblEinheit1.setVisible(true);
        }

        if (num == 3) {
            lblWert3.setVisible(true);
            txtWert3.setVisible(true);
            lblEinheit3.setVisible(true);
        }
    }
}
