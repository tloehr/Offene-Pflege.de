/*
 * 
 * http://www.jroller.com/santhosh/date/20050610
 * Santhosh Kumar T - santhosh@in.fiorano.com 
 * 
 */
package de.offene_pflege.op.tools;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;

/*
 * 
 * http://www.jroller.com/santhosh/date/20050610
 * Santhosh Kumar T - santhosh@in.fiorano.com 
 * 
 */
public class CheckTreeCellRenderer extends JPanel implements TreeCellRenderer {

    private CheckTreeSelectionModel selectionModel;
    private TreeCellRenderer delegate;
    private TristateCheckBox checkBox = new TristateCheckBox();

    public CheckTreeCellRenderer(TreeCellRenderer delegate, CheckTreeSelectionModel selectionModel) {
        this.delegate = delegate;
        this.selectionModel = selectionModel;
        setLayout(new BorderLayout());
        setOpaque(false);
        checkBox.setOpaque(false);

    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component renderer = delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        TreePath path = tree.getPathForRow(row);
        if (path != null) {
            
            if (selectionModel.isPathSelected(path, true)) {
                checkBox.setState(Boolean.TRUE);
            } else {
                checkBox.setState(selectionModel.isPartiallySelected(path) ? null : Boolean.FALSE);
            }
        }
        removeAll();
        add(checkBox, BorderLayout.WEST);
        add(renderer, BorderLayout.CENTER);
        checkBox.setEnabled(tree.isEnabled());
        return this;
    }
}
