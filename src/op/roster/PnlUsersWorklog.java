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
import entity.EntityTools;
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
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.joda.time.DateMidnight;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Torsten Löhr
 */
public class PnlUsersWorklog extends CleanablePanel {
    public static final String internalClassID = "opde.roster";

    private Map<String, CollapsiblePane> cpMap;
    private Map<String, JPanel> contentmap;
    private TableScrollPane tsp1;
    private ArrayList<Rosters> lstAllRosters;
    private JPopupMenu menu;
    Format weekFormater = new SimpleDateFormat("w yyyy");

    public PnlUsersWorklog() {
        initComponents();
        initPanel();
    }

    private void initPanel() {

        cpMap = Collections.synchronizedMap(new HashMap<String, CollapsiblePane>());
        contentmap = Collections.synchronizedMap(new HashMap<String, JPanel>());

        EntityManager em = OPDE.createEM();
        lstAllRosters = new ArrayList<Rosters>(em.createQuery("SELECT r FROM Rosters r ORDER BY r.month ASC").getResultList());
        em.close();

        lstRosters.setModel(SYSTools.list2dlm(lstAllRosters));
        lstRosters.setCellRenderer(RostersTools.getRenderer());


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
            final Format monthFormatter = new SimpleDateFormat("MMMM yyyy");
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

    private CollapsiblePane createCP4Week(final LocalDate week) {
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

        String title = "<html><font size=+1><b>" +
                DateFormat.getDateInstance(DateFormat.SHORT).format(week.dayOfWeek().withMaximumValue().toDate()) + " - " +
                DateFormat.getDateInstance(DateFormat.SHORT).format(week.dayOfWeek().withMinimumValue().toDate()) +
                " (" +
                OPDE.lang.getString("misc.msg.weekinyear") +
                week.getWeekOfWeekyear() +
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

        GUITools.addExpandCollapseButtons(cpWeek, cptitle.getRight());

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

        cpWeek.setBackground(SYSConst.orange1[SYSConst.medium1]);
        cpWeek.setOpaque(false);
        cpWeek.setHorizontalAlignment(SwingConstants.LEADING);
        //        cpMonth.setBackground(getColor(vtype, SYSConst.light3));


        cpWeek.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                Users user = EntityTools.find(Users.class, "glaumann");
                Rosters roster = EntityTools.find(Rosters.class, 6l);
                RosterParameters rosterParameters = RostersTools.getParameters(roster);
                UserContracts userContracts = UsersTools.getContracts(user);

                pnlWorklog.add(new PnlWorkingLogWeek(user, new LocalDate(roster.getMonth()).plusWeeks(1), rosterParameters, userContracts));
                cpWeek.setContentPane();
            }
        });

        if (!cpWeek.isCollapsed()) {
            cpWeek.setContentPane(createWeekContentPanel4(week));
        }


        return cpWeek;
    }

    private void reloadDisplay() {
        /***
         *               _                 _ ____  _           _
         *      _ __ ___| | ___   __ _  __| |  _ \(_)___ _ __ | | __ _ _   _
         *     | '__/ _ \ |/ _ \ / _` |/ _` | | | | / __| '_ \| |/ _` | | | |
         *     | | |  __/ | (_) | (_| | (_| | |_| | \__ \ |_) | | (_| | |_| |
         *     |_|  \___|_|\___/ \__,_|\__,_|____/|_|___/ .__/|_|\__,_|\__, |
         *                                              |_|            |___/
         */

        synchronized (contentmap) {
            SYSTools.clear(contentmap);
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

                GUITools.setResidentDisplay(resident);

                Pair<LocalDate, LocalDate> minmax = RostersTools.getMinMax(RostersTools.SECTION_CARE);
                if (minmax != null) {
                    LocalDate start = SYSCalendar.bow(SYSCalendar.bom(minmax.getFirst()));
                    LocalDate end = SYSCalendar.bow(SYSCalendar.eom(minmax.getSecond()));
                    for (LocalDate week = start; week.isBefore(end); week = week.plusWeeks(1)) {

                        max = minmax.getSecond().toDate();
                        DateMidnight start = minmax.getFirst().toDateMidnight().dayOfMonth().withMinimumValue();
                        DateMidnight end = resident.isActive() ? new DateMidnight() : minmax.getSecond().toDateMidnight().dayOfMonth().withMinimumValue();
                        for (int year = end.getYear(); year >= start.getYear(); year--) {
                            createCP4Year(year, start, end);
                        }
                    }

                    return null;
                }

                @Override
                protected void done () {

                    buildPanel();
                    initPhase = false;
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                    if (lockmessageAfterwards) OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    if (max != null) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.lastEntry") + ": " + DateFormat.getDateInstance().format(max), 5));
                    } else {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.noentryyet"), 5));
                    }
                }
            }

            ;
            worker.execute();


        }
    }

    private void buildPanel() {
        cpsWL.removeAll();
        cpsWL.setLayout(new JideBoxLayout(cpsWL, JideBoxLayout.Y_AXIS));

        synchronized (cpMap) {
            Pair<LocalDate, LocalDate> minmax = RostersTools.getMinMax(RostersTools.SECTION_CARE);
            LocalDate start = SYSCalendar.bow(SYSCalendar.bom(minmax.getFirst()));
            LocalDate end = SYSCalendar.bow(SYSCalendar.eom(minmax.getSecond()));
            for (LocalDate week = start; week.isBefore(end); week = week.plusWeeks(1)) {
                final String key = weekFormater.format(week.toDate()) + ".week";
                cpsWL.add(cpMap.get(key));
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
        panel1 = new JPanel();
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

            //======== panel1 ========
            {
                panel1.setLayout(new FormLayout(
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
                panel1.add(scrollPane1, CC.xy(1, 1, CC.DEFAULT, CC.FILL));

                //---- btnNewRoster ----
                btnNewRoster.setText("new roster");
                btnNewRoster.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnNewRosterActionPerformed(e);
                    }
                });
                panel1.add(btnNewRoster, CC.xy(1, 3));
            }
            tabbedPane1.addTab("Roster", panel1);
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
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private JList lstRosters;
    private JButton btnNewRoster;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
