package gui.interfaces;

import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * Created by tloehr on 23.06.15.
 */
public class YesNoToggleButton extends JPanel {

    public YesNoToggleButton() {
        this("misc.msg.yes", "misc.msg.no", true);
    }

    public YesNoToggleButton(String yes, String no, boolean selectYes) {
        super(new GridLayout(1, 2));

        JToggleButton tbYes = new JToggleButton(SYSTools.xx(yes));
        JToggleButton tbNo = new JToggleButton(SYSTools.xx(no));

        add(tbYes);
        add(tbNo);

        tbYes.setOpaque(true);
        tbNo.setOpaque(true);
        tbNo.setBackground(SYSConst.darkred);
        tbYes.setBackground(SYSConst.darkgreen);
        tbNo.setForeground(Color.YELLOW);
        tbYes.setForeground(Color.YELLOW);

        tbYes.addItemListener(e -> tbNo.setSelected(e.getStateChange() != ItemEvent.SELECTED));
        tbYes.addItemListener(e -> tbYes.setForeground(e.getStateChange() != ItemEvent.SELECTED ? Color.WHITE : Color.BLACK));
        tbNo.addItemListener(e -> tbYes.setSelected(e.getStateChange() != ItemEvent.SELECTED));
        tbNo.addItemListener(e -> tbNo.setForeground(e.getStateChange() != ItemEvent.SELECTED ? Color.WHITE : Color.BLACK));

        tbYes.setSelected(selectYes);
        tbNo.setSelected(!selectYes);

    }
}
