package op.users;

import entity.system.Groups;
import entity.system.Users;
import op.OPDE;
import op.threads.DisplayManager;
import op.tools.RiverLayout;
import op.tools.SYSConst;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 01.09.12
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */
public class PnlEditMembers extends JPanel {

    private Groups group;
    private ArrayList<Users> listUsers; // is only needed once during setup of the panel
    private HashMap<String, Users> userMap; // will be used during the use of the panel

    public PnlEditMembers(Groups group, ArrayList<Users> listUsers) {
        super();
        this.group = group;
        this.listUsers = listUsers;
        setLayout(new GridLayout(0, 3));
        initPanel();
    }

    private void initPanel() {

        userMap = new HashMap<String, Users>();
//        int num = 0;

        for (final Users user : listUsers) {

//            num++;

            if (user.isActive()) {
                final String uid = user.getUID();
                userMap.put(uid, user);

                JCheckBox cbMembership = new JCheckBox(user.toString());
                cbMembership.setFont(SYSConst.ARIAL14);
                cbMembership.setSelected(group.getMembers().contains(userMap.get(uid)));
                cbMembership.addItemListener(itemEvent -> {

                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        Users myUser = em.merge(userMap.get(uid));
                        em.lock(myUser, LockModeType.OPTIMISTIC);
                        Groups myGroup = em.merge(group);
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
                        group = myGroup;
                        userMap.put(uid, myUser);

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
                add(cbMembership);


            }
        }

    }

}
