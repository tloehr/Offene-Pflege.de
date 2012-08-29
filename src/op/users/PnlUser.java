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

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.files.SYSFilesTools;
import entity.system.Groups;
import entity.system.GroupsTools;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.system.InternalClass;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
    private JToggleButton tbOldUsers, tbShowUsers, tbShowGroups;
    private ButtonGroup bg1;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private HashMap<Users, CollapsiblePane> userMap;
    private HashMap<Groups, CollapsiblePane> groupMap;
    private ArrayList<Users> lstUsers;
    private ArrayList<Groups> lstGroups;

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

        initPhase = true;
        initPanel();
        reloadDisplay();
    }

    private void initPanel() {

        userMap = new HashMap<Users, CollapsiblePane>();
        groupMap = new HashMap<Groups, CollapsiblePane>();
        prepareSearchArea();
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


        final boolean withworker = false;
        if (withworker) {

//            OPDE.getMainframe().setBlocked(true);
//            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));
//
//            cpDFN.removeAll();
//
//            SwingWorker worker = new SwingWorker() {
//
//                @Override
//                protected Object doInBackground() throws Exception {
//
//                    int progress = 0;
//                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, 100));
//
//                    for (DFN dfn : DFNTools.getDFNs(resident, jdcDatum.getDate())) {
//                        shiftMAPDFN.get(dfn.getShift()).add(dfn);
//                    }
//
//                    for (Byte shift : new Byte[]{DFNTools.SHIFT_ON_DEMAND, DFNTools.SHIFT_VERY_EARLY, DFNTools.SHIFT_EARLY, DFNTools.SHIFT_LATE, DFNTools.SHIFT_VERY_LATE}) {
//                        shiftMAPpane.put(shift, createCP4(shift));
//                        try {
//                            shiftMAPpane.get(shift).setCollapsed(shift == DFNTools.SHIFT_ON_DEMAND || shift != SYSCalendar.whatShiftIs(new Date()));
//                        } catch (PropertyVetoException e) {
//                            OPDE.debug(e);
//                        }
//                        progress += 20;
//                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, 100));
//                    }
//
//                    return null;
//                }
//
//                @Override
//                protected void done() {
//                    buildPanel(true);
//                    initPhase = false;
//                    OPDE.getDisplayManager().setProgressBarMessage(null);
//                    OPDE.getMainframe().setBlocked(false);
//                }
//            };
//            worker.execute();

        } else {
            lstUsers = UsersTools.getUsers(true);
            lstGroups = GroupsTools.getGroups();
            for (Users user : lstUsers) {
                userMap.put(user, createCP4(user));
            }
            for (Groups group : lstGroups) {
                groupMap.put(group, createCP4(group));
            }
            buildPanel();
        }
        initPhase = false;
    }


    @Override
    public void cleanup() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void reload() {
        reloadDisplay();
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
//            txtGKennung.setText(SYSTools.catchNull(selectedGroup.getID()));
//            txtGroupDescription.setText(SYSTools.catchNull(selectedGroup.getDescription()));
//            cbExamen.setSelected(selectedGroup.isQualified());
//
//            lstMembers.getModel().removeListDataListener(ldlMember);
//            lstUsers.getModel().removeListDataListener(ldlUser);
//
//            // TODO: hier liegt noch was im Argen
//            // Hab den Teil auskommentiert
////            if (em.contains(selectedGroup)) { // EntityBean ist schon gespeichert.
////                if (selectedGroup.getID().equalsIgnoreCase("everyone")) {
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
//        txtUKennung.setText(selectedUser.getUID());
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
        scrollPane1 = new JScrollPane();
        cpMain = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== scrollPane1 ========
        {

            //======== cpMain ========
            {
                cpMain.setLayout(new BoxLayout(cpMain, BoxLayout.X_AXIS));
            }
            scrollPane1.setViewportView(cpMain);
        }
        add(scrollPane1);
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
//                selectedUser.setUID(txtUKennung.getText().trim());
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
//        if (JOptionPane.showConfirmDialog(this, "Möchten Sie die Gruppe '" + selectedGroup.getID() + "' wirklich löschen  ?", "Gruppe löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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
//    private void jtpMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpMainStateChanged
//        if (jtpMain.getSelectedIndex() == TAB_USER) {
//            IntClassesTools.clearEntitiesFromAllInternalClasses();
//            mode = BROWSE_USER;
////            setUserTab();
//        } else {
//            mode = BROWSE_GROUP;
////            setGroupTab();
//        }
//    }//GEN-LAST:event_jtpMainStateChanged
//
//    private void txtGKennungCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtGKennungCaretUpdate
//        if (mode == NEW_GROUP) {
//            if (txtGKennung.getText().matches("[a-zA-Z0-9]+")) {
//                EntityManager em = OPDE.createEM();
//                Query query = em.createNamedQuery("Groups.findByGkennung");
//                query.setParameter("gkennung", txtGKennung.getText());
//                if (query.getResultList().isEmpty()) {
//                    lblGKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballgreen.png")));
//                    lblGKennungFree.setToolTipText(null);
//                    gkennung_status = OK;
//                    selectedGroup.setGkennung(txtGKennung.getText().trim());
//                } else {
//                    lblGKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballred.png")));
//                    lblGKennungFree.setToolTipText("Gruppenkennung ist schon vergeben.");
//                    selectedGroup.setGkennung(null);
//                    gkennung_status = CONFLICT;
//                }
//                em.close();
//            } else {
//                lblGKennungFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/ballred.png")));
//                lblGKennungFree.setToolTipText("Keine Leer- oder Sonderzeichen als Gruppenkennung verwenden.");
//                selectedGroup.setGkennung(null);
//                gkennung_status = CONFLICT;
//            }
////            setRightButtonsOnGroupTab();
//        }
//    }//GEN-LAST:event_txtGKennungCaretUpdate
//
//    private void txtGKennungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGKennungActionPerformed
//        txtGroupDescription.requestFocus();
//    }//GEN-LAST:event_txtGKennungActionPerformed
//
//    private void txtGroupDescriptionCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtGroupDescriptionCaretUpdate
//        if (selectedGroup != null) {
//            selectedGroup.setBeschreibung(txtGroupDescription.getText().trim());
//        }
//    }//GEN-LAST:event_txtGroupDescriptionCaretUpdate
//
//    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
//        IntClassesTools.clearEntitiesFromAllInternalClasses();
//    }//GEN-LAST:event_formWindowClosing
//
//    private void cbExamenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbExamenItemStateChanged
//        if (mode != BROWSE_GROUP) {
//            selectedGroup.setQualified(cbExamen.isSelected());
//        }
//    }//GEN-LAST:event_cbExamenItemStateChanged

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

        html = SYSTools.replace(html, "<opde-user-fullname/>", selectedUser.getFullname());
        html = SYSTools.replace(html, "<opde-user-userid/>", selectedUser.getUID());
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
//            if (!selectedGroup.getID().equalsIgnoreCase("admin")) { // Bei Admin kann man den Beaum nicht ändern.
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
//            if (selectedUser.getUID() == null) {
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
         *      _   _      ___  _     _ _   _
         *     | |_| |__  / _ \| | __| | | | |___  ___ _ __ ___
         *     | __| '_ \| | | | |/ _` | | | / __|/ _ \ '__/ __|
         *     | |_| |_) | |_| | | (_| | |_| \__ \  __/ |  \__ \
         *      \__|_.__/ \___/|_|\__,_|\___/|___/\___|_|  |___/
         *
         */
        tbOldUsers = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.showclosed"));
        tbOldUsers.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase) return;
                buildPanel();
            }
        });
        tbOldUsers.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbOldUsers);


        /***
         *      _   _    ____  _                   _   _                      ______
         *     | |_| |__/ ___|| |__   _____      _| | | |___  ___ _ __ ___   / / ___|_ __ ___  _   _ _ __  ___
         *     | __| '_ \___ \| '_ \ / _ \ \ /\ / / | | / __|/ _ \ '__/ __| / / |  _| '__/ _ \| | | | '_ \/ __|
         *     | |_| |_) |__) | | | | (_) \ V  V /| |_| \__ \  __/ |  \__ \/ /| |_| | | | (_) | |_| | |_) \__ \
         *      \__|_.__/____/|_| |_|\___/ \_/\_/  \___/|___/\___|_|  |___/_/  \____|_|  \___/ \__,_| .__/|___/
         *                                                                                          |_|
         */
        tbShowUsers = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".filter.showusers"));
        tbShowUsers.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase || itemEvent.getStateChange() == ItemEvent.DESELECTED) return;
                buildPanel();
            }
        });
        tbShowUsers.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbShowUsers);

        tbShowGroups = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".filter.showgroups"));
        tbShowGroups.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase || itemEvent.getStateChange() == ItemEvent.DESELECTED) return;
                buildPanel();
            }
        });
        tbShowGroups.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbShowGroups);

        bg1 = new ButtonGroup();
        bg1.add(tbShowGroups);
        bg1.add(tbShowUsers);

        tbShowUsers.setSelected(true);


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

        /***
         *         _       _     _  ____
         *        / \   __| | __| |/ ___|_ __ ___  _   _ _ __
         *       / _ \ / _` |/ _` | |  _| '__/ _ \| | | | '_ \
         *      / ___ \ (_| | (_| | |_| | | | (_) | |_| | |_) |
         *     /_/   \_\__,_|\__,_|\____|_|  \___/ \__,_| .__/
         *                                              |_|
         */
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

        /***
         *      _     _         ____       _       _
         *     | |__ | |_ _ __ |  _ \ _ __(_)_ __ | |_
         *     | '_ \| __| '_ \| |_) | '__| | '_ \| __|
         *     | |_) | |_| | | |  __/| |  | | | | | |_
         *     |_.__/ \__|_| |_|_|   |_|  |_|_| |_|\__|
         *
         */
        JideButton btnPrint = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.print"), SYSConst.icon22print, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
        list.add(btnPrint);


        return list;
    }

    private CollapsiblePane createCP4(final Users user) {
        final CollapsiblePane cp = new CollapsiblePane();

        /***
         *      _   _ _____    _    ____  _____ ____
         *     | | | | ____|  / \  |  _ \| ____|  _ \
         *     | |_| |  _|   / _ \ | | | |  _| | |_) |
         *     |  _  | |___ / ___ \| |_| | |___|  _ <
         *     |_| |_|_____/_/   \_\____/|_____|_| \_\
         *
         */

        JPanel titlePanelleft = new JPanel();
        titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));


        /***
         *      _     _       _    _           _   _                _   _                _
         *     | |   (_)_ __ | | _| |__  _   _| |_| |_ ___  _ __   | | | | ___  __ _  __| | ___ _ __
         *     | |   | | '_ \| |/ / '_ \| | | | __| __/ _ \| '_ \  | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
         *     | |___| | | | |   <| |_) | |_| | |_| || (_) | | | | |  _  |  __/ (_| | (_| |  __/ |
         *     |_____|_|_| |_|_|\_\_.__/ \__,_|\__|\__\___/|_| |_| |_| |_|\___|\__,_|\__,_|\___|_|
         *
         */
        JideButton btnUser = GUITools.createHyperlinkButton("<html><font size=+1>" +
                user.toString() +
                (UsersTools.isQualified(user) ?
                        ", Examen" : "") +
                "</font></html>", null, null);

        btnUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    cp.setCollapsed(!cp.isCollapsed());
                } catch (PropertyVetoException e) {
                    OPDE.error(e);
                }
            }
        });
        btnUser.setForeground(user.isActive() ? Color.black : Color.gray);
        titlePanelleft.add(btnUser);


        JPanel titlePanelright = new JPanel();
        titlePanelright.setLayout(new BoxLayout(titlePanelright, BoxLayout.LINE_AXIS));


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
        btnChangePW.setToolTipText(OPDE.lang.getString(internalClassID + ".btnChangePW.tooltip"));
        btnChangePW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new DlgChangePW(user, new Closure() {
                    @Override
                    public void execute(Object answer) {
                        if (answer != null) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                Users myUser = em.merge(user);
                                em.lock(myUser, LockModeType.OPTIMISTIC);
                                myUser.setMd5pw(SYSTools.hashword(answer.toString()));
                                em.getTransaction().commit();

                                lstUsers.remove(user);
                                lstUsers.add(myUser);
                                Collections.sort(lstUsers);
                                CollapsiblePane cp = createCP4(myUser);
                                boolean wasCollapsed = userMap.get(user).isCollapsed();
                                userMap.remove(user);
                                userMap.put(myUser, cp);
                                cp.setCollapsed(wasCollapsed);
                                buildPanel();

                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".pwchanged")));

                            } catch (OptimisticLockException ole) {
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
        titlePanelright.add(btnChangePW);

        /***
         *      _     _            _        _   _           ___                  _   _
         *     | |__ | |_ _ __    / \   ___| |_(_)_   _____|_ _|_ __   __ _  ___| |_(_)_   _____
         *     | '_ \| __| '_ \  / _ \ / __| __| \ \ / / _ \| || '_ \ / _` |/ __| __| \ \ / / _ \
         *     | |_) | |_| | | |/ ___ \ (__| |_| |\ V /  __/| || | | | (_| | (__| |_| |\ V /  __/
         *     |_.__/ \__|_| |_/_/   \_\___|\__|_| \_/ \___|___|_| |_|\__,_|\___|\__|_| \_/ \___|
         *
         */
        final JButton btnActiveInactive = new JButton(user.isActive() ? SYSConst.icon22stop : SYSConst.icon22play);
        btnActiveInactive.setPressedIcon(user.isActive() ? SYSConst.icon22stopPressed : SYSConst.icon22playPressed);
        btnActiveInactive.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnActiveInactive.setContentAreaFilled(false);
        btnActiveInactive.setBorder(null);
        btnActiveInactive.setToolTipText(OPDE.lang.getString(internalClassID + (user.isActive() ? ".btnActiveInactive.stop" : ".btnActiveInactive.play")));
        btnActiveInactive.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                EntityManager em = OPDE.createEM();
                try {
                    em.getTransaction().begin();
                    Users myUser = em.merge(user);
                    em.lock(myUser, LockModeType.OPTIMISTIC);

                    myUser.setStatus(myUser.isActive() ? UsersTools.STATUS_INACTIVE : UsersTools.STATUS_ACTIVE);
                    em.getTransaction().commit();

                    lstUsers.remove(user);
                    lstUsers.add(myUser);
                    Collections.sort(lstUsers);
                    CollapsiblePane cp = createCP4(myUser);
                    boolean wasCollapsed = userMap.get(user).isCollapsed();
                    userMap.remove(user);
                    userMap.put(myUser, cp);
                    cp.setCollapsed(wasCollapsed);

                    buildPanel();
                } catch (OptimisticLockException ole) {
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
        titlePanelright.add(btnActiveInactive);

        titlePanelleft.setOpaque(false);
        titlePanelright.setOpaque(false);
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);

        titlePanel.setLayout(new GridBagLayout());
        ((GridBagLayout) titlePanel.getLayout()).columnWidths = new int[]{0, 80};
        ((GridBagLayout) titlePanel.getLayout()).columnWeights = new double[]{1.0, 1.0};

        titlePanel.add(titlePanelleft, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 5), 0, 0));

        titlePanel.add(titlePanelright, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));

        cp.setTitleLabelComponent(titlePanel);
        cp.setSlidingDirection(SwingConstants.SOUTH);

        try {
            cp.setCollapsed(true);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }


        /***
         *       ___ ___  _  _ _____ ___ _  _ _____
         *      / __/ _ \| \| |_   _| __| \| |_   _|
         *     | (_| (_) | .` | | | | _|| .` | | |
         *      \___\___/|_|\_| |_| |___|_|\_| |_|
         *
         */

        cp.addCollapsiblePaneListener(new

                CollapsiblePaneAdapter() {
                    @Override
                    public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                        cp.setContentPane(createContentPanel4(user));
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

    private JPanel createContentPanel4(final Users user) {

//        CollapsiblePane cpContent = new CollapsiblePane();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new VerticalLayout());
//        contentPanel.add(cpContent);

        for (final Groups group : lstGroups) {
            JCheckBox cbGroup = new JCheckBox(group.toString());
            cbGroup.setFont(SYSConst.ARIAL14);
            cbGroup.setSelected(user.getGroups().contains(group));
            cbGroup.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent itemEvent) {


                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        Users myUser = em.merge(user);
                        em.lock(myUser, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                        Groups myGroup = em.merge(group);
                        em.lock(myGroup, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                            myUser.getGroups().add(myGroup);
                            myGroup.getMembers().add(myUser);
                        } else {
                            myUser.getGroups().remove(myGroup);
                            myGroup.getMembers().remove(myUser);
                        }

                        em.getTransaction().commit();
                        lstUsers.remove(user);
                        lstUsers.add(myUser);
                        lstGroups.remove(group);
                        lstGroups.add(myGroup);
                        Collections.sort(lstGroups);

                        userMap.remove(user);
                        CollapsiblePane cp = createCP4(myUser);
                        cp.setCollapsed(false);
                        userMap.put(myUser, cp);
                        groupMap.remove(group);
                        groupMap.put(myGroup, createCP4(myGroup));
                        buildPanel();
                    } catch (OptimisticLockException ole) {
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

            contentPanel.add(cbGroup);

        }
        return contentPanel;
    }

    private CollapsiblePane createCP4(final Groups group) {
        final CollapsiblePane cp = new CollapsiblePane();

        /***
         *      _   _ _____    _    ____  _____ ____
         *     | | | | ____|  / \  |  _ \| ____|  _ \
         *     | |_| |  _|   / _ \ | | | |  _| | |_) |
         *     |  _  | |___ / ___ \| |_| | |___|  _ <
         *     |_| |_|_____/_/   \_\____/|_____|_| \_\
         *
         */

        JPanel titlePanelleft = new JPanel();
        titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));


        /***
         *      _     _       _    _           _   _                _   _                _
         *     | |   (_)_ __ | | _| |__  _   _| |_| |_ ___  _ __   | | | | ___  __ _  __| | ___ _ __
         *     | |   | | '_ \| |/ / '_ \| | | | __| __/ _ \| '_ \  | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
         *     | |___| | | | |   <| |_) | |_| | |_| || (_) | | | | |  _  |  __/ (_| | (_| |  __/ |
         *     |_____|_|_| |_|_|\_\_.__/ \__,_|\__|\__\___/|_| |_| |_| |_|\___|\__,_|\__,_|\___|_|
         *
         */
        JideButton btnUser = GUITools.createHyperlinkButton("<html><font size=+1>" +
                group.toString() +
                (group.isQualified() ?
                        ", " + OPDE.lang.getString(internalClassID + ".qualifiedGroup") : "") +
                "</font></html>", null, null);

        btnUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    cp.setCollapsed(!cp.isCollapsed());
                } catch (PropertyVetoException e) {
                    OPDE.error(e);
                }
            }
        });
        titlePanelleft.add(btnUser);


        JPanel titlePanelright = new JPanel();
        titlePanelright.setLayout(new BoxLayout(titlePanelright, BoxLayout.LINE_AXIS));


//        final JButton btnChangePW = new JButton(SYSConst.icon22password);
//        btnChangePW.setPressedIcon(SYSConst.icon22passwordPressed);
//        btnChangePW.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        btnChangePW.setContentAreaFilled(false);
//        btnChangePW.setBorder(null);
//        btnChangePW.setToolTipText(OPDE.lang.getString(internalClassID + ".btnChangePW.tooltip"));
//        btnChangePW.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                new DlgChangePW(user, new Closure() {
//                    @Override
//                    public void execute(Object answer) {
//                        if (answer != null) {
//                            EntityManager em = OPDE.createEM();
//                            try {
//                                em.getTransaction().begin();
//                                Users myUser = em.merge(user);
//                                em.lock(myUser, LockModeType.OPTIMISTIC);
//                                myUser.setMd5pw(SYSTools.hashword(answer.toString()));
//                                em.getTransaction().commit();
//
//                                lstUsers.remove(user);
//                                lstUsers.add(myUser);
//                                Collections.sort(lstUsers);
//                                CollapsiblePane cp = createCP4(myUser);
//                                boolean wasCollapsed = userMap.get(user).isCollapsed();
//                                userMap.remove(user);
//                                userMap.put(myUser, cp);
//                                cp.setCollapsed(wasCollapsed);
//                                buildPanel();
//
//                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".pwchanged")));
//
//                            } catch (OptimisticLockException ole) {
//                                if (em.getTransaction().isActive()) {
//                                    em.getTransaction().rollback();
//                                }
//                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                    OPDE.getMainframe().emptyFrame();
//                                    OPDE.getMainframe().afterLogin();
//                                }
//                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                            } catch (Exception e) {
//                                if (em.getTransaction().isActive()) {
//                                    em.getTransaction().rollback();
//                                }
//                                OPDE.fatal(e);
//                            } finally {
//                                em.close();
//                            }
//                        }
//                    }
//                });
//            }
//        });
//        titlePanelright.add(btnChangePW);


        titlePanelleft.setOpaque(false);
        titlePanelright.setOpaque(false);
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);

        titlePanel.setLayout(new GridBagLayout());
        ((GridBagLayout) titlePanel.getLayout()).columnWidths = new int[]{0, 80};
        ((GridBagLayout) titlePanel.getLayout()).columnWeights = new double[]{1.0, 1.0};

        titlePanel.add(titlePanelleft, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 5), 0, 0));

        titlePanel.add(titlePanelright, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));

        cp.setTitleLabelComponent(titlePanel);
        cp.setSlidingDirection(SwingConstants.SOUTH);

        try {
            cp.setCollapsed(true);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }


        /***
         *       ___ ___  _  _ _____ ___ _  _ _____
         *      / __/ _ \| \| |_   _| __| \| |_   _|
         *     | (_| (_) | .` | | | | _|| .` | | |
         *      \___\___/|_|\_| |_| |___|_|\_| |_|
         *
         */

        cp.addCollapsiblePaneListener(new

                CollapsiblePaneAdapter() {
                    @Override
                    public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                        cp.setContentPane(createContentPanel4(group));
                        cp.setOpaque(false);
                    }
                }

        );

        cp.setHorizontalAlignment(SwingConstants.LEADING);
        cp.setOpaque(false);

        return cp;
    }


    private JPanel createContentPanel4(final Groups group) {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new VerticalLayout());

        if (!group.isEveryone()) { // everyone does not need a membership.
            CollapsiblePane cpMember = new CollapsiblePane(OPDE.lang.getString(internalClassID + ".members"));
            JPanel contentMember = new JPanel();
            contentMember.setLayout(new VerticalLayout());

            for (final Users user : lstUsers) {
                if (user.isActive()) {
                    JCheckBox cbUser = new JCheckBox(user.toString());
                    cbUser.setFont(SYSConst.ARIAL14);
                    cbUser.setSelected(group.getMembers().contains(user));
                    cbUser.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent itemEvent) {

                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                Users myUser = em.merge(user);
                                em.lock(myUser, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                Groups myGroup = em.merge(group);
                                em.lock(myGroup, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                                    myUser.getGroups().add(myGroup);
                                    myGroup.getMembers().add(myUser);
                                } else {
                                    myUser.getGroups().remove(myGroup);
                                    myGroup.getMembers().remove(myUser);
                                }

                                em.getTransaction().commit();
                                lstUsers.remove(user);
                                lstUsers.add(myUser);
                                lstGroups.remove(group);
                                lstGroups.add(myGroup);
                                Collections.sort(lstUsers);
                                userMap.remove(user);
                                userMap.put(myUser, createCP4(myUser));

                                groupMap.remove(group);
                                CollapsiblePane cp = createCP4(myGroup);
                                cp.setCollapsed(false);
                                groupMap.put(myGroup, cp);

                                buildPanel();
                            } catch (OptimisticLockException ole) {
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
                    contentMember.add(cbUser);
                }
                cpMember.setContentPane(contentMember);
            }

            try {
                cpMember.setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            contentPanel.add(cpMember);
            cpMember.setFont(SYSConst.ARIAL14BOLD);
        }

        if (!group.isAdmin()) { // admin does not need further acls. he is in godmode anyways
            contentPanel.add(createClassesPanel4(group));
        }

        return contentPanel;
    }


    private JPanel createClassesPanel4(final Groups group) {

        CollapsiblePane cpClasses = new CollapsiblePane(OPDE.lang.getString(internalClassID + ".modules"));
        JPanel contentClasses = new JPanel();
        contentClasses.setLayout(new VerticalLayout());

//        JPanel classPanel = new JPanel();
//        classPanel.setLayout(new VerticalLayout());

        for (final InternalClass ic : OPDE.getAppInfo().getInternalClasses().values()) {


            JPanel aclPanel = new JPanel();
            aclPanel.setLayout(new VerticalLayout());


            for (final InternalClassACL acl : ic.getAcls()) {
                JCheckBox cbACL = new JCheckBox(InternalClassACL.strACLS[acl.getAcl()]);
                cbACL.setFont(SYSConst.ARIAL14);
//                cbACL.setSelected(group.getMembers().contains(user));
//                cbACL.addItemListener(new ItemListener() {
//                    @Override
//                    public void itemStateChanged(ItemEvent itemEvent) {
//
//                        EntityManager em = OPDE.createEM();
//                        try {
//                            em.getTransaction().begin();
//                            Users myUser = em.merge(user);
//                            em.lock(myUser, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//                            Groups myGroup = em.merge(group);
//                            em.lock(myGroup, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//                            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
//                                myUser.getGroups().add(myGroup);
//                                myGroup.getMembers().add(myUser);
//                            } else {
//                                myUser.getGroups().remove(myGroup);
//                                myGroup.getMembers().remove(myUser);
//                            }
//
//                            em.getTransaction().commit();
//                            lstUsers.remove(user);
//                            lstUsers.add(myUser);
//                            lstGroups.remove(group);
//                            lstGroups.add(myGroup);
//                            Collections.sort(lstUsers);
//                            userMap.remove(user);
//                            userMap.put(myUser, createCP4(myUser));
//
//                            groupMap.remove(group);
//                            CollapsiblePane cp = createCP4(myGroup);
//                            cp.setCollapsed(false);
//                            groupMap.put(myGroup, cp);
//
//                            buildPanel();
//                        } catch (OptimisticLockException ole) {
//                            if (em.getTransaction().isActive()) {
//                                em.getTransaction().rollback();
//                            }
//                            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                OPDE.getMainframe().emptyFrame();
//                                OPDE.getMainframe().afterLogin();
//                            }
//                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                        } catch (Exception e) {
//                            if (em.getTransaction().isActive()) {
//                                em.getTransaction().rollback();
//                            }
//                            OPDE.fatal(e);
//                        } finally {
//                            em.close();
//                        }
//                    }
//                });

                aclPanel.add(cbACL);
            }
            CollapsiblePane cpClass = new CollapsiblePane(ic.getShortDescription() + " / " + ic.getLongDescription());
            cpClass.setStyle(CollapsiblePane.TREE_STYLE);
            cpClass.setContentPane(aclPanel);

            contentClasses.add(cpClass);
            try {
                cpClass.setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        cpClasses.setContentPane(contentClasses);
        try {
            cpClasses.setCollapsed(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        cpClasses.setFont(SYSConst.ARIAL14BOLD);

        return cpClasses;
    }


    private void buildPanel() {
        cpMain.removeAll();
        cpMain.setLayout(new JideBoxLayout(cpMain, JideBoxLayout.Y_AXIS));
        Collections.sort(lstUsers);
        Collections.sort(lstGroups);
        if (tbShowUsers.isSelected()) {
            for (Users user : lstUsers) {
                if (tbOldUsers.isSelected() || user.isActive()) {
                    CollapsiblePane cp = userMap.get(user);
                    cpMain.add(cp);
                }
            }
        } else {
            for (Groups group : lstGroups) {
                CollapsiblePane cp = groupMap.get(group);
                cpMain.add(cp);
            }
        }
        cpMain.addExpansion();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private CollapsiblePanes cpMain;
    // End of variables declaration//GEN-END:variables
}
