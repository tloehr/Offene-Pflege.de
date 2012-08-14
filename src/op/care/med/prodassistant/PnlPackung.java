/*
 * Created by JFormDesigner on Thu May 31 16:24:59 CEST 2012
 */

package op.care.med.prodassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.prescription.TradeForm;
import entity.prescription.MedPackung;
import entity.prescription.MedPackungTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.JXSearchField;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

/**
 * @author Torsten Löhr
 */
public class PnlPackung extends JPanel {
    String pzn;
    BigDecimal inhalt;
    private TradeForm darreichung;
    private MedPackung packung;
    private Closure validate;
    private String template;

    public PnlPackung(Closure validate, String template) {
        pzn = null;
        inhalt = null;
        packung = null;
        this.template = template;
        this.validate = validate;
        initComponents();
        initPanel();
    }

    public void setDarreichung(TradeForm darreichung) {
        this.darreichung = darreichung;
    }

    private void initPanel() {
        cmbGroesse.setModel(new DefaultComboBoxModel(MedPackungTools.GROESSE));
        txtPZN.setText(template);
    }

    public void setLabelEinheit(String text){
        lblEinheit.setText(text);
    }

    private void txtPZNActionPerformed(ActionEvent e) {
        pzn = MedPackungTools.checkNewPZN(txtPZN.getText().trim(), null);

        if (MedPackungTools.parsePZN(txtPZN.getText().trim()) == null) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Die PZN ist falsch. Sie muss aus genau 7 Ziffern bestehen.", DisplayMessage.WARNING));
        } else if (pzn == null){
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Die PZN ist wird bereits verwendet.", DisplayMessage.WARNING));
        }
        check();
    }

    private void txtInhaltCaretUpdate(CaretEvent e) {
        inhalt = SYSTools.parseBigDecimal(txtInhalt.getText());
        if (inhalt == null) {
            if (!txtInhalt.getText().isEmpty()) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Die Inhaltsangabe ist falsch.", DisplayMessage.WARNING));
            }
        } else if (inhalt.compareTo(BigDecimal.ZERO) <= 0) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Die Inhaltsangabe muss größer als 0 sein.", DisplayMessage.WARNING));
        }


        check();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        txtPZN = new JXSearchField();
        cmbGroesse = new JComboBox();
        panel1 = new JPanel();
        label3 = new JLabel();
        txtInhalt = new JTextField();
        lblEinheit = new JLabel();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default",
            "4*(default, $lgap), default"));

        //---- txtPZN ----
        txtPZN.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPZN.setInstantSearchDelay(2000);
        txtPZN.setPrompt("PZN");
        txtPZN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtPZNActionPerformed(e);
            }
        });
        add(txtPZN, CC.xy(3, 3));

        //---- cmbGroesse ----
        cmbGroesse.setFont(new Font("Arial", Font.PLAIN, 14));
        add(cmbGroesse, CC.xy(3, 5));

        //======== panel1 ========
        {
            panel1.setLayout(new HorizontalLayout(10));

            //---- label3 ----
            label3.setText("Inhalt");
            label3.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(label3);

            //---- txtInhalt ----
            txtInhalt.setFont(new Font("Arial", Font.PLAIN, 14));
            txtInhalt.setColumns(10);
            txtInhalt.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    txtInhaltCaretUpdate(e);
                }
            });
            panel1.add(txtInhalt);

            //---- lblEinheit ----
            lblEinheit.setText("g Gel");
            lblEinheit.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblEinheit);
        }
        add(panel1, CC.xy(3, 7, CC.LEFT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void check() {

        OPDE.debug("PZN: " + pzn);
        OPDE.debug("inhalt: " + inhalt);

        if (pzn != null && inhalt != null) {
            packung = new MedPackung(darreichung);
            packung.setPzn(pzn);
            packung.setInhalt(inhalt);
            packung.setGroesse((short) cmbGroesse.getSelectedIndex());
        } else {
            packung = null;
        }
        validate.execute(packung);
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JXSearchField txtPZN;
    private JComboBox cmbGroesse;
    private JPanel panel1;
    private JLabel label3;
    private JTextField txtInhalt;
    private JLabel lblEinheit;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
