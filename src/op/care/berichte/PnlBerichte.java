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
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import entity.*;
import entity.files.SYSFilesTools;
import entity.system.SYSPropsTools;
import entity.vorgang.VorgaengeTools;
import op.OPDE;
import op.care.sysfiles.PnlFiles;
import op.system.DlgYesNo;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;
import tablemodels.TMPflegeberichte;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
import java.util.*;

/**
 * @author root
 */
public class PnlBerichte extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.reports";

    /**
     * Dies ist immer der zur Zeit ausgewählte Bericht. null, wenn nichts ausgewählt ist. Wenn mehr als ein
     * Bericht ausgewählt wurde, steht hier immer der Verweis auf den ERSTEN Bericht der Auswahl.
     */
    private Pflegeberichte bericht;

    private JDateChooser jdcVon;
    private JXSearchField txtSearch;
    private CollapsiblePane panelTags;
    private JToggleButton tbShowReplaced, tbShowIDs, tbFilesOnly;
    private JComboBox cmbAuswahl;

    private Bewohner bewohner;
    private JPopupMenu menu;
    private boolean initPhase;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

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
    public PnlBerichte(Bewohner bw, JScrollPane jspSearch) {
        this.initPhase = true;
        initComponents();
        this.jspSearch = jspSearch;
        standardActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        };

        tagFilter = new ArrayList<PBerichtTAGS>();
        prepareSearchArea();

        this.initPhase = false;

        change2Bewohner(bw);


    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);
        searchPanes.add(addCommands());
        searchPanes.add(addFilters());
        searchPanes.addExpansion();
    }

    @Override
    public void reload() {
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
        int textWidth = dim.width - 200 - 100;
        TableColumnModel tcm1 = tblTB.getColumnModel();
        tcm1.getColumn(TMPflegeberichte.COL_PIT).setPreferredWidth(200);
        tcm1.getColumn(TMPflegeberichte.COL_Flags).setPreferredWidth(100);
        tcm1.getColumn(TMPflegeberichte.COL_HTML).setPreferredWidth(textWidth);

        tcm1.getColumn(TMPflegeberichte.COL_PIT).setHeaderValue("Datum");
        tcm1.getColumn(TMPflegeberichte.COL_Flags).setHeaderValue("Info");
        tcm1.getColumn(TMPflegeberichte.COL_HTML).setHeaderValue("Bericht");

//        OPDE.debug("jspTblTBComponentResized(java.awt.event.ComponentEvent evt)");

    }//GEN-LAST:event_jspTblTBComponentResized


    @Override
    public void cleanup() {

        jdcVon.cleanup();

    }

    @Override
    public void change2Bewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
        OPDE.getDisplayManager().setMainMessage(BewohnerTools.getBWLabelText(bewohner));
        txtSearch.setText(null);
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
            SYSFilesTools.handleFile(temp, Desktop.Action.OPEN);
        } catch (IOException e) {
        }
    }

    private void tblTBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTBMousePressed
        Point p = evt.getPoint();

        Point p2 = evt.getPoint();
        SwingUtilities.convertPointToScreen(p2, tblTB);
        final Point screenposition = p2;

        final int row = tblTB.rowAtPoint(p);
        final int col = tblTB.columnAtPoint(p);
//        OPDE.debug("COLUMN: " + col);

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
            final boolean bearbeitenMoeglich = !alreadyEdited && singleRowSelected && bericht.getUsersAcknowledged().isEmpty();

            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {

                final JMenuItem itemPopupEdit = new JMenuItem(OPDE.lang.getString("misc.commands.edit"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/edit.png")));
                itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {

                        final JidePopup popup = new JidePopup();
                        popup.setMovable(false);
                        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

                        final JComponent editor;
                        final Collection<PBerichtTAGS> mytags = new ArrayList(bericht.getTags());

                        switch (col) {
                            case TMPflegeberichte.COL_PIT: {
                                editor = new PnlUhrzeitDatum(bericht.getPit());
                                break;
                            }
                            case TMPflegeberichte.COL_Flags: {
                                if (sameUser || OPDE.isAdmin()) {
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

                                    editor = PBerichtTAGSTools.createCheckBoxPanelForTags(il, bericht.getTags(), new GridLayout(8, 4));
                                } else {
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.cantedit")));
                                    editor = null;
                                }
                                break;
                            }
                            case TMPflegeberichte.COL_HTML: {
                                editor = new JTextArea(bericht.getText(), 10, 40);
                                ((JTextArea) editor).setLineWrap(true);
                                ((JTextArea) editor).setWrapStyleWord(true);
                                ((JTextArea) editor).setEditable(true);
                                break;
                            }
                            default: {
                                editor = null;
                            }
                        }

                        if (editor != null) {
//                            popup.getContentPane().add(new JScrollPane(editor));
                            final JButton saveButton = new JButton(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                            saveButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent actionEvent) {
                                    EntityManager em = OPDE.createEM();
                                    try {

                                        Pflegeberichte newBericht = PflegeberichteTools.copyBericht(bericht);
                                        popup.hidePopup();

                                        switch (col) {
                                            case TMPflegeberichte.COL_PIT: {
                                                newBericht.setPit(((PnlUhrzeitDatum) editor).getPIT());
                                                PflegeberichteTools.changeBericht(bericht, newBericht);
                                                reloadTable();
                                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.edited")));
                                                break;
                                            }
                                            case TMPflegeberichte.COL_Flags: {
                                                bericht.getTags().clear();
                                                bericht.getTags().addAll(mytags);
                                                bericht = EntityTools.merge(bericht);
                                                ((TMPflegeberichte) tblTB.getModel()).setPflegebericht(row, bericht);
                                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.edited")));
                                                break;
                                            }
                                            case TMPflegeberichte.COL_HTML: {
                                                if (!((JTextArea) editor).getText().trim().isEmpty()) {
                                                    newBericht.setText(((JTextArea) editor).getText().trim());
                                                    PflegeberichteTools.changeBericht(bericht, newBericht);
                                                    reloadTable();
                                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.edited")));
                                                } else {
                                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.emptyentry")));
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

                            JPanel pnl = new JPanel(new BorderLayout(10, 10));
                            JScrollPane pnlEditor = new JScrollPane(editor);

                            pnl.add(pnlEditor, BorderLayout.CENTER);
                            JPanel buttonPanel = new JPanel();
                            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
                            buttonPanel.add(saveButton);
                            pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
                            pnl.add(buttonPanel, BorderLayout.SOUTH);

                            popup.setOwner(tblTB);
                            popup.removeExcludedComponent(tblTB);
                            popup.getContentPane().add(pnl);
                            popup.setDefaultFocusComponent(editor);
                            popup.showPopup(screenposition.x, screenposition.y);
                        } else {
                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.cantedit")));
                        }
                    }
                });
                menu.add(itemPopupEdit);
                itemPopupEdit.setEnabled(bearbeitenMoeglich);

                JMenu timemenu = new JMenu(OPDE.lang.getString("misc.commands.changeeffort"));
                int[] mins = new int[]{15, 30, 45, 60, 120, 240, 360};

                HashMap text = new HashMap();
                text.put(60, "1 " + OPDE.lang.getString("misc.msg.Hour"));
                text.put(120, "2 " + OPDE.lang.getString("misc.msg.Hours"));
                text.put(240, "3 " + OPDE.lang.getString("misc.msg.Hours"));
                text.put(360, "4 " + OPDE.lang.getString("misc.msg.Hours"));

                for (int min : mins) {
                    String einheit = "";
                    if (text.containsKey(min)) {
                        einheit = text.get(min).toString();
                    } else {
                        einheit = min + " " + OPDE.lang.getString("misc.msg.Minutes");
                    }
                    JMenuItem item = new JMenuItem(einheit);
                    final int minutes = min;
                    item.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            bericht.setDauer(minutes);
                            bericht = EntityTools.merge(bericht);
                            ((TMPflegeberichte) tblTB.getModel()).setPflegebericht(row, bericht);
                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.edited")));
                        }
                    });
                    timemenu.add(item);
                }
                text.clear();
                menu.add(timemenu);
                timemenu.setEnabled(OPDE.isAdmin() || (!alreadyEdited && singleRowSelected && sameUser)); // Sonderfall, egal ob schon bestätigt.
            }

            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE)) {

                JMenuItem itemPopupDelete = new JMenuItem(OPDE.lang.getString("misc.commands.delete"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/trashcan_empty.png")));
                itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        new DlgYesNo(OPDE.lang.getString("misc.questions.delete"), new ImageIcon(getClass().getResource("/artwork/48x48/bw/trashcan_empty.png")), new Closure() {
                            @Override
                            public void execute(Object answer) {
                                if (answer.equals(JOptionPane.YES_OPTION)) {
                                    Pflegeberichte mybericht = PflegeberichteTools.deleteBericht(bericht);
                                    ((TMPflegeberichte) tblTB.getModel()).setPflegebericht(row, mybericht);

                                    if (!tbShowReplaced.isSelected()) {
                                        reloadTable();
                                    }
                                }
                            }
                        });
                    }
                });
                menu.add(itemPopupDelete);
                itemPopupDelete.setEnabled(bearbeitenMoeglich);
            }

            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.PRINT)) {
                JMenuItem itemPopupPrint = new JMenuItem(OPDE.lang.getString("misc.commands.printselected"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")));
                itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        int[] sel = tblTB.getSelectedRows();
                        printBericht(sel);
                    }
                });
                menu.add(itemPopupPrint);
            }

            menu.add(new JSeparator());

            final JMenuItem itemPopupFiles = SYSFilesTools.getFileMenu(bericht, new Closure() {
                @Override
                public void execute(Object o) {
                    reloadTable();
                }
            });
            menu.add(itemPopupFiles);


            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !alreadyEdited && singleRowSelected) {
//                menu.add(new JSeparator());
                menu.add(VorgaengeTools.getVorgangContextMenu(new JFrame(), bericht, bewohner, standardActionListener));
            }

            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }//GEN-LAST:event_tblTBMousePressed


    private CollapsiblePane addFilters() {
        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setLayout(new VerticalLayout(5));


        txtSearch = new JXSearchField(OPDE.lang.getString("misc.msg.searchphrase"));
        txtSearch.setInstantSearchDelay(750);
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        });


        labelPanel.add(txtSearch);

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
        labelPanel.add(jdcVon);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new HorizontalLayout(5));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JButton homeButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_start.png")));
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Pflegeberichte first = PflegeberichteTools.getFirstBericht(bewohner);
                jdcVon.setDate(first == null ? SYSCalendar.addField(SYSCalendar.today_date(), -2, GregorianCalendar.WEEK_OF_MONTH) : first.getPit());
            }
        });
        homeButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_start_pressed.png")));
        homeButton.setBorder(null);
        homeButton.setBorderPainted(false);
        homeButton.setOpaque(false);
        homeButton.setContentAreaFilled(false);
        homeButton.setToolTipText(OPDE.lang.getString("misc.nav.home"));
        homeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton twoweeksButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/2weeksback.png")));
        twoweeksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcVon.setDate(SYSCalendar.addField(new Date(), -2, GregorianCalendar.WEEK_OF_MONTH));
            }
        });
        twoweeksButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/2weeksback-pressed.png")));
        twoweeksButton.setBorder(null);
        twoweeksButton.setBorderPainted(false);
        twoweeksButton.setOpaque(false);
        twoweeksButton.setContentAreaFilled(false);
        twoweeksButton.setToolTipText(OPDE.lang.getString("misc.nav.2weeksback"));
        twoweeksButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        JButton fourweeksButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/4weeksback.png")));
        fourweeksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcVon.setDate(SYSCalendar.addDate(jdcVon.getDate(), 1));
            }
        });
        fourweeksButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/4weeksback-pressed.png")));
        fourweeksButton.setBorder(null);
        fourweeksButton.setBorderPainted(false);
        fourweeksButton.setOpaque(false);
        fourweeksButton.setContentAreaFilled(false);
        fourweeksButton.setToolTipText(OPDE.lang.getString("misc.nav.4weeksback"));
        fourweeksButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        buttonPanel.add(homeButton);
        buttonPanel.add(twoweeksButton);
        buttonPanel.add(fourweeksButton);


        labelPanel.add(buttonPanel);

        EntityManager em = OPDE.createEM();
        MouseAdapter ma = GUITools.getHyperlinkStyleMouseAdapter();
        Query query = em.createNamedQuery("PBerichtTAGS.findAllActive");
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel(query.getResultList().toArray());
        em.close();

        dcbm.insertElementAt(OPDE.lang.getString("misc.commands.noselection"), 0);
        cmbAuswahl = new JComboBox(dcbm);
        cmbAuswahl.setRenderer(PBerichtTAGSTools.getPBerichtTAGSRenderer());
        cmbAuswahl.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
                SYSPropsTools.storeState(internalClassID + ":cmbAuswahl", cmbAuswahl);
                reloadTable();
            }
        });
        labelPanel.add(cmbAuswahl);
        SYSPropsTools.restoreState(internalClassID + ":cmbAuswahl", cmbAuswahl);

        tbFilesOnly = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.filesonly"));
        tbFilesOnly.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase) return;
                SYSPropsTools.storeState(internalClassID + ":tbFilesOnly", tbFilesOnly);
                reloadTable();
            }
        });
        labelPanel.add(tbFilesOnly);
        SYSPropsTools.restoreState(internalClassID + ":tbFilesOnly", tbFilesOnly);
        tbFilesOnly.setHorizontalAlignment(SwingConstants.LEFT);

        tbShowReplaced = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.showreplaced"));
        tbShowReplaced.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase) return;
                SYSPropsTools.storeState(internalClassID + ":tbShowReplaced", tbShowReplaced);
                reloadTable();
            }
        });
        labelPanel.add(tbShowReplaced);
        SYSPropsTools.restoreState(internalClassID + ":tbShowReplaced", tbShowReplaced);
        tbShowReplaced.setHorizontalAlignment(SwingConstants.LEFT);

        tbShowIDs = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.showpks"));
        tbShowIDs.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase) return;
                SYSPropsTools.storeState(internalClassID + ":tbShowIDs", tbShowIDs);
                reloadTable();
            }
        });
        tbShowIDs.setHorizontalAlignment(SwingConstants.LEFT);

        labelPanel.add(tbShowIDs);
        SYSPropsTools.restoreState(internalClassID + ":tbShowIDs", tbShowIDs);

        CollapsiblePane panelFilter = new CollapsiblePane(OPDE.lang.getString("misc.msg.Filter"));
        panelFilter.setStyle(CollapsiblePane.PLAIN_STYLE);
        panelFilter.setCollapsible(false);
        panelFilter.setContentPane(labelPanel);

        return panelFilter;
    }

    private CollapsiblePane addCommands() {

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(OPDE.lang.getString(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            JideButton addButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.new"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgBericht(bewohner, new Closure() {
                        @Override
                        public void execute(Object bericht) {
                            if (bericht != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.merge(bericht);
                                    em.getTransaction().commit();
                                } catch (Exception e) {
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                                reloadTable();
                            }
                        }
                    });
                }
            });
            mypanel.add(addButton);
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
            JideButton printButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.print"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

//                    SYSPrint.print(new JFrame(), SYSTools.htmlUmlautConversion(op.care.DBHandling.getUeberleitung(bewohner, false, false, cbMedi.isSelected(), cbBilanz.isSelected(), cbBerichte.isSelected(), true, false, false, false, cbBWInfo.isSelected())), false);
                }
            });
            mypanel.add(printButton);
        }


        searchPane.setContentPane(mypanel);
        searchPanes.add(searchPane);


        searchPane.setContentPane(mypanel);
        return searchPane;
    }

    private void reloadTable() {
        if (initPhase) {
            return;
        }

        String tags = "";
        if (cmbAuswahl.getSelectedIndex() > 0) {
            tags = ((PBerichtTAGS) cmbAuswahl.getSelectedItem()).getPbtagid().toString();
        }
//        if (!panelTags.isCollapsed()) {
//            Iterator<PBerichtTAGS> it = tagFilter.iterator();
//            while (it.hasNext()) {
//                tags += Long.toString(it.next().getPbtagid());
//                tags += (it.hasNext() ? "," : "");
//            }
//        }

        String search = txtSearch.getText().trim();

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" "
                + " SELECT p FROM Pflegeberichte p "
                + (tbFilesOnly.isSelected() ? " JOIN p.attachedFiles af " : "")
                + (tags.isEmpty() ? "" : " JOIN p.tags t ")
                + " WHERE p.bewohner = :bewohner "
                + (search.isEmpty() ? " AND p.pit >= :von " : " AND p.text like :search ")
                + (tags.isEmpty() ? "" : " AND t.pbtagid IN (" + tags + " ) ")
                + (tbShowReplaced.isSelected() ? "" : " AND p.editedBy is null ")
                + " ORDER BY p.pit DESC ");
        query.setParameter("bewohner", bewohner);


        if (!search.isEmpty()) {
            query.setParameter("search", "%" + search + "%");
        } else {
            query.setParameter("von", new Date(SYSCalendar.startOfDay(jdcVon.getDate())));
        }

        ArrayList<Pflegeberichte> listBerichte = new ArrayList<Pflegeberichte>(query.getResultList());
        em.close();

        tblTB.setModel(new TMPflegeberichte(listBerichte, tbShowIDs.isSelected()));
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
