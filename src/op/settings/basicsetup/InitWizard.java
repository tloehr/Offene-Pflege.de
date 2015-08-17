package op.settings.basicsetup;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.combobox.FileChooserPanel;
import com.jidesoft.dialog.ButtonEvent;
import com.jidesoft.dialog.ButtonNames;
import com.jidesoft.dialog.PageEvent;
import com.jidesoft.dialog.PageList;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.wizard.AbstractWizardPage;
import com.jidesoft.wizard.CompletionWizardPage;
import com.jidesoft.wizard.DefaultWizardPage;
import com.jidesoft.wizard.WizardDialog;
import entity.EntityTools;
import entity.files.SYSFilesTools;
import entity.system.SYSPropsTools;
import gui.GUITools;
import gui.events.RelaxedDocumentListener;
import gui.interfaces.YesNoToggleButton;
import op.OPDE;
import op.system.AppInfo;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 09.07.12
 * Time: 10:05
 * To change this template use File | Settings | File Templates.
 */
public class InitWizard extends WizardDialog {

    // state analysis
    enum DB_VERSION {
        UNKNOWN, TOO_LOW, PERFECT, TOO_HIGH
    }


    private DB_VERSION db_version;
    //    private boolean db_parameters_complete;
    private boolean db_server_pingable;
    private boolean db_dbms_reachable;
    private boolean db_catalog_exists;
    //    private boolean db_password_readable;
    private boolean db_credentials_correct;


    private final int WAIT_SECONDS_BEFORE_UPDATE = 60;


    private String pingResult = "";


    private AbstractWizardPage pageWelcome;
    private AbstractWizardPage pageConnection;
    private AbstractWizardPage pageCreateDB;
    private AbstractWizardPage pageUpgradeDB;
    private AbstractWizardPage pageCompletion;

//    private String mysqldump;

    private ArrayList<String> summary;


    // this Map contains the current entries of the user during the lifetime of the wizard
    // after succesful completion the settings are copied over to the OPDE.localProperties and
    // eventually saved to opde.cfg
    private Properties jdbcProps;
    private WizardDialog thisWizard;

    public InitWizard() {
        super(new JFrame(), false);
        thisWizard = this;
        setResizable(true);
        summary = new ArrayList<>();
        jdbcProps = new Properties();
        jdbcProps.put(SYSPropsTools.KEY_MYSQLDUMP_EXEC, SYSTools.catchNull(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_MYSQLDUMP_EXEC), "/usr/local/mysql/bin/mysqldump"));
//        logger = Logger.getLogger(getClass());
//        dbcb = new DatabaseConnectionBean(OPDE.getLocalProps());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        DriverManager.setLoginTimeout(2);

//        analyzeSituation();

        // if there is nothing to do, DO NOTHING

        createWizard();

    }


    private void createWizard() {


        pageWelcome = new WelcomePage(SYSTools.xx("opde.initwizard.page1.title"), SYSTools.xx("opde.initwizard.page1.description"));
        pageConnection = new ConnectionPage(SYSTools.xx("opde.initwizard.page.connection.title"), SYSTools.xx("opde.initwizard.page.connection.description"));

        pageCreateDB = new CreateDBPage(SYSTools.xx("opde.initwizard.page.createdb.title"), SYSTools.xx("opde.initwizard.page.createdb.description"));
        pageUpgradeDB = new UpdateDB(SYSTools.xx("opde.initwizard.page.upgradedb.title"), SYSTools.xx("opde.initwizard.page.upgradedb.description"));
        pageCompletion = new CompletionPage(SYSTools.xx("opde.initwizard.page.summary.title"), SYSTools.xx("opde.initwizard.page.summary.description"));

        final PageList model;
        model = new PageList();
        model.append(pageWelcome);
        model.append(pageConnection);
        model.append(pageCreateDB);
        model.append(pageUpgradeDB);
        model.append(pageCompletion);

        pageCreateDB.setPageEnabled(false);

        setPageList(model);

        setFinishAction(new AbstractAction("Finish") {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setCancelAction(new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setBackAction(new AbstractAction("Back") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = model.getPageIndexByFullTitle(getCurrentPage().getFullTitle()) - 1;
                while (!model.getPage(index).isPageEnabled()) {
                    index--;
                }
                setCurrentPage(model.getPage(index).getFullTitle());
            }
        });


        ((JPanel) getContentPane()).setBorder(new LineBorder(Color.BLACK, 1));
        pack();
        setSize(new Dimension(1000, 650));
    }


    private class WelcomePage extends DefaultWizardPage {

        public WelcomePage(String title, String description) {
            super(title, description);

        }

        @Override
        protected void initContentPane() {
            super.initContentPane();

            JTextPane txt = new JTextPane();
            txt.setEditable(false);
            txt.setContentType("text/html");
            txt.setOpaque(false);

            txt.setText(SYSTools.toHTML("opde.initwizard.page1.welcome"));

            addComponent(txt, true);
            addSpace();
            addText(SYSTools.xx("opde.wizards.buttontext.letsgo"), SYSConst.ARIAL14);
        }

        @Override
        public void setupWizardButtons() {
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.BACK, SYSTools.xx("opde.wizards.buttontext.back"));
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.NEXT, SYSTools.xx("opde.wizards.buttontext.next"));
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.FINISH, SYSTools.xx("opde.wizards.buttontext.finish"));
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.CANCEL, SYSTools.xx("opde.wizards.buttontext.cancel"));

            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.CANCEL);
        }
    }


    private class ConnectionPage extends DefaultWizardPage {
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
        private JPasswordField txtPassword;
        private Logger logger = Logger.getLogger(getClass());

        JTextPane txtComment;

        private JButton btnTestParameters;
        private JButton btnCreateNewSchema;
        private JButton btnUpdateSchema;

        //        private JTextArea txtComments;
        private int port = 3306;

        public ConnectionPage(String title, String description) {
            super(title, description);
            // we consider the worst case first.
            db_version = DB_VERSION.UNKNOWN;
            db_server_pingable = false;
            db_dbms_reachable = false;
            db_catalog_exists = false;
            db_credentials_correct = false;

            addPageListener(pageEvent -> {
                if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
//                    jfxPanel.setScene(createScene());

//                    SwingUtilities.invokeLater(() -> {
//                        revalidate();
//                        repaint();
//                    });
//                    btnTestParameters.doClick();
//                    userListener.check(null);
//                    catalogListener.check(null);
                }
                if (pageEvent.getID() == PageEvent.PAGE_CLOSED) {
                    jdbcProps.put(SYSPropsTools.KEY_JDBC_HOST, txtServer.getText().trim());
                    jdbcProps.put(SYSPropsTools.KEY_JDBC_PORT, Integer.toString(port));
                    jdbcProps.put(SYSPropsTools.KEY_JDBC_USER, txtUser.getText().trim());
                    jdbcProps.put(SYSPropsTools.KEY_JDBC_PASSWORD, new String(txtPassword.getPassword()).trim());
                    jdbcProps.put(SYSPropsTools.KEY_JDBC_CATALOG, txtCatalog.getText().trim());
                }
            });

        }

        @Override
        public void setupWizardButtons() {
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.CANCEL);
        }


//        private Scene createScene() {
//
//            AnchorPane anchorPane = new AnchorPane();
//
//            txtArea = new TextArea();
//
//            txtArea.setFont(javafx.scene.text.Font.font("sans-serif", 14d));
//            txtArea.setEditable(false);
//
////            scrollPane = new ScrollPane(txtArea);
//            //Set Layout Constraint
//            AnchorPane.setTopAnchor(txtArea, 0.0);
//            AnchorPane.setBottomAnchor(txtArea, 0.0);
//            AnchorPane.setLeftAnchor(txtArea, 0.0);
//            AnchorPane.setRightAnchor(txtArea, 0.0);
//
//
//            anchorPane.getChildren().add(txtArea);
//
//            return new Scene(anchorPane, javafx.scene.paint.Color.ALICEBLUE);
//        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            pnlDB = new JPanel();

            RelaxedDocumentListener dl = new RelaxedDocumentListener(0, o -> {
                btnUpdateSchema.setEnabled(false);
                btnCreateNewSchema.setEnabled(false);
            });

            txtServer = new JTextField();
            txtPassword = new JPasswordField();
            txtPort = new JTextField();
            txtCatalog = new JTextField();
            txtUser = new JTextField();
            txtComment = new JTextPane();

            txtServer.getDocument().addDocumentListener(dl);
            txtPassword.getDocument().addDocumentListener(dl);
            txtPort.getDocument().addDocumentListener(dl);
            txtCatalog.getDocument().addDocumentListener(dl);
            txtUser.getDocument().addDocumentListener(dl);
            txtComment.getDocument().addDocumentListener(dl);


            lblPassword = new JLabel();
            lblUser = new JLabel();
            lblServer = new JLabel();
            lblPort = new JLabel();
            lblCat = new JLabel();

            btnTestParameters = new JButton(SYSTools.xx("opde.initwizard.page.connection.testing"), SYSConst.icon48dbstatus);
            btnTestParameters.addActionListener(e1 -> testParameters());
            btnCreateNewSchema = new JButton(SYSTools.xx("opde.initwizard.page.update.createdb"), SYSConst.icon48dbcreate);
            btnCreateNewSchema.addActionListener(e1 -> setCurrentPage(SYSTools.xx("opde.initwizard.page.createdb.title")));
            btnCreateNewSchema.setEnabled(false);
            btnCreateNewSchema.setToolTipText(SYSTools.xx("opde.initwizard.page.connection.testfirst"));
            btnUpdateSchema = new JButton(SYSTools.xx("opde.initwizard.page.update.updatedb"), SYSConst.icon48dbupdate);
            btnUpdateSchema.addActionListener(e1 -> setCurrentPage(SYSTools.xx("opde.initwizard.page.upgradedb.title")));
            btnUpdateSchema.setEnabled(false);
            btnUpdateSchema.setToolTipText(SYSTools.xx("opde.initwizard.page.connection.testfirst"));

            logger.addAppender(new StatusMessageAppender(txtComment));

            //======== this ========

            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            //======== pnlDB ========
            {
                pnlDB.setBackground(new Color(238, 238, 238));
                pnlDB.setLayout(new FormLayout(
                        "default, $ugap, default:grow, $ugap, default",
                        "7*(default, $lgap), fill:default:grow, $lgap, default"));

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
                txtPort.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtPort, CC.xy(3, 3));


                //---- lblUser ----
                lblUser.setText("Benutzer");
                lblUser.setHorizontalAlignment(SwingConstants.RIGHT);
                lblUser.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(lblUser, CC.xy(1, 5));

                //---- txtAdmin ----
                txtUser.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtUser, CC.xy(3, 5));


                //---- lblPassword ----
                lblPassword.setText("Passwort");
                lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
                lblPassword.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(lblPassword, CC.xy(1, 7));

                //---- txtPassword ----
                txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtPassword, CC.xy(3, 7));


                //---- lblCat ----
                lblCat.setText("Katalog");
                lblCat.setHorizontalAlignment(SwingConstants.RIGHT);
                lblCat.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(lblCat, CC.xy(1, 9));

                //---- txtCatalog ----
                txtCatalog.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtCatalog, CC.xy(3, 9));


                pnlDB.add(btnTestParameters, CC.xyw(1, 11, 3));


                btnTestParameters.setFont(new Font("Arial", Font.PLAIN, 20));
                btnCreateNewSchema.setFont(new Font("Arial", Font.PLAIN, 20));
                btnUpdateSchema.setFont(new Font("Arial", Font.PLAIN, 20));

                JPanel buttonLine2 = new JPanel();
                buttonLine2.setLayout(new BoxLayout(buttonLine2, BoxLayout.LINE_AXIS));
                buttonLine2.add(btnCreateNewSchema);
                buttonLine2.add(btnUpdateSchema);

                pnlDB.add(buttonLine2, CC.xyw(1, 13, 3));


                pnlDB.add(new JScrollPane(txtComment), CC.xyw(1, 15, 3, CC.DEFAULT, CC.FILL));
            }
            addComponent(pnlDB, true);


            try {
                String server = SYSTools.catchNull(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_JDBC_HOST));
                String catalog = SYSTools.catchNull(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_JDBC_CATALOG, "opde"));
                String sPort = SYSTools.catchNull(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_JDBC_PORT), "3306");
                String user = SYSTools.catchNull(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_JDBC_USER, "opdeuser"));

                if (server.isEmpty() || catalog.isEmpty()) {
                    // if the is an old URL in the config file, try to parse it
                    String url = SYSTools.catchNull(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_JDBC_URL));
                    if (url.length() >= 13) { // to trim "jdbc:mysql://"
                        StringTokenizer st = new StringTokenizer(url.substring(13, url.length()), ":/");
                        if (st.countTokens() == 3) {
                            server = st.nextToken();
                            port = Integer.parseInt(st.nextToken());
                            catalog = st.nextToken();
                        }
                    }
                }

                txtServer.getDocument().insertString(0, server, null);
                txtPort.getDocument().insertString(0, sPort, null);
                txtUser.getDocument().insertString(0, user, null);
                txtPassword.getDocument().insertString(0, OPDE.decryptJDBCPasswort(), null);
                txtCatalog.getDocument().insertString(0, catalog, null);

            } catch (BadLocationException e) {
                e.printStackTrace();
            }


        }


        private void testParameters() {

            db_server_pingable = false;
            db_dbms_reachable = false;
            db_version = DB_VERSION.UNKNOWN;
            db_credentials_correct = false;

            try {
                port = Integer.parseInt(txtPort.getText().trim());
                if (port < 1 || port > 65535) {
                    port = 3306;
                }
            } catch (NumberFormatException nfe) {
                port = 3306;
            }

            try {
                // Server Connection

                logger.debug("pinging: " + txtServer.getText() + ":" + port);
                pingResult = SYSTools.socketping(txtServer.getText(), Integer.toString(port));
                logger.debug(pingResult);

                db_server_pingable = true;

                // Credentials
                Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(txtServer.getText().trim(), Integer.toString(port), null), txtUser.getText(), new String(txtPassword.getPassword()).trim());
                logger.debug("jdbc.connection.ok");
                db_credentials_correct = true;

                db_dbms_reachable = true;

                jdbcConnection.setCatalog(txtCatalog.getText().trim());
                // catalog and schema version
                int neededVersion = OPDE.getAppInfo().getDbversion();
                int currentVersion = EntityTools.getDatabaseSchemaVersion(jdbcConnection);

                if (currentVersion == -1) db_version = DB_VERSION.UNKNOWN; // tables SYSProps is messed up
                else if (currentVersion < neededVersion) db_version = DB_VERSION.TOO_LOW;
                else if (currentVersion > neededVersion) db_version = DB_VERSION.TOO_HIGH;
                else db_version = DB_VERSION.PERFECT;

                if (db_version != DB_VERSION.PERFECT) {
                    String message = "";
                    if (db_version == DB_VERSION.UNKNOWN) message = "opde.initwizard.db_version_unknown";
                    else if (db_version == DB_VERSION.TOO_HIGH) message = "opde.initwizard.db_version_too_high";
                    else message = "opde.initwizard.db_version_too_low";

                    throw new SQLException(SYSTools.xx(message));
                }


                jdbcConnection.close();


            } catch (IOException e) {
                logger.error(e);
            } catch (SQLException e) {
                logger.error(e);
            } catch (Exception e) {
                OPDE.fatal(logger, e);
            } finally {
                logger.debug("db_version: " + db_version);
                logger.debug("db_server_pingable: " + db_server_pingable);
                logger.debug("db_dbms_reachable: " + db_dbms_reachable);
                fireButtonEvent((db_version == DB_VERSION.PERFECT ? ButtonEvent.ENABLE_BUTTON : ButtonEvent.DISABLE_BUTTON), ButtonNames.FINISH);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        btnCreateNewSchema.setEnabled(db_server_pingable);
                        btnUpdateSchema.setEnabled(db_version == DB_VERSION.TOO_LOW);
                    }
                });

            }
        }

    }


    private class UpdateDB extends DefaultWizardPage {
        JButton btnSearchMysqlDump;
        JButton btnDBBackup;
        JButton btnOpenBackupDir;
        JButton btnLockDB;
        JButton btnUnLockDB;
        JButton btnUpdateDB;
        JLabel lblRoot;
        JLabel lblPassword;
        JLabel lblMysqldump;
        JTextField txtAdmin;
        JPasswordField txtPassword;
        JTextPane txtComments;
        JScrollPane vertical;
        JProgressBar pbProgress;
        SwingWorker worker;

        Logger logger = Logger.getLogger(getClass());


        public UpdateDB(String title, String description) {
            super(title, description);
            setupWizardButtons();
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            JPanel pnlMain = new JPanel();
            pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.PAGE_AXIS));

            String server = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST));
            String catalog = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG, "opde"));
            String sPort = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_PORT), "3306");
            String root = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_ROOTUSER), "root");

            lblRoot = new JLabel();
            lblPassword = new JLabel();
            txtAdmin = new JTextField();
            txtPassword = new JPasswordField();
            txtComments = new JTextPane();
            txtComments.setEditable(false);
            lblMysqldump = new JLabel();
            pbProgress = new JProgressBar();

            txtAdmin.getDocument().addDocumentListener(new RelaxedDocumentListener(var1 -> {
                jdbcProps.put(SYSPropsTools.KEY_JDBC_ROOTUSER, txtAdmin.getText().trim().isEmpty() ? "root" : txtAdmin.getText().trim());
            }));

            logger.addAppender(new StatusMessageAppender(txtComments));
//            h1Style = txtComments.addStyle("h1Style", null);
//            StyleConstants.setForeground(h1Style, Color.blue);
//            StyleConstants.setBold(h1Style, true);
//            StyleConstants.setFontSize(h1Style, 24);
//
//            h2Style = txtComments.addStyle("h2Style", null);
//            StyleConstants.setForeground(h2Style, SYSConst.mediumpurple4);
//            StyleConstants.setBold(h2Style, true);
//            StyleConstants.setFontSize(h2Style, 20);
//
//            h3Style = txtComments.addStyle("h3Style", null);
//            StyleConstants.setForeground(h3Style, SYSConst.salmon4);
//            StyleConstants.setBold(h3Style, true);
//            StyleConstants.setFontSize(h3Style, 16);

            txtComments.setEditable(false);
            vertical = new JScrollPane(txtComments);

            logger.info(SYSTools.xx("opde.initwizard.page.update.current.mysqldump") + ": " + jdbcProps.getProperty(SYSPropsTools.KEY_MYSQLDUMP_EXEC));
            logger.info(SYSTools.xx("opde.initwizard.page.update.target") + ": " + EntityTools.getJDBCUrl(server, sPort, catalog));
            logger.info(SYSTools.xx("opde.initwizard.page.update.always.backup.first"));

            btnLockDB = new JButton(SYSConst.icon22locked);
            btnLockDB.addActionListener(e -> {
                if (worker != null && !worker.isDone()) return;
                try {
                    lockServer();
                } catch (SQLException e1) {
                    logger.error(e1);
                }
            });

            btnUnLockDB = new JButton(SYSConst.icon22unlocked);
            btnUnLockDB.addActionListener(e2 -> {
                if (worker != null && !worker.isDone()) return;
                try {
                    unlockServer();
                } catch (SQLException e1) {
                    logger.error(e1);
                }
            });

            btnUpdateDB = new JButton(SYSTools.xx("opde.initwizard.page.update.updatedb"), SYSConst.icon22updateDB);
            btnUpdateDB.setFont(new Font("Arial", Font.PLAIN, 16));
            btnUpdateDB.addActionListener(al -> {
                if (worker != null && !worker.isDone()) return;
                fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.BACK);
                upgradeDatabase(EntityTools.getJDBCUrl(server, sPort, catalog));
            });

            btnDBBackup = new JButton(SYSTools.xx("opde.initwizard.page.update.backupdb"));
            btnDBBackup.setFont(new Font("Arial", Font.PLAIN, 16));
            btnDBBackup.addActionListener(e -> {
                if (worker != null && !worker.isDone()) return;
                SwingWorker worker = new SwingWorker() {
                    String sBackupFile = System.getProperty("java.io.tmpdir") + File.separator + catalog + "-backup-" + System.currentTimeMillis() + ".dump";

                    @Override
                    protected Object doInBackground() throws Exception {

                        txtAdmin.setEnabled(false);
                        txtPassword.setEnabled(false);
                        btnDBBackup.setEnabled(false);
                        btnSearchMysqlDump.setEnabled(false);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.BACK);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.CANCEL);

                        Map map = new HashMap();

                        map.put("host", jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST));
                        map.put("user", txtAdmin.getText().trim());
                        map.put("pw", new String(txtPassword.getPassword()).trim());
                        map.put("file", sBackupFile);
                        map.put("catalog", jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG));
                        CommandLine cmdLine = CommandLine.parse(jdbcProps.getProperty(SYSPropsTools.KEY_MYSQLDUMP_EXEC) + " -v --opt -h ${host} -u ${user} -p${pw} -r ${file} ${catalog}");

                        cmdLine.setSubstitutionMap(map);
                        DefaultExecutor executor = new DefaultExecutor();
                        executor.setExitValue(0);

                        OutputStream output = new OutputStream() {
                            private StringBuilder string = new StringBuilder();

                            @Override
                            public void write(int b) throws IOException {
                                String str = Character.toString((char) b);
                                if (str.equals(System.lineSeparator())) {
                                    logger.info(string);
                                    string = new StringBuilder();
                                } else {
                                    string.append(str);
                                }
                            }
                        };
                        executor.setStreamHandler(new PumpStreamHandler(output));
                        return executor.execute(cmdLine);
                    }

                    @Override
                    protected void done() {
                        try {
                            int exitValue = (int) get();
                            logger.info("Exitvalue: " + exitValue);
                            logger.info(SYSTools.xx("opde.initwizard.page.update.backupdb.targetfile", sBackupFile));
                            summary.add(SYSTools.xx("opde.initwizard.page.update.backupdb.targetfile", sBackupFile));
                        } catch (Exception e1) {
                            logger.error(e1);
                        } finally {
                            txtAdmin.setEnabled(true);
                            txtPassword.setEnabled(true);
                            btnDBBackup.setEnabled(true);
                            btnSearchMysqlDump.setEnabled(true);
                            setupWizardButtons();
                        }
                        super.done();
                    }
                };
                worker.execute();
            });

            btnSearchMysqlDump = GUITools.getTinyButton("opde.initwizard.page.update.search.mysqldump", SYSConst.icon22exec);
            btnSearchMysqlDump.addActionListener(e -> {
                if (worker != null && !worker.isDone()) return;
                final JidePopup popup = new JidePopup();
                final FileChooserPanel fcp = new FileChooserPanel(jdbcProps.getProperty(SYSPropsTools.KEY_MYSQLDUMP_EXEC));
                fcp.addItemListener(e1 -> {
                    if (e1.getStateChange() == ItemEvent.SELECTED) {
                        jdbcProps.put(SYSPropsTools.KEY_MYSQLDUMP_EXEC, e1.getItem().toString());
                        lblMysqldump.setText(jdbcProps.getProperty(SYSPropsTools.KEY_MYSQLDUMP_EXEC));
                        logger.info(SYSTools.xx("opde.initwizard.page.update.current.mysqldump", jdbcProps.getProperty(SYSPropsTools.KEY_MYSQLDUMP_EXEC)));
                    }
                    popup.hidePopup();
                });

                popup.setMovable(false);
                JPanel pnl = new JPanel(new BorderLayout(10, 10));
                pnl.setBorder(new EmptyBorder(5, 5, 5, 5));
                pnl.add(fcp, BorderLayout.CENTER);

                popup.setContentPane(pnl);
                popup.setPreferredSize(pnl.getPreferredSize());
                pnl.revalidate();
                popup.setOwner(btnSearchMysqlDump);
                popup.removeExcludedComponent(thisWizard);
                popup.setDefaultFocusComponent(pnl);
                popup.showPopup();
            });

            btnOpenBackupDir = new JButton("open backup dir");
            btnOpenBackupDir.addActionListener(e -> {
                if (worker != null && !worker.isDone()) return;
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(new File(System.getProperty("java.io.tmpdir")));
                    }
                } catch (IOException ioe) {
                    logger.error(ioe.getMessage());
                }
            });


            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            //======== pnlDB ========
            {

                pnlMain.setLayout(new FormLayout(
                        "default, $ugap, default:grow, $ugap, default",
                        "7*(default, $lgap), fill:default:grow, $lgap, default"));

                //---- lblServer ----
                lblRoot.setText(SYSTools.xx("opde.initwizard.root.user"));
                lblRoot.setHorizontalAlignment(SwingConstants.RIGHT);
                lblRoot.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlMain.add(lblRoot, CC.xy(1, 1));

                //---- txtServer ----
                txtAdmin.setFont(new Font("Arial", Font.PLAIN, 16));
                txtAdmin.setText(root);
                pnlMain.add(txtAdmin, CC.xy(3, 1));

                //---- lblPort ----
                lblPassword.setText(SYSTools.xx("opde.initwizard.root.password"));
                lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
                lblPassword.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlMain.add(lblPassword, CC.xy(1, 3));

                //---- txtPort ----
                txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlMain.add(txtPassword, CC.xy(3, 3));

                lblMysqldump.setText(jdbcProps.getProperty(SYSPropsTools.KEY_MYSQLDUMP_EXEC));
                lblMysqldump.setHorizontalAlignment(SwingConstants.LEADING);
                lblMysqldump.setFont(new Font("Arial", Font.PLAIN, 16));

                JPanel buttonLine1 = new JPanel();
                buttonLine1.setLayout(new BoxLayout(buttonLine1, BoxLayout.LINE_AXIS));
                buttonLine1.add(btnDBBackup);
                buttonLine1.add(btnSearchMysqlDump);
                buttonLine1.add(Box.createHorizontalStrut(5));
                buttonLine1.add(lblMysqldump);
                buttonLine1.add(btnOpenBackupDir);

                JPanel buttonLine2 = new JPanel();
                buttonLine2.setLayout(new BoxLayout(buttonLine2, BoxLayout.LINE_AXIS));
                buttonLine2.add(btnLockDB);
                buttonLine2.add(Box.createHorizontalStrut(5));
                buttonLine2.add(btnUpdateDB);
                buttonLine2.add(Box.createHorizontalStrut(5));
                buttonLine2.add(btnUnLockDB);


                pnlMain.add(buttonLine1, CC.xyw(1, 7, 5));
                pnlMain.add(buttonLine2, CC.xyw(1, 9, 5));
                pnlMain.add(pbProgress, CC.xyw(1, 11, 5));
                pnlMain.add(vertical, CC.xyw(1, 15, 5, CC.DEFAULT, CC.FILL));

            }

            addComponent(pnlMain, true);
        }

        private void lockServer() throws SQLException {
            String server = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST));
            String catalog = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG, "opde"));
            String sPort = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_PORT), "3306");

            Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(server, sPort, catalog), txtAdmin.getText().trim(), new String(txtPassword.getPassword()).trim());
            if (!EntityTools.isServerLocked(jdbcConnection)) {
                EntityTools.setServerLocked(jdbcConnection, true);
                jdbcConnection.close();
                logger.info(SYSTools.xx("opde.initwizard.page.update.server.locked"));
                summary.add(SYSTools.xx("opde.initwizard.page.update.server.locked"));
            } else {
                logger.warn(SYSTools.xx("opde.initwizard.page.update.server.locked"));
            }
        }

        private void unlockServer() throws SQLException {
            String server = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST));
            String catalog = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG, "opde"));
            String sPort = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_PORT), "3306");

            Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(server, sPort, catalog), txtAdmin.getText().trim(), new String(txtPassword.getPassword()).trim());
            if (EntityTools.isServerLocked(jdbcConnection)) {
                EntityTools.setServerLocked(jdbcConnection, false);
                jdbcConnection.close();
                logger.info(SYSTools.xx("opde.initwizard.page.update.server.unlocked"));
                summary.add(SYSTools.xx("opde.initwizard.page.update.server.unlocked"));
            } else {
                logger.warn(SYSTools.xx("opde.initwizard.page.update.server.unlocked"));
            }
        }


        private void upgradeDatabase(String jdbcurl) {
            worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {

                    fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.BACK);
                    fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);
                    fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
                    fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.CANCEL);

                    Connection jdbcConnection = DriverManager.getConnection(jdbcurl, txtAdmin.getText().trim(), new String(txtPassword.getPassword()).trim());

                    if (!EntityTools.isServerLocked(jdbcConnection)) {
                        throw new SQLException(SYSTools.xx("opde.initwizard.page.update.lock.first"));
                    }

                    int neededVersion = OPDE.getAppInfo().getDbversion();
                    int currentVersion = EntityTools.getDatabaseSchemaVersion(jdbcConnection);

                    HashMap<Integer, ArrayList<String>> mapToNewestVersion = new HashMap<>();

                    String currentSQLCommand = "";
                    for (int startVersion = currentVersion; startVersion < neededVersion; startVersion++) {
                        ArrayList<String> sqlToNextVersion = new ArrayList<>();
                        File sqlUpdate = AppInfo.getSQLUpdateScript(startVersion);

                        Iterator it = Files.lines(sqlUpdate.toPath()).iterator();
                        while (it.hasNext()) {
                            String line = it.next().toString().trim();
                            if (!line.startsWith("--")) {
                                currentSQLCommand += line;
                                if (currentSQLCommand.endsWith(";")) {
                                    sqlToNextVersion.add(currentSQLCommand);
                                    currentSQLCommand = "";
                                }
                            }
                        }
                        mapToNewestVersion.put(startVersion, sqlToNextVersion);
                    }

                    // for progressbar
                    int min = 0;
                    int max = 0;
                    int progress = 0;
                    for (int startVersion = currentVersion; startVersion < neededVersion; startVersion++) {
                        max += mapToNewestVersion.get(startVersion).size();
                    }
                    pbProgress.setMinimum(min);
                    pbProgress.setMaximum(max);
                    pbProgress.setStringPainted(true);

                    for (int startVersion = currentVersion; startVersion < neededVersion; startVersion++) {
//                        GUITools.appendText(SYSTools.xx("opde.initwizard.page.update.updating.version", startVersion, startVersion + 1), txtComments, h1Style);
                        summary.add(SYSTools.xx("opde.initwizard.page.update.updating.version", startVersion, startVersion + 1));
                        pbProgress.setString(SYSTools.xx("opde.initwizard.page.update.updating.version", startVersion, startVersion + 1));
                        logger.info(SYSTools.xx("opde.initwizard.page.update.updating.version", startVersion, startVersion + 1));
                        for (String sql : mapToNewestVersion.get(startVersion)) {
                            progress++;
                            final int p = progress;
                            SwingUtilities.invokeLater(() -> pbProgress.setValue(p));
                            logger.info(sql.toString());
                            try {
                                PreparedStatement stmt = jdbcConnection.prepareStatement(sql);
                                int result = stmt.executeUpdate();
                                logger.info(SYSTools.xx("misc.msg.result") + " " + (result == 0 ? "OK" : result + " " + SYSTools.xx("opde.initwizard.page.update.updatedb.rows.affected")));
                                stmt.close();
                            } catch (SQLException sqle) {
                                OPDE.fatal(sqle);
                            }
                        }
                    }

                    EntityTools.setServerLocked(jdbcConnection, false);
                    summary.add(SYSTools.xx("opde.initwizard.page.update.server.unlocked"));
                    jdbcConnection.close();
                    return null;
                }

                @Override
                protected void done() {
                    super.done();
                    try {
                        get();
                        setCurrentPage(SYSTools.xx("opde.initwizard.page.summary.title"));
                    } catch (Exception e) {
                        logger.error(e);
                    } finally {
                        worker = null;
                    }
                }
            };
            worker.execute();
        }

    }


//    private class FXStatusMessageAppender extends AppenderSkeleton {
//        private final TextArea jTextA;
//        private PatternLayout defaultPatternLayout = new PatternLayout("%d{ISO8601} %-5p: %m%n");
//        private Style errorStyle, warnStyle;
//
//        public FXStatusMessageAppender(TextArea jTextA) {
//            this.jTextA = jTextA;
//            //            errorStyle = jTextA.addStyle("errorStyle", null);
//            //            warnStyle = jTextA.addStyle("warnStyle", null);
//            //            StyleConstants.setForeground(errorStyle, Color.red);
//            //            StyleConstants.setForeground(warnStyle, SYSConst.darkorange);
//        }
//
//        protected void append(LoggingEvent event) {
//            if (event.getLevel().isGreaterOrEqual(Logger.getRootLogger().getLevel())) {
//                jTextA.appendText(defaultPatternLayout.format(event));
//            }
//        }
//
//        private Style getCurrentStyle(Level level) {
//            if (level.equals(Level.ERROR))
//                return errorStyle;
//            if (level.equals(Level.WARN))
//                return warnStyle;
//            return null;
//        }
//
//        public void close() {
//        }
//
//        @Override
//        public boolean requiresLayout() {
//            return true;
//        }
//    }


    private class StatusMessageAppender extends AppenderSkeleton {
        private final JTextPane jTextA;
        private PatternLayout defaultPatternLayout = new PatternLayout("%d{ISO8601} %-5p: %m%n");
        private Style errorStyle, warnStyle;

        public StatusMessageAppender(JTextPane jTextA) {
            this.jTextA = jTextA;
            errorStyle = jTextA.addStyle("errorStyle", null);
            warnStyle = jTextA.addStyle("warnStyle", null);
            StyleConstants.setForeground(errorStyle, Color.red);
            StyleConstants.setForeground(warnStyle, SYSConst.darkorange);
        }

        protected void append(LoggingEvent event) {
            if (event.getLevel().isGreaterOrEqual(Logger.getRootLogger().getLevel())) {
                GUITools.appendText(defaultPatternLayout.format(event), jTextA, getCurrentStyle(event.getLevel()));
            }
        }

        private Style getCurrentStyle(Level level) {
            if (level.equals(Level.ERROR))
                return errorStyle;
            if (level.equals(Level.WARN))
                return warnStyle;
            return null;
        }

        public void close() {
        }

        @Override
        public boolean requiresLayout() {
            return true;
        }
    }

    private class CreateDBPage extends DefaultWizardPage {

        JButton btnCreateDB;
        JLabel lblRoot;
        JLabel lblPassword;

        JTextField txtAdmin;
        JPasswordField txtPassword;
        JTextPane txtComments;
        JScrollPane vertical;
        JProgressBar pbProgress;
        JLabel lblInstallMed;
        YesNoToggleButton btnInstallMed;
        SwingWorker worker;
        Logger logger = Logger.getLogger(getClass());

        public CreateDBPage(String title, String description) {
            super(title, description);
            setupWizardButtons();

            addPageListener(pageEvent -> {
                if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                    String server = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST));
                    String catalog = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG, "opde"));
                    String sPort = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_PORT), "3306");
                    logger.info(SYSTools.xx("misc.msg.jdbc.url", EntityTools.getJDBCUrl(server, sPort, catalog)));
                }
                if (pageEvent.getID() == PageEvent.PAGE_CLOSED) {

                }
            });
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            JPanel pnlMain = new JPanel();
            pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.PAGE_AXIS));

            String server = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST));
            String catalog = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG, "opde"));
            String sPort = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_PORT), "3306");
            String root = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_ROOTUSER), "root");

            lblRoot = new JLabel();
            lblPassword = new JLabel();
            txtAdmin = new JTextField();
            txtPassword = new JPasswordField();
            txtComments = new JTextPane();

            txtComments.setEditable(false);
            pbProgress = new JProgressBar();
            lblInstallMed = new JLabel();
            btnInstallMed = new YesNoToggleButton();

            txtAdmin.getDocument().addDocumentListener(new RelaxedDocumentListener(var1 -> {
                jdbcProps.put(SYSPropsTools.KEY_JDBC_ROOTUSER, txtAdmin.getText().trim().isEmpty() ? "root" : txtAdmin.getText().trim());
            }));

            logger.addAppender(new StatusMessageAppender(txtComments));

            txtComments.setEditable(false);
            vertical = new JScrollPane(txtComments);

            btnCreateDB = new JButton(SYSTools.xx("opde.initwizard.page.update.createdb"), SYSConst.icon22createDB);
            btnCreateDB.setFont(new Font("Arial", Font.PLAIN, 16));
            btnCreateDB.addActionListener(al -> {
                if (worker != null && !worker.isDone()) return;
                fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.BACK);
                createDB(EntityTools.getJDBCUrl(server, sPort, catalog), catalog);
            });


            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            //======== pnlDB ========
            {

                pnlMain.setLayout(new FormLayout(
                        "default, $ugap, default:grow, $ugap, default",
                        "5*(default, $lgap), fill:default:grow, $lgap, default"));

                lblRoot.setText(SYSTools.xx("opde.initwizard.root.user"));
                lblRoot.setHorizontalAlignment(SwingConstants.RIGHT);
                lblRoot.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlMain.add(lblRoot, CC.xy(1, 1));

                txtAdmin.setFont(new Font("Arial", Font.PLAIN, 16));
                txtAdmin.setText(root);
                pnlMain.add(txtAdmin, CC.xy(3, 1));

                lblPassword.setText(SYSTools.xx("opde.initwizard.root.password"));
                lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
                lblPassword.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlMain.add(lblPassword, CC.xy(1, 3));

                txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlMain.add(txtPassword, CC.xy(3, 3));

                lblInstallMed.setText(SYSTools.xx("opde.initwizard.page.create.install.medcontent"));
                lblInstallMed.setHorizontalAlignment(SwingConstants.RIGHT);
                lblInstallMed.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlMain.add(lblInstallMed, CC.xy(1, 5));

                btnInstallMed.setToolTipText(SYSTools.toHTMLForScreen("opde.initwizard.page.create.install.medcontent.tooltip"));
                pnlMain.add(btnInstallMed, CC.xy(3, 5));

                pnlMain.add(btnCreateDB, CC.xyw(1, 7, 5));

                pnlMain.add(pbProgress, CC.xyw(1, 9, 5));

                pnlMain.add(vertical, CC.xyw(1, 11, 5, CC.DEFAULT, CC.FILL));

            }

            addComponent(pnlMain, true);
        }

        protected void createDB(String jdbcurl, String catalog) {


            worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    String server = jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST);
                    String port = jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_PORT);
                    String dbuser = jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_USER);
                    String generatedPassword4DBUser = RandomStringUtils.random(10, 0, 0, true, true, null, new SecureRandom());
                    String generatedPassword4AdminUser = RandomStringUtils.random(6, 0, 0, true, true, null, new SecureRandom());
                    String outcome = "";
                    try {
                        jdbcProps.put(SYSPropsTools.KEY_JDBC_PASSWORD, OPDE.getDesEncrypter().encrypt(generatedPassword4DBUser));

                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.BACK);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.CANCEL);

                        Connection jdbcConnection = DriverManager.getConnection(jdbcurl, txtAdmin.getText().trim(), new String(txtPassword.getPassword()).trim());

                        HashMap<File, ArrayList<String>> mapToNewestVersion = new HashMap<>();
                        HashMap<File, String> mapWithMessages = new HashMap<>();

                        File structure = AppInfo.getSQLStructureScript(OPDE.getAppInfo().getDbversion());
                        File basecontent = AppInfo.getSQLBaseContentScript(OPDE.getAppInfo().getDbversion());
                        File medcontent = AppInfo.getSQLMedContentScript(OPDE.getAppInfo().getDbversion());
                        File finallyContent = AppInfo.getFinallyContentScript(OPDE.getAppInfo().getDbversion());

                        mapToNewestVersion.put(structure, new ArrayList<>());
                        mapToNewestVersion.put(basecontent, new ArrayList<>());
                        mapToNewestVersion.put(medcontent, new ArrayList<>());
                        mapToNewestVersion.put(finallyContent, new ArrayList<>());

                        mapWithMessages.put(structure, SYSTools.xx("opde.initwizard.page.create.version", AppInfo.getSQLStructureScript(OPDE.getAppInfo().getDbversion())));
                        mapWithMessages.put(basecontent, SYSTools.xx("opde.initwizard.page.create.basecontent"));
                        mapWithMessages.put(medcontent, SYSTools.xx("opde.initwizard.page.create.medcontent"));
                        mapWithMessages.put(finallyContent, SYSTools.xx("opde.initwizard.page.create.finallyContent"));

                        ArrayList<File> files = new ArrayList<>();
                        files.add(structure);
                        files.add(basecontent);
                        if (btnInstallMed.isPositiveSelected() && medcontent.exists())
                            files.add(medcontent);
                        if (finallyContent.exists())
                            files.add(finallyContent);

                        String currentSQLCommand = "";

                        int max = 0;

                        for (File file : files) {
                            Iterator it = Files.lines(file.toPath()).iterator();
                            while (it.hasNext()) {
                                String line = it.next().toString().trim();
                                if (!line.startsWith("--")) {
                                    currentSQLCommand += line;
                                    if (currentSQLCommand.endsWith(";")) {
                                        mapToNewestVersion.get(file).add(currentSQLCommand);
                                        max++;
                                        currentSQLCommand = "";
                                    }
                                }
                            }
                        }

                        // for progressbar
                        int min = 0;
                        int progress = 0;
                        pbProgress.setMinimum(min);
                        pbProgress.setMaximum(max);
                        pbProgress.setStringPainted(true);

                        PreparedStatement stmt;

                        String queryDrop = " DROP SCHEMA IF EXISTS " + catalog;
                        stmt = jdbcConnection.prepareStatement(queryDrop);
                        stmt.executeUpdate();
                        stmt.close();
                        summary.add(SYSTools.xx("opde.initwizard.summary.createdb.dropschema", catalog));

                        String queryCreate = " CREATE SCHEMA " + catalog + " DEFAULT CHARACTER SET utf8";
                        stmt = jdbcConnection.prepareStatement(queryCreate);
                        stmt.executeUpdate();
                        stmt.close();
                        summary.add(SYSTools.xx("opde.initwizard.summary.createdb.createschema", catalog));

                        String queryGrant1 = " GRANT SELECT,INSERT,UPDATE,DELETE,CREATE TEMPORARY TABLES ON " + catalog + ".* TO '" + dbuser + "'@'localhost' IDENTIFIED BY '" + generatedPassword4DBUser + "' ";
                        stmt = jdbcConnection.prepareStatement(queryGrant1);
                        stmt.executeUpdate();
                        stmt.close();

                        String queryGrant2 = " GRANT SELECT,INSERT,UPDATE,DELETE,CREATE TEMPORARY TABLES ON " + catalog + ".* TO '" + dbuser + "'@'%' IDENTIFIED BY '" + generatedPassword4DBUser + "' ";
                        stmt = jdbcConnection.prepareStatement(queryGrant2);
                        stmt.executeUpdate();
                        stmt.close();
                        summary.add(SYSTools.xx("opde.initwizard.summary.createdb.grant", catalog));


                        jdbcConnection.setCatalog(catalog);

                        for (File file : files) {

                            summary.add(mapWithMessages.get(file));
                            logger.info(mapWithMessages.get(file));
                            pbProgress.setString(file.getAbsolutePath());

                            for (String sql : mapToNewestVersion.get(file)) {
                                progress++;
                                final int p = progress;
                                SwingUtilities.invokeLater(() -> pbProgress.setValue(p));
                                logger.info(sql.toString());

                                stmt = jdbcConnection.prepareStatement(sql);
                                int result = stmt.executeUpdate();
                                logger.info(SYSTools.xx("misc.msg.result") + " " + (result == 0 ? "OK" : result + " " + SYSTools.xx("opde.initwizard.page.update.updatedb.rows.affected")));
                                stmt.close();

                            }
                        }

                        // Set the password for the OPDE admin user
                        String queryAdminPW = " UPDATE users SET md5pw = MD5(?) WHERE ukennung = 'admin'";
                        stmt = jdbcConnection.prepareStatement(queryAdminPW);
                        stmt.setString(1, generatedPassword4AdminUser);
                        stmt.executeUpdate();
                        jdbcConnection.close();
                        summary.add(SYSTools.xx("opde.initwizard.summary.createdb.setpassword.adminuser", catalog));

                        outcome = SYSTools.xx("opde.initwizard.page.create.installation.summary", server, port, catalog, dbuser, generatedPassword4DBUser, generatedPassword4AdminUser, EntityTools.getJDBCUrl(server, port, catalog), DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date()));
                    } catch (SQLException sql) {
                        logger.error(sql);
                    }

                    return outcome;
                }

                @Override
                protected void done() {
                    super.done();
                    try {
                        String result = get().toString();
                        if (!result.isEmpty()) {
                            SYSFilesTools.print(result, true);
                            setCurrentPage(SYSTools.xx("opde.initwizard.page.summary.title"));
                        }
                    } catch (Exception e) {
                        logger.fatal(e);
                        System.exit(1);
                    } finally {
                        worker = null;
                    }
                }
            };
            worker.execute();

        }
    }


    private class CompletionPage extends CompletionWizardPage {
        public CompletionPage(String title, String description) {
            super(title, description);
            setupWizardButtons();
            addPageListener(pageEvent -> {
                if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                    OPDE.getLocalProps().putAll(jdbcProps);
                    OPDE.saveLocalProps();
                }
                if (pageEvent.getID() == PageEvent.PAGE_CLOSED) {

                }
            });

        }


        @Override
        protected void initContentPane() {
            super.initContentPane();


            JTextPane txt = new JTextPane();
            txt.setEditable(false);
            txt.setContentType("text/html");
            txt.setOpaque(false);
            txt.setText(SYSTools.toHTML(SYSConst.html_div(check())));

            addComponent(new JScrollPane(txt), true);
            addSpace();
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.CANCEL);
        }

        private String check() {
            String result = SYSConst.html_h2("opde.initwizard.page.completion.summary1");
            String items = "";
            for (String item : summary) {
                items += SYSConst.html_li(item);
            }
            result += SYSConst.html_ol(items);
            result += SYSConst.html_paragraph("opde.initwizard.page.completion.summary2");
            return result;
        }


    }
}
