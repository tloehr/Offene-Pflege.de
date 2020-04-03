/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.backend.entity.system;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author tloehr
 */
public class SYSGROUPS2ACLTools {

    public static final int ROOT = 0;
    public static final int SELECTION = 1;

//    /**
//     * Diese Klasse erzeugt eine Baumstruktur. Sie enthält alle übergebenen Klassen.
//     * Anhand der InternalClasses (nicht zu verwechseln mit SYSGROUPS2ACL) bestimmt sie
//     * die erlaubten ACLs. Die übergebene Collection der EntityBeans (das sind jetzt
//     * SYSGROUPS2ACL) enthält, welche Rechte für die entsprechende Gruppe gesetzt
//     * wurden. So werden die vorselektierten Pfade gesetzt.
//     * Der Baum hat die Tiefe 3. Die Klassen der einzelnen Ebenen sind festgelegt: (String GKennung, InternalClass, InternalClassACL)
//     *
//     * This methode creates a tree structure which contains all classes described in /appinfo.xml.
//     * A common "primary key" between the internal XML structure and the actual JPA entity entity.system.SYSGROUPS2ACL is a string "name" tag in XML, classname in the entity and
//     * internalClassID in many actual java classes used in OPDE.
//     *
//     *
//     *
//     * @param Gruppe, für die die Klassen in Baumform benötigt werden.
//     * @return eine Liste mit zwei Einträgen (MutableTreeNode root, TreePath[] selection).
//     */
//    public static ArrayList getIntClassesAsTree(Groups group) {
//        HashMap<String, SYSGROUPS2ACL> dbClasses = getDBLookupTable(group);
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
//            if (!currentClass.getPossibleACLs().isEmpty()) {
//
//                String classname = currentClass.getInternalClassID();
//
//                currentClass.setIntClass(dbClasses.get(classname));
//
//                DefaultMutableTreeNode classnode = new DefaultMutableTreeNode(OPDE.getAppInfo().getInternalClasses().get(classname));
//                root.add(classnode);
//
//                // Alle ACLs, die diese Klasse kennt.
//                List<InternalClassACL> keyset = OPDE.getAppInfo().getInternalClasses().get(classname).getPossibleACLs();
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
//                    if (group.getGID().equalsIgnoreCase("admin")) {
//                        selection.add(new TreePath(new Object[]{root, classnode, node}));
//                    } else {
//                        // Ist bei der übergebenen Gruppe dieses Recht auch dabei ?
//                        // Falls ja, Selection Pfad hinzufügen.
//                        if (dbClasses.containsKey(classname)) {
//                            Acl[] acls = dbClasses.get(classname).getAclCollection().toArray(new Acl[]{});
//                            for (int i = 0; i < acls.length; i++) {
//                                if (currentacl.getACLcode() == acls[i].getACLcode()) {
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
//            OPDE.debug("saveTree: speichere Gruppe: " + ((Groups) node.getUserObject()).getGID());
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
//                SYSGROUPS2ACL intClasses = new SYSGROUPS2ACL(myInternalClass.getInternalClassID(), group);
//                em.persist(intClasses);
//                myInternalClass.setIntClass(intClasses);
//                OPDE.debug("       saveTree: speichere EntityBean IntClass: " + myInternalClass.getInternalClassID());
//            } else {
//                OPDE.debug("       saveTree: hat schon EntityBean: " + myInternalClass.getIntClass().getJavaclass());
//                OPDE.debug("       saveTree: und zwar für die Gruppe: " + myInternalClass.getIntClass().getGroups().getGID());
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
//                OPDE.debug("              saveTree: SPEICHERE ACL " + InternalClassACL.strACLS[myACL.getACLcode()]);
//                OPDE.debug("              saveTree: " + InternalClassACL.strACLS[myACL.getACLcode()] + " hat " + (myACL.hasAclEntity() ? "eine" : "keine") + " Entity Bean");
//                if (!myACL.hasAclEntity()) { // Nur hinzufügen, wenn nötig.
//                    OPDE.debug("              saveTree: erstelle neue EntityBean");
//                    Acl acl = new Acl(myACL.getACLcode(), myClass.getIntClass());
//                    em.persist(acl);
//                } else {
//                    OPDE.debug("              saveTree: für die Gruppe: " + myACL.getAclEntity().getIntclass().getGroups().getGID());
//                }
//            } else { // ACL entziehen
//                OPDE.debug("              saveTree: LÖSCHE ACL " + InternalClassACL.strACLS[myACL.getACLcode()]);
//                OPDE.debug("              saveTree: " + InternalClassACL.strACLS[myACL.getACLcode()] + " hat " + (myACL.hasAclEntity() ? "eine" : "keine") + " Entity Bean");
//                if (myACL.hasAclEntity()) { // Nur löschen, wenn nötig.
//                    OPDE.debug("              saveTree: lösche jetzt die EntityBean");
//                    em.remove(myACL.getAclEntity());
//                    OPDE.debug("              saveTree: für die Gruppe: " + myACL.getAclEntity().getIntclass().getGroups().getGID());
//                }
//            }
//            myACL.setAclEntity(null);
//        }
//        em.close();
//    }

    /**
     * creates a hashmap which contains all assigned SYSGROUPS2ACL for a group. the key
     * index is the internalClassesID String.
     * @param group
     * @return
     */
    public static HashMap<String, SYSGROUPS2ACL> getIntClassesMap(OPGroups group) {
        HashMap<String, SYSGROUPS2ACL> dblookup = new HashMap();
        if (group.getIntClasses() != null) {
            Iterator<SYSGROUPS2ACL> it = group.getIntClasses().iterator();
            while (it.hasNext()) {
                SYSGROUPS2ACL thisClass = it.next();
                dblookup.put(thisClass.getInternalClassID(), thisClass);
            }
        }
        return dblookup;
    }

    /**
     * searches for a Acl which is assigned to SYSGROUPS2ACL according to the aclCode.
     *
     * @param SYSGROUPS2ACL
     * @param aclCode
     * @return the found Acl, null if not found or incClasses == null
     */
    public static Acl findACLbyCODE(SYSGROUPS2ACL SYSGROUPS2ACL, short aclCode){
        Acl result = null;
        if (SYSGROUPS2ACL == null){
            return null;
        }
        for (Acl acl : SYSGROUPS2ACL.getAclCollection()){
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
//        Iterator<InternalClassACL> itacls = thisclass.getPossibleACLs().iterator();
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
//        HashMap<String, SYSGROUPS2ACL> dblookup = getDBLookupTable(group);
//
//        // Für jede mögliche Klasse, alle möglichen ACLs, je nach der Variable "add" setzen oder löschen.
//        for (int i = 0; i < alleKlassen.size(); i++) {
//            if (!dblookup.containsKey(alleKlassen.get(i).getInternalClassID())) {
//                // Bisher war der Gruppe diese Klasse nicht zugeordnet.
//                // Müssen wir eine erstellen.
//                dblookup.put(alleKlassen.get(i).getInternalClassID(), new SYSGROUPS2ACL(alleKlassen.get(i).getInternalClassID(), group));
//            }
//            setAcl(dblookup.get(alleKlassen.get(i).getInternalClassID()), add, toBeDeleted);
//        }
//    }
//
//    public static void setAcl(SYSGROUPS2ACL dbclass, boolean add, List<Acl> toBeDeleted) {
//        // Welche ACLs kennt die Klasse denn ?
//        // Applikation fragen
//        // Als "virtueller" PK fungiert hier die interne Klassenbezeichnung.
//        List<InternalClassACL> moeglicheACLs = OPDE.internalClasses.getInternalClasses().get(dbclass.getJavaclass()).getPossibleACLs();
//
//        for (int i = 0; i < moeglicheACLs.size(); i++) {
//            setAcl(dbclass, moeglicheACLs.get(i).getACLcode(), add, toBeDeleted);
//        }
//    }
//
//    public static void setAcl(SYSGROUPS2ACL dbclass, short acl, boolean add, List<Acl> toBeDeleted) {
//
//        // Suche die betreffende ACL
//        boolean found = false;
//        Iterator<Acl> aclList = dbclass.getAclCollection().iterator();
//        if (add) { // ACL soll hinzugefügt werden
//            while (!found && aclList.hasNext()) {
//                Acl thisAcl = aclList.next();
//                if (thisAcl.getACLcode() == acl) { // Eintrag gefunden
//                    // falls es diesen Eintrag gibt.
//                    toBeDeleted.remove(thisAcl);
//                    found = true;
//                }
//            }
//            if (!found) { // Am Ende und war nicht dabei ?
//                // Dann hinzufügen.
//                dbclass.getAclCollection().add(new Acl(acl, dbclass));
//                OPDE.debug("++ Gruppe " + dbclass.getGroups().getGID() + " erhält das Recht " + InternalClassACL.strACLS[acl] + " für die Klasse " + dbclass.getJavaclass());
//            }
//        } else { // ACL soll entfernt werden
//            while (!found && aclList.hasNext()) {
//                Acl thisAcl = aclList.next();
//                if (thisAcl.getACLcode() == acl) { // Eintrag gefunden
//                    toBeDeleted.add(thisAcl);
//                    OPDE.debug("-- Gruppe " + dbclass.getGroups().getGID() + " verliert das Recht " + InternalClassACL.strACLS[acl] + " für die Klasse " + dbclass.getJavaclass());
//                    found = true;
//                }
//            }
//        }
//    }
}
