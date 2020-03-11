/*
 * OffenePflege
 * Copyright (C) 2006-2012 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License V2 as published by the Free Software Foundation
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------ 
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 * 
 */

package de.offene_pflege.op.care.med.inventory;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.entity.prescription.MedStockTools;
import de.offene_pflege.entity.prescription.MedStockTransaction;
import de.offene_pflege.entity.prescription.MedStockTransactionTools;
import de.offene_pflege.entity.prescription.TradeFormTools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.DocumentSizeFilter;
import de.offene_pflege.op.tools.MyJDialog;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;

/**
 *
 */
public class DlgTX extends MyJDialog {
    private BigDecimal amount, weight;
    private Closure actionBlock;
    private BigDecimal bestandsumme;
    private BigDecimal packgroesse;
    private MedStockTransaction tx;

    public DlgTX(MedStockTransaction tx, Closure actionBlock) {
        super();
        this.tx = tx;
        this.actionBlock = actionBlock;
        initDialog();
//        pack();
    }

    private void txtMengeFocusGained(FocusEvent e) {
        txtValue.selectAll();
    }

//    @Override
//    public void dispose() {
//        super.dispose();
//    }

    private void txtTextActionPerformed(ActionEvent e) {
        txtValue.requestFocus();
    }

    private void txtValueActionPerformed(ActionEvent e) {
        txtText.requestFocus();
    }

    boolean isAmountOk() {
        boolean amountOK = false;
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            amountOK = amount.negate().compareTo(bestandsumme) <= 0;
        } else if (amount.compareTo(BigDecimal.ZERO) > 0) {
            amountOK = amount.compareTo(packgroesse.subtract(bestandsumme)) <= 0;
        }
        return amountOK;
    }


    boolean isWeightOk() {
        if (!tx.getStock().getTradeForm().isWeightControlled()) return true;
        boolean weightOK = weight.compareTo(BigDecimal.ZERO) > 0;
        return weightOK;
    }

    private void txtWeightControlledFocusGained(FocusEvent e) {
        txtWeightControlled.selectAll();
    }

    private void txtWeightControlledCaretUpdate(CaretEvent evt) {
        weight = SYSTools.checkBigDecimal(evt);
        // https://github.com/tloehr/Offene-Pflege.de/issues/30
        if (weight == null) weight = BigDecimal.ZERO;
        OPDE.debug("weight = " + SYSTools.formatBigDecimal(weight));
        btnBuchung.setEnabled(isAmountOk() && isWeightOk());
    }

    private void txtValueCaretUpdate(CaretEvent evt) {
        amount = SYSTools.checkBigDecimal(evt);
        // https://github.com/tloehr/Offene-Pflege.de/issues/30
        if (amount == null) amount = BigDecimal.ZERO;

        btnBuchung.setEnabled(isAmountOk() && isWeightOk());
        OPDE.debug("amount = " + SYSTools.formatBigDecimal(amount));
    }

    private void thisWindowClosing(WindowEvent e) {
        tx = null;
    }

    private void updateCount(DefaultStyledDocument doc) {
        // http://stackoverflow.com/questions/13863795/enforce-max-characters-on-swing-jtextarea-with-a-few-curve-balls
        // https://github.com/tloehr/Offene-Pflege.de/issues/76
        lblLen.setText((100 - doc.getLength()) + " " + SYSTools.xx("misc.msg.characters.remaining"));
    }

    private void initDialog() {
        initComponents();
        lblText.setText(SYSTools.xx("opde.medication.tx.text"));

        // http://stackoverflow.com/questions/13863795/enforce-max-characters-on-swing-jtextarea-with-a-few-curve-balls
        // https://github.com/tloehr/Offene-Pflege.de/issues/76
        DefaultStyledDocument doc = new DefaultStyledDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(100));

        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateCount(doc);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCount(doc);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCount(doc);
            }
        });

        txtText.setDocument(doc);
        updateCount(doc);

        lblValue.setText(SYSTools.xx("misc.msg.amount"));
        lblWeightControl.setText(SYSTools.xx("opde.medication.tx.controlWeight"));
        lblUnit2.setText("g");
        bestandsumme = MedStockTools.getSum(tx.getStock());
        weight = null;
        txtWeightControlled.setVisible(tx.getStock().getTradeForm().isWeightControlled());
        lblWeightControl.setVisible(tx.getStock().getTradeForm().isWeightControlled());

        lblUnit.setText(TradeFormTools.getPackUnit(tx.getStock().getTradeForm()));

        if (tx.getStock().hasPackage()) {
            packgroesse = tx.getStock().getPackage().getContent();
        } else {
            packgroesse = BigDecimal.valueOf(Double.MAX_VALUE);
        }

        txtValue.setText(SYSTools.formatBigDecimal(tx.getAmount()));

        if (txtWeightControlled.isVisible()) {
            txtWeightControlled.setToolTipText(SYSTools.xx("opde.medication.controlWeight.only.after.change"));
            txtWeightControlled.setText(SYSTools.formatBigDecimal(tx.getWeight() != null ? tx.getWeight() : BigDecimal.ZERO));
        }
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        panel2 = new JPanel();
        lblText = new JLabel();
        lblLen = new JLabel();
        scrollPane1 = new JScrollPane();
        txtText = new JTextArea();
        txtValue = new JTextField();
        lblValue = new JLabel();
        lblUnit = new JLabel();
        lblWeightControl = new JLabel();
        txtWeightControlled = new JTextField();
        lblUnit2 = new JLabel();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnBuchung = new JButton();

        //======== this ========
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$ugap, $lcgap, default, $ugap, 141dlu:grow, $rgap, default, $lcgap, $ugap",
            "$ugap, $lgap, 34dlu, $lgap, fill:default, $lgap, default, $lgap, fill:default, $ugap"));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));

            //---- lblText ----
            lblText.setText("Buchungstext");
            lblText.setFont(new Font("Arial", Font.PLAIN, 14));
            panel2.add(lblText);

            //---- lblLen ----
            lblLen.setText("12");
            lblLen.setFont(new Font("Arial", Font.PLAIN, 14));
            panel2.add(lblLen);
        }
        contentPane.add(panel2, CC.xy(3, 3, CC.DEFAULT, CC.TOP));

        //======== scrollPane1 ========
        {

            //---- txtText ----
            txtText.setLineWrap(true);
            scrollPane1.setViewportView(txtText);
        }
        contentPane.add(scrollPane1, CC.xywh(5, 3, 3, 1, CC.FILL, CC.FILL));

        //---- txtValue ----
        txtValue.setHorizontalAlignment(SwingConstants.RIGHT);
        txtValue.setText("jTextField1");
        txtValue.setFont(new Font("Arial", Font.PLAIN, 14));
        txtValue.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtMengeFocusGained(e);
            }
        });
        txtValue.addActionListener(e -> txtValueActionPerformed(e));
        txtValue.addCaretListener(e -> txtValueCaretUpdate(e));
        contentPane.add(txtValue, CC.xy(5, 5));

        //---- lblValue ----
        lblValue.setText("Menge");
        lblValue.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblValue, CC.xy(3, 5));

        //---- lblUnit ----
        lblUnit.setHorizontalAlignment(SwingConstants.TRAILING);
        lblUnit.setText("jLabel4");
        lblUnit.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblUnit, CC.xy(7, 5));

        //---- lblWeightControl ----
        lblWeightControl.setText("Menge");
        lblWeightControl.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblWeightControl, CC.xy(3, 7));

        //---- txtWeightControlled ----
        txtWeightControlled.setHorizontalAlignment(SwingConstants.RIGHT);
        txtWeightControlled.setText("jTextField1");
        txtWeightControlled.setFont(new Font("Arial", Font.PLAIN, 14));
        txtWeightControlled.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtWeightControlledFocusGained(e);
            }
        });
        txtWeightControlled.addCaretListener(e -> txtWeightControlledCaretUpdate(e));
        contentPane.add(txtWeightControlled, CC.xy(5, 7));

        //---- lblUnit2 ----
        lblUnit2.setHorizontalAlignment(SwingConstants.TRAILING);
        lblUnit2.setText("g");
        lblUnit2.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblUnit2, CC.xy(7, 7, CC.LEFT, CC.DEFAULT));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- btnCancel ----
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnCancel.addActionListener(e -> btnCancelActionPerformed(e));
            panel1.add(btnCancel);

            //---- btnBuchung ----
            btnBuchung.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnBuchung.addActionListener(e -> btnBuchungActionPerformed(e));
            panel1.add(btnBuchung);
        }
        contentPane.add(panel1, CC.xywh(5, 9, 3, 1, CC.RIGHT, CC.DEFAULT));
        setSize(600, 195);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        tx = null;
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnBuchungActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnBuchungActionPerformed
        tx.setAmount(amount);
        tx.setWeight(weight);
        tx.setState(MedStockTransactionTools.STATE_EDIT_MANUAL);
        tx.setText(txtText.getText().trim());
        actionBlock.execute(tx);
        dispose();
    }//GEN-LAST:event_btnBuchungActionPerformed


    private void txtMengeCaretUpdate(CaretEvent evt) {//GEN-FIRST:event_txtMengeCaretUpdate


//        if (amount.compareTo(BigDecimal.ZERO) < 0) {
//            btnBuchung.setPanelEnabled(amount.negate().compareTo(bestandsumme) <= 0);
//        } else if (amount.compareTo(BigDecimal.ZERO) > 0) {
//            btnBuchung.setPanelEnabled(amount.compareTo(packgroesse.subtract(bestandsumme)) <= 0);
//        } else {
//            btnBuchung.setPanelEnabled(false);
//        }
    }//GEN-LAST:event_txtMengeCaretUpdate


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel panel2;
    private JLabel lblText;
    private JLabel lblLen;
    private JScrollPane scrollPane1;
    private JTextArea txtText;
    private JTextField txtValue;
    private JLabel lblValue;
    private JLabel lblUnit;
    private JLabel lblWeightControl;
    private JTextField txtWeightControlled;
    private JLabel lblUnit2;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnBuchung;
    // End of variables declaration//GEN-END:variables

}
