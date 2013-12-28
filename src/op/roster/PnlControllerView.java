/*
 * Created by JFormDesigner on Fri Dec 27 16:16:10 CET 2013
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.roster.*;
import op.OPDE;
import op.threads.DisplayManager;
import op.tools.GUITools;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * @author Torsten Löhr
 */
public class PnlControllerView extends JPanel {
    public static final String internalClassID = "opde.roster.controllerview";
    private final Rplan rplan;
    private final RosterParameters rosterParameters;
    private final ContractsParameterSet contractsParameterSet;
    private final Symbol effectiveSymbol;
    private final Workinglog timeclock;
    private BigDecimal sum;
    LocalDate currentDate;

    public PnlControllerView(Rplan rplan, RosterParameters rosterParameters, ContractsParameterSet contractsParameterSet) {
        this.rplan = rplan;
        this.rosterParameters = rosterParameters;
        this.contractsParameterSet = contractsParameterSet;
        effectiveSymbol = rosterParameters.getSymbol(rplan.getEffectiveSymbol());
        currentDate = new LocalDate(rplan.getStart());

        timeclock = RPlanTools.getTimeClock(rplan);

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

        grmpf;
        und weiter gehts... bring das ding zum laufen

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

    }

    private void btnAddActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void btnPlanActionPerformed(ActionEvent e) {
        lblPlanUsed.setIcon(SYSConst.icon22ledGreenOn);
        lblTimeclockUsed.setIcon(SYSConst.icon22ledRedOn);
        lblOverrideUsed.setIcon(SYSConst.icon22ledRedOn);

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
//                    myRplan.getWorkinglogs().add(em.merge(new Workinglog(addBD, from.toDate(), to.toDate(), myRplan, txtComment.getText().trim(), WorkinglogTools.TYPE_ADDITIONAL)));
//                    myRplan.setStart(from.toDate());
//                    myRplan.setEnd(to.toDate());
//                } else {
            for (Workinglog workinglog : WorkinglogTools.createWorkingLogs(myRplan, rosterParameters.getSymbol(myRplan.getEffectiveSymbol()), contractsParameterSet)) {
                Workinglog myWorkinglog = em.merge(workinglog);
                myRplan.getWorkinglogs().add(myWorkinglog);
            }

//                }

            // TODO: Fixme
            //                myRplan.setActual(myRplan.getEffectiveSymbol());

            em.getTransaction().commit();
//            rplan = myRplan;
//            setButtonState();
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
//                Collection<Workinglog> logs2remove = new ArrayList<Workinglog>();
//                for (Workinglog workinglog : myRplan.getWorkinglogs()) {
//
//                    if (workinglog.isAuto()) {
//                        Workinglog myWorkinglog = em.merge(workinglog);
//                        em.remove(myWorkinglog);
//                        logs2remove.add(workinglog);
//                    }
//                }
//
//                myRplan.getWorkinglogs().removeAll(logs2remove);
//                // TODO: Fixme
//                //                myRplan.setActual(null);
//                if (effectiveSymbol.getSymbolType() == Symbol.PVALUE) {
//                    myRplan.getWorkinglogs().add(em.merge(new Workinglog(addBD, from.toDate(), to.toDate(), myRplan, txtComment.getText().trim(), WorkinglogTools.TYPE_ADDITIONAL)));
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
        scrollPane1 = new JScrollPane();
        pnlList = new JPanel();
        lblTimeclockUsed = new JLabel();
        lblOverrideUsed = new JLabel();
        lblAdditional = new JLabel();
        lblText = new JLabel();
        panel2 = new JPanel();
        txTtext = new JTextField();
        btnAdd = new JButton();

        //======== this ========
        setBorder(new LineBorder(Color.black, 2));
        setLayout(new FormLayout(
            "2*(default:grow, $lcgap), default, $lcgap, default:grow",
            "8*(default, $lgap), default, $nlgap, default, $lgap, default, $nlgap, default, $lgap, default:grow"));

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
        lblList.setHorizontalAlignment(SwingConstants.TRAILING);
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
        add(txtFrom, CC.xy(1, 19));
        add(txtTo, CC.xy(3, 19));

        //======== scrollPane1 ========
        {

            //======== pnlList ========
            {
                pnlList.setLayout(new BoxLayout(pnlList, BoxLayout.PAGE_AXIS));
            }
            scrollPane1.setViewportView(pnlList);
        }
        add(scrollPane1, CC.xywh(7, 5, 1, 21));

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
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


//    private JPanel getLine() {
//        // TODO: Fixme
//        // if (rplan.getActual().isEmpty()) return null;
//
//
//        JPanel pnlLine = new JPanel();
//        pnlLine.setLayout(new BorderLayout());
//        pnlLine.setAlignmentY(TOP_ALIGNMENT);
//
//        ArrayList<Workinglog> listActual = new ArrayList<Workinglog>();
//        BigDecimal mySum = BigDecimal.ZERO;
//        for (Workinglog workinglog : rplan.getWorkinglogs()) {
//            if (workinglog.isAuto()) {
//                listActual.add(workinglog);
//                mySum = mySum.add(workinglog.getHours());
//            }
//        }
//        Collections.sort(listActual);
//
//        DefaultMutableTreeNode root = new DefaultMutableTreeNode(mySum.setScale(2, RoundingMode.HALF_UP));
//        for (Workinglog workinglog : listActual) {
//            root.add(new DefaultMutableTreeNode(workinglog.getHours().setScale(2, RoundingMode.HALF_UP) + " [" + WorkinglogTools.TYPES[workinglog.getType()] + "]"));
//        }
//
//        JTree tree = new JTree(root);
//        tree.setShowsRootHandles(true);
//        SYSTools.collapseAll(tree);
//
//        JPanel pnlButton = new JPanel();
//        pnlButton.setOpaque(false);
//        pnlButton.setLayout(new BoxLayout(pnlButton, BoxLayout.LINE_AXIS));
//        JButton delButton = GUITools.getTinyButton(SYSConst.icon22delete, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                btnApply.setSelected(false);
//            }
//        });
//        pnlButton.add(delButton);
//        pnlButton.setAlignmentY(TOP_ALIGNMENT);
//
//        delButton.setEnabled(!rplan.isLocked());
//
//        pnlLine.add(tree, BorderLayout.CENTER);
//        pnlLine.add(pnlButton, BorderLayout.EAST);
//
//        pnlLine.setAlignmentY(TOP_ALIGNMENT);
//
//        sum = sum.add(mySum);
//
//        return pnlLine;
//    }
//
//    private JPanel getLine(final Workinglog workinglog) {
//        JPanel pnlLine = new JPanel();
//        pnlLine.setLayout(new BorderLayout());
//
//        JPanel pnlButton = new JPanel();
//        pnlButton.setLayout(new BoxLayout(pnlButton, BoxLayout.LINE_AXIS));
//        pnlButton.setOpaque(false);
//
//        JButton delButton = GUITools.getTinyButton(SYSConst.icon22delete, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                EntityManager em = OPDE.createEM();
//                try {
//                    em.getTransaction().begin();
//                    Rplan myRplan = em.merge(rplan);
//                    em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//                    em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);
//
//                    Workinglog myWorkinglog = em.merge(workinglog);
//                    em.remove(myWorkinglog);
//                    myRplan.getWorkinglogs().remove(workinglog);
//
//                    em.getTransaction().commit();
//                    rplan = myRplan;
//                } catch (OptimisticLockException ole) {
//                    if (em.getTransaction().isActive()) {
//                        em.getTransaction().rollback();
//                    }
//                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                } catch (Exception ex) {
//                    if (em.getTransaction().isActive()) {
//                        em.getTransaction().rollback();
//                    }
//                    OPDE.fatal(ex);
//                } finally {
//                    em.close();
//                    updateList();
//                }
//
//            }
//        });
//
//        pnlButton.add(delButton);
//        pnlButton.setAlignmentY(TOP_ALIGNMENT);
//        delButton.setEnabled(!rplan.isLocked());
//
//        DefaultMutableTreeNode root = new DefaultMutableTreeNode(workinglog.getHours().setScale(2, RoundingMode.HALF_UP).toString() + " (" + workinglog.getText() + ")");
//
//        root.add(new DefaultMutableTreeNode(OPDE.lang.getString("misc.msg.from") + ": " + new LocalTime(workinglog.getStart()).toString()));
//        root.add(new DefaultMutableTreeNode(OPDE.lang.getString("misc.msg.to") + ": " + new LocalTime(workinglog.getEnd()).toString()));
//
//
//        JTree tree = new JTree(root);
//        tree.setShowsRootHandles(true);
//        SYSTools.collapseAll(tree);
//
//        pnlLine.add(tree, BorderLayout.CENTER);
//        //        pnlLine.add(new JLabel(WorkinglogTools.toPrettyString(workinglog) + " (" + workinglog.getText() + ")"), BorderLayout.CENTER);
//        pnlLine.add(pnlButton, BorderLayout.EAST);
//
//        pnlLine.setAlignmentY(TOP_ALIGNMENT);
//
//        return pnlLine;
//    }
//
//    void updateList() {
//
//        pnlList.removeAll();
//        sum = BigDecimal.ZERO;
//
//        //        if (!rplan.getWorkinglogs().isEmpty()) {
//        //            pnlList.setBackground(Color.WHITE);
//        //        }
//        //        pnlList.setOpaque(!rplan.getWorkinglogs().isEmpty());
//
//        JPanel pnlActual = getLine();
//        if (pnlActual != null) {
//            pnlList.add(pnlActual);
//        }
//
//
//        for (Workinglog workinglog : rplan.getWorkinglogs()) {
//            //            if (workinglog.isActual()) {
//            //                actual = workinglog;
//            //            }
//
//            if (!workinglog.isAuto()) {
//                pnlList.add(getLine(workinglog));
//                sum = sum.add(workinglog.getHours());
//            }
//        }
//        afterAction.execute(rplan);
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
    private JScrollPane scrollPane1;
    private JPanel pnlList;
    private JLabel lblTimeclockUsed;
    private JLabel lblOverrideUsed;
    private JLabel lblAdditional;
    private JLabel lblText;
    private JPanel panel2;
    private JTextField txTtext;
    private JButton btnAdd;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
