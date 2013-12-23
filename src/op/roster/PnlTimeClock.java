/*
 * Created by JFormDesigner on Fri Dec 20 11:54:13 CET 2013
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.HomesTools;
import entity.StationTools;
import entity.roster.*;
import entity.system.Users;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Torsten Löhr
 */
public class PnlTimeClock extends JPanel {

    private final int SECTION = RostersTools.SECTION_CARE;
    private Rplan myPlan;
    private LocalDate currentDate;
    private Users user;
    private Workinglog timeclock;
    private HashMap<LocalDate, Rosters> rosters;
    private HashMap<LocalDate, RosterParameters> rosterparameters;
    private Interval minmax;
    public static final String internalClassID = "dlglogin.timeclock";


    public PnlTimeClock(Users user) {
        this.user = user;
        currentDate = new LocalDate();
        minmax = RostersTools.getMinMax(SECTION);

        if (!minmax.contains(currentDate.toDateTimeAtCurrentTime())) {
            currentDate = minmax.getEnd().toLocalDate();
        }


        rosters = new HashMap<LocalDate, Rosters>();
        rosterparameters = new HashMap<LocalDate, RosterParameters>();

        initComponents();
        HomesTools.setComboBox(cmbHome);
        cmbHome.setSelectedIndex(-1);
        cmbHome.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbHomeItemStateChanged(e);
            }
        });

        cmbSymbol.setRenderer(getCMBRenderer());

        lblFrom.setText(OPDE.lang.getString("misc.msg.from"));
        lblTo.setText(OPDE.lang.getString("misc.msg.to"));
        lblActual.setText(OPDE.lang.getString("dlglogin.timeclock.actually"));
        lblHome.setText(OPDE.lang.getString("dlglogin.timeclock.home"));
        lblText.setText(OPDE.lang.getString("misc.msg.comment"));
        initPanel();
    }

    private void initPanel() {

        lblDay.setText(currentDate.toString("EEEE, dd.MM.yyyy"));
        if (!rosters.containsKey(SYSCalendar.bom(currentDate))) {
            rosters.put(SYSCalendar.bom(currentDate), RostersTools.get(currentDate, SECTION));
            rosterparameters.put(SYSCalendar.bom(currentDate), RostersTools.getParameters(rosters.get(SYSCalendar.bom(currentDate))));
        }

        Rosters roster = rosters.get(SYSCalendar.bom(currentDate));
        RosterParameters rosterparameter = rosterparameters.get(SYSCalendar.bom(currentDate));

        ArrayList<Rplan> listRPlans = RPlanTools.getAll(currentDate, roster);
        // does he have a plan for today ?
        myPlan = null;
        for (Rplan rplan : listRPlans) {
            if (rplan.getOwner().equals(user)) {
                myPlan = rplan;
                break;
            }
        }


        String message = "";
        if (myPlan == null || rosterparameter.getSymbol(myPlan.getEffectiveSymbol()).getSymbolType() != Symbol.WORK) {
            cmbHome.setSelectedItem(StationTools.getStationForThisHost().getHome());
            if (currentDate.isEqual(new LocalDate())) {
                message = "dlglogin.timeclock.you.are.not.supposed.to.be.here";
            } else {
                message = "dlglogin.timeclock.you.are.not.planned.for.date";
            }
            lblYourPlan.setText("");
            lblDay.setIcon(null);

        } else {
            lblYourPlan.setText(myPlan.getEffectiveSymbol());
            lblDay.setIcon(myPlan.isLocked() ? SYSConst.icon22encrypted : null);
            cmbHome.setSelectedItem(myPlan.getEffectiveHome());
            if (currentDate.isEqual(new LocalDate())) {
                message = "dlglogin.timeclock.your.plan.for.today";
            } else {
                message = "dlglogin.timeclock.your.plan.for.date";
            }
        }

        lblYourPlanMsg.setText(OPDE.lang.getString(message));

        timeclock = RPlanTools.getTimeClock(myPlan);

        txtComment.setText(timeclock != null ? timeclock.getText() : null);
        txtFrom.setText(timeclock != null ? DateFormat.getTimeInstance(DateFormat.SHORT).format(timeclock.getStart()) : null);
        txtTo.setText(timeclock != null ? DateFormat.getTimeInstance(DateFormat.SHORT).format(timeclock.getEnd()) : null);

        wirklich ?
        txtFrom.setEnabled(myPlan == null || !myPlan.isLocked());
        txtTo.setEnabled(myPlan == null || !myPlan.isLocked());
        txtComment.setEnabled(myPlan == null || !myPlan.isLocked());
        cmbHome.setEnabled(myPlan == null || !myPlan.isLocked());
        cmbSymbol.setEnabled(myPlan == null || !myPlan.isLocked());


    }

    private void btnBackActionPerformed(ActionEvent e) {
        if (minmax.contains(currentDate.minusDays(1).toDateTimeAtCurrentTime())) {
            currentDate = currentDate.minusDays(1);
        }
        initPanel();
    }

    private void btnFwdActionPerformed(ActionEvent e) {
        if (minmax.contains(currentDate.plusDays(1).toDateTimeAtCurrentTime())) {
            currentDate = currentDate.plusDays(1);
        }
        initPanel();
    }

    private void btnNowActionPerformed(ActionEvent e) {
        if (minmax.contains(new DateTime())) {
            currentDate = new LocalDate();
        } else {
            currentDate = new LocalDate(minmax.getEnd());
        }
        initPanel();
    }

    private ListCellRenderer getCMBRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTMLForScreen("<i>Keine Auswahl</i>");
                } else if (o instanceof Rplan) {
                    Rplan rplan = (Rplan) o;
                    RosterParameters rosterparameter = rosterparameters.get(SYSCalendar.bom(currentDate));
                    Symbol symbol = rosterparameter.getSymbol(rplan.getEffectiveSymbol());
                    text = SYSConst.html_bold(symbol.getKey().toUpperCase()) + " " + SYSConst.html_italic(symbol.getDescription()) + ", ";
                    text += (rplan.getOwner().equals(OPDE.getLogin().getUser()) ? "(mein geplanter Dienst)" : rplan.getOwner().getFullname() + " (" + OPDE.lang.getString("dlglogin.timeclock.taken.over") + ")");
                    text = SYSTools.toHTMLForScreen(text);
                } else if (o instanceof Symbol) {
                    Symbol symbol = (Symbol) o;
                    text = SYSConst.html_bold(symbol.getKey().toUpperCase()) + " " + SYSConst.html_italic(symbol.getDescription()) + ", (" + OPDE.lang.getString("dlglogin.timeclock.filled.in") + ")";
                    text = SYSTools.toHTMLForScreen(text);
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, b, b1);
            }
        };
    }

    private void cmbHomeItemStateChanged(ItemEvent e) {

        if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null) {
            DefaultComboBoxModel symbolModel = new DefaultComboBoxModel();

            if (myPlan != null) {
                symbolModel.addElement(myPlan);
            } else {

                ArrayList<Rplan> listRPlans = RPlanTools.getAll(currentDate, rosters.get(SYSCalendar.bom(currentDate)));
                RosterParameters rosterparameter = rosterparameters.get(SYSCalendar.bom(currentDate));

                for (Rplan rplan : listRPlans) {
                    if (rplan.getEffectiveHome().equals(e.getItem()) && rosterparameter.getSymbol(rplan.getEffectiveSymbol()).getSymbolType() == Symbol.WORK) {
                        symbolModel.addElement(rplan);
                    }
                }

                for (Symbol symbol : rosterparameter.getSymbolMap().values()) {
                    if (symbol.getSymbolType() == Symbol.WORK) {
                        symbolModel.addElement(symbol);
                    }
                }
            }

            cmbSymbol.setModel(symbolModel);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    revalidate();
                    repaint();
                }
            });
        }
    }

    private void txtFromFocusLost(FocusEvent e) {
        LocalTime time = null;
        try {
            time = SYSCalendar.parseLocalTime(txtFrom.getText());
        } catch (NumberFormatException nfe) {
            //fuckit !!
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel2 = new JPanel();
        lblDay = new JLabel();
        btnBack = new JButton();
        btnFwd = new JButton();
        btnNow = new JButton();
        lblYourPlanMsg = new JLabel();
        lblYourPlan = new JLabel();
        lblActual = new JLabel();
        lblHome = new JLabel();
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
        txtComment = new JTextArea();

        //======== this ========
        setBorder(new EmptyBorder(15, 5, 15, 15));
        setLayout(new BorderLayout());

        //======== panel2 ========
        {
            panel2.setLayout(new FormLayout(
                    "default:grow, 3*($lcgap, default)",
                    "9*(default, $lgap), default:grow, $lgap, default"));

            //---- lblDay ----
            lblDay.setText("Freitag, den 20.12.2013");
            lblDay.setFont(new Font("Arial", Font.PLAIN, 18));
            lblDay.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/encrypted.png")));
            panel2.add(lblDay, CC.xy(1, 1));

            //---- btnBack ----
            btnBack.setText(null);
            btnBack.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_rev.png")));
            btnBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnBackActionPerformed(e);
                }
            });
            panel2.add(btnBack, CC.xy(3, 1));

            //---- btnFwd ----
            btnFwd.setText(null);
            btnFwd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_fwd.png")));
            btnFwd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnFwdActionPerformed(e);
                }
            });
            panel2.add(btnFwd, CC.xy(5, 1));

            //---- btnNow ----
            btnNow.setText(null);
            btnNow.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_end.png")));
            btnNow.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnNowActionPerformed(e);
                }
            });
            panel2.add(btnNow, CC.xy(7, 1));

            //---- lblYourPlanMsg ----
            lblYourPlanMsg.setText("text");
            lblYourPlanMsg.setFont(new Font("Arial", Font.PLAIN, 16));
            panel2.add(lblYourPlanMsg, CC.xywh(1, 3, 7, 1));

            //---- lblYourPlan ----
            lblYourPlan.setText("text");
            panel2.add(lblYourPlan, CC.xywh(1, 5, 7, 1));

            //---- lblActual ----
            lblActual.setText("text");
            lblActual.setFont(new Font("Arial", Font.BOLD, 9));
            lblActual.setHorizontalAlignment(SwingConstants.TRAILING);
            panel2.add(lblActual, CC.xy(1, 7));

            //---- lblHome ----
            lblHome.setText("text");
            lblHome.setFont(new Font("Arial", Font.BOLD, 9));
            lblHome.setHorizontalAlignment(SwingConstants.TRAILING);
            panel2.add(lblHome, CC.xywh(3, 7, 5, 1));
            panel2.add(cmbSymbol, CC.xy(1, 9));
            panel2.add(cmbHome, CC.xywh(3, 9, 5, 1));

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
            panel2.add(panel1, CC.xywh(1, 11, 7, 1));

            //---- lblFrom ----
            lblFrom.setText("text");
            lblFrom.setFont(new Font("Arial", Font.BOLD, 9));
            lblFrom.setHorizontalAlignment(SwingConstants.TRAILING);
            panel2.add(lblFrom, CC.xy(1, 13));

            //---- lblTo ----
            lblTo.setText("text");
            lblTo.setFont(new Font("Arial", Font.BOLD, 9));
            lblTo.setHorizontalAlignment(SwingConstants.TRAILING);
            panel2.add(lblTo, CC.xywh(3, 13, 5, 1));

            //---- txtFrom ----
            txtFrom.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtFromFocusLost(e);
                }
            });
            panel2.add(txtFrom, CC.xy(1, 15));
            panel2.add(txtTo, CC.xywh(3, 15, 5, 1));

            //---- lblText ----
            lblText.setText("text");
            lblText.setFont(new Font("Arial", Font.BOLD, 9));
            lblText.setHorizontalAlignment(SwingConstants.TRAILING);
            panel2.add(lblText, CC.xywh(1, 17, 7, 1));

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(txtComment);
            }
            panel2.add(scrollPane1, CC.xywh(1, 19, 7, 1, CC.FILL, CC.FILL));
        }
        add(panel2, BorderLayout.CENTER);

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(rbFillIn);
        buttonGroup1.add(rbAdditional);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel2;
    private JLabel lblDay;
    private JButton btnBack;
    private JButton btnFwd;
    private JButton btnNow;
    private JLabel lblYourPlanMsg;
    private JLabel lblYourPlan;
    private JLabel lblActual;
    private JLabel lblHome;
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
    private JTextArea txtComment;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
