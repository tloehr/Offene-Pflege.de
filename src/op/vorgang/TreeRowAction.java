package op.vorgang;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 21.06.11
 * Time: 14:12
 * To change this template use File | Settings | File Templates.
 */
public abstract class TreeRowAction extends AbstractAction {
    int col, row;
    JTable table;

    public TreeRowAction(int col, int row, JTable table) {
        this.col = col;
        this.row = row;
        this.table = table;
    }

}
