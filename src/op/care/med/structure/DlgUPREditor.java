/*
 * Created by JFormDesigner on Tue Apr 23 14:41:58 CEST 2013
 */

package op.care.med.structure;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.prescription.*;
import op.OPDE;
import op.tools.MyJDialog;
import op.tools.Pair;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Torsten Löhr
 */
public class DlgUPREditor extends MyJDialog {
    private TradeForm tradeForm;
    private ArrayList<MedStock> listStocks;
    private HashMap<MedStock, Pair<BigDecimal, BigDecimal>> mapEffectiveUPRs;

    public DlgUPREditor(TradeForm tradeForm) {
        super(false);
        this.tradeForm = tradeForm;
        initComponents();
        initPanel();
//        pack();
        setVisible(true);
    }

    private void initPanel() {
        lblProduct.setText(tradeForm.getMedProduct().getText() + " " + TradeFormTools.toPrettyStringMedium(tradeForm));
//        lblTradeForm.setText(tradeForm.getSubtext());

        mapEffectiveUPRs = new HashMap<MedStock, Pair<BigDecimal, BigDecimal>>();

        EntityManager em = OPDE.createEM();

        Query query = em.createQuery("SELECT m FROM DosageForm m ORDER BY m.preparation, m.usageText");
        cmbDosageForm.setModel(new DefaultComboBoxModel(query.getResultList().toArray(new DosageForm[]{})));
        cmbDosageForm.setRenderer(DosageFormTools.getRenderer(0));

        if (tradeForm.getUpr() != null) {
            txtUPR.setText(tradeForm.getUpr().setScale(2, RoundingMode.HALF_UP).toString());
            rbUPRConst.setSelected(true);
        } else {
            txtSetUPR.setText(MedStockTools.getEstimatedUPR(tradeForm, null).setScale(2, RoundingMode.HALF_UP).toString());
            rbUPRAuto.setSelected(true);
        }

        query = em.createQuery("SELECT s FROM MedStock s WHERE s.tradeform = :tf ORDER BY s.in ");
        query.setParameter("tf", tradeForm);
        listStocks = new ArrayList<MedStock>(query.getResultList());

        em.close();


        // calculate effective UPRs for every closed stock for that tradeform
        for (MedStock stock : listStocks) {
            if (stock.isClosed()) {
                mapEffectiveUPRs.put(stock, new Pair<BigDecimal, BigDecimal>(MedStockTools.getSumOfDosesInBHP(stock), MedStockTools.getEffectiveUPR(stock)));
            }
        }

        tblStock.setModel(new MDLStock());

        cmbDosageForm.setSelectedItem(tradeForm.getDosageForm());
    }

    private void btnCloseActionPerformed(ActionEvent e) {
        dispose();
    }

    private void rbUPRConstItemStateChanged(ItemEvent e) {
        txtUPR.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            txtUPR.setText("10");
            tradeForm.setUpr(BigDecimal.TEN);
        } else {
            txtUPR.setText(null);
            tradeForm.setUpr(null);
        }
    }

    private void txtUPRActionPerformed(ActionEvent e) {
        checkUPR();
    }

    private void checkUPR() {
        BigDecimal upr = SYSTools.checkBigDecimal(txtUPR.getText());
        if (upr == null || upr.compareTo(BigDecimal.ZERO) <= 0) {
            upr = BigDecimal.TEN;
            txtUPR.setText("10");
        } else {
            txtUPR.setText(upr.setScale(2, RoundingMode.HALF_UP).toString());
        }
        tradeForm.setUpr(upr);
    }

    private void txtUPRFocusLost(FocusEvent e) {
        checkUPR();
    }

    private void rbUPRAutoItemStateChanged(ItemEvent e) {
        txtSetUPR.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        btnSetUPR.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            txtSetUPR.setText("10");
        } else {
            txtSetUPR.setText(null);
        }
    }

    private void btnSetUPRActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblProduct = new JLabel();
        panel3 = new JPanel();
        rbUPRAuto = new JRadioButton();
        txtSetUPR = new JTextField();
        btnSetUPR = new JButton();
        scrollPane1 = new JScrollPane();
        tblStock = new JTable();
        panel1 = new JPanel();
        rbUPRConst = new JRadioButton();
        txtUPR = new JTextField();
        lblDosageForm = new JLabel();
        cmbDosageForm = new JComboBox();
        panel2 = new JPanel();
        btnClose = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default, $lcgap, pref, $lcgap, default:grow, $lcgap, default",
            "6*(default, $lgap), default:grow, $lgap, default"));

        //---- lblProduct ----
        lblProduct.setText("Product here");
        lblProduct.setFont(lblProduct.getFont().deriveFont(lblProduct.getFont().getStyle() | Font.ITALIC, lblProduct.getFont().getSize() + 4f));
        contentPane.add(lblProduct, CC.xywh(3, 3, 3, 1));

        //======== panel3 ========
        {
            panel3.setLayout(new FormLayout(
                "pref",
                "2*(default, $lgap), default"));

            //---- rbUPRAuto ----
            rbUPRAuto.setText("UPR automatic");
            rbUPRAuto.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    rbUPRAutoItemStateChanged(e);
                }
            });
            panel3.add(rbUPRAuto, CC.xy(1, 1));
            panel3.add(txtSetUPR, CC.xy(1, 3));

            //---- btnSetUPR ----
            btnSetUPR.setText("setUPR");
            btnSetUPR.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSetUPRActionPerformed(e);
                }
            });
            panel3.add(btnSetUPR, CC.xy(1, 5));
        }
        contentPane.add(panel3, CC.xy(3, 5));

        //======== scrollPane1 ========
        {

            //---- tblStock ----
            tblStock.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            scrollPane1.setViewportView(tblStock);
        }
        contentPane.add(scrollPane1, CC.xywh(5, 5, 1, 9));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));

            //---- rbUPRConst ----
            rbUPRConst.setText("UPR constant");
            rbUPRConst.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    rbUPRConstItemStateChanged(e);
                }
            });
            panel1.add(rbUPRConst);

            //---- txtUPR ----
            txtUPR.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtUPRActionPerformed(e);
                }
            });
            txtUPR.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtUPRFocusLost(e);
                }
            });
            panel1.add(txtUPR);
        }
        contentPane.add(panel1, CC.xy(3, 7));

        //---- lblDosageForm ----
        lblDosageForm.setText("DosageForm here");
        contentPane.add(lblDosageForm, CC.xy(3, 9));
        contentPane.add(cmbDosageForm, CC.xy(3, 11));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));

            //---- btnClose ----
            btnClose.setText("cancel");
            btnClose.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnCloseActionPerformed(e);
                }
            });
            panel2.add(btnClose);
        }
        contentPane.add(panel2, CC.xy(3, 13));
        setSize(970, 490);
        setLocationRelativeTo(getOwner());

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(rbUPRAuto);
        buttonGroup1.add(rbUPRConst);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private class MDLStock extends AbstractTableModel {

        String[] columnNames;

        private MDLStock() {
            columnNames = new String[]{"BestID", "ResID", "Stock UPR", "IN", "StartValue", "SumDoses", "UPReff"};
        }

        @Override
        public int getRowCount() {
            return listStocks.size();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public int getColumnCount() {
            return 7;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object result = null;

            switch (columnIndex) {
                case 0: {
                    result = listStocks.get(rowIndex).getID();
                    break;
                }
                case 1: {
                    result = listStocks.get(rowIndex).getInventory().getResident().getRID();
                    break;
                }
                case 2: {
                    result = listStocks.get(rowIndex).getUPR().setScale(2, RoundingMode.HALF_UP).toString();
                    break;
                }
                case 3: {
                    result = DateFormat.getDateInstance().format(listStocks.get(rowIndex).getIN());
                    break;
                }
                case 4: {
                    result = MedStockTools.getStartTX(listStocks.get(rowIndex)).getAmount().setScale(2, RoundingMode.HALF_UP).toString();
                    break;
                }
                case 5: {
                    result = "OPEN";
                    if (mapEffectiveUPRs.containsKey(listStocks.get(rowIndex))) {
                        result = mapEffectiveUPRs.get(listStocks.get(rowIndex)).getFirst().setScale(2, RoundingMode.HALF_UP).toString();
                    }
                    break;
                }
                case 6: {
                    result = "OPEN";
                    if (mapEffectiveUPRs.containsKey(listStocks.get(rowIndex))) {
                        result = mapEffectiveUPRs.get(listStocks.get(rowIndex)).getSecond().setScale(2, RoundingMode.HALF_UP).toString();
                    }
                    break;
                }
                default: {
                    result = "Fuck it!";
                }
            }
            return result;
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblProduct;
    private JPanel panel3;
    private JRadioButton rbUPRAuto;
    private JTextField txtSetUPR;
    private JButton btnSetUPR;
    private JScrollPane scrollPane1;
    private JTable tblStock;
    private JPanel panel1;
    private JRadioButton rbUPRConst;
    private JTextField txtUPR;
    private JLabel lblDosageForm;
    private JComboBox cmbDosageForm;
    private JPanel panel2;
    private JButton btnClose;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
