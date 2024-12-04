package de.offene_pflege.op.settings.subpanels;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.gui.interfaces.DefaultPanel;
import de.offene_pflege.gui.interfaces.YesNoToggleButton;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.RiverLayout;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Created by tloehr on 30.06.15.
 */
public class PnlMedication extends DefaultPanel {

    public PnlMedication() {
        super("opde.settings.medication");
        helpkey = "opde.settings.medication.helpurl";
        JLabel lbl = new JLabel(SYSTools.xx("opde.settings.medication.calcEnabled"));
        lbl.setFont(SYSConst.ARIAL20);

        YesNoToggleButton btn = new YesNoToggleButton(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_CALC_MEDI_UPR1)).equalsIgnoreCase("true"));
        btn.addItemListener(e -> {
            SYSPropsTools.storeProp(SYSPropsTools.KEY_CALC_MEDI_UPR1, Boolean.toString(e.getStateChange() == ItemEvent.SELECTED));
        });
        btn.setFont(SYSConst.ARIAL20);

        JComboBox<DayOfWeek> cmb_start_day = new JComboBox<>(DayOfWeek.values());
        cmb_start_day.setFont(SYSConst.ARIAL20);
        cmb_start_day.setSelectedIndex(Math.max(SYSPropsTools.getInteger(SYSPropsTools.KEY_CALC_MEDI_START_ORDER_WEEK, DayOfWeek.MONDAY.getValue()) - 1, 0));
        cmb_start_day.addItemListener(e -> SYSPropsTools.storeInteger(SYSPropsTools.KEY_CALC_MEDI_START_ORDER_WEEK, cmb_start_day.getSelectedIndex() + 1));
        cmb_start_day.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
            return dlcr.getListCellRendererComponent(list, value.getDisplayName(TextStyle.FULL, Locale.getDefault()), index, isSelected, cellHasFocus);
        });

        /**
         * br - Add a line break
         * p - Add a paragraph break
         * tab - Add a tab stop (handy for constructing forms with labels followed by fields)
         * hfill - Extend component horizontally
         * vfill - Extent component vertically (currently only one allowed)
         * left - Align following components to the left (default)
         * center - Align following components horizontally centered
         * right - Align following components to the right
         * vtop - Align following components vertically top aligned
         * vcenter - Align following components vertically centered (default)
         */
        mainPanel.setLayout(new RiverLayout());

        mainPanel.add("p left", lbl);
        mainPanel.add("tab", btn);

        JLabel lbl2 = new JLabel("Bestell Woche beginnt: ");
        lbl2.setFont(SYSConst.ARIAL20);

        mainPanel.add("p left", lbl2);
        mainPanel.add("tab", cmb_start_day);

    }



}
