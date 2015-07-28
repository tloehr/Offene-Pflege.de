/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package op.system;

import entity.system.Acl;
import entity.system.SYSGROUPS2ACL;
import op.OPDE;
import op.tools.Hardware;
import op.tools.SYSTools;
import op.tools.SortedProperties;
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
import java.util.*;

/**
 * Die AppInfo dient dazu Informationen bzgl. der Module innerhalb von OPDE
 * aus der Datei appinfo.xml einzulesen und während der Laufzeit
 * bereit zu halten. Die xml Datei befindet sich im JAR Archiv und wird
 * nur währen der Entwicklungsphase bearbeitet.
 * <p>
 * Diese Klasse implementiert ein Sicherheitskonzept für OPDE. Zu jeder Klasse
 * können 11 Rechte definiert werden, die dem jeweiligen Benutzer gewährt
 * oder verweigert werden.
 * Welche Rechte für eine betreffende Klasse von Bedeutung sind wird
 * innerhalb der Applikation in besagter xml Datei festgelegt.
 * Da diese Informationen spezifisch für eine Programmversion sind
 * sind sie hier als "quasi Konstanten" definiert werden.
 * <p>
 * Außerdem
 *
 * @author tloehr
 */
public class AppInfo {

    public static final String fileConfig = "opde.cfg";
    public static final String fileNewuser = "newuser.html";
    public static final String filePrinters = "printers.xml";
    public static final String fileStandardCSS = "standard.css";

    public static final String dirCache = "cache";
    public static final String dirArtwork = "artwork";
    public static final String dirTemplates = "templates";

//    public static final String dirJar = "jar";
    public static final String dirArtwork16 = "16x16";
    public static final String dirArtwork22 = "22x22";
    public static final String dirArtwork48 = "48x48";
    public static final String dirArtwork64 = "64x64";
    public static final String dirBase = "opde";


    private HashMap<String, InternalClass> internalClasses;

    private ArrayList<InternalClass> mainClasses;

    /**
     * Diese defaultsProperties werden gebraucht, wenn der Client zum ersten mal eingerichtet wird. Dann stehen hier
     * die unbedingt erforderlichen Konfigurationen drin. Diese werden dann in die opde.cfg übernommen,
     * damit es nicht zu Exceptions kommt.
     */
    private SortedProperties defaultProperties;

//    private String updateCheckUrl;

    /**
     * Hier stehen Versions und Build Informationen drin. Diese stammen aus der appinfo.properties, die teilweise automatisch
     * von einem ANT script geändert wird.
     */
    private Properties appinfo;

    private String version;
    private int build, dbversion;
    private String progname;

    public AppInfo() {

        appinfo = new Properties();
        internalClasses = new HashMap();
        mainClasses = new ArrayList<InternalClass>();
        defaultProperties = new SortedProperties();

        try {

            // lade appinfo.properties
            InputStream in2 = OPDE.class.getResourceAsStream("/appinfo.properties");
            appinfo.load(in2);
            in2.close();

            progname = appinfo.getProperty("program.PROGNAME");
            version = appinfo.getProperty("program.MAJOR") + "." + appinfo.getProperty("program.MINOR") + "." + appinfo.getProperty("program.RELEASE");
            dbversion = Integer.parseInt(appinfo.getProperty("program.DBVERSION"));
            build = Integer.parseInt(appinfo.getProperty("program.BUILDNUM"));
//            updateCheckUrl = appinfo.getProperty("program.UPDATECHECKURL");

            // parse appinfo.xml
            XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            InputSource is = new org.xml.sax.InputSource(OPDE.class.getResourceAsStream("/appinfo.xml"));
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
     * Diese Methode "kann man um Erlaubnis fragen". Sie sieht dann
     * zu einer Klassenbezeichnung und einem gewünschten Zugriff
     * nach, ob das erlaubt ist oder nicht.
     * Die DB Tabelle "ACL" enthält zuordnungen der jeweiligen ACLs zu Gruppen. Anhand dieser Einträge wird ermittelt, ob
     * eine Operation erlaubt ist oder nicht. Natürlich darf ein Admin immer alles.
     *
     * @param acl
     * @param internalClassID - Interne Klassenname, wie in appinfo.properties vereinbart. Nicht zu verwechseln mit den Java Klassennamen.
     * @return
     */
    public boolean isAllowedTo(short acl, String internalClassID) {
        boolean allowed = true;
        if (!OPDE.isAdmin()) {
            EntityManager em = OPDE.createEM();
            Query query = em.createQuery("SELECT DISTINCT i FROM SYSGROUPS2ACL i "
                    + "WHERE i.internalClassID = :internalClassID AND :user MEMBER OF i.groups.members ");
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

    public SortedProperties getDefaultProperties() {
        return defaultProperties;
    }

    public String getVersion() {
        return version;
    }

    public int getBuildnum() {
        return build;
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


    public static File getTemplate(String templateName) throws IOException {
           String userTemplatePath = OPDE.getOPWD() + File.separator + dirTemplates + File.separator + templateName;
           String systemTemplatePath = Hardware.getProgrammPath() + File.separator + dirTemplates + File.separator + templateName;

           File user = new File(userTemplatePath);
           File sys = new File(systemTemplatePath);

           return user.exists() ? user : sys;
       }

    public int getDbversion() {
        return dbversion;
    }

    public String getSignature() {
        return (OPDE.getLogin() != null ? SYSTools.htmlUmlautConversion(OPDE.getLogin().getUser().getUID()) : "") + "; " + DateFormat.getDateTimeInstance().format(new Date())
                + "; " + OPDE.getAppInfo().getProgname() + ", v" + OPDE.getAppInfo().getVersion() + "/" + OPDE.getAppInfo().getBuildnum();
    }

//    public String getUpdateCheckUrl() {
//        return updateCheckUrl;
//    }
}
