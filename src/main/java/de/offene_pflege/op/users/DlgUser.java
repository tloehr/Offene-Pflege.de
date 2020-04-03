/*
 * Created by JFormDesigner on Tue Aug 28 16:03:54 CEST 2012
 */

package de.offene_pflege.op.users;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.backend.entity.system.OPUsers;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.system.EMailSystem;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.MyJDialog;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * @author Torsten Löhr
 */
public class DlgUser extends MyJDialog {
    public static final String internalClassID = "opde.users.dlgusers";
    private OPUsers user;
    private Closure callback;

    public DlgUser(OPUsers user, Closure callback) {
        super(false);
        this.user = user;
        this.callback = callback;
        initComponents();
        initPanel();
        pack();
//        setVisible(true);
    }

    private void initPanel() {
        lblFirstname.setText(SYSTools.xx("misc.msg.firstname"));
        lblName.setText(SYSTools.xx("misc.msg.name"));
        lblPW.setText(SYSTools.xx("misc.msg.password"));
        lblUID.setText(SYSTools.xx("misc.msg.uid"));
        lblEmail.setText(SYSTools.xx("misc.msg.email"));

        txtName.setText(user.getName());
        txtEMail.setText(user.getEMail());
        txtVorname.setText(user.getVorname());
        txtUID.setText(user.getUID());

        txtPW.setEnabled(user.getUID() == null);
        txtUID.setEnabled(user.getUID() == null);

//        txtName.requestFocus();

    }

    private void txtNameFocusLost(FocusEvent e) {
        if (txtPW.isEnabled() && !txtName.getText().isEmpty() && !txtVorname.getText().isEmpty()) {
            txtPW.setText(SYSTools.generatePassword(txtVorname.getText(), txtName.getText()));
        }
    }

    private void txtVornameFocusLost(FocusEvent e) {
        if (txtPW.isEnabled() && !txtName.getText().isEmpty() && !txtVorname.getText().isEmpty()) {
            txtPW.setText(SYSTools.generatePassword(txtVorname.getText(), txtName.getText()));
        }
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        user = null;
        dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        callback.execute(user);
    }


    private void btnSaveActionPerformed(ActionEvent e) {
        if (txtName.getText().isEmpty() || txtVorname.getText().isEmpty() || (txtPW.isEnabled() && txtPW.getText().isEmpty()) || txtUID.getText().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.emptyentry"));
            return;
        }

        if (!txtEMail.getText().isEmpty() && !EMailSystem.isValidEmailAddress(txtEMail.getText().trim())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("opde.users.dlgusers.wrongemail")));
            return;
        }

        if (txtUID.isEnabled()) {
            EntityManager em = OPDE.createEM();
            OPUsers check4user = em.find(OPUsers.class, txtUID.getText().trim());
            em.close();
            if (check4user != null) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("opde.users.dlgusers.uidtaken")));
                return;
            }
        }

        user.setEMail(txtEMail.getText().isEmpty() ? null : txtEMail.getText().trim());
        user.setVorname(txtVorname.getText().trim());
        user.setNachname(txtName.getText().trim());

        if (txtUID.isEnabled()) {
            user.setMd5pw(SYSTools.hashword(txtPW.getText()));
            user.setUID(txtUID.getText().trim());
            SYSTools.printpw(txtPW.getText().trim(), user);
        }

        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel4 = new JPanel();
        lblUID = new JLabel();
        txtUID = new JTextField();
        lblFirstname = new JLabel();
        txtName = new JTextField();
        txtEMail = new JTextField();
        lblEmail = new JLabel();
        txtVorname = new JTextField();
        lblName = new JLabel();
        lblPW = new JLabel();
        txtPW = new JTextField();
        jPanel3 = new JPanel();
        btnCancel = new JButton();
        btnSave = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== jPanel4 ========
        {
            jPanel4.setLayout(new FormLayout(
                "13dlu, $lcgap, default, $lcgap, 134dlu:grow, $lcgap, 13dlu",
                "13dlu, 4*($lgap, fill:18dlu), $lgap, 18dlu, 9dlu, default, $lgap, 13dlu"));

            //---- lblUID ----
            lblUID.setText("UKennung");
            lblUID.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(lblUID, CC.xy(3, 3));

            //---- txtUID ----
            txtUID.setColumns(10);
            txtUID.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(txtUID, CC.xywh(5, 3, 2, 1));

            //---- lblFirstname ----
            lblFirstname.setText("Vorname");
            lblFirstname.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(lblFirstname, CC.xy(3, 5));

            //---- txtName ----
            txtName.setDragEnabled(false);
            txtName.setFont(new Font("Arial", Font.PLAIN, 14));
            txtName.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtNameFocusLost(e);
                }
            });
            jPanel4.add(txtName, CC.xywh(5, 7, 2, 1));

            //---- txtEMail ----
            txtEMail.setDragEnabled(false);
            txtEMail.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(txtEMail, CC.xy(5, 9));

            //---- lblEmail ----
            lblEmail.setText("E-Mail");
            lblEmail.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(lblEmail, CC.xy(3, 9));

            //---- txtVorname ----
            txtVorname.setDragEnabled(false);
            txtVorname.setFont(new Font("Arial", Font.PLAIN, 14));
            txtVorname.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtVornameFocusLost(e);
                }
            });
            jPanel4.add(txtVorname, CC.xywh(5, 5, 2, 1));

            //---- lblName ----
            lblName.setText("Nachname");
            lblName.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(lblName, CC.xy(3, 7));

            //---- lblPW ----
            lblPW.setText("Passwort");
            lblPW.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(lblPW, CC.xy(3, 11));

            //---- txtPW ----
            txtPW.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(txtPW, CC.xywh(5, 11, 2, 1, CC.DEFAULT, CC.FILL));

            //======== jPanel3 ========
            {
                jPanel3.setLayout(new BoxLayout(jPanel3, BoxLayout.X_AXIS));

                //---- btnCancel ----
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                btnCancel.setToolTipText("Abbrechen");
                btnCancel.addActionListener(e -> btnCancelActionPerformed(e));
                jPanel3.add(btnCancel);

                //---- btnSave ----
                btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnSave.setToolTipText("Sichern");
                btnSave.addActionListener(e -> btnSaveActionPerformed(e));
                jPanel3.add(btnSave);
            }
            jPanel4.add(jPanel3, CC.xywh(5, 13, 2, 1, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(jPanel4);
        setSize(510, 285);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel4;
    private JLabel lblUID;
    private JTextField txtUID;
    private JLabel lblFirstname;
    private JTextField txtName;
    private JTextField txtEMail;
    private JLabel lblEmail;
    private JTextField txtVorname;
    private JLabel lblName;
    private JLabel lblPW;
    private JTextField txtPW;
    private JPanel jPanel3;
    private JButton btnCancel;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
