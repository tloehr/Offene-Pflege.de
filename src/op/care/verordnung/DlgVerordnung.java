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
package op.care.verordnung;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import com.toedter.calendar.JDateChooser;
import entity.*;
import entity.verordnungen.*;
import op.OPDE;
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
public class DlgVerordnung extends MyJDialog {

    public static final int ALLOW_ALL_EDIT = 0;
    public static final int NO_CHANGE_MED_AND_SIT = 1;
//    public static final int EDIT_MODE = 2; // Korrigieren
//    public static final int CHANGE_MODE = 3; // Ändern
//    public static final int EDIT_OF_CHANGE_MODE = 4; // Das ist dann, wenn man eine Veränderung (Change) nachträglich nochmal korrigiert.

    //    private java.awt.Frame parent;
//    private boolean ignoreSitCaret;
    private boolean ignoreEvent;

    private JPopupMenu menu;
    private PropertyChangeListener myPropertyChangeListener;
    private int editMode;
    private Closure actionBlock;
    private Verordnung verordnung;
    private List<VerordnungPlanung> planungenToDelete = null;
    private Pair<Verordnung, List<VerordnungPlanung>> returnPackage = null;

//    private EntityManager em;

//    private DisplayMessage currentSubMessage = null;


    /**
     * Creates new form DlgVerordnung
     */
    public DlgVerordnung(Verordnung verordnung, int mode, Closure actionBlock) {
        this.actionBlock = actionBlock;
        this.verordnung = verordnung;
        planungenToDelete = new ArrayList<VerordnungPlanung>();
        this.editMode = mode;
        initComponents();
        initDialog();
        pack();
        setVisible(true);
    }

    private void jdcANPropertyChange(PropertyChangeEvent e) {
        saveOK();
    }

    private void btnAddDosisActionPerformed(ActionEvent e) {
        if (isBedarf() && verordnung.getPlanungen().size() > 0) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.toHTML("Bei einer Bedarfsverordnung kann nur <u>eine</u> Dosierung eingegeben werden."), 2));
            return;
        }

        final JidePopup popup = new JidePopup();

        JPanel dlg;
        if (isBedarf()) {
            dlg = new PnlBedarfDosis(null, new Closure() {
                @Override
                public void execute(Object o) {
                    if (o != null) {
                        ((VerordnungPlanung) o).setVerordnung(verordnung);
                        verordnung.getPlanungen().add(((VerordnungPlanung) o));
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
                        ((VerordnungPlanung) o).setVerordnung(verordnung);
                        verordnung.getPlanungen().add(((VerordnungPlanung) o));
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

    private void thisKeyPressed(KeyEvent e) {
        OPDE.debug(e.getKeyCode());
    }

    private void thisKeyTyped(KeyEvent e) {
        OPDE.debug(e.getKeyCode());

    }

    private void txtSitActionPerformed(ActionEvent e) {
        if (txtSit.getText().isEmpty()) {
            cmbSit.setModel(new DefaultComboBoxModel());
        } else {
            cmbSit.setModel(new DefaultComboBoxModel(SituationenTools.findSituationByText(txtSit.getText()).toArray()));
        }
        saveOK();
    }

    private void saveSituation(String text) {
        if (text.isEmpty()) {
            return;
        }
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT s FROM Situationen s WHERE s.text = :text");
        query.setParameter("text", text);
        if (query.getResultList().isEmpty()) {
            Situationen neueSituation = new Situationen(text);
            em.persist(neueSituation);
            cmbSit.setModel(new DefaultComboBoxModel(new Situationen[]{neueSituation}));
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
//        popup.getContentPane().add(editor);
        popup.getContentPane().add(saveButton);
        popup.setDefaultFocusComponent(editor);

//        Point p3 = new Point(btnSituation.getX(), btnSituation.getY());
//        SwingUtilities.convertPointToScreen(p3, btnSituation);
        popup.showPopup();
    }

    private void cmbSitPropertyChange(PropertyChangeEvent e) {
        cmbSitItemStateChanged(null);
    }

    private void cbPackEndeItemStateChanged(ItemEvent e) {
        saveOK();
    }

    private void btnEmptySitActionPerformed(ActionEvent e) {
        cmbSit.setModel(new DefaultComboBoxModel());
    }

    private void txtMedActionPerformed(ActionEvent e) {
        if (txtMed.getText().isEmpty()) {
            cmbMed.setModel(new DefaultComboBoxModel());
            cmbMass.setEnabled(true);
            cbStellplan.setEnabled(true);
            cbStellplan.setSelected(false);
            cbPackEnde.setSelected(false);
            cbPackEnde.setEnabled(false);
        } else {
            OPDE.getDisplayManager().setDBActionMessage(true);
            EntityManager em = OPDE.createEM();
            if (txtMed.getText().matches("^ß?\\d{7}")) { // Hier sucht man nach einer PZN. Im Barcode ist das führende 'ß' enthalten.
                String pzn = txtMed.getText();

                pzn = (pzn.startsWith("ß") ? pzn.substring(1) : pzn);
                Query pznQuery = em.createNamedQuery("MedPackung.findByPzn");
                pznQuery.setParameter("pzn", pzn);

                try {
                    MedPackung medPackung = (MedPackung) pznQuery.getSingleResult();
                    cmbMed.setModel(new DefaultComboBoxModel(new Darreichung[]{medPackung.getDarreichung()}));
                } catch (NoResultException nre) {
                    OPDE.debug("Nichts passendes zu dieser PZN gefunden");
                } catch (Exception ex) {
                    OPDE.fatal(ex);
                }

            } else { // Falls die Suche NICHT nur aus Zahlen besteht, dann nach Namen suchen.
                cmbMed.setModel(new DefaultComboBoxModel(DarreichungTools.findDarreichungByMedProduktText(em, txtMed.getText()).toArray()));
            }
            OPDE.getDisplayManager().setDBActionMessage(false);
            em.close();

            if (cmbMed.getModel().getSize() > 0) {
                cmbMedItemStateChanged(null);
            } else {
                cmbMed.setToolTipText("");
                cmbMass.setSelectedIndex(-1);
                cmbMass.setEnabled(true);
                cbStellplan.setEnabled(true);
                cbStellplan.setSelected(false);
                cbPackEnde.setSelected(false);
                OPDE.getDisplayManager().clearSubMessages();
            }
            cbPackEnde.setEnabled(!isBedarf() && cmbMed.getModel().getSize() > 0);
        }
    }

    private void jdcABPropertyChange(PropertyChangeEvent e) {
        saveOK();
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
        cmbMass = new JComboBox();
        txtSit = new JXSearchField();
        cmbSit = new JComboBox();
        panel3 = new JPanel();
        btnEmptySit = new JButton();
        btnSituation = new JButton();
        xSearchField1 = new JXSearchField();
        jPanel8 = new JPanel();
        jspDosis = new JScrollPane();
        tblDosis = new JTable();
        panel2 = new JPanel();
        btnAddDosis = new JButton();
        cbPackEnde = new JCheckBox();
        cbStellplan = new JCheckBox();
        jPanel3 = new JPanel();
        jPanel4 = new JPanel();
        jLabel3 = new JLabel();
        jdcAB = new JDateChooser();
        jLabel4 = new JLabel();
        cmbAB = new JComboBox();
        cbAB = new JCheckBox();
        lblAB = new JLabel();
        cmbKHAb = new JComboBox();
        jScrollPane3 = new JScrollPane();
        txtBemerkung = new JTextPane();
        jLabel5 = new JLabel();
        jPanel2 = new JPanel();
        jLabel1 = new JLabel();
        jdcAN = new JDateChooser();
        cmbAN = new JComboBox();
        jLabel2 = new JLabel();
        lblAN = new JLabel();
        cmbKHAn = new JComboBox();
        panel1 = new JPanel();
        btnClose = new JButton();
        btnSave = new JButton();

        //======== this ========
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                thisKeyTyped(e);
            }
            @Override
            public void keyPressed(KeyEvent e) {
                thisKeyPressed(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$rgap, $lcgap, default, $lcgap, default:grow, $lcgap, $rgap",
            "$rgap, $lgap, fill:default:grow, $lgap, fill:default, $lgap, $rgap"));

        //======== jPanel1 ========
        {
            jPanel1.setBorder(null);
            jPanel1.setLayout(new FormLayout(
                "68dlu, $lcgap, 242dlu:grow, $lcgap, pref",
                "3*(16dlu, $lgap), 2*(default, $lgap), fill:default:grow"));

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
                btnMed.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnMedActionPerformed(e);
                    }
                });
                panel4.add(btnMed);
            }
            jPanel1.add(panel4, CC.xy(5, 1));

            //---- cmbMass ----
            cmbMass.setModel(new DefaultComboBoxModel(new String[] {
                "Item 1",
                "Item 2",
                "Item 3",
                "Item 4"
            }));
            cmbMass.setFont(new Font("Arial", Font.PLAIN, 14));
            cmbMass.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cmbMassItemStateChanged(e);
                }
            });
            jPanel1.add(cmbMass, CC.xywh(3, 5, 3, 1));

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
                btnSituation.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnSituationActionPerformed(e);
                    }
                });
                panel3.add(btnSituation);
            }
            jPanel1.add(panel3, CC.xy(5, 3));

            //---- xSearchField1 ----
            xSearchField1.setFont(new Font("Arial", Font.PLAIN, 14));
            xSearchField1.setPrompt("Massnahmen");
            jPanel1.add(xSearchField1, CC.xy(1, 5));

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
                    jspDosis.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            jspDosisComponentResized(e);
                        }
                    });

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
            jPanel1.add(jPanel8, CC.xywh(1, 11, 5, 1));

            //---- cbPackEnde ----
            cbPackEnde.setText("Bis Packungsende");
            cbPackEnde.setBorder(BorderFactory.createEmptyBorder());
            cbPackEnde.setEnabled(false);
            cbPackEnde.setMargin(new Insets(0, 0, 0, 0));
            cbPackEnde.setFont(new Font("Arial", Font.PLAIN, 14));
            cbPackEnde.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cbPackEndeItemStateChanged(e);
                }
            });
            jPanel1.add(cbPackEnde, CC.xywh(1, 7, 5, 1));

            //---- cbStellplan ----
            cbStellplan.setText("Auf den Stellplan, auch wenn kein Medikament");
            cbStellplan.setBorder(BorderFactory.createEmptyBorder());
            cbStellplan.setMargin(new Insets(0, 0, 0, 0));
            cbStellplan.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel1.add(cbStellplan, CC.xywh(1, 9, 5, 1));
        }
        contentPane.add(jPanel1, CC.xy(5, 3));

        //======== jPanel3 ========
        {
            jPanel3.setBorder(null);
            jPanel3.setLayout(new FormLayout(
                "149dlu",
                "3*(fill:default, $lgap), fill:default:grow"));

            //======== jPanel4 ========
            {
                jPanel4.setBorder(new TitledBorder("Absetzung"));
                jPanel4.setLayout(new FormLayout(
                    "default, $lcgap, 120dlu",
                    "4*(fill:17dlu, $lgap), fill:17dlu"));

                //---- jLabel3 ----
                jLabel3.setText("Am:");
                jPanel4.add(jLabel3, CC.xy(1, 3));

                //---- jdcAB ----
                jdcAB.setEnabled(false);
                jdcAB.addPropertyChangeListener("date", new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent e) {
                        jdcABPropertyChange(e);
                    }
                });
                jPanel4.add(jdcAB, CC.xy(3, 3));

                //---- jLabel4 ----
                jLabel4.setText("Durch:");
                jPanel4.add(jLabel4, CC.xy(1, 5));

                //---- cmbAB ----
                cmbAB.setModel(new DefaultComboBoxModel(new String[] {
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
                }));
                cmbAB.setEnabled(false);
                cmbAB.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cmbABItemStateChanged(e);
                    }
                });
                jPanel4.add(cmbAB, CC.xy(3, 5));

                //---- cbAB ----
                cbAB.setText("Abgesetzt");
                cbAB.setBorder(BorderFactory.createEmptyBorder());
                cbAB.setMargin(new Insets(0, 0, 0, 0));
                cbAB.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbABActionPerformed(e);
                    }
                });
                jPanel4.add(cbAB, CC.xywh(1, 1, 3, 1));

                //---- lblAB ----
                lblAB.setText("jLabel13");
                jPanel4.add(lblAB, CC.xy(3, 9));

                //---- cmbKHAb ----
                cmbKHAb.setModel(new DefaultComboBoxModel(new String[] {
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
                }));
                cmbKHAb.setEnabled(false);
                cmbKHAb.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cmbKHAbItemStateChanged(e);
                    }
                });
                jPanel4.add(cmbKHAb, CC.xy(3, 7));
            }
            jPanel3.add(jPanel4, CC.xy(1, 3));

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

            //---- jLabel5 ----
            jLabel5.setText("Bemerkung:");
            jPanel3.add(jLabel5, CC.xy(1, 5));

            //======== jPanel2 ========
            {
                jPanel2.setBorder(new TitledBorder("Ansetzung"));
                jPanel2.setLayout(new FormLayout(
                    "default, $lcgap, 119dlu:grow",
                    "17dlu, 3*($lgap, fill:17dlu)"));

                //---- jLabel1 ----
                jLabel1.setText("Am:");
                jPanel2.add(jLabel1, CC.xy(1, 1));

                //---- jdcAN ----
                jdcAN.addPropertyChangeListener("date", new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent e) {
                        jdcANPropertyChange(e);
                    }
                });
                jPanel2.add(jdcAN, CC.xy(3, 1));

                //---- cmbAN ----
                cmbAN.setModel(new DefaultComboBoxModel(new String[] {
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
                }));
                cmbAN.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cmbANItemStateChanged(e);
                    }
                });
                jPanel2.add(cmbAN, CC.xy(3, 3));

                //---- jLabel2 ----
                jLabel2.setText("Durch:");
                jPanel2.add(jLabel2, CC.xy(1, 3));

                //---- lblAN ----
                lblAN.setText("jLabel11");
                jPanel2.add(lblAN, CC.xy(3, 7));

                //---- cmbKHAn ----
                cmbKHAn.setModel(new DefaultComboBoxModel(new String[] {
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
                }));
                cmbKHAn.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cmbKHAnItemStateChanged(e);
                    }
                });
                jPanel2.add(cmbKHAn, CC.xy(3, 5));
            }
            jPanel3.add(jPanel2, CC.xy(1, 1));
        }
        contentPane.add(jPanel3, CC.xy(3, 3));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));

            //---- btnClose ----
            btnClose.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
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
    }// </editor-fold>//GEN-END:initComponents

    private void cmbABItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbABItemStateChanged
        saveOK();
    }//GEN-LAST:event_cmbABItemStateChanged

    private void cmbKHAbItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbKHAbItemStateChanged
        saveOK();
    }//GEN-LAST:event_cmbKHAbItemStateChanged

    private void cmbKHAnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbKHAnItemStateChanged
        saveOK();
    }//GEN-LAST:event_cmbKHAnItemStateChanged

    public void initDialog() {

        fillAerzteUndKHs();

        ignoreEvent = true;
        txtSit.setText("");
        txtMed.setText("");
        cmbMass.setModel(new DefaultComboBoxModel(MassnahmenTools.findMassnahmenBy(MassnahmenTools.MODE_NUR_BHP).toArray()));
        cmbMass.setRenderer(MassnahmenTools.getMassnahmenRenderer());
        jdcAN.setMinSelectableDate(new Date());
        jdcAB.setMinSelectableDate(new Date());
        cmbMed.setRenderer(DarreichungTools.getDarreichungRenderer(DarreichungTools.LONG));
        cmbSit.setRenderer(SituationenTools.getSituationenRenderer());
        cmbMed.setModel(new DefaultComboBoxModel());

        btnSave.setEnabled(true);
        jdcAN.setDate(new Date());
        lblAN.setText(verordnung.getAngesetztDurch().getNameUndVorname());
        cmbAN.setSelectedItem(verordnung.getAnArzt());
        cmbKHAn.setSelectedItem(verordnung.getAnKH());

        cbPackEnde.setSelected(verordnung.isBisPackEnde());

//            jdcAN.setEnabled(editMode == EDIT_MODE);
        txtBemerkung.setText(SYSTools.catchNull(verordnung.getBemerkung()));

        if (verordnung.hasMedi()) {
            cmbMed.setModel(new DefaultComboBoxModel(new Darreichung[]{verordnung.getDarreichung()}));
        }

        cmbMass.setEnabled(cmbMed.getModel().getSize() == 0);
        cbStellplan.setEnabled(cmbMed.getModel().getSize() == 0);
        cbStellplan.setSelected(verordnung.isStellplan());

        cmbSit.setModel(new DefaultComboBoxModel(new Situationen[]{verordnung.getSituation()}));

        cmbMass.setSelectedItem(verordnung.getMassnahme());

        cmbMed.setEnabled(editMode == ALLOW_ALL_EDIT);
        txtMed.setEnabled(editMode == ALLOW_ALL_EDIT);
        txtSit.setEnabled(editMode == ALLOW_ALL_EDIT);
        cmbSit.setEnabled(editMode == ALLOW_ALL_EDIT);

        if (cmbMed.getSelectedItem() != null) {
//                lblVerordnung.setText();
//            currentSubMessage = new DisplayMessage(DarreichungTools.toPrettyString((Darreichung) cmbMed.getSelectedItem()));
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(DarreichungTools.toPrettyString((Darreichung) cmbMed.getSelectedItem())));
            cbPackEnde.setEnabled(true);
        } else {
//            currentSubMessage = new DisplayMessage(((Massnahmen) cmbMass.getSelectedItem()).getBezeichnung());
//            OPDE.getDisplayManager().addSubMessage(currentSubMessage);
//                lblVerordnung.setText();
            cbPackEnde.setEnabled(false);
        }
        if (!verordnung.isAbgesetzt()) {
            cbAB.setSelected(false);
            lblAB.setText("");
            cmbAB.setSelectedIndex(-1);
        } else {
            cbAB.setSelected(true);
            jdcAB.setDate(verordnung.getAbDatum());
            lblAB.setText(verordnung.getAbgesetztDurch().getUKennung());
            cmbAB.setSelectedItem(verordnung.getAbArzt());
            cmbKHAb.setSelectedItem(verordnung.getAbKH());
            cmbAB.setToolTipText(cmbAB.getSelectedItem().toString());
            cmbKHAb.setToolTipText(cmbKHAb.getSelectedItem().toString());
        }
//        }


//        ignoreSitCaret = false;
        ignoreEvent = false;

//        SYSTools.centerOnParent(parent, this);
        txtMed.requestFocus();

//        myPropertyChangeListener = new java.beans.PropertyChangeListener() {
//
//            public void propertyChange(java.beans.PropertyChangeEvent evt) {
//                if (evt.getPropertyName().equals("value")) {
//                    JDateChooser jdcDatum = (JDateChooser) ((JComponent) evt.getSource()).getParent();
//                    SYSCalendar.checkJDC(jdcDatum);
//                }
//            }
//        };
//
//        jdcAN.getDateEditor().addPropertyChangeListener(myPropertyChangeListener);
//        jdcAB.getDateEditor().addPropertyChangeListener(myPropertyChangeListener);

        reloadTable();
//        setVisible(true);
    }

    private void txtMedFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMedFocusGained
        SYSTools.markAllTxt(txtMed);
    }//GEN-LAST:event_txtMedFocusGained

    private void cmbMassItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbMassItemStateChanged
//        verordnung.setMassnahme((Massnahmen) cmbMass.getSelectedItem());
        saveOK();
    }//GEN-LAST:event_cmbMassItemStateChanged

    private void btnMedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMedActionPerformed
        //String template = ( txtMed.getText().matches("^ß?\\d{7}") ? "" : txtMed.getText());
        //new DlgMed(this, template);
//        ArrayList result = new ArrayList();
//        result.add(txtMed.getText());
//        new DlgMediAssistent(this, result);
//        if (result.size() > 0) {
//            ignoreEvent = true;
//            txtMed.setText(result.get(0).toString());
//            ignoreEvent = false;
//            txtMedCaretUpdate(null);
//        }
    }//GEN-LAST:event_btnMedActionPerformed


    private void cmbMedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbMedItemStateChanged
        cmbMass.setSelectedItem(((Darreichung) cmbMed.getSelectedItem()).getMedForm().getMassnahme());
        cmbMass.setEnabled(false);
        cbStellplan.setEnabled(false);
        cbStellplan.setSelected(false);
//        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(DarreichungTools.toPrettyString((Darreichung) cmbMed.getSelectedItem())));
        saveOK();
    }//GEN-LAST:event_cmbMedItemStateChanged

    private void cbPackEndeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPackEndeActionPerformed

    }//GEN-LAST:event_cbPackEndeActionPerformed

    private boolean isBedarf() {
        return cmbSit.getSelectedItem() != null;
    }

    private void saveOK() {
        if (ignoreEvent) return;
        boolean ansetzungOK = jdcAN.getDate() != null && (cmbAN.getSelectedIndex() > 0 || cmbKHAn.getSelectedIndex() > 0);
        boolean absetzungOK = !cbAB.isSelected() || (jdcAB.getDate() != null && (cmbAB.getSelectedIndex() > 0 || cmbKHAb.getSelectedIndex() > 0));
        boolean medOK = cmbMed.getModel().getSize() == 0 || cmbMed.getSelectedItem() != null;
        boolean massOK = cmbMass.getSelectedItem() != null;
        boolean dosisVorhanden = tblDosis.getModel().getRowCount() > 0;
        btnSave.setEnabled(ansetzungOK && absetzungOK && medOK && massOK && dosisVorhanden);
        cbPackEnde.setEnabled(!isBedarf() && cmbMed.getModel().getSize() > 0);

        if (!btnSave.isEnabled()) {
            String ursache = "<html><body>Es fehlen noch Angaben, bevor Sie speichern können.<ul>";
            ursache += (ansetzungOK ? "" : "<li>Die Informationen zum <b>an</b>setzenden Arzt oder KH sind unvollständig.</li>");
            ursache += (absetzungOK ? "" : "<li>Die Informationen zum <b>ab</b>setzenden Arzt oder KH sind unvollständig.</li>");
            ursache += (medOK ? "" : "<li>Die Medikamentenangabe ist falsch.</li>");
            ursache += (massOK ? "" : "<li>Die Angaben über die Massnahmen sind falsch.</li>");
            ursache += (dosisVorhanden ? "" : "<li>Sie müssen mindestens eine Dosierung angegeben.</li>");
            ursache += "</ul></body></html>";
            btnSave.setToolTipText(ursache);
        } else {
            btnSave.setToolTipText(null);
        }
    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
        returnPackage = new Pair(verordnung, planungenToDelete);
        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    @Override
    public void dispose() {
        actionBlock.execute(returnPackage);
        jdcAB.removePropertyChangeListener(myPropertyChangeListener);
        jdcAN.removePropertyChangeListener(myPropertyChangeListener);
        jdcAB.cleanup();
        jdcAN.cleanup();
//        OPDE.getDisplayManager().clearSubMessages();
        SYSTools.unregisterListeners(this);
        super.dispose();
    }

    private void cmbSitItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSitItemStateChanged
        if (ignoreEvent) return;

        cbPackEnde.setEnabled(!isBedarf());
        cbPackEnde.setSelected(false);

        if (!isBedarf()) {
            planungenToDelete.addAll(verordnung.getPlanungen());
            verordnung.getPlanungen().clear();
            reloadTable();
        } else {
            saveOK();
        }

    }//GEN-LAST:event_cmbSitItemStateChanged

    private void txtBemerkungCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBemerkungCaretUpdate
//        if (ignoreEvent) {
//            return;
//        }
//        verordnung.setBemerkung(txtBemerkung.getText());
        saveOK();
    }//GEN-LAST:event_txtBemerkungCaretUpdate

    private void save() {

        if (cbAB.isSelected()) {

            if (SYSCalendar.sameDay(jdcAB.getDate(), new Date()) == 0) {
                OPDE.debug("jdcAB steht auf HEUTE");
                if (SYSCalendar.sameDay(jdcAB.getDate(), jdcAN.getDate()) == 0) {
                    OPDE.debug("jdcAB und jdcAN sind gleich");
                    verordnung.setAnDatum(new Date(SYSCalendar.startOfDay()));
                    verordnung.setAbDatum(new Date(SYSCalendar.endOfDay()));
                } else {
                    verordnung.setAbDatum(new Date());
                }
            } else {
                OPDE.debug("jdcAB steht nicht auf HEUTE");
                verordnung.setAbDatum(new Date(SYSCalendar.endOfDay(jdcAB.getDate())));

            }
            verordnung.setAbgesetztDurch(OPDE.getLogin().getUser());
        } else {
            verordnung.setAbKH(null);
            verordnung.setAbArzt(null);
            verordnung.setAbDatum(SYSConst.DATE_BIS_AUF_WEITERES);
        }

        verordnung.setAnArzt((Arzt) cmbAN.getSelectedItem());
        verordnung.setAnKH((Krankenhaus) cmbKHAn.getSelectedItem());
        verordnung.setAbArzt((Arzt) cmbAB.getSelectedItem());
        verordnung.setAbKH((Krankenhaus) cmbKHAb.getSelectedItem());
        verordnung.setAngesetztDurch(OPDE.getLogin().getUser());
        verordnung.setStellplan(cbStellplan.isSelected());
        verordnung.setBisPackEnde(cbPackEnde.isSelected());
        verordnung.setBemerkung(txtBemerkung.getText());
        verordnung.setMassnahme((Massnahmen) cmbMass.getSelectedItem());
        verordnung.setDarreichung((Darreichung) cmbMed.getSelectedItem());
        verordnung.setStellplan(cbStellplan.isSelected());

        verordnung.setSituation((Situationen) cmbSit.getSelectedItem());



//            } else { // if(editMode == CHANGE_MODE) { // =================== VERÄNDERUNG ====================
//                // Bei einer Veränderung, wird erst die alte Verordnung durch den ANsetzenden Arzt ABgesetzt.
//                // Dann werden die nicht mehr benötigten BHPs entfernt.
//                // Dann wird die neue Verordnung angesetzt.
//                VerordnungTools.absetzen(em, verordnung, verordnung.getAnArzt(), verordnung.getAnKH());
//
//                // die neue Verordnung beginnt eine Sekunde, nachdem die vorherige Abgesetzt wurde.
////                verordnung.setAnDatum(SYSCalendar.addField(oldVerordnung.getAbDatum(), 1, GregorianCalendar.SECOND));
//                em.persist(verordnung);
//            }
//
//
//            if (!verordnung.isBedarf()) {
//                if (editMode == CHANGE_MODE || editMode == EDIT_OF_CHANGE_MODE) {
//                    // ab der aktuellen Uhrzeit
//                    BHPTools.erzeugen(em, verordnung.getPlanungen(), new Date(), verordnung.getAnDatum());
//                } else {
//                    // für den ganzen Tag
//                    BHPTools.erzeugen(em, verordnung.getPlanungen(), new Date(), null);
//                }
//            }

//            em.getTransaction().commit();

//            if (OPDE.isDebug()) {
//            if (editMode != CHANGE_MODE) {
//                OPDE.debug("Verordnung wurde neu erstellt bzw. korrigiert");
//                OPDE.debug(verordnung);
//                CollectionUtils.forAllDo(verordnung.getPlanungen(), new Closure() {
//                    @Override
//                    public void execute(Object o) {
//                        OPDE.debug(o);
//                    }
//                });
//            } else {
//                OPDE.debug("Verordnung wurde neu geändert und gegen eine neue ersetzt.");
//                OPDE.debug("ALT");
//                OPDE.debug("==============");
//                OPDE.debug(verordnung);
//                CollectionUtils.forAllDo(verordnung.getPlanungen(), new Closure() {
//                    @Override
//                    public void execute(Object o) {
//                        OPDE.debug(o);
//                    }
//                });
//                OPDE.debug("==============");
//                OPDE.debug("NEU");
//                OPDE.debug("==============");
//                OPDE.debug(verordnung);
//                CollectionUtils.forAllDo(verordnung.getPlanungen(), new Closure() {
//                    @Override
//                    public void execute(Object o) {
//                        OPDE.debug(o);
//                    }
//                });
//            }
//        } // DEBUG OUTPUT

//        } catch (OptimisticLockException ole) {
//            em.getTransaction().rollback();
//            em.refresh(em.merge(verordnung));
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Während Ihrer Eingabe wurde diese Verordnung von einem anderen Benutzer geändert. Ihre Eingabe wird rückgängig gemacht.", DisplayMessage.IMMEDIATELY, 4));
//        } catch (Exception e) {
//            em.getTransaction().rollback();
//            OPDE.fatal(e);
//        } finally {
//            em.close();
//        }

    }


    private void tblDosisMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDosisMousePressed
        if (!evt.isPopupTrigger()) {
            return;
        }

        if (verordnung.isAbgesetzt() && !SYSCalendar.isInFuture(jdcAB.getDate().getTime())) {
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
                VerordnungPlanung planung = verordnung.getPlanungen().toArray(new VerordnungPlanung[0])[row];
                final JidePopup popup = new JidePopup();
                PnlRegelDosis dlg = new PnlRegelDosis(planung, new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o != null) {
                            reloadTable();
                            popup.hidePopup();
                        }
                    }
                });
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
                VerordnungPlanung planung = verordnung.getPlanungen().toArray(new VerordnungPlanung[0])[row];
                verordnung.getPlanungen().remove(planung);
                planungenToDelete.add(planung);
                reloadTable();
            }
        });
        menu.add(itemPopupDelete);
        //ocs.setEnabled(classname, "itemPopupEditVer", itemPopupEditVer, true);
        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());

    }//GEN-LAST:event_tblDosisMousePressed

    private void jspDosisComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspDosisComponentResized
    }//GEN-LAST:event_jspDosisComponentResized

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void cmbANItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbANItemStateChanged
        saveOK();
    }//GEN-LAST:event_cmbANItemStateChanged

    private void cbABActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbABActionPerformed
        jdcAB.setEnabled(cbAB.isSelected());
        cmbAB.setEnabled(cbAB.isSelected());
        cmbKHAb.setEnabled(cbAB.isSelected());
        cmbAB.setSelectedIndex(0);
        cmbKHAb.setSelectedIndex(0);
        jdcAB.setDate(new Date());
        jdcAB.setMinSelectableDate(jdcAN.getDate());
        lblAB.setText(cbAB.isSelected() ? OPDE.getLogin().getUser().getUKennung() : "");
        saveOK();
    }//GEN-LAST:event_cbABActionPerformed


    private void fillAerzteUndKHs() {
        EntityManager em = OPDE.createEM();
        Query queryArzt = em.createNamedQuery("Arzt.findAll");
        List<Arzt> listAerzte = queryArzt.getResultList();
        listAerzte.add(0, null);

        Query queryKH = em.createNamedQuery("Krankenhaus.findAll");
        List<Krankenhaus> listKH = queryKH.getResultList();
        listKH.add(0, null);
        em.close();

        cmbAN.setModel(new DefaultComboBoxModel(listAerzte.toArray()));
        cmbAB.setModel(new DefaultComboBoxModel(listAerzte.toArray()));
        cmbAN.setRenderer(ArztTools.getArztRenderer());
        cmbAB.setRenderer(ArztTools.getArztRenderer());
        cmbAN.setSelectedIndex(0);
        cmbAB.setSelectedIndex(0);

        cmbKHAn.setModel(new DefaultComboBoxModel(listKH.toArray()));
        cmbKHAb.setModel(new DefaultComboBoxModel(listKH.toArray()));
        cmbKHAn.setRenderer(KrankenhausTools.getKHRenderer());
        cmbKHAb.setRenderer(KrankenhausTools.getKHRenderer());
        cmbKHAn.setSelectedIndex(0);
        cmbKHAb.setSelectedIndex(0);

//        em.close();
    }

    private void reloadTable() {
        String zubereitung = "x";
        if (verordnung.getDarreichung() != null) {
            zubereitung = verordnung.getDarreichung().getMedForm().getZubereitung();
        }

        tblDosis.setModel(new TMDosis(zubereitung, verordnung));
        tblDosis.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jspDosis.dispatchEvent(new ComponentEvent(jspDosis, ComponentEvent.COMPONENT_RESIZED));
        tblDosis.getColumnModel().getColumn(TMDosis.COL_Dosis).setCellRenderer(new RNDHTML());
        tblDosis.getColumnModel().getColumn(TMDosis.COL_Dosis).setHeaderValue("Anwendung");
        //tblDosis.getColumnModel().getColumn(1).setCellRenderer(new RNDStandard());

//        if (tblDosis.getModel().getRowCount() > 0) { // Sobald etwas in der Tabelle steht, darf man die Situation nicht mehr verändern.
//            txtSit.setEnabled(false);
//            txtSit.setText("");
//        } else {
//            txtSit.setEnabled(true);
//        }
        saveOK();

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel1;
    private JXSearchField txtMed;
    private JComboBox cmbMed;
    private JPanel panel4;
    private JButton btnEmptySit2;
    private JButton btnMed;
    private JComboBox cmbMass;
    private JXSearchField txtSit;
    private JComboBox cmbSit;
    private JPanel panel3;
    private JButton btnEmptySit;
    private JButton btnSituation;
    private JXSearchField xSearchField1;
    private JPanel jPanel8;
    private JScrollPane jspDosis;
    private JTable tblDosis;
    private JPanel panel2;
    private JButton btnAddDosis;
    private JCheckBox cbPackEnde;
    private JCheckBox cbStellplan;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JLabel jLabel3;
    private JDateChooser jdcAB;
    private JLabel jLabel4;
    private JComboBox cmbAB;
    private JCheckBox cbAB;
    private JLabel lblAB;
    private JComboBox cmbKHAb;
    private JScrollPane jScrollPane3;
    private JTextPane txtBemerkung;
    private JLabel jLabel5;
    private JPanel jPanel2;
    private JLabel jLabel1;
    private JDateChooser jdcAN;
    private JComboBox cmbAN;
    private JLabel jLabel2;
    private JLabel lblAN;
    private JComboBox cmbKHAn;
    private JPanel panel1;
    private JButton btnClose;
    private JButton btnSave;
    // End of variables declaration//GEN-END:variables

}
