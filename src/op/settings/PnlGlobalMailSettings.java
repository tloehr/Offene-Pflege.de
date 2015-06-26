/*
 * Created by JFormDesigner on Fri Jun 26 16:37:31 CEST 2015
 */

package op.settings;

import gui.PnlBeanEditor;
import gui.interfaces.DefaultPanel;

import javax.swing.*;

/**
 * @author Torsten Löhr
 */
public class PnlGlobalMailSettings extends DefaultPanel {
    public PnlGlobalMailSettings() {
        internalClassID = "opde.settings.global.mail";
        initComponents();

        try {
            MailSettingsBean mailSettingsBean = new MailSettingsBean();
            add(new PnlBeanEditor<>(() -> mailSettingsBean, MailSettingsBean.class, PnlBeanEditor.SAVE_MODE_OK_CANCEL));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }
}
