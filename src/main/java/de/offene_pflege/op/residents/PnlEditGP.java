/*
 * Created by JFormDesigner on Mon Jul 09 15:57:43 CEST 2012
 */

package de.offene_pflege.op.residents;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.entity.prescription.GP;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.tools.PopupPanel;
import de.offene_pflege.op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Torsten Löhr
 */
public class PnlEditGP extends PopupPanel {
    private GP doc;

    public PnlEditGP(GP doc) {
        this.doc = doc;
        initComponents();
        initPanel();

        txtAnrede.requestFocus();
    }

    private void initPanel() {
        lblAnrede.setText(SYSTools.xx("misc.msg.termofaddress"));
        lblTitel.setText(SYSTools.xx("misc.msg.title"));
        lblNachname.setText(SYSTools.xx("misc.msg.surname"));
        lblVorname.setText(SYSTools.xx("misc.msg.firstname"));
        lblStrasse.setText(SYSTools.xx("misc.msg.street"));
        lblPLZ.setText(SYSTools.xx("misc.msg.zipcode"));
        lblOrt.setText(SYSTools.xx("misc.msg.city"));
        lblTel.setText(SYSTools.xx("misc.msg.phone"));
        lblFax.setText(SYSTools.xx("misc.msg.fax"));
        lblMobil.setText(SYSTools.xx("misc.msg.mobilephone"));
        lblEMAIL.setText(SYSTools.xx("misc.msg.email"));

        txtAnrede.setText(doc.getAnrede());
        txtTitel.setText(doc.getTitle());
        txtNachname.setText(doc.getName());
        txtVorname.setText(doc.getFirstname());
        txtStrasse.setText(doc.getStreet());
        txtPLZ.setText(doc.getZIP());
        txtOrt.setText(doc.getCity());
        txtTel.setText(doc.getTel());
        txtFax.setText(doc.getFax());
        txtMobil.setText(SYSTools.catchNull(doc.getMobile()));
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

        setFocusCycleRoot(true);
        setFocusTraversalPolicy(GUITools.createTraversalPolicy(new ArrayList<Component>(Arrays.asList(new Component[]{txtAnrede, txtTitel, txtNachname, txtVorname, txtStrasse, txtPLZ, txtOrt, txtTel, txtFax, txtMobil, txtEMAIL}))));

    }

    private void txtAnredeActionPerformed(ActionEvent e) {
        txtTitel.requestFocus();
    }

    private void txtTitelActionPerformed(ActionEvent e) {
        txtNachname.requestFocus();
    }

    private void txtNachnameActionPerformed(ActionEvent e) {
        txtVorname.requestFocus();
    }

    private void txtVornameActionPerformed(ActionEvent e) {
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
        txtMobil.requestFocus();
    }

    private void txtMobilActionPerformed(ActionEvent e) {
        txtEMAIL.requestFocus();
    }

    private void txtEMAILActionPerformed(ActionEvent e) {
        txtAnrede.requestFocus();
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
                "13dlu, $lcgap, default, $lcgap, 143dlu, $lcgap, 13dlu",
                "13dlu, 11*($lgap, default), $lgap, 13dlu"));

        //---- lblAnrede ----
        lblAnrede.setText("Anrede");
        lblAnrede.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblAnrede, CC.xy(3, 3));

        //---- txtAnrede ----
        txtAnrede.setFont(new Font("Arial", Font.PLAIN, 14));
        txtAnrede.addActionListener(e -> txtAnredeActionPerformed(e));
        add(txtAnrede, CC.xy(5, 3));

        //---- lblTitel ----
        lblTitel.setText("text");
        lblTitel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblTitel, CC.xy(3, 5));

        //---- txtTitel ----
        txtTitel.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTitel.addActionListener(e -> txtTitelActionPerformed(e));
        add(txtTitel, CC.xy(5, 5));

        //---- lblNachname ----
        lblNachname.setText("text");
        lblNachname.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblNachname, CC.xy(3, 7));

        //---- txtNachname ----
        txtNachname.setFont(new Font("Arial", Font.PLAIN, 14));
        txtNachname.addActionListener(e -> txtNachnameActionPerformed(e));
        add(txtNachname, CC.xy(5, 7));

        //---- lblVorname ----
        lblVorname.setText("text");
        lblVorname.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblVorname, CC.xy(3, 9));

        //---- txtVorname ----
        txtVorname.setFont(new Font("Arial", Font.PLAIN, 14));
        txtVorname.addActionListener(e -> txtVornameActionPerformed(e));
        add(txtVorname, CC.xy(5, 9));

        //---- lblStrasse ----
        lblStrasse.setText("text");
        lblStrasse.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblStrasse, CC.xy(3, 11));

        //---- txtStrasse ----
        txtStrasse.setFont(new Font("Arial", Font.PLAIN, 14));
        txtStrasse.addActionListener(e -> txtStrasseActionPerformed(e));
        add(txtStrasse, CC.xy(5, 11));

        //---- lblPLZ ----
        lblPLZ.setText("text");
        lblPLZ.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblPLZ, CC.xy(3, 13));

        //---- txtPLZ ----
        txtPLZ.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPLZ.addActionListener(e -> txtPLZActionPerformed(e));
        add(txtPLZ, CC.xy(5, 13));

        //---- lblOrt ----
        lblOrt.setText("text");
        lblOrt.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblOrt, CC.xy(3, 15));

        //---- txtOrt ----
        txtOrt.setFont(new Font("Arial", Font.PLAIN, 14));
        txtOrt.addActionListener(e -> txtOrtActionPerformed(e));
        add(txtOrt, CC.xy(5, 15));

        //---- lblTel ----
        lblTel.setText("text");
        lblTel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblTel, CC.xy(3, 17));

        //---- txtTel ----
        txtTel.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTel.addActionListener(e -> txtTelActionPerformed(e));
        add(txtTel, CC.xy(5, 17));

        //---- lblFax ----
        lblFax.setText("text");
        lblFax.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblFax, CC.xy(3, 19));

        //---- txtFax ----
        txtFax.setFont(new Font("Arial", Font.PLAIN, 14));
        txtFax.addActionListener(e -> txtFaxActionPerformed(e));
        add(txtFax, CC.xy(5, 19));

        //---- lblMobil ----
        lblMobil.setText("text");
        lblMobil.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblMobil, CC.xy(3, 21));

        //---- txtMobil ----
        txtMobil.setFont(new Font("Arial", Font.PLAIN, 14));
        txtMobil.addActionListener(e -> txtMobilActionPerformed(e));
        add(txtMobil, CC.xy(5, 21));

        //---- lblEMAIL ----
        lblEMAIL.setText("text");
        lblEMAIL.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblEMAIL, CC.xy(3, 23));

        //---- txtEMAIL ----
        txtEMAIL.setFont(new Font("Arial", Font.PLAIN, 14));
        txtEMAIL.addActionListener(e -> txtEMAILActionPerformed(e));
        add(txtEMAIL, CC.xy(5, 23));
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

    @Override
    public Object getResult() {

        if (txtNachname.getText().isEmpty()) {
            return null;
        }

        doc.setAnrede(SYSTools.left(txtAnrede.getText().trim(), 20));
        doc.setTitle(SYSTools.left(txtTitel.getText().trim(), 20));
        doc.setName(txtNachname.getText().trim());
        doc.setFirstname(txtVorname.getText().trim());
        doc.setStreet(txtStrasse.getText().trim());
        doc.setZIP(txtPLZ.getText().trim());
        doc.setOrt(txtOrt.getText().trim());
        doc.setTel(txtTel.getText().trim());
        doc.setFax(txtFax.getText().trim());
        doc.setMobile(txtMobil.getText().trim());
        doc.setEMail(txtEMAIL.getText().trim());

        return doc;
    }

    @Override
    public boolean isSaveOK() {
        return !txtNachname.getText().isEmpty();
    }

    @Override
    public void setStartFocus() {
        txtAnrede.requestFocus();
    }
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
