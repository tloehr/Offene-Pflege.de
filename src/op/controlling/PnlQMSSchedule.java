/*
 * OffenePflege
 * Copyright (C) 2006-2012 Torsten Löhr
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
package op.controlling;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.swing.JideLabel;
import com.jidesoft.swing.JideTabbedPane;
import com.toedter.calendar.*;
import entity.qms.Qmssched;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.LocalDate;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author tloehr
 */
public class PnlQMSSchedule extends JPanel {
    public static final String internalClassID = "opde.controlling.qms.dlgqmsplan.pnlschedule";

    private boolean ignoreEvent = false;
    private Qmssched qmssched;
    private Closure actionBlock;

    private final int TAB_DAILY = 0;
    private final int TAB_WEEKLY = 1;
    private final int TAB_MONTHLY = 2;

    public PnlQMSSchedule(Qmssched qmssched, Closure actionBlock) {
        this.qmssched = qmssched;
        this.actionBlock = actionBlock;

        initComponents();
        initPanel();
    }


    private void panelMainComponentResized(ComponentEvent e) {
//        SYSTools.showSide(splitRegular, splitRegularPos);
    }

    private void cmbUhrzeitItemStateChanged(ItemEvent e) {
//        currentSelectedTime = (Date) e.getItem();
//        lblUhrzeit.setText("Dosis um " + DateFormat.getTimeInstance(DateFormat.SHORT).format(e.getItem()) + " Uhr");
    }

    private void btnJedenTagActionPerformed(ActionEvent e) {
        spinTaeglich.setValue(1);
    }

    private void btnJedeWocheActionPerformed(ActionEvent e) {
        spinWoche.setValue(1);
    }

    private void btnJedenMonatActionPerformed(ActionEvent e) {
        spinMonat.setValue(1);
    }


    private void txtLDateFocusLost(FocusEvent evt) {
        SYSCalendar.handleDateFocusLost(evt, new LocalDate(), new LocalDate().plusWeeks(4));
    }


    private void initPanel() {


        spinTaeglich.setModel(new SpinnerNumberModel(1, 1, 365, 1));
        spinWoche.setModel(new SpinnerNumberModel(1, 1, 52, 1));
        spinMonat.setModel(new SpinnerNumberModel(1, 1, 12, 1));
        spinMonatTag.setModel(new SpinnerNumberModel(1, 1, 31, 1));

        spinTaeglich.setValue(Math.max(qmssched.getDaily(), 1));
        spinWoche.setValue(Math.max(qmssched.getWeekly(), 1));
        spinMonat.setValue(Math.max(qmssched.getMonthly(), 1));
        spinMonatTag.setValue(Math.max(qmssched.getDaynum(), 1));

        tabWdh.setSelectedIndex(TAB_DAILY);

        if (qmssched.getWeekly() > 0) {
            cbMon.setSelected(qmssched.getMon() > 0);
            cbDie.setSelected(qmssched.getTue() > 0);
            cbMit.setSelected(qmssched.getWed() > 0);
            cbDon.setSelected(qmssched.getThu() > 0);
            cbFre.setSelected(qmssched.getFri() > 0);
            cbSam.setSelected(qmssched.getSat() > 0);
            cbSon.setSelected(qmssched.getSun() > 0);
            tabWdh.setSelectedIndex(TAB_WEEKLY);
        }

        if (qmssched.getMonthly() > 0) {
            if (qmssched.getDaynum() > 0) {

                spinMonatTag.setValue(qmssched.getDaynum());
                cmbTag.setSelectedIndex(0);
            } else {

                if (qmssched.getMon() > 0) {
                    cmbTag.setSelectedIndex(1);
                    spinMonatTag.setValue(qmssched.getMon());
                } else if (qmssched.getTue() > 0) {
                    cmbTag.setSelectedIndex(2);
                    spinMonatTag.setValue(qmssched.getTue());
                } else if (qmssched.getWed() > 0) {
                    cmbTag.setSelectedIndex(3);
                    spinMonatTag.setValue(qmssched.getWed());
                } else if (qmssched.getThu() > 0) {
                    cmbTag.setSelectedIndex(4);
                    spinMonatTag.setValue(qmssched.getThu());
                } else if (qmssched.getFri() > 0) {
                    cmbTag.setSelectedIndex(5);
                    spinMonatTag.setValue(qmssched.getFri());
                } else if (qmssched.getSat() > 0) {
                    cmbTag.setSelectedIndex(6);
                    spinMonatTag.setValue(qmssched.getSat());
                } else if (qmssched.getSun() > 0) {
                    cmbTag.setSelectedIndex(7);
                    spinMonatTag.setValue(qmssched.getSun());
                }
            }
            tabWdh.setSelectedIndex(TAB_MONTHLY);
        }

        jdcLDate.setMinSelectableDate(new Date());
        jdcLDate.setDate(qmssched.getlDate());

        ArrayList<Date> timelist = SYSCalendar.getTimeList();
        cmbTime.setModel(new DefaultComboBoxModel(timelist.toArray()));
        cmbTime.setRenderer(SYSCalendar.getTimeRenderer());

        Date now = qmssched.getTime();
        if (now == null) now = new Date();


        for (Date time : timelist) {
            if (SYSCalendar.compareTime(time, now) >= 0) {
                now = time;
                break;
            }
        }
        cmbTime.setSelectedItem(now);

        txtBemerkung.setText(qmssched.getText());
        txtQMS.setText(qmssched.getMeasure());

        lblMeasure.setText(OPDE.lang.getString("misc.msg.measure"));
        lblTime.setText(OPDE.lang.getString("misc.msg.Time"));
        lblLDate.setText(OPDE.lang.getString("opde.controlling.qms.dlgqmsplan.pnlschedule.ldate"));
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        panelMain = new JPanel();
        lblMeasure = new JLabel();
        txtQMS = new JTextField();
        lblTime = new JLabel();
        cmbTime = new JComboBox();
        tabWdh = new JideTabbedPane();
        pnlDaily = new JPanel();
        label3 = new JLabel();
        spinTaeglich = new JSpinner();
        jLabel7 = new JLabel();
        btnJedenTag = new JButton();
        pnlWeekly = new JPanel();
        panel3 = new JPanel();
        btnJedeWoche = new JButton();
        label2 = new JLabel();
        spinWoche = new JSpinner();
        jLabel8 = new JLabel();
        lblUhrzeit2 = new JideLabel();
        lblUhrzeit3 = new JideLabel();
        lblUhrzeit4 = new JideLabel();
        lblUhrzeit5 = new JideLabel();
        lblUhrzeit6 = new JideLabel();
        lblUhrzeit7 = new JideLabel();
        lblUhrzeit8 = new JideLabel();
        cbMon = new JCheckBox();
        cbDie = new JCheckBox();
        cbMit = new JCheckBox();
        cbDon = new JCheckBox();
        cbFre = new JCheckBox();
        cbSam = new JCheckBox();
        cbSon = new JCheckBox();
        pnlMonthly = new JPanel();
        label4 = new JLabel();
        spinMonat = new JSpinner();
        label6 = new JLabel();
        btnJedenMonat = new JButton();
        label5 = new JLabel();
        spinMonatTag = new JSpinner();
        cmbTag = new JComboBox<>();
        lblLDate = new JLabel();
        jdcLDate = new JDateChooser();
        jScrollPane1 = new JScrollPane();
        txtBemerkung = new JTextArea();
        btnSave = new JButton();

        //======== this ========
        setLayout(new BorderLayout());

        //======== panelMain ========
        {
            panelMain.setBorder(new LineBorder(Color.black, 2, true));
            panelMain.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    panelMainComponentResized(e);
                }
            });
            panelMain.setLayout(new FormLayout(
                "$rgap, $lcgap, 223dlu:grow, $lcgap, $rgap",
                "default, $nlgap, default, $lgap, default, $nlgap, default, $lgap, pref, $lgap, default, $nlgap, default, $lgap, 72dlu:grow, $lgap, default, $lgap, $rgap"));

            //---- lblMeasure ----
            lblMeasure.setText("text");
            lblMeasure.setFont(new Font("Arial", Font.PLAIN, 10));
            lblMeasure.setHorizontalAlignment(SwingConstants.TRAILING);
            panelMain.add(lblMeasure, CC.xy(3, 1));
            panelMain.add(txtQMS, CC.xy(3, 3));

            //---- lblTime ----
            lblTime.setText("text");
            lblTime.setFont(new Font("Arial", Font.PLAIN, 10));
            lblTime.setHorizontalAlignment(SwingConstants.TRAILING);
            panelMain.add(lblTime, CC.xy(3, 5));
            panelMain.add(cmbTime, CC.xy(3, 7));

            //======== tabWdh ========
            {

                //======== pnlDaily ========
                {
                    pnlDaily.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlDaily.setLayout(new FormLayout(
                        "2*(default), $rgap, $lcgap, 40dlu, $rgap, default",
                        "default, $lgap, pref, $lgap, default"));

                    //---- label3 ----
                    label3.setText("alle");
                    label3.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlDaily.add(label3, CC.xy(2, 3));

                    //---- spinTaeglich ----
                    spinTaeglich.setFont(new Font("Arial", Font.PLAIN, 14));
                    spinTaeglich.setModel(new SpinnerNumberModel(1, null, null, 1));
                    pnlDaily.add(spinTaeglich, CC.xy(5, 3));

                    //---- jLabel7 ----
                    jLabel7.setText("Tage");
                    jLabel7.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlDaily.add(jLabel7, CC.xy(7, 3));

                    //---- btnJedenTag ----
                    btnJedenTag.setText("Jeden Tag");
                    btnJedenTag.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnJedenTagActionPerformed(e);
                        }
                    });
                    pnlDaily.add(btnJedenTag, CC.xywh(2, 5, 6, 1));
                }
                tabWdh.addTab("T\u00e4glich", pnlDaily);

                //======== pnlWeekly ========
                {
                    pnlWeekly.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlWeekly.setLayout(new FormLayout(
                        "default, 7*(13dlu), $lcgap, default:grow",
                        "$ugap, $lgap, default, $lgap, pref, $nlgap, default:grow, $lgap, $rgap"));

                    //======== panel3 ========
                    {
                        panel3.setLayout(new FormLayout(
                            "default, $rgap, 40dlu, $rgap, 2*(default), $lcgap, default, $lcgap",
                            "default:grow, $lgap, default"));

                        //---- btnJedeWoche ----
                        btnJedeWoche.setText("Jede Woche");
                        btnJedeWoche.setFont(new Font("Arial", Font.PLAIN, 14));
                        btnJedeWoche.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnJedeWocheActionPerformed(e);
                            }
                        });
                        panel3.add(btnJedeWoche, CC.xywh(3, 3, 3, 1));

                        //---- label2 ----
                        label2.setText("alle");
                        label2.setFont(new Font("Arial", Font.PLAIN, 14));
                        panel3.add(label2, CC.xy(1, 1));
                        panel3.add(spinWoche, CC.xy(3, 1));

                        //---- jLabel8 ----
                        jLabel8.setText("Wochen am");
                        jLabel8.setFont(new Font("Arial", Font.PLAIN, 14));
                        panel3.add(jLabel8, CC.xy(5, 1));
                    }
                    pnlWeekly.add(panel3, CC.xywh(2, 3, 9, 1));

                    //---- lblUhrzeit2 ----
                    lblUhrzeit2.setText("montags");
                    lblUhrzeit2.setOrientation(1);
                    lblUhrzeit2.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblUhrzeit2.setClockwise(false);
                    lblUhrzeit2.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlWeekly.add(lblUhrzeit2, CC.xy(2, 5, CC.CENTER, CC.BOTTOM));

                    //---- lblUhrzeit3 ----
                    lblUhrzeit3.setText("dienstags");
                    lblUhrzeit3.setOrientation(1);
                    lblUhrzeit3.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblUhrzeit3.setClockwise(false);
                    lblUhrzeit3.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlWeekly.add(lblUhrzeit3, CC.xy(3, 5, CC.CENTER, CC.BOTTOM));

                    //---- lblUhrzeit4 ----
                    lblUhrzeit4.setText("mittwochs");
                    lblUhrzeit4.setOrientation(1);
                    lblUhrzeit4.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblUhrzeit4.setClockwise(false);
                    lblUhrzeit4.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlWeekly.add(lblUhrzeit4, CC.xy(4, 5, CC.CENTER, CC.BOTTOM));

                    //---- lblUhrzeit5 ----
                    lblUhrzeit5.setText("donnerstags");
                    lblUhrzeit5.setOrientation(1);
                    lblUhrzeit5.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblUhrzeit5.setClockwise(false);
                    lblUhrzeit5.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlWeekly.add(lblUhrzeit5, CC.xy(5, 5, CC.CENTER, CC.BOTTOM));

                    //---- lblUhrzeit6 ----
                    lblUhrzeit6.setText("freitags");
                    lblUhrzeit6.setOrientation(1);
                    lblUhrzeit6.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblUhrzeit6.setClockwise(false);
                    lblUhrzeit6.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlWeekly.add(lblUhrzeit6, CC.xy(6, 5, CC.CENTER, CC.BOTTOM));

                    //---- lblUhrzeit7 ----
                    lblUhrzeit7.setText("samstags");
                    lblUhrzeit7.setOrientation(1);
                    lblUhrzeit7.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblUhrzeit7.setClockwise(false);
                    lblUhrzeit7.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlWeekly.add(lblUhrzeit7, CC.xy(7, 5, CC.CENTER, CC.BOTTOM));

                    //---- lblUhrzeit8 ----
                    lblUhrzeit8.setText("sonntags");
                    lblUhrzeit8.setOrientation(1);
                    lblUhrzeit8.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblUhrzeit8.setClockwise(false);
                    lblUhrzeit8.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlWeekly.add(lblUhrzeit8, CC.xy(8, 5, CC.CENTER, CC.BOTTOM));

                    //---- cbMon ----
                    cbMon.setBorder(BorderFactory.createEmptyBorder());
                    cbMon.setMargin(new Insets(0, 0, 0, 0));
                    cbMon.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbMonActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbMon, CC.xy(2, 7, CC.CENTER, CC.DEFAULT));

                    //---- cbDie ----
                    cbDie.setBorder(BorderFactory.createEmptyBorder());
                    cbDie.setMargin(new Insets(0, 0, 0, 0));
                    cbDie.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbDieActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbDie, CC.xy(3, 7, CC.CENTER, CC.DEFAULT));

                    //---- cbMit ----
                    cbMit.setBorder(BorderFactory.createEmptyBorder());
                    cbMit.setMargin(new Insets(0, 0, 0, 0));
                    cbMit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbMitActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbMit, CC.xy(4, 7, CC.CENTER, CC.DEFAULT));

                    //---- cbDon ----
                    cbDon.setBorder(BorderFactory.createEmptyBorder());
                    cbDon.setMargin(new Insets(0, 0, 0, 0));
                    cbDon.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbDonActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbDon, CC.xy(5, 7, CC.CENTER, CC.DEFAULT));

                    //---- cbFre ----
                    cbFre.setBorder(BorderFactory.createEmptyBorder());
                    cbFre.setMargin(new Insets(0, 0, 0, 0));
                    cbFre.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbFreActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbFre, CC.xy(6, 7, CC.CENTER, CC.DEFAULT));

                    //---- cbSam ----
                    cbSam.setBorder(BorderFactory.createEmptyBorder());
                    cbSam.setMargin(new Insets(0, 0, 0, 0));
                    cbSam.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbSamActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbSam, CC.xy(7, 7, CC.CENTER, CC.DEFAULT));

                    //---- cbSon ----
                    cbSon.setBorder(BorderFactory.createEmptyBorder());
                    cbSon.setMargin(new Insets(0, 0, 0, 0));
                    cbSon.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbSonActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbSon, CC.xy(8, 7, CC.CENTER, CC.DEFAULT));
                }
                tabWdh.addTab("W\u00f6chentlich", pnlWeekly);

                //======== pnlMonthly ========
                {
                    pnlMonthly.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlMonthly.setLayout(new FormLayout(
                        "default, $lcgap, pref, $lcgap, 40dlu, $lcgap, pref, $lcgap, 61dlu",
                        "3*(default, $lgap), default"));

                    //---- label4 ----
                    label4.setText("jeden");
                    label4.setFont(new Font("Arial", Font.PLAIN, 14));
                    label4.setHorizontalAlignment(SwingConstants.TRAILING);
                    pnlMonthly.add(label4, CC.xy(3, 3));

                    //---- spinMonat ----
                    spinMonat.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlMonthly.add(spinMonat, CC.xy(5, 3));

                    //---- label6 ----
                    label6.setText("Monat");
                    label6.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlMonthly.add(label6, CC.xy(7, 3));

                    //---- btnJedenMonat ----
                    btnJedenMonat.setText("Jeden Monat");
                    btnJedenMonat.setFont(new Font("Arial", Font.PLAIN, 14));
                    btnJedenMonat.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnJedenMonatActionPerformed(e);
                        }
                    });
                    pnlMonthly.add(btnJedenMonat, CC.xywh(3, 5, 5, 1));

                    //---- label5 ----
                    label5.setText("jeweils am");
                    label5.setFont(new Font("Arial", Font.PLAIN, 14));
                    label5.setHorizontalAlignment(SwingConstants.TRAILING);
                    pnlMonthly.add(label5, CC.xy(3, 7));

                    //---- spinMonatTag ----
                    spinMonatTag.setFont(new Font("Arial", Font.PLAIN, 14));
                    spinMonatTag.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            spinMonatTagStateChanged(e);
                        }
                    });
                    pnlMonthly.add(spinMonatTag, CC.xy(5, 7));

                    //---- cmbTag ----
                    cmbTag.setModel(new DefaultComboBoxModel<>(new String[] {
                        "Tag des Monats",
                        "Montag",
                        "Dienstag",
                        "Mittwoch",
                        "Donnerstag",
                        "Freitag",
                        "Samstag",
                        "Sonntag"
                    }));
                    cmbTag.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlMonthly.add(cmbTag, CC.xywh(7, 7, 3, 1));
                }
                tabWdh.addTab("Monatlich", pnlMonthly);
            }
            panelMain.add(tabWdh, CC.xy(3, 9, CC.FILL, CC.FILL));

            //---- lblLDate ----
            lblLDate.setText("text");
            lblLDate.setFont(new Font("Arial", Font.PLAIN, 10));
            lblLDate.setHorizontalAlignment(SwingConstants.TRAILING);
            panelMain.add(lblLDate, CC.xy(3, 11));
            panelMain.add(jdcLDate, CC.xy(3, 13));

            //======== jScrollPane1 ========
            {

                //---- txtBemerkung ----
                txtBemerkung.setColumns(20);
                txtBemerkung.setRows(5);
                jScrollPane1.setViewportView(txtBemerkung);
            }
            panelMain.add(jScrollPane1, CC.xy(3, 15, CC.DEFAULT, CC.FILL));

            //---- btnSave ----
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });
            panelMain.add(btnSave, CC.xy(3, 17, CC.RIGHT, CC.DEFAULT));
        }
        add(panelMain, BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


//    @Override
//    public void cleanup() {
//        SYSTools.unregisterListeners(this);
////        jdcLDatum.removePropertyChangeListener(jdcpcl);
//        jdcLDatum.cleanup();
//    }

    private void cbSonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbSonActionPerformed

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.needoneweekday")));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

    }//GEN-LAST:event_cbSonActionPerformed

    private void cbSamActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbSamActionPerformed
        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.needoneweekday")));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbSamActionPerformed

    private void cbFreActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbFreActionPerformed
        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.needoneweekday")));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbFreActionPerformed

    private void cbDonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbDonActionPerformed
        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.needoneweekday")));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbDonActionPerformed

    private void cbMitActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbMitActionPerformed
        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.needoneweekday")));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbMitActionPerformed

    private void cbDieActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbDieActionPerformed
        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.needoneweekday")));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbDieActionPerformed

    private void cbMonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbMonActionPerformed
        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.needoneweekday")));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbMonActionPerformed

//    private void spinMonatWTagStateChanged(ChangeEvent evt) {//GEN-FIRST:event_spinMonatWTagStateChanged
//
//        int monat = Integer.parseInt(spinMonat.getValue().toString());
//        if (monat == 0) {
//            spinMonat.setValue(1);
//        }
//
//    }//GEN-LAST:event_spinMonatWTagStateChanged

    private void spinMonatTagStateChanged(ChangeEvent evt) {//GEN-FIRST:event_spinMonatTagStateChanged

        int wert = Integer.parseInt(spinMonat.getValue().toString());

        if (wert > 5) {
            if (cmbTag.getSelectedIndex() > 0) { // Einstellung steht auf Wochentag. Das passt nicht zum Wert > 5.
                cmbTag.setSelectedIndex(0);
            }
        }

    }//GEN-LAST:event_spinMonatTagStateChanged

    private void btnSaveActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (isSaveOK()) {
            save();
            actionBlock.execute(qmssched);
        }

    }//GEN-LAST:event_btnSaveActionPerformed

    public boolean isSaveOK(){
        return jdcLDate.getDate() != null && !SYSTools.tidy(txtQMS.getText()).isEmpty();
    }

    public void save() throws NumberFormatException {



        qmssched.setTime((Date) cmbTime.getSelectedItem());

        qmssched.setDaily(tabWdh.getSelectedIndex() == TAB_DAILY ? Byte.parseByte(spinTaeglich.getValue().toString()) : (byte) 0);
        qmssched.setWeekly(tabWdh.getSelectedIndex() == TAB_WEEKLY ? Byte.parseByte(spinWoche.getValue().toString()) : (byte) 0);
        qmssched.setMonthly(tabWdh.getSelectedIndex() == TAB_MONTHLY ? Byte.parseByte(spinMonat.getValue().toString()) : (byte) 0);


        qmssched.setlDate(jdcLDate.getDate());

        qmssched.setMon(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbMon.isSelected() ? (byte) 1 : (byte) 0);
        qmssched.setTue(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbDie.isSelected() ? (byte) 1 : (byte) 0);
        qmssched.setWed(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbMit.isSelected() ? (byte) 1 : (byte) 0);
        qmssched.setThu(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbDon.isSelected() ? (byte) 1 : (byte) 0);
        qmssched.setFri(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbFre.isSelected() ? (byte) 1 : (byte) 0);
        qmssched.setSat(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbSam.isSelected() ? (byte) 1 : (byte) 0);
        qmssched.setSun(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbSon.isSelected() ? (byte) 1 : (byte) 0);

        if (tabWdh.getSelectedIndex() == TAB_MONTHLY) {
            byte b = Byte.parseByte(spinMonatTag.getValue().toString());
            qmssched.setDaynum(cmbTag.getSelectedIndex() == 0 ? b : (byte) 0);

            if (cmbTag.getSelectedIndex() == 1) {
                qmssched.setMon(b);
            } else if (cmbTag.getSelectedIndex() == 2) {
                qmssched.setTue(b);
            } else if (cmbTag.getSelectedIndex() == 3) {
                qmssched.setWed(b);
            } else if (cmbTag.getSelectedIndex() == 4) {
                qmssched.setThu(b);
            } else if (cmbTag.getSelectedIndex() == 5) {
                qmssched.setFri(b);
            } else if (cmbTag.getSelectedIndex() == 6) {
                qmssched.setSat(b);
            } else if (cmbTag.getSelectedIndex() == 7) {
                qmssched.setSun(b);
            }
        }

        qmssched.setMeasure(SYSTools.tidy(txtQMS.getText()));
        qmssched.setText(SYSTools.tidy(txtBemerkung.getText()));

//        if (!qmssched.isValid()) {
//            throw new NumberFormatException("Anzahl muss min. 1 sein");
//        }

    }

    private void txtFocusGained(FocusEvent evt) {//GEN-FIRST:event_txtFocusGained
        SYSTools.markAllTxt((JTextField) evt.getSource());
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel panelMain;
    private JLabel lblMeasure;
    private JTextField txtQMS;
    private JLabel lblTime;
    private JComboBox cmbTime;
    private JideTabbedPane tabWdh;
    private JPanel pnlDaily;
    private JLabel label3;
    private JSpinner spinTaeglich;
    private JLabel jLabel7;
    private JButton btnJedenTag;
    private JPanel pnlWeekly;
    private JPanel panel3;
    private JButton btnJedeWoche;
    private JLabel label2;
    private JSpinner spinWoche;
    private JLabel jLabel8;
    private JideLabel lblUhrzeit2;
    private JideLabel lblUhrzeit3;
    private JideLabel lblUhrzeit4;
    private JideLabel lblUhrzeit5;
    private JideLabel lblUhrzeit6;
    private JideLabel lblUhrzeit7;
    private JideLabel lblUhrzeit8;
    private JCheckBox cbMon;
    private JCheckBox cbDie;
    private JCheckBox cbMit;
    private JCheckBox cbDon;
    private JCheckBox cbFre;
    private JCheckBox cbSam;
    private JCheckBox cbSon;
    private JPanel pnlMonthly;
    private JLabel label4;
    private JSpinner spinMonat;
    private JLabel label6;
    private JButton btnJedenMonat;
    private JLabel label5;
    private JSpinner spinMonatTag;
    private JComboBox<String> cmbTag;
    private JLabel lblLDate;
    private JDateChooser jdcLDate;
    private JScrollPane jScrollPane1;
    private JTextArea txtBemerkung;
    private JButton btnSave;
    // End of variables declaration//GEN-END:variables
}
