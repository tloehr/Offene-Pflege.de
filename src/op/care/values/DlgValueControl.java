/*
 * Created by JFormDesigner on Sat Oct 20 14:22:06 CEST 2012
 */

package op.care.values;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.info.Resident;
import op.OPDE;
import op.tools.GUITools;
import op.tools.MyJDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Properties;

/**
 * @author Torsten Löhr
 */
public class DlgValueControl extends MyJDialog {
    private JToggleButton tbStool, tbBalance, tbLowIn, tbHighIn;
    private Resident resident;
    Properties props = new Properties();

    public DlgValueControl(Resident resident) {
        super();
        this.resident = resident;
        initComponents();
        initDialog();
        setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();    //To change body of overridden methods use File | Settings | File Templates.
    }

    private void initDialog() {
        tbStool = GUITools.getNiceToggleButton(OPDE.lang.getString(PnlValues.internalClassID + ".DlgValueControl.tbStool.tooltip"));
        tbStool.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                txtStoolDays.setText(itemEvent.getStateChange() == ItemEvent.SELECTED ? "1" : "");
                txtStoolDays.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
            }
        });
        tbStool.setHorizontalAlignment(SwingConstants.LEFT);
        panel1.add(tbStool, CC.xy(3, 3));

        tbBalance = GUITools.getNiceToggleButton(OPDE.lang.getString(PnlValues.internalClassID + ".DlgValueControl.tbBalance.tooltip"));
        tbBalance.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                setTxtDaysDrink();
            }
        });
        tbBalance.setHorizontalAlignment(SwingConstants.LEFT);
        panel1.add(tbBalance, CC.xy(3, 11));

        tbLowIn = GUITools.getNiceToggleButton(OPDE.lang.getString(PnlValues.internalClassID + ".DlgValueControl.tbLowIn.tooltip"));
        tbLowIn.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                setTxtDaysDrink();
                txtLowIn.setText(itemEvent.getStateChange() == ItemEvent.SELECTED ? "1000" : "");
                txtLowIn.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
            }
        });
        tbLowIn.setHorizontalAlignment(SwingConstants.LEFT);
        panel1.add(tbLowIn, CC.xy(3, 13));

        tbHighIn = GUITools.getNiceToggleButton(OPDE.lang.getString(PnlValues.internalClassID + ".DlgValueControl.tbHighIn.tooltip"));
        tbHighIn.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                setTxtDaysDrink();
                txtHighIn.setText(itemEvent.getStateChange() == ItemEvent.SELECTED ? "1700" : "");
                txtHighIn.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
            }
        });
        tbHighIn.setHorizontalAlignment(SwingConstants.LEFT);
        panel1.add(tbHighIn, CC.xy(3, 19));

        lblDaysDrink.setText(OPDE.lang.getString(PnlValues.internalClassID + ".DlgValueControl.lblDaysDrink.tooltip"));
        lblDayStool.setText(OPDE.lang.getString(PnlValues.internalClassID + ".DlgValueControl.lblDayStool.tooltip"));
        lblMin.setText(OPDE.lang.getString(PnlValues.internalClassID + ".DlgValueControl.lblMin.tooltip"));
        lblMax.setText(OPDE.lang.getString(PnlValues.internalClassID + ".DlgValueControl.lblMax.tooltip"));
    }

    private void setTxtDaysDrink() {
        txtDaysDrink.setEnabled(tbBalance.isSelected() || tbHighIn.isSelected() || tbLowIn.isSelected());
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        props = null;
        dispose();
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        lblDayStool = new JLabel();
        txtStoolDays = new JTextField();
        separator1 = new JSeparator();
        lblMin = new JLabel();
        txtLowIn = new JTextField();
        lblMax = new JLabel();
        txtHighIn = new JTextField();
        lblDaysDrink = new JLabel();
        txtDaysDrink = new JTextField();
        panel2 = new JPanel();
        btnCancel = new JButton();
        btnApply = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                    "13dlu, $lcgap, default:grow, $lcgap, 13dlu",
                    "13dlu, 13*($lgap, default), $pgap, default, $lgap, 13dlu"));

            //---- lblDayStool ----
            lblDayStool.setText("tage ohne stuhlgang");
            lblDayStool.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblDayStool, CC.xy(3, 5));

            //---- txtStoolDays ----
            txtStoolDays.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(txtStoolDays, CC.xy(3, 7));
            panel1.add(separator1, CC.xy(3, 9));

            //---- lblMin ----
            lblMin.setText("min menge in 24h");
            lblMin.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblMin, CC.xy(3, 15));

            //---- txtLowIn ----
            txtLowIn.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(txtLowIn, CC.xy(3, 17));

            //---- lblMax ----
            lblMax.setText("max menge in 24h");
            lblMax.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblMax, CC.xy(3, 21));

            //---- txtHighIn ----
            txtHighIn.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(txtHighIn, CC.xy(3, 23));

            //---- lblDaysDrink ----
            lblDaysDrink.setText("kontrollzeitraum tage");
            lblDaysDrink.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblDaysDrink, CC.xy(3, 25));

            //---- txtDaysDrink ----
            txtDaysDrink.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(txtDaysDrink, CC.xy(3, 27));

            //======== panel2 ========
            {
                panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

                //---- btnCancel ----
                btnCancel.setText(null);
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancelActionPerformed(e);
                    }
                });
                panel2.add(btnCancel);

                //---- btnApply ----
                btnApply.setText(null);
                btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnApply.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnApplyActionPerformed(e);
                    }
                });
                panel2.add(btnApply);
            }
            panel1.add(panel2, CC.xy(3, 29, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(panel1);
        setSize(540, 440);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel lblDayStool;
    private JTextField txtStoolDays;
    private JSeparator separator1;
    private JLabel lblMin;
    private JTextField txtLowIn;
    private JLabel lblMax;
    private JTextField txtHighIn;
    private JLabel lblDaysDrink;
    private JTextField txtDaysDrink;
    private JPanel panel2;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
