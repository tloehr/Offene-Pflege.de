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
import entity.info.Resident;
import entity.system.SYSPropsTools;
import gui.GUITools;
import gui.events.RelaxedDocumentListener;
import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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


    private Resident resident;
    //    private DatabaseConnectionBean dbcb;
    private String clearpassword;
    private Logger logger;
    private String pingResult = "";

    private ArrayList<String> stuffThatAnnoysMe;

    private AbstractWizardPage pageWelcome;
    private AbstractWizardPage pageConnection;
    private AbstractWizardPage pageCreateDB;
    private AbstractWizardPage pageUpgradeDB;
    private AbstractWizardPage pageCompletion;

    private String mysqldump = SYSTools.catchNull(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_MYSQLDUMP_EXEC), "/usr/local/mysql/bin/mysqldump");

    // this Map contains the current entries of the user during the lifetime of the wizard
    // after succesful completion the settings are copied over to the OPDE.localProperties and
    // eventually saved to opde.cfg
    private Properties jdbcProps;
    private WizardDialog thisWizard;

    public InitWizard() {
        super(new JFrame(), false);
        thisWizard = this;
        setResizable(true);

        jdbcProps = new Properties();
        logger = Logger.getLogger(getClass());
        stuffThatAnnoysMe = new ArrayList<>();
//        dbcb = new DatabaseConnectionBean(OPDE.getLocalProps());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        DriverManager.setLoginTimeout(2);

//        analyzeSituation();

        // if there is nothing to do, DO NOTHING

        createWizard();

    }

//    public void upgradeDatabase(Connection jdbcConnection) throws SQLException {
//
//        int neededVersion = OPDE.getAppInfo().getDbversion();
//        int currentVersion = EntityTools.getDatabaseSchemaVersion(jdbcConnection);
//
//
//
//        final StringBuilder txt = new StringBuilder();
//        txt.append(Main.lang.getString("tab.dbconnection.db.versionupgrade.progress") + "\n");
//
//        int maxProgress = 0;
//        for (int dbversion = currentDBVersion + 1; dbversion <= latestRevision.getDbstructure(); dbversion++) {
//            maxProgress += ufp.getMapVersion2UpgradeList().get(dbversion).size();
//        }
//        pbSQL.setMaximum(maxProgress);
//        pbSQL.setMinimum(0);
//
//        SwingWorker worker = new SwingWorker() {
//            @Override
//            protected Object doInBackground() throws Exception {
//                int progress = 0;
//                PreparedStatement stmt;
//
//                btnFixDB.setEnabled(false);
//                btnLockServer.setEnabled(false);
//
//                try {
//                    for (int dbversion = currentDBVersion + 1; dbversion <= latestRevision.getDbstructure(); dbversion++) {
//                        if (ufp.getMapVersion2UpgradeList().containsKey(dbversion)) {
//                            txt.append(dbversion - 1 + " >> " + dbversion + "\n");
//                            for (String upgradeTableCommand : ufp.getMapVersion2UpgradeList().get(dbversion)) {
//                                pbSQL.setValue(progress);
//                                progress++;
//                                txt.append(upgradeTableCommand + "\n");
//                                Main.logger.debug(upgradeTableCommand);
//                                stmt = jdbcConnection.prepareStatement(upgradeTableCommand);
//                                int result = stmt.executeUpdate();
//                                txt.append(Main.lang.getString("misc.msg.result") + " " + (result == 0 ? "OK" : result + " " + Main.lang.getString("tab.dbconnection.db.rows.affected")) + "\n");
//                                txtDBTest.setText(txt.toString());
//                                scrollPane1.getVerticalScrollBar().setValue(scrollPane1.getVerticalScrollBar().getMaximum());
//                            }
//                            SYSTools.setDBVersion(jdbcConnection, dbversion);
//                            txt.append("\n\n");
//                            txtDBTest.setText(txt.toString());
//                            scrollPane1.getVerticalScrollBar().setValue(scrollPane1.getVerticalScrollBar().getMaximum());
//                        }
//                    }
//                } catch (Exception ex) {
//                    Main.fatal(ex);
//                }
//
//                return null;
//            }
//
//            @Override
//            protected void done() {
//                super.done();
//                txt.append(Main.lang.getString("tab.dbconnection.db.versionupgrade.complete"));
//
//                btnLockServer.setEnabled(true);
//                btnLockServer.setSelected(false);
//
//                JTextArea textArea = new JTextArea(txt + "\n" + Main.lang.getString("misc.msg.restart.required"));
//                JScrollPane scrollPane = new JScrollPane(textArea);
//                textArea.setLineWrap(true);
//                textArea.setWrapStyleWord(true);
//                scrollPane.setPreferredSize(new Dimension(500, 500));
//
//                JOptionPane.showMessageDialog(null, scrollPane, SYSTools.getAppTitle(), JOptionPane.INFORMATION_MESSAGE, SYSTools.icon64opde);
//                System.exit(0);
//            }
//        };
//
//        worker.execute();
//
//    }

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
//                if (wizard.closeCurrentPage(wizard.getButtonPanel().getButtonByName(ButtonNames.FINISH))) {
//                    save();
//                }
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
                        logger.debug("Nö du");
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


//    public WizardDialog getWizard() {
//        return wizard;
//    }

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

        private JTextArea txtComments;

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

            txtComments = new JTextArea();
            txtComments.setWrapStyleWord(true);
            txtComments.setLineWrap(true);
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

                //---- txtUser ----
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
            addComponent(pnlDB);


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
        JLabel lblRoot;
        JLabel lblPassword;
        JLabel lblMysqldump;
        JTextField txtUser;
        JTextField txtPassword;
        JTextArea txtComments;
        JScrollPane vertical;

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


            lblRoot = new JLabel();
            lblPassword = new JLabel();
            txtUser = new JTextField();
            txtPassword = new JTextField();
            txtComments = new JTextArea();
            lblMysqldump = new JLabel();

            txtComments.setWrapStyleWord(true);
            txtComments.setLineWrap(true);
            txtComments.setEditable(false);
            vertical = new JScrollPane(txtComments);

            txtComments.append(SYSTools.xx("opde.initwizard.current.mysqldump") + ": " + mysqldump + "\n");
            txtComments.append(SYSTools.xx("opde.initwizard.update.target") + ": " + EntityTools.getJDBCUrl(server, sPort, catalog) + "\n");

            btnDBBackup = new JButton(SYSTools.xx("opde.initwizard.update.backupdb"));
            btnDBBackup.addActionListener(e -> {
                SwingWorker worker = new SwingWorker() {
                    String sBackupFile = System.getProperty("java.io.tmpdir") + File.separator + catalog + "-backup-" + System.currentTimeMillis() + ".dump";

                    @Override
                    protected Object doInBackground() throws Exception {

                        txtUser.setEnabled(false);
                        txtPassword.setEnabled(false);
                        btnDBBackup.setEnabled(false);
                        btnSearchMysqlDump.setEnabled(false);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.BACK);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.FINISH);
                        fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.CANCEL);


                        Map map = new HashMap();

                        map.put("host", jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST));
                        map.put("user", txtUser.getText().trim());
                        map.put("pw", txtPassword.getText().trim());
                        map.put("file", sBackupFile);
                        map.put("catalog", jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG));
                        CommandLine cmdLine = CommandLine.parse(mysqldump + " -v --opt -h ${host} -u ${user} -p${pw} -r ${file} ${catalog}");

                        cmdLine.setSubstitutionMap(map);
                        DefaultExecutor executor = new DefaultExecutor();
                        executor.setExitValue(0);

                        OutputStream output = new OutputStream() {
                            private StringBuilder string = new StringBuilder();

                            @Override
                            public void write(int b) throws IOException {
//                                logger.debug(new Character((char) b).toString());
                                txtComments.append(new Character((char) b).toString());
                                vertical.getVerticalScrollBar().setValue(vertical.getVerticalScrollBar().getMaximum());
                            }
                        };
                        executor.setStreamHandler(new PumpStreamHandler(output));
                        return executor.execute(cmdLine);
                    }

                    @Override
                    protected void done() {
                        try {
                            txtComments.append("Exitvalue: " + get() + "\n");
                            txtComments.append(SYSTools.xx("opde.initwizard.update.backupdb.targetfile") + ": " + sBackupFile + "\n");
                            vertical.getVerticalScrollBar().setValue(vertical.getVerticalScrollBar().getMaximum());
                        } catch (Exception e1) {
                            OPDE.fatal(e1);
                        } finally {
                            txtUser.setEnabled(true);
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

            btnSearchMysqlDump = GUITools.getTinyButton("opde.initwizard.update.search.mysqldump", SYSConst.icon22exec);
            btnSearchMysqlDump.addActionListener(e -> {
                final JidePopup popup = new JidePopup();
                final FileChooserPanel fcp = new FileChooserPanel(mysqldump);
                fcp.addItemListener(e1 -> {
                    if (e1.getStateChange() == ItemEvent.SELECTED) {
                        mysqldump = e1.getItem().toString();
                        txtComments.append(SYSTools.xx("opde.initwizard.current.mysqldump") + ": " + mysqldump + "\n");
                        vertical.getVerticalScrollBar().setValue(vertical.getVerticalScrollBar().getMaximum());
                        lblMysqldump.setText(mysqldump);
                        logger.info(SYSTools.xx("opde.initwizard.current.mysqldump") + ": " + mysqldump);
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
            btnOpenBackupDir.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(new File(System.getProperty("java.io.tmpdir")));
                        }
                    } catch (IOException ioe) {
                        txtComments.append(ioe.getMessage());
                        vertical.getVerticalScrollBar().setValue(vertical.getVerticalScrollBar().getMaximum());
                    }
                }
            });


            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            //======== pnlDB ========
            {

                pnlMain.setLayout(new FormLayout(
                        "default, $ugap, default:grow, $ugap, default",
                        "5*(default, $lgap), fill:default:grow, $lgap, default"));

                //---- lblServer ----
                lblRoot.setText(SYSTools.xx("opde.initwizard.root.user"));
                lblRoot.setHorizontalAlignment(SwingConstants.RIGHT);
                lblRoot.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlMain.add(lblRoot, CC.xy(1, 1));

                //---- txtServer ----
                txtUser.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlMain.add(txtUser, CC.xy(3, 1));

                //---- lblPort ----
                lblPassword.setText(SYSTools.xx("opde.initwizard.root.password"));
                lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
                lblPassword.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlMain.add(lblPassword, CC.xy(1, 3));

                //---- txtPort ----
                txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
                pnlMain.add(txtPassword, CC.xy(3, 3));

                lblMysqldump.setText(mysqldump);
                lblMysqldump.setHorizontalAlignment(SwingConstants.LEADING);
                lblMysqldump.setFont(new Font("Arial", Font.PLAIN, 16));

                JPanel buttonLine1 = new JPanel();
                buttonLine1.setLayout(new BoxLayout(buttonLine1, BoxLayout.LINE_AXIS));
                buttonLine1.add(btnDBBackup);
                buttonLine1.add(btnSearchMysqlDump);
                buttonLine1.add(lblMysqldump);
                buttonLine1.add(btnOpenBackupDir);

                pnlMain.add(buttonLine1, CC.xyw(1, 7, 5));
//                pnlMain.add(btnDBBackup, CC.xyw(1, 9, 5));

                pnlMain.add(vertical, CC.xyw(1, 11, 5, CC.DEFAULT, CC.FILL));

            }
            addComponent(pnlMain);


//            pnlMain.add(new JButton("Datenbank sperren"));

//            pnlMain.add(new JButton("Datenbank sichern"));
//            pnlMain.add(new JButton("Datenbank-Schema aktualisieren"));
//            pnlMain.add(new JButton("Datenbank freigeben"));

            addComponent(pnlMain, true);
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

        }

        @Override
        protected void initContentPane() {
            super.initContentPane();

            JTextPane txt = new JTextPane();
            txt.setEditable(false);
            txt.setContentType("text/html");
            txt.setOpaque(false);
            txt.setText(SYSTools.toHTML(SYSConst.html_div(check())));

            addComponent(txt, true);
            addSpace();

        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.CANCEL);
        }

        private String check() {
            String result = "<b>" + SYSTools.xx("opde.admin.bw.wizard.page7.summaryline1") + "</b><br/>";
//            result += SYSTools.xx("opde.admin.bw.wizard.page7.summaryline2") + "<br/>";
//            result += "<ul>";
//            result += "<li>" + ResidentTools.getFullName(resident) + "</li>";
//            result += "<li>" + SYSTools.xx("misc.msg.dob") + ": " + DateFormat.getDateInstance().format(resident.getDOB()) + "</li>";
//            result += "<li>" + SYSTools.xx("misc.msg.primaryNurse") + ": " + (resident.getPN1() == null ? SYSTools.xx("misc.msg.noentryyet") : resident.getPN1().getFullname()) + "</li>";
//            result += "<li>" + SYSTools.xx("misc.msg.gp") + ": " + GPTools.getFullName(resident.getGP()) + "</li>";
////            result += "<li>" + SYSTools.xx("misc.msg.lc") + ": " + LCustodianTools.getFullName(resident.getLCustodian1()) + "</li>";
//
////            result += "<li>" + SYSTools.xx("misc.msg.movein") + ": " + DateFormat.getDateInstance().format(resinfo_hauf.getFrom()) + "</li>";
////            result += "<li>" + SYSTools.xx("misc.msg.room") + ": " + (resinfo_room == null ? SYSTools.xx("misc.msg.noentryyet") : resinfo_room.toString()) + "</li>";
//            result += "<li>" + SYSTools.xx("misc.msg.subdivision") + ": " + resident.getStation().getName() + "</li>";
//
//            result += "</ul>";
//
//            result += "<p>" + SYSTools.xx("opde.admin.bw.wizard.page7.summaryline3") + "</p>";
            return result;
        }


    }


    private class StatusMessageAppender extends AppenderSkeleton {
        private final JTextArea jTextA;
        private PatternLayout defaultPatternLayout = new PatternLayout("%d{ISO8601} %-5p: %m%n");

        public StatusMessageAppender(JTextArea jTextA) {
            this.jTextA = jTextA;

        }

        protected void append(LoggingEvent event) {
            if (event.getLevel().isGreaterOrEqual(Logger.getRootLogger().getLevel())) {
                jTextA.append(defaultPatternLayout.format(event));
                JScrollPane scrl = (JScrollPane) jTextA.getParent().getParent();
                scrl.getVerticalScrollBar().setValue(scrl.getVerticalScrollBar().getMaximum());
            }
        }

        public void close() {
        }

        @Override
        public boolean requiresLayout() {
            return true;
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
