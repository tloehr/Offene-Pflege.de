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

import java.awt.Component;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import op.OPDE;
import op.tools.ListElement;
import op.tools.DBHandling;
import op.tools.DBRetrieve;
import op.tools.JDCPropertyChangeListener;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

/**
 *
 * @author  tloehr
 */
public class DlgVerabreichung extends javax.swing.JDialog {

    private HashMap template;
    private JRadioButton dummy;
    private long bhppid;
    private String tage[] = {"Mon", "Die", "Mit", "Don", "Fre", "Sam", "Son"};
    private boolean ignoreEvent = false;
    private boolean bedarf;
    private long verid;
    private long dafid;
    private Component parent;
    private JDCPropertyChangeListener jdcpcl;

    /**
     * für Neueingaben
     *
     */
    public DlgVerabreichung(java.awt.Frame parent, long verid, long dafid, boolean bedarf) {
        this(parent, 0, verid, dafid, bedarf);
    }

    public DlgVerabreichung(JDialog parent, long verid, long dafid, boolean bedarf) {
        this(parent, 0, verid, dafid, bedarf);
    }

    /**
     * für Änderungen
     *
     */
    public DlgVerabreichung(java.awt.Frame parent, long bhppid, long verid, long dafid, boolean bedarf) {
        super(parent, true);
        this.parent = parent;
        this.bhppid = bhppid;
        this.verid = verid;
        this.dafid = dafid;
        this.bedarf = bedarf;
        initDialog();
    }

    public DlgVerabreichung(JDialog parent, long bhppid, long verid, long dafid, boolean bedarf) {
        super(parent, true);
        this.parent = parent;
        this.bhppid = bhppid;
        this.verid = verid;
        this.dafid = dafid;
        this.bedarf = bedarf;
        initDialog();
    }

    private void initDialog() {

        initComponents();
        initData();

        String einheit = "x";
        if (dafid > 0) {
            long formid = ((BigInteger) DBRetrieve.getSingleValue("MPDarreichung", "FormID", "DafID", dafid)).longValue();
            ArrayList al = DBRetrieve.getMPForm(formid);
            int anwEinheit = ((Integer) al.get(1)).intValue();
            einheit = SYSConst.EINHEIT[anwEinheit] + SYSTools.catchNull(al.get(3).toString(), " ", "");
        }

        lblEin1.setText("Einheit: " + einheit);

        this.setTitle(SYSTools.getWindowTitle("Dosierung/Häufigkeit"));
        int selectIndex = 0;

        dummy = new JRadioButton();
        bgMonat.add(dummy);

        cmbUhrzeit.setModel(new DefaultComboBoxModel(SYSCalendar.fillUhrzeiten().toArray()));

        ignoreEvent = true;
        if (bedarf) {
            txtMaxTimes.setText((template.get("MaxAnzahl")).toString());
            txtEDosis.setText(Double.toString(Double.parseDouble(template.get("MaxEDosis").toString())));
        } else {
            txtMaxTimes.setText("0");
            txtEDosis.setText("0.0");
        }
        lblDosis.setEnabled(bedarf);
        lblX.setEnabled(bedarf);

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

        txtNachtMo.setEnabled(!bedarf);
        txtMorgens.setEnabled(!bedarf);
        txtMittags.setEnabled(!bedarf);
        txtNachmittags.setEnabled(!bedarf);
        txtAbends.setEnabled(!bedarf);
        txtUhrzeit.setEnabled(!bedarf);
        txtNachtAb.setEnabled(!bedarf);
        cmbUhrzeit.setEnabled(!bedarf);
        txtMaxTimes.setEnabled(bedarf);
        txtEDosis.setEnabled(bedarf);

        txtMorgens.setBackground(SYSConst.lightblue);
        txtMittags.setBackground(SYSConst.gold7);
        txtNachmittags.setBackground(SYSConst.melonrindgreen);
        txtAbends.setBackground(SYSConst.bermuda_sand);
        txtNachtAb.setBackground(SYSConst.bluegrey);

        SYSTools.setXEnabled(pnlWdh, !bedarf);
        cmbWTag.setEnabled(!bedarf);

        jdcLDatum.setEnabled(!bedarf);

        if (bedarf) {
            txtEDosis.requestFocus();
        } else {
            txtMorgens.requestFocus();
        }


        ignoreEvent = false;
        pack();
        SYSTools.centerOnParent(parent, this);
        setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgWdh = new javax.swing.ButtonGroup();
        bgMonat = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblDosis = new javax.swing.JLabel();
        txtMaxTimes = new javax.swing.JTextField();
        lblX = new javax.swing.JLabel();
        txtEDosis = new javax.swing.JTextField();
        lblEin1 = new javax.swing.JLabel();
        pnlRegular = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cmbUhrzeit = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        txtNachtMo = new javax.swing.JTextField();
        txtNachtAb = new javax.swing.JTextField();
        txtMittags = new javax.swing.JTextField();
        txtMorgens = new javax.swing.JTextField();
        txtNachmittags = new javax.swing.JTextField();
        txtAbends = new javax.swing.JTextField();
        txtUhrzeit = new javax.swing.JTextField();
        pnlWdh = new javax.swing.JPanel();
        rbTag = new javax.swing.JRadioButton();
        rbWoche = new javax.swing.JRadioButton();
        rbMonat = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cbMon = new javax.swing.JCheckBox();
        cbDie = new javax.swing.JCheckBox();
        cbMit = new javax.swing.JCheckBox();
        cbDon = new javax.swing.JCheckBox();
        cbFre = new javax.swing.JCheckBox();
        cbSam = new javax.swing.JCheckBox();
        cbSon = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        rbMonatTag = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        rbMonatWTag = new javax.swing.JRadioButton();
        cmbWTag = new javax.swing.JComboBox();
        spinTaeglich = new javax.swing.JSpinner();
        spinWoche = new javax.swing.JSpinner();
        spinMonat = new javax.swing.JSpinner();
        spinMonatTag = new javax.swing.JSpinner();
        spinMonatWTag = new javax.swing.JSpinner();
        jLabel13 = new javax.swing.JLabel();
        jdcLDatum = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        btnDiscard = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Dosierung (bei Bedarf)"));

        lblDosis.setText("Max. Tagesdosis:");

        txtMaxTimes.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMaxTimes.setText("1");
        txtMaxTimes.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtMaxTimesCaretUpdate(evt);
            }
        });
        txtMaxTimes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaxTimesActionPerformed(evt);
            }
        });
        txtMaxTimes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMaxTimesFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMaxTimesFocusLost(evt);
            }
        });

        lblX.setText("x");

        txtEDosis.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtEDosis.setText("1.0");
        txtEDosis.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtEDosisCaretUpdate(evt);
            }
        });
        txtEDosis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEDosisActionPerformed(evt);
            }
        });
        txtEDosis.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtEDosisFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtEDosisFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(lblDosis)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtMaxTimes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblX)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(txtEDosis, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 134, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtEDosis, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(lblDosis)
                        .add(txtMaxTimes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(lblX)))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        lblEin1.setForeground(new java.awt.Color(51, 51, 255));
        lblEin1.setText("Einheit:");

        pnlRegular.setBorder(javax.swing.BorderFactory.createTitledBorder("Dosierung, Häufigkeit (Regelmäßig)"));

        jLabel1.setForeground(new java.awt.Color(0, 0, 204));
        jLabel1.setText("Morgens:");

        jLabel2.setForeground(new java.awt.Color(255, 102, 0));
        jLabel2.setText("Mittags:");

        jLabel3.setForeground(new java.awt.Color(255, 0, 51));
        jLabel3.setText("Abends:");

        jLabel4.setText("Nacht, spät abends:");

        jLabel6.setText("Nachts, früh morgens:");

        cmbUhrzeit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10:00", "10:15", "10:30", "10:45" }));
        cmbUhrzeit.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbUhrzeitItemStateChanged(evt);
            }
        });

        jLabel11.setForeground(new java.awt.Color(0, 153, 51));
        jLabel11.setText("Nachmittag:");

        txtNachtMo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNachtMo.setText("0.0");
        txtNachtMo.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtNachtMoCaretUpdate(evt);
            }
        });
        txtNachtMo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNachtMoActionPerformed(evt);
            }
        });
        txtNachtMo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNachtMoFocusLost(evt);
            }
        });

        txtNachtAb.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNachtAb.setText("0.0");
        txtNachtAb.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtNachtAbCaretUpdate(evt);
            }
        });
        txtNachtAb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNachtAbActionPerformed(evt);
            }
        });
        txtNachtAb.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNachtAbFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNachtAbFocusLost(evt);
            }
        });

        txtMittags.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMittags.setText("0.0");
        txtMittags.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtMittagsCaretUpdate(evt);
            }
        });
        txtMittags.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMittagsActionPerformed(evt);
            }
        });
        txtMittags.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMittagsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMittagsFocusLost(evt);
            }
        });

        txtMorgens.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMorgens.setText("1.0");
        txtMorgens.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtMorgensCaretUpdate(evt);
            }
        });
        txtMorgens.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMorgensActionPerformed(evt);
            }
        });
        txtMorgens.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMorgensFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMorgensFocusLost(evt);
            }
        });

        txtNachmittags.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNachmittags.setText("0.0");
        txtNachmittags.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtNachmittagsCaretUpdate(evt);
            }
        });
        txtNachmittags.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNachmittagsActionPerformed(evt);
            }
        });
        txtNachmittags.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNachmittagsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNachmittagsFocusLost(evt);
            }
        });

        txtAbends.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAbends.setText("0.0");
        txtAbends.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtAbendsCaretUpdate(evt);
            }
        });
        txtAbends.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAbendsActionPerformed(evt);
            }
        });
        txtAbends.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAbendsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAbendsFocusLost(evt);
            }
        });

        txtUhrzeit.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtUhrzeit.setText("0.0");
        txtUhrzeit.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtUhrzeitCaretUpdate(evt);
            }
        });
        txtUhrzeit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUhrzeitActionPerformed(evt);
            }
        });
        txtUhrzeit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUhrzeitFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUhrzeitFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlRegularLayout = new org.jdesktop.layout.GroupLayout(pnlRegular);
        pnlRegular.setLayout(pnlRegularLayout);
        pnlRegularLayout.setHorizontalGroup(
            pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlRegularLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel4)
                    .add(jLabel3)
                    .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, cmbUhrzeit, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel11)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(txtNachtMo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .add(txtMorgens, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .add(txtMittags, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .add(txtAbends, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .add(txtNachtAb, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .add(txtUhrzeit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .add(txtNachmittags, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlRegularLayout.setVerticalGroup(
            pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlRegularLayout.createSequentialGroup()
                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(txtNachtMo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlRegularLayout.createSequentialGroup()
                        .add(76, 76, 76)
                        .add(jLabel11))
                    .add(pnlRegularLayout.createSequentialGroup()
                        .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtMorgens, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtMittags, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtNachmittags, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtAbends, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .add(2, 2, 2)
                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtNachtAb, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtUhrzeit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cmbUhrzeit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlWdh.setBorder(javax.swing.BorderFactory.createTitledBorder("Wiederholungen"));

        bgWdh.add(rbTag);
        rbTag.setText("täglich alle");
        rbTag.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbTag.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbTag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbTagActionPerformed(evt);
            }
        });

        bgWdh.add(rbWoche);
        rbWoche.setText("wöchentlich alle");
        rbWoche.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbWoche.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbWoche.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbWocheActionPerformed(evt);
            }
        });

        bgWdh.add(rbMonat);
        rbMonat.setText("monatlich alle");
        rbMonat.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbMonat.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbMonat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbMonatActionPerformed(evt);
            }
        });

        jLabel7.setText("Tage");

        jLabel8.setText("Wochen am:");

        cbMon.setText("Mon");
        cbMon.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbMon.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbMon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbMonActionPerformed(evt);
            }
        });

        cbDie.setText("Die");
        cbDie.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbDie.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbDie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDieActionPerformed(evt);
            }
        });

        cbMit.setText("Mit");
        cbMit.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbMit.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbMit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbMitActionPerformed(evt);
            }
        });

        cbDon.setText("Don");
        cbDon.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbDon.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDonActionPerformed(evt);
            }
        });

        cbFre.setText("Fre");
        cbFre.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbFre.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbFre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFreActionPerformed(evt);
            }
        });

        cbSam.setText("Sam");
        cbSam.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbSam.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbSam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSamActionPerformed(evt);
            }
        });

        cbSon.setText("Son");
        cbSon.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbSon.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbSon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSonActionPerformed(evt);
            }
        });

        jLabel9.setText("Monat(e)");

        bgMonat.add(rbMonatTag);
        rbMonatTag.setText("wiederholt am");
        rbMonatTag.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbMonatTag.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbMonatTag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbMonatTagActionPerformed(evt);
            }
        });

        jLabel10.setText("Tag");

        bgMonat.add(rbMonatWTag);
        rbMonatWTag.setText("wiederholt am");
        rbMonatWTag.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbMonatWTag.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbMonatWTag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbMonatWTagActionPerformed(evt);
            }
        });

        cmbWTag.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag" }));
        cmbWTag.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbWTagItemStateChanged(evt);
            }
        });

        spinTaeglich.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinTaeglichStateChanged(evt);
            }
        });

        spinWoche.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinWocheStateChanged(evt);
            }
        });

        spinMonat.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinMonatStateChanged(evt);
            }
        });

        spinMonatTag.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinMonatTagStateChanged(evt);
            }
        });

        spinMonatWTag.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinMonatWTagStateChanged(evt);
            }
        });

        jLabel13.setText("Erste Anwendung am:");

        org.jdesktop.layout.GroupLayout pnlWdhLayout = new org.jdesktop.layout.GroupLayout(pnlWdh);
        pnlWdh.setLayout(pnlWdhLayout);
        pnlWdhLayout.setHorizontalGroup(
            pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlWdhLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlWdhLayout.createSequentialGroup()
                        .add(17, 17, 17)
                        .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pnlWdhLayout.createSequentialGroup()
                                .add(rbMonatTag)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(spinMonatTag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnlWdhLayout.createSequentialGroup()
                                .add(rbMonatWTag)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(spinMonatWTag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(20, 20, 20)
                        .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel10)
                            .add(cmbWTag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(pnlWdhLayout.createSequentialGroup()
                        .add(rbWoche)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(spinWoche, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel8))
                    .add(pnlWdhLayout.createSequentialGroup()
                        .add(rbTag)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(spinTaeglich, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel7))
                    .add(pnlWdhLayout.createSequentialGroup()
                        .add(17, 17, 17)
                        .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pnlWdhLayout.createSequentialGroup()
                                .add(cbFre)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbSam)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbSon))
                            .add(pnlWdhLayout.createSequentialGroup()
                                .add(cbMon)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbDie)
                                .add(16, 16, 16)
                                .add(cbMit)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbDon))))
                    .add(pnlWdhLayout.createSequentialGroup()
                        .add(rbMonat)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(spinMonat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel9))
                    .add(pnlWdhLayout.createSequentialGroup()
                        .add(jLabel13)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jdcLDatum, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlWdhLayout.setVerticalGroup(
            pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlWdhLayout.createSequentialGroup()
                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rbTag)
                    .add(spinTaeglich, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .add(18, 18, 18)
                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rbWoche)
                    .add(spinWoche, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbMon)
                    .add(cbDie)
                    .add(cbMit)
                    .add(cbDon))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFre)
                    .add(cbSam)
                    .add(cbSon))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rbMonat)
                    .add(jLabel9)
                    .add(spinMonat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rbMonatTag)
                    .add(spinMonatTag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rbMonatWTag)
                    .add(spinMonatWTag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cmbWTag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlWdhLayout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(jLabel13))
                    .add(pnlWdhLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jdcLDatum, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(98, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlRegular, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblEin1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlWdh, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlWdh, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                        .add(lblEin1)
                        .add(1, 1, 1)
                        .add(pnlRegular, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        btnDiscard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/cancel.png"))); // NOI18N
        btnDiscard.setText("Verwerfen");
        btnDiscard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiscardActionPerformed(evt);
            }
        });

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/apply.png"))); // NOI18N
        btnSave.setText("Speichern");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(btnSave)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnDiscard)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnDiscard)
                    .add(btnSave))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(530, Short.MAX_VALUE)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-739)/2, (screenSize.height-482)/2, 739, 482);
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
        if (bhppid > 0) {
            template = DBRetrieve.getSingleRecord("BHPPlanung", "BHPPID", bhppid);
        } else {
            BigDecimal b0 = new BigDecimal(0d);
            BigDecimal b1 = new BigDecimal(1d);
            template = new HashMap();
            template.put("NachtMo", b0);
            template.put("Mittags", b0);
            template.put("Nachmittags", b0);
            template.put("Abends", b0);
            template.put("NachtAb", b0);
            template.put("UhrzeitDosis", b0);
            template.put("Uhrzeit", null);
            template.put("Taeglich", 1);
            template.put("Woechentlich", 0);
            template.put("Monatlich", 0);
            template.put("TagNum", 0);
            template.put("Bemerkung", "");
            template.put("LDatum", new Timestamp(SYSCalendar.now()));
            if (bedarf) {
                template.put("Morgens", b0);
                template.put("MaxAnzahl", 1);
                template.put("MaxEDosis", b1);
            } else {
                template.put("Morgens", b1);
                template.put("MaxAnzahl", 0);
                template.put("MaxEDosis", b0);
            }

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
        HashMap hm = new HashMap();

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
    private javax.swing.ButtonGroup bgMonat;
    private javax.swing.ButtonGroup bgWdh;
    private javax.swing.JButton btnDiscard;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox cbDie;
    private javax.swing.JCheckBox cbDon;
    private javax.swing.JCheckBox cbFre;
    private javax.swing.JCheckBox cbMit;
    private javax.swing.JCheckBox cbMon;
    private javax.swing.JCheckBox cbSam;
    private javax.swing.JCheckBox cbSon;
    private javax.swing.JComboBox cmbUhrzeit;
    private javax.swing.JComboBox cmbWTag;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private com.toedter.calendar.JDateChooser jdcLDatum;
    private javax.swing.JLabel lblDosis;
    private javax.swing.JLabel lblEin1;
    private javax.swing.JLabel lblX;
    private javax.swing.JPanel pnlRegular;
    private javax.swing.JPanel pnlWdh;
    private javax.swing.JRadioButton rbMonat;
    private javax.swing.JRadioButton rbMonatTag;
    private javax.swing.JRadioButton rbMonatWTag;
    private javax.swing.JRadioButton rbTag;
    private javax.swing.JRadioButton rbWoche;
    private javax.swing.JSpinner spinMonat;
    private javax.swing.JSpinner spinMonatTag;
    private javax.swing.JSpinner spinMonatWTag;
    private javax.swing.JSpinner spinTaeglich;
    private javax.swing.JSpinner spinWoche;
    private javax.swing.JTextField txtAbends;
    private javax.swing.JTextField txtEDosis;
    private javax.swing.JTextField txtMaxTimes;
    private javax.swing.JTextField txtMittags;
    private javax.swing.JTextField txtMorgens;
    private javax.swing.JTextField txtNachmittags;
    private javax.swing.JTextField txtNachtAb;
    private javax.swing.JTextField txtNachtMo;
    private javax.swing.JTextField txtUhrzeit;
    // End of variables declaration//GEN-END:variables
}
