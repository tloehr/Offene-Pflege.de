/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package op.care.planung.massnahmen;

import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import op.tools.ListElement;
import op.tools.SYSTools;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ParserMassnahmen extends DefaultHandler {

    public static final int TYPE_ERROR = 0;
    public static final int TYPE_ROOT = 1; // folder_green
    public static final int TYPE_MODFAKTOR = 2; // +-
    public static final int TYPE_VBMaterial = 3; // package
    public static final int TYPE_Teilschritt = 4; // gebogener Pfeil ?
    public static final int TYPE_DF = 5;
    public static final int TYPE_Vorbereitung = 6;
    public static final int TYPE_Nachbereitung = 7;
    public static final int O_BESCHREIBUNG = 0;
    public static final int O_ZEIT = 1;
    public static final int O_MODFAKTOR = 2;
    public static final int O_VBMATERIAL = 3;
    public static final int O_TYP = 4;
    public static final int O_SUMME = 5;
    public static final int O_MF_LABEL = 0;
    public static final int O_MF_BESCHREIBUNG = 1;
    public static final int O_MF_ZEIT = 2;
    public static final int O_MF_PROZENT = 3;
    public static final int O_MF_SELECTED = 4;
    private DefaultMutableTreeNode tree;
    private TreeNode parent;
    private Vector selection;

    public void startDocument() throws SAXException {
        selection = new Vector();
    }

    public static String getNodeTypeName(int typ) {
        String title = "";
        switch (typ) {
            case ParserMassnahmen.TYPE_MODFAKTOR: {
                title = "Erschwernis/Erleichterung";
                break;
            }
            case ParserMassnahmen.TYPE_DF: {
                title = "Durchführung";
                break;
            }
            case ParserMassnahmen.TYPE_Teilschritt: {
                title = "Teilschritt";
                break;
            }
            case ParserMassnahmen.TYPE_Vorbereitung: {
                title = "Vorbereitung";
                break;
            }
            case ParserMassnahmen.TYPE_Nachbereitung: {
                title = "Nachbereitung";
                break;
            }
        }
        return title;
    }

    public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
        int typ = identifyTag(tagName);

        //String name = attributes.getValue("name");
        String beschreibung = SYSTools.catchNull(attributes.getValue("beschreibung"));
        String label = SYSTools.catchNull(attributes.getValue("label"));
        if (typ == TYPE_Nachbereitung) {
            label = "Nachbereitung";
        }
        if (typ == TYPE_Vorbereitung) {
            label = "Vorbereitung";
        }
        String sSelected = attributes.getValue("selected");
        boolean selected = false;
        if (sSelected != null && sSelected.equalsIgnoreCase("true")) {
            selected = true;
        } else {
            selected = false;
        }

        String sZeit = SYSTools.catchNull(attributes.getValue("zeit"));
        Double zeit = 0d;
        try {
            zeit = Double.parseDouble(sZeit);
        } catch (NumberFormatException numberFormatException) {
            zeit = 0d;
        }

        if (typ == TYPE_MODFAKTOR) { // Erschwernisfaktoren
            ListElement le = (ListElement) ((DefaultMutableTreeNode) parent).getUserObject();
            Object[] o = (Object[]) le.getObject();
            Vector faktoren = (Vector) o[TYPE_MODFAKTOR];

            String sProzent = SYSTools.catchNull(attributes.getValue("prozent"));
            Double prozent = 0d;
            try {
                prozent = Double.parseDouble(sProzent);
            } catch (NumberFormatException numberFormatException) {
                prozent = 0d;
            }
            Object[] modfaktor = new Object[]{label, beschreibung, zeit, prozent, new Boolean(selected)};
            faktoren.add(modfaktor);
        }

        if (typ == TYPE_VBMaterial) { // Verbrauchsmaterial
            ListElement le = (ListElement) ((DefaultMutableTreeNode) parent).getUserObject();
            Object[] o = (Object[]) le.getObject();
            Vector vbs = (Vector) o[TYPE_VBMaterial];

            String sAnzahl = SYSTools.catchNull(attributes.getValue("anzahl"));
            Double anzahl = 0d;
            try {
                anzahl = Double.parseDouble(sAnzahl);
            } catch (NumberFormatException numberFormatException) {
                anzahl = 0d;
            }

            long id = 0;
            try {
                id = Long.parseLong(sAnzahl);
            } catch (NumberFormatException numberFormatException) {
                id = 0;
            }

            Object[] vbmaterial = new Object[]{id, anzahl, new Boolean(selected)};
            vbs.add(vbmaterial);
        }
        /*
         * label des Tags
         * beschreibung: nähere Beschreibung
         * zeit in Minuten, die dieses Tag beisteuert, wenn es angewählt ist.
         * Vector für die evtl. vorhandenen Modfaktoren
         * Vector für die evtl. vorhandenen VBMaterialien
         * typ ist ein int code, der den Typ des Knotens angibt. Kann man den Konstanten dieser Klasse entnehmen.
         * die letzte 0 wird später bei der Berechnung des Baums verwendet. Dort steht dann nämlich der aktuelle
         * Zeitwert drin, den dieser Knoten und seine angewählten Kindknoten hat.
         */

        // Dieses Objekt wird jedem Knoten mitgegeben. Und zwar in einem ListElement, damit der
        // Tree die toString() funktion verwenden kann.

        // Ein normaler Knoten für den Baum wird erzeugt.
        if (typ != TYPE_MODFAKTOR && typ != TYPE_VBMaterial) {
            Object[] o = new Object[]{beschreibung, zeit, new Vector(), new Vector(), typ, 0d};
            ListElement le = new ListElement(label, o);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(le);
            if (parent != null) {
                ((DefaultMutableTreeNode) parent).add(node);
            } else { // wenn parent null ist, dann ist der Baum noch leer. Dann ist der erste Knoten ab jetzt der tree.
                tree = node;
            }

            // für die preselection Pathes
            if (selected) {
                selection.add(new TreePath(node.getPath()));
            }

            // Solange dieser Tag nicht geschlossen wird, werden alle neuen unterhalb dieser Ebene einsortiert.
            parent = node;
        }
    //OpenCare.logger.debug("STARTELEMENT: " + this.toString() + ": " + tagName + "    name: " + name);


    }

    public DefaultMutableTreeNode getTree() {
        return tree;
    }

    public TreePath[] getSelectionPaths() {
        return (TreePath[]) selection.toArray(new TreePath[]{});
    }

    private int identifyTag(String tagName) {
        int result = 0;

        if (tagName.equalsIgnoreCase("massnahme")) {
            result = TYPE_ROOT;
        } else if (tagName.equalsIgnoreCase("df")) {
            result = TYPE_DF;
        } else if (tagName.equalsIgnoreCase("modfaktor")) {
            result = TYPE_MODFAKTOR;
        } else if (tagName.equalsIgnoreCase("vbmaterial")) {
            result = TYPE_VBMaterial;
        } else if (tagName.equalsIgnoreCase("vorbereitung")) {
            result = TYPE_Vorbereitung;
        } else if (tagName.equalsIgnoreCase("teil")) {
            result = TYPE_Teilschritt;
        } else if (tagName.equalsIgnoreCase("nachbereitung")) {
            result = TYPE_Nachbereitung;
        } else {
            result = TYPE_ERROR;
        }
        return result;

    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        // sobald ein Element geschlossen wird, werden neue Elemente an das darüber liegende angeschlossen.
        int typ = identifyTag(localName);
        if (typ != TYPE_VBMaterial && typ != TYPE_MODFAKTOR) {
            if (parent.getParent() != null) {
                parent = parent.getParent();
            }
        }
    }
} // private class HandlerFragenStruktur