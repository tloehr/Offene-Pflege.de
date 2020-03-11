/*
 * Created by JFormDesigner on Wed May 30 16:04:17 CEST 2012
 */

package de.offene_pflege.op.care.med.prodassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.entity.prescription.*;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.care.med.structure.PnlDosageForm;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Torsten Löhr
 */
public class PnlTradeForm extends JPanel {
    private MedProducts product;
    private TradeForm tradeForm;
    private DosageForm dosageForm;
    private Closure validate;
    private boolean ignoreEvent = false;

    public static final String internalClassID = "opde.medication.medproduct.wizard.subtext";

    public PnlTradeForm(Closure validate, MedProducts product) {
        this.validate = validate;
        initComponents();
        setProduct(product);
        initPanel();
    }

    public void setProduct(MedProducts product) {
        this.product = product;
        if (!product.getTradeforms().isEmpty()) {
            ArrayList model = new ArrayList(product.getTradeforms());
            model.add(0, "<html><b>" + SYSTools.xx("misc.msg.noneOfThem") + "</b></html>");
            DefaultListModel lmDaf = SYSTools.list2dlm(model);
            lstDaf.setModel(lmDaf);
            lstDaf.setCellRenderer(TradeFormTools.getRenderer(TradeFormTools.LONG));
        }
        lblMsg.setText(SYSTools.xx("opde.medication.medproduct.wizard.subtext.existingTradeforms"));
        cbWeightControlled.setText(SYSTools.xx("opde.medication.medproduct.wizard.subtext.weightControlled"));
        lblMsg.setVisible(!product.getTradeforms().isEmpty());
        jsp1.setVisible(!product.getTradeforms().isEmpty());
        lstDaf.setVisible(!product.getTradeforms().isEmpty());
    }

    private void initPanel() {


        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" SELECT m FROM DosageForm m ");

        java.util.List listDosageForm = query.getResultList();
        Collections.sort(listDosageForm, (us, them) -> DosageFormTools.toPrettyString((DosageForm) us).compareTo(DosageFormTools.toPrettyString((DosageForm) them)));

        cmbFormen.setModel(SYSTools.list2cmb(listDosageForm));
        cmbFormen.setRenderer(DosageFormTools.getRenderer(0));
        em.close();

        cmbDaysWeeks.setModel(new DefaultComboBoxModel(new String[]{SYSTools.xx("misc.msg.Days"), SYSTools.xx("misc.msg.weeks")}));

        dosageForm = (DosageForm) cmbFormen.getSelectedItem();
        cbExpiresAfterOpened.setText(SYSTools.xx("tradeform.subtext.expiresAfterOpenedIn"));
        cbExpiresAfterOpened.setSelected(false);
        tradeForm = new TradeForm(product, "", dosageForm);

        rbCalcUPR.setSelected(true);
        rbCalcUPR.setText(SYSTools.xx("opde.medication.medproduct.wizard.subtext.calcUPR"));
        rbSetUPR.setText(SYSTools.xx("opde.medication.medproduct.wizard.subtext.setUPR"));
        txtUPR.setText("10");
        txtUPR.setEnabled(false);
        pnlUPR.setVisible(false);

        validate.execute(tradeForm);
    }

    private void txtZusatzActionPerformed(ActionEvent e) {
        cmbFormen.setEnabled(true);
        tradeForm = new TradeForm(product, txtZusatz.getText().trim(), dosageForm);
        validate.execute(tradeForm);
        if (lstDaf.isVisible() && lstDaf.getSelectedIndex() != 0) {
            ignoreEvent = true;
            lstDaf.setSelectedIndex(0);
            ignoreEvent = false;
        }
    }

    private void lstDafValueChanged(ListSelectionEvent e) {
        if (ignoreEvent) {
            return;
        }
        if (!e.getValueIsAdjusting()) {
            if (lstDaf.getSelectedIndex() > 0) {
                tradeForm = (TradeForm) lstDaf.getSelectedValue();
                txtZusatz.setText(null);
            } else {
                tradeForm = new TradeForm(product, txtZusatz.getText().trim(), dosageForm);
            }
            // https://github.com/tloehr/Offene-Pflege.de/issues/34
            btnAdd.setEnabled(lstDaf.getSelectedIndex() <= 0);
            cmbFormen.setEnabled(lstDaf.getSelectedIndex() <= 0);
            SYSTools.setXEnabled(pnlUPR, lstDaf.getSelectedIndex() <= 0);
            SYSTools.setXEnabled(panel1, lstDaf.getSelectedIndex() <= 0);
            cbWeightControlled.setEnabled(lstDaf.getSelectedIndex() <= 0);
            validate.execute(tradeForm);
        }
    }

    private void cmbFormenItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;

        dosageForm = (DosageForm) cmbFormen.getSelectedItem();

        tradeForm = new TradeForm(product, txtZusatz.getText().trim(), dosageForm);

        // selection of constant UPR ?
        if (dosageForm.isUPRn()) {
            lblTo1.setText(" " + SYSConst.UNITS[dosageForm.getUsageUnit()] + " " + SYSTools.xx("misc.msg.to1") + " " + SYSConst.UNITS[dosageForm.getPackUnit()]);
            pnlUPR.setVisible(true);
            rbCalcUPR.setSelected(true);
        } else {
            pnlUPR.setVisible(false);
        }

        validate.execute(tradeForm);

    }

    private void btnAddActionPerformed(ActionEvent e) {
        PnlDosageForm pnl = new PnlDosageForm(new DosageForm(0));

        GUITools.showPopup(GUITools.createPanelPopup(pnl, o -> {
            if (o != null) {
                cmbFormen.setModel(new DefaultComboBoxModel(new DosageForm[]{(DosageForm) o}));
                dosageForm = (DosageForm) cmbFormen.getSelectedItem();
                tradeForm = new TradeForm(product, txtZusatz.getText().trim(), dosageForm);
                validate.execute(tradeForm);
            }
        }, btnAdd), SwingConstants.SOUTH_WEST);
    }

    private void rbCalcUPRItemStateChanged(ItemEvent e) {
        txtUPR.setEnabled(e.getStateChange() != ItemEvent.SELECTED);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            tradeForm.setUPR(null);
            validate.execute(tradeForm);
        }
    }

    private void rbSetUPRItemStateChanged(ItemEvent e) {
        txtUPR.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            tradeForm.setUPR(SYSTools.parseDecimal(txtUPR.getText()));
            validate.execute(tradeForm);
        }
    }

    private void txtUPRFocusLost(FocusEvent e) {
        BigDecimal upr = SYSTools.parseDecimal(txtUPR.getText());
        if (upr == null || upr.compareTo(BigDecimal.ZERO) <= 0) {
            upr = BigDecimal.TEN;
            txtUPR.setText("10");
        } else {
            txtUPR.setText(SYSTools.formatBigDecimal(upr.setScale(2, RoundingMode.HALF_UP)));
        }
        tradeForm.setUPR(upr);
    }

    private void cbExpiresAfterOpenedItemStateChanged(ItemEvent e) {
        txtExpiresIn.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        cmbDaysWeeks.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            txtExpiresIn.setText("7");
            cmbDaysWeeks.setSelectedIndex(0);
            tradeForm.setDaysToExpireAfterOpened(7);
            validate.execute(tradeForm);
        } else {
            tradeForm.setDaysToExpireAfterOpened(null);
            validate.execute(tradeForm);
        }
    }

    private void txtExpiresInFocusLost(FocusEvent e) {
        Integer i = SYSTools.checkInteger(txtExpiresIn.getText());
        if (i == null || i.compareTo(0) <= 0) {
            i = 7;
//            txtExpiresIn.setText("7");
        }
        if (cmbDaysWeeks.getSelectedIndex() == 1) {
            tradeForm.setDaysToExpireAfterOpened(i * 7);
        } else {
            tradeForm.setDaysToExpireAfterOpened(i);
        }

        txtExpiresIn.setText(Integer.toString(i));

        validate.execute(tradeForm);
    }

    private void cmbDaysWeeksItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Integer i = SYSTools.checkInteger(txtExpiresIn.getText());
            if (i == null || i.compareTo(0) <= 0) {
                i = 7;
//                txtExpiresIn.setText("7");
            }
            if (cmbDaysWeeks.getSelectedIndex() == 1) {
                tradeForm.setDaysToExpireAfterOpened(i * 7);
            } else {
                tradeForm.setDaysToExpireAfterOpened(i);
            }
            txtExpiresIn.setText(Integer.toString(i));
            validate.execute(tradeForm);
        }
    }

    private void cbWeightControlledItemStateChanged(ItemEvent e) {
        tradeForm.setWeightControlled(e.getStateChange() == ItemEvent.SELECTED);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        txtZusatz = new JXSearchField();
        cmbFormen = new JComboBox();
        btnAdd = new JButton();
        pnlUPR = new JPanel();
        rbCalcUPR = new JRadioButton();
        rbSetUPR = new JRadioButton();
        txtUPR = new JTextField();
        lblTo1 = new JLabel();
        panel1 = new JPanel();
        cbExpiresAfterOpened = new JCheckBox();
        hSpacer1 = new JPanel(null);
        txtExpiresIn = new JTextField();
        hSpacer2 = new JPanel(null);
        cmbDaysWeeks = new JComboBox();
        lbl1 = new JLabel();
        cbWeightControlled = new JCheckBox();
        lblMsg = new JLabel();
        jsp1 = new JScrollPane();
        lstDaf = new JList();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default, $ugap, default:grow, 2*($lcgap, default)",
            "2*(default, $lgap), default, $rgap, pref, 3*($lgap, default), $lgap, default:grow, $lgap, default"));

        //---- txtZusatz ----
        txtZusatz.setFont(new Font("Arial", Font.PLAIN, 14));
        txtZusatz.setInstantSearchDelay(0);
        txtZusatz.setPrompt("Zusatzbezeichnung");
        txtZusatz.addActionListener(e -> txtZusatzActionPerformed(e));
        add(txtZusatz, CC.xywh(3, 3, 5, 1));

        //---- cmbFormen ----
        cmbFormen.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbFormen.addItemListener(e -> cmbFormenItemStateChanged(e));
        add(cmbFormen, CC.xywh(3, 5, 3, 1));

        //---- btnAdd ----
        btnAdd.setBackground(Color.white);
        btnAdd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
        btnAdd.setToolTipText("Medikamente bearbeiten");
        btnAdd.setBorder(null);
        btnAdd.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png")));
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> btnAddActionPerformed(e));
        add(btnAdd, CC.xy(7, 5));

        //======== pnlUPR ========
        {
            pnlUPR.setLayout(new BoxLayout(pnlUPR, BoxLayout.X_AXIS));

            //---- rbCalcUPR ----
            rbCalcUPR.setText("text");
            rbCalcUPR.addItemListener(e -> rbCalcUPRItemStateChanged(e));
            pnlUPR.add(rbCalcUPR);

            //---- rbSetUPR ----
            rbSetUPR.setText("text");
            rbSetUPR.addItemListener(e -> rbSetUPRItemStateChanged(e));
            pnlUPR.add(rbSetUPR);

            //---- txtUPR ----
            txtUPR.setColumns(10);
            txtUPR.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtUPRFocusLost(e);
                }
            });
            pnlUPR.add(txtUPR);

            //---- lblTo1 ----
            lblTo1.setText("text");
            pnlUPR.add(lblTo1);
        }
        add(pnlUPR, CC.xywh(3, 7, 5, 1, CC.LEFT, CC.FILL));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- cbExpiresAfterOpened ----
            cbExpiresAfterOpened.setText("expiresAfterOpened");
            cbExpiresAfterOpened.addItemListener(e -> cbExpiresAfterOpenedItemStateChanged(e));
            panel1.add(cbExpiresAfterOpened);
            panel1.add(hSpacer1);

            //---- txtExpiresIn ----
            txtExpiresIn.setColumns(10);
            txtExpiresIn.setEnabled(false);
            txtExpiresIn.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtExpiresInFocusLost(e);
                }
            });
            panel1.add(txtExpiresIn);
            panel1.add(hSpacer2);

            //---- cmbDaysWeeks ----
            cmbDaysWeeks.setEnabled(false);
            cmbDaysWeeks.addItemListener(e -> cmbDaysWeeksItemStateChanged(e));
            panel1.add(cmbDaysWeeks);
        }
        add(panel1, CC.xywh(3, 9, 3, 1));

        //---- lbl1 ----
        lbl1.setText(null);
        lbl1.setIcon(new ImageIcon(getClass().getResource("/artwork/other/medicine2.png")));
        lbl1.setFont(new Font("Arial", Font.PLAIN, 18));
        add(lbl1, CC.xy(3, 15, CC.LEFT, CC.FILL));

        //---- cbWeightControlled ----
        cbWeightControlled.setText("text");
        cbWeightControlled.addItemListener(e -> cbWeightControlledItemStateChanged(e));
        add(cbWeightControlled, CC.xywh(3, 11, 3, 1));

        //---- lblMsg ----
        lblMsg.setText("text");
        lblMsg.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMsg.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lblMsg, CC.xywh(3, 13, 5, 1));

        //======== jsp1 ========
        {

            //---- lstDaf ----
            lstDaf.setFont(new Font("Arial", Font.PLAIN, 14));
            lstDaf.setVisible(false);
            lstDaf.addListSelectionListener(e -> lstDafValueChanged(e));
            jsp1.setViewportView(lstDaf);
        }
        add(jsp1, CC.xywh(5, 15, 3, 1, CC.DEFAULT, CC.FILL));

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(rbCalcUPR);
        buttonGroup1.add(rbSetUPR);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JXSearchField txtZusatz;
    private JComboBox cmbFormen;
    private JButton btnAdd;
    private JPanel pnlUPR;
    private JRadioButton rbCalcUPR;
    private JRadioButton rbSetUPR;
    private JTextField txtUPR;
    private JLabel lblTo1;
    private JPanel panel1;
    private JCheckBox cbExpiresAfterOpened;
    private JPanel hSpacer1;
    private JTextField txtExpiresIn;
    private JPanel hSpacer2;
    private JComboBox cmbDaysWeeks;
    private JLabel lbl1;
    private JCheckBox cbWeightControlled;
    private JLabel lblMsg;
    private JScrollPane jsp1;
    private JList lstDaf;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
