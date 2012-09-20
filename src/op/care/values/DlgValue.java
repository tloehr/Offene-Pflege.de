/*
 * Created by JFormDesigner on Thu Jun 14 14:23:47 CEST 2012
 */

package op.care.values;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;

/**
 * @author Torsten Löhr
 */
public class DlgValue extends JDialog {
    private BigDecimal wert1, wert2, wert3;
    private String lbl1, lbl1einheit, lbl2, lbl2einheit, lbl3, lbl3einheit;

    public DlgValue(BigDecimal wert, String lbl, String lbleinheit) {
        this(wert, null, null, lbl, lbleinheit, null, null, null, null);
    }

    public DlgValue(BigDecimal wert1, BigDecimal wert2, BigDecimal wert3, String lbl1, String lbl1einheit, String lbl2, String lbl2einheit, String lbl3, String lbl3einheit) {
        this.wert1 = wert1;
        this.wert2 = wert2;
        this.wert3 = wert3;
        this.lbl1 = lbl1;
        this.lbl2 = lbl2;
        this.lbl3 = lbl3;
        this.lbl1einheit = lbl1einheit;
        this.lbl2einheit = lbl2einheit;
        this.lbl3einheit = lbl3einheit;
        initComponents();
        initPanel();
    }

    public void initPanel() {
        setWert1Visible(wert1 != null);
        setWert2Visible(wert2 != null);
        setWert3Visible(wert3 != null);

        if (wert1 != null) {
            lblWert1.setText(lbl1);
            txtWert1.setText(wert1.toPlainString());
            lblWert1Einheit.setText(lbl1einheit);
        }

        if (wert2 != null) {
            lblWert2.setText(lbl2);
            txtWert2.setText(wert2.toPlainString());
            lblWert2Einheit.setText(lbl2einheit);
        }

        if (wert3 != null) {
            lblWert3.setText(lbl3);
            txtWert3.setText(wert3.toPlainString());
            lblWert3Einheit.setText(lbl3einheit);
        }

    }

    public BigDecimal getWert1() {
        return wert1;
    }

    public BigDecimal getWert2() {
        return wert2;
    }

    public BigDecimal getWert3() {
        return wert3;
    }

    private void setWert1Visible(boolean visible) {
        lblWert1.setVisible(visible);
        txtWert1.setVisible(visible);
        lblWert1Einheit.setVisible(visible);
    }

    private void setWert2Visible(boolean visible) {
        lblWert2.setVisible(visible);
        txtWert2.setVisible(visible);
        lblWert2Einheit.setVisible(visible);
    }

    private void setWert3Visible(boolean visible) {
        lblWert3.setVisible(visible);
        txtWert3.setVisible(visible);
        lblWert3Einheit.setVisible(visible);
    }

    private void txtWert1FocusLost(FocusEvent e) {
        wert1 = SYSTools.parseDecimal(((JTextField) e.getSource()).getText());
        if (wert1 != null){
            ((JTextField) e.getSource()).setText(wert1.toPlainString());
        }
    }

    private void txtWert2FocusLost(FocusEvent e) {
        wert2 = SYSTools.parseDecimal(((JTextField) e.getSource()).getText());
        if (wert2 != null){
            ((JTextField) e.getSource()).setText(wert2.toPlainString());
        }
    }

    private void txtWert3FocusLost(FocusEvent e) {
        wert3 = SYSTools.parseDecimal(((JTextField) e.getSource()).getText());
        if (wert3 != null){
            ((JTextField) e.getSource()).setText(wert3.toPlainString());
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblWert1 = new JLabel();
        txtWert1 = new JTextField();
        lblWert1Einheit = new JLabel();
        lblWert2 = new JLabel();
        txtWert2 = new JTextField();
        lblWert2Einheit = new JLabel();
        lblWert3 = new JLabel();
        txtWert3 = new JTextField();
        lblWert3Einheit = new JLabel();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnApply = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$rgap, $lcgap, default, $lcgap, default:grow, $lcgap, default, $lcgap, $rgap",
            "$rgap, 5*($lgap, default)"));

        //---- lblWert1 ----
        lblWert1.setText("text");
        lblWert1.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblWert1, CC.xy(3, 3));

        //---- txtWert1 ----
        txtWert1.setFont(new Font("Arial", Font.PLAIN, 14));
        txtWert1.setColumns(10);
        txtWert1.setHorizontalAlignment(SwingConstants.TRAILING);
        txtWert1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtWert1FocusLost(e);
            }
        });
        contentPane.add(txtWert1, CC.xy(5, 3));

        //---- lblWert1Einheit ----
        lblWert1Einheit.setText("text");
        lblWert1Einheit.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblWert1Einheit, CC.xy(7, 3));

        //---- lblWert2 ----
        lblWert2.setText("text");
        lblWert2.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblWert2, CC.xy(3, 5));

        //---- txtWert2 ----
        txtWert2.setFont(new Font("Arial", Font.PLAIN, 14));
        txtWert2.setColumns(10);
        txtWert2.setHorizontalAlignment(SwingConstants.TRAILING);
        txtWert2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtWert2FocusLost(e);
            }
        });
        contentPane.add(txtWert2, CC.xy(5, 5));

        //---- lblWert2Einheit ----
        lblWert2Einheit.setText("text");
        lblWert2Einheit.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblWert2Einheit, CC.xy(7, 5));

        //---- lblWert3 ----
        lblWert3.setText("text");
        lblWert3.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblWert3, CC.xy(3, 7));

        //---- txtWert3 ----
        txtWert3.setFont(new Font("Arial", Font.PLAIN, 14));
        txtWert3.setColumns(10);
        txtWert3.setHorizontalAlignment(SwingConstants.TRAILING);
        txtWert3.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtWert3FocusLost(e);
            }
        });
        contentPane.add(txtWert3, CC.xy(5, 7));

        //---- lblWert3Einheit ----
        lblWert3Einheit.setText("text");
        lblWert3Einheit.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblWert3Einheit, CC.xy(7, 7));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- btnCancel ----
            btnCancel.setText(null);
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            panel1.add(btnCancel);

            //---- btnApply ----
            btnApply.setText(null);
            btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            panel1.add(btnApply);
        }
        contentPane.add(panel1, CC.xywh(3, 11, 5, 1, CC.RIGHT, CC.DEFAULT));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblWert1;
    private JTextField txtWert1;
    private JLabel lblWert1Einheit;
    private JLabel lblWert2;
    private JTextField txtWert2;
    private JLabel lblWert2Einheit;
    private JLabel lblWert3;
    private JTextField txtWert3;
    private JLabel lblWert3Einheit;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
