/*
 * Created by JFormDesigner on Fri Dec 27 16:16:10 CET 2013
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Homes;
import entity.roster.*;
import op.OPDE;
import op.threads.DisplayManager;
import op.tools.GUITools;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class PnlControllerView extends JPanel {
    public static final String internalClassID = "opde.roster.controllerview";
    private final Rplan rplan;
    private final RosterParameters rosterParameters;
    private final ContractsParameterSet contractsParameterSet;
    private final Symbol effectiveSymbol;
    private WLog timeclock;
    private BigDecimal sum;
    LocalDate currentDate;

    public PnlControllerView(Rplan rplan, RosterParameters rosterParameters, ContractsParameterSet contractsParameterSet) {
        this.rplan = rplan;
        this.rosterParameters = rosterParameters;
        this.contractsParameterSet = contractsParameterSet;
        effectiveSymbol = rosterParameters.getSymbol(rplan.getEffectiveSymbol());
        currentDate = new LocalDate(rplan.getStart());

//        timeclock = RPlanTools.getTimeClock(rplan);

        initComponents();
        initPanel();
    }

    private void initPanel() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.");

        String datetext = sdf.format(rplan.getStart());
        lblDate.setBackground(SYSConst.bluegrey);
        if (currentDate.getDayOfWeek() == DateTimeConstants.SUNDAY || currentDate.getDayOfWeek() == DateTimeConstants.SATURDAY) {
            lblDate.setBackground(SYSConst.pearl);
        }
        if (OPDE.isHoliday(currentDate)) {
            lblDate.setBackground(GUITools.blend(SYSConst.pearl, Color.black, 0.8f));
            datetext += " (" + OPDE.getHoliday(currentDate) + ")";
        }
        lblDate.setText(datetext);

        lblFrom.setText(OPDE.lang.getString("misc.msg.from"));
        lblTo.setText(OPDE.lang.getString("misc.msg.to"));
        lblText.setText(OPDE.lang.getString("misc.msg.comment"));
        lblPlan.setText(OPDE.lang.getString("opde.roster.controllerview.plan"));
        lblTimeClock.setText(OPDE.lang.getString("opde.roster.controllerview.timeclock"));
        lblOverride.setText(OPDE.lang.getString("opde.roster.controllerview.override"));
        lblAdditional.setText(OPDE.lang.getString("opde.roster.controllerview.additional"));

        String plantext = SYSConst.html_bold(effectiveSymbol.getKey().toUpperCase())
                + "&nbsp;<font size=\"-1\">" + rplan.getEffectiveHome().getShortname() + "</font>"
                + "&nbsp;<i><font size=\"-1\">" + effectiveSymbol.getDescription() + "</font></i>";
        btnPlan.setText(SYSTools.toHTMLForScreen(plantext));
        btnPlan.setEnabled(effectiveSymbol.getSymbolType() != Symbol.PVALUE);

        String timeclocktext = timeclock == null ? SYSConst.html_italic(OPDE.lang.getString("opde.roster.controllerview.notimeclock.yet")) : SYSConst.html_bold(timeclock.getActual().toUpperCase())
                + "&nbsp;<font size=\"-1\">" + timeclock.getHomeactual().getShortname() + "</font>"
                + "&nbsp;<i><font size=\"-1\">" + rosterParameters.getSymbol(timeclock.getActual()).getDescription() + "</font></i>";
        btnTimeClock.setText(SYSTools.toHTMLForScreen(timeclocktext));
        btnTimeClock.setEnabled(timeclock != null);

//        updateList();
    }

    private void btnAddActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void btnPlanActionPerformed(ActionEvent e) {
        lblPlanUsed.setIcon(SYSConst.icon22ledGreenOn);
        lblTimeclockUsed.setIcon(SYSConst.icon22ledGreenOff);
        lblOverrideUsed.setIcon(SYSConst.icon22ledGreenOff);

        DateTime from = null;
        DateTime to = null;
        BigDecimal addBD = null;

//        if (effectiveSymbol.getSymbolType() == Symbol.PVALUE) {
//            try {
//                from = new LocalDate(rplan.getStart()).toDateTime(SYSCalendar.parseLocalTime(txtFrom.getText()));
//                to = new LocalDate(rplan.getStart()).toDateTime(SYSCalendar.parseLocalTime(txtTo.getText()));
//            } catch (NumberFormatException e1) {
//                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.wrongtime2"));
//                return;
//            }
//
//            if (from.isAfter(to)) {
//                to = to.plusDays(1);
//            }
//
//            addBD = SYSCalendar.getHoursAsDecimal(from, to);
//            if (txtComment.getText().isEmpty()) {
//                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.emptyentry"));
//                return;
//            }
//        }

        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            Rplan myRplan = em.merge(rplan);
            em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);


//                if (effectiveSymbol.getSymbolType() == Symbol.PVALUE) {
//                    myRplan.getWLogs().add(em.merge(new WLog(addBD, from.toDate(), to.toDate(), myRplan, txtComment.getText().trim(), WorkinglogTools.TYPE_ADDITIONAL)));
//                    myRplan.setStart(from.toDate());
//                    myRplan.setEnd(to.toDate());
//                } else {

            for (WLog WLog : WorkinglogTools.createWorkingLogs(myRplan, rosterParameters.getSymbol(myRplan.getEffectiveSymbol()), contractsParameterSet)) {
                WLog myWLog = em.merge(WLog);
                myRplan.getWLogs().add(myWLog);
            }

            em.getTransaction().commit();

//            updateList();
        } catch (OptimisticLockException ole) {
            OPDE.error(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(ex);
        } finally {
            em.close();

        }
//        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
//            EntityManager em = OPDE.createEM();
//            try {
//                em.getTransaction().begin();
//                Rplan myRplan = em.merge(rplan);
//                em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//                em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);
//                Collection<WLog> logs2remove = new ArrayList<WLog>();
//                for (WLog workinglog : myRplan.getWLogs()) {
//
//                    if (workinglog.isAuto()) {
//                        WLog myWorkinglog = em.merge(workinglog);
//                        em.remove(myWorkinglog);
//                        logs2remove.add(workinglog);
//                    }
//                }
//
//                myRplan.getWLogs().removeAll(logs2remove);
//                // TODO: Fixme
//                //                myRplan.setActual(null);
//                if (effectiveSymbol.getSymbolType() == Symbol.PVALUE) {
//                    myRplan.getWLogs().add(em.merge(new WLog(addBD, from.toDate(), to.toDate(), myRplan, txtComment.getText().trim(), WorkinglogTools.TYPE_ADDITIONAL)));
//                    myRplan.setStart(new DateTime(myRplan.getStart()).toLocalDate().toDateTimeAtStartOfDay().toDate());
//                    myRplan.setEnd(null);
//                }
//
//                em.getTransaction().commit();
//                rplan = myRplan;
//                setButtonState();
//                updateList();
//            } catch (OptimisticLockException ole) {
//                if (em.getTransaction().isActive()) {
//                    em.getTransaction().rollback();
//                }
//                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//            } catch (Exception ex) {
//                if (em.getTransaction().isActive()) {
//                    em.getTransaction().rollback();
//                }
//                OPDE.fatal(ex);
//            } finally {
//                em.close();
//            }
//        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblDate = new JLabel();
        lblPlan = new JLabel();
        lblPlanUsed = new JLabel();
        lblList = new JLabel();
        btnPlan = new JButton();
        lblTimeClock = new JLabel();
        btnTimeClock = new JButton();
        lblOverride = new JLabel();
        panel1 = new JPanel();
        cmbOverride = new JComboBox();
        btnOverride = new JButton();
        lblFrom = new JLabel();
        lblTo = new JLabel();
        txtFrom = new JTextField();
        txtTo = new JTextField();
        scrl = new JScrollPane();
        pnlList = new JPanel();
        lblTimeclockUsed = new JLabel();
        lblOverrideUsed = new JLabel();
        lblAdditional = new JLabel();
        lblText = new JLabel();
        panel2 = new JPanel();
        txTtext = new JTextField();
        btnAdd = new JButton();
        lblSum = new JLabel();

        //======== this ========
        setBorder(new LineBorder(Color.black, 2));
        setLayout(new FormLayout(
            "2*(default:grow, $lcgap), default, $lcgap, default:grow",
            "8*(default, $lgap), default, $nlgap, 21dlu, $lgap, default, $nlgap, default, $lgap, bottom:default:grow"));

        //---- lblDate ----
        lblDate.setText("Mo, 03.06");
        lblDate.setFont(new Font("Arial", Font.BOLD, 16));
        lblDate.setHorizontalAlignment(SwingConstants.CENTER);
        lblDate.setBackground(new Color(204, 204, 255));
        lblDate.setOpaque(true);
        add(lblDate, CC.xywh(1, 1, 7, 1));

        //---- lblPlan ----
        lblPlan.setText("Planung");
        add(lblPlan, CC.xywh(1, 3, 3, 1));

        //---- lblPlanUsed ----
        lblPlanUsed.setText(null);
        lblPlanUsed.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ledoff.png")));
        add(lblPlanUsed, CC.xy(5, 5));

        //---- lblList ----
        lblList.setText("Ergebnis");
        add(lblList, CC.xy(7, 3));

        //---- btnPlan ----
        btnPlan.setText("text");
        btnPlan.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/shetaddrow.png")));
        btnPlan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnPlanActionPerformed(e);
            }
        });
        add(btnPlan, CC.xywh(1, 5, 3, 1));

        //---- lblTimeClock ----
        lblTimeClock.setText("MA Angaben");
        add(lblTimeClock, CC.xywh(1, 7, 3, 1));

        //---- btnTimeClock ----
        btnTimeClock.setText("text");
        btnTimeClock.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/person-red.png")));
        add(btnTimeClock, CC.xywh(1, 9, 3, 1));

        //---- lblOverride ----
        lblOverride.setText("Korrektur");
        add(lblOverride, CC.xywh(1, 11, 3, 1));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));
            panel1.add(cmbOverride);

            //---- btnOverride ----
            btnOverride.setText(null);
            btnOverride.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/graphic-design.png")));
            btnOverride.setFont(new Font("Arial", Font.BOLD, 18));
            btnOverride.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnAddActionPerformed(e);
                }
            });
            panel1.add(btnOverride);
        }
        add(panel1, CC.xywh(1, 13, 3, 1));

        //---- lblFrom ----
        lblFrom.setText("text");
        lblFrom.setFont(new Font("Arial", Font.PLAIN, 11));
        lblFrom.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblFrom, CC.xy(1, 17));

        //---- lblTo ----
        lblTo.setText("text");
        lblTo.setFont(new Font("Arial", Font.PLAIN, 11));
        lblTo.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblTo, CC.xy(3, 17));
        add(txtFrom, CC.xy(1, 19, CC.DEFAULT, CC.FILL));
        add(txtTo, CC.xy(3, 19, CC.DEFAULT, CC.FILL));

        //======== scrl ========
        {

            //======== pnlList ========
            {
                pnlList.setLayout(new VerticalLayout());
            }
            scrl.setViewportView(pnlList);
        }
        add(scrl, CC.xywh(7, 5, 1, 21));

        //---- lblTimeclockUsed ----
        lblTimeclockUsed.setText(null);
        lblTimeclockUsed.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ledoff.png")));
        add(lblTimeclockUsed, CC.xy(5, 9));

        //---- lblOverrideUsed ----
        lblOverrideUsed.setText(null);
        lblOverrideUsed.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ledoff.png")));
        add(lblOverrideUsed, CC.xy(5, 13));

        //---- lblAdditional ----
        lblAdditional.setText("Zus\u00e4tzliches");
        add(lblAdditional, CC.xywh(1, 15, 3, 1));

        //---- lblText ----
        lblText.setText("text");
        lblText.setFont(new Font("Arial", Font.PLAIN, 11));
        lblText.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblText, CC.xywh(1, 21, 3, 1));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
            panel2.add(txTtext);

            //---- btnAdd ----
            btnAdd.setText(null);
            btnAdd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
            btnAdd.setFont(new Font("Arial", Font.BOLD, 18));
            btnAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnAddActionPerformed(e);
                }
            });
            panel2.add(btnAdd);
        }
        add(panel2, CC.xywh(1, 23, 3, 1));

        //---- lblSum ----
        lblSum.setText("text");
        add(lblSum, CC.xywh(1, 25, 3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

//    void updateList() {
//
//        pnlList.removeAll();
//        sum = BigDecimal.ZERO;
//
//        if (!rplan.getWLogs().isEmpty()) {
//            pnlList.setBackground(Color.WHITE);
//        }
//        pnlList.setOpaque(!rplan.getWLogs().isEmpty());
//
//        String actual = null;
//        Homes homeactual = null;
//
//        ArrayList<WLog> listActual = new ArrayList<WLog>();
//        ArrayList<WLog> listAdditional = new ArrayList<WLog>();
//
//        // 1. analyze the existing workinglogs
//        for (WLog WLog : rplan.getWLogs()) {
//            if (actual == null && WLog.getActual() != null) {
//                actual = WLog.getActual();
//                homeactual = WLog.getHomeactual();
//            }
//            if (WLog.isAuto()) {
//                listActual.add(WLog);
//                sum = sum.add(WLog.getHours());
//            } else if (!WLog.isTimeClock()) {
//                listAdditional.add(WLog);
//                sum = sum.add(WLog.getHours());
//            } else {
//                timeclock = WLog;
//            }
//        }
//
//        // 2. build the list for the actual shift
//        if (!listActual.isEmpty()) {
//
//            JPanel pnlActual = new JPanel();
//            pnlActual.setLayout(new BorderLayout());
//            pnlActual.setAlignmentY(TOP_ALIGNMENT);
//
//            DefaultMutableTreeNode root = new DefaultMutableTreeNode(actual.toUpperCase() + ": " + sum.setScale(2, RoundingMode.HALF_UP));
//            for (WLog WLog : listActual) {
//                root.add(new DefaultMutableTreeNode(WLog.getHours().setScale(2, RoundingMode.HALF_UP) + " [" + WorkinglogTools.TYPES[WLog.getType()] + "]"));
//            }
//
//
//            JTree tree = new JTree(root);
//            tree.setCellRenderer(new DefaultTreeCellRenderer() {
//                @Override
//                public Component getTreeCellRendererComponent(JTree tree,
//                                                              Object value, boolean selected, boolean expanded,
//                                                              boolean isLeaf, int row, boolean focused) {
//                    Component c = super.getTreeCellRendererComponent(tree, value,
//                            selected, expanded, isLeaf, row, focused);
//                    if (((DefaultMutableTreeNode) value).isRoot()) {
//                        setIcon(SYSConst.icon22addrow);
//                    }
//
//                    return c;
//                }
//            });
//            tree.setShowsRootHandles(true);
//            SYSTools.collapseAll(tree);
//
//            pnlActual.add(tree, BorderLayout.CENTER);
////                    pnlActual.add(pnlButton, BorderLayout.EAST);
//            pnlActual.setAlignmentY(TOP_ALIGNMENT);
//            pnlList.add(pnlActual);
//
//        }
//
//        // 3. build the list for the rest (without the timeclock)
//        if (!listAdditional.isEmpty()) {
//
//            JPanel pnlAdditional = new JPanel();
//            pnlAdditional.setLayout(new BorderLayout());
//            pnlAdditional.setAlignmentY(TOP_ALIGNMENT);
//
//
//            for (WLog WLog : listAdditional) {
//                DefaultMutableTreeNode root = new DefaultMutableTreeNode(WLog.getHours().setScale(2, RoundingMode.HALF_UP).toString() + SYSTools.catchNull(WLog.getText(), " (", ")"));
//
//                root.add(new DefaultMutableTreeNode(OPDE.lang.getString("misc.msg.from") + ": " + new LocalTime(WLog.getStart()).toString("HH:mm")));
//                root.add(new DefaultMutableTreeNode(OPDE.lang.getString("misc.msg.to") + ": " + new LocalTime(WLog.getEnd()).toString("HH:mm")));
//                root.add(new DefaultMutableTreeNode(WLog.getHours().setScale(2, RoundingMode.HALF_UP) + " [" + WorkinglogTools.TYPES[WLog.getType()] + "]"));
//
//                JTree tree = new JTree(root);
//                tree.setShowsRootHandles(true);
//                SYSTools.collapseAll(tree);
//
//                pnlAdditional.add(tree, BorderLayout.CENTER);
//                //                    pnlActual.add(pnlButton, BorderLayout.EAST);
//                pnlAdditional.setAlignmentY(TOP_ALIGNMENT);
//
//                tree.setCellRenderer(new DefaultTreeCellRenderer() {
//                    @Override
//                    public Component getTreeCellRendererComponent(JTree tree,
//                                                                  Object value, boolean selected, boolean expanded,
//                                                                  boolean isLeaf, int row, boolean focused) {
//                        Component c = super.getTreeCellRendererComponent(tree, value,
//                                selected, expanded, isLeaf, row, focused);
//                        if (((DefaultMutableTreeNode) value).isRoot()) {
//                            setIcon(SYSConst.icon22add);
//                        }
//                        return c;
//                    }
//                });
//
//            }
//            pnlList.add(pnlAdditional);
//        }
//
//
//        lblTimeclockUsed.setIcon(timeclock != null && timeclock.getState() == WorkinglogTools.STATE_ACCEPTED ? SYSConst.icon22ledGreenOn : SYSConst.icon22ledGreenOff);
//        lblPlanUsed.setIcon(listActual.isEmpty() && (timeclock == null || timeclock.getState() != WorkinglogTools.STATE_ACCEPTED) ? SYSConst.icon22ledGreenOff : SYSConst.icon22ledGreenOn);
//        lblOverrideUsed.setIcon(SYSConst.icon22ledGreenOff);
//
//        lblSum.setText(OPDE.lang.getString("misc.msg.sum") + ": " + sum.setScale(2, RoundingMode.HALF_UP).toString());
//
//        scrl.validate();
//        scrl.repaint();
//    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblDate;
    private JLabel lblPlan;
    private JLabel lblPlanUsed;
    private JLabel lblList;
    private JButton btnPlan;
    private JLabel lblTimeClock;
    private JButton btnTimeClock;
    private JLabel lblOverride;
    private JPanel panel1;
    private JComboBox cmbOverride;
    private JButton btnOverride;
    private JLabel lblFrom;
    private JLabel lblTo;
    private JTextField txtFrom;
    private JTextField txtTo;
    private JScrollPane scrl;
    private JPanel pnlList;
    private JLabel lblTimeclockUsed;
    private JLabel lblOverrideUsed;
    private JLabel lblAdditional;
    private JLabel lblText;
    private JPanel panel2;
    private JTextField txTtext;
    private JButton btnAdd;
    private JLabel lblSum;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
