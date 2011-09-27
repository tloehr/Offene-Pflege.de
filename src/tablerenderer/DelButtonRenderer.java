/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tablerenderer;

import op.tools.SYSTools;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;

/**
 * @author tloehr
 */
public class DelButtonRenderer extends JPanel implements TableCellRenderer {
    protected JButton button;
    protected JPanel panel;
    protected MouseAdapter ma;

    public DelButtonRenderer() {
        panel = new JPanel();
        panel.add(new JButton(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/edit_remove.png"))));
        setName("Table.cellRenderer");
    }

    public Component getTableCellRendererComponent(final JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return panel;
    }



}




