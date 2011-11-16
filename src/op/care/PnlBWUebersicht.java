/*
 * OffenePflege
 * Copyright (C) 2008 Torsten Löhr
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
package op.care;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import entity.Bewohner;
import entity.BewohnerTools;
import op.OPDE;
import op.tools.SYSPrint;
import op.tools.SYSTools;

import java.awt.*;

/**
 * @author tloehr
 */
public class PnlBWUebersicht extends CleanablePanel {

    private Bewohner bewohner;

    /**
     * Creates new form PnlBWUebersicht
     */
    public PnlBWUebersicht(FrmPflege pflege, Bewohner bewohner) {
        initComponents();
        txtUebersicht.setContentType("text/html");
        change2Bewohner(bewohner);
    }

    public void cleanup() {
        SYSTools.unregisterListeners(this);
    }

    @Override
    public void change2Bewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
        BewohnerTools.setBWLabel(lblBW, bewohner);
        reloadDisplay();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jToolBar1 = new JToolBar();
        btnPrint = new JButton();
        lblBW = new JLabel();
        jspHTML = new JScrollPane();
        txtUebersicht = new JTextPane();
        jPanel1 = new JPanel();
        cbMedi = new JCheckBox();
        cbBilanz = new JCheckBox();
        cbBerichte = new JCheckBox();
        cbBWInfo = new JCheckBox();

        //======== this ========

        //======== jToolBar1 ========
        {
            jToolBar1.setFloatable(false);

            //---- btnPrint ----
            btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/printer.png")));
            btnPrint.setText("Drucken");
            btnPrint.setFocusable(false);
            btnPrint.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnPrintActionPerformed(e);
                }
            });
            jToolBar1.add(btnPrint);
        }

        //---- lblBW ----
        lblBW.setFont(new Font("Dialog", Font.BOLD, 18));
        lblBW.setForeground(new Color(255, 51, 0));
        lblBW.setText("jLabel3");

        //======== jspHTML ========
        {

            //---- txtUebersicht ----
            txtUebersicht.setEditable(false);
            jspHTML.setViewportView(txtUebersicht);
        }

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new BevelBorder(BevelBorder.RAISED));

            //---- cbMedi ----
            cbMedi.setText("Verordnungen");
            cbMedi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbMediActionPerformed(e);
                }
            });

            //---- cbBilanz ----
            cbBilanz.setSelected(true);
            cbBilanz.setText("Bilanz");
            cbBilanz.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbBilanzActionPerformed(e);
                }
            });

            //---- cbBerichte ----
            cbBerichte.setText("Berichte");
            cbBerichte.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbBerichteActionPerformed(e);
                }
            });

            //---- cbBWInfo ----
            cbBWInfo.setText("Informationen");
            cbBWInfo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbBWInfoActionPerformed(e);
                }
            });

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cbMedi)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbBilanz)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbBerichte)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbBWInfo)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(cbMedi)
                            .addComponent(cbBilanz)
                            .addComponent(cbBerichte)
                            .addComponent(cbBWInfo))
                        .addContainerGap())
            );
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblBW, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(jspHTML, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                        .addComponent(jPanel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lblBW)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jspHTML, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                    .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public void reloadDisplay() {
        txtUebersicht.setText(DBHandling.getUeberleitung(bewohner, false, false, cbMedi.isSelected(), cbBilanz.isSelected(), cbBerichte.isSelected(), true, false, false, true, cbBWInfo.isSelected()));
        jspHTML.getViewport().setViewPosition(new Point(0, 0));
    }

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        OPDE.ocmain.lockOC();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        SYSPrint.print(this, SYSTools.htmlUmlautConversion(DBHandling.getUeberleitung(bewohner, false, false, cbMedi.isSelected(), cbBilanz.isSelected(), cbBerichte.isSelected(), true, false, false, false, cbBWInfo.isSelected())), false);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void cbMediActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMediActionPerformed
        reloadDisplay();
    }//GEN-LAST:event_cbMediActionPerformed

    private void cbBilanzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBilanzActionPerformed
        reloadDisplay();
    }//GEN-LAST:event_cbBilanzActionPerformed

    private void cbBerichteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBerichteActionPerformed
        reloadDisplay();
    }//GEN-LAST:event_cbBerichteActionPerformed

    private void cbBWInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBWInfoActionPerformed
        reloadDisplay();
    }//GEN-LAST:event_cbBWInfoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JToolBar jToolBar1;
    private JButton btnPrint;
    private JLabel lblBW;
    private JScrollPane jspHTML;
    private JTextPane txtUebersicht;
    private JPanel jPanel1;
    private JCheckBox cbMedi;
    private JCheckBox cbBilanz;
    private JCheckBox cbBerichte;
    private JCheckBox cbBWInfo;
    // End of variables declaration//GEN-END:variables
}
