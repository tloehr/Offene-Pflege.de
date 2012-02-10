/*
 * Created by JFormDesigner on Thu Feb 09 09:47:38 CET 2012
 */

package op.system;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.system.SYSLoginTools;
import op.OPDE;
import op.tools.CleanablePanel;
import op.tools.DlgHTML;
import op.tools.SYSTools;
import org.jdesktop.swingx.JXHyperlink;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Torsten Löhr
 */
public class PnlLogin extends CleanablePanel {

    private Thread thread = null;

    public PnlLogin() {
        initComponents();
        initPanel();
        //TODO: Hier muss noch ein CallBack Code rein, der aufrufen wird, wenn die Anmeldung erfolgreich war.
    }

    private void btnAboutActionPerformed(ActionEvent e) {
//        new DlgHTML(this);
    }

    private void txtPasswordActionPerformed(ActionEvent e) {
        btnLogin.doClick();
    }

    private void txtPasswordFocusGained(FocusEvent e) {
        txtPassword.selectAll();
    }

    private void txtUsernameActionPerformed(ActionEvent e) {
        txtPassword.requestFocus();
    }

    private void txtUsernameFocusGained(FocusEvent e) {
        txtUsername.selectAll();
    }

    private void btnLoginActionPerformed(ActionEvent e) {
        String username = txtUsername.getText().trim();
//            char[] password = txtPassword.getPassword();

        try {

            OPDE.initDB();

            // Hier wird erst geprüft, ob Username und Passwort stimmen.
            registerLogin();
            if (OPDE.getLogin() == null) {
                JOptionPane.showMessageDialog(this, "Benutzername oder Passwort falsch.", "Anmeldefehler", JOptionPane.INFORMATION_MESSAGE);
                OPDE.info("Falsches Passwort eingegeben.");
            } else {
                OPDE.newOCSec();
                OPDE.initProps();

                OPDE.info("Anmeldung erfolgt: UKennung: " + username);
                OPDE.info("LoginID: " + OPDE.getLogin().getUser().getUKennung());
            }

        } catch (SQLException se) {
            OPDE.fatal(se);
            System.exit(se.getErrorCode());
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        btnAbout = new JButton();
        panel1 = new JPanel();
        linkOPDE = new JXHyperlink();
        jLabel4 = new JLabel();
        label1 = new JLabel();
        txtUsername = new JTextField();
        label2 = new JLabel();
        txtPassword = new JPasswordField();
        btnLogin = new JButton();

        //======== this ========
        setLayout(new FormLayout(
                "default:grow, 2*($lcgap, default), $lcgap, default:grow",
                "default:grow, $lgap, default, $lgap, $ugap, 3*($lgap, default), $lgap, default:grow"));

        //---- btnAbout ----
        btnAbout.setIcon(new ImageIcon(getClass().getResource("/artwork/animation/opde-58.png")));
        btnAbout.setToolTipText("\u00dcber Offene-Pflege.de...");
        btnAbout.setOpaque(true);
        btnAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAboutActionPerformed(e);
            }
        });
        add(btnAbout, CC.xy(3, 3));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                    "default",
                    "default, $lgap, default"));

            //---- linkOPDE ----
            linkOPDE.setText("Offene-Pflege.de");
            linkOPDE.setFont(new Font("Arial", Font.PLAIN, 24));
            linkOPDE.setHorizontalAlignment(SwingConstants.CENTER);
            panel1.add(linkOPDE, CC.xy(1, 1));

            //---- jLabel4 ----
            jLabel4.setFont(new Font("Arial", Font.PLAIN, 13));
            jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
            jLabel4.setText("So viel Pflege wie m\u00f6glich, so viel Technik wie n\u00f6tig. ");
            panel1.add(jLabel4, CC.xy(1, 3));
        }
        add(panel1, CC.xy(5, 3));

        //---- label1 ----
        label1.setText("Benutzername");
        label1.setFont(new Font("Arial", Font.PLAIN, 16));
        add(label1, CC.xy(3, 7));

        //---- txtUsername ----
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 16));
        txtUsername.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtUsernameActionPerformed(e);
            }
        });
        txtUsername.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtUsernameFocusGained(e);
            }
        });
        add(txtUsername, CC.xy(5, 7));

        //---- label2 ----
        label2.setText("Passwort");
        label2.setFont(new Font("Arial", Font.PLAIN, 16));
        add(label2, CC.xy(3, 9));

        //---- txtPassword ----
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtPasswordActionPerformed(e);
            }
        });
        txtPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtPasswordFocusGained(e);
            }
        });
        add(txtPassword, CC.xy(5, 9));

        //---- btnLogin ----
        btnLogin.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/apply.png")));
        btnLogin.setText("Anmelden");
        btnLogin.setActionCommand("btnLogin");
        btnLogin.setFont(new Font("Arial", Font.PLAIN, 16));
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLoginActionPerformed(e);
            }
        });
        add(btnLogin, CC.xy(5, 11, CC.RIGHT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void initPanel() {
        OPDE.setLogin(null);


//
//        String defaultlogin = ;
//        String defaultpw =;

//        if (OPDE.getLocalProps().containsKey("defaultlogin")) {
//
//        }
//        if (OPDE.getLocalProps().containsKey("defaultpw")) {
//            defaultpw = OPDE.getLocalProps().getProperty("defaultpw");
//        }
        txtUsername.setText(SYSTools.catchNull(OPDE.getLocalProps().getProperty("defaultlogin")));
        txtPassword.setText(SYSTools.catchNull(OPDE.getLocalProps().getProperty("defaultpw")));

        try {
            linkOPDE.setURI(new URI("http://www.offene-pflege.de"));
            linkOPDE.setText("Offene-Pflege.de");
        } catch (URISyntaxException ex) {
            OPDE.fatal(ex);
        }

        if (OPDE.isAnimation()) {
            animateLogo();
        } else {
            btnAbout.setIcon(new ImageIcon(getClass().getResource("/artwork/animation/opde-52.png")));
        }

        txtUsername.requestFocus();
    }


    private void animateLogo() {
        thread = new Thread() {
            public void run() {
                int maxIconsNum = 85;
                try {
                    int i = 0;
                    while (true) {
                        if (i == OPDE.getAnimationCache().size()) {
                            OPDE.getAnimationCache().add(new ImageIcon(getClass().getResource("/artwork/animation/opde-" + (i + 1) + ".png")));
                        }
                        btnAbout.setIcon(OPDE.getAnimationCache().get(i));

                        Thread.sleep(55);
                        if (i == maxIconsNum) {
                            i = 0;
                        } else {
                            i++;
                        }
                    }
                } catch (InterruptedException e) {
                    OPDE.debug(e);
                }
            }
        };
        thread.start();
    }

    private void registerLogin() {
        //long loginid;
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        OPDE.setLogin(SYSLoginTools.login(username, password));
        try {
            // Dann OCLogin bereinigen.
            String sqlCleanupSession = "UPDATE SYSLogin l INNER JOIN SYSHosts h ON l.HostID = h.HostID SET l.Logout=LPOL WHERE l.Logout='9999-12-31 23:59:59' AND DATE_ADD(h.LPOL,INTERVAL 3 MINUTE) <= now()";
            PreparedStatement stmtCleanupSession = OPDE.getDb().db.prepareStatement(sqlCleanupSession);
            stmtCleanupSession.executeUpdate();
        } catch (Exception se) {
            OPDE.fatal(se);
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JButton btnAbout;
    private JPanel panel1;
    private JXHyperlink linkOPDE;
    private JLabel jLabel4;
    private JLabel label1;
    private JTextField txtUsername;
    private JLabel label2;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @Override
    public void cleanup() {
        if (thread != null) {
            thread.interrupt();
        }
        SYSTools.unregisterListeners(this);
    }
}
