package op.controlling;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import entity.prescription.PrescriptionTools;
import entity.qms.*;
import io.lamma.Date;
import io.lamma.Lamma4j;
import io.lamma.Recurrence;
import op.OPDE;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static io.lamma.LammaConversion.everyYear;

/**
 * Created by tloehr on 17.06.14.
 */
public class PnlQMSPlan extends CleanablePanel {
    public static final String internalClassID = "opde.controlling.qms.pnlqmsplan";
    CollapsiblePanes cpsMain;
    private HashMap<String, CollapsiblePane> cpMap;
    private ArrayList<Qmsplan> listQMSPlans;
    private int MAX_TEXT_LENGTH = 65;

    public PnlQMSPlan(ArrayList<Qmsplan> listQMSPlans) {
        this.listQMSPlans = listQMSPlans;
        cpMap = new HashMap<>();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        cpsMain = new CollapsiblePanes();
        add(new JScrollPane(cpsMain));
        reload();
    }

    public ArrayList<Qmsplan> getListQMSPlans() {
        return listQMSPlans;
    }

    @Override
    public void cleanup() {
        cpsMain.removeAll();
    }

    @Override
    public void reload() {

        for (Qmsplan qmsplan : listQMSPlans) {
            createCP4(qmsplan);
        }
        buildPanel();
    }

    @Override
    public String getInternalClassID() {
        return internalClassID;
    }

    private void buildPanel() {
        cpsMain.removeAll();
        cpsMain.setLayout(new JideBoxLayout(cpsMain, JideBoxLayout.Y_AXIS));
        for (Qmsplan qmsplan : listQMSPlans) {
            cpsMain.add(cpMap.get(qmsplan.getId() + ".qmsplan"));
        }
        cpsMain.addExpansion();
    }


    private CollapsiblePane createCP4(final Qmsplan qmsplan) {
        final String key = qmsplan.getId() + ".qmsplan";
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                // Bah!
            }
        }
        final CollapsiblePane cpPlan = cpMap.get(key);

        String title = "<html><font size=+1><b>" +
                qmsplan.getTitle() +
                "</b></font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //                    if (cpCat.isCollapsed() && !tbInactive.isSelected()  && !isEmpty(cat) && containsOnlyClosedNPs(cat)) {
                    //                        tbInactive.setSelected(true);
                    //                    }
                    cpPlan.setCollapsed(!cpPlan.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

        cpPlan.setTitleLabelComponent(cptitle.getMain());
        cpPlan.setSlidingDirection(SwingConstants.SOUTH);
//            cpPlan.setBackground(getColor(cat)[SYSConst.medium2]);
        cpPlan.setOpaque(true);
        cpPlan.setHorizontalAlignment(SwingConstants.LEADING);


        /***
         *      __  __
         *     |  \/  | ___ _ __  _   _
         *     | |\/| |/ _ \ '_ \| | | |
         *     | |  | |  __/ | | | |_| |
         *     |_|  |_|\___|_| |_|\__,_|
         *
         */
        final JButton btnMenu = new JButton(SYSConst.icon22menu);
        btnMenu.setPressedIcon(SYSConst.icon22Pressed);
        btnMenu.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnMenu.setAlignmentY(Component.TOP_ALIGNMENT);
        btnMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMenu.setContentAreaFilled(false);
        btnMenu.setBorder(null);
        btnMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JidePopup popup = new JidePopup();
                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                popup.setOwner(btnMenu);
                popup.removeExcludedComponent(btnMenu);
                JPanel pnl = getMenu(qmsplan);
                popup.getContentPane().add(pnl);
                popup.setDefaultFocusComponent(pnl);

                GUITools.showPopup(popup, SwingConstants.WEST);
            }
        });

        btnMenu.setEnabled(!qmsplan.isClosed());

        cptitle.getRight().add(btnMenu);

        cpPlan.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
//                JPanel pnlContent = new JPanel(new VerticalLayout());
//
//                int i = 0; // for zebra pattern
//                for (Qmsplan myQmsplan : listQMSPlans) {
//                    //                        if (!np.isClosed()) { // tbInactive.isSelected() ||
//                    JPanel pnl = createContent4(myQmsplan);
//                    pnl.setBackground(i % 2 == 0 ? Color.WHITE : Color.DARK_GRAY);
//                    pnl.setOpaque(true);
//                    pnlContent.add(pnl);
//                    i++;
//                    //                        }
//                }
                cpPlan.setContentPane(createContent4(qmsplan));


            }
        });

        if (!cpPlan.isCollapsed()) {
            cpPlan.setContentPane(createContent4(qmsplan));

        }

        return cpPlan;
    }


    private CollapsiblePane createCP4(final Qmssched qmssched) {
        final String key = qmssched.getId() + ".qmssched";
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                // Bah!
            }
        }
        final CollapsiblePane cpPlan = cpMap.get(key);

        String title = "<html><font size=+1><b>" +
                QmsschedTools.getAsHTML(qmssched) +
                "</b></font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //                    if (cpCat.isCollapsed() && !tbInactive.isSelected()  && !isEmpty(cat) && containsOnlyClosedNPs(cat)) {
                    //                        tbInactive.setSelected(true);
                    //                    }
                    cpPlan.setCollapsed(!cpPlan.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

        cpPlan.setTitleLabelComponent(cptitle.getMain());
        cpPlan.setSlidingDirection(SwingConstants.SOUTH);
        //            cpPlan.setBackground(getColor(cat)[SYSConst.medium2]);
        cpPlan.setOpaque(true);
        cpPlan.setHorizontalAlignment(SwingConstants.LEADING);


//           /***
//            *      __  __
//            *     |  \/  | ___ _ __  _   _
//            *     | |\/| |/ _ \ '_ \| | | |
//            *     | |  | |  __/ | | | |_| |
//            *     |_|  |_|\___|_| |_|\__,_|
//            *
//            */
//           final JButton btnMenu = new JButton(SYSConst.icon22menu);
//           btnMenu.setPressedIcon(SYSConst.icon22Pressed);
//           btnMenu.setAlignmentX(Component.RIGHT_ALIGNMENT);
//           btnMenu.setAlignmentY(Component.TOP_ALIGNMENT);
//           btnMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//           btnMenu.setContentAreaFilled(false);
//           btnMenu.setBorder(null);
//           btnMenu.addActionListener(new ActionListener() {
//               @Override
//               public void actionPerformed(ActionEvent e) {
//                   JidePopup popup = new JidePopup();
//                   popup.setMovable(false);
//                   popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//                   popup.setOwner(btnMenu);
//                   popup.removeExcludedComponent(btnMenu);
//                   JPanel pnl = getMenu(qmsplan);
//                   popup.getContentPane().add(pnl);
//                   popup.setDefaultFocusComponent(pnl);
//
//                   GUITools.showPopup(popup, SwingConstants.WEST);
//               }
//           });
//
//           btnMenu.setEnabled(!qmsplan.isClosed());
//
//           cptitle.getRight().add(btnMenu);

        cpPlan.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                //                JPanel pnlContent = new JPanel(new VerticalLayout());
                //
                //                int i = 0; // for zebra pattern
                //                for (Qmsplan myQmsplan : listQMSPlans) {
                //                    //                        if (!np.isClosed()) { // tbInactive.isSelected() ||
                //                    JPanel pnl = createContent4(myQmsplan);
                //                    pnl.setBackground(i % 2 == 0 ? Color.WHITE : Color.DARK_GRAY);
                //                    pnl.setOpaque(true);
                //                    pnlContent.add(pnl);
                //                    i++;
                //                    //                        }
                //                }
                cpPlan.setContentPane(createContent4(qmssched));


            }
        });

        if (!cpPlan.isCollapsed()) {
            cpPlan.setContentPane(createContent4(qmssched));

        }

        return cpPlan;
    }


    private JPanel createContent4(final Qmsplan qmsplan) {
        JPanel pnl = new JPanel(new VerticalLayout());
//        pnl.setLayout(new BoxLayout(pnl, BoxLayout.X_AXIS));

        String title = SYSTools.toHTMLForScreen(SYSConst.html_paragraph(QmsplanTools.getAsHTML(qmsplan)));
        pnl.add(new JLabel(title));

        for (Qmssched qmssched : qmsplan.getQmsschedules()) {
            pnl.add(createCP4(qmssched));
        }


        return pnl;
    }


    /**
     * this panel contains sub collapsible panes for every year with existing QMS (in the past) and 2 years in advance for possible future QMS.
     *
     * @param qmssched
     * @return
     */
    private JPanel createContent4(final Qmssched qmssched) {

        ArrayList<Qms> listQMS = new ArrayList<>(qmssched.getQmsList());
        Collections.sort(listQMS);

        JPanel pnlYear = new JPanel(new VerticalLayout());
        pnlYear.setOpaque(false);

        LocalDate now = new LocalDate();
        LocalDate from = listQMS.isEmpty() ? now : new LocalDate(listQMS.get(0).getTarget());
        LocalDate to = now.plusYears(2).dayOfYear().withMaximumValue();
        to = SYSCalendar.min(to, new LocalDate(qmssched.getQmsplan().getTo()));

        for (int year = from.getYear(); year <= to.getYear(); year++) {
            pnlYear.add(createCP4(year, qmssched));
        }

        return pnlYear;

    }

    private CollapsiblePane createCP4(final int year, final Qmssched qmssched) {
        ArrayList<Qms> listQMS = new ArrayList<>(qmssched.getQmsList());

        final String key = year + ".year" + qmssched.getId() + ".qmssched";
        synchronized (cpMap) {
            if (!cpMap.containsKey(key)) {
                cpMap.put(key, new CollapsiblePane());
                try {
                    cpMap.get(key).setCollapsed(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        }
        final CollapsiblePane cpYear = cpMap.get(key);

        String title = "<html><font size=+1><b>" +
                year +
                "</b>" +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpYear.setCollapsed(!cpYear.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

        GUITools.addExpandCollapseButtons(cpYear, cptitle.getRight());


        cpYear.setTitleLabelComponent(cptitle.getMain());
        cpYear.setSlidingDirection(SwingConstants.SOUTH);

        cpYear.setBackground(SYSConst.orange1[SYSConst.medium1]);
        cpYear.setOpaque(false);
        cpYear.setHorizontalAlignment(SwingConstants.LEADING);

        cpYear.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpYear.setContentPane(createContent4(year, qmssched));
            }
        });

        if (!cpYear.isCollapsed()) {
            cpYear.setContentPane(createContent4(year, qmssched));
        }


        return cpYear;
    }


    private JPanel createContent4(final int year, final Qmssched qmssched) {

//        ArrayList<Qms> listQMS = new ArrayList<>(qmssched.getQmsList());
//        Collections.sort(listQMS);

        JPanel pnlYear = new JPanel(new VerticalLayout());
        pnlYear.setOpaque(false);

        LocalDate ldYear = new LocalDate(year, 1, 1);

        LocalDate from = SYSCalendar.max(ldYear, new LocalDate(qmssched.getQmsplan().getFrom()));
        LocalDate to = SYSCalendar.min(SYSCalendar.eoy(ldYear), new LocalDate(qmssched.getQmsplan().getTo()));


        // reading and indexing the existing QMS for this Schedule
        ArrayList<Qms> listQMS = QmsTools.get(qmssched, ldYear);
        HashMap<LocalDate, Qms> mapQMS = new HashMap<>();
        for (Qms qms : listQMS) {
            mapQMS.put(new LocalDate(qms.getTarget()), qms);
        }


        // adding those who may be used in the future.
        ArrayList<Date> lstPotentialTargetDates = new ArrayList<>();
        for (Recurrence recurrence : QmsschedTools.getRecurrences(qmssched)) {
            lstPotentialTargetDates.addAll(new ArrayList(Lamma4j.sequence(SYSCalendar.toLammaDate(from.toDate()), SYSCalendar.toLammaDate(to.toDate()), recurrence)));
        }

        for (Date lammaDate : lstPotentialTargetDates) {
            if (!mapQMS.containsKey(SYSCalendar.toLocalDate(lammaDate))) {
                listQMS.add(new Qms(SYSCalendar.toLocalDate(lammaDate).toDate(), qmssched));
            }
        }

        Collections.sort(listQMS);


        for (Qms qms : listQMS) {
            pnlYear.add(createCP4(qms));
        }

        mapQMS.clear();

        return pnlYear;

    }

    private CollapsiblePane createCP4(final Qms qms) {

        final String key = qms.getId() + ".qms";
        synchronized (cpMap) {
            if (!cpMap.containsKey(key)) {
                cpMap.put(key, new CollapsiblePane());
                try {
                    cpMap.get(key).setCollapsed(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        }
//        final CollapsiblePane cpYear = cpMap.get(key);

        final CollapsiblePane cpYear = cpMap.get(key);

        cpYear.setCollapseOnTitleClick(false);


        ActionListener applyActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (qms.getState() != QmsTools.STATE_OPEN) {
                    return;
                }
                if (qms.getQmsplan().isClosed()) {
                    return;
                }

                EntityManager em = OPDE.createEM();
                try {
                    em.getTransaction().begin();
                    Qms myQms = em.merge(qms);
                    em.lock(myQms.getQmssched(), LockModeType.OPTIMISTIC);
                    em.lock(myQms.getQmsplan(), LockModeType.OPTIMISTIC);

                    myQms.setState(QmsTools.STATE_DONE);
                    myQms.setUser(em.merge(OPDE.getLogin().getUser()));
                    myQms.setActual(new java.util.Date());

                    em.getTransaction().commit();

                    cpMap.remove(qms);
                    createCP4(myQms);

                    buildPanel();

                } catch (OptimisticLockException ole) {
                    OPDE.warn(ole);
                    OPDE.warn(ole);
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                        OPDE.getMainframe().emptyFrame();
                        OPDE.getMainframe().afterLogin();
                    }
                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                } catch (RollbackException ole) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                        OPDE.getMainframe().emptyFrame();
                        OPDE.getMainframe().afterLogin();
                    }
                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    OPDE.fatal(e);
                } finally {
                    em.close();
                }


            }
        };


        String title = "<html><font size=+1>" +
                SYSTools.left(qms.getQmssched().getMeasure(), MAX_TEXT_LENGTH) +

                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, PnlControlling.internalClassID) ? applyActionListener : null);


        JLabel icon1 = new JLabel(QmsTools.getIcon(qms));
        icon1.setOpaque(false);


        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, PnlControlling.internalClassID)) {
            if (!qms.getQmsplan().isClosed()) {

                /***
                 *      _     _            _                _
                 *     | |__ | |_ _ __    / \   _ __  _ __ | |_   _
                 *     | '_ \| __| '_ \  / _ \ | '_ \| '_ \| | | | |
                 *     | |_) | |_| | | |/ ___ \| |_) | |_) | | |_| |
                 *     |_.__/ \__|_| |_/_/   \_\ .__/| .__/|_|\__, |
                 *                             |_|   |_|      |___/
                 */
                JButton btnApply = new JButton(SYSConst.icon22apply);
                btnApply.setPressedIcon(SYSConst.icon22applyPressed);
                btnApply.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnApply.setToolTipText(OPDE.lang.getString("nursingrecords.bhp.btnApply.tooltip"));
                btnApply.addActionListener(applyActionListener);
                btnApply.setContentAreaFilled(false);
                btnApply.setBorder(null);
                btnApply.setEnabled(qms.isOpen());
                cptitle.getRight().add(btnApply);


                /***
                 *      _     _         ____       __
                 *     | |__ | |_ _ __ |  _ \ ___ / _|_   _ ___  ___
                 *     | '_ \| __| '_ \| |_) / _ \ |_| | | / __|/ _ \
                 *     | |_) | |_| | | |  _ <  __/  _| |_| \__ \  __/
                 *     |_.__/ \__|_| |_|_| \_\___|_|  \__,_|___/\___|
                 *
                 */
                final JButton btnRefuse = new JButton(SYSConst.icon22cancel);
                btnRefuse.setPressedIcon(SYSConst.icon22cancelPressed);
                btnRefuse.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnRefuse.setContentAreaFilled(false);
                btnRefuse.setBorder(null);
                btnRefuse.setToolTipText(SYSTools.toHTMLForScreen(OPDE.lang.getString("nursingrecords.bhp.btnRefuse.tooltip")));
                btnRefuse.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if (!qms.isOpen()) {
                            return;
                        }


                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            Qms myQms = em.merge(qms);
                            em.lock(myQms.getQmssched(), LockModeType.OPTIMISTIC);
                            em.lock(myQms.getQmsplan(), LockModeType.OPTIMISTIC);

                            myQms.setState(QmsTools.STATE_REFUSED);
                            myQms.setUser(em.merge(OPDE.getLogin().getUser()));
                            myQms.setActual(new java.util.Date());

                            em.getTransaction().commit();

                            cpMap.remove(qms);
                            createCP4(myQms);

                            buildPanel();
                        } catch (OptimisticLockException ole) {
                            OPDE.warn(ole);
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                OPDE.getMainframe().emptyFrame();
                                OPDE.getMainframe().afterLogin();
                            }
                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                        } catch (Exception e) {
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            OPDE.fatal(e);
                        } finally {
                            em.close();
                        }


                    }
                });
                btnRefuse.setEnabled(qms.isOpen());
                cptitle.getRight().add(btnRefuse);


                /***
                 *      _     _         _____                 _
                 *     | |__ | |_ _ __ | ____|_ __ ___  _ __ | |_ _   _
                 *     | '_ \| __| '_ \|  _| | '_ ` _ \| '_ \| __| | | |
                 *     | |_) | |_| | | | |___| | | | | | |_) | |_| |_| |
                 *     |_.__/ \__|_| |_|_____|_| |_| |_| .__/ \__|\__, |
                 *                                     |_|        |___/
                 */
                final JButton btnEmpty = new JButton(SYSConst.icon22empty);
                btnEmpty.setPressedIcon(SYSConst.icon22emptyPressed);
                btnEmpty.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnEmpty.setContentAreaFilled(false);
                btnEmpty.setBorder(null);
                btnEmpty.setToolTipText(OPDE.lang.getString("nursingrecords.bhp.btnEmpty.tooltip"));
                btnEmpty.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if (qms.isOpen()) {
                            return;
                        }


                        EntityManager em = OPDE.createEM();
                        try {


                            em.getTransaction().begin();
                            Qms myQms = em.merge(qms);
                            em.lock(myQms.getQmssched(), LockModeType.OPTIMISTIC);
                            em.lock(myQms.getQmsplan(), LockModeType.OPTIMISTIC);
                            em.remove(myQms);
                            em.getTransaction().commit();

                            cpMap.remove(qms);
                            createCP4(new Qms(myQms.getTarget(), myQms.getQmssched()));

                            buildPanel();


                        } catch (OptimisticLockException ole) {
                            OPDE.warn(ole);
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                OPDE.getMainframe().emptyFrame();
                                OPDE.getMainframe().afterLogin();
                            }
                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                        } catch (Exception e) {
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            OPDE.fatal(e);
                        } finally {
                            em.close();
                        }

                    }
                });
                btnEmpty.setEnabled(!qms.isOpen());
                cptitle.getRight().add(btnEmpty);
            }

        }

        cpYear.setTitleLabelComponent(cptitle.getMain());
        cpYear.setSlidingDirection(SwingConstants.SOUTH);

        final JTextPane contentPane = new JTextPane();
        contentPane.setEditable(false);
        contentPane.setContentType("text/html");
        cpYear.setContentPane(contentPane);

        try {
            cpYear.setCollapsed(true);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }



        cpYear.setHorizontalAlignment(SwingConstants.LEADING);
        cpYear.setOpaque(false);
        return cpYear;

    }


    private JPanel getMenu(final Qmsplan qmsplan) {

        final JPanel pnlMenu = new JPanel(new VerticalLayout());
        long numQMS = 0l;//DFNTools.getNumDFNs(np);

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, PnlControlling.internalClassID)) {


            /***
             *               _ _ _
             *       ___  __| (_) |_
             *      / _ \/ _` | | __|
             *     |  __/ (_| | | |_
             *      \___|\__,_|_|\__|
             *
             */
            JButton btnEdit = GUITools.createHyperlinkButton("misc.commands.edit", SYSConst.icon22edit, null);
            btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgQMSPlan(qmsplan, new Closure() {
                        @Override
                        public void execute(Object qmsplan) {
                            if (qmsplan != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    Qmsplan myQMSPlan = (Qmsplan) em.merge(qmsplan);
                                    em.getTransaction().commit();
                                    listQMSPlans.remove(qmsplan);
                                    listQMSPlans.add(myQMSPlan);
                                    reload();
                                } catch (OptimisticLockException ole) {
                                    OPDE.warn(ole);
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                        OPDE.getMainframe().emptyFrame();
                                        OPDE.getMainframe().afterLogin();
                                    } else {
                                        reload();
                                    }
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                            }
                        }
                    });
                }
            });
            btnEdit.setEnabled(!qmsplan.isClosed() && numQMS == 0);
            pnlMenu.add(btnEdit);
        }

        /***
         *          _      _      _
         *       __| | ___| | ___| |_ ___
         *      / _` |/ _ \ |/ _ \ __/ _ \
         *     | (_| |  __/ |  __/ ||  __/
         *      \__,_|\___|_|\___|\__\___|
         *
         */
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.DELETE, PnlControlling.internalClassID)) {  // => ACL_MATRIX
            JButton btnDelete = GUITools.createHyperlinkButton("misc.commands.delete", SYSConst.icon22delete, null);
            btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgYesNo(OPDE.lang.getString("misc.questions.delete1") + "<br/><b>" + qmsplan.getTitle() + "</b><br/>" + OPDE.lang.getString("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    Qmsplan myQMSPlan = (Qmsplan) em.merge(qmsplan);
                                    em.remove(myQMSPlan);
                                    em.getTransaction().commit();


                                    // Refresh Display
                                    listQMSPlans.remove(qmsplan);
                                    reload();

                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getSuccessMessage(qmsplan.getTitle(), "deleted"));

                                } catch (OptimisticLockException ole) {
                                    OPDE.warn(ole);
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                        OPDE.getMainframe().emptyFrame();
                                        OPDE.getMainframe().afterLogin();
                                    }
                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                            }
                        }
                    });
                }
            });
            btnDelete.setEnabled(!qmsplan.isClosed() && numQMS == 0);
            pnlMenu.add(btnDelete);
        }


        JButton btnTmp = GUITools.createHyperlinkButton("shedule!", SYSConst.icon22calendar, null);
        btnTmp.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnTmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                LocalDate ld = new LocalDate(qmsplan.getFrom());

                Lamma4j.sequence(SYSCalendar.toLammaDate(qmsplan.getFrom()), SYSCalendar.toLammaDate(qmsplan.getTo()), everyYear());


            }
        });
        pnlMenu.add(btnTmp);


        return pnlMenu;
    }


}
