/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package op.tools;

import op.OPDE;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Die AppInfo dient dazu Informationen bzgl. der Module innerhalb von OPDE
 * aus der Datei appinfo.xml einzulesen und während der Laufzeit
 * bereit zu halten. Die xml Datei befindet sich im JAR Archiv und wird
 * nur währen der Entwicklungsphase bearbeitet.
 * <p/>
 * Diese Klasse implementiert ein Sicherheitskonzept für OPDE. Zu jeder Klasse
 * können 11 Rechte definiert werden, die dem jeweiligen Benutzer gewährt
 * oder verweigert werden.
 * Welche Rechte für eine betreffende Klasse von Bedeutung sind wird
 * innerhalb der Applikation in besagter xml Datei festgelegt.
 * Da diese Informationen spezifisch für eine Programmversion sind
 * sind sie hier als "quasi Konstanten" definiert werden.
 * <p/>
 * Außerdem
 *
 * @author tloehr
 */
public class AppInfo {

    private HashMap<String, InternalClass> internalClasses;

    private ArrayList<InternalClass> mainClasses;

    /**
     * Angabe darüber, welches Datenbank Schema diese Version des Programms unbedingt braucht.
     */
    private ArrayList<Integer> dbschema;

    /**
     * Diese defaultsProperties werden gebraucht, wenn der Client zum ersten mal eingerichtet wird. Dann stehen hier
     * die unbedingt erforderlichen Konfigurationen drin. Diese werden dann in die local.properties übernommen,
     * damit es nicht zu Exceptions kommt.
     */
    private SortedProperties defaultProperties;

    /**
     * Hier stehen Versions und Build Informationen drin. Diese stammen aus der appinfo.properties, die teilweise automatisch
     * von einem ANT script geändert wird.
     */
    private Properties appinfo;

    private String version;
    private String build;
    private String progname;

    public AppInfo() {

        appinfo = new Properties();
        internalClasses = new HashMap();
        mainClasses = new ArrayList<InternalClass>();
        dbschema = new ArrayList<Integer>();
        defaultProperties = new SortedProperties();

        try {

            // lade appinfo.properties
            InputStream in2 = OPDE.class.getResourceAsStream("/appinfo.properties");
            appinfo.load(in2);
            in2.close();

            progname = appinfo.getProperty("program.PROGNAME");
            version = appinfo.getProperty("program.VERSION");
            build = appinfo.getProperty("program.BUILDNUM");

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
     * @param classname - Interne Klassenname, wie in appinfo.properties vereinbart. Nicht zu verwechseln mit den Java Klassennamen.
     * @param acl
     * @return
     */
    public boolean userHasAccessLevelForThisClass(String classname, short acl) {
        boolean allowed = true;
        if (!OPDE.isAdmin()) {
            EntityManager em = OPDE.createEM();
            Query query = em.createNamedQuery("IntClasses.findByUserAndClassnameAndACL");
            query.setParameter("ocuser", OPDE.getLogin().getUser());
            query.setParameter("classname", classname);
            query.setParameter("shortacl", acl);
            allowed = !query.getResultList().isEmpty();
            em.close();
        }
        return allowed;
    }

    public ArrayList<Integer> getDBschema() {
        return dbschema;
    }

    public SortedProperties getDefaultProperties() {
        return defaultProperties;
    }

    public String getVersion() {
        return version;
    }

    public String getBuild() {
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
                    thisClass = new InternalClass(attributes.getValue("name"), attributes.getValue("short"), attributes.getValue("long"), SYSTools.catchNull(attributes.getValue("main")).equalsIgnoreCase("true"), attributes.getValue("javaclass"), SYSTools.catchNull(attributes.getValue("icon"), "run.png"));
//                    OPDE.debug(thisClass.getInternalClassname());
                } else if (tagName.equalsIgnoreCase("insert")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.INSERT));
                } else if (tagName.equalsIgnoreCase("select")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.SELECT));
                } else if (tagName.equalsIgnoreCase("delete")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.DELETE));
                } else if (tagName.equalsIgnoreCase("update")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.UPDATE));
                } else if (tagName.equalsIgnoreCase("grant")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.GRANT));
                } else if (tagName.equalsIgnoreCase("execute")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.EXECUTE));
                } else if (tagName.equalsIgnoreCase("archive")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.ARCHIVE));
                } else if (tagName.equalsIgnoreCase("manager")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.MANAGER));
                } else if (tagName.equalsIgnoreCase("cancel")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.CANCEL));
                } else if (tagName.equalsIgnoreCase("print")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.PRINT));
                } else if (tagName.equalsIgnoreCase("user1")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.USER1));
                } else if (tagName.equalsIgnoreCase("user2")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.USER2));
                } else if (tagName.equalsIgnoreCase("user3")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.USER3));
                } else if (tagName.equalsIgnoreCase("user4")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("langbundle"), InternalClassACL.USER4));
                }

            } else if (environment.equalsIgnoreCase("database")) {
                if (tagName.equalsIgnoreCase("schema")) {
                    dbschema.add(Integer.parseInt(attributes.getValue("version")));
                }
            } else if (environment.equalsIgnoreCase("properties")) {
                if (tagName.equalsIgnoreCase("property")) {
                    defaultProperties.put(attributes.getValue("key"), attributes.getValue("value"));
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName.equalsIgnoreCase("class")) {
                internalClasses.put(thisClass.getInternalClassname(), thisClass);
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
}
