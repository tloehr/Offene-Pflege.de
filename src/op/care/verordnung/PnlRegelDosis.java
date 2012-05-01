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

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.swing.*;
import com.toedter.calendar.JDateChooser;
import entity.verordnungen.Verordnung;
import entity.verordnungen.VerordnungPlanung;
import op.tools.*;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import org.jdesktop.swingx.*;

/**
 * @author tloehr
 */
public class PnlRegelDosis extends CleanablePanel {

    //private HashMap template;
    private JRadioButton dummy;
    private String tage[] = {"Mon", "Die", "Mit", "Don", "Fre", "Sam", "Son"};
    private boolean ignoreEvent = false;
    private JDCPropertyChangeListener jdcpcl;
    private Verordnung verordnung;
    private VerordnungPlanung planung;
    private Closure actionBlock;


    /**
     * für Neueingaben
     */
    public PnlRegelDosis(Verordnung verordnung, Closure actionBlock) {
        this(null, verordnung, actionBlock);
    }

//    public DlgVerabreichung(JDialog parent, Verordnung verordnung) {
//        this(parent, null, verordnung);
//    }

    /**
     * für Änderungen
     */
    public PnlRegelDosis(VerordnungPlanung planung, Verordnung verordnung, Closure actionBlock) {
        this.actionBlock = actionBlock;
        this.verordnung = verordnung;
        if (planung == null) {
            planung = new VerordnungPlanung(verordnung);
        }
        this.planung = planung;
        initPanel();
    }
//
//    public DlgVerabreichung(JDialog parent, VerordnungPlanung planung, Verordnung verordnung) {
//        super(parent, true);
//        this.parent = parent;
//        this.verordnung = verordnung;
//        if (planung == null) {
//            planung = new VerordnungPlanung(verordnung);
//        }
//        this.planung = planung;
//        initDialog();
//    }

    private void initPanel() {

        initComponents();

//        String einheit = "x";
//        if (verordnung.getDarreichung() != null) {
//            einheit = MedFormenTools.EINHEIT[verordnung.getDarreichung().getMedForm().getAnwEinheit()];
//        }
//
//        lblEin1.setText("Einheit: " + einheit);

//        this.setTitle(SYSTools.getWindowTitle("Dosierung/Häufigkeit"));

        dummy = new JRadioButton();
        bgMonat.add(dummy);

        cmbUhrzeit.setModel(new DefaultComboBoxModel(SYSCalendar.fillUhrzeiten().toArray()));

        ignoreEvent = true;
//        if (verordnung.isBedarf()) {
//            txtMaxTimes.setText(planung.getMaxAnzahl().toString());
//            txtEDosis.setText(planung.getMaxEDosis().toString());
//        } else {
//            txtMaxTimes.setText("0");
//            txtEDosis.setText(BigDecimal.ZERO.toString());
//        }
//        lblDosis.setEnabled(verordnung.isBedarf());
//        lblX.setEnabled(verordnung.isBedarf());

        if (planung.getUhrzeit() == null) {
            cmbUhrzeit.setSelectedIndex(-1);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            SYSTools.selectInComboBox(cmbUhrzeit, sdf.format(planung.getUhrzeit()), true);
        }
        spinTaeglich.setModel(new SpinnerNumberModel(0, 0, 365, 1));
        spinWoche.setModel(new SpinnerNumberModel(0, 0, 52, 1));
        spinMonat.setModel(new SpinnerNumberModel(0, 0, 12, 1));
        spinMonatTag.setModel(new SpinnerNumberModel(1, 0, 31, 1));
        spinMonatWTag.setModel(new SpinnerNumberModel(0, 0, 5, 1));

        spinTaeglich.setValue(planung.getTaeglich());
        rbTag.setSelected(planung.getTaeglich() > 0);
        spinWoche.setValue(planung.getWoechentlich());
        rbWoche.setSelected(planung.getWoechentlich() > 0);
        spinMonat.setValue(planung.getMonatlich());
        rbMonat.setSelected(planung.getMonatlich() > 0);
        spinMonatTag.setValue(planung.getTagNum());


        if (planung.getWoechentlich() > 0) {
            cbMon.setSelected(planung.getMon() > 0);
            cbDie.setSelected(planung.getDie() > 0);
            cbMit.setSelected(planung.getMit() > 0);
            cbDon.setSelected(planung.getDon() > 0);
            cbFre.setSelected(planung.getFre() > 0);
            cbSam.setSelected(planung.getSam() > 0);
            cbSon.setSelected(planung.getSon() > 0);
            rbWoche.setSelected(true);
        }

        if (planung.getMonatlich() > 0) {
            if (planung.getTagNum() > 0) {
                spinMonatTag.setValue(planung.getTagNum());
                rbMonatTag.setSelected(true);
            } else {
                if (planung.getMon() > 0) {
                    cmbWTag.setSelectedIndex(0);
                    spinMonatWTag.setValue(planung.getMon());
                } else if (planung.getDie() > 0) {
                    cmbWTag.setSelectedIndex(1);
                    spinMonatWTag.setValue(planung.getDie());
                } else if (planung.getMit() > 0) {
                    cmbWTag.setSelectedIndex(2);
                    spinMonatWTag.setValue(planung.getMit());
                } else if (planung.getDon() > 0) {
                    cmbWTag.setSelectedIndex(3);
                    spinMonatWTag.setValue(planung.getDon());
                } else if (planung.getFre() > 0) {
                    cmbWTag.setSelectedIndex(4);
                    spinMonatWTag.setValue(planung.getFre());
                } else if (planung.getSam() > 0) {
                    cmbWTag.setSelectedIndex(5);
                    spinMonatWTag.setValue(planung.getSam());
                } else if (planung.getSon() > 0) {
                    cmbWTag.setSelectedIndex(6);
                    spinMonatWTag.setValue(planung.getSon());
                }

                rbMonatWTag.setSelected(true);
            }
            rbMonat.setSelected(true);
        }

        jdcLDatum.setMinSelectableDate(new Date());
        jdcLDatum.setDate(new Date(Math.max(planung.getLDatum().getTime(), SYSCalendar.startOfDay())));
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
//        txtMaxTimes.setEnabled(verordnung.isBedarf());
//        txtEDosis.setEnabled(verordnung.isBedarf());

        txtNachtMo.setText(planung.getNachtMo().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        txtMorgens.setText(planung.getMorgens().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        txtMittags.setText(planung.getMittags().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        txtNachmittags.setText(planung.getNachmittags().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        txtAbends.setText(planung.getAbends().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        txtNachtAb.setText(planung.getNachtAb().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        txtUhrzeit.setText(planung.getUhrzeitDosis().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());

        txtMorgens.setBackground(SYSConst.lightblue);
        txtMittags.setBackground(SYSConst.gold7);
        txtNachmittags.setBackground(SYSConst.melonrindgreen);
        txtAbends.setBackground(SYSConst.bermuda_sand);
        txtNachtAb.setBackground(SYSConst.bluegrey);

        SYSTools.setXEnabled(pnlWdh, !verordnung.isBedarf());
        cmbWTag.setEnabled(!verordnung.isBedarf());

        jdcLDatum.setEnabled(!verordnung.isBedarf());

        txtMorgens.requestFocus();


        ignoreEvent = false;
//        pack();
//        SYSTools.centerOnParent(parent, this);
//        setVisible(true);
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
        pnlWdh = new JPanel();
        rbTag = new JRadioButton();
        rbWoche = new JRadioButton();
        rbMonat = new JRadioButton();
        cbMon = new JCheckBox();
        cbDie = new JCheckBox();
        rbMonatTag = new JRadioButton();
        rbMonatWTag = new JRadioButton();
        spinWoche = new JSpinner();
        jLabel8 = new JLabel();
        cbMit = new JCheckBox();
        cbDon = new JCheckBox();
        cbFre = new JCheckBox();
        cbSam = new JCheckBox();
        cbSon = new JCheckBox();
        spinMonat = new JSpinner();
        jLabel9 = new JLabel();
        spinMonatTag = new JSpinner();
        jLabel10 = new JLabel();
        spinMonatWTag = new JSpinner();
        cmbWTag = new JComboBox();
        jLabel13 = new JLabel();
        jdcLDatum = new JDateChooser();
        pnlRegular = new JPanel();
        jLabel6 = new JideLabel();
        jLabel1 = new JideLabel();
        jLabel2 = new JideLabel();
        jLabel11 = new JideLabel();
        jLabel3 = new JideLabel();
        jLabel4 = new JideLabel();
        panel1 = new JPanel();
        label1 = new JLabel();
        jideLabel1 = new JideLabel();
        cmbUhrzeit = new JComboBox();
        txtNachtMo = new JTextField();
        txtMorgens = new JTextField();
        txtMittags = new JTextField();
        txtNachmittags = new JTextField();
        txtAbends = new JTextField();
        txtNachtAb = new JTextField();
        txtUhrzeit = new JTextField();
        tabWdh = new JideTabbedPane();
        pnlDaily = new JPanel();
        spinTaeglich = new JSpinner();
        jLabel7 = new JLabel();
        pnlWeekly = new JPanel();
        pnlMonthly = new JPanel();
        btnSave = new JButton();
        bgWdh = new ButtonGroup();
        bgMonat = new ButtonGroup();

        //======== this ========
        setLayout(new BorderLayout());

        //======== jPanel3 ========
        {
            jPanel3.setLayout(new FormLayout(
                "default, $lcgap, default:grow, $lcgap, 143dlu:grow",
                "fill:default, $lgap, fill:default:grow, $lgap, default:grow, $lgap, default"));

            //======== pnlWdh ========
            {
                pnlWdh.setBorder(new TitledBorder("Wiederholungen"));
                pnlWdh.setLayout(new FormLayout(
                    "25dlu, $lcgap, 17dlu, $lcgap, 11dlu, $lcgap, 29dlu, $lcgap, default, $lcgap, 26dlu, $lcgap, default, $lcgap, 59dlu, $lcgap, default",
                    "7*(fill:default, $lgap), fill:default"));

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
                pnlWdh.add(rbTag, CC.xywh(1, 1, 5, 1));

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
                pnlWdh.add(rbWoche, CC.xywh(1, 3, 9, 1));

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
                pnlWdh.add(rbMonat, CC.xywh(1, 9, 7, 1));

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
                pnlWdh.add(cbMon, CC.xywh(1, 5, 3, 1));

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
                pnlWdh.add(cbDie, CC.xywh(5, 5, 3, 1));

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
                pnlWdh.add(rbMonatTag, CC.xywh(3, 11, 5, 1));

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
                pnlWdh.add(rbMonatWTag, CC.xywh(3, 13, 6, 1));

                //---- spinWoche ----
                spinWoche.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinWocheStateChanged(e);
                    }
                });
                pnlWdh.add(spinWoche, CC.xywh(9, 3, 5, 1));

                //---- jLabel8 ----
                jLabel8.setText("Wochen am:");
                pnlWdh.add(jLabel8, CC.xy(15, 3));

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
                pnlWdh.add(cbMit, CC.xywh(9, 5, 4, 1));

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
                pnlWdh.add(cbDon, CC.xywh(13, 5, 3, 1));

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
                pnlWdh.add(cbFre, CC.xywh(1, 7, 3, 1));

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
                pnlWdh.add(cbSam, CC.xywh(5, 7, 3, 1));

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
                pnlWdh.add(cbSon, CC.xywh(9, 7, 4, 1));

                //---- spinMonat ----
                spinMonat.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinMonatStateChanged(e);
                    }
                });
                pnlWdh.add(spinMonat, CC.xywh(9, 9, 5, 1));

                //---- jLabel9 ----
                jLabel9.setText("Monat(e)");
                pnlWdh.add(jLabel9, CC.xy(15, 9));

                //---- spinMonatTag ----
                spinMonatTag.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinMonatTagStateChanged(e);
                    }
                });
                pnlWdh.add(spinMonatTag, CC.xywh(9, 11, 5, 1));

                //---- jLabel10 ----
                jLabel10.setText("Tag");
                pnlWdh.add(jLabel10, CC.xy(15, 11));

                //---- spinMonatWTag ----
                spinMonatWTag.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinMonatWTagStateChanged(e);
                    }
                });
                pnlWdh.add(spinMonatWTag, CC.xywh(9, 13, 5, 1));

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
                pnlWdh.add(cmbWTag, CC.xywh(15, 13, 3, 1));

                //---- jLabel13 ----
                jLabel13.setText("Erst einplanen ab dem");
                pnlWdh.add(jLabel13, CC.xywh(1, 15, 7, 1));
                pnlWdh.add(jdcLDatum, CC.xywh(9, 15, 9, 1));
            }
            jPanel3.add(pnlWdh, CC.xywh(3, 1, 3, 3));

            //======== pnlRegular ========
            {
                pnlRegular.setBorder(new TitledBorder("Dosierung, H\u00e4ufigkeit (Regelm\u00e4\u00dfig)"));
                pnlRegular.setLayout(new FormLayout(
                    "6*(pref, $lcgap), default",
                    "fill:default, $lgap, fill:default"));

                //---- jLabel6 ----
                jLabel6.setText("Nachts, fr\u00fch morgens");
                jLabel6.setOrientation(1);
                jLabel6.setHorizontalAlignment(SwingConstants.TRAILING);
                pnlRegular.add(jLabel6, CC.xy(1, 1));

                //---- jLabel1 ----
                jLabel1.setForeground(new Color(0, 0, 204));
                jLabel1.setText("Morgens");
                jLabel1.setOrientation(1);
                jLabel1.setHorizontalAlignment(SwingConstants.TRAILING);
                pnlRegular.add(jLabel1, CC.xy(3, 1));

                //---- jLabel2 ----
                jLabel2.setForeground(new Color(255, 102, 0));
                jLabel2.setText("Mittags");
                jLabel2.setOrientation(1);
                jLabel2.setHorizontalAlignment(SwingConstants.TRAILING);
                pnlRegular.add(jLabel2, CC.xy(5, 1));

                //---- jLabel11 ----
                jLabel11.setForeground(new Color(0, 153, 51));
                jLabel11.setText("Nachmittag");
                jLabel11.setOrientation(1);
                jLabel11.setHorizontalAlignment(SwingConstants.TRAILING);
                pnlRegular.add(jLabel11, CC.xy(7, 1));

                //---- jLabel3 ----
                jLabel3.setForeground(new Color(255, 0, 51));
                jLabel3.setText("Abends");
                jLabel3.setOrientation(1);
                jLabel3.setHorizontalAlignment(SwingConstants.TRAILING);
                pnlRegular.add(jLabel3, CC.xy(9, 1));

                //---- jLabel4 ----
                jLabel4.setText("Nacht, sp\u00e4t abends");
                jLabel4.setOrientation(1);
                jLabel4.setHorizontalAlignment(SwingConstants.TRAILING);
                pnlRegular.add(jLabel4, CC.xy(11, 1));

                //======== panel1 ========
                {
                    panel1.setLayout(new VerticalLayout());

                    //---- label1 ----
                    label1.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/clock.png")));
                    label1.setHorizontalAlignment(SwingConstants.CENTER);
                    panel1.add(label1);

                    //---- jideLabel1 ----
                    jideLabel1.setText("Uhrzeit");
                    jideLabel1.setOrientation(1);
                    panel1.add(jideLabel1);

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
                    panel1.add(cmbUhrzeit);
                }
                pnlRegular.add(panel1, CC.xy(13, 1, CC.FILL, CC.BOTTOM));

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
                pnlRegular.add(txtNachtMo, CC.xy(1, 3));

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
                pnlRegular.add(txtMorgens, CC.xy(3, 3));

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
                pnlRegular.add(txtMittags, CC.xy(5, 3));

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
                pnlRegular.add(txtNachmittags, CC.xy(7, 3));

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
                pnlRegular.add(txtAbends, CC.xy(9, 3));

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
                pnlRegular.add(txtNachtAb, CC.xy(11, 3));

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
                pnlRegular.add(txtUhrzeit, CC.xy(13, 3));
            }
            jPanel3.add(pnlRegular, CC.xy(1, 5, CC.DEFAULT, CC.FILL));

            //======== tabWdh ========
            {

                //======== pnlDaily ========
                {
                    pnlDaily.setLayout(new VerticalLayout());

                    //---- spinTaeglich ----
                    spinTaeglich.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            spinTaeglichStateChanged(e);
                        }
                    });
                    pnlDaily.add(spinTaeglich);

                    //---- jLabel7 ----
                    jLabel7.setText("Tage");
                    pnlDaily.add(jLabel7);
                }
                tabWdh.addTab("T\u00e4glich", pnlDaily);


                //======== pnlWeekly ========
                {
                    pnlWeekly.setLayout(new BoxLayout(pnlWeekly, BoxLayout.X_AXIS));
                }
                tabWdh.addTab("W\u00f6chentlich", pnlWeekly);


                //======== pnlMonthly ========
                {
                    pnlMonthly.setLayout(new FormLayout(
                        "default, $lcgap, default",
                        "2*(default, $lgap), default"));
                }
                tabWdh.addTab("Monatlich", pnlMonthly);

            }
            jPanel3.add(tabWdh, CC.xywh(3, 5, 3, 1, CC.FILL, CC.FILL));

            //---- btnSave ----
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });
            jPanel3.add(btnSave, CC.xy(5, 7, CC.RIGHT, CC.DEFAULT));
        }
        add(jPanel3, BorderLayout.CENTER);

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

    @Override
    public void cleanup() {
        SYSTools.unregisterListeners(this);
        jdcLDatum.removePropertyChangeListener(jdcpcl);
        jdcLDatum.cleanup();
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
        actionBlock.execute(planung);
//        this.setVisible(false);
    }//GEN-LAST:event_btnSaveActionPerformed

    public void save() {
        planung.setNachtMo(new BigDecimal(Double.parseDouble(txtNachtMo.getText())));
        planung.setMorgens(new BigDecimal(Double.parseDouble(txtMorgens.getText())));
        planung.setMittags(new BigDecimal(Double.parseDouble(txtMittags.getText())));
        planung.setNachmittags(new BigDecimal(Double.parseDouble(txtNachmittags.getText())));
        planung.setAbends(new BigDecimal(Double.parseDouble(txtAbends.getText())));
        planung.setNachtAb(new BigDecimal(Double.parseDouble(txtNachtAb.getText())));
        planung.setUhrzeitDosis(new BigDecimal(Double.parseDouble(txtUhrzeit.getText())));

        ListElement e = (ListElement) cmbUhrzeit.getSelectedItem();
        if (e == null) {
            planung.setUhrzeit(null);
        } else {
            planung.setUhrzeit(new Date(((GregorianCalendar) e.getObject()).getTimeInMillis()));
        }
//        planung.setMaxAnzahl(Integer.parseInt(txtMaxTimes.getText()));
//        planung.setMaxEDosis(new BigDecimal(Double.parseDouble(txtEDosis.getText())));


        planung.setMaxAnzahl(0);
        planung.setMaxEDosis(BigDecimal.ZERO);

        planung.setTaeglich(Short.parseShort(spinTaeglich.getValue().toString()));
        planung.setWoechentlich(Short.parseShort(spinWoche.getValue().toString()));
        planung.setMonatlich(Short.parseShort(spinMonat.getValue().toString()));
        planung.setTagNum(Short.parseShort(spinMonatTag.getValue().toString()));

        planung.setLDatum(jdcLDatum.getDate());

        planung.setMon(cbMon.isSelected() ? (short) 1 : (short) 0);
        planung.setDie(cbDie.isSelected() ? (short) 1 : (short) 0);
        planung.setMit(cbMit.isSelected() ? (short) 1 : (short) 0);
        planung.setDon(cbDon.isSelected() ? (short) 1 : (short) 0);
        planung.setFre(cbFre.isSelected() ? (short) 1 : (short) 0);
        planung.setSam(cbSam.isSelected() ? (short) 1 : (short) 0);
        planung.setSon(cbSon.isSelected() ? (short) 1 : (short) 0);

        if (rbMonatWTag.isSelected()) {
            short s = Short.parseShort(spinMonatWTag.getValue().toString());
            if (cmbWTag.getSelectedIndex() == 0) {
                planung.setMon(s);
            } else if (cmbWTag.getSelectedIndex() == 1) {
                planung.setDie(s);
            } else if (cmbWTag.getSelectedIndex() == 2) {
                planung.setMit(s);
            } else if (cmbWTag.getSelectedIndex() == 3) {
                planung.setDon(s);
            } else if (cmbWTag.getSelectedIndex() == 4) {
                planung.setFre(s);
            } else if (cmbWTag.getSelectedIndex() == 5) {
                planung.setSam(s);
            } else if (cmbWTag.getSelectedIndex() == 6) {
                planung.setSon(s);
            }
        }

    }

    public VerordnungPlanung getPlanung() {
        return planung;
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
        planung = null;
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
//        txtEDosis.requestFocus();
    }//GEN-LAST:event_txtMaxTimesActionPerformed

    private void txtEDosisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEDosisActionPerformed
//        txtMaxTimes.requestFocus();
    }//GEN-LAST:event_txtEDosisActionPerformed

//    private void txtMaxTimesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMaxTimesFocusLost
//        ignoreEvent = true;
//        int i;
//        try {
//            i = Integer.parseInt(txtMaxTimes.getText());
//            txtMaxTimes.setText(Integer.toString(i));
//        } catch (NumberFormatException nfe) {
//            txtMaxTimes.setText("1");
//        }
//
//        txtNachtMo.setText("0.0");
//        txtMorgens.setText("0.0");
//        txtMittags.setText("0.0");
//        txtNachmittags.setText("0.0");
//        txtAbends.setText("0.0");
//        txtNachtAb.setText("0.0");
//        txtUhrzeit.setText("0.0");
//        cmbUhrzeit.setSelectedIndex(-1);
//
//        if (Double.parseDouble(txtEDosis.getText()) == 0) {
//            txtEDosis.setText("1.0");
//        }
//
//        btnSave.setEnabled(true);
//
//        ignoreEvent = false;
//    }//GEN-LAST:event_txtMaxTimesFocusLost
//
//    private void txtEDosisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEDosisFocusLost
//
//        ignoreEvent = true;
//        double d = SYSTools.parseDouble(txtEDosis.getText());
//        if (d == 0d) {
//            txtEDosis.setText("1.0");
//        } else {
//            txtEDosis.setText(Double.toString(d));
//        }
//
//        txtNachtMo.setText("0.0");
//        txtMorgens.setText("0.0");
//        txtMittags.setText("0.0");
//        txtNachmittags.setText("0.0");
//        txtAbends.setText("0.0");
//        txtNachtAb.setText("0.0");
//        txtUhrzeit.setText("0.0");
//        cmbUhrzeit.setSelectedIndex(-1);
//
//        btnSave.setEnabled(true);
//        ignoreEvent = false;
//    }//GEN-LAST:event_txtEDosisFocusLost

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
    private JPanel pnlWdh;
    private JRadioButton rbTag;
    private JRadioButton rbWoche;
    private JRadioButton rbMonat;
    private JCheckBox cbMon;
    private JCheckBox cbDie;
    private JRadioButton rbMonatTag;
    private JRadioButton rbMonatWTag;
    private JSpinner spinWoche;
    private JLabel jLabel8;
    private JCheckBox cbMit;
    private JCheckBox cbDon;
    private JCheckBox cbFre;
    private JCheckBox cbSam;
    private JCheckBox cbSon;
    private JSpinner spinMonat;
    private JLabel jLabel9;
    private JSpinner spinMonatTag;
    private JLabel jLabel10;
    private JSpinner spinMonatWTag;
    private JComboBox cmbWTag;
    private JLabel jLabel13;
    private JDateChooser jdcLDatum;
    private JPanel pnlRegular;
    private JideLabel jLabel6;
    private JideLabel jLabel1;
    private JideLabel jLabel2;
    private JideLabel jLabel11;
    private JideLabel jLabel3;
    private JideLabel jLabel4;
    private JPanel panel1;
    private JLabel label1;
    private JideLabel jideLabel1;
    private JComboBox cmbUhrzeit;
    private JTextField txtNachtMo;
    private JTextField txtMorgens;
    private JTextField txtMittags;
    private JTextField txtNachmittags;
    private JTextField txtAbends;
    private JTextField txtNachtAb;
    private JTextField txtUhrzeit;
    private JideTabbedPane tabWdh;
    private JPanel pnlDaily;
    private JSpinner spinTaeglich;
    private JLabel jLabel7;
    private JPanel pnlWeekly;
    private JPanel pnlMonthly;
    private JButton btnSave;
    private ButtonGroup bgWdh;
    private ButtonGroup bgMonat;
    // End of variables declaration//GEN-END:variables
}
