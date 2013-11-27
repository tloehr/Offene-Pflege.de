package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.roster.*;
import entity.system.Unique;
import entity.system.UniqueTools;
import op.OPDE;
import op.threads.DisplayManager;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 16.10.13
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class PnlWorkingLogSingleDayOld extends JPanel {
    //    private JList listLogs;
    JScrollPane scrl;
    //    JButton apply, add;
    JToggleButton apply;
    private Rplan rplan;
    private RosterParameters rosterParameters;
    private ContractsParameterSet contractsParameterSet;
    private Closure afterAction;
    private ItemListener mainItemListener;
    private Symbol effectiveSymbol;


    public PnlWorkingLogSingleDayOld(Rplan rplan, RosterParameters rosterParameters, ContractsParameterSet contractsParameterSet, Closure afterAction) {
        super();
        this.rplan = rplan;
        this.rosterParameters = rosterParameters;
        this.contractsParameterSet = contractsParameterSet;
        this.afterAction = afterAction;
        effectiveSymbol = rosterParameters.getSymbol(rplan.getEffectiveSymbol());

        scrl = new JScrollPane();
//        setBorder(new LineBorder(Color.DARK_GRAY, 2));

//        owner = this;


        initPanel();
    }

    void initPanel() {

        setLayout(new FormLayout(
                "2*(default:grow, $lcgap), default", // "default:grow, $lcgap, default:grow",
                "2*(default:grow, $lgap), default")); // "default:grow, $lgap, default"


        mainItemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        Rplan myRplan = em.merge(rplan);
                        em.lock(myRplan, LockModeType.OPTIMISTIC);

                        Unique unique = UniqueTools.getNewUID(em, "wlog_");

                        for (Workinglog workinglog : WorkinglogTools.createWorkingLogs(myRplan, rosterParameters.getSymbol(myRplan.getEffectiveSymbol()), contractsParameterSet, unique.getUid())) {
                            myRplan.getWorkinglogs().add(em.merge(workinglog));
                        }

                        myRplan.setActual(myRplan.getEffectiveSymbol());

                        em.getTransaction().commit();
                        rplan = myRplan;
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

                        Collection<Workinglog> logs2remove = new ArrayList<Workinglog>();
                        for (Workinglog workinglog : myRplan.getWorkinglogs()) {

                            if (workinglog.getActualKey() > 0) {
                                Workinglog myWorkinglog = em.merge(workinglog);
                                em.remove(myWorkinglog);
                                logs2remove.add(workinglog);
                            }

                            myRplan.getWorkinglogs().removeAll(logs2remove);
                            myRplan.setActual(null);
                        }

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
                    }


//                    EntityManager em = OPDE.createEM();
//                    try {
//                        em.getTransaction().begin();
//
//
//
//
//                        Workinglog workinglog = em.merge((Workinglog) o);
//                        Rplan myRplan = em.merge(rplan);
//                        em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//                        myRplan.getWorkinglogs().add(workinglog);
//
//                        em.getTransaction().commit();
//                        rplan = myRplan;
//                    } catch (OptimisticLockException ole) {
//                        OPDE.error(ole);
//                        if (em.getTransaction().isActive()) {
//                            em.getTransaction().rollback();
//                        }
//                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                    } catch (Exception ex) {
//                        if (em.getTransaction().isActive()) {
//                            em.getTransaction().rollback();
//                        }
//                        OPDE.fatal(ex);
//                    } finally {
//                        em.close();
//                        updateList();
//                    }
                }
            }
        };


//        pnlList = new JPanel();
//        pnlList.setLayout(new VerticalLayout());
//        scrl.setViewportView(pnlList);
//        updateList();

//        JPanel pnlHeader = new JPanel();
//        pnlHeader.setLayout(new BorderLayout());

        SimpleDateFormat sdf = new SimpleDateFormat("dd.");

        String text = "<b><font size=\"+1\">" + sdf.format(rplan.getStart()) + " &rarr; " + effectiveSymbol.getKey().toUpperCase() + "</font></b>"
                + "<br/><font size=\"-1\">" + rplan.getEffectiveHome().getShortname() + "</font>"
                + "<br/><i><font size=\"-1\">" + effectiveSymbol.getDescription() + "</font></i>";

        apply = new JToggleButton(SYSConst.icon32empty);
        apply.setSelectedIcon(SYSConst.icon32apply);
        apply.setSelected(!rplan.getActual().isEmpty());
        apply.addItemListener(mainItemListener);
        apply.setHorizontalTextPosition(SwingConstants.TRAILING);
        apply.setVerticalTextPosition(SwingConstants.CENTER);
        apply.setText(SYSTools.toHTMLForScreen(text));
        add(apply, CC.xywh(1, 1, 3, 3));

        JToggleButton add1 = new JToggleButton(SYSConst.icon22ledBlueOff);
        add1.setSelectedIcon(SYSConst.icon22ledBlueOn);
        add1.setSelected(WorkinglogTools.getAdditional1(rplan) != null);
        add1.setHorizontalTextPosition(SwingConstants.CENTER);
        add1.setVerticalTextPosition(SwingConstants.CENTER);
        add1.setText("+1");
        add1.setFont(SYSConst.ARIAL18BOLD);
        add1.setForeground(Color.YELLOW);
        add(add1, CC.xy(1, 5));

        JToggleButton add2 = new JToggleButton(SYSConst.icon22ledYellowOff);
        add2.setSelectedIcon(SYSConst.icon22ledYellowOn);
        add2.setSelected(WorkinglogTools.getAdditional2(rplan) != null);
        add2.setHorizontalTextPosition(SwingConstants.CENTER);
        add2.setVerticalTextPosition(SwingConstants.CENTER);
        add2.setText("+2");
        add2.setFont(SYSConst.ARIAL18BOLD);
        add2.setForeground(Color.BLUE);
        add(add2, CC.xy(3, 5));

        String sum = SYSConst.html_table_tr(SYSConst.html_table_td("<font size=\"-1\">"+"1,25"+ "</font>", "right")) +
                SYSConst.html_table_tr(SYSConst.html_table_td("<font size=\"-1\">"+"Z1: 1,25"+ "</font>", "right")) +
                SYSConst.html_table_tr(SYSConst.html_table_td("<font size=\"-1\">"+"Z2: 1,25"+ "</font>", "right")) +
                SYSConst.html_table_tr(SYSConst.html_table_td("==========","right")) +
                SYSConst.html_table_tr(SYSConst.html_table_td("9,0","right"));

        String tbl = "<font size=\"-1\">" + SYSConst.html_table(sum, "0") + "</font>";

//        String sum = "<b><font size=\"+1\">" + sdf.format(rplan.getStart()) + " &rarr; " + effectiveSymbol.getKey().toUpperCase() + "</font></b>"
//                + "<br/><font size=\"-1\">" + rplan.getEffectiveHome().getShortname() + "</font>"
//                + "<br/><i><font size=\"-1\">" + effectiveSymbol.getDescription() + "</font></i>";
        JLabel sumLabel = new JLabel(SYSTools.toHTMLForScreen(tbl));
        add(sumLabel, CC.xywh(5, 1, 1, 5));

        add2.setEnabled(add1.isSelected());
//        add(scrl, BorderLayout.CENTER);

    }

//    JPanel getLine(final Workinglog workinglog) {
//        JPanel pnlLine = new JPanel();
//        pnlLine.setLayout(new BorderLayout());
//
//        JPanel pnlButton = new JPanel();
//        pnlButton.setLayout(new BoxLayout(pnlButton, BoxLayout.LINE_AXIS));
//        pnlButton.add(GUITools.getTinyButton(SYSConst.icon22delete, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                EntityManager em = OPDE.createEM();
//                try {
//                    em.getTransaction().begin();
//                    Rplan myRplan = em.merge(rplan);
//                    em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//
//                    if (workinglog.getActualKey() > 0) {
//                        Collection<Workinglog> logs2remove = new ArrayList<Workinglog>();
//                        for (Workinglog wlog : myRplan.getWorkinglogs()) {
//                            if (wlog.getActualKey() == workinglog.getActualKey()) {
//                                Workinglog myWorkinglog = em.merge(wlog);
//                                em.remove(myWorkinglog);
//                                logs2remove.add(wlog);
//                            }
//                        }
//
//                        myRplan.getWorkinglogs().removeAll(logs2remove);
//                        myRplan.setActual(null);
//
//                    } else {
//                        Workinglog myWorkinglog = em.merge(workinglog);
//                        em.remove(myWorkinglog);
//                        myRplan.getWorkinglogs().remove(workinglog);
//                    }
//
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
//        }));
//
//        pnlLine.add(new JLabel(WorkinglogTools.toPrettyString(workinglog)), BorderLayout.CENTER);
//        pnlLine.add(pnlButton, BorderLayout.EAST);
//
//        pnlLine.setAlignmentY(TOP_ALIGNMENT);
//
//        return pnlLine;
//    }

//    void updateList() {
//        pnlList.removeAll();
//
//        if (!rplan.getWorkinglogs().isEmpty()) {
//            pnlList.setBackground(Color.WHITE);
//        }
//        pnlList.setOpaque(!rplan.getWorkinglogs().isEmpty());
//
////        actual = null;
//        for (Workinglog workinglog : rplan.getWorkinglogs()) {
////            if (workinglog.isActual()) {
////                actual = workinglog;
////            }
//            if (!workinglog.isDeleted() && !workinglog.isReplaced()) {
//                pnlList.add(getLine(workinglog));
//            }
//        }
//        afterAction.execute(rplan);
//        apply.setEnabled(rplan.getActual().isEmpty());
//        scrl.validate();
//        scrl.repaint();
//    }
}
