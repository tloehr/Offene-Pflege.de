package de.offene_pflege.services.qdvs;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * diese Klasse erstellt eine art Lookup Table für die Bewohner IDs. Ich erhalte vom Validator leider nur die Zeilen und
 * Spalten Nummer des schließenden qs_data Elements. Daher muss ich hier einen Index aufbauen, damit ich das nachher
 * zuordnen kann.
 */
public class QSData extends DefaultHandler {
    long idbewohner = 0l;
    MultiKeyMap<MultiKey<Integer>, Long> lookup; //enthält den Bewohner zu den jeweiligen zeilen und Spalten des End Tags "qsdata"
    private Locator locator;

    public QSData() {
        lookup = new MultiKeyMap();
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }


    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("IDBEWOHNER")) {
            idbewohner = Long.valueOf(attributes.getValue(0));
        }
    }

    public MultiKeyMap getLookup() {
        return lookup;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("qs_data")) {
            lookup.put(new MultiKey(locator.getLineNumber(), locator.getColumnNumber()), idbewohner);
        }
    }
}
