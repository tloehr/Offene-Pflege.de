/*
 * Created by JFormDesigner on Sat May 17 15:36:50 CEST 2014
 */

package op.training;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.staff.Training;
import entity.staff.Training2Users;
import entity.staff.TrainingTools;
import entity.system.Commontags;
import entity.system.CommontagsTools;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.care.sysfiles.DlgFiles;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.util.*;


/**
 * ACL
 * ============
 * EXECUTE
 * UPDATE
 * DELETE
 *
 * @author tloehr
 */
public class PnlTraining extends CleanablePanel {
    public static final String internalClassID = "opde.training";
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private Map<String, CollapsiblePane> cpMap;
    private Map<String, ArrayList<Training2Users>> userMap;
    private Map<String, Training> trainingMap;
    private Commontags filterTag;
    private JScrollPane jspMain;
    private CollapsiblePanes cpsMain;

    public PnlTraining(JScrollPane jspSearch) {
        this.jspSearch = jspSearch;
        filterTag = null;
        initPanel();
        reload();
    }

    private void initPanel() {
        jspMain = new JScrollPane();
        cpsMain = new CollapsiblePanes();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        jspMain.setViewportView(cpsMain);

        add(jspMain);
        userMap = Collections.synchronizedMap(new HashMap<String, ArrayList<Training2Users>>());
        cpMap = Collections.synchronizedMap(new HashMap<String, CollapsiblePane>());
        trainingMap = Collections.synchronizedMap(new HashMap<String, Training>());
        prepareSearchArea();
        OPDE.getDisplayManager().setMainMessage(internalClassID);
    }

    @Override
    public void cleanup() {
        synchronized (userMap) {
            SYSTools.clear(userMap);
        }
        synchronized (cpMap) {
            SYSTools.clear(cpMap);
        }
    }

    @Override
    public void reload() {

        cleanup();

        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

        SwingWorker worker = new SwingWorker() {
            Date max = null;

            @Override
            protected Object doInBackground() throws Exception {

                Pair<LocalDate, LocalDate> minmax = TrainingTools.getMinMax(filterTag);

                if (minmax != null) {
                    max = minmax.getSecond().toDate();
                    LocalDate start = minmax.getFirst().dayOfYear().withMinimumValue();
                    LocalDate end = minmax.getSecond().dayOfYear().withMinimumValue();

                    for (int year = end.getYear(); year >= start.getYear(); year--) {
                        createCP4(TrainingTools.getTrainings4(year, filterTag));
                    }
                }

                return null;
            }

            @Override
            protected void done() {
                buildPanel();
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);
            }
        };
        worker.execute();
    }


    private CollapsiblePane createCP4(final ArrayList<Training> listTrainingsThisYear) {
        if (listTrainingsThisYear.isEmpty()) return null;

        final int year = new LocalDate(listTrainingsThisYear.get(0).getStarting()).getYear();

        final String keyYear = Integer.toString(year) + ".year";
        synchronized (cpMap) {
            if (!cpMap.containsKey(keyYear)) {
                cpMap.put(keyYear, new CollapsiblePane());
                try {
                    cpMap.get(keyYear).setCollapsed(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        }

        final CollapsiblePane cpYear = cpMap.get(keyYear);

        String title = "<html><font size=+1>" +
                "<b>" + Integer.toString(year) + "</b>" +
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


        cpYear.setTitleLabelComponent(cptitle.getMain());
        cpYear.setSlidingDirection(SwingConstants.SOUTH);
        cpYear.setBackground(year % 2 == 0 ? SYSConst.purple1[SYSConst.medium3] : SYSConst.purple1[SYSConst.light3]);
        cpYear.setOpaque(true);

        cpYear.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                JPanel pnlContent = new JPanel(new VerticalLayout());

                for (Training training : listTrainingsThisYear) {
                    pnlContent.add(createCP4(training));
                }

                cpYear.setContentPane(pnlContent);

            }
        });

        if (!cpYear.isCollapsed()) {
            JPanel pnlContent = new JPanel(new VerticalLayout());

            for (Training training : listTrainingsThisYear) {
                pnlContent.add(createCP4(training));
            }

            cpYear.setContentPane(pnlContent);
            cpYear.setOpaque(false);
        }

        cpYear.setHorizontalAlignment(SwingConstants.LEADING);
        cpYear.setOpaque(false);

        return cpYear;
    }


    private CollapsiblePane createCP4(final Training training) {
        final String key = training.getId() + ".trainid";


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
        final CollapsiblePane cpTraining = cpMap.get(key);

        synchronized (trainingMap) {
            trainingMap.put(key, training);
        }

        String title = "<font size=+1><b>" +
                training.getTitle() + ", " + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(training.getStarting()) +
                "</b></font><p>";

        for (Commontags ctag : training.getCommontags()) {
            title += SYSConst.html_16x16_tagPurple_internal + "&nbsp;" + ctag.getText() + "&nbsp;";
        }


        title += "</p>";


        DefaultCPTitle cptitle = new DefaultCPTitle(SYSTools.toHTMLForScreen(title), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpTraining.setCollapsed(!cpTraining.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });


        if (!training.getAttachedFilesConnections().isEmpty()) {
            /***
             *      _     _         _____ _ _
             *     | |__ | |_ _ __ |  ___(_) | ___  ___
             *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
             *     | |_) | |_| | | |  _| | | |  __/\__ \
             *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
             *
             */
            final JButton btnFiles = new JButton(Integer.toString(training.getAttachedFilesConnections().size()), SYSConst.icon22greenStar);
            btnFiles.setToolTipText(SYSTools.xx("misc.btnfiles.tooltip"));
            btnFiles.setForeground(Color.BLUE);
            btnFiles.setHorizontalTextPosition(SwingUtilities.CENTER);
            btnFiles.setFont(SYSConst.ARIAL18BOLD);
            btnFiles.setPressedIcon(SYSConst.icon22Pressed);
            btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnFiles.setAlignmentY(Component.TOP_ALIGNMENT);
            btnFiles.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnFiles.setContentAreaFilled(false);
            btnFiles.setBorder(null);

            btnFiles.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    Closure fileHandleClosure = OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID) ? null : new Closure() {
                        @Override
                        public void execute(Object o) {
                            EntityManager em = OPDE.createEM();
                            final Training myTraining = em.find(Training.class, training.getId());
                            em.close();

                            final String keyYear = Integer.toString(new DateTime(myTraining.getStarting()).getYear()) + ".year";

                            if (!cpMap.containsKey(keyYear)) {
                                reload();
                            } else {
                                createCP4(myTraining);
                                buildPanel();
                            }
                        }
                    };
                    new DlgFiles(training, fileHandleClosure);
                }
            });
            btnFiles.setEnabled(OPDE.isFTPworking());
            cptitle.getRight().add(btnFiles);
        }


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
                JPanel pnl = getMenu(training);
                popup.getContentPane().add(pnl);
                popup.setDefaultFocusComponent(pnl);

                GUITools.showPopup(popup, SwingConstants.WEST);
            }
        });
        cptitle.getRight().add(btnMenu);


        cpTraining.setTitleLabelComponent(cptitle.getMain());
        cpTraining.setSlidingDirection(SwingConstants.SOUTH);
        cpTraining.setBackground(SYSConst.purple1[SYSConst.light2]);
        cpTraining.setOpaque(true);
        cpTraining.setHorizontalAlignment(SwingConstants.LEADING);

        cpTraining.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpTraining.setContentPane(createContentPanel4(training));
            }
        });

        if (!cpTraining.isCollapsed()) {
            cpTraining.setContentPane(createContentPanel4(training));
        }


        return cpTraining;
    }


    private void fillUserPanel(final JPanel userPanel, final String key) {
        userPanel.removeAll();


        if (userMap.get(key).isEmpty()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    userPanel.setLayout(new FlowLayout());
                    userPanel.revalidate();
                }
            });
            return;
        }

//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                userPanel.setLayout(new GridLayout(userMap.get(key).size() / 3, 3, 5, 5));
//
//                for (Users usertmp : userMap.get(key)) {
//                    JCheckBox cb = new JCheckBox(usertmp.getFullname());
//                    final Users user;
//
//                    synchronized (trainingMap) {
//                        EntityManager em = OPDE.createEM();
//                        usertmp = em.merge(usertmp);
//                        em.refresh(usertmp);
//                        cb.setSelected(usertmp.getTrainings().contains(trainingMap.get(key)));
//                        em.close();
//                        user = usertmp;
//                    }
//
//                    cb.addItemListener(new ItemListener() {
//                        @Override
//                        public void itemStateChanged(ItemEvent e) {
//                            EntityManager em = OPDE.createEM();
//
//                            try {
//                                em.getTransaction().begin();
//
//
//                                Users myUser = em.merge(user);
//                                em.lock(myUser, LockModeType.OPTIMISTIC);
//                                Training myTraining = null;
//                                synchronized (trainingMap) {
//                                    myTraining = em.merge(trainingMap.get(key));
//                                }
//                                em.lock(myTraining, LockModeType.OPTIMISTIC);
//
//                                if (e.getStateChange() == ItemEvent.SELECTED) {
//                                    myUser.getTrainings().add(myTraining);
//                                    myTraining.getAttendees().add(user);
//                                } else {
//                                    myUser.getTrainings().remove(myTraining);
//                                    myTraining.getAttendees().remove(user);
//                                }
//
//                                em.getTransaction().commit();
//                                synchronized (trainingMap) {
//                                    trainingMap.put(key, myTraining);
//                                    ArrayList<Training2Users> listUsers = new ArrayList<>(trainingMap.get(key).getAttendees());
//                                    Collections.sort(listUsers, new Comparator<Training2Users>() {
//                                        @Override
//                                        public int compare(Training2Users o1, Training2Users o2) {
//                                            return o1.getAttendee().compareTo(o2.getAttendee());
//                                        }
//                                    });
//                                    synchronized (userMap) {
//                                        userMap.put(key, listUsers);
//                                    }
//                                }
//
//                                if (myUser.equals(OPDE.getLogin().getUser())) {
//                                    OPDE.getLogin().setUser(myUser);
//                                }
//                            } catch (OptimisticLockException ole) {
//                                OPDE.warn(ole);
//                                if (em.getTransaction().isActive()) {
//                                    em.getTransaction().rollback();
//                                }
//                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                    OPDE.getMainframe().emptyFrame();
//                                    OPDE.getMainframe().afterLogin();
//                                } else {
//                                    reload();
//                                }
//                            } catch (RollbackException ole) {
//                                if (em.getTransaction().isActive()) {
//                                    em.getTransaction().rollback();
//                                }
//                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                    OPDE.getMainframe().emptyFrame();
//                                    OPDE.getMainframe().afterLogin();
//                                }
//                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                            } catch (Exception exc) {
//                                if (em.getTransaction().isActive()) {
//                                    em.getTransaction().rollback();
//                                }
//                                OPDE.fatal(exc);
//                            } finally {
//                                em.close();
//                            }
//                        }
//                    });
//
//                    userPanel.add(cb);
//                }
//                userPanel.revalidate();
//            }
//        });

    }


    private JPanel createContentPanel4(final Training training) {


        final JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        contentPanel.setOpaque(false);

        contentPanel.add(new PnlTrainingEditor(training));



        return contentPanel;
    }


    private void buildPanel() {
        cpsMain.removeAll();
        cpsMain.setLayout(new JideBoxLayout(cpsMain, JideBoxLayout.Y_AXIS));

        synchronized (cpMap) {
            Pair<LocalDate, LocalDate> minmax = TrainingTools.getMinMax(filterTag);
            if (minmax != null) {
                LocalDate start = minmax.getFirst().dayOfYear().withMinimumValue();
                LocalDate end = minmax.getSecond().dayOfYear().withMinimumValue();
                for (int year = end.getYear(); year >= start.getYear(); year--) {
                    final String keyYear = Integer.toString(year) + ".year";
                    if (cpMap.containsKey(keyYear)) {
                        cpsMain.add(cpMap.get(keyYear));
                    }
                }
            }
        }
        cpsMain.addExpansion();
    }

    private void prepareSearchArea() {

        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(5));
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(SYSTools.xx(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

        GUITools.addAllComponents(mypanel, addCommands());
        GUITools.addAllComponents(mypanel, addFilters());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();


    }

    private JPanel getMenu(final Training training) {

        JPanel pnlMenu = new JPanel(new VerticalLayout());

//        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
//            /***
//             *      _____    _ _ _
//             *     | ____|__| (_) |_
//             *     |  _| / _` | | __|
//             *     | |__| (_| | | |_
//             *     |_____\__,_|_|\__|
//             *
//             */
//            final JButton btnEdit = GUITools.createHyperlinkButton("misc.msg.edit", SYSConst.icon22edit3, null);
//            btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            btnEdit.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    new DlgTraining(training, new Closure() {
//                        @Override
//                        public void execute(Object editedTraining) {
//                            if (editedTraining != null) {
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    final Training myTraining = (Training) em.merge(editedTraining);
//                                    em.getTransaction().commit();
//
//                                    final String keyYear = Integer.toString(new DateTime(myTraining.getStarting()).getYear()) + ".year";
//
//                                    if (!cpMap.containsKey(keyYear)) {
//                                        reload();
//                                    } else {
//                                        createCP4(myTraining);
//                                        buildPanel();
//                                    }
//                                } catch (OptimisticLockException ole) {
//                                    OPDE.warn(ole);
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
//                                    } else {
//                                        reload();
//                                    }
//                                } catch (Exception e) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//                            }
//                        }
//                    });
//                }
//            });
//
//            pnlMenu.add(btnEdit);
//        }


        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.DELETE, internalClassID)) {

            /***
             *      ____       _      _
             *     |  _ \  ___| | ___| |_ ___
             *     | | | |/ _ \ |/ _ \ __/ _ \
             *     | |_| |  __/ |  __/ ||  __/
             *     |____/ \___|_|\___|\__\___|
             *
             */
            final JButton btnDelete = GUITools.createHyperlinkButton("misc.commands.delete", SYSConst.icon22delete, null);
            btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><i>" + training.getTitle() + ", " + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(training.getStarting()) + "</i><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                        @Override
                        public void execute(Object answer) {
                            if (answer.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    Training myTraining = em.merge(training);

                                    for (Training2Users training2Users : myTraining.getAttendees()) {
                                        em.remove(em.merge(training2Users));
                                    }

                                    em.remove(myTraining);
                                    em.getTransaction().commit();

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
            //            btnDelete.setEnabled(NReportTools.isChangeable(nreport));
            pnlMenu.add(btnDelete);
        }


        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {

            /***
             *      _     _         _____ _ _
             *     | |__ | |_ _ __ |  ___(_) | ___  ___
             *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
             *     | |_) | |_| | | |  _| | | |  __/\__ \
             *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
             *
             */
            final JButton btnFiles = GUITools.createHyperlinkButton("misc.btnfiles.tooltip", SYSConst.icon22attach, null);
            btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnFiles.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    Closure fileHandleClosure = new Closure() {
                        @Override
                        public void execute(Object o) {
                            EntityManager em = OPDE.createEM();
                            final Training myTraining = em.find(Training.class, training.getId());
                            em.close();

                            final String keyYear = Integer.toString(new DateTime(myTraining.getStarting()).getYear()) + ".year";

                            if (!cpMap.containsKey(keyYear)) {
                                reload();
                            } else {
                                createCP4(myTraining);
                                buildPanel();
                            }

                        }
                    };
                    new DlgFiles(training, fileHandleClosure);
                }
            });
            btnFiles.setEnabled(OPDE.isFTPworking());
            pnlMenu.add(btnFiles);


        }
        return pnlMenu;
    }


    private java.util.List<Component> addCommands() {
        java.util.List<Component> list = new ArrayList<Component>();

//        /***
//         *      _   _
//         *     | \ | | _____      __
//         *     |  \| |/ _ \ \ /\ / /
//         *     | |\  |  __/\ V  V /
//         *     |_| \_|\___| \_/\_/
//         *
//         */
//        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
//            JideButton addButton = GUITools.createHyperlinkButton(SYSTools.xx("misc.commands.new"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    new DlgTraining(new Training(), new Closure() {
//                        @Override
//                        public void execute(Object training) {
//                            if (training != null) {
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    final Training myTraining = (Training) em.merge(training);
//                                    em.getTransaction().commit();
//
//                                    final String keyYear = Integer.toString(new DateTime(myTraining.getStarting()).getYear()) + ".year";
//
//                                    if (!cpMap.containsKey(keyYear)) {
//                                        reload();
//                                    } else {
//                                        filterTag = null;
//                                        createCP4(TrainingTools.getTrainings4(new LocalDate(myTraining.getStarting()).getYear(), filterTag));
//                                        buildPanel();
//                                    }
//                                } catch (OptimisticLockException ole) {
//                                    OPDE.warn(ole);
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
//                                    } else {
//                                        reload();
//                                    }
//                                } catch (Exception e) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//                            }
//                        }
//                    });
//                }
//            });
//            list.add(addButton);
//        }
        return list;
    }


    private java.util.List<Component> addFilters() {
        java.util.List<Component> list = new ArrayList<Component>();

        ArrayList<Commontags> listTags = CommontagsTools.getAllUsedInTrainings();
        if (!listTags.isEmpty()) {

            JPanel pnlTags = new JPanel();
            pnlTags.setLayout(new BoxLayout(pnlTags, BoxLayout.PAGE_AXIS));
            pnlTags.setOpaque(false);

            final JButton btnReset = GUITools.createHyperlinkButton("misc.commands.resetFilter", SYSConst.icon16tagPurpleDelete4, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    filterTag = null;
                    reload();
                }
            });
            pnlTags.add(btnReset, RiverLayout.LEFT);


            for (final Commontags commontag : listTags) {
                final JButton btnTag = GUITools.createHyperlinkButton(commontag.getText(), SYSConst.icon16tagPurple, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        filterTag = commontag;
                        reload();
                    }
                });
                pnlTags.add(btnTag);

            }
            list.add(pnlTags);
        }


        return list;
    }


    @Override
    public String getInternalClassID() {
        return null;
    }


}
