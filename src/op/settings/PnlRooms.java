/*
 * Created by JFormDesigner on Sat Feb 23 12:00:49 CET 2013
 */

package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.building.Rooms;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.GUITools;
import op.tools.PopupPanel;
import op.tools.SYSTools;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class PnlRooms extends PopupPanel {
    public static final String internalClassID = "opde.settings.pnlrooms";
    private final Rooms room;

    private ArrayList<JTextComponent> allTXT;
    private ArrayList<Component> allComponents;

    JToggleButton btnSingle, btnBath, btnActive;

    public PnlRooms(Rooms room) {
        this.room = room;
        allTXT = new ArrayList<JTextComponent>();
        allComponents = new ArrayList<Component>();
        initComponents();

        btnSingle = GUITools.getNiceToggleButton("misc.msg.single.room");
        add(btnSingle, CC.xywh(3, 5, 3, 1));

        btnBath = GUITools.getNiceToggleButton("misc.msg.room.bath");
        add(btnBath, CC.xywh(3, 7, 3, 1));

        btnActive = GUITools.getNiceToggleButton("misc.msg.single.room");
        add(btnSingle, CC.xywh(3, 9, 3, 1));

        initPanel();
    }

    @Override
    public void setStartFocus() {
        txtText.requestFocus();
    }

    private void initPanel() {
        lblText.setText(SYSTools.xx("opde.settings.pnlhomes.lblName"));
        lblSingle.setText(SYSTools.xx("misc.msg.single.room"));
        lblBath.setText(SYSTools.xx("misc.msg.room.bath"));

        txtText.setText(room.getText());
        btnSingle.setSelected(room.isSingle());
        btnBath.setSelected(room.hasBath());
        btnActive.setSelected(room.isActive());


        allComponents.add(txtText);
        allComponents.add(btnSingle);
        allComponents.add(btnBath);
        allComponents.add(btnActive);

        setFocusCycleRoot(true);
        setFocusTraversalPolicy(GUITools.createTraversalPolicy(allComponents));
    }

    @Override
    public Object getResult() {

        room.setText(txtText.getText().trim());
        room.setActive(btnActive.isSelected());
        room.setSingle(btnSingle.isSelected());
        room.setBath(btnBath.isSelected());

        return room;
    }

    @Override
    public boolean isSaveOK() {
        boolean ok = !txtText.getText().isEmpty();

        if (!ok) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.emptyFields", DisplayMessage.WARNING));
        }

        return ok;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblText = new JLabel();
        txtText = new JTextField();
        lblSingle = new JLabel();
        lblBath = new JLabel();
        lblActive = new JLabel();

        //======== this ========
        setLayout(new FormLayout(
                "2*(default, $lcgap), 162dlu:grow, $lcgap, default",
                "5*(default, $lgap), default"));

        //---- lblText ----
        lblText.setText("Anrede");
        lblText.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblText, CC.xy(3, 3));

        //---- txtText ----
        txtText.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtText, CC.xy(5, 3));

        //---- lblSingle ----
        lblSingle.setText("text");
        lblSingle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSingle, CC.xywh(3, 5, 3, 1));

        //---- lblBath ----
        lblBath.setText("text");
        lblBath.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblBath, CC.xywh(3, 7, 3, 1));

        //---- lblActive ----
        lblActive.setText("text");
        lblActive.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblActive, CC.xywh(3, 9, 3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblText;
    private JTextField txtText;
    private JLabel lblSingle;
    private JLabel lblBath;
    private JLabel lblActive;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
