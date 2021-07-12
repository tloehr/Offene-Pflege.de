/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.op.tools;

import java.math.BigDecimal;

/**
 * @author tloehr
 */
public class HTMLTools {

    public static final String JSCRIPT_PRINT = "<script type=\"text/javascript\">"
            + "window.onload = function() {"
            + "window.print();"
            + "}</script>";

    /**
     * Erzeugt Bruchdarstellung (für 0.5, 0.25, 0.75, 0.33) in HTML zu dem übergebenen Wert.
     */
    public static String printDouble(double d) {
        String dbl = Double.toString(d);
        if (dbl.equals("0.0")) {
            dbl = "&nbsp;";
        } else if (dbl.substring(dbl.length() - 2).equals(".0")) {
            dbl = dbl.substring(0, dbl.length() - 2);
        } else if (dbl.equals("0.5")) {
            dbl = "&frac12;";
        } else if (dbl.equals("0.25")) {
            dbl = "&frac14;";
        } else if (dbl.equals("0.75")) {
            dbl = "&frac34;";
        } else if (dbl.equals("0.33")) {
            dbl = "<sup>1</sup>/<sub>3</sub>";
        }
        return dbl;
    }

    public static String getTable(String content, String attribs) {
            return "<table id=\"fonttext\" " + attribs + " \">" + SYSTools.xx(content) + "</table>\n";
        }

    /**
     * Erzeugt Bruchdarstellung (für 0.5, 0.25, 0.75, 0.33) in HTML zu dem übergebenen Wert.
     */
    public static String printDouble(BigDecimal bd) {
        return printDouble(bd.doubleValue());
    }

    /**
     * Erzeugt eine HTML Tabellen Zeile aus den verschiedenen Parametern, die der Methode übergeben wurden. Nur die
     * Zeile inkl. der "tr" Tags.
     *
     * @param objects
     * @return
     */
    public static String getTableRow(Object... objects) {
        return "<tr>" + getTableData(objects) + "</tr>";
    }

    /**
     * Erzeugt eine HTML Tabellen Zeile aus den verschiedenen Parametern, die der Methode übergeben wurden. Nur die
     * Zeile inkl. der "th" Tags.
     *
     * @param objects
     * @return
     */
    public static String getTableHead(Object... objects) {
        return "<th>" + getTableData(objects) + "</th>";
    }

    /**
     * Erzeugt eine HTML Tabellen Zeile aus den verschiedenen Parametern, die der Methode übergeben wurden.
     *
     * @param objects
     * @return
     */
    public static String getTableData(Object... objects) {
        String result = "";
        for (Object o : objects) {
            result += "<td>" + o.toString() + "</td>";
        }
        return result;
    }

    /**
     * Fügt html Tags vor und hinter den Eingangsstring ein.
     *
     * @param in Eingangsstring
     * @return String mit HTML Erweiterungen.
     */
    public static String toHTML(String in) {
        String out = null;
        if (!SYSTools.catchNull(in).equals("")) {
            out = "<html>\n<head>\n" +
                    "  <meta charset=\"UTF-8\">\n" +
                    "</head>\n<body>" + SYSTools.xx(in) + "</body></html>";
        }
        return out;
    }

    public static String p(String in) {
        String out = null;
        if (!SYSTools.catchNull(in).equals("")) {
            out = "<p>" + SYSTools.xx(in) + "</p>";
        }
        return out;
    }
}
