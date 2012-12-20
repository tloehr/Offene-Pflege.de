/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package op.system;

import op.OPDE;
import op.tools.SYSTools;
import org.eclipse.persistence.annotations.Array;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Diese Klasse befasst sich mit der Handhabung der speziellen Drucker für Etiketten und Kassenbons.
 * Sie enthält auch den XML Parser, der die Drucker und Vorlagen Konfigurationsdatei %workdir%/mapName2Printer.xml einliest.
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
    //    public static final int DRUCK_KEIN_DRUCK = 0;
//    public static final int DRUCK_ETI1 = 1;
//    public static final int DRUCK_ETI2 = 2;
//    public static final int DRUCK_BON1 = 3;
//    public static final int DRUCK_BON2 = 4;
//    public static final int DRUCK_LASER = 5;
    //public static int[] drucker
    private HashMap<String, LogicalPrinter> mapName2Printer;
    private ArrayList<LogicalPrinter> printers;
    private final String CONFIGFILE = "printers.xml";
    private HashMap tags;


//    public HashBean[] getPrinterTypeArray() {
//        return printerTypeArray;
//    }
//
//    private HashBean[] printerTypeArray;

    public HashMap<String, LogicalPrinter> getTypesMap() {
        return mapName2Printer;
    }

//    public Printer getPrinter(String type) {
//        Printer p = null;
//        Iterator<Printer> it = mapName2Printer.iterator();
//        boolean found = false;
//        while (!found && it.hasNext()) {
//            p = it.next();
//            found = p.getType().equals(type);
//        }
//        return (!found ? null : p);
//    }

    public LogicalPrinters() {
        initTags();
        printers = new ArrayList<LogicalPrinter>();
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            InputSource is = new InputSource(new FileInputStream(new File(OPDE.getOPWD() + System.getProperty("file.separator") + CONFIGFILE)));
            XMLHandler xml = new XMLHandler();
            parser.setContentHandler(xml);
            parser.parse(is);
        } catch (SAXException sAXException) {
            OPDE.fatal(sAXException);
        } catch (IOException iOException) {
            OPDE.fatal(iOException);
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
            mapName2Printer = new HashMap();
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
                mapName2Printer.put(printer.getName(), printer);
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

    public ArrayList<LogicalPrinter> getPrinterList() {
        return printers;
    }

    /**
     *
     */
    public void print(Object printData, String printer, DocFlavor flavor) {
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(MediaSizeName.ISO_A4);

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
        for (PrintService printService : prservices) {
            if (printService.getName().equalsIgnoreCase(printername)) {
                result = printService;
                break;
            }
        }
        return result;
    }

    /**
     * Standard Druck Routine. Nimmt einen HTML Text entgegen und öffnet den lokal installierten Browser damit.
     * Erstellt temporäre Dateien im temp Verzeichnis kueche<irgendwas>.html
     *
     * @param parent
     * @param html
     * @param addPrintJScript - Auf Wunsch kann an das HTML automatisch eine JScript Druckroutine angehangen werden.
     */
//    public static void print(Component parent, String html, boolean addPrintJScript) {
//        try {
//            // Create temp file.
//            File temp = File.createTempFile("kueche", ".html");
//
//            // Delete temp file when program exits.
//            temp.deleteOnExit();
//
//            if (addPrintJScript) {
//                html = "<html><head><script type=\"text/javascript\">"
//                        + "window.onload = function() {"
//                        + "window.print();"
//                        + "}</script>"
//                        + OPDE.getCSS()
//                        + "</head><body>" + SYSTools.htmlUmlautConversion(html)
//                        + "<hr/><span id=\"fonttext\"><b>Ende des Berichtes</b><br/>http://www.offene-pflege.de/component/content/article/3-informationen/16-kueche</span></body></html>";
//            } else {
//                html = "<html><head>" + OPDE.getCSS() + "</head><body>" + SYSTools.htmlUmlautConversion(html)
//                        + "<hr/><span id=\"fonttext\"><b>Ende des Berichtes</b><br/>http://www.offene-pflege.de/component/content/article/3-informationen/16-kueche</span></body></html>";
//            }
//
//            OPDE.debug(html);
//
//            // Write to temp file
//            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
//            out.write(html);
//
//            out.close();
//            handleFile(parent, temp.getAbsolutePath(), Desktop.Action.OPEN);
//        } catch (IOException e) {
//            OPDE.fatal(e);
//        }
//
//    }


//    public static void handleFile(Component parent, String filename, java.awt.Desktop.Action action) {
//        Desktop desktop = null;
//        if (parent == null) {
//            parent = new Frame();
//        }
//
//        if (SYSTools.getLocalDefinedApp(filename) != null) {
//            try {
//                Runtime.getRuntime().exec(SYSTools.getLocalDefinedApp(filename));
//            } catch (IOException ex) {
//                OPDE.getLogger().error(ex);
//            }
//        } else {
//
//            if (Desktop.isDesktopSupported()) {
//                desktop = Desktop.getDesktop();
//                if (action == Desktop.Action.OPEN && desktop.isSupported(Desktop.Action.OPEN)) {
//                    try {
//                        desktop.open(new File(filename));
//                    } catch (IOException ex) {
//                        JOptionPane.showMessageDialog(parent, "Datei \n" + filename + "\nkonnte nicht angezeigt werden.)",
//                                "Kein Anzeigeprogramm vorhanden", JOptionPane.INFORMATION_MESSAGE);
//                    }
//                } else if (action == Desktop.Action.PRINT && desktop.isSupported(Desktop.Action.PRINT)) {
//                    try {
//                        desktop.print(new File(filename));
//                    } catch (IOException ex) {
//                        JOptionPane.showMessageDialog(parent, "Datei \n" + filename + "\nkonnte nicht gedruckt werden.)",
//                                "Kein Druckprogramm vorhanden", JOptionPane.INFORMATION_MESSAGE);
//                    }
//                } else {
//                    JOptionPane.showMessageDialog(parent, "Datei \n" + filename + "\nkonnte nicht bearbeitet werden.)",
//                            "Keine passende Anwendung vorhanden", JOptionPane.INFORMATION_MESSAGE);
//                }
//            } else {
//                JOptionPane.showMessageDialog(parent, "JAVA Desktop Unterstützung nicht vorhanden", "JAVA Desktop API", JOptionPane.ERROR_MESSAGE);
//            }
//        }
//    }


//    private static void printPrintServiceAttributesAndDocFlavors(PrintService prserv) {
//        String s1 = null, s2;
//        Attribute[] prattr = prserv.getAttributes().toArray();
//        DocFlavor[] prdfl = prserv.getSupportedDocFlavors();
//        if (null != prattr && 0 < prattr.length) {
//            for (int i = 0; i < prattr.length; i++) {
//                Main.Main.logger.debug("      PrintService-Attribute[" + i + "]: " + prattr[i].getName() + " = " + prattr[i]);
//            }
//        }
//        if (null != prdfl && 0 < prdfl.length) {
//            for (int i = 0; i < prdfl.length; i++) {
//                s2 = prdfl[i].getMimeType();
//                if (null != s2 && !s2.equals(s1)) {
//                    Main.Main.logger.debug("      PrintService-DocFlavor-Mime[" + i + "]: " + s2);
//                }
//                s1 = s2;
//            }
//        }
//    }
}
