/*
 * Created by JFormDesigner on Sat Oct 20 14:22:06 CEST 2012
 */

package op.care.values;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.info.Resident;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.GUITools;
import op.tools.MyJDialog;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;

/**
 * @author Torsten Löhr
 */
public class DlgValueControl extends MyJDialog {
    public static final String internalClassID = "nursingrecords.vitalparameters.DlgValueControl";
    private JToggleButton tbStool, tbBalance, tbLowIn, tbHighIn;
    Properties props = null;

    final String KEY_STOOLDAYS = "stooldays";
    final String KEY_BALANCE = "liquidbalance";
    final String KEY_LOWIN = "lowin";
    final String KEY_HIGHIN = "highin";
    final String KEY_DAYSDRINK = "daysdrink";
    private Closure afterAction;

    public DlgValueControl(Resident resident, Closure afterAction) {
        super();
        this.afterAction = afterAction;
        props = resident.getControlling();
        initComponents();
        initDialog();
        setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();    //To change body of overridden methods use File | Settings | File Templates.
        afterAction.execute(props);
    }

    private void initDialog() {
        tbStool = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".tbStool.tooltip"));
        tbStool.setFont(SYSConst.ARIAL14BOLD);
        tbStool.setHorizontalAlignment(SwingConstants.LEFT);
        panel1.add(tbStool, CC.xy(3, 3));

        tbBalance = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".tbBalance.tooltip"));
        tbBalance.setHorizontalAlignment(SwingConstants.LEFT);
        tbBalance.setFont(SYSConst.ARIAL14BOLD);
        panel1.add(tbBalance, CC.xy(3, 11));

        tbLowIn = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".tbLowIn.tooltip"));
        tbLowIn.setHorizontalAlignment(SwingConstants.LEFT);
        tbLowIn.setFont(SYSConst.ARIAL14BOLD);
        panel1.add(tbLowIn, CC.xy(3, 13));

        tbHighIn = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".tbHighIn.tooltip"));
        tbHighIn.setHorizontalAlignment(SwingConstants.LEFT);
        tbHighIn.setFont(SYSConst.ARIAL14BOLD);
        panel1.add(tbHighIn, CC.xy(3, 19));

        lblDaysDrink.setText(OPDE.lang.getString(internalClassID + ".lblDaysDrink.tooltip"));
        lblDayStool.setText(OPDE.lang.getString(internalClassID + ".lblDayStool.tooltip"));
        lblMin.setText(OPDE.lang.getString(internalClassID + ".lblMin.tooltip"));
        lblMax.setText(OPDE.lang.getString(internalClassID + ".lblMax.tooltip"));

        tbStool.setSelected(props.containsKey(KEY_STOOLDAYS) && !props.getProperty(KEY_STOOLDAYS).equals("off"));
        tbBalance.setSelected(props.containsKey(KEY_BALANCE) && !props.getProperty(KEY_BALANCE).equals("off"));
        tbLowIn.setSelected(props.containsKey(KEY_LOWIN) && !props.getProperty(KEY_LOWIN).equals("off"));
        tbHighIn.setSelected(props.containsKey(KEY_HIGHIN) && !props.getProperty(KEY_HIGHIN).equals("off"));
        boolean drinkon = tbBalance.isSelected() || tbLowIn.isSelected() || tbHighIn.isSelected();
        txtDaysDrink.setEnabled(drinkon);
        txtStoolDays.setText(tbStool.isSelected() ? props.getProperty(KEY_STOOLDAYS) : "");
        txtStoolDays.setEnabled(tbStool.isSelected());
        txtLowIn.setText(tbLowIn.isSelected() ? props.getProperty(KEY_LOWIN) : "");
        txtLowIn.setEnabled(tbLowIn.isSelected());
        txtHighIn.setText(tbHighIn.isSelected() ? props.getProperty(KEY_HIGHIN) : "");
        txtHighIn.setEnabled(tbHighIn.isSelected());
        txtDaysDrink.setText(drinkon ? props.getProperty(KEY_DAYSDRINK) : "");
        txtDaysDrink.setEnabled(drinkon);

        tbStool.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                txtStoolDays.setText(itemEvent.getStateChange() == ItemEvent.SELECTED ? "1" : "");
                txtStoolDays.setEnabled(tbStool.isSelected());
            }
        });
        tbBalance.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                setTxtDaysDrink();
            }
        });
        tbLowIn.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                setTxtDaysDrink();
                txtLowIn.setText(itemEvent.getStateChange() == ItemEvent.SELECTED ? "1000" : "");
                txtLowIn.setEnabled(tbLowIn.isSelected());
            }
        });
        tbHighIn.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                setTxtDaysDrink();
                txtHighIn.setText(itemEvent.getStateChange() == ItemEvent.SELECTED ? "1700" : "");
                txtHighIn.setEnabled(tbHighIn.isSelected());
            }
        });
    }

    private void setTxtDaysDrink() {
        txtDaysDrink.setEnabled(tbBalance.isSelected() || tbHighIn.isSelected() || tbLowIn.isSelected());
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        props = null;
        dispose();
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        if (saveOK()) {
            save();
            dispose();
        }
    }

    private boolean saveOK() {
        boolean drinkon = tbBalance.isSelected() || tbLowIn.isSelected() || tbHighIn.isSelected();

        boolean stoolOK = !tbStool.isSelected() || SYSTools.parseDecimal(txtStoolDays.getText()) != null;
        boolean highinOK = !tbHighIn.isSelected() || SYSTools.parseDecimal(txtHighIn.getText()) != null;
        boolean lowinOK = !tbLowIn.isSelected() || SYSTools.parseDecimal(txtLowIn.getText()) != null;
        boolean daysOK = !drinkon || SYSTools.parseDecimal(txtDaysDrink.getText()) != null;

        String reason = "";
        reason += (stoolOK ? "" : OPDE.lang.getString(internalClassID + ".stoolXX"));
        reason += (highinOK ? "" : OPDE.lang.getString(internalClassID + ".highinXX"));
        reason += (lowinOK ? "" : OPDE.lang.getString(internalClassID + ".lowinXX"));
        reason += (daysOK ? "" : OPDE.lang.getString(internalClassID + ".daysdrinkXX"));

        if (!reason.isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(reason, DisplayMessage.WARNING));
        }
        return stoolOK && highinOK && lowinOK && daysOK;

    }

    private void save() {
        boolean drinkon = tbBalance.isSelected() || tbLowIn.isSelected() || tbHighIn.isSelected();
        props.setProperty(KEY_STOOLDAYS, tbStool.isSelected() ? txtStoolDays.getText() : "off");
        props.setProperty(KEY_BALANCE, tbBalance.isSelected() ? "on" : "off");
        props.setProperty(KEY_LOWIN, tbLowIn.isSelected() ? txtLowIn.getText() : "off");
        props.setProperty(KEY_HIGHIN, tbHighIn.isSelected() ? txtHighIn.getText() : "off");
        props.setProperty(KEY_DAYSDRINK, drinkon ? txtDaysDrink.getText() : "off");
    }

    private void txtStoolDaysFocusLost(FocusEvent e) {
        txtStoolDays.setText(SYSTools.parseDecimal(txtStoolDays.getText()).toString());
    }

    private void txtLowInFocusLost(FocusEvent e) {
        txtLowIn.setText(SYSTools.parseDecimal(txtLowIn.getText()).toString());
    }

    private void txtHighInFocusLost(FocusEvent e) {
        txtHighIn.setText(SYSTools.parseDecimal(txtHighIn.getText()).toString());
    }

    private void txtDaysDrinkFocusLost(FocusEvent e) {
        txtDaysDrink.setText(SYSTools.parseDecimal(txtDaysDrink.getText()).toString());
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
            txtStoolDays.setEnabled(false);
            txtStoolDays.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtStoolDaysFocusLost(e);
                }
            });
            panel1.add(txtStoolDays, CC.xy(3, 7));
            panel1.add(separator1, CC.xy(3, 9));

            //---- lblMin ----
            lblMin.setText("min menge in 24h");
            lblMin.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblMin, CC.xy(3, 15));

            //---- txtLowIn ----
            txtLowIn.setFont(new Font("Arial", Font.PLAIN, 14));
            txtLowIn.setEnabled(false);
            txtLowIn.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtLowInFocusLost(e);
                }
            });
            panel1.add(txtLowIn, CC.xy(3, 17));

            //---- lblMax ----
            lblMax.setText("max menge in 24h");
            lblMax.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblMax, CC.xy(3, 21));

            //---- txtHighIn ----
            txtHighIn.setFont(new Font("Arial", Font.PLAIN, 14));
            txtHighIn.setEnabled(false);
            txtHighIn.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtHighInFocusLost(e);
                }
            });
            panel1.add(txtHighIn, CC.xy(3, 23));

            //---- lblDaysDrink ----
            lblDaysDrink.setText("kontrollzeitraum tage");
            lblDaysDrink.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblDaysDrink, CC.xy(3, 25));

            //---- txtDaysDrink ----
            txtDaysDrink.setFont(new Font("Arial", Font.PLAIN, 14));
            txtDaysDrink.setEnabled(false);
            txtDaysDrink.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtDaysDrinkFocusLost(e);
                }
            });
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