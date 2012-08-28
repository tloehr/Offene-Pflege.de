/*
 * Created by JFormDesigner on Tue Aug 28 16:03:54 CEST 2012
 */

package op.users;

import java.awt.event.*;
import javax.swing.event.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import op.tools.MyJDialog;

import java.awt.*;
import javax.swing.*;

/**
 * @author Torsten Löhr
 */
public class DlgUser extends MyJDialog {
    public DlgUser() {
        super();
        initComponents();
    }

    private void txtNameCaretUpdate(CaretEvent e) {
        // TODO add your code here
    }

    private void txtNameFocusLost(FocusEvent e) {
        // TODO add your code here
    }

    private void txtEMailFocusLost(FocusEvent e) {
        // TODO add your code here
    }

    private void txtVornameCaretUpdate(CaretEvent e) {
        // TODO add your code here
    }

    private void txtVornameFocusLost(FocusEvent e) {
        // TODO add your code here
    }

    private void txtUKennungFocusLost(FocusEvent e) {

    }

    private void btnCancelActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void btnSaveActionPerformed(ActionEvent e) {
        // TODO add your code here
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel4 = new JPanel();
        jLabel6 = new JLabel();
        txtUKennung = new JTextField();
        lblFirstname = new JLabel();
        txtName = new JTextField();
        txtEMail = new JTextField();
        jLabel5 = new JLabel();
        txtVorname = new JTextField();
        lblName = new JLabel();
        jPanel3 = new JPanel();
        btnCancel = new JButton();
        btnSave = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== jPanel4 ========
        {
            jPanel4.setLayout(new FormLayout(
                "2*(default, $lcgap), default:grow, 2*($lcgap, default)",
                "default, 4*($lgap, fill:default), $lgap, default"));

            //---- jLabel6 ----
            jLabel6.setText("UKennung");
            jLabel6.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(jLabel6, CC.xy(3, 3));

            //---- txtUKennung ----
            txtUKennung.setColumns(10);
            txtUKennung.setDragEnabled(false);
            txtUKennung.setEnabled(false);
            txtUKennung.setFont(new Font("Arial", Font.PLAIN, 14));
            txtUKennung.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtUKennungFocusLost(e);
                }
            });
            jPanel4.add(txtUKennung, CC.xywh(5, 3, 3, 1));

            //---- lblFirstname ----
            lblFirstname.setText("Vorname");
            lblFirstname.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(lblFirstname, CC.xy(3, 5));

            //---- txtName ----
            txtName.setDragEnabled(false);
            txtName.setEnabled(false);
            txtName.setFont(new Font("Arial", Font.PLAIN, 14));
            txtName.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    txtNameCaretUpdate(e);
                }
            });
            txtName.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtNameFocusLost(e);
                }
            });
            jPanel4.add(txtName, CC.xywh(5, 7, 3, 1));

            //---- txtEMail ----
            txtEMail.setDragEnabled(false);
            txtEMail.setEnabled(false);
            txtEMail.setFont(new Font("Arial", Font.PLAIN, 14));
            txtEMail.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtEMailFocusLost(e);
                }
            });
            jPanel4.add(txtEMail, CC.xywh(5, 9, 3, 1));

            //---- jLabel5 ----
            jLabel5.setText("E-Mail");
            jLabel5.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(jLabel5, CC.xy(3, 9));

            //---- txtVorname ----
            txtVorname.setDragEnabled(false);
            txtVorname.setEnabled(false);
            txtVorname.setFont(new Font("Arial", Font.PLAIN, 14));
            txtVorname.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    txtVornameCaretUpdate(e);
                }
            });
            txtVorname.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtVornameFocusLost(e);
                }
            });
            jPanel4.add(txtVorname, CC.xywh(5, 5, 3, 1));

            //---- lblName ----
            lblName.setText("Nachname");
            lblName.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel4.add(lblName, CC.xy(3, 7));

            //======== jPanel3 ========
            {
                jPanel3.setLayout(new BoxLayout(jPanel3, BoxLayout.X_AXIS));

                //---- btnCancel ----
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                btnCancel.setToolTipText("Abbrechen");
                btnCancel.setEnabled(false);
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancelActionPerformed(e);
                    }
                });
                jPanel3.add(btnCancel);

                //---- btnSave ----
                btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnSave.setToolTipText("Sichern");
                btnSave.setEnabled(false);
                btnSave.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnSaveActionPerformed(e);
                    }
                });
                jPanel3.add(btnSave);
            }
            jPanel4.add(jPanel3, CC.xy(1, 11));
        }
        contentPane.add(jPanel4);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel4;
    private JLabel jLabel6;
    private JTextField txtUKennung;
    private JLabel lblFirstname;
    private JTextField txtName;
    private JTextField txtEMail;
    private JLabel jLabel5;
    private JTextField txtVorname;
    private JLabel lblName;
    private JPanel jPanel3;
    private JButton btnCancel;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
