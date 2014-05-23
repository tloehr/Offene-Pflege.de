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
import entity.staff.TrainingTools;
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
 * @author Torsten Löhr
 */
public class PnlTraining extends CleanablePanel {
    public static final String internalClassID = "opde.training";
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private Map<String, CollapsiblePane> cpMap;
    private Map<String, ArrayList<Users>> userMap;
    private Map<String, Training> trainingMap;

    public PnlTraining(JScrollPane jspSearch) {
        initComponents();
        this.jspSearch = jspSearch;
        initPanel();
        reload();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jspMain = new JScrollPane();
        cpsMain = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspMain ========
        {
            jspMain.setViewportView(cpsMain);
        }
        add(jspMain);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void initPanel() {
        userMap = Collections.synchronizedMap(new HashMap<String, ArrayList<Users>>());
        cpMap = Collections.synchronizedMap(new HashMap<String, CollapsiblePane>());
        trainingMap = Collections.synchronizedMap(new HashMap<String, Training>());
        prepareSearchArea();
        OPDE.getDisplayManager().setMainMessage(internalClassID);

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void reload() {
        synchronized (userMap) {
            SYSTools.clear(userMap);
        }
        synchronized (cpMap) {
            SYSTools.clear(cpMap);
        }
        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

        SwingWorker worker = new SwingWorker() {
            Date max = null;

            @Override
            protected Object doInBackground() throws Exception {

                Pair<LocalDate, LocalDate> minmax = TrainingTools.getMinMax();

                if (minmax != null) {
                    max = minmax.getSecond().toDate();
                    LocalDate start = minmax.getFirst().dayOfYear().withMinimumValue();
                    LocalDate end = minmax.getSecond().dayOfYear().withMinimumValue();
                    for (int year = end.getYear(); year >= start.getYear(); year--) {
                        createCP4Year(year);
                    }
                }

                return null;
            }

            @Override
            protected void done() {

                buildPanel();
//                initPhase = false;
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);

            }
        };
        worker.execute();
    }


    private CollapsiblePane createCP4Year(final int year) {
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

                for (Training training : TrainingTools.getTrainings4(year)) {
                    pnlContent.add(createCP4(training));
                }

                cpYear.setContentPane(pnlContent);

            }
        });
        //        cpYear.setBackground(getColor(vtype, SYSConst.light4));

        if (!cpYear.isCollapsed()) {
            JPanel pnlContent = new JPanel(new VerticalLayout());

            for (Training training : TrainingTools.getTrainings4(year)) {
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

        String title = "<html><font size=+1><b>" +
                training.getTitle() + ", " + DateFormat.getDateInstance(DateFormat.SHORT).format(training.getDate()) +
                "</b>" +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpTraining.setCollapsed(!cpTraining.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });


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
                cpTraining.setContentPane(createContentPanel4(key));
            }
        });
//
//           if (!cpTraining.isCollapsed()) {
//               cpTraining.setContentPane(createContentPanel4Month(month));
//           }


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

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                userPanel.setLayout(new GridLayout(userMap.get(key).size() / 3, 3, 5, 5));

                for (final Users user : userMap.get(key)) {
                    JCheckBox cb = new JCheckBox(user.getFullname());
                    cb.setSelected(user.getTrainings().contains(trainingMap.get(key)));

                    cb.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            EntityManager em = OPDE.createEM();

                            try {
                                em.getTransaction().begin();

                                Users myUser = em.merge(user);
                                em.lock(myUser, LockModeType.OPTIMISTIC);
                                Training myTraining = em.merge(trainingMap.get(key));
                                em.lock(myTraining, LockModeType.OPTIMISTIC);

                                if (e.getStateChange() == ItemEvent.SELECTED) {
                                    myUser.getTrainings().add(myTraining);
                                    myTraining.getAttendees().add(user);
                                } else {
                                    myUser.getTrainings().remove(myTraining);
                                    myTraining.getAttendees().remove(user);
                                }

                                trainingMap.put(key, myTraining);
                                OPDE.getLogin().setUser(myUser);

                                em.getTransaction().commit();
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
                            } catch (RollbackException ole) {
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
                                }
                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                            } catch (Exception exc) {
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                OPDE.fatal(exc);
                            } finally {
                                em.close();
                            }
                        }
                    });

                    userPanel.add(cb);
                }
                userPanel.revalidate();
            }
        });


    }


    private JPanel createContentPanel4(final String key) {

        final JPanel centerPanel = new JPanel();

        final JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setOpaque(false);

        JPanel northernPanel = new JPanel();
        northernPanel.setLayout(new BoxLayout(northernPanel, BoxLayout.LINE_AXIS));
        JLabel lbl = new JLabel("Mitarbeiter[innen] zeigen:");

        ButtonGroup bg = new ButtonGroup();
        JToggleButton btnAssigned = new JToggleButton(SYSTools.xx("opde.training.assignedOnly"));
        JToggleButton btnActive = new JToggleButton(SYSTools.xx("opde.training.assignedAndActive"));
        JToggleButton btnAllUsers = new JToggleButton(SYSTools.xx("opde.training.all"));

        bg.add(btnAssigned);
        bg.add(btnActive);
        bg.add(btnAllUsers);

        btnAssigned.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        centerPanel.removeAll();
                        synchronized (userMap) {
                            ArrayList<Users> listUsers = new ArrayList<Users>(trainingMap.get(key).getAttendees());
                            Collections.sort(listUsers);
                            userMap.put(key, listUsers);
                        }
                        fillUserPanel(centerPanel, key);
                    }
                });


            }
        });

        btnActive.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                centerPanel.removeAll();
                synchronized (userMap) {
                    HashSet<Users> setUsers = new HashSet<Users>(trainingMap.get(key).getAttendees());
                    setUsers.addAll(UsersTools.getUsers(false));
                    ArrayList<Users> listUsers = new ArrayList<Users>(setUsers);
                    Collections.sort(listUsers);
                    userMap.put(key, listUsers);
                }
                fillUserPanel(centerPanel, key);
            }
        });

        btnAllUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                centerPanel.removeAll();
                synchronized (userMap) {
                    userMap.put(key, UsersTools.getUsers(true));
                }
                fillUserPanel(centerPanel, key);
            }
        });

        northernPanel.add(lbl);
        northernPanel.add(btnAssigned);
        northernPanel.add(btnActive);
        northernPanel.add(btnAllUsers);

        btnAssigned.doClick();

        contentPanel.add(northernPanel, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);


        return contentPanel;
    }


    private void buildPanel() {
        cpsMain.removeAll();
        cpsMain.setLayout(new JideBoxLayout(cpsMain, JideBoxLayout.Y_AXIS));

        synchronized (cpMap) {
            Pair<LocalDate, LocalDate> minmax = TrainingTools.getMinMax();
            if (minmax != null) {
                LocalDate start = minmax.getFirst().dayOfYear().withMinimumValue();
                LocalDate end = minmax.getSecond().dayOfYear().withMinimumValue();
                for (int year = end.getYear(); year >= start.getYear(); year--) {
                    final String keyYear = Integer.toString(year) + ".year";

                    cpsMain.add(cpMap.get(keyYear));
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

        CollapsiblePane searchPane = new CollapsiblePane(OPDE.lang.getString(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

        GUITools.addAllComponents(mypanel, addCommands());
//           GUITools.addAllComponents(mypanel, addFilters());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();


    }

    private JPanel getMenu(final Training training) {

        JPanel pnlMenu = new JPanel(new VerticalLayout());

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            /***
             *      _____    _ _ _
             *     | ____|__| (_) |_
             *     |  _| / _` | | __|
             *     | |__| (_| | | |_
             *     |_____\__,_|_|\__|
             *
             */
            final JButton btnEdit = GUITools.createHyperlinkButton("misc.msg.edit", SYSConst.icon22edit3, null);
            btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
//                    new DlgReport(nreport.clone(), new Closure() {
//                        @Override
//                        public void execute(Object o) {
//                            if (o != null) {
//
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                    final NReport newReport = em.merge((NReport) o);
//                                    NReport oldReport = em.merge(nreport);
//                                    em.lock(oldReport, LockModeType.OPTIMISTIC);
//                                    newReport.setReplacementFor(oldReport);
//
//                                    for (SYSNR2FILE oldAssignment : oldReport.getAttachedFilesConnections()) {
//                                        em.remove(oldAssignment);
//                                    }
//                                    oldReport.getAttachedFilesConnections().clear();
//                                    for (SYSNR2PROCESS oldAssignment : oldReport.getAttachedQProcessConnections()) {
//                                        em.remove(oldAssignment);
//                                    }
//                                    oldReport.getAttachedQProcessConnections().clear();
//
//                                    oldReport.setEditedBy(em.merge(OPDE.getLogin().getUser()));
//                                    oldReport.setEditDate(new Date());
//                                    oldReport.setReplacedBy(newReport);
//
//                                    em.getTransaction().commit();
//
//                                    final String keyNewDay = DateFormat.getDateInstance().format(newReport.getPit());
//                                    final String keyOldDay = DateFormat.getDateInstance().format(oldReport.getPit());
//
//                                    synchronized (contentmap) {
//                                        contentmap.remove(keyNewDay);
//                                        contentmap.remove(keyOldDay);
//                                    }
//                                    synchronized (linemap) {
//                                        linemap.remove(oldReport);
//                                    }
//
//                                    synchronized (valuecache) {
//                                        valuecache.get(keyOldDay).remove(nreport);
//                                        valuecache.get(keyOldDay).add(oldReport);
//                                        Collections.sort(valuecache.get(keyOldDay));
//
//                                        if (valuecache.containsKey(keyNewDay)) {
//                                            valuecache.get(keyNewDay).add(newReport);
//                                            Collections.sort(valuecache.get(keyNewDay));
//                                        }
//                                    }
//
//                                    createCP4Day(new DateMidnight(oldReport.getPit()));
//                                    createCP4Day(new DateMidnight(newReport.getPit()));
//
//                                    buildPanel();
//                                    GUITools.scroll2show(jspReports, cpMap.get(keyNewDay), cpsReports, new Closure() {
//                                        @Override
//                                        public void execute(Object o) {
//                                            GUITools.flashBackground(linemap.get(newReport), Color.YELLOW, 2);
//                                        }
//                                    });
//                                } catch (OptimisticLockException ole) { OPDE.warn(ole);
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
//                                    } else {
//                                        reloadDisplay(true);
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
                }
            });
//            btnEdit.setEnabled(NReportTools.isChangeable(nreport));
            pnlMenu.add(btnEdit);
            /***
             *      ____       _      _
             *     |  _ \  ___| | ___| |_ ___
             *     | | | |/ _ \ |/ _ \ __/ _ \
             *     | |_| |  __/ |  __/ ||  __/
             *     |____/ \___|_|\___|\__\___|
             *
             */
            final JButton btnDelete = GUITools.createHyperlinkButton("opde.training.btnDelete.tooltip", SYSConst.icon22delete, null);
            btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
//                    new DlgYesNo(OPDE.lang.getString("misc.questions.delete1") + "<br/><i>" + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(nreport.getPit()) + "</i><br/>" + OPDE.lang.getString("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
//                        @Override
//                        public void execute(Object answer) {
//                            if (answer.equals(JOptionPane.YES_OPTION)) {
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                    final NReport delReport = em.merge(nreport);
//                                    em.lock(delReport, LockModeType.OPTIMISTIC);
//                                    delReport.setDeletedBy(em.merge(OPDE.getLogin().getUser()));
//                                    for (SYSNR2FILE oldAssignment : delReport.getAttachedFilesConnections()) {
//                                        em.remove(oldAssignment);
//                                    }
//                                    delReport.getAttachedFilesConnections().clear();
//                                    for (SYSNR2PROCESS oldAssignment : delReport.getAttachedQProcessConnections()) {
//                                        em.remove(oldAssignment);
//                                    }
//                                    delReport.getAttachedQProcessConnections().clear();
//                                    em.getTransaction().commit();
//
//                                    final String keyDay = DateFormat.getDateInstance().format(delReport.getPit());
//
//
//                                    synchronized (contentmap) {
//                                        contentmap.remove(keyDay);
//                                    }
//                                    synchronized (linemap) {
//                                        linemap.remove(delReport);
//                                    }
//
//                                    synchronized (valuecache) {
//                                        valuecache.get(keyDay).remove(nreport);
//                                        valuecache.get(keyDay).add(delReport);
//                                        Collections.sort(valuecache.get(keyDay));
//                                    }
//
//                                    createCP4Day(new DateMidnight(delReport.getPit()));
//
//                                    buildPanel();
//                                    if (tbShowReplaced.isSelected()) {
//                                        GUITools.flashBackground(linemap.get(delReport), Color.YELLOW, 2);
//                                    }
//                                } catch (OptimisticLockException ole) { OPDE.warn(ole);
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
//                                    } else {
//                                        reloadDisplay(true);
//                                    }
//                                } catch (Exception e) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//
//                            }
//                        }
//                    });
                }
            });
//            btnDelete.setEnabled(NReportTools.isChangeable(nreport));
            pnlMenu.add(btnDelete);


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
//                            EntityManager em = OPDE.createEM();
//                            final NReport myReport = em.find(NReport.class, nreport.getID());
//                            em.close();
//
//                            final String keyNewDay = DateFormat.getDateInstance().format(myReport.getPit());
//
//
//                            synchronized (contentmap) {
//                                contentmap.remove(keyNewDay);
//                            }
//                            synchronized (linemap) {
//                                linemap.remove(nreport);
//                            }
//
//                            synchronized (valuecache) {
//                                valuecache.get(keyNewDay).remove(nreport);
//                                valuecache.get(keyNewDay).add(myReport);
//                                Collections.sort(valuecache.get(keyNewDay));
//                            }
//
//                            createCP4Day(new DateMidnight(myReport.getPit()));
//
//                            buildPanel();
//                            GUITools.flashBackground(linemap.get(myReport), Color.YELLOW, 2);
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

        /***
         *      _   _
         *     | \ | | _____      __
         *     |  \| |/ _ \ \ /\ / /
         *     | |\  |  __/\ V  V /
         *     |_| \_|\___| \_/\_/
         *
         */
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            JideButton addButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.new"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgTraining(new Training(), new Closure() {
                        @Override
                        public void execute(Object training) {
                            if (training != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    final Training myTraining = (Training) em.merge(training);
                                    em.getTransaction().commit();

                                    final String keyYear = Integer.toString(new DateTime(myTraining.getDate()).getYear()) + ".year";

                                    if (!cpMap.containsKey(keyYear)) {
                                        reload();
                                    } else {
                                        createCP4(myTraining);
                                        buildPanel();
                                    }
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
            list.add(addButton);
        }
        return list;
    }


    @Override
    public String getInternalClassID() {
        return null;
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane jspMain;
    private CollapsiblePanes cpsMain;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
