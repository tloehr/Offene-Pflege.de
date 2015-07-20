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
import entity.EntityTools;
import entity.files.SYSFilesTools;
import entity.nursingprocess.DFNTools;
import entity.prescription.BHPTools;
import entity.system.*;
import gui.GUITools;
import op.settings.basicsetup.FrmDBConnection;
import op.system.AppInfo;
import op.system.EMailSystem;
import op.system.LogicalPrinters;
import op.threads.DisplayManager;
import op.threads.PrintProcessor;
import op.tools.*;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

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
    private static DesEncrypter desEncrypter;
    public static FrmMain mainframe;
    protected static String url;
    protected static LogicalPrinters printers;
    protected static Properties props;
    protected static boolean anonym;
    protected static SortedProperties localProps;
    private final static Logger logger = Logger.getRootLogger();
    public static HashMap[] anonymize = null;

    public static String SUBDIR_TEMPLATES = "templates";
    public static String SUBDIR_CACHE = "cache";

    protected static EntityManagerFactory emf;
    protected static AppInfo appInfo;
    protected static SYSLogin login;
    protected static ValidatorFactory validatorFactory;

    protected static boolean animation = false;
    protected static boolean debug;
    protected static boolean training;
    protected static boolean experimental;
    //    protected static String oldopwd = "";
    protected static String css = "";

    private static int DEFAULT_TIMEOUT = 30;

    //    protected static boolean FTPisWORKING = false;
    public static String UPDATE_FTPSERVER = "ftp.offene-pflege.de";
    protected static boolean updateAvailable = false;

    public static String getUpdateDescriptionURL() {
        return updateDescriptionURL;
    }

    protected static String updateDescriptionURL = "http://www.offene-pflege.de";

    protected static final String sep = System.getProperty("file.separator");

    /**
     * @return Das Arbeitsverzeichnis für OPDE.
     */
    public static String getOPWD() {
        return Hardware.getAppDataPath();
    }

    public static long getUPTime() {
        return uptime;
    }

    public static boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public static void setUpdateAvailable(boolean updateAvailable, String url) {
        OPDE.updateAvailable = updateAvailable;
        if (updateAvailable) {
            updateDescriptionURL = url;
        }
    }

    public static String getOPCache() {
        return getOPWD() + sep + AppInfo.dirCache;
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
        return SYSPropsTools.isBooleanTrue(SYSPropsTools.KEY_CALC_MEDI_UPR1);
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
        warn(logger, message);
    }

    public static void warn(Logger classLogger, Throwable message) {
        classLogger.warn(message);
        if (emf != null)
            SyslogTools.warn(ExceptionUtils.getMessage(message) + ": " + ExceptionUtils.getStackTrace(message));
    }

    public static void info(Object message) {
        logger.info(message);
    }

    public static void important(Object message) {
        logger.info(message);
        SyslogTools.info(message.toString());
    }

    public static ValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }

    public static void important(EntityManager em, Object message) throws Exception {
        logger.info(message);
        SyslogTools.addLog(em, message.toString(), SyslogTools.INFO);
    }

    public static void fatal(Throwable e) {
        fatal(logger, e);
    }

    public static void fatal(Logger classLogger, Throwable e) {
        classLogger.fatal(e.getMessage(), e);

        if (emf != null) {
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

            String html = SYSTools.getThrowableAsHTML(e);
            File temp = SYSFilesTools.print(html, false);

            if (!isDebug()) {
                EMailSystem.sendErrorMail(e.getMessage(), temp);
            }
        }

        System.exit(1);
    }

    public static void error(Object message) {
        logger.error(message);
        SyslogTools.error(message.toString());
    }

    public static void error(Logger classLogger, Object message) {
        classLogger.error(message);
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

    /**
     * returns the minutes until the system timeouts the current login automatically when no user action is detected.
     * if the timeout value is 0, no timeout is performed.
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
            FileOutputStream out = new FileOutputStream(new File(getOPWD() + sep + AppInfo.fileConfig));
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

    public static boolean isExperimental() {
        return experimental;
    }

    public static boolean isTraining() {
        return training;
    }

    public static DesEncrypter getDesEncrypter() {
        return desEncrypter;
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
        validatorFactory = Validation.buildDefaultValidatorFactory();
        desEncrypter = new DesEncrypter();

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
        opts.addOption("x", "experimental", false, "Schaltet experimentelle Programm-Module für User frei, die Admin Rechte haben. VORSICHT !!!!");
        opts.addOption("a", "anonym", false, "Blendet die Bewohnernamen in allen Ansichten aus. Spezieller Modus für Schulungsmaterial zu erstellen.");
//        opts.addOption("w", "workingdir", true, "Damit kannst Du ein anderes Arbeitsverzeichnis setzen. Wenn Du diese Option weglässt, dann ist das Dein Benutzerverzeichnis: " + System.getProperty("user.home"));
        opts.addOption("l", "debug", false, "Schaltet alle Ausgaben ein auf der Konsole ein, auch die, die eigentlich nur während der Softwareentwicklung angezeigt werden.");
//        opts.addOption("t", "training", false, "Wird für Einarbeitungsversionen benötigt. Färbt die Oberfläche anders ein und zeigt eine Warnmeldung nach jeder Anmeldung.");
        Option optFTPserver = OptionBuilder.withLongOpt("ftpserver").withArgName("ip or hostname").hasArgs(1).withDescription(SYSTools.xx("cmdline.ftpserver")).create("f");
        opts.addOption(optFTPserver);
//        opts.addOption("p", "pidfile", false, "Path to the pidfile which needs to be deleted when this application ends properly.");

        Option notification = OptionBuilder.withLongOpt("notification").hasOptionalArg().withDescription("Schickt allen festgelegten Empfängern die jeweilige Benachrichtungs-Mail.").create("n");
        notification.setArgName("Liste der Empfänger (durch Komma getrennt, ohne Leerzeichen. UID verwenden). Damit kannst Du die Benachrichtigungen einschränken. Fehlt diese Liste, erhalten ALLE Empfänger eine Mail.");
        opts.addOption(notification);

        opts.addOption(OptionBuilder.withLongOpt("jdbc").hasArg().withDescription(SYSTools.xx("cmdline.jdbc")).create("j"));

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
            f.printHelp("OffenePflege.jar [OPTION]", "Offene-Pflege.de, Version " + appInfo.getVersion() + " [" + appInfo.getBuildnum() + "]", opts, footer);
            System.exit(0);
        }

        // Alternative FTP-Server
        if (cl.hasOption("f")) {
            UPDATE_FTPSERVER = cl.getOptionValue("f");
        }


        if (cl.hasOption("h")) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp("OffenePflege.jar [OPTION]", "Offene-Pflege.de, Version " + appInfo.getVersion() + " [" + appInfo.getBuildnum() + "]", opts, footer);
            System.exit(0);
        }


//        String homedir = System.getProperty("user.home");
//        // alternatice working dir
//        if (cl.hasOption("w")) {
//            File dir = new File(cl.getOptionValue("w"));
//            if (dir.exists() && dir.isDirectory()) {
//                homedir = dir.getAbsolutePath();
//            }
//        }

        // old style oldopwd
//        oldopwd = homedir + sep + AppInfo.dirBase;
//        oldopwd = homedir + sep + AppInfo.dirBase;

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

        try {

            loadLocalProperties();


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
                logger.setLevel(Level.DEBUG);
            } else {
                debug = false;
                logger.setLevel(Level.INFO);
            }

            Logger.getLogger("org.hibernate").setLevel(Level.OFF);

            if (cl.hasOption("x") || SYSTools.catchNull(localProps.getProperty("experimental")).equalsIgnoreCase("true")) {
                experimental = true;

            } else {
                experimental = false;
            }

            if (cl.hasOption("t") || SYSTools.catchNull(localProps.getProperty("training")).equalsIgnoreCase("true")) {
                training = true;
            } else {
                training = false;
            }


            /***
             *          _ _                       _                                               _   _ _     _        ___ 
             *       __| | |____   _____ _ __ ___(_) ___  _ __     ___ ___  _ __ ___  _ __   __ _| |_(_) |__ | | ___  |__ \
             *      / _` | '_ \ \ / / _ \ '__/ __| |/ _ \| '_ \   / __/ _ \| '_ ` _ \| '_ \ / _` | __| | '_ \| |/ _ \   / /
             *     | (_| | |_) \ V /  __/ |  \__ \ | (_) | | | | | (_| (_) | | | | | | |_) | (_| | |_| | |_) | |  __/  |_| 
             *      \__,_|_.__/ \_/ \___|_|  |___/_|\___/|_| |_|  \___\___/|_| |_| |_| .__/ \__,_|\__|_|_.__/|_|\___|  (_) 
             *                                                                       |_|                                   
             */
            // second test. is the database sane ?
            url = cl.hasOption("j") ? cl.getOptionValue("j") : EntityTools.getJDBCUrl(localProps.getProperty(SYSPropsTools.KEY_JDBC_HOST), localProps.getProperty(SYSPropsTools.KEY_JDBC_PORT), localProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG));

            String clearpassword = null;
//            try {
//                clearpassword = desEncrypter.decrypt(SYSTools.catchNull(localProps.getProperty(SYSPropsTools.KEY_JDBC_PASSWORD)));
//            } catch (IOException io) {
//                fatal(io);
//            } catch (BadPaddingException bpe) { // wrong password
//                logger.warn(bpe);
//            } catch (IllegalBlockSizeException ibse) {
//                fatal(ibse);
//            }


            FrmDBConnection frmDBConnection = new FrmDBConnection();
                            frmDBConnection.setVisible(true);

            if (!frmDBConnection.isDatabaseOK()) {
                frmDBConnection.setVisible(true);
            } else {


                /***
                 *          _ ____   _      ____        _        _
                 *         | |  _ \ / \    |  _ \  __ _| |_ __ _| |__   __ _ ___  ___
                 *      _  | | |_) / _ \   | | | |/ _` | __/ _` | '_ \ / _` / __|/ _ \
                 *     | |_| |  __/ ___ \  | |_| | (_| | || (_| | |_) | (_| \__ \  __/
                 *      \___/|_| /_/   \_\ |____/ \__,_|\__\__,_|_.__/ \__,_|___/\___|
                 *
                 */
                Properties jpaProps = new Properties();
                jpaProps.put(SYSPropsTools.KEY_JDBC_USER, localProps.getProperty("javax.persistence.jdbc.user"));

                try {
                    jpaProps.put(SYSPropsTools.KEY_JDBC_PASSWORD, clearpassword);
                } catch (Exception e) {
                    if (Desktop.isDesktopSupported()) {
                        JOptionPane.showMessageDialog(null, SYSTools.xx("misc.msg.decryption.failure"), appInfo.getProgname(), JOptionPane.ERROR_MESSAGE);
                    } else {
                        OPDE.fatal(e);
                    }
                    System.exit(1);
                }

                jpaProps.put(SYSPropsTools.KEY_JDBC_DRIVER, "com.mysql.jdbc.Driver");
                jpaProps.put(SYSPropsTools.KEY_JDBC_URL, url);


                //            if (cl.hasOption("d") || cl.hasOption("d")) {  // not for BHP or DFN
                //                jpaProps.put("eclipselink.cache.shared.default", "false");
                //            } else {
                //                jpaProps.put("eclipselink.cache.shared.default", "true");
                //            }

                jpaProps.put("eclipselink.cache.shared.default", "false");
                jpaProps.put("eclipselink.session.customizer", "entity.JPAEclipseLinkSessionCustomizer");
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


                /***
                 *      _   _       _   _  __ _           _   _
                 *     | \ | | ___ | |_(_)/ _(_) ___ __ _| |_(_) ___  _ __
                 *     |  \| |/ _ \| __| | |_| |/ __/ _` | __| |/ _ \| '_ \
                 *     | |\  | (_) | |_| |  _| | (_| (_| | |_| | (_) | | | |
                 *     |_| \_|\___/ \__|_|_| |_|\___\__,_|\__|_|\___/|_| |_|
                 *
                 */
                if (cl.hasOption("n")) {

                    EntityManager em = OPDE.createEM();

                    try {
                        em.getTransaction().begin();
                        Users rootUser = em.find(Users.class, "admin");

                        SYSLogin rootLogin = em.merge(new SYSLogin(rootUser));
                        OPDE.setLogin(rootLogin);
                        initProps();

                        EMailSystem.notify(cl.getOptionValue("n"));

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
                    css = SYSTools.readFileAsString(getOPWD() + sep + AppInfo.dirTemplates + sep + AppInfo.fileStandardCSS);
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


                //        JFrame frm = new JFrame();
                //            frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                //            frm.setLayout(new FlowLayout());
                //
                //                    frm.getContentPane().add(new PnlBodyScheme(new Properties()));
                //
                //                    frm.setVisible(true);

                //            SYSTools.checkForSoftwareupdates();


                mainframe = new FrmMain();


                mainframe.setVisible(true);
            }
        } catch (IOException ioe) {
            fatal(ioe);
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


        File configFile = new File(Hardware.getAppDataPath() + sep + AppInfo.fileConfig);
        debug("configFile:" + configFile);

        // make sure the file exists
        if (!configFile.exists()) { // path didnt exist yet.

            debug("configFile: missing");

            // is there an old opde.cfg ?
            // then we should copy it over
            String oldopwd = System.getProperty("user.home") + sep + AppInfo.dirBase;
            File oldConfigFile = new File(oldopwd + sep + AppInfo.fileConfig);

            if (oldConfigFile.exists()) {
                debug("oldConfigFile found:" + oldConfigFile);
                FileUtils.copyFile(oldConfigFile, configFile);
                debug("copying over and renaming the old one");
                FileUtils.moveFile(oldConfigFile, new File(oldopwd + sep + AppInfo.fileConfig + ".old"));
            }
        }

        configFile.createNewFile();


        // make sure the minimum requirements for the configs are present. this will be overwritten by any contents in the actual configFile.
        // missing these settings will most definitely cause exceptions
        localProps.put(SYSPropsTools.BHP_MAX_MINUTES_TO_WITHDRAW, "30");
        localProps.put(SYSPropsTools.DFN_MAX_MINUTES_TO_WITHDRAW, "30");
        localProps.put(SYSPropsTools.KEY_CASH_PAGEBREAK, "30");
        localProps.put(SYSPropsTools.KEY_STATION, "1");
        localProps.put(SYSPropsTools.KEY_VERY_EARLY_FGSHIFT, "FFECF5");
        localProps.put(SYSPropsTools.KEY_VERY_EARLY_BGSHIFT, "FF62B0");
        localProps.put(SYSPropsTools.KEY_VERY_EARLY_FGITEM, "FF62B0");
        localProps.put(SYSPropsTools.KEY_VERY_EARLY_BGITEM, "FFC8E3");
        localProps.put(SYSPropsTools.KEY_EARLY_FGSHIFT, "ECF4FF");
        localProps.put(SYSPropsTools.KEY_EARLY_BGSHIFT, "62A9FF");
        localProps.put(SYSPropsTools.KEY_EARLY_FGITEM, "62A9FF");
        localProps.put(SYSPropsTools.KEY_EARLY_BGITEM, "D0E6FF");
        localProps.put(SYSPropsTools.KEY_LATE_FGSHIFT, "F3F8F4");
        localProps.put(SYSPropsTools.KEY_LATE_BGSHIFT, "59955C");
        localProps.put(SYSPropsTools.KEY_LATE_FGITEM, "59955C");
        localProps.put(SYSPropsTools.KEY_LATE_BGITEM, "DBEADC");
        localProps.put(SYSPropsTools.KEY_VERY_LATE_FGSHIFT, "FFE3FF");
        localProps.put(SYSPropsTools.KEY_VERY_LATE_BGSHIFT, "990099");
        localProps.put(SYSPropsTools.KEY_VERY_LATE_FGITEM, "990099");
        localProps.put(SYSPropsTools.KEY_VERY_LATE_BGITEM, "FFA8FF");
        localProps.put(SYSPropsTools.KEY_ONDEMAND_FGSHIFT, "F5F5E2");
        localProps.put(SYSPropsTools.KEY_ONDEMAND_BGSHIFT, "D1D17A");
        localProps.put(SYSPropsTools.KEY_ONDEMAND_FGITEM, "D1D17A");
        localProps.put(SYSPropsTools.KEY_ONDEMAND_BGITEM, "EEEECE");
        localProps.put(SYSPropsTools.KEY_OUTCOME_FGSHIFT, GUITools.toHexString(Color.LIGHT_GRAY));
        localProps.put(SYSPropsTools.KEY_OUTCOME_BGSHIFT, GUITools.toHexString(Color.DARK_GRAY));


        FileInputStream in = new FileInputStream(configFile);
        Properties p = new Properties();
        p.load(in);
        localProps.putAll(p);
        p.clear();

        in.close();

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
