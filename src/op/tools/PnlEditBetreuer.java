/*
 * Created by JFormDesigner on Mon Jul 09 15:57:43 CEST 2012
 */

package op.tools;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Betreuer;
import op.OPDE;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * @author Torsten Löhr
 */
public class PnlEditBetreuer extends JPanel {
    private Betreuer betreuer;

    public PnlEditBetreuer(Betreuer betreuer) {
        this.betreuer = betreuer;
        initComponents();
        initPanel();

        txtAnrede.requestFocus();
    }

    private void initPanel(){
        lblAnrede.setText(OPDE.lang.getString("misc.msg.termofaddress"));
        lblNachname.setText(OPDE.lang.getString("misc.msg.surname"));
        lblVorname.setText(OPDE.lang.getString("misc.msg.firstname"));
        lblStrasse.setText(OPDE.lang.getString("misc.msg.street"));
        lblPLZ.setText(OPDE.lang.getString("misc.msg.zipcode"));
        lblOrt.setText(OPDE.lang.getString("misc.msg.city"));
        lblTel.setText(OPDE.lang.getString("misc.msg.phone"));
        lblPrivate.setText(OPDE.lang.getString("misc.msg.privatephone"));
        lblFax.setText(OPDE.lang.getString("misc.msg.fax"));
        lblMobil.setText(OPDE.lang.getString("misc.msg.mobilephone"));
        lblEMAIL.setText(OPDE.lang.getString("misc.msg.email"));

        txtAnrede.setText(betreuer.getAnrede());
        txtNachname.setText(betreuer.getName());
        txtVorname.setText(betreuer.getVorname());
        txtStrasse.setText(betreuer.getStrasse());
        txtPLZ.setText(betreuer.getPlz());
        txtOrt.setText(betreuer.getOrt());
        txtTel.setText(betreuer.getTel());
        txtPrivate.setText(betreuer.getPrivat());
        txtFax.setText(betreuer.getFax());
        txtMobil.setText(SYSTools.catchNull(betreuer.getOrt()));
        txtEMAIL.setText(SYSTools.catchNull(betreuer.getEMail()));

        FocusAdapter fa = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                ((JTextField) focusEvent.getSource()).selectAll();
            }
        };

        txtAnrede.addFocusListener(fa);
        txtNachname.addFocusListener(fa);
        txtVorname.addFocusListener(fa);
        txtStrasse.addFocusListener(fa);
        txtPLZ.addFocusListener(fa);
        txtOrt.addFocusListener(fa);
        txtTel.addFocusListener(fa);
        txtPrivate.addFocusListener(fa);
        txtFax.addFocusListener(fa);
        txtMobil.addFocusListener(fa);
        txtEMAIL.addFocusListener(fa);
    }

    public Betreuer getBetreuer(){
        if (txtNachname.getText().isEmpty()){
            return null;
        }

        betreuer.setAnrede(txtAnrede.getText().trim());
        betreuer.setName(txtNachname.getText().trim());
        betreuer.setVorname(txtVorname.getText().trim());
        betreuer.setStrasse(txtStrasse.getText().trim());
        betreuer.setPlz(txtPLZ.getText().trim());
        betreuer.setOrt(txtOrt.getText().trim());
        betreuer.setTel(txtTel.getText().trim());
        betreuer.setPrivat(txtPrivate.getText().trim());
        betreuer.setFax(txtFax.getText().trim());
        betreuer.setMobil(txtMobil.getText().trim());
        betreuer.setEMail(txtEMAIL.getText().trim());

        return betreuer;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblAnrede = new JLabel();
        txtAnrede = new JTextField();
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
        lblPrivate = new JLabel();
        txtPrivate = new JTextField();
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

        //---- lblNachname ----
        lblNachname.setText("text");
        lblNachname.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblNachname, CC.xy(1, 3));

        //---- txtNachname ----
        txtNachname.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtNachname, CC.xy(3, 3));

        //---- lblVorname ----
        lblVorname.setText("text");
        lblVorname.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblVorname, CC.xy(1, 5));

        //---- txtVorname ----
        txtVorname.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtVorname, CC.xy(3, 5));

        //---- lblStrasse ----
        lblStrasse.setText("text");
        lblStrasse.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblStrasse, CC.xy(1, 7));

        //---- txtStrasse ----
        txtStrasse.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtStrasse, CC.xy(3, 7));

        //---- lblPLZ ----
        lblPLZ.setText("text");
        lblPLZ.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblPLZ, CC.xy(1, 9));

        //---- txtPLZ ----
        txtPLZ.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtPLZ, CC.xy(3, 9));

        //---- lblOrt ----
        lblOrt.setText("text");
        lblOrt.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblOrt, CC.xy(1, 11));

        //---- txtOrt ----
        txtOrt.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtOrt, CC.xy(3, 11));

        //---- lblTel ----
        lblTel.setText("text");
        lblTel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblTel, CC.xy(1, 13));

        //---- txtTel ----
        txtTel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtTel, CC.xy(3, 13));

        //---- lblPrivate ----
        lblPrivate.setText("text");
        lblPrivate.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblPrivate, CC.xy(1, 15));

        //---- txtPrivate ----
        txtPrivate.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtPrivate, CC.xy(3, 15));

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
    private JLabel lblPrivate;
    private JTextField txtPrivate;
    private JLabel lblFax;
    private JTextField txtFax;
    private JLabel lblMobil;
    private JTextField txtMobil;
    private JLabel lblEMAIL;
    private JTextField txtEMAIL;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
