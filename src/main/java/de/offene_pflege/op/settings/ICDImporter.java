package de.offene_pflege.op.settings;

import de.offene_pflege.backend.entity.done.ICD;
import de.offene_pflege.op.OPDE;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 01.03.13
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
public class ICDImporter extends DefaultHandler {

//    private final EntityManager em;
    private String firstElement;
    private StringBuilder content;
    String code = null;
    ArrayList<ICD> listICDs;

    public ICDImporter() throws Exception {
//        this.em = em;
        listICDs = new ArrayList<>(10000);
        firstElement = null;
    }

    public ArrayList<ICD> getICDs() {
        return listICDs;
    }

    @Override
    public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {

        if (firstElement == null) {
            firstElement = tagName;
            if (!firstElement.equalsIgnoreCase("opdeicd")) throw new SAXException("not my kind of document");
        }

        if (tagName.equalsIgnoreCase("icd")) {
            code = attributes.getValue("code");
            OPDE.debug(code);
            content = new StringBuilder();
        }
    }

    @Override
    public void characters(char[] c, int start, int length)
            throws SAXException {
        if (code != null) {
            content.append(new String(c, start, length)); // remove double whitespaces, if any
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("icd")) {
            listICDs.add(new ICD(code, content.toString()));
            code = null;
            content = null;
        }
    }

}
