package de.offene_pflege.op.users;

import com.jidesoft.swing.JideButton;
import de.offene_pflege.backend.entity.system.Acl;
import de.offene_pflege.backend.entity.system.SYSGROUPS2ACL;
import de.offene_pflege.backend.entity.system.SYSGROUPS2ACLTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.system.InternalClass;
import de.offene_pflege.op.system.InternalClassACL;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.RiverLayout;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.net.URI;

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
        setLayout(new RiverLayout(5, 0));
//        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        initPanel();
    }

    private void initPanel() {
        final InternalClass ic = OPDE.getAppInfo().getInternalClasses().get(sysgroups2ACL.getInternalClassID());

        add(RiverLayout.PARAGRAPH_BREAK + " " + RiverLayout.HFILL, new JLabel(SYSTools.toHTMLForScreen(SYSConst.html_paragraph(SYSConst.html_italic(ic.getLongDescription())))));

        if (!SYSTools.catchNull(ic.getHelpurl()).isEmpty()) {
            JideButton helpButton = GUITools.createHyperlinkButton("misc.msg.explain.this.to.me", SYSConst.icon22helpMe, e -> {
                try {
                    URI uri = new URI(SYSTools.xx(ic.getHelpurl()));
                    Desktop.getDesktop().browse(uri);
                } catch (Exception ex) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.mainframe.noHelpAvailable"));
                }
            });
            helpButton.setFont(SYSConst.ARIAL14ITALIC);
            add(RiverLayout.LEFT, helpButton);
        }


        for (final InternalClassACL possibleACL : ic.getPossibleACLs()) {

            JCheckBox cbACL = new JCheckBox(InternalClassACL.strACLS[possibleACL.getACLcode()]);
//            cbACL.setToolTipText();
            cbACL.setFont(SYSConst.ARIAL14BOLD);
            // The CB should be selected if (and only if) the IntClass (with the fitting internalClassesID) is assigned to the group and
            // a ACL is assigned to the SYSGROUPS2ACL object with the same SHORT code for the acl.
            cbACL.setSelected(SYSGROUPS2ACLTools.findACLbyCODE(sysgroups2ACL, possibleACL.getACLcode()) != null);
            cbACL.addItemListener(itemEvent -> {
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
                    OPDE.warn(ole);
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
            add(RiverLayout.PARAGRAPH_BREAK+ " "+ RiverLayout.VTOP, cbACL);
            add(RiverLayout.LEFT + " "+ RiverLayout.HFILL, new JLabel(SYSTools.toHTMLForScreen(SYSConst.html_paragraph(possibleACL.getDescription()))));
        }

    }
}
