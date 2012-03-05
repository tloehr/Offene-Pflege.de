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
import com.toedter.calendar.JDateChooser;
import entity.*;
import entity.verordnungen.*;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import tablemodels.TMDosis;
import tablerenderer.RNDHTML;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

/**
 * Bei einer Verordnungsänderung wird die alte Verordnung abgesetzt und die neue Verordnung statt dessen angesetzt. BHPs werden immer erstellt.
 * Bei neuen Verordnungen immer für den ganzen Tag. Bei Änderung immer ab dem Moment, ab dem die neue Verordnung gilt.
 *
 * @author tloehr
 */
public class DlgVerordnung extends javax.swing.JDialog {

    public static final int NEW_MODE = 1; // Neu
    public static final int EDIT_MODE = 2; // Korrigieren
    public static final int CHANGE_MODE = 3; // Ändern
    public static final int EDIT_OF_CHANGE_MODE = 4; // Das ist dann, wenn man eine Veränderung (Change) nachträglich nochmal korrigiert.

    private java.awt.Frame parent;
    private boolean ignoreSitCaret;
    private boolean ignoreEvent;


    private JPopupMenu menu;
    private PropertyChangeListener myPropertyChangeListener;
    private int editMode;

    Verordnung verordnung = null, oldVerordnung = null;

    EntityManager em;


    /**
     * Creates new form DlgVerordnung
     */
    public DlgVerordnung(java.awt.Frame parent, Verordnung verordnung, int mode) {
        super(parent, true);
        this.parent = parent;
        this.editMode = mode;
        em = OPDE.createEM();
        try {
            em.getTransaction().begin();

            if (editMode == CHANGE_MODE) {
                oldVerordnung = em.merge(verordnung);

                Map<String,Object> props = new HashMap<String, Object>();
                props.put("javax.persistence.lock.timeout", 3000);

                em.lock(oldVerordnung, LockModeType.PESSIMISTIC_WRITE, props);
                this.verordnung = (Verordnung) verordnung.clone();

            } else {
                this.verordnung = em.merge(verordnung);
                em.lock(this.verordnung, LockModeType.PESSIMISTIC_WRITE);
            }

        } catch (PessimisticLockException ple) {
            OPDE.debug(ple);
            em.getTransaction().rollback();
            em.close();
            dispose();
        } catch (Exception e) {
            OPDE.fatal(e);
        }

        initDialog();
    }

    private void cbStellplanActionPerformed(ActionEvent e) {
        if (ignoreEvent) {
            return;
        }
        verordnung.setStellplan(cbStellplan.isSelected());
    }

    private void jdcANPropertyChange(PropertyChangeEvent e) {
        if (ignoreEvent) {
            return;
        }
        verordnung.setAnDatum(jdcAN.getDate());
        saveOK();
    }

    private void jdcABPropertyChange(PropertyChangeEvent e) {
        if (ignoreEvent) {
            return;
        }
        verordnung.setAbDatum(jdcAB.getDate());
        saveOK();
    }

    public DlgVerordnung(java.awt.Frame parent, Bewohner bewohner) {
        super(parent, true);
        this.parent = parent;
        this.verordnung = new Verordnung(bewohner);
        this.editMode = NEW_MODE;
        initDialog();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblBW = new JLabel();
        lblTitle = new JLabel();
        jPanel1 = new JPanel();
        cmbSit = new JComboBox();
        txtMed = new JTextField();
        cmbMed = new JComboBox();
        cmbMass = new JComboBox();
        txtSit = new JTextField();
        btnBedarf = new JButton();
        btnMed = new JButton();
        jPanel8 = new JPanel();
        jspDosis = new JScrollPane();
        tblDosis = new JTable();
        cbPackEnde = new JCheckBox();
        cbStellplan = new JCheckBox();
        jLabel6 = new JLabel();
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
        jPanel5 = new JPanel();
        lblVerordnung = new JLabel();
        panel1 = new JPanel();
        btnSave = new JButton();
        btnClose = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                formWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
                "$rgap, $lcgap, pref, $lcgap, default:grow, $lcgap, $rgap",
                "3*(fill:default, $lgap), fill:default:grow, $lgap, fill:default, $lgap, $rgap"));

        //---- lblBW ----
        lblBW.setFont(new Font("Dialog", Font.BOLD, 18));
        lblBW.setForeground(new Color(255, 51, 0));
        lblBW.setText("Nachname, Vorname (*GebDatum, 00 Jahre) [??1]");
        contentPane.add(lblBW, CC.xywh(3, 3, 3, 1));

        //---- lblTitle ----
        lblTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        lblTitle.setText("\u00c4rztliche Verordnung");
        contentPane.add(lblTitle, CC.xywh(3, 1, 3, 1));

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
            jPanel1.setLayout(new FormLayout(
                    "63dlu, $lcgap, default, 2*($lcgap, default:grow)",
                    "3*(fill:default, $lgap), fill:default"));

            //---- cmbSit ----
            cmbSit.setModel(new DefaultComboBoxModel(new String[]{
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
            }));
            cmbSit.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cmbSitItemStateChanged(e);
                }
            });
            jPanel1.add(cmbSit, CC.xy(7, 3));

            //---- txtMed ----
            txtMed.setText("jTextField1");
            txtMed.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    txtMedCaretUpdate(e);
                }
            });
            txtMed.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtMedFocusGained(e);
                }
            });
            jPanel1.add(txtMed, CC.xy(5, 1));

            //---- cmbMed ----
            cmbMed.setModel(new DefaultComboBoxModel(new String[]{
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
            }));
            cmbMed.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cmbMedItemStateChanged(e);
                }
            });
            jPanel1.add(cmbMed, CC.xy(7, 1));

            //---- cmbMass ----
            cmbMass.setModel(new DefaultComboBoxModel(new String[]{
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
            }));
            cmbMass.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cmbMassItemStateChanged(e);
                }
            });
            jPanel1.add(cmbMass, CC.xywh(3, 5, 5, 1));

            //---- txtSit ----
            txtSit.setText("jTextField1");
            txtSit.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    txtSitCaretUpdate(e);
                }
            });
            jPanel1.add(txtSit, CC.xy(5, 3));

            //---- btnBedarf ----
            btnBedarf.setText("Situation");
            btnBedarf.setEnabled(false);
            btnBedarf.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnBedarfActionPerformed(e);
                }
            });
            jPanel1.add(btnBedarf, CC.xywh(1, 3, 3, 1));

            //---- btnMed ----
            btnMed.setText("Medikament");
            btnMed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnMedActionPerformed(e);
                }
            });
            jPanel1.add(btnMed, CC.xywh(1, 1, 3, 1));

            //======== jPanel8 ========
            {
                jPanel8.setBorder(new TitledBorder("Dosis / H\u00e4ufigkeit"));
                jPanel8.setLayout(new FormLayout(
                        "default, $lcgap, default:grow",
                        "fill:default:grow, $lgap, fill:default"));

                //======== jspDosis ========
                {
                    jspDosis.setToolTipText("<html>Dr\u00fccken Sie die <b>rechte</b> Maustaste, wenn Sie neue Dosierungen eintragen wollen.</html>");
                    jspDosis.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            jspDosisMousePressed(e);
                        }
                    });
                    jspDosis.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            jspDosisComponentResized(e);
                        }
                    });

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
                    tblDosis.setToolTipText("<html>Dr\u00fccken Sie die <b>rechte</b> Maustaste, wenn Sie Ver\u00e4nderungen vornehmen wollen.</html>");
                    tblDosis.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            tblDosisMousePressed(e);
                        }
                    });
                    jspDosis.setViewportView(tblDosis);
                }
                jPanel8.add(jspDosis, CC.xywh(1, 1, 3, 1));

                //---- cbPackEnde ----
                cbPackEnde.setText("Bis Packungsende");
                cbPackEnde.setBorder(BorderFactory.createEmptyBorder());
                cbPackEnde.setEnabled(false);
                cbPackEnde.setMargin(new Insets(0, 0, 0, 0));
                cbPackEnde.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbPackEndeActionPerformed(e);
                    }
                });
                jPanel8.add(cbPackEnde, CC.xy(1, 3));

                //---- cbStellplan ----
                cbStellplan.setText("Auf den Stellplan, auch wenn kein Medikament");
                cbStellplan.setBorder(BorderFactory.createEmptyBorder());
                cbStellplan.setMargin(new Insets(0, 0, 0, 0));
                cbStellplan.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbStellplanActionPerformed(e);
                    }
                });
                jPanel8.add(cbStellplan, CC.xy(3, 3));
            }
            jPanel1.add(jPanel8, CC.xywh(1, 7, 7, 1));

            //---- jLabel6 ----
            jLabel6.setText("Massnahmen:");
            jPanel1.add(jLabel6, CC.xy(1, 5));
        }
        contentPane.add(jPanel1, CC.xy(5, 7));

        //======== jPanel3 ========
        {
            jPanel3.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
            jPanel3.setLayout(new FormLayout(
                    "149dlu",
                    "3*(fill:default, $lgap), fill:default:grow"));

            //======== jPanel4 ========
            {
                jPanel4.setBorder(new TitledBorder("Absetzung"));
                jPanel4.setLayout(new FormLayout(
                        "default, $lcgap, default:grow",
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
                cmbAB.setModel(new DefaultComboBoxModel(new String[]{
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
                cmbKHAb.setModel(new DefaultComboBoxModel(new String[]{
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
                        "default, $lcgap, 68dlu:grow",
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
                cmbAN.setModel(new DefaultComboBoxModel(new String[]{
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
                cmbKHAn.setModel(new DefaultComboBoxModel(new String[]{
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
        contentPane.add(jPanel3, CC.xy(3, 7));

        //======== jPanel5 ========
        {
            jPanel5.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

            //---- lblVerordnung ----
            lblVerordnung.setFont(new Font("Dialog", Font.BOLD, 18));
            lblVerordnung.setForeground(new Color(0, 51, 255));
            lblVerordnung.setText("jLabel11");

            GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
            jPanel5.setLayout(jPanel5Layout);
            jPanel5Layout.setHorizontalGroup(
                    jPanel5Layout.createParallelGroup()
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(lblVerordnung, GroupLayout.DEFAULT_SIZE, 882, Short.MAX_VALUE)
                                    .addContainerGap())
            );
            jPanel5Layout.setVerticalGroup(
                    jPanel5Layout.createParallelGroup()
                            .addComponent(lblVerordnung, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );
        }
        contentPane.add(jPanel5, CC.xywh(3, 5, 3, 1));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));

            //---- btnSave ----
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.setText("Speichern");
            btnSave.setEnabled(false);
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });
            panel1.add(btnSave);

            //---- btnClose ----
            btnClose.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnClose.setText("Schlie\u00dfen");
            btnClose.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnCloseActionPerformed(e);
                }
            });
            panel1.add(btnClose);
        }
        contentPane.add(panel1, CC.xy(5, 9, CC.RIGHT, CC.DEFAULT));
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void cmbABItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbABItemStateChanged
        if (ignoreEvent) {
            return;
        }

        verordnung.setAbArzt((Arzt) cmbAB.getSelectedItem());

        saveOK();
    }//GEN-LAST:event_cmbABItemStateChanged

    private void cmbKHAbItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbKHAbItemStateChanged
        if (ignoreEvent) {
            return;
        }

        verordnung.setAbKH((Krankenhaus) cmbKHAb.getSelectedItem());

        saveOK();
    }//GEN-LAST:event_cmbKHAbItemStateChanged

    private void cmbKHAnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbKHAnItemStateChanged
        if (ignoreEvent) {
            return;
        }
        verordnung.setAnKH((Krankenhaus) cmbKHAn.getSelectedItem());

        saveOK();
    }//GEN-LAST:event_cmbKHAnItemStateChanged

    public void initDialog() {
        initComponents();
//        prepareTMPData();

        BewohnerTools.setBWLabel(lblBW, verordnung.getBewohner());
        setTitle(SYSTools.getWindowTitle("Ärztliche Verordnungen, Detailansicht"));
        fillAerzteUndKHs();

        ignoreSitCaret = true;
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
        if (this.editMode == NEW_MODE) { // NewMode
            lblTitle.setText(lblTitle.getText() + " (Neuer Eintrag)");
            jdcAN.setDate(SYSCalendar.today_date());
            cbAB.setSelected(false);
            txtBemerkung.setText("");
            lblAN.setText(OPDE.getLogin().getUser().getUKennung());
            lblAB.setText("");
            cmbSit.setModel(new DefaultComboBoxModel());
            cmbMass.setSelectedIndex(-1);
            cbStellplan.setEnabled(true);
            cbStellplan.setSelected(false);
            tblDosis.setModel(new DefaultTableModel());
            lblVerordnung.setText(" ");

            cbPackEnde.setEnabled(false);
        } else { // CHANGE oder EDIT
            lblTitle.setText(lblTitle.getText() + (editMode == EDIT_MODE ? " (Korrektur)" : " (Änderung der bestehenden Verordnung)"));
            // Bei einer Änderung muss sich das Fenster am Anfang in einem Zustand befinden,
            // der ein Save ermöglich
            btnSave.setEnabled(true);
            //HashMap verordnung = DBRetrieve.getSingleRecord("BHPVerordnung", new String[]{"AnDatum", "AbDatum", "AnArztID", "AbArztID", "AnKHID", "AbKHID", "AnUKennung", "AbUKennung", "VerKennung", "Bemerkung", "MassID", "DafID", "SitID", "BisPackEnde", "Stellplan"}, "VerID", verid);

            jdcAN.setDate(new Date());
            if (this.editMode == EDIT_MODE) {
                lblAN.setText(verordnung.getAngesetztDurch().getUKennung());
            } else {
                lblAN.setText(OPDE.getLogin().getUser().getUKennung());
            }
            cmbAN.setSelectedItem(verordnung.getAnArzt());
            cmbKHAn.setSelectedItem(verordnung.getAnKH());

            cbPackEnde.setSelected(verordnung.isBisPackEnde());

            jdcAN.setEnabled(editMode == EDIT_MODE);
            txtBemerkung.setText(SYSTools.catchNull(verordnung.getBemerkung()));

            if (verordnung.hasMedi()) {
                cmbMed.setModel(new DefaultComboBoxModel(new Darreichung[]{verordnung.getDarreichung()}));
            }

            cmbMass.setEnabled(cmbMed.getModel().getSize() == 0);
            cbStellplan.setEnabled(cmbMed.getModel().getSize() == 0);
            cbStellplan.setSelected(verordnung.isStellplan());

            cmbSit.setModel(new DefaultComboBoxModel(new Situationen[]{verordnung.getSituation()}));

            cmbMass.setSelectedItem(verordnung.getMassnahme());

            cmbMed.setEnabled(this.editMode == NEW_MODE || this.editMode == EDIT_MODE);
            txtMed.setEnabled(this.editMode == NEW_MODE || this.editMode == EDIT_MODE);
            txtSit.setEnabled(this.editMode == NEW_MODE || this.editMode == EDIT_MODE);
            cmbSit.setEnabled(this.editMode == NEW_MODE || this.editMode == EDIT_MODE);

            if (cmbMed.getSelectedItem() != null) {
                lblVerordnung.setText(DarreichungTools.toPrettyString((Darreichung) cmbMed.getSelectedItem()));
                cbPackEnde.setEnabled(true);
            } else {
                lblVerordnung.setText(((Massnahmen) cmbMass.getSelectedItem()).getBezeichnung());
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
        }

        reloadTable();
        ignoreSitCaret = false;
        ignoreEvent = false;
        pack();
        SYSTools.centerOnParent(parent, this);
        txtMed.requestFocus();

        myPropertyChangeListener = new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("value")) {
                    JDateChooser jdcDatum = (JDateChooser) ((JComponent) evt.getSource()).getParent();
                    SYSCalendar.checkJDC(jdcDatum);
                }
            }
        };

        jdcAN.getDateEditor().addPropertyChangeListener(myPropertyChangeListener);
        jdcAB.getDateEditor().addPropertyChangeListener(myPropertyChangeListener);
        setVisible(true);
    }

    private void txtMedFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMedFocusGained
        SYSTools.markAllTxt(txtMed);
    }//GEN-LAST:event_txtMedFocusGained

    private void cmbMassItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbMassItemStateChanged
        if (ignoreEvent) {
            return;
        }
        verordnung.setMassnahme((Massnahmen) cmbMass.getSelectedItem());
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

    private void btnBedarfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBedarfActionPerformed
        if (JOptionPane.showConfirmDialog(this, "\"" + txtSit.getText() + "\"", "Situation hinzufügen",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

            Situationen neueSituation = new Situationen(txtSit.getText());
            EntityTools.persist(neueSituation);

            cmbSit.setModel(new DefaultComboBoxModel(new Situationen[]{neueSituation}));
            cbPackEnde.setEnabled(false);
            ignoreEvent = true;
            cbPackEnde.setSelected(false);
            ignoreEvent = false;
        }
    }//GEN-LAST:event_btnBedarfActionPerformed

    private void jspDosisMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jspDosisMousePressed
        if (!evt.isPopupTrigger()) {
            return;
        }

        // Wenn die Dosis Tabelle leer ist, dann funktioniert da auch kein MousePressed Event
        // In diesem Fall muss die ScrollPane einspringen.
        TableModel tm = tblDosis.getModel();
        if (tm.getRowCount() > 0) {
            return;
        }

        SYSTools.unregisterListeners(menu);
        menu = new JPopupMenu();

        JMenuItem itemPopupNew = new JMenuItem("Neue Dosis eingeben");
        itemPopupNew.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Date ldatum = null;
                DlgVerabreichung dlg = new DlgVerabreichung(parent, verordnung);
                if (dlg.getPlanung() != null) {
                    verordnung.getPlanungen().add(dlg.getPlanung());
                    reloadTable();
                }
                dlg = null;
            }
        });
        menu.add(itemPopupNew);
        Point p = evt.getPoint();
        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }//GEN-LAST:event_jspDosisMousePressed

    private void cmbMedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbMedItemStateChanged
        if (ignoreEvent) {
            return;
        }
        verordnung.setDarreichung((Darreichung) cmbMed.getSelectedItem());
        cmbMass.setSelectedItem(verordnung.getDarreichung().getMedForm().getMassnahme());
        cmbMass.setEnabled(false);
        cbStellplan.setEnabled(false);
        cbStellplan.setSelected(false);
        lblVerordnung.setText(DarreichungTools.toPrettyString((Darreichung) cmbMed.getSelectedItem()));
        // Bestand prüfen
        saveOK();
    }//GEN-LAST:event_cmbMedItemStateChanged

    private void cbPackEndeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPackEndeActionPerformed
        if (ignoreEvent) {
            return;
        }
        verordnung.setBisPackEnde(cbPackEnde.isSelected());
        saveOK();
    }//GEN-LAST:event_cbPackEndeActionPerformed

    private void saveOK() {
        boolean ansetzungOK = jdcAN.getDate() != null && (cmbAN.getSelectedIndex() > 0 || cmbKHAn.getSelectedIndex() > 0);
        boolean absetzungOK = !cbAB.isSelected() || (jdcAB.getDate() != null && (cmbAB.getSelectedIndex() > 0 || cmbKHAb.getSelectedIndex() > 0));
        boolean medOK = cmbMed.getModel().getSize() == 0 || cmbMed.getSelectedItem() != null;
        boolean massOK = cmbMass.getSelectedItem() != null;
        boolean dosisVorhanden = tblDosis.getModel().getRowCount() > 0;
        btnSave.setEnabled(ansetzungOK && absetzungOK && medOK && massOK && dosisVorhanden);
        cbPackEnde.setEnabled(!verordnung.isBedarf() && cmbMed.getModel().getSize() > 0);

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
        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    public void dispose() {

        if (em.isOpen()){
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
        }

        jdcAB.removePropertyChangeListener(myPropertyChangeListener);
        jdcAN.removePropertyChangeListener(myPropertyChangeListener);
        jdcAB.cleanup();
        jdcAN.cleanup();
        SYSTools.unregisterListeners(this);
        super.dispose();
    }

    private void cmbSitItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSitItemStateChanged
        if (ignoreEvent) {
            return;
        }
        verordnung.setSituation((Situationen) cmbSit.getSelectedItem());
        saveOK();
    }//GEN-LAST:event_cmbSitItemStateChanged

    private void txtBemerkungCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBemerkungCaretUpdate
        if (ignoreEvent) {
            return;
        }
        verordnung.setBemerkung(txtBemerkung.getText());
        saveOK();
    }//GEN-LAST:event_txtBemerkungCaretUpdate

    private void txtSitCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtSitCaretUpdate
        if (!txtSit.isEnabled() || ignoreEvent || ignoreSitCaret) {
            return;
        }
        if (txtSit.getText().equals("")) {
            cmbSit.setModel(new DefaultComboBoxModel());
            btnBedarf.setEnabled(false);
        } else {
            cmbSit.setModel(new DefaultComboBoxModel(SituationenTools.findSituationByText(txtSit.getText()).toArray()));
            btnBedarf.setEnabled(cmbSit.getModel().getSize() == 0);
        }
        saveOK();
    }//GEN-LAST:event_txtSitCaretUpdate

    private void save() {
        try {

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
            verordnung.setSituation((Situationen) cmbSit.getSelectedItem());


            // Sicherung
            if (editMode == NEW_MODE) { // =================== NEU ====================
                // Bei einer neuen Verordnung kann einfach eingetragen werden. Die BHP spielt hier keine Rolle.
                verordnung.setVerKennung(UniqueTools.getNewUID(em, "__verkenn").getUid());
                em.persist(verordnung);
            } else if (editMode == EDIT_MODE) { // =================== KORREKTUR ====================
                // Bei einer Korrektur werden alle bisherigen Einträge aus der BHP zuerst wieder entfernt.
                verordnung = em.merge(verordnung);
                Query queryDELBHP = em.createQuery("DELETE FROM BHP bhp WHERE bhp.verordnungPlanung.verordnung = :verordnung");
                queryDELBHP.setParameter("verordnung", verordnung);
                queryDELBHP.executeUpdate();
            } else { // if(editMode == CHANGE_MODE) { // =================== VERÄNDERUNG ====================
                // Bei einer Veränderung, wird erst die alte Verordnung durch den ANsetzenden Arzt ABgesetzt.
                // Dann werden die nicht mehr benötigten BHPs entfernt.
                // Dann wird die neue Verordnung angesetzt.
                oldVerordnung = VerordnungTools.absetzen(em, oldVerordnung, verordnung.getAnArzt(), verordnung.getAnKH());

                // die neue Verordnung beginnt eine Sekunde, nachdem die vorherige Abgesetzt wurde.
                verordnung.setAnDatum(SYSCalendar.addField(oldVerordnung.getAbDatum(), 1, GregorianCalendar.SECOND));
                em.persist(verordnung);
            }


            if (!verordnung.isBedarf()) {
                if (editMode == CHANGE_MODE || editMode == EDIT_OF_CHANGE_MODE) {
                    // ab der aktuellen Uhrzeit
                    BHPTools.erzeugen(em, verordnung.getPlanungen(), new Date(), verordnung.getAnDatum());
                } else {
                    // für den ganzen Tag
                    BHPTools.erzeugen(em, verordnung.getPlanungen(), new Date(), null);
                }
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        if (OPDE.isDebug()) {
            if (editMode != CHANGE_MODE) {
                OPDE.debug("Verordnung wurde neu erstellt bzw. korrigiert");
                OPDE.debug(verordnung);
                CollectionUtils.forAllDo(verordnung.getPlanungen(), new Closure() {
                    @Override
                    public void execute(Object o) {
                        OPDE.debug(o);
                    }
                });
            } else {
                OPDE.debug("Verordnung wurde neu geändert und gegen eine neue ersetzt.");
                OPDE.debug("ALT");
                OPDE.debug("==============");
                OPDE.debug(oldVerordnung);
                CollectionUtils.forAllDo(oldVerordnung.getPlanungen(), new Closure() {
                    @Override
                    public void execute(Object o) {
                        OPDE.debug(o);
                    }
                });
                OPDE.debug("==============");
                OPDE.debug("NEU");
                OPDE.debug("==============");
                OPDE.debug(verordnung);
                CollectionUtils.forAllDo(verordnung.getPlanungen(), new Closure() {
                    @Override
                    public void execute(Object o) {
                        OPDE.debug(o);
                    }
                });
            }
        } // DEBUG OUTPUT
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
        //final int col = tblDosis.columnAtPoint(p);
        final int row = tblDosis.rowAtPoint(p);
        ListSelectionModel lsm = tblDosis.getSelectionModel();
        lsm.setSelectionInterval(row, row);


        //final long bhppid = ((Long) tm.getValueAt(row, TMDosis.COL_BHPPID)).longValue();


        // Menüeinträge
        SYSTools.unregisterListeners(menu);
        menu = new JPopupMenu();

        JMenuItem itemPopupNew = new JMenuItem("Neue Dosis eingeben");
        itemPopupNew.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //Date ldatum = null;
                DlgVerabreichung dlg = new DlgVerabreichung(parent, verordnung);
                if (dlg.getPlanung() != null) {
                    verordnung.getPlanungen().add(dlg.getPlanung());
                    reloadTable();
                }
                dlg = null;
            }
        });
        menu.add(itemPopupNew);
        // Bei Bedarfsmedikation kann immer nur eine Dosis eingegeben werden.
        itemPopupNew.setEnabled(!verordnung.isBedarf() || tm.getRowCount() == 0);

        JMenuItem itemPopupEditText = new JMenuItem("Bearbeiten");
        itemPopupEditText.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VerordnungPlanung planung = verordnung.getPlanungen().toArray(new VerordnungPlanung[0])[row];
                DlgVerabreichung dlg = new DlgVerabreichung(parent, planung, verordnung);
                dlg = null;
                reloadTable();
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
                reloadTable();
            }
        });
        menu.add(itemPopupDelete);
        //ocs.setEnabled(classname, "itemPopupEditVer", itemPopupEditVer, true);
        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());

    }//GEN-LAST:event_tblDosisMousePressed

    private void jspDosisComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspDosisComponentResized
    }//GEN-LAST:event_jspDosisComponentResized

    private void txtMedCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtMedCaretUpdate
        if (ignoreEvent) {
            return;
        }
        if (!txtMed.isEnabled() || ignoreEvent) {
            return;
        }
        if (txtMed.getText().equals("")) {
            cmbMed.setModel(new DefaultComboBoxModel());

            cmbMass.setEnabled(true);
            cbStellplan.setEnabled(true);
            cbStellplan.setSelected(false);
            lblVerordnung.setText(" ");
            cmbMed.setToolTipText("");
            ignoreEvent = true;
            cbPackEnde.setSelected(false);
            ignoreEvent = false;
            cbPackEnde.setEnabled(false);
        } else {
            if (txtMed.getText().matches("^ß?\\d{7}")) { // Hier sucht man nach einer PZN. Im Barcode ist das führende 'ß' enthalten.
                String pzn = txtMed.getText();

                EntityManager em = OPDE.createEM();
                pzn = (pzn.startsWith("ß") ? pzn.substring(1) : pzn);
                Query pznQuery = em.createNamedQuery("MedPackung.findByPzn");
                pznQuery.setParameter("pzn", pzn);

                try {
                    MedPackung medPackung = (MedPackung) pznQuery.getSingleResult();
                    cmbMed.setModel(new DefaultComboBoxModel(new Darreichung[]{medPackung.getDarreichung()}));
                } catch (NoResultException nre) {
                    OPDE.debug("Nichts passendes zu dieser PZN gefunden");
                } catch (Exception e) {
                    OPDE.fatal(e);
                } finally {
                    em.close();
                }

            } else { // Falls die Suche NICHT nur aus Zahlen besteht, dann nach Namen suchen.
                cmbMed.setModel(new DefaultComboBoxModel(DarreichungTools.findDarreichungByMedProduktText(txtMed.getText()).toArray()));
            }

            if (cmbMed.getModel().getSize() > 0) {
                cmbMedItemStateChanged(null);
            } else {
                lblVerordnung.setText(" ");
                cmbMed.setToolTipText("");
                cmbMass.setSelectedIndex(-1);
                cmbMass.setEnabled(true);
                cbStellplan.setEnabled(true);
                cbStellplan.setSelected(false);
                ignoreEvent = true;
                cbPackEnde.setSelected(false);
                ignoreEvent = false;
            }
            cbPackEnde.setEnabled(!verordnung.isBedarf() && cmbMed.getModel().getSize() > 0);
        }
    }//GEN-LAST:event_txtMedCaretUpdate

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed

        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void cmbANItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbANItemStateChanged
        if (ignoreEvent) {
            return;
        }
        verordnung.setAnArzt((Arzt) cmbAN.getSelectedItem());
        saveOK();
    }//GEN-LAST:event_cmbANItemStateChanged

    private void cbABActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbABActionPerformed
        if (ignoreEvent) {
            return;
        }
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

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        dispose();
    }//GEN-LAST:event_formWindowClosing

    private void fillAerzteUndKHs() {
        EntityManager em = OPDE.createEM();
        Query queryArzt = em.createNamedQuery("Arzt.findAll");
        List<Arzt> listAerzte = queryArzt.getResultList();
        listAerzte.add(0, null);

        Query queryKH = em.createNamedQuery("Krankenhaus.findAll");
        List<Krankenhaus> listKH = queryKH.getResultList();
        listKH.add(0, null);

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

        em.close();
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

        if (tblDosis.getModel().getRowCount() > 0) { // Sobald etwas in der Tabelle steht, darf man die Situation nicht mehr verändern.
            txtSit.setEnabled(false);
            txtSit.setText("");
        } else {
            txtSit.setEnabled(true);
        }
        saveOK();

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel lblBW;
    private JLabel lblTitle;
    private JPanel jPanel1;
    private JComboBox cmbSit;
    private JTextField txtMed;
    private JComboBox cmbMed;
    private JComboBox cmbMass;
    private JTextField txtSit;
    private JButton btnBedarf;
    private JButton btnMed;
    private JPanel jPanel8;
    private JScrollPane jspDosis;
    private JTable tblDosis;
    private JCheckBox cbPackEnde;
    private JCheckBox cbStellplan;
    private JLabel jLabel6;
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
    private JPanel jPanel5;
    private JLabel lblVerordnung;
    private JPanel panel1;
    private JButton btnSave;
    private JButton btnClose;
    // End of variables declaration//GEN-END:variables

}
