/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package op.care.planung.massnahmen;

import op.tools.CheckTreeSelectionModel;
import op.tools.ListElement;
import op.tools.SYSTools;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @author tloehr
 */
public class Tools {

    public static double calculateTree(TreeModel tm, CheckTreeSelectionModel sm) {
        //DefaultTreeModel tm = (DefaultTreeModel) treeMass.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tm.getRoot();
        double sum = 0d;
        if (root != null) {
            Enumeration e = root.postorderEnumeration();

            while (e.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
                Enumeration children = node.children();
                double childrenSum = 0d;
                //double childrenPercent = 1d;

                // Aufaddieren der Summen bzw. Prozente der evtl. vorhandenen Kinder. Aber nur wenn sie angewählt sind.
                while (children.hasMoreElements()) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
                    ListElement le = (ListElement) child.getUserObject();
                    if (sm.isPartiallySelected(new TreePath(child.getPath())) ||
                            sm.isPathSelected(new TreePath(child.getPath()), true)) {
                        childrenSum += (Double) ((Object[]) le.getObject())[ParserMassnahmen.O_SUMME];
                        // Hier muss ich noch mal ran.
                        //OPDE.debug("Children: " + le.getValue() + childrenSum + " %" + childrenPercent);
                    } else {
                        ((Object[]) le.getObject())[ParserMassnahmen.O_SUMME] = 0d;
                    }
                }

                double[] mf = calculateModfaktor(node);
                //double prozent = 1 + ((Double) ((Object[]) le.getObject())[ParserMassnahmen.O_PROZENT] / 100d);
                double mfzeit = mf[0];
                // Die Prozente müssen erst in einen Dezimalbruch umgerechnet werden.
                double mfprozent = 1 + (mf[1] / 100);
                //childrenPercent *= prozent;


                // Aufaddieren der Summen aller Kinder, des Knotens selber, sowie der Zeiten aus den Modfaktoren.
                // Das ganze MAL den Prozenten aus den Modfaktoren.
                ListElement le = (ListElement) node.getUserObject();
                sum = mfprozent * (childrenSum + (Double) ((Object[]) le.getObject())[ParserMassnahmen.O_ZEIT] + mfzeit);
                sum = SYSTools.roundScale2(sum);
                //OPDE.debug("Node: " + le.getValue() + sum);
                // Die Summe neu setzen, dieser Knoten ist wahrscheinlich auch Kind von jemandem ;-)
                ((Object[]) le.getObject())[ParserMassnahmen.O_SUMME] = sum;

            }
            //reloadTree();
        }
        return sum;
    }

    /**
     * Wählt eine ModFaktor eines Teilschrittes an oder ab.
     *
     * @param num  - Nummer der Modfaktors in der Liste
     * @param path - Pfad des Knotens im Baum um den es geht
     */
    public static void selectModfaktor(int num, TreePath path) {
        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) path.getLastPathComponent();
        ListElement le = (ListElement) dmtn.getUserObject();
        Object[] o = (Object[]) le.getObject();
        //Object[] o = new Object[]{beschreibung, zeit, new Vector(), new Vector(), typ, 0d};
        //Object[] modfaktor = new Object[]{label, beschreibung, zeit, prozent, new Boolean(selected)};
        Vector mdfs = (Vector) o[ParserMassnahmen.O_MODFAKTOR];
        Object[] md = (Object[]) mdfs.get(num);
        md[4] = !(Boolean) md[4];
    }

    public static double[] calculateModfaktor(DefaultMutableTreeNode node) {
        double result[] = new double[]{0d, 0d}; // zeit, prozent
        ListElement le = (ListElement) node.getUserObject();
        Object[] o = (Object[]) le.getObject();
        Vector mdfs = (Vector) o[ParserMassnahmen.O_MODFAKTOR];
        Enumeration e = mdfs.elements();

        while (e.hasMoreElements()) {
            Object[] md = (Object[]) e.nextElement();
            if ((Boolean) md[ParserMassnahmen.O_MF_SELECTED]) {
                result[0] += (Double) md[ParserMassnahmen.O_MF_ZEIT];
                result[1] += (Double) md[ParserMassnahmen.O_MF_PROZENT];
            }
        }
        return result;
    }

    public static String toXML(TreeModel tm, CheckTreeSelectionModel sm) {
        String result = "";
        if (tm != null) {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) tm.getRoot();
            if (root != null) {
                result = toXML(root, sm);
            }
        }
        return result;
    }

    /**
     * erstellt eine XML Datei, die den Baum repräsentiert. Inclusive aller Selections.
     */
    public static String toXML(DefaultMutableTreeNode root, CheckTreeSelectionModel sm) {
        String result = "";
        if (!root.isLeaf()) { // hat e überhaupt Kinder ?
            Enumeration e = root.children();
            String enclosedXML = "";
            while (e.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
                enclosedXML += toXML(node, sm);
            }
            result = toXML(root, enclosedXML, sm);
        } else { // Rekursionsanker
            result = toXML(root, "", sm);
        }
        return result;
    }

    public static String toXML(DefaultMutableTreeNode node, String enclosedXML, CheckTreeSelectionModel sm) {
        String result = "";
        //boolean emptyTag = enclosedXML.equals("");
        String tagname = "";
        ListElement le = (ListElement) node.getUserObject();
        Object[] o = (Object[]) le.getObject();
        int typ = (Integer) o[ParserMassnahmen.O_TYP];
        String label = le.getValue();
        String beschreibung = o[ParserMassnahmen.O_BESCHREIBUNG].toString();
        Vector modfaktoren = (Vector) o[ParserMassnahmen.O_MODFAKTOR];
        //String prozent = o[ParserMassnahmen.O_PROZENT].toString();
        String prozent = "1";
        String zeit = o[ParserMassnahmen.O_ZEIT].toString();
        String selected = "";
        if (sm.isPathSelected(new TreePath(node.getPath()), true)) {
            selected = " selected=\"true\"";
        }
        switch (typ) {
            case ParserMassnahmen.TYPE_ROOT: {
                result += "<massnahme label=\"" + label + "\" beschreibung=\"" + beschreibung + "\"";
                tagname = "massnahme";
                break;
            }
            case ParserMassnahmen.TYPE_Vorbereitung: {
                result += "<vorbereitung zeit=\"" + zeit + "\" beschreibung=\"" + beschreibung + "\"";
                tagname = "vorbereitung";
                break;
            }
            case ParserMassnahmen.TYPE_Nachbereitung: {
                result += "<nachbereitung zeit=\"" + zeit + "\" beschreibung=\"" + beschreibung + "\"";
                tagname = "nachbereitung";
                break;
            }
            case ParserMassnahmen.TYPE_DF: {
                result += "<df label=\"" + label + "\" beschreibung=\"" + beschreibung + "\" zeit=\"" + zeit + "\"";
                tagname = "df";
                break;
            }
            case ParserMassnahmen.TYPE_Teilschritt: {
                result += "<teil label=\"" + label + "\" beschreibung=\"" + beschreibung + "\" zeit=\"" + zeit + "\"";
                tagname = "teil";
                break;
            }
//            case ParserMassnahmen.TYPE_MODFAKTOR: {
//                result += "<modfaktor label=\"" + label + "\" beschreibung=\"" + beschreibung + "\" zeit=\"" + zeit + "\" prozent=\"" + prozent + "\"";
//                tagname = "modfaktor";
//                break;
//            }
            default: {
            }
        }
        if (modfaktoren != null && modfaktoren.size() > 0) {
            for (int m = 0; m < modfaktoren.size(); m++) {
                Object[] mf = (Object[]) modfaktoren.get(m);
                String mfprozent = mf[ParserMassnahmen.O_MF_PROZENT].toString();
                String mfzeit = mf[ParserMassnahmen.O_MF_ZEIT].toString();
                String mflabel = mf[ParserMassnahmen.O_MF_LABEL].toString();
                String mfbeschr = mf[ParserMassnahmen.O_MF_BESCHREIBUNG].toString();
                boolean mfselected = ((Boolean) mf[ParserMassnahmen.O_MF_SELECTED]).booleanValue();
                enclosedXML += "<modfaktor label=\"" + mflabel + "\" zeit=\"" + mfzeit + "\" prozent=\"" + mfprozent + "\" beschreibung=\"" + mfbeschr + "\"";
                if (mfselected) {
                    enclosedXML += " selected=\"true\"";
                }
                enclosedXML += "/>";
            }
        }
        // gilt für alle... SELECTED hinzufügen.
        result += selected;
        // Abschluss...
        if (enclosedXML.equals("")) { // Soll nichts eingeschlossen werden. Dann empty Tag.
            result += "/>\n";
        } else {
            result += ">\n" + enclosedXML + "</" + tagname + ">\n";
        }
        return result;
    }
}
