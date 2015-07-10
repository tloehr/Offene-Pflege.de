/*
 * Created by JFormDesigner on Thu Feb 14 15:09:30 CET 2013
 */

package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.system.SYSPropsTools;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.system.EMailSystem;
import op.system.Recipient;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import gui.interfaces.CleanablePanel;
import gui.GUITools;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Random;

/**
 * @author Torsten Löhr
 */
public class PnlUserSettings extends CleanablePanel {

    private final JScrollPane jspSearch;
    private JToggleButton tbNotify;
    private boolean ignoreTBNotify;

    public PnlUserSettings(JScrollPane jspSearch) {
        super("opde.usersettings");
        this.jspSearch = jspSearch;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        ignoreTBNotify = true;

        OPDE.getDisplayManager().setMainMessage(SYSTools.xx(internalClassID));
        OPDE.getDisplayManager().clearAllIcons();

        lblPWTitle.setText(SYSTools.xx("opde.usersettings.pwtitle"));
        lblNew.setText(SYSTools.xx("opde.usersettings.newpw"));
        lblOld.setText(SYSTools.xx("opde.usersettings.oldpw"));
        btnChangePW.setToolTipText(SYSTools.xx("opde.usersettings.pwtitle"));

        lblMailTitle.setText(SYSTools.xx("opde.usersettings.your.mailsettings"));
        lblMailAddress.setText(SYSTools.xx("opde.usersettings.your.mailaddress"));
        btnSendTestMail.setToolTipText(SYSTools.xx("opde.settings.global.mail.btnTestmail"));

        tbNotify = GUITools.getNiceToggleButton("opde.usersettings.enable.notification");
        panel1.add(tbNotify, CC.xy(5, 1));

        tbNotify.setEnabled(OPDE.getLogin().getUser().getMailConfirmed() != UsersTools.MAIL_UNCONFIRMED);
        txtMailKey.setEnabled(OPDE.getLogin().getUser().getMailConfirmed() == UsersTools.MAIL_UNCONFIRMED);

        tbNotify.setSelected(OPDE.getLogin().getUser().getMailConfirmed() == UsersTools.MAIL_NOTIFICATIONS_ENABLED && OPDE.getProps().containsKey(SYSPropsTools.KEY_MAIL_TESTKEY));

        tbNotify.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                if (ignoreTBNotify) return;

                txtMailKey.setEnabled(e.getStateChange() == ItemEvent.DESELECTED);

                EntityManager em = OPDE.createEM();
                Users user = em.merge(OPDE.getLogin().getUser());
                try {
                    em.getTransaction().begin();

                    em.lock(user, LockModeType.OPTIMISTIC);
                    user.setMailConfirmed(e.getStateChange() == ItemEvent.SELECTED ? UsersTools.MAIL_NOTIFICATIONS_ENABLED : UsersTools.MAIL_CONFIRMED);
                    em.getTransaction().commit();

                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.usersettings.notifications.enabled"));

                    OPDE.getLogin().setUser(user);
                    txtMailKey.setText("");
                } catch (OptimisticLockException ole) {
                    OPDE.warn(ole);
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                        OPDE.getMainframe().emptyFrame();
                        OPDE.getMainframe().afterLogin();
                    }
                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                } catch (RollbackException ole) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                        OPDE.getMainframe().emptyFrame();
                        OPDE.getMainframe().afterLogin();
                    }
                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                } catch (Exception ex) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    OPDE.fatal(ex);
                } finally {
                    em.close();
                }

            }

        });

        txtMailAddress.setText(SYSTools.catchNull(OPDE.getLogin().getUser().getEMail()));
        lblMailStatus.setIcon(OPDE.getLogin().getUser().getMailConfirmed() != UsersTools.MAIL_UNCONFIRMED ? SYSConst.icon22ledGreenOn : SYSConst.icon22ledGreenOff);
        lblMailKey.setText(SYSTools.xx("opde.usersettings.mail.key"));

        prepareSearchArea();
        ignoreTBNotify = false;
    }

    @Override
    public void cleanup() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void reload() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getInternalClassID() {
        return internalClassID;
    }

    private void btnChangePWActionPerformed(ActionEvent evt) {
        EntityManager em = OPDE.createEM();
        Users user = em.merge(OPDE.getLogin().getUser());
        if (!user.getMd5pw().equals(SYSTools.hashword(txtOld.getText().trim()))) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("opde.settings.personal.oldpw.wrong")));
            return;
        }
        if (txtNew.getText().trim().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("opde.usersettings.newpwempty")));
            return;
        }

        try {
            em.getTransaction().begin();
            em.lock(user, LockModeType.OPTIMISTIC);
            user.setMd5pw(SYSTools.hashword(txtNew.getText().trim()));
            em.getTransaction().commit();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.usersettings.pwchanged"));

            OPDE.getLogin().setUser(user);
            txtNew.setText("");
            txtOld.setText("");
        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                OPDE.getMainframe().emptyFrame();
                OPDE.getMainframe().afterLogin();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
        } catch (RollbackException ole) {
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


    private void prepareSearchArea() {
        jspSearch.setViewportView(new JPanel());
    }

    private void btnSendTestMailActionPerformed(ActionEvent e) {

        if (!EMailSystem.isValidEmailAddress(txtMailAddress.getText().trim())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.users.dlgusers.wrongemail", DisplayMessage.WARNING));
            return;
        }

        Random generator = new Random(System.currentTimeMillis());
        String testkey = SYSTools.padL(Integer.toString(generator.nextInt(9999)), 4, "0");
        EMailSystem.sendMail(SYSTools.xx("opde.settings.global.mail.testsubject"), SYSTools.xx("opde.usersettings.confirmmail.testbody") + "<br/>"+SYSConst.html_h2(SYSTools.xx("opde.usersettings.mail.key")+": " + testkey), new Recipient(txtMailAddress.getText().trim(), OPDE.getLogin().getUser().getFullname()), null);

        EntityManager em = OPDE.createEM();

        try {
            em.getTransaction().begin();
            Users user = em.merge(OPDE.getLogin().getUser());
            em.lock(user, LockModeType.OPTIMISTIC);
            SYSPropsTools.storeProp(em, SYSPropsTools.KEY_MAIL_TESTKEY, testkey, user);
            user.setEMail(txtMailAddress.getText().trim());
            user.setMailConfirmed(UsersTools.MAIL_UNCONFIRMED);
            em.getTransaction().commit();

            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.usersettings.notifications.enabled"));

            OPDE.getLogin().setUser(user);

        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                OPDE.getMainframe().emptyFrame();
                OPDE.getMainframe().afterLogin();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
        } catch (RollbackException ole) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                OPDE.getMainframe().emptyFrame();
                OPDE.getMainframe().afterLogin();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(ex);
        } finally {
            em.close();
        }

        ignoreTBNotify = true;

        lblMailStatus.setIcon(SYSConst.icon22ledGreenOff);
        txtMailKey.setEnabled(true);
        tbNotify.setSelected(false);
        tbNotify.setEnabled(false);

        ignoreTBNotify = false;
    }

    private void txtMailAddressActionPerformed(ActionEvent e) {


    }

    private void txtMailKeyActionPerformed(ActionEvent e) {
        String testkey = OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_TESTKEY);

        if (testkey.equals(txtMailKey.getText().trim())) {
            tbNotify.setEnabled(true);

            EntityManager em = OPDE.createEM();
            Users user = em.merge(OPDE.getLogin().getUser());
            try {
                em.getTransaction().begin();

                em.lock(user, LockModeType.OPTIMISTIC);
                user.setMailConfirmed(UsersTools.MAIL_CONFIRMED);
                SYSPropsTools.removeProp(em, SYSPropsTools.KEY_MAIL_TESTKEY, OPDE.getLogin().getUser());
                em.getTransaction().commit();

                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.usersettings.mail.confirmed"));

                OPDE.getLogin().setUser(user);
                txtMailKey.setText("");

                lblMailStatus.setIcon(SYSConst.icon22ledGreenOn);

            } catch (OptimisticLockException ole) {
                OPDE.warn(ole);
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                    OPDE.getMainframe().emptyFrame();
                    OPDE.getMainframe().afterLogin();
                }
                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            } catch (RollbackException ole) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                    OPDE.getMainframe().emptyFrame();
                    OPDE.getMainframe().afterLogin();
                }
                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                OPDE.fatal(ex);
            } finally {
                em.close();
            }

        } else {
            lblMailStatus.setIcon(SYSConst.icon22ledGreenOff);
        }

    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblPWTitle = new JLabel();
        lblOld = new JLabel();
        txtOld = new JTextField();
        lblNew = new JLabel();
        txtNew = new JTextField();
        btnChangePW = new JButton();
        lblMailTitle = new JLabel();
        lblMailAddress = new JLabel();
        txtMailAddress = new JTextField();
        btnSendTestMail = new JButton();
        lblMailKey = new JLabel();
        panel1 = new JPanel();
        txtMailKey = new JTextField();
        lblMailStatus = new JLabel();

        //======== this ========
        setLayout(new FormLayout(
            "2*(default, $lcgap), 178dlu, $lcgap, default:grow",
            "5*(default, $lgap), 13dlu, 5*($lgap, default), $lgap, fill:default:grow"));

        //---- lblPWTitle ----
        lblPWTitle.setText("text");
        lblPWTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        add(lblPWTitle, CC.xywh(3, 3, 3, 1));

        //---- lblOld ----
        lblOld.setText("text");
        lblOld.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblOld, CC.xy(3, 5));

        //---- txtOld ----
        txtOld.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtOld, CC.xy(5, 5));

        //---- lblNew ----
        lblNew.setText("text");
        lblNew.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblNew, CC.xy(3, 7));

        //---- txtNew ----
        txtNew.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtNew, CC.xy(5, 7));

        //---- btnChangePW ----
        btnChangePW.setText(null);
        btnChangePW.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
        btnChangePW.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnChangePW.addActionListener(e -> btnChangePWActionPerformed(e));
        add(btnChangePW, CC.xy(5, 9, CC.RIGHT, CC.DEFAULT));

        //---- lblMailTitle ----
        lblMailTitle.setText("text");
        lblMailTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        add(lblMailTitle, CC.xywh(3, 13, 3, 1));

        //---- lblMailAddress ----
        lblMailAddress.setText("text");
        lblMailAddress.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblMailAddress, CC.xy(3, 15));

        //---- txtMailAddress ----
        txtMailAddress.setFont(new Font("Arial", Font.PLAIN, 14));
        txtMailAddress.addActionListener(e -> txtMailAddressActionPerformed(e));
        add(txtMailAddress, CC.xy(5, 15));

        //---- btnSendTestMail ----
        btnSendTestMail.setText("SendTestMail");
        btnSendTestMail.addActionListener(e -> btnSendTestMailActionPerformed(e));
        add(btnSendTestMail, CC.xy(5, 17));

        //---- lblMailKey ----
        lblMailKey.setText("text");
        lblMailKey.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblMailKey, CC.xy(3, 19));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                "default, $rgap, default, $ugap, default:grow",
                "default:grow"));

            //---- txtMailKey ----
            txtMailKey.setColumns(4);
            txtMailKey.setFont(new Font("Arial", Font.PLAIN, 16));
            txtMailKey.addActionListener(e -> txtMailKeyActionPerformed(e));
            panel1.add(txtMailKey, CC.xy(1, 1));

            //---- lblMailStatus ----
            lblMailStatus.setText(null);
            lblMailStatus.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/leddarkgreen.png")));
            panel1.add(lblMailStatus, CC.xy(3, 1));
        }
        add(panel1, CC.xy(5, 19, CC.FILL, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblPWTitle;
    private JLabel lblOld;
    private JTextField txtOld;
    private JLabel lblNew;
    private JTextField txtNew;
    private JButton btnChangePW;
    private JLabel lblMailTitle;
    private JLabel lblMailAddress;
    private JTextField txtMailAddress;
    private JButton btnSendTestMail;
    private JLabel lblMailKey;
    private JPanel panel1;
    private JTextField txtMailKey;
    private JLabel lblMailStatus;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
