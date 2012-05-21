/*
 * OffenePflege
 * Copyright (C) 2008 Torsten L?hr
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
 * Auf deutsch (freie ?bersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie k?nnen es unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation ver?ffentlicht, weitergeben und/oder modifizieren, gem?? Version 2 der Lizenz.
 *
 * Die Ver?ffentlichung dieses Programms erfolgt in der Hoffnung, da? es Ihnen von Nutzen sein wird, aber
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT F?R EINEN
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht,
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 *
 */
package op.tools;

import entity.verordnungen.MedBestand;
import entity.verordnungen.MedBestandTools;
import op.OPDE;

import javax.print.*;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author tloehr
 */
public class SYSPrint {

    public static final String sCrLf = System.getProperty("line.separator");
    public static final String ESCPOS_LF = new String(new char[]{10});
    //public static final String ESCPOS_FEED = new String(new char[]{27,});
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
    public static final int EPL2_FONT_6pt = 1;
    public static final int EPL2_FONT_7pt = 2;
    public static final int EPL2_FONT_10pt = 3;
    public static final int EPL2_FONT_12pt = 4;
    public static final int EPL2_FONT_24pt = 5;
    public static final String EPL2_RESET = "^@\n";
    public static final String EPL2_CLEAR_IMAGE_BUFFER = "N\n";
    public static final String EPL2_PRINT = "P1\n";
    public static String PRINTER = "prt0009";

    /**
     * Creates a new instance of OCPrint
     */
    public SYSPrint() {
    }

    public static String reset() {
        //return ESCPOS_INIT_PRINTER+ESCPOS_CHARACTER_TABLE_PC850;
        return ESCPOS_CHARACTER_TABLE_PC850;
    }

    public static String doubleStrike(String in) {
        return ESCPOS_DOUBLE_STRIKE_ON + in + ESCPOS_DOUBLE_STRIKE_OFF;
    }

    public static String doubleHeight(String in) {
        return ESCPOS_DOUBLE_HEIGHT_ON + in + ESCPOS_DOUBLE_HEIGHT_OFF;
    }

    public static String doubleWidth(String in) {
        return ESCPOS_DOUBLE_WIDTH_ON + in + ESCPOS_DOUBLE_WIDTH_OFF;
    }

    public static String underline(String in) {
        return ESCPOS_UNDERLINE_ON + in + ESCPOS_UNDERLINE_OFF;
    }

    public static String red(String in) {
        return ESCPOS_PRINT_COLOR2 + in + ESCPOS_PRINT_COLOR1;
    }

    public static void printLabel(MedBestand bestand) {
        String text = "";
        if (OPDE.getProps().containsKey("labelprinter")) {
            PRINTER = OPDE.getProps().get("labelprinter").toString();
        }

        text = MedBestandTools.getBestandText4Print(bestand);

        printLabel(text);
    }

    private static void printLabel(String text) {

        OPDE.debug(text);

        DocFlavor flavor = DocFlavor.STRING.TEXT_PLAIN;//.STRING;//DocFlavor.INPUT_STREAM.TEXT_HTML_US_ASCII;

        // Set print attributes:
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();

        aset.add(MediaSizeName.ISO_A4);

        try {
            PrintService[] prservices = PrintServiceLookup.lookupPrintServices(null, null); // alle Drucker listen
//            for (int i=0; i < prservices.length; i++){
//                OPDE.debug(prservices[i].getName());
//            }
            int idxPrintService = 0;
            if (null == prservices || 0 >= prservices.length) {
                System.out.println("Print-Services:");
                // Drucker raussuchen
            }
            for (int i = 0; i < prservices.length; i++) {
                System.out.println("  " + i + ":  " + prservices[i]);
                if (prservices[i].getName().equalsIgnoreCase(PRINTER)) {
                    idxPrintService = i;
                }
            }
            PrintService prserv = prservices[idxPrintService];

            if (null != prserv) {
                OPDE.info("Ausgewaehlter Print-Service:");
                OPDE.info("      " + prserv);
                printPrintServiceAttributesAndDocFlavors(prserv);
                DocPrintJob pj = prserv.createPrintJob();
                //FileInputStream fis = new FileInputStream();
                // new java.io.BufferedReader(new java.io.StringReader(xmltext))
                Doc doc = new SimpleDoc(text, flavor, null);

                pj.print(doc, aset);
            }


        } catch (PrintException pe) {
            System.err.println(pe);
        }
    }

    private static void printPrintServiceAttributesAndDocFlavors(PrintService prserv) {
        String s1 = null, s2;
        Attribute[] prattr = prserv.getAttributes().toArray();
        DocFlavor[] prdfl = prserv.getSupportedDocFlavors();
        if (null != prattr && 0 < prattr.length) {
            for (int i = 0; i < prattr.length; i++) {
                OPDE.debug("      PrintService-Attribute[" + i + "]: " + prattr[i].getName() + " = " + prattr[i]);
            }
        }
        if (null != prdfl && 0 < prdfl.length) {
            for (int i = 0; i < prdfl.length; i++) {
                s2 = prdfl[i].getMimeType();
                if (null != s2 && !s2.equals(s1)) {
                    OPDE.debug("      PrintService-DocFlavor-Mime[" + i + "]: " + s2);
                }
                s1 = s2;
            }
        }
    }

//    public static void printReport(boolean preview, TableModel tm, HashMap tmcolassign, String reportFilenameWOExtension) {
//        printReport(preview, tm, tmcolassign, new HashMap(), reportFilenameWOExtension);
//    }
//
//    /**
//     * Die Methode druckt ein beliebige Tabelle aus. Sie verwendet die HashMap tmcolassign um die Feldern von Jasper auf
//     * die Feldern des TableModels abzubilden.
//     *
//     */
//    public static void printReport(boolean preview, TableModel tm, HashMap tmcolassign, HashMap params, String reportFilenameWOExtension) {
//        //JRDSTableModel jrds = new JRDSTableModel(tm, tmcolassign);
//        printReport(preview, tm, tmcolassign, params, reportFilenameWOExtension, !preview);
//    }

//    /**
//     * Die Methode druckt ein beliebige Tabelle aus. Sie verwendet die HashMap tmcolassign um die Feldern von Jasper auf
//     * die Feldern des TableModels abzubilden.
//     *
//     */
//    public static void printReport(boolean preview, TableModel tm, HashMap tmcolassign, HashMap params, String reportFilenameWOExtension, boolean dialog) {
//        JRDSTableModel jrds = new JRDSTableModel(tm, tmcolassign);
//        printReport(preview, jrds, params, reportFilenameWOExtension, dialog);
//    }

//    public static void printReport(boolean preview, JRDataSource jrds, HashMap params, String reportFilenameWOExtension, boolean dialog) {
//        try {
//            String defaultPrinter = "";
//            if (OPDE.getProps().containsKey("defaultprinter")) {
//                defaultPrinter = OPDE.getProps().getProperty("defaultprinter");
//            }
////            HashMap einrichtung = op.tools.DBRetrieve.getEinrichtung();
////            String ein = einrichtung.get("Bezeichnung").toString() + ", "
////                    + einrichtung.get("Strasse").toString() + ", "
////                    + einrichtung.get("PLZ").toString() + " " + einrichtung.get("Ort").toString() + ", "
////                    + "Tel.:" + einrichtung.get("Tel").toString() + ", "
////                    + "Fax.:" + einrichtung.get("Fax").toString();
////            einrichtung.clear();
////            if (!params.containsKey("einrichtung")) {
////                params.put("einrichtung", ein);
////            }
////            if (!params.containsKey("txtHeim")) {
////                params.put("txtHeim", ein);
////            }
//            if (!params.containsKey("PRTTimeAndUser")) {
//                params.put("PRTTimeAndUser", "Gedruckt am: " + SYSCalendar.printGCGermanStyle(new GregorianCalendar()) + " // " + OPDE.getLogin().getUser().getUKennung());
//            }
//            if (!params.containsKey("prtInfo")) {
//                params.put("prtInfo", "Gedruckt am: " + SYSCalendar.printGCGermanStyle(new GregorianCalendar()) + " // " + OPDE.getLogin().getUser().getUKennung());
//            }
//
//            String reportTargetFilename = compileReport(reportFilenameWOExtension);
//            if (!reportTargetFilename.equals("")) {
//                JasperPrint jasperPrint = JasperFillManager.fillReport(reportTargetFilename, params, jrds);
//
//                if (preview) {
//                    JasperViewer.viewReport(jasperPrint, false);
//                } else if (defaultPrinter.equals("") || dialog) {
//                    JasperPrintManager.printReport(jasperPrint, true);
//                } else {
//                    String printer = defaultPrinter;
//                    boolean showDialog = true;
//                    if (OPDE.getProps().containsKey("printer")) {
//                        printer = OPDE.getProps().getProperty("printer");
//                        showDialog = false;
//                    }
//                    PrintService prserv = getPrintService(printer);
//
//                    if (prserv != null) {
//                        OPDE.info("Ausgewaehlter Print-Service:");
//                        OPDE.info("      " + prserv);
//                        //DocPrintJob pj = prserv.createPrintJob();
//
//                        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
//                        //MediaSizeName mediaSizeName = MediaSizeName.ISO_A4; //MediaSize.ISO.^findMedia(4,4,MediaPrintableArea.INCH);
//                        //printRequestAttributeSet.add(mediaSizeName);
//                        printRequestAttributeSet.add(new Copies(1));
//                        JRPrintServiceExporter exporter;
//                        exporter = new JRPrintServiceExporter();
//                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
//                        /* We set the selected service and pass it as a paramenter */
//                        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, prserv);
//                        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, prserv.getAttributes());
//                        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
//                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
//                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, new Boolean(showDialog));
//                        exporter.exportReport();
//
//                    } else {
//                        JasperPrintManager.printReport(jasperPrint, true);
//                    }
//
//                }
//                OPDE.info("Druck " + reportFilenameWOExtension + " von " + OPDE.getLogin().getUser().getUKennung());
//            } else {
//                OPDE.info("Reportdatei " + reportFilenameWOExtension + " nicht verf¸gbar.");
//            }
//        } catch (JRException ex) {
//            new DlgException(ex);
//        } catch (SocketException ex) {
//            new DlgException(ex);
//        } catch (IOException ex) {
//            new DlgException(ex);
//        }
//    }
//
//    public static String compileReport(String reportFilenameWOExtension) throws JRException, SocketException, IOException {
//        String sep = System.getProperty("file.separator");
//        return "";
////        // Erstmal gucken, ob es die compilierte Version schon gibt ?
////        long ocfidsource = SYSFiles.findlastFile(reportFilenameWOExtension + ".jrxml");
////        long ocfidtarget = SYSFiles.findlastFile(reportFilenameWOExtension + ".jasper");
////        String reportSourceFilename = OPDE.getProps().getProperty("occache") + sep + reportFilenameWOExtension + ".jrxml";
////        String reportTargetFilename = OPDE.getProps().getProperty("occache") + sep + reportFilenameWOExtension + ".jasper";
////
////        File source = null;
////        File target = null;
////        SYSFiles ocfiles = new SYSFiles();
////
////        if (ocfidtarget > 0) { // Gibts ¸berhaupt eine .jasper Datei ?
////            if (ocfidsource > 0) { // Habe ich ¸berhaupt Zugriff auf den Source und ist der Source auch neuer ?
////                Date datetarget = (Date) DBRetrieve.getSingleValue("OCFiles", "Filedate", "OCFID", ocfidtarget);
////                Date datesource = (Date) DBRetrieve.getSingleValue("OCFiles", "Filedate", "OCFID", ocfidsource);
////                if (datesource.after(datetarget)) { // er ist neuer!
////                    OPDE.info("Neu ?bersetzung der Reportdatei n?tig: " + reportFilenameWOExtension);
////                    source = ocfiles.getFile(ocfidsource);
////                    JasperCompileManager.compileReportToFile(reportSourceFilename, reportTargetFilename);
////                    target = new File(reportTargetFilename);
////                    ocfiles.deleteFile(ocfidtarget); // alte l?schen.
////                    ocfidtarget = ocfiles.putFile(target); // neu hochladen.
////                }
////            }
////            if (target == null) {
////                target = ocfiles.getFile(ocfidtarget);
////            }
////        } else { // keine Jasper Datei
////            if (ocfidsource > 0) { // Habe ich ¸berhaupt Zugriff auf den Source ?
////                OPDE.info("Neu ?bersetzung der Reportdatei n?tig: " + reportFilenameWOExtension);
////                source = ocfiles.getFile(ocfidsource);
////                JasperCompileManager.compileReportToFile(reportSourceFilename, reportTargetFilename);
////                target = new File(reportTargetFilename);
////                ocfidtarget = ocfiles.putFile(target); // neu hochladen.
////            }
////        }
////        ocfiles.disconnect();
////        return (target != null ? target.getAbsolutePath() : "");
//    }

    private static PrintService getPrintService(String printer) {

        PrintService[] prservices = PrintServiceLookup.lookupPrintServices(null, null); // alle Drucker listen
        int idxPrintService = 0;
        if (null == prservices || 0 >= prservices.length) {
            System.out.println("Print-Services:");
            // Drucker raussuchen
        }
        for (int i = 0; i < prservices.length; i++) {
            System.out.println("  " + i + ":  " + prservices[i]);
            if (prservices[i].getName().equalsIgnoreCase(printer)) {
                idxPrintService = i;
            }
        }
        return prservices[idxPrintService];
    }

    /**
     * Standard Druck Routine. Nimmt einen HTML Text entgegen und öffnet den lokal installierten Browser damit.
     * Erstellt temporäre Dateien im temp Verzeichnis opde<irgendwas>.html
     *
     * @param html
     * @param addPrintJScript Auf Wunsch kann an das HTML automatisch eine JScript Druckroutine angehangen werden.
     */
    public static File print(String html, boolean addPrintJScript) {
        File temp = null;
        try {
            // Create temp file.
            temp = File.createTempFile("opde", ".html");


            String text = "<html><head>";
            if (addPrintJScript) {
                text += "<script type=\"text/javascript\">" +
                        "window.onload = function() {"
                        + "window.print();"
                        + "}</script>";
            }
            text += OPDE.getCSS();
            text += "</head><body>" + SYSTools.htmlUmlautConversion(html)
                    + "<hr/>" +
                    "<div font=\"fonttext\">" +
                    "<b>Ende des Berichtes</b><br/>" + (OPDE.getLogin() != null ? SYSTools.htmlUmlautConversion(OPDE.getLogin().getUser().getNameUndVorname()) : "")
                    + "<br/>" + DateFormat.getDateTimeInstance().format(new Date())
                    + "<br/>http://www.offene-pflege.de</div></body></html>";


            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(text);

            out.close();
            handleFile(temp.getAbsolutePath(), Desktop.Action.OPEN);
        } catch (IOException e) {
            OPDE.debug(e);
        }
        return temp;
    }

//    public static void showFile(Component parent, String filename) {
//        handleFile(parent, filename, Desktop.Action.OPEN);
//    }

    public static void handleFile(String filename, java.awt.Desktop.Action action) {
        Desktop desktop = null;

            Component parent = new Frame();


        if (SYSTools.getLocalDefinedApp(filename) != null) {
            try {
                Runtime.getRuntime().exec(SYSTools.getLocalDefinedApp(filename));
            } catch (IOException ex) {
                OPDE.getLogger().error(ex);
            }
        } else {

            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
                if (action == Desktop.Action.OPEN && desktop.isSupported(Desktop.Action.OPEN)) {
                    try {
                        desktop.open(new File(filename));
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(parent, "Datei \n" + filename + "\nkonnte nicht angezeigt werden.)",
                                "Kein Anzeigeprogramm vorhanden", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else if (action == Desktop.Action.PRINT && desktop.isSupported(Desktop.Action.PRINT)) {
                    try {
                        desktop.print(new File(filename));
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(parent, "Datei \n" + filename + "\nkonnte nicht gedruckt werden.)",
                                "Kein Druckprogramm vorhanden", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(parent, "Datei \n" + filename + "\nkonnte nicht bearbeitet werden.)",
                            "Keine passende Anwendung vorhanden", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parent, "JAVA Desktop Unterstützung nicht vorhanden", "JAVA Desktop API", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static String EPL2_print_ascii(int hstart, int vstart, int rotation, int font, int hmultiplier, int vmultiplier, boolean reverse, String data) {
        String result = "A" + hstart + "," + vstart + "," + rotation + "," + font + "," + hmultiplier + "," + vmultiplier + ","
                + (reverse ? "R" : "N") + ",\"" + data + "\"\n";
        return result;
    }

    public static String EPL2_labelformat(int width, int height, int gap) {
        String result = "q" + (width * 8) + "\nQ" + (height * 8) + "," + (gap * 8) + "\n";
        return result;
    }
}
