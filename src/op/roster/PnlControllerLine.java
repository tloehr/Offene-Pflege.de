/*
 * Created by JFormDesigner on Tue Jan 14 14:51:47 CET 2014
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Homes;
import entity.HomesTools;
import entity.roster.*;
import op.OPDE;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.tools.GUITools;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class PnlControllerLine extends JPanel {
    private Rplan rplan;
    private final RosterParameters rosterParameters;
    private final ContractsParameterSet contractsParameterSet;
    //    private final Symbol effectiveSymbol;
//    private Symbol actualSymbol;
    private final LocalDate refDate;
    // all time clocks that start on this particular day
    ArrayList<Timeclock> listTimeClocks;
    SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yy");
    private BigDecimal sumHours;

    public PnlControllerLine(Rplan rplan, RosterParameters rosterParameters, ContractsParameterSet contractsParameterSet) {
        this.rplan = rplan;
        this.rosterParameters = rosterParameters;
        this.contractsParameterSet = contractsParameterSet;
        refDate = new LocalDate(rplan.getStart());
        initComponents();
        initPanel();
    }

    private void initPanel() {
        lblDate.setText(sdf.format(refDate.toDate()));

        // Weekend ?
        if (refDate.getDayOfWeek() == DateTimeConstants.SATURDAY || refDate.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            lblDate.setBackground(SYSConst.red1[SYSConst.medium3]);
        }

        // Holiday ?
        if (OPDE.isHoliday(refDate)) {
            lblDate.setBackground(SYSConst.red1[SYSConst.medium1]);
            lblDate.setText(SYSTools.toHTMLForScreen(sdf.format(refDate.toDate()) + "<br/>" + SYSConst.html_italic(OPDE.getHoliday(refDate))));
        }

        listTimeClocks = TimeclockTools.getAllStartingOn(refDate, rplan.getOwner());


        lblEffectivePlan.setText(SYSTools.toHTMLForScreen(rosterParameters.toHTML(rplan.getEffectiveSymbol(), rplan.getEffectiveHome())));

        rosterParameters.setComboBox(cmbSymbol);
        HomesTools.setComboBox(cmbHome);

        if (rplan.getActual() != null) {
            cmbSymbol.setSelectedItem(rosterParameters.getSymbol(rplan.getActual()));
            cmbHome.setSelectedItem(rplan.getHomeActual());
        } else {
            cmbSymbol.setSelectedIndex(0);
            cmbHome.setSelectedItem(rplan.getEffectiveHome());
        }

        btnOK1.setSelected(rplan.getCtrl1() != null);
        btnOK2.setSelected(rplan.getCtrl2() != null);
        btnOK1.setEnabled(!btnOK2.isSelected() && (OPDE.getAppInfo().isAllowedTo(InternalClassACL.USER1, PnlUsersWorklog.internalClassID) || OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, PnlUsersWorklog.internalClassID)));
        btnOK2.setEnabled(btnOK1.isSelected() && OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, PnlUsersWorklog.internalClassID));

        btnOK1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                btnOK1ItemStateChanged(e);
            }
        });

        btnOK2.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                btnOK2ItemStateChanged(e);
            }
        });


        /***
         *     __        ___                ____       _        _ _
         *     \ \      / / |    ___   __ _|  _ \  ___| |_ __ _(_) |___
         *      \ \ /\ / /| |   / _ \ / _` | | | |/ _ \ __/ _` | | / __|
         *       \ V  V / | |__| (_) | (_| | |_| |  __/ || (_| | | \__ \
         *        \_/\_/  |_____\___/ \__, |____/ \___|\__\__,_|_|_|___/
         *                            |___/
         */
        updateList();

        /***
         *      _____ _                 ____ _            _
         *     |_   _(_)_ __ ___   ___ / ___| | ___   ___| | __
         *       | | | | '_ ` _ \ / _ \ |   | |/ _ \ / __| |/ /
         *       | | | | | | | | |  __/ |___| | (_) | (__|   <
         *       |_| |_|_| |_| |_|\___|\____|_|\___/ \___|_|\_\
         *
         */

        if (refDate.compareTo(new LocalDate()) <= 0 && !listTimeClocks.isEmpty()) {

            pnlTimeClock.setLayout(new GridLayout(0, 2));

            pnlTimeClock.add(new JLabel(OPDE.lang.getString("dlglogin.timeclock.came")));
            pnlTimeClock.add(new JLabel(OPDE.lang.getString("dlglogin.timeclock.gone")));

            DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
            for (Timeclock timeclock : listTimeClocks) {
                pnlTimeClock.add(new JLabel(df.format(timeclock.getBegin())));

                JLabel lblEnd = new JLabel(">>>>>>>>");
                if (!timeclock.isOpen()) {
                    lblEnd.setText(df.format(timeclock.getEnd()));

                }

                if (!SYSTools.catchNull(timeclock.getText()).isEmpty()) {
                    lblEnd.setIcon(SYSConst.icon16info);
                    lblEnd.setToolTipText(timeclock.getText().trim());
                }
                pnlTimeClock.add(lblEnd);
            }
        }

    }

//    private void cmbSymbolItemStateChanged(ItemEvent e) {
//        if (e.getStateChange() != ItemEvent.SELECTED) return;
//
//        EntityManager em = OPDE.createEM();
//        try {
//            em.getTransaction().begin();
//            Rplan myRplan = em.merge(rplan);
////            if (myRplan.getWlog() == null) {
////                WLog wlog = em.merge(new WLog(myRplan, actualSymbol.getKey(), (Homes) e.getItem()));
////                myRplan.setWlog(wlog);
////            }
//            myRplan.getWlog().setHomeactual((Homes) cmbHome.getSelectedItem());
//
//
//            em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//            em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);
//
//            em.getTransaction().commit();
//
//
//            rplan = myRplan;
//        } catch (OptimisticLockException ole) {
//            OPDE.error(ole);
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//        } catch (Exception ex) {
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//            OPDE.fatal(ex);
//        } finally {
//            em.close();
//
//        }
//    }

//    private void cmbHomeItemStateChanged(ItemEvent e) {
//        if (e.getStateChange() != ItemEvent.SELECTED) return;
//
//        EntityManager em = OPDE.createEM();
//        try {
//            em.getTransaction().begin();
//            Rplan myRplan = em.merge(rplan);
////            if (myRplan.getWlog() == null) {
////                WLog wlog = em.merge(new WLog(myRplan, actualSymbol.getKey(), (Homes) e.getItem()));
////                myRplan.setWlog(wlog);
////            }
//            myRplan.getWlog().setHomeactual((Homes) e.getItem());
//            em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//            em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);
//
//            em.getTransaction().commit();
//
//            rplan = myRplan;
//        } catch (OptimisticLockException ole) {
//            OPDE.error(ole);
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//        } catch (Exception ex) {
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//            OPDE.fatal(ex);
//        } finally {
//            em.close();
//        }
//    }

    private void btnOK2ItemStateChanged(ItemEvent e) {
        rplan.setCtrl2(OPDE.getLogin().getUser());
        btnOK1.setEnabled(e.getStateChange() != ItemEvent.SELECTED);
    }

    private void btnOK1ItemStateChanged(ItemEvent e) {
        rplan.setCtrl1(OPDE.getLogin().getUser());
        btnOK2.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
    }


    private void btnProcessActionPerformed(ActionEvent e) {
        if (cmbSymbol.getSelectedItem() == null) {
            cmbSymbol.setSelectedItem(rosterParameters.getSymbol(rplan.getEffectiveSymbol()));

        }

        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            Rplan myRplan = em.merge(rplan);
            em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);

            myRplan.setHomeActual(em.merge((Homes) cmbHome.getSelectedItem()));
            myRplan.setActual(((Symbol) cmbSymbol.getSelectedItem()).getKey());

            WLogDetailsTools.setDetails(em, myRplan, (Symbol) cmbSymbol.getSelectedItem(), contractsParameterSet);

            em.getTransaction().commit();
            rplan = myRplan;
            cmbSymbol.setSelectedItem(rosterParameters.getSymbol(rplan.getActual()));

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
    }

    JPanel getLine(final WLogDetails wLogDetails) {

        sumHours = sumHours.add(wLogDetails.getHours());

        JPanel pnlLine = new JPanel();
        pnlLine.setLayout(new BorderLayout());

        JPanel pnlButton = new JPanel();
        pnlButton.setLayout(new BoxLayout(pnlButton, BoxLayout.LINE_AXIS));
        pnlButton.add(GUITools.getTinyButton(SYSConst.icon22delete, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                EntityManager em = OPDE.createEM();
//                try {
//                    em.getTransaction().begin();
//                    Rplan myRplan = em.merge(rplan);
//                    em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//                    Workinglog myWorkinglog = em.merge(workinglog);
//                    em.remove(myWorkinglog);
//                    myRplan.getWorkinglogs().remove(workinglog);
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

            }
        }));

        pnlLine.add(new JLabel(WLogDetailsTools.TYPES[wLogDetails.getType()] + ": " + wLogDetails.getHours().toString()), BorderLayout.CENTER);
        pnlLine.add(pnlButton, BorderLayout.EAST);

        pnlLine.setAlignmentY(TOP_ALIGNMENT);

        return pnlLine;
    }

    JPanel getLine(final ArrayList<WLogDetails> listDetails) {

        JTree tree = new JTree();
        tree.setShowsRootHandles(true);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();

        sumHours = BigDecimal.ZERO;

        for (WLogDetails wld : listDetails) {
            sumHours = sumHours.add(wld.getHours());
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(WLogDetailsTools.TYPES[wld.getType()] + ": " + wld.getHours().toString());
            root.add(node);
        }

        root.setUserObject(sumHours.toString());

        tree.setModel(new DefaultTreeModel(root));

        JPanel pnlLine = new JPanel();
        pnlLine.setLayout(new BorderLayout());

        JPanel pnlButton = new JPanel();
        pnlButton.setLayout(new BoxLayout(pnlButton, BoxLayout.LINE_AXIS));
        pnlButton.add(GUITools.getTinyButton(SYSConst.icon22delete, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //                EntityManager em = OPDE.createEM();
                //                try {
                //                    em.getTransaction().begin();
                //                    Rplan myRplan = em.merge(rplan);
                //                    em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                //                    Workinglog myWorkinglog = em.merge(workinglog);
                //                    em.remove(myWorkinglog);
                //                    myRplan.getWorkinglogs().remove(workinglog);
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

            }
        }));
        pnlLine.add(tree, BorderLayout.CENTER);
        pnlLine.add(pnlButton, BorderLayout.EAST);

        pnlLine.setAlignmentY(TOP_ALIGNMENT);

        return pnlLine;
    }

    void updateList() {
        pnlList.removeAll();

        if (!rplan.getWLogDetails().isEmpty()) {
            pnlList.setBackground(Color.WHITE);
        }
        pnlList.setOpaque(!rplan.getWLogDetails().isEmpty());

        ArrayList<WLogDetails> listBlockedDetails = new ArrayList<WLogDetails>();
        ArrayList<WLogDetails> listSingleDetails = new ArrayList<WLogDetails>();

        // find all wlogdetails which belong together
        for (WLogDetails wld : rplan.getWLogDetails()) {
            if (wld.getType() != WLogDetailsTools.ADDITIONAL) {
                listBlockedDetails.add(wld);
            } else {
                listSingleDetails.add(wld);
            }
        }

        pnlList.add(getLine(listBlockedDetails));


        scrl2.validate();
        scrl2.repaint();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        lblDate = new JLabel();
        lblEffectivePlan = new JLabel();
        cmbSymbol = new JComboBox();
        btnProcess = new JButton();
        scrl2 = new JScrollPane();
        pnlList = new JPanel();
        btnOK1 = new JToggleButton();
        scrollPane1 = new JScrollPane();
        pnlTimeClock = new JPanel();
        cmbHome = new JComboBox();
        button1 = new JButton();
        btnOK2 = new JToggleButton();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== panel1 ========
        {
            panel1.setBorder(LineBorder.createGrayLineBorder());
            panel1.setLayout(new FormLayout(
                    "60dlu, 2*(60dlu:grow), 2*(default, 100dlu)",
                    "2*(default)"));

            //---- lblDate ----
            lblDate.setText("03.06.14");
            lblDate.setFont(new Font("Arial", Font.BOLD, 16));
            lblDate.setHorizontalAlignment(SwingConstants.CENTER);
            lblDate.setBackground(new Color(204, 204, 255));
            lblDate.setOpaque(true);
            panel1.add(lblDate, CC.xywh(1, 1, 1, 2, CC.DEFAULT, CC.FILL));

            //---- lblEffectivePlan ----
            lblEffectivePlan.setText("text");
            panel1.add(lblEffectivePlan, CC.xywh(2, 1, 1, 2));
            panel1.add(cmbSymbol, CC.xy(3, 1, CC.DEFAULT, CC.FILL));

            //---- btnProcess ----
            btnProcess.setText(null);
            btnProcess.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/work.png")));
            btnProcess.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnProcessActionPerformed(e);
                }
            });
            panel1.add(btnProcess, CC.xy(4, 1, CC.DEFAULT, CC.FILL));

            //======== scrl2 ========
            {

                //======== pnlList ========
                {
                    pnlList.setLayout(new VerticalLayout());
                }
                scrl2.setViewportView(pnlList);
            }
            panel1.add(scrl2, CC.xywh(5, 1, 1, 2, CC.FILL, CC.FILL));

            //---- btnOK1 ----
            btnOK1.setText("1");
            btnOK1.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/leddarkyellow.png")));
            btnOK1.setFont(new Font("Arial", Font.BOLD, 16));
            btnOK1.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ledyellow.png")));
            panel1.add(btnOK1, CC.xy(6, 1, CC.DEFAULT, CC.FILL));

            //======== scrollPane1 ========
            {

                //======== pnlTimeClock ========
                {
                    pnlTimeClock.setLayout(new GridLayout());
                }
                scrollPane1.setViewportView(pnlTimeClock);
            }
            panel1.add(scrollPane1, CC.xywh(7, 1, 1, 2));
            panel1.add(cmbHome, CC.xy(3, 2, CC.DEFAULT, CC.FILL));

            //---- button1 ----
            button1.setText(null);
            button1.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_add.png")));
            panel1.add(button1, CC.xy(4, 2, CC.DEFAULT, CC.FILL));

            //---- btnOK2 ----
            btnOK2.setText("2");
            btnOK2.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/leddarkgreen.png")));
            btnOK2.setFont(new Font("Arial", Font.BOLD, 16));
            btnOK2.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ledgreen.png")));
            panel1.add(btnOK2, CC.xy(6, 2, CC.DEFAULT, CC.FILL));
        }
        add(panel1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel lblDate;
    private JLabel lblEffectivePlan;
    private JComboBox cmbSymbol;
    private JButton btnProcess;
    private JScrollPane scrl2;
    private JPanel pnlList;
    private JToggleButton btnOK1;
    private JScrollPane scrollPane1;
    private JPanel pnlTimeClock;
    private JComboBox cmbHome;
    private JButton button1;
    private JToggleButton btnOK2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
