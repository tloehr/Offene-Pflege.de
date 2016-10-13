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
package op.care.prescription;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.wizard.WizardDialog;
import entity.EntityTools;
import entity.nursingprocess.Intervention;
import entity.nursingprocess.InterventionTools;
import entity.prescription.*;
import gui.GUITools;
import op.OPDE;
import op.care.med.prodassistant.MedProductWizard;
import op.residents.PnlEditGP;
import op.residents.PnlEditHospital;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.joda.time.LocalDate;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Bei einer Verordnungsänderung wird die alte Verordnung abgesetzt und die neue Verordnung statt dessen angesetzt. BHPs werden immer erstellt.
 * Bei neuen Verordnungen immer für den ganzen Tag. Bei Änderung immer ab dem Moment, ab dem die neue Verordnung gilt.
 *
 * @author tloehr
 */
public class DlgRegular extends MyJDialog {

    public static final int MODE_EDIT = 0;
    public static final int MODE_CHANGE = 1;
    public static final int MODE_NEW = 2;

    private boolean ignoreEvent;

    private List<GP> listAerzte;
    private List<Hospital> listKH;

    private JPopupMenu menu;
    private int editMode;
    private Closure actionBlock;
    private Prescription prescription, resultPrescription;
    private List<PrescriptionSchedule> schedules2delete = null;
//    private Pair<Prescription, List<PrescriptionSchedule>> returnPackage = null;

    private JToggleButton tbDailyPlan;
    private PnlCommonTags pnlCommonTags;

    /**
     * Creates new form DlgRegular
     */
    public DlgRegular(Prescription prescription, int mode, Closure actionBlock) {
        super();
        setResizable(false);
        this.actionBlock = actionBlock;
        this.prescription = prescription;
        schedules2delete = new ArrayList<PrescriptionSchedule>();
        this.editMode = mode;
        initComponents();
        initDialog();
        pack();
//        setVisible(true);
    }

    private void btnAddDosisActionPerformed(ActionEvent e) {
        final JidePopup popup = new JidePopup();

        JPanel dlg = new PnlScheduleDose(new PrescriptionSchedule(prescription), o -> {
            if (o != null) {
                prescription.getPrescriptionSchedule().add(((PrescriptionSchedule) o));
                reloadTable();
                popup.hidePopup();
            }
        });

        popup.setMovable(false);
        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
        popup.getContentPane().add(dlg);
        popup.setOwner(btnAddDosis);
        popup.removeExcludedComponent(btnAddDosis);
        popup.setDefaultFocusComponent(dlg);

        GUITools.showPopup(popup, SwingConstants.NORTH);

    }


    private void txtMedActionPerformed(ActionEvent e) {
        if (txtMed.getText().isEmpty()) {
            cmbMed.setModel(new DefaultComboBoxModel());
            cmbIntervention.setEnabled(true);
            txtIntervention.setEnabled(true);
            tbDailyPlan.setEnabled(true);
            tbDailyPlan.setSelected(false);
            rbEndOfPackage.setEnabled(false);
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
//                tbWeightControl.setEnabled(true);
//                                tbWeightControl.setSelected(false);
            } else {
                cmbMed.setToolTipText("");
                cmbIntervention.setSelectedIndex(-1);
                cmbIntervention.setEnabled(true);
                txtIntervention.setEnabled(true);
                tbDailyPlan.setEnabled(true);
                tbDailyPlan.setSelected(false);
                OPDE.getDisplayManager().clearSubMessages();
            }
            rbEndOfPackage.setEnabled(prescription.getResident().isCalcMediUPR1() && cmbMed.getModel().getSize() > 0);
        }
    }

    private void rbActiveItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            txtTo.setText("");
        }
    }

    private void rbDateItemStateChanged(ItemEvent e) {
        txtTo.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            txtTo.setText(DateFormat.getDateInstance().format(new Date()));
        }
    }

    private void rbEndOfPackageItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            txtTo.setText("");
        }
    }

    private void txtToFocusLost(FocusEvent evt) {
        SYSCalendar.handleDateFocusLost(evt, new LocalDate(), new LocalDate().plusYears(1));
    }

    private void cmbDocONKeyPressed(KeyEvent e) {
        final String searchKey = String.valueOf(e.getKeyChar());
        GP doc = (GP) CollectionUtils.find(listAerzte, o -> o != null && ((GP) o).getName().toLowerCase().charAt(0) == searchKey.toLowerCase().charAt(0));
        if (doc != null) {
            cmbDocON.setSelectedItem(doc);
        }
    }

    private void btnAddGPActionPerformed(ActionEvent e) {
        final PnlEditGP pnlGP = new PnlEditGP(new GP());
        JidePopup popup = GUITools.createPanelPopup(pnlGP, o -> {
            if (o != null) {
                cmbDocON.setModel(new DefaultComboBoxModel(new GP[]{(GP) o}));
            }
        }, btnAddGP);
        GUITools.showPopup(popup, SwingConstants.EAST);
    }

    private void btnAddHospitalActionPerformed(ActionEvent e) {
        final PnlEditHospital pnlHospital = new PnlEditHospital(new Hospital());
        JidePopup popup = GUITools.createPanelPopup(pnlHospital, o -> {
            if (o != null) {
                cmbHospitalON.setModel(new DefaultComboBoxModel(new Hospital[]{(Hospital) o}));
            }
        }, btnAddHospital);
        GUITools.showPopup(popup, SwingConstants.EAST);
    }

    private void thisWindowClosing(WindowEvent e) {
        resultPrescription = null;
        // the red button was pressed
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
        btnMed = new JButton();
        cmbIntervention = new JComboBox<>();
        txtIntervention = new JXSearchField();
        jPanel8 = new JPanel();
        jspDosis = new JScrollPane();
        tblDosis = new JTable();
        panel2 = new JPanel();
        btnAddDosis = new JButton();
        jPanel3 = new JPanel();
        pnlOFF = new JPanel();
        rbActive = new JRadioButton();
        rbDate = new JRadioButton();
        txtTo = new JTextField();
        rbEndOfPackage = new JRadioButton();
        jScrollPane3 = new JScrollPane();
        txtBemerkung = new JTextPane();
        lblText = new JLabel();
        pnlON = new JPanel();
        cmbDocON = new JComboBox<>();
        btnAddGP = new JButton();
        cmbHospitalON = new JComboBox<>();
        btnAddHospital = new JButton();
        panel1 = new JPanel();
        btnClose = new JButton();
        btnSave = new JButton();
        lblTX = new JLabel();

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
            "$rgap, $lcgap, default, $lcgap, pref, $lcgap, $rgap",
            "$rgap, $lgap, fill:default:grow, $lgap, fill:default, $lgap, $rgap"));

        //======== jPanel1 ========
        {
            jPanel1.setBorder(null);
            jPanel1.setLayout(new FormLayout(
                "68dlu, $lcgap, 284dlu, $lcgap, pref",
                "2*(16dlu, $lgap), default, $lgap, fill:default:grow"));

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
            cmbMed.setModel(new DefaultComboBoxModel<>(new String[] {
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

                //---- btnMed ----
                btnMed.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                btnMed.setBorderPainted(false);
                btnMed.setBorder(null);
                btnMed.setContentAreaFilled(false);
                btnMed.setToolTipText("Neues Medikament eintragen");
                btnMed.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnMed.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png")));
                btnMed.addActionListener(e -> btnMedActionPerformed(e));
                panel4.add(btnMed);
            }
            jPanel1.add(panel4, CC.xy(5, 1));

            //---- cmbIntervention ----
            cmbIntervention.setModel(new DefaultComboBoxModel<>(new String[] {
                "Item 1",
                "Item 2",
                "Item 3",
                "Item 4"
            }));
            cmbIntervention.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel1.add(cmbIntervention, CC.xywh(3, 3, 3, 1));

            //---- txtIntervention ----
            txtIntervention.setFont(new Font("Arial", Font.PLAIN, 14));
            txtIntervention.setPrompt("Massnahmen");
            txtIntervention.addActionListener(e -> txtMassActionPerformed(e));
            jPanel1.add(txtIntervention, CC.xy(1, 3));

            //======== jPanel8 ========
            {
                jPanel8.setBorder(new TitledBorder(null, "Dosis / H\u00e4ufigkeit", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
                    new Font("Arial", Font.PLAIN, 14)));
                jPanel8.setFont(new Font("Arial", Font.PLAIN, 14));
                jPanel8.setLayout(new FormLayout(
                    "370dlu",
                    "fill:default:grow, $lgap, pref"));

                //======== jspDosis ========
                {
                    jspDosis.setToolTipText(null);

                    //---- tblDosis ----
                    tblDosis.setModel(new DefaultTableModel(
                        new Object[][] {
                            {null, null, null, null},
                            {null, null, null, null},
                            {null, null, null, null},
                            {null, null, null, null},
                        },
                        new String[] {
                            "Title 1", "Title 2", "Title 3", "Title 4"
                        }
                    ));
                    tblDosis.setSurrendersFocusOnKeystroke(true);
                    tblDosis.setToolTipText(null);
                    tblDosis.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            tblDosisMousePressed(e);
                        }
                    });
                    jspDosis.setViewportView(tblDosis);
                }
                jPanel8.add(jspDosis, CC.xy(1, 1));

                //======== panel2 ========
                {
                    panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));

                    //---- btnAddDosis ----
                    btnAddDosis.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                    btnAddDosis.setBorderPainted(false);
                    btnAddDosis.setBorder(null);
                    btnAddDosis.setContentAreaFilled(false);
                    btnAddDosis.setToolTipText("Neue Dosierung eintragen");
                    btnAddDosis.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btnAddDosis.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png")));
                    btnAddDosis.addActionListener(e -> btnAddDosisActionPerformed(e));
                    panel2.add(btnAddDosis);
                }
                jPanel8.add(panel2, CC.xy(1, 3, CC.LEFT, CC.DEFAULT));
            }
            jPanel1.add(jPanel8, CC.xywh(1, 7, 5, 1));
        }
        contentPane.add(jPanel1, CC.xy(5, 3));

        //======== jPanel3 ========
        {
            jPanel3.setBorder(null);
            jPanel3.setLayout(new FormLayout(
                "149dlu",
                "3*(fill:default, $lgap), fill:108dlu:grow, $lgap, 60dlu"));

            //======== pnlOFF ========
            {
                pnlOFF.setBorder(new TitledBorder("Absetzung"));
                pnlOFF.setLayout(new FormLayout(
                    "pref, 86dlu:grow",
                    "2*(fill:17dlu, $lgap), fill:17dlu"));

                //---- rbActive ----
                rbActive.setText("text");
                rbActive.setSelected(true);
                rbActive.addItemListener(e -> rbActiveItemStateChanged(e));
                pnlOFF.add(rbActive, CC.xywh(1, 1, 2, 1));

                //---- rbDate ----
                rbDate.setText(null);
                rbDate.addItemListener(e -> rbDateItemStateChanged(e));
                pnlOFF.add(rbDate, CC.xy(1, 3));

                //---- txtTo ----
                txtTo.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtToFocusLost(e);
                    }
                });
                pnlOFF.add(txtTo, CC.xy(2, 3));

                //---- rbEndOfPackage ----
                rbEndOfPackage.setText("text");
                rbEndOfPackage.addItemListener(e -> rbEndOfPackageItemStateChanged(e));
                pnlOFF.add(rbEndOfPackage, CC.xywh(1, 5, 2, 1));
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
                    "119dlu:grow, $lcgap, default",
                    "default, $lgap, default"));

                //---- cmbDocON ----
                cmbDocON.setModel(new DefaultComboBoxModel<>(new String[] {
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

                //---- btnAddGP ----
                btnAddGP.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                btnAddGP.setBorderPainted(false);
                btnAddGP.setBorder(null);
                btnAddGP.setContentAreaFilled(false);
                btnAddGP.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnAddGP.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png")));
                btnAddGP.addActionListener(e -> btnAddGPActionPerformed(e));
                pnlON.add(btnAddGP, CC.xy(3, 1));

                //---- cmbHospitalON ----
                cmbHospitalON.setModel(new DefaultComboBoxModel<>(new String[] {
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
                }));
                pnlON.add(cmbHospitalON, CC.xy(1, 3));

                //---- btnAddHospital ----
                btnAddHospital.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                btnAddHospital.setBorderPainted(false);
                btnAddHospital.setBorder(null);
                btnAddHospital.setContentAreaFilled(false);
                btnAddHospital.setToolTipText("Neues Medikament eintragen");
                btnAddHospital.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnAddHospital.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png")));
                btnAddHospital.addActionListener(e -> btnAddHospitalActionPerformed(e));
                pnlON.add(btnAddHospital, CC.xy(3, 3));
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

        //---- lblTX ----
        lblTX.setText(null);
        lblTX.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ambulance2.png")));
        contentPane.add(lblTX, CC.xy(3, 5));
        setSize(1015, 585);
        setLocationRelativeTo(getOwner());

        //---- bgMedikament ----
        ButtonGroup bgMedikament = new ButtonGroup();
        bgMedikament.add(rbActive);
        bgMedikament.add(rbDate);
        bgMedikament.add(rbEndOfPackage);
    }// </editor-fold>//GEN-END:initComponents

    public void initDialog() {
        fillComboBoxes();

        String tooltip = SYSTools.xx("nursingrecords.prescription.dlgRegular.tx.tooltip").replace('[', '<').replace(']', '>');
        lblTX.setToolTipText(SYSTools.toHTMLForScreen("<p style=\"width:300px;\">" + tooltip + "</p>"));

        ignoreEvent = true;

        rbActive.setText(SYSTools.xx("nursingrecords.prescription.dlgRegular.rbActive"));
        rbEndOfPackage.setText(SYSTools.xx("nursingrecords.prescription.dlgRegular.rbEndOfPackage"));

        txtMed.setText("");
        cmbMed.setRenderer(TradeFormTools.getRenderer(TradeFormTools.LONG));
        cmbDocON.setSelectedItem(prescription.getDocON());
        cmbHospitalON.setSelectedItem(prescription.getHospitalON());
        cmbMed.setModel(new DefaultComboBoxModel());

        rbEndOfPackage.setSelected(prescription.isUntilEndOfPackage());

        txtBemerkung.setText(SYSTools.catchNull(prescription.getText()));

        if (prescription.hasMed()) {
            cmbMed.setModel(new DefaultComboBoxModel(new TradeForm[]{prescription.getTradeForm()}));
        }

        cmbIntervention.setModel(new DefaultComboBoxModel(InterventionTools.findBy(InterventionTools.TYPE_PRESCRIPTION).toArray()));
        cmbIntervention.setRenderer(InterventionTools.getRenderer());
        cmbIntervention.setEnabled(cmbMed.getModel().getSize() == 0);
        cmbIntervention.setSelectedItem(prescription.getIntervention());
        txtIntervention.setEnabled(cmbMed.getModel().getSize() == 0);

        tbDailyPlan = GUITools.getNiceToggleButton("nursingrecords.prescription.dlgRegular.addToDailyPlan");
        jPanel1.add(tbDailyPlan, CC.xywh(1, 5, 5, 1, CC.LEFT, CC.DEFAULT));
        tbDailyPlan.setEnabled(cmbMed.getModel().getSize() == 0);
        tbDailyPlan.setSelected(prescription.isOnDailyPlan());

//        tbWeightControl = GUITools.getNiceToggleButton("nursingrecords.prescription.dlgRegular.weightControl");
//        jPanel1.add(tbWeightControl, CC.xywh(1, 7, 5, 1, CC.LEFT, CC.DEFAULT));
//        tbWeightControl.setEnabled(cmbMed.getModel().getSize() != 0);
//        tbWeightControl.setSelected(prescription.isWeightControl());

        cmbMed.setEnabled(editMode != MODE_CHANGE);
        txtMed.setEnabled(editMode != MODE_CHANGE);
        cmbIntervention.setEnabled(editMode != MODE_CHANGE);
        txtIntervention.setEnabled(editMode != MODE_CHANGE);

        if (cmbMed.getSelectedItem() != null) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(TradeFormTools.toPrettyString((TradeForm) cmbMed.getSelectedItem())));
        }
        rbEndOfPackage.setEnabled(prescription.getResident().isCalcMediUPR1() && cmbMed.getSelectedItem() != null);

        ignoreEvent = false;
        txtMed.requestFocus();

        setFocusCycleRoot(true);
        setFocusTraversalPolicy(GUITools.createTraversalPolicy(new ArrayList<Component>(Arrays.asList(new Component[]{cmbDocON, cmbHospitalON, txtMed, rbActive, txtBemerkung}))));

        reloadTable();

        // #28
        btnMed.setToolTipText(SYSTools.xx("nursingrecords.prescription.dlgRegular.addMed.tooltip"));
        btnAddGP.setToolTipText(SYSTools.xx("nursingrecords.prescription.dlgRegular.addGP.tooltip"));
        btnAddHospital.setToolTipText(SYSTools.xx("nursingrecords.prescription.dlgRegular.addHospital.tooltip"));

        pnlCommonTags = new PnlCommonTags(prescription.getCommontags(), true, 3);
        jPanel3.add(new JScrollPane(pnlCommonTags), CC.xy(1, 9, CC.DEFAULT, CC.FILL));
    }

    private void txtMedFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMedFocusGained
        SYSTools.markAllTxt(txtMed);
    }//GEN-LAST:event_txtMedFocusGained

    private void btnMedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMedActionPerformed

//        String pzn = MedPackageTools.parsePZN(txtMed.getText());
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
        popup.setOwner(btnMed);
        popup.removeExcludedComponent(btnMed);
        popup.setTransient(true);
        popup.setDefaultFocusComponent(wizard.getContentPane());

        popup.showPopup(new Insets(-5, wizard.getPreferredSize().width * -1 - 200, -5, -100), btnMed);

    }//GEN-LAST:event_btnMedActionPerformed


    private void cmbMedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbMedItemStateChanged
        cmbIntervention.setModel(new DefaultComboBoxModel(InterventionTools.findBy(InterventionTools.TYPE_PRESCRIPTION).toArray()));
        cmbIntervention.setSelectedItem(((TradeForm) cmbMed.getSelectedItem()).getDosageForm().getIntervention());
        cmbIntervention.setEnabled(false);
        txtIntervention.setText(null);
        txtIntervention.setEnabled(false);
        tbDailyPlan.setEnabled(false);
        tbDailyPlan.setSelected(false);
    }//GEN-LAST:event_cmbMedItemStateChanged


    private boolean saveOK() {
        if (ignoreEvent) return false;

        LocalDate day;
        try {
            day = new LocalDate(SYSCalendar.parseDate(txtTo.getText()));
        } catch (NumberFormatException ex) {
            day = null;
        }

        boolean OnOK = (cmbDocON.getSelectedItem() != null || cmbHospitalON.getSelectedIndex() > 0);
        boolean OffOK = !rbDate.isSelected() || day != null;
        boolean medOK = cmbMed.getModel().getSize() == 0 || cmbMed.getSelectedItem() != null;
        boolean intervOK = cmbIntervention.getSelectedItem() != null;
        boolean doseOK = tblDosis.getModel().getRowCount() > 0;

        String reason = "";
        reason += (OnOK ? "" : "Die Informationen zum <b>an</b>setzenden <b>Arzt</b> oder KH sind unvollständig. ");
        reason += (OffOK ? "" : "Die Informationen zum <b>ab</b>setzenden <b>Arzt</b> oder KH sind unvollständig. ");
        reason += (medOK ? "" : "Die <b>Medikamentenangabe</b> ist falsch. ");
        reason += (intervOK ? "" : "Die Angaben über die <b>Massnahmen</b> sind falsch. ");
        reason += (doseOK ? "" : "Sie müssen mindestens eine <b>Dosierung</b> angegeben. ");


        if (!reason.isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(reason, DisplayMessage.WARNING));
        }
        return OnOK & OffOK & medOK & intervOK & doseOK;

    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
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

    private void txtBemerkungCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBemerkungCaretUpdate

    }//GEN-LAST:event_txtBemerkungCaretUpdate

    private boolean save() {
        if (!saveOK()) return false;

        prescription.setHospitalON((Hospital) cmbHospitalON.getSelectedItem());
        prescription.setIntervention((Intervention) cmbIntervention.getSelectedItem());
        prescription.setShowOnDailyPlan(tbDailyPlan.isSelected());
        prescription.setSituation(null);
        prescription.setText(txtBemerkung.getText().trim());
        prescription.setTradeForm((TradeForm) cmbMed.getSelectedItem());
        prescription.setUserON(OPDE.getLogin().getUser());
        prescription.setDocON((GP) cmbDocON.getSelectedItem());

        prescription.setFrom(new Date());
        if (rbDate.isSelected()) {

            LocalDate day = null;
            try {
                day = new LocalDate(SYSCalendar.parseDate(txtTo.getText()));
            } catch (NumberFormatException ex) {
                OPDE.fatal(ex);
            }

            prescription.setTo(SYSCalendar.eod(day).toDate());
            prescription.setUserOFF(OPDE.getLogin().getUser());
            prescription.setHospitalOFF(prescription.getHospitalON());
            prescription.setDocOFF(prescription.getDocON());
        } else {
            prescription.setHospitalOFF(null);
            prescription.setDocOFF(null);
            prescription.setTo(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
        }
        prescription.setUntilEndOfPackage(rbEndOfPackage.isSelected());

        prescription.getCommontags().clear();
        prescription.getCommontags().addAll(pnlCommonTags.getListSelectedTags());


        return true;
    }


    private void tblDosisMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDosisMousePressed
        if (!SwingUtilities.isRightMouseButton(evt)) {
            return;
        }

        final TMDose tm = (TMDose) tblDosis.getModel();
        if (tm.getRowCount() == 0) {
            return;
        }
        Point p = evt.getPoint();
        Point p2 = evt.getPoint();
        // Convert a coordinate relative to a component's bounds to screen coordinates
        SwingUtilities.convertPointToScreen(p2, tblDosis);

//        final Point screenposition = p2;
        final int row = tblDosis.rowAtPoint(p);

        ListSelectionModel lsm = tblDosis.getSelectionModel();
        lsm.setSelectionInterval(row, row);

        // Menüeinträge
        SYSTools.unregisterListeners(menu);
        menu = new JPopupMenu();

        //-----------------------------------------
        JMenuItem itemPopupDelete = new JMenuItem(SYSTools.xx("misc.msg.delete"), SYSConst.icon22delete);
        itemPopupDelete.addActionListener(evt1 -> {
            PrescriptionSchedule schedule = prescription.getPrescriptionSchedule().get(row);
            prescription.getPrescriptionSchedule().remove(schedule);
            schedules2delete.add(schedule);
            reloadTable();
        });
        menu.add(itemPopupDelete);
        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());

    }//GEN-LAST:event_tblDosisMousePressed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
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
    }

    private void reloadTable() {
        String zubereitung = "x";
        if (prescription.getTradeForm() != null) {
            zubereitung = prescription.getTradeForm().getDosageForm().getPreparation();
        }

        tblDosis.setModel(new TMDose(zubereitung, prescription));
        tblDosis.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jspDosis.dispatchEvent(new ComponentEvent(jspDosis, ComponentEvent.COMPONENT_RESIZED));
        tblDosis.getColumnModel().getColumn(TMDose.COL_Dosis).setCellRenderer(new RNDHTML());
        tblDosis.getColumnModel().getColumn(TMDose.COL_Dosis).setHeaderValue(SYSTools.xx("misc.msg.usage"));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel1;
    private JXSearchField txtMed;
    private JComboBox<String> cmbMed;
    private JPanel panel4;
    private JButton btnMed;
    private JComboBox<String> cmbIntervention;
    private JXSearchField txtIntervention;
    private JPanel jPanel8;
    private JScrollPane jspDosis;
    private JTable tblDosis;
    private JPanel panel2;
    private JButton btnAddDosis;
    private JPanel jPanel3;
    private JPanel pnlOFF;
    private JRadioButton rbActive;
    private JRadioButton rbDate;
    private JTextField txtTo;
    private JRadioButton rbEndOfPackage;
    private JScrollPane jScrollPane3;
    private JTextPane txtBemerkung;
    private JLabel lblText;
    private JPanel pnlON;
    private JComboBox<String> cmbDocON;
    private JButton btnAddGP;
    private JComboBox<String> cmbHospitalON;
    private JButton btnAddHospital;
    private JPanel panel1;
    private JButton btnClose;
    private JButton btnSave;
    private JLabel lblTX;
    // End of variables declaration//GEN-END:variables

}
