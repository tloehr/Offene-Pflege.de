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
package op.ma.admin;

import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import entity.Groups;
import entity.Users;
import entity.UsersTools;
import entity.files.SYSFilesTools;
import entity.system.IntClassesTools;
import op.OPDE;
import op.tools.*;
import tablemodels.TMUser;
import tablerenderer.RNDOCUsers;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author tloehr
 */
public class FrmUser extends javax.swing.JFrame {

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

    /**
     * Creates new form FrmUser
     */
    public FrmUser() {
        initComponents();
        btnEnableUser.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/user_active.png")));
        btnEnableUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/user_inactive.png")));
        //mode = BROWSE_USER;
        ukennung_status = INDIFFERENT;
        gkennung_status = INDIFFERENT;
        setTitle(SYSTools.getWindowTitle("Benutzerverwaltung"));

        lstAllGroups.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {

                    if (lstAllGroups.getSelectedIndex() >= 0) {
                        selectedGroup = (Groups) lstAllGroups.getSelectedValue();
                        EntityManager em1 = OPDE.createEM();
                        em1.refresh(selectedGroup);
                        em1.close();
                    } else {
                        selectedGroup = null;
                    }
                    setLeftSideOnGroupTab();
                    setRightSideOnGroupTab();
                }
            }
        });

        jtpMain.setSelectedIndex(TAB_USER);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        SYSTools.center(this);
    }

    private void loadUserTable() {
        EntityManager em = OPDE.createEM();
        try {
            Query query = em.createNamedQuery("Users.findAllSorted");
            tblUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblUsers.setModel(new TMUser(query.getResultList()));
            tblUsers.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent lse) {
                    if (!lse.getValueIsAdjusting()) {
                        if (tblUsers.getSelectedRow() >= 0) {
                            selectedUser = ((TMUser) tblUsers.getModel()).getUserAt(tblUsers.getSelectedRow());
                            setRightSideOnUserTab();
                            setLeftSideOnUserTab();
                        } else {
                            selectedUser = null;
                        }
                    }
                }
            });
            tblUsers.getColumnModel().getColumn(0).setCellRenderer(new RNDOCUsers());
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }

    }

    private void setGroupTab() {

        if (ldlMember == null) {
            // Wird immer aufgerufen, wenn sich die Daten in den Member und Userlisten ändern
            ldlMember = new ListDataListener() {

                @Override
                public void intervalAdded(ListDataEvent e) {
                    Users draggedUser = (Users) lstMembers.getModel().getElementAt(e.getIndex0());
                    selectedGroup.getMembers().add(draggedUser);
                }

                @Override
                public void intervalRemoved(ListDataEvent e) {
                }

                // Wird bei uns nicht aufgerufen.
                @Override
                public void contentsChanged(ListDataEvent e) {
                }
            };
            ldlUser = new ListDataListener() {

                @Override
                public void intervalAdded(ListDataEvent e) {
                    Users draggedUser = (Users) lstUsers.getModel().getElementAt(e.getIndex0());
                    selectedGroup.getMembers().remove(draggedUser);
                }

                @Override
                public void intervalRemoved(ListDataEvent e) {
                }

                // Wird bei uns nicht aufgerufen.
                @Override
                public void contentsChanged(ListDataEvent e) {
                }
            };
        }

        lstAllGroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstAllGroups.setTransferHandler(null);
        lstAllGroups.setModel(SYSTools.newListModel("Groups.findAllSorted"));
        setLeftSideOnGroupTab();
        setRightSideOnGroupTab();

    }

    private void enableRightSideOnGroupTab() {
        // Bei Everyone kann man die Mitglieder nicht ändern
        lstMembers.setEnabled(mode != BROWSE_GROUP && !selectedGroup.getGkennung().equalsIgnoreCase("everyone"));
        lstUsers.setEnabled(mode != BROWSE_GROUP && !selectedGroup.getGkennung().equalsIgnoreCase("everyone"));
        // Bei Admin nicht die Rechte.
        treeRights.setEnabled(mode != BROWSE_GROUP && !selectedGroup.getGkennung().equalsIgnoreCase("admin"));
        txtGKennung.setEnabled(mode == NEW_GROUP);
        txtGroupDescription.setEnabled(mode != BROWSE_GROUP);
        cbExamen.setEnabled(mode != BROWSE_GROUP);
    }

    //    private void setUsersAndMembersOnGroupTab() {
//        if (mode == BROWSE_GROUP) {
//        } else if (mode == NEW_GROUP) {
//        } else { // EDIT_GROUP
//        }
//
//    }
    private void setLeftSideOnGroupTab() {
        lstAllGroups.setEnabled(mode == BROWSE_GROUP);
        btnAddGroup.setEnabled(mode == BROWSE_GROUP);
        btnDeleteGroup.setEnabled(mode == BROWSE_GROUP && selectedGroup != null && !selectedGroup.isSystem());
        btnEditGroup.setEnabled(mode == BROWSE_GROUP && selectedGroup != null);
    }

    private void setRightSideOnGroupTab() {
        enableRightSideOnGroupTab();
        setRightButtonsOnGroupTab();

        if (selectedGroup != null) {

            txtGKennung.setText(SYSTools.catchNull(selectedGroup.getGkennung()));
            txtGroupDescription.setText(SYSTools.catchNull(selectedGroup.getBeschreibung()));
            cbExamen.setSelected(selectedGroup.isExamen());

            lstMembers.getModel().removeListDataListener(ldlMember);
            lstUsers.getModel().removeListDataListener(ldlUser);

            // TODO: hier liegt noch was im Argen
            // Hab den Teil auskommentiert
//            if (em.contains(selectedGroup)) { // EntityBean ist schon gespeichert.
//                if (selectedGroup.getGkennung().equalsIgnoreCase("everyone")) {
//                    // everyone hat immer alle Benutzer als Mitglied.
//                    lstMembers.setModel(SYSTools.newListModel("Users.findAllSorted"));
//                    lstUsers.setModel(new DefaultListModel());
//                } else {
//                    lstMembers.setModel(SYSTools.newListModel("Users.findAllMembers", new Object[]{"group", selectedGroup}));
//                    lstUsers.setModel(SYSTools.newListModel("Users.findAllNonMembers", new Object[]{"group", selectedGroup}));
//                }
//            } else {
//                lstMembers.setModel(new DefaultListModel());
//                lstUsers.setModel(SYSTools.newListModel("Users.findAllSorted"));
//            }

            lstMembers.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            lstUsers.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            TxHdlrUsers tx = new TxHdlrUsers();
            lstMembers.setTransferHandler(tx);
            lstUsers.setTransferHandler(tx);
            lstMembers.getModel().addListDataListener(ldlMember);
            lstUsers.getModel().addListDataListener(ldlUser);

            IntClassesTools.clearEntitiesFromAllInternalClasses();
            ArrayList treeData = IntClassesTools.getIntClassesAsTree(selectedGroup);
            treeRights.setModel(new DefaultTreeModel((MutableTreeNode) treeData.get(IntClassesTools.ROOT)));

            cm = new CheckTreeManager(treeRights, true);
            sm = (CheckTreeSelectionModel) cm.getSelectionModel();
            sm.addSelectionPaths((TreePath[]) treeData.get(IntClassesTools.SELECTION));
            //originalSelection = sm.getSelectionPaths();
//            if (cblst != null) {
//                sm.removeTreeSelectionListener(cblst);
//            }
            //cblst = new cbClickListener();
            //sm.addTreeSelectionListener(cblst);
            SYSTools.expandAll(treeRights);
        } else {
            txtGKennung.setText("");
            txtGroupDescription.setText("");
            cbExamen.setSelected(false);
            lstUsers.setModel(new DefaultListModel());
            lstMembers.setModel(new DefaultListModel());
            treeRights.setModel(new DefaultTreeModel(null));
        }

        if (mode == NEW_GROUP) {
            txtGKennung.setText("");
            txtGroupDescription.setText("");
            cbExamen.setSelected(false);
            txtGKennung.requestFocus();
        }
        if (mode == EDIT_GROUP) {
            txtGroupDescription.requestFocus();
        }
        setRightButtonsOnGroupTab();
    }

    private void setUserTab() {
        loadUserTable();
        TxHdlrGroups tx = new TxHdlrGroups();
        listMembership.setBackground(Color.LIGHT_GRAY);
        listGroups.setBackground(Color.LIGHT_GRAY);
        listMembership.setTransferHandler(tx);
        listGroups.setTransferHandler(tx);
        listMembership.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listGroups.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    private void setRightSideOnUserTab() {
        txtUKennung.setEnabled(mode == NEW_USER);
        txtVorname.setEnabled(mode != BROWSE_USER);
        txtName.setEnabled(mode != BROWSE_USER);
        txtEMail.setEnabled(mode != BROWSE_USER);
        listMembership.setEnabled(mode != BROWSE_USER);
        listGroups.setEnabled(mode != BROWSE_USER);

        // Durch die HTML Darstellung wird die Schrift nicht grau, daher muss ich
        // hier etwas nachhelfen.
        if (listGroups.isEnabled()) {
            listGroups.setBackground(Color.WHITE);
        } else {
            listGroups.setBackground(Color.LIGHT_GRAY);
        }
        if (listMembership.isEnabled()) {
            listMembership.setBackground(Color.WHITE);
        } else {
            listMembership.setBackground(Color.LIGHT_GRAY);
        }

        txtUKennung.setText(selectedUser.getUKennung());
        txtVorname.setText(selectedUser.getVorname());
        txtName.setText(selectedUser.getNachname());
        txtEMail.setText(SYSTools.catchNull(selectedUser.getEMail()));
        listMembership.setModel(SYSTools.newListModel((List) selectedUser.getGroups()));
        listGroups.setModel(SYSTools.newListModel("Groups.findAllUnassigned", new Object[]{"ocuser", selectedUser}));

        if (mode == NEW_USER) {
            lblUKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballred.png")));
        } else {
            lblUKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballblue.png")));
        }

        setRightButtonsOnUserTab();
    }

    private void setRightButtonsOnGroupTab() {
        btnSaveGroups.setEnabled(mode != BROWSE_GROUP && gkennung_status != CONFLICT);
        btnCancelGroups.setEnabled(mode != BROWSE_GROUP);
    }

    private void setRightButtonsOnUserTab() {
        btnSave.setEnabled(mode != BROWSE_USER && ukennung_status != CONFLICT);
        btnCancel.setEnabled(mode != BROWSE_USER);
    }

    private void setLeftSideOnUserTab() {
        if (mode == BROWSE_USER && selectedUser != null) {
            btnEnableUser.setSelected(selectedUser.isActive());
        }
        btnEnableUser.setEnabled(mode == BROWSE_USER && selectedUser != null);
        btnAddUser.setEnabled(mode == BROWSE_USER);
        btnEditUser.setEnabled(mode == BROWSE_USER && selectedUser != null);
        btnPassword.setEnabled(mode == BROWSE_USER && selectedUser != null);
        tblUsers.setEnabled(mode == BROWSE_USER);
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
        pnlLeftUser = new JPanel();
        jScrollPane2 = new JScrollPane();
        tblUsers = new JTable();
        jLabel8 = new JLabel();
        jPanel5 = new JPanel();
        btnAddUser = new JButton();
        btnPassword = new JButton();
        btnEditUser = new JButton();
        btnEnableUser = new JToggleButton();
        pnlRightUser = new JPanel();
        jsp1 = new JScrollPane();
        listMembership = new JList();
        jScrollPane1 = new JScrollPane();
        listGroups = new JList();
        jPanel3 = new JPanel();
        btnCancel = new JButton();
        btnSave = new JButton();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jPanel4 = new JPanel();
        txtUKennung = new JTextField();
        txtName = new JTextField();
        txtEMail = new JTextField();
        jLabel5 = new JLabel();
        txtVorname = new JTextField();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel6 = new JLabel();
        lblUKennungFree = new JLabel();
        pnlGroupTab = new JPanel();
        pnlLeftGrp = new JPanel();
        jScrollPane3 = new JScrollPane();
        lstAllGroups = new JList();
        jPanel8 = new JPanel();
        btnEditGroup = new JButton();
        btnAddGroup = new JButton();
        btnDeleteGroup = new JButton();
        pnlRightGrp = new JPanel();
        jPanel2 = new JPanel();
        jLabel9 = new JLabel();
        jLabel7 = new JLabel();
        jScrollPane6 = new JScrollPane();
        lstUsers = new JList();
        jScrollPane4 = new JScrollPane();
        lstMembers = new JList();
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
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                formWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();

        //======== jtpMain ========
        {
            jtpMain.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    jtpMainStateChanged(e);
                }
            });

            //======== pnlUserTab ========
            {

                //======== pnlLeftUser ========
                {

                    //======== jScrollPane2 ========
                    {

                        //---- tblUsers ----
                        tblUsers.setModel(new DefaultTableModel(
                            new Object[][] {
                                {null, null, null, null},
                                {null, null, null, null},
                                {null, null, null, null},
                                {null, null, null, null},
                            },
                            new String[] {
                                "Title 1", "Title 2", "Title 3", "Title 4"
                            }
                        ));
                        jScrollPane2.setViewportView(tblUsers);
                    }

                    //---- jLabel8 ----
                    jLabel8.setBackground(Color.blue);
                    jLabel8.setForeground(Color.yellow);
                    jLabel8.setHorizontalAlignment(SwingConstants.CENTER);
                    jLabel8.setText("Alle MitarbeiterInnen");
                    jLabel8.setOpaque(true);

                    //======== jPanel5 ========
                    {

                        //---- btnAddUser ----
                        btnAddUser.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_add.png")));
                        btnAddUser.setToolTipText("Neuen Mitarbeiter eintragen.");
                        btnAddUser.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnAddUserActionPerformed(e);
                            }
                        });

                        //---- btnPassword ----
                        btnPassword.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/password.png")));
                        btnPassword.setToolTipText("Passwort erzeugen");
                        btnPassword.setEnabled(false);
                        btnPassword.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnPasswordActionPerformed(e);
                            }
                        });

                        //---- btnEditUser ----
                        btnEditUser.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit.png")));
                        btnEditUser.setToolTipText("\u00c4nderungen vornehmen.");
                        btnEditUser.setEnabled(false);
                        btnEditUser.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnEditUserActionPerformed(e);
                            }
                        });

                        //---- btnEnableUser ----
                        btnEnableUser.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/user_active.png")));
                        btnEnableUser.setToolTipText("Aktiv / Inaktiv schalten");
                        btnEnableUser.setEnabled(false);
                        btnEnableUser.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                btnEnableUserItemStateChanged(e);
                            }
                        });

                        GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
                        jPanel5.setLayout(jPanel5Layout);
                        jPanel5Layout.setHorizontalGroup(
                            jPanel5Layout.createParallelGroup()
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(btnAddUser)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btnEditUser)
                                    .addGap(2, 2, 2)
                                    .addComponent(btnEnableUser)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnPassword)
                                    .addContainerGap())
                        );
                        jPanel5Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnAddUser, btnEditUser, btnEnableUser, btnPassword});
                        jPanel5Layout.setVerticalGroup(
                            jPanel5Layout.createParallelGroup()
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnPassword, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnEnableUser, 0, 0, Short.MAX_VALUE)
                                        .addComponent(btnEditUser, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnAddUser))
                                    .addContainerGap())
                        );
                        jPanel5Layout.linkSize(SwingConstants.VERTICAL, new Component[] {btnAddUser, btnEditUser, btnEnableUser, btnPassword});
                    }

                    GroupLayout pnlLeftUserLayout = new GroupLayout(pnlLeftUser);
                    pnlLeftUser.setLayout(pnlLeftUserLayout);
                    pnlLeftUserLayout.setHorizontalGroup(
                        pnlLeftUserLayout.createParallelGroup()
                            .addGroup(pnlLeftUserLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlLeftUserLayout.createParallelGroup()
                                    .addGroup(pnlLeftUserLayout.createSequentialGroup()
                                        .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                                        .addContainerGap())
                                    .addGroup(pnlLeftUserLayout.createSequentialGroup()
                                        .addComponent(jLabel8, GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                                        .addGap(24, 24, 24))
                                    .addGroup(pnlLeftUserLayout.createSequentialGroup()
                                        .addComponent(jPanel5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(93, Short.MAX_VALUE))))
                    );
                    pnlLeftUserLayout.setVerticalGroup(
                        pnlLeftUserLayout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, pnlLeftUserLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel8)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    );
                }

                //======== pnlRightUser ========
                {

                    //======== jsp1 ========
                    {

                        //---- listMembership ----
                        listMembership.setModel(new AbstractListModel() {
                            String[] values = {

                            };
                            @Override
                            public int getSize() { return values.length; }
                            @Override
                            public Object getElementAt(int i) { return values[i]; }
                        });
                        listMembership.setDragEnabled(true);
                        listMembership.setEnabled(false);
                        listMembership.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                listMembershipMouseClicked(e);
                            }
                        });
                        jsp1.setViewportView(listMembership);
                    }

                    //======== jScrollPane1 ========
                    {

                        //---- listGroups ----
                        listGroups.setModel(new AbstractListModel() {
                            String[] values = {

                            };
                            @Override
                            public int getSize() { return values.length; }
                            @Override
                            public Object getElementAt(int i) { return values[i]; }
                        });
                        listGroups.setDragEnabled(true);
                        listGroups.setEnabled(false);
                        listGroups.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                listGroupsMouseClicked(e);
                            }
                        });
                        jScrollPane1.setViewportView(listGroups);
                    }

                    //======== jPanel3 ========
                    {

                        //---- btnCancel ----
                        btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                        btnCancel.setToolTipText("Abbrechen");
                        btnCancel.setEnabled(false);
                        btnCancel.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnCancelActionPerformed(e);
                            }
                        });

                        //---- btnSave ----
                        btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                        btnSave.setToolTipText("Sichern");
                        btnSave.setEnabled(false);
                        btnSave.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnSaveActionPerformed(e);
                            }
                        });

                        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
                        jPanel3.setLayout(jPanel3Layout);
                        jPanel3Layout.setHorizontalGroup(
                            jPanel3Layout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                    .addContainerGap(63, Short.MAX_VALUE)
                                    .addComponent(btnSave)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btnCancel)
                                    .addContainerGap())
                        );
                        jPanel3Layout.setVerticalGroup(
                            jPanel3Layout.createParallelGroup()
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGap(7, 7, 7)
                                    .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnSave, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnCancel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addContainerGap())
                        );
                    }

                    //---- jLabel3 ----
                    jLabel3.setBackground(new Color(255, 255, 51));
                    jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
                    jLabel3.setText("Mitglied in");
                    jLabel3.setOpaque(true);

                    //---- jLabel4 ----
                    jLabel4.setBackground(new Color(51, 255, 51));
                    jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
                    jLabel4.setText("Verf\u00fcgbare Gruppen");
                    jLabel4.setOpaque(true);

                    //======== jPanel4 ========
                    {

                        //---- txtUKennung ----
                        txtUKennung.setColumns(10);
                        txtUKennung.setDragEnabled(false);
                        txtUKennung.setEnabled(false);
                        txtUKennung.addCaretListener(new CaretListener() {
                            @Override
                            public void caretUpdate(CaretEvent e) {
                                txtUKennungCaretUpdate(e);
                            }
                        });

                        //---- txtName ----
                        txtName.setDragEnabled(false);
                        txtName.setEnabled(false);
                        txtName.addCaretListener(new CaretListener() {
                            @Override
                            public void caretUpdate(CaretEvent e) {
                                txtNameCaretUpdate(e);
                            }
                        });
                        txtName.addFocusListener(new FocusAdapter() {
                            @Override
                            public void focusLost(FocusEvent e) {
                                txtNameFocusLost(e);
                            }
                        });

                        //---- txtEMail ----
                        txtEMail.setDragEnabled(false);
                        txtEMail.setEnabled(false);
                        txtEMail.addFocusListener(new FocusAdapter() {
                            @Override
                            public void focusLost(FocusEvent e) {
                                txtEMailFocusLost(e);
                            }
                        });

                        //---- jLabel5 ----
                        jLabel5.setText("E-Mail");

                        //---- txtVorname ----
                        txtVorname.setDragEnabled(false);
                        txtVorname.setEnabled(false);
                        txtVorname.addCaretListener(new CaretListener() {
                            @Override
                            public void caretUpdate(CaretEvent e) {
                                txtVornameCaretUpdate(e);
                            }
                        });
                        txtVorname.addFocusListener(new FocusAdapter() {
                            @Override
                            public void focusLost(FocusEvent e) {
                                txtVornameFocusLost(e);
                            }
                        });

                        //---- jLabel1 ----
                        jLabel1.setText("Vorname");

                        //---- jLabel2 ----
                        jLabel2.setText("Nachname");

                        //---- jLabel6 ----
                        jLabel6.setText("UKennung");

                        //---- lblUKennungFree ----
                        lblUKennungFree.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ballred.png")));

                        GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
                        jPanel4.setLayout(jPanel4Layout);
                        jPanel4Layout.setHorizontalGroup(
                            jPanel4Layout.createParallelGroup()
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel4Layout.createParallelGroup()
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                            .addComponent(jLabel6)
                                            .addGap(35, 35, 35)
                                            .addComponent(txtUKennung, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(lblUKennungFree))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                            .addComponent(jLabel5)
                                            .addGap(58, 58, 58)
                                            .addComponent(txtEMail, GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                            .addGroup(jPanel4Layout.createParallelGroup()
                                                .addComponent(jLabel1)
                                                .addComponent(jLabel2))
                                            .addGap(33, 33, 33)
                                            .addGroup(jPanel4Layout.createParallelGroup()
                                                .addComponent(txtVorname, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                                                .addComponent(txtName, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE))))
                                    .addContainerGap())
                        );
                        jPanel4Layout.setVerticalGroup(
                            jPanel4Layout.createParallelGroup()
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(txtUKennung, GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                                            .addComponent(lblUKennungFree))
                                        .addComponent(jLabel6, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtVorname, 0, 0, Short.MAX_VALUE)
                                        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(txtName))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtEMail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5))
                                    .addContainerGap())
                        );
                    }

                    GroupLayout pnlRightUserLayout = new GroupLayout(pnlRightUser);
                    pnlRightUser.setLayout(pnlRightUserLayout);
                    pnlRightUserLayout.setHorizontalGroup(
                        pnlRightUserLayout.createParallelGroup()
                            .addGroup(pnlRightUserLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlRightUserLayout.createParallelGroup()
                                    .addGroup(pnlRightUserLayout.createSequentialGroup()
                                        .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addContainerGap())
                                    .addGroup(pnlRightUserLayout.createSequentialGroup()
                                        .addGroup(pnlRightUserLayout.createParallelGroup()
                                            .addComponent(jsp1, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                                            .addComponent(jLabel3, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(pnlRightUserLayout.createParallelGroup()
                                            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                                            .addComponent(jLabel4, GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
                                        .addContainerGap())
                                    .addComponent(jPanel3, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                    );
                    pnlRightUserLayout.setVerticalGroup(
                        pnlRightUserLayout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, pnlRightUserLayout.createSequentialGroup()
                                .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlRightUserLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlRightUserLayout.createParallelGroup()
                                    .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                                    .addComponent(jsp1, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    );
                }

                GroupLayout pnlUserTabLayout = new GroupLayout(pnlUserTab);
                pnlUserTab.setLayout(pnlUserTabLayout);
                pnlUserTabLayout.setHorizontalGroup(
                    pnlUserTabLayout.createParallelGroup()
                        .addGroup(pnlUserTabLayout.createSequentialGroup()
                            .addComponent(pnlLeftUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(pnlRightUser, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
                pnlUserTabLayout.setVerticalGroup(
                    pnlUserTabLayout.createParallelGroup()
                        .addComponent(pnlLeftUser, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlRightUser, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                );
            }
            jtpMain.addTab("Benutzer", pnlUserTab);


            //======== pnlGroupTab ========
            {

                //======== pnlLeftGrp ========
                {

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
                            public int getSize() { return values.length; }
                            @Override
                            public Object getElementAt(int i) { return values[i]; }
                        });
                        jScrollPane3.setViewportView(lstAllGroups);
                    }

                    //======== jPanel8 ========
                    {

                        //---- btnEditGroup ----
                        btnEditGroup.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit.png")));
                        btnEditGroup.setToolTipText("\u00c4nderungen vornehmen.");
                        btnEditGroup.setEnabled(false);
                        btnEditGroup.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnEditGroupActionPerformed(e);
                            }
                        });

                        //---- btnAddGroup ----
                        btnAddGroup.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_add.png")));
                        btnAddGroup.setToolTipText("Neuen Mitarbeiter eintragen.");
                        btnAddGroup.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnAddGroupActionPerformed(e);
                            }
                        });

                        //---- btnDeleteGroup ----
                        btnDeleteGroup.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_remove.png")));
                        btnDeleteGroup.setToolTipText("Gruppe l\u00f6schen");
                        btnDeleteGroup.setEnabled(false);
                        btnDeleteGroup.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnDeleteGroupActionPerformed(e);
                            }
                        });

                        GroupLayout jPanel8Layout = new GroupLayout(jPanel8);
                        jPanel8.setLayout(jPanel8Layout);
                        jPanel8Layout.setHorizontalGroup(
                            jPanel8Layout.createParallelGroup()
                                .addGroup(jPanel8Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(btnAddGroup)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnDeleteGroup)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnEditGroup)
                                    .addContainerGap())
                        );
                        jPanel8Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnAddGroup, btnDeleteGroup, btnEditGroup});
                        jPanel8Layout.setVerticalGroup(
                            jPanel8Layout.createParallelGroup()
                                .addGroup(jPanel8Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel8Layout.createParallelGroup()
                                        .addComponent(btnAddGroup, GroupLayout.Alignment.TRAILING)
                                        .addComponent(btnDeleteGroup, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                                        .addComponent(btnEditGroup, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
                                    .addContainerGap())
                        );
                        jPanel8Layout.linkSize(SwingConstants.VERTICAL, new Component[] {btnAddGroup, btnDeleteGroup, btnEditGroup});
                    }

                    GroupLayout pnlLeftGrpLayout = new GroupLayout(pnlLeftGrp);
                    pnlLeftGrp.setLayout(pnlLeftGrpLayout);
                    pnlLeftGrpLayout.setHorizontalGroup(
                        pnlLeftGrpLayout.createParallelGroup()
                            .addGroup(pnlLeftGrpLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlLeftGrpLayout.createParallelGroup()
                                    .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                                    .addComponent(jPanel8, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
                    );
                    pnlLeftGrpLayout.setVerticalGroup(
                        pnlLeftGrpLayout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, pnlLeftGrpLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel8, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    );
                }

                //======== pnlRightGrp ========
                {

                    //======== jPanel2 ========
                    {
                        jPanel2.setBorder(LineBorder.createBlackLineBorder());

                        //---- jLabel9 ----
                        jLabel9.setBackground(Color.red);
                        jLabel9.setFont(new Font("Lucida Grande", Font.BOLD, 13));
                        jLabel9.setForeground(new Color(255, 255, 51));
                        jLabel9.setHorizontalAlignment(SwingConstants.CENTER);
                        jLabel9.setText("MitarbeiterInnen");
                        jLabel9.setOpaque(true);

                        //---- jLabel7 ----
                        jLabel7.setBackground(new Color(255, 255, 51));
                        jLabel7.setFont(new Font("Lucida Grande", Font.BOLD, 13));
                        jLabel7.setHorizontalAlignment(SwingConstants.CENTER);
                        jLabel7.setText("Mitglieder");
                        jLabel7.setOpaque(true);

                        //======== jScrollPane6 ========
                        {

                            //---- lstUsers ----
                            lstUsers.setModel(new AbstractListModel() {
                                String[] values = {
                                    "Item 1",
                                    "Item 2",
                                    "Item 3",
                                    "Item 4",
                                    "Item 5"
                                };
                                @Override
                                public int getSize() { return values.length; }
                                @Override
                                public Object getElementAt(int i) { return values[i]; }
                            });
                            lstUsers.setDragEnabled(true);
                            lstUsers.setEnabled(false);
                            jScrollPane6.setViewportView(lstUsers);
                        }

                        //======== jScrollPane4 ========
                        {

                            //---- lstMembers ----
                            lstMembers.setModel(new AbstractListModel() {
                                String[] values = {
                                    "Item 1",
                                    "Item 2",
                                    "Item 3",
                                    "Item 4",
                                    "Item 5"
                                };
                                @Override
                                public int getSize() { return values.length; }
                                @Override
                                public Object getElementAt(int i) { return values[i]; }
                            });
                            lstMembers.setDragEnabled(true);
                            lstMembers.setEnabled(false);
                            jScrollPane4.setViewportView(lstMembers);
                        }

                        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
                        jPanel2.setLayout(jPanel2Layout);
                        jPanel2Layout.setHorizontalGroup(
                            jPanel2Layout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(jScrollPane4, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                                        .addComponent(jScrollPane6, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                                        .addComponent(jLabel7, GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                                        .addComponent(jLabel9, GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
                                    .addContainerGap())
                        );
                        jPanel2Layout.setVerticalGroup(
                            jPanel2Layout.createParallelGroup()
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jLabel7)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jScrollPane4, GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel9)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jScrollPane6, GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                                    .addContainerGap())
                        );
                    }

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
                                        .addComponent(jScrollPane5, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                                        .addComponent(jLabel10, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE))
                                    .addContainerGap())
                        );
                        jPanel6Layout.setVerticalGroup(
                            jPanel6Layout.createParallelGroup()
                                .addGroup(jPanel6Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jLabel10)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jScrollPane5, GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                                    .addContainerGap())
                        );
                    }

                    //======== jPanel7 ========
                    {

                        //---- btnCancelGroups ----
                        btnCancelGroups.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                        btnCancelGroups.setToolTipText("Abbrechen");
                        btnCancelGroups.setEnabled(false);
                        btnCancelGroups.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnCancelGroupsActionPerformed(e);
                            }
                        });

                        //---- btnSaveGroups ----
                        btnSaveGroups.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                        btnSaveGroups.setToolTipText("Sichern");
                        btnSaveGroups.setEnabled(false);
                        btnSaveGroups.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnSaveGroupsActionPerformed(e);
                            }
                        });

                        GroupLayout jPanel7Layout = new GroupLayout(jPanel7);
                        jPanel7.setLayout(jPanel7Layout);
                        jPanel7Layout.setHorizontalGroup(
                            jPanel7Layout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                                        .addComponent(txtGroupDescription, GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                                            .addComponent(txtGKennung, GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
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
                                        .addComponent(txtGKennung, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                                        .addComponent(jLabel11)
                                        .addComponent(lblGKennungFree, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                                        .addComponent(cbExamen, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
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
                                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
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
                                .addGroup(pnlRightGrpLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    );
                }

                GroupLayout pnlGroupTabLayout = new GroupLayout(pnlGroupTab);
                pnlGroupTab.setLayout(pnlGroupTabLayout);
                pnlGroupTabLayout.setHorizontalGroup(
                    pnlGroupTabLayout.createParallelGroup()
                        .addGroup(pnlGroupTabLayout.createSequentialGroup()
                            .addComponent(pnlLeftGrp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(pnlRightGrp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
                pnlGroupTabLayout.setVerticalGroup(
                    pnlGroupTabLayout.createParallelGroup()
                        .addComponent(pnlLeftGrp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlRightGrp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                );
            }
            jtpMain.addTab("Gruppen", pnlGroupTab);

        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jtpMain, GroupLayout.DEFAULT_SIZE, 805, Short.MAX_VALUE)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jtpMain, GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (selectedUser.getMd5pw() == null) {
            generatePassword();
        }
        saveUser();
        mode = BROWSE_USER;
        setLeftSideOnUserTab();
        setRightSideOnUserTab();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnAddUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUserActionPerformed
        selectedUser = new Users();
        mode = NEW_USER;
        ukennung_status = CONFLICT;
        setRightSideOnUserTab();
        setLeftSideOnUserTab();
        txtVorname.requestFocus();
    }//GEN-LAST:event_btnAddUserActionPerformed

    private void txtVornameCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtVornameCaretUpdate
        if (mode == NEW_USER) {
            txtUKennung.setText(SYSTools.generateUKennung(txtName.getText(), txtVorname.getText()));
        }
        setRightButtonsOnUserTab();
    }//GEN-LAST:event_txtVornameCaretUpdate

    private void txtNameCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtNameCaretUpdate
        if (mode == NEW_USER) {
            txtUKennung.setText(SYSTools.generateUKennung(txtName.getText(), txtVorname.getText()));
        }
        setRightButtonsOnUserTab();
    }//GEN-LAST:event_txtNameCaretUpdate

    /**
     * siehe: @EMRGX
     *
     * @param evt
     */
    private void txtEMailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEMailFocusLost
        // Später: txtEMail.getText().matches("\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}\b");
        selectedUser.setEMail(txtEMail.getText());
    }//GEN-LAST:event_txtEMailFocusLost

    private void txtUKennungCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtUKennungCaretUpdate
        if (mode == NEW_USER) {
            EntityManager em = OPDE.createEM();
            Query query = em.createNamedQuery("Users.findByUKennung");
            query.setParameter("uKennung", txtUKennung.getText());
            if (query.getResultList().isEmpty()) {
                lblUKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballgreen.png")));
                lblUKennungFree.setToolTipText(null);
                ukennung_status = OK;
                selectedUser.setUKennung(txtUKennung.getText().trim());
            } else {
                lblUKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballred.png")));
                lblUKennungFree.setToolTipText("Benutzerkennung ist schon vergeben.");
                ukennung_status = CONFLICT;
            }
            setRightButtonsOnUserTab();
            em.close();
        }
    }//GEN-LAST:event_txtUKennungCaretUpdate

    private void btnPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasswordActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Möchten Sie das Password zurücksetzen ?", "Passwort erstellen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            generatePassword();
            mode = BROWSE_USER;
            setLeftSideOnUserTab();
            setRightSideOnUserTab();
        }
    }//GEN-LAST:event_btnPasswordActionPerformed

    private void listGroupsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listGroupsMouseClicked
        if (evt.getClickCount() == 2) {
//            selectedUser.getOcgroups().add((Groups) listGroups.getSelectedValue());
//            listMembership.setModel(SYSTools.newListModel((List) selectedUser.getOcgroups()));
//
            DefaultListModel dlmMember = (DefaultListModel) listMembership.getModel();
            DefaultListModel dlmGroup = (DefaultListModel) listGroups.getModel();

            dlmMember.addElement(listGroups.getSelectedValue());
            dlmGroup.removeElement(listGroups.getSelectedValue());

        }
    }//GEN-LAST:event_listGroupsMouseClicked

    private void listMembershipMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMembershipMouseClicked
        if (evt.getClickCount() == 2) {

            DefaultListModel dlmMember = (DefaultListModel) listMembership.getModel();
            DefaultListModel dlmGroup = (DefaultListModel) listGroups.getModel();

            dlmGroup.addElement(listMembership.getSelectedValue());
            dlmMember.removeElement(listMembership.getSelectedValue());

        }
    }//GEN-LAST:event_listMembershipMouseClicked

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if (mode == EDIT_USER) {
            EntityManager em = OPDE.createEM();
            em.refresh(selectedUser);
            em.close();
        } else {
            selectedUser = new Users();
        }
        mode = BROWSE_USER;
        setLeftSideOnUserTab();
        setRightSideOnUserTab();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtVornameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVornameFocusLost
        selectedUser.setVorname(txtVorname.getText());
    }//GEN-LAST:event_txtVornameFocusLost

    private void txtNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusLost
        selectedUser.setNachname(txtName.getText());
    }//GEN-LAST:event_txtNameFocusLost

    private void btnEnableUserItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnEnableUserItemStateChanged
        selectedUser.setStatus(btnEnableUser.isSelected() ? UsersTools.STATUS_ACTIVE : UsersTools.STATUS_INACTIVE);
        saveUser();
    }//GEN-LAST:event_btnEnableUserItemStateChanged

    private void btnEditUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditUserActionPerformed
        mode = EDIT_USER;
        setLeftSideOnUserTab();
        setRightSideOnUserTab();
    }//GEN-LAST:event_btnEditUserActionPerformed

    private void btnAddGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddGroupActionPerformed
        mode = NEW_GROUP;
        selectedGroup = new Groups();
        selectedGroup.setMembers(new ArrayList());
        setLeftSideOnGroupTab();
        setRightSideOnGroupTab();
    }//GEN-LAST:event_btnAddGroupActionPerformed

    private void btnEditGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditGroupActionPerformed
        mode = EDIT_GROUP;
        setLeftSideOnGroupTab();
        setRightSideOnGroupTab();
    }//GEN-LAST:event_btnEditGroupActionPerformed

    private void btnDeleteGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteGroupActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Möchten Sie die Gruppe '" + selectedGroup.getGkennung() + "' wirklich löschen  ?", "Gruppe löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            EntityManager em = OPDE.createEM();

            try {
                em.getTransaction().begin();
                em.remove(selectedGroup);
                em.getTransaction().commit();
            } catch (Exception e1) {
                em.getTransaction().rollback();
//                new DlgException(e1);
            } finally {
                em.close();
            }
            selectedGroup = null;
            mode = BROWSE_GROUP;
            setGroupTab();
        }
    }//GEN-LAST:event_btnDeleteGroupActionPerformed

    private void btnCancelGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelGroupsActionPerformed
        if (mode == EDIT_GROUP) {
            EntityManager em = OPDE.createEM();
            em.refresh(selectedGroup);
            em.close();
        } else {
            selectedGroup = null;
        }
        mode = BROWSE_GROUP;
        setLeftSideOnGroupTab();
        setRightSideOnGroupTab();
    }//GEN-LAST:event_btnCancelGroupsActionPerformed

    private void btnSaveGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveGroupsActionPerformed
        saveGroup();
    }//GEN-LAST:event_btnSaveGroupsActionPerformed

    private void jtpMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpMainStateChanged
        if (jtpMain.getSelectedIndex() == TAB_USER) {
            IntClassesTools.clearEntitiesFromAllInternalClasses();
            mode = BROWSE_USER;
            setUserTab();
        } else {
            mode = BROWSE_GROUP;
            setGroupTab();
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
            setRightButtonsOnGroupTab();
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

    private void generatePassword() {
        Random generator = new Random(System.currentTimeMillis());
        String pw = txtName.getText().substring(0, 1).toLowerCase() + txtVorname.getText().substring(0, 1).toLowerCase() + SYSTools.padL(Integer.toString(generator.nextInt(9999)), 4, "0");
        selectedUser.setMd5pw(SYSTools.hashword(pw));
        if (JOptionPane.showConfirmDialog(this, "Das Passwort wurde auf '" + pw + "' gesetzt.\nMöchten Sie einen Beleg ausdrucken ?", "Passwort", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            print(pw);
        }
    }

    private void print(String pw) {
        String html = "<html>"
                + "<body>"
                + "<h1>Zugang zu Offene-Pflege.de</h1>"
                + "<br/>"
                + "<br/>"
                + "<br/>"
                + "<h2>Computer-Zugang für <opde-ocuser-fullname/></h2>"
                + "<br/>"
                + "<br/>"
                + "<br/>"
                + "<p>Benutzer-Name: <b><opde-ocuser-ukennung/></b></p>"
                + "<p>Passwort: <b><opde-ocuser-pw/></b></p>"
                + "<br/>"
                + "<br/>"
                + "Bitte bewahren Sie diesen Zettel an einem sicheren Ort auf. Teilen Sie Ihr Passwort niemandem mit."
                + "</body>"
                + "</html>";
        html = SYSTools.replace(html, "<opde-ocuser-fullname/>", selectedUser.getNameUndVorname());
        html = SYSTools.replace(html, "<opde-ocuser-ukennung/>", selectedUser.getUKennung());
        html = SYSTools.replace(html, "<opde-ocuser-pw/>", pw);
        html = SYSTools.htmlUmlautConversion(html);

        SYSFilesTools.print(html, true);
    }

    /**
     * Alle aus der Liste der Mitgliedschaften hinzufügen. Und alle aus der Gruppenliste entfernen.
     */
    private void setMemberships() {
        for (int i = 0; i < listMembership.getModel().getSize(); i++) {
            Groups group = (Groups) listMembership.getModel().getElementAt(i);
            if (!selectedUser.getGroups().contains(group)) {
                selectedUser.getGroups().add(group);
            }
        }
        for (int i = 0; i < listGroups.getModel().getSize(); i++) {
            Groups group = (Groups) listGroups.getModel().getElementAt(i);
            if (selectedUser.getGroups().contains(group)) {
                selectedUser.getGroups().remove(group);
            }
        }
    }

    private void saveGroup() {
        EntityManager em = OPDE.createEM();

        try {
            em.getTransaction().begin();
            // TODO: Das muss anderes geregelt werden.
            if (em.contains(selectedGroup)) { // Gruppe existiert schon in der Datenbank
                em.merge(selectedGroup);
            } else {
                em.persist(selectedGroup);
            }
            if (!selectedGroup.getGkennung().equalsIgnoreCase("admin")) { // Bei Admin kann man den Beaum nicht ändern.
                IntClassesTools.saveTree((DefaultMutableTreeNode) treeRights.getModel().getRoot(), sm);
            }
            em.getTransaction().commit();

        } catch (Exception e1) {
            em.getTransaction().rollback();
            OPDE.fatal(e1);
        } finally {
            em.close();
        }
        selectedGroup = null;
        mode = BROWSE_GROUP;
        setGroupTab();
    }

    private void saveUser() {
        setMemberships();
        EntityManager em = OPDE.createEM();

        try {
            em.getTransaction().begin();
            if (selectedUser.getUKennung() == null) {
                generatePassword();
                em.persist(selectedUser);
            } else {
                em.merge(selectedUser);
            }
            em.getTransaction().commit();
        } catch (Exception e1) {
            em.getTransaction().rollback();
            OPDE.fatal(e1);
        } finally {
            em.close();
        }

        if (mode == NEW_USER) {
            //loadUserTable();

            Query query = em.createNamedQuery("Users.findAllSorted");
            tblUsers.setModel(new TMUser(query.getResultList()));


            //((TMUser) tblUsers.getModel()).updateTable();
        } else {
            ((TMUser) tblUsers.getModel()).updateRow(tblUsers.getSelectedRow());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTabbedPane jtpMain;
    private JPanel pnlUserTab;
    private JPanel pnlLeftUser;
    private JScrollPane jScrollPane2;
    private JTable tblUsers;
    private JLabel jLabel8;
    private JPanel jPanel5;
    private JButton btnAddUser;
    private JButton btnPassword;
    private JButton btnEditUser;
    private JToggleButton btnEnableUser;
    private JPanel pnlRightUser;
    private JScrollPane jsp1;
    private JList listMembership;
    private JScrollPane jScrollPane1;
    private JList listGroups;
    private JPanel jPanel3;
    private JButton btnCancel;
    private JButton btnSave;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JPanel jPanel4;
    private JTextField txtUKennung;
    private JTextField txtName;
    private JTextField txtEMail;
    private JLabel jLabel5;
    private JTextField txtVorname;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel6;
    private JLabel lblUKennungFree;
    private JPanel pnlGroupTab;
    private JPanel pnlLeftGrp;
    private JScrollPane jScrollPane3;
    private JList lstAllGroups;
    private JPanel jPanel8;
    private JButton btnEditGroup;
    private JButton btnAddGroup;
    private JButton btnDeleteGroup;
    private JPanel pnlRightGrp;
    private JPanel jPanel2;
    private JLabel jLabel9;
    private JLabel jLabel7;
    private JScrollPane jScrollPane6;
    private JList lstUsers;
    private JScrollPane jScrollPane4;
    private JList lstMembers;
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
