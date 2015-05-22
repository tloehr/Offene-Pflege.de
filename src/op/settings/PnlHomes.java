/*
 * Created by JFormDesigner on Sat Feb 23 12:00:49 CET 2013
 */

package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.building.Homes;
import entity.building.Rooms;
import interfaces.DataChangeEvent;
import interfaces.DataChangeListener;
import interfaces.EditPanelDefault;
import op.tools.GUITools;
import op.tools.SYSTools;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class PnlHomes extends EditPanelDefault<Homes> {
    public static final String internalClassID = "opde.settings.pnlhomes";

    private ArrayList<JTextComponent> allTXT;
    private ArrayList<Component> allComponents;

    public PnlHomes(Homes data, DataChangeListener dcl) {
        this.data = data;
        addDataChangeListener(dcl);
        allTXT = new ArrayList<JTextComponent>();
        allComponents = new ArrayList<Component>();
        initComponents();
        initPanel();
    }

    @Override
    public void setStartFocus() {
        txtName.requestFocus();
    }

    private void initPanel() {
        lblName.setText(SYSTools.xx("opde.settings.pnlhomes.lblName"));
        lblStrasse.setText(SYSTools.xx("misc.msg.street"));
        lblPLZ.setText(SYSTools.xx("misc.msg.zipcode"));
        lblOrt.setText(SYSTools.xx("misc.msg.city"));
        lblTel.setText(SYSTools.xx("misc.msg.phone"));
        lblFax.setText(SYSTools.xx("misc.msg.fax"));

        txtName.setText(data.getName());
        txtStrasse.setText(data.getStreet());
        txtPLZ.setText(data.getZIP());
        txtOrt.setText(data.getCity());
        txtFax.setText(data.getFax());
        txtTel.setText(data.getTel());

        allTXT.add(txtName);
        allTXT.add(txtStrasse);
        allTXT.add(txtPLZ);
        allTXT.add(txtOrt);
        allTXT.add(txtTel);
        allTXT.add(txtFax);

        allComponents.add(txtName);
        allComponents.add(txtStrasse);
        allComponents.add(txtPLZ);
        allComponents.add(txtOrt);
        allComponents.add(txtTel);
        allComponents.add(txtFax);


        setFocusCycleRoot(true);
        setFocusTraversalPolicy(GUITools.createTraversalPolicy(allComponents));


        for (JTextComponent txt : allTXT) {
            txt.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    data.setName(txtName.getText().trim());
                    data.setStreet(txtStrasse.getText().trim());
                    data.setZip(txtPLZ.getText().trim());
                    data.setCity(txtOrt.getText().trim());
                    data.setTel(txtTel.getText().trim());
                    data.setFax(txtFax.getText().trim());
                    broadcast(new DataChangeEvent<>(this, data));
                }
            });
        }

    }


    @Override
    public Homes getResult() {

        data.setName(txtName.getText().trim());
        data.setStreet(txtStrasse.getText().trim());
        data.setZip(SYSTools.left(txtPLZ.getText().trim(), 5, ""));
        data.setCity(txtOrt.getText().trim());
        data.setTel(txtTel.getText().trim());
        data.setFax(txtFax.getText().trim());
//        } else {
//            home = null;
//        }
        return data;
    }

    @Override
    public String doValidation() {
        boolean ok = !GUITools.containsEmpty(allTXT);

        if (!ok) {
            return SYSTools.xx("misc.msg.emptyFields");
        }

        return "";
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblName = new JLabel();
        txtName = new JTextField();
        lblStrasse = new JLabel();
        txtStrasse = new JTextField();
        lblPLZ = new JLabel();
        txtPLZ = new JTextField();
        lblOrt = new JLabel();
        txtOrt = new JTextField();
        lblTel = new JLabel();
        txtTel = new JTextField();
        lblFax = new JLabel();
        txtFax = new JTextField();

        //======== this ========
        setLayout(new FormLayout(
                "2*(default, $lcgap), 162dlu:grow, $lcgap, default",
                "7*(default, $lgap), default"));

        //---- lblName ----
        lblName.setText("Anrede");
        lblName.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblName, CC.xy(3, 3));

        //---- txtName ----
        txtName.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtName, CC.xy(5, 3));

        //---- lblStrasse ----
        lblStrasse.setText("text");
        lblStrasse.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblStrasse, CC.xy(3, 5));

        //---- txtStrasse ----
        txtStrasse.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtStrasse, CC.xy(5, 5));

        //---- lblPLZ ----
        lblPLZ.setText("text");
        lblPLZ.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblPLZ, CC.xy(3, 7));

        //---- txtPLZ ----
        txtPLZ.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtPLZ, CC.xy(5, 7));

        //---- lblOrt ----
        lblOrt.setText("text");
        lblOrt.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblOrt, CC.xy(3, 9));

        //---- txtOrt ----
        txtOrt.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtOrt, CC.xy(5, 9));

        //---- lblTel ----
        lblTel.setText("text");
        lblTel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblTel, CC.xy(3, 11));

        //---- txtTel ----
        txtTel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtTel, CC.xy(5, 11));

        //---- lblFax ----
        lblFax.setText("text");
        lblFax.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblFax, CC.xy(3, 13));

        //---- txtFax ----
        txtFax.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtFax, CC.xy(5, 13));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblName;
    private JTextField txtName;
    private JLabel lblStrasse;
    private JTextField txtStrasse;
    private JLabel lblPLZ;
    private JTextField txtPLZ;
    private JLabel lblOrt;
    private JTextField txtOrt;
    private JLabel lblTel;
    private JTextField txtTel;
    private JLabel lblFax;
    private JTextField txtFax;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
