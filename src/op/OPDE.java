/*
 * OffenePflege
 * Copyright (C) 2008 Torsten Löhr
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

import entity.*;
import op.care.BHPImport;
import op.care.DFNImport;
import op.threads.ProofOfLife;
import op.tools.*;
import org.apache.commons.cli.*;
import org.apache.log4j.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

public class OPDE {

    //public static final String OCDEBUG = "true";
    public static long uptime;
    protected static Database db;
    //OPDE.getLogin().getUser().getUKennung()
    //public static String UKennung; // Zuer Zeit angemeldeter Benutzer.
    //public static char[] UPW;
    public static OPMain ocmain;
    protected static String url;
    //public static long ocloginid;
    protected static Properties props;
    protected static boolean anonym;
    protected static SortedProperties localProps;
    protected static Properties appinfo;
    //private static boolean admin;
    //public static Properties ocgroups;
    protected static Logger logger;
    private static OCSec ocsec;
    //private static ArrayList groups;
    public static HashMap[] anonymize = null;
    // Diese listener List ist dazu da, dass wir immer
    // wissen, welchen Fenstern wir bescheid sagen müssen, wenn
    // der User sich abmeldet.
    protected static EventListenerList listenerList = new EventListenerList();
    protected static HashMap<String, ActionListener> runningModules = new HashMap();
    protected static EntityManager em;
    protected static InternalClasses internalClasses;
    protected static SYSLogin login;
    protected static SYSHosts host;
    protected static ProofOfLife pol;
    protected static ArrayList<ImageIcon> animationCache;
    protected static boolean animation = false;
    protected static boolean debug;

    public static ArrayList<ImageIcon> getAnimationCache() {
        return animationCache;
    }

    public static Properties getAppinfo() {
        return appinfo;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static ProofOfLife getPoL() {
        return pol;
    }

    public static boolean isAnimation() {
        return animation;
    }

    public static boolean isAnonym() {
        return anonym;
    }

    public static void setProp(String key, String value) {
        props.put(key, value);

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

    public static boolean addNewModule(String internalClassID, ActionListener listener) {
        boolean success = false;
        if (!runningModules.containsKey(internalClassID)) {
            listenerList.add(ActionListener.class, listener);
            runningModules.put(internalClassID, listener);
            success = true;
            OPDE.getLogger().debug("Modul " + internalClassID + " hinzugefügt.");
        }
        return success;
    }

    public static void removeModule(String internalClassID) {
        listenerList.remove(ActionListener.class, runningModules.get(internalClassID));
        runningModules.remove(internalClassID);
        OPDE.getLogger().debug("Modul " + internalClassID + " entfernt.");
    }

    public static void notifyAboutLogout() {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            ((ActionListener) listeners[i + 1]).actionPerformed(new ActionEvent(OPDE.class, 1, "LOGOUT"));
        }
        clearModules();
    }

    protected static void clearModules() {
        Iterator<String> keys = runningModules.keySet().iterator();
        while (keys.hasNext()) {
            listenerList.remove(ActionListener.class, runningModules.get(keys.next()));
        }
        runningModules.clear();
        listenerList = new EventListenerList();
    }

    public static Logger getLogger() {
        return logger;
    }

    /**
     * Diese initDB() Methode wird verschwinden, sobald ich ganz auf JPA umgestellt habe.
     * @throws SQLException
     */
    public static void initDB() throws SQLException {
        if (db != null) return;
        String dbuser = localProps.getProperty("javax.persistence.jdbc.user");
        String dbpw = localProps.getProperty("javax.persistence.jdbc.password");
        db = new Database(url, dbuser, dbpw.toCharArray());
    }

    public static void warn(Object message) {
        logger.warn(message);
        SyslogTools.warn(message.toString());
    }

    public static void closeDB() throws SQLException {
        db.db.close();
        db = null;
    }

    public static void info(Object message) {
        logger.info(message);
        SyslogTools.info(message.toString());
    }

    public static void fatal(Object message) {
        logger.fatal(message);
        SyslogTools.fatal(message.toString());
    }

    public static void error(Object message) {
        logger.error(message);
        SyslogTools.error(message.toString());
    }

    public static void debug(Object message) {
        logger.debug(message);
    }

    public static EntityManager getEM() {
        return em;
    }

    public static Properties getProps() {
        return props;
    }

    public static Properties getLocalProps() {
        return localProps;
    }

    public static void saveLocalProps() {
        try {
            FileOutputStream out = new FileOutputStream(new File(localProps.getProperty("opwd") + System.getProperty("file.separator") + "local.properties"));
            localProps.store(out, "Lokale Einstellungen für Offene-Pflege.de");
            out.close();
        } catch (Exception ex) {
            logger.fatal(ex);
            System.exit(1);
        }
    }

    public static InternalClasses getInternalClasses() {
        return internalClasses;
    }

    public static SYSHosts getHost() {
        return host;
    }

    public static SYSLogin getLogin() {
        return login;
    }

    public static void setLogin(SYSLogin login) {
        OPDE.login = login;
        //ocloginid = (login == null) ? -1 : login.getLoginID();
        //UKennung = (login == null) ? "" : login.getUser().getUKennung();
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
    public static void main(String[] args) {
        // throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        uptime = SYSCalendar.now();
        animationCache = new ArrayList(96);
        //debug = true;

        // Das hier fängt alle ungefangenen Exceptions auf.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                //new DlgException(new Exception(e));
                e.printStackTrace();
                OPDE.error(e);
            }
        });

        localProps = new SortedProperties();
        appinfo = new Properties();
        props = new Properties();

        try {
            // Lade Build Informationen
            InputStream in2 = null;
            //Class clazz = getClass();
            in2 = OPDE.class.getResourceAsStream("/appinfo.properties");
            appinfo.load(in2);
            in2.close();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        /*
        try {
        // @Java-2
        // http://openbook.galileodesign.de/javainsel5/javainsel25_004.htm

        // Hin
        Cipher c = Cipher.getInstance("DES");
        Key k = new SecretKeySpec("01234567".getBytes(), "DES");
        c.init(Cipher.ENCRYPT_MODE, k);
        OutputStream out = new ByteOutputStream();
        CipherOutputStream cos = new CipherOutputStream(out, c);
        cos.write("Das wird anders werden".getBytes());
        cos.close();
        String crypt = out.toString();
        System.out.println(crypt);

        // und Her
        c.init(Cipher.DECRYPT_MODE, k);
        out = new ByteOutputStream();
        cos = new CipherOutputStream(out, c);
        cos.write(crypt.getBytes());
        cos.close();
        String decrypt = out.toString();
        System.out.println(decrypt);


        } catch (Exception ex) {
        java.util.logging.Logger.getLogger(OPDE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

         */

        // AUSWERTUNG KOMMANDOZEILE-----------------------------------------------------------------------------
        // Hier erfolgt die Unterscheidung, in welchem Modus OPDE gestartet wurde.
        Options opts = new Options();
        opts.addOption("h", "hilfe", false, "Gibt die Hilfeseite für OPDE aus.");
        opts.addOption("v", "version", false, "Zeigt die Versionsinformationen an.");
        opts.addOption("a", "anonym", false, "Blendet die Bewohnernamen in allen Ansichten aus. Spezieller Modus für Schulungsmaterial zu erstellen.");
        opts.addOption("l", "debug", false, "Schaltet alle Ausgaben ein auf der Konsole ein, auch die, die eigentlich nur während der Softwareentwicklung angezeigt werden.");

        Option konfigdir = OptionBuilder.hasOptionalArg().withDescription("Legt einen altenativen Pfad fest, in dem sich das .op Verzeichnis befindet.").create("k");
        opts.addOption(konfigdir);


        opts.addOption(OptionBuilder.withLongOpt("jdbc").hasArg().withDescription("Setzt eine alternative URL zur Datenbank fest. Ersetzt die Angaben in der local.properties.").create("j"));

        Option dfnimport = OptionBuilder //.withArgName("datum")
                .withLongOpt("dfnimport") //.hasOptionalArg()
                .withDescription("Startet OPDE im DFNImport Modus für den aktuellen Tag.").create("d");

        opts.addOption(dfnimport);

        //Option bhpimport = null;
//
//        if (debug) {
//            bhpimport = OptionBuilder.withLongOpt("bhpimport").hasOptionalArg().withDescription("Startet OPDE im BHPImport Modus für den aktuellen Tag. (Debug Modus mit DayOffset)").create("b");
//        } else {
//
//        }

        Option bhpimport = OptionBuilder.withLongOpt("bhpimport").withDescription("Startet OPDE im BHPImport Modus für den aktuellen Tag.").create("b");

//        if (OCDEBUG.equalsIgnoreCase("true")){
//            bhpimport.setOptionalArg(true);
//            bhpimport.setArgName("DayOffset (nur im Debug Modus)");
//        }
        opts.addOption(bhpimport);


        BasicParser parser = new BasicParser();
        CommandLine cl = null;
        String footer = "http://www.Offene-Pflege.de";

        try {
            cl = parser.parse(opts, args);
        } catch (ParseException ex) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp("OffenePflege.jar [OPTION]", "Offene-Pflege.de, Version " + OPDE.getLocalProps().getProperty("program.VERSION")
                    + " Build:" + OPDE.getLocalProps().getProperty("program.BUILDNUM"), opts, footer);
            System.exit(0);
        }

        if (cl.hasOption("h")) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp("OffenePflege.jar [OPTION]", "Offene-Pflege.de, Version " + OPDE.getLocalProps().getProperty("program.VERSION")
                    + " Build:" + OPDE.getLocalProps().getProperty("program.BUILDNUM"), opts, footer);
            System.exit(0);
        }

        // Alternatives Arbeitsverzeichnis setzen
        if (cl.hasOption("k")) {
            String homedir = cl.getOptionValue("k");
            localProps.put("opwd", homedir + System.getProperty("file.separator") + ".op");
        } else {
            localProps.put("opwd", System.getProperty("user.home") + System.getProperty("file.separator") + ".op");
            localProps.put("opcache", localProps.getProperty("opwd") + System.getProperty("file.separator") + "cache");
        }

        if (cl.hasOption("a")) { // anonym Modus
            //localProps.put("anonym", "true");
            anonym = true;
            anonymize = new HashMap[]{SYSConst.getNachnamenAnonym(), SYSConst.getVornamenFrauAnonym(), SYSConst.getVornamenMannAnonym()};
        } else {
            anonym = false;
        }

        // Legt bei Bedarf ein neues Arbeitsverzeichnis an.
        if (!new File(localProps.getProperty("opwd")).exists()) {
            new File(localProps.getProperty("opwd")).mkdir();
        }
//        if (!new File(localProps.getProperty("ocreports")).exists()) {
//            new File(localProps.getProperty("ocreports")).mkdir();
//        }
        if (!new File(localProps.getProperty("opcache")).exists()) {
            new File(localProps.getProperty("opcache")).mkdir();
        }
//        if (!new File(localProps.getProperty("ocdownload")).exists()) {
//            new File(localProps.getProperty("ocdownload")).mkdir();
//        }

        // LogSystem initialisieren.
        logger = Logger.getRootLogger();

        //SimpleLayout layout = new SimpleLayout();
        PatternLayout layout = new PatternLayout("%d{ISO8601} %-5p [%t] %c: %m%n");
        ConsoleAppender consoleAppender = new ConsoleAppender(layout);
        logger.addAppender(consoleAppender);
        String sep = System.getProperty("file.separator");
        try {
            FileAppender fileAppender = new FileAppender(layout, localProps.getProperty("opwd") + sep + "op.log", true);
            logger.addAppender(fileAppender);
        } catch (IOException ex) {
            logger.fatal(localProps.getProperty("opwd") + ": falscher Pfad.");
            System.exit(1);
        }

        loadLocalProperties();

        animation = localProps.containsKey("animation") && localProps.getProperty("animation").equals("true");

        logger.info("######### START ###########  " + SYSTools.getWindowTitle(""));

        if (cl.hasOption("l") || localProps.getProperty("debug").equalsIgnoreCase("true")) {
            debug = true;
            logger.setLevel(Level.ALL);
        } else {
            debug = false;
            logger.setLevel(Level.INFO);
        }

//        if (isDebug()) {
//            url = "jdbc:mysql://" + localProps.getProperty("devdbsrv") + ":" + localProps.getProperty("dbport") + "/" + localProps.getProperty("dbdevcat");
//        } else {
//            url = "jdbc:mysql://" + localProps.getProperty("dbsrv") + ":" + localProps.getProperty("dbport") + "/" + localProps.getProperty("dbcat");
//        }

        Properties jpaProps = new Properties();
        jpaProps.put("javax.persistence.jdbc.user", localProps.getProperty("javax.persistence.jdbc.user"));
        jpaProps.put("javax.persistence.jdbc.password", localProps.getProperty("javax.persistence.jdbc.password"));
        jpaProps.put("javax.persistence.jdbc.driver", localProps.getProperty("javax.persistence.jdbc.driver"));
        url = cl.hasOption("j") ? cl.getOptionValue("j") : localProps.getProperty("javax.persistence.jdbc.url");
        jpaProps.put("javax.persistence.jdbc.url", url);

        em = Persistence.createEntityManagerFactory("OPDEPU", jpaProps).createEntityManager();

        host = SYSHostsTools.getHost(OPDE.getLocalProps().getProperty("hostkey"));

        if (host == null) {
            logger.fatal("Host kann nicht doppelt starten. Warten sie ca. 2 Minuten.");
            logger.fatal("Wenn es dann nicht besser wird, fragen Sie den Administrator.");
            System.exit(1);
        }

        pol = new ProofOfLife();
        pol.start();

        String header = SYSTools.getWindowTitle("");

        if (cl.hasOption("v")) {
            System.out.println(header);
            System.out.println(footer);
            System.exit(0);
        }

        if (cl.hasOption("d")) {
            String d = cl.getOptionValue("d");
            try {
                DFNImport.importDFN();
            } catch (Exception ex) {
                logger.fatal("Exception beim DFNImport", ex);
                System.exit(1);
            }
            System.exit(0);
        }

        if (cl.hasOption("b")) {
            try {
                int offset = 0;
//                if (isDebug()) {
//                    String sOffset = cl.getOptionValue("b");
//                    OPDE.getLogger().debug(cl.getOptionValue("b"));
//                    try {
//                        offset = Integer.parseInt(sOffset);
//                    } catch (NumberFormatException ex) {
//                        offset = 0;
//                    }
//                }
                BHPImport.importBHP(0, 0, offset);
            } catch (Exception ex) {
                logger.fatal("Exception beim BHPImport", ex);
                System.exit(1);
            }
            System.exit(0);
        }

        internalClasses = new InternalClasses();
//        try {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(OPDE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(OPDE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(OPDE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(OPDE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }

        ocmain = new OPMain(); // !!!!!!!!!!!!!!!!!!!!!!!! HAUPTPROGRAMM !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    }

    private static void loadLocalProperties() {
        Properties sysprops = System.getProperties();
        String sep = sysprops.getProperty("file.separator");
        try {
            FileInputStream in = new FileInputStream(new File(localProps.getProperty("opwd") + sep + "local.properties"));
            Properties p = new Properties();
            p.load(in);
            localProps.putAll(p);
            p.clear();
            // damit das nicht von den local.properties überschrieben werden kann.
            //localProps.put("debug", OCDEBUG);

            in.close();
        } catch (FileNotFoundException ex) {
            // Keine local.properties. Nicht gut....
            logger.fatal(localProps.getProperty("opwd") + sep + "local.properties existiert nicht. Bitte legen Sie diese Datei an.");
            System.exit(1);
        } catch (IOException ex) {
            logger.fatal(localProps.getProperty("opwd") + sep + "local.properties nicht lesbar. Bitte korrigieren Sie das Problem.");
            System.exit(1);
        }
    }


    public static Database getDb() {
        return db;
    }
//    public static ArrayList getGroups() {
//        return groups;
//    }
//
//    public static void setGroups(ArrayList gr) {
//        groups = gr;
//    }

    public static boolean isAdmin() {
        return UsersTools.isAdmin(login.getUser());
    }

    //    public static boolean isPDL() {
//        return groups.contains("pdl");
//    }
//
//    public static boolean isSTPDL() {
//        return groups.contains("stpdl");
//    }
    public static boolean isExamen() {
        return UsersTools.isExamen(login.getUser());
    }

    public static OCSec getOCSec() {
        return ocsec;
    }

    public static void newOCSec() {
        if (ocsec != null) {
            ocsec.cleanup();
        }
        ocsec = new OCSec();
    }
}
