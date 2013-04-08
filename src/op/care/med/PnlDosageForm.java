/*
 * Created by JFormDesigner on Thu Mar 07 10:20:20 CET 2013
 */

package op.care.med;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.nursingprocess.Intervention;
import entity.nursingprocess.InterventionTools;
import entity.prescription.DosageForm;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.GUITools;
import op.tools.PopupPanel;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;

/**
 * @author Torsten Löhr
 */
public class PnlDosageForm extends PopupPanel {
    public static final String internalClassID = "opde.medication.pnlDosageForm";
    private DosageForm form;
    private JTextField txtDailyPlan, txtSameAs;

    public PnlDosageForm(DosageForm form) {
        this.form = form;
        initComponents();
        initPanel();
    }

    private void cmbUsageUnitItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (cmbPackUnit.getSelectedIndex() == cmbUsageUnit.getSelectedIndex()) {
                cmbUPR.setSelectedIndex(0);
                cmbUPR.setEnabled(false);
            } else {
                cmbUPR.setEnabled(true);
            }
        }
    }

    private void cmbPackUnitItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (cmbPackUnit.getSelectedIndex() == cmbUsageUnit.getSelectedIndex()) {
                cmbUPR.setSelectedIndex(0);
                cmbUPR.setEnabled(false);
            } else {
                cmbUPR.setEnabled(true);
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblPreparation = new JLabel();
        txtPreparation = new JTextField();
        lblUsage = new JLabel();
        txtUsageText = new JTextField();
        lblUsageUnit = new JLabel();
        cmbUsageUnit = new JComboBox();
        lblPackUnit = new JLabel();
        cmbPackUnit = new JComboBox();
        lblIntervention = new JLabel();
        cmbIntervention = new JComboBox();
        lblDailyPlan = new JLabel();
        lblEquiv = new JLabel();
        lblUPRState = new JLabel();
        cmbUPR = new JComboBox();

        //======== this ========
        setLayout(new FormLayout(
            "2*(default, $lcgap), default:grow, $lcgap, default",
            "9*(default, $lgap), default"));

        //---- lblPreparation ----
        lblPreparation.setText("preparation");
        add(lblPreparation, CC.xy(3, 3));
        add(txtPreparation, CC.xy(5, 3));

        //---- lblUsage ----
        lblUsage.setText("usageText");
        add(lblUsage, CC.xy(3, 5));
        add(txtUsageText, CC.xy(5, 5));

        //---- lblUsageUnit ----
        lblUsageUnit.setText("usageUnit");
        add(lblUsageUnit, CC.xy(3, 7));

        //---- cmbUsageUnit ----
        cmbUsageUnit.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbUsageUnitItemStateChanged(e);
            }
        });
        add(cmbUsageUnit, CC.xy(5, 7));

        //---- lblPackUnit ----
        lblPackUnit.setText("packUnit");
        add(lblPackUnit, CC.xy(3, 9));

        //---- cmbPackUnit ----
        cmbPackUnit.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbPackUnitItemStateChanged(e);
            }
        });
        add(cmbPackUnit, CC.xy(5, 9));

        //---- lblIntervention ----
        lblIntervention.setText("Intervention");
        add(lblIntervention, CC.xy(3, 11));
        add(cmbIntervention, CC.xy(5, 11));

        //---- lblDailyPlan ----
        lblDailyPlan.setText("dailyPlan");
        add(lblDailyPlan, CC.xy(3, 13));

        //---- lblEquiv ----
        lblEquiv.setText("equiv");
        add(lblEquiv, CC.xy(3, 15));

        //---- lblUPRState ----
        lblUPRState.setText("UPR_STATE");
        add(lblUPRState, CC.xy(3, 17));
        add(cmbUPR, CC.xy(5, 17));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void initPanel() {

        lblPreparation.setText(OPDE.lang.getString(internalClassID + ".preparation"));
        lblUsage.setText(OPDE.lang.getString(internalClassID + ".usagetext"));
        lblUsageUnit.setText(OPDE.lang.getString(internalClassID + ".usageUnit"));
        lblPackUnit.setText(OPDE.lang.getString(internalClassID + ".packUnit"));
        lblIntervention.setText(OPDE.lang.getString(internalClassID + ".intervention"));
        lblDailyPlan.setText(OPDE.lang.getString(internalClassID + ".dailyPlan"));
        lblEquiv.setText(OPDE.lang.getString(internalClassID + ".sameas"));
        lblUPRState.setText(OPDE.lang.getString(internalClassID + ".uprstate"));

        cmbUPR.setModel(new DefaultComboBoxModel(new String[]{OPDE.lang.getString("state_upr1"), OPDE.lang.getString("state_upr_by_tradeform"), OPDE.lang.getString("state_upr_by_resident")}));
        cmbUPR.setSelectedIndex(form.getUPRState());
        cmbIntervention.setModel(new DefaultComboBoxModel(InterventionTools.findBy(InterventionTools.TYPE_PRESCRIPTION).toArray()));
        cmbUsageUnit.setModel(new DefaultComboBoxModel(Arrays.copyOfRange(SYSConst.UNITS, 1, SYSConst.UNITS.length - 1)));
        cmbPackUnit.setModel(new DefaultComboBoxModel(Arrays.copyOfRange(SYSConst.UNITS, 1, SYSConst.UNITS.length - 1)));
        txtDailyPlan = GUITools.createIntegerTextField(0, 20, 0);
        add(txtDailyPlan, CC.xy(5, 13));
        txtSameAs = GUITools.createIntegerTextField(0, 100, 0);
        add(txtSameAs, CC.xy(5, 15));

        txtUsageText.setText(form.getUsageText());
        txtPreparation.setText(form.getPreparation());
        cmbPackUnit.setSelectedIndex(form.getPackUnit() - 1);
        cmbUsageUnit.setSelectedIndex(form.getUsageUnit() - 1);

        cmbUPR.setEnabled(cmbPackUnit.getSelectedIndex() != cmbUsageUnit.getSelectedIndex());

        if (form.getIntervention() != null) {
            cmbIntervention.setSelectedItem(form.getIntervention());
        } else {
            cmbIntervention.setSelectedIndex(0);
        }

        txtDailyPlan.setText(Short.toString(form.getDailyPlan()));
        txtSameAs.setText(Integer.toString(form.getSameAs()));

    }

    @Override
    public Object getResult() {
        form.setPreparation(txtPreparation.getText());
        form.setUsageText(txtUsageText.getText());
        form.setPackUnit(Short.parseShort(Integer.toString(cmbPackUnit.getSelectedIndex() + 1)));
        form.setUsageUnit(Short.parseShort(Integer.toString(cmbUsageUnit.getSelectedIndex() + 1)));
        form.setSameas(Integer.parseInt(txtSameAs.getText()));
        form.setDailyPlan(Short.parseShort(txtDailyPlan.getText()));
        form.setIntervention((Intervention) cmbIntervention.getSelectedItem());
        form.setUPRState(Short.parseShort(Integer.toString(cmbUPR.getSelectedIndex())));
        return form;
    }

    @Override
    public boolean isSaveOK() {

        if (SYSTools.catchNull(txtPreparation.getText()).trim().isEmpty() && SYSTools.catchNull(txtUsageText.getText()).trim().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".notbothmustbeempty", DisplayMessage.WARNING));
            return false;
        }
        if (SYSTools.catchNull(txtSameAs.getText()).trim().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.emptyentry", DisplayMessage.WARNING));
            return false;
        }
        if (SYSTools.catchNull(txtDailyPlan.getText()).trim().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.emptyentry", DisplayMessage.WARNING));
            return false;
        }

        return true;
    }

    @Override
    public void setStartFocus() {
        txtPreparation.requestFocus();
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblPreparation;
    private JTextField txtPreparation;
    private JLabel lblUsage;
    private JTextField txtUsageText;
    private JLabel lblUsageUnit;
    private JComboBox cmbUsageUnit;
    private JLabel lblPackUnit;
    private JComboBox cmbPackUnit;
    private JLabel lblIntervention;
    private JComboBox cmbIntervention;
    private JLabel lblDailyPlan;
    private JLabel lblEquiv;
    private JLabel lblUPRState;
    private JComboBox cmbUPR;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
