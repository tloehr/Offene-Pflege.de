/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package op.system;

import javax.swing.*;
import javax.swing.table.TableModel;

/**
 *
 * @author tloehr
 */
public class TableModelHTMLConverter {

    /**
     * Erstellt ein HTML Dokument aus dem TableModel mit <b>allen</b> Spalten.
     * @param tbl - JTable, dass die Daten enthält.
     * @return
     */
    public static String convert(JTable tbl) {
        TableModel tm = tbl.getModel();
        int[] filter = new int[tm.getColumnCount()];
        for (int i = 0; i < tm.getColumnCount(); i++){
            filter[i] = i;
        }
        return convert(tbl, filter);
    }

    /**
     *
     * @param tbl - JTable, dass die Daten enthält.
     * @param filter - Array mit den Spaltennummern die mit in das HTML Dokument einbezogen werden sollen. Analog zur den Spaltennummern des TableModels.
     * @return
     */
    public static String convert(JTable tbl, int[] filter) {
        String result = "";
        TableModel tm = tbl.getModel();
        result += "<table id=\"fonttext\" border=\"1\">";
        result += "<tr>";
        for (int i = 0; i < filter.length; i++) {
            result += "<th>";
            result += tm.getColumnName(i);
            result += "</th>";
        }
        result += "</tr>";
        for (int row = 0; row < tm.getRowCount(); row++) {
            int r = tbl.convertRowIndexToModel(row);
            result += "<tr>";
            for (int col = 0; col < tm.getColumnCount(); col++) {
                result += "<td>";
                result += tm.getValueAt(r, col);
                result += "</td>";
            }
            result += "</tr>";
        }
        result += "</table>";

        return result;
    }
}
