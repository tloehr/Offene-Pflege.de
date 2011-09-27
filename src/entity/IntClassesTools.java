/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import op.OPDE;
import op.tools.CheckTreeSelectionModel;
import op.tools.InternalClass;
import op.tools.InternalClassACL;

/**
 *
 * @author tloehr
 */
public class IntClassesTools {

    public static final int ROOT = 0;
    public static final int SELECTION = 1;

    /**
     * Diese Klasse erzeugt eine Baumstruktur. Sie enthält alle übergebenen Klassen.
     * Anhand der InternalClasses (nicht zu verwechseln mit IntClasses) bestimmt sie
     * die erlaubten ACLs. Die übergebene Collection der EntityBeans (das sind jetzt
     * IntClasses) enthält, welche Rechte für die entsprechende Gruppe gesetzt
     * wurden. So werden die vorselektierten Pfade gesetzt.
     * Der Baum hat die Tiefe 3. Die Klassen der einzelnen Ebenen sind festgelegt: (String GKennung, InternalClass, InternalClassACL)
     * @param Gruppe, für die die Klassen in Baumform benötigt werden.
     * @return eine Liste mit zwei Einträgen (MutableTreeNode root, TreePath[] selection).
     */
    public static ArrayList getIntClassesAsTree(Groups group) {
        HashMap<String, IntClasses> dbClasses = getDBLookupTable(group);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(group);

        // Alle bekannten Classen und deren möglichen Rechte (anhand internalclasses.xml) ermitteln.
        ArrayList<InternalClass> classset = new ArrayList(OPDE.getInternalClasses().getInternalClasses().values());
        Collections.sort(classset);
        Iterator<InternalClass> itClasses = classset.iterator();

        ArrayList<TreePath> selection = new ArrayList();
        while (itClasses.hasNext()) { // Klasse als Knoten einfügen
            InternalClass currentClass = itClasses.next();

            // Nur solche Klassen hinzufügen,
            // die auch ACLs besitzen. Es gibt auch
            // Klassendefinitionen, die nur für die Kollisionsdefinition
            // da sind. Die brauchen manchmaln gar keine ACLs. (z.B. BHPImport)
            if (!currentClass.getAcls().isEmpty()) {

                String classname = currentClass.getInternalClassname();

                currentClass.setIntClass(dbClasses.get(classname));

                DefaultMutableTreeNode classnode = new DefaultMutableTreeNode(OPDE.getInternalClasses().getInternalClasses().get(classname));
                root.add(classnode);

                // Alle ACLs, die diese Klasse kennt.
                List<InternalClassACL> keyset = OPDE.getInternalClasses().getInternalClasses().get(classname).getAcls();
                Collections.sort(keyset);

                Iterator<InternalClassACL> itACLs = keyset.iterator();
                while (itACLs.hasNext()) {
                    // Diese acl kennt die Klasse

                    InternalClassACL currentacl = itACLs.next();
                    // Machen wir einen Knoten draus.
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(currentacl);
                    classnode.add(node);

                    // Admin hat IMMER alle Rechte.
                    if (group.getGkennung().equalsIgnoreCase("admin")) {
                        selection.add(new TreePath(new Object[]{root, classnode, node}));
                    } else {
                        // Ist bei der übergebenen Gruppe dieses Recht auch dabei ?
                        // Falls ja, Selection Pfad hinzufügen.
                        if (dbClasses.containsKey(classname)) {
                            Acl[] acls = dbClasses.get(classname).getAclCollection().toArray(new Acl[]{});
                            for (int i = 0; i < acls.length; i++) {
                                if (currentacl.getAcl() == acls[i].getAcl()) {
                                    OPDE.getLogger().debug("selected ACL found");
                                    currentacl.setAclEntity(acls[i]);
                                    selection.add(new TreePath(new Object[]{root, classnode, node}));
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        }

        dbClasses.clear();

        ArrayList result = new ArrayList(2);
        result.add(0, root);
        result.add(1, selection.toArray(new TreePath[]{}));

        return result;
    }

    public static void saveTree(DefaultMutableTreeNode node, CheckTreeSelectionModel selmodel) {
        if (node.getUserObject() instanceof Groups) { // Root
            boolean rootWasSelected = selmodel.isPathSelected(new TreePath(node.getPath()));
            OPDE.getLogger().debug("saveTree: speichere Gruppe: " + ((Groups) node.getUserObject()).getGkennung());
            OPDE.getLogger().debug("saveTree: Gruppe war " + (rootWasSelected ? "" : "NICHT") + " ausgewählt.");
            Enumeration<DefaultMutableTreeNode> enumInternalClasses = node.children();
            while (enumInternalClasses.hasMoreElements()) {
                saveTree(enumInternalClasses.nextElement(), selmodel, rootWasSelected);
            }
        }
    }

    public static void saveTree(DefaultMutableTreeNode node, CheckTreeSelectionModel selmodel, boolean parentWasSelected) {
        if (node.getUserObject() instanceof InternalClass) {

            InternalClass myInternalClass = (InternalClass) node.getUserObject();
            if (!myInternalClass.hasIntClass()) { // Bisher keine EntityBean ?
                Groups group = (Groups) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
                IntClasses intClasses = new IntClasses(myInternalClass.getInternalClassname(), group);
                OPDE.getEM().persist(intClasses);
                myInternalClass.setIntClass(intClasses);
                OPDE.getLogger().debug("       saveTree: speichere EntityBean IntClass: " + myInternalClass.getInternalClassname());
            } else {
                OPDE.getLogger().debug("       saveTree: hat schon EntityBean: " + myInternalClass.getIntClass().getClassname());
                OPDE.getLogger().debug("       saveTree: und zwar für die Gruppe: " + myInternalClass.getIntClass().getGroups().getGkennung());

            }
            OPDE.getLogger().debug("       saveTree: sie war " + (selmodel.isPathSelected(new TreePath(node.getPath())) ? "" : "NICHT") + " ausgewählt.");

            Enumeration<DefaultMutableTreeNode> enumInternalClasses = node.children();
            while (enumInternalClasses.hasMoreElements()) {
                DefaultMutableTreeNode child = enumInternalClasses.nextElement();
                saveTree(child, selmodel, parentWasSelected || selmodel.isPathSelected(new TreePath(node.getPath())));
            }

            myInternalClass.setIntClass(null);

        } else if (node.getUserObject() instanceof InternalClassACL) { // Leaf
            InternalClassACL myACL = (InternalClassACL) node.getUserObject();
            InternalClass myClass = (InternalClass) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
            boolean shouldBeSelected = parentWasSelected || selmodel.isPathSelected(new TreePath(node.getPath()));


            if (shouldBeSelected) { // ACL gewähren
                OPDE.getLogger().debug("              saveTree: SPEICHERE ACL " + InternalClassACL.strACLS[myACL.getAcl()]);
                OPDE.getLogger().debug("              saveTree: " + InternalClassACL.strACLS[myACL.getAcl()] + " hat " + (myACL.hasAclEntity() ? "eine" : "keine") + " Entity Bean");
                if (!myACL.hasAclEntity()) { // Nur hinzufügen, wenn nötig.
                    OPDE.getLogger().debug("              saveTree: erstelle neue EntityBean");
                    Acl acl = new Acl(myACL.getAcl(), myClass.getIntClass());
                    OPDE.getEM().persist(acl);
                } else {
                    OPDE.getLogger().debug("              saveTree: für die Gruppe: " + myACL.getAclEntity().getIntclass().getGroups().getGkennung());
                }
            } else { // ACL entziehen
                OPDE.getLogger().debug("              saveTree: LÖSCHE ACL " + InternalClassACL.strACLS[myACL.getAcl()]);
                OPDE.getLogger().debug("              saveTree: " + InternalClassACL.strACLS[myACL.getAcl()] + " hat " + (myACL.hasAclEntity() ? "eine" : "keine") + " Entity Bean");
                if (myACL.hasAclEntity()) { // Nur löschen, wenn nötig.
                    OPDE.getLogger().debug("              saveTree: lösche jetzt die EntityBean");
                    OPDE.getEM().remove(myACL.getAclEntity());
                    OPDE.getLogger().debug("              saveTree: für die Gruppe: " + myACL.getAclEntity().getIntclass().getGroups().getGkennung());
                }
            }
            myACL.setAclEntity(null);
        }
    }

    public static HashMap<String, IntClasses> getDBLookupTable(Groups group) {
        HashMap<String, IntClasses> dblookup = new HashMap();
        if (group.getIcCollection() != null) {
            Iterator<IntClasses> it = group.getIcCollection().iterator();
            while (it.hasNext()) {
                IntClasses thisClass = it.next();
                dblookup.put(thisClass.getClassname(), thisClass);
            }
        }
        return dblookup;
    }

    public static void clearEntitiesFromAllInternalClasses() {
        ArrayList<InternalClass> classset = new ArrayList(OPDE.getInternalClasses().getInternalClasses().values());
        Iterator<InternalClass> itClasses = classset.iterator();

        while (itClasses.hasNext()) {
            clearEntitiesFromAllInternalClasses(itClasses.next());
        }
    }

    public static void clearEntitiesFromAllInternalClasses(InternalClass thisclass) {
        thisclass.setIntClass(null);
        Iterator<InternalClassACL> itacls = thisclass.getAcls().iterator();

        while (itacls.hasNext()) {
            itacls.next().setAclEntity(null);
        }

    }
//    public static void setAcl(Groups group, boolean add, List<Acl> toBeDeleted) {
//        // Welche ACLs kennt die Klasse denn ?
//        // Applikation fragen
//        // Als "virtueller" PK fungiert hier die interne Klassenbezeichnung.
//        List<InternalClass> alleKlassen = new ArrayList(OPDE.internalClasses.getInternalClasses().values());
//        HashMap<String, IntClasses> dblookup = getDBLookupTable(group);
//
//        // Für jede mögliche Klasse, alle möglichen ACLs, je nach der Variable "add" setzen oder löschen.
//        for (int i = 0; i < alleKlassen.size(); i++) {
//            if (!dblookup.containsKey(alleKlassen.get(i).getInternalClassname())) {
//                // Bisher war der Gruppe diese Klasse nicht zugeordnet.
//                // Müssen wir eine erstellen.
//                dblookup.put(alleKlassen.get(i).getInternalClassname(), new IntClasses(alleKlassen.get(i).getInternalClassname(), group));
//            }
//            setAcl(dblookup.get(alleKlassen.get(i).getInternalClassname()), add, toBeDeleted);
//        }
//    }
//
//    public static void setAcl(IntClasses dbclass, boolean add, List<Acl> toBeDeleted) {
//        // Welche ACLs kennt die Klasse denn ?
//        // Applikation fragen
//        // Als "virtueller" PK fungiert hier die interne Klassenbezeichnung.
//        List<InternalClassACL> moeglicheACLs = OPDE.internalClasses.getInternalClasses().get(dbclass.getClassname()).getAcls();
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
//                OPDE.getLogger().debug("++ Gruppe " + dbclass.getGroups().getGkennung() + " erhält das Recht " + InternalClassACL.strACLS[acl] + " für die Klasse " + dbclass.getClassname());
//            }
//        } else { // ACL soll entfernt werden
//            while (!found && aclList.hasNext()) {
//                Acl thisAcl = aclList.next();
//                if (thisAcl.getAcl() == acl) { // Eintrag gefunden
//                    toBeDeleted.add(thisAcl);
//                    OPDE.getLogger().debug("-- Gruppe " + dbclass.getGroups().getGkennung() + " verliert das Recht " + InternalClassACL.strACLS[acl] + " für die Klasse " + dbclass.getClassname());
//                    found = true;
//                }
//            }
//        }
//    }
}
