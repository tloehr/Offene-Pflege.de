/*
 * Created by JFormDesigner on Tue Aug 28 16:03:54 CEST 2012
 */

package op.users;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.files.SYSFilesTools;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.MyJDialog;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.Random;

/**
 * @author Torsten Löhr
 */
public class DlgUser extends MyJDialog {
    public static final String internalClassID = "opde.users.dlgusers";
    private Users user;
    private Closure callback;

    public DlgUser(Users user, Closure callback) {
        super();
        this.user = user;
        this.callback = callback;
        initComponents();
        pack();
        setVisible(true);
    }

    private void txtNameFocusLost(FocusEvent e) {
        if (!txtName.getText().isEmpty() && !txtVorname.getText().isEmpty()) {
            txtPW.setText(generatePassword(txtVorname.getText(), txtName.getText()));
        }
    }

    private void txtVornameFocusLost(FocusEvent e) {
        if (!txtName.getText().isEmpty() && !txtVorname.getText().isEmpty()) {
            txtPW.setText(generatePassword(txtVorname.getText(), txtName.getText()));
        }
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        user = null;
        dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (user != null){
            print(txtPW.getText().trim());
        }
        callback.execute(user);
    }

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

        html = SYSTools.replace(html, "<opde-user-fullname/>", user.getFullname());
        html = SYSTools.replace(html, "<opde-user-userid/>", user.getUID());
        html = SYSTools.replace(html, "<opde-user-pw/>", password);
        html = SYSTools.htmlUmlautConversion(html);


        SYSFilesTools.print(html, true);
    }

    private void btnSaveActionPerformed(ActionEvent e) {
        if (txtName.getText().isEmpty() || txtVorname.getText().isEmpty() || txtPW.getText().isEmpty() || txtUID.getText().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.emptyentry"));
            return;
        }

        if (!txtEMail.getText().isEmpty() && !SYSTools.isValidEMail(txtEMail.getText().trim())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".wrongemail")));
            return;
        }

        EntityManager em = OPDE.createEM();
        Users check4user = em.find(Users.class, txtUID.getText().trim());
        if (check4user != null) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".uidtaken")));
            return;
        }

        user.setUID(txtUID.getText().trim());
        user.setEMail(txtEMail.getText().isEmpty() ? null : txtEMail.getText().trim());
        user.setVorname(txtVorname.getText().trim());
        user.setNachname(txtName.getText().trim());
        user.setMd5pw(SYSTools.hashword(txtPW.getText()));
        dispose();
    }

    private String generatePassword(String firstname, String lastname) {
        Random generator = new Random(System.currentTimeMillis());
        return lastname.substring(0, 1).toLowerCase() + firstname.substring(0, 1).toLowerCase() + SYSTools.padL(Integer.toString(generator.nextInt(9999)), 4, "0");
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
                "14dlu, $lcgap, default, $lcgap, default:grow, $lcgap, 14dlu",
                "14dlu, 4*($lgap, fill:default), 2*($lgap, default), $lgap, 14dlu"));

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
            jPanel4.add(txtEMail, CC.xywh(5, 9, 2, 1));

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
            jPanel4.add(txtPW, CC.xywh(5, 11, 2, 1));

            //======== jPanel3 ========
            {
                jPanel3.setLayout(new BoxLayout(jPanel3, BoxLayout.X_AXIS));

                //---- btnCancel ----
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                btnCancel.setToolTipText("Abbrechen");
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancelActionPerformed(e);
                    }
                });
                jPanel3.add(btnCancel);

                //---- btnSave ----
                btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnSave.setToolTipText("Sichern");
                btnSave.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnSaveActionPerformed(e);
                    }
                });
                jPanel3.add(btnSave);
            }
            jPanel4.add(jPanel3, CC.xywh(5, 13, 2, 1, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(jPanel4);
        setSize(400, 240);
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
