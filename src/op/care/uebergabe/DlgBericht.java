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
package op.care.uebergabe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;
import com.toedter.calendar.*;
import entity.Homes;
import entity.Handover2User;
import entity.Handovers;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.jdesktop.swingx.JXErrorPane;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author root
 */
public class DlgBericht extends javax.swing.JDialog {

    private Homes einrichtung;
    public static final String internalClassID = "nursingrecords.handover.newreport";
    private PnlUebergabe parent;
    private boolean logout = false;

    /**
     * Creates new form DlgReport
     */
    public DlgBericht(PnlUebergabe parent, Homes einrichtung, Date datum) {
        super(parent.getPflege(), false);
//        ithis.parent = parent;
            this.einrichtung = einrichtung;
            initComponents();
            this.setTitle(SYSTools.getWindowTitle("Allgemeinen Bericht eingeben"));
            jdcDatum.setDate(datum);
            txtTBUhrzeit.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(datum));
            txtUebergabe.setText("");
            btnSave.setEnabled(false);
            SYSTools.centerOnParent(parent, this);
            this.setVisible(true);

        this.parent = parent;
            this.einrichtung = einrichtung;
            initComponents();
            this.setTitle(SYSTools.getWindowTitle("Allgemeinen Bericht eingeben"));
            jdcDatum.setDate(datum);
            txtTBUhrzeit.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(datum));
            txtUebergabe.setText("");
            btnSave.setEnabled(false);
            SYSTools.centerOnParent(parent, this);
            this.setVisible(true);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblVital = new JLabel();
        btnCancel = new JButton();
        btnSave = new JButton();
        jPanel1 = new JPanel();
        txtTBUhrzeit = new JTextField();
        jLabel2 = new JLabel();
        jdcDatum = new JDateChooser();
        jLabel1 = new JLabel();
        jScrollPane1 = new JScrollPane();
        txtUebergabe = new JTextPane();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                formWindowOpened(e);
            }
            @Override
            public void windowClosing(WindowEvent e) {
                formWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();

        //---- lblVital ----
        lblVital.setFont(new Font("Dialog", Font.BOLD, 18));
        lblVital.setText("Eintrag ins \u00dcbergabeprotokoll");

        //---- btnCancel ----
        btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
        btnCancel.setText("Abbrechen");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCancelActionPerformed(e);
            }
        });

        //---- btnSave ----
        btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
        btnSave.setText("Speichern");
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnSaveActionPerformed(e);
            }
        });

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

            //---- txtTBUhrzeit ----
            txtTBUhrzeit.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtTBUhrzeittxtUhrzeitHandler(e);
                }
            });

            //---- jLabel2 ----
            jLabel2.setText("Uhrzeit:");

            //---- jLabel1 ----
            jLabel1.setText("Datum:");

            //======== jScrollPane1 ========
            {

                //---- txtUebergabe ----
                txtUebergabe.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtUebergabeCaretUpdate(e);
                    }
                });
                jScrollPane1.setViewportView(txtUebergabe);
            }

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup()
                            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jdcDatum, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTBUhrzeit, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jdcDatum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(txtTBUhrzeit, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.LEADING, contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addComponent(lblVital)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE))
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addComponent(btnSave)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
                            .addComponent(btnCancel)))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblVital)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCancel)
                        .addComponent(btnSave))
                    .addContainerGap())
        );
        setSize(462, 379);
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtUebergabeCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtUebergabeCaretUpdate
        btnSave.setEnabled(!txtUebergabe.getText().equals(""));
    }//GEN-LAST:event_txtUebergabeCaretUpdate

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        txtUebergabe.requestFocusInWindow();
    }//GEN-LAST:event_formWindowOpened

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            Handovers bericht = new Handovers(SYSCalendar.addTime2Date(jdcDatum.getDate(), new Time(SYSCalendar.parseTime(txtTBUhrzeit.getText()).getTimeInMillis())), txtUebergabe.getText(), einrichtung, OPDE.getLogin().getUser());
            em.persist(bericht);
            // Der aktuelle User bestätigt direkt seinen eigenen Bericht.
            bericht.getUsersAcknowledged().add(new Handover2User(bericht, OPDE.getLogin().getUser()));
            em.merge(bericht);
            em.getTransaction().commit();
        } catch (Exception e) {
            JXErrorPane.showDialog(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        this.dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    @Override
    public void dispose() {

//        OPDE.removeModule(internalClassID);
        parent.reloadTable();

        jdcDatum.cleanup();
        SYSTools.unregisterListeners(this);
        super.dispose();
    }

    private void txtTBUhrzeittxtUhrzeitHandler(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTBUhrzeittxtUhrzeitHandler
        JTextField txt = (JTextField) evt.getComponent();
        String t = txt.getText();
        try {
            GregorianCalendar gc = SYSCalendar.parseTime(t);
            txt.setText(SYSCalendar.toGermanTime(gc));
        } catch (NumberFormatException nfe) {
            txt.setText(SYSCalendar.toGermanTime(new GregorianCalendar()));
            txt.requestFocusInWindow();
        }
    }//GEN-LAST:event_txtTBUhrzeittxtUhrzeitHandler

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel lblVital;
    private JButton btnCancel;
    private JButton btnSave;
    private JPanel jPanel1;
    private JTextField txtTBUhrzeit;
    private JLabel jLabel2;
    private JDateChooser jdcDatum;
    private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private JTextPane txtUebergabe;
    // End of variables declaration//GEN-END:variables
}
