/*
 * Created by JFormDesigner on Tue Apr 28 15:59:43 CEST 2015
 */

package de.offene_pflege.op.settings.subpanels;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.backend.entity.done.MedStock;
import de.offene_pflege.backend.entity.system.SYSPropsTools;
import de.offene_pflege.gui.interfaces.DefaultPanel;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.system.LogicalPrinter;
import de.offene_pflege.op.system.PrinterForm;
import de.offene_pflege.op.tools.PrintListElement;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.math.BigInteger;

/**
 * @author Torsten Löhr
 */
public class PnlLabelPrinterSetup extends DefaultPanel {
    private MedStock testStock;

    public PnlLabelPrinterSetup() {
        super("opde.settings.labelPrinters");
        initComponents();
        initPanel();
    }

    private void initPanel() {
        testStock = null;

        cmbPhysicalPrinters.setModel(new DefaultComboBoxModel());
        cmbForm.setModel(new DefaultComboBoxModel());
        cmbLogicalPrinters.setModel(new DefaultComboBoxModel());

        //todo: seit macos sierra funktioniert das so gut wie nicht mehr.
        PrintService[] prservices = PrintServiceLookup.lookupPrintServices(null, null);

        // this prevents exceptions when there are no printers installed on the OS yet
        if (prservices != null && prservices.length == 0) {
            prservices = null;
        }

        if (prservices != null) {
            cmbPhysicalPrinters.setModel(new DefaultComboBoxModel(prservices));
            cmbPhysicalPrinters.setRenderer((jList, o, i, isSelected, cellHasFocus) -> {
                if (o == null)
                    return new DefaultListCellRenderer().getListCellRendererComponent(jList, SYSTools.xx("misc.msg.error"), i, isSelected, cellHasFocus);
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, ((PrintService) o).getName(), i, isSelected, cellHasFocus);
            });

            cmbLogicalPrinters.setRenderer((jList, o, i, isSelected, cellHasFocus) -> {
                if (o == null)
                    return new DefaultListCellRenderer().getListCellRendererComponent(jList, SYSTools.xx("misc.msg.error"), i, isSelected, cellHasFocus);
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, ((LogicalPrinter) o).getLabel(), i, isSelected, cellHasFocus);
            });
            cmbForm.setRenderer((jList, o, i, isSelected, cellHasFocus) -> {
                if (o == null)
                    return new DefaultListCellRenderer().getListCellRendererComponent(jList, SYSTools.xx("misc.msg.error"), i, isSelected, cellHasFocus);
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, ((PrinterForm) o).getLabel(), i, isSelected, cellHasFocus);
            });

            cmbPhysicalPrinters.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    PrintService printService = (PrintService) cmbPhysicalPrinters.getSelectedItem();
                    OPDE.getProps().setProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER, printService.getName());
                    OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER, printService.getName());
                    OPDE.saveLocalProps();
                }

            });

            cmbLogicalPrinters.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    LogicalPrinter logicalPrinter = (LogicalPrinter) cmbLogicalPrinters.getSelectedItem();
                    cmbForm.setModel(new DefaultComboBoxModel(logicalPrinter.getForms().values().toArray()));
                    if (OPDE.getProps().containsKey(SYSPropsTools.KEY_MEDSTOCK_LABEL) && logicalPrinter.getForms().containsKey(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL))) {
                        cmbForm.setSelectedItem(logicalPrinter.getForms().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL)));
                    }
                    OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_LOGICAL_PRINTER, logicalPrinter.getName());
                    OPDE.getProps().setProperty(SYSPropsTools.KEY_LOGICAL_PRINTER, logicalPrinter.getName());
                    OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
                    OPDE.getProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());

                }
            });

            cmbForm.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    OPDE.getProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
                    OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
                }

            });

            if (OPDE.getPrintProcessor().isWorking()) {
                cmbLogicalPrinters.setModel(new DefaultComboBoxModel(OPDE.getLogicalPrinters().getLogicalPrintersList().toArray()));
                LogicalPrinter logicalPrinter = OPDE.getLogicalPrinters().getMapName2LogicalPrinter().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_LOGICAL_PRINTER));
                cmbLogicalPrinters.setSelectedItem(logicalPrinter);

                cmbForm.setModel(new DefaultComboBoxModel(logicalPrinter.getForms().values().toArray()));
                cmbForm.setSelectedItem(logicalPrinter.getForms().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL)));
            }
            if (OPDE.getProps().containsKey(SYSPropsTools.KEY_PHYSICAL_PRINTER) && OPDE.getLogicalPrinters().getPrintService(OPDE.getProps().getProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER)) != null) {
                cmbPhysicalPrinters.setSelectedItem(OPDE.getLogicalPrinters().getPrintService(OPDE.getProps().getProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER)));
            } else {
                PrintService printService = (PrintService) cmbPhysicalPrinters.getSelectedItem();
                OPDE.getProps().setProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER, printService.getName());
                OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER, printService.getName());

            }

            if (!OPDE.getLogicalPrinters().getLogicalPrintersList().isEmpty()) {
                cmbLogicalPrinters.setModel(new DefaultComboBoxModel(OPDE.getLogicalPrinters().getLogicalPrintersList().toArray()));
                LogicalPrinter logicalPrinter = OPDE.getLogicalPrinters().getMapName2LogicalPrinter().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_LOGICAL_PRINTER));
                if (logicalPrinter == null) logicalPrinter = OPDE.getLogicalPrinters().getLogicalPrintersList().get(0);

                if (!OPDE.getProps().containsKey(SYSPropsTools.KEY_LOGICAL_PRINTER)) {
                    OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_LOGICAL_PRINTER, logicalPrinter.getName());
                    OPDE.getProps().setProperty(SYSPropsTools.KEY_LOGICAL_PRINTER, logicalPrinter.getName());

                }

                cmbLogicalPrinters.setSelectedItem(logicalPrinter);

                cmbForm.setModel(new DefaultComboBoxModel(logicalPrinter.getForms().values().toArray()));
                if (OPDE.getProps().containsKey(SYSPropsTools.KEY_MEDSTOCK_LABEL) && logicalPrinter.getForms().containsKey(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL))) {
                    cmbForm.setSelectedItem(logicalPrinter.getForms().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL)));
                } else {
                    cmbForm.setSelectedIndex(0);
                    OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
                    OPDE.getProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());

                }
            }
        } else {
            cmbPhysicalPrinters.setEnabled(false);
            cmbLogicalPrinters.setEnabled(false);
            cmbPhysicalPrinters.setEnabled(false);
        }

        btnTestLabel.setEnabled(prservices != null);
        cmbForm.setEnabled(prservices != null);
        cmbLogicalPrinters.setEnabled(prservices != null);
        cmbPhysicalPrinters.setEnabled(prservices != null);


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
