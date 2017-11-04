package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.dialog.*;
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
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
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
        DBVERSION_UNKNOWN, DBVERSION_TOO_LOW, DB_VERSION_PERFECT, DB_VERSION_TOO_HIGH
    }


    private DB_VERSION db_version;
    private boolean db_server_pingable;
    private boolean db_dbms_reachable;
    private boolean db_catalog_exists;


    private String pingResult = "";
    private String creationResultPage = "";
    private String adminPassword = "";

    private AbstractWizardPage pageWelcome;
    private AbstractWizardPage pageConnection;
    private AbstractWizardPage pageSituation;
    private AbstractWizardPage pageBackup;
    private AbstractWizardPage pageSelection;
    private AbstractWizardPage pageCreateDB;
    private AbstractWizardPage pageUpgradeDB;
    private AbstractWizardPage pageCompletion;

    private ArrayList<String> summary, situation;

    // this Map contains the current entries of the user during the lifetime of the wizard
    // after succesful completion the settings are copied over to the OPDE.localProperties and
    // eventually saved to opde.cfg
    private Properties jdbcProps;
    private WizardDialog thisWizard;

    private String helpKey = "opde.initwizard.page.welcome.helpurl";

    @Override
    public ButtonPanel createButtonPanel() {
        ButtonPanel pnl = super.createButtonPanel();
        JButton btnHelp = GUITools.getTinyButton("opde.mainframe.btnHelp.tooltip", SYSConst.icon48help);
        btnHelp.addActionListener(al -> {
            try {
                URI uri = new URI(SYSTools.xx(helpKey));
                Desktop.getDesktop().browse(uri);
            } catch (Exception ex) {
                Logger.getLogger(getClass()).warn(SYSTools.xx("opde.mainframe.noHelpAvailable"));
            }
        });
        pnl.addButton(btnHelp, ButtonPanel.HELP_BUTTON);

        return pnl;
    }

    public InitWizard() {
        super(new JFrame(), false);
        setTitle(SYSTools.getWindowTitle("opde.initwizard.title"));
        thisWizard = this;
        setResizable(true);
        summary = new ArrayList<>();
        situation = new ArrayList<>();

        jdbcProps = new Properties();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        DriverManager.setLoginTimeout(2);
        createWizard();


    }


    private void createWizard() {


        pageWelcome = new WelcomePage(SYSTools.xx("opde.initwizard.page.welcome.title"), SYSTools.xx("opde.initwizard.page.welcome.description"));
        pageConnection = new ConnectionPage(SYSTools.xx("opde.initwizard.page.connection.title"), SYSTools.xx("opde.initwizard.page.connection.description"));
        pageSituation = new SituationPage(SYSTools.xx("opde.initwizard.page.situation.title"), SYSTools.xx("opde.initwizard.page.situation.description"));
        pageBackup = new BackupPage(SYSTools.xx("opde.initwizard.page.backup.title"), SYSTools.xx("opde.initwizard.page.backup.description"));
        pageSelection = new SelectionPage(SYSTools.xx("opde.initwizard.page.selection.title"), SYSTools.xx("opde.initwizard.page.selection.description"));
        pageCreateDB = new CreateDBPage(SYSTools.xx("opde.initwizard.page.createdb.title"), SYSTools.xx("opde.initwizard.page.createdb.description"));
        pageUpgradeDB = new UpdateDB(SYSTools.xx("opde.initwizard.page.upgradedb.title"), SYSTools.xx("opde.initwizard.page.upgradedb.description"));
        pageCompletion = new CompletionPage(SYSTools.xx("opde.initwizard.page.summary.title"), SYSTools.xx("opde.initwizard.page.summary.description"));

        final PageList model;
        model = new PageList();
        model.append(pageWelcome);
        model.append(pageConnection);
        model.append(pageSituation);
        model.append(pageBackup);
        model.append(pageSelection);
        model.append(pageCreateDB);
        model.append(pageUpgradeDB);
        model.append(pageCompletion);

        setPageList(model);

        setFinishAction(new AbstractAction("Finish") {
            public void actionPerformed(ActionEvent e) {
                OPDE.getLocalProps().putAll(jdbcProps);
                OPDE.saveLocalProps();
                if (!creationResultPage.isEmpty())
                    SYSFilesTools.print(creationResultPage, true);
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
        setNextAction(new AbstractAction("Next") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = model.getPageIndexByFullTitle(getCurrentPage().getFullTitle()) + 1;
                while (!model.getPage(index).isPageEnabled()) {
                    index++;
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
            addPageListener(pageEvent -> {
                if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                    helpKey = "opde.initwizard.page.welcome.helpurl";
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

            txt.setText(SYSTools.toHTML("opde.initwizard.page.welcome.text"));

            addComponent(txt, true);
            addSpace();
            addText(SYSTools.xx("opde.wizards.buttontext.letsgo"), new Font("SansSerif", Font.PLAIN, 14));
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
        private JTextPane txtComments;
        private JButton btnTestParameters;

        private int port = 3306;

        public ConnectionPage(String title, String description) {
            super(title, description);
            // we consider the worst case first.
            db_version = DB_VERSION.DBVERSION_UNKNOWN;
            db_server_pingable = false;
            db_dbms_reachable = false;
            db_catalog_exists = false;


            addPageListener(pageEvent -> {

                if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                    helpKey = "opde.initwizard.page.connection.helpurl";
                }
                if (pageEvent.getID() == PageEvent.PAGE_CLOSED) {

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

        @Override
        protected void initContentPane() {
            super.initContentPane();
            pnlDB = new JPanel();

            RelaxedDocumentListener dl = new RelaxedDocumentListener(0, o -> {
                fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);
                fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
//                btnUpdateSchema.setEnabled(false);
//                btnCreateNewSchema.setEnabled(false);
            });

            txtServer = new JTextField();
            txtPassword = new JPasswordField();
            txtPort = new JTextField();
            txtCatalog = new JTextField();
            txtUser = new JTextField();
            txtUser.setEnabled(true);
            txtComments = new JTextPane();
            txtComments.setFont(new Font("MonoSpaced", Font.PLAIN, 12));

            txtServer.getDocument().addDocumentListener(dl);
            txtPassword.getDocument().addDocumentListener(dl);
            txtPort.getDocument().addDocumentListener(dl);
            txtCatalog.getDocument().addDocumentListener(dl);
            txtUser.getDocument().addDocumentListener(dl);

            lblPassword = new JLabel();
            lblUser = new JLabel();
            lblServer = new JLabel();
            lblPort = new JLabel();
            lblCat = new JLabel();

            btnTestParameters = new JButton(SYSTools.xx("opde.initwizard.page.connection.testing"), SYSConst.icon48statusDB);
            btnTestParameters.addActionListener(e1 -> testParameters());


            logger.addAppender(new StatusMessageAppender(txtComments));

            //======== this ========

            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            //======== pnlDB ========
            {
                pnlDB.setBackground(new Color(238, 238, 238));
                pnlDB.setLayout(new FormLayout(
                        "default, $ugap, default:grow, $ugap, default",
                        "7*(default, $lgap), fill:default:grow, $lgap, default"));

                //---- lblServer ----
                lblServer.setText(SYSTools.xx("opde.initwizard.db.host"));
                lblServer.setHorizontalAlignment(SwingConstants.RIGHT);
                lblServer.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlDB.add(lblServer, CC.xy(1, 1));

                //---- txtServer ----
                txtServer.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlDB.add(txtServer, CC.xy(3, 1));


                //---- lblPort ----
                lblPort.setText(SYSTools.xx("opde.initwizard.db.port"));
                lblPort.setHorizontalAlignment(SwingConstants.RIGHT);
                lblPort.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlDB.add(lblPort, CC.xy(1, 3));

                //---- txtPort ----
                txtPort.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlDB.add(txtPort, CC.xy(3, 3));


                //---- lblUser ----
                lblUser.setText(SYSTools.xx("opde.initwizard.db.app.user"));
                lblUser.setHorizontalAlignment(SwingConstants.RIGHT);
                lblUser.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlDB.add(lblUser, CC.xy(1, 5));

                //---- txtAdmin ----
                txtUser.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlDB.add(txtUser, CC.xy(3, 5));


                //---- lblPassword ----
                lblPassword.setText(SYSTools.xx("opde.initwizard.db.app.password"));
                lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
                lblPassword.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlDB.add(lblPassword, CC.xy(1, 7));

                //---- txtPassword ----
                txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlDB.add(txtPassword, CC.xy(3, 7));


                //---- lblCat ----
                lblCat.setText(SYSTools.xx("opde.initwizard.db.catalog"));
                lblCat.setHorizontalAlignment(SwingConstants.RIGHT);
                lblCat.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlDB.add(lblCat, CC.xy(1, 9));

                //---- txtCatalog ----
                txtCatalog.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlDB.add(txtCatalog, CC.xy(3, 9));
                pnlDB.add(btnTestParameters, CC.xyw(1, 11, 3));
                btnTestParameters.setFont(new Font("SansSerif", Font.PLAIN, 20));
                pnlDB.add(new JScrollPane(txtComments), CC.xyw(1, 15, 3, CC.DEFAULT, CC.FILL));
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
                txtPassword.getDocument().insertString(0, OPDE.getEncryption().decryptJDBCPasswort(), null);
                txtCatalog.getDocument().insertString(0, catalog, null);

            } catch (BadLocationException e) {
                e.printStackTrace();
            }

        }


        private void testParameters() {

            db_server_pingable = false;
            db_dbms_reachable = false;
            db_catalog_exists = false;
            db_version = DB_VERSION.DBVERSION_UNKNOWN;
            situation.clear();

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

                logger.info("ping: " + txtServer.getText() + ":" + port);
                pingResult = SYSTools.socketping(txtServer.getText(), Integer.toString(port));
                logger.info(pingResult);
                db_server_pingable = true;

                // Credentials
                Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(txtServer.getText().trim(), Integer.toString(port), null), txtUser.getText(), new String(txtPassword.getPassword()).trim());
                logger.debug("jdbc.connection.ok");
                db_dbms_reachable = true;

                jdbcConnection.setCatalog(txtCatalog.getText().trim());
                db_catalog_exists = true;

                // catalog and schema version
                int neededVersion = OPDE.getAppInfo().getDbversion();
                int currentVersion = EntityTools.getDatabaseSchemaVersion(jdbcConnection);

                jdbcConnection.close();

                if (currentVersion == -1) db_version = DB_VERSION.DBVERSION_UNKNOWN; // table SYSProps is messed up
                else if (currentVersion < neededVersion) db_version = DB_VERSION.DBVERSION_TOO_LOW;
                else if (currentVersion > neededVersion) db_version = DB_VERSION.DB_VERSION_TOO_HIGH;
                else db_version = DB_VERSION.DB_VERSION_PERFECT;

                if (db_version != DB_VERSION.DB_VERSION_PERFECT) {
                    String db_version_message;
                    if (db_version == DB_VERSION.DBVERSION_UNKNOWN)
                        db_version_message = SYSTools.xx("opde.initwizard.db_version_unknown");
                    else if (db_version == DB_VERSION.DB_VERSION_TOO_HIGH)
                        db_version_message = SYSTools.xx("opde.initwizard.db_version_too_high", neededVersion, currentVersion);
                    else
                        db_version_message = SYSTools.xx("opde.initwizard.db_version_too_low", neededVersion, currentVersion);
                    situation.add(db_version_message);
                    throw new SQLException(db_version_message);
                } else {
                    logger.info(SYSTools.xx("opde.initwizard.db_version_perfect"));
                }

            } catch (IOException e) {
                logger.error(e);
            } catch (SQLException e) {
                logger.error(e);
            } catch (Exception e) {
                logger.fatal(e);
                System.exit(1);

            } finally {

                jdbcProps.put(SYSPropsTools.KEY_JDBC_HOST, txtServer.getText().trim());
                jdbcProps.put(SYSPropsTools.KEY_JDBC_PORT, Integer.toString(port));
                jdbcProps.put(SYSPropsTools.KEY_JDBC_USER, txtUser.getText().trim());
                try {
//                    jdbcProps.put(SYSPropsTools.KEY_JDBC_PASSWORD, OPDE.getDesEncrypter().encrypt(new String(txtPassword.getPassword()).trim()));
//                    jdbcProps.put(SYSPropsTools.KEY_JDBC_PASSWORD, new String(OPDE.getEncryption().encrypt(new String(txtPassword.getPassword()).getBytes("UTF-8")), Charset.forName("UTF-8")));
                    jdbcProps.put(SYSPropsTools.KEY_JDBC_PASSWORD, OPDE.getEncryption().encrypt(new String(txtPassword.getPassword())));

                } catch (Exception e) {
                    logger.fatal(e);
                    System.exit(1);
                }
                jdbcProps.put(SYSPropsTools.KEY_JDBC_CATALOG, txtCatalog.getText().trim());


                logger.info(SYSTools.xx("misc.msg.db_server_pingable") + ": " + SYSTools.booleanToString(db_server_pingable));
                logger.info(SYSTools.xx("misc.msg.db_dbms_reachable") + ": " + SYSTools.booleanToString(db_dbms_reachable));
                logger.info(SYSTools.xx("misc.msg.db_catalog_exists") + ": " + SYSTools.booleanToString(db_catalog_exists));


                logger.info(SYSTools.xx("misc.msg.db_version") + ": " + SYSTools.xx(db_version.toString()));

                if (!db_server_pingable) situation.add(SYSTools.xx("opde.initwizard.not.db_server_pingable"));
                else if (!db_dbms_reachable) situation.add(SYSTools.xx("opde.initwizard.not.db_dbms_reachable"));
                else if (!db_catalog_exists) situation.add(SYSTools.xx("opde.initwizard.not.db_catalog_exists"));

                fireButtonEvent(db_version == DB_VERSION.DB_VERSION_PERFECT ? ButtonEvent.ENABLE_BUTTON : ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
                fireButtonEvent(db_version != DB_VERSION.DB_VERSION_PERFECT && db_server_pingable ? ButtonEvent.ENABLE_BUTTON : ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);

            }
        }


    }


    private class UpdateDB extends DefaultWizardPage {

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
            addPageListener(pageEvent -> {
                if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                    helpKey = "opde.initwizard.page.upgradedb.helpurl";
                    String server = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST));
                    String catalog = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG, "opde"));
                    String sPort = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_PORT), "3306");
                    logger.info(SYSTools.xx("misc.msg.jdbc.url", EntityTools.getJDBCUrl(server, sPort, catalog)));
                    pageCreateDB.setPageEnabled(false);
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

            String server = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST));
            String catalog = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG, "opde"));
            String sPort = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_PORT), "3306");
            String root = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_ROOTUSER), "root");

            lblRoot = new JLabel();
            lblPassword = new JLabel();
            txtAdmin = new JTextField();
            txtPassword = new JPasswordField(adminPassword);
            txtComments = new JTextPane();
            txtComments.setEditable(false);
            lblMysqldump = new JLabel();
            pbProgress = new JProgressBar();

            txtAdmin.getDocument().addDocumentListener(new RelaxedDocumentListener(var1 -> {
                jdbcProps.put(SYSPropsTools.KEY_JDBC_ROOTUSER, txtAdmin.getText().trim().isEmpty() ? "root" : txtAdmin.getText().trim());
            }));

            logger.addAppender(new StatusMessageAppender(txtComments));

            txtComments.setEditable(false);
            txtComments.setFont(new Font("MonoSpaced", Font.PLAIN, 12));

            vertical = new JScrollPane(txtComments);

//            logger.info(SYSTools.xx("opde.initwizard.page.update.current.mysqldump") + ": " + jdbcProps.getProperty(SYSPropsTools.KEY_MYSQLDUMP_EXEC));
//            logger.info(SYSTools.xx("opde.initwizard.page.update.target") + ": " + EntityTools.getJDBCUrl(server, sPort, catalog));
//            logger.info(SYSTools.xx("opde.initwizard.page.update.always.backup.first"));

            btnLockDB = new JButton(SYSConst.icon48locked);
            btnLockDB.addActionListener(e -> {
                if (worker != null && !worker.isDone()) return;
                try {
                    lockServer();
                    fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.BACK);
                } catch (SQLException e1) {
                    logger.error(e1);
                }
            });

            btnUnLockDB = new JButton(SYSConst.icon48unlocked);
            btnUnLockDB.addActionListener(e2 -> {
                if (worker != null && !worker.isDone()) return;
                try {
                    unlockServer();
                    fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.BACK);
                } catch (SQLException e1) {
                    logger.error(e1);
                }
            });

            btnUpdateDB = new JButton(SYSTools.xx("opde.initwizard.page.update.updatedb"), SYSConst.icon48updateDB);
            btnUpdateDB.setFont(new Font("SansSerif", Font.PLAIN, 20));
            btnUpdateDB.addActionListener(al -> {
                if (worker != null && !worker.isDone()) return;
                upgradeDatabase(EntityTools.getJDBCUrl(server, sPort, catalog));
            });


            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            //======== pnlDB ========
            {

                pnlMain.setLayout(new FormLayout(
                        "default, $ugap, default:grow, $ugap, default",
                        "7*(default, $lgap), fill:default:grow, $lgap, default"));

                //---- lblServer ----
                lblRoot.setText(SYSTools.xx("opde.initwizard.db.root.user"));
                lblRoot.setHorizontalAlignment(SwingConstants.RIGHT);
                lblRoot.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlMain.add(lblRoot, CC.xy(1, 1));

                //---- txtServer ----
                txtAdmin.setFont(new Font("SansSerif", Font.PLAIN, 16));
                txtAdmin.setText(root);
                pnlMain.add(txtAdmin, CC.xy(3, 1));

                //---- lblPort ----
                lblPassword.setText(SYSTools.xx("opde.initwizard.db.root.password"));
                lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
                lblPassword.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlMain.add(lblPassword, CC.xy(1, 3));

                //---- txtPort ----
                txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlMain.add(txtPassword, CC.xy(3, 3));

                lblMysqldump.setText(jdbcProps.getProperty(SYSPropsTools.KEY_MYSQLDUMP_EXEC));
                lblMysqldump.setHorizontalAlignment(SwingConstants.LEADING);
                lblMysqldump.setFont(new Font("SansSerif", Font.PLAIN, 16));

//                JPanel buttonLine1 = new JPanel();
//                buttonLine1.setLayout(new BoxLayout(buttonLine1, BoxLayout.LINE_AXIS));
//                buttonLine1.add(btnDBBackup);
//                buttonLine1.add(btnSearchMysqlDump);
//                buttonLine1.add(Box.createHorizontalStrut(5));
//                buttonLine1.add(lblMysqldump);
//                buttonLine1.add(btnSearchBackupdir);

                JPanel buttonLine2 = new JPanel();
                buttonLine2.setLayout(new BoxLayout(buttonLine2, BoxLayout.LINE_AXIS));
                buttonLine2.add(btnLockDB);
                buttonLine2.add(Box.createHorizontalStrut(5));
                buttonLine2.add(btnUpdateDB);
                buttonLine2.add(Box.createHorizontalStrut(5));
                buttonLine2.add(btnUnLockDB);


//                pnlMain.add(buttonLine1, CC.xyw(1, 7, 5));
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
                    logger.info(SYSTools.xx("opde.initwizard.page.upgradedb.success"));
                    pbProgress.setStringPainted(false);
                    pbProgress.setValue(0);
                    return null;
                }

                @Override
                protected void done() {
                    super.done();
                    try {
                        get();
                        btnLockDB.setEnabled(false);
                        btnUpdateDB.setEnabled(false);
                        btnUnLockDB.setEnabled(false);
                    } catch (Exception e) {
                        fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
                        logger.error(e);
                    } finally {
                        fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.CANCEL);
                        worker = null;
                    }
                }
            };
            worker.execute();
        }

    }

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
                String message = event.getLevel().isGreaterOrEqual(Level.WARN) ? defaultPatternLayout.format(event) : defaultPatternLayout.format(event);
                GUITools.appendText(message, jTextA, getCurrentStyle(event.getLevel()));
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
                    helpKey = "opde.initwizard.page.createdb.helpurl";
                    String server = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST));
                    String catalog = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG, "opde"));
                    String sPort = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_PORT), "3306");
                    logger.info(SYSTools.xx("misc.msg.jdbc.url", EntityTools.getJDBCUrl(server, sPort, catalog)));
                    setNextPage(pageCompletion);
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


            String root = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_ROOTUSER), "root");

            lblRoot = new JLabel();
            lblPassword = new JLabel();
            txtAdmin = new JTextField();
            txtPassword = new JPasswordField(adminPassword);
            txtComments = new JTextPane();
            txtComments.setEditable(false);
            txtComments.setFont(new Font("MonoSpaced", Font.PLAIN, 12));
            pbProgress = new JProgressBar();
            lblInstallMed = new JLabel();
            btnInstallMed = new YesNoToggleButton();

            txtAdmin.getDocument().addDocumentListener(new RelaxedDocumentListener(var1 -> {
                jdbcProps.put(SYSPropsTools.KEY_JDBC_ROOTUSER, txtAdmin.getText().trim().isEmpty() ? "root" : txtAdmin.getText().trim());
            }));

            logger.addAppender(new StatusMessageAppender(txtComments));

            txtComments.setEditable(false);
            vertical = new JScrollPane(txtComments);

            btnCreateDB = new JButton(SYSTools.xx("opde.initwizard.page.update.createdb"), SYSConst.icon48createDB);
            btnCreateDB.setFont(new Font("SansSerif", Font.PLAIN, 20));
            btnCreateDB.addActionListener(al -> {
                if (worker != null && !worker.isDone()) return;
                fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.BACK);
                createDB();
            });


            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            //======== pnlDB ========
            {

                pnlMain.setLayout(new FormLayout(
                        "default, $ugap, default:grow, $ugap, default",
                        "5*(default, $lgap), fill:default:grow, $lgap, default"));

                lblRoot.setText(SYSTools.xx("opde.initwizard.db.root.user"));
                lblRoot.setHorizontalAlignment(SwingConstants.RIGHT);
                lblRoot.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlMain.add(lblRoot, CC.xy(1, 1));

                txtAdmin.setFont(new Font("SansSerif", Font.PLAIN, 16));
                txtAdmin.setText(root);
                pnlMain.add(txtAdmin, CC.xy(3, 1));

                lblPassword.setText(SYSTools.xx("opde.initwizard.db.root.password"));
                lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
                lblPassword.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlMain.add(lblPassword, CC.xy(1, 3));

                txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlMain.add(txtPassword, CC.xy(3, 3));

                lblInstallMed.setText(SYSTools.xx("opde.initwizard.page.create.install.medcontent"));
                lblInstallMed.setHorizontalAlignment(SwingConstants.RIGHT);
                lblInstallMed.setFont(new Font("SansSerif", Font.PLAIN, 16));
                pnlMain.add(lblInstallMed, CC.xy(1, 5));

                btnInstallMed.setToolTipText(SYSTools.toHTMLForScreen("opde.initwizard.page.create.install.medcontent.tooltip"));
                pnlMain.add(btnInstallMed, CC.xy(3, 5));

                pnlMain.add(btnCreateDB, CC.xyw(1, 7, 5));

                pnlMain.add(pbProgress, CC.xyw(1, 9, 5));

                pnlMain.add(vertical, CC.xyw(1, 11, 5, CC.DEFAULT, CC.FILL));

            }

            addComponent(pnlMain, true);
        }

        protected void createDB() {


            worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    String catalog = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG, "opde"));
                    String server = jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST);
                    String port = jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_PORT);
                    String dbuser = jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_USER);
                    String generatedPassword4DBUser = RandomStringUtils.random(10, 0, 0, true, true, null, new SecureRandom());
                    String generatedPassword4AdminUser = RandomStringUtils.random(6, 0, 0, true, true, null, new SecureRandom());
                    String outcome = "";

                    btnCreateDB.setEnabled(false);

                    try {
                        // https://github.com/tloehr/Offene-Pflege.de/issues/52
                        jdbcProps.put(SYSPropsTools.KEY_JDBC_PASSWORD, OPDE.getEncryption().encrypt(generatedPassword4DBUser));

                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.BACK);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.CANCEL);

                        Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(server, port, null), txtAdmin.getText().trim(), new String(txtPassword.getPassword()).trim());

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
                        summary.add(SYSTools.xx("opde.initwizard.summary.createdb.grant", dbuser, generatedPassword4DBUser));


                        // "Flush Privileges" ist nicht nötig. http://stackoverflow.com/a/36464093

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
                        logger.info(SYSTools.xx("opde.initwizard.page.createdb.success"));

                        pbProgress.setStringPainted(false);
                        pbProgress.setValue(0);
                        fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
                    } catch (SQLException sql) {
                        logger.error(sql);
                        fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.CANCEL);
                        btnCreateDB.setEnabled(true);
                    }

                    return outcome;
                }

                @Override
                protected void done() {
                    super.done();
                    try {
                        creationResultPage = get().toString();
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


    private class SituationPage extends DefaultWizardPage {

        public SituationPage(String title, String description) {
            super(title, description);
            setupWizardButtons();


            addPageListener(pageEvent -> {
                if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                    helpKey = "opde.initwizard.page.situation.helpurl";
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
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(db_version == DB_VERSION.DB_VERSION_TOO_HIGH ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.CANCEL);
        }

        private String check() {
            String result = SYSConst.html_h2("opde.initwizard.page.situation.header");
            String items = "";
            for (String item : situation) {
                items += SYSConst.html_li(item);
            }
            result += SYSConst.html_ul(items);
            if (db_version != DB_VERSION.DB_VERSION_TOO_HIGH)
                result += SYSConst.html_paragraph("opde.initwizard.page.situation.footer");

            return result;
        }

    }


    private class SelectionPage extends DefaultWizardPage {
        private JButton btnCreateNewSchema;
        private JButton btnUpdateSchema;

        public SelectionPage(String title, String description) {
            super(title, description);
            setupWizardButtons();

            addPageListener(pageEvent -> {
                if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                    pageCreateDB.setPageEnabled(db_version != DB_VERSION.DBVERSION_TOO_LOW);
                    pageUpgradeDB.setPageEnabled(db_version == DB_VERSION.DBVERSION_TOO_LOW);
                    helpKey = "opde.initwizard.page.selection.helpurl";
                }
            });
        }


        @Override
        protected void initContentPane() {
            super.initContentPane();

            JPanel pnl = new JPanel();
            pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));

            btnCreateNewSchema = new JButton(SYSTools.xx("opde.initwizard.page.update.createdb"), SYSConst.icon48createDB);
            btnCreateNewSchema.addActionListener(e1 -> {
                setCurrentPage(SYSTools.xx("opde.initwizard.page.createdb.title"));
            });
            btnCreateNewSchema.setToolTipText(SYSTools.xx("opde.initwizard.page.connection.testfirst"));
            btnUpdateSchema = new JButton(SYSTools.xx("opde.initwizard.page.update.updatedb"), SYSConst.icon48updateDB);
            btnUpdateSchema.addActionListener(e1 -> {
                setCurrentPage(SYSTools.xx("opde.initwizard.page.upgradedb.title"));
            });
            btnUpdateSchema.setToolTipText(SYSTools.xx("opde.initwizard.page.connection.testfirst"));
            btnUpdateSchema.setEnabled(db_version == DB_VERSION.DBVERSION_TOO_LOW);
            btnCreateNewSchema.setFont(new Font("SansSerif", Font.PLAIN, 20));
            btnUpdateSchema.setFont(new Font("SansSerif", Font.PLAIN, 20));

            pnl.add(btnCreateNewSchema);
            pnl.add(btnUpdateSchema);
            btnUpdateSchema.setEnabled(db_version == DB_VERSION.DBVERSION_TOO_LOW);

            String recommendation = "";

            if (db_version == DB_VERSION.DBVERSION_TOO_LOW) {
                recommendation = "opde.initwizard.page.selection.recommend.update";
            } else {
                recommendation = "opde.initwizard.page.selection.recommend.createdb";
            }

            JLabel lblRecommendation = new JLabel(SYSTools.toHTMLForScreen(recommendation));
            lblRecommendation.setIcon(SYSConst.icon48getInfo);
            lblRecommendation.setFont(new Font("SansSerif", Font.PLAIN, 20));
            lblRecommendation.setForeground(SYSConst.orangered);

            pnl.add(Box.createVerticalGlue());
            pnl.add(lblRecommendation);

            addComponent(pnl, true);
            addSpace();
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.CANCEL);
        }


    }

    private class BackupPage extends DefaultWizardPage {
        JButton btnSearchMysqlDump;
        JButton btnDBBackup;
        JLabel lblMysqldump;
        JLabel lblBackupdir;
        JLabel lblCatalog;
        JTextField txtCatalog;
        JButton btnSearchBackupdir;
        SwingWorker worker;
        JLabel lblRoot;
        JTextField txtMysqldump;
        JTextField txtBackupdir;
        JLabel lblPassword;
        JTextField txtAdmin;
        JPasswordField txtPassword;
        JTextPane txtComments;
        Logger logger = Logger.getLogger(getClass());
        JPanel pnlMain;

        public BackupPage(String title, String description) {
            super(title, description);
            setupWizardButtons();
            addPageListener(pageEvent -> {
                if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                    helpKey = "opde.initwizard.page.backup.helpurl";
                }
                if (pageEvent.getID() == PageEvent.PAGE_CLOSED) {
                    adminPassword = new String(txtPassword.getPassword()).trim();
                }
            });

        }


        @Override
        protected void initContentPane() {

            super.initContentPane();
            pnlMain = new JPanel();

            lblRoot = new JLabel();
            lblRoot = new JLabel();
            lblCatalog = new JLabel();
            lblPassword = new JLabel();
            txtAdmin = new JTextField();
            txtCatalog = new JTextField();
            txtPassword = new JPasswordField();
            txtComments = new JTextPane();
            txtComments.setEditable(false);
            txtComments.setFont(new Font("MonoSpaced", Font.PLAIN, 12));
            lblMysqldump = new JLabel();
            lblBackupdir = new JLabel();
            txtMysqldump = new JTextField();
            txtBackupdir = new JTextField();

            logger.addAppender(new StatusMessageAppender(txtComments));
            btnDBBackup = new JButton(SYSTools.xx("opde.initwizard.page.backup.dobackup"), SYSConst.icon48updateDB);
            btnDBBackup.setFont(new Font("SansSerif", Font.PLAIN, 20));
            btnDBBackup.addActionListener(e -> {
                if (txtMysqldump.getText().isEmpty()) return;
                if (worker != null && !worker.isDone()) return;
                worker = new SwingWorker() {

                    String sBackupFile = txtBackupdir.getText() + File.separator + jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG) + "-backup-" + System.currentTimeMillis() + ".dump";


                    private void watch(final Process process) {
                        new Thread(() -> {
                            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            String line = null;
                            try {
                                while ((line = input.readLine()) != null) {
                                    logger.info(line);
                                }
                            } catch (IOException e12) {
                                e12.printStackTrace();
                                logger.error(e12);
                            }
                        }).start();
                    }

                    @Override
                    protected Object doInBackground() throws Exception {

                        txtAdmin.setEnabled(false);
                        txtPassword.setEnabled(false);
                        txtMysqldump.setEnabled(false);
                        txtBackupdir.setEnabled(false);
                        txtCatalog.setEnabled(false);
                        btnDBBackup.setEnabled(false);
                        btnSearchMysqlDump.setEnabled(false);
                        btnSearchBackupdir.setEnabled(false);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.BACK);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.CANCEL);

                        Map map = new HashMap();

                        map.put("host", jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST));
                        map.put("port", jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_PORT));
                        map.put("user", txtAdmin.getText().trim());
                        map.put("pw", new String(txtPassword.getPassword()).trim());
                        map.put("file", sBackupFile);
                        map.put("catalog", jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG));

                        String execFilename = txtMysqldump.getText();

                        String password = new String(txtPassword.getPassword()).trim();

                        ProcessBuilder builder = new ProcessBuilder(execFilename,
                                "-v",
                                "--opt",
                                String.format("-h%s", jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST)),
                                String.format("-u%s", txtAdmin.getText().trim()),
                                String.format("-P%s", jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_PORT)),
                                // damit provoziere ich eine Fehlermeldung. Wenn das Passwort leer ist, geht mysqldump in den interaktiven Modus und fragt ein PW ab. Dann hängt der Process.
                                String.format("-p%s", password.isEmpty() ? "adksjdks112d" : password),
                                String.format("-r%s", sBackupFile),
                                jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG)
                        );

                        builder.redirectErrorStream(true);
                        final Process process = builder.start();
                        watch(process);

                        while (process.isAlive()) {
                            Thread.sleep(1000);
                        }

                        return process.exitValue();

                    }

                    @Override
                    protected void done() {
                        try {
                            int exitValue = (int) get();
                            logger.info("Exit Value: " + exitValue);

                            if (exitValue == 0) {

                                logger.info(SYSTools.xx("opde.initwizard.page.backup.targetfile", sBackupFile));
                                summary.add(SYSTools.xx("opde.initwizard.page.backup.targetfile", sBackupFile));
                            } else {
                                logger.info(SYSTools.xx("misc.msg.error"));
                                logger.info(SYSTools.xx("!! Datenbank wurde nicht gesichert."));
                                summary.add(SYSTools.xx("!! Datenbank wurde nicht gesichert."));
                            }
                        } catch (Exception e1) {
                            logger.error(e1);
                        } finally {
                            txtAdmin.setEnabled(true);
                            txtPassword.setEnabled(true);
                            btnDBBackup.setEnabled(true);
                            btnSearchMysqlDump.setEnabled(true);
                            txtMysqldump.setEnabled(true);
                            txtBackupdir.setEnabled(true);
                            txtCatalog.setEnabled(true);
                            btnSearchBackupdir.setEnabled(true);
                            setupWizardButtons();
                        }
                        super.done();
                    }
                };
                worker.execute();
            });

            btnSearchMysqlDump = GUITools.getTinyButton("opde.initwizard.page.backup.mysqldump", SYSConst.icon22exec);
            btnSearchMysqlDump.addActionListener(e -> {
                if (worker != null && !worker.isDone()) return;


                JFileChooser jfc = new JFileChooser(jdbcProps.getProperty(SYSPropsTools.KEY_MYSQLDUMP_EXEC));
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jfc.setMultiSelectionEnabled(false);
                if (jfc.showOpenDialog(pnlMain) == JFileChooser.APPROVE_OPTION) {
                    File mysqldump = jfc.getSelectedFile();

                    if (mysqldump.exists()) {
                        OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_MYSQLDUMP_EXEC, mysqldump.getAbsolutePath());
                        txtMysqldump.setText(mysqldump.getAbsolutePath());
                        logger.info(SYSTools.xx("opde.initwizard.page.backup.current.mysqldump", mysqldump));
                    } else {
                        txtMysqldump.setText("");
                    }
                }

            });

            btnSearchBackupdir = GUITools.getTinyButton("opde.initwizard.page.backup.backupdir", SYSConst.icon22exec);
            btnSearchBackupdir.addActionListener(e -> {
                if (worker != null && !worker.isDone()) return;

                JFileChooser jfc = new JFileChooser(jdbcProps.getProperty(SYSPropsTools.KEY_MYSQLDUMP_DIRECTORY));
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jfc.setMultiSelectionEnabled(false);
                if (jfc.showOpenDialog(pnlMain) == JFileChooser.APPROVE_OPTION) {
                    File mysqldumpdir = jfc.getSelectedFile();

                    if (mysqldumpdir.exists()) {
                        OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_MYSQLDUMP_DIRECTORY, mysqldumpdir.getAbsolutePath());
                        txtBackupdir.setText(mysqldumpdir.getAbsolutePath());
                        logger.info(SYSTools.xx("opde.initwizard.page.backup.backupdir", mysqldumpdir));
                    } else {
                        txtBackupdir.setText("");
                    }
                }

            });


            pnlMain.setLayout(new FormLayout(
                    "default, $ugap, default:grow, $ugap, default",
                    "7*(default, $lgap), fill:default:grow, $lgap, default"));

            //---- lblServer ----
            lblRoot.setText(SYSTools.xx("opde.initwizard.db.root.user"));
            lblRoot.setHorizontalAlignment(SwingConstants.RIGHT);
            lblRoot.setFont(new Font("SansSerif", Font.PLAIN, 16));
            pnlMain.add(lblRoot, CC.xy(1, 1));

            //---- txtServer ----

            txtAdmin.getDocument().addDocumentListener(new RelaxedDocumentListener(var1 -> {
                jdbcProps.put(SYSPropsTools.KEY_JDBC_ROOTUSER, txtAdmin.getText().trim().isEmpty() ? "root" : txtAdmin.getText().trim());
            }));
            txtAdmin.setFont(new Font("SansSerif", Font.PLAIN, 16));
            txtAdmin.setText(SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_ROOTUSER), "root"));
            pnlMain.add(txtAdmin, CC.xy(3, 1));

            //---- lblPort ----
            lblPassword.setText(SYSTools.xx("opde.initwizard.db.root.password"));
            lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
            lblPassword.setFont(new Font("SansSerif", Font.PLAIN, 16));
            pnlMain.add(lblPassword, CC.xy(1, 3));

            //---- txtPort ----
            txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 16));
            pnlMain.add(txtPassword, CC.xy(3, 3));

            lblCatalog.setText(SYSTools.xx("opde.initwizard.db.catalog"));
            lblCatalog.setHorizontalAlignment(SwingConstants.RIGHT);
            lblCatalog.setFont(new Font("SansSerif", Font.PLAIN, 16));
            pnlMain.add(lblCatalog, CC.xy(1, 5));

            txtCatalog.setFont(new Font("SansSerif", Font.PLAIN, 16));
            txtCatalog.setText(SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG), "opde"));
            pnlMain.add(txtCatalog, CC.xy(3, 5));

            lblMysqldump.setText(SYSTools.xx("opde.initwizard.page.backup.mysqldump"));
            lblMysqldump.setHorizontalAlignment(SwingConstants.RIGHT);
            lblMysqldump.setFont(new Font("SansSerif", Font.PLAIN, 16));
            pnlMain.add(lblMysqldump, CC.xy(1, 7));

            txtMysqldump.setFont(new Font("SansSerif", Font.PLAIN, 16));
            txtMysqldump.setText(SYSTools.catchNull(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_MYSQLDUMP_EXEC), ""));

            txtMysqldump.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    String mysqldump = txtMysqldump.getText().trim();
                    if (new File(mysqldump).exists()) {
                        OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_MYSQLDUMP_EXEC, mysqldump);
                    } else {
                        txtMysqldump.setText(System.getProperty(""));
                    }
                }
            });

            pnlMain.add(txtMysqldump, CC.xy(3, 7));
            pnlMain.add(btnSearchMysqlDump, CC.xy(5, 7));

            lblBackupdir.setText(SYSTools.xx("opde.initwizard.page.backup.backupdir"));
            lblBackupdir.setHorizontalAlignment(SwingConstants.RIGHT);
            lblBackupdir.setFont(new Font("SansSerif", Font.PLAIN, 16));
            pnlMain.add(lblBackupdir, CC.xy(1, 9));

            txtBackupdir.setFont(new Font("SansSerif", Font.PLAIN, 16));
            txtBackupdir.setText(SYSTools.catchNull(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_MYSQLDUMP_DIRECTORY), System.getProperty("java.io.tmpdir")));

            txtBackupdir.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    String mysqldump = txtBackupdir.getText().trim();
                    if (new File(mysqldump).exists()) {
                        OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_MYSQLDUMP_DIRECTORY, mysqldump);
                    } else {
                        txtBackupdir.setText(System.getProperty("java.io.tmpdir"));
                    }
                }
            });

            pnlMain.add(txtBackupdir, CC.xy(3, 9));
            pnlMain.add(btnSearchBackupdir, CC.xy(5, 9));

            pnlMain.add(btnDBBackup, CC.xyw(1, 11, 5));
            pnlMain.add(new JScrollPane(txtComments), CC.xywh(1, 13, 5, 3, CC.DEFAULT, CC.FILL));

            addComponent(pnlMain, true);
            addSpace();
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.CANCEL);
        }


    }

    private class CompletionPage extends CompletionWizardPage {

        public CompletionPage(String title, String description) {
            super(title, description);
            setupWizardButtons();
            addPageListener(pageEvent -> {
                if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                    helpKey = "opde.initwizard.page.summary.helpurl";
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
