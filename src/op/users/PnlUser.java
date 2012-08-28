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
package op.users;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.Groups;
import entity.Users;
import entity.UsersTools;
import entity.files.SYSFilesTools;
import entity.system.IntClassesTools;
import op.OPDE;
import op.tools.*;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author tloehr
 */
public class PnlUser extends CleanablePanel {
    public static final String internalClassID = "opde.users";

    private static final int NEW_USER = 0;
    private static final int EDIT_USER = 1;
    private static final int BROWSE_USER = 2;
    private static final int NEW_GROUP = 3;
    private static final int EDIT_GROUP = 4;
    private static final int BROWSE_GROUP = 5;
    private static final int OK = 0;
    private static final int CONFLICT = 1;
    private static final int INDIFFERENT = 2;
    private static final int TAB_USER = 0;
    private static final int TAB_GROUP = 1;
    private Users selectedUser;
    private Groups selectedGroup;
    private int mode, ukennung_status, gkennung_status;
    private ListDataListener ldlMember, ldlUser;
    private CheckTreeManager cm;
    private CheckTreeSelectionModel sm;

    private boolean initPhase = false;
    private JToggleButton tbRefactor;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    private ArrayList<Users> lstUsers;

    /**
     * Creates new form PnlUser
     */
    public PnlUser(JScrollPane jspSearch) {
        this.jspSearch = jspSearch;
        initComponents();
//        btnEnableUser.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/user_active.png")));
//        btnEnableUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/user_inactive.png")));

//        lstAllGroups.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//
//            @Override
//            public void valueChanged(ListSelectionEvent lse) {
//                if (!lse.getValueIsAdjusting()) {
//
//                    if (lstAllGroups.getSelectedIndex() >= 0) {
//                        selectedGroup = (Groups) lstAllGroups.getSelectedValue();
//                        EntityManager em1 = OPDE.createEM();
//                        em1.refresh(selectedGroup);
//                        em1.close();
//                    } else {
//                        selectedGroup = null;
//                    }
//                    setLeftSideOnGroupTab();
//                    setRightSideOnGroupTab();
//                }
//            }
//        });

        jtpMain.setSelectedIndex(TAB_USER);
        initPanel();
    }

    private void initPanel() {
        lstUsers = UsersTools.getUsers(true);
        listUsers.setModel(SYSTools.list2dlm(lstUsers));
        listAvailableGroups.setModel(new DefaultListModel());
        listMemberIn.setModel(new DefaultListModel());
        prepareSearchArea();
    }

//    private void loadUserTable() {
//        EntityManager em = OPDE.createEM();
//        try {
//            Query query = em.createNamedQuery("Users.findAllSorted");
//            tblUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//            tblUsers.setModel(new TMUser(query.getResultList()));
//            tblUsers.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//
//                @Override
//                public void valueChanged(ListSelectionEvent lse) {
//                    if (!lse.getValueIsAdjusting()) {
//                        if (tblUsers.getSelectedRow() >= 0) {
//                            selectedUser = ((TMUser) tblUsers.getModel()).getUserAt(tblUsers.getSelectedRow());
//                            setRightSideOnUserTab();
//                            setLeftSideOnUserTab();
//                        } else {
//                            selectedUser = null;
//                        }
//                    }
//                }
//            });
//            tblUsers.getColumnModel().getColumn(0).setCellRenderer(new RNDOCUsers());
//        } catch (Exception e) {
//            OPDE.fatal(e);
//        } finally {
//            em.close();
//        }
//
//    }

    @Override
    public void cleanup() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void reload() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

//    private void setGroupTab() {
//
//        if (ldlMember == null) {
//            // Wird immer aufgerufen, wenn sich die Daten in den Member und Userlisten ändern
//            ldlMember = new ListDataListener() {
//
//                @Override
//                public void intervalAdded(ListDataEvent e) {
//                    Users draggedUser = (Users) lstMembers.getModel().getElementAt(e.getIndex0());
//                    selectedGroup.getMembers().add(draggedUser);
//                }
//
//                @Override
//                public void intervalRemoved(ListDataEvent e) {
//                }
//
//                // Wird bei uns nicht aufgerufen.
//                @Override
//                public void contentsChanged(ListDataEvent e) {
//                }
//            };
//            ldlUser = new ListDataListener() {
//
//                @Override
//                public void intervalAdded(ListDataEvent e) {
//                    Users draggedUser = (Users) lstUsers.getModel().getElementAt(e.getIndex0());
//                    selectedGroup.getMembers().remove(draggedUser);
//                }
//
//                @Override
//                public void intervalRemoved(ListDataEvent e) {
//                }
//
//                // Wird bei uns nicht aufgerufen.
//                @Override
//                public void contentsChanged(ListDataEvent e) {
//                }
//            };
//        }
//
//        lstAllGroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        lstAllGroups.setTransferHandler(null);
//        lstAllGroups.setModel(SYSTools.newListModel("Groups.findAllSorted"));
//        setLeftSideOnGroupTab();
//        setRightSideOnGroupTab();
//
//    }

    //    private void setUsersAndMembersOnGroupTab() {
//        if (mode == BROWSE_GROUP) {
//        } else if (mode == NEW_GROUP) {
//        } else { // EDIT_GROUP
//        }
//
//    }

//    private void setRightSideOnGroupTab() {
//        enableRightSideOnGroupTab();
//        setRightButtonsOnGroupTab();
//
//        if (selectedGroup != null) {
//
//            txtGKennung.setText(SYSTools.catchNull(selectedGroup.getGkennung()));
//            txtGroupDescription.setText(SYSTools.catchNull(selectedGroup.getBeschreibung()));
//            cbExamen.setSelected(selectedGroup.isExamen());
//
//            lstMembers.getModel().removeListDataListener(ldlMember);
//            lstUsers.getModel().removeListDataListener(ldlUser);
//
//            // TODO: hier liegt noch was im Argen
//            // Hab den Teil auskommentiert
////            if (em.contains(selectedGroup)) { // EntityBean ist schon gespeichert.
////                if (selectedGroup.getGkennung().equalsIgnoreCase("everyone")) {
////                    // everyone hat immer alle Benutzer als Mitglied.
////                    lstMembers.setModel(SYSTools.newListModel("Users.findAllSorted"));
////                    lstUsers.setModel(new DefaultListModel());
////                } else {
////                    lstMembers.setModel(SYSTools.newListModel("Users.findAllMembers", new Object[]{"group", selectedGroup}));
////                    lstUsers.setModel(SYSTools.newListModel("Users.findAllNonMembers", new Object[]{"group", selectedGroup}));
////                }
////            } else {
////                lstMembers.setModel(new DefaultListModel());
////                lstUsers.setModel(SYSTools.newListModel("Users.findAllSorted"));
////            }
//
//            lstMembers.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//            lstUsers.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//            TxHdlrUsers tx = new TxHdlrUsers();
//            lstMembers.setTransferHandler(tx);
//            lstUsers.setTransferHandler(tx);
//            lstMembers.getModel().addListDataListener(ldlMember);
//            lstUsers.getModel().addListDataListener(ldlUser);
//
//            IntClassesTools.clearEntitiesFromAllInternalClasses();
//            ArrayList treeData = IntClassesTools.getIntClassesAsTree(selectedGroup);
//            treeRights.setModel(new DefaultTreeModel((MutableTreeNode) treeData.get(IntClassesTools.ROOT)));
//
//            cm = new CheckTreeManager(treeRights, true);
//            sm = (CheckTreeSelectionModel) cm.getSelectionModel();
//            sm.addSelectionPaths((TreePath[]) treeData.get(IntClassesTools.SELECTION));
//            //originalSelection = sm.getSelectionPaths();
////            if (cblst != null) {
////                sm.removeTreeSelectionListener(cblst);
////            }
//            //cblst = new cbClickListener();
//            //sm.addTreeSelectionListener(cblst);
//            SYSTools.expandAll(treeRights);
//        } else {
//            txtGKennung.setText("");
//            txtGroupDescription.setText("");
//            cbExamen.setSelected(false);
//            lstUsers.setModel(new DefaultListModel());
//            lstMembers.setModel(new DefaultListModel());
//            treeRights.setModel(new DefaultTreeModel(null));
//        }
//
//        if (mode == NEW_GROUP) {
//            txtGKennung.setText("");
//            txtGroupDescription.setText("");
//            cbExamen.setSelected(false);
//            txtGKennung.requestFocus();
//        }
//        if (mode == EDIT_GROUP) {
//            txtGroupDescription.requestFocus();
//        }
//        setRightButtonsOnGroupTab();
//    }
//
//    private void setUserTab() {
//        loadUserTable();
//        TxHdlrGroups tx = new TxHdlrGroups();
//        listMembership.setBackground(Color.LIGHT_GRAY);
//        listGroups.setBackground(Color.LIGHT_GRAY);
//        listMembership.setTransferHandler(tx);
//        listGroups.setTransferHandler(tx);
//        listMembership.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//        listGroups.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//    }
//
//    private void setRightSideOnUserTab() {
//        txtUKennung.setEnabled(mode == NEW_USER);
//        txtVorname.setEnabled(mode != BROWSE_USER);
//        txtName.setEnabled(mode != BROWSE_USER);
//        txtEMail.setEnabled(mode != BROWSE_USER);
//        listMembership.setEnabled(mode != BROWSE_USER);
//        listGroups.setEnabled(mode != BROWSE_USER);
//
//        // Durch die HTML Darstellung wird die Schrift nicht grau, daher muss ich
//        // hier etwas nachhelfen.
//        if (listGroups.isEnabled()) {
//            listGroups.setBackground(Color.WHITE);
//        } else {
//            listGroups.setBackground(Color.LIGHT_GRAY);
//        }
//        if (listMembership.isEnabled()) {
//            listMembership.setBackground(Color.WHITE);
//        } else {
//            listMembership.setBackground(Color.LIGHT_GRAY);
//        }
//
//        txtUKennung.setText(selectedUser.getUKennung());
//        txtVorname.setText(selectedUser.getVorname());
//        txtName.setText(selectedUser.getNachname());
//        txtEMail.setText(SYSTools.catchNull(selectedUser.getEMail()));
//
//
//        if (mode == NEW_USER) {
//            lblUKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballred.png")));
//        } else {
//            lblUKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballblue.png")));
//        }
//
//        setRightButtonsOnUserTab();
//    }


    private void listUsersValueChanged(ListSelectionEvent e) {
        // TODO add your code here
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jtpMain = new JTabbedPane();
        pnlUserTab = new JPanel();
        label3 = new JLabel();
        label1 = new JLabel();
        label2 = new JLabel();
        jsp0 = new JScrollPane();
        listUsers = new JList();
        jPanel5 = new JPanel();
        btnAddUser = new JButton();
        btnPassword = new JButton();
        btnEditUser = new JButton();
        btnEnableUser = new JToggleButton();
        jsp1 = new JScrollPane();
        listMemberIn = new JList();
        jsp2 = new JScrollPane();
        listAvailableGroups = new JList();
        pnlGroupTab = new JPanel();
        pnlLeftGrp = new JPanel();
        jScrollPane3 = new JScrollPane();
        lstAllGroups = new JList();
        jPanel8 = new JPanel();
        btnEditGroup = new JButton();
        btnAddGroup = new JButton();
        btnDeleteGroup = new JButton();
        pnlRightGrp = new JPanel();
        jPanel6 = new JPanel();
        jScrollPane5 = new JScrollPane();
        treeRights = new JTree();
        jLabel10 = new JLabel();
        jPanel7 = new JPanel();
        btnCancelGroups = new JButton();
        btnSaveGroups = new JButton();
        jPanel9 = new JPanel();
        jLabel11 = new JLabel();
        jLabel12 = new JLabel();
        txtGKennung = new JTextField();
        txtGroupDescription = new JTextField();
        lblGKennungFree = new JLabel();
        cbExamen = new JCheckBox();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jtpMain ========
        {
            jtpMain.setTabPlacement(SwingConstants.RIGHT);
            jtpMain.setFont(new Font("Arial", Font.PLAIN, 14));
            jtpMain.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    jtpMainStateChanged(e);
                }
            });

            //======== pnlUserTab ========
            {
                pnlUserTab.setLayout(new FormLayout(
                        "2*(default:grow, $lcgap), default:grow",
                        "default, $lgap, default:grow, 3*($lgap, default)"));

                //---- label3 ----
                label3.setText("text");
                label3.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlUserTab.add(label3, CC.xy(1, 1));

                //---- label1 ----
                label1.setText("text");
                label1.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlUserTab.add(label1, CC.xy(3, 1));

                //---- label2 ----
                label2.setText("text");
                label2.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlUserTab.add(label2, CC.xy(5, 1));

                //======== jsp0 ========
                {

                    //---- listUsers ----
                    listUsers.setFont(new Font("Arial", Font.PLAIN, 14));
                    listUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    listUsers.addListSelectionListener(new ListSelectionListener() {
                        @Override
                        public void valueChanged(ListSelectionEvent e) {
                            listUsersValueChanged(e);
                        }
                    });
                    jsp0.setViewportView(listUsers);
                }
                pnlUserTab.add(jsp0, CC.xy(1, 3, CC.FILL, CC.FILL));

                //======== jPanel5 ========
                {
                    jPanel5.setLayout(new BoxLayout(jPanel5, BoxLayout.X_AXIS));

                    //---- btnAddUser ----
                    btnAddUser.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_add.png")));
                    btnAddUser.setToolTipText("Neuen Mitarbeiter eintragen.");
                    jPanel5.add(btnAddUser);

                    //---- btnPassword ----
                    btnPassword.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/password.png")));
                    btnPassword.setToolTipText("Passwort erzeugen");
                    btnPassword.setEnabled(false);
                    jPanel5.add(btnPassword);

                    //---- btnEditUser ----
                    btnEditUser.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit.png")));
                    btnEditUser.setToolTipText("\u00c4nderungen vornehmen.");
                    btnEditUser.setEnabled(false);
                    jPanel5.add(btnEditUser);

                    //---- btnEnableUser ----
                    btnEnableUser.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/user_active.png")));
                    btnEnableUser.setToolTipText("Aktiv / Inaktiv schalten");
                    btnEnableUser.setEnabled(false);
                    jPanel5.add(btnEnableUser);
                }
                pnlUserTab.add(jPanel5, CC.xy(3, 7));

                //======== jsp1 ========
                {

                    //---- listMemberIn ----
                    listMemberIn.setModel(new AbstractListModel() {
                        String[] values = {

                        };

                        @Override
                        public int getSize() {
                            return values.length;
                        }

                        @Override
                        public Object getElementAt(int i) {
                            return values[i];
                        }
                    });
                    listMemberIn.setDragEnabled(true);
                    listMemberIn.setEnabled(false);
                    listMemberIn.setFont(new Font("Arial", Font.PLAIN, 14));
                    listMemberIn.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            listMembershipMouseClicked(e);
                        }
                    });
                    jsp1.setViewportView(listMemberIn);
                }
                pnlUserTab.add(jsp1, CC.xy(3, 3, CC.FILL, CC.FILL));

                //======== jsp2 ========
                {

                    //---- listAvailableGroups ----
                    listAvailableGroups.setModel(new AbstractListModel() {
                        String[] values = {

                        };

                        @Override
                        public int getSize() {
                            return values.length;
                        }

                        @Override
                        public Object getElementAt(int i) {
                            return values[i];
                        }
                    });
                    listAvailableGroups.setDragEnabled(true);
                    listAvailableGroups.setEnabled(false);
                    listAvailableGroups.setFont(new Font("Arial", Font.PLAIN, 14));
                    listAvailableGroups.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            listGroupsMouseClicked(e);
                        }
                    });
                    jsp2.setViewportView(listAvailableGroups);
                }
                pnlUserTab.add(jsp2, CC.xy(5, 3, CC.FILL, CC.FILL));
            }
            jtpMain.addTab("Benutzer", pnlUserTab);


            //======== pnlGroupTab ========
            {
                pnlGroupTab.setLayout(new FormLayout(
                        "default, $lcgap, default",
                        "fill:default"));

                //======== pnlLeftGrp ========
                {
                    pnlLeftGrp.setLayout(new FormLayout(
                            "default",
                            "fill:default, $lgap, fill:default"));

                    //======== jScrollPane3 ========
                    {

                        //---- lstAllGroups ----
                        lstAllGroups.setModel(new AbstractListModel() {
                            String[] values = {
                                    "Item 1",
                                    "Item 2",
                                    "Item 3",
                                    "Item 4",
                                    "Item 5"
                            };

                            @Override
                            public int getSize() {
                                return values.length;
                            }

                            @Override
                            public Object getElementAt(int i) {
                                return values[i];
                            }
                        });
                        jScrollPane3.setViewportView(lstAllGroups);
                    }
                    pnlLeftGrp.add(jScrollPane3, CC.xy(1, 1));

                    //======== jPanel8 ========
                    {
                        jPanel8.setLayout(new BoxLayout(jPanel8, BoxLayout.X_AXIS));

                        //---- btnEditGroup ----
                        btnEditGroup.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit.png")));
                        btnEditGroup.setToolTipText("\u00c4nderungen vornehmen.");
                        btnEditGroup.setEnabled(false);
                        jPanel8.add(btnEditGroup);

                        //---- btnAddGroup ----
                        btnAddGroup.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_add.png")));
                        btnAddGroup.setToolTipText("Neuen Mitarbeiter eintragen.");
                        jPanel8.add(btnAddGroup);

                        //---- btnDeleteGroup ----
                        btnDeleteGroup.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_remove.png")));
                        btnDeleteGroup.setToolTipText("Gruppe l\u00f6schen");
                        btnDeleteGroup.setEnabled(false);
                        jPanel8.add(btnDeleteGroup);
                    }
                    pnlLeftGrp.add(jPanel8, CC.xy(1, 3));
                }
                pnlGroupTab.add(pnlLeftGrp, CC.xy(1, 1));

                //======== pnlRightGrp ========
                {

                    //======== jPanel6 ========
                    {
                        jPanel6.setBorder(LineBorder.createBlackLineBorder());

                        //======== jScrollPane5 ========
                        {

                            //---- treeRights ----
                            treeRights.setEnabled(false);
                            jScrollPane5.setViewportView(treeRights);
                        }

                        //---- jLabel10 ----
                        jLabel10.setBackground(Color.blue);
                        jLabel10.setFont(new Font("Lucida Grande", Font.BOLD, 13));
                        jLabel10.setForeground(Color.yellow);
                        jLabel10.setHorizontalAlignment(SwingConstants.CENTER);
                        jLabel10.setText("Rechte");
                        jLabel10.setOpaque(true);

                        GroupLayout jPanel6Layout = new GroupLayout(jPanel6);
                        jPanel6.setLayout(jPanel6Layout);
                        jPanel6Layout.setHorizontalGroup(
                                jPanel6Layout.createParallelGroup()
                                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jScrollPane5, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                                                        .addComponent(jLabel10, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE))
                                                .addContainerGap())
                        );
                        jPanel6Layout.setVerticalGroup(
                                jPanel6Layout.createParallelGroup()
                                        .addGroup(jPanel6Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jLabel10)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jScrollPane5, GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                                                .addContainerGap())
                        );
                    }

                    //======== jPanel7 ========
                    {

                        //---- btnCancelGroups ----
                        btnCancelGroups.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                        btnCancelGroups.setToolTipText("Abbrechen");
                        btnCancelGroups.setEnabled(false);

                        //---- btnSaveGroups ----
                        btnSaveGroups.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                        btnSaveGroups.setToolTipText("Sichern");
                        btnSaveGroups.setEnabled(false);

                        GroupLayout jPanel7Layout = new GroupLayout(jPanel7);
                        jPanel7.setLayout(jPanel7Layout);
                        jPanel7Layout.setHorizontalGroup(
                                jPanel7Layout.createParallelGroup()
                                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                                .addContainerGap(12, Short.MAX_VALUE)
                                                .addComponent(btnSaveGroups)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnCancelGroups)
                                                .addContainerGap())
                        );
                        jPanel7Layout.setVerticalGroup(
                                jPanel7Layout.createParallelGroup()
                                        .addGroup(jPanel7Layout.createSequentialGroup()
                                                .addGap(7, 7, 7)
                                                .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(btnSaveGroups, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnCancelGroups, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addContainerGap())
                        );
                    }

                    //======== jPanel9 ========
                    {
                        jPanel9.setBorder(LineBorder.createBlackLineBorder());

                        //---- jLabel11 ----
                        jLabel11.setText("Gruppenbezeichnung");

                        //---- jLabel12 ----
                        jLabel12.setText("Erl\u00e4uterung");

                        //---- txtGKennung ----
                        txtGKennung.setDragEnabled(false);
                        txtGKennung.addCaretListener(new CaretListener() {
                            @Override
                            public void caretUpdate(CaretEvent e) {
                                txtGKennungCaretUpdate(e);
                            }
                        });
                        txtGKennung.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                txtGKennungActionPerformed(e);
                            }
                        });

                        //---- txtGroupDescription ----
                        txtGroupDescription.setDragEnabled(false);
                        txtGroupDescription.addCaretListener(new CaretListener() {
                            @Override
                            public void caretUpdate(CaretEvent e) {
                                txtGroupDescriptionCaretUpdate(e);
                            }
                        });

                        //---- lblGKennungFree ----
                        lblGKennungFree.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ballred.png")));

                        //---- cbExamen ----
                        cbExamen.setText("Examen");
                        cbExamen.setToolTipText("Die Mitgliedschaft in dieser Gruppe gew\u00e4hrt Examensrechte.");
                        cbExamen.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                cbExamenItemStateChanged(e);
                            }
                        });

                        GroupLayout jPanel9Layout = new GroupLayout(jPanel9);
                        jPanel9.setLayout(jPanel9Layout);
                        jPanel9Layout.setHorizontalGroup(
                                jPanel9Layout.createParallelGroup()
                                        .addGroup(jPanel9Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(jPanel9Layout.createParallelGroup()
                                                        .addComponent(jLabel11)
                                                        .addComponent(jLabel12))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel9Layout.createParallelGroup()
                                                        .addComponent(txtGroupDescription, GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                                                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                                                                .addComponent(txtGKennung, GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(lblGKennungFree)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(cbExamen)))
                                                .addContainerGap())
                        );
                        jPanel9Layout.setVerticalGroup(
                                jPanel9Layout.createParallelGroup()
                                        .addGroup(jPanel9Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(jPanel9Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(txtGKennung, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                                                        .addComponent(jLabel11)
                                                        .addComponent(lblGKennungFree, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                                                        .addComponent(cbExamen, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel9Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel12)
                                                        .addComponent(txtGroupDescription, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                .addGap(30, 30, 30))
                        );
                    }

                    GroupLayout pnlRightGrpLayout = new GroupLayout(pnlRightGrp);
                    pnlRightGrp.setLayout(pnlRightGrpLayout);
                    pnlRightGrpLayout.setHorizontalGroup(
                            pnlRightGrpLayout.createParallelGroup()
                                    .addGroup(pnlRightGrpLayout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(pnlRightGrpLayout.createParallelGroup()
                                                    .addComponent(jPanel9, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addGroup(pnlRightGrpLayout.createSequentialGroup()
                                                            .addGap(222, 222, 222)
                                                            .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                    .addComponent(jPanel7, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addContainerGap())
                    );
                    pnlRightGrpLayout.setVerticalGroup(
                            pnlRightGrpLayout.createParallelGroup()
                                    .addGroup(GroupLayout.Alignment.TRAILING, pnlRightGrpLayout.createSequentialGroup()
                                            .addContainerGap()
                                            .addComponent(jPanel9, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jPanel7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    );
                }
                pnlGroupTab.add(pnlRightGrp, CC.xy(3, 1));
            }
            jtpMain.addTab("Gruppen", pnlGroupTab);

        }
        add(jtpMain);
    }// </editor-fold>//GEN-END:initComponents

//    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
//        if (selectedUser.getMd5pw() == null) {
//            generatePassword();
//        }
//        saveUser();
//        mode = BROWSE_USER;
//        setLeftSideOnUserTab();
//        setRightSideOnUserTab();
//    }//GEN-LAST:event_btnSaveActionPerformed
//
//    private void btnAddUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUserActionPerformed
//        selectedUser = new Users();
//        mode = NEW_USER;
//        ukennung_status = CONFLICT;
//        setRightSideOnUserTab();
//        setLeftSideOnUserTab();
//        txtVorname.requestFocus();
//    }//GEN-LAST:event_btnAddUserActionPerformed
//
//    private void txtVornameCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtVornameCaretUpdate
//        if (mode == NEW_USER) {
//            txtUKennung.setText(SYSTools.generateUKennung(txtName.getText(), txtVorname.getText()));
//        }
//        setRightButtonsOnUserTab();
//    }//GEN-LAST:event_txtVornameCaretUpdate
//
//    private void txtNameCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtNameCaretUpdate
//        if (mode == NEW_USER) {
//            txtUKennung.setText(SYSTools.generateUKennung(txtName.getText(), txtVorname.getText()));
//        }
//        setRightButtonsOnUserTab();
//    }//GEN-LAST:event_txtNameCaretUpdate
//
//    /**
//     * siehe: @EMRGX
//     *
//     * @param evt
//     */
//    private void txtEMailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEMailFocusLost
//        // Später: txtEMail.getText().matches("\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}\b");
//        selectedUser.setEMail(txtEMail.getText());
//    }//GEN-LAST:event_txtEMailFocusLost
//
//    private void txtUKennungCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtUKennungCaretUpdate
//        if (mode == NEW_USER) {
//            EntityManager em = OPDE.createEM();
//            Query query = em.createNamedQuery("Users.findByUKennung");
//            query.setParameter("uKennung", txtUKennung.getText());
//            if (query.getResultList().isEmpty()) {
//                lblUKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballgreen.png")));
//                lblUKennungFree.setToolTipText(null);
//                ukennung_status = OK;
//                selectedUser.setUKennung(txtUKennung.getText().trim());
//            } else {
//                lblUKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballred.png")));
//                lblUKennungFree.setToolTipText("Benutzerkennung ist schon vergeben.");
//                ukennung_status = CONFLICT;
//            }
//            setRightButtonsOnUserTab();
//            em.close();
//        }
//    }//GEN-LAST:event_txtUKennungCaretUpdate
//
//    private void btnPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasswordActionPerformed
//        if (JOptionPane.showConfirmDialog(this, "Möchten Sie das Password zurücksetzen ?", "Passwort erstellen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//            generatePassword();
//            mode = BROWSE_USER;
//            setLeftSideOnUserTab();
//            setRightSideOnUserTab();
//        }
//    }//GEN-LAST:event_btnPasswordActionPerformed

    private void listGroupsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listGroupsMouseClicked
        if (evt.getClickCount() == 2) {
//            selectedUser.getOcgroups().add((Groups) listGroups.getSelectedValue());
//            listMembership.setModel(SYSTools.newListModel((List) selectedUser.getOcgroups()));
//
//            DefaultListModel dlmMember = (DefaultListModel) listMembership.getModel();
//            DefaultListModel dlmGroup = (DefaultListModel) listGroups.getModel();
//
//            dlmMember.addElement(listGroups.getSelectedValue());
//            dlmGroup.removeElement(listGroups.getSelectedValue());

        }
    }//GEN-LAST:event_listGroupsMouseClicked

    private void listMembershipMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMembershipMouseClicked
        if (evt.getClickCount() == 2) {

//            DefaultListModel dlmMember = (DefaultListModel) listMembership.getModel();
//            DefaultListModel dlmGroup = (DefaultListModel) listGroups.getModel();
//
//            dlmGroup.addElement(listMembership.getSelectedValue());
//            dlmMember.removeElement(listMembership.getSelectedValue());

        }
    }//GEN-LAST:event_listMembershipMouseClicked

    //    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
//        if (mode == EDIT_USER) {
//            EntityManager em = OPDE.createEM();
//            em.refresh(selectedUser);
//            em.close();
//        } else {
//            selectedUser = new Users();
//        }
//        mode = BROWSE_USER;
//        setLeftSideOnUserTab();
//        setRightSideOnUserTab();
//    }//GEN-LAST:event_btnCancelActionPerformed
//
//    private void txtVornameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVornameFocusLost
//        selectedUser.setVorname(txtVorname.getText());
//    }//GEN-LAST:event_txtVornameFocusLost
//
//    private void txtNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusLost
//        selectedUser.setNachname(txtName.getText());
//    }//GEN-LAST:event_txtNameFocusLost
//
//    private void btnEnableUserItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnEnableUserItemStateChanged
//        selectedUser.setStatus(btnEnableUser.isSelected() ? UsersTools.STATUS_ACTIVE : UsersTools.STATUS_INACTIVE);
//        saveUser();
//    }//GEN-LAST:event_btnEnableUserItemStateChanged
//
//    private void btnEditUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditUserActionPerformed
//        mode = EDIT_USER;
//        setLeftSideOnUserTab();
//        setRightSideOnUserTab();
//    }//GEN-LAST:event_btnEditUserActionPerformed
//
//    private void btnAddGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddGroupActionPerformed
//        mode = NEW_GROUP;
//        selectedGroup = new Groups();
//        selectedGroup.setMembers(new ArrayList());
//        setLeftSideOnGroupTab();
//        setRightSideOnGroupTab();
//    }//GEN-LAST:event_btnAddGroupActionPerformed
//
//    private void btnEditGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditGroupActionPerformed
//        mode = EDIT_GROUP;
//        setLeftSideOnGroupTab();
//        setRightSideOnGroupTab();
//    }//GEN-LAST:event_btnEditGroupActionPerformed
//
//    private void btnDeleteGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteGroupActionPerformed
//        if (JOptionPane.showConfirmDialog(this, "Möchten Sie die Gruppe '" + selectedGroup.getGkennung() + "' wirklich löschen  ?", "Gruppe löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//            EntityManager em = OPDE.createEM();
//
//            try {
//                em.getTransaction().begin();
//                em.remove(selectedGroup);
//                em.getTransaction().commit();
//            } catch (Exception e1) {
//                em.getTransaction().rollback();
////                new DlgException(e1);
//            } finally {
//                em.close();
//            }
//            selectedGroup = null;
//            mode = BROWSE_GROUP;
//            setGroupTab();
//        }
//    }//GEN-LAST:event_btnDeleteGroupActionPerformed
//
//    private void btnCancelGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelGroupsActionPerformed
//        if (mode == EDIT_GROUP) {
//            EntityManager em = OPDE.createEM();
//            em.refresh(selectedGroup);
//            em.close();
//        } else {
//            selectedGroup = null;
//        }
//        mode = BROWSE_GROUP;
//        setLeftSideOnGroupTab();
//        setRightSideOnGroupTab();
//    }//GEN-LAST:event_btnCancelGroupsActionPerformed
//
//    private void btnSaveGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveGroupsActionPerformed
//        saveGroup();
//    }//GEN-LAST:event_btnSaveGroupsActionPerformed
//
    private void jtpMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpMainStateChanged
        if (jtpMain.getSelectedIndex() == TAB_USER) {
            IntClassesTools.clearEntitiesFromAllInternalClasses();
            mode = BROWSE_USER;
//            setUserTab();
        } else {
            mode = BROWSE_GROUP;
//            setGroupTab();
        }
    }//GEN-LAST:event_jtpMainStateChanged

    private void txtGKennungCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtGKennungCaretUpdate
        if (mode == NEW_GROUP) {
            if (txtGKennung.getText().matches("[a-zA-Z0-9]+")) {
                EntityManager em = OPDE.createEM();
                Query query = em.createNamedQuery("Groups.findByGkennung");
                query.setParameter("gkennung", txtGKennung.getText());
                if (query.getResultList().isEmpty()) {
                    lblGKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballgreen.png")));
                    lblGKennungFree.setToolTipText(null);
                    gkennung_status = OK;
                    selectedGroup.setGkennung(txtGKennung.getText().trim());
                } else {
                    lblGKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballred.png")));
                    lblGKennungFree.setToolTipText("Gruppenkennung ist schon vergeben.");
                    selectedGroup.setGkennung(null);
                    gkennung_status = CONFLICT;
                }
                em.close();
            } else {
                lblGKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballred.png")));
                lblGKennungFree.setToolTipText("Keine Leer- oder Sonderzeichen als Gruppenkennung verwenden.");
                selectedGroup.setGkennung(null);
                gkennung_status = CONFLICT;
            }
//            setRightButtonsOnGroupTab();
        }
    }//GEN-LAST:event_txtGKennungCaretUpdate

    private void txtGKennungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGKennungActionPerformed
        txtGroupDescription.requestFocus();
    }//GEN-LAST:event_txtGKennungActionPerformed

    private void txtGroupDescriptionCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtGroupDescriptionCaretUpdate
        if (selectedGroup != null) {
            selectedGroup.setBeschreibung(txtGroupDescription.getText().trim());
        }
    }//GEN-LAST:event_txtGroupDescriptionCaretUpdate

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        IntClassesTools.clearEntitiesFromAllInternalClasses();
    }//GEN-LAST:event_formWindowClosing

    private void cbExamenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbExamenItemStateChanged
        if (mode != BROWSE_GROUP) {
            selectedGroup.setExamen(cbExamen.isSelected());
        }
    }//GEN-LAST:event_cbExamenItemStateChanged

//    private void generatePassword() {
//        Random generator = new Random(System.currentTimeMillis());
//        String pw = txtName.getText().substring(0, 1).toLowerCase() + txtVorname.getText().substring(0, 1).toLowerCase() + SYSTools.padL(Integer.toString(generator.nextInt(9999)), 4, "0");
//        selectedUser.setMd5pw(SYSTools.hashword(pw));
//        if (JOptionPane.showConfirmDialog(this, "Das Passwort wurde auf '" + pw + "' gesetzt.\nMöchten Sie einen Beleg ausdrucken ?", "Passwort", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//            print(pw);
//        }
//    }

    private void print(String password) {
        String html;

        try {
            html = SYSTools.readFileAsString(OPDE.getOpwd() + System.getProperty("file.separator") + "newuser.html");
        } catch (IOException ie) {
            html = "<body>"
                    + "<h1>Access to Offene-Pflege.de (OPDE)</h1>"
                    + "<br/>"
                    + "<br/>"
                    + "<br/>"
                    + "<h2>For <opde-user-fullname/></h2>"
                    + "<br/>"
                    + "<br/>"
                    + "<br/>"
                    + "<p>UserID: <b><opde-user-userid/></b></p>"
                    + "<p>Password: <b><opde-user-pw/></b></p>"
                    + "<br/>"
                    + "<br/>"
                    + "Please keep this note in a safe place. Don't tell Your password to anyone."
                    + "</body>";
        }

        html = SYSTools.replace(html, "<opde-user-fullname/>", selectedUser.getNameUndVorname());
        html = SYSTools.replace(html, "<opde-user-userid/>", selectedUser.getUKennung());
        html = SYSTools.replace(html, "<opde-user-pw/>", password);
        html = SYSTools.htmlUmlautConversion(html);


        SYSFilesTools.print(html, true);
    }

    /**
     * Alle aus der Liste der Mitgliedschaften hinzufügen. Und alle aus der Gruppenliste entfernen.
     */
//    private void setMemberships() {
//        for (int i = 0; i < listMembership.getModel().getSize(); i++) {
//            Groups group = (Groups) listMembership.getModel().getElementAt(i);
//            if (!selectedUser.getGroups().contains(group)) {
//                selectedUser.getGroups().add(group);
//            }
//        }
//        for (int i = 0; i < listGroups.getModel().getSize(); i++) {
//            Groups group = (Groups) listGroups.getModel().getElementAt(i);
//            if (selectedUser.getGroups().contains(group)) {
//                selectedUser.getGroups().remove(group);
//            }
//        }
//    }
//
//    private void saveGroup() {
//        EntityManager em = OPDE.createEM();
//
//        try {
//            em.getTransaction().begin();
//            // TODO: Das muss anderes geregelt werden.
//            if (em.contains(selectedGroup)) { // Gruppe existiert schon in der Datenbank
//                em.merge(selectedGroup);
//            } else {
//                em.persist(selectedGroup);
//            }
//            if (!selectedGroup.getGkennung().equalsIgnoreCase("admin")) { // Bei Admin kann man den Beaum nicht ändern.
//                IntClassesTools.saveTree((DefaultMutableTreeNode) treeRights.getModel().getRoot(), sm);
//            }
//            em.getTransaction().commit();
//
//        } catch (Exception e1) {
//            em.getTransaction().rollback();
//            OPDE.fatal(e1);
//        } finally {
//            em.close();
//        }
//        selectedGroup = null;
//        mode = BROWSE_GROUP;
//        setGroupTab();
//    }
//
//    private void saveUser() {
//        setMemberships();
//        EntityManager em = OPDE.createEM();
//
//        try {
//            em.getTransaction().begin();
//            if (selectedUser.getUKennung() == null) {
//                generatePassword();
//                em.persist(selectedUser);
//            } else {
//                em.merge(selectedUser);
//            }
//            em.getTransaction().commit();
//        } catch (Exception e1) {
//            em.getTransaction().rollback();
//            OPDE.fatal(e1);
//        } finally {
//            em.close();
//        }
//
//        if (mode == NEW_USER) {
//            //loadUserTable();
//
//            Query query = em.createNamedQuery("Users.findAllSorted");
//            tblUsers.setModel(new TMUser(query.getResultList()));
//
//
//            //((TMUser) tblUsers.getModel()).updateTable();
//        } else {
//            ((TMUser) tblUsers.getModel()).updateRow(tblUsers.getSelectedRow());
//        }
//    }
    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(3));
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
        GUITools.addAllComponents(mypanel, addFilters());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }

    private java.util.List<Component> addFilters() {
        java.util.List<Component> list = new ArrayList<Component>();

        /***
         *      _   _      ____ _                    _
         *     | |_| |__  / ___| | ___  ___  ___  __| |
         *     | __| '_ \| |   | |/ _ \/ __|/ _ \/ _` |
         *     | |_| |_) | |___| | (_) \__ \  __/ (_| |
         *      \__|_.__/ \____|_|\___/|___/\___|\__,_|
         *
         */
        tbRefactor = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.showclosed"));
        tbRefactor.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase) return;
//                buildPanel();
            }
        });
        tbRefactor.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbRefactor);


        return list;
    }


    private java.util.List<Component> addCommands() {

        java.util.List<Component> list = new ArrayList<Component>();

        /***
         *      _     _            _       _     _
         *     | |__ | |_ _ __    / \   __| | __| |
         *     | '_ \| __| '_ \  / _ \ / _` |/ _` |
         *     | |_) | |_| | | |/ ___ \ (_| | (_| |
         *     |____/ \__|_| |_/_/   \_\__,_|\__,_|
         *
         */

        final JideButton btnAddUser = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnAddUser"), SYSConst.icon22addUser, null);
        btnAddUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
//                    new DlgProcess(new QProcess(resident), new Closure() {
//                        @Override
//                        public void execute(Object o) {
//                            if (o != null) {
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    QProcess qProcess = em.merge((QProcess) o);
//                                    em.getTransaction().commit();
//                                    processList.add(qProcess);
//                                    qProcessMap.put(qProcess, createCP4(qProcess));
//                                    buildPanel();
//                                } catch (Exception e) {
//                                    em.getTransaction().rollback();
//                                } finally {
//                                    em.close();
//                                }
//                            }
//                        }
//                    });
            }
        });
        list.add(btnAddUser);

        final JideButton btnAddGroup = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnAddGroup"), SYSConst.icon22addGroup, null);
        btnAddGroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
//                    new DlgProcess(new QProcess(resident), new Closure() {
//                        @Override
//                        public void execute(Object o) {
//                            if (o != null) {
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    QProcess qProcess = em.merge((QProcess) o);
//                                    em.getTransaction().commit();
//                                    processList.add(qProcess);
//                                    qProcessMap.put(qProcess, createCP4(qProcess));
//                                    buildPanel();
//                                } catch (Exception e) {
//                                    em.getTransaction().rollback();
//                                } finally {
//                                    em.close();
//                                }
//                            }
//                        }
//                    });
            }
        });
        list.add(btnAddGroup);


        return list;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTabbedPane jtpMain;
    private JPanel pnlUserTab;
    private JLabel label3;
    private JLabel label1;
    private JLabel label2;
    private JScrollPane jsp0;
    private JList listUsers;
    private JPanel jPanel5;
    private JButton btnAddUser;
    private JButton btnPassword;
    private JButton btnEditUser;
    private JToggleButton btnEnableUser;
    private JScrollPane jsp1;
    private JList listMemberIn;
    private JScrollPane jsp2;
    private JList listAvailableGroups;
    private JPanel pnlGroupTab;
    private JPanel pnlLeftGrp;
    private JScrollPane jScrollPane3;
    private JList lstAllGroups;
    private JPanel jPanel8;
    private JButton btnEditGroup;
    private JButton btnAddGroup;
    private JButton btnDeleteGroup;
    private JPanel pnlRightGrp;
    private JPanel jPanel6;
    private JScrollPane jScrollPane5;
    private JTree treeRights;
    private JLabel jLabel10;
    private JPanel jPanel7;
    private JButton btnCancelGroups;
    private JButton btnSaveGroups;
    private JPanel jPanel9;
    private JLabel jLabel11;
    private JLabel jLabel12;
    private JTextField txtGKennung;
    private JTextField txtGroupDescription;
    private JLabel lblGKennungFree;
    private JCheckBox cbExamen;
    // End of variables declaration//GEN-END:variables
}
