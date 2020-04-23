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

package de.offene_pflege.op.care.med.structure;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.backend.entity.done.MedPackage;
import de.offene_pflege.backend.services.MedPackageService;
import de.offene_pflege.backend.services.TradeFormTools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.MyJDialog;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;

/**
 * @author tloehr
 */
public class DlgPack extends MyJDialog {
    private MedPackage aPackage;

    /**
     * Creates new form DlgPack
     */
    public DlgPack(String title, MedPackage aPackage) {
        super(false);
        initComponents();
        setTitle(title);
        this.aPackage = aPackage;
        cmbGroesse.setModel(new DefaultComboBoxModel(MedPackageService.GROESSE));

        if (aPackage.getID() != null) {
            txtPZN.setText(SYSTools.catchNull(aPackage.getPzn()));
            txtInhalt.setText(SYSTools.formatBigDecimal(aPackage.getContent().setScale(2, BigDecimal.ROUND_HALF_UP)));
            cmbGroesse.setSelectedIndex(aPackage.getSize());
        }
        lblPackEinheit.setText(TradeFormTools.getPackUnit(aPackage.getTradeForm()));
//        SYSTools.centerOnParent(parent, this);
        pack();
//        setVisible(true);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Erzeugter Quelltext ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblPZN = new JLabel();
        cmbGroesse = new JComboBox<>();
        jLabel3 = new JLabel();
        txtPZN = new JTextField();
        lblInhalt = new JLabel();
        txtInhalt = new JTextField();
        lblPackEinheit = new JLabel();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnOK = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "14dlu, $lcgap, default, 2*($lcgap, default:grow), $lcgap, default, $lcgap, 14dlu",
            "14dlu, 5*($lgap, fill:default), $lgap, 14dlu"));

        //---- lblPZN ----
        lblPZN.setText("PZN:");
        lblPZN.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblPZN, CC.xy(3, 3));

        //---- cmbGroesse ----
        cmbGroesse.setModel(new DefaultComboBoxModel<>(new String[] {
            "Item 1",
            "Item 2",
            "Item 3",
            "Item 4"
        }));
        cmbGroesse.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(cmbGroesse, CC.xywh(5, 5, 5, 1));

        //---- jLabel3 ----
        jLabel3.setText("Gr\u00f6\u00dfe:");
        jLabel3.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(jLabel3, CC.xy(3, 5));

        //---- txtPZN ----
        txtPZN.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPZN.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtPZNFocusGained(e);
            }
        });
        contentPane.add(txtPZN, CC.xywh(5, 3, 5, 1));

        //---- lblInhalt ----
        lblInhalt.setText("Inhalt:");
        lblInhalt.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblInhalt, CC.xy(3, 7));

        //---- txtInhalt ----
        txtInhalt.setHorizontalAlignment(SwingConstants.RIGHT);
        txtInhalt.setText("0");
        txtInhalt.setFont(new Font("Arial", Font.PLAIN, 14));
        txtInhalt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtInhaltFocusGained(e);
            }
        });
        contentPane.add(txtInhalt, CC.xywh(5, 7, 3, 1));

        //---- lblPackEinheit ----
        lblPackEinheit.setText("jLabel5");
        lblPackEinheit.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblPackEinheit, CC.xy(9, 7));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- btnCancel ----
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/cancel.png")));
            btnCancel.setText(null);
            btnCancel.addActionListener(e -> btnCancelActionPerformed(e));
            panel1.add(btnCancel);

            //---- btnOK ----
            btnOK.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/apply.png")));
            btnOK.setText(null);
            btnOK.addActionListener(e -> btnOKActionPerformed(e));
            panel1.add(btnOK);
        }
        contentPane.add(panel1, CC.xywh(5, 11, 5, 1, CC.RIGHT, CC.DEFAULT));
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void txtInhaltFocusGained(FocusEvent evt) {//GEN-FIRST:event_txtInhaltFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtInhaltFocusGained

    private void txtPZNFocusGained(FocusEvent evt) {//GEN-FIRST:event_txtPZNFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtPZNFocusGained

    private void btnOKActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed

        String pzn = MedPackageService.checkNewPZN(txtPZN.getText().trim(), aPackage.getID() != null ? aPackage : null);
        BigDecimal inhalt = SYSTools.parseDecimal(txtInhalt.getText());
        if (inhalt != null && inhalt.compareTo(BigDecimal.ZERO) <= 0) {
            inhalt = null;
        }

        String txt = "";

        if (pzn == null) {
            lblPZN.setIcon(SYSConst.icon22delete);
        } else {
            lblPZN.setIcon(null);
        }

        if (inhalt == null) {
            lblInhalt.setIcon(SYSConst.icon22delete);
        } else {
            lblInhalt.setIcon(null);
        }


        if (pzn != null && inhalt != null) {
            // TODO: locking ? do it better.
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                MedPackage myPackage = em.merge(aPackage);
                myPackage.setPzn(pzn);
                myPackage.setSize((short) cmbGroesse.getSelectedIndex());
                myPackage.setContent(inhalt);
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
        }

    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed


    // Variablendeklaration - nicht modifizieren//GEN-BEGIN:variables
    private JLabel lblPZN;
    private JComboBox<String> cmbGroesse;
    private JLabel jLabel3;
    private JTextField txtPZN;
    private JLabel lblInhalt;
    private JTextField txtInhalt;
    private JLabel lblPackEinheit;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnOK;
    // Ende der Variablendeklaration//GEN-END:variables

}
