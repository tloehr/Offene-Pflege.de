/*
 * Created by JFormDesigner on Mon Jul 09 10:39:27 CEST 2012
 */

package op.care.info;

import java.awt.event.*;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import entity.building.Rooms;
import entity.building.RoomsTools;
import entity.building.Station;
import entity.building.StationTools;
import op.tools.MyJDialog;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.javatuples.Triplet;

import javax.swing.*;
import java.awt.*;
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

        lblTitle.setText(SYSTools.xx("opde.info.dlg.resident.returns.title"));
        cmbStation.setModel(StationTools.getAll4Combobox(false));

        cmbRoom.setModel(SYSTools.list2cmb(RoomsTools.getAllActive()));
        cmbRoom.setSelectedItem(null);

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
        action.execute(new Triplet<Date, Station, Rooms>(jdcHAUF.getDate(), (Station) cmbStation.getSelectedItem(), (Rooms) cmbRoom.getSelectedItem()));
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
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnOk = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default, $lcgap, pref, $lcgap, default:grow, $lcgap, default",
            "default, $pgap, 2*(default, $lgap), default, $pgap, default"));

        //---- lblTitle ----
        lblTitle.setText("text");
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 16));
        contentPane.add(lblTitle, CC.xywh(3, 1, 3, 1));

        //---- lblHAUF ----
        lblHAUF.setText("text");
        lblHAUF.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblHAUF, CC.xy(3, 3));

        //---- jdcHAUF ----
        jdcHAUF.setFont(new Font("Arial", Font.PLAIN, 14));
        jdcHAUF.addPropertyChangeListener("date", e -> jdcDOBPropertyChange(e));
        contentPane.add(jdcHAUF, CC.xy(5, 3));

        //---- lblStation ----
        lblStation.setText("text");
        lblStation.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblStation, CC.xy(3, 5));

        //---- cmbStation ----
        cmbStation.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbStation.addItemListener(e -> cmbStationItemStateChanged(e));
        contentPane.add(cmbStation, CC.xy(5, 5));

        //---- lblRoom ----
        lblRoom.setText("text");
        lblRoom.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblRoom, CC.xy(3, 7));

        //---- cmbRoom ----
        cmbRoom.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbRoom.addItemListener(e -> cmbRoomItemStateChanged(e));
        contentPane.add(cmbRoom, CC.xy(5, 7));

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
        contentPane.add(panel1, CC.xy(5, 9, CC.RIGHT, CC.DEFAULT));
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
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnOk;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
