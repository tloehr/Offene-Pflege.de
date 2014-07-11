package op.users;

import com.jidesoft.swing.JideButton;
import entity.system.Acl;
import entity.system.SYSGROUPS2ACL;
import entity.system.SYSGROUPS2ACLTools;
import op.OPDE;
import op.system.InternalClass;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.GUITools;
import op.tools.RiverLayout;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
            JideButton helpButton = GUITools.createHyperlinkButton("misc.msg.explain.this.to.me", SYSConst.icon22helpMe, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        URI uri = new URI(SYSTools.xx(ic.getHelpurl()));
                        Desktop.getDesktop().browse(uri);
                    } catch (Exception ex) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.mainframe.noHelpAvailable"));
                    }
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
                }
            });
            add(RiverLayout.PARAGRAPH_BREAK+ " "+ RiverLayout.VTOP, cbACL);
            add(RiverLayout.LEFT + " "+ RiverLayout.HFILL, new JLabel(SYSTools.toHTMLForScreen(SYSConst.html_paragraph(possibleACL.getDescription()))));
        }

    }
}
