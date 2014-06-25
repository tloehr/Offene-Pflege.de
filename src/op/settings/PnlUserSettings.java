/*
 * Created by JFormDesigner on Thu Feb 14 15:09:30 CET 2013
 */

package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.system.Users;
import op.OPDE;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.CleanablePanel;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Torsten Löhr
 */
public class PnlUserSettings extends CleanablePanel {
    public static final String internalClassID = "opde.usersettings";
    private final JScrollPane jspSearch;

    public PnlUserSettings(JScrollPane jspSearch) {
        this.jspSearch = jspSearch;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        OPDE.getDisplayManager().setMainMessage(SYSTools.xx(internalClassID));
        OPDE.getDisplayManager().clearAllIcons();

        lblPWTitle.setText(SYSTools.xx(internalClassID + ".pwtitle"));
        lblNew.setText(SYSTools.xx(internalClassID + ".newpw"));
        lblOld.setText(SYSTools.xx(internalClassID + ".oldpw"));
        btnChangePW.setToolTipText(SYSTools.xx(internalClassID + ".pwtitle"));

        prepareSearchArea();
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
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx(internalClassID + ".oldpwwrong")));
            return;
        }
        if (txtNew.getText().trim().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx(internalClassID + ".newpwempty")));
            return;
        }

        try {
            em.getTransaction().begin();
            em.lock(user, LockModeType.OPTIMISTIC);
            user.setMd5pw(SYSTools.hashword(txtNew.getText().trim()));
            em.getTransaction().commit();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".pwchanged"));

            OPDE.getLogin().setUser(user);
            txtNew.setText("");
            txtOld.setText("");
        } catch (OptimisticLockException ole) { OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                OPDE.getMainframe().emptyFrame();
                OPDE.getMainframe().afterLogin();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
        } catch (RollbackException ole) {
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


    private void prepareSearchArea() {
        jspSearch.setViewportView(new JPanel());
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblPWTitle = new JLabel();
        lblOld = new JLabel();
        txtOld = new JTextField();
        lblNew = new JLabel();
        txtNew = new JTextField();
        btnChangePW = new JButton();

        //======== this ========
        setLayout(new FormLayout(
                "2*(default, $lcgap), default:grow, $lcgap, default",
                "5*(default, $lgap), default"));

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
        btnChangePW.setBorderPainted(false);
        btnChangePW.setBorder(null);
        btnChangePW.setContentAreaFilled(false);
        btnChangePW.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnChangePW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnChangePWActionPerformed(e);
            }
        });
        add(btnChangePW, CC.xy(5, 9, CC.RIGHT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblPWTitle;
    private JLabel lblOld;
    private JTextField txtOld;
    private JLabel lblNew;
    private JTextField txtNew;
    private JButton btnChangePW;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
