/*
 * Created by JFormDesigner on Mon Jul 09 15:57:43 CEST 2012
 */

package op.residents;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.prescription.Hospital;
import op.OPDE;
import op.tools.GUITools;
import op.tools.PopupPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Torsten Löhr
 */
public class PnlEditHospital extends PopupPanel {
    private Hospital hospital;

    public PnlEditHospital(Hospital hospital) {
        this.hospital = hospital;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        lblName.setText(OPDE.lang.getString("misc.msg.name"));
        lblStrasse.setText(OPDE.lang.getString("misc.msg.street"));
        lblPLZ.setText(OPDE.lang.getString("misc.msg.zipcode"));
        lblOrt.setText(OPDE.lang.getString("misc.msg.city"));
        lblTel.setText(OPDE.lang.getString("misc.msg.phone"));
        lblFax.setText(OPDE.lang.getString("misc.msg.fax"));

        txtName.setText(hospital.getName());
        txtStrasse.setText(hospital.getStreet());
        txtPLZ.setText(hospital.getZip());
        txtOrt.setText(hospital.getCity());
        txtTel.setText(hospital.getTel());
        txtFax.setText(hospital.getFax());

        FocusAdapter fa = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                ((JTextField) focusEvent.getSource()).selectAll();
            }
        };

        txtName.addFocusListener(fa);
        txtStrasse.addFocusListener(fa);
        txtPLZ.addFocusListener(fa);
        txtOrt.addFocusListener(fa);
        txtTel.addFocusListener(fa);
        txtFax.addFocusListener(fa);

        setFocusCycleRoot(true);
        setFocusTraversalPolicy(GUITools.createTraversalPolicy(new ArrayList<Component>(Arrays.asList(new Component[]{txtName, txtStrasse, txtPLZ, txtOrt, txtTel, txtFax}))));

    }

    private void txtNachnameActionPerformed(ActionEvent e) {
        txtStrasse.requestFocus();
    }

    private void txtStrasseActionPerformed(ActionEvent e) {
        txtPLZ.requestFocus();
    }

    private void txtPLZActionPerformed(ActionEvent e) {
        txtOrt.requestFocus();
    }

    private void txtOrtActionPerformed(ActionEvent e) {
        txtTel.requestFocus();
    }

    private void txtTelActionPerformed(ActionEvent e) {
        txtFax.requestFocus();
    }

    private void txtFaxActionPerformed(ActionEvent e) {
        txtName.requestFocus();
    }

    @Override
    public Object getResult() {

        if (txtName.getText().isEmpty()) {
            return null;
        }

        hospital.setName(txtName.getText().trim());
        hospital.setStreet(txtStrasse.getText().trim());
        hospital.setZip(txtPLZ.getText().trim());
        hospital.setCity(txtOrt.getText().trim());
        hospital.setTel(txtTel.getText().trim());
        hospital.setFax(txtFax.getText().trim());

        return hospital;
    }

    @Override
    public boolean isSaveOK() {
        return !txtName.getText().isEmpty();
    }

    @Override
    public void setStartFocus() {
        txtName.requestFocus();
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
                "13dlu, $lcgap, default, $lcgap, 143dlu, $lcgap, 13dlu",
                "13dlu, 6*($lgap, default), $lgap, 13dlu"));

        //---- lblName ----
        lblName.setText("text");
        lblName.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblName, CC.xy(3, 3));

        //---- txtName ----
        txtName.setFont(new Font("Arial", Font.PLAIN, 14));
        txtName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtNachnameActionPerformed(e);
            }
        });
        add(txtName, CC.xy(5, 3));

        //---- lblStrasse ----
        lblStrasse.setText("text");
        lblStrasse.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblStrasse, CC.xy(3, 5));

        //---- txtStrasse ----
        txtStrasse.setFont(new Font("Arial", Font.PLAIN, 14));
        txtStrasse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtStrasseActionPerformed(e);
            }
        });
        add(txtStrasse, CC.xy(5, 5));

        //---- lblPLZ ----
        lblPLZ.setText("text");
        lblPLZ.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblPLZ, CC.xy(3, 7));

        //---- txtPLZ ----
        txtPLZ.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPLZ.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtPLZActionPerformed(e);
            }
        });
        add(txtPLZ, CC.xy(5, 7));

        //---- lblOrt ----
        lblOrt.setText("text");
        lblOrt.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblOrt, CC.xy(3, 9));

        //---- txtOrt ----
        txtOrt.setFont(new Font("Arial", Font.PLAIN, 14));
        txtOrt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtOrtActionPerformed(e);
            }
        });
        add(txtOrt, CC.xy(5, 9));

        //---- lblTel ----
        lblTel.setText("text");
        lblTel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblTel, CC.xy(3, 11));

        //---- txtTel ----
        txtTel.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTelActionPerformed(e);
            }
        });
        add(txtTel, CC.xy(5, 11));

        //---- lblFax ----
        lblFax.setText("text");
        lblFax.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblFax, CC.xy(3, 13));

        //---- txtFax ----
        txtFax.setFont(new Font("Arial", Font.PLAIN, 14));
        txtFax.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtFaxActionPerformed(e);
            }
        });
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
