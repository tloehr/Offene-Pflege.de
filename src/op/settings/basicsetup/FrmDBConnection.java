/*
 * Created by JFormDesigner on Sat Jul 18 14:06:06 CEST 2015
 */

package op.settings.basicsetup;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.swing.JideTabbedPane;
import entity.EntityTools;
import op.OPDE;
import op.settings.databeans.DatabaseConnectionBean;
import op.tools.SYSTools;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Torsten Löhr
 */
public class FrmDBConnection extends JFrame {

    boolean db_reachable = false;
    boolean db_data_present = false;
    boolean db_version_ok = false;
    boolean db_password_readable = false;
    boolean db_password_credentials_correct = false;

    DatabaseConnectionBean dbcb;
    private String clearpassword;

    public FrmDBConnection() {
        this.clearpassword = clearpassword;

        dbcb = new DatabaseConnectionBean(OPDE.getLocalProps());
        initComponents();
        initFrame();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        getReasonToBeHere();
    }


    public boolean isDatabaseOK(){
        return false;
    }

    private void getReasonToBeHere() {
        try {
            Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(dbcb.getHost(), dbcb.getPort(), dbcb.getCatalog()), dbcb.toProperties(new Properties()));
            db_password_readable = true;
            db_password_credentials_correct = true;
            db_reachable = true;
            db_version_ok = OPDE.getAppInfo().getDbversion() == EntityTools.getNeededDBVersion(jdbcConnection);
        } catch (SQLException e) {

            if (e.getMessage().startsWith("Access denied for user")){
                db_password_credentials_correct = false;
            }

            db_reachable = false;
            e.printStackTrace();
        } catch (BadPaddingException e) {
            db_password_readable = false;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void initFrame() {


        lblServer.setText(SYSTools.xx("opde.settings.db.host"));
        lblPort.setText(SYSTools.xx("opde.settings.db.port"));
        lblCat.setText(SYSTools.xx("opde.settings.db.catalog"));
        lblUser.setText(SYSTools.xx("opde.settings.db.user"));
        lblPassword.setText(SYSTools.xx("opde.settings.db.password"));
    }

    private void btnCheckDBActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void btnLockServerItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void btnFixDBActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void btnCreateDBActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        pnlMain = new JideTabbedPane();
        pnlWelcome = new JPanel();
        scrollPane2 = new JScrollPane();
        txtDBTest2 = new JTextPane();
        pnlDB = new JPanel();
        lblCommon = new JLabel();
        lblServer = new JLabel();
        txtServer = new JTextField();
        lblPort = new JLabel();
        txtPort = new JTextField();
        lblCat = new JLabel();
        txtCatalog = new JTextField();
        lblUser = new JLabel();
        txtUser = new JTextField();
        btn1 = new JButton();
        btn2 = new JButton();
        btn3 = new JButton();
        btn4 = new JButton();
        scrollPane1 = new JScrollPane();
        txtDBTest = new JTextPane();
        pbSQL = new JProgressBar();
        lblPassword = new JLabel();
        txtPW = new JPasswordField();
        lblInstall = new JLabel();
        lblUser2 = new JLabel();
        txtUser2 = new JTextField();
        lblPassword2 = new JLabel();
        txtPW2 = new JPasswordField();
        lblOperations = new JLabel();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== pnlMain ========
        {
            pnlMain.setTabPlacement(SwingConstants.LEFT);

            //======== pnlWelcome ========
            {
                pnlWelcome.setLayout(new BoxLayout(pnlWelcome, BoxLayout.X_AXIS));

                //======== scrollPane2 ========
                {

                    //---- txtDBTest2 ----
                    txtDBTest2.setBackground(UIManager.getColor("Button.background"));
                    txtDBTest2.setEditable(false);
                    txtDBTest2.setContentType("text/html");
                    scrollPane2.setViewportView(txtDBTest2);
                }
                pnlWelcome.add(scrollPane2);
            }
            pnlMain.addTab("text", pnlWelcome);

            //======== pnlDB ========
            {
                pnlDB.setBackground(new Color(238, 238, 238));
                pnlDB.setLayout(new FormLayout(
                        "default, $lcgap, default, $ugap, default:grow, $lcgap, pref, $lcgap, default",
                        "$ugap, 11*(default, $lgap), default, $rgap, 2*(default, $lgap), fill:default:grow, default"));

                //---- lblCommon ----
                lblCommon.setText("Allgemeine Datenbank Verbindungsinformationen");
                lblCommon.setFont(new Font("Arial", Font.BOLD, 22));
                lblCommon.setHorizontalAlignment(SwingConstants.CENTER);
                lblCommon.setForeground(new Color(51, 51, 255));
                pnlDB.add(lblCommon, CC.xywh(3, 2, 3, 1));

                //---- lblServer ----
                lblServer.setText("Server");
                lblServer.setHorizontalAlignment(SwingConstants.RIGHT);
                lblServer.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(lblServer, CC.xy(3, 4));

                //---- txtServer ----
                txtServer.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtServer, CC.xy(5, 4));

                //---- lblPort ----
                lblPort.setText("Port");
                lblPort.setHorizontalAlignment(SwingConstants.RIGHT);
                lblPort.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(lblPort, CC.xy(3, 6));

                //---- txtPort ----
                txtPort.setText("3306");
                txtPort.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtPort, CC.xy(5, 6));

                //---- lblCat ----
                lblCat.setText("Katalog");
                lblCat.setHorizontalAlignment(SwingConstants.RIGHT);
                lblCat.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(lblCat, CC.xy(3, 8));

                //---- txtCatalog ----
                txtCatalog.setText("opde");
                txtCatalog.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtCatalog, CC.xy(5, 8));

                //---- lblUser ----
                lblUser.setText("Benutzer");
                lblUser.setHorizontalAlignment(SwingConstants.RIGHT);
                lblUser.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(lblUser, CC.xy(3, 10));

                //---- txtUser ----
                txtUser.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtUser, CC.xy(5, 10));

                //---- btn1 ----
                btn1.setText("1. Verbindung pr\u00fcfen");
                btn1.setIcon(null);
                btn1.setFont(new Font("Arial", Font.PLAIN, 22));
                btn1.setHorizontalAlignment(SwingConstants.LEFT);
                btn1.addActionListener(e -> btnCheckDBActionPerformed(e));
                pnlDB.add(btn1, CC.xy(3, 22));

                //---- btn2 ----
                btn2.setText("2. Datenbank sperren");
                btn2.setEnabled(false);
                btn2.setSelectedIcon(null);
                btn2.setIcon(null);
                btn2.setFont(new Font("Arial", Font.PLAIN, 22));
                btn2.setHorizontalAlignment(SwingConstants.LEFT);
                btn2.addItemListener(e -> btnLockServerItemStateChanged(e));
                pnlDB.add(btn2, CC.xy(3, 24));

                //---- btn3 ----
                btn3.setText("3. Schema aktualisieren");
                btn3.setIcon(null);
                btn3.setEnabled(false);
                btn3.setFont(new Font("Arial", Font.PLAIN, 22));
                btn3.setHorizontalAlignment(SwingConstants.LEFT);
                btn3.addActionListener(e -> btnFixDBActionPerformed(e));
                pnlDB.add(btn3, CC.xy(3, 26));

                //---- btn4 ----
                btn4.setText("4. Datenbank freigeben");
                btn4.setIcon(null);
                btn4.setEnabled(false);
                btn4.setFont(new Font("Arial", Font.PLAIN, 22));
                btn4.setHorizontalAlignment(SwingConstants.LEFT);
                btn4.addActionListener(e -> btnCreateDBActionPerformed(e));
                pnlDB.add(btn4, CC.xy(3, 28));

                //======== scrollPane1 ========
                {

                    //---- txtDBTest ----
                    txtDBTest.setBackground(UIManager.getColor("Button.background"));
                    txtDBTest.setEditable(false);
                    txtDBTest.setContentType("text/html");
                    scrollPane1.setViewportView(txtDBTest);
                }
                pnlDB.add(scrollPane1, new CellConstraints(5, 22, 2, 9, CC.DEFAULT, CC.DEFAULT, new Insets(5, 5, 5, 5)));

                //---- pbSQL ----
                pbSQL.setOrientation(SwingConstants.VERTICAL);
                pnlDB.add(pbSQL, CC.xywh(7, 4, 1, 27));

                //---- lblPassword ----
                lblPassword.setText("Passwort");
                lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
                lblPassword.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(lblPassword, CC.xy(3, 12));

                //---- txtPW ----
                txtPW.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtPW, CC.xy(5, 12));

                //---- lblInstall ----
                lblInstall.setText("Nur bei Neu-Installation n\u00f6tig");
                lblInstall.setFont(new Font("Arial", Font.BOLD, 22));
                lblInstall.setHorizontalAlignment(SwingConstants.CENTER);
                lblInstall.setForeground(Color.red);
                pnlDB.add(lblInstall, CC.xywh(3, 14, 3, 1));

                //---- lblUser2 ----
                lblUser2.setText("Benutzer");
                lblUser2.setHorizontalAlignment(SwingConstants.RIGHT);
                lblUser2.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(lblUser2, CC.xy(3, 16));

                //---- txtUser2 ----
                txtUser2.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtUser2, CC.xy(5, 16));

                //---- lblPassword2 ----
                lblPassword2.setText("Passwort");
                lblPassword2.setHorizontalAlignment(SwingConstants.RIGHT);
                lblPassword2.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(lblPassword2, CC.xy(3, 18));

                //---- txtPW2 ----
                txtPW2.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtPW2, CC.xy(5, 18));

                //---- lblOperations ----
                lblOperations.setText("Datenbank Operationen");
                lblOperations.setFont(new Font("Arial", Font.BOLD, 22));
                lblOperations.setHorizontalAlignment(SwingConstants.CENTER);
                lblOperations.setForeground(new Color(0, 102, 102));
                pnlDB.add(lblOperations, CC.xywh(3, 20, 3, 1));
            }
            pnlMain.addTab("pnlDB", pnlDB);
        }
        contentPane.add(pnlMain);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JideTabbedPane pnlMain;
    private JPanel pnlWelcome;
    private JScrollPane scrollPane2;
    private JTextPane txtDBTest2;
    private JPanel pnlDB;
    private JLabel lblCommon;
    private JLabel lblServer;
    private JTextField txtServer;
    private JLabel lblPort;
    private JTextField txtPort;
    private JLabel lblCat;
    private JTextField txtCatalog;
    private JLabel lblUser;
    private JTextField txtUser;
    private JButton btn1;
    private JButton btn2;
    private JButton btn3;
    private JButton btn4;
    private JScrollPane scrollPane1;
    private JTextPane txtDBTest;
    private JProgressBar pbSQL;
    private JLabel lblPassword;
    private JPasswordField txtPW;
    private JLabel lblInstall;
    private JLabel lblUser2;
    private JTextField txtUser2;
    private JLabel lblPassword2;
    private JPasswordField txtPW2;
    private JLabel lblOperations;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
