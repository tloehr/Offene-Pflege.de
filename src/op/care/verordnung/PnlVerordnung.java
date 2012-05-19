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
package op.care.verordnung;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.Bewohner;
import entity.BewohnerTools;
import entity.UniqueTools;
import entity.system.SYSPropsTools;
import entity.verordnungen.*;
import op.OPDE;
import op.care.med.vorrat.DlgBestandAbschliessen;
import op.care.med.vorrat.DlgBestandAnbrechen;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import tablemodels.TMVerordnung;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author tloehr
 */
public class PnlVerordnung extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.prescription";

    private Bewohner bewohner;
    //    private JFrame parent;
    //    private boolean readOnly = false;
    private JPopupMenu menu;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JCheckBox cbAbgesetzt;
    private boolean initPhase;


    /**
     * Dieser Actionlistener wird gebraucht, damit die einzelnen Menüpunkte des Kontextmenüs, nachdem sie
     * aufgerufen wurden, einen reloadTable() auslösen können.
     */
//    private ActionListener standardActionListener;

    /**
     * Creates new form PnlVerordnung
     */
    public PnlVerordnung(Bewohner bewohner, JScrollPane jspSearch) {
        initPhase = true;
        this.jspSearch = jspSearch;
        initComponents();
        prepareSearchArea();
        this.bewohner = bewohner;
        loadTable();
        initPhase = false;
    }

    @Override
    public void change2Bewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
        OPDE.getDisplayManager().setMainMessage(BewohnerTools.getBWLabelText(bewohner));
        reloadTable();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspVerordnung = new JScrollPane();
        tblVerordnung = new JTable();

        //======== this ========
        setLayout(new FormLayout(
                "default:grow",
                "fill:default:grow"));

        //======== jspVerordnung ========
        {
            jspVerordnung.setToolTipText("");
            jspVerordnung.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    jspVerordnungComponentResized(e);
                }
            });

            //---- tblVerordnung ----
            tblVerordnung.setModel(new DefaultTableModel(
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
            tblVerordnung.setToolTipText(null);
            tblVerordnung.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    tblVerordnungMousePressed(e);
                }
            });
            jspVerordnung.setViewportView(tblVerordnung);
        }
        add(jspVerordnung, CC.xy(1, 1));
    }// </editor-fold>//GEN-END:initComponents

    private void printVerordnungen(int[] sel) {
        try {
            File temp = File.createTempFile("verordnungen", ".html");
            temp.deleteOnExit();
            List<Verordnung> listVerordnung = ((TMVerordnung) tblVerordnung.getModel()).getVordnungenAt(sel);
            String html = SYSTools.htmlUmlautConversion(VerordnungTools.getVerordnungenAsHTML(listVerordnung));
            SYSPrint.print(html, true);
        } catch (IOException e) {
            new DlgException(e);
        }

    }

    private void printStellplan() {

        try {
            File temp = File.createTempFile("stellplan", ".html");
            temp.deleteOnExit();
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            String html = SYSTools.htmlUmlautConversion(VerordnungTools.getStellplanAsHTML(bewohner.getStation().getEinrichtung()));
            out.write(html);
            out.close();
            SYSPrint.handleFile(temp.getAbsolutePath(), Desktop.Action.OPEN);
        } catch (IOException e) {
            new DlgException(e);
        }

    }

    private void tblVerordnungMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVerordnungMousePressed
        Point p = evt.getPoint();

        final ListSelectionModel lsm = tblVerordnung.getSelectionModel();
        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        if (lsm.getMinSelectionIndex() == lsm.getMaxSelectionIndex()) {

            int row = tblVerordnung.rowAtPoint(p);
            lsm.setSelectionInterval(row, row);
        }

        final List<Verordnung> selection = ((TMVerordnung) tblVerordnung.getModel()).getVordnungenAt(tblVerordnung.getSelectedRows());

        // Kontext Menü
        if (singleRowSelected && evt.isPopupTrigger()) {
            boolean readOnly = false;
            final Verordnung selectedVerordnung = selection.get(0);

            long num = BHPTools.getNumBHPs(selectedVerordnung);
            boolean editAllowed = !readOnly && num == 0;
            boolean changeAllowed = !readOnly && !selectedVerordnung.isBedarf() && !selectedVerordnung.isAbgesetzt() && num > 0;
            boolean absetzenAllowed = !readOnly && !selectedVerordnung.isAbgesetzt();
            boolean deleteAllowed = !readOnly && num == 0;

            SYSTools.unregisterListeners(menu);
            menu = new JPopupMenu();

            JMenuItem itemPopupEdit = new JMenuItem("Korrigieren");
            itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    long numVerKennung = VerordnungTools.getNumVerodnungenMitGleicherKennung(verordnung);
//                    int status = numVerKennung == 1 ? DlgVerordnung.EDIT_MODE : DlgVerordnung.EDIT_OF_CHANGE_MODE;

                    new DlgVerordnung(selectedVerordnung, DlgVerordnung.ALLOW_ALL_EDIT, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                Pair<Verordnung, List<VerordnungPlanung>> result = (Pair<Verordnung, List<VerordnungPlanung>>) o;
                                EntityManager em = OPDE.createEM();
                                Verordnung verordnung = em.merge(result.getFirst());

                                try {
                                    em.getTransaction().begin();
                                    em.lock(verordnung, LockModeType.OPTIMISTIC);

                                    // Änderung an bestehenden Planungen
                                    for (VerordnungPlanung planung : verordnung.getPlanungen()) {
                                        planung = em.merge(planung);
                                        em.lock(planung, LockModeType.OPTIMISTIC);
                                    }

                                    // Planungen die zukünftig wegfallen.
                                    for (VerordnungPlanung planung : result.getSecond()) {
                                        planung = em.merge(planung);
                                        em.lock(planung, LockModeType.OPTIMISTIC);
                                        em.remove(planung);
                                    }

                                    // Bei einer Korrektur werden alle bisherigen Einträge aus der BHP zuerst wieder entfernt.
                                    Query queryDELBHP = em.createQuery("DELETE FROM BHP bhp WHERE bhp.verordnung = :verordnung");
                                    queryDELBHP.setParameter("verordnung", verordnung);
                                    queryDELBHP.executeUpdate();

                                    if (!verordnung.isBedarf()) {
                                        BHPTools.erzeugen(em, verordnung.getPlanungen(), new Date(), true);
                                    }

                                    em.getTransaction().commit();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Korrigiert: " + VerordnungTools.toPrettyString(verordnung), 2));
                                } catch (javax.persistence.OptimisticLockException ole) {
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Wurde zwischenzeitlich von jemand anderem korrigiert.", DisplayMessage.IMMEDIATELY, 2));
                                    em.getTransaction().rollback();
                                } catch (Exception e) {
                                    em.getTransaction().rollback();
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }

                                reloadTable();
                            }
                        }
                    }).setVisible(true);
                }
            });

            menu.add(itemPopupEdit);
            itemPopupEdit.setEnabled(editAllowed && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
            //ocs.setEnabled(this, "itemPopupEditText", itemPopupEditText, !readOnly && status > 0 && changeable);
            // -------------------------------------------------
            JMenuItem itemPopupChange = new JMenuItem("Verändern");
            itemPopupChange.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {

                    new DlgVerordnung((Verordnung) selectedVerordnung.clone(), DlgVerordnung.NO_CHANGE_MED_AND_SIT, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                EntityManager em = OPDE.createEM();

                                Pair<Verordnung, List<VerordnungPlanung>> result = (Pair<Verordnung, List<VerordnungPlanung>>) o;

                                try {
                                    em.getTransaction().begin();

                                    Verordnung newVerordnung = em.merge(result.getFirst());
                                    Verordnung oldVerordnung = em.merge(selectedVerordnung);

                                    em.lock(oldVerordnung, LockModeType.OPTIMISTIC);

                                    // Bei einer Veränderung, wird erst die alte Verordnung durch den ANsetzenden Arzt ABgesetzt.
                                    oldVerordnung.setAbDatum(new Date());
                                    oldVerordnung.setAbgesetztDurch(em.merge(OPDE.getLogin().getUser()));
                                    oldVerordnung.setAbArzt(newVerordnung.getAnArzt() == null ? null : em.merge(newVerordnung.getAnArzt()));
                                    oldVerordnung.setAbKH(newVerordnung.getAnKH() == null ? null : em.merge(newVerordnung.getAnKH()));

                                    // Dann wird die neue Verordnung angesetzt.
                                    // die neue Verordnung beginnt eine Sekunde, nachdem die vorherige Abgesetzt wurde.
                                    newVerordnung.setAnDatum(SYSCalendar.addField(oldVerordnung.getAbDatum(), 1, GregorianCalendar.SECOND));

                                    // Dann werden die nicht mehr benötigten BHPs der alten Verordnung entfernt.
                                    BHPTools.aufräumen(em, oldVerordnung);

                                    // Die neuen BHPs werden erzeugt.
                                    if (!newVerordnung.isBedarf()) {
                                        // ab der aktuellen Uhrzeit
                                        BHPTools.erzeugen(em, newVerordnung.getPlanungen(), new Date(), false);
                                    }

                                    em.getTransaction().commit();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Geändert: " + VerordnungTools.toPrettyString(oldVerordnung), 2));
                                } catch (OptimisticLockException ole) {
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Wurde zwischenzeitlich von jemand anderem korrigiert.", DisplayMessage.IMMEDIATELY, 2));
                                    em.getTransaction().rollback();
                                } catch (Exception e) {
                                    em.getTransaction().rollback();
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                                reloadTable();
                            }

                        }
                    }).setVisible(true);
                }
            });
            menu.add(itemPopupChange);
            itemPopupChange.setEnabled(changeAllowed && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
            // -------------------------------------------------
            JMenuItem itemPopupQuit = new JMenuItem("Absetzen");
            itemPopupQuit.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {

                    new DlgAbsetzen(selectedVerordnung, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    Verordnung verordnung = (Verordnung) em.merge(o);
                                    em.lock(verordnung, LockModeType.OPTIMISTIC);
                                    verordnung.setAbDatum(new Date());
                                    verordnung.setAbgesetztDurch(em.merge(OPDE.getLogin().getUser()));
                                    BHPTools.aufräumen(em, verordnung);
                                    em.getTransaction().commit();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Abgesetzt: " + VerordnungTools.toPrettyString(verordnung), 2));
                                    em.getEntityManagerFactory().getCache().evict(Verordnung.class);
                                } catch (OptimisticLockException ole) {
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Verordnung oder eine BHP wurden zwischenzeitlich von jemand anderem verändert", DisplayMessage.IMMEDIATELY, 2));
                                    em.getTransaction().rollback();
                                } catch (Exception e) {
                                    em.getTransaction().rollback();
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                                reloadTable();
                            }
                        }
                    }).setVisible(true);
                }
            });
            menu.add(itemPopupQuit);
            itemPopupQuit.setEnabled(absetzenAllowed && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
            // -------------------------------------------------
            JMenuItem itemPopupDelete = new JMenuItem("Löschen");
            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    JOptionPane pane = new JOptionPane("Soll die Verordnung wirklich gelöscht werden.", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, new ImageIcon(getClass().getResource("/artwork/48x48/bw/trashcan_empty.png")));
                    JDialog dialog = pane.createDialog(OPDE.getMainframe(), "");
                    dialog.setLocation(OPDE.getMainframe().getLocationForDialog(dialog.getSize()));
                    dialog.setVisible(true);

                    if (pane.getValue().equals(JOptionPane.YES_OPTION)) {
                        Verordnung myverordnung = null;
                        EntityManager em = OPDE.createEM();
                        try {
                            myverordnung = em.merge(selectedVerordnung);
                            em.getTransaction().begin();
                            em.lock(myverordnung, LockModeType.OPTIMISTIC);
                            em.remove(myverordnung);

                            Query delQuery = em.createQuery("DELETE FROM BHP b WHERE b.verordnung = :verordnung");
                            delQuery.setParameter("verordnung", selectedVerordnung);
                            delQuery.executeUpdate();

                            em.getTransaction().commit();
                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Gelöscht: " + VerordnungTools.toPrettyString(selectedVerordnung), 2));
//                            em.getEntityManagerFactory().getCache().evict(Verordnung.class, myverordnung);
                        } catch (OptimisticLockException ole) {
                            em.getTransaction().rollback();
                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Diese Verordnung wurde zwischenzeitlich schon gelöscht.", DisplayMessage.IMMEDIATELY, OPDE.getErrorMessageTime()));
                        } catch (Exception e) {
                            em.getTransaction().rollback();
                            OPDE.fatal(e);
                        } finally {
                            em.close();
                        }

                        reloadTable();
                    }

                }
            });
            menu.add(itemPopupDelete);

            itemPopupDelete.setEnabled(deleteAllowed && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE));
//            itemPopupDelete.setEnabled(true);

            if (selectedVerordnung.hasMedi()) {
                menu.add(new JSeparator());

                final MedBestand bestandImAnbruch = MedBestandTools.getBestandImAnbruch(DarreichungTools.getVorratZurDarreichung(bewohner, selectedVerordnung.getDarreichung()));
                boolean bestandAbschliessenAllowed = !readOnly && !selectedVerordnung.isAbgesetzt() && bestandImAnbruch != null && !bestandImAnbruch.hasNextBestand();
                boolean bestandAnbrechenAllowed = !readOnly && !selectedVerordnung.isAbgesetzt() && bestandImAnbruch == null;

                JMenuItem itemPopupCloseBestand = new JMenuItem("Bestand abschließen");
                itemPopupCloseBestand.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        new DlgBestandAbschliessen(bestandImAnbruch, new Closure() {
                            @Override
                            public void execute(Object o) {
                                if (o != null){
                                    reloadTable();
                                }
                            }
                        });
                    }
                });
                menu.add(itemPopupCloseBestand);
                itemPopupCloseBestand.setEnabled(bestandAbschliessenAllowed && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));

                JMenuItem itemPopupOpenBestand = new JMenuItem("Bestand anbrechen");
                itemPopupOpenBestand.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        new DlgBestandAnbrechen(selectedVerordnung.getDarreichung(), selectedVerordnung.getBewohner(), new Closure() {
                            @Override
                            public void execute(Object o) {
                                if (o != null){
                                    reloadTable();
                                }
                            }
                        });
                    }
                });
                menu.add(itemPopupOpenBestand);
                itemPopupOpenBestand.setEnabled(bestandAnbrechenAllowed && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
            }
            menu.add(new JSeparator());

            JMenuItem itemPopupPrint = new JMenuItem("Markierte Verordnungen drucken");
            itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    int[] sel = tblVerordnung.getSelectedRows();
                    printVerordnungen(sel);
                }
            });
            menu.add(itemPopupPrint);

//            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !verordnung.isAbgesetzt() && singleRowSelected) {
//                menu.add(new JSeparator());
//                menu.add(SYSFilesTools.getSYSFilesContextMenu(parent, verordnung, standardActionListener));
//            }
//
//            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !verordnung.isAbgesetzt() && singleRowSelected) {
//                menu.add(new JSeparator());
//                menu.add(VorgaengeTools.getVorgangContextMenu(parent, verordnung, bewohner, standardActionListener));
//            }


            menu.add(new JSeparator());
            JMenuItem itemPopupInfo = new JMenuItem("Infos anzeigen");
            itemPopupInfo.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    final MedBestand bestandImAnbruch = MedBestandTools.getBestandImAnbruch(DarreichungTools.getVorratZurDarreichung(bewohner, selectedVerordnung.getDarreichung()));

                    long dafid = 0;
                    String message = "VerID: " + selectedVerordnung.getVerid();
                    if (bestandImAnbruch != null) {
                        BigDecimal apv = MedBestandTools.getAPVperBW(bestandImAnbruch.getVorrat());
                        BigDecimal apvBest = bestandImAnbruch.getApv();
                        message += "  VorID: " + bestandImAnbruch.getVorrat().getVorID() + "  DafID: " + dafid + "  APV: " + apv + "  APV (Bestand): " + apvBest;
                    }

                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(message, 10));
                }
            });
            itemPopupInfo.setEnabled(true);
            menu.add(itemPopupInfo);


            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }//GEN-LAST:event_tblVerordnungMousePressed

    private void jspVerordnungComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspVerordnungComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();

        TableColumnModel tcm1 = tblVerordnung.getColumnModel();

        tcm1.getColumn(TMVerordnung.COL_MSSN).setPreferredWidth(dim.width / 5);  // 1/5 tel der Gesamtbreite
        tcm1.getColumn(TMVerordnung.COL_Dosis).setPreferredWidth(dim.width / 5 * 3);  // 3/5 tel der Gesamtbreite
        tcm1.getColumn(TMVerordnung.COL_Hinweis).setPreferredWidth(dim.width / 5);  // 1/5 tel der Gesamtbreite
        tcm1.getColumn(0).setHeaderValue("Medikament / Massnahme");
        tcm1.getColumn(1).setHeaderValue("Dosierung / Häufigkeit");
        tcm1.getColumn(2).setHeaderValue("Hinweise");

    }//GEN-LAST:event_jspVerordnungComponentResized

    public void cleanup() {
        SYSTools.unregisterListeners(this);
//        SYSRunningClassesTools.endModule(myRunningClass);
    }

    private void loadTable() {

        tblVerordnung.setModel(new TMVerordnung(bewohner, cbAbgesetzt.isSelected(), true));
        tblVerordnung.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

//        btnBuchen.setEnabled(OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
//        btnVorrat.setEnabled(OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
//        btnPrint.setEnabled(tblVerordnung.getModel().getRowCount() > 0 && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT));

        jspVerordnung.dispatchEvent(new ComponentEvent(jspVerordnung, ComponentEvent.COMPONENT_RESIZED));
        tblVerordnung.getColumnModel().getColumn(0).setCellRenderer(new RNDHTML());
        tblVerordnung.getColumnModel().getColumn(1).setCellRenderer(new RNDHTML());
        tblVerordnung.getColumnModel().getColumn(2).setCellRenderer(new RNDHTML());
//        tblVerordnung.getColumnModel().getColumn(3).setCellRenderer(new RNDHTML());
//        tblVerordnung.getColumnModel().getColumn(4).setCellRenderer(new RNDHTML());
    }

    private void reloadTable() {
        if (initPhase) return;
        TMVerordnung tm = (TMVerordnung) tblVerordnung.getModel();
        tm.reload(bewohner, cbAbgesetzt.isSelected());
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

        CollapsiblePane panelFilter = new CollapsiblePane("Filter");
        panelFilter.setStyle(CollapsiblePane.PLAIN_STYLE);
        panelFilter.setCollapsible(false);


        cbAbgesetzt = new JCheckBox("Abgesetzte Verordnungen anzeigen");
        cbAbgesetzt.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
        cbAbgesetzt.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SYSPropsTools.storeState(internalClassID + ":cbAbgesetzt", cbAbgesetzt);
                reloadTable();
            }
        });
        cbAbgesetzt.setBackground(Color.WHITE);

        labelPanel.add(cbAbgesetzt);
        SYSPropsTools.restoreState(internalClassID + ":cbAbgesetzt", cbAbgesetzt);

        panelFilter.setContentPane(labelPanel);

        return panelFilter;
    }

    private CollapsiblePane addCommands() {
        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane("Verordnungen");
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            JideButton addButton = GUITools.createHyperlinkButton("Neue Verordnung eingeben", new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgVerordnung(new Verordnung(bewohner), DlgVerordnung.ALLOW_ALL_EDIT, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                Verordnung verordnung = (Verordnung) o;
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    verordnung.setVerKennung(UniqueTools.getNewUID(em, "__verkenn").getUid());
                                    verordnung = em.merge(verordnung);
                                    if (!verordnung.isBedarf()) {
                                        BHPTools.erzeugen(em, verordnung.getPlanungen(), new Date(), true);
                                    }
                                    em.getTransaction().commit();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Neu erstellt: " + VerordnungTools.toPrettyString(verordnung), 2));
                                } catch (Exception e) {
                                    em.getTransaction().rollback();
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                                reloadTable();
                            }
                        }
                    }).setVisible(true);
                }
            });
            mypanel.add(addButton);
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {
            JideButton buchenButton = GUITools.createHyperlinkButton("Medikamente einbuchen", new ImageIcon(getClass().getResource("/artwork/22x22/shetaddrow.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
//                    new DlgBericht(bewohner, new Closure() {
//                        @Override
//                        public void execute(Object bericht) {
//                            if (bericht != null) {
//                                EntityTools.persist(bericht);
//                                reloadTable();
//                            }
//                        }
//                    }));
                }
            });
            mypanel.add(buchenButton);
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {
            JideButton vorratButton = GUITools.createHyperlinkButton("Vorräte bearbeiten", new ImageIcon(getClass().getResource("/artwork/22x22/sheetremocolums.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
//                    OPDE.showJDialogAsSheet(new DlgBericht(bewohner, new Closure() {
//                        @Override
//                        public void execute(Object bericht) {
//                            if (bericht != null) {
//                                EntityTools.persist(bericht);
//                                reloadTable();
//                            }
//                            OPDE.hideSheet();
//                        }
//                    }));
                }
            });
            mypanel.add(vorratButton);
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
            JideButton printButton = GUITools.createHyperlinkButton("Verordnungen drucken", new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    printVerordnungen(null);
                }
            });
            mypanel.add(printButton);
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
            JideButton printButton = GUITools.createHyperlinkButton("Stellplan drucken", new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    printStellplan();
                }
            });
            mypanel.add(printButton);
        }

        searchPane.setContentPane(mypanel);
        return searchPane;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspVerordnung;
    private JTable tblVerordnung;
    // End of variables declaration//GEN-END:variables


}
