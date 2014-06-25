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
package op.care.prescription;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideLabel;
import com.jidesoft.swing.JideTabbedPane;
import entity.prescription.PrescriptionSchedule;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.CleanablePanel;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateMidnight;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author tloehr
 */
public class PnlScheduleDose extends CleanablePanel {
    public static final String internalClassID = PnlPrescription.internalClassID + ".pnlscheduledose";
    private int MIN = 0;
    private int MAX = 1;
    private int DEFAULT = 2;

    private int[] dailyModel = new int[]{1, 365, 1};
    private int[] weeklyModel = new int[]{1, 52, 1};
    private int[] monthlyModel = new int[]{1, 12, 1};
    private int[] perMonthModel = new int[]{1, 31, 1};
    private int[] weekdayModel = new int[]{1, 5, 1};

    private boolean ignoreEvent = false;
    private PrescriptionSchedule schedule;
    private Closure actionBlock;

    private double splitRegularPos;

    private final int TAB_DAILY = 0;
    private final int TAB_WEEKLY = 1;
    private final int TAB_MONTHLY = 2;

    public PnlScheduleDose(PrescriptionSchedule schedule, Closure actionBlock) {
        this.actionBlock = actionBlock;
//        this.currentSelectedTime = null;

        this.schedule = schedule;
        initComponents();
        initPanel();
    }

    private void btnToTimeActionPerformed(ActionEvent e) {
        splitRegularPos = SYSTools.showSide(splitRegular, SYSTools.RIGHT_LOWER_SIDE, SYSTools.SPEED_NORMAL);
        if (Double.parseDouble(txtTimeDose.getText()) == 0) {
            txtTimeDose.setText("1.0");
        }
//        currentSelectedTime = (Date) cmbUhrzeit.getSelectedItem();
    }

    private void btnToTimeOfDayActionPerformed(ActionEvent e) {
        splitRegularPos = SYSTools.showSide(splitRegular, SYSTools.LEFT_UPPER_SIDE, SYSTools.SPEED_NORMAL);
        if (!isAtLeastOneTxtFieldNotZero()) {
            txtTimeDose.setText("1.0");
        }
//        currentSelectedTime = null;
    }

    @Override
    public void reload() {

    }

    private void panelMainComponentResized(ComponentEvent e) {
        SYSTools.showSide(splitRegular, splitRegularPos);
    }

    private void cmbUhrzeitItemStateChanged(ItemEvent e) {
//        currentSelectedTime = (Date) e.getItem();
        lblTimeDose.setText(SYSTools.xx(internalClassID + ".lblTimeDose") + " " + DateFormat.getTimeInstance(DateFormat.SHORT).format(e.getItem()) + " " + SYSTools.xx("misc.msg.Time.short"));
    }

    private void btnJedenTagActionPerformed(ActionEvent e) {
        txtEveryDay.setText("1");
    }

    private void btnJedeWocheActionPerformed(ActionEvent e) {
        txtEveryWeek.setText("1");
    }

    private void btnJedenMonatActionPerformed(ActionEvent e) {
        txtEveryMonth.setText("1");
    }

    @Override
    public String getInternalClassID() {
        return null;
    }

    private void txtLDateFocusLost(FocusEvent evt) {
        SYSCalendar.handleDateFocusLost(evt, new DateMidnight(), new DateMidnight().plusWeeks(4));
    }

    private void txtEveryMonthFocusLost(FocusEvent e) {
        SYSTools.handleIntegerFocusLost(e, monthlyModel[MIN], monthlyModel[MAX], monthlyModel[DEFAULT]);
    }

    private void txtEveryWDayOfMonthFocusLost(FocusEvent e) {
        if (cmbWDay.getSelectedIndex() == 0) {
            SYSTools.handleIntegerFocusLost(e, perMonthModel[MIN], perMonthModel[MAX], perMonthModel[DEFAULT]);
        } else {
            SYSTools.handleIntegerFocusLost(e, weekdayModel[MIN], weekdayModel[MAX], weekdayModel[DEFAULT]);
        }
    }

    private void txtEveryWeekFocusLost(FocusEvent e) {
        SYSTools.handleIntegerFocusLost(e, weeklyModel[MIN], weeklyModel[MAX], weeklyModel[DEFAULT]);
    }

    private void txtEveryDayFocusLost(FocusEvent e) {
        SYSTools.handleIntegerFocusLost(e, dailyModel[MIN], dailyModel[MAX], dailyModel[DEFAULT]);
    }

    private void txtDoubleFocusLost(FocusEvent e) {
        SYSTools.handleBigDecimalFocusLost(e, BigDecimal.ZERO, new BigDecimal(10000), BigDecimal.ONE);
    }

    private void txtActionPerformed(ActionEvent e) {
        if (e.getSource().equals(txtVeryEarly)) {
            txtMorning.requestFocus();
        } else if (e.getSource().equals(txtMorning)) {
            txtNoon.requestFocus();
        } else if (e.getSource().equals(txtNoon)) {
            txtAfternoon.requestFocus();
        } else if (e.getSource().equals(txtAfternoon)) {
            txtEvening.requestFocus();
        } else if (e.getSource().equals(txtEvening)) {
            txtVeryLate.requestFocus();
        } else if (e.getSource().equals(txtVeryLate)) {
            txtVeryEarly.requestFocus();
        }
    }

    private void initPanel() {

        tabWdh.setTitleAt(0, SYSTools.xx("misc.msg.daily"));
        tabWdh.setTitleAt(1, SYSTools.xx("misc.msg.weekly"));
        tabWdh.setTitleAt(2, SYSTools.xx("misc.msg.monthly"));

        lblLDate.setText(SYSTools.xx(internalClassID + ".lblLDate") + " ");
        lblOnThe.setText(SYSTools.xx(internalClassID + ".lblOnThe"));
        lblMonth.setText(SYSTools.xx("misc.msg.months"));
        lblEach.setText(SYSTools.xx("misc.msg.every"));
        lblEvery1.setText(SYSTools.xx("misc.msg.every"));
        lblEvery2.setText(SYSTools.xx("misc.msg.every"));
        lblWeeksAt.setText(SYSTools.xx("misc.msg.weeks") + " " + SYSTools.xx("misc.msg.atchrono"));
        lblDays.setText(SYSTools.xx("misc.msg.Days2"));

        ArrayList<Date> timelist = SYSCalendar.getTimeList();
        cmbUhrzeit.setModel(new DefaultComboBoxModel(timelist.toArray()));
        cmbUhrzeit.setRenderer(SYSCalendar.getTimeRenderer());

        String[] wdaymodel = new String[]{SYSTools.xx("misc.msg.dayOfMonth"), SYSTools.xx("misc.msg.monday"), SYSTools.xx("misc.msg.tuesday"), SYSTools.xx("misc.msg.wednesday"),
                SYSTools.xx("misc.msg.thursday"), SYSTools.xx("misc.msg.friday"), SYSTools.xx("misc.msg.saturday"), SYSTools.xx("misc.msg.sunday")};
        cmbWDay.setModel(new DefaultComboBoxModel(wdaymodel));

        lblMon.setText(SYSTools.xx("misc.msg.monday"));
        lblTue.setText(SYSTools.xx("misc.msg.tuesday"));
        lblWed.setText(SYSTools.xx("misc.msg.wednesday"));
        lblThu.setText(SYSTools.xx("misc.msg.thursday"));
        lblFri.setText(SYSTools.xx("misc.msg.friday"));
        lblSat.setText(SYSTools.xx("misc.msg.saturday"));
        lblSun.setText(SYSTools.xx("misc.msg.sunday"));

        lblVeryEarly.setText(SYSTools.xx("misc.msg.earlyinthemorning.long"));
        lblMorning.setText(SYSTools.xx("misc.msg.morning.long"));
        lblNoon.setText(SYSTools.xx("misc.msg.noon.long"));
        lblAfternoon.setText(SYSTools.xx("misc.msg.afternoon.long"));
        lblEvening.setText(SYSTools.xx("misc.msg.evening.long"));
        lblVeryLate.setText(SYSTools.xx("misc.msg.lateatnight.long"));

        txtEveryDay.setText("1");
        txtEveryWeek.setText("1");
        txtEveryMonth.setText("1");
        txtEveryWDayOfMonth.setText("1");

//        txtEveryDay.setText(schedule.getTaeglich().toString());
//        txtEveryWeek.setText(schedule.getWoechentlich().toString());
//        txtEveryMonth.setText(schedule.getMonatlich().toString());
//        txtEveryWDayOfMonth.setText(schedule.getTagNum().toString());

        tabWdh.setSelectedIndex(TAB_DAILY);

        if (schedule.getWoechentlich() > 0) {
            cbMon.setSelected(schedule.getMon() > 0);
            cbTue.setSelected(schedule.getTue() > 0);
            cbWed.setSelected(schedule.getWed() > 0);
            cbThu.setSelected(schedule.getThu() > 0);
            cbFri.setSelected(schedule.getFri() > 0);
            cbSat.setSelected(schedule.getSat() > 0);
            cbSun.setSelected(schedule.getSun() > 0);
            tabWdh.setSelectedIndex(TAB_WEEKLY);
        }

        if (schedule.getMonatlich() > 0) {
            if (schedule.getTagNum() > 0) {
                txtEveryWDayOfMonth.setText(Short.toString(schedule.getTagNum()));
                cmbWDay.setSelectedIndex(0);
            } else {
                if (schedule.getMon() > 0) {
                    cmbWDay.setSelectedIndex(1);
                    txtEveryWDayOfMonth.setText(Short.toString(schedule.getMon()));
                } else if (schedule.getTue() > 0) {
                    cmbWDay.setSelectedIndex(2);
                    txtEveryWDayOfMonth.setText(Short.toString(schedule.getTue()));
                } else if (schedule.getWed() > 0) {
                    cmbWDay.setSelectedIndex(3);
                    txtEveryWDayOfMonth.setText(Short.toString(schedule.getWed()));
                } else if (schedule.getThu() > 0) {
                    cmbWDay.setSelectedIndex(4);
                    txtEveryWDayOfMonth.setText(Short.toString(schedule.getThu()));
                } else if (schedule.getFri() > 0) {
                    cmbWDay.setSelectedIndex(5);
                    txtEveryWDayOfMonth.setText(Short.toString(schedule.getFri()));
                } else if (schedule.getSat() > 0) {
                    cmbWDay.setSelectedIndex(6);
                    txtEveryWDayOfMonth.setText(Short.toString(schedule.getSat()));
                } else if (schedule.getSun() > 0) {
                    cmbWDay.setSelectedIndex(7);
                    txtEveryWDayOfMonth.setText(Short.toString(schedule.getSun()));
                }
            }
            tabWdh.setSelectedIndex(TAB_MONTHLY);
        }

//        jdcLDatum.setMinSelectableDate(new Date());
//        jdcLDatum.setDate(new Date(Math.max(schedule.getLDatum().getTime(), SYSCalendar.startOfDay())));

        DateMidnight scheduleLDate = new DateMidnight(schedule.getLDatum());
        DateMidnight today = new DateMidnight();
        DateMidnight ldate = new DateMidnight(Math.max(scheduleLDate.getMillis(), today.getMillis()));
        txtLDate.setText(DateFormat.getDateInstance().format(ldate.toDate()));

        txtVeryEarly.setText(schedule.getNachtMo().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        txtMorning.setText(schedule.getMorgens().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        txtNoon.setText(schedule.getMittags().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        txtAfternoon.setText(schedule.getNachmittags().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        txtEvening.setText(schedule.getAbends().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        txtVeryLate.setText(schedule.getNachtAb().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        txtTimeDose.setText(schedule.getUhrzeitDosis().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());

        txtMorning.setBackground(SYSConst.lightblue);
        txtNoon.setBackground(SYSConst.gold7);
        txtAfternoon.setBackground(SYSConst.melonrindgreen);
        txtEvening.setBackground(SYSConst.bermuda_sand);
        txtVeryLate.setBackground(SYSConst.bluegrey);

        Date now = null;
        if (schedule.getUhrzeitDosis().compareTo(BigDecimal.ZERO) > 0) {
            splitRegularPos = 0.0d;
            now = schedule.getUhrzeit();
        } else {
            now = new Date();
            splitRegularPos = 1.0d;
        }

        for (Date zeit : timelist) {
            if (SYSCalendar.compareTime(zeit, now) >= 0) {
                now = zeit;
                break;
            }
        }
        cmbUhrzeit.setSelectedItem(now);
        lblTimeDose.setText(SYSTools.xx(internalClassID + ".lblTimeDose") + " " + DateFormat.getTimeInstance(DateFormat.SHORT).format(now) + " " + SYSTools.xx("misc.msg.Time.short"));

        panelMainComponentResized(null);
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
        splitRegular = new JSplitPane();
        pnlTageszeit = new JPanel();
        lblVeryEarly = new JideLabel();
        lblMorning = new JideLabel();
        lblNoon = new JideLabel();
        lblAfternoon = new JideLabel();
        lblEvening = new JideLabel();
        lblVeryLate = new JideLabel();
        txtVeryEarly = new JTextField();
        txtMorning = new JTextField();
        txtNoon = new JTextField();
        txtAfternoon = new JTextField();
        txtEvening = new JTextField();
        txtVeryLate = new JTextField();
        btnToTime = new JButton();
        pnlUhrzeit = new JPanel();
        lblTimeDose = new JideLabel();
        btnToTimeOfDay = new JButton();
        txtTimeDose = new JTextField();
        cmbUhrzeit = new JComboBox();
        tabWdh = new JideTabbedPane();
        pnlDaily = new JPanel();
        lblEvery1 = new JLabel();
        txtEveryDay = new JTextField();
        lblDays = new JLabel();
        btnEveryDay = new JideButton();
        pnlWeekly = new JPanel();
        panel3 = new JPanel();
        lblEvery2 = new JLabel();
        txtEveryWeek = new JTextField();
        lblWeeksAt = new JLabel();
        btnEveryWeek = new JideButton();
        lblMon = new JideLabel();
        lblTue = new JideLabel();
        lblWed = new JideLabel();
        lblThu = new JideLabel();
        lblFri = new JideLabel();
        lblSat = new JideLabel();
        lblSun = new JideLabel();
        cbMon = new JCheckBox();
        cbTue = new JCheckBox();
        cbWed = new JCheckBox();
        cbThu = new JCheckBox();
        cbFri = new JCheckBox();
        cbSat = new JCheckBox();
        cbSun = new JCheckBox();
        pnlMonthly = new JPanel();
        lblEach = new JLabel();
        txtEveryMonth = new JTextField();
        lblMonth = new JLabel();
        btnEveryMonth = new JideButton();
        lblOnThe = new JLabel();
        txtEveryWDayOfMonth = new JTextField();
        cmbWDay = new JComboBox();
        panel2 = new JPanel();
        lblLDate = new JLabel();
        txtLDate = new JTextField();
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
                "$rgap, $lcgap, 223dlu, $lcgap, $rgap",
                "$rgap, 2*($lgap, pref), 2*($lgap, default), $lgap, $rgap"));

            //======== splitRegular ========
            {
                splitRegular.setDividerSize(0);
                splitRegular.setEnabled(false);
                splitRegular.setDividerLocation(300);
                splitRegular.setDoubleBuffered(true);

                //======== pnlTageszeit ========
                {
                    pnlTageszeit.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlTageszeit.setBorder(new EtchedBorder());
                    pnlTageszeit.setLayout(new FormLayout(
                        "6*(28dlu, $lcgap), default",
                        "fill:default, $lgap, fill:default"));

                    //---- lblVeryEarly ----
                    lblVeryEarly.setText("Nachts, fr\u00fch morgens");
                    lblVeryEarly.setOrientation(1);
                    lblVeryEarly.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblVeryEarly.setClockwise(false);
                    lblVeryEarly.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlTageszeit.add(lblVeryEarly, CC.xy(1, 1));

                    //---- lblMorning ----
                    lblMorning.setForeground(new Color(0, 0, 204));
                    lblMorning.setText("Morgens");
                    lblMorning.setOrientation(1);
                    lblMorning.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblMorning.setClockwise(false);
                    lblMorning.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlTageszeit.add(lblMorning, CC.xy(3, 1));

                    //---- lblNoon ----
                    lblNoon.setForeground(new Color(255, 102, 0));
                    lblNoon.setText("Mittags");
                    lblNoon.setOrientation(1);
                    lblNoon.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblNoon.setClockwise(false);
                    lblNoon.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlTageszeit.add(lblNoon, CC.xy(5, 1));

                    //---- lblAfternoon ----
                    lblAfternoon.setForeground(new Color(0, 153, 51));
                    lblAfternoon.setText("Nachmittag");
                    lblAfternoon.setOrientation(1);
                    lblAfternoon.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblAfternoon.setClockwise(false);
                    lblAfternoon.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlTageszeit.add(lblAfternoon, CC.xy(7, 1));

                    //---- lblEvening ----
                    lblEvening.setForeground(new Color(255, 0, 51));
                    lblEvening.setText("Abends");
                    lblEvening.setOrientation(1);
                    lblEvening.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblEvening.setClockwise(false);
                    lblEvening.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlTageszeit.add(lblEvening, CC.xy(9, 1));

                    //---- lblVeryLate ----
                    lblVeryLate.setText("Nacht, sp\u00e4t abends");
                    lblVeryLate.setOrientation(1);
                    lblVeryLate.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblVeryLate.setClockwise(false);
                    lblVeryLate.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlTageszeit.add(lblVeryLate, CC.xy(11, 1));

                    //---- txtVeryEarly ----
                    txtVeryEarly.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtVeryEarly.setText("0.0");
                    txtVeryEarly.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtVeryEarly.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtFocusGained(e);
                        }
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtDoubleFocusLost(e);
                        }
                    });
                    txtVeryEarly.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtActionPerformed(e);
                        }
                    });
                    pnlTageszeit.add(txtVeryEarly, CC.xy(1, 3));

                    //---- txtMorning ----
                    txtMorning.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtMorning.setText("1.0");
                    txtMorning.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtMorning.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtFocusGained(e);
                        }
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtDoubleFocusLost(e);
                        }
                    });
                    txtMorning.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtActionPerformed(e);
                        }
                    });
                    pnlTageszeit.add(txtMorning, CC.xy(3, 3));

                    //---- txtNoon ----
                    txtNoon.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtNoon.setText("0.0");
                    txtNoon.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtNoon.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtFocusGained(e);
                        }
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtDoubleFocusLost(e);
                        }
                    });
                    txtNoon.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtActionPerformed(e);
                        }
                    });
                    pnlTageszeit.add(txtNoon, CC.xy(5, 3));

                    //---- txtAfternoon ----
                    txtAfternoon.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtAfternoon.setText("0.0");
                    txtAfternoon.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtAfternoon.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtFocusGained(e);
                        }
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtDoubleFocusLost(e);
                        }
                    });
                    txtAfternoon.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtActionPerformed(e);
                        }
                    });
                    pnlTageszeit.add(txtAfternoon, CC.xy(7, 3));

                    //---- txtEvening ----
                    txtEvening.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtEvening.setText("0.0");
                    txtEvening.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtEvening.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtFocusGained(e);
                        }
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtDoubleFocusLost(e);
                        }
                    });
                    txtEvening.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtActionPerformed(e);
                        }
                    });
                    pnlTageszeit.add(txtEvening, CC.xy(9, 3));

                    //---- txtVeryLate ----
                    txtVeryLate.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtVeryLate.setText("0.0");
                    txtVeryLate.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtVeryLate.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtFocusGained(e);
                        }
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtDoubleFocusLost(e);
                        }
                    });
                    txtVeryLate.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtActionPerformed(e);
                        }
                    });
                    pnlTageszeit.add(txtVeryLate, CC.xy(11, 3));

                    //---- btnToTime ----
                    btnToTime.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/clock.png")));
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
                        "default, $ugap, 28dlu, $ugap, pref",
                        "default:grow, $rgap, default"));

                    //---- lblTimeDose ----
                    lblTimeDose.setText("Dosis zur Uhrzeit");
                    lblTimeDose.setOrientation(1);
                    lblTimeDose.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblTimeDose.setClockwise(false);
                    lblTimeDose.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlUhrzeit.add(lblTimeDose, CC.xy(3, 1, CC.DEFAULT, CC.BOTTOM));

                    //---- btnToTimeOfDay ----
                    btnToTimeOfDay.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/1rightarrow.png")));
                    btnToTimeOfDay.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnToTimeOfDayActionPerformed(e);
                        }
                    });
                    pnlUhrzeit.add(btnToTimeOfDay, CC.xy(1, 3));

                    //---- txtTimeDose ----
                    txtTimeDose.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtTimeDose.setText("0.0");
                    txtTimeDose.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtTimeDose.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtFocusGained(e);
                        }
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtDoubleFocusLost(e);
                        }
                    });
                    pnlUhrzeit.add(txtTimeDose, CC.xy(3, 3));

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

                    //---- lblEvery1 ----
                    lblEvery1.setText("alle");
                    lblEvery1.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlDaily.add(lblEvery1, CC.xy(2, 3));

                    //---- txtEveryDay ----
                    txtEveryDay.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtEveryDay.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtEveryDayFocusLost(e);
                        }
                    });
                    pnlDaily.add(txtEveryDay, CC.xy(5, 3));

                    //---- lblDays ----
                    lblDays.setText("Tage");
                    lblDays.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlDaily.add(lblDays, CC.xy(7, 3));

                    //---- btnEveryDay ----
                    btnEveryDay.setText("Jeden Tag");
                    btnEveryDay.setButtonStyle(3);
                    btnEveryDay.setFont(new Font("Arial", Font.BOLD, 14));
                    btnEveryDay.setForeground(Color.blue);
                    btnEveryDay.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnJedenTagActionPerformed(e);
                        }
                    });
                    pnlDaily.add(btnEveryDay, CC.xywh(2, 5, 6, 1));
                }
                tabWdh.addTab("T\u00e4glich", pnlDaily);


                //======== pnlWeekly ========
                {
                    pnlWeekly.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlWeekly.setLayout(new FormLayout(
                        "default, 7*(13dlu), $lcgap, default:grow",
                        "$ugap, $lgap, default, $lgap, pref, $lgap, default:grow, $lgap, $rgap"));

                    //======== panel3 ========
                    {
                        panel3.setLayout(new FormLayout(
                            "default, $rgap, 40dlu, $rgap, 2*(default)",
                            "default:grow, $lgap, default"));

                        //---- lblEvery2 ----
                        lblEvery2.setText("alle");
                        lblEvery2.setFont(new Font("Arial", Font.PLAIN, 14));
                        panel3.add(lblEvery2, CC.xy(1, 1));

                        //---- txtEveryWeek ----
                        txtEveryWeek.addFocusListener(new FocusAdapter() {
                            @Override
                            public void focusLost(FocusEvent e) {
                                txtEveryWeekFocusLost(e);
                            }
                        });
                        panel3.add(txtEveryWeek, CC.xy(3, 1));

                        //---- lblWeeksAt ----
                        lblWeeksAt.setText("Wochen am");
                        lblWeeksAt.setFont(new Font("Arial", Font.PLAIN, 14));
                        panel3.add(lblWeeksAt, CC.xy(5, 1));

                        //---- btnEveryWeek ----
                        btnEveryWeek.setText("Jede Woche");
                        btnEveryWeek.setFont(new Font("Arial", Font.BOLD, 14));
                        btnEveryWeek.setButtonStyle(3);
                        btnEveryWeek.setForeground(Color.blue);
                        btnEveryWeek.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnJedeWocheActionPerformed(e);
                            }
                        });
                        panel3.add(btnEveryWeek, CC.xywh(1, 3, 5, 1));
                    }
                    pnlWeekly.add(panel3, CC.xywh(2, 3, 9, 1));

                    //---- lblMon ----
                    lblMon.setText("montags");
                    lblMon.setOrientation(1);
                    lblMon.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblMon.setClockwise(false);
                    lblMon.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlWeekly.add(lblMon, CC.xy(2, 5, CC.CENTER, CC.BOTTOM));

                    //---- lblTue ----
                    lblTue.setText("dienstags");
                    lblTue.setOrientation(1);
                    lblTue.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblTue.setClockwise(false);
                    lblTue.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlWeekly.add(lblTue, CC.xy(3, 5, CC.CENTER, CC.BOTTOM));

                    //---- lblWed ----
                    lblWed.setText("mittwochs");
                    lblWed.setOrientation(1);
                    lblWed.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblWed.setClockwise(false);
                    lblWed.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlWeekly.add(lblWed, CC.xy(4, 5, CC.CENTER, CC.BOTTOM));

                    //---- lblThu ----
                    lblThu.setText("donnerstags");
                    lblThu.setOrientation(1);
                    lblThu.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblThu.setClockwise(false);
                    lblThu.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlWeekly.add(lblThu, CC.xy(5, 5, CC.CENTER, CC.BOTTOM));

                    //---- lblFri ----
                    lblFri.setText("freitags");
                    lblFri.setOrientation(1);
                    lblFri.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblFri.setClockwise(false);
                    lblFri.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlWeekly.add(lblFri, CC.xy(6, 5, CC.CENTER, CC.BOTTOM));

                    //---- lblSat ----
                    lblSat.setText("samstags");
                    lblSat.setOrientation(1);
                    lblSat.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblSat.setClockwise(false);
                    lblSat.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlWeekly.add(lblSat, CC.xy(7, 5, CC.CENTER, CC.BOTTOM));

                    //---- lblSun ----
                    lblSun.setText("sonntags");
                    lblSun.setOrientation(1);
                    lblSun.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblSun.setClockwise(false);
                    lblSun.setHorizontalTextPosition(SwingConstants.LEFT);
                    pnlWeekly.add(lblSun, CC.xy(8, 5, CC.CENTER, CC.BOTTOM));

                    //---- cbMon ----
                    cbMon.setBorder(BorderFactory.createEmptyBorder());
                    cbMon.setMargin(new Insets(0, 0, 0, 0));
                    cbMon.setSelected(true);
                    cbMon.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbMonActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbMon, CC.xy(2, 7, CC.CENTER, CC.DEFAULT));

                    //---- cbTue ----
                    cbTue.setBorder(BorderFactory.createEmptyBorder());
                    cbTue.setMargin(new Insets(0, 0, 0, 0));
                    cbTue.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbTueActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbTue, CC.xy(3, 7, CC.CENTER, CC.DEFAULT));

                    //---- cbWed ----
                    cbWed.setBorder(BorderFactory.createEmptyBorder());
                    cbWed.setMargin(new Insets(0, 0, 0, 0));
                    cbWed.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbWedActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbWed, CC.xy(4, 7, CC.CENTER, CC.DEFAULT));

                    //---- cbThu ----
                    cbThu.setBorder(BorderFactory.createEmptyBorder());
                    cbThu.setMargin(new Insets(0, 0, 0, 0));
                    cbThu.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbThuActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbThu, CC.xy(5, 7, CC.CENTER, CC.DEFAULT));

                    //---- cbFri ----
                    cbFri.setBorder(BorderFactory.createEmptyBorder());
                    cbFri.setMargin(new Insets(0, 0, 0, 0));
                    cbFri.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbFriActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbFri, CC.xy(6, 7, CC.CENTER, CC.DEFAULT));

                    //---- cbSat ----
                    cbSat.setBorder(BorderFactory.createEmptyBorder());
                    cbSat.setMargin(new Insets(0, 0, 0, 0));
                    cbSat.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbSatActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbSat, CC.xy(7, 7, CC.CENTER, CC.DEFAULT));

                    //---- cbSun ----
                    cbSun.setBorder(BorderFactory.createEmptyBorder());
                    cbSun.setMargin(new Insets(0, 0, 0, 0));
                    cbSun.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbSunActionPerformed(e);
                        }
                    });
                    pnlWeekly.add(cbSun, CC.xy(8, 7, CC.CENTER, CC.DEFAULT));
                }
                tabWdh.addTab("W\u00f6chentlich", pnlWeekly);


                //======== pnlMonthly ========
                {
                    pnlMonthly.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlMonthly.setLayout(new FormLayout(
                        "default, $lcgap, pref, $lcgap, 40dlu, $lcgap, pref, $lcgap, 61dlu, $lcgap, default",
                        "2*(default, $lgap), default"));

                    //---- lblEach ----
                    lblEach.setText("jeden");
                    lblEach.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblEach.setHorizontalAlignment(SwingConstants.TRAILING);
                    pnlMonthly.add(lblEach, CC.xy(3, 3));

                    //---- txtEveryMonth ----
                    txtEveryMonth.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtEveryMonth.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtEveryMonthFocusLost(e);
                        }
                    });
                    pnlMonthly.add(txtEveryMonth, CC.xy(5, 3));

                    //---- lblMonth ----
                    lblMonth.setText("Monat");
                    lblMonth.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlMonthly.add(lblMonth, CC.xy(7, 3));

                    //---- btnEveryMonth ----
                    btnEveryMonth.setText("Jeden Monat");
                    btnEveryMonth.setFont(new Font("Arial", Font.BOLD, 14));
                    btnEveryMonth.setButtonStyle(3);
                    btnEveryMonth.setForeground(Color.blue);
                    btnEveryMonth.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnJedenMonatActionPerformed(e);
                        }
                    });
                    pnlMonthly.add(btnEveryMonth, CC.xy(9, 3));

                    //---- lblOnThe ----
                    lblOnThe.setText("jeweils am");
                    lblOnThe.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblOnThe.setHorizontalAlignment(SwingConstants.TRAILING);
                    pnlMonthly.add(lblOnThe, CC.xy(3, 5));

                    //---- txtEveryWDayOfMonth ----
                    txtEveryWDayOfMonth.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtEveryWDayOfMonth.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtEveryWDayOfMonthFocusLost(e);
                        }
                    });
                    pnlMonthly.add(txtEveryWDayOfMonth, CC.xy(5, 5));

                    //---- cmbWDay ----
                    cmbWDay.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlMonthly.add(cmbWDay, CC.xywh(7, 5, 3, 1));
                }
                tabWdh.addTab("Monatlich", pnlMonthly);

            }
            panelMain.add(tabWdh, CC.xy(3, 5, CC.FILL, CC.FILL));

            //======== panel2 ========
            {
                panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

                //---- lblLDate ----
                lblLDate.setText("Erst einplanen ab dem ");
                lblLDate.setFont(new Font("Arial", Font.PLAIN, 14));
                panel2.add(lblLDate);

                //---- txtLDate ----
                txtLDate.setFont(new Font("Arial", Font.PLAIN, 14));
                txtLDate.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtLDateFocusLost(e);
                    }
                });
                panel2.add(txtLDate);
            }
            panelMain.add(panel2, CC.xy(3, 7));

            //---- btnSave ----
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });
            panelMain.add(btnSave, CC.xy(3, 9, CC.RIGHT, CC.DEFAULT));
        }
        add(panelMain, BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    @Override
    public void cleanup() {
        SYSTools.unregisterListeners(this);
//        jdcLDatum.removePropertyChangeListener(jdcpcl);
//        jdcLDatum.cleanup();
    }

    private void cbSunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSunActionPerformed

        if (!(cbSun.isSelected() || cbSat.isSelected() || cbFri.isSelected() || cbThu.isSelected() || cbWed.isSelected() || cbTue.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".error.atLeastOneWeekDay"));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

    }//GEN-LAST:event_cbSunActionPerformed

    private void cbSatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSatActionPerformed
        if (!(cbSun.isSelected() || cbSat.isSelected() || cbFri.isSelected() || cbThu.isSelected() || cbWed.isSelected() || cbTue.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".error.atLeastOneWeekDay"));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbSatActionPerformed

    private void cbFriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFriActionPerformed
        if (!(cbSun.isSelected() || cbSat.isSelected() || cbFri.isSelected() || cbThu.isSelected() || cbWed.isSelected() || cbTue.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".error.atLeastOneWeekDay"));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbFriActionPerformed

    private void cbThuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbThuActionPerformed
        if (!(cbSun.isSelected() || cbSat.isSelected() || cbFri.isSelected() || cbThu.isSelected() || cbWed.isSelected() || cbTue.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".error.atLeastOneWeekDay"));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbThuActionPerformed

    private void cbWedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbWedActionPerformed
        if (!(cbSun.isSelected() || cbSat.isSelected() || cbFri.isSelected() || cbThu.isSelected() || cbWed.isSelected() || cbTue.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".error.atLeastOneWeekDay"));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbWedActionPerformed

    private void cbTueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTueActionPerformed
        if (!(cbSun.isSelected() || cbSat.isSelected() || cbFri.isSelected() || cbThu.isSelected() || cbWed.isSelected() || cbTue.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".error.atLeastOneWeekDay"));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbTueActionPerformed

    private void cbMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMonActionPerformed
        if (!(cbSun.isSelected() || cbSat.isSelected() || cbFri.isSelected() || cbThu.isSelected() || cbWed.isSelected() || cbTue.isSelected() || cbMon.isSelected())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".error.atLeastOneWeekDay"));
            ((JCheckBox) evt.getSource()).setSelected(true);
        }
    }//GEN-LAST:event_cbMonActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        try {
            save();
            actionBlock.execute(schedule);
        } catch (NumberFormatException nfe) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".error.wrongDoseEntry"));
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    public void save() throws NumberFormatException {

        boolean splitSetToTime = splitRegularPos == 0d;

        if (!isAtLeastOneTxtFieldNotZero() && Double.parseDouble(txtTimeDose.getText()) == 0d) {
            throw new NumberFormatException("All Doses are ZERO");
        }

        schedule.setNachtMo(splitSetToTime ? BigDecimal.ZERO : new BigDecimal(Double.parseDouble(txtVeryEarly.getText())));
        schedule.setMorgens(splitSetToTime ? BigDecimal.ZERO : new BigDecimal(Double.parseDouble(txtMorning.getText())));
        schedule.setMittags(splitSetToTime ? BigDecimal.ZERO : new BigDecimal(Double.parseDouble(txtNoon.getText())));
        schedule.setNachmittags(splitSetToTime ? BigDecimal.ZERO : new BigDecimal(Double.parseDouble(txtAfternoon.getText())));
        schedule.setAbends(splitSetToTime ? BigDecimal.ZERO : new BigDecimal(Double.parseDouble(txtEvening.getText())));
        schedule.setNachtAb(splitSetToTime ? BigDecimal.ZERO : new BigDecimal(Double.parseDouble(txtVeryLate.getText())));
        schedule.setUhrzeitDosis(splitSetToTime ? new BigDecimal(Double.parseDouble(txtTimeDose.getText())) : BigDecimal.ZERO);
        schedule.setUhrzeit(splitSetToTime ? (Date) cmbUhrzeit.getSelectedItem() : null);

        schedule.setMaxAnzahl(0);
        schedule.setMaxEDosis(BigDecimal.ZERO);

        schedule.setTaeglich(tabWdh.getSelectedIndex() == TAB_DAILY ? Short.parseShort(txtEveryDay.getText()) : (short) 0);
        schedule.setWoechentlich(tabWdh.getSelectedIndex() == TAB_WEEKLY ? Short.parseShort(txtEveryWeek.getText()) : (short) 0);
        schedule.setMonatlich(tabWdh.getSelectedIndex() == TAB_MONTHLY ? Short.parseShort(txtEveryMonth.getText()) : (short) 0);

        DateMidnight day;
        try {
            day = new DateMidnight(SYSCalendar.parseDate(txtLDate.getText()));
        } catch (NumberFormatException ex) {
            day = new DateMidnight();
        }
        schedule.setLDatum(day.toDate());

        schedule.setMon(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbMon.isSelected() ? (short) 1 : (short) 0);
        schedule.setTue(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbTue.isSelected() ? (short) 1 : (short) 0);
        schedule.setWed(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbWed.isSelected() ? (short) 1 : (short) 0);
        schedule.setThu(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbThu.isSelected() ? (short) 1 : (short) 0);
        schedule.setFri(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbFri.isSelected() ? (short) 1 : (short) 0);
        schedule.setSat(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbSat.isSelected() ? (short) 1 : (short) 0);
        schedule.setSun(tabWdh.getSelectedIndex() == TAB_WEEKLY && cbSun.isSelected() ? (short) 1 : (short) 0);

        if (tabWdh.getSelectedIndex() == TAB_MONTHLY) {
            short s = Short.parseShort(txtEveryWDayOfMonth.getText());
            schedule.setTagNum(cmbWDay.getSelectedIndex() == 0 ? s : (short) 0);

            if (cmbWDay.getSelectedIndex() == 1) {
                schedule.setMon(s);
            } else if (cmbWDay.getSelectedIndex() == 2) {
                schedule.setTue(s);
            } else if (cmbWDay.getSelectedIndex() == 3) {
                schedule.setWed(s);
            } else if (cmbWDay.getSelectedIndex() == 4) {
                schedule.setThu(s);
            } else if (cmbWDay.getSelectedIndex() == 5) {
                schedule.setFri(s);
            } else if (cmbWDay.getSelectedIndex() == 6) {
                schedule.setSat(s);
            } else if (cmbWDay.getSelectedIndex() == 7) {
                schedule.setSun(s);
            }
        }

    }

    private void txtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFocusGained
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
            double result = Double.parseDouble(txtVeryEarly.getText()) + Double.parseDouble(txtMorning.getText()) +
                    Double.parseDouble(txtNoon.getText()) + Double.parseDouble(txtAfternoon.getText()) +
                    Double.parseDouble(txtEvening.getText()) + Double.parseDouble(txtVeryLate.getText());
            yesItIs = result > 0d;
        } catch (NumberFormatException nfe) {

        }

        return yesItIs;
    }


    private void txtUhrzeitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUhrzeitActionPerformed
    }//GEN-LAST:event_txtUhrzeitActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel panelMain;
    private JSplitPane splitRegular;
    private JPanel pnlTageszeit;
    private JideLabel lblVeryEarly;
    private JideLabel lblMorning;
    private JideLabel lblNoon;
    private JideLabel lblAfternoon;
    private JideLabel lblEvening;
    private JideLabel lblVeryLate;
    private JTextField txtVeryEarly;
    private JTextField txtMorning;
    private JTextField txtNoon;
    private JTextField txtAfternoon;
    private JTextField txtEvening;
    private JTextField txtVeryLate;
    private JButton btnToTime;
    private JPanel pnlUhrzeit;
    private JideLabel lblTimeDose;
    private JButton btnToTimeOfDay;
    private JTextField txtTimeDose;
    private JComboBox cmbUhrzeit;
    private JideTabbedPane tabWdh;
    private JPanel pnlDaily;
    private JLabel lblEvery1;
    private JTextField txtEveryDay;
    private JLabel lblDays;
    private JideButton btnEveryDay;
    private JPanel pnlWeekly;
    private JPanel panel3;
    private JLabel lblEvery2;
    private JTextField txtEveryWeek;
    private JLabel lblWeeksAt;
    private JideButton btnEveryWeek;
    private JideLabel lblMon;
    private JideLabel lblTue;
    private JideLabel lblWed;
    private JideLabel lblThu;
    private JideLabel lblFri;
    private JideLabel lblSat;
    private JideLabel lblSun;
    private JCheckBox cbMon;
    private JCheckBox cbTue;
    private JCheckBox cbWed;
    private JCheckBox cbThu;
    private JCheckBox cbFri;
    private JCheckBox cbSat;
    private JCheckBox cbSun;
    private JPanel pnlMonthly;
    private JLabel lblEach;
    private JTextField txtEveryMonth;
    private JLabel lblMonth;
    private JideButton btnEveryMonth;
    private JLabel lblOnThe;
    private JTextField txtEveryWDayOfMonth;
    private JComboBox cmbWDay;
    private JPanel panel2;
    private JLabel lblLDate;
    private JTextField txtLDate;
    private JButton btnSave;
    // End of variables declaration//GEN-END:variables
}
