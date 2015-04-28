/*
 * Created by JFormDesigner on Tue Apr 28 15:59:43 CEST 2015
 */

package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.prescription.MedStock;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.system.LogicalPrinter;
import op.system.PrinterForm;
import op.tools.CleanablePanel;
import op.tools.PrintListElement;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.math.BigInteger;

/**
 * @author Torsten Löhr
 */
public class PnlLabelPrinterSetup extends CleanablePanel {
    private MedStock testStock;

    public PnlLabelPrinterSetup() {
        initComponents();
    }


    @Override
    public void cleanup() {

    }

    @Override
    public void reload() {

    }

    @Override
    public String getInternalClassID() {
        return null;
    }

    private void btnTestLabelActionPerformed(ActionEvent e) {
        try {
            EntityManager em = OPDE.createEM();
            Query query = em.createNativeQuery("SELECT BestID FROM medstock ORDER BY RAND() LIMIT 0,1");
            if (!query.getResultList().isEmpty()) {
                testStock = em.find(MedStock.class, ((BigInteger) query.getResultList().get(0)).longValue());
            }
            em.close();
        } catch (Exception e1) {
            testStock = null;

        }
        if (testStock == null) return;

        LogicalPrinter localPrinter = OPDE.getLogicalPrinters().getMapName2LogicalPrinter().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_LOGICAL_PRINTER));
        PrinterForm printerForm1 = localPrinter.getForms().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL));

        OPDE.getPrintProcessor().addPrintJob(new PrintListElement(testStock, localPrinter, printerForm1, OPDE.getProps().getProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER)));
    }

    private void cmbFormItemStateChanged(ItemEvent e) {
        PrinterForm form = (PrinterForm) cmbForm.getSelectedItem();
                if (form != null) {
                    OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
                    OPDE.getProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
                }
                OPDE.saveLocalProps();
    }

    private void cmbLogicalPrintersItemStateChanged(ItemEvent e) {
        LogicalPrinter logicalPrinter = (LogicalPrinter) cmbLogicalPrinters.getSelectedItem();
        if (logicalPrinter != null) {
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_LOGICAL_PRINTER, logicalPrinter.getName());
            OPDE.getProps().setProperty(SYSPropsTools.KEY_LOGICAL_PRINTER, logicalPrinter.getName());
        }
        OPDE.saveLocalProps();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        btnTestLabel = new JButton();
        cmbForm = new JComboBox();
        cmbLogicalPrinters = new JComboBox();
        cmbPhysicalPrinters = new JComboBox();

        //======== this ========
        setLayout(new FormLayout(
            "default:grow",
            "3*(default, $lgap), default"));

        //---- btnTestLabel ----
        btnTestLabel.setText("Test");
        btnTestLabel.setEnabled(false);
        btnTestLabel.addActionListener(e -> btnTestLabelActionPerformed(e));
        add(btnTestLabel, CC.xy(1, 7, CC.RIGHT, CC.DEFAULT));

        //---- cmbForm ----
        cmbForm.addItemListener(e -> cmbFormItemStateChanged(e));
        add(cmbForm, CC.xy(1, 5));

        //---- cmbLogicalPrinters ----
        cmbLogicalPrinters.addItemListener(e -> cmbLogicalPrintersItemStateChanged(e));
        add(cmbLogicalPrinters, CC.xy(1, 3));
        add(cmbPhysicalPrinters, CC.xy(1, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JButton btnTestLabel;
    private JComboBox cmbForm;
    private JComboBox cmbLogicalPrinters;
    private JComboBox cmbPhysicalPrinters;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
