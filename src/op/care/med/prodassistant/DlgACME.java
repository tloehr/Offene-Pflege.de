/*
 * Created by JFormDesigner on Mon Jun 04 14:35:11 CEST 2012
 */

package op.care.med.prodassistant;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.OverlayTextField;
import com.jidesoft.swing.OverlayableIconsFactory;
import com.jidesoft.swing.OverlayableUtils;
import entity.prescription.MedFactory;
import op.tools.SYSConst;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Torsten Löhr
 */
public class DlgACME extends JPanel {
    private Closure actionBlock;
    private MedFactory factory;
    private JLabel attentionIconFirma, attentionIconOrt;
    private OverlayTextField txtFirma, txtOrt;
    private DefaultOverlayable ovrFirma, ovrOrt;

    public DlgACME(Closure actionBlock) {
        super();
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
    }

    private void initDialog() {
        attentionIconFirma = new JLabel(OverlayableUtils.getPredefinedOverlayIcon(OverlayableIconsFactory.ATTENTION));
        txtFirma = new OverlayTextField(25);
        txtFirma.setFont(SYSConst.ARIAL14);
        ovrFirma = new DefaultOverlayable(txtFirma, attentionIconFirma, DefaultOverlayable.SOUTH_EAST);
        ovrFirma.setOverlayVisible(true);
        contentPanel.add(ovrFirma, CC.xy(3, 1, CC.DEFAULT, CC.FILL));

        txtFirma.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent caretEvent) {
                ovrFirma.setOverlayVisible(txtFirma.getText().trim().isEmpty());
            }
        });

        attentionIconOrt = new JLabel(OverlayableUtils.getPredefinedOverlayIcon(OverlayableIconsFactory.ATTENTION));
        txtOrt = new OverlayTextField(25);
        txtOrt.setFont(SYSConst.ARIAL14);
        ovrOrt = new DefaultOverlayable(txtOrt, attentionIconOrt, DefaultOverlayable.SOUTH_EAST);
        ovrOrt.setOverlayVisible(true);
        contentPanel.add(ovrOrt, CC.xy(3, 7));

        txtOrt.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent caretEvent) {
                ovrOrt.setOverlayVisible(txtOrt.getText().trim().isEmpty());
            }
        });

    }

    private void btnOKActionPerformed(ActionEvent e) {
        if (txtFirma.getText().trim().isEmpty() || txtOrt.getText().trim().isEmpty()) {
            factory = null;
        } else {
            factory = new MedFactory(txtFirma.getText().trim(), txtStrasse.getText().trim(), txtPLZ.getText().trim(), txtOrt.getText().trim(), txtTel.getText().trim(), txtFax.getText().trim(), txtWWW.getText().trim());
        }
        actionBlock.execute(factory);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        txtStrasse = new JTextField();
        label3 = new JLabel();
        txtPLZ = new JTextField();
        label4 = new JLabel();
        label5 = new JLabel();
        txtTel = new JTextField();
        label6 = new JLabel();
        txtFax = new JTextField();
        label7 = new JLabel();
        txtWWW = new JTextField();
        btnOK = new JButton();

        //======== this ========
        setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                    "default, $lcgap, default:grow",
                    "14dlu, 6*($lgap, default), $lgap, $ugap, $lgap, default"));

                //---- label1 ----
                label1.setText("Firma");
                label1.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(label1, CC.xy(1, 1));

                //---- label2 ----
                label2.setText("Strasse");
                label2.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(label2, CC.xy(1, 3));

                //---- txtStrasse ----
                txtStrasse.setFont(new Font("Arial", Font.PLAIN, 14));
                txtStrasse.setColumns(20);
                contentPanel.add(txtStrasse, CC.xy(3, 3));

                //---- label3 ----
                label3.setText("PLZ");
                label3.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(label3, CC.xy(1, 5));

                //---- txtPLZ ----
                txtPLZ.setFont(new Font("Arial", Font.PLAIN, 14));
                txtPLZ.setColumns(20);
                contentPanel.add(txtPLZ, CC.xy(3, 5));

                //---- label4 ----
                label4.setText("Ort");
                label4.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(label4, CC.xy(1, 7));

                //---- label5 ----
                label5.setText("Telefon");
                label5.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(label5, CC.xy(1, 9));

                //---- txtTel ----
                txtTel.setFont(new Font("Arial", Font.PLAIN, 14));
                txtTel.setColumns(20);
                contentPanel.add(txtTel, CC.xy(3, 9));

                //---- label6 ----
                label6.setText("Telefax");
                label6.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(label6, CC.xy(1, 11));

                //---- txtFax ----
                txtFax.setFont(new Font("Arial", Font.PLAIN, 14));
                txtFax.setColumns(20);
                contentPanel.add(txtFax, CC.xy(3, 11));

                //---- label7 ----
                label7.setText("WWW");
                label7.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(label7, CC.xy(1, 13));

                //---- txtWWW ----
                txtWWW.setFont(new Font("Arial", Font.PLAIN, 14));
                txtWWW.setColumns(20);
                contentPanel.add(txtWWW, CC.xy(3, 13));

                //---- btnOK ----
                btnOK.setText(null);
                btnOK.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnOK.setContentAreaFilled(false);
                btnOK.setBorderPainted(false);
                btnOK.setBorder(null);
                btnOK.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnOK.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnOKActionPerformed(e);
                    }
                });
                contentPanel.add(btnOK, CC.xy(3, 17, CC.RIGHT, CC.DEFAULT));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
        }
        add(dialogPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JLabel label2;
    private JTextField txtStrasse;
    private JLabel label3;
    private JTextField txtPLZ;
    private JLabel label4;
    private JLabel label5;
    private JTextField txtTel;
    private JLabel label6;
    private JTextField txtFax;
    private JLabel label7;
    private JTextField txtWWW;
    private JButton btnOK;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
