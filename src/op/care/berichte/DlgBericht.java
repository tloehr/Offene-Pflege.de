/*
 * Created by JFormDesigner on Mon Apr 23 16:41:35 CEST 2012
 */

package op.care.berichte;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.toedter.calendar.*;

/**
 * @author Torsten Löhr
 */
public class DlgBericht extends JDialog {
    public DlgBericht(Frame owner) {
        super(owner);
        initComponents();
    }

    public DlgBericht(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void jdcDatumPropertyChange(PropertyChangeEvent e) {
        // TODO add your code here
    }

    private void txtUhrzeitFocusLost(FocusEvent e) {
        // TODO add your code here
    }

    private void txtUhrzeitActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void txtDauerFocusGained(FocusEvent e) {
        // TODO add your code here
    }

    private void txtDauerFocusLost(FocusEvent e) {
        // TODO add your code here
    }

    private void txtBerichtCaretUpdate(CaretEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        label1 = new JLabel();
        jdcDatum = new JDateChooser();
        pnlTags = new JScrollPane();
        label2 = new JLabel();
        txtUhrzeit = new JTextField();
        label3 = new JLabel();
        txtDauer = new JTextField();
        scrollPane1 = new JScrollPane();
        txtBericht = new JTextArea();
        panel2 = new JPanel();
        btnCancel = new JButton();
        btnApply = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new CardLayout());

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                "$rgap, $lcgap, default, $lcgap, default:grow, $lcgap, center:pref, $lcgap, $rgap",
                "0dlu, 3*($lgap, default), $lgap, default:grow, $lgap, default, $lgap, $rgap"));

            //---- label1 ----
            label1.setText("Datum");
            panel1.add(label1, CC.xy(3, 3));

            //---- jdcDatum ----
            jdcDatum.setFont(new Font("Lucida Grande", Font.BOLD, 16));
            jdcDatum.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                    jdcDatumPropertyChange(e);
                }
            });
            panel1.add(jdcDatum, CC.xy(5, 3));
            panel1.add(pnlTags, CC.xywh(7, 3, 1, 7));

            //---- label2 ----
            label2.setText("Uhrzeit");
            panel1.add(label2, CC.xy(3, 5));

            //---- txtUhrzeit ----
            txtUhrzeit.setFont(new Font("Lucida Grande", Font.BOLD, 16));
            txtUhrzeit.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtUhrzeitFocusLost(e);
                }
            });
            txtUhrzeit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtUhrzeitActionPerformed(e);
                }
            });
            panel1.add(txtUhrzeit, CC.xy(5, 5));

            //---- label3 ----
            label3.setText("Dauer");
            panel1.add(label3, CC.xy(3, 7));

            //---- txtDauer ----
            txtDauer.setText("3");
            txtDauer.setToolTipText("Dauer in Minuten");
            txtDauer.setFont(new Font("Lucida Grande", Font.BOLD, 16));
            txtDauer.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtDauerFocusGained(e);
                }
                @Override
                public void focusLost(FocusEvent e) {
                    txtDauerFocusLost(e);
                }
            });
            panel1.add(txtDauer, CC.xy(5, 7));

            //======== scrollPane1 ========
            {

                //---- txtBericht ----
                txtBericht.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtBerichtCaretUpdate(e);
                    }
                });
                scrollPane1.setViewportView(txtBericht);
            }
            panel1.add(scrollPane1, CC.xywh(3, 9, 3, 1, CC.FILL, CC.FILL));

            //======== panel2 ========
            {
                panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));

                //---- btnCancel ----
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                panel2.add(btnCancel);

                //---- btnApply ----
                btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                panel2.add(btnApply);
            }
            panel1.add(panel2, CC.xywh(3, 11, 5, 1, CC.RIGHT, CC.FILL));
        }
        contentPane.add(panel1, "card1");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel label1;
    private JDateChooser jdcDatum;
    private JScrollPane pnlTags;
    private JLabel label2;
    private JTextField txtUhrzeit;
    private JLabel label3;
    private JTextField txtDauer;
    private JScrollPane scrollPane1;
    private JTextArea txtBericht;
    private JPanel panel2;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
