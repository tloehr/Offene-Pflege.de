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
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.Bewohner;
import entity.BewohnerTools;
import entity.EntityTools;
import entity.system.SYSPropsTools;
import entity.verordnungen.*;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.*;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;
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
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

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
public class PnlVorrat extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.vorrat";

    private Bewohner bewohner;
    //    private Component parent;
    private Component thisDialog;
    private JPopupMenu menuV;
    //    private JPopupMenu menuB;
    private JPopupMenu menuBuch;

    //    private OCSec ocs;
    private MedVorrat vorrat;
    private MedBestand bestand;
//    private JDialog thisComponent;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JCheckBox cbClosedBestand, cbClosedVorrat;

    /**
     * Creates new form DlgVorrat
     */
    public PnlVorrat(Bewohner bewohner, JScrollPane jspSearch) {
        super();
        this.jspSearch = jspSearch;
        initComponents();
        initDialog();
        change2Bewohner(bewohner);
    }

    private void cmbBWPropertyChange(PropertyChangeEvent e) {
        OPDE.debug("MODEL");
//        cmbBWItemStateChanged(null);
    }

    @Override
    public void change2Bewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
        OPDE.getDisplayManager().setMainMessage(BewohnerTools.getBWLabelText(bewohner));
        reloadVorratTable();
    }

    @Override
    public void cleanup() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);


        searchPanes.add(addCommands());
        searchPanes.add(addFilter());

        searchPanes.addExpansion();

    }

    private CollapsiblePane addFilter() {

        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setLayout(new VerticalLayout());

        CollapsiblePane panelFilter = new CollapsiblePane("Suchen");
        panelFilter.setStyle(CollapsiblePane.PLAIN_STYLE);
        panelFilter.setCollapsible(false);

        JXSearchField search = new JXSearchField("Bestandsnummer");
        search.setFont(new Font("Arial", Font.PLAIN, 14));
        search.setFocusBehavior(org.jdesktop.swingx.prompt.PromptSupport.FocusBehavior.HIGHLIGHT_PROMPT);
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSucheActionPerformed(e);
            }
        });
        search.setInstantSearchDelay(5000);
        labelPanel.add(search);

        cbClosedVorrat = new JCheckBox("Geschlossene Vorräte anzeigen");
        cbClosedVorrat.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
        SYSPropsTools.restoreState(internalClassID + ":cbClosedVorrat", cbClosedVorrat);
        cbClosedVorrat.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SYSPropsTools.storeState(internalClassID + ":cbClosedVorrat", cbClosedVorrat);
                reloadVorratTable();
            }
        });
        cbClosedVorrat.setBackground(Color.WHITE);
        labelPanel.add(cbClosedVorrat);

        cbClosedBestand = new JCheckBox("Geschlossene Bestände anzeigen");
        cbClosedBestand.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
        SYSPropsTools.restoreState(internalClassID + ":cbClosedBestand", cbClosedBestand);
        cbClosedBestand.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SYSPropsTools.storeState(internalClassID + ":cbClosedBestand", cbClosedBestand);
                reloadVorratTable();
            }
        });
        cbClosedBestand.setBackground(Color.WHITE);
        labelPanel.add(cbClosedBestand);


        panelFilter.setContentPane(labelPanel);

        return panelFilter;
    }

    private CollapsiblePane addCommands() {
        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane("Medikamenten-Vorrat");
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            JideButton addButton = GUITools.createHyperlinkButton("Bei Bedarf", new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                }
            });
            mypanel.add(addButton);
        }

        searchPane.setContentPane(mypanel);
        return searchPane;
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
//        setTitle(SYSTools.getWindowTitle("Medikamentenvorrat"));
//        thisDialog = this;


//        txtSuche.setEnabled(bewohner == null);
//        cmbBW.setEnabled(bewohner == null);
//        if (bewohner == null) {
//            txtSuche.requestFocus();
//            lblBW.setText("");
//        } else {
//            BewohnerTools.setBWLabel(lblBW, bewohner);
//        }

        prepareSearchArea();

        tblVorrat.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    if (tblVorrat.getSelectedRowCount() > 0) {
                        MedVorrat myVorrat = ((TMVorraete) tblVorrat.getModel()).getVorrat(tblVorrat.getSelectedRow());
                        if (!myVorrat.equals(vorrat)) {
                            vorrat = myVorrat;
                            reloadBestandTable();
                        }
                    }
                }
            }
        });
        tblBuchung.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    if (tblBestand.getSelectedRowCount() > 0) {
                        MedBestand myBestand = ((TMBestand) tblBestand.getModel()).getBestand(tblBestand.getSelectedRow());
                        if (!myBestand.equals(bestand)) {
                            bestand = myBestand;
                            reloadBuchungTable();
                        }
                    }

                }
            }
        });
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel2 = new JPanel();
        jspVorrat = new JScrollPane();
        tblVorrat = new JTable();
        jPanel3 = new JPanel();
        jspBestand = new JScrollPane();
        tblBestand = new JTable();
        pnl123 = new JPanel();
        jspBuchung = new JScrollPane();
        tblBuchung = new JTable();

        //======== this ========
        setLayout(new FormLayout(
                "308dlu:grow, $lcgap, default:grow(0.5)",
                "fill:215dlu:grow, $lgap, fill:default:grow"));

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("Vorr\u00e4te"));
            jPanel2.setLayout(new BoxLayout(jPanel2, BoxLayout.PAGE_AXIS));

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
                tblVorrat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                tblVorrat.setFont(new Font("Arial", Font.PLAIN, 14));
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
        add(jPanel2, CC.xy(1, 1));

        //======== jPanel3 ========
        {
            jPanel3.setBorder(new TitledBorder("Best\u00e4nde"));
            jPanel3.setLayout(new BoxLayout(jPanel3, BoxLayout.PAGE_AXIS));

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
                tblBestand.setFont(new Font("Arial", Font.PLAIN, 14));
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
        add(jPanel3, CC.xy(1, 3));

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
                tblBuchung.setFont(new Font("Arial", Font.PLAIN, 14));
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
        add(pnl123, CC.xywh(3, 1, 1, 3));
    }// </editor-fold>//GEN-END:initComponents

    private void cbClosedBestandItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbClosedBestandItemStateChanged
        reloadBestandTable();
    }//GEN-LAST:event_cbClosedBestandItemStateChanged

    private void cbClosedVorratItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbClosedVorratItemStateChanged
        reloadVorratTable();
    }//GEN-LAST:event_cbClosedVorratItemStateChanged

//    private void printBestand() {
////        if (!bwkennung.equals("") ||
////                JOptionPane.showConfirmDialog(this, "Es wurde kein Bewohner ausgewählt.\nMöchten Sie wirklich eine Gesamtliste ausdrucken ?", "Frage", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
////
////            HashMap params = new HashMap();
////            JRDSMedBestand jrds = new JRDSMedBestand(bwkennung);
////
////            SYSPrint.printReport(preview, jrds, params, "medbestand", dialog);
////            if (!preview && !dialog) {
////                JOptionPane.showMessageDialog(this, "Der Druckvorgang ist abgeschlossen.", "Drucker", JOptionPane.INFORMATION_MESSAGE);
////            }
////        }
//    }

    //    private void cmbBWItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbBWItemStateChanged
//        if (cmbBW.getModel().getSize() > 0) {
//            bewohner = (Bewohner) cmbBW.getSelectedItem();
//            reloadVorratTable();
//        }
//    }//GEN-LAST:event_cmbBWItemStateChanged
//
//
    private void txtSucheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSucheActionPerformed
        JXSearchField search = (JXSearchField) evt.getSource();
        if (!search.getText().isEmpty() && search.getText().matches("\\d*")) {
            // Nur Zahlen.. Das ist eine BestID
            long bestid = Long.parseLong(search.getText());
            EntityManager em = OPDE.createEM();
            bestand = em.find(MedBestand.class, bestid);
            em.close();

            if (bestand != null) {

                if (!bewohner.equals(bestand.getVorrat().getBewohner())) {
                    this.bewohner = bestand.getVorrat().getBewohner();
                    OPDE.getDisplayManager().setMainMessage(BewohnerTools.getBWLabelText(bewohner));
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Medikament gehört eine[m|r] anderen Bewohner[in]. Habe umgeschaltet.", 2));
                }

                reloadVorratTable(bestand.getVorrat());

            } else {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Der eingegebene Bestand existiert nicht.", 2));
            }

        }
    }//GEN-LAST:event_txtSucheActionPerformed

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
//                    DlgEditBuchung dlg = new DlgEditBuchung(thisComponent, bestand);
//                    reloadBuchungTable();
//                    refreshBothTables();
                }
            });
            menuBuch.add(itemPopupNew);

            // Menüeinträge

            JMenuItem itemPopupDelete = new JMenuItem("Löschen");
            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {

//                    if (JOptionPane.showConfirmDialog(parent, "Möchten Sie die Buchung wirklich löschen ?") == JOptionPane.YES_OPTION) {
//                        EntityTools.delete(buchung);
//                        reloadBuchungTable();
//                        refreshBothTables();
//                    }
                }
            });
            menuBuch.add(itemPopupDelete);


            JMenuItem itemPopupReset = new JMenuItem("Alle Buchungen zurücksetzen.");
            itemPopupReset.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    if (JOptionPane.showConfirmDialog(thisComponent, "Sind Sie sicher ?") == JOptionPane.YES_OPTION) {
//                        MedBestandTools.zuruecksetzen(bestand);
//                        reloadBuchungTable();
//                        refreshBothTables();
//                    }
                }
            });
            menuBuch.add(itemPopupReset);
            menuBuch.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }


    }//GEN-LAST:event_tblBuchungMousePressed


//    private void refreshBothTables() {
//        // Tabellen aktualisieren ohne sie komplett neu zu laden.
//        BigDecimal bestandSumme = MedBestandTools.getBestandSumme(bestand);
//        int bestandRow = tblBestand.getSelectedRow();
//        ((TMBestand) tblBestand.getModel()).getData().get(bestandRow)[1] = bestandSumme;
//        ((TMBestand) tblBestand.getModel()).fireTableCellUpdated(bestandRow, TMBestand.COL_MENGE);
//
//        BigDecimal vorratSumme = MedVorratTools.getSumme(vorrat);
//        int vorratRow = tblVorrat.getSelectedRow();// ((TMVorraete) tblVorrat.getModel()).findPositionOf(vorrat);
//        ((TMVorraete) tblVorrat.getModel()).getData().get(vorratRow). = vorratSumme;
//        ((TMVorraete) tblVorrat.getModel()).fireTableCellUpdated(vorratRow, TMVorraete.COL_MENGE);
//    }

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
//                    if (JOptionPane.showConfirmDialog(parent, "Möchten Sie den Bestand wirklich löschen ?") == JOptionPane.YES_OPTION) {
//                        EntityTools.delete(bestand);
//                        reloadBestandTable();
//                    }
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
//                    if (JOptionPane.showConfirmDialog(parent, "Sind sie sicher ?", "Vorrat löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//                        if (JOptionPane.showConfirmDialog(parent, "Wirklich ?", "Vorrat löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//                            EntityTools.delete(vorrat);
//                        }
//                    }
                    reloadVorratTable();
                }
            });
            menuV.add(itemPopupDelete);

            JMenuItem itemPopupClose = new JMenuItem("Vorrat abschließen und ausbuchen");
            itemPopupClose.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    if (JOptionPane.showConfirmDialog(parent, "Sind sie sicher ?", "Vorrat abschließen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//                        MedVorratTools.abschliessen(vorrat);
//                    }
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
        reloadVorratTable(null);
    }

    private void reloadVorratTable(MedVorrat preselect) {

        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("MedVorrat.findVorraeteMitSummen");
        query.setParameter(1, bewohner.getBWKennung());
        query.setParameter(2, bewohner.getBWKennung());
        query.setParameter(3, cbClosedVorrat.isSelected());

        java.util.List<Pair<MedVorrat, BigDecimal>> list = new ArrayList();

        for (Object[] objs : (java.util.List<Object[]>) query.getResultList()) {
            list.add(new Pair<MedVorrat, BigDecimal>(em.find(MedVorrat.class, ((BigInteger) objs[0]).longValue()), (BigDecimal) objs[1]));
        }

        TMVorraete tm = new TMVorraete(list);
        tblVorrat.setModel(tm);
        tblVorrat.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jspVorrat.dispatchEvent(new ComponentEvent(jspVorrat, ComponentEvent.COMPONENT_RESIZED));

        em.close();

        if (preselect != null) {
            int row = tm.findPositionOf(preselect);
            tblVorrat.getSelectionModel().setSelectionInterval(row, row);
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

            java.util.List<Pair<MedBestand, BigDecimal>> list = new ArrayList();

            for (Object[] objs : (java.util.List<Object[]>) query.getResultList()) {
                list.add(new Pair<MedBestand, BigDecimal>(em.find(MedBestand.class, ((BigInteger) objs[0]).longValue()), (BigDecimal) objs[1]));
            }

            tblBestand.setModel(new TMBestand(list));
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
    private JPanel jPanel2;
    private JScrollPane jspVorrat;
    private JTable tblVorrat;
    private JPanel jPanel3;
    private JScrollPane jspBestand;
    private JTable tblBestand;
    private JPanel pnl123;
    private JScrollPane jspBuchung;
    private JTable tblBuchung;
    // End of variables declaration//GEN-END:variables

}
