package de.offene_pflege.entity.info;

import de.offene_pflege.op.tools.HasLogger;
import de.offene_pflege.op.tools.InfoTreeNodeBean;
import de.offene_pflege.op.tools.RiskBean;
import de.offene_pflege.op.tools.SYSTools;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.tree.DefaultMutableTreeNode;
import java.math.BigDecimal;
import java.util.*;

// Dieser Struktur Handler ist nur für die Umwandlung eines ResInfos in eine HTML oder Text Datei
public class ResInfoContentParser extends DefaultHandler implements HasLogger {

    // Die Multikey enthält alle Strukturen außer dem Scalerisk (gibts nur bei der BRADEN)
    // Diese Map liest man so: NAME_DER_COMPONENT,
    // Durch die MultiKeys spare ich mir den Tree.
    // Es gibt zweistellige Multikeys und dreistellige.
    // Zweistellig für einfache Components ohne Parent (z.B. Textfields)
    // dreistellig für Components mit Parent (Tabgroup, Optiongroup usw.)

    // liest sich wie folgt: component, parent, label
    // parent kann leer sein

    private DefaultMutableTreeNode wurzel;
    //        private Optional<DefaultMutableTreeNode> parent;
    private Stack<DefaultMutableTreeNode> parent;
    private Properties content;


    private HashSet<String> parents = new HashSet<>(Arrays.asList("tabgroup", "combobox", "optiongroup", "scale", "scalegroup"));
    private HashSet<String> selectorcomponents = new HashSet<>(Arrays.asList("gpselect", "hospitalselect", "roomselect")); // die stehen immer 2x in den contents einmal mit .id und einmal mit .text

    public ResInfoContentParser(Properties content) {
        this.content = content;
    }

    public void startDocument() {
        wurzel = new DefaultMutableTreeNode(new InfoTreeNodeBean("root", "", "", Optional.empty()));
        parent = new Stack<>();
        parent.push(wurzel);
    }

    @Override
    public void endDocument() {

    }

    public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
        // Baut eine Liste mit names und labels auf.
        String name = attributes.getValue("name");
        String rating = attributes.getValue("rating"); // gibts beim riskmodel

        if (name == null) name = UUID.randomUUID().toString(); // irgendeinen namen der eindeutig ist
        tagName = tagName.toLowerCase();
        //getLogger().debug(tagName);

        String label = SYSTools.xx(attributes.getValue("label"));
        // Tags, die es nicht mehr gibt müssen hier auch nicht berücksichtigt werden, weil
        // wenn die einmal weg sind, kann keiner mehr etwas damit erzeugen und dieser Handler
        // wird nur bei der Erstellung einer neuen RESINFO aufgerufen. Nicht mehr spöter.

        // das RiskModel ist ein wenig anders. Das parse ich nur, damit ich das hinterher als Auswertung nehmen kann.
        Optional value;
        if (tagName.equals("risk")) {
            value = Optional.of(new RiskBean(attributes.getValue("from"), attributes.getValue("to"), attributes.getValue("label"), attributes.getValue("color"), attributes.getValue("rating")));
            name = rating; // risks haben keinen name, sondern ein rating. eigentlich eine dumme Idee von mir
        } else if (tagName.equals("scale")) {
            value = content.containsKey("scalesum") ? Optional.of(content.get("scalesum")) : Optional.empty();
        } else if (selectorcomponents.contains(tagName)) {
            value = content.containsKey(name + ".text") ? Optional.of(content.get(name + ".text")) : Optional.empty();
        } else {
            value = content.containsKey(name) ? (content.getProperty(name).isEmpty() ? Optional.empty() : Optional.of(content.getProperty(name))) : Optional.empty();
        }
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new InfoTreeNodeBean(tagName, name, label, value));

        if (parents.contains(tagName.toLowerCase())) { // dieser node ist ein parent
            parent.push(node);
        } else {
            parent.peek().add(node); // immer auf an den aktuellen Parent hängen. Ganz oben ist ROOT
        }

    }

    public DefaultMutableTreeNode getStruktur() {
        return wurzel;
    }

    public void endElement(String uri, String localName, String qName) {
        if (parents.contains(qName.toLowerCase())) {
            DefaultMutableTreeNode child = parent.pop();
            parent.peek().add(child);
        }
    }

    public String render(ResInfoRenderInterface resInfoRenderer) {
        String text = "";
        Enumeration en = wurzel.children();
        while (en.hasMoreElements())
            text += render((DefaultMutableTreeNode) en.nextElement(), resInfoRenderer);
        //getLogger().debug(text);
        return text;
    }

    public String render(DefaultMutableTreeNode node, ResInfoRenderInterface renderer) {
        //    private static String toHTML(DefaultMutableTreeNode struktur, Properties content, ArrayList<RiskBean> scaleriskmodel) {
        //        BigDecimal scalesum = null;
        String text = "";


        InfoTreeNodeBean infonode = (InfoTreeNodeBean) node.getUserObject();
        String tagname = infonode.getTagName().toLowerCase();

        if (tagname.equals("scale")) {

            String myString = "";
            // Einsammeln der ScaleGroups und Risks
            Enumeration en = node.children();


            ArrayList<RiskBean> scaleriskmodel = new ArrayList<>();
            while (en.hasMoreElements()) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) en.nextElement();
                InfoTreeNodeBean myinfonode = (InfoTreeNodeBean) child.getUserObject();

                if (myinfonode.getTagName().equals("scalegroup")) {
                    myString += renderer.renderListEntry(render(child, renderer));
                } else {
                    if (myinfonode.getTagName().equals("risk")) {
                        scaleriskmodel.add((RiskBean) myinfonode.getValue().get());
                    }
                }
            }

            if (infonode.getValue().isPresent()) { // falls nicht, es gibt alte BRADEN einträge, die keine scalesum haben. Die vernachlässigen wir hier
                String value = infonode.getValue().get().toString();
                BigDecimal scalesum = new BigDecimal(value);
                String color = "", risiko = "";
                for (RiskBean risk : scaleriskmodel) {
                    if (risk.getFrom().compareTo(scalesum) <= 0 && scalesum.compareTo(risk.getTo()) <= 0) {
                        color = risk.getColor();
                        risiko = risk.getLabel();
                        break;
                    }
                }

                text = renderer.renderGroupOfEntries(renderer.renderKeyValue("misc.msg.scalerisk.rating", scalesum + " (" + risiko + ")"), myString);
            } else {
                text = myString;
            }


        } else if (tagname.equals("bodyscheme")) {
            ArrayList<String> bodyparts = new ArrayList<>();
            for (String key : content.stringPropertyNames()) {
                // nur die Körperteile, die angewählt wurden.
                //https://github.com/tloehr/Offene-Pflege.de/issues/73
                if (key.startsWith(infonode.getName()) && content.getProperty(key).equalsIgnoreCase("true")) {
                    bodyparts.add(key.substring(4));
                }
            }
            text = renderer.renderBodyparts(bodyparts);
        } else if (tagname.equals("label")) {
            text = renderer.renderLabel(infonode.getLabel());
        } else if (tagname.equalsIgnoreCase("tabgroup")) {
            String myChildren = "";
            Enumeration en = node.children();
            while (en.hasMoreElements()) {
                InfoTreeNodeBean child = (InfoTreeNodeBean) ((DefaultMutableTreeNode) en.nextElement()).getUserObject();
                if (child.isCheckbox())
                    myChildren += renderer.renderListEntry(renderer.renderBoolean(child.getLabel(), child.getValue().get().toString(), false));
                else if (child.getValue().isPresent())
                    myChildren += renderer.renderListEntry(renderer.renderKeyValue(child.getLabel(), child.getValue().get().toString()));
            }
            text = renderer.renderGroupOfEntries(infonode.getLabel(), myChildren);
        } else if (tagname.equalsIgnoreCase("combobox") || tagname.equalsIgnoreCase("optiongroup") || tagname.equalsIgnoreCase("scalegroup")) {
            String myChildren = "";
            Enumeration en = node.children();
            while (en.hasMoreElements()) {
                InfoTreeNodeBean child = (InfoTreeNodeBean) ((DefaultMutableTreeNode) en.nextElement()).getUserObject();
                myChildren += renderer.renderOption(infonode, child);
            }
            text = myChildren.isEmpty() ? "" : renderer.renderKeyValue(infonode.getLabel(), myChildren) + renderer.renderNewLine(); // wenn keines der children TRUE war, dann brauchen wir den abschnitt nicht.
        } else {
            if (infonode.getValue().isPresent()) {
                if (infonode.isCheckbox()) {
                    text = renderer.renderBoolean(infonode.getLabel(), infonode.getValue().get().toString(), true) + renderer.renderNewLine();
                } else {
                    text = renderer.renderKeyValue(infonode.getLabel(), infonode.getValue().get().toString()) + renderer.renderNewLine();
                }
            }
        }
        return text;
    }


}