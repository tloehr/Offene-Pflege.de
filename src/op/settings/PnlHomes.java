/*
 * Created by JFormDesigner on Sat Feb 23 12:00:49 CET 2013
 */

package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Homes;
import op.OPDE;
import op.tools.NonEmptyTextfieldVerifier;
import op.tools.PopupPanel;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.UUID;

/**
 * @author Torsten Löhr
 */
public class PnlHomes extends PopupPanel {
    public static final String internalClassID = "opde.settings.pnlhomes";
    private Homes home;

    public PnlHomes(Homes home) {
        this.home = home;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        lblName.setText(OPDE.lang.getString(internalClassID + ".lblName"));
        lblStrasse.setText(OPDE.lang.getString("misc.msg.street"));
        lblPLZ.setText(OPDE.lang.getString("misc.msg.zipcode"));
        lblOrt.setText(OPDE.lang.getString("misc.msg.city"));
        lblTel.setText(OPDE.lang.getString("misc.msg.phone"));
        lblFax.setText(OPDE.lang.getString("misc.msg.fax"));

        txtName.setText(home.getName());
        txtStrasse.setText(home.getStreet());
        txtPLZ.setText(home.getZIP());
        txtOrt.setText(home.getCity());
        txtFax.setText(home.getFax());
        txtTel.setText(home.getTel());


        txtName.setInputVerifier(new NonEmptyTextfieldVerifier());
        txtStrasse.setInputVerifier(new NonEmptyTextfieldVerifier());
        txtPLZ.setInputVerifier(new NonEmptyTextfieldVerifier());
        txtOrt.setInputVerifier(new NonEmptyTextfieldVerifier());
        txtTel.setInputVerifier(new NonEmptyTextfieldVerifier());
        txtFax.setInputVerifier(new NonEmptyTextfieldVerifier());

    }

    @Override
    public Object getResult() {
        home.setName(txtName.getText().trim());
        home.setStreet(txtStrasse.getText().trim());
        home.setZip(txtPLZ.getText().trim());
        home.setCity(txtOrt.getText().trim());
        home.setTel(txtTel.getText().trim());
        home.setFax(txtFax.getText().trim());
        return home;
    }

    private boolean isSaveOK() {
        return false;
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
