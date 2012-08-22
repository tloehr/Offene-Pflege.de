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
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.system.SYSPropsTools;
import entity.prescription.*;
import op.OPDE;
import op.tools.DlgYesNo;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;
import tablemodels.TMBestand;
import tablemodels.TMVorraete;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;

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

    public static final String internalClassID = "nursingrecords.inventory";

    private Resident bewohner;
    private boolean ignoreEvent;
    //    private Component thisDialog;
    private JPopupMenu menuV;
    //    private JPopupMenu menuB;
    private PnlBuchungen pnlBuchungen;

    //    private OCSec ocs;
    private MedInventory inventory;
    private MedStock bestand;
//    private JDialog thisComponent;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JCheckBox cbClosedBestand, cbClosedVorrat;

    /**
     * Creates new form DlgVorrat
     */
    public PnlVorrat(Resident bewohner, JScrollPane jspSearch) {
        super();
        ignoreEvent = true;
        this.jspSearch = jspSearch;
        initComponents();
        initDialog();
        switchResident(bewohner);
        ignoreEvent = false;
    }

    @Override
    public void switchResident(Resident bewohner) {
        this.bewohner = bewohner;
        OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(bewohner));
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
                if (!ignoreEvent) reloadVorratTable();
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
                if (!ignoreEvent) reloadVorratTable();
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
            JideButton addButton = GUITools.createHyperlinkButton("Neue Buchung", new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (pnlBuchungen.hasBestand()) {
                        pnlBuchungen.getNeueBuchungPopup((JComponent) actionEvent.getSource()).showPopup();
                    } else {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Wählen Sie zuerst einen Bestand aus", 2));
                    }
                }
            });
            mypanel.add(addButton);
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER)) {
            JideButton addButton = GUITools.createHyperlinkButton("Alle Buchungen zurücksetzen", new ImageIcon(getClass().getResource("/artwork/22x22/bw/undo.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (pnlBuchungen.hasBestand()) {
                        pnlBuchungen.resetBuchungen();
                    } else {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Wählen Sie zuerst einen Bestand aus", 2));
                    }
                }
            });
            mypanel.add(addButton);
        }


//        JMenuItem itemPopupReset = new JMenuItem(".", new ImageIcon(getClass().getResource("/artwork/22x22/bw/undo.png")));
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

    @Override
    public void reload() {
        reloadVorratTable();
    }

    private void initDialog() {

        pnlBuchungen = new PnlBuchungen(null, new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
//                    if (o instanceof MedStockTransaction) {
//                        recalculate(((MedStockTransaction) o).getBestand());
//                    } else if (o instanceof MedBestand) {
//                        recalculate((MedBestand) o);
//                    } else {
//                        reloadVorratTable();
//                        reloadBestandTable();
//                    }

                    reloadVorratTable();
                    reloadBestandTable();
                }
            }
        });
        add(pnlBuchungen, CC.xywh(3, 1, 1, 3));

        prepareSearchArea();

        tblVorrat.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    if (tblVorrat.getSelectedRowCount() > 0) {
                        MedInventory myInventory = ((TMVorraete) tblVorrat.getModel()).getVorrat(tblVorrat.getSelectedRow());
                        if (!myInventory.equals(inventory)) {
                            inventory = myInventory;
                            reloadBestandTable();
                        }
                    }
                }
            }
        });
        tblBestand.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    if (tblBestand.getSelectedRowCount() > 0) {
                        MedStock myBestand = ((TMBestand) tblBestand.getModel()).getBestand(tblBestand.getSelectedRow());
                        if (!myBestand.equals(bestand)) {
                            bestand = myBestand;
                            pnlBuchungen.setBestand(bestand);
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
    }// </editor-fold>//GEN-END:initComponents

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

    private void txtSucheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSucheActionPerformed
        JXSearchField search = (JXSearchField) evt.getSource();
        if (!search.getText().isEmpty() && search.getText().matches("\\d*")) {
            // Nur Zahlen.. Das ist eine BestID
            long bestid = Long.parseLong(search.getText());
            EntityManager em = OPDE.createEM();
            bestand = em.find(MedStock.class, bestid);
            em.close();

            if (bestand != null) {
                if (!bewohner.equals(bestand.getInventory().getResident())) {
                    bewohner = bestand.getInventory().getResident();
                    OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(bewohner));
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Medikament gehört eine[m|r] anderen Bewohner[in]. Habe umgeschaltet.", 2));
                    OPDE.getMainframe().change2Bewohner(bewohner);
                }

                reloadVorratTable(bestand);

            } else {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Der eingegebene Bestand existiert nicht.", 2));
            }

        }
    }//GEN-LAST:event_txtSucheActionPerformed

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
        pnlBuchungen.setBestand(bestand);


        // Menüeinträge
        if (evt.isPopupTrigger()) {

            ListSelectionModel lsm = tblBestand.getSelectionModel();
            lsm.setSelectionInterval(row, row);

            SYSTools.unregisterListeners(menuV);
            menuV = new JPopupMenu();

            // ---------------
            JMenuItem itemPopupAnbruch = new JMenuItem("Bestand anbrechen", new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_play.png")));
            itemPopupAnbruch.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {

                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        bestand = em.merge(bestand);
                        em.lock(bestand, LockModeType.OPTIMISTIC);
                        BigDecimal apv = MedStockTools.getPassendesAPV(bestand);
                        MedStockTools.anbrechen(bestand, apv);
                        em.getTransaction().commit();
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bestand Nr. " + bestand.getBestID() + " wurde angebrochen", 2));
                        reloadBestandTable();
                    } catch (OptimisticLockException ole) {
                        em.getTransaction().rollback();
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Bestand wurde zwischenzeitlich geändert.", internalClassID));
                        reloadVorratTable();
                    } catch (Exception e) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }

                    reloadBestandTable();
                }
            });
            itemPopupAnbruch.setEnabled(!bestand.isAbgeschlossen() && !bestand.isAngebrochen());
            menuV.add(itemPopupAnbruch);

            // ---------------
            JMenuItem itemPopupVerschließen = new JMenuItem("Bestand wieder verschließen", new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_stop.png")));
            itemPopupVerschließen.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        bestand = em.merge(bestand);
                        em.lock(bestand, LockModeType.OPTIMISTIC);
                        bestand.setAus(SYSConst.DATE_BIS_AUF_WEITERES);
                        bestand.setAnbruch(SYSConst.DATE_BIS_AUF_WEITERES);
                        em.getTransaction().commit();
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bestand Nr. " + bestand.getBestID() + " wurde wieder verschlossen", 2));
                        reloadBestandTable();
                    } catch (OptimisticLockException ole) {
                        em.getTransaction().rollback();
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Bestand wurde zwischenzeitlich geändert.", internalClassID));
                        reloadVorratTable();
                    } catch (Exception e) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }
                    reloadBestandTable();
                }
            });
            itemPopupVerschließen.setEnabled(!bestand.isAbgeschlossen() && bestand.isAngebrochen());
            menuV.add(itemPopupVerschließen);

            // ----------------
            JMenuItem itemPopupClose = new JMenuItem("Bestand abschließen", new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_end.png")));
            itemPopupClose.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgYesNo("Möchten Sie den Bestand Nr. " + bestand.getBestID() + " wirklich abschließen ?", new ImageIcon(getClass().getResource("/artwork/48x48/bw/bottom.png")), new Closure() {
                        @Override
                        public void execute(Object answer) {
                            if (answer.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    bestand = em.merge(bestand);
                                    MedStockTools.abschliessen(em, bestand, "", MedStockTransactionTools.STATUS_KORREKTUR_MANUELL);
                                    em.getTransaction().commit();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bestand Nr. " + bestand.getBestID() + " wurde abgeschlossen", 2));
                                    reloadBestandTable();
                                } catch (OptimisticLockException ole) {
                                    em.getTransaction().rollback();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Bestand wurde zwischenzeitlich geändert.", internalClassID));
                                    reloadVorratTable();
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }

                            }
                        }
                    });
                }
            });


            itemPopupClose.setEnabled(bestand.isAngebrochen());
            menuV.add(itemPopupClose);

            // ---------------
            JMenuItem itemPopupEinbuchen = new JMenuItem("Bestand wieder aktivieren", new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_start.png")));
            itemPopupEinbuchen.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {

                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        bestand = em.merge(bestand);
                        em.lock(bestand, LockModeType.OPTIMISTIC);
                        bestand.setAus(SYSConst.DATE_BIS_AUF_WEITERES);
                        em.getTransaction().commit();
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bestand Nr. " + bestand.getBestID() + " wurde wieder aktiviert", 2));
                        reloadBestandTable();
                    } catch (OptimisticLockException ole) {
                        em.getTransaction().rollback();
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Bestand wurde zwischenzeitlich geändert.", internalClassID));
                        reloadVorratTable();
                    } catch (Exception e) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }
                }
            });
            itemPopupEinbuchen.setEnabled(bestand.isAbgeschlossen());
            menuV.add(itemPopupEinbuchen);

            JMenuItem itemPopupDelete = new JMenuItem("Löschen", new ImageIcon(getClass().getResource("/artwork/22x22/bw/trashcan_empty.png")));
            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {

                    new DlgYesNo("Möchten Sie den Bestand Nr. " + bestand.getBestID() + " wirklich löschen ?", new ImageIcon(getClass().getResource("/artwork/48x48/bw/trashcan_empty.png")), new Closure() {
                        @Override
                        public void execute(Object answer) {
                            if (answer.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    bestand = em.merge(bestand);

                                    MedInventory inventory = em.merge(bestand.getInventory());

                                    em.lock(bestand, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    em.lock(inventory, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                                    inventory.getMedStocks().remove(bestand);
                                    em.remove(bestand);

                                    em.getTransaction().commit();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bestand Nr. " + bestand.getBestID() + " und alle zugehörigen Buchungen wurden gelöscht.", 2));
                                    reloadBestandTable();
                                } catch (OptimisticLockException ole) {
                                    em.getTransaction().rollback();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Bestand wurde zwischenzeitlich geändert.", internalClassID));
                                    reloadVorratTable();
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                            }
                        }
                    });
                }
            });
            menuV.add(itemPopupDelete);
            // ----------------
            menuV.add(new JSeparator());

            JMenuItem itemPopupPrint = new JMenuItem("Beleg drucken", new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")));
            itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    //SYSPrint.printLabel(mybestid);
                }
            });
            menuV.add(itemPopupPrint);


            menuV.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }//GEN-LAST:event_tblBestandMousePressed

    private void tblVorratMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVorratMousePressed
        final TMVorraete tm = (TMVorraete) tblVorrat.getModel();
        if (tm.getRowCount() == 0) {
            inventory = null;
            return;
        }

        Point p = evt.getPoint();
//        final int col = tblVorrat.columnAtPoint(p);
        final int row = tblVorrat.rowAtPoint(p);
        ListSelectionModel lsm = tblVorrat.getSelectionModel();
        lsm.setSelectionInterval(row, row);

        inventory = tm.getVorrat(row);
        reloadBestandTable();

        if (evt.isPopupTrigger()) {
            // Menüeinträge
            SYSTools.unregisterListeners(menuV);
            menuV = new JPopupMenu();

            // <** TEST 0001
            JMenuItem itemPopupDelete = new JMenuItem("Vorrat löschen", new ImageIcon(getClass().getResource("/artwork/22x22/bw/trashcan_empty.png")));
            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {

                    new DlgYesNo("Möchten Sie den Vorrat Nr. " + inventory.getVorID() + " wirklich löschen ?", new ImageIcon(getClass().getResource("/artwork/48x48/bw/trashcan_empty.png")), new Closure() {
                        @Override
                        public void execute(Object answer) {
                            if (answer.equals(JOptionPane.YES_OPTION)) {
                                OPDE.getDisplayManager().setDBActionMessage(true);
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();

                                    inventory = em.merge(inventory);
                                    em.lock(inventory, LockModeType.OPTIMISTIC);
                                    em.remove(inventory);

                                    em.getTransaction().commit();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Vorrat Nr. " + inventory.getVorID() + " und alle zugehörigen Bestände und Buchungen wurden gelöscht.", 2));
                                } catch (OptimisticLockException ole) {
                                    em.getTransaction().rollback();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Vorrat wurde zwischenzeitlich geändert.", internalClassID));
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                                OPDE.getDisplayManager().setDBActionMessage(false);
                            }
                        }
                    });
                    reloadVorratTable();

                }
            });
            menuV.add(itemPopupDelete);
            // TEST 0001 **>


            // <** TEST 0002
            JMenuItem itemPopupClose = new JMenuItem("Vorrat abschließen und ausbuchen", new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_end.png")));
            itemPopupClose.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    new DlgYesNo("Möchten Sie den Vorrat Nr. " + inventory.getVorID() + " wirklich abschließen ?", new ImageIcon(getClass().getResource("/artwork/48x48/bw/player_end.png")), new Closure() {
                        @Override
                        public void execute(Object answer) {
                            if (answer.equals(JOptionPane.YES_OPTION)) {
                                OPDE.getDisplayManager().setDBActionMessage(true);
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();

                                    inventory = em.merge(inventory);
                                    em.lock(inventory, LockModeType.OPTIMISTIC);


                                    // Alle Bestände abschliessen.
                                    for (MedStock bestand : inventory.getMedStocks()) {
                                        if (!bestand.isAbgeschlossen()) {
                                            bestand = em.merge(bestand);
                                            em.lock(bestand, LockModeType.OPTIMISTIC);
                                            MedStockTools.abschliessen(em, bestand, "Abschluss des Bestandes bei Vorratsabschluss.", MedStockTransactionTools.STATUS_KORREKTUR_AUTO_ABSCHLUSS_BEI_VORRATSABSCHLUSS);
                                        }
                                    }

                                    // Vorrat abschliessen
                                    inventory.setBis(new Date());

                                    em.getTransaction().commit();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Vorrat Nr. " + inventory.getVorID() + " und alle zugehörigen Bestände abgeschlossen", 2));

                                } catch (OptimisticLockException ole) {
                                    em.getTransaction().rollback();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Vorrat wurde zwischenzeitlich geändert", internalClassID));
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                                OPDE.getDisplayManager().setDBActionMessage(false);
                            }
                        }
                    });
                    reloadVorratTable();
                }
            });
            menuV.add(itemPopupClose);
            // TEST 0002 **>

            menuV.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }//GEN-LAST:event_tblVorratMousePressed


    private void reloadVorratTable() {
        reloadVorratTable(null);
    }

    private void reloadVorratTable(MedStock preselect) {

        bestand = preselect;

        if (preselect != null && preselect.getInventory().isAbgeschlossen()) {
            ignoreEvent = true;
            cbClosedVorrat.setSelected(true);
            ignoreEvent = false;
        }

        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("MedVorrat.findVorraeteMitSummen");
        query.setParameter(1, bewohner.getBWKennung());
        query.setParameter(2, bewohner.getBWKennung());
        query.setParameter(3, cbClosedVorrat.isSelected());

        java.util.List<Pair<MedInventory, BigDecimal>> list = new ArrayList();

        for (Object[] objs : (java.util.List<Object[]>) query.getResultList()) {
            list.add(new Pair<MedInventory, BigDecimal>(em.find(MedInventory.class, ((BigInteger) objs[0]).longValue()), (BigDecimal) objs[1]));
        }

        TMVorraete tm = new TMVorraete(list);
        tblVorrat.setModel(tm);
        tblVorrat.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblVorrat.getColumnModel().getColumn(TMVorraete.COL_NAME).setCellRenderer(new RNDHTML());
        tblVorrat.getColumnModel().getColumn(TMVorraete.COL_MENGE).setCellRenderer(new RNDHTML());
        jspVorrat.dispatchEvent(new ComponentEvent(jspVorrat, ComponentEvent.COMPONENT_RESIZED));

        em.close();

        if (preselect != null) {
            int row = tm.findPositionOf(preselect.getInventory());
            tblVorrat.getSelectionModel().setSelectionInterval(row, row);
            scrollToCenter(tblVorrat, row, 0);
        } else {
            tblBestand.setModel(new DefaultTableModel());
            bestand = null;
            pnlBuchungen.setBestand(null);
        }
    }


    private void scrollToCenter(JTable table, int rowIndex, int vColIndex) {
        if (!(table.getParent() instanceof JViewport)) {
            return;
        }
        JViewport viewport = (JViewport) table.getParent();
        Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
        Rectangle viewRect = viewport.getViewRect();
        rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);


        int centerX = (viewRect.width - rect.width) / 2;
        int centerY = (viewRect.height - rect.height) / 2;
        if (rect.x < centerX) {
            centerX = -centerX;
        }
        if (rect.y < centerY) {
            centerY = -centerY;
        }
        rect.translate(centerX, centerY);
        viewport.scrollRectToVisible(rect);
    }

    private void reloadBestandTable() {
        if (inventory == null) {
            tblBestand.setModel(new DefaultTableModel());
        } else {

            if (bestand != null && bestand.isAbgeschlossen()) {
                ignoreEvent = true;
                cbClosedBestand.setSelected(true);
                ignoreEvent = false;
            }

            EntityManager em = OPDE.createEM();
            Query query = em.createNamedQuery("MedBestand.findByVorratMitRestsumme");
            query.setParameter(1, inventory.getVorID());
            query.setParameter(2, inventory.getVorID());
            query.setParameter(3, cbClosedBestand.isSelected());

            java.util.List<Pair<MedStock, BigDecimal>> list = new ArrayList();

            for (Object[] objs : (java.util.List<Object[]>) query.getResultList()) {
                list.add(new Pair<MedStock, BigDecimal>(em.find(MedStock.class, ((BigInteger) objs[0]).longValue()), (BigDecimal) objs[1]));
            }

            TMBestand tm = new TMBestand(list);

            tblBestand.setModel(tm);
            tblBestand.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

            jspBestand.dispatchEvent(new ComponentEvent(jspBestand, ComponentEvent.COMPONENT_RESIZED));

            em.close();

            for (int i = 0; i < tblBestand.getModel().getColumnCount(); i++) {
                tblBestand.getColumnModel().getColumn(i).setCellRenderer(new RNDHTML());
            }

            if (bestand != null) {
                int row = tm.findPositionOf(bestand);
                tblBestand.getSelectionModel().setSelectionInterval(row, row);
                tblBestand.scrollRectToVisible(tblBestand.getCellRect(row, 0, true));
            }
        }
    }

    private void recalculate(MedStock changed) {

        BigDecimal newvorrat = MedInventoryTools.getInventorySum(changed.getInventory());
        BigDecimal newbestand = MedStockTools.getBestandSumme(changed);

        TMVorraete tmv = (TMVorraete) tblVorrat.getModel();
        int rowv = tmv.findPositionOf(changed.getInventory());

        TMBestand tmb = (TMBestand) tblBestand.getModel();
        int rowb = tmb.findPositionOf(changed);

        tmv.setBestandsMenge(rowv, newvorrat);
        tmv.fireTableCellUpdated(rowv, TMVorraete.COL_MENGE);

        tmb.setBestandsMenge(rowb, newbestand);
        tmb.fireTableCellUpdated(rowv, TMBestand.COL_MENGE);


    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel2;
    private JScrollPane jspVorrat;
    private JTable tblVorrat;
    private JPanel jPanel3;
    private JScrollPane jspBestand;
    private JTable tblBestand;
    // End of variables declaration//GEN-END:variables

}
