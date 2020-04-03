package de.offene_pflege.op.settings.subpanels;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.backend.entity.system.SYSPropsTools;
import de.offene_pflege.gui.interfaces.DefaultPanel;
import de.offene_pflege.gui.interfaces.YesNoToggleButton;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;

import javax.swing.*;
import java.awt.event.ItemEvent;

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

        mainPanel.setLayout(new FormLayout(
                "default, $lcgap, center:default:grow, $lcgap, default",
                "default:grow, $lgap, default, $lgap, default, $lgap, default:grow"));


        mainPanel.add(btn, CC.xy(3, 3));
        mainPanel.add(lbl, CC.xy(3, 5));

    }


//    private void createCountryList() {
//
//
//
//           String[] countries = new String[]{"germany", "austria", "switzerland"};
//           cmbCountry.setModel(SYSTools.list2cmb(Arrays.asList(countries)));
//           cmbCountry.setRenderer(new ListCellRenderer() {
//               @Override
//               public Component getListCellRendererComponent(JList jList, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//                   String text = SYSTools.xx("country." + value.toString());
//                   return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, index, isSelected, cellHasFocus);
//               }
//           });
//
//           cmbCountry.addItemListener(new ItemListener() {
//               @Override
//               public void itemStateChanged(ItemEvent e) {
//                   if (e.getStateChange() == ItemEvent.SELECTED) {
//                       SYSPropsTools.storeProp(SYSPropsTools.KEY_COUNTRY, e.getItem().toString());
//                   }
//               }
//           });
//
//
//           if (OPDE.getProps().containsKey(SYSPropsTools.KEY_COUNTRY)) {
//               cmbCountry.setSelectedItem(OPDE.getProps().getProperty(SYSPropsTools.KEY_COUNTRY));
//           } else {
//               cmbCountry.setSelectedItem("germany");
//               SYSPropsTools.storeProp(SYSPropsTools.KEY_COUNTRY, "germany");
//           }
//
//       }

}
