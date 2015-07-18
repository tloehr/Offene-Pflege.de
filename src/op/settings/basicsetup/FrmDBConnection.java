/*
 * Created by JFormDesigner on Sat Jul 18 14:06:06 CEST 2015
 */

package op.settings.basicsetup;

import op.OPDE;
import op.settings.databeans.DatabaseConnectionBean;

import javax.swing.*;
import java.awt.*;

/**
 * @author Torsten Löhr
 */
public class FrmDBConnection extends JFrame {
    public FrmDBConnection() {
        initComponents();

        DatabaseConnectionBean dbcb = new DatabaseConnectionBean(OPDE.getLocalProps());



    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
