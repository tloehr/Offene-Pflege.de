/*
 * 
 *
 */
package tablerenderer;

import op.tools.SYSTools;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Diese Klasse implementiert einen Renderer für den JTable mit automatischer Anpassung der Zeilenhöhe und
 * Interpretation von HTML Code in den Zellen.
 * Ich habe die ursprüngliche Klasse im JavaSpecialist Newsletter #106 gefunden und weitestgehend* unverändert übernommen.
 * Vielen Dank an den Autor Dr. Heinz M. Kabutz.
 *
 * Anmerkungen zu Nimbus:
 *
 *
 * @author Heinz Kabutz: This code is from The Java Specialists' Newsletter http://www.javaspecialists.eu, used with permission.
 * @see "http://www.offene-pflege.de/component/content/article/3-informationen/9-quellen#TJSN"
 *      <p/>
 *      geringe Änderung zur HTML Fähigkeit. JTextPane statt JTextArea
 */
public class RNDHTML implements TableCellRenderer {

    private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();
    /**
     * map from table to map of rows to map of column heights
     */
    private final Map cellSizes = new HashMap();

    JTextPane txt;
    JPanel panel;

    public RNDHTML() {

        // Dieser Trick mit dem Einbetten der JTextPane ist nur wegen dem Nimbus Bug bei der Hintergrundfarbe einer Text Component.
        // http://solutioncrawler.wordpress.com/2009/10/07/nimbus-lookfeel-und-seine-eigenarten/
        txt = new JTextPane();
        txt.setEditable(false);
        txt.setContentType("text/html");
        txt.setOpaque(false);
        txt.setBackground(new Color(0,0,0,0));

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(txt);

    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {

        txt.setText(obj.toString());


        // This line was very important to get it working with JDK1.4
        TableColumnModel columnModel = table.getColumnModel();
        txt.setSize(columnModel.getColumn(column).getWidth(), 100000);
        int height_wanted = (int) txt.getPreferredSize().getHeight();
        addSize(table, row, column, height_wanted);
        height_wanted = findTotalMaximumRowSize(table, row);
        if (height_wanted != table.getRowHeight(row)) {
            table.setRowHeight(row, Math.max(1, height_wanted));
        }

        //setBackground();

        panel.setBackground(SYSTools.getTableCellBackgroundColor(isSelected, row));

        //setBackground(Color.BLUE);

        return panel;
    }

    private void addSize(JTable table, int row, int column, int height) {
        //OpenCare.logger.debug("HTMLRenderer: addSize()");
        Map rows = (Map) cellSizes.get(table);
        if (rows == null) {
            cellSizes.put(table, rows = new HashMap());
        }
        Map rowheights = (Map) rows.get(new Integer(row));
        if (rowheights == null) {
            rows.put(new Integer(row), rowheights = new HashMap());
        }
        rowheights.put(new Integer(column), new Integer(height));
    }

    /**
     * Look through all columns and get the renderer.  If it is
     * also a TextAreaRenderer, we look at the maximum height in
     * its hash table for this row.
     */
    private int findTotalMaximumRowSize(JTable table, int row) {
        //OpenCare.logger.debug("HTMLRenderer: findTotalMaximumRowSize()");
        int maximum_height = 0;
        Enumeration columns = table.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn tc = (TableColumn) columns.nextElement();
            TableCellRenderer cellRenderer = tc.getCellRenderer();
            if (cellRenderer instanceof RNDHTML) {
                RNDHTML tar = (RNDHTML) cellRenderer;
                maximum_height = Math.max(maximum_height, tar.findMaximumRowSize(table, row));
            }
        }
        return maximum_height;
    }

    private int findMaximumRowSize(JTable table, int row) {
        //OpenCare.logger.debug("HTMLRenderer: findMaximumRowSize()");
        Map rows = (Map) cellSizes.get(table);
        if (rows == null) {
            return 0;
        }
        Map rowheights = (Map) rows.get(new Integer(row));
        if (rowheights == null) {
            return 0;
        }
        int maximum_height = 0;
        for (Iterator it = rowheights.entrySet().iterator();
             it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            int cellHeight = ((Integer) entry.getValue()).intValue();
            maximum_height = Math.max(maximum_height, cellHeight);
        }
        return maximum_height;
    }
}
