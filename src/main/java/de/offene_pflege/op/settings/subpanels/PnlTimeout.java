package de.offene_pflege.op.settings.subpanels;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.gui.interfaces.DefaultPanel;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tloehr on 29.04.15.
 */
public class PnlTimeout extends DefaultPanel {


    public PnlTimeout() {
        super("opde.settings.local.timeout");
        initComponents();
        SpinnerNumberModel snm = new SpinnerNumberModel(OPDE.getTimeout(), 0, 999, 1);
        spinTimeout.setModel(snm);
        ((JSpinner.NumberEditor) spinTimeout.getEditor()).getTextField().setFont(SYSConst.ARIAL28);
        lblTimeout.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.local.timeout.tooltip")));
        spinTimeout.addChangeListener(e -> OPDE.setTimeout((Integer) spinTimeout.getValue()));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        spinTimeout = new JSpinner();
        lblTimeout = new JLabel();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default",
            "default:grow, 2*($lgap, default), $lgap, default:grow"));

        //---- spinTimeout ----
        spinTimeout.setFont(new Font("Arial", Font.PLAIN, 28));
        add(spinTimeout, CC.xy(3, 3));

        //---- lblTimeout ----
        lblTimeout.setText("text");
        lblTimeout.setFont(new Font("Arial", Font.PLAIN, 20));
        add(lblTimeout, CC.xy(3, 5, CC.CENTER, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JSpinner spinTimeout;
    private JLabel lblTimeout;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
