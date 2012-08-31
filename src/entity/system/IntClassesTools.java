/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.system;

import op.OPDE;
import op.system.InternalClass;
import op.system.InternalClassACL;

import java.util.*;

/**
 * @author tloehr
 */
public class IntClassesTools {

    public static final int ROOT = 0;
    public static final int SELECTION = 1;

//    /**
//     * Diese Klasse erzeugt eine Baumstruktur. Sie enthält alle übergebenen Klassen.
//     * Anhand der InternalClasses (nicht zu verwechseln mit IntClasses) bestimmt sie
//     * die erlaubten ACLs. Die übergebene Collection der EntityBeans (das sind jetzt
//     * IntClasses) enthält, welche Rechte für die entsprechende Gruppe gesetzt
//     * wurden. So werden die vorselektierten Pfade gesetzt.
//     * Der Baum hat die Tiefe 3. Die Klassen der einzelnen Ebenen sind festgelegt: (String GKennung, InternalClass, InternalClassACL)
//     *
//     * This methode creates a tree structure which contains all classes described in /appinfo.xml.
//     * A common "primary key" between the internal XML structure and the actual JPA entity entity.system.IntClasses is a string "name" tag in XML, classname in the entity and
//     * internalClassID in many actual java classes used in OPDE.
//     *
//     *
//     *
//     * @param Gruppe, für die die Klassen in Baumform benötigt werden.
//     * @return eine Liste mit zwei Einträgen (MutableTreeNode root, TreePath[] selection).
//     */
//    public static ArrayList getIntClassesAsTree(Groups group) {
//        HashMap<String, IntClasses> dbClasses = getDBLookupTable(group);
//        DefaultMutableTreeNode root = new DefaultMutableTreeNode(group);
//
//        // Alle bekannten Classen und deren möglichen Rechte (anhand internalclasses.xml) ermitteln.
//        ArrayList<InternalClass> classset = new ArrayList(OPDE.getAppInfo().getInternalClasses().values());
//        Collections.sort(classset);
//        Iterator<InternalClass> itClasses = classset.iterator();
//
//        ArrayList<TreePath> selection = new ArrayList();
//        while (itClasses.hasNext()) { // Klasse als Knoten einfügen
//            InternalClass currentClass = itClasses.next();
//
//            // Nur solche Klassen hinzufügen,
//            // die auch ACLs besitzen. Es gibt auch
//            // Klassendefinitionen, die nur für die Kollisionsdefinition
//            // da sind. Die brauchen manchmaln gar keine ACLs. (z.B. BHPImport)
//            if (!currentClass.getAcls().isEmpty()) {
//
//                String classname = currentClass.getInternalClassID();
//
//                currentClass.setIntClass(dbClasses.get(classname));
//
//                DefaultMutableTreeNode classnode = new DefaultMutableTreeNode(OPDE.getAppInfo().getInternalClasses().get(classname));
//                root.add(classnode);
//
//                // Alle ACLs, die diese Klasse kennt.
//                List<InternalClassACL> keyset = OPDE.getAppInfo().getInternalClasses().get(classname).getAcls();
//                Collections.sort(keyset);
//
//                Iterator<InternalClassACL> itACLs = keyset.iterator();
//                while (itACLs.hasNext()) {
//                    // Diese acl kennt die Klasse
//
//                    InternalClassACL currentacl = itACLs.next();
//                    // Machen wir einen Knoten draus.
//                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(currentacl);
//                    classnode.add(node);
//
//                    // Admin hat IMMER alle Rechte.
//                    if (group.getID().equalsIgnoreCase("admin")) {
//                        selection.add(new TreePath(new Object[]{root, classnode, node}));
//                    } else {
//                        // Ist bei der übergebenen Gruppe dieses Recht auch dabei ?
//                        // Falls ja, Selection Pfad hinzufügen.
//                        if (dbClasses.containsKey(classname)) {
//                            Acl[] acls = dbClasses.get(classname).getAclCollection().toArray(new Acl[]{});
//                            for (int i = 0; i < acls.length; i++) {
//                                if (currentacl.getAcl() == acls[i].getAcl()) {
//                                    OPDE.debug("selected ACL found");
//                                    currentacl.setAclEntity(acls[i]);
//                                    selection.add(new TreePath(new Object[]{root, classnode, node}));
//                                    break;
//                                }
//                            }
//
//                        }
//                    }
//                }
//            }
//        }
//
//        dbClasses.clear();
//
//        ArrayList result = new ArrayList(2);
//        result.add(0, root);
//        result.add(1, selection.toArray(new TreePath[]{}));
//
//        return result;
//    }
//
//    public static void saveTree(DefaultMutableTreeNode node, CheckTreeSelectionModel selmodel) {
//        if (node.getUserObject() instanceof Groups) { // Root
//            boolean rootWasSelected = selmodel.isPathSelected(new TreePath(node.getPath()));
//            OPDE.debug("saveTree: speichere Gruppe: " + ((Groups) node.getUserObject()).getID());
//            OPDE.debug("saveTree: Gruppe war " + (rootWasSelected ? "" : "NICHT") + " ausgewählt.");
//            Enumeration<DefaultMutableTreeNode> enumInternalClasses = node.children();
//            while (enumInternalClasses.hasMoreElements()) {
//                saveTree(enumInternalClasses.nextElement(), selmodel, rootWasSelected);
//            }
//        }
//    }
//
//    public static void saveTree(DefaultMutableTreeNode node, CheckTreeSelectionModel selmodel, boolean parentWasSelected) {
//        EntityManager em = OPDE.createEM();
//        if (node.getUserObject() instanceof InternalClass) {
//
//            InternalClass myInternalClass = (InternalClass) node.getUserObject();
//            if (!myInternalClass.hasIntClass()) { // Bisher keine EntityBean ?
//                Groups group = (Groups) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
//                IntClasses intClasses = new IntClasses(myInternalClass.getInternalClassID(), group);
//                em.persist(intClasses);
//                myInternalClass.setIntClass(intClasses);
//                OPDE.debug("       saveTree: speichere EntityBean IntClass: " + myInternalClass.getInternalClassID());
//            } else {
//                OPDE.debug("       saveTree: hat schon EntityBean: " + myInternalClass.getIntClass().getJavaclass());
//                OPDE.debug("       saveTree: und zwar für die Gruppe: " + myInternalClass.getIntClass().getGroups().getID());
//
//            }
//            OPDE.debug("       saveTree: sie war " + (selmodel.isPathSelected(new TreePath(node.getPath())) ? "" : "NICHT") + " ausgewählt.");
//
//            Enumeration<DefaultMutableTreeNode> enumInternalClasses = node.children();
//            while (enumInternalClasses.hasMoreElements()) {
//                DefaultMutableTreeNode child = enumInternalClasses.nextElement();
//                saveTree(child, selmodel, parentWasSelected || selmodel.isPathSelected(new TreePath(node.getPath())));
//            }
//
//            myInternalClass.setIntClass(null);
//
//        } else if (node.getUserObject() instanceof InternalClassACL) { // Leaf
//            InternalClassACL myACL = (InternalClassACL) node.getUserObject();
//            InternalClass myClass = (InternalClass) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
//            boolean shouldBeSelected = parentWasSelected || selmodel.isPathSelected(new TreePath(node.getPath()));
//
//
//            if (shouldBeSelected) { // ACL gewähren
//                OPDE.debug("              saveTree: SPEICHERE ACL " + InternalClassACL.strACLS[myACL.getAcl()]);
//                OPDE.debug("              saveTree: " + InternalClassACL.strACLS[myACL.getAcl()] + " hat " + (myACL.hasAclEntity() ? "eine" : "keine") + " Entity Bean");
//                if (!myACL.hasAclEntity()) { // Nur hinzufügen, wenn nötig.
//                    OPDE.debug("              saveTree: erstelle neue EntityBean");
//                    Acl acl = new Acl(myACL.getAcl(), myClass.getIntClass());
//                    em.persist(acl);
//                } else {
//                    OPDE.debug("              saveTree: für die Gruppe: " + myACL.getAclEntity().getIntclass().getGroups().getID());
//                }
//            } else { // ACL entziehen
//                OPDE.debug("              saveTree: LÖSCHE ACL " + InternalClassACL.strACLS[myACL.getAcl()]);
//                OPDE.debug("              saveTree: " + InternalClassACL.strACLS[myACL.getAcl()] + " hat " + (myACL.hasAclEntity() ? "eine" : "keine") + " Entity Bean");
//                if (myACL.hasAclEntity()) { // Nur löschen, wenn nötig.
//                    OPDE.debug("              saveTree: lösche jetzt die EntityBean");
//                    em.remove(myACL.getAclEntity());
//                    OPDE.debug("              saveTree: für die Gruppe: " + myACL.getAclEntity().getIntclass().getGroups().getID());
//                }
//            }
//            myACL.setAclEntity(null);
//        }
//        em.close();
//    }

    /**
     * creates a hashmap which contains all assigned IntClasses for a group. the key
     * index is the internalClassesID String.
     * @param group
     * @return
     */
    public static HashMap<String, IntClasses> getIntClassesMap(Groups group) {
        HashMap<String, IntClasses> dblookup = new HashMap();
        if (group.getIntClasses() != null) {
            Iterator<IntClasses> it = group.getIntClasses().iterator();
            while (it.hasNext()) {
                IntClasses thisClass = it.next();
                dblookup.put(thisClass.getInternalClassID(), thisClass);
            }
        }
        return dblookup;
    }

    /**
     * searches for a Acl which is assigned to intClasses according to the aclCode.
     *
     * @param intClasses
     * @param aclCode
     * @return the found Acl, null if not found or incClasses == null
     */
    public static Acl findACLbyCODE(IntClasses intClasses, short aclCode){
        Acl result = null;
        if (intClasses == null){
            return null;
        }
        for (Acl acl : intClasses.getAclCollection()){
            if (acl.getAcl() == aclCode){
                result = acl;
                break;
            }
        }
        return result;
    }

//    public static void clearEntitiesFromAllInternalClasses() {
//        ArrayList<InternalClass> classset = new ArrayList(OPDE.getAppInfo().getInternalClasses().values());
//        Iterator<InternalClass> itClasses = classset.iterator();
//
//        while (itClasses.hasNext()) {
//            clearEntitiesFromAllInternalClasses(itClasses.next());
//        }
//    }
//
//    public static void clearEntitiesFromAllInternalClasses(InternalClass thisclass) {
//        thisclass.setIntClass(null);
//        Iterator<InternalClassACL> itacls = thisclass.getAcls().iterator();
//
//        while (itacls.hasNext()) {
//            itacls.next().setAclEntity(null);
//        }
//
//    }
//    public static void setAcl(Groups group, boolean add, List<Acl> toBeDeleted) {
//        // Welche ACLs kennt die Klasse denn ?
//        // Applikation fragen
//        // Als "virtueller" PK fungiert hier die interne Klassenbezeichnung.
//        List<InternalClass> alleKlassen = new ArrayList(OPDE.internalClasses.getInternalClasses().values());
//        HashMap<String, IntClasses> dblookup = getDBLookupTable(group);
//
//        // Für jede mögliche Klasse, alle möglichen ACLs, je nach der Variable "add" setzen oder löschen.
//        for (int i = 0; i < alleKlassen.size(); i++) {
//            if (!dblookup.containsKey(alleKlassen.get(i).getInternalClassID())) {
//                // Bisher war der Gruppe diese Klasse nicht zugeordnet.
//                // Müssen wir eine erstellen.
//                dblookup.put(alleKlassen.get(i).getInternalClassID(), new IntClasses(alleKlassen.get(i).getInternalClassID(), group));
//            }
//            setAcl(dblookup.get(alleKlassen.get(i).getInternalClassID()), add, toBeDeleted);
//        }
//    }
//
//    public static void setAcl(IntClasses dbclass, boolean add, List<Acl> toBeDeleted) {
//        // Welche ACLs kennt die Klasse denn ?
//        // Applikation fragen
//        // Als "virtueller" PK fungiert hier die interne Klassenbezeichnung.
//        List<InternalClassACL> moeglicheACLs = OPDE.internalClasses.getInternalClasses().get(dbclass.getJavaclass()).getAcls();
//
//        for (int i = 0; i < moeglicheACLs.size(); i++) {
//            setAcl(dbclass, moeglicheACLs.get(i).getAcl(), add, toBeDeleted);
//        }
//    }
//
//    public static void setAcl(IntClasses dbclass, short acl, boolean add, List<Acl> toBeDeleted) {
//
//        // Suche die betreffende ACL
//        boolean found = false;
//        Iterator<Acl> aclList = dbclass.getAclCollection().iterator();
//        if (add) { // ACL soll hinzugefügt werden
//            while (!found && aclList.hasNext()) {
//                Acl thisAcl = aclList.next();
//                if (thisAcl.getAcl() == acl) { // Eintrag gefunden
//                    // falls es diesen Eintrag gibt.
//                    toBeDeleted.remove(thisAcl);
//                    found = true;
//                }
//            }
//            if (!found) { // Am Ende und war nicht dabei ?
//                // Dann hinzufügen.
//                dbclass.getAclCollection().add(new Acl(acl, dbclass));
//                OPDE.debug("++ Gruppe " + dbclass.getGroups().getID() + " erhält das Recht " + InternalClassACL.strACLS[acl] + " für die Klasse " + dbclass.getJavaclass());
//            }
//        } else { // ACL soll entfernt werden
//            while (!found && aclList.hasNext()) {
//                Acl thisAcl = aclList.next();
//                if (thisAcl.getAcl() == acl) { // Eintrag gefunden
//                    toBeDeleted.add(thisAcl);
//                    OPDE.debug("-- Gruppe " + dbclass.getGroups().getID() + " verliert das Recht " + InternalClassACL.strACLS[acl] + " für die Klasse " + dbclass.getJavaclass());
//                    found = true;
//                }
//            }
//        }
//    }
}
