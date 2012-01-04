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
package op.care.med.vorrat;

import entity.Bewohner;
import entity.BewohnerTools;
import entity.EntityTools;
import entity.verordnungen.*;
import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;
import tablemodels.BeanTableModel;
import tablemodels.TMBestand;
import tablemodels.TMVorraete;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * In OPDE.de gibt es eine Bestandsverwaltung für Medikamente. Bestände werden mit Hilfe von 3 Tabellen
 * in der Datenbank verwaltet.
 * <ul>
 * <li><B>MPVorrat</B> Ein Vorrat ist wie eine Schachtel oder Schublade zu sehen, in denen
 * einzelne Päckchen enthalten sind. Jetzt kann es natürlich passieren, dass verschiedene
 * Präparete, die aber pharmazeutisch gleichwertig sind in derselben Schachtel enthalten
 * sind. Wenn z.B. 3 verschiedene Medikamente mit demselben Wirkstoff, derselben Darreichungsform
 * und in derselben Stärke vorhanden sind, dann sollten sie auch in demselben Vorrat zusammengefasst
 * werden. Vorräte gehören immer einem bestimmten Bewohner.</li>
 * <li><B>MPBestand</B> Ein Bestand entspricht i.d.R. einer Verpackung. Also eine Schachtel eines
 * Medikamentes wäre für sich genommen ein Bestand. Aber auch z.B. wenn ein BW von zu Hause einen
 * angebrochenen Blister mitbringt, dann wird dies als eigener Bestand angesehen. Bestände gehören
 * immer zu einem bestimmten Vorrat. Das Eingangs-, Ausgangs und Anbruchdatum wird vermerkt. Es meistens
 * einen Verweis auf eine MPID aus der Tabelle MPackung. Bei eigenen Gebinden kann dieses Feld auch
 * <CODE>null</CODE> sein.</li>
 * <li><B>MPBuchung</B> Eine Buchung ist ein Ein- bzw. Ausgang von einer Menge von Einzeldosen zu oder von
 * einem bestimmten Bestand. Also wenn eine Packung eingebucht wird, dann wird ein Bestand erstellt und
 * eine Eingangsbuchung in Höhe der Ursprünglichen Packungsgrößen (z.B. 100 Stück). Bei Vergabe von
 * Medikamenten an einen Bewohner (über Abhaken in der BHP) werden die jeweiligen Mengen
 * ausgebucht. In diesem Fall steht in der Spalte BHPID der Verweis zur entsprechenden Zeile in der
 * Tabelle BHP.</li>
 * </ul>
 *
 * @author tloehr
 */
public class DlgVorrat extends javax.swing.JDialog {

    private Bewohner bewohner;
    private Component parent;
    private Component thisDialog;
    private JPopupMenu menuV;
    //    private JPopupMenu menuB;
    private JPopupMenu menuBuch;

    //    private OCSec ocs;
    private MedVorrat vorrat;
    private MedBestand bestand;

    /**
     * Creates new form DlgVorrat
     */
    public DlgVorrat(JDialog parent, Bewohner bewohner) {
        super(parent, true);
        this.parent = parent;
        this.bewohner = bewohner;
        initDialog();
    }

    public DlgVorrat(JFrame parent, Bewohner bewohner) {
        super(parent, true);
        this.parent = parent;
        this.bewohner = bewohner;
        initDialog();
    }

    public DlgVorrat(JDialog parent) {
        super(parent, true);
        this.parent = parent;
        this.bewohner = null;
        initDialog();
    }

    public DlgVorrat(JFrame parent) {
        super(parent, true);
        this.parent = parent;
        this.bewohner = null;
        initDialog();
    }

    private void cmbBWPropertyChange(PropertyChangeEvent e) {
        OPDE.debug("MODEL");
        cmbBWItemStateChanged(null);
    }

    private void jspBestandComponentResized(ComponentEvent e) {
        JScrollPane jsp = (JScrollPane) e.getComponent();
        Dimension dim = jsp.getSize();

        TableColumnModel tcm1 = tblBestand.getColumnModel();

        if (tcm1.getColumnCount() == 0) {
            return;
        }

        tcm1.getColumn(TMBestand.COL_NAME).setPreferredWidth(dim.width / 5 * 4);  // 4/5 tel der Gesamtbreite
        tcm1.getColumn(TMBestand.COL_MENGE).setPreferredWidth(dim.width / 5);  // 1/5 tel der Gesamtbreite
        tcm1.getColumn(TMBestand.COL_NAME).setHeaderValue("Bestandsangabe");
        tcm1.getColumn(TMBestand.COL_MENGE).setHeaderValue("Restsumme");
    }

    private void initDialog() {
        setTitle(SYSTools.getWindowTitle("Medikamentenvorrat"));
        thisDialog = this;

        initComponents();
        txtSuche.setEnabled(bewohner == null);
        cmbBW.setEnabled(bewohner == null);
        if (bewohner == null) {
            txtSuche.requestFocus();
            lblBW.setText("");
        } else {
            BewohnerTools.setBWLabel(lblBW, bewohner);
        }
        reloadVorratTable();
        SYSTools.centerOnParent(parent, this);
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
        lblFrage = new JLabel();
        lblBW = new JLabel();
        jPanel2 = new JPanel();
        jspVorrat = new JScrollPane();
        tblVorrat = new JTable();
        cbClosedVorrat = new JCheckBox();
        jPanel3 = new JPanel();
        jspBestand = new JScrollPane();
        tblBestand = new JTable();
        cbClosedBestand = new JCheckBox();
        btnClose = new JButton();
        pnl123 = new JPanel();
        jspBuchung = new JScrollPane();
        tblBuchung = new JTable();
        jPanel1 = new JPanel();
        txtSuche = new JTextField();
        cmbBW = new JComboBox();
        jSeparator1 = new JSeparator();
        jToolBar1 = new JToolBar();
        btnBestandsliste = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();

        //---- lblFrage ----
        lblFrage.setFont(new Font("Dialog", Font.BOLD, 24));
        lblFrage.setText("Medikamenten Vorrat");

        //---- lblBW ----
        lblBW.setFont(new Font("Dialog", Font.BOLD, 18));
        lblBW.setForeground(new Color(255, 51, 0));
        lblBW.setText("Nachname, Vorname (*GebDatum, 00 Jahre) [??1]");

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("Vorr\u00e4te"));

            //======== jspVorrat ========
            {
                jspVorrat.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        jspVorratMousePressed(e);
                    }
                });

                //---- tblVorrat ----
                tblVorrat.setModel(new DefaultTableModel(
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
                tblVorrat.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        tblVorratMousePressed(e);
                    }
                });
                jspVorrat.setViewportView(tblVorrat);
            }

            //---- cbClosedVorrat ----
            cbClosedVorrat.setText("Abgeschlossene anzeigen");
            cbClosedVorrat.setBorder(BorderFactory.createEmptyBorder());
            cbClosedVorrat.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cbClosedVorratItemStateChanged(e);
                }
            });

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                    jPanel2Layout.createParallelGroup()
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel2Layout.createParallelGroup()
                                            .addComponent(jspVorrat, GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                                            .addComponent(cbClosedVorrat))
                                    .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                    jPanel2Layout.createParallelGroup()
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(cbClosedVorrat)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jspVorrat, GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                                    .addContainerGap())
            );
        }

        //======== jPanel3 ========
        {
            jPanel3.setBorder(new TitledBorder("Best\u00e4nde"));

            //======== jspBestand ========
            {
                jspBestand.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        jspBestandMousePressed(e);
                    }
                });
                jspBestand.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        jspBestandComponentResized(e);
                    }
                });

                //---- tblBestand ----
                tblBestand.setModel(new DefaultTableModel(
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
                tblBestand.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        tblBestandMousePressed(e);
                    }
                });
                jspBestand.setViewportView(tblBestand);
            }

            //---- cbClosedBestand ----
            cbClosedBestand.setText("Abgeschlossene anzeigen");
            cbClosedBestand.setBorder(BorderFactory.createEmptyBorder());
            cbClosedBestand.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cbClosedBestandItemStateChanged(e);
                }
            });

            GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                    jPanel3Layout.createParallelGroup()
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel3Layout.createParallelGroup()
                                            .addComponent(jspBestand, GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                                            .addComponent(cbClosedBestand))
                                    .addContainerGap())
            );
            jPanel3Layout.setVerticalGroup(
                    jPanel3Layout.createParallelGroup()
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(cbClosedBestand)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jspBestand, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                                    .addContainerGap())
            );
        }

        //---- btnClose ----
        btnClose.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
        btnClose.setText("Schlie\u00dfen");
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCloseActionPerformed(e);
            }
        });

        //======== pnl123 ========
        {
            pnl123.setBorder(new TitledBorder("Buchungen"));

            //======== jspBuchung ========
            {
                jspBuchung.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        jspBuchungMousePressed(e);
                    }
                });

                //---- tblBuchung ----
                tblBuchung.setModel(new DefaultTableModel(
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
                tblBuchung.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        tblBuchungMousePressed(e);
                    }
                });
                jspBuchung.setViewportView(tblBuchung);
            }

            GroupLayout pnl123Layout = new GroupLayout(pnl123);
            pnl123.setLayout(pnl123Layout);
            pnl123Layout.setHorizontalGroup(
                    pnl123Layout.createParallelGroup()
                            .addGroup(pnl123Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jspBuchung, GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                                    .addContainerGap())
            );
            pnl123Layout.setVerticalGroup(
                    pnl123Layout.createParallelGroup()
                            .addGroup(pnl123Layout.createSequentialGroup()
                                    .addComponent(jspBuchung, GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                                    .addContainerGap())
            );
        }

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Suche"));

            //---- txtSuche ----
            txtSuche.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    txtSucheCaretUpdate(e);
                }
            });
            txtSuche.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtSucheActionPerformed(e);
                }
            });
            txtSuche.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtSucheFocusGained(e);
                }
            });

            //---- cmbBW ----
            cmbBW.setModel(new DefaultComboBoxModel(new String[]{

            }));
            cmbBW.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cmbBWItemStateChanged(e);
                }
            });
            cmbBW.addPropertyChangeListener("model", new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                    cmbBWPropertyChange(e);
                }
            });

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addComponent(cmbBW, GroupLayout.Alignment.LEADING, 0, 931, Short.MAX_VALUE)
                                            .addComponent(txtSuche, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 931, Short.MAX_VALUE))
                                    .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(txtSuche, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cmbBW, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap(46, Short.MAX_VALUE))
            );
        }

        //======== jToolBar1 ========
        {
            jToolBar1.setFloatable(false);

            //---- btnBestandsliste ----
            btnBestandsliste.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/fileprint.png")));
            btnBestandsliste.setText("Bestandsliste");
            btnBestandsliste.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnBestandslisteActionPerformed(e);
                }
            });
            jToolBar1.add(btnBestandsliste);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(jSeparator1, GroupLayout.DEFAULT_SIZE, 981, Short.MAX_VALUE)
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(pnl123, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(btnClose, GroupLayout.Alignment.TRAILING))
                                .addContainerGap())
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblFrage, GroupLayout.DEFAULT_SIZE, 981, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblBW, GroupLayout.DEFAULT_SIZE, 981, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                        .addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, 1003, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblFrage)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblBW)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(pnl123, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnClose)
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void cbClosedBestandItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbClosedBestandItemStateChanged
        reloadBestandTable();
    }//GEN-LAST:event_cbClosedBestandItemStateChanged

    private void cbClosedVorratItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbClosedVorratItemStateChanged
        reloadVorratTable();
    }//GEN-LAST:event_cbClosedVorratItemStateChanged

    private void btnBestandslisteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBestandslisteActionPerformed
        printBestand();
    }//GEN-LAST:event_btnBestandslisteActionPerformed

    private void printBestand() {
//        if (!bwkennung.equals("") ||
//                JOptionPane.showConfirmDialog(this, "Es wurde kein Bewohner ausgewählt.\nMöchten Sie wirklich eine Gesamtliste ausdrucken ?", "Frage", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//
//            HashMap params = new HashMap();
//            JRDSMedBestand jrds = new JRDSMedBestand(bwkennung);
//
//            SYSPrint.printReport(preview, jrds, params, "medbestand", dialog);
//            if (!preview && !dialog) {
//                JOptionPane.showMessageDialog(this, "Der Druckvorgang ist abgeschlossen.", "Drucker", JOptionPane.INFORMATION_MESSAGE);
//            }
//        }
    }

    private void cmbBWItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbBWItemStateChanged
        if (cmbBW.getModel().getSize() > 0) {
            bewohner = (Bewohner) cmbBW.getSelectedItem();
            reloadVorratTable();
        }
    }//GEN-LAST:event_cmbBWItemStateChanged

    private void txtSucheCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtSucheCaretUpdate
//        if (ignoreEvent || !txtSuche.isEnabled()) {
//            return;
//        }
//        if (txtSuche.getText().equals("")) {
//            cmbBW.setModel(new DefaultComboBoxModel());
//            bewohner = null;
//            reloadVorratTable();
//        } else if (txtSuche.getText().matches("\\d*")) { // Nur Zahlen.. Das ist eine BestID
//            bewohner = null;
//            reloadVorratTable();
//        } else {
//            DefaultComboBoxModel dcbm = null;
//            if (txtSuche.getText().length() == 3) { // Könnte eine Suche nach der Kennung sein
//                ResultSet rs = op.tools.DBRetrieve.getResultSet("Bewohner", new String[]{"BWKennung", "Nachname", "Vorname", "GebDatum"}, "BWKennung",
//                        txtSuche.getText(), "=");
//                dcbm = SYSTools.rs2cmb(rs);
//            }
//            if (dcbm == null || dcbm.getSize() == 0) {
//                ResultSet rs = op.tools.DBRetrieve.getResultSet("Bewohner", new String[]{"BWKennung", "Nachname", "Vorname", "GebDatum"}, "Nachname",
//                        "%" + txtSuche.getText() + "%", "like");
//                dcbm = SYSTools.rs2cmb(rs, new String[]{"", "", "", "*"});
//            }
//            if (dcbm != null && dcbm.getSize() > 0) {
//                cmbBW.setModel(dcbm);
//                cmbBW.setSelectedIndex(0);
//                cmbBWItemStateChanged(null);
//            } else {
//                cmbBW.setModel(new DefaultComboBoxModel());
//                bwkennung = "";
//            }
//
//        }
    }//GEN-LAST:event_txtSucheCaretUpdate

    private void txtSucheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSucheActionPerformed
        if (!txtSuche.getText().equals("")) {
            if (txtSuche.getText().matches("\\d*")) { // Nur Zahlen.. Das ist eine BestID
                long bestid = Long.parseLong(txtSuche.getText());
                EntityManager em = OPDE.createEM();
                bestand = em.find(MedBestand.class, bestid);
                em.close();

                if (bestand != null) {
                    bewohner = bestand.getVorrat().getBewohner();
                    reloadVorratTable();
                    cmbBW.setModel(new DefaultComboBoxModel(new Bewohner[]{bewohner}));
                    TMVorraete tm = (TMVorraete) tblVorrat.getModel();
                    int row = tm.findPositionOf(bestand.getVorrat());
                    tblVorrat.getSelectionModel().setSelectionInterval(row, row);
                } else {
                    JOptionPane.showMessageDialog(this, "Der eingegebene Bestand existiert nicht.", "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                bewohner = null;
                if (txtSuche.getText().length() == 3) { // Könnte eine Suche nach der Bewohner Kennung sein
                    bewohner = EntityTools.find(Bewohner.class, txtSuche.getText());
                    if (bewohner != null) {
                        cmbBW.setModel(new DefaultComboBoxModel(new Bewohner[]{bewohner}));
                        cmbBW.setSelectedIndex(0);
                    }
                }
                if (bewohner == null) {
                    EntityManager em = OPDE.createEM();
                    Query query = em.createNamedQuery("Bewohner.findByNachname");
                    query.setParameter("nachname", "%" + txtSuche.getText() + "%");
                    cmbBW.setModel(new DefaultComboBoxModel(query.getResultList().toArray(new Bewohner[]{})));
                    cmbBW.setSelectedIndex(0);
                }
            }
        } else {
            cmbBW.setModel(new DefaultComboBoxModel());
            bewohner = null;
            reloadVorratTable();
        }
    }//GEN-LAST:event_txtSucheActionPerformed

    private void txtSucheFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSucheFocusGained
        SYSTools.markAllTxt(txtSuche);
    }//GEN-LAST:event_txtSucheFocusGained

    private void jspBuchungMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jspBuchungMousePressed
        tblBuchungMousePressed(evt);
    }//GEN-LAST:event_jspBuchungMousePressed

    private void tblBuchungMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBuchungMousePressed
        final BeanTableModel tm = (BeanTableModel) tblBuchung.getModel();
        if (tm.getRowCount() == 0) {
            bestand = null;
            return;
        }

        Point p = evt.getPoint();
        final int row = tblBuchung.rowAtPoint(p);
        ListSelectionModel lsm = tblBuchung.getSelectionModel();
        lsm.setSelectionInterval(row, row);

        final MedBuchungen buchung = (MedBuchungen) tm.getRow(row);

        if (evt.isPopupTrigger()) {

            SYSTools.unregisterListeners(menuBuch);
            menuBuch = new JPopupMenu();


            JMenuItem itemPopupNew = new JMenuItem("Neu");
            itemPopupNew.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    newBuchung();
                }
            });
            menuBuch.add(itemPopupNew);

            // Menüeinträge

            JMenuItem itemPopupDelete = new JMenuItem("Löschen");
            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {

                    if (JOptionPane.showConfirmDialog(parent, "Möchten Sie die Buchung wirklich löschen ?") == JOptionPane.YES_OPTION) {
                        EntityTools.delete(buchung);
                        reloadBuchungTable();
//                        TMResultSet tm = (TMResultSet) tblVorrat.getModel();
//                        tm.reload(tblVorrat.getSelectedRow(), tblVorrat.getSelectedColumn());

                    }


                }
            });
            menuBuch.add(itemPopupDelete);


            JMenuItem itemPopupReset = new JMenuItem("Alle Buchungen zurücksetzen.");
            itemPopupReset.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    resetBuchung();
                }
            });
            menuBuch.add(itemPopupReset);
            menuBuch.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }


    }//GEN-LAST:event_tblBuchungMousePressed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void jspBestandMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jspBestandMousePressed
        if (!evt.isPopupTrigger() || vorrat == null) {
            return;
        }
        Point p = evt.getPoint();
        SYSTools.unregisterListeners(menuV);
        menuV = new JPopupMenu();

        JMenuItem itemPopupNew = new JMenuItem("Neu");
        itemPopupNew.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBestand();
            }
        });
        menuV.add(itemPopupNew);
        menuV.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }//GEN-LAST:event_jspBestandMousePressed

    private void tblBestandMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBestandMousePressed
        final TMBestand tm = (TMBestand) tblBestand.getModel();
        if (tm.getRowCount() == 0) {
            bestand = null;
            return;
        }

        Point p = evt.getPoint();
        final int col = tblBestand.columnAtPoint(p);
        final int row = tblBestand.rowAtPoint(p);
        ListSelectionModel lsm = tblBestand.getSelectionModel();
        lsm.setSelectionInterval(row, row);

        bestand = tm.getBestand(row);
        reloadBuchungTable();

        // Menüeinträge
        if (evt.isPopupTrigger()) {

            SYSTools.unregisterListeners(menuV);
            menuV = new JPopupMenu();

            JMenuItem itemPopupNew = new JMenuItem("Neu");
            itemPopupNew.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    newBestand();
                }
            });
            menuV.add(itemPopupNew);

//            JMenuItem itemPopupEdit = new JMenuItem("Bearbeiten");
//            itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                }
//            });
//            menuV.add(itemPopupEdit);
//
            JMenuItem itemPopupDelete = new JMenuItem("Löschen");
            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (JOptionPane.showConfirmDialog(parent, "Möchten Sie den Bestand wirklich löschen ?") == JOptionPane.YES_OPTION) {
                        EntityTools.delete(bestand);
                        reloadBestandTable();
                    }
                }
            });
            menuV.add(itemPopupDelete);
            // ----------------
            JMenuItem itemPopupPrint = new JMenuItem("Beleg drucken");
            itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    //SYSPrint.printLabel(mybestid);
                }
            });
            menuV.add(itemPopupPrint);
            // ----------------
            JMenuItem itemPopupClose = new JMenuItem("Bestand abschließen");
            itemPopupClose.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (JOptionPane.showConfirmDialog(thisDialog, "Sind sie sicher ?", "Bestand abschließen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            boolean apvNeuberechnung = true;
                            MedBestandTools.abschliessen(em, bestand, "", !apvNeuberechnung, MedBuchungenTools.STATUS_KORREKTUR_MANUELL);
                            em.getTransaction().commit();
                        } catch (Exception e) {
                            em.getTransaction().rollback();
                            OPDE.fatal(e);
                        } finally {
                            em.close();
                        }
                        reloadVorratTable();
                    }
                }
            });
            itemPopupClose.setEnabled(bestand.isAngebrochen());
            menuV.add(itemPopupClose);

            // ---------------
            JMenuItem itemPopupEinbuchen = new JMenuItem("Bestand wieder aktivieren");
            itemPopupEinbuchen.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    bestand.setAus(SYSConst.DATE_BIS_AUF_WEITERES);
                    bestand = EntityTools.merge(bestand);
                    reloadBestandTable();
                }
            });
            itemPopupEinbuchen.setEnabled(bestand.isAbgeschlossen());
            menuV.add(itemPopupEinbuchen);

            // ---------------
            JMenuItem itemPopupAnbruch = new JMenuItem("Bestand anbrechen");
            itemPopupAnbruch.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    bestand = MedBestandTools.anbrechen(bestand);
                    reloadBestandTable();
                }
            });
            //itemPopupAnbruch.setEnabled(!op.care.med.DBHandling.hasAnbruch(vorid)); // Nur an anbrechen lassen, wenn noch keine im Anbruch ist.
            menuV.add(itemPopupAnbruch);

            // ---------------
            JMenuItem itemPopupVerschließen = new JMenuItem("Bestand wieder verschließen");
            itemPopupVerschließen.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    bestand.setAus(SYSConst.DATE_BIS_AUF_WEITERES);
                    bestand.setAnbruch(SYSConst.DATE_BIS_AUF_WEITERES);
                    bestand = EntityTools.merge(bestand);
                    reloadBestandTable();
                }
            });
            itemPopupVerschließen.setEnabled(bestand.isAngebrochen());
            menuV.add(itemPopupVerschließen);

            menuV.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }//GEN-LAST:event_tblBestandMousePressed

    private void tblVorratMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVorratMousePressed
        final TMVorraete tm = (TMVorraete) tblVorrat.getModel();
        if (tm.getRowCount() == 0) {
            vorrat = null;
            return;
        }

        Point p = evt.getPoint();
        final int col = tblVorrat.columnAtPoint(p);
        final int row = tblVorrat.rowAtPoint(p);
        ListSelectionModel lsm = tblVorrat.getSelectionModel();
        lsm.setSelectionInterval(row, row);

        vorrat = tm.getVorrat(row);
        reloadBestandTable();

        if (evt.isPopupTrigger()) {
            // Menüeinträge
            SYSTools.unregisterListeners(menuV);
            menuV = new JPopupMenu();

            JMenuItem itemPopupNew = new JMenuItem("Neu");
            itemPopupNew.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    newVorrat();
                }
            });
            menuV.add(itemPopupNew);

            JMenuItem itemPopupDelete = new JMenuItem("Vorrat löschen");
            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (JOptionPane.showConfirmDialog(parent, "Sind sie sicher ?", "Vorrat löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        if (JOptionPane.showConfirmDialog(parent, "Wirklich ?", "Vorrat löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            EntityTools.delete(vorrat);
                        }
                    }
                    reloadVorratTable();
                }
            });
            menuV.add(itemPopupDelete);

            JMenuItem itemPopupClose = new JMenuItem("Vorrat abschließen und ausbuchen");
            itemPopupClose.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (JOptionPane.showConfirmDialog(parent, "Sind sie sicher ?", "Vorrat abschließen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        MedVorratTools.abschliessen(vorrat);
                    }
                    reloadVorratTable();
                }
            });
            menuV.add(itemPopupClose);

            menuV.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }//GEN-LAST:event_tblVorratMousePressed

    private void jspVorratMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jspVorratMousePressed
        if (!evt.isPopupTrigger()) {
            return;
        }
        Point p = evt.getPoint();
        SYSTools.unregisterListeners(menuV);
        menuV = new JPopupMenu();

        JMenuItem itemPopupNew = new JMenuItem("Neu");
        itemPopupNew.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newVorrat();
            }
        });
        menuV.add(itemPopupNew);

        menuV.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }//GEN-LAST:event_jspVorratMousePressed

    /**
     * Diese Methode legt einen neuen Vorrat in der Tabelle MPVorrat an. Ein
     * Vorrat braucht nur eine allgemeine Bezeichnung zu haben.
     */
    private void newVorrat() {
        String neuerVorrat = JOptionPane.showInputDialog(this, "Bitte geben Sie die Bezeichnung für den neuen Vorrat ein.");

        if (!SYSTools.catchNull(neuerVorrat).isEmpty()) {
            MedVorrat vorrat = new MedVorrat(bewohner, neuerVorrat);
            EntityTools.persist(vorrat);
            reloadVorratTable();
        }
    }


    /**
     * Löscht einen bestimmten Bestand und die zugehörigen Buchungen.
     */
//    private void closeBestand(long bestid){
//        if (JOptionPane.showConfirmDialog(this, "Möchten Sie den Bestand wirklich abschließen ?") == JOptionPane.YES_OPTION){
//
//            HashMap hm = new HashMap();
//            hm.put("BestID", bestid);
//            hm.put("BHPID", 0);
//            hm.put("Menge", op.care.med.DBHandling.getBestandSumme(bestid) * -1);
//            hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//            hm.put("PIT", "!NOW!");
//            op.tools.DBHandling.insertRecord("MPBuchung",hm);
//
//            HashMap hm2 = new HashMap();
//            hm2.put("Aus","!NOW!");
//            op.tools.DBHandling.updateRecord("MPBestand",hm2,"BestID",bestid);
//            hm2.clear();
//
//            reloadBestandTable();
//        }
//    }


    /**
     * Öffnet den Dialog DlgEditBestand.
     */
    private void newBestand() {
//        new DlgEditBestand(this, vorid);
//        reloadBestandTable();
    }

    /**
     * Öffnet den Dialog DlgEditBuchung
     */
    private void newBuchung() {
//        new DlgEditBuchung(this, bestid);
//        reloadBuchungTable();
//        TMResultSet tm = (TMResultSet) tblVorrat.getModel();
//        tm.reload(tblVorrat.getSelectedRow(), tblVorrat.getSelectedColumn());

    }

    private void resetBuchung() {
        if (JOptionPane.showConfirmDialog(this, "Sind Sie sicher ?") == JOptionPane.YES_OPTION) {
            MedBestandTools.zuruecksetzen(bestand);
            reloadVorratTable();
        }
    }

    private void reloadVorratTable() {
        if (bewohner != null) {
//            String sql = "SELECT DISTINCT v.VorID, v.Text 'Name des Vorrats', ifnull(b.saldo, 0.00) Bestandsmenge" +
//                    " FROM MPVorrat v " +
//                    " LEFT OUTER JOIN (" +
//                    "   SELECT best.VorID, sum(buch.Menge) saldo FROM MPBestand best " +
//                    "   INNER JOIN MPVorrat v ON v.VorID = best.VorID " +
//                    "   INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
//                    "   WHERE v.BWKennung=? " + // Diese Zeile ist eigentlich nicht nötig. Beschleunigt aber ungemein.
//                    "   GROUP BY best.VorID" +
//                    " ) b ON b.VorID = v.VorID" +
//                    " WHERE v.BWKennung=? " +
//                    (cbClosedVorrat.isSelected() ? "" : " AND v.Bis = '9999-12-31 23:59:59' ") +
//                    " ORDER BY v.Text ";

            EntityManager em = OPDE.createEM();
            Query query = em.createNamedQuery("MedVorrat.findVorraeteMitSummen");
            query.setParameter(1, bewohner.getBWKennung());
            query.setParameter(2, bewohner.getBWKennung());
            query.setParameter(3, cbClosedVorrat.isSelected());

            tblVorrat.setModel(new TMVorraete(query.getResultList()));
            tblVorrat.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

            em.close();

//            for (int i = 0; i < tblVorrat.getModel().getColumnCount(); i++) {
//                tblVorrat.getColumnModel().getColumn(i).setCellRenderer(new RNDStandard());
//            }

        } else {
            tblVorrat.setModel(new DefaultTableModel());
        }
        ListSelectionModel lsm1 = tblBestand.getSelectionModel();
        tblBestand.setModel(new DefaultTableModel());
        tblBuchung.setModel(new DefaultTableModel());
    }

    private void reloadBestandTable() {
        if (vorrat == null) {
            tblBestand.setModel(new DefaultTableModel());
        } else {

            EntityManager em = OPDE.createEM();
            Query query = em.createNamedQuery("MedBestand.findByVorratMitRestsumme");
            query.setParameter(1, vorrat.getVorID());
            query.setParameter(2, vorrat.getVorID());
            query.setParameter(3, cbClosedBestand.isSelected());

            tblBestand.setModel(new TMBestand(query.getResultList()));
            tblBestand.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

            em.close();

            for (int i = 0; i < tblBestand.getModel().getColumnCount(); i++) {
                tblBestand.getColumnModel().getColumn(i).setCellRenderer(new RNDHTML());
            }

            tblBuchung.setModel(new DefaultTableModel());
        }
        bestand = null;
        reloadBuchungTable();
    }

    private void reloadBuchungTable() {
        if (bestand == null) {
            tblBuchung.setModel(new DefaultTableModel());
        } else {
            EntityManager em = OPDE.createEM();
            Query query = em.createNamedQuery("MedBuchungen.findByBestand");
            query.setParameter("bestand", bestand);

            tblBuchung.setModel(new BeanTableModel<MedBuchungen>(MedBuchungen.class, query.getResultList()));
            tblBestand.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            em.close();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel lblFrage;
    private JLabel lblBW;
    private JPanel jPanel2;
    private JScrollPane jspVorrat;
    private JTable tblVorrat;
    private JCheckBox cbClosedVorrat;
    private JPanel jPanel3;
    private JScrollPane jspBestand;
    private JTable tblBestand;
    private JCheckBox cbClosedBestand;
    private JButton btnClose;
    private JPanel pnl123;
    private JScrollPane jspBuchung;
    private JTable tblBuchung;
    private JPanel jPanel1;
    private JTextField txtSuche;
    private JComboBox cmbBW;
    private JSeparator jSeparator1;
    private JToolBar jToolBar1;
    private JButton btnBestandsliste;
    // End of variables declaration//GEN-END:variables

}
