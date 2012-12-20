package op.threads;

import entity.prescription.MedStock;
import entity.prescription.MedStockTools;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.system.LogicalPrinter;
import op.tools.PrintListElement;

import javax.print.DocFlavor;
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

    public void addPrintJobs(List<PrintListElement> jobs) {
        printQueue.addAll(jobs);
    }

    public void addPrintJob(PrintListElement job) {
        printQueue.add(job);
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public boolean isWorking(){
//        boolean working = OPDE.getProps().containsKey(SYSPropsTools.KEY_PHYSICAL_PRINTER);
//        working = working && OPDE.getLogicalPrinters().getPrintService(OPDE.getProps().getProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER)) != null;
//        working = working && OPDE.getProps().containsKey(SYSPropsTools.KEY_LOGICAL_PRINTER) && OPDE.getLogicalPrinters().getTypesMap().containsKey(OPDE.getProps().getProperty(SYSPropsTools.KEY_LOGICAL_PRINTER));
//        working = working && OPDE.getProps().containsKey(SYSPropsTools.KEY_MEDSTOCK_LABEL) && OPDE.getLogicalPrinters().getTypesMap().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_LOGICAL_PRINTER)).getForms().containsKey(SYSPropsTools.KEY_MEDSTOCK_LABEL);
        return true;
    }

    public boolean isIdle() {
        return printQueue.size() == 0;
    }

    public PrintProcessor() {
        super();
        preparedPrintJobs = new HashMap<String, ArrayList<PrintListElement>>();
        setName("PrintProcessor");
        interrupted = false;
        printQueue = new ArrayList();
    }

    public void run() {
        while (!interrupted) {
            try {
                if (!printQueue.isEmpty()) {
                    String currentPrinterName = "";
                    String combinedPrintJob = "";
                    LogicalPrinter currentPrinterType = null;
                    int size = printQueue.size();

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

                            OPDE.getLogicalPrinters().print(getPrintableObject(thisElement), thisElement.getPrintername(), DocFlavor.SERVICE_FORMATTED.PRINTABLE);

//                            pb.setValue(progressbar);
                            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.printing") + " " + OPDE.lang.getString("misc.msg.number") + " " + progressbar, progressbar, printQueue.size()));
//                            OPDE.debug("Drucke Nr. " + progressbar);
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
                        LogicalPrinter printerType = null;
                        String printjob = "";

                        Collection<ArrayList<PrintListElement>> collection = preparedPrintJobs.values();
                        for (ArrayList<PrintListElement> printListElements : collection) {
                            printerType = printListElements.get(0).getPrinter();
                            printername = printListElements.get(0).getPrintername();
                            printjob = printerType.getReset();

                            for (PrintListElement printListElement : printListElements) {
                                Object printableObject = getPrintableObject(printListElement);
                                if (printableObject != null) {
                                    printjob += getPrintableObject(printListElement);
                                } else {
                                    OPDE.error("invalid printer object. can't print a NULL value. please check printers.xml for typos");
                                }

                                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.printing") + " " + OPDE.lang.getString("misc.msg.number") + " " + progressbar, progressbar, printQueue.size()));

                                progressbar++;

                            }
                            printjob += printerType.getFooter();

                            OPDE.debug(printjob);

                            try {
                                encoded = printjob.getBytes(printerType.getEncoding());
                                OPDE.getLogicalPrinters().print(encoded, printername, flavor);

                                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.printing") + " " + OPDE.lang.getString("misc.msg.number") + " " + progressbar, progressbar, printQueue.size()));

                                progressbar++;

                            } catch (UnsupportedEncodingException e) {
                                OPDE.fatal(e);
                            }
                            printListElements.clear();
                        }
                        preparedPrintJobs.clear();
                    }
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                }
                Thread.sleep(2000); // Millisekunden
            } catch (InterruptedException ie) {
                interrupted = true;
                OPDE.debug("PrintProcessor interrupted!");
            }
        }
    }


    private Object getPrintableObject(PrintListElement element) {
        Object printableObject = null;
        if (element.getObject() instanceof MedStock) {
            MedStock bestand = (MedStock) element.getObject();
            OPDE.debug("PrintProcessor prints StockID: " + bestand.getID());

            printableObject = element.getPrinterForm().getFormtext(MedStockTools.getStock4Printing(bestand));
        }
        return printableObject;
    }

}
