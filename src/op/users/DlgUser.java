/*
 * Created by JFormDesigner on Tue Aug 28 16:03:54 CEST 2012
 */

package op.users;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideBoxLayout;
import entity.roster.Rosters;
import entity.roster.UserContract;
import entity.roster.UserContracts;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.DefaultCPTitle;
import op.tools.MyJDialog;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyVetoException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Torsten Löhr
 */
public class DlgUser extends MyJDialog {
    public static final String internalClassID = "opde.users.dlgusers";
    private Users user;
    private Closure callback;
    private Map<String, CollapsiblePane> cpMap;
    UserContracts userContracts;

    public DlgUser(Users user, Closure callback) {
        super(false);
        this.user = user;
        this.callback = callback;
        initComponents();
        initDialog();
        setVisible(true);
    }

    private void initDialog() {
        cpMap = Collections.synchronizedMap(new HashMap<String, CollapsiblePane>());

        userContracts = UsersTools.getContracts(user);
        if (userContracts != null) {
            Collections.sort(userContracts.getListContracts());
            for (UserContract userContract : userContracts.getListContracts()) {
                createCP4(userContract);
            }
            cpsContracts.removeAll();
            cpsContracts.setLayout(new JideBoxLayout(cpsContracts, JideBoxLayout.Y_AXIS));

            synchronized (cpMap) {
                for (UserContract userContract : userContracts.getListContracts()) {
                    final String key = userContract.hashCode() + ".contract";
                    cpsContracts.add(cpMap.get(key));
                }
            }
            cpsContracts.addExpansion();
        }

        lblFirstname.setText(OPDE.lang.getString("misc.msg.firstname"));
        lblName.setText(OPDE.lang.getString("misc.msg.name"));
        lblPW.setText(OPDE.lang.getString("misc.msg.password"));
        lblUID.setText(OPDE.lang.getString("misc.msg.uid"));
        lblEmail.setText(OPDE.lang.getString("misc.msg.email"));

        txtName.setText(user.getName());
        txtEMail.setText(user.getEMail());
        txtVorname.setText(user.getVorname());
        txtUID.setText(user.getUID());

        txtPW.setEnabled(user.getUID() == null);
        txtUID.setEnabled(user.getUID() == null);
    }

    private void txtNameFocusLost(FocusEvent e) {
        if (txtPW.isEnabled() && txtName.getText().isEmpty() && !txtVorname.getText().isEmpty()) {
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

        if (!txtEMail.getText().isEmpty() && !SYSTools.isValidEMail(txtEMail.getText().trim())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".wrongemail")));
            return;
        }

        if (txtUID.isEnabled()) {
            EntityManager em = OPDE.createEM();
            Users check4user = em.find(Users.class, txtUID.getText().trim());
            em.close();
            if (check4user != null) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".uidtaken")));
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

    private CollapsiblePane createCP4(UserContract contract) {
        final String key = contract.hashCode() + ".contract";
        synchronized (cpMap) {
            if (!cpMap.containsKey(key)) {
                cpMap.put(key, new CollapsiblePane());
                try {
                    cpMap.get(key).setCollapsed(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        }
        final CollapsiblePane cpRoster = cpMap.get(key);

        String title = "<html>" + contract.getPeriodAsHTML() + "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpRoster.setCollapsed(!cpRoster.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

        cpRoster.setTitleLabelComponent(cptitle.getMain());
        cpRoster.setSlidingDirection(SwingConstants.SOUTH);

        cpRoster.setBackground(Color.WHITE);
        cpRoster.setOpaque(false);
        cpRoster.setHorizontalAlignment(SwingConstants.LEADING);

        cpRoster.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
//                cpRoster.setContentPane(createContentPane4(roster));
            }
        });

        if (!cpRoster.isCollapsed()) {
//            cpRoster.setContentPane(createContentPane4(roster));
        }


        return cpRoster;
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel4 = new JPanel();
        lblUID = new JLabel();
        txtUID = new JTextField();
        scrlContracts = new JScrollPane();
        cpsContracts = new CollapsiblePanes();
        lblFirstname = new JLabel();
        txtVorname = new JTextField();
        lblEmail = new JLabel();
        lblName = new JLabel();
        txtName = new JTextField();
        txtEMail = new JTextField();
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
                    "14dlu, $lcgap, default, $lcgap, default:grow, $ugap, 14dlu:grow, $lcgap, default",
                    "14dlu, 4*($lgap, fill:default), $lgap, default, 9dlu, default, $lgap, 14dlu"));

            //---- lblUID ----
            lblUID.setText("UKennung");
            lblUID.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(lblUID, CC.xy(3, 3));

            //---- txtUID ----
            txtUID.setColumns(10);
            txtUID.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(txtUID, CC.xy(5, 3));

            //======== scrlContracts ========
            {
                scrlContracts.setViewportView(cpsContracts);
            }
            jPanel4.add(scrlContracts, CC.xywh(7, 3, 1, 9));

            //---- lblFirstname ----
            lblFirstname.setText("Vorname");
            lblFirstname.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(lblFirstname, CC.xy(3, 5));

            //---- txtVorname ----
            txtVorname.setDragEnabled(false);
            txtVorname.setFont(new Font("Arial", Font.PLAIN, 14));
            txtVorname.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtVornameFocusLost(e);
                }
            });
            jPanel4.add(txtVorname, CC.xy(5, 5));

            //---- lblEmail ----
            lblEmail.setText("E-Mail");
            lblEmail.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(lblEmail, CC.xy(3, 9));

            //---- lblName ----
            lblName.setText("Nachname");
            lblName.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(lblName, CC.xy(3, 7));

            //---- txtName ----
            txtName.setDragEnabled(false);
            txtName.setFont(new Font("Arial", Font.PLAIN, 14));
            txtName.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtNameFocusLost(e);
                }
            });
            jPanel4.add(txtName, CC.xy(5, 7));

            //---- txtEMail ----
            txtEMail.setDragEnabled(false);
            txtEMail.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(txtEMail, CC.xy(5, 9));

            //---- lblPW ----
            lblPW.setText("Passwort");
            lblPW.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(lblPW, CC.xy(3, 11));

            //---- txtPW ----
            txtPW.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(txtPW, CC.xy(5, 11));

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
            jPanel4.add(jPanel3, CC.xywh(7, 13, 2, 1, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(jPanel4);
        setSize(605, 295);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel4;
    private JLabel lblUID;
    private JTextField txtUID;
    private JScrollPane scrlContracts;
    private CollapsiblePanes cpsContracts;
    private JLabel lblFirstname;
    private JTextField txtVorname;
    private JLabel lblEmail;
    private JLabel lblName;
    private JTextField txtName;
    private JTextField txtEMail;
    private JLabel lblPW;
    private JTextField txtPW;
    private JPanel jPanel3;
    private JButton btnCancel;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
