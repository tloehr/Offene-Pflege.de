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

import javax.persistence.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Die InternalClasses dient dazu Informationen bzgl. der Module innerhalb von OPDE
 * aus der Datei internalclasses.xml einzulesen und währen der Laufzeit
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
public class InternalClasses {

    /**
     * Ein Lookup Table, der zu einer internen Klassenbezeichnung die Informationen als InternalClass liefert.
     */
    private HashMap<String, InternalClass> internalClasses;
    /*
     * Damit man nachher schnell suchen kann, welche Klassen "sich gegenseitig" ins Gehege kommen
     * könnten, benötige ich diese beiden Listen.
     */
    private HashMap<String, List<String>> signedCollisionByClass, unsignedCollisionByClass, classByUnsignedCollision, classBySignedCollision, mainClassByUnsignedCollision;

    public InternalClasses() {
        internalClasses = new HashMap();
        signedCollisionByClass = new HashMap();
        unsignedCollisionByClass = new HashMap();
        classByUnsignedCollision = new HashMap();
        classBySignedCollision = new HashMap();
        mainClassByUnsignedCollision = new HashMap();

        try {
            XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            InputSource is = new org.xml.sax.InputSource(OPDE.class.getResourceAsStream("/internalclasses.xml"));
            parser.setContentHandler(new HandlerClasses());
            parser.parse(is);
        } catch (SAXException sAXException) {
            OPDE.fatal(sAXException);
        } catch (IOException iOException) {
            OPDE.fatal(iOException);
        }
    }

    public boolean hasCollisions(String internalClassName) {
        return !(getClassesWithSignedCollisions(internalClassName) + getClassesWithUnsignedCollisions(internalClassName)).equals("");
    }

    /**
     * Diese Methode erzeugt eine Liste von Klassen, die mit der übergebenen Klasse kollidieren würden.
     * In diesem Fall geht es um die Kollisionen <b>ohne</b> Signaturen. Dabei wird der Fall unterschieden,
     * ob die übergebene Klasse eine MainClass ist oder nicht. Demnach unterscheidet sich die zurückgegebene
     * Liste.
     *
     * @param internalClassName
     * @return Dies ist die Liste der kollidierenden Klassen. Die einzelnen Klassen sind durch Kommata getrennt. Dadurch kann man diesen
     *         String in eine JPQL Ausdruck einbauen. Also in der Art: <code>s.class IN ('A','B','C')</code>
     */
    public String getClassesWithUnsignedCollisions(String internalClassName) {
        String list = "";
        Iterator<String> collisionIDs = null;

        if (unsignedCollisionByClass.containsKey(internalClassName)) {
            collisionIDs = unsignedCollisionByClass.get(internalClassName).iterator();
        }

        while (collisionIDs != null && collisionIDs.hasNext()) {
            String collisionID = collisionIDs.next();
            boolean mainClass = mainClassByUnsignedCollision.containsKey(internalClassName) && mainClassByUnsignedCollision.get(internalClassName).contains(collisionID);

            Iterator<String> conflicitingClasses = null;
            // Wenn eine MainClass starten will, dann geht das nur, wenn KEIN anderer aus der CollisionDomain läuft.
            // Daher enthält die Liste dann alle möglichen Konflikte
            if (mainClass) {
                conflicitingClasses = classByUnsignedCollision.get(collisionID).iterator();
            } else { // Wenn eine normale Klasse starten will, dann geht das nur, wenn keine MainClass läuft.
                conflicitingClasses = mainClassByUnsignedCollision.get(collisionID).iterator();
            }

            while (conflicitingClasses.hasNext()) {
                String classname = conflicitingClasses.next();
                list += "'" + classname + "'" + (conflicitingClasses.hasNext() ? "," : "");
            }
        }
        return list;
    }

    /**
     * Diese Methode erzeugt eine Liste von Klassen, die mit der übergebenen Klasse kollidieren würden.
     * In diesem Fall geht es um die Kollisionen <b>mit</b> Signaturen.
     *
     * @param internalClassName
     * @return Dies ist die Liste der kollidierenden Klassen. Die einzelnen Klassen sind durch Kommata getrennt. Dadurch kann man diesen
     *         String in eine JPQL Ausdruck einbauen. Also in der Art: <code>s.class IN ('A','B','C')</code>
     */
    public String getClassesWithSignedCollisions(String internalClassName) {
        String list = "";
        Iterator<String> collisionIDs = null;

        if (signedCollisionByClass.containsKey(internalClassName)) {
            collisionIDs = signedCollisionByClass.get(internalClassName).iterator();
        }

        while (collisionIDs != null && collisionIDs.hasNext()) {
            Iterator<String> conflicitingClasses;
            conflicitingClasses = classBySignedCollision.get(collisionIDs.next()).iterator();

            while (conflicitingClasses.hasNext()) {
                String classname = conflicitingClasses.next();
                list += "'" + classname + "'" + (conflicitingClasses.hasNext() ? "," : "");
            }
        }
        return list;
    }

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
            Query query = OPDE.getEM().createNamedQuery("IntClasses.findByUserAndClassnameAndACL");
            query.setParameter("ocuser", OPDE.getLogin().getUser());
            query.setParameter("classname", classname);
            query.setParameter("shortacl", acl);
            allowed = !query.getResultList().isEmpty();
        }
        return allowed;
    }

    private class HandlerClasses extends DefaultHandler {

        InternalClass thisClass;

        HandlerClasses() {
        }

        @Override
        public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
            if (!tagName.equalsIgnoreCase("classes")) {
                //OPDE.getLogger().debug(this.toString() + ":" + tagName);
                if (tagName.equalsIgnoreCase("class")) {
                    thisClass = new InternalClass(attributes.getValue("name"), attributes.getValue("short"), attributes.getValue("long"));
                } else if (tagName.equalsIgnoreCase("insert")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("doc"), InternalClassACL.INSERT));
                } else if (tagName.equalsIgnoreCase("select")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("doc"), InternalClassACL.SELECT));
                } else if (tagName.equalsIgnoreCase("delete")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("doc"), InternalClassACL.DELETE));
                } else if (tagName.equalsIgnoreCase("update")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("doc"), InternalClassACL.UPDATE));
                } else if (tagName.equalsIgnoreCase("grant")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("doc"), InternalClassACL.GRANT));
                } else if (tagName.equalsIgnoreCase("execute")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("doc"), InternalClassACL.EXECUTE));
                } else if (tagName.equalsIgnoreCase("archive")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("doc"), InternalClassACL.ARCHIVE));
                } else if (tagName.equalsIgnoreCase("manager")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("doc"), InternalClassACL.MANAGER));
                } else if (tagName.equalsIgnoreCase("cancel")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("doc"), InternalClassACL.CANCEL));
                } else if (tagName.equalsIgnoreCase("print")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("doc"), InternalClassACL.PRINT));
                } else if (tagName.equalsIgnoreCase("user1")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("doc"), InternalClassACL.USER1));
                } else if (tagName.equalsIgnoreCase("user2")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("doc"), InternalClassACL.USER2));
                } else if (tagName.equalsIgnoreCase("user3")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("doc"), InternalClassACL.USER3));
                } else if (tagName.equalsIgnoreCase("user4")) {
                    thisClass.getAcls().add(new InternalClassACL(attributes.getValue("doc"), InternalClassACL.USER4));
                } else if (tagName.equalsIgnoreCase("collideswith")) {
                    String collisionDomain = attributes.getValue("id");
                    boolean signed = attributes.getValue("signature").equalsIgnoreCase("true");
                    boolean mainClass = SYSTools.catchNull(attributes.getValue("mainClass")).equalsIgnoreCase("true");

                    if (signed) {
                        // Wenn nötig leere Listen erzeugen.
                        if (!signedCollisionByClass.containsKey(thisClass.getInternalClassname())) {
                            signedCollisionByClass.put(thisClass.getInternalClassname(), new ArrayList<String>());
                        }
                        if (!classBySignedCollision.containsKey(collisionDomain)) {
                            classBySignedCollision.put(collisionDomain, new ArrayList<String>());
                        }

                        signedCollisionByClass.get(thisClass.getInternalClassname()).add(collisionDomain);
                        classBySignedCollision.get(collisionDomain).add(thisClass.getInternalClassname());

                    } else {

                        if (!unsignedCollisionByClass.containsKey(thisClass.getInternalClassname())) {
                            unsignedCollisionByClass.put(thisClass.getInternalClassname(), new ArrayList<String>());
                        }
                        if (!classByUnsignedCollision.containsKey(collisionDomain)) {
                            classByUnsignedCollision.put(collisionDomain, new ArrayList<String>());
                        }

                        unsignedCollisionByClass.get(thisClass.getInternalClassname()).add(collisionDomain);
                        classByUnsignedCollision.get(collisionDomain).add(thisClass.getInternalClassname());

                        if (mainClass) {
                            if (!mainClassByUnsignedCollision.containsKey(thisClass.getInternalClassname())) {
                                mainClassByUnsignedCollision.put(collisionDomain, new ArrayList<String>());
                            }
                            mainClassByUnsignedCollision.get(collisionDomain).add(thisClass.getInternalClassname());
                        }
                    }
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName.equalsIgnoreCase("class")) {
                internalClasses.put(thisClass.getInternalClassname(), thisClass);
                thisClass = null;
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

    }
}
