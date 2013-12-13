/*
 * Created by JFormDesigner on Sat Dec 07 12:14:43 CET 2013
 */

package op.users;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.roster.RosterXML;
import entity.roster.UserContract;
import op.OPDE;
import op.tools.*;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Torsten Löhr
 */
public class PnlContractsEditor extends PopupPanel {

    private UserContract contract;
    private boolean editable, saveOK = false;

    private int btnAddPeriodClickCount = -1;

    public PnlContractsEditor(UserContract contract, boolean editable) {
        this.contract = contract;
        this.editable = editable;

        initComponents();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                validate();
                repaint();
            }
        });

        initPanel();

    }


    public FocusTraversalPolicy getPolicy() {

        return GUITools.createTraversalPolicy(new ArrayList<Component>(Arrays.asList(new Component[]{txtFrom, txtTo, txtWPH, txtVDPY, txtHolidayPercent, cmbSection, txtWDPW, txtTHPM, txtTHPW, txtNightFrom, txtNightTo, txtNightPercent})));

    }

    void initPanel() {

        cmbSection.setModel(new DefaultComboBoxModel(RosterXML.sections));
        cmbSection.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                String text = OPDE.lang.getString("opde.roster.section." + value);
                return new DefaultListCellRenderer().getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            }
        });

        txtFrom.setText(contract.getDefaults().getFrom().toString("dd.MM.yyyy"));
        if (contract.getDefaults().getTo().equals(SYSConst.LD_UNTIL_FURTHER_NOTICE)) {
            txtTo.setText(OPDE.lang.getString("opde.roster.unlimited"));
        } else {
            txtTo.setText(contract.getDefaults().getTo().toString("dd.MM.yyyy"));
        }

        txtWPH.setText(SYSTools.catchNull(contract.getDefaults().getWagePerHour()));
        txtWDPW.setText(SYSTools.catchNull(contract.getDefaults().getWorkingDaysPerWeek()));
        txtTHPM.setText(SYSTools.catchNull(contract.getDefaults().getTargetHoursPerMonth()));
        txtTHPW.setText(SYSTools.catchNull(contract.getDefaults().getTargetHoursPerWeek()));
        lblTHPDx.setText(SYSTools.catchNull(contract.getDefaults().getTargetHoursPerDay(), "?"));

        txtHolidayPercent.setText(SYSTools.catchNull(contract.getDefaults().getHolidayPremiumPercentage()));
        txtNightPercent.setText(SYSTools.catchNull(contract.getDefaults().getNightPremiumPercentage()));
        if (contract.getDefaults().getNight() != null) {
            txtNightFrom.setText(contract.getDefaults().getNight().getFirst().toString("HH:mm"));
            txtNightTo.setText(contract.getDefaults().getNight().getSecond().toString("HH:mm"));
        }

        lblFrom.setText(OPDE.lang.getString("misc.msg.from"));
        lblTo.setText(OPDE.lang.getString("misc.msg.to"));
        lblWPH.setText(OPDE.lang.getString("opde.roster.wage.per.hour"));
        lblWDPW.setText(OPDE.lang.getString("opde.roster.workingdays.per.week"));
        lblTHPM.setText(OPDE.lang.getString("opde.roster.targethours.per.month"));
        lblTHPW.setText(OPDE.lang.getString("opde.roster.targethours.per.week"));
        lblTHPD.setText(OPDE.lang.getString("opde.roster.targethours.per.day"));
        lblVDPY.setText(OPDE.lang.getString("opde.roster.holiday.per.year"));
        lblHolidayPercent.setText(OPDE.lang.getString("opde.roster.holiday.premium.percent"));
        lblNightFrom.setText(OPDE.lang.getString("opde.roster.night.from"));
        lblNightTo.setText(OPDE.lang.getString("opde.roster.night.to"));
        lblNightPercent.setText(OPDE.lang.getString("opde.roster.night.premium.percent"));


        LocalDate unlimitedSince = null;
        for (Pair<LocalDate, LocalDate> probation : contract.getProbations()) {
            JPanel pnl = new JPanel();
            pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));
            pnl.add(new JLabel(String.format("%s: %s -> %s", "Probezeit", probation.getFirst().toString("dd.MM.yyyy"), probation.getSecond().toString("dd.MM.yyyy"))));
            unlimitedSince = probation.getSecond();
            pnl.add(new JButton(SYSConst.icon22delete));
            pnlPeriods.add(pnl);
        }
        for (Pair<LocalDate, LocalDate> extension : contract.getExtensions()) {
            JPanel pnl = new JPanel();
            pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));
            pnl.add(new JLabel(String.format("%s: %s -> %s", "Verlängerung", extension.getFirst().toString("dd.MM.yyyy"), extension.getSecond().toString("dd.MM.yyyy"))));
            unlimitedSince = extension.getSecond();
            pnl.add(new JButton(SYSConst.icon22delete));
            pnlPeriods.add(pnl);
        }
        if (contract.getDefaults().getTo().equals(SYSConst.LD_UNTIL_FURTHER_NOTICE)) {
            pnlPeriods.add(new JLabel("unbefristet" + (unlimitedSince != null ? " seit: " + unlimitedSince.toString("dd.MM.yyyy") : "")));
        }

        txtFrom.setEditable(editable);
        txtTo.setEditable(editable);
        btnTo.setEnabled(editable);
        txtWPH.setEditable(editable);
        txtWDPW.setEditable(editable);
        txtTHPM.setEditable(editable);
        txtTHPW.setEditable(editable);
        txtHolidayPercent.setEditable(editable);
        txtNightFrom.setEditable(editable);
        txtNightTo.setEditable(editable);
        txtNightPercent.setEditable(editable);
        txtVDPY.setEditable(editable);
        cmbSection.setEnabled(editable);

        checkAll();
    }

    private boolean checkAll() {

        Color red = SYSConst.pearl;

        boolean dateok = contract.getDefaults().getFrom().isBefore(contract.getDefaults().getTo());
        txtNightFrom.setBackground(dateok ? Color.WHITE : red);
        txtNightTo.setBackground(dateok ? Color.WHITE : red);

        boolean wph = contract.getDefaults().getWagePerHour() != null;
        txtWPH.setBackground(wph ? Color.WHITE : red);

        boolean wdpw = contract.getDefaults().getWorkingDaysPerWeek() != null;
        txtWDPW.setBackground(wdpw ? Color.WHITE : red);

        boolean vdpy = contract.getDefaults().getVacationDaysPerYear() != null;
        txtVDPY.setBackground(vdpy ? Color.WHITE : red);

        boolean thpm = contract.getDefaults().getTargetHoursPerMonth() != null;
        txtTHPM.setBackground(thpm ? Color.WHITE : red);
        txtTHPW.setBackground(thpm ? Color.WHITE : red);

        boolean hpp = contract.getDefaults().getHolidayPremiumPercentage() != null;
        txtHolidayPercent.setBackground(hpp ? Color.WHITE : red);

        boolean nightok = contract.getDefaults().getNight() != null;
        txtNightFrom.setBackground(nightok ? Color.WHITE : red);
        txtNightTo.setBackground(nightok ? Color.WHITE : red);

        boolean npp = contract.getDefaults().getNightPremiumPercentage() != null;
        txtNightPercent.setBackground(npp ? Color.WHITE : red);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                validate();
                repaint();
            }
        });

        saveOK = editable && dateok && wph && wdpw && thpm && hpp && nightok && npp && vdpy;

        if (dateok && wph && wdpw && thpm && hpp && nightok && npp && vdpy) {
            OPDE.debug(contract.toXML());
        }

        return saveOK;
    }


    private void txtFromFocusLost(FocusEvent e) {
        try {
            LocalDate startDate = new LocalDate(SYSCalendar.parseDate(txtFrom.getText()));
            contract.getDefaults().setFrom(SYSCalendar.bom(startDate));
        } catch (NumberFormatException e1) {
            contract.getDefaults().setFrom(null);
        } finally {
            if (checkAll()) {
                txtFrom.setText(contract.getDefaults().getFrom().toString("dd.MM.yyyy"));
            }
        }

    }

    private void txtToFocusLost(FocusEvent e) {
        try {
            if (SYSTools.catchNull(txtTo.getText()).equalsIgnoreCase(OPDE.lang.getString("opde.roster.unlimited"))) {
                contract.getDefaults().setTo(SYSConst.LD_UNTIL_FURTHER_NOTICE);
            } else {
                LocalDate endDate = new LocalDate(SYSCalendar.parseDate(txtTo.getText()));
                contract.getDefaults().setTo(SYSCalendar.eom(endDate));
            }
        } catch (NumberFormatException e1) {
            contract.getDefaults().setTo(null);
        } finally {
            if (checkAll()) {
                if (contract.getDefaults().getTo().equals(SYSConst.LD_UNTIL_FURTHER_NOTICE)) {
                    txtTo.setText(OPDE.lang.getString("opde.roster.unlimited"));
                } else {
                    txtTo.setText(contract.getDefaults().getTo().toString("dd.MM.yyyy"));
                }
            }
        }
    }

    private void txtWPHFocusLost(FocusEvent e) {
        try {
            contract.getDefaults().setWagePerHour(BigDecimal.valueOf(Double.parseDouble(txtWPH.getText().replaceAll(",", "\\."))));
            if (contract.getDefaults().getWagePerHour().compareTo(BigDecimal.ZERO) <= 0) {
                contract.getDefaults().setWagePerHour(null);
            } else {
                txtWPH.setText(contract.getDefaults().getWagePerHour().setScale(2, RoundingMode.HALF_UP).toString());
            }
        } catch (NumberFormatException e1) {
            contract.getDefaults().setWagePerHour(null);
        } finally {
            checkAll();
        }
    }

    private void txtWDPWFocusLost(FocusEvent e) {
        try {
            contract.getDefaults().setWorkingDaysPerWeek(BigDecimal.valueOf(Double.parseDouble(txtWDPW.getText().replaceAll(",", "\\."))));
            if (contract.getDefaults().getWorkingDaysPerWeek().compareTo(BigDecimal.ZERO) <= 0) {
                contract.getDefaults().setWorkingDaysPerWeek(null);
            }
        } catch (NumberFormatException e1) {
            contract.getDefaults().setWorkingDaysPerWeek(null);
        } finally {
            if (checkAll()) {
                txtWDPW.setText(SYSTools.catchNull(contract.getDefaults().getWorkingDaysPerWeek()));
                if (contract.getDefaults().getTargetHoursPerDay() != null) {
                    lblTHPDx.setText(contract.getDefaults().getTargetHoursPerDay().setScale(2, RoundingMode.HALF_UP).toString());
                }
            }
        }
    }

    private void txtTHPWFocusLost(FocusEvent e) {
        try {
            contract.getDefaults().setTargetHoursPerWeek(BigDecimal.valueOf(Double.parseDouble(txtTHPW.getText().replaceAll(",", "\\."))));
            if (contract.getDefaults().getTargetHoursPerWeek().compareTo(BigDecimal.ZERO) <= 0) {
                contract.getDefaults().setTargetHoursPerWeek(null);
            } else {
                txtTHPM.setText(contract.getDefaults().getTargetHoursPerMonth().setScale(2, RoundingMode.HALF_UP).toString());
                txtTHPW.setText(contract.getDefaults().getTargetHoursPerWeek().setScale(2, RoundingMode.HALF_UP).toString());
                if (contract.getDefaults().getTargetHoursPerDay() != null) {
                    lblTHPDx.setText(contract.getDefaults().getTargetHoursPerDay().setScale(2, RoundingMode.HALF_UP).toString());
                }
            }
        } catch (NumberFormatException e1) {
            contract.getDefaults().setTargetHoursPerWeek(null);
        } finally {

            checkAll();
        }
    }

    private void txtTHPMFocusLost(FocusEvent e) {
        try {
            contract.getDefaults().setTargetHoursPerMonth(BigDecimal.valueOf(Double.parseDouble(txtTHPM.getText().replaceAll(",", "\\."))));
            if (contract.getDefaults().getTargetHoursPerMonth().compareTo(BigDecimal.ZERO) <= 0) {
                contract.getDefaults().setTargetHoursPerMonth(null);
            } else {
                txtTHPM.setText(contract.getDefaults().getTargetHoursPerMonth().setScale(2, RoundingMode.HALF_UP).toString());
                txtTHPW.setText(contract.getDefaults().getTargetHoursPerWeek().setScale(2, RoundingMode.HALF_UP).toString());
                if (contract.getDefaults().getTargetHoursPerDay() != null) {
                    lblTHPDx.setText(contract.getDefaults().getTargetHoursPerDay().setScale(2, RoundingMode.HALF_UP).toString());
                }
            }
        } catch (NumberFormatException e1) {
            contract.getDefaults().setTargetHoursPerMonth(null);
        } finally {
            checkAll();
        }
    }


    private void txtHolidayPercentFocusLost(FocusEvent e) {
        try {
            contract.getDefaults().setHolidayPremiumPercentage(BigDecimal.valueOf(Double.parseDouble(txtHolidayPercent.getText().replaceAll(",", "\\."))));
            if (contract.getDefaults().getHolidayPremiumPercentage().compareTo(BigDecimal.ZERO) <= 0 || contract.getDefaults().getHolidayPremiumPercentage().compareTo(new BigDecimal(100)) > 0) {
                contract.getDefaults().setHolidayPremiumPercentage(null);
            } else {
                txtHolidayPercent.setText(contract.getDefaults().getHolidayPremiumPercentage().toString());
            }
        } catch (NumberFormatException e1) {
            contract.getDefaults().setHolidayPremiumPercentage(null);
        } finally {
            checkAll();
        }
    }

    private void txtNightFromFocusLost(FocusEvent e) {
        try {
            if (contract.getDefaults().getNight() == null) {
                contract.getDefaults().setNight(new Pair<LocalTime, LocalTime>(new LocalTime(), new LocalTime()));
            }
            contract.getDefaults().getNight().setFirst(SYSCalendar.parseLocalTime(txtNightFrom.getText()));
        } catch (NumberFormatException e1) {
            contract.getDefaults().setNight(null);
        } finally {
            if (checkAll()) {
                txtNightFrom.setText(contract.getDefaults().getNight().getFirst().toString("HH:mm"));
                txtNightTo.setText(contract.getDefaults().getNight().getSecond().toString("HH:mm"));
            }
        }
    }

    private void txtNightToFocusLost(FocusEvent e) {
        try {
            if (contract.getDefaults().getNight() == null) {
                contract.getDefaults().setNight(new Pair<LocalTime, LocalTime>(new LocalTime(), new LocalTime()));
            }
            contract.getDefaults().getNight().setSecond(SYSCalendar.parseLocalTime(txtNightTo.getText()));
        } catch (NumberFormatException e1) {
            contract.getDefaults().setNight(null);
        } finally {
            if (checkAll()) {
                txtNightFrom.setText(contract.getDefaults().getNight().getFirst().toString("HH:mm"));
                txtNightTo.setText(contract.getDefaults().getNight().getSecond().toString("HH:mm"));
            }
        }
    }

    private void txtNightPercentFocusLost(FocusEvent e) {
        try {
            contract.getDefaults().setNightPremiumPercentage(BigDecimal.valueOf(Double.parseDouble(txtNightPercent.getText().replaceAll(",", "\\."))));
            if (contract.getDefaults().getNightPremiumPercentage().compareTo(BigDecimal.ZERO) <= 0 || contract.getDefaults().getNightPremiumPercentage().compareTo(new BigDecimal(100)) > 0) {
                contract.getDefaults().setNightPremiumPercentage(null);
            } else {
                txtNightPercent.setText(contract.getDefaults().getNightPremiumPercentage().toString());
            }
        } catch (NumberFormatException e1) {
            contract.getDefaults().setNightPremiumPercentage(null);
        } finally {
            checkAll();
        }
    }

    private void btnToActionPerformed(ActionEvent e) {
        if (contract.getDefaults().getFrom() == null)
            return;

        btnAddPeriodClickCount++;
        if (btnAddPeriodClickCount % 5 == 0) {
            contract.getDefaults().setTo(contract.getDefaults().getFrom().plusMonths(3).minusDays(1));
        } else if (btnAddPeriodClickCount % 5 == 1) {
            contract.getDefaults().setTo(contract.getDefaults().getFrom().plusMonths(6).minusDays(1));
        } else if (btnAddPeriodClickCount % 5 == 2) {
            contract.getDefaults().setTo(contract.getDefaults().getFrom().plusYears(1).minusDays(1));
        } else if (btnAddPeriodClickCount % 5 == 3) {
            contract.getDefaults().setTo(contract.getDefaults().getFrom().plusYears(2).minusDays(1));
        } else if (btnAddPeriodClickCount % 5 == 4) {
            contract.getDefaults().setTo(SYSConst.LD_UNTIL_FURTHER_NOTICE);
        }


        if (contract.getDefaults().getTo().equals(SYSConst.LD_UNTIL_FURTHER_NOTICE)) {
            txtTo.setText(OPDE.lang.getString("opde.roster.unlimited"));
        } else {
            txtTo.setText(contract.getDefaults().getTo().toString("dd.MM.yyyy"));
        }

        checkAll();

    }

    private void txtFocusGained(FocusEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                validate();
                repaint();
            }
        });
    }

    private void txtWDPWPropertyChange(PropertyChangeEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                validate();
                repaint();
            }
        });
    }

    private void txtVDPYFocusLost(FocusEvent e) {
        try {
            contract.getDefaults().setVacationDaysPerYear(BigDecimal.valueOf(Double.parseDouble(txtVDPY.getText().replaceAll(",", "\\."))));
            if (contract.getDefaults().getVacationDaysPerYear().compareTo(BigDecimal.ZERO) < 0) {
                contract.getDefaults().setVacationDaysPerYear(null);
            }
        } catch (NumberFormatException e1) {
            contract.getDefaults().setVacationDaysPerYear(null);
        } finally {
            checkAll();
        }
    }

    private void cmbSectionItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            contract.getDefaults().setSection(cmbSection.getSelectedItem().toString());
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblFrom = new JLabel();
        lblTo = new JLabel();
        txtFrom = new JTextField();
        label1 = new JLabel();
        panel1 = new JPanel();
        txtTo = new JTextField();
        btnTo = new JButton();
        lblWPH = new JLabel();
        lblVDPY = new JLabel();
        lblHolidayPercent = new JLabel();
        lblSection = new JLabel();
        txtWPH = new JTextField();
        txtVDPY = new JTextField();
        txtHolidayPercent = new JTextField();
        cmbSection = new JComboBox();
        lblWDPW = new JLabel();
        lblTHPM = new JLabel();
        lblTHPW = new JLabel();
        lblTHPD = new JLabel();
        txtWDPW = new JTextField();
        txtTHPM = new JTextField();
        txtTHPW = new JTextField();
        lblTHPDx = new JLabel();
        lblNightFrom = new JLabel();
        lblNightTo = new JLabel();
        lblNightPercent = new JLabel();
        txtNightFrom = new JTextField();
        label2 = new JLabel();
        txtNightTo = new JTextField();
        txtNightPercent = new JTextField();
        pnlPeriods = new JPanel();

        //======== this ========
        setLayout(new FormLayout(
            "default, 4*($lcgap, default:grow), $lcgap, default",
            "9*(default, $lgap), fill:default:grow, 2*($lgap, default)"));

        //---- lblFrom ----
        lblFrom.setText("text");
        lblFrom.setFont(new Font("Arial", Font.BOLD, 9));
        lblFrom.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblFrom, CC.xy(3, 3));

        //---- lblTo ----
        lblTo.setText("text");
        lblTo.setFont(new Font("Arial", Font.BOLD, 9));
        lblTo.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblTo, CC.xywh(7, 3, 3, 1));

        //---- txtFrom ----
        txtFrom.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtFocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtFromFocusLost(e);
            }
        });
        txtFrom.addPropertyChangeListener("text", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                txtWDPWPropertyChange(e);
            }
        });
        add(txtFrom, CC.xy(3, 5));

        //---- label1 ----
        label1.setText(null);
        label1.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/2rightarrow.png")));
        add(label1, CC.xy(5, 5, CC.CENTER, CC.DEFAULT));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- txtTo ----
            txtTo.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtFocusGained(e);
                }
                @Override
                public void focusLost(FocusEvent e) {
                    txtToFocusLost(e);
                }
            });
            txtTo.addPropertyChangeListener("text", new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                    txtWDPWPropertyChange(e);
                }
            });
            panel1.add(txtTo);

            //---- btnTo ----
            btnTo.setText(null);
            btnTo.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/3rightarrow.png")));
            btnTo.setBorderPainted(false);
            btnTo.setBorder(null);
            btnTo.setContentAreaFilled(false);
            btnTo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnTo.setFocusable(false);
            btnTo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnToActionPerformed(e);
                }
            });
            panel1.add(btnTo);
        }
        add(panel1, CC.xywh(7, 5, 3, 1));

        //---- lblWPH ----
        lblWPH.setText("text");
        lblWPH.setFont(new Font("Arial", Font.BOLD, 9));
        lblWPH.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblWPH, CC.xy(3, 7));

        //---- lblVDPY ----
        lblVDPY.setText("text");
        lblVDPY.setFont(new Font("Arial", Font.BOLD, 9));
        lblVDPY.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblVDPY, CC.xy(5, 7));

        //---- lblHolidayPercent ----
        lblHolidayPercent.setText("text");
        lblHolidayPercent.setFont(new Font("Arial", Font.BOLD, 9));
        lblHolidayPercent.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblHolidayPercent, CC.xy(7, 7));

        //---- lblSection ----
        lblSection.setText("text");
        lblSection.setFont(new Font("Arial", Font.BOLD, 9));
        lblSection.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblSection, CC.xy(9, 7));

        //---- txtWPH ----
        txtWPH.setHorizontalAlignment(SwingConstants.TRAILING);
        txtWPH.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtFocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtWPHFocusLost(e);
            }
        });
        txtWPH.addPropertyChangeListener("text", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                txtWDPWPropertyChange(e);
            }
        });
        add(txtWPH, CC.xy(3, 9));

        //---- txtVDPY ----
        txtVDPY.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtVDPYFocusLost(e);
            }
        });
        add(txtVDPY, CC.xy(5, 9));

        //---- txtHolidayPercent ----
        txtHolidayPercent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtFocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtHolidayPercentFocusLost(e);
            }
        });
        txtHolidayPercent.addPropertyChangeListener("text", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                txtWDPWPropertyChange(e);
            }
        });
        add(txtHolidayPercent, CC.xy(7, 9));

        //---- cmbSection ----
        cmbSection.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbSectionItemStateChanged(e);
            }
        });
        add(cmbSection, CC.xy(9, 9));

        //---- lblWDPW ----
        lblWDPW.setText("text");
        lblWDPW.setFont(new Font("Arial", Font.BOLD, 9));
        lblWDPW.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblWDPW, CC.xy(3, 11));

        //---- lblTHPM ----
        lblTHPM.setText("text");
        lblTHPM.setFont(new Font("Arial", Font.BOLD, 9));
        lblTHPM.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblTHPM, CC.xy(5, 11));

        //---- lblTHPW ----
        lblTHPW.setText("text");
        lblTHPW.setFont(new Font("Arial", Font.BOLD, 9));
        lblTHPW.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblTHPW, CC.xy(7, 11));

        //---- lblTHPD ----
        lblTHPD.setText("text");
        lblTHPD.setFont(new Font("Arial", Font.BOLD, 9));
        lblTHPD.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblTHPD, CC.xy(9, 11));

        //---- txtWDPW ----
        txtWDPW.setHorizontalAlignment(SwingConstants.TRAILING);
        txtWDPW.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtFocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtWDPWFocusLost(e);
            }
        });
        txtWDPW.addPropertyChangeListener("text", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                txtWDPWPropertyChange(e);
            }
        });
        add(txtWDPW, CC.xy(3, 13));

        //---- txtTHPM ----
        txtTHPM.setHorizontalAlignment(SwingConstants.TRAILING);
        txtTHPM.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtFocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtTHPMFocusLost(e);
            }
        });
        txtTHPM.addPropertyChangeListener("text", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                txtWDPWPropertyChange(e);
            }
        });
        add(txtTHPM, CC.xy(5, 13));

        //---- txtTHPW ----
        txtTHPW.setHorizontalAlignment(SwingConstants.TRAILING);
        txtTHPW.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtFocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtTHPWFocusLost(e);
            }
        });
        txtTHPW.addPropertyChangeListener("text", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                txtWDPWPropertyChange(e);
            }
        });
        add(txtTHPW, CC.xy(7, 13));

        //---- lblTHPDx ----
        lblTHPDx.setText("text");
        lblTHPDx.setHorizontalAlignment(SwingConstants.TRAILING);
        lblTHPDx.setBorder(LineBorder.createBlackLineBorder());
        add(lblTHPDx, CC.xy(9, 13));

        //---- lblNightFrom ----
        lblNightFrom.setText("text");
        lblNightFrom.setFont(new Font("Arial", Font.BOLD, 9));
        lblNightFrom.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblNightFrom, CC.xy(3, 15));

        //---- lblNightTo ----
        lblNightTo.setText("text");
        lblNightTo.setFont(new Font("Arial", Font.BOLD, 9));
        lblNightTo.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblNightTo, CC.xy(7, 15));

        //---- lblNightPercent ----
        lblNightPercent.setText("text");
        lblNightPercent.setFont(new Font("Arial", Font.BOLD, 9));
        lblNightPercent.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblNightPercent, CC.xy(9, 15));

        //---- txtNightFrom ----
        txtNightFrom.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtFocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtNightFromFocusLost(e);
            }
        });
        txtNightFrom.addPropertyChangeListener("text", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                txtWDPWPropertyChange(e);
            }
        });
        add(txtNightFrom, CC.xy(3, 17));

        //---- label2 ----
        label2.setText(null);
        label2.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/2rightarrow.png")));
        add(label2, CC.xy(5, 17, CC.CENTER, CC.DEFAULT));

        //---- txtNightTo ----
        txtNightTo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtFocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtNightToFocusLost(e);
            }
        });
        txtNightTo.addPropertyChangeListener("text", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                txtWDPWPropertyChange(e);
            }
        });
        add(txtNightTo, CC.xy(7, 17));

        //---- txtNightPercent ----
        txtNightPercent.setHorizontalAlignment(SwingConstants.TRAILING);
        txtNightPercent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtFocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtNightPercentFocusLost(e);
            }
        });
        txtNightPercent.addPropertyChangeListener("text", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                txtWDPWPropertyChange(e);
            }
        });
        add(txtNightPercent, CC.xy(9, 17));

        //======== pnlPeriods ========
        {
            pnlPeriods.setLayout(new BoxLayout(pnlPeriods, BoxLayout.PAGE_AXIS));
        }
        add(pnlPeriods, CC.xywh(3, 19, 7, 1, CC.DEFAULT, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    @Override
    public Object getResult() {
        return contract;
    }

    @Override
    public void setStartFocus() {
        txtFrom.requestFocus();
    }

    @Override
    public boolean isSaveOK() {
        return saveOK;
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblFrom;
    private JLabel lblTo;
    private JTextField txtFrom;
    private JLabel label1;
    private JPanel panel1;
    private JTextField txtTo;
    private JButton btnTo;
    private JLabel lblWPH;
    private JLabel lblVDPY;
    private JLabel lblHolidayPercent;
    private JLabel lblSection;
    private JTextField txtWPH;
    private JTextField txtVDPY;
    private JTextField txtHolidayPercent;
    private JComboBox cmbSection;
    private JLabel lblWDPW;
    private JLabel lblTHPM;
    private JLabel lblTHPW;
    private JLabel lblTHPD;
    private JTextField txtWDPW;
    private JTextField txtTHPM;
    private JTextField txtTHPW;
    private JLabel lblTHPDx;
    private JLabel lblNightFrom;
    private JLabel lblNightTo;
    private JLabel lblNightPercent;
    private JTextField txtNightFrom;
    private JLabel label2;
    private JTextField txtNightTo;
    private JTextField txtNightPercent;
    private JPanel pnlPeriods;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
