/*
 * Created by JFormDesigner on Sat Oct 20 14:22:06 CEST 2012
 */

package op.care.values;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.info.Resident;
import entity.info.ResidentTools;
import gui.GUITools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.MyJDialog;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.Properties;

/**
 * @author Torsten Löhr
 */
public class DlgValueControl extends MyJDialog {
    //    public static final String internalClassID = "nursingrecords.vitalparameters.DlgValueControl";
    private JToggleButton tbStool, tbBalance, tbLowIn, tbHighIn;
    Properties props = null;


    private Closure afterAction;

    public DlgValueControl(Resident resident, Closure afterAction) {
        super(false);
        this.afterAction = afterAction;
        props = resident.getControlling();
        initComponents();
        initDialog();
        pack();
        setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();    //To change body of overridden methods use File | Settings | File Templates.
        afterAction.execute(props);
    }

    private void initDialog() {


        String tooltip = SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.tx.tooltip").replace('[', '<').replace(']', '>');
        lblTX.setToolTipText(SYSTools.toHTMLForScreen("<p style=\"width:300px;\">" + tooltip + "</p>"));
        tooltip = SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.tx.hiloin.tooltip").replace('[', '<').replace(']', '>');
        lblTX2.setToolTipText(SYSTools.toHTMLForScreen("<p style=\"width:300px;\">" + tooltip + "</p>"));
        lblTX3.setToolTipText(SYSTools.toHTMLForScreen("<p style=\"width:300px;\">" + tooltip + "</p>"));

        tbStool = GUITools.getNiceToggleButton(SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.tbStool.tooltip"));
        tbStool.setFont(SYSConst.ARIAL14BOLD);
        tbStool.setHorizontalAlignment(SwingConstants.LEFT);
        panel1.add(tbStool, CC.xyw(3, 3, 2));

        tbBalance = GUITools.getNiceToggleButton(SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.tbBalance.tooltip"));
        tbBalance.setHorizontalAlignment(SwingConstants.LEFT);
        tbBalance.setFont(SYSConst.ARIAL14BOLD);
        panel1.add(tbBalance, CC.xy(3, 11));

        tbLowIn = GUITools.getNiceToggleButton(SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.tbLowIn.tooltip"));
        tbLowIn.setHorizontalAlignment(SwingConstants.LEFT);
        tbLowIn.setFont(SYSConst.ARIAL14BOLD);
        panel1.add(tbLowIn, CC.xyw(3, 13, 2));

        tbHighIn = GUITools.getNiceToggleButton(SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.tbHighIn.tooltip"));
        tbHighIn.setHorizontalAlignment(SwingConstants.LEFT);
        tbHighIn.setFont(SYSConst.ARIAL14BOLD);
        panel1.add(tbHighIn, CC.xyw(3, 15, 2));

        lblDaysDrink.setText(SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.lblDaysDrink.tooltip"));
        lblDayStool.setText(SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.lblDayStool.tooltip"));
        lblMin.setText(SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.lblMin.tooltip"));
        lblMax.setText(SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.lblMax.tooltip"));
        lblTarget.setText(SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.lblTarget.tooltip"));

        tbStool.setSelected(props.containsKey(ResidentTools.KEY_STOOLDAYS) && !props.getProperty(ResidentTools.KEY_STOOLDAYS).equals("off"));
        tbBalance.setSelected(props.containsKey(ResidentTools.KEY_BALANCE) && !props.getProperty(ResidentTools.KEY_BALANCE).equals("off"));
        tbLowIn.setSelected(props.containsKey(ResidentTools.KEY_LOWIN) && !props.getProperty(ResidentTools.KEY_LOWIN).equals("off"));
        tbHighIn.setSelected(props.containsKey(ResidentTools.KEY_HIGHIN) && !props.getProperty(ResidentTools.KEY_HIGHIN).equals("off"));
        boolean drinkon = tbBalance.isSelected() || tbLowIn.isSelected() || tbHighIn.isSelected();
        txtDaysDrink.setEnabled(drinkon);
        txtStoolDays.setText(tbStool.isSelected() ? props.getProperty(ResidentTools.KEY_STOOLDAYS) : "");
        txtStoolDays.setEnabled(tbStool.isSelected());
        txtLowIn.setText(tbLowIn.isSelected() ? props.getProperty(ResidentTools.KEY_LOWIN) : "");
        txtLowIn.setEnabled(tbLowIn.isSelected());
        txtTargetIn.setText(tbBalance.isSelected() ? props.getProperty(ResidentTools.KEY_TARGETIN) : "");
        txtTargetIn.setEnabled(tbBalance.isSelected());
        txtHighIn.setText(tbHighIn.isSelected() ? props.getProperty(ResidentTools.KEY_HIGHIN) : "");
        txtHighIn.setEnabled(tbHighIn.isSelected());
        txtDaysDrink.setText(drinkon ? props.getProperty(ResidentTools.KEY_DAYSDRINK) : "");
        txtDaysDrink.setEnabled(drinkon);

        tbStool.addItemListener(itemEvent -> {
            txtStoolDays.setText(itemEvent.getStateChange() == ItemEvent.SELECTED ? "1" : "");
            // #24
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                props.setProperty(ResidentTools.KEY_DATE1, new LocalDate().toString("yyyy-MM-dd"));
            } else {
                props.remove(ResidentTools.KEY_DATE1);
            }
            txtStoolDays.setEnabled(tbStool.isSelected());
        });
        tbBalance.addItemListener(itemEvent -> {
            setTxtDaysDrink();
            txtTargetIn.setText(itemEvent.getStateChange() == ItemEvent.SELECTED ? "1400" : "");
            txtTargetIn.setEnabled(tbBalance.isSelected());
        });
        tbLowIn.addItemListener(itemEvent -> {
            setTxtDaysDrink();
            // #24
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                props.setProperty(ResidentTools.KEY_DATE2, new LocalDate().toString("yyyy-MM-dd"));
            } else {
                props.remove(ResidentTools.KEY_DATE2);
            }
            txtLowIn.setText(itemEvent.getStateChange() == ItemEvent.SELECTED ? "1000" : "");
            txtLowIn.setEnabled(tbLowIn.isSelected());
        });
        tbHighIn.addItemListener(itemEvent -> {
            setTxtDaysDrink();
            // #24
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                props.setProperty(ResidentTools.KEY_DATE3, new LocalDate().toString("yyyy-MM-dd"));
            } else {
                props.remove(ResidentTools.KEY_DATE3);
            }
            txtHighIn.setText(itemEvent.getStateChange() == ItemEvent.SELECTED ? "1700" : "");
            txtHighIn.setEnabled(tbHighIn.isSelected());
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
        if (isSaveOK()) {
            save();
            dispose();
        }
    }

    private boolean isSaveOK() {
        boolean drinkon = tbBalance.isSelected() || tbLowIn.isSelected() || tbHighIn.isSelected();

        BigDecimal stooldays = SYSTools.parseDecimal(txtStoolDays.getText());
        BigDecimal highin = SYSTools.parseDecimal(txtHighIn.getText());
        BigDecimal targetin = SYSTools.parseDecimal(txtTargetIn.getText());
        BigDecimal lowin = SYSTools.parseDecimal(txtLowIn.getText());

        boolean stoolOK = !tbStool.isSelected() || (stooldays != null && isGreaterZero(stooldays));
        boolean highinOK = !tbHighIn.isSelected() || (highin != null && isGreaterZero(highin));
        boolean targetinOK = !tbBalance.isSelected() || (targetin != null && isGreaterZero(targetin));
        boolean lowinOK = !tbLowIn.isSelected() || (lowin != null && isGreaterZero(lowin));
        boolean daysOK = !drinkon || (SYSTools.parseDecimal(txtDaysDrink.getText()) != null && SYSTools.parseDecimal(txtDaysDrink.getText()).compareTo(BigDecimal.ZERO) > 0);

        boolean sanityOK = aLESSERb(lowin, targetin) && aLESSERb(lowin, highin) && aLESSERb(targetin, highin) && isGreaterZero(lowin) && isGreaterZero(highin) && isGreaterZero(targetin);

        String reason = "";
        reason += (stoolOK ? "" : SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.stoolXX"));
        reason += (highinOK ? "" : SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.highinXX"));
        reason += (lowinOK ? "" : SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.lowinXX"));
        reason += (daysOK ? "" : SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.daysdrinkXX"));
        reason += (sanityOK ? "" : SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.sanityXX"));
        reason += (targetinOK ? "" : SYSTools.xx("nursingrecords.vitalparameters.DlgValueControl.targetinXX"));

        if (!reason.isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(reason, DisplayMessage.WARNING));
        }
        return stoolOK && highinOK && lowinOK && daysOK && sanityOK;

    }

    private boolean isGreaterZero(BigDecimal a) {
        return a == null || a.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean aLESSERb(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            return true;
        }

        return a.compareTo(b) < 0;
    }

    private void save() {
        boolean drinkon = tbBalance.isSelected() || tbLowIn.isSelected() || tbHighIn.isSelected();
        props.setProperty(ResidentTools.KEY_STOOLDAYS, tbStool.isSelected() ? txtStoolDays.getText() : "off");
        props.setProperty(ResidentTools.KEY_BALANCE, tbBalance.isSelected() ? "on" : "off");
        props.setProperty(ResidentTools.KEY_LOWIN, tbLowIn.isSelected() ? txtLowIn.getText() : "off");
        props.setProperty(ResidentTools.KEY_TARGETIN, tbBalance.isSelected() ? txtTargetIn.getText() : "off");
        props.setProperty(ResidentTools.KEY_HIGHIN, tbHighIn.isSelected() ? txtHighIn.getText() : "off");
        props.setProperty(ResidentTools.KEY_DAYSDRINK, drinkon ? txtDaysDrink.getText() : "off");
    }

    // #fixes #19
    private void txtStoolDaysFocusLost(FocusEvent e) {
        txtStoolDays.setText(SYSTools.parseDecimal(SYSTools.catchNull(txtStoolDays.getText(), "0")).toBigInteger().toString());
    }

    private void txtLowInFocusLost(FocusEvent e) {
        txtLowIn.setText(SYSTools.formatBigDecimal(SYSTools.parseDecimal(SYSTools.catchNull(txtLowIn.getText(), "0"))));
    }

    private void txtHighInFocusLost(FocusEvent e) {
        txtHighIn.setText(SYSTools.formatBigDecimal(SYSTools.parseDecimal(SYSTools.catchNull(txtHighIn.getText(), "0"))));
    }

    // #fixes #19
    private void txtDaysDrinkFocusLost(FocusEvent e) {
        txtDaysDrink.setText(SYSTools.parseDecimal(SYSTools.catchNull(txtDaysDrink.getText(), "0")).toBigInteger().toString());
    }

    private void txtTargetInFocusLost(FocusEvent e) {
        txtTargetIn.setText(SYSTools.formatBigDecimal(SYSTools.parseDecimal(SYSTools.catchNull(txtTargetIn.getText(), "0"))));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        lblDayStool = new JLabel();
        txtStoolDays = new JTextField();
        separator1 = new JSeparator();
        lblTX = new JLabel();
        separator2 = new JSeparator();
        lblMin = new JLabel();
        txtLowIn = new JTextField();
        lblTX2 = new JLabel();
        lblTarget = new JLabel();
        txtTargetIn = new JTextField();
        lblMax = new JLabel();
        txtHighIn = new JTextField();
        lblTX3 = new JLabel();
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
                    "13dlu, $lcgap, default:grow, $lcgap, default, $lcgap, 13dlu",
                    "13dlu, 16*($lgap, default), $pgap, default, $lgap, 13dlu"));

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
            panel1.add(txtStoolDays, CC.xywh(3, 7, 3, 1));
            panel1.add(separator1, CC.xywh(3, 9, 3, 1));

            //---- lblTX ----
            lblTX.setText(null);
            lblTX.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ambulance2.png")));
            panel1.add(lblTX, CC.xy(5, 11));
            panel1.add(separator2, CC.xywh(3, 17, 3, 1));

            //---- lblMin ----
            lblMin.setText("min menge in 24h");
            lblMin.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblMin, CC.xy(3, 19));

            //---- txtLowIn ----
            txtLowIn.setFont(new Font("Arial", Font.PLAIN, 14));
            txtLowIn.setEnabled(false);
            txtLowIn.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtLowInFocusLost(e);
                }
            });
            panel1.add(txtLowIn, CC.xy(3, 21));

            //---- lblTX2 ----
            lblTX2.setText(null);
            lblTX2.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ambulance2.png")));
            panel1.add(lblTX2, CC.xy(5, 21));

            //---- lblTarget ----
            lblTarget.setText("zieltrink menge in 24h");
            lblTarget.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblTarget, CC.xy(3, 23));

            //---- txtTargetIn ----
            txtTargetIn.setFont(new Font("Arial", Font.PLAIN, 14));
            txtTargetIn.setEnabled(false);
            txtTargetIn.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtTargetInFocusLost(e);
                }
            });
            panel1.add(txtTargetIn, CC.xy(3, 25));

            //---- lblMax ----
            lblMax.setText("max menge in 24h");
            lblMax.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblMax, CC.xy(3, 27));

            //---- txtHighIn ----
            txtHighIn.setFont(new Font("Arial", Font.PLAIN, 14));
            txtHighIn.setEnabled(false);
            txtHighIn.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtHighInFocusLost(e);
                }
            });
            panel1.add(txtHighIn, CC.xy(3, 29));

            //---- lblTX3 ----
            lblTX3.setText(null);
            lblTX3.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ambulance2.png")));
            panel1.add(lblTX3, CC.xy(5, 29));

            //---- lblDaysDrink ----
            lblDaysDrink.setText("kontrollzeitraum tage");
            lblDaysDrink.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblDaysDrink, CC.xy(3, 31));

            //---- txtDaysDrink ----
            txtDaysDrink.setFont(new Font("Arial", Font.PLAIN, 14));
            txtDaysDrink.setEnabled(false);
            txtDaysDrink.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtDaysDrinkFocusLost(e);
                }
            });
            panel1.add(txtDaysDrink, CC.xywh(3, 33, 3, 1));

            //======== panel2 ========
            {
                panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

                //---- btnCancel ----
                btnCancel.setText(null);
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                btnCancel.addActionListener(e -> btnCancelActionPerformed(e));
                panel2.add(btnCancel);

                //---- btnApply ----
                btnApply.setText(null);
                btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnApply.addActionListener(e -> btnApplyActionPerformed(e));
                panel2.add(btnApply);
            }
            panel1.add(panel2, CC.xy(3, 35, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(panel1);
        setSize(540, 515);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel lblDayStool;
    private JTextField txtStoolDays;
    private JSeparator separator1;
    private JLabel lblTX;
    private JSeparator separator2;
    private JLabel lblMin;
    private JTextField txtLowIn;
    private JLabel lblTX2;
    private JLabel lblTarget;
    private JTextField txtTargetIn;
    private JLabel lblMax;
    private JTextField txtHighIn;
    private JLabel lblTX3;
    private JLabel lblDaysDrink;
    private JTextField txtDaysDrink;
    private JPanel panel2;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
