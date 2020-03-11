package de.offene_pflege.op.tools;

import de.offene_pflege.entity.prescription.MedStock;
import de.offene_pflege.op.system.LogicalPrinter;
import de.offene_pflege.op.system.PrinterForm;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 02.03.12
 * Time: 14:09
 * To change this template use File | Settings | File Templates.
 */
public class PrintListElement implements Comparable {

    private LogicalPrinter printer;
    private String printername; // Name des Druckers innerhalb des Betriebssystems.
    private PrinterForm printerForm;

    @Override
    public int compareTo(Object o) {

        int result = 0;
        if (((PrintListElement) o).getObject() instanceof MedStock){
            result = new Long(((MedStock) object).getID()).compareTo(((MedStock)((PrintListElement) o).getObject()).getID());
        }
        return result;
    }

    public Object getObject() {
        return object;
    }

    public PrintListElement(Object object, LogicalPrinter printer, PrinterForm printerForm, String printername) {
        this.object = object;
        this.printer = printer;
        this.printerForm = printerForm;
        this.printername = printername;
    }


    public String getPrintername() {
        return printername;
    }

    public LogicalPrinter getPrinter() {
        return printer;
    }

    public PrinterForm getPrinterForm() {
        return printerForm;
    }

    Object object;
}