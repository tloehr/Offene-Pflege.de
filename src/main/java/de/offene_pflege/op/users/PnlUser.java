/*
 * OffenePflege.de (OPDE)
 * Copyright (C) 2011 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License V2 as published by the Free Software Foundation
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht,
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 */
package de.offene_pflege.op.users;

import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import de.offene_pflege.entity.system.*;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.gui.interfaces.CleanablePanel;
import de.offene_pflege.gui.interfaces.DefaultCPTitle;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.system.InternalClass;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.DlgYesNo;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import lombok.extern.log4j.Log4j2;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author tloehr
 */
@Log4j2
public class PnlUser extends CleanablePanel {


    private final int TAB_USER = 0;
    private final int TAB_GROUPS = 1;

    private boolean initPhase = false;
    private JToggleButton tbOldUsers;//, tbShowUsers, tbShowGroups;
    private ButtonGroup bg1;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    private ArrayList<OPUsers> lstUsers;
    private ArrayList<OPGroups> lstGroups;

    private HashMap<String, JPanel> contentMap;
    private HashMap<String, CollapsiblePane> cpMap;


    private HashMap<String, OPUsers> usermap;


    private Color fg, bg;

    /**
     * Creates new form PnlUser
     */
    public PnlUser(JScrollPane jspSearch) {
        super("opde.users");
        this.jspSearch = jspSearch;
        initComponents();

        initPhase = true;
        initPanel();
        reloadDisplay();
    }

    private void initPanel() {
        fg = GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_EARLY_FGITEM));
        bg = GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_EARLY_BGITEM));
        contentMap = new HashMap<String, JPanel>();
        cpMap = new HashMap<String, CollapsiblePane>();
        usermap = new HashMap<String, OPUsers>();
        OPDE.getDisplayManager().setMainMessage(OPDE.getAppInfo().getInternalClasses().get(internalClassID).getShortDescription());
        prepareSearchArea();
        tabMain.setTitleAt(TAB_USER, SYSTools.xx("opde.users.tab.users"));
        tabMain.setTitleAt(TAB_GROUPS, SYSTools.xx("opde.users.tab.groups"));
        tabMain.addChangeListener(e -> buildPanel());
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

        lstUsers = UsersTools.getUsers(true);
        lstGroups = GroupsTools.getGroups();
        for (OPUsers user : lstUsers) {
            usermap.put(user.getUID(), user);
            createCP4(user);
        }
        for (OPGroups group : lstGroups) {
            createCP4(group);
        }
        buildPanel();

        initPhase = false;
    }

    @Override
    public String getInternalClassID() {
        return internalClassID;
    }


    @Override
    public void cleanup() {
        super.cleanup();
        lstGroups.clear();
        lstUsers.clear();
        usermap.clear();
        contentMap.clear();
        cpMap.clear();
        cpsGroups.removeAll();
        cpsUsers.removeAll();
        searchPanes.removeAll();
    }


    @Override
    public void reload() {
        cleanup();
        reloadDisplay();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        tabMain = new JTabbedPane();
        jspUsers = new JScrollPane();
        cpsUsers = new CollapsiblePanes();
        jspGroups = new JScrollPane();
        cpsGroups = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== tabMain ========
        {
            tabMain.setFont(new Font("Arial", Font.PLAIN, 18));

            //======== jspUsers ========
            {

                //======== cpsUsers ========
                {
                    cpsUsers.setLayout(new BoxLayout(cpsUsers, BoxLayout.X_AXIS));
                }
                jspUsers.setViewportView(cpsUsers);
            }
            tabMain.addTab("text", jspUsers);


            //======== jspGroups ========
            {

                //======== cpsGroups ========
                {
                    cpsGroups.setLayout(new FormLayout(
                            "default, $lcgap, default",
                            "2*(default, $lgap), default"));
                }
                jspGroups.setViewportView(cpsGroups);
            }
            tabMain.addTab("text", jspGroups);

        }
        add(tabMain);
    }// </editor-fold>//GEN-END:initComponents


//    private String generatePassword(String firstname, String lastname) {
//        Random generator = new Random(System.currentTimeMillis());
//        return lastname.substring(0, 1).toLowerCase() + firstname.substring(0, 1).toLowerCase() + SYSTools.padL(Integer.toString(generator.nextInt(9999)), 4, "0");
////        selectedUser.setMd5pw(SYSTools.hashword(pw));
////        if (JOptionPane.showConfirmDialog(this, "Das Passwort wurde auf '" + pw + "' gesetzt.\nMöchten Sie einen Beleg ausdrucken ?", "Passwort", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
////            print(pw);
////        }
//    }


    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(3));
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(SYSTools.xx(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            log.error(e);
        }

        GUITools.addAllComponents(mypanel, addCommands());
        GUITools.addAllComponents(mypanel, addFilters());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }

    private java.util.List<Component> addFilters() {
        java.util.List<Component> list = new ArrayList<Component>();

        /***
         *      _   _      ___  _     _ _   _
         *     | |_| |__  / _ \| | __| | | | |___  ___ _ __ ___
         *     | __| '_ \| | | | |/ _` | | | / __|/ _ \ '__/ __|
         *     | |_| |_) | |_| | | (_| | |_| \__ \  __/ |  \__ \
         *      \__|_.__/ \___/|_|\__,_|\___/|___/\___|_|  |___/
         *
         */
        tbOldUsers = GUITools.getNiceToggleButton(SYSTools.xx("opde.users.showclosedmembers"));
        tbOldUsers.addItemListener(itemEvent -> {
            if (initPhase) return;
            buildPanel();
        });
        tbOldUsers.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbOldUsers);


        return list;
    }

    private java.util.List<Component> addCommands() {

        java.util.List<Component> list = new ArrayList<Component>();

        /***
         *         _       _     _ _   _
         *        / \   __| | __| | | | |___  ___ _ __
         *       / _ \ / _` |/ _` | | | / __|/ _ \ '__|
         *      / ___ \ (_| | (_| | |_| \__ \  __/ |
         *     /_/   \_\__,_|\__,_|\___/|___/\___|_|
         *
         */
        final JideButton btnAddUser = GUITools.createHyperlinkButton(SYSTools.xx("opde.users.btnAddUser"), SYSConst.icon22addUser, null);
        btnAddUser.addActionListener(actionEvent -> {
            if (tabMain.getSelectedIndex() != TAB_USER) {
                tabMain.setSelectedIndex(TAB_USER);
            }
            currentEditor = new DlgUser(new OPUsers(), o -> {
                if (o != null) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        OPUsers user = em.merge((OPUsers) o);
                        // Put everyone into >>everyone<<
                        OPGroups everyone = em.find(OPGroups.class, "everyone");
                        em.lock(everyone, LockModeType.OPTIMISTIC);
                        user.getGroups().add(everyone);
                        everyone.getMembers().add(user);

                        // create a cipherid for the new user. If it already exists, the operation is rolled back.
                        // not neat but worls. And the chances are very low, that you would have to re-enter the user.

                        // https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
                        int randomNum = ThreadLocalRandom.current().nextInt(10000, 10000000 + 1);
                        user.setCipherid(randomNum);
                        
                        em.getTransaction().commit();
                        lstUsers.add(user);
                        reloadDisplay();
                    } catch (Exception e) {
                        em.getTransaction().rollback();
                    } finally {
                        em.close();
                    }
                }
                currentEditor = null;
            });
            currentEditor.setVisible(true);

        });
        list.add(btnAddUser);

        /***
         *         _       _     _  ____
         *        / \   __| | __| |/ ___|_ __ ___  _   _ _ __
         *       / _ \ / _` |/ _` | |  _| '__/ _ \| | | | '_ \
         *      / ___ \ (_| | (_| | |_| | | | (_) | |_| | |_) |
         *     /_/   \_\__,_|\__,_|\____|_|  \___/ \__,_| .__/
         *                                              |_|
         */
        final JideButton btnAddGroup = GUITools.createHyperlinkButton(SYSTools.xx("opde.users.btnAddGroup"), SYSConst.icon22addGroup, null);
        btnAddGroup.addActionListener(actionEvent -> {
            if (tabMain.getSelectedIndex() != TAB_GROUPS) {
                tabMain.setSelectedIndex(TAB_GROUPS);
            }
            currentEditor = new DlgGroup(new OPGroups(), o -> {
                if (o != null) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        OPGroups myGroup = em.merge((OPGroups) o);
                        em.getTransaction().commit();
                        createCP4(myGroup);
                        lstGroups.add(myGroup);
                        Collections.sort(lstGroups);
                        buildPanel();
                    } catch (Exception e) {
//                                em.getTransaction().rollback();
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }
                }
                currentEditor = null;
            });
            currentEditor.setVisible(true);

        });
        list.add(btnAddGroup);

        /***
         *      _     _         ____       _       _
         *     | |__ | |_ _ __ |  _ \ _ __(_)_ __ | |_
         *     | '_ \| __| '_ \| |_) | '__| | '_ \| __|
         *     | |_) | |_| | | |  __/| |  | | | | | |_
         *     |_.__/ \__|_| |_|_|   |_|  |_|_| |_|\__|
         *
         */
//        JideButton btnPrint = GUITools.createHyperlinkButton(SYSTools.xx("misc.commands.print"), SYSConst.icon22print2, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//
//            }
//        });
//        list.add(btnPrint);


        return list;
    }

    private CollapsiblePane createCP4(final OPUsers user) {
        final String key = user.getUID() + ".xusers";
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        final CollapsiblePane cp = cpMap.get(key);
        DefaultCPTitle cptitle = new DefaultCPTitle("<html><font size=+1>" +
                user.toString() +
                (UsersTools.isQualified(user) ?
                        ", " + SYSTools.xx("opde.users.qualifiedNurse") : "") +
                "</font></html>", e -> {
            try {
                cp.setCollapsed(!cp.isCollapsed());
            } catch (PropertyVetoException pve) {
                // BAH!
            }
        });


        /***
         *       ____ _                            ______        __
         *      / ___| |__   __ _ _ __   __ _  ___|  _ \ \      / /
         *     | |   | '_ \ / _` | '_ \ / _` |/ _ \ |_) \ \ /\ / /
         *     | |___| | | | (_| | | | | (_| |  __/  __/ \ V  V /
         *      \____|_| |_|\__,_|_| |_|\__, |\___|_|     \_/\_/
         *                              |___/
         */
        final JButton btnChangePW = new JButton(SYSConst.icon22password);
        btnChangePW.setPressedIcon(SYSConst.icon22passwordPressed);
        btnChangePW.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnChangePW.setContentAreaFilled(false);
        btnChangePW.setBorder(null);
        btnChangePW.setToolTipText(SYSTools.xx("opde.users.btnChangePW.tooltip"));
        btnChangePW.addActionListener(actionEvent -> {

            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                OPUsers myUser = em.merge(usermap.get(user.getUID()));
                String newpw = SYSTools.generatePassword(myUser.getVorname(), myUser.getName());
                em.lock(myUser, LockModeType.OPTIMISTIC);
                myUser.setMd5pw(SYSTools.hashword(newpw));
                em.getTransaction().commit();

                lstUsers.remove(user);
                lstUsers.add(myUser);
                usermap.put(key, myUser);
                Collections.sort(lstUsers);

                SYSTools.printpw(newpw, myUser);

                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("opde.users.pwchanged")));
            } catch (OptimisticLockException ole) {
                log.warn(ole);
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
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

        });
        btnChangePW.setEnabled(user.isActive());
        cptitle.getRight().add(btnChangePW);


        /***
         *      _     _            _        _   _           ___                  _   _
         *     | |__ | |_ _ __    / \   ___| |_(_)_   _____|_ _|_ __   __ _  ___| |_(_)_   _____
         *     | '_ \| __| '_ \  / _ \ / __| __| \ \ / / _ \| || '_ \ / _` |/ __| __| \ \ / / _ \
         *     | |_) | |_| | | |/ ___ \ (__| |_| |\ V /  __/| || | | | (_| | (__| |_| |\ V /  __/
         *     |_.__/ \__|_| |_/_/   \_\___|\__|_| \_/ \___|___|_| |_|\__,_|\___|\__|_| \_/ \___|
         *
         */
        final JButton btnActiveInactive = new JButton(user.isActive() ? SYSConst.icon22stop : SYSConst.icon22playerPlay);
        btnActiveInactive.setPressedIcon(user.isActive() ? SYSConst.icon22stopPressed : SYSConst.icon22playerPlayPressed);
        btnActiveInactive.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnActiveInactive.setContentAreaFilled(false);
        btnActiveInactive.setBorder(null);
        btnActiveInactive.setToolTipText(SYSTools.xx(internalClassID + (user.isActive() ? ".btnActiveInactive.stop" : ".btnActiveInactive.play")));
        btnActiveInactive.addActionListener(actionEvent -> {

            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                OPUsers myUser = em.merge(usermap.get(user.getUID()));
                em.lock(myUser, LockModeType.OPTIMISTIC);

                myUser.setUserstatus(myUser.isActive() ? UsersTools.STATUS_INACTIVE : UsersTools.STATUS_ACTIVE);

                em.getTransaction().commit();
                lstUsers.remove(user);
                lstUsers.add(myUser);
                usermap.put(myUser.getUID(), myUser);
                Collections.sort(lstUsers);
                CollapsiblePane cp12 = createCP4(myUser);
                boolean wasCollapsed = cpMap.get(key).isCollapsed();
                cpMap.put(key, cp12);

                cp12.setCollapsed(myUser.isActive() ? wasCollapsed : true);
                buildPanel();
            } catch (OptimisticLockException ole) {
                log.warn(ole);
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
        });
        cptitle.getRight().add(btnActiveInactive);

        /***
         *               _ _ _
         *       ___  __| (_) |_
         *      / _ \/ _` | | __|
         *     |  __/ (_| | | |_
         *      \___|\__,_|_|\__|
         *
         */
        final JButton btnEdit = new JButton(SYSConst.icon22edit3);
        btnEdit.setPressedIcon(SYSConst.icon22edit3Pressed);
        btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnEdit.setContentAreaFilled(false);
        btnEdit.setBorder(null);
        btnEdit.setToolTipText(SYSTools.xx("opde.users.btnEdit"));
        btnEdit.addActionListener(actionEvent -> {
            currentEditor = new DlgUser(user, o -> {
                if (o != null) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        OPUsers myUser = em.merge((OPUsers) o);
                        em.lock(myUser, LockModeType.OPTIMISTIC);
                        em.getTransaction().commit();
                        lstUsers.remove(user);
                        lstUsers.add(myUser);
                        usermap.put(myUser.getUID(), myUser);
                        Collections.sort(lstUsers);
                        CollapsiblePane cp1 = createCP4(myUser);
                        boolean wasCollapsed = cpMap.get(key).isCollapsed();
                        cpMap.put(key, cp1);

                        cp1.setCollapsed(myUser.isActive() ? wasCollapsed : true);
                        buildPanel();
                    } catch (OptimisticLockException ole) {
                        log.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                currentEditor = null;
            });
            currentEditor.setVisible(true);
        });
        cptitle.getRight().add(btnEdit);

        cp.setTitleLabelComponent(cptitle.getMain());
        cp.setSlidingDirection(SwingConstants.SOUTH);


        /***
         *       ___ ___  _  _ _____ ___ _  _ _____
         *      / __/ _ \| \| |_   _| __| \| |_   _|
         *     | (_| (_) | .` | | | | _|| .` | | |
         *      \___\___/|_|\_| |_| |___|_|\_| |_|
         *
         */

        cp.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
                                          @Override
                                          public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                                              if (!contentMap.containsKey(key)) {
                                                  contentMap.put(key, new PnlEditMemberships(user, lstGroups));
                                              }
                                              cp.setContentPane(contentMap.get(key));
                                              cp.setOpaque(false);
                                          }
                                      }

        );
        cp.setBackground(UsersTools.getBG1(user));
        cp.setCollapsible(user.isActive());

        cp.setHorizontalAlignment(SwingConstants.LEADING);
        cp.setOpaque(false);

        return cp;
    }

    private CollapsiblePane createCP4(final OPGroups group) {
        final String key = group.getGID() + ".xgroups";
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            cpMap.get(key).setSlidingDirection(SwingConstants.SOUTH);

            cpMap.get(key).setBackground(bg);
            cpMap.get(key).setForeground(fg);

            cpMap.get(key).addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
                @Override
                public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                    if (!contentMap.containsKey(key)) {
                        contentMap.put(key, createContentPanel4(group));
                    }
                    cpMap.get(key).setContentPane(contentMap.get(key));
                }
            });
            cpMap.get(key).setHorizontalAlignment(SwingConstants.LEADING);
            cpMap.get(key).setOpaque(false);
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


        }
        final CollapsiblePane cp = cpMap.get(key);

        DefaultCPTitle cpTitle = new DefaultCPTitle("<html><font size=+1>" +
                group.getGID().toUpperCase() +
                (group.isQualified() ?
                        ", " + SYSTools.xx("opde.users.qualifiedGroup") : "") +
                "</font></html>", e -> {
            try {
                cp.setCollapsed(!cp.isCollapsed());
            } catch (PropertyVetoException pve) {
                // BAH!
            }
        });

        /***
         *          _      _      _
         *       __| | ___| | ___| |_ ___    __ _ _ __ ___  _   _ _ __
         *      / _` |/ _ \ |/ _ \ __/ _ \  / _` | '__/ _ \| | | | '_ \
         *     | (_| |  __/ |  __/ ||  __/ | (_| | | | (_) | |_| | |_) |
         *      \__,_|\___|_|\___|\__\___|  \__, |_|  \___/ \__,_| .__/
         *                                  |___/                |_|
         */
        final JButton btnDeleteGroup = new JButton(SYSConst.icon22delete);
        btnDeleteGroup.setPressedIcon(SYSConst.icon22deletePressed);
        btnDeleteGroup.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnDeleteGroup.setContentAreaFilled(false);
        btnDeleteGroup.setBorder(null);
        btnDeleteGroup.setToolTipText(SYSTools.xx("opde.users.btnDeleteGroup"));
        btnDeleteGroup.addActionListener(actionEvent -> {
            currentEditor = new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><i>" + group.getGID() + "</i><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, o -> {
                if (o.equals(JOptionPane.YES_OPTION)) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        OPGroups myGroup = em.merge(group);
                        em.remove(myGroup);
                        em.getTransaction().commit();
                        lstGroups.remove(group);
                        cpMap.remove(key);
                        buildPanel();
                    } catch (OptimisticLockException ole) {
                        log.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                        currentEditor = null;
                    }
                }
            });
            currentEditor.setVisible(true);
        });
        btnDeleteGroup.setEnabled(!group.isSysflag());
        cpTitle.getRight().add(btnDeleteGroup);

        cp.setTitleLabelComponent(cpTitle.getMain());

        if (!cp.isCollapsed()) {
            if (!contentMap.containsKey(key)) {
                contentMap.put(key, createContentPanel4(group));
            }
            cp.setContentPane(contentMap.get(key));
        }

        return cp;
    }

    private JPanel createContentPanel4(final OPGroups group) {
        JPanel contentPanel = new JPanel(new VerticalLayout());
        contentPanel.add(new JLabel(SYSTools.toHTMLForScreen(SYSConst.html_bold(group.getDescription()))));

        if (!group.isEveryone()) { // everyone does not need a membership.
            contentPanel.add(createMemberPanel4(group));
        }

        if (!group.isAdmin()) { // admin does not need further acls. he is in godmode anyways
            contentPanel.add(createClassesPanel4(group));
        }
        return contentPanel;
    }

    private JPanel createMemberPanel4(final OPGroups group) {

        CollapsiblePane cpMember = new CollapsiblePane(SYSTools.xx("opde.users.members"));
        cpMember.setBackground(bg.darker()); // a little darker
        cpMember.setForeground(Color.WHITE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new VerticalLayout());
        contentPanel.setOpaque(false);

        cpMember.setContentPane(new PnlEditMembers(group, lstUsers));

        contentPanel.add(cpMember);
        cpMember.setFont(SYSConst.ARIAL14BOLD);


        return contentPanel;
    }

    private JPanel createClassesPanel4(final OPGroups group) {

        HashMap<String, SYSGROUPS2ACL> lookup = SYSGROUPS2ACLTools.getIntClassesMap(group);
        CollapsiblePane cpClasses = new CollapsiblePane(SYSTools.xx("opde.users.modules"));
        try {
            cpClasses.setCollapsed(true);
        } catch (PropertyVetoException e) {
        }

        JPanel contentClasses = new JPanel();
        contentClasses.setLayout(new VerticalLayout());

        ArrayList<InternalClass> listClasses = new ArrayList<InternalClass>(OPDE.getAppInfo().getInternalClasses().values());
        Collections.sort(listClasses);
        for (final InternalClass ic : listClasses) {
            CollapsiblePane cpClass = new CollapsiblePane(ic.getShortDescription());
//            cpClass.setToolTipText(ic.getLongDescription());
            SYSGROUPS2ACL mySYSGROUPS2ACL = lookup.containsKey(ic.getInternalClassID()) ? lookup.get(ic.getInternalClassID()) : new SYSGROUPS2ACL(ic.getInternalClassID(), group);
            cpClass.setContentPane(new PnlEditACL(mySYSGROUPS2ACL));
            cpClass.setStyle(CollapsiblePane.TREE_STYLE);
            try {
                cpClass.setCollapsed(true);
            } catch (PropertyVetoException e) {
            }
            contentClasses.add(cpClass);
        }

        cpClasses.setContentPane(contentClasses);
        cpClasses.setFont(SYSConst.ARIAL14BOLD);

        return cpClasses;
    }

    private void buildPanel() {

        if (tabMain.getSelectedIndex() == TAB_USER) {
            cpsUsers.removeAll();
            cpsUsers.setLayout(new JideBoxLayout(cpsUsers, JideBoxLayout.Y_AXIS));
            Collections.sort(lstUsers);
            for (OPUsers user : lstUsers) {
                if (tbOldUsers.isSelected() || user.isActive()) {
                    cpsUsers.add(cpMap.get(user.getUID() + ".xusers"));
                }
            }
            cpsUsers.addExpansion();
        } else {
            cpsGroups.removeAll();
            cpsGroups.setLayout(new JideBoxLayout(cpsGroups, JideBoxLayout.Y_AXIS));
            Collections.sort(lstGroups);
            for (OPGroups group : lstGroups) {
                cpsGroups.add(cpMap.get(group.getGID() + ".xgroups"));
            }
            cpsGroups.addExpansion();
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTabbedPane tabMain;
    private JScrollPane jspUsers;
    private CollapsiblePanes cpsUsers;
    private JScrollPane jspGroups;
    private CollapsiblePanes cpsGroups;
    // End of variables declaration//GEN-END:variables
}
