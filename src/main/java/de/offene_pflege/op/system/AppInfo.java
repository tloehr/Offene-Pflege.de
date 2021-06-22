/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.op.system;

import de.offene_pflege.entity.system.Acl;
import de.offene_pflege.entity.system.SYSGROUPS2ACL;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.LocalMachine;
import de.offene_pflege.op.tools.SYSTools;
import de.offene_pflege.op.tools.SortedProperties;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Die AppInfo dient dazu Informationen bzgl. der Module innerhalb von OPDE aus der Datei context.xml einzulesen und
 * während der Laufzeit bereit zu halten. Die xml Datei befindet sich im JAR Archiv und wird nur währen der
 * Entwicklungsphase bearbeitet.
 * <p>
 * Diese Klasse implementiert ein Sicherheitskonzept für OPDE. Zu jeder Klasse können 11 Rechte definiert werden, die
 * dem jeweiligen Benutzer gewährt oder verweigert werden. Welche Rechte für eine betreffende Klasse von Bedeutung sind
 * wird innerhalb der Applikation in besagter xml Datei festgelegt. Da diese Informationen spezifisch für eine
 * Programmversion sind sind sie hier als "quasi Konstanten" definiert werden.
 * <p>
 * Außerdem
 *
 * @author tloehr
 */
public class AppInfo {

    // Zur Unterscheidung in TXEssenDoc
    public static final int DB_VERSION_BEFORE_DAS_PFLEGE_101 = 12;


    public static final String fileConfig = "opde.cfg";
    public static final String fileConfigXML = "opde.xml";
    public static final String fileNewuser = "newuser.html";
    public static final String filePrinters = "printers.xml";
    public static final String fileStandardCSS = "standard.css";
    public static final String filePID = "opde.pid";

    public static final String dirCache = "cache";
    public static final String dirSql = "dbscripts";
    public static final String dirArtwork = "artwork";
    private static final String dirTemplates = "templates";

    //    public static final String dirJar = "jar";
    public static final String dirArtwork16 = "16x16";
    public static final String dirArtwork22 = "22x22";
    public static final String dirArtwork48 = "48x48";
    public static final String dirArtwork64 = "64x64";
    public static final String dirBase = "opde";


    private HashMap<String, InternalClass> internalClasses;

    private ArrayList<InternalClass> mainClasses;

    /**
     * Diese defaultsProperties werden gebraucht, wenn der Client zum ersten mal eingerichtet wird. Dann stehen hier die
     * unbedingt erforderlichen Konfigurationen drin. Diese werden dann in die opde.cfg übernommen, damit es nicht zu
     * Exceptions kommt.
     */
    private SortedProperties defaultProperties;

//    private String updateCheckUrl;

    /**
     * Hier stehen Versions und Build Informationen drin. Diese stammen aus der context.properties, die teilweise
     * automatisch von einem ANT script geändert wird.
     */
    private Properties context;

//    private String version;

//    public String getBuilddate() {
//        return builddate;
//    }

    private LocalDateTime builddate;
    private int dbversion;
    private String progname;

    public AppInfo() {

        context = new Properties();
        internalClasses = new HashMap();
        mainClasses = new ArrayList<InternalClass>();
        defaultProperties = new SortedProperties();

        try {

            // lade context.properties
            InputStream in2 = OPDE.class.getResourceAsStream("/application.properties");
            context.load(in2);
            in2.close();

            progname = "OPDE1";
            builddate = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(context.getProperty("timestamp"))), ZoneId.systemDefault());
            dbversion = Integer.parseInt(context.getProperty("opde.dbversion"));

            // parse context.xml
            XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            InputSource is = new InputSource(OPDE.class.getResourceAsStream("/appinfo.xml"));
            parser.setContentHandler(new HandlerClasses());
            parser.parse(is);
        } catch (SAXException sAXException) {
            OPDE.fatal(sAXException);
        } catch (IOException iOException) {
            OPDE.fatal(iOException);
        }

    }

    /**
     * Welche Klassen können als Unterprogramm aufgerufen werden ?  Stehen dann links oben auf der Startleiste.
     */
    public ArrayList<InternalClass> getMainClasses() {
        return mainClasses;
    }

    /**
     * Ein Lookup Table, der zu einer internen Klassenbezeichnung die Informationen als InternalClass liefert.
     */
    public HashMap<String, InternalClass> getInternalClasses() {
        return internalClasses;
    }

    /**
     * Diese Methode "kann man um Erlaubnis fragen". Sie sieht dann zu einer Klassenbezeichnung und einem gewünschten
     * Zugriff nach, ob das erlaubt ist oder nicht. Die DB Tabelle "ACL" enthält zuordnungen der jeweiligen ACLs zu
     * Gruppen. Anhand dieser Einträge wird ermittelt, ob eine Operation erlaubt ist oder nicht. Natürlich darf ein
     * Admin immer alles.
     *
     * @param acl
     * @param internalClassID - Interne Klassenname, wie in context.properties vereinbart. Nicht zu verwechseln mit den
     *                        Java Klassennamen.
     * @return
     */
    public boolean isAllowedTo(short acl, String internalClassID) {
        boolean allowed = true;
        if (!OPDE.isAdmin()) {
            EntityManager em = OPDE.createEM();
            Query query = em.createQuery("SELECT DISTINCT i FROM SYSGROUPS2ACL i "
                    + "WHERE i.internalClassID = :internalClassID AND :user MEMBER OF i.opgroups.members ");
            query.setParameter("user", OPDE.getLogin().getUser());
            query.setParameter("internalClassID", internalClassID);


            try {
                allowed = false;
                ArrayList<SYSGROUPS2ACL> listGROUPS = new ArrayList<SYSGROUPS2ACL>(query.getResultList());

                ArrayList<Acl> listAllowed = new ArrayList<Acl>();
                for (SYSGROUPS2ACL sgAcl : listGROUPS) {
                    listAllowed.addAll(sgAcl.getAclCollection());
                }

                for (Acl aclsForGroup : listAllowed) {
                    if (aclsForGroup.getAcl() == acl) {
                        allowed = true;
                        break;
                    }
                }
            } catch (NoResultException e) {
                allowed = false;
            }
            em.close();
        }
        return allowed;
    }

//    public SortedProperties getDefaultProperties() {
//        return defaultProperties;
//    }

    public String getVersion() {
        return context.getProperty("opde.major") + "." + context.getProperty("opde.minor") + "." + context.getProperty("opde.release") + "." + context.getProperty("buildNumber");
    }


    public String getBuildInformation() {
        return "[" + builddate + "|" + System.getProperty("java.version") + "]";
    }

    public String getVersionVerbose() {
        return getVersion() + " " + getBuildInformation();
    }


    public String getProgname() {
        return progname;
    }

    private class HandlerClasses extends DefaultHandler {

        // Gibt an, in welcher Hauptumgebung sich der Parser gerade befindet. Ist er gerade im Classes Block,
        // oder im Properties Block oder im Database Block.
        String environment = "";

        InternalClass thisClass;

        HandlerClasses() {
        }

        @Override
        public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {

            // Entweder die Environment wechselt gerade
            if (tagName.equalsIgnoreCase("classes") || tagName.equalsIgnoreCase("database") || tagName.equalsIgnoreCase("properties")) {
                environment = tagName;
            } else if (environment.equalsIgnoreCase("classes")) { // oder wir sind schon in einer Umgebung.
                if (tagName.equalsIgnoreCase("class")) {
                    thisClass = new InternalClass(attributes.getValue("name"), SYSTools.xx(attributes.getValue("short")), SYSTools.xx(attributes.getValue("long")), SYSTools.catchNull(attributes.getValue("main")).equalsIgnoreCase("true"), attributes.getValue("javaclass"), SYSTools.catchNull(attributes.getValue("icon"), "run.png"), attributes.getValue("helpurl"));
//                    OPDE.debug(thisClass.getInternalClassID());
                } else if (tagName.equalsIgnoreCase("insert")) {
                    thisClass.getPossibleACLs().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.INSERT));
                } else if (tagName.equalsIgnoreCase("select")) {
                    thisClass.getPossibleACLs().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.SELECT));
                } else if (tagName.equalsIgnoreCase("delete")) {
                    thisClass.getPossibleACLs().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.DELETE));
                } else if (tagName.equalsIgnoreCase("update")) {
                    thisClass.getPossibleACLs().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.UPDATE));
                } else if (tagName.equalsIgnoreCase("grant")) {
                    thisClass.getPossibleACLs().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.GRANT));
                } else if (tagName.equalsIgnoreCase("execute")) {
                    thisClass.getPossibleACLs().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.EXECUTE));
                } else if (tagName.equalsIgnoreCase("archive")) {
                    thisClass.getPossibleACLs().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.ARCHIVE));
                } else if (tagName.equalsIgnoreCase("manager")) {
                    thisClass.getPossibleACLs().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.MANAGER));
                } else if (tagName.equalsIgnoreCase("cancel")) {
                    thisClass.getPossibleACLs().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.CANCEL));
                } else if (tagName.equalsIgnoreCase("print")) {
                    thisClass.getPossibleACLs().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.PRINT));
                } else if (tagName.equalsIgnoreCase("user1")) {
                    thisClass.getPossibleACLs().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.USER1));
                } else if (tagName.equalsIgnoreCase("user2")) {
                    thisClass.getPossibleACLs().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.USER2));
                } else if (tagName.equalsIgnoreCase("user3")) {
                    thisClass.getPossibleACLs().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.USER3));
                } else if (tagName.equalsIgnoreCase("user4")) {
                    thisClass.getPossibleACLs().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.USER4));
                }

            } else if (environment.equalsIgnoreCase("properties")) {
                if (tagName.equalsIgnoreCase("property")) {
                    SimpleDateFormat sdf = null;
                    if (attributes.getValue("format") != null) {
                        sdf = new SimpleDateFormat(attributes.getValue("format"));
                    }
                    if (attributes.getValue("value").equals("##now##")) {
                        defaultProperties.put(attributes.getValue("key"), sdf.format(new Date()));
                    } else {
                        defaultProperties.put(attributes.getValue("key"), attributes.getValue("value"));
                    }
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName.equalsIgnoreCase("class")) {
                Collections.sort(thisClass.getPossibleACLs());
                internalClasses.put(thisClass.getInternalClassID(), thisClass);
                if (thisClass.isMainClass()) {
                    mainClasses.add(thisClass);
                }
                thisClass = null;
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

    }

    public String getProperty(String key) {
        return context.getProperty(key);
    }


    public static String getUserTemplatePath() throws IOException {
        return LocalMachine.getAppDataPath() + File.separator + dirTemplates;
    }


    public static String getSystemTemplatePath() throws IOException {
        return LocalMachine.getProgrammPath() + File.separator + dirTemplates;
    }

    public static File getTemplate(String templateName) throws IOException {
        String userTemplatePath = getUserTemplatePath() + File.separator + templateName;
        String systemTemplatePath = getSystemTemplatePath() + File.separator + templateName;

        File user = new File(userTemplatePath);
        File sys = new File(systemTemplatePath);

        OPDE.debug(user.getAbsoluteFile() + " " + user.exists());
        OPDE.debug(sys.getAbsoluteFile() + " " + sys.exists());

        return user.exists() ? user : sys;
    }

    public static String getSQLScriptPath() {
//        return "/Volumes/Volume1/Dropbox/opde/install4j/dbscripts";
        return LocalMachine.getProgrammPath() + File.separator + dirSql;
    }

    public static File getSQLUpdateScript(int startVersion) {
        return new File(getSQLScriptPath() + File.separator + startVersion + "to" + (startVersion + 1) + ".sql");
    }

    public static File getSQLStructureScript(int version) {
        return new File(getSQLScriptPath() + File.separator + "structure-" + version + ".sql");
    }

    public static File getSQLBaseContentScript(int version) {
        return new File(getSQLScriptPath() + File.separator + "content-base-" + version + ".sql");
    }

    public static File getSQLMedContentScript(int version) {
        return new File(getSQLScriptPath() + File.separator + "content-med-" + version + ".sql");
    }

    public static File getFinallyContentScript(int version) {
        return new File(getSQLScriptPath() + File.separator + "content-finally-" + version + ".sql");
    }

    public static String getOPCache() {
        return LocalMachine.getAppDataPath() + File.separator + dirCache;
    }

    public int getDbversion() {
        return dbversion;
    }

    public String getSignature() {
        return (OPDE.getLogin() != null ? SYSTools.htmlUmlautConversion(OPDE.getLogin().getUser().getUIDCiphered()) : "") + "; " + DateFormat.getDateTimeInstance().format(new Date())
                + "; " + OPDE.getAppInfo().getProgname() + ", v" + OPDE.getAppInfo().getVersion();
    }

}
