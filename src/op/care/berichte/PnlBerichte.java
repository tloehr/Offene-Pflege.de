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
import com.toedter.calendar.JDateChooser;
import entity.*;
import op.OPDE;
import op.care.CleanablePanel;
import op.care.FrmPflege;
import op.tools.*;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.JXTitledSeparator;
import org.pushingpixels.trident.Timeline;
import tablemodels.TMPflegeberichte;

import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

/**
 * @author root
 */
public class PnlBerichte extends CleanablePanel {

    public static final String internalClassID = "nursingrecords.reports";
    public static final int DEFAULT_DAUER = 3;

    private static int speedSlow = 700;
    private static int speedFast = 500;

    private final int LAUFENDE_OPERATION_NICHTS = 0;
    private final int LAUFENDE_OPERATION_BERICHT_EINGABE = 1;
    private final int LAUFENDE_OPERATION_BERICHT_LOESCHEN = 2;
    private final int LAUFENDE_OPERATION_BERICHT_BEARBEITEN = 3;

    private int laufendeOperation;
    private double splitTEPercent, splitBCPercent;
    private Pflegeberichte aktuellerBericht;

    private JDateChooser jdcVon, jdcBis;
    private JTextField txtSearch;
    private JXTaskPane panelTime, panelText, panelTags, panelSpecials;
    private JCheckBox cbShowEdits, cbShowIDs;

    private Timeline textmessageTL, textErrorTL;


    private Bewohner bewohner;
    private JPopupMenu menu;
    private boolean initPhase;
    private javax.swing.JFrame parent;
    /**
     * Dieser Actionlistener wird gebraucht, damit die einzelnen Menüpunkte des Kontextmenüs, nachdem sie
     * aufgerufen wurden, einen reloadTable() auslösen können.
     */
    private ActionListener fileActionListener;
    /**
     * Dies ist immer der zur Zeit ausgewählte Bericht. null, wenn nichts ausgewählt ist. Wenn mehr als ein
     * Bericht ausgewählt wurde, steht hier immer der Verweis auf den ERSTEN Bericht der Auswahl.
     */
    private Pflegeberichte bericht;
    private final int TAB_DATE = 0;
    private final int TAB_SEARCH = 1;
    private final int TAB_TAGS = 2;


    /**
     * Diese Liste enhtält die Menge der Tags, die im Suchfenster gesetzt wurden.
     */
    private ArrayList<PBerichtTAGS> tagFilter;

    /**
     * Creates new form PnlBerichte
     */
    public PnlBerichte(FrmPflege pflege, Bewohner bewohner) {
        this.initPhase = true;
        this.bewohner = bewohner;
        this.parent = pflege;
        this.bericht = null;
        this.laufendeOperation = LAUFENDE_OPERATION_NICHTS;
        this.textmessageTL = null;
        this.aktuellerBericht = null;

        initComponents();
        BewohnerTools.setBWLabel(lblBW, bewohner);

        //TODO: die RestoreStates müssen nach JPA gewandelt werden.
//        SYSTools.restoreState(this.getClass().getName() + ":cbShowEdits", cbShowEdits);
//        SYSTools.restoreState(this.getClass().getName() + ":cbTBIDS", cbTBIDS);

        fileActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        };

        tagFilter = new ArrayList<PBerichtTAGS>();
        prepareSearchArea();

        btnAddBericht.setEnabled(OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT));
        btnEdit.setEnabled(OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT));

        splitBCPercent = SYSTools.showSide(splitButtonsCenter, SYSTools.LEFT_UPPER_SIDE);
        splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE);

        this.initPhase = false;

        reloadTable();

    }

    private void btnAddBerichtActionPerformed(ActionEvent e) {
        initPhase = true;
        Date now = new Date();
        laufendeOperation = LAUFENDE_OPERATION_BERICHT_EINGABE;
        aktuellerBericht = new Pflegeberichte(bewohner);
        aktuellerBericht.setPit(now);
        aktuellerBericht.setText("");
        aktuellerBericht.setDauer(DEFAULT_DAUER);

        pnlTags.setViewportView(PBerichtTAGSTools.createCheckBoxPanelForTags(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox cb = (JCheckBox) e.getSource();
                PBerichtTAGS tag = (PBerichtTAGS) cb.getClientProperty("UserObject");
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    aktuellerBericht.getTags().remove(tag);
                } else {
                    aktuellerBericht.getTags().add(tag);
                }
            }
        }, aktuellerBericht.getTags(), new GridLayout(0, 1)));

        DateFormat df = DateFormat.getTimeInstance();
        jdcDatum.setDate(now);
        jdcDatum.setMaxSelectableDate(now);
        txtUhrzeit.setText(df.format(now));
        txtBericht.setText("");

        initPhase = false;
        textmessageTL = SYSTools.flashLabel(lblMessage, "Neuen Bericht speichern ?");
        splitBCPercent = SYSTools.showSide(splitButtonsCenter, SYSTools.RIGHT_LOWER_SIDE, speedFast);
        splitTEPercent = SYSTools.showSide(splitTableEditor, 0.4d, speedFast);
    }

    private void btnDetailsItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void btnEndReactivateActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        switch (laufendeOperation) {
            case LAUFENDE_OPERATION_BERICHT_EINGABE: {
                if (txtBericht.getText().trim().isEmpty()) {
                    SYSTools.flashLabel(lblBW, "Kann keinen leeren Bericht speichern.", 6, Color.ORANGE);
                } else {
                    EntityTools.persist(aktuellerBericht);
                }
                splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow);
                break;
            }
            case LAUFENDE_OPERATION_BERICHT_BEARBEITEN: {
                splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow);
                break;
            }
            default: {

            }
        }
        lblMessage.setText(null);
        aktuellerBericht = null;
        textmessageTL.cancel();
        splitBCPercent = SYSTools.showSide(splitButtonsCenter, SYSTools.LEFT_UPPER_SIDE, speedFast);
        laufendeOperation = LAUFENDE_OPERATION_NICHTS;
    }

    private void splitButtonsCenterComponentResized(ComponentEvent e) {
        SYSTools.showSide(splitButtonsCenter, splitBCPercent);
    }

    private void jdcDatumPropertyChange(PropertyChangeEvent e) {
        if (!initPhase && e.getPropertyName().equals("date")) {
            Time uhrzeit = new Time(SYSCalendar.erkenneUhrzeit(txtUhrzeit.getText()).getTimeInMillis());
            aktuellerBericht.setPit(SYSCalendar.addTime2Date(jdcDatum.getDate(), uhrzeit));
        }
    }

    private void txtUhrzeitFocusLost(FocusEvent e) {
        GregorianCalendar gc;
        try {
            gc = SYSCalendar.erkenneUhrzeit(txtUhrzeit.getText());
            txtUhrzeit.setText(SYSCalendar.toGermanTime(gc));

        } catch (NumberFormatException nfe) {
            gc = new GregorianCalendar();
            txtUhrzeit.setText(SYSCalendar.toGermanTime(gc));
        }
        Time uhrzeit = new Time(gc.getTimeInMillis());
        aktuellerBericht.setPit(SYSCalendar.addTime2Date(jdcDatum.getDate(), uhrzeit));
    }

    private void txtDauerFocusGained(FocusEvent e) {
        // TODO add your code here
    }

    private void txtDauerFocusLost(FocusEvent e) {
        NumberFormat nf = NumberFormat.getIntegerInstance();
        String test = txtDauer.getText();
        int dauer = 3;
        try {
            Number num = nf.parse(test);
            dauer = num.intValue();
            if (dauer < 0) {
                dauer = 3;
                txtDauer.setText("3");
            }
        } catch (ParseException ex) {
            dauer = 3;
            txtDauer.setText("3");
        }
        aktuellerBericht.setDauer(dauer);
    }

    private void txtBerichtCaretUpdate(CaretEvent e) {
        aktuellerBericht.setText(txtBericht.getText());
    }

    private void txtUhrzeitActionPerformed(ActionEvent e) {
        txtDauer.requestFocus();
    }

    private void splitTableEditorComponentResized(ComponentEvent e) {
        SYSTools.showSide(splitTableEditor, splitTEPercent);
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        switch (laufendeOperation) {
            case LAUFENDE_OPERATION_BERICHT_EINGABE: {
                splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow);
                break;
            }
            case LAUFENDE_OPERATION_BERICHT_BEARBEITEN: {
                splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow);
                break;
            }
            default: {

            }
        }
        lblMessage.setText(null);
        textmessageTL.cancel();
        SYSTools.showSide(splitButtonsCenter, SYSTools.LEFT_UPPER_SIDE, speedFast);
        laufendeOperation = LAUFENDE_OPERATION_NICHTS;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        scrollPane2 = new JScrollPane();
        taskSearch = new JXTaskPaneContainer();
        lblBW = new JLabel();
        splitTableEditor = new JSplitPane();
        jspTblTB = new JScrollPane();
        tblTB = new JTable();
        panel1 = new JPanel();
        label1 = new JLabel();
        jdcDatum = new JDateChooser();
        pnlTags = new JScrollPane();
        label2 = new JLabel();
        txtUhrzeit = new JTextField();
        label3 = new JLabel();
        txtDauer = new JTextField();
        txtBericht = new JTextArea();
        splitButtonsCenter = new JSplitPane();
        pnlUpper = new JPanel();
        btnAddBericht = new JButton();
        btnEndReactivate = new JButton();
        btnEdit = new JToggleButton();
        btnPrint = new JButton();
        btnLogout = new JButton();
        pnlLower = new JPanel();
        btnApply = new JButton();
        hSpacer1 = new JPanel(null);
        lblMessage = new JLabel();
        hSpacer2 = new JPanel(null);
        btnCancel = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "$rgap, pref, $lcgap, default:grow, 0dlu, $rgap",
            "fill:default, $lgap, fill:default:grow, $lgap, 20dlu, $lgap, 0dlu"));

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(taskSearch);
        }
        add(scrollPane2, CC.xywh(2, 3, 1, 3));

        //---- lblBW ----
        lblBW.setFont(new Font("Dialog", Font.BOLD, 18));
        lblBW.setForeground(new Color(255, 51, 0));
        lblBW.setText("jLabel3");
        add(lblBW, CC.xywh(2, 1, 3, 1));

        //======== splitTableEditor ========
        {
            splitTableEditor.setDividerSize(0);
            splitTableEditor.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    splitTableEditorComponentResized(e);
                }
            });

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
                    new Object[][] {
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                    },
                    new String[] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        Object.class, Object.class, Object.class, Object.class
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                });
                tblTB.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        tblTBMousePressed(e);
                    }
                });
                jspTblTB.setViewportView(tblTB);
            }
            splitTableEditor.setLeftComponent(jspTblTB);

            //======== panel1 ========
            {
                panel1.setLayout(new FormLayout(
                    "$rgap, $lcgap, default, $lcgap, default:grow, $lcgap, pref, $lcgap, $rgap",
                    "0dlu, 3*($lgap, default), $lgap, default:grow, $lgap, default"));

                //---- label1 ----
                label1.setText("Datum");
                panel1.add(label1, CC.xy(3, 3));

                //---- jdcDatum ----
                jdcDatum.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                jdcDatum.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent e) {
                        jdcDatumPropertyChange(e);
                    }
                });
                panel1.add(jdcDatum, CC.xy(5, 3));
                panel1.add(pnlTags, CC.xywh(7, 3, 1, 7, CC.FILL, CC.DEFAULT));

                //---- label2 ----
                label2.setText("Uhrzeit");
                panel1.add(label2, CC.xy(3, 5));

                //---- txtUhrzeit ----
                txtUhrzeit.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                txtUhrzeit.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtUhrzeitFocusLost(e);
                    }
                });
                txtUhrzeit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtUhrzeitActionPerformed(e);
                    }
                });
                panel1.add(txtUhrzeit, CC.xy(5, 5));

                //---- label3 ----
                label3.setText("Dauer");
                panel1.add(label3, CC.xy(3, 7));

                //---- txtDauer ----
                txtDauer.setHorizontalAlignment(SwingConstants.RIGHT);
                txtDauer.setText("3");
                txtDauer.setToolTipText("Dauer in Minuten");
                txtDauer.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                txtDauer.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtDauerFocusGained(e);
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtDauerFocusLost(e);
                    }
                });
                panel1.add(txtDauer, CC.xy(5, 7));

                //---- txtBericht ----
                txtBericht.setColumns(20);
                txtBericht.setLineWrap(true);
                txtBericht.setRows(5);
                txtBericht.setWrapStyleWord(true);
                txtBericht.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
                txtBericht.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtBerichtCaretUpdate(e);
                    }
                });
                panel1.add(txtBericht, CC.xywh(3, 9, 3, 1, CC.FILL, CC.FILL));
            }
            splitTableEditor.setRightComponent(panel1);
        }
        add(splitTableEditor, CC.xy(4, 3));

        //======== splitButtonsCenter ========
        {
            splitButtonsCenter.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitButtonsCenter.setDividerSize(0);
            splitButtonsCenter.setDividerLocation(30);
            splitButtonsCenter.setBorder(null);
            splitButtonsCenter.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    splitButtonsCenterComponentResized(e);
                }
            });

            //======== pnlUpper ========
            {
                pnlUpper.setLayout(new BoxLayout(pnlUpper, BoxLayout.X_AXIS));

                //---- btnAddBericht ----
                btnAddBericht.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_add.png")));
                btnAddBericht.setToolTipText("Neuen Bericht schreiben");
                btnAddBericht.setEnabled(false);
                btnAddBericht.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnAddBerichtActionPerformed(e);
                    }
                });
                pnlUpper.add(btnAddBericht);

                //---- btnEndReactivate ----
                btnEndReactivate.setFont(new Font("Lucida Grande", Font.BOLD, 14));
                btnEndReactivate.setToolTipText("Vorgang abschlie\u00dfen");
                btnEndReactivate.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_remove.png")));
                btnEndReactivate.setEnabled(false);
                btnEndReactivate.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnEndReactivateActionPerformed(e);
                    }
                });
                pnlUpper.add(btnEndReactivate);

                //---- btnEdit ----
                btnEdit.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/graphic-design.png")));
                btnEdit.setToolTipText("Details anzeigen / \u00e4ndern");
                btnEdit.setEnabled(false);
                btnEdit.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        btnDetailsItemStateChanged(e);
                    }
                });
                pnlUpper.add(btnEdit);

                //---- btnPrint ----
                btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/printer1.png")));
                btnPrint.setEnabled(false);
                btnPrint.setToolTipText("Berichte drucken");
                btnPrint.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnPrintActionPerformed(e);
                    }
                });
                pnlUpper.add(btnPrint);

                //---- btnLogout ----
                btnLogout.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/lock.png")));
                btnLogout.setText("Abmelden");
                btnLogout.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnLogoutbtnLogoutHandler(e);
                    }
                });
                pnlUpper.add(btnLogout);
            }
            splitButtonsCenter.setTopComponent(pnlUpper);

            //======== pnlLower ========
            {
                pnlLower.setLayout(new BoxLayout(pnlLower, BoxLayout.X_AXIS));

                //---- btnApply ----
                btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnApply.setToolTipText("\u00c4nderungen sichern");
                btnApply.setFont(new Font("Lucida Grande", Font.BOLD, 14));
                btnApply.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnApplyActionPerformed(e);
                    }
                });
                pnlLower.add(btnApply);
                pnlLower.add(hSpacer1);

                //---- lblMessage ----
                lblMessage.setFont(new Font("Lucida Grande", Font.BOLD, 14));
                lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
                lblMessage.setText("My Message");
                pnlLower.add(lblMessage);
                pnlLower.add(hSpacer2);

                //---- btnCancel ----
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                btnCancel.setToolTipText("\u00c4nderungen verwerfen");
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancelActionPerformed(e);
                    }
                });
                pnlLower.add(btnCancel);
            }
            splitButtonsCenter.setBottomComponent(pnlLower);
        }
        add(splitButtonsCenter, CC.xy(4, 5));
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

    }//GEN-LAST:event_jspTblTBComponentResized

    private void btnLogoutbtnLogoutHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutbtnLogoutHandler
        OPDE.ocmain.lockOC();
    }//GEN-LAST:event_btnLogoutbtnLogoutHandler

    public void cleanup() {
        if (textErrorTL != null){
            textErrorTL.cancel();
            textErrorTL = null;
        }
        if (textmessageTL != null){
            textmessageTL.cancel();
            textmessageTL = null;
        }
        jdcVon.cleanup();
        jdcBis.cleanup();
        taskSearch.removeAll();
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
            out.write(SYSTools.htmlUmlautConversion(PflegeberichteTools.getBerichteAsHTML(SYSTools.getSelectionAsList(tm.getPflegeberichte(), sel))));

            out.close();
            SYSPrint.handleFile(parent, temp.getAbsolutePath(), Desktop.Action.OPEN);
        } catch (IOException e) {
        }
    }

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        printBericht(null);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void tblTBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTBMousePressed
        Point p = evt.getPoint();
        ListSelectionModel lsm = tblTB.getSelectionModel();

        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        int row = tblTB.rowAtPoint(p);
        if (singleRowSelected) {
            lsm.setSelectionInterval(row, row);
        }

        bericht = (Pflegeberichte) tblTB.getModel().getValueAt(lsm.getLeadSelectionIndex(), TMPflegeberichte.COL_BERICHT);
        boolean alreadyEdited = bericht.isDeleted() || bericht.isReplaced();
        boolean sameUser = bericht.getUser().equals(OPDE.getLogin().getUser());

        if (evt.isPopupTrigger()) {
            /**
             * KORRIGIEREN
             * Ein Bericht kann geändert werden (Korrektur)
             * - Wenn sie nicht im Übergabeprotokoll abgehakt wurde.
             */
            boolean bearbeitenMöglich = !alreadyEdited && singleRowSelected && bericht.getUsersAcknowledged().isEmpty();

            SYSTools.unregisterListeners(menu);
            menu = new JPopupMenu();

//            // KORRIGIEREN
//            JMenuItem itemPopupEdit = new JMenuItem("Korrigieren");
//            itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    //new DlgBericht(parent, bericht);
//                    reloadTable();
//                }
//            });
//            menu.add(itemPopupEdit);
//
//            JMenuItem itemPopupDelete = new JMenuItem("Löschen");
//            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    if (JOptionPane.showConfirmDialog(parent, "Möchten Sie diesen Eintrag wirklich löschen ?",
//                            "Bericht löschen ?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//                        PflegeberichteTools.deleteBericht(bericht);
//                        reloadTable();
//                    }
//                }
//            });
//            menu.add(itemPopupDelete);

            // #0000039
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
            if (bearbeitenMöglich && (sameUser || OPDE.isAdmin())) {
                menu.add(PBerichtTAGSTools.createMenuForTags(bericht));
            }


            if (OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !alreadyEdited && singleRowSelected) {
                menu.add(new JSeparator());
                menu.add(SYSFilesTools.getSYSFilesContextMenu(parent, bericht, fileActionListener));
            }

            menu.add(new JSeparator());
            menu.add(VorgaengeTools.getVorgangContextMenu(parent, bericht, bewohner));

//            if (!singleRowSelected){
//                int[] sel = tblTB.getSelectedRows();
//                long[] ids = (long[])Array.newInstance(long.class, 2);
//                for (int i = 0; i < sel.length; i++){
//                    ids[i] = (Long) tm.getValueAt(sel[i], TMBerichte.COL_TBID);
//                }
//                menu.add(new JSeparator());
//                // #0000003
//                menu.add(op.share.vorgang.DBHandling.getVorgangContextMenu(parent, "Tagesberichte", ids, currentBW, fileActionListener));
//
//                // #0000035
//                menu.add(SYSFiles.getOPFilesContextMenu(parent, "Tagesberichte", selectedTBID, currentBW, tblTB, true, true, SYSFiles.CODE_BERICHTE, fileActionListener));
//            }

            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }//GEN-LAST:event_tblTBMousePressed


    protected void prepareSearchArea() {
        addByTime();
        addByTags();
        addBySearchText();
        addSpecials();
    }


    private void addByTags() {
        panelTags = new JXTaskPane("nach Markierung");
        PBerichtTAGSTools.addCheckBoxPanelForTags(panelTags, new ItemListener() {

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

        panelTags.setCollapsed(true);
        taskSearch.add((JPanel) panelTags);

        panelTags.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("collapsed")) {
                    reloadTable();
                }
            }
        });

    }

    private void addBySearchText() {
        panelText = new JXTaskPane("nach Suchbegriff");
        txtSearch = new JTextField();
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        });
        panelText.add(txtSearch);
        panelText.setCollapsed(true);
        taskSearch.add((JPanel) panelText);

        panelText.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("collapsed")) {
                    reloadTable();
                }
            }
        });

    }

    private void addByTime() {

        panelTime = new JXTaskPane("nach Zeit");
        jdcVon = new JDateChooser(SYSCalendar.addField(new Date(), -2, GregorianCalendar.WEEK_OF_MONTH));
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
        panelTime.add(new JXTitledSeparator("Von"));
        panelTime.add(jdcVon);
        panelTime.add(new AbstractAction() {
            {
                putValue(Action.NAME, "vor 2 Wochen");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                jdcVon.setDate(SYSCalendar.addField(new Date(), -2, GregorianCalendar.WEEK_OF_MONTH));
            }
        });

        panelTime.add(new AbstractAction() {
            {
                putValue(Action.NAME, "vor 4 Wochen");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                jdcVon.setDate(SYSCalendar.addField(new Date(), -4, GregorianCalendar.WEEK_OF_MONTH));
            }
        });


        panelTime.add(new AbstractAction() {
            {
                putValue(Action.NAME, "von Anfang an");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                jdcVon.setDate(PflegeberichteTools.firstBericht(bewohner).getPit());
            }
        });

        jdcBis = new JDateChooser(new Date());
        jdcBis.addPropertyChangeListener(new PropertyChangeListener() {
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
        panelTime.add(new JLabel(" "));
        panelTime.add(new JXTitledSeparator("Bis"));
        panelTime.add(jdcBis);
        panelTime.add(new AbstractAction() {
            {
                putValue(Action.NAME, "heute");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                jdcVon.setDate(new Date());
            }
        });

        taskSearch.add((JPanel) panelTime);

        panelTime.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("collapsed")) {
                    reloadTable();
                }
            }
        });
    }

    private void addSpecials() {
        panelSpecials = new JXTaskPane("Sonstiges");

        cbShowEdits = new JCheckBox("Änderungen anzeigen");
        cbShowEdits.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                reloadTable();
            }
        });
        cbShowIDs = new JCheckBox("");
        cbShowIDs.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                reloadTable();
            }
        });

        txtSearch = new JTextField();
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        });
        panelSpecials.add(cbShowEdits);
        panelSpecials.add(cbShowIDs);
        panelSpecials.setCollapsed(false);
        taskSearch.add((JPanel) panelSpecials);

    }

    private void reloadTable() {
        if (initPhase) {
            return;
        }

        Query query = null;

        String tags = "";
        if (!panelTags.isCollapsed()) {
            Iterator<PBerichtTAGS> it = tagFilter.iterator();
            while (it.hasNext()) {
                tags += Long.toString(it.next().getPbtagid());
                tags += (it.hasNext() ? "," : "");
            }
        }

        String search = panelText.isCollapsed() ? "" : txtSearch.getText().trim();
        Date von = panelTime.isCollapsed() ? SYSConst.DATE_VON_ANFANG_AN : jdcVon.getDate();
        Date bis = panelTime.isCollapsed() ? SYSConst.DATE_BIS_AUF_WEITERES : jdcBis.getDate();

        if (von.after(bis)) {
            von = bis;
        }


        query = OPDE.getEM().createQuery(" "
                + " SELECT p FROM Pflegeberichte p "
                + (tags.isEmpty() ? "" : " JOIN p.tags t ")
                + " WHERE p.bewohner = :bewohner "
                + " AND p.pit >= :von AND p.pit <= :bis "
                + (search.isEmpty() ? "" : " p.text like :search ")
                + (tags.isEmpty() ? "" : " AND t.pbtagid IN (" + tags + ")")
                + (cbShowEdits.isSelected() ? "" : " AND p.editedBy is null ")
                + " ORDER BY p.pit DESC ");
        query.setParameter("bewohner", bewohner);
        query.setParameter("von", von);
        query.setParameter("bis", bis);

        if (!search.isEmpty()) {
            query.setParameter("search", "%" + search + "%");
        }
        tblTB.setModel(new TMPflegeberichte(query, cbShowIDs.isSelected()));

        btnPrint.setEnabled(tblTB.getModel().getRowCount() > 0 && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT));

        tblTB.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jspTblTB.dispatchEvent(new ComponentEvent(jspTblTB, ComponentEvent.COMPONENT_RESIZED));

        tblTB.getColumnModel().getColumn(0).setCellRenderer(new RNDBerichte());
        tblTB.getColumnModel().getColumn(1).setCellRenderer(new RNDBerichte());
        tblTB.getColumnModel().getColumn(2).setCellRenderer(new RNDBerichte());

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane scrollPane2;
    private JXTaskPaneContainer taskSearch;
    private JLabel lblBW;
    private JSplitPane splitTableEditor;
    private JScrollPane jspTblTB;
    private JTable tblTB;
    private JPanel panel1;
    private JLabel label1;
    private JDateChooser jdcDatum;
    private JScrollPane pnlTags;
    private JLabel label2;
    private JTextField txtUhrzeit;
    private JLabel label3;
    private JTextField txtDauer;
    private JTextArea txtBericht;
    private JSplitPane splitButtonsCenter;
    private JPanel pnlUpper;
    private JButton btnAddBericht;
    private JButton btnEndReactivate;
    private JToggleButton btnEdit;
    private JButton btnPrint;
    private JButton btnLogout;
    private JPanel pnlLower;
    private JButton btnApply;
    private JPanel hSpacer1;
    private JLabel lblMessage;
    private JPanel hSpacer2;
    private JButton btnCancel;
    // End of variables declaration//GEN-END:variables
}
