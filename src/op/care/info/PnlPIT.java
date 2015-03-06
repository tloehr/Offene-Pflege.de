/*
 * Created by JFormDesigner on Fri Jul 13 15:32:45 CEST 2012
 */

package op.care.info;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JCalendar;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import org.apache.commons.collections.Closure;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.GregorianCalendar;

/**
 * @author Torsten Löhr
 */
public class PnlPIT extends JPanel {
    private final DateTime preset;
    private LocalTime time;
    private final DateTime max;
    private final DateTime min;
    private Closure actionBlock;

    public PnlPIT(DateTime preset, DateTime max, DateTime min, Closure actionBlock) {
        this.preset = preset;
        this.max = max == null ? new DateTime() : max;
        this.min = min == null ? new DateTime(SYSConst.DATE_THE_VERY_BEGINNING) : min;

        this.actionBlock = actionBlock;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        txtTime.setText(preset.toString("HH:mm"));
        cal1.setDate(preset.toDate());
        cal1.setMinSelectableDate(this.min.toDate());
        cal1.setMaxSelectableDate(this.max.toDate());
    }


    public DateTime getPIT() {
        return new LocalDate(cal1.getDate()).toDateTime(time);
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        actionBlock.execute(null);
    }

    private void btnOKActionPerformed(ActionEvent e) {
        actionBlock.execute(getPIT());
    }

    private void txtTimeActionPerformed(ActionEvent e) {

        GregorianCalendar gc;
        try {
            gc = SYSCalendar.parseTime(txtTime.getText());
        } catch (NumberFormatException nfe) {
            gc = null;
        }

        DateTime pit;
        if (gc != null) {
//            DateTime time = new DateTime(gc.getTimeInMillis());
//            LocalDate day = ;
            pit = new LocalDate(cal1.getDate()).toDateTime(new LocalTime(gc.getTimeInMillis()));

            if (pit.isAfter(new DateTime(max))) {
                pit = new DateTime(max);
            }

            if (pit.isBefore(new DateTime(min))) {
                pit = new DateTime(min);
            }

        } else {
            pit = new DateTime();
        }


        txtTime.setText(pit.toString("HH:mm"));
        time = pit.toLocalTime();


    }

    private void txtTimeFocusLost(FocusEvent e) {
        txtTimeActionPerformed(null);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        cal1 = new JCalendar();
        txtTime = new JTextField();
        panel3 = new JPanel();
        btnCancel = new JButton();
        btnOK = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default",
            "default, fill:default:grow, 3*($lgap, default)"));
        add(cal1, CC.xy(3, 2));

        //---- txtTime ----
        txtTime.setFont(new Font("Arial", Font.PLAIN, 16));
        txtTime.addActionListener(e -> txtTimeActionPerformed(e));
        txtTime.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtTimeFocusLost(e);
            }
        });
        add(txtTime, CC.xy(3, 4));

        //======== panel3 ========
        {
            panel3.setLayout(new BoxLayout(panel3, BoxLayout.LINE_AXIS));

            //---- btnCancel ----
            btnCancel.setText(null);
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/cancel.png")));
            btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnCancel.addActionListener(e -> btnCancelActionPerformed(e));
            panel3.add(btnCancel);

            //---- btnOK ----
            btnOK.setText(null);
            btnOK.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/apply.png")));
            btnOK.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnOK.addActionListener(e -> btnOKActionPerformed(e));
            panel3.add(btnOK);
        }
        add(panel3, CC.xy(3, 6, CC.RIGHT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JCalendar cal1;
    private JTextField txtTime;
    private JPanel panel3;
    private JButton btnCancel;
    private JButton btnOK;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
