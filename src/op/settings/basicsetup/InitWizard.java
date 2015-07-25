package op.settings.basicsetup;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.dialog.*;
import com.jidesoft.wizard.AbstractWizardPage;
import com.jidesoft.wizard.CompletionWizardPage;
import com.jidesoft.wizard.DefaultWizardPage;
import com.jidesoft.wizard.WizardDialog;
import entity.EntityTools;
import entity.info.ResInfo;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.prescription.GPTools;
import entity.system.SYSPropsTools;
import gui.events.RelaxedDocumentListener;
import op.OPDE;
import op.tools.DesEncrypter;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
    private boolean db_parameters_complete;
    private boolean db_server_pingable;
    private boolean db_server_connected;
    private boolean db_catalog_exists;
    private boolean db_password_readable;
    private boolean db_credentials_correct;

    private int port = 3306;


    private Resident resident;
    //    private DatabaseConnectionBean dbcb;
    private String clearpassword;
    private Logger logger;
    private String pingResult = "";

    private ArrayList<String> stuffThatAnnoysMe;

    private ResInfo resinfo_hauf, resinfo_room;

    public InitWizard() {
        super(new JFrame(), false);

        logger = Logger.getLogger(getClass());
        stuffThatAnnoysMe = new ArrayList<>();
//        dbcb = new DatabaseConnectionBean(OPDE.getLocalProps());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


//        analyzeSituation();

        // if there is nothing to do, DO NOTHING

        createWizard();

    }

    public boolean isDatabaseOK() {
        return db_version == DB_VERSION.PERFECT; // if thats true, EVERYTHING is true;
    }

    private void createWizard() {
        PageList model = new PageList();

        AbstractWizardPage page1 = new WelcomePage(SYSTools.xx("opde.initwizard.page1.title"), SYSTools.xx("opde.initwizard.page1.description"));
//        AbstractWizardPage page2 = new ComplainPage(SYSTools.xx("opde.initwizard.page2.title"), SYSTools.xx("opde.initwizard.page2.description"));
        AbstractWizardPage page3 = new ConnectionPage(SYSTools.xx("opde.initwizard.page3.title"), SYSTools.xx("opde.initwizard.page3.description"));

        // all these situations cause the paramters to be checked or entered
        if (!db_parameters_complete || !db_server_pingable || !db_password_readable || !db_server_connected || !db_catalog_exists) {

        }

//        AbstractWizardPage page2 = new BasisInfoPage(SYSTools.xx("opde.admin.bw.wizard.page2.title"), SYSTools.xx("opde.admin.bw.wizard.page2.description"));
//        AbstractWizardPage page3 = new PNPage(SYSTools.xx("opde.admin.bw.wizard.page3.title"), SYSTools.xx("opde.admin.bw.wizard.page3.description"));
//        AbstractWizardPage page4 = new GPPage(SYSTools.xx("opde.admin.bw.wizard.page4.title"), SYSTools.xx("opde.admin.bw.wizard.page4.description"));
////        AbstractWizardPage page5 = new LCPage(SYSTools.xx(PnlLC.internalClassID + ".title"), SYSTools.xx(PnlLC.internalClassID + ".description"));
//        AbstractWizardPage page6 = new HaufPage(SYSTools.xx("opde.admin.bw.wizard.page6.title"), SYSTools.xx("opde.admin.bw.wizard.page6.description"));
//        AbstractWizardPage page7 = new CompletionPage(SYSTools.xx("opde.admin.bw.wizard.page7.title"), SYSTools.xx("opde.admin.bw.wizard.page7.description"));

        model.append(page1);
//        model.append(page2);
        model.append(page3);
//        model.append(page4);
////        model.append(page5);
//        model.append(page6);
//        model.append(page7);

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

        ((JPanel) getContentPane()).setBorder(new LineBorder(Color.BLACK, 1));
        pack();
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

        public ConnectionPage(String title, String description) {
            super(title, description);
            // we consider the worst case first.
            db_version = DB_VERSION.UNKNOWN;
            db_parameters_complete = false;
            db_server_pingable = false;
            db_server_connected = false;
            db_catalog_exists = false;
            db_password_readable = false;
            db_credentials_correct = false;
        }

        @Override
        public void setupWizardButtons() {
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(db_server_pingable && db_server_connected && db_catalog_exists ? ButtonEvent.ENABLE_BUTTON : ButtonEvent.DISABLE_BUTTON, ButtonNames.NEXT);
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

            serverListener = new RelaxedDocumentListener(documentEvent -> {
                db_server_pingable = false;
                if (port < 1 || port > 65535) {
                    //                    lblPortState.setText("Port muss eine ganze Zahl zwischen 1 und 65535 sein. Standard: 3306");
                    txtPort.setText("3306");
                    port = 3306;
                }

//                if (txtServer.getText().isEmpty()) {
//                    lblServerState.setText(SYSTools.xx("javax.validation.constraints.NotNull.message"));
//                    return;
//                }

                try {
                    logger.info("pinging");
                    pingResult = SYSTools.socketping(txtServer.getText(), Integer.toString(port));
                    logger.info("success");
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
            portListener = new RelaxedDocumentListener(documentEvent -> {
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

//                if (txtServer.getText().isEmpty()) {
//                    lblServerState.setText(SYSTools.xx("javax.validation.constraints.NotNull.message"));
//                    lblServerState.setIcon(SYSConst.icon16ledRedOn);
//                    return;
//                }

                try {
                    logger.info("pinging");
                    pingResult = SYSTools.socketping(txtServer.getText(), Integer.toString(port));
                    logger.info("success");
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
                db_credentials_correct = false;
//                if (!db_server_pingable) {
//                    lblUserState.setText(SYSTools.xx("cant.check.yet"));
//                    lblUserState.setIcon(SYSConst.icon16ledYellowOn);
//                    return;
//                }
//
//                if (txtUser.getText().isEmpty()) {
//                    lblUserState.setText(SYSTools.xx("javax.validation.constraints.NotNull.message"));
//                    lblUserState.setIcon(SYSConst.icon16ledRedOn);
//                    return;
//                }

                try {
                    Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(txtServer.getText().trim(), Integer.toString(port), null), txtUser.getText(), txtPassword.getText());
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
                } catch (SQLException e) {
                    logger.warn(e);
                    db_credentials_correct = false;
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
            passwordListener = new RelaxedDocumentListener(documentEvent -> {
                db_credentials_correct = false;
//                if (!db_server_pingable) {
//                    lblPasswordState.setText(SYSTools.xx("cant.check.yet"));
//                    lblPasswordState.setIcon(SYSConst.icon16ledYellowOn);
//                    return;
//                }
//
//                if (txtPassword.getText().isEmpty()) {
//                    lblPasswordState.setText(SYSTools.xx("javax.validation.constraints.NotNull.message"));
//                    lblPasswordState.setIcon(SYSConst.icon16ledRedOn);
//                    return;
//                }

                try {
                    Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(txtServer.getText().trim(), Integer.toString(port), null), txtUser.getText(), txtPassword.getText());
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
                    catalogListener.check(null);
                } catch (SQLException e) {
                    logger.warn(e);
                    db_credentials_correct = false;
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
                if (!db_credentials_correct) {
                    SwingUtilities.invokeLater(() -> {
                        lblCatState.setText(SYSTools.xx("cant.check.yet"));
                        lblCatState.setIcon(SYSConst.icon16ledYellowOn);
                        revalidate();
                        repaint();
                    });

                    return;
                }
//
//                if (txtCatalog.getText().isEmpty()) {
//                    lblCatState.setText(SYSTools.xx("javax.validation.constraints.NotNull.message"));
//                    lblCatState.setIcon(SYSConst.icon16ledRedOn);
//                    return;
//                }

                try {
                    Connection jdbcConnection = DriverManager.getConnection(EntityTools.getJDBCUrl(txtServer.getText().trim(), Integer.toString(port), txtCatalog.getText().trim()), txtUser.getText(), txtPassword.getText());
                    db_credentials_correct = true;

                    SwingUtilities.invokeLater(() -> {
                        lblCatState.setText(SYSTools.xx("misc.msg.ok"));
                        lblCatState.setIcon(SYSConst.icon16ledGreenOn);
                        revalidate();
                        repaint();
                    });
                    jdbcConnection.close();
                } catch (SQLException e) {
                    logger.warn(e);
                    db_credentials_correct = false;

                    SwingUtilities.invokeLater(() -> {
                        lblCatState.setText(SYSTools.xx("error.ping.failed"));
                        lblCatState.setIcon(SYSConst.icon16ledRedOn);
                        revalidate();
                        repaint();
                    });
                }

            });

            txtServer.getDocument().addDocumentListener(serverListener);
            txtPort.getDocument().addDocumentListener(portListener);
            txtUser.getDocument().addDocumentListener(userListener);
            txtPassword.getDocument().addDocumentListener(passwordListener);
            txtCatalog.getDocument().addDocumentListener(catalogListener);


            //======== this ========

            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            //======== pnlDB ========
            {
                pnlDB.setBackground(new Color(238, 238, 238));
                pnlDB.setLayout(new FormLayout(
                        "default, $ugap, default:grow, $ugap, default",
                        "5*(default, $lgap), default"));

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

                pnlDB.add(lblTesting, CC.xyw(1, 11, 5));


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


    }


//    private class LCPage extends DefaultWizardPage {
//        private PnlLC pnlLC;
////        private boolean alreadyexecute = false;
//
//        public LCPage(String title, String description) {
//            super(title, description);
//            pnlLC = new PnlLC(new Closure() {
//                @Override
//                public void execute(Object o) {
//                    resident.setLCustodian1((LCustodian) o);
//                    setupWizardButtons();
//                }
//            });
//            addPageListener(new PageListener() {
//                @Override
//                public void pageEventFired(PageEvent pageEvent) {
//                    if (pageEvent.getID() != PageEvent.PAGE_OPENED) return;
//                    setupWizardButtons();
//                }
//            });
//            setupWizardButtons();
//        }
//
//        @Override
//        public void setupWizardButtons() {
//            super.setupWizardButtons();
//
//            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
//            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
//            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
//            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
//        }
//
//        @Override
//        protected void initContentPane() {
//            super.initContentPane();
//            addComponent(pnlLC, true);
//        }
//    }


    private class CompletionPage extends CompletionWizardPage {
        public CompletionPage(String title, String description) {
            super(title, description);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() != PageEvent.PAGE_OPENED) return;
                    setupWizardButtons();
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

            addComponent(txt, true);
            addSpace();

        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        private String check() {
            String result = "<b>" + SYSTools.xx("opde.admin.bw.wizard.page7.summaryline1") + "</b><br/>";
            result += SYSTools.xx("opde.admin.bw.wizard.page7.summaryline2") + "<br/>";
            result += "<ul>";
            result += "<li>" + ResidentTools.getFullName(resident) + "</li>";
            result += "<li>" + SYSTools.xx("misc.msg.dob") + ": " + DateFormat.getDateInstance().format(resident.getDOB()) + "</li>";
            result += "<li>" + SYSTools.xx("misc.msg.primaryNurse") + ": " + (resident.getPN1() == null ? SYSTools.xx("misc.msg.noentryyet") : resident.getPN1().getFullname()) + "</li>";
            result += "<li>" + SYSTools.xx("misc.msg.gp") + ": " + GPTools.getFullName(resident.getGP()) + "</li>";
//            result += "<li>" + SYSTools.xx("misc.msg.lc") + ": " + LCustodianTools.getFullName(resident.getLCustodian1()) + "</li>";

            result += "<li>" + SYSTools.xx("misc.msg.movein") + ": " + DateFormat.getDateInstance().format(resinfo_hauf.getFrom()) + "</li>";
            result += "<li>" + SYSTools.xx("misc.msg.room") + ": " + (resinfo_room == null ? SYSTools.xx("misc.msg.noentryyet") : resinfo_room.toString()) + "</li>";
            result += "<li>" + SYSTools.xx("misc.msg.subdivision") + ": " + resident.getStation().getName() + "</li>";

            result += "</ul>";

            result += "<p>" + SYSTools.xx("opde.admin.bw.wizard.page7.summaryline3") + "</p>";
            return result;
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
