/*
 * Created by JFormDesigner on Tue Jan 14 14:51:47 CET 2014
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.roster.Timeclock;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class PnlControllerHeader extends JPanel {

    // all time clocks that start on this particular day
    ArrayList<Timeclock> listTimeClocks;

    public PnlControllerHeader() {

        initComponents();
        initPanel();
    }

    private void initPanel() {

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        lblDate = new JLabel();
        lblPlan = new JLabel();
        lblWorkingLog = new JLabel();
        lblTimeClock = new JLabel();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                "60dlu, $lcgap, 67dlu:grow, 2*($lcgap, 150dlu)",
                "pref"));

            //---- lblDate ----
            lblDate.setText("Date");
            lblDate.setFont(new Font("Arial", Font.BOLD, 16));
            lblDate.setHorizontalAlignment(SwingConstants.CENTER);
            lblDate.setBackground(new Color(204, 204, 255));
            lblDate.setOpaque(true);
            panel1.add(lblDate, CC.xy(1, 1, CC.DEFAULT, CC.FILL));

            //---- lblPlan ----
            lblPlan.setText("Plan");
            lblPlan.setFont(new Font("Arial", Font.BOLD, 16));
            lblPlan.setHorizontalAlignment(SwingConstants.CENTER);
            lblPlan.setBackground(new Color(204, 204, 255));
            lblPlan.setOpaque(true);
            panel1.add(lblPlan, CC.xy(3, 1, CC.DEFAULT, CC.FILL));

            //---- lblWorkingLog ----
            lblWorkingLog.setText("WorkingLog");
            lblWorkingLog.setFont(new Font("Arial", Font.BOLD, 16));
            lblWorkingLog.setHorizontalAlignment(SwingConstants.CENTER);
            lblWorkingLog.setBackground(new Color(204, 204, 255));
            lblWorkingLog.setOpaque(true);
            panel1.add(lblWorkingLog, CC.xy(5, 1, CC.DEFAULT, CC.FILL));

            //---- lblTimeClock ----
            lblTimeClock.setText("TimeClock");
            lblTimeClock.setFont(new Font("Arial", Font.BOLD, 16));
            lblTimeClock.setHorizontalAlignment(SwingConstants.CENTER);
            lblTimeClock.setBackground(new Color(204, 204, 255));
            lblTimeClock.setOpaque(true);
            panel1.add(lblTimeClock, CC.xy(7, 1, CC.DEFAULT, CC.FILL));
        }
        add(panel1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel lblDate;
    private JLabel lblPlan;
    private JLabel lblWorkingLog;
    private JLabel lblTimeClock;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
