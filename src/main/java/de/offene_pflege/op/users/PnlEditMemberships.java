package de.offene_pflege.op.users;

import de.offene_pflege.entity.system.Groups;
import de.offene_pflege.entity.system.Users;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.tools.SYSConst;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 01.09.12
 * Time: 14:37
 * To change this template use File | Settings | File Templates.
 */
public class PnlEditMemberships extends JPanel {
    private Users user;
    private ArrayList<Groups> listGroups; // is only needed once during setup of the panel
    private HashMap<String, Groups> groupMap; // will be used during the use of the panel

    public PnlEditMemberships(Users user, ArrayList<Groups> listGroups) {
        super();
        this.user = user;
        this.listGroups = listGroups;
        setLayout(new GridLayout(0, 3));
        initPanel();
    }

    private void initPanel() {

        groupMap = new HashMap<String, Groups>();

        for (final Groups group : listGroups) {
            final String gid = group.getGID();
            groupMap.put(group.getGID(), group);
            JCheckBox cbGroup = new JCheckBox(group.getGID());
            cbGroup.setToolTipText(group.getDescription());
            cbGroup.setFont(SYSConst.ARIAL14);
            cbGroup.setSelected(user.getGroups().contains(groupMap.get(gid)));
            cbGroup.addItemListener(itemEvent -> {

                EntityManager em = OPDE.createEM();
                try {
                    em.getTransaction().begin();
                    Users myUser = em.merge(user);
                    em.lock(myUser, LockModeType.OPTIMISTIC);
                    Groups myGroup = em.merge(groupMap.get(gid));
                    em.lock(myGroup, LockModeType.OPTIMISTIC);

                    if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                        myUser.getGroups().add(myGroup);
                        myGroup.getMembers().add(myUser);
                    } else {
                        myUser.getGroups().remove(myGroup);
                        myGroup.getMembers().remove(myUser);
                    }

                    em.getTransaction().commit();

                    // so we won't get locking exceptions because of outdated version informations
                    user = myUser;
                    groupMap.put(gid, myGroup);
                } catch (OptimisticLockException ole) { OPDE.warn(ole);
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
            add(cbGroup);
            cbGroup.setEnabled(!group.isEveryone());
        }


    }
}
