package de.offene_pflege.op.settings;

import de.offene_pflege.backend.entity.done.ICD;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class ClaMLImporter extends DefaultHandler {
    boolean exclusion = false;
    boolean fragment = false;
    String code = null, modifierID = null, modifierCode = null, modifiedBy = null;
    StringBuilder value = new StringBuilder();
    ArrayList<ICD> listICDs;

    // Map for the Class Modifiers
    // Key is the ClassCode -> Map<ModifierCode, Text>
    HashMap<String, HashMap<String, ArrayList<String>>> modifierClasses;

    public ClaMLImporter() {
        modifierClasses = new HashMap();
        listICDs = new ArrayList<>(10000);
    }

    public ArrayList<ICD> getICDs() {
        return listICDs;
    }

    @Override
    public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
        if (tagName.equalsIgnoreCase("class") && attributes.getValue("kind").equalsIgnoreCase("category")) {
            code = attributes.getValue("code");
        } else if (tagName.equalsIgnoreCase("fragment")) {
            fragment = true;
        } else if (tagName.equalsIgnoreCase("rubric")) {
            value = new StringBuilder();
            exclusion = !(attributes.getValue("kind").equalsIgnoreCase("inclusion") || attributes.getValue("kind").equalsIgnoreCase("preferred"));
        } else if (tagName.equalsIgnoreCase("ModifierClass")) {
            modifierID = attributes.getValue("modifier");
            modifierCode = attributes.getValue("code");
            if (!modifierClasses.containsKey(modifierID)) {
                modifierClasses.put(modifierID, new HashMap<String, ArrayList<String>>());
            }
            if (!modifierClasses.get(modifierID).containsKey(modifierCode)) {
                modifierClasses.get(modifierID).put(modifierCode, new ArrayList<String>());
            }

            value = new StringBuilder();
        } else if (tagName.equalsIgnoreCase("ModifiedBy")) {
            modifiedBy = attributes.getValue("code");
        }

    }

    @Override
    public void characters(char[] c, int start, int length)
            throws SAXException {
        if (!exclusion && (code != null || modifierID != null)) {

            value.append(new String(c, start, length).replaceAll("\\s+", " ")); // remove double whitespaces, if any

            if (fragment) {
                value.append(" ");
            }

        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("class")) {
            if (modifiedBy != null) {
                for (String mcode : modifierClasses.get(modifiedBy).keySet()) {
                    for (String text : modifierClasses.get(modifiedBy).get(mcode)) {
                        listICDs.add(new ICD((code + mcode).toString(), text));
                    }
                }
            }
            code = null;
            modifiedBy = null;
        } else if (qName.equalsIgnoreCase("fragment")) {
            fragment = false;
        } else if (qName.equalsIgnoreCase("rubric")) {
            if (modifierID != null) {
                if (!exclusion) {
                    modifierClasses.get(modifierID).get(modifierCode).add(value.toString().replaceAll("\\s+", " ").trim());
                }
            } else {
                if (code != null && !exclusion) {
                    if (fragment) {
                        value.delete(value.length() - 1, value.length());
                    }
                    listICDs.add(new ICD(code, value.toString().replaceAll("\\s+", " ").trim()));
                }
            }
            value = null;
            exclusion = true;
        } else if (qName.equalsIgnoreCase("ModifierClass")) {
            modifierCode = null;
            modifierID = null;
        }
    }

}