/*
 * Created by JFormDesigner on Thu May 31 16:24:59 CEST 2012
 */

package op.care.med.prodassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.prescription.MedPackage;
import entity.prescription.MedPackageTools;
import entity.prescription.TradeForm;
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
public class PnlPackage extends JPanel {
    public static final String internalClassID = "opde.medication.medproduct.wizard.package";
    String pzn;
    BigDecimal inhalt;
    private TradeForm darreichung;
    private MedPackage aPackage;
    private Closure validate;
    private String template;

    public PnlPackage(Closure validate, String template) {
        pzn = null;
        inhalt = null;
        aPackage = null;
        this.template = template;
        this.validate = validate;
        initComponents();
        initPanel();
    }

    public void setDarreichung(TradeForm darreichung) {
        this.darreichung = darreichung;
    }

    private void initPanel() {
        cmbGroesse.setModel(new DefaultComboBoxModel(MedPackageTools.GROESSE));
        txtPZN.setText(template);
    }

    public void setLabelEinheit(String text) {
        lblUnit.setText(text);
    }

    private void txtPZNActionPerformed(ActionEvent e) {

        try {
            pzn = MedPackageTools.parsePZN(txtPZN.getText().trim());
            if (MedPackageTools.checkNewPZN(pzn, null) == null) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.medication.medproduct.wizard.package.ppn.inuse", DisplayMessage.WARNING));
                pzn = null;
            }
        } catch (NumberFormatException nfe) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(nfe.getMessage(), DisplayMessage.WARNING));
            pzn = null;
        }

        check();
    }

    private void txtInhaltCaretUpdate(CaretEvent e) {
        inhalt = SYSTools.parseDecimal(txtInhalt.getText());
        if (inhalt == null) {
            if (!txtInhalt.getText().isEmpty()) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.medication.medproduct.wizard.package.content.wrong", DisplayMessage.WARNING));
            }
        } else if (inhalt.compareTo(BigDecimal.ZERO) <= 0) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.medication.medproduct.wizard.package.content.greater0", DisplayMessage.WARNING));
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
        lblUnit = new JLabel();
        label1 = new JLabel();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default",
            "4*(default, $lgap), default"));

        //---- txtPZN ----
        txtPZN.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPZN.setInstantSearchDelay(2000);
        txtPZN.setPrompt("PZN");
        txtPZN.addActionListener(e -> txtPZNActionPerformed(e));
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
            txtInhalt.setHorizontalAlignment(SwingConstants.TRAILING);
            txtInhalt.addCaretListener(e -> txtInhaltCaretUpdate(e));
            panel1.add(txtInhalt);

            //---- lblUnit ----
            lblUnit.setText("g Gel");
            lblUnit.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblUnit);
        }
        add(panel1, CC.xy(3, 7, CC.LEFT, CC.DEFAULT));

        //---- label1 ----
        label1.setText(null);
        label1.setIcon(new ImageIcon(getClass().getResource("/artwork/other/medicine3.png")));
        add(label1, CC.xy(3, 9, CC.CENTER, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void check() {

        if (pzn != null && inhalt != null) {
            aPackage = new MedPackage(darreichung);
            aPackage.setPzn(pzn);
            aPackage.setContent(inhalt);
            aPackage.setSize((short) cmbGroesse.getSelectedIndex());
        } else {
            aPackage = null;
        }
        validate.execute(aPackage);
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JXSearchField txtPZN;
    private JComboBox cmbGroesse;
    private JPanel panel1;
    private JLabel label3;
    private JTextField txtInhalt;
    private JLabel lblUnit;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
