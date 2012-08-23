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
package op.care.nursingprocess;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.swing.JideLabel;
import com.jidesoft.swing.JideTabbedPane;
import com.toedter.calendar.JDateChooser;
import entity.nursingprocess.InterventionSchedule;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.GUITools;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateMidnight;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author tloehr
 */
public class PnlSchedule extends JPanel {
    public static final String internalClassID = "nursingrecords.nursingprocess.dlgplanung.pnlschedule";

    private boolean ignoreEvent = false;
    private InterventionSchedule is;
    private Closure actionBlock;

    private double splitRegularPos;

    private final int TAB_DAILY = 0;
    private final int TAB_WEEKLY = 1;
    private final int TAB_MONTHLY = 2;

    private JToggleButton tbFloating;

    public PnlSchedule(InterventionSchedule is, Closure actionBlock) {
        this.actionBlock = actionBlock;
        this.is = is;
        initComponents();
        initPanel();        
    }

    private void btnToTimeActionPerformed(ActionEvent e) {
        splitRegularPos = SYSTools.showSide(splitRegular, SYSTools.RIGHT_LOWER_SIDE, SYSTools.SPEED_NORMAL);
//        if (Double.parseDouble(txtUhrzeit.getText()) == 0) {
//            txtUhrzeit.setText("1");
//        }
    }

    private void btnToTimeOfDayActionPerformed(ActionEvent e) {
        splitRegularPos = SYSTools.showSide(splitRegular, SYSTools.LEFT_UPPER_SIDE, SYSTools.SPEED_NORMAL);
//        if (!isAtLeastOneTxtFieldNotZero()) {
//            txtMorgens.setText("1");
//        }
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

    private void txtMinutesFocusLost(FocusEvent e) {
        BigDecimal bd = SYSTools.parseBigDecimal(txtMinutes.getText());
        if (bd == null || bd.compareTo(BigDecimal.ZERO) <= 0){
            txtMinutes.setText(is.getDauer().toPlainString());
        }
    }


    private void initPanel() {

        pnlBemerkung.setBorder(new TitledBorder(OPDE.lang.getString(internalClassID + ".bordertitle4textfield")));

        ArrayList<Date> timelist = SYSCalendar.getTimeList();
        cmbUhrzeit.setModel(new DefaultComboBoxModel(timelist.toArray()));
        cmbUhrzeit.setRenderer(SYSCalendar.getTimeRenderer());

        spinTaeglich.setModel(new SpinnerNumberModel(1, 1, 365, 1));
        spinWoche.setModel(new SpinnerNumberModel(1, 1, 52, 1));
        spinMonat.setModel(new SpinnerNumberModel(1, 1, 12, 1));
        spinMonatTag.setModel(new SpinnerNumberModel(1, 1, 31, 1));

        spinTaeglich.setValue(Math.max(is.getTaeglich(), 1));
        spinWoche.setValue(Math.max(is.getWoechentlich(), 1));
        spinMonat.setValue(Math.max(is.getMonatlich(), 1));
        spinMonatTag.setValue(Math.max(is.getTagNum(), 1));

        tabWdh.setSelectedIndex(TAB_DAILY);

        if (is.getWoechentlich() > 0) {
            cbMon.setSelected(is.getMon() > 0);
            cbDie.setSelected(is.getDie() > 0);
            cbMit.setSelected(is.getMit() > 0);
            cbDon.setSelected(is.getDon() > 0);
            cbFre.setSelected(is.getFre() > 0);
            cbSam.setSelected(is.getSam() > 0);
            cbSon.setSelected(is.getSon() > 0);
            tabWdh.setSelectedIndex(TAB_WEEKLY);
        }

        if (is.getMonatlich() > 0) {
            if (is.getTagNum() > 0) {

                spinMonatTag.setValue(is.getTagNum());
                cmbTag.setSelectedIndex(0);
            } else {

                if (is.getMon() > 0) {
                    cmbTag.setSelectedIndex(1);
                    spinMonatTag.setValue(is.getMon());
                } else if (is.getDie() > 0) {
                    cmbTag.setSelectedIndex(2);
                    spinMonatTag.setValue(is.getDie());
                } else if (is.getMit() > 0) {
                    cmbTag.setSelectedIndex(3);
                    spinMonatTag.setValue(is.getMit());
                } else if (is.getDon() > 0) {
                    cmbTag.setSelectedIndex(4);
                    spinMonatTag.setValue(is.getDon());
                } else if (is.getFre() > 0) {
                    cmbTag.setSelectedIndex(5);
                    spinMonatTag.setValue(is.getFre());
                } else if (is.getSam() > 0) {
                    cmbTag.setSelectedIndex(6);
                    spinMonatTag.setValue(is.getSam());
                } else if (is.getSon() > 0) {
                    cmbTag.setSelectedIndex(7);
                    spinMonatTag.setValue(is.getSon());
                }
            }
            tabWdh.setSelectedIndex(TAB_MONTHLY);
        }

        jdcLDatum.setMinSelectableDate(new Date());
        jdcLDatum.setDate(new Date(Math.max(is.getLDatum().getTime(), new DateMidnight().getMillis())));

        txtNachtMo.setText(is.getNachtMo().toString());
        txtMorgens.setText(is.getMorgens().toString());
        txtMittags.setText(is.getMittags().toString());
        txtNachmittags.setText(is.getNachmittags().toString());
        txtAbends.setText(is.getAbends().toString());
        txtNachtAb.setText(is.getNachtAb().toString());
        txtUhrzeit.setText(is.getUhrzeitAnzahl().toString());

        txtMorgens.setBackground(SYSConst.lightblue);
        txtMittags.setBackground(SYSConst.gold7);
        txtNachmittags.setBackground(SYSConst.melonrindgreen);
        txtAbends.setBackground(SYSConst.bermuda_sand);
        txtNachtAb.setBackground(SYSConst.bluegrey);

        Date now = null;
        if (is.getUhrzeitAnzahl() > 0) {
            now = is.getUhrzeit();
        } else {
            now = new Date();
        }

        for (Date zeit : timelist) {
            if (SYSCalendar.compareTime(zeit, now) >= 0) {
                now = zeit;
                break;
            }
        }
        cmbUhrzeit.setSelectedItem(now);
        lblUhrzeit.setText(OPDE.lang.getString("misc.msg.Number"));

        txtBemerkung.setText(is.getBemerkung());

        lblMinutes.setText(OPDE.lang.getString("misc.msg.Minute(s)"));
        txtMinutes.setText(is.getDauer().toPlainString());
        
        tbFloating = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".floatinginterventions"));
        tbFloating.setSelected(is.isFloating());
        panelMain.add(tbFloating, CC.xy(3, 5));

        splitRegularPos = SYSTools.showSide(splitRegular, is.getUhrzeit() != null ? SYSTools.RIGHT_LOWER_SIDE : SYSTools.LEFT_UPPER_SIDE);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        panelMain = new JPanel();
        splitRegular = new JSplitPane();
        pnlTageszeit = new JPanel();
        jLabel6 = new JideLabel();
        jLabel1 = new JideLabel();
        jLabel2 = new JideLabel();
        jLabel11 = new JideLabel();
        jLabel3 = new JideLabel();
        jLabel4 = new JideLabel();
        txtNachtMo = new JTextField();
        txtMorgens = new JTextField();
        txtMittags = new JTextField();
        txtNachmittags = new JTextField();
        txtAbends = new JTextField();
        txtNachtAb = new JTextField();
        btnToTime = new JButton();
        pnlUhrzeit = new JPanel();
        lblUhrzeit = new JideLabel();
        btnToTimeOfDay = new JButton();
        txtUhrzeit = new JTextField();
        cmbUhrzeit = new JComboBox();
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
        cmbTag = new JComboBox();
        panel2 = new JPanel();
        jLabel13 = new JLabel();
        jdcLDatum = new JDateChooser();
        lblMinutes = new JLabel();
        txtMinutes = new JTextField();
        pnlBemerkung = new JPanel();
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
                "$rgap, $lgap, pref, $lgap, default, $lgap, pref, $lgap, default, $lgap, 72dlu:grow, 2*($lgap, default)"));

            //======== splitRegular ========
            {
                splitRegular.setDividerSize(0);
                splitRegular.setEnabled(false);
                splitRegular.setDividerLocation(150);
                splitRegular.setDoubleBuffered(true);

                //======== pnlTageszeit ========
                {
                    pnlTageszeit.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlTageszeit.setBorder(new EtchedBorder());
                    pnlTageszeit.setLayout(new FormLayout(
                        "6*(28dlu, $lcgap), default",
                        "fill:default, $lgap, fill:default"));

                    //---- jLabel6 ----
                    jLabel6.setText("Nachts, fr\u00fch morgens");
                    jLabel6.setOrientation(1);
                    jLabel6.setFont(new Font("Arial", Font.PLAIN, 14));
                    jLabel6.setClockwise(false);
                    jLabel6.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlTageszeit.add(jLabel6, CC.xy(1, 1));

                    //---- jLabel1 ----
                    jLabel1.setForeground(new Color(0, 0, 204));
                    jLabel1.setText("Morgens");
                    jLabel1.setOrientation(1);
                    jLabel1.setFont(new Font("Arial", Font.PLAIN, 14));
                    jLabel1.setClockwise(false);
                    jLabel1.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlTageszeit.add(jLabel1, CC.xy(3, 1));

                    //---- jLabel2 ----
                    jLabel2.setForeground(new Color(255, 102, 0));
                    jLabel2.setText("Mittags");
                    jLabel2.setOrientation(1);
                    jLabel2.setFont(new Font("Arial", Font.PLAIN, 14));
                    jLabel2.setClockwise(false);
                    jLabel2.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlTageszeit.add(jLabel2, CC.xy(5, 1));

                    //---- jLabel11 ----
                    jLabel11.setForeground(new Color(0, 153, 51));
                    jLabel11.setText("Nachmittag");
                    jLabel11.setOrientation(1);
                    jLabel11.setFont(new Font("Arial", Font.PLAIN, 14));
                    jLabel11.setClockwise(false);
                    jLabel11.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlTageszeit.add(jLabel11, CC.xy(7, 1));

                    //---- jLabel3 ----
                    jLabel3.setForeground(new Color(255, 0, 51));
                    jLabel3.setText("Abends");
                    jLabel3.setOrientation(1);
                    jLabel3.setFont(new Font("Arial", Font.PLAIN, 14));
                    jLabel3.setClockwise(false);
                    jLabel3.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlTageszeit.add(jLabel3, CC.xy(9, 1));

                    //---- jLabel4 ----
                    jLabel4.setText("Nacht, sp\u00e4t abends");
                    jLabel4.setOrientation(1);
                    jLabel4.setFont(new Font("Arial", Font.PLAIN, 14));
                    jLabel4.setClockwise(false);
                    jLabel4.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlTageszeit.add(jLabel4, CC.xy(11, 1));

                    //---- txtNachtMo ----
                    txtNachtMo.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtNachtMo.setText("0.0");
                    txtNachtMo.setFont(new Font("Arial", Font.PLAIN, 14));
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
                    });
                    pnlTageszeit.add(txtNachtMo, CC.xy(1, 3));

                    //---- txtMorgens ----
                    txtMorgens.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtMorgens.setText("1.0");
                    txtMorgens.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtMorgens.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtMorgensActionPerformed(e);
                        }
                    });
                    txtMorgens.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtFocusGained(e);
                        }
                    });
                    pnlTageszeit.add(txtMorgens, CC.xy(3, 3));

                    //---- txtMittags ----
                    txtMittags.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtMittags.setText("0.0");
                    txtMittags.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtMittags.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtMittagsActionPerformed(e);
                        }
                    });
                    txtMittags.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtFocusGained(e);
                        }
                    });
                    pnlTageszeit.add(txtMittags, CC.xy(5, 3));

                    //---- txtNachmittags ----
                    txtNachmittags.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtNachmittags.setText("0.0");
                    txtNachmittags.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtNachmittags.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtNachmittagsActionPerformed(e);
                        }
                    });
                    txtNachmittags.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtFocusGained(e);
                        }
                    });
                    pnlTageszeit.add(txtNachmittags, CC.xy(7, 3));

                    //---- txtAbends ----
                    txtAbends.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtAbends.setText("0.0");
                    txtAbends.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtAbends.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtAbendsActionPerformed(e);
                        }
                    });
                    txtAbends.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtFocusGained(e);
                        }
                    });
                    pnlTageszeit.add(txtAbends, CC.xy(9, 3));

                    //---- txtNachtAb ----
                    txtNachtAb.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtNachtAb.setText("0.0");
                    txtNachtAb.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtNachtAb.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtNachtAbActionPerformed(e);
                        }
                    });
                    txtNachtAb.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtFocusGained(e);
                        }
                    });
                    pnlTageszeit.add(txtNachtAb, CC.xy(11, 3));

                    //---- btnToTime ----
                    btnToTime.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/1rightarrow.png")));
                    btnToTime.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnToTimeActionPerformed(e);
                        }
                    });
                    pnlTageszeit.add(btnToTime, CC.xy(13, 3));
                }
                splitRegular.setLeftComponent(pnlTageszeit);

                //======== pnlUhrzeit ========
                {
                    pnlUhrzeit.setBorder(new EtchedBorder());
                    pnlUhrzeit.setLayout(new FormLayout(
                        "default, $ugap, 75dlu, $ugap, pref",
                        "default:grow, $rgap, default"));

                    //---- lblUhrzeit ----
                    lblUhrzeit.setText("Anzahl Massnahmen");
                    lblUhrzeit.setOrientation(2);
                    lblUhrzeit.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblUhrzeit.setClockwise(false);
                    lblUhrzeit.setHorizontalTextPosition(SwingConstants.LEFT);
                    lblUhrzeit.setVerticalAlignment(SwingConstants.BOTTOM);
                    pnlUhrzeit.add(lblUhrzeit, CC.xy(3, 1, CC.DEFAULT, CC.BOTTOM));

                    //---- btnToTimeOfDay ----
                    btnToTimeOfDay.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/1leftarrow.png")));
                    btnToTimeOfDay.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnToTimeOfDayActionPerformed(e);
                        }
                    });
                    pnlUhrzeit.add(btnToTimeOfDay, CC.xy(1, 3));

                    //---- txtUhrzeit ----
                    txtUhrzeit.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtUhrzeit.setText("0.0");
                    txtUhrzeit.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtUhrzeit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtUhrzeitActionPerformed(e);
                        }
                    });
                    txtUhrzeit.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtFocusGained(e);
                        }
                    });
                    pnlUhrzeit.add(txtUhrzeit, CC.xy(3, 3));

                    //---- cmbUhrzeit ----
                    cmbUhrzeit.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbUhrzeitItemStateChanged(e);
                        }
                    });
                    pnlUhrzeit.add(cmbUhrzeit, CC.xy(5, 3));
                }
                splitRegular.setRightComponent(pnlUhrzeit);
            }
            panelMain.add(splitRegular, CC.xy(3, 3));

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
                        "$ugap, $lgap, default, $lgap, pref, default:grow, $lgap, $rgap"));

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
                    pnlWeekly.add(cbMon, CC.xy(2, 6, CC.CENTER, CC.DEFAULT));

                    //---- cbDie ----
                    cbDie.setBorder(BorderFactory.createEmptyBorder());
                    cbDie.setMargin(new Insets(0, 0, 0, 0));
                    cbDie.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbDieActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbDie, CC.xy(3, 6, CC.CENTER, CC.DEFAULT));

                    //---- cbMit ----
                    cbMit.setBorder(BorderFactory.createEmptyBorder());
                    cbMit.setMargin(new Insets(0, 0, 0, 0));
                    cbMit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbMitActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbMit, CC.xy(4, 6, CC.CENTER, CC.DEFAULT));

                    //---- cbDon ----
                    cbDon.setBorder(BorderFactory.createEmptyBorder());
                    cbDon.setMargin(new Insets(0, 0, 0, 0));
                    cbDon.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbDonActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbDon, CC.xy(5, 6, CC.CENTER, CC.DEFAULT));

                    //---- cbFre ----
                    cbFre.setBorder(BorderFactory.createEmptyBorder());
                    cbFre.setMargin(new Insets(0, 0, 0, 0));
                    cbFre.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbFreActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbFre, CC.xy(6, 6, CC.CENTER, CC.DEFAULT));

                    //---- cbSam ----
                    cbSam.setBorder(BorderFactory.createEmptyBorder());
                    cbSam.setMargin(new Insets(0, 0, 0, 0));
                    cbSam.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbSamActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbSam, CC.xy(7, 6, CC.CENTER, CC.DEFAULT));

                    //---- cbSon ----
                    cbSon.setBorder(BorderFactory.createEmptyBorder());
                    cbSon.setMargin(new Insets(0, 0, 0, 0));
                    cbSon.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbSonActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbSon, CC.xy(8, 6, CC.CENTER, CC.DEFAULT));
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
                    cmbTag.setModel(new DefaultComboBoxModel(new String[] {
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
            panelMain.add(tabWdh, CC.xy(3, 7, CC.FILL, CC.FILL));

            //======== panel2 ========
            {
                panel2.setLayout(new FormLayout(
                    "default, $lcgap, default:grow, $ugap, default, $lcgap, default:grow",
                    "default:grow"));

                //---- jLabel13 ----
                jLabel13.setText("Erst einplanen ab dem");
                jLabel13.setFont(new Font("Arial", Font.PLAIN, 14));
                panel2.add(jLabel13, CC.xy(1, 1));

                //---- jdcLDatum ----
                jdcLDatum.setFont(new Font("Arial", Font.PLAIN, 14));
                panel2.add(jdcLDatum, CC.xy(3, 1));

                //---- lblMinutes ----
                lblMinutes.setText("text");
                lblMinutes.setFont(new Font("Arial", Font.PLAIN, 14));
                panel2.add(lblMinutes, CC.xy(5, 1));

                //---- txtMinutes ----
                txtMinutes.setFont(new Font("Arial", Font.PLAIN, 14));
                txtMinutes.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtMinutesFocusLost(e);
                    }
                });
                panel2.add(txtMinutes, CC.xy(7, 1));
            }
            panelMain.add(panel2, CC.xy(3, 9));

            //======== pnlBemerkung ========
            {
                pnlBemerkung.setBorder(new TitledBorder(null, "Kommentar zur Anwendung (Erscheint im DFN)", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
                    new Font("Arial", Font.PLAIN, 14)));
                pnlBemerkung.setLayout(new BoxLayout(pnlBemerkung, BoxLayout.X_AXIS));

                //======== jScrollPane1 ========
                {

                    //---- txtBemerkung ----
                    txtBemerkung.setColumns(20);
                    txtBemerkung.setRows(5);
                    jScrollPane1.setViewportView(txtBemerkung);
                }
                pnlBemerkung.add(jScrollPane1);
            }
            panelMain.add(pnlBemerkung, CC.xy(3, 11, CC.FILL, CC.FILL));

            //---- btnSave ----
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });
            panelMain.add(btnSave, CC.xy(3, 13, CC.RIGHT, CC.DEFAULT));
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
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID+".needoneweekday")));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

    }//GEN-LAST:event_cbSonActionPerformed

    private void cbSamActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbSamActionPerformed
        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID+".needoneweekday")));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbSamActionPerformed

    private void cbFreActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbFreActionPerformed
        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID+".needoneweekday")));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbFreActionPerformed

    private void cbDonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbDonActionPerformed
        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID+".needoneweekday")));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbDonActionPerformed

    private void cbMitActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbMitActionPerformed
        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID+".needoneweekday")));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbMitActionPerformed

    private void cbDieActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbDieActionPerformed
        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID+".needoneweekday")));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbDieActionPerformed

    private void cbMonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbMonActionPerformed
        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID+".needoneweekday")));
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
        try {
            save();
            actionBlock.execute(is);
        } catch (NumberFormatException nfe) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".parseerror") + nfe.getLocalizedMessage(), 2));
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    public void save() throws NumberFormatException {

        boolean splitSetToTime = splitRegularPos == 0d;

        if (!isAtLeastOneTxtFieldNotZero() && Double.parseDouble(txtUhrzeit.getText()) == 0d) {
            throw new NumberFormatException("Anzahl der Durchführungen steht auf null.");
        }

        is.setNachtMo(splitSetToTime ? (short) 0 : Short.parseShort(txtNachtMo.getText()));
        is.setMorgens(splitSetToTime ? (short) 0 : Short.parseShort(txtMorgens.getText()));
        is.setMittags(splitSetToTime ? (short) 0 : Short.parseShort(txtMittags.getText()));
        is.setNachmittags(splitSetToTime ? (short) 0 : Short.parseShort(txtNachmittags.getText()));
        is.setAbends(splitSetToTime ? (short) 0 : Short.parseShort(txtAbends.getText()));
        is.setNachtAb(splitSetToTime ? (short) 0 : Short.parseShort(txtNachtAb.getText()));
        is.setUhrzeitAnzahl(!splitSetToTime ? (short) 0 : Short.parseShort(txtUhrzeit.getText()));
        is.setUhrzeit(!splitSetToTime ? null : (Date) cmbUhrzeit.getSelectedItem());

        is.setTaeglich(tabWdh.getSelectedIndex() == TAB_DAILY ? Short.parseShort(spinTaeglich.getValue().toString()) : (short) 0);
        is.setWoechentlich(tabWdh.getSelectedIndex() == TAB_WEEKLY ? Short.parseShort(spinWoche.getValue().toString()) : (short) 0);
        is.setMonatlich(tabWdh.getSelectedIndex() == TAB_MONTHLY ? Short.parseShort(spinMonat.getValue().toString()) : (short) 0);
        is.setLDatum(jdcLDatum.getDate());

        is.setMon(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbMon.isSelected() ? (short) 1 : (short) 0);
        is.setDie(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbDie.isSelected() ? (short) 1 : (short) 0);
        is.setMit(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbMit.isSelected() ? (short) 1 : (short) 0);
        is.setDon(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbDon.isSelected() ? (short) 1 : (short) 0);
        is.setFre(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbFre.isSelected() ? (short) 1 : (short) 0);
        is.setSam(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbSam.isSelected() ? (short) 1 : (short) 0);
        is.setSon(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbSon.isSelected() ? (short) 1 : (short) 0);

        if (tabWdh.getSelectedIndex() == TAB_MONTHLY) {
            short s = Short.parseShort(spinMonatTag.getValue().toString());
            is.setTagNum(cmbTag.getSelectedIndex() == 0 ? s : (short) 0);

            if (cmbTag.getSelectedIndex() == 1) {
                is.setMon(s);
            } else if (cmbTag.getSelectedIndex() == 2) {
                is.setDie(s);
            } else if (cmbTag.getSelectedIndex() == 3) {
                is.setMit(s);
            } else if (cmbTag.getSelectedIndex() == 4) {
                is.setDon(s);
            } else if (cmbTag.getSelectedIndex() == 5) {
                is.setFre(s);
            } else if (cmbTag.getSelectedIndex() == 6) {
                is.setSam(s);
            } else if (cmbTag.getSelectedIndex() == 7) {
                is.setSon(s);
            }
        }

        is.setFloating(tbFloating.isSelected());
        is.setBemerkung(txtBemerkung.getText());
        is.setDauer(SYSTools.parseBigDecimal(txtMinutes.getText()));

        if (!is.isValid()) {
            throw new NumberFormatException("Anzahl muss min. 1 sein");
        }

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

    private boolean isAtLeastOneTxtFieldNotZero() {
        boolean yesItIs = false;

        try {
            double result = Double.parseDouble(txtNachtMo.getText()) + Double.parseDouble(txtMorgens.getText()) +
                    Double.parseDouble(txtMittags.getText()) + Double.parseDouble(txtNachmittags.getText()) +
                    Double.parseDouble(txtAbends.getText()) + Double.parseDouble(txtNachtAb.getText());
            yesItIs = result > 0d;
        } catch (NumberFormatException nfe) {

        }

        return yesItIs;
    }

    private void txtNachtMoActionPerformed(ActionEvent evt) {//GEN-FIRST:event_txtNachtMoActionPerformed
        txtMorgens.requestFocus();
    }//GEN-LAST:event_txtNachtMoActionPerformed

    private void txtMorgensActionPerformed(ActionEvent evt) {//GEN-FIRST:event_txtMorgensActionPerformed
        txtMittags.requestFocus();
    }//GEN-LAST:event_txtMorgensActionPerformed

    private void txtMittagsActionPerformed(ActionEvent evt) {//GEN-FIRST:event_txtMittagsActionPerformed
        txtNachmittags.requestFocus();
    }//GEN-LAST:event_txtMittagsActionPerformed

    private void txtNachmittagsActionPerformed(ActionEvent evt) {//GEN-FIRST:event_txtNachmittagsActionPerformed
        txtAbends.requestFocus();
    }//GEN-LAST:event_txtNachmittagsActionPerformed

    private void txtAbendsActionPerformed(ActionEvent evt) {//GEN-FIRST:event_txtAbendsActionPerformed
        txtNachtAb.requestFocus();
    }//GEN-LAST:event_txtAbendsActionPerformed

    private void txtNachtAbActionPerformed(ActionEvent evt) {//GEN-FIRST:event_txtNachtAbActionPerformed
        txtNachtMo.requestFocus();
    }//GEN-LAST:event_txtNachtAbActionPerformed


    private void txtUhrzeitActionPerformed(ActionEvent evt) {//GEN-FIRST:event_txtUhrzeitActionPerformed
    }//GEN-LAST:event_txtUhrzeitActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel panelMain;
    private JSplitPane splitRegular;
    private JPanel pnlTageszeit;
    private JideLabel jLabel6;
    private JideLabel jLabel1;
    private JideLabel jLabel2;
    private JideLabel jLabel11;
    private JideLabel jLabel3;
    private JideLabel jLabel4;
    private JTextField txtNachtMo;
    private JTextField txtMorgens;
    private JTextField txtMittags;
    private JTextField txtNachmittags;
    private JTextField txtAbends;
    private JTextField txtNachtAb;
    private JButton btnToTime;
    private JPanel pnlUhrzeit;
    private JideLabel lblUhrzeit;
    private JButton btnToTimeOfDay;
    private JTextField txtUhrzeit;
    private JComboBox cmbUhrzeit;
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
    private JComboBox cmbTag;
    private JPanel panel2;
    private JLabel jLabel13;
    private JDateChooser jdcLDatum;
    private JLabel lblMinutes;
    private JTextField txtMinutes;
    private JPanel pnlBemerkung;
    private JScrollPane jScrollPane1;
    private JTextArea txtBemerkung;
    private JButton btnSave;
    // End of variables declaration//GEN-END:variables
}
