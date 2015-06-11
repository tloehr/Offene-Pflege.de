package gui.interfaces;

import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;

/**
 * Created by tloehr on 11.06.15.
 */
public class DefaultCollapsiblePanes extends CollapsiblePanes {
    public DefaultCollapsiblePanes() {
        super();
        setLayout(new JideBoxLayout(this, JideBoxLayout.Y_AXIS));
    }
}
