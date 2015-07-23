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
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Torsten Löhr
 */
public class FrmDBConnection extends JFrame {

    private boolean db_parameters_complete;
    private boolean db_server_pingable;
    private boolean db_server_connected;
    private boolean db_catalog_exists;
    private boolean db_version_ok;
    private boolean db_password_readable;
    private boolean db_password_credentials_correct;


    private DatabaseConnectionBean dbcb;
    private String clearpassword;
    private Logger logger;
    private String pingResult = "";

    private ArrayList<String> stuffThatAnnoysMe;

    public FrmDBConnection() {
        logger = Logger.getLogger(getClass());
        stuffThatAnnoysMe = new ArrayList<>();
        dbcb = new DatabaseConnectionBean(OPDE.getLocalProps());
        initComponents();
        initFrame();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        analyzeSituation();
    }


    public boolean isDatabaseOK() {
        return db_version_ok; // if thats true, EVERYTHING is true;
    }

    /**
     * this method checks the availability of the database connection in stages.
     * it sets the 7 boolean in order to describe
     *
     */
    private void analyzeSituation() {

        db_parameters_complete = false;
        db_server_pingable = false;
        db_server_connected = false;
        db_catalog_exists = false;
        db_version_ok = false;
        db_password_readable = false;
        db_password_credentials_correct = false;

        // 1. All parameters entered ?
        Validator validator = OPDE.getValidatorFactory().getValidator();
        Set<ConstraintViolation<DatabaseConnectionBean>> constraintViolations = validator.validate(dbcb);
        db_parameters_complete = constraintViolations.isEmpty();
        constraintViolations.forEach(new Consumer<ConstraintViolation<DatabaseConnectionBean>>() {
            @Override
            public void accept(ConstraintViolation<DatabaseConnectionBean> databaseConnectionBeanConstraintViolation) {
                stuffThatAnnoysMe.add(databaseConnectionBeanConstraintViolation.getPropertyPath().toString() + ": " + databaseConnectionBeanConstraintViolation.getMessage());
            }
        });
        if (!db_parameters_complete) return;

        db_password_readable = true; // we would have not made it here otherwise

        try {
            pingResult = SYSTools.socketping(dbcb.getHost(), dbcb.getPort().toString());
            db_server_pingable = true;
        } catch (IOException e) {
            pingResult = e.getMessage();
            stuffThatAnnoysMe.add(pingResult);
        }
        if (!db_server_pingable) return;

        try {
            Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(dbcb.getHost(), dbcb.getPort().toString(), null), dbcb.getUser(), dbcb.getPassword());
            db_password_credentials_correct = true;
            db_server_connected = true;

            jdbcConnection.setCatalog(dbcb.getCatalog());
            db_catalog_exists = true;

            db_version_ok = OPDE.getAppInfo().getDbversion() == EntityTools.getDatabaseSchemaVersion(jdbcConnection);
            jdbcConnection.close();
        } catch (SQLException e) {
            if (e.getMessage().startsWith("Access denied for user")) {
                db_password_credentials_correct = false;
            }
            stuffThatAnnoysMe.add(e.getMessage());
        }


    }


//    public static boolean isDatabaseConnectionOK() {
//
//
////        if (password == null) return false;
//        boolean result = true;
//
//        try {
//            Connection jdbcConnection = DriverManager.getConnection(url, user, password);
//            result = OPDE.getAppInfo().getDbversion() == getDatabaseSchemaVersion(jdbcConnection);
//            jdbcConnection.close();
//        } catch (SQLException sqe) {
//            result = false;
//        }
//
//        return result;
//    }

    private void initFrame() {
        lblServer.setText(SYSTools.xx("opde.settings.db.host"));
        lblPort.setText(SYSTools.xx("opde.settings.db.port"));
        lblCat.setText(SYSTools.xx("opde.settings.db.catalog"));
        lblUser.setText(SYSTools.xx("opde.settings.db.user"));
        lblPassword.setText(SYSTools.xx("opde.settings.db.password"));

//        txtServer
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
        pnlDB = new JPanel();
        lblServer = new JLabel();
        txtServer = new JTextField();
        lblPort = new JLabel();
        txtPort = new JTextField();
        lblCat = new JLabel();
        txtCatalog = new JTextField();
        lblUser = new JLabel();
        txtUser = new JTextField();
        lblPassword = new JLabel();
        txtPW = new JPasswordField();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== pnlDB ========
        {
            pnlDB.setBackground(new Color(238, 238, 238));
            pnlDB.setLayout(new FormLayout(
                "default, $ugap, default:grow",
                "4*(default, $lgap), default"));

            //---- lblServer ----
            lblServer.setText("Server");
            lblServer.setHorizontalAlignment(SwingConstants.RIGHT);
            lblServer.setFont(new Font("Arial", Font.PLAIN, 16));
            pnlDB.add(lblServer, CC.xy(1, 1));

            //---- txtServer ----
            txtServer.setFont(new Font("Arial", Font.PLAIN, 16));
            pnlDB.add(txtServer, CC.xy(3, 1));

            //---- lblPort ----
            lblPort.setText("Port");
            lblPort.setHorizontalAlignment(SwingConstants.RIGHT);
            lblPort.setFont(new Font("Arial", Font.PLAIN, 16));
            pnlDB.add(lblPort, CC.xy(1, 3));

            //---- txtPort ----
            txtPort.setText("3306");
            txtPort.setFont(new Font("Arial", Font.PLAIN, 16));
            pnlDB.add(txtPort, CC.xy(3, 3));

            //---- lblCat ----
            lblCat.setText("Katalog");
            lblCat.setHorizontalAlignment(SwingConstants.RIGHT);
            lblCat.setFont(new Font("Arial", Font.PLAIN, 16));
            pnlDB.add(lblCat, CC.xy(1, 5));

            //---- txtCatalog ----
            txtCatalog.setText("opde");
            txtCatalog.setFont(new Font("Arial", Font.PLAIN, 16));
            pnlDB.add(txtCatalog, CC.xy(3, 5));

            //---- lblUser ----
            lblUser.setText("Benutzer");
            lblUser.setHorizontalAlignment(SwingConstants.RIGHT);
            lblUser.setFont(new Font("Arial", Font.PLAIN, 16));
            pnlDB.add(lblUser, CC.xy(1, 7));

            //---- txtUser ----
            txtUser.setFont(new Font("Arial", Font.PLAIN, 16));
            pnlDB.add(txtUser, CC.xy(3, 7));

            //---- lblPassword ----
            lblPassword.setText("Passwort");
            lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
            lblPassword.setFont(new Font("Arial", Font.PLAIN, 16));
            pnlDB.add(lblPassword, CC.xy(1, 9));

            //---- txtPW ----
            txtPW.setFont(new Font("Arial", Font.PLAIN, 16));
            pnlDB.add(txtPW, CC.xy(3, 9));
        }
        contentPane.add(pnlDB);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel pnlDB;
    private JLabel lblServer;
    private JTextField txtServer;
    private JLabel lblPort;
    private JTextField txtPort;
    private JLabel lblCat;
    private JTextField txtCatalog;
    private JLabel lblUser;
    private JTextField txtUser;
    private JLabel lblPassword;
    private JPasswordField txtPW;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
