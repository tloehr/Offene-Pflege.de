/*
 * OffenePflege
 * Copyright (C) 2006-2017 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License V2 as published by the Free Software Foundation
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht,
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 */
package de.offene_pflege.op;

import com.jidesoft.utils.Lm;
import com.jidesoft.wizard.WizardStyle;
import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.files.SYSFilesTools;
import de.offene_pflege.entity.nursingprocess.DFNTools;
import de.offene_pflege.entity.prescription.BHPTools;
import de.offene_pflege.entity.system.*;
import de.offene_pflege.op.settings.InitWizard;
import de.offene_pflege.op.system.AppInfo;
import de.offene_pflege.op.system.EMailSystem;
import de.offene_pflege.op.system.LogicalPrinters;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.PrintProcessor;
import de.offene_pflege.op.tools.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.swing.*;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.UUID;

/*! \mainpage My Personal Index Page
 *
 * \section intro_sec Introduction
 *
 * This is the introduction.
 *
 * \section install_sec Installation
 *
 * \subsection step1 Step 1: Opening the box
 *
 * etc...
 */
@Log4j2
public class OPDE {
    public static final String internalClassID = "opde";

    public static final int INFO_TIME = 2;
    public static final int WARNING_TIME = 4;
    public static final int ERROR_TIME = 4;
    public static final int START_OF_MODULE_TIME = 6;
    public static final int DEFAULT_SCREEN_RESFRESH_MILLIS = 50;
    public static final int DEFAULT_DOCUMENT_LISTENER_REACTION_TIME_IN_MILLIS = 500;

    private static long uptime;
    public static ResourceBundle lang;
    private static Encryption encryption;
    public static FrmMain mainframe;
    protected static String url;
    protected static LogicalPrinters printers;
    protected static Properties props;
    protected static SortedProperties localProps;
    protected static Properties jpaProps = new Properties();
    public static HashMap[] anonymize = null;
    protected static EntityManagerFactory emf;
    protected static AppInfo appInfo;
    protected static SYSLogin login;
    protected static ValidatorFactory validatorFactory;
    protected static boolean animation = false;
//    protected static boolean debug;
    protected static boolean experimental;
    protected static String css = SYSConst.fallbackCSS;
    private static int DEFAULT_TIMEOUT = 30;
    protected static final String sep = System.getProperty("file.separator");
    private static boolean customJDBCUrl;
    private static boolean runningInstanceDetected;
    private static boolean anonym;

    /**
     * @return Das Arbeitsverzeichnis für OPDE.
     */
    public static String getOPWD() {
        return LocalMachine.getAppDataPath();
    }

    public static long getUPTime() {
        return uptime;
    }

    public static LogicalPrinters getLogicalPrinters() {
        return printers;
    }

    public static int getErrorMessageTime() {
        return ERROR_TIME;
    }

    public static boolean isAnimation() {
        return animation;
    }

    /**
     * @return zeigt an, ob die Medikamente gerechnet werden sollen oder nicht.
     */
    public static boolean isCalcMediUPR1() {
        return SYSPropsTools.isBooleanTrue(SYSPropsTools.KEY_CALC_MEDI_UPR1);
    }

    public static boolean isCalcMediOther() {
        return false;
    }

    public static boolean isAnonym() {
        //return SYSTools.catchNull(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_RESIDENTS_ANONYMIZED)).equalsIgnoreCase("true");
        return anonym;
    }

    /**
     * Sollen die Namen der Benutzer durch die Chiffre ersetzt werden ?
     *
     * @return
     */
    public static boolean isUserCipher() {
        return SYSTools.catchNull(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_USERS_CIPHERED)).equalsIgnoreCase("true");
    }

    public static void setProp(String key, String value) {
        props.put(key, value);
    }

    public static String getCSS() {
        return css;
    }

//    public static EntityManagerFactory getEMF() {
//        return emf;
//    }

    public static void closeEMF() {
        if (emf == null) return;
        if (emf.isOpen()) emf.close();
        emf = null;
        return;
    }

    public static void clearCache() {
        if (emf == null || !emf.isOpen()) return;
        emf.getCache().evictAll();
        return;
    }


    public static boolean isFTPworking() {
        return SYSTools.catchNull(props.getProperty(SYSPropsTools.KEY_FTP_IS_WORKING)).equalsIgnoreCase("true");
    }


    public static boolean isCustomJDBCUrl() {
        return customJDBCUrl;
    }

    public static String getUrl() {
        return url;
    }

    public static void initProps() {
        if (OPDE.getProps() != null) {
            OPDE.getProps().clear();
        }
        OPDE.getProps().putAll(SYSPropsTools.loadProps(null));
        OPDE.getProps().putAll(SYSPropsTools.loadProps(login.getUser()));
        OPDE.getProps().putAll(OPDE.getLocalProps());
    }

//    public static Logger getLogger() {
//        return logger;
//    }
//
//    public static void warn(Throwable message) {
//        warn(logger, message);
//    }
//
//    public static void warn(Logger classLogger, Throwable message) {
//        classlog.warn(message);
//        if (emf != null)
//            SyslogTools.warn(ExceptionUtils.getMessage(message) + ": " + ExceptionUtils.getStackTrace(message));
//    }
//
//    public static void info(Object message) {
//        log.info(message);
//    }
//
//    public static void important(Object message) {
//        log.info(message);
//        SyslogTools.info(message.toString());
//    }

    public static ValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }

    //    public static void important(EntityManager em, Object message) throws Exception {
//        log.info(message);
//        SyslogTools.addLog(em, message.toString(), SyslogTools.INFO);
//    }
//
//    public static void fatal(Throwable e) {
//        fatal(logger, e);
//    }
//
    @SneakyThrows
    public static void fatal(Throwable e) {
        if (emf != null && emf.isOpen()) {
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                SyslogTools.addLog(em, e.getMessage(), SyslogTools.FATAL);
                em.getTransaction().commit();
            } catch (Exception ee) {
                em.getTransaction().rollback();
                ee.printStackTrace();
            } finally {
                em.close();
            }
            e.printStackTrace();
        }

        String html = SYSTools.getThrowableAsHTML(e);
        File temp = SYSFilesTools.createTempFile("opde", ".html");
        temp.deleteOnExit();
        SYSFilesTools.print(html, temp, false, true);

        if (System.getProperties().getOrDefault("loglevel","INFO").equals("INFO")) {
            EMailSystem.sendErrorMail(e.getMessage(), temp);
        }

        System.exit(1);
    }

    public static EntityManager createEM() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory("OPDEPU", jpaProps);
        }
        return emf.createEntityManager();
    }


    public static Properties getProps() {
        return props;
    }

    public static Properties getLocalProps() {
        return localProps;
    }

    /**
     * returns the minutes until the system timeouts the current login automatically when no user action is detected. if
     * the timeout value is 0, no timeout is performed. the timeout value is 0, no timeout is performed.
     *
     * @return
     */
    public static int getTimeout() {
        int timeout = DEFAULT_TIMEOUT;
        if (localProps.containsKey("timeout")) {
            try {
                timeout = Integer.parseInt(localProps.getProperty("timeout"));
            } catch (NumberFormatException nfe) {
                timeout = DEFAULT_TIMEOUT;
            }
        }
        return timeout;
    }

    public static void setTimeout(int timeout) {
        localProps.setProperty("timeout", Integer.toString(timeout));
        getDisplayManager().setTimeoutmins(timeout);
        saveLocalProps();
    }

    public static void saveLocalProps() {
        try {
            File configFile = new File(LocalMachine.getAppDataPath() + sep + AppInfo.fileConfig);
            FileOutputStream out = new FileOutputStream(configFile);
            localProps.store(out, "Settings Offene-Pflege.de");

            out.close();
        } catch (Exception ex) {
            fatal(ex);
            System.exit(1);
        }
    }


    public static AppInfo getAppInfo() {
        return appInfo;
    }

    public static SYSLogin getLogin() {
        return login;
    }

    public static OPUsers getMe() {
        if (login == null) return null;
        return login.getUser();
    }

    public static void setLogin(SYSLogin login) {
        OPDE.login = login;
    }

    public static boolean isExperimental() {
        return experimental;
    }


    public static Encryption getEncryption() {
        return encryption;
    }

    /**
     * Hier ist die main Methode von OPDE. In dieser Methode wird auch festgestellt, wie OPDE gestartet wurde.
     * <ul>
     * <li>Im Standard Modus, das heisst mit graphischer Oberfläche. Das dürfte der häufigste Fall sein.</li>
     * <li>Im DFNImport Modus. Der wird meist auf dem Datenbankserver gebraucht um Nachts die Durchführungsnachweise
     * anhand der DFNImport Tabelle zu generieren. Das alles gehört zu der Pflegeplanung.</li>
     * <li>Im BHPImport Modus. Auch dieser Modus wird auf dem DB-Server gebraucht um die Behandlungspflege Massnahmen
     * anhand der ärztlichen Verordnungen zu generieren.</li>
     * </ul>
     *
     * @param args Hier stehen die Kommandozeilen Parameter. Diese werden mit
     */
    public static void main(String[] args) throws Exception {
        /***
         *
         *              ____
         *            ,'  , `.
         *         ,-+-,.' _ |              ,--,
         *      ,-+-. ;   , ||            ,--.'|         ,---,
         *     ,--.'|'   |  ;|            |  |,      ,-+-. /  |
         *    |   |  ,', |  ':  ,--.--.   `--'_     ,--.'|'   |
         *    |   | /  | |  || /       \  ,' ,'|   |   |  ,"' |
         *    '   | :  | :  |,.--.  .-. | '  | |   |   | /  | |
         *    ;   . |  ; |--'  \__\/: . . |  | :   |   | |  | |
         *    |   : |  | ,     ," .--.; | '  : |__ |   | |  |/
         *    |   : '  |/     /  /  ,.  | |  | '.'||   | |--'
         *    ;   | |`-'     ;  :   .'   \;  :    ;|   |/
         *    |   ;/         |  ,     .-./|  ,   / '---'
         *    '---'           `--`---'     ---`-'
         *
         */


//        System.out.println("yoy");

        if (!System.getProperties().containsKey("workspace")) {
            log.fatal("workspace directory parameter needs to be set via -Dworkspace=/path/you/want/without-app-dir/with-closing-separator/");
            Runtime.getRuntime().halt(0);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Shutdown Hook")));

        FileUtils.forceMkdir(new File(AppInfo.getOPCache()));
        FileUtils.forceMkdir(new File(AppInfo.getUserTemplatePath()));
        FileUtils.forceMkdir(new File(LocalMachine.getLogPath()));
        System.setProperty("logs", LocalMachine.getLogPath());

        uptime = SYSCalendar.now();

        lang = ResourceBundle.getBundle("languageBundle", new CustomResourceBundleControl("UTF-8"));

        validatorFactory = Validation.buildDefaultValidatorFactory();

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> OPDE.fatal(e));

        localProps = new SortedProperties();
        props = new Properties();
        appInfo = new AppInfo();

        // JideSoft
        Lm.verifyLicense("Torsten Loehr", "Open-Pflege.de", "G9F4JW:Bm44t62pqLzp5woAD4OCSUAr2");
        WizardStyle.setStyle(WizardStyle.JAVA_STYLE);

        // anonmyous
        anonymize = new HashMap[]{SYSConst.getNachnamenAnonym(), SYSConst.getVornamenFrauAnonym(), SYSConst.getVornamenMannAnonym()};


        /***
         *       ____                                          _   _     _               ___        _   _
         *      / ___|___  _ __ ___  _ __ ___   __ _ _ __   __| | | |   (_)_ __   ___   / _ \ _ __ | |_(_) ___  _ __  ___
         *     | |   / _ \| '_ ` _ \| '_ ` _ \ / _` | '_ \ / _` | | |   | | '_ \ / _ \ | | | | '_ \| __| |/ _ \| '_ \/ __|
         *     | |__| (_) | | | | | | | | | | | (_| | | | | (_| | | |___| | | | |  __/ | |_| | |_) | |_| | (_) | | | \__ \
         *      \____\___/|_| |_| |_|_| |_| |_|\__,_|_| |_|\__,_| |_____|_|_| |_|\___|  \___/| .__/ \__|_|\___/|_| |_|___/
         *                                                                                   |_|
         */
        Options opts = new Options();
        opts.addOption("h", "help", false, SYSTools.xx("cmdline.help.description"));
        opts.addOption("v", "version", false, SYSTools.xx("cmdline.version.description"));
        opts.addOption("x", "experimental", false, SYSTools.xx("cmdline.experimental.description"));
//        opts.addOption("l", "debug", false, SYSTools.xx("cmdline.debug.description"));
        opts.addOption("a", "anonymous", false, SYSTools.xx("cmdline.anonymous.description"));
        opts.addOption("t", "setup-database", false, SYSTools.xx("cmdline.setup-database.description"));
        opts.addOption("c", "enable-cache", false, SYSTools.xx("cmdline.enable-cache.description"));
        opts.addOption("p", "keyphrase", true, SYSTools.xx("cmdline.keyphrase.description"));
        opts.addOption("e", "encrypt-key", true, SYSTools.xx("cmdline.encrypt.key.description"));
        opts.addOption("j", "jdbc", true, SYSTools.xx("cmdline.jdbc.description"));


        Option dfnimport = Option.builder().longOpt("dfnimport").optionalArg(true)
                .desc(SYSTools.xx("cmdline.dfnimport.description")).option("d").build();
        opts.addOption(dfnimport);

        Option bhpimport = Option.builder().longOpt("bhpimport").optionalArg(true)
                .desc(SYSTools.xx("cmdline.bhpimport.description")).option("b").build();
        opts.addOption(bhpimport);

        DefaultParser parser = new DefaultParser();
        CommandLine cl = null;
        String footer = "https://www.Offene-Pflege.de" + " " + OPDE.getAppInfo().getBuildInformation();

        /***
         *      _          _
         *     | |__   ___| |_ __    ___  ___ _ __ ___  ___ _ __
         *     | '_ \ / _ \ | '_ \  / __|/ __| '__/ _ \/ _ \ '_ \
         *     | | | |  __/ | |_) | \__ \ (__| | |  __/  __/ | | |
         *     |_| |_|\___|_| .__/  |___/\___|_|  \___|\___|_| |_|
         *                  |_|
         */
        try {
            cl = parser.parse(opts, args);
        } catch (ParseException ex) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp("OffenePflege.jar [OPTION]", "Offene-Pflege.de, Version " + appInfo.getVersion(), opts, footer);
            System.exit(0);
        }

        if (cl.hasOption("h")) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp("OffenePflege.jar [OPTION]", "Offene-Pflege.de, Version " + appInfo.getVersion(), opts, footer);
            System.exit(0);
        }


        /***
         *     __     __            _
         *     \ \   / /__ _ __ ___(_) ___  _ __
         *      \ \ / / _ \ '__/ __| |/ _ \| '_ \
         *       \ V /  __/ |  \__ \ | (_) | | | |
         *        \_/ \___|_|  |___/_|\___/|_| |_|
         *
         */
        String header = SYSTools.getWindowTitle("");
        if (cl.hasOption("v")) {
            System.out.println(header);
            System.out.println(appInfo.getVersionVerbose());
            System.out.println(footer);
            System.exit(0);
        }

        /***
         *                                                                ___
         *       __ _ _ __   ___  _ __  _   _ _ __ ___   ___  _   _ ___  |__ \
         *      / _` | '_ \ / _ \| '_ \| | | | '_ ` _ \ / _ \| | | / __|   / /
         *     | (_| | | | | (_) | | | | |_| | | | | | | (_) | |_| \__ \  |_|
         *      \__,_|_| |_|\___/|_| |_|\__, |_| |_| |_|\___/ \__,_|___/  (_)
         *                              |___/
         */
        if (cl.hasOption("a")) { // anonym Modus
            anonym = true;
            anonymize = new HashMap[]{SYSConst.getNachnamenAnonym(), SYSConst.getVornamenFrauAnonym(), SYSConst.getVornamenMannAnonym()};
        } else {
            anonym = false;
        }


        try {

            printers = new LogicalPrinters(); //  wenn die printers.xml fehlt, kommt es hierdurch zur exception

            loadLocalProperties();

            // different encryption keyphrase ?
            if (cl.hasOption("p")) {
                encryption = new Encryption(cl.getOptionValue("p"));
            } else {
                encryption = new Encryption();
            }

            //generate a key and exit
            if (cl.hasOption("e")) {
                System.out.println(encryption.encrypt(cl.getOptionValue("e")));
                System.exit(0);
            }


            try {
                css = SYSTools.readFileAsString(AppInfo.getTemplate(AppInfo.fileStandardCSS).getAbsolutePath());
            } catch (IOException ie) {
                css = SYSConst.fallbackCSS;
            }

            animation = SYSTools.catchNull(localProps.getProperty(SYSPropsTools.KEY_ANIMATION)).equals("true");

            log.info("######### START ###########  " + OPDE.getAppInfo().getProgname() + ", v" + OPDE.getAppInfo().getVersion());
            log.info(System.getProperty("os.name").toLowerCase());

//
//            if (cl.hasOption("l") || SYSTools.catchNull(localProps.getProperty(SYSPropsTools.KEY_DEBUG)).equalsIgnoreCase("true")) {
//                debug = true;
//;
//            }


            if (cl.hasOption("x") || SYSTools.catchNull(localProps.getProperty(SYSPropsTools.KEY_EXPERIMENTAL)).equalsIgnoreCase("true")) {
                experimental = true;

            } else {
                experimental = false;
            }


            if (cl.hasOption("t"))
                throw new PersistenceException("user forces the database setup");

            /***
             *          _ ____   _      ____        _        _
             *         | |  _ \ / \    |  _ \  __ _| |_ __ _| |__   __ _ ___  ___
             *      _  | | |_) / _ \   | | | |/ _` | __/ _` | '_ \ / _` / __|/ _ \
             *     | |_| |  __/ ___ \  | |_| | (_| | || (_| | |_) | (_| \__ \  __/
             *      \___/|_| /_/   \_\ |____/ \__,_|\__\__,_|_.__/ \__,_|___/\___|
             *
             */

            jpaProps.putAll(localProps);

            customJDBCUrl = cl.hasOption("j");
            url = customJDBCUrl ? cl.getOptionValue("j") : EntityTools.getJDBCUrl(localProps.getProperty(SYSPropsTools.KEY_JDBC_HOST), localProps.getProperty(SYSPropsTools.KEY_JDBC_PORT), localProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG));
            log.info(url);

            jpaProps.put(SYSPropsTools.KEY_JDBC_URL, url);
            jpaProps.put(SYSPropsTools.KEY_JDBC_PASSWORD, encryption.decryptJDBCPasswort());  // this is a temporarily clear version of the password.

            // enable JPA cache
            jpaProps.put("eclipselink.cache.shared.default", cl.hasOption("c") ? "true" : "false");
            jpaProps.put("eclipselink.session.customizer", "de.offene_pflege.entity.JPAEclipseLinkSessionCustomizer");

            Connection jdbcConnection = DriverManager.getConnection(url, jpaProps.getProperty(SYSPropsTools.KEY_JDBC_USER), jpaProps.getProperty(SYSPropsTools.KEY_JDBC_PASSWORD));
            int neededVersion = OPDE.getAppInfo().getDbversion();
            int currentVersion = EntityTools.getDatabaseSchemaVersion(jdbcConnection);
            jdbcConnection.close();

            if (neededVersion != currentVersion)
                throw new PersistenceException(SYSTools.xx("error.sql.schema.version.mismatch"));

//            createEM();

            /***
             *       ____                           _         ____  _____ _   _
             *      / ___| ___ _ __   ___ _ __ __ _| |_ ___  |  _ \|  ___| \ | |___
             *     | |  _ / _ \ '_ \ / _ \ '__/ _` | __/ _ \ | | | | |_  |  \| / __|
             *     | |_| |  __/ | | |  __/ | | (_| | ||  __/ | |_| |  _| | |\  \__ \
             *      \____|\___|_| |_|\___|_|  \__,_|\__\___| |____/|_|   |_| \_|___/
             *
             */
            if (cl.hasOption("d")) {
                EntityManager em = OPDE.createEM();

                try {
                    em.getTransaction().begin();
                    OPUsers rootUser = em.find(OPUsers.class, "admin");

                    SYSLogin rootLogin = em.merge(new SYSLogin(rootUser));
                    OPDE.setLogin(rootLogin);
                    initProps();

                    // create the new DFNs
                    DFNTools.generate(em);
                    // move over the floating ones that have not yet been clicked to the current day
                    DFNTools.moveFloating(em);

                    em.getTransaction().commit();
                } catch (Exception ex) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    fatal(ex);
                } finally {
                    em.close();
                }
                System.exit(0);
            }

            /***
             *       ____                           _         ____  _   _ ____
             *      / ___| ___ _ __   ___ _ __ __ _| |_ ___  | __ )| | | |  _ \ ___
             *     | |  _ / _ \ '_ \ / _ \ '__/ _` | __/ _ \ |  _ \| |_| | |_) / __|
             *     | |_| |  __/ | | |  __/ | | (_| | ||  __/ | |_) |  _  |  __/\__ \
             *      \____|\___|_| |_|\___|_|  \__,_|\__\___| |____/|_| |_|_|   |___/
             *
             */
            if (cl.hasOption("b")) {

                EntityManager em = OPDE.createEM();

                try {
                    em.getTransaction().begin();
                    OPUsers rootUser = em.find(OPUsers.class, "admin");

                    SYSLogin rootLogin = em.merge(new SYSLogin(rootUser));
                    OPDE.setLogin(rootLogin);
                    initProps();

                    BHPTools.generate(em);

                    em.getTransaction().commit();
                } catch (Exception ex) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    fatal(ex);
                } finally {
                    em.close();
                }
                System.exit(0);
            }

            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            setStandardFont();

            /***
             *      _____               __  __       _        ____
             *     |  ___| __ _ __ ___ |  \/  | __ _(_)_ __  / /\ \
             *     | |_ | '__| '_ ` _ \| |\/| |/ _` | | '_ \| |  | |
             *     |  _|| |  | | | | | | |  | | (_| | | | | | |  | |
             *     |_|  |_|  |_| |_| |_|_|  |_|\__,_|_|_| |_| |  | |
             *                                               \_\/_/
             */

            mainframe = new FrmMain();
            mainframe.setVisible(true);

        } catch (Exception ioe) {

            if (cl.hasOption("d") || cl.hasOption("b")) {
                log.fatal(ioe);
                System.exit(0);
            }

            // trouble with the setup ?
            // start the init wizard
            if (ioe instanceof SQLException || ioe instanceof PersistenceException || ioe instanceof IOException) {
                log.warn(ioe);
                InitWizard initWizard = new InitWizard();
                SYSTools.center(initWizard);
                initWizard.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        System.exit(0);
                    }
                });
                initWizard.setVisible(true);
            } else {
                fatal(ioe);
                System.exit(0);
            }
        }
    }


    public static DisplayManager getDisplayManager() {
        return mainframe.getDisplayManager();
    }

    public static PrintProcessor getPrintProcessor() {
        return mainframe.getPrintProcessor();
    }

    public static FrmMain getMainframe() {
        return mainframe;
    }

    private static void loadLocalProperties() throws IOException {


        File configFile = new File(LocalMachine.getAppDataPath() + sep + AppInfo.fileConfig);
        log.info("configFile:" + configFile);

        // make sure the file exists
        if (!configFile.exists()) { // path didnt exist yet.

            log.info("configFile: missing");

            // is there an old opde.cfg ?
            // then we should copy it over
            String oldopwd = System.getProperty("user.home") + sep + AppInfo.dirBase;
            File oldConfigFile = new File(oldopwd + sep + AppInfo.fileConfig);

            if (oldConfigFile.exists()) {
                log.info("oldConfigFile found:" + oldConfigFile);
                FileUtils.copyFile(oldConfigFile, configFile);
                log.info("copying over and renaming the old one");
                FileUtils.copyFile(oldConfigFile, new File(oldopwd + sep + AppInfo.fileConfig + ".old"));
                FileUtils.deleteQuietly(oldConfigFile);
            }
        }

        configFile.createNewFile();

        FileInputStream in = new FileInputStream(configFile);
        Properties p = new Properties();
        p.load(in);
        localProps.putAll(p);
        p.clear();
        in.close();

        // minimum requirement
        if (!localProps.containsKey(SYSPropsTools.KEY_STATION)) localProps.put(SYSPropsTools.KEY_STATION, "1");
        if (!localProps.containsKey(SYSPropsTools.KEY_ANIMATION)) localProps.put(SYSPropsTools.KEY_ANIMATION, "true");
        if (!localProps.containsKey(SYSPropsTools.KEY_TMP_DIR))
            localProps.put(SYSPropsTools.KEY_TMP_DIR, System.getProperty("java.io.tmpdir"));
        if (!localProps.containsKey(SYSPropsTools.KEY_HOSTKEY))
            localProps.put(SYSPropsTools.KEY_HOSTKEY, UUID.randomUUID().toString());

    }


//    // http://stackoverflow.com/questions/19082265/how-to-ensure-only-one-instance-of-a-java-program-can-be-executed
//    private static boolean getMonitoredVMs(int processPid) {
//        MonitoredHost host;
//        Set vms;
//        try {
//            host = MonitoredHost.getMonitoredHost(new HostIdentifier((String) null));
//            vms = host.activeVms();
//        } catch (java.net.URISyntaxException sx) {
//            throw new InternalError(sx.getMessage());
//        } catch (MonitorException mx) {
//            throw new InternalError(mx.getMessage());
//        }
//        MonitoredVm mvm = null;
//        String processName = null;
//        try {
//            mvm = host.getMonitoredVm(new VmIdentifier(String.valueOf(processPid)));
//            processName = MonitoredVmUtil.commandLine(mvm);
//            processName = processName.substring(processName.lastIndexOf("\\") + 1, processName.length());
//            mvm.detach();
//        } catch (Exception ex) {
//
//        }
//        // This line is just to verify the process name. It can be removed.
//        JOptionPane.showMessageDialog(null, processName);
//        for (Object vmid : vms) {
//            if (vmid instanceof Integer) {
//                int pid = ((Integer) vmid).intValue();
//                String name = vmid.toString(); // default to pid if name not available
//                try {
//                    mvm = host.getMonitoredVm(new VmIdentifier(name));
//                    // use the command line as the display name
//                    name = MonitoredVmUtil.commandLine(mvm);
//                    name = name.substring(name.lastIndexOf("\\") + 1, name.length());
//                    mvm.detach();
//                    if ((name.equalsIgnoreCase(processName)) && (processPid != pid))
//                        return false;
//                } catch (Exception x) {
//                    // ignore
//                }
//            }
//        }
//
//        return true;
//    }


    public static boolean isAdmin() {
        return UsersTools.isAdmin(login.getUser());
    }

    public static void setStandardFont() {
        UIManager.put("Button.font", SYSConst.ARIAL14);
        UIManager.put("ToggleButton.font", SYSConst.ARIAL14);
        UIManager.put("RadioButton.font", SYSConst.ARIAL14);
        UIManager.put("CheckBox.font", SYSConst.ARIAL14);
        UIManager.put("ColorChooser.font", SYSConst.ARIAL14);
        UIManager.put("ComboBox.font", SYSConst.ARIAL14);
        UIManager.put("Label.font", SYSConst.ARIAL14);
        UIManager.put("List.font", SYSConst.ARIAL14);
        UIManager.put("MenuBar.font", SYSConst.ARIAL14);
        UIManager.put("MenuItem.font", SYSConst.ARIAL14);
        UIManager.put("RadioButtonMenuItem.font", SYSConst.ARIAL14);
        UIManager.put("CheckBoxMenuItem.font", SYSConst.ARIAL14);
        UIManager.put("Menu.font", SYSConst.ARIAL14);
        UIManager.put("PopupMenu.font", SYSConst.ARIAL14);
        UIManager.put("OptionPane.font", SYSConst.ARIAL14);
        UIManager.put("Panel.font", SYSConst.ARIAL14);
        UIManager.put("ProgressBar.font", SYSConst.ARIAL14);
        UIManager.put("ScrollPane.font", SYSConst.ARIAL14);
        UIManager.put("Viewport.font", SYSConst.ARIAL14);
        UIManager.put("TabbedPane.font", SYSConst.ARIAL14);
        UIManager.put("Table.font", SYSConst.ARIAL14);
        UIManager.put("TableHeader.font", SYSConst.ARIAL14);
        UIManager.put("TextField.font", SYSConst.ARIAL14);
        UIManager.put("PasswordField.font", SYSConst.ARIAL14);
        UIManager.put("TextArea.font", SYSConst.ARIAL14);
        UIManager.put("TextPane.font", SYSConst.ARIAL14);
        UIManager.put("EditorPane.font", SYSConst.ARIAL14);
        UIManager.put("TitledBorder.font", SYSConst.ARIAL14);
        UIManager.put("ToolBar.font", SYSConst.ARIAL14);
        UIManager.put("ToolTip.font", SYSConst.ARIAL14);
        UIManager.put("Tree.font", SYSConst.ARIAL14);
    }


}
