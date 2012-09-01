package op.users;

import entity.system.Acl;
import entity.system.SYSGROUPS2ACL;
import entity.system.SYSGROUPS2ACLTools;
import op.OPDE;
import op.system.InternalClass;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.tools.SYSConst;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 01.09.12
 * Time: 11:36
 * To change this template use File | Settings | File Templates.
 */
public class PnlEditACL extends JPanel {
    private SYSGROUPS2ACL sysgroups2ACL;

    public PnlEditACL(SYSGROUPS2ACL sysgroups2ACL) {
        super();
        this.sysgroups2ACL = sysgroups2ACL;
        setLayout(new GridLayout(0, 8));
        initPanel();
    }

    private void initPanel() {
        InternalClass ic = OPDE.getAppInfo().getInternalClasses().get(sysgroups2ACL.getInternalClassID());

        for (final InternalClassACL possibleACL : ic.getPossibleACLs()) {

            JCheckBox cbACL = new JCheckBox(InternalClassACL.strACLS[possibleACL.getACLcode()]);
            cbACL.setToolTipText(possibleACL.getDescription());
            cbACL.setFont(SYSConst.ARIAL14);
            // The CB should be selected if (and only if) the IntClass (with the fitting internalClassesID) is assigned to the group and
            // a ACL is assigned to the SYSGROUPS2ACL object with the same SHORT code for the acl.
            cbACL.setSelected(SYSGROUPS2ACLTools.findACLbyCODE(sysgroups2ACL, possibleACL.getACLcode()) != null);
            cbACL.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent itemEvent) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();

                        SYSGROUPS2ACL mySYSGROUPS2ACL = em.merge(sysgroups2ACL);
                        em.lock(mySYSGROUPS2ACL, LockModeType.OPTIMISTIC);

                        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                            mySYSGROUPS2ACL.getAclCollection().add(em.merge(new Acl(possibleACL.getACLcode(), mySYSGROUPS2ACL)));
                        } else {
                            Acl myAcl = em.merge(SYSGROUPS2ACLTools.findACLbyCODE(mySYSGROUPS2ACL, possibleACL.getACLcode()));
                            mySYSGROUPS2ACL.getAclCollection().remove(myAcl);
                            em.remove(myAcl);
                        }
                        em.getTransaction().commit();

                        sysgroups2ACL = mySYSGROUPS2ACL;

                    } catch (OptimisticLockException ole) {
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
                }
            });
            add(cbACL);

        }
    }
}
