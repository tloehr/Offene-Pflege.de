package de.offene_pflege.op.settings.subpanels;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.backend.entity.done.Station;
import de.offene_pflege.backend.services.StationService;
import de.offene_pflege.backend.entity.system.SYSPropsTools;
import de.offene_pflege.gui.interfaces.DefaultPanel;
import de.offene_pflege.op.OPDE;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tloehr on 29.04.15.
 */
public class PnlStation extends DefaultPanel {

    public PnlStation() {
        super("opde.settings.default.station");
        initComponents();
        cmbStation.setModel(StationService.getAll4Combobox(false));
        cmbStation.setSelectedItem(StationService.getStationForThisHost());

        cmbStation.addItemListener(e -> {
            if (cmbStation.getSelectedItem() == null) return;
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_STATION, ((Station) cmbStation.getSelectedItem()).getId().toString());
            OPDE.saveLocalProps();
        });

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
