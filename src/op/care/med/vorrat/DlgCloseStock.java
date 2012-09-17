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
package op.care.med.vorrat;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.prescription.*;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.MyJDialog;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.eclipse.persistence.exceptions.OptimisticLockException;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;

/**
 * @author tloehr
 */
public class DlgCloseStock extends MyJDialog {


    private MedStock bestand;
    private Closure actionBlock;

    /**
     * Creates new form DlgBestandAnbruch
     */
    public DlgCloseStock(MedStock bestand, Closure actionBlock) {
        super();
        this.actionBlock = actionBlock;
        this.bestand = bestand;
        initComponents();
        initDialog();
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        jScrollPane1 = new JScrollPane();
        txtInfo = new JTextPane();
        rbLeer = new JRadioButton();
        rbStellen = new JRadioButton();
        txtLetzte = new JTextField();
        lblEinheiten = new JLabel();
        rbAbgelaufen = new JRadioButton();
        jSeparator1 = new JSeparator();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        rbGefallen = new JRadioButton();
        cmbBestID = new JComboBox();
        panel1 = new JPanel();
        btnClose = new JButton();
        btnOk = new JButton();

        //======== this ========
        setResizable(false);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        //======== jPanel1 ========
        {
            jPanel1.setBorder(null);
            jPanel1.setLayout(new FormLayout(
                "14dlu, $lcgap, 145dlu, $lcgap, 41dlu, $lcgap, 93dlu, $lcgap, 14dlu",
                "14dlu, $lgap, default, $lgap, fill:70dlu:grow, 4*($lgap, fill:default), $lgap, $rgap, $lgap, fill:default, $lgap, $rgap, $lgap, default, $lgap, 14dlu"));

            //---- jLabel1 ----
            jLabel1.setFont(new Font("Arial", Font.PLAIN, 24));
            jLabel1.setText("Bestand abschlie\u00dfen");
            jPanel1.add(jLabel1, CC.xy(3, 3));

            //======== jScrollPane1 ========
            {

                //---- txtInfo ----
                txtInfo.setEditable(false);
                txtInfo.setFont(new Font("Arial", Font.PLAIN, 14));
                jScrollPane1.setViewportView(txtInfo);
            }
            jPanel1.add(jScrollPane1, CC.xywh(3, 5, 5, 1));

            //---- rbLeer ----
            rbLeer.setSelected(true);
            rbLeer.setText("Die Packung ist nun leer");
            rbLeer.setFont(new Font("Arial", Font.PLAIN, 14));
            rbLeer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    rbLeerActionPerformed(e);
                }
            });
            jPanel1.add(rbLeer, CC.xy(3, 7));

            //---- rbStellen ----
            rbStellen.setText("Beim Vorab Stellen haben Sie die letzten ");
            rbStellen.setFont(new Font("Arial", Font.PLAIN, 14));
            rbStellen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    rbStellenActionPerformed(e);
                }
            });
            jPanel1.add(rbStellen, CC.xywh(3, 9, 2, 1));

            //---- txtLetzte ----
            txtLetzte.setText("jTextField1");
            txtLetzte.setFont(new Font("Arial", Font.PLAIN, 14));
            txtLetzte.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtLetzteFocusLost(e);
                }
            });
            jPanel1.add(txtLetzte, CC.xy(5, 9));

            //---- lblEinheiten ----
            lblEinheiten.setText("Einheiten verbraucht.");
            lblEinheiten.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel1.add(lblEinheiten, CC.xy(7, 9));

            //---- rbAbgelaufen ----
            rbAbgelaufen.setText("Die Packung ist abgelaufen oder wird nicht mehr ben\u00f6tigt. Bereit zur Entsorgung.");
            rbAbgelaufen.setFont(new Font("Arial", Font.PLAIN, 14));
            rbAbgelaufen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    rbAbgelaufenActionPerformed(e);
                }
            });
            jPanel1.add(rbAbgelaufen, CC.xywh(3, 11, 5, 1));
            jPanel1.add(jSeparator1, CC.xywh(3, 15, 5, 1));

            //---- jLabel2 ----
            jLabel2.setText("Als n\u00e4chstes Packung soll die Nummer");
            jLabel2.setFont(new Font("Arial", Font.PLAIN, 14));
            jLabel2.setHorizontalAlignment(SwingConstants.TRAILING);
            jPanel1.add(jLabel2, CC.xy(3, 17));

            //---- jLabel3 ----
            jLabel3.setText("angebrochen werden.");
            jLabel3.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel1.add(jLabel3, CC.xy(7, 17));

            //---- rbGefallen ----
            rbGefallen.setText("<html>Die Packung ist <font color=\"red\">runter gefallen</font> oder <font color=\"red\">verschwunden</font> und muss ausgebucht werden.</html>");
            rbGefallen.setFont(new Font("Arial", Font.PLAIN, 14));
            rbGefallen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    rbGefallenActionPerformed(e);
                }
            });
            jPanel1.add(rbGefallen, CC.xywh(3, 13, 5, 1));

            //---- cmbBestID ----
            cmbBestID.setModel(new DefaultComboBoxModel(new String[] {
                "Item 1",
                "Item 2",
                "Item 3",
                "Item 4"
            }));
            cmbBestID.setFont(new Font("Arial", Font.PLAIN, 14));
            cmbBestID.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cmbBestIDItemStateChanged(e);
                }
            });
            jPanel1.add(cmbBestID, CC.xy(5, 17));

            //======== panel1 ========
            {
                panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

                //---- btnClose ----
                btnClose.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                btnClose.setText(null);
                btnClose.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCloseActionPerformed(e);
                    }
                });
                panel1.add(btnClose);

                //---- btnOk ----
                btnOk.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnOk.setText(null);
                btnOk.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnOkActionPerformed(e);
                    }
                });
                panel1.add(btnOk);
            }
            jPanel1.add(panel1, CC.xy(7, 21, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(jPanel1);
        pack();
        setLocationRelativeTo(getOwner());

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(rbLeer);
        buttonGroup1.add(rbStellen);
        buttonGroup1.add(rbAbgelaufen);
        buttonGroup1.add(rbGefallen);
    }// </editor-fold>//GEN-END:initComponents

    private void rbAbgelaufenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAbgelaufenActionPerformed
        txtLetzte.setEnabled(rbStellen.isSelected());
    }//GEN-LAST:event_rbAbgelaufenActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        bestand = null;
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    @Override
    public void dispose() {
        actionBlock.execute(bestand);
        SYSTools.unregisterListeners(this);
        super.dispose();
    }

    private void initDialog() {
////        this.setTitle(SYSTools.getWindowTitle("Bestand abschließen"));

        String text = "Sie möchten den Bestand mit der Nummer <font color=\"red\"><b>" + bestand.getID() + "</b></font> abschließen.";
        text += "<br/>" + MedStockTools.getTextASHTML(bestand) + "</br>";
        text += "<br/>Bitte wählen Sie einen der drei folgenden Gründe für den Abschluss:";
        txtInfo.setContentType("text/html");
        txtInfo.setText(SYSTools.toHTML(text));

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" " +
                " SELECT b FROM MedStock b " +
                " WHERE b.inventory = :vorrat AND b.out = :aus AND b.opened = :anbruch " +
                " ORDER BY b.in, b.id "); // Geht davon aus, dass die PKs immer fortlaufend, automatisch vergeben werden.
        query.setParameter("vorrat", bestand.getInventory());
        query.setParameter("aus", SYSConst.DATE_BIS_AUF_WEITERES);
        query.setParameter("anbruch", SYSConst.DATE_BIS_AUF_WEITERES);
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel(query.getResultList().toArray());
        dcbm.insertElementAt("keine", 0);
        cmbBestID.setModel(dcbm);
        cmbBestID.setRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
                String text = o instanceof MedStock ? ((MedStock) o).getID().toString() : o.toString();
                return new JLabel(text);
            }
        });
        em.close();

        int index = Math.min(2, cmbBestID.getItemCount());
        cmbBestID.setSelectedIndex(index - 1);

        lblEinheiten.setText(DosageFormTools.EINHEIT[bestand.getTradeForm().getDosageForm().getPackUnit()] + " verbraucht");
        txtLetzte.setText("");
        txtLetzte.setEnabled(false);
        // Das mit dem Vorabstellen nur bei Formen, die auf Stück basieren also APV = 1
        rbStellen.setEnabled(bestand.getTradeForm().getDosageForm().getState() == DosageFormTools.APV1);
    }

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        save();//GEN-LAST:event_btnOkActionPerformed
    }

    private void rbStellenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbStellenActionPerformed
        txtLetzte.setEnabled(true);
        txtLetzte.requestFocus();
    }//GEN-LAST:event_rbStellenActionPerformed

    private void rbLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbLeerActionPerformed
        txtLetzte.setEnabled(rbStellen.isSelected());
    }//GEN-LAST:event_rbLeerActionPerformed

    private void txtLetzteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLetzteFocusLost
        try {
            double inhalt = Double.parseDouble(txtLetzte.getText().replace(",", "."));
            if (inhalt <= 0d) {
                txtLetzte.setText("1");
            }
        } catch (NumberFormatException ex) {
            txtLetzte.setText("1");
        }
    }//GEN-LAST:event_txtLetzteFocusLost

    private void cmbBestIDItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbBestIDItemStateChanged
        if (cmbBestID.getSelectedIndex() == 0) {
            cmbBestID.setToolTipText(null);
        } else {
            MedStock myBestand = (MedStock) cmbBestID.getSelectedItem();
            cmbBestID.setToolTipText(SYSTools.toHTML(MedStockTools.getTextASHTML(myBestand)));
        }
    }//GEN-LAST:event_cmbBestIDItemStateChanged

    private void rbGefallenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbGefallenActionPerformed
        txtLetzte.setEnabled(rbStellen.isSelected());
    }//GEN-LAST:event_rbGefallenActionPerformed

    private void save() {
        String classname = this.getClass().getName() + ".save()";
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();

            bestand = em.merge(bestand);
            em.lock(bestand, LockModeType.OPTIMISTIC);

            OPDE.info("Bestands Nr. " + bestand.getID() + " wird abgeschlossen");
            OPDE.info("UKennung: " + OPDE.getLogin().getUser().getUID());

            MedStock nextBest = null;
            if (cmbBestID.getSelectedIndex() > 0) {
                nextBest = em.merge((MedStock) cmbBestID.getSelectedItem());
                em.lock(nextBest, LockModeType.OPTIMISTIC);
            }

            if (rbStellen.isSelected()) {
                bestand.setNextStock(nextBest);
                BigDecimal inhalt = new BigDecimal(Double.parseDouble(txtLetzte.getText().replace(",", ".")));
                MedStockTools.setzeBestandAuf(em, bestand, inhalt, "Korrekturbuchung zum Packungsabschluss", MedStockTransactionTools.STATE_EDIT_EMPTY_SOON);

                OPDE.info(classname + ": Vorabstellen angeklickt. Es sind noch " + inhalt + " in der Packung.");
                OPDE.info(classname + ": Nächste Packung im Anbruch wird die Bestands Nr.: " + nextBest.getID() + " sein.");

            } else {
                BigDecimal apv = bestand.getAPV();

                if (rbGefallen.isSelected()) {
                    MedStockTools.close(em, bestand, "Packung ist runtergefallen.", MedStockTransactionTools.STATE_EDIT_EMPTY_BROKEN_OR_LOST);
                    OPDE.info(classname + ": Runtergefallen angeklickt.");
                } else if (rbAbgelaufen.isSelected()) {
                    MedStockTools.close(em, bestand, "Packung ist abgelaufen.", MedStockTransactionTools.STATE_EDIT_EMPTY_PASS_EXPIRY);
                    OPDE.info(classname + ": Abgelaufen angeklickt.");
                } else {
                    MedStockTools.close(em, bestand, "Korrekturbuchung zum Packungsabschluss", MedStockTransactionTools.STATE_EDIT_EMPTY_NOW);
                    apv = MedStockTools.calcAPV(bestand);
                    OPDE.info(classname + ": Packung ist nun leer angeklickt.");
                }
            }
            em.getTransaction().commit();
        } catch (OptimisticLockException ole) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Verordnung oder Bestand wurden zwischenzeitlich von jemand anderem verändert", DisplayMessage.IMMEDIATELY, 4));
            em.getTransaction().rollback();
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel1;
    private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private JTextPane txtInfo;
    private JRadioButton rbLeer;
    private JRadioButton rbStellen;
    private JTextField txtLetzte;
    private JLabel lblEinheiten;
    private JRadioButton rbAbgelaufen;
    private JSeparator jSeparator1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JRadioButton rbGefallen;
    private JComboBox cmbBestID;
    private JPanel panel1;
    private JButton btnClose;
    private JButton btnOk;
    // End of variables declaration//GEN-END:variables
}
