/*
 * Created by JFormDesigner on Thu Dec 20 15:01:22 CET 2012
 */

package op.system;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Station;
import entity.StationTools;
import entity.prescription.MedStock;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.tools.CleanablePanel;
import op.tools.PrintListElement;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigInteger;

/**
 * @author Torsten Löhr
 */
public class PnlConfigs extends CleanablePanel {
    public static final String internalClassID = "opde.config";
    private MedStock testStock;

    private boolean configsHaveBeenSaved = false;

    public PnlConfigs(JScrollPane jspSearch) {
        jspSearch.setViewportView(new JPanel());
        initComponents();
        initPanel();
    }

    private void btnTestLabelActionPerformed(ActionEvent e) {
        if (!configsHaveBeenSaved) return;

        try {
            //            long stockid = Long.parseLong(txtStockID.getText());
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
//
//
//    private void txtStockIDFocusLost(FocusEvent e) {
//        try {
////            long stockid = Long.parseLong(txtStockID.getText());
//            EntityManager em = OPDE.createEM();
//            Query query = em.createNativeQuery("SELECT BestID FROM medstock ORDER BY RAND() LIMIT 0,1");
//            if (!query.getResultList().isEmpty()) {
//                testStock = em.find(MedStock.class, query.getResultList().get(0));
//            }
//            em.close();
//        } catch (Exception e1) {
//            testStock = null;
//
//        }
//        btnTestLabel.setEnabled(testStock != null);
//    }
//
//    private void txtStockIDActionPerformed(ActionEvent e) {
//        btnTestLabel.requestFocus();
//    }

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
        if (form != null) {
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
            OPDE.getProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
        }

        OPDE.saveLocalProps();

        configsHaveBeenSaved = true;
    }

    private void cmbPrintersItemStateChanged(ItemEvent e) {
        configsHaveBeenSaved = false;
    }

    private void cmbStationItemStateChanged(ItemEvent e) {
        OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_STATION, ((Station) cmbStation.getSelectedItem()).getStatID().toString());
        OPDE.saveLocalProps();
        configsHaveBeenSaved = true;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblPrinters = new JLabel();
        lblStation = new JLabel();
        cmbPhysicalPrinters = new JComboBox();
        cmbStation = new JComboBox();
        cmbLogicalPrinters = new JComboBox();
        cmbForm = new JComboBox();
        btnTestLabel = new JButton();
        panel1 = new JPanel();
        btnSave = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "default, 2*($lcgap, default:grow), $lcgap, default",
            "6*(default, $lgap), default:grow, $lgap, default, $lgap, 14dlu"));

        //---- lblPrinters ----
        lblPrinters.setText("Etiketten-Drucker");
        lblPrinters.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblPrinters, CC.xy(3, 3));

        //---- lblStation ----
        lblStation.setText("Etiketten-Drucker");
        lblStation.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblStation, CC.xy(5, 3));
        add(cmbPhysicalPrinters, CC.xy(3, 5));

        //---- cmbStation ----
        cmbStation.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbStationItemStateChanged(e);
            }
        });
        add(cmbStation, CC.xy(5, 5));
        add(cmbLogicalPrinters, CC.xy(3, 7));
        add(cmbForm, CC.xy(3, 9));

        //---- btnTestLabel ----
        btnTestLabel.setText("Test");
        btnTestLabel.setEnabled(false);
        btnTestLabel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnTestLabelActionPerformed(e);
            }
        });
        add(btnTestLabel, CC.xy(3, 11));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));

            //---- btnSave ----
            btnSave.setText("Save");
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });
            panel1.add(btnSave);
        }
        add(panel1, CC.xywh(3, 15, 3, 1, CC.RIGHT, CC.DEFAULT));
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

//        txtStockID.setEnabled(prservices != null);
        btnTestLabel.setEnabled(prservices != null);
        cmbForm.setEnabled(prservices != null);
        cmbLogicalPrinters.setEnabled(prservices != null);
        cmbPhysicalPrinters.setEnabled(prservices != null);
        btnSave.setEnabled(prservices != null);


        lblPrinters.setText(OPDE.lang.getString(internalClassID + ".labelPrinters"));
        lblStation.setText(OPDE.lang.getString(internalClassID + ".station"));
        btnSave.setText(OPDE.lang.getString(internalClassID + ".btnsave"));

        cmbStation.setModel(StationTools.getAll4Combobox(false));
        cmbStation.setSelectedItem(StationTools.getStationForThisHost());

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblPrinters;
    private JLabel lblStation;
    private JComboBox cmbPhysicalPrinters;
    private JComboBox cmbStation;
    private JComboBox cmbLogicalPrinters;
    private JComboBox cmbForm;
    private JButton btnTestLabel;
    private JPanel panel1;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
