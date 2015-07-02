package op.settings.subpanels;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.system.SYSPropsTools;
import gui.interfaces.DefaultPanel;
import gui.interfaces.YesNoToggleButton;
import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;

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

}
