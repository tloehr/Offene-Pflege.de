package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.building.Station;
import entity.building.StationTools;
import entity.system.SYSPropsTools;
import gui.interfaces.DefaultPanel;
import op.OPDE;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tloehr on 29.04.15.
 */
public class PnlStation extends DefaultPanel {

    public PnlStation() {
        internalClassID = "opde.settings.default.station";
        initComponents();
        cmbStation.setModel(StationTools.getAll4Combobox(false));
        cmbStation.setSelectedItem(StationTools.getStationForThisHost());

        cmbStation.addItemListener(e -> {
            if (cmbStation.getSelectedItem() == null) return;
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_STATION, ((Station) cmbStation.getSelectedItem()).getStatID().toString());
            OPDE.saveLocalProps();
        });

    }

    @Override
    public void cleanup() {
    }

    @Override
    public void reload() {
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        cmbStation = new JComboBox();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default",
            "default:grow, $lgap, default, $lgap, default:grow"));

        //---- cmbStation ----
        cmbStation.setFont(new Font("Arial", Font.PLAIN, 28));
        add(cmbStation, CC.xy(3, 3));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JComboBox cmbStation;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
