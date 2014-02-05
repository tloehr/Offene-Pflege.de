/*
 * Created by JFormDesigner on Tue Jan 14 14:51:47 CET 2014
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
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
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateTime;
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
import java.util.Collections;

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
    private boolean ignoreLEDEvent, selGreen, selYellow, selRed;

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

        rosterParameters.setComboBox(cmbSymbol, refDate);
        HomesTools.setComboBox(cmbHome);

        if (rplan.getActual() != null) {
            cmbSymbol.setSelectedItem(rosterParameters.getSymbol(rplan.getActual()));
            cmbHome.setSelectedItem(rplan.getHomeActual());
        } else {
            cmbSymbol.setSelectedIndex(0);
            cmbHome.setSelectedItem(rplan.getEffectiveHome());
        }

        cmbSymbol.setEnabled(!rplan.isLocked());
        cmbHome.setEnabled(!rplan.isLocked());


        selGreen = rplan.getCtrl1() == null && rplan.getCtrl2() == null;
        selYellow = rplan.getCtrl1() != null;
        selRed = rplan.getCtrl2() != null;
        resetLED();

        btnRed.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                btnGreenItemStateChanged(e);
            }
        });

        btnYellow.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                btnYellowItemStateChanged(e);
            }
        });

        btnGreen.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                btnRedItemStateChanged(e);
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

        pnlList.setBackground(Color.WHITE);
        pnlList.setOpaque(true);
        updateList();

        /***
         *      _____ _                 ____ _            _
         *     |_   _(_)_ __ ___   ___ / ___| | ___   ___| | __
         *       | | | | '_ ` _ \ / _ \ |   | |/ _ \ / __| |/ /
         *       | | | | | | | | |  __/ |___| | (_) | (__|   <
         *       |_| |_|_| |_| |_|\___|\____|_|\___/ \___|_|\_\
         *
         */
        pnlTimeClock.setBackground(Color.WHITE);
        pnlTimeClock.setOpaque(true);
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


    private void btnRedItemStateChanged(ItemEvent e) {
        if (ignoreLEDEvent) return;
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        if (rplan.getWLogDetails().isEmpty()) {
            // OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.noaccess"));
            resetLED();
            return;
        }
        if (!OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, PnlUsersWorklog.internalClassID)) {
            // OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.noaccess"));
            resetLED();
            return;
        }

        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            Rplan myRplan = em.merge(rplan);
            em.lock(myRplan, LockModeType.OPTIMISTIC);
            em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);

            if (myRplan.getCtrl1() == null) {
                myRplan.setCtrl1(em.merge(OPDE.getLogin().getUser()));
            }
            myRplan.setCtrl2(em.merge(OPDE.getLogin().getUser()));

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

        selGreen = false;
        selYellow = false;
        selRed = true;

        cmbSymbol.setEnabled(!rplan.isLocked());
        cmbHome.setEnabled(!rplan.isLocked());
    }

    private void btnGreenItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;

        if (rplan.getWLogDetails().isEmpty()) {
            resetLED();
            return;
        } // only if there are details to apply

        if (btnGreen.isSelected() && !OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, PnlUsersWorklog.internalClassID)) {
            resetLED();
            return; // only a manager may take back the green light
        }

        if (!OPDE.getAppInfo().isAllowedTo(InternalClassACL.USER1, PnlUsersWorklog.internalClassID) || !OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, PnlUsersWorklog.internalClassID)) {
            resetLED();
            return;
        }


        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            Rplan myRplan = em.merge(rplan);
            em.lock(myRplan, LockModeType.OPTIMISTIC);
            em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);

            myRplan.setCtrl1(null);
            myRplan.setCtrl2(null);

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
        selGreen = true;
        selYellow = false;
        selRed = false;

        cmbSymbol.setEnabled(!rplan.isLocked());
        cmbHome.setEnabled(!rplan.isLocked());
    }

    private void btnYellowItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        if (rplan.getWLogDetails().isEmpty()) {
            resetLED();
            return;
        } // only if there are details to apply
        if (btnGreen.isSelected() && !OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, PnlUsersWorklog.internalClassID)) {
            resetLED();
            return; // only a manager may take back the green light
        }

        if (!OPDE.getAppInfo().isAllowedTo(InternalClassACL.USER1, PnlUsersWorklog.internalClassID) || !OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, PnlUsersWorklog.internalClassID)) {
            resetLED();
            return;
        }

        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            Rplan myRplan = em.merge(rplan);
            em.lock(myRplan, LockModeType.OPTIMISTIC);
            em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);

            myRplan.setCtrl1(em.merge(OPDE.getLogin().getUser()));
            myRplan.setCtrl2(null);

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

//        btnOK2.setEnabled(e.getStateChange() == ItemEvent.SELECTED);

        selGreen = false;
        selYellow = true;
        selRed = false;

        cmbSymbol.setEnabled(!rplan.isLocked());
        cmbHome.setEnabled(!rplan.isLocked());
    }


    private void btnProcessActionPerformed(ActionEvent e) {

        if (rplan.isLocked()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.roster.controllerview.alreadyLocked"));
            return;
        }

        if (cmbSymbol.getSelectedItem() == null) {
            cmbSymbol.setSelectedItem(rosterParameters.getSymbol(rplan.getEffectiveSymbol()));
        }

        // no need to repeat the same operation twice
        if (rplan.getActual() != null) {
            if (rosterParameters.getSymbol(rplan.getActual()).equals(cmbSymbol.getSelectedItem())) {
                return;
            }
        }

        // PValue ? We need to know the exact time.
        if (((Symbol) cmbSymbol.getSelectedItem()).getCalc() == Symbol.PVALUE) {
            final JidePopup popupAdd = new JidePopup();
            popupAdd.setMovable(false);
            PnlAdditional pnlAdd = new PnlAdditional(refDate, new Closure() {
                @Override
                public void execute(Object o) {
                    popupAdd.hidePopup();

                    if (o == null) return;

                    Object[] objects = (Object[]) o;
                    DateTime from = (DateTime) objects[0];
                    DateTime to = (DateTime) objects[1];
                    BigDecimal hours = (BigDecimal) objects[2];
                    String text = objects[3].toString();

                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        Rplan myRplan = em.merge(rplan);
                        em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                        em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);

                        myRplan.setHomeActual(em.merge((Homes) cmbHome.getSelectedItem()));
                        myRplan.setActual(((Symbol) cmbSymbol.getSelectedItem()).getKey());

                        WLogDetails wlog = em.merge(new WLogDetails(hours, BigDecimal.ZERO, WLogDetailsTools.DAY1, myRplan));
                        if (from != null) wlog.setStart(from.toDate());
                        if (to != null) wlog.setStart(to.toDate());
                        wlog.setText(text);
                        myRplan.getWLogDetails().add(wlog);

                        em.getTransaction().commit();
                        rplan = myRplan;

                        updateList();
//                        btnOK1.setEnabled(true);

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
            });
            popupAdd.setContentPane(pnlAdd);
            popupAdd.removeExcludedComponent(pnlAdd);
            popupAdd.setDefaultFocusComponent(pnlAdd);

            popupAdd.setOwner(btnAdditional);
            GUITools.showPopup(popupAdd, SwingConstants.NORTH_EAST);
        } else { // ==============================================================================
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

                updateList();
//                btnOK1.setEnabled(true);

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
    }

    JPanel getLine(final WLogDetails wLogDetails) {

        sumHours = sumHours.add(wLogDetails.getHours());

        JPanel pnlLine = new JPanel();
        pnlLine.setOpaque(false);
        pnlLine.setLayout(new BorderLayout());

        JPanel pnlButton = new JPanel();
        pnlButton.setOpaque(false);
        pnlButton.setLayout(new BoxLayout(pnlButton, BoxLayout.LINE_AXIS));
        pnlButton.add(GUITools.getTinyButton(SYSConst.icon22delete, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rplan.isLocked()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.roster.controllerview.alreadyLocked"));
                    return;
                }

                EntityManager em = OPDE.createEM();
                try {
                    em.getTransaction().begin();
                    Rplan myRplan = em.merge(rplan);
                    em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                    WLogDetails myWLD = em.merge(wLogDetails);
                    em.remove(myWLD);
                    myRplan.getWLogDetails().remove(myWLD);
                    em.getTransaction().commit();
                    rplan = myRplan;
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
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(WLogDetailsTools.TYPES[wld.getType()] + ": " + SYSTools.roundScale2(wld.getHours()));
            root.add(node);
        }

        root.setUserObject(rosterParameters.toString(rplan.getActual(), rplan.getHomeActual()) + ": " + SYSTools.roundScale2(sumHours));


        tree.setModel(new DefaultTreeModel(root));

        JPanel pnlLine = new JPanel();
        pnlLine.setOpaque(false);
        pnlLine.setLayout(new BorderLayout());

        JPanel pnlButton = new JPanel(new GridLayout(1, 2));
        pnlButton.setOpaque(false);
        pnlButton.add(GUITools.getTinyButton(SYSConst.icon22delete, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (rplan.isLocked()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.roster.controllerview.alreadyLocked"));
                    return;
                }

                EntityManager em = OPDE.createEM();
                try {
                    em.getTransaction().begin();
                    Rplan myRplan = em.merge(rplan);
                    em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                    myRplan.setActual(null);
                    myRplan.setHomeActual(null);

                    // clean all existing automatic WLOGDETAILS
                    if (!myRplan.getWLogDetails().isEmpty()) {
                        ArrayList<WLogDetails> details2remove = new ArrayList();
                        for (WLogDetails wLogDetails : myRplan.getWLogDetails()) {
                            if (wLogDetails.getType() != WLogDetailsTools.ADDITIONAL) {
                                details2remove.add(wLogDetails);
                                em.remove(wLogDetails);
                            }
                        }
                        myRplan.getWLogDetails().removeAll(details2remove);
                    }

                    em.getTransaction().commit();

                    rplan = myRplan;
                    cmbSymbol.setSelectedItem(null);
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
        }));

        tree.collapseRow(0);
        pnlLine.add(tree, BorderLayout.CENTER);
        pnlLine.add(pnlButton, BorderLayout.EAST);

        pnlLine.setAlignmentY(TOP_ALIGNMENT);

        return pnlLine;
    }

    void updateList() {
        pnlList.removeAll();
        sumHours = BigDecimal.ZERO;


//        if (!rplan.getWLogDetails().isEmpty()) {
//            pnlList.setBackground(Color.WHITE);
//        }
//        pnlList.setOpaque(!rplan.getWLogDetails().isEmpty());

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
        Collections.sort(listBlockedDetails);
        Collections.sort(listSingleDetails);

        if (!listBlockedDetails.isEmpty()) {
            pnlList.add(getLine(listBlockedDetails));
        }

        for (WLogDetails wld : listSingleDetails) {
            pnlList.add(getLine(wld));
        }

        JLabel sumLabel = new JLabel(SYSTools.toHTMLForScreen(SYSConst.html_bold("Summe: " + SYSTools.roundScale2(sumHours))));
//        sumLabel.setOpaque(false);
        sumLabel.setBackground(Color.white);
        pnlList.add(sumLabel);

        scrl2.validate();
        scrl2.repaint();
    }

    private void btnAdditionalActionPerformed(ActionEvent e) {

        if (rplan.isLocked()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.roster.controllerview.alreadyLocked"));
            return;
        }

        final JidePopup popupAdd = new JidePopup();
        popupAdd.setMovable(false);
        PnlAdditional pnlAdd = new PnlAdditional(refDate, new Closure() {
            @Override
            public void execute(Object o) {
                popupAdd.hidePopup();

                if (o == null) return;

                Object[] objects = (Object[]) o;
                DateTime from = (DateTime) objects[0];
                DateTime to = (DateTime) objects[1];
                BigDecimal hours = (BigDecimal) objects[2];
                String text = objects[3].toString();

                EntityManager em = OPDE.createEM();
                try {
                    em.getTransaction().begin();
                    Rplan myRplan = em.merge(rplan);
                    em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                    em.lock(myRplan.getRoster(), LockModeType.OPTIMISTIC);


                    WLogDetails wlog = em.merge(new WLogDetails(hours, BigDecimal.ZERO, WLogDetailsTools.ADDITIONAL, myRplan));
                    if (from != null) wlog.setStart(from.toDate());
                    if (to != null) wlog.setStart(to.toDate());
                    wlog.setText(text);
                    myRplan.getWLogDetails().add(wlog);

                    em.getTransaction().commit();
                    rplan = myRplan;

                    updateList();
//                    btnOK1.setEnabled(true);

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
        });
        popupAdd.setContentPane(pnlAdd);
        popupAdd.removeExcludedComponent(pnlAdd);
        popupAdd.setDefaultFocusComponent(pnlAdd);

        popupAdd.setOwner(btnAdditional);
        GUITools.showPopup(popupAdd, SwingConstants.NORTH_EAST);
    }

    void resetLED() {
        ignoreLEDEvent = true;
        btnRed.setSelected(selGreen);

        btnYellow.setSelected(selYellow);
        btnGreen.setSelected(selRed);
        ignoreLEDEvent = false;
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        lblDate = new JLabel();
        lblEffectivePlan = new JLabel();
        cmbSymbol = new JComboBox();
        panel2 = new JPanel();
        btnProcess = new JButton();
        btnAdditional = new JButton();
        panel3 = new JPanel();
        btnRed = new JToggleButton();
        btnYellow = new JToggleButton();
        btnGreen = new JToggleButton();
        scrl2 = new JScrollPane();
        pnlList = new JPanel();
        scrollPane1 = new JScrollPane();
        pnlTimeClock = new JPanel();
        cmbHome = new JComboBox();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== panel1 ========
        {
            panel1.setBorder(LineBorder.createGrayLineBorder());
            panel1.setLayout(new FormLayout(
                    "60dlu, 2*(60dlu:grow), 25dlu, 2*(100dlu)",
                    "fill:default, default"));

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

            //======== panel2 ========
            {
                panel2.setLayout(new FormLayout(
                        "2*(default:grow)",
                        "2*(fill:default:grow)"));

                //---- btnProcess ----
                btnProcess.setText(null);
                btnProcess.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/work.png")));
                btnProcess.setContentAreaFilled(false);
                btnProcess.setBorderPainted(false);
                btnProcess.setBorder(null);
                btnProcess.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnProcess.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/work_inverse.png")));
                btnProcess.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnProcessActionPerformed(e);
                    }
                });
                panel2.add(btnProcess, CC.xy(1, 1));

                //---- btnAdditional ----
                btnAdditional.setText(null);
                btnAdditional.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_add.png")));
                btnAdditional.setContentAreaFilled(false);
                btnAdditional.setBorderPainted(false);
                btnAdditional.setBorder(null);
                btnAdditional.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnAdditional.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_add_inverse.png")));
                btnAdditional.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnAdditionalActionPerformed(e);
                    }
                });
                panel2.add(btnAdditional, CC.xy(1, 2));

                //======== panel3 ========
                {
                    panel3.setLayout(new BoxLayout(panel3, BoxLayout.PAGE_AXIS));

                    //---- btnRed ----
                    btnRed.setText(null);
                    btnRed.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/leddarkred.png")));
                    btnRed.setFont(new Font("Arial", Font.BOLD, 16));
                    btnRed.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ledred.png")));
                    btnRed.setContentAreaFilled(false);
                    btnRed.setBorderPainted(false);
                    btnRed.setBorder(null);
                    btnRed.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btnRed.setForeground(new Color(153, 255, 153));
                    btnRed.setHorizontalTextPosition(SwingConstants.CENTER);
                    panel3.add(btnRed);

                    //---- btnYellow ----
                    btnYellow.setText(null);
                    btnYellow.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/leddarkyellow.png")));
                    btnYellow.setFont(new Font("Arial", Font.BOLD, 16));
                    btnYellow.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ledyellow.png")));
                    btnYellow.setContentAreaFilled(false);
                    btnYellow.setBorderPainted(false);
                    btnYellow.setBorder(null);
                    btnYellow.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btnYellow.setHorizontalTextPosition(SwingConstants.CENTER);
                    btnYellow.setForeground(new Color(255, 255, 153));
                    panel3.add(btnYellow);

                    //---- btnGreen ----
                    btnGreen.setText(null);
                    btnGreen.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/leddarkgreen.png")));
                    btnGreen.setFont(new Font("Arial", Font.BOLD, 16));
                    btnGreen.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ledgreen.png")));
                    btnGreen.setContentAreaFilled(false);
                    btnGreen.setBorderPainted(false);
                    btnGreen.setBorder(null);
                    btnGreen.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btnGreen.setHorizontalTextPosition(SwingConstants.CENTER);
                    btnGreen.setForeground(Color.black);
                    panel3.add(btnGreen);
                }
                panel2.add(panel3, CC.xywh(2, 1, 1, 2));
            }
            panel1.add(panel2, CC.xywh(4, 1, 1, 2, CC.DEFAULT, CC.TOP));

            //======== scrl2 ========
            {

                //======== pnlList ========
                {
                    pnlList.setLayout(new VerticalLayout());
                }
                scrl2.setViewportView(pnlList);
            }
            panel1.add(scrl2, CC.xywh(5, 1, 1, 2, CC.FILL, CC.FILL));

            //======== scrollPane1 ========
            {

                //======== pnlTimeClock ========
                {
                    pnlTimeClock.setLayout(new VerticalLayout());
                }
                scrollPane1.setViewportView(pnlTimeClock);
            }
            panel1.add(scrollPane1, CC.xywh(6, 1, 1, 2));
            panel1.add(cmbHome, CC.xy(3, 2, CC.DEFAULT, CC.FILL));
        }
        add(panel1);

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(btnRed);
        buttonGroup1.add(btnYellow);
        buttonGroup1.add(btnGreen);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel lblDate;
    private JLabel lblEffectivePlan;
    private JComboBox cmbSymbol;
    private JPanel panel2;
    private JButton btnProcess;
    private JButton btnAdditional;
    private JPanel panel3;
    private JToggleButton btnRed;
    private JToggleButton btnYellow;
    private JToggleButton btnGreen;
    private JScrollPane scrl2;
    private JPanel pnlList;
    private JScrollPane scrollPane1;
    private JPanel pnlTimeClock;
    private JComboBox cmbHome;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
