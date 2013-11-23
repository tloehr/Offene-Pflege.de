/*
 * Created by JFormDesigner on Tue Oct 15 16:17:33 CEST 2013
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.roster.*;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.LocalDate;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

/**
 * @author Torsten Löhr
 */
public class PnlWorkingLogSingle extends JPanel {
    private LocalDate day;
    private Workinglog workinglog;
    private Closure afterAction;
    private boolean ok1 = true, ok2 = false;

    public PnlWorkingLogSingle(Rplan rplan, Closure afterAction) {
        this.afterAction = afterAction;
        workinglog = new Workinglog(BigDecimal.ONE, BigDecimal.ZERO, rplan, WorkinglogTools.TYPE_MANUAL, 0l);
        day = new LocalDate(rplan.getStart());

        initComponents();
        initPanel();
    }

    void initPanel() {
        btnApply.setEnabled(false);
        lblDay.setText(day.toString("EEEE dd.MM.yyyy"));
    }

    private void txtAdditionalHoursCaretUpdate(CaretEvent evt) {
        BigDecimal hours = SYSTools.checkBigDecimal(evt, true, true);
//        OPDE.debug(hours);
        if (hours == null) {
            ok1 = false;
        } else {
            if (hours.compareTo(BigDecimal.TEN) >= 0) {
                ok1 = false;
            } else if (hours.compareTo(BigDecimal.ZERO) <= 0) {
                ok1 = false;
            } else {
                workinglog.setHours(hours);
                ok1 = true;
            }
        }
        btnApply.setEnabled(ok1 && ok2);
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        afterAction.execute(workinglog);
    }

    private void txtCommentCaretUpdate(CaretEvent e) {
        workinglog.setText(txtComment.getText().trim());
        ok2 = !txtComment.getText().trim().isEmpty();
        btnApply.setEnabled(ok1 && ok2);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblDay = new JLabel();
        txtHours = new JTextField();
        txtComment = new JTextField();
        btnApply = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, 164dlu, $lcgap, default",
            "4*(default, $lgap), default"));

        //---- lblDay ----
        lblDay.setText("text");
        lblDay.setFont(new Font("Arial", Font.BOLD, 14));
        add(lblDay, CC.xy(3, 3));

        //---- txtHours ----
        txtHours.setText("1,0");
        txtHours.setHorizontalAlignment(SwingConstants.TRAILING);
        txtHours.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                txtAdditionalHoursCaretUpdate(e);
            }
        });
        add(txtHours, CC.xy(3, 5));

        //---- txtComment ----
        txtComment.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                txtCommentCaretUpdate(e);
            }
        });
        add(txtComment, CC.xy(3, 7));

        //---- btnApply ----
        btnApply.setText("text");
        btnApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnApplyActionPerformed(e);
            }
        });
        add(btnApply, CC.xy(3, 9, CC.RIGHT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblDay;
    private JTextField txtHours;
    private JTextField txtComment;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
