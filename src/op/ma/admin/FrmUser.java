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

import entity.Groups;
import entity.system.IntClassesTools;
import entity.Users;
import op.OPDE;
import op.tools.*;
import tablemodels.TMUser;
import tablerenderer.RNDOCUsers;

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
                        OPDE.getEM().refresh(selectedGroup);
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
        Query query = OPDE.getEM().createNamedQuery("Users.findAllSorted");
        tblUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        try {
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
            new DlgException(e);
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

            if (OPDE.getEM().contains(selectedGroup)) { // EntityBean ist schon gespeichert.
                if (selectedGroup.getGkennung().equalsIgnoreCase("everyone")) {
                    // everyone hat immer alle Benutzer als Mitglied.
                    lstMembers.setModel(SYSTools.newListModel("Users.findAllSorted"));
                    lstUsers.setModel(new DefaultListModel());
                } else {
                    lstMembers.setModel(SYSTools.newListModel("Users.findAllMembers", new Object[]{"group", selectedGroup}));
                    lstUsers.setModel(SYSTools.newListModel("Users.findAllNonMembers", new Object[]{"group", selectedGroup}));
                }
            } else {
                lstMembers.setModel(new DefaultListModel());
                lstUsers.setModel(SYSTools.newListModel("Users.findAllSorted"));
            }

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

        jtpMain = new javax.swing.JTabbedPane();
        pnlUserTab = new javax.swing.JPanel();
        pnlLeftUser = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblUsers = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btnAddUser = new javax.swing.JButton();
        btnPassword = new javax.swing.JButton();
        btnEditUser = new javax.swing.JButton();
        btnEnableUser = new javax.swing.JToggleButton();
        pnlRightUser = new javax.swing.JPanel();
        jsp1 = new javax.swing.JScrollPane();
        listMembership = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        listGroups = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txtUKennung = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        txtEMail = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtVorname = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblUKennungFree = new javax.swing.JLabel();
        pnlGroupTab = new javax.swing.JPanel();
        pnlLeftGrp = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstAllGroups = new javax.swing.JList();
        jPanel8 = new javax.swing.JPanel();
        btnEditGroup = new javax.swing.JButton();
        btnAddGroup = new javax.swing.JButton();
        btnDeleteGroup = new javax.swing.JButton();
        pnlRightGrp = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        lstUsers = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        lstMembers = new javax.swing.JList();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        treeRights = new javax.swing.JTree();
        jLabel10 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        btnCancelGroups = new javax.swing.JButton();
        btnSaveGroups = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtGKennung = new javax.swing.JTextField();
        txtGroupDescription = new javax.swing.JTextField();
        lblGKennungFree = new javax.swing.JLabel();
        cbExamen = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jtpMain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jtpMainStateChanged(evt);
            }
        });

        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null}
                },
                new String[]{
                        "Title 1", "Title 2", "Title 3", "Title 4"
                }
        ));
        jScrollPane2.setViewportView(tblUsers);

        jLabel8.setBackground(new java.awt.Color(0, 0, 255));
        jLabel8.setForeground(new java.awt.Color(255, 255, 0));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Alle MitarbeiterInnen");
        jLabel8.setOpaque(true);

        btnAddUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/edit_add.png"))); // NOI18N
        btnAddUser.setToolTipText("Neuen Mitarbeiter eintragen.");
        btnAddUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddUserActionPerformed(evt);
            }
        });

        btnPassword.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/password.png"))); // NOI18N
        btnPassword.setToolTipText("Passwort erzeugen");
        btnPassword.setEnabled(false);
        btnPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasswordActionPerformed(evt);
            }
        });

        btnEditUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/edit.png"))); // NOI18N
        btnEditUser.setToolTipText("Änderungen vornehmen.");
        btnEditUser.setEnabled(false);
        btnEditUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditUserActionPerformed(evt);
            }
        });

        btnEnableUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/user_active.png"))); // NOI18N
        btnEnableUser.setToolTipText("Aktiv / Inaktiv schalten");
        btnEnableUser.setEnabled(false);
        btnEnableUser.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btnEnableUserItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(btnAddUser)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(btnEditUser)
                                .add(2, 2, 2)
                                .add(btnEnableUser)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btnPassword)
                                .addContainerGap())
        );

        jPanel5Layout.linkSize(new java.awt.Component[]{btnAddUser, btnEditUser, btnEnableUser, btnPassword}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                        .add(btnPassword, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(btnEnableUser, 0, 0, Short.MAX_VALUE)
                                        .add(btnEditUser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(btnAddUser))
                                .addContainerGap())
        );

        jPanel5Layout.linkSize(new java.awt.Component[]{btnAddUser, btnEditUser, btnEnableUser, btnPassword}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout pnlLeftUserLayout = new org.jdesktop.layout.GroupLayout(pnlLeftUser);
        pnlLeftUser.setLayout(pnlLeftUserLayout);
        pnlLeftUserLayout.setHorizontalGroup(
                pnlLeftUserLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlLeftUserLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(pnlLeftUserLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(pnlLeftUserLayout.createSequentialGroup()
                                                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .add(pnlLeftUserLayout.createSequentialGroup()
                                                .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                                                .add(24, 24, 24))
                                        .add(pnlLeftUserLayout.createSequentialGroup()
                                                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(93, Short.MAX_VALUE))))
        );
        pnlLeftUserLayout.setVerticalGroup(
                pnlLeftUserLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlLeftUserLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(jLabel8)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 724, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        listMembership.setDragEnabled(true);
        listMembership.setDropMode(javax.swing.DropMode.INSERT);
        listMembership.setEnabled(false);
        listMembership.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listMembershipMouseClicked(evt);
            }
        });
        jsp1.setViewportView(listMembership);

        listGroups.setDragEnabled(true);
        listGroups.setDropMode(javax.swing.DropMode.INSERT);
        listGroups.setEnabled(false);
        listGroups.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listGroupsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(listGroups);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/cancel.png"))); // NOI18N
        btnCancel.setToolTipText("Abbrechen");
        btnCancel.setEnabled(false);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/apply.png"))); // NOI18N
        btnSave.setToolTipText("Sichern");
        btnSave.setEnabled(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addContainerGap(63, Short.MAX_VALUE)
                                .add(btnSave)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(btnCancel)
                                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel3Layout.createSequentialGroup()
                                .add(7, 7, 7)
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, btnSave, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, btnCancel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        jLabel3.setBackground(new java.awt.Color(255, 255, 51));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Mitglied in");
        jLabel3.setOpaque(true);

        jLabel4.setBackground(new java.awt.Color(51, 255, 51));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Verfügbare Gruppen");
        jLabel4.setOpaque(true);

        txtUKennung.setColumns(10);
        txtUKennung.setDragEnabled(false);
        txtUKennung.setDropTarget(null);
        txtUKennung.setEnabled(false);
        txtUKennung.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtUKennungCaretUpdate(evt);
            }
        });

        txtName.setDragEnabled(false);
        txtName.setDropTarget(null);
        txtName.setEnabled(false);
        txtName.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtNameCaretUpdate(evt);
            }
        });
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNameFocusLost(evt);
            }
        });

        txtEMail.setDragEnabled(false);
        txtEMail.setDropTarget(null);
        txtEMail.setEnabled(false);
        txtEMail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtEMailFocusLost(evt);
            }
        });

        jLabel5.setText("E-Mail");

        txtVorname.setDragEnabled(false);
        txtVorname.setDropTarget(null);
        txtVorname.setEnabled(false);
        txtVorname.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtVornameCaretUpdate(evt);
            }
        });
        txtVorname.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtVornameFocusLost(evt);
            }
        });

        jLabel1.setText("Vorname");

        jLabel2.setText("Nachname");

        jLabel6.setText("UKennung");

        lblUKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballred.png"))); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jPanel4Layout.createSequentialGroup()
                                                .add(jLabel6)
                                                .add(35, 35, 35)
                                                .add(txtUKennung, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(lblUKennungFree))
                                        .add(jPanel4Layout.createSequentialGroup()
                                                .add(jLabel5)
                                                .add(58, 58, 58)
                                                .add(txtEMail, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE))
                                        .add(jPanel4Layout.createSequentialGroup()
                                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jLabel1)
                                                        .add(jLabel2))
                                                .add(33, 33, 33)
                                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, txtVorname, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, txtName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE))))
                                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                .add(txtUKennung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, Short.MAX_VALUE)
                                                .add(lblUKennungFree))
                                        .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                        .add(txtVorname, 0, 0, Short.MAX_VALUE)
                                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jLabel2)
                                        .add(txtName))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(txtEMail, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jLabel5))
                                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout pnlRightUserLayout = new org.jdesktop.layout.GroupLayout(pnlRightUser);
        pnlRightUser.setLayout(pnlRightUserLayout);
        pnlRightUserLayout.setHorizontalGroup(
                pnlRightUserLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlRightUserLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(pnlRightUserLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(pnlRightUserLayout.createSequentialGroup()
                                                .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .add(pnlRightUserLayout.createSequentialGroup()
                                                .add(pnlRightUserLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jsp1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE))
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(pnlRightUserLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                                                        .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE))
                                                .addContainerGap())
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
        pnlRightUserLayout.setVerticalGroup(
                pnlRightUserLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlRightUserLayout.createSequentialGroup()
                                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlRightUserLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jLabel4)
                                        .add(jLabel3))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlRightUserLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                                        .add(jsp1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout pnlUserTabLayout = new org.jdesktop.layout.GroupLayout(pnlUserTab);
        pnlUserTab.setLayout(pnlUserTabLayout);
        pnlUserTabLayout.setHorizontalGroup(
                pnlUserTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlUserTabLayout.createSequentialGroup()
                                .add(pnlLeftUser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlRightUser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlUserTabLayout.setVerticalGroup(
                pnlUserTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlLeftUser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(pnlRightUser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jtpMain.addTab("Benutzer", pnlUserTab);

        lstAllGroups.setModel(new javax.swing.AbstractListModel() {
            String[] strings = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        jScrollPane3.setViewportView(lstAllGroups);

        btnEditGroup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/edit.png"))); // NOI18N
        btnEditGroup.setToolTipText("Änderungen vornehmen.");
        btnEditGroup.setEnabled(false);
        btnEditGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditGroupActionPerformed(evt);
            }
        });

        btnAddGroup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/edit_add.png"))); // NOI18N
        btnAddGroup.setToolTipText("Neuen Mitarbeiter eintragen.");
        btnAddGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddGroupActionPerformed(evt);
            }
        });

        btnDeleteGroup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/edit_remove.png"))); // NOI18N
        btnDeleteGroup.setToolTipText("Gruppe löschen");
        btnDeleteGroup.setEnabled(false);
        btnDeleteGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteGroupActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
                jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel8Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(btnAddGroup)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btnDeleteGroup)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btnEditGroup)
                                .addContainerGap())
        );

        jPanel8Layout.linkSize(new java.awt.Component[]{btnAddGroup, btnDeleteGroup, btnEditGroup}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel8Layout.setVerticalGroup(
                jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel8Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, btnAddGroup)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, btnDeleteGroup, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                                        .add(btnEditGroup, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
                                .addContainerGap())
        );

        jPanel8Layout.linkSize(new java.awt.Component[]{btnAddGroup, btnDeleteGroup, btnEditGroup}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout pnlLeftGrpLayout = new org.jdesktop.layout.GroupLayout(pnlLeftGrp);
        pnlLeftGrp.setLayout(pnlLeftGrpLayout);
        pnlLeftGrpLayout.setHorizontalGroup(
                pnlLeftGrpLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlLeftGrpLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(pnlLeftGrpLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                                        .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );
        pnlLeftGrpLayout.setVerticalGroup(
                pnlLeftGrpLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlLeftGrpLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 747, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel9.setBackground(new java.awt.Color(255, 0, 0));
        jLabel9.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jLabel9.setForeground(new java.awt.Color(255, 255, 51));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("MitarbeiterInnen");
        jLabel9.setOpaque(true);

        jLabel7.setBackground(new java.awt.Color(255, 255, 51));
        jLabel7.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Mitglieder");
        jLabel7.setOpaque(true);

        lstUsers.setModel(new javax.swing.AbstractListModel() {
            String[] strings = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        lstUsers.setDragEnabled(true);
        lstUsers.setDropMode(javax.swing.DropMode.INSERT);
        lstUsers.setEnabled(false);
        jScrollPane6.setViewportView(lstUsers);

        lstMembers.setModel(new javax.swing.AbstractListModel() {
            String[] strings = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        lstMembers.setDragEnabled(true);
        lstMembers.setDropMode(javax.swing.DropMode.INSERT);
        lstMembers.setEnabled(false);
        jScrollPane4.setViewportView(lstMembers);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                                        .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                                        .add(jLabel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jLabel7)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel9)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        treeRights.setEnabled(false);
        jScrollPane5.setViewportView(treeRights);

        jLabel10.setBackground(new java.awt.Color(0, 0, 255));
        jLabel10.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jLabel10.setForeground(new java.awt.Color(255, 255, 0));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Rechte");
        jLabel10.setOpaque(true);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel6Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jLabel10)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
                                .addContainerGap())
        );

        btnCancelGroups.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/cancel.png"))); // NOI18N
        btnCancelGroups.setToolTipText("Abbrechen");
        btnCancelGroups.setEnabled(false);
        btnCancelGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelGroupsActionPerformed(evt);
            }
        });

        btnSaveGroups.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/apply.png"))); // NOI18N
        btnSaveGroups.setToolTipText("Sichern");
        btnSaveGroups.setEnabled(false);
        btnSaveGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveGroupsActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
                jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(btnSaveGroups)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(btnCancelGroups)
                                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
                jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel7Layout.createSequentialGroup()
                                .add(7, 7, 7)
                                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, btnSaveGroups, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, btnCancelGroups, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel11.setText("Gruppenbezeichnung");

        jLabel12.setText("Erläuterung");

        txtGKennung.setDragEnabled(false);
        txtGKennung.setDropTarget(null);
        txtGKennung.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtGKennungCaretUpdate(evt);
            }
        });
        txtGKennung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGKennungActionPerformed(evt);
            }
        });

        txtGroupDescription.setDragEnabled(false);
        txtGroupDescription.setDropTarget(null);
        txtGroupDescription.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtGroupDescriptionCaretUpdate(evt);
            }
        });

        lblGKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballred.png"))); // NOI18N

        cbExamen.setText("Examen");
        cbExamen.setToolTipText("Die Mitgliedschaft in dieser Gruppe gewährt Examensrechte.");
        cbExamen.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbExamenItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
                jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel9Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jLabel11)
                                        .add(jLabel12))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(txtGroupDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel9Layout.createSequentialGroup()
                                                .add(txtGKennung, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(lblGKennungFree)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(cbExamen)))
                                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
                jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel9Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(txtGKennung)
                                        .add(jLabel11)
                                        .add(lblGKennungFree, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                                        .add(cbExamen, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                                .add(18, 18, 18)
                                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jLabel12)
                                        .add(txtGroupDescription, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(30, 30, 30))
        );

        org.jdesktop.layout.GroupLayout pnlRightGrpLayout = new org.jdesktop.layout.GroupLayout(pnlRightGrp);
        pnlRightGrp.setLayout(pnlRightGrpLayout);
        pnlRightGrpLayout.setHorizontalGroup(
                pnlRightGrpLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlRightGrpLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(pnlRightGrpLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(pnlRightGrpLayout.createSequentialGroup()
                                                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );
        pnlRightGrpLayout.setVerticalGroup(
                pnlRightGrpLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlRightGrpLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlRightGrpLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout pnlGroupTabLayout = new org.jdesktop.layout.GroupLayout(pnlGroupTab);
        pnlGroupTab.setLayout(pnlGroupTabLayout);
        pnlGroupTabLayout.setHorizontalGroup(
                pnlGroupTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlGroupTabLayout.createSequentialGroup()
                                .add(pnlLeftGrp, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlRightGrp, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlGroupTabLayout.setVerticalGroup(
                pnlGroupTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlLeftGrp, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(pnlRightGrp, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jtpMain.addTab("Gruppen", pnlGroupTab);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jtpMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1075, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jtpMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
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
            Query query = OPDE.getEM().createNamedQuery("Users.findByUKennung");
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
            OPDE.getEM().refresh(selectedUser);
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
        selectedUser.setStatus(btnEnableUser.isSelected() ? Users.STATUS_ACTIVE : Users.STATUS_INACTIVE);
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
            OPDE.getEM().getTransaction().begin();
            try {
                OPDE.getEM().remove(selectedGroup);
                OPDE.getEM().getTransaction().commit();
            } catch (Exception e1) {
                OPDE.getEM().getTransaction().rollback();
                new DlgException(e1);
            }
            selectedGroup = null;
            mode = BROWSE_GROUP;
            setGroupTab();
        }
    }//GEN-LAST:event_btnDeleteGroupActionPerformed

    private void btnCancelGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelGroupsActionPerformed
        if (mode == EDIT_GROUP) {
            OPDE.getEM().refresh(selectedGroup);
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
                Query query = OPDE.getEM().createNamedQuery("Groups.findByGkennung");
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

        SYSPrint.print(this, html, true);
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
        OPDE.getEM().getTransaction().begin();
        try {
            if (OPDE.getEM().contains(selectedGroup)) { // Gruppe existiert schon in der Datenbank
                OPDE.getEM().merge(selectedGroup);
            } else {
                OPDE.getEM().persist(selectedGroup);
            }
            if (!selectedGroup.getGkennung().equalsIgnoreCase("admin")) { // Bei Admin kann man den Beaum nicht ändern.
                IntClassesTools.saveTree((DefaultMutableTreeNode) treeRights.getModel().getRoot(), sm);
            }
            OPDE.getEM().getTransaction().commit();

        } catch (Exception e1) {
            OPDE.getEM().getTransaction().rollback();
            new DlgException(e1);
        }
        selectedGroup = null;
        mode = BROWSE_GROUP;
        setGroupTab();
    }

    private void saveUser() {
        setMemberships();
        OPDE.getEM().getTransaction().begin();
        try {
            if (selectedUser.getUKennung() == null) {
                generatePassword();
                OPDE.getEM().persist(selectedUser);
            } else {
                OPDE.getEM().merge(selectedUser);
            }
            OPDE.getEM().getTransaction().commit();
        } catch (Exception e1) {
            OPDE.getEM().getTransaction().rollback();
            new DlgException(e1);
        }

        if (mode == NEW_USER) {
            //loadUserTable();

            Query query = OPDE.getEM().createNamedQuery("Users.findAllSorted");
            tblUsers.setModel(new TMUser(query.getResultList()));


            //((TMUser) tblUsers.getModel()).updateTable();
        } else {
            ((TMUser) tblUsers.getModel()).updateRow(tblUsers.getSelectedRow());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddGroup;
    private javax.swing.JButton btnAddUser;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCancelGroups;
    private javax.swing.JButton btnDeleteGroup;
    private javax.swing.JButton btnEditGroup;
    private javax.swing.JButton btnEditUser;
    private javax.swing.JToggleButton btnEnableUser;
    private javax.swing.JButton btnPassword;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveGroups;
    private javax.swing.JCheckBox cbExamen;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jsp1;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JLabel lblGKennungFree;
    private javax.swing.JLabel lblUKennungFree;
    private javax.swing.JList listGroups;
    private javax.swing.JList listMembership;
    private javax.swing.JList lstAllGroups;
    private javax.swing.JList lstMembers;
    private javax.swing.JList lstUsers;
    private javax.swing.JPanel pnlGroupTab;
    private javax.swing.JPanel pnlLeftGrp;
    private javax.swing.JPanel pnlLeftUser;
    private javax.swing.JPanel pnlRightGrp;
    private javax.swing.JPanel pnlRightUser;
    private javax.swing.JPanel pnlUserTab;
    private javax.swing.JTable tblUsers;
    private javax.swing.JTree treeRights;
    private javax.swing.JTextField txtEMail;
    private javax.swing.JTextField txtGKennung;
    private javax.swing.JTextField txtGroupDescription;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtUKennung;
    private javax.swing.JTextField txtVorname;
    // End of variables declaration//GEN-END:variables
}
