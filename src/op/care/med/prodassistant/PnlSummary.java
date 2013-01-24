/*
 * Created by JFormDesigner on Fri Jun 01 14:44:18 CEST 2012
 */

package op.care.med.prodassistant;

import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Torsten Löhr
 */
public class PnlSummary extends JPanel {
    public PnlSummary() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        textPane1 = new JTextPane();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default",
            "default, $lgap, default:grow, $lgap, default"));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(textPane1);
        }
        add(scrollPane1, CC.xy(3, 3, CC.DEFAULT, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JTextPane textPane1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
