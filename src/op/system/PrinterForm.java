/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package op.system;

import op.tools.SYSTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author tloehr
 */
public class PrinterForm {
    private String name, label;
    private String form, encoding;
    //private HashMap attributes;
    private HashMap<String, ArrayList> elemAttributes;
    private final int LINECOUNTER = 0;
    private final int CHARCOUNTER = 1;

    private final int LEFT = 1;
    private final int CENTER = 2;
    private final int RIGHT = 3;

    public PrinterForm(String name, String label, HashMap elemAttributes, String encoding) {
        this.name = name;
        this.label = label;
        this.elemAttributes = elemAttributes;
        this.encoding = encoding;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }


    public void setFormtext(String form) {
        this.form = form;
    }

    public String getFormtext(HashMap attributes) {
        String myForm = form;
        // this.attributes = attributes;
        Iterator it = attributes.keySet().iterator();

        // Diese Map behält die Übersicht, welche Elemente sich zur Zeit in welcher Zeile und bei welchem Zeichen befinden.
        //HashMap<String, Integer[]> multilines = new HashMap<String, Integer[]>();

        while (it.hasNext()) {
            String providedAttribKey = it.next().toString();

            if (elemAttributes.containsKey(providedAttribKey)) {
                int currentCharForThisElement = 0;

                // Für jede Zeile einer Multiline. Wenn keine Multiline, dann eben nur einmal.
                for (int line = 0; line < elemAttributes.get(providedAttribKey).size(); line++) {

                    if (attributes.get(providedAttribKey) != null) {
                        // Wert für den Einsatz im Formular.
                        String replacement = attributes.get(providedAttribKey).toString();
                        // Parameter für das aktuelle Element
                        HashMap<String, String> attribs = (HashMap) elemAttributes.get(providedAttribKey).get(line);

                        // Defaults für die Parameter
                        int fixedlength = 0;
                        int maxlength = 0;
                        boolean multiline = false;
                        String fillchar = " ";
                        boolean toUpper = false;
                        boolean toLower = false;
                        int pad = RIGHT;
                        String dateformat = "dd.MM.yy";

                        if (attribs.containsKey("fixedlength")) {
                            fixedlength = Integer.parseInt(attribs.get("fixedlength"));
                        }
                        if (attribs.containsKey("maxlength")) {
                            maxlength = Integer.parseInt(attribs.get("maxlength"));
                        }
                        if (attribs.containsKey("multiline")) {
                            multiline = attribs.get("multiline").equalsIgnoreCase("true");
                        }
                        if (attribs.containsKey("fillchar")) {
                            fillchar = attribs.get("fillchar");
                        }
                        if (attribs.containsKey("toupper")) {
                            toUpper = attribs.get("toupper").equalsIgnoreCase("true");
                        }
                        if (attribs.containsKey("tolower")) {
                            toLower = attribs.get("tolower").equalsIgnoreCase("true");
                        }
                        if (attribs.containsKey("pad")) {
                            if (attribs.get("pad").equalsIgnoreCase("left")) {
                                pad = LEFT;
                            } else if (attribs.get("pad").equalsIgnoreCase("center")) {
                                pad = CENTER;
                            } else {
                                pad = RIGHT;
                            }
                        }
                        if (attribs.containsKey("dateformat")) {
                            dateformat = attribs.get("dateformat");
                        }

                        if (attributes.get(providedAttribKey) instanceof Date) {
                            SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
                            replacement = sdf.format((Date) attributes.get(providedAttribKey));
                        }

                        if (maxlength > 0) {
                            replacement = substring(replacement, currentCharForThisElement, currentCharForThisElement + maxlength);
                            currentCharForThisElement += maxlength;
                        } else {
                            replacement = substring(replacement, currentCharForThisElement, replacement.length());
                            currentCharForThisElement += replacement.length();
                        }

                        // Egal was vorher gerechnet wurde. Nur bei Multiline ist das interessant.
                        if (!multiline) {
                            currentCharForThisElement = 0;
                        }

                        if (fixedlength > 0) {
                            if (pad == LEFT) {
                                replacement = SYSTools.padL(replacement, fixedlength, fillchar);
                            } else if (pad == CENTER) {
                                replacement = SYSTools.padC(replacement, fixedlength, fillchar);
                            } else {
                                replacement = SYSTools.padR(replacement, fixedlength, fillchar);
                            }
                        }

                        if (toUpper) {
                            replacement = replacement.toUpperCase();
                        }

                        if (toLower) {
                            replacement = replacement.toLowerCase();
                        }

                        // Hier wird ein Teil des Formulars ausgefüllt. Jeweils für ein Element und (wenn nötig) für eine weitere Zeile
                        // So werden Stück für Stück alle Platzhalter gegen die Werte erstetzt.
                        myForm = SYSTools.replace(myForm, "$" + providedAttribKey + (line + 1) + "$", replacement, false);
                    } else {
                        myForm = SYSTools.replace(myForm, "$" + providedAttribKey + (line + 1) + "$", "", false);
                    }
                }

            }
        }

        return myForm;
    }

    /**
     * Bloss nicht vergessen zu dokumentieren.
     *
     * @param providedAttribKey
     * @return
     */
    private String getBaseFormAttrib(String providedAttribKey) {
        String newAttrib = providedAttribKey;
        // Das hier findet auch Schlüssel, die bei Multilines gebraucht werden.
        // Dann stehen von den Namen jeweils die Zeilennummern davor.
        // z.B. 1:produkt.bezeichnung


        if (providedAttribKey.matches("[0-9]+:{1}.*")) {
            int divider = providedAttribKey.indexOf(":");
            newAttrib = providedAttribKey.substring(divider);

        }
        return newAttrib;
    }

    /**
     * Mein eigener <i>substring</i>. Diese Version reagiert nicht mit einer Exception, wenn man
     * versucht substrings zu erhalten, die ganz oder teilweise außerhalb der Quell Strings liegen.
     * In diesem Fall wird einfach weniger oder gar nichts zurück gegeben.
     *
     * @param str   Eingangs-String
     * @param begin Stelle, ab der Substring beginnen soll.
     * @param end   Stelle, ab der Substring enden soll.
     * @return substring. ggf. gekürzt oder ganz leer.
     */
    private String substring(String str, int begin, int end) {
        int stop = Math.min(end, str.length()); // Bereinigung bei Ende, das außerhalb des Strings liegt.
        String result = "";
        if (begin < str.length()) { // Start liegt vor dem Ende des Strings.
            result = str.substring(begin, stop);
        }
        return result;
    }


}
