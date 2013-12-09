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
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Torsten Löhr
 */
public class PnlContractsEditor extends JPanel {

    private UserContract contract;
    //    private JTextField txtWDPW, txtTHPM, txtTHPW, txtTHPD
    private Users user;
    private JLabel lblOvrFrom, lblOvrTo, lblOvrWPH, lblOvrWDPW, lblOvrTHPM, lblOvrTHPW, lblOvrTHPD;


//    public PnlContractsEditor(Users user) {
//        this(new UserContract(new ContractsParameterSet()), user);
//        this.contract.getDefaults().setExam(UsersTools.isQualified(user)); // just as a suggestion for new contracts
//    }

    public PnlContractsEditor(UserContract contract, Users user) {
        this.contract = contract;
        this.user = user;
        initComponents();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                revalidate();
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

        txtWPH.setText(SYSTools.catchNull(contract.getDefaults().getWagePerHour()));
        txtWDPW.setText(SYSTools.catchNull(contract.getDefaults().getWorkingDaysPerWeek()));
        txtTHPM.setText(SYSTools.catchNull(contract.getDefaults().getTargetHoursPerMonth()));
        txtTHPW.setText(SYSTools.catchNull(contract.getDefaults().getTargetHoursPerWeek()));

        if (contract.getDefaults().getTargetHoursPerWeek() != null && contract.getDefaults().getWorkingDaysPerWeek() != null) {
            txtTHPD.setText(contract.getDefaults().getTargetHoursPerWeek().divide(contract.getDefaults().getWorkingDaysPerWeek(), 2, RoundingMode.HALF_UP).toString());
        }

        JLabel lblFrom = new JLabel(OPDE.lang.getString("misc.msg.from") + " ");
        lblFrom.setFont(SYSConst.ARIAL10BOLD);
        lblFrom.setForeground(Color.LIGHT_GRAY);
        overFrom.addOverlayComponent(lblFrom, SwingConstants.EAST);
        overFrom.addOverlayComponent(lblOvrFrom, SwingConstants.SOUTH_EAST);

        JLabel lblTo = new JLabel(OPDE.lang.getString("misc.msg.to") + " ");
        lblTo.setFont(SYSConst.ARIAL10BOLD);
        lblTo.setForeground(Color.LIGHT_GRAY);
        overTo.addOverlayComponent(lblTo, SwingConstants.EAST);
        overTo.addOverlayComponent(lblOvrTo, SwingConstants.SOUTH_EAST);

        JLabel lblWage = new JLabel(OPDE.lang.getString("opde.roster.wage.per.hour") + " ");
        lblWage.setFont(SYSConst.ARIAL10BOLD);
        lblWage.setForeground(Color.LIGHT_GRAY);
        overWPH.addOverlayComponent(lblWage, SwingConstants.EAST);
        overWPH.addOverlayComponent(lblOvrWPH, SwingConstants.SOUTH_EAST);

        JLabel lblWDPW = new JLabel(OPDE.lang.getString("opde.roster.workingdays.per.week") + " ");
        lblWDPW.setFont(SYSConst.ARIAL10BOLD);
        lblWDPW.setForeground(Color.LIGHT_GRAY);
        overWDPW.addOverlayComponent(lblWDPW, SwingConstants.EAST);
        overWDPW.addOverlayComponent(lblOvrWDPW, SwingConstants.SOUTH_EAST);

        JLabel lblTHPM = new JLabel(OPDE.lang.getString("opde.roster.targethours.per.month") + " ");
        lblTHPM.setFont(SYSConst.ARIAL10BOLD);
        lblTHPM.setForeground(Color.LIGHT_GRAY);
        overTHPM.addOverlayComponent(lblTHPM, SwingConstants.EAST);
        overTHPM.addOverlayComponent(lblOvrTHPM, SwingConstants.SOUTH_EAST);

        JLabel lblTHPW = new JLabel(OPDE.lang.getString("opde.roster.targethours.per.week") + " ");
        lblTHPW.setFont(SYSConst.ARIAL10BOLD);
        lblTHPW.setForeground(Color.LIGHT_GRAY);
        overTHPW.addOverlayComponent(lblTHPW, SwingConstants.EAST);
        overTHPW.addOverlayComponent(lblOvrTHPW, SwingConstants.SOUTH_EAST);

        JLabel lblTHPD = new JLabel(OPDE.lang.getString("opde.roster.targethours.per.day") + " ");
        lblTHPD.setFont(SYSConst.ARIAL10BOLD);
        lblTHPD.setForeground(Color.LIGHT_GRAY);
        overTHPD.addOverlayComponent(lblTHPD, SwingConstants.EAST);
        overTHPD.addOverlayComponent(lblOvrTHPD, SwingConstants.SOUTH_EAST);

        JLabel lblNightFrom = new JLabel(OPDE.lang.getString("opde.roster.night.from") + " ");
        lblNightFrom.setFont(SYSConst.ARIAL10BOLD);
        lblNightFrom.setForeground(Color.LIGHT_GRAY);

    }

    private boolean checkAll() {

        lblOvrFrom.setIcon(contract.getDefaults().getFrom().isBefore(contract.getDefaults().getTo()) ? SYSConst.icon16apply : SYSConst.icon16infored);
        lblOvrTo.setIcon(contract.getDefaults().getFrom().isBefore(contract.getDefaults().getTo()) ? SYSConst.icon16apply : SYSConst.icon16infored);

        boolean wph = contract.getDefaults().getWagePerHour() != null;
        lblOvrWPH.setIcon(wph ? SYSConst.icon16apply : SYSConst.icon16infored);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                revalidate();
                repaint();
            }
        });

        return false;
    }


    private void txtFromFocusLost(FocusEvent e) {
        try {
            contract.getDefaults().setFrom(new LocalDate(SYSCalendar.parseDate(txtFrom.getText())));
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
                contract.getDefaults().setTo(new LocalDate(SYSCalendar.parseDate(txtTo.getText())));
            }
        } catch (NumberFormatException e1) {
            // too bad
        } finally {
            if (contract.getDefaults().getTo().equals(SYSConst.LD_UNTIL_FURTHER_NOTICE)) {
                txtTo.setText(OPDE.lang.getString("opde.roster.unlimited"));
            } else {
                txtFrom.setText(contract.getDefaults().getFrom().toString("dd.MM.yyyy"));
            }
            checkAll();
        }
    }

    private void txtWPHFocusLost(FocusEvent e) {
        try {
            contract.getDefaults().setWagePerHour(BigDecimal.valueOf(Double.parseDouble(txtWPH.getText().replaceAll(",", "\\."))));
            if (contract.getDefaults().getWagePerHour().compareTo(BigDecimal.ZERO) <= 0) {
                contract.getDefaults().setWagePerHour(null);
            }
        } catch (NumberFormatException e1) {
            // too bad
        } finally {
            if (contract.getDefaults().getWagePerHour() != null){
                txtWPH.setText(contract.getDefaults().getWagePerHour().setScale(2, RoundingMode.HALF_UP).toString());
            } else {
                txtWPH.setText(null);
            }

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
            checkAll();
        }
    }

    private void txtTHPWFocusLost(FocusEvent e) {
        try {
            contract.getDefaults().setTargetHoursPerWeek(BigDecimal.valueOf(Double.parseDouble(txtTHPW.getText().replaceAll(",", "\\."))));
            if (contract.getDefaults().getTargetHoursPerWeek().compareTo(BigDecimal.ZERO) <= 0) {
                contract.getDefaults().setTargetHoursPerWeek(null);
            }
        } catch (NumberFormatException e1) {
            // too bad
        } finally {
            txtTHPM.setText(SYSTools.catchNull(contract.getDefaults().getTargetHoursPerMonth()));
            txtTHPW.setText(SYSTools.catchNull(contract.getDefaults().getTargetHoursPerWeek()));
            txtTHPD.setText(SYSTools.catchNull(contract.getDefaults().getTargetHoursPerDay()));
            checkAll();
        }
    }

    private void txtTHPMFocusLost(FocusEvent e) {
        try {
            contract.getDefaults().setTargetHoursPerMonth(BigDecimal.valueOf(Double.parseDouble(txtTHPM.getText().replaceAll(",", "\\."))));
            if (contract.getDefaults().getTargetHoursPerMonth().compareTo(BigDecimal.ZERO) <= 0) {
                contract.getDefaults().setTargetHoursPerMonth(null);
            }
        } catch (NumberFormatException e1) {
            // too bad
        } finally {
            txtTHPM.setText(SYSTools.catchNull(contract.getDefaults().getTargetHoursPerMonth()));
            txtTHPW.setText(SYSTools.catchNull(contract.getDefaults().getTargetHoursPerWeek()));
            txtTHPD.setText(SYSTools.catchNull(contract.getDefaults().getTargetHoursPerDay()));
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
            }
        } catch (NumberFormatException e1) {
            // too bad
        } finally {
            txtTHPM.setText(SYSTools.catchNull(contract.getDefaults().getTargetHoursPerMonth()));
            txtTHPW.setText(SYSTools.catchNull(contract.getDefaults().getTargetHoursPerWeek()));
            txtTHPD.setText(SYSTools.catchNull(contract.getDefaults().getTargetHoursPerDay()));
            checkAll();
        }
    }

    private void txtHolidayPercentFocusLost(FocusEvent e) {
        // TODO add your code here
    }

    private void txtNightFromFocusLost(FocusEvent e) {
        // TODO add your code here
    }

    private void txtNightToFocusLost(FocusEvent e) {
        // TODO add your code here
    }

    private void txtNightPercentFocusLost(FocusEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        overFrom = new DefaultOverlayable();
        label1 = new JLabel();
        overTo = new DefaultOverlayable();
        button1 = new JButton();
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
                "default, $lcgap, default:grow, $lcgap, default",
                "5*(default, $lgap), default"));

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

            //---- button1 ----
            button1.setText(null);
            button1.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/3rightarrow.png")));
            button1.setBorderPainted(false);
            button1.setBorder(null);
            button1.setContentAreaFilled(false);
            button1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            panel1.add(button1);
        }
        add(panel1, CC.xywh(3, 3, 2, 1));

        //---- overWPH ----
        overWPH.setActualComponent(txtWPH);
        add(overWPH, CC.xywh(3, 5, 2, 1));

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
        add(panel2, CC.xywh(3, 7, 2, 1, CC.DEFAULT, CC.FILL));

        //---- overHolidayPercent ----
        overHolidayPercent.setActualComponent(txtHolidayPercent);
        add(overHolidayPercent, CC.xywh(3, 9, 2, 1));

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
        add(panel3, CC.xy(3, 11));

        //---- txtWDPW ----
        txtWDPW.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtWDPWFocusLost(e);
            }
        });

        //---- txtTHPM ----
        txtTHPM.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtTHPMFocusLost(e);
            }
        });

        //---- txtTHPW ----
        txtTHPW.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtTHPWFocusLost(e);
            }
        });

        //---- txtTHPD ----
        txtTHPD.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtTHPDFocusLost(e);
            }
        });

        //---- txtWPH ----
        txtWPH.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtWPHFocusLost(e);
            }
        });

        //---- txtHolidayPercent ----
        txtHolidayPercent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtHolidayPercentFocusLost(e);
            }
        });

        //---- txtNightFrom ----
        txtNightFrom.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtNightFromFocusLost(e);
            }
        });

        //---- txtNightTo ----
        txtNightTo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtNightToFocusLost(e);
            }
        });

        //---- txtNightPercent ----
        txtNightPercent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtNightPercentFocusLost(e);
            }
        });

        //---- txtFrom ----
        txtFrom.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtFromFocusLost(e);
            }
        });

        //---- txtTo ----
        txtTo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtToFocusLost(e);
            }
        });
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private DefaultOverlayable overFrom;
    private JLabel label1;
    private DefaultOverlayable overTo;
    private JButton button1;
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
