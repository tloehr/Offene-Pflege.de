/*
 * Created by JFormDesigner on Thu Aug 15 16:52:39 CEST 2013
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.grid.TableScrollPane;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideBoxLayout;
import entity.Homes;
import entity.HomesTools;
import entity.roster.RosterParameters;
import entity.roster.Rosters;
import entity.roster.RostersTools;
import entity.roster.UserContracts;
import entity.system.SYSPropsTools;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.system.InternalClassACL;
import op.threads.DisplayMessage;
import op.tools.*;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class PnlUsersWorklog extends CleanablePanel {
    public static final String internalClassID = "opde.roster";

    private Map<String, CollapsiblePane> cpMap;
    private Map<String, JPanel> contentmap;
    private TableScrollPane tsp1;
    private List<Rosters> lstAllRosters;
    private JPopupMenu menu;
    final Format weekFormater = new SimpleDateFormat("w yyyy");
    final Format monthFormatter = new SimpleDateFormat("MMMM yyyy");

    public PnlUsersWorklog() {
        initComponents();
        initPanel();
    }

    private void initPanel() {

        cpMap = Collections.synchronizedMap(new HashMap<String, CollapsiblePane>());
        contentmap = Collections.synchronizedMap(new HashMap<String, JPanel>());
        lstAllRosters = Collections.synchronizedList(new ArrayList<Rosters>());

        reloadDisplay();


    }

    private void lstRostersMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            final FrmRoster frmRoster = OPDE.getMainframe().addRoster((Rosters) lstRosters.getSelectedValue());
            final int pos = lstAllRosters.indexOf(lstRosters.getSelectedValue());
            lstAllRosters.remove(pos);
            lstAllRosters.add(pos, frmRoster.getRoster());
            lstRosters.setModel(SYSTools.list2dlm(lstAllRosters));
            frmRoster.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    lstAllRosters.remove(pos);
                    lstAllRosters.add(pos, frmRoster.getRoster());
                    lstRosters.setModel(SYSTools.list2dlm(lstAllRosters));
                    super.windowClosed(e);
                }
            });
        }
    }

    private void btnNewRosterActionPerformed(ActionEvent e) {
        LocalDate monthToCreate = null;
        String paramsXML = null;

        Homes defaultHome = HomesTools.getAll().get(0);

        if (lstAllRosters.isEmpty()) {
            JComboBox cmbMonth = new JComboBox(SYSCalendar.createMonthList(new LocalDate().minusYears(1).monthOfYear().withMinimumValue(), new LocalDate().monthOfYear().withMaximumValue()));
//            final Format monthFormatter = new SimpleDateFormat("MMMM yyyy");
            cmbMonth.setRenderer(new ListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    return new DefaultListCellRenderer().getListCellRendererComponent(list, monthFormatter.format(((LocalDate) value).toDate()), index, isSelected, cellHasFocus);

                }
            });
            cmbMonth.setSelectedItem(SYSCalendar.bom(new LocalDate()));
            JOptionPane.showMessageDialog(this, cmbMonth);
            monthToCreate = new LocalDate(cmbMonth.getSelectedItem());
            paramsXML = RostersTools.DEFAULT_XML;
        } else {
            monthToCreate = new LocalDate(lstAllRosters.get(lstAllRosters.size() - 1).getMonth()).plusMonths(1).dayOfMonth().withMinimumValue();
            paramsXML = lstAllRosters.get(lstAllRosters.size() - 1).getXml();
        }


        EntityManager em = OPDE.createEM();
        em.getTransaction().begin();
        Rosters newRoster = em.merge(new Rosters(monthToCreate, paramsXML));

        // the stats of users with a valid contract for this month are entered here
        HashMap<Users, UserContracts> mapUsers = UsersTools.getUsersWithValidContractsIn(monthToCreate);

        String userlist = "";
        for (Users user : mapUsers.keySet()) {
            OPDE.debug(user);
//            OPDE.debug(mapUsers.get(user).getTargetHoursForMonth(monthToCreate, user));
            em.merge(mapUsers.get(user).getTargetHoursForMonth(monthToCreate, user));
            userlist += user.getUID() + "=" + defaultHome.getEID() + ",";
        }

        if (!userlist.isEmpty()) {
            userlist = userlist.substring(0, userlist.length() - 1);
        }
        // ma liste für plan eintragen. auch mehrfach nennnungen erlauben. vielleicht über sysprops.

        em.getTransaction().commit();
        em.close();


        SYSPropsTools.storeProp("rosterid:" + newRoster.getId(), userlist);

        lstAllRosters.add(newRoster);
        lstRosters.setModel(SYSTools.list2dlm(lstAllRosters));

    }

    private void lstRostersMousePressed(MouseEvent evt) {
        Point p = evt.getPoint();
        final int row = lstRosters.locationToIndex(p);

        lstRosters.setSelectedIndex(row);

        final Rosters selectedRoster = (Rosters) lstRosters.getSelectedValue();

        if (SwingUtilities.isRightMouseButton(evt) && selectedRoster != null && selectedRoster.getOpenedBy() != null && !selectedRoster.getOpenedBy().equals(OPDE.getLogin()) && OPDE.isAdmin()) {

            SYSTools.unregisterListeners(menu);
            menu = new JPopupMenu();

            JMenuItem forceUnlock = new JMenuItem(OPDE.lang.getString("opde.roster.force.unlock"), null);
            forceUnlock.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        Rosters myRoster = em.merge(selectedRoster);
                        em.lock(myRoster, LockModeType.OPTIMISTIC);
                        myRoster.setOpenedBy(null);

                        em.getTransaction().commit();
                    } catch (OptimisticLockException ole) {
                        OPDE.debug(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                    } catch (Exception e) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }
                    reload();
                }
            });
            menu.add(forceUnlock);

            if (menu != null) {
                menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
            }
        }
    }

//    private CollapsiblePane createCP4(final Rosters roster, final Users user) {
//        final String key = roster.getId() + ".roster" + user.getUID() + ".uid";
//        synchronized (cpMap) {
//            if (!cpMap.containsKey(key)) {
//                cpMap.put(key, new CollapsiblePane());
//                try {
//                    cpMap.get(key).setCollapsed(true);
//                } catch (PropertyVetoException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//            }
//        }
//        final CollapsiblePane cpUser = cpMap.get(key);
//        String title = "<html><font size=+1><b>" +
//                monthFormatter.format(roster.getMonth()) +
//                " (" +
//                RostersTools.getStage(roster) +
//                ")" +
//                "</b>" +
//                "</font></html>";
//
//        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    cpUser.setCollapsed(!cpUser.isCollapsed());
//                } catch (PropertyVetoException pve) {
//                    // BAH!
//                }
//            }
//        });
//
//        GUITools.addExpandCollapseButtons(cpUser, cptitle.getRight());
//
//        cpUser.setTitleLabelComponent(cptitle.getMain());
//        cpUser.setSlidingDirection(SwingConstants.SOUTH);
//
//        cpUser.setBackground(Color.WHITE);
//        cpUser.setOpaque(false);
//        cpUser.setHorizontalAlignment(SwingConstants.LEADING);
//        //        cpMonth.setBackground(getColor(vtype, SYSConst.light3));
//
//
//        cpUser.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
//            @Override
//            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
//                cpUser.setContentPane(createContentPane4(roster));
//            }
//        });
//
//        if (!cpUser.isCollapsed()) {
//            cpUser.setContentPane(createContentPane4(roster));
//        }
//
//
//        return cpUser;
//    }


    private CollapsiblePane createCP4(final Rosters roster) {
        final String key = roster.getId() + ".roster";
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
        final CollapsiblePane cpRoster = cpMap.get(key);

        String title = "<html><font size=+2><b>" +
                monthFormatter.format(roster.getMonth()) +
                " (" +
                RostersTools.getStage(roster) +
                ")" +
                "</b>" +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpRoster.setCollapsed(!cpRoster.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

//        GUITools.addExpandCollapseButtons(cpRoster, cptitle.getRight());

        //           if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
        //               final JButton btnPrintWeek = new JButton(SYSConst.icon22print2);
        //               btnPrintWeek.setPressedIcon(SYSConst.icon22print2Pressed);
        //               btnPrintWeek.setAlignmentX(Component.RIGHT_ALIGNMENT);
        //               btnPrintWeek.setContentAreaFilled(false);
        //               btnPrintWeek.setBorder(null);
        //               btnPrintWeek.setToolTipText(OPDE.lang.getString("misc.tooltips.btnprintweek"));
        //               btnPrintWeek.addActionListener(new ActionListener() {
        //                   @Override
        //                   public void actionPerformed(ActionEvent actionEvent) {
        //                       SYSFilesTools.print(NReportTools.getReportsAsHTML(NReportTools.getNReports4Week(resident, week), false, true, null, null), true);
        //                   }
        //               });
        //               cptitle.getRight().add(btnPrintWeek);
        //           }

        cpRoster.setTitleLabelComponent(cptitle.getMain());
        cpRoster.setSlidingDirection(SwingConstants.SOUTH);

        cpRoster.setBackground(Color.WHITE);
        cpRoster.setOpaque(false);
        cpRoster.setHorizontalAlignment(SwingConstants.LEADING);
        //        cpMonth.setBackground(getColor(vtype, SYSConst.light3));


        cpRoster.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpRoster.setContentPane(createContentPane4(roster));
            }
        });

        if (!cpRoster.isCollapsed()) {
            cpRoster.setContentPane(createContentPane4(roster));
        }


        return cpRoster;
    }


    private CollapsiblePane createCP4(final LocalDate week, final Rosters roster, final ArrayList<Users> listUsers) {
        final String key = weekFormater.format(week.toDate()) + ".week";
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
        final CollapsiblePane cpWeek = cpMap.get(key);


        LocalDate month = new LocalDate(roster.getMonth());


        LocalDate start = SYSCalendar.max(SYSCalendar.bow(week), SYSCalendar.bom(month));
        LocalDate end = SYSCalendar.min(SYSCalendar.eow(week), SYSCalendar.eom(month));


        String title = "<html><font size=+1><b>" +
                start.toString("dd.MM.yy") + " - " + end.toString("dd.MM.yy") +
                " (" +
                OPDE.lang.getString("misc.msg.weekinyear") +
                start.getWeekOfWeekyear() +
                ")" +
                "</b>" +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpWeek.setCollapsed(!cpWeek.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

//        GUITools.addExpandCollapseButtons(cpWeek, cptitle.getRight());

        //           if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
        //               final JButton btnPrintWeek = new JButton(SYSConst.icon22print2);
        //               btnPrintWeek.setPressedIcon(SYSConst.icon22print2Pressed);
        //               btnPrintWeek.setAlignmentX(Component.RIGHT_ALIGNMENT);
        //               btnPrintWeek.setContentAreaFilled(false);
        //               btnPrintWeek.setBorder(null);
        //               btnPrintWeek.setToolTipText(OPDE.lang.getString("misc.tooltips.btnprintweek"));
        //               btnPrintWeek.addActionListener(new ActionListener() {
        //                   @Override
        //                   public void actionPerformed(ActionEvent actionEvent) {
        //                       SYSFilesTools.print(NReportTools.getReportsAsHTML(NReportTools.getNReports4Week(resident, week), false, true, null, null), true);
        //                   }
        //               });
        //               cptitle.getRight().add(btnPrintWeek);
        //           }

        cpWeek.setTitleLabelComponent(cptitle.getMain());
        cpWeek.setSlidingDirection(SwingConstants.SOUTH);

        cpWeek.setBackground(Color.WHITE);
        cpWeek.setOpaque(false);        cpWeek.setHorizontalAlignment(SwingConstants.LEADING);
        //        cpMonth.setBackground(getColor(vtype, SYSConst.light3));


        cpWeek.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {

                cpWeek.setContentPane(createContentPane4(week, roster, listUsers));
            }
        });

        if (!cpWeek.isCollapsed()) {
            cpWeek.setContentPane(createContentPane4(week, roster, listUsers));
        }

        return cpWeek;
    }

    private CollapsiblePane createCP4(final LocalDate week, final Rosters roster, final Users user) {
        final String key = weekFormater.format(week.toDate()) + ".week." + user.getUID() + ".user";
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
        final CollapsiblePane cpWeek = cpMap.get(key);

        String title = "<html><b>" +
                user.getFullname() +
                "</b>" +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpWeek.setCollapsed(!cpWeek.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

//        GUITools.addExpandCollapseButtons(cpWeek, cptitle.getRight());

        //           if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
        //               final JButton btnPrintWeek = new JButton(SYSConst.icon22print2);
        //               btnPrintWeek.setPressedIcon(SYSConst.icon22print2Pressed);
        //               btnPrintWeek.setAlignmentX(Component.RIGHT_ALIGNMENT);
        //               btnPrintWeek.setContentAreaFilled(false);
        //               btnPrintWeek.setBorder(null);
        //               btnPrintWeek.setToolTipText(OPDE.lang.getString("misc.tooltips.btnprintweek"));
        //               btnPrintWeek.addActionListener(new ActionListener() {
        //                   @Override
        //                   public void actionPerformed(ActionEvent actionEvent) {
        //                       SYSFilesTools.print(NReportTools.getReportsAsHTML(NReportTools.getNReports4Week(resident, week), false, true, null, null), true);
        //                   }
        //               });
        //               cptitle.getRight().add(btnPrintWeek);
        //           }

        cpWeek.setTitleLabelComponent(cptitle.getMain());
        cpWeek.setSlidingDirection(SwingConstants.SOUTH);

        cpWeek.setBackground(Color.WHITE);
        cpWeek.setOpaque(false);
        cpWeek.setHorizontalAlignment(SwingConstants.LEADING);
        //        cpMonth.setBackground(getColor(vtype, SYSConst.light3));

        cpWeek.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                RosterParameters rosterParameters = RostersTools.getParameters(roster);
                UserContracts userContracts = UsersTools.getContracts(user);
                cpWeek.setContentPane(new PnlWorkingLogWeek(user, week, rosterParameters, userContracts));
            }
        });

        if (!cpWeek.isCollapsed()) {
            RosterParameters rosterParameters = RostersTools.getParameters(roster);
            UserContracts userContracts = UsersTools.getContracts(user);
            cpWeek.setContentPane(new PnlWorkingLogWeek(user, week, rosterParameters, userContracts));
        }

        return cpWeek;
    }

    private JPanel createContentPane4(final Rosters roster) {
        JPanel pnlMonth = new JPanel(new VerticalLayout());
        pnlMonth.setOpaque(false);
        LocalDate month = new LocalDate(roster.getMonth());

        ArrayList<Users> listAllPossibleUsers = new ArrayList<Users>(RostersTools.getAllUsersIn(roster));
        ArrayList<Users> listUsers = new ArrayList<Users>();
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, internalClassID) || OPDE.getAppInfo().isAllowedTo(InternalClassACL.USER1, internalClassID)) {
            listUsers.addAll(listAllPossibleUsers);
        } else if (listAllPossibleUsers.contains(OPDE.getLogin().getUser())) {
            listUsers.add(OPDE.getLogin().getUser());
        }
        Collections.sort(listUsers);
        listAllPossibleUsers.clear();

        final LocalDate start = SYSCalendar.bow(SYSCalendar.bom(month));
        final LocalDate end = SYSCalendar.bow(SYSCalendar.eom(month));

        for (LocalDate week = start; !week.isAfter(end); week = week.plusWeeks(1)) {
            pnlMonth.add(createCP4(week, roster, listUsers));
        }

        return pnlMonth;
    }

    private JPanel createContentPane4(final LocalDate week, final Rosters roster, ArrayList<Users> listUsers) {

        JPanel pnlWeek = new JPanel(new VerticalLayout());
        pnlWeek.setOpaque(false);


//        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, internalClassID) || OPDE.getAppInfo().isAllowedTo(InternalClassACL.USER1, internalClassID)) {
//
//        }

        // hier muss noch eine weitere Unterteilung rein

        for (Users user : listUsers) {
            pnlWeek.add(createCP4(week, roster, user));
        }


        return pnlWeek;

        //

    }

    private void reloadDisplay() {
        synchronized (contentmap) {
            SYSTools.clear(contentmap);
        }
        synchronized (cpMap) {
            SYSTools.clear(cpMap);
        }
        synchronized (lstAllRosters) {
            SYSTools.clear(lstAllRosters);
        }

        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {

                EntityManager em = OPDE.createEM();
                synchronized (lstAllRosters) {
                    lstAllRosters.addAll(RostersTools.getAll(RostersTools.SECTION_CARE, RostersTools.STAGE_PLANNING));
                    lstAllRosters.addAll(RostersTools.getAll(RostersTools.SECTION_CARE, RostersTools.STAGE_ACTIVE));
                    lstRosters.setModel(SYSTools.list2dlm(lstAllRosters));
                }
                em.close();
                lstRosters.setCellRenderer(RostersTools.getRenderer());

                List<Rosters> myList = RostersTools.getAll(RostersTools.SECTION_CARE, RostersTools.STAGE_ACTIVE);

                if (myList != null) {
                    synchronized (lstAllRosters) {
                        for (Rosters rosters : lstAllRosters) {
                            createCP4(rosters);
                        }
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


    private void buildPanel() {
        cpsWL.removeAll();
        cpsWL.setLayout(new JideBoxLayout(cpsWL, JideBoxLayout.Y_AXIS));

        synchronized (cpMap) {
            synchronized (lstAllRosters) {
                Collections.sort(lstAllRosters);
                for (Rosters rosters : lstAllRosters) {
                    final String key = rosters.getId() + ".roster";
                    cpsWL.add(cpMap.get(key));
                }
            }
        }
        cpsWL.addExpansion();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        tabbedPane1 = new JTabbedPane();
        pnlWorklog = new JPanel();
        scrollPane2 = new JScrollPane();
        cpsWL = new CollapsiblePanes();
        pnlRosters = new JPanel();
        scrollPane1 = new JScrollPane();
        lstRosters = new JList();
        btnNewRoster = new JButton();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== tabbedPane1 ========
        {

            //======== pnlWorklog ========
            {
                pnlWorklog.setLayout(new BoxLayout(pnlWorklog, BoxLayout.X_AXIS));

                //======== scrollPane2 ========
                {
                    scrollPane2.setViewportView(cpsWL);
                }
                pnlWorklog.add(scrollPane2);
            }
            tabbedPane1.addTab("Workinglog", pnlWorklog);

            //======== pnlRosters ========
            {
                pnlRosters.setLayout(new FormLayout(
                    "default:grow",
                    "default:grow, $lgap, default"));

                //======== scrollPane1 ========
                {

                    //---- lstRosters ----
                    lstRosters.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            lstRostersMouseClicked(e);
                        }
                        @Override
                        public void mousePressed(MouseEvent e) {
                            lstRostersMousePressed(e);
                        }
                    });
                    scrollPane1.setViewportView(lstRosters);
                }
                pnlRosters.add(scrollPane1, CC.xy(1, 1, CC.DEFAULT, CC.FILL));

                //---- btnNewRoster ----
                btnNewRoster.setText("new roster");
                btnNewRoster.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnNewRosterActionPerformed(e);
                    }
                });
                pnlRosters.add(btnNewRoster, CC.xy(1, 3));
            }
            tabbedPane1.addTab("Roster", pnlRosters);
        }
        add(tabbedPane1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    @Override
    public void cleanup() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void reload() {
        initPanel();
    }

    @Override
    public String getInternalClassID() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane tabbedPane1;
    private JPanel pnlWorklog;
    private JScrollPane scrollPane2;
    private CollapsiblePanes cpsWL;
    private JPanel pnlRosters;
    private JScrollPane scrollPane1;
    private JList lstRosters;
    private JButton btnNewRoster;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
