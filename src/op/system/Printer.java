/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package op.system;

import op.tools.SYSTools;

import java.util.HashMap;

/**
 *
 * @author tloehr
 */
public class Printer {
    private String name;
    private String label;
    private String reset;
    private String footer;
    private String encoding;
    private boolean pageprinter;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;
    private HashMap<String,Form> forms;

    public String getFooter() {
        return footer;
    }

    public Printer(String name, String label, String type, String encoding, String pageprinter) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.encoding = encoding;
        this.pageprinter = SYSTools.catchNull(pageprinter).equalsIgnoreCase("true");
        this.footer = "";

    }

    public boolean isPageprinter() {
        return pageprinter;
    }

    public void setPageprinter(boolean pageprinter) {
        this.pageprinter = pageprinter;
    }

    public String getReset() {
        return reset;
    }

    public boolean isCombinePrintjobs(){
        return !pageprinter;
    }

    public void setReset(String reset) {
        this.reset = reset;
    }

    public HashMap<String,Form> getForms() {
        return forms;
    }

    public void setForms(HashMap<String,Form> forms) {
        this.forms = forms;
    }

    public String getLabel() {
        return label;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getName() {
        return name;
    }

   

}
