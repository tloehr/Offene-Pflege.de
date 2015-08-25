/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package op.system;

import op.OPDE;
import op.tools.SYSTools;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Diese Klasse befasst sich mit der Handhabung der speziellen Drucker für Etiketten und Kassenbons.
 * Sie enthält auch den XML Parser, der die Drucker und Vorlagen Konfigurationsdatei %workdir%/mapName2LogicalPrinter.xml einliest.
 *
 * @author tloehr
 */
public class LogicalPrinters {

    public static final String ESCPOS_INIT_PRINTER = new String(new char[]{27, 40});
    public static final String ESCPOS_DOUBLE_HEIGHT_ON = new String(new char[]{27, 33, 16});
    public static final String ESCPOS_DOUBLE_HEIGHT_OFF = new String(new char[]{27, 33, 0});
    public static final String ESCPOS_DOUBLE_WIDTH_ON = new String(new char[]{27, 33, 32});
    public static final String ESCPOS_DOUBLE_WIDTH_OFF = new String(new char[]{27, 33, 0});
    public static final String ESCPOS_EMPHASIZED_ON = new String(new char[]{27, 33, 8});
    public static final String ESCPOS_EMPHASIZED_OFF = new String(new char[]{27, 33, 0});
    public static final String ESCPOS_UNDERLINE_ON = new String(new char[]{27, 45, 1});
    public static final String ESCPOS_UNDERLINE_OFF = new String(new char[]{27, 45, 0});
    public static final String ESCPOS_DOUBLE_STRIKE_ON = new String(new char[]{27, 71, 1});
    public static final String ESCPOS_DOUBLE_STRIKE_OFF = new String(new char[]{27, 71, 0});
    public static final String ESCPOS_PRINT_COLOR1 = new String(new char[]{27, 114, 0});
    public static final String ESCPOS_PRINT_COLOR2 = new String(new char[]{27, 114, 1});
    public static final String ESCPOS_CHARACTER_TABLE_PC437 = new String(new char[]{27, 116, 0});
    public static final String ESCPOS_CHARACTER_TABLE_PC850 = new String(new char[]{27, 116, 2});
    public static final String ESCPOS_FULL_CUT = new String(new char[]{29, 86, 65});
    public static final String ESCPOS_PARTIAL_CUT = new String(new char[]{29, 86, 66});

    private HashMap<String, LogicalPrinter> mapName2LogicalPrinter;
    private ArrayList<LogicalPrinter> printers;
    //    private final String CONFIGFILE = "printers.xml";
    private HashMap tags;


    public HashMap<String, LogicalPrinter> getMapName2LogicalPrinter() {
        return mapName2LogicalPrinter;
    }

    public LogicalPrinters() throws IOException {
        initTags();
        printers = new ArrayList<>();
        mapName2LogicalPrinter = new HashMap<>();

        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            InputSource is = new InputSource(new FileInputStream(AppInfo.getTemplate(AppInfo.filePrinters)));
            XMLHandler xml = new XMLHandler();
            parser.setContentHandler(xml);
            parser.parse(is);
        } catch (SAXException sAXException) {
            OPDE.fatal(sAXException);
        }

    }

    private class XMLHandler extends DefaultHandler {

        LogicalPrinter printer = null;
        HashMap<String, PrinterForm> forms = null;
        PrinterForm printerForm = null;
        String reset = null;
        String formtext = null;
        String line = null;
        HashMap<String, ArrayList> elemAttributes = null;

        @Override
        public void startDocument() throws SAXException {
//            mapName2LogicalPrinter = new HashMap();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (line != null) {
                line += new String(ch, start, length);
            }
        }

        @Override
        public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
            String name = attributes.getValue("name");
            String label = attributes.getValue("label");
            //Main.logger.debug("startElement: " + this.toString() + ": " + tagName + "    name: " + name);
            if (tagName.equalsIgnoreCase("printer")) {
                printer = new LogicalPrinter(attributes.getValue("name"), attributes.getValue("label"), attributes.getValue("type"), attributes.getValue("encoding"), attributes.getValue("pageprinter"));
            } else if (tagName.equalsIgnoreCase("reset")) {
                reset = "";
            } else if (tagName.equalsIgnoreCase("cr")) {
                line += System.getProperty("line.separator");
            } else if (tagName.equalsIgnoreCase("forms")) {
                forms = new HashMap<String, PrinterForm>();
            } else if (tagName.equalsIgnoreCase("form")) {
                elemAttributes = new HashMap();
                printerForm = new PrinterForm(name, label, elemAttributes, printer.getEncoding());
                formtext = "";
            } else if (tagName.equalsIgnoreCase("line")) {
                line = "";
            } else if (tagName.equalsIgnoreCase("elem")) {

                HashMap<String, String> attribsCopy = new HashMap();
                for (int i = 0; i < attributes.getLength(); i++) {
                    attribsCopy.put(attributes.getQName(i), attributes.getValue(i));
                }

                ArrayList<HashMap> parameterPerLine = null;
                if (elemAttributes.containsKey(name)) {
                    parameterPerLine = elemAttributes.get(name);
                } else {
                    parameterPerLine = new ArrayList();
                }
                parameterPerLine.add(attribsCopy);
                // Wenn Elemente mehrfach an verschiedenen Stellen vorkommen, wird hier jeweils die Nummer des Auftretens angehangen.
                // Die Liste wächst dann ja jeweils um ein Element an.
                // Dadurch wird die Multiline Geschichte direkt mit abgebügelt.
                line += "$" + name + parameterPerLine.size() + "$";

                elemAttributes.put(name, parameterPerLine);

            } else if (tagName.equalsIgnoreCase("char")) {
                line += new Character((char) Integer.parseInt(attributes.getValue("code")));
            } else {
                line += SYSTools.catchNull(tags.get(tagName));
            }

        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName.equalsIgnoreCase("reset")) {
                printer.setReset(reset);
                reset = null;
            } else if (localName.equalsIgnoreCase("form")) {
                printerForm.setFormtext(formtext);
                forms.put(printerForm.getName(), printerForm);
                printerForm = null;
            } else if (localName.equalsIgnoreCase("forms")) {
                printer.setForms(forms);
                forms = null;
            } else if (localName.equalsIgnoreCase("printer")) {
                mapName2LogicalPrinter.put(printer.getName(), printer);
                printers.add(printer);
                printer = null;
            } else if (localName.equalsIgnoreCase("line")) {
                if (reset != null) {
                    reset += line + System.getProperty("line.separator");
                } else if (formtext != null) {
                    formtext += line + System.getProperty("line.separator");
                }
                line = null;
            }

        }
    } // private class HandlerFragenStruktur

    private void initTags() {
        tags = new HashMap(17);
        tags.put("EscposInitPrinter", ESCPOS_INIT_PRINTER);
        tags.put("EscposDoubleHeightOn", ESCPOS_DOUBLE_HEIGHT_ON);
        tags.put("EscposDoubleHeightOff", ESCPOS_DOUBLE_STRIKE_OFF);
        tags.put("EscposDoubleWidthOn", ESCPOS_DOUBLE_WIDTH_ON);
        tags.put("EscposDoubleWidthOff", ESCPOS_DOUBLE_WIDTH_OFF);
        tags.put("EscposEmphasizedOn", ESCPOS_EMPHASIZED_ON);
        tags.put("EscposEmphasizedOff", ESCPOS_EMPHASIZED_OFF);
        tags.put("EscposUnderlineOn", ESCPOS_UNDERLINE_ON);
        tags.put("EscposUnderlineOff", ESCPOS_UNDERLINE_OFF);
        tags.put("EscposDoubleStrikeOn", ESCPOS_DOUBLE_STRIKE_ON);
        tags.put("EscposDoubleStrikeOff", ESCPOS_DOUBLE_STRIKE_OFF);
        tags.put("EscposPrintColor1", ESCPOS_PRINT_COLOR1);
        tags.put("EscposPrintColor2", ESCPOS_PRINT_COLOR2);
        tags.put("EscposCharacterTablePC437", ESCPOS_CHARACTER_TABLE_PC437);
        tags.put("EscposCharacterTablePC850", ESCPOS_CHARACTER_TABLE_PC850);
        tags.put("EscposFullCut", ESCPOS_FULL_CUT);
        tags.put("EscposPartialCut", ESCPOS_PARTIAL_CUT);
    }

    public ArrayList<LogicalPrinter> getLogicalPrintersList() {
        return printers;
    }

    /**
     *
     */
    public void print(Object printData, String printer, DocFlavor flavor) {
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(MediaSizeName.ISO_A4);

//        OPDE.info(flavor);

        try {
            PrintService ps = getPrintService(printer);
            if (ps != null) {
                Doc doc = new SimpleDoc(printData, flavor, null);
                ps.createPrintJob().print(doc, aset);
            }
        } catch (PrintException pe) {
            OPDE.fatal(pe);
        }
    }

    public PrintService getPrintService(String printername) {
        PrintService result = null;
        PrintService[] prservices = PrintServiceLookup.lookupPrintServices(null, null);
        if (prservices == null) return null;
        for (PrintService printService : prservices) {
            if (printService.getName().equalsIgnoreCase(printername)) {
                result = printService;
                break;
            }
        }
        return result;
    }

}
