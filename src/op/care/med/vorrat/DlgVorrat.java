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

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Bewohner;
import entity.BewohnerTools;
import entity.EntityTools;
import entity.verordnungen.*;
import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;
import tablemodels.TMBestand;
import tablemodels.TMBuchungen;
import tablemodels.TMVorraete;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;

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
    private JDialog thisComponent;

    /**
     * Creates new form DlgVorrat
     */
    public DlgVorrat(JDialog parent, Bewohner bewohner) {
        super(parent, true);
        this.parent = parent;
        this.bewohner = bewohner;
        thisComponent = this;
        initDialog();
    }

    public DlgVorrat(JFrame parent, Bewohner bewohner) {
        super(parent, true);
        this.parent = parent;
        this.bewohner = bewohner;
        thisComponent = this;
        initDialog();
    }

    public DlgVorrat(JDialog parent) {
        super(parent, true);
        this.parent = parent;
        this.bewohner = null;
        thisComponent = this;
        initDialog();
    }

    public DlgVorrat(JFrame parent) {
        super(parent, true);
        this.parent = parent;
        this.bewohner = null;
        thisComponent = this;
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

    private void jspVorratComponentResized(ComponentEvent e) {
        JScrollPane jsp = (JScrollPane) e.getComponent();
        Dimension dim = jsp.getSize();

        TableColumnModel tcm1 = tblVorrat.getColumnModel();

        if (tcm1.getColumnCount() == 0) {
            return;
        }

        tcm1.getColumn(TMVorraete.COL_NAME).setPreferredWidth(dim.width / 4 * 3);
        tcm1.getColumn(TMVorraete.COL_MENGE).setPreferredWidth(dim.width / 4);
        tcm1.getColumn(TMVorraete.COL_NAME).setHeaderValue("Vorratsbezeichnung");
        tcm1.getColumn(TMVorraete.COL_MENGE).setHeaderValue("Gesamtsumme");
    }

    private void jspBuchungComponentResized(ComponentEvent e) {

        TableColumnModel tcm1 = tblBuchung.getColumnModel();

        if (tcm1.getColumnCount() == 0) {
            return;
        }

        tcm1.getColumn(TMBuchungen.COL_Datum).setHeaderValue("Datum");
        tcm1.getColumn(TMBuchungen.COL_Text).setHeaderValue("Text");
        tcm1.getColumn(TMBuchungen.COL_Menge).setHeaderValue("Menge");
        tcm1.getColumn(TMBuchungen.COL_User).setHeaderValue("MitarbeiterIn");

        SYSTools.packTable(tblBuchung, 2);
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
        tblVorrat.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    MedVorrat myVorrat = ((TMVorraete) tblVorrat.getModel()).getVorrat(tblVorrat.getSelectedRow());
                    if (!myVorrat.equals(vorrat)){
                        vorrat = myVorrat;
                        reloadBestandTable();
                    }

                }
            }
        });
        tblBuchung.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    MedBestand myBestand = ((TMBestand) tblBestand.getModel()).getBestand(tblBestand.getSelectedRow());
                    if (!myBestand.equals(bestand)){
                        bestand = myBestand;
                        reloadBuchungTable();
                    }

                }
            }
        });
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
        jToolBar1 = new JToolBar();
        btnBestandsliste = new JButton();
        lblFrage = new JLabel();
        lblBW = new JLabel();
        jPanel2 = new JPanel();
        cbClosedVorrat = new JCheckBox();
        jspVorrat = new JScrollPane();
        tblVorrat = new JTable();
        jPanel3 = new JPanel();
        cbClosedBestand = new JCheckBox();
        jspBestand = new JScrollPane();
        tblBestand = new JTable();
        btnClose = new JButton();
        pnl123 = new JPanel();
        jspBuchung = new JScrollPane();
        tblBuchung = new JTable();
        jPanel1 = new JPanel();
        txtSuche = new JTextField();
        cmbBW = new JComboBox();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$rgap, $lcgap, 308dlu:grow, $lcgap, default:grow(0.5), $lcgap, $rgap",
            "fill:default, $rgap, 2*(fill:default, $lgap), fill:60dlu, $lgap, fill:215dlu:grow, $lgap, fill:default:grow, $lgap, fill:default, $lgap, $rgap"));

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
        contentPane.add(jToolBar1, CC.xywh(3, 1, 3, 1, CC.FILL, CC.DEFAULT));

        //---- lblFrage ----
        lblFrage.setFont(new Font("Dialog", Font.BOLD, 24));
        lblFrage.setText("Medikamenten Vorrat");
        contentPane.add(lblFrage, CC.xywh(3, 3, 3, 1, CC.FILL, CC.DEFAULT));

        //---- lblBW ----
        lblBW.setFont(new Font("Dialog", Font.BOLD, 18));
        lblBW.setForeground(new Color(255, 51, 0));
        lblBW.setText("Nachname, Vorname (*GebDatum, 00 Jahre) [??1]");
        contentPane.add(lblBW, CC.xywh(3, 5, 3, 1, CC.FILL, CC.DEFAULT));

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("Vorr\u00e4te"));
            jPanel2.setLayout(new BoxLayout(jPanel2, BoxLayout.PAGE_AXIS));

            //---- cbClosedVorrat ----
            cbClosedVorrat.setText("Abgeschlossene anzeigen");
            cbClosedVorrat.setBorder(BorderFactory.createEmptyBorder());
            cbClosedVorrat.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cbClosedVorratItemStateChanged(e);
                }
            });
            jPanel2.add(cbClosedVorrat);

            //======== jspVorrat ========
            {
                jspVorrat.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        jspVorratMousePressed(e);
                    }
                });
                jspVorrat.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        jspVorratComponentResized(e);
                    }
                });

                //---- tblVorrat ----
                tblVorrat.setModel(new DefaultTableModel(
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
                tblVorrat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                tblVorrat.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        tblVorratMousePressed(e);
                    }
                });
                jspVorrat.setViewportView(tblVorrat);
            }
            jPanel2.add(jspVorrat);
        }
        contentPane.add(jPanel2, CC.xy(3, 9));

        //======== jPanel3 ========
        {
            jPanel3.setBorder(new TitledBorder("Best\u00e4nde"));
            jPanel3.setLayout(new BoxLayout(jPanel3, BoxLayout.PAGE_AXIS));

            //---- cbClosedBestand ----
            cbClosedBestand.setText("Abgeschlossene anzeigen");
            cbClosedBestand.setBorder(BorderFactory.createEmptyBorder());
            cbClosedBestand.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cbClosedBestandItemStateChanged(e);
                }
            });
            jPanel3.add(cbClosedBestand);

            //======== jspBestand ========
            {
                jspBestand.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        jspBestandComponentResized(e);
                    }
                });
                jspBestand.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        jspBestandMousePressed(e);
                    }
                });

                //---- tblBestand ----
                tblBestand.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                tblBestand.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        tblBestandMousePressed(e);
                    }
                });
                jspBestand.setViewportView(tblBestand);
            }
            jPanel3.add(jspBestand);
        }
        contentPane.add(jPanel3, CC.xy(3, 11));

        //---- btnClose ----
        btnClose.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
        btnClose.setText("Schlie\u00dfen");
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCloseActionPerformed(e);
            }
        });
        contentPane.add(btnClose, CC.xywh(3, 13, 3, 1));

        //======== pnl123 ========
        {
            pnl123.setBorder(new TitledBorder("Buchungen"));
            pnl123.setLayout(new BoxLayout(pnl123, BoxLayout.PAGE_AXIS));

            //======== jspBuchung ========
            {
                jspBuchung.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        jspBuchungComponentResized(e);
                    }
                });

                //---- tblBuchung ----
                tblBuchung.setModel(new DefaultTableModel(4, 0));
                tblBuchung.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                tblBuchung.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        tblBuchungMousePressed(e);
                    }
                });
                jspBuchung.setViewportView(tblBuchung);
            }
            pnl123.add(jspBuchung);
        }
        contentPane.add(pnl123, CC.xywh(5, 9, 1, 3));

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Suche"));
            jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.PAGE_AXIS));

            //---- txtSuche ----
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
            jPanel1.add(txtSuche);

            //---- cmbBW ----
            cmbBW.setModel(new DefaultComboBoxModel(new String[] {

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
            jPanel1.add(cmbBW);
        }
        contentPane.add(jPanel1, CC.xywh(3, 7, 3, 1));
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
                    }
                }
                if (bewohner == null) {
                    EntityManager em = OPDE.createEM();
                    Query query = em.createNamedQuery("Bewohner.findByNachname");
                    query.setParameter("nachname", "%" + txtSuche.getText() + "%");
                    cmbBW.setModel(new DefaultComboBoxModel(query.getResultList().toArray(new Bewohner[]{})));
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

        final TMBuchungen tm = (TMBuchungen) tblBuchung.getModel();
        if (tm.getRowCount() == 0) {
            bestand = null;
            return;
        }

        Point p = evt.getPoint();
        final int row = tblBuchung.rowAtPoint(p);
        ListSelectionModel lsm = tblBuchung.getSelectionModel();
        lsm.setSelectionInterval(row, row);

        final MedBuchungen buchung = tm.getData().get(row);

        if (evt.isPopupTrigger()) {

            SYSTools.unregisterListeners(menuBuch);
            menuBuch = new JPopupMenu();

            JMenuItem itemPopupNew = new JMenuItem("Neu");
            itemPopupNew.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    DlgEditBuchung dlg = new DlgEditBuchung(thisComponent, bestand);
                    reloadBuchungTable();
                    refreshBothTables();
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
                        refreshBothTables();
                    }
                }
            });
            menuBuch.add(itemPopupDelete);


            JMenuItem itemPopupReset = new JMenuItem("Alle Buchungen zurücksetzen.");
            itemPopupReset.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (JOptionPane.showConfirmDialog(thisComponent, "Sind Sie sicher ?") == JOptionPane.YES_OPTION) {
                        MedBestandTools.zuruecksetzen(bestand);
                        reloadBuchungTable();
                        refreshBothTables();
                    }
                }
            });
            menuBuch.add(itemPopupReset);
            menuBuch.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }


    }//GEN-LAST:event_tblBuchungMousePressed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed


    private void refreshBothTables() {
        // Tabellen aktualisieren ohne sie komplett neu zu laden.
        BigDecimal bestandSumme = MedBestandTools.getBestandSumme(bestand);
        int bestandRow = tblBestand.getSelectedRow();
        ((TMBestand) tblBestand.getModel()).getData().get(bestandRow)[1] = bestandSumme;
        ((TMBestand) tblBestand.getModel()).fireTableCellUpdated(bestandRow, TMBestand.COL_MENGE);

        BigDecimal vorratSumme = MedVorratTools.getSumme(vorrat);
        int vorratRow = tblVorrat.getSelectedRow();// ((TMVorraete) tblVorrat.getModel()).findPositionOf(vorrat);
        ((TMVorraete) tblVorrat.getModel()).getData().get(vorratRow)[1] = vorratSumme;
        ((TMVorraete) tblVorrat.getModel()).fireTableCellUpdated(vorratRow, TMVorraete.COL_MENGE);
    }

    private void jspBestandMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jspBestandMousePressed
//        if (!evt.isPopupTrigger() || vorrat == null) {
//            return;
//        }
//        Point p = evt.getPoint();
//        SYSTools.unregisterListeners(menuV);
//        menuV = new JPopupMenu();
//
//        JMenuItem itemPopupNew = new JMenuItem("Neu");
//        itemPopupNew.addActionListener(new java.awt.event.ActionListener() {
//
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                newBestand();
//            }
//        });
//        menuV.add(itemPopupNew);
//        menuV.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
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


        bestand = tm.getBestand(row);
        reloadBuchungTable();

        // Menüeinträge
        if (evt.isPopupTrigger()) {

            ListSelectionModel lsm = tblBestand.getSelectionModel();
            lsm.setSelectionInterval(row, row);

            SYSTools.unregisterListeners(menuV);
            menuV = new JPopupMenu();

//            JMenuItem itemPopupNew = new JMenuItem("Neu");
//            itemPopupNew.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    newBestand();
//                }
//            });
//            menuV.add(itemPopupNew);

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

                            MedBestandTools.abschliessen(em, bestand, "", MedBuchungenTools.STATUS_KORREKTUR_MANUELL);
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
            itemPopupAnbruch.setEnabled(!bestand.isAbgeschlossen() && !bestand.isAngebrochen());
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
            itemPopupVerschließen.setEnabled(!bestand.isAbgeschlossen() && bestand.isAngebrochen());
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
//        if (!evt.isPopupTrigger()) {
//            return;
//        }
//        Point p = evt.getPoint();
//        SYSTools.unregisterListeners(menuV);
//        menuV = new JPopupMenu();
//
//        JMenuItem itemPopupNew = new JMenuItem("Neu");
//        itemPopupNew.addActionListener(new java.awt.event.ActionListener() {
//
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                newVorrat();
//            }
//        });
//        menuV.add(itemPopupNew);
//
//        menuV.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
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


    private void reloadVorratTable() {
        if (bewohner != null) {

            EntityManager em = OPDE.createEM();
            Query query = em.createNamedQuery("MedVorrat.findVorraeteMitSummen");
            query.setParameter(1, bewohner.getBWKennung());
            query.setParameter(2, bewohner.getBWKennung());
            query.setParameter(3, cbClosedVorrat.isSelected());

            tblVorrat.setModel(new TMVorraete(query.getResultList()));
            tblVorrat.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

            jspVorrat.dispatchEvent(new ComponentEvent(jspVorrat, ComponentEvent.COMPONENT_RESIZED));

            em.close();

        } else {
            tblVorrat.setModel(new DefaultTableModel());
        }

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

            jspBestand.dispatchEvent(new ComponentEvent(jspBestand, ComponentEvent.COMPONENT_RESIZED));

            em.close();

            for (int i = 0; i < tblBestand.getModel().getColumnCount(); i++) {
                tblBestand.getColumnModel().getColumn(i).setCellRenderer(new RNDHTML());
            }
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

            tblBuchung.setModel(new TMBuchungen(query.getResultList()));
            tblBuchung.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            jspBuchung.dispatchEvent(new ComponentEvent(jspBuchung, ComponentEvent.COMPONENT_RESIZED));

            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(SwingConstants.RIGHT);
            DefaultTableCellRenderer renderer2 = new DefaultTableCellRenderer();
            renderer2.setHorizontalAlignment(SwingConstants.CENTER);

            tblBuchung.getColumnModel().getColumn(TMBuchungen.COL_Menge).setCellRenderer(renderer);
            tblBuchung.getColumnModel().getColumn(TMBuchungen.COL_Text).setCellRenderer(renderer2);
            em.close();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JToolBar jToolBar1;
    private JButton btnBestandsliste;
    private JLabel lblFrage;
    private JLabel lblBW;
    private JPanel jPanel2;
    private JCheckBox cbClosedVorrat;
    private JScrollPane jspVorrat;
    private JTable tblVorrat;
    private JPanel jPanel3;
    private JCheckBox cbClosedBestand;
    private JScrollPane jspBestand;
    private JTable tblBestand;
    private JButton btnClose;
    private JPanel pnl123;
    private JScrollPane jspBuchung;
    private JTable tblBuchung;
    private JPanel jPanel1;
    private JTextField txtSuche;
    private JComboBox cmbBW;
    // End of variables declaration//GEN-END:variables

}
