package gui.interfaces;

import gui.ColoredToggleButton;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;

/**
 * Created by tloehr on 23.06.15.
 */
public class YesNoToggleButton extends JPanel implements ItemSelectable {
    protected HashSet<ItemListener> itemListenerList = new HashSet<>();
    protected ColoredToggleButton tbYes, tbNo;

    public YesNoToggleButton() {
        this("misc.msg.yes", "misc.msg.no", true);
    }

    public YesNoToggleButton(String yes, String no, boolean selectYes) {
        super(new GridLayout(1, 2));

        tbYes = new ColoredToggleButton(SYSTools.xx(yes), SYSConst.darkgreen, UIManager.getColor("ToggleButton.background"), Color.YELLOW, Color.gray);
        tbNo = new ColoredToggleButton(SYSTools.xx(no), SYSConst.darkred, UIManager.getColor("ToggleButton.background"), Color.YELLOW, Color.gray);

        add(tbYes);
        add(tbNo);

        tbYes.setOpaque(true);
        tbNo.setOpaque(true);

        tbYes.addItemListener(e -> {
            tbNo.setSelected(e.getStateChange() != ItemEvent.SELECTED);
            broadcast(e);
        });
//        tbYes.addItemListener(e -> tbYes.setForeground(e.getStateChange() != ItemEvent.SELECTED ? Color.WHITE : Color.BLACK));
        tbNo.addItemListener(e -> {
            tbYes.setSelected(e.getStateChange() != ItemEvent.SELECTED);
        });
//        tbNo.addItemListener(e -> tbNo.setForeground(e.getStateChange() != ItemEvent.SELECTED ? Color.WHITE : Color.BLACK));

        tbYes.setSelected(selectYes);
        tbNo.setSelected(!selectYes);

    }


    @Override
    public Object[] getSelectedObjects() {
        return tbYes.isSelected() ? new Integer[]{JOptionPane.YES_OPTION} : new Integer[]{JOptionPane.NO_OPTION};
    }

    @Override
    public void addItemListener(ItemListener l) {
        itemListenerList.add(l);
    }

    @Override
    public void removeItemListener(ItemListener l) {
        itemListenerList.remove(l);
    }

    void broadcast(final ItemEvent e) {
        CollectionUtils.forAllDo(itemListenerList, o -> ((ItemListener) o).itemStateChanged(e));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        SYSTools.setXEnabled(this, enabled);
    }
}
