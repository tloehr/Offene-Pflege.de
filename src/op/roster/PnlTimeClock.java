/*
 * Created by JFormDesigner on Fri Dec 20 11:54:13 CET 2013
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Homes;
import entity.HomesTools;
import entity.StationTools;
import entity.roster.*;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.Pair;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.util.*;

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
    private boolean changed = false;

    public PnlTimeClock(Users u) {
        this.user = u;
        currentDate = new LocalDate();
        minmax = RostersTools.getMinMax(SECTION);

        if (!minmax.contains(currentDate.toDateTimeAtCurrentTime())) {
            currentDate = minmax.getEnd().toLocalDate();
        }

        rosters = new HashMap<LocalDate, Rosters>();
        rosterparameters = new HashMap<LocalDate, RosterParameters>();

        initComponents();
//        HomesTools.setComboBox(cmbHome);
//        cmbHome.setSelectedIndex(-1);
//        cmbHome.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                cmbHomeItemStateChanged(e);
//            }
//        });

        cmbSymbol.setRenderer(getSymbolRenderer());

        btnSave.setToolTipText(OPDE.lang.getString("misc.msg.save"));
        btnNowFrom.setToolTipText(OPDE.lang.getString("misc.msg.now"));
        btnNowTo.setToolTipText(OPDE.lang.getString("misc.msg.now"));

        lblFrom.setText(OPDE.lang.getString("misc.msg.from"));
        lblTo.setText(OPDE.lang.getString("misc.msg.to"));
        lblActual.setText(OPDE.lang.getString("dlglogin.timeclock.actually"));
//        lblHome.setText(OPDE.lang.getString("dlglogin.timeclock.home"));
        lblText.setText(OPDE.lang.getString("misc.msg.comment"));

        // TODO: remove after development
        cmbUser.setModel(new DefaultComboBoxModel(new Vector<Users>(UsersTools.getUsers(true))));
        cmbUser.setSelectedItem(user);
        cmbUser.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    user = (Users) e.getItem();
                    initPanel();
                }
            }
        });

        initPanel();
    }

    private void initPanel() {
        lblSaved.setText(null);
        btnSave.setIcon(SYSConst.icon22usbpen);


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

        if (myPlan == null) { // this should never happen. fix this in the roster creation and the transition of the roster states
            // no rplan ? create an nearly empty one.
            myPlan = new Rplan(roster, StationTools.getStationForThisHost().getHome(), currentDate.toDate(), user);

            // todo: this is just a dirty trick. Fix this by creating a proper way to find an appropriate OFF_DUTY symbol
            myPlan.setP1("X");
            myPlan.setStartEndFromSymbol(rosterparameter.getSymbolMap().get("X"));
        }

        String message = "";
        if (rosterparameter.getSymbol(myPlan.getEffectiveSymbol()).getSymbolType() != Symbol.WORK) {

            if (currentDate.isEqual(new LocalDate())) {
                message = "dlglogin.timeclock.you.are.not.supposed.to.be.here";
            } else {
                message = "dlglogin.timeclock.you.are.not.planned.for.date";
            }

            lblDay.setIcon(null);
        } else {
            lblDay.setIcon(myPlan.isLocked() ? SYSConst.icon22encrypted : null);

            if (currentDate.isEqual(new LocalDate())) {
                message = "dlglogin.timeclock.your.plan.for.today";
            } else {
                message = "dlglogin.timeclock.your.plan.for.date";
            }
        }

        lblSaved.setText("");


        DefaultComboBoxModel symbolModel = new DefaultComboBoxModel();

        int select;
        if (rosterparameters.get(SYSCalendar.bom(currentDate)).getSymbol(myPlan.getEffectiveSymbol()).getSymbolType() == Symbol.WORK) {
            symbolModel.addElement(myPlan);
            select = 0;
        } else {

            Collections.sort(listRPlans, new Comparator<Rplan>() {
                @Override
                public int compare(Rplan o1, Rplan o2) {
                    return o1.getEffectiveHome().compareTo(o2.getEffectiveHome());
                }
            });

            for (Rplan rplan : listRPlans) {
                if (rosterparameter.getSymbol(rplan.getEffectiveSymbol()).getSymbolType() == Symbol.WORK) { // rplan.getEffectiveHome().equals(e.getItem()) &&
                    symbolModel.addElement(rplan);
                }
            }

            for (Homes home : HomesTools.getAll()) {
                for (Symbol symbol : rosterparameter.getSymbolMap().values()) {
                    if (symbol.getSymbolType() == Symbol.WORK) {
                        symbolModel.addElement(new Pair<Homes, Symbol>(home, symbol));
                    }
                }
            }
            select = -1;
        }
        cmbSymbol.setModel(symbolModel);
        cmbSymbol.setSelectedIndex(select);

        lblYourPlanMsg.setText(OPDE.lang.getString(message));

        timeclock = RPlanTools.getTimeClock(myPlan);
        if (timeclock == null) {
            timeclock = new Workinglog(myPlan);
            myPlan.getWorkinglogs().add(timeclock);
        }

        txtComment.setText(SYSTools.catchNull(timeclock.getText()));
        txtFrom.setText(!SYSTools.catchNull(timeclock.getStart()).isEmpty() ? DateFormat.getTimeInstance(DateFormat.SHORT).format(timeclock.getStart()) : null);
        txtTo.setText(!SYSTools.catchNull(timeclock.getEnd()).isEmpty() ? DateFormat.getTimeInstance(DateFormat.SHORT).format(timeclock.getEnd()) : null);

// todo: wirklich ? später. kann ich noch nicht beurteilen!
//        txtFrom.setEnabled(myPlan == null || !myPlan.isLocked());
//        txtTo.setEnabled(myPlan == null || !myPlan.isLocked());
//        txtComment.setEnabled(myPlan == null || !myPlan.isLocked());
//        cmbHome.setEnabled(myPlan == null || !myPlan.isLocked());
//        cmbSymbol.setEnabled(myPlan == null || !myPlan.isLocked());


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

    private ListCellRenderer getSymbolRenderer() {
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
                    text = SYSConst.html_bold(symbol.getKey().toUpperCase()) + " " + SYSConst.html_italic(symbol.getDescription()) + ", " + rplan.getEffectiveHome().getShortname() + ", ";
                    text += (rplan.getOwner().equals(user) ? "(mein geplanter Dienst)" : rplan.getOwner().getFullname() + " (" + OPDE.lang.getString("dlglogin.timeclock.taken.over") + ")");
                    text = SYSTools.toHTMLForScreen(text);
                } else if (o instanceof Pair) {
                    Pair pair = (Pair) o;
                    Symbol symbol = (Symbol) pair.getSecond();
                    Homes home = (Homes) pair.getFirst();
                    text = SYSConst.html_bold(symbol.getKey().toUpperCase()) + " " + SYSConst.html_italic(symbol.getDescription()) + ", " + home.getShortname() + ", (" + OPDE.lang.getString("dlglogin.timeclock.filled.in") + ")";
                    text = SYSTools.toHTMLForScreen(text);
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, b, b1);
            }
        };
    }

//    private void cmbHomeItemStateChanged(ItemEvent e) {
//
//        if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null) {
//            DefaultComboBoxModel symbolModel = new DefaultComboBoxModel();
//
//            if (rosterparameters.get(SYSCalendar.bom(currentDate)).getSymbol(myPlan.getEffectiveSymbol()).getSymbolType() == Symbol.WORK) {
//                symbolModel.addElement(myPlan);
//            } else {
//
//
//                ArrayList<Rplan> listRPlans = RPlanTools.getAll(currentDate, rosters.get(SYSCalendar.bom(currentDate)));
//                RosterParameters rosterparameter = rosterparameters.get(SYSCalendar.bom(currentDate));
//
//                Collections.sort(listRPlans, new Comparator<Rplan>() {
//                    @Override
//                    public int compare(Rplan o1, Rplan o2) {
//                        return o1.getEffectiveHome().compareTo(o2.getEffectiveHome());
//                    }
//                });
//
//                for (Rplan rplan : listRPlans) {
//                    if (rosterparameter.getSymbol(rplan.getEffectiveSymbol()).getSymbolType() == Symbol.WORK) { // rplan.getEffectiveHome().equals(e.getItem()) &&
//                        symbolModel.addElement(rplan);
//                    }
//                }
//
//                for (Homes home : HomesTools.getAll()) {
//                    for (Symbol symbol : rosterparameter.getSymbolMap().values()) {
//                        if (symbol.getSymbolType() == Symbol.WORK) {
//                            symbolModel.addElement(new Pair<Homes, Symbol>(home, symbol));
//                        }
//                    }
//                }
//            }
//            cmbSymbol.setModel(symbolModel);
//            setShift();
//
//            SwingUtilities.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    revalidate();
//                    repaint();
//                }
//            });
//        }
//    }

    private void txtFromFocusLost(FocusEvent e) {
        LocalTime time;
        try {
            time = SYSCalendar.parseLocalTime(txtFrom.getText());

            if (time.isAfter(new LocalTime())) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.futuretime", DisplayMessage.WARNING));
                time = new LocalTime();
            }

            txtFrom.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(time.toDateTimeToday().toDate()));
            txtFrom.setBackground(Color.WHITE);
            timeclock.setStart(time.toDateTimeToday().toDate());
        } catch (NumberFormatException nfe) {
            time = null;
            txtFrom.setText(null);
            txtFrom.setBackground(SYSConst.pearl);
            timeclock.setStart(null);
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(nfe.getMessage(), DisplayMessage.WARNING));
        }
        set4Changed(true);
    }

    private void setShift(String symbol, Homes home) {
        timeclock.setActual(symbol);
        timeclock.setHomeactual(home);
//        if (myPlan.getP1() != null) {
//            myPlan.setP2(symbol);
//            myPlan.setHome2(home);
//        } else {
//            myPlan.setP1(symbol);
//            myPlan.setHome1(home);
//        }
//
//        myPlan.setStartEndFromSymbol(rosterparameters.get(SYSCalendar.bom(currentDate)).getSymbol(symbol));
    }

    private void cmbSymbolItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            set4Changed(true);
            if (e.getItem() instanceof Rplan) {
                Rplan rplan = (Rplan) e.getItem();
//                RosterParameters rosterparameter = rosterparameters.get(SYSCalendar.bom(currentDate));
//                Symbol symbol = rosterparameter.getSymbol(rplan.getEffectiveSymbol());
                setShift(rplan.getEffectiveSymbol(), rplan.getEffectiveHome());
            } else if (e.getItem() instanceof Pair) {
                Pair pair = (Pair) e.getItem();
                Symbol symbol = (Symbol) pair.getSecond();
                Homes home = (Homes) pair.getFirst();
                setShift(symbol.getKey(), home);
            }
        }
    }

    private void txtToFocusLost(FocusEvent e) {
        LocalTime time;
        try {
            time = SYSCalendar.parseLocalTime(txtTo.getText());

            if (time.isAfter(new LocalTime())) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.futuretime", DisplayMessage.WARNING));
                time = new LocalTime();
            }

            txtTo.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(time.toDateTimeToday().toDate()));
            txtTo.setBackground(Color.WHITE);
            timeclock.setEnd(time.toDateTimeToday().toDate());
        } catch (NumberFormatException nfe) {
            time = null;
            txtTo.setText(null);
            txtTo.setBackground(SYSConst.pearl);
            timeclock.setEnd(null);
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(nfe.getMessage(), DisplayMessage.WARNING));
        }
        set4Changed(true);
    }

    private void btnNowFromActionPerformed(ActionEvent e) {
        txtFrom.setText(new LocalTime().toString("HH:mm"));
        txtFromFocusLost(null);
    }

    private void btnNowToActionPerformed(ActionEvent e) {
        txtTo.setText(new LocalTime().toString("HH:mm"));
        txtToFocusLost(null);
    }

    private void txtCommentCaretUpdate(CaretEvent e) {
        timeclock.setText(txtComment.getText());
        set4Changed(true);
    }

    private void btnSaveActionPerformed(ActionEvent ae) {
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();

            Users myUser = em.merge(user);
            em.lock(myUser, LockModeType.OPTIMISTIC);

            Rplan thisPlan = em.merge(myPlan);
            em.lock(thisPlan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            Workinglog myTimeclock = em.merge(timeclock);
            em.lock(myTimeclock, LockModeType.OPTIMISTIC);

            em.getTransaction().commit();
        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(e);
        } finally {
            em.close();
        }

        set4Changed(false);
    }

    private void set4Changed(boolean change) {
        changed = change;
        lblSaved.setText(changed ? "Änderungen noch nicht gespeichert." : null);
        btnSave.setIcon(changed ? SYSConst.icon22usbpenred : SYSConst.icon22usbpengreen);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel2 = new JPanel();
        lblDay = new JLabel();
        btnBack = new JButton();
        btnFwd = new JButton();
        btnNow = new JButton();
        lblYourPlanMsg = new JLabel();
        lblActual = new JLabel();
        cmbSymbol = new JComboBox();
        lblFrom = new JLabel();
        lblTo = new JLabel();
        panel1 = new JPanel();
        txtFrom = new JTextField();
        btnNowFrom = new JButton();
        panel3 = new JPanel();
        txtTo = new JTextField();
        btnNowTo = new JButton();
        lblText = new JLabel();
        scrollPane1 = new JScrollPane();
        txtComment = new JTextArea();
        cmbUser = new JComboBox();
        btnSave = new JButton();
        lblSaved = new JLabel();

        //======== this ========
        setBorder(new EmptyBorder(15, 5, 15, 15));
        setLayout(new BorderLayout());

        //======== panel2 ========
        {
            panel2.setLayout(new FormLayout(
                "default:grow, 3*($lcgap, default)",
                "7*(default, $lgap), default:grow, 2*($lgap, default)"));

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

            //---- lblActual ----
            lblActual.setText("text");
            lblActual.setFont(new Font("Arial", Font.PLAIN, 11));
            lblActual.setHorizontalAlignment(SwingConstants.TRAILING);
            panel2.add(lblActual, CC.xywh(1, 5, 7, 1));

            //---- cmbSymbol ----
            cmbSymbol.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cmbSymbolItemStateChanged(e);
                }
            });
            panel2.add(cmbSymbol, CC.xywh(1, 7, 7, 1));

            //---- lblFrom ----
            lblFrom.setText("text");
            lblFrom.setFont(new Font("Arial", Font.PLAIN, 11));
            lblFrom.setHorizontalAlignment(SwingConstants.TRAILING);
            panel2.add(lblFrom, CC.xy(1, 9));

            //---- lblTo ----
            lblTo.setText("text");
            lblTo.setFont(new Font("Arial", Font.PLAIN, 11));
            lblTo.setHorizontalAlignment(SwingConstants.TRAILING);
            panel2.add(lblTo, CC.xywh(3, 9, 5, 1));

            //======== panel1 ========
            {
                panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

                //---- txtFrom ----
                txtFrom.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtFromFocusLost(e);
                    }
                });
                panel1.add(txtFrom);

                //---- btnNowFrom ----
                btnNowFrom.setText(null);
                btnNowFrom.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/clock.png")));
                btnNowFrom.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnNowFromActionPerformed(e);
                    }
                });
                panel1.add(btnNowFrom);
            }
            panel2.add(panel1, CC.xy(1, 11));

            //======== panel3 ========
            {
                panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

                //---- txtTo ----
                txtTo.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtToFocusLost(e);
                    }
                });
                panel3.add(txtTo);

                //---- btnNowTo ----
                btnNowTo.setText(null);
                btnNowTo.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/clock.png")));
                btnNowTo.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnNowToActionPerformed(e);
                    }
                });
                panel3.add(btnNowTo);
            }
            panel2.add(panel3, CC.xywh(3, 11, 5, 1));

            //---- lblText ----
            lblText.setText("text");
            lblText.setFont(new Font("Arial", Font.PLAIN, 11));
            lblText.setHorizontalAlignment(SwingConstants.TRAILING);
            panel2.add(lblText, CC.xywh(1, 13, 7, 1));

            //======== scrollPane1 ========
            {

                //---- txtComment ----
                txtComment.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtCommentCaretUpdate(e);
                    }
                });
                scrollPane1.setViewportView(txtComment);
            }
            panel2.add(scrollPane1, CC.xywh(1, 15, 7, 1, CC.FILL, CC.FILL));
            panel2.add(cmbUser, CC.xywh(1, 17, 5, 1));

            //---- btnSave ----
            btnSave.setText(null);
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/usbpen.png")));
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });
            panel2.add(btnSave, CC.xy(7, 17));

            //---- lblSaved ----
            lblSaved.setText(null);
            lblSaved.setHorizontalAlignment(SwingConstants.CENTER);
            lblSaved.setForeground(Color.red);
            panel2.add(lblSaved, CC.xywh(1, 19, 7, 1));
        }
        add(panel2, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel2;
    private JLabel lblDay;
    private JButton btnBack;
    private JButton btnFwd;
    private JButton btnNow;
    private JLabel lblYourPlanMsg;
    private JLabel lblActual;
    private JComboBox cmbSymbol;
    private JLabel lblFrom;
    private JLabel lblTo;
    private JPanel panel1;
    private JTextField txtFrom;
    private JButton btnNowFrom;
    private JPanel panel3;
    private JTextField txtTo;
    private JButton btnNowTo;
    private JLabel lblText;
    private JScrollPane scrollPane1;
    private JTextArea txtComment;
    private JComboBox cmbUser;
    private JButton btnSave;
    private JLabel lblSaved;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
