package de.offene_pflege.op.settings.subpanels;


import de.offene_pflege.backend.entity.system.SYSPropsTools;
import de.offene_pflege.gui.interfaces.DefaultPanel;
import de.offene_pflege.gui.interfaces.YesNoToggleButton;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class PnlAnonymous extends DefaultPanel {
    public PnlAnonymous() {
        super("opde.settings.anonymous");
        helpkey = "opde.settings.anonymous.helpurl";

        JLabel lbl1 = new JLabel(SYSTools.xx("opde.settings.users.ciphered"));
        lbl1.setFont(SYSConst.ARIAL20);

        JLabel lbl2 = new JLabel(SYSTools.xx("opde.settings.residents.anonymous"));
        lbl2.setFont(SYSConst.ARIAL20);

        JLabel lbl3 = new JLabel(SYSTools.xx("opde.settings.residents.anonymous.relogin"));
        lbl2.setFont(SYSConst.ARIAL20);

        YesNoToggleButton btn1 = new YesNoToggleButton(SYSTools.catchNull(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_USERS_CIPHERED)).equalsIgnoreCase("true"));
        btn1.addItemListener(e -> {
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_USERS_CIPHERED, Boolean.toString(e.getStateChange() == ItemEvent.SELECTED));
            OPDE.saveLocalProps();
        });
        btn1.setFont(SYSConst.ARIAL20);

        YesNoToggleButton btn2 = new YesNoToggleButton(SYSTools.catchNull(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_RESIDENTS_ANONYMIZED)).equalsIgnoreCase("true"));
        btn2.addItemListener(e -> {
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_RESIDENTS_ANONYMIZED, Boolean.toString(e.getStateChange() == ItemEvent.SELECTED));
            OPDE.saveLocalProps();
        });
        btn2.setFont(SYSConst.ARIAL20);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));


        JPanel innerLayout = new JPanel(new GridLayout(3, 2, 5, 5));
        innerLayout.add(lbl1);
        innerLayout.add(btn1);
        innerLayout.add(lbl2);
        innerLayout.add(btn2);
        innerLayout.add(lbl3);

        mainPanel.add(innerLayout);

    }
}
