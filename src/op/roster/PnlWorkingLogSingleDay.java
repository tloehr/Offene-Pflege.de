/*
 * Created by JFormDesigner on Tue Oct 15 16:17:33 CEST 2013
 */

package op.roster;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import entity.roster.Rplan;
import entity.roster.Workinglog;
import org.joda.time.LocalDate;

/**
 * @author Torsten Löhr
 */
public class PnlWorkingLogSingleDay extends JPanel {
    private LocalDate day;
    private Workinglog workinglog;

    public PnlWorkingLogSingleDay(Workinglog workinglog) {
        this.workinglog = workinglog;
        day = new LocalDate(workinglog.getRplan().getStart());

        initComponents();
        initPanel();
    }

    void initPanel(){
        lblDay.setText(day.toString("EE dd."));
        lblPlan.setText(workinglog.getRplan().getEffectiveP());
        txtActual.setText(workinglog.getActual());
        txtAdditionalHours.setText(workinglog.getAdditional().toString());
        txtAdditionalText.setText(workinglog.getText());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblDay = new JLabel();
        lblPlan = new JLabel();
        txtActual = new JTextField();
        txtAdditionalHours = new JTextField();
        txtAdditionalText = new JTextField();
        lblHourSum = new JLabel();

        //======== this ========
        setLayout(new FormLayout(
            "default:grow",
            "5*(default, $lgap), default"));

        //---- lblDay ----
        lblDay.setText("text");
        lblDay.setFont(new Font("Arial", Font.BOLD, 14));
        add(lblDay, CC.xy(1, 1));

        //---- lblPlan ----
        lblPlan.setText("text");
        add(lblPlan, CC.xy(1, 3));
        add(txtActual, CC.xy(1, 5));
        add(txtAdditionalHours, CC.xy(1, 7));
        add(txtAdditionalText, CC.xy(1, 9));

        //---- lblHourSum ----
        lblHourSum.setText("text");
        add(lblHourSum, CC.xy(1, 11));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblDay;
    private JLabel lblPlan;
    private JTextField txtActual;
    private JTextField txtAdditionalHours;
    private JTextField txtAdditionalText;
    private JLabel lblHourSum;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
