/*
 * @author Heinz Kabutz: This code is from The Java Specialists' Newsletter http://www.javaspecialists.eu, used with permission.
 */

package tablerenderer;

import op.tools.SYSCalendar;
import op.tools.SYSConst;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 *
 * @author tloehr
 */
public class RNDStandard
        extends RNDTextArea {
    Color color;
    Font font;
    
    public RNDStandard() {
        super();
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        TableModel tm = table.getModel();
        
        if (isSelected) {
            if (row % 2 == 0) {
                this.color = SYSConst.grey80;
            } else {
                this.color = SYSConst.khaki3;
            }
        } else {
            if (row % 2 == 0) {
                this.color = Color.WHITE;
            } else {
                this.color = SYSConst.khaki2;
            }
        }
                
        if (value instanceof Date){
            value = SYSCalendar.printGermanStyle((Date) value);
        }
        
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
    
    public Color getBackground(){
        return color;
    }
    
}
