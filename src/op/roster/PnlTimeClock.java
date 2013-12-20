/*
 * Created by JFormDesigner on Fri Dec 20 11:54:13 CET 2013
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.HomesTools;
import entity.roster.*;
import entity.system.Users;
import op.OPDE;
import op.tools.SYSCalendar;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Torsten Löhr
 */
public class PnlTimeClock extends JPanel {
    private LocalDate currentDate;
    private Users user;
    private HashMap<LocalDate, Rosters> rosters;
    private HashMap<LocalDate, RosterParameters> rosterparameters;
    private Interval minmax;


    public PnlTimeClock(Users user) {
        this.user = user;
        currentDate = new LocalDate();
        minmax = RostersTools.getMinMax(RostersTools.SECTION_CARE);
        rosters = new HashMap<LocalDate, Rosters>();
        rosterparameters = new HashMap<LocalDate, RosterParameters>();
        HomesTools.setComboBox(cmbHome);


        initComponents();
        initPanel();
    }

    private void initPanel() {

        if (!minmax.contains(currentDate.toDateTimeAtCurrentTime())) {
            currentDate = minmax.getEnd().toLocalDate();
        }

        if (!rosters.containsKey(SYSCalendar.bom(currentDate))) {
            rosters.put(SYSCalendar.bom(currentDate), RostersTools.get(currentDate, RostersTools.SECTION_CARE));
            rosterparameters.put(SYSCalendar.bom(currentDate), RostersTools.getParameters(rosters.get(SYSCalendar.bom(currentDate))));
        }

        Rosters roster = rosters.get(SYSCalendar.bom(currentDate));
        RosterParameters rosterparameter = rosterparameters.get(SYSCalendar.bom(currentDate));

        ArrayList<Rplan> listRPlans = RPlanTools.getAll(currentDate, roster);

        // does he has a plan for today ?
        DefaultComboBoxModel symbolModel = new DefaultComboBoxModel();
        Rplan myPlan = null;
        for (Rplan rplan : listRPlans) {
            if (rplan.getOwner().equals(user)) {
                myPlan = rplan;
            }

        }



        lblDay.setText(currentDate.toString("EEEE, dd.MM.yyyy"));
        lblYourPlanMsg.setText(OPDE.lang.getString(""));


    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblDay = new JLabel();
        btnBack = new JButton();
        btnFwd = new JButton();
        btnNow = new JButton();
        lblYourPlanMsg = new JLabel();
        lblYourPlan = new JLabel();
        lblFrom2 = new JLabel();
        lblFrom3 = new JLabel();
        cmbSymbol = new JComboBox();
        cmbHome = new JComboBox();
        panel1 = new JPanel();
        rbFillIn = new JRadioButton();
        rbAdditional = new JRadioButton();
        lblFrom = new JLabel();
        lblTo = new JLabel();
        txtFrom = new JTextField();
        txtTo = new JTextField();
        lblText = new JLabel();
        scrollPane1 = new JScrollPane();
        textArea1 = new JTextArea();

        //======== this ========
        setLayout(new FormLayout(
                "3*(default, $lcgap), default",
                "9*(default, $lgap), default:grow, $lgap, default"));

        //---- lblDay ----
        lblDay.setText("Freitag, den 20.12.2013");
        lblDay.setFont(new Font("Arial", Font.PLAIN, 18));
        lblDay.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/encrypted.png")));
        add(lblDay, CC.xy(1, 1));

        //---- btnBack ----
        btnBack.setText(null);
        btnBack.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_rev.png")));
        add(btnBack, CC.xy(3, 1));

        //---- btnFwd ----
        btnFwd.setText(null);
        btnFwd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_fwd.png")));
        add(btnFwd, CC.xy(5, 1));

        //---- btnNow ----
        btnNow.setText(null);
        btnNow.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_end.png")));
        add(btnNow, CC.xy(7, 1));

        //---- lblYourPlanMsg ----
        lblYourPlanMsg.setText("text");
        lblYourPlanMsg.setFont(lblYourPlanMsg.getFont().deriveFont(lblYourPlanMsg.getFont().getStyle() & ~Font.BOLD));
        add(lblYourPlanMsg, CC.xywh(1, 3, 7, 1));

        //---- lblYourPlan ----
        lblYourPlan.setText("text");
        add(lblYourPlan, CC.xywh(1, 5, 7, 1));

        //---- lblFrom2 ----
        lblFrom2.setText("text");
        lblFrom2.setFont(new Font("Arial", Font.BOLD, 9));
        lblFrom2.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblFrom2, CC.xy(1, 7));

        //---- lblFrom3 ----
        lblFrom3.setText("text");
        lblFrom3.setFont(new Font("Arial", Font.BOLD, 9));
        lblFrom3.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblFrom3, CC.xywh(3, 7, 5, 1));
        add(cmbSymbol, CC.xy(1, 9));
        add(cmbHome, CC.xywh(3, 9, 5, 1));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- rbFillIn ----
            rbFillIn.setText("text");
            panel1.add(rbFillIn);

            //---- rbAdditional ----
            rbAdditional.setText("text");
            panel1.add(rbAdditional);
        }
        add(panel1, CC.xywh(1, 11, 7, 1));

        //---- lblFrom ----
        lblFrom.setText("text");
        lblFrom.setFont(new Font("Arial", Font.BOLD, 9));
        lblFrom.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblFrom, CC.xy(1, 13));

        //---- lblTo ----
        lblTo.setText("text");
        lblTo.setFont(new Font("Arial", Font.BOLD, 9));
        lblTo.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblTo, CC.xywh(3, 13, 5, 1));
        add(txtFrom, CC.xy(1, 15));
        add(txtTo, CC.xywh(3, 15, 5, 1));

        //---- lblText ----
        lblText.setText("text");
        lblText.setFont(new Font("Arial", Font.BOLD, 9));
        lblText.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblText, CC.xywh(1, 17, 7, 1));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(textArea1);
        }
        add(scrollPane1, CC.xywh(1, 19, 7, 1, CC.FILL, CC.FILL));

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(rbFillIn);
        buttonGroup1.add(rbAdditional);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblDay;
    private JButton btnBack;
    private JButton btnFwd;
    private JButton btnNow;
    private JLabel lblYourPlanMsg;
    private JLabel lblYourPlan;
    private JLabel lblFrom2;
    private JLabel lblFrom3;
    private JComboBox cmbSymbol;
    private JComboBox cmbHome;
    private JPanel panel1;
    private JRadioButton rbFillIn;
    private JRadioButton rbAdditional;
    private JLabel lblFrom;
    private JLabel lblTo;
    private JTextField txtFrom;
    private JTextField txtTo;
    private JLabel lblText;
    private JScrollPane scrollPane1;
    private JTextArea textArea1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
