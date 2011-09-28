/*
 * OffenePflege
 * Copyright (C) 2008 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License V2 as published by the Free Software Foundation
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------ 
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 * 
 */

package op.care.vital;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;

/**
 * @author tloehr
 */
public class ParseXMLBraden {

    private int[] werte = {0, 0, 0, 0, 0, 0};

    /**
     * Creates a new instance of ParseXMLBraden
     */
    public ParseXMLBraden(String xml) {
        try {
            String text = "<?xml version=\"1.0\"?>" + xml;
            XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            InputSource is = new org.xml.sax.InputSource(new java.io.BufferedReader(new java.io.StringReader(text)));
            parser.setContentHandler(new HandlerBraden());
            parser.parse(is);
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private class HandlerBraden extends DefaultHandler {

        public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
            if (tagName.equalsIgnoreCase("braden")) {
                getWerte()[0] = Integer.parseInt(attributes.getValue("a1"));
                getWerte()[1] = Integer.parseInt(attributes.getValue("a2"));
                getWerte()[2] = Integer.parseInt(attributes.getValue("a3"));
                getWerte()[3] = Integer.parseInt(attributes.getValue("a4"));
                getWerte()[4] = Integer.parseInt(attributes.getValue("a5"));
                getWerte()[5] = Integer.parseInt(attributes.getValue("a6"));

            }
        }

    } // private class HandlerI

    public int[] getWerte() {
        return werte;
    }
}
