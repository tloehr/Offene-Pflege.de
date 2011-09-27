package tablerenderer;

import op.tools.SYSTools;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 25.06.11
 * Time: 13:43
 * To change this template use File | Settings | File Templates.
 */
public class ButtonsRenderer extends JPanel implements TableCellRenderer {
    protected HashMap<JButton, TableButtonBehaviour> behaviourMap;

    public ButtonsRenderer(JButton cancelButton, Object[]... buttons) {
        super();
        setOpaque(true);
        behaviourMap = new HashMap<JButton, TableButtonBehaviour>();

        for (Object[] mybutton : buttons) {
            final JButton button = (JButton) mybutton[0];
            final TableButtonBehaviour action = (TableButtonBehaviour) mybutton[1];
            behaviourMap.put(button, action);
            button.setFocusable(false);
            button.setRolloverEnabled(false);
            add(button);
        }
        add(cancelButton);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.setBackground(SYSTools.getTableCellBackgroundColor(isSelected, row));
        setButtonsEnabled(table, row, column);
        return this;
    }

    protected void setButtonsEnabled(JTable table, int row, int col){
        Iterator<JButton> it = behaviourMap.keySet().iterator();
        while (it.hasNext()){
            JButton btn = it.next();
            btn.setEnabled(behaviourMap.get(btn).isEnabled(table, row, col));
        }
    }

}
