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

package op.care.med.structure;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.prescription.DosageForm;
import entity.prescription.DosageFormTools;
import entity.prescription.TradeForm;
import entity.prescription.TradeFormTools;
import op.OPDE;
import op.system.InternalClassACL;
import op.tools.GUITools;
import op.tools.MyJDialog;
import op.tools.Pair;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author root
 */
public class DlgTradeForm extends MyJDialog {
    private TradeForm tradeForm;
    private boolean initPhase;

    private void btnEditActionPerformed(ActionEvent e) {
        PnlDosageForm pnl = new PnlDosageForm((DosageForm) cmbForm.getSelectedItem());

        GUITools.showPopup(GUITools.createPanelPopup(pnl, new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    cmbForm.setModel(new DefaultComboBoxModel(new DosageForm[]{(DosageForm) o}));
                }
            }
        }, this), SwingConstants.SOUTH);
    }

    private void btnAddActionPerformed(ActionEvent e) {
        PnlDosageForm pnl = new PnlDosageForm(new DosageForm(0));

        GUITools.showPopup(GUITools.createPanelPopup(pnl, new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    cmbForm.setModel(new DefaultComboBoxModel(new DosageForm[]{(DosageForm) o}));
                }
            }
        }, this), SwingConstants.SOUTH);
    }


    private void cbExpiresAfterOpenedItemStateChanged(ItemEvent e) {
        if (initPhase) return;
        txtExpiresIn.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        cmbDaysWeeks.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            txtExpiresIn.setText("7");
            cmbDaysWeeks.setSelectedIndex(0);
            tradeForm.setDaysToExpireAfterOpened(7);
        } else {
            tradeForm.setDaysToExpireAfterOpened(null);
        }
    }

    private void txtExpiresInFocusLost(FocusEvent e) {
        if (initPhase) return;
        Integer i = SYSTools.checkInteger(txtExpiresIn.getText());
        if (i == null || i.compareTo(0) <= 0) {
            i = 7;
            txtExpiresIn.setText("7");
        }
        if (cmbDaysWeeks.getSelectedIndex() == 1) {
            tradeForm.setDaysToExpireAfterOpened(i * 7);
        } else {
            tradeForm.setDaysToExpireAfterOpened(i);
        }

    }

    private void cmbDaysWeeksItemStateChanged(ItemEvent e) {
        if (initPhase) return;
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Integer i = SYSTools.checkInteger(txtExpiresIn.getText());
            if (i == null || i.compareTo(0) <= 0) {
                i = 7;
                txtExpiresIn.setText("7");
            }
            if (cmbDaysWeeks.getSelectedIndex() == 1) {
                tradeForm.setDaysToExpireAfterOpened(i * 7);
            } else {
                tradeForm.setDaysToExpireAfterOpened(i);
            }
        }
    }

    public DlgTradeForm(TradeForm tradeForm) {
        super(false);
        this.tradeForm = tradeForm;
        initComponents();
        initDialog();
        pack();
        setVisible(true);
    }

    private void initDialog() {
        initPhase = true;
        cmbDaysWeeks.setModel(new DefaultComboBoxModel(new String[]{OPDE.lang.getString("misc.msg.Days"), OPDE.lang.getString("misc.msg.weeks")}));
        cbExpiresAfterOpened.setText(OPDE.lang.getString("tradeform.subtext.expiresAfterOpenedIn"));
        cbExpiresAfterOpened.setSelected(tradeForm.getDaysToExpireAfterOpened() != null);
        txtExpiresIn.setEnabled(cbExpiresAfterOpened.isSelected());
        cmbDaysWeeks.setEnabled(cbExpiresAfterOpened.isSelected());
        Pair<Integer, Integer> pair = TradeFormTools.getExpiresIn(tradeForm);
        if (pair != null) {
            txtExpiresIn.setText(pair.getFirst() > 0 ? pair.getFirst().toString() : pair.getSecond().toString());
            cmbDaysWeeks.setSelectedIndex(pair.getFirst() > 0 ? 0 : 1);
        }

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT m FROM DosageForm m ORDER BY m.preparation, m.usageText");
        cmbForm.setModel(new DefaultComboBoxModel(query.getResultList().toArray(new DosageForm[]{})));
        cmbForm.setRenderer(DosageFormTools.getRenderer(0));
        em.close();

        cmbForm.setSelectedItem(tradeForm.getDosageForm());
        txtZusatz.setText(SYSTools.catchNull(tradeForm.getSubtext()));

        btnAdd.setEnabled(OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, PnlMed.internalClassID));
        btnEdit.setEnabled(OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, PnlMed.internalClassID));
        initPhase = false;
    }


    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new JPanel();
        txtZusatz = new JTextField();
        cmbForm = new JComboBox();
        panel2 = new JPanel();
        btnAdd = new JButton();
        hSpacer1 = new JPanel(null);
        btnEdit = new JButton();
        panel4 = new JPanel();
        cbExpiresAfterOpened = new JCheckBox();
        hSpacer2 = new JPanel(null);
        txtExpiresIn = new JTextField();
        hSpacer3 = new JPanel(null);
        cmbDaysWeeks = new JComboBox();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnOK = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== jPanel1 ========
        {
            jPanel1.setLayout(new FormLayout(
                    "14dlu, $lcgap, default, $lcgap, default:grow, $lcgap, default, $lcgap, 14dlu",
                    "fill:14dlu, 2*($lgap, fill:default), $lgap, default, 2*($lgap, fill:default), $lgap, 14dlu"));

            //---- txtZusatz ----
            txtZusatz.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel1.add(txtZusatz, CC.xywh(3, 3, 5, 1));

            //---- cmbForm ----
            cmbForm.setModel(new DefaultComboBoxModel(new String[]{
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
            }));
            cmbForm.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel1.add(cmbForm, CC.xywh(3, 5, 3, 1));

            //======== panel2 ========
            {
                panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

                //---- btnAdd ----
                btnAdd.setText(null);
                btnAdd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                btnAdd.setBorder(null);
                btnAdd.setBorderPainted(false);
                btnAdd.setContentAreaFilled(false);
                btnAdd.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/pressed.png")));
                btnAdd.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnAddActionPerformed(e);
                    }
                });
                panel2.add(btnAdd);
                panel2.add(hSpacer1);

                //---- btnEdit ----
                btnEdit.setText(null);
                btnEdit.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/edit3.png")));
                btnEdit.setBorder(null);
                btnEdit.setBorderPainted(false);
                btnEdit.setContentAreaFilled(false);
                btnEdit.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/pressed.png")));
                btnEdit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnEditActionPerformed(e);
                    }
                });
                panel2.add(btnEdit);
            }
            jPanel1.add(panel2, CC.xy(7, 5));

            //======== panel4 ========
            {
                panel4.setLayout(new BoxLayout(panel4, BoxLayout.X_AXIS));

                //---- cbExpiresAfterOpened ----
                cbExpiresAfterOpened.setText("expiresAfterOpened");
                cbExpiresAfterOpened.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cbExpiresAfterOpenedItemStateChanged(e);
                    }
                });
                panel4.add(cbExpiresAfterOpened);
                panel4.add(hSpacer2);

                //---- txtExpiresIn ----
                txtExpiresIn.setColumns(10);
                txtExpiresIn.setEnabled(false);
                txtExpiresIn.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtExpiresInFocusLost(e);
                    }
                });
                panel4.add(txtExpiresIn);
                panel4.add(hSpacer3);

                //---- cmbDaysWeeks ----
                cmbDaysWeeks.setEnabled(false);
                cmbDaysWeeks.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cmbDaysWeeksItemStateChanged(e);
                    }
                });
                panel4.add(cmbDaysWeeks);
            }
            jPanel1.add(panel4, CC.xywh(3, 7, 5, 1));

            //======== panel1 ========
            {
                panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

                //---- btnCancel ----
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/cancel.png")));
                btnCancel.setText(null);
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancelActionPerformed(e);
                    }
                });
                panel1.add(btnCancel);

                //---- btnOK ----
                btnOK.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/apply.png")));
                btnOK.setText(null);
                btnOK.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnOKActionPerformed(e);
                    }
                });
                panel1.add(btnOK);
            }
            jPanel1.add(panel1, CC.xywh(5, 11, 3, 1, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(jPanel1);
        setSize(425, 220);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            TradeForm myTradeForm = em.merge(tradeForm);
            em.lock(myTradeForm, LockModeType.OPTIMISTIC);
            myTradeForm.setSubtext(txtZusatz.getText());
            DosageForm dosageForm = em.merge((DosageForm) cmbForm.getSelectedItem());
            em.lock(dosageForm, LockModeType.OPTIMISTIC);
            myTradeForm.setDosageForm(dosageForm);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        dispose();
    }//GEN-LAST:event_btnOKActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel1;
    private JTextField txtZusatz;
    private JComboBox cmbForm;
    private JPanel panel2;
    private JButton btnAdd;
    private JPanel hSpacer1;
    private JButton btnEdit;
    private JPanel panel4;
    private JCheckBox cbExpiresAfterOpened;
    private JPanel hSpacer2;
    private JTextField txtExpiresIn;
    private JPanel hSpacer3;
    private JComboBox cmbDaysWeeks;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnOK;
    // End of variables declaration//GEN-END:variables

}
