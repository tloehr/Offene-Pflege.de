package tablerenderer;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 28.06.11
 * Time: 11:05
 * To change this template use File | Settings | File Templates.
 */
public interface TableButtonBehaviour {
    public void actionPerformed(TableButtonActionEvent e);
    public boolean isEnabled(JTable table, int row, int column);
}
