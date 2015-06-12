package gui.interfaces;

import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;

/**
 * Created by tloehr on 11.06.15.
 */
public class DefaultCollapsiblePanes extends CollapsiblePanes {

    boolean expansionSet = false;

    @Override
    public void addExpansion() {
        removeExpansion();
        super.addExpansion();
        expansionSet = true;
    }

    public void removeExpansion() {
        if (!expansionSet) return;
        remove(getComponentCount()-1);
        expansionSet = false;
    }

    public DefaultCollapsiblePanes() {
        super();
        setLayout(new JideBoxLayout(this, JideBoxLayout.Y_AXIS));
    }
}
