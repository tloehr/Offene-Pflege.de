/*
 * Created by JFormDesigner on Mon Jul 09 15:57:43 CEST 2012
 */

package op.tools;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import entity.prescription.Doc;
import op.OPDE;

/**
 * @author Torsten Löhr
 */
public class PnlEditArzt extends JPanel {
    private Doc doc;

    public PnlEditArzt(Doc doc) {
        this.doc = doc;
        initComponents();
        initPanel();

        txtAnrede.requestFocus();
    }

    private void initPanel(){
        lblAnrede.setText(OPDE.lang.getString("misc.msg.termofaddress"));
        lblTitel.setText(OPDE.lang.getString("misc.msg.title"));
        lblNachname.setText(OPDE.lang.getString("misc.msg.surname"));
        lblVorname.setText(OPDE.lang.getString("misc.msg.firstname"));
        lblStrasse.setText(OPDE.lang.getString("misc.msg.street"));
        lblPLZ.setText(OPDE.lang.getString("misc.msg.zipcode"));
        lblOrt.setText(OPDE.lang.getString("misc.msg.city"));
        lblTel.setText(OPDE.lang.getString("misc.msg.phone"));
        lblFax.setText(OPDE.lang.getString("misc.msg.fax"));
        lblMobil.setText(OPDE.lang.getString("misc.msg.mobilephone"));
        lblEMAIL.setText(OPDE.lang.getString("misc.msg.email"));

        txtAnrede.setText(doc.getAnrede());
        txtTitel.setText(doc.getTitel());
        txtNachname.setText(doc.getName());
        txtVorname.setText(doc.getVorname());
        txtStrasse.setText(doc.getStrasse());
        txtPLZ.setText(doc.getPlz());
        txtOrt.setText(doc.getOrt());
        txtTel.setText(doc.getTel());
        txtFax.setText(doc.getFax());
        txtMobil.setText(SYSTools.catchNull(doc.getOrt()));
        txtEMAIL.setText(SYSTools.catchNull(doc.getEMail()));

        FocusAdapter fa = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                ((JTextField) focusEvent.getSource()).selectAll();
            }
        };

        txtAnrede.addFocusListener(fa);
        txtTitel.addFocusListener(fa);
        txtNachname.addFocusListener(fa);
        txtVorname.addFocusListener(fa);
        txtStrasse.addFocusListener(fa);
        txtPLZ.addFocusListener(fa);
        txtOrt.addFocusListener(fa);
        txtTel.addFocusListener(fa);
        txtFax.addFocusListener(fa);
        txtMobil.addFocusListener(fa);
        txtEMAIL.addFocusListener(fa);

    }

    public Doc getDoc(){
        if (txtNachname.getText().isEmpty()){
            return null;
        }

        doc.setAnrede(txtAnrede.getText().trim());
        doc.setTitel(txtTitel.getText().trim());
        doc.setName(txtNachname.getText().trim());
        doc.setVorname(txtVorname.getText().trim());
        doc.setStrasse(txtStrasse.getText().trim());
        doc.setPlz(txtPLZ.getText().trim());
        doc.setOrt(txtOrt.getText().trim());
        doc.setTel(txtTel.getText().trim());
        doc.setFax(txtFax.getText().trim());
        doc.setMobil(txtMobil.getText().trim());
        doc.setEMail(txtEMAIL.getText().trim());

        return doc;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblAnrede = new JLabel();
        txtAnrede = new JTextField();
        lblTitel = new JLabel();
        txtTitel = new JTextField();
        lblNachname = new JLabel();
        txtNachname = new JTextField();
        lblVorname = new JLabel();
        txtVorname = new JTextField();
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
        lblMobil = new JLabel();
        txtMobil = new JTextField();
        lblEMAIL = new JLabel();
        txtEMAIL = new JTextField();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow",
            "10*(default, $lgap), default"));

        //---- lblAnrede ----
        lblAnrede.setText("Anrede");
        lblAnrede.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblAnrede, CC.xy(1, 1));

        //---- txtAnrede ----
        txtAnrede.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtAnrede, CC.xy(3, 1));

        //---- lblTitel ----
        lblTitel.setText("text");
        lblTitel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblTitel, CC.xy(1, 3));

        //---- txtTitel ----
        txtTitel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtTitel, CC.xy(3, 3));

        //---- lblNachname ----
        lblNachname.setText("text");
        lblNachname.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblNachname, CC.xy(1, 5));

        //---- txtNachname ----
        txtNachname.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtNachname, CC.xy(3, 5));

        //---- lblVorname ----
        lblVorname.setText("text");
        lblVorname.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblVorname, CC.xy(1, 7));

        //---- txtVorname ----
        txtVorname.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtVorname, CC.xy(3, 7));

        //---- lblStrasse ----
        lblStrasse.setText("text");
        lblStrasse.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblStrasse, CC.xy(1, 9));

        //---- txtStrasse ----
        txtStrasse.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtStrasse, CC.xy(3, 9));

        //---- lblPLZ ----
        lblPLZ.setText("text");
        lblPLZ.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblPLZ, CC.xy(1, 11));

        //---- txtPLZ ----
        txtPLZ.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtPLZ, CC.xy(3, 11));

        //---- lblOrt ----
        lblOrt.setText("text");
        lblOrt.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblOrt, CC.xy(1, 13));

        //---- txtOrt ----
        txtOrt.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtOrt, CC.xy(3, 13));

        //---- lblTel ----
        lblTel.setText("text");
        lblTel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblTel, CC.xy(1, 15));

        //---- txtTel ----
        txtTel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtTel, CC.xy(3, 15));

        //---- lblFax ----
        lblFax.setText("text");
        lblFax.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblFax, CC.xy(1, 17));

        //---- txtFax ----
        txtFax.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtFax, CC.xy(3, 17));

        //---- lblMobil ----
        lblMobil.setText("text");
        lblMobil.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblMobil, CC.xy(1, 19));

        //---- txtMobil ----
        txtMobil.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtMobil, CC.xy(3, 19));

        //---- lblEMAIL ----
        lblEMAIL.setText("text");
        lblEMAIL.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblEMAIL, CC.xy(1, 21));

        //---- txtEMAIL ----
        txtEMAIL.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtEMAIL, CC.xy(3, 21));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblAnrede;
    private JTextField txtAnrede;
    private JLabel lblTitel;
    private JTextField txtTitel;
    private JLabel lblNachname;
    private JTextField txtNachname;
    private JLabel lblVorname;
    private JTextField txtVorname;
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
    private JLabel lblMobil;
    private JTextField txtMobil;
    private JLabel lblEMAIL;
    private JTextField txtEMAIL;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
