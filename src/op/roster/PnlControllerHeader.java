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
        lblActual = new JLabel();
        lblOperations = new JLabel();
        lblWorkingLog = new JLabel();
        lblCtrl = new JLabel();
        lblTimeClock = new JLabel();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                "60dlu, 2*(60dlu:grow), 2*(default, 100dlu)",
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
            panel1.add(lblPlan, CC.xy(2, 1, CC.DEFAULT, CC.FILL));

            //---- lblActual ----
            lblActual.setText("Tats\u00e4chlich");
            lblActual.setFont(new Font("Arial", Font.BOLD, 16));
            lblActual.setHorizontalAlignment(SwingConstants.CENTER);
            lblActual.setBackground(new Color(204, 204, 255));
            lblActual.setOpaque(true);
            panel1.add(lblActual, CC.xy(3, 1, CC.DEFAULT, CC.FILL));

            //---- lblOperations ----
            lblOperations.setText("\u00dcbernahme");
            lblOperations.setFont(new Font("Arial", Font.BOLD, 16));
            lblOperations.setHorizontalAlignment(SwingConstants.CENTER);
            lblOperations.setBackground(new Color(204, 204, 255));
            lblOperations.setOpaque(true);
            panel1.add(lblOperations, CC.xy(4, 1, CC.DEFAULT, CC.FILL));

            //---- lblWorkingLog ----
            lblWorkingLog.setText("WorkingLog");
            lblWorkingLog.setFont(new Font("Arial", Font.BOLD, 16));
            lblWorkingLog.setHorizontalAlignment(SwingConstants.CENTER);
            lblWorkingLog.setBackground(new Color(204, 204, 255));
            lblWorkingLog.setOpaque(true);
            panel1.add(lblWorkingLog, CC.xy(5, 1, CC.DEFAULT, CC.FILL));

            //---- lblCtrl ----
            lblCtrl.setText("Kontrolle");
            lblCtrl.setFont(new Font("Arial", Font.BOLD, 16));
            lblCtrl.setHorizontalAlignment(SwingConstants.CENTER);
            lblCtrl.setBackground(new Color(204, 204, 255));
            lblCtrl.setOpaque(true);
            panel1.add(lblCtrl, CC.xy(6, 1, CC.DEFAULT, CC.FILL));

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
    private JLabel lblActual;
    private JLabel lblOperations;
    private JLabel lblWorkingLog;
    private JLabel lblCtrl;
    private JLabel lblTimeClock;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
