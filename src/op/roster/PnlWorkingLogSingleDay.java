package op.roster;

import com.jidesoft.swing.StyledLabel;
import com.jidesoft.swing.StyledLabelBuilder;
import entity.roster.*;
import entity.system.Unique;
import entity.system.UniqueTools;
import op.OPDE;
import op.threads.DisplayManager;
import op.tools.GUITools;
import op.tools.SYSConst;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 16.10.13
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class PnlWorkingLogSingleDay extends JPanel {
    //    private JList listLogs;
    JScrollPane scrl;
    JButton apply, add;
    private Rplan rplan;
    private RosterParameters rosterParameters;
    private UserContracts userContracts;
    private Workinglog actual;
    private JPanel pnlList;
//    private LocalDate day;


    public PnlWorkingLogSingleDay(Rplan rplan, RosterParameters rosterParameters, UserContracts userContracts) {
        super();
        this.rplan = rplan;
        this.rosterParameters = rosterParameters;
        this.userContracts = userContracts;
//        this.day = new LocalDate(rplan.getStart());
        scrl = new JScrollPane();
        setBorder(new LineBorder(Color.DARK_GRAY, 2));




        initPanel();
    }

    void initPanel() {

        setLayout(new BorderLayout());

        apply = GUITools.getTinyButton(SYSConst.icon22apply, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EntityManager em = OPDE.createEM();
                try {
                    em.getTransaction().begin();
                    Rplan myRplan = em.merge(rplan);
                    em.lock(myRplan, LockModeType.OPTIMISTIC);

                    Unique unique = UniqueTools.getNewUID(em, "wlog_");

                    for (Workinglog workinglog : WorkinglogTools.createWorkingLogs(myRplan, rosterParameters.getSymbol(myRplan.getEffectiveSymbol()), userContracts.getParameterSet(new LocalDate(myRplan.getStart())), unique.getUid())) {
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
                    updateList();
                }

            }
        });
        add = GUITools.getTinyButton(SYSConst.icon22add, null);

        pnlList = new JPanel();
        pnlList.setLayout(new VerticalLayout());
        scrl.setViewportView(pnlList);
        updateList();

        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BorderLayout());

        StyledLabel lblDate = StyledLabelBuilder.createStyledLabel(DateFormat.getDateInstance(DateFormat.SHORT).format(rplan.getStart()) + " {" + rplan.getEffectiveHome().getPrefix() + "." + rplan.getEffectiveSymbol() + ":bold}");
        lblDate.setAlignmentX(CENTER_ALIGNMENT);
        JPanel pnlSurroundDate = new JPanel();
        pnlSurroundDate.setLayout(new BoxLayout(pnlSurroundDate, BoxLayout.LINE_AXIS));
        pnlSurroundDate.add(lblDate);
        pnlHeader.add(pnlSurroundDate, BorderLayout.CENTER);

        JPanel pnlButton = new JPanel();
        pnlButton.setLayout(new BoxLayout(pnlButton, BoxLayout.LINE_AXIS));
        pnlButton.add(apply);
        pnlButton.add(add);
        pnlHeader.add(pnlButton, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);
        add(scrl, BorderLayout.CENTER);

    }

    JPanel getLine(final Workinglog workinglog) {
        JPanel pnlLine = new JPanel();
        pnlLine.setLayout(new BorderLayout());

        JPanel pnlButton = new JPanel();
        pnlButton.setLayout(new BoxLayout(pnlButton, BoxLayout.LINE_AXIS));
        pnlButton.add(GUITools.getTinyButton(SYSConst.icon22delete, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EntityManager em = OPDE.createEM();
                try {
                    em.getTransaction().begin();
                    Rplan myRplan = em.merge(rplan);
                    em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                    if (workinglog.getActualKey() > 0) {
                        Collection<Workinglog> logs2remove = new ArrayList<Workinglog>();
                        for (Workinglog wlog : myRplan.getWorkinglogs()) {
                            if (wlog.getActualKey() == workinglog.getActualKey()) {
                                Workinglog myWorkinglog = em.merge(wlog);
                                em.remove(myWorkinglog);
                                logs2remove.add(wlog);
                            }
                        }

                        myRplan.getWorkinglogs().removeAll(logs2remove);
                        myRplan.setActual(null);

                    } else {
                        Workinglog myWorkinglog = em.merge(workinglog);
                        em.remove(myWorkinglog);
                        myRplan.getWorkinglogs().remove(workinglog);
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
                    updateList();
                }

            }
        }));

        pnlLine.add(new JLabel(WorkinglogTools.toPrettyString(workinglog)), BorderLayout.CENTER);
        pnlLine.add(pnlButton, BorderLayout.EAST);

        pnlLine.setAlignmentY(TOP_ALIGNMENT);

        return pnlLine;
    }

    void updateList() {
        pnlList.removeAll();

        if (!rplan.getWorkinglogs().isEmpty()) {
            pnlList.setBackground(Color.WHITE);
        }
        pnlList.setOpaque(!rplan.getWorkinglogs().isEmpty());

        actual = null;
        for (Workinglog workinglog : rplan.getWorkinglogs()) {
//            if (workinglog.isActual()) {
//                actual = workinglog;
//            }
            if (!workinglog.isDeleted() && !workinglog.isReplaced()) {
                pnlList.add(getLine(workinglog));
            }
        }
        apply.setEnabled(rplan.getActual().isEmpty());
        scrl.validate();
        scrl.repaint();
    }
}
