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
import entity.system.SYSPropsTools;
import gui.GUITools;
import gui.events.RelaxedDocumentListener;
import op.OPDE;
import op.system.AppInfo;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

    private int port = 3306;
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

        setNextAction(new AbstractAction("Next") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getCurrentPage() instanceof ConnectionPage) {
                    if (((ConnectionPage) getCurrentPage()).isDocumentEventsStillProcessing()) {
                        OPDE.debug("Nö du");
                        return;
                    }
                }
                int index = model.getPageIndexByFullTitle(getCurrentPage().getFullTitle()) + 1;
                while (!model.getPage(index).isPageEnabled()) {
                    index++;
                }

                setCurrentPage(model.getPage(index).getFullTitle());
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


//    private void save() {
//        EntityManager em = OPDE.createEM();
//        try {
//            em.getTransaction().begin();
//
//
//            String prefix = resident.getName().substring(0, 1) + resident.getFirstname().substring(0, 1);
//            prefix = prefix.toUpperCase();
//
//            Unique unique = UniqueTools.getNewUID(em, prefix);
//            String bwkennung = prefix + unique.getUid();
//            resident.setRID(bwkennung);
//
//            resident = em.merge(resident);
//            resinfo_hauf = em.merge(resinfo_hauf);
//            if (resinfo_room != null) em.merge(resinfo_room);
//
//            em.getTransaction().commit();
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(ResidentTools.getTextCompact(resident) + " " + SYSTools.xx("misc.msg.entrysuccessful"), 6));
//
//        } catch (Exception e) {
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//            OPDE.fatal(e);
//        } finally {
//            em.close();
//        }
//    }

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

//        @Override
//        public Image getGraphic() {
//            return getLeftGraphic("/artwork/aspecton1.png");
//        }

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
        private JTextField txtPassword;
        private Logger logger = Logger.getLogger(getClass());

        private JLabel lblServerState;
        private JLabel lblPortState;
        private JLabel lblUserState;
        private JLabel lblPasswordState;
        private JLabel lblCatState;
        private JLabel lblTesting;

        private RelaxedDocumentListener serverListener;
        private RelaxedDocumentListener portListener;
        private RelaxedDocumentListener userListener;
        private RelaxedDocumentListener passwordListener;
        private RelaxedDocumentListener catalogListener;

        private JTextPane txtComments;

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
                    serverListener.check(null);
                    userListener.check(null);
                    catalogListener.check(null);
                }
                if (pageEvent.getID() == PageEvent.PAGE_CLOSED) {
                    jdbcProps.put(SYSPropsTools.KEY_JDBC_HOST, txtServer.getText().trim());
                    jdbcProps.put(SYSPropsTools.KEY_JDBC_PORT, port);
                    jdbcProps.put(SYSPropsTools.KEY_JDBC_USER, txtUser.getText().trim());
                    jdbcProps.put(SYSPropsTools.KEY_JDBC_PASSWORD, txtPassword.getText().trim());
                    jdbcProps.put(SYSPropsTools.KEY_JDBC_CATALOG, txtCatalog.getText().trim());
                }
            });

        }

//        @Override
//        public void reset() {
//            super.reset();
//        }

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

            txtServer = new JTextField();
            txtPort = new JTextField();
            txtCatalog = new JTextField();
            txtUser = new JTextField();

            txtPassword = new JTextField();

            lblPassword = new JLabel();
            lblUser = new JLabel();
            lblServer = new JLabel();
            lblPort = new JLabel();
            lblCat = new JLabel();
            lblTesting = new JLabel("Testing");

            lblServerState = new JLabel(SYSTools.xx("cant.check.yet"), SYSConst.icon16ledYellowOn, SwingConstants.LEADING);
            lblPortState = new JLabel(SYSTools.xx("cant.check.yet"), SYSConst.icon16ledYellowOn, SwingConstants.LEADING);
            lblUserState = new JLabel(SYSTools.xx("cant.check.yet"), SYSConst.icon16ledYellowOn, SwingConstants.LEADING);
            lblPasswordState = new JLabel(SYSTools.xx("cant.check.yet"), SYSConst.icon16ledYellowOn, SwingConstants.LEADING);
            lblCatState = new JLabel(SYSTools.xx("cant.check.yet"), SYSConst.icon16ledYellowOn, SwingConstants.LEADING);

            txtComments = new JTextPane();
//            txtComments.setWrapStyleWord(true);
//            txtComments.setLineWrap(true);
            txtComments.setEditable(false);

            serverListener = new RelaxedDocumentListener(documentEvent -> {
                fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);

                db_version = DB_VERSION.UNKNOWN;
                db_server_pingable = false;


                if (port < 1 || port > 65535) {
                    //                    lblPortState.setText("Port muss eine ganze Zahl zwischen 1 und 65535 sein. Standard: 3306");
                    txtPort.setText("3306");
                    port = 3306;
                }

                try {
                    logger.addAppender(new StatusMessageAppender(txtComments));
                    logger.info("pinging: " + txtServer.getText() + ":" + port);
                    pingResult = SYSTools.socketping(txtServer.getText(), Integer.toString(port));
                    logger.info(pingResult);
                    SwingUtilities.invokeLater(() -> {
                        lblServerState.setText(SYSTools.xx("misc.msg.ok"));
                        lblPortState.setText(SYSTools.xx("misc.msg.ok"));
                        lblServerState.setIcon(SYSConst.icon16ledGreenOn);
                        lblPortState.setIcon(SYSConst.icon16ledGreenOn);
                        revalidate();
                        repaint();
                    });

                    db_server_pingable = true;

                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> {
                        logger.error(e);
                        lblServerState.setText(SYSTools.xx("error.ping.failed"));
                        lblPortState.setText(SYSTools.xx("error.ping.failed"));
                        lblServerState.setIcon(SYSConst.icon16ledRedOn);
                        lblPortState.setIcon(SYSConst.icon16ledRedOn);
                        revalidate();
                        repaint();
                    });

                } finally {
                    portListener.check(null);
                }

            }, documentEvent -> {
                setPagesEnabled();
            });
            portListener = new RelaxedDocumentListener(documentEvent -> {
                fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);

                db_version = DB_VERSION.UNKNOWN;
                db_server_pingable = false;

                try {
                    port = Integer.parseInt(txtPort.getText().trim());
                    if (port < 1 || port > 65535) {
                        txtPort.setText("3306");
                        return;
                    }
                } catch (NumberFormatException nfe) {
                    txtPort.setText("3306");
                    return;
                }

                try {
                    logger.info("pinging: " + txtServer.getText() + ":" + port);
                    pingResult = SYSTools.socketping(txtServer.getText(), Integer.toString(port));
                    logger.info(pingResult);
                    SwingUtilities.invokeLater(() -> {
                        lblServerState.setText(SYSTools.xx("misc.msg.ok"));
                        lblPortState.setText(SYSTools.xx("misc.msg.ok"));
                        lblServerState.setIcon(SYSConst.icon16ledGreenOn);
                        lblPortState.setIcon(SYSConst.icon16ledGreenOn);
                        revalidate();
                        repaint();
                    });
                    db_server_pingable = true;
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> {
                        logger.error(e);
                        lblServerState.setText(SYSTools.xx("error.ping.failed"));
                        lblPortState.setText(SYSTools.xx("error.ping.failed"));
                        lblServerState.setIcon(SYSConst.icon16ledRedOn);
                        lblPortState.setIcon(SYSConst.icon16ledRedOn);
                        revalidate();
                        repaint();
                    });
                } finally {
                    userListener.check(null);
                }
            });
            userListener = new RelaxedDocumentListener(documentEvent -> {
                fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);

                db_version = DB_VERSION.UNKNOWN;

                db_credentials_correct = false;

                try {
                    Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(txtServer.getText().trim(), Integer.toString(port), null), txtUser.getText(), txtPassword.getText());
                    logger.info("jdbc.connection.ok");
                    db_credentials_correct = true;
                    SwingUtilities.invokeLater(() -> {
                        lblUserState.setText(SYSTools.xx("misc.msg.ok"));
                        lblPasswordState.setText(SYSTools.xx("misc.msg.ok"));
                        lblUserState.setIcon(SYSConst.icon16ledGreenOn);
                        lblPasswordState.setIcon(SYSConst.icon16ledGreenOn);
                        revalidate();
                        repaint();
                    });
                    jdbcConnection.close();
                    db_dbms_reachable = true;
                } catch (SQLException e) {
                    logger.error(e);
                    db_credentials_correct = false;
                    db_dbms_reachable = !(e instanceof com.mysql.jdbc.exceptions.jdbc4.CommunicationsException && e.getCause() instanceof java.net.SocketTimeoutException);
                    SwingUtilities.invokeLater(() -> {
                        lblUserState.setText(SYSTools.xx("error.ping.failed"));
                        lblPasswordState.setText(SYSTools.xx("error.ping.failed"));
                        lblUserState.setIcon(SYSConst.icon16ledRedOn);
                        lblPasswordState.setIcon(SYSConst.icon16ledRedOn);
                        revalidate();
                        repaint();
                    });
                } finally {
                    passwordListener.check(null);
                }

            });
            passwordListener = new RelaxedDocumentListener(documentEvent -> {
                fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);


                db_version = DB_VERSION.UNKNOWN;

                db_credentials_correct = false;

                try {
                    Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(txtServer.getText().trim(), Integer.toString(port), null), txtUser.getText(), txtPassword.getText());
                    logger.info("jdbc.connection.ok");
                    db_credentials_correct = true;
                    SwingUtilities.invokeLater(() -> {
                        lblUserState.setText(SYSTools.xx("misc.msg.ok"));
                        lblPasswordState.setText(SYSTools.xx("misc.msg.ok"));
                        lblUserState.setIcon(SYSConst.icon16ledGreenOn);
                        lblPasswordState.setIcon(SYSConst.icon16ledGreenOn);
                        revalidate();
                        repaint();
                    });
                    jdbcConnection.close();
                    db_dbms_reachable = true;
                } catch (SQLException e) {
                    logger.error(e);
                    db_credentials_correct = false;
                    db_dbms_reachable = !(e instanceof com.mysql.jdbc.exceptions.jdbc4.CommunicationsException && e.getCause() instanceof java.net.SocketTimeoutException);
                    SwingUtilities.invokeLater(() -> {
                        lblUserState.setText(SYSTools.xx("error.ping.failed"));
                        lblPasswordState.setText(SYSTools.xx("error.ping.failed"));
                        lblUserState.setIcon(SYSConst.icon16ledRedOn);
                        lblPasswordState.setIcon(SYSConst.icon16ledRedOn);
                        revalidate();
                        repaint();
                    });
                } finally {
                    catalogListener.check(null);

                }

            });
            catalogListener = new RelaxedDocumentListener(documentEvent -> {
                fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);
                db_version = DB_VERSION.UNKNOWN;
//                if (!db_credentials_correct) {
//                    SwingUtilities.invokeLater(() -> {
//                        lblCatState.setText(SYSTools.xx("cant.check.yet"));
//                        lblCatState.setIcon(SYSConst.icon16ledYellowOn);
//                        lblCatState.setToolTipText(null);
//                        revalidate();
//                        repaint();
//                    });
//                    return;
//                }

                try {
                    Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(txtServer.getText().trim(), Integer.toString(port), txtCatalog.getText().trim()), txtUser.getText(), txtPassword.getText());
                    logger.info("jdbc.connection.ok");
                    db_catalog_exists = true;

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

                    SwingUtilities.invokeLater(() -> {
                        lblCatState.setText(SYSTools.xx("misc.msg.ok"));
                        lblCatState.setIcon(SYSConst.icon16ledGreenOn);
                        revalidate();
                        repaint();
                    });
                    jdbcConnection.close();

                } catch (SQLException e) {
                    logger.error(e);
//                    db_credentials_correct = false;

                    SwingUtilities.invokeLater(() -> {
                        lblCatState.setText(SYSTools.xx("error.db.connection.failed"));
                        lblCatState.setToolTipText(SYSTools.xx(e.getMessage()));
                        lblCatState.setIcon(SYSConst.icon16ledRedOn);
                        revalidate();
                        repaint();
                    });
                } finally {
                    setPagesEnabled();
                }

            });

            txtServer.getDocument().addDocumentListener(serverListener);
            txtPort.getDocument().addDocumentListener(portListener);
            txtUser.getDocument().addDocumentListener(userListener);
            txtPassword.getDocument().addDocumentListener(passwordListener);
            txtCatalog.getDocument().addDocumentListener(catalogListener);


            //======== this ========

            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            //======== pnlDB ========
            {
                pnlDB.setBackground(new Color(238, 238, 238));
                pnlDB.setLayout(new FormLayout(
                        "default, $ugap, default:grow, $ugap, default",
                        "5*(default, $lgap), fill:default:grow, $lgap, default"));

                //---- lblServer ----
                lblServer.setText("Server");
                lblServer.setHorizontalAlignment(SwingConstants.RIGHT);
                lblServer.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(lblServer, CC.xy(1, 1));

                //---- txtServer ----
                txtServer.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtServer, CC.xy(3, 1));
                pnlDB.add(lblServerState, CC.xy(5, 1));

                //---- lblPort ----
                lblPort.setText("Port");
                lblPort.setHorizontalAlignment(SwingConstants.RIGHT);
                lblPort.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(lblPort, CC.xy(1, 3));

                //---- txtPort ----
                txtPort.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtPort, CC.xy(3, 3));
                pnlDB.add(lblPortState, CC.xy(5, 3));

                //---- lblUser ----
                lblUser.setText("Benutzer");
                lblUser.setHorizontalAlignment(SwingConstants.RIGHT);
                lblUser.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(lblUser, CC.xy(1, 5));

                //---- txtAdmin ----
                txtUser.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtUser, CC.xy(3, 5));
                pnlDB.add(lblUserState, CC.xy(5, 5));

                //---- lblPassword ----
                lblPassword.setText("Passwort");
                lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
                lblPassword.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(lblPassword, CC.xy(1, 7));

                //---- txtPassword ----
                txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtPassword, CC.xy(3, 7));
                pnlDB.add(lblPasswordState, CC.xy(5, 7));

                //---- lblCat ----
                lblCat.setText("Katalog");
                lblCat.setHorizontalAlignment(SwingConstants.RIGHT);
                lblCat.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(lblCat, CC.xy(1, 9));

                //---- txtCatalog ----
                txtCatalog.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlDB.add(txtCatalog, CC.xy(3, 9));
                pnlDB.add(lblCatState, CC.xy(5, 9));

                pnlDB.add(new JScrollPane(txtComments), CC.xyw(1, 11, 5, CC.DEFAULT, CC.FILL));
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

                // test stuff
//                Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(server, sPort, catalog), txtAdmin.getText().trim(), txtPassword.getText().trim());
//                String sql = "INSERT INTO `opde`.`resinfo` (`AnUKennung`, `AbUKennung`, `BWKennung`, `BWINFTYP`, `Von`, `Bis`, `Bemerkung`, `Properties`, `HTML`, `version`, `resvalueid`, `prescriptionid`) VALUES('tloehr',NULL,'KJ1','ROOM1','2015-04-01 16:09:37','9999-12-31 23:59:59','','#[ROOM1] BewohnerInnen-Zimmer\\n#Thu Mar 26 16:09:40 CET 2015\\nroom.id=29\\nroom.text=Zimmer 22, Etage\\: 2, Seniorenheim Wiedenhof\\n','<ul></ul>','1',NULL,NULL);";
//                PreparedStatement stmt = jdbcConnection.prepareStatement(sql);
//                int result = stmt.executeUpdate();
//                logger.info(SYSTools.xx("misc.msg.result") + " " + (result == 0 ? "OK" : result + " " + SYSTools.xx("opde.initwizard.page.update.updatedb.rows.affected")) + "\n");
//                logger.info(sql.toString());
//                stmt.close();
//                System.exit(0);

//                port = Integer.parseInt(txtPort.getText());

            } catch (BadLocationException e) {
                e.printStackTrace();
            }


        }


        // db_server_pingable == false  -> kein next
        // db_server_pingable == true && db_server_connected == false  -> PAGE_CREATE_DB
        // db_server_pingable == true && db_server_connected == true && db_version == PERFECT  -> PAGE_COMPLETION, Success, Store localprops, quit
        // db_server_pingable == true && db_server_connected == true && db_version == TOO_LOW  -> PAGE_UPDATE_DB
        // db_server_pingable == true && db_server_connected == true && db_version == TOO_HIGH  -> PAGE_COMPLETION, Fail, Update the software, quit


        private void setPagesEnabled() {
            logger.debug("db_version: " + db_version);
            logger.debug("db_server_pingable: " + db_server_pingable);
            logger.debug("db_dbms_reachable: " + db_dbms_reachable);
            fireButtonEvent(db_server_pingable && db_dbms_reachable ? ButtonEvent.ENABLE_BUTTON : ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);
            pageCreateDB.setPageEnabled(db_version == DB_VERSION.UNKNOWN);
            pageUpgradeDB.setPageEnabled(db_version == DB_VERSION.TOO_LOW);
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent((db_version == DB_VERSION.UNKNOWN || db_version == DB_VERSION.TOO_LOW ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON), ButtonNames.FINISH);
        }


        public boolean isDocumentEventsStillProcessing() {
            boolean b = serverListener.isListenerActive() || portListener.isListenerActive() || userListener.isListenerActive() || passwordListener.isListenerActive() || catalogListener.isListenerActive();
            logger.debug("isDocumentEventsStillProcessing: " + b);
            return b;
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
        JTextField txtPassword;
        JTextPane txtComments;
        JScrollPane vertical;
        Logger logger = Logger.getLogger(getClass());
        //        Timer timer;
        Style h1Style, h2Style, h3Style;


        public UpdateDB(String title, String description) {
            super(title, description);
            setupWizardButtons();
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
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
            txtPassword = new JTextField();
            txtComments = new JTextPane();
            lblMysqldump = new JLabel();

            txtAdmin.getDocument().addDocumentListener(new RelaxedDocumentListener(var1 -> {
                jdbcProps.put(SYSPropsTools.KEY_JDBC_ROOTUSER, txtAdmin.getText().trim().isEmpty() ? "root" : txtAdmin.getText().trim());
            }));

            logger.addAppender(new StatusMessageAppender(txtComments));
            h1Style = txtComments.addStyle("h1Style", null);
            StyleConstants.setForeground(h1Style, Color.blue);
            StyleConstants.setBold(h1Style, true);
            StyleConstants.setFontSize(h1Style, 24);

            h2Style = txtComments.addStyle("h2Style", null);
            StyleConstants.setForeground(h2Style, SYSConst.mediumpurple4);
            StyleConstants.setBold(h2Style, true);
            StyleConstants.setFontSize(h2Style, 20);

            h3Style = txtComments.addStyle("h3Style", null);
            StyleConstants.setForeground(h3Style, SYSConst.salmon4);
            StyleConstants.setBold(h3Style, true);
            StyleConstants.setFontSize(h3Style, 16);

            txtComments.setEditable(false);
            vertical = new JScrollPane(txtComments);

            logger.info(SYSTools.xx("opde.initwizard.page.update.current.mysqldump") + ": " + jdbcProps.getProperty(SYSPropsTools.KEY_MYSQLDUMP_EXEC));
            logger.info(SYSTools.xx("opde.initwizard.page.update.target") + ": " + EntityTools.getJDBCUrl(server, sPort, catalog));

            btnLockDB = new JButton(SYSConst.icon22locked);
            btnLockDB.addActionListener(e -> {
                try {
                    lockServer();
                } catch (SQLException e1) {
                    logger.error(e1);
                }
            });

            btnUnLockDB = new JButton(SYSConst.icon22unlocked);
            btnUnLockDB.addActionListener(e2 -> {
                try {
                    unlockServer();
                } catch (SQLException e1) {
                    logger.error(e1);
                }
            });

            btnUpdateDB = new JButton(SYSTools.xx("opde.initwizard.page.update.updatedb"), SYSConst.icon22updateDB);
            btnUpdateDB.setFont(new Font("Arial", Font.PLAIN, 16));
            btnUpdateDB.addActionListener(al -> {
                try {
                    Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(server, sPort, catalog), txtAdmin.getText().trim(), txtPassword.getText().trim());
                    upgradeDatabase(jdbcConnection);
                    jdbcConnection.close();
                    setCurrentPage(SYSTools.xx("opde.initwizard.page.summary.title"));
                } catch (SQLException e) {
                    logger.error(e);
                } catch (IOException e) {
                    logger.error(e);
                }
            });

            btnDBBackup = new JButton(SYSTools.xx("opde.initwizard.page.update.backupdb"));
            btnDBBackup.setFont(new Font("Arial", Font.PLAIN, 16));
            btnDBBackup.addActionListener(e -> {
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
                        map.put("pw", txtPassword.getText().trim());
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

                pnlMain.add(vertical, CC.xyw(1, 15, 5, CC.DEFAULT, CC.FILL));

            }

            addComponent(pnlMain, true);
        }

        private void lockServer() throws SQLException {
            String server = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST));
            String catalog = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG, "opde"));
            String sPort = SYSTools.catchNull(jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_PORT), "3306");

            Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(server, sPort, catalog), txtAdmin.getText().trim(), txtPassword.getText().trim());
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

            Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(server, sPort, catalog), txtAdmin.getText().trim(), txtPassword.getText().trim());
            if (EntityTools.isServerLocked(jdbcConnection)) {
                EntityTools.setServerLocked(jdbcConnection, false);
                jdbcConnection.close();
                logger.info(SYSTools.xx("opde.initwizard.page.update.server.unlocked"));
                summary.add(SYSTools.xx("opde.initwizard.page.update.server.unlocked"));
            } else {
                logger.warn(SYSTools.xx("opde.initwizard.page.update.server.unlocked"));
            }
        }


        private void upgradeDatabase(Connection jdbcConnection) throws SQLException, IOException {
            if (!EntityTools.isServerLocked(jdbcConnection)) {
                logger.error(SYSTools.xx("opde.initwizard.page.update.lock.first"));
                return;
            }

            int neededVersion = 8;//OPDE.getAppInfo().getDbversion();
            int currentVersion = 7;//EntityTools.getDatabaseSchemaVersion(jdbcConnection);


            HashMap<Integer, ArrayList<String>> mapToNewestVersion = new HashMap<>();

//            ArrayList<ArrayList<String>> sqlToNewestVersion = new ArrayList<>();
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

            for (int startVersion = currentVersion; startVersion < neededVersion; startVersion++) {
                GUITools.appendText(SYSTools.xx("opde.initwizard.page.update.updating.version", startVersion, startVersion + 1), txtComments, h1Style);
                summary.add(SYSTools.xx("opde.initwizard.page.update.updating.version", startVersion, startVersion + 1));
                for (String sql : mapToNewestVersion.get(startVersion)) {
                    logger.info(sql.toString());
//                    PreparedStatement stmt = jdbcConnection.prepareStatement(sql);
//                    int result = stmt.executeUpdate();
//                    logger.info(SYSTools.xx("misc.msg.result") + " " + (result == 0 ? "OK" : result + " " + SYSTools.xx("opde.initwizard.page.update.updatedb.rows.affected")) + "\n");
//                    logger.info("=============");
//                    stmt.close();
                }
                EntityTools.setServerLocked(jdbcConnection, false);
                summary.add(SYSTools.xx("opde.initwizard.page.update.server.unlocked"));
            }
        }


//    private String situation2html() {
//        String html = "";
//
//        html += db_parameters_complete ? "" : SYSConst.html_li("opde.initwizard.not.db_parameters_complete");
//        html += db_server_pingable ? "" : SYSConst.html_li("opde.initwizard.not.db_server_pingable");
//        html += db_server_connected ? "" : SYSConst.html_li("opde.initwizard.not.db_server_connected");
//        html += db_catalog_exists ? "" : SYSConst.html_li("opde.initwizard.not.db_catalog_exists");
//        html += db_password_readable ? "" : SYSConst.html_li("opde.initwizard.not.db_password_readable");
//        html += db_credentials_correct ? "" : SYSConst.html_li("opde.initwizard.not.db_credentials_correct");
//
//        if (db_version == DB_VERSION.UNKNOWN) html += SYSConst.html_li("opde.initwizard.db_version_unknown");
//        else if (db_version == DB_VERSION.TOO_LOW) html += SYSConst.html_li("opde.initwizard.db_version_too_low");
//        else if (db_version == DB_VERSION.TOO_HIGH) html += SYSConst.html_li("opde.initwizard.db_version_too_high");
//
//
//        if (!html.isEmpty()) {
//            html = SYSConst.html_ul(html);
//        }
//
//        return html;
//    }


        /**
         * this method checks the availability of the database connection in stages.
         * it sets the 7 boolean in order to describe
         */
//    private void analyzeSituation() {
//
//        // we consider the worst case first.
//        db_version = DB_VERSION.UNKNOWN;
//        db_parameters_complete = false;
//        db_server_pingable = false;
//        db_server_connected = false;
//        db_catalog_exists = false;
//        db_password_readable = false;
//        db_credentials_correct = false;
//
//        // 1. All parameters entered ?
//        Validator validator = OPDE.getValidatorFactory().getValidator();
//        Set<ConstraintViolation<DatabaseConnectionBean>> constraintViolations = validator.validate(dbcb);
//        db_parameters_complete = constraintViolations.isEmpty();
//        constraintViolations.forEach(new Consumer<ConstraintViolation<DatabaseConnectionBean>>() {
//            @Override
//            public void accept(ConstraintViolation<DatabaseConnectionBean> databaseConnectionBeanConstraintViolation) {
//                stuffThatAnnoysMe.add(databaseConnectionBeanConstraintViolation.getPropertyPath().toString() + ": " + databaseConnectionBeanConstraintViolation.getMessage());
//            }
//        });
//        if (!db_parameters_complete) return;
//
//        db_password_readable = true; // we would have not made it here otherwise
//
//        try {
//            pingResult = SYSTools.socketping(dbcb.getHost(), dbcb.getPort().toString());
//            db_server_pingable = true;
//        } catch (IOException e) {
//            pingResult = e.getMessage();
//            stuffThatAnnoysMe.add(pingResult);
//        }
//        if (!db_server_pingable) return;
//
//        try {
//            Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(dbcb.getHost(), dbcb.getPort().toString(), null), dbcb.getUser(), dbcb.getPassword());
//            db_credentials_correct = true;
//            db_server_connected = true;
//
//            jdbcConnection.setCatalog(dbcb.getCatalog());
//            db_catalog_exists = true;
//
//            int neededVersion = OPDE.getAppInfo().getDbversion();
//            int currentVersion = EntityTools.getDatabaseSchemaVersion(jdbcConnection);
//
//            if (currentVersion == -1) db_version = DB_VERSION.UNKNOWN; // tables SYSProps is messed up
//            else if (currentVersion < neededVersion) db_version = DB_VERSION.TOO_LOW;
//            else if (currentVersion > neededVersion) db_version = DB_VERSION.TOO_HIGH;
//            else db_version = DB_VERSION.PERFECT;
//
//            jdbcConnection.close();
//        } catch (SQLException e) {
//            if (e.getMessage().startsWith("Access denied for user")) {
//                db_credentials_correct = false;
//            }
//            db_version = DB_VERSION.UNKNOWN;
//            stuffThatAnnoysMe.add(e.getMessage());
//        }
//
//
//    }


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

        //        private boolean alreadyexecute = false;

        public CreateDBPage(String title, String description) {
            super(title, description);
            setupWizardButtons();
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            JPanel pnlMain = new JPanel();
            pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.PAGE_AXIS));

            pnlMain.add(new JTextField());
            pnlMain.add(new JTextField());
            pnlMain.add(new JButton("Datenbank erstellen"));

            addComponent(pnlMain, true);
        }
    }


    private class CompletionPage extends CompletionWizardPage {
        public CompletionPage(String title, String description) {
            super(title, description);
            setupWizardButtons();
            OPDE.getLocalProps().putAll(jdbcProps);
            OPDE.saveLocalProps();
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
            for (String item : summary){
                items+= SYSConst.html_li(item);
            }
            result += SYSConst.html_ol(items);
            result += SYSConst.html_paragraph("opde.initwizard.page.completion.summary2");
            return result;
        }


    }
}
