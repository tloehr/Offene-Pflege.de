/*
 * Created by JFormDesigner on Fri Dec 27 16:16:10 CET 2013
 */

package op.roster;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Torsten Löhr
 */
public class PnlControllerView extends JPanel {
    public PnlControllerView() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblDate = new JLabel();
        lblPlan = new JLabel();
        lblTimeClock = new JLabel();
        lblOverride = new JLabel();
        btnPlan = new JButton();
        btnTimeClock = new JButton();
        panel1 = new JPanel();
        cmbOverride = new JComboBox();
        btnOverride = new JButton();
        lblFrom = new JLabel();
        lblTo = new JLabel();
        txtFrom = new JTextField();
        txtTo = new JTextField();
        scrollPane1 = new JScrollPane();
        panel2 = new JPanel();
        lbltext = new JLabel();
        txTtext = new JTextField();

        //======== this ========
        setLayout(new FormLayout(
            "2*(default:grow, $lcgap), default:grow",
            "3*(default, $lgap), default, $nlgap, default, $lgap, default, $nlgap, default, $lgap, default:grow"));

        //---- lblDate ----
        lblDate.setText("Mo, 03.06");
        lblDate.setFont(new Font("Arial", Font.BOLD, 16));
        lblDate.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblDate, CC.xywh(1, 1, 5, 1));

        //---- lblPlan ----
        lblPlan.setText("Planung");
        add(lblPlan, CC.xy(1, 3));

        //---- lblTimeClock ----
        lblTimeClock.setText("MA Angaben");
        add(lblTimeClock, CC.xy(3, 3));

        //---- lblOverride ----
        lblOverride.setText("Korrektur");
        add(lblOverride, CC.xy(5, 3));

        //---- btnPlan ----
        btnPlan.setText("text");
        add(btnPlan, CC.xy(1, 5));

        //---- btnTimeClock ----
        btnTimeClock.setText("text");
        add(btnTimeClock, CC.xy(3, 5));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));
            panel1.add(cmbOverride);

            //---- btnOverride ----
            btnOverride.setText("text");
            panel1.add(btnOverride);
        }
        add(panel1, CC.xy(5, 5));

        //---- lblFrom ----
        lblFrom.setText("text");
        lblFrom.setFont(new Font("Arial", Font.PLAIN, 11));
        lblFrom.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblFrom, CC.xy(1, 7));

        //---- lblTo ----
        lblTo.setText("text");
        lblTo.setFont(new Font("Arial", Font.PLAIN, 11));
        lblTo.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblTo, CC.xy(3, 7));
        add(txtFrom, CC.xy(1, 9));
        add(txtTo, CC.xy(3, 9));

        //======== scrollPane1 ========
        {

            //======== panel2 ========
            {
                panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));
            }
            scrollPane1.setViewportView(panel2);
        }
        add(scrollPane1, CC.xywh(5, 7, 1, 9));

        //---- lbltext ----
        lbltext.setText("text");
        lbltext.setFont(new Font("Arial", Font.PLAIN, 11));
        lbltext.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lbltext, CC.xy(3, 11));
        add(txTtext, CC.xywh(1, 13, 3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblDate;
    private JLabel lblPlan;
    private JLabel lblTimeClock;
    private JLabel lblOverride;
    private JButton btnPlan;
    private JButton btnTimeClock;
    private JPanel panel1;
    private JComboBox cmbOverride;
    private JButton btnOverride;
    private JLabel lblFrom;
    private JLabel lblTo;
    private JTextField txtFrom;
    private JTextField txtTo;
    private JScrollPane scrollPane1;
    private JPanel panel2;
    private JLabel lbltext;
    private JTextField txTtext;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
