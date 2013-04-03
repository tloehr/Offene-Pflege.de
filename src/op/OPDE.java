/*
 * OffenePflege
 * Copyright (C) 2006-2012 Torsten Löhr
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
package op;

import com.jidesoft.utils.Lm;
import com.jidesoft.wizard.WizardStyle;

import entity.nursingprocess.DFNTools;
import entity.prescription.BHPTools;
import entity.system.*;
import op.system.AppInfo;
import op.system.EMailSystem;
import op.system.FrmInit;
import op.system.LogicalPrinters;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.threads.PrintProcessor;
import op.tools.*;
import org.apache.commons.cli.*;
import org.apache.log4j.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class OPDE {
    public static final String internalClassID = "opde";

    public static final int INFO_TIME = 2;
    public static final int WARNING_TIME = 4;
    public static final int ERROR_TIME = 4;
    public static final int START_OF_MODULE_TIME = 6;

    public static long uptime;
    public static ResourceBundle lang;

    public static FrmMain mainframe;
    protected static String url;
    protected static LogicalPrinters printers;
    protected static Properties props;
    protected static boolean anonym;
    protected static SortedProperties localProps;
    protected static Logger logger;
    public static HashMap[] anonymize = null;

    protected static EntityManagerFactory emf;
    protected static AppInfo appInfo;
    protected static SYSLogin login;

    protected static boolean animation = false;
    protected static boolean debug;
    protected static String opwd = "";
    protected static String css = "";

//    protected static boolean FTPisWORKING = false;
    public static String UPDATE_FTPSERVER = "ftp.offene-pflege.de";
    protected static boolean updateAvailable = false;

    protected static final String sep = System.getProperty("file.separator");

    /**
     * @return Das Arbeitsverzeichnis für OPDE.
     */
    public static String getOPWD() {
        return opwd;
    }

    public static boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public static void setUpdateAvailable(boolean updateAvailable) {
        OPDE.updateAvailable = updateAvailable;
    }

    public static String getOPCache() {
        return opwd + sep + AppInfo.dirCache;
    }

    public static LogicalPrinters getLogicalPrinters() {
        return printers;
    }

    public static boolean isDebug() {
        return debug;
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
        return SYSPropsTools.isBooleanTrue(SYSPropsTools.KEY_CALC_MEDI_APV1);
    }

    public static boolean isCalcMediOther() {
        return false;
    }

    public static boolean isAnonym() {
        return anonym;
    }

    public static void setProp(String key, String value) {
        props.put(key, value);
    }

    public static String getCSS() {
        return css;
    }

    public static EntityManagerFactory getEMF() {
        return emf;
    }

    public static boolean isFTPworking() {
        return SYSTools.catchNull(props.getProperty(SYSPropsTools.KEY_FTP_IS_WORKING)).equalsIgnoreCase("true");
    }


    public static boolean isCustomUrl() {
        return !url.equals(localProps.getProperty("javax.persistence.jdbc.url"));

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

    public static Logger getLogger() {
        return logger;
    }

    public static void warn(Throwable message) {
        logger.warn(message);
        SyslogTools.warn(message.toString());
    }

    public static void info(Object message) {
        logger.info(message);
    }

    public static void important(Object message) {
        logger.info(message);
        SyslogTools.info(message.toString());
    }

    public static void important(EntityManager em, Object message) throws Exception {
        logger.info(message);
        SyslogTools.addLog(em, message.toString(), SyslogTools.INFO);
    }

    public static void fatal(Throwable e) {
        logger.fatal(e.getMessage(), e);
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

        if (!isDebug()) {
            EMailSystem.sendErrorMail(e);
        }

        System.exit(1);
    }

    public static void error(Object message) {
        logger.error(message);
        SyslogTools.error(message.toString());
    }

    public static void debug(Object message) {
        logger.debug(message);
    }

    public static EntityManager createEM() {
        return emf.createEntityManager();
    }

    public static Properties getProps() {
        return props;
    }

    public static Properties getLocalProps() {
        return localProps;
    }

    public static void saveLocalProps() {
        try {
            FileOutputStream out = new FileOutputStream(new File(opwd + sep + AppInfo.fileConfig));
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

    public static void setLogin(SYSLogin login) {
        OPDE.login = login;
    }

    /**
     * Hier ist die main Methode von OPDE. In dieser Methode wird auch festgestellt, wie OPDE gestartet wurde.
     * <ul>
     * <li>Im Standard Modus, das heisst mit graphischer Oberfläche. Das dürfte der häufigste Fall sein.</li>
     * <li>Im DFNImport Modus. Der wird meist auf dem Datenbankserver gebraucht um Nachts die Durchführungsnachweise anhand der
     * DFNImport Tabelle zu generieren. Das alles gehört zu der Pflegeplanung.</li>
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
        uptime = SYSCalendar.now();
//        arial14 = new Font("Arial", Font.PLAIN, 14);
//        arial28 = new Font("Arial", Font.PLAIN, 28);

        /***
         *      _                                               ____                  _ _
         *     | |    __ _ _ __   __ _ _   _  __ _  __ _  ___  | __ ) _   _ _ __   __| | | ___
         *     | |   / _` | '_ \ / _` | | | |/ _` |/ _` |/ _ \ |  _ \| | | | '_ \ / _` | |/ _ \
         *     | |__| (_| | | | | (_| | |_| | (_| | (_| |  __/ | |_) | |_| | | | | (_| | |  __/
         *     |_____\__,_|_| |_|\__, |\__,_|\__,_|\__, |\___| |____/ \__,_|_| |_|\__,_|_|\___|
         *                       |___/             |___/
         */
        lang = ResourceBundle.getBundle("languageBundle", Locale.getDefault());

        /***
         *       ____      _       _             _ _                                                        _   _
         *      / ___|__ _| |_ ___| |__     __ _| | |  _ __ ___   __ _ _   _  ___    _____  _____ ___ _ __ | |_(_) ___  _ __  ___
         *     | |   / _` | __/ __| '_ \   / _` | | | | '__/ _ \ / _` | | | |/ _ \  / _ \ \/ / __/ _ \ '_ \| __| |/ _ \| '_ \/ __|
         *     | |__| (_| | || (__| | | | | (_| | | | | | | (_) | (_| | |_| |  __/ |  __/>  < (_|  __/ |_) | |_| | (_) | | | \__ \
         *      \____\__,_|\__\___|_| |_|  \__,_|_|_| |_|  \___/ \__, |\__,_|\___|  \___/_/\_\___\___| .__/ \__|_|\___/|_| |_|___/
         *                                                       |___/                               |_|
         */
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                OPDE.fatal(e);
            }
        });

        localProps = new SortedProperties();
        props = new Properties();

        /***
         *      _       _ _     _                   _                             _
         *     (_)_ __ (_) |_  | | ___   __ _  __ _(_)_ __   __ _   ___ _   _ ___| |_ ___ _ __ ___
         *     | | '_ \| | __| | |/ _ \ / _` |/ _` | | '_ \ / _` | / __| | | / __| __/ _ \ '_ ` _ \
         *     | | | | | | |_  | | (_) | (_| | (_| | | | | | (_| | \__ \ |_| \__ \ ||  __/ | | | | |
         *     |_|_| |_|_|\__| |_|\___/ \__, |\__, |_|_| |_|\__, | |___/\__, |___/\__\___|_| |_| |_|
         *                              |___/ |___/         |___/       |___/
         */
        logger = Logger.getRootLogger();
        PatternLayout layout = new PatternLayout("%d{ISO8601} %-5p [%t] %c: %m%n");
        ConsoleAppender consoleAppender = new ConsoleAppender(layout);
        logger.addAppender(consoleAppender);

        /***
         *                         _      _               ___        __
         *      _ __ ___  __ _  __| |    / \   _ __  _ __|_ _|_ __  / _| ___
         *     | '__/ _ \/ _` |/ _` |   / _ \ | '_ \| '_ \| || '_ \| |_ / _ \
         *     | | |  __/ (_| | (_| |  / ___ \| |_) | |_) | || | | |  _| (_) |
         *     |_|  \___|\__,_|\__,_| /_/   \_\ .__/| .__/___|_| |_|_|  \___/
         *                                    |_|   |_|
         */
        appInfo = new AppInfo();

        /***
         *       ____                                          _   _     _               ___        _   _
         *      / ___|___  _ __ ___  _ __ ___   __ _ _ __   __| | | |   (_)_ __   ___   / _ \ _ __ | |_(_) ___  _ __  ___
         *     | |   / _ \| '_ ` _ \| '_ ` _ \ / _` | '_ \ / _` | | |   | | '_ \ / _ \ | | | | '_ \| __| |/ _ \| '_ \/ __|
         *     | |__| (_) | | | | | | | | | | | (_| | | | | (_| | | |___| | | | |  __/ | |_| | |_) | |_| | (_) | | | \__ \
         *      \____\___/|_| |_| |_|_| |_| |_|\__,_|_| |_|\__,_| |_____|_|_| |_|\___|  \___/| .__/ \__|_|\___/|_| |_|___/
         *                                                                                   |_|
         */
        Options opts = new Options();
        opts.addOption("h", "hilfe", false, "Gibt die Hilfeseite für OPDE aus.");
        opts.addOption("v", "version", false, "Zeigt die Versionsinformationen an.");
        opts.addOption("a", "anonym", false, "Blendet die Bewohnernamen in allen Ansichten aus. Spezieller Modus für Schulungsmaterial zu erstellen.");
        opts.addOption("l", "debug", false, "Schaltet alle Ausgaben ein auf der Konsole ein, auch die, die eigentlich nur während der Softwareentwicklung angezeigt werden.");
        Option optFTPserver = OptionBuilder.withLongOpt("ftpserver").withArgName("ip or hostname").hasArgs(1).withDescription(lang.getString("cmdline.ftpserver")).create("f");
        opts.addOption(optFTPserver);
//        opts.addOption("p", "pidfile", false, "Path to the pidfile which needs to be deleted when this application ends properly.");

//        Option konfigdir = OptionBuilder.hasOptionalArg().withDescription("Legt einen altenativen Pfad fest, in dem sich das .opde Verzeichnis befindet.").create("k");
//        opts.addOption(konfigdir);

        opts.addOption(OptionBuilder.withLongOpt("jdbc").hasArg().withDescription("Setzt eine alternative URL zur Datenbank fest. Ersetzt die Angaben in der opde.cfg.").create("j"));

        Option dfnimport = OptionBuilder //.withArgName("datum")
                .withLongOpt("dfnimport").hasOptionalArg()
                .withDescription("Startet OPDE im DFNImport Modus für den aktuellen Tag.").create("d");
        dfnimport.setArgName("Anzahl der Tage (+ oder -) abweichend vom aktuellen Tag für den der Import durchgeführt werden soll. Nur in Ausnahmefällen anzuwenden.");
        opts.addOption(dfnimport);

        Option bhpimport = OptionBuilder.withLongOpt("bhpimport").hasOptionalArg().withDescription("Startet OPDE im BHPImport Modus für den aktuellen Tag.").create("b");
//        bhpimport.setOptionalArg(true);
        bhpimport.setArgName("Anzahl der Tage (+ oder -) abweichend vom aktuellen Tag für den der Import durchgeführt werden soll. Nur in Ausnahmefällen anzuwenden.");
        opts.addOption(bhpimport);

        BasicParser parser = new BasicParser();
        CommandLine cl = null;
        String footer = "http://www.Offene-Pflege.de";

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
            f.printHelp("OffenePflege.jar [OPTION]", "Offene-Pflege.de, Version " + appInfo.getVersion()
                    + " Build:" + appInfo.getBuildnum(), opts, footer);
            System.exit(0);
        }

        // Alternative FTP-Server
        if (cl.hasOption("f")) {
            UPDATE_FTPSERVER = cl.getOptionValue("f");
        }


        if (cl.hasOption("h")) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp("OffenePflege.jar [OPTION]", "Offene-Pflege.de, Version " + appInfo.getVersion()
                    + " Build:" + appInfo.getBuildnum(), opts, footer);
            System.exit(0);
        }

        opwd = System.getProperty("user.home") + sep + AppInfo.dirBase;

        /***
         *                                                                ___
         *       __ _ _ __   ___  _ __  _   _ _ __ ___   ___  _   _ ___  |__ \
         *      / _` | '_ \ / _ \| '_ \| | | | '_ ` _ \ / _ \| | | / __|   / /
         *     | (_| | | | | (_) | | | | |_| | | | | | | (_) | |_| \__ \  |_|
         *      \__,_|_| |_|\___/|_| |_|\__, |_| |_| |_|\___/ \__,_|___/  (_)
         *                              |___/
         */
        if (cl.hasOption("a")) { // anonym Modus
            //localProps.put("anonym", "true");
            anonym = true;
            anonymize = new HashMap[]{SYSConst.getNachnamenAnonym(), SYSConst.getVornamenFrauAnonym(), SYSConst.getVornamenMannAnonym()};
        } else {
            anonym = false;
        }

        /***
         *      _       _ _                _       _
         *     (_)_ __ (_) |_   _ __  _ __(_)_ __ | |_ ___ _ __ ___
         *     | | '_ \| | __| | '_ \| '__| | '_ \| __/ _ \ '__/ __|
         *     | | | | | | |_  | |_) | |  | | | | | ||  __/ |  \__ \
         *     |_|_| |_|_|\__| | .__/|_|  |_|_| |_|\__\___|_|  |___/
         *                     |_|
         */
        printers = new LogicalPrinters();

        /***
         *      _                 _   _                 _                                   _   _
         *     | | ___   __ _  __| | | | ___   ___ __ _| |  _ __  _ __ ___  _ __   ___ _ __| |_(_) ___  ___
         *     | |/ _ \ / _` |/ _` | | |/ _ \ / __/ _` | | | '_ \| '__/ _ \| '_ \ / _ \ '__| __| |/ _ \/ __|
         *     | | (_) | (_| | (_| | | | (_) | (_| (_| | | | |_) | | | (_) | |_) |  __/ |  | |_| |  __/\__ \
         *     |_|\___/ \__,_|\__,_| |_|\___/ \___\__,_|_| | .__/|_|  \___/| .__/ \___|_|   \__|_|\___||___/
         *                                                 |_|             |_|
         */
        if (loadLocalProperties()) {


            try {
                FileAppender fileAppender = new FileAppender(layout, opwd + sep + "opde.log", true);
                logger.addAppender(fileAppender);
            } catch (IOException ex) {
                fatal(ex);
            }

            animation = localProps.containsKey("animation") && localProps.getProperty("animation").equals("true");

            logger.info("######### START ###########  " + OPDE.getAppInfo().getProgname() + ", v" + OPDE.getAppInfo().getVersion() + "/" + OPDE.getAppInfo().getBuildnum());
            logger.info(System.getProperty("os.name").toLowerCase());

            /***
             *      _     ____       _                   ___ ___
             *     (_)___|  _ \  ___| |__  _   _  __ _  |__ \__ \
             *     | / __| | | |/ _ \ '_ \| | | |/ _` |   / / / /
             *     | \__ \ |_| |  __/ |_) | |_| | (_| |  |_| |_|
             *     |_|___/____/ \___|_.__/ \__,_|\__, |  (_) (_)
             *                                   |___/
             */
            if (cl.hasOption("l") || SYSTools.catchNull(localProps.getProperty("debug")).equalsIgnoreCase("true")) {
                debug = true;
                logger.setLevel(Level.ALL);
            } else {
                debug = false;
                logger.setLevel(Level.INFO);
            }

            /***
             *          _ ____   _      ____        _        _
             *         | |  _ \ / \    |  _ \  __ _| |_ __ _| |__   __ _ ___  ___
             *      _  | | |_) / _ \   | | | |/ _` | __/ _` | '_ \ / _` / __|/ _ \
             *     | |_| |  __/ ___ \  | |_| | (_| | || (_| | |_) | (_| \__ \  __/
             *      \___/|_| /_/   \_\ |____/ \__,_|\__\__,_|_.__/ \__,_|___/\___|
             *
             */
            String hostkey = OPDE.getLocalProps().getProperty("hostkey");
            String cryptpassword = localProps.getProperty("javax.persistence.jdbc.password");
            DesEncrypter desEncrypter = new DesEncrypter(hostkey);
            Properties jpaProps = new Properties();
            jpaProps.put("javax.persistence.jdbc.user", localProps.getProperty("javax.persistence.jdbc.user"));

            try {
                jpaProps.put("javax.persistence.jdbc.password", desEncrypter.decrypt(cryptpassword));
            } catch (Exception e) {
                if (Desktop.isDesktopSupported()) {
                    JOptionPane.showMessageDialog(null, OPDE.lang.getString("misc.msg.decryption.failure"), appInfo.getProgname(), JOptionPane.ERROR_MESSAGE);
                } else {
                    OPDE.fatal(e);
                }
                System.exit(1);
            }

            jpaProps.put("javax.persistence.jdbc.driver", localProps.getProperty("javax.persistence.jdbc.driver"));
            url = cl.hasOption("j") ? cl.getOptionValue("j") : localProps.getProperty("javax.persistence.jdbc.url");
            jpaProps.put("javax.persistence.jdbc.url", url);

            // Turn of JPA Cache
            jpaProps.put("eclipselink.cache.shared.default", "false");
            jpaProps.put("eclipselink.session.customizer", "op.system.JPAEclipseLinkSessionCustomizer");
//            jpaProps.put("eclipselink.logging.level", JavaLog.FINEST_LABEL);
            emf = Persistence.createEntityManagerFactory("OPDEPU", jpaProps);


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
                System.out.println(footer);
                System.exit(0);
            }


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
                    Users rootUser = em.find(Users.class, "admin");

                    SYSLogin rootLogin = em.merge(new SYSLogin(rootUser));
                    OPDE.setLogin(rootLogin);
                    initProps();

                    DFNTools.generate(em);
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
                    Users rootUser = em.find(Users.class, "admin");

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


            // to speed things later. The first connection loads the while JPA system.
            EntityManager em1 = createEM();
            em1.close();

            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            setStandardFont();

            try {
                css = SYSTools.readFileAsString(opwd + sep + AppInfo.dirTemplates + sep + AppInfo.fileStandardCSS);
            } catch (IOException ie) {
                css = "";
            }

            // JideSoft
            Lm.verifyLicense("Torsten Loehr", "Open-Pflege.de", "G9F4JW:Bm44t62pqLzp5woAD4OCSUAr2");
            WizardStyle.setStyle(WizardStyle.JAVA_STYLE);
            // JideSoft

            /***
             *      _____               __  __       _        ____
             *     |  ___| __ _ __ ___ |  \/  | __ _(_)_ __  / /\ \
             *     | |_ | '__| '_ ` _ \| |\/| |/ _` | | '_ \| |  | |
             *     |  _|| |  | | | | | | |  | | (_| | | | | | |  | |
             *     |_|  |_|  |_| |_| |_|_|  |_|\__,_|_|_| |_| |  | |
             *                                               \_\/_/
             */

            SYSTools.checkForSoftwareupdates();
            mainframe = new FrmMain();
            mainframe.setVisible(true);

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

    private static boolean loadLocalProperties() {
        boolean success = false;
        Properties sysprops = System.getProperties();

        try {
            FileInputStream in = new FileInputStream(new File(opwd + sep + AppInfo.fileConfig));
            Properties p = new Properties();
            p.load(in);
            localProps.putAll(p);
            p.clear();

            in.close();

            success = true;
        } catch (FileNotFoundException ex) {
            // Keine local.properties. Wir richten wohl gerade einen neuen Client ein.

            FrmInit frame = new FrmInit();
            frame.setVisible(true);
            SYSTools.center(frame);

        } catch (IOException ex) {
            fatal(ex);
            System.exit(1);
        }
        return success;
    }

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
