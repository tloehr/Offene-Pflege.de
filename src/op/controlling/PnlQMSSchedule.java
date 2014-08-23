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
import com.jidesoft.combobox.TreeComboBox;
import com.jidesoft.swing.JideLabel;
import com.jidesoft.swing.JideTabbedPane;
import com.toedter.calendar.JDateChooser;
import entity.Homes;
import entity.Station;
import entity.StationTools;
import entity.qms.QmsTools;
import entity.qms.Qmssched;
import op.tools.GUITools;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateTimeConstants;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

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
    private final int TAB_YEARLY = 3;
    private final int[] maxdays = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public PnlQMSSchedule(Qmssched qmssched, Closure actionBlock) {
        this.qmssched = qmssched;
        this.actionBlock = actionBlock;

        initComponents();
        initPanel();
    }


    private void panelMainComponentResized(ComponentEvent e) {
//        SYSTools.showSide(splitRegular, splitRegularPos);
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

    private void cmbTagItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (cmbTag.getSelectedIndex() == 0) {
                spinDayInMonth.setModel(new SpinnerNumberModel(1, 1, 31, 1));
            } else {
                spinDayInMonth.setModel(new SpinnerNumberModel(1, 1, 5, 1));
            }
        }
    }

    private void btnEveryYearActionPerformed(ActionEvent e) {
        spinYearly.setValue(1);
    }

    private void i18n() {
        tabWdh.setTitleAt(TAB_DAILY, SYSTools.xx("misc.msg.daily"));
        tabWdh.setTitleAt(TAB_WEEKLY, SYSTools.xx("misc.msg.weekly"));
        tabWdh.setTitleAt(TAB_MONTHLY, SYSTools.xx("misc.msg.monthly"));
        tabWdh.setTitleAt(TAB_YEARLY, SYSTools.xx("misc.msg.yearly"));

        lblEveryDay.setText(SYSTools.xx("misc.msg.every"));
        lblDays.setText(SYSTools.xx("misc.msg.Days2"));
        btnJedenTag.setText(SYSTools.xx("misc.msg.everyDay"));

        lblEveryWeek.setText(SYSTools.xx("misc.msg.every"));
        lblWeeks.setText(SYSTools.xx("misc.msg.weeks"));
        btnJedeWoche.setText(SYSTools.xx("misc.msg.everyWeek"));

        cbMon.setText(SYSTools.xx("misc.msg.monday"));
        cbDie.setText(SYSTools.xx("misc.msg.tuesday"));
        cbMit.setText(SYSTools.xx("misc.msg.wednesday"));
        cbDon.setText(SYSTools.xx("misc.msg.thursday"));
        cbFre.setText(SYSTools.xx("misc.msg.friday"));
        cbSam.setText(SYSTools.xx("misc.msg.saturday"));
        cbSon.setText(SYSTools.xx("misc.msg.sunday"));

        lblEveryMonth.setText(SYSTools.xx("misc.msg.every3"));
        lblMonth.setText(SYSTools.xx("misc.msg.month"));
        btnJedenMonat.setText(SYSTools.xx("misc.msg.everyMonth"));
        cmbTag.setModel(new DefaultComboBoxModel<>(new String[]{
                SYSTools.xx("misc.msg.dayOfMonth"),
                SYSTools.xx("misc.msg.monday"),
                SYSTools.xx("misc.msg.tuesday"),
                SYSTools.xx("misc.msg.wednesday"),
                SYSTools.xx("misc.msg.thursday"),
                SYSTools.xx("misc.msg.friday"),
                SYSTools.xx("misc.msg.saturday"),
                SYSTools.xx("misc.msg.sunday")
        }));

        lblEveryYear.setText(SYSTools.xx("misc.msg.every"));
        lblYear.setText(SYSTools.xx("misc.msg.Years"));
        btnEveryYear.setText(SYSTools.xx("misc.msg.everyYear"));
        lblOnDay.setText(SYSTools.xx("misc.msg.atchrono"));

        lblMeasure.setText(SYSTools.xx("misc.msg.measure"));
        lblLDate.setText(SYSTools.xx("opde.controlling.qms.dlgqmsplan.pnlschedule.startingon"));
        lblLocation.setText(SYSTools.xx("opde.controlling.qms.dlgqmsplan.pnlschedule.location"));
        lblDueDays.setText(SYSTools.xx("opde.controlling.qms.dlgqmsplan.pnlschedule.duedays"));


        cmbMonth.setModel(new DefaultComboBoxModel<>(new String[]{
                SYSTools.xx("misc.msg.january"),
                SYSTools.xx("misc.msg.february"),
                SYSTools.xx("misc.msg.march"),
                SYSTools.xx("misc.msg.april"),
                SYSTools.xx("misc.msg.may"),
                SYSTools.xx("misc.msg.june"),
                SYSTools.xx("misc.msg.july"),
                SYSTools.xx("misc.msg.august"),
                SYSTools.xx("misc.msg.september"),
                SYSTools.xx("misc.msg.october"),
                SYSTools.xx("misc.msg.november"),
                SYSTools.xx("misc.msg.december"),
        }));


    }

    private void initPanel() {


        i18n();

        txtDueDays.setText(Integer.toString(qmssched.getDuedays()));

        spinTaeglich.setModel(new SpinnerNumberModel(1, 1, 365, 1));
        spinWoche.setModel(new SpinnerNumberModel(1, 1, 52, 1));
        spinMonat.setModel(new SpinnerNumberModel(1, 1, 12, 1));
        spinYearly.setModel(new SpinnerNumberModel(1, 1, 10, 1));
        spinDayInMonth.setModel(new SpinnerNumberModel(1, 1, 31, 1));

        spinTaeglich.setValue(Math.max(qmssched.getDaily(), 1));
        spinWoche.setValue(Math.max(qmssched.getWeekly(), 1));
        spinMonat.setValue(Math.max(qmssched.getMonthly(), 1));
        spinDayInMonth.setValue(Math.max(qmssched.getDayinmonth(), 1));
        spinYearly.setValue(Math.max(qmssched.getYearly(), 1));

        cbMon.setSelected(true);

        cmbMonth.setSelectedIndex(qmssched.isYearly() ? qmssched.getMonthinyear() - 1 : 0);
        spinDayInMonthInYear.setModel(new SpinnerNumberModel(qmssched.isYearly() ? qmssched.getDayinmonth() : 1, 1, maxdays[cmbMonth.getSelectedIndex()], 1));
        cmbMonth.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    spinDayInMonthInYear.setModel(new SpinnerNumberModel(1, 1, maxdays[cmbMonth.getSelectedIndex()], 1));
                }
            }
        });

        if (qmssched.isDaily()) {
            tabWdh.setSelectedIndex(TAB_DAILY);
        } else if (qmssched.isWeekly()) {
            cbMon.setSelected(qmssched.getWeekday() == DateTimeConstants.MONDAY);
            cbDie.setSelected(qmssched.getWeekday() == DateTimeConstants.TUESDAY);
            cbMit.setSelected(qmssched.getWeekday() == DateTimeConstants.WEDNESDAY);
            cbDon.setSelected(qmssched.getWeekday() == DateTimeConstants.THURSDAY);
            cbFre.setSelected(qmssched.getWeekday() == DateTimeConstants.FRIDAY);
            cbSam.setSelected(qmssched.getWeekday() == DateTimeConstants.SATURDAY);
            cbSon.setSelected(qmssched.getWeekday() == DateTimeConstants.SUNDAY);

            tabWdh.setSelectedIndex(TAB_WEEKLY);
        } else if (qmssched.isMonthly()) {

            spinDayInMonth.setValue(qmssched.getDayinmonth());
            cmbTag.setSelectedIndex(qmssched.getWeekday());

            tabWdh.setSelectedIndex(TAB_MONTHLY);
        } else if (qmssched.isYearly()) {
            spinYearly.setValue(qmssched.getYearly());
            spinDayInMonthInYear.setValue(qmssched.getDayinmonth());
            cmbMonth.setSelectedIndex(qmssched.getMonthinyear() - 1);
            tabWdh.setSelectedIndex(TAB_YEARLY);
        }

        jdcStartingOn.setMinSelectableDate(new Date());
        jdcStartingOn.setDate(qmssched.getStartingOn());

        ArrayList<Date> timelist = SYSCalendar.getTimeList();
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel(timelist.toArray());
        dcbm.insertElementAt(null, 0);


        txtBemerkung.setText(qmssched.getText());
        txtQMS.setText(qmssched.getMeasure());

        cmbLocation.setTreeModel(new DefaultTreeModel(StationTools.getCompleteStructure()));


        Object userObject = null;
        if (qmssched.getHome() != null) {
            userObject = qmssched.getHome();
        } else if (qmssched.getStation() != null) {
            userObject = qmssched.getStation();
        }

        int row = 0;
        if (userObject == null) {
            cmbLocation.setSelectedItem(null);
        } else {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) cmbLocation.getTreeModel().getRoot();
            DefaultMutableTreeNode theNode = null;
            for (Enumeration e = root.depthFirstEnumeration(); e.hasMoreElements() && theNode == null; ) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
                if (userObject.equals(node.getUserObject())) {
                    cmbLocation.setSelectedItem(node);
                    break;
                }
                row++;
            }
        }


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
        lblLocation = new JLabel();
        cmbLocation = new TreeComboBox();
        tabWdh = new JideTabbedPane();
        pnlDaily = new JPanel();
        lblEveryDay = new JLabel();
        spinTaeglich = new JSpinner();
        lblDays = new JLabel();
        btnJedenTag = new JButton();
        pnlWeekly = new JPanel();
        panel3 = new JPanel();
        btnJedeWoche = new JButton();
        lblEveryWeek = new JLabel();
        spinWoche = new JSpinner();
        lblWeeks = new JLabel();
        lblUhrzeit2 = new JideLabel();
        lblUhrzeit3 = new JideLabel();
        lblUhrzeit4 = new JideLabel();
        lblUhrzeit5 = new JideLabel();
        lblUhrzeit6 = new JideLabel();
        lblUhrzeit7 = new JideLabel();
        lblUhrzeit8 = new JideLabel();
        cbMon = new JRadioButton();
        cbDie = new JRadioButton();
        cbMit = new JRadioButton();
        cbDon = new JRadioButton();
        cbFre = new JRadioButton();
        cbSam = new JRadioButton();
        cbSon = new JRadioButton();
        pnlMonthly = new JPanel();
        lblEveryMonth = new JLabel();
        spinMonat = new JSpinner();
        lblMonth = new JLabel();
        btnJedenMonat = new JButton();
        llblOnDayOfMonth = new JLabel();
        spinDayInMonth = new JSpinner();
        cmbTag = new JComboBox<>();
        pnlYearly = new JPanel();
        lblEveryYear = new JLabel();
        spinYearly = new JSpinner();
        lblYear = new JLabel();
        btnEveryYear = new JButton();
        lblOnDay = new JLabel();
        spinDayInMonthInYear = new JSpinner();
        cmbMonth = new JComboBox();
        lblLDate = new JLabel();
        jdcStartingOn = new JDateChooser();
        jScrollPane1 = new JScrollPane();
        txtBemerkung = new JTextArea();
        btnSave = new JButton();
        lblDueDays = new JLabel();
        txtDueDays = GUITools.createIntegerTextField(1, 31, 1);

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
                    "$rgap, $lcgap, 35dlu:grow, $ugap, 105dlu:grow, $lcgap, $rgap",
                    "default, $nlgap, 18dlu, $lgap, default, $nlgap, 2*(default, $lgap), pref, $lgap, default, $nlgap, default, $lgap, 72dlu:grow, $lgap, default, $lgap, $rgap"));

            //---- lblMeasure ----
            lblMeasure.setText("text");
            lblMeasure.setFont(new Font("Arial", Font.PLAIN, 10));
            lblMeasure.setHorizontalAlignment(SwingConstants.TRAILING);
            panelMain.add(lblMeasure, CC.xy(5, 1));

            //---- txtQMS ----
            txtQMS.setFont(new Font("Arial", Font.BOLD, 14));
            panelMain.add(txtQMS, CC.xywh(3, 3, 3, 1, CC.DEFAULT, CC.FILL));

            //---- lblLocation ----
            lblLocation.setText("text");
            lblLocation.setFont(new Font("Arial", Font.PLAIN, 10));
            lblLocation.setHorizontalAlignment(SwingConstants.TRAILING);
            panelMain.add(lblLocation, CC.xy(5, 5));
            panelMain.add(cmbLocation, CC.xy(5, 7));

            //======== tabWdh ========
            {

                //======== pnlDaily ========
                {
                    pnlDaily.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlDaily.setLayout(new FormLayout(
                            "2*(default), $rgap, $lcgap, 40dlu, $rgap, default",
                            "default, $lgap, pref, $lgap, default"));

                    //---- lblEveryDay ----
                    lblEveryDay.setText("alle");
                    lblEveryDay.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlDaily.add(lblEveryDay, CC.xy(2, 3));

                    //---- spinTaeglich ----
                    spinTaeglich.setFont(new Font("Arial", Font.PLAIN, 14));
                    spinTaeglich.setModel(new SpinnerNumberModel(1, null, null, 1));
                    pnlDaily.add(spinTaeglich, CC.xy(5, 3));

                    //---- lblDays ----
                    lblDays.setText("Tage");
                    lblDays.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlDaily.add(lblDays, CC.xy(7, 3));

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
                            "$ugap, $lgap, default, $lgap, fill:53dlu:grow, $nlgap, default:grow, $lgap, $rgap"));

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

                        //---- lblEveryWeek ----
                        lblEveryWeek.setText("alle");
                        lblEveryWeek.setFont(new Font("Arial", Font.PLAIN, 14));
                        panel3.add(lblEveryWeek, CC.xy(1, 1));
                        panel3.add(spinWoche, CC.xy(3, 1));

                        //---- lblWeeks ----
                        lblWeeks.setText("Wochen am");
                        lblWeeks.setFont(new Font("Arial", Font.PLAIN, 14));
                        panel3.add(lblWeeks, CC.xy(5, 1));
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

                    //---- lblEveryMonth ----
                    lblEveryMonth.setText("jeden");
                    lblEveryMonth.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblEveryMonth.setHorizontalAlignment(SwingConstants.TRAILING);
                    pnlMonthly.add(lblEveryMonth, CC.xy(3, 3));

                    //---- spinMonat ----
                    spinMonat.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlMonthly.add(spinMonat, CC.xy(5, 3));

                    //---- lblMonth ----
                    lblMonth.setText("Monat");
                    lblMonth.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlMonthly.add(lblMonth, CC.xy(7, 3));

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

                    //---- llblOnDayOfMonth ----
                    llblOnDayOfMonth.setText("jeweils am");
                    llblOnDayOfMonth.setFont(new Font("Arial", Font.PLAIN, 14));
                    llblOnDayOfMonth.setHorizontalAlignment(SwingConstants.TRAILING);
                    pnlMonthly.add(llblOnDayOfMonth, CC.xy(3, 7));

                    //---- spinDayInMonth ----
                    spinDayInMonth.setFont(new Font("Arial", Font.PLAIN, 14));
                    spinDayInMonth.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            spinMonatTagStateChanged(e);
                        }
                    });
                    pnlMonthly.add(spinDayInMonth, CC.xy(5, 7));

                    //---- cmbTag ----
                    cmbTag.setModel(new DefaultComboBoxModel<>(new String[]{
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
                    cmbTag.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbTagItemStateChanged(e);
                        }
                    });
                    pnlMonthly.add(cmbTag, CC.xywh(7, 7, 3, 1));
                }
                tabWdh.addTab("Monatlich", pnlMonthly);

                //======== pnlYearly ========
                {
                    pnlYearly.setLayout(new FormLayout(
                            "30dlu, $rgap, 26dlu, $rgap, pref, $ugap, default",
                            "default, 15dlu, default"));

                    //---- lblEveryYear ----
                    lblEveryYear.setText("alle");
                    lblEveryYear.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblEveryYear.setHorizontalAlignment(SwingConstants.TRAILING);
                    pnlYearly.add(lblEveryYear, CC.xy(1, 1));

                    //---- spinYearly ----
                    spinYearly.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlYearly.add(spinYearly, CC.xy(3, 1));

                    //---- lblYear ----
                    lblYear.setText("Jahre");
                    lblYear.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlYearly.add(lblYear, CC.xy(5, 1));

                    //---- btnEveryYear ----
                    btnEveryYear.setText("jedes Jahr");
                    btnEveryYear.setFont(new Font("Arial", Font.PLAIN, 14));
                    btnEveryYear.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnEveryYearActionPerformed(e);
                        }
                    });
                    pnlYearly.add(btnEveryYear, CC.xy(7, 1));

                    //---- lblOnDay ----
                    lblOnDay.setText("alle");
                    lblOnDay.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblOnDay.setHorizontalAlignment(SwingConstants.TRAILING);
                    pnlYearly.add(lblOnDay, CC.xy(1, 3));

                    //---- spinDayInMonthInYear ----
                    spinDayInMonthInYear.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlYearly.add(spinDayInMonthInYear, CC.xy(3, 3));
                    pnlYearly.add(cmbMonth, CC.xywh(5, 3, 3, 1));
                }
                tabWdh.addTab("text", pnlYearly);
            }
            panelMain.add(tabWdh, CC.xywh(3, 11, 3, 1, CC.FILL, CC.FILL));

            //---- lblLDate ----
            lblLDate.setText("text");
            lblLDate.setFont(new Font("Arial", Font.PLAIN, 10));
            lblLDate.setHorizontalAlignment(SwingConstants.TRAILING);
            panelMain.add(lblLDate, CC.xy(5, 13));
            panelMain.add(jdcStartingOn, CC.xywh(3, 15, 3, 1));

            //======== jScrollPane1 ========
            {

                //---- txtBemerkung ----
                txtBemerkung.setColumns(20);
                txtBemerkung.setRows(5);
                txtBemerkung.setFont(new Font("Arial", Font.PLAIN, 14));
                txtBemerkung.setLineWrap(true);
                txtBemerkung.setWrapStyleWord(true);
                jScrollPane1.setViewportView(txtBemerkung);
            }
            panelMain.add(jScrollPane1, CC.xywh(3, 17, 3, 1, CC.DEFAULT, CC.FILL));

            //---- btnSave ----
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });
            panelMain.add(btnSave, CC.xy(5, 19, CC.RIGHT, CC.DEFAULT));

            //---- lblDueDays ----
            lblDueDays.setText("text");
            lblDueDays.setFont(new Font("Arial", Font.PLAIN, 10));
            lblDueDays.setHorizontalAlignment(SwingConstants.TRAILING);
            panelMain.add(lblDueDays, CC.xy(3, 5));

            //---- txtDueDays ----
            txtDueDays.setFont(new Font("Arial", Font.PLAIN, 14));
            panelMain.add(txtDueDays, CC.xy(3, 7, CC.DEFAULT, CC.FILL));
        }
        add(panelMain, BorderLayout.CENTER);

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(cbMon);
        buttonGroup1.add(cbDie);
        buttonGroup1.add(cbMit);
        buttonGroup1.add(cbDon);
        buttonGroup1.add(cbFre);
        buttonGroup1.add(cbSam);
        buttonGroup1.add(cbSon);
    }// </editor-fold>//GEN-END:initComponents


//    @Override
//    public void cleanup() {
//        SYSTools.unregisterListeners(this);
////        jdcLDatum.removePropertyChangeListener(jdcpcl);
//        jdcLDatum.cleanup();
//    }

    private void cbSonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbSonActionPerformed
        qmssched.setWeekday(DateTimeConstants.SUNDAY);
    }//GEN-LAST:event_cbSonActionPerformed

    private void cbSamActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbSamActionPerformed
        qmssched.setWeekday(DateTimeConstants.SATURDAY);
    }//GEN-LAST:event_cbSamActionPerformed

    private void cbFreActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbFreActionPerformed
        qmssched.setWeekday(DateTimeConstants.FRIDAY);
    }//GEN-LAST:event_cbFreActionPerformed

    private void cbDonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbDonActionPerformed
        qmssched.setWeekday(DateTimeConstants.THURSDAY);
    }//GEN-LAST:event_cbDonActionPerformed

    private void cbMitActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbMitActionPerformed
        qmssched.setWeekday(DateTimeConstants.WEDNESDAY);
    }//GEN-LAST:event_cbMitActionPerformed

    private void cbDieActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbDieActionPerformed
        qmssched.setWeekday(DateTimeConstants.TUESDAY);
    }//GEN-LAST:event_cbDieActionPerformed

    private void cbMonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbMonActionPerformed
        qmssched.setWeekday(DateTimeConstants.MONDAY);
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

    public boolean isSaveOK() {
        return jdcStartingOn.getDate() != null && !SYSTools.tidy(txtQMS.getText()).isEmpty();
    }

    public void save() throws NumberFormatException {

        qmssched.setDaily(tabWdh.getSelectedIndex() == TAB_DAILY ? Byte.parseByte(spinTaeglich.getValue().toString()) : 0);
        qmssched.setWeekly(tabWdh.getSelectedIndex() == TAB_WEEKLY ? Byte.parseByte(spinWoche.getValue().toString()) : 0);
        qmssched.setMonthly(tabWdh.getSelectedIndex() == TAB_MONTHLY ? Byte.parseByte(spinMonat.getValue().toString()) : 0);
        qmssched.setYearly(tabWdh.getSelectedIndex() == TAB_YEARLY ? Byte.parseByte(spinYearly.getValue().toString()) : 0);
        qmssched.setStartingOn(jdcStartingOn.getDate());

        if (tabWdh.getSelectedIndex() == TAB_DAILY) {
            qmssched.setWeekday(0);
        }

        if (tabWdh.getSelectedIndex() == TAB_MONTHLY) {
            qmssched.setDayinmonth(Integer.parseInt(spinDayInMonth.getValue().toString()));
            qmssched.setWeekday(cmbTag.getSelectedIndex());
        }

        if (tabWdh.getSelectedIndex() == TAB_YEARLY) {
            qmssched.setDayinmonth(Integer.parseInt(spinDayInMonthInYear.getValue().toString()));
            qmssched.setMonthinyear(cmbMonth.getSelectedIndex() + 1);
        }

        qmssched.setMeasure(SYSTools.tidy(txtQMS.getText()));
        qmssched.setText(SYSTools.tidy(txtBemerkung.getText()));


        qmssched.setDuedays(Integer.parseInt(txtDueDays.getText()));

        if (cmbLocation.getSelectedItem() == null) {
            qmssched.setHome(null);
            qmssched.setStation(null);
        } else if (cmbLocation.getSelectedItem() instanceof TreePath) {
            TreePath treePath = (TreePath) cmbLocation.getSelectedItem();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            if (node.getUserObject() instanceof Station) {
                qmssched.setStation((Station) node.getUserObject());
                qmssched.setHome(null);
            } else if (node.getUserObject() instanceof Homes) {
                qmssched.setHome((Homes) node.getUserObject());
                qmssched.setStation(null);
            } else {
                qmssched.setHome(null);
                qmssched.setStation(null);
            }
        } else {
            qmssched.setHome(null);
            qmssched.setStation(null);
        }

        qmssched.getQmsList().clear();
        QmsTools.generate(qmssched, 2);

    }

//    private void txtFocusGained(FocusEvent evt) {//GEN-FIRST:event_txtFocusGained
//        SYSTools.markAllTxt((JTextField) evt.getSource());
//    }//GEN-LAST:event_txtFocusGained
//
//    private void txtMaxTimesCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtMaxTimesCaretUpdate
//        if (ignoreEvent) {
//            return;
//        }
//        try {
//            Integer.parseInt(((JTextField) evt.getSource()).getText());
//            btnSave.setEnabled(true);
//        } catch (NumberFormatException nfe) {
//            btnSave.setEnabled(false);
//        }
//    }//GEN-LAST:event_txtMaxTimesCaretUpdate
//

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel panelMain;
    private JLabel lblMeasure;
    private JTextField txtQMS;
    private JLabel lblLocation;
    private TreeComboBox cmbLocation;
    private JideTabbedPane tabWdh;
    private JPanel pnlDaily;
    private JLabel lblEveryDay;
    private JSpinner spinTaeglich;
    private JLabel lblDays;
    private JButton btnJedenTag;
    private JPanel pnlWeekly;
    private JPanel panel3;
    private JButton btnJedeWoche;
    private JLabel lblEveryWeek;
    private JSpinner spinWoche;
    private JLabel lblWeeks;
    private JideLabel lblUhrzeit2;
    private JideLabel lblUhrzeit3;
    private JideLabel lblUhrzeit4;
    private JideLabel lblUhrzeit5;
    private JideLabel lblUhrzeit6;
    private JideLabel lblUhrzeit7;
    private JideLabel lblUhrzeit8;
    private JRadioButton cbMon;
    private JRadioButton cbDie;
    private JRadioButton cbMit;
    private JRadioButton cbDon;
    private JRadioButton cbFre;
    private JRadioButton cbSam;
    private JRadioButton cbSon;
    private JPanel pnlMonthly;
    private JLabel lblEveryMonth;
    private JSpinner spinMonat;
    private JLabel lblMonth;
    private JButton btnJedenMonat;
    private JLabel llblOnDayOfMonth;
    private JSpinner spinDayInMonth;
    private JComboBox<String> cmbTag;
    private JPanel pnlYearly;
    private JLabel lblEveryYear;
    private JSpinner spinYearly;
    private JLabel lblYear;
    private JButton btnEveryYear;
    private JLabel lblOnDay;
    private JSpinner spinDayInMonthInYear;
    private JComboBox cmbMonth;
    private JLabel lblLDate;
    private JDateChooser jdcStartingOn;
    private JScrollPane jScrollPane1;
    private JTextArea txtBemerkung;
    private JButton btnSave;
    private JLabel lblDueDays;
    private JTextField txtDueDays;
    // End of variables declaration//GEN-END:variables
}
