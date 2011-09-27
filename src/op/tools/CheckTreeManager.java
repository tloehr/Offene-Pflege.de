/*
 * 
 * http://www.jroller.com/santhosh/date/20050610
 * Santhosh Kumar T - santhosh@in.fiorano.com
 * Mit kleiner Erweiterung von mir. (rootClickable)
 * @SKW05
 * 
 */
package op.tools;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

public class CheckTreeManager extends MouseAdapter implements TreeSelectionListener {

    private CheckTreeSelectionModel selectionModel;
    private JTree tree = new JTree();
    int hotspot = new JCheckBox().getPreferredSize().width;
    private boolean rootClickable;

    public CheckTreeManager(JTree tree) {
        this(tree, true);
    }

    public CheckTreeManager(JTree tree, boolean rootClickable) {
        this.tree = tree;
        tree.setCellRenderer(new DefaultTreeCellRenderer()); // Erweiterung. Damit sich die Renderer bei mehrfachem Aufruf nicht aufaddieren.
        selectionModel = new CheckTreeSelectionModel(tree.getModel());
        tree.setCellRenderer(new CheckTreeCellRenderer(tree.getCellRenderer(), selectionModel));
        tree.addMouseListener(this);
        selectionModel.addTreeSelectionListener(this);
        this.rootClickable = rootClickable;
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        TreePath path = tree.getPathForLocation(me.getX(), me.getY());
        // Hier hab ich ein bisschen erweitert. Falls man nicht auf den root
        // Knoten clicken soll.
        if (path == null || !this.tree.isEnabled() || (!rootClickable && path.getPathCount() == 1)) {
            return;
        }
        if (me.getX() > tree.getPathBounds(path).x + hotspot) {
            return;
        }

        boolean selected = selectionModel.isPathSelected(path, true);
        selectionModel.removeTreeSelectionListener(this);

        try {
            if (selected) {
                selectionModel.removeSelectionPath(path);
            } else {
                selectionModel.addSelectionPath(path);
            }
        } finally {
            selectionModel.addTreeSelectionListener(this);
            tree.treeDidChange();
        }
    }

    public CheckTreeSelectionModel getSelectionModel() {
        return selectionModel;
    }

    public void valueChanged(TreeSelectionEvent e) {
        tree.treeDidChange();
    }
}
