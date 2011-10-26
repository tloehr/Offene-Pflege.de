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
package op.care.berichte;

import entity.PBerichtTAGS;
import entity.PBerichtTAGSTools;
import entity.Pflegeberichte;
import entity.PflegeberichteTools;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Time;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author root
 */
public class DlgBericht extends javax.swing.JDialog {

    public final static int TAG_SOZIAL = 10;
    boolean dauerVeraendert = false;
    Pflegeberichte bericht;
    final Pflegeberichte editBericht;
    boolean init = true;

    /**
     *
     */
    public DlgBericht(Frame owner, Pflegeberichte bericht) {
        super(owner, true);
        this.bericht = bericht;
        // Dieser editBericht wird direkt vom Dialog bearbeitet.
        if (OPDE.getEM().contains(bericht)) {
            editBericht = PflegeberichteTools.copyBericht(bericht);
        } else {
            editBericht = bericht;
        }

        initComponents();

//        pnlTags.setViewportView(PBerichtTAGSTools.createCheckBoxPanelForTags(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                JCheckBox cb = (JCheckBox) e.getSource();
//                PBerichtTAGS tag = (PBerichtTAGS) cb.getClientProperty("UserObject");
//                if (e.getStateChange() == ItemEvent.DESELECTED) {
//                    editBericht.getTags().remove(tag);
//                } else {
//                    editBericht.getTags().add(tag);
//                }
//            }
//        }, bericht.getTags(), new GridLayout(0, 1)));

        setTitle(SYSTools.getWindowTitle("Pflegebericht"));
        int dauer = 3;
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);

        if (!OPDE.getEM().contains(bericht)) { // Bei Neueingabe

            Date now = new Date();
            jdcDatum.setDate(now);
            jdcDatum.setMaxSelectableDate(now);
            txtTBUhrzeit.setText(df.format(now));

            txtAreaTB.setText("");
            btnSave.setEnabled(false);

            editBericht.setPit(now);
            editBericht.setText("");
            editBericht.setDauer(dauer);

//            HashMap filter = new HashMap();
//            filter.put("BWKennung", new Object[]{bericht.getBewohner().getBWKennung(), "="});
//            filter.put("BIS", new Object[]{SYSConst.DATE_BIS_AUF_WEITERES, "="});
//            ResultSet rs = DBRetrieve.getResultSet("Vorgaenge", new String[]{"VorgangID", "Titel"}, filter, new String[]{"Titel"});
//
//            cmbVorgang.setModel(SYSTools.rs2cmb(rs, true));
        } else {

            cmbVorgang.setModel(new DefaultComboBoxModel());
            cmbVorgang.setEnabled(false);

            txtAreaTB.setText(editBericht.getText());

            jdcDatum.setDate(editBericht.getPit());

            txtTBUhrzeit.setText(df.format(editBericht.getPit()));
            dauer = editBericht.getDauer();

            btnSave.setEnabled(true);
        }
        txtDauer.setText(Integer.toString(dauer));

//        SYSTools.setXEnabled(pnlTags, true);

        init = false;
        SYSTools.centerOnParent(owner, this);
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        lblVital = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaTB = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jdcDatum = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        txtTBUhrzeit = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDauer = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cmbVorgang = new javax.swing.JComboBox();
        pnlTags = new javax.swing.JScrollPane();

        jLabel4.setText("jLabel4");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/cancel.png"))); // NOI18N
        btnCancel.setText("Abbrechen");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/apply.png"))); // NOI18N
        btnSave.setText("Speichern");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lblVital.setFont(new java.awt.Font("Dialog", 1, 18));
        lblVital.setText("Bericht");

        txtAreaTB.setColumns(20);
        txtAreaTB.setLineWrap(true);
        txtAreaTB.setRows(5);
        txtAreaTB.setWrapStyleWord(true);
        txtAreaTB.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtAreaTBCaretUpdate(evt);
            }
        });
        jScrollPane1.setViewportView(txtAreaTB);

        jLabel1.setText("Datum:");

        jdcDatum.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcDatumPropertyChange(evt);
            }
        });

        jLabel2.setText("Uhrzeit:");

        txtTBUhrzeit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTBUhrzeitFocusLost(evt);
            }
        });

        jLabel3.setText("Dauer:");

        txtDauer.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDauer.setText("3");
        txtDauer.setToolTipText("Dauer in Minuten");
        txtDauer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDauerFocusGained(evt);
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDauerFocusLost(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel5.setText("Vorgang:");

        cmbVorgang.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jdcDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtTBUhrzeit, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtDauer, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel5)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cmbVorgang, 0, 267, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(pnlTags, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(lblVital)))
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblVital)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(jLabel5)
                                        .addComponent(txtDauer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3)
                                        .addComponent(txtTBUhrzeit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2)
                                        .addComponent(jdcDatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1)
                                        .addComponent(cmbVorgang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                                        .addComponent(pnlTags, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE))
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnSave)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnCancel)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnCancel)
                                        .addComponent(btnSave))
                                .addContainerGap())
        );

        setSize(new java.awt.Dimension(885, 555));
    }// </editor-fold>//GEN-END:initComponents

    private void txtDauerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDauerFocusLost
        NumberFormat nf = NumberFormat.getIntegerInstance();
        String test = txtDauer.getText();
        int dauer = 3;
        try {
            Number num = nf.parse(test);
            dauer = num.intValue();
            if (dauer < 0) {
                dauer = 3;
                txtDauer.setText("3");
            }
        } catch (ParseException ex) {
            dauer = 3;
            txtDauer.setText("3");
        }
        editBericht.setDauer(dauer);
    }//GEN-LAST:event_txtDauerFocusLost

    private void txtTBUhrzeitFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTBUhrzeitFocusLost
        JTextField txt = (JTextField) evt.getComponent();
        String t = txt.getText();
        GregorianCalendar gc;
        try {
            gc = SYSCalendar.erkenneUhrzeit(t);
            txt.setText(SYSCalendar.toGermanTime(gc));

        } catch (NumberFormatException nfe) {
            gc = new GregorianCalendar();
            txt.setText(SYSCalendar.toGermanTime(gc));
            txt.requestFocusInWindow();
        }
        Time uhrzeit = new Time(gc.getTimeInMillis());
        editBericht.setPit(SYSCalendar.addTime2Date(jdcDatum.getDate(), uhrzeit));
    }//GEN-LAST:event_txtTBUhrzeitFocusLost

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        txtAreaTB.requestFocusInWindow();
    }//GEN-LAST:event_formWindowOpened

    private void txtAreaTBCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtAreaTBCaretUpdate
        editBericht.setText(txtAreaTB.getText());
        btnSave.setEnabled(!bericht.getText().equals(""));
    }//GEN-LAST:event_txtAreaTBCaretUpdate

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // Veränderung Sozialberichte müssen nun eine Zeitveränderung erfahren, wenn sie neu eingegeben werden.
        if (!OPDE.getEM().contains(bericht) && PBerichtTAGSTools.isSozial(bericht) && !dauerVeraendert) {
            JOptionPane.showMessageDialog(this, "Bei sozialen Berichten müssen Sie die Dauer verändern", "Sozial Bericht", JOptionPane.ERROR_MESSAGE);
        } else {
            if (OPDE.getEM().contains(bericht)) { // Der Bericht war schon in der Datenbank. Daher sind alle Änderungen als neuer Datensatz zu verwenden.

                PflegeberichteTools.changeBericht(bericht, editBericht);

            } else { // Bei Neueintrag (also tbid == 0) kann es noch sein, dass direkt ein Vorgang zur Zuordnung angegeben wurde.

//                    if (cmbVorgang.getSelectedIndex() > 0) {
//                        HashMap vassign = new HashMap();
//                        ListElement le = (ListElement) cmbVorgang.getSelectedItem();
//                        vassign.put("VorgangID", le.getPk());
//                        vassign.put("TableName", "Tagesberichte");
//                        vassign.put("ForeignKey", newtbid);
//                        vassign.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//                        if (op.tools.DBHandling.insertRecord("VorgangAssign", vassign) > 0) {
//                            op.share.vorgang.DBHandling.newVBericht(le.getPk(), "Neue Zuordnung wurde vorgenommen. TableName: Tagesberichte  Key: " + newtbid, op.share.vorgang.DBHandling.VBERICHT_ART_ASSIGN_ELEMENT);
//                        }
//                    }
                PflegeberichteTools.saveBericht(editBericht);
            }

            this.dispose();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtDauerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDauerFocusGained
        dauerVeraendert = true;
    }//GEN-LAST:event_txtDauerFocusGained

    private void jdcDatumPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcDatumPropertyChange
        if (!init && evt.getPropertyName().equals("date")) {
            Time uhrzeit = new Time(SYSCalendar.erkenneUhrzeit(txtTBUhrzeit.getText()).getTimeInMillis());
            editBericht.setPit(SYSCalendar.addTime2Date(jdcDatum.getDate(), uhrzeit));
        }
    }//GEN-LAST:event_jdcDatumPropertyChange

    public void dispose() {
        jdcDatum.cleanup();
        SYSTools.unregisterListeners(this);
        super.dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cmbVorgang;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser jdcDatum;
    private javax.swing.JLabel lblVital;
    private javax.swing.JScrollPane pnlTags;
    private javax.swing.JTextArea txtAreaTB;
    private javax.swing.JTextField txtDauer;
    private javax.swing.JTextField txtTBUhrzeit;
    // End of variables declaration//GEN-END:variables
}
