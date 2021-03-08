/*
 * Created by JFormDesigner on Mon Jul 09 10:39:27 CEST 2012
 */

package de.offene_pflege.op.care.info;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import de.offene_pflege.entity.building.Rooms;
import de.offene_pflege.entity.building.Station;
import de.offene_pflege.services.RoomsService;
import de.offene_pflege.services.StationService;
import de.offene_pflege.op.tools.MyJDialog;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class DlgResidentReturns extends MyJDialog {
    public static final String internalClassID = "opde.info.dlg.resident.returns";
    Closure action;
    private final Date min;


    public DlgResidentReturns(Date min, Closure action) {
        this.action = action;
        this.min = min;

        initComponents();
        initPanel();
        pack();
    }

    private void initPanel() {
        lblHAUF.setText(SYSTools.xx("misc.msg.movein"));
        lblStation.setText(SYSTools.xx("misc.msg.subdivision"));
        lblRoom.setText(SYSTools.xx("misc.msg.room"));
        cbKZP.setText(SYSTools.xx("misc.msg.kzp"));

        lblTitle.setText(SYSTools.xx("opde.info.dlg.resident.returns.title"));
        cmbStation.setModel(StationService.getAll4Combobox(false));

        cmbRoom.setModel(SYSTools.list2cmb(RoomsService.getAllActive()));
        cmbRoom.setSelectedItem(null);
        cmbRoom.setRenderer(RoomsService.getRenderer());

        jdcHAUF.setMaxSelectableDate(new Date());
        if (min != null) jdcHAUF.setMinSelectableDate(min);
        jdcHAUF.setDate(new Date());
    }


    private void jdcDOBPropertyChange(PropertyChangeEvent e) {

    }


    private void cmbStationItemStateChanged(ItemEvent e) {

    }

    private void cmbRoomItemStateChanged(ItemEvent e) {

    }

    private void btnCancelActionPerformed(ActionEvent e) {
        dispose();
    }

    private void btnOkActionPerformed(ActionEvent e) {
        if (jdcHAUF.getDate() == null){
            jdcHAUF.setDate(new Date());
        }
        action.execute(new Quartet<>(jdcHAUF.getDate(), (Station) cmbStation.getSelectedItem(), (Rooms) cmbRoom.getSelectedItem(), cbKZP.isSelected()));
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblTitle = new JLabel();
        lblHAUF = new JLabel();
        jdcHAUF = new JDateChooser();
        lblStation = new JLabel();
        cmbStation = new JComboBox<>();
        lblRoom = new JLabel();
        cmbRoom = new JComboBox<>();
        cbKZP = new JCheckBox();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnOk = new JButton();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$ugap, $lcgap, pref, $lcgap, default:grow, $lcgap, $ugap",
            "$ugap, $lgap, default, $pgap, 3*(default, $lgap), default, $pgap, default, $lgap, $ugap"));

        //---- lblTitle ----
        lblTitle.setText("text");
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 16));
        contentPane.add(lblTitle, CC.xywh(3, 3, 3, 1));

        //---- lblHAUF ----
        lblHAUF.setText("text");
        lblHAUF.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblHAUF, CC.xy(3, 5));

        //---- jdcHAUF ----
        jdcHAUF.setFont(new Font("Arial", Font.PLAIN, 14));
        jdcHAUF.addPropertyChangeListener("date", e -> jdcDOBPropertyChange(e));
        contentPane.add(jdcHAUF, CC.xy(5, 5));

        //---- lblStation ----
        lblStation.setText("text");
        lblStation.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblStation, CC.xy(3, 7));

        //---- cmbStation ----
        cmbStation.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbStation.addItemListener(e -> cmbStationItemStateChanged(e));
        contentPane.add(cmbStation, CC.xy(5, 7));

        //---- lblRoom ----
        lblRoom.setText("text");
        lblRoom.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblRoom, CC.xy(3, 9));

        //---- cmbRoom ----
        cmbRoom.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbRoom.addItemListener(e -> cmbRoomItemStateChanged(e));
        contentPane.add(cmbRoom, CC.xy(5, 9));

        //---- cbKZP ----
        cbKZP.setText("text");
        contentPane.add(cbKZP, CC.xywh(3, 11, 3, 1));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- btnCancel ----
            btnCancel.setText(null);
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnCancel.addActionListener(e -> btnCancelActionPerformed(e));
            panel1.add(btnCancel);

            //---- btnOk ----
            btnOk.setText(null);
            btnOk.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnOk.addActionListener(e -> btnOkActionPerformed(e));
            panel1.add(btnOk);
        }
        contentPane.add(panel1, CC.xy(5, 13, CC.RIGHT, CC.DEFAULT));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblTitle;
    private JLabel lblHAUF;
    private JDateChooser jdcHAUF;
    private JLabel lblStation;
    private JComboBox<Station> cmbStation;
    private JLabel lblRoom;
    private JComboBox<Rooms> cmbRoom;
    private JCheckBox cbKZP;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnOk;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
