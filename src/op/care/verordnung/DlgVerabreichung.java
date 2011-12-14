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

import com.toedter.calendar.JDateChooser;
import entity.verordnungen.*;
import op.OPDE;
import op.tools.DBHandling;
import op.tools.DBRetrieve;
import op.tools.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class DlgVerabreichung extends javax.swing.JDialog {

    private HashMap template;
    private JRadioButton dummy;
    private String tage[] = {"Mon", "Die", "Mit", "Don", "Fre", "Sam", "Son"};
    private boolean ignoreEvent = false;
    private Component parent;
    private JDCPropertyChangeListener jdcpcl;
    private Verordnung verordnung;
    private VerordnungPlanung planung;


    /**
     * für Neueingaben
     */
    public DlgVerabreichung(java.awt.Frame parent, Verordnung verordnung) {
        this(parent, null, verordnung);
    }

    public DlgVerabreichung(JDialog parent, Verordnung verordnung) {
        this(parent, null, verordnung);
    }

    /**
     * für Änderungen
     */
    public DlgVerabreichung(java.awt.Frame parent, VerordnungPlanung planung, Verordnung verordnung) {
        super(parent, true);
        this.parent = parent;
        this.verordnung = verordnung;
        this.planung = planung;
        initDialog();
    }

    public DlgVerabreichung(JDialog parent, VerordnungPlanung planung, Verordnung verordnung) {
        super(parent, true);
        this.parent = parent;
        this.verordnung = verordnung;
        this.planung = planung;
        initDialog();
    }

    private void initDialog() {

        initComponents();
        initData();

        String einheit = "x";
        if (verordnung.getDarreichung() != null) {
            einheit = MedFormenTools.EINHEIT[verordnung.getDarreichung().getMedForm().getAnwEinheit()];
        }

        lblEin1.setText("Einheit: " + einheit);

        this.setTitle(SYSTools.getWindowTitle("Dosierung/Häufigkeit"));
        int selectIndex = 0;

        dummy = new JRadioButton();
        bgMonat.add(dummy);

        cmbUhrzeit.setModel(new DefaultComboBoxModel(SYSCalendar.fillUhrzeiten().toArray()));

        ignoreEvent = true;
        if (verordnung.isBedarf()) {
            txtMaxTimes.setText((template.get("MaxAnzahl")).toString());
            txtEDosis.setText(Double.toString(Double.parseDouble(template.get("MaxEDosis").toString())));
        } else {
            txtMaxTimes.setText("0");
            txtEDosis.setText("0.0");
        }
        lblDosis.setEnabled(verordnung.isBedarf());
        lblX.setEnabled(verordnung.isBedarf());

        if (template.get("Uhrzeit") == null) {
            cmbUhrzeit.setSelectedIndex(-1);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            SYSTools.selectInComboBox(cmbUhrzeit, sdf.format((Time) template.get("Uhrzeit")), true);
        }
        spinTaeglich.setModel(new SpinnerNumberModel(0, 0, 365, 1));
        spinWoche.setModel(new SpinnerNumberModel(0, 0, 52, 1));
        spinMonat.setModel(new SpinnerNumberModel(0, 0, 12, 1));
        spinMonatTag.setModel(new SpinnerNumberModel(1, 0, 31, 1));
        spinMonatWTag.setModel(new SpinnerNumberModel(0, 0, 5, 1));

        for (int i = 0; i < 7; i++) {
            if (template.containsKey(tage[i])) {
                selectIndex = i;
            }
        }

        spinTaeglich.setValue(template.get("Taeglich"));
        rbTag.setSelected(Integer.parseInt(spinTaeglich.getValue().toString()) > 0);
        spinWoche.setValue(template.get("Woechentlich"));
        rbWoche.setSelected(Integer.parseInt(spinWoche.getValue().toString()) > 0);
        spinMonat.setValue(template.get("Monatlich"));
        rbMonat.setSelected(Integer.parseInt(spinMonat.getValue().toString()) > 0);
        spinMonatTag.setValue(template.get("TagNum"));


        if (((Integer) template.get("Woechentlich")).intValue() > 0) {
            cbMon.setSelected(((Integer) template.get("Mon")).intValue() > 0);
            cbDie.setSelected(((Integer) template.get("Die")).intValue() > 0);
            cbMit.setSelected(((Integer) template.get("Mit")).intValue() > 0);
            cbDon.setSelected(((Integer) template.get("Don")).intValue() > 0);
            cbFre.setSelected(((Integer) template.get("Fre")).intValue() > 0);
            cbSam.setSelected(((Integer) template.get("Sam")).intValue() > 0);
            cbSon.setSelected(((Integer) template.get("Son")).intValue() > 0);
            rbWoche.setSelected(true);
        }

        if (((Integer) template.get("Monatlich")).intValue() > 0) {
            if (((Integer) template.get("TagNum")).intValue() > 0) {
                spinMonatTag.setValue(template.get("TagNum"));
                rbMonatTag.setSelected(true);
            } else {
                cmbWTag.setSelectedIndex(selectIndex);
                spinMonatWTag.setValue(template.get(tage[selectIndex]));
                rbMonatWTag.setSelected(true);
            }
            rbMonat.setSelected(true);
        }

        if (((Integer) template.get("Taeglich")).intValue() > 0) {
            rbTag.setSelected(true);
        }

        jdcLDatum.setMinSelectableDate(new Date());
        jdcLDatum.setDate(new Date(Math.max(((Timestamp) template.get("LDatum")).getTime(), SYSCalendar.startOfDay())));
        jdcpcl = new JDCPropertyChangeListener();
        jdcLDatum.getDateEditor().addPropertyChangeListener(jdcpcl);

        dummy.setSelected(true);
        rbMonatTag.setSelected(Integer.parseInt(spinMonatTag.getValue().toString()) > 0);
        rbMonatWTag.setSelected(Integer.parseInt(spinMonatWTag.getValue().toString()) > 0);

        txtNachtMo.setEnabled(!verordnung.isBedarf());
        txtMorgens.setEnabled(!verordnung.isBedarf());
        txtMittags.setEnabled(!verordnung.isBedarf());
        txtNachmittags.setEnabled(!verordnung.isBedarf());
        txtAbends.setEnabled(!verordnung.isBedarf());
        txtUhrzeit.setEnabled(!verordnung.isBedarf());
        txtNachtAb.setEnabled(!verordnung.isBedarf());
        cmbUhrzeit.setEnabled(!verordnung.isBedarf());
        txtMaxTimes.setEnabled(verordnung.isBedarf());
        txtEDosis.setEnabled(verordnung.isBedarf());

        txtMorgens.setBackground(SYSConst.lightblue);
        txtMittags.setBackground(SYSConst.gold7);
        txtNachmittags.setBackground(SYSConst.melonrindgreen);
        txtAbends.setBackground(SYSConst.bermuda_sand);
        txtNachtAb.setBackground(SYSConst.bluegrey);

        SYSTools.setXEnabled(pnlWdh, !verordnung.isBedarf());
        cmbWTag.setEnabled(!verordnung.isBedarf());

        jdcLDatum.setEnabled(!verordnung.isBedarf());

        if (verordnung.isBedarf()) {
            txtEDosis.requestFocus();
        } else {
            txtMorgens.requestFocus();
        }


        ignoreEvent = false;
        pack();
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
        jPanel3 = new JPanel();
        jPanel2 = new JPanel();
        lblDosis = new JLabel();
        txtMaxTimes = new JTextField();
        lblX = new JLabel();
        txtEDosis = new JTextField();
        lblEin1 = new JLabel();
        pnlRegular = new JPanel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jLabel6 = new JLabel();
        cmbUhrzeit = new JComboBox();
        jLabel11 = new JLabel();
        txtNachtMo = new JTextField();
        txtNachtAb = new JTextField();
        txtMittags = new JTextField();
        txtMorgens = new JTextField();
        txtNachmittags = new JTextField();
        txtAbends = new JTextField();
        txtUhrzeit = new JTextField();
        pnlWdh = new JPanel();
        rbTag = new JRadioButton();
        rbWoche = new JRadioButton();
        rbMonat = new JRadioButton();
        jLabel7 = new JLabel();
        jLabel8 = new JLabel();
        cbMon = new JCheckBox();
        cbDie = new JCheckBox();
        cbMit = new JCheckBox();
        cbDon = new JCheckBox();
        cbFre = new JCheckBox();
        cbSam = new JCheckBox();
        cbSon = new JCheckBox();
        jLabel9 = new JLabel();
        rbMonatTag = new JRadioButton();
        jLabel10 = new JLabel();
        rbMonatWTag = new JRadioButton();
        cmbWTag = new JComboBox();
        spinTaeglich = new JSpinner();
        spinWoche = new JSpinner();
        spinMonat = new JSpinner();
        spinMonatTag = new JSpinner();
        spinMonatWTag = new JSpinner();
        jLabel13 = new JLabel();
        jdcLDatum = new JDateChooser();
        jPanel1 = new JPanel();
        btnDiscard = new JButton();
        btnSave = new JButton();
        bgWdh = new ButtonGroup();
        bgMonat = new ButtonGroup();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();

        //======== jPanel3 ========
        {

            //======== jPanel2 ========
            {
                jPanel2.setBorder(new TitledBorder("Dosierung (bei Bedarf)"));

                //---- lblDosis ----
                lblDosis.setText("Max. Tagesdosis:");

                //---- txtMaxTimes ----
                txtMaxTimes.setHorizontalAlignment(SwingConstants.RIGHT);
                txtMaxTimes.setText("1");
                txtMaxTimes.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtMaxTimesCaretUpdate(e);
                    }
                });
                txtMaxTimes.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtMaxTimesActionPerformed(e);
                    }
                });
                txtMaxTimes.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtMaxTimesFocusGained(e);
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtMaxTimesFocusLost(e);
                    }
                });

                //---- lblX ----
                lblX.setText("x");

                //---- txtEDosis ----
                txtEDosis.setHorizontalAlignment(SwingConstants.RIGHT);
                txtEDosis.setText("1.0");
                txtEDosis.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtEDosisCaretUpdate(e);
                    }
                });
                txtEDosis.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtEDosisActionPerformed(e);
                    }
                });
                txtEDosis.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtEDosisFocusGained(e);
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtEDosisFocusLost(e);
                    }
                });

                GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                    jPanel2Layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addContainerGap(18, Short.MAX_VALUE)
                            .addComponent(lblDosis)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtMaxTimes, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lblX)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtEDosis, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                );
                jPanel2Layout.setVerticalGroup(
                    jPanel2Layout.createParallelGroup()
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel2Layout.createParallelGroup()
                                .addComponent(txtEDosis, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblDosis)
                                    .addComponent(txtMaxTimes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblX)))
                            .addContainerGap(59, Short.MAX_VALUE))
                );
            }

            //---- lblEin1 ----
            lblEin1.setForeground(new Color(51, 51, 255));
            lblEin1.setText("Einheit:");

            //======== pnlRegular ========
            {
                pnlRegular.setBorder(new TitledBorder("Dosierung, H\u00e4ufigkeit (Regelm\u00e4\u00dfig)"));

                //---- jLabel1 ----
                jLabel1.setForeground(new Color(0, 0, 204));
                jLabel1.setText("Morgens:");

                //---- jLabel2 ----
                jLabel2.setForeground(new Color(255, 102, 0));
                jLabel2.setText("Mittags:");

                //---- jLabel3 ----
                jLabel3.setForeground(new Color(255, 0, 51));
                jLabel3.setText("Abends:");

                //---- jLabel4 ----
                jLabel4.setText("Nacht, sp\u00e4t abends:");

                //---- jLabel6 ----
                jLabel6.setText("Nachts, fr\u00fch morgens:");

                //---- cmbUhrzeit ----
                cmbUhrzeit.setModel(new DefaultComboBoxModel(new String[] {
                    "10:00",
                    "10:15",
                    "10:30",
                    "10:45"
                }));
                cmbUhrzeit.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cmbUhrzeitItemStateChanged(e);
                    }
                });

                //---- jLabel11 ----
                jLabel11.setForeground(new Color(0, 153, 51));
                jLabel11.setText("Nachmittag:");

                //---- txtNachtMo ----
                txtNachtMo.setHorizontalAlignment(SwingConstants.RIGHT);
                txtNachtMo.setText("0.0");
                txtNachtMo.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtNachtMoCaretUpdate(e);
                    }
                });
                txtNachtMo.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtNachtMoActionPerformed(e);
                    }
                });
                txtNachtMo.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtFocusGained(e);
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtNachtMoFocusLost(e);
                    }
                });

                //---- txtNachtAb ----
                txtNachtAb.setHorizontalAlignment(SwingConstants.RIGHT);
                txtNachtAb.setText("0.0");
                txtNachtAb.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtNachtAbCaretUpdate(e);
                    }
                });
                txtNachtAb.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtNachtAbActionPerformed(e);
                    }
                });
                txtNachtAb.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtNachtAbFocusGained(e);
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtNachtAbFocusLost(e);
                    }
                });

                //---- txtMittags ----
                txtMittags.setHorizontalAlignment(SwingConstants.RIGHT);
                txtMittags.setText("0.0");
                txtMittags.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtMittagsCaretUpdate(e);
                    }
                });
                txtMittags.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtMittagsActionPerformed(e);
                    }
                });
                txtMittags.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtMittagsFocusGained(e);
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtMittagsFocusLost(e);
                    }
                });

                //---- txtMorgens ----
                txtMorgens.setHorizontalAlignment(SwingConstants.RIGHT);
                txtMorgens.setText("1.0");
                txtMorgens.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtMorgensCaretUpdate(e);
                    }
                });
                txtMorgens.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtMorgensActionPerformed(e);
                    }
                });
                txtMorgens.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtMorgensFocusGained(e);
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtMorgensFocusLost(e);
                    }
                });

                //---- txtNachmittags ----
                txtNachmittags.setHorizontalAlignment(SwingConstants.RIGHT);
                txtNachmittags.setText("0.0");
                txtNachmittags.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtNachmittagsCaretUpdate(e);
                    }
                });
                txtNachmittags.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtNachmittagsActionPerformed(e);
                    }
                });
                txtNachmittags.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtNachmittagsFocusGained(e);
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtNachmittagsFocusLost(e);
                    }
                });

                //---- txtAbends ----
                txtAbends.setHorizontalAlignment(SwingConstants.RIGHT);
                txtAbends.setText("0.0");
                txtAbends.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtAbendsCaretUpdate(e);
                    }
                });
                txtAbends.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtAbendsActionPerformed(e);
                    }
                });
                txtAbends.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtAbendsFocusGained(e);
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtAbendsFocusLost(e);
                    }
                });

                //---- txtUhrzeit ----
                txtUhrzeit.setHorizontalAlignment(SwingConstants.RIGHT);
                txtUhrzeit.setText("0.0");
                txtUhrzeit.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtUhrzeitCaretUpdate(e);
                    }
                });
                txtUhrzeit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtUhrzeitActionPerformed(e);
                    }
                });
                txtUhrzeit.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtUhrzeitFocusGained(e);
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtUhrzeitFocusLost(e);
                    }
                });

                GroupLayout pnlRegularLayout = new GroupLayout(pnlRegular);
                pnlRegular.setLayout(pnlRegularLayout);
                pnlRegularLayout.setHorizontalGroup(
                    pnlRegularLayout.createParallelGroup()
                        .addGroup(pnlRegularLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel4)
                                .addComponent(jLabel3)
                                .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmbUhrzeit, GroupLayout.Alignment.TRAILING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel1, GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel11, GroupLayout.Alignment.TRAILING)))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(txtNachtMo, GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                                .addComponent(txtMorgens, GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                                .addComponent(txtMittags, GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                                .addComponent(txtAbends, GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                                .addComponent(txtNachtAb, GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                                .addComponent(txtUhrzeit, GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                                .addComponent(txtNachmittags, GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
                            .addContainerGap())
                );
                pnlRegularLayout.setVerticalGroup(
                    pnlRegularLayout.createParallelGroup()
                        .addGroup(pnlRegularLayout.createSequentialGroup()
                            .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(txtNachtMo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGap(6, 6, 6)
                            .addGroup(pnlRegularLayout.createParallelGroup()
                                .addGroup(pnlRegularLayout.createSequentialGroup()
                                    .addGap(76, 76, 76)
                                    .addComponent(jLabel11))
                                .addGroup(pnlRegularLayout.createSequentialGroup()
                                    .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtMorgens, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtMittags, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtNachmittags, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(txtAbends, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3))
                            .addGap(2, 2, 2)
                            .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(txtNachtAb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(txtUhrzeit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmbUhrzeit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addContainerGap(26, Short.MAX_VALUE))
                );
            }

            //======== pnlWdh ========
            {
                pnlWdh.setBorder(new TitledBorder("Wiederholungen"));

                //---- rbTag ----
                rbTag.setText("t\u00e4glich alle");
                rbTag.setBorder(BorderFactory.createEmptyBorder());
                rbTag.setMargin(new Insets(0, 0, 0, 0));
                rbTag.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        rbTagActionPerformed(e);
                    }
                });

                //---- rbWoche ----
                rbWoche.setText("w\u00f6chentlich alle");
                rbWoche.setBorder(BorderFactory.createEmptyBorder());
                rbWoche.setMargin(new Insets(0, 0, 0, 0));
                rbWoche.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        rbWocheActionPerformed(e);
                    }
                });

                //---- rbMonat ----
                rbMonat.setText("monatlich alle");
                rbMonat.setBorder(BorderFactory.createEmptyBorder());
                rbMonat.setMargin(new Insets(0, 0, 0, 0));
                rbMonat.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        rbMonatActionPerformed(e);
                    }
                });

                //---- jLabel7 ----
                jLabel7.setText("Tage");

                //---- jLabel8 ----
                jLabel8.setText("Wochen am:");

                //---- cbMon ----
                cbMon.setText("Mon");
                cbMon.setBorder(BorderFactory.createEmptyBorder());
                cbMon.setMargin(new Insets(0, 0, 0, 0));
                cbMon.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbMonActionPerformed(e);
                    }
                });

                //---- cbDie ----
                cbDie.setText("Die");
                cbDie.setBorder(BorderFactory.createEmptyBorder());
                cbDie.setMargin(new Insets(0, 0, 0, 0));
                cbDie.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbDieActionPerformed(e);
                    }
                });

                //---- cbMit ----
                cbMit.setText("Mit");
                cbMit.setBorder(BorderFactory.createEmptyBorder());
                cbMit.setMargin(new Insets(0, 0, 0, 0));
                cbMit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbMitActionPerformed(e);
                    }
                });

                //---- cbDon ----
                cbDon.setText("Don");
                cbDon.setBorder(BorderFactory.createEmptyBorder());
                cbDon.setMargin(new Insets(0, 0, 0, 0));
                cbDon.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbDonActionPerformed(e);
                    }
                });

                //---- cbFre ----
                cbFre.setText("Fre");
                cbFre.setBorder(BorderFactory.createEmptyBorder());
                cbFre.setMargin(new Insets(0, 0, 0, 0));
                cbFre.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbFreActionPerformed(e);
                    }
                });

                //---- cbSam ----
                cbSam.setText("Sam");
                cbSam.setBorder(BorderFactory.createEmptyBorder());
                cbSam.setMargin(new Insets(0, 0, 0, 0));
                cbSam.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbSamActionPerformed(e);
                    }
                });

                //---- cbSon ----
                cbSon.setText("Son");
                cbSon.setBorder(BorderFactory.createEmptyBorder());
                cbSon.setMargin(new Insets(0, 0, 0, 0));
                cbSon.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbSonActionPerformed(e);
                    }
                });

                //---- jLabel9 ----
                jLabel9.setText("Monat(e)");

                //---- rbMonatTag ----
                rbMonatTag.setText("wiederholt am");
                rbMonatTag.setBorder(BorderFactory.createEmptyBorder());
                rbMonatTag.setMargin(new Insets(0, 0, 0, 0));
                rbMonatTag.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        rbMonatTagActionPerformed(e);
                    }
                });

                //---- jLabel10 ----
                jLabel10.setText("Tag");

                //---- rbMonatWTag ----
                rbMonatWTag.setText("wiederholt am");
                rbMonatWTag.setBorder(BorderFactory.createEmptyBorder());
                rbMonatWTag.setMargin(new Insets(0, 0, 0, 0));
                rbMonatWTag.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        rbMonatWTagActionPerformed(e);
                    }
                });

                //---- cmbWTag ----
                cmbWTag.setModel(new DefaultComboBoxModel(new String[] {
                    "Montag",
                    "Dienstag",
                    "Mittwoch",
                    "Donnerstag",
                    "Freitag",
                    "Samstag",
                    "Sonntag"
                }));
                cmbWTag.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cmbWTagItemStateChanged(e);
                    }
                });

                //---- spinTaeglich ----
                spinTaeglich.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinTaeglichStateChanged(e);
                    }
                });

                //---- spinWoche ----
                spinWoche.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinWocheStateChanged(e);
                    }
                });

                //---- spinMonat ----
                spinMonat.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinMonatStateChanged(e);
                    }
                });

                //---- spinMonatTag ----
                spinMonatTag.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinMonatTagStateChanged(e);
                    }
                });

                //---- spinMonatWTag ----
                spinMonatWTag.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinMonatWTagStateChanged(e);
                    }
                });

                //---- jLabel13 ----
                jLabel13.setText("Erste Anwendung am:");

                GroupLayout pnlWdhLayout = new GroupLayout(pnlWdh);
                pnlWdh.setLayout(pnlWdhLayout);
                pnlWdhLayout.setHorizontalGroup(
                    pnlWdhLayout.createParallelGroup()
                        .addGroup(pnlWdhLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(pnlWdhLayout.createParallelGroup()
                                .addGroup(pnlWdhLayout.createSequentialGroup()
                                    .addGap(17, 17, 17)
                                    .addGroup(pnlWdhLayout.createParallelGroup()
                                        .addGroup(pnlWdhLayout.createSequentialGroup()
                                            .addComponent(rbMonatTag)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(spinMonatTag, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(pnlWdhLayout.createSequentialGroup()
                                            .addComponent(rbMonatWTag)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(spinMonatWTag, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)))
                                    .addGap(20, 20, 20)
                                    .addGroup(pnlWdhLayout.createParallelGroup()
                                        .addComponent(jLabel10)
                                        .addComponent(cmbWTag, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                .addGroup(pnlWdhLayout.createSequentialGroup()
                                    .addComponent(rbWoche)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(spinWoche, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel8))
                                .addGroup(pnlWdhLayout.createSequentialGroup()
                                    .addComponent(rbTag)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(spinTaeglich, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel7))
                                .addGroup(pnlWdhLayout.createSequentialGroup()
                                    .addGap(17, 17, 17)
                                    .addGroup(pnlWdhLayout.createParallelGroup()
                                        .addGroup(pnlWdhLayout.createSequentialGroup()
                                            .addComponent(cbFre)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(cbSam)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(cbSon))
                                        .addGroup(pnlWdhLayout.createSequentialGroup()
                                            .addComponent(cbMon)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(cbDie)
                                            .addGap(16, 16, 16)
                                            .addComponent(cbMit)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(cbDon))))
                                .addGroup(pnlWdhLayout.createSequentialGroup()
                                    .addComponent(rbMonat)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(spinMonat, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel9))
                                .addGroup(pnlWdhLayout.createSequentialGroup()
                                    .addComponent(jLabel13)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jdcLDatum, GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)))
                            .addContainerGap())
                );
                pnlWdhLayout.setVerticalGroup(
                    pnlWdhLayout.createParallelGroup()
                        .addGroup(pnlWdhLayout.createSequentialGroup()
                            .addGroup(pnlWdhLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(rbTag)
                                .addComponent(spinTaeglich, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7))
                            .addGap(18, 18, 18)
                            .addGroup(pnlWdhLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(rbWoche)
                                .addComponent(spinWoche, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlWdhLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(cbMon)
                                .addComponent(cbDie)
                                .addComponent(cbMit)
                                .addComponent(cbDon))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlWdhLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(cbFre)
                                .addComponent(cbSam)
                                .addComponent(cbSon))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlWdhLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(rbMonat)
                                .addComponent(jLabel9)
                                .addComponent(spinMonat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlWdhLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(rbMonatTag)
                                .addComponent(spinMonatTag, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel10))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlWdhLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(rbMonatWTag)
                                .addComponent(spinMonatWTag, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmbWTag, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlWdhLayout.createParallelGroup()
                                .addGroup(pnlWdhLayout.createSequentialGroup()
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel13))
                                .addGroup(pnlWdhLayout.createSequentialGroup()
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jdcLDatum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                            .addContainerGap(138, Short.MAX_VALUE))
                );
            }

            GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup()
                    .addGroup(jPanel3Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel3Layout.createParallelGroup()
                                    .addComponent(pnlRegular, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblEin1, GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(pnlWdh, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            );
            jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup()
                    .addGroup(jPanel3Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addComponent(pnlWdh, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                            .addComponent(lblEin1)
                                            .addGap(1, 1, 1)
                                            .addComponent(pnlRegular, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                            .addContainerGap())
            );
        }

        //======== jPanel1 ========
        {

            //---- btnDiscard ----
            btnDiscard.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnDiscard.setText("Verwerfen");
            btnDiscard.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnDiscardActionPerformed(e);
                }
            });

            //---- btnSave ----
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.setText("Speichern");
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnSave)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDiscard)
                        .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(btnDiscard)
                            .addComponent(btnSave))
                        .addContainerGap())
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .addComponent(jPanel1, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        setSize(739, 482);
        setLocationRelativeTo(null);

        //---- bgWdh ----
        bgWdh.add(rbTag);
        bgWdh.add(rbWoche);
        bgWdh.add(rbMonat);

        //---- bgMonat ----
        bgMonat.add(rbMonatTag);
        bgMonat.add(rbMonatWTag);
    }// </editor-fold>//GEN-END:initComponents

    private void cmbUhrzeitItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbUhrzeitItemStateChanged
        if (ignoreEvent) {
            return;
        }
        ignoreEvent = true;
        txtNachtMo.setText("0.0");
        txtMorgens.setText("0.0");
        txtMittags.setText("0.0");
        txtNachmittags.setText("0.0");
        txtAbends.setText("0.0");
        txtNachtAb.setText("0.0");

        if (SYSTools.parseDouble(txtUhrzeit.getText()) == 0) {
            txtUhrzeit.setText("1.0");
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cmbUhrzeitItemStateChanged

    public void dispose() {
        SYSTools.unregisterListeners(this);
        jdcLDatum.removePropertyChangeListener(jdcpcl);
        jdcLDatum.cleanup();
        super.dispose();
    }

    private void initData() {
        if (planung == null) {
            planung = new VerordnungPlanung(verordnung);
        }
    }

    private void rbMonatWTagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbMonatWTagActionPerformed
        if (ignoreEvent) {
            return;
        }
        ignoreEvent = true;
        if (!rbMonat.isSelected()) {
            rbMonat.setSelected(true);
            spinMonat.setValue(1);
            rbMonatWTag.setSelected(true);
        }
        spinMonatWTag.setValue(1);
        spinMonatTag.setValue(0);
        cmbWTag.setSelectedIndex(0);
        ignoreEvent = false;
    }//GEN-LAST:event_rbMonatWTagActionPerformed

    private void rbMonatTagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbMonatTagActionPerformed
        if (ignoreEvent) {
            return;
        }
        ignoreEvent = true;
        if (!rbMonat.isSelected()) {
            rbMonat.setSelected(true);
            spinMonat.setValue(1); // BugID #13
            rbMonatTag.setSelected(true);
        }
        spinMonatTag.setValue(1);
        spinMonatWTag.setValue(0);
        cmbWTag.setSelectedIndex(0);
        ignoreEvent = false;
    }//GEN-LAST:event_rbMonatTagActionPerformed

    private void rbMonatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbMonatActionPerformed
        if (ignoreEvent) {
            return;
        }
        ignoreEvent = true;
        spinTaeglich.setValue(0);
        spinWoche.setValue(0);
        cbMon.setSelected(false);
        cbDie.setSelected(false);
        cbMit.setSelected(false);
        cbDon.setSelected(false);
        cbFre.setSelected(false);
        cbSam.setSelected(false);
        cbSon.setSelected(false);
        spinMonat.setValue(1);
        spinMonatTag.setValue(1);
        spinMonatWTag.setValue(0);
        cmbWTag.setSelectedIndex(0);
        rbMonatTag.setSelected(true);
        ignoreEvent = false;
    }//GEN-LAST:event_rbMonatActionPerformed

    private void rbWocheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbWocheActionPerformed
        if (ignoreEvent) {
            return;
        }
        ignoreEvent = true;

        spinTaeglich.setValue(0);
        spinWoche.setValue(1);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            cbMon.setSelected(true);
        }

        dummy.setSelected(true);
        spinMonat.setValue(0);
        spinMonatTag.setValue(0);
        spinMonatWTag.setValue(0);
        cmbWTag.setSelectedIndex(0);
        ignoreEvent = false;
    }//GEN-LAST:event_rbWocheActionPerformed

    private void rbTagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbTagActionPerformed
        if (ignoreEvent) {
            return;
        }
        ignoreEvent = true;
        spinTaeglich.setValue(1);
        //spinTaeglich.getModel().
        spinWoche.setValue(0);
        cbMon.setSelected(false);
        cbDie.setSelected(false);
        cbMit.setSelected(false);
        cbDon.setSelected(false);
        cbFre.setSelected(false);
        cbSam.setSelected(false);
        cbSon.setSelected(false);
        dummy.setSelected(true);
        spinMonat.setValue(0);
        spinMonatTag.setValue(0);
        spinMonatWTag.setValue(0);
        cmbWTag.setSelectedIndex(0);
        ignoreEvent = false;
    }//GEN-LAST:event_rbTagActionPerformed

    private void cbSonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSonActionPerformed
        if (ignoreEvent) {
            return;
        }
        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }
        ignoreEvent = true;

        dummy.setSelected(true);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            JOptionPane.showMessageDialog(this, "Sie müssen mindestens einen Wochentag angeben.");
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cbSonActionPerformed

    private void cbSamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSamActionPerformed
        if (ignoreEvent) {
            return;
        }
        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }
        ignoreEvent = true;

        dummy.setSelected(true);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            JOptionPane.showMessageDialog(this, "Sie müssen mindestens einen Wochentag angeben.");
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cbSamActionPerformed

    private void cbFreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFreActionPerformed
        if (ignoreEvent) {
            return;
        }
        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }
        ignoreEvent = true;

        dummy.setSelected(true);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            JOptionPane.showMessageDialog(this, "Sie müssen mindestens einen Wochentag angeben.");
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cbFreActionPerformed

    private void cbDonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDonActionPerformed
        if (ignoreEvent) {
            return;
        }
        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }
        ignoreEvent = true;

        dummy.setSelected(true);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            JOptionPane.showMessageDialog(this, "Sie müssen mindestens einen Wochentag angeben.");
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cbDonActionPerformed

    private void cbMitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMitActionPerformed
        if (ignoreEvent) {
            return;
        }
        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }
        ignoreEvent = true;

        dummy.setSelected(true);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            JOptionPane.showMessageDialog(this, "Sie müssen mindestens einen Wochentag angeben.");
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cbMitActionPerformed

    private void cbDieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDieActionPerformed
        if (ignoreEvent) {
            return;
        }
        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }
        ignoreEvent = true;

        dummy.setSelected(true);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            JOptionPane.showMessageDialog(this, "Sie müssen mindestens einen Wochentag angeben.");
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cbDieActionPerformed

    private void cbMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMonActionPerformed
        if (ignoreEvent) {
            return;
        }

        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }
        ignoreEvent = true;
        dummy.setSelected(true);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            JOptionPane.showMessageDialog(this, "Sie müssen mindestens einen Wochentag angeben.");
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cbMonActionPerformed

    private void cmbWTagItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbWTagItemStateChanged
        if (ignoreEvent) {
            return;
        }

        if (!rbMonat.isSelected()) {
            rbMonat.doClick();
        }


        if (!rbMonatWTag.isSelected()) {
            rbMonatWTag.doClick();
            //spinMonatWTag.setValue(1);
        }
    }//GEN-LAST:event_cmbWTagItemStateChanged

    private void spinMonatWTagStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinMonatWTagStateChanged
        if (ignoreEvent) {
            return;
        }

        if (!rbMonat.isSelected()) {
            rbMonat.doClick();
        }
        if (!rbMonatWTag.isSelected()) {
            rbMonatWTag.doClick();
        }

        ignoreEvent = true;
        int monat = Integer.parseInt(spinMonat.getValue().toString());
        if (monat == 0) {
            spinMonat.setValue(1);
        }
        ignoreEvent = false;
    }//GEN-LAST:event_spinMonatWTagStateChanged

    private void spinMonatTagStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinMonatTagStateChanged
        if (ignoreEvent) {
            return;
        }

        if (!rbMonat.isSelected()) {
            rbMonat.doClick();
        }
        if (!rbMonatTag.isSelected()) {
            rbMonatTag.doClick();
        }
        ignoreEvent = true;
        rbMonatTag.setSelected(true);
        //if (!rbMonatTag.isSelected()) rbMonatTag.setSelected(true);
        int monat = Integer.parseInt(spinMonat.getValue().toString());
        if (monat == 0) {
            spinMonat.setValue(1);
        }
        ignoreEvent = false;
    }//GEN-LAST:event_spinMonatTagStateChanged

    private void spinMonatStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinMonatStateChanged
        if (ignoreEvent) {
            return;
        }

        if (!rbMonat.isSelected()) {
            rbMonat.doClick();
        }
        ignoreEvent = true;
        int monat = Integer.parseInt(spinMonat.getValue().toString());
        if (monat == 0) {
            spinMonat.setValue(1);
        }
        ignoreEvent = false;
    }//GEN-LAST:event_spinMonatStateChanged

    private void spinWocheStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinWocheStateChanged
        if (ignoreEvent) {
            return;
        }

        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }

        ignoreEvent = true;
        dummy.setSelected(true);
        int woche = Integer.parseInt(spinWoche.getValue().toString());
        if (woche == 0) {
            spinWoche.setValue(1);
        }
        ignoreEvent = false;
    }//GEN-LAST:event_spinWocheStateChanged

    private void spinTaeglichStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinTaeglichStateChanged
        if (ignoreEvent) {
            return;
        }

        if (!rbTag.isSelected()) {
            rbTag.doClick();
        }
        ignoreEvent = true;
        dummy.setSelected(true);
        ignoreEvent = false;
    }//GEN-LAST:event_spinTaeglichStateChanged

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
        this.setVisible(false);
    }//GEN-LAST:event_btnSaveActionPerformed

    public void save() {
        //HashMap hm = new HashMap();

        hm.put("NachtMo", txtNachtMo.getText());
        hm.put("Morgens", txtMorgens.getText());
        hm.put("Mittags", txtMittags.getText());
        hm.put("Nachmittags", txtNachmittags.getText());
        hm.put("Abends", txtAbends.getText());
        hm.put("NachtAb", txtNachtAb.getText());
        hm.put("UhrzeitDosis", txtUhrzeit.getText());
        ListElement e = (ListElement) cmbUhrzeit.getSelectedItem();

        if (e == null) {
            hm.put("Uhrzeit", null);
        } else {
            hm.put("Uhrzeit", new Timestamp(((GregorianCalendar) e.getObject()).getTimeInMillis()));
        }
        hm.put("MaxAnzahl", txtMaxTimes.getText());
        hm.put("MaxEDosis", txtEDosis.getText());

        hm.put("Taeglich", spinTaeglich.getValue());
        hm.put("Woechentlich", spinWoche.getValue());
        hm.put("Monatlich", spinMonat.getValue());
        hm.put("TagNum", spinMonatTag.getValue());

        hm.put("LDatum", jdcLDatum.getDate());

        if (cbSon.isSelected()) {
            hm.put("Son", 1);
        } else {
            hm.put("Son", 0);
        }
        if (cbSam.isSelected()) {
            hm.put("Sam", 1);
        } else {
            hm.put("Sam", 0);
        }
        if (cbFre.isSelected()) {
            hm.put("Fre", 1);
        } else {
            hm.put("Fre", 0);
        }
        if (cbDon.isSelected()) {
            hm.put("Don", 1);
        } else {
            hm.put("Don", 0);
        }
        if (cbMit.isSelected()) {
            hm.put("Mit", 1);
        } else {
            hm.put("Mit", 0);
        }
        if (cbDie.isSelected()) {
            hm.put("Die", 1);
        } else {
            hm.put("Die", 0);
        }
        if (cbMon.isSelected()) {
            hm.put("Mon", 1);
        } else {
            hm.put("Mon", 0);
        }

        if (rbMonatWTag.isSelected()) {
            hm.put(tage[cmbWTag.getSelectedIndex()], spinMonatWTag.getValue());
        }

        if (bhppid == 0) {
            hm.put("VerID", verid);
            hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
            hm.put("tmp", OPDE.getLogin().getLoginID());
            DBHandling.insertRecord("BHPPlanung", hm);
        } else {
            DBHandling.updateRecord("BHPPlanung", hm, "BHPPID", bhppid);
        }
    }

    private void txtZeitCaretUpdate(javax.swing.event.CaretEvent evt) {
        if (ignoreEvent) {
            return;
        }
        try {
            Double.parseDouble(((JTextField) evt.getSource()).getText());
            btnSave.setEnabled(true);
        } catch (NumberFormatException nfe) {
            btnSave.setEnabled(false);
        }
    }

    private void btnDiscardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiscardActionPerformed
        this.template = null;
        this.setVisible(false);
    }//GEN-LAST:event_btnDiscardActionPerformed

    private void txtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFocusGained
        ignoreEvent = true;
        btnSave.setEnabled(true);
        SYSTools.markAllTxt((JTextField) evt.getSource());
        ignoreEvent = false;
    }//GEN-LAST:event_txtFocusGained

    private void txtMaxTimesCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtMaxTimesCaretUpdate
        if (ignoreEvent) {
            return;
        }
        try {
            Integer.parseInt(((JTextField) evt.getSource()).getText());
            btnSave.setEnabled(true);
        } catch (NumberFormatException nfe) {
            btnSave.setEnabled(false);
        }
    }//GEN-LAST:event_txtMaxTimesCaretUpdate

    private void txtEDosisCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtEDosisCaretUpdate
        txtZeitCaretUpdate(evt);
    }//GEN-LAST:event_txtEDosisCaretUpdate

    private void txtNachtMoCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtNachtMoCaretUpdate
        txtZeitCaretUpdate(evt);
    }//GEN-LAST:event_txtNachtMoCaretUpdate

    private void txtMorgensCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtMorgensCaretUpdate
        txtZeitCaretUpdate(evt);
    }//GEN-LAST:event_txtMorgensCaretUpdate

    private void txtMittagsCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtMittagsCaretUpdate
        txtZeitCaretUpdate(evt);
    }//GEN-LAST:event_txtMittagsCaretUpdate

    private void txtNachmittagsCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtNachmittagsCaretUpdate
        txtZeitCaretUpdate(evt);
    }//GEN-LAST:event_txtNachmittagsCaretUpdate

    private void txtAbendsCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtAbendsCaretUpdate
        txtZeitCaretUpdate(evt);
    }//GEN-LAST:event_txtAbendsCaretUpdate

    private void txtNachtAbCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtNachtAbCaretUpdate
        txtZeitCaretUpdate(evt);
    }//GEN-LAST:event_txtNachtAbCaretUpdate

    private void txtUhrzeitCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtUhrzeitCaretUpdate
        txtZeitCaretUpdate(evt);
    }//GEN-LAST:event_txtUhrzeitCaretUpdate

    private void txtMorgensFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMorgensFocusGained
        ignoreEvent = true;
        btnSave.setEnabled(true);
        SYSTools.markAllTxt((JTextField) evt.getSource());
        ignoreEvent = false;
    }//GEN-LAST:event_txtMorgensFocusGained

    private void txtMittagsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMittagsFocusGained
        ignoreEvent = true;
        btnSave.setEnabled(true);
        SYSTools.markAllTxt((JTextField) evt.getSource());
        ignoreEvent = false;
    }//GEN-LAST:event_txtMittagsFocusGained

    private void txtNachmittagsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNachmittagsFocusGained
        ignoreEvent = true;
        btnSave.setEnabled(true);
        SYSTools.markAllTxt((JTextField) evt.getSource());
        ignoreEvent = false;
    }//GEN-LAST:event_txtNachmittagsFocusGained

    private void txtAbendsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAbendsFocusGained
        ignoreEvent = true;
        btnSave.setEnabled(true);
        SYSTools.markAllTxt((JTextField) evt.getSource());
        ignoreEvent = false;
    }//GEN-LAST:event_txtAbendsFocusGained

    private void txtNachtAbFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNachtAbFocusGained
        ignoreEvent = true;
        btnSave.setEnabled(true);
        SYSTools.markAllTxt((JTextField) evt.getSource());
        ignoreEvent = false;
    }//GEN-LAST:event_txtNachtAbFocusGained

    private void txtUhrzeitFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUhrzeitFocusGained
        ignoreEvent = true;
        btnSave.setEnabled(true);
        SYSTools.markAllTxt((JTextField) evt.getSource());
        ignoreEvent = false;
    }//GEN-LAST:event_txtUhrzeitFocusGained

    private void txtMaxTimesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMaxTimesFocusGained
        ignoreEvent = true;
        btnSave.setEnabled(true);
        SYSTools.markAllTxt((JTextField) evt.getSource());
        ignoreEvent = false;
    }//GEN-LAST:event_txtMaxTimesFocusGained

    private void txtEDosisFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEDosisFocusGained
        ignoreEvent = true;
        btnSave.setEnabled(true);
        SYSTools.markAllTxt((JTextField) evt.getSource());
        ignoreEvent = false;
    }//GEN-LAST:event_txtEDosisFocusGained

    private void txtNachtMoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNachtMoFocusLost
        double d = SYSTools.parseDouble(((JTextField) evt.getSource()).getText());
        ((JTextField) evt.getSource()).setText(Double.toString(d));
        txtZeitFocusLost();
    }//GEN-LAST:event_txtNachtMoFocusLost

    private void txtMorgensFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMorgensFocusLost
        double d = SYSTools.parseDouble(((JTextField) evt.getSource()).getText());
        ((JTextField) evt.getSource()).setText(Double.toString(d));
        txtZeitFocusLost();
    }//GEN-LAST:event_txtMorgensFocusLost

    private void txtMittagsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMittagsFocusLost
        double d = SYSTools.parseDouble(((JTextField) evt.getSource()).getText());
        ((JTextField) evt.getSource()).setText(Double.toString(d));
        txtZeitFocusLost();
    }//GEN-LAST:event_txtMittagsFocusLost

    private void txtNachmittagsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNachmittagsFocusLost
        double d = SYSTools.parseDouble(((JTextField) evt.getSource()).getText());
        ((JTextField) evt.getSource()).setText(Double.toString(d));
        txtZeitFocusLost();
    }//GEN-LAST:event_txtNachmittagsFocusLost

    private void txtAbendsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAbendsFocusLost
        double d = SYSTools.parseDouble(((JTextField) evt.getSource()).getText());
        ((JTextField) evt.getSource()).setText(Double.toString(d));
        txtZeitFocusLost();
    }//GEN-LAST:event_txtAbendsFocusLost

    private void txtNachtAbFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNachtAbFocusLost
        double d = SYSTools.parseDouble(((JTextField) evt.getSource()).getText());
        ((JTextField) evt.getSource()).setText(Double.toString(d));
        txtZeitFocusLost();
    }//GEN-LAST:event_txtNachtAbFocusLost

    private void txtUhrzeitFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUhrzeitFocusLost
        ignoreEvent = true;
        if (!txtUhrzeit.getText().equals("0.0")) {
            double d = SYSTools.parseDouble(txtUhrzeit.getText());
            if (d == 0d) {
                d = 1;
            }
            txtUhrzeit.setText(Double.toString(d));

            txtNachtMo.setText("0.0");
            txtMorgens.setText("0.0");
            txtMittags.setText("0.0");
            txtNachmittags.setText("0.0");
            txtAbends.setText("0.0");
            txtNachtAb.setText("0.0");
            if (cmbUhrzeit.getSelectedIndex() == -1) {
                // cmbUhrzeit auf die nächste volle Stunde ab JETZT setzen.
                GregorianCalendar gc = new GregorianCalendar();
                gc.add(GregorianCalendar.HOUR, 1);
                gc.set(GregorianCalendar.MINUTE, 0);
                gc.set(GregorianCalendar.SECOND, 0);
                gc.set(GregorianCalendar.MILLISECOND, 0);
                SYSTools.selectInComboBox(cmbUhrzeit, SYSCalendar.toGermanTime(gc), true);
            }
            if (Double.parseDouble(txtUhrzeit.getText()) == 0) {
                txtUhrzeit.setText("1.0");
            }

            btnSave.setEnabled(true);
        }
        ignoreEvent = false;
    }//GEN-LAST:event_txtUhrzeitFocusLost

    private void txtNachtMoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNachtMoActionPerformed
        txtMorgens.requestFocus();
    }//GEN-LAST:event_txtNachtMoActionPerformed

    private void txtMorgensActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMorgensActionPerformed
        txtMittags.requestFocus();
    }//GEN-LAST:event_txtMorgensActionPerformed

    private void txtMittagsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMittagsActionPerformed
        txtNachmittags.requestFocus();
    }//GEN-LAST:event_txtMittagsActionPerformed

    private void txtNachmittagsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNachmittagsActionPerformed
        txtAbends.requestFocus();
    }//GEN-LAST:event_txtNachmittagsActionPerformed

    private void txtAbendsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAbendsActionPerformed
        txtNachtAb.requestFocus();
    }//GEN-LAST:event_txtAbendsActionPerformed

    private void txtNachtAbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNachtAbActionPerformed
        txtNachtMo.requestFocus();
    }//GEN-LAST:event_txtNachtAbActionPerformed

    private void txtMaxTimesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaxTimesActionPerformed
        txtEDosis.requestFocus();
    }//GEN-LAST:event_txtMaxTimesActionPerformed

    private void txtEDosisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEDosisActionPerformed
        txtMaxTimes.requestFocus();
    }//GEN-LAST:event_txtEDosisActionPerformed

    private void txtMaxTimesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMaxTimesFocusLost
        ignoreEvent = true;
        int i;
        try {
            i = Integer.parseInt(txtMaxTimes.getText());
            txtMaxTimes.setText(Integer.toString(i));
        } catch (NumberFormatException nfe) {
            txtMaxTimes.setText("1");
        }

        txtNachtMo.setText("0.0");
        txtMorgens.setText("0.0");
        txtMittags.setText("0.0");
        txtNachmittags.setText("0.0");
        txtAbends.setText("0.0");
        txtNachtAb.setText("0.0");
        txtUhrzeit.setText("0.0");
        cmbUhrzeit.setSelectedIndex(-1);

        if (Double.parseDouble(txtEDosis.getText()) == 0) {
            txtEDosis.setText("1.0");
        }

        btnSave.setEnabled(true);

        ignoreEvent = false;
    }//GEN-LAST:event_txtMaxTimesFocusLost

    private void txtEDosisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEDosisFocusLost

        ignoreEvent = true;
        double d = SYSTools.parseDouble(txtEDosis.getText());
        if (d == 0d) {
            txtEDosis.setText("1.0");
        } else {
            txtEDosis.setText(Double.toString(d));
        }

        txtNachtMo.setText("0.0");
        txtMorgens.setText("0.0");
        txtMittags.setText("0.0");
        txtNachmittags.setText("0.0");
        txtAbends.setText("0.0");
        txtNachtAb.setText("0.0");
        txtUhrzeit.setText("0.0");
        cmbUhrzeit.setSelectedIndex(-1);

        btnSave.setEnabled(true);
        ignoreEvent = false;
    }//GEN-LAST:event_txtEDosisFocusLost

    private void txtUhrzeitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUhrzeitActionPerformed
    }//GEN-LAST:event_txtUhrzeitActionPerformed

    private void txtZeitFocusLost() {
        ignoreEvent = true;

        double nachtMo = SYSTools.parseDouble(txtNachtMo.getText());
        double morgens = SYSTools.parseDouble(txtMorgens.getText());
        double mittags = SYSTools.parseDouble(txtMittags.getText());
        double nachmittags = SYSTools.parseDouble(txtNachmittags.getText());
        double abends = SYSTools.parseDouble(txtAbends.getText());
        double nachtAb = SYSTools.parseDouble(txtNachtAb.getText());

        double uhrzeit = SYSTools.parseDouble(txtUhrzeit.getText());

        if (uhrzeit != 0d || cmbUhrzeit.getSelectedIndex() > -1) {
            txtUhrzeit.setText("0.0");
            cmbUhrzeit.setSelectedIndex(-1);
        }

        if (nachtMo + morgens + mittags + nachmittags + abends + nachtAb == 0) {
            txtMorgens.setText("1.0");
        }
        btnSave.setEnabled(true);
        ignoreEvent = false;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel3;
    private JPanel jPanel2;
    private JLabel lblDosis;
    private JTextField txtMaxTimes;
    private JLabel lblX;
    private JTextField txtEDosis;
    private JLabel lblEin1;
    private JPanel pnlRegular;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel6;
    private JComboBox cmbUhrzeit;
    private JLabel jLabel11;
    private JTextField txtNachtMo;
    private JTextField txtNachtAb;
    private JTextField txtMittags;
    private JTextField txtMorgens;
    private JTextField txtNachmittags;
    private JTextField txtAbends;
    private JTextField txtUhrzeit;
    private JPanel pnlWdh;
    private JRadioButton rbTag;
    private JRadioButton rbWoche;
    private JRadioButton rbMonat;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JCheckBox cbMon;
    private JCheckBox cbDie;
    private JCheckBox cbMit;
    private JCheckBox cbDon;
    private JCheckBox cbFre;
    private JCheckBox cbSam;
    private JCheckBox cbSon;
    private JLabel jLabel9;
    private JRadioButton rbMonatTag;
    private JLabel jLabel10;
    private JRadioButton rbMonatWTag;
    private JComboBox cmbWTag;
    private JSpinner spinTaeglich;
    private JSpinner spinWoche;
    private JSpinner spinMonat;
    private JSpinner spinMonatTag;
    private JSpinner spinMonatWTag;
    private JLabel jLabel13;
    private JDateChooser jdcLDatum;
    private JPanel jPanel1;
    private JButton btnDiscard;
    private JButton btnSave;
    private ButtonGroup bgWdh;
    private ButtonGroup bgMonat;
    // End of variables declaration//GEN-END:variables
}
