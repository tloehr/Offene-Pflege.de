/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tablerenderer;

import entity.Users;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import op.OPDE;
import tablemodels.TMUser;
import op.tools.SYSConst;

public class RNDOCUsers extends JLabel
        implements TableCellRenderer {

    Color fg, bg;

    public RNDOCUsers() {        
        setBorder(null);        
        this.fg = Color.BLACK;
        this.bg = Color.WHITE;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        setText(value.toString());
        Users ocuser = ((TMUser) table.getModel()).getUserAt(row);
        fg = ocuser.isActive() ? Color.BLACK : Color.GRAY;
        bg = isSelected ? SYSConst.bluegrey : Color.WHITE;
        setOpaque(true);
        return this;
    }

    @Override
    public Color getBackground() {
        return bg;
    }

    @Override
    public Color getForeground() {
        return fg;
    }

}
