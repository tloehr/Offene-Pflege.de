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
import entity.nursingprocess.Intervention;
import entity.nursingprocess.InterventionTools;
import entity.prescription.*;
import op.OPDE;
import op.care.med.prodassistant.MedProductWizard;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;
import org.joda.time.DateMidnight;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.util.ArrayList;
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

    private JPopupMenu menu;
    private int editMode;
    private Closure actionBlock;
    private Prescription prescription;
    private List<PrescriptionSchedule> schedules2delete = null;
    private Pair<Prescription, List<PrescriptionSchedule>> returnPackage = null;


    /**
     * Creates new form DlgRegular
     */
    public DlgRegular(Prescription prescription, int mode, Closure actionBlock) {
        this.actionBlock = actionBlock;
        this.prescription = prescription;
        schedules2delete = new ArrayList<PrescriptionSchedule>();
        this.editMode = mode;
        initComponents();
        initDialog();
        pack();
        setVisible(true);
    }

    private void btnAddDosisActionPerformed(ActionEvent e) {
        final JidePopup popup = new JidePopup();

        JPanel dlg = new PnlRegelDosis(new PrescriptionSchedule(prescription), new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    prescription.getPrescriptionSchedule().add(((PrescriptionSchedule) o));
                    reloadTable();
                    popup.hidePopup();
                }
            }
        });

        popup.setMovable(false);
        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
        popup.getContentPane().add(dlg);
        popup.setOwner(btnAddDosis);
        popup.removeExcludedComponent(btnAddDosis);
        popup.setDefaultFocusComponent(dlg);
//        popup.addPropertyChangeListener("visible", new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
//                OPDE.debug("popup property: " + propertyChangeEvent.getPropertyName() + " value: " + propertyChangeEvent.getNewValue() + " compCount: " + popup.getContentPane().getComponentCount());
//                popup.getContentPane().getComponentCount();
//            }
//        });

        GUITools.showPopup(popup, SwingConstants.NORTH);

    }


    private void txtMedActionPerformed(ActionEvent e) {
        if (txtMed.getText().isEmpty()) {
            cmbMed.setModel(new DefaultComboBoxModel());
            cmbIntervention.setEnabled(true);
            txtMass.setEnabled(true);
            cbDailyPlan.setEnabled(true);
            cbDailyPlan.setSelected(false);
            rbEndOfPackage.setEnabled(false);
        } else {
            OPDE.getDisplayManager().setDBActionMessage(true);
            EntityManager em = OPDE.createEM();

            String pzn = MedPackageTools.parsePZN(txtMed.getText());

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

            } else { // Falls die Suche NICHT nur aus Zahlen besteht, dann nach Namen suchen.
                cmbMed.setModel(new DefaultComboBoxModel(TradeFormTools.findTradeFormByMedProductText(em, txtMed.getText()).toArray()));
            }
            OPDE.getDisplayManager().setDBActionMessage(false);
            em.close();

            if (cmbMed.getModel().getSize() > 0) {
                cmbMedItemStateChanged(null);
            } else {
                cmbMed.setToolTipText("");
                cmbIntervention.setSelectedIndex(-1);
                cmbIntervention.setEnabled(true);
                txtMass.setEnabled(true);
                cbDailyPlan.setEnabled(true);
                cbDailyPlan.setSelected(false);
                OPDE.getDisplayManager().clearSubMessages();
            }
            rbEndOfPackage.setEnabled(cmbMed.getModel().getSize() > 0);
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
        SYSCalendar.handleDateFocusLost(evt, new DateMidnight(), new DateMidnight().plusYears(1));
    }

    private void txtMassActionPerformed(ActionEvent e) {
        cmbIntervention.setModel(new DefaultComboBoxModel(InterventionTools.findMassnahmenBy(InterventionTools.TYPE_PRESCRIPTION, txtMass.getText()).toArray()));
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new JPanel();
        txtMed = new JXSearchField();
        cmbMed = new JComboBox();
        panel4 = new JPanel();
        btnMed = new JButton();
        cmbIntervention = new JComboBox();
        txtMass = new JXSearchField();
        jPanel8 = new JPanel();
        jspDosis = new JScrollPane();
        tblDosis = new JTable();
        panel2 = new JPanel();
        btnAddDosis = new JButton();
        cbDailyPlan = new JCheckBox();
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
        cmbDocON = new JComboBox();
        cmbHospitalON = new JComboBox();
        panel1 = new JPanel();
        btnClose = new JButton();
        btnSave = new JButton();

        //======== this ========
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
                "$rgap, $lcgap, default, $lcgap, default:grow, $lcgap, $rgap",
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
            txtMed.setFocusBehavior(org.jdesktop.swingx.prompt.PromptSupport.FocusBehavior.HIGHLIGHT_PROMPT);
            txtMed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtMedActionPerformed(e);
                }
            });
            txtMed.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtMedFocusGained(e);
                }
            });
            jPanel1.add(txtMed, CC.xy(1, 1));

            //---- cmbMed ----
            cmbMed.setModel(new DefaultComboBoxModel(new String[]{
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
            }));
            cmbMed.setFont(new Font("Arial", Font.PLAIN, 14));
            cmbMed.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cmbMedItemStateChanged(e);
                }
            });
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
                btnMed.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnMedActionPerformed(e);
                    }
                });
                panel4.add(btnMed);
            }
            jPanel1.add(panel4, CC.xy(5, 1));

            //---- cmbIntervention ----
            cmbIntervention.setModel(new DefaultComboBoxModel(new String[]{
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
            }));
            cmbIntervention.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel1.add(cmbIntervention, CC.xywh(3, 3, 3, 1));

            //---- txtMass ----
            txtMass.setFont(new Font("Arial", Font.PLAIN, 14));
            txtMass.setPrompt("Massnahmen");
            txtMass.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtMassActionPerformed(e);
                }
            });
            jPanel1.add(txtMass, CC.xy(1, 3));

            //======== jPanel8 ========
            {
                jPanel8.setBorder(new TitledBorder(null, "Dosis / H\u00e4ufigkeit", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
                        new Font("Arial", Font.PLAIN, 14)));
                jPanel8.setFont(new Font("Arial", Font.PLAIN, 14));
                jPanel8.setLayout(new FormLayout(
                        "default:grow",
                        "fill:default:grow, $lgap, pref"));

                //======== jspDosis ========
                {
                    jspDosis.setToolTipText(null);

                    //---- tblDosis ----
                    tblDosis.setModel(new DefaultTableModel(
                            new Object[][]{
                                    {null, null, null, null},
                                    {null, null, null, null},
                                    {null, null, null, null},
                                    {null, null, null, null},
                            },
                            new String[]{
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
                    btnAddDosis.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnAddDosisActionPerformed(e);
                        }
                    });
                    panel2.add(btnAddDosis);
                }
                jPanel8.add(panel2, CC.xy(1, 3, CC.LEFT, CC.DEFAULT));
            }
            jPanel1.add(jPanel8, CC.xywh(1, 7, 5, 1));

            //---- cbDailyPlan ----
            cbDailyPlan.setText("Auf den Stellplan, auch wenn kein Medikament");
            cbDailyPlan.setBorder(BorderFactory.createEmptyBorder());
            cbDailyPlan.setMargin(new Insets(0, 0, 0, 0));
            cbDailyPlan.setFont(new Font("Arial", Font.PLAIN, 14));
            cbDailyPlan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            jPanel1.add(cbDailyPlan, CC.xywh(1, 5, 5, 1));
        }
        contentPane.add(jPanel1, CC.xy(5, 3));

        //======== jPanel3 ========
        {
            jPanel3.setBorder(null);
            jPanel3.setLayout(new FormLayout(
                    "149dlu",
                    "3*(fill:default, $lgap), fill:default:grow"));

            //======== pnlOFF ========
            {
                pnlOFF.setBorder(new TitledBorder("Absetzung"));
                pnlOFF.setLayout(new FormLayout(
                        "pref, 86dlu:grow",
                        "2*(fill:17dlu, $lgap), fill:17dlu"));

                //---- rbActive ----
                rbActive.setText("text");
                rbActive.setSelected(true);
                rbActive.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        rbActiveItemStateChanged(e);
                    }
                });
                pnlOFF.add(rbActive, CC.xywh(1, 1, 2, 1));

                //---- rbDate ----
                rbDate.setText(null);
                rbDate.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        rbDateItemStateChanged(e);
                    }
                });
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
                rbEndOfPackage.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        rbEndOfPackageItemStateChanged(e);
                    }
                });
                pnlOFF.add(rbEndOfPackage, CC.xywh(1, 5, 2, 1));
            }
            jPanel3.add(pnlOFF, CC.xy(1, 3));

            //======== jScrollPane3 ========
            {

                //---- txtBemerkung ----
                txtBemerkung.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtBemerkungCaretUpdate(e);
                    }
                });
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
                cmbDocON.setModel(new DefaultComboBoxModel(new String[]{
                        "Item 1",
                        "Item 2",
                        "Item 3",
                        "Item 4"
                }));
                pnlON.add(cmbDocON, CC.xy(1, 1));

                //---- cmbHospitalON ----
                cmbHospitalON.setModel(new DefaultComboBoxModel(new String[]{
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
            btnClose.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnCloseActionPerformed(e);
                }
            });
            panel1.add(btnClose);

            //---- btnSave ----
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });
            panel1.add(btnSave);
        }
        contentPane.add(panel1, CC.xy(5, 5, CC.RIGHT, CC.DEFAULT));
        setSize(1070, 640);
        setLocationRelativeTo(getOwner());

        //---- bgMedikament ----
        ButtonGroup bgMedikament = new ButtonGroup();
        bgMedikament.add(rbActive);
        bgMedikament.add(rbDate);
        bgMedikament.add(rbEndOfPackage);
    }// </editor-fold>//GEN-END:initComponents

    public void initDialog() {
        fillComboBoxes();

        ignoreEvent = true;

        rbActive.setText(OPDE.lang.getString(PnlPrescription.internalClassID + ".dlgRegular.rbActive"));
        rbEndOfPackage.setText(OPDE.lang.getString(PnlPrescription.internalClassID + ".dlgRegular.rbEndOfPackage"));

        txtMed.setText("");
//        jdcAB.setMinSelectableDate(new Date());

        cmbMed.setRenderer(TradeFormTools.getDarreichungRenderer(TradeFormTools.LONG));
        cmbDocON.setSelectedItem(prescription.getDocON());
        cmbHospitalON.setSelectedItem(prescription.getHospitalON());
        cmbMed.setModel(new DefaultComboBoxModel());

        rbEndOfPackage.setSelected(prescription.isTillEndOfPackage());

        txtBemerkung.setText(SYSTools.catchNull(prescription.getText()));

        if (prescription.hasMed()) {
            cmbMed.setModel(new DefaultComboBoxModel(new TradeForm[]{prescription.getTradeForm()}));
        }

        cmbIntervention.setModel(new DefaultComboBoxModel(InterventionTools.findMassnahmenBy(InterventionTools.TYPE_PRESCRIPTION).toArray()));
        cmbIntervention.setRenderer(InterventionTools.getMassnahmenRenderer());
        cmbIntervention.setEnabled(cmbMed.getModel().getSize() == 0);
        cmbIntervention.setSelectedItem(prescription.getIntervention());
        txtMass.setEnabled(cmbMed.getModel().getSize() == 0);

        cbDailyPlan.setEnabled(cmbMed.getModel().getSize() == 0);
        cbDailyPlan.setSelected(prescription.isOnDailyPlan());

        cmbMed.setEnabled(editMode != MODE_CHANGE);
        txtMed.setEnabled(editMode != MODE_CHANGE);

        if (cmbMed.getSelectedItem() != null) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(TradeFormTools.toPrettyString((TradeForm) cmbMed.getSelectedItem())));
            rbEndOfPackage.setEnabled(true);
        } else {
            rbEndOfPackage.setEnabled(false);
        }
//        if (prescription.isClosed()) {
//            rbDate.setSelected(true);
//            jdcAB.setDate(prescription.getTo());
//        } else {
//
//        }
//

        ignoreEvent = false;
        txtMed.requestFocus();

        reloadTable();
    }

    private void txtMedFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMedFocusGained
        SYSTools.markAllTxt(txtMed);
    }//GEN-LAST:event_txtMedFocusGained

    private void btnMedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMedActionPerformed

        String pzn = MedPackageTools.parsePZN(txtMed.getText());
        final JidePopup popup = new JidePopup();

        WizardDialog wizard = new MedProductWizard(new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    MedPackage aPackage = (MedPackage) o;
                    txtMed.setText(aPackage.getPzn());
                }
                popup.hidePopup();
            }
        }, (pzn == null ? pzn : txtMed.getText().trim())).getWizard();

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
        cmbIntervention.setModel(new DefaultComboBoxModel(InterventionTools.findMassnahmenBy(InterventionTools.TYPE_PRESCRIPTION).toArray()));
        cmbIntervention.setSelectedItem(((TradeForm) cmbMed.getSelectedItem()).getDosageForm().getIntervention());
        cmbIntervention.setEnabled(false);
        txtMass.setText(null);
        txtMass.setEnabled(false);
        cbDailyPlan.setEnabled(false);
        cbDailyPlan.setSelected(false);
    }//GEN-LAST:event_cmbMedItemStateChanged


    private boolean saveOK() {
        if (ignoreEvent) return false;

        DateMidnight day;
        try {
            day = new DateMidnight(SYSCalendar.parseDate(txtTo.getText()));
        } catch (NumberFormatException ex) {
            day = null;
        }

        boolean OnOK = (cmbDocON.getSelectedIndex() > 0 || cmbHospitalON.getSelectedIndex() > 0);
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
            returnPackage = new Pair(prescription, schedules2delete);
            dispose();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    @Override
    public void dispose() {
        actionBlock.execute(returnPackage);
        SYSTools.unregisterListeners(this);
        super.dispose();
    }

    private void txtBemerkungCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBemerkungCaretUpdate

    }//GEN-LAST:event_txtBemerkungCaretUpdate

    private boolean save() {
        if (!saveOK()) return false;

        prescription.setHospitalON((Hospital) cmbHospitalON.getSelectedItem());
        prescription.setIntervention((Intervention) cmbIntervention.getSelectedItem());
        prescription.setShowOnDailyPlan(cbDailyPlan.isSelected());
        prescription.setSituation(null);
        prescription.setText(txtBemerkung.getText().trim());
        prescription.setTradeForm((TradeForm) cmbMed.getSelectedItem());
        prescription.setUserON(OPDE.getLogin().getUser());
        prescription.setDocON((Doc) cmbDocON.getSelectedItem());

        prescription.setFrom(new Date());
        if (rbDate.isSelected()) {

            DateMidnight day = null;
            try {
                day = new DateMidnight(SYSCalendar.parseDate(txtTo.getText()));
            } catch (NumberFormatException ex) {
                OPDE.fatal(ex);
            }

            prescription.setTo(day.plusDays(1).toDateTime().minusSeconds(1).toDate());
            prescription.setUserOFF(OPDE.getLogin().getUser());
            prescription.setHospitalOFF(prescription.getHospitalON());
            prescription.setDocOFF(prescription.getDocON());
        } else {
            prescription.setHospitalOFF(null);
            prescription.setDocOFF(null);
            prescription.setTo(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
        }
        prescription.setTillEndOfPackage(rbEndOfPackage.isSelected());

        return true;
    }


    private void tblDosisMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDosisMousePressed
        if (!evt.isPopupTrigger()) {
            return;
        }

//        if (prescription.isClosed() && new DateMidnight(jdcAB.getDate()).isBefore(new DateMidnight())) {
//            JOptionPane.showMessageDialog(tblDosis, "Verordnung wurde bereits abgesetzt. Sie können diese nicht mehr ändern.");
//            return;
//        }

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

//
//        JMenuItem itemPopupEditText = new JMenuItem(OPDE.lang.getString("misc.msg.edit"), SYSConst.icon22edit);
//        itemPopupEditText.addActionListener(new java.awt.event.ActionListener() {
//
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                PrescriptionSchedule planung = prescription.getPrescriptionSchedule().get(row);
//                final JidePopup popup = new JidePopup();
//
//                CleanablePanel dlg = null;
//                dlg = new PnlRegelDosis(planung, new Closure() {
//                    @Override
//                    public void execute(Object o) {
//                        if (o != null) {
//                            reloadTable();
//                            popup.hidePopup();
//                        }
//                    }
//                });
//
//                popup.setMovable(false);
//                popup.setOwner(tblDosis);
//                popup.removeExcludedComponent(tblDosis);
//                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//                popup.getContentPane().add(dlg);
//                popup.setDefaultFocusComponent(dlg);
//
//                Point p3 = new Point(btnAddDosis.getX(), btnAddDosis.getY());
//                SwingUtilities.convertPointToScreen(p3, btnAddDosis);
//                popup.showPopup(p3.x, p3.y - (int) dlg.getPreferredSize().getWidth() - (int) btnAddDosis.getPreferredSize().getHeight());
//            }
//        });
//        menu.add(itemPopupEditText);

        //-----------------------------------------
        JMenuItem itemPopupDelete = new JMenuItem(OPDE.lang.getString("misc.msg.delete"), SYSConst.icon22delete);
        itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PrescriptionSchedule schedule = prescription.getPrescriptionSchedule().get(row);
                prescription.getPrescriptionSchedule().remove(schedule);
                schedules2delete.add(schedule);
                reloadTable();
            }
        });
        menu.add(itemPopupDelete);
        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());

    }//GEN-LAST:event_tblDosisMousePressed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        returnPackage = null;
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed


    private void fillComboBoxes() {
        EntityManager em = OPDE.createEM();
        Query queryArzt = em.createQuery("SELECT a FROM Doc a WHERE a.status >= 0 ORDER BY a.name, a.vorname");
        List<Doc> listAerzte = queryArzt.getResultList();
        listAerzte.add(0, null);

        Query queryKH = em.createQuery("SELECT k FROM Hospital k WHERE k.status >= 0 ORDER BY k.name");
        List<Hospital> listKH = queryKH.getResultList();
        listKH.add(0, null);
        em.close();

        cmbDocON.setModel(new DefaultComboBoxModel(listAerzte.toArray()));
        cmbDocON.setRenderer(DocTools.getRenderer());
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
        tblDosis.getColumnModel().getColumn(TMDose.COL_Dosis).setHeaderValue(OPDE.lang.getString("misc.msg.usage"));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel1;
    private JXSearchField txtMed;
    private JComboBox cmbMed;
    private JPanel panel4;
    private JButton btnMed;
    private JComboBox cmbIntervention;
    private JXSearchField txtMass;
    private JPanel jPanel8;
    private JScrollPane jspDosis;
    private JTable tblDosis;
    private JPanel panel2;
    private JButton btnAddDosis;
    private JCheckBox cbDailyPlan;
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
    private JComboBox cmbDocON;
    private JComboBox cmbHospitalON;
    private JPanel panel1;
    private JButton btnClose;
    private JButton btnSave;
    // End of variables declaration//GEN-END:variables

}
