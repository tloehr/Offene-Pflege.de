/*
 * Created by JFormDesigner on Tue Jun 23 16:08:55 CEST 2015
 */

package gui.interfaces;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Torsten Löhr
 */
public class tb extends JPanel {
    public tb() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        btnYes = new JToggleButton();
        btnNo = new JToggleButton();

        //======== this ========
        setLayout(new FormLayout(
            "default:grow, $lcgap, default:grow",
            "default"));

        //---- btnYes ----
        btnYes.setText("sdkfjhkl\u00f6jfklsdfj");
        add(btnYes, CC.xy(1, 1));

        //---- btnNo ----
        btnNo.setText("text");
        btnNo.setBackground(UIManager.getColor("Button.background"));
        add(btnNo, CC.xy(3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JToggleButton btnYes;
    private JToggleButton btnNo;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
