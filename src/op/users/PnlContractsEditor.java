/*
 * Created by JFormDesigner on Sat Dec 07 12:14:43 CET 2013
 */

package op.users;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.swing.DefaultOverlayable;
import entity.roster.UserContract;
import entity.system.Users;
import op.OPDE;
import op.tools.Pair;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Torsten Löhr
 */
public class PnlContractsEditor extends JPanel {

    private UserContract contract;
    //    private JTextField txtWDPW, txtTHPM, txtTHPW, txtTHPD
//    private Users user;
    private JLabel lblOvrFrom, lblOvrTo, lblOvrWPH, lblOvrWDPW, lblOvrTHPM, lblOvrTHPW, lblOvrTHPD, lblOvrHPP, lblOvrNightFrom, lblOvrNightTo, lblOvrNightPercent;
    private int btnAddPeriodClickCount = -1;

//    public PnlContractsEditor(Users user) {
//        this(new UserContract(new ContractsParameterSet()), user);
//        this.contract.getDefaults().setExam(UsersTools.isQualified(user)); // just as a suggestion for new contracts
//    }

    public PnlContractsEditor(UserContract contract) {
        this.contract = contract;

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


    void initPanel() {

        lblOvrFrom = new JLabel();
        lblOvrTo = new JLabel();
        lblOvrWPH = new JLabel();
        lblOvrWDPW = new JLabel();
        lblOvrTHPM = new JLabel();
        lblOvrTHPW = new JLabel();
        lblOvrTHPD = new JLabel();
        lblOvrHPP = new JLabel();
        lblOvrNightFrom = new JLabel();
        lblOvrNightTo = new JLabel();
        lblOvrNightPercent = new JLabel();

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
        txtTHPD.setText(SYSTools.catchNull(contract.getDefaults().getTargetHoursPerDay()));

        txtHolidayPercent.setText(SYSTools.catchNull(contract.getDefaults().getHolidayPremiumPercentage()));
        txtNightPercent.setText(SYSTools.catchNull(contract.getDefaults().getNightPremiumPercentage()));
        if (contract.getDefaults().getNight() != null) {
            txtNightFrom.setText(contract.getDefaults().getNight().getFirst().toString("HH:mm"));
            txtNightTo.setText(contract.getDefaults().getNight().getSecond().toString("HH:mm"));
        }

        JLabel lblFrom = new JLabel(OPDE.lang.getString("misc.msg.from") + " ");
        lblFrom.setFont(SYSConst.ARIAL9BOLD);
        lblFrom.setForeground(SYSConst.bluegrey.darker());
        overFrom.addOverlayComponent(lblFrom, SwingConstants.EAST);
        overFrom.addOverlayComponent(lblOvrFrom, SwingConstants.SOUTH_EAST);

        JLabel lblTo = new JLabel(OPDE.lang.getString("misc.msg.to") + " ");
        lblTo.setFont(SYSConst.ARIAL9BOLD);
        lblTo.setForeground(SYSConst.bluegrey.darker());
        overTo.addOverlayComponent(lblTo, SwingConstants.EAST);
        overTo.addOverlayComponent(lblOvrTo, SwingConstants.SOUTH_EAST);

        JLabel lblWage = new JLabel(OPDE.lang.getString("opde.roster.wage.per.hour") + " ");
        lblWage.setFont(SYSConst.ARIAL9BOLD);
        lblWage.setForeground(SYSConst.bluegrey.darker());
        overWPH.addOverlayComponent(lblWage, SwingConstants.EAST);
        overWPH.addOverlayComponent(lblOvrWPH, SwingConstants.SOUTH_EAST);

        JLabel lblWDPW = new JLabel(OPDE.lang.getString("opde.roster.workingdays.per.week") + " ");
        lblWDPW.setFont(SYSConst.ARIAL9BOLD);
        lblWDPW.setForeground(SYSConst.bluegrey.darker());
        overWDPW.addOverlayComponent(lblWDPW, SwingConstants.EAST);
        overWDPW.addOverlayComponent(lblOvrWDPW, SwingConstants.SOUTH_EAST);

        JLabel lblTHPM = new JLabel(OPDE.lang.getString("opde.roster.targethours.per.month") + " ");
        lblTHPM.setFont(SYSConst.ARIAL9BOLD);
        lblTHPM.setForeground(SYSConst.bluegrey.darker());
        overTHPM.addOverlayComponent(lblTHPM, SwingConstants.EAST);
        overTHPM.addOverlayComponent(lblOvrTHPM, SwingConstants.SOUTH_EAST);

        JLabel lblTHPW = new JLabel(OPDE.lang.getString("opde.roster.targethours.per.week") + " ");
        lblTHPW.setFont(SYSConst.ARIAL9BOLD);
        lblTHPW.setForeground(SYSConst.bluegrey.darker());
        overTHPW.addOverlayComponent(lblTHPW, SwingConstants.EAST);
        overTHPW.addOverlayComponent(lblOvrTHPW, SwingConstants.SOUTH_EAST);

        JLabel lblTHPD = new JLabel(OPDE.lang.getString("opde.roster.targethours.per.day") + " ");
        lblTHPD.setFont(SYSConst.ARIAL9BOLD);
        lblTHPD.setForeground(SYSConst.bluegrey.darker());
        overTHPD.addOverlayComponent(lblTHPD, SwingConstants.EAST);
        overTHPD.addOverlayComponent(lblOvrTHPD, SwingConstants.SOUTH_EAST);

        JLabel lblHPP = new JLabel(OPDE.lang.getString("opde.roster.holiday.premium.percent") + " ");
        lblHPP.setFont(SYSConst.ARIAL9BOLD);
        lblHPP.setForeground(SYSConst.bluegrey.darker());
        overHolidayPercent.addOverlayComponent(lblHPP, SwingConstants.EAST);
        overHolidayPercent.addOverlayComponent(lblOvrHPP, SwingConstants.SOUTH_EAST);

        JLabel lblNightFrom = new JLabel(OPDE.lang.getString("opde.roster.night.from") + " ");
        lblNightFrom.setFont(SYSConst.ARIAL9BOLD);
        lblNightFrom.setForeground(SYSConst.bluegrey.darker());
        overNightFrom.addOverlayComponent(lblNightFrom, SwingConstants.EAST);
        overNightFrom.addOverlayComponent(lblOvrNightFrom, SwingConstants.SOUTH_EAST);

        JLabel lblNightTo = new JLabel(OPDE.lang.getString("opde.roster.night.to") + " ");
        lblNightTo.setFont(SYSConst.ARIAL9BOLD);
        lblNightTo.setForeground(SYSConst.bluegrey.darker());
        overNightTo.addOverlayComponent(lblNightTo, SwingConstants.EAST);
        overNightTo.addOverlayComponent(lblOvrNightTo, SwingConstants.SOUTH_EAST);

        JLabel lblNightPP = new JLabel(OPDE.lang.getString("opde.roster.night.premium.percent") + " ");
        lblNightPP.setFont(SYSConst.ARIAL9BOLD);
        lblNightPP.setForeground(SYSConst.bluegrey.darker());
        overNightPercent.addOverlayComponent(lblNightPP, SwingConstants.EAST);
        overNightPercent.addOverlayComponent(lblOvrNightPercent, SwingConstants.SOUTH_EAST);

        checkAll();
    }

    private boolean checkAll() {

        boolean dateok = contract.getDefaults().getFrom().isBefore(contract.getDefaults().getTo());
        lblOvrFrom.setIcon(dateok ? SYSConst.icon16apply : SYSConst.icon16infored);
        lblOvrTo.setIcon(dateok ? SYSConst.icon16apply : SYSConst.icon16infored);

        boolean wph = contract.getDefaults().getWagePerHour() != null;
        lblOvrWPH.setIcon(wph ? SYSConst.icon16apply : SYSConst.icon16infored);

        boolean wdpw = contract.getDefaults().getWorkingDaysPerWeek() != null;
        lblOvrWDPW.setIcon(wdpw ? SYSConst.icon16apply : SYSConst.icon16infored);

        boolean thpm = contract.getDefaults().getTargetHoursPerMonth() != null;
        lblOvrTHPM.setIcon(thpm ? SYSConst.icon16apply : SYSConst.icon16infored);
        lblOvrTHPD.setIcon(thpm ? SYSConst.icon16apply : SYSConst.icon16infored);
        lblOvrTHPW.setIcon(thpm ? SYSConst.icon16apply : SYSConst.icon16infored);

        boolean hpp = contract.getDefaults().getHolidayPremiumPercentage() != null;
        lblOvrHPP.setIcon(hpp ? SYSConst.icon16apply : SYSConst.icon16infored);

        boolean nightok = contract.getDefaults().getNight() != null;
        lblOvrNightTo.setIcon(nightok ? SYSConst.icon16apply : SYSConst.icon16infored);
        lblOvrNightFrom.setIcon(nightok ? SYSConst.icon16apply : SYSConst.icon16infored);

        boolean npp = contract.getDefaults().getNightPremiumPercentage() != null;
        lblOvrNightPercent.setIcon(npp ? SYSConst.icon16apply : SYSConst.icon16infored);


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                validate();
                repaint();
            }
        });

        btnApply.setEnabled(dateok && wph && wdpw && thpm && hpp && nightok && npp);

        return dateok && wph && wdpw && thpm && hpp && nightok && npp;
    }


    private void txtFromFocusLost(FocusEvent e) {
        try {
            LocalDate startDate = new LocalDate(SYSCalendar.parseDate(txtFrom.getText()));
            contract.getDefaults().setFrom(SYSCalendar.bom(startDate));
        } catch (NumberFormatException e1) {
            // too bad
        } finally {
            txtFrom.setText(contract.getDefaults().getFrom().toString("dd.MM.yyyy"));
            checkAll();
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
            // too bad
        } finally {
            if (contract.getDefaults().getTo().equals(SYSConst.LD_UNTIL_FURTHER_NOTICE)) {
                txtTo.setText(OPDE.lang.getString("opde.roster.unlimited"));
            } else {
                txtTo.setText(contract.getDefaults().getTo().toString("dd.MM.yyyy"));
            }
            checkAll();
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
            // too bad
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
            // too bad
        } finally {
            txtWDPW.setText(SYSTools.catchNull(contract.getDefaults().getWorkingDaysPerWeek()));
            if (contract.getDefaults().getTargetHoursPerDay() != null) {
                txtTHPD.setText(contract.getDefaults().getTargetHoursPerDay().setScale(2, RoundingMode.HALF_UP).toString());
            }
            checkAll();
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
                    txtTHPD.setText(contract.getDefaults().getTargetHoursPerDay().setScale(2, RoundingMode.HALF_UP).toString());
                }
            }
        } catch (NumberFormatException e1) {
            // too bad
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
                    txtTHPD.setText(contract.getDefaults().getTargetHoursPerDay().setScale(2, RoundingMode.HALF_UP).toString());
                }
            }
        } catch (NumberFormatException e1) {
            // too bad
            checkAll();
        }
    }

    private void txtTHPDFocusLost(FocusEvent e) {
        if (contract.getDefaults().getWorkingDaysPerWeek() == null) {
            checkAll();
            return;
        }
        try {
            contract.getDefaults().setTargetHoursPerDay(BigDecimal.valueOf(Double.parseDouble(txtTHPD.getText().replaceAll(",", "\\."))));
            if (contract.getDefaults().getTargetHoursPerWeek().compareTo(BigDecimal.ZERO) <= 0) {
                contract.getDefaults().setTargetHoursPerWeek(null);
            } else {
                txtTHPM.setText(contract.getDefaults().getTargetHoursPerMonth().setScale(2, RoundingMode.HALF_UP).toString());
                txtTHPW.setText(contract.getDefaults().getTargetHoursPerWeek().setScale(2, RoundingMode.HALF_UP).toString());
                if (contract.getDefaults().getTargetHoursPerDay() != null) {
                    txtTHPD.setText(contract.getDefaults().getTargetHoursPerDay().setScale(2, RoundingMode.HALF_UP).toString());
                }
            }
        } catch (NumberFormatException e1) {
            // too bad
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
            // too bad
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
            // too bad
        } finally {
            txtNightFrom.setText(contract.getDefaults().getNight().getFirst().toString("HH:mm"));
            txtNightTo.setText(contract.getDefaults().getNight().getSecond().toString("HH:mm"));
            checkAll();
        }
    }

    private void txtNightToFocusLost(FocusEvent e) {
        try {
            if (contract.getDefaults().getNight() == null) {
                contract.getDefaults().setNight(new Pair<LocalTime, LocalTime>(new LocalTime(), new LocalTime()));
            }
            contract.getDefaults().getNight().setSecond(SYSCalendar.parseLocalTime(txtNightTo.getText()));
        } catch (NumberFormatException e1) {
            // too bad
        } finally {
            txtNightFrom.setText(contract.getDefaults().getNight().getFirst().toString("HH:mm"));
            txtNightTo.setText(contract.getDefaults().getNight().getSecond().toString("HH:mm"));
            checkAll();
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
            // too bad
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

    private void btnApplyActionPerformed(ActionEvent e) {
        // TODO add your code here
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label3 = new JLabel();
        panel1 = new JPanel();
        overFrom = new DefaultOverlayable();
        label1 = new JLabel();
        overTo = new DefaultOverlayable();
        btnTo = new JButton();
        overWPH = new DefaultOverlayable();
        panel2 = new JPanel();
        overWDPW = new DefaultOverlayable();
        overTHPM = new DefaultOverlayable();
        overTHPW = new DefaultOverlayable();
        overTHPD = new DefaultOverlayable();
        overHolidayPercent = new DefaultOverlayable();
        panel3 = new JPanel();
        overNightFrom = new DefaultOverlayable();
        label2 = new JLabel();
        overNightTo = new DefaultOverlayable();
        overNightPercent = new DefaultOverlayable();
        pnlPeriods = new JPanel();
        btnApply = new JButton();
        txtWDPW = new JTextField();
        txtTHPM = new JTextField();
        txtTHPW = new JTextField();
        txtTHPD = new JTextField();
        txtWPH = new JTextField();
        txtHolidayPercent = new JTextField();
        txtNightFrom = new JTextField();
        txtNightTo = new JTextField();
        txtNightPercent = new JTextField();
        txtFrom = new JTextField();
        txtTo = new JTextField();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, 122dlu:grow, $lcgap, default",
            "7*(default, $lgap), fill:default:grow, $lgap, default"));

        //---- label3 ----
        label3.setText("text");
        label3.setFont(new Font("Arial", Font.BOLD, 9));
        add(label3, CC.xy(3, 3));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- overFrom ----
            overFrom.setActualComponent(txtFrom);
            panel1.add(overFrom);

            //---- label1 ----
            label1.setText(null);
            label1.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/1rightarrow.png")));
            panel1.add(label1);

            //---- overTo ----
            overTo.setActualComponent(txtTo);
            panel1.add(overTo);

            //---- btnTo ----
            btnTo.setText(null);
            btnTo.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/3rightarrow.png")));
            btnTo.setBorderPainted(false);
            btnTo.setBorder(null);
            btnTo.setContentAreaFilled(false);
            btnTo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnTo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnToActionPerformed(e);
                }
            });
            panel1.add(btnTo);
        }
        add(panel1, CC.xy(3, 5));

        //---- overWPH ----
        overWPH.setActualComponent(txtWPH);
        add(overWPH, CC.xy(3, 7));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

            //---- overWDPW ----
            overWDPW.setActualComponent(txtWDPW);
            panel2.add(overWDPW);

            //---- overTHPM ----
            overTHPM.setActualComponent(txtTHPM);
            panel2.add(overTHPM);

            //---- overTHPW ----
            overTHPW.setActualComponent(txtTHPW);
            panel2.add(overTHPW);

            //---- overTHPD ----
            overTHPD.setActualComponent(txtTHPD);
            panel2.add(overTHPD);
        }
        add(panel2, CC.xy(3, 9, CC.DEFAULT, CC.FILL));

        //---- overHolidayPercent ----
        overHolidayPercent.setActualComponent(txtHolidayPercent);
        add(overHolidayPercent, CC.xy(3, 11));

        //======== panel3 ========
        {
            panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

            //---- overNightFrom ----
            overNightFrom.setActualComponent(txtNightFrom);
            panel3.add(overNightFrom);

            //---- label2 ----
            label2.setText(null);
            label2.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/1rightarrow.png")));
            panel3.add(label2);

            //---- overNightTo ----
            overNightTo.setActualComponent(txtNightTo);
            panel3.add(overNightTo);

            //---- overNightPercent ----
            overNightPercent.setActualComponent(txtNightPercent);
            panel3.add(overNightPercent);
        }
        add(panel3, CC.xy(3, 13));

        //======== pnlPeriods ========
        {
            pnlPeriods.setLayout(new BoxLayout(pnlPeriods, BoxLayout.PAGE_AXIS));
        }
        add(pnlPeriods, CC.xy(3, 15, CC.DEFAULT, CC.FILL));

        //---- btnApply ----
        btnApply.setText(null);
        btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
        add(btnApply, CC.xy(3, 17, CC.RIGHT, CC.DEFAULT));

        //---- txtWDPW ----
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

        //---- txtTHPM ----
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

        //---- txtTHPW ----
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

        //---- txtTHPD ----
        txtTHPD.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtFocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtTHPDFocusLost(e);
            }
        });
        txtTHPD.addPropertyChangeListener("text", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                txtWDPWPropertyChange(e);
            }
        });

        //---- txtWPH ----
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

        //---- txtNightPercent ----
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
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label3;
    private JPanel panel1;
    private DefaultOverlayable overFrom;
    private JLabel label1;
    private DefaultOverlayable overTo;
    private JButton btnTo;
    private DefaultOverlayable overWPH;
    private JPanel panel2;
    private DefaultOverlayable overWDPW;
    private DefaultOverlayable overTHPM;
    private DefaultOverlayable overTHPW;
    private DefaultOverlayable overTHPD;
    private DefaultOverlayable overHolidayPercent;
    private JPanel panel3;
    private DefaultOverlayable overNightFrom;
    private JLabel label2;
    private DefaultOverlayable overNightTo;
    private DefaultOverlayable overNightPercent;
    private JPanel pnlPeriods;
    private JButton btnApply;
    private JTextField txtWDPW;
    private JTextField txtTHPM;
    private JTextField txtTHPW;
    private JTextField txtTHPD;
    private JTextField txtWPH;
    private JTextField txtHolidayPercent;
    private JTextField txtNightFrom;
    private JTextField txtNightTo;
    private JTextField txtNightPercent;
    private JTextField txtFrom;
    private JTextField txtTo;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
