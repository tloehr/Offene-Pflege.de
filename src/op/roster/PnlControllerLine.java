/*
 * Created by JFormDesigner on Tue Jan 14 14:51:47 CET 2014
 */

package op.roster;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Torsten Löhr
 */
public class PnlControllerLine extends JPanel {
    public PnlControllerLine() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        lblDate2 = new JLabel();
        lblPlan = new JLabel();
        scrollPane1 = new JScrollPane();
        tblTimeclock = new JTable();
        scrollPane2 = new JScrollPane();
        lstWLogs = new JList();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                "default, 2*($lcgap, default:grow), $lcgap, 85dlu:grow",
                "default, $lgap, 62dlu:grow"));

            //---- label2 ----
            label2.setText("Datum");
            panel1.add(label2, CC.xy(1, 1));

            //---- label3 ----
            label3.setText("Soll/ist");
            panel1.add(label3, CC.xy(3, 1));

            //---- label4 ----
            label4.setText("Zeiterfassung");
            panel1.add(label4, CC.xy(5, 1));

            //---- label5 ----
            label5.setText("Arbeitszeiten");
            panel1.add(label5, CC.xy(7, 1));

            //---- lblDate2 ----
            lblDate2.setText("03.06.14");
            lblDate2.setFont(new Font("Arial", Font.BOLD, 16));
            lblDate2.setHorizontalAlignment(SwingConstants.CENTER);
            lblDate2.setBackground(new Color(204, 204, 255));
            lblDate2.setOpaque(true);
            panel1.add(lblDate2, CC.xy(1, 3, CC.DEFAULT, CC.FILL));

            //---- lblPlan ----
            lblPlan.setText("text");
            panel1.add(lblPlan, CC.xy(3, 3));

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(tblTimeclock);
            }
            panel1.add(scrollPane1, CC.xy(5, 3));

            //======== scrollPane2 ========
            {
                scrollPane2.setViewportView(lstWLogs);
            }
            panel1.add(scrollPane2, CC.xy(7, 3, CC.FILL, CC.FILL));
        }
        add(panel1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel lblDate2;
    private JLabel lblPlan;
    private JScrollPane scrollPane1;
    private JTable tblTimeclock;
    private JScrollPane scrollPane2;
    private JList lstWLogs;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
