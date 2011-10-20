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
 */

package op.tools;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author tloehr
 */
public class DlgListSelector extends javax.swing.JDialog {
    private boolean apply;

    /**
     * Creates new form DlgFindeBW
     */
    public DlgListSelector(java.awt.Frame parent, String title, String topic, String detail, DefaultListModel dlm) {
        super(parent, true);
        initComponents();
        lstSelect.setModel(dlm);
        lstSelect.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstSelect.addListSelectionListener(new HandleSelections());
        btnApply.setEnabled(false);
        this.setTitle(title);
        lblTopic.setText(topic);
        lblDetail.setText(detail);
        SYSTools.centerOnParent(parent, this);
        apply = false;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTopic = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblDetail = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstSelect = new javax.swing.JList();
        btnCancel = new javax.swing.JButton();
        btnApply = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        lblTopic.setFont(new java.awt.Font("Dialog", 1, 14));
        lblTopic.setText("jLabel1");

        lblDetail.setFont(new java.awt.Font("Dialog", 0, 12));
        lblDetail.setText("jLabel2");

        lstSelect.setModel(new javax.swing.AbstractListModel() {
            String[] strings = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        lstSelect.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstSelectMouseClicked(evt);
            }
        });

        jScrollPane1.setViewportView(lstSelect);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
        btnCancel.setText("Abbrechen");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnApply.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
        btnApply.setText("W\u00e4hlen");
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, lblTopic, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, lblDetail, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                                        .add(layout.createSequentialGroup()
                                                .add(btnApply)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(btnCancel)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(lblTopic)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(lblDetail)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(btnCancel)
                                        .add(btnApply))
                                .addContainerGap())
        );
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - 600) / 2, (screenSize.height - 441) / 2, 600, 441);
    }// </editor-fold>//GEN-END:initComponents

    private void lstSelectMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstSelectMouseClicked
        if (evt.getClickCount() > 1) {
            btnApply.doClick();
        }
    }//GEN-LAST:event_lstSelectMouseClicked

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        super.setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    // dummy, damit keiner das fenster selbst sichtbar macht.
    public void setVisible(boolean b) {
    }

    public Object getSelection() {
        super.setVisible(true);
        Object result = null;
        if (apply) {
            ListModel lm = lstSelect.getModel();
            ListSelectionModel lsm = lstSelect.getSelectionModel();
            result = lm.getElementAt(lsm.getLeadSelectionIndex());
        }
        return result;
    }

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        apply = true;
        super.setVisible(false);
    }//GEN-LAST:event_btnApplyActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnCancel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblDetail;
    private javax.swing.JLabel lblTopic;
    private javax.swing.JList lstSelect;
    // End of variables declaration//GEN-END:variables

    class HandleSelections implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent lse) {
            // Erst reagieren wenn der Auswahl-Vorgang abgeschlossen ist.
            ListModel lm = lstSelect.getModel();

            if (lm.getSize() <= 0) {
                return;
            }

            if (!lse.getValueIsAdjusting()) {
                ListSelectionModel lsm = lstSelect.getSelectionModel();
                if (lsm.isSelectionEmpty()) {
                    btnApply.setEnabled(false);
                    //int selection = lsm.getLeadSelectionIndex();

                } else {
                    btnApply.setEnabled(true);
                }
            }

        }
    } // class HandleSelections
}