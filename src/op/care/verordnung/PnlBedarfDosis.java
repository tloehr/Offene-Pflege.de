/*
 * Created by JFormDesigner on Tue May 01 14:14:53 CEST 2012
 */

package op.care.verordnung;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swingx.border.*;

/**
 * @author Torsten Löhr
 */
public class PnlBedarfDosis extends JPanel {
    public PnlBedarfDosis() {
        initComponents();
    }

    private void txtMaxTimesCaretUpdate(CaretEvent e) {
        // TODO add your code here
    }

    private void txtMaxTimesActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void txtMaxTimesFocusGained(FocusEvent e) {
        // TODO add your code here
    }

    private void txtMaxTimesFocusLost(FocusEvent e) {
        // TODO add your code here
    }

    private void txtEDosisCaretUpdate(CaretEvent e) {
        // TODO add your code here
    }

    private void txtEDosisActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void txtEDosisFocusGained(FocusEvent e) {
        // TODO add your code here
    }

    private void txtEDosisFocusLost(FocusEvent e) {
        // TODO add your code here
    }

    private void btnSaveActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel2 = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        lblDosis = new JLabel();
        txtMaxTimes = new JTextField();
        lblX = new JLabel();
        txtEDosis = new JTextField();
        btnSave = new JButton();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new DropShadowBorder(Color.black, 5, 0.5f, 12, true, true, true, true));
            jPanel2.setLayout(new FormLayout(
                "default, $lcgap, pref, $lcgap, default, $lcgap, 37dlu, $lcgap, 52dlu",
                "default, fill:default"));

            //---- label1 ----
            label1.setText("Anzahl");
            jPanel2.add(label1, CC.xy(3, 1));

            //---- label2 ----
            label2.setText("Dosis");
            jPanel2.add(label2, CC.xy(7, 1, CC.CENTER, CC.DEFAULT));

            //---- lblDosis ----
            lblDosis.setText("Max. Tagesdosis:");
            jPanel2.add(lblDosis, CC.xy(1, 2));

            //---- txtMaxTimes ----
            txtMaxTimes.setHorizontalAlignment(SwingConstants.RIGHT);
            txtMaxTimes.setText("1");
            txtMaxTimes.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    txtMaxTimesCaretUpdate(e);
                }
            });
            txtMaxTimes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtMaxTimesActionPerformed(e);
                }
            });
            txtMaxTimes.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtMaxTimesFocusGained(e);
                }
                @Override
                public void focusLost(FocusEvent e) {
                    txtMaxTimesFocusLost(e);
                }
            });
            jPanel2.add(txtMaxTimes, CC.xy(3, 2));

            //---- lblX ----
            lblX.setText("x");
            jPanel2.add(lblX, CC.xy(5, 2));

            //---- txtEDosis ----
            txtEDosis.setHorizontalAlignment(SwingConstants.RIGHT);
            txtEDosis.setText("1.0");
            txtEDosis.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    txtEDosisCaretUpdate(e);
                }
            });
            txtEDosis.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtEDosisActionPerformed(e);
                }
            });
            txtEDosis.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtEDosisFocusGained(e);
                }
                @Override
                public void focusLost(FocusEvent e) {
                    txtEDosisFocusLost(e);
                }
            });
            jPanel2.add(txtEDosis, CC.xy(7, 2));

            //---- btnSave ----
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });
            jPanel2.add(btnSave, CC.xy(9, 2, CC.RIGHT, CC.DEFAULT));
        }
        add(jPanel2);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel2;
    private JLabel label1;
    private JLabel label2;
    private JLabel lblDosis;
    private JTextField txtMaxTimes;
    private JLabel lblX;
    private JTextField txtEDosis;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
