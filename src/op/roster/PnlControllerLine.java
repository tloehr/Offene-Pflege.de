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
import op.threads.DisplayMessage;
import op.tools.GUITools;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;

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

    public PnlControllerLine(Rplan rplan, RosterParameters rosterParameters, ContractsParameterSet contractsParameterSet) {
        this.rplan = rplan;
        this.rosterParameters = rosterParameters;
        this.contractsParameterSet = contractsParameterSet;
//        effectiveSymbol =
//        actualSymbol = null;
        refDate = new LocalDate(rplan.getStart());
        initComponents();
        initPanel();
    }

    private void initPanel() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yy");
        listTimeClocks = TimeclockTools.getAllStartingOn(refDate, rplan.getOwner());
        lblDate.setText(sdf.format(refDate.toDate()));

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
        Vector<Vector> data = new Vector<Vector>();
        if (refDate.compareTo(new LocalDate()) <= 0) {
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            for (Timeclock timeclock : listTimeClocks) {
                Vector line = new Vector(3);
                line.add(df.format(timeclock.getBegin()));

                if (timeclock.isOpen()) {
                    line.add(">>>>>>>>");
                } else {
                    line.add(df.format(timeclock.getEnd()));
                }

                line.add(SYSTools.catchNull(timeclock.getText()));
                data.add(line);

            }
        }
        Vector header = new Vector(3);
        header.add(OPDE.lang.getString("dlglogin.timeclock.came"));
        header.add(OPDE.lang.getString("dlglogin.timeclock.gone"));
        header.add(OPDE.lang.getString("misc.msg.comment"));

        tblTimeclocks.setModel(new DefaultTableModel(data, header));
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
        btnOK1.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
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
        pnlLine.add(new JLabel(WLogDetailsTools.toPrettyString(wLogDetails)), BorderLayout.CENTER);
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

        for (WLogDetails wld : rplan.getWLogDetails()) {
            pnlList.add(getLine(wld));
        }

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
        panel2 = new JPanel();
        btnOK1 = new JToggleButton();
        btnOK2 = new JToggleButton();
        scrollPane1 = new JScrollPane();
        tblTimeclocks = new JTable();
        cmbHome = new JComboBox();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== panel1 ========
        {
            panel1.setBorder(LineBorder.createGrayLineBorder());
            panel1.setLayout(new FormLayout(
                    "60dlu, 60dlu:grow, default:grow, 2*(default, 130dlu)",
                    "default, $lgap, default"));

            //---- lblDate ----
            lblDate.setText("03.06.14");
            lblDate.setFont(new Font("Arial", Font.BOLD, 16));
            lblDate.setHorizontalAlignment(SwingConstants.CENTER);
            lblDate.setBackground(new Color(204, 204, 255));
            lblDate.setOpaque(true);
            panel1.add(lblDate, CC.xywh(1, 1, 1, 3, CC.DEFAULT, CC.FILL));

            //---- lblEffectivePlan ----
            lblEffectivePlan.setText("text");
            panel1.add(lblEffectivePlan, CC.xywh(2, 1, 1, 3));
            panel1.add(cmbSymbol, CC.xy(3, 1));

            //---- btnProcess ----
            btnProcess.setText(null);
            btnProcess.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/work.png")));
            btnProcess.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnProcessActionPerformed(e);
                }
            });
            panel1.add(btnProcess, CC.xywh(4, 1, 1, 3));

            //======== scrl2 ========
            {

                //======== pnlList ========
                {
                    pnlList.setLayout(new VerticalLayout());
                }
                scrl2.setViewportView(pnlList);
            }
            panel1.add(scrl2, CC.xywh(5, 1, 1, 3, CC.FILL, CC.FILL));

            //======== panel2 ========
            {
                panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));

                //---- btnOK1 ----
                btnOK1.setText("1");
                btnOK1.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/leddarkyellow.png")));
                btnOK1.setFont(new Font("Arial", Font.BOLD, 16));
                btnOK1.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ledyellow.png")));
                panel2.add(btnOK1);

                //---- btnOK2 ----
                btnOK2.setText("2");
                btnOK2.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/leddarkgreen.png")));
                btnOK2.setFont(new Font("Arial", Font.BOLD, 16));
                btnOK2.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ledgreen.png")));
                panel2.add(btnOK2);
            }
            panel1.add(panel2, CC.xywh(6, 1, 1, 3, CC.DEFAULT, CC.FILL));

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(tblTimeclocks);
            }
            panel1.add(scrollPane1, CC.xywh(7, 1, 1, 3));
            panel1.add(cmbHome, CC.xy(3, 3));
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
    private JPanel panel2;
    private JToggleButton btnOK1;
    private JToggleButton btnOK2;
    private JScrollPane scrollPane1;
    private JTable tblTimeclocks;
    private JComboBox cmbHome;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
