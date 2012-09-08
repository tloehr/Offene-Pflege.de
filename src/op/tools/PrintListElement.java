package op.tools;

import entity.prescription.MedStock;
import op.system.Form;
import op.system.PrinterType;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 02.03.12
 * Time: 14:09
 * To change this template use File | Settings | File Templates.
 */
public class PrintListElement implements Comparable {

    private PrinterType printer;
    private String printername; // Name des Druckers innerhalb des Betriebssystems.
    private Form form;

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

    public PrintListElement(Object object, PrinterType printer, Form form, String printername) {
        this.object = object;
        this.printer = printer;
        this.form = form;
        this.printername = printername;
    }


    public String getPrintername() {
        return printername;
    }

    public PrinterType getPrinter() {
        return printer;
    }

    public Form getForm() {
        return form;
    }

    Object object;
}