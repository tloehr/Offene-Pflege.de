/*
 * 
 *
 */
package tablerenderer;

import java.awt.Color;
import java.awt.Component;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import op.tools.SYSConst;
import op.tools.SYSTools;

/**
 * Diese Klasse implementiert einen Renderer für den JTable mit automatischer Anpassung der Zeilenhöhe und
 * Interpretation von HTML Code in den Zellen.
 * Ich habe die ursprüngliche Klasse im JavaSpecialist Newsletter #106 gefunden und weitestgehend* unverändert übernommen. 
 * Vielen Dank an den Autor Dr. Heinz M. Kabutz.
 * @author Heinz Kabutz: This code is from The Java Specialists' Newsletter http://www.javaspecialists.eu, used with permission.
 * @see http://www.offene-pflege.de/component/content/article/3-informationen/9-quellen#TJSN
 * 
 * geringe Änderung zur HTML Fähigkeit. JTextPane statt JTextArea
 */
public class RNDHTML extends JTextPane implements TableCellRenderer {

    private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();
    /** map from table to map of rows to map of column heights */
    private final Map cellSizes = new HashMap();

    public RNDHTML() {
        this.setEditable(false);
        this.setContentType("text/html");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
        adaptee.getTableCellRendererComponent(table, obj,
                isSelected, hasFocus, row, column);
        setForeground(adaptee.getForeground());
        setBackground(adaptee.getBackground());
        setBorder(adaptee.getBorder());
        setFont(adaptee.getFont());
        setText(adaptee.getText());

        // This line was very important to get it working with JDK1.4
        TableColumnModel columnModel = table.getColumnModel();
        setSize(columnModel.getColumn(column).getWidth(), 100000);
        int height_wanted = (int) getPreferredSize().getHeight();
        addSize(table, row, column, height_wanted);
        height_wanted = findTotalMaximumRowSize(table, row);
        if (height_wanted != table.getRowHeight(row)) {
            table.setRowHeight(row, Math.max(1, height_wanted));
        }

        setBackground(SYSTools.getTableCellBackgroundColor(isSelected, row));

        return this;
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
                it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            int cellHeight = ((Integer) entry.getValue()).intValue();
            maximum_height = Math.max(maximum_height, cellHeight);
        }
        return maximum_height;
    }
}
