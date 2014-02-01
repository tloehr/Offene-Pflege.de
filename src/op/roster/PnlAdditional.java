/*
 * Created by JFormDesigner on Sat Feb 01 11:44:52 CET 2014
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;

/**
 * @author Torsten Löhr
 */
public class PnlAdditional extends JPanel {
    private final LocalDate refDate;
    private final Closure afterAction;
    DateTime from = null;
    DateTime to = null;
    BigDecimal hours = null;

    public PnlAdditional(LocalDate refDate, Closure afterAction) {
        this.refDate = refDate;
        this.afterAction = afterAction;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        lblFrom.setText(OPDE.lang.getString("misc.msg.from"));
        lblTo.setText(OPDE.lang.getString("misc.msg.to"));
        lblHours.setText(OPDE.lang.getString("misc.msg.Hours"));
        lblText.setText(OPDE.lang.getString("misc.msg.comment"));

    }

    private void btnApplyActionPerformed(ActionEvent e) {
        afterAction.execute(new Object[]{from, to, hours, txtText.getText()});
    }

    private void txtFromFocusLost(FocusEvent e) {
        try {
            from = SYSCalendar.parseLocalTime(txtFrom.getText()).toDateTimeToday();

            if (to == null) {
                hours = null;
                txtHours.setText(null);
            } else {
                if (from.compareTo(to) >= 0) {
                    to = to.plusDays(1);
                }
                hours = SYSCalendar.getHoursAsDecimal(from, to).setScale(2, BigDecimal.ROUND_HALF_UP);
                txtHours.setText(hours.toString());
            }
        } catch (NumberFormatException nfe) {

        }
    }

    private void txtToFocusLost(FocusEvent e) {
        try {
            to = SYSCalendar.parseLocalTime(txtTo.getText()).toDateTimeToday();

            if (from == null) {
                hours = null;
                txtHours.setText(null);
            } else {
                if (from.compareTo(to) >= 0) {
                    to = to.plusDays(1);
                }
                hours = SYSCalendar.getHoursAsDecimal(from, to).setScale(2, BigDecimal.ROUND_HALF_UP);
                txtHours.setText(hours.toString());
            }
        } catch (NumberFormatException nfe) {

        }
    }

    private void txtHoursCaretUpdate(CaretEvent e) {

    }

    private void txtHoursFocusLost(FocusEvent e) {
        if (SYSTools.catchNull(txtFrom.getText()).isEmpty()) {
            hours = SYSTools.parseBigDecimal(txtHours.getText());
        }
    }

    private void txtHoursKeyTyped(KeyEvent e) {
        txtFrom.setText(null);
        txtTo.setText(null);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblFrom = new JLabel();
        lblTo = new JLabel();
        txtFrom = new JTextField();
        txtTo = new JTextField();
        lblHours = new JLabel();
        lblText = new JLabel();
        txtHours = new JTextField();
        txtText = new JTextField();
        panel1 = new JPanel();
        btnApply = new JButton();

        //======== this ========
        setFocusCycleRoot(true);
        setFocusTraversalPolicyProvider(true);
        setLayout(new FormLayout(
            "default, 2*($lcgap, 80dlu), $lcgap, default",
            "4*($lgap, default), $pgap, default"));

        //---- lblFrom ----
        lblFrom.setText("text");
        lblFrom.setFont(new Font("Arial", Font.PLAIN, 11));
        add(lblFrom, CC.xy(3, 2, CC.RIGHT, CC.DEFAULT));

        //---- lblTo ----
        lblTo.setText("text");
        lblTo.setFont(new Font("Arial", Font.PLAIN, 11));
        add(lblTo, CC.xy(5, 2, CC.RIGHT, CC.DEFAULT));

        //---- txtFrom ----
        txtFrom.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtFromFocusLost(e);
            }
        });
        add(txtFrom, CC.xy(3, 4));

        //---- txtTo ----
        txtTo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtToFocusLost(e);
            }
        });
        add(txtTo, CC.xy(5, 4));

        //---- lblHours ----
        lblHours.setText("text");
        lblHours.setFont(new Font("Arial", Font.PLAIN, 11));
        add(lblHours, CC.xy(3, 6, CC.RIGHT, CC.DEFAULT));

        //---- lblText ----
        lblText.setText("text");
        lblText.setFont(new Font("Arial", Font.PLAIN, 11));
        add(lblText, CC.xy(5, 6, CC.RIGHT, CC.DEFAULT));

        //---- txtHours ----
        txtHours.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                txtHoursCaretUpdate(e);
            }
        });
        txtHours.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtHoursFocusLost(e);
            }
        });
        txtHours.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                txtHoursKeyTyped(e);
            }
        });
        add(txtHours, CC.xy(3, 8));
        add(txtText, CC.xy(5, 8));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                "default:grow",
                "default"));

            //---- btnApply ----
            btnApply.setText(null);
            btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnApply.setBorderPainted(false);
            btnApply.setBorder(null);
            btnApply.setContentAreaFilled(false);
            btnApply.setSelectedIcon(null);
            btnApply.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply_pressed.png")));
            btnApply.setFocusable(false);
            btnApply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnApplyActionPerformed(e);
                }
            });
            panel1.add(btnApply, CC.xy(1, 1, CC.RIGHT, CC.DEFAULT));
        }
        add(panel1, CC.xywh(3, 10, 3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblFrom;
    private JLabel lblTo;
    private JTextField txtFrom;
    private JTextField txtTo;
    private JLabel lblHours;
    private JLabel lblText;
    private JTextField txtHours;
    private JTextField txtText;
    private JPanel panel1;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
