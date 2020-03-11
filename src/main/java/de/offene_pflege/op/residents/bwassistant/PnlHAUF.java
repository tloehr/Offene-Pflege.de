/*
 * Created by JFormDesigner on Mon Jul 09 10:39:27 CEST 2012
 */

package de.offene_pflege.op.residents.bwassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import de.offene_pflege.entity.building.Rooms;
import de.offene_pflege.entity.building.RoomsTools;
import de.offene_pflege.entity.building.Station;
import de.offene_pflege.entity.building.StationTools;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.javatuples.Quartet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class PnlHAUF extends JPanel {
    public static final String internalClassID = "opde.admin.bw.wizard.page6";
    Closure validate;
    private final Date min;

    public PnlHAUF(Closure validate) {
        this.validate = validate;
        this.min = null;

        initComponents();
        initPanel();
    }

    private void initPanel() {
        lblHAUF.setText(SYSTools.xx("misc.msg.movein"));
        lblStation.setText(SYSTools.xx("misc.msg.subdivision"));
        lblRoom.setText(SYSTools.xx("misc.msg.room"));
        cbKZP.setText(SYSTools.xx("misc.msg.kzp"));


        cmbStation.setModel(StationTools.getAll4Combobox(false));

        cmbRoom.setModel(SYSTools.list2cmb(RoomsTools.getAllActive()));
        cmbRoom.setSelectedItem(null);

        jdcHAUF.setMaxSelectableDate(new Date());
        if (min != null) jdcHAUF.setMinSelectableDate(min);
        jdcHAUF.setDate(new Date());
    }


    private void jdcDOBPropertyChange(PropertyChangeEvent e) {
        check();
    }

    private void check() {
        validate.execute(new Quartet<>(jdcHAUF.getDate(), (Station) cmbStation.getSelectedItem(), (Rooms) cmbRoom.getSelectedItem(), cbKZP.isSelected()));
    }

    private void cmbStationItemStateChanged(ItemEvent e) {
        check();
    }

    private void cmbRoomItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        check();
    }

    private void cbKZPItemStateChanged(ItemEvent e) {
        check();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblHAUF = new JLabel();
        jdcHAUF = new JDateChooser();
        lblStation = new JLabel();
        cmbStation = new JComboBox<>();
        lblRoom = new JLabel();
        cmbRoom = new JComboBox<>();
        cbKZP = new JCheckBox();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, pref, $lcgap, default:grow, $lcgap, default",
            "4*(default, $lgap), default"));

        //---- lblHAUF ----
        lblHAUF.setText("text");
        lblHAUF.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblHAUF, CC.xy(3, 3));

        //---- jdcHAUF ----
        jdcHAUF.setFont(new Font("Arial", Font.PLAIN, 14));
        jdcHAUF.addPropertyChangeListener("date", e -> jdcDOBPropertyChange(e));
        add(jdcHAUF, CC.xy(5, 3));

        //---- lblStation ----
        lblStation.setText("text");
        lblStation.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblStation, CC.xy(3, 5));

        //---- cmbStation ----
        cmbStation.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbStation.addItemListener(e -> cmbStationItemStateChanged(e));
        add(cmbStation, CC.xy(5, 5));

        //---- lblRoom ----
        lblRoom.setText("text");
        lblRoom.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblRoom, CC.xy(3, 7));

        //---- cmbRoom ----
        cmbRoom.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbRoom.addItemListener(e -> cmbRoomItemStateChanged(e));
        add(cmbRoom, CC.xy(5, 7));

        //---- cbKZP ----
        cbKZP.setText("text");
        cbKZP.addItemListener(e -> cbKZPItemStateChanged(e));
        add(cbKZP, CC.xywh(3, 9, 3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblHAUF;
    private JDateChooser jdcHAUF;
    private JLabel lblStation;
    private JComboBox<Station> cmbStation;
    private JLabel lblRoom;
    private JComboBox<Rooms> cmbRoom;
    private JCheckBox cbKZP;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
