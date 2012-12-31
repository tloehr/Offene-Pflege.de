/*
 * Created by JFormDesigner on Thu Dec 20 15:01:22 CET 2012
 */

package op.system;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.prescription.MedStock;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.tools.CleanablePanel;
import op.tools.PrintListElement;

import javax.persistence.EntityManager;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterGraphics;

/**
 * @author Torsten Löhr
 */
public class PnlConfigs extends CleanablePanel {
    public static final String internalClassID = "opde.config";
    private MedStock testStock;

    public PnlConfigs(JScrollPane jspSearch) {
        jspSearch.setViewportView(new JPanel());
        initComponents();
        initPanel();
    }

    private void btnTestLabelActionPerformed(ActionEvent e) {
        LogicalPrinter localPrinter = OPDE.getLogicalPrinters().getMapName2LogicalPrinter().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_LOGICAL_PRINTER));
        PrinterForm printerForm1 = localPrinter.getForms().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL));

        OPDE.getPrintProcessor().addPrintJob(new PrintListElement(testStock, localPrinter, printerForm1, OPDE.getProps().getProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER)));
    }


    private void txtStockIDFocusLost(FocusEvent e) {

        try {
            long stockid = Long.parseLong(txtStockID.getText());
            EntityManager em = OPDE.createEM();
            testStock = em.find(MedStock.class, stockid);
            em.close();
        } catch (Exception e1) {
            testStock = null;

        }
        btnTestLabel.setEnabled(testStock != null);
    }

    private void txtStockIDActionPerformed(ActionEvent e) {
        btnTestLabel.requestFocus();
    }

    private void btnSaveActionPerformed(ActionEvent e) {

        PrintService printService = (PrintService) cmbPhysicalPrinters.getSelectedItem();
        if (printService != null) {
            OPDE.getProps().setProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER, printService.getName());
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER, printService.getName());
        }

        LogicalPrinter logicalPrinter = (LogicalPrinter) cmbLogicalPrinters.getSelectedItem();
        if (logicalPrinter != null) {
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_LOGICAL_PRINTER, logicalPrinter.getName());
            OPDE.getProps().setProperty(SYSPropsTools.KEY_LOGICAL_PRINTER, logicalPrinter.getName());
        }

        PrinterForm form = (PrinterForm) cmbForm.getSelectedItem();
        if (form != null){
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
            OPDE.getProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
        }

        OPDE.saveLocalProps();

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblPrinters = new JLabel();
        cmbPhysicalPrinters = new JComboBox();
        cmbLogicalPrinters = new JComboBox();
        cmbForm = new JComboBox();
        panel1 = new JPanel();
        txtStockID = new JTextField();
        hSpacer1 = new JPanel(null);
        btnTestLabel = new JButton();
        btnSave = new JButton();

        //======== this ========
        setLayout(new FormLayout(
                "default, $lcgap, default:grow, $lcgap, default",
                "6*(default, $lgap), default:grow"));

        //---- lblPrinters ----
        lblPrinters.setText("Etiketten-Drucker");
        lblPrinters.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblPrinters, CC.xy(3, 3));
        add(cmbPhysicalPrinters, CC.xy(3, 5));
        add(cmbLogicalPrinters, CC.xy(3, 7));
        add(cmbForm, CC.xy(3, 9));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- txtStockID ----
            txtStockID.setToolTipText("StockID");
            txtStockID.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtStockIDFocusLost(e);
                }
            });
            txtStockID.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtStockIDActionPerformed(e);
                }
            });
            panel1.add(txtStockID);
            panel1.add(hSpacer1);

            //---- btnTestLabel ----
            btnTestLabel.setText("Test");
            btnTestLabel.setEnabled(false);
            btnTestLabel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnTestLabelActionPerformed(e);
                }
            });
            panel1.add(btnTestLabel);
        }
        add(panel1, CC.xy(3, 11));

        //---- btnSave ----
        btnSave.setText("Save");
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnSaveActionPerformed(e);
            }
        });
        add(btnSave, CC.xy(3, 13, CC.RIGHT, CC.BOTTOM));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    @Override
    public void cleanup() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void reload() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getInternalClassID() {
        return internalClassID;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void initPanel() {
        testStock = null;

        cmbPhysicalPrinters.setModel(new DefaultComboBoxModel());
        cmbForm.setModel(new DefaultComboBoxModel());
        cmbLogicalPrinters.setModel(new DefaultComboBoxModel());

        PrintService[] prservices = PrintServiceLookup.lookupPrintServices(null, null);

        if (prservices != null) {

            cmbPhysicalPrinters.setModel(new DefaultComboBoxModel(prservices));

            cmbPhysicalPrinters.setRenderer(new ListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                    if (o == null)
                        return new DefaultListCellRenderer().getListCellRendererComponent(jList, OPDE.lang.getString("misc.msg.error"), i, isSelected, cellHasFocus);
                    return new DefaultListCellRenderer().getListCellRendererComponent(jList, ((PrintService) o).getName(), i, isSelected, cellHasFocus);
                }
            });
            cmbLogicalPrinters.setRenderer(new ListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                    if (o == null)
                        return new DefaultListCellRenderer().getListCellRendererComponent(jList, OPDE.lang.getString("misc.msg.error"), i, isSelected, cellHasFocus);
                    return new DefaultListCellRenderer().getListCellRendererComponent(jList, ((LogicalPrinter) o).getLabel(), i, isSelected, cellHasFocus);
                }
            });
            cmbForm.setRenderer(new ListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                    if (o == null)
                        return new DefaultListCellRenderer().getListCellRendererComponent(jList, OPDE.lang.getString("misc.msg.error"), i, isSelected, cellHasFocus);
                    return new DefaultListCellRenderer().getListCellRendererComponent(jList, ((PrinterForm) o).getLabel(), i, isSelected, cellHasFocus);
                }
            });

            cmbPhysicalPrinters.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        PrintService printService = (PrintService) cmbPhysicalPrinters.getSelectedItem();
                        OPDE.getProps().setProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER, printService.getName());
                        OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER, printService.getName());
                    }
                }
            });

            cmbLogicalPrinters.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
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
                }
            });

            cmbForm.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        OPDE.getProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
                        OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
                    }
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
            }


            if (!OPDE.getLogicalPrinters().getLogicalPrintersList().isEmpty()) {
                cmbLogicalPrinters.setModel(new DefaultComboBoxModel(OPDE.getLogicalPrinters().getLogicalPrintersList().toArray()));
                LogicalPrinter logicalPrinter = OPDE.getLogicalPrinters().getMapName2LogicalPrinter().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_LOGICAL_PRINTER));
                if (logicalPrinter == null) logicalPrinter = OPDE.getLogicalPrinters().getLogicalPrintersList().get(0);

                cmbLogicalPrinters.setSelectedItem(logicalPrinter);

                cmbForm.setModel(new DefaultComboBoxModel(logicalPrinter.getForms().values().toArray()));
                if (OPDE.getProps().containsKey(SYSPropsTools.KEY_MEDSTOCK_LABEL) && logicalPrinter.getForms().containsKey(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL))) {
                    cmbForm.setSelectedItem(logicalPrinter.getForms().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL)));
                } else {
                    cmbForm.setSelectedIndex(0);
                }
            }
        }

        txtStockID.setEnabled(prservices != null);
        btnTestLabel.setEnabled(prservices != null);
        cmbForm.setEnabled(prservices != null);
        cmbLogicalPrinters.setEnabled(prservices != null);
        cmbPhysicalPrinters.setEnabled(prservices != null);
        btnSave.setEnabled(prservices != null);
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblPrinters;
    private JComboBox cmbPhysicalPrinters;
    private JComboBox cmbLogicalPrinters;
    private JComboBox cmbForm;
    private JPanel panel1;
    private JTextField txtStockID;
    private JPanel hSpacer1;
    private JButton btnTestLabel;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
