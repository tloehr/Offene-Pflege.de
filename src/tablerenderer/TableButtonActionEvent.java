package tablerenderer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 27.06.11
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
public class TableButtonActionEvent extends ActionEvent {

    JTable table;

    public TableButtonActionEvent(Object source, int id, String command) {
        super(source, id, command);
    }

    public TableButtonActionEvent(Object source, int id, String command, int modifiers) {
        super(source, id, command, modifiers);
    }

    public TableButtonActionEvent(Object source, int id, String command, long when, int modifiers) {
        super(source, id, command, when, modifiers);
    }

    public TableButtonActionEvent(ActionEvent evt, JTable table) {
        super(evt.getSource(), evt.getID(), evt.getActionCommand());
        this.table = table;
    }

    public JTable getTable() {
        return table;
    }

}
