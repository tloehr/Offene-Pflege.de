package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import op.OPDE;
import op.tools.CleanablePanel;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by tloehr on 29.04.15.
 */
public class PnlTimeout extends CleanablePanel {


    public PnlTimeout() {
        initComponents();
        SpinnerNumberModel snm = new SpinnerNumberModel(OPDE.getTimeout(), 0, 999, 1);
        spinTimeout.setModel(snm);
        ((JSpinner.NumberEditor) spinTimeout.getEditor()).getTextField().setFont(SYSConst.ARIAL28);
        lblTimeout.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.local.timeout.tooltip")));
        spinTimeout.addChangeListener(e -> OPDE.setTimeout((Integer) spinTimeout.getValue()));
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void reload() {

    }

    @Override
    public String getInternalClassID() {
        return null;
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
