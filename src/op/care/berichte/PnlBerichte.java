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
package op.care.berichte;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.TitledSeparator;
import com.toedter.calendar.JDateChooser;
import entity.*;
import entity.files.SYSFilesTools;
import entity.system.SYSPropsTools;
import entity.vorgang.VorgaengeTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;
import tablemodels.TMPflegeberichte;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

/**
 * @author root
 */
public class PnlBerichte extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.reports";
    public static final int DEFAULT_DAUER = 3;

    /**
     * Dies ist immer der zur Zeit ausgewählte Bericht. null, wenn nichts ausgewählt ist. Wenn mehr als ein
     * Bericht ausgewählt wurde, steht hier immer der Verweis auf den ERSTEN Bericht der Auswahl.
     */
    private Pflegeberichte bericht;

    private JDateChooser jdcVon;
    private JXSearchField txtSearch;
    private CollapsiblePane panelTime, panelSearchText, panelTags, panelSpecials;
    private JCheckBox cbShowEdits, cbShowIDs;


    private boolean dauerChanged;

    private Bewohner bewohner;
    private JPopupMenu menu;
    private boolean initPhase;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private ItemListener itemListener;
    private MouseAdapter mouseAdapter;

    /**
     * Dieser Actionlistener wird gebraucht, damit die einzelnen Menüpunkte des Kontextmenüs, nachdem sie
     * aufgerufen wurden, einen reloadTable() auslösen können.
     */
    private ActionListener standardActionListener;


    /**
     * Diese Liste enhtält die Menge der Tags, die im Suchfenster gesetzt wurden.
     */
    private ArrayList<PBerichtTAGS> tagFilter;

    /**
     * Creates new form PnlBerichte
     */
    public PnlBerichte(Bewohner bewohner, JScrollPane jspSearch) {
        this.initPhase = true;

        initComponents();

//        btnSystemInfo.setSelected(SYSPropsTools.isBoolean(internalClassID + ":btnSystemInfo"));

        this.jspSearch = jspSearch;

//        this.panelSearch.add(new JXHeader("Berichte", "", new ImageIcon(getClass().getResource("/artwork/22x22/bw/viewmag1.png"))));
//        positionToAddPanels = this.panelSearch.getComponentCount();

        standardActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        };

        tagFilter = new ArrayList<PBerichtTAGS>();
        prepareSearchArea();

        this.initPhase = false;

        change2Bewohner(bewohner);


    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        CollapsiblePane searchPane = new CollapsiblePane("Pflegeberichte");
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);


        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            JideButton addButton = GUITools.createHyperlinkButton("Neuen Bericht eingeben", new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    OPDE.showJDialogAsSheet(new DlgBericht(bewohner, new Closure() {
                        @Override
                        public void execute(Object bericht) {
                            if (bericht != null) {
                                EntityTools.persist(bericht);
                                reloadTable();
                            }
                            OPDE.hideSheet();
                        }
                    }));
                }
            });
            mypanel.add(addButton);
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
            JideButton printButton = GUITools.createHyperlinkButton("Drucken", new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
//                    SYSPrint.print(new JFrame(), SYSTools.htmlUmlautConversion(op.care.DBHandling.getUeberleitung(bewohner, false, false, cbMedi.isSelected(), cbBilanz.isSelected(), cbBerichte.isSelected(), true, false, false, false, cbBWInfo.isSelected())), false);
                }
            });
            mypanel.add(printButton);
        }


        txtSearch = new JXSearchField("Suchbegriff");
        txtSearch.setInstantSearchDelay(500);
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        });


        searchPane.add(txtSearch);

        searchPane.setContentPane(mypanel);
        searchPanes.add(searchPane);

        addSpecials();
        addByTime();
        addByTags();

        searchPanes.addExpansion();

    }

//    private void btnApplyActionPerformed(ActionEvent e) {
//        boolean success = false;
//        TimelineCallbackAdapter standardAdapter = new TimelineCallbackAdapter() {
//            @Override
//            public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
//                if (newState == Timeline.TimelineState.DONE) {
////                    btnSearch.setEnabled(true);
//                    btnAddBericht.setEnabled(true);
//                }
//            }
//        };
//        switch (laufendeOperation) {
//            case LAUFENDE_OPERATION_BERICHT_EINGABE: {
//                if (txtBericht.getText().trim().isEmpty()) {
//                    if (lblMessage2Timeline == null || lblMessage2Timeline.getState() == Timeline.TimelineState.IDLE) {
//                        lblMessage2Timeline = SYSTools.flashLabel(lblMessage2, "Kann keinen leeren Bericht speichern.", 6, Color.BLUE);
//                    }
//                } else {
//                    success = EntityTools.persist(aktuellerBericht);
//                    splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow, standardAdapter);
//                }
//                break;
//            }
//            case LAUFENDE_OPERATION_BERICHT_BEARBEITEN: {
//                if (txtBericht.getText().trim().isEmpty()) {
//                    if (lblMessage2Timeline == null || lblMessage2Timeline.getState() == Timeline.TimelineState.IDLE) {
//                        lblMessage2Timeline = SYSTools.flashLabel(lblMessage2, "Kann keinen leeren Bericht speichern.", 6, Color.BLUE);
//                    }
//                } else {
//                    success = PflegeberichteTools.changeBericht(oldBericht, aktuellerBericht);
//                    oldBericht = null;
//                    splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow, standardAdapter);
//                }
//                break;
//            }
//            case LAUFENDE_OPERATION_BERICHT_LOESCHEN: {
//                success = PflegeberichteTools.deleteBericht(aktuellerBericht);
//                break;
//            }
//            default: {
//
//            }
//        }
//
//        // War alles ok, dann wird der Ausgangszustand wieder hergestellt.
//        if (success) {
//            lblMessage.setText(null);
//            aktuellerBericht = null;
//            textmessageTL.cancel();
//            splitBCPercent = SYSTools.showSide(splitButtonsCenter, SYSTools.LEFT_UPPER_SIDE, speedFast);
//            laufendeOperation = LAUFENDE_OPERATION_NICHTS;
//            reloadTable();
//        }
//    }

//    private void splitTableEditorComponentResized(ComponentEvent e) {
//        SYSTools.showSide(splitTableEditor, splitTEPercent);
//    }
//
//    private void btnDeleteActionPerformed(ActionEvent e) {
//        // Darf ich das ?
//        if (!(singleRowSelected && aktuellerBericht != null && !aktuellerBericht.isDeleted() && !aktuellerBericht.isReplaced() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE))) {
//            return;
//        }
//        laufendeOperation = LAUFENDE_OPERATION_BERICHT_LOESCHEN;
//
//        textmessageTL = SYSTools.flashLabel(lblMessage, "Bericht löschen ?");
//        splitBCPercent = SYSTools.showSide(splitButtonsCenter, SYSTools.RIGHT_LOWER_SIDE, speedFast);
//    }
//
//    private void btnEditActionPerformed(ActionEvent e) {
//        // Darf ich das ?
//        if (!(singleRowSelected && aktuellerBericht != null && !aktuellerBericht.isDeleted() && !aktuellerBericht.isReplaced() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE))) {
//            return;
//        }
//
//        initPhase = true;
//        Date now = new Date();
//        laufendeOperation = LAUFENDE_OPERATION_BERICHT_BEARBEITEN;
//        oldBericht = aktuellerBericht;
//        aktuellerBericht = PflegeberichteTools.copyBericht(aktuellerBericht);
//
//        pnlTags.setViewportView(PBerichtTAGSTools.createCheckBoxPanelForTags(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                JCheckBox cb = (JCheckBox) e.getSource();
//                PBerichtTAGS tag = (PBerichtTAGS) cb.getClientProperty("UserObject");
//                if (e.getStateChange() == ItemEvent.DESELECTED) {
//                    aktuellerBericht.getTags().remove(tag);
//                } else {
//                    aktuellerBericht.getTags().add(tag);
//                }
//            }
//        }, aktuellerBericht.getTags(), new GridLayout(0, 1)));
//
//        DateFormat df = DateFormat.getTimeInstance();
//        jdcDatum.setDate(aktuellerBericht.getPit());
//        jdcDatum.setMaxSelectableDate(now);
//        txtUhrzeit.setText(df.format(aktuellerBericht.getPit()));
//        txtBericht.setText(aktuellerBericht.getText());
//
//        initPhase = false;
//        textmessageTL = SYSTools.flashLabel(lblMessage, "Geänderten Bericht speichern ?");
//        splitBCPercent = SYSTools.showSide(splitButtonsCenter, SYSTools.RIGHT_LOWER_SIDE, speedFast);
//        splitTEPercent = SYSTools.showSide(splitTableEditor, 0.4d, speedFast, null);
//        txtBericht.requestFocus();
//    }

//    private void btnSearchActionPerformed(ActionEvent e) {
//        toggleSearchArea(speedSlow);
//    }

//    private void tblTBMouseReleased(MouseEvent e) {
//        ListSelectionModel lsm = tblTB.getSelectionModel();
//        singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();
////        btnEdit.setEnabled(singleRowSelected && !aktuellerBericht.isDeleted() && !aktuellerBericht.isReplaced() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
//
//    }


//    private void btnSystemInfoItemStateChanged(ItemEvent e) {
//        if (initPhase) {
//            return;
//        }
//        SYSPropsTools.storeBoolean(internalClassID + ":btnSystemInfo", btnSystemInfo.isSelected());
//        reloadTable();
//    }
//
//    private void btnCancelActionPerformed(ActionEvent e) {
//
//        TimelineCallbackAdapter standardCancelAdapter = new TimelineCallbackAdapter() {
//            @Override
//            public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
//                if (newState == Timeline.TimelineState.DONE) {
//                    btnAddBericht.setEnabled(OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT));
//                }
//            }
//        };
//
//
//        switch (laufendeOperation) {
//            case LAUFENDE_OPERATION_BERICHT_EINGABE: {
//                splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow, standardCancelAdapter);
//                break;
//            }
//            case LAUFENDE_OPERATION_BERICHT_BEARBEITEN: {
//                splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow, standardCancelAdapter);
//                break;
//            }
//            default: {
//
//            }
//        }
//        aktuellerBericht = null;
//        oldBericht = null;
//        lblMessage.setText(null);
//        textmessageTL.cancel();
//        splitBCPercent = SYSTools.showSide(splitButtonsCenter, SYSTools.LEFT_UPPER_SIDE, speedFast);
//        laufendeOperation = LAUFENDE_OPERATION_NICHTS;
//    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspTblTB = new JScrollPane();
        tblTB = new JTable();

        //======== this ========
        setLayout(new FormLayout(
                "default:grow",
                "fill:default:grow"));

        //======== jspTblTB ========
        {
            jspTblTB.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    jspTblTBComponentResized(e);
                }
            });

            //---- tblTB ----
            tblTB.setModel(new DefaultTableModel(
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
            tblTB.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    tblTBMousePressed(e);
                }
            });
            jspTblTB.setViewportView(tblTB);
        }
        add(jspTblTB, CC.xy(1, 1));
    }// </editor-fold>//GEN-END:initComponents

    private void jspTblTBComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspTblTBComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();
        // Größe der Text Spalte im TB ändern.
        // Summe der fixen Spalten  = 210 + ein bisschen
        int textWidth = dim.width - 200 - 50;
        TableColumnModel tcm1 = tblTB.getColumnModel();
        tcm1.getColumn(0).setPreferredWidth(200);
        tcm1.getColumn(1).setPreferredWidth(50);
        tcm1.getColumn(2).setPreferredWidth(textWidth);

        tcm1.getColumn(0).setHeaderValue("Datum");
        tcm1.getColumn(1).setHeaderValue("Info");
        tcm1.getColumn(2).setHeaderValue("Bericht");

        OPDE.debug("jspTblTBComponentResized(java.awt.event.ComponentEvent evt)");

    }//GEN-LAST:event_jspTblTBComponentResized


    @Override
    public void cleanup() {

        jdcVon.cleanup();

    }

    @Override
    public void change2Bewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
        reloadTable();
    }

    private void printBericht(int[] sel) {
        try {
            // Create temp file.
            File temp = File.createTempFile("pflegebericht", ".html");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));

            TMPflegeberichte tm = (TMPflegeberichte) tblTB.getModel();
            out.write(SYSTools.htmlUmlautConversion(PflegeberichteTools.getBerichteAsHTML(SYSTools.getSelectionAsList(tm.getPflegeberichte(), sel), false)));

            out.close();
            SYSPrint.handleFile(new JFrame(), temp.getAbsolutePath(), Desktop.Action.OPEN);
        } catch (IOException e) {
        }
    }

    private void tblTBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTBMousePressed
        Point p = evt.getPoint();

        Point p2 = evt.getPoint();
        // Convert a coordinate relative to a component's bounds to screen coordinates
        SwingUtilities.convertPointToScreen(p2, tblTB);

        final Point screenposition = p2;

        final int row = tblTB.rowAtPoint(p);
        final int col = tblTB.columnAtPoint(p);
        OPDE.debug("COLUMN: " + col);

        ListSelectionModel lsm = tblTB.getSelectionModel();
        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        if (singleRowSelected) {
            lsm.setSelectionInterval(row, row);
        }

        bericht = (Pflegeberichte) tblTB.getModel().getValueAt(lsm.getLeadSelectionIndex(), TMPflegeberichte.COL_BERICHT);
        final boolean alreadyEdited = bericht.isDeleted() || bericht.isReplaced();
        final boolean sameUser = bericht.getUser().equals(OPDE.getLogin().getUser());

//        btnEdit.setEnabled(singleRowSelected && !aktuellerBericht.isDeleted() && !aktuellerBericht.isReplaced() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
//        btnDelete.setEnabled(singleRowSelected && !aktuellerBericht.isDeleted() && !aktuellerBericht.isReplaced() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE));

        if (evt.isPopupTrigger()) {
            SYSTools.unregisterListeners(menu);
            menu = new JPopupMenu();

            /**
             * KORRIGIEREN
             * Ein Bericht kann geändert werden (Korrektur)
             * - Wenn sie nicht im Übergabeprotokoll abgehakt wurde.
             */
            final boolean bearbeitenMöglich = !alreadyEdited && singleRowSelected && bericht.getUsersAcknowledged().isEmpty();

            final JMenuItem itemPopupEdit = new JMenuItem("Bericht korrigieren");
            itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {

                    final JidePopup popup = new JidePopup();
                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

                    final JComponent editor;
                    final Collection<PBerichtTAGS> mytags = new ArrayList(bericht.getTags());

                    switch (col) {
                        case TMPflegeberichte.COL_PIT: {
                            editor = new JTextArea(DateFormat.getDateTimeInstance().format(bericht.getPit()));
                            ((JTextArea) editor).setLineWrap(true);
                            ((JTextArea) editor).setWrapStyleWord(true);
                            break;
                        }
                        case TMPflegeberichte.COL_Flags: {
                            if (bearbeitenMöglich && (sameUser || OPDE.isAdmin())) {

                                ItemListener il = new ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent itemEvent) {
                                        JCheckBox cb = (JCheckBox) itemEvent.getSource();
                                        PBerichtTAGS tag = (PBerichtTAGS) cb.getClientProperty("UserObject");
                                        if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
                                            mytags.remove(tag);
                                        } else {
                                            mytags.add(tag);
                                        }
                                    }
                                };

                                editor = PBerichtTAGSTools.createCheckBoxPanelForTags(il, bericht.getTags(), new VerticalLayout());

                            } else {
                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Sie können diesen Bericht nicht ändern.", 2));
                                editor = null;
                            }
                            break;
                        }
                        case TMPflegeberichte.COL_HTML: {
                            editor = new JTextArea(bericht.getText(), 10, 40);
                            break;
                        }
                        default: {
                            editor = null;
                        }
                    }

                    if (editor != null && bearbeitenMöglich) {
                        popup.getContentPane().add(new JScrollPane(editor));
                        final JButton saveButton = new JButton(new ImageIcon(getClass().getResource("/artwork/22x22/bw/apply.png")));
                        saveButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                EntityManager em = OPDE.createEM();
                                try {

                                    Pflegeberichte newBericht = PflegeberichteTools.copyBericht(bericht);
                                    popup.hidePopup();

                                    switch (col) {
                                        case TMPflegeberichte.COL_PIT: {
                                            try {
                                                newBericht.setPit(DateFormat.getDateTimeInstance().parse(((JTextArea) editor).getText()));
                                                PflegeberichteTools.changeBericht(bericht, newBericht);
                                                reloadTable();
                                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Datum geändert", 2));
                                            } catch (ParseException pe) {
                                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Falsche Datumseingabe", 2));
                                            }
                                            break;
                                        }
                                        case TMPflegeberichte.COL_Flags: {
                                            bericht.getTags().clear();
                                            bericht.getTags().addAll(mytags);
                                            bericht = EntityTools.merge(bericht);
                                            reloadTable();
                                            break;
                                        }
                                        case TMPflegeberichte.COL_HTML: {
                                            if (!((JTextArea) editor).getText().trim().isEmpty()) {
                                                newBericht.setText(((JTextArea) editor).getText().trim());
                                                PflegeberichteTools.changeBericht(bericht, newBericht);
                                                reloadTable();
                                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bericht geändert", 2));
                                            } else {
                                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Kann keinen leeren Bericht speichern", 2));
                                            }
                                            break;
                                        }
                                        default: {

                                        }
                                    }


                                } catch (Exception e) {
                                    em.getTransaction().rollback();
                                } finally {
                                    em.close();
                                }
                            }
                        });

//                    popup.setOwner(tblTB);
                        popup.getContentPane().add(new JPanel().add(saveButton));
                        popup.setDefaultFocusComponent(editor);
                        popup.showPopup(screenposition.x, screenposition.y);
                    } else {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Sie können diesen Bericht nicht ändern.", 2));
                    }
                }
            });
            menu.add(itemPopupEdit);
            itemPopupEdit.setEnabled(OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));


            JMenuItem itemPopupPrint = new JMenuItem("Markierte Berichte drucken");
            itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    int[] sel = tblTB.getSelectedRows();
                    printBericht(sel);
                }
            });
            menu.add(itemPopupPrint);

//            itemPopupEdit.setEnabled(OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
//            itemPopupDelete.setEnabled(OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.CANCEL));

            // Nur anzeigen wenn derselbe User die Änderung versucht, der auch den Text geschrieben hat.
//            if (bearbeitenMöglich && (sameUser || OPDE.isAdmin())) {
//                menu.add(PBerichtTAGSTools.createMenuForTags(bericht));
//            }


            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !alreadyEdited && singleRowSelected) {
                menu.add(new JSeparator());
                menu.add(SYSFilesTools.getSYSFilesContextMenu(new JFrame(), bericht, standardActionListener));
            }

            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !alreadyEdited && singleRowSelected) {
                menu.add(new JSeparator());
                menu.add(VorgaengeTools.getVorgangContextMenu(new JFrame(), bericht, bewohner, standardActionListener));
            }

            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }//GEN-LAST:event_tblTBMousePressed


//    private void prepareSearchArea2() {
//        panelSearch = new ArrayList<CollapsiblePane>();
//        addByTime();
//        addByTags();
//        addBySearchText();
//        taskPaneContentChangedListener.contentChanged(new TaskPaneContentChangedEvent(this, panelSearch, "Pflegeberichte"));
//    }


    private void addByTags() {
        panelTags = new CollapsiblePane("nach Markierung");
        panelTags.setSlidingDirection(SwingConstants.SOUTH);
        panelTags.setStyle(CollapsiblePane.PLAIN_STYLE);
//        panelTags.setCollapsible(false);

        JPanel panel = PBerichtTAGSTools.getCheckBoxPanelForTags(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox cb = (JCheckBox) e.getSource();
                // Ich benutze hier die ClientProperty Map um die Entities dem Listener mitzugeben.
                // Das war wohl nicht so gedacht. Aber es geht trotzdem.
                PBerichtTAGS tag = (PBerichtTAGS) cb.getClientProperty("UserObject");
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    tagFilter.remove(tag);
                } else {
                    tagFilter.add(tag);
                }
                reloadTable();
            }
        }, new ArrayList<PBerichtTAGS>());

        try {
            panelTags.setCollapsed(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        panelTags.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                reloadTable();
            }

            @Override
            public void paneCollapsed(CollapsiblePaneEvent collapsiblePaneEvent) {
                reloadTable();
            }
        });

        panelTags.setContentPane(panel);
        searchPanes.add(panelTags);
    }

//    private void addBySearchText() {
//
//        panelSearchText = new CollapsiblePane("nach Suchbegriff");
//        panelSearchText.setSlidingDirection(SwingConstants.SOUTH);
//        panelSearchText.setStyle(CollapsiblePane.PLAIN_STYLE);
//        panelSearchText.setCollapsible(false);
//
//        txtSearch = new JXSearchField("Suchbegriff");
//        txtSearch.setInstantSearchDelay(500);
//        txtSearch.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                reloadTable();
//            }
//        });
//
//
//        panelSearchText.add(txtSearch);
//        try {
//            panelSearchText.setCollapsed(true);
//        } catch (PropertyVetoException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
////        panelSearchText.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
////            @Override
////            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
////                reloadTable();
////            }
////
////            @Override
////            public void paneCollapsed(CollapsiblePaneEvent collapsiblePaneEvent) {
////                reloadTable();
////            }
////        });
//        searchPanes.add(panelSearchText);
//    }

    private void addByTime() {

        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setLayout(new VerticalLayout());

        panelTime = new CollapsiblePane("nach Zeit");
        panelTime.setStyle(CollapsiblePane.PLAIN_STYLE);
        panelTime.setCollapsible(false);

        jdcVon = new JDateChooser(SYSCalendar.addField(new Date(), -2, GregorianCalendar.WEEK_OF_MONTH));
        jdcVon.setBackground(Color.WHITE);
        jdcVon.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (initPhase) {
                    return;
                }
                if (!evt.getPropertyName().equals("date")) {
                    return;
                }
                reloadTable();
            }
        });
        labelPanel.add(new TitledSeparator("Berichte anzeigen von", TitledSeparator.TYPE_PARTIAL_ETCHED, SwingConstants.LEADING));
        labelPanel.add(jdcVon);
        JideButton button2Weeks = GUITools.createHyperlinkButton("vor 2 Wochen", null, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcVon.setDate(SYSCalendar.addField(new Date(), -2, GregorianCalendar.WEEK_OF_MONTH));
            }
        });
        JideButton button4Weeks = GUITools.createHyperlinkButton("vor 4 Wochen", null, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcVon.setDate(SYSCalendar.addField(new Date(), -4, GregorianCalendar.WEEK_OF_MONTH));
            }
        });
        JideButton buttonBeginning = GUITools.createHyperlinkButton("von Anfang an", null, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcVon.setDate(PflegeberichteTools.firstBericht(bewohner).getPit());
            }
        });

        labelPanel.add(button2Weeks);
        labelPanel.add(button4Weeks);
        labelPanel.add(buttonBeginning);

        panelTime.setContentPane(labelPanel);

        panelTime.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                reloadTable();
            }

            @Override
            public void paneCollapsed(CollapsiblePaneEvent collapsiblePaneEvent) {
                reloadTable();
            }
        });

        searchPanes.add(panelTime);
    }

    private void addSpecials() {
        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setLayout(new VerticalLayout());


        panelSpecials = new CollapsiblePane("Sonstiges");
        panelSpecials.setStyle(CollapsiblePane.PLAIN_STYLE);
        panelSpecials.setCollapsible(false);

        txtSearch = new JXSearchField("Suchbegriff");
        txtSearch.setInstantSearchDelay(500);
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        });


        labelPanel.add(txtSearch);

        cbShowEdits = new JCheckBox("Änderungen anzeigen");
        cbShowEdits.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
        cbShowEdits.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SYSPropsTools.storeState(internalClassID + ":cbShowEdits", cbShowEdits);
                reloadTable();
            }
        });
        cbShowIDs = new JCheckBox("Bericht Nummern anzeigen");
        cbShowIDs.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
        cbShowIDs.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SYSPropsTools.storeState(internalClassID + ":cbShowIDs", cbShowIDs);
                reloadTable();
            }
        });

        labelPanel.add(cbShowEdits);
        labelPanel.add(cbShowIDs);

        panelSpecials.setContentPane(labelPanel);
        searchPanes.add(panelSpecials);

        SYSPropsTools.restoreState(internalClassID + ":cbShowEdits", cbShowEdits);
        SYSPropsTools.restoreState(internalClassID + ":cbShowIDs", cbShowIDs);

    }

    private void reloadTable() {
        if (initPhase) {
            return;
        }

        OPDE.debug("reloadTable()");
//        if (textUpperLabelTL != null){
//            textUpperLabelTL.cancel();
//        }
//        textUpperLabelTL = SYSTools.flashLabel(lblBW, "Datenbankzugriff");


        String tags = "";
        if (!panelTags.isCollapsed()) {
            Iterator<PBerichtTAGS> it = tagFilter.iterator();
            while (it.hasNext()) {
                tags += Long.toString(it.next().getPbtagid());
                tags += (it.hasNext() ? "," : "");
            }
        }

        String search = txtSearch.getText().trim();


        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" "
                + " SELECT p FROM Pflegeberichte p "
                + (tags.isEmpty() ? "" : " JOIN p.tags t ")
                + " WHERE p.bewohner = :bewohner "
                + (search.isEmpty() ? " AND p.pit >= :von " : " AND p.text like :search ")
                + (tags.isEmpty() ? "" : " AND t.pbtagid IN (" + tags + " ) ")
                + (cbShowEdits.isSelected() ? "" : " AND p.editedBy is null ")
                + " ORDER BY p.pit DESC ");
        query.setParameter("bewohner", bewohner);


        if (!search.isEmpty()) {
            query.setParameter("search", "%" + search + "%");
        } else {
            query.setParameter("von", new Date(SYSCalendar.startOfDay(jdcVon.getDate())));
        }

        ArrayList<Pflegeberichte> listBerichte = new ArrayList<Pflegeberichte>(query.getResultList());
        em.close();

        tblTB.setModel(new TMPflegeberichte(listBerichte, cbShowIDs.isSelected()));
        tblTB.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jspTblTB.dispatchEvent(new ComponentEvent(jspTblTB, ComponentEvent.COMPONENT_RESIZED));

        tblTB.getColumnModel().getColumn(0).setCellRenderer(new RNDHTML());
        tblTB.getColumnModel().getColumn(1).setCellRenderer(new RNDHTML());
        tblTB.getColumnModel().getColumn(2).setCellRenderer(new RNDHTML());

        dispatchEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspTblTB;
    private JTable tblTB;
    // End of variables declaration//GEN-END:variables
}
