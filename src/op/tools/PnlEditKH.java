/*
 * Created by JFormDesigner on Mon Jul 09 15:57:43 CEST 2012
 */

package op.tools;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Krankenhaus;
import op.OPDE;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * @author Torsten Löhr
 */
public class PnlEditKH extends JPanel {
    private Krankenhaus kh;

    public PnlEditKH(Krankenhaus kh) {
        this.kh = kh;
        initComponents();
        initPanel();
    }

    private void initPanel(){
        lblKHName.setText(OPDE.lang.getString("misc.msg.name"));
        lblStrasse.setText(OPDE.lang.getString("misc.msg.street"));
        lblPLZ.setText(OPDE.lang.getString("misc.msg.zipcode"));
        lblOrt.setText(OPDE.lang.getString("misc.msg.city"));
        lblTel.setText(OPDE.lang.getString("misc.msg.phone"));
        lblFax.setText(OPDE.lang.getString("misc.msg.fax"));

        txtKHName.setText(kh.getName());
        txtStrasse.setText(kh.getStrasse());
        txtPLZ.setText(kh.getPlz());
        txtOrt.setText(kh.getOrt());
        txtTel.setText(kh.getTel());
        txtFax.setText(kh.getFax());

        FocusAdapter fa = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                ((JTextField) focusEvent.getSource()).selectAll();
            }
        };

        txtKHName.addFocusListener(fa);
        txtStrasse.addFocusListener(fa);
        txtPLZ.addFocusListener(fa);
        txtOrt.addFocusListener(fa);
        txtTel.addFocusListener(fa);
        txtFax.addFocusListener(fa);
    }

    public Krankenhaus getKrankenhaus(){
        if (txtKHName.getText().isEmpty()){
            return null;
        }

        kh.setName(txtKHName.getText().trim());
        kh.setStrasse(txtStrasse.getText().trim());
        kh.setPlz(txtPLZ.getText().trim());
        kh.setOrt(txtOrt.getText().trim());
        kh.setTel(txtTel.getText().trim());
        kh.setFax(txtFax.getText().trim());

        return kh;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblKHName = new JLabel();
        txtKHName = new JTextField();
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
            "default, $lcgap, default:grow",
            "5*(default, $lgap), default"));

        //---- lblKHName ----
        lblKHName.setText("Anrede");
        lblKHName.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblKHName, CC.xy(1, 1));

        //---- txtKHName ----
        txtKHName.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtKHName, CC.xy(3, 1));

        //---- lblStrasse ----
        lblStrasse.setText("text");
        lblStrasse.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblStrasse, CC.xy(1, 3));

        //---- txtStrasse ----
        txtStrasse.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtStrasse, CC.xy(3, 3));

        //---- lblPLZ ----
        lblPLZ.setText("text");
        lblPLZ.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblPLZ, CC.xy(1, 5));

        //---- txtPLZ ----
        txtPLZ.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtPLZ, CC.xy(3, 5));

        //---- lblOrt ----
        lblOrt.setText("text");
        lblOrt.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblOrt, CC.xy(1, 7));

        //---- txtOrt ----
        txtOrt.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtOrt, CC.xy(3, 7));

        //---- lblTel ----
        lblTel.setText("text");
        lblTel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblTel, CC.xy(1, 9));

        //---- txtTel ----
        txtTel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtTel, CC.xy(3, 9));

        //---- lblFax ----
        lblFax.setText("text");
        lblFax.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblFax, CC.xy(1, 11));

        //---- txtFax ----
        txtFax.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtFax, CC.xy(3, 11));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblKHName;
    private JTextField txtKHName;
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
