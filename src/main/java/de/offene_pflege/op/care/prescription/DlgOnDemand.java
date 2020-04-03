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
package de.offene_pflege.op.care.prescription;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.wizard.WizardDialog;
import de.offene_pflege.backend.entity.EntityTools;
import de.offene_pflege.backend.entity.nursingprocess.Intervention;
import de.offene_pflege.backend.entity.nursingprocess.InterventionTools;
import de.offene_pflege.backend.entity.prescription.*;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.care.med.prodassistant.MedProductWizard;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.*;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Bei einer Verordnungsänderung wird die alte Verordnung abgesetzt und die neue Verordnung statt dessen angesetzt. BHPs werden immer erstellt.
 * Bei neuen Verordnungen immer für den ganzen Tag. Bei Änderung immer ab dem Moment, ab dem die neue Verordnung gilt.
 *
 * @author tloehr
 */
public class DlgOnDemand extends MyJDialog {

    private boolean ignoreEvent;

    private Closure actionBlock;
    private Prescription prescription, resultPrescription;
    private PrescriptionSchedule schedule;
    private List<GP> listAerzte;
    private List<Hospital> listKH;
    private PnlCommonTags pnlCommonTags;

    /**
     * Creates new form DlgRegular
     */
    public DlgOnDemand(Prescription prescription, Closure actionBlock) {
        super();
        setResizable(false);
        // OnDemand prescriptions have exactly ONE schedule
        if (prescription.getPrescriptionSchedule().isEmpty()) {
            PrescriptionSchedule schedule = new PrescriptionSchedule(prescription);
            schedule.setMorgens(BigDecimal.ZERO);
            schedule.setMaxAnzahl(1);
            schedule.setMaxEDosis(BigDecimal.ONE);
            schedule.setPrescription(prescription);
            prescription.getPrescriptionSchedule().add(schedule);
        }
        schedule = prescription.getPrescriptionSchedule().get(0);
        this.actionBlock = actionBlock;
        this.prescription = prescription;
//        schedules2delete = new ArrayList<PrescriptionSchedule>();

        initComponents();
        initDialog();
        pack();
//        setVisible(true);
    }


    private void txtSitActionPerformed(ActionEvent e) {
        if (txtSit.getText().isEmpty()) {
            cmbSit.setModel(new DefaultComboBoxModel());
        } else {
            cmbSit.setModel(new DefaultComboBoxModel(SituationsTools.findSituationByText(txtSit.getText()).toArray()));
        }
    }

    private void saveSituation(String text) {
        if (text.isEmpty()) {
            return;
        }
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT s FROM Situations s WHERE s.text = :text");
        query.setParameter("text", text);
        if (query.getResultList().isEmpty()) {
            Situations neueSituation = new Situations(text);
            em.persist(neueSituation);
            cmbSit.setModel(new DefaultComboBoxModel(new Situations[]{neueSituation}));
        } else {
            cmbSit.setModel(new DefaultComboBoxModel(query.getResultList().toArray()));
        }
        em.close();
    }

    private ListCellRenderer getRenderer() {
        return (jList, o, i, isSelected, cellHasFocus) -> {
            String text;
            if (o == null) {
                text = "<i>" + SYSTools.xx("nursingrecords.prescription.dlgOnDemand.noOutcomeCheck") + "</i>";
            } else if (o instanceof BigDecimal) {
                if (o.equals(new BigDecimal("0.5"))) {
                    text = "&frac12; " + SYSTools.xx("misc.msg.Hour");
                } else if (o.equals(BigDecimal.ONE)) {
                    text = "1 " + SYSTools.xx("misc.msg.Hour");
                } else {
                    text = o.toString() + " " + SYSTools.xx("misc.msg.Hours");
                }
            } else {
                text = o.toString();
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, SYSTools.toHTMLForScreen(text), i, isSelected, cellHasFocus);
        };
    }

    private void btnSituationActionPerformed(ActionEvent e) {

        final JidePopup popup = new JidePopup();
        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

        final JTextField editor = new JTextField(txtSit.getText(), 30);
        editor.addActionListener(actionEvent -> {
            saveSituation(editor.getText());
            popup.hidePopup();
        });

        popup.getContentPane().add(new JScrollPane(editor));
        JButton saveButton = new JButton(SYSConst.icon22apply);
        saveButton.addActionListener(actionEvent -> {
            saveSituation(editor.getText());
            popup.hidePopup();
        });

        popup.setMovable(false);
        popup.setOwner(btnAddSit);
        popup.removeExcludedComponent(btnAddSit);

        popup.getContentPane().add(saveButton);
        popup.setDefaultFocusComponent(editor);

        GUITools.showPopup(popup, SwingConstants.SOUTH_WEST);
    }

    private void cmbSitPropertyChange(PropertyChangeEvent e) {
        cmbSitItemStateChanged(null);
    }

    private void txtMedActionPerformed(ActionEvent e) {
        if (txtMed.getText().isEmpty()) {
            cmbMed.setModel(new DefaultComboBoxModel());
            cmbIntervention.setEnabled(true);
            txtIntervention.setEnabled(true);
        } else {
            EntityManager em = OPDE.createEM();

            String pzn = null;
            try {
                pzn = MedPackageTools.parsePZN(txtMed.getText());
            } catch (NumberFormatException nfe) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(nfe.getMessage(), DisplayMessage.WARNING));
                pzn = null;
            }


            if (pzn != null) {

                Query pznQuery = em.createQuery("SELECT m FROM MedPackage m WHERE m.pzn = :pzn");
                pznQuery.setParameter("pzn", pzn);

                try {
                    MedPackage medPackage = (MedPackage) pznQuery.getSingleResult();
                    cmbMed.setModel(new DefaultComboBoxModel(new TradeForm[]{medPackage.getTradeForm()}));
                } catch (NoResultException nre) {
                    OPDE.debug("nothing found for this PZN");
                } catch (Exception ex) {
                    OPDE.fatal(ex);
                }

            } else { // no PZN, a Stock maybe ? or just a text
                MedStock potentialStock = null;
                try {
                    long potentialStockID = Integer.parseInt(txtMed.getText());
                    potentialStock = EntityTools.find(MedStock.class, potentialStockID);
                } catch (NumberFormatException e1) {
                    // noch stockid then
                }
                if (potentialStock != null && potentialStock.getInventory().getResident().equals(prescription.getResident()) && !potentialStock.isClosed()) {
                    cmbMed.setModel(new DefaultComboBoxModel(new TradeForm[]{potentialStock.getTradeForm()}));
                } else {
                    cmbMed.setModel(new DefaultComboBoxModel(TradeFormTools.findTradeFormByMedProductText(em, txtMed.getText()).toArray()));
                }
            }

            em.close();

            if (cmbMed.getModel().getSize() > 0) {
                cmbMedItemStateChanged(null);
            } else {
                cmbMed.setToolTipText("");
                cmbIntervention.setSelectedIndex(-1);
                cmbIntervention.setEnabled(true);
                txtIntervention.setEnabled(true);
            }
        }
    }

    private void rbActiveItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            txtOFF.setText(null);
        }
    }

    private void rbDateItemStateChanged(ItemEvent e) {
        txtOFF.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            txtOFF.setText(DateFormat.getDateInstance().format(new Date()));
        }
    }

    private void txtMaxTimesActionPerformed(ActionEvent e) {
        txtEDosis.requestFocus();
    }

    private void txtMaxTimesFocusGained(FocusEvent e) {
        SYSTools.markAllTxt((JTextField) e.getSource());
    }

    private void txtEDosisFocusGained(FocusEvent e) {
        SYSTools.markAllTxt((JTextField) e.getSource());
    }

    private void txtOFFFocusLost(FocusEvent evt) {
        SYSCalendar.handleDateFocusLost(evt, new LocalDate(), new LocalDate().plusYears(1));
    }

    private void txtMaxTimesFocusLost(FocusEvent e) {
        SYSTools.handleIntegerFocusLost(e, 1, 20, 1);
    }

    private void txtEDosisFocusLost(FocusEvent e) {
        SYSTools.handleBigDecimalFocusLost(e, new BigDecimal(BigInteger.ZERO), new BigDecimal(1000), BigDecimal.ONE);
    }

    private void txtEDosisActionPerformed(ActionEvent e) {
        txtMaxTimes.requestFocus();
    }

    private void cmbDocONKeyPressed(KeyEvent e) {
        final String searchKey = String.valueOf(e.getKeyChar());
        GP doc = (GP) CollectionUtils.find(listAerzte, o -> o != null && ((GP) o).getName().toLowerCase().charAt(0) == searchKey.toLowerCase().charAt(0));
        if (doc != null) {
            cmbDocON.setSelectedItem(doc);
        }
    }


    private void thisWindowClosing(WindowEvent e) {
        prescription = null;
        // fixes #22
    }

    private void txtMassActionPerformed(ActionEvent e) {
        cmbIntervention.setModel(new DefaultComboBoxModel(InterventionTools.findBy(InterventionTools.TYPE_PRESCRIPTION, txtIntervention.getText()).toArray()));
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
        txtMed = new JXSearchField();
        cmbMed = new JComboBox<>();
        panel4 = new JPanel();
        btnMedWizard = new JButton();
        cmbIntervention = new JComboBox<>();
        txtSit = new JXSearchField();
        cmbSit = new JComboBox<>();
        panel3 = new JPanel();
        btnAddSit = new JButton();
        txtIntervention = new JXSearchField();
        jPanel2 = new JPanel();
        lblNumber = new JLabel();
        lblDose = new JLabel();
        lblMaxPerDay = new JLabel();
        txtMaxTimes = new JTextField();
        lblX = new JLabel();
        txtEDosis = new JTextField();
        lblCheckResultAfter = new JLabel();
        cmbCheckAfter = new JComboBox<>();
        jPanel3 = new JPanel();
        pnlOFF = new JPanel();
        rbActive = new JRadioButton();
        rbDate = new JRadioButton();
        txtOFF = new JTextField();
        jScrollPane3 = new JScrollPane();
        txtBemerkung = new JTextPane();
        lblText = new JLabel();
        pnlON = new JPanel();
        cmbDocON = new JComboBox<>();
        cmbHospitalON = new JComboBox<>();
        panel1 = new JPanel();
        btnClose = new JButton();
        btnSave = new JButton();

        //======== this ========
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
                "14dlu, $lcgap, default, 6dlu, 355dlu, $lcgap, 14dlu",
                "14dlu, $lgap, fill:default:grow, $lgap, fill:default, $lgap, 14dlu"));

        //======== jPanel1 ========
        {
            jPanel1.setBorder(null);
            jPanel1.setLayout(new FormLayout(
                    "68dlu, $lcgap, pref:grow, $lcgap, pref",
                    "3*(16dlu, $lgap), default, $lgap, fill:113dlu:grow, $lgap, 60dlu"));

            //---- txtMed ----
            txtMed.setFont(new Font("Arial", Font.PLAIN, 14));
            txtMed.setPrompt("Medikamente");
            txtMed.setFocusBehavior(PromptSupport.FocusBehavior.HIGHLIGHT_PROMPT);
            txtMed.addActionListener(e -> txtMedActionPerformed(e));
            txtMed.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtMedFocusGained(e);
                }
            });
            jPanel1.add(txtMed, CC.xy(1, 1));

            //---- cmbMed ----
            cmbMed.setModel(new DefaultComboBoxModel<>(new String[]{
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
            }));
            cmbMed.setFont(new Font("Arial", Font.PLAIN, 14));
            cmbMed.addItemListener(e -> cmbMedItemStateChanged(e));
            jPanel1.add(cmbMed, CC.xy(3, 1));

            //======== panel4 ========
            {
                panel4.setLayout(new BoxLayout(panel4, BoxLayout.LINE_AXIS));

                //---- btnMedWizard ----
                btnMedWizard.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                btnMedWizard.setBorderPainted(false);
                btnMedWizard.setBorder(null);
                btnMedWizard.setContentAreaFilled(false);
                btnMedWizard.setToolTipText("Neues Medikament eintragen");
                btnMedWizard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnMedWizard.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png")));
                btnMedWizard.addActionListener(e -> btnMedActionPerformed(e));
                panel4.add(btnMedWizard);
            }
            jPanel1.add(panel4, CC.xy(5, 1));

            //---- cmbIntervention ----
            cmbIntervention.setModel(new DefaultComboBoxModel<>(new String[]{
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
            }));
            cmbIntervention.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel1.add(cmbIntervention, CC.xywh(3, 5, 3, 1));

            //---- txtSit ----
            txtSit.setPrompt("Situationen");
            txtSit.setFont(new Font("Arial", Font.PLAIN, 14));
            txtSit.addActionListener(e -> txtSitActionPerformed(e));
            jPanel1.add(txtSit, CC.xy(1, 3));

            //---- cmbSit ----
            cmbSit.setModel(new DefaultComboBoxModel<>(new String[]{
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
            }));
            cmbSit.setFont(new Font("Arial", Font.PLAIN, 14));
            cmbSit.addItemListener(e -> cmbSitItemStateChanged(e));
            cmbSit.addPropertyChangeListener("model", e -> cmbSitPropertyChange(e));
            jPanel1.add(cmbSit, CC.xy(3, 3));

            //======== panel3 ========
            {
                panel3.setLayout(new BoxLayout(panel3, BoxLayout.LINE_AXIS));

                //---- btnAddSit ----
                btnAddSit.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                btnAddSit.setBorderPainted(false);
                btnAddSit.setBorder(null);
                btnAddSit.setContentAreaFilled(false);
                btnAddSit.setToolTipText("Neue  Situation eintragen");
                btnAddSit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnAddSit.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png")));
                btnAddSit.addActionListener(e -> btnSituationActionPerformed(e));
                panel3.add(btnAddSit);
            }
            jPanel1.add(panel3, CC.xy(5, 3, CC.RIGHT, CC.DEFAULT));

            //---- txtIntervention ----
            txtIntervention.setFont(new Font("Arial", Font.PLAIN, 14));
            txtIntervention.setPrompt("Massnahmen");
            txtIntervention.addActionListener(e -> txtMassActionPerformed(e));
            jPanel1.add(txtIntervention, CC.xy(1, 5));

            //======== jPanel2 ========
            {
                jPanel2.setLayout(new FormLayout(
                        "default, $lcgap, pref, $lcgap, default, $lcgap, 37dlu:grow",
                        "23dlu, fill:22dlu, $ugap, default"));

                //---- lblNumber ----
                lblNumber.setText("Anzahl");
                jPanel2.add(lblNumber, CC.xy(3, 1));

                //---- lblDose ----
                lblDose.setText("Dosis");
                jPanel2.add(lblDose, CC.xy(7, 1, CC.CENTER, CC.DEFAULT));

                //---- lblMaxPerDay ----
                lblMaxPerDay.setText("Max. Tagesdosis:");
                jPanel2.add(lblMaxPerDay, CC.xy(1, 2));

                //---- txtMaxTimes ----
                txtMaxTimes.setHorizontalAlignment(SwingConstants.CENTER);
                txtMaxTimes.setText("1");
                txtMaxTimes.addActionListener(e -> txtMaxTimesActionPerformed(e));
                txtMaxTimes.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtMaxTimesFocusGained(e);
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        txtMaxTimesFocusLost(e);
                    }
                });
                jPanel2.add(txtMaxTimes, CC.xy(3, 2));

                //---- lblX ----
                lblX.setText("x");
                jPanel2.add(lblX, CC.xy(5, 2));

                //---- txtEDosis ----
                txtEDosis.setHorizontalAlignment(SwingConstants.CENTER);
                txtEDosis.setText("1.0");
                txtEDosis.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtEDosisFocusGained(e);
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        txtEDosisFocusLost(e);
                    }
                });
                txtEDosis.addActionListener(e -> txtEDosisActionPerformed(e));
                jPanel2.add(txtEDosis, CC.xy(7, 2));

                //---- lblCheckResultAfter ----
                lblCheckResultAfter.setText("Nachkontrolle:");
                jPanel2.add(lblCheckResultAfter, CC.xy(1, 4));

                //---- cmbCheckAfter ----
                cmbCheckAfter.setModel(new DefaultComboBoxModel<>(new String[]{
                        "keine Nachkontrolle",
                        "nach 1 Stunde",
                        "nach 2 Stunden",
                        "nach 3 Stunden"
                }));
                jPanel2.add(cmbCheckAfter, CC.xywh(3, 4, 5, 1));
            }
            jPanel1.add(jPanel2, CC.xywh(1, 9, 5, 1, CC.CENTER, CC.TOP));
        }
        contentPane.add(jPanel1, CC.xy(5, 3));

        //======== jPanel3 ========
        {
            jPanel3.setBorder(null);
            jPanel3.setLayout(new FormLayout(
                    "149dlu",
                    "3*(fill:default, $lgap), fill:100dlu:grow"));

            //======== pnlOFF ========
            {
                pnlOFF.setBorder(new TitledBorder("Absetzung"));
                pnlOFF.setLayout(new FormLayout(
                        "pref, 86dlu:grow",
                        "fill:17dlu, $lgap, fill:17dlu"));

                //---- rbActive ----
                rbActive.setText("text");
                rbActive.setSelected(true);
                rbActive.addItemListener(e -> rbActiveItemStateChanged(e));
                pnlOFF.add(rbActive, CC.xywh(1, 1, 2, 1));

                //---- rbDate ----
                rbDate.setText(null);
                rbDate.addItemListener(e -> rbDateItemStateChanged(e));
                pnlOFF.add(rbDate, CC.xy(1, 3));

                //---- txtOFF ----
                txtOFF.setEnabled(false);
                txtOFF.setFont(new Font("Arial", Font.PLAIN, 14));
                txtOFF.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtOFFFocusLost(e);
                    }
                });
                pnlOFF.add(txtOFF, CC.xy(2, 3));
            }
            jPanel3.add(pnlOFF, CC.xy(1, 3));

            //======== jScrollPane3 ========
            {

                //---- txtBemerkung ----
                txtBemerkung.addCaretListener(e -> txtBemerkungCaretUpdate(e));
                jScrollPane3.setViewportView(txtBemerkung);
            }
            jPanel3.add(jScrollPane3, CC.xy(1, 7));

            //---- lblText ----
            lblText.setText("Bemerkung:");
            jPanel3.add(lblText, CC.xy(1, 5));

            //======== pnlON ========
            {
                pnlON.setBorder(new TitledBorder("Ansetzung"));
                pnlON.setLayout(new FormLayout(
                        "119dlu:grow",
                        "17dlu, $lgap, fill:17dlu"));

                //---- cmbDocON ----
                cmbDocON.setModel(new DefaultComboBoxModel<>(new String[]{
                        "Item 1",
                        "Item 2",
                        "Item 3",
                        "Item 4"
                }));
                cmbDocON.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        cmbDocONKeyPressed(e);
                    }
                });
                pnlON.add(cmbDocON, CC.xy(1, 1));

                //---- cmbHospitalON ----
                cmbHospitalON.setModel(new DefaultComboBoxModel<>(new String[]{
                        "Item 1",
                        "Item 2",
                        "Item 3",
                        "Item 4"
                }));
                pnlON.add(cmbHospitalON, CC.xy(1, 3));
            }
            jPanel3.add(pnlON, CC.xy(1, 1));
        }
        contentPane.add(jPanel3, CC.xy(3, 3));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));

            //---- btnClose ----
            btnClose.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnClose.addActionListener(e -> btnCloseActionPerformed(e));
            panel1.add(btnClose);

            //---- btnSave ----
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnSave.addActionListener(e -> btnSaveActionPerformed(e));
            panel1.add(btnSave);
        }
        contentPane.add(panel1, CC.xy(5, 5, CC.RIGHT, CC.DEFAULT));
        setSize(1035, 515);
        setLocationRelativeTo(getOwner());

        //---- bgMedikament ----
        ButtonGroup bgMedikament = new ButtonGroup();
        bgMedikament.add(rbActive);
        bgMedikament.add(rbDate);
    }// </editor-fold>//GEN-END:initComponents

    public void initDialog() {
        fillComboBoxes();

        ignoreEvent = true;

        lblCheckResultAfter.setText(SYSTools.xx("nursingrecords.prescription.dlgOnDemand.outcomeCheck") + ":");

        rbActive.setText(SYSTools.xx("nursingrecords.prescription.dlgOnDemand.rbActive"));

        txtMed.setText("");
        txtEDosis.setText(SYSTools.formatBigDecimal(BigDecimal.ONE));

        cmbMed.setRenderer(TradeFormTools.getRenderer(TradeFormTools.LONG));

        cmbMed.setModel(new DefaultComboBoxModel());
        cmbDocON.setSelectedItem(prescription.getDocON());
        cmbHospitalON.setSelectedItem(prescription.getHospitalON());
        txtBemerkung.setText(SYSTools.catchNull(prescription.getText()));

        if (prescription.hasMed()) {
            cmbMed.setModel(new DefaultComboBoxModel(new TradeForm[]{prescription.getTradeForm()}));
        }

        cmbIntervention.setModel(new DefaultComboBoxModel(InterventionTools.findBy(InterventionTools.TYPE_PRESCRIPTION).toArray()));
        cmbIntervention.setRenderer(InterventionTools.getRenderer());
        cmbIntervention.setEnabled(cmbMed.getModel().getSize() == 0);
        txtIntervention.setEnabled(cmbIntervention.isEnabled());
        cmbIntervention.setSelectedItem(prescription.getIntervention());

        cmbCheckAfter.setSelectedItem(schedule.getCheckAfterHours());

        cmbSit.setRenderer(SituationsTools.getSituationenRenderer());
        cmbSit.setModel(new DefaultComboBoxModel(new Situations[]{prescription.getSituation()}));
        txtSit.setText("");

        txtMaxTimes.setText(schedule.getMaxAnzahl().toString());
        txtEDosis.setText(SYSTools.formatBigDecimal(schedule.getMaxEDosis().setScale(2, RoundingMode.HALF_UP)));

        pnlCommonTags = new PnlCommonTags(prescription.getCommontags(), true, 3);
        jPanel1.add(new JScrollPane(pnlCommonTags), CC.xyw(1, 11, 5));


        ignoreEvent = false;
        txtMed.requestFocus();

    }

    private void txtMedFocusGained(FocusEvent evt) {//GEN-FIRST:event_txtMedFocusGained
        SYSTools.markAllTxt(txtMed);
    }//GEN-LAST:event_txtMedFocusGained

    private void btnMedActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMedActionPerformed

//        String pzan = MedPackageTools.parsePZN(txtMed.getText());
        final JidePopup popup = new JidePopup();

        WizardDialog wizard = new MedProductWizard(o -> {
            if (o != null) {
                MedPackage aPackage = (MedPackage) o;
                txtMed.setText(aPackage.getPzn());
            }
            popup.hidePopup();
        }).getWizard();

        popup.setMovable(false);
        popup.setPreferredSize((new Dimension(800, 450)));
        popup.setResizable(false);
        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
        popup.getContentPane().add(wizard.getContentPane());
        popup.setOwner(btnMedWizard);
        popup.removeExcludedComponent(btnMedWizard);
        popup.setTransient(true);
        popup.setDefaultFocusComponent(wizard.getContentPane());

        popup.showPopup(new Insets(-5, wizard.getPreferredSize().width * -1 - 200, -5, -100), btnMedWizard);

    }//GEN-LAST:event_btnMedActionPerformed


    private void cmbMedItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_cmbMedItemStateChanged
        cmbIntervention.setModel(new DefaultComboBoxModel(InterventionTools.findBy(InterventionTools.TYPE_PRESCRIPTION).toArray()));
        cmbIntervention.setSelectedItem(((TradeForm) cmbMed.getSelectedItem()).getDosageForm().getIntervention());
        cmbIntervention.setEnabled(false);
        txtIntervention.setText(null);
        txtIntervention.setEnabled(false);
    }//GEN-LAST:event_cmbMedItemStateChanged

    private boolean saveOK() {
        if (ignoreEvent) return false;
        boolean OnOK = (cmbDocON.getSelectedIndex() > 0 || cmbHospitalON.getSelectedIndex() > 0);
//        boolean OffOK = rbActive.isSelected() || jdcAB.getDate() != null;
        boolean sitOK = cmbSit.getSelectedItem() != null;
        boolean medOK = cmbMed.getModel().getSize() == 0 || cmbMed.getSelectedItem() != null;
        boolean intervOK = cmbIntervention.getSelectedItem() != null;

        boolean doseOK = true;
        try {
            BigDecimal parsedDecimal = SYSTools.parseDecimal(txtEDosis.getText());
            if (parsedDecimal == null || parsedDecimal.equals(BigDecimal.ZERO)) {
                throw new NumberFormatException("Alle Dosierungen sind 0.");
            }

            if (Integer.parseInt(txtMaxTimes.getText()) == 0) {
                throw new NumberFormatException("Die Anzahl ist Null.");
            }
        } catch (NumberFormatException nfe) {
            doseOK = false;
        }

        String reason = "";

        reason += (OnOK ? "" : SYSTools.xx("nursingrecords.prescription.dlgOnDemand.NotOnOK") + " ");
        reason += (medOK ? "" : SYSTools.xx("nursingrecords.prescription.dlgOnDemand.NotMedOK") + " ");
        reason += (sitOK ? "" : SYSTools.xx("nursingrecords.prescription.dlgOnDemand.NotSitOK") + " ");
        reason += (intervOK ? "" : SYSTools.xx("nursingrecords.prescription.dlgOnDemand.NotIntervOK") + " ");
        reason += (doseOK ? "" : SYSTools.xx("nursingrecords.prescription.dlgOnDemand.NotDoseOK") + " ");

        if (!reason.isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(reason, DisplayMessage.WARNING));
        }
        return OnOK & medOK & intervOK & doseOK & sitOK;
    }

    private void btnSaveActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (save()) {
            resultPrescription = prescription;
            dispose();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    @Override
    public void dispose() {
        actionBlock.execute(resultPrescription);
        SYSTools.unregisterListeners(this);
        super.dispose();
    }

    private void cmbSitItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_cmbSitItemStateChanged
        if (ignoreEvent) return;

//        saveOK();

    }//GEN-LAST:event_cmbSitItemStateChanged

    private void txtBemerkungCaretUpdate(CaretEvent evt) {//GEN-FIRST:event_txtBemerkungCaretUpdate

    }//GEN-LAST:event_txtBemerkungCaretUpdate

    private boolean save() {
        if (!saveOK()) return false;

        schedule.setNachtMo(BigDecimal.ZERO);
        schedule.setMorgens(BigDecimal.ZERO);
        schedule.setMittags(BigDecimal.ZERO);
        schedule.setNachmittags(BigDecimal.ZERO);
        schedule.setAbends(BigDecimal.ZERO);
        schedule.setNachtAb(BigDecimal.ZERO);
        schedule.setUhrzeitDosis(BigDecimal.ZERO);
        schedule.setUhrzeit(null);

        schedule.setTaeglich((short) 0);
        schedule.setWoechentlich((short) 0);
        schedule.setMonatlich((short) 0);
        schedule.setLDatum(new Date());

        schedule.setMon((short) 0);
        schedule.setTue((short) 0);
        schedule.setWed((short) 0);
        schedule.setThu((short) 0);
        schedule.setFri((short) 0);
        schedule.setSat((short) 0);
        schedule.setSun((short) 0);

        schedule.setTagNum((short) 0);

        schedule.setCheckAfterHours((BigDecimal) cmbCheckAfter.getSelectedItem());

        schedule.setMaxEDosis(SYSTools.parseDecimal(txtEDosis.getText()));
        schedule.setMaxAnzahl(Integer.parseInt(txtMaxTimes.getText()));

        prescription.setHospitalON((Hospital) cmbHospitalON.getSelectedItem());
        prescription.setIntervention((Intervention) cmbIntervention.getSelectedItem());
        prescription.setShowOnDailyPlan(false);
        prescription.setSituation((Situations) cmbSit.getSelectedItem());
        prescription.setText(txtBemerkung.getText().trim());
        prescription.setTradeForm((TradeForm) cmbMed.getSelectedItem());
        prescription.setUserON(OPDE.getLogin().getUser());
        prescription.setDocON((GP) cmbDocON.getSelectedItem());

        prescription.setFrom(new LocalDate().toDate());
        if (rbDate.isSelected()) {
            prescription.setTo(SYSCalendar.eod(new LocalDate(SYSCalendar.parseDate(txtOFF.getText()))).toDate());
            prescription.setUserOFF(OPDE.getLogin().getUser());
            prescription.setHospitalOFF(prescription.getHospitalON());
            prescription.setDocOFF(prescription.getDocON());
            prescription.setTo(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
        } else {
            prescription.setHospitalOFF(null);
            prescription.setDocOFF(null);
            prescription.setTo(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
        }
        prescription.setUntilEndOfPackage(false);

        prescription.getPrescriptionSchedule().clear();
        prescription.getPrescriptionSchedule().add(schedule);

        prescription.getCommontags().clear();
        prescription.getCommontags().addAll(pnlCommonTags.getListSelectedTags());

        return true;
    }

    private void btnCloseActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        resultPrescription = null;
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void fillComboBoxes() {
        EntityManager em = OPDE.createEM();
        Query queryArzt = em.createQuery("SELECT a FROM GP a WHERE a.status >= 0 ORDER BY a.name, a.vorname");
        listAerzte = queryArzt.getResultList();
        listAerzte.add(0, null);

        Query queryKH = em.createQuery("SELECT k FROM Hospital k WHERE k.state >= 0 ORDER BY k.name");
        listKH = queryKH.getResultList();
        listKH.add(0, null);
        em.close();

        cmbDocON.setModel(new DefaultComboBoxModel(listAerzte.toArray()));
        cmbDocON.setRenderer(GPTools.getRenderer());
        cmbDocON.setSelectedIndex(0);

        cmbHospitalON.setModel(new DefaultComboBoxModel(listKH.toArray()));
        cmbHospitalON.setRenderer(HospitalTools.getKHRenderer());
        cmbHospitalON.setSelectedIndex(0);


        cmbCheckAfter.setModel(new DefaultComboBoxModel(new BigDecimal[]{null, new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3"), new BigDecimal("4"), new BigDecimal("5"), new BigDecimal("6"), new BigDecimal("8"), new BigDecimal("12"), new BigDecimal("18"), new BigDecimal("24")}));
        cmbCheckAfter.setRenderer(getRenderer());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel1;
    private JXSearchField txtMed;
    private JComboBox<String> cmbMed;
    private JPanel panel4;
    private JButton btnMedWizard;
    private JComboBox<String> cmbIntervention;
    private JXSearchField txtSit;
    private JComboBox<String> cmbSit;
    private JPanel panel3;
    private JButton btnAddSit;
    private JXSearchField txtIntervention;
    private JPanel jPanel2;
    private JLabel lblNumber;
    private JLabel lblDose;
    private JLabel lblMaxPerDay;
    private JTextField txtMaxTimes;
    private JLabel lblX;
    private JTextField txtEDosis;
    private JLabel lblCheckResultAfter;
    private JComboBox<String> cmbCheckAfter;
    private JPanel jPanel3;
    private JPanel pnlOFF;
    private JRadioButton rbActive;
    private JRadioButton rbDate;
    private JTextField txtOFF;
    private JScrollPane jScrollPane3;
    private JTextPane txtBemerkung;
    private JLabel lblText;
    private JPanel pnlON;
    private JComboBox<String> cmbDocON;
    private JComboBox<String> cmbHospitalON;
    private JPanel panel1;
    private JButton btnClose;
    private JButton btnSave;
    // End of variables declaration//GEN-END:variables

}
