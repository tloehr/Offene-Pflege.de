package op.threads;

import entity.verordnungen.MedBestand;
import entity.verordnungen.MedBestandTools;
import op.OPDE;
import op.system.Printer;
import op.tools.PrintListElement;

import javax.print.DocFlavor;
import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Während des Einbuch-Vorgang können sehr schnell viele einzelne kleine Druckaufträge
 * für die Etiketten nötig werden. Damit die Erstellung dieser Jobs das Programm nicht anhält
 * bedienen wir uns hier einer nebenläufigen Programierung.
 */
public class PrintProcessor extends Thread {
    private boolean interrupted;
    private List<PrintListElement> printQueue;
    HashMap<String, ArrayList<PrintListElement>> preparedPrintJobs;
    JProgressBar pb;

    public void addPrintJobs(List<PrintListElement> jobs) {
        printQueue.addAll(jobs);
    }

    public void addPrintJob(PrintListElement job) {
        printQueue.add(job);
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public boolean isIdle() {
        return printQueue.size() == 0;
    }

    public PrintProcessor() {
        super();
        preparedPrintJobs = new HashMap<String,ArrayList<PrintListElement>>();
        setName("PrintProcessor");
        this.pb = new JProgressBar();
        interrupted = false;
        printQueue = new ArrayList();
        pb.setValue(0);
        pb.setMinimum(0);
    }

    public void run() {
        while (!interrupted) {
            try {
                if (!printQueue.isEmpty()) {
                    String currentPrinterName = "";
                    String combinedPrintJob = "";
                    Printer currentPrinterType = null;
                    int size = printQueue.size();
                    pb.setMaximum(size);
                    pb.setStringPainted(true);

                    int progressbar = 1;

                    //
                    // Schritt 1 Vorbereitung
                    //
                    for (int i = 0; i < size; i++) {
                        PrintListElement thisElement = printQueue.get(0);
                        printQueue.remove(0); // Das wird danach dann direkt aus der Liste gelöscht.

                        // Aus Geschwindigkeitsgründen (und die sind immens) werden innerhalb eines Drucklaufes
                        // alle nicht Seitendrucker-Ausgaben innerhalb eines Printjobs (im Sinne des Betriebssystems)
                        // zusammengefasst.
                        //
                        // Da sich die Länge der PrintQueue während der Bearbeitung verändern kann muss hier mit etwas Aufwand unterschieden
                        // werden. Sobald sich der Druckername (Betriebssystem) bei den Jobs ändert, muss ein PrintJob (OS Notion) erzeugt werden
                        // Bei Pagedruckern (also meist Postscript, ist dann, wenn direkt Printable Objects erzeugt werden) wird sowieso ein PrintJob
                        // per PrintListElement erzeug.
                        // Bleiben die zusammengebastelten Strings der LabelDrucker (epl2, esc2). Die werden mit einem einheitlichen Reset versehen und
                        // dann werden alle aufeinanderfolgenden Etiketten als Strings drangehangen. Erst bei Ende der Queue oder eben, wenn ein anderer
                        // Drucker dran kommt, wird der Printjob (OS Notion) erzeugt.
                        // Der Aufwand lohnt. Ansonsten wird so ein Zebra Drucker UNEEEEENDLICH langsam.
                        //
                        // Um das sauber durchzuführen wird in dieser Schleife hier eine Art Vorverarbeitung durchgeführt. Also werden die
                        // Printjobs praktisch vorbereitet (als PrintJob Preparation). In der nächsten Schleife werden sie dann der Reihe nach dem Betriebssystem
                        // zur Weiterverarbeitung übergeben.
                        //
                        if (!thisElement.getPrinter().isCombinePrintjobs()) {

                            OPDE.getPrinters().print(getPrintableObject(thisElement), thisElement.getPrintername(), DocFlavor.SERVICE_FORMATTED.PRINTABLE);

                            pb.setValue(progressbar);
                            pb.setString("Drucke Nr. " + progressbar);
                            OPDE.debug("Drucke Nr. " + progressbar);
                            progressbar++;

                        } else {
                            if (!preparedPrintJobs.containsKey(thisElement.getPrintername())) {
                                preparedPrintJobs.put(thisElement.getPrintername(), new ArrayList<PrintListElement>());
                            }
                            preparedPrintJobs.get(thisElement.getPrintername()).add(thisElement);
                        }
                    }

                    //
                    // Schritt 2 Abschluss
                    //
                    // Die PagePrinter Jobs sind jetzt schon durch
                    // Zu kombinierende Jobs stehen jetzt ggf. hier drin.
                    if (!preparedPrintJobs.isEmpty()) {
                        byte[] encoded = null;
                        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
                        String printername = "";
                        Printer printerType = null;
                        String printjob = "";

                        Collection<ArrayList<PrintListElement>> collection = preparedPrintJobs.values();
                        for (ArrayList<PrintListElement> printListElements : collection) {
                            printerType = printListElements.get(0).getPrinter();
                            printername = printListElements.get(0).getPrintername();
                            printjob = printerType.getReset();

                            for (PrintListElement printListElement : printListElements) {
                                printjob += getPrintableObject(printListElement);

                                pb.setValue(progressbar);
                                pb.setString("Drucke Nr. " + progressbar);
                                OPDE.debug("Drucke Nr. " + progressbar);
                                progressbar++;

                            }
                            printjob += printerType.getFooter();

                            OPDE.debug(printjob);

                            try {
                                encoded = printjob.getBytes(printerType.getEncoding());
                                OPDE.getPrinters().print(encoded, printername, flavor);

                                pb.setValue(progressbar);
                                pb.setString("Drucke Nr. " + progressbar);
                                OPDE.debug("Drucke Nr. " + progressbar);
                                progressbar++;

                            } catch (UnsupportedEncodingException e) {
                                OPDE.fatal(e);
                            }
                            printListElements.clear();
                        }
                        preparedPrintJobs.clear();
                    }
                    pb.setValue(size);
                }
                Thread.sleep(2000); // Millisekunden
                pb.setValue(0);
                pb.setString("");
                pb.setStringPainted(false);
            } catch (InterruptedException ie) {
                interrupted = true;
                OPDE.debug("PrintProcessor interrupted!");
            }
        }
    }


    private Object getPrintableObject(PrintListElement element) {
        Object printableObject = null;
        if (element.getObject() instanceof MedBestand) {
            MedBestand bestand = (MedBestand) element.getObject();
            OPDE.debug("PrintProcessor druckt BestID: " + bestand.getBestID());
//            if (element.getPrinter().isPageprinter()) {
//                printableObject = new TKLabel(VorratTools.getVorrat4Printing(vorrat));
//            } else {
//                printableObject = element.getForm().getForm(VorratTools.getVorrat4Printing(vorrat));
//            }
            printableObject = element.getForm().getFormtext(MedBestandTools.getBestand4Printing(bestand));
        }
        return printableObject;
    }

}
