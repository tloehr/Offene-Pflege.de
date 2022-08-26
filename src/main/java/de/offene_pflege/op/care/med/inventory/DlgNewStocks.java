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
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.*;
import com.jidesoft.wizard.WizardDialog;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.prescription.*;
import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.care.med.prodassistant.MedProductWizard;
import de.offene_pflege.op.system.LogicalPrinter;
import de.offene_pflege.op.system.PrinterForm;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.*;
import lombok.extern.log4j.Log4j2;
import org.jdesktop.swingx.JXSearchField;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author tloehr
 */
@Log4j2
public class DlgNewStocks extends MyJDialog {
    private boolean ignoreEvent;

    private BigDecimal amount, weight;
    private MedPackage aPackage;
    private TradeForm tradeForm;
    private Resident resident;
    private MedInventory inventory;
    private Date expiry;

    private OverlayComboBox cmbVorrat, cmbBW;
    private DefaultOverlayable ovrVorrat, ovrBW;
    private JLabel attentionIconVorrat, infoIconVorrat, correctIconVorrat, questionIconVorrat;
    private JLabel attentionIconBW;

    private OverlayTextField txtMenge;
    private DefaultOverlayable ovrMenge;
    private JLabel attentionIconMenge, correctIconMenge;

    private LogicalPrinter logicalPrinter;
    private PrinterForm printForm;

    public static final String internalClassID = "newstocks";

//    private CaretListener weightListener;

    public DlgNewStocks(Resident resident) {
        super(false);
        this.resident = resident;
        initComponents();
        initDialog();
    }

    private void txtMedSucheActionPerformed(ActionEvent evt) {
        if (ignoreEvent) {
            return;
        }

        txtMenge.setText("");

        if (txtMedSuche.getText().trim().isEmpty()) {
            cmbMProdukt.setModel(new DefaultComboBoxModel());
            cmbPackung.setModel(new DefaultComboBoxModel());
            tradeForm = null;
            aPackage = null;
            txtWeightControl.setVisible(false);
            lblWeightControl.setVisible(false);
            initCmbVorrat();

        } else {

            String pzn = null;
            try {
                pzn = MedPackageTools.parsePZN(txtMedSuche.getText());
            } catch (NumberFormatException nfe) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(nfe.getMessage(), DisplayMessage.WARNING));
                pzn = null;
            }

            if (pzn != null) { // Hier sucht man nach einer PZN. Im Barcode ist das führende 'ß' enthalten.
                EntityManager em = OPDE.createEM();
                Query query = em.createQuery("SELECT m FROM MedPackage m WHERE m.pzn = :pzn");
                query.setParameter("pzn", pzn);
                try {
                    aPackage = (MedPackage) query.getSingleResult();
                    tradeForm = aPackage.getTradeForm();
                    cmbMProdukt.setModel(new DefaultComboBoxModel(new TradeForm[]{tradeForm}));
                    cmbMProdukt.getModel().setSelectedItem(tradeForm);
                } catch (NoResultException nre) {
                    cmbMProdukt.setModel(new DefaultComboBoxModel());
                    log.debug(nre);
                } catch (Exception e) {
                    OPDE.fatal(e);
                } finally {
                    em.close();
                }
            } else { // Falls die Suche NICHT nur aus Zahlen besteht, dann nach Namen suchen.

                List<TradeForm> list = TradeFormTools.findDarreichungByMedProduktText(txtMedSuche.getText());
                cmbMProdukt.setModel(new DefaultComboBoxModel(list.toArray()));

            }
            cmbMProduktItemStateChanged(null);
        }
        setApply();
    }

    private void btnPrintItemStateChanged(ItemEvent e) {
        if (ignoreEvent) return;
        SYSPropsTools.storeState(this.getClass().getName() + "::btnPrint", btnPrint);
    }

    private void txtExpiresFocusGained(FocusEvent e) {
        txtExpires.selectAll();
    }

    private void txtExpiresFocusLost(FocusEvent e) {
        try {
            DateTime myExpiry = SYSCalendar.parseExpiryDate(txtExpires.getText());
            if (myExpiry.isBeforeNow()) {
                throw new Exception("date must not be in the past");
            }
            expiry = myExpiry.toDate();
            txtExpires.setText(DateFormat.getDateInstance().format(expiry));
        } catch (Exception ex) {
            expiry = null;
            txtExpires.setText(null);
        }
    }

    private void txtExpiresActionPerformed(ActionEvent e) {
        txtExpiresFocusLost(null);
    }

    private void initDialog() {
        ignoreEvent = true;

        expiry = null;
        logicalPrinter = OPDE.getPrintProcessor().getSelectedLogicalPrinter();
        printForm = OPDE.getPrintProcessor().getSelectedForm();

        if (logicalPrinter != null && printForm != null) {
            btnPrint.setEnabled(true);
            SYSPropsTools.restoreState(this.getClass().getName() + "::btnPrint", btnPrint);
        } else {
            btnPrint.setSelected(false);
        }

        lblPZN.setText(SYSTools.xx("newstocks.lblPZN"));
        lblProd.setText(SYSTools.xx("newstocks.lblProd"));
        lblPack.setText(SYSTools.xx("newstocks.lblPack"));
        lblAmount.setText(SYSTools.xx("newstocks.lblAmount"));
        lblInventory.setText(SYSTools.xx("newstocks.lblInventory"));
        lblRemark.setText(SYSTools.xx("misc.msg.comment"));
        lblExpires.setText(SYSTools.xx("misc.msg.expires"));
        lblResident.setText(SYSTools.xx("misc.msg.resident"));

        amount = null;
        cmbMProdukt.setRenderer(TradeFormTools.getRenderer(TradeFormTools.LONG));

        attentionIconVorrat = new JLabel(OverlayableUtils.getPredefinedOverlayIcon(OverlayableIconsFactory.ATTENTION));
        infoIconVorrat = new JLabel(OverlayableUtils.getPredefinedOverlayIcon(OverlayableIconsFactory.INFO));
        correctIconVorrat = new JLabel(OverlayableUtils.getPredefinedOverlayIcon(OverlayableIconsFactory.CORRECT));
        questionIconVorrat = new JLabel(OverlayableUtils.getPredefinedOverlayIcon(OverlayableIconsFactory.QUESTION));

        cmbVorrat = new OverlayComboBox();
        cmbVorrat.addItemListener(itemEvent -> inventory = (MedInventory) itemEvent.getItem());
        cmbVorrat.setFont(SYSConst.ARIAL14);
        ovrVorrat = new DefaultOverlayable(cmbVorrat);
        mainPane.add(ovrVorrat, CC.xywh(5, 13, 4, 1));

        attentionIconBW = new JLabel(OverlayableUtils.getPredefinedOverlayIcon(OverlayableIconsFactory.ATTENTION));
        cmbBW = new OverlayComboBox();
        cmbBW.addItemListener(itemEvent -> cmbBWItemStateChanged(itemEvent));
        cmbBW.setFont(SYSConst.ARIAL14);
        ovrBW = new DefaultOverlayable(cmbBW);
        mainPane.add(ovrBW, CC.xywh(7, 17, 2, 1));

        if (resident == null) {
            ovrBW.addOverlayComponent(attentionIconBW, DefaultOverlayable.SOUTH_EAST);
            attentionIconBW.setToolTipText(SYSTools.xx("misc.msg.emptyselection"));
        } else {
            txtBWSuche.setEnabled(false);
            cmbBW.setModel(new DefaultComboBoxModel(new Resident[]{resident}));
        }

        attentionIconMenge = new JLabel(OverlayableUtils.getPredefinedOverlayIcon(OverlayableIconsFactory.ATTENTION));
        correctIconMenge = new JLabel(OverlayableUtils.getPredefinedOverlayIcon(OverlayableIconsFactory.CORRECT));
        txtMenge = new OverlayTextField();
//        txtMenge.addCaretListener(new CaretListener() {
//            @Override
//            public void caretUpdate(CaretEvent caretEvent) {
//                txtMengeCaretUpdate(caretEvent);
//            }
//        });
        txtMenge.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                txtMengeFocusGained(focusEvent);
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                txtMengeFocusLost(focusEvent);
            }
        });
        txtMenge.setFont(SYSConst.ARIAL14);
        ovrMenge = new DefaultOverlayable(txtMenge);
        mainPane.add(ovrMenge, CC.xy(5, 11));

        lblWeightControl.setText(SYSTools.xx("opde.medication.tx.controlWeight"));
        lblWeightControl.setToolTipText(SYSTools.xx("opde.medication.controlWeight.newBottle.bottle.only"));
        weight = null;
        txtWeightControl.setVisible(false);
        txtWeightControl.setText("");
        lblWeightControl.setVisible(false);

        ignoreEvent = false;
//        setVisible(true);
    }

    private void txtWeightControlCaretUpdate(CaretEvent evt) {
        weight = SYSTools.checkBigDecimal(evt);
    }

    private void txtWeightControlFocusGained(FocusEvent e) {
        txtWeightControl.selectAll();
    }


    boolean isWeightOk() {
        if (!txtWeightControl.isVisible()) return true;
        boolean weightOK = weight != null && weight.compareTo(BigDecimal.ZERO) > 0;
        return weightOK;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the PrinterForm Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Erzeugter Quelltext ">//GEN-BEGIN:initComponents
    private void initComponents() {
        mainPane = new JPanel();
        lblPZN = new JLabel();
        panel2 = new JPanel();
        txtMedSuche = new JXSearchField();
        hSpacer1 = new JPanel(null);
        btnMed = new JButton();
        lblProd = new JLabel();
        cmbMProdukt = new JComboBox<>();
        lblInventory = new JLabel();
        lblResident = new JLabel();
        txtBWSuche = new JTextField();
        lblAmount = new JLabel();
        lblPack = new JLabel();
        cmbPackung = new JComboBox<>();
        lblExpires = new JLabel();
        txtExpires = new JTextField();
        panel3 = new JPanel();
        lblWeightControl = new JLabel();
        txtWeightControl = new JTextField();
        lblRemark = new JLabel();
        txtBemerkung = new JTextField();
        btnPrint = new JToggleButton();
        panel1 = new JPanel();
        btnClose = new JButton();
        btnApply = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Medikamente einbuchen");
        setMinimumSize(new Dimension(640, 300));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        //======== mainPane ========
        {
            mainPane.setLayout(new FormLayout(
                    "14dlu, $lcgap, default, $lcgap, 39dlu:grow, $lcgap, default:grow, $lcgap, 14dlu",
                    "14dlu, 2*($lgap, fill:17dlu), $lgap, fill:default, $lgap, 17dlu, 4*($lgap, fill:17dlu), 10dlu, fill:default, $lgap, 14dlu"));

            //---- lblPZN ----
            lblPZN.setText("PZN oder Suchbegriff");
            lblPZN.setFont(new Font("Arial", Font.PLAIN, 14));
            mainPane.add(lblPZN, CC.xy(3, 3));

            //======== panel2 ========
            {
                panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));

                //---- txtMedSuche ----
                txtMedSuche.setFont(new Font("Arial", Font.PLAIN, 14));
                txtMedSuche.addActionListener(e -> txtMedSucheActionPerformed(e));
                panel2.add(txtMedSuche);
                panel2.add(hSpacer1);

                //---- btnMed ----
                btnMed.setBackground(Color.white);
                btnMed.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                btnMed.setToolTipText("Medikamente bearbeiten");
                btnMed.setBorder(null);
                btnMed.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png")));
                btnMed.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnMed.addActionListener(e -> btnMedActionPerformed(e));
                panel2.add(btnMed);
            }
            mainPane.add(panel2, CC.xywh(5, 3, 4, 1));

            //---- lblProd ----
            lblProd.setText("Produkt");
            lblProd.setFont(new Font("Arial", Font.PLAIN, 14));
            mainPane.add(lblProd, CC.xy(3, 5));

            //---- cmbMProdukt ----
            cmbMProdukt.setModel(new DefaultComboBoxModel<>(new String[]{

            }));
            cmbMProdukt.setFont(new Font("Arial", Font.PLAIN, 14));
            cmbMProdukt.addItemListener(e -> cmbMProduktItemStateChanged(e));
            mainPane.add(cmbMProdukt, CC.xywh(5, 5, 4, 1));

            //---- lblInventory ----
            lblInventory.setText("vorhandene Vorr\u00e4te");
            lblInventory.setFont(new Font("Arial", Font.PLAIN, 14));
            mainPane.add(lblInventory, CC.xy(3, 13));

            //---- lblResident ----
            lblResident.setText("Zuordnung zu Bewohner");
            lblResident.setFont(new Font("Arial", Font.PLAIN, 14));
            mainPane.add(lblResident, CC.xy(3, 17));

            //---- txtBWSuche ----
            txtBWSuche.setFont(new Font("Arial", Font.PLAIN, 14));
            txtBWSuche.addCaretListener(e -> txtBWSucheCaretUpdate(e));
            mainPane.add(txtBWSuche, CC.xy(5, 17));

            //---- lblAmount ----
            lblAmount.setText("Buchungsmenge");
            lblAmount.setFont(new Font("Arial", Font.PLAIN, 14));
            mainPane.add(lblAmount, CC.xy(3, 11));

            //---- lblPack ----
            lblPack.setText("Packung");
            lblPack.setFont(new Font("Arial", Font.PLAIN, 14));
            mainPane.add(lblPack, CC.xy(3, 7));

            //---- cmbPackung ----
            cmbPackung.setModel(new DefaultComboBoxModel<>(new String[]{

            }));
            cmbPackung.setFont(new Font("Arial", Font.PLAIN, 14));
            cmbPackung.addItemListener(e -> cmbPackungItemStateChanged(e));
            mainPane.add(cmbPackung, CC.xywh(5, 7, 4, 1));

            //---- lblExpires ----
            lblExpires.setText("expires");
            lblExpires.setFont(new Font("Arial", Font.PLAIN, 14));
            mainPane.add(lblExpires, CC.xy(3, 9));

            //---- txtExpires ----
            txtExpires.setFont(new Font("Arial", Font.PLAIN, 14));
            txtExpires.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtExpiresFocusGained(e);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    txtExpiresFocusLost(e);
                }
            });
            txtExpires.addActionListener(e -> txtExpiresActionPerformed(e));
            mainPane.add(txtExpires, CC.xywh(5, 9, 3, 1, CC.DEFAULT, CC.FILL));

            //======== panel3 ========
            {
                panel3.setLayout(new FormLayout(
                        "pref, $lcgap, default:grow",
                        "fill:17dlu"));

                //---- lblWeightControl ----
                lblWeightControl.setText("weightcontrol");
                lblWeightControl.setFont(new Font("Arial", Font.PLAIN, 14));
                lblWeightControl.setBackground(Color.pink);
                panel3.add(lblWeightControl, CC.xy(1, 1));

                //---- txtWeightControl ----
                txtWeightControl.setFont(new Font("Arial", Font.PLAIN, 14));
                txtWeightControl.setBackground(Color.pink);
                txtWeightControl.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtWeightControlFocusGained(e);
                    }
                });
                txtWeightControl.addCaretListener(e -> txtWeightControlCaretUpdate(e));
                panel3.add(txtWeightControl, CC.xy(3, 1, CC.DEFAULT, CC.FILL));
            }
            mainPane.add(panel3, CC.xy(7, 11));

            //---- lblRemark ----
            lblRemark.setText("Bemerkung");
            lblRemark.setFont(new Font("Arial", Font.PLAIN, 14));
            mainPane.add(lblRemark, CC.xy(3, 15));

            //---- txtBemerkung ----
            txtBemerkung.setFont(new Font("Arial", Font.PLAIN, 14));
            txtBemerkung.addCaretListener(e -> txtBemerkungCaretUpdate(e));
            mainPane.add(txtBemerkung, CC.xywh(5, 15, 4, 1));

            //---- btnPrint ----
            btnPrint.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer-on.png")));
            btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer-off.png")));
            btnPrint.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnPrint.setEnabled(false);
            btnPrint.addItemListener(e -> btnPrintItemStateChanged(e));
            mainPane.add(btnPrint, CC.xy(3, 19, CC.RIGHT, CC.DEFAULT));

            //======== panel1 ========
            {
                panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

                //---- btnClose ----
                btnClose.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnClose.addActionListener(e -> btnCloseActionPerformed(e));
                panel1.add(btnClose);

                //---- btnApply ----
                btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnApply.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnApply.addActionListener(e -> btnApplyActionPerformed(e));
                panel1.add(btnApply);
            }
            mainPane.add(panel1, CC.xywh(7, 19, 2, 1, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(mainPane);
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void txtMengeFocusGained(FocusEvent evt) {//GEN-FIRST:event_txtMengeFocusGained
        SYSTools.markAllTxt((JTextField) evt.getSource());
    }//GEN-LAST:event_txtMengeFocusGained

    private void txtBemerkungCaretUpdate(CaretEvent evt) {//GEN-FIRST:event_txtBemerkungCaretUpdate
        setApply();
    }//GEN-LAST:event_txtBemerkungCaretUpdate

    private void cmbBWItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_cmbBWItemStateChanged
        if (ignoreEvent || (evt != null && evt.getStateChange() != ItemEvent.SELECTED)) {
            return;
        }
        resident = (Resident) cmbBW.getSelectedItem();
        log.debug("cmbPackungItemStateChanged: " + cmbBW.getSelectedItem());
        initCmbVorrat();
        setApply();
    }//GEN-LAST:event_cmbBWItemStateChanged

    private void btnApplyActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        String text = "";

        if (resident == null) {
            text += "Keine(n) BewohnerIn ausgewählt. ";
        }
        if (tradeForm == null) {
            text += "Kein Medikament ausgewählt. ";
        }
        if (amount == null && aPackage == null) {
            text += "Keine korrekte Mengenangabe. ";
        }
        if (inventory == null) {
            text += "Keinen Vorrat ausgewählt. ";
        }

        if (!isWeightOk()) {
            text += "Kontrollgewicht falsch. ";
        }

        if (text.isEmpty()) {
            save();

            txtMenge.setText(null);
            txtBemerkung.setText(null);
            txtMedSuche.setText(null);
            txtExpires.setText(null);
            txtMedSuche.requestFocus();
        } else {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("newstocks.registration.failed") + ": " + text, DisplayMessage.WARNING));
        }
    }//GEN-LAST:event_btnApplyActionPerformed

    private void save() {
        EntityManager em = OPDE.createEM();

        try {
            em.getTransaction().begin();

            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);

            // Wenn die aPackage null ist, dann ist eine Sonderpackung
            if (aPackage != null) {
                aPackage = em.merge(aPackage);
                if (amount == null) {
                    amount = aPackage.getContent();
                }
            }

            tradeForm = em.merge(tradeForm);
            inventory = em.merge(inventory);

            if (inventory.getID() == null) { // create a new MedInvetory.
                inventory.setText(TradeFormTools.toPrettyString(tradeForm) + "; " + ACMETools.toPrettyStringShort(tradeForm.getMedProduct().getACME()));
            }

            // https://github.com/tloehr/Offene-Pflege.de/issues/16
            BigDecimal estimatedUPR = BigDecimal.ONE;

            int dummyMode = MedStockTools.DONT_REPLACE_UPR;
            if (tradeForm.getDosageForm().isUPRn()) {
                if (tradeForm.getMedStocks().isEmpty()) {
                    // first of its kind. will calculate real UPR when it's closed
                    dummyMode = MedStockTools.REPLACE_WITH_EFFECTIVE_UPR_WHEN_CLOSING;
                } else if (MedStockTools.stillWorkingOnTheFirstOneToCalculateUPRn(tradeForm)) {
                    dummyMode = MedStockTools.REPLACE_WITH_EFFECTIVE_UPR_WHEN_FIRST_STOCK_OF_THIS_KIND_IS_CLOSING;
                } else {
                    dummyMode = MedStockTools.ADD_TO_AVERAGES_UPR_WHEN_CLOSING;
                    estimatedUPR = MedStockTools.getEstimatedUPR(tradeForm);
                }
            }

            MedStock newStock = em.merge(new MedStock(inventory, tradeForm, aPackage, txtBemerkung.getText(), estimatedUPR, dummyMode));
            newStock.setExpires(expiry);
            MedStockTransaction tx = em.merge(new MedStockTransaction(newStock, amount));
            tx.setWeight(weight);

            // wenn es offene Bestellungen zu diesem Medikament gibt, dann schließen wir sie jetzt.
            List<MedOrder> open_orders = MedOrderTools.get_open_orders(em, resident);
            if (!open_orders.isEmpty()) {
                // alle passenden tradeforms finden, anhand vergangenener Zurordnungen
                final List<TradeForm> equivalent_tfs = TradeFormTools.get_tradeforms_in_this_inventory(em, inventory);
                
                // alle passenden bestellungen suchen und abschließen.
                open_orders.stream()
                        .filter(medOrder -> medOrder.getTradeForm().equals(tradeForm) || equivalent_tfs.contains(medOrder.getTradeForm()))
                        .forEach(medOrder -> {
                            medOrder.setClosed_on(LocalDateTime.now());
                            medOrder.setClosed_by(OPDE.getLogin().getUser());
                            medOrder.setNote(String.format("Bestand Nr. %s eingebucht.", newStock.getID()));
                        });
            }

            em.getTransaction().commit();
            amount = null;
            aPackage = null;
            tradeForm = null;
            inventory = null;
            expiry = null;
            weight = null;

            if (btnPrint.isSelected()) {
                OPDE.getPrintProcessor().addPrintJob(new PrintListElement(newStock, logicalPrinter, printForm, OPDE.getProps().getProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER)));
            }

            // if the label printer is not used, the new number is shown until the next message, so the user has time to write the number down manually.
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("newstocks.registration.success.1") + " <b>" + newStock.getID() + "</b> " + SYSTools.xx("newstocks.registration.success.2"), btnPrint.isSelected() ? 2 : 0));
        } catch (OptimisticLockException ole) {
            log.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                OPDE.getMainframe().emptyFrame();
                OPDE.getMainframe().afterLogin();
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
    }

    private void btnMedActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMedActionPerformed

//        String pzn = MedPackageTools.parsePZN(txtMedSuche.getText());
        final JidePopup popup = new JidePopup();

        WizardDialog wizard = new MedProductWizard(o -> {
            if (o != null) {
                MedPackage aPackage1 = (MedPackage) o;
                txtMedSuche.setText(aPackage1.getPzn());
            }
            popup.hidePopup();

        }).getWizard();


        popup.setMovable(false);
        popup.setPreferredSize((new Dimension(800, 450)));
        popup.setResizable(false);
        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
        popup.getContentPane().add(wizard.getContentPane());
        popup.setOwner(btnMed);
        popup.removeExcludedComponent(btnMed);
        popup.setTransient(true);
        popup.setDefaultFocusComponent(wizard.getContentPane());
        popup.addPropertyChangeListener("visible", propertyChangeEvent -> {
            log.debug("popup property: " + propertyChangeEvent.getPropertyName() + " value: " + propertyChangeEvent.getNewValue() + " compCount: " + popup.getContentPane().getComponentCount());
            popup.getContentPane().getComponentCount();
        });

        GUITools.showPopup(popup, SwingConstants.WEST);

//        popup.showPopup(new Insets(-5, wizard.getPreferredSize().width * -1 - 200, -5, -100), btnMed);


    }//GEN-LAST:event_btnMedActionPerformed

    @Override
    public void dispose() {
        SYSTools.unregisterListeners(this);
        super.dispose();
    }

    private void txtMengeFocusLost(FocusEvent evt) {//GEN-FIRST:event_txtMengeCaretUpdate
        if (ovrMenge.getOverlayComponents().length > 0) {
            ovrMenge.removeOverlayComponent(ovrMenge.getOverlayComponents()[0]);
        }

        amount = SYSTools.parseDecimal(txtMenge.getText().trim());

        if (amount != null) {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
//                lblMenge.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")));
                ovrMenge.addOverlayComponent(attentionIconMenge, DefaultOverlayable.SOUTH_EAST);
                attentionIconMenge.setToolTipText(SYSTools.toHTML("<i>Mengen müssen größer 0 sein.</i>"));
                amount = null;
            } else if (aPackage != null && amount.compareTo(aPackage.getContent()) > 0) {
                ovrMenge.addOverlayComponent(attentionIconMenge, DefaultOverlayable.SOUTH_EAST);
                attentionIconMenge.setToolTipText(SYSTools.toHTML("<i>Mengen dürfen nicht größer als der Packungsinhalt sein.</i>"));
                amount = aPackage.getContent();
            } else {
                ovrMenge.addOverlayComponent(correctIconMenge, DefaultOverlayable.SOUTH_EAST);
            }
        } else {
            ovrMenge.addOverlayComponent(attentionIconMenge, DefaultOverlayable.SOUTH_EAST);
            attentionIconMenge.setToolTipText(SYSTools.toHTML("<i>Die Mengenangabe ist falsch.</i>"));
        }
        txtMenge.setText(SYSTools.formatBigDecimal(amount));


    }//GEN-LAST:event_txtMengeCaretUpdate

    private void txtBWSucheCaretUpdate(CaretEvent evt) {//GEN-FIRST:event_txtBWSucheCaretUpdate
        if (ignoreEvent || !txtBWSuche.isEnabled()) {
            return;
        }

        if (txtBWSuche.getText().isEmpty()) {
            cmbBW.setModel(new DefaultComboBoxModel());
            resident = null;
        } else {
            DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
            EntityManager em = OPDE.createEM();
            if (txtBWSuche.getText().trim().length() == 3) { // Könnte eine Suche nach der Kennung sein
                resident = em.find(Resident.class, txtBWSuche.getText().trim());
                if (resident != null) {
                    dcbm = new DefaultComboBoxModel(new Resident[]{resident});
                }
            }

            if (dcbm.getSize() == 0) { // Vielleicht Suche nach Nachname

                Query query = em.createQuery(" SELECT b FROM Resident b WHERE b.station IS NOT NULL AND b.name like :nachname ORDER BY b.name, b.firstname ");
                query.setParameter("nachname", txtBWSuche.getText().trim() + "%");
                List<Resident> listbw = query.getResultList();

                dcbm = new DefaultComboBoxModel(listbw.toArray());
            }

            if (dcbm.getSize() > 0) {
                cmbBW.setModel(dcbm);
                cmbBW.setSelectedIndex(0);
                resident = (Resident) cmbBW.getSelectedItem();
            } else {
                cmbBW.setModel(new DefaultComboBoxModel());
                resident = null;
            }
            em.close();
        }

        if (ovrBW.getOverlayComponents().length > 0) {
            ovrBW.removeOverlayComponent(ovrBW.getOverlayComponents()[0]);
        }
        if (resident == null) {
            ovrBW.addOverlayComponent(attentionIconBW, DefaultOverlayable.SOUTH_EAST);
            attentionIconBW.setToolTipText("<html>Keine(n) BewohnerIn ausgewählt.<html>");
        }


        initCmbVorrat();
    }//GEN-LAST:event_txtBWSucheCaretUpdate

    private void btnCloseActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void cmbPackungItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_cmbPackungItemStateChanged
        if (ignoreEvent || (evt != null && evt.getStateChange() != ItemEvent.SELECTED)) {
            return;
        }

        log.debug("cmbPackungItemStateChanged: " + cmbPackung.getSelectedItem());
        if (cmbPackung.getSelectedItem() instanceof MedPackage) {
            aPackage = (MedPackage) cmbPackung.getSelectedItem();
        } else {
            aPackage = null;
        }
        txtMenge.setText("");

    }//GEN-LAST:event_cmbPackungItemStateChanged

    private void initCmbVorrat() {

        if (ovrVorrat.getOverlayComponents().length > 0) {
            ovrVorrat.removeOverlayComponent(ovrVorrat.getOverlayComponents()[0]);
        }

        if (resident != null) {

            cmbVorrat.setRenderer(MedInventoryTools.getInventoryRenderer());
            if (tradeForm == null) {
                cmbVorrat.setModel(new DefaultComboBoxModel());
                inventory = null;
            } else {
                List<MedInventory> vorraete = new ArrayList<MedInventory>();
                inventory = TradeFormTools.getInventory4TradeForm(resident, tradeForm);

                if (inventory == null) {
                    vorraete = TradeFormTools.getSuitableInventoriesForThisTradeForm(resident, tradeForm);
                } else {
                    vorraete.add(inventory);
                }
                cmbVorrat.setModel(new DefaultComboBoxModel(vorraete.toArray()));
            }

//            ovrVorrat.removeOverlayComponent(ovrVorrat.getOverlayComponents()[0]);
            if (tradeForm != null) {
                if (inventory == null) {
                    DefaultComboBoxModel dcbm = (DefaultComboBoxModel) cmbVorrat.getModel();
                    dcbm.insertElementAt(new MedInventory(resident, "<AUTOMATISCH>"), 0);
                    cmbVorrat.setSelectedIndex(0);

                    if (dcbm.getSize() > 1) {
                        ovrVorrat.addOverlayComponent(attentionIconVorrat, DefaultOverlayable.SOUTH_EAST);
                        attentionIconVorrat.setToolTipText("<html>Keinen <b>exakt</b> passender Vorrat gefunden. Wählen Sie selbst einen passenden aus oder verwenden Sie <b>automatisch</b>.<html>");
                        cmbVorrat.showPopup();
                    } else {
                        ovrVorrat.addOverlayComponent(infoIconVorrat, DefaultOverlayable.SOUTH_EAST);
                        infoIconVorrat.setToolTipText("<html>Ein neuer Vorrat wird <b>automatisch</b> erstellt.</html>");
                    }
                } else {
                    correctIconVorrat.setToolTipText(null);
                    ovrVorrat.addOverlayComponent(correctIconVorrat, DefaultOverlayable.SOUTH_EAST);
                }
            } else {
                ovrVorrat.addOverlayComponent(questionIconVorrat, DefaultOverlayable.SOUTH_EAST);
                questionIconVorrat.setToolTipText("<html>Kein Medikament ausgewählt.<html>");
            }
        }
    }

    private void cmbMProduktItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_cmbMProduktItemStateChanged
        if (ignoreEvent || (evt != null && evt.getStateChange() != ItemEvent.SELECTED)) {
            return;
        }

        tradeForm = (TradeForm) cmbMProdukt.getSelectedItem();

        if (tradeForm != null) {
            DefaultComboBoxModel dcbm = new DefaultComboBoxModel(tradeForm.getPackages().toArray());
            txtWeightControl.setVisible(tradeForm.isWeightControlled());
            lblWeightControl.setVisible(tradeForm.isWeightControlled());
            dcbm.insertElementAt("<Sonderpackung>", 0);
            cmbPackung.setModel(dcbm);
            cmbPackung.setRenderer(MedPackageTools.getMedPackungRenderer());
            if (aPackage == null) {
                cmbPackung.setSelectedIndex(cmbPackung.getModel().getSize() - 1);
            } else {
                cmbPackung.setSelectedItem(aPackage);
            }
            cmbPackungItemStateChanged(null);
        } else {
            cmbPackung.setModel(new DefaultComboBoxModel());
            aPackage = null;
            txtWeightControl.setVisible(false);
            lblWeightControl.setVisible(false);
        }

        initCmbVorrat();

    }//GEN-LAST:event_cmbMProduktItemStateChanged

    private void setApply() {
//        boolean txtEntry = true;
//        if (cmbPackung.getSelectedIndex() < 0) {
//            txtEntry = !txtBemerkung.getText().isEmpty();
//        }
//
//        btnApply.setPanelEnabled(medEingegeben && (mengeEingegeben || packungEingegeben) && bwEingegeben && txtEntry);
    }


    // Variablendeklaration - nicht modifizieren//GEN-BEGIN:variables
    private JPanel mainPane;
    private JLabel lblPZN;
    private JPanel panel2;
    private JXSearchField txtMedSuche;
    private JPanel hSpacer1;
    private JButton btnMed;
    private JLabel lblProd;
    private JComboBox<String> cmbMProdukt;
    private JLabel lblInventory;
    private JLabel lblResident;
    private JTextField txtBWSuche;
    private JLabel lblAmount;
    private JLabel lblPack;
    private JComboBox<String> cmbPackung;
    private JLabel lblExpires;
    private JTextField txtExpires;
    private JPanel panel3;
    private JLabel lblWeightControl;
    private JTextField txtWeightControl;
    private JLabel lblRemark;
    private JTextField txtBemerkung;
    private JToggleButton btnPrint;
    private JPanel panel1;
    private JButton btnClose;
    private JButton btnApply;
    // Ende der Variablendeklaration//GEN-END:variables

}
