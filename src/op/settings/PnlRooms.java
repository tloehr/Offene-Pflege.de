/*
 * Created by JFormDesigner on Sat Feb 23 12:00:49 CET 2013
 */

package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.building.Rooms;
import gui.interfaces.ContentProvider;
import gui.events.DataChangeEvent;
import gui.events.DataChangeListener;
import gui.interfaces.EditPanelDefault;
import op.tools.GUITools;
import op.tools.SYSTools;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class PnlRooms extends EditPanelDefault<Rooms> {
    public static final String internalClassID = "opde.settings.pnlrooms";

    private ArrayList<Component> allComponents;

    JToggleButton btnSingle, btnBath, btnActive;
    ItemListener il;

    public PnlRooms(DataChangeListener dcl, ContentProvider<Rooms> contentProvider) {
        super(dcl, contentProvider);

        allComponents = new ArrayList<Component>();
        initComponents();

        il = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED || e.getStateChange() == ItemEvent.DESELECTED) {
                    data.setActive(btnActive.isSelected());
                    data.setSingle(btnSingle.isSelected());
                    data.setBath(btnBath.isSelected());
                    broadcast(new DataChangeEvent<Rooms>(thisPanel, data));
                }
            }
        };

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new HorizontalLayout(10));

        btnSingle = GUITools.getNiceToggleButton("misc.msg.single.room");
        btnBath = GUITools.getNiceToggleButton("misc.msg.room.bath");
        btnActive = GUITools.getNiceToggleButton("misc.msg.active");

        buttonPanel.add(btnSingle);
        buttonPanel.add(btnBath);
        buttonPanel.add(btnActive);

        add(buttonPanel, CC.xywh(3, 5, 3, 1));

        btnSingle.addItemListener(il);
        btnBath.addItemListener(il);
        btnActive.addItemListener(il);

        initPanel();

        refreshDisplay();
    }


//    @Override
//    public void setDataObject(Rooms room) {
//        super.setDataObject(room);
//        txtText.setText(data.getText());
//        btnSingle.setSelected(data.isSingle());
//        btnBath.setSelected(data.hasBath());
//        btnActive.setSelected(data.isActive());
//    }

    @Override
    public void setStartFocus() {
        txtText.requestFocus();
    }

    private void initPanel() {
        lblText.setText(SYSTools.xx("opde.settings.pnlrooms.name"));

        allComponents.add(txtText);
        allComponents.add(btnSingle);
        allComponents.add(btnBath);
        allComponents.add(btnActive);

        setFocusCycleRoot(true);
        setFocusTraversalPolicy(GUITools.createTraversalPolicy(allComponents));
    }


    @Override
    public String doValidation() {
        return txtText.getText().isEmpty() ? SYSTools.xx("misc.msg.emptyFields") : "";
    }

    private void txtTextFocusLost(FocusEvent e) {
//        data.setText(txtText.getText().trim());
//        broadcast(new DataChangeEvent<Rooms>(this, data, doValidation()));
    }

    private void txtTextActionPerformed(ActionEvent e) {
        txtTextFocusLost(null);
    }

    @Override
    public void refreshDisplay() {
        txtText.setText(data.getText());
        btnSingle.setSelected(data.isSingle());
        btnBath.setSelected(data.hasBath());
        btnActive.setSelected(data.isActive());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblText = new JLabel();
        txtText = new JTextField();

        //======== this ========
        setLayout(new FormLayout(
                "2*(default, $lcgap), 162dlu:grow, $lcgap, default",
                "2*(default, $lgap), default"));

        //---- lblText ----
        lblText.setText("Anrede");
        lblText.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblText, CC.xy(3, 3));

        //---- txtText ----
        txtText.setFont(new Font("Arial", Font.PLAIN, 14));
        txtText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtTextFocusLost(e);
            }
        });
        txtText.addActionListener(e -> txtTextActionPerformed(e));
        add(txtText, CC.xy(5, 3));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblText;
    private JTextField txtText;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
