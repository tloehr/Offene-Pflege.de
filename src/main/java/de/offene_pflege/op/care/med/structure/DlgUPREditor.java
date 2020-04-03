/*
 * Created by JFormDesigner on Tue Apr 23 14:41:58 CEST 2013
 */

package de.offene_pflege.op.care.med.structure;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.backend.entity.prescription.MedStock;
import de.offene_pflege.backend.entity.prescription.MedStockTools;
import de.offene_pflege.backend.entity.prescription.TradeForm;
import de.offene_pflege.backend.entity.prescription.TradeFormTools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.*;
import org.apache.commons.collections.Closure;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Torsten Löhr
 */
public class DlgUPREditor extends MyJDialog {
    private static final String internalClassID = "upreditor";

    private TradeForm tradeForm;
    private ArrayList<MedStock> listStocks;
    private HashMap<MedStock, Pair<BigDecimal, BigDecimal>> mapEffectiveUPRs;
    private Closure afterAction;
    private JDialog currentEditor;

    public DlgUPREditor(TradeForm tradeForm, Closure afterAction) {
        super(false);
        this.tradeForm = tradeForm;
        this.afterAction = afterAction;
        initComponents();
        initPanel();
    }

    private void initPanel() {

        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {

                int progress = 0;
                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, 100));


                lblProduct.setText(tradeForm.getMedProduct().getText() + " " + TradeFormTools.toPrettyStringMedium(tradeForm));

                mapEffectiveUPRs = new HashMap<MedStock, Pair<BigDecimal, BigDecimal>>();

                rbUPRConst.setText(SYSTools.xx("upreditor.constant.upr"));
                rbUPRAuto.setText(SYSTools.xx("upreditor.calculated.upr"));


                //        Query query = em.createQuery("SELECT m FROM DosageForm m ORDER BY m.preparation, m.usageText");
                //        cmbDosageForm.setModel(new DefaultComboBoxModel(query.getResultList().toArray(new DosageForm[]{})));
                //        cmbDosageForm.setRenderer(DosageFormTools.getRenderer(0));

                if (tradeForm.getConstantUPRn() != null) {
                    txtUPR.setText(SYSTools.formatBigDecimal(tradeForm.getConstantUPRn().setScale(2, RoundingMode.HALF_UP)));
                    rbUPRConst.setSelected(true);
                } else {
                    txtSetUPR.setText(SYSTools.formatBigDecimal(MedStockTools.getEstimatedUPR(tradeForm).setScale(2, RoundingMode.HALF_UP)));
                    rbUPRAuto.setSelected(true);
                }

                EntityManager em = OPDE.createEM();
                Query query = em.createQuery("SELECT s FROM MedStock s WHERE s.tradeform = :tf ORDER BY s.in ");
                query.setParameter("tf", tradeForm);
                listStocks = new ArrayList<MedStock>(query.getResultList());
                em.close();

                // calculate effective UPRs for every closed stock for that tradeform
                for (MedStock stock : listStocks) {
                    progress++;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, listStocks.size()));
                    if (stock.isClosed()) {
                        mapEffectiveUPRs.put(stock, new Pair<BigDecimal, BigDecimal>(MedStockTools.getSumOfDosesInBHP(stock), MedStockTools.getEffectiveUPR(stock)));
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                tblStock.setModel(new MDLStock());
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);
                setVisible(true);
            }
        };
        worker.execute();


    }

    private void btnCloseActionPerformed(ActionEvent e) {
        afterAction.execute(null);
        dispose();
    }

    private void rbUPRConstItemStateChanged(ItemEvent e) {
        txtUPR.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (txtUPR.getText().isEmpty()) {
                txtUPR.setText("10");
            }
        } else {
            txtUPR.setText(null);
        }
    }

    private void txtUPRActionPerformed(ActionEvent e) {
        BigDecimal upr = checkUPR(txtUPR.getText());
        txtUPR.setText(SYSTools.formatBigDecimal(upr.setScale(2, RoundingMode.HALF_UP)));
    }

    private BigDecimal checkUPR(String text) {
        BigDecimal upr = SYSTools.parseDecimal(text);
        if (upr == null || upr.compareTo(BigDecimal.ZERO) <= 0) {
            upr = BigDecimal.TEN;
        }
        return upr;
    }

    private void txtUPRFocusLost(FocusEvent e) {
        BigDecimal upr = checkUPR(txtUPR.getText());
        txtUPR.setText(SYSTools.formatBigDecimal(upr.setScale(2, RoundingMode.HALF_UP)));
    }

    private void rbUPRAutoItemStateChanged(ItemEvent e) {
        txtSetUPR.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (txtSetUPR.getText().isEmpty()) {
                txtSetUPR.setText("10");
            }
        } else {
            txtSetUPR.setText(null);
        }
    }

    private void txtSetUPRActionPerformed(ActionEvent e) {
        BigDecimal upr = checkUPR(txtSetUPR.getText());
        txtSetUPR.setText(SYSTools.formatBigDecimal(upr.setScale(2, RoundingMode.HALF_UP)));
    }

    private void txtSetUPRFocusLost(FocusEvent e) {
        BigDecimal upr = checkUPR(txtSetUPR.getText());
        txtSetUPR.setText(SYSTools.formatBigDecimal(upr.setScale(2, RoundingMode.HALF_UP)));
    }

    private void btnSaveActionPerformed(ActionEvent e) {
        currentEditor = new DlgYesNo(SYSTools.xx("upreditor.changeupr.yesno"), SYSConst.icon48playerStop, answer -> {
            if (answer.equals(JOptionPane.YES_OPTION)) {
                SwingWorker worker = new SwingWorker() {

                    @Override
                    protected Object doInBackground() throws Exception {

                        btnSave.setEnabled(false);
                        btnClose.setEnabled(false);
                        rbUPRAuto.setEnabled(false);
                        rbUPRConst.setEnabled(false);
                        txtUPR.setEnabled(false);
                        txtSetUPR.setEnabled(false);

                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            if (rbUPRAuto.isSelected()) {

                                int progress = 0;
                                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, 100));

                                BigDecimal upr = checkUPR(txtSetUPR.getText());
                                TradeForm mytf = em.merge(tradeForm);
                                em.lock(mytf, LockModeType.OPTIMISTIC);
                                mytf.setUPR(null);

                                for (MedStock s : listStocks) {
                                    progress++;
                                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, listStocks.size()));
                                    MedStock stock = em.merge(s);
                                    em.lock(stock, LockModeType.OPTIMISTIC);
                                    em.lock(stock.getInventory(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    stock.setUPR(upr);
                                    stock.setUPRDummyMode(MedStockTools.ADD_TO_AVERAGES_UPR_WHEN_CLOSING); // no dummies after this has been set
                                }

                            } else {
                                BigDecimal upr = checkUPR(txtUPR.getText());
                                TradeForm mytf = em.merge(tradeForm);
                                em.lock(mytf, LockModeType.OPTIMISTIC);
                                mytf.setUPR(upr);
                            }
                            em.getTransaction().commit();

                        } catch (OptimisticLockException ole) {
                            OPDE.warn(ole);
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                        } catch (RollbackException ole) {
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                        } catch (Exception ex) {
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            OPDE.fatal(ex);
                        } finally {
                            em.close();
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        OPDE.getDisplayManager().setProgressBarMessage(null);
                        OPDE.getMainframe().setBlocked(false);
                        afterAction.execute(null);
                        currentEditor = null;
                        dispose();
                    }
                };
                worker.execute();
            }
        });
        currentEditor.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblProduct = new JLabel();
        panel3 = new JPanel();
        rbUPRAuto = new JRadioButton();
        txtSetUPR = new JTextField();
        scrollPane1 = new JScrollPane();
        tblStock = new JTable();
        panel1 = new JPanel();
        rbUPRConst = new JRadioButton();
        txtUPR = new JTextField();
        panel2 = new JPanel();
        panel4 = new JPanel();
        btnClose = new JButton();
        btnSave = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default, $lcgap, pref, $lcgap, default:grow, $lcgap, default",
            "6*(default, $lgap), default:grow, 2*($lgap, default)"));

        //---- lblProduct ----
        lblProduct.setText("Product here");
        lblProduct.setFont(lblProduct.getFont().deriveFont(lblProduct.getFont().getStyle() | Font.ITALIC, lblProduct.getFont().getSize() + 6f));
        contentPane.add(lblProduct, CC.xywh(3, 3, 3, 1));

        //======== panel3 ========
        {
            panel3.setLayout(new BoxLayout(panel3, BoxLayout.PAGE_AXIS));

            //---- rbUPRAuto ----
            rbUPRAuto.setText("UPR automatic");
            rbUPRAuto.addItemListener(e -> rbUPRAutoItemStateChanged(e));
            panel3.add(rbUPRAuto);

            //---- txtSetUPR ----
            txtSetUPR.addActionListener(e -> txtSetUPRActionPerformed(e));
            txtSetUPR.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtSetUPRFocusLost(e);
                }
            });
            panel3.add(txtSetUPR);
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
            rbUPRConst.addItemListener(e -> rbUPRConstItemStateChanged(e));
            panel1.add(rbUPRConst);

            //---- txtUPR ----
            txtUPR.addActionListener(e -> txtUPRActionPerformed(e));
            txtUPR.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtUPRFocusLost(e);
                }
            });
            panel1.add(txtUPR);
        }
        contentPane.add(panel1, CC.xy(3, 7));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));
        }
        contentPane.add(panel2, CC.xy(3, 13));

        //======== panel4 ========
        {
            panel4.setLayout(new BoxLayout(panel4, BoxLayout.LINE_AXIS));

            //---- btnClose ----
            btnClose.setText(null);
            btnClose.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnClose.addActionListener(e -> btnCloseActionPerformed(e));
            panel4.add(btnClose);

            //---- btnSave ----
            btnSave.setText(null);
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.addActionListener(e -> btnSaveActionPerformed(e));
            panel4.add(btnSave);
        }
        contentPane.add(panel4, CC.xy(5, 15, CC.RIGHT, CC.FILL));
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
            columnNames = new String[]{SYSTools.xx("upreditor.col1"), SYSTools.xx("upreditor.col2"), SYSTools.xx("upreditor.col3"), SYSTools.xx("upreditor.col4"), SYSTools.xx("upreditor.col5"), SYSTools.xx("upreditor.col6"), SYSTools.xx("upreditor.col7")};
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
                    result = listStocks.get(rowIndex).getInventory().getResident().getId();
                    break;
                }
                case 2: {
                    result = SYSTools.formatBigDecimal(listStocks.get(rowIndex).getUPR().setScale(2, RoundingMode.HALF_UP));
                    break;
                }
                case 3: {
                    result = DateFormat.getDateInstance().format(listStocks.get(rowIndex).getIN());
                    break;
                }
                case 4: {
                    result = SYSTools.formatBigDecimal(MedStockTools.getStartTX(listStocks.get(rowIndex)).getAmount().setScale(2, RoundingMode.HALF_UP));
                    break;
                }
                case 5: {
                    result = "OPEN";
                    if (mapEffectiveUPRs.containsKey(listStocks.get(rowIndex))) {
                        result = SYSTools.formatBigDecimal(mapEffectiveUPRs.get(listStocks.get(rowIndex)).getFirst().setScale(2, RoundingMode.HALF_UP));
                    }
                    break;
                }
                case 6: {
                    result = "OPEN";
                    if (mapEffectiveUPRs.containsKey(listStocks.get(rowIndex))) {
                        result = SYSTools.formatBigDecimal(mapEffectiveUPRs.get(listStocks.get(rowIndex)).getSecond().setScale(2, RoundingMode.HALF_UP));
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
    private JScrollPane scrollPane1;
    private JTable tblStock;
    private JPanel panel1;
    private JRadioButton rbUPRConst;
    private JTextField txtUPR;
    private JPanel panel2;
    private JPanel panel4;
    private JButton btnClose;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
