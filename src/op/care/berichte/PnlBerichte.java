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
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import entity.*;
import entity.files.SYSFilesTools;
import entity.system.SYSPropsTools;
import entity.vorgang.VorgaengeTools;
import op.OPDE;
import op.events.TaskPaneContentChangedEvent;
import op.events.TaskPaneContentChangedListener;
import op.tools.*;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.JXTaskPane;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import tablemodels.TMPflegeberichte;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
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
import java.beans.PropertyVetoException;
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
public class PnlBerichte extends NursingRecordsPanel {

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
    private boolean singleRowSelected;

    private ArrayList<CollapsiblePane> panelSearch;
    private TaskPaneContentChangedListener taskPaneContentChangedListener;

    /**
     * Dies ist immer der zur Zeit ausgewählte Bericht. null, wenn nichts ausgewählt ist. Wenn mehr als ein
     * Bericht ausgewählt wurde, steht hier immer der Verweis auf den ERSTEN Bericht der Auswahl.
     */
    private Pflegeberichte aktuellerBericht, oldBericht;

    private JDateChooser jdcVon, jdcBis;
    private JXSearchField txtSearch;
    private CollapsiblePane panelTime, panelText, panelTags;
    private JCheckBox cbShowEdits, cbShowIDs;

    private Timeline textmessageTL, textUpperLabelTL, lblMessage2Timeline;
    private boolean dauerChanged;

    private Bewohner bewohner;
    private JPopupMenu menu;
    private boolean initPhase;
    private JFrame parent;

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
    public PnlBerichte(JFrame parent, Bewohner bewohner, TaskPaneContentChangedListener taskPaneContentChangedListener) {
        this.initPhase = true;
        this.parent = parent;
        this.laufendeOperation = LAUFENDE_OPERATION_NICHTS;
        this.taskPaneContentChangedListener = taskPaneContentChangedListener;
        this.textmessageTL = null;
        this.aktuellerBericht = null;
        this.oldBericht = null;
        this.dauerChanged = false;
        this.singleRowSelected = false;


        initComponents();

        btnSystemInfo.setSelected(SYSPropsTools.isBoolean(internalClassID + ":btnSystemInfo"));

        this.panelSearch = panelSearch;
//        this.panelSearch.add(new JXHeader("Berichte", "", new ImageIcon(getClass().getResource("/artwork/22x22/bw/viewmag1.png"))));
//        positionToAddPanels = this.panelSearch.getComponentCount();

        standardActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        };

        tagFilter = new ArrayList<PBerichtTAGS>();
        prepareSearchArea();

        btnAddBericht.setEnabled(OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT));
        btnSystemInfo.setEnabled(true);

        splitBCPercent = SYSTools.showSide(splitButtonsCenter, SYSTools.LEFT_UPPER_SIDE);
        splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE);

        this.initPhase = false;

        change2Bewohner(bewohner);


    }

    private void btnAddBerichtActionPerformed(ActionEvent e) {
        initPhase = true;

        Date now = new Date();
        laufendeOperation = LAUFENDE_OPERATION_BERICHT_EINGABE;
        aktuellerBericht = new Pflegeberichte(bewohner);
        aktuellerBericht.setPit(now);
        aktuellerBericht.setText("");
        aktuellerBericht.setDauer(DEFAULT_DAUER);
        btnAddBericht.setEnabled(false);

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
        txtBericht.requestFocus();
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        boolean success = false;
        TimelineCallbackAdapter standardAdapter = new TimelineCallbackAdapter() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                if (newState == Timeline.TimelineState.DONE) {
//                    btnSearch.setEnabled(true);
                    btnAddBericht.setEnabled(true);
                }
            }
        };
        switch (laufendeOperation) {
            case LAUFENDE_OPERATION_BERICHT_EINGABE: {
                if (txtBericht.getText().trim().isEmpty()) {
                    if (lblMessage2Timeline == null || lblMessage2Timeline.getState() == Timeline.TimelineState.IDLE) {
                        lblMessage2Timeline = SYSTools.flashLabel(lblMessage2, "Kann keinen leeren Bericht speichern.", 6, Color.BLUE);
                    }
                } else {
                    success = EntityTools.persist(aktuellerBericht);
                    splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow, standardAdapter);
                }
                break;
            }
            case LAUFENDE_OPERATION_BERICHT_BEARBEITEN: {
                if (txtBericht.getText().trim().isEmpty()) {
                    if (lblMessage2Timeline == null || lblMessage2Timeline.getState() == Timeline.TimelineState.IDLE) {
                        lblMessage2Timeline = SYSTools.flashLabel(lblMessage2, "Kann keinen leeren Bericht speichern.", 6, Color.BLUE);
                    }
                } else {
                    success = PflegeberichteTools.changeBericht(oldBericht, aktuellerBericht);
                    oldBericht = null;
                    splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow, standardAdapter);
                }
                break;
            }
            case LAUFENDE_OPERATION_BERICHT_LOESCHEN: {
                success = PflegeberichteTools.deleteBericht(aktuellerBericht);
                break;
            }
            default: {

            }
        }

        // War alles ok, dann wird der Ausgangszustand wieder hergestellt.
        if (success) {
            lblMessage.setText(null);
            aktuellerBericht = null;
            textmessageTL.cancel();
            splitBCPercent = SYSTools.showSide(splitButtonsCenter, SYSTools.LEFT_UPPER_SIDE, speedFast);
            laufendeOperation = LAUFENDE_OPERATION_NICHTS;
            reloadTable();
        }
    }

    private void splitButtonsCenterComponentResized(ComponentEvent e) {
        SYSTools.showSide(splitButtonsCenter, splitBCPercent);
        //OPDE.debug("splitButtonsCenterComponentResized(ComponentEvent e)");
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
        dauerChanged = true;
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

    private void btnDeleteActionPerformed(ActionEvent e) {
        // Darf ich das ?
        if (!(singleRowSelected && aktuellerBericht != null && !aktuellerBericht.isDeleted() && !aktuellerBericht.isReplaced() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE))){
            return;
        }
        laufendeOperation = LAUFENDE_OPERATION_BERICHT_LOESCHEN;

        textmessageTL = SYSTools.flashLabel(lblMessage, "Bericht löschen ?");
        splitBCPercent = SYSTools.showSide(splitButtonsCenter, SYSTools.RIGHT_LOWER_SIDE, speedFast);
    }

    private void btnEditActionPerformed(ActionEvent e) {
        // Darf ich das ?
        if (!(singleRowSelected && aktuellerBericht != null && !aktuellerBericht.isDeleted() && !aktuellerBericht.isReplaced() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE))) {
            return;
        }

        initPhase = true;
        Date now = new Date();
        laufendeOperation = LAUFENDE_OPERATION_BERICHT_BEARBEITEN;
        oldBericht = aktuellerBericht;
        aktuellerBericht = PflegeberichteTools.copyBericht(aktuellerBericht);

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
        jdcDatum.setDate(aktuellerBericht.getPit());
        jdcDatum.setMaxSelectableDate(now);
        txtUhrzeit.setText(df.format(aktuellerBericht.getPit()));
        txtBericht.setText(aktuellerBericht.getText());

        initPhase = false;
        textmessageTL = SYSTools.flashLabel(lblMessage, "Geänderten Bericht speichern ?");
        splitBCPercent = SYSTools.showSide(splitButtonsCenter, SYSTools.RIGHT_LOWER_SIDE, speedFast);
        splitTEPercent = SYSTools.showSide(splitTableEditor, 0.4d, speedFast, null);
        txtBericht.requestFocus();
    }

//    private void btnSearchActionPerformed(ActionEvent e) {
//        toggleSearchArea(speedSlow);
//    }

//    private void toggleSearchArea(int speed) {
//        btnSearch.setEnabled(false);
//        if (searchAreaVisible) {
//            SYSTools.showSide(splitSearchEdit, SYSTools.RIGHT_LOWER_SIDE, speed, new TimelineCallbackAdapter() {
//                @Override
//                public void onTimelineStateChanged(Timeline.TimelineState timelineState, Timeline.TimelineState timelineState1, float v, float v1) {
//                    if (timelineState == Timeline.TimelineState.DONE) {
//                        btnSearch.setEnabled(true);
//                        btnSearch.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/2rightarrow.png")));
//                    }
//                }
//            });
//        } else {
//            SYSTools.showSide(splitSearchEdit, panelSearch.getPreferredSize().width, speedSlow, new TimelineCallbackAdapter() {
//                @Override
//                public void onTimelineStateChanged(Timeline.TimelineState timelineState, Timeline.TimelineState timelineState1, float v, float v1) {
//                    if (timelineState == Timeline.TimelineState.DONE) {
//                        btnSearch.setEnabled(true);
//                        btnSearch.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/2leftarrow.png")));
//                    }
//                }
//            });
//        }
//        searchAreaVisible = !searchAreaVisible;
//        SYSPropsTools.storeBoolean(internalClassID + "::searchAreaVisible", searchAreaVisible);
//    }

    private void tblTBMouseReleased(MouseEvent e) {
        ListSelectionModel lsm = tblTB.getSelectionModel();
        singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();
//        btnEdit.setEnabled(singleRowSelected && !aktuellerBericht.isDeleted() && !aktuellerBericht.isReplaced() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));

    }

    private void thisComponentResized(ComponentEvent e) {
        splitButtonsCenterComponentResized(e);
        splitTableEditorComponentResized(e);
    }

    private void btnSystemInfoItemStateChanged(ItemEvent e) {
        if (initPhase) {
            return;
        }
        SYSPropsTools.storeBoolean(internalClassID + ":btnSystemInfo", btnSystemInfo.isSelected());
        reloadTable();
    }

    private void btnCancelActionPerformed(ActionEvent e) {

        TimelineCallbackAdapter standardCancelAdapter = new TimelineCallbackAdapter() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                if (newState == Timeline.TimelineState.DONE) {
                    btnAddBericht.setEnabled(OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT));
                }
            }
        };


        switch (laufendeOperation) {
            case LAUFENDE_OPERATION_BERICHT_EINGABE: {
                splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow, standardCancelAdapter);
                break;
            }
            case LAUFENDE_OPERATION_BERICHT_BEARBEITEN: {
                splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow, standardCancelAdapter);
                break;
            }
            default: {

            }
        }
        aktuellerBericht = null;
        oldBericht = null;
        lblMessage.setText(null);
        textmessageTL.cancel();
        splitBCPercent = SYSTools.showSide(splitButtonsCenter, SYSTools.LEFT_UPPER_SIDE, speedFast);
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
        pnlContent = new JPanel();
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
        scrollPane1 = new JScrollPane();
        txtBericht = new JTextArea();
        splitButtonsCenter = new JSplitPane();
        pnlUpper = new JPanel();
        btnAddBericht = new JButton();
        btnDelete = new JButton();
        btnEdit = new JButton();
        btnPrint = new JButton();
        btnSystemInfo = new JToggleButton();
        pnlLower = new JPanel();
        btnApply = new JButton();
        btnCancel = new JButton();
        lblMessage = new JLabel();
        hSpacer1 = new JPanel(null);
        lblMessage2 = new JLabel();
        hSpacer2 = new JPanel(null);

        //======== this ========
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                thisComponentResized(e);
            }
        });
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        //======== pnlContent ========
        {
            pnlContent.setLayout(new FormLayout(
                "pref:grow",
                "fill:default:grow, 20dlu"));

            //======== splitTableEditor ========
            {
                splitTableEditor.setDividerSize(0);
                splitTableEditor.setDividerLocation(600);
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
                    ));
                    tblTB.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            tblTBMousePressed(e);
                        }
                        @Override
                        public void mouseReleased(MouseEvent e) {
                            tblTBMouseReleased(e);
                        }
                    });
                    jspTblTB.setViewportView(tblTB);
                }
                splitTableEditor.setLeftComponent(jspTblTB);

                //======== panel1 ========
                {
                    panel1.setLayout(new FormLayout(
                        "$rgap, $lcgap, default, $lcgap, default:grow, $lcgap, center:pref, $lcgap, $rgap",
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
                    panel1.add(pnlTags, CC.xywh(7, 3, 1, 7));

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

                    //======== scrollPane1 ========
                    {

                        //---- txtBericht ----
                        txtBericht.addCaretListener(new CaretListener() {
                            @Override
                            public void caretUpdate(CaretEvent e) {
                                txtBerichtCaretUpdate(e);
                            }
                        });
                        scrollPane1.setViewportView(txtBericht);
                    }
                    panel1.add(scrollPane1, CC.xywh(3, 9, 3, 1, CC.FILL, CC.FILL));
                }
                splitTableEditor.setRightComponent(panel1);
            }
            pnlContent.add(splitTableEditor, CC.xy(1, 1));

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
                    btnAddBericht.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                    btnAddBericht.setToolTipText("Neuen Bericht schreiben");
                    btnAddBericht.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnAddBerichtActionPerformed(e);
                        }
                    });
                    pnlUpper.add(btnAddBericht);

                    //---- btnDelete ----
                    btnDelete.setFont(new Font("Lucida Grande", Font.BOLD, 14));
                    btnDelete.setToolTipText("Bericht l\u00f6schen");
                    btnDelete.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/trashcan_empty.png")));
                    btnDelete.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnDeleteActionPerformed(e);
                        }
                    });
                    pnlUpper.add(btnDelete);

                    //---- btnEdit ----
                    btnEdit.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/edit.png")));
                    btnEdit.setToolTipText("Details anzeigen / \u00e4ndern");
                    btnEdit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnEditActionPerformed(e);
                        }
                    });
                    pnlUpper.add(btnEdit);

                    //---- btnPrint ----
                    btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")));
                    btnPrint.setToolTipText("Berichte drucken");
                    btnPrint.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnPrintActionPerformed(e);
                        }
                    });
                    pnlUpper.add(btnPrint);

                    //---- btnSystemInfo ----
                    btnSystemInfo.setToolTipText("System Berichte anzeigen / verbergen");
                    btnSystemInfo.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/idea.png")));
                    btnSystemInfo.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            btnSystemInfoItemStateChanged(e);
                        }
                    });
                    pnlUpper.add(btnSystemInfo);
                }
                splitButtonsCenter.setTopComponent(pnlUpper);

                //======== pnlLower ========
                {
                    pnlLower.setLayout(new BoxLayout(pnlLower, BoxLayout.X_AXIS));

                    //---- btnApply ----
                    btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/apply.png")));
                    btnApply.setToolTipText("\u00c4nderungen sichern");
                    btnApply.setFont(new Font("Lucida Grande", Font.BOLD, 14));
                    btnApply.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnApplyActionPerformed(e);
                        }
                    });
                    pnlLower.add(btnApply);

                    //---- btnCancel ----
                    btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")));
                    btnCancel.setToolTipText("\u00c4nderungen verwerfen");
                    btnCancel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnCancelActionPerformed(e);
                        }
                    });
                    pnlLower.add(btnCancel);

                    //---- lblMessage ----
                    lblMessage.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
                    lblMessage.setText(" ");
                    pnlLower.add(lblMessage);
                    pnlLower.add(hSpacer1);

                    //---- lblMessage2 ----
                    lblMessage2.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    pnlLower.add(lblMessage2);
                    pnlLower.add(hSpacer2);
                }
                splitButtonsCenter.setBottomComponent(pnlLower);
            }
            pnlContent.add(splitButtonsCenter, CC.xy(1, 2));
        }
        add(pnlContent);
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

    private void btnLogoutbtnLogoutHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutbtnLogoutHandler

    }//GEN-LAST:event_btnLogoutbtnLogoutHandler

    @Override
    public void cleanup() {
        if (textUpperLabelTL != null) {
            textUpperLabelTL.cancel();
            textUpperLabelTL = null;
        }
        if (textmessageTL != null) {
            textmessageTL.cancel();
            textmessageTL = null;
        }
        if (lblMessage2Timeline != null) {
            lblMessage2Timeline.cancel();
            lblMessage2Timeline = null;
        }
//        tblTB.getSelectionModel().removeListSelectionListener(lsl);
//        lsl = null;
        jdcVon.cleanup();
        jdcBis.cleanup();
//        SYSTools.removeSearchPanels(panelSearch, positionToAddPanels);
    }

    @Override
    public void change2Bewohner(Bewohner bewohner) {
        if (laufendeOperation != LAUFENDE_OPERATION_NICHTS) {
            btnCancel.doClick();
        }

        this.bewohner = bewohner;
//        SYSTools.removeSearchPanels(panelSearch, positionToAddPanels);
        prepareSearchArea();
//        BewohnerTools.setBWLabel(lblBW, bewohner);
//        panelSearch.validate();
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

        singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        int row = tblTB.rowAtPoint(p);
        if (singleRowSelected) {
            lsm.setSelectionInterval(row, row);
        }

        aktuellerBericht = (Pflegeberichte) tblTB.getModel().getValueAt(lsm.getLeadSelectionIndex(), TMPflegeberichte.COL_BERICHT);
        boolean alreadyEdited = aktuellerBericht.isDeleted() || aktuellerBericht.isReplaced();
        boolean sameUser = aktuellerBericht.getUser().equals(OPDE.getLogin().getUser());

        // Wenn die Zeile gewechselt wird, dann wird jede aktuelle Eingabe einfach beendet.
        if (laufendeOperation != LAUFENDE_OPERATION_NICHTS) {
            btnCancel.doClick();
        }

//        btnEdit.setEnabled(singleRowSelected && !aktuellerBericht.isDeleted() && !aktuellerBericht.isReplaced() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
//        btnDelete.setEnabled(singleRowSelected && !aktuellerBericht.isDeleted() && !aktuellerBericht.isReplaced() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE));

        if (evt.isPopupTrigger()) {
            /**
             * KORRIGIEREN
             * Ein Bericht kann geändert werden (Korrektur)
             * - Wenn sie nicht im Übergabeprotokoll abgehakt wurde.
             */
            boolean bearbeitenMöglich = !alreadyEdited && singleRowSelected && aktuellerBericht.getUsersAcknowledged().isEmpty();

            SYSTools.unregisterListeners(menu);
            menu = new JPopupMenu();

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
                menu.add(PBerichtTAGSTools.createMenuForTags(aktuellerBericht));
            }


            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !alreadyEdited && singleRowSelected) {
                menu.add(new JSeparator());
                menu.add(SYSFilesTools.getSYSFilesContextMenu(parent, aktuellerBericht, standardActionListener));
            }

            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !alreadyEdited && singleRowSelected) {
                menu.add(new JSeparator());
                menu.add(VorgaengeTools.getVorgangContextMenu(parent, aktuellerBericht, bewohner, standardActionListener));
            }

            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }//GEN-LAST:event_tblTBMousePressed


    private void prepareSearchArea() {
        panelSearch = new ArrayList<CollapsiblePane>();
        addByTime();
        addByTags();
        addBySearchText();
        taskPaneContentChangedListener.contentChanged(new TaskPaneContentChangedEvent(this, panelSearch, "Pflegeberichte"));
    }


    private void addByTags() {
        panelTags = new CollapsiblePane("nach Markierung");
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

        try {
            panelTags.setCollapsed(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        panelSearch.add(panelTags);

        panelTags.addCollapsiblePaneListener(new CollapsiblePaneAdapter(){
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                reloadTable();
            }

            @Override
            public void paneCollapsed(CollapsiblePaneEvent collapsiblePaneEvent) {
                reloadTable();
            }
        });

    }

    private void addBySearchText() {
        panelText = new CollapsiblePane("nach Suchbegriff");
        txtSearch = new JXSearchField("Suchbegriff");
        txtSearch.setInstantSearchDelay(500);
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        });

        panelText.add(txtSearch);
        try {
            panelText.setCollapsed(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        panelSearch.add(panelText);

        panelText.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                reloadTable();
            }

            @Override
            public void paneCollapsed(CollapsiblePaneEvent collapsiblePaneEvent) {
                reloadTable();
            }
        });
    }

    private void addByTime() {

        panelTime = new CollapsiblePane("nach Zeit");
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
        panelTime.add(new JLabel("Von"));
        panelTime.add(jdcVon);
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
        JideButton buttonToday = GUITools.createHyperlinkButton("heute", null, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcBis.setDate(new Date());
            }
        });
        panelTime.add(button2Weeks);
        panelTime.add(button4Weeks);
        panelTime.add(buttonBeginning);

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
        panelTime.add(new JLabel("Bis"));
//        panelTime.add(new JXTitledSeparator("Bis"));
        panelTime.add(jdcBis);
        panelTime.add(buttonToday);

        panelSearch.add(panelTime);

        panelTime.addCollapsiblePaneListener(new CollapsiblePaneAdapter(){
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                reloadTable();
            }

            @Override
            public void paneCollapsed(CollapsiblePaneEvent collapsiblePaneEvent) {
                reloadTable();
            }
        });
    }

//    private void addSpecials() {
//        panelSpecials = new JXTaskPane("Sonstiges");
//
//        cbShowEdits = new JCheckBox("Änderungen anzeigen");
//        cbShowEdits.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                SYSPropsTools.storeState(internalClassID + ":cbShowEdits", cbShowEdits);
//                reloadTable();
//            }
//        });
//        cbShowIDs = new JCheckBox("Bericht Nummern anzeigen");
//        cbShowIDs.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                SYSPropsTools.storeState(internalClassID + ":cbShowIDs", cbShowIDs);
//                reloadTable();
//            }
//        });
//
//        panelSpecials.add(cbShowEdits);
//        panelSpecials.add(cbShowIDs);
//        panelSpecials.setCollapsed(false);
//        panelSearch.add((JPanel) panelSpecials);
//
//        SYSPropsTools.restoreState(internalClassID + ":cbShowEdits", cbShowEdits);
//        SYSPropsTools.restoreState(internalClassID + ":cbShowIDs", cbShowIDs);
//
//    }

    private void reloadTable() {
        if (initPhase) {
            return;
        }

        OPDE.debug("reloadTable()");
//        if (textUpperLabelTL != null){
//            textUpperLabelTL.cancel();
//        }
//        textUpperLabelTL = SYSTools.flashLabel(lblBW, "Datenbankzugriff");

        if (laufendeOperation != LAUFENDE_OPERATION_NICHTS) {
            btnCancel.doClick();
        }


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

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" "
                + " SELECT p FROM Pflegeberichte p "
                + (tags.isEmpty() ? "" : " JOIN p.tags t ")
                + " WHERE p.bewohner = :bewohner "
                + " AND p.pit >= :von AND p.pit <= :bis "
                + (search.isEmpty() ? "" : " AND p.text like :search ")
                + (tags.isEmpty() ? "" : " AND t.pbtagid IN (" + tags + " ) ")
                + (btnSystemInfo.isSelected() ? "" : " AND p.editedBy is null ")
                + " ORDER BY p.pit DESC ");
        query.setParameter("bewohner", bewohner);
        query.setParameter("von", new Date(SYSCalendar.startOfDay(von)));
        query.setParameter("bis", new Date(SYSCalendar.endOfDay(bis)));

        if (!search.isEmpty()) {
            query.setParameter("search", "%" + search + "%");
        }

        ArrayList<Pflegeberichte> listBerichte = new ArrayList<Pflegeberichte>(query.getResultList());
        em.close();

        tblTB.setModel(new TMPflegeberichte(listBerichte, btnSystemInfo.isSelected()));


        //OPDE.debug("tl: "+tl.getState());
//        if (tl.getState() == Timeline.TimelineState.READY){
//            OPDE.debug("ready");
//        } else {
//            tl.cancel();
//        }
//        OPDE.debug("cancelling");
        //tl.cancel();

        btnPrint.setEnabled(tblTB.getModel().getRowCount() > 0 && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT));

        tblTB.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jspTblTB.dispatchEvent(new ComponentEvent(jspTblTB, ComponentEvent.COMPONENT_RESIZED));

        tblTB.getColumnModel().getColumn(0).setCellRenderer(new RNDHTML());
        tblTB.getColumnModel().getColumn(1).setCellRenderer(new RNDHTML());
        tblTB.getColumnModel().getColumn(2).setCellRenderer(new RNDHTML());

        dispatchEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel pnlContent;
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
    private JScrollPane scrollPane1;
    private JTextArea txtBericht;
    private JSplitPane splitButtonsCenter;
    private JPanel pnlUpper;
    private JButton btnAddBericht;
    private JButton btnDelete;
    private JButton btnEdit;
    private JButton btnPrint;
    private JToggleButton btnSystemInfo;
    private JPanel pnlLower;
    private JButton btnApply;
    private JButton btnCancel;
    private JLabel lblMessage;
    private JPanel hSpacer1;
    private JLabel lblMessage2;
    private JPanel hSpacer2;
    // End of variables declaration//GEN-END:variables
}
