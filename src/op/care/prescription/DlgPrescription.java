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
import com.toedter.calendar.JDateChooser;
import entity.nursingprocess.Intervention;
import entity.nursingprocess.InterventionTools;
import entity.prescription.*;
import op.OPDE;
import op.care.med.prodassistant.MedProductWizard;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;
import tablemodels.TMDosis;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Bei einer Verordnungsänderung wird die alte Verordnung abgesetzt und die neue Verordnung statt dessen angesetzt. BHPs werden immer erstellt.
 * Bei neuen Verordnungen immer für den ganzen Tag. Bei Änderung immer ab dem Moment, ab dem die neue Verordnung gilt.
 *
 * @author tloehr
 */
public class DlgPrescription extends MyJDialog {

    public static final int ALLOW_ALL_EDIT = 0;
    public static final int NO_CHANGE_MED_AND_SIT = 1;

    private boolean ignoreEvent;

    private JPopupMenu menu;
    private PropertyChangeListener myPropertyChangeListener;
    private int editMode;
    private Closure actionBlock;
    private Prescriptions prescription;
    private List<PrescriptionSchedule> planungenToDelete = null;
    private Pair<Prescriptions, List<PrescriptionSchedule>> returnPackage = null;


    /**
     * Creates new form DlgPrescription
     */
    public DlgPrescription(Prescriptions prescription, int mode, Closure actionBlock) {
        this.actionBlock = actionBlock;
        this.prescription = prescription;
        planungenToDelete = new ArrayList<PrescriptionSchedule>();
        this.editMode = mode;
        initComponents();
        initDialog();
        pack();
        setVisible(true);
    }

    private void btnAddDosisActionPerformed(ActionEvent e) {
        if (isBedarf() && prescription.getPrescriptionSchedule().size() > 0) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bei einer Bedarfsverordnung kann nur <u>eine</u> Dosierung eingegeben werden.", 2));
            return;
        }

        final JidePopup popup = new JidePopup();

        JPanel dlg;
        if (isBedarf()) {
            dlg = new PnlBedarfDosis(null, new Closure() {
                @Override
                public void execute(Object o) {
                    if (o != null) {
                        ((PrescriptionSchedule) o).setPrescription(prescription);
                        prescription.getPrescriptionSchedule().add(((PrescriptionSchedule) o));
                        reloadTable();
                        popup.hidePopup();
                    }
                }
            });
        } else {
            dlg = new PnlRegelDosis(null, new Closure() {
                @Override
                public void execute(Object o) {
                    if (o != null) {
                        ((PrescriptionSchedule) o).setPrescription(prescription);
                        prescription.getPrescriptionSchedule().add(((PrescriptionSchedule) o));
                        reloadTable();
                        popup.hidePopup();
                    }
                }
            });
        }

        popup.setMovable(false);
        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
        popup.getContentPane().add(dlg);
        popup.setOwner(btnAddDosis);
        popup.removeExcludedComponent(btnAddDosis);
        popup.setDefaultFocusComponent(dlg);
        popup.addPropertyChangeListener("visible", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                OPDE.debug("popup property: " + propertyChangeEvent.getPropertyName() + " value: " + propertyChangeEvent.getNewValue() + " compCount: " + popup.getContentPane().getComponentCount());
                popup.getContentPane().getComponentCount();
//                OPDE.getDisplayManager().addSubMessage(currentSubMessage);
            }
        });


        Point p = new Point(btnAddDosis.getX(), btnAddDosis.getY());
        // Convert a coordinate relative to a component's bounds to screen coordinates
        SwingUtilities.convertPointToScreen(p, btnAddDosis);
        popup.showPopup(p.x, p.y - (int) dlg.getPreferredSize().getHeight() - (int) btnAddDosis.getPreferredSize().getHeight());
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

    private void btnSituationActionPerformed(ActionEvent e) {
        if (cmbSit.getModel().getSize() != 0) {
            return;
        }

        final JidePopup popup = new JidePopup();
        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

        final JTextField editor = new JTextField(txtSit.getText(), 30);
        editor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveSituation(editor.getText());
                popup.hidePopup();
            }
        });

        popup.getContentPane().add(new JScrollPane(editor));
        JButton saveButton = new JButton(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveSituation(editor.getText());
                popup.hidePopup();
            }
        });

        popup.setMovable(false);
        popup.setOwner(btnSituation);
        popup.removeExcludedComponent(btnSituation);

        popup.getContentPane().add(saveButton);
        popup.setDefaultFocusComponent(editor);

        popup.showPopup();
    }

    private void cmbSitPropertyChange(PropertyChangeEvent e) {
        cmbSitItemStateChanged(null);
    }

    private void btnEmptySitActionPerformed(ActionEvent e) {
        cmbSit.setModel(new DefaultComboBoxModel());
    }

    private void txtMedActionPerformed(ActionEvent e) {
        if (txtMed.getText().isEmpty()) {
            cmbMed.setModel(new DefaultComboBoxModel());
            cmbIntervention.setEnabled(true);
            txtMass.setEnabled(true);
            cbStellplan.setEnabled(true);
            cbStellplan.setSelected(false);
            rbEndOfPackage.setEnabled(false);
        } else {
            OPDE.getDisplayManager().setDBActionMessage(true);
            EntityManager em = OPDE.createEM();

            String pzn = MedPackageTools.parsePZN(txtMed.getText());

            if (pzn != null) {

                Query pznQuery = em.createNamedQuery("MedPackung.findByPzn");
                pznQuery.setParameter("pzn", pzn);

                try {
                    MedPackage medPackage = (MedPackage) pznQuery.getSingleResult();
                    cmbMed.setModel(new DefaultComboBoxModel(new TradeForm[]{medPackage.getTradeForm()}));
                } catch (NoResultException nre) {
                    OPDE.debug("Nichts passendes zu dieser PZN gefunden");
                } catch (Exception ex) {
                    OPDE.fatal(ex);
                }

            } else { // Falls die Suche NICHT nur aus Zahlen besteht, dann nach Namen suchen.
                cmbMed.setModel(new DefaultComboBoxModel(TradeFormTools.findDarreichungByMedProduktText(em, txtMed.getText()).toArray()));
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
                cbStellplan.setEnabled(true);
                cbStellplan.setSelected(false);
                OPDE.getDisplayManager().clearSubMessages();
            }
            rbEndOfPackage.setEnabled(!isBedarf() && cmbMed.getModel().getSize() > 0);
        }
    }

    private void rbActiveItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void rbDateItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void rbEndOfPackageItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void txtMassActionPerformed(ActionEvent e) {
        cmbIntervention.setModel(new DefaultComboBoxModel(InterventionTools.findMassnahmenBy(InterventionTools.MASSART_BHP, txtMass.getText()).toArray()));
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
        btnEmptySit2 = new JButton();
        btnMed = new JButton();
        cmbIntervention = new JComboBox();
        txtSit = new JXSearchField();
        cmbSit = new JComboBox();
        panel3 = new JPanel();
        btnEmptySit = new JButton();
        btnSituation = new JButton();
        txtMass = new JXSearchField();
        jPanel8 = new JPanel();
        jspDosis = new JScrollPane();
        tblDosis = new JTable();
        panel2 = new JPanel();
        btnAddDosis = new JButton();
        cbStellplan = new JCheckBox();
        jPanel3 = new JPanel();
        pnlOFF = new JPanel();
        rbActive = new JRadioButton();
        rbDate = new JRadioButton();
        jdcAB = new JDateChooser();
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
                "68dlu, $lcgap, 242dlu:grow, $lcgap, pref",
                "3*(16dlu, $lgap), default, $lgap, fill:default:grow"));

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
            cmbMed.setModel(new DefaultComboBoxModel(new String[] {
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

                //---- btnEmptySit2 ----
                btnEmptySit2.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/button_cancel.png")));
                btnEmptySit2.setBorderPainted(false);
                btnEmptySit2.setBorder(null);
                btnEmptySit2.setContentAreaFilled(false);
                btnEmptySit2.setToolTipText("Auswahl l\u00f6schen");
                btnEmptySit2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnEmptySit2.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/button_cancel-pressed.png")));
                btnEmptySit2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnEmptySitActionPerformed(e);
                    }
                });
                panel4.add(btnEmptySit2);

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
            cmbIntervention.setModel(new DefaultComboBoxModel(new String[] {
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
            txtSit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtSitActionPerformed(e);
                }
            });
            jPanel1.add(txtSit, CC.xy(1, 3));

            //---- cmbSit ----
            cmbSit.setModel(new DefaultComboBoxModel(new String[] {
                "Item 1",
                "Item 2",
                "Item 3",
                "Item 4"
            }));
            cmbSit.setFont(new Font("Arial", Font.PLAIN, 14));
            cmbSit.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cmbSitItemStateChanged(e);
                }
            });
            cmbSit.addPropertyChangeListener("model", new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                    cmbSitPropertyChange(e);
                }
            });
            jPanel1.add(cmbSit, CC.xy(3, 3));

            //======== panel3 ========
            {
                panel3.setLayout(new BoxLayout(panel3, BoxLayout.LINE_AXIS));

                //---- btnEmptySit ----
                btnEmptySit.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/button_cancel.png")));
                btnEmptySit.setBorderPainted(false);
                btnEmptySit.setBorder(null);
                btnEmptySit.setContentAreaFilled(false);
                btnEmptySit.setToolTipText("Auswahl l\u00f6schen");
                btnEmptySit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnEmptySit.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/button_cancel-pressed.png")));
                btnEmptySit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnEmptySitActionPerformed(e);
                    }
                });
                panel3.add(btnEmptySit);

                //---- btnSituation ----
                btnSituation.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                btnSituation.setBorderPainted(false);
                btnSituation.setBorder(null);
                btnSituation.setContentAreaFilled(false);
                btnSituation.setToolTipText("Neue  Situation eintragen");
                btnSituation.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnSituation.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png")));
                btnSituation.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnSituationActionPerformed(e);
                    }
                });
                panel3.add(btnSituation);
            }
            jPanel1.add(panel3, CC.xy(5, 3));

            //---- txtMass ----
            txtMass.setFont(new Font("Arial", Font.PLAIN, 14));
            txtMass.setPrompt("Massnahmen");
            txtMass.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtMassActionPerformed(e);
                }
            });
            jPanel1.add(txtMass, CC.xy(1, 5));

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
            jPanel1.add(jPanel8, CC.xywh(1, 9, 5, 1));

            //---- cbStellplan ----
            cbStellplan.setText("Auf den Stellplan, auch wenn kein Medikament");
            cbStellplan.setBorder(BorderFactory.createEmptyBorder());
            cbStellplan.setMargin(new Insets(0, 0, 0, 0));
            cbStellplan.setFont(new Font("Arial", Font.PLAIN, 14));
            cbStellplan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            jPanel1.add(cbStellplan, CC.xywh(1, 7, 5, 1));
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

                //---- jdcAB ----
                jdcAB.setEnabled(false);
                pnlOFF.add(jdcAB, CC.xy(2, 3));

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
                cmbDocON.setModel(new DefaultComboBoxModel(new String[] {
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
                }));
                pnlON.add(cmbDocON, CC.xy(1, 1));

                //---- cmbHospitalON ----
                cmbHospitalON.setModel(new DefaultComboBoxModel(new String[] {
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
            btnSave.setEnabled(false);
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
        pack();
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
        txtSit.setText("");
        txtMed.setText("");
        cmbIntervention.setModel(new DefaultComboBoxModel(InterventionTools.findMassnahmenBy(InterventionTools.MASSART_BHP).toArray()));
        cmbIntervention.setRenderer(InterventionTools.getMassnahmenRenderer());
        jdcAB.setMinSelectableDate(new Date());
        cmbMed.setRenderer(TradeFormTools.getDarreichungRenderer(TradeFormTools.LONG));
        cmbSit.setRenderer(SituationsTools.getSituationenRenderer());
        cmbMed.setModel(new DefaultComboBoxModel());

        btnSave.setEnabled(true);

        cmbDocON.setSelectedItem(prescription.getDocON());
        cmbHospitalON.setSelectedItem(prescription.getHospitalON());

        rbEndOfPackage.setSelected(prescription.isTillEndOfPackage());

        txtBemerkung.setText(SYSTools.catchNull(prescription.getText()));

        if (prescription.hasMed()) {
            cmbMed.setModel(new DefaultComboBoxModel(new TradeForm[]{prescription.getTradeForm()}));
        }

        cmbIntervention.setEnabled(cmbMed.getModel().getSize() == 0);
        txtMass.setEnabled(cmbMed.getModel().getSize() == 0);
        cbStellplan.setEnabled(cmbMed.getModel().getSize() == 0);
        cbStellplan.setSelected(prescription.isOnDailyPlan());

        cmbSit.setModel(new DefaultComboBoxModel(new Situations[]{prescription.getSituation()}));

        cmbIntervention.setSelectedItem(prescription.getIntervention());

        cmbMed.setEnabled(editMode == ALLOW_ALL_EDIT);
        txtMed.setEnabled(editMode == ALLOW_ALL_EDIT);
        txtSit.setEnabled(editMode == ALLOW_ALL_EDIT);
        cmbSit.setEnabled(editMode == ALLOW_ALL_EDIT);

        if (cmbMed.getSelectedItem() != null) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(TradeFormTools.toPrettyString((TradeForm) cmbMed.getSelectedItem())));
            rbEndOfPackage.setEnabled(true);
        } else {
            rbEndOfPackage.setEnabled(false);
        }
        if (prescription.isDiscontinued()) {
            rbDate.setSelected(true);
            jdcAB.setDate(prescription.getTo());
        } else {

        }

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
        cmbIntervention.setModel(new DefaultComboBoxModel(InterventionTools.findMassnahmenBy(InterventionTools.MASSART_BHP).toArray()));
        cmbIntervention.setSelectedItem(((TradeForm) cmbMed.getSelectedItem()).getDosageForm().getMassnahme());
        cmbIntervention.setEnabled(false);
        txtMass.setText(null);
        txtMass.setEnabled(false);
        cbStellplan.setEnabled(false);
        cbStellplan.setSelected(false);
//        cbPackEnde.setSelected(false);
//        cbPackEnde.setEnabled(cmbMed.getSelectedItem() != null);
//        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(DarreichungTools.toPrettyString((Darreichung) cmbMed.getSelectedItem())));

    }//GEN-LAST:event_cmbMedItemStateChanged

    private boolean isBedarf() {
        return cmbSit.getSelectedItem() != null;
    }

    private boolean saveOK() {
        if (ignoreEvent) return false;
        boolean ansetzungOK = (cmbDocON.getSelectedIndex() > 0 || cmbHospitalON.getSelectedIndex() > 0);
        boolean absetzungOK = !rbDate.isSelected() || jdcAB.getDate() != null;
        boolean medOK = cmbMed.getModel().getSize() == 0 || cmbMed.getSelectedItem() != null;
        boolean massOK = cmbIntervention.getSelectedItem() != null;
        boolean dosisVorhanden = tblDosis.getModel().getRowCount() > 0;
//        btnSave.setEnabled(ansetzungOK && absetzungOK && medOK && massOK && dosisVorhanden);
//        cbPackEnde.setEnabled(!isOnDemand() && cmbMed.getModel().getSize() > 0);


        String ursache = "";
        ursache += (ansetzungOK ? "" : "Die Informationen zum <b>an</b>setzenden <b>Doc</b> oder KH sind unvollständig. ");
        ursache += (absetzungOK ? "" : "Die Informationen zum <b>ab</b>setzenden <b>Doc</b> oder KH sind unvollständig. ");
        ursache += (medOK ? "" : "Die <b>Medikamentenangabe</b> ist falsch. ");
        ursache += (massOK ? "" : "Die Angaben über die <b>Massnahmen</b> sind falsch. ");
        ursache += (dosisVorhanden ? "" : "Sie müssen mindestens eine <b>Dosierung</b> angegeben. ");


        if (!ursache.isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(ursache, DisplayMessage.WARNING));
        }
        return ansetzungOK & absetzungOK & medOK & massOK & dosisVorhanden;

    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (save()) {
            returnPackage = new Pair(prescription, planungenToDelete);
            dispose();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    @Override
    public void dispose() {
        actionBlock.execute(returnPackage);
        jdcAB.removePropertyChangeListener(myPropertyChangeListener);

        jdcAB.cleanup();

//        OPDE.getDisplayManager().clearSubMessages();
        SYSTools.unregisterListeners(this);
        super.dispose();
    }

    private void cmbSitItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSitItemStateChanged
        if (ignoreEvent) return;

        rbEndOfPackage.setEnabled(!isBedarf());
        rbEndOfPackage.setSelected(false);

        cbStellplan.setEnabled(!isBedarf());
        cbStellplan.setSelected(false);

        planungenToDelete.addAll(prescription.getPrescriptionSchedule());
        prescription.getPrescriptionSchedule().clear();

        reloadTable();

//        saveOK();

    }//GEN-LAST:event_cmbSitItemStateChanged

    private void txtBemerkungCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBemerkungCaretUpdate

    }//GEN-LAST:event_txtBemerkungCaretUpdate

    private boolean save() {
        if (!saveOK()) return false;

        if (cbAB.isSelected()) {

            if (SYSCalendar.sameDay(jdcAB.getDate(), new Date()) == 0) {
                OPDE.debug("jdcAB steht auf HEUTE");
                if (SYSCalendar.sameDay(jdcAB.getDate(), jdcAN.getDate()) == 0) {
                    OPDE.debug("jdcAB und jdcAN sind gleich");
                    prescription.setFrom(new Date(SYSCalendar.startOfDay()));
                    prescription.setTo(new Date(SYSCalendar.endOfDay()));
                } else {
                    prescription.setTo(new Date());
                }
            } else {
                OPDE.debug("jdcAB steht nicht auf HEUTE");
                prescription.setTo(new Date(SYSCalendar.endOfDay(jdcAB.getDate())));

            }
            prescription.setUserOFF(OPDE.getLogin().getUser());
        } else {
            prescription.setHospitalOFF(null);
            prescription.setDocOFF(null);
            prescription.setTo(SYSConst.DATE_BIS_AUF_WEITERES);
        }

        prescription.setDocON((Doc) cmbDocON.getSelectedItem());
        prescription.setHospitalON((Hospital) cmbHospitalON.getSelectedItem());
        prescription.setDocOFF((Doc) cmbDocOFF.getSelectedItem());
        prescription.setHospitalOFF((Hospital) cmbKHAb.getSelectedItem());
        prescription.setAngesetztDurch(OPDE.getLogin().getUser());
        prescription.setShowOnDailyPlan(cbStellplan.isSelected());
        prescription.setBisPackEnde(cbPackEnde.isSelected());
        prescription.setText(txtBemerkung.getText());
        prescription.setIntervention((Intervention) cmbIntervention.getSelectedItem());
        prescription.setTradeForm((TradeForm) cmbMed.getSelectedItem());
        prescription.setShowOnDailyPlan(cbStellplan.isSelected());

        prescription.setSituation((Situations) cmbSit.getSelectedItem());
        return true;
    }


    private void tblDosisMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDosisMousePressed
        if (!evt.isPopupTrigger()) {
            return;
        }

        if (prescription.isDiscontinued() && !SYSCalendar.isInFuture(jdcAB.getDate().getTime())) {
            JOptionPane.showMessageDialog(tblDosis, "Verordnung wurde bereits abgesetzt. Sie können diese nicht mehr ändern.");
            return;
        }

        final TMDosis tm = (TMDosis) tblDosis.getModel();
        if (tm.getRowCount() == 0) {
            return;
        }
        Point p = evt.getPoint();
        Point p2 = evt.getPoint();
        // Convert a coordinate relative to a component's bounds to screen coordinates
        SwingUtilities.convertPointToScreen(p2, tblDosis);

        final Point screenposition = p2;
        final int row = tblDosis.rowAtPoint(p);

        ListSelectionModel lsm = tblDosis.getSelectionModel();
        lsm.setSelectionInterval(row, row);


        //final long bhppid = ((Long) tm.getValueAt(row, TMDosis.COL_BHPPID)).longValue();


        // Menüeinträge
        SYSTools.unregisterListeners(menu);
        menu = new JPopupMenu();

        // Bei Bedarfsmedikation kann immer nur eine Dosis eingegeben werden.
        JMenuItem itemPopupEditText = new JMenuItem("Bearbeiten");
        itemPopupEditText.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PrescriptionSchedule planung = prescription.getPrescriptionSchedule().toArray(new PrescriptionSchedule[0])[row];
                final JidePopup popup = new JidePopup();

                CleanablePanel dlg;
                if (prescription.isOnDemand()) {
                    dlg = new PnlBedarfDosis(planung, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                reloadTable();
                                popup.hidePopup();
                            }
                        }
                    });
                } else {
                    dlg = new PnlRegelDosis(planung, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                reloadTable();
                                popup.hidePopup();
                            }
                        }
                    });
                }

                popup.setMovable(false);
                popup.setOwner(tblDosis);
                popup.removeExcludedComponent(tblDosis);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                popup.getContentPane().add(dlg);
                popup.setDefaultFocusComponent(dlg);

                Point p3 = new Point(btnAddDosis.getX(), btnAddDosis.getY());
                SwingUtilities.convertPointToScreen(p3, btnAddDosis);
                popup.showPopup(p3.x, p3.y - (int) dlg.getPreferredSize().getWidth() - (int) btnAddDosis.getPreferredSize().getHeight());
            }
        });
        menu.add(itemPopupEditText);
        //ocs.setEnabled(classname, "itemPopupEditText", itemPopupEditText, status > 0 && changeable);

        //-----------------------------------------
        JMenuItem itemPopupDelete = new JMenuItem("löschen");
        itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PrescriptionSchedule schedule = prescription.getPrescriptionSchedule().toArray(new PrescriptionSchedule[0])[row];
                prescription.getPrescriptionSchedule().remove(schedule);
                planungenToDelete.add(schedule);
                reloadTable();
            }
        });
        menu.add(itemPopupDelete);
        //ocs.setEnabled(classname, "itemPopupEditVer", itemPopupEditVer, true);
        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());

    }//GEN-LAST:event_tblDosisMousePressed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void cbABActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbABActionPerformed
        jdcAB.setEnabled(cbAB.isSelected());
        cmbDocOFF.setEnabled(cbAB.isSelected());
        cmbKHAb.setEnabled(cbAB.isSelected());
        cmbDocOFF.setSelectedIndex(0);
        cmbKHAb.setSelectedIndex(0);
        jdcAB.setDate(new Date());
        jdcAB.setMinSelectableDate(jdcAN.getDate());
        lblAB.setText(cbAB.isSelected() ? OPDE.getLogin().getUser().getUID() : "");
        cbPackEnde.setSelected(false);
        cbPackEnde.setEnabled(!cbAB.isSelected());

    }//GEN-LAST:event_cbABActionPerformed


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
        cmbDocON.setRenderer(DocTools.getArztRenderer());
        cmbDocON.setSelectedIndex(0);

        cmbHospitalON.setModel(new DefaultComboBoxModel(listKH.toArray()));
        cmbHospitalON.setRenderer(HospitalTools.getKHRenderer());
        cmbHospitalON.setSelectedIndex(0);
    }

    private void reloadTable() {
        String zubereitung = "x";
        if (prescription.getTradeForm() != null) {
            zubereitung = prescription.getTradeForm().getDosageForm().getZubereitung();
        }

        tblDosis.setModel(new TMDosis(zubereitung, prescription));
        tblDosis.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jspDosis.dispatchEvent(new ComponentEvent(jspDosis, ComponentEvent.COMPONENT_RESIZED));
        tblDosis.getColumnModel().getColumn(TMDosis.COL_Dosis).setCellRenderer(new RNDHTML());
        tblDosis.getColumnModel().getColumn(TMDosis.COL_Dosis).setHeaderValue("Anwendung");

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel1;
    private JXSearchField txtMed;
    private JComboBox cmbMed;
    private JPanel panel4;
    private JButton btnEmptySit2;
    private JButton btnMed;
    private JComboBox cmbIntervention;
    private JXSearchField txtSit;
    private JComboBox cmbSit;
    private JPanel panel3;
    private JButton btnEmptySit;
    private JButton btnSituation;
    private JXSearchField txtMass;
    private JPanel jPanel8;
    private JScrollPane jspDosis;
    private JTable tblDosis;
    private JPanel panel2;
    private JButton btnAddDosis;
    private JCheckBox cbStellplan;
    private JPanel jPanel3;
    private JPanel pnlOFF;
    private JRadioButton rbActive;
    private JRadioButton rbDate;
    private JDateChooser jdcAB;
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
