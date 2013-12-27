/*
 * Created by JFormDesigner on Wed Nov 27 14:23:26 CET 2013
 */

package op.roster;

import javax.swing.border.*;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.swing.DefaultOverlayable;
import entity.roster.*;
import op.OPDE;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.GUITools;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Torsten Löhr
 */
public class PnlWorkingLogDay extends JPanel {
    private Rplan rplan;
    private RosterParameters rosterParameters;
    private ContractsParameterSet contractsParameterSet;
    private Closure afterAction;
    private ItemListener mainItemListener;
    private Symbol effectiveSymbol;
    private JTextField txtComment, txtFrom, txtTo;
    private boolean initPhase;
    private BigDecimal sum;

    public PnlWorkingLogDay(Rplan rplan, RosterParameters rosterParameters, ContractsParameterSet contractsParameterSet, Closure afterAction) {
        initPhase = true;
        this.rplan = rplan;
        this.rosterParameters = rosterParameters;
        this.contractsParameterSet = contractsParameterSet;
        this.afterAction = afterAction;
        effectiveSymbol = rosterParameters.getSymbol(rplan.getEffectiveSymbol());

        initComponents();
        initPanel();
        initPhase = false;
    }

    private void btnApplyItemStateChanged(ItemEvent e) {

        lblOKIcon.setIcon(e.getStateChange() == ItemEvent.SELECTED ? SYSConst.icon32apply : null);

        if (initPhase) return;

        DateTime from = null;
        DateTime to = null;
        BigDecimal addBD = null;
        if (effectiveSymbol.getSymbolType() == Symbol.PVALUE) {
            try {
                from = new LocalDate(rplan.getStart()).toDateTime(SYSCalendar.parseLocalTime(txtFrom.getText()));
                to = new LocalDate(rplan.getStart()).toDateTime(SYSCalendar.parseLocalTime(txtTo.getText()));
            } catch (NumberFormatException e1) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.wrongtime2"));
                return;
            }

            if (from.isAfter(to)) {
                to = to.plusDays(1);
            }

            addBD = SYSCalendar.getHoursAsDecimal(from, to);
            if (txtComment.getText().isEmpty()) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.emptyentry"));
                return;
            }
        }

        if (e.getStateChange() == ItemEvent.SELECTED) {
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                Rplan myRplan = em.merge(rplan);
                em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);


                if (effectiveSymbol.getSymbolType() == Symbol.PVALUE) {
                    myRplan.getWorkinglogs().add(em.merge(new Workinglog(addBD, from.toDate(), to.toDate(), myRplan, txtComment.getText().trim(), WorkinglogTools.TYPE_ADDITIONAL)));
                    myRplan.setStart(from.toDate());
                    myRplan.setEnd(to.toDate());
                } else {
                    for (Workinglog workinglog : WorkinglogTools.createWorkingLogs(myRplan, rosterParameters.getSymbol(myRplan.getEffectiveSymbol()), contractsParameterSet)) {
                        myRplan.getWorkinglogs().add(em.merge(workinglog));
                    }

                }

                // TODO: Fixme
//                myRplan.setActual(myRplan.getEffectiveSymbol());

                em.getTransaction().commit();
                rplan = myRplan;
                setButtonState();
                updateList();
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
        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                Rplan myRplan = em.merge(rplan);
                em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);
                Collection<Workinglog> logs2remove = new ArrayList<Workinglog>();
                for (Workinglog workinglog : myRplan.getWorkinglogs()) {

                    if (workinglog.isAuto()) {
                        Workinglog myWorkinglog = em.merge(workinglog);
                        em.remove(myWorkinglog);
                        logs2remove.add(workinglog);
                    }
                }

                myRplan.getWorkinglogs().removeAll(logs2remove);
                // TODO: Fixme
//                myRplan.setActual(null);
                if (effectiveSymbol.getSymbolType() == Symbol.PVALUE) {
                    myRplan.getWorkinglogs().add(em.merge(new Workinglog(addBD, from.toDate(), to.toDate(), myRplan, txtComment.getText().trim(), WorkinglogTools.TYPE_ADDITIONAL)));
                    myRplan.setStart(new DateTime(myRplan.getStart()).toLocalDate().toDateTimeAtStartOfDay().toDate());
                    myRplan.setEnd(null);
                }

                em.getTransaction().commit();
                rplan = myRplan;
                setButtonState();
                updateList();
            } catch (OptimisticLockException ole) {
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
        }
    }

    private void initPanel() {

        txtFrom = new JTextField();
        txtTo = new JTextField();
        txtComment = new JTextField();

        txtFrom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTo.requestFocus();
            }
        });

        txtTo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtComment.requestFocus();
            }
        });

        txtComment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtFrom.requestFocus();
            }
        });

        DefaultOverlayable overFrom = new DefaultOverlayable(txtFrom);
        JLabel lblFrom = new JLabel(OPDE.lang.getString("misc.msg.from"));
        lblFrom.setForeground(Color.LIGHT_GRAY);
        overFrom.addOverlayComponent(lblFrom);

        DefaultOverlayable overTo = new DefaultOverlayable(txtTo);
        JLabel lblTo = new JLabel(OPDE.lang.getString("misc.msg.to"));
        lblTo.setForeground(Color.LIGHT_GRAY);
        overTo.addOverlayComponent(lblTo);

        DefaultOverlayable overComm = new DefaultOverlayable(txtComment);
        JLabel lblComm = new JLabel(OPDE.lang.getString("misc.msg.comment"));
        lblComm.setForeground(Color.LIGHT_GRAY);
        overComm.addOverlayComponent(lblComm);

        pnlAdditional.add(overFrom, CC.xy(1, 1));
        pnlAdditional.add(overTo, CC.xy(2, 1));
        pnlAdditional.add(overComm, CC.xywh(1, 2, 2, 1));

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.");
        String text = "<b><font size=\"+1\">" + sdf.format(rplan.getStart()) + " &rarr; " + effectiveSymbol.getKey().toUpperCase() + "</font></b>"
                + "<br/><font size=\"-1\">" + rplan.getEffectiveHome().getShortname() + "</font>"
                + "<br/><i><font size=\"-1\">" + effectiveSymbol.getDescription() + "</font></i>";
        btnApply.setText(SYSTools.toHTMLForScreen(text));
        // TODO: Fixme
//        btnApply.setSelected(!rplan.getActual().isEmpty());

//        btnController.setSelected(rplan.isLocked());
        setButtonState();
        updateList();
    }


    private JPanel getLine() {
        // TODO: Fixme
//        if (rplan.getActual().isEmpty()) return null;

        JPanel pnlLine = new JPanel();
        pnlLine.setLayout(new BorderLayout());
        pnlLine.setAlignmentY(TOP_ALIGNMENT);

        ArrayList<Workinglog> listActual = new ArrayList<Workinglog>();
        BigDecimal mySum = BigDecimal.ZERO;
        for (Workinglog workinglog : rplan.getWorkinglogs()) {
            if (workinglog.isAuto()) {
                listActual.add(workinglog);
                mySum = mySum.add(workinglog.getHours());
            }
        }
        Collections.sort(listActual);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(mySum.setScale(2, RoundingMode.HALF_UP));
        for (Workinglog workinglog : listActual) {
            root.add(new DefaultMutableTreeNode(workinglog.getHours().setScale(2, RoundingMode.HALF_UP) + " [" + WorkinglogTools.TYPES[workinglog.getType()] + "]"));
        }

        JTree tree = new JTree(root);
        tree.setShowsRootHandles(true);
        SYSTools.collapseAll(tree);

        JPanel pnlButton = new JPanel();
        pnlButton.setOpaque(false);
        pnlButton.setLayout(new BoxLayout(pnlButton, BoxLayout.LINE_AXIS));
        JButton delButton = GUITools.getTinyButton(SYSConst.icon22delete, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnApply.setSelected(false);
            }
        });
        pnlButton.add(delButton);
        pnlButton.setAlignmentY(TOP_ALIGNMENT);

        delButton.setEnabled(!rplan.isLocked());

        pnlLine.add(tree, BorderLayout.CENTER);
        pnlLine.add(pnlButton, BorderLayout.EAST);

        pnlLine.setAlignmentY(TOP_ALIGNMENT);

        sum = sum.add(mySum);

        return pnlLine;
    }

    private JPanel getLine(final Workinglog workinglog) {
        JPanel pnlLine = new JPanel();
        pnlLine.setLayout(new BorderLayout());

        JPanel pnlButton = new JPanel();
        pnlButton.setLayout(new BoxLayout(pnlButton, BoxLayout.LINE_AXIS));
        pnlButton.setOpaque(false);

        JButton delButton = GUITools.getTinyButton(SYSConst.icon22delete, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EntityManager em = OPDE.createEM();
                try {
                    em.getTransaction().begin();
                    Rplan myRplan = em.merge(rplan);
                    em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                    em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);

                    Workinglog myWorkinglog = em.merge(workinglog);
                    em.remove(myWorkinglog);
                    myRplan.getWorkinglogs().remove(workinglog);

                    em.getTransaction().commit();
                    rplan = myRplan;
                } catch (OptimisticLockException ole) {
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
                    updateList();
                }

            }
        });

        pnlButton.add(delButton);
        pnlButton.setAlignmentY(TOP_ALIGNMENT);
        delButton.setEnabled(!rplan.isLocked());

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(workinglog.getHours().setScale(2, RoundingMode.HALF_UP).toString() + " (" + workinglog.getText() + ")");

        root.add(new DefaultMutableTreeNode(OPDE.lang.getString("misc.msg.from") + ": " + new LocalTime(workinglog.getStart()).toString()));
        root.add(new DefaultMutableTreeNode(OPDE.lang.getString("misc.msg.to") + ": " + new LocalTime(workinglog.getEnd()).toString()));


        JTree tree = new JTree(root);
        tree.setShowsRootHandles(true);
        SYSTools.collapseAll(tree);

        pnlLine.add(tree, BorderLayout.CENTER);
//        pnlLine.add(new JLabel(WorkinglogTools.toPrettyString(workinglog) + " (" + workinglog.getText() + ")"), BorderLayout.CENTER);
        pnlLine.add(pnlButton, BorderLayout.EAST);

        pnlLine.setAlignmentY(TOP_ALIGNMENT);

        return pnlLine;
    }


    void updateList() {

        pnlList.removeAll();
        sum = BigDecimal.ZERO;

//        if (!rplan.getWorkinglogs().isEmpty()) {
//            pnlList.setBackground(Color.WHITE);
//        }
//        pnlList.setOpaque(!rplan.getWorkinglogs().isEmpty());

        JPanel pnlActual = getLine();
        if (pnlActual != null) {
            pnlList.add(pnlActual);
        }


        for (Workinglog workinglog : rplan.getWorkinglogs()) {
            //            if (workinglog.isActual()) {
            //                actual = workinglog;
            //            }

            if (!workinglog.isAuto()) {
                pnlList.add(getLine(workinglog));
                sum = sum.add(workinglog.getHours());
            }
        }
        afterAction.execute(rplan);

        lblSum.setText(OPDE.lang.getString("misc.msg.sum") + ": " + sum.setScale(2, RoundingMode.HALF_UP).toString());

        scrl.validate();
        scrl.repaint();
    }

    private void btnAddActionPerformed(ActionEvent e) {

        // Symbole für Betreuung wo die Zeiten vorher nicht klar sind

        DateTime from = null;
        DateTime to = null;
        try {
            from = new LocalDate(rplan.getStart()).toDateTime(SYSCalendar.parseLocalTime(txtFrom.getText()));
            to = new LocalDate(rplan.getStart()).toDateTime(SYSCalendar.parseLocalTime(txtTo.getText()));
        } catch (NumberFormatException e1) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.wrongtime2"));
            return;
        }

        if (from.isAfter(to)) {
            to = to.plusDays(1);
        }

        BigDecimal addBD = SYSCalendar.getHoursAsDecimal(from, to);
        if (txtComment.getText().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.emptyentry"));
            return;
        }
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            Rplan myRplan = em.merge(rplan);
            em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);

            Workinglog workinglog = em.merge(new Workinglog(addBD, from.toDate(), to.toDate(), myRplan, txtComment.getText().trim(), WorkinglogTools.TYPE_ADDITIONAL));
            myRplan.getWorkinglogs().add(workinglog);

            em.getTransaction().commit();
            rplan = myRplan;
        } catch (OptimisticLockException ole) {
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
            updateList();
        }
        txtComment.setText(null);
        txtFrom.setText(null);
        txtTo.setText(null);
    }

    private void btnControllerItemStateChanged(ItemEvent e) {
        if (initPhase) return;
        if (e.getStateChange() == ItemEvent.SELECTED) {
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                Rplan myRplan = em.merge(rplan);
                em.lock(myRplan, LockModeType.OPTIMISTIC);
                em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);

                myRplan.setController(em.merge(OPDE.getLogin().getUser()));

                em.getTransaction().commit();
                rplan = myRplan;

                setButtonState();
                updateList();

            } catch (OptimisticLockException ole) {
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
                updateList();
            }
        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                Rplan myRplan = em.merge(rplan);
                em.lock(myRplan, LockModeType.OPTIMISTIC);
                em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);

                myRplan.setController(null);

                em.getTransaction().commit();
                rplan = myRplan;

                setButtonState();
                updateList();

            } catch (OptimisticLockException ole) {
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
                updateList();
            }
        }
    }

    private void setButtonState() {
        btnApply.setEnabled(!rplan.isLocked());
        btnAdd.setEnabled(!rplan.isLocked() && effectiveSymbol.getSymbolType() != Symbol.PVALUE);
        txtFrom.setEnabled(!rplan.isLocked());
        txtTo.setEnabled(!rplan.isLocked());
        txtComment.setEnabled(!rplan.isLocked());
        // TODO: Fixme
//        btnController.setEnabled(!rplan.getActual().isEmpty() && OPDE.getAppInfo().isAllowedTo(InternalClassACL.USER1, PnlUsersWorklog.internalClassID));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        btnApply = new JToggleButton();
        scrl = new JScrollPane();
        pnlList = new JPanel();
        pnlAdditional = new JPanel();
        btnAdd = new JButton();
        lblOKIcon = new JLabel();
        lblSum = new JLabel();

        //======== this ========
        setBorder(new LineBorder(Color.black, 2, true));
        setLayout(new FormLayout(
                "80dlu, 2*($lcgap, default), $lcgap, default:grow",
                "default, $lgap, default"));

        //---- btnApply ----
        btnApply.setText("text");
        btnApply.setIcon(null);
        btnApply.setHorizontalAlignment(SwingConstants.LEFT);
        btnApply.setSelectedIcon(null);
        btnApply.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                btnApplyItemStateChanged(e);
            }
        });
        add(btnApply, CC.xywh(1, 1, 3, 1));

        //======== scrl ========
        {

            //======== pnlList ========
            {
                pnlList.setBackground(Color.white);
                pnlList.setLayout(new VerticalLayout());
            }
            scrl.setViewportView(pnlList);
        }
        add(scrl, CC.xywh(5, 1, 3, 1, CC.DEFAULT, CC.FILL));

        //======== pnlAdditional ========
        {
            pnlAdditional.setLayout(new FormLayout(
                "2*(default:grow)",
                "default, fill:default"));
        }
        add(pnlAdditional, CC.xy(1, 3, CC.DEFAULT, CC.FILL));

        //---- btnAdd ----
        btnAdd.setText(null);
        btnAdd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
        btnAdd.setFont(new Font("Arial", Font.BOLD, 18));
        btnAdd.setContentAreaFilled(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setBorder(null);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAddActionPerformed(e);
            }
        });
        add(btnAdd, CC.xy(3, 3));

        //---- lblOKIcon ----
        lblOKIcon.setText(null);
        add(lblOKIcon, CC.xy(5, 3));

        //---- lblSum ----
        lblSum.setText("text");
        add(lblSum, CC.xy(7, 3, CC.RIGHT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JToggleButton btnApply;
    private JScrollPane scrl;
    private JPanel pnlList;
    private JPanel pnlAdditional;
    private JButton btnAdd;
    private JLabel lblOKIcon;
    private JLabel lblSum;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
